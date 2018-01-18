package com.android.settings.widget;

import android.annotation.IntDef;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewHierarchyEncoder;
import com.android.internal.R.styleable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MatchParentShrinkingLinearLayout
  extends ViewGroup
{
  public static final int HORIZONTAL = 0;
  private static final int INDEX_BOTTOM = 2;
  private static final int INDEX_CENTER_VERTICAL = 0;
  private static final int INDEX_FILL = 3;
  private static final int INDEX_TOP = 1;
  public static final int SHOW_DIVIDER_BEGINNING = 1;
  public static final int SHOW_DIVIDER_END = 4;
  public static final int SHOW_DIVIDER_MIDDLE = 2;
  public static final int SHOW_DIVIDER_NONE = 0;
  public static final int VERTICAL = 1;
  private static final int VERTICAL_GRAVITY_COUNT = 4;
  @ViewDebug.ExportedProperty(category="layout")
  private boolean mBaselineAligned = true;
  @ViewDebug.ExportedProperty(category="layout")
  private int mBaselineAlignedChildIndex = -1;
  @ViewDebug.ExportedProperty(category="measurement")
  private int mBaselineChildTop = 0;
  private Drawable mDivider;
  private int mDividerHeight;
  private int mDividerPadding;
  private int mDividerWidth;
  @ViewDebug.ExportedProperty(category="measurement", flagMapping={@android.view.ViewDebug.FlagToString(equals=-1, mask=-1, name="NONE"), @android.view.ViewDebug.FlagToString(equals=0, mask=0, name="NONE"), @android.view.ViewDebug.FlagToString(equals=48, mask=48, name="TOP"), @android.view.ViewDebug.FlagToString(equals=80, mask=80, name="BOTTOM"), @android.view.ViewDebug.FlagToString(equals=3, mask=3, name="LEFT"), @android.view.ViewDebug.FlagToString(equals=5, mask=5, name="RIGHT"), @android.view.ViewDebug.FlagToString(equals=8388611, mask=8388611, name="START"), @android.view.ViewDebug.FlagToString(equals=8388613, mask=8388613, name="END"), @android.view.ViewDebug.FlagToString(equals=16, mask=16, name="CENTER_VERTICAL"), @android.view.ViewDebug.FlagToString(equals=112, mask=112, name="FILL_VERTICAL"), @android.view.ViewDebug.FlagToString(equals=1, mask=1, name="CENTER_HORIZONTAL"), @android.view.ViewDebug.FlagToString(equals=7, mask=7, name="FILL_HORIZONTAL"), @android.view.ViewDebug.FlagToString(equals=17, mask=17, name="CENTER"), @android.view.ViewDebug.FlagToString(equals=119, mask=119, name="FILL"), @android.view.ViewDebug.FlagToString(equals=8388608, mask=8388608, name="RELATIVE")}, formatToHexString=true)
  private int mGravity = 8388659;
  private int mLayoutDirection = -1;
  private int[] mMaxAscent;
  private int[] mMaxDescent;
  @ViewDebug.ExportedProperty(category="measurement")
  private int mOrientation;
  private int mShowDividers;
  @ViewDebug.ExportedProperty(category="measurement")
  private int mTotalLength;
  @ViewDebug.ExportedProperty(category="layout")
  private boolean mUseLargestChild;
  @ViewDebug.ExportedProperty(category="layout")
  private float mWeightSum;
  
  public MatchParentShrinkingLinearLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public MatchParentShrinkingLinearLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public MatchParentShrinkingLinearLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public MatchParentShrinkingLinearLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.LinearLayout, paramInt1, paramInt2);
    paramInt1 = paramContext.getInt(1, -1);
    if (paramInt1 >= 0) {
      setOrientation(paramInt1);
    }
    paramInt1 = paramContext.getInt(0, -1);
    if (paramInt1 >= 0) {
      setGravity(paramInt1);
    }
    boolean bool = paramContext.getBoolean(2, true);
    if (!bool) {
      setBaselineAligned(bool);
    }
    this.mWeightSum = paramContext.getFloat(4, -1.0F);
    this.mBaselineAlignedChildIndex = paramContext.getInt(3, -1);
    this.mUseLargestChild = paramContext.getBoolean(6, false);
    setDividerDrawable(paramContext.getDrawable(5));
    this.mShowDividers = paramContext.getInt(7, 0);
    this.mDividerPadding = paramContext.getDimensionPixelSize(8, 0);
    paramContext.recycle();
  }
  
  private void forceUniformHeight(int paramInt1, int paramInt2)
  {
    int j = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824);
    int i = 0;
    while (i < paramInt1)
    {
      View localView = getVirtualChildAt(i);
      if (localView.getVisibility() != 8)
      {
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if (localLayoutParams.height == -1)
        {
          int k = localLayoutParams.width;
          localLayoutParams.width = localView.getMeasuredWidth();
          measureChildWithMargins(localView, paramInt2, 0, j, 0);
          localLayoutParams.width = k;
        }
      }
      i += 1;
    }
  }
  
  private void forceUniformWidth(int paramInt1, int paramInt2)
  {
    int j = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
    int i = 0;
    while (i < paramInt1)
    {
      View localView = getVirtualChildAt(i);
      if (localView.getVisibility() != 8)
      {
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if (localLayoutParams.width == -1)
        {
          int k = localLayoutParams.height;
          localLayoutParams.height = localView.getMeasuredHeight();
          measureChildWithMargins(localView, j, 0, paramInt2, 0);
          localLayoutParams.height = k;
        }
      }
      i += 1;
    }
  }
  
  private void setChildFrame(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramView.layout(paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4);
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  void drawDividersHorizontal(Canvas paramCanvas)
  {
    int k = getVirtualChildCount();
    boolean bool = isLayoutRtl();
    int i = 0;
    View localView;
    LayoutParams localLayoutParams;
    if (i < k)
    {
      localView = getVirtualChildAt(i);
      if ((localView != null) && (localView.getVisibility() != 8) && (hasDividerBeforeChildAt(i)))
      {
        localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if (!bool) {
          break label90;
        }
      }
      label90:
      for (int j = localView.getRight() + localLayoutParams.rightMargin;; j = localView.getLeft() - localLayoutParams.leftMargin - this.mDividerWidth)
      {
        drawVerticalDivider(paramCanvas, j);
        i += 1;
        break;
      }
    }
    if (hasDividerBeforeChildAt(k))
    {
      localView = getVirtualChildAt(k - 1);
      if (localView != null) {
        break label169;
      }
      if (!bool) {
        break label151;
      }
      i = getPaddingLeft();
    }
    for (;;)
    {
      drawVerticalDivider(paramCanvas, i);
      return;
      label151:
      i = getWidth() - getPaddingRight() - this.mDividerWidth;
      continue;
      label169:
      localLayoutParams = (LayoutParams)localView.getLayoutParams();
      if (bool) {
        i = localView.getLeft() - localLayoutParams.leftMargin - this.mDividerWidth;
      } else {
        i = localView.getRight() + localLayoutParams.rightMargin;
      }
    }
  }
  
  void drawDividersVertical(Canvas paramCanvas)
  {
    int j = getVirtualChildCount();
    int i = 0;
    View localView;
    LayoutParams localLayoutParams;
    while (i < j)
    {
      localView = getVirtualChildAt(i);
      if ((localView != null) && (localView.getVisibility() != 8) && (hasDividerBeforeChildAt(i)))
      {
        localLayoutParams = (LayoutParams)localView.getLayoutParams();
        drawHorizontalDivider(paramCanvas, localView.getTop() - localLayoutParams.topMargin - this.mDividerHeight);
      }
      i += 1;
    }
    if (hasDividerBeforeChildAt(j))
    {
      localView = getVirtualChildAt(j - 1);
      if (localView != null) {
        break label124;
      }
    }
    for (i = getHeight() - getPaddingBottom() - this.mDividerHeight;; i = localView.getBottom() + localLayoutParams.bottomMargin)
    {
      drawHorizontalDivider(paramCanvas, i);
      return;
      label124:
      localLayoutParams = (LayoutParams)localView.getLayoutParams();
    }
  }
  
  void drawHorizontalDivider(Canvas paramCanvas, int paramInt)
  {
    this.mDivider.setBounds(getPaddingLeft() + this.mDividerPadding, paramInt, getWidth() - getPaddingRight() - this.mDividerPadding, this.mDividerHeight + paramInt);
    this.mDivider.draw(paramCanvas);
  }
  
  void drawVerticalDivider(Canvas paramCanvas, int paramInt)
  {
    this.mDivider.setBounds(paramInt, getPaddingTop() + this.mDividerPadding, this.mDividerWidth + paramInt, getHeight() - getPaddingBottom() - this.mDividerPadding);
    this.mDivider.draw(paramCanvas);
  }
  
  protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
  {
    super.encodeProperties(paramViewHierarchyEncoder);
    paramViewHierarchyEncoder.addProperty("layout:baselineAligned", this.mBaselineAligned);
    paramViewHierarchyEncoder.addProperty("layout:baselineAlignedChildIndex", this.mBaselineAlignedChildIndex);
    paramViewHierarchyEncoder.addProperty("measurement:baselineChildTop", this.mBaselineChildTop);
    paramViewHierarchyEncoder.addProperty("measurement:orientation", this.mOrientation);
    paramViewHierarchyEncoder.addProperty("measurement:gravity", this.mGravity);
    paramViewHierarchyEncoder.addProperty("measurement:totalLength", this.mTotalLength);
    paramViewHierarchyEncoder.addProperty("layout:totalLength", this.mTotalLength);
    paramViewHierarchyEncoder.addProperty("layout:useLargestChild", this.mUseLargestChild);
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    if (this.mOrientation == 0) {
      return new LayoutParams(-2, -2);
    }
    if (this.mOrientation == 1) {
      return new LayoutParams(-1, -2);
    }
    return null;
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return new LayoutParams(paramLayoutParams);
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return MatchParentShrinkingLinearLayout.class.getName();
  }
  
  public int getBaseline()
  {
    if (this.mBaselineAlignedChildIndex < 0) {
      return super.getBaseline();
    }
    if (getChildCount() <= this.mBaselineAlignedChildIndex) {
      throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout set to an index that is out of bounds.");
    }
    View localView = getChildAt(this.mBaselineAlignedChildIndex);
    int k = localView.getBaseline();
    if (k == -1)
    {
      if (this.mBaselineAlignedChildIndex == 0) {
        return -1;
      }
      throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout points to a View that doesn't know how to get its baseline.");
    }
    int j = this.mBaselineChildTop;
    int i = j;
    if (this.mOrientation == 1)
    {
      int m = this.mGravity & 0x70;
      i = j;
      if (m != 48) {
        switch (m)
        {
        default: 
          i = j;
        }
      }
    }
    for (;;)
    {
      return ((LayoutParams)localView.getLayoutParams()).topMargin + i + k;
      i = this.mBottom - this.mTop - this.mPaddingBottom - this.mTotalLength;
      continue;
      i = j + (this.mBottom - this.mTop - this.mPaddingTop - this.mPaddingBottom - this.mTotalLength) / 2;
    }
  }
  
  public int getBaselineAlignedChildIndex()
  {
    return this.mBaselineAlignedChildIndex;
  }
  
  int getChildrenSkipCount(View paramView, int paramInt)
  {
    return 0;
  }
  
  public Drawable getDividerDrawable()
  {
    return this.mDivider;
  }
  
  public int getDividerPadding()
  {
    return this.mDividerPadding;
  }
  
  public int getDividerWidth()
  {
    return this.mDividerWidth;
  }
  
  int getLocationOffset(View paramView)
  {
    return 0;
  }
  
  int getNextLocationOffset(View paramView)
  {
    return 0;
  }
  
  public int getOrientation()
  {
    return this.mOrientation;
  }
  
  public int getShowDividers()
  {
    return this.mShowDividers;
  }
  
  View getVirtualChildAt(int paramInt)
  {
    return getChildAt(paramInt);
  }
  
  int getVirtualChildCount()
  {
    return getChildCount();
  }
  
  public float getWeightSum()
  {
    return this.mWeightSum;
  }
  
  protected boolean hasDividerBeforeChildAt(int paramInt)
  {
    if (paramInt == 0) {
      return (this.mShowDividers & 0x1) != 0;
    }
    if (paramInt == getChildCount()) {
      return (this.mShowDividers & 0x4) != 0;
    }
    if ((this.mShowDividers & 0x2) != 0)
    {
      boolean bool2 = false;
      paramInt -= 1;
      for (;;)
      {
        boolean bool1 = bool2;
        if (paramInt >= 0)
        {
          if (getChildAt(paramInt).getVisibility() != 8) {
            bool1 = true;
          }
        }
        else {
          return bool1;
        }
        paramInt -= 1;
      }
    }
    return false;
  }
  
  public boolean isBaselineAligned()
  {
    return this.mBaselineAligned;
  }
  
  public boolean isMeasureWithLargestChildEnabled()
  {
    return this.mUseLargestChild;
  }
  
  void layoutHorizontal(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    boolean bool1 = isLayoutRtl();
    int k = this.mPaddingTop;
    int n = paramInt4 - paramInt2;
    int i1 = this.mPaddingBottom;
    int i2 = this.mPaddingBottom;
    int i3 = getVirtualChildCount();
    paramInt2 = this.mGravity;
    int i4 = this.mGravity;
    boolean bool2 = this.mBaselineAligned;
    int[] arrayOfInt1 = this.mMaxAscent;
    int[] arrayOfInt2 = this.mMaxDescent;
    label133:
    int i5;
    View localView;
    switch (Gravity.getAbsoluteGravity(paramInt2 & 0x800007, getLayoutDirection()))
    {
    default: 
      paramInt1 = this.mPaddingLeft;
      int i = 0;
      paramInt4 = 1;
      if (bool1)
      {
        i = i3 - 1;
        paramInt4 = -1;
      }
      paramInt2 = 0;
      paramInt3 = paramInt1;
      if (paramInt2 >= i3) {
        return;
      }
      i5 = i + paramInt4 * paramInt2;
      localView = getVirtualChildAt(i5);
      if (localView == null)
      {
        paramInt1 = paramInt3 + measureNullChild(i5);
        j = paramInt2;
      }
      break;
    }
    do
    {
      paramInt2 = j + 1;
      paramInt3 = paramInt1;
      break label133;
      paramInt1 = this.mPaddingLeft + paramInt3 - paramInt1 - this.mTotalLength;
      break;
      paramInt1 = this.mPaddingLeft + (paramInt3 - paramInt1 - this.mTotalLength) / 2;
      break;
      paramInt1 = paramInt3;
      j = paramInt2;
    } while (localView.getVisibility() == 8);
    int i6 = localView.getMeasuredWidth();
    int i7 = localView.getMeasuredHeight();
    paramInt1 = -1;
    LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
    int j = paramInt1;
    if (bool2)
    {
      j = paramInt1;
      if (localLayoutParams.height != -1) {
        j = localView.getBaseline();
      }
    }
    int m = localLayoutParams.gravity;
    paramInt1 = m;
    if (m < 0) {
      paramInt1 = i4 & 0x70;
    }
    switch (paramInt1 & 0x70)
    {
    default: 
      paramInt1 = k;
    }
    for (;;)
    {
      j = paramInt3;
      if (hasDividerBeforeChildAt(i5)) {
        j = paramInt3 + this.mDividerWidth;
      }
      paramInt3 = j + localLayoutParams.leftMargin;
      setChildFrame(localView, paramInt3 + getLocationOffset(localView), paramInt1, i6, i7);
      paramInt1 = paramInt3 + (localLayoutParams.rightMargin + i6 + getNextLocationOffset(localView));
      j = paramInt2 + getChildrenSkipCount(localView, i5);
      break;
      m = k + localLayoutParams.topMargin;
      paramInt1 = m;
      if (j != -1)
      {
        paramInt1 = m + (arrayOfInt1[1] - j);
        continue;
        paramInt1 = (n - k - i2 - i7) / 2 + k + localLayoutParams.topMargin - localLayoutParams.bottomMargin;
        continue;
        m = n - i1 - i7 - localLayoutParams.bottomMargin;
        paramInt1 = m;
        if (j != -1)
        {
          paramInt1 = localView.getMeasuredHeight();
          paramInt1 = m - (arrayOfInt2[2] - (paramInt1 - j));
        }
      }
    }
  }
  
  void layoutVertical(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = this.mPaddingLeft;
    int j = paramInt3 - paramInt1;
    int k = this.mPaddingRight;
    int m = this.mPaddingRight;
    int n = getVirtualChildCount();
    paramInt1 = this.mGravity;
    int i1 = this.mGravity;
    label79:
    View localView;
    switch (paramInt1 & 0x70)
    {
    default: 
      paramInt1 = this.mPaddingTop;
      paramInt2 = 0;
      if (paramInt2 >= n) {
        return;
      }
      localView = getVirtualChildAt(paramInt2);
      if (localView == null)
      {
        paramInt3 = paramInt1 + measureNullChild(paramInt2);
        paramInt4 = paramInt2;
      }
      break;
    }
    do
    {
      paramInt2 = paramInt4 + 1;
      paramInt1 = paramInt3;
      break label79;
      paramInt1 = this.mPaddingTop + paramInt4 - paramInt2 - this.mTotalLength;
      break;
      paramInt1 = this.mPaddingTop + (paramInt4 - paramInt2 - this.mTotalLength) / 2;
      break;
      paramInt3 = paramInt1;
      paramInt4 = paramInt2;
    } while (localView.getVisibility() == 8);
    int i2 = localView.getMeasuredWidth();
    int i3 = localView.getMeasuredHeight();
    LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
    paramInt4 = localLayoutParams.gravity;
    paramInt3 = paramInt4;
    if (paramInt4 < 0) {
      paramInt3 = i1 & 0x800007;
    }
    switch (Gravity.getAbsoluteGravity(paramInt3, getLayoutDirection()) & 0x7)
    {
    default: 
      paramInt3 = i + localLayoutParams.leftMargin;
    }
    for (;;)
    {
      paramInt4 = paramInt1;
      if (hasDividerBeforeChildAt(paramInt2)) {
        paramInt4 = paramInt1 + this.mDividerHeight;
      }
      paramInt1 = paramInt4 + localLayoutParams.topMargin;
      setChildFrame(localView, paramInt3, paramInt1 + getLocationOffset(localView), i2, i3);
      paramInt3 = paramInt1 + (localLayoutParams.bottomMargin + i3 + getNextLocationOffset(localView));
      paramInt4 = paramInt2 + getChildrenSkipCount(localView, paramInt2);
      break;
      paramInt3 = (j - i - m - i2) / 2 + i + localLayoutParams.leftMargin - localLayoutParams.rightMargin;
      continue;
      paramInt3 = j - k - i2 - localLayoutParams.rightMargin;
    }
  }
  
  void measureChildBeforeLayout(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    measureChildWithMargins(paramView, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  void measureHorizontal(int paramInt1, int paramInt2)
  {
    throw new IllegalStateException("horizontal mode not supported.");
  }
  
  int measureNullChild(int paramInt)
  {
    return 0;
  }
  
  void measureVertical(int paramInt1, int paramInt2)
  {
    this.mTotalLength = 0;
    int m = 0;
    int k = 0;
    int i = 0;
    int i1 = 0;
    int j = 1;
    float f1 = 0.0F;
    int i8 = getVirtualChildCount();
    int i9 = View.MeasureSpec.getMode(paramInt1);
    int i10 = View.MeasureSpec.getMode(paramInt2);
    int n = 0;
    int i4 = 0;
    int i11 = this.mBaselineAlignedChildIndex;
    boolean bool = this.mUseLargestChild;
    int i3 = Integer.MIN_VALUE;
    int i2 = 0;
    int i6;
    label148:
    LayoutParams localLayoutParams;
    if (i2 < i8)
    {
      localView = getVirtualChildAt(i2);
      if (localView == null) {
        this.mTotalLength += measureNullChild(i2);
      }
      for (i6 = i3;; i6 = i3)
      {
        i2 += 1;
        i3 = i6;
        break;
        if (localView.getVisibility() != 8) {
          break label148;
        }
        i2 += getChildrenSkipCount(localView, i2);
      }
      if (hasDividerBeforeChildAt(i2)) {
        this.mTotalLength += this.mDividerHeight;
      }
      localLayoutParams = (LayoutParams)localView.getLayoutParams();
      f1 += localLayoutParams.weight;
      if ((i10 == 1073741824) && (localLayoutParams.height == 0) && (localLayoutParams.weight > 0.0F))
      {
        i4 = this.mTotalLength;
        this.mTotalLength = Math.max(i4, localLayoutParams.topMargin + i4 + localLayoutParams.bottomMargin);
        i5 = 1;
        i6 = i3;
        if ((i11 >= 0) && (i11 == i2 + 1)) {
          this.mBaselineChildTop = this.mTotalLength;
        }
        if ((i2 < i11) && (localLayoutParams.weight > 0.0F)) {
          throw new RuntimeException("A child of LinearLayout with index less than mBaselineAlignedChildIndex has weight > 0, which won't work.  Either remove the weight, or don't set mBaselineAlignedChildIndex.");
        }
      }
      else
      {
        i6 = Integer.MIN_VALUE;
        i5 = i6;
        if (localLayoutParams.height == 0)
        {
          i5 = i6;
          if (localLayoutParams.weight > 0.0F)
          {
            i5 = 0;
            localLayoutParams.height = -2;
          }
        }
        if (f1 == 0.0F) {}
        for (i6 = this.mTotalLength;; i6 = 0)
        {
          measureChildBeforeLayout(localView, i2, paramInt1, 0, paramInt2, i6);
          if (i5 != Integer.MIN_VALUE) {
            localLayoutParams.height = i5;
          }
          i7 = localView.getMeasuredHeight();
          i5 = this.mTotalLength;
          this.mTotalLength = Math.max(i5, i5 + i7 + localLayoutParams.topMargin + localLayoutParams.bottomMargin + getNextLocationOffset(localView));
          i6 = i3;
          i5 = i4;
          if (!bool) {
            break;
          }
          i6 = Math.max(i7, i3);
          i5 = i4;
          break;
        }
      }
      i7 = 0;
      i3 = n;
      i4 = i7;
      if (i9 != 1073741824)
      {
        i3 = n;
        i4 = i7;
        if (localLayoutParams.width == -1)
        {
          i3 = 1;
          i4 = 1;
        }
      }
      n = localLayoutParams.leftMargin + localLayoutParams.rightMargin;
      i7 = localView.getMeasuredWidth() + n;
      m = Math.max(m, i7);
      k = combineMeasuredStates(k, localView.getMeasuredState());
      if ((j != 0) && (localLayoutParams.width == -1))
      {
        j = 1;
        label563:
        if (localLayoutParams.weight <= 0.0F) {
          break label624;
        }
        if (i4 == 0) {
          break label617;
        }
      }
      for (;;)
      {
        i1 = Math.max(i1, n);
        i2 += getChildrenSkipCount(localView, i2);
        n = i3;
        i4 = i5;
        break;
        j = 0;
        break label563;
        label617:
        n = i7;
      }
      label624:
      if (i4 != 0) {}
      for (;;)
      {
        i = Math.max(i, n);
        break;
        n = i7;
      }
    }
    if ((this.mTotalLength > 0) && (hasDividerBeforeChildAt(i8))) {
      this.mTotalLength += this.mDividerHeight;
    }
    if ((bool) && ((i10 == Integer.MIN_VALUE) || (i10 == 0)))
    {
      this.mTotalLength = 0;
      i2 = 0;
      if (i2 < i8)
      {
        localView = getVirtualChildAt(i2);
        if (localView == null) {
          this.mTotalLength += measureNullChild(i2);
        }
        for (;;)
        {
          i2 += 1;
          break;
          if (localView.getVisibility() == 8)
          {
            i2 += getChildrenSkipCount(localView, i2);
          }
          else
          {
            localLayoutParams = (LayoutParams)localView.getLayoutParams();
            i5 = this.mTotalLength;
            this.mTotalLength = Math.max(i5, i5 + i3 + localLayoutParams.topMargin + localLayoutParams.bottomMargin + getNextLocationOffset(localView));
          }
        }
      }
    }
    this.mTotalLength += this.mPaddingTop + this.mPaddingBottom;
    int i7 = resolveSizeAndState(Math.max(this.mTotalLength, getSuggestedMinimumHeight()), paramInt2, 0);
    int i5 = (i7 & 0xFFFFFF) - this.mTotalLength;
    if ((i4 != 0) || ((i5 != 0) && (f1 > 0.0F)))
    {
      if (this.mWeightSum > 0.0F) {
        f1 = this.mWeightSum;
      }
      for (;;)
      {
        this.mTotalLength = 0;
        i4 = 0;
        i2 = m;
        i3 = i5;
        i1 = i;
        m = j;
        for (;;)
        {
          if (i4 >= i8) {
            break label1479;
          }
          localView = getVirtualChildAt(i4);
          if (localView.getVisibility() != 8) {
            break;
          }
          i = i3;
          j = k;
          k = m;
          i4 += 1;
          m = k;
          k = j;
          i3 = i;
        }
      }
      localLayoutParams = (LayoutParams)localView.getLayoutParams();
      float f2 = localLayoutParams.weight;
      if ((f2 > 0.0F) && (i3 > 0))
      {
        i = (int)(i3 * f2 / f1);
        f2 = f1 - f2;
        j = i3 - i;
        i5 = getChildMeasureSpec(paramInt1, this.mPaddingLeft + this.mPaddingRight + localLayoutParams.leftMargin + localLayoutParams.rightMargin, localLayoutParams.width);
        if ((localLayoutParams.height != 0) || (i10 != 1073741824))
        {
          i3 = localView.getMeasuredHeight() + i;
          i = i3;
          if (i3 < 0) {
            i = 0;
          }
          localView.measure(i5, View.MeasureSpec.makeMeasureSpec(i, 1073741824));
          k = combineMeasuredStates(k, localView.getMeasuredState() & 0xFF00);
          i = j;
          j = k;
          label1149:
          i3 = localLayoutParams.leftMargin + localLayoutParams.rightMargin;
          i5 = localView.getMeasuredWidth() + i3;
          i2 = Math.max(i2, i5);
          if (i9 == 1073741824) {
            break label1460;
          }
          if (localLayoutParams.width != -1) {
            break label1454;
          }
          k = 1;
          label1200:
          if (k == 0) {
            break label1466;
          }
          k = i3;
          label1209:
          i1 = Math.max(i1, k);
          if ((m == 0) || (localLayoutParams.width != -1)) {
            break label1473;
          }
        }
      }
      label1454:
      label1460:
      label1466:
      label1473:
      for (k = 1;; k = 0)
      {
        m = this.mTotalLength;
        this.mTotalLength = Math.max(m, localView.getMeasuredHeight() + m + localLayoutParams.topMargin + localLayoutParams.bottomMargin + getNextLocationOffset(localView));
        f1 = f2;
        break;
        if (i > 0) {}
        for (;;)
        {
          localView.measure(i5, View.MeasureSpec.makeMeasureSpec(i, 1073741824));
          break;
          i = 0;
        }
        j = k;
        i = i3;
        f2 = f1;
        if (i3 >= 0) {
          break label1149;
        }
        j = k;
        i = i3;
        f2 = f1;
        if (localLayoutParams.height != -1) {
          break label1149;
        }
        i5 = getChildMeasureSpec(paramInt1, this.mPaddingLeft + this.mPaddingRight + localLayoutParams.leftMargin + localLayoutParams.rightMargin, localLayoutParams.width);
        j = localView.getMeasuredHeight() + i3;
        i = j;
        if (j < 0) {
          i = 0;
        }
        i3 -= i - localView.getMeasuredHeight();
        localView.measure(i5, View.MeasureSpec.makeMeasureSpec(i, 1073741824));
        j = combineMeasuredStates(k, localView.getMeasuredState() & 0xFF00);
        i = i3;
        f2 = f1;
        break label1149;
        k = 0;
        break label1200;
        k = 0;
        break label1200;
        k = i5;
        break label1209;
      }
      label1479:
      this.mTotalLength += this.mPaddingTop + this.mPaddingBottom;
      i = k;
      i4 = m;
    }
    do
    {
      do
      {
        do
        {
          j = i2;
          if (i4 == 0)
          {
            j = i2;
            if (i9 != 1073741824) {
              j = i1;
            }
          }
          setMeasuredDimension(resolveSizeAndState(Math.max(j + (this.mPaddingLeft + this.mPaddingRight), getSuggestedMinimumWidth()), paramInt1, i), i7);
          if (n != 0) {
            forceUniformWidth(i8, paramInt2);
          }
          return;
          i6 = Math.max(i, i1);
          i4 = j;
          i1 = i6;
          i = k;
          i2 = m;
        } while (!bool);
        i4 = j;
        i1 = i6;
        i = k;
        i2 = m;
      } while (i10 == 1073741824);
      i5 = 0;
      i4 = j;
      i1 = i6;
      i = k;
      i2 = m;
    } while (i5 >= i8);
    View localView = getVirtualChildAt(i5);
    if ((localView == null) || (localView.getVisibility() == 8)) {}
    for (;;)
    {
      i5 += 1;
      break;
      if (((LayoutParams)localView.getLayoutParams()).weight > 0.0F) {
        localView.measure(View.MeasureSpec.makeMeasureSpec(localView.getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(i3, 1073741824));
      }
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.mDivider == null) {
      return;
    }
    if (this.mOrientation == 1)
    {
      drawDividersVertical(paramCanvas);
      return;
    }
    drawDividersHorizontal(paramCanvas);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.mOrientation == 1)
    {
      layoutVertical(paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    layoutHorizontal(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (this.mOrientation == 1)
    {
      measureVertical(paramInt1, paramInt2);
      return;
    }
    measureHorizontal(paramInt1, paramInt2);
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    super.onRtlPropertiesChanged(paramInt);
    if (paramInt != this.mLayoutDirection)
    {
      this.mLayoutDirection = paramInt;
      if (this.mOrientation == 0) {
        requestLayout();
      }
    }
  }
  
  @RemotableViewMethod
  public void setBaselineAligned(boolean paramBoolean)
  {
    this.mBaselineAligned = paramBoolean;
  }
  
  @RemotableViewMethod
  public void setBaselineAlignedChildIndex(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= getChildCount())) {
      throw new IllegalArgumentException("base aligned child index out of range (0, " + getChildCount() + ")");
    }
    this.mBaselineAlignedChildIndex = paramInt;
  }
  
  public void setDividerDrawable(Drawable paramDrawable)
  {
    boolean bool = false;
    if (paramDrawable == this.mDivider) {
      return;
    }
    this.mDivider = paramDrawable;
    if (paramDrawable != null) {
      this.mDividerWidth = paramDrawable.getIntrinsicWidth();
    }
    for (this.mDividerHeight = paramDrawable.getIntrinsicHeight();; this.mDividerHeight = 0)
    {
      if (paramDrawable == null) {
        bool = true;
      }
      setWillNotDraw(bool);
      requestLayout();
      return;
      this.mDividerWidth = 0;
    }
  }
  
  public void setDividerPadding(int paramInt)
  {
    this.mDividerPadding = paramInt;
  }
  
  @RemotableViewMethod
  public void setGravity(int paramInt)
  {
    if (this.mGravity != paramInt)
    {
      int i = paramInt;
      if ((0x800007 & paramInt) == 0) {
        i = paramInt | 0x800003;
      }
      paramInt = i;
      if ((i & 0x70) == 0) {
        paramInt = i | 0x30;
      }
      this.mGravity = paramInt;
      requestLayout();
    }
  }
  
  @RemotableViewMethod
  public void setHorizontalGravity(int paramInt)
  {
    paramInt &= 0x800007;
    if ((this.mGravity & 0x800007) != paramInt)
    {
      this.mGravity = (this.mGravity & 0xFF7FFFF8 | paramInt);
      requestLayout();
    }
  }
  
  @RemotableViewMethod
  public void setMeasureWithLargestChildEnabled(boolean paramBoolean)
  {
    this.mUseLargestChild = paramBoolean;
  }
  
  public void setOrientation(int paramInt)
  {
    if (this.mOrientation != paramInt)
    {
      this.mOrientation = paramInt;
      requestLayout();
    }
  }
  
  public void setShowDividers(int paramInt)
  {
    if (paramInt != this.mShowDividers) {
      requestLayout();
    }
    this.mShowDividers = paramInt;
  }
  
  @RemotableViewMethod
  public void setVerticalGravity(int paramInt)
  {
    paramInt &= 0x70;
    if ((this.mGravity & 0x70) != paramInt)
    {
      this.mGravity = (this.mGravity & 0xFFFFFF8F | paramInt);
      requestLayout();
    }
  }
  
  @RemotableViewMethod
  public void setWeightSum(float paramFloat)
  {
    this.mWeightSum = Math.max(0.0F, paramFloat);
  }
  
  public boolean shouldDelayChildPressedState()
  {
    return false;
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef(flag=true, value={0L, 1L, 2L, 4L})
  public static @interface DividerMode {}
  
  public static class LayoutParams
    extends ViewGroup.MarginLayoutParams
  {
    @ViewDebug.ExportedProperty(category="layout", mapping={@android.view.ViewDebug.IntToString(from=-1, to="NONE"), @android.view.ViewDebug.IntToString(from=0, to="NONE"), @android.view.ViewDebug.IntToString(from=48, to="TOP"), @android.view.ViewDebug.IntToString(from=80, to="BOTTOM"), @android.view.ViewDebug.IntToString(from=3, to="LEFT"), @android.view.ViewDebug.IntToString(from=5, to="RIGHT"), @android.view.ViewDebug.IntToString(from=8388611, to="START"), @android.view.ViewDebug.IntToString(from=8388613, to="END"), @android.view.ViewDebug.IntToString(from=16, to="CENTER_VERTICAL"), @android.view.ViewDebug.IntToString(from=112, to="FILL_VERTICAL"), @android.view.ViewDebug.IntToString(from=1, to="CENTER_HORIZONTAL"), @android.view.ViewDebug.IntToString(from=7, to="FILL_HORIZONTAL"), @android.view.ViewDebug.IntToString(from=17, to="CENTER"), @android.view.ViewDebug.IntToString(from=119, to="FILL")})
    public int gravity = -1;
    @ViewDebug.ExportedProperty(category="layout")
    public float weight;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
      this.weight = 0.0F;
    }
    
    public LayoutParams(int paramInt1, int paramInt2, float paramFloat)
    {
      super(paramInt2);
      this.weight = paramFloat;
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.LinearLayout_Layout);
      this.weight = paramContext.getFloat(3, 0.0F);
      this.gravity = paramContext.getInt(0, -1);
      paramContext.recycle();
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      super();
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
      this.weight = paramLayoutParams.weight;
      this.gravity = paramLayoutParams.gravity;
    }
    
    public String debug(String paramString)
    {
      return paramString + "MatchParentShrinkingLinearLayout.LayoutParams={width=" + sizeToString(this.width) + ", height=" + sizeToString(this.height) + " weight=" + this.weight + "}";
    }
    
    protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
    {
      super.encodeProperties(paramViewHierarchyEncoder);
      paramViewHierarchyEncoder.addProperty("layout:weight", this.weight);
      paramViewHierarchyEncoder.addProperty("layout:gravity", this.gravity);
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({0L, 1L})
  public static @interface OrientationMode {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\widget\MatchParentShrinkingLinearLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */