package android.support.v4.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.RestrictTo;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ScrollView;
import java.util.ArrayList;
import java.util.List;

public class NestedScrollView
  extends FrameLayout
  implements NestedScrollingParent, NestedScrollingChild, ScrollingView
{
  private static final AccessibilityDelegate ACCESSIBILITY_DELEGATE = new AccessibilityDelegate();
  static final int ANIMATED_SCROLL_GAP = 250;
  private static final int INVALID_POINTER = -1;
  static final float MAX_SCROLL_FACTOR = 0.5F;
  private static final int[] SCROLLVIEW_STYLEABLE = { 16843130 };
  private static final String TAG = "NestedScrollView";
  private int mActivePointerId = -1;
  private final NestedScrollingChildHelper mChildHelper;
  private View mChildToScrollTo = null;
  private EdgeEffectCompat mEdgeGlowBottom;
  private EdgeEffectCompat mEdgeGlowTop;
  private boolean mFillViewport;
  private boolean mIsBeingDragged = false;
  private boolean mIsLaidOut = false;
  private boolean mIsLayoutDirty = true;
  private int mLastMotionY;
  private long mLastScroll;
  private int mMaximumVelocity;
  private int mMinimumVelocity;
  private int mNestedYOffset;
  private OnScrollChangeListener mOnScrollChangeListener;
  private final NestedScrollingParentHelper mParentHelper;
  private SavedState mSavedState;
  private final int[] mScrollConsumed = new int[2];
  private final int[] mScrollOffset = new int[2];
  private ScrollerCompat mScroller;
  private boolean mSmoothScrollingEnabled = true;
  private final Rect mTempRect = new Rect();
  private int mTouchSlop;
  private VelocityTracker mVelocityTracker;
  private float mVerticalScrollFactor;
  
  public NestedScrollView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public NestedScrollView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public NestedScrollView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initScrollView();
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, SCROLLVIEW_STYLEABLE, paramInt, 0);
    setFillViewport(paramContext.getBoolean(0, false));
    paramContext.recycle();
    this.mParentHelper = new NestedScrollingParentHelper(this);
    this.mChildHelper = new NestedScrollingChildHelper(this);
    setNestedScrollingEnabled(true);
    ViewCompat.setAccessibilityDelegate(this, ACCESSIBILITY_DELEGATE);
  }
  
  private boolean canScroll()
  {
    boolean bool = false;
    View localView = getChildAt(0);
    if (localView != null)
    {
      int i = localView.getHeight();
      if (getHeight() < getPaddingTop() + i + getPaddingBottom()) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  private static int clamp(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt2 >= paramInt3) || (paramInt1 < 0)) {
      return 0;
    }
    if (paramInt2 + paramInt1 > paramInt3) {
      return paramInt3 - paramInt2;
    }
    return paramInt1;
  }
  
  private void doScrollY(int paramInt)
  {
    if (paramInt != 0)
    {
      if (this.mSmoothScrollingEnabled) {
        smoothScrollBy(0, paramInt);
      }
    }
    else {
      return;
    }
    scrollBy(0, paramInt);
  }
  
  private void endDrag()
  {
    this.mIsBeingDragged = false;
    recycleVelocityTracker();
    stopNestedScroll();
    if (this.mEdgeGlowTop != null)
    {
      this.mEdgeGlowTop.onRelease();
      this.mEdgeGlowBottom.onRelease();
    }
  }
  
  private void ensureGlows()
  {
    if (getOverScrollMode() != 2)
    {
      if (this.mEdgeGlowTop == null)
      {
        Context localContext = getContext();
        this.mEdgeGlowTop = new EdgeEffectCompat(localContext);
        this.mEdgeGlowBottom = new EdgeEffectCompat(localContext);
      }
      return;
    }
    this.mEdgeGlowTop = null;
    this.mEdgeGlowBottom = null;
  }
  
  private View findFocusableViewInBounds(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    ArrayList localArrayList = getFocusables(2);
    Object localObject2 = null;
    int n = 0;
    int i1 = localArrayList.size();
    int m = 0;
    if (m < i1)
    {
      View localView = (View)localArrayList.get(m);
      int k = localView.getTop();
      int i2 = localView.getBottom();
      Object localObject1 = localObject2;
      int j = n;
      int i;
      if (paramInt1 < i2)
      {
        localObject1 = localObject2;
        j = n;
        if (k < paramInt2)
        {
          if ((paramInt1 >= k) || (i2 >= paramInt2)) {
            break label133;
          }
          i = 1;
          label103:
          if (localObject2 != null) {
            break label139;
          }
          localObject1 = localView;
          j = i;
        }
      }
      for (;;)
      {
        m += 1;
        localObject2 = localObject1;
        n = j;
        break;
        label133:
        i = 0;
        break label103;
        label139:
        if ((paramBoolean) && (k < ((View)localObject2).getTop())) {
          k = 1;
        }
        for (;;)
        {
          if (n == 0) {
            break label224;
          }
          localObject1 = localObject2;
          j = n;
          if (i == 0) {
            break;
          }
          localObject1 = localObject2;
          j = n;
          if (k == 0) {
            break;
          }
          localObject1 = localView;
          j = n;
          break;
          if ((!paramBoolean) && (i2 > ((View)localObject2).getBottom())) {
            k = 1;
          } else {
            k = 0;
          }
        }
        label224:
        if (i != 0)
        {
          localObject1 = localView;
          j = 1;
        }
        else
        {
          localObject1 = localObject2;
          j = n;
          if (k != 0)
          {
            localObject1 = localView;
            j = n;
          }
        }
      }
    }
    return (View)localObject2;
  }
  
  private void flingWithNestedDispatch(int paramInt)
  {
    int i = getScrollY();
    boolean bool;
    if ((i > 0) || (paramInt > 0)) {
      if ((i < getScrollRange()) || (paramInt < 0)) {
        bool = true;
      }
    }
    for (;;)
    {
      if (!dispatchNestedPreFling(0.0F, paramInt))
      {
        dispatchNestedFling(0.0F, paramInt, bool);
        if (bool) {
          fling(paramInt);
        }
      }
      return;
      bool = false;
      continue;
      bool = false;
    }
  }
  
  private float getVerticalScrollFactorCompat()
  {
    if (this.mVerticalScrollFactor == 0.0F)
    {
      TypedValue localTypedValue = new TypedValue();
      Context localContext = getContext();
      if (!localContext.getTheme().resolveAttribute(16842829, localTypedValue, true)) {
        throw new IllegalStateException("Expected theme to define listPreferredItemHeight.");
      }
      this.mVerticalScrollFactor = localTypedValue.getDimension(localContext.getResources().getDisplayMetrics());
    }
    return this.mVerticalScrollFactor;
  }
  
  private boolean inChild(int paramInt1, int paramInt2)
  {
    boolean bool2 = false;
    if (getChildCount() > 0)
    {
      int i = getScrollY();
      View localView = getChildAt(0);
      boolean bool1 = bool2;
      if (paramInt2 >= localView.getTop() - i)
      {
        bool1 = bool2;
        if (paramInt2 < localView.getBottom() - i)
        {
          bool1 = bool2;
          if (paramInt1 >= localView.getLeft())
          {
            bool1 = bool2;
            if (paramInt1 < localView.getRight()) {
              bool1 = true;
            }
          }
        }
      }
      return bool1;
    }
    return false;
  }
  
  private void initOrResetVelocityTracker()
  {
    if (this.mVelocityTracker == null)
    {
      this.mVelocityTracker = VelocityTracker.obtain();
      return;
    }
    this.mVelocityTracker.clear();
  }
  
  private void initScrollView()
  {
    this.mScroller = ScrollerCompat.create(getContext(), null);
    setFocusable(true);
    setDescendantFocusability(262144);
    setWillNotDraw(false);
    ViewConfiguration localViewConfiguration = ViewConfiguration.get(getContext());
    this.mTouchSlop = localViewConfiguration.getScaledTouchSlop();
    this.mMinimumVelocity = localViewConfiguration.getScaledMinimumFlingVelocity();
    this.mMaximumVelocity = localViewConfiguration.getScaledMaximumFlingVelocity();
  }
  
  private void initVelocityTrackerIfNotExists()
  {
    if (this.mVelocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
    }
  }
  
  private boolean isOffScreen(View paramView)
  {
    return !isWithinDeltaOfScreen(paramView, 0, getHeight());
  }
  
  private static boolean isViewDescendantOf(View paramView1, View paramView2)
  {
    if (paramView1 == paramView2) {
      return true;
    }
    paramView1 = paramView1.getParent();
    if ((paramView1 instanceof ViewGroup)) {
      return isViewDescendantOf((View)paramView1, paramView2);
    }
    return false;
  }
  
  private boolean isWithinDeltaOfScreen(View paramView, int paramInt1, int paramInt2)
  {
    boolean bool2 = false;
    paramView.getDrawingRect(this.mTempRect);
    offsetDescendantRectToMyCoords(paramView, this.mTempRect);
    boolean bool1 = bool2;
    if (this.mTempRect.bottom + paramInt1 >= getScrollY())
    {
      bool1 = bool2;
      if (this.mTempRect.top - paramInt1 <= getScrollY() + paramInt2) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private void onSecondaryPointerUp(MotionEvent paramMotionEvent)
  {
    int i = 0;
    int j = (paramMotionEvent.getAction() & 0xFF00) >> 8;
    if (paramMotionEvent.getPointerId(j) == this.mActivePointerId)
    {
      if (j == 0) {
        i = 1;
      }
      this.mLastMotionY = ((int)paramMotionEvent.getY(i));
      this.mActivePointerId = paramMotionEvent.getPointerId(i);
      if (this.mVelocityTracker != null) {
        this.mVelocityTracker.clear();
      }
    }
  }
  
  private void recycleVelocityTracker()
  {
    if (this.mVelocityTracker != null)
    {
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
    }
  }
  
  private boolean scrollAndFocus(int paramInt1, int paramInt2, int paramInt3)
  {
    boolean bool2 = true;
    int j = getHeight();
    int i = getScrollY();
    j = i + j;
    if (paramInt1 == 33) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      View localView = findFocusableViewInBounds(bool1, paramInt2, paramInt3);
      Object localObject = localView;
      if (localView == null) {
        localObject = this;
      }
      if ((paramInt2 < i) || (paramInt3 > j)) {
        break;
      }
      bool1 = false;
      if (localObject != findFocus()) {
        ((View)localObject).requestFocus(paramInt1);
      }
      return bool1;
    }
    if (bool1) {
      paramInt2 -= i;
    }
    for (;;)
    {
      doScrollY(paramInt2);
      bool1 = bool2;
      break;
      paramInt2 = paramInt3 - j;
    }
  }
  
  private void scrollToChild(View paramView)
  {
    paramView.getDrawingRect(this.mTempRect);
    offsetDescendantRectToMyCoords(paramView, this.mTempRect);
    int i = computeScrollDeltaToGetChildRectOnScreen(this.mTempRect);
    if (i != 0) {
      scrollBy(0, i);
    }
  }
  
  private boolean scrollToChildRect(Rect paramRect, boolean paramBoolean)
  {
    int i = computeScrollDeltaToGetChildRectOnScreen(paramRect);
    if (i != 0) {}
    for (boolean bool = true;; bool = false)
    {
      if (bool)
      {
        if (!paramBoolean) {
          break;
        }
        scrollBy(0, i);
      }
      return bool;
    }
    smoothScrollBy(0, i);
    return bool;
  }
  
  public void addView(View paramView)
  {
    if (getChildCount() > 0) {
      throw new IllegalStateException("ScrollView can host only one direct child");
    }
    super.addView(paramView);
  }
  
  public void addView(View paramView, int paramInt)
  {
    if (getChildCount() > 0) {
      throw new IllegalStateException("ScrollView can host only one direct child");
    }
    super.addView(paramView, paramInt);
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    if (getChildCount() > 0) {
      throw new IllegalStateException("ScrollView can host only one direct child");
    }
    super.addView(paramView, paramInt, paramLayoutParams);
  }
  
  public void addView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    if (getChildCount() > 0) {
      throw new IllegalStateException("ScrollView can host only one direct child");
    }
    super.addView(paramView, paramLayoutParams);
  }
  
  public boolean arrowScroll(int paramInt)
  {
    View localView2 = findFocus();
    View localView1 = localView2;
    if (localView2 == this) {
      localView1 = null;
    }
    localView2 = FocusFinder.getInstance().findNextFocus(this, localView1, paramInt);
    int k = getMaxScrollAmount();
    if ((localView2 != null) && (isWithinDeltaOfScreen(localView2, k, getHeight())))
    {
      localView2.getDrawingRect(this.mTempRect);
      offsetDescendantRectToMyCoords(localView2, this.mTempRect);
      doScrollY(computeScrollDeltaToGetChildRectOnScreen(this.mTempRect));
      localView2.requestFocus(paramInt);
      if ((localView1 != null) && (localView1.isFocused()) && (isOffScreen(localView1)))
      {
        paramInt = getDescendantFocusability();
        setDescendantFocusability(131072);
        requestFocus();
        setDescendantFocusability(paramInt);
      }
      return true;
    }
    int j = k;
    int i;
    if ((paramInt == 33) && (getScrollY() < k)) {
      i = getScrollY();
    }
    while (i == 0)
    {
      return false;
      i = j;
      if (paramInt == 130)
      {
        i = j;
        if (getChildCount() > 0)
        {
          int m = getChildAt(0).getBottom();
          int n = getScrollY() + getHeight() - getPaddingBottom();
          i = j;
          if (m - n < k) {
            i = m - n;
          }
        }
      }
    }
    if (paramInt == 130) {}
    for (;;)
    {
      doScrollY(i);
      break;
      i = -i;
    }
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public int computeHorizontalScrollExtent()
  {
    return super.computeHorizontalScrollExtent();
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public int computeHorizontalScrollOffset()
  {
    return super.computeHorizontalScrollOffset();
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public int computeHorizontalScrollRange()
  {
    return super.computeHorizontalScrollRange();
  }
  
  public void computeScroll()
  {
    int k;
    int n;
    int i1;
    int i;
    if (this.mScroller.computeScrollOffset())
    {
      int j = getScrollX();
      k = getScrollY();
      int m = this.mScroller.getCurrX();
      n = this.mScroller.getCurrY();
      if ((j != m) || (k != n))
      {
        i1 = getScrollRange();
        i = getOverScrollMode();
        if (i == 0) {
          break label132;
        }
        if ((i != 1) || (i1 <= 0)) {
          break label137;
        }
        i = 1;
        overScrollByCompat(m - j, n - k, j, k, 0, i1, 0, 0, false);
        if (i != 0)
        {
          ensureGlows();
          if ((n > 0) || (k <= 0)) {
            break label142;
          }
          this.mEdgeGlowTop.onAbsorb((int)this.mScroller.getCurrVelocity());
        }
      }
    }
    label132:
    label137:
    label142:
    while ((n < i1) || (k >= i1))
    {
      return;
      i = 1;
      break;
      i = 0;
      break;
    }
    this.mEdgeGlowBottom.onAbsorb((int)this.mScroller.getCurrVelocity());
  }
  
  protected int computeScrollDeltaToGetChildRectOnScreen(Rect paramRect)
  {
    if (getChildCount() == 0) {
      return 0;
    }
    int n = getHeight();
    int i = getScrollY();
    int k = i + n;
    int m = getVerticalFadingEdgeLength();
    int j = i;
    if (paramRect.top > 0) {
      j = i + m;
    }
    i = k;
    if (paramRect.bottom < getChildAt(0).getHeight()) {
      i = k - m;
    }
    m = 0;
    if ((paramRect.bottom > i) && (paramRect.top > j)) {
      if (paramRect.height() > n)
      {
        j = paramRect.top - j + 0;
        k = Math.min(j, getChildAt(0).getBottom() - i);
      }
    }
    do
    {
      do
      {
        return k;
        j = paramRect.bottom - i + 0;
        break;
        k = m;
      } while (paramRect.top >= j);
      k = m;
    } while (paramRect.bottom >= i);
    if (paramRect.height() > n) {}
    for (i = 0 - (i - paramRect.bottom);; i = 0 - (j - paramRect.top)) {
      return Math.max(i, -getScrollY());
    }
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public int computeVerticalScrollExtent()
  {
    return super.computeVerticalScrollExtent();
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public int computeVerticalScrollOffset()
  {
    return Math.max(0, super.computeVerticalScrollOffset());
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public int computeVerticalScrollRange()
  {
    int j = getChildCount();
    int i = getHeight() - getPaddingBottom() - getPaddingTop();
    if (j == 0) {
      return i;
    }
    j = getChildAt(0).getBottom();
    int k = getScrollY();
    int m = Math.max(0, j - i);
    if (k < 0) {
      i = j - k;
    }
    do
    {
      return i;
      i = j;
    } while (k <= m);
    return j + (k - m);
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if (!super.dispatchKeyEvent(paramKeyEvent)) {
      return executeKeyEvent(paramKeyEvent);
    }
    return true;
  }
  
  public boolean dispatchNestedFling(float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    return this.mChildHelper.dispatchNestedFling(paramFloat1, paramFloat2, paramBoolean);
  }
  
  public boolean dispatchNestedPreFling(float paramFloat1, float paramFloat2)
  {
    return this.mChildHelper.dispatchNestedPreFling(paramFloat1, paramFloat2);
  }
  
  public boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    return this.mChildHelper.dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2);
  }
  
  public boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    return this.mChildHelper.dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt);
  }
  
  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    if (this.mEdgeGlowTop != null)
    {
      int i = getScrollY();
      int j;
      int k;
      int m;
      if (!this.mEdgeGlowTop.isFinished())
      {
        j = paramCanvas.save();
        k = getWidth();
        m = getPaddingLeft();
        int n = getPaddingRight();
        paramCanvas.translate(getPaddingLeft(), Math.min(0, i));
        this.mEdgeGlowTop.setSize(k - m - n, getHeight());
        if (this.mEdgeGlowTop.draw(paramCanvas)) {
          ViewCompat.postInvalidateOnAnimation(this);
        }
        paramCanvas.restoreToCount(j);
      }
      if (!this.mEdgeGlowBottom.isFinished())
      {
        j = paramCanvas.save();
        k = getWidth() - getPaddingLeft() - getPaddingRight();
        m = getHeight();
        paramCanvas.translate(-k + getPaddingLeft(), Math.max(getScrollRange(), i) + m);
        paramCanvas.rotate(180.0F, k, 0.0F);
        this.mEdgeGlowBottom.setSize(k, m);
        if (this.mEdgeGlowBottom.draw(paramCanvas)) {
          ViewCompat.postInvalidateOnAnimation(this);
        }
        paramCanvas.restoreToCount(j);
      }
    }
  }
  
  public boolean executeKeyEvent(KeyEvent paramKeyEvent)
  {
    boolean bool2 = false;
    this.mTempRect.setEmpty();
    if (!canScroll())
    {
      if ((isFocused()) && (paramKeyEvent.getKeyCode() != 4))
      {
        View localView = findFocus();
        paramKeyEvent = localView;
        if (localView == this) {
          paramKeyEvent = null;
        }
        paramKeyEvent = FocusFinder.getInstance().findNextFocus(this, paramKeyEvent, 130);
        boolean bool1 = bool2;
        if (paramKeyEvent != null)
        {
          bool1 = bool2;
          if (paramKeyEvent != this) {
            bool1 = paramKeyEvent.requestFocus(130);
          }
        }
        return bool1;
      }
      return false;
    }
    if (paramKeyEvent.getAction() == 0) {}
    switch (paramKeyEvent.getKeyCode())
    {
    default: 
      return false;
    case 19: 
      if (!paramKeyEvent.isAltPressed()) {
        return arrowScroll(33);
      }
      return fullScroll(33);
    case 20: 
      if (!paramKeyEvent.isAltPressed()) {
        return arrowScroll(130);
      }
      return fullScroll(130);
    }
    if (paramKeyEvent.isShiftPressed()) {}
    for (int i = 33;; i = 130)
    {
      pageScroll(i);
      return false;
    }
  }
  
  public void fling(int paramInt)
  {
    if (getChildCount() > 0)
    {
      int i = getHeight() - getPaddingBottom() - getPaddingTop();
      int j = getChildAt(0).getHeight();
      this.mScroller.fling(getScrollX(), getScrollY(), 0, paramInt, 0, 0, 0, Math.max(0, j - i), 0, i / 2);
      ViewCompat.postInvalidateOnAnimation(this);
    }
  }
  
  public boolean fullScroll(int paramInt)
  {
    if (paramInt == 130) {}
    for (int i = 1;; i = 0)
    {
      int j = getHeight();
      this.mTempRect.top = 0;
      this.mTempRect.bottom = j;
      if (i != 0)
      {
        i = getChildCount();
        if (i > 0)
        {
          View localView = getChildAt(i - 1);
          this.mTempRect.bottom = (localView.getBottom() + getPaddingBottom());
          this.mTempRect.top = (this.mTempRect.bottom - j);
        }
      }
      return scrollAndFocus(paramInt, this.mTempRect.top, this.mTempRect.bottom);
    }
  }
  
  protected float getBottomFadingEdgeStrength()
  {
    if (getChildCount() == 0) {
      return 0.0F;
    }
    int i = getVerticalFadingEdgeLength();
    int j = getHeight();
    int k = getPaddingBottom();
    j = getChildAt(0).getBottom() - getScrollY() - (j - k);
    if (j < i) {
      return j / i;
    }
    return 1.0F;
  }
  
  public int getMaxScrollAmount()
  {
    return (int)(getHeight() * 0.5F);
  }
  
  public int getNestedScrollAxes()
  {
    return this.mParentHelper.getNestedScrollAxes();
  }
  
  int getScrollRange()
  {
    int i = 0;
    if (getChildCount() > 0) {
      i = Math.max(0, getChildAt(0).getHeight() - (getHeight() - getPaddingBottom() - getPaddingTop()));
    }
    return i;
  }
  
  protected float getTopFadingEdgeStrength()
  {
    if (getChildCount() == 0) {
      return 0.0F;
    }
    int i = getVerticalFadingEdgeLength();
    int j = getScrollY();
    if (j < i) {
      return j / i;
    }
    return 1.0F;
  }
  
  public boolean hasNestedScrollingParent()
  {
    return this.mChildHelper.hasNestedScrollingParent();
  }
  
  public boolean isFillViewport()
  {
    return this.mFillViewport;
  }
  
  public boolean isNestedScrollingEnabled()
  {
    return this.mChildHelper.isNestedScrollingEnabled();
  }
  
  public boolean isSmoothScrollingEnabled()
  {
    return this.mSmoothScrollingEnabled;
  }
  
  protected void measureChild(View paramView, int paramInt1, int paramInt2)
  {
    ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
    paramView.measure(getChildMeasureSpec(paramInt1, getPaddingLeft() + getPaddingRight(), localLayoutParams.width), View.MeasureSpec.makeMeasureSpec(0, 0));
  }
  
  protected void measureChildWithMargins(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    paramView.measure(getChildMeasureSpec(paramInt1, getPaddingLeft() + getPaddingRight() + localMarginLayoutParams.leftMargin + localMarginLayoutParams.rightMargin + paramInt2, localMarginLayoutParams.width), View.MeasureSpec.makeMeasureSpec(localMarginLayoutParams.topMargin + localMarginLayoutParams.bottomMargin, 0));
  }
  
  public void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.mIsLaidOut = false;
  }
  
  public boolean onGenericMotionEvent(MotionEvent paramMotionEvent)
  {
    if ((paramMotionEvent.getSource() & 0x2) != 0) {
      switch (paramMotionEvent.getAction())
      {
      }
    }
    for (;;)
    {
      return false;
      if (!this.mIsBeingDragged)
      {
        float f = MotionEventCompat.getAxisValue(paramMotionEvent, 9);
        if (f != 0.0F)
        {
          int i = (int)(getVerticalScrollFactorCompat() * f);
          int j = getScrollRange();
          int m = getScrollY();
          int k = m - i;
          if (k < 0) {
            i = 0;
          }
          while (i != m)
          {
            super.scrollTo(getScrollX(), i);
            return true;
            i = k;
            if (k > j) {
              i = j;
            }
          }
        }
      }
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool = false;
    int i = paramMotionEvent.getAction();
    if ((i == 2) && (this.mIsBeingDragged)) {
      return true;
    }
    switch (i & 0xFF)
    {
    }
    for (;;)
    {
      return this.mIsBeingDragged;
      i = this.mActivePointerId;
      if (i != -1)
      {
        int j = paramMotionEvent.findPointerIndex(i);
        if (j == -1)
        {
          Log.e("NestedScrollView", "Invalid pointerId=" + i + " in onInterceptTouchEvent");
        }
        else
        {
          i = (int)paramMotionEvent.getY(j);
          if ((Math.abs(i - this.mLastMotionY) > this.mTouchSlop) && ((getNestedScrollAxes() & 0x2) == 0))
          {
            this.mIsBeingDragged = true;
            this.mLastMotionY = i;
            initVelocityTrackerIfNotExists();
            this.mVelocityTracker.addMovement(paramMotionEvent);
            this.mNestedYOffset = 0;
            paramMotionEvent = getParent();
            if (paramMotionEvent != null)
            {
              paramMotionEvent.requestDisallowInterceptTouchEvent(true);
              continue;
              i = (int)paramMotionEvent.getY();
              if (!inChild((int)paramMotionEvent.getX(), i))
              {
                this.mIsBeingDragged = false;
                recycleVelocityTracker();
              }
              else
              {
                this.mLastMotionY = i;
                this.mActivePointerId = paramMotionEvent.getPointerId(0);
                initOrResetVelocityTracker();
                this.mVelocityTracker.addMovement(paramMotionEvent);
                this.mScroller.computeScrollOffset();
                if (this.mScroller.isFinished()) {}
                for (;;)
                {
                  this.mIsBeingDragged = bool;
                  startNestedScroll(2);
                  break;
                  bool = true;
                }
                this.mIsBeingDragged = false;
                this.mActivePointerId = -1;
                recycleVelocityTracker();
                if (this.mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange())) {
                  ViewCompat.postInvalidateOnAnimation(this);
                }
                stopNestedScroll();
                continue;
                onSecondaryPointerUp(paramMotionEvent);
              }
            }
          }
        }
      }
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    this.mIsLayoutDirty = false;
    if ((this.mChildToScrollTo != null) && (isViewDescendantOf(this.mChildToScrollTo, this))) {
      scrollToChild(this.mChildToScrollTo);
    }
    this.mChildToScrollTo = null;
    if (!this.mIsLaidOut)
    {
      if (this.mSavedState != null)
      {
        scrollTo(getScrollX(), this.mSavedState.scrollPosition);
        this.mSavedState = null;
      }
      if (getChildCount() <= 0) {
        break label153;
      }
      paramInt1 = getChildAt(0).getMeasuredHeight();
      paramInt1 = Math.max(0, paramInt1 - (paramInt4 - paramInt2 - getPaddingBottom() - getPaddingTop()));
      if (getScrollY() <= paramInt1) {
        break label158;
      }
      scrollTo(getScrollX(), paramInt1);
    }
    for (;;)
    {
      scrollTo(getScrollX(), getScrollY());
      this.mIsLaidOut = true;
      return;
      label153:
      paramInt1 = 0;
      break;
      label158:
      if (getScrollY() < 0) {
        scrollTo(getScrollX(), 0);
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    if (!this.mFillViewport) {
      return;
    }
    if (View.MeasureSpec.getMode(paramInt2) == 0) {
      return;
    }
    if (getChildCount() > 0)
    {
      View localView = getChildAt(0);
      paramInt2 = getMeasuredHeight();
      if (localView.getMeasuredHeight() < paramInt2)
      {
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
        localView.measure(getChildMeasureSpec(paramInt1, getPaddingLeft() + getPaddingRight(), localLayoutParams.width), View.MeasureSpec.makeMeasureSpec(paramInt2 - getPaddingTop() - getPaddingBottom(), 1073741824));
      }
    }
  }
  
  public boolean onNestedFling(View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      flingWithNestedDispatch((int)paramFloat2);
      return true;
    }
    return false;
  }
  
  public boolean onNestedPreFling(View paramView, float paramFloat1, float paramFloat2)
  {
    return dispatchNestedPreFling(paramFloat1, paramFloat2);
  }
  
  public void onNestedPreScroll(View paramView, int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfInt, null);
  }
  
  public void onNestedScroll(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt1 = getScrollY();
    scrollBy(0, paramInt4);
    paramInt1 = getScrollY() - paramInt1;
    dispatchNestedScroll(0, paramInt1, 0, paramInt4 - paramInt1, null);
  }
  
  public void onNestedScrollAccepted(View paramView1, View paramView2, int paramInt)
  {
    this.mParentHelper.onNestedScrollAccepted(paramView1, paramView2, paramInt);
    startNestedScroll(2);
  }
  
  protected void onOverScrolled(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    super.scrollTo(paramInt1, paramInt2);
  }
  
  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect)
  {
    int i;
    if (paramInt == 2)
    {
      i = 130;
      if (paramRect != null) {
        break label44;
      }
    }
    label44:
    for (View localView = FocusFinder.getInstance().findNextFocus(this, null, i);; localView = FocusFinder.getInstance().findNextFocusFromRect(this, paramRect, i))
    {
      if (localView != null) {
        break label58;
      }
      return false;
      i = paramInt;
      if (paramInt != 1) {
        break;
      }
      i = 33;
      break;
    }
    label58:
    if (isOffScreen(localView)) {
      return false;
    }
    return localView.requestFocus(i, paramRect);
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
    this.mSavedState = paramParcelable;
    requestLayout();
  }
  
  protected Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    localSavedState.scrollPosition = getScrollY();
    return localSavedState;
  }
  
  protected void onScrollChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onScrollChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (this.mOnScrollChangeListener != null) {
      this.mOnScrollChangeListener.onScrollChange(this, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    View localView = findFocus();
    if ((localView == null) || (this == localView)) {
      return;
    }
    if (isWithinDeltaOfScreen(localView, 0, paramInt4))
    {
      localView.getDrawingRect(this.mTempRect);
      offsetDescendantRectToMyCoords(localView, this.mTempRect);
      doScrollY(computeScrollDeltaToGetChildRectOnScreen(this.mTempRect));
    }
  }
  
  public boolean onStartNestedScroll(View paramView1, View paramView2, int paramInt)
  {
    boolean bool = false;
    if ((paramInt & 0x2) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public void onStopNestedScroll(View paramView)
  {
    this.mParentHelper.onStopNestedScroll(paramView);
    stopNestedScroll();
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    initVelocityTrackerIfNotExists();
    MotionEvent localMotionEvent = MotionEvent.obtain(paramMotionEvent);
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    if (i == 0) {
      this.mNestedYOffset = 0;
    }
    localMotionEvent.offsetLocation(0.0F, this.mNestedYOffset);
    switch (i)
    {
    }
    for (;;)
    {
      if (this.mVelocityTracker != null) {
        this.mVelocityTracker.addMovement(localMotionEvent);
      }
      localMotionEvent.recycle();
      return true;
      if (getChildCount() == 0) {
        return false;
      }
      if (this.mScroller.isFinished()) {}
      ViewParent localViewParent;
      for (boolean bool = false;; bool = true)
      {
        this.mIsBeingDragged = bool;
        if (bool)
        {
          localViewParent = getParent();
          if (localViewParent != null) {
            localViewParent.requestDisallowInterceptTouchEvent(true);
          }
        }
        if (!this.mScroller.isFinished()) {
          this.mScroller.abortAnimation();
        }
        this.mLastMotionY = ((int)paramMotionEvent.getY());
        this.mActivePointerId = paramMotionEvent.getPointerId(0);
        startNestedScroll(2);
        break;
      }
      int k = paramMotionEvent.findPointerIndex(this.mActivePointerId);
      if (k == -1)
      {
        Log.e("NestedScrollView", "Invalid pointerId=" + this.mActivePointerId + " in onTouchEvent");
      }
      else
      {
        int m = (int)paramMotionEvent.getY(k);
        i = this.mLastMotionY - m;
        int j = i;
        if (dispatchNestedPreScroll(0, i, this.mScrollConsumed, this.mScrollOffset))
        {
          j = i - this.mScrollConsumed[1];
          localMotionEvent.offsetLocation(0.0F, this.mScrollOffset[1]);
          this.mNestedYOffset += this.mScrollOffset[1];
        }
        i = j;
        if (!this.mIsBeingDragged)
        {
          i = j;
          if (Math.abs(j) > this.mTouchSlop)
          {
            localViewParent = getParent();
            if (localViewParent != null) {
              localViewParent.requestDisallowInterceptTouchEvent(true);
            }
            this.mIsBeingDragged = true;
            if (j <= 0) {
              break label543;
            }
            i = j - this.mTouchSlop;
          }
        }
        label389:
        if (this.mIsBeingDragged)
        {
          this.mLastMotionY = (m - this.mScrollOffset[1]);
          int n = getScrollY();
          m = getScrollRange();
          j = getOverScrollMode();
          if (j != 0)
          {
            if ((j != 1) || (m <= 0)) {
              break label558;
            }
            j = 1;
            label442:
            if ((overScrollByCompat(0, i, 0, getScrollY(), 0, m, 0, 0, true)) && (!hasNestedScrollingParent())) {
              break label563;
            }
          }
          for (;;)
          {
            int i1 = getScrollY() - n;
            if (!dispatchNestedScroll(0, i1, 0, i - i1, this.mScrollOffset)) {
              break label573;
            }
            this.mLastMotionY -= this.mScrollOffset[1];
            localMotionEvent.offsetLocation(0.0F, this.mScrollOffset[1]);
            this.mNestedYOffset += this.mScrollOffset[1];
            break;
            label543:
            i = j + this.mTouchSlop;
            break label389;
            j = 1;
            break label442;
            label558:
            j = 0;
            break label442;
            label563:
            this.mVelocityTracker.clear();
          }
          label573:
          if (j != 0)
          {
            ensureGlows();
            j = n + i;
            if (j < 0)
            {
              this.mEdgeGlowTop.onPull(i / getHeight(), paramMotionEvent.getX(k) / getWidth());
              if (!this.mEdgeGlowBottom.isFinished()) {
                this.mEdgeGlowBottom.onRelease();
              }
            }
            while ((this.mEdgeGlowTop != null) && ((!this.mEdgeGlowTop.isFinished()) || (!this.mEdgeGlowBottom.isFinished())))
            {
              ViewCompat.postInvalidateOnAnimation(this);
              break;
              if (j > m)
              {
                this.mEdgeGlowBottom.onPull(i / getHeight(), 1.0F - paramMotionEvent.getX(k) / getWidth());
                if (!this.mEdgeGlowTop.isFinished()) {
                  this.mEdgeGlowTop.onRelease();
                }
              }
            }
            if (this.mIsBeingDragged)
            {
              paramMotionEvent = this.mVelocityTracker;
              paramMotionEvent.computeCurrentVelocity(1000, this.mMaximumVelocity);
              i = (int)VelocityTrackerCompat.getYVelocity(paramMotionEvent, this.mActivePointerId);
              if (Math.abs(i) <= this.mMinimumVelocity) {
                break label790;
              }
              flingWithNestedDispatch(-i);
            }
            for (;;)
            {
              this.mActivePointerId = -1;
              endDrag();
              break;
              label790:
              if (this.mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange())) {
                ViewCompat.postInvalidateOnAnimation(this);
              }
            }
            if ((this.mIsBeingDragged) && (getChildCount() > 0) && (this.mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange()))) {
              ViewCompat.postInvalidateOnAnimation(this);
            }
            this.mActivePointerId = -1;
            endDrag();
            continue;
            i = MotionEventCompat.getActionIndex(paramMotionEvent);
            this.mLastMotionY = ((int)paramMotionEvent.getY(i));
            this.mActivePointerId = paramMotionEvent.getPointerId(i);
            continue;
            onSecondaryPointerUp(paramMotionEvent);
            this.mLastMotionY = ((int)paramMotionEvent.getY(paramMotionEvent.findPointerIndex(this.mActivePointerId)));
          }
        }
      }
    }
  }
  
  boolean overScrollByCompat(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, boolean paramBoolean)
  {
    int k = getOverScrollMode();
    int i;
    int j;
    label34:
    label45:
    label56:
    label115:
    boolean bool;
    if (computeHorizontalScrollRange() > computeHorizontalScrollExtent())
    {
      i = 1;
      if (computeVerticalScrollRange() <= computeVerticalScrollExtent()) {
        break label177;
      }
      j = 1;
      if (k == 0) {
        break label183;
      }
      if (k != 1) {
        break label189;
      }
      if (k == 0) {
        break label195;
      }
      if (k != 1) {
        break label201;
      }
      paramInt3 += paramInt1;
      if (i == 0) {
        paramInt7 = 0;
      }
      paramInt4 += paramInt2;
      if (j == 0) {
        paramInt8 = 0;
      }
      paramInt2 = -paramInt7;
      paramInt1 = paramInt7 + paramInt5;
      paramInt5 = -paramInt8;
      paramInt6 = paramInt8 + paramInt6;
      paramBoolean = false;
      if (paramInt3 <= paramInt1) {
        break label207;
      }
      paramBoolean = true;
      bool = false;
      if (paramInt4 <= paramInt6) {
        break label222;
      }
      paramInt2 = paramInt6;
      bool = true;
    }
    for (;;)
    {
      if (bool) {
        this.mScroller.springBack(paramInt1, paramInt2, 0, 0, 0, getScrollRange());
      }
      onOverScrolled(paramInt1, paramInt2, paramBoolean, bool);
      if (paramBoolean) {
        break label241;
      }
      return bool;
      i = 0;
      break;
      label177:
      j = 0;
      break label34;
      label183:
      i = 1;
      break label45;
      label189:
      i = 0;
      break label45;
      label195:
      j = 1;
      break label56;
      label201:
      j = 0;
      break label56;
      label207:
      paramInt1 = paramInt3;
      if (paramInt3 >= paramInt2) {
        break label115;
      }
      paramInt1 = paramInt2;
      paramBoolean = true;
      break label115;
      label222:
      paramInt2 = paramInt4;
      if (paramInt4 < paramInt5)
      {
        paramInt2 = paramInt5;
        bool = true;
      }
    }
    label241:
    return true;
  }
  
  public boolean pageScroll(int paramInt)
  {
    int i;
    int j;
    if (paramInt == 130)
    {
      i = 1;
      j = getHeight();
      if (i == 0) {
        break label121;
      }
      this.mTempRect.top = (getScrollY() + j);
      i = getChildCount();
      if (i > 0)
      {
        View localView = getChildAt(i - 1);
        if (this.mTempRect.top + j > localView.getBottom()) {
          this.mTempRect.top = (localView.getBottom() - j);
        }
      }
    }
    for (;;)
    {
      this.mTempRect.bottom = (this.mTempRect.top + j);
      return scrollAndFocus(paramInt, this.mTempRect.top, this.mTempRect.bottom);
      i = 0;
      break;
      label121:
      this.mTempRect.top = (getScrollY() - j);
      if (this.mTempRect.top < 0) {
        this.mTempRect.top = 0;
      }
    }
  }
  
  public void requestChildFocus(View paramView1, View paramView2)
  {
    if (!this.mIsLayoutDirty) {
      scrollToChild(paramView2);
    }
    for (;;)
    {
      super.requestChildFocus(paramView1, paramView2);
      return;
      this.mChildToScrollTo = paramView2;
    }
  }
  
  public boolean requestChildRectangleOnScreen(View paramView, Rect paramRect, boolean paramBoolean)
  {
    paramRect.offset(paramView.getLeft() - paramView.getScrollX(), paramView.getTop() - paramView.getScrollY());
    return scrollToChildRect(paramRect, paramBoolean);
  }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
  {
    if (paramBoolean) {
      recycleVelocityTracker();
    }
    super.requestDisallowInterceptTouchEvent(paramBoolean);
  }
  
  public void requestLayout()
  {
    this.mIsLayoutDirty = true;
    super.requestLayout();
  }
  
  public void scrollTo(int paramInt1, int paramInt2)
  {
    if (getChildCount() > 0)
    {
      View localView = getChildAt(0);
      paramInt1 = clamp(paramInt1, getWidth() - getPaddingRight() - getPaddingLeft(), localView.getWidth());
      paramInt2 = clamp(paramInt2, getHeight() - getPaddingBottom() - getPaddingTop(), localView.getHeight());
      if ((paramInt1 != getScrollX()) || (paramInt2 != getScrollY())) {
        super.scrollTo(paramInt1, paramInt2);
      }
    }
  }
  
  public void setFillViewport(boolean paramBoolean)
  {
    if (paramBoolean != this.mFillViewport)
    {
      this.mFillViewport = paramBoolean;
      requestLayout();
    }
  }
  
  public void setNestedScrollingEnabled(boolean paramBoolean)
  {
    this.mChildHelper.setNestedScrollingEnabled(paramBoolean);
  }
  
  public void setOnScrollChangeListener(OnScrollChangeListener paramOnScrollChangeListener)
  {
    this.mOnScrollChangeListener = paramOnScrollChangeListener;
  }
  
  public void setSmoothScrollingEnabled(boolean paramBoolean)
  {
    this.mSmoothScrollingEnabled = paramBoolean;
  }
  
  public boolean shouldDelayChildPressedState()
  {
    return true;
  }
  
  public final void smoothScrollBy(int paramInt1, int paramInt2)
  {
    if (getChildCount() == 0) {
      return;
    }
    if (AnimationUtils.currentAnimationTimeMillis() - this.mLastScroll > 250L)
    {
      paramInt1 = getHeight();
      int i = getPaddingBottom();
      int j = getPaddingTop();
      i = Math.max(0, getChildAt(0).getHeight() - (paramInt1 - i - j));
      paramInt1 = getScrollY();
      paramInt2 = Math.max(0, Math.min(paramInt1 + paramInt2, i));
      this.mScroller.startScroll(getScrollX(), paramInt1, 0, paramInt2 - paramInt1);
      ViewCompat.postInvalidateOnAnimation(this);
    }
    for (;;)
    {
      this.mLastScroll = AnimationUtils.currentAnimationTimeMillis();
      return;
      if (!this.mScroller.isFinished()) {
        this.mScroller.abortAnimation();
      }
      scrollBy(paramInt1, paramInt2);
    }
  }
  
  public final void smoothScrollTo(int paramInt1, int paramInt2)
  {
    smoothScrollBy(paramInt1 - getScrollX(), paramInt2 - getScrollY());
  }
  
  public boolean startNestedScroll(int paramInt)
  {
    return this.mChildHelper.startNestedScroll(paramInt);
  }
  
  public void stopNestedScroll()
  {
    this.mChildHelper.stopNestedScroll();
  }
  
  static class AccessibilityDelegate
    extends AccessibilityDelegateCompat
  {
    public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      boolean bool = false;
      super.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
      paramView = (NestedScrollView)paramView;
      paramAccessibilityEvent.setClassName(ScrollView.class.getName());
      paramAccessibilityEvent = AccessibilityEventCompat.asRecord(paramAccessibilityEvent);
      if (paramView.getScrollRange() > 0) {
        bool = true;
      }
      paramAccessibilityEvent.setScrollable(bool);
      paramAccessibilityEvent.setScrollX(paramView.getScrollX());
      paramAccessibilityEvent.setScrollY(paramView.getScrollY());
      paramAccessibilityEvent.setMaxScrollX(paramView.getScrollX());
      paramAccessibilityEvent.setMaxScrollY(paramView.getScrollRange());
    }
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat);
      paramView = (NestedScrollView)paramView;
      paramAccessibilityNodeInfoCompat.setClassName(ScrollView.class.getName());
      if (paramView.isEnabled())
      {
        int i = paramView.getScrollRange();
        if (i > 0)
        {
          paramAccessibilityNodeInfoCompat.setScrollable(true);
          if (paramView.getScrollY() > 0) {
            paramAccessibilityNodeInfoCompat.addAction(8192);
          }
          if (paramView.getScrollY() < i) {
            paramAccessibilityNodeInfoCompat.addAction(4096);
          }
        }
      }
    }
    
    public boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
    {
      if (super.performAccessibilityAction(paramView, paramInt, paramBundle)) {
        return true;
      }
      paramView = (NestedScrollView)paramView;
      if (!paramView.isEnabled()) {
        return false;
      }
      switch (paramInt)
      {
      default: 
        return false;
      case 4096: 
        paramInt = paramView.getHeight();
        i = paramView.getPaddingBottom();
        j = paramView.getPaddingTop();
        paramInt = Math.min(paramView.getScrollY() + (paramInt - i - j), paramView.getScrollRange());
        if (paramInt != paramView.getScrollY())
        {
          paramView.smoothScrollTo(0, paramInt);
          return true;
        }
        return false;
      }
      paramInt = paramView.getHeight();
      int i = paramView.getPaddingBottom();
      int j = paramView.getPaddingTop();
      paramInt = Math.max(paramView.getScrollY() - (paramInt - i - j), 0);
      if (paramInt != paramView.getScrollY())
      {
        paramView.smoothScrollTo(0, paramInt);
        return true;
      }
      return false;
    }
  }
  
  public static abstract interface OnScrollChangeListener
  {
    public abstract void onScrollChange(NestedScrollView paramNestedScrollView, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public NestedScrollView.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new NestedScrollView.SavedState(paramAnonymousParcel);
      }
      
      public NestedScrollView.SavedState[] newArray(int paramAnonymousInt)
      {
        return new NestedScrollView.SavedState[paramAnonymousInt];
      }
    };
    public int scrollPosition;
    
    SavedState(Parcel paramParcel)
    {
      super();
      this.scrollPosition = paramParcel.readInt();
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public String toString()
    {
      return "HorizontalScrollView.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " scrollPosition=" + this.scrollPosition + "}";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.scrollPosition);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\widget\NestedScrollView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */