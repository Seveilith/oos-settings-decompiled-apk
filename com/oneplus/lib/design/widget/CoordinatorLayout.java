package com.oneplus.lib.design.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import com.oneplus.commonctrl.R.style;
import com.oneplus.commonctrl.R.styleable;
import com.oneplus.lib.util.MathUtils;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoordinatorLayout
  extends ViewGroup
  implements NestedScrollingParent
{
  static final Class<?>[] CONSTRUCTOR_PARAMS;
  static final int EVENT_NESTED_SCROLL = 1;
  static final int EVENT_PRE_DRAW = 0;
  static final int EVENT_VIEW_REMOVED = 2;
  static final String TAG = "CoordinatorLayout";
  static final Comparator<View> TOP_SORTED_CHILDREN_COMPARATOR;
  private static final int TYPE_ON_INTERCEPT = 0;
  private static final int TYPE_ON_TOUCH = 1;
  static final String WIDGET_PACKAGE_NAME;
  static final ThreadLocal<Map<String, Constructor<Behavior>>> sConstructors;
  private OnApplyWindowInsetsListener mApplyWindowInsetsListener;
  private View mBehaviorTouchView;
  private final DirectedAcyclicGraph<View> mChildDag = new DirectedAcyclicGraph();
  private final List<View> mDependencySortedChildren = new ArrayList();
  private boolean mDisallowInterceptReset;
  private boolean mDrawStatusBarBackground;
  private boolean mIsAttachedToWindow;
  private int[] mKeylines;
  private WindowInsetsCompat mLastInsets;
  private boolean mNeedsPreDrawListener;
  private View mNestedScrollingDirectChild;
  private final NestedScrollingParentHelper mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
  private View mNestedScrollingTarget;
  ViewGroup.OnHierarchyChangeListener mOnHierarchyChangeListener;
  private OnPreDrawListener mOnPreDrawListener;
  private Paint mScrimPaint;
  private Drawable mStatusBarBackground;
  private final List<View> mTempDependenciesList = new ArrayList();
  private final int[] mTempIntPair = new int[2];
  private final List<View> mTempList1 = new ArrayList();
  private final Rect mTempRect1 = new Rect();
  private final Rect mTempRect2 = new Rect();
  private final Rect mTempRect3 = new Rect();
  private final Rect mTempRect4 = new Rect();
  private final Rect mTempRect5 = new Rect();
  
  static
  {
    Object localObject = CoordinatorLayout.class.getPackage();
    if (localObject != null)
    {
      localObject = ((Package)localObject).getName();
      WIDGET_PACKAGE_NAME = (String)localObject;
      if (Build.VERSION.SDK_INT < 21) {
        break label70;
      }
    }
    label70:
    for (TOP_SORTED_CHILDREN_COMPARATOR = new ViewElevationComparator();; TOP_SORTED_CHILDREN_COMPARATOR = null)
    {
      CONSTRUCTOR_PARAMS = new Class[] { Context.class, AttributeSet.class };
      sConstructors = new ThreadLocal();
      return;
      localObject = null;
      break;
    }
  }
  
  public CoordinatorLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public CoordinatorLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public CoordinatorLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.OpCoordinatorLayout, paramInt, R.style.Widget_Design_CoordinatorLayout);
    paramInt = paramAttributeSet.getResourceId(R.styleable.OpCoordinatorLayout_opKeylines, 0);
    if (paramInt != 0)
    {
      paramContext = paramContext.getResources();
      this.mKeylines = paramContext.getIntArray(paramInt);
      float f = paramContext.getDisplayMetrics().density;
      int i = this.mKeylines.length;
      paramInt = 0;
      while (paramInt < i)
      {
        paramContext = this.mKeylines;
        paramContext[paramInt] = ((int)(paramContext[paramInt] * f));
        paramInt += 1;
      }
    }
    this.mStatusBarBackground = paramAttributeSet.getDrawable(R.styleable.OpCoordinatorLayout_opStatusBarBackground);
    paramAttributeSet.recycle();
    setupForInsets();
    super.setOnHierarchyChangeListener(new HierarchyChangeListener());
  }
  
  private void constrainChildRect(LayoutParams paramLayoutParams, Rect paramRect, int paramInt1, int paramInt2)
  {
    int j = getWidth();
    int i = getHeight();
    j = Math.max(getPaddingLeft() + paramLayoutParams.leftMargin, Math.min(paramRect.left, j - getPaddingRight() - paramInt1 - paramLayoutParams.rightMargin));
    i = Math.max(getPaddingTop() + paramLayoutParams.topMargin, Math.min(paramRect.top, i - getPaddingBottom() - paramInt2 - paramLayoutParams.bottomMargin));
    paramRect.set(j, i, j + paramInt1, i + paramInt2);
  }
  
  private WindowInsetsCompat dispatchApplyWindowInsetsToBehaviors(WindowInsetsCompat paramWindowInsetsCompat)
  {
    if (paramWindowInsetsCompat.isConsumed()) {
      return paramWindowInsetsCompat;
    }
    int i = 0;
    int j = getChildCount();
    for (;;)
    {
      WindowInsetsCompat localWindowInsetsCompat = paramWindowInsetsCompat;
      if (i < j)
      {
        View localView = getChildAt(i);
        localWindowInsetsCompat = paramWindowInsetsCompat;
        if (ViewCompat.getFitsSystemWindows(localView))
        {
          Behavior localBehavior = ((LayoutParams)localView.getLayoutParams()).getBehavior();
          localWindowInsetsCompat = paramWindowInsetsCompat;
          if (localBehavior != null)
          {
            paramWindowInsetsCompat = localBehavior.onApplyWindowInsets(this, localView, paramWindowInsetsCompat);
            localWindowInsetsCompat = paramWindowInsetsCompat;
            if (paramWindowInsetsCompat.isConsumed()) {
              localWindowInsetsCompat = paramWindowInsetsCompat;
            }
          }
        }
      }
      else
      {
        return localWindowInsetsCompat;
      }
      i += 1;
      paramWindowInsetsCompat = localWindowInsetsCompat;
    }
  }
  
  private void getDesiredAnchoredChildRectWithoutConstraints(View paramView, int paramInt1, Rect paramRect1, Rect paramRect2, LayoutParams paramLayoutParams, int paramInt2, int paramInt3)
  {
    int k = GravityCompat.getAbsoluteGravity(resolveAnchoredChildGravity(paramLayoutParams.gravity), paramInt1);
    int i = GravityCompat.getAbsoluteGravity(resolveGravity(paramLayoutParams.anchorGravity), paramInt1);
    label118:
    int j;
    switch (i & 0x7)
    {
    case 2: 
    case 3: 
    case 4: 
    default: 
      paramInt1 = paramRect1.left;
      switch (i & 0x70)
      {
      case 48: 
      default: 
        i = paramRect1.top;
        j = paramInt1;
        switch (k & 0x7)
        {
        case 2: 
        case 3: 
        case 4: 
        default: 
          j = paramInt1 - paramInt2;
        case 5: 
          label166:
          paramInt1 = i;
          switch (k & 0x70)
          {
          }
          break;
        }
        break;
      }
      break;
    }
    for (paramInt1 = i - paramInt3;; paramInt1 = i - paramInt3 / 2)
    {
      paramRect2.set(j, paramInt1, j + paramInt2, paramInt1 + paramInt3);
      return;
      paramInt1 = paramRect1.right;
      break;
      paramInt1 = paramRect1.left + paramRect1.width() / 2;
      break;
      i = paramRect1.bottom;
      break label118;
      i = paramRect1.top + paramRect1.height() / 2;
      break label118;
      j = paramInt1 - paramInt2 / 2;
      break label166;
    }
  }
  
  private int getKeyline(int paramInt)
  {
    if (this.mKeylines == null)
    {
      Log.e("CoordinatorLayout", "No keylines defined for " + this + " - attempted index lookup " + paramInt);
      return 0;
    }
    if ((paramInt < 0) || (paramInt >= this.mKeylines.length))
    {
      Log.e("CoordinatorLayout", "Keyline index " + paramInt + " out of range for " + this);
      return 0;
    }
    return this.mKeylines[paramInt];
  }
  
  private void getTopSortedChildren(List<View> paramList)
  {
    paramList.clear();
    boolean bool = isChildrenDrawingOrderEnabled();
    int k = getChildCount();
    int i = k - 1;
    if (i >= 0)
    {
      if (bool) {}
      for (int j = getChildDrawingOrder(k, i);; j = i)
      {
        paramList.add(getChildAt(j));
        i -= 1;
        break;
      }
    }
    if (TOP_SORTED_CHILDREN_COMPARATOR != null) {
      Collections.sort(paramList, TOP_SORTED_CHILDREN_COMPARATOR);
    }
  }
  
  private boolean hasDependencies(View paramView)
  {
    return this.mChildDag.hasOutgoingEdges(paramView);
  }
  
  private void layoutChild(View paramView, int paramInt)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    Rect localRect1 = this.mTempRect1;
    localRect1.set(getPaddingLeft() + localLayoutParams.leftMargin, getPaddingTop() + localLayoutParams.topMargin, getWidth() - getPaddingRight() - localLayoutParams.rightMargin, getHeight() - getPaddingBottom() - localLayoutParams.bottomMargin);
    if ((this.mLastInsets == null) || (!ViewCompat.getFitsSystemWindows(this)) || (ViewCompat.getFitsSystemWindows(paramView))) {}
    for (;;)
    {
      Rect localRect2 = this.mTempRect2;
      GravityCompat.apply(resolveGravity(localLayoutParams.gravity), paramView.getMeasuredWidth(), paramView.getMeasuredHeight(), localRect1, localRect2, paramInt);
      paramView.layout(localRect2.left, localRect2.top, localRect2.right, localRect2.bottom);
      return;
      localRect1.left += this.mLastInsets.getSystemWindowInsetLeft();
      localRect1.top += this.mLastInsets.getSystemWindowInsetTop();
      localRect1.right -= this.mLastInsets.getSystemWindowInsetRight();
      localRect1.bottom -= this.mLastInsets.getSystemWindowInsetBottom();
    }
  }
  
  private void layoutChildWithAnchor(View paramView1, View paramView2, int paramInt)
  {
    Object localObject = (LayoutParams)paramView1.getLayoutParams();
    localObject = this.mTempRect1;
    Rect localRect = this.mTempRect2;
    getDescendantRect(paramView2, (Rect)localObject);
    getDesiredAnchoredChildRect(paramView1, paramInt, (Rect)localObject, localRect);
    paramView1.layout(localRect.left, localRect.top, localRect.right, localRect.bottom);
  }
  
  private void layoutChildWithKeyline(View paramView, int paramInt1, int paramInt2)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int i1 = GravityCompat.getAbsoluteGravity(resolveKeylineGravity(localLayoutParams.gravity), paramInt2);
    int n = getWidth();
    int m = getHeight();
    int j = paramView.getMeasuredWidth();
    int k = paramView.getMeasuredHeight();
    int i = paramInt1;
    if (paramInt2 == 1) {
      i = n - paramInt1;
    }
    paramInt2 = getKeyline(i) - j;
    i = 0;
    paramInt1 = paramInt2;
    switch (i1 & 0x7)
    {
    default: 
      paramInt1 = paramInt2;
    case 2: 
    case 3: 
    case 4: 
      paramInt2 = i;
      switch (i1 & 0x70)
      {
      default: 
        paramInt2 = i;
      }
      break;
    }
    for (;;)
    {
      paramInt1 = Math.max(getPaddingLeft() + localLayoutParams.leftMargin, Math.min(paramInt1, n - getPaddingRight() - j - localLayoutParams.rightMargin));
      paramInt2 = Math.max(getPaddingTop() + localLayoutParams.topMargin, Math.min(paramInt2, m - getPaddingBottom() - k - localLayoutParams.bottomMargin));
      paramView.layout(paramInt1, paramInt2, paramInt1 + j, paramInt2 + k);
      return;
      paramInt1 = paramInt2 + j;
      break;
      paramInt1 = paramInt2 + j / 2;
      break;
      paramInt2 = k + 0;
      continue;
      paramInt2 = k / 2 + 0;
    }
  }
  
  private void offsetChildByInset(View paramView, Rect paramRect, int paramInt)
  {
    if (!ViewCompat.isLaidOut(paramView)) {
      return;
    }
    Rect localRect1 = this.mTempRect5;
    localRect1.set(paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom());
    if (localRect1.isEmpty()) {
      return;
    }
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    Behavior localBehavior = localLayoutParams.getBehavior();
    Rect localRect2 = this.mTempRect3;
    localRect2.setEmpty();
    if ((localBehavior != null) && (localBehavior.getInsetDodgeRect(this, paramView, localRect2)))
    {
      if (!localRect1.contains(localRect2)) {
        throw new IllegalArgumentException("Rect should be within the child's bounds. Rect:" + localRect2.toShortString() + " | Bounds:" + localRect1.toShortString());
      }
    }
    else {
      localRect2.set(localRect1);
    }
    if (localRect2.isEmpty()) {
      return;
    }
    int j = GravityCompat.getAbsoluteGravity(localLayoutParams.dodgeInsetEdges, paramInt);
    int i = 0;
    paramInt = i;
    int k;
    if ((j & 0x30) == 48)
    {
      k = localRect2.top - localLayoutParams.topMargin - localLayoutParams.mInsetOffsetY;
      paramInt = i;
      if (k < paramRect.top)
      {
        setInsetOffsetY(paramView, paramRect.top - k);
        paramInt = 1;
      }
    }
    i = paramInt;
    if ((j & 0x50) == 80)
    {
      k = getHeight() - localRect2.bottom - localLayoutParams.bottomMargin + localLayoutParams.mInsetOffsetY;
      i = paramInt;
      if (k < paramRect.bottom)
      {
        setInsetOffsetY(paramView, k - paramRect.bottom);
        i = 1;
      }
    }
    if (i == 0) {
      setInsetOffsetY(paramView, 0);
    }
    i = 0;
    paramInt = i;
    if ((j & 0x3) == 3)
    {
      k = localRect2.left - localLayoutParams.leftMargin - localLayoutParams.mInsetOffsetX;
      paramInt = i;
      if (k < paramRect.left)
      {
        setInsetOffsetX(paramView, paramRect.left - k);
        paramInt = 1;
      }
    }
    i = paramInt;
    if ((j & 0x5) == 5)
    {
      j = getWidth() - localRect2.right - localLayoutParams.rightMargin + localLayoutParams.mInsetOffsetX;
      i = paramInt;
      if (j < paramRect.right)
      {
        setInsetOffsetX(paramView, j - paramRect.right);
        i = 1;
      }
    }
    if (i == 0) {
      setInsetOffsetX(paramView, 0);
    }
  }
  
  static Behavior parseBehavior(Context paramContext, AttributeSet paramAttributeSet, String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return null;
    }
    if (paramString.startsWith(".")) {
      paramString = paramContext.getPackageName() + paramString;
    }
    for (;;)
    {
      try
      {
        Object localObject2 = (Map)sConstructors.get();
        Object localObject1 = localObject2;
        if (localObject2 == null)
        {
          localObject1 = new HashMap();
          sConstructors.set(localObject1);
        }
        Constructor localConstructor = (Constructor)((Map)localObject1).get(paramString);
        localObject2 = localConstructor;
        if (localConstructor == null)
        {
          localObject2 = Class.forName(paramString, true, paramContext.getClassLoader()).getConstructor(CONSTRUCTOR_PARAMS);
          ((Constructor)localObject2).setAccessible(true);
          ((Map)localObject1).put(paramString, localObject2);
        }
        paramContext = (Behavior)((Constructor)localObject2).newInstance(new Object[] { paramContext, paramAttributeSet });
        return paramContext;
      }
      catch (Exception paramContext)
      {
        throw new RuntimeException("Could not inflate Behavior subclass " + paramString, paramContext);
      }
      if (paramString.indexOf('.') < 0) {
        if (!TextUtils.isEmpty(WIDGET_PACKAGE_NAME)) {
          paramString = WIDGET_PACKAGE_NAME + '.' + paramString;
        }
      }
    }
  }
  
  private boolean performIntercept(MotionEvent paramMotionEvent, int paramInt)
  {
    boolean bool1 = false;
    int i = 0;
    Object localObject1 = null;
    int m = MotionEventCompat.getActionMasked(paramMotionEvent);
    List localList = this.mTempList1;
    getTopSortedChildren(localList);
    int n = localList.size();
    int j = 0;
    boolean bool2 = bool1;
    View localView;
    Object localObject2;
    Behavior localBehavior;
    boolean bool3;
    int k;
    if (j < n)
    {
      localView = (View)localList.get(j);
      localObject2 = (LayoutParams)localView.getLayoutParams();
      localBehavior = ((LayoutParams)localObject2).getBehavior();
      if (((bool1) || (i != 0)) && (m != 0))
      {
        localObject2 = localObject1;
        bool3 = bool1;
        k = i;
        if (localBehavior != null)
        {
          localObject2 = localObject1;
          if (localObject1 == null)
          {
            long l = SystemClock.uptimeMillis();
            localObject2 = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
          }
          switch (paramInt)
          {
          default: 
            k = i;
            bool3 = bool1;
          }
        }
        for (;;)
        {
          j += 1;
          localObject1 = localObject2;
          bool1 = bool3;
          i = k;
          break;
          localBehavior.onInterceptTouchEvent(this, localView, (MotionEvent)localObject2);
          bool3 = bool1;
          k = i;
          continue;
          localBehavior.onTouchEvent(this, localView, (MotionEvent)localObject2);
          bool3 = bool1;
          k = i;
        }
      }
      bool2 = bool1;
      if (!bool1)
      {
        bool2 = bool1;
        if (localBehavior == null) {}
      }
      switch (paramInt)
      {
      default: 
        label272:
        bool2 = bool1;
        if (bool1)
        {
          this.mBehaviorTouchView = localView;
          bool2 = bool1;
        }
        bool3 = ((LayoutParams)localObject2).didBlockInteraction();
        bool1 = ((LayoutParams)localObject2).isBlockingInteractionBelow(this, localView);
        if ((bool1) && (!bool3)) {
          break;
        }
      }
    }
    for (i = 0;; i = 1)
    {
      localObject2 = localObject1;
      bool3 = bool2;
      k = i;
      if (!bool1) {
        break;
      }
      localObject2 = localObject1;
      bool3 = bool2;
      k = i;
      if (i != 0) {
        break;
      }
      localList.clear();
      return bool2;
      bool1 = localBehavior.onInterceptTouchEvent(this, localView, paramMotionEvent);
      break label272;
      bool1 = localBehavior.onTouchEvent(this, localView, paramMotionEvent);
      break label272;
    }
  }
  
  private void prepareChildren()
  {
    this.mDependencySortedChildren.clear();
    this.mChildDag.clear();
    int i = 0;
    int k = getChildCount();
    while (i < k)
    {
      View localView1 = getChildAt(i);
      getResolvedLayoutParams(localView1).findAnchorView(this, localView1);
      this.mChildDag.addNode(localView1);
      int j = 0;
      if (j < k)
      {
        if (j == i) {}
        for (;;)
        {
          j += 1;
          break;
          View localView2 = getChildAt(j);
          if (getResolvedLayoutParams(localView2).dependsOn(this, localView2, localView1))
          {
            if (!this.mChildDag.contains(localView2)) {
              this.mChildDag.addNode(localView2);
            }
            this.mChildDag.addEdge(localView1, localView2);
          }
        }
      }
      i += 1;
    }
    this.mDependencySortedChildren.addAll(this.mChildDag.getSortedList());
    Collections.reverse(this.mDependencySortedChildren);
  }
  
  private void resetTouchBehaviors()
  {
    if (this.mBehaviorTouchView != null)
    {
      Behavior localBehavior = ((LayoutParams)this.mBehaviorTouchView.getLayoutParams()).getBehavior();
      if (localBehavior != null)
      {
        long l = SystemClock.uptimeMillis();
        MotionEvent localMotionEvent = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
        localBehavior.onTouchEvent(this, this.mBehaviorTouchView, localMotionEvent);
        localMotionEvent.recycle();
      }
      this.mBehaviorTouchView = null;
    }
    int j = getChildCount();
    int i = 0;
    while (i < j)
    {
      ((LayoutParams)getChildAt(i).getLayoutParams()).resetTouchBehaviorTracking();
      i += 1;
    }
    this.mDisallowInterceptReset = false;
  }
  
  private static int resolveAnchoredChildGravity(int paramInt)
  {
    int i = paramInt;
    if (paramInt == 0) {
      i = 17;
    }
    return i;
  }
  
  private static int resolveGravity(int paramInt)
  {
    int i = paramInt;
    if (paramInt == 0) {
      i = 8388659;
    }
    return i;
  }
  
  private static int resolveKeylineGravity(int paramInt)
  {
    int i = paramInt;
    if (paramInt == 0) {
      i = 8388661;
    }
    return i;
  }
  
  private void setInsetOffsetX(View paramView, int paramInt)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if (localLayoutParams.mInsetOffsetX != paramInt)
    {
      ViewCompat.offsetLeftAndRight(paramView, paramInt - localLayoutParams.mInsetOffsetX);
      localLayoutParams.mInsetOffsetX = paramInt;
    }
  }
  
  private void setInsetOffsetY(View paramView, int paramInt)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if (localLayoutParams.mInsetOffsetY != paramInt)
    {
      ViewCompat.offsetTopAndBottom(paramView, paramInt - localLayoutParams.mInsetOffsetY);
      localLayoutParams.mInsetOffsetY = paramInt;
    }
  }
  
  private void setupForInsets()
  {
    if (Build.VERSION.SDK_INT < 21) {
      return;
    }
    if (ViewCompat.getFitsSystemWindows(this))
    {
      if (this.mApplyWindowInsetsListener == null) {
        this.mApplyWindowInsetsListener = new OnApplyWindowInsetsListener()
        {
          public WindowInsetsCompat onApplyWindowInsets(View paramAnonymousView, WindowInsetsCompat paramAnonymousWindowInsetsCompat)
          {
            return CoordinatorLayout.this.setWindowInsets(paramAnonymousWindowInsetsCompat);
          }
        };
      }
      ViewCompat.setOnApplyWindowInsetsListener(this, this.mApplyWindowInsetsListener);
      setSystemUiVisibility(1280);
      return;
    }
    ViewCompat.setOnApplyWindowInsetsListener(this, null);
  }
  
  void addPreDrawListener()
  {
    if (this.mIsAttachedToWindow)
    {
      if (this.mOnPreDrawListener == null) {
        this.mOnPreDrawListener = new OnPreDrawListener();
      }
      getViewTreeObserver().addOnPreDrawListener(this.mOnPreDrawListener);
    }
    this.mNeedsPreDrawListener = true;
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((paramLayoutParams instanceof LayoutParams)) {
      return super.checkLayoutParams(paramLayoutParams);
    }
    return false;
  }
  
  public void dispatchDependentViewsChanged(View paramView)
  {
    List localList = this.mChildDag.getIncomingEdges(paramView);
    if ((localList == null) || (localList.isEmpty())) {}
    for (;;)
    {
      return;
      int i = 0;
      while (i < localList.size())
      {
        View localView = (View)localList.get(i);
        Behavior localBehavior = ((LayoutParams)localView.getLayoutParams()).getBehavior();
        if (localBehavior != null) {
          localBehavior.onDependentViewChanged(this, localView, paramView);
        }
        i += 1;
      }
    }
  }
  
  public boolean doViewsOverlap(View paramView1, View paramView2)
  {
    if ((paramView1.getVisibility() == 0) && (paramView2.getVisibility() == 0))
    {
      Rect localRect = this.mTempRect1;
      if (paramView1.getParent() != this)
      {
        bool = true;
        getChildRect(paramView1, bool, localRect);
        paramView1 = this.mTempRect2;
        if (paramView2.getParent() == this) {
          break label115;
        }
      }
      label115:
      for (boolean bool = true;; bool = false)
      {
        getChildRect(paramView2, bool, paramView1);
        if ((localRect.left > paramView1.right) || (localRect.top > paramView1.bottom) || (localRect.right < paramView1.left)) {
          break label122;
        }
        if (localRect.bottom < paramView1.top) {
          break label120;
        }
        return true;
        bool = false;
        break;
      }
      label120:
      return false;
      label122:
      return false;
    }
    return false;
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if (localLayoutParams.mBehavior != null)
    {
      float f = localLayoutParams.mBehavior.getScrimOpacity(this, paramView);
      if (f > 0.0F)
      {
        if (this.mScrimPaint == null) {
          this.mScrimPaint = new Paint();
        }
        this.mScrimPaint.setColor(localLayoutParams.mBehavior.getScrimColor(this, paramView));
        this.mScrimPaint.setAlpha(MathUtils.constrain(Math.round(255.0F * f), 0, 255));
        int i = paramCanvas.save();
        if (paramView.isOpaque()) {
          paramCanvas.clipRect(paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom(), Region.Op.DIFFERENCE);
        }
        paramCanvas.drawRect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom(), this.mScrimPaint);
        paramCanvas.restoreToCount(i);
      }
    }
    return super.drawChild(paramCanvas, paramView, paramLong);
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    int[] arrayOfInt = getDrawableState();
    boolean bool2 = false;
    Drawable localDrawable = this.mStatusBarBackground;
    boolean bool1 = bool2;
    if (localDrawable != null)
    {
      bool1 = bool2;
      if (localDrawable.isStateful()) {
        bool1 = localDrawable.setState(arrayOfInt);
      }
    }
    if (bool1) {
      invalidate();
    }
  }
  
  void ensurePreDrawListener()
  {
    int m = 0;
    int j = getChildCount();
    int i = 0;
    for (;;)
    {
      int k = m;
      if (i < j)
      {
        if (hasDependencies(getChildAt(i))) {
          k = 1;
        }
      }
      else
      {
        if (k != this.mNeedsPreDrawListener)
        {
          if (k == 0) {
            break;
          }
          addPreDrawListener();
        }
        return;
      }
      i += 1;
    }
    removePreDrawListener();
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-2, -2);
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((paramLayoutParams instanceof LayoutParams)) {
      return new LayoutParams((LayoutParams)paramLayoutParams);
    }
    if ((paramLayoutParams instanceof ViewGroup.MarginLayoutParams)) {
      return new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams);
    }
    return new LayoutParams(paramLayoutParams);
  }
  
  void getChildRect(View paramView, boolean paramBoolean, Rect paramRect)
  {
    if ((paramView.isLayoutRequested()) || (paramView.getVisibility() == 8))
    {
      paramRect.setEmpty();
      return;
    }
    if (paramBoolean)
    {
      getDescendantRect(paramView, paramRect);
      return;
    }
    paramRect.set(paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom());
  }
  
  @NonNull
  public List<View> getDependencies(@NonNull View paramView)
  {
    paramView = this.mChildDag.getOutgoingEdges(paramView);
    this.mTempDependenciesList.clear();
    if (paramView != null) {
      this.mTempDependenciesList.addAll(paramView);
    }
    return this.mTempDependenciesList;
  }
  
  @VisibleForTesting
  final List<View> getDependencySortedChildren()
  {
    prepareChildren();
    return Collections.unmodifiableList(this.mDependencySortedChildren);
  }
  
  @NonNull
  public List<View> getDependents(@NonNull View paramView)
  {
    paramView = this.mChildDag.getIncomingEdges(paramView);
    this.mTempDependenciesList.clear();
    if (paramView != null) {
      this.mTempDependenciesList.addAll(paramView);
    }
    return this.mTempDependenciesList;
  }
  
  void getDescendantRect(View paramView, Rect paramRect)
  {
    Utils.getDescendantRect(this, paramView, paramRect);
  }
  
  void getDesiredAnchoredChildRect(View paramView, int paramInt, Rect paramRect1, Rect paramRect2)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = paramView.getMeasuredWidth();
    int j = paramView.getMeasuredHeight();
    getDesiredAnchoredChildRectWithoutConstraints(paramView, paramInt, paramRect1, paramRect2, localLayoutParams, i, j);
    constrainChildRect(localLayoutParams, paramRect2, i, j);
  }
  
  void getLastChildRect(View paramView, Rect paramRect)
  {
    paramRect.set(((LayoutParams)paramView.getLayoutParams()).getLastChildRect());
  }
  
  final WindowInsetsCompat getLastWindowInsets()
  {
    return this.mLastInsets;
  }
  
  public int getNestedScrollAxes()
  {
    return this.mNestedScrollingParentHelper.getNestedScrollAxes();
  }
  
  LayoutParams getResolvedLayoutParams(View paramView)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    View localView;
    if (!localLayoutParams.mBehaviorResolved)
    {
      Class localClass = paramView.getClass();
      paramView = null;
      for (;;)
      {
        localView = paramView;
        if (localClass == null) {
          break;
        }
        paramView = (DefaultBehavior)localClass.getAnnotation(DefaultBehavior.class);
        localView = paramView;
        if (paramView != null) {
          break;
        }
        localClass = localClass.getSuperclass();
      }
      if (localView == null) {}
    }
    try
    {
      localLayoutParams.setBehavior((Behavior)localView.value().newInstance());
      localLayoutParams.mBehaviorResolved = true;
      return localLayoutParams;
    }
    catch (Exception paramView)
    {
      for (;;)
      {
        Log.e("CoordinatorLayout", "Default behavior class " + localView.value().getName() + " could not be instantiated. Did you forget a default constructor?", paramView);
      }
    }
  }
  
  @Nullable
  public Drawable getStatusBarBackground()
  {
    return this.mStatusBarBackground;
  }
  
  protected int getSuggestedMinimumHeight()
  {
    return Math.max(super.getSuggestedMinimumHeight(), getPaddingTop() + getPaddingBottom());
  }
  
  protected int getSuggestedMinimumWidth()
  {
    return Math.max(super.getSuggestedMinimumWidth(), getPaddingLeft() + getPaddingRight());
  }
  
  public boolean isPointInChildBounds(View paramView, int paramInt1, int paramInt2)
  {
    Rect localRect = this.mTempRect1;
    getDescendantRect(paramView, localRect);
    return localRect.contains(paramInt1, paramInt2);
  }
  
  void offsetChildToAnchor(View paramView, int paramInt)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    Object localObject;
    Rect localRect1;
    Rect localRect2;
    int i;
    int j;
    if (localLayoutParams.mAnchorView != null)
    {
      localObject = this.mTempRect1;
      localRect1 = this.mTempRect2;
      localRect2 = this.mTempRect3;
      getDescendantRect(localLayoutParams.mAnchorView, (Rect)localObject);
      getChildRect(paramView, false, localRect1);
      i = paramView.getMeasuredWidth();
      j = paramView.getMeasuredHeight();
      getDesiredAnchoredChildRectWithoutConstraints(paramView, paramInt, (Rect)localObject, localRect2, localLayoutParams, i, j);
      if (localRect2.left != localRect1.left) {
        break label194;
      }
      if (localRect2.top == localRect1.top) {
        break label199;
      }
      paramInt = 1;
    }
    for (;;)
    {
      constrainChildRect(localLayoutParams, localRect2, i, j);
      i = localRect2.left - localRect1.left;
      j = localRect2.top - localRect1.top;
      if (i != 0) {
        ViewCompat.offsetLeftAndRight(paramView, i);
      }
      if (j != 0) {
        ViewCompat.offsetTopAndBottom(paramView, j);
      }
      if (paramInt != 0)
      {
        localObject = localLayoutParams.getBehavior();
        if (localObject != null) {
          ((Behavior)localObject).onDependentViewChanged(this, paramView, localLayoutParams.mAnchorView);
        }
      }
      return;
      label194:
      paramInt = 1;
      continue;
      label199:
      paramInt = 0;
    }
  }
  
  public void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    resetTouchBehaviors();
    if (this.mNeedsPreDrawListener)
    {
      if (this.mOnPreDrawListener == null) {
        this.mOnPreDrawListener = new OnPreDrawListener();
      }
      getViewTreeObserver().addOnPreDrawListener(this.mOnPreDrawListener);
    }
    if ((this.mLastInsets == null) && (ViewCompat.getFitsSystemWindows(this))) {
      ViewCompat.requestApplyInsets(this);
    }
    this.mIsAttachedToWindow = true;
  }
  
  final void onChildViewsChanged(int paramInt)
  {
    int k = ViewCompat.getLayoutDirection(this);
    int m = this.mDependencySortedChildren.size();
    Rect localRect = this.mTempRect4;
    localRect.setEmpty();
    int i = 0;
    if (i < m)
    {
      View localView = (View)this.mDependencySortedChildren.get(i);
      Object localObject1 = (LayoutParams)localView.getLayoutParams();
      int j = 0;
      while (j < i)
      {
        localObject2 = (View)this.mDependencySortedChildren.get(j);
        if (((LayoutParams)localObject1).mAnchorDirectChild == localObject2) {
          offsetChildToAnchor(localView, k);
        }
        j += 1;
      }
      Object localObject2 = this.mTempRect1;
      getChildRect(localView, true, (Rect)localObject2);
      if ((((LayoutParams)localObject1).insetEdge == 0) || (((Rect)localObject2).isEmpty()))
      {
        label139:
        if ((((LayoutParams)localObject1).dodgeInsetEdges != 0) && (localView.getVisibility() == 0)) {
          offsetChildByInset(localView, localRect, k);
        }
        if (paramInt != 0) {
          break label377;
        }
        localObject1 = this.mTempRect2;
        getLastChildRect(localView, (Rect)localObject1);
        if (!((Rect)localObject1).equals(localObject2)) {
          break label369;
        }
      }
      label369:
      label377:
      Behavior localBehavior;
      for (;;)
      {
        i += 1;
        break;
        j = GravityCompat.getAbsoluteGravity(((LayoutParams)localObject1).insetEdge, k);
        switch (j & 0x70)
        {
        }
        for (;;)
        {
          switch (j & 0x7)
          {
          case 4: 
          default: 
            break;
          case 3: 
            localRect.left = Math.max(localRect.left, ((Rect)localObject2).right);
            break;
            localRect.top = Math.max(localRect.top, ((Rect)localObject2).bottom);
            continue;
            localRect.bottom = Math.max(localRect.bottom, getHeight() - ((Rect)localObject2).top);
          }
        }
        localRect.right = Math.max(localRect.right, getWidth() - ((Rect)localObject2).left);
        break label139;
        recordLastChildRect(localView, (Rect)localObject2);
        j = i + 1;
        while (j < m)
        {
          localObject1 = (View)this.mDependencySortedChildren.get(j);
          localObject2 = (LayoutParams)((View)localObject1).getLayoutParams();
          localBehavior = ((LayoutParams)localObject2).getBehavior();
          if ((localBehavior != null) && (localBehavior.layoutDependsOn(this, (View)localObject1, localView)))
          {
            if ((paramInt != 0) || (!((LayoutParams)localObject2).getChangedAfterNestedScroll())) {
              break label461;
            }
            ((LayoutParams)localObject2).resetChangedAfterNestedScroll();
          }
          j += 1;
        }
      }
      label461:
      switch (paramInt)
      {
      }
      for (boolean bool = localBehavior.onDependentViewChanged(this, (View)localObject1, localView); paramInt == 1; bool = true)
      {
        ((LayoutParams)localObject2).setChangedAfterNestedScroll(bool);
        break;
        localBehavior.onDependentViewRemoved(this, (View)localObject1, localView);
      }
    }
  }
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    resetTouchBehaviors();
    if ((this.mNeedsPreDrawListener) && (this.mOnPreDrawListener != null)) {
      getViewTreeObserver().removeOnPreDrawListener(this.mOnPreDrawListener);
    }
    if (this.mNestedScrollingTarget != null) {
      onStopNestedScroll(this.mNestedScrollingTarget);
    }
    this.mIsAttachedToWindow = false;
  }
  
  public void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if ((this.mDrawStatusBarBackground) && (this.mStatusBarBackground != null)) {
      if (this.mLastInsets == null) {
        break label61;
      }
    }
    label61:
    for (int i = this.mLastInsets.getSystemWindowInsetTop();; i = 0)
    {
      if (i > 0)
      {
        this.mStatusBarBackground.setBounds(0, 0, getWidth(), i);
        this.mStatusBarBackground.draw(paramCanvas);
      }
      return;
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    if (i == 0) {
      resetTouchBehaviors();
    }
    boolean bool = performIntercept(paramMotionEvent, 0);
    if ((i == 1) || (i == 3)) {
      resetTouchBehaviors();
    }
    return bool;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt2 = ViewCompat.getLayoutDirection(this);
    paramInt3 = this.mDependencySortedChildren.size();
    paramInt1 = 0;
    if (paramInt1 < paramInt3)
    {
      View localView = (View)this.mDependencySortedChildren.get(paramInt1);
      if (localView.getVisibility() == 8) {}
      for (;;)
      {
        paramInt1 += 1;
        break;
        Behavior localBehavior = ((LayoutParams)localView.getLayoutParams()).getBehavior();
        if ((localBehavior == null) || (!localBehavior.onLayoutChild(this, localView, paramInt2))) {
          onLayoutChild(localView, paramInt2);
        }
      }
    }
  }
  
  public void onLayoutChild(View paramView, int paramInt)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if (localLayoutParams.checkAnchorChanged()) {
      throw new IllegalStateException("An anchor may not be changed after CoordinatorLayout measurement begins before layout is complete.");
    }
    if (localLayoutParams.mAnchorView != null)
    {
      layoutChildWithAnchor(paramView, localLayoutParams.mAnchorView, paramInt);
      return;
    }
    if (localLayoutParams.keyline >= 0)
    {
      layoutChildWithKeyline(paramView, localLayoutParams.keyline, paramInt);
      return;
    }
    layoutChild(paramView, paramInt);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    prepareChildren();
    ensurePreDrawListener();
    int i6 = getPaddingLeft();
    int i7 = getPaddingTop();
    int i8 = getPaddingRight();
    int i9 = getPaddingBottom();
    int i10 = ViewCompat.getLayoutDirection(this);
    int j;
    int i11;
    int i12;
    int i13;
    int i14;
    int i1;
    int n;
    int m;
    if (i10 == 1)
    {
      j = 1;
      i11 = View.MeasureSpec.getMode(paramInt1);
      i12 = View.MeasureSpec.getSize(paramInt1);
      i13 = View.MeasureSpec.getMode(paramInt2);
      i14 = View.MeasureSpec.getSize(paramInt2);
      i1 = getSuggestedMinimumWidth();
      n = getSuggestedMinimumHeight();
      m = 0;
      if (this.mLastInsets == null) {
        break label161;
      }
    }
    View localView;
    label161:
    for (boolean bool = ViewCompat.getFitsSystemWindows(this);; bool = false)
    {
      int i15 = this.mDependencySortedChildren.size();
      int k = 0;
      for (;;)
      {
        if (k >= i15) {
          break label527;
        }
        localView = (View)this.mDependencySortedChildren.get(k);
        if (localView.getVisibility() != 8) {
          break;
        }
        k += 1;
      }
      j = 0;
      break;
    }
    LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
    int i2 = 0;
    int i = i2;
    if (localLayoutParams.keyline >= 0)
    {
      i = i2;
      if (i11 != 0)
      {
        i3 = getKeyline(localLayoutParams.keyline);
        i4 = GravityCompat.getAbsoluteGravity(resolveKeylineGravity(localLayoutParams.gravity), i10) & 0x7;
        if (((i4 != 3) || (j != 0)) && ((i4 != 5) || (j == 0))) {
          break label402;
        }
        i = Math.max(0, i12 - i8 - i3);
      }
    }
    label263:
    int i3 = paramInt1;
    int i4 = paramInt2;
    i2 = i3;
    int i5 = i4;
    if (bool)
    {
      if (ViewCompat.getFitsSystemWindows(localView))
      {
        i5 = i4;
        i2 = i3;
      }
    }
    else
    {
      label298:
      Behavior localBehavior = localLayoutParams.getBehavior();
      if ((localBehavior == null) || (!localBehavior.onMeasureChild(this, localView, i2, i, i5, 0))) {
        break label512;
      }
    }
    for (;;)
    {
      i1 = Math.max(i1, localView.getMeasuredWidth() + (i6 + i8) + localLayoutParams.leftMargin + localLayoutParams.rightMargin);
      n = Math.max(n, localView.getMeasuredHeight() + (i7 + i9) + localLayoutParams.topMargin + localLayoutParams.bottomMargin);
      m = ViewCompat.combineMeasuredStates(m, ViewCompat.getMeasuredState(localView));
      break;
      label402:
      if ((i4 != 5) || (j != 0))
      {
        i = i2;
        if (i4 != 3) {
          break label263;
        }
        i = i2;
        if (j == 0) {
          break label263;
        }
      }
      i = Math.max(0, i3 - i6);
      break label263;
      i2 = this.mLastInsets.getSystemWindowInsetLeft();
      i5 = this.mLastInsets.getSystemWindowInsetRight();
      i3 = this.mLastInsets.getSystemWindowInsetTop();
      i4 = this.mLastInsets.getSystemWindowInsetBottom();
      i2 = View.MeasureSpec.makeMeasureSpec(i12 - (i2 + i5), i11);
      i5 = View.MeasureSpec.makeMeasureSpec(i14 - (i3 + i4), i13);
      break label298;
      label512:
      onMeasureChild(localView, i2, i, i5, 0);
    }
    label527:
    setMeasuredDimension(ViewCompat.resolveSizeAndState(i1, paramInt1, 0xFF000000 & m), ViewCompat.resolveSizeAndState(n, paramInt2, m << 16));
  }
  
  public void onMeasureChild(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    measureChildWithMargins(paramView, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public boolean onNestedFling(View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    boolean bool1 = false;
    int j = getChildCount();
    int i = 0;
    if (i < j)
    {
      View localView = getChildAt(i);
      Object localObject = (LayoutParams)localView.getLayoutParams();
      boolean bool2;
      if (!((LayoutParams)localObject).isNestedScrollAccepted()) {
        bool2 = bool1;
      }
      for (;;)
      {
        i += 1;
        bool1 = bool2;
        break;
        localObject = ((LayoutParams)localObject).getBehavior();
        bool2 = bool1;
        if (localObject != null) {
          bool2 = bool1 | ((Behavior)localObject).onNestedFling(this, localView, paramView, paramFloat1, paramFloat2, paramBoolean);
        }
      }
    }
    if (bool1) {
      onChildViewsChanged(1);
    }
    return bool1;
  }
  
  public boolean onNestedPreFling(View paramView, float paramFloat1, float paramFloat2)
  {
    boolean bool1 = false;
    int j = getChildCount();
    int i = 0;
    if (i < j)
    {
      View localView = getChildAt(i);
      Object localObject = (LayoutParams)localView.getLayoutParams();
      boolean bool2;
      if (!((LayoutParams)localObject).isNestedScrollAccepted()) {
        bool2 = bool1;
      }
      for (;;)
      {
        i += 1;
        bool1 = bool2;
        break;
        localObject = ((LayoutParams)localObject).getBehavior();
        bool2 = bool1;
        if (localObject != null) {
          bool2 = bool1 | ((Behavior)localObject).onNestedPreFling(this, localView, paramView, paramFloat1, paramFloat2);
        }
      }
    }
    return bool1;
  }
  
  public void onNestedPreScroll(View paramView, int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    int i = 0;
    int j = 0;
    int m = 0;
    int i2 = getChildCount();
    int k = 0;
    if (k < i2)
    {
      View localView = getChildAt(k);
      Object localObject = (LayoutParams)localView.getLayoutParams();
      int i1;
      int n;
      if (!((LayoutParams)localObject).isNestedScrollAccepted())
      {
        i1 = j;
        n = i;
      }
      do
      {
        k += 1;
        i = n;
        j = i1;
        break;
        localObject = ((LayoutParams)localObject).getBehavior();
        n = i;
        i1 = j;
      } while (localObject == null);
      int[] arrayOfInt = this.mTempIntPair;
      this.mTempIntPair[1] = 0;
      arrayOfInt[0] = 0;
      ((Behavior)localObject).onNestedPreScroll(this, localView, paramView, paramInt1, paramInt2, this.mTempIntPair);
      if (paramInt1 > 0)
      {
        i = Math.max(i, this.mTempIntPair[0]);
        label146:
        if (paramInt2 <= 0) {
          break label193;
        }
      }
      label193:
      for (j = Math.max(j, this.mTempIntPair[1]);; j = Math.min(j, this.mTempIntPair[1]))
      {
        m = 1;
        n = i;
        i1 = j;
        break;
        i = Math.min(i, this.mTempIntPair[0]);
        break label146;
      }
    }
    paramArrayOfInt[0] = i;
    paramArrayOfInt[1] = j;
    if (m != 0) {
      onChildViewsChanged(1);
    }
  }
  
  public void onNestedScroll(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int k = getChildCount();
    int j = 0;
    int i = 0;
    if (i < k)
    {
      View localView = getChildAt(i);
      Object localObject = (LayoutParams)localView.getLayoutParams();
      if (!((LayoutParams)localObject).isNestedScrollAccepted()) {}
      for (;;)
      {
        i += 1;
        break;
        localObject = ((LayoutParams)localObject).getBehavior();
        if (localObject != null)
        {
          ((Behavior)localObject).onNestedScroll(this, localView, paramView, paramInt1, paramInt2, paramInt3, paramInt4);
          j = 1;
        }
      }
    }
    if (j != 0) {
      onChildViewsChanged(1);
    }
  }
  
  public void onNestedScrollAccepted(View paramView1, View paramView2, int paramInt)
  {
    this.mNestedScrollingParentHelper.onNestedScrollAccepted(paramView1, paramView2, paramInt);
    this.mNestedScrollingDirectChild = paramView1;
    this.mNestedScrollingTarget = paramView2;
    int j = getChildCount();
    int i = 0;
    if (i < j)
    {
      View localView = getChildAt(i);
      Object localObject = (LayoutParams)localView.getLayoutParams();
      if (!((LayoutParams)localObject).isNestedScrollAccepted()) {}
      for (;;)
      {
        i += 1;
        break;
        localObject = ((LayoutParams)localObject).getBehavior();
        if (localObject != null) {
          ((Behavior)localObject).onNestedScrollAccepted(this, localView, paramView1, paramView2, paramInt);
        }
      }
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
    paramParcelable = paramParcelable.behaviorStates;
    int i = 0;
    int j = getChildCount();
    while (i < j)
    {
      View localView = getChildAt(i);
      int k = localView.getId();
      Behavior localBehavior = getResolvedLayoutParams(localView).getBehavior();
      if ((k != -1) && (localBehavior != null))
      {
        Parcelable localParcelable = (Parcelable)paramParcelable.get(k);
        if (localParcelable != null) {
          localBehavior.onRestoreInstanceState(this, localView, localParcelable);
        }
      }
      i += 1;
    }
  }
  
  protected Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    SparseArray localSparseArray = new SparseArray();
    int i = 0;
    int j = getChildCount();
    while (i < j)
    {
      Object localObject = getChildAt(i);
      int k = ((View)localObject).getId();
      Behavior localBehavior = ((LayoutParams)((View)localObject).getLayoutParams()).getBehavior();
      if ((k != -1) && (localBehavior != null))
      {
        localObject = localBehavior.onSaveInstanceState(this, (View)localObject);
        if (localObject != null) {
          localSparseArray.append(k, localObject);
        }
      }
      i += 1;
    }
    localSavedState.behaviorStates = localSparseArray;
    return localSavedState;
  }
  
  public boolean onStartNestedScroll(View paramView1, View paramView2, int paramInt)
  {
    boolean bool1 = false;
    int j = getChildCount();
    int i = 0;
    if (i < j)
    {
      View localView = getChildAt(i);
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      Behavior localBehavior = localLayoutParams.getBehavior();
      if (localBehavior != null)
      {
        boolean bool2 = localBehavior.onStartNestedScroll(this, localView, paramView1, paramView2, paramInt);
        bool1 |= bool2;
        localLayoutParams.acceptNestedScroll(bool2);
      }
      for (;;)
      {
        i += 1;
        break;
        localLayoutParams.acceptNestedScroll(false);
      }
    }
    return bool1;
  }
  
  public void onStopNestedScroll(View paramView)
  {
    this.mNestedScrollingParentHelper.onStopNestedScroll(paramView);
    int j = getChildCount();
    int i = 0;
    if (i < j)
    {
      View localView = getChildAt(i);
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      if (!localLayoutParams.isNestedScrollAccepted()) {}
      for (;;)
      {
        i += 1;
        break;
        Behavior localBehavior = localLayoutParams.getBehavior();
        if (localBehavior != null) {
          localBehavior.onStopNestedScroll(this, localView, paramView);
        }
        localLayoutParams.resetNestedScroll();
        localLayoutParams.resetChangedAfterNestedScroll();
      }
    }
    this.mNestedScrollingDirectChild = null;
    this.mNestedScrollingTarget = null;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool4 = false;
    boolean bool2 = false;
    Object localObject = null;
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    boolean bool3;
    boolean bool1;
    if (this.mBehaviorTouchView == null)
    {
      bool2 = performIntercept(paramMotionEvent, 1);
      bool3 = bool2;
      bool1 = bool4;
      if (!bool2) {}
    }
    else
    {
      Behavior localBehavior = ((LayoutParams)this.mBehaviorTouchView.getLayoutParams()).getBehavior();
      bool3 = bool2;
      bool1 = bool4;
      if (localBehavior != null)
      {
        bool1 = localBehavior.onTouchEvent(this, this.mBehaviorTouchView, paramMotionEvent);
        bool3 = bool2;
      }
    }
    if (this.mBehaviorTouchView == null)
    {
      bool2 = bool1 | super.onTouchEvent(paramMotionEvent);
      paramMotionEvent = (MotionEvent)localObject;
    }
    for (;;)
    {
      if (((bool2) || (i != 0)) || (paramMotionEvent != null)) {
        paramMotionEvent.recycle();
      }
      if ((i == 1) || (i == 3)) {
        resetTouchBehaviors();
      }
      return bool2;
      paramMotionEvent = (MotionEvent)localObject;
      bool2 = bool1;
      if (bool3)
      {
        long l = SystemClock.uptimeMillis();
        paramMotionEvent = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
        super.onTouchEvent(paramMotionEvent);
        bool2 = bool1;
      }
    }
  }
  
  void recordLastChildRect(View paramView, Rect paramRect)
  {
    ((LayoutParams)paramView.getLayoutParams()).setLastChildRect(paramRect);
  }
  
  void removePreDrawListener()
  {
    if ((this.mIsAttachedToWindow) && (this.mOnPreDrawListener != null)) {
      getViewTreeObserver().removeOnPreDrawListener(this.mOnPreDrawListener);
    }
    this.mNeedsPreDrawListener = false;
  }
  
  public boolean requestChildRectangleOnScreen(View paramView, Rect paramRect, boolean paramBoolean)
  {
    Behavior localBehavior = ((LayoutParams)paramView.getLayoutParams()).getBehavior();
    if ((localBehavior != null) && (localBehavior.onRequestChildRectangleOnScreen(this, paramView, paramRect, paramBoolean))) {
      return true;
    }
    return super.requestChildRectangleOnScreen(paramView, paramRect, paramBoolean);
  }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
  {
    super.requestDisallowInterceptTouchEvent(paramBoolean);
    if ((!paramBoolean) || (this.mDisallowInterceptReset)) {
      return;
    }
    resetTouchBehaviors();
    this.mDisallowInterceptReset = true;
  }
  
  public void setFitsSystemWindows(boolean paramBoolean)
  {
    super.setFitsSystemWindows(paramBoolean);
    setupForInsets();
  }
  
  public void setOnHierarchyChangeListener(ViewGroup.OnHierarchyChangeListener paramOnHierarchyChangeListener)
  {
    this.mOnHierarchyChangeListener = paramOnHierarchyChangeListener;
  }
  
  public void setStatusBarBackground(@Nullable Drawable paramDrawable)
  {
    Drawable localDrawable = null;
    if (this.mStatusBarBackground != paramDrawable)
    {
      if (this.mStatusBarBackground != null) {
        this.mStatusBarBackground.setCallback(null);
      }
      if (paramDrawable != null) {
        localDrawable = paramDrawable.mutate();
      }
      this.mStatusBarBackground = localDrawable;
      if (this.mStatusBarBackground != null)
      {
        if (this.mStatusBarBackground.isStateful()) {
          this.mStatusBarBackground.setState(getDrawableState());
        }
        DrawableCompat.setLayoutDirection(this.mStatusBarBackground, ViewCompat.getLayoutDirection(this));
        paramDrawable = this.mStatusBarBackground;
        if (getVisibility() != 0) {
          break label114;
        }
      }
    }
    label114:
    for (boolean bool = true;; bool = false)
    {
      paramDrawable.setVisible(bool, false);
      this.mStatusBarBackground.setCallback(this);
      ViewCompat.postInvalidateOnAnimation(this);
      return;
    }
  }
  
  public void setStatusBarBackgroundColor(@ColorInt int paramInt)
  {
    setStatusBarBackground(new ColorDrawable(paramInt));
  }
  
  public void setStatusBarBackgroundResource(@DrawableRes int paramInt)
  {
    if (paramInt != 0) {}
    for (Drawable localDrawable = ContextCompat.getDrawable(getContext(), paramInt);; localDrawable = null)
    {
      setStatusBarBackground(localDrawable);
      return;
    }
  }
  
  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    if (paramInt == 0) {}
    for (boolean bool = true;; bool = false)
    {
      if ((this.mStatusBarBackground != null) && (this.mStatusBarBackground.isVisible() != bool)) {
        this.mStatusBarBackground.setVisible(bool, false);
      }
      return;
    }
  }
  
  final WindowInsetsCompat setWindowInsets(WindowInsetsCompat paramWindowInsetsCompat)
  {
    boolean bool2 = false;
    WindowInsetsCompat localWindowInsetsCompat = paramWindowInsetsCompat;
    if (!Utils.objectEquals(this.mLastInsets, paramWindowInsetsCompat))
    {
      this.mLastInsets = paramWindowInsetsCompat;
      if ((paramWindowInsetsCompat == null) || (paramWindowInsetsCompat.getSystemWindowInsetTop() <= 0)) {
        break label78;
      }
    }
    label78:
    for (boolean bool1 = true;; bool1 = false)
    {
      this.mDrawStatusBarBackground = bool1;
      bool1 = bool2;
      if (!this.mDrawStatusBarBackground)
      {
        bool1 = bool2;
        if (getBackground() == null) {
          bool1 = true;
        }
      }
      setWillNotDraw(bool1);
      localWindowInsetsCompat = dispatchApplyWindowInsetsToBehaviors(paramWindowInsetsCompat);
      requestLayout();
      return localWindowInsetsCompat;
    }
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    return (super.verifyDrawable(paramDrawable)) || (paramDrawable == this.mStatusBarBackground);
  }
  
  public static abstract class Behavior<V extends View>
  {
    public Behavior() {}
    
    public Behavior(Context paramContext, AttributeSet paramAttributeSet) {}
    
    public static Object getTag(View paramView)
    {
      return ((CoordinatorLayout.LayoutParams)paramView.getLayoutParams()).mBehaviorTag;
    }
    
    public static void setTag(View paramView, Object paramObject)
    {
      ((CoordinatorLayout.LayoutParams)paramView.getLayoutParams()).mBehaviorTag = paramObject;
    }
    
    public boolean blocksInteractionBelow(CoordinatorLayout paramCoordinatorLayout, V paramV)
    {
      return getScrimOpacity(paramCoordinatorLayout, paramV) > 0.0F;
    }
    
    public boolean getInsetDodgeRect(@NonNull CoordinatorLayout paramCoordinatorLayout, @NonNull V paramV, @NonNull Rect paramRect)
    {
      return false;
    }
    
    @ColorInt
    public int getScrimColor(CoordinatorLayout paramCoordinatorLayout, V paramV)
    {
      return -16777216;
    }
    
    @FloatRange(from=0.0D, to=1.0D)
    public float getScrimOpacity(CoordinatorLayout paramCoordinatorLayout, V paramV)
    {
      return 0.0F;
    }
    
    @Deprecated
    public boolean isDirty(CoordinatorLayout paramCoordinatorLayout, V paramV)
    {
      return false;
    }
    
    public boolean layoutDependsOn(CoordinatorLayout paramCoordinatorLayout, V paramV, View paramView)
    {
      return false;
    }
    
    @NonNull
    public WindowInsetsCompat onApplyWindowInsets(CoordinatorLayout paramCoordinatorLayout, V paramV, WindowInsetsCompat paramWindowInsetsCompat)
    {
      return paramWindowInsetsCompat;
    }
    
    public void onAttachedToLayoutParams(@NonNull CoordinatorLayout.LayoutParams paramLayoutParams) {}
    
    public boolean onDependentViewChanged(CoordinatorLayout paramCoordinatorLayout, V paramV, View paramView)
    {
      return false;
    }
    
    public void onDependentViewRemoved(CoordinatorLayout paramCoordinatorLayout, V paramV, View paramView) {}
    
    public void onDetachedFromLayoutParams() {}
    
    public boolean onInterceptTouchEvent(CoordinatorLayout paramCoordinatorLayout, V paramV, MotionEvent paramMotionEvent)
    {
      return false;
    }
    
    public boolean onLayoutChild(CoordinatorLayout paramCoordinatorLayout, V paramV, int paramInt)
    {
      return false;
    }
    
    public boolean onMeasureChild(CoordinatorLayout paramCoordinatorLayout, V paramV, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return false;
    }
    
    public boolean onNestedFling(CoordinatorLayout paramCoordinatorLayout, V paramV, View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean)
    {
      return false;
    }
    
    public boolean onNestedPreFling(CoordinatorLayout paramCoordinatorLayout, V paramV, View paramView, float paramFloat1, float paramFloat2)
    {
      return false;
    }
    
    public void onNestedPreScroll(CoordinatorLayout paramCoordinatorLayout, V paramV, View paramView, int paramInt1, int paramInt2, int[] paramArrayOfInt) {}
    
    public void onNestedScroll(CoordinatorLayout paramCoordinatorLayout, V paramV, View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
    
    public void onNestedScrollAccepted(CoordinatorLayout paramCoordinatorLayout, V paramV, View paramView1, View paramView2, int paramInt) {}
    
    public boolean onRequestChildRectangleOnScreen(CoordinatorLayout paramCoordinatorLayout, V paramV, Rect paramRect, boolean paramBoolean)
    {
      return false;
    }
    
    public void onRestoreInstanceState(CoordinatorLayout paramCoordinatorLayout, V paramV, Parcelable paramParcelable) {}
    
    public Parcelable onSaveInstanceState(CoordinatorLayout paramCoordinatorLayout, V paramV)
    {
      return View.BaseSavedState.EMPTY_STATE;
    }
    
    public boolean onStartNestedScroll(CoordinatorLayout paramCoordinatorLayout, V paramV, View paramView1, View paramView2, int paramInt)
    {
      return false;
    }
    
    public void onStopNestedScroll(CoordinatorLayout paramCoordinatorLayout, V paramV, View paramView) {}
    
    public boolean onTouchEvent(CoordinatorLayout paramCoordinatorLayout, V paramV, MotionEvent paramMotionEvent)
    {
      return false;
    }
  }
  
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface DefaultBehavior
  {
    Class<? extends CoordinatorLayout.Behavior> value();
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public static @interface DispatchChangeEvent {}
  
  private class HierarchyChangeListener
    implements ViewGroup.OnHierarchyChangeListener
  {
    HierarchyChangeListener() {}
    
    public void onChildViewAdded(View paramView1, View paramView2)
    {
      if (CoordinatorLayout.this.mOnHierarchyChangeListener != null) {
        CoordinatorLayout.this.mOnHierarchyChangeListener.onChildViewAdded(paramView1, paramView2);
      }
    }
    
    public void onChildViewRemoved(View paramView1, View paramView2)
    {
      CoordinatorLayout.this.onChildViewsChanged(2);
      if (CoordinatorLayout.this.mOnHierarchyChangeListener != null) {
        CoordinatorLayout.this.mOnHierarchyChangeListener.onChildViewRemoved(paramView1, paramView2);
      }
    }
  }
  
  public static class LayoutParams
    extends ViewGroup.MarginLayoutParams
  {
    public int anchorGravity = 0;
    public int dodgeInsetEdges = 0;
    public int gravity = 0;
    public int insetEdge = 0;
    public int keyline = -1;
    View mAnchorDirectChild;
    int mAnchorId = -1;
    View mAnchorView;
    CoordinatorLayout.Behavior mBehavior;
    boolean mBehaviorResolved = false;
    Object mBehaviorTag;
    private boolean mDidAcceptNestedScroll;
    private boolean mDidBlockInteraction;
    private boolean mDidChangeAfterNestedScroll;
    int mInsetOffsetX;
    int mInsetOffsetY;
    final Rect mLastChildRect = new Rect();
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.OpCoordinatorLayout_Layout);
      this.gravity = localTypedArray.getInteger(R.styleable.OpCoordinatorLayout_Layout_android_layout_gravity, 0);
      this.mAnchorId = localTypedArray.getResourceId(R.styleable.OpCoordinatorLayout_Layout_op_layout_anchor, -1);
      this.anchorGravity = localTypedArray.getInteger(R.styleable.OpCoordinatorLayout_Layout_op_layout_anchorGravity, 0);
      this.keyline = localTypedArray.getInteger(R.styleable.OpCoordinatorLayout_Layout_op_layout_keyline, -1);
      this.insetEdge = localTypedArray.getInt(R.styleable.OpCoordinatorLayout_Layout_op_layout_insetEdge, 0);
      this.dodgeInsetEdges = localTypedArray.getInt(R.styleable.OpCoordinatorLayout_Layout_op_layout_dodgeInsetEdges, 0);
      this.mBehaviorResolved = localTypedArray.hasValue(R.styleable.OpCoordinatorLayout_Layout_op_layout_behavior);
      if (this.mBehaviorResolved) {
        this.mBehavior = CoordinatorLayout.parseBehavior(paramContext, paramAttributeSet, localTypedArray.getString(R.styleable.OpCoordinatorLayout_Layout_op_layout_behavior));
      }
      localTypedArray.recycle();
      if (this.mBehavior != null) {
        this.mBehavior.onAttachedToLayoutParams(this);
      }
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
    }
    
    private void resolveAnchorView(View paramView, CoordinatorLayout paramCoordinatorLayout)
    {
      this.mAnchorView = paramCoordinatorLayout.findViewById(this.mAnchorId);
      if (this.mAnchorView != null)
      {
        if (this.mAnchorView == paramCoordinatorLayout)
        {
          if (paramCoordinatorLayout.isInEditMode())
          {
            this.mAnchorDirectChild = null;
            this.mAnchorView = null;
            return;
          }
          throw new IllegalStateException("View can not be anchored to the the parent CoordinatorLayout");
        }
        View localView = this.mAnchorView;
        for (ViewParent localViewParent = this.mAnchorView.getParent(); (localViewParent != paramCoordinatorLayout) && (localViewParent != null); localViewParent = localViewParent.getParent())
        {
          if (localViewParent == paramView)
          {
            if (paramCoordinatorLayout.isInEditMode())
            {
              this.mAnchorDirectChild = null;
              this.mAnchorView = null;
              return;
            }
            throw new IllegalStateException("Anchor must not be a descendant of the anchored view");
          }
          if ((localViewParent instanceof View)) {
            localView = (View)localViewParent;
          }
        }
        this.mAnchorDirectChild = localView;
        return;
      }
      if (paramCoordinatorLayout.isInEditMode())
      {
        this.mAnchorDirectChild = null;
        this.mAnchorView = null;
        return;
      }
      throw new IllegalStateException("Could not find CoordinatorLayout descendant view with id " + paramCoordinatorLayout.getResources().getResourceName(this.mAnchorId) + " to anchor view " + paramView);
    }
    
    private boolean shouldDodge(View paramView, int paramInt)
    {
      boolean bool2 = false;
      int i = GravityCompat.getAbsoluteGravity(((LayoutParams)paramView.getLayoutParams()).insetEdge, paramInt);
      boolean bool1 = bool2;
      if (i != 0)
      {
        bool1 = bool2;
        if ((GravityCompat.getAbsoluteGravity(this.dodgeInsetEdges, paramInt) & i) == i) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    private boolean verifyAnchorView(View paramView, CoordinatorLayout paramCoordinatorLayout)
    {
      if (this.mAnchorView.getId() != this.mAnchorId) {
        return false;
      }
      View localView = this.mAnchorView;
      for (ViewParent localViewParent = this.mAnchorView.getParent(); localViewParent != paramCoordinatorLayout; localViewParent = localViewParent.getParent())
      {
        if ((localViewParent == null) || (localViewParent == paramView))
        {
          this.mAnchorDirectChild = null;
          this.mAnchorView = null;
          return false;
        }
        if ((localViewParent instanceof View)) {
          localView = (View)localViewParent;
        }
      }
      this.mAnchorDirectChild = localView;
      return true;
    }
    
    void acceptNestedScroll(boolean paramBoolean)
    {
      this.mDidAcceptNestedScroll = paramBoolean;
    }
    
    boolean checkAnchorChanged()
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (this.mAnchorView == null)
      {
        bool1 = bool2;
        if (this.mAnchorId != -1) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    boolean dependsOn(CoordinatorLayout paramCoordinatorLayout, View paramView1, View paramView2)
    {
      if ((paramView2 != this.mAnchorDirectChild) && (!shouldDodge(paramView2, ViewCompat.getLayoutDirection(paramCoordinatorLayout))))
      {
        if (this.mBehavior != null) {
          return this.mBehavior.layoutDependsOn(paramCoordinatorLayout, paramView1, paramView2);
        }
      }
      else {
        return true;
      }
      return false;
    }
    
    boolean didBlockInteraction()
    {
      if (this.mBehavior == null) {
        this.mDidBlockInteraction = false;
      }
      return this.mDidBlockInteraction;
    }
    
    View findAnchorView(CoordinatorLayout paramCoordinatorLayout, View paramView)
    {
      if (this.mAnchorId == -1)
      {
        this.mAnchorDirectChild = null;
        this.mAnchorView = null;
        return null;
      }
      if ((this.mAnchorView != null) && (verifyAnchorView(paramView, paramCoordinatorLayout))) {}
      for (;;)
      {
        return this.mAnchorView;
        resolveAnchorView(paramView, paramCoordinatorLayout);
      }
    }
    
    @IdRes
    public int getAnchorId()
    {
      return this.mAnchorId;
    }
    
    @Nullable
    public CoordinatorLayout.Behavior getBehavior()
    {
      return this.mBehavior;
    }
    
    boolean getChangedAfterNestedScroll()
    {
      return this.mDidChangeAfterNestedScroll;
    }
    
    Rect getLastChildRect()
    {
      return this.mLastChildRect;
    }
    
    void invalidateAnchor()
    {
      this.mAnchorDirectChild = null;
      this.mAnchorView = null;
    }
    
    boolean isBlockingInteractionBelow(CoordinatorLayout paramCoordinatorLayout, View paramView)
    {
      if (this.mDidBlockInteraction) {
        return true;
      }
      boolean bool2 = this.mDidBlockInteraction;
      if (this.mBehavior != null) {}
      for (boolean bool1 = this.mBehavior.blocksInteractionBelow(paramCoordinatorLayout, paramView);; bool1 = false)
      {
        bool1 |= bool2;
        this.mDidBlockInteraction = bool1;
        return bool1;
      }
    }
    
    boolean isNestedScrollAccepted()
    {
      return this.mDidAcceptNestedScroll;
    }
    
    void resetChangedAfterNestedScroll()
    {
      this.mDidChangeAfterNestedScroll = false;
    }
    
    void resetNestedScroll()
    {
      this.mDidAcceptNestedScroll = false;
    }
    
    void resetTouchBehaviorTracking()
    {
      this.mDidBlockInteraction = false;
    }
    
    public void setAnchorId(@IdRes int paramInt)
    {
      invalidateAnchor();
      this.mAnchorId = paramInt;
    }
    
    public void setBehavior(@Nullable CoordinatorLayout.Behavior paramBehavior)
    {
      if (this.mBehavior != paramBehavior)
      {
        if (this.mBehavior != null) {
          this.mBehavior.onDetachedFromLayoutParams();
        }
        this.mBehavior = paramBehavior;
        this.mBehaviorTag = null;
        this.mBehaviorResolved = true;
        if (paramBehavior != null) {
          paramBehavior.onAttachedToLayoutParams(this);
        }
      }
    }
    
    void setChangedAfterNestedScroll(boolean paramBoolean)
    {
      this.mDidChangeAfterNestedScroll = paramBoolean;
    }
    
    void setLastChildRect(Rect paramRect)
    {
      this.mLastChildRect.set(paramRect);
    }
  }
  
  class OnPreDrawListener
    implements ViewTreeObserver.OnPreDrawListener
  {
    OnPreDrawListener() {}
    
    public boolean onPreDraw()
    {
      CoordinatorLayout.this.onChildViewsChanged(0);
      return true;
    }
  }
  
  protected static class SavedState
    extends AbsSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks()
    {
      public CoordinatorLayout.SavedState createFromParcel(Parcel paramAnonymousParcel, ClassLoader paramAnonymousClassLoader)
      {
        return new CoordinatorLayout.SavedState(paramAnonymousParcel, paramAnonymousClassLoader);
      }
      
      public CoordinatorLayout.SavedState[] newArray(int paramAnonymousInt)
      {
        return new CoordinatorLayout.SavedState[paramAnonymousInt];
      }
    });
    SparseArray<Parcelable> behaviorStates;
    
    public SavedState(Parcel paramParcel, ClassLoader paramClassLoader)
    {
      super(paramClassLoader);
      int j = paramParcel.readInt();
      int[] arrayOfInt = new int[j];
      paramParcel.readIntArray(arrayOfInt);
      paramParcel = paramParcel.readParcelableArray(paramClassLoader);
      this.behaviorStates = new SparseArray(j);
      int i = 0;
      while (i < j)
      {
        this.behaviorStates.append(arrayOfInt[i], paramParcel[i]);
        i += 1;
      }
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      if (this.behaviorStates != null) {}
      int[] arrayOfInt;
      Parcelable[] arrayOfParcelable;
      for (int i = this.behaviorStates.size();; i = 0)
      {
        paramParcel.writeInt(i);
        arrayOfInt = new int[i];
        arrayOfParcelable = new Parcelable[i];
        int j = 0;
        while (j < i)
        {
          arrayOfInt[j] = this.behaviorStates.keyAt(j);
          arrayOfParcelable[j] = ((Parcelable)this.behaviorStates.valueAt(j));
          j += 1;
        }
      }
      paramParcel.writeIntArray(arrayOfInt);
      paramParcel.writeParcelableArray(arrayOfParcelable, paramInt);
    }
  }
  
  static class ViewElevationComparator
    implements Comparator<View>
  {
    public int compare(View paramView1, View paramView2)
    {
      float f1 = ViewCompat.getZ(paramView1);
      float f2 = ViewCompat.getZ(paramView2);
      if (f1 > f2) {
        return -1;
      }
      if (f1 < f2) {
        return 1;
      }
      return 0;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\design\widget\CoordinatorLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */