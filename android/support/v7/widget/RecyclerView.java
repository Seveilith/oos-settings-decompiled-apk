package android.support.v7.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.database.Observable;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.os.TraceCompat;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.CollectionInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.CollectionItemInfoCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.support.v4.widget.ScrollerCompat;
import android.support.v7.recyclerview.R.styleable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Display;
import android.view.FocusFinder;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Interpolator;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RecyclerView
  extends ViewGroup
  implements ScrollingView, NestedScrollingChild
{
  private static final boolean ALLOW_PREFETCHING;
  static final boolean ALLOW_SIZE_IN_UNSPECIFIED_SPEC;
  private static final int[] CLIP_TO_PADDING_ATTR;
  static final boolean DEBUG = false;
  static final boolean DISPATCH_TEMP_DETACH = false;
  static final boolean FORCE_INVALIDATE_DISPLAY_LIST;
  public static final int HORIZONTAL = 0;
  private static final int INVALID_POINTER = -1;
  public static final int INVALID_TYPE = -1;
  private static final Class<?>[] LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE;
  static final int MAX_SCROLL_DURATION = 2000;
  private static final long MIN_PREFETCH_TIME_NANOS;
  private static final double MOVE_TOUCH_SLOP = 0.6D;
  private static final int[] NESTED_SCROLLING_ATTRS = { 16843830 };
  public static final long NO_ID = -1L;
  public static final int NO_POSITION = -1;
  private static final boolean OPTS_INPUT = true;
  static final boolean POST_UPDATES_ON_ANIMATION;
  public static final int SCROLL_STATE_DRAGGING = 1;
  public static final int SCROLL_STATE_IDLE = 0;
  public static final int SCROLL_STATE_SETTLING = 2;
  static final String TAG = "RecyclerView";
  public static final int TOUCH_SLOP_DEFAULT = 0;
  public static final int TOUCH_SLOP_PAGING = 1;
  static final String TRACE_BIND_VIEW_TAG = "RV OnBindView";
  static final String TRACE_CREATE_VIEW_TAG = "RV CreateView";
  private static final String TRACE_HANDLE_ADAPTER_UPDATES_TAG = "RV PartialInvalidate";
  private static final String TRACE_ON_DATA_SET_CHANGE_LAYOUT_TAG = "RV FullInvalidate";
  private static final String TRACE_ON_LAYOUT_TAG = "RV OnLayout";
  private static final String TRACE_PREFETCH_TAG = "RV Prefetch";
  static final String TRACE_SCROLL_TAG = "RV Scroll";
  public static final int VERTICAL = 1;
  static long sFrameIntervalNanos;
  static final Interpolator sQuinticInterpolator;
  RecyclerViewAccessibilityDelegate mAccessibilityDelegate;
  private final AccessibilityManager mAccessibilityManager;
  private OnItemTouchListener mActiveOnItemTouchListener;
  Adapter mAdapter;
  AdapterHelper mAdapterHelper;
  boolean mAdapterUpdateDuringMeasure;
  private EdgeEffectCompat mBottomGlow;
  private ChildDrawingOrderCallback mChildDrawingOrderCallback;
  ChildHelper mChildHelper;
  boolean mClipToPadding;
  boolean mDataSetHasChangedAfterLayout = false;
  private int mDispatchScrollCounter = 0;
  private int mEatRequestLayout = 0;
  private int mEatenAccessibilityChangeFlags;
  @VisibleForTesting
  boolean mFirstLayoutComplete;
  boolean mHasFixedSize;
  private boolean mIgnoreMotionEventTillDown;
  private int mInitialTouchX;
  private int mInitialTouchY;
  boolean mIsAttached;
  private boolean mIsFirstTouchMoveEvent = false;
  ItemAnimator mItemAnimator = new DefaultItemAnimator();
  private RecyclerView.ItemAnimator.ItemAnimatorListener mItemAnimatorListener;
  private Runnable mItemAnimatorRunner;
  final ArrayList<ItemDecoration> mItemDecorations = new ArrayList();
  boolean mItemsAddedOrRemoved;
  boolean mItemsChanged;
  private int mLastTouchX;
  private int mLastTouchY;
  @VisibleForTesting
  LayoutManager mLayout;
  boolean mLayoutFrozen;
  private int mLayoutOrScrollCounter = 0;
  boolean mLayoutRequestEaten;
  private EdgeEffectCompat mLeftGlow;
  private final int mMaxFlingVelocity;
  private final int mMinFlingVelocity;
  private final int[] mMinMaxLayoutPositions;
  private int mMoveAcceleration;
  private final int[] mNestedOffsets;
  private int mNumTouchMoveEvent = 0;
  private final RecyclerViewDataObserver mObserver = new RecyclerViewDataObserver();
  private List<OnChildAttachStateChangeListener> mOnChildAttachStateListeners;
  private OnFlingListener mOnFlingListener;
  private final ArrayList<OnItemTouchListener> mOnItemTouchListeners = new ArrayList();
  private final List<ViewHolder> mPendingAccessibilityImportanceChange;
  private SavedState mPendingSavedState;
  boolean mPostedAnimatorRunner;
  private boolean mPreserveFocusAfterLayout = true;
  final Recycler mRecycler = new Recycler();
  RecyclerListener mRecyclerListener;
  private EdgeEffectCompat mRightGlow;
  private final int[] mScrollConsumed;
  private float mScrollFactor = Float.MIN_VALUE;
  private OnScrollListener mScrollListener;
  private List<OnScrollListener> mScrollListeners;
  private final int[] mScrollOffset;
  private int mScrollPointerId = -1;
  private int mScrollState = 0;
  private NestedScrollingChildHelper mScrollingChildHelper;
  final State mState;
  final Rect mTempRect = new Rect();
  private final Rect mTempRect2 = new Rect();
  final RectF mTempRectF = new RectF();
  private EdgeEffectCompat mTopGlow;
  private int mTouchSlop;
  final Runnable mUpdateChildViewsRunnable = new Runnable()
  {
    public void run()
    {
      if ((!RecyclerView.this.mFirstLayoutComplete) || (RecyclerView.this.isLayoutRequested())) {
        return;
      }
      if (!RecyclerView.this.mIsAttached)
      {
        RecyclerView.this.requestLayout();
        return;
      }
      if (RecyclerView.this.mLayoutFrozen)
      {
        RecyclerView.this.mLayoutRequestEaten = true;
        return;
      }
      RecyclerView.this.consumePendingUpdateOperations();
    }
  };
  private VelocityTracker mVelocityTracker;
  final ViewFlinger mViewFlinger = new ViewFlinger();
  private final ViewInfoStore.ProcessCallback mViewInfoProcessCallback;
  final ViewInfoStore mViewInfoStore = new ViewInfoStore();
  ViewPrefetcher mViewPrefetcher;
  
  static
  {
    CLIP_TO_PADDING_ATTR = new int[] { 16842987 };
    if ((Build.VERSION.SDK_INT == 18) || (Build.VERSION.SDK_INT == 19))
    {
      bool = true;
      FORCE_INVALIDATE_DISPLAY_LIST = bool;
      if (Build.VERSION.SDK_INT < 23) {
        break label164;
      }
      bool = true;
      label56:
      ALLOW_SIZE_IN_UNSPECIFIED_SPEC = bool;
      if (Build.VERSION.SDK_INT < 16) {
        break label169;
      }
      bool = true;
      label70:
      POST_UPDATES_ON_ANIMATION = bool;
      if (Build.VERSION.SDK_INT < 21) {
        break label174;
      }
    }
    label164:
    label169:
    label174:
    for (boolean bool = true;; bool = false)
    {
      ALLOW_PREFETCHING = bool;
      LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE = new Class[] { Context.class, AttributeSet.class, Integer.TYPE, Integer.TYPE };
      MIN_PREFETCH_TIME_NANOS = TimeUnit.MILLISECONDS.toNanos(4L);
      sFrameIntervalNanos = 0L;
      sQuinticInterpolator = new Interpolator()
      {
        public float getInterpolation(float paramAnonymousFloat)
        {
          paramAnonymousFloat -= 1.0F;
          return paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat + 1.0F;
        }
      };
      return;
      if (Build.VERSION.SDK_INT == 20)
      {
        bool = true;
        break;
      }
      bool = false;
      break;
      bool = false;
      break label56;
      bool = false;
      break label70;
    }
  }
  
  public RecyclerView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public RecyclerView(Context paramContext, @Nullable AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public RecyclerView(Context paramContext, @Nullable AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    Object localObject;
    label339:
    boolean bool;
    if (ALLOW_PREFETCHING)
    {
      localObject = new ViewPrefetcher();
      this.mViewPrefetcher = ((ViewPrefetcher)localObject);
      this.mState = new State();
      this.mItemsAddedOrRemoved = false;
      this.mItemsChanged = false;
      this.mItemAnimatorListener = new ItemAnimatorRestoreListener();
      this.mPostedAnimatorRunner = false;
      this.mMinMaxLayoutPositions = new int[2];
      this.mScrollOffset = new int[2];
      this.mScrollConsumed = new int[2];
      this.mNestedOffsets = new int[2];
      this.mPendingAccessibilityImportanceChange = new ArrayList();
      this.mItemAnimatorRunner = new Runnable()
      {
        public void run()
        {
          if (RecyclerView.this.mItemAnimator != null) {
            RecyclerView.this.mItemAnimator.runPendingAnimations();
          }
          RecyclerView.this.mPostedAnimatorRunner = false;
        }
      };
      this.mViewInfoProcessCallback = new ViewInfoStore.ProcessCallback()
      {
        public void processAppeared(RecyclerView.ViewHolder paramAnonymousViewHolder, RecyclerView.ItemAnimator.ItemHolderInfo paramAnonymousItemHolderInfo1, RecyclerView.ItemAnimator.ItemHolderInfo paramAnonymousItemHolderInfo2)
        {
          RecyclerView.this.animateAppearance(paramAnonymousViewHolder, paramAnonymousItemHolderInfo1, paramAnonymousItemHolderInfo2);
        }
        
        public void processDisappeared(RecyclerView.ViewHolder paramAnonymousViewHolder, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo paramAnonymousItemHolderInfo1, @Nullable RecyclerView.ItemAnimator.ItemHolderInfo paramAnonymousItemHolderInfo2)
        {
          RecyclerView.this.mRecycler.unscrapView(paramAnonymousViewHolder);
          RecyclerView.this.animateDisappearance(paramAnonymousViewHolder, paramAnonymousItemHolderInfo1, paramAnonymousItemHolderInfo2);
        }
        
        public void processPersistent(RecyclerView.ViewHolder paramAnonymousViewHolder, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo paramAnonymousItemHolderInfo1, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo paramAnonymousItemHolderInfo2)
        {
          paramAnonymousViewHolder.setIsRecyclable(false);
          if (RecyclerView.this.mDataSetHasChangedAfterLayout) {
            if (RecyclerView.this.mItemAnimator.animateChange(paramAnonymousViewHolder, paramAnonymousViewHolder, paramAnonymousItemHolderInfo1, paramAnonymousItemHolderInfo2)) {
              RecyclerView.this.postAnimationRunner();
            }
          }
          while (!RecyclerView.this.mItemAnimator.animatePersistence(paramAnonymousViewHolder, paramAnonymousItemHolderInfo1, paramAnonymousItemHolderInfo2)) {
            return;
          }
          RecyclerView.this.postAnimationRunner();
        }
        
        public void unused(RecyclerView.ViewHolder paramAnonymousViewHolder)
        {
          RecyclerView.this.mLayout.removeAndRecycleView(paramAnonymousViewHolder.itemView, RecyclerView.this.mRecycler);
        }
      };
      if (paramAttributeSet == null) {
        break label581;
      }
      localObject = paramContext.obtainStyledAttributes(paramAttributeSet, CLIP_TO_PADDING_ATTR, paramInt, 0);
      this.mClipToPadding = ((TypedArray)localObject).getBoolean(0, true);
      ((TypedArray)localObject).recycle();
      setScrollContainer(true);
      setFocusableInTouchMode(true);
      localObject = ViewConfiguration.get(paramContext);
      this.mTouchSlop = ((ViewConfiguration)localObject).getScaledTouchSlop();
      this.mMoveAcceleration = ((int)(this.mTouchSlop * 0.6D));
      this.mMinFlingVelocity = ((ViewConfiguration)localObject).getScaledMinimumFlingVelocity();
      this.mMaxFlingVelocity = ((ViewConfiguration)localObject).getScaledMaximumFlingVelocity();
      if (getOverScrollMode() != 2) {
        break label589;
      }
      bool = true;
      label407:
      setWillNotDraw(bool);
      this.mItemAnimator.setListener(this.mItemAnimatorListener);
      initAdapterManager();
      initChildrenHelper();
      if (ViewCompat.getImportantForAccessibility(this) == 0) {
        ViewCompat.setImportantForAccessibility(this, 1);
      }
      this.mAccessibilityManager = ((AccessibilityManager)getContext().getSystemService("accessibility"));
      setAccessibilityDelegateCompat(new RecyclerViewAccessibilityDelegate(this));
      bool = true;
      if (paramAttributeSet == null) {
        break label595;
      }
      localObject = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.RecyclerView, paramInt, 0);
      String str = ((TypedArray)localObject).getString(R.styleable.RecyclerView_layoutManager);
      if (((TypedArray)localObject).getInt(R.styleable.RecyclerView_android_descendantFocusability, -1) == -1) {
        setDescendantFocusability(262144);
      }
      ((TypedArray)localObject).recycle();
      createLayoutManager(paramContext, str, paramAttributeSet, paramInt, 0);
      if (Build.VERSION.SDK_INT >= 21)
      {
        paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, NESTED_SCROLLING_ATTRS, paramInt, 0);
        bool = paramContext.getBoolean(0, true);
        paramContext.recycle();
      }
    }
    for (;;)
    {
      setNestedScrollingEnabled(bool);
      return;
      localObject = null;
      break;
      label581:
      this.mClipToPadding = true;
      break label339;
      label589:
      bool = false;
      break label407;
      label595:
      setDescendantFocusability(262144);
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
  
  private void animateChange(@NonNull ViewHolder paramViewHolder1, @NonNull ViewHolder paramViewHolder2, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo1, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo2, boolean paramBoolean1, boolean paramBoolean2)
  {
    paramViewHolder1.setIsRecyclable(false);
    if (paramBoolean1) {
      addAnimatingView(paramViewHolder1);
    }
    if (paramViewHolder1 != paramViewHolder2)
    {
      if (paramBoolean2) {
        addAnimatingView(paramViewHolder2);
      }
      paramViewHolder1.mShadowedHolder = paramViewHolder2;
      addAnimatingView(paramViewHolder1);
      this.mRecycler.unscrapView(paramViewHolder1);
      paramViewHolder2.setIsRecyclable(false);
      paramViewHolder2.mShadowingHolder = paramViewHolder1;
    }
    if (this.mItemAnimator.animateChange(paramViewHolder1, paramViewHolder2, paramItemHolderInfo1, paramItemHolderInfo2)) {
      postAnimationRunner();
    }
  }
  
  private void cancelTouch()
  {
    resetTouch();
    setScrollState(0);
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
  
  private boolean didChildRangeChange(int paramInt1, int paramInt2)
  {
    findMinMaxChildLayoutPositions(this.mMinMaxLayoutPositions);
    return (this.mMinMaxLayoutPositions[0] != paramInt1) || (this.mMinMaxLayoutPositions[1] != paramInt2);
  }
  
  private void dispatchContentChangedIfNecessary()
  {
    int i = this.mEatenAccessibilityChangeFlags;
    this.mEatenAccessibilityChangeFlags = 0;
    if ((i != 0) && (isAccessibilityEnabled()))
    {
      AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain();
      localAccessibilityEvent.setEventType(2048);
      AccessibilityEventCompat.setContentChangeTypes(localAccessibilityEvent, i);
      sendAccessibilityEventUnchecked(localAccessibilityEvent);
    }
  }
  
  private void dispatchLayoutStep1()
  {
    this.mState.assertLayoutStep(1);
    this.mState.mIsMeasuring = false;
    eatRequestLayout();
    this.mViewInfoStore.clear();
    onEnterLayoutOrScroll();
    saveFocusInfo();
    processAdapterUpdatesAndSetAnimationFlags();
    Object localObject = this.mState;
    boolean bool;
    int j;
    int i;
    label134:
    RecyclerView.ItemAnimator.ItemHolderInfo localItemHolderInfo;
    if (this.mState.mRunSimpleAnimations)
    {
      bool = this.mItemsChanged;
      ((State)localObject).mTrackOldChangeHolders = bool;
      this.mItemsChanged = false;
      this.mItemsAddedOrRemoved = false;
      this.mState.mInPreLayout = this.mState.mRunPredictiveAnimations;
      this.mState.mItemCount = this.mAdapter.getItemCount();
      findMinMaxChildLayoutPositions(this.mMinMaxLayoutPositions);
      if (!this.mState.mRunSimpleAnimations) {
        break label291;
      }
      j = this.mChildHelper.getChildCount();
      i = 0;
      if (i >= j) {
        break label291;
      }
      localObject = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
      if ((!((ViewHolder)localObject).shouldIgnore()) && ((!((ViewHolder)localObject).isInvalid()) || (this.mAdapter.hasStableIds())))
      {
        localItemHolderInfo = this.mItemAnimator.recordPreLayoutInformation(this.mState, (ViewHolder)localObject, ItemAnimator.buildAdapterChangeFlagsForAnimations((ViewHolder)localObject), ((ViewHolder)localObject).getUnmodifiedPayloads());
        this.mViewInfoStore.addToPreLayout((ViewHolder)localObject, localItemHolderInfo);
        if ((this.mState.mTrackOldChangeHolders) && (((ViewHolder)localObject).isUpdated()) && (!((ViewHolder)localObject).isRemoved())) {
          break label253;
        }
      }
    }
    for (;;)
    {
      i += 1;
      break label134;
      bool = false;
      break;
      label253:
      if ((!((ViewHolder)localObject).shouldIgnore()) && (!((ViewHolder)localObject).isInvalid()))
      {
        long l = getChangedHolderKey((ViewHolder)localObject);
        this.mViewInfoStore.addToOldChangeHolders(l, (ViewHolder)localObject);
      }
    }
    label291:
    if (this.mState.mRunPredictiveAnimations)
    {
      saveOldPositions();
      bool = this.mState.mStructureChanged;
      this.mState.mStructureChanged = false;
      this.mLayout.onLayoutChildren(this.mRecycler, this.mState);
      this.mState.mStructureChanged = bool;
      i = 0;
      if (i < this.mChildHelper.getChildCount())
      {
        localObject = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
        if (((ViewHolder)localObject).shouldIgnore()) {}
        for (;;)
        {
          i += 1;
          break;
          if (!this.mViewInfoStore.isInPreLayout((ViewHolder)localObject))
          {
            int k = ItemAnimator.buildAdapterChangeFlagsForAnimations((ViewHolder)localObject);
            bool = ((ViewHolder)localObject).hasAnyOfTheFlags(8192);
            j = k;
            if (!bool) {
              j = k | 0x1000;
            }
            localItemHolderInfo = this.mItemAnimator.recordPreLayoutInformation(this.mState, (ViewHolder)localObject, j, ((ViewHolder)localObject).getUnmodifiedPayloads());
            if (bool) {
              recordAnimationInfoIfBouncedHiddenView((ViewHolder)localObject, localItemHolderInfo);
            } else {
              this.mViewInfoStore.addToAppearedInPreLayoutHolders((ViewHolder)localObject, localItemHolderInfo);
            }
          }
        }
      }
      clearOldPositions();
    }
    for (;;)
    {
      onExitLayoutOrScroll();
      resumeRequestLayout(false);
      this.mState.mLayoutStep = 2;
      return;
      clearOldPositions();
    }
  }
  
  private void dispatchLayoutStep2()
  {
    eatRequestLayout();
    onEnterLayoutOrScroll();
    this.mState.assertLayoutStep(6);
    this.mAdapterHelper.consumeUpdatesInOnePass();
    this.mState.mItemCount = this.mAdapter.getItemCount();
    this.mState.mDeletedInvisibleItemCountSincePreviousLayout = 0;
    this.mState.mInPreLayout = false;
    this.mLayout.onLayoutChildren(this.mRecycler, this.mState);
    this.mState.mStructureChanged = false;
    this.mPendingSavedState = null;
    State localState = this.mState;
    if ((this.mState.mRunSimpleAnimations) && (this.mItemAnimator != null)) {}
    for (boolean bool = true;; bool = false)
    {
      localState.mRunSimpleAnimations = bool;
      this.mState.mLayoutStep = 4;
      onExitLayoutOrScroll();
      resumeRequestLayout(false);
      return;
    }
  }
  
  private void dispatchLayoutStep3()
  {
    this.mState.assertLayoutStep(4);
    eatRequestLayout();
    onEnterLayoutOrScroll();
    this.mState.mLayoutStep = 1;
    if (this.mState.mRunSimpleAnimations)
    {
      int i = this.mChildHelper.getChildCount() - 1;
      if (i >= 0)
      {
        ViewHolder localViewHolder1 = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
        if (localViewHolder1.shouldIgnore()) {}
        for (;;)
        {
          i -= 1;
          break;
          long l = getChangedHolderKey(localViewHolder1);
          RecyclerView.ItemAnimator.ItemHolderInfo localItemHolderInfo2 = this.mItemAnimator.recordPostLayoutInformation(this.mState, localViewHolder1);
          ViewHolder localViewHolder2 = this.mViewInfoStore.getFromOldChangeHolders(l);
          if ((localViewHolder2 == null) || (localViewHolder2.shouldIgnore()))
          {
            this.mViewInfoStore.addToPostLayout(localViewHolder1, localItemHolderInfo2);
          }
          else
          {
            boolean bool1 = this.mViewInfoStore.isDisappearing(localViewHolder2);
            boolean bool2 = this.mViewInfoStore.isDisappearing(localViewHolder1);
            if ((bool1) && (localViewHolder2 == localViewHolder1))
            {
              this.mViewInfoStore.addToPostLayout(localViewHolder1, localItemHolderInfo2);
            }
            else
            {
              RecyclerView.ItemAnimator.ItemHolderInfo localItemHolderInfo1 = this.mViewInfoStore.popFromPreLayout(localViewHolder2);
              this.mViewInfoStore.addToPostLayout(localViewHolder1, localItemHolderInfo2);
              localItemHolderInfo2 = this.mViewInfoStore.popFromPostLayout(localViewHolder1);
              if (localItemHolderInfo1 == null) {
                handleMissingPreInfoForChangeError(l, localViewHolder1, localViewHolder2);
              } else {
                animateChange(localViewHolder2, localViewHolder1, localItemHolderInfo1, localItemHolderInfo2, bool1, bool2);
              }
            }
          }
        }
      }
      this.mViewInfoStore.process(this.mViewInfoProcessCallback);
    }
    this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
    this.mState.mPreviousLayoutItemCount = this.mState.mItemCount;
    this.mDataSetHasChangedAfterLayout = false;
    this.mState.mRunSimpleAnimations = false;
    this.mState.mRunPredictiveAnimations = false;
    this.mLayout.mRequestedSimpleAnimations = false;
    if (this.mRecycler.mChangedScrap != null) {
      this.mRecycler.mChangedScrap.clear();
    }
    this.mLayout.onLayoutCompleted(this.mState);
    onExitLayoutOrScroll();
    resumeRequestLayout(false);
    this.mViewInfoStore.clear();
    if (didChildRangeChange(this.mMinMaxLayoutPositions[0], this.mMinMaxLayoutPositions[1])) {
      dispatchOnScrolled(0, 0);
    }
    recoverFocusFromState();
    resetFocusInfo();
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
      paramArrayOfInt[0] = -1;
      paramArrayOfInt[1] = -1;
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
  
  static ViewHolder getChildViewHolderInt(View paramView)
  {
    if (paramView == null) {
      return null;
    }
    return ((LayoutParams)paramView.getLayoutParams()).mViewHolder;
  }
  
  static void getDecoratedBoundsWithMarginsInt(View paramView, Rect paramRect)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    Rect localRect = localLayoutParams.mDecorInsets;
    paramRect.set(paramView.getLeft() - localRect.left - localLayoutParams.leftMargin, paramView.getTop() - localRect.top - localLayoutParams.topMargin, paramView.getRight() + localRect.right + localLayoutParams.rightMargin, paramView.getBottom() + localRect.bottom + localLayoutParams.bottomMargin);
  }
  
  private int getDeepestFocusedViewWithId(View paramView)
  {
    int i = paramView.getId();
    while ((!paramView.isFocused()) && ((paramView instanceof ViewGroup)) && (paramView.hasFocus()))
    {
      View localView = ((ViewGroup)paramView).getFocusedChild();
      paramView = localView;
      if (localView.getId() != -1)
      {
        i = localView.getId();
        paramView = localView;
      }
    }
    return i;
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
  
  private NestedScrollingChildHelper getScrollingChildHelper()
  {
    if (this.mScrollingChildHelper == null) {
      this.mScrollingChildHelper = new NestedScrollingChildHelper(this);
    }
    return this.mScrollingChildHelper;
  }
  
  private void handleMissingPreInfoForChangeError(long paramLong, ViewHolder paramViewHolder1, ViewHolder paramViewHolder2)
  {
    int j = this.mChildHelper.getChildCount();
    int i = 0;
    if (i < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
      if (localViewHolder == paramViewHolder1) {}
      while (getChangedHolderKey(localViewHolder) != paramLong)
      {
        i += 1;
        break;
      }
      if ((this.mAdapter != null) && (this.mAdapter.hasStableIds())) {
        throw new IllegalStateException("Two different ViewHolders have the same stable ID. Stable IDs in your adapter MUST BE unique and SHOULD NOT change.\n ViewHolder 1:" + localViewHolder + " \n View Holder 2:" + paramViewHolder1);
      }
      throw new IllegalStateException("Two different ViewHolders have the same change ID. This might happen due to inconsistent Adapter update events or if the LayoutManager lays out the same View multiple times.\n ViewHolder 1:" + localViewHolder + " \n View Holder 2:" + paramViewHolder1);
    }
    Log.e("RecyclerView", "Problem while matching changed view holders with the newones. The pre-layout information for the change holder " + paramViewHolder2 + " cannot be found but it is necessary for " + paramViewHolder1);
  }
  
  private boolean hasUpdatedView()
  {
    int j = this.mChildHelper.getChildCount();
    int i = 0;
    if (i < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
      if ((localViewHolder == null) || (localViewHolder.shouldIgnore())) {}
      while (!localViewHolder.isUpdated())
      {
        i += 1;
        break;
      }
      return true;
    }
    return false;
  }
  
  private void initChildrenHelper()
  {
    this.mChildHelper = new ChildHelper(new ChildHelper.Callback()
    {
      public void addView(View paramAnonymousView, int paramAnonymousInt)
      {
        RecyclerView.this.addView(paramAnonymousView, paramAnonymousInt);
        RecyclerView.this.dispatchChildAttached(paramAnonymousView);
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
          RecyclerView.-wrap1(RecyclerView.this, paramAnonymousView, paramAnonymousInt, paramAnonymousLayoutParams);
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
        RecyclerView.-wrap2(RecyclerView.this, paramAnonymousInt);
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
          RecyclerView.ViewHolder.-wrap2(paramAnonymousView, RecyclerView.this);
        }
      }
      
      public void onLeftHiddenState(View paramAnonymousView)
      {
        paramAnonymousView = RecyclerView.getChildViewHolderInt(paramAnonymousView);
        if (paramAnonymousView != null) {
          RecyclerView.ViewHolder.-wrap3(paramAnonymousView, RecyclerView.this);
        }
      }
      
      public void removeAllViews()
      {
        int j = getChildCount();
        int i = 0;
        while (i < j)
        {
          RecyclerView.this.dispatchChildDetached(getChildAt(i));
          i += 1;
        }
        RecyclerView.this.removeAllViews();
      }
      
      public void removeViewAt(int paramAnonymousInt)
      {
        View localView = RecyclerView.this.getChildAt(paramAnonymousInt);
        if (localView != null) {
          RecyclerView.this.dispatchChildDetached(localView);
        }
        RecyclerView.this.removeViewAt(paramAnonymousInt);
      }
    });
  }
  
  private boolean isPreferredNextFocus(View paramView1, View paramView2, int paramInt)
  {
    int j = 0;
    if ((paramView2 == null) || (paramView2 == this)) {
      return false;
    }
    if (paramView1 == null) {
      return true;
    }
    if ((paramInt == 2) || (paramInt == 1))
    {
      if (this.mLayout.getLayoutDirection() == 1)
      {
        i = 1;
        if (paramInt == 2) {
          j = 1;
        }
        if ((j ^ i) == 0) {
          break label83;
        }
      }
      label83:
      for (int i = 66;; i = 17)
      {
        if (!isPreferredNextFocusAbsolute(paramView1, paramView2, i)) {
          break label90;
        }
        return true;
        i = 0;
        break;
      }
      label90:
      if (paramInt == 2) {
        return isPreferredNextFocusAbsolute(paramView1, paramView2, 130);
      }
      return isPreferredNextFocusAbsolute(paramView1, paramView2, 33);
    }
    return isPreferredNextFocusAbsolute(paramView1, paramView2, paramInt);
  }
  
  private boolean isPreferredNextFocusAbsolute(View paramView1, View paramView2, int paramInt)
  {
    boolean bool3 = true;
    boolean bool4 = true;
    boolean bool5 = true;
    boolean bool2 = true;
    boolean bool6 = false;
    boolean bool7 = false;
    boolean bool8 = false;
    boolean bool1 = false;
    this.mTempRect.set(0, 0, paramView1.getWidth(), paramView1.getHeight());
    this.mTempRect2.set(0, 0, paramView2.getWidth(), paramView2.getHeight());
    offsetDescendantRectToMyCoords(paramView1, this.mTempRect);
    offsetDescendantRectToMyCoords(paramView2, this.mTempRect2);
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("direction must be absolute. received:" + paramInt);
    case 17: 
      if ((this.mTempRect.right > this.mTempRect2.right) || (this.mTempRect.left >= this.mTempRect2.right)) {
        if (this.mTempRect.left <= this.mTempRect2.left) {
          break label206;
        }
      }
      for (bool1 = bool2;; bool1 = false) {
        return bool1;
      }
    case 66: 
      if (this.mTempRect.left >= this.mTempRect2.left)
      {
        bool1 = bool6;
        if (this.mTempRect.right > this.mTempRect2.left) {}
      }
      else
      {
        if (this.mTempRect.right >= this.mTempRect2.right) {
          break label274;
        }
      }
      for (bool1 = bool3;; bool1 = false) {
        return bool1;
      }
    case 33: 
      label206:
      label274:
      if (this.mTempRect.bottom <= this.mTempRect2.bottom)
      {
        bool1 = bool7;
        if (this.mTempRect.top < this.mTempRect2.bottom) {}
      }
      else
      {
        if (this.mTempRect.top <= this.mTempRect2.top) {
          break label342;
        }
      }
      label342:
      for (bool1 = bool4;; bool1 = false) {
        return bool1;
      }
    }
    if (this.mTempRect.top >= this.mTempRect2.top)
    {
      bool1 = bool8;
      if (this.mTempRect.bottom > this.mTempRect2.top) {}
    }
    else
    {
      if (this.mTempRect.bottom >= this.mTempRect2.bottom) {
        break label410;
      }
    }
    label410:
    for (bool1 = bool5;; bool1 = false) {
      return bool1;
    }
  }
  
  private void onPointerUp(MotionEvent paramMotionEvent)
  {
    int i = 0;
    int j = MotionEventCompat.getActionIndex(paramMotionEvent);
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
    boolean bool2;
    label54:
    State localState;
    if (predictiveItemAnimationsEnabled())
    {
      this.mAdapterHelper.preProcess();
      if (this.mItemsAddedOrRemoved) {
        break label166;
      }
      bool2 = this.mItemsChanged;
      localState = this.mState;
      if ((!this.mFirstLayoutComplete) || (this.mItemAnimator == null) || ((!this.mDataSetHasChangedAfterLayout) && (!bool2) && (!this.mLayout.mRequestedSimpleAnimations))) {
        break label176;
      }
      if (!this.mDataSetHasChangedAfterLayout) {
        break label171;
      }
      bool1 = this.mAdapter.hasStableIds();
      label110:
      localState.mRunSimpleAnimations = bool1;
      localState = this.mState;
      bool1 = bool3;
      if (this.mState.mRunSimpleAnimations)
      {
        bool1 = bool3;
        if (bool2) {
          if (!this.mDataSetHasChangedAfterLayout) {
            break label181;
          }
        }
      }
    }
    label166:
    label171:
    label176:
    label181:
    for (boolean bool1 = bool3;; bool1 = predictiveItemAnimationsEnabled())
    {
      localState.mRunPredictiveAnimations = bool1;
      return;
      this.mAdapterHelper.consumeUpdatesInOnePass();
      break;
      bool2 = true;
      break label54;
      bool1 = true;
      break label110;
      bool1 = false;
      break label110;
    }
  }
  
  private void pullGlows(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    int j = 0;
    int i;
    if (paramFloat2 < 0.0F)
    {
      ensureLeftGlow();
      i = j;
      if (this.mLeftGlow.onPull(-paramFloat2 / getWidth(), 1.0F - paramFloat3 / getHeight())) {
        i = 1;
      }
      if (paramFloat4 >= 0.0F) {
        break label155;
      }
      ensureTopGlow();
      j = i;
      if (this.mTopGlow.onPull(-paramFloat4 / getHeight(), paramFloat1 / getWidth())) {
        j = 1;
      }
      label91:
      if ((j == 0) && (paramFloat2 == 0.0F)) {
        break label207;
      }
    }
    for (;;)
    {
      ViewCompat.postInvalidateOnAnimation(this);
      label155:
      label207:
      do
      {
        return;
        i = j;
        if (paramFloat2 <= 0.0F) {
          break;
        }
        ensureRightGlow();
        i = j;
        if (!this.mRightGlow.onPull(paramFloat2 / getWidth(), paramFloat3 / getHeight())) {
          break;
        }
        i = 1;
        break;
        j = i;
        if (paramFloat4 <= 0.0F) {
          break label91;
        }
        ensureBottomGlow();
        j = i;
        if (!this.mBottomGlow.onPull(paramFloat4 / getHeight(), 1.0F - paramFloat1 / getWidth())) {
          break label91;
        }
        j = 1;
        break label91;
      } while (paramFloat4 == 0.0F);
    }
  }
  
  private void recoverFocusFromState()
  {
    if ((!this.mPreserveFocusAfterLayout) || (this.mAdapter == null)) {}
    while (!hasFocus()) {
      return;
    }
    Object localObject1;
    if (!isFocused())
    {
      localObject1 = getFocusedChild();
      if ((localObject1 == null) || (!this.mChildHelper.isHidden((View)localObject1))) {}
    }
    else
    {
      localObject1 = null;
      if (this.mState.mFocusedItemPosition != -1) {
        localObject1 = findViewHolderForAdapterPosition(this.mState.mFocusedItemPosition);
      }
      Object localObject2 = localObject1;
      if (localObject1 == null)
      {
        localObject2 = localObject1;
        if (this.mState.mFocusedItemId != -1L)
        {
          localObject2 = localObject1;
          if (this.mAdapter.hasStableIds()) {
            localObject2 = findViewHolderForItemId(this.mState.mFocusedItemId);
          }
        }
      }
      if ((localObject2 == null) || (((ViewHolder)localObject2).itemView.hasFocus()) || (!((ViewHolder)localObject2).itemView.hasFocusable())) {
        return;
      }
      View localView = ((ViewHolder)localObject2).itemView;
      localObject1 = localView;
      if (this.mState.mFocusedSubChildId != -1L)
      {
        localObject2 = ((ViewHolder)localObject2).itemView.findViewById(this.mState.mFocusedSubChildId);
        localObject1 = localView;
        if (localObject2 != null)
        {
          localObject1 = localView;
          if (((View)localObject2).isFocusable()) {
            localObject1 = localObject2;
          }
        }
      }
      ((View)localObject1).requestFocus();
      return;
    }
    return;
  }
  
  private void releaseGlows()
  {
    boolean bool2 = false;
    if (this.mLeftGlow != null) {
      bool2 = this.mLeftGlow.onRelease();
    }
    boolean bool1 = bool2;
    if (this.mTopGlow != null) {
      bool1 = bool2 | this.mTopGlow.onRelease();
    }
    bool2 = bool1;
    if (this.mRightGlow != null) {
      bool2 = bool1 | this.mRightGlow.onRelease();
    }
    bool1 = bool2;
    if (this.mBottomGlow != null) {
      bool1 = bool2 | this.mBottomGlow.onRelease();
    }
    if (bool1) {
      ViewCompat.postInvalidateOnAnimation(this);
    }
  }
  
  private void resetFocusInfo()
  {
    this.mState.mFocusedItemId = -1L;
    this.mState.mFocusedItemPosition = -1;
    this.mState.mFocusedSubChildId = -1;
  }
  
  private void resetTouch()
  {
    if (this.mVelocityTracker != null) {
      this.mVelocityTracker.clear();
    }
    stopNestedScroll();
    releaseGlows();
  }
  
  private void saveFocusInfo()
  {
    State localState = null;
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (this.mPreserveFocusAfterLayout)
    {
      localObject1 = localObject2;
      if (hasFocus())
      {
        localObject1 = localObject2;
        if (this.mAdapter != null) {
          localObject1 = getFocusedChild();
        }
      }
    }
    if (localObject1 == null) {}
    for (localObject1 = localState; localObject1 == null; localObject1 = findContainingViewHolder((View)localObject1))
    {
      resetFocusInfo();
      return;
    }
    localState = this.mState;
    long l;
    if (this.mAdapter.hasStableIds())
    {
      l = ((ViewHolder)localObject1).getItemId();
      localState.mFocusedItemId = l;
      localState = this.mState;
      if (!this.mDataSetHasChangedAfterLayout) {
        break label148;
      }
    }
    label148:
    for (int i = -1;; i = ((ViewHolder)localObject1).getAdapterPosition())
    {
      localState.mFocusedItemPosition = i;
      this.mState.mFocusedSubChildId = getDeepestFocusedViewWithId(((ViewHolder)localObject1).itemView);
      return;
      l = -1L;
      break;
    }
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
    this.mState.mStructureChanged = true;
    markKnownViewsInvalid();
  }
  
  private void stopScrollersInternal()
  {
    this.mViewFlinger.stop();
    if (this.mLayout != null) {
      this.mLayout.stopSmoothScroller();
    }
  }
  
  void absorbGlows(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0)
    {
      ensureLeftGlow();
      this.mLeftGlow.onAbsorb(-paramInt1);
      if (paramInt2 >= 0) {
        break label69;
      }
      ensureTopGlow();
      this.mTopGlow.onAbsorb(-paramInt2);
    }
    for (;;)
    {
      if ((paramInt1 != 0) || (paramInt2 != 0)) {
        ViewCompat.postInvalidateOnAnimation(this);
      }
      return;
      if (paramInt1 <= 0) {
        break;
      }
      ensureRightGlow();
      this.mRightGlow.onAbsorb(paramInt1);
      break;
      label69:
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
  
  void animateAppearance(@NonNull ViewHolder paramViewHolder, @Nullable RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo1, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo2)
  {
    paramViewHolder.setIsRecyclable(false);
    if (this.mItemAnimator.animateAppearance(paramViewHolder, paramItemHolderInfo1, paramItemHolderInfo2)) {
      postAnimationRunner();
    }
  }
  
  void animateDisappearance(@NonNull ViewHolder paramViewHolder, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo1, @Nullable RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo2)
  {
    addAnimatingView(paramViewHolder);
    paramViewHolder.setIsRecyclable(false);
    if (this.mItemAnimator.animateDisappearance(paramViewHolder, paramItemHolderInfo1, paramItemHolderInfo2)) {
      postAnimationRunner();
    }
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
    if (this.mDispatchScrollCounter > 0) {
      Log.w("RecyclerView", "Cannot call this method in a scroll callback. Scroll callbacks might be run during a measure & layout pass where you cannot change the RecyclerView data. Any method call that might change the structure of the RecyclerView or the adapter contents should be postponed to the next frame.", new IllegalStateException(""));
    }
  }
  
  boolean canReuseUpdatedViewHolder(ViewHolder paramViewHolder)
  {
    if (this.mItemAnimator != null) {
      return this.mItemAnimator.canReuseUpdatedViewHolder(paramViewHolder, paramViewHolder.getUnmodifiedPayloads());
    }
    return true;
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
    int i = 0;
    if (this.mLayout == null) {
      return 0;
    }
    if (this.mLayout.canScrollHorizontally()) {
      i = this.mLayout.computeHorizontalScrollExtent(this.mState);
    }
    return i;
  }
  
  public int computeHorizontalScrollOffset()
  {
    int i = 0;
    if (this.mLayout == null) {
      return 0;
    }
    if (this.mLayout.canScrollHorizontally()) {
      i = this.mLayout.computeHorizontalScrollOffset(this.mState);
    }
    return i;
  }
  
  public int computeHorizontalScrollRange()
  {
    int i = 0;
    if (this.mLayout == null) {
      return 0;
    }
    if (this.mLayout.canScrollHorizontally()) {
      i = this.mLayout.computeHorizontalScrollRange(this.mState);
    }
    return i;
  }
  
  public int computeVerticalScrollExtent()
  {
    int i = 0;
    if (this.mLayout == null) {
      return 0;
    }
    if (this.mLayout.canScrollVertically()) {
      i = this.mLayout.computeVerticalScrollExtent(this.mState);
    }
    return i;
  }
  
  public int computeVerticalScrollOffset()
  {
    int i = 0;
    if (this.mLayout == null) {
      return 0;
    }
    if (this.mLayout.canScrollVertically()) {
      i = this.mLayout.computeVerticalScrollOffset(this.mState);
    }
    return i;
  }
  
  public int computeVerticalScrollRange()
  {
    int i = 0;
    if (this.mLayout == null) {
      return 0;
    }
    if (this.mLayout.canScrollVertically()) {
      i = this.mLayout.computeVerticalScrollRange(this.mState);
    }
    return i;
  }
  
  void considerReleasingGlowsOnScroll(int paramInt1, int paramInt2)
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
          break label123;
        }
        bool2 = bool1;
      }
      label49:
      bool1 = bool2;
      if (this.mTopGlow != null)
      {
        if (!this.mTopGlow.isFinished()) {
          break label144;
        }
        bool1 = bool2;
      }
      label72:
      bool2 = bool1;
      if (this.mBottomGlow != null)
      {
        if (!this.mBottomGlow.isFinished()) {
          break label165;
        }
        bool2 = bool1;
      }
    }
    for (;;)
    {
      if (bool2) {
        ViewCompat.postInvalidateOnAnimation(this);
      }
      return;
      bool1 = bool2;
      if (paramInt1 <= 0) {
        break;
      }
      bool1 = this.mLeftGlow.onRelease();
      break;
      label123:
      bool2 = bool1;
      if (paramInt1 >= 0) {
        break label49;
      }
      bool2 = bool1 | this.mRightGlow.onRelease();
      break label49;
      label144:
      bool1 = bool2;
      if (paramInt2 <= 0) {
        break label72;
      }
      bool1 = bool2 | this.mTopGlow.onRelease();
      break label72;
      label165:
      bool2 = bool1;
      if (paramInt2 < 0) {
        bool2 = bool1 | this.mBottomGlow.onRelease();
      }
    }
  }
  
  void consumePendingUpdateOperations()
  {
    if ((!this.mFirstLayoutComplete) || (this.mDataSetHasChangedAfterLayout))
    {
      TraceCompat.beginSection("RV FullInvalidate");
      dispatchLayout();
      TraceCompat.endSection();
      return;
    }
    if (!this.mAdapterHelper.hasPendingUpdates()) {
      return;
    }
    if ((!this.mAdapterHelper.hasAnyUpdateTypes(4)) || (this.mAdapterHelper.hasAnyUpdateTypes(11)))
    {
      if (this.mAdapterHelper.hasPendingUpdates())
      {
        TraceCompat.beginSection("RV FullInvalidate");
        dispatchLayout();
        TraceCompat.endSection();
      }
      return;
    }
    TraceCompat.beginSection("RV PartialInvalidate");
    eatRequestLayout();
    this.mAdapterHelper.preProcess();
    if (!this.mLayoutRequestEaten)
    {
      if (!hasUpdatedView()) {
        break label127;
      }
      dispatchLayout();
    }
    for (;;)
    {
      resumeRequestLayout(true);
      TraceCompat.endSection();
      return;
      label127:
      this.mAdapterHelper.consumePostponedUpdates();
    }
  }
  
  void defaultOnMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(LayoutManager.chooseSize(paramInt1, getPaddingLeft() + getPaddingRight(), ViewCompat.getMinimumWidth(this)), LayoutManager.chooseSize(paramInt2, getPaddingTop() + getPaddingBottom(), ViewCompat.getMinimumHeight(this)));
  }
  
  void dispatchChildAttached(View paramView)
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
  
  void dispatchChildDetached(View paramView)
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
    this.mState.mIsMeasuring = false;
    if (this.mState.mLayoutStep == 1)
    {
      dispatchLayoutStep1();
      this.mLayout.setExactMeasureSpecsFrom(this);
      dispatchLayoutStep2();
    }
    for (;;)
    {
      dispatchLayoutStep3();
      return;
      if ((this.mAdapterHelper.hasUpdates()) || (this.mLayout.getWidth() != getWidth())) {}
      while (this.mLayout.getHeight() != getHeight())
      {
        this.mLayout.setExactMeasureSpecsFrom(this);
        dispatchLayoutStep2();
        break;
      }
      this.mLayout.setExactMeasureSpecsFrom(this);
    }
  }
  
  public boolean dispatchNestedFling(float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    return getScrollingChildHelper().dispatchNestedFling(paramFloat1, paramFloat2, paramBoolean);
  }
  
  public boolean dispatchNestedPreFling(float paramFloat1, float paramFloat2)
  {
    return getScrollingChildHelper().dispatchNestedPreFling(paramFloat1, paramFloat2);
  }
  
  public boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    return getScrollingChildHelper().dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2);
  }
  
  public boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    return getScrollingChildHelper().dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt);
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
    this.mDispatchScrollCounter += 1;
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
    this.mDispatchScrollCounter -= 1;
  }
  
  void dispatchPendingImportantForAccessibilityChanges()
  {
    int i = this.mPendingAccessibilityImportanceChange.size() - 1;
    while (i >= 0)
    {
      ViewHolder localViewHolder = (ViewHolder)this.mPendingAccessibilityImportanceChange.get(i);
      if ((localViewHolder.itemView.getParent() != this) || (localViewHolder.shouldIgnore())) {
        return;
      }
      int j = ViewHolder.-get2(localViewHolder);
      if (j != -1)
      {
        ViewCompat.setImportantForAccessibility(localViewHolder.itemView, j);
        ViewHolder.-set1(localViewHolder, -1);
      }
      i -= 1;
    }
    this.mPendingAccessibilityImportanceChange.clear();
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
        ViewCompat.postInvalidateOnAnimation(this);
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
    this.mEatRequestLayout += 1;
    if ((this.mEatRequestLayout != 1) || (this.mLayoutFrozen)) {
      return;
    }
    this.mLayoutRequestEaten = false;
  }
  
  void ensureBottomGlow()
  {
    if (this.mBottomGlow != null) {
      return;
    }
    this.mBottomGlow = new EdgeEffectCompat(getContext());
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
    this.mLeftGlow = new EdgeEffectCompat(getContext());
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
    this.mRightGlow = new EdgeEffectCompat(getContext());
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
    this.mTopGlow = new EdgeEffectCompat(getContext());
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
      float f1 = ViewCompat.getTranslationX(localView);
      float f2 = ViewCompat.getTranslationY(localView);
      if ((paramFloat1 >= localView.getLeft() + f1) && (paramFloat1 <= localView.getRight() + f1) && (paramFloat2 >= localView.getTop() + f2) && (paramFloat2 <= localView.getBottom() + f2)) {
        return localView;
      }
      i -= 1;
    }
    return null;
  }
  
  @Nullable
  public View findContainingItemView(View paramView)
  {
    ViewParent localViewParent = paramView.getParent();
    View localView = paramView;
    for (paramView = localViewParent; (paramView != null) && (paramView != this) && ((paramView instanceof View)); paramView = localView.getParent()) {
      localView = (View)paramView;
    }
    if (paramView == this) {
      return localView;
    }
    return null;
  }
  
  @Nullable
  public ViewHolder findContainingViewHolder(View paramView)
  {
    paramView = findContainingItemView(paramView);
    if (paramView == null) {
      return null;
    }
    return getChildViewHolder(paramView);
  }
  
  public ViewHolder findViewHolderForAdapterPosition(int paramInt)
  {
    if (this.mDataSetHasChangedAfterLayout) {
      return null;
    }
    int j = this.mChildHelper.getUnfilteredChildCount();
    Object localObject1 = null;
    int i = 0;
    if (i < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
      Object localObject2 = localObject1;
      if (localViewHolder != null)
      {
        if (!localViewHolder.isRemoved()) {
          break label72;
        }
        localObject2 = localObject1;
      }
      for (;;)
      {
        i += 1;
        localObject1 = localObject2;
        break;
        label72:
        localObject2 = localObject1;
        if (getAdapterPositionFor(localViewHolder) == paramInt)
        {
          if (!this.mChildHelper.isHidden(localViewHolder.itemView)) {
            break label108;
          }
          localObject2 = localViewHolder;
        }
      }
      label108:
      return localViewHolder;
    }
    return (ViewHolder)localObject1;
  }
  
  public ViewHolder findViewHolderForItemId(long paramLong)
  {
    Object localObject1;
    int i;
    ViewHolder localViewHolder;
    Object localObject2;
    if ((this.mAdapter != null) && (this.mAdapter.hasStableIds()))
    {
      int j = this.mChildHelper.getUnfilteredChildCount();
      localObject1 = null;
      i = 0;
      if (i >= j) {
        break label123;
      }
      localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
      localObject2 = localObject1;
      if (localViewHolder != null)
      {
        if (!localViewHolder.isRemoved()) {
          break label84;
        }
        localObject2 = localObject1;
      }
    }
    for (;;)
    {
      i += 1;
      localObject1 = localObject2;
      break;
      return null;
      label84:
      localObject2 = localObject1;
      if (localViewHolder.getItemId() == paramLong)
      {
        if (!this.mChildHelper.isHidden(localViewHolder.itemView)) {
          break label120;
        }
        localObject2 = localViewHolder;
      }
    }
    label120:
    return localViewHolder;
    label123:
    return (ViewHolder)localObject1;
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
    Object localObject1 = null;
    int i = 0;
    if (i < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
      Object localObject2 = localObject1;
      if (localViewHolder != null)
      {
        if (!localViewHolder.isRemoved()) {
          break label65;
        }
        localObject2 = localObject1;
      }
      for (;;)
      {
        i += 1;
        localObject1 = localObject2;
        break;
        label65:
        if (paramBoolean)
        {
          localObject2 = localObject1;
          if (localViewHolder.mPosition != paramInt) {}
        }
        else
        {
          while (localViewHolder.getLayoutPosition() == paramInt)
          {
            if (!this.mChildHelper.isHidden(localViewHolder.itemView)) {
              break label120;
            }
            localObject2 = localViewHolder;
            break;
          }
          localObject2 = localObject1;
        }
      }
      label120:
      return localViewHolder;
    }
    return (ViewHolder)localObject1;
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
        if ((this.mOnFlingListener == null) || (!this.mOnFlingListener.onFling(i, paramInt1))) {
          break;
        }
        return true;
        bool1 = true;
      }
      if (bool1)
      {
        paramInt2 = Math.max(-this.mMaxFlingVelocity, Math.min(i, this.mMaxFlingVelocity));
        paramInt1 = Math.max(-this.mMaxFlingVelocity, Math.min(paramInt1, this.mMaxFlingVelocity));
        this.mViewFlinger.fling(paramInt2, paramInt1);
        return true;
      }
    }
    return false;
  }
  
  public View focusSearch(View paramView, int paramInt)
  {
    int k = 1;
    Object localObject = this.mLayout.onInterceptFocusSearch(paramView, paramInt);
    if (localObject != null) {
      return (View)localObject;
    }
    int i;
    label85:
    label98:
    int j;
    if ((this.mAdapter == null) || (this.mLayout == null) || (isComputingLayout()))
    {
      i = 0;
      localObject = FocusFinder.getInstance();
      if ((i == 0) || ((paramInt != 2) && (paramInt != 1))) {
        break label285;
      }
      i = 0;
      if (this.mLayout.canScrollVertically())
      {
        if (paramInt != 2) {
          break label200;
        }
        i = 130;
        if (((FocusFinder)localObject).findNextFocus(this, paramView, i) != null) {
          break label206;
        }
        i = 1;
      }
      j = i;
      if (i == 0)
      {
        j = i;
        if (this.mLayout.canScrollHorizontally())
        {
          if (this.mLayout.getLayoutDirection() != 1) {
            break label211;
          }
          i = 1;
          label131:
          if (paramInt != 2) {
            break label216;
          }
          j = k;
          label140:
          if ((j ^ i) == 0) {
            break label222;
          }
          i = 66;
          label150:
          if (((FocusFinder)localObject).findNextFocus(this, paramView, i) != null) {
            break label228;
          }
          j = 1;
        }
      }
    }
    for (;;)
    {
      if (j != 0)
      {
        consumePendingUpdateOperations();
        if (findContainingItemView(paramView) == null)
        {
          return null;
          if (this.mLayoutFrozen)
          {
            i = 0;
            break;
          }
          i = 1;
          break;
          label200:
          i = 33;
          break label85;
          label206:
          i = 0;
          break label98;
          label211:
          i = 0;
          break label131;
          label216:
          j = 0;
          break label140;
          label222:
          i = 17;
          break label150;
          label228:
          j = 0;
          continue;
        }
        eatRequestLayout();
        this.mLayout.onFocusSearchFailed(paramView, paramInt, this.mRecycler, this.mState);
        resumeRequestLayout(false);
      }
    }
    localObject = ((FocusFinder)localObject).findNextFocus(this, paramView, paramInt);
    while (isPreferredNextFocus(paramView, (View)localObject, paramInt))
    {
      return (View)localObject;
      label285:
      View localView = ((FocusFinder)localObject).findNextFocus(this, paramView, paramInt);
      localObject = localView;
      if (localView == null)
      {
        localObject = localView;
        if (i != 0)
        {
          consumePendingUpdateOperations();
          if (findContainingItemView(paramView) == null) {
            return null;
          }
          eatRequestLayout();
          localObject = this.mLayout.onFocusSearchFailed(paramView, paramInt, this.mRecycler, this.mState);
          resumeRequestLayout(false);
        }
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
  
  int getAdapterPositionFor(ViewHolder paramViewHolder)
  {
    if ((!paramViewHolder.hasAnyOfTheFlags(524)) && (paramViewHolder.isBound())) {
      return this.mAdapterHelper.applyPendingUpdatesToPosition(paramViewHolder.mPosition);
    }
    return -1;
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
  
  public boolean getClipToPadding()
  {
    return this.mClipToPadding;
  }
  
  public RecyclerViewAccessibilityDelegate getCompatAccessibilityDelegate()
  {
    return this.mAccessibilityDelegate;
  }
  
  public void getDecoratedBoundsWithMargins(View paramView, Rect paramRect)
  {
    getDecoratedBoundsWithMarginsInt(paramView, paramRect);
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
    if ((this.mState.isPreLayout()) && ((localLayoutParams.isItemChanged()) || (localLayoutParams.isViewInvalid()))) {
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
  
  @Nullable
  public OnFlingListener getOnFlingListener()
  {
    return this.mOnFlingListener;
  }
  
  public boolean getPreserveFocusAfterLayout()
  {
    return this.mPreserveFocusAfterLayout;
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
    return getScrollingChildHelper().hasNestedScrollingParent();
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
        case 3: 
        case 5: 
        case 6: 
        case 7: 
        default: 
          return;
        case 1: 
          RecyclerView.this.mLayout.onItemsAdded(RecyclerView.this, paramAnonymousUpdateOp.positionStart, paramAnonymousUpdateOp.itemCount);
          return;
        case 2: 
          RecyclerView.this.mLayout.onItemsRemoved(RecyclerView.this, paramAnonymousUpdateOp.positionStart, paramAnonymousUpdateOp.itemCount);
          return;
        case 4: 
          RecyclerView.this.mLayout.onItemsUpdated(RecyclerView.this, paramAnonymousUpdateOp.positionStart, paramAnonymousUpdateOp.itemCount, paramAnonymousUpdateOp.payload);
          return;
        }
        RecyclerView.this.mLayout.onItemsMoved(RecyclerView.this, paramAnonymousUpdateOp.positionStart, paramAnonymousUpdateOp.itemCount, 1);
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
        localState.mDeletedInvisibleItemCountSincePreviousLayout += paramAnonymousInt2;
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
    return getScrollingChildHelper().isNestedScrollingEnabled();
  }
  
  void jumpToPositionForSmoothScroller(int paramInt)
  {
    if (this.mLayout == null) {
      return;
    }
    this.mLayout.scrollToPosition(paramInt);
    awakenScrollBars();
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
          this.mState.mStructureChanged = true;
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
        break label130;
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
      this.mState.mStructureChanged = true;
      break;
      localViewHolder.offsetPosition(j, false);
    }
    label130:
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
          this.mState.mStructureChanged = true;
        }
        else if (localViewHolder.mPosition >= paramInt1)
        {
          localViewHolder.flagRemovedAndOffsetPosition(paramInt1 - 1, -paramInt2, paramBoolean);
          this.mState.mStructureChanged = true;
        }
      }
    }
    this.mRecycler.offsetPositionRecordsForRemove(paramInt1, paramInt2, paramBoolean);
    requestLayout();
  }
  
  protected void onAttachedToWindow()
  {
    boolean bool = true;
    super.onAttachedToWindow();
    this.mLayoutOrScrollCounter = 0;
    this.mIsAttached = true;
    if ((!this.mFirstLayoutComplete) || (isLayoutRequested())) {
      bool = false;
    }
    this.mFirstLayoutComplete = bool;
    if (this.mLayout != null) {
      this.mLayout.dispatchAttachedToWindow(this);
    }
    this.mPostedAnimatorRunner = false;
    if ((ALLOW_PREFETCHING) && (sFrameIntervalNanos == 0L))
    {
      float f2 = 60.0F;
      Display localDisplay = ViewCompat.getDisplay(this);
      float f1 = f2;
      if (!isInEditMode())
      {
        f1 = f2;
        if (localDisplay != null)
        {
          float f3 = localDisplay.getRefreshRate();
          f1 = f2;
          if (f3 >= 30.0F) {
            f1 = f3;
          }
        }
      }
      sFrameIntervalNanos = (1.0E9F / f1);
    }
  }
  
  public void onChildAttachedToWindow(View paramView) {}
  
  public void onChildDetachedFromWindow(View paramView) {}
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (this.mItemAnimator != null) {
      this.mItemAnimator.endAnimations();
    }
    stopScroll();
    this.mIsAttached = false;
    if (this.mLayout != null) {
      this.mLayout.dispatchDetachedFromWindow(this, this.mRecycler);
    }
    this.mPendingAccessibilityImportanceChange.clear();
    removeCallbacks(this.mItemAnimatorRunner);
    this.mViewInfoStore.onDetach();
  }
  
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
  
  void onEnterLayoutOrScroll()
  {
    this.mLayoutOrScrollCounter += 1;
  }
  
  void onExitLayoutOrScroll()
  {
    this.mLayoutOrScrollCounter -= 1;
    if (this.mLayoutOrScrollCounter < 1)
    {
      this.mLayoutOrScrollCounter = 0;
      dispatchContentChangedIfNecessary();
      dispatchPendingImportantForAccessibilityChanges();
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
      f1 = -MotionEventCompat.getAxisValue(paramMotionEvent, 9);
      if (!this.mLayout.canScrollHorizontally()) {
        break label112;
      }
    }
    label107:
    label112:
    for (float f2 = MotionEventCompat.getAxisValue(paramMotionEvent, 10);; f2 = 0.0F)
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
    int j = MotionEventCompat.getActionMasked(paramMotionEvent);
    int i = MotionEventCompat.getActionIndex(paramMotionEvent);
    switch (j)
    {
    }
    while (this.mScrollState == 1)
    {
      return true;
      this.mNumTouchMoveEvent = 0;
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
      paramMotionEvent = this.mNestedOffsets;
      this.mNestedOffsets[1] = 0;
      paramMotionEvent[0] = 0;
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
      this.mNumTouchMoveEvent = 0;
      this.mScrollPointerId = paramMotionEvent.getPointerId(i);
      j = (int)(paramMotionEvent.getX(i) + 0.5F);
      this.mLastTouchX = j;
      this.mInitialTouchX = j;
      i = (int)(paramMotionEvent.getY(i) + 0.5F);
      this.mLastTouchY = i;
      this.mInitialTouchY = i;
      continue;
      this.mNumTouchMoveEvent += 1;
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
        int i2 = i - this.mInitialTouchX;
        int i1 = j - this.mInitialTouchY;
        int n = 0;
        label458:
        label472:
        int k;
        int m;
        if (this.mNumTouchMoveEvent == 1) {
          if (Math.abs(i2) > this.mMoveAcceleration)
          {
            i = 1;
            if (Math.abs(i1) <= this.mMoveAcceleration) {
              break label581;
            }
            j = 1;
            k = this.mMoveAcceleration;
            m = i;
            i = n;
            if (bool1)
            {
              i = n;
              if (m != 0)
              {
                m = this.mInitialTouchX;
                if (i2 >= 0) {
                  break label636;
                }
                i = -1;
                label510:
                this.mLastTouchX = (i * k + m);
                i = 1;
              }
            }
            m = i;
            if (bool2)
            {
              m = i;
              if (j != 0)
              {
                j = this.mInitialTouchY;
                if (i1 >= 0) {
                  break label641;
                }
              }
            }
          }
        }
        label581:
        label600:
        label631:
        label636:
        label641:
        for (i = -1;; i = 1)
        {
          this.mLastTouchY = (i * k + j);
          m = 1;
          if (m == 0) {
            break;
          }
          setScrollState(1);
          break;
          i = 0;
          break label458;
          j = 0;
          break label472;
          if (Math.abs(i2) > this.mTouchSlop)
          {
            i = 1;
            if (Math.abs(i1) <= this.mTouchSlop) {
              break label631;
            }
          }
          for (j = 1;; j = 0)
          {
            k = this.mTouchSlop;
            m = i;
            break;
            i = 0;
            break label600;
          }
          i = 1;
          break label510;
        }
        this.mNumTouchMoveEvent = 0;
        onPointerUp(paramMotionEvent);
        continue;
        this.mNumTouchMoveEvent = 0;
        this.mVelocityTracker.clear();
        stopNestedScroll();
        continue;
        this.mNumTouchMoveEvent = 0;
        cancelTouch();
      }
    }
    return false;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    TraceCompat.beginSection("RV OnLayout");
    dispatchLayout();
    TraceCompat.endSection();
    this.mFirstLayoutComplete = true;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (this.mLayout == null)
    {
      defaultOnMeasure(paramInt1, paramInt2);
      return;
    }
    if (this.mLayout.mAutoMeasure)
    {
      int i = View.MeasureSpec.getMode(paramInt1);
      int j = View.MeasureSpec.getMode(paramInt2);
      if (i == 1073741824) {
        if (j == 1073741824) {
          i = 1;
        }
      }
      for (;;)
      {
        this.mLayout.onMeasure(this.mRecycler, this.mState, paramInt1, paramInt2);
        if ((i == 0) && (this.mAdapter != null)) {
          break;
        }
        return;
        i = 0;
        continue;
        i = 0;
      }
      if (this.mState.mLayoutStep == 1) {
        dispatchLayoutStep1();
      }
      this.mLayout.setMeasureSpecs(paramInt1, paramInt2);
      this.mState.mIsMeasuring = true;
      dispatchLayoutStep2();
      this.mLayout.setMeasuredDimensionFromChildren(paramInt1, paramInt2);
      if (this.mLayout.shouldMeasureTwice())
      {
        this.mLayout.setMeasureSpecs(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
        this.mState.mIsMeasuring = true;
        dispatchLayoutStep2();
        this.mLayout.setMeasuredDimensionFromChildren(paramInt1, paramInt2);
      }
      return;
    }
    if (this.mHasFixedSize)
    {
      this.mLayout.onMeasure(this.mRecycler, this.mState, paramInt1, paramInt2);
      return;
    }
    if (this.mAdapterUpdateDuringMeasure)
    {
      eatRequestLayout();
      processAdapterUpdatesAndSetAnimationFlags();
      if (this.mState.mRunPredictiveAnimations)
      {
        this.mState.mInPreLayout = true;
        this.mAdapterUpdateDuringMeasure = false;
        resumeRequestLayout(false);
      }
    }
    else
    {
      if (this.mAdapter == null) {
        break label337;
      }
    }
    label337:
    for (this.mState.mItemCount = this.mAdapter.getItemCount();; this.mState.mItemCount = 0)
    {
      eatRequestLayout();
      this.mLayout.onMeasure(this.mRecycler, this.mState, paramInt1, paramInt2);
      resumeRequestLayout(false);
      this.mState.mInPreLayout = false;
      return;
      this.mAdapterHelper.consumeUpdatesInOnePass();
      this.mState.mInPreLayout = false;
      break;
    }
  }
  
  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect)
  {
    if (isComputingLayout()) {
      return false;
    }
    return super.onRequestFocusInDescendants(paramInt, paramRect);
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!(paramParcelable instanceof SavedState))
    {
      super.onRestoreInstanceState(paramParcelable);
      return;
    }
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
      localSavedState.copyFrom(this.mPendingSavedState);
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
    int i4 = 0;
    MotionEvent localMotionEvent = MotionEvent.obtain(paramMotionEvent);
    int k = MotionEventCompat.getActionMasked(paramMotionEvent);
    int j = MotionEventCompat.getActionIndex(paramMotionEvent);
    if (k == 0)
    {
      int[] arrayOfInt = this.mNestedOffsets;
      this.mNestedOffsets[1] = 0;
      arrayOfInt[0] = 0;
    }
    localMotionEvent.offsetLocation(this.mNestedOffsets[0], this.mNestedOffsets[1]);
    int i = i4;
    switch (k)
    {
    default: 
      i = i4;
    }
    for (;;)
    {
      if (i == 0) {
        this.mVelocityTracker.addMovement(localMotionEvent);
      }
      localMotionEvent.recycle();
      return true;
      this.mNumTouchMoveEvent = 0;
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
      i = i4;
      continue;
      this.mNumTouchMoveEvent = 0;
      this.mScrollPointerId = paramMotionEvent.getPointerId(j);
      i = (int)(paramMotionEvent.getX(j) + 0.5F);
      this.mLastTouchX = i;
      this.mInitialTouchX = i;
      i = (int)(paramMotionEvent.getY(j) + 0.5F);
      this.mLastTouchY = i;
      this.mInitialTouchY = i;
      i = i4;
      continue;
      i = paramMotionEvent.findPointerIndex(this.mScrollPointerId);
      if (i < 0)
      {
        Log.e("RecyclerView", "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
        return false;
      }
      int i5 = (int)(paramMotionEvent.getX(i) + 0.5F);
      int i6 = (int)(paramMotionEvent.getY(i) + 0.5F);
      int m = this.mLastTouchX - i5;
      k = this.mLastTouchY - i6;
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
      label604:
      label619:
      int i2;
      int i1;
      int i3;
      int n;
      if (this.mNumTouchMoveEvent == 1) {
        if (Math.abs(j) > this.mMoveAcceleration)
        {
          k = 1;
          if (Math.abs(i) <= this.mMoveAcceleration) {
            break label873;
          }
          m = 1;
          i2 = this.mMoveAcceleration;
          i1 = m;
          i3 = k;
          k = j;
          m = i;
          if (this.mScrollState != 1)
          {
            m = 0;
            n = j;
            k = m;
            if (bool1)
            {
              n = j;
              k = m;
              if (i3 != 0)
              {
                if (j <= 0) {
                  break label938;
                }
                n = j - i2;
                label690:
                k = 1;
              }
            }
            j = i;
            i3 = k;
            if (bool2)
            {
              j = i;
              i3 = k;
              if (i1 != 0)
              {
                if (i <= 0) {
                  break label948;
                }
                j = i - i2;
                label731:
                i3 = 1;
              }
            }
            k = n;
            m = j;
            if (i3 != 0)
            {
              setScrollState(1);
              m = j;
              k = n;
            }
          }
          i = i4;
          if (this.mScrollState != 1) {
            continue;
          }
          this.mLastTouchX = (i5 - this.mScrollOffset[0]);
          this.mLastTouchY = (i6 - this.mScrollOffset[1]);
          if (!bool1) {
            break label958;
          }
          i = k;
          label807:
          if (!bool2) {
            break label964;
          }
        }
      }
      label873:
      label894:
      label932:
      label938:
      label948:
      label958:
      label964:
      for (j = m;; j = 0)
      {
        if (scrollByInternal(i, j, localMotionEvent)) {
          getParent().requestDisallowInterceptTouchEvent(true);
        }
        i = i4;
        if (!ALLOW_PREFETCHING) {
          break;
        }
        this.mViewPrefetcher.postFromTraversal(k, m);
        i = i4;
        break;
        k = 0;
        break label604;
        m = 0;
        break label619;
        if (Math.abs(j) > this.mTouchSlop)
        {
          k = 1;
          if (Math.abs(i) <= this.mTouchSlop) {
            break label932;
          }
        }
        for (m = 1;; m = 0)
        {
          i2 = this.mTouchSlop;
          i3 = k;
          i1 = m;
          break;
          k = 0;
          break label894;
        }
        n = j + i2;
        break label690;
        j = i + i2;
        break label731;
        i = 0;
        break label807;
      }
      this.mNumTouchMoveEvent = 0;
      onPointerUp(paramMotionEvent);
      i = i4;
      continue;
      this.mNumTouchMoveEvent = 0;
      this.mVelocityTracker.addMovement(localMotionEvent);
      i = 1;
      this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaxFlingVelocity);
      float f1;
      label1037:
      float f2;
      if (bool1)
      {
        f1 = -VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mScrollPointerId);
        if (!bool2) {
          break label1099;
        }
        f2 = -VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mScrollPointerId);
        label1055:
        if ((f1 == 0.0F) && (f2 == 0.0F)) {
          break label1104;
        }
      }
      label1099:
      label1104:
      for (bool1 = fling((int)f1, (int)f2);; bool1 = false)
      {
        if (!bool1) {
          setScrollState(0);
        }
        resetTouch();
        break;
        f1 = 0.0F;
        break label1037;
        f2 = 0.0F;
        break label1055;
      }
      this.mNumTouchMoveEvent = 0;
      cancelTouch();
      i = i4;
    }
  }
  
  void postAnimationRunner()
  {
    if ((!this.mPostedAnimatorRunner) && (this.mIsAttached))
    {
      ViewCompat.postOnAnimation(this, this.mItemAnimatorRunner);
      this.mPostedAnimatorRunner = true;
    }
  }
  
  void recordAnimationInfoIfBouncedHiddenView(ViewHolder paramViewHolder, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo)
  {
    paramViewHolder.setFlags(0, 8192);
    if ((!this.mState.mTrackOldChangeHolders) || (!paramViewHolder.isUpdated()) || (paramViewHolder.isRemoved())) {}
    for (;;)
    {
      this.mViewInfoStore.addToPreLayout(paramViewHolder, paramItemHolderInfo);
      return;
      if (!paramViewHolder.shouldIgnore())
      {
        long l = getChangedHolderKey(paramViewHolder);
        this.mViewInfoStore.addToOldChangeHolders(l, paramViewHolder);
      }
    }
  }
  
  boolean removeAnimatingView(View paramView)
  {
    eatRequestLayout();
    boolean bool2 = this.mChildHelper.removeViewIfHidden(paramView);
    if (bool2)
    {
      paramView = getChildViewHolderInt(paramView);
      this.mRecycler.unscrapView(paramView);
      this.mRecycler.recycleViewHolderInternal(paramView);
    }
    if (bool2) {}
    for (boolean bool1 = false;; bool1 = true)
    {
      resumeRequestLayout(bool1);
      return bool2;
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
  
  void repositionShadowingViews()
  {
    int j = this.mChildHelper.getChildCount();
    int i = 0;
    while (i < j)
    {
      View localView = this.mChildHelper.getChildAt(i);
      Object localObject = getChildViewHolder(localView);
      if ((localObject != null) && (((ViewHolder)localObject).mShadowingHolder != null))
      {
        localObject = ((ViewHolder)localObject).mShadowingHolder.itemView;
        int k = localView.getLeft();
        int m = localView.getTop();
        if ((k != ((View)localObject).getLeft()) || (m != ((View)localObject).getTop())) {
          ((View)localObject).layout(k, m, ((View)localObject).getWidth() + k, ((View)localObject).getHeight() + m);
        }
      }
      i += 1;
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
    if ((this.mEatRequestLayout != 0) || (this.mLayoutFrozen))
    {
      this.mLayoutRequestEaten = true;
      return;
    }
    super.requestLayout();
  }
  
  void resumeRequestLayout(boolean paramBoolean)
  {
    if (this.mEatRequestLayout < 1) {
      this.mEatRequestLayout = 1;
    }
    if (!paramBoolean) {
      this.mLayoutRequestEaten = false;
    }
    if (this.mEatRequestLayout == 1) {
      if ((paramBoolean) && (this.mLayoutRequestEaten) && (!this.mLayoutFrozen)) {
        break label71;
      }
    }
    for (;;)
    {
      if (!this.mLayoutFrozen) {
        this.mLayoutRequestEaten = false;
      }
      this.mEatRequestLayout -= 1;
      return;
      label71:
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
      TraceCompat.beginSection("RV Scroll");
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
      TraceCompat.endSection();
      repositionShadowingViews();
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
    Log.w("RecyclerView", "RecyclerView does not support scrolling to an absolute position. Use scrollToPosition instead");
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
    ViewCompat.setAccessibilityDelegate(this, this.mAccessibilityDelegate);
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
  
  @VisibleForTesting
  boolean setChildImportantForAccessibilityInternal(ViewHolder paramViewHolder, int paramInt)
  {
    if (isComputingLayout())
    {
      ViewHolder.-set1(paramViewHolder, paramInt);
      this.mPendingAccessibilityImportanceChange.add(paramViewHolder);
      return false;
    }
    ViewCompat.setImportantForAccessibility(paramViewHolder.itemView, paramInt);
    return true;
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
  
  void setDataSetChangedAfterLayout()
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
        this.mLayoutFrozen = false;
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
    this.mLayoutFrozen = true;
    this.mIgnoreMotionEventTillDown = true;
    stopScroll();
  }
  
  public void setLayoutManager(LayoutManager paramLayoutManager)
  {
    if (paramLayoutManager == this.mLayout) {
      return;
    }
    stopScroll();
    if (this.mLayout != null)
    {
      if (this.mItemAnimator != null) {
        this.mItemAnimator.endAnimations();
      }
      this.mLayout.removeAndRecycleAllViews(this.mRecycler);
      this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
      this.mRecycler.clear();
      if (this.mIsAttached) {
        this.mLayout.dispatchDetachedFromWindow(this, this.mRecycler);
      }
      this.mLayout.setRecyclerView(null);
      this.mLayout = null;
    }
    for (;;)
    {
      this.mChildHelper.removeAllViewsUnfiltered();
      this.mLayout = paramLayoutManager;
      if (paramLayoutManager == null) {
        break label192;
      }
      if (paramLayoutManager.mRecyclerView == null) {
        break;
      }
      throw new IllegalArgumentException("LayoutManager " + paramLayoutManager + " is already attached to a RecyclerView: " + paramLayoutManager.mRecyclerView);
      this.mRecycler.clear();
    }
    this.mLayout.setRecyclerView(this);
    if (this.mIsAttached) {
      this.mLayout.dispatchAttachedToWindow(this);
    }
    label192:
    this.mRecycler.updateViewCacheSize();
    requestLayout();
  }
  
  public void setNestedScrollingEnabled(boolean paramBoolean)
  {
    getScrollingChildHelper().setNestedScrollingEnabled(paramBoolean);
  }
  
  public void setOnFlingListener(@Nullable OnFlingListener paramOnFlingListener)
  {
    this.mOnFlingListener = paramOnFlingListener;
  }
  
  @Deprecated
  public void setOnScrollListener(OnScrollListener paramOnScrollListener)
  {
    this.mScrollListener = paramOnScrollListener;
  }
  
  public void setPreserveFocusAfterLayout(boolean paramBoolean)
  {
    this.mPreserveFocusAfterLayout = paramBoolean;
  }
  
  public void setRecycledViewPool(RecycledViewPool paramRecycledViewPool)
  {
    this.mRecycler.setRecycledViewPool(paramRecycledViewPool);
  }
  
  public void setRecyclerListener(RecyclerListener paramRecyclerListener)
  {
    this.mRecyclerListener = paramRecyclerListener;
  }
  
  void setScrollState(int paramInt)
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
  
  public void setScrollingTouchSlop(int paramInt)
  {
    ViewConfiguration localViewConfiguration = ViewConfiguration.get(getContext());
    switch (paramInt)
    {
    default: 
      Log.w("RecyclerView", "setScrollingTouchSlop(): bad argument constant " + paramInt + "; using default value");
    case 0: 
      this.mTouchSlop = localViewConfiguration.getScaledTouchSlop();
      this.mMoveAcceleration = ((int)(this.mTouchSlop * 0.6D));
      return;
    }
    this.mTouchSlop = localViewConfiguration.getScaledPagingTouchSlop();
    this.mMoveAcceleration = ((int)(this.mTouchSlop * 0.6D));
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
        i = AccessibilityEventCompat.getContentChangeTypes(paramAccessibilityEvent);
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
    return getScrollingChildHelper().startNestedScroll(paramInt);
  }
  
  public void stopNestedScroll()
  {
    getScrollingChildHelper().stopNestedScroll();
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
      TraceCompat.beginSection("RV OnBindView");
      onBindViewHolder(paramVH, paramInt, paramVH.getUnmodifiedPayloads());
      paramVH.clearPayload();
      paramVH = paramVH.itemView.getLayoutParams();
      if ((paramVH instanceof RecyclerView.LayoutParams)) {
        ((RecyclerView.LayoutParams)paramVH).mInsetsDirty = true;
      }
      TraceCompat.endSection();
    }
    
    public final VH createViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      TraceCompat.beginSection("RV CreateView");
      paramViewGroup = onCreateViewHolder(paramViewGroup, paramInt);
      paramViewGroup.mItemViewType = paramInt;
      TraceCompat.endSection();
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
    public static final int FLAG_APPEARED_IN_PRE_LAYOUT = 4096;
    public static final int FLAG_CHANGED = 2;
    public static final int FLAG_INVALIDATED = 4;
    public static final int FLAG_MOVED = 2048;
    public static final int FLAG_REMOVED = 8;
    private long mAddDuration = 120L;
    private long mChangeDuration = 250L;
    private ArrayList<ItemAnimatorFinishedListener> mFinishedListeners = new ArrayList();
    private ItemAnimatorListener mListener = null;
    private long mMoveDuration = 250L;
    private long mRemoveDuration = 120L;
    
    static int buildAdapterChangeFlagsForAnimations(RecyclerView.ViewHolder paramViewHolder)
    {
      int j = RecyclerView.ViewHolder.-get0(paramViewHolder) & 0xE;
      if (paramViewHolder.isInvalid()) {
        return 4;
      }
      int i = j;
      if ((j & 0x4) == 0)
      {
        int k = paramViewHolder.getOldPosition();
        int m = paramViewHolder.getAdapterPosition();
        i = j;
        if (k != -1)
        {
          i = j;
          if (m != -1)
          {
            i = j;
            if (k != m) {
              i = j | 0x800;
            }
          }
        }
      }
      return i;
    }
    
    public abstract boolean animateAppearance(@NonNull RecyclerView.ViewHolder paramViewHolder, @Nullable ItemHolderInfo paramItemHolderInfo1, @NonNull ItemHolderInfo paramItemHolderInfo2);
    
    public abstract boolean animateChange(@NonNull RecyclerView.ViewHolder paramViewHolder1, @NonNull RecyclerView.ViewHolder paramViewHolder2, @NonNull ItemHolderInfo paramItemHolderInfo1, @NonNull ItemHolderInfo paramItemHolderInfo2);
    
    public abstract boolean animateDisappearance(@NonNull RecyclerView.ViewHolder paramViewHolder, @NonNull ItemHolderInfo paramItemHolderInfo1, @Nullable ItemHolderInfo paramItemHolderInfo2);
    
    public abstract boolean animatePersistence(@NonNull RecyclerView.ViewHolder paramViewHolder, @NonNull ItemHolderInfo paramItemHolderInfo1, @NonNull ItemHolderInfo paramItemHolderInfo2);
    
    public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder paramViewHolder)
    {
      return true;
    }
    
    public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder paramViewHolder, @NonNull List<Object> paramList)
    {
      return canReuseUpdatedViewHolder(paramViewHolder);
    }
    
    public final void dispatchAnimationFinished(RecyclerView.ViewHolder paramViewHolder)
    {
      onAnimationFinished(paramViewHolder);
      if (this.mListener != null) {
        this.mListener.onAnimationFinished(paramViewHolder);
      }
    }
    
    public final void dispatchAnimationStarted(RecyclerView.ViewHolder paramViewHolder)
    {
      onAnimationStarted(paramViewHolder);
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
    
    public ItemHolderInfo obtainHolderInfo()
    {
      return new ItemHolderInfo();
    }
    
    public void onAnimationFinished(RecyclerView.ViewHolder paramViewHolder) {}
    
    public void onAnimationStarted(RecyclerView.ViewHolder paramViewHolder) {}
    
    @NonNull
    public ItemHolderInfo recordPostLayoutInformation(@NonNull RecyclerView.State paramState, @NonNull RecyclerView.ViewHolder paramViewHolder)
    {
      return obtainHolderInfo().setFrom(paramViewHolder);
    }
    
    @NonNull
    public ItemHolderInfo recordPreLayoutInformation(@NonNull RecyclerView.State paramState, @NonNull RecyclerView.ViewHolder paramViewHolder, int paramInt, @NonNull List<Object> paramList)
    {
      return obtainHolderInfo().setFrom(paramViewHolder);
    }
    
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
    
    @Retention(RetentionPolicy.SOURCE)
    public static @interface AdapterChanges {}
    
    public static abstract interface ItemAnimatorFinishedListener
    {
      public abstract void onAnimationsFinished();
    }
    
    static abstract interface ItemAnimatorListener
    {
      public abstract void onAnimationFinished(RecyclerView.ViewHolder paramViewHolder);
    }
    
    public static class ItemHolderInfo
    {
      public int bottom;
      public int changeFlags;
      public int left;
      public int right;
      public int top;
      
      public ItemHolderInfo setFrom(RecyclerView.ViewHolder paramViewHolder)
      {
        return setFrom(paramViewHolder, 0);
      }
      
      public ItemHolderInfo setFrom(RecyclerView.ViewHolder paramViewHolder, int paramInt)
      {
        paramViewHolder = paramViewHolder.itemView;
        this.left = paramViewHolder.getLeft();
        this.top = paramViewHolder.getTop();
        this.right = paramViewHolder.getRight();
        this.bottom = paramViewHolder.getBottom();
        return this;
      }
    }
  }
  
  private class ItemAnimatorRestoreListener
    implements RecyclerView.ItemAnimator.ItemAnimatorListener
  {
    ItemAnimatorRestoreListener() {}
    
    public void onAnimationFinished(RecyclerView.ViewHolder paramViewHolder)
    {
      paramViewHolder.setIsRecyclable(true);
      if ((paramViewHolder.mShadowedHolder != null) && (paramViewHolder.mShadowingHolder == null)) {
        paramViewHolder.mShadowedHolder = null;
      }
      paramViewHolder.mShadowingHolder = null;
      if ((!RecyclerView.ViewHolder.-wrap1(paramViewHolder)) && (!RecyclerView.this.removeAnimatingView(paramViewHolder.itemView)) && (paramViewHolder.isTmpDetached())) {
        RecyclerView.this.removeDetachedView(paramViewHolder.itemView, false);
      }
    }
  }
  
  public static abstract class ItemDecoration
  {
    @Deprecated
    public void getItemOffsets(Rect paramRect, int paramInt, RecyclerView paramRecyclerView)
    {
      paramRect.set(0, 0, 0, 0);
    }
    
    public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState)
    {
      getItemOffsets(paramRect, ((RecyclerView.LayoutParams)paramView.getLayoutParams()).getViewLayoutPosition(), paramRecyclerView);
    }
    
    @Deprecated
    public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView) {}
    
    public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
    {
      onDraw(paramCanvas, paramRecyclerView);
    }
    
    @Deprecated
    public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView) {}
    
    public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
    {
      onDrawOver(paramCanvas, paramRecyclerView);
    }
  }
  
  public static abstract class LayoutManager
  {
    boolean mAutoMeasure = false;
    ChildHelper mChildHelper;
    private int mHeight;
    private int mHeightMode;
    boolean mIsAttachedToWindow = false;
    private boolean mItemPrefetchEnabled = true;
    private boolean mMeasurementCacheEnabled = true;
    RecyclerView mRecyclerView;
    boolean mRequestedSimpleAnimations = false;
    @Nullable
    RecyclerView.SmoothScroller mSmoothScroller;
    private int mWidth;
    private int mWidthMode;
    
    private void addViewInt(View paramView, int paramInt, boolean paramBoolean)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      RecyclerView.LayoutParams localLayoutParams;
      if ((paramBoolean) || (localViewHolder.isRemoved()))
      {
        this.mRecyclerView.mViewInfoStore.addToDisappearedInLayout(localViewHolder);
        localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
        if ((!localViewHolder.wasReturnedFromScrap()) && (!localViewHolder.isScrap())) {
          break label128;
        }
        if (!localViewHolder.isScrap()) {
          break label120;
        }
        localViewHolder.unScrap();
        label68:
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
        this.mRecyclerView.mViewInfoStore.removeFromDisappearedInLayout(localViewHolder);
        break;
        label120:
        localViewHolder.clearReturnedFromScrapFlag();
        break label68;
        label128:
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
            this.mRecyclerView.mLayout.moveView(j, i);
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
    
    public static int chooseSize(int paramInt1, int paramInt2, int paramInt3)
    {
      int i = View.MeasureSpec.getMode(paramInt1);
      paramInt1 = View.MeasureSpec.getSize(paramInt1);
      switch (i)
      {
      default: 
        return Math.max(paramInt2, paramInt3);
      case 1073741824: 
        return paramInt1;
      }
      return Math.min(paramInt1, Math.max(paramInt2, paramInt3));
    }
    
    private void detachViewInternal(int paramInt, View paramView)
    {
      this.mChildHelper.detachViewFromParent(paramInt);
    }
    
    public static int getChildMeasureSpec(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
    {
      int i = Math.max(0, paramInt1 - paramInt3);
      paramInt3 = 0;
      paramInt1 = 0;
      if (paramInt4 >= 0)
      {
        paramInt3 = paramInt4;
        paramInt1 = 1073741824;
      }
      for (;;)
      {
        return View.MeasureSpec.makeMeasureSpec(paramInt3, paramInt1);
        if (paramBoolean)
        {
          if (paramInt4 == -1)
          {
            switch (paramInt2)
            {
            default: 
              break;
            case 1073741824: 
            case -2147483648: 
              paramInt3 = i;
              paramInt1 = paramInt2;
              break;
            case 0: 
              paramInt3 = 0;
              paramInt1 = 0;
              break;
            }
          }
          else if (paramInt4 == -2)
          {
            paramInt3 = 0;
            paramInt1 = 0;
          }
        }
        else if (paramInt4 == -1)
        {
          paramInt3 = i;
          paramInt1 = paramInt2;
        }
        else if (paramInt4 == -2)
        {
          paramInt3 = i;
          if ((paramInt2 == Integer.MIN_VALUE) || (paramInt2 == 1073741824)) {
            paramInt1 = Integer.MIN_VALUE;
          } else {
            paramInt1 = 0;
          }
        }
      }
    }
    
    @Deprecated
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
      localProperties.spanCount = paramContext.getInt(R.styleable.RecyclerView_spanCount, 1);
      localProperties.reverseLayout = paramContext.getBoolean(R.styleable.RecyclerView_reverseLayout, false);
      localProperties.stackFromEnd = paramContext.getBoolean(R.styleable.RecyclerView_stackFromEnd, false);
      paramContext.recycle();
      return localProperties;
    }
    
    private static boolean isMeasurementUpToDate(int paramInt1, int paramInt2, int paramInt3)
    {
      int i = View.MeasureSpec.getMode(paramInt2);
      paramInt2 = View.MeasureSpec.getSize(paramInt2);
      if ((paramInt3 > 0) && (paramInt1 != paramInt3)) {
        return false;
      }
      switch (i)
      {
      default: 
        return false;
      case 0: 
        return true;
      case -2147483648: 
        return paramInt2 >= paramInt1;
      }
      return paramInt2 == paramInt1;
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
      while (this.mRecyclerView.mAdapter.hasStableIds())
      {
        detachViewAt(paramInt);
        paramRecycler.scrapView(paramView);
        this.mRecyclerView.mViewInfoStore.onViewDetached(localViewHolder);
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
        this.mRecyclerView.mViewInfoStore.addToDisappearedInLayout(localViewHolder);
      }
      for (;;)
      {
        this.mChildHelper.attachViewToParent(paramView, paramInt, paramLayoutParams, localViewHolder.isRemoved());
        return;
        this.mRecyclerView.mViewInfoStore.removeFromDisappearedInLayout(localViewHolder);
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
    
    public void endAnimation(View paramView)
    {
      if (this.mRecyclerView.mItemAnimator != null) {
        this.mRecyclerView.mItemAnimator.endAnimation(RecyclerView.getChildViewHolderInt(paramView));
      }
    }
    
    @Nullable
    public View findContainingItemView(View paramView)
    {
      if (this.mRecyclerView == null) {
        return null;
      }
      paramView = this.mRecyclerView.findContainingItemView(paramView);
      if (paramView == null) {
        return null;
      }
      if (this.mChildHelper.isHidden(paramView)) {
        return null;
      }
      return paramView;
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
    
    int gatherPrefetchIndices(int paramInt1, int paramInt2, RecyclerView.State paramState, int[] paramArrayOfInt)
    {
      return 0;
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
        return this.mRecyclerView.mClipToPadding;
      }
      return false;
    }
    
    public int getColumnCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      int i = 1;
      if ((this.mRecyclerView == null) || (this.mRecyclerView.mAdapter == null)) {
        return 1;
      }
      if (canScrollHorizontally()) {
        i = this.mRecyclerView.mAdapter.getItemCount();
      }
      return i;
    }
    
    public int getDecoratedBottom(View paramView)
    {
      return paramView.getBottom() + getBottomDecorationHeight(paramView);
    }
    
    public void getDecoratedBoundsWithMargins(View paramView, Rect paramRect)
    {
      RecyclerView.getDecoratedBoundsWithMarginsInt(paramView, paramRect);
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
      return this.mHeight;
    }
    
    public int getHeightMode()
    {
      return this.mHeightMode;
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
    
    int getItemPrefetchCount()
    {
      return 0;
    }
    
    public int getItemViewType(View paramView)
    {
      return RecyclerView.getChildViewHolderInt(paramView).getItemViewType();
    }
    
    public int getLayoutDirection()
    {
      return ViewCompat.getLayoutDirection(this.mRecyclerView);
    }
    
    public int getLeftDecorationWidth(View paramView)
    {
      return ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets.left;
    }
    
    public int getMinimumHeight()
    {
      return ViewCompat.getMinimumHeight(this.mRecyclerView);
    }
    
    public int getMinimumWidth()
    {
      return ViewCompat.getMinimumWidth(this.mRecyclerView);
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
        return ViewCompat.getPaddingEnd(this.mRecyclerView);
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
        return ViewCompat.getPaddingStart(this.mRecyclerView);
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
      if ((this.mRecyclerView == null) || (this.mRecyclerView.mAdapter == null)) {
        return 1;
      }
      if (canScrollVertically()) {
        i = this.mRecyclerView.mAdapter.getItemCount();
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
    
    public void getTransformedBoundingBox(View paramView, boolean paramBoolean, Rect paramRect)
    {
      Object localObject;
      if (paramBoolean)
      {
        localObject = ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets;
        paramRect.set(-((Rect)localObject).left, -((Rect)localObject).top, paramView.getWidth() + ((Rect)localObject).right, paramView.getHeight() + ((Rect)localObject).bottom);
        if (this.mRecyclerView != null)
        {
          localObject = ViewCompat.getMatrix(paramView);
          if ((localObject != null) && (!((Matrix)localObject).isIdentity())) {
            break label108;
          }
        }
      }
      for (;;)
      {
        paramRect.offset(paramView.getLeft(), paramView.getTop());
        return;
        paramRect.set(0, 0, paramView.getWidth(), paramView.getHeight());
        break;
        label108:
        RectF localRectF = this.mRecyclerView.mTempRectF;
        localRectF.set(paramRect);
        ((Matrix)localObject).mapRect(localRectF);
        paramRect.set((int)Math.floor(localRectF.left), (int)Math.floor(localRectF.top), (int)Math.ceil(localRectF.right), (int)Math.ceil(localRectF.bottom));
      }
    }
    
    public int getWidth()
    {
      return this.mWidth;
    }
    
    public int getWidthMode()
    {
      return this.mWidthMode;
    }
    
    boolean hasFlexibleChildInBothOrientations()
    {
      int j = getChildCount();
      int i = 0;
      while (i < j)
      {
        ViewGroup.LayoutParams localLayoutParams = getChildAt(i).getLayoutParams();
        if ((localLayoutParams.width < 0) && (localLayoutParams.height < 0)) {
          return true;
        }
        i += 1;
      }
      return false;
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
      this.mRecyclerView.mViewInfoStore.removeViewHolder(paramView);
    }
    
    public boolean isAttachedToWindow()
    {
      return this.mIsAttachedToWindow;
    }
    
    public boolean isAutoMeasureEnabled()
    {
      return this.mAutoMeasure;
    }
    
    public boolean isFocused()
    {
      if (this.mRecyclerView != null) {
        return this.mRecyclerView.isFocused();
      }
      return false;
    }
    
    public final boolean isItemPrefetchEnabled()
    {
      return this.mItemPrefetchEnabled;
    }
    
    public boolean isLayoutHierarchical(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      return false;
    }
    
    public boolean isMeasurementCacheEnabled()
    {
      return this.mMeasurementCacheEnabled;
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
    
    public void layoutDecoratedWithMargins(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
      Rect localRect = localLayoutParams.mDecorInsets;
      paramView.layout(localRect.left + paramInt1 + localLayoutParams.leftMargin, localRect.top + paramInt2 + localLayoutParams.topMargin, paramInt3 - localRect.right - localLayoutParams.rightMargin, paramInt4 - localRect.bottom - localLayoutParams.bottomMargin);
    }
    
    public void measureChild(View paramView, int paramInt1, int paramInt2)
    {
      RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
      Rect localRect = this.mRecyclerView.getItemDecorInsetsForChild(paramView);
      int k = localRect.left;
      int m = localRect.right;
      int i = localRect.top;
      int j = localRect.bottom;
      paramInt1 = getChildMeasureSpec(getWidth(), getWidthMode(), getPaddingLeft() + getPaddingRight() + (paramInt1 + (k + m)), localLayoutParams.width, canScrollHorizontally());
      paramInt2 = getChildMeasureSpec(getHeight(), getHeightMode(), getPaddingTop() + getPaddingBottom() + (paramInt2 + (i + j)), localLayoutParams.height, canScrollVertically());
      if (shouldMeasureChild(paramView, paramInt1, paramInt2, localLayoutParams)) {
        paramView.measure(paramInt1, paramInt2);
      }
    }
    
    public void measureChildWithMargins(View paramView, int paramInt1, int paramInt2)
    {
      RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
      Rect localRect = this.mRecyclerView.getItemDecorInsetsForChild(paramView);
      int k = localRect.left;
      int m = localRect.right;
      int i = localRect.top;
      int j = localRect.bottom;
      paramInt1 = getChildMeasureSpec(getWidth(), getWidthMode(), getPaddingLeft() + getPaddingRight() + localLayoutParams.leftMargin + localLayoutParams.rightMargin + (paramInt1 + (k + m)), localLayoutParams.width, canScrollHorizontally());
      paramInt2 = getChildMeasureSpec(getHeight(), getHeightMode(), getPaddingTop() + getPaddingBottom() + localLayoutParams.topMargin + localLayoutParams.bottomMargin + (paramInt2 + (i + j)), localLayoutParams.height, canScrollVertically());
      if (shouldMeasureChild(paramView, paramInt1, paramInt2, localLayoutParams)) {
        paramView.measure(paramInt1, paramInt2);
      }
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
    
    @CallSuper
    public void onAttachedToWindow(RecyclerView paramRecyclerView) {}
    
    @Deprecated
    public void onDetachedFromWindow(RecyclerView paramRecyclerView) {}
    
    @CallSuper
    public void onDetachedFromWindow(RecyclerView paramRecyclerView, RecyclerView.Recycler paramRecycler)
    {
      onDetachedFromWindow(paramRecyclerView);
    }
    
    @Nullable
    public View onFocusSearchFailed(View paramView, int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      return null;
    }
    
    public void onInitializeAccessibilityEvent(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, AccessibilityEvent paramAccessibilityEvent)
    {
      boolean bool2 = true;
      paramRecycler = AccessibilityEventCompat.asRecord(paramAccessibilityEvent);
      if ((this.mRecyclerView == null) || (paramRecycler == null)) {
        return;
      }
      boolean bool1 = bool2;
      if (!ViewCompat.canScrollVertically(this.mRecyclerView, 1))
      {
        bool1 = bool2;
        if (!ViewCompat.canScrollVertically(this.mRecyclerView, -1))
        {
          bool1 = bool2;
          if (!ViewCompat.canScrollHorizontally(this.mRecyclerView, -1)) {
            bool1 = ViewCompat.canScrollHorizontally(this.mRecyclerView, 1);
          }
        }
      }
      paramRecycler.setScrollable(bool1);
      if (this.mRecyclerView.mAdapter != null) {
        paramRecycler.setItemCount(this.mRecyclerView.mAdapter.getItemCount());
      }
    }
    
    public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
    {
      onInitializeAccessibilityEvent(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, paramAccessibilityEvent);
    }
    
    void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      onInitializeAccessibilityNodeInfo(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, paramAccessibilityNodeInfoCompat);
    }
    
    public void onInitializeAccessibilityNodeInfo(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      if ((ViewCompat.canScrollVertically(this.mRecyclerView, -1)) || (ViewCompat.canScrollHorizontally(this.mRecyclerView, -1)))
      {
        paramAccessibilityNodeInfoCompat.addAction(8192);
        paramAccessibilityNodeInfoCompat.setScrollable(true);
      }
      if ((ViewCompat.canScrollVertically(this.mRecyclerView, 1)) || (ViewCompat.canScrollHorizontally(this.mRecyclerView, 1)))
      {
        paramAccessibilityNodeInfoCompat.addAction(4096);
        paramAccessibilityNodeInfoCompat.setScrollable(true);
      }
      paramAccessibilityNodeInfoCompat.setCollectionInfo(AccessibilityNodeInfoCompat.CollectionInfoCompat.obtain(getRowCountForAccessibility(paramRecycler, paramState), getColumnCountForAccessibility(paramRecycler, paramState), isLayoutHierarchical(paramRecycler, paramState), getSelectionModeForAccessibility(paramRecycler, paramState)));
    }
    
    public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
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
        paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(i, 1, j, 1, false, false));
        return;
        i = 0;
        break;
      }
    }
    
    void onInitializeAccessibilityNodeInfoForItem(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      if ((localViewHolder == null) || (localViewHolder.isRemoved())) {}
      while (this.mChildHelper.isHidden(localViewHolder.itemView)) {
        return;
      }
      onInitializeAccessibilityNodeInfoForItem(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, paramView, paramAccessibilityNodeInfoCompat);
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
    
    public void onLayoutCompleted(RecyclerView.State paramState) {}
    
    public void onMeasure(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt1, int paramInt2)
    {
      this.mRecyclerView.defaultOnMeasure(paramInt1, paramInt2);
    }
    
    public boolean onRequestChildFocus(RecyclerView paramRecyclerView, RecyclerView.State paramState, View paramView1, View paramView2)
    {
      return onRequestChildFocus(paramRecyclerView, paramView1, paramView2);
    }
    
    @Deprecated
    public boolean onRequestChildFocus(RecyclerView paramRecyclerView, View paramView1, View paramView2)
    {
      if (!isSmoothScrolling()) {
        return paramRecyclerView.isComputingLayout();
      }
      return true;
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
        if (ViewCompat.canScrollVertically(this.mRecyclerView, -1)) {
          i = -(getHeight() - getPaddingTop() - getPaddingBottom());
        }
        paramInt = i;
        if (ViewCompat.canScrollHorizontally(this.mRecyclerView, -1))
        {
          j = -(getWidth() - getPaddingLeft() - getPaddingRight());
          paramInt = i;
          continue;
          i = m;
          if (ViewCompat.canScrollVertically(this.mRecyclerView, 1)) {
            i = getHeight() - getPaddingTop() - getPaddingBottom();
          }
          paramInt = i;
          if (ViewCompat.canScrollHorizontally(this.mRecyclerView, 1))
          {
            j = getWidth() - getPaddingLeft() - getPaddingRight();
            paramInt = i;
          }
        }
      }
      this.mRecyclerView.scrollBy(j, paramInt);
      return true;
    }
    
    public boolean performAccessibilityActionForItem(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, View paramView, int paramInt, Bundle paramBundle)
    {
      return false;
    }
    
    boolean performAccessibilityActionForItem(View paramView, int paramInt, Bundle paramBundle)
    {
      return performAccessibilityActionForItem(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, paramView, paramInt, paramBundle);
    }
    
    public void postOnAnimation(Runnable paramRunnable)
    {
      if (this.mRecyclerView != null) {
        ViewCompat.postOnAnimation(this.mRecyclerView, paramRunnable);
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
      int i4 = paramView.getLeft() + paramRect.left - paramView.getScrollX();
      int n = paramView.getTop() + paramRect.top - paramView.getScrollY();
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
            break label217;
          }
          label154:
          if ((i == 0) && (j == 0)) {
            break label243;
          }
          if (!paramBoolean) {
            break label232;
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
        label217:
        j = Math.min(n - m, i1);
        break label154;
        label232:
        paramRecyclerView.smoothScrollBy(i, j);
      }
      label243:
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
    
    public void setAutoMeasureEnabled(boolean paramBoolean)
    {
      this.mAutoMeasure = paramBoolean;
    }
    
    void setExactMeasureSpecsFrom(RecyclerView paramRecyclerView)
    {
      setMeasureSpecs(View.MeasureSpec.makeMeasureSpec(paramRecyclerView.getWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(paramRecyclerView.getHeight(), 1073741824));
    }
    
    public final void setItemPrefetchEnabled(boolean paramBoolean)
    {
      if (paramBoolean != this.mItemPrefetchEnabled)
      {
        this.mItemPrefetchEnabled = paramBoolean;
        if (this.mRecyclerView != null) {
          this.mRecyclerView.mRecycler.updateViewCacheSize();
        }
      }
    }
    
    void setMeasureSpecs(int paramInt1, int paramInt2)
    {
      this.mWidth = View.MeasureSpec.getSize(paramInt1);
      this.mWidthMode = View.MeasureSpec.getMode(paramInt1);
      if ((this.mWidthMode != 0) || (RecyclerView.ALLOW_SIZE_IN_UNSPECIFIED_SPEC)) {}
      for (;;)
      {
        this.mHeight = View.MeasureSpec.getSize(paramInt2);
        this.mHeightMode = View.MeasureSpec.getMode(paramInt2);
        if ((this.mHeightMode == 0) && (!RecyclerView.ALLOW_SIZE_IN_UNSPECIFIED_SPEC)) {
          break;
        }
        return;
        this.mWidth = 0;
      }
      this.mHeight = 0;
    }
    
    public void setMeasuredDimension(int paramInt1, int paramInt2)
    {
      RecyclerView.-wrap3(this.mRecyclerView, paramInt1, paramInt2);
    }
    
    public void setMeasuredDimension(Rect paramRect, int paramInt1, int paramInt2)
    {
      int i = paramRect.width();
      int j = getPaddingLeft();
      int k = getPaddingRight();
      int m = paramRect.height();
      int n = getPaddingTop();
      int i1 = getPaddingBottom();
      setMeasuredDimension(chooseSize(paramInt1, i + j + k, getMinimumWidth()), chooseSize(paramInt2, m + n + i1, getMinimumHeight()));
    }
    
    void setMeasuredDimensionFromChildren(int paramInt1, int paramInt2)
    {
      int i3 = getChildCount();
      if (i3 == 0)
      {
        this.mRecyclerView.defaultOnMeasure(paramInt1, paramInt2);
        return;
      }
      int i1 = Integer.MAX_VALUE;
      int j = Integer.MAX_VALUE;
      int n = Integer.MIN_VALUE;
      int i = Integer.MIN_VALUE;
      int k = 0;
      while (k < i3)
      {
        View localView = getChildAt(k);
        Object localObject = (RecyclerView.LayoutParams)localView.getLayoutParams();
        localObject = this.mRecyclerView.mTempRect;
        getDecoratedBoundsWithMargins(localView, (Rect)localObject);
        int m = i1;
        if (((Rect)localObject).left < i1) {
          m = ((Rect)localObject).left;
        }
        i1 = n;
        if (((Rect)localObject).right > n) {
          i1 = ((Rect)localObject).right;
        }
        int i2 = j;
        if (((Rect)localObject).top < j) {
          i2 = ((Rect)localObject).top;
        }
        j = i;
        if (((Rect)localObject).bottom > i) {
          j = ((Rect)localObject).bottom;
        }
        k += 1;
        n = i1;
        i = j;
        i1 = m;
        j = i2;
      }
      this.mRecyclerView.mTempRect.set(i1, j, n, i);
      setMeasuredDimension(this.mRecyclerView.mTempRect, paramInt1, paramInt2);
    }
    
    public void setMeasurementCacheEnabled(boolean paramBoolean)
    {
      this.mMeasurementCacheEnabled = paramBoolean;
    }
    
    void setRecyclerView(RecyclerView paramRecyclerView)
    {
      if (paramRecyclerView == null)
      {
        this.mRecyclerView = null;
        this.mChildHelper = null;
        this.mWidth = 0;
      }
      for (this.mHeight = 0;; this.mHeight = paramRecyclerView.getHeight())
      {
        this.mWidthMode = 1073741824;
        this.mHeightMode = 1073741824;
        return;
        this.mRecyclerView = paramRecyclerView;
        this.mChildHelper = paramRecyclerView.mChildHelper;
        this.mWidth = paramRecyclerView.getWidth();
      }
    }
    
    boolean shouldMeasureChild(View paramView, int paramInt1, int paramInt2, RecyclerView.LayoutParams paramLayoutParams)
    {
      boolean bool2 = true;
      boolean bool1 = bool2;
      if (!paramView.isLayoutRequested())
      {
        bool1 = bool2;
        if (this.mMeasurementCacheEnabled)
        {
          bool1 = bool2;
          if (isMeasurementUpToDate(paramView.getWidth(), paramInt1, paramLayoutParams.width))
          {
            bool1 = bool2;
            if (isMeasurementUpToDate(paramView.getHeight(), paramInt2, paramLayoutParams.height)) {
              bool1 = false;
            }
          }
        }
      }
      return bool1;
    }
    
    boolean shouldMeasureTwice()
    {
      return false;
    }
    
    boolean shouldReMeasureChild(View paramView, int paramInt1, int paramInt2, RecyclerView.LayoutParams paramLayoutParams)
    {
      boolean bool2 = true;
      boolean bool1 = bool2;
      if (this.mMeasurementCacheEnabled)
      {
        bool1 = bool2;
        if (isMeasurementUpToDate(paramView.getMeasuredWidth(), paramInt1, paramLayoutParams.width))
        {
          bool1 = bool2;
          if (isMeasurementUpToDate(paramView.getMeasuredHeight(), paramInt2, paramLayoutParams.height)) {
            bool1 = false;
          }
        }
      }
      return bool1;
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
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
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
    
    @Deprecated
    public int getViewPosition()
    {
      return this.mViewHolder.getPosition();
    }
    
    public boolean isItemChanged()
    {
      return this.mViewHolder.isUpdated();
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
  
  public static abstract class OnFlingListener
  {
    public abstract boolean onFling(int paramInt1, int paramInt2);
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
    static final int DEFAULT_CACHE_SIZE = 2;
    final ArrayList<RecyclerView.ViewHolder> mAttachedScrap = new ArrayList();
    final ArrayList<RecyclerView.ViewHolder> mCachedViews = new ArrayList();
    ArrayList<RecyclerView.ViewHolder> mChangedScrap = null;
    private RecyclerView.RecycledViewPool mRecyclerPool;
    private int mRequestedCacheMax = 2;
    private final List<RecyclerView.ViewHolder> mUnmodifiableAttachedScrap = Collections.unmodifiableList(this.mAttachedScrap);
    private RecyclerView.ViewCacheExtension mViewCacheExtension;
    int mViewCacheMax = 2;
    
    public Recycler() {}
    
    private void attachAccessibilityDelegate(View paramView)
    {
      if (RecyclerView.this.isAccessibilityEnabled())
      {
        if (ViewCompat.getImportantForAccessibility(paramView) == 0) {
          ViewCompat.setImportantForAccessibility(paramView, 1);
        }
        if (!ViewCompat.hasAccessibilityDelegate(paramView)) {
          ViewCompat.setAccessibilityDelegate(paramView, RecyclerView.this.mAccessibilityDelegate.getItemDelegate());
        }
      }
    }
    
    private void invalidateDisplayListInt(RecyclerView.ViewHolder paramViewHolder)
    {
      if ((paramViewHolder.itemView instanceof ViewGroup)) {
        invalidateDisplayListInt((ViewGroup)paramViewHolder.itemView, false);
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
    
    void addViewHolderToRecycledViewPool(RecyclerView.ViewHolder paramViewHolder)
    {
      ViewCompat.setAccessibilityDelegate(paramViewHolder.itemView, null);
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
      if ((i < 0) || (i >= RecyclerView.this.mAdapter.getItemCount())) {
        throw new IndexOutOfBoundsException("Inconsistency detected. Invalid item position " + paramInt + "(offset:" + i + ")." + "state:" + RecyclerView.this.mState.getItemCount());
      }
      localViewHolder.mOwnerRecyclerView = RecyclerView.this;
      RecyclerView.this.mAdapter.bindViewHolder(localViewHolder, i);
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
      if (this.mChangedScrap != null) {
        this.mChangedScrap.clear();
      }
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
      if (RecyclerView.this.mRecyclerListener != null) {
        RecyclerView.this.mRecyclerListener.onViewRecycled(paramViewHolder);
      }
      if (RecyclerView.this.mAdapter != null) {
        RecyclerView.this.mAdapter.onViewRecycled(paramViewHolder);
      }
      if (RecyclerView.this.mState != null) {
        RecyclerView.this.mViewInfoStore.removeViewHolder(paramViewHolder);
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
      if (RecyclerView.this.mAdapter.hasStableIds())
      {
        paramInt = RecyclerView.this.mAdapterHelper.findPositionOffset(paramInt);
        if ((paramInt > 0) && (paramInt < RecyclerView.this.mAdapter.getItemCount()))
        {
          long l = RecyclerView.this.mAdapter.getItemId(paramInt);
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
      RecyclerView.ViewHolder localViewHolder;
      if (i < j)
      {
        localObject = (RecyclerView.ViewHolder)this.mAttachedScrap.get(i);
        if ((((RecyclerView.ViewHolder)localObject).wasReturnedFromScrap()) || (((RecyclerView.ViewHolder)localObject).getLayoutPosition() != paramInt1) || (((RecyclerView.ViewHolder)localObject).isInvalid())) {}
        while ((!RecyclerView.this.mState.mInPreLayout) && (((RecyclerView.ViewHolder)localObject).isRemoved()))
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
        if (paramBoolean) {
          break label292;
        }
        localObject = RecyclerView.this.mChildHelper.findHiddenNonRemovedView(paramInt1, paramInt2);
        if (localObject == null) {
          break label292;
        }
        localViewHolder = RecyclerView.getChildViewHolderInt((View)localObject);
        RecyclerView.this.mChildHelper.unhide((View)localObject);
        paramInt1 = RecyclerView.this.mChildHelper.indexOfChild((View)localObject);
        if (paramInt1 != -1) {
          break label264;
        }
        throw new IllegalStateException("layout index should not be -1 after unhiding a view:" + localViewHolder);
      }
      ((RecyclerView.ViewHolder)localObject).addFlags(32);
      return (RecyclerView.ViewHolder)localObject;
      label264:
      RecyclerView.this.mChildHelper.detachViewFromParent(paramInt1);
      scrapView((View)localObject);
      localViewHolder.addFlags(8224);
      return localViewHolder;
      label292:
      i = this.mCachedViews.size();
      paramInt2 = 0;
      while (paramInt2 < i)
      {
        localObject = (RecyclerView.ViewHolder)this.mCachedViews.get(paramInt2);
        if ((!((RecyclerView.ViewHolder)localObject).isInvalid()) && (((RecyclerView.ViewHolder)localObject).getLayoutPosition() == paramInt1))
        {
          if (!paramBoolean) {
            this.mCachedViews.remove(paramInt2);
          }
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
        if ((k >= 0) && (k < RecyclerView.this.mAdapter.getItemCount())) {
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
      int m = RecyclerView.this.mAdapter.getItemViewType(k);
      j = i;
      localObject2 = localObject1;
      if (RecyclerView.this.mAdapter.hasStableIds())
      {
        localObject1 = getScrapViewForId(RecyclerView.this.mAdapter.getItemId(k), m, paramBoolean);
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
          if (RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST)
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
        localObject2 = RecyclerView.this.mAdapter.createViewHolder(RecyclerView.this, m);
        k = j;
      }
      label597:
      if ((k == 0) || (RecyclerView.this.mState.isPreLayout()))
      {
        paramBoolean = false;
        if ((!RecyclerView.this.mState.isPreLayout()) || (!((RecyclerView.ViewHolder)localObject2).isBound())) {
          break label789;
        }
        ((RecyclerView.ViewHolder)localObject2).mPreLayoutPosition = paramInt;
        label644:
        localObject1 = ((RecyclerView.ViewHolder)localObject2).itemView.getLayoutParams();
        if (localObject1 != null) {
          break label887;
        }
        localObject1 = (RecyclerView.LayoutParams)RecyclerView.this.generateDefaultLayoutParams();
        ((RecyclerView.ViewHolder)localObject2).itemView.setLayoutParams((ViewGroup.LayoutParams)localObject1);
        label681:
        ((RecyclerView.LayoutParams)localObject1).mViewHolder = ((RecyclerView.ViewHolder)localObject2);
        if (k == 0) {
          break label936;
        }
      }
      for (;;)
      {
        ((RecyclerView.LayoutParams)localObject1).mPendingInvalidate = paramBoolean;
        return ((RecyclerView.ViewHolder)localObject2).itemView;
        if (!((RecyclerView.ViewHolder)localObject2).hasAnyOfTheFlags(8192)) {
          break;
        }
        ((RecyclerView.ViewHolder)localObject2).setFlags(0, 8192);
        if (!RecyclerView.this.mState.mRunSimpleAnimations) {
          break;
        }
        i = RecyclerView.ItemAnimator.buildAdapterChangeFlagsForAnimations((RecyclerView.ViewHolder)localObject2);
        localObject1 = RecyclerView.this.mItemAnimator.recordPreLayoutInformation(RecyclerView.this.mState, (RecyclerView.ViewHolder)localObject2, i | 0x1000, ((RecyclerView.ViewHolder)localObject2).getUnmodifiedPayloads());
        RecyclerView.this.recordAnimationInfoIfBouncedHiddenView((RecyclerView.ViewHolder)localObject2, (RecyclerView.ItemAnimator.ItemHolderInfo)localObject1);
        break;
        label789:
        if ((((RecyclerView.ViewHolder)localObject2).isBound()) && (!((RecyclerView.ViewHolder)localObject2).needsUpdate()) && (!((RecyclerView.ViewHolder)localObject2).isInvalid())) {
          break label644;
        }
        i = RecyclerView.this.mAdapterHelper.findPositionOffset(paramInt);
        ((RecyclerView.ViewHolder)localObject2).mOwnerRecyclerView = RecyclerView.this;
        RecyclerView.this.mAdapter.bindViewHolder((RecyclerView.ViewHolder)localObject2, i);
        attachAccessibilityDelegate(((RecyclerView.ViewHolder)localObject2).itemView);
        boolean bool = true;
        paramBoolean = bool;
        if (!RecyclerView.this.mState.isPreLayout()) {
          break label644;
        }
        ((RecyclerView.ViewHolder)localObject2).mPreLayoutPosition = paramInt;
        paramBoolean = bool;
        break label644;
        label887:
        if (!RecyclerView.this.checkLayoutParams((ViewGroup.LayoutParams)localObject1))
        {
          localObject1 = (RecyclerView.LayoutParams)RecyclerView.this.generateLayoutParams((ViewGroup.LayoutParams)localObject1);
          ((RecyclerView.ViewHolder)localObject2).itemView.setLayoutParams((ViewGroup.LayoutParams)localObject1);
          break label681;
        }
        localObject1 = (RecyclerView.LayoutParams)localObject1;
        break label681;
        label936:
        paramBoolean = false;
      }
    }
    
    boolean isPrefetchPositionAttached(int paramInt)
    {
      int j = RecyclerView.this.mChildHelper.getUnfilteredChildCount();
      int i = 0;
      while (i < j)
      {
        if (RecyclerView.getChildViewHolderInt(RecyclerView.this.mChildHelper.getUnfilteredChildAt(i)).mPosition == paramInt) {
          return true;
        }
        i += 1;
      }
      return false;
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
      if ((RecyclerView.this.mAdapter != null) && (RecyclerView.this.mAdapter.hasStableIds()))
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
        if ((localViewHolder != null) && (localViewHolder.mPosition >= paramInt1)) {
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
          if (localViewHolder.mPosition < paramInt1 + paramInt2) {
            break label63;
          }
          localViewHolder.offsetPosition(-paramInt2, paramBoolean);
        }
        for (;;)
        {
          i -= 1;
          break;
          label63:
          if (localViewHolder.mPosition >= paramInt1)
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
    
    void prefetch(int[] paramArrayOfInt, int paramInt)
    {
      if (paramInt == 0) {
        return;
      }
      int i = paramArrayOfInt[(paramInt - 1)];
      if (i < 0) {
        throw new IllegalArgumentException("Recycler requested to prefetch invalid view " + i);
      }
      View localView = null;
      if (!isPrefetchPositionAttached(i)) {
        localView = getViewForPosition(i);
      }
      if (paramInt > 1) {
        prefetch(paramArrayOfInt, paramInt - 1);
      }
      if (localView != null) {
        recycleView(localView);
      }
    }
    
    void quickRecycleScrapView(View paramView)
    {
      paramView = RecyclerView.getChildViewHolderInt(paramView);
      RecyclerView.ViewHolder.-set2(paramView, null);
      RecyclerView.ViewHolder.-set0(paramView, false);
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
      if (RecyclerView.-get0()) {
        RecyclerView.this.mViewPrefetcher.clearPrefetchPositions();
      }
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
      int j;
      int k;
      int i;
      if ((RecyclerView.this.mAdapter != null) && (bool2))
      {
        bool1 = RecyclerView.this.mAdapter.onFailedToRecycleView(paramViewHolder);
        j = 0;
        int n = 0;
        int m = 0;
        if (!bool1)
        {
          k = m;
          if (!paramViewHolder.isRecyclable()) {}
        }
        else
        {
          i = n;
          if (this.mViewCacheMax > 0)
          {
            if (!paramViewHolder.hasAnyOfTheFlags(14)) {
              break label259;
            }
            i = n;
          }
          label212:
          j = i;
          k = m;
          if (i == 0)
          {
            addViewHolderToRecycledViewPool(paramViewHolder);
            k = 1;
            j = i;
          }
        }
        RecyclerView.this.mViewInfoStore.removeViewHolder(paramViewHolder);
        if ((j == 0) && (k == 0)) {
          break label397;
        }
      }
      label259:
      label345:
      label397:
      while (!bool2)
      {
        return;
        bool1 = false;
        break;
        j = this.mCachedViews.size();
        i = j;
        if (j >= this.mViewCacheMax)
        {
          i = j;
          if (j > 0)
          {
            recycleCachedViewAt(0);
            i = j - 1;
          }
        }
        j = i;
        k = j;
        if (RecyclerView.-get0())
        {
          k = j;
          if (i > 0)
          {
            if (!RecyclerView.this.mViewPrefetcher.lastPrefetchIncludedPosition(paramViewHolder.mPosition)) {
              break label345;
            }
            k = j;
          }
        }
        this.mCachedViews.add(k, paramViewHolder);
        i = 1;
        break label212;
        i -= 1;
        for (;;)
        {
          if (i >= 0)
          {
            j = ((RecyclerView.ViewHolder)this.mCachedViews.get(i)).mPosition;
            if (RecyclerView.this.mViewPrefetcher.lastPrefetchIncludedPosition(j)) {}
          }
          else
          {
            k = i + 1;
            break;
          }
          i -= 1;
        }
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
      if ((paramView.hasAnyOfTheFlags(12)) || (!paramView.isUpdated()) || (RecyclerView.this.canReuseUpdatedViewHolder(paramView)))
      {
        if ((!paramView.isInvalid()) || (paramView.isRemoved())) {}
        while (RecyclerView.this.mAdapter.hasStableIds())
        {
          paramView.setScrapContainer(this, false);
          this.mAttachedScrap.add(paramView);
          return;
        }
        throw new IllegalArgumentException("Called scrap view with an invalid view. Invalid views cannot be reused from scrap, they should rebound from recycler pool.");
      }
      if (this.mChangedScrap == null) {
        this.mChangedScrap = new ArrayList();
      }
      paramView.setScrapContainer(this, true);
      this.mChangedScrap.add(paramView);
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
      this.mRequestedCacheMax = paramInt;
      updateViewCacheSize();
    }
    
    void unscrapView(RecyclerView.ViewHolder paramViewHolder)
    {
      if (RecyclerView.ViewHolder.-get1(paramViewHolder)) {
        this.mChangedScrap.remove(paramViewHolder);
      }
      for (;;)
      {
        RecyclerView.ViewHolder.-set2(paramViewHolder, null);
        RecyclerView.ViewHolder.-set0(paramViewHolder, false);
        paramViewHolder.clearReturnedFromScrapFlag();
        return;
        this.mAttachedScrap.remove(paramViewHolder);
      }
    }
    
    void updateViewCacheSize()
    {
      int j = 0;
      int i = j;
      if (RecyclerView.this.mLayout != null)
      {
        i = j;
        if (RecyclerView.-get0()) {
          if (!RecyclerView.this.mLayout.isItemPrefetchEnabled()) {
            break label96;
          }
        }
      }
      label96:
      for (i = RecyclerView.this.mLayout.getItemPrefetchCount();; i = 0)
      {
        this.mViewCacheMax = (this.mRequestedCacheMax + i);
        i = this.mCachedViews.size() - 1;
        while ((i >= 0) && (this.mCachedViews.size() > this.mViewCacheMax))
        {
          recycleCachedViewAt(i);
          i -= 1;
        }
      }
    }
    
    boolean validateViewHolderForOffsetPosition(RecyclerView.ViewHolder paramViewHolder)
    {
      if (paramViewHolder.isRemoved()) {
        return RecyclerView.this.mState.isPreLayout();
      }
      if ((paramViewHolder.mPosition < 0) || (paramViewHolder.mPosition >= RecyclerView.this.mAdapter.getItemCount())) {
        throw new IndexOutOfBoundsException("Inconsistency detected. Invalid view holder adapter position" + paramViewHolder);
      }
      if ((!RecyclerView.this.mState.isPreLayout()) && (RecyclerView.this.mAdapter.getItemViewType(paramViewHolder.mPosition) != paramViewHolder.getItemViewType())) {
        return false;
      }
      if (RecyclerView.this.mAdapter.hasStableIds()) {
        return paramViewHolder.getItemId() == RecyclerView.this.mAdapter.getItemId(paramViewHolder.mPosition);
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
    RecyclerViewDataObserver() {}
    
    public void onChanged()
    {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      if (RecyclerView.this.mAdapter.hasStableIds())
      {
        RecyclerView.this.mState.mStructureChanged = true;
        RecyclerView.this.setDataSetChangedAfterLayout();
      }
      for (;;)
      {
        if (!RecyclerView.this.mAdapterHelper.hasPendingUpdates()) {
          RecyclerView.this.requestLayout();
        }
        return;
        RecyclerView.this.mState.mStructureChanged = true;
        RecyclerView.this.setDataSetChangedAfterLayout();
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
      if ((RecyclerView.POST_UPDATES_ON_ANIMATION) && (RecyclerView.this.mHasFixedSize) && (RecyclerView.this.mIsAttached))
      {
        ViewCompat.postOnAnimation(RecyclerView.this, RecyclerView.this.mUpdateChildViewsRunnable);
        return;
      }
      RecyclerView.this.mAdapterUpdateDuringMeasure = true;
      RecyclerView.this.requestLayout();
    }
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public static class SavedState
    extends AbsSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks()
    {
      public RecyclerView.SavedState createFromParcel(Parcel paramAnonymousParcel, ClassLoader paramAnonymousClassLoader)
      {
        return new RecyclerView.SavedState(paramAnonymousParcel, paramAnonymousClassLoader);
      }
      
      public RecyclerView.SavedState[] newArray(int paramAnonymousInt)
      {
        return new RecyclerView.SavedState[paramAnonymousInt];
      }
    });
    Parcelable mLayoutState;
    
    SavedState(Parcel paramParcel, ClassLoader paramClassLoader)
    {
      super(paramClassLoader);
      if (paramClassLoader != null) {}
      for (;;)
      {
        this.mLayoutState = paramParcel.readParcelable(paramClassLoader);
        return;
        paramClassLoader = RecyclerView.LayoutManager.class.getClassLoader();
      }
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    void copyFrom(SavedState paramSavedState)
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
          this.mRecyclingAction.runIfNecessary(localRecyclerView);
          stop();
        }
      }
      for (;;)
      {
        if (this.mRunning)
        {
          onSeekTargetStep(paramInt1, paramInt2, localRecyclerView.mState, this.mRecyclingAction);
          boolean bool = this.mRecyclingAction.hasJumpTarget();
          this.mRecyclingAction.runIfNecessary(localRecyclerView);
          if (bool)
          {
            if (!this.mRunning) {
              break label170;
            }
            this.mPendingInitialRun = true;
            localRecyclerView.mViewFlinger.postOnAnimation();
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
      return this.mRecyclerView.mLayout.findViewByPosition(paramInt);
    }
    
    public int getChildCount()
    {
      return this.mRecyclerView.mLayout.getChildCount();
    }
    
    public int getChildPosition(View paramView)
    {
      return this.mRecyclerView.getChildLayoutPosition(paramView);
    }
    
    @Nullable
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
      RecyclerView.State.-set0(this.mRecyclerView.mState, this.mTargetPosition);
      this.mRunning = true;
      this.mPendingInitialRun = true;
      this.mTargetView = findViewByPosition(getTargetPosition());
      onStart();
      this.mRecyclerView.mViewFlinger.postOnAnimation();
    }
    
    protected final void stop()
    {
      if (!this.mRunning) {
        return;
      }
      onStop();
      RecyclerView.State.-set0(this.mRecyclerView.mState, -1);
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
      
      void runIfNecessary(RecyclerView paramRecyclerView)
      {
        if (this.mJumpToPosition >= 0)
        {
          int i = this.mJumpToPosition;
          this.mJumpToPosition = -1;
          paramRecyclerView.jumpToPositionForSmoothScroller(i);
          this.changed = false;
          return;
        }
        if (this.changed)
        {
          validate();
          if (this.mInterpolator == null) {
            if (this.mDuration == Integer.MIN_VALUE) {
              paramRecyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy);
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
            paramRecyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration);
            continue;
            paramRecyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration, this.mInterpolator);
          }
        }
        this.consecutiveUpdates = 0;
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
    
    public static abstract interface ScrollVectorProvider
    {
      public abstract PointF computeScrollVectorForPosition(int paramInt);
    }
  }
  
  public static class State
  {
    static final int STEP_ANIMATIONS = 4;
    static final int STEP_LAYOUT = 2;
    static final int STEP_START = 1;
    private SparseArray<Object> mData;
    int mDeletedInvisibleItemCountSincePreviousLayout = 0;
    long mFocusedItemId;
    int mFocusedItemPosition;
    int mFocusedSubChildId;
    boolean mInPreLayout = false;
    boolean mIsMeasuring = false;
    int mItemCount = 0;
    int mLayoutStep = 1;
    int mPreviousLayoutItemCount = 0;
    boolean mRunPredictiveAnimations = false;
    boolean mRunSimpleAnimations = false;
    boolean mStructureChanged = false;
    private int mTargetPosition = -1;
    boolean mTrackOldChangeHolders = false;
    
    void assertLayoutStep(int paramInt)
    {
      if ((this.mLayoutStep & paramInt) == 0) {
        throw new IllegalStateException("Layout state should be one of " + Integer.toBinaryString(paramInt) + " but it is " + Integer.toBinaryString(this.mLayoutStep));
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
    
    public boolean isMeasuring()
    {
      return this.mIsMeasuring;
    }
    
    public boolean isPreLayout()
    {
      return this.mInPreLayout;
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
    
    State reset()
    {
      this.mTargetPosition = -1;
      if (this.mData != null) {
        this.mData.clear();
      }
      this.mItemCount = 0;
      this.mStructureChanged = false;
      this.mIsMeasuring = false;
      return this;
    }
    
    public String toString()
    {
      return "State{mTargetPosition=" + this.mTargetPosition + ", mData=" + this.mData + ", mItemCount=" + this.mItemCount + ", mPreviousLayoutItemCount=" + this.mPreviousLayoutItemCount + ", mDeletedInvisibleItemCountSincePreviousLayout=" + this.mDeletedInvisibleItemCountSincePreviousLayout + ", mStructureChanged=" + this.mStructureChanged + ", mInPreLayout=" + this.mInPreLayout + ", mRunSimpleAnimations=" + this.mRunSimpleAnimations + ", mRunPredictiveAnimations=" + this.mRunPredictiveAnimations + '}';
    }
    
    public boolean willRunPredictiveAnimations()
    {
      return this.mRunPredictiveAnimations;
    }
    
    public boolean willRunSimpleAnimations()
    {
      return this.mRunSimpleAnimations;
    }
    
    @Retention(RetentionPolicy.SOURCE)
    static @interface LayoutState {}
  }
  
  public static abstract class ViewCacheExtension
  {
    public abstract View getViewForPositionAndType(RecyclerView.Recycler paramRecycler, int paramInt1, int paramInt2);
  }
  
  private class ViewFlinger
    implements Runnable
  {
    private boolean mEatRunOnAnimationRequest = false;
    private Interpolator mInterpolator = RecyclerView.sQuinticInterpolator;
    private int mLastFlingX;
    private int mLastFlingY;
    private boolean mReSchedulePostAnimationCallback = false;
    private ScrollerCompat mScroller = ScrollerCompat.create(RecyclerView.this.getContext(), RecyclerView.sQuinticInterpolator);
    
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
      RecyclerView.this.setScrollState(2);
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
      ViewCompat.postOnAnimation(RecyclerView.this, this);
    }
    
    public void run()
    {
      if (RecyclerView.this.mLayout == null)
      {
        stop();
        return;
      }
      disableRunOnAnimationRequests();
      RecyclerView.this.consumePendingUpdateOperations();
      ScrollerCompat localScrollerCompat = this.mScroller;
      RecyclerView.SmoothScroller localSmoothScroller = RecyclerView.this.mLayout.mSmoothScroller;
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
      if (localScrollerCompat.computeScrollOffset())
      {
        int i6 = localScrollerCompat.getCurrX();
        int i7 = localScrollerCompat.getCurrY();
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
        if (RecyclerView.this.mAdapter != null)
        {
          RecyclerView.this.eatRequestLayout();
          RecyclerView.this.onEnterLayoutOrScroll();
          TraceCompat.beginSection("RV Scroll");
          if (i4 != 0)
          {
            i = RecyclerView.this.mLayout.scrollHorizontallyBy(i4, RecyclerView.this.mRecycler, RecyclerView.this.mState);
            j = i4 - i;
          }
          if (i5 != 0)
          {
            m = RecyclerView.this.mLayout.scrollVerticallyBy(i5, RecyclerView.this.mRecycler, RecyclerView.this.mState);
            k = i5 - m;
          }
          TraceCompat.endSection();
          RecyclerView.this.repositionShadowingViews();
          RecyclerView.this.onExitLayoutOrScroll();
          RecyclerView.this.resumeRequestLayout(false);
          n = i;
          i1 = j;
          i2 = k;
          i3 = m;
          if (localSmoothScroller != null)
          {
            if (!localSmoothScroller.isPendingInitialRun()) {
              break label603;
            }
            i3 = m;
            i2 = k;
            i1 = j;
            n = i;
          }
        }
        if (!RecyclerView.this.mItemDecorations.isEmpty()) {
          RecyclerView.this.invalidate();
        }
        if (RecyclerView.this.getOverScrollMode() != 2) {
          RecyclerView.this.considerReleasingGlowsOnScroll(i4, i5);
        }
        if ((i1 != 0) || (i2 != 0))
        {
          k = (int)localScrollerCompat.getCurrVelocity();
          i = 0;
          if (i1 != i6)
          {
            if (i1 >= 0) {
              break label739;
            }
            i = -k;
          }
          label362:
          j = 0;
          if (i2 != i7)
          {
            if (i2 >= 0) {
              break label754;
            }
            j = -k;
          }
          label379:
          if (RecyclerView.this.getOverScrollMode() != 2) {
            RecyclerView.this.absorbGlows(i, j);
          }
          if ((i == 0) && (i1 != i6)) {
            break label769;
          }
          label410:
          if ((j == 0) && (i2 != i7)) {
            break label780;
          }
          label421:
          localScrollerCompat.abortAnimation();
        }
        label426:
        if ((n != 0) || (i3 != 0)) {
          RecyclerView.this.dispatchOnScrolled(n, i3);
        }
        if (!RecyclerView.-wrap0(RecyclerView.this)) {
          RecyclerView.this.invalidate();
        }
        if ((i5 == 0) || (!RecyclerView.this.mLayout.canScrollVertically())) {
          break label796;
        }
        if (i3 != i5) {
          break label791;
        }
        i = 1;
        label491:
        if ((i4 == 0) || (!RecyclerView.this.mLayout.canScrollHorizontally())) {
          break label806;
        }
        if (n != i4) {
          break label801;
        }
        j = 1;
        label518:
        if ((i4 != 0) || (i5 != 0)) {
          break label811;
        }
        label528:
        i = 1;
        label530:
        if ((localScrollerCompat.isFinished()) || (i == 0)) {
          break label818;
        }
        postOnAnimation();
        if (RecyclerView.-get0()) {
          RecyclerView.this.mViewPrefetcher.postFromTraversal(i4, i5);
        }
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
        label603:
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
        label739:
        if (i1 > 0)
        {
          i = k;
          break label362;
        }
        i = 0;
        break label362;
        label754:
        if (i2 > 0)
        {
          j = k;
          break label379;
        }
        j = 0;
        break label379;
        label769:
        if (localScrollerCompat.getFinalX() != 0) {
          break label426;
        }
        break label410;
        label780:
        if (localScrollerCompat.getFinalY() != 0) {
          break label426;
        }
        break label421;
        label791:
        i = 0;
        break label491;
        label796:
        i = 0;
        break label491;
        label801:
        j = 0;
        break label518;
        label806:
        j = 0;
        break label518;
        label811:
        if (j != 0) {
          break label528;
        }
        break label530;
        label818:
        RecyclerView.this.setScrollState(0);
        if (RecyclerView.-get0()) {
          RecyclerView.this.mViewPrefetcher.clearPrefetchPositions();
        }
      }
    }
    
    public void smoothScrollBy(int paramInt1, int paramInt2)
    {
      smoothScrollBy(paramInt1, paramInt2, 0, 0);
    }
    
    public void smoothScrollBy(int paramInt1, int paramInt2, int paramInt3)
    {
      smoothScrollBy(paramInt1, paramInt2, paramInt3, RecyclerView.sQuinticInterpolator);
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
        this.mScroller = ScrollerCompat.create(RecyclerView.this.getContext(), paramInterpolator);
      }
      RecyclerView.this.setScrollState(2);
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
    static final int FLAG_APPEARED_IN_PRE_LAYOUT = 4096;
    static final int FLAG_BOUNCED_FROM_HIDDEN_LIST = 8192;
    static final int FLAG_BOUND = 1;
    static final int FLAG_IGNORE = 128;
    static final int FLAG_INVALID = 4;
    static final int FLAG_MOVED = 2048;
    static final int FLAG_NOT_RECYCLABLE = 16;
    static final int FLAG_REMOVED = 8;
    static final int FLAG_RETURNED_FROM_SCRAP = 32;
    static final int FLAG_TMP_DETACHED = 256;
    static final int FLAG_UPDATE = 2;
    private static final List<Object> FULLUPDATE_PAYLOADS = Collections.EMPTY_LIST;
    static final int PENDING_ACCESSIBILITY_STATE_NOT_SET = -1;
    public final View itemView;
    private int mFlags;
    private boolean mInChangeScrap = false;
    private int mIsRecyclableCount = 0;
    long mItemId = -1L;
    int mItemViewType = -1;
    int mOldPosition = -1;
    RecyclerView mOwnerRecyclerView;
    List<Object> mPayloads = null;
    private int mPendingAccessibilityState = -1;
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
        bool = ViewCompat.hasTransientState(this.itemView);
      }
      return bool;
    }
    
    private void onEnteredHiddenState(RecyclerView paramRecyclerView)
    {
      this.mWasImportantForAccessibilityBeforeHidden = ViewCompat.getImportantForAccessibility(this.itemView);
      paramRecyclerView.setChildImportantForAccessibilityInternal(this, 4);
    }
    
    private void onLeftHiddenState(RecyclerView paramRecyclerView)
    {
      paramRecyclerView.setChildImportantForAccessibilityInternal(this, this.mWasImportantForAccessibilityBeforeHidden);
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
      return this.mOwnerRecyclerView.getAdapterPositionFor(this);
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
      return ((this.mFlags & 0x10) == 0) && (!ViewCompat.hasTransientState(this.itemView));
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
    
    boolean isUpdated()
    {
      boolean bool = false;
      if ((this.mFlags & 0x2) != 0) {
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
      this.mPendingAccessibilityState = -1;
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
          break label65;
        }
        this.mIsRecyclableCount = 0;
        Log.e("View", "isRecyclable decremented below 0: unmatched pair of setIsRecyable() calls for " + this);
      }
      label65:
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
    
    void setScrapContainer(RecyclerView.Recycler paramRecycler, boolean paramBoolean)
    {
      this.mScrapContainer = paramRecycler;
      this.mInChangeScrap = paramBoolean;
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
      StringBuilder localStringBuilder1 = new StringBuilder("ViewHolder{" + Integer.toHexString(hashCode()) + " position=" + this.mPosition + " id=" + this.mItemId + ", oldPos=" + this.mOldPosition + ", pLpos:" + this.mPreLayoutPosition);
      StringBuilder localStringBuilder2;
      if (isScrap())
      {
        localStringBuilder2 = localStringBuilder1.append(" scrap ");
        if (!this.mInChangeScrap) {
          break label282;
        }
      }
      label282:
      for (String str = "[changeScrap]";; str = "[attachedScrap]")
      {
        localStringBuilder2.append(str);
        if (isInvalid()) {
          localStringBuilder1.append(" invalid");
        }
        if (!isBound()) {
          localStringBuilder1.append(" unbound");
        }
        if (needsUpdate()) {
          localStringBuilder1.append(" update");
        }
        if (isRemoved()) {
          localStringBuilder1.append(" removed");
        }
        if (shouldIgnore()) {
          localStringBuilder1.append(" ignored");
        }
        if (isTmpDetached()) {
          localStringBuilder1.append(" tmpDetached");
        }
        if (!isRecyclable()) {
          localStringBuilder1.append(" not recyclable(").append(this.mIsRecyclableCount).append(")");
        }
        if (isAdapterPositionUnknown()) {
          localStringBuilder1.append(" undefined adapter position");
        }
        if (this.itemView.getParent() == null) {
          localStringBuilder1.append(" no parent");
        }
        localStringBuilder1.append("}");
        return localStringBuilder1.toString();
      }
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
  
  class ViewPrefetcher
    implements Runnable
  {
    private int mDx;
    private int mDy;
    int[] mItemPrefetchArray;
    long mPostTimeNanos;
    
    ViewPrefetcher() {}
    
    public void clearPrefetchPositions()
    {
      if (this.mItemPrefetchArray != null) {
        Arrays.fill(this.mItemPrefetchArray, -1);
      }
    }
    
    public boolean lastPrefetchIncludedPosition(int paramInt)
    {
      if (this.mItemPrefetchArray != null)
      {
        int i = 0;
        while (i < this.mItemPrefetchArray.length)
        {
          if (this.mItemPrefetchArray[i] == paramInt) {
            return true;
          }
          i += 1;
        }
      }
      return false;
    }
    
    public void postFromTraversal(int paramInt1, int paramInt2)
    {
      if ((RecyclerView.-get0()) && (RecyclerView.this.mAdapter != null) && (RecyclerView.this.mLayout != null) && (RecyclerView.this.mLayout.getItemPrefetchCount() > 0))
      {
        this.mDx = paramInt1;
        this.mDy = paramInt2;
        this.mPostTimeNanos = System.nanoTime();
        RecyclerView.this.post(this);
      }
    }
    
    public void run()
    {
      try
      {
        TraceCompat.beginSection("RV Prefetch");
        int i = RecyclerView.this.mLayout.getItemPrefetchCount();
        if (RecyclerView.this.mAdapter != null)
        {
          RecyclerView.LayoutManager localLayoutManager = RecyclerView.this.mLayout;
          if (localLayoutManager != null) {
            break label44;
          }
        }
        label44:
        while ((!RecyclerView.this.mLayout.isItemPrefetchEnabled()) || (i < 1) || (RecyclerView.this.hasPendingAdapterUpdates())) {
          return;
        }
        long l1 = TimeUnit.MILLISECONDS.toNanos(RecyclerView.this.getDrawingTime());
        if (l1 != 0L)
        {
          l2 = RecyclerView.sFrameIntervalNanos;
          if (l2 != 0L) {}
        }
        else
        {
          return;
        }
        long l2 = System.nanoTime();
        long l3 = RecyclerView.sFrameIntervalNanos;
        if (l2 - this.mPostTimeNanos <= RecyclerView.sFrameIntervalNanos)
        {
          long l4 = RecyclerView.-get1();
          if (l1 + l3 - l2 >= l4) {}
        }
        else
        {
          return;
        }
        if ((this.mItemPrefetchArray == null) || (this.mItemPrefetchArray.length < i)) {
          this.mItemPrefetchArray = new int[i];
        }
        Arrays.fill(this.mItemPrefetchArray, -1);
        i = RecyclerView.this.mLayout.gatherPrefetchIndices(this.mDx, this.mDy, RecyclerView.this.mState, this.mItemPrefetchArray);
        RecyclerView.this.mRecycler.prefetch(this.mItemPrefetchArray, i);
        return;
      }
      finally
      {
        TraceCompat.endSection();
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\widget\RecyclerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */