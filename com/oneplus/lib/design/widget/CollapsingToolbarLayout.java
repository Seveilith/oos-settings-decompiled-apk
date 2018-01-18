package com.oneplus.lib.design.widget;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toolbar;
import com.oneplus.commonctrl.R.id;
import com.oneplus.commonctrl.R.style;
import com.oneplus.commonctrl.R.styleable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CollapsingToolbarLayout
  extends FrameLayout
{
  private static final int DEFAULT_SCRIM_ANIMATION_DURATION = 600;
  final CollapsingTextHelper mCollapsingTextHelper = new CollapsingTextHelper(this);
  private boolean mCollapsingTitleEnabled;
  private Drawable mContentScrim;
  int mCurrentOffset;
  private boolean mDrawCollapsingTitle;
  private View mDummyView;
  private int mExpandedMarginBottom;
  private int mExpandedMarginEnd;
  private int mExpandedMarginStart;
  private int mExpandedMarginTop;
  WindowInsetsCompat mLastInsets;
  private AppBarLayout.OnOffsetChangedListener mOnOffsetChangedListener;
  private boolean mRefreshToolbar = true;
  private int mScrimAlpha;
  private long mScrimAnimationDuration;
  private ValueAnimator mScrimAnimator;
  private int mScrimVisibleHeightTrigger = -1;
  private boolean mScrimsAreShown;
  Drawable mStatusBarScrim;
  private final Rect mTmpRect = new Rect();
  private Toolbar mToolbar;
  private View mToolbarDirectChild;
  private int mToolbarDrawIndex;
  private int mToolbarId;
  
  public CollapsingToolbarLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public CollapsingToolbarLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public CollapsingToolbarLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.mCollapsingTextHelper.setTextSizeInterpolator(new DecelerateInterpolator());
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.OpCollapsingToolbarLayout, paramInt, R.style.Widget_Design_CollapsingToolbar);
    this.mCollapsingTextHelper.setExpandedTextGravity(paramContext.getInt(R.styleable.OpCollapsingToolbarLayout_opExpandedTitleGravity, 8388691));
    this.mCollapsingTextHelper.setCollapsedTextGravity(paramContext.getInt(R.styleable.OpCollapsingToolbarLayout_opCollapsedTitleGravity, 8388627));
    paramInt = paramContext.getDimensionPixelSize(R.styleable.OpCollapsingToolbarLayout_opExpandedTitleMargin, 0);
    this.mExpandedMarginBottom = paramInt;
    this.mExpandedMarginEnd = paramInt;
    this.mExpandedMarginTop = paramInt;
    this.mExpandedMarginStart = paramInt;
    if (paramContext.hasValue(R.styleable.OpCollapsingToolbarLayout_opExpandedTitleMarginStart)) {
      this.mExpandedMarginStart = paramContext.getDimensionPixelSize(R.styleable.OpCollapsingToolbarLayout_opExpandedTitleMarginStart, 0);
    }
    if (paramContext.hasValue(R.styleable.OpCollapsingToolbarLayout_opExpandedTitleMarginEnd)) {
      this.mExpandedMarginEnd = paramContext.getDimensionPixelSize(R.styleable.OpCollapsingToolbarLayout_opExpandedTitleMarginEnd, 0);
    }
    if (paramContext.hasValue(R.styleable.OpCollapsingToolbarLayout_opExpandedTitleMarginTop)) {
      this.mExpandedMarginTop = paramContext.getDimensionPixelSize(R.styleable.OpCollapsingToolbarLayout_opExpandedTitleMarginTop, 0);
    }
    if (paramContext.hasValue(R.styleable.OpCollapsingToolbarLayout_opExpandedTitleMarginBottom)) {
      this.mExpandedMarginBottom = paramContext.getDimensionPixelSize(R.styleable.OpCollapsingToolbarLayout_opExpandedTitleMarginBottom, 0);
    }
    this.mCollapsingTitleEnabled = paramContext.getBoolean(R.styleable.OpCollapsingToolbarLayout_opTitleEnabled, true);
    setTitle(paramContext.getText(R.styleable.OpCollapsingToolbarLayout_android_title));
    this.mCollapsingTextHelper.setExpandedTextAppearance(R.style.OPTextAppearance_Design_CollapsingToolbar_Expanded);
    this.mCollapsingTextHelper.setCollapsedTextAppearance(R.style.TextAppearance_Widget_ActionBar_Title);
    if (paramContext.hasValue(R.styleable.OpCollapsingToolbarLayout_opExpandedTitleTextAppearance)) {
      this.mCollapsingTextHelper.setExpandedTextAppearance(paramContext.getResourceId(R.styleable.OpCollapsingToolbarLayout_opExpandedTitleTextAppearance, 0));
    }
    if (paramContext.hasValue(R.styleable.OpCollapsingToolbarLayout_opCollapsedTitleTextAppearance)) {
      this.mCollapsingTextHelper.setCollapsedTextAppearance(paramContext.getResourceId(R.styleable.OpCollapsingToolbarLayout_opCollapsedTitleTextAppearance, 0));
    }
    this.mScrimVisibleHeightTrigger = paramContext.getDimensionPixelSize(R.styleable.OpCollapsingToolbarLayout_opScrimVisibleHeightTrigger, -1);
    this.mScrimAnimationDuration = paramContext.getInt(R.styleable.OpCollapsingToolbarLayout_opScrimAnimationDuration, 600);
    setContentScrim(paramContext.getDrawable(R.styleable.OpCollapsingToolbarLayout_opContentScrim));
    setStatusBarScrim(paramContext.getDrawable(R.styleable.OpCollapsingToolbarLayout_opStatusBarScrim));
    this.mToolbarId = paramContext.getResourceId(R.styleable.OpCollapsingToolbarLayout_opToolbarId, -1);
    paramContext.recycle();
    setWillNotDraw(false);
    ViewCompat.setOnApplyWindowInsetsListener(this, new OnApplyWindowInsetsListener()
    {
      public WindowInsetsCompat onApplyWindowInsets(View paramAnonymousView, WindowInsetsCompat paramAnonymousWindowInsetsCompat)
      {
        return CollapsingToolbarLayout.this.onWindowInsetChanged(paramAnonymousWindowInsetsCompat);
      }
    });
  }
  
  private void animateScrim(int paramInt)
  {
    ensureToolbar();
    Object localObject;
    if (this.mScrimAnimator == null)
    {
      this.mScrimAnimator = new ValueAnimator();
      this.mScrimAnimator.setDuration(this.mScrimAnimationDuration);
      ValueAnimator localValueAnimator = this.mScrimAnimator;
      if (paramInt > this.mScrimAlpha)
      {
        localObject = new FastOutLinearInInterpolator();
        localValueAnimator.setInterpolator((TimeInterpolator)localObject);
        this.mScrimAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
          public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
          {
            CollapsingToolbarLayout.this.setScrimAlpha(((Integer)paramAnonymousValueAnimator.getAnimatedValue()).intValue());
          }
        });
      }
    }
    for (;;)
    {
      this.mScrimAnimator.setIntValues(new int[] { this.mScrimAlpha, paramInt });
      this.mScrimAnimator.start();
      return;
      localObject = new LinearOutSlowInInterpolator();
      break;
      if (this.mScrimAnimator.isRunning()) {
        this.mScrimAnimator.cancel();
      }
    }
  }
  
  private void ensureToolbar()
  {
    if (!this.mRefreshToolbar) {
      return;
    }
    this.mToolbar = null;
    this.mToolbarDirectChild = null;
    if (this.mToolbarId != -1)
    {
      this.mToolbar = ((Toolbar)findViewById(this.mToolbarId));
      if (this.mToolbar != null) {
        this.mToolbarDirectChild = findDirectChild(this.mToolbar);
      }
    }
    Object localObject2;
    int i;
    int j;
    if (this.mToolbar == null)
    {
      localObject2 = null;
      i = 0;
      j = getChildCount();
    }
    for (;;)
    {
      Object localObject1 = localObject2;
      if (i < j)
      {
        localObject1 = getChildAt(i);
        if ((localObject1 instanceof Toolbar)) {
          localObject1 = (Toolbar)localObject1;
        }
      }
      else
      {
        this.mToolbar = ((Toolbar)localObject1);
        updateDummyView();
        this.mRefreshToolbar = false;
        return;
      }
      i += 1;
    }
  }
  
  private View findDirectChild(View paramView)
  {
    View localView = paramView;
    for (paramView = paramView.getParent(); (paramView != this) && (paramView != null); paramView = paramView.getParent()) {
      if ((paramView instanceof View)) {
        localView = (View)paramView;
      }
    }
    return localView;
  }
  
  private static int getHeightWithMargins(@NonNull View paramView)
  {
    Object localObject = paramView.getLayoutParams();
    if ((localObject instanceof ViewGroup.MarginLayoutParams))
    {
      localObject = (ViewGroup.MarginLayoutParams)localObject;
      return paramView.getHeight() + ((ViewGroup.MarginLayoutParams)localObject).topMargin + ((ViewGroup.MarginLayoutParams)localObject).bottomMargin;
    }
    return paramView.getHeight();
  }
  
  static ViewOffsetHelper getViewOffsetHelper(View paramView)
  {
    ViewOffsetHelper localViewOffsetHelper2 = (ViewOffsetHelper)paramView.getTag(R.id.view_offset_helper);
    ViewOffsetHelper localViewOffsetHelper1 = localViewOffsetHelper2;
    if (localViewOffsetHelper2 == null)
    {
      localViewOffsetHelper1 = new ViewOffsetHelper(paramView);
      paramView.setTag(R.id.view_offset_helper, localViewOffsetHelper1);
    }
    return localViewOffsetHelper1;
  }
  
  private boolean isToolbarChildDrawnNext(View paramView)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mToolbarDrawIndex >= 0)
    {
      bool1 = bool2;
      if (this.mToolbarDrawIndex == indexOfChild(paramView) + 1) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private void updateDummyView()
  {
    if ((!this.mCollapsingTitleEnabled) && (this.mDummyView != null))
    {
      ViewParent localViewParent = this.mDummyView.getParent();
      if ((localViewParent instanceof ViewGroup)) {
        ((ViewGroup)localViewParent).removeView(this.mDummyView);
      }
    }
    if ((this.mCollapsingTitleEnabled) && (this.mToolbar != null))
    {
      if (this.mDummyView == null) {
        this.mDummyView = new View(getContext());
      }
      if (this.mDummyView.getParent() == null) {
        this.mToolbar.addView(this.mDummyView, -1, -1);
      }
    }
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    ensureToolbar();
    if ((this.mToolbar == null) && (this.mContentScrim != null) && (this.mScrimAlpha > 0))
    {
      this.mContentScrim.mutate().setAlpha(this.mScrimAlpha);
      this.mContentScrim.draw(paramCanvas);
    }
    if ((this.mCollapsingTitleEnabled) && (this.mDrawCollapsingTitle)) {
      this.mCollapsingTextHelper.draw(paramCanvas);
    }
    if ((this.mStatusBarScrim != null) && (this.mScrimAlpha > 0)) {
      if (this.mLastInsets == null) {
        break label153;
      }
    }
    label153:
    for (int i = this.mLastInsets.getSystemWindowInsetTop();; i = 0)
    {
      if (i > 0)
      {
        this.mStatusBarScrim.setBounds(0, -this.mCurrentOffset, getWidth(), i - this.mCurrentOffset);
        this.mStatusBarScrim.mutate().setAlpha(this.mScrimAlpha);
        this.mStatusBarScrim.draw(paramCanvas);
      }
      return;
    }
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    boolean bool2 = super.drawChild(paramCanvas, paramView, paramLong);
    boolean bool1 = bool2;
    if (this.mContentScrim != null)
    {
      bool1 = bool2;
      if (this.mScrimAlpha > 0)
      {
        bool1 = bool2;
        if (isToolbarChildDrawnNext(paramView))
        {
          this.mContentScrim.mutate().setAlpha(this.mScrimAlpha);
          this.mContentScrim.draw(paramCanvas);
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    int[] arrayOfInt = getDrawableState();
    boolean bool2 = false;
    Drawable localDrawable = this.mStatusBarScrim;
    boolean bool1 = bool2;
    if (localDrawable != null)
    {
      bool1 = bool2;
      if (localDrawable.isStateful()) {
        bool1 = localDrawable.setState(arrayOfInt);
      }
    }
    localDrawable = this.mContentScrim;
    bool2 = bool1;
    if (localDrawable != null)
    {
      bool2 = bool1;
      if (localDrawable.isStateful()) {
        bool2 = bool1 | localDrawable.setState(arrayOfInt);
      }
    }
    bool1 = bool2;
    if (this.mCollapsingTextHelper != null) {
      bool1 = bool2 | this.mCollapsingTextHelper.setState(arrayOfInt);
    }
    if (bool1) {
      invalidate();
    }
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-1, -1);
  }
  
  public FrameLayout.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected FrameLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return new LayoutParams(paramLayoutParams);
  }
  
  public int getCollapsedTitleGravity()
  {
    return this.mCollapsingTextHelper.getCollapsedTextGravity();
  }
  
  @NonNull
  public Typeface getCollapsedTitleTypeface()
  {
    return this.mCollapsingTextHelper.getCollapsedTypeface();
  }
  
  @Nullable
  public Drawable getContentScrim()
  {
    return this.mContentScrim;
  }
  
  public int getExpandedTitleGravity()
  {
    return this.mCollapsingTextHelper.getExpandedTextGravity();
  }
  
  public int getExpandedTitleMarginBottom()
  {
    return this.mExpandedMarginBottom;
  }
  
  public int getExpandedTitleMarginEnd()
  {
    return this.mExpandedMarginEnd;
  }
  
  public int getExpandedTitleMarginStart()
  {
    return this.mExpandedMarginStart;
  }
  
  public int getExpandedTitleMarginTop()
  {
    return this.mExpandedMarginTop;
  }
  
  @NonNull
  public Typeface getExpandedTitleTypeface()
  {
    return this.mCollapsingTextHelper.getExpandedTypeface();
  }
  
  final int getMaxOffsetForPinChild(View paramView)
  {
    ViewOffsetHelper localViewOffsetHelper = getViewOffsetHelper(paramView);
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    return getHeight() - localViewOffsetHelper.getLayoutTop() - paramView.getHeight() - localLayoutParams.bottomMargin;
  }
  
  public long getScrimAnimationDuration()
  {
    return this.mScrimAnimationDuration;
  }
  
  public int getScrimVisibleHeightTrigger()
  {
    if (this.mScrimVisibleHeightTrigger >= 0) {
      return this.mScrimVisibleHeightTrigger;
    }
    if (this.mLastInsets != null) {}
    for (int i = this.mLastInsets.getSystemWindowInsetTop();; i = 0)
    {
      int j = ViewCompat.getMinimumHeight(this);
      if (j <= 0) {
        break;
      }
      return Math.min(j * 2 + i, getHeight());
    }
    return getHeight() / 3;
  }
  
  @Nullable
  public Drawable getStatusBarScrim()
  {
    return this.mStatusBarScrim;
  }
  
  @Nullable
  public CharSequence getTitle()
  {
    if (this.mCollapsingTitleEnabled) {
      return this.mCollapsingTextHelper.getText();
    }
    return null;
  }
  
  public boolean isTitleEnabled()
  {
    return this.mCollapsingTitleEnabled;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    ViewParent localViewParent = getParent();
    if ((localViewParent instanceof AppBarLayout))
    {
      ViewCompat.setFitsSystemWindows(this, ViewCompat.getFitsSystemWindows((View)localViewParent));
      if (this.mOnOffsetChangedListener == null) {
        this.mOnOffsetChangedListener = new OffsetUpdateListener();
      }
      ((AppBarLayout)localViewParent).addOnOffsetChangedListener(this.mOnOffsetChangedListener);
      ViewCompat.requestApplyInsets(this);
    }
  }
  
  protected void onDetachedFromWindow()
  {
    ViewParent localViewParent = getParent();
    if ((this.mOnOffsetChangedListener != null) && ((localViewParent instanceof AppBarLayout))) {
      ((AppBarLayout)localViewParent).removeOnOffsetChangedListener(this.mOnOffsetChangedListener);
    }
    super.onDetachedFromWindow();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    int j;
    int k;
    Object localObject;
    if (this.mLastInsets != null)
    {
      j = this.mLastInsets.getSystemWindowInsetTop();
      i = 0;
      k = getChildCount();
      while (i < k)
      {
        localObject = getChildAt(i);
        if ((!ViewCompat.getFitsSystemWindows((View)localObject)) && (((View)localObject).getTop() < j)) {
          ViewCompat.offsetTopAndBottom((View)localObject, j);
        }
        i += 1;
      }
    }
    label144:
    label157:
    int m;
    if ((this.mCollapsingTitleEnabled) && (this.mDummyView != null))
    {
      if (!ViewCompat.isAttachedToWindow(this.mDummyView)) {
        break label401;
      }
      if (this.mDummyView.getVisibility() != 0) {
        break label396;
      }
      paramBoolean = true;
      this.mDrawCollapsingTitle = paramBoolean;
      if (this.mDrawCollapsingTitle)
      {
        if (ViewCompat.getLayoutDirection(this) != 1) {
          break label406;
        }
        i = 1;
        if (this.mToolbarDirectChild == null) {
          break label412;
        }
        localObject = this.mToolbarDirectChild;
        m = getMaxOffsetForPinChild((View)localObject);
        Utils.getDescendantRect(this, this.mDummyView, this.mTmpRect);
        localObject = this.mCollapsingTextHelper;
        int n = this.mTmpRect.left;
        if (i == 0) {
          break label421;
        }
        j = Utils.getTitleMarginEnd(this.mToolbar);
        label206:
        int i1 = this.mTmpRect.top;
        int i2 = Utils.getTitleMarginTop(this.mToolbar);
        int i3 = this.mTmpRect.right;
        if (i == 0) {
          break label433;
        }
        k = Utils.getTitleMarginStart(this.mToolbar);
        label247:
        ((CollapsingTextHelper)localObject).setCollapsedBounds(n + j, i2 + (i1 + m), k + i3, this.mTmpRect.bottom + m - Utils.getTitleMarginBottom(this.mToolbar));
        localObject = this.mCollapsingTextHelper;
        if (i == 0) {
          break label445;
        }
        j = this.mExpandedMarginEnd;
        label305:
        k = this.mTmpRect.top;
        m = this.mExpandedMarginTop;
        if (i == 0) {
          break label454;
        }
      }
    }
    label396:
    label401:
    label406:
    label412:
    label421:
    label433:
    label445:
    label454:
    for (int i = this.mExpandedMarginStart;; i = this.mExpandedMarginEnd)
    {
      ((CollapsingTextHelper)localObject).setExpandedBounds(j, m + k, paramInt3 - paramInt1 - i, paramInt4 - paramInt2 - this.mExpandedMarginBottom);
      this.mCollapsingTextHelper.recalculate();
      paramInt1 = 0;
      paramInt2 = getChildCount();
      while (paramInt1 < paramInt2)
      {
        getViewOffsetHelper(getChildAt(paramInt1)).onViewLayout();
        paramInt1 += 1;
      }
      paramBoolean = false;
      break;
      paramBoolean = false;
      break;
      i = 0;
      break label144;
      localObject = this.mToolbar;
      break label157;
      j = Utils.getTitleMarginStart(this.mToolbar);
      break label206;
      k = Utils.getTitleMarginEnd(this.mToolbar);
      break label247;
      j = this.mExpandedMarginStart;
      break label305;
    }
    if (this.mToolbar != null)
    {
      if ((this.mCollapsingTitleEnabled) && (TextUtils.isEmpty(this.mCollapsingTextHelper.getText()))) {
        this.mCollapsingTextHelper.setText(this.mToolbar.getTitle());
      }
      if ((this.mToolbarDirectChild == null) || (this.mToolbarDirectChild == this))
      {
        setMinimumHeight(getHeightWithMargins(this.mToolbar));
        this.mToolbarDrawIndex = indexOfChild(this.mToolbar);
      }
    }
    for (;;)
    {
      updateScrimVisibility();
      return;
      setMinimumHeight(getHeightWithMargins(this.mToolbarDirectChild));
      this.mToolbarDrawIndex = indexOfChild(this.mToolbarDirectChild);
      continue;
      this.mToolbarDrawIndex = -1;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    ensureToolbar();
    super.onMeasure(paramInt1, paramInt2);
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (this.mContentScrim != null) {
      this.mContentScrim.setBounds(0, 0, paramInt1, paramInt2);
    }
  }
  
  WindowInsetsCompat onWindowInsetChanged(WindowInsetsCompat paramWindowInsetsCompat)
  {
    WindowInsetsCompat localWindowInsetsCompat = null;
    if (ViewCompat.getFitsSystemWindows(this)) {
      localWindowInsetsCompat = paramWindowInsetsCompat;
    }
    if (!Utils.objectEquals(this.mLastInsets, localWindowInsetsCompat))
    {
      this.mLastInsets = localWindowInsetsCompat;
      requestLayout();
    }
    return paramWindowInsetsCompat.consumeSystemWindowInsets();
  }
  
  public void setCollapsedTitleGravity(int paramInt)
  {
    this.mCollapsingTextHelper.setCollapsedTextGravity(paramInt);
  }
  
  public void setCollapsedTitleTextAppearance(@StyleRes int paramInt)
  {
    this.mCollapsingTextHelper.setCollapsedTextAppearance(paramInt);
  }
  
  public void setCollapsedTitleTextColor(@ColorInt int paramInt)
  {
    setCollapsedTitleTextColor(ColorStateList.valueOf(paramInt));
  }
  
  public void setCollapsedTitleTextColor(@NonNull ColorStateList paramColorStateList)
  {
    this.mCollapsingTextHelper.setCollapsedTextColor(paramColorStateList);
  }
  
  public void setCollapsedTitleTypeface(@Nullable Typeface paramTypeface)
  {
    this.mCollapsingTextHelper.setCollapsedTypeface(paramTypeface);
  }
  
  public void setContentScrim(@Nullable Drawable paramDrawable)
  {
    Drawable localDrawable = null;
    if (this.mContentScrim != paramDrawable)
    {
      if (this.mContentScrim != null) {
        this.mContentScrim.setCallback(null);
      }
      if (paramDrawable != null) {
        localDrawable = paramDrawable.mutate();
      }
      this.mContentScrim = localDrawable;
      if (this.mContentScrim != null)
      {
        this.mContentScrim.setBounds(0, 0, getWidth(), getHeight());
        this.mContentScrim.setCallback(this);
        this.mContentScrim.setAlpha(this.mScrimAlpha);
      }
      ViewCompat.postInvalidateOnAnimation(this);
    }
  }
  
  public void setContentScrimColor(@ColorInt int paramInt)
  {
    setContentScrim(new ColorDrawable(paramInt));
  }
  
  public void setContentScrimResource(@DrawableRes int paramInt)
  {
    setContentScrim(ContextCompat.getDrawable(getContext(), paramInt));
  }
  
  public void setExpandedTitleColor(@ColorInt int paramInt)
  {
    setExpandedTitleTextColor(ColorStateList.valueOf(paramInt));
  }
  
  public void setExpandedTitleGravity(int paramInt)
  {
    this.mCollapsingTextHelper.setExpandedTextGravity(paramInt);
  }
  
  public void setExpandedTitleMargin(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mExpandedMarginStart = paramInt1;
    this.mExpandedMarginTop = paramInt2;
    this.mExpandedMarginEnd = paramInt3;
    this.mExpandedMarginBottom = paramInt4;
    requestLayout();
  }
  
  public void setExpandedTitleMarginBottom(int paramInt)
  {
    this.mExpandedMarginBottom = paramInt;
    requestLayout();
  }
  
  public void setExpandedTitleMarginEnd(int paramInt)
  {
    this.mExpandedMarginEnd = paramInt;
    requestLayout();
  }
  
  public void setExpandedTitleMarginStart(int paramInt)
  {
    this.mExpandedMarginStart = paramInt;
    requestLayout();
  }
  
  public void setExpandedTitleMarginTop(int paramInt)
  {
    this.mExpandedMarginTop = paramInt;
    requestLayout();
  }
  
  public void setExpandedTitleTextAppearance(@StyleRes int paramInt)
  {
    this.mCollapsingTextHelper.setExpandedTextAppearance(paramInt);
  }
  
  public void setExpandedTitleTextColor(@NonNull ColorStateList paramColorStateList)
  {
    this.mCollapsingTextHelper.setExpandedTextColor(paramColorStateList);
  }
  
  public void setExpandedTitleTypeface(@Nullable Typeface paramTypeface)
  {
    this.mCollapsingTextHelper.setExpandedTypeface(paramTypeface);
  }
  
  void setScrimAlpha(int paramInt)
  {
    if (paramInt != this.mScrimAlpha)
    {
      if ((this.mContentScrim != null) && (this.mToolbar != null)) {
        ViewCompat.postInvalidateOnAnimation(this.mToolbar);
      }
      this.mScrimAlpha = paramInt;
      ViewCompat.postInvalidateOnAnimation(this);
    }
  }
  
  public void setScrimAnimationDuration(@IntRange(from=0L) long paramLong)
  {
    this.mScrimAnimationDuration = paramLong;
  }
  
  public void setScrimVisibleHeightTrigger(@IntRange(from=0L) int paramInt)
  {
    if (this.mScrimVisibleHeightTrigger != paramInt)
    {
      this.mScrimVisibleHeightTrigger = paramInt;
      updateScrimVisibility();
    }
  }
  
  public void setScrimsShown(boolean paramBoolean)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (ViewCompat.isLaidOut(this)) {
      if (!isInEditMode()) {
        break label27;
      }
    }
    label27:
    for (bool1 = bool2;; bool1 = true)
    {
      setScrimsShown(paramBoolean, bool1);
      return;
    }
  }
  
  public void setScrimsShown(boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = 255;
    if (this.mScrimsAreShown != paramBoolean1)
    {
      if (!paramBoolean2) {
        break label36;
      }
      if (!paramBoolean1) {
        break label31;
      }
    }
    for (;;)
    {
      animateScrim(i);
      this.mScrimsAreShown = paramBoolean1;
      return;
      label31:
      i = 0;
    }
    label36:
    if (paramBoolean1) {}
    for (;;)
    {
      setScrimAlpha(i);
      break;
      i = 0;
    }
  }
  
  public void setStatusBarScrim(@Nullable Drawable paramDrawable)
  {
    Drawable localDrawable = null;
    if (this.mStatusBarScrim != paramDrawable)
    {
      if (this.mStatusBarScrim != null) {
        this.mStatusBarScrim.setCallback(null);
      }
      if (paramDrawable != null) {
        localDrawable = paramDrawable.mutate();
      }
      this.mStatusBarScrim = localDrawable;
      if (this.mStatusBarScrim != null)
      {
        if (this.mStatusBarScrim.isStateful()) {
          this.mStatusBarScrim.setState(getDrawableState());
        }
        DrawableCompat.setLayoutDirection(this.mStatusBarScrim, ViewCompat.getLayoutDirection(this));
        paramDrawable = this.mStatusBarScrim;
        if (getVisibility() != 0) {
          break label125;
        }
      }
    }
    label125:
    for (boolean bool = true;; bool = false)
    {
      paramDrawable.setVisible(bool, false);
      this.mStatusBarScrim.setCallback(this);
      this.mStatusBarScrim.setAlpha(this.mScrimAlpha);
      ViewCompat.postInvalidateOnAnimation(this);
      return;
    }
  }
  
  public void setStatusBarScrimColor(@ColorInt int paramInt)
  {
    setStatusBarScrim(new ColorDrawable(paramInt));
  }
  
  public void setStatusBarScrimResource(@DrawableRes int paramInt)
  {
    setStatusBarScrim(ContextCompat.getDrawable(getContext(), paramInt));
  }
  
  public void setTitle(@Nullable CharSequence paramCharSequence)
  {
    this.mCollapsingTextHelper.setText(paramCharSequence);
  }
  
  public void setTitleEnabled(boolean paramBoolean)
  {
    if (paramBoolean != this.mCollapsingTitleEnabled)
    {
      this.mCollapsingTitleEnabled = paramBoolean;
      updateDummyView();
      requestLayout();
    }
  }
  
  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    if (paramInt == 0) {}
    for (boolean bool = true;; bool = false)
    {
      if ((this.mStatusBarScrim != null) && (this.mStatusBarScrim.isVisible() != bool)) {
        this.mStatusBarScrim.setVisible(bool, false);
      }
      if ((this.mContentScrim != null) && (this.mContentScrim.isVisible() != bool)) {
        this.mContentScrim.setVisible(bool, false);
      }
      return;
    }
  }
  
  final void updateScrimVisibility()
  {
    if ((this.mContentScrim != null) || (this.mStatusBarScrim != null)) {
      if (getHeight() + this.mCurrentOffset >= getScrimVisibleHeightTrigger()) {
        break label38;
      }
    }
    label38:
    for (boolean bool = true;; bool = false)
    {
      setScrimsShown(bool);
      return;
    }
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    if ((super.verifyDrawable(paramDrawable)) || (paramDrawable == this.mContentScrim)) {}
    while (paramDrawable == this.mStatusBarScrim) {
      return true;
    }
    return false;
  }
  
  public static class LayoutParams
    extends FrameLayout.LayoutParams
  {
    public static final int COLLAPSE_MODE_OFF = 0;
    public static final int COLLAPSE_MODE_PARALLAX = 2;
    public static final int COLLAPSE_MODE_PIN = 1;
    private static final float DEFAULT_PARALLAX_MULTIPLIER = 0.5F;
    int mCollapseMode = 0;
    float mParallaxMult = 0.5F;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(int paramInt1, int paramInt2, int paramInt3)
    {
      super(paramInt2, paramInt3);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.OpCollapsingToolbarLayout_Layout);
      this.mCollapseMode = paramContext.getInt(R.styleable.OpCollapsingToolbarLayout_Layout_op_layout_collapseMode, 0);
      setParallaxMultiplier(paramContext.getFloat(R.styleable.OpCollapsingToolbarLayout_Layout_op_layout_collapseParallaxMultiplier, 0.5F));
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
    
    public LayoutParams(FrameLayout.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public int getCollapseMode()
    {
      return this.mCollapseMode;
    }
    
    public float getParallaxMultiplier()
    {
      return this.mParallaxMult;
    }
    
    public void setCollapseMode(int paramInt)
    {
      this.mCollapseMode = paramInt;
    }
    
    public void setParallaxMultiplier(float paramFloat)
    {
      this.mParallaxMult = paramFloat;
    }
    
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
    static @interface CollapseMode {}
  }
  
  private class OffsetUpdateListener
    implements AppBarLayout.OnOffsetChangedListener
  {
    OffsetUpdateListener() {}
    
    public void onOffsetChanged(AppBarLayout paramAppBarLayout, int paramInt)
    {
      CollapsingToolbarLayout.this.mCurrentOffset = paramInt;
      int i;
      label41:
      CollapsingToolbarLayout.LayoutParams localLayoutParams;
      ViewOffsetHelper localViewOffsetHelper;
      if (CollapsingToolbarLayout.this.mLastInsets != null)
      {
        i = CollapsingToolbarLayout.this.mLastInsets.getSystemWindowInsetTop();
        j = 0;
        k = CollapsingToolbarLayout.this.getChildCount();
        if (j >= k) {
          break label158;
        }
        paramAppBarLayout = CollapsingToolbarLayout.this.getChildAt(j);
        localLayoutParams = (CollapsingToolbarLayout.LayoutParams)paramAppBarLayout.getLayoutParams();
        localViewOffsetHelper = CollapsingToolbarLayout.getViewOffsetHelper(paramAppBarLayout);
        switch (localLayoutParams.mCollapseMode)
        {
        }
      }
      for (;;)
      {
        j += 1;
        break label41;
        i = 0;
        break;
        localViewOffsetHelper.setTopAndBottomOffset(Utils.constrain(-paramInt, 0, CollapsingToolbarLayout.this.getMaxOffsetForPinChild(paramAppBarLayout)));
        continue;
        localViewOffsetHelper.setTopAndBottomOffset(Math.round(-paramInt * localLayoutParams.mParallaxMult));
      }
      label158:
      CollapsingToolbarLayout.this.updateScrimVisibility();
      if ((CollapsingToolbarLayout.this.mStatusBarScrim != null) && (i > 0)) {
        ViewCompat.postInvalidateOnAnimation(CollapsingToolbarLayout.this);
      }
      int j = CollapsingToolbarLayout.this.getHeight();
      int k = ViewCompat.getMinimumHeight(CollapsingToolbarLayout.this);
      CollapsingToolbarLayout.this.mCollapsingTextHelper.setExpansionFraction(Math.abs(paramInt) / (j - k - i));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\design\widget\CollapsingToolbarLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */