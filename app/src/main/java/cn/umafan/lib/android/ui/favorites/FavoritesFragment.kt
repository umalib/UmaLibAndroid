package cn.umafan.lib.android.ui.favorites

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import cn.umafan.lib.android.R
import cn.umafan.lib.android.databinding.FragmentFavoritesBinding
import cn.umafan.lib.android.model.DataBaseHandler
import cn.umafan.lib.android.model.MyBaseActivity
import cn.umafan.lib.android.model.db.ArtInfo
import cn.umafan.lib.android.model.db.ArtInfoDao
import cn.umafan.lib.android.model.db.DaoSession
import cn.umafan.lib.android.ui.main.DatabaseCopyThread
import cn.umafan.lib.android.ui.main.MainActivity
import cn.umafan.lib.android.util.FavoriteArticleUtil
import cn.umafan.lib.android.util.PageSizeUtil
import cn.umafan.lib.android.util.SettingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.liangguo.androidkit.app.ToastUtil
import kotlinx.coroutines.launch

@SuppressLint("InflateParams")
class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null

    private val binding get() = _binding!!

    private lateinit var _favoritesViewModel: FavoritesViewModel

    private val favoritesViewModel get() = _favoritesViewModel

    private var daoSession: DaoSession? = null

    private var pageData = mutableListOf<Int>()

    private var isShowing = false

    private val mPageSelectorDialog by lazy {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_page_selector, null)
        val pageSizeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            arrayOf(10, 20, 30, 50)
        )
        view.findViewById<RecyclerView>(R.id.page_recycler_view)?.adapter =
            favoritesViewModel.pageSelectorAdapter
        val inputText = view.findViewById<TextInputEditText>(R.id.page_jumper_input_text)
        inputText.doOnTextChanged { text, start, _, count ->
            if (text.toString().isNotBlank()) {
                val page = text.toString().toInt()
                if (page > favoritesViewModel.pageLen.value!!) {
                    inputText.setText(text.toString().replaceRange(start, start + count, ""))
                }
            }
        }
        inputText.setOnEditorActionListener { textView, i, _ ->
            if (i == EditorInfo.IME_ACTION_NEXT) {
                val page = textView.text.toString().toInt()
                favoritesViewModel.currentPage.postValue(page)
                with(favoritesViewModel) {
                    checkedButton.value?.isChecked = false
                    checkedButton.value = null
                    //清除数组再重新记录状态
                    favoritesViewModel.checkedList.clear()
                    for (n in 0 until favoritesViewModel.pageLen.value!!) {
                        if (n + 1 != favoritesViewModel.currentPage.value) favoritesViewModel.checkedList.add(
                            false
                        )
                        else favoritesViewModel.checkedList.add(true)
                    }
                    checkedList[page - 1] = true
                    selectedPage.value = page
                }
                inputText.setText("")
                isShowing = true
            }
            false
        }
        val pageSizeSelector =
            view.findViewById<AutoCompleteTextView>(R.id.page_size_selector_input)
        pageSizeSelector.setAdapter(pageSizeAdapter)
        pageSizeSelector.hint =
            "${getString(R.string.page_size_selector_label)}-当前：${PageSizeUtil.getSize()}"
        pageSizeSelector.setOnItemClickListener { adapterView, _, i, _ ->
            PageSizeUtil.setSize(adapterView.adapter.getItem(i).toString().toInt())
            favoritesViewModel.currentPage.postValue(1)
            isShowing = true
        }
        MaterialAlertDialogBuilder(
            requireActivity(),
            com.google.android.material.R.style.MaterialAlertDialog_Material3
        )
            .setTitle("${getString(R.string.select)}${getString(R.string.page_num)}")
            .setPositiveButton(
                R.string.confirm,
            ) { _, _ ->
                favoritesViewModel.currentPage.postValue(favoritesViewModel.selectedPage.value)
            }
            .setNegativeButton(R.string.cancel, null)
            .setView(view)
            .create()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _favoritesViewModel =
            ViewModelProvider(this)[FavoritesViewModel::class.java]

        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        favoritesViewModel.currentPage.observe(viewLifecycleOwner) {
            favoritesViewModel.checkedList.clear()
            for (i in 0 until favoritesViewModel.pageLen.value!!) {
                if (i + 1 != favoritesViewModel.currentPage.value) favoritesViewModel.checkedList.add(
                    false
                )
                else favoritesViewModel.checkedList.add(true)
            }
            if (isShowing) {
                mPageSelectorDialog.hide()
                isShowing = false
            }
            loadArticles(
                it,
                PageSizeUtil.getSize()
            )
            binding.lastPageBtn.isEnabled = it > 1
            binding.nextPageBtn.isEnabled = it < favoritesViewModel.pageLen.value!!
            binding.pageNumBtn.text = "$it/${favoritesViewModel.pageLen.value} 页"
        }

        with(favoritesViewModel) {
            viewModelScope.launch {
                pageLen.observe(viewLifecycleOwner) {
                    viewModelScope.launch {
                        favoritesViewModel.checkedList.clear()
                        for (i in 0 until it + 1) {
                            if (i + 1 != favoritesViewModel.currentPage.value) favoritesViewModel.checkedList.add(
                                false
                            )
                            else favoritesViewModel.checkedList.add(true)
                        }
                        this@FavoritesFragment.pageData = mutableListOf()
                        for (i in 1 until it + 1) {
                            this@FavoritesFragment.pageData.add(i)
                        }
                        pageData.emit(this@FavoritesFragment.pageData)
                        println("fucka" + pageData.value)
                    }
                }
            }
        }

        initViews()

        return root
    }

    private fun initViews() {
        with(binding) {
            nextPageBtn.setOnClickListener {
                favoritesViewModel.currentPage.postValue(favoritesViewModel.currentPage.value!! + 1)
            }
            lastPageBtn.setOnClickListener {
                favoritesViewModel.currentPage.postValue(favoritesViewModel.currentPage.value!! - 1)
            }
            recyclerView.adapter = favoritesViewModel.articleDataAdapter
            pageNumBtn.setOnClickListener { mPageSelectorDialog.show() }

            layout.apply {
                val uri = SettingUtil.getImageBackground(SettingUtil.INDEX_BG)
                if (null != uri) background = Drawable.createFromPath(SettingUtil.getRealPathFromUriAboveApi19(requireContext(), uri))
            }
        }
    }

    private fun loadArticles(page: Int?, pageSize: Int) {
        val idList = FavoriteArticleUtil.getFavorites()
        if (idList.isEmpty()) {
            ToastUtil.info(getString(R.string.no_data))
        }
        favoritesViewModel.pageLen.value = idList.size / pageSize + 1
        val handler = DataBaseHandler(activity as MyBaseActivity) {
            daoSession = it.obj as DaoSession
            if (null != daoSession) {
                val artInfoDao: ArtInfoDao = daoSession!!.artInfoDao
                val offset = if (page != null) {
                    (page - 1) * pageSize
                } else {
                    0
                }
                val list = if (offset + pageSize > idList.size) idList.subList(offset, idList.size)
                else idList.subList(offset, offset + pageSize)
                val data = mutableListOf<ArtInfo>()
                data.addAll(
                    artInfoDao.queryBuilder().where(ArtInfoDao.Properties.Id.`in`(list)).build()
                        .list().sortedBy { artInfo -> list.indexOf(artInfo.id.toInt()) }
                )
                favoritesViewModel.loadArticles(data)
            }
        }
        (activity as MainActivity).shapeLoadingDialog?.show()
        DatabaseCopyThread.addHandler(handler)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}