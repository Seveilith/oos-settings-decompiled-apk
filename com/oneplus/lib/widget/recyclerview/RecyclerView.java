package com.oneplus.lib.widget.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.database.Observable;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.os.Trace;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.FocusFinder;
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
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.CollectionInfo;
import android.view.accessibility.AccessibilityNodeInfo.CollectionItemInfo;
import android.view.accessibility.AccessibilityRecord;
import android.view.animation.Interpolator;
import android.widget.EdgeEffect;
import android.widget.Scroller;
import com.oneplus.commonctrl.R.styleable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecyclerView
  extends ViewGroup
  implements ScrollingView, NestedScrollingChild
{
  private static final boolean DEBUG = false;
  private static final boolean DISPATCH_TEMP_DETACH = false;
  private static final boolean FORCE_INVALIDATE_DISPLAY_LIST;
  public static final int HORIZONTAL = 0;
  private static final int INVALID_POINTER = -1;
  public static final int INVALID_TYPE = -1;
  private static final Class<?>[] LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE;
  private static final int MAX_SCROLL_DURATION = 2000;
  public static final long NO_ID = -1L;
  public static final int NO_POSITION = -1;
  public static final int SCROLL_STATE_DRAGGING = 1;
  public static final int SCROLL_STATE_IDLE = 0;
  public static final int SCROLL_STATE_SETTLING = 2;
  private static final String TAG = "RecyclerView";
  public static final int TOUCH_SLOP_DEFAULT = 0;
  public static final int TOUCH_SLOP_PAGING = 1;
  private static final String TRACE_BIND_VIEW_TAG = "RV OnBindView";
  private static final String TRACE_CREATE_VIEW_TAG = "RV CreateView";
  private static final String TRACE_HANDLE_ADAPTER_UPDATES_TAG = "RV PartialInvalidate";
  private static final String TRACE_ON_DATA_SET_CHANGE_LAYOUT_TAG = "RV FullInvalidate";
  private static final String TRACE_ON_LAYOUT_TAG = "RV OnLayout";
  private static final String TRACE_SCROLL_TAG = "RV Scroll";
  public static final int VERTICAL = 1;
  private static final Interpolator sQuinticInterpolator;
  private RecyclerViewAccessibilityDelegate mAccessibilityDelegate;
  private final AccessibilityManager mAccessibilityManager;
  private OnItemTouchListener mActiveOnItemTouchListener;
  private Adapter mAdapter;
  AdapterHelper mAdapterHelper;
  private boolean mAdapterUpdateDuringMeasure;
  private EdgeEffect mBottomGlow;
  private ChildDrawingOrderCallback mChildDrawingOrderCallback;
  ChildHelper mChildHelper;
  private boolean mClipToPadding;
  private boolean mDataSetHasChangedAfterLayout = false;
  private boolean mEatRequestLayout;
  private int mEatenAccessibilityChangeFlags;
  private boolean mFirstLayoutComplete;
  private boolean mHasFixedSize;
  private boolean mIgnoreMotionEventTillDown;
  private int mInitialTouchX;
  private int mInitialTouchY;
  private boolean mIsAttached;
  ItemAnimator mItemAnimator = new DefaultItemAnimator();
  private RecyclerView.ItemAnimator.ItemAnimatorListener mItemAnimatorListener = new ItemAnimatorRestoreListener(null);
  private Runnable mItemAnimatorRunner = new Runnable()
  {
    public void run()
    {
      if (RecyclerView.this.mItemAnimator != null) {
        RecyclerView.this.mItemAnimator.runPendingAnimations();
      }
      RecyclerView.-set1(RecyclerView.this, false);
    }
  };
  private final ArrayList<ItemDecoration> mItemDecorations = new ArrayList();
  boolean mItemsAddedOrRemoved = false;
  boolean mItemsChanged = false;
  private int mLastTouchX;
  private int mLastTouchY;
  private LayoutManager mLayout;
  private boolean mLayoutFrozen;
  private int mLayoutOrScrollCounter = 0;
  private boolean mLayoutRequestEaten;
  private EdgeEffect mLeftGlow;
  private final int mMaxFlingVelocity;
  private final int mMinFlingVelocity;
  private final int[] mMinMaxLayoutPositions = new int[2];
  private final int[] mNestedOffsets = new int[2];
  private final RecyclerViewDataObserver mObserver = new RecyclerViewDataObserver(null);
  private List<OnChildAttachStateChangeListener> mOnChildAttachStateListeners;
  private final ArrayList<OnItemTouchListener> mOnItemTouchListeners = new ArrayList();
  private SavedState mPendingSavedState;
  private final boolean mPostUpdatesOnAnimation;
  private boolean mPostedAnimatorRunner = false;
  final Recycler mRecycler = new Recycler();
  private RecyclerListener mRecyclerListener;
  private EdgeEffect mRightGlow;
  private final int[] mScrollConsumed = new int[2];
  private float mScrollFactor = Float.MIN_VALUE;
  private OnScrollListener mScrollListener;
  private List<OnScrollListener> mScrollListeners;
  private final int[] mScrollOffset = new int[2];
  private int mScrollPointerId = -1;
  private int mScrollState = 0;
  private final NestedScrollingChildHelper mScrollingChildHelper;
  final State mState = new State();
  private final Rect mTempRect = new Rect();
  private EdgeEffect mTopGlow;
  private int mTouchSlop;
  private final Runnable mUpdateChildViewsRunnable = new Runnable()
  {
    public void run()
    {
      if (!RecyclerView.-get5(RecyclerView.this)) {
        return;
      }
      if (RecyclerView.-get4(RecyclerView.this))
      {
        Trace.beginSection("RV FullInvalidate");
        RecyclerView.this.dispatchLayout();
        Trace.endSection();
      }
      while (!RecyclerView.this.mAdapterHelper.hasPendingUpdates()) {
        return;
      }
      Trace.beginSection("RV PartialInvalidate");
      RecyclerView.this.eatRequestLayout();
      RecyclerView.this.mAdapterHelper.preProcess();
      if (!RecyclerView.-get10(RecyclerView.this)) {
        RecyclerView.this.rebindUpdatedViewHolders();
      }
      RecyclerView.this.resumeRequestLayout(true);
      Trace.endSection();
    }
  };
  private VelocityTracker mVelocityTracker;
  private final ViewFlinger mViewFlinger = new ViewFlinger();
  
  static
  {
    boolean bool;
    if ((Build.VERSION.SDK_INT == 18) || (Build.VERSION.SDK_INT == 19)) {
      bool = true;
    }
    for (;;)
    {
      FORCE_INVALIDATE_DISPLAY_LIST = bool;
      LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE = new Class[] { Context.class, AttributeSet.class, Integer.TYPE, Integer.TYPE };
      sQuinticInterpolator = new Interpolator()
      {
        public float getInterpolation(float paramAnonymousFloat)
        {
          paramAnonymousFloat -= 1.0F;
          return paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat + 1.0F;
        }
      };
      return;
      if (Build.VERSION.SDK_INT == 20) {
        bool = true;
      } else {
        bool = false;
      }
    }
  }
  
  public RecyclerView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public RecyclerView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public RecyclerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setScrollContainer(true);
    setFocusableInTouchMode(true);
    if (Build.VERSION.SDK_INT >= 16) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.mPostUpdatesOnAnimation = bool1;
      Object localObject = ViewConfiguration.get(paramContext);
      this.mTouchSlop = ((ViewConfiguration)localObject).getScaledTouchSlop();
      this.mMinFlingVelocity = ((ViewConfiguration)localObject).getScaledMinimumFlingVelocity();
      this.mMaxFlingVelocity = ((ViewConfiguration)localObject).getScaledMaximumFlingVelocity();
      bool1 = bool2;
      if (getOverScrollMode() == 2) {
        bool1 = true;
      }
      setWillNotDraw(bool1);
      this.mItemAnimator.setListener(this.mItemAnimatorListener);
      initAdapterManager();
      initChildrenHelper();
      if (getImportantForAccessibility() == 0) {
        setImportantForAccessibility(1);
      }
      this.mAccessibilityManager = ((AccessibilityManager)getContext().getSystemService("accessibility"));
      setAccessibilityDelegateCompat(new RecyclerViewAccessibilityDelegate(this));
      if (paramAttributeSet != null)
      {
        localObject = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.RecyclerView, paramInt, 0);
        String str = ((TypedArray)localObject).getString(R.styleable.RecyclerView_op_layoutManager);
        ((TypedArray)localObject).recycle();
        createLayoutManager(paramContext, str, paramAttributeSet, paramInt, 0);
      }
      this.mScrollingChildHelper = new NestedScrollingChildHelper(this);
      setNestedScrollingEnabled(true);
      return;
    }
  }
  
  private void addAnimatingView(ViewHolder paramViewHolder)
  {
    View localView = paramViewHolder.itemView;
    if (localView.getParent() == this) {}
    for (int i = 1;; i = 0)
    {
      this.mRecycler.unscrapView(getChildViewHolder(localView));
      if (!paramViewHolder.isTmpDetached()) {
        break;
      }
      this.mChildHelper.attachViewToParent(localView, -1, localView.getLayoutParams(), true);
      return;
    }
    if (i == 0)
    {
      this.mChildHelper.addView(localView, true);
      return;
    }
    this.mChildHelper.hide(localView);
  }
  
  private void animateAppearance(ViewHolder paramViewHolder, Rect paramRect, int paramInt1, int paramInt2)
  {
    View localView = paramViewHolder.itemView;
    if ((paramRect != null) && ((paramRect.left != paramInt1) || (paramRect.top != paramInt2)))
    {
      paramViewHolder.setIsRecyclable(false);
      if (this.mItemAnimator.animateMove(paramViewHolder, paramRect.left, paramRect.top, paramInt1, paramInt2)) {
        postAnimationRunner();
      }
    }
    do
    {
      return;
      paramViewHolder.setIsRecyclable(false);
    } while (!this.mItemAnimator.animateAdd(paramViewHolder));
    postAnimationRunner();
  }
  
  private void animateChange(ViewHolder paramViewHolder1, ViewHolder paramViewHolder2)
  {
    paramViewHolder1.setIsRecyclable(false);
    addAnimatingView(paramViewHolder1);
    paramViewHolder1.mShadowedHolder = paramViewHolder2;
    this.mRecycler.unscrapView(paramViewHolder1);
    int k = paramViewHolder1.itemView.getLeft();
    int m = paramViewHolder1.itemView.getTop();
    int i;
    int j;
    if ((paramViewHolder2 == null) || (paramViewHolder2.shouldIgnore()))
    {
      i = k;
      j = m;
    }
    for (;;)
    {
      if (this.mItemAnimator.animateChange(paramViewHolder1, paramViewHolder2, k, m, i, j)) {
        postAnimationRunner();
      }
      return;
      i = paramViewHolder2.itemView.getLeft();
      j = paramViewHolder2.itemView.getTop();
      paramViewHolder2.setIsRecyclable(false);
      paramViewHolder2.mShadowingHolder = paramViewHolder1;
    }
  }
  
  private void animateDisappearance(ItemHolderInfo paramItemHolderInfo)
  {
    View localView = paramItemHolderInfo.holder.itemView;
    addAnimatingView(paramItemHolderInfo.holder);
    int i = paramItemHolderInfo.left;
    int j = paramItemHolderInfo.top;
    int k = localView.getLeft();
    int m = localView.getTop();
    if ((!paramItemHolderInfo.holder.isRemoved()) && ((i != k) || (j != m)))
    {
      paramItemHolderInfo.holder.setIsRecyclable(false);
      localView.layout(k, m, localView.getWidth() + k, localView.getHeight() + m);
      if (this.mItemAnimator.animateMove(paramItemHolderInfo.holder, i, j, k, m)) {
        postAnimationRunner();
      }
    }
    do
    {
      return;
      paramItemHolderInfo.holder.setIsRecyclable(false);
    } while (!this.mItemAnimator.animateRemove(paramItemHolderInfo.holder));
    postAnimationRunner();
  }
  
  private void cancelTouch()
  {
    resetTouch();
    setScrollState(0);
  }
  
  private void considerReleasingGlowsOnScroll(int paramInt1, int paramInt2)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mLeftGlow != null)
    {
      if (this.mLeftGlow.isFinished()) {
        bool1 = bool2;
      }
    }
    else
    {
      bool2 = bool1;
      if (this.mRightGlow != null)
      {
        if (!this.mRightGlow.isFinished()) {
          break label130;
        }
        bool2 = bool1;
      }
      label49:
      bool1 = bool2;
      if (this.mTopGlow != null)
      {
        if (!this.mTopGlow.isFinished()) {
          break label158;
        }
        bool1 = bool2;
      }
      label72:
      bool2 = bool1;
      if (this.mBottomGlow != null)
      {
        if (!this.mBottomGlow.isFinished()) {
          break label186;
        }
        bool2 = bool1;
      }
    }
    for (;;)
    {
      if (bool2) {
        postInvalidateOnAnimation();
      }
      return;
      bool1 = bool2;
      if (paramInt1 <= 0) {
        break;
      }
      this.mLeftGlow.onRelease();
      bool1 = this.mLeftGlow.isFinished();
      break;
      label130:
      bool2 = bool1;
      if (paramInt1 >= 0) {
        break label49;
      }
      this.mRightGlow.onRelease();
      bool2 = bool1 | this.mRightGlow.isFinished();
      break label49;
      label158:
      bool1 = bool2;
      if (paramInt2 <= 0) {
        break label72;
      }
      this.mTopGlow.onRelease();
      bool1 = bool2 | this.mTopGlow.isFinished();
      break label72;
      label186:
      bool2 = bool1;
      if (paramInt2 < 0)
      {
        this.mBottomGlow.onRelease();
        bool2 = bool1 | this.mBottomGlow.isFinished();
      }
    }
  }
  
  private void consumePendingUpdateOperations()
  {
    this.mUpdateChildViewsRunnable.run();
  }
  
  private void createLayoutManager(Context paramContext, String paramString, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    if (paramString != null)
    {
      paramString = paramString.trim();
      if (paramString.length() != 0)
      {
        String str = getFullClassName(paramContext, paramString);
        try
        {
          if (isInEditMode()) {}
          Class localClass;
          for (paramString = getClass().getClassLoader();; paramString = paramContext.getClassLoader())
          {
            localClass = paramString.loadClass(str).asSubclass(LayoutManager.class);
            paramString = null;
            try
            {
              Constructor localConstructor = localClass.getConstructor(LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE);
              paramString = new Object[] { paramContext, paramAttributeSet, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) };
              paramContext = localConstructor;
            }
            catch (NoSuchMethodException localNoSuchMethodException)
            {
              try
              {
                paramContext = localClass.getConstructor(new Class[0]);
              }
              catch (NoSuchMethodException paramContext)
              {
                paramContext.initCause(localNoSuchMethodException);
                throw new IllegalStateException(paramAttributeSet.getPositionDescription() + ": Error creating LayoutManager " + str, paramContext);
              }
            }
            paramContext.setAccessible(true);
            setLayoutManager((LayoutManager)paramContext.newInstance(paramString));
            return;
          }
          return;
        }
        catch (ClassNotFoundException paramContext)
        {
          throw new IllegalStateException(paramAttributeSet.getPositionDescription() + ": Unable to find LayoutManager " + str, paramContext);
        }
        catch (ClassCastException paramContext)
        {
          throw new IllegalStateException(paramAttributeSet.getPositionDescription() + ": Class is not a LayoutManager " + str, paramContext);
        }
        catch (IllegalAccessException paramContext)
        {
          throw new IllegalStateException(paramAttributeSet.getPositionDescription() + ": Cannot access non-public constructor " + str, paramContext);
        }
        catch (InstantiationException paramContext)
        {
          throw new IllegalStateException(paramAttributeSet.getPositionDescription() + ": Could not instantiate the LayoutManager: " + str, paramContext);
        }
        catch (InvocationTargetException paramContext)
        {
          throw new IllegalStateException(paramAttributeSet.getPositionDescription() + ": Could not instantiate the LayoutManager: " + str, paramContext);
        }
      }
    }
  }
  
  private void defaultOnMeasure(int paramInt1, int paramInt2)
  {
    int j = View.MeasureSpec.getMode(paramInt1);
    int i = View.MeasureSpec.getMode(paramInt2);
    paramInt1 = View.MeasureSpec.getSize(paramInt1);
    paramInt2 = View.MeasureSpec.getSize(paramInt2);
    switch (j)
    {
    default: 
      paramInt1 = getMinimumWidth();
      switch (i)
      {
      default: 
        paramInt2 = getMinimumHeight();
      }
      break;
    }
    for (;;)
    {
      setMeasuredDimension(paramInt1, paramInt2);
      return;
      break;
    }
  }
  
  private boolean didChildRangeChange(int paramInt1, int paramInt2)
  {
    int j = this.mChildHelper.getChildCount();
    if (j == 0) {
      return (paramInt1 != 0) || (paramInt2 != 0);
    }
    int i = 0;
    if (i < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
      if (localViewHolder.shouldIgnore()) {}
      int k;
      do
      {
        i += 1;
        break;
        k = localViewHolder.getLayoutPosition();
      } while ((k >= paramInt1) && (k <= paramInt2));
      return true;
    }
    return false;
  }
  
  private void dispatchChildAttached(View paramView)
  {
    ViewHolder localViewHolder = getChildViewHolderInt(paramView);
    onChildAttachedToWindow(paramView);
    if ((this.mAdapter != null) && (localViewHolder != null)) {
      this.mAdapter.onViewAttachedToWindow(localViewHolder);
    }
    if (this.mOnChildAttachStateListeners != null)
    {
      int i = this.mOnChildAttachStateListeners.size() - 1;
      while (i >= 0)
      {
        ((OnChildAttachStateChangeListener)this.mOnChildAttachStateListeners.get(i)).onChildViewAttachedToWindow(paramView);
        i -= 1;
      }
    }
  }
  
  private void dispatchChildDetached(View paramView)
  {
    ViewHolder localViewHolder = getChildViewHolderInt(paramView);
    onChildDetachedFromWindow(paramView);
    if ((this.mAdapter != null) && (localViewHolder != null)) {
      this.mAdapter.onViewDetachedFromWindow(localViewHolder);
    }
    if (this.mOnChildAttachStateListeners != null)
    {
      int i = this.mOnChildAttachStateListeners.size() - 1;
      while (i >= 0)
      {
        ((OnChildAttachStateChangeListener)this.mOnChildAttachStateListeners.get(i)).onChildViewDetachedFromWindow(paramView);
        i -= 1;
      }
    }
  }
  
  private void dispatchContentChangedIfNecessary()
  {
    int i = this.mEatenAccessibilityChangeFlags;
    this.mEatenAccessibilityChangeFlags = 0;
    if ((i != 0) && (isAccessibilityEnabled()))
    {
      AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain();
      localAccessibilityEvent.setEventType(2048);
      localAccessibilityEvent.setContentChangeTypes(i);
      sendAccessibilityEventUnchecked(localAccessibilityEvent);
    }
  }
  
  private boolean dispatchOnItemTouch(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getAction();
    int j;
    if (this.mActiveOnItemTouchListener != null)
    {
      if (i == 0) {
        this.mActiveOnItemTouchListener = null;
      }
    }
    else
    {
      if (i == 0) {
        break label108;
      }
      j = this.mOnItemTouchListeners.size();
      i = 0;
    }
    while (i < j)
    {
      OnItemTouchListener localOnItemTouchListener = (OnItemTouchListener)this.mOnItemTouchListeners.get(i);
      if (localOnItemTouchListener.onInterceptTouchEvent(this, paramMotionEvent))
      {
        this.mActiveOnItemTouchListener = localOnItemTouchListener;
        return true;
        this.mActiveOnItemTouchListener.onTouchEvent(this, paramMotionEvent);
        if ((i == 3) || (i == 1)) {
          this.mActiveOnItemTouchListener = null;
        }
        return true;
      }
      i += 1;
    }
    label108:
    return false;
  }
  
  private boolean dispatchOnItemTouchIntercept(MotionEvent paramMotionEvent)
  {
    int j = paramMotionEvent.getAction();
    if ((j == 3) || (j == 0)) {
      this.mActiveOnItemTouchListener = null;
    }
    int k = this.mOnItemTouchListeners.size();
    int i = 0;
    while (i < k)
    {
      OnItemTouchListener localOnItemTouchListener = (OnItemTouchListener)this.mOnItemTouchListeners.get(i);
      if ((localOnItemTouchListener.onInterceptTouchEvent(this, paramMotionEvent)) && (j != 3))
      {
        this.mActiveOnItemTouchListener = localOnItemTouchListener;
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private void findMinMaxChildLayoutPositions(int[] paramArrayOfInt)
  {
    int i2 = this.mChildHelper.getChildCount();
    if (i2 == 0)
    {
      paramArrayOfInt[0] = 0;
      paramArrayOfInt[1] = 0;
      return;
    }
    int j = Integer.MAX_VALUE;
    int m = Integer.MIN_VALUE;
    int k = 0;
    if (k < i2)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getChildAt(k));
      int i1;
      if (localViewHolder.shouldIgnore())
      {
        i1 = j;
        j = m;
      }
      for (;;)
      {
        k += 1;
        m = j;
        j = i1;
        break;
        int n = localViewHolder.getLayoutPosition();
        int i = j;
        if (n < j) {
          i = n;
        }
        j = m;
        i1 = i;
        if (n > m)
        {
          j = n;
          i1 = i;
        }
      }
    }
    paramArrayOfInt[0] = j;
    paramArrayOfInt[1] = m;
  }
  
  private int getAdapterPositionFor(ViewHolder paramViewHolder)
  {
    if ((!paramViewHolder.hasAnyOfTheFlags(524)) && (paramViewHolder.isBound())) {
      return this.mAdapterHelper.applyPendingUpdatesToPosition(paramViewHolder.mPosition);
    }
    return -1;
  }
  
  static ViewHolder getChildViewHolderInt(View paramView)
  {
    if (paramView == null) {
      return null;
    }
    return ((LayoutParams)paramView.getLayoutParams()).mViewHolder;
  }
  
  private String getFullClassName(Context paramContext, String paramString)
  {
    if (paramString.charAt(0) == '.') {
      return paramContext.getPackageName() + paramString;
    }
    if (paramString.contains(".")) {
      return paramString;
    }
    return RecyclerView.class.getPackage().getName() + '.' + paramString;
  }
  
  private float getScrollFactor()
  {
    if (this.mScrollFactor == Float.MIN_VALUE)
    {
      TypedValue localTypedValue = new TypedValue();
      if (getContext().getTheme().resolveAttribute(16842829, localTypedValue, true)) {
        this.mScrollFactor = localTypedValue.getDimension(getContext().getResources().getDisplayMetrics());
      }
    }
    else
    {
      return this.mScrollFactor;
    }
    return 0.0F;
  }
  
  private void initChildrenHelper()
  {
    this.mChildHelper = new ChildHelper(new ChildHelper.Callback()
    {
      public void addView(View paramAnonymousView, int paramAnonymousInt)
      {
        RecyclerView.this.addView(paramAnonymousView, paramAnonymousInt);
        RecyclerView.-wrap9(RecyclerView.this, paramAnonymousView);
      }
      
      public void attachViewToParent(View paramAnonymousView, int paramAnonymousInt, ViewGroup.LayoutParams paramAnonymousLayoutParams)
      {
        RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramAnonymousView);
        if (localViewHolder != null)
        {
          if ((localViewHolder.isTmpDetached()) || (localViewHolder.shouldIgnore())) {
            localViewHolder.clearTmpDetachFlag();
          }
        }
        else
        {
          RecyclerView.-wrap4(RecyclerView.this, paramAnonymousView, paramAnonymousInt, paramAnonymousLayoutParams);
          return;
        }
        throw new IllegalArgumentException("Called attach on a child which is not detached: " + localViewHolder);
      }
      
      public void detachViewFromParent(int paramAnonymousInt)
      {
        Object localObject = getChildAt(paramAnonymousInt);
        if (localObject != null)
        {
          localObject = RecyclerView.getChildViewHolderInt((View)localObject);
          if (localObject != null)
          {
            if ((((RecyclerView.ViewHolder)localObject).isTmpDetached()) && (!((RecyclerView.ViewHolder)localObject).shouldIgnore())) {
              break label49;
            }
            ((RecyclerView.ViewHolder)localObject).addFlags(256);
          }
        }
        RecyclerView.-wrap8(RecyclerView.this, paramAnonymousInt);
        return;
        label49:
        throw new IllegalArgumentException("called detach on an already detached child " + localObject);
      }
      
      public View getChildAt(int paramAnonymousInt)
      {
        return RecyclerView.this.getChildAt(paramAnonymousInt);
      }
      
      public int getChildCount()
      {
        return RecyclerView.this.getChildCount();
      }
      
      public RecyclerView.ViewHolder getChildViewHolder(View paramAnonymousView)
      {
        return RecyclerView.getChildViewHolderInt(paramAnonymousView);
      }
      
      public int indexOfChild(View paramAnonymousView)
      {
        return RecyclerView.this.indexOfChild(paramAnonymousView);
      }
      
      public void onEnteredHiddenState(View paramAnonymousView)
      {
        paramAnonymousView = RecyclerView.getChildViewHolderInt(paramAnonymousView);
        if (paramAnonymousView != null) {
          RecyclerView.ViewHolder.-wrap2(paramAnonymousView);
        }
      }
      
      public void onLeftHiddenState(View paramAnonymousView)
      {
        paramAnonymousView = RecyclerView.getChildViewHolderInt(paramAnonymousView);
        if (paramAnonymousView != null) {
          RecyclerView.ViewHolder.-wrap3(paramAnonymousView);
        }
      }
      
      public void removeAllViews()
      {
        int j = getChildCount();
        int i = 0;
        while (i < j)
        {
          RecyclerView.-wrap10(RecyclerView.this, getChildAt(i));
          i += 1;
        }
        RecyclerView.this.removeAllViews();
      }
      
      public void removeViewAt(int paramAnonymousInt)
      {
        View localView = RecyclerView.this.getChildAt(paramAnonymousInt);
        if (localView != null) {
          RecyclerView.-wrap10(RecyclerView.this, localView);
        }
        RecyclerView.this.removeViewAt(paramAnonymousInt);
      }
    });
  }
  
  private void jumpToPositionForSmoothScroller(int paramInt)
  {
    if (this.mLayout == null) {
      return;
    }
    this.mLayout.scrollToPosition(paramInt);
    awakenScrollBars();
  }
  
  private void onEnterLayoutOrScroll()
  {
    this.mLayoutOrScrollCounter += 1;
  }
  
  private void onExitLayoutOrScroll()
  {
    this.mLayoutOrScrollCounter -= 1;
    if (this.mLayoutOrScrollCounter < 1)
    {
      this.mLayoutOrScrollCounter = 0;
      dispatchContentChangedIfNecessary();
    }
  }
  
  private void onPointerUp(MotionEvent paramMotionEvent)
  {
    int i = 0;
    int j = paramMotionEvent.getActionIndex();
    if (paramMotionEvent.getPointerId(j) == this.mScrollPointerId)
    {
      if (j == 0) {
        i = 1;
      }
      this.mScrollPointerId = paramMotionEvent.getPointerId(i);
      j = (int)(paramMotionEvent.getX(i) + 0.5F);
      this.mLastTouchX = j;
      this.mInitialTouchX = j;
      i = (int)(paramMotionEvent.getY(i) + 0.5F);
      this.mLastTouchY = i;
      this.mInitialTouchY = i;
    }
  }
  
  private void postAnimationRunner()
  {
    if ((!this.mPostedAnimatorRunner) && (this.mIsAttached))
    {
      postOnAnimation(this.mItemAnimatorRunner);
      this.mPostedAnimatorRunner = true;
    }
  }
  
  private boolean predictiveItemAnimationsEnabled()
  {
    if (this.mItemAnimator != null) {
      return this.mLayout.supportsPredictiveItemAnimations();
    }
    return false;
  }
  
  private void processAdapterUpdatesAndSetAnimationFlags()
  {
    boolean bool3 = false;
    if (this.mDataSetHasChangedAfterLayout)
    {
      this.mAdapterHelper.reset();
      markKnownViewsInvalid();
      this.mLayout.onItemsChanged(this);
    }
    boolean bool1;
    label85:
    State localState;
    if ((this.mItemAnimator != null) && (this.mLayout.supportsPredictiveItemAnimations()))
    {
      this.mAdapterHelper.preProcess();
      if ((this.mItemsAddedOrRemoved) && (!this.mItemsChanged)) {
        break label199;
      }
      if (this.mItemsAddedOrRemoved) {
        break label204;
      }
      if (!this.mItemsChanged) {
        break label209;
      }
      bool1 = supportsChangeAnimations();
      localState = this.mState;
      if ((!this.mFirstLayoutComplete) || (this.mItemAnimator == null) || ((!this.mDataSetHasChangedAfterLayout) && (!bool1) && (!LayoutManager.-get0(this.mLayout)))) {
        break label219;
      }
      if (!this.mDataSetHasChangedAfterLayout) {
        break label214;
      }
      bool2 = this.mAdapter.hasStableIds();
      label141:
      State.-set4(localState, bool2);
      localState = this.mState;
      bool2 = bool3;
      if (State.-get3(this.mState))
      {
        bool2 = bool3;
        if (bool1) {
          if (!this.mDataSetHasChangedAfterLayout) {
            break label224;
          }
        }
      }
    }
    label199:
    label204:
    label209:
    label214:
    label219:
    label224:
    for (boolean bool2 = bool3;; bool2 = predictiveItemAnimationsEnabled())
    {
      State.-set3(localState, bool2);
      return;
      this.mAdapterHelper.consumeUpdatesInOnePass();
      break;
      bool1 = true;
      break label85;
      bool1 = true;
      break label85;
      bool1 = false;
      break label85;
      bool2 = true;
      break label141;
      bool2 = false;
      break label141;
    }
  }
  
  private void processDisappearingList(ArrayMap<View, Rect> paramArrayMap)
  {
    List localList = this.mState.mDisappearingViewsInLayoutPass;
    int i = localList.size() - 1;
    if (i >= 0)
    {
      View localView = (View)localList.get(i);
      ViewHolder localViewHolder = getChildViewHolderInt(localView);
      ItemHolderInfo localItemHolderInfo = (ItemHolderInfo)this.mState.mPreLayoutHolderMap.remove(localViewHolder);
      if (!this.mState.isPreLayout()) {
        this.mState.mPostLayoutHolderMap.remove(localViewHolder);
      }
      if (paramArrayMap.remove(localView) != null) {
        this.mLayout.removeAndRecycleView(localView, this.mRecycler);
      }
      for (;;)
      {
        i -= 1;
        break;
        if (localItemHolderInfo != null) {
          animateDisappearance(localItemHolderInfo);
        } else {
          animateDisappearance(new ItemHolderInfo(localViewHolder, localView.getLeft(), localView.getTop(), localView.getRight(), localView.getBottom()));
        }
      }
    }
    localList.clear();
  }
  
  private void pullGlows(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    int i = 0;
    if (paramFloat2 < 0.0F)
    {
      ensureLeftGlow();
      this.mLeftGlow.onPull(-paramFloat2 / getWidth(), 1.0F - paramFloat3 / getHeight());
      i = 1;
      break label92;
      label40:
      if (paramFloat4 >= 0.0F) {
        break label130;
      }
      ensureTopGlow();
      this.mTopGlow.onPull(-paramFloat4 / getHeight(), paramFloat1 / getWidth());
      i = 1;
      label77:
      if ((i == 0) && (paramFloat2 == 0.0F)) {
        break label171;
      }
    }
    for (;;)
    {
      postInvalidateOnAnimation();
      label92:
      return;
      if (paramFloat2 <= 0.0F) {
        break label40;
      }
      ensureRightGlow();
      this.mRightGlow.onPull(paramFloat2 / getWidth(), paramFloat3 / getHeight());
      i = 1;
      break label40;
      label130:
      if (paramFloat4 <= 0.0F) {
        break label77;
      }
      ensureBottomGlow();
      this.mBottomGlow.onPull(paramFloat4 / getHeight(), 1.0F - paramFloat1 / getWidth());
      i = 1;
      break label77;
      label171:
      if (paramFloat4 == 0.0F) {
        break;
      }
    }
  }
  
  private void releaseGlows()
  {
    boolean bool2 = false;
    if (this.mLeftGlow != null)
    {
      this.mLeftGlow.onRelease();
      bool2 = this.mLeftGlow.isFinished();
    }
    boolean bool1 = bool2;
    if (this.mTopGlow != null)
    {
      this.mTopGlow.onRelease();
      bool1 = bool2 | this.mTopGlow.isFinished();
    }
    bool2 = bool1;
    if (this.mRightGlow != null)
    {
      this.mRightGlow.onRelease();
      bool2 = bool1 | this.mRightGlow.isFinished();
    }
    bool1 = bool2;
    if (this.mBottomGlow != null)
    {
      this.mBottomGlow.onRelease();
      bool1 = bool2 | this.mBottomGlow.isFinished();
    }
    if (bool1) {
      postInvalidateOnAnimation();
    }
  }
  
  private boolean removeAnimatingView(View paramView)
  {
    eatRequestLayout();
    boolean bool = this.mChildHelper.removeViewIfHidden(paramView);
    if (bool)
    {
      paramView = getChildViewHolderInt(paramView);
      this.mRecycler.unscrapView(paramView);
      this.mRecycler.recycleViewHolderInternal(paramView);
    }
    resumeRequestLayout(false);
    return bool;
  }
  
  private void resetTouch()
  {
    if (this.mVelocityTracker != null) {
      this.mVelocityTracker.clear();
    }
    stopNestedScroll();
    releaseGlows();
  }
  
  private void setAdapterInternal(Adapter paramAdapter, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mAdapter != null)
    {
      this.mAdapter.unregisterAdapterDataObserver(this.mObserver);
      this.mAdapter.onDetachedFromRecyclerView(this);
    }
    if ((!paramBoolean1) || (paramBoolean2))
    {
      if (this.mItemAnimator != null) {
        this.mItemAnimator.endAnimations();
      }
      if (this.mLayout != null)
      {
        this.mLayout.removeAndRecycleAllViews(this.mRecycler);
        this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
      }
      this.mRecycler.clear();
    }
    this.mAdapterHelper.reset();
    Adapter localAdapter = this.mAdapter;
    this.mAdapter = paramAdapter;
    if (paramAdapter != null)
    {
      paramAdapter.registerAdapterDataObserver(this.mObserver);
      paramAdapter.onAttachedToRecyclerView(this);
    }
    if (this.mLayout != null) {
      this.mLayout.onAdapterChanged(localAdapter, this.mAdapter);
    }
    this.mRecycler.onAdapterChanged(localAdapter, this.mAdapter, paramBoolean1);
    State.-set5(this.mState, true);
    markKnownViewsInvalid();
  }
  
  private void setDataSetChangedAfterLayout()
  {
    if (this.mDataSetHasChangedAfterLayout) {
      return;
    }
    this.mDataSetHasChangedAfterLayout = true;
    int j = this.mChildHelper.getUnfilteredChildCount();
    int i = 0;
    if (i < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
      if ((localViewHolder == null) || (localViewHolder.shouldIgnore())) {}
      for (;;)
      {
        i += 1;
        break;
        localViewHolder.addFlags(512);
      }
    }
    this.mRecycler.setAdapterPositionsAsUnknown();
  }
  
  private void setScrollState(int paramInt)
  {
    if (paramInt == this.mScrollState) {
      return;
    }
    this.mScrollState = paramInt;
    if (paramInt != 2) {
      stopScrollersInternal();
    }
    dispatchOnScrollStateChanged(paramInt);
  }
  
  private void stopScrollersInternal()
  {
    this.mViewFlinger.stop();
    if (this.mLayout != null) {
      this.mLayout.stopSmoothScroller();
    }
  }
  
  private boolean supportsChangeAnimations()
  {
    if (this.mItemAnimator != null) {
      return this.mItemAnimator.getSupportsChangeAnimations();
    }
    return false;
  }
  
  void absorbGlows(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0)
    {
      ensureLeftGlow();
      this.mLeftGlow.onAbsorb(-paramInt1);
      if (paramInt2 >= 0) {
        break label66;
      }
      ensureTopGlow();
      this.mTopGlow.onAbsorb(-paramInt2);
    }
    for (;;)
    {
      if ((paramInt1 != 0) || (paramInt2 != 0)) {
        postInvalidateOnAnimation();
      }
      return;
      if (paramInt1 <= 0) {
        break;
      }
      ensureRightGlow();
      this.mRightGlow.onAbsorb(paramInt1);
      break;
      label66:
      if (paramInt2 > 0)
      {
        ensureBottomGlow();
        this.mBottomGlow.onAbsorb(paramInt2);
      }
    }
  }
  
  public void addFocusables(ArrayList<View> paramArrayList, int paramInt1, int paramInt2)
  {
    if ((this.mLayout != null) && (this.mLayout.onAddFocusables(this, paramArrayList, paramInt1, paramInt2))) {
      return;
    }
    super.addFocusables(paramArrayList, paramInt1, paramInt2);
  }
  
  public void addItemDecoration(ItemDecoration paramItemDecoration)
  {
    addItemDecoration(paramItemDecoration, -1);
  }
  
  public void addItemDecoration(ItemDecoration paramItemDecoration, int paramInt)
  {
    if (this.mLayout != null) {
      this.mLayout.assertNotInLayoutOrScroll("Cannot add item decoration during a scroll  or layout");
    }
    if (this.mItemDecorations.isEmpty()) {
      setWillNotDraw(false);
    }
    if (paramInt < 0) {
      this.mItemDecorations.add(paramItemDecoration);
    }
    for (;;)
    {
      markItemDecorInsetsDirty();
      requestLayout();
      return;
      this.mItemDecorations.add(paramInt, paramItemDecoration);
    }
  }
  
  public void addOnChildAttachStateChangeListener(OnChildAttachStateChangeListener paramOnChildAttachStateChangeListener)
  {
    if (this.mOnChildAttachStateListeners == null) {
      this.mOnChildAttachStateListeners = new ArrayList();
    }
    this.mOnChildAttachStateListeners.add(paramOnChildAttachStateChangeListener);
  }
  
  public void addOnItemTouchListener(OnItemTouchListener paramOnItemTouchListener)
  {
    this.mOnItemTouchListeners.add(paramOnItemTouchListener);
  }
  
  public void addOnScrollListener(OnScrollListener paramOnScrollListener)
  {
    if (this.mScrollListeners == null) {
      this.mScrollListeners = new ArrayList();
    }
    this.mScrollListeners.add(paramOnScrollListener);
  }
  
  void assertInLayoutOrScroll(String paramString)
  {
    if (!isComputingLayout())
    {
      if (paramString == null) {
        throw new IllegalStateException("Cannot call this method unless RecyclerView is computing a layout or scrolling");
      }
      throw new IllegalStateException(paramString);
    }
  }
  
  void assertNotInLayoutOrScroll(String paramString)
  {
    if (isComputingLayout())
    {
      if (paramString == null) {
        throw new IllegalStateException("Cannot call this method while RecyclerView is computing a layout or scrolling");
      }
      throw new IllegalStateException(paramString);
    }
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((paramLayoutParams instanceof LayoutParams)) {
      return this.mLayout.checkLayoutParams((LayoutParams)paramLayoutParams);
    }
    return false;
  }
  
  void clearOldPositions()
  {
    int j = this.mChildHelper.getUnfilteredChildCount();
    int i = 0;
    while (i < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
      if (!localViewHolder.shouldIgnore()) {
        localViewHolder.clearOldPosition();
      }
      i += 1;
    }
    this.mRecycler.clearOldPositions();
  }
  
  public void clearOnChildAttachStateChangeListeners()
  {
    if (this.mOnChildAttachStateListeners != null) {
      this.mOnChildAttachStateListeners.clear();
    }
  }
  
  public void clearOnScrollListeners()
  {
    if (this.mScrollListeners != null) {
      this.mScrollListeners.clear();
    }
  }
  
  public int computeHorizontalScrollExtent()
  {
    if (this.mLayout.canScrollHorizontally()) {
      return this.mLayout.computeHorizontalScrollExtent(this.mState);
    }
    return 0;
  }
  
  public int computeHorizontalScrollOffset()
  {
    if (this.mLayout.canScrollHorizontally()) {
      return this.mLayout.computeHorizontalScrollOffset(this.mState);
    }
    return 0;
  }
  
  public int computeHorizontalScrollRange()
  {
    if (this.mLayout.canScrollHorizontally()) {
      return this.mLayout.computeHorizontalScrollRange(this.mState);
    }
    return 0;
  }
  
  public int computeVerticalScrollExtent()
  {
    if (this.mLayout.canScrollVertically()) {
      return this.mLayout.computeVerticalScrollExtent(this.mState);
    }
    return 0;
  }
  
  public int computeVerticalScrollOffset()
  {
    if (this.mLayout.canScrollVertically()) {
      return this.mLayout.computeVerticalScrollOffset(this.mState);
    }
    return 0;
  }
  
  public int computeVerticalScrollRange()
  {
    if (this.mLayout.canScrollVertically()) {
      return this.mLayout.computeVerticalScrollRange(this.mState);
    }
    return 0;
  }
  
  void dispatchLayout()
  {
    if (this.mAdapter == null)
    {
      Log.e("RecyclerView", "No adapter attached; skipping layout");
      return;
    }
    if (this.mLayout == null)
    {
      Log.e("RecyclerView", "No layout manager attached; skipping layout");
      return;
    }
    this.mState.mDisappearingViewsInLayoutPass.clear();
    eatRequestLayout();
    onEnterLayoutOrScroll();
    processAdapterUpdatesAndSetAnimationFlags();
    Object localObject2 = this.mState;
    if ((State.-get3(this.mState)) && (this.mItemsChanged) && (supportsChangeAnimations())) {}
    Object localObject3;
    for (Object localObject1 = new ArrayMap();; localObject1 = null)
    {
      ((State)localObject2).mOldChangedHolders = ((ArrayMap)localObject1);
      this.mItemsChanged = false;
      this.mItemsAddedOrRemoved = false;
      localObject1 = null;
      State.-set1(this.mState, State.-get2(this.mState));
      this.mState.mItemCount = this.mAdapter.getItemCount();
      findMinMaxChildLayoutPositions(this.mMinMaxLayoutPositions);
      if (!State.-get3(this.mState)) {
        break;
      }
      this.mState.mPreLayoutHolderMap.clear();
      this.mState.mPostLayoutHolderMap.clear();
      j = this.mChildHelper.getChildCount();
      i = 0;
      while (i < j)
      {
        localObject2 = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
        if ((!((ViewHolder)localObject2).shouldIgnore()) && ((!((ViewHolder)localObject2).isInvalid()) || (this.mAdapter.hasStableIds())))
        {
          localObject3 = ((ViewHolder)localObject2).itemView;
          this.mState.mPreLayoutHolderMap.put(localObject2, new ItemHolderInfo((ViewHolder)localObject2, ((View)localObject3).getLeft(), ((View)localObject3).getTop(), ((View)localObject3).getRight(), ((View)localObject3).getBottom()));
        }
        i += 1;
      }
    }
    long l;
    boolean bool;
    if (State.-get2(this.mState))
    {
      saveOldPositions();
      if (this.mState.mOldChangedHolders != null)
      {
        j = this.mChildHelper.getChildCount();
        i = 0;
        if (i < j)
        {
          localObject1 = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
          if ((!((ViewHolder)localObject1).isChanged()) || (((ViewHolder)localObject1).isRemoved())) {}
          for (;;)
          {
            i += 1;
            break;
            if (!((ViewHolder)localObject1).shouldIgnore())
            {
              l = getChangedHolderKey((ViewHolder)localObject1);
              this.mState.mOldChangedHolders.put(Long.valueOf(l), localObject1);
              this.mState.mPreLayoutHolderMap.remove(localObject1);
            }
          }
        }
      }
      bool = State.-get4(this.mState);
      State.-set5(this.mState, false);
      this.mLayout.onLayoutChildren(this.mRecycler, this.mState);
      State.-set5(this.mState, bool);
      localObject2 = new ArrayMap();
      i = 0;
      while (i < this.mChildHelper.getChildCount())
      {
        int m = 0;
        localObject1 = this.mChildHelper.getChildAt(i);
        if (getChildViewHolderInt((View)localObject1).shouldIgnore())
        {
          i += 1;
        }
        else
        {
          j = 0;
          for (;;)
          {
            int k = m;
            if (j < this.mState.mPreLayoutHolderMap.size())
            {
              if (((ViewHolder)this.mState.mPreLayoutHolderMap.keyAt(j)).itemView == localObject1) {
                k = 1;
              }
            }
            else
            {
              if (k != 0) {
                break;
              }
              ((ArrayMap)localObject2).put(localObject1, new Rect(((View)localObject1).getLeft(), ((View)localObject1).getTop(), ((View)localObject1).getRight(), ((View)localObject1).getBottom()));
              break;
            }
            j += 1;
          }
        }
      }
      clearOldPositions();
      this.mAdapterHelper.consumePostponedUpdates();
      this.mState.mItemCount = this.mAdapter.getItemCount();
      State.-set0(this.mState, 0);
      State.-set1(this.mState, false);
      this.mLayout.onLayoutChildren(this.mRecycler, this.mState);
      State.-set5(this.mState, false);
      this.mPendingSavedState = null;
      localObject1 = this.mState;
      if ((!State.-get3(this.mState)) || (this.mItemAnimator == null)) {
        break label921;
      }
      bool = true;
      label711:
      State.-set4((State)localObject1, bool);
      if (!State.-get3(this.mState)) {
        break label1592;
      }
      if (this.mState.mOldChangedHolders == null) {
        break label927;
      }
      localObject1 = new ArrayMap();
      label748:
      j = this.mChildHelper.getChildCount();
      i = 0;
      label758:
      if (i >= j) {
        break label1032;
      }
      localObject3 = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
      if (!((ViewHolder)localObject3).shouldIgnore()) {
        break label933;
      }
    }
    label921:
    label927:
    label933:
    Object localObject4;
    for (;;)
    {
      i += 1;
      break label758;
      clearOldPositions();
      this.mAdapterHelper.consumeUpdatesInOnePass();
      localObject2 = localObject1;
      if (this.mState.mOldChangedHolders == null) {
        break;
      }
      j = this.mChildHelper.getChildCount();
      i = 0;
      localObject2 = localObject1;
      if (i >= j) {
        break;
      }
      localObject2 = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
      if ((!((ViewHolder)localObject2).isChanged()) || (((ViewHolder)localObject2).isRemoved())) {}
      for (;;)
      {
        i += 1;
        break;
        if (!((ViewHolder)localObject2).shouldIgnore())
        {
          l = getChangedHolderKey((ViewHolder)localObject2);
          this.mState.mOldChangedHolders.put(Long.valueOf(l), localObject2);
          this.mState.mPreLayoutHolderMap.remove(localObject2);
        }
      }
      bool = false;
      break label711;
      localObject1 = null;
      break label748;
      localObject4 = ((ViewHolder)localObject3).itemView;
      l = getChangedHolderKey((ViewHolder)localObject3);
      if ((localObject1 != null) && (this.mState.mOldChangedHolders.get(Long.valueOf(l)) != null)) {
        ((ArrayMap)localObject1).put(Long.valueOf(l), localObject3);
      } else {
        this.mState.mPostLayoutHolderMap.put(localObject3, new ItemHolderInfo((ViewHolder)localObject3, ((View)localObject4).getLeft(), ((View)localObject4).getTop(), ((View)localObject4).getRight(), ((View)localObject4).getBottom()));
      }
    }
    label1032:
    processDisappearingList((ArrayMap)localObject2);
    int i = this.mState.mPreLayoutHolderMap.size() - 1;
    while (i >= 0)
    {
      localObject3 = (ViewHolder)this.mState.mPreLayoutHolderMap.keyAt(i);
      if (!this.mState.mPostLayoutHolderMap.containsKey(localObject3))
      {
        localObject3 = (ItemHolderInfo)this.mState.mPreLayoutHolderMap.valueAt(i);
        this.mState.mPreLayoutHolderMap.removeAt(i);
        localObject4 = ((ItemHolderInfo)localObject3).holder.itemView;
        this.mRecycler.unscrapView(((ItemHolderInfo)localObject3).holder);
        animateDisappearance((ItemHolderInfo)localObject3);
      }
      i -= 1;
    }
    i = this.mState.mPostLayoutHolderMap.size();
    if (i > 0)
    {
      i -= 1;
      while (i >= 0)
      {
        localObject4 = (ViewHolder)this.mState.mPostLayoutHolderMap.keyAt(i);
        ItemHolderInfo localItemHolderInfo = (ItemHolderInfo)this.mState.mPostLayoutHolderMap.valueAt(i);
        if ((!this.mState.mPreLayoutHolderMap.isEmpty()) && (this.mState.mPreLayoutHolderMap.containsKey(localObject4)))
        {
          i -= 1;
        }
        else
        {
          this.mState.mPostLayoutHolderMap.removeAt(i);
          if (localObject2 != null) {}
          for (localObject3 = (Rect)((ArrayMap)localObject2).get(((ViewHolder)localObject4).itemView);; localObject3 = null)
          {
            animateAppearance((ViewHolder)localObject4, (Rect)localObject3, localItemHolderInfo.left, localItemHolderInfo.top);
            break;
          }
        }
      }
    }
    int j = this.mState.mPostLayoutHolderMap.size();
    i = 0;
    while (i < j)
    {
      localObject2 = (ViewHolder)this.mState.mPostLayoutHolderMap.keyAt(i);
      localObject3 = (ItemHolderInfo)this.mState.mPostLayoutHolderMap.valueAt(i);
      localObject4 = (ItemHolderInfo)this.mState.mPreLayoutHolderMap.get(localObject2);
      if ((localObject4 != null) && (localObject3 != null) && ((((ItemHolderInfo)localObject4).left != ((ItemHolderInfo)localObject3).left) || (((ItemHolderInfo)localObject4).top != ((ItemHolderInfo)localObject3).top)))
      {
        ((ViewHolder)localObject2).setIsRecyclable(false);
        if (this.mItemAnimator.animateMove((ViewHolder)localObject2, ((ItemHolderInfo)localObject4).left, ((ItemHolderInfo)localObject4).top, ((ItemHolderInfo)localObject3).left, ((ItemHolderInfo)localObject3).top)) {
          postAnimationRunner();
        }
      }
      i += 1;
    }
    if (this.mState.mOldChangedHolders != null)
    {
      i = this.mState.mOldChangedHolders.size();
      i -= 1;
      label1475:
      if (i < 0) {
        break label1592;
      }
      l = ((Long)this.mState.mOldChangedHolders.keyAt(i)).longValue();
      localObject2 = (ViewHolder)this.mState.mOldChangedHolders.get(Long.valueOf(l));
      localObject3 = ((ViewHolder)localObject2).itemView;
      if (!((ViewHolder)localObject2).shouldIgnore()) {
        break label1545;
      }
    }
    for (;;)
    {
      i -= 1;
      break label1475;
      i = 0;
      break;
      label1545:
      if ((Recycler.-get0(this.mRecycler) != null) && (Recycler.-get0(this.mRecycler).contains(localObject2))) {
        animateChange((ViewHolder)localObject2, (ViewHolder)((ArrayMap)localObject1).get(Long.valueOf(l)));
      }
    }
    label1592:
    resumeRequestLayout(false);
    this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
    State.-set2(this.mState, this.mState.mItemCount);
    this.mDataSetHasChangedAfterLayout = false;
    State.-set4(this.mState, false);
    State.-set3(this.mState, false);
    onExitLayoutOrScroll();
    LayoutManager.-set0(this.mLayout, false);
    if (Recycler.-get0(this.mRecycler) != null) {
      Recycler.-get0(this.mRecycler).clear();
    }
    this.mState.mOldChangedHolders = null;
    if (didChildRangeChange(this.mMinMaxLayoutPositions[0], this.mMinMaxLayoutPositions[1])) {
      dispatchOnScrolled(0, 0);
    }
  }
  
  public boolean dispatchNestedFling(float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    return this.mScrollingChildHelper.dispatchNestedFling(paramFloat1, paramFloat2, paramBoolean);
  }
  
  public boolean dispatchNestedPreFling(float paramFloat1, float paramFloat2)
  {
    return this.mScrollingChildHelper.dispatchNestedPreFling(paramFloat1, paramFloat2);
  }
  
  public boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    return this.mScrollingChildHelper.dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2);
  }
  
  public boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    return this.mScrollingChildHelper.dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt);
  }
  
  void dispatchOnScrollStateChanged(int paramInt)
  {
    if (this.mLayout != null) {
      this.mLayout.onScrollStateChanged(paramInt);
    }
    onScrollStateChanged(paramInt);
    if (this.mScrollListener != null) {
      this.mScrollListener.onScrollStateChanged(this, paramInt);
    }
    if (this.mScrollListeners != null)
    {
      int i = this.mScrollListeners.size() - 1;
      while (i >= 0)
      {
        ((OnScrollListener)this.mScrollListeners.get(i)).onScrollStateChanged(this, paramInt);
        i -= 1;
      }
    }
  }
  
  void dispatchOnScrolled(int paramInt1, int paramInt2)
  {
    int i = getScrollX();
    int j = getScrollY();
    onScrollChanged(i, j, i, j);
    onScrolled(paramInt1, paramInt2);
    if (this.mScrollListener != null) {
      this.mScrollListener.onScrolled(this, paramInt1, paramInt2);
    }
    if (this.mScrollListeners != null)
    {
      i = this.mScrollListeners.size() - 1;
      while (i >= 0)
      {
        ((OnScrollListener)this.mScrollListeners.get(i)).onScrolled(this, paramInt1, paramInt2);
        i -= 1;
      }
    }
  }
  
  protected void dispatchRestoreInstanceState(SparseArray<Parcelable> paramSparseArray)
  {
    dispatchThawSelfOnly(paramSparseArray);
  }
  
  protected void dispatchSaveInstanceState(SparseArray<Parcelable> paramSparseArray)
  {
    dispatchFreezeSelfOnly(paramSparseArray);
  }
  
  public void draw(Canvas paramCanvas)
  {
    boolean bool3 = false;
    super.draw(paramCanvas);
    int j = this.mItemDecorations.size();
    int i = 0;
    while (i < j)
    {
      ((ItemDecoration)this.mItemDecorations.get(i)).onDrawOver(paramCanvas, this, this.mState);
      i += 1;
    }
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mLeftGlow != null)
    {
      if (this.mLeftGlow.isFinished()) {
        bool1 = bool2;
      }
    }
    else
    {
      bool2 = bool1;
      if (this.mTopGlow != null)
      {
        if (!this.mTopGlow.isFinished()) {
          break label287;
        }
        bool2 = bool1;
      }
      bool1 = bool2;
      if (this.mRightGlow != null)
      {
        if (!this.mRightGlow.isFinished()) {
          break label351;
        }
        bool1 = bool2;
      }
      bool2 = bool1;
      if (this.mBottomGlow != null)
      {
        if (!this.mBottomGlow.isFinished()) {
          break label435;
        }
        bool2 = bool1;
      }
      bool1 = bool2;
      if (!bool2)
      {
        bool1 = bool2;
        if (this.mItemAnimator != null)
        {
          bool1 = bool2;
          if (this.mItemDecorations.size() > 0)
          {
            bool1 = bool2;
            if (this.mItemAnimator.isRunning()) {
              bool1 = true;
            }
          }
        }
      }
      if (bool1) {
        postInvalidateOnAnimation();
      }
      return;
    }
    j = paramCanvas.save();
    if (this.mClipToPadding)
    {
      i = getPaddingBottom();
      label231:
      paramCanvas.rotate(270.0F);
      paramCanvas.translate(-getHeight() + i, 0.0F);
      if (this.mLeftGlow == null) {
        break label281;
      }
    }
    label281:
    for (bool1 = this.mLeftGlow.draw(paramCanvas);; bool1 = false)
    {
      paramCanvas.restoreToCount(j);
      break;
      i = 0;
      break label231;
    }
    label287:
    i = paramCanvas.save();
    if (this.mClipToPadding) {
      paramCanvas.translate(getPaddingLeft(), getPaddingTop());
    }
    if (this.mTopGlow != null) {}
    for (bool2 = this.mTopGlow.draw(paramCanvas);; bool2 = false)
    {
      bool2 = bool1 | bool2;
      paramCanvas.restoreToCount(i);
      break;
    }
    label351:
    j = paramCanvas.save();
    int k = getWidth();
    if (this.mClipToPadding)
    {
      i = getPaddingTop();
      label374:
      paramCanvas.rotate(90.0F);
      paramCanvas.translate(-i, -k);
      if (this.mRightGlow == null) {
        break label429;
      }
    }
    label429:
    for (bool1 = this.mRightGlow.draw(paramCanvas);; bool1 = false)
    {
      bool1 = bool2 | bool1;
      paramCanvas.restoreToCount(j);
      break;
      i = 0;
      break label374;
    }
    label435:
    i = paramCanvas.save();
    paramCanvas.rotate(180.0F);
    if (this.mClipToPadding) {
      paramCanvas.translate(-getWidth() + getPaddingRight(), -getHeight() + getPaddingBottom());
    }
    for (;;)
    {
      bool2 = bool3;
      if (this.mBottomGlow != null) {
        bool2 = this.mBottomGlow.draw(paramCanvas);
      }
      bool2 = bool1 | bool2;
      paramCanvas.restoreToCount(i);
      break;
      paramCanvas.translate(-getWidth(), -getHeight());
    }
  }
  
  public boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    return super.drawChild(paramCanvas, paramView, paramLong);
  }
  
  void eatRequestLayout()
  {
    if (!this.mEatRequestLayout)
    {
      this.mEatRequestLayout = true;
      if (!this.mLayoutFrozen) {
        this.mLayoutRequestEaten = false;
      }
    }
  }
  
  void ensureBottomGlow()
  {
    if (this.mBottomGlow != null) {
      return;
    }
    this.mBottomGlow = new EdgeEffect(getContext());
    if (this.mClipToPadding)
    {
      this.mBottomGlow.setSize(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), getMeasuredHeight() - getPaddingTop() - getPaddingBottom());
      return;
    }
    this.mBottomGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
  }
  
  void ensureLeftGlow()
  {
    if (this.mLeftGlow != null) {
      return;
    }
    this.mLeftGlow = new EdgeEffect(getContext());
    if (this.mClipToPadding)
    {
      this.mLeftGlow.setSize(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), getMeasuredWidth() - getPaddingLeft() - getPaddingRight());
      return;
    }
    this.mLeftGlow.setSize(getMeasuredHeight(), getMeasuredWidth());
  }
  
  void ensureRightGlow()
  {
    if (this.mRightGlow != null) {
      return;
    }
    this.mRightGlow = new EdgeEffect(getContext());
    if (this.mClipToPadding)
    {
      this.mRightGlow.setSize(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), getMeasuredWidth() - getPaddingLeft() - getPaddingRight());
      return;
    }
    this.mRightGlow.setSize(getMeasuredHeight(), getMeasuredWidth());
  }
  
  void ensureTopGlow()
  {
    if (this.mTopGlow != null) {
      return;
    }
    this.mTopGlow = new EdgeEffect(getContext());
    if (this.mClipToPadding)
    {
      this.mTopGlow.setSize(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), getMeasuredHeight() - getPaddingTop() - getPaddingBottom());
      return;
    }
    this.mTopGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
  }
  
  public View findChildViewUnder(float paramFloat1, float paramFloat2)
  {
    int i = this.mChildHelper.getChildCount() - 1;
    while (i >= 0)
    {
      View localView = this.mChildHelper.getChildAt(i);
      float f1 = localView.getTranslationX();
      float f2 = localView.getTranslationY();
      if ((paramFloat1 >= localView.getLeft() + f1) && (paramFloat1 <= localView.getRight() + f1) && (paramFloat2 >= localView.getTop() + f2) && (paramFloat2 <= localView.getBottom() + f2)) {
        return localView;
      }
      i -= 1;
    }
    return null;
  }
  
  public ViewHolder findViewHolderForAdapterPosition(int paramInt)
  {
    if (this.mDataSetHasChangedAfterLayout) {
      return null;
    }
    int j = this.mChildHelper.getUnfilteredChildCount();
    int i = 0;
    if (i < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
      if ((localViewHolder == null) || (localViewHolder.isRemoved())) {}
      while (getAdapterPositionFor(localViewHolder) != paramInt)
      {
        i += 1;
        break;
      }
      return localViewHolder;
    }
    return null;
  }
  
  public ViewHolder findViewHolderForItemId(long paramLong)
  {
    int j = this.mChildHelper.getUnfilteredChildCount();
    int i = 0;
    while (i < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
      if ((localViewHolder != null) && (localViewHolder.getItemId() == paramLong)) {
        return localViewHolder;
      }
      i += 1;
    }
    return null;
  }
  
  public ViewHolder findViewHolderForLayoutPosition(int paramInt)
  {
    return findViewHolderForPosition(paramInt, false);
  }
  
  @Deprecated
  public ViewHolder findViewHolderForPosition(int paramInt)
  {
    return findViewHolderForPosition(paramInt, false);
  }
  
  ViewHolder findViewHolderForPosition(int paramInt, boolean paramBoolean)
  {
    int j = this.mChildHelper.getUnfilteredChildCount();
    int i = 0;
    if (i < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
      if ((localViewHolder == null) || (localViewHolder.isRemoved())) {}
      label66:
      do
      {
        do
        {
          i += 1;
          break;
          if (!paramBoolean) {
            break label66;
          }
        } while (localViewHolder.mPosition != paramInt);
        return localViewHolder;
      } while (localViewHolder.getLayoutPosition() != paramInt);
      return localViewHolder;
    }
    return null;
  }
  
  public boolean fling(int paramInt1, int paramInt2)
  {
    if (this.mLayout == null)
    {
      Log.e("RecyclerView", "Cannot fling without a LayoutManager set. Call setLayoutManager with a non-null argument.");
      return false;
    }
    if (this.mLayoutFrozen) {
      return false;
    }
    boolean bool2 = this.mLayout.canScrollHorizontally();
    boolean bool1 = this.mLayout.canScrollVertically();
    int i;
    if (bool2)
    {
      i = paramInt1;
      if (Math.abs(paramInt1) >= this.mMinFlingVelocity) {}
    }
    else
    {
      i = 0;
    }
    if (bool1)
    {
      paramInt1 = paramInt2;
      if (Math.abs(paramInt2) >= this.mMinFlingVelocity) {}
    }
    else
    {
      paramInt1 = 0;
    }
    if ((i == 0) && (paramInt1 == 0)) {
      return false;
    }
    if (!dispatchNestedPreFling(i, paramInt1))
    {
      if (!bool2) {}
      for (;;)
      {
        dispatchNestedFling(i, paramInt1, bool1);
        if (!bool1) {
          break;
        }
        paramInt2 = Math.max(-this.mMaxFlingVelocity, Math.min(i, this.mMaxFlingVelocity));
        paramInt1 = Math.max(-this.mMaxFlingVelocity, Math.min(paramInt1, this.mMaxFlingVelocity));
        this.mViewFlinger.fling(paramInt2, paramInt1);
        return true;
        bool1 = true;
      }
    }
    return false;
  }
  
  public View focusSearch(View paramView, int paramInt)
  {
    Object localObject = this.mLayout.onInterceptFocusSearch(paramView, paramInt);
    if (localObject != null) {
      return (View)localObject;
    }
    View localView = FocusFinder.getInstance().findNextFocus(this, paramView, paramInt);
    localObject = localView;
    if (localView == null)
    {
      localObject = localView;
      if (this.mAdapter != null)
      {
        localObject = localView;
        if (this.mLayout != null)
        {
          if (!isComputingLayout()) {
            break label71;
          }
          localObject = localView;
        }
      }
    }
    while (localObject != null)
    {
      return (View)localObject;
      label71:
      localObject = localView;
      if (!this.mLayoutFrozen)
      {
        eatRequestLayout();
        localObject = this.mLayout.onFocusSearchFailed(paramView, paramInt, this.mRecycler, this.mState);
        resumeRequestLayout(false);
      }
    }
    return super.focusSearch(paramView, paramInt);
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams()
  {
    if (this.mLayout == null) {
      throw new IllegalStateException("RecyclerView has no LayoutManager");
    }
    return this.mLayout.generateDefaultLayoutParams();
  }
  
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    if (this.mLayout == null) {
      throw new IllegalStateException("RecyclerView has no LayoutManager");
    }
    return this.mLayout.generateLayoutParams(getContext(), paramAttributeSet);
  }
  
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if (this.mLayout == null) {
      throw new IllegalStateException("RecyclerView has no LayoutManager");
    }
    return this.mLayout.generateLayoutParams(paramLayoutParams);
  }
  
  public Adapter getAdapter()
  {
    return this.mAdapter;
  }
  
  public int getBaseline()
  {
    if (this.mLayout != null) {
      return this.mLayout.getBaseline();
    }
    return super.getBaseline();
  }
  
  long getChangedHolderKey(ViewHolder paramViewHolder)
  {
    if (this.mAdapter.hasStableIds()) {
      return paramViewHolder.getItemId();
    }
    return paramViewHolder.mPosition;
  }
  
  public int getChildAdapterPosition(View paramView)
  {
    paramView = getChildViewHolderInt(paramView);
    if (paramView != null) {
      return paramView.getAdapterPosition();
    }
    return -1;
  }
  
  protected int getChildDrawingOrder(int paramInt1, int paramInt2)
  {
    if (this.mChildDrawingOrderCallback == null) {
      return super.getChildDrawingOrder(paramInt1, paramInt2);
    }
    return this.mChildDrawingOrderCallback.onGetChildDrawingOrder(paramInt1, paramInt2);
  }
  
  public long getChildItemId(View paramView)
  {
    long l = -1L;
    if ((this.mAdapter != null) && (this.mAdapter.hasStableIds()))
    {
      paramView = getChildViewHolderInt(paramView);
      if (paramView != null) {
        l = paramView.getItemId();
      }
      return l;
    }
    return -1L;
  }
  
  public int getChildLayoutPosition(View paramView)
  {
    paramView = getChildViewHolderInt(paramView);
    if (paramView != null) {
      return paramView.getLayoutPosition();
    }
    return -1;
  }
  
  @Deprecated
  public int getChildPosition(View paramView)
  {
    return getChildAdapterPosition(paramView);
  }
  
  public ViewHolder getChildViewHolder(View paramView)
  {
    ViewParent localViewParent = paramView.getParent();
    if ((localViewParent != null) && (localViewParent != this)) {
      throw new IllegalArgumentException("View " + paramView + " is not a direct child of " + this);
    }
    return getChildViewHolderInt(paramView);
  }
  
  public RecyclerViewAccessibilityDelegate getCompatAccessibilityDelegate()
  {
    return this.mAccessibilityDelegate;
  }
  
  public ItemAnimator getItemAnimator()
  {
    return this.mItemAnimator;
  }
  
  Rect getItemDecorInsetsForChild(View paramView)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if (!localLayoutParams.mInsetsDirty) {
      return localLayoutParams.mDecorInsets;
    }
    Rect localRect = localLayoutParams.mDecorInsets;
    localRect.set(0, 0, 0, 0);
    int j = this.mItemDecorations.size();
    int i = 0;
    while (i < j)
    {
      this.mTempRect.set(0, 0, 0, 0);
      ((ItemDecoration)this.mItemDecorations.get(i)).getItemOffsets(this.mTempRect, paramView, this, this.mState);
      localRect.left += this.mTempRect.left;
      localRect.top += this.mTempRect.top;
      localRect.right += this.mTempRect.right;
      localRect.bottom += this.mTempRect.bottom;
      i += 1;
    }
    localLayoutParams.mInsetsDirty = false;
    return localRect;
  }
  
  public LayoutManager getLayoutManager()
  {
    return this.mLayout;
  }
  
  public int getMaxFlingVelocity()
  {
    return this.mMaxFlingVelocity;
  }
  
  public int getMinFlingVelocity()
  {
    return this.mMinFlingVelocity;
  }
  
  public RecycledViewPool getRecycledViewPool()
  {
    return this.mRecycler.getRecycledViewPool();
  }
  
  public int getScrollState()
  {
    return this.mScrollState;
  }
  
  public boolean hasFixedSize()
  {
    return this.mHasFixedSize;
  }
  
  public boolean hasNestedScrollingParent()
  {
    return this.mScrollingChildHelper.hasNestedScrollingParent();
  }
  
  public boolean hasPendingAdapterUpdates()
  {
    if ((this.mFirstLayoutComplete) && (!this.mDataSetHasChangedAfterLayout)) {
      return this.mAdapterHelper.hasPendingUpdates();
    }
    return true;
  }
  
  void initAdapterManager()
  {
    this.mAdapterHelper = new AdapterHelper(new AdapterHelper.Callback()
    {
      void dispatchUpdate(AdapterHelper.UpdateOp paramAnonymousUpdateOp)
      {
        switch (paramAnonymousUpdateOp.cmd)
        {
        default: 
          return;
        case 0: 
          RecyclerView.-get9(RecyclerView.this).onItemsAdded(RecyclerView.this, paramAnonymousUpdateOp.positionStart, paramAnonymousUpdateOp.itemCount);
          return;
        case 1: 
          RecyclerView.-get9(RecyclerView.this).onItemsRemoved(RecyclerView.this, paramAnonymousUpdateOp.positionStart, paramAnonymousUpdateOp.itemCount);
          return;
        case 2: 
          RecyclerView.-get9(RecyclerView.this).onItemsUpdated(RecyclerView.this, paramAnonymousUpdateOp.positionStart, paramAnonymousUpdateOp.itemCount, paramAnonymousUpdateOp.payload);
          return;
        }
        RecyclerView.-get9(RecyclerView.this).onItemsMoved(RecyclerView.this, paramAnonymousUpdateOp.positionStart, paramAnonymousUpdateOp.itemCount, 1);
      }
      
      public RecyclerView.ViewHolder findViewHolder(int paramAnonymousInt)
      {
        RecyclerView.ViewHolder localViewHolder = RecyclerView.this.findViewHolderForPosition(paramAnonymousInt, true);
        if (localViewHolder == null) {
          return null;
        }
        if (RecyclerView.this.mChildHelper.isHidden(localViewHolder.itemView)) {
          return null;
        }
        return localViewHolder;
      }
      
      public void markViewHoldersUpdated(int paramAnonymousInt1, int paramAnonymousInt2, Object paramAnonymousObject)
      {
        RecyclerView.this.viewRangeUpdate(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousObject);
        RecyclerView.this.mItemsChanged = true;
      }
      
      public void offsetPositionsForAdd(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        RecyclerView.this.offsetPositionRecordsForInsert(paramAnonymousInt1, paramAnonymousInt2);
        RecyclerView.this.mItemsAddedOrRemoved = true;
      }
      
      public void offsetPositionsForMove(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        RecyclerView.this.offsetPositionRecordsForMove(paramAnonymousInt1, paramAnonymousInt2);
        RecyclerView.this.mItemsAddedOrRemoved = true;
      }
      
      public void offsetPositionsForRemovingInvisible(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        RecyclerView.this.offsetPositionRecordsForRemove(paramAnonymousInt1, paramAnonymousInt2, true);
        RecyclerView.this.mItemsAddedOrRemoved = true;
        RecyclerView.State localState = RecyclerView.this.mState;
        RecyclerView.State.-set0(localState, RecyclerView.State.-get0(localState) + paramAnonymousInt2);
      }
      
      public void offsetPositionsForRemovingLaidOutOrNewView(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        RecyclerView.this.offsetPositionRecordsForRemove(paramAnonymousInt1, paramAnonymousInt2, false);
        RecyclerView.this.mItemsAddedOrRemoved = true;
      }
      
      public void onDispatchFirstPass(AdapterHelper.UpdateOp paramAnonymousUpdateOp)
      {
        dispatchUpdate(paramAnonymousUpdateOp);
      }
      
      public void onDispatchSecondPass(AdapterHelper.UpdateOp paramAnonymousUpdateOp)
      {
        dispatchUpdate(paramAnonymousUpdateOp);
      }
    });
  }
  
  void invalidateGlows()
  {
    this.mBottomGlow = null;
    this.mTopGlow = null;
    this.mRightGlow = null;
    this.mLeftGlow = null;
  }
  
  public void invalidateItemDecorations()
  {
    if (this.mItemDecorations.size() == 0) {
      return;
    }
    if (this.mLayout != null) {
      this.mLayout.assertNotInLayoutOrScroll("Cannot invalidate item decorations during a scroll or layout");
    }
    markItemDecorInsetsDirty();
    requestLayout();
  }
  
  boolean isAccessibilityEnabled()
  {
    if (this.mAccessibilityManager != null) {
      return this.mAccessibilityManager.isEnabled();
    }
    return false;
  }
  
  public boolean isAnimating()
  {
    if (this.mItemAnimator != null) {
      return this.mItemAnimator.isRunning();
    }
    return false;
  }
  
  public boolean isAttachedToWindow()
  {
    return this.mIsAttached;
  }
  
  public boolean isComputingLayout()
  {
    boolean bool = false;
    if (this.mLayoutOrScrollCounter > 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isLayoutFrozen()
  {
    return this.mLayoutFrozen;
  }
  
  public boolean isNestedScrollingEnabled()
  {
    return this.mScrollingChildHelper.isNestedScrollingEnabled();
  }
  
  void markItemDecorInsetsDirty()
  {
    int j = this.mChildHelper.getUnfilteredChildCount();
    int i = 0;
    while (i < j)
    {
      ((LayoutParams)this.mChildHelper.getUnfilteredChildAt(i).getLayoutParams()).mInsetsDirty = true;
      i += 1;
    }
    this.mRecycler.markItemDecorInsetsDirty();
  }
  
  void markKnownViewsInvalid()
  {
    int j = this.mChildHelper.getUnfilteredChildCount();
    int i = 0;
    if (i < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
      if ((localViewHolder == null) || (localViewHolder.shouldIgnore())) {}
      for (;;)
      {
        i += 1;
        break;
        localViewHolder.addFlags(6);
      }
    }
    markItemDecorInsetsDirty();
    this.mRecycler.markKnownViewsInvalid();
  }
  
  public void offsetChildrenHorizontal(int paramInt)
  {
    int j = this.mChildHelper.getChildCount();
    int i = 0;
    while (i < j)
    {
      this.mChildHelper.getChildAt(i).offsetLeftAndRight(paramInt);
      i += 1;
    }
  }
  
  public void offsetChildrenVertical(int paramInt)
  {
    int j = this.mChildHelper.getChildCount();
    int i = 0;
    while (i < j)
    {
      this.mChildHelper.getChildAt(i).offsetTopAndBottom(paramInt);
      i += 1;
    }
  }
  
  void offsetPositionRecordsForInsert(int paramInt1, int paramInt2)
  {
    int j = this.mChildHelper.getUnfilteredChildCount();
    int i = 0;
    if (i < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
      if ((localViewHolder == null) || (localViewHolder.shouldIgnore())) {}
      for (;;)
      {
        i += 1;
        break;
        if (localViewHolder.mPosition >= paramInt1)
        {
          localViewHolder.offsetPosition(paramInt2, false);
          State.-set5(this.mState, true);
        }
      }
    }
    this.mRecycler.offsetPositionRecordsForInsert(paramInt1, paramInt2);
    requestLayout();
  }
  
  void offsetPositionRecordsForMove(int paramInt1, int paramInt2)
  {
    int n = this.mChildHelper.getUnfilteredChildCount();
    int k;
    int i;
    int j;
    int m;
    label25:
    ViewHolder localViewHolder;
    if (paramInt1 < paramInt2)
    {
      k = paramInt1;
      i = paramInt2;
      j = -1;
      m = 0;
      if (m >= n) {
        break label131;
      }
      localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(m));
      if ((localViewHolder != null) && (localViewHolder.mPosition >= k)) {
        break label81;
      }
    }
    label81:
    while (localViewHolder.mPosition > i)
    {
      m += 1;
      break label25;
      k = paramInt2;
      i = paramInt1;
      j = 1;
      break;
    }
    if (localViewHolder.mPosition == paramInt1) {
      localViewHolder.offsetPosition(paramInt2 - paramInt1, false);
    }
    for (;;)
    {
      State.-set5(this.mState, true);
      break;
      localViewHolder.offsetPosition(j, false);
    }
    label131:
    this.mRecycler.offsetPositionRecordsForMove(paramInt1, paramInt2);
    requestLayout();
  }
  
  void offsetPositionRecordsForRemove(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int j = this.mChildHelper.getUnfilteredChildCount();
    int i = 0;
    if (i < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
      if ((localViewHolder == null) || (localViewHolder.shouldIgnore())) {}
      for (;;)
      {
        i += 1;
        break;
        if (localViewHolder.mPosition >= paramInt1 + paramInt2)
        {
          localViewHolder.offsetPosition(-paramInt2, paramBoolean);
          State.-set5(this.mState, true);
        }
        else if (localViewHolder.mPosition >= paramInt1)
        {
          localViewHolder.flagRemovedAndOffsetPosition(paramInt1 - 1, -paramInt2, paramBoolean);
          State.-set5(this.mState, true);
        }
      }
    }
    this.mRecycler.offsetPositionRecordsForRemove(paramInt1, paramInt2, paramBoolean);
    requestLayout();
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.mLayoutOrScrollCounter = 0;
    this.mIsAttached = true;
    this.mFirstLayoutComplete = false;
    if (this.mLayout != null) {
      this.mLayout.dispatchAttachedToWindow(this);
    }
    this.mPostedAnimatorRunner = false;
  }
  
  public void onChildAttachedToWindow(View paramView) {}
  
  public void onChildDetachedFromWindow(View paramView) {}
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (this.mItemAnimator != null) {
      this.mItemAnimator.endAnimations();
    }
    this.mFirstLayoutComplete = false;
    stopScroll();
    this.mIsAttached = false;
    if (this.mLayout != null) {
      this.mLayout.dispatchDetachedFromWindow(this, this.mRecycler);
    }
    removeCallbacks(this.mItemAnimatorRunner);
  }
  
  @SuppressLint({"WrongCall"})
  public void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    int j = this.mItemDecorations.size();
    int i = 0;
    while (i < j)
    {
      ((ItemDecoration)this.mItemDecorations.get(i)).onDraw(paramCanvas, this, this.mState);
      i += 1;
    }
  }
  
  public boolean onGenericMotionEvent(MotionEvent paramMotionEvent)
  {
    if (this.mLayout == null) {
      return false;
    }
    if (this.mLayoutFrozen) {
      return false;
    }
    float f1;
    if (((paramMotionEvent.getSource() & 0x2) != 0) && (paramMotionEvent.getAction() == 8))
    {
      if (!this.mLayout.canScrollVertically()) {
        break label107;
      }
      f1 = -paramMotionEvent.getAxisValue(9);
      if (!this.mLayout.canScrollHorizontally()) {
        break label112;
      }
    }
    label107:
    label112:
    for (float f2 = paramMotionEvent.getAxisValue(10);; f2 = 0.0F)
    {
      if ((f1 != 0.0F) || (f2 != 0.0F))
      {
        float f3 = getScrollFactor();
        scrollByInternal((int)(f2 * f3), (int)(f1 * f3), paramMotionEvent);
      }
      return false;
      f1 = 0.0F;
      break;
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.mLayoutFrozen) {
      return false;
    }
    if (dispatchOnItemTouchIntercept(paramMotionEvent))
    {
      cancelTouch();
      return true;
    }
    if (this.mLayout == null) {
      return false;
    }
    boolean bool1 = this.mLayout.canScrollHorizontally();
    boolean bool2 = this.mLayout.canScrollVertically();
    if (this.mVelocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
    }
    this.mVelocityTracker.addMovement(paramMotionEvent);
    int j = paramMotionEvent.getActionMasked();
    int i = paramMotionEvent.getActionIndex();
    switch (j)
    {
    }
    while (this.mScrollState == 1)
    {
      return true;
      if (this.mIgnoreMotionEventTillDown) {
        this.mIgnoreMotionEventTillDown = false;
      }
      this.mScrollPointerId = paramMotionEvent.getPointerId(0);
      i = (int)(paramMotionEvent.getX() + 0.5F);
      this.mLastTouchX = i;
      this.mInitialTouchX = i;
      i = (int)(paramMotionEvent.getY() + 0.5F);
      this.mLastTouchY = i;
      this.mInitialTouchY = i;
      if (this.mScrollState == 2)
      {
        getParent().requestDisallowInterceptTouchEvent(true);
        setScrollState(1);
      }
      i = 0;
      if (bool1) {
        i = 1;
      }
      j = i;
      if (bool2) {
        j = i | 0x2;
      }
      startNestedScroll(j);
      continue;
      this.mScrollPointerId = paramMotionEvent.getPointerId(i);
      j = (int)(paramMotionEvent.getX(i) + 0.5F);
      this.mLastTouchX = j;
      this.mInitialTouchX = j;
      i = (int)(paramMotionEvent.getY(i) + 0.5F);
      this.mLastTouchY = i;
      this.mInitialTouchY = i;
      continue;
      j = paramMotionEvent.findPointerIndex(this.mScrollPointerId);
      if (j < 0)
      {
        Log.e("RecyclerView", "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
        return false;
      }
      i = (int)(paramMotionEvent.getX(j) + 0.5F);
      j = (int)(paramMotionEvent.getY(j) + 0.5F);
      if (this.mScrollState != 1)
      {
        int m = i - this.mInitialTouchX;
        int k = j - this.mInitialTouchY;
        j = 0;
        i = j;
        if (bool1)
        {
          i = j;
          if (Math.abs(m) > this.mTouchSlop)
          {
            j = this.mInitialTouchX;
            int n = this.mTouchSlop;
            if (m >= 0) {
              break label529;
            }
            i = -1;
            label438:
            this.mLastTouchX = (i * n + j);
            i = 1;
          }
        }
        j = i;
        if (bool2)
        {
          j = i;
          if (Math.abs(k) > this.mTouchSlop)
          {
            j = this.mInitialTouchY;
            m = this.mTouchSlop;
            if (k >= 0) {
              break label534;
            }
          }
        }
        label529:
        label534:
        for (i = -1;; i = 1)
        {
          this.mLastTouchY = (i * m + j);
          j = 1;
          if (j == 0) {
            break;
          }
          paramMotionEvent = getParent();
          if (paramMotionEvent != null) {
            paramMotionEvent.requestDisallowInterceptTouchEvent(true);
          }
          setScrollState(1);
          break;
          i = 1;
          break label438;
        }
        onPointerUp(paramMotionEvent);
        continue;
        this.mVelocityTracker.clear();
        stopNestedScroll();
        continue;
        cancelTouch();
      }
    }
    return false;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    eatRequestLayout();
    Trace.beginSection("RV OnLayout");
    dispatchLayout();
    Trace.endSection();
    resumeRequestLayout(false);
    this.mFirstLayoutComplete = true;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (this.mAdapterUpdateDuringMeasure)
    {
      eatRequestLayout();
      processAdapterUpdatesAndSetAnimationFlags();
      if (State.-get2(this.mState))
      {
        State.-set1(this.mState, true);
        this.mAdapterUpdateDuringMeasure = false;
        resumeRequestLayout(false);
      }
    }
    else
    {
      if (this.mAdapter == null) {
        break label107;
      }
      this.mState.mItemCount = this.mAdapter.getItemCount();
      label65:
      if (this.mLayout != null) {
        break label118;
      }
      defaultOnMeasure(paramInt1, paramInt2);
    }
    for (;;)
    {
      State.-set1(this.mState, false);
      return;
      this.mAdapterHelper.consumeUpdatesInOnePass();
      State.-set1(this.mState, false);
      break;
      label107:
      this.mState.mItemCount = 0;
      break label65;
      label118:
      this.mLayout.doMeasure(this.mRecycler, this.mState, paramInt1, paramInt2);
    }
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    this.mPendingSavedState = ((SavedState)paramParcelable);
    super.onRestoreInstanceState(this.mPendingSavedState.getSuperState());
    if ((this.mLayout != null) && (this.mPendingSavedState.mLayoutState != null)) {
      this.mLayout.onRestoreInstanceState(this.mPendingSavedState.mLayoutState);
    }
  }
  
  protected Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    if (this.mPendingSavedState != null)
    {
      SavedState.-wrap0(localSavedState, this.mPendingSavedState);
      return localSavedState;
    }
    if (this.mLayout != null)
    {
      localSavedState.mLayoutState = this.mLayout.onSaveInstanceState();
      return localSavedState;
    }
    localSavedState.mLayoutState = null;
    return localSavedState;
  }
  
  public void onScrollStateChanged(int paramInt) {}
  
  public void onScrolled(int paramInt1, int paramInt2) {}
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if ((paramInt1 != paramInt3) || (paramInt2 != paramInt4)) {
      invalidateGlows();
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((this.mLayoutFrozen) || (this.mIgnoreMotionEventTillDown)) {
      return false;
    }
    if (dispatchOnItemTouch(paramMotionEvent))
    {
      cancelTouch();
      return true;
    }
    if (this.mLayout == null) {
      return false;
    }
    boolean bool1 = this.mLayout.canScrollHorizontally();
    boolean bool2 = this.mLayout.canScrollVertically();
    if (this.mVelocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
    }
    int i2 = 0;
    MotionEvent localMotionEvent = MotionEvent.obtain(paramMotionEvent);
    int k = paramMotionEvent.getActionMasked();
    int j = paramMotionEvent.getActionIndex();
    if (k == 0)
    {
      int[] arrayOfInt = this.mNestedOffsets;
      this.mNestedOffsets[1] = 0;
      arrayOfInt[0] = 0;
    }
    localMotionEvent.offsetLocation(this.mNestedOffsets[0], this.mNestedOffsets[1]);
    int i = i2;
    switch (k)
    {
    default: 
      i = i2;
    }
    for (;;)
    {
      if (i == 0) {
        this.mVelocityTracker.addMovement(localMotionEvent);
      }
      localMotionEvent.recycle();
      return true;
      this.mScrollPointerId = paramMotionEvent.getPointerId(0);
      i = (int)(paramMotionEvent.getX() + 0.5F);
      this.mLastTouchX = i;
      this.mInitialTouchX = i;
      i = (int)(paramMotionEvent.getY() + 0.5F);
      this.mLastTouchY = i;
      this.mInitialTouchY = i;
      i = 0;
      if (bool1) {
        i = 1;
      }
      j = i;
      if (bool2) {
        j = i | 0x2;
      }
      startNestedScroll(j);
      i = i2;
      continue;
      this.mScrollPointerId = paramMotionEvent.getPointerId(j);
      i = (int)(paramMotionEvent.getX(j) + 0.5F);
      this.mLastTouchX = i;
      this.mInitialTouchX = i;
      i = (int)(paramMotionEvent.getY(j) + 0.5F);
      this.mLastTouchY = i;
      this.mInitialTouchY = i;
      i = i2;
      continue;
      i = paramMotionEvent.findPointerIndex(this.mScrollPointerId);
      if (i < 0)
      {
        Log.e("RecyclerView", "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
        return false;
      }
      int i3 = (int)(paramMotionEvent.getX(i) + 0.5F);
      int i4 = (int)(paramMotionEvent.getY(i) + 0.5F);
      int m = this.mLastTouchX - i3;
      k = this.mLastTouchY - i4;
      j = m;
      i = k;
      if (dispatchNestedPreScroll(m, k, this.mScrollConsumed, this.mScrollOffset))
      {
        j = m - this.mScrollConsumed[0];
        i = k - this.mScrollConsumed[1];
        localMotionEvent.offsetLocation(this.mScrollOffset[0], this.mScrollOffset[1]);
        paramMotionEvent = this.mNestedOffsets;
        paramMotionEvent[0] += this.mScrollOffset[0];
        paramMotionEvent = this.mNestedOffsets;
        paramMotionEvent[1] += this.mScrollOffset[1];
      }
      int n = j;
      m = i;
      if (this.mScrollState != 1)
      {
        n = 0;
        k = j;
        m = n;
        if (bool1)
        {
          k = j;
          m = n;
          if (Math.abs(j) > this.mTouchSlop)
          {
            if (j <= 0) {
              break label814;
            }
            k = j - this.mTouchSlop;
            label637:
            m = 1;
          }
        }
        j = i;
        int i1 = m;
        if (bool2)
        {
          j = i;
          i1 = m;
          if (Math.abs(i) > this.mTouchSlop)
          {
            if (i <= 0) {
              break label826;
            }
            j = i - this.mTouchSlop;
            label687:
            i1 = 1;
          }
        }
        n = k;
        m = j;
        if (i1 != 0)
        {
          paramMotionEvent = getParent();
          if (paramMotionEvent != null) {
            paramMotionEvent.requestDisallowInterceptTouchEvent(true);
          }
          setScrollState(1);
          m = j;
          n = k;
        }
      }
      i = i2;
      if (this.mScrollState == 1)
      {
        this.mLastTouchX = (i3 - this.mScrollOffset[0]);
        this.mLastTouchY = (i4 - this.mScrollOffset[1]);
        if (bool1) {
          label775:
          if (!bool2) {
            break label844;
          }
        }
        for (;;)
        {
          i = i2;
          if (!scrollByInternal(n, m, localMotionEvent)) {
            break;
          }
          getParent().requestDisallowInterceptTouchEvent(true);
          i = i2;
          break;
          label814:
          k = j + this.mTouchSlop;
          break label637;
          label826:
          j = i + this.mTouchSlop;
          break label687;
          n = 0;
          break label775;
          label844:
          m = 0;
        }
        onPointerUp(paramMotionEvent);
        i = i2;
        continue;
        this.mVelocityTracker.addMovement(localMotionEvent);
        i = 1;
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaxFlingVelocity);
        float f1;
        label907:
        float f2;
        if (bool1)
        {
          f1 = -this.mVelocityTracker.getXVelocity(this.mScrollPointerId);
          if (!bool2) {
            break label969;
          }
          f2 = -this.mVelocityTracker.getYVelocity(this.mScrollPointerId);
          label925:
          if ((f1 == 0.0F) && (f2 == 0.0F)) {
            break label974;
          }
        }
        label969:
        label974:
        for (bool1 = fling((int)f1, (int)f2);; bool1 = false)
        {
          if (!bool1) {
            setScrollState(0);
          }
          resetTouch();
          break;
          f1 = 0.0F;
          break label907;
          f2 = 0.0F;
          break label925;
        }
        cancelTouch();
        i = i2;
      }
    }
  }
  
  void rebindUpdatedViewHolders()
  {
    int j = this.mChildHelper.getChildCount();
    int i = 0;
    if (i < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
      if ((localViewHolder == null) || (localViewHolder.shouldIgnore())) {}
      for (;;)
      {
        i += 1;
        break;
        if ((localViewHolder.isRemoved()) || (localViewHolder.isInvalid()))
        {
          requestLayout();
        }
        else if (localViewHolder.needsUpdate())
        {
          int k = this.mAdapter.getItemViewType(localViewHolder.mPosition);
          if (localViewHolder.getItemViewType() != k) {
            break label140;
          }
          if ((localViewHolder.isChanged()) && (supportsChangeAnimations())) {
            requestLayout();
          } else {
            this.mAdapter.bindViewHolder(localViewHolder, localViewHolder.mPosition);
          }
        }
      }
      label140:
      requestLayout();
    }
  }
  
  protected void removeDetachedView(View paramView, boolean paramBoolean)
  {
    ViewHolder localViewHolder = getChildViewHolderInt(paramView);
    if (localViewHolder != null)
    {
      if (!localViewHolder.isTmpDetached()) {
        break label32;
      }
      localViewHolder.clearTmpDetachFlag();
    }
    label32:
    while (localViewHolder.shouldIgnore())
    {
      dispatchChildDetached(paramView);
      super.removeDetachedView(paramView, paramBoolean);
      return;
    }
    throw new IllegalArgumentException("Called removeDetachedView with a view which is not flagged as tmp detached." + localViewHolder);
  }
  
  public void removeItemDecoration(ItemDecoration paramItemDecoration)
  {
    if (this.mLayout != null) {
      this.mLayout.assertNotInLayoutOrScroll("Cannot remove item decoration during a scroll  or layout");
    }
    this.mItemDecorations.remove(paramItemDecoration);
    if (this.mItemDecorations.isEmpty()) {
      if (getOverScrollMode() != 2) {
        break label60;
      }
    }
    label60:
    for (boolean bool = true;; bool = false)
    {
      setWillNotDraw(bool);
      markItemDecorInsetsDirty();
      requestLayout();
      return;
    }
  }
  
  public void removeOnChildAttachStateChangeListener(OnChildAttachStateChangeListener paramOnChildAttachStateChangeListener)
  {
    if (this.mOnChildAttachStateListeners == null) {
      return;
    }
    this.mOnChildAttachStateListeners.remove(paramOnChildAttachStateChangeListener);
  }
  
  public void removeOnItemTouchListener(OnItemTouchListener paramOnItemTouchListener)
  {
    this.mOnItemTouchListeners.remove(paramOnItemTouchListener);
    if (this.mActiveOnItemTouchListener == paramOnItemTouchListener) {
      this.mActiveOnItemTouchListener = null;
    }
  }
  
  public void removeOnScrollListener(OnScrollListener paramOnScrollListener)
  {
    if (this.mScrollListeners != null) {
      this.mScrollListeners.remove(paramOnScrollListener);
    }
  }
  
  public void requestChildFocus(View paramView1, View paramView2)
  {
    boolean bool = false;
    Object localObject;
    if ((!this.mLayout.onRequestChildFocus(this, this.mState, paramView1, paramView2)) && (paramView2 != null))
    {
      this.mTempRect.set(0, 0, paramView2.getWidth(), paramView2.getHeight());
      localObject = paramView2.getLayoutParams();
      if ((localObject instanceof LayoutParams))
      {
        localObject = (LayoutParams)localObject;
        if (!((LayoutParams)localObject).mInsetsDirty)
        {
          localObject = ((LayoutParams)localObject).mDecorInsets;
          Rect localRect = this.mTempRect;
          localRect.left -= ((Rect)localObject).left;
          localRect = this.mTempRect;
          localRect.right += ((Rect)localObject).right;
          localRect = this.mTempRect;
          localRect.top -= ((Rect)localObject).top;
          localRect = this.mTempRect;
          localRect.bottom += ((Rect)localObject).bottom;
        }
      }
      offsetDescendantRectToMyCoords(paramView2, this.mTempRect);
      offsetRectIntoDescendantCoords(paramView1, this.mTempRect);
      localObject = this.mTempRect;
      if (!this.mFirstLayoutComplete) {
        break label211;
      }
    }
    for (;;)
    {
      requestChildRectangleOnScreen(paramView1, (Rect)localObject, bool);
      super.requestChildFocus(paramView1, paramView2);
      return;
      label211:
      bool = true;
    }
  }
  
  public boolean requestChildRectangleOnScreen(View paramView, Rect paramRect, boolean paramBoolean)
  {
    return this.mLayout.requestChildRectangleOnScreen(this, paramView, paramRect, paramBoolean);
  }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
  {
    int j = this.mOnItemTouchListeners.size();
    int i = 0;
    while (i < j)
    {
      ((OnItemTouchListener)this.mOnItemTouchListeners.get(i)).onRequestDisallowInterceptTouchEvent(paramBoolean);
      i += 1;
    }
    super.requestDisallowInterceptTouchEvent(paramBoolean);
  }
  
  public void requestLayout()
  {
    if ((this.mEatRequestLayout) || (this.mLayoutFrozen))
    {
      this.mLayoutRequestEaten = true;
      return;
    }
    super.requestLayout();
  }
  
  void resumeRequestLayout(boolean paramBoolean)
  {
    if (this.mEatRequestLayout) {
      if ((paramBoolean) && (this.mLayoutRequestEaten) && (!this.mLayoutFrozen)) {
        break label43;
      }
    }
    for (;;)
    {
      this.mEatRequestLayout = false;
      if (!this.mLayoutFrozen) {
        this.mLayoutRequestEaten = false;
      }
      return;
      label43:
      if ((this.mLayout != null) && (this.mAdapter != null)) {
        dispatchLayout();
      }
    }
  }
  
  void saveOldPositions()
  {
    int j = this.mChildHelper.getUnfilteredChildCount();
    int i = 0;
    while (i < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
      if (!localViewHolder.shouldIgnore()) {
        localViewHolder.saveOldPosition();
      }
      i += 1;
    }
  }
  
  public void scrollBy(int paramInt1, int paramInt2)
  {
    if (this.mLayout == null)
    {
      Log.e("RecyclerView", "Cannot scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
      return;
    }
    if (this.mLayoutFrozen) {
      return;
    }
    boolean bool1 = this.mLayout.canScrollHorizontally();
    boolean bool2 = this.mLayout.canScrollVertically();
    if ((bool1) || (bool2))
    {
      if (!bool1) {
        break label69;
      }
      if (!bool2) {
        break label74;
      }
    }
    for (;;)
    {
      scrollByInternal(paramInt1, paramInt2, null);
      return;
      label69:
      paramInt1 = 0;
      break;
      label74:
      paramInt2 = 0;
    }
  }
  
  boolean scrollByInternal(int paramInt1, int paramInt2, MotionEvent paramMotionEvent)
  {
    int j = 0;
    int i2 = 0;
    int m = 0;
    int n = 0;
    int i = 0;
    int i3 = 0;
    int k = 0;
    int i1 = 0;
    consumePendingUpdateOperations();
    if (this.mAdapter != null)
    {
      eatRequestLayout();
      onEnterLayoutOrScroll();
      Trace.beginSection("RV Scroll");
      i = i3;
      j = i2;
      if (paramInt1 != 0)
      {
        i = this.mLayout.scrollHorizontallyBy(paramInt1, this.mRecycler, this.mState);
        j = paramInt1 - i;
      }
      k = i1;
      m = n;
      if (paramInt2 != 0)
      {
        k = this.mLayout.scrollVerticallyBy(paramInt2, this.mRecycler, this.mState);
        m = paramInt2 - k;
      }
      Trace.endSection();
      if (supportsChangeAnimations())
      {
        i1 = this.mChildHelper.getChildCount();
        n = 0;
        if (n < i1)
        {
          View localView = this.mChildHelper.getChildAt(n);
          Object localObject = getChildViewHolder(localView);
          if ((localObject != null) && (((ViewHolder)localObject).mShadowingHolder != null))
          {
            localObject = ((ViewHolder)localObject).mShadowingHolder;
            if (localObject == null) {
              break label273;
            }
          }
          label273:
          for (localObject = ((ViewHolder)localObject).itemView;; localObject = null)
          {
            if (localObject != null)
            {
              i2 = localView.getLeft();
              i3 = localView.getTop();
              if ((i2 != ((View)localObject).getLeft()) || (i3 != ((View)localObject).getTop())) {
                ((View)localObject).layout(i2, i3, ((View)localObject).getWidth() + i2, ((View)localObject).getHeight() + i3);
              }
            }
            n += 1;
            break;
          }
        }
      }
      onExitLayoutOrScroll();
      resumeRequestLayout(false);
    }
    if (!this.mItemDecorations.isEmpty()) {
      invalidate();
    }
    if (dispatchNestedScroll(i, k, j, m, this.mScrollOffset))
    {
      this.mLastTouchX -= this.mScrollOffset[0];
      this.mLastTouchY -= this.mScrollOffset[1];
      if (paramMotionEvent != null) {
        paramMotionEvent.offsetLocation(this.mScrollOffset[0], this.mScrollOffset[1]);
      }
      paramMotionEvent = this.mNestedOffsets;
      paramMotionEvent[0] += this.mScrollOffset[0];
      paramMotionEvent = this.mNestedOffsets;
      paramMotionEvent[1] += this.mScrollOffset[1];
    }
    for (;;)
    {
      if ((i != 0) || (k != 0)) {
        dispatchOnScrolled(i, k);
      }
      if (!awakenScrollBars()) {
        invalidate();
      }
      if ((i == 0) && (k == 0)) {
        break;
      }
      return true;
      if (getOverScrollMode() != 2)
      {
        if (paramMotionEvent != null) {
          pullGlows(paramMotionEvent.getX(), j, paramMotionEvent.getY(), m);
        }
        considerReleasingGlowsOnScroll(paramInt1, paramInt2);
      }
    }
    return false;
  }
  
  public void scrollTo(int paramInt1, int paramInt2)
  {
    throw new UnsupportedOperationException("RecyclerView does not support scrolling to an absolute position.");
  }
  
  public void scrollToPosition(int paramInt)
  {
    if (this.mLayoutFrozen) {
      return;
    }
    stopScroll();
    if (this.mLayout == null)
    {
      Log.e("RecyclerView", "Cannot scroll to position a LayoutManager set. Call setLayoutManager with a non-null argument.");
      return;
    }
    this.mLayout.scrollToPosition(paramInt);
    awakenScrollBars();
  }
  
  public void sendAccessibilityEventUnchecked(AccessibilityEvent paramAccessibilityEvent)
  {
    if (shouldDeferAccessibilityEvent(paramAccessibilityEvent)) {
      return;
    }
    super.sendAccessibilityEventUnchecked(paramAccessibilityEvent);
  }
  
  public void setAccessibilityDelegateCompat(RecyclerViewAccessibilityDelegate paramRecyclerViewAccessibilityDelegate)
  {
    this.mAccessibilityDelegate = paramRecyclerViewAccessibilityDelegate;
    setAccessibilityDelegate(this.mAccessibilityDelegate);
  }
  
  public void setAdapter(Adapter paramAdapter)
  {
    setLayoutFrozen(false);
    setAdapterInternal(paramAdapter, false, true);
    requestLayout();
  }
  
  public void setChildDrawingOrderCallback(ChildDrawingOrderCallback paramChildDrawingOrderCallback)
  {
    if (paramChildDrawingOrderCallback == this.mChildDrawingOrderCallback) {
      return;
    }
    this.mChildDrawingOrderCallback = paramChildDrawingOrderCallback;
    if (this.mChildDrawingOrderCallback != null) {}
    for (boolean bool = true;; bool = false)
    {
      setChildrenDrawingOrderEnabled(bool);
      return;
    }
  }
  
  public void setClipToPadding(boolean paramBoolean)
  {
    if (paramBoolean != this.mClipToPadding) {
      invalidateGlows();
    }
    this.mClipToPadding = paramBoolean;
    super.setClipToPadding(paramBoolean);
    if (this.mFirstLayoutComplete) {
      requestLayout();
    }
  }
  
  public void setHasFixedSize(boolean paramBoolean)
  {
    this.mHasFixedSize = paramBoolean;
  }
  
  public void setItemAnimator(ItemAnimator paramItemAnimator)
  {
    if (this.mItemAnimator != null)
    {
      this.mItemAnimator.endAnimations();
      this.mItemAnimator.setListener(null);
    }
    this.mItemAnimator = paramItemAnimator;
    if (this.mItemAnimator != null) {
      this.mItemAnimator.setListener(this.mItemAnimatorListener);
    }
  }
  
  public void setItemViewCacheSize(int paramInt)
  {
    this.mRecycler.setViewCacheSize(paramInt);
  }
  
  public void setLayoutFrozen(boolean paramBoolean)
  {
    if (paramBoolean != this.mLayoutFrozen)
    {
      assertNotInLayoutOrScroll("Do not setLayoutFrozen in layout or scroll");
      if (!paramBoolean)
      {
        this.mLayoutFrozen = paramBoolean;
        if ((this.mLayoutRequestEaten) && (this.mLayout != null) && (this.mAdapter != null)) {
          requestLayout();
        }
        this.mLayoutRequestEaten = false;
      }
    }
    else
    {
      return;
    }
    long l = SystemClock.uptimeMillis();
    onTouchEvent(MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0));
    this.mLayoutFrozen = paramBoolean;
    this.mIgnoreMotionEventTillDown = true;
    stopScroll();
  }
  
  public void setLayoutManager(LayoutManager paramLayoutManager)
  {
    if (paramLayoutManager == this.mLayout) {
      return;
    }
    if (this.mLayout != null)
    {
      if (this.mIsAttached) {
        this.mLayout.dispatchDetachedFromWindow(this, this.mRecycler);
      }
      this.mLayout.setRecyclerView(null);
    }
    this.mRecycler.clear();
    this.mChildHelper.removeAllViewsUnfiltered();
    this.mLayout = paramLayoutManager;
    if (paramLayoutManager != null)
    {
      if (paramLayoutManager.mRecyclerView != null) {
        throw new IllegalArgumentException("LayoutManager " + paramLayoutManager + " is already attached to a RecyclerView: " + paramLayoutManager.mRecyclerView);
      }
      this.mLayout.setRecyclerView(this);
      if (this.mIsAttached) {
        this.mLayout.dispatchAttachedToWindow(this);
      }
    }
    requestLayout();
  }
  
  public void setNestedScrollingEnabled(boolean paramBoolean)
  {
    this.mScrollingChildHelper.setNestedScrollingEnabled(paramBoolean);
  }
  
  @Deprecated
  public void setOnScrollListener(OnScrollListener paramOnScrollListener)
  {
    this.mScrollListener = paramOnScrollListener;
  }
  
  public void setRecycledViewPool(RecycledViewPool paramRecycledViewPool)
  {
    this.mRecycler.setRecycledViewPool(paramRecycledViewPool);
  }
  
  public void setRecyclerListener(RecyclerListener paramRecyclerListener)
  {
    this.mRecyclerListener = paramRecyclerListener;
  }
  
  public void setScrollingTouchSlop(int paramInt)
  {
    ViewConfiguration localViewConfiguration = ViewConfiguration.get(getContext());
    switch (paramInt)
    {
    default: 
      Log.w("RecyclerView", "setScrollingTouchSlop(): bad argument constant " + paramInt + "; using default value");
    case 0: 
      this.mTouchSlop = localViewConfiguration.getScaledTouchSlop();
      return;
    }
    this.mTouchSlop = localViewConfiguration.getScaledPagingTouchSlop();
  }
  
  public void setViewCacheExtension(ViewCacheExtension paramViewCacheExtension)
  {
    this.mRecycler.setViewCacheExtension(paramViewCacheExtension);
  }
  
  boolean shouldDeferAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    if (isComputingLayout())
    {
      int i = 0;
      if (paramAccessibilityEvent != null) {
        i = paramAccessibilityEvent.getContentChangeTypes();
      }
      int j = i;
      if (i == 0) {
        j = 0;
      }
      this.mEatenAccessibilityChangeFlags |= j;
      return true;
    }
    return false;
  }
  
  public void smoothScrollBy(int paramInt1, int paramInt2)
  {
    if (this.mLayout == null)
    {
      Log.e("RecyclerView", "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
      return;
    }
    if (this.mLayoutFrozen) {
      return;
    }
    if (!this.mLayout.canScrollHorizontally()) {
      paramInt1 = 0;
    }
    if (!this.mLayout.canScrollVertically()) {
      paramInt2 = 0;
    }
    if ((paramInt1 != 0) || (paramInt2 != 0)) {
      this.mViewFlinger.smoothScrollBy(paramInt1, paramInt2);
    }
  }
  
  public void smoothScrollToPosition(int paramInt)
  {
    if (this.mLayoutFrozen) {
      return;
    }
    if (this.mLayout == null)
    {
      Log.e("RecyclerView", "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
      return;
    }
    this.mLayout.smoothScrollToPosition(this, this.mState, paramInt);
  }
  
  public boolean startNestedScroll(int paramInt)
  {
    return this.mScrollingChildHelper.startNestedScroll(paramInt);
  }
  
  public void stopNestedScroll()
  {
    this.mScrollingChildHelper.stopNestedScroll();
  }
  
  public void stopScroll()
  {
    setScrollState(0);
    stopScrollersInternal();
  }
  
  public void swapAdapter(Adapter paramAdapter, boolean paramBoolean)
  {
    setLayoutFrozen(false);
    setAdapterInternal(paramAdapter, true, paramBoolean);
    setDataSetChangedAfterLayout();
    requestLayout();
  }
  
  void viewRangeUpdate(int paramInt1, int paramInt2, Object paramObject)
  {
    int j = this.mChildHelper.getUnfilteredChildCount();
    int i = 0;
    if (i < j)
    {
      View localView = this.mChildHelper.getUnfilteredChildAt(i);
      ViewHolder localViewHolder = getChildViewHolderInt(localView);
      if ((localViewHolder == null) || (localViewHolder.shouldIgnore())) {}
      for (;;)
      {
        i += 1;
        break;
        if ((localViewHolder.mPosition >= paramInt1) && (localViewHolder.mPosition < paramInt1 + paramInt2))
        {
          localViewHolder.addFlags(2);
          localViewHolder.addChangePayload(paramObject);
          if (supportsChangeAnimations()) {
            localViewHolder.addFlags(64);
          }
          ((LayoutParams)localView.getLayoutParams()).mInsetsDirty = true;
        }
      }
    }
    this.mRecycler.viewRangeUpdate(paramInt1, paramInt2);
  }
  
  public static abstract class Adapter<VH extends RecyclerView.ViewHolder>
  {
    private boolean mHasStableIds = false;
    private final RecyclerView.AdapterDataObservable mObservable = new RecyclerView.AdapterDataObservable();
    
    public final void bindViewHolder(VH paramVH, int paramInt)
    {
      paramVH.mPosition = paramInt;
      if (hasStableIds()) {
        paramVH.mItemId = getItemId(paramInt);
      }
      paramVH.setFlags(1, 519);
      Trace.beginSection("RV OnBindView");
      onBindViewHolder(paramVH, paramInt, paramVH.getUnmodifiedPayloads());
      paramVH.clearPayload();
      Trace.endSection();
    }
    
    public final VH createViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      Trace.beginSection("RV CreateView");
      paramViewGroup = onCreateViewHolder(paramViewGroup, paramInt);
      paramViewGroup.mItemViewType = paramInt;
      Trace.endSection();
      return paramViewGroup;
    }
    
    public abstract int getItemCount();
    
    public long getItemId(int paramInt)
    {
      return -1L;
    }
    
    public int getItemViewType(int paramInt)
    {
      return 0;
    }
    
    public final boolean hasObservers()
    {
      return this.mObservable.hasObservers();
    }
    
    public final boolean hasStableIds()
    {
      return this.mHasStableIds;
    }
    
    public final void notifyDataSetChanged()
    {
      this.mObservable.notifyChanged();
    }
    
    public final void notifyItemChanged(int paramInt)
    {
      this.mObservable.notifyItemRangeChanged(paramInt, 1);
    }
    
    public final void notifyItemChanged(int paramInt, Object paramObject)
    {
      this.mObservable.notifyItemRangeChanged(paramInt, 1, paramObject);
    }
    
    public final void notifyItemInserted(int paramInt)
    {
      this.mObservable.notifyItemRangeInserted(paramInt, 1);
    }
    
    public final void notifyItemMoved(int paramInt1, int paramInt2)
    {
      this.mObservable.notifyItemMoved(paramInt1, paramInt2);
    }
    
    public final void notifyItemRangeChanged(int paramInt1, int paramInt2)
    {
      this.mObservable.notifyItemRangeChanged(paramInt1, paramInt2);
    }
    
    public final void notifyItemRangeChanged(int paramInt1, int paramInt2, Object paramObject)
    {
      this.mObservable.notifyItemRangeChanged(paramInt1, paramInt2, paramObject);
    }
    
    public final void notifyItemRangeInserted(int paramInt1, int paramInt2)
    {
      this.mObservable.notifyItemRangeInserted(paramInt1, paramInt2);
    }
    
    public final void notifyItemRangeRemoved(int paramInt1, int paramInt2)
    {
      this.mObservable.notifyItemRangeRemoved(paramInt1, paramInt2);
    }
    
    public final void notifyItemRemoved(int paramInt)
    {
      this.mObservable.notifyItemRangeRemoved(paramInt, 1);
    }
    
    public void onAttachedToRecyclerView(RecyclerView paramRecyclerView) {}
    
    public abstract void onBindViewHolder(VH paramVH, int paramInt);
    
    public void onBindViewHolder(VH paramVH, int paramInt, List<Object> paramList)
    {
      onBindViewHolder(paramVH, paramInt);
    }
    
    public abstract VH onCreateViewHolder(ViewGroup paramViewGroup, int paramInt);
    
    public void onDetachedFromRecyclerView(RecyclerView paramRecyclerView) {}
    
    public boolean onFailedToRecycleView(VH paramVH)
    {
      return false;
    }
    
    public void onViewAttachedToWindow(VH paramVH) {}
    
    public void onViewDetachedFromWindow(VH paramVH) {}
    
    public void onViewRecycled(VH paramVH) {}
    
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver paramAdapterDataObserver)
    {
      this.mObservable.registerObserver(paramAdapterDataObserver);
    }
    
    public void setHasStableIds(boolean paramBoolean)
    {
      if (hasObservers()) {
        throw new IllegalStateException("Cannot change whether this adapter has stable IDs while the adapter has registered observers.");
      }
      this.mHasStableIds = paramBoolean;
    }
    
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver paramAdapterDataObserver)
    {
      this.mObservable.unregisterObserver(paramAdapterDataObserver);
    }
  }
  
  static class AdapterDataObservable
    extends Observable<RecyclerView.AdapterDataObserver>
  {
    public boolean hasObservers()
    {
      return !this.mObservers.isEmpty();
    }
    
    public void notifyChanged()
    {
      int i = this.mObservers.size() - 1;
      while (i >= 0)
      {
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onChanged();
        i -= 1;
      }
    }
    
    public void notifyItemMoved(int paramInt1, int paramInt2)
    {
      int i = this.mObservers.size() - 1;
      while (i >= 0)
      {
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeMoved(paramInt1, paramInt2, 1);
        i -= 1;
      }
    }
    
    public void notifyItemRangeChanged(int paramInt1, int paramInt2)
    {
      notifyItemRangeChanged(paramInt1, paramInt2, null);
    }
    
    public void notifyItemRangeChanged(int paramInt1, int paramInt2, Object paramObject)
    {
      int i = this.mObservers.size() - 1;
      while (i >= 0)
      {
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeChanged(paramInt1, paramInt2, paramObject);
        i -= 1;
      }
    }
    
    public void notifyItemRangeInserted(int paramInt1, int paramInt2)
    {
      int i = this.mObservers.size() - 1;
      while (i >= 0)
      {
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeInserted(paramInt1, paramInt2);
        i -= 1;
      }
    }
    
    public void notifyItemRangeRemoved(int paramInt1, int paramInt2)
    {
      int i = this.mObservers.size() - 1;
      while (i >= 0)
      {
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeRemoved(paramInt1, paramInt2);
        i -= 1;
      }
    }
  }
  
  public static abstract class AdapterDataObserver
  {
    public void onChanged() {}
    
    public void onItemRangeChanged(int paramInt1, int paramInt2) {}
    
    public void onItemRangeChanged(int paramInt1, int paramInt2, Object paramObject)
    {
      onItemRangeChanged(paramInt1, paramInt2);
    }
    
    public void onItemRangeInserted(int paramInt1, int paramInt2) {}
    
    public void onItemRangeMoved(int paramInt1, int paramInt2, int paramInt3) {}
    
    public void onItemRangeRemoved(int paramInt1, int paramInt2) {}
  }
  
  public static abstract interface ChildDrawingOrderCallback
  {
    public abstract int onGetChildDrawingOrder(int paramInt1, int paramInt2);
  }
  
  public static abstract class ItemAnimator
  {
    private long mAddDuration = 120L;
    private long mChangeDuration = 250L;
    private ArrayList<ItemAnimatorFinishedListener> mFinishedListeners = new ArrayList();
    private ItemAnimatorListener mListener = null;
    private long mMoveDuration = 250L;
    private long mRemoveDuration = 120L;
    private boolean mSupportsChangeAnimations = true;
    
    public abstract boolean animateAdd(RecyclerView.ViewHolder paramViewHolder);
    
    public abstract boolean animateChange(RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
    
    public abstract boolean animateMove(RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
    
    public abstract boolean animateRemove(RecyclerView.ViewHolder paramViewHolder);
    
    public final void dispatchAddFinished(RecyclerView.ViewHolder paramViewHolder)
    {
      onAddFinished(paramViewHolder);
      if (this.mListener != null) {
        this.mListener.onAddFinished(paramViewHolder);
      }
    }
    
    public final void dispatchAddStarting(RecyclerView.ViewHolder paramViewHolder)
    {
      onAddStarting(paramViewHolder);
    }
    
    public final void dispatchAnimationsFinished()
    {
      int j = this.mFinishedListeners.size();
      int i = 0;
      while (i < j)
      {
        ((ItemAnimatorFinishedListener)this.mFinishedListeners.get(i)).onAnimationsFinished();
        i += 1;
      }
      this.mFinishedListeners.clear();
    }
    
    public final void dispatchChangeFinished(RecyclerView.ViewHolder paramViewHolder, boolean paramBoolean)
    {
      onChangeFinished(paramViewHolder, paramBoolean);
      if (this.mListener != null) {
        this.mListener.onChangeFinished(paramViewHolder);
      }
    }
    
    public final void dispatchChangeStarting(RecyclerView.ViewHolder paramViewHolder, boolean paramBoolean)
    {
      onChangeStarting(paramViewHolder, paramBoolean);
    }
    
    public final void dispatchMoveFinished(RecyclerView.ViewHolder paramViewHolder)
    {
      onMoveFinished(paramViewHolder);
      if (this.mListener != null) {
        this.mListener.onMoveFinished(paramViewHolder);
      }
    }
    
    public final void dispatchMoveStarting(RecyclerView.ViewHolder paramViewHolder)
    {
      onMoveStarting(paramViewHolder);
    }
    
    public final void dispatchRemoveFinished(RecyclerView.ViewHolder paramViewHolder)
    {
      onRemoveFinished(paramViewHolder);
      if (this.mListener != null) {
        this.mListener.onRemoveFinished(paramViewHolder);
      }
    }
    
    public final void dispatchRemoveStarting(RecyclerView.ViewHolder paramViewHolder)
    {
      onRemoveStarting(paramViewHolder);
    }
    
    public abstract void endAnimation(RecyclerView.ViewHolder paramViewHolder);
    
    public abstract void endAnimations();
    
    public long getAddDuration()
    {
      return this.mAddDuration;
    }
    
    public long getChangeDuration()
    {
      return this.mChangeDuration;
    }
    
    public long getMoveDuration()
    {
      return this.mMoveDuration;
    }
    
    public long getRemoveDuration()
    {
      return this.mRemoveDuration;
    }
    
    public boolean getSupportsChangeAnimations()
    {
      return this.mSupportsChangeAnimations;
    }
    
    public abstract boolean isRunning();
    
    public final boolean isRunning(ItemAnimatorFinishedListener paramItemAnimatorFinishedListener)
    {
      boolean bool = isRunning();
      if (paramItemAnimatorFinishedListener != null)
      {
        if (!bool) {
          paramItemAnimatorFinishedListener.onAnimationsFinished();
        }
      }
      else {
        return bool;
      }
      this.mFinishedListeners.add(paramItemAnimatorFinishedListener);
      return bool;
    }
    
    public void onAddFinished(RecyclerView.ViewHolder paramViewHolder) {}
    
    public void onAddStarting(RecyclerView.ViewHolder paramViewHolder) {}
    
    public void onChangeFinished(RecyclerView.ViewHolder paramViewHolder, boolean paramBoolean) {}
    
    public void onChangeStarting(RecyclerView.ViewHolder paramViewHolder, boolean paramBoolean) {}
    
    public void onMoveFinished(RecyclerView.ViewHolder paramViewHolder) {}
    
    public void onMoveStarting(RecyclerView.ViewHolder paramViewHolder) {}
    
    public void onRemoveFinished(RecyclerView.ViewHolder paramViewHolder) {}
    
    public void onRemoveStarting(RecyclerView.ViewHolder paramViewHolder) {}
    
    public abstract void runPendingAnimations();
    
    public void setAddDuration(long paramLong)
    {
      this.mAddDuration = paramLong;
    }
    
    public void setChangeDuration(long paramLong)
    {
      this.mChangeDuration = paramLong;
    }
    
    void setListener(ItemAnimatorListener paramItemAnimatorListener)
    {
      this.mListener = paramItemAnimatorListener;
    }
    
    public void setMoveDuration(long paramLong)
    {
      this.mMoveDuration = paramLong;
    }
    
    public void setRemoveDuration(long paramLong)
    {
      this.mRemoveDuration = paramLong;
    }
    
    public void setSupportsChangeAnimations(boolean paramBoolean)
    {
      this.mSupportsChangeAnimations = paramBoolean;
    }
    
    public static abstract interface ItemAnimatorFinishedListener
    {
      public abstract void onAnimationsFinished();
    }
    
    static abstract interface ItemAnimatorListener
    {
      public abstract void onAddFinished(RecyclerView.ViewHolder paramViewHolder);
      
      public abstract void onChangeFinished(RecyclerView.ViewHolder paramViewHolder);
      
      public abstract void onMoveFinished(RecyclerView.ViewHolder paramViewHolder);
      
      public abstract void onRemoveFinished(RecyclerView.ViewHolder paramViewHolder);
    }
  }
  
  private class ItemAnimatorRestoreListener
    implements RecyclerView.ItemAnimator.ItemAnimatorListener
  {
    private ItemAnimatorRestoreListener() {}
    
    public void onAddFinished(RecyclerView.ViewHolder paramViewHolder)
    {
      paramViewHolder.setIsRecyclable(true);
      if (!RecyclerView.ViewHolder.-wrap1(paramViewHolder)) {
        RecyclerView.-wrap1(RecyclerView.this, paramViewHolder.itemView);
      }
    }
    
    public void onChangeFinished(RecyclerView.ViewHolder paramViewHolder)
    {
      paramViewHolder.setIsRecyclable(true);
      if ((paramViewHolder.mShadowedHolder != null) && (paramViewHolder.mShadowingHolder == null))
      {
        paramViewHolder.mShadowedHolder = null;
        paramViewHolder.setFlags(-65, RecyclerView.ViewHolder.-get0(paramViewHolder));
      }
      paramViewHolder.mShadowingHolder = null;
      if (!RecyclerView.ViewHolder.-wrap1(paramViewHolder)) {
        RecyclerView.-wrap1(RecyclerView.this, paramViewHolder.itemView);
      }
    }
    
    public void onMoveFinished(RecyclerView.ViewHolder paramViewHolder)
    {
      paramViewHolder.setIsRecyclable(true);
      if (!RecyclerView.ViewHolder.-wrap1(paramViewHolder)) {
        RecyclerView.-wrap1(RecyclerView.this, paramViewHolder.itemView);
      }
    }
    
    public void onRemoveFinished(RecyclerView.ViewHolder paramViewHolder)
    {
      paramViewHolder.setIsRecyclable(true);
      if ((!RecyclerView.-wrap1(RecyclerView.this, paramViewHolder.itemView)) && (paramViewHolder.isTmpDetached())) {
        RecyclerView.this.removeDetachedView(paramViewHolder.itemView, false);
      }
    }
  }
  
  public static abstract class ItemDecoration
  {
    @Deprecated
    public void doDraw(Canvas paramCanvas, RecyclerView paramRecyclerView) {}
    
    @Deprecated
    public void getItemOffsets(Rect paramRect, int paramInt, RecyclerView paramRecyclerView)
    {
      paramRect.set(0, 0, 0, 0);
    }
    
    public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState)
    {
      getItemOffsets(paramRect, ((RecyclerView.LayoutParams)paramView.getLayoutParams()).getViewLayoutPosition(), paramRecyclerView);
    }
    
    public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
    {
      doDraw(paramCanvas, paramRecyclerView);
    }
    
    @Deprecated
    public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView) {}
    
    public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
    {
      onDrawOver(paramCanvas, paramRecyclerView);
    }
  }
  
  private static class ItemHolderInfo
  {
    int bottom;
    RecyclerView.ViewHolder holder;
    int left;
    int right;
    int top;
    
    ItemHolderInfo(RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.holder = paramViewHolder;
      this.left = paramInt1;
      this.top = paramInt2;
      this.right = paramInt3;
      this.bottom = paramInt4;
    }
  }
  
  public static abstract class LayoutManager
  {
    ChildHelper mChildHelper;
    private Method mDispatchFinishTemporaryDetach;
    private Method mDispatchStartTemporaryDetach;
    private boolean mIsAttachedToWindow = false;
    RecyclerView mRecyclerView;
    private boolean mRequestedSimpleAnimations = false;
    RecyclerView.SmoothScroller mSmoothScroller;
    private boolean mTempDetachBound;
    
    private void addViewInt(View paramView, int paramInt, boolean paramBoolean)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      RecyclerView.LayoutParams localLayoutParams;
      if ((paramBoolean) || (localViewHolder.isRemoved()))
      {
        this.mRecyclerView.mState.addToDisappearingList(paramView);
        localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
        if ((!localViewHolder.wasReturnedFromScrap()) && (!localViewHolder.isScrap())) {
          break label126;
        }
        if (!localViewHolder.isScrap()) {
          break label118;
        }
        localViewHolder.unScrap();
        label67:
        this.mChildHelper.attachViewToParent(paramView, paramInt, paramView.getLayoutParams(), false);
      }
      for (;;)
      {
        if (localLayoutParams.mPendingInvalidate)
        {
          localViewHolder.itemView.invalidate();
          localLayoutParams.mPendingInvalidate = false;
        }
        return;
        this.mRecyclerView.mState.removeFromDisappearingList(paramView);
        break;
        label118:
        localViewHolder.clearReturnedFromScrapFlag();
        break label67;
        label126:
        if (paramView.getParent() == this.mRecyclerView)
        {
          int j = this.mChildHelper.indexOfChild(paramView);
          int i = paramInt;
          if (paramInt == -1) {
            i = this.mChildHelper.getChildCount();
          }
          if (j == -1) {
            throw new IllegalStateException("Added View has RecyclerView as parent but view is not a real child. Unfiltered index:" + this.mRecyclerView.indexOfChild(paramView));
          }
          if (j != i) {
            RecyclerView.-get9(this.mRecyclerView).moveView(j, i);
          }
        }
        else
        {
          this.mChildHelper.addView(paramView, paramInt, false);
          localLayoutParams.mInsetsDirty = true;
          if ((this.mSmoothScroller != null) && (this.mSmoothScroller.isRunning())) {
            this.mSmoothScroller.onChildAttachedToWindow(paramView);
          }
        }
      }
    }
    
    private void bindTempDetach()
    {
      try
      {
        this.mDispatchStartTemporaryDetach = View.class.getDeclaredMethod("dispatchStartTemporaryDetach", new Class[0]);
        this.mDispatchFinishTemporaryDetach = View.class.getDeclaredMethod("dispatchFinishTemporaryDetach", new Class[0]);
        this.mTempDetachBound = true;
        return;
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        for (;;)
        {
          Log.e("RecyclerView", "Couldn't find method", localNoSuchMethodException);
        }
      }
    }
    
    private void detachViewInternal(int paramInt, View paramView)
    {
      this.mChildHelper.detachViewFromParent(paramInt);
    }
    
    public static int getChildMeasureSpec(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
    {
      int i = Math.max(0, paramInt1 - paramInt2);
      paramInt2 = 0;
      paramInt1 = 0;
      if (paramBoolean) {
        if (paramInt3 >= 0)
        {
          paramInt2 = paramInt3;
          paramInt1 = 1073741824;
        }
      }
      for (;;)
      {
        return View.MeasureSpec.makeMeasureSpec(paramInt2, paramInt1);
        paramInt2 = 0;
        paramInt1 = 0;
        continue;
        if (paramInt3 >= 0)
        {
          paramInt2 = paramInt3;
          paramInt1 = 1073741824;
        }
        else if (paramInt3 == -1)
        {
          paramInt2 = i;
          paramInt1 = 1073741824;
        }
        else if (paramInt3 == -2)
        {
          paramInt2 = i;
          paramInt1 = Integer.MIN_VALUE;
        }
      }
    }
    
    public static Properties getProperties(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
    {
      Properties localProperties = new Properties();
      paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.RecyclerView, paramInt1, paramInt2);
      localProperties.orientation = paramContext.getInt(R.styleable.RecyclerView_android_orientation, 1);
      localProperties.spanCount = paramContext.getInt(R.styleable.RecyclerView_op_spanCount, 1);
      localProperties.reverseLayout = paramContext.getBoolean(R.styleable.RecyclerView_op_reverseLayout, false);
      localProperties.stackFromEnd = paramContext.getBoolean(R.styleable.RecyclerView_op_stackFromEnd, false);
      paramContext.recycle();
      return localProperties;
    }
    
    private void onSmoothScrollerStopped(RecyclerView.SmoothScroller paramSmoothScroller)
    {
      if (this.mSmoothScroller == paramSmoothScroller) {
        this.mSmoothScroller = null;
      }
    }
    
    private void scrapOrRecycleView(RecyclerView.Recycler paramRecycler, int paramInt, View paramView)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      if (localViewHolder.shouldIgnore()) {
        return;
      }
      if ((!localViewHolder.isInvalid()) || (localViewHolder.isRemoved())) {}
      while ((localViewHolder.isChanged()) || (RecyclerView.-get2(this.mRecyclerView).hasStableIds()))
      {
        detachViewAt(paramInt);
        paramRecycler.scrapView(paramView);
        return;
      }
      removeViewAt(paramInt);
      paramRecycler.recycleViewHolderInternal(localViewHolder);
    }
    
    public void addDisappearingView(View paramView)
    {
      addDisappearingView(paramView, -1);
    }
    
    public void addDisappearingView(View paramView, int paramInt)
    {
      addViewInt(paramView, paramInt, true);
    }
    
    public void addView(View paramView)
    {
      addView(paramView, -1);
    }
    
    public void addView(View paramView, int paramInt)
    {
      addViewInt(paramView, paramInt, false);
    }
    
    public void assertInLayoutOrScroll(String paramString)
    {
      if (this.mRecyclerView != null) {
        this.mRecyclerView.assertInLayoutOrScroll(paramString);
      }
    }
    
    public void assertNotInLayoutOrScroll(String paramString)
    {
      if (this.mRecyclerView != null) {
        this.mRecyclerView.assertNotInLayoutOrScroll(paramString);
      }
    }
    
    public void attachView(View paramView)
    {
      attachView(paramView, -1);
    }
    
    public void attachView(View paramView, int paramInt)
    {
      attachView(paramView, paramInt, (RecyclerView.LayoutParams)paramView.getLayoutParams());
    }
    
    public void attachView(View paramView, int paramInt, RecyclerView.LayoutParams paramLayoutParams)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      if (localViewHolder.isRemoved()) {
        this.mRecyclerView.mState.addToDisappearingList(paramView);
      }
      for (;;)
      {
        this.mChildHelper.attachViewToParent(paramView, paramInt, paramLayoutParams, localViewHolder.isRemoved());
        return;
        this.mRecyclerView.mState.removeFromDisappearingList(paramView);
      }
    }
    
    public void calculateItemDecorationsForChild(View paramView, Rect paramRect)
    {
      if (this.mRecyclerView == null)
      {
        paramRect.set(0, 0, 0, 0);
        return;
      }
      paramRect.set(this.mRecyclerView.getItemDecorInsetsForChild(paramView));
    }
    
    public boolean canScrollHorizontally()
    {
      return false;
    }
    
    public boolean canScrollVertically()
    {
      return false;
    }
    
    public boolean checkLayoutParams(RecyclerView.LayoutParams paramLayoutParams)
    {
      return paramLayoutParams != null;
    }
    
    public int computeHorizontalScrollExtent(RecyclerView.State paramState)
    {
      return 0;
    }
    
    public int computeHorizontalScrollOffset(RecyclerView.State paramState)
    {
      return 0;
    }
    
    public int computeHorizontalScrollRange(RecyclerView.State paramState)
    {
      return 0;
    }
    
    public int computeVerticalScrollExtent(RecyclerView.State paramState)
    {
      return 0;
    }
    
    public int computeVerticalScrollOffset(RecyclerView.State paramState)
    {
      return 0;
    }
    
    public int computeVerticalScrollRange(RecyclerView.State paramState)
    {
      return 0;
    }
    
    public void detachAndScrapAttachedViews(RecyclerView.Recycler paramRecycler)
    {
      int i = getChildCount() - 1;
      while (i >= 0)
      {
        scrapOrRecycleView(paramRecycler, i, getChildAt(i));
        i -= 1;
      }
    }
    
    public void detachAndScrapView(View paramView, RecyclerView.Recycler paramRecycler)
    {
      scrapOrRecycleView(paramRecycler, this.mChildHelper.indexOfChild(paramView), paramView);
    }
    
    public void detachAndScrapViewAt(int paramInt, RecyclerView.Recycler paramRecycler)
    {
      scrapOrRecycleView(paramRecycler, paramInt, getChildAt(paramInt));
    }
    
    public void detachView(View paramView)
    {
      int i = this.mChildHelper.indexOfChild(paramView);
      if (i >= 0) {
        detachViewInternal(i, paramView);
      }
    }
    
    public void detachViewAt(int paramInt)
    {
      detachViewInternal(paramInt, getChildAt(paramInt));
    }
    
    void dispatchAttachedToWindow(RecyclerView paramRecyclerView)
    {
      this.mIsAttachedToWindow = true;
      onAttachedToWindow(paramRecyclerView);
    }
    
    void dispatchDetachedFromWindow(RecyclerView paramRecyclerView, RecyclerView.Recycler paramRecycler)
    {
      this.mIsAttachedToWindow = false;
      onDetachedFromWindow(paramRecyclerView, paramRecycler);
    }
    
    void dispatchFinishTemporaryDetach(View paramView)
    {
      if (!this.mTempDetachBound) {
        bindTempDetach();
      }
      if (this.mDispatchFinishTemporaryDetach != null) {
        try
        {
          this.mDispatchFinishTemporaryDetach.invoke(paramView, new Object[0]);
          return;
        }
        catch (Exception paramView)
        {
          Log.d("RecyclerView", "Error calling dispatchFinishTemporaryDetach", paramView);
          return;
        }
      }
      paramView.onFinishTemporaryDetach();
    }
    
    public void dispatchStartTemporaryDetach(View paramView)
    {
      if (!this.mTempDetachBound) {
        bindTempDetach();
      }
      if (this.mDispatchStartTemporaryDetach != null) {
        try
        {
          this.mDispatchStartTemporaryDetach.invoke(paramView, new Object[0]);
          return;
        }
        catch (Exception paramView)
        {
          Log.d("RecyclerView", "Error calling dispatchStartTemporaryDetach", paramView);
          return;
        }
      }
      paramView.onStartTemporaryDetach();
    }
    
    public void doMeasure(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt1, int paramInt2)
    {
      RecyclerView.-wrap7(this.mRecyclerView, paramInt1, paramInt2);
    }
    
    public void endAnimation(View paramView)
    {
      if (this.mRecyclerView.mItemAnimator != null) {
        this.mRecyclerView.mItemAnimator.endAnimation(RecyclerView.getChildViewHolderInt(paramView));
      }
    }
    
    public View findViewByPosition(int paramInt)
    {
      int j = getChildCount();
      int i = 0;
      if (i < j)
      {
        View localView = getChildAt(i);
        RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(localView);
        if (localViewHolder == null) {}
        while ((localViewHolder.getLayoutPosition() != paramInt) || (localViewHolder.shouldIgnore()) || ((!this.mRecyclerView.mState.isPreLayout()) && (localViewHolder.isRemoved())))
        {
          i += 1;
          break;
        }
        return localView;
      }
      return null;
    }
    
    public abstract RecyclerView.LayoutParams generateDefaultLayoutParams();
    
    public RecyclerView.LayoutParams generateLayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      return new RecyclerView.LayoutParams(paramContext, paramAttributeSet);
    }
    
    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      if ((paramLayoutParams instanceof RecyclerView.LayoutParams)) {
        return new RecyclerView.LayoutParams((RecyclerView.LayoutParams)paramLayoutParams);
      }
      if ((paramLayoutParams instanceof ViewGroup.MarginLayoutParams)) {
        return new RecyclerView.LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams);
      }
      return new RecyclerView.LayoutParams(paramLayoutParams);
    }
    
    public int getBaseline()
    {
      return -1;
    }
    
    public int getBottomDecorationHeight(View paramView)
    {
      return ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets.bottom;
    }
    
    public View getChildAt(int paramInt)
    {
      View localView = null;
      if (this.mChildHelper != null) {
        localView = this.mChildHelper.getChildAt(paramInt);
      }
      return localView;
    }
    
    public int getChildCount()
    {
      if (this.mChildHelper != null) {
        return this.mChildHelper.getChildCount();
      }
      return 0;
    }
    
    public boolean getClipToPadding()
    {
      if (this.mRecyclerView != null) {
        return RecyclerView.-get3(this.mRecyclerView);
      }
      return false;
    }
    
    public int getColumnCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      int i = 1;
      if ((this.mRecyclerView == null) || (RecyclerView.-get2(this.mRecyclerView) == null)) {
        return 1;
      }
      if (canScrollHorizontally()) {
        i = RecyclerView.-get2(this.mRecyclerView).getItemCount();
      }
      return i;
    }
    
    public int getDecoratedBottom(View paramView)
    {
      return paramView.getBottom() + getBottomDecorationHeight(paramView);
    }
    
    public int getDecoratedLeft(View paramView)
    {
      return paramView.getLeft() - getLeftDecorationWidth(paramView);
    }
    
    public int getDecoratedMeasuredHeight(View paramView)
    {
      Rect localRect = ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets;
      return paramView.getMeasuredHeight() + localRect.top + localRect.bottom;
    }
    
    public int getDecoratedMeasuredWidth(View paramView)
    {
      Rect localRect = ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets;
      return paramView.getMeasuredWidth() + localRect.left + localRect.right;
    }
    
    public int getDecoratedRight(View paramView)
    {
      return paramView.getRight() + getRightDecorationWidth(paramView);
    }
    
    public int getDecoratedTop(View paramView)
    {
      return paramView.getTop() - getTopDecorationHeight(paramView);
    }
    
    public View getFocusedChild()
    {
      if (this.mRecyclerView == null) {
        return null;
      }
      View localView = this.mRecyclerView.getFocusedChild();
      if ((localView == null) || (this.mChildHelper.isHidden(localView))) {
        return null;
      }
      return localView;
    }
    
    public int getHeight()
    {
      if (this.mRecyclerView != null) {
        return this.mRecyclerView.getHeight();
      }
      return 0;
    }
    
    public int getItemCount()
    {
      RecyclerView.Adapter localAdapter = null;
      if (this.mRecyclerView != null) {
        localAdapter = this.mRecyclerView.getAdapter();
      }
      if (localAdapter != null) {
        return localAdapter.getItemCount();
      }
      return 0;
    }
    
    public int getItemViewType(View paramView)
    {
      return RecyclerView.getChildViewHolderInt(paramView).getItemViewType();
    }
    
    public int getLayoutDirection()
    {
      return this.mRecyclerView.getLayoutDirection();
    }
    
    public int getLeftDecorationWidth(View paramView)
    {
      return ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets.left;
    }
    
    public int getMinimumHeight()
    {
      return this.mRecyclerView.getMinimumHeight();
    }
    
    public int getMinimumWidth()
    {
      return this.mRecyclerView.getMinimumWidth();
    }
    
    public int getPaddingBottom()
    {
      if (this.mRecyclerView != null) {
        return this.mRecyclerView.getPaddingBottom();
      }
      return 0;
    }
    
    public int getPaddingEnd()
    {
      if (this.mRecyclerView != null) {
        return this.mRecyclerView.getPaddingEnd();
      }
      return 0;
    }
    
    public int getPaddingLeft()
    {
      if (this.mRecyclerView != null) {
        return this.mRecyclerView.getPaddingLeft();
      }
      return 0;
    }
    
    public int getPaddingRight()
    {
      if (this.mRecyclerView != null) {
        return this.mRecyclerView.getPaddingRight();
      }
      return 0;
    }
    
    public int getPaddingStart()
    {
      if (this.mRecyclerView != null) {
        return this.mRecyclerView.getPaddingStart();
      }
      return 0;
    }
    
    public int getPaddingTop()
    {
      if (this.mRecyclerView != null) {
        return this.mRecyclerView.getPaddingTop();
      }
      return 0;
    }
    
    public int getPosition(View paramView)
    {
      return ((RecyclerView.LayoutParams)paramView.getLayoutParams()).getViewLayoutPosition();
    }
    
    public int getRightDecorationWidth(View paramView)
    {
      return ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets.right;
    }
    
    public int getRowCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      int i = 1;
      if ((this.mRecyclerView == null) || (RecyclerView.-get2(this.mRecyclerView) == null)) {
        return 1;
      }
      if (canScrollVertically()) {
        i = RecyclerView.-get2(this.mRecyclerView).getItemCount();
      }
      return i;
    }
    
    public int getSelectionModeForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      return 0;
    }
    
    public int getTopDecorationHeight(View paramView)
    {
      return ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets.top;
    }
    
    public int getWidth()
    {
      if (this.mRecyclerView != null) {
        return this.mRecyclerView.getWidth();
      }
      return 0;
    }
    
    public boolean hasFocus()
    {
      if (this.mRecyclerView != null) {
        return this.mRecyclerView.hasFocus();
      }
      return false;
    }
    
    public void ignoreView(View paramView)
    {
      if ((paramView.getParent() != this.mRecyclerView) || (this.mRecyclerView.indexOfChild(paramView) == -1)) {
        throw new IllegalArgumentException("View should be fully attached to be ignored");
      }
      paramView = RecyclerView.getChildViewHolderInt(paramView);
      paramView.addFlags(128);
      this.mRecyclerView.mState.onViewIgnored(paramView);
    }
    
    public boolean isAttachedToWindow()
    {
      return this.mIsAttachedToWindow;
    }
    
    public boolean isFocused()
    {
      if (this.mRecyclerView != null) {
        return this.mRecyclerView.isFocused();
      }
      return false;
    }
    
    public boolean isLayoutHierarchical(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      return false;
    }
    
    public boolean isSmoothScrolling()
    {
      if (this.mSmoothScroller != null) {
        return this.mSmoothScroller.isRunning();
      }
      return false;
    }
    
    public void layoutDecorated(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Rect localRect = ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets;
      paramView.layout(localRect.left + paramInt1, localRect.top + paramInt2, paramInt3 - localRect.right, paramInt4 - localRect.bottom);
    }
    
    public void measureChild(View paramView, int paramInt1, int paramInt2)
    {
      RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
      Rect localRect = this.mRecyclerView.getItemDecorInsetsForChild(paramView);
      int i = localRect.left;
      int j = localRect.right;
      int k = localRect.top;
      int m = localRect.bottom;
      paramView.measure(getChildMeasureSpec(getWidth(), getPaddingLeft() + getPaddingRight() + (paramInt1 + (i + j)), localLayoutParams.width, canScrollHorizontally()), getChildMeasureSpec(getHeight(), getPaddingTop() + getPaddingBottom() + (paramInt2 + (k + m)), localLayoutParams.height, canScrollVertically()));
    }
    
    public void measureChildWithMargins(View paramView, int paramInt1, int paramInt2)
    {
      RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
      Rect localRect = this.mRecyclerView.getItemDecorInsetsForChild(paramView);
      int i = localRect.left;
      int j = localRect.right;
      int k = localRect.top;
      int m = localRect.bottom;
      paramView.measure(getChildMeasureSpec(getWidth(), getPaddingLeft() + getPaddingRight() + localLayoutParams.leftMargin + localLayoutParams.rightMargin + (paramInt1 + (i + j)), localLayoutParams.width, canScrollHorizontally()), getChildMeasureSpec(getHeight(), getPaddingTop() + getPaddingBottom() + localLayoutParams.topMargin + localLayoutParams.bottomMargin + (paramInt2 + (k + m)), localLayoutParams.height, canScrollVertically()));
    }
    
    public void moveView(int paramInt1, int paramInt2)
    {
      View localView = getChildAt(paramInt1);
      if (localView == null) {
        throw new IllegalArgumentException("Cannot move a child from non-existing index:" + paramInt1);
      }
      detachViewAt(paramInt1);
      attachView(localView, paramInt2);
    }
    
    public void offsetChildrenHorizontal(int paramInt)
    {
      if (this.mRecyclerView != null) {
        this.mRecyclerView.offsetChildrenHorizontal(paramInt);
      }
    }
    
    public void offsetChildrenVertical(int paramInt)
    {
      if (this.mRecyclerView != null) {
        this.mRecyclerView.offsetChildrenVertical(paramInt);
      }
    }
    
    public void onAdapterChanged(RecyclerView.Adapter paramAdapter1, RecyclerView.Adapter paramAdapter2) {}
    
    public boolean onAddFocusables(RecyclerView paramRecyclerView, ArrayList<View> paramArrayList, int paramInt1, int paramInt2)
    {
      return false;
    }
    
    public void onAttachedToWindow(RecyclerView paramRecyclerView) {}
    
    @Deprecated
    public void onDetachedFromWindow(RecyclerView paramRecyclerView) {}
    
    public void onDetachedFromWindow(RecyclerView paramRecyclerView, RecyclerView.Recycler paramRecycler)
    {
      onDetachedFromWindow(paramRecyclerView);
    }
    
    public View onFocusSearchFailed(View paramView, int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      return null;
    }
    
    public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
    {
      onInitializeAccessibilityEvent(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, paramAccessibilityEvent);
    }
    
    public void onInitializeAccessibilityEvent(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, AccessibilityEvent paramAccessibilityEvent)
    {
      boolean bool2 = true;
      paramRecycler = AccessibilityEvent.obtain(paramAccessibilityEvent);
      if ((this.mRecyclerView == null) || (paramRecycler == null)) {
        return;
      }
      boolean bool1 = bool2;
      if (!this.mRecyclerView.canScrollVertically(1))
      {
        bool1 = bool2;
        if (!this.mRecyclerView.canScrollVertically(-1))
        {
          bool1 = bool2;
          if (!this.mRecyclerView.canScrollHorizontally(-1)) {
            bool1 = this.mRecyclerView.canScrollHorizontally(1);
          }
        }
      }
      paramRecycler.setScrollable(bool1);
      if (RecyclerView.-get2(this.mRecyclerView) != null) {
        paramRecycler.setItemCount(RecyclerView.-get2(this.mRecyclerView).getItemCount());
      }
    }
    
    void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      onInitializeAccessibilityNodeInfo(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, paramAccessibilityNodeInfo);
    }
    
    public void onInitializeAccessibilityNodeInfo(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      if ((this.mRecyclerView.canScrollVertically(-1)) || (this.mRecyclerView.canScrollHorizontally(-1)))
      {
        paramAccessibilityNodeInfo.addAction(8192);
        paramAccessibilityNodeInfo.setScrollable(true);
      }
      if ((this.mRecyclerView.canScrollVertically(1)) || (this.mRecyclerView.canScrollHorizontally(1)))
      {
        paramAccessibilityNodeInfo.addAction(4096);
        paramAccessibilityNodeInfo.setScrollable(true);
      }
      paramAccessibilityNodeInfo.setCollectionInfo(AccessibilityNodeInfo.CollectionInfo.obtain(getRowCountForAccessibility(paramRecycler, paramState), getColumnCountForAccessibility(paramRecycler, paramState), isLayoutHierarchical(paramRecycler, paramState), getSelectionModeForAccessibility(paramRecycler, paramState)));
    }
    
    void onInitializeAccessibilityNodeInfoForItem(View paramView, AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      if ((localViewHolder == null) || (localViewHolder.isRemoved())) {}
      while (this.mChildHelper.isHidden(localViewHolder.itemView)) {
        return;
      }
      onInitializeAccessibilityNodeInfoForItem(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, paramView, paramAccessibilityNodeInfo);
    }
    
    public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, View paramView, AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      int i;
      if (canScrollVertically())
      {
        i = getPosition(paramView);
        if (!canScrollHorizontally()) {
          break label51;
        }
      }
      label51:
      for (int j = getPosition(paramView);; j = 0)
      {
        paramAccessibilityNodeInfo.setCollectionItemInfo(AccessibilityNodeInfo.CollectionItemInfo.obtain(i, 1, j, 1, false, false));
        return;
        i = 0;
        break;
      }
    }
    
    public View onInterceptFocusSearch(View paramView, int paramInt)
    {
      return null;
    }
    
    public void onItemsAdded(RecyclerView paramRecyclerView, int paramInt1, int paramInt2) {}
    
    public void onItemsChanged(RecyclerView paramRecyclerView) {}
    
    public void onItemsMoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, int paramInt3) {}
    
    public void onItemsRemoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2) {}
    
    public void onItemsUpdated(RecyclerView paramRecyclerView, int paramInt1, int paramInt2) {}
    
    public void onItemsUpdated(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, Object paramObject)
    {
      onItemsUpdated(paramRecyclerView, paramInt1, paramInt2);
    }
    
    public void onLayoutChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      Log.e("RecyclerView", "You must override onLayoutChildren(Recycler recycler, State state) ");
    }
    
    @Deprecated
    public boolean onRequestChildFocus(RecyclerView paramRecyclerView, View paramView1, View paramView2)
    {
      if (!isSmoothScrolling()) {
        return paramRecyclerView.isComputingLayout();
      }
      return true;
    }
    
    public boolean onRequestChildFocus(RecyclerView paramRecyclerView, RecyclerView.State paramState, View paramView1, View paramView2)
    {
      return onRequestChildFocus(paramRecyclerView, paramView1, paramView2);
    }
    
    public void onRestoreInstanceState(Parcelable paramParcelable) {}
    
    public Parcelable onSaveInstanceState()
    {
      return null;
    }
    
    public void onScrollStateChanged(int paramInt) {}
    
    boolean performAccessibilityAction(int paramInt, Bundle paramBundle)
    {
      return performAccessibilityAction(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, paramInt, paramBundle);
    }
    
    public boolean performAccessibilityAction(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt, Bundle paramBundle)
    {
      if (this.mRecyclerView == null) {
        return false;
      }
      int k = 0;
      int m = 0;
      int i = 0;
      int j = 0;
      switch (paramInt)
      {
      default: 
        paramInt = i;
      }
      while ((paramInt == 0) && (j == 0))
      {
        return false;
        i = k;
        if (this.mRecyclerView.canScrollVertically(-1)) {
          i = -(getHeight() - getPaddingTop() - getPaddingBottom());
        }
        paramInt = i;
        if (this.mRecyclerView.canScrollHorizontally(-1))
        {
          j = -(getWidth() - getPaddingLeft() - getPaddingRight());
          paramInt = i;
          continue;
          i = m;
          if (this.mRecyclerView.canScrollVertically(1)) {
            i = getHeight() - getPaddingTop() - getPaddingBottom();
          }
          paramInt = i;
          if (this.mRecyclerView.canScrollHorizontally(1))
          {
            j = getWidth() - getPaddingLeft() - getPaddingRight();
            paramInt = i;
          }
        }
      }
      this.mRecyclerView.scrollBy(j, paramInt);
      return true;
    }
    
    boolean performAccessibilityActionForItem(View paramView, int paramInt, Bundle paramBundle)
    {
      return performAccessibilityActionForItem(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, paramView, paramInt, paramBundle);
    }
    
    public boolean performAccessibilityActionForItem(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, View paramView, int paramInt, Bundle paramBundle)
    {
      return false;
    }
    
    public void postOnAnimation(Runnable paramRunnable)
    {
      if (this.mRecyclerView != null) {
        this.mRecyclerView.postOnAnimation(paramRunnable);
      }
    }
    
    public void removeAllViews()
    {
      int i = getChildCount() - 1;
      while (i >= 0)
      {
        this.mChildHelper.removeViewAt(i);
        i -= 1;
      }
    }
    
    public void removeAndRecycleAllViews(RecyclerView.Recycler paramRecycler)
    {
      int i = getChildCount() - 1;
      while (i >= 0)
      {
        if (!RecyclerView.getChildViewHolderInt(getChildAt(i)).shouldIgnore()) {
          removeAndRecycleViewAt(i, paramRecycler);
        }
        i -= 1;
      }
    }
    
    void removeAndRecycleScrapInt(RecyclerView.Recycler paramRecycler)
    {
      int j = paramRecycler.getScrapCount();
      int i = j - 1;
      if (i >= 0)
      {
        View localView = paramRecycler.getScrapViewAt(i);
        RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(localView);
        if (localViewHolder.shouldIgnore()) {}
        for (;;)
        {
          i -= 1;
          break;
          localViewHolder.setIsRecyclable(false);
          if (localViewHolder.isTmpDetached()) {
            this.mRecyclerView.removeDetachedView(localView, false);
          }
          if (this.mRecyclerView.mItemAnimator != null) {
            this.mRecyclerView.mItemAnimator.endAnimation(localViewHolder);
          }
          localViewHolder.setIsRecyclable(true);
          paramRecycler.quickRecycleScrapView(localView);
        }
      }
      paramRecycler.clearScrap();
      if (j > 0) {
        this.mRecyclerView.invalidate();
      }
    }
    
    public void removeAndRecycleView(View paramView, RecyclerView.Recycler paramRecycler)
    {
      removeView(paramView);
      paramRecycler.recycleView(paramView);
    }
    
    public void removeAndRecycleViewAt(int paramInt, RecyclerView.Recycler paramRecycler)
    {
      View localView = getChildAt(paramInt);
      removeViewAt(paramInt);
      paramRecycler.recycleView(localView);
    }
    
    public boolean removeCallbacks(Runnable paramRunnable)
    {
      if (this.mRecyclerView != null) {
        return this.mRecyclerView.removeCallbacks(paramRunnable);
      }
      return false;
    }
    
    public void removeDetachedView(View paramView)
    {
      this.mRecyclerView.removeDetachedView(paramView, false);
    }
    
    public void removeView(View paramView)
    {
      this.mChildHelper.removeView(paramView);
    }
    
    public void removeViewAt(int paramInt)
    {
      if (getChildAt(paramInt) != null) {
        this.mChildHelper.removeViewAt(paramInt);
      }
    }
    
    public boolean requestChildRectangleOnScreen(RecyclerView paramRecyclerView, View paramView, Rect paramRect, boolean paramBoolean)
    {
      int i2 = getPaddingLeft();
      int m = getPaddingTop();
      int i3 = getWidth() - getPaddingRight();
      int i1 = getHeight();
      int i6 = getPaddingBottom();
      int i4 = paramView.getLeft() + paramRect.left;
      int n = paramView.getTop() + paramRect.top;
      int i5 = i4 + paramRect.width();
      int i7 = paramRect.height();
      int i = Math.min(0, i4 - i2);
      int j = Math.min(0, n - m);
      int k = Math.max(0, i5 - i3);
      i1 = Math.max(0, n + i7 - (i1 - i6));
      if (getLayoutDirection() == 1) {
        if (k != 0)
        {
          i = k;
          if (j == 0) {
            break label207;
          }
          label144:
          if ((i == 0) && (j == 0)) {
            break label233;
          }
          if (!paramBoolean) {
            break label222;
          }
          paramRecyclerView.scrollBy(i, j);
        }
      }
      for (;;)
      {
        return true;
        i = Math.max(i, i5 - i3);
        break;
        if (i != 0) {
          break;
        }
        i = Math.min(i4 - i2, k);
        break;
        label207:
        j = Math.min(n - m, i1);
        break label144;
        label222:
        paramRecyclerView.smoothScrollBy(i, j);
      }
      label233:
      return false;
    }
    
    public void requestLayout()
    {
      if (this.mRecyclerView != null) {
        this.mRecyclerView.requestLayout();
      }
    }
    
    public void requestSimpleAnimationsInNextLayout()
    {
      this.mRequestedSimpleAnimations = true;
    }
    
    public int scrollHorizontallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      return 0;
    }
    
    public void scrollToPosition(int paramInt) {}
    
    public int scrollVerticallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      return 0;
    }
    
    public void setMeasuredDimension(int paramInt1, int paramInt2)
    {
      RecyclerView.-wrap15(this.mRecyclerView, paramInt1, paramInt2);
    }
    
    void setRecyclerView(RecyclerView paramRecyclerView)
    {
      if (paramRecyclerView == null)
      {
        this.mRecyclerView = null;
        this.mChildHelper = null;
        return;
      }
      this.mRecyclerView = paramRecyclerView;
      this.mChildHelper = paramRecyclerView.mChildHelper;
    }
    
    public void smoothScrollToPosition(RecyclerView paramRecyclerView, RecyclerView.State paramState, int paramInt)
    {
      Log.e("RecyclerView", "You must override smoothScrollToPosition to support smooth scrolling");
    }
    
    public void startSmoothScroll(RecyclerView.SmoothScroller paramSmoothScroller)
    {
      if ((this.mSmoothScroller != null) && (paramSmoothScroller != this.mSmoothScroller) && (this.mSmoothScroller.isRunning())) {
        this.mSmoothScroller.stop();
      }
      this.mSmoothScroller = paramSmoothScroller;
      this.mSmoothScroller.start(this.mRecyclerView, this);
    }
    
    public void stopIgnoringView(View paramView)
    {
      paramView = RecyclerView.getChildViewHolderInt(paramView);
      paramView.stopIgnoring();
      paramView.resetInternal();
      paramView.addFlags(4);
    }
    
    void stopSmoothScroller()
    {
      if (this.mSmoothScroller != null) {
        this.mSmoothScroller.stop();
      }
    }
    
    public boolean supportsPredictiveItemAnimations()
    {
      return false;
    }
    
    public static class Properties
    {
      public int orientation;
      public boolean reverseLayout;
      public int spanCount;
      public boolean stackFromEnd;
    }
  }
  
  public static class LayoutParams
    extends ViewGroup.MarginLayoutParams
  {
    final Rect mDecorInsets = new Rect();
    boolean mInsetsDirty = true;
    boolean mPendingInvalidate = false;
    RecyclerView.ViewHolder mViewHolder;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
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
    
    public int getViewAdapterPosition()
    {
      return this.mViewHolder.getAdapterPosition();
    }
    
    public int getViewLayoutPosition()
    {
      return this.mViewHolder.getLayoutPosition();
    }
    
    public int getViewPosition()
    {
      return this.mViewHolder.getPosition();
    }
    
    public boolean isItemChanged()
    {
      return this.mViewHolder.isChanged();
    }
    
    public boolean isItemRemoved()
    {
      return this.mViewHolder.isRemoved();
    }
    
    public boolean isViewInvalid()
    {
      return this.mViewHolder.isInvalid();
    }
    
    public boolean viewNeedsUpdate()
    {
      return this.mViewHolder.needsUpdate();
    }
  }
  
  public static abstract interface OnChildAttachStateChangeListener
  {
    public abstract void onChildViewAttachedToWindow(View paramView);
    
    public abstract void onChildViewDetachedFromWindow(View paramView);
  }
  
  public static abstract interface OnItemTouchListener
  {
    public abstract boolean onInterceptTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent);
    
    public abstract void onRequestDisallowInterceptTouchEvent(boolean paramBoolean);
    
    public abstract void onTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent);
  }
  
  public static abstract class OnScrollListener
  {
    public void onScrollStateChanged(RecyclerView paramRecyclerView, int paramInt) {}
    
    public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2) {}
  }
  
  public static class RecycledViewPool
  {
    private static final int DEFAULT_MAX_SCRAP = 5;
    private int mAttachCount = 0;
    private SparseIntArray mMaxScrap = new SparseIntArray();
    private SparseArray<ArrayList<RecyclerView.ViewHolder>> mScrap = new SparseArray();
    
    private ArrayList<RecyclerView.ViewHolder> getScrapHeapForType(int paramInt)
    {
      ArrayList localArrayList2 = (ArrayList)this.mScrap.get(paramInt);
      ArrayList localArrayList1 = localArrayList2;
      if (localArrayList2 == null)
      {
        localArrayList2 = new ArrayList();
        this.mScrap.put(paramInt, localArrayList2);
        localArrayList1 = localArrayList2;
        if (this.mMaxScrap.indexOfKey(paramInt) < 0)
        {
          this.mMaxScrap.put(paramInt, 5);
          localArrayList1 = localArrayList2;
        }
      }
      return localArrayList1;
    }
    
    void attach(RecyclerView.Adapter paramAdapter)
    {
      this.mAttachCount += 1;
    }
    
    public void clear()
    {
      this.mScrap.clear();
    }
    
    void detach()
    {
      this.mAttachCount -= 1;
    }
    
    public RecyclerView.ViewHolder getRecycledView(int paramInt)
    {
      ArrayList localArrayList = (ArrayList)this.mScrap.get(paramInt);
      if ((localArrayList == null) || (localArrayList.isEmpty())) {
        return null;
      }
      paramInt = localArrayList.size() - 1;
      RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)localArrayList.get(paramInt);
      localArrayList.remove(paramInt);
      return localViewHolder;
    }
    
    void onAdapterChanged(RecyclerView.Adapter paramAdapter1, RecyclerView.Adapter paramAdapter2, boolean paramBoolean)
    {
      if (paramAdapter1 != null) {
        detach();
      }
      if ((!paramBoolean) && (this.mAttachCount == 0)) {
        clear();
      }
      if (paramAdapter2 != null) {
        attach(paramAdapter2);
      }
    }
    
    public void putRecycledView(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getItemViewType();
      ArrayList localArrayList = getScrapHeapForType(i);
      if (this.mMaxScrap.get(i) <= localArrayList.size()) {
        return;
      }
      paramViewHolder.resetInternal();
      localArrayList.add(paramViewHolder);
    }
    
    public void setMaxRecycledViews(int paramInt1, int paramInt2)
    {
      this.mMaxScrap.put(paramInt1, paramInt2);
      ArrayList localArrayList = (ArrayList)this.mScrap.get(paramInt1);
      if (localArrayList != null) {
        while (localArrayList.size() > paramInt2) {
          localArrayList.remove(localArrayList.size() - 1);
        }
      }
    }
    
    int size()
    {
      int j = 0;
      int i = 0;
      while (i < this.mScrap.size())
      {
        ArrayList localArrayList = (ArrayList)this.mScrap.valueAt(i);
        int k = j;
        if (localArrayList != null) {
          k = j + localArrayList.size();
        }
        i += 1;
        j = k;
      }
      return j;
    }
  }
  
  public final class Recycler
  {
    private static final int DEFAULT_CACHE_SIZE = 2;
    boolean accessibilityDelegateCheckFailed = false;
    Field mAccessibilityDelegateField;
    final ArrayList<RecyclerView.ViewHolder> mAttachedScrap = new ArrayList();
    final ArrayList<RecyclerView.ViewHolder> mCachedViews = new ArrayList();
    private ArrayList<RecyclerView.ViewHolder> mChangedScrap = null;
    private RecyclerView.RecycledViewPool mRecyclerPool;
    private final List<RecyclerView.ViewHolder> mUnmodifiableAttachedScrap = Collections.unmodifiableList(this.mAttachedScrap);
    private RecyclerView.ViewCacheExtension mViewCacheExtension;
    private int mViewCacheMax = 2;
    
    public Recycler() {}
    
    private void attachAccessibilityDelegate(View paramView)
    {
      if (RecyclerView.this.isAccessibilityEnabled())
      {
        if (paramView.getImportantForAccessibility() == 0) {
          paramView.setImportantForAccessibility(1);
        }
        if (!hasAccessibilityDelegate(paramView)) {
          paramView.setAccessibilityDelegate(RecyclerView.-get1(RecyclerView.this).getItemDelegate());
        }
      }
    }
    
    private void invalidateDisplayListInt(ViewGroup paramViewGroup, boolean paramBoolean)
    {
      int i = paramViewGroup.getChildCount() - 1;
      while (i >= 0)
      {
        View localView = paramViewGroup.getChildAt(i);
        if ((localView instanceof ViewGroup)) {
          invalidateDisplayListInt((ViewGroup)localView, true);
        }
        i -= 1;
      }
      if (!paramBoolean) {
        return;
      }
      if (paramViewGroup.getVisibility() == 4)
      {
        paramViewGroup.setVisibility(0);
        paramViewGroup.setVisibility(4);
        return;
      }
      i = paramViewGroup.getVisibility();
      paramViewGroup.setVisibility(4);
      paramViewGroup.setVisibility(i);
    }
    
    private void invalidateDisplayListInt(RecyclerView.ViewHolder paramViewHolder)
    {
      if ((paramViewHolder.itemView instanceof ViewGroup)) {
        invalidateDisplayListInt((ViewGroup)paramViewHolder.itemView, false);
      }
    }
    
    void addViewHolderToRecycledViewPool(RecyclerView.ViewHolder paramViewHolder)
    {
      paramViewHolder.itemView.setAccessibilityDelegate(null);
      dispatchViewRecycled(paramViewHolder);
      paramViewHolder.mOwnerRecyclerView = null;
      getRecycledViewPool().putRecycledView(paramViewHolder);
    }
    
    public void bindViewToPosition(View paramView, int paramInt)
    {
      boolean bool = true;
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      if (localViewHolder == null) {
        throw new IllegalArgumentException("The view does not have a ViewHolder. You cannot pass arbitrary views to this method, they should be created by the Adapter");
      }
      int i = RecyclerView.this.mAdapterHelper.findPositionOffset(paramInt);
      if ((i < 0) || (i >= RecyclerView.-get2(RecyclerView.this).getItemCount())) {
        throw new IndexOutOfBoundsException("Inconsistency detected. Invalid item position " + paramInt + "(offset:" + i + ")." + "state:" + RecyclerView.this.mState.getItemCount());
      }
      localViewHolder.mOwnerRecyclerView = RecyclerView.this;
      RecyclerView.-get2(RecyclerView.this).bindViewHolder(localViewHolder, i);
      attachAccessibilityDelegate(paramView);
      if (RecyclerView.this.mState.isPreLayout()) {
        localViewHolder.mPreLayoutPosition = paramInt;
      }
      paramView = localViewHolder.itemView.getLayoutParams();
      if (paramView == null)
      {
        paramView = (RecyclerView.LayoutParams)RecyclerView.this.generateDefaultLayoutParams();
        localViewHolder.itemView.setLayoutParams(paramView);
        paramView.mInsetsDirty = true;
        paramView.mViewHolder = localViewHolder;
        if (localViewHolder.itemView.getParent() != null) {
          break label264;
        }
      }
      for (;;)
      {
        paramView.mPendingInvalidate = bool;
        return;
        if (!RecyclerView.this.checkLayoutParams(paramView))
        {
          paramView = (RecyclerView.LayoutParams)RecyclerView.this.generateLayoutParams(paramView);
          localViewHolder.itemView.setLayoutParams(paramView);
          break;
        }
        paramView = (RecyclerView.LayoutParams)paramView;
        break;
        label264:
        bool = false;
      }
    }
    
    public void clear()
    {
      this.mAttachedScrap.clear();
      recycleAndClearCachedViews();
    }
    
    void clearOldPositions()
    {
      int j = this.mCachedViews.size();
      int i = 0;
      while (i < j)
      {
        ((RecyclerView.ViewHolder)this.mCachedViews.get(i)).clearOldPosition();
        i += 1;
      }
      j = this.mAttachedScrap.size();
      i = 0;
      while (i < j)
      {
        ((RecyclerView.ViewHolder)this.mAttachedScrap.get(i)).clearOldPosition();
        i += 1;
      }
      if (this.mChangedScrap != null)
      {
        j = this.mChangedScrap.size();
        i = 0;
        while (i < j)
        {
          ((RecyclerView.ViewHolder)this.mChangedScrap.get(i)).clearOldPosition();
          i += 1;
        }
      }
    }
    
    void clearScrap()
    {
      this.mAttachedScrap.clear();
    }
    
    public int convertPreLayoutPositionToPostLayout(int paramInt)
    {
      if ((paramInt < 0) || (paramInt >= RecyclerView.this.mState.getItemCount())) {
        throw new IndexOutOfBoundsException("invalid position " + paramInt + ". State " + "item count is " + RecyclerView.this.mState.getItemCount());
      }
      if (!RecyclerView.this.mState.isPreLayout()) {
        return paramInt;
      }
      return RecyclerView.this.mAdapterHelper.findPositionOffset(paramInt);
    }
    
    void dispatchViewRecycled(RecyclerView.ViewHolder paramViewHolder)
    {
      if (RecyclerView.-get12(RecyclerView.this) != null) {
        RecyclerView.-get12(RecyclerView.this).onViewRecycled(paramViewHolder);
      }
      if (RecyclerView.-get2(RecyclerView.this) != null) {
        RecyclerView.-get2(RecyclerView.this).onViewRecycled(paramViewHolder);
      }
      if (RecyclerView.this.mState != null) {
        RecyclerView.this.mState.onViewRecycled(paramViewHolder);
      }
    }
    
    RecyclerView.ViewHolder getChangedScrapViewForPosition(int paramInt)
    {
      int j;
      if (this.mChangedScrap != null)
      {
        j = this.mChangedScrap.size();
        if (j != 0) {}
      }
      else
      {
        return null;
      }
      int i = 0;
      RecyclerView.ViewHolder localViewHolder;
      while (i < j)
      {
        localViewHolder = (RecyclerView.ViewHolder)this.mChangedScrap.get(i);
        if ((!localViewHolder.wasReturnedFromScrap()) && (localViewHolder.getLayoutPosition() == paramInt))
        {
          localViewHolder.addFlags(32);
          return localViewHolder;
        }
        i += 1;
      }
      if (RecyclerView.-get2(RecyclerView.this).hasStableIds())
      {
        paramInt = RecyclerView.this.mAdapterHelper.findPositionOffset(paramInt);
        if ((paramInt > 0) && (paramInt < RecyclerView.-get2(RecyclerView.this).getItemCount()))
        {
          long l = RecyclerView.-get2(RecyclerView.this).getItemId(paramInt);
          paramInt = 0;
          while (paramInt < j)
          {
            localViewHolder = (RecyclerView.ViewHolder)this.mChangedScrap.get(paramInt);
            if ((!localViewHolder.wasReturnedFromScrap()) && (localViewHolder.getItemId() == l))
            {
              localViewHolder.addFlags(32);
              return localViewHolder;
            }
            paramInt += 1;
          }
        }
      }
      return null;
    }
    
    RecyclerView.RecycledViewPool getRecycledViewPool()
    {
      if (this.mRecyclerPool == null) {
        this.mRecyclerPool = new RecyclerView.RecycledViewPool();
      }
      return this.mRecyclerPool;
    }
    
    int getScrapCount()
    {
      return this.mAttachedScrap.size();
    }
    
    public List<RecyclerView.ViewHolder> getScrapList()
    {
      return this.mUnmodifiableAttachedScrap;
    }
    
    View getScrapViewAt(int paramInt)
    {
      return ((RecyclerView.ViewHolder)this.mAttachedScrap.get(paramInt)).itemView;
    }
    
    RecyclerView.ViewHolder getScrapViewForId(long paramLong, int paramInt, boolean paramBoolean)
    {
      int i = this.mAttachedScrap.size() - 1;
      RecyclerView.ViewHolder localViewHolder;
      if (i >= 0)
      {
        localViewHolder = (RecyclerView.ViewHolder)this.mAttachedScrap.get(i);
        if ((localViewHolder.getItemId() != paramLong) || (localViewHolder.wasReturnedFromScrap())) {}
        for (;;)
        {
          i -= 1;
          break;
          if (paramInt == localViewHolder.getItemViewType())
          {
            localViewHolder.addFlags(32);
            if ((localViewHolder.isRemoved()) && (!RecyclerView.this.mState.isPreLayout())) {
              localViewHolder.setFlags(2, 14);
            }
            return localViewHolder;
          }
          if (!paramBoolean)
          {
            this.mAttachedScrap.remove(i);
            RecyclerView.this.removeDetachedView(localViewHolder.itemView, false);
            quickRecycleScrapView(localViewHolder.itemView);
          }
        }
      }
      i = this.mCachedViews.size() - 1;
      while (i >= 0)
      {
        localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(i);
        if (localViewHolder.getItemId() == paramLong)
        {
          if (paramInt == localViewHolder.getItemViewType())
          {
            if (!paramBoolean) {
              this.mCachedViews.remove(i);
            }
            return localViewHolder;
          }
          if (!paramBoolean) {
            recycleCachedViewAt(i);
          }
        }
        i -= 1;
      }
      return null;
    }
    
    RecyclerView.ViewHolder getScrapViewForPosition(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      int j = this.mAttachedScrap.size();
      int i = 0;
      Object localObject;
      if (i < j)
      {
        localObject = (RecyclerView.ViewHolder)this.mAttachedScrap.get(i);
        if ((((RecyclerView.ViewHolder)localObject).wasReturnedFromScrap()) || (((RecyclerView.ViewHolder)localObject).getLayoutPosition() != paramInt1) || (((RecyclerView.ViewHolder)localObject).isInvalid())) {}
        while ((!RecyclerView.State.-get1(RecyclerView.this.mState)) && (((RecyclerView.ViewHolder)localObject).isRemoved()))
        {
          i += 1;
          break;
        }
        if ((paramInt2 != -1) && (((RecyclerView.ViewHolder)localObject).getItemViewType() != paramInt2)) {
          Log.e("RecyclerView", "Scrap view for position " + paramInt1 + " isn't dirty but has" + " wrong view type! (found " + ((RecyclerView.ViewHolder)localObject).getItemViewType() + " but expected " + paramInt2 + ")");
        }
      }
      else
      {
        if (!paramBoolean)
        {
          localObject = RecyclerView.this.mChildHelper.findHiddenNonRemovedView(paramInt1, paramInt2);
          if (localObject != null) {
            RecyclerView.this.mItemAnimator.endAnimation(RecyclerView.this.getChildViewHolder((View)localObject));
          }
        }
        i = this.mCachedViews.size();
        paramInt2 = 0;
      }
      while (paramInt2 < i)
      {
        localObject = (RecyclerView.ViewHolder)this.mCachedViews.get(paramInt2);
        if ((!((RecyclerView.ViewHolder)localObject).isInvalid()) && (((RecyclerView.ViewHolder)localObject).getLayoutPosition() == paramInt1))
        {
          if (!paramBoolean) {
            this.mCachedViews.remove(paramInt2);
          }
          return (RecyclerView.ViewHolder)localObject;
          ((RecyclerView.ViewHolder)localObject).addFlags(32);
          return (RecyclerView.ViewHolder)localObject;
        }
        paramInt2 += 1;
      }
      return null;
    }
    
    public View getViewForPosition(int paramInt)
    {
      return getViewForPosition(paramInt, false);
    }
    
    View getViewForPosition(int paramInt, boolean paramBoolean)
    {
      if ((paramInt < 0) || (paramInt >= RecyclerView.this.mState.getItemCount())) {
        throw new IndexOutOfBoundsException("Invalid item position " + paramInt + "(" + paramInt + "). Item count:" + RecyclerView.this.mState.getItemCount());
      }
      int j = 0;
      Object localObject2 = null;
      int i;
      if (RecyclerView.this.mState.isPreLayout())
      {
        localObject2 = getChangedScrapViewForPosition(paramInt);
        if (localObject2 != null) {
          j = 1;
        }
      }
      else
      {
        i = j;
        localObject1 = localObject2;
        if (localObject2 == null)
        {
          localObject2 = getScrapViewForPosition(paramInt, -1, paramBoolean);
          i = j;
          localObject1 = localObject2;
          if (localObject2 != null)
          {
            if (validateViewHolderForOffsetPosition((RecyclerView.ViewHolder)localObject2)) {
              break label326;
            }
            if (!paramBoolean)
            {
              ((RecyclerView.ViewHolder)localObject2).addFlags(4);
              if (!((RecyclerView.ViewHolder)localObject2).isScrap()) {
                break label310;
              }
              RecyclerView.this.removeDetachedView(((RecyclerView.ViewHolder)localObject2).itemView, false);
              ((RecyclerView.ViewHolder)localObject2).unScrap();
              label187:
              recycleViewHolderInternal((RecyclerView.ViewHolder)localObject2);
            }
            localObject1 = null;
            i = j;
          }
        }
      }
      for (;;)
      {
        k = i;
        localObject2 = localObject1;
        if (localObject1 != null) {
          break label597;
        }
        k = RecyclerView.this.mAdapterHelper.findPositionOffset(paramInt);
        if ((k >= 0) && (k < RecyclerView.-get2(RecyclerView.this).getItemCount())) {
          break label335;
        }
        throw new IndexOutOfBoundsException("Inconsistency detected. Invalid item position " + paramInt + "(offset:" + k + ")." + "state:" + RecyclerView.this.mState.getItemCount());
        j = 0;
        break;
        label310:
        if (!((RecyclerView.ViewHolder)localObject2).wasReturnedFromScrap()) {
          break label187;
        }
        ((RecyclerView.ViewHolder)localObject2).clearReturnedFromScrapFlag();
        break label187;
        label326:
        i = 1;
        localObject1 = localObject2;
      }
      label335:
      int m = RecyclerView.-get2(RecyclerView.this).getItemViewType(k);
      j = i;
      localObject2 = localObject1;
      if (RecyclerView.-get2(RecyclerView.this).hasStableIds())
      {
        localObject1 = getScrapViewForId(RecyclerView.-get2(RecyclerView.this).getItemId(k), m, paramBoolean);
        j = i;
        localObject2 = localObject1;
        if (localObject1 != null)
        {
          ((RecyclerView.ViewHolder)localObject1).mPosition = k;
          j = 1;
          localObject2 = localObject1;
        }
      }
      Object localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = localObject2;
        if (this.mViewCacheExtension != null)
        {
          localObject3 = this.mViewCacheExtension.getViewForPositionAndType(this, paramInt, m);
          localObject1 = localObject2;
          if (localObject3 != null)
          {
            localObject2 = RecyclerView.this.getChildViewHolder((View)localObject3);
            if (localObject2 == null) {
              throw new IllegalArgumentException("getViewForPositionAndType returned a view which does not have a ViewHolder");
            }
            localObject1 = localObject2;
            if (((RecyclerView.ViewHolder)localObject2).shouldIgnore()) {
              throw new IllegalArgumentException("getViewForPositionAndType returned a view that is ignored. You must call stopIgnoring before returning this view.");
            }
          }
        }
      }
      Object localObject3 = localObject1;
      if (localObject1 == null)
      {
        localObject1 = getRecycledViewPool().getRecycledView(m);
        localObject3 = localObject1;
        if (localObject1 != null)
        {
          ((RecyclerView.ViewHolder)localObject1).resetInternal();
          localObject3 = localObject1;
          if (RecyclerView.-get0())
          {
            invalidateDisplayListInt((RecyclerView.ViewHolder)localObject1);
            localObject3 = localObject1;
          }
        }
      }
      int k = j;
      localObject2 = localObject3;
      if (localObject3 == null)
      {
        localObject2 = RecyclerView.-get2(RecyclerView.this).createViewHolder(RecyclerView.this, m);
        k = j;
      }
      label597:
      paramBoolean = false;
      if ((RecyclerView.this.mState.isPreLayout()) && (((RecyclerView.ViewHolder)localObject2).isBound()))
      {
        ((RecyclerView.ViewHolder)localObject2).mPreLayoutPosition = paramInt;
        localObject1 = ((RecyclerView.ViewHolder)localObject2).itemView.getLayoutParams();
        if (localObject1 != null) {
          break label785;
        }
        localObject1 = (RecyclerView.LayoutParams)RecyclerView.this.generateDefaultLayoutParams();
        ((RecyclerView.ViewHolder)localObject2).itemView.setLayoutParams((ViewGroup.LayoutParams)localObject1);
        label663:
        ((RecyclerView.LayoutParams)localObject1).mViewHolder = ((RecyclerView.ViewHolder)localObject2);
        if (k == 0) {
          break label834;
        }
      }
      for (;;)
      {
        ((RecyclerView.LayoutParams)localObject1).mPendingInvalidate = paramBoolean;
        return ((RecyclerView.ViewHolder)localObject2).itemView;
        if ((((RecyclerView.ViewHolder)localObject2).isBound()) && (!((RecyclerView.ViewHolder)localObject2).needsUpdate()) && (!((RecyclerView.ViewHolder)localObject2).isInvalid())) {
          break;
        }
        i = RecyclerView.this.mAdapterHelper.findPositionOffset(paramInt);
        ((RecyclerView.ViewHolder)localObject2).mOwnerRecyclerView = RecyclerView.this;
        RecyclerView.-get2(RecyclerView.this).bindViewHolder((RecyclerView.ViewHolder)localObject2, i);
        attachAccessibilityDelegate(((RecyclerView.ViewHolder)localObject2).itemView);
        boolean bool = true;
        paramBoolean = bool;
        if (!RecyclerView.this.mState.isPreLayout()) {
          break;
        }
        ((RecyclerView.ViewHolder)localObject2).mPreLayoutPosition = paramInt;
        paramBoolean = bool;
        break;
        label785:
        if (!RecyclerView.this.checkLayoutParams((ViewGroup.LayoutParams)localObject1))
        {
          localObject1 = (RecyclerView.LayoutParams)RecyclerView.this.generateLayoutParams((ViewGroup.LayoutParams)localObject1);
          ((RecyclerView.ViewHolder)localObject2).itemView.setLayoutParams((ViewGroup.LayoutParams)localObject1);
          break label663;
        }
        localObject1 = (RecyclerView.LayoutParams)localObject1;
        break label663;
        label834:
        paramBoolean = false;
      }
    }
    
    /* Error */
    public boolean hasAccessibilityDelegate(View paramView)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 60	com/oneplus/lib/widget/recyclerview/RecyclerView$Recycler:accessibilityDelegateCheckFailed	Z
      //   4: ifeq +5 -> 9
      //   7: iconst_0
      //   8: ireturn
      //   9: aload_0
      //   10: getfield 462	com/oneplus/lib/widget/recyclerview/RecyclerView$Recycler:mAccessibilityDelegateField	Ljava/lang/reflect/Field;
      //   13: ifnonnull +23 -> 36
      //   16: aload_0
      //   17: ldc 68
      //   19: ldc_w 464
      //   22: invokevirtual 470	java/lang/Class:getDeclaredField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
      //   25: putfield 462	com/oneplus/lib/widget/recyclerview/RecyclerView$Recycler:mAccessibilityDelegateField	Ljava/lang/reflect/Field;
      //   28: aload_0
      //   29: getfield 462	com/oneplus/lib/widget/recyclerview/RecyclerView$Recycler:mAccessibilityDelegateField	Ljava/lang/reflect/Field;
      //   32: iconst_1
      //   33: invokevirtual 476	java/lang/reflect/Field:setAccessible	(Z)V
      //   36: aload_0
      //   37: getfield 462	com/oneplus/lib/widget/recyclerview/RecyclerView$Recycler:mAccessibilityDelegateField	Ljava/lang/reflect/Field;
      //   40: aload_1
      //   41: invokevirtual 479	java/lang/reflect/Field:get	(Ljava/lang/Object;)Ljava/lang/Object;
      //   44: astore_1
      //   45: aload_1
      //   46: ifnull +13 -> 59
      //   49: iconst_1
      //   50: ireturn
      //   51: astore_1
      //   52: aload_0
      //   53: iconst_1
      //   54: putfield 60	com/oneplus/lib/widget/recyclerview/RecyclerView$Recycler:accessibilityDelegateCheckFailed	Z
      //   57: iconst_0
      //   58: ireturn
      //   59: iconst_0
      //   60: ireturn
      //   61: astore_1
      //   62: aload_0
      //   63: iconst_1
      //   64: putfield 60	com/oneplus/lib/widget/recyclerview/RecyclerView$Recycler:accessibilityDelegateCheckFailed	Z
      //   67: iconst_0
      //   68: ireturn
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	69	0	this	Recycler
      //   0	69	1	paramView	View
      // Exception table:
      //   from	to	target	type
      //   16	36	51	java/lang/Throwable
      //   36	45	61	java/lang/Throwable
    }
    
    void markItemDecorInsetsDirty()
    {
      int j = this.mCachedViews.size();
      int i = 0;
      while (i < j)
      {
        RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)((RecyclerView.ViewHolder)this.mCachedViews.get(i)).itemView.getLayoutParams();
        if (localLayoutParams != null) {
          localLayoutParams.mInsetsDirty = true;
        }
        i += 1;
      }
    }
    
    void markKnownViewsInvalid()
    {
      int j;
      int i;
      if ((RecyclerView.-get2(RecyclerView.this) != null) && (RecyclerView.-get2(RecyclerView.this).hasStableIds()))
      {
        j = this.mCachedViews.size();
        i = 0;
      }
      while (i < j)
      {
        RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(i);
        if (localViewHolder != null)
        {
          localViewHolder.addFlags(6);
          localViewHolder.addChangePayload(null);
        }
        i += 1;
        continue;
        recycleAndClearCachedViews();
      }
    }
    
    void offsetPositionRecordsForInsert(int paramInt1, int paramInt2)
    {
      int j = this.mCachedViews.size();
      int i = 0;
      while (i < j)
      {
        RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(i);
        if ((localViewHolder != null) && (localViewHolder.getLayoutPosition() >= paramInt1)) {
          localViewHolder.offsetPosition(paramInt2, true);
        }
        i += 1;
      }
    }
    
    void offsetPositionRecordsForMove(int paramInt1, int paramInt2)
    {
      int k;
      int i;
      int j;
      int m;
      label25:
      RecyclerView.ViewHolder localViewHolder;
      if (paramInt1 < paramInt2)
      {
        k = paramInt1;
        i = paramInt2;
        j = -1;
        int n = this.mCachedViews.size();
        m = 0;
        if (m >= n) {
          return;
        }
        localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(m);
        if ((localViewHolder != null) && (localViewHolder.mPosition >= k)) {
          break label81;
        }
      }
      for (;;)
      {
        m += 1;
        break label25;
        k = paramInt2;
        i = paramInt1;
        j = 1;
        break;
        label81:
        if (localViewHolder.mPosition <= i) {
          if (localViewHolder.mPosition == paramInt1) {
            localViewHolder.offsetPosition(paramInt2 - paramInt1, false);
          } else {
            localViewHolder.offsetPosition(j, false);
          }
        }
      }
    }
    
    void offsetPositionRecordsForRemove(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      int i = this.mCachedViews.size() - 1;
      if (i >= 0)
      {
        RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(i);
        if (localViewHolder != null)
        {
          if (localViewHolder.getLayoutPosition() < paramInt1 + paramInt2) {
            break label63;
          }
          localViewHolder.offsetPosition(-paramInt2, paramBoolean);
        }
        for (;;)
        {
          i -= 1;
          break;
          label63:
          if (localViewHolder.getLayoutPosition() >= paramInt1)
          {
            localViewHolder.addFlags(8);
            recycleCachedViewAt(i);
          }
        }
      }
    }
    
    void onAdapterChanged(RecyclerView.Adapter paramAdapter1, RecyclerView.Adapter paramAdapter2, boolean paramBoolean)
    {
      clear();
      getRecycledViewPool().onAdapterChanged(paramAdapter1, paramAdapter2, paramBoolean);
    }
    
    void quickRecycleScrapView(View paramView)
    {
      paramView = RecyclerView.getChildViewHolderInt(paramView);
      RecyclerView.ViewHolder.-set0(paramView, null);
      paramView.clearReturnedFromScrapFlag();
      recycleViewHolderInternal(paramView);
    }
    
    void recycleAndClearCachedViews()
    {
      int i = this.mCachedViews.size() - 1;
      while (i >= 0)
      {
        recycleCachedViewAt(i);
        i -= 1;
      }
      this.mCachedViews.clear();
    }
    
    void recycleCachedViewAt(int paramInt)
    {
      addViewHolderToRecycledViewPool((RecyclerView.ViewHolder)this.mCachedViews.get(paramInt));
      this.mCachedViews.remove(paramInt);
    }
    
    public void recycleView(View paramView)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      if (localViewHolder.isTmpDetached()) {
        RecyclerView.this.removeDetachedView(paramView, false);
      }
      if (localViewHolder.isScrap()) {
        localViewHolder.unScrap();
      }
      for (;;)
      {
        recycleViewHolderInternal(localViewHolder);
        return;
        if (localViewHolder.wasReturnedFromScrap()) {
          localViewHolder.clearReturnedFromScrapFlag();
        }
      }
    }
    
    void recycleViewHolderInternal(RecyclerView.ViewHolder paramViewHolder)
    {
      boolean bool1 = false;
      if ((paramViewHolder.isScrap()) || (paramViewHolder.itemView.getParent() != null))
      {
        StringBuilder localStringBuilder = new StringBuilder().append("Scrapped or attached views may not be recycled. isScrap:").append(paramViewHolder.isScrap()).append(" isAttached:");
        if (paramViewHolder.itemView.getParent() != null) {
          bool1 = true;
        }
        throw new IllegalArgumentException(bool1);
      }
      if (paramViewHolder.isTmpDetached()) {
        throw new IllegalArgumentException("Tmp detached view should be removed from RecyclerView before it can be recycled: " + paramViewHolder);
      }
      if (paramViewHolder.shouldIgnore()) {
        throw new IllegalArgumentException("Trying to recycle an ignored view holder. You should first call stopIgnoringView(view) before calling recycle.");
      }
      boolean bool2 = RecyclerView.ViewHolder.-wrap0(paramViewHolder);
      if ((RecyclerView.-get2(RecyclerView.this) != null) && (bool2))
      {
        bool1 = RecyclerView.-get2(RecyclerView.this).onFailedToRecycleView(paramViewHolder);
        int j = 0;
        int n = 0;
        int m = 0;
        int k;
        if (!bool1)
        {
          k = m;
          if (!paramViewHolder.isRecyclable()) {}
        }
        else
        {
          int i = n;
          if (!paramViewHolder.hasAnyOfTheFlags(78))
          {
            j = this.mCachedViews.size();
            if ((j == this.mViewCacheMax) && (j > 0)) {
              recycleCachedViewAt(0);
            }
            i = n;
            if (j < this.mViewCacheMax)
            {
              this.mCachedViews.add(paramViewHolder);
              i = 1;
            }
          }
          j = i;
          k = m;
          if (i == 0)
          {
            addViewHolderToRecycledViewPool(paramViewHolder);
            k = 1;
            j = i;
          }
        }
        RecyclerView.this.mState.onViewRecycled(paramViewHolder);
        if ((j == 0) && (k == 0)) {
          break label296;
        }
      }
      label296:
      while (!bool2)
      {
        return;
        bool1 = false;
        break;
      }
      paramViewHolder.mOwnerRecyclerView = null;
    }
    
    void recycleViewInternal(View paramView)
    {
      recycleViewHolderInternal(RecyclerView.getChildViewHolderInt(paramView));
    }
    
    void scrapView(View paramView)
    {
      paramView = RecyclerView.getChildViewHolderInt(paramView);
      paramView.setScrapContainer(this);
      if ((paramView.isChanged()) && (RecyclerView.-wrap2(RecyclerView.this)))
      {
        if (this.mChangedScrap == null) {
          this.mChangedScrap = new ArrayList();
        }
        this.mChangedScrap.add(paramView);
        return;
      }
      if ((!paramView.isInvalid()) || (paramView.isRemoved())) {}
      while (RecyclerView.-get2(RecyclerView.this).hasStableIds())
      {
        this.mAttachedScrap.add(paramView);
        return;
      }
      throw new IllegalArgumentException("Called scrap view with an invalid view. Invalid views cannot be reused from scrap, they should rebound from recycler pool.");
    }
    
    void setAdapterPositionsAsUnknown()
    {
      int j = this.mCachedViews.size();
      int i = 0;
      while (i < j)
      {
        RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(i);
        if (localViewHolder != null) {
          localViewHolder.addFlags(512);
        }
        i += 1;
      }
    }
    
    void setRecycledViewPool(RecyclerView.RecycledViewPool paramRecycledViewPool)
    {
      if (this.mRecyclerPool != null) {
        this.mRecyclerPool.detach();
      }
      this.mRecyclerPool = paramRecycledViewPool;
      if (paramRecycledViewPool != null) {
        this.mRecyclerPool.attach(RecyclerView.this.getAdapter());
      }
    }
    
    void setViewCacheExtension(RecyclerView.ViewCacheExtension paramViewCacheExtension)
    {
      this.mViewCacheExtension = paramViewCacheExtension;
    }
    
    public void setViewCacheSize(int paramInt)
    {
      this.mViewCacheMax = paramInt;
      int i = this.mCachedViews.size() - 1;
      while ((i >= 0) && (this.mCachedViews.size() > paramInt))
      {
        recycleCachedViewAt(i);
        i -= 1;
      }
    }
    
    void unscrapView(RecyclerView.ViewHolder paramViewHolder)
    {
      if ((!paramViewHolder.isChanged()) || (!RecyclerView.-wrap2(RecyclerView.this)) || (this.mChangedScrap == null)) {
        this.mAttachedScrap.remove(paramViewHolder);
      }
      for (;;)
      {
        RecyclerView.ViewHolder.-set0(paramViewHolder, null);
        paramViewHolder.clearReturnedFromScrapFlag();
        return;
        this.mChangedScrap.remove(paramViewHolder);
      }
    }
    
    boolean validateViewHolderForOffsetPosition(RecyclerView.ViewHolder paramViewHolder)
    {
      if (paramViewHolder.isRemoved()) {
        return true;
      }
      if ((paramViewHolder.mPosition < 0) || (paramViewHolder.mPosition >= RecyclerView.-get2(RecyclerView.this).getItemCount())) {
        throw new IndexOutOfBoundsException("Inconsistency detected. Invalid view holder adapter position" + paramViewHolder);
      }
      if ((!RecyclerView.this.mState.isPreLayout()) && (RecyclerView.-get2(RecyclerView.this).getItemViewType(paramViewHolder.mPosition) != paramViewHolder.getItemViewType())) {
        return false;
      }
      if (RecyclerView.-get2(RecyclerView.this).hasStableIds()) {
        return paramViewHolder.getItemId() == RecyclerView.-get2(RecyclerView.this).getItemId(paramViewHolder.mPosition);
      }
      return true;
    }
    
    void viewRangeUpdate(int paramInt1, int paramInt2)
    {
      int i = this.mCachedViews.size() - 1;
      if (i >= 0)
      {
        RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(i);
        if (localViewHolder == null) {}
        for (;;)
        {
          i -= 1;
          break;
          int j = localViewHolder.getLayoutPosition();
          if ((j >= paramInt1) && (j < paramInt1 + paramInt2))
          {
            localViewHolder.addFlags(2);
            recycleCachedViewAt(i);
          }
        }
      }
    }
  }
  
  public static abstract interface RecyclerListener
  {
    public abstract void onViewRecycled(RecyclerView.ViewHolder paramViewHolder);
  }
  
  private class RecyclerViewDataObserver
    extends RecyclerView.AdapterDataObserver
  {
    private RecyclerViewDataObserver() {}
    
    public void onChanged()
    {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      if (RecyclerView.-get2(RecyclerView.this).hasStableIds())
      {
        RecyclerView.State.-set5(RecyclerView.this.mState, true);
        RecyclerView.-wrap14(RecyclerView.this);
      }
      for (;;)
      {
        if (!RecyclerView.this.mAdapterHelper.hasPendingUpdates()) {
          RecyclerView.this.requestLayout();
        }
        return;
        RecyclerView.State.-set5(RecyclerView.this.mState, true);
        RecyclerView.-wrap14(RecyclerView.this);
      }
    }
    
    public void onItemRangeChanged(int paramInt1, int paramInt2, Object paramObject)
    {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      if (RecyclerView.this.mAdapterHelper.onItemRangeChanged(paramInt1, paramInt2, paramObject)) {
        triggerUpdateProcessor();
      }
    }
    
    public void onItemRangeInserted(int paramInt1, int paramInt2)
    {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      if (RecyclerView.this.mAdapterHelper.onItemRangeInserted(paramInt1, paramInt2)) {
        triggerUpdateProcessor();
      }
    }
    
    public void onItemRangeMoved(int paramInt1, int paramInt2, int paramInt3)
    {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      if (RecyclerView.this.mAdapterHelper.onItemRangeMoved(paramInt1, paramInt2, paramInt3)) {
        triggerUpdateProcessor();
      }
    }
    
    public void onItemRangeRemoved(int paramInt1, int paramInt2)
    {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      if (RecyclerView.this.mAdapterHelper.onItemRangeRemoved(paramInt1, paramInt2)) {
        triggerUpdateProcessor();
      }
    }
    
    void triggerUpdateProcessor()
    {
      if ((RecyclerView.-get11(RecyclerView.this)) && (RecyclerView.-get6(RecyclerView.this)) && (RecyclerView.-get7(RecyclerView.this)))
      {
        RecyclerView.this.postOnAnimation(RecyclerView.-get13(RecyclerView.this));
        return;
      }
      RecyclerView.-set0(RecyclerView.this, true);
      RecyclerView.this.requestLayout();
    }
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public RecyclerView.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new RecyclerView.SavedState(paramAnonymousParcel);
      }
      
      public RecyclerView.SavedState[] newArray(int paramAnonymousInt)
      {
        return new RecyclerView.SavedState[paramAnonymousInt];
      }
    };
    Parcelable mLayoutState;
    
    SavedState(Parcel paramParcel)
    {
      super();
      this.mLayoutState = paramParcel.readParcelable(RecyclerView.LayoutManager.class.getClassLoader());
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    private void copyFrom(SavedState paramSavedState)
    {
      this.mLayoutState = paramSavedState.mLayoutState;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeParcelable(this.mLayoutState, 0);
    }
  }
  
  public static class SimpleOnItemTouchListener
    implements RecyclerView.OnItemTouchListener
  {
    public boolean onInterceptTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent)
    {
      return false;
    }
    
    public void onRequestDisallowInterceptTouchEvent(boolean paramBoolean) {}
    
    public void onTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent) {}
  }
  
  public static abstract class SmoothScroller
  {
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean mPendingInitialRun;
    private RecyclerView mRecyclerView;
    private final Action mRecyclingAction = new Action(0, 0);
    private boolean mRunning;
    private int mTargetPosition = -1;
    private View mTargetView;
    
    private void onAnimation(int paramInt1, int paramInt2)
    {
      RecyclerView localRecyclerView = this.mRecyclerView;
      if ((!this.mRunning) || (this.mTargetPosition == -1))
      {
        stop();
        label25:
        this.mPendingInitialRun = false;
        if (this.mTargetView != null)
        {
          if (getChildPosition(this.mTargetView) != this.mTargetPosition) {
            break label154;
          }
          onTargetFound(this.mTargetView, localRecyclerView.mState, this.mRecyclingAction);
          Action.-wrap0(this.mRecyclingAction, localRecyclerView);
          stop();
        }
      }
      for (;;)
      {
        if (this.mRunning)
        {
          onSeekTargetStep(paramInt1, paramInt2, localRecyclerView.mState, this.mRecyclingAction);
          boolean bool = this.mRecyclingAction.hasJumpTarget();
          Action.-wrap0(this.mRecyclingAction, localRecyclerView);
          if (bool)
          {
            if (!this.mRunning) {
              break label170;
            }
            this.mPendingInitialRun = true;
            RecyclerView.-get14(localRecyclerView).postOnAnimation();
          }
        }
        return;
        if (localRecyclerView != null) {
          break label25;
        }
        break;
        label154:
        Log.e("RecyclerView", "Passed over target position while smooth scrolling.");
        this.mTargetView = null;
      }
      label170:
      stop();
    }
    
    public View findViewByPosition(int paramInt)
    {
      return RecyclerView.-get9(this.mRecyclerView).findViewByPosition(paramInt);
    }
    
    public int getChildCount()
    {
      return RecyclerView.-get9(this.mRecyclerView).getChildCount();
    }
    
    public int getChildPosition(View paramView)
    {
      return this.mRecyclerView.getChildLayoutPosition(paramView);
    }
    
    public RecyclerView.LayoutManager getLayoutManager()
    {
      return this.mLayoutManager;
    }
    
    public int getTargetPosition()
    {
      return this.mTargetPosition;
    }
    
    @Deprecated
    public void instantScrollToPosition(int paramInt)
    {
      this.mRecyclerView.scrollToPosition(paramInt);
    }
    
    public boolean isPendingInitialRun()
    {
      return this.mPendingInitialRun;
    }
    
    public boolean isRunning()
    {
      return this.mRunning;
    }
    
    protected void normalize(PointF paramPointF)
    {
      double d = Math.sqrt(paramPointF.x * paramPointF.x + paramPointF.y * paramPointF.y);
      paramPointF.x = ((float)(paramPointF.x / d));
      paramPointF.y = ((float)(paramPointF.y / d));
    }
    
    protected void onChildAttachedToWindow(View paramView)
    {
      if (getChildPosition(paramView) == getTargetPosition()) {
        this.mTargetView = paramView;
      }
    }
    
    protected abstract void onSeekTargetStep(int paramInt1, int paramInt2, RecyclerView.State paramState, Action paramAction);
    
    protected abstract void onStart();
    
    protected abstract void onStop();
    
    protected abstract void onTargetFound(View paramView, RecyclerView.State paramState, Action paramAction);
    
    public void setTargetPosition(int paramInt)
    {
      this.mTargetPosition = paramInt;
    }
    
    void start(RecyclerView paramRecyclerView, RecyclerView.LayoutManager paramLayoutManager)
    {
      this.mRecyclerView = paramRecyclerView;
      this.mLayoutManager = paramLayoutManager;
      if (this.mTargetPosition == -1) {
        throw new IllegalArgumentException("Invalid target position");
      }
      RecyclerView.State.-set6(this.mRecyclerView.mState, this.mTargetPosition);
      this.mRunning = true;
      this.mPendingInitialRun = true;
      this.mTargetView = findViewByPosition(getTargetPosition());
      onStart();
      RecyclerView.-get14(this.mRecyclerView).postOnAnimation();
    }
    
    protected final void stop()
    {
      if (!this.mRunning) {
        return;
      }
      onStop();
      RecyclerView.State.-set6(this.mRecyclerView.mState, -1);
      this.mTargetView = null;
      this.mTargetPosition = -1;
      this.mPendingInitialRun = false;
      this.mRunning = false;
      RecyclerView.LayoutManager.-wrap0(this.mLayoutManager, this);
      this.mLayoutManager = null;
      this.mRecyclerView = null;
    }
    
    public static class Action
    {
      public static final int UNDEFINED_DURATION = Integer.MIN_VALUE;
      private boolean changed = false;
      private int consecutiveUpdates = 0;
      private int mDuration;
      private int mDx;
      private int mDy;
      private Interpolator mInterpolator;
      private int mJumpToPosition = -1;
      
      public Action(int paramInt1, int paramInt2)
      {
        this(paramInt1, paramInt2, Integer.MIN_VALUE, null);
      }
      
      public Action(int paramInt1, int paramInt2, int paramInt3)
      {
        this(paramInt1, paramInt2, paramInt3, null);
      }
      
      public Action(int paramInt1, int paramInt2, int paramInt3, Interpolator paramInterpolator)
      {
        this.mDx = paramInt1;
        this.mDy = paramInt2;
        this.mDuration = paramInt3;
        this.mInterpolator = paramInterpolator;
      }
      
      private void runIfNecessary(RecyclerView paramRecyclerView)
      {
        if (this.mJumpToPosition >= 0)
        {
          int i = this.mJumpToPosition;
          this.mJumpToPosition = -1;
          RecyclerView.-wrap11(paramRecyclerView, i);
          this.changed = false;
          return;
        }
        if (this.changed)
        {
          validate();
          if (this.mInterpolator == null) {
            if (this.mDuration == Integer.MIN_VALUE) {
              RecyclerView.-get14(paramRecyclerView).smoothScrollBy(this.mDx, this.mDy);
            }
          }
          for (;;)
          {
            this.consecutiveUpdates += 1;
            if (this.consecutiveUpdates > 10) {
              Log.e("RecyclerView", "Smooth Scroll action is being updated too frequently. Make sure you are not changing it unless necessary");
            }
            this.changed = false;
            return;
            RecyclerView.-get14(paramRecyclerView).smoothScrollBy(this.mDx, this.mDy, this.mDuration);
            continue;
            RecyclerView.-get14(paramRecyclerView).smoothScrollBy(this.mDx, this.mDy, this.mDuration, this.mInterpolator);
          }
        }
        this.consecutiveUpdates = 0;
      }
      
      private void validate()
      {
        if ((this.mInterpolator != null) && (this.mDuration < 1)) {
          throw new IllegalStateException("If you provide an interpolator, you must set a positive duration");
        }
        if (this.mDuration < 1) {
          throw new IllegalStateException("Scroll duration must be a positive number");
        }
      }
      
      public int getDuration()
      {
        return this.mDuration;
      }
      
      public int getDx()
      {
        return this.mDx;
      }
      
      public int getDy()
      {
        return this.mDy;
      }
      
      public Interpolator getInterpolator()
      {
        return this.mInterpolator;
      }
      
      boolean hasJumpTarget()
      {
        boolean bool = false;
        if (this.mJumpToPosition >= 0) {
          bool = true;
        }
        return bool;
      }
      
      public void jumpTo(int paramInt)
      {
        this.mJumpToPosition = paramInt;
      }
      
      public void setDuration(int paramInt)
      {
        this.changed = true;
        this.mDuration = paramInt;
      }
      
      public void setDx(int paramInt)
      {
        this.changed = true;
        this.mDx = paramInt;
      }
      
      public void setDy(int paramInt)
      {
        this.changed = true;
        this.mDy = paramInt;
      }
      
      public void setInterpolator(Interpolator paramInterpolator)
      {
        this.changed = true;
        this.mInterpolator = paramInterpolator;
      }
      
      public void update(int paramInt1, int paramInt2, int paramInt3, Interpolator paramInterpolator)
      {
        this.mDx = paramInt1;
        this.mDy = paramInt2;
        this.mDuration = paramInt3;
        this.mInterpolator = paramInterpolator;
        this.changed = true;
      }
    }
  }
  
  public static class State
  {
    private SparseArray<Object> mData;
    private int mDeletedInvisibleItemCountSincePreviousLayout = 0;
    final List<View> mDisappearingViewsInLayoutPass = new ArrayList();
    private boolean mInPreLayout = false;
    int mItemCount = 0;
    ArrayMap<Long, RecyclerView.ViewHolder> mOldChangedHolders = new ArrayMap();
    ArrayMap<RecyclerView.ViewHolder, RecyclerView.ItemHolderInfo> mPostLayoutHolderMap = new ArrayMap();
    ArrayMap<RecyclerView.ViewHolder, RecyclerView.ItemHolderInfo> mPreLayoutHolderMap = new ArrayMap();
    private int mPreviousLayoutItemCount = 0;
    private boolean mRunPredictiveAnimations = false;
    private boolean mRunSimpleAnimations = false;
    private boolean mStructureChanged = false;
    private int mTargetPosition = -1;
    
    private void removeFrom(ArrayMap<Long, RecyclerView.ViewHolder> paramArrayMap, RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramArrayMap.size() - 1;
      while (i >= 0)
      {
        if (paramViewHolder == paramArrayMap.valueAt(i))
        {
          paramArrayMap.removeAt(i);
          return;
        }
        i -= 1;
      }
    }
    
    void addToDisappearingList(View paramView)
    {
      if (!this.mDisappearingViewsInLayoutPass.contains(paramView)) {
        this.mDisappearingViewsInLayoutPass.add(paramView);
      }
    }
    
    public boolean didStructureChange()
    {
      return this.mStructureChanged;
    }
    
    public <T> T get(int paramInt)
    {
      if (this.mData == null) {
        return null;
      }
      return (T)this.mData.get(paramInt);
    }
    
    public int getItemCount()
    {
      if (this.mInPreLayout) {
        return this.mPreviousLayoutItemCount - this.mDeletedInvisibleItemCountSincePreviousLayout;
      }
      return this.mItemCount;
    }
    
    public int getTargetScrollPosition()
    {
      return this.mTargetPosition;
    }
    
    public boolean hasTargetScrollPosition()
    {
      return this.mTargetPosition != -1;
    }
    
    public boolean isPreLayout()
    {
      return this.mInPreLayout;
    }
    
    public void onViewIgnored(RecyclerView.ViewHolder paramViewHolder)
    {
      onViewRecycled(paramViewHolder);
    }
    
    void onViewRecycled(RecyclerView.ViewHolder paramViewHolder)
    {
      this.mPreLayoutHolderMap.remove(paramViewHolder);
      this.mPostLayoutHolderMap.remove(paramViewHolder);
      if (this.mOldChangedHolders != null) {
        removeFrom(this.mOldChangedHolders, paramViewHolder);
      }
      this.mDisappearingViewsInLayoutPass.remove(paramViewHolder.itemView);
    }
    
    public void put(int paramInt, Object paramObject)
    {
      if (this.mData == null) {
        this.mData = new SparseArray();
      }
      this.mData.put(paramInt, paramObject);
    }
    
    public void remove(int paramInt)
    {
      if (this.mData == null) {
        return;
      }
      this.mData.remove(paramInt);
    }
    
    void removeFromDisappearingList(View paramView)
    {
      this.mDisappearingViewsInLayoutPass.remove(paramView);
    }
    
    State reset()
    {
      this.mTargetPosition = -1;
      if (this.mData != null) {
        this.mData.clear();
      }
      this.mItemCount = 0;
      this.mStructureChanged = false;
      return this;
    }
    
    public String toString()
    {
      return "State{mTargetPosition=" + this.mTargetPosition + ", mPreLayoutHolderMap=" + this.mPreLayoutHolderMap + ", mPostLayoutHolderMap=" + this.mPostLayoutHolderMap + ", mData=" + this.mData + ", mItemCount=" + this.mItemCount + ", mPreviousLayoutItemCount=" + this.mPreviousLayoutItemCount + ", mDeletedInvisibleItemCountSincePreviousLayout=" + this.mDeletedInvisibleItemCountSincePreviousLayout + ", mStructureChanged=" + this.mStructureChanged + ", mInPreLayout=" + this.mInPreLayout + ", mRunSimpleAnimations=" + this.mRunSimpleAnimations + ", mRunPredictiveAnimations=" + this.mRunPredictiveAnimations + '}';
    }
    
    public boolean willRunPredictiveAnimations()
    {
      return this.mRunPredictiveAnimations;
    }
    
    public boolean willRunSimpleAnimations()
    {
      return this.mRunSimpleAnimations;
    }
  }
  
  public static abstract class ViewCacheExtension
  {
    public abstract View getViewForPositionAndType(RecyclerView.Recycler paramRecycler, int paramInt1, int paramInt2);
  }
  
  private class ViewFlinger
    implements Runnable
  {
    private boolean mEatRunOnAnimationRequest = false;
    private Interpolator mInterpolator = RecyclerView.-get15();
    private int mLastFlingX;
    private int mLastFlingY;
    private boolean mReSchedulePostAnimationCallback = false;
    private Scroller mScroller = new Scroller(RecyclerView.this.getContext(), RecyclerView.-get15());
    
    public ViewFlinger() {}
    
    private int computeScrollDuration(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      int j = Math.abs(paramInt1);
      int k = Math.abs(paramInt2);
      int i;
      if (j > k)
      {
        i = 1;
        paramInt3 = (int)Math.sqrt(paramInt3 * paramInt3 + paramInt4 * paramInt4);
        paramInt2 = (int)Math.sqrt(paramInt1 * paramInt1 + paramInt2 * paramInt2);
        if (i == 0) {
          break label140;
        }
      }
      label140:
      for (paramInt1 = RecyclerView.this.getWidth();; paramInt1 = RecyclerView.this.getHeight())
      {
        paramInt4 = paramInt1 / 2;
        float f3 = Math.min(1.0F, paramInt2 * 1.0F / paramInt1);
        float f1 = paramInt4;
        float f2 = paramInt4;
        f3 = distanceInfluenceForSnapDuration(f3);
        if (paramInt3 <= 0) {
          break label151;
        }
        paramInt1 = Math.round(Math.abs((f1 + f2 * f3) / paramInt3) * 1000.0F) * 4;
        return Math.min(paramInt1, 2000);
        i = 0;
        break;
      }
      label151:
      if (i != 0) {}
      for (paramInt2 = j;; paramInt2 = k)
      {
        paramInt1 = (int)((paramInt2 / paramInt1 + 1.0F) * 300.0F);
        break;
      }
    }
    
    private void disableRunOnAnimationRequests()
    {
      this.mReSchedulePostAnimationCallback = false;
      this.mEatRunOnAnimationRequest = true;
    }
    
    private float distanceInfluenceForSnapDuration(float paramFloat)
    {
      return (float)Math.sin((float)((paramFloat - 0.5F) * 0.4712389167638204D));
    }
    
    private void enableRunOnAnimationRequests()
    {
      this.mEatRunOnAnimationRequest = false;
      if (this.mReSchedulePostAnimationCallback) {
        postOnAnimation();
      }
    }
    
    public void fling(int paramInt1, int paramInt2)
    {
      RecyclerView.-wrap16(RecyclerView.this, 2);
      this.mLastFlingY = 0;
      this.mLastFlingX = 0;
      this.mScroller.fling(0, 0, paramInt1, paramInt2, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
      postOnAnimation();
    }
    
    void postOnAnimation()
    {
      if (this.mEatRunOnAnimationRequest)
      {
        this.mReSchedulePostAnimationCallback = true;
        return;
      }
      RecyclerView.this.removeCallbacks(this);
      RecyclerView.this.postOnAnimation(this);
    }
    
    public void run()
    {
      disableRunOnAnimationRequests();
      RecyclerView.-wrap6(RecyclerView.this);
      Scroller localScroller = this.mScroller;
      RecyclerView.SmoothScroller localSmoothScroller = RecyclerView.-get9(RecyclerView.this).mSmoothScroller;
      int i4;
      int i5;
      int n;
      int i;
      int i3;
      int m;
      int i1;
      int j;
      int i2;
      int k;
      if (localScroller.computeScrollOffset())
      {
        int i6 = localScroller.getCurrX();
        int i7 = localScroller.getCurrY();
        i4 = i6 - this.mLastFlingX;
        i5 = i7 - this.mLastFlingY;
        n = 0;
        i = 0;
        i3 = 0;
        m = 0;
        this.mLastFlingX = i6;
        this.mLastFlingY = i7;
        i1 = 0;
        j = 0;
        i2 = 0;
        k = 0;
        if (RecyclerView.-get2(RecyclerView.this) != null)
        {
          RecyclerView.this.eatRequestLayout();
          RecyclerView.-wrap12(RecyclerView.this);
          Trace.beginSection("RV Scroll");
          if (i4 != 0)
          {
            i = RecyclerView.-get9(RecyclerView.this).scrollHorizontallyBy(i4, RecyclerView.this.mRecycler, RecyclerView.this.mState);
            j = i4 - i;
          }
          if (i5 != 0)
          {
            m = RecyclerView.-get9(RecyclerView.this).scrollVerticallyBy(i5, RecyclerView.this.mRecycler, RecyclerView.this.mState);
            k = i5 - m;
          }
          Trace.endSection();
          if (RecyclerView.-wrap2(RecyclerView.this))
          {
            i1 = RecyclerView.this.mChildHelper.getChildCount();
            n = 0;
            while (n < i1)
            {
              View localView = RecyclerView.this.mChildHelper.getChildAt(n);
              Object localObject = RecyclerView.this.getChildViewHolder(localView);
              if ((localObject != null) && (((RecyclerView.ViewHolder)localObject).mShadowingHolder != null))
              {
                localObject = ((RecyclerView.ViewHolder)localObject).mShadowingHolder.itemView;
                i2 = localView.getLeft();
                i3 = localView.getTop();
                if ((i2 != ((View)localObject).getLeft()) || (i3 != ((View)localObject).getTop())) {
                  ((View)localObject).layout(i2, i3, ((View)localObject).getWidth() + i2, ((View)localObject).getHeight() + i3);
                }
              }
              n += 1;
            }
          }
          RecyclerView.-wrap13(RecyclerView.this);
          RecyclerView.this.resumeRequestLayout(false);
          n = i;
          i1 = j;
          i2 = k;
          i3 = m;
          if (localSmoothScroller != null)
          {
            if (!localSmoothScroller.isPendingInitialRun()) {
              break label709;
            }
            i3 = m;
            i2 = k;
            i1 = j;
            n = i;
          }
        }
        if (!RecyclerView.-get8(RecyclerView.this).isEmpty()) {
          RecyclerView.this.invalidate();
        }
        if (RecyclerView.this.getOverScrollMode() != 2) {
          RecyclerView.-wrap5(RecyclerView.this, i4, i5);
        }
        if ((i1 != 0) || (i2 != 0))
        {
          k = (int)localScroller.getCurrVelocity();
          i = 0;
          if (i1 != i6)
          {
            if (i1 >= 0) {
              break label845;
            }
            i = -k;
          }
          label488:
          j = 0;
          if (i2 != i7)
          {
            if (i2 >= 0) {
              break label860;
            }
            j = -k;
          }
          label505:
          if (RecyclerView.this.getOverScrollMode() != 2) {
            RecyclerView.this.absorbGlows(i, j);
          }
          if ((i == 0) && (i1 != i6)) {
            break label875;
          }
          label536:
          if ((j == 0) && (i2 != i7)) {
            break label886;
          }
          label547:
          localScroller.abortAnimation();
        }
        label552:
        if ((n != 0) || (i3 != 0)) {
          RecyclerView.this.dispatchOnScrolled(n, i3);
        }
        if (!RecyclerView.-wrap0(RecyclerView.this)) {
          RecyclerView.this.invalidate();
        }
        if ((i5 == 0) || (!RecyclerView.-get9(RecyclerView.this).canScrollVertically())) {
          break label902;
        }
        if (i3 != i5) {
          break label897;
        }
        i = 1;
        label617:
        if ((i4 == 0) || (!RecyclerView.-get9(RecyclerView.this).canScrollHorizontally())) {
          break label912;
        }
        if (n != i4) {
          break label907;
        }
        j = 1;
        label644:
        if ((i4 != 0) || (i5 != 0)) {
          break label917;
        }
        label654:
        i = 1;
        label656:
        if ((localScroller.isFinished()) || (i == 0)) {
          break label924;
        }
        postOnAnimation();
      }
      for (;;)
      {
        if (localSmoothScroller != null)
        {
          if (localSmoothScroller.isPendingInitialRun()) {
            RecyclerView.SmoothScroller.-wrap0(localSmoothScroller, 0, 0);
          }
          if (!this.mReSchedulePostAnimationCallback) {
            localSmoothScroller.stop();
          }
        }
        enableRunOnAnimationRequests();
        return;
        label709:
        n = i;
        i1 = j;
        i2 = k;
        i3 = m;
        if (!localSmoothScroller.isRunning()) {
          break;
        }
        n = RecyclerView.this.mState.getItemCount();
        if (n == 0)
        {
          localSmoothScroller.stop();
          n = i;
          i1 = j;
          i2 = k;
          i3 = m;
          break;
        }
        if (localSmoothScroller.getTargetPosition() >= n)
        {
          localSmoothScroller.setTargetPosition(n - 1);
          RecyclerView.SmoothScroller.-wrap0(localSmoothScroller, i4 - j, i5 - k);
          n = i;
          i1 = j;
          i2 = k;
          i3 = m;
          break;
        }
        RecyclerView.SmoothScroller.-wrap0(localSmoothScroller, i4 - j, i5 - k);
        n = i;
        i1 = j;
        i2 = k;
        i3 = m;
        break;
        label845:
        if (i1 > 0)
        {
          i = k;
          break label488;
        }
        i = 0;
        break label488;
        label860:
        if (i2 > 0)
        {
          j = k;
          break label505;
        }
        j = 0;
        break label505;
        label875:
        if (localScroller.getFinalX() != 0) {
          break label552;
        }
        break label536;
        label886:
        if (localScroller.getFinalY() != 0) {
          break label552;
        }
        break label547;
        label897:
        i = 0;
        break label617;
        label902:
        i = 0;
        break label617;
        label907:
        j = 0;
        break label644;
        label912:
        j = 0;
        break label644;
        label917:
        if (j != 0) {
          break label654;
        }
        break label656;
        label924:
        RecyclerView.-wrap16(RecyclerView.this, 0);
      }
    }
    
    public void smoothScrollBy(int paramInt1, int paramInt2)
    {
      smoothScrollBy(paramInt1, paramInt2, 0, 0);
    }
    
    public void smoothScrollBy(int paramInt1, int paramInt2, int paramInt3)
    {
      smoothScrollBy(paramInt1, paramInt2, paramInt3, RecyclerView.-get15());
    }
    
    public void smoothScrollBy(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      smoothScrollBy(paramInt1, paramInt2, computeScrollDuration(paramInt1, paramInt2, paramInt3, paramInt4));
    }
    
    public void smoothScrollBy(int paramInt1, int paramInt2, int paramInt3, Interpolator paramInterpolator)
    {
      if (this.mInterpolator != paramInterpolator)
      {
        this.mInterpolator = paramInterpolator;
        this.mScroller = new Scroller(RecyclerView.this.getContext(), paramInterpolator);
      }
      RecyclerView.-wrap16(RecyclerView.this, 2);
      this.mLastFlingY = 0;
      this.mLastFlingX = 0;
      this.mScroller.startScroll(0, 0, paramInt1, paramInt2, paramInt3);
      postOnAnimation();
    }
    
    public void stop()
    {
      RecyclerView.this.removeCallbacks(this);
      this.mScroller.abortAnimation();
    }
  }
  
  public static abstract class ViewHolder
  {
    static final int FLAG_ADAPTER_FULLUPDATE = 1024;
    static final int FLAG_ADAPTER_POSITION_UNKNOWN = 512;
    static final int FLAG_BOUND = 1;
    static final int FLAG_CHANGED = 64;
    static final int FLAG_IGNORE = 128;
    static final int FLAG_INVALID = 4;
    static final int FLAG_NOT_RECYCLABLE = 16;
    static final int FLAG_REMOVED = 8;
    static final int FLAG_RETURNED_FROM_SCRAP = 32;
    static final int FLAG_TMP_DETACHED = 256;
    static final int FLAG_UPDATE = 2;
    private static final List<Object> FULLUPDATE_PAYLOADS = Collections.EMPTY_LIST;
    public final View itemView;
    private int mFlags;
    private int mIsRecyclableCount = 0;
    long mItemId = -1L;
    int mItemViewType = -1;
    int mOldPosition = -1;
    RecyclerView mOwnerRecyclerView;
    List<Object> mPayloads = null;
    int mPosition = -1;
    int mPreLayoutPosition = -1;
    private RecyclerView.Recycler mScrapContainer = null;
    ViewHolder mShadowedHolder = null;
    ViewHolder mShadowingHolder = null;
    List<Object> mUnmodifiedPayloads = null;
    private int mWasImportantForAccessibilityBeforeHidden = 0;
    
    public ViewHolder(View paramView)
    {
      if (paramView == null) {
        throw new IllegalArgumentException("itemView may not be null");
      }
      this.itemView = paramView;
    }
    
    private void createPayloadsIfNeeded()
    {
      if (this.mPayloads == null)
      {
        this.mPayloads = new ArrayList();
        this.mUnmodifiedPayloads = Collections.unmodifiableList(this.mPayloads);
      }
    }
    
    private boolean doesTransientStatePreventRecycling()
    {
      boolean bool = false;
      if ((this.mFlags & 0x10) == 0) {
        bool = this.itemView.hasTransientState();
      }
      return bool;
    }
    
    private void onEnteredHiddenState()
    {
      this.mWasImportantForAccessibilityBeforeHidden = this.itemView.getImportantForAccessibility();
      this.itemView.setImportantForAccessibility(4);
    }
    
    private void onLeftHiddenState()
    {
      this.itemView.setImportantForAccessibility(this.mWasImportantForAccessibilityBeforeHidden);
      this.mWasImportantForAccessibilityBeforeHidden = 0;
    }
    
    private boolean shouldBeKeptAsChild()
    {
      boolean bool = false;
      if ((this.mFlags & 0x10) != 0) {
        bool = true;
      }
      return bool;
    }
    
    void addChangePayload(Object paramObject)
    {
      if (paramObject == null) {
        addFlags(1024);
      }
      while ((this.mFlags & 0x400) != 0) {
        return;
      }
      createPayloadsIfNeeded();
      this.mPayloads.add(paramObject);
    }
    
    void addFlags(int paramInt)
    {
      this.mFlags |= paramInt;
    }
    
    void clearOldPosition()
    {
      this.mOldPosition = -1;
      this.mPreLayoutPosition = -1;
    }
    
    void clearPayload()
    {
      if (this.mPayloads != null) {
        this.mPayloads.clear();
      }
      this.mFlags &= 0xFBFF;
    }
    
    void clearReturnedFromScrapFlag()
    {
      this.mFlags &= 0xFFFFFFDF;
    }
    
    void clearTmpDetachFlag()
    {
      this.mFlags &= 0xFEFF;
    }
    
    void flagRemovedAndOffsetPosition(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      addFlags(8);
      offsetPosition(paramInt2, paramBoolean);
      this.mPosition = paramInt1;
    }
    
    public final int getAdapterPosition()
    {
      if (this.mOwnerRecyclerView == null) {
        return -1;
      }
      return RecyclerView.-wrap3(this.mOwnerRecyclerView, this);
    }
    
    public final long getItemId()
    {
      return this.mItemId;
    }
    
    public final int getItemViewType()
    {
      return this.mItemViewType;
    }
    
    public final int getLayoutPosition()
    {
      if (this.mPreLayoutPosition == -1) {
        return this.mPosition;
      }
      return this.mPreLayoutPosition;
    }
    
    public final int getOldPosition()
    {
      return this.mOldPosition;
    }
    
    @Deprecated
    public final int getPosition()
    {
      if (this.mPreLayoutPosition == -1) {
        return this.mPosition;
      }
      return this.mPreLayoutPosition;
    }
    
    List<Object> getUnmodifiedPayloads()
    {
      if ((this.mFlags & 0x400) == 0)
      {
        if ((this.mPayloads == null) || (this.mPayloads.size() == 0)) {
          return FULLUPDATE_PAYLOADS;
        }
        return this.mUnmodifiedPayloads;
      }
      return FULLUPDATE_PAYLOADS;
    }
    
    boolean hasAnyOfTheFlags(int paramInt)
    {
      boolean bool = false;
      if ((this.mFlags & paramInt) != 0) {
        bool = true;
      }
      return bool;
    }
    
    boolean isAdapterPositionUnknown()
    {
      if ((this.mFlags & 0x200) == 0) {
        return isInvalid();
      }
      return true;
    }
    
    boolean isBound()
    {
      boolean bool = false;
      if ((this.mFlags & 0x1) != 0) {
        bool = true;
      }
      return bool;
    }
    
    boolean isChanged()
    {
      boolean bool = false;
      if ((this.mFlags & 0x40) != 0) {
        bool = true;
      }
      return bool;
    }
    
    boolean isInvalid()
    {
      boolean bool = false;
      if ((this.mFlags & 0x4) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public final boolean isRecyclable()
    {
      return ((this.mFlags & 0x10) == 0) && (!this.itemView.hasTransientState());
    }
    
    boolean isRemoved()
    {
      boolean bool = false;
      if ((this.mFlags & 0x8) != 0) {
        bool = true;
      }
      return bool;
    }
    
    boolean isScrap()
    {
      return this.mScrapContainer != null;
    }
    
    boolean isTmpDetached()
    {
      boolean bool = false;
      if ((this.mFlags & 0x100) != 0) {
        bool = true;
      }
      return bool;
    }
    
    boolean needsUpdate()
    {
      boolean bool = false;
      if ((this.mFlags & 0x2) != 0) {
        bool = true;
      }
      return bool;
    }
    
    void offsetPosition(int paramInt, boolean paramBoolean)
    {
      if (this.mOldPosition == -1) {
        this.mOldPosition = this.mPosition;
      }
      if (this.mPreLayoutPosition == -1) {
        this.mPreLayoutPosition = this.mPosition;
      }
      if (paramBoolean) {
        this.mPreLayoutPosition += paramInt;
      }
      this.mPosition += paramInt;
      if (this.itemView.getLayoutParams() != null) {
        ((RecyclerView.LayoutParams)this.itemView.getLayoutParams()).mInsetsDirty = true;
      }
    }
    
    void resetInternal()
    {
      this.mFlags = 0;
      this.mPosition = -1;
      this.mOldPosition = -1;
      this.mItemId = -1L;
      this.mPreLayoutPosition = -1;
      this.mIsRecyclableCount = 0;
      this.mShadowedHolder = null;
      this.mShadowingHolder = null;
      clearPayload();
      this.mWasImportantForAccessibilityBeforeHidden = 0;
    }
    
    void saveOldPosition()
    {
      if (this.mOldPosition == -1) {
        this.mOldPosition = this.mPosition;
      }
    }
    
    void setFlags(int paramInt1, int paramInt2)
    {
      this.mFlags = (this.mFlags & paramInt2 | paramInt1 & paramInt2);
    }
    
    public final void setIsRecyclable(boolean paramBoolean)
    {
      int i;
      if (paramBoolean)
      {
        i = this.mIsRecyclableCount - 1;
        this.mIsRecyclableCount = i;
        if (this.mIsRecyclableCount >= 0) {
          break label64;
        }
        this.mIsRecyclableCount = 0;
        Log.e("View", "isRecyclable decremented below 0: unmatched pair of setIsRecyable() calls for " + this);
      }
      label64:
      do
      {
        return;
        i = this.mIsRecyclableCount + 1;
        break;
        if ((!paramBoolean) && (this.mIsRecyclableCount == 1))
        {
          this.mFlags |= 0x10;
          return;
        }
      } while ((!paramBoolean) || (this.mIsRecyclableCount != 0));
      this.mFlags &= 0xFFFFFFEF;
    }
    
    void setScrapContainer(RecyclerView.Recycler paramRecycler)
    {
      this.mScrapContainer = paramRecycler;
    }
    
    boolean shouldIgnore()
    {
      boolean bool = false;
      if ((this.mFlags & 0x80) != 0) {
        bool = true;
      }
      return bool;
    }
    
    void stopIgnoring()
    {
      this.mFlags &= 0xFF7F;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("ViewHolder{" + Integer.toHexString(hashCode()) + " position=" + this.mPosition + " id=" + this.mItemId + ", oldPos=" + this.mOldPosition + ", pLpos:" + this.mPreLayoutPosition);
      if (isScrap()) {
        localStringBuilder.append(" scrap");
      }
      if (isInvalid()) {
        localStringBuilder.append(" invalid");
      }
      if (!isBound()) {
        localStringBuilder.append(" unbound");
      }
      if (needsUpdate()) {
        localStringBuilder.append(" update");
      }
      if (isRemoved()) {
        localStringBuilder.append(" removed");
      }
      if (shouldIgnore()) {
        localStringBuilder.append(" ignored");
      }
      if (isChanged()) {
        localStringBuilder.append(" changed");
      }
      if (isTmpDetached()) {
        localStringBuilder.append(" tmpDetached");
      }
      if (!isRecyclable()) {
        localStringBuilder.append(" not recyclable(").append(this.mIsRecyclableCount).append(")");
      }
      if (isAdapterPositionUnknown()) {
        localStringBuilder.append("undefined adapter position");
      }
      if (this.itemView.getParent() == null) {
        localStringBuilder.append(" no parent");
      }
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
    
    void unScrap()
    {
      this.mScrapContainer.unscrapView(this);
    }
    
    boolean wasReturnedFromScrap()
    {
      boolean bool = false;
      if ((this.mFlags & 0x20) != 0) {
        bool = true;
      }
      return bool;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\recyclerview\RecyclerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */