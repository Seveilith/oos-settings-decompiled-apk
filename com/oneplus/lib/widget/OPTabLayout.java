package com.oneplus.lib.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.TargetApi;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.oneplus.commonctrl.R.attr;
import com.oneplus.commonctrl.R.dimen;
import com.oneplus.commonctrl.R.layout;
import com.oneplus.commonctrl.R.style;
import com.oneplus.commonctrl.R.styleable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;

public class OPTabLayout
  extends HorizontalScrollView
{
  private static final int ANIMATION_DURATION = 300;
  private static final int DEFAULT_HEIGHT = 48;
  private static final int FIXED_WRAP_GUTTER_MIN = 16;
  public static final int GRAVITY_CENTER = 1;
  public static final int GRAVITY_FILL = 0;
  public static final int MODE_FIXED = 1;
  public static final int MODE_SCROLLABLE = 0;
  private static final int MOTION_NON_ADJACENT_OFFSET = 24;
  private static final int TAB_MIN_WIDTH_MARGIN = 56;
  private Interpolator fast_out_slow_in_interpolator;
  private int mContentInsetStart;
  private ValueAnimator mIndicatorAnimator;
  private int mMode;
  private OnTabSelectedListener mOnTabSelectedListener;
  private final int mRequestedTabMaxWidth;
  private ValueAnimator mScrollAnimator;
  private Tab mSelectedTab;
  private final int mTabBackgroundResId;
  private View.OnClickListener mTabClickListener;
  private int mTabGravity;
  private int mTabHorizontalSpacing;
  private int mTabMaxWidth = Integer.MAX_VALUE;
  private final int mTabMinWidth;
  private int mTabPaddingBottom;
  private int mTabPaddingEnd;
  private int mTabPaddingStart;
  private int mTabPaddingTop;
  private final SlidingTabStrip mTabStrip;
  private int mTabTextAppearance;
  private ColorStateList mTabTextColors;
  private final ArrayList<Tab> mTabs = new ArrayList();
  
  public OPTabLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public OPTabLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.OPTabLayoutStyle);
  }
  
  public OPTabLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.fast_out_slow_in_interpolator = AnimationUtils.loadInterpolator(paramContext, 17563661);
    setHorizontalScrollBarEnabled(false);
    setFillViewport(true);
    this.mTabStrip = new SlidingTabStrip(paramContext);
    addView(this.mTabStrip, -2, -1);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.OPTabLayout, paramInt, R.style.Oneplus_Widget_Design_OPTabLayout);
    this.mTabStrip.setSelectedIndicatorHeight(paramContext.getDimensionPixelSize(R.styleable.OPTabLayout_op_tabIndicatorHeight, 0));
    this.mTabStrip.setSelectedIndicatorColor(paramContext.getColor(R.styleable.OPTabLayout_op_tabIndicatorColor, 0));
    this.mTabTextAppearance = paramContext.getResourceId(R.styleable.OPTabLayout_op_tabTextAppearance, R.style.Oneplus_TextAppearance_Design_Tab);
    paramInt = paramContext.getDimensionPixelSize(R.styleable.OPTabLayout_op_tabPadding, 0);
    this.mTabPaddingBottom = paramInt;
    this.mTabPaddingEnd = paramInt;
    this.mTabPaddingTop = paramInt;
    this.mTabPaddingStart = paramInt;
    this.mTabPaddingStart = paramContext.getDimensionPixelSize(R.styleable.OPTabLayout_op_tabPaddingStart, this.mTabPaddingStart);
    this.mTabPaddingTop = paramContext.getDimensionPixelSize(R.styleable.OPTabLayout_op_tabPaddingTop, this.mTabPaddingTop);
    this.mTabPaddingEnd = paramContext.getDimensionPixelSize(R.styleable.OPTabLayout_op_tabPaddingEnd, this.mTabPaddingEnd);
    this.mTabPaddingBottom = paramContext.getDimensionPixelSize(R.styleable.OPTabLayout_op_tabPaddingBottom, this.mTabPaddingBottom);
    this.mTabTextColors = loadTextColorFromTextAppearance(this.mTabTextAppearance);
    if (paramContext.hasValue(R.styleable.OPTabLayout_op_tabTextColor)) {
      this.mTabTextColors = paramContext.getColorStateList(R.styleable.OPTabLayout_op_tabTextColor);
    }
    if (paramContext.hasValue(R.styleable.OPTabLayout_op_tabSelectedTextColor))
    {
      paramInt = paramContext.getColor(R.styleable.OPTabLayout_op_tabSelectedTextColor, 0);
      this.mTabTextColors = createColorStateList(this.mTabTextColors.getDefaultColor(), paramInt);
    }
    this.mTabMinWidth = paramContext.getDimensionPixelSize(R.styleable.OPTabLayout_op_tabMinWidth, 0);
    this.mRequestedTabMaxWidth = paramContext.getDimensionPixelSize(R.styleable.OPTabLayout_op_tabMaxWidth, 0);
    this.mTabBackgroundResId = paramContext.getResourceId(R.styleable.OPTabLayout_op_tabBackground, 0);
    this.mContentInsetStart = paramContext.getDimensionPixelSize(R.styleable.OPTabLayout_op_tabContentStart, 0);
    this.mTabHorizontalSpacing = paramContext.getDimensionPixelSize(R.styleable.OPTabLayout_op_horizontalSpacing, 0);
    this.mMode = paramContext.getInt(R.styleable.OPTabLayout_op_tabMode, 1);
    this.mTabGravity = paramContext.getInt(R.styleable.OPTabLayout_op_tabGravity, 0);
    paramContext.recycle();
    applyModeAndGravity();
  }
  
  private void addTabView(Tab paramTab, int paramInt, boolean paramBoolean)
  {
    paramTab = createTabView(paramTab);
    this.mTabStrip.addView(paramTab, paramInt, createLayoutParamsForTabs());
    updateTabViewsMargin();
    if (paramBoolean) {
      paramTab.setSelected(true);
    }
  }
  
  private void addTabView(Tab paramTab, boolean paramBoolean)
  {
    paramTab = createTabView(paramTab);
    this.mTabStrip.addView(paramTab, createLayoutParamsForTabs());
    updateTabViewsMargin();
    if (paramBoolean) {
      paramTab.setSelected(true);
    }
  }
  
  private void animateToTab(int paramInt)
  {
    if (paramInt == -1) {
      return;
    }
    if ((getWindowToken() == null) || (!isLaidOut()) || (this.mTabStrip.childrenNeedLayout()))
    {
      setScrollPosition(paramInt, 0.0F, true);
      return;
    }
    int i = getScrollX();
    int j = calculateScrollXForTab(paramInt, 0.0F);
    if (i != j)
    {
      if (this.mScrollAnimator == null)
      {
        this.mScrollAnimator = new ValueAnimator();
        this.mScrollAnimator.setInterpolator(this.fast_out_slow_in_interpolator);
        this.mScrollAnimator.setDuration(300L);
        this.mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
          public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
          {
            OPTabLayout.this.scrollTo(((Integer)paramAnonymousValueAnimator.getAnimatedValue()).intValue(), 0);
          }
        });
      }
      this.mScrollAnimator.setIntValues(new int[] { i, j });
      this.mScrollAnimator.start();
    }
    this.mTabStrip.animateIndicatorToPosition(paramInt, 300);
  }
  
  private void applyModeAndGravity()
  {
    int i = 0;
    if (this.mMode == 0) {
      i = Math.max(0, this.mContentInsetStart - this.mTabPaddingStart);
    }
    this.mTabStrip.setPaddingRelative(i, 0, 0, 0);
    switch (this.mMode)
    {
    }
    for (;;)
    {
      updateTabViewsLayoutParams();
      return;
      this.mTabStrip.setGravity(1);
      continue;
      this.mTabStrip.setGravity(8388611);
    }
  }
  
  private int calculateScrollXForTab(int paramInt, float paramFloat)
  {
    if (this.mMode == 0)
    {
      View localView2 = this.mTabStrip.getChildAt(paramInt);
      View localView1;
      if (paramInt + 1 < this.mTabStrip.getChildCount())
      {
        localView1 = this.mTabStrip.getChildAt(paramInt + 1);
        if (localView2 == null) {
          break label103;
        }
        paramInt = localView2.getWidth();
        label53:
        if (localView1 == null) {
          break label108;
        }
      }
      label103:
      label108:
      for (int i = localView1.getWidth();; i = 0)
      {
        return localView2.getLeft() + (int)((paramInt + i) * paramFloat * 0.5F) + localView2.getWidth() / 2 - getWidth() / 2;
        localView1 = null;
        break;
        paramInt = 0;
        break label53;
      }
    }
    return 0;
  }
  
  private void configureTab(Tab paramTab, int paramInt)
  {
    paramTab.setPosition(paramInt);
    this.mTabs.add(paramInt, paramTab);
    int i = this.mTabs.size();
    paramInt += 1;
    while (paramInt < i)
    {
      ((Tab)this.mTabs.get(paramInt)).setPosition(paramInt);
      paramInt += 1;
    }
  }
  
  private static ColorStateList createColorStateList(int paramInt1, int paramInt2)
  {
    return new ColorStateList(new int[][] { SELECTED_STATE_SET, EMPTY_STATE_SET }, new int[] { paramInt2, paramInt1 });
  }
  
  private LinearLayout.LayoutParams createLayoutParamsForTabs()
  {
    LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-2, -1);
    localLayoutParams.setMarginStart(this.mTabHorizontalSpacing);
    updateTabViewLayoutParams(localLayoutParams);
    return localLayoutParams;
  }
  
  private TabView createTabView(Tab paramTab)
  {
    paramTab = new TabView(getContext(), paramTab);
    paramTab.setFocusable(true);
    if (this.mTabClickListener == null) {
      this.mTabClickListener = new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          ((OPTabLayout.TabView)paramAnonymousView).getTab().select();
        }
      };
    }
    paramTab.setOnClickListener(this.mTabClickListener);
    return paramTab;
  }
  
  private int dpToPx(int paramInt)
  {
    return Math.round(getResources().getDisplayMetrics().density * paramInt);
  }
  
  private ColorStateList loadTextColorFromTextAppearance(int paramInt)
  {
    TypedArray localTypedArray = getContext().obtainStyledAttributes(paramInt, R.styleable.TextAppearance);
    try
    {
      ColorStateList localColorStateList = localTypedArray.getColorStateList(R.styleable.TextAppearance_android_textColor);
      return localColorStateList;
    }
    finally
    {
      localTypedArray.recycle();
    }
  }
  
  private void removeTabViewAt(int paramInt)
  {
    this.mTabStrip.removeViewAt(paramInt);
    updateTabViewsMargin();
    requestLayout();
  }
  
  private void setSelectedTabView(int paramInt)
  {
    int j = this.mTabStrip.getChildCount();
    if ((paramInt >= j) || (this.mTabStrip.getChildAt(paramInt).isSelected())) {
      return;
    }
    int i = 0;
    label30:
    View localView;
    if (i < j)
    {
      localView = this.mTabStrip.getChildAt(i);
      if (i != paramInt) {
        break label67;
      }
    }
    label67:
    for (boolean bool = true;; bool = false)
    {
      localView.setSelected(bool);
      i += 1;
      break label30;
      break;
    }
  }
  
  private void updateAllTabs()
  {
    int i = 0;
    int j = this.mTabStrip.getChildCount();
    while (i < j)
    {
      updateTab(i);
      i += 1;
    }
  }
  
  private void updateTab(int paramInt)
  {
    TabView localTabView = (TabView)this.mTabStrip.getChildAt(paramInt);
    if (localTabView != null) {
      localTabView.update();
    }
  }
  
  private void updateTabViewLayoutParams(LinearLayout.LayoutParams paramLayoutParams)
  {
    if ((this.mMode == 1) && (this.mTabGravity == 0))
    {
      paramLayoutParams.width = 0;
      paramLayoutParams.weight = 1.0F;
      return;
    }
    paramLayoutParams.width = -2;
    paramLayoutParams.weight = 0.0F;
  }
  
  private void updateTabViewsLayoutParams()
  {
    int i = 0;
    while (i < this.mTabStrip.getChildCount())
    {
      View localView = this.mTabStrip.getChildAt(i);
      updateTabViewLayoutParams((LinearLayout.LayoutParams)localView.getLayoutParams());
      localView.requestLayout();
      i += 1;
    }
  }
  
  private void updateTabViewsMargin()
  {
    if (this.mTabStrip.getChildCount() > 0) {
      ((LinearLayout.LayoutParams)this.mTabStrip.getChildAt(0).getLayoutParams()).setMarginStart(0);
    }
  }
  
  public void addTab(Tab paramTab)
  {
    addTab(paramTab, this.mTabs.isEmpty());
  }
  
  public void addTab(Tab paramTab, int paramInt)
  {
    addTab(paramTab, paramInt, this.mTabs.isEmpty());
  }
  
  public void addTab(Tab paramTab, int paramInt, boolean paramBoolean)
  {
    if (Tab.-get0(paramTab) != this) {
      throw new IllegalArgumentException("Tab belongs to a different OPTabLayout.");
    }
    addTabView(paramTab, paramInt, paramBoolean);
    configureTab(paramTab, paramInt);
    if (paramBoolean) {
      paramTab.select();
    }
  }
  
  public void addTab(Tab paramTab, boolean paramBoolean)
  {
    if (Tab.-get0(paramTab) != this) {
      throw new IllegalArgumentException("Tab belongs to a different OPTabLayout.");
    }
    addTabView(paramTab, paramBoolean);
    configureTab(paramTab, this.mTabs.size());
    if (paramBoolean) {
      paramTab.select();
    }
  }
  
  public int getSelectedTabPosition()
  {
    if (this.mSelectedTab != null) {
      return this.mSelectedTab.getPosition();
    }
    return -1;
  }
  
  public Tab getTabAt(int paramInt)
  {
    return (Tab)this.mTabs.get(paramInt);
  }
  
  public int getTabCount()
  {
    return this.mTabs.size();
  }
  
  public int getTabGravity()
  {
    return this.mTabGravity;
  }
  
  public int getTabMode()
  {
    return this.mMode;
  }
  
  public ColorStateList getTabTextColors()
  {
    return this.mTabTextColors;
  }
  
  public Tab newTab()
  {
    return new Tab(this);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = getContext().getResources().getDimensionPixelSize(R.dimen.tab_layout_default_height_material) + getPaddingTop() + getPaddingBottom();
    switch (View.MeasureSpec.getMode(paramInt2))
    {
    }
    for (;;)
    {
      super.onMeasure(paramInt1, paramInt2);
      if ((this.mMode == 1) && (getChildCount() == 1))
      {
        View localView = getChildAt(0);
        i = getMeasuredWidth();
        if (localView.getMeasuredWidth() > i)
        {
          j = getChildMeasureSpec(paramInt2, getPaddingTop() + getPaddingBottom(), localView.getLayoutParams().height);
          localView.measure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), j);
        }
      }
      int j = this.mRequestedTabMaxWidth;
      int k = getMeasuredWidth() - dpToPx(56);
      if (j != 0)
      {
        i = j;
        if (j <= k) {}
      }
      else
      {
        i = k;
      }
      if (this.mTabMaxWidth != i)
      {
        this.mTabMaxWidth = i;
        super.onMeasure(paramInt1, paramInt2);
      }
      return;
      paramInt2 = View.MeasureSpec.makeMeasureSpec(Math.min(i, View.MeasureSpec.getSize(paramInt2)), 1073741824);
      continue;
      paramInt2 = View.MeasureSpec.makeMeasureSpec(i, 1073741824);
    }
  }
  
  public void removeAllTabs()
  {
    this.mTabStrip.removeAllViews();
    Iterator localIterator = this.mTabs.iterator();
    while (localIterator.hasNext())
    {
      ((Tab)localIterator.next()).setPosition(-1);
      localIterator.remove();
    }
    this.mSelectedTab = null;
  }
  
  public void removeTab(Tab paramTab)
  {
    if (Tab.-get0(paramTab) != this) {
      throw new IllegalArgumentException("Tab does not belong to this OPTabLayout.");
    }
    removeTabAt(paramTab.getPosition());
  }
  
  public void removeTabAt(int paramInt)
  {
    if (this.mSelectedTab != null) {}
    for (int i = this.mSelectedTab.getPosition();; i = 0)
    {
      removeTabViewAt(paramInt);
      localTab = (Tab)this.mTabs.remove(paramInt);
      if (localTab != null) {
        localTab.setPosition(-1);
      }
      int k = this.mTabs.size();
      int j = paramInt;
      while (j < k)
      {
        ((Tab)this.mTabs.get(j)).setPosition(j);
        j += 1;
      }
    }
    if (i == paramInt) {
      if (!this.mTabs.isEmpty()) {
        break label113;
      }
    }
    label113:
    for (Tab localTab = null;; localTab = (Tab)this.mTabs.get(Math.max(0, paramInt - 1)))
    {
      selectTab(localTab);
      return;
    }
  }
  
  void selectTab(Tab paramTab)
  {
    selectTab(paramTab, true);
  }
  
  void selectTab(Tab paramTab, boolean paramBoolean)
  {
    if (this.mSelectedTab == paramTab)
    {
      if (this.mSelectedTab != null)
      {
        if (this.mOnTabSelectedListener != null) {
          this.mOnTabSelectedListener.onTabReselected(this.mSelectedTab);
        }
        animateToTab(paramTab.getPosition());
      }
      return;
    }
    int i;
    if (paramTab != null)
    {
      i = paramTab.getPosition();
      label53:
      setSelectedTabView(i);
      if (paramBoolean)
      {
        if (((this.mSelectedTab != null) && (this.mSelectedTab.getPosition() != -1)) || (i == -1)) {
          break label157;
        }
        setScrollPosition(i, 0.0F, true);
      }
    }
    for (;;)
    {
      if ((this.mSelectedTab != null) && (this.mOnTabSelectedListener != null)) {
        this.mOnTabSelectedListener.onTabUnselected(this.mSelectedTab);
      }
      this.mSelectedTab = paramTab;
      if ((this.mSelectedTab == null) || (this.mOnTabSelectedListener == null)) {
        break;
      }
      this.mOnTabSelectedListener.onTabSelected(this.mSelectedTab);
      return;
      i = -1;
      break label53;
      label157:
      animateToTab(i);
    }
  }
  
  public void setOnTabSelectedListener(OnTabSelectedListener paramOnTabSelectedListener)
  {
    this.mOnTabSelectedListener = paramOnTabSelectedListener;
  }
  
  public void setScrollPosition(int paramInt, float paramFloat, boolean paramBoolean)
  {
    if ((this.mIndicatorAnimator != null) && (this.mIndicatorAnimator.isRunning())) {
      return;
    }
    if ((paramInt < 0) || (paramInt >= this.mTabStrip.getChildCount())) {
      return;
    }
    this.mTabStrip.setIndicatorPositionFromTabPosition(paramInt, paramFloat);
    scrollTo(calculateScrollXForTab(paramInt, paramFloat), 0);
    if (paramBoolean) {
      setSelectedTabView(Math.round(paramInt + paramFloat));
    }
  }
  
  public void setSelectedTabIndicatorColor(int paramInt)
  {
    this.mTabStrip.setSelectedIndicatorColor(paramInt);
  }
  
  public void setSelectedTabIndicatorHeight(int paramInt)
  {
    this.mTabStrip.setSelectedIndicatorHeight(paramInt);
  }
  
  public void setTabGravity(int paramInt)
  {
    if (this.mTabGravity != paramInt)
    {
      this.mTabGravity = paramInt;
      applyModeAndGravity();
    }
  }
  
  public void setTabMode(int paramInt)
  {
    if (paramInt != this.mMode)
    {
      this.mMode = paramInt;
      applyModeAndGravity();
    }
  }
  
  public void setTabTextColors(int paramInt1, int paramInt2)
  {
    setTabTextColors(createColorStateList(paramInt1, paramInt2));
  }
  
  public void setTabTextColors(ColorStateList paramColorStateList)
  {
    if (this.mTabTextColors != paramColorStateList)
    {
      this.mTabTextColors = paramColorStateList;
      updateAllTabs();
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Mode {}
  
  public static abstract interface OnTabSelectedListener
  {
    public abstract void onTabReselected(OPTabLayout.Tab paramTab);
    
    public abstract void onTabSelected(OPTabLayout.Tab paramTab);
    
    public abstract void onTabUnselected(OPTabLayout.Tab paramTab);
  }
  
  private class SlidingTabStrip
    extends LinearLayout
  {
    private int mIndicatorLeft = -1;
    private int mIndicatorRight = -1;
    private int mSelectedIndicatorHeight;
    private final Paint mSelectedIndicatorPaint;
    private int mSelectedPosition = -1;
    private float mSelectionOffset;
    
    SlidingTabStrip(Context paramContext)
    {
      super();
      setWillNotDraw(false);
      this.mSelectedIndicatorPaint = new Paint();
    }
    
    private void setIndicatorPosition(int paramInt1, int paramInt2)
    {
      if ((paramInt1 != this.mIndicatorLeft) || (paramInt2 != this.mIndicatorRight))
      {
        this.mIndicatorLeft = paramInt1;
        this.mIndicatorRight = paramInt2;
        postInvalidateOnAnimation();
      }
    }
    
    private void updateIndicatorPosition()
    {
      View localView = getChildAt(this.mSelectedPosition);
      int i;
      int j;
      if ((localView != null) && (localView.getWidth() > 0))
      {
        int m = localView.getLeft();
        int k = localView.getRight();
        i = m;
        j = k;
        if (this.mSelectionOffset > 0.0F)
        {
          i = m;
          j = k;
          if (this.mSelectedPosition < getChildCount() - 1)
          {
            localView = getChildAt(this.mSelectedPosition + 1);
            i = (int)(this.mSelectionOffset * localView.getLeft() + (1.0F - this.mSelectionOffset) * m);
            j = (int)(this.mSelectionOffset * localView.getRight() + (1.0F - this.mSelectionOffset) * k);
          }
        }
      }
      for (;;)
      {
        setIndicatorPosition(i, j);
        return;
        j = -1;
        i = -1;
      }
    }
    
    void animateIndicatorToPosition(final int paramInt1, int paramInt2)
    {
      final int i;
      Object localObject;
      final int k;
      final int m;
      final int j;
      if (getLayoutDirection() == 1)
      {
        i = 1;
        localObject = getChildAt(paramInt1);
        k = ((View)localObject).getLeft();
        m = ((View)localObject).getRight();
        if (Math.abs(paramInt1 - this.mSelectedPosition) > 1) {
          break label165;
        }
        i = this.mIndicatorLeft;
        j = this.mIndicatorRight;
      }
      for (;;)
      {
        if ((i != k) || (j != m))
        {
          localObject = OPTabLayout.-set0(OPTabLayout.this, new ValueAnimator());
          ((ValueAnimator)localObject).setInterpolator(OPTabLayout.-get0(OPTabLayout.this));
          ((ValueAnimator)localObject).setDuration(paramInt2);
          ((ValueAnimator)localObject).setFloatValues(new float[] { 0.0F, 1.0F });
          ((ValueAnimator)localObject).addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
          {
            public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
            {
              float f = paramAnonymousValueAnimator.getAnimatedFraction();
              OPTabLayout.SlidingTabStrip.-wrap0(OPTabLayout.SlidingTabStrip.this, OPAnimationUtils.lerp(i, k, f), OPAnimationUtils.lerp(j, m, f));
            }
          });
          ((ValueAnimator)localObject).addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationCancel(Animator paramAnonymousAnimator)
            {
              OPTabLayout.SlidingTabStrip.-set0(OPTabLayout.SlidingTabStrip.this, paramInt1);
              OPTabLayout.SlidingTabStrip.-set1(OPTabLayout.SlidingTabStrip.this, 0.0F);
            }
            
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              OPTabLayout.SlidingTabStrip.-set0(OPTabLayout.SlidingTabStrip.this, paramInt1);
              OPTabLayout.SlidingTabStrip.-set1(OPTabLayout.SlidingTabStrip.this, 0.0F);
            }
          });
          ((ValueAnimator)localObject).start();
        }
        return;
        i = 0;
        break;
        label165:
        j = OPTabLayout.-wrap0(OPTabLayout.this, 24);
        if (paramInt1 < this.mSelectedPosition)
        {
          if (i != 0)
          {
            j = k - j;
            i = j;
          }
          else
          {
            j = m + j;
            i = j;
          }
        }
        else if (i != 0)
        {
          j = m + j;
          i = j;
        }
        else
        {
          j = k - j;
          i = j;
        }
      }
    }
    
    boolean childrenNeedLayout()
    {
      int i = 0;
      int j = getChildCount();
      while (i < j)
      {
        if (getChildAt(i).getWidth() <= 0) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    public void draw(Canvas paramCanvas)
    {
      super.draw(paramCanvas);
      if ((this.mIndicatorLeft >= 0) && (this.mIndicatorRight > this.mIndicatorLeft)) {
        paramCanvas.drawRect(this.mIndicatorLeft, getHeight() - this.mSelectedIndicatorHeight, this.mIndicatorRight, getHeight(), this.mSelectedIndicatorPaint);
      }
    }
    
    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
      updateIndicatorPosition();
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      super.onMeasure(paramInt1, paramInt2);
      if (View.MeasureSpec.getMode(paramInt1) != 1073741824) {
        return;
      }
      if ((OPTabLayout.-get1(OPTabLayout.this) == 1) && (OPTabLayout.-get3(OPTabLayout.this) == 1))
      {
        int k = getChildCount();
        int m = View.MeasureSpec.makeMeasureSpec(0, 0);
        int i = 0;
        int j = 0;
        Object localObject;
        while (j < k)
        {
          localObject = getChildAt(j);
          ((View)localObject).measure(m, paramInt2);
          i = Math.max(i, ((View)localObject).getMeasuredWidth());
          j += 1;
        }
        if (i <= 0) {
          return;
        }
        j = OPTabLayout.-wrap0(OPTabLayout.this, 16);
        if (i * k <= getMeasuredWidth() - j * 2)
        {
          j = 0;
          while (j < k)
          {
            localObject = (LinearLayout.LayoutParams)getChildAt(j).getLayoutParams();
            ((LinearLayout.LayoutParams)localObject).width = i;
            ((LinearLayout.LayoutParams)localObject).weight = 0.0F;
            j += 1;
          }
        }
        OPTabLayout.-set1(OPTabLayout.this, 0);
        OPTabLayout.-wrap1(OPTabLayout.this);
        super.onMeasure(paramInt1, paramInt2);
      }
    }
    
    void setIndicatorPositionFromTabPosition(int paramInt, float paramFloat)
    {
      this.mSelectedPosition = paramInt;
      this.mSelectionOffset = paramFloat;
      updateIndicatorPosition();
    }
    
    void setSelectedIndicatorColor(int paramInt)
    {
      if (this.mSelectedIndicatorPaint.getColor() != paramInt)
      {
        this.mSelectedIndicatorPaint.setColor(paramInt);
        postInvalidateOnAnimation();
      }
    }
    
    void setSelectedIndicatorHeight(int paramInt)
    {
      if (this.mSelectedIndicatorHeight != paramInt)
      {
        this.mSelectedIndicatorHeight = paramInt;
        postInvalidateOnAnimation();
      }
    }
  }
  
  public static final class Tab
  {
    public static final int INVALID_POSITION = -1;
    private CharSequence mContentDesc;
    private View mCustomView;
    private Drawable mIcon;
    private final OPTabLayout mParent;
    private int mPosition = -1;
    private Object mTag;
    private CharSequence mText;
    
    Tab(OPTabLayout paramOPTabLayout)
    {
      this.mParent = paramOPTabLayout;
    }
    
    public CharSequence getContentDescription()
    {
      return this.mContentDesc;
    }
    
    public View getCustomView()
    {
      return this.mCustomView;
    }
    
    public Drawable getIcon()
    {
      return this.mIcon;
    }
    
    public int getPosition()
    {
      return this.mPosition;
    }
    
    public Object getTag()
    {
      return this.mTag;
    }
    
    public CharSequence getText()
    {
      return this.mText;
    }
    
    public boolean isSelected()
    {
      return this.mParent.getSelectedTabPosition() == this.mPosition;
    }
    
    public void select()
    {
      this.mParent.selectTab(this);
    }
    
    public Tab setContentDescription(int paramInt)
    {
      return setContentDescription(this.mParent.getResources().getText(paramInt));
    }
    
    public Tab setContentDescription(CharSequence paramCharSequence)
    {
      this.mContentDesc = paramCharSequence;
      if (this.mPosition >= 0) {
        OPTabLayout.-wrap2(this.mParent, this.mPosition);
      }
      return this;
    }
    
    public Tab setCustomView(int paramInt)
    {
      return setCustomView(LayoutInflater.from(this.mParent.getContext()).inflate(paramInt, null));
    }
    
    public Tab setCustomView(View paramView)
    {
      this.mCustomView = paramView;
      if (this.mPosition >= 0) {
        OPTabLayout.-wrap2(this.mParent, this.mPosition);
      }
      return this;
    }
    
    public Tab setIcon(int paramInt)
    {
      return setIcon(this.mParent.getContext().getDrawable(paramInt));
    }
    
    public Tab setIcon(Drawable paramDrawable)
    {
      this.mIcon = paramDrawable;
      if (this.mPosition >= 0) {
        OPTabLayout.-wrap2(this.mParent, this.mPosition);
      }
      return this;
    }
    
    void setPosition(int paramInt)
    {
      this.mPosition = paramInt;
    }
    
    public Tab setTag(Object paramObject)
    {
      this.mTag = paramObject;
      return this;
    }
    
    public Tab setText(int paramInt)
    {
      return setText(this.mParent.getResources().getText(paramInt));
    }
    
    public Tab setText(CharSequence paramCharSequence)
    {
      this.mText = paramCharSequence;
      if (this.mPosition >= 0) {
        OPTabLayout.-wrap2(this.mParent, this.mPosition);
      }
      return this;
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface TabGravity {}
  
  class TabView
    extends LinearLayout
    implements View.OnLongClickListener
  {
    private ImageView mCustomIconView;
    private TextView mCustomTextView;
    private View mCustomView;
    private ImageView mIconView;
    private final OPTabLayout.Tab mTab;
    private TextView mTextView;
    
    public TabView(Context paramContext, OPTabLayout.Tab paramTab)
    {
      super();
      this.mTab = paramTab;
      if (OPTabLayout.-get2(OPTabLayout.this) != 0) {
        setBackgroundDrawable(paramContext.getDrawable(OPTabLayout.-get2(OPTabLayout.this)));
      }
      setPaddingRelative(OPTabLayout.-get8(OPTabLayout.this), OPTabLayout.-get9(OPTabLayout.this), OPTabLayout.-get7(OPTabLayout.this), OPTabLayout.-get6(OPTabLayout.this));
      setGravity(17);
      update();
    }
    
    private void updateTextAndIcon(OPTabLayout.Tab paramTab, TextView paramTextView, ImageView paramImageView)
    {
      Drawable localDrawable = paramTab.getIcon();
      CharSequence localCharSequence = paramTab.getText();
      int i;
      if (paramImageView != null)
      {
        if (localDrawable != null)
        {
          paramImageView.setImageDrawable(localDrawable);
          paramImageView.setVisibility(0);
          setVisibility(0);
          paramImageView.setContentDescription(paramTab.getContentDescription());
        }
      }
      else
      {
        if (!TextUtils.isEmpty(localCharSequence)) {
          break label129;
        }
        i = 0;
        label56:
        if (paramTextView != null)
        {
          if (i == 0) {
            break label135;
          }
          paramTextView.setText(localCharSequence);
          paramTextView.setContentDescription(paramTab.getContentDescription());
          paramTextView.setVisibility(0);
          setVisibility(0);
        }
      }
      for (;;)
      {
        if ((i == 0) && (!TextUtils.isEmpty(paramTab.getContentDescription()))) {
          break label149;
        }
        setOnLongClickListener(null);
        setLongClickable(false);
        return;
        paramImageView.setVisibility(8);
        paramImageView.setImageDrawable(null);
        break;
        label129:
        i = 1;
        break label56;
        label135:
        paramTextView.setVisibility(8);
        paramTextView.setText(null);
      }
      label149:
      setOnLongClickListener(this);
    }
    
    public OPTabLayout.Tab getTab()
    {
      return this.mTab;
    }
    
    @TargetApi(14)
    public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
    {
      super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
      paramAccessibilityEvent.setClassName(ActionBar.Tab.class.getName());
    }
    
    @TargetApi(14)
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
      paramAccessibilityNodeInfo.setClassName(ActionBar.Tab.class.getName());
    }
    
    public boolean onLongClick(View paramView)
    {
      paramView = new int[2];
      getLocationOnScreen(paramView);
      Object localObject = getContext();
      int i = getWidth();
      int j = getHeight();
      int k = ((Context)localObject).getResources().getDisplayMetrics().widthPixels;
      localObject = Toast.makeText((Context)localObject, this.mTab.getContentDescription(), 0);
      ((Toast)localObject).setGravity(49, paramView[0] + i / 2 - k / 2, j);
      ((Toast)localObject).show();
      return true;
    }
    
    public void onMeasure(int paramInt1, int paramInt2)
    {
      super.onMeasure(paramInt1, paramInt2);
      paramInt1 = getMeasuredWidth();
      if ((paramInt1 < OPTabLayout.-get5(OPTabLayout.this)) || (paramInt1 > OPTabLayout.-get4(OPTabLayout.this))) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(OPMathUtils.constrain(paramInt1, OPTabLayout.-get5(OPTabLayout.this), OPTabLayout.-get4(OPTabLayout.this)), 1073741824), paramInt2);
      }
    }
    
    public void setSelected(boolean paramBoolean)
    {
      if (isSelected() != paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        super.setSelected(paramBoolean);
        if ((i != 0) && (paramBoolean))
        {
          sendAccessibilityEvent(4);
          if (this.mTextView != null) {
            this.mTextView.setSelected(paramBoolean);
          }
          if (this.mIconView != null) {
            this.mIconView.setSelected(paramBoolean);
          }
        }
        return;
      }
    }
    
    final void update()
    {
      OPTabLayout.Tab localTab = this.mTab;
      Object localObject = localTab.getCustomView();
      if (localObject != null)
      {
        ViewParent localViewParent = ((View)localObject).getParent();
        if (localViewParent != this)
        {
          if (localViewParent != null) {
            ((ViewGroup)localViewParent).removeView((View)localObject);
          }
          addView((View)localObject);
        }
        this.mCustomView = ((View)localObject);
        if (this.mTextView != null) {
          this.mTextView.setVisibility(8);
        }
        if (this.mIconView != null)
        {
          this.mIconView.setVisibility(8);
          this.mIconView.setImageDrawable(null);
        }
        this.mCustomTextView = ((TextView)((View)localObject).findViewById(16908308));
        this.mCustomIconView = ((ImageView)((View)localObject).findViewById(16908294));
        if (this.mCustomView != null) {
          break label283;
        }
        if (this.mIconView == null)
        {
          localObject = (ImageView)LayoutInflater.from(getContext()).inflate(R.layout.op_layout_tab_icon, this, false);
          addView((View)localObject, 0);
          this.mIconView = ((ImageView)localObject);
        }
        if (this.mTextView == null)
        {
          localObject = (TextView)LayoutInflater.from(getContext()).inflate(R.layout.op_layout_tab_text, this, false);
          addView((View)localObject);
          this.mTextView = ((TextView)localObject);
        }
        this.mTextView.setTextAppearance(getContext(), OPTabLayout.-get10(OPTabLayout.this));
        if (OPTabLayout.-get11(OPTabLayout.this) != null) {
          this.mTextView.setTextColor(OPTabLayout.-get11(OPTabLayout.this));
        }
        updateTextAndIcon(localTab, this.mTextView, this.mIconView);
      }
      label283:
      while ((this.mCustomTextView == null) && (this.mCustomIconView == null))
      {
        return;
        if (this.mCustomView != null)
        {
          removeView(this.mCustomView);
          this.mCustomView = null;
        }
        this.mCustomTextView = null;
        this.mCustomIconView = null;
        break;
      }
      updateTextAndIcon(localTab, this.mCustomTextView, this.mCustomIconView);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\OPTabLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */