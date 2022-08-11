package cn.umafan.lib.android.ui.home

import android.annotation.SuppressLint
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
import cn.umafan.lib.android.model.db.ArtInfo
import cn.umafan.lib.android.model.db.ArtInfoDao
import cn.umafan.lib.android.model.db.DaoSession
import cn.umafan.lib.android.model.db.TaggedDao
import cn.umafan.lib.android.databinding.FragmentHomeBinding
import cn.umafan.lib.android.model.DataBaseHandler
import cn.umafan.lib.android.model.SearchBean
import cn.umafan.lib.android.ui.main.DatabaseCopyThread
import cn.umafan.lib.android.ui.main.MainActivity
import cn.umafan.lib.android.util.PageSizeUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.liangguo.androidkit.app.ToastUtil
import kotlinx.coroutines.launch
import org.greenrobot.greendao.query.QueryBuilder

@SuppressLint("InflateParams")
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private var _homeViewModel: HomeViewModel? = null

    private val homeViewModel get() = _homeViewModel!!

    private var daoSession: DaoSession? = null

    private var isShowing = false

    private var pageData = mutableListOf<Int>()

    private val mPageSelectorDialog by lazy {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_page_selector, null)
        val pageSizeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            arrayOf(10, 20, 30, 50)
        )
        view.findViewById<RecyclerView>(R.id.page_recycler_view)?.adapter =
            homeViewModel.pageSelectorAdapter
        val inputText = view.findViewById<TextInputEditText>(R.id.page_jumper_input_text)
        inputText.doOnTextChanged { text, start, _, count ->
            if (text.toString().isNotBlank()) {
                val page = text.toString().toInt()
                if (page > homeViewModel.pageLen.value!!) {
                    inputText.setText(text.toString().replaceRange(start, start + count, ""))
                }
            }
        }
        inputText.setOnEditorActionListener { textView, i, _ ->
            if (i == EditorInfo.IME_ACTION_NEXT) {
                val page = textView.text.toString().toInt()
                homeViewModel.currentPage.postValue(page)
                with(homeViewModel) {
                    checkedButton.value?.isChecked = false
                    checkedButton.value = null
                    //清除数组再重新记录状态
                    homeViewModel.checkedList.clear()
                    for (n in 0 until homeViewModel.pageLen.value!!) {
                        if (n + 1 != homeViewModel.currentPage.value) homeViewModel.checkedList.add(
                            false
                        )
                        else homeViewModel.checkedList.add(true)
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
        pageSizeSelector.hint = "${getString(R.string.page_size_selector_label)}-当前：${PageSizeUtil.getSize()}"
        pageSizeSelector.setOnItemClickListener { adapterView, _, i, _ ->
            PageSizeUtil.setSize(adapterView.adapter.getItem(i).toString().toInt())
            homeViewModel.currentPage.postValue(1)
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
                homeViewModel.currentPage.postValue(homeViewModel.selectedPage.value)
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

        _homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initView()

        with(homeViewModel) {
            currentPage.observe(viewLifecycleOwner) {
                homeViewModel.checkedList.clear()
                for (i in 0 until homeViewModel.pageLen.value!!) {
                    if (i + 1 != homeViewModel.currentPage.value) homeViewModel.checkedList.add(false)
                    else homeViewModel.checkedList.add(true)
                }
                if (isShowing) {
                    mPageSelectorDialog.hide()
                    isShowing = false
                }
                loadArticles(
                    it,
                    arguments?.getSerializable("searchParams") as SearchBean?,
                    PageSizeUtil.getSize()
                )
                binding.lastPageBtn.isEnabled = it > 1
                binding.nextPageBtn.isEnabled = it < homeViewModel.pageLen.value!!
                binding.pageNumBtn.text = "$it/${homeViewModel.pageLen.value} 页"
            }
            pageLen.observe(viewLifecycleOwner) {
                viewModelScope.launch {
                    homeViewModel.checkedList.clear()
                    for (i in 0 until it + 1) {
                        if (i + 1 != homeViewModel.currentPage.value) homeViewModel.checkedList.add(
                            false
                        )
                        else homeViewModel.checkedList.add(true)
                    }
                    this@HomeFragment.pageData = mutableListOf()
                    for (i in 1 until it + 1) {
                        this@HomeFragment.pageData.add(i)
                    }
                    pageData.emit(this@HomeFragment.pageData)
                }
            }
        }

        binding.randomBtn.setOnClickListener {
            loadArticles(1,
            SearchBean(isRandom = true),
                PageSizeUtil.getSize()
            )
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {
        with(binding) {
            nextPageBtn.setOnClickListener {
                homeViewModel.currentPage.postValue(homeViewModel.currentPage.value!! + 1)
            }
            lastPageBtn.setOnClickListener {
                homeViewModel.currentPage.postValue(homeViewModel.currentPage.value!! - 1)
            }
            recyclerView.adapter = homeViewModel.articleDataAdapter
            pageNumBtn.setOnClickListener { mPageSelectorDialog.show() }
        }
    }

    /**
     * 从数据库中查询内容
     * @param page 查询页数
     * @param params 查询主要参数
     */
    @SuppressLint("SetTextI18n")
    private fun loadArticles(page: Int?, params: SearchBean?, pageSize: Int) {
        val handler = DataBaseHandler(activity as MainActivity) {
            daoSession = it.obj as DaoSession
            var count = 5L
            if (null != daoSession) {
                val artInfoDao: ArtInfoDao = daoSession!!.artInfoDao
                val taggedDao = daoSession!!.taggedDao
                val offset = if (page != null) {
                    (page - 1) * pageSize
                } else {
                    0
                }
                println("search-params: $params")
                val query: QueryBuilder<ArtInfo> = artInfoDao.queryBuilder()
                if (null != params) {
                    if (!params.isRandom) {
                        if (params.creator!!.isNotBlank()) {
                            query.where(
                                query.or(
                                    ArtInfoDao.Properties.Author.eq(params.creator),
                                    ArtInfoDao.Properties.Translator.eq(params.creator)
                                )
                            )
                        }
                        if (params.keyword!!.isNotBlank()) {
                            query.where(
                                query.or(
                                    ArtInfoDao.Properties.Name.like("%${params.keyword}%"),
                                    ArtInfoDao.Properties.Note.like("%${params.keyword}%")
                                )
                            )
                        }
                        if (params.tags.isNotEmpty()) {
                            val taggedList = taggedDao.queryRaw(
                                "where tagId in ${
                                    params.tags.map { tag -> tag.id }.joinToString(",", "(", ")")
                                } group by artId having count(id) = ${params.tags.size}"
                            )
                            query.where(
                                ArtInfoDao.Properties.Id.`in`(
                                    taggedList.map { tagged -> tagged.artId })
                            )
                        }
                        if (params.exceptedTags.isNotEmpty()) {
                            val taggedList = taggedDao.queryBuilder()
                                .where(TaggedDao.Properties.TagId.`in`(params.exceptedTags.map { tag -> tag.id }))
                                .build().list()
                            query.where(
                                ArtInfoDao.Properties.Id.notIn(taggedList.map { tagged -> tagged.artId })
                            )
                        }
                    } else {
                        val q = query.limit(pageSize).orderRaw("RANDOM()").orderDesc(ArtInfoDao.Properties.UploadTime).build().listLazy()
                        homeViewModel.loadArticles(q)
                        homeViewModel.pageLen.value = 1
                        with(homeViewModel.currentPage.value) {
                            binding.lastPageBtn.isEnabled = this!! > 1
                            binding.nextPageBtn.isEnabled = this < homeViewModel.pageLen.value!!
                            binding.pageNumBtn.text = "$this/${homeViewModel.pageLen.value} 页"
                        }
                        return@DataBaseHandler
                    }
                }

                query.offset(offset).limit(pageSize).orderDesc(ArtInfoDao.Properties.UploadTime)

                count = query.count()
                if (count == 0L) {
                    ToastUtil.info(getString(R.string.no_data))
                }
                if (0L == count || 0L != count % pageSize) {
                    count = count / pageSize + 1
                } else {
                    count /= pageSize
                }
                val list = query.build().listLazy()
                homeViewModel.loadArticles(list)
                list.close()
            } else {
                homeViewModel.loadArticles(null)
            }
            homeViewModel.pageLen.value = count.toInt()
            with(homeViewModel.currentPage.value) {
                binding.lastPageBtn.isEnabled = this!! > 1
                binding.nextPageBtn.isEnabled = this < homeViewModel.pageLen.value!!
                binding.pageNumBtn.text = "$this/${homeViewModel.pageLen.value} 页"
            }
        }
        (activity as MainActivity).shapeLoadingDialog?.show()
        DatabaseCopyThread.addHandler(handler)
    }
}