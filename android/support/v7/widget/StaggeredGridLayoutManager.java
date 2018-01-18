package android.support.v7.widget;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.CollectionItemInfoCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class StaggeredGridLayoutManager
  extends RecyclerView.LayoutManager
  implements RecyclerView.SmoothScroller.ScrollVectorProvider
{
  static final boolean DEBUG = false;
  @Deprecated
  public static final int GAP_HANDLING_LAZY = 1;
  public static final int GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS = 2;
  public static final int GAP_HANDLING_NONE = 0;
  public static final int HORIZONTAL = 0;
  static final int INVALID_OFFSET = Integer.MIN_VALUE;
  private static final float MAX_SCROLL_FACTOR = 0.33333334F;
  private static final String TAG = "StaggeredGridLayoutManager";
  public static final int VERTICAL = 1;
  private final AnchorInfo mAnchorInfo = new AnchorInfo();
  private final Runnable mCheckForGapsRunnable = new Runnable()
  {
    public void run()
    {
      StaggeredGridLayoutManager.this.checkForGaps();
    }
  };
  private int mFullSizeSpec;
  private int mGapStrategy = 2;
  private boolean mLaidOutInvalidFullSpan = false;
  private boolean mLastLayoutFromEnd;
  private boolean mLastLayoutRTL;
  @NonNull
  private final LayoutState mLayoutState;
  LazySpanLookup mLazySpanLookup = new LazySpanLookup();
  private int mOrientation;
  private SavedState mPendingSavedState;
  int mPendingScrollPosition = -1;
  int mPendingScrollPositionOffset = Integer.MIN_VALUE;
  @NonNull
  OrientationHelper mPrimaryOrientation;
  private BitSet mRemainingSpans;
  boolean mReverseLayout = false;
  @NonNull
  OrientationHelper mSecondaryOrientation;
  boolean mShouldReverseLayout = false;
  private int mSizePerSpan;
  private boolean mSmoothScrollbarEnabled = true;
  private int mSpanCount = -1;
  Span[] mSpans;
  private final Rect mTmpRect = new Rect();
  
  public StaggeredGridLayoutManager(int paramInt1, int paramInt2)
  {
    this.mOrientation = paramInt2;
    setSpanCount(paramInt1);
    if (this.mGapStrategy != 0) {}
    for (;;)
    {
      setAutoMeasureEnabled(bool);
      this.mLayoutState = new LayoutState();
      createOrientationHelpers();
      return;
      bool = false;
    }
  }
  
  public StaggeredGridLayoutManager(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    paramContext = getProperties(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setOrientation(paramContext.orientation);
    setSpanCount(paramContext.spanCount);
    setReverseLayout(paramContext.reverseLayout);
    if (this.mGapStrategy != 0) {}
    for (;;)
    {
      setAutoMeasureEnabled(bool);
      this.mLayoutState = new LayoutState();
      createOrientationHelpers();
      return;
      bool = false;
    }
  }
  
  private void appendViewToAllSpans(View paramView)
  {
    int i = this.mSpanCount - 1;
    while (i >= 0)
    {
      this.mSpans[i].appendToSpan(paramView);
      i -= 1;
    }
  }
  
  private void applyPendingSavedState(AnchorInfo paramAnchorInfo)
  {
    if (this.mPendingSavedState.mSpanOffsetsSize > 0) {
      if (this.mPendingSavedState.mSpanOffsetsSize == this.mSpanCount)
      {
        int j = 0;
        if (j < this.mSpanCount)
        {
          this.mSpans[j].clear();
          int k = this.mPendingSavedState.mSpanOffsets[j];
          int i = k;
          if (k != Integer.MIN_VALUE) {
            if (!this.mPendingSavedState.mAnchorLayoutFromEnd) {
              break label102;
            }
          }
          label102:
          for (i = k + this.mPrimaryOrientation.getEndAfterPadding();; i = k + this.mPrimaryOrientation.getStartAfterPadding())
          {
            this.mSpans[j].setLine(i);
            j += 1;
            break;
          }
        }
      }
      else
      {
        this.mPendingSavedState.invalidateSpanInfo();
        this.mPendingSavedState.mAnchorPosition = this.mPendingSavedState.mVisibleAnchorPosition;
      }
    }
    this.mLastLayoutRTL = this.mPendingSavedState.mLastLayoutRTL;
    setReverseLayout(this.mPendingSavedState.mReverseLayout);
    resolveShouldLayoutReverse();
    if (this.mPendingSavedState.mAnchorPosition != -1) {
      this.mPendingScrollPosition = this.mPendingSavedState.mAnchorPosition;
    }
    for (paramAnchorInfo.mLayoutFromEnd = this.mPendingSavedState.mAnchorLayoutFromEnd;; paramAnchorInfo.mLayoutFromEnd = this.mShouldReverseLayout)
    {
      if (this.mPendingSavedState.mSpanLookupSize > 1)
      {
        this.mLazySpanLookup.mData = this.mPendingSavedState.mSpanLookup;
        this.mLazySpanLookup.mFullSpanItems = this.mPendingSavedState.mFullSpanItems;
      }
      return;
    }
  }
  
  private void attachViewToSpans(View paramView, LayoutParams paramLayoutParams, LayoutState paramLayoutState)
  {
    if (paramLayoutState.mLayoutDirection == 1)
    {
      if (paramLayoutParams.mFullSpan)
      {
        appendViewToAllSpans(paramView);
        return;
      }
      paramLayoutParams.mSpan.appendToSpan(paramView);
      return;
    }
    if (paramLayoutParams.mFullSpan)
    {
      prependViewToAllSpans(paramView);
      return;
    }
    paramLayoutParams.mSpan.prependToSpan(paramView);
  }
  
  private int calculateScrollDirectionForPosition(int paramInt)
  {
    int i = 0;
    if (getChildCount() == 0)
    {
      if (this.mShouldReverseLayout) {
        return 1;
      }
      return -1;
    }
    if (paramInt < getFirstChildPosition()) {
      i = 1;
    }
    if (i != this.mShouldReverseLayout) {
      return -1;
    }
    return 1;
  }
  
  private boolean checkSpanForGap(Span paramSpan)
  {
    if (this.mShouldReverseLayout)
    {
      if (paramSpan.getEndLine() < this.mPrimaryOrientation.getEndAfterPadding()) {
        return !paramSpan.getLayoutParams((View)paramSpan.mViews.get(paramSpan.mViews.size() - 1)).mFullSpan;
      }
    }
    else if (paramSpan.getStartLine() > this.mPrimaryOrientation.getStartAfterPadding()) {
      return !paramSpan.getLayoutParams((View)paramSpan.mViews.get(0)).mFullSpan;
    }
    return false;
  }
  
  private int computeScrollExtent(RecyclerView.State paramState)
  {
    boolean bool2 = false;
    if (getChildCount() == 0) {
      return 0;
    }
    OrientationHelper localOrientationHelper = this.mPrimaryOrientation;
    View localView;
    if (this.mSmoothScrollbarEnabled)
    {
      bool1 = false;
      localView = findFirstVisibleItemClosestToStart(bool1);
      if (!this.mSmoothScrollbarEnabled) {
        break label66;
      }
    }
    label66:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      return ScrollbarHelper.computeScrollExtent(paramState, localOrientationHelper, localView, findFirstVisibleItemClosestToEnd(bool1), this, this.mSmoothScrollbarEnabled);
      bool1 = true;
      break;
    }
  }
  
  private int computeScrollOffset(RecyclerView.State paramState)
  {
    boolean bool2 = false;
    if (getChildCount() == 0) {
      return 0;
    }
    OrientationHelper localOrientationHelper = this.mPrimaryOrientation;
    View localView;
    if (this.mSmoothScrollbarEnabled)
    {
      bool1 = false;
      localView = findFirstVisibleItemClosestToStart(bool1);
      if (!this.mSmoothScrollbarEnabled) {
        break label70;
      }
    }
    label70:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      return ScrollbarHelper.computeScrollOffset(paramState, localOrientationHelper, localView, findFirstVisibleItemClosestToEnd(bool1), this, this.mSmoothScrollbarEnabled, this.mShouldReverseLayout);
      bool1 = true;
      break;
    }
  }
  
  private int computeScrollRange(RecyclerView.State paramState)
  {
    boolean bool2 = false;
    if (getChildCount() == 0) {
      return 0;
    }
    OrientationHelper localOrientationHelper = this.mPrimaryOrientation;
    View localView;
    if (this.mSmoothScrollbarEnabled)
    {
      bool1 = false;
      localView = findFirstVisibleItemClosestToStart(bool1);
      if (!this.mSmoothScrollbarEnabled) {
        break label66;
      }
    }
    label66:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      return ScrollbarHelper.computeScrollRange(paramState, localOrientationHelper, localView, findFirstVisibleItemClosestToEnd(bool1), this, this.mSmoothScrollbarEnabled);
      bool1 = true;
      break;
    }
  }
  
  private int convertFocusDirectionToLayoutDirection(int paramInt)
  {
    int i = Integer.MIN_VALUE;
    switch (paramInt)
    {
    default: 
      return Integer.MIN_VALUE;
    case 1: 
      if (this.mOrientation == 1) {
        return -1;
      }
      if (isLayoutRTL()) {
        return 1;
      }
      return -1;
    case 2: 
      if (this.mOrientation == 1) {
        return 1;
      }
      if (isLayoutRTL()) {
        return -1;
      }
      return 1;
    case 33: 
      if (this.mOrientation == 1) {
        return -1;
      }
      return Integer.MIN_VALUE;
    case 130: 
      paramInt = i;
      if (this.mOrientation == 1) {
        paramInt = 1;
      }
      return paramInt;
    case 17: 
      if (this.mOrientation == 0) {
        return -1;
      }
      return Integer.MIN_VALUE;
    }
    if (this.mOrientation == 0) {
      return 1;
    }
    return Integer.MIN_VALUE;
  }
  
  private StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem createFullSpanItemFromEnd(int paramInt)
  {
    StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem localFullSpanItem = new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem();
    localFullSpanItem.mGapPerSpan = new int[this.mSpanCount];
    int i = 0;
    while (i < this.mSpanCount)
    {
      localFullSpanItem.mGapPerSpan[i] = (paramInt - this.mSpans[i].getEndLine(paramInt));
      i += 1;
    }
    return localFullSpanItem;
  }
  
  private StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem createFullSpanItemFromStart(int paramInt)
  {
    StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem localFullSpanItem = new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem();
    localFullSpanItem.mGapPerSpan = new int[this.mSpanCount];
    int i = 0;
    while (i < this.mSpanCount)
    {
      localFullSpanItem.mGapPerSpan[i] = (this.mSpans[i].getStartLine(paramInt) - paramInt);
      i += 1;
    }
    return localFullSpanItem;
  }
  
  private void createOrientationHelpers()
  {
    this.mPrimaryOrientation = OrientationHelper.createOrientationHelper(this, this.mOrientation);
    this.mSecondaryOrientation = OrientationHelper.createOrientationHelper(this, 1 - this.mOrientation);
  }
  
  private int fill(RecyclerView.Recycler paramRecycler, LayoutState paramLayoutState, RecyclerView.State paramState)
  {
    this.mRemainingSpans.set(0, this.mSpanCount, true);
    int k;
    label62:
    int j;
    if (this.mLayoutState.mInfinite) {
      if (paramLayoutState.mLayoutDirection == 1)
      {
        i = Integer.MAX_VALUE;
        updateAllRemainingSpans(paramLayoutState.mLayoutDirection, i);
        if (!this.mShouldReverseLayout) {
          break label201;
        }
        k = this.mPrimaryOrientation.getEndAfterPadding();
        j = 0;
        if ((paramLayoutState.hasMore(paramState)) && ((this.mLayoutState.mInfinite) || (!this.mRemainingSpans.isEmpty()))) {
          break label213;
        }
        if (j == 0) {
          recycle(paramRecycler, this.mLayoutState);
        }
        if (this.mLayoutState.mLayoutDirection != -1) {
          break label982;
        }
        i = getMinStart(this.mPrimaryOrientation.getStartAfterPadding());
      }
    }
    label201:
    label213:
    label257:
    label278:
    label289:
    label310:
    label343:
    label450:
    label491:
    label535:
    label556:
    label610:
    label620:
    label632:
    label642:
    label654:
    label766:
    label842:
    label844:
    label874:
    label931:
    label948:
    label966:
    label982:
    for (int i = this.mPrimaryOrientation.getStartAfterPadding() - i;; i = getMaxEnd(this.mPrimaryOrientation.getEndAfterPadding()) - this.mPrimaryOrientation.getEndAfterPadding())
    {
      if (i <= 0) {
        break label1006;
      }
      return Math.min(paramLayoutState.mAvailable, i);
      i = Integer.MIN_VALUE;
      break;
      if (paramLayoutState.mLayoutDirection == 1)
      {
        i = paramLayoutState.mEndLine + paramLayoutState.mAvailable;
        break;
      }
      i = paramLayoutState.mStartLine - paramLayoutState.mAvailable;
      break;
      k = this.mPrimaryOrientation.getStartAfterPadding();
      break label62;
      View localView = paramLayoutState.next(paramRecycler);
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      int i3 = localLayoutParams.getViewLayoutPosition();
      j = this.mLazySpanLookup.getSpan(i3);
      int i1;
      Span localSpan;
      int i2;
      int m;
      int n;
      StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem localFullSpanItem;
      if (j == -1)
      {
        i1 = 1;
        if (i1 == 0) {
          break label620;
        }
        if (!localLayoutParams.mFullSpan) {
          break label610;
        }
        localSpan = this.mSpans[0];
        this.mLazySpanLookup.setSpan(i3, localSpan);
        localLayoutParams.mSpan = localSpan;
        if (paramLayoutState.mLayoutDirection != 1) {
          break label632;
        }
        addView(localView);
        measureChildWithDecorationsAndMargin(localView, localLayoutParams, false);
        if (paramLayoutState.mLayoutDirection != 1) {
          break label654;
        }
        if (!localLayoutParams.mFullSpan) {
          break label642;
        }
        j = getMaxEnd(k);
        i2 = j + this.mPrimaryOrientation.getDecoratedMeasurement(localView);
        m = j;
        n = i2;
        if (i1 != 0)
        {
          m = j;
          n = i2;
          if (localLayoutParams.mFullSpan)
          {
            localFullSpanItem = createFullSpanItemFromEnd(j);
            localFullSpanItem.mGapDir = -1;
            localFullSpanItem.mPosition = i3;
            this.mLazySpanLookup.addFullSpanItem(localFullSpanItem);
            n = i2;
            m = j;
          }
        }
        if ((localLayoutParams.mFullSpan) && (paramLayoutState.mItemDirection == -1))
        {
          if (i1 == 0) {
            break label766;
          }
          this.mLaidOutInvalidFullSpan = true;
        }
        attachViewToSpans(localView, localLayoutParams, paramLayoutState);
        if ((!isLayoutRTL()) || (this.mOrientation != 1)) {
          break label874;
        }
        if (!localLayoutParams.mFullSpan) {
          break label844;
        }
        j = this.mSecondaryOrientation.getEndAfterPadding();
        i2 = j - this.mSecondaryOrientation.getDecoratedMeasurement(localView);
        i1 = j;
        j = i2;
        if (this.mOrientation != 1) {
          break label931;
        }
        layoutDecoratedWithMargins(localView, j, m, i1, n);
        if (!localLayoutParams.mFullSpan) {
          break label948;
        }
        updateAllRemainingSpans(this.mLayoutState.mLayoutDirection, i);
        recycle(paramRecycler, this.mLayoutState);
        if ((this.mLayoutState.mStopInFocusable) && (localView.isFocusable()))
        {
          if (!localLayoutParams.mFullSpan) {
            break label966;
          }
          this.mRemainingSpans.clear();
        }
      }
      for (;;)
      {
        j = 1;
        break;
        i1 = 0;
        break label257;
        localSpan = getNextSpan(paramLayoutState);
        break label278;
        localSpan = this.mSpans[j];
        break label289;
        addView(localView, 0);
        break label310;
        j = localSpan.getEndLine(k);
        break label343;
        if (localLayoutParams.mFullSpan) {}
        for (j = getMinStart(k);; j = localSpan.getStartLine(k))
        {
          i2 = j - this.mPrimaryOrientation.getDecoratedMeasurement(localView);
          m = i2;
          n = j;
          if (i1 == 0) {
            break;
          }
          m = i2;
          n = j;
          if (!localLayoutParams.mFullSpan) {
            break;
          }
          localFullSpanItem = createFullSpanItemFromStart(j);
          localFullSpanItem.mGapDir = 1;
          localFullSpanItem.mPosition = i3;
          this.mLazySpanLookup.addFullSpanItem(localFullSpanItem);
          m = i2;
          n = j;
          break;
        }
        if (paramLayoutState.mLayoutDirection == 1) {
          if (areAllEndsEqual()) {
            j = 0;
          }
        }
        for (;;)
        {
          if (j == 0) {
            break label842;
          }
          localFullSpanItem = this.mLazySpanLookup.getFullSpanItem(i3);
          if (localFullSpanItem != null) {
            localFullSpanItem.mHasUnwantedGapAfter = true;
          }
          this.mLaidOutInvalidFullSpan = true;
          break;
          j = 1;
          continue;
          if (areAllStartsEqual()) {
            j = 0;
          } else {
            j = 1;
          }
        }
        break label450;
        j = this.mSecondaryOrientation.getEndAfterPadding() - (this.mSpanCount - 1 - localSpan.mIndex) * this.mSizePerSpan;
        break label491;
        if (localLayoutParams.mFullSpan) {}
        for (j = this.mSecondaryOrientation.getStartAfterPadding();; j = localSpan.mIndex * this.mSizePerSpan + this.mSecondaryOrientation.getStartAfterPadding())
        {
          i1 = j + this.mSecondaryOrientation.getDecoratedMeasurement(localView);
          break;
        }
        layoutDecoratedWithMargins(localView, m, j, n, i1);
        break label535;
        updateRemainingSpans(localSpan, this.mLayoutState.mLayoutDirection, i);
        break label556;
        this.mRemainingSpans.set(localSpan.mIndex, false);
      }
    }
    label1006:
    return 0;
  }
  
  private int findFirstReferenceChildPosition(int paramInt)
  {
    int j = getChildCount();
    int i = 0;
    while (i < j)
    {
      int k = getPosition(getChildAt(i));
      if ((k >= 0) && (k < paramInt)) {
        return k;
      }
      i += 1;
    }
    return 0;
  }
  
  private int findLastReferenceChildPosition(int paramInt)
  {
    int i = getChildCount() - 1;
    while (i >= 0)
    {
      int j = getPosition(getChildAt(i));
      if ((j >= 0) && (j < paramInt)) {
        return j;
      }
      i -= 1;
    }
    return 0;
  }
  
  private void fixEndGap(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, boolean paramBoolean)
  {
    int i = getMaxEnd(Integer.MIN_VALUE);
    if (i == Integer.MIN_VALUE) {
      return;
    }
    i = this.mPrimaryOrientation.getEndAfterPadding() - i;
    if (i > 0)
    {
      i -= -scrollBy(-i, paramRecycler, paramState);
      if ((paramBoolean) && (i > 0)) {
        this.mPrimaryOrientation.offsetChildren(i);
      }
      return;
    }
  }
  
  private void fixStartGap(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, boolean paramBoolean)
  {
    int i = getMinStart(Integer.MAX_VALUE);
    if (i == Integer.MAX_VALUE) {
      return;
    }
    i -= this.mPrimaryOrientation.getStartAfterPadding();
    if (i > 0)
    {
      i -= scrollBy(i, paramRecycler, paramState);
      if ((paramBoolean) && (i > 0)) {
        this.mPrimaryOrientation.offsetChildren(-i);
      }
      return;
    }
  }
  
  private int getFirstChildPosition()
  {
    if (getChildCount() == 0) {
      return 0;
    }
    return getPosition(getChildAt(0));
  }
  
  private int getLastChildPosition()
  {
    int i = getChildCount();
    if (i == 0) {
      return 0;
    }
    return getPosition(getChildAt(i - 1));
  }
  
  private int getMaxEnd(int paramInt)
  {
    int j = this.mSpans[0].getEndLine(paramInt);
    int i = 1;
    while (i < this.mSpanCount)
    {
      int m = this.mSpans[i].getEndLine(paramInt);
      int k = j;
      if (m > j) {
        k = m;
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  private int getMaxStart(int paramInt)
  {
    int j = this.mSpans[0].getStartLine(paramInt);
    int i = 1;
    while (i < this.mSpanCount)
    {
      int m = this.mSpans[i].getStartLine(paramInt);
      int k = j;
      if (m > j) {
        k = m;
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  private int getMinEnd(int paramInt)
  {
    int j = this.mSpans[0].getEndLine(paramInt);
    int i = 1;
    while (i < this.mSpanCount)
    {
      int m = this.mSpans[i].getEndLine(paramInt);
      int k = j;
      if (m < j) {
        k = m;
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  private int getMinStart(int paramInt)
  {
    int j = this.mSpans[0].getStartLine(paramInt);
    int i = 1;
    while (i < this.mSpanCount)
    {
      int m = this.mSpans[i].getStartLine(paramInt);
      int k = j;
      if (m < j) {
        k = m;
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  private Span getNextSpan(LayoutState paramLayoutState)
  {
    int i;
    int k;
    int j;
    if (preferLastSpan(paramLayoutState.mLayoutDirection))
    {
      i = this.mSpanCount - 1;
      k = -1;
      j = -1;
    }
    Span localSpan;
    int i1;
    int n;
    while (paramLayoutState.mLayoutDirection == 1)
    {
      paramLayoutState = null;
      m = Integer.MAX_VALUE;
      i2 = this.mPrimaryOrientation.getStartAfterPadding();
      for (;;)
      {
        if (i != k)
        {
          localSpan = this.mSpans[i];
          i1 = localSpan.getEndLine(i2);
          n = m;
          if (i1 < m)
          {
            paramLayoutState = localSpan;
            n = i1;
          }
          i += j;
          m = n;
          continue;
          i = 0;
          k = this.mSpanCount;
          j = 1;
          break;
        }
      }
      return paramLayoutState;
    }
    paramLayoutState = null;
    int m = Integer.MIN_VALUE;
    int i2 = this.mPrimaryOrientation.getEndAfterPadding();
    while (i != k)
    {
      localSpan = this.mSpans[i];
      i1 = localSpan.getStartLine(i2);
      n = m;
      if (i1 > m)
      {
        paramLayoutState = localSpan;
        n = i1;
      }
      i += j;
      m = n;
    }
    return paramLayoutState;
  }
  
  private void handleUpdate(int paramInt1, int paramInt2, int paramInt3)
  {
    int k;
    int j;
    int i;
    if (this.mShouldReverseLayout)
    {
      k = getLastChildPosition();
      if (paramInt3 != 8) {
        break label104;
      }
      if (paramInt1 >= paramInt2) {
        break label93;
      }
      j = paramInt2 + 1;
      i = paramInt1;
      label32:
      this.mLazySpanLookup.invalidateAfter(i);
      switch (paramInt3)
      {
      }
    }
    for (;;)
    {
      if (j > k) {
        break label160;
      }
      return;
      k = getFirstChildPosition();
      break;
      label93:
      j = paramInt1 + 1;
      i = paramInt2;
      break label32;
      label104:
      i = paramInt1;
      j = paramInt1 + paramInt2;
      break label32;
      this.mLazySpanLookup.offsetForAddition(paramInt1, paramInt2);
      continue;
      this.mLazySpanLookup.offsetForRemoval(paramInt1, paramInt2);
      continue;
      this.mLazySpanLookup.offsetForRemoval(paramInt1, 1);
      this.mLazySpanLookup.offsetForAddition(paramInt2, 1);
    }
    label160:
    if (this.mShouldReverseLayout) {}
    for (paramInt1 = getFirstChildPosition();; paramInt1 = getLastChildPosition())
    {
      if (i <= paramInt1) {
        requestLayout();
      }
      return;
    }
  }
  
  private void measureChildWithDecorationsAndMargin(View paramView, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    calculateItemDecorationsForChild(paramView, this.mTmpRect);
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    paramInt1 = updateSpecWithExtra(paramInt1, localLayoutParams.leftMargin + this.mTmpRect.left, localLayoutParams.rightMargin + this.mTmpRect.right);
    paramInt2 = updateSpecWithExtra(paramInt2, localLayoutParams.topMargin + this.mTmpRect.top, localLayoutParams.bottomMargin + this.mTmpRect.bottom);
    if (paramBoolean) {}
    for (paramBoolean = shouldReMeasureChild(paramView, paramInt1, paramInt2, localLayoutParams);; paramBoolean = shouldMeasureChild(paramView, paramInt1, paramInt2, localLayoutParams))
    {
      if (paramBoolean) {
        paramView.measure(paramInt1, paramInt2);
      }
      return;
    }
  }
  
  private void measureChildWithDecorationsAndMargin(View paramView, LayoutParams paramLayoutParams, boolean paramBoolean)
  {
    if (paramLayoutParams.mFullSpan)
    {
      if (this.mOrientation == 1)
      {
        measureChildWithDecorationsAndMargin(paramView, this.mFullSizeSpec, getChildMeasureSpec(getHeight(), getHeightMode(), 0, paramLayoutParams.height, true), paramBoolean);
        return;
      }
      measureChildWithDecorationsAndMargin(paramView, getChildMeasureSpec(getWidth(), getWidthMode(), 0, paramLayoutParams.width, true), this.mFullSizeSpec, paramBoolean);
      return;
    }
    if (this.mOrientation == 1)
    {
      measureChildWithDecorationsAndMargin(paramView, getChildMeasureSpec(this.mSizePerSpan, getWidthMode(), 0, paramLayoutParams.width, false), getChildMeasureSpec(getHeight(), getHeightMode(), 0, paramLayoutParams.height, true), paramBoolean);
      return;
    }
    measureChildWithDecorationsAndMargin(paramView, getChildMeasureSpec(getWidth(), getWidthMode(), 0, paramLayoutParams.width, true), getChildMeasureSpec(this.mSizePerSpan, getHeightMode(), 0, paramLayoutParams.height, false), paramBoolean);
  }
  
  private void onLayoutChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, boolean paramBoolean)
  {
    int j = 1;
    AnchorInfo localAnchorInfo = this.mAnchorInfo;
    if (((this.mPendingSavedState != null) || (this.mPendingScrollPosition != -1)) && (paramState.getItemCount() == 0))
    {
      removeAndRecycleAllViews(paramRecycler);
      localAnchorInfo.reset();
      return;
    }
    int i;
    if ((!localAnchorInfo.mValid) || (this.mPendingScrollPosition != -1))
    {
      i = 1;
      if (i != 0)
      {
        localAnchorInfo.reset();
        if (this.mPendingSavedState == null) {
          break label256;
        }
        applyPendingSavedState(localAnchorInfo);
      }
    }
    for (;;)
    {
      updateAnchorInfoForLayout(paramState, localAnchorInfo);
      localAnchorInfo.mValid = true;
      if ((this.mPendingSavedState == null) && (this.mPendingScrollPosition == -1) && ((localAnchorInfo.mLayoutFromEnd != this.mLastLayoutFromEnd) || (isLayoutRTL() != this.mLastLayoutRTL)))
      {
        this.mLazySpanLookup.clear();
        localAnchorInfo.mInvalidateOffsets = true;
      }
      if ((getChildCount() <= 0) || ((this.mPendingSavedState != null) && (this.mPendingSavedState.mSpanOffsetsSize >= 1))) {
        break label338;
      }
      if (!localAnchorInfo.mInvalidateOffsets) {
        break label272;
      }
      i = 0;
      while (i < this.mSpanCount)
      {
        this.mSpans[i].clear();
        if (localAnchorInfo.mOffset != Integer.MIN_VALUE) {
          this.mSpans[i].setLine(localAnchorInfo.mOffset);
        }
        i += 1;
      }
      if (this.mPendingSavedState != null)
      {
        i = 1;
        break;
      }
      i = 0;
      break;
      label256:
      resolveShouldLayoutReverse();
      localAnchorInfo.mLayoutFromEnd = this.mShouldReverseLayout;
    }
    label272:
    if ((i != 0) || (this.mAnchorInfo.mSpanReferenceLines == null))
    {
      i = 0;
      while (i < this.mSpanCount)
      {
        this.mSpans[i].cacheReferenceLineAndClear(this.mShouldReverseLayout, localAnchorInfo.mOffset);
        i += 1;
      }
      this.mAnchorInfo.saveSpanReferenceLines(this.mSpans);
      label338:
      detachAndScrapAttachedViews(paramRecycler);
      this.mLayoutState.mRecycle = false;
      this.mLaidOutInvalidFullSpan = false;
      updateMeasureSpecs(this.mSecondaryOrientation.getTotalSpace());
      updateLayoutState(localAnchorInfo.mPosition, paramState);
      if (!localAnchorInfo.mLayoutFromEnd) {
        break label592;
      }
      setLayoutStateDirection(-1);
      fill(paramRecycler, this.mLayoutState, paramState);
      setLayoutStateDirection(1);
      this.mLayoutState.mCurrentPosition = (localAnchorInfo.mPosition + this.mLayoutState.mItemDirection);
      fill(paramRecycler, this.mLayoutState, paramState);
      label437:
      repositionToWrapContentIfNecessary();
      if (getChildCount() > 0)
      {
        if (!this.mShouldReverseLayout) {
          break label647;
        }
        fixEndGap(paramRecycler, paramState, true);
        fixStartGap(paramRecycler, paramState, false);
      }
    }
    int k;
    for (;;)
    {
      k = 0;
      i = k;
      if (paramBoolean)
      {
        if (!paramState.isPreLayout()) {
          break label664;
        }
        i = k;
      }
      if (paramState.isPreLayout()) {
        this.mAnchorInfo.reset();
      }
      this.mLastLayoutFromEnd = localAnchorInfo.mLayoutFromEnd;
      this.mLastLayoutRTL = isLayoutRTL();
      if (i != 0)
      {
        this.mAnchorInfo.reset();
        onLayoutChildren(paramRecycler, paramState, false);
      }
      return;
      i = 0;
      while (i < this.mSpanCount)
      {
        Span localSpan = this.mSpans[i];
        localSpan.clear();
        localSpan.setLine(this.mAnchorInfo.mSpanReferenceLines[i]);
        i += 1;
      }
      break;
      label592:
      setLayoutStateDirection(1);
      fill(paramRecycler, this.mLayoutState, paramState);
      setLayoutStateDirection(-1);
      this.mLayoutState.mCurrentPosition = (localAnchorInfo.mPosition + this.mLayoutState.mItemDirection);
      fill(paramRecycler, this.mLayoutState, paramState);
      break label437;
      label647:
      fixStartGap(paramRecycler, paramState, true);
      fixEndGap(paramRecycler, paramState, false);
    }
    label664:
    if ((this.mGapStrategy != 0) && (getChildCount() > 0))
    {
      i = j;
      if (!this.mLaidOutInvalidFullSpan)
      {
        if (hasGapsToFix() == null) {
          break label739;
        }
        i = j;
      }
    }
    label700:
    for (j = i;; j = 0)
    {
      i = k;
      if (j == 0) {
        break;
      }
      removeCallbacks(this.mCheckForGapsRunnable);
      i = k;
      if (!checkForGaps()) {
        break;
      }
      i = 1;
      break;
      label739:
      i = 0;
      break label700;
    }
  }
  
  private boolean preferLastSpan(int paramInt)
  {
    if (this.mOrientation == 0)
    {
      if (paramInt == -1) {}
      for (i = 1; i != this.mShouldReverseLayout; i = 0) {
        return true;
      }
      return false;
    }
    if (paramInt == -1)
    {
      i = 1;
      if (i != this.mShouldReverseLayout) {
        break label63;
      }
    }
    label63:
    for (int i = 1;; i = 0)
    {
      if (i != isLayoutRTL()) {
        break label68;
      }
      return true;
      i = 0;
      break;
    }
    label68:
    return false;
  }
  
  private void prependViewToAllSpans(View paramView)
  {
    int i = this.mSpanCount - 1;
    while (i >= 0)
    {
      this.mSpans[i].prependToSpan(paramView);
      i -= 1;
    }
  }
  
  private void recycle(RecyclerView.Recycler paramRecycler, LayoutState paramLayoutState)
  {
    if ((!paramLayoutState.mRecycle) || (paramLayoutState.mInfinite)) {
      return;
    }
    if (paramLayoutState.mAvailable == 0)
    {
      if (paramLayoutState.mLayoutDirection == -1)
      {
        recycleFromEnd(paramRecycler, paramLayoutState.mEndLine);
        return;
      }
      recycleFromStart(paramRecycler, paramLayoutState.mStartLine);
      return;
    }
    if (paramLayoutState.mLayoutDirection == -1)
    {
      i = paramLayoutState.mStartLine - getMaxStart(paramLayoutState.mStartLine);
      if (i < 0) {}
      for (i = paramLayoutState.mEndLine;; i = paramLayoutState.mEndLine - Math.min(i, paramLayoutState.mAvailable))
      {
        recycleFromEnd(paramRecycler, i);
        return;
      }
    }
    int i = getMinEnd(paramLayoutState.mEndLine) - paramLayoutState.mEndLine;
    if (i < 0) {}
    for (i = paramLayoutState.mStartLine;; i = paramLayoutState.mStartLine + Math.min(i, paramLayoutState.mAvailable))
    {
      recycleFromStart(paramRecycler, i);
      return;
    }
  }
  
  private void recycleFromEnd(RecyclerView.Recycler paramRecycler, int paramInt)
  {
    int i = getChildCount() - 1;
    while (i >= 0)
    {
      View localView = getChildAt(i);
      if ((this.mPrimaryOrientation.getDecoratedStart(localView) >= paramInt) && (this.mPrimaryOrientation.getTransformedStartWithDecoration(localView) >= paramInt))
      {
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if (localLayoutParams.mFullSpan)
        {
          int j = 0;
          while (j < this.mSpanCount)
          {
            if (this.mSpans[j].mViews.size() == 1) {
              return;
            }
            j += 1;
          }
          j = 0;
          while (j < this.mSpanCount)
          {
            this.mSpans[j].popEnd();
            j += 1;
          }
        }
        if (localLayoutParams.mSpan.mViews.size() == 1) {
          return;
        }
        localLayoutParams.mSpan.popEnd();
        removeAndRecycleView(localView, paramRecycler);
        i -= 1;
      }
      else {}
    }
  }
  
  private void recycleFromStart(RecyclerView.Recycler paramRecycler, int paramInt)
  {
    while (getChildCount() > 0)
    {
      View localView = getChildAt(0);
      if ((this.mPrimaryOrientation.getDecoratedEnd(localView) <= paramInt) && (this.mPrimaryOrientation.getTransformedEndWithDecoration(localView) <= paramInt))
      {
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if (localLayoutParams.mFullSpan)
        {
          int i = 0;
          while (i < this.mSpanCount)
          {
            if (this.mSpans[i].mViews.size() == 1) {
              return;
            }
            i += 1;
          }
          i = 0;
          while (i < this.mSpanCount)
          {
            this.mSpans[i].popStart();
            i += 1;
          }
        }
        if (localLayoutParams.mSpan.mViews.size() == 1) {
          return;
        }
        localLayoutParams.mSpan.popStart();
        removeAndRecycleView(localView, paramRecycler);
      }
      else {}
    }
  }
  
  private void repositionToWrapContentIfNecessary()
  {
    if (this.mSecondaryOrientation.getMode() == 1073741824) {
      return;
    }
    float f1 = 0.0F;
    int k = getChildCount();
    int i = 0;
    View localView;
    if (i < k)
    {
      localView = getChildAt(i);
      float f3 = this.mSecondaryOrientation.getDecoratedMeasurement(localView);
      if (f3 < f1) {}
      for (;;)
      {
        i += 1;
        break;
        float f2 = f3;
        if (((LayoutParams)localView.getLayoutParams()).isFullSpan()) {
          f2 = 1.0F * f3 / this.mSpanCount;
        }
        f1 = Math.max(f1, f2);
      }
    }
    int m = this.mSizePerSpan;
    int j = Math.round(this.mSpanCount * f1);
    i = j;
    if (this.mSecondaryOrientation.getMode() == Integer.MIN_VALUE) {
      i = Math.min(j, this.mSecondaryOrientation.getTotalSpace());
    }
    updateMeasureSpecs(i);
    if (this.mSizePerSpan == m) {
      return;
    }
    i = 0;
    if (i < k)
    {
      localView = getChildAt(i);
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      if (localLayoutParams.mFullSpan) {}
      for (;;)
      {
        i += 1;
        break;
        if ((isLayoutRTL()) && (this.mOrientation == 1))
        {
          localView.offsetLeftAndRight(-(this.mSpanCount - 1 - localLayoutParams.mSpan.mIndex) * this.mSizePerSpan - -(this.mSpanCount - 1 - localLayoutParams.mSpan.mIndex) * m);
        }
        else
        {
          j = localLayoutParams.mSpan.mIndex * this.mSizePerSpan;
          int n = localLayoutParams.mSpan.mIndex * m;
          if (this.mOrientation == 1) {
            localView.offsetLeftAndRight(j - n);
          } else {
            localView.offsetTopAndBottom(j - n);
          }
        }
      }
    }
  }
  
  private void resolveShouldLayoutReverse()
  {
    boolean bool = true;
    if ((this.mOrientation != 1) && (isLayoutRTL()))
    {
      if (this.mReverseLayout) {
        bool = false;
      }
      this.mShouldReverseLayout = bool;
      return;
    }
    this.mShouldReverseLayout = this.mReverseLayout;
  }
  
  private void setLayoutStateDirection(int paramInt)
  {
    int i = 1;
    this.mLayoutState.mLayoutDirection = paramInt;
    LayoutState localLayoutState = this.mLayoutState;
    boolean bool2 = this.mShouldReverseLayout;
    boolean bool1;
    if (paramInt == -1)
    {
      bool1 = true;
      if (bool2 != bool1) {
        break label49;
      }
    }
    label49:
    for (paramInt = i;; paramInt = -1)
    {
      localLayoutState.mItemDirection = paramInt;
      return;
      bool1 = false;
      break;
    }
  }
  
  private void updateAllRemainingSpans(int paramInt1, int paramInt2)
  {
    int i = 0;
    if (i < this.mSpanCount)
    {
      if (this.mSpans[i].mViews.isEmpty()) {}
      for (;;)
      {
        i += 1;
        break;
        updateRemainingSpans(this.mSpans[i], paramInt1, paramInt2);
      }
    }
  }
  
  private boolean updateAnchorFromChildren(RecyclerView.State paramState, AnchorInfo paramAnchorInfo)
  {
    if (this.mLastLayoutFromEnd) {}
    for (int i = findLastReferenceChildPosition(paramState.getItemCount());; i = findFirstReferenceChildPosition(paramState.getItemCount()))
    {
      paramAnchorInfo.mPosition = i;
      paramAnchorInfo.mOffset = Integer.MIN_VALUE;
      return true;
    }
  }
  
  private void updateLayoutState(int paramInt, RecyclerView.State paramState)
  {
    boolean bool2 = true;
    this.mLayoutState.mAvailable = 0;
    this.mLayoutState.mCurrentPosition = paramInt;
    int k = 0;
    int m = 0;
    int i = m;
    int j = k;
    boolean bool1;
    if (isSmoothScrolling())
    {
      int n = paramState.getTargetScrollPosition();
      i = m;
      j = k;
      if (n != -1)
      {
        boolean bool3 = this.mShouldReverseLayout;
        if (n >= paramInt) {
          break label184;
        }
        bool1 = true;
        if (bool3 != bool1) {
          break label190;
        }
        i = this.mPrimaryOrientation.getTotalSpace();
        j = k;
      }
    }
    label92:
    if (getClipToPadding())
    {
      this.mLayoutState.mStartLine = (this.mPrimaryOrientation.getStartAfterPadding() - j);
      this.mLayoutState.mEndLine = (this.mPrimaryOrientation.getEndAfterPadding() + i);
      label132:
      this.mLayoutState.mStopInFocusable = false;
      this.mLayoutState.mRecycle = true;
      paramState = this.mLayoutState;
      if (this.mPrimaryOrientation.getMode() != 0) {
        break label240;
      }
      if (this.mPrimaryOrientation.getEnd() != 0) {
        break label234;
      }
      bool1 = bool2;
    }
    for (;;)
    {
      paramState.mInfinite = bool1;
      return;
      label184:
      bool1 = false;
      break;
      label190:
      j = this.mPrimaryOrientation.getTotalSpace();
      i = m;
      break label92;
      this.mLayoutState.mEndLine = (this.mPrimaryOrientation.getEnd() + i);
      this.mLayoutState.mStartLine = (-j);
      break label132;
      label234:
      bool1 = false;
      continue;
      label240:
      bool1 = false;
    }
  }
  
  private void updateRemainingSpans(Span paramSpan, int paramInt1, int paramInt2)
  {
    int i = paramSpan.getDeletedSize();
    if (paramInt1 == -1) {
      if (paramSpan.getStartLine() + i <= paramInt2) {
        this.mRemainingSpans.set(paramSpan.mIndex, false);
      }
    }
    while (paramSpan.getEndLine() - i < paramInt2) {
      return;
    }
    this.mRemainingSpans.set(paramSpan.mIndex, false);
  }
  
  private int updateSpecWithExtra(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt2 == 0) && (paramInt3 == 0)) {
      return paramInt1;
    }
    int i = View.MeasureSpec.getMode(paramInt1);
    if ((i == Integer.MIN_VALUE) || (i == 1073741824)) {
      return View.MeasureSpec.makeMeasureSpec(Math.max(0, View.MeasureSpec.getSize(paramInt1) - paramInt2 - paramInt3), i);
    }
    return paramInt1;
  }
  
  boolean areAllEndsEqual()
  {
    int j = this.mSpans[0].getEndLine(Integer.MIN_VALUE);
    int i = 1;
    while (i < this.mSpanCount)
    {
      if (this.mSpans[i].getEndLine(Integer.MIN_VALUE) != j) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  boolean areAllStartsEqual()
  {
    int j = this.mSpans[0].getStartLine(Integer.MIN_VALUE);
    int i = 1;
    while (i < this.mSpanCount)
    {
      if (this.mSpans[i].getStartLine(Integer.MIN_VALUE) != j) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  public void assertNotInLayoutOrScroll(String paramString)
  {
    if (this.mPendingSavedState == null) {
      super.assertNotInLayoutOrScroll(paramString);
    }
  }
  
  public boolean canScrollHorizontally()
  {
    boolean bool = false;
    if (this.mOrientation == 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean canScrollVertically()
  {
    return this.mOrientation == 1;
  }
  
  boolean checkForGaps()
  {
    if ((getChildCount() == 0) || (this.mGapStrategy == 0)) {}
    while (!isAttachedToWindow()) {
      return false;
    }
    int j;
    if (this.mShouldReverseLayout) {
      j = getLastChildPosition();
    }
    for (int i = getFirstChildPosition(); (j == 0) && (hasGapsToFix() != null); i = getLastChildPosition())
    {
      this.mLazySpanLookup.clear();
      requestSimpleAnimationsInNextLayout();
      requestLayout();
      return true;
      j = getFirstChildPosition();
    }
    if (!this.mLaidOutInvalidFullSpan) {
      return false;
    }
    if (this.mShouldReverseLayout) {}
    StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem localFullSpanItem1;
    for (int k = -1;; k = 1)
    {
      localFullSpanItem1 = this.mLazySpanLookup.getFirstFullSpanItemInRange(j, i + 1, k, true);
      if (localFullSpanItem1 != null) {
        break;
      }
      this.mLaidOutInvalidFullSpan = false;
      this.mLazySpanLookup.forceInvalidateAfter(i + 1);
      return false;
    }
    StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem localFullSpanItem2 = this.mLazySpanLookup.getFirstFullSpanItemInRange(j, localFullSpanItem1.mPosition, k * -1, true);
    if (localFullSpanItem2 == null) {
      this.mLazySpanLookup.forceInvalidateAfter(localFullSpanItem1.mPosition);
    }
    for (;;)
    {
      requestSimpleAnimationsInNextLayout();
      requestLayout();
      return true;
      this.mLazySpanLookup.forceInvalidateAfter(localFullSpanItem2.mPosition + 1);
    }
  }
  
  public boolean checkLayoutParams(RecyclerView.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  public int computeHorizontalScrollExtent(RecyclerView.State paramState)
  {
    return computeScrollExtent(paramState);
  }
  
  public int computeHorizontalScrollOffset(RecyclerView.State paramState)
  {
    return computeScrollOffset(paramState);
  }
  
  public int computeHorizontalScrollRange(RecyclerView.State paramState)
  {
    return computeScrollRange(paramState);
  }
  
  public PointF computeScrollVectorForPosition(int paramInt)
  {
    paramInt = calculateScrollDirectionForPosition(paramInt);
    PointF localPointF = new PointF();
    if (paramInt == 0) {
      return null;
    }
    if (this.mOrientation == 0)
    {
      localPointF.x = paramInt;
      localPointF.y = 0.0F;
      return localPointF;
    }
    localPointF.x = 0.0F;
    localPointF.y = paramInt;
    return localPointF;
  }
  
  public int computeVerticalScrollExtent(RecyclerView.State paramState)
  {
    return computeScrollExtent(paramState);
  }
  
  public int computeVerticalScrollOffset(RecyclerView.State paramState)
  {
    return computeScrollOffset(paramState);
  }
  
  public int computeVerticalScrollRange(RecyclerView.State paramState)
  {
    return computeScrollRange(paramState);
  }
  
  public int[] findFirstCompletelyVisibleItemPositions(int[] paramArrayOfInt)
  {
    int[] arrayOfInt;
    if (paramArrayOfInt == null) {
      arrayOfInt = new int[this.mSpanCount];
    }
    do
    {
      int i = 0;
      while (i < this.mSpanCount)
      {
        arrayOfInt[i] = this.mSpans[i].findFirstCompletelyVisibleItemPosition();
        i += 1;
      }
      arrayOfInt = paramArrayOfInt;
    } while (paramArrayOfInt.length >= this.mSpanCount);
    throw new IllegalArgumentException("Provided int[]'s size must be more than or equal to span count. Expected:" + this.mSpanCount + ", array size:" + paramArrayOfInt.length);
    return arrayOfInt;
  }
  
  View findFirstVisibleItemClosestToEnd(boolean paramBoolean)
  {
    int j = this.mPrimaryOrientation.getStartAfterPadding();
    int k = this.mPrimaryOrientation.getEndAfterPadding();
    Object localObject1 = null;
    int i = getChildCount() - 1;
    if (i >= 0)
    {
      View localView = getChildAt(i);
      int m = this.mPrimaryOrientation.getDecoratedStart(localView);
      int n = this.mPrimaryOrientation.getDecoratedEnd(localView);
      Object localObject2 = localObject1;
      if (n > j)
      {
        if (m < k) {
          break label92;
        }
        localObject2 = localObject1;
      }
      for (;;)
      {
        i -= 1;
        localObject1 = localObject2;
        break;
        label92:
        if ((n <= k) || (!paramBoolean)) {
          break label119;
        }
        localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = localView;
        }
      }
      label119:
      return localView;
    }
    return (View)localObject1;
  }
  
  View findFirstVisibleItemClosestToStart(boolean paramBoolean)
  {
    int j = this.mPrimaryOrientation.getStartAfterPadding();
    int k = this.mPrimaryOrientation.getEndAfterPadding();
    int m = getChildCount();
    Object localObject1 = null;
    int i = 0;
    if (i < m)
    {
      View localView = getChildAt(i);
      int n = this.mPrimaryOrientation.getDecoratedStart(localView);
      Object localObject2 = localObject1;
      if (this.mPrimaryOrientation.getDecoratedEnd(localView) > j)
      {
        if (n < k) {
          break label91;
        }
        localObject2 = localObject1;
      }
      for (;;)
      {
        i += 1;
        localObject1 = localObject2;
        break;
        label91:
        if ((n >= j) || (!paramBoolean)) {
          break label117;
        }
        localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = localView;
        }
      }
      label117:
      return localView;
    }
    return (View)localObject1;
  }
  
  int findFirstVisibleItemPositionInt()
  {
    if (this.mShouldReverseLayout) {}
    for (View localView = findFirstVisibleItemClosestToEnd(true); localView == null; localView = findFirstVisibleItemClosestToStart(true)) {
      return -1;
    }
    return getPosition(localView);
  }
  
  public int[] findFirstVisibleItemPositions(int[] paramArrayOfInt)
  {
    int[] arrayOfInt;
    if (paramArrayOfInt == null) {
      arrayOfInt = new int[this.mSpanCount];
    }
    do
    {
      int i = 0;
      while (i < this.mSpanCount)
      {
        arrayOfInt[i] = this.mSpans[i].findFirstVisibleItemPosition();
        i += 1;
      }
      arrayOfInt = paramArrayOfInt;
    } while (paramArrayOfInt.length >= this.mSpanCount);
    throw new IllegalArgumentException("Provided int[]'s size must be more than or equal to span count. Expected:" + this.mSpanCount + ", array size:" + paramArrayOfInt.length);
    return arrayOfInt;
  }
  
  public int[] findLastCompletelyVisibleItemPositions(int[] paramArrayOfInt)
  {
    int[] arrayOfInt;
    if (paramArrayOfInt == null) {
      arrayOfInt = new int[this.mSpanCount];
    }
    do
    {
      int i = 0;
      while (i < this.mSpanCount)
      {
        arrayOfInt[i] = this.mSpans[i].findLastCompletelyVisibleItemPosition();
        i += 1;
      }
      arrayOfInt = paramArrayOfInt;
    } while (paramArrayOfInt.length >= this.mSpanCount);
    throw new IllegalArgumentException("Provided int[]'s size must be more than or equal to span count. Expected:" + this.mSpanCount + ", array size:" + paramArrayOfInt.length);
    return arrayOfInt;
  }
  
  public int[] findLastVisibleItemPositions(int[] paramArrayOfInt)
  {
    int[] arrayOfInt;
    if (paramArrayOfInt == null) {
      arrayOfInt = new int[this.mSpanCount];
    }
    do
    {
      int i = 0;
      while (i < this.mSpanCount)
      {
        arrayOfInt[i] = this.mSpans[i].findLastVisibleItemPosition();
        i += 1;
      }
      arrayOfInt = paramArrayOfInt;
    } while (paramArrayOfInt.length >= this.mSpanCount);
    throw new IllegalArgumentException("Provided int[]'s size must be more than or equal to span count. Expected:" + this.mSpanCount + ", array size:" + paramArrayOfInt.length);
    return arrayOfInt;
  }
  
  int gatherPrefetchIndices(int paramInt1, int paramInt2, RecyclerView.State paramState, int[] paramArrayOfInt)
  {
    if (this.mOrientation == 0) {}
    while ((getChildCount() == 0) || (paramInt1 == 0))
    {
      return 0;
      paramInt1 = paramInt2;
    }
    prepareLayoutStateForDelta(paramInt1, paramState);
    paramInt2 = this.mSpanCount;
    paramInt1 = 0;
    while ((paramInt1 < this.mSpanCount) && (this.mLayoutState.hasMore(paramState)) && (paramInt2 > 0))
    {
      paramArrayOfInt[paramInt1] = this.mLayoutState.mCurrentPosition;
      paramInt2 -= 1;
      LayoutState localLayoutState = this.mLayoutState;
      localLayoutState.mCurrentPosition += this.mLayoutState.mItemDirection;
      paramInt1 += 1;
    }
    return paramInt1;
  }
  
  public RecyclerView.LayoutParams generateDefaultLayoutParams()
  {
    if (this.mOrientation == 0) {
      return new LayoutParams(-2, -1);
    }
    return new LayoutParams(-1, -2);
  }
  
  public RecyclerView.LayoutParams generateLayoutParams(Context paramContext, AttributeSet paramAttributeSet)
  {
    return new LayoutParams(paramContext, paramAttributeSet);
  }
  
  public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((paramLayoutParams instanceof ViewGroup.MarginLayoutParams)) {
      return new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams);
    }
    return new LayoutParams(paramLayoutParams);
  }
  
  public int getColumnCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (this.mOrientation == 1) {
      return this.mSpanCount;
    }
    return super.getColumnCountForAccessibility(paramRecycler, paramState);
  }
  
  public int getGapStrategy()
  {
    return this.mGapStrategy;
  }
  
  int getItemPrefetchCount()
  {
    return this.mSpanCount;
  }
  
  public int getOrientation()
  {
    return this.mOrientation;
  }
  
  public boolean getReverseLayout()
  {
    return this.mReverseLayout;
  }
  
  public int getRowCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (this.mOrientation == 0) {
      return this.mSpanCount;
    }
    return super.getRowCountForAccessibility(paramRecycler, paramState);
  }
  
  public int getSpanCount()
  {
    return this.mSpanCount;
  }
  
  View hasGapsToFix()
  {
    int i = getChildCount() - 1;
    BitSet localBitSet = new BitSet(this.mSpanCount);
    localBitSet.set(0, this.mSpanCount, true);
    int j;
    int k;
    if ((this.mOrientation == 1) && (isLayoutRTL()))
    {
      j = 1;
      if (!this.mShouldReverseLayout) {
        break label128;
      }
      k = -1;
      label57:
      if (i >= k) {
        break label141;
      }
    }
    int n;
    View localView;
    LayoutParams localLayoutParams;
    label128:
    label141:
    for (int m = 1;; m = -1)
    {
      n = i;
      if (n == k) {
        break label355;
      }
      localView = getChildAt(n);
      localLayoutParams = (LayoutParams)localView.getLayoutParams();
      if (!localBitSet.get(localLayoutParams.mSpan.mIndex)) {
        break label160;
      }
      if (!checkSpanForGap(localLayoutParams.mSpan)) {
        break label147;
      }
      return localView;
      j = -1;
      break;
      m = 0;
      k = i + 1;
      i = m;
      break label57;
    }
    label147:
    localBitSet.clear(localLayoutParams.mSpan.mIndex);
    label160:
    if (localLayoutParams.mFullSpan) {}
    label248:
    label284:
    label342:
    label344:
    label349:
    label353:
    for (;;)
    {
      n += m;
      break;
      if (n + m != k)
      {
        Object localObject = getChildAt(n + m);
        i = 0;
        int i2;
        if (this.mShouldReverseLayout)
        {
          i1 = this.mPrimaryOrientation.getDecoratedEnd(localView);
          i2 = this.mPrimaryOrientation.getDecoratedEnd((View)localObject);
          if (i1 < i2) {
            return localView;
          }
          if (i1 == i2) {
            i = 1;
          }
          if (i == 0) {
            break label342;
          }
          localObject = (LayoutParams)((View)localObject).getLayoutParams();
          if (localLayoutParams.mSpan.mIndex - ((LayoutParams)localObject).mSpan.mIndex >= 0) {
            break label344;
          }
          i = 1;
          if (j >= 0) {
            break label349;
          }
        }
        for (int i1 = 1;; i1 = 0)
        {
          if (i == i1) {
            break label353;
          }
          return localView;
          i1 = this.mPrimaryOrientation.getDecoratedStart(localView);
          i2 = this.mPrimaryOrientation.getDecoratedStart((View)localObject);
          if (i1 > i2) {
            return localView;
          }
          if (i1 != i2) {
            break label248;
          }
          i = 1;
          break label248;
          break;
          i = 0;
          break label284;
        }
      }
    }
    label355:
    return null;
  }
  
  public void invalidateSpanAssignments()
  {
    this.mLazySpanLookup.clear();
    requestLayout();
  }
  
  boolean isLayoutRTL()
  {
    return getLayoutDirection() == 1;
  }
  
  public void offsetChildrenHorizontal(int paramInt)
  {
    super.offsetChildrenHorizontal(paramInt);
    int i = 0;
    while (i < this.mSpanCount)
    {
      this.mSpans[i].onOffset(paramInt);
      i += 1;
    }
  }
  
  public void offsetChildrenVertical(int paramInt)
  {
    super.offsetChildrenVertical(paramInt);
    int i = 0;
    while (i < this.mSpanCount)
    {
      this.mSpans[i].onOffset(paramInt);
      i += 1;
    }
  }
  
  public void onDetachedFromWindow(RecyclerView paramRecyclerView, RecyclerView.Recycler paramRecycler)
  {
    removeCallbacks(this.mCheckForGapsRunnable);
    int i = 0;
    while (i < this.mSpanCount)
    {
      this.mSpans[i].clear();
      i += 1;
    }
    paramRecyclerView.requestLayout();
  }
  
  @Nullable
  public View onFocusSearchFailed(View paramView, int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (getChildCount() == 0) {
      return null;
    }
    paramView = findContainingItemView(paramView);
    if (paramView == null) {
      return null;
    }
    resolveShouldLayoutReverse();
    int j = convertFocusDirectionToLayoutDirection(paramInt);
    if (j == Integer.MIN_VALUE) {
      return null;
    }
    Object localObject = (LayoutParams)paramView.getLayoutParams();
    boolean bool = ((LayoutParams)localObject).mFullSpan;
    localObject = ((LayoutParams)localObject).mSpan;
    if (j == 1) {}
    for (paramInt = getLastChildPosition();; paramInt = getFirstChildPosition())
    {
      updateLayoutState(paramInt, paramState);
      setLayoutStateDirection(j);
      this.mLayoutState.mCurrentPosition = (this.mLayoutState.mItemDirection + paramInt);
      this.mLayoutState.mAvailable = ((int)(this.mPrimaryOrientation.getTotalSpace() * 0.33333334F));
      this.mLayoutState.mStopInFocusable = true;
      this.mLayoutState.mRecycle = false;
      fill(paramRecycler, this.mLayoutState, paramState);
      this.mLastLayoutFromEnd = this.mShouldReverseLayout;
      if (bool) {
        break;
      }
      paramRecycler = ((Span)localObject).getFocusableViewAfter(paramInt, j);
      if ((paramRecycler == null) || (paramRecycler == paramView)) {
        break;
      }
      return paramRecycler;
    }
    if (preferLastSpan(j))
    {
      i = this.mSpanCount - 1;
      while (i >= 0)
      {
        paramRecycler = this.mSpans[i].getFocusableViewAfter(paramInt, j);
        if ((paramRecycler != null) && (paramRecycler != paramView)) {
          return paramRecycler;
        }
        i -= 1;
      }
    }
    int i = 0;
    while (i < this.mSpanCount)
    {
      paramRecycler = this.mSpans[i].getFocusableViewAfter(paramInt, j);
      if ((paramRecycler != null) && (paramRecycler != paramView)) {
        return paramRecycler;
      }
      i += 1;
    }
    return null;
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    int i;
    int j;
    if (getChildCount() > 0)
    {
      paramAccessibilityEvent = AccessibilityEventCompat.asRecord(paramAccessibilityEvent);
      View localView1 = findFirstVisibleItemClosestToStart(false);
      View localView2 = findFirstVisibleItemClosestToEnd(false);
      if ((localView1 == null) || (localView2 == null)) {
        return;
      }
      i = getPosition(localView1);
      j = getPosition(localView2);
      if (i < j)
      {
        paramAccessibilityEvent.setFromIndex(i);
        paramAccessibilityEvent.setToIndex(j);
      }
    }
    else
    {
      return;
    }
    paramAccessibilityEvent.setFromIndex(j);
    paramAccessibilityEvent.setToIndex(i);
  }
  
  public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
  {
    paramRecycler = paramView.getLayoutParams();
    if (!(paramRecycler instanceof LayoutParams))
    {
      super.onInitializeAccessibilityNodeInfoForItem(paramView, paramAccessibilityNodeInfoCompat);
      return;
    }
    paramRecycler = (LayoutParams)paramRecycler;
    if (this.mOrientation == 0)
    {
      j = paramRecycler.getSpanIndex();
      if (paramRecycler.mFullSpan) {}
      for (i = this.mSpanCount;; i = 1)
      {
        paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(j, i, -1, -1, paramRecycler.mFullSpan, false));
        return;
      }
    }
    int j = paramRecycler.getSpanIndex();
    if (paramRecycler.mFullSpan) {}
    for (int i = this.mSpanCount;; i = 1)
    {
      paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(-1, -1, j, i, paramRecycler.mFullSpan, false));
      return;
    }
  }
  
  public void onItemsAdded(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    handleUpdate(paramInt1, paramInt2, 1);
  }
  
  public void onItemsChanged(RecyclerView paramRecyclerView)
  {
    this.mLazySpanLookup.clear();
    requestLayout();
  }
  
  public void onItemsMoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, int paramInt3)
  {
    handleUpdate(paramInt1, paramInt2, 8);
  }
  
  public void onItemsRemoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    handleUpdate(paramInt1, paramInt2, 2);
  }
  
  public void onItemsUpdated(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, Object paramObject)
  {
    handleUpdate(paramInt1, paramInt2, 4);
  }
  
  public void onLayoutChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    onLayoutChildren(paramRecycler, paramState, true);
  }
  
  public void onLayoutCompleted(RecyclerView.State paramState)
  {
    super.onLayoutCompleted(paramState);
    this.mPendingScrollPosition = -1;
    this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
    this.mPendingSavedState = null;
    this.mAnchorInfo.reset();
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if ((paramParcelable instanceof SavedState))
    {
      this.mPendingSavedState = ((SavedState)paramParcelable);
      requestLayout();
    }
  }
  
  public Parcelable onSaveInstanceState()
  {
    if (this.mPendingSavedState != null) {
      return new SavedState(this.mPendingSavedState);
    }
    SavedState localSavedState = new SavedState();
    localSavedState.mReverseLayout = this.mReverseLayout;
    localSavedState.mAnchorLayoutFromEnd = this.mLastLayoutFromEnd;
    localSavedState.mLastLayoutRTL = this.mLastLayoutRTL;
    int i;
    label126:
    int j;
    label163:
    int k;
    if ((this.mLazySpanLookup != null) && (this.mLazySpanLookup.mData != null))
    {
      localSavedState.mSpanLookup = this.mLazySpanLookup.mData;
      localSavedState.mSpanLookupSize = localSavedState.mSpanLookup.length;
      localSavedState.mFullSpanItems = this.mLazySpanLookup.mFullSpanItems;
      if (getChildCount() <= 0) {
        break label273;
      }
      if (!this.mLastLayoutFromEnd) {
        break label232;
      }
      i = getLastChildPosition();
      localSavedState.mAnchorPosition = i;
      localSavedState.mVisibleAnchorPosition = findFirstVisibleItemPositionInt();
      localSavedState.mSpanOffsetsSize = this.mSpanCount;
      localSavedState.mSpanOffsets = new int[this.mSpanCount];
      j = 0;
      if (j >= this.mSpanCount) {
        break label291;
      }
      if (!this.mLastLayoutFromEnd) {
        break label240;
      }
      k = this.mSpans[j].getEndLine(Integer.MIN_VALUE);
      i = k;
      if (k != Integer.MIN_VALUE) {
        i = k - this.mPrimaryOrientation.getEndAfterPadding();
      }
    }
    for (;;)
    {
      localSavedState.mSpanOffsets[j] = i;
      j += 1;
      break label163;
      localSavedState.mSpanLookupSize = 0;
      break;
      label232:
      i = getFirstChildPosition();
      break label126;
      label240:
      k = this.mSpans[j].getStartLine(Integer.MIN_VALUE);
      i = k;
      if (k != Integer.MIN_VALUE) {
        i = k - this.mPrimaryOrientation.getStartAfterPadding();
      }
    }
    label273:
    localSavedState.mAnchorPosition = -1;
    localSavedState.mVisibleAnchorPosition = -1;
    localSavedState.mSpanOffsetsSize = 0;
    label291:
    return localSavedState;
  }
  
  public void onScrollStateChanged(int paramInt)
  {
    if (paramInt == 0) {
      checkForGaps();
    }
  }
  
  void prepareLayoutStateForDelta(int paramInt, RecyclerView.State paramState)
  {
    int i;
    if (paramInt > 0) {
      i = 1;
    }
    for (int j = getLastChildPosition();; j = getFirstChildPosition())
    {
      this.mLayoutState.mRecycle = true;
      updateLayoutState(j, paramState);
      setLayoutStateDirection(i);
      this.mLayoutState.mCurrentPosition = (this.mLayoutState.mItemDirection + j);
      paramInt = Math.abs(paramInt);
      this.mLayoutState.mAvailable = paramInt;
      return;
      i = -1;
    }
  }
  
  int scrollBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if ((getChildCount() == 0) || (paramInt == 0)) {
      return 0;
    }
    prepareLayoutStateForDelta(paramInt, paramState);
    int i = fill(paramRecycler, this.mLayoutState, paramState);
    if (this.mLayoutState.mAvailable < i) {}
    for (;;)
    {
      this.mPrimaryOrientation.offsetChildren(-paramInt);
      this.mLastLayoutFromEnd = this.mShouldReverseLayout;
      this.mLayoutState.mAvailable = 0;
      recycle(paramRecycler, this.mLayoutState);
      return paramInt;
      if (paramInt < 0) {
        paramInt = -i;
      } else {
        paramInt = i;
      }
    }
  }
  
  public int scrollHorizontallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    return scrollBy(paramInt, paramRecycler, paramState);
  }
  
  public void scrollToPosition(int paramInt)
  {
    if ((this.mPendingSavedState != null) && (this.mPendingSavedState.mAnchorPosition != paramInt)) {
      this.mPendingSavedState.invalidateAnchorPositionInfo();
    }
    this.mPendingScrollPosition = paramInt;
    this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
    requestLayout();
  }
  
  public void scrollToPositionWithOffset(int paramInt1, int paramInt2)
  {
    if (this.mPendingSavedState != null) {
      this.mPendingSavedState.invalidateAnchorPositionInfo();
    }
    this.mPendingScrollPosition = paramInt1;
    this.mPendingScrollPositionOffset = paramInt2;
    requestLayout();
  }
  
  public int scrollVerticallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    return scrollBy(paramInt, paramRecycler, paramState);
  }
  
  public void setGapStrategy(int paramInt)
  {
    boolean bool = false;
    assertNotInLayoutOrScroll(null);
    if (paramInt == this.mGapStrategy) {
      return;
    }
    if ((paramInt != 0) && (paramInt != 2)) {
      throw new IllegalArgumentException("invalid gap strategy. Must be GAP_HANDLING_NONE or GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS");
    }
    this.mGapStrategy = paramInt;
    if (this.mGapStrategy != 0) {
      bool = true;
    }
    setAutoMeasureEnabled(bool);
    requestLayout();
  }
  
  public void setMeasuredDimension(Rect paramRect, int paramInt1, int paramInt2)
  {
    int j = getPaddingLeft() + getPaddingRight();
    int k = getPaddingTop() + getPaddingBottom();
    int i;
    if (this.mOrientation == 1)
    {
      i = chooseSize(paramInt2, paramRect.height() + k, getMinimumHeight());
      paramInt2 = chooseSize(paramInt1, this.mSizePerSpan * this.mSpanCount + j, getMinimumWidth());
      paramInt1 = i;
    }
    for (;;)
    {
      setMeasuredDimension(paramInt2, paramInt1);
      return;
      i = chooseSize(paramInt1, paramRect.width() + j, getMinimumWidth());
      paramInt1 = chooseSize(paramInt2, this.mSizePerSpan * this.mSpanCount + k, getMinimumHeight());
      paramInt2 = i;
    }
  }
  
  public void setOrientation(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1)) {
      throw new IllegalArgumentException("invalid orientation.");
    }
    assertNotInLayoutOrScroll(null);
    if (paramInt == this.mOrientation) {
      return;
    }
    this.mOrientation = paramInt;
    OrientationHelper localOrientationHelper = this.mPrimaryOrientation;
    this.mPrimaryOrientation = this.mSecondaryOrientation;
    this.mSecondaryOrientation = localOrientationHelper;
    requestLayout();
  }
  
  public void setReverseLayout(boolean paramBoolean)
  {
    assertNotInLayoutOrScroll(null);
    if ((this.mPendingSavedState != null) && (this.mPendingSavedState.mReverseLayout != paramBoolean)) {
      this.mPendingSavedState.mReverseLayout = paramBoolean;
    }
    this.mReverseLayout = paramBoolean;
    requestLayout();
  }
  
  public void setSpanCount(int paramInt)
  {
    assertNotInLayoutOrScroll(null);
    if (paramInt != this.mSpanCount)
    {
      invalidateSpanAssignments();
      this.mSpanCount = paramInt;
      this.mRemainingSpans = new BitSet(this.mSpanCount);
      this.mSpans = new Span[this.mSpanCount];
      paramInt = 0;
      while (paramInt < this.mSpanCount)
      {
        this.mSpans[paramInt] = new Span(paramInt);
        paramInt += 1;
      }
      requestLayout();
    }
  }
  
  public void smoothScrollToPosition(RecyclerView paramRecyclerView, RecyclerView.State paramState, int paramInt)
  {
    paramRecyclerView = new LinearSmoothScroller(paramRecyclerView.getContext());
    paramRecyclerView.setTargetPosition(paramInt);
    startSmoothScroll(paramRecyclerView);
  }
  
  public boolean supportsPredictiveItemAnimations()
  {
    return this.mPendingSavedState == null;
  }
  
  boolean updateAnchorFromPendingData(RecyclerView.State paramState, AnchorInfo paramAnchorInfo)
  {
    boolean bool = false;
    if ((paramState.isPreLayout()) || (this.mPendingScrollPosition == -1)) {
      return false;
    }
    if ((this.mPendingScrollPosition < 0) || (this.mPendingScrollPosition >= paramState.getItemCount()))
    {
      this.mPendingScrollPosition = -1;
      this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
      return false;
    }
    if ((this.mPendingSavedState == null) || (this.mPendingSavedState.mAnchorPosition == -1))
    {
      paramState = findViewByPosition(this.mPendingScrollPosition);
      if (paramState == null) {
        break label320;
      }
      if (!this.mShouldReverseLayout) {
        break label169;
      }
    }
    label169:
    for (int i = getLastChildPosition();; i = getFirstChildPosition())
    {
      paramAnchorInfo.mPosition = i;
      if (this.mPendingScrollPositionOffset == Integer.MIN_VALUE) {
        break label204;
      }
      if (!paramAnchorInfo.mLayoutFromEnd) {
        break label177;
      }
      paramAnchorInfo.mOffset = (this.mPrimaryOrientation.getEndAfterPadding() - this.mPendingScrollPositionOffset - this.mPrimaryOrientation.getDecoratedEnd(paramState));
      return true;
      if (this.mPendingSavedState.mSpanOffsetsSize < 1) {
        break;
      }
      paramAnchorInfo.mOffset = Integer.MIN_VALUE;
      paramAnchorInfo.mPosition = this.mPendingScrollPosition;
      return true;
    }
    label177:
    paramAnchorInfo.mOffset = (this.mPrimaryOrientation.getStartAfterPadding() + this.mPendingScrollPositionOffset - this.mPrimaryOrientation.getDecoratedStart(paramState));
    return true;
    label204:
    if (this.mPrimaryOrientation.getDecoratedMeasurement(paramState) > this.mPrimaryOrientation.getTotalSpace())
    {
      if (paramAnchorInfo.mLayoutFromEnd) {}
      for (i = this.mPrimaryOrientation.getEndAfterPadding();; i = this.mPrimaryOrientation.getStartAfterPadding())
      {
        paramAnchorInfo.mOffset = i;
        return true;
      }
    }
    i = this.mPrimaryOrientation.getDecoratedStart(paramState) - this.mPrimaryOrientation.getStartAfterPadding();
    if (i < 0)
    {
      paramAnchorInfo.mOffset = (-i);
      return true;
    }
    i = this.mPrimaryOrientation.getEndAfterPadding() - this.mPrimaryOrientation.getDecoratedEnd(paramState);
    if (i < 0)
    {
      paramAnchorInfo.mOffset = i;
      return true;
    }
    paramAnchorInfo.mOffset = Integer.MIN_VALUE;
    return true;
    label320:
    paramAnchorInfo.mPosition = this.mPendingScrollPosition;
    if (this.mPendingScrollPositionOffset == Integer.MIN_VALUE)
    {
      if (calculateScrollDirectionForPosition(paramAnchorInfo.mPosition) == 1) {
        bool = true;
      }
      paramAnchorInfo.mLayoutFromEnd = bool;
      paramAnchorInfo.assignCoordinateFromPadding();
    }
    for (;;)
    {
      paramAnchorInfo.mInvalidateOffsets = true;
      return true;
      paramAnchorInfo.assignCoordinateFromPadding(this.mPendingScrollPositionOffset);
    }
  }
  
  void updateAnchorInfoForLayout(RecyclerView.State paramState, AnchorInfo paramAnchorInfo)
  {
    if (updateAnchorFromPendingData(paramState, paramAnchorInfo)) {
      return;
    }
    if (updateAnchorFromChildren(paramState, paramAnchorInfo)) {
      return;
    }
    paramAnchorInfo.assignCoordinateFromPadding();
    paramAnchorInfo.mPosition = 0;
  }
  
  void updateMeasureSpecs(int paramInt)
  {
    this.mSizePerSpan = (paramInt / this.mSpanCount);
    this.mFullSizeSpec = View.MeasureSpec.makeMeasureSpec(paramInt, this.mSecondaryOrientation.getMode());
  }
  
  class AnchorInfo
  {
    boolean mInvalidateOffsets;
    boolean mLayoutFromEnd;
    int mOffset;
    int mPosition;
    int[] mSpanReferenceLines;
    boolean mValid;
    
    public AnchorInfo()
    {
      reset();
    }
    
    void assignCoordinateFromPadding()
    {
      if (this.mLayoutFromEnd) {}
      for (int i = StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding();; i = StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding())
      {
        this.mOffset = i;
        return;
      }
    }
    
    void assignCoordinateFromPadding(int paramInt)
    {
      if (this.mLayoutFromEnd)
      {
        this.mOffset = (StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding() - paramInt);
        return;
      }
      this.mOffset = (StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding() + paramInt);
    }
    
    void reset()
    {
      this.mPosition = -1;
      this.mOffset = Integer.MIN_VALUE;
      this.mLayoutFromEnd = false;
      this.mInvalidateOffsets = false;
      this.mValid = false;
      if (this.mSpanReferenceLines != null) {
        Arrays.fill(this.mSpanReferenceLines, -1);
      }
    }
    
    void saveSpanReferenceLines(StaggeredGridLayoutManager.Span[] paramArrayOfSpan)
    {
      int j = paramArrayOfSpan.length;
      if ((this.mSpanReferenceLines == null) || (this.mSpanReferenceLines.length < j)) {
        this.mSpanReferenceLines = new int[StaggeredGridLayoutManager.this.mSpans.length];
      }
      int i = 0;
      while (i < j)
      {
        this.mSpanReferenceLines[i] = paramArrayOfSpan[i].getStartLine(Integer.MIN_VALUE);
        i += 1;
      }
    }
  }
  
  public static class LayoutParams
    extends RecyclerView.LayoutParams
  {
    public static final int INVALID_SPAN_ID = -1;
    boolean mFullSpan;
    StaggeredGridLayoutManager.Span mSpan;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public LayoutParams(RecyclerView.LayoutParams paramLayoutParams)
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
    
    public final int getSpanIndex()
    {
      if (this.mSpan == null) {
        return -1;
      }
      return this.mSpan.mIndex;
    }
    
    public boolean isFullSpan()
    {
      return this.mFullSpan;
    }
    
    public void setFullSpan(boolean paramBoolean)
    {
      this.mFullSpan = paramBoolean;
    }
  }
  
  static class LazySpanLookup
  {
    private static final int MIN_SIZE = 10;
    int[] mData;
    List<FullSpanItem> mFullSpanItems;
    
    private int invalidateFullSpansAfter(int paramInt)
    {
      if (this.mFullSpanItems == null) {
        return -1;
      }
      FullSpanItem localFullSpanItem = getFullSpanItem(paramInt);
      if (localFullSpanItem != null) {
        this.mFullSpanItems.remove(localFullSpanItem);
      }
      int k = -1;
      int m = this.mFullSpanItems.size();
      int i = 0;
      for (;;)
      {
        int j = k;
        if (i < m)
        {
          if (((FullSpanItem)this.mFullSpanItems.get(i)).mPosition >= paramInt) {
            j = i;
          }
        }
        else
        {
          if (j == -1) {
            break;
          }
          localFullSpanItem = (FullSpanItem)this.mFullSpanItems.get(j);
          this.mFullSpanItems.remove(j);
          return localFullSpanItem.mPosition;
        }
        i += 1;
      }
      return -1;
    }
    
    private void offsetFullSpansForAddition(int paramInt1, int paramInt2)
    {
      if (this.mFullSpanItems == null) {
        return;
      }
      int i = this.mFullSpanItems.size() - 1;
      if (i >= 0)
      {
        FullSpanItem localFullSpanItem = (FullSpanItem)this.mFullSpanItems.get(i);
        if (localFullSpanItem.mPosition < paramInt1) {}
        for (;;)
        {
          i -= 1;
          break;
          localFullSpanItem.mPosition += paramInt2;
        }
      }
    }
    
    private void offsetFullSpansForRemoval(int paramInt1, int paramInt2)
    {
      if (this.mFullSpanItems == null) {
        return;
      }
      int i = this.mFullSpanItems.size() - 1;
      if (i >= 0)
      {
        FullSpanItem localFullSpanItem = (FullSpanItem)this.mFullSpanItems.get(i);
        if (localFullSpanItem.mPosition < paramInt1) {}
        for (;;)
        {
          i -= 1;
          break;
          if (localFullSpanItem.mPosition < paramInt1 + paramInt2) {
            this.mFullSpanItems.remove(i);
          } else {
            localFullSpanItem.mPosition -= paramInt2;
          }
        }
      }
    }
    
    public void addFullSpanItem(FullSpanItem paramFullSpanItem)
    {
      if (this.mFullSpanItems == null) {
        this.mFullSpanItems = new ArrayList();
      }
      int j = this.mFullSpanItems.size();
      int i = 0;
      while (i < j)
      {
        FullSpanItem localFullSpanItem = (FullSpanItem)this.mFullSpanItems.get(i);
        if (localFullSpanItem.mPosition == paramFullSpanItem.mPosition) {
          this.mFullSpanItems.remove(i);
        }
        if (localFullSpanItem.mPosition >= paramFullSpanItem.mPosition)
        {
          this.mFullSpanItems.add(i, paramFullSpanItem);
          return;
        }
        i += 1;
      }
      this.mFullSpanItems.add(paramFullSpanItem);
    }
    
    void clear()
    {
      if (this.mData != null) {
        Arrays.fill(this.mData, -1);
      }
      this.mFullSpanItems = null;
    }
    
    void ensureSize(int paramInt)
    {
      if (this.mData == null)
      {
        this.mData = new int[Math.max(paramInt, 10) + 1];
        Arrays.fill(this.mData, -1);
      }
      while (paramInt < this.mData.length) {
        return;
      }
      int[] arrayOfInt = this.mData;
      this.mData = new int[sizeForPosition(paramInt)];
      System.arraycopy(arrayOfInt, 0, this.mData, 0, arrayOfInt.length);
      Arrays.fill(this.mData, arrayOfInt.length, this.mData.length, -1);
    }
    
    int forceInvalidateAfter(int paramInt)
    {
      if (this.mFullSpanItems != null)
      {
        int i = this.mFullSpanItems.size() - 1;
        while (i >= 0)
        {
          if (((FullSpanItem)this.mFullSpanItems.get(i)).mPosition >= paramInt) {
            this.mFullSpanItems.remove(i);
          }
          i -= 1;
        }
      }
      return invalidateAfter(paramInt);
    }
    
    public FullSpanItem getFirstFullSpanItemInRange(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
    {
      if (this.mFullSpanItems == null) {
        return null;
      }
      int j = this.mFullSpanItems.size();
      int i = 0;
      while (i < j)
      {
        FullSpanItem localFullSpanItem = (FullSpanItem)this.mFullSpanItems.get(i);
        if (localFullSpanItem.mPosition >= paramInt2) {
          return null;
        }
        if (localFullSpanItem.mPosition >= paramInt1)
        {
          if ((paramInt3 == 0) || (localFullSpanItem.mGapDir == paramInt3)) {}
          while ((paramBoolean) && (localFullSpanItem.mHasUnwantedGapAfter)) {
            return localFullSpanItem;
          }
        }
        i += 1;
      }
      return null;
    }
    
    public FullSpanItem getFullSpanItem(int paramInt)
    {
      if (this.mFullSpanItems == null) {
        return null;
      }
      int i = this.mFullSpanItems.size() - 1;
      while (i >= 0)
      {
        FullSpanItem localFullSpanItem = (FullSpanItem)this.mFullSpanItems.get(i);
        if (localFullSpanItem.mPosition == paramInt) {
          return localFullSpanItem;
        }
        i -= 1;
      }
      return null;
    }
    
    int getSpan(int paramInt)
    {
      if ((this.mData == null) || (paramInt >= this.mData.length)) {
        return -1;
      }
      return this.mData[paramInt];
    }
    
    int invalidateAfter(int paramInt)
    {
      if (this.mData == null) {
        return -1;
      }
      if (paramInt >= this.mData.length) {
        return -1;
      }
      int i = invalidateFullSpansAfter(paramInt);
      if (i == -1)
      {
        Arrays.fill(this.mData, paramInt, this.mData.length, -1);
        return this.mData.length;
      }
      Arrays.fill(this.mData, paramInt, i + 1, -1);
      return i + 1;
    }
    
    void offsetForAddition(int paramInt1, int paramInt2)
    {
      if ((this.mData == null) || (paramInt1 >= this.mData.length)) {
        return;
      }
      ensureSize(paramInt1 + paramInt2);
      System.arraycopy(this.mData, paramInt1, this.mData, paramInt1 + paramInt2, this.mData.length - paramInt1 - paramInt2);
      Arrays.fill(this.mData, paramInt1, paramInt1 + paramInt2, -1);
      offsetFullSpansForAddition(paramInt1, paramInt2);
    }
    
    void offsetForRemoval(int paramInt1, int paramInt2)
    {
      if ((this.mData == null) || (paramInt1 >= this.mData.length)) {
        return;
      }
      ensureSize(paramInt1 + paramInt2);
      System.arraycopy(this.mData, paramInt1 + paramInt2, this.mData, paramInt1, this.mData.length - paramInt1 - paramInt2);
      Arrays.fill(this.mData, this.mData.length - paramInt2, this.mData.length, -1);
      offsetFullSpansForRemoval(paramInt1, paramInt2);
    }
    
    void setSpan(int paramInt, StaggeredGridLayoutManager.Span paramSpan)
    {
      ensureSize(paramInt);
      this.mData[paramInt] = paramSpan.mIndex;
    }
    
    int sizeForPosition(int paramInt)
    {
      int i = this.mData.length;
      while (i <= paramInt) {
        i *= 2;
      }
      return i;
    }
    
    static class FullSpanItem
      implements Parcelable
    {
      public static final Parcelable.Creator<FullSpanItem> CREATOR = new Parcelable.Creator()
      {
        public StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem createFromParcel(Parcel paramAnonymousParcel)
        {
          return new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem(paramAnonymousParcel);
        }
        
        public StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem[] newArray(int paramAnonymousInt)
        {
          return new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem[paramAnonymousInt];
        }
      };
      int mGapDir;
      int[] mGapPerSpan;
      boolean mHasUnwantedGapAfter;
      int mPosition;
      
      public FullSpanItem() {}
      
      public FullSpanItem(Parcel paramParcel)
      {
        this.mPosition = paramParcel.readInt();
        this.mGapDir = paramParcel.readInt();
        if (paramParcel.readInt() == 1) {}
        for (;;)
        {
          this.mHasUnwantedGapAfter = bool;
          int i = paramParcel.readInt();
          if (i > 0)
          {
            this.mGapPerSpan = new int[i];
            paramParcel.readIntArray(this.mGapPerSpan);
          }
          return;
          bool = false;
        }
      }
      
      public int describeContents()
      {
        return 0;
      }
      
      int getGapForSpan(int paramInt)
      {
        if (this.mGapPerSpan == null) {
          return 0;
        }
        return this.mGapPerSpan[paramInt];
      }
      
      public String toString()
      {
        return "FullSpanItem{mPosition=" + this.mPosition + ", mGapDir=" + this.mGapDir + ", mHasUnwantedGapAfter=" + this.mHasUnwantedGapAfter + ", mGapPerSpan=" + Arrays.toString(this.mGapPerSpan) + '}';
      }
      
      public void writeToParcel(Parcel paramParcel, int paramInt)
      {
        paramParcel.writeInt(this.mPosition);
        paramParcel.writeInt(this.mGapDir);
        if (this.mHasUnwantedGapAfter) {}
        for (paramInt = 1;; paramInt = 0)
        {
          paramParcel.writeInt(paramInt);
          if ((this.mGapPerSpan == null) || (this.mGapPerSpan.length <= 0)) {
            break;
          }
          paramParcel.writeInt(this.mGapPerSpan.length);
          paramParcel.writeIntArray(this.mGapPerSpan);
          return;
        }
        paramParcel.writeInt(0);
      }
    }
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public static class SavedState
    implements Parcelable
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public StaggeredGridLayoutManager.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new StaggeredGridLayoutManager.SavedState(paramAnonymousParcel);
      }
      
      public StaggeredGridLayoutManager.SavedState[] newArray(int paramAnonymousInt)
      {
        return new StaggeredGridLayoutManager.SavedState[paramAnonymousInt];
      }
    };
    boolean mAnchorLayoutFromEnd;
    int mAnchorPosition;
    List<StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem> mFullSpanItems;
    boolean mLastLayoutRTL;
    boolean mReverseLayout;
    int[] mSpanLookup;
    int mSpanLookupSize;
    int[] mSpanOffsets;
    int mSpanOffsetsSize;
    int mVisibleAnchorPosition;
    
    public SavedState() {}
    
    SavedState(Parcel paramParcel)
    {
      this.mAnchorPosition = paramParcel.readInt();
      this.mVisibleAnchorPosition = paramParcel.readInt();
      this.mSpanOffsetsSize = paramParcel.readInt();
      if (this.mSpanOffsetsSize > 0)
      {
        this.mSpanOffsets = new int[this.mSpanOffsetsSize];
        paramParcel.readIntArray(this.mSpanOffsets);
      }
      this.mSpanLookupSize = paramParcel.readInt();
      if (this.mSpanLookupSize > 0)
      {
        this.mSpanLookup = new int[this.mSpanLookupSize];
        paramParcel.readIntArray(this.mSpanLookup);
      }
      if (paramParcel.readInt() == 1)
      {
        bool1 = true;
        this.mReverseLayout = bool1;
        if (paramParcel.readInt() != 1) {
          break label152;
        }
        bool1 = true;
        label113:
        this.mAnchorLayoutFromEnd = bool1;
        if (paramParcel.readInt() != 1) {
          break label157;
        }
      }
      label152:
      label157:
      for (boolean bool1 = bool2;; bool1 = false)
      {
        this.mLastLayoutRTL = bool1;
        this.mFullSpanItems = paramParcel.readArrayList(StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem.class.getClassLoader());
        return;
        bool1 = false;
        break;
        bool1 = false;
        break label113;
      }
    }
    
    public SavedState(SavedState paramSavedState)
    {
      this.mSpanOffsetsSize = paramSavedState.mSpanOffsetsSize;
      this.mAnchorPosition = paramSavedState.mAnchorPosition;
      this.mVisibleAnchorPosition = paramSavedState.mVisibleAnchorPosition;
      this.mSpanOffsets = paramSavedState.mSpanOffsets;
      this.mSpanLookupSize = paramSavedState.mSpanLookupSize;
      this.mSpanLookup = paramSavedState.mSpanLookup;
      this.mReverseLayout = paramSavedState.mReverseLayout;
      this.mAnchorLayoutFromEnd = paramSavedState.mAnchorLayoutFromEnd;
      this.mLastLayoutRTL = paramSavedState.mLastLayoutRTL;
      this.mFullSpanItems = paramSavedState.mFullSpanItems;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    void invalidateAnchorPositionInfo()
    {
      this.mSpanOffsets = null;
      this.mSpanOffsetsSize = 0;
      this.mAnchorPosition = -1;
      this.mVisibleAnchorPosition = -1;
    }
    
    void invalidateSpanInfo()
    {
      this.mSpanOffsets = null;
      this.mSpanOffsetsSize = 0;
      this.mSpanLookupSize = 0;
      this.mSpanLookup = null;
      this.mFullSpanItems = null;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      int i = 1;
      paramParcel.writeInt(this.mAnchorPosition);
      paramParcel.writeInt(this.mVisibleAnchorPosition);
      paramParcel.writeInt(this.mSpanOffsetsSize);
      if (this.mSpanOffsetsSize > 0) {
        paramParcel.writeIntArray(this.mSpanOffsets);
      }
      paramParcel.writeInt(this.mSpanLookupSize);
      if (this.mSpanLookupSize > 0) {
        paramParcel.writeIntArray(this.mSpanLookup);
      }
      if (this.mReverseLayout)
      {
        paramInt = 1;
        paramParcel.writeInt(paramInt);
        if (!this.mAnchorLayoutFromEnd) {
          break label120;
        }
        paramInt = 1;
        label87:
        paramParcel.writeInt(paramInt);
        if (!this.mLastLayoutRTL) {
          break label125;
        }
      }
      label120:
      label125:
      for (paramInt = i;; paramInt = 0)
      {
        paramParcel.writeInt(paramInt);
        paramParcel.writeList(this.mFullSpanItems);
        return;
        paramInt = 0;
        break;
        paramInt = 0;
        break label87;
      }
    }
  }
  
  class Span
  {
    static final int INVALID_LINE = Integer.MIN_VALUE;
    int mCachedEnd = Integer.MIN_VALUE;
    int mCachedStart = Integer.MIN_VALUE;
    int mDeletedSize = 0;
    final int mIndex;
    ArrayList<View> mViews = new ArrayList();
    
    Span(int paramInt)
    {
      this.mIndex = paramInt;
    }
    
    void appendToSpan(View paramView)
    {
      StaggeredGridLayoutManager.LayoutParams localLayoutParams = getLayoutParams(paramView);
      localLayoutParams.mSpan = this;
      this.mViews.add(paramView);
      this.mCachedEnd = Integer.MIN_VALUE;
      if (this.mViews.size() == 1) {
        this.mCachedStart = Integer.MIN_VALUE;
      }
      if ((localLayoutParams.isItemRemoved()) || (localLayoutParams.isItemChanged())) {
        this.mDeletedSize += StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(paramView);
      }
    }
    
    void cacheReferenceLineAndClear(boolean paramBoolean, int paramInt)
    {
      if (paramBoolean) {}
      for (int i = getEndLine(Integer.MIN_VALUE);; i = getStartLine(Integer.MIN_VALUE))
      {
        clear();
        if (i != Integer.MIN_VALUE) {
          break;
        }
        return;
      }
      if ((paramBoolean) && (i < StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding())) {}
      while ((!paramBoolean) && (i > StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding())) {
        return;
      }
      int j = i;
      if (paramInt != Integer.MIN_VALUE) {
        j = i + paramInt;
      }
      this.mCachedEnd = j;
      this.mCachedStart = j;
    }
    
    void calculateCachedEnd()
    {
      Object localObject = (View)this.mViews.get(this.mViews.size() - 1);
      StaggeredGridLayoutManager.LayoutParams localLayoutParams = getLayoutParams((View)localObject);
      this.mCachedEnd = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedEnd((View)localObject);
      if (localLayoutParams.mFullSpan)
      {
        localObject = StaggeredGridLayoutManager.this.mLazySpanLookup.getFullSpanItem(localLayoutParams.getViewLayoutPosition());
        if ((localObject != null) && (((StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem)localObject).mGapDir == 1)) {
          this.mCachedEnd += ((StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem)localObject).getGapForSpan(this.mIndex);
        }
      }
    }
    
    void calculateCachedStart()
    {
      Object localObject = (View)this.mViews.get(0);
      StaggeredGridLayoutManager.LayoutParams localLayoutParams = getLayoutParams((View)localObject);
      this.mCachedStart = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedStart((View)localObject);
      if (localLayoutParams.mFullSpan)
      {
        localObject = StaggeredGridLayoutManager.this.mLazySpanLookup.getFullSpanItem(localLayoutParams.getViewLayoutPosition());
        if ((localObject != null) && (((StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem)localObject).mGapDir == -1)) {
          this.mCachedStart -= ((StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem)localObject).getGapForSpan(this.mIndex);
        }
      }
    }
    
    void clear()
    {
      this.mViews.clear();
      invalidateCache();
      this.mDeletedSize = 0;
    }
    
    public int findFirstCompletelyVisibleItemPosition()
    {
      if (StaggeredGridLayoutManager.this.mReverseLayout) {
        return findOneVisibleChild(this.mViews.size() - 1, -1, true);
      }
      return findOneVisibleChild(0, this.mViews.size(), true);
    }
    
    public int findFirstVisibleItemPosition()
    {
      if (StaggeredGridLayoutManager.this.mReverseLayout) {
        return findOneVisibleChild(this.mViews.size() - 1, -1, false);
      }
      return findOneVisibleChild(0, this.mViews.size(), false);
    }
    
    public int findLastCompletelyVisibleItemPosition()
    {
      if (StaggeredGridLayoutManager.this.mReverseLayout) {
        return findOneVisibleChild(0, this.mViews.size(), true);
      }
      return findOneVisibleChild(this.mViews.size() - 1, -1, true);
    }
    
    public int findLastVisibleItemPosition()
    {
      if (StaggeredGridLayoutManager.this.mReverseLayout) {
        return findOneVisibleChild(0, this.mViews.size(), false);
      }
      return findOneVisibleChild(this.mViews.size() - 1, -1, false);
    }
    
    int findOneVisibleChild(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      int j = StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding();
      int k = StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding();
      int i;
      if (paramInt2 > paramInt1) {
        i = 1;
      }
      while (paramInt1 != paramInt2)
      {
        View localView = (View)this.mViews.get(paramInt1);
        int m = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedStart(localView);
        int n = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedEnd(localView);
        if ((m < k) && (n > j)) {
          if (paramBoolean)
          {
            if ((m >= j) && (n <= k))
            {
              return StaggeredGridLayoutManager.this.getPosition(localView);
              i = -1;
            }
          }
          else {
            return StaggeredGridLayoutManager.this.getPosition(localView);
          }
        }
        paramInt1 += i;
      }
      return -1;
    }
    
    public int getDeletedSize()
    {
      return this.mDeletedSize;
    }
    
    int getEndLine()
    {
      if (this.mCachedEnd != Integer.MIN_VALUE) {
        return this.mCachedEnd;
      }
      calculateCachedEnd();
      return this.mCachedEnd;
    }
    
    int getEndLine(int paramInt)
    {
      if (this.mCachedEnd != Integer.MIN_VALUE) {
        return this.mCachedEnd;
      }
      if (this.mViews.size() == 0) {
        return paramInt;
      }
      calculateCachedEnd();
      return this.mCachedEnd;
    }
    
    public View getFocusableViewAfter(int paramInt1, int paramInt2)
    {
      Object localObject2 = null;
      Object localObject1 = null;
      int i;
      View localView;
      if (paramInt2 == -1)
      {
        i = this.mViews.size();
        paramInt2 = 0;
        localObject2 = localObject1;
        if (paramInt2 < i)
        {
          localView = (View)this.mViews.get(paramInt2);
          localObject2 = localObject1;
          if (localView.isFocusable())
          {
            if (StaggeredGridLayoutManager.this.getPosition(localView) > paramInt1) {}
            for (int k = 1;; k = 0)
            {
              localObject2 = localObject1;
              if (k != StaggeredGridLayoutManager.this.mReverseLayout) {
                break label211;
              }
              localObject1 = localView;
              paramInt2 += 1;
              break;
            }
          }
        }
      }
      else
      {
        paramInt2 = this.mViews.size() - 1;
        localObject1 = localObject2;
        localObject2 = localObject1;
        if (paramInt2 >= 0)
        {
          localView = (View)this.mViews.get(paramInt2);
          localObject2 = localObject1;
          if (localView.isFocusable())
          {
            if (StaggeredGridLayoutManager.this.getPosition(localView) > paramInt1)
            {
              i = 1;
              label166:
              if (!StaggeredGridLayoutManager.this.mReverseLayout) {
                break label205;
              }
            }
            label205:
            for (int j = 0;; j = 1)
            {
              localObject2 = localObject1;
              if (i != j) {
                break label211;
              }
              localObject1 = localView;
              paramInt2 -= 1;
              break;
              i = 0;
              break label166;
            }
          }
        }
      }
      label211:
      return (View)localObject2;
    }
    
    StaggeredGridLayoutManager.LayoutParams getLayoutParams(View paramView)
    {
      return (StaggeredGridLayoutManager.LayoutParams)paramView.getLayoutParams();
    }
    
    int getStartLine()
    {
      if (this.mCachedStart != Integer.MIN_VALUE) {
        return this.mCachedStart;
      }
      calculateCachedStart();
      return this.mCachedStart;
    }
    
    int getStartLine(int paramInt)
    {
      if (this.mCachedStart != Integer.MIN_VALUE) {
        return this.mCachedStart;
      }
      if (this.mViews.size() == 0) {
        return paramInt;
      }
      calculateCachedStart();
      return this.mCachedStart;
    }
    
    void invalidateCache()
    {
      this.mCachedStart = Integer.MIN_VALUE;
      this.mCachedEnd = Integer.MIN_VALUE;
    }
    
    void onOffset(int paramInt)
    {
      if (this.mCachedStart != Integer.MIN_VALUE) {
        this.mCachedStart += paramInt;
      }
      if (this.mCachedEnd != Integer.MIN_VALUE) {
        this.mCachedEnd += paramInt;
      }
    }
    
    void popEnd()
    {
      int i = this.mViews.size();
      View localView = (View)this.mViews.remove(i - 1);
      StaggeredGridLayoutManager.LayoutParams localLayoutParams = getLayoutParams(localView);
      localLayoutParams.mSpan = null;
      if ((localLayoutParams.isItemRemoved()) || (localLayoutParams.isItemChanged())) {
        this.mDeletedSize -= StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(localView);
      }
      if (i == 1) {
        this.mCachedStart = Integer.MIN_VALUE;
      }
      this.mCachedEnd = Integer.MIN_VALUE;
    }
    
    void popStart()
    {
      View localView = (View)this.mViews.remove(0);
      StaggeredGridLayoutManager.LayoutParams localLayoutParams = getLayoutParams(localView);
      localLayoutParams.mSpan = null;
      if (this.mViews.size() == 0) {
        this.mCachedEnd = Integer.MIN_VALUE;
      }
      if ((localLayoutParams.isItemRemoved()) || (localLayoutParams.isItemChanged())) {
        this.mDeletedSize -= StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(localView);
      }
      this.mCachedStart = Integer.MIN_VALUE;
    }
    
    void prependToSpan(View paramView)
    {
      StaggeredGridLayoutManager.LayoutParams localLayoutParams = getLayoutParams(paramView);
      localLayoutParams.mSpan = this;
      this.mViews.add(0, paramView);
      this.mCachedStart = Integer.MIN_VALUE;
      if (this.mViews.size() == 1) {
        this.mCachedEnd = Integer.MIN_VALUE;
      }
      if ((localLayoutParams.isItemRemoved()) || (localLayoutParams.isItemChanged())) {
        this.mDeletedSize += StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(paramView);
      }
    }
    
    void setLine(int paramInt)
    {
      this.mCachedStart = paramInt;
      this.mCachedEnd = paramInt;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\widget\StaggeredGridLayoutManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */