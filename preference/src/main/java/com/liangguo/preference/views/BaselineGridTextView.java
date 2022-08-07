package com.liangguo.preference.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.FontRes;

import com.google.android.material.textview.MaterialTextView;
import com.liangguo.preference.R;


public class BaselineGridTextView extends MaterialTextView {

  private final float FOUR_DIP;

  private int extraBottomPadding = 0;

  private int extraTopPadding = 0;

  private @FontRes int fontResId = 0;

  private float lineHeightHint = 0f;

  private float lineHeightMultiplierHint = 1f;

  private boolean maxLinesByHeight;

  public BaselineGridTextView(Context context) {
    this(context, null);
  }

  public BaselineGridTextView(Context context, AttributeSet attrs) {
    this(context, attrs, android.R.attr.textViewStyle);
  }

  public BaselineGridTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    final TypedArray a =
        context.obtainStyledAttributes(attrs, R.styleable.BaselineGridTextView, defStyleAttr, 0);

    //首先检查 TextAppearance 的行高和字体属性
    if (a.hasValue(R.styleable.BaselineGridTextView_android_textAppearance)) {
      int textAppearanceId =
          a.getResourceId(
              R.styleable.BaselineGridTextView_android_textAppearance,
              android.R.style.TextAppearance);
      TypedArray ta =
          context.obtainStyledAttributes(textAppearanceId, R.styleable.BaselineGridTextView);
      parseTextAttrs(ta);
      ta.recycle();
    }

    parseTextAttrs(a);
    maxLinesByHeight = a.getBoolean(R.styleable.BaselineGridTextView_maxLinesByHeight, false);
    a.recycle();

    FOUR_DIP =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
    computeLineHeight();
  }

  @Override
  public int getCompoundPaddingBottom() {
    //额外的填充以使高度成为 4dp 的倍数
    return super.getCompoundPaddingBottom() + extraBottomPadding;
  }

  @Override
  public int getCompoundPaddingTop() {
    //额外的填充以将第一行的基线放置在网格上
    return super.getCompoundPaddingTop() + extraTopPadding;
  }

  public @FontRes int getFontResId() {
    return fontResId;
  }

  public float getLineHeightHint() {
    return lineHeightHint;
  }

  public void setLineHeightHint(float lineHeightHint) {
    this.lineHeightHint = lineHeightHint;
    computeLineHeight();
  }

  public float getLineHeightMultiplierHint() {
    return lineHeightMultiplierHint;
  }

  public void setLineHeightMultiplierHint(float lineHeightMultiplierHint) {
    this.lineHeightMultiplierHint = lineHeightMultiplierHint;
    computeLineHeight();
  }

  public boolean getMaxLinesByHeight() {
    return maxLinesByHeight;
  }

  public void setMaxLinesByHeight(boolean maxLinesByHeight) {
    this.maxLinesByHeight = maxLinesByHeight;
    requestLayout();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    extraTopPadding = 0;
    extraBottomPadding = 0;
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int height = getMeasuredHeight();
    height += ensureBaselineOnGrid();
    height += ensureHeightGridAligned(height);
    setMeasuredDimension(getMeasuredWidth(), height);
    checkMaxLines(height, MeasureSpec.getMode(heightMeasureSpec));
  }

  /**
   * 当以精确的高度测量时，可以在中线垂直裁剪文本。 通过根据可用空间设置maxLines属性来防止这种情况
   */
  private void checkMaxLines(int height, int heightMode) {
    if (!maxLinesByHeight || heightMode != MeasureSpec.EXACTLY) {
      return;
    }

    int textHeight = height - getCompoundPaddingTop() - getCompoundPaddingBottom();
    int completeLines = (int) Math.floor(textHeight / getLineHeight());
    setMaxLines(completeLines);
  }

  private void computeLineHeight() {
    final Paint.FontMetrics fm = getPaint().getFontMetrics();
    final float fontHeight = Math.abs(fm.ascent - fm.descent) + fm.leading;
    final float desiredLineHeight =
        (lineHeightHint > 0) ? lineHeightHint : lineHeightMultiplierHint * fontHeight;

    final int baselineAlignedLineHeight =
        (int) ((FOUR_DIP * (float) Math.ceil(desiredLineHeight / FOUR_DIP)) + 0.5f);
    setLineSpacing(baselineAlignedLineHeight - fontHeight, 1f);
  }

  private int ensureBaselineOnGrid() {
    float baseline = getBaseline();
    float gridAlign = baseline % FOUR_DIP;
    if (gridAlign != 0) {
      extraTopPadding = (int) (FOUR_DIP - Math.ceil(gridAlign));
    }
    return extraTopPadding;
  }

  private int ensureHeightGridAligned(int height) {
    float gridOverhang = height % FOUR_DIP;
    if (gridOverhang != 0) {
      extraBottomPadding = (int) (FOUR_DIP - Math.ceil(gridOverhang));
    }
    return extraBottomPadding;
  }

  private void parseTextAttrs(TypedArray a) {
    if (a.hasValue(R.styleable.BaselineGridTextView_lineHeightMultiplierHint)) {
      lineHeightMultiplierHint =
          a.getFloat(R.styleable.BaselineGridTextView_lineHeightMultiplierHint, 1f);
    }
    if (a.hasValue(R.styleable.BaselineGridTextView_lineHeightHint)) {
      lineHeightHint = a.getDimensionPixelSize(R.styleable.BaselineGridTextView_lineHeightHint, 0);
    }
    if (a.hasValue(R.styleable.BaselineGridTextView_android_fontFamily)) {
      fontResId = a.getResourceId(R.styleable.BaselineGridTextView_android_fontFamily, 0);
    }
  }
}
