package android.support.v4.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class SlidingPaneLayout
  extends ViewGroup
{
  private static final int DEFAULT_FADE_COLOR = -858993460;
  private static final int DEFAULT_OVERHANG_SIZE = 32;
  static final SlidingPanelLayoutImpl IMPL = new SlidingPanelLayoutImplBase();
  private static final int MIN_FLING_VELOCITY = 400;
  private static final String TAG = "SlidingPaneLayout";
  private boolean mCanSlide;
  private int mCoveredFadeColor;
  final ViewDragHelper mDragHelper;
  private boolean mFirstLayout = true;
  private float mInitialMotionX;
  private float mInitialMotionY;
  boolean mIsUnableToDrag;
  private final int mOverhangSize;
  private PanelSlideListener mPanelSlideListener;
  private int mParallaxBy;
  private float mParallaxOffset;
  final ArrayList<DisableLayerRunnable> mPostedRunnables = new ArrayList();
  boolean mPreservedOpenState;
  private Drawable mShadowDrawableLeft;
  private Drawable mShadowDrawableRight;
  float mSlideOffset;
  int mSlideRange;
  View mSlideableView;
  private int mSliderFadeColor = -858993460;
  private final Rect mTmpRect = new Rect();
  
  static
  {
    int i = Build.VERSION.SDK_INT;
    if (i >= 17)
    {
      IMPL = new SlidingPanelLayoutImplJBMR1();
      return;
    }
    if (i >= 16)
    {
      IMPL = new SlidingPanelLayoutImplJB();
      return;
    }
  }
  
  public SlidingPaneLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SlidingPaneLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public SlidingPaneLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    float f = paramContext.getResources().getDisplayMetrics().density;
    this.mOverhangSize = ((int)(32.0F * f + 0.5F));
    ViewConfiguration.get(paramContext);
    setWillNotDraw(false);
    ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegate());
    ViewCompat.setImportantForAccessibility(this, 1);
    this.mDragHelper = ViewDragHelper.create(this, 0.5F, new DragHelperCallback());
    this.mDragHelper.setMinVelocity(400.0F * f);
  }
  
  private boolean closePane(View paramView, int paramInt)
  {
    if ((this.mFirstLayout) || (smoothSlideTo(0.0F, paramInt)))
    {
      this.mPreservedOpenState = false;
      return true;
    }
    return false;
  }
  
  private void dimChildView(View paramView, float paramFloat, int paramInt)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if ((paramFloat > 0.0F) && (paramInt != 0))
    {
      i = (int)(((0xFF000000 & paramInt) >>> 24) * paramFloat);
      if (localLayoutParams.dimPaint == null) {
        localLayoutParams.dimPaint = new Paint();
      }
      localLayoutParams.dimPaint.setColorFilter(new PorterDuffColorFilter(i << 24 | 0xFFFFFF & paramInt, PorterDuff.Mode.SRC_OVER));
      if (ViewCompat.getLayerType(paramView) != 2) {
        ViewCompat.setLayerType(paramView, 2, localLayoutParams.dimPaint);
      }
      invalidateChildRegion(paramView);
    }
    while (ViewCompat.getLayerType(paramView) == 0)
    {
      int i;
      return;
    }
    if (localLayoutParams.dimPaint != null) {
      localLayoutParams.dimPaint.setColorFilter(null);
    }
    paramView = new DisableLayerRunnable(paramView);
    this.mPostedRunnables.add(paramView);
    ViewCompat.postOnAnimation(this, paramView);
  }
  
  private boolean openPane(View paramView, int paramInt)
  {
    if ((this.mFirstLayout) || (smoothSlideTo(1.0F, paramInt)))
    {
      this.mPreservedOpenState = true;
      return true;
    }
    return false;
  }
  
  private void parallaxOtherViews(float paramFloat)
  {
    boolean bool = isLayoutRtlSupport();
    Object localObject = (LayoutParams)this.mSlideableView.getLayoutParams();
    int i;
    label43:
    int j;
    if (((LayoutParams)localObject).dimWhenOffset) {
      if (bool)
      {
        i = ((LayoutParams)localObject).rightMargin;
        if (i > 0) {
          break label94;
        }
        i = 1;
        int n = getChildCount();
        j = 0;
        label52:
        if (j >= n) {
          return;
        }
        localObject = getChildAt(j);
        if (localObject != this.mSlideableView) {
          break label104;
        }
      }
    }
    label94:
    label104:
    do
    {
      j += 1;
      break label52;
      i = ((LayoutParams)localObject).leftMargin;
      break;
      i = 0;
      break label43;
      i = 0;
      break label43;
      int k = (int)((1.0F - this.mParallaxOffset) * this.mParallaxBy);
      this.mParallaxOffset = paramFloat;
      int m = k - (int)((1.0F - paramFloat) * this.mParallaxBy);
      k = m;
      if (bool) {
        k = -m;
      }
      ((View)localObject).offsetLeftAndRight(k);
    } while (i == 0);
    if (bool) {}
    for (float f = this.mParallaxOffset - 1.0F;; f = 1.0F - this.mParallaxOffset)
    {
      dimChildView((View)localObject, f, this.mCoveredFadeColor);
      break;
    }
  }
  
  private static boolean viewIsOpaque(View paramView)
  {
    if (paramView.isOpaque()) {
      return true;
    }
    if (Build.VERSION.SDK_INT >= 18) {
      return false;
    }
    paramView = paramView.getBackground();
    if (paramView != null) {
      return paramView.getOpacity() == -1;
    }
    return false;
  }
  
  protected boolean canScroll(View paramView, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramView instanceof ViewGroup))
    {
      ViewGroup localViewGroup = (ViewGroup)paramView;
      int j = paramView.getScrollX();
      int k = paramView.getScrollY();
      int i = localViewGroup.getChildCount() - 1;
      while (i >= 0)
      {
        View localView = localViewGroup.getChildAt(i);
        if ((paramInt2 + j >= localView.getLeft()) && (paramInt2 + j < localView.getRight()) && (paramInt3 + k >= localView.getTop()) && (paramInt3 + k < localView.getBottom()) && (canScroll(localView, true, paramInt1, paramInt2 + j - localView.getLeft(), paramInt3 + k - localView.getTop()))) {
          return true;
        }
        i -= 1;
      }
    }
    if (paramBoolean)
    {
      if (isLayoutRtlSupport()) {}
      for (;;)
      {
        return ViewCompat.canScrollHorizontally(paramView, paramInt1);
        paramInt1 = -paramInt1;
      }
    }
    return false;
  }
  
  @Deprecated
  public boolean canSlide()
  {
    return this.mCanSlide;
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((paramLayoutParams instanceof LayoutParams)) {
      return super.checkLayoutParams(paramLayoutParams);
    }
    return false;
  }
  
  public boolean closePane()
  {
    return closePane(this.mSlideableView, 0);
  }
  
  public void computeScroll()
  {
    if (this.mDragHelper.continueSettling(true))
    {
      if (!this.mCanSlide)
      {
        this.mDragHelper.abort();
        return;
      }
      ViewCompat.postInvalidateOnAnimation(this);
    }
  }
  
  void dispatchOnPanelClosed(View paramView)
  {
    if (this.mPanelSlideListener != null) {
      this.mPanelSlideListener.onPanelClosed(paramView);
    }
    sendAccessibilityEvent(32);
  }
  
  void dispatchOnPanelOpened(View paramView)
  {
    if (this.mPanelSlideListener != null) {
      this.mPanelSlideListener.onPanelOpened(paramView);
    }
    sendAccessibilityEvent(32);
  }
  
  void dispatchOnPanelSlide(View paramView)
  {
    if (this.mPanelSlideListener != null) {
      this.mPanelSlideListener.onPanelSlide(paramView, this.mSlideOffset);
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    View localView = null;
    super.draw(paramCanvas);
    if (isLayoutRtlSupport()) {}
    for (Drawable localDrawable = this.mShadowDrawableRight;; localDrawable = this.mShadowDrawableLeft)
    {
      if (getChildCount() > 1) {
        localView = getChildAt(1);
      }
      if ((localView != null) && (localDrawable != null)) {
        break;
      }
      return;
    }
    int k = localView.getTop();
    int m = localView.getBottom();
    int n = localDrawable.getIntrinsicWidth();
    int i;
    int j;
    if (isLayoutRtlSupport())
    {
      i = localView.getRight();
      j = i + n;
    }
    for (;;)
    {
      localDrawable.setBounds(i, k, j, m);
      localDrawable.draw(paramCanvas);
      return;
      j = localView.getLeft();
      i = j - n;
    }
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = paramCanvas.save(2);
    boolean bool;
    if ((!this.mCanSlide) || (localLayoutParams.slideable))
    {
      if (Build.VERSION.SDK_INT < 11) {
        break label143;
      }
      bool = super.drawChild(paramCanvas, paramView, paramLong);
    }
    for (;;)
    {
      paramCanvas.restoreToCount(i);
      return bool;
      if (this.mSlideableView == null) {
        break;
      }
      paramCanvas.getClipBounds(this.mTmpRect);
      if (isLayoutRtlSupport()) {
        this.mTmpRect.left = Math.max(this.mTmpRect.left, this.mSlideableView.getRight());
      }
      for (;;)
      {
        paramCanvas.clipRect(this.mTmpRect);
        break;
        this.mTmpRect.right = Math.min(this.mTmpRect.right, this.mSlideableView.getLeft());
      }
      label143:
      if ((localLayoutParams.dimWhenOffset) && (this.mSlideOffset > 0.0F))
      {
        if (!paramView.isDrawingCacheEnabled()) {
          paramView.setDrawingCacheEnabled(true);
        }
        Bitmap localBitmap = paramView.getDrawingCache();
        if (localBitmap != null)
        {
          paramCanvas.drawBitmap(localBitmap, paramView.getLeft(), paramView.getTop(), localLayoutParams.dimPaint);
          bool = false;
        }
        else
        {
          Log.e("SlidingPaneLayout", "drawChild: child view " + paramView + " returned null drawing cache");
          bool = super.drawChild(paramCanvas, paramView, paramLong);
        }
      }
      else
      {
        if (paramView.isDrawingCacheEnabled()) {
          paramView.setDrawingCacheEnabled(false);
        }
        bool = super.drawChild(paramCanvas, paramView, paramLong);
      }
    }
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams();
  }
  
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((paramLayoutParams instanceof ViewGroup.MarginLayoutParams)) {
      return new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams);
    }
    return new LayoutParams(paramLayoutParams);
  }
  
  @ColorInt
  public int getCoveredFadeColor()
  {
    return this.mCoveredFadeColor;
  }
  
  public int getParallaxDistance()
  {
    return this.mParallaxBy;
  }
  
  @ColorInt
  public int getSliderFadeColor()
  {
    return this.mSliderFadeColor;
  }
  
  void invalidateChildRegion(View paramView)
  {
    IMPL.invalidateChildRegion(this, paramView);
  }
  
  boolean isDimmed(View paramView)
  {
    boolean bool2 = false;
    if (paramView == null) {
      return false;
    }
    paramView = (LayoutParams)paramView.getLayoutParams();
    boolean bool1 = bool2;
    if (this.mCanSlide)
    {
      bool1 = bool2;
      if (paramView.dimWhenOffset)
      {
        bool1 = bool2;
        if (this.mSlideOffset > 0.0F) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  boolean isLayoutRtlSupport()
  {
    return ViewCompat.getLayoutDirection(this) == 1;
  }
  
  public boolean isOpen()
  {
    return (!this.mCanSlide) || (this.mSlideOffset == 1.0F);
  }
  
  public boolean isSlideable()
  {
    return this.mCanSlide;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.mFirstLayout = true;
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.mFirstLayout = true;
    int i = 0;
    int j = this.mPostedRunnables.size();
    while (i < j)
    {
      ((DisableLayerRunnable)this.mPostedRunnables.get(i)).run();
      i += 1;
    }
    this.mPostedRunnables.clear();
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool2 = true;
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    if ((!this.mCanSlide) && (i == 0) && (getChildCount() > 1))
    {
      View localView = getChildAt(1);
      if (localView != null) {
        if (!this.mDragHelper.isViewUnder(localView, (int)paramMotionEvent.getX(), (int)paramMotionEvent.getY())) {
          break label104;
        }
      }
    }
    label104:
    for (boolean bool1 = false;; bool1 = true)
    {
      this.mPreservedOpenState = bool1;
      if ((this.mCanSlide) && ((!this.mIsUnableToDrag) || (i == 0))) {
        break;
      }
      this.mDragHelper.cancel();
      return super.onInterceptTouchEvent(paramMotionEvent);
    }
    if ((i == 3) || (i == 1))
    {
      this.mDragHelper.cancel();
      return false;
    }
    boolean bool3 = false;
    bool1 = bool3;
    switch (i)
    {
    default: 
      bool1 = bool3;
    }
    float f1;
    float f2;
    do
    {
      do
      {
        for (;;)
        {
          if (!this.mDragHelper.shouldInterceptTouchEvent(paramMotionEvent)) {
            bool2 = bool1;
          }
          return bool2;
          this.mIsUnableToDrag = false;
          f1 = paramMotionEvent.getX();
          f2 = paramMotionEvent.getY();
          this.mInitialMotionX = f1;
          this.mInitialMotionY = f2;
          bool1 = bool3;
          if (this.mDragHelper.isViewUnder(this.mSlideableView, (int)f1, (int)f2))
          {
            bool1 = bool3;
            if (isDimmed(this.mSlideableView)) {
              bool1 = true;
            }
          }
        }
        f2 = paramMotionEvent.getX();
        f1 = paramMotionEvent.getY();
        f2 = Math.abs(f2 - this.mInitialMotionX);
        f1 = Math.abs(f1 - this.mInitialMotionY);
        bool1 = bool3;
      } while (f2 <= this.mDragHelper.getTouchSlop());
      bool1 = bool3;
    } while (f1 <= f2);
    this.mDragHelper.cancel();
    this.mIsUnableToDrag = true;
    return false;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    boolean bool = isLayoutRtlSupport();
    int k;
    label35:
    label46:
    int n;
    int m;
    if (bool)
    {
      this.mDragHelper.setEdgeTrackingEnabled(2);
      k = paramInt3 - paramInt1;
      if (!bool) {
        break label138;
      }
      paramInt1 = getPaddingRight();
      if (!bool) {
        break label146;
      }
      paramInt3 = getPaddingLeft();
      n = getPaddingTop();
      m = getChildCount();
      paramInt2 = paramInt1;
      if (this.mFirstLayout) {
        if ((!this.mCanSlide) || (!this.mPreservedOpenState)) {
          break label155;
        }
      }
    }
    View localView;
    label138:
    label146:
    label155:
    for (float f = 1.0F;; f = 0.0F)
    {
      this.mSlideOffset = f;
      paramInt4 = 0;
      for (;;)
      {
        if (paramInt4 >= m) {
          break label434;
        }
        localView = getChildAt(paramInt4);
        if (localView.getVisibility() != 8) {
          break;
        }
        paramInt4 += 1;
      }
      this.mDragHelper.setEdgeTrackingEnabled(1);
      break;
      paramInt1 = getPaddingLeft();
      break label35;
      paramInt3 = getPaddingRight();
      break label46;
    }
    LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
    int i1 = localView.getMeasuredWidth();
    int j = 0;
    int i;
    if (localLayoutParams.slideable)
    {
      i = localLayoutParams.leftMargin;
      int i2 = localLayoutParams.rightMargin;
      i2 = Math.min(paramInt1, k - paramInt3 - this.mOverhangSize) - paramInt2 - (i + i2);
      this.mSlideRange = i2;
      if (bool)
      {
        i = localLayoutParams.rightMargin;
        label245:
        if (paramInt2 + i + i2 + i1 / 2 <= k - paramInt3) {
          break label370;
        }
        paramBoolean = true;
        label267:
        localLayoutParams.dimWhenOffset = paramBoolean;
        i2 = (int)(i2 * this.mSlideOffset);
        paramInt2 += i2 + i;
        this.mSlideOffset = (i2 / this.mSlideRange);
        i = j;
        label309:
        if (!bool) {
          break label418;
        }
        j = k - paramInt2 + i;
        i = j - i1;
      }
    }
    for (;;)
    {
      localView.layout(i, n, j, n + localView.getMeasuredHeight());
      paramInt1 += localView.getWidth();
      break;
      i = localLayoutParams.leftMargin;
      break label245;
      label370:
      paramBoolean = false;
      break label267;
      if ((this.mCanSlide) && (this.mParallaxBy != 0))
      {
        i = (int)((1.0F - this.mSlideOffset) * this.mParallaxBy);
        paramInt2 = paramInt1;
        break label309;
      }
      paramInt2 = paramInt1;
      i = j;
      break label309;
      label418:
      i = paramInt2 - i;
      j = i + i1;
    }
    label434:
    if (this.mFirstLayout)
    {
      if (!this.mCanSlide) {
        break label509;
      }
      if (this.mParallaxBy != 0) {
        parallaxOtherViews(this.mSlideOffset);
      }
      if (((LayoutParams)this.mSlideableView.getLayoutParams()).dimWhenOffset) {
        dimChildView(this.mSlideableView, this.mSlideOffset, this.mSliderFadeColor);
      }
    }
    for (;;)
    {
      updateObscuredViewsVisibility(this.mSlideableView);
      this.mFirstLayout = false;
      return;
      label509:
      paramInt1 = 0;
      while (paramInt1 < m)
      {
        dimChildView(getChildAt(paramInt1), 0.0F, this.mSliderFadeColor);
        paramInt1 += 1;
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int m = View.MeasureSpec.getMode(paramInt1);
    int i = View.MeasureSpec.getSize(paramInt1);
    int j = View.MeasureSpec.getMode(paramInt2);
    paramInt2 = View.MeasureSpec.getSize(paramInt2);
    int k;
    int n;
    label88:
    float f1;
    boolean bool1;
    int i3;
    int i4;
    int i1;
    label140:
    View localView;
    LayoutParams localLayoutParams;
    int i2;
    if (m != 1073741824) {
      if (isInEditMode()) {
        if (m == Integer.MIN_VALUE)
        {
          k = i;
          paramInt1 = paramInt2;
          n = j;
          i = 0;
          paramInt2 = -1;
          switch (n)
          {
          default: 
            f1 = 0.0F;
            bool1 = false;
            i3 = k - getPaddingLeft() - getPaddingRight();
            m = i3;
            i4 = getChildCount();
            if (i4 > 2) {
              Log.e("SlidingPaneLayout", "onMeasure: More than two child views are not supported.");
            }
            this.mSlideableView = null;
            i1 = 0;
            if (i1 >= i4) {
              break label653;
            }
            localView = getChildAt(i1);
            localLayoutParams = (LayoutParams)localView.getLayoutParams();
            if (localView.getVisibility() == 8)
            {
              localLayoutParams.dimWhenOffset = false;
              i2 = m;
              j = i;
              bool2 = bool1;
            }
            break;
          }
        }
      }
    }
    float f2;
    do
    {
      i1 += 1;
      bool1 = bool2;
      i = j;
      m = i2;
      break label140;
      n = j;
      paramInt1 = paramInt2;
      k = i;
      if (m != 0) {
        break;
      }
      k = 300;
      n = j;
      paramInt1 = paramInt2;
      break;
      throw new IllegalStateException("Width must have an exact value or MATCH_PARENT");
      n = j;
      paramInt1 = paramInt2;
      k = i;
      if (j != 0) {
        break;
      }
      if (isInEditMode())
      {
        n = j;
        paramInt1 = paramInt2;
        k = i;
        if (j != 0) {
          break;
        }
        n = Integer.MIN_VALUE;
        paramInt1 = 300;
        k = i;
        break;
      }
      throw new IllegalStateException("Height must not be UNSPECIFIED");
      paramInt2 = paramInt1 - getPaddingTop() - getPaddingBottom();
      i = paramInt2;
      break label88;
      paramInt2 = paramInt1 - getPaddingTop() - getPaddingBottom();
      break label88;
      f2 = f1;
      if (localLayoutParams.weight <= 0.0F) {
        break label396;
      }
      f2 = f1 + localLayoutParams.weight;
      bool2 = bool1;
      j = i;
      f1 = f2;
      i2 = m;
    } while (localLayoutParams.width == 0);
    label396:
    paramInt1 = localLayoutParams.leftMargin + localLayoutParams.rightMargin;
    if (localLayoutParams.width == -2)
    {
      paramInt1 = View.MeasureSpec.makeMeasureSpec(i3 - paramInt1, Integer.MIN_VALUE);
      label429:
      if (localLayoutParams.height != -2) {
        break label610;
      }
      j = View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE);
      label448:
      localView.measure(paramInt1, j);
      j = localView.getMeasuredWidth();
      i2 = localView.getMeasuredHeight();
      paramInt1 = i;
      if (n == Integer.MIN_VALUE)
      {
        paramInt1 = i;
        if (i2 > i) {
          paramInt1 = Math.min(i2, paramInt2);
        }
      }
      i = m - j;
      if (i >= 0) {
        break label647;
      }
    }
    label610:
    label647:
    for (boolean bool2 = true;; bool2 = false)
    {
      localLayoutParams.slideable = bool2;
      bool1 |= bool2;
      bool2 = bool1;
      j = paramInt1;
      f1 = f2;
      i2 = i;
      if (!localLayoutParams.slideable) {
        break;
      }
      this.mSlideableView = localView;
      bool2 = bool1;
      j = paramInt1;
      f1 = f2;
      i2 = i;
      break;
      if (localLayoutParams.width == -1)
      {
        paramInt1 = View.MeasureSpec.makeMeasureSpec(i3 - paramInt1, 1073741824);
        break label429;
      }
      paramInt1 = View.MeasureSpec.makeMeasureSpec(localLayoutParams.width, 1073741824);
      break label429;
      if (localLayoutParams.height == -1)
      {
        j = View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824);
        break label448;
      }
      j = View.MeasureSpec.makeMeasureSpec(localLayoutParams.height, 1073741824);
      break label448;
    }
    label653:
    if ((bool1) || (f1 > 0.0F))
    {
      i1 = i3 - this.mOverhangSize;
      j = 0;
      if (j < i4)
      {
        localView = getChildAt(j);
        if (localView.getVisibility() == 8) {}
        for (;;)
        {
          j += 1;
          break;
          localLayoutParams = (LayoutParams)localView.getLayoutParams();
          if (localView.getVisibility() != 8)
          {
            if ((localLayoutParams.width == 0) && (localLayoutParams.weight > 0.0F))
            {
              paramInt1 = 1;
              label750:
              if (paramInt1 == 0) {
                break label840;
              }
              n = 0;
              label757:
              if ((!bool1) || (localView == this.mSlideableView)) {
                break label900;
              }
              if ((localLayoutParams.width >= 0) || ((n <= i1) && (localLayoutParams.weight <= 0.0F))) {
                continue;
              }
              if (paramInt1 == 0) {
                break label885;
              }
              if (localLayoutParams.height != -2) {
                break label850;
              }
              paramInt1 = View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE);
            }
            for (;;)
            {
              localView.measure(View.MeasureSpec.makeMeasureSpec(i1, 1073741824), paramInt1);
              break;
              paramInt1 = 0;
              break label750;
              label840:
              n = localView.getMeasuredWidth();
              break label757;
              label850:
              if (localLayoutParams.height == -1)
              {
                paramInt1 = View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824);
              }
              else
              {
                paramInt1 = View.MeasureSpec.makeMeasureSpec(localLayoutParams.height, 1073741824);
                continue;
                label885:
                paramInt1 = View.MeasureSpec.makeMeasureSpec(localView.getMeasuredHeight(), 1073741824);
              }
            }
            label900:
            if (localLayoutParams.weight > 0.0F)
            {
              if (localLayoutParams.width == 0) {
                if (localLayoutParams.height == -2) {
                  paramInt1 = View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE);
                }
              }
              for (;;)
              {
                if (!bool1) {
                  break label1035;
                }
                i2 = i3 - (localLayoutParams.leftMargin + localLayoutParams.rightMargin);
                int i5 = View.MeasureSpec.makeMeasureSpec(i2, 1073741824);
                if (n == i2) {
                  break;
                }
                localView.measure(i5, paramInt1);
                break;
                if (localLayoutParams.height == -1)
                {
                  paramInt1 = View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824);
                }
                else
                {
                  paramInt1 = View.MeasureSpec.makeMeasureSpec(localLayoutParams.height, 1073741824);
                  continue;
                  paramInt1 = View.MeasureSpec.makeMeasureSpec(localView.getMeasuredHeight(), 1073741824);
                }
              }
              label1035:
              i2 = Math.max(0, m);
              localView.measure(View.MeasureSpec.makeMeasureSpec(n + (int)(localLayoutParams.weight * i2 / f1), 1073741824), paramInt1);
            }
          }
        }
      }
    }
    setMeasuredDimension(k, getPaddingTop() + i + getPaddingBottom());
    this.mCanSlide = bool1;
    if ((this.mDragHelper.getViewDragState() == 0) || (bool1)) {
      return;
    }
    this.mDragHelper.abort();
  }
  
  void onPanelDragged(int paramInt)
  {
    if (this.mSlideableView == null)
    {
      this.mSlideOffset = 0.0F;
      return;
    }
    boolean bool = isLayoutRtlSupport();
    LayoutParams localLayoutParams = (LayoutParams)this.mSlideableView.getLayoutParams();
    int i = this.mSlideableView.getWidth();
    if (bool)
    {
      paramInt = getWidth() - paramInt - i;
      if (!bool) {
        break label141;
      }
      i = getPaddingRight();
      label63:
      if (!bool) {
        break label149;
      }
    }
    label141:
    label149:
    for (int j = localLayoutParams.rightMargin;; j = localLayoutParams.leftMargin)
    {
      this.mSlideOffset = ((paramInt - (i + j)) / this.mSlideRange);
      if (this.mParallaxBy != 0) {
        parallaxOtherViews(this.mSlideOffset);
      }
      if (localLayoutParams.dimWhenOffset) {
        dimChildView(this.mSlideableView, this.mSlideOffset, this.mSliderFadeColor);
      }
      dispatchOnPanelSlide(this.mSlideableView);
      return;
      break;
      i = getPaddingLeft();
      break label63;
    }
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!(paramParcelable instanceof SavedState))
    {
      super.onRestoreInstanceState(paramParcelable);
      return;
    }
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    if (paramParcelable.isOpen) {
      openPane();
    }
    for (;;)
    {
      this.mPreservedOpenState = paramParcelable.isOpen;
      return;
      closePane();
    }
  }
  
  protected Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    if (isSlideable()) {}
    for (boolean bool = isOpen();; bool = this.mPreservedOpenState)
    {
      localSavedState.isOpen = bool;
      return localSavedState;
    }
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (paramInt1 != paramInt3) {
      this.mFirstLayout = true;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!this.mCanSlide) {
      return super.onTouchEvent(paramMotionEvent);
    }
    this.mDragHelper.processTouchEvent(paramMotionEvent);
    switch (paramMotionEvent.getAction() & 0xFF)
    {
    }
    float f1;
    float f2;
    float f3;
    float f4;
    int i;
    do
    {
      do
      {
        return true;
        f1 = paramMotionEvent.getX();
        f2 = paramMotionEvent.getY();
        this.mInitialMotionX = f1;
        this.mInitialMotionY = f2;
        return true;
      } while (!isDimmed(this.mSlideableView));
      f1 = paramMotionEvent.getX();
      f2 = paramMotionEvent.getY();
      f3 = f1 - this.mInitialMotionX;
      f4 = f2 - this.mInitialMotionY;
      i = this.mDragHelper.getTouchSlop();
    } while ((f3 * f3 + f4 * f4 >= i * i) || (!this.mDragHelper.isViewUnder(this.mSlideableView, (int)f1, (int)f2)));
    closePane(this.mSlideableView, 0);
    return true;
  }
  
  public boolean openPane()
  {
    return openPane(this.mSlideableView, 0);
  }
  
  public void requestChildFocus(View paramView1, View paramView2)
  {
    super.requestChildFocus(paramView1, paramView2);
    if ((isInTouchMode()) || (this.mCanSlide)) {
      return;
    }
    if (paramView1 == this.mSlideableView) {}
    for (boolean bool = true;; bool = false)
    {
      this.mPreservedOpenState = bool;
      return;
    }
  }
  
  void setAllChildrenVisible()
  {
    int i = 0;
    int j = getChildCount();
    while (i < j)
    {
      View localView = getChildAt(i);
      if (localView.getVisibility() == 4) {
        localView.setVisibility(0);
      }
      i += 1;
    }
  }
  
  public void setCoveredFadeColor(@ColorInt int paramInt)
  {
    this.mCoveredFadeColor = paramInt;
  }
  
  public void setPanelSlideListener(PanelSlideListener paramPanelSlideListener)
  {
    this.mPanelSlideListener = paramPanelSlideListener;
  }
  
  public void setParallaxDistance(int paramInt)
  {
    this.mParallaxBy = paramInt;
    requestLayout();
  }
  
  @Deprecated
  public void setShadowDrawable(Drawable paramDrawable)
  {
    setShadowDrawableLeft(paramDrawable);
  }
  
  public void setShadowDrawableLeft(Drawable paramDrawable)
  {
    this.mShadowDrawableLeft = paramDrawable;
  }
  
  public void setShadowDrawableRight(Drawable paramDrawable)
  {
    this.mShadowDrawableRight = paramDrawable;
  }
  
  @Deprecated
  public void setShadowResource(@DrawableRes int paramInt)
  {
    setShadowDrawable(getResources().getDrawable(paramInt));
  }
  
  public void setShadowResourceLeft(int paramInt)
  {
    setShadowDrawableLeft(ContextCompat.getDrawable(getContext(), paramInt));
  }
  
  public void setShadowResourceRight(int paramInt)
  {
    setShadowDrawableRight(ContextCompat.getDrawable(getContext(), paramInt));
  }
  
  public void setSliderFadeColor(@ColorInt int paramInt)
  {
    this.mSliderFadeColor = paramInt;
  }
  
  @Deprecated
  public void smoothSlideClosed()
  {
    closePane();
  }
  
  @Deprecated
  public void smoothSlideOpen()
  {
    openPane();
  }
  
  boolean smoothSlideTo(float paramFloat, int paramInt)
  {
    if (!this.mCanSlide) {
      return false;
    }
    boolean bool = isLayoutRtlSupport();
    LayoutParams localLayoutParams = (LayoutParams)this.mSlideableView.getLayoutParams();
    int i;
    int j;
    if (bool)
    {
      paramInt = getPaddingRight();
      i = localLayoutParams.rightMargin;
      j = this.mSlideableView.getWidth();
    }
    for (paramInt = (int)(getWidth() - (paramInt + i + this.mSlideRange * paramFloat + j)); this.mDragHelper.smoothSlideViewTo(this.mSlideableView, paramInt, this.mSlideableView.getTop()); paramInt = (int)(getPaddingLeft() + localLayoutParams.leftMargin + this.mSlideRange * paramFloat))
    {
      setAllChildrenVisible();
      ViewCompat.postInvalidateOnAnimation(this);
      return true;
    }
    return false;
  }
  
  void updateObscuredViewsVisibility(View paramView)
  {
    boolean bool = isLayoutRtlSupport();
    int i;
    int j;
    label31:
    int i4;
    int i5;
    int i6;
    int m;
    int n;
    int i1;
    int k;
    label84:
    int i2;
    int i7;
    if (bool)
    {
      i = getWidth() - getPaddingRight();
      if (!bool) {
        break label123;
      }
      j = getPaddingLeft();
      i4 = getPaddingTop();
      i5 = getHeight();
      i6 = getPaddingBottom();
      if ((paramView == null) || (!viewIsOpaque(paramView))) {
        break label136;
      }
      m = paramView.getLeft();
      n = paramView.getRight();
      i1 = paramView.getTop();
      k = paramView.getBottom();
      i2 = 0;
      i7 = getChildCount();
    }
    View localView;
    for (;;)
    {
      if (i2 < i7)
      {
        localView = getChildAt(i2);
        if (localView != paramView) {}
      }
      else
      {
        return;
        i = getPaddingLeft();
        break;
        label123:
        j = getWidth() - getPaddingRight();
        break label31;
        label136:
        k = 0;
        i1 = 0;
        n = 0;
        m = 0;
        break label84;
      }
      if (localView.getVisibility() != 8) {
        break label170;
      }
      i2 += 1;
    }
    label170:
    if (bool)
    {
      i3 = j;
      label178:
      int i8 = Math.max(i3, localView.getLeft());
      int i9 = Math.max(i4, localView.getTop());
      if (!bool) {
        break label284;
      }
      i3 = i;
      label210:
      i3 = Math.min(i3, localView.getRight());
      int i10 = Math.min(i5 - i6, localView.getBottom());
      if ((i8 < m) || (i9 < i1) || (i3 > n) || (i10 > k)) {
        break label290;
      }
    }
    label284:
    label290:
    for (int i3 = 4;; i3 = 0)
    {
      localView.setVisibility(i3);
      break;
      i3 = i;
      break label178;
      i3 = j;
      break label210;
    }
  }
  
  class AccessibilityDelegate
    extends AccessibilityDelegateCompat
  {
    private final Rect mTmpRect = new Rect();
    
    AccessibilityDelegate() {}
    
    private void copyNodeInfoNoChildren(AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat1, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat2)
    {
      Rect localRect = this.mTmpRect;
      paramAccessibilityNodeInfoCompat2.getBoundsInParent(localRect);
      paramAccessibilityNodeInfoCompat1.setBoundsInParent(localRect);
      paramAccessibilityNodeInfoCompat2.getBoundsInScreen(localRect);
      paramAccessibilityNodeInfoCompat1.setBoundsInScreen(localRect);
      paramAccessibilityNodeInfoCompat1.setVisibleToUser(paramAccessibilityNodeInfoCompat2.isVisibleToUser());
      paramAccessibilityNodeInfoCompat1.setPackageName(paramAccessibilityNodeInfoCompat2.getPackageName());
      paramAccessibilityNodeInfoCompat1.setClassName(paramAccessibilityNodeInfoCompat2.getClassName());
      paramAccessibilityNodeInfoCompat1.setContentDescription(paramAccessibilityNodeInfoCompat2.getContentDescription());
      paramAccessibilityNodeInfoCompat1.setEnabled(paramAccessibilityNodeInfoCompat2.isEnabled());
      paramAccessibilityNodeInfoCompat1.setClickable(paramAccessibilityNodeInfoCompat2.isClickable());
      paramAccessibilityNodeInfoCompat1.setFocusable(paramAccessibilityNodeInfoCompat2.isFocusable());
      paramAccessibilityNodeInfoCompat1.setFocused(paramAccessibilityNodeInfoCompat2.isFocused());
      paramAccessibilityNodeInfoCompat1.setAccessibilityFocused(paramAccessibilityNodeInfoCompat2.isAccessibilityFocused());
      paramAccessibilityNodeInfoCompat1.setSelected(paramAccessibilityNodeInfoCompat2.isSelected());
      paramAccessibilityNodeInfoCompat1.setLongClickable(paramAccessibilityNodeInfoCompat2.isLongClickable());
      paramAccessibilityNodeInfoCompat1.addAction(paramAccessibilityNodeInfoCompat2.getActions());
      paramAccessibilityNodeInfoCompat1.setMovementGranularities(paramAccessibilityNodeInfoCompat2.getMovementGranularities());
    }
    
    public boolean filter(View paramView)
    {
      return SlidingPaneLayout.this.isDimmed(paramView);
    }
    
    public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      super.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
      paramAccessibilityEvent.setClassName(SlidingPaneLayout.class.getName());
    }
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      AccessibilityNodeInfoCompat localAccessibilityNodeInfoCompat = AccessibilityNodeInfoCompat.obtain(paramAccessibilityNodeInfoCompat);
      super.onInitializeAccessibilityNodeInfo(paramView, localAccessibilityNodeInfoCompat);
      copyNodeInfoNoChildren(paramAccessibilityNodeInfoCompat, localAccessibilityNodeInfoCompat);
      localAccessibilityNodeInfoCompat.recycle();
      paramAccessibilityNodeInfoCompat.setClassName(SlidingPaneLayout.class.getName());
      paramAccessibilityNodeInfoCompat.setSource(paramView);
      paramView = ViewCompat.getParentForAccessibility(paramView);
      if ((paramView instanceof View)) {
        paramAccessibilityNodeInfoCompat.setParent((View)paramView);
      }
      int j = SlidingPaneLayout.this.getChildCount();
      int i = 0;
      while (i < j)
      {
        paramView = SlidingPaneLayout.this.getChildAt(i);
        if ((!filter(paramView)) && (paramView.getVisibility() == 0))
        {
          ViewCompat.setImportantForAccessibility(paramView, 1);
          paramAccessibilityNodeInfoCompat.addChild(paramView);
        }
        i += 1;
      }
    }
    
    public boolean onRequestSendAccessibilityEvent(ViewGroup paramViewGroup, View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      if (!filter(paramView)) {
        return super.onRequestSendAccessibilityEvent(paramViewGroup, paramView, paramAccessibilityEvent);
      }
      return false;
    }
  }
  
  private class DisableLayerRunnable
    implements Runnable
  {
    final View mChildView;
    
    DisableLayerRunnable(View paramView)
    {
      this.mChildView = paramView;
    }
    
    public void run()
    {
      if (this.mChildView.getParent() == SlidingPaneLayout.this)
      {
        ViewCompat.setLayerType(this.mChildView, 0, null);
        SlidingPaneLayout.this.invalidateChildRegion(this.mChildView);
      }
      SlidingPaneLayout.this.mPostedRunnables.remove(this);
    }
  }
  
  private class DragHelperCallback
    extends ViewDragHelper.Callback
  {
    DragHelperCallback() {}
    
    public int clampViewPositionHorizontal(View paramView, int paramInt1, int paramInt2)
    {
      paramView = (SlidingPaneLayout.LayoutParams)SlidingPaneLayout.this.mSlideableView.getLayoutParams();
      if (SlidingPaneLayout.this.isLayoutRtlSupport())
      {
        paramInt2 = SlidingPaneLayout.this.getWidth() - (SlidingPaneLayout.this.getPaddingRight() + paramView.rightMargin + SlidingPaneLayout.this.mSlideableView.getWidth());
        i = SlidingPaneLayout.this.mSlideRange;
        return Math.max(Math.min(paramInt1, paramInt2), paramInt2 - i);
      }
      paramInt2 = SlidingPaneLayout.this.getPaddingLeft() + paramView.leftMargin;
      int i = SlidingPaneLayout.this.mSlideRange;
      return Math.min(Math.max(paramInt1, paramInt2), paramInt2 + i);
    }
    
    public int clampViewPositionVertical(View paramView, int paramInt1, int paramInt2)
    {
      return paramView.getTop();
    }
    
    public int getViewHorizontalDragRange(View paramView)
    {
      return SlidingPaneLayout.this.mSlideRange;
    }
    
    public void onEdgeDragStarted(int paramInt1, int paramInt2)
    {
      SlidingPaneLayout.this.mDragHelper.captureChildView(SlidingPaneLayout.this.mSlideableView, paramInt2);
    }
    
    public void onViewCaptured(View paramView, int paramInt)
    {
      SlidingPaneLayout.this.setAllChildrenVisible();
    }
    
    public void onViewDragStateChanged(int paramInt)
    {
      if (SlidingPaneLayout.this.mDragHelper.getViewDragState() == 0)
      {
        if (SlidingPaneLayout.this.mSlideOffset == 0.0F)
        {
          SlidingPaneLayout.this.updateObscuredViewsVisibility(SlidingPaneLayout.this.mSlideableView);
          SlidingPaneLayout.this.dispatchOnPanelClosed(SlidingPaneLayout.this.mSlideableView);
          SlidingPaneLayout.this.mPreservedOpenState = false;
        }
      }
      else {
        return;
      }
      SlidingPaneLayout.this.dispatchOnPanelOpened(SlidingPaneLayout.this.mSlideableView);
      SlidingPaneLayout.this.mPreservedOpenState = true;
    }
    
    public void onViewPositionChanged(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      SlidingPaneLayout.this.onPanelDragged(paramInt1);
      SlidingPaneLayout.this.invalidate();
    }
    
    public void onViewReleased(View paramView, float paramFloat1, float paramFloat2)
    {
      SlidingPaneLayout.LayoutParams localLayoutParams = (SlidingPaneLayout.LayoutParams)paramView.getLayoutParams();
      int j;
      int i;
      if (SlidingPaneLayout.this.isLayoutRtlSupport())
      {
        j = SlidingPaneLayout.this.getPaddingRight() + localLayoutParams.rightMargin;
        if (paramFloat1 >= 0.0F)
        {
          i = j;
          if (paramFloat1 == 0.0F)
          {
            i = j;
            if (SlidingPaneLayout.this.mSlideOffset <= 0.5F) {}
          }
        }
        else
        {
          i = j + SlidingPaneLayout.this.mSlideRange;
        }
        j = SlidingPaneLayout.this.mSlideableView.getWidth();
        i = SlidingPaneLayout.this.getWidth() - i - j;
      }
      for (;;)
      {
        SlidingPaneLayout.this.mDragHelper.settleCapturedViewAt(i, paramView.getTop());
        SlidingPaneLayout.this.invalidate();
        return;
        j = SlidingPaneLayout.this.getPaddingLeft() + localLayoutParams.leftMargin;
        if (paramFloat1 <= 0.0F)
        {
          i = j;
          if (paramFloat1 == 0.0F)
          {
            i = j;
            if (SlidingPaneLayout.this.mSlideOffset <= 0.5F) {}
          }
        }
        else
        {
          i = j + SlidingPaneLayout.this.mSlideRange;
        }
      }
    }
    
    public boolean tryCaptureView(View paramView, int paramInt)
    {
      if (SlidingPaneLayout.this.mIsUnableToDrag) {
        return false;
      }
      return ((SlidingPaneLayout.LayoutParams)paramView.getLayoutParams()).slideable;
    }
  }
  
  public static class LayoutParams
    extends ViewGroup.MarginLayoutParams
  {
    private static final int[] ATTRS = { 16843137 };
    Paint dimPaint;
    boolean dimWhenOffset;
    boolean slideable;
    public float weight = 0.0F;
    
    public LayoutParams()
    {
      super(-1);
    }
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, ATTRS);
      this.weight = paramContext.getFloat(0, 0.0F);
      paramContext.recycle();
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
      this.weight = paramLayoutParams.weight;
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      super();
    }
  }
  
  public static abstract interface PanelSlideListener
  {
    public abstract void onPanelClosed(View paramView);
    
    public abstract void onPanelOpened(View paramView);
    
    public abstract void onPanelSlide(View paramView, float paramFloat);
  }
  
  static class SavedState
    extends AbsSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks()
    {
      public SlidingPaneLayout.SavedState createFromParcel(Parcel paramAnonymousParcel, ClassLoader paramAnonymousClassLoader)
      {
        return new SlidingPaneLayout.SavedState(paramAnonymousParcel, paramAnonymousClassLoader);
      }
      
      public SlidingPaneLayout.SavedState[] newArray(int paramAnonymousInt)
      {
        return new SlidingPaneLayout.SavedState[paramAnonymousInt];
      }
    });
    boolean isOpen;
    
    SavedState(Parcel paramParcel, ClassLoader paramClassLoader)
    {
      super(paramClassLoader);
      if (paramParcel.readInt() != 0) {
        bool = true;
      }
      this.isOpen = bool;
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      if (this.isOpen) {}
      for (paramInt = 1;; paramInt = 0)
      {
        paramParcel.writeInt(paramInt);
        return;
      }
    }
  }
  
  public static class SimplePanelSlideListener
    implements SlidingPaneLayout.PanelSlideListener
  {
    public void onPanelClosed(View paramView) {}
    
    public void onPanelOpened(View paramView) {}
    
    public void onPanelSlide(View paramView, float paramFloat) {}
  }
  
  static abstract interface SlidingPanelLayoutImpl
  {
    public abstract void invalidateChildRegion(SlidingPaneLayout paramSlidingPaneLayout, View paramView);
  }
  
  static class SlidingPanelLayoutImplBase
    implements SlidingPaneLayout.SlidingPanelLayoutImpl
  {
    public void invalidateChildRegion(SlidingPaneLayout paramSlidingPaneLayout, View paramView)
    {
      ViewCompat.postInvalidateOnAnimation(paramSlidingPaneLayout, paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom());
    }
  }
  
  static class SlidingPanelLayoutImplJB
    extends SlidingPaneLayout.SlidingPanelLayoutImplBase
  {
    private Method mGetDisplayList;
    private Field mRecreateDisplayList;
    
    SlidingPanelLayoutImplJB()
    {
      try
      {
        this.mGetDisplayList = View.class.getDeclaredMethod("getDisplayList", (Class[])null);
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        for (;;)
        {
          try
          {
            this.mRecreateDisplayList = View.class.getDeclaredField("mRecreateDisplayList");
            this.mRecreateDisplayList.setAccessible(true);
            return;
          }
          catch (NoSuchFieldException localNoSuchFieldException)
          {
            Log.e("SlidingPaneLayout", "Couldn't fetch mRecreateDisplayList field; dimming will be slow.", localNoSuchFieldException);
          }
          localNoSuchMethodException = localNoSuchMethodException;
          Log.e("SlidingPaneLayout", "Couldn't fetch getDisplayList method; dimming won't work right.", localNoSuchMethodException);
        }
      }
    }
    
    public void invalidateChildRegion(SlidingPaneLayout paramSlidingPaneLayout, View paramView)
    {
      if ((this.mGetDisplayList != null) && (this.mRecreateDisplayList != null)) {
        try
        {
          this.mRecreateDisplayList.setBoolean(paramView, true);
          this.mGetDisplayList.invoke(paramView, (Object[])null);
          super.invalidateChildRegion(paramSlidingPaneLayout, paramView);
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            Log.e("SlidingPaneLayout", "Error refreshing display list state", localException);
          }
        }
      }
      paramView.invalidate();
    }
  }
  
  static class SlidingPanelLayoutImplJBMR1
    extends SlidingPaneLayout.SlidingPanelLayoutImplBase
  {
    public void invalidateChildRegion(SlidingPaneLayout paramSlidingPaneLayout, View paramView)
    {
      ViewCompat.setLayerPaint(paramView, ((SlidingPaneLayout.LayoutParams)paramView.getLayoutParams()).dimPaint);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\widget\SlidingPaneLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */