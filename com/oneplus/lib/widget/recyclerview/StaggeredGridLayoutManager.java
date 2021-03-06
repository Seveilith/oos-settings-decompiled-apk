package com.oneplus.lib.widget.recyclerview;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.CollectionItemInfo;
import android.view.accessibility.AccessibilityRecord;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class StaggeredGridLayoutManager
  extends RecyclerView.LayoutManager
{
  private static final boolean DEBUG = false;
  @Deprecated
  public static final int GAP_HANDLING_LAZY = 1;
  public static final int GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS = 2;
  public static final int GAP_HANDLING_NONE = 0;
  public static final int HORIZONTAL = 0;
  private static final int INVALID_OFFSET = Integer.MIN_VALUE;
  public static final String TAG = "StaggeredGridLayoutManager";
  public static final int VERTICAL = 1;
  private final AnchorInfo mAnchorInfo = new AnchorInfo(null);
  private final Runnable mCheckForGapsRunnable = new Runnable()
  {
    public void run()
    {
      StaggeredGridLayoutManager.-wrap0(StaggeredGridLayoutManager.this);
    }
  };
  private int mFullSizeSpec;
  private int mGapStrategy = 2;
  private int mHeightSpec;
  private boolean mLaidOutInvalidFullSpan = false;
  private boolean mLastLayoutFromEnd;
  private boolean mLastLayoutRTL;
  private LayoutState mLayoutState;
  LazySpanLookup mLazySpanLookup = new LazySpanLookup();
  private int mOrientation;
  private SavedState mPendingSavedState;
  int mPendingScrollPosition = -1;
  int mPendingScrollPositionOffset = Integer.MIN_VALUE;
  OrientationHelper mPrimaryOrientation;
  private BitSet mRemainingSpans;
  private boolean mReverseLayout = false;
  OrientationHelper mSecondaryOrientation;
  boolean mShouldReverseLayout = false;
  private int mSizePerSpan;
  private boolean mSmoothScrollbarEnabled = true;
  private int mSpanCount = -1;
  private Span[] mSpans;
  private final Rect mTmpRect = new Rect();
  private int mWidthSpec;
  
  public StaggeredGridLayoutManager(int paramInt1, int paramInt2)
  {
    this.mOrientation = paramInt2;
    setSpanCount(paramInt1);
  }
  
  public StaggeredGridLayoutManager(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    paramContext = getProperties(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setOrientation(paramContext.orientation);
    setSpanCount(paramContext.spanCount);
    setReverseLayout(paramContext.reverseLayout);
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
  
  private boolean checkForGaps()
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
  
  private boolean checkSpanForGap(Span paramSpan)
  {
    if (this.mShouldReverseLayout)
    {
      if (paramSpan.getEndLine() < this.mPrimaryOrientation.getEndAfterPadding()) {
        return true;
      }
    }
    else if (paramSpan.getStartLine() > this.mPrimaryOrientation.getStartAfterPadding()) {
      return true;
    }
    return false;
  }
  
  private int computeScrollExtent(RecyclerView.State paramState)
  {
    boolean bool2 = false;
    if (getChildCount() == 0) {
      return 0;
    }
    ensureOrientationHelper();
    OrientationHelper localOrientationHelper = this.mPrimaryOrientation;
    View localView;
    if (this.mSmoothScrollbarEnabled)
    {
      bool1 = false;
      localView = findFirstVisibleItemClosestToStart(bool1, true);
      if (!this.mSmoothScrollbarEnabled) {
        break label72;
      }
    }
    label72:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      return ScrollbarHelper.computeScrollExtent(paramState, localOrientationHelper, localView, findFirstVisibleItemClosestToEnd(bool1, true), this, this.mSmoothScrollbarEnabled);
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
    ensureOrientationHelper();
    OrientationHelper localOrientationHelper = this.mPrimaryOrientation;
    View localView;
    if (this.mSmoothScrollbarEnabled)
    {
      bool1 = false;
      localView = findFirstVisibleItemClosestToStart(bool1, true);
      if (!this.mSmoothScrollbarEnabled) {
        break label76;
      }
    }
    label76:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      return ScrollbarHelper.computeScrollOffset(paramState, localOrientationHelper, localView, findFirstVisibleItemClosestToEnd(bool1, true), this, this.mSmoothScrollbarEnabled, this.mShouldReverseLayout);
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
    ensureOrientationHelper();
    OrientationHelper localOrientationHelper = this.mPrimaryOrientation;
    View localView;
    if (this.mSmoothScrollbarEnabled)
    {
      bool1 = false;
      localView = findFirstVisibleItemClosestToStart(bool1, true);
      if (!this.mSmoothScrollbarEnabled) {
        break label72;
      }
    }
    label72:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      return ScrollbarHelper.computeScrollRange(paramState, localOrientationHelper, localView, findFirstVisibleItemClosestToEnd(bool1, true), this, this.mSmoothScrollbarEnabled);
      bool1 = true;
      break;
    }
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
  
  private void ensureOrientationHelper()
  {
    if (this.mPrimaryOrientation == null)
    {
      this.mPrimaryOrientation = OrientationHelper.createOrientationHelper(this, this.mOrientation);
      this.mSecondaryOrientation = OrientationHelper.createOrientationHelper(this, 1 - this.mOrientation);
      this.mLayoutState = new LayoutState();
    }
  }
  
  private int fill(RecyclerView.Recycler paramRecycler, LayoutState paramLayoutState, RecyclerView.State paramState)
  {
    this.mRemainingSpans.set(0, this.mSpanCount, true);
    int j;
    int k;
    if (paramLayoutState.mLayoutDirection == 1)
    {
      j = paramLayoutState.mEndLine + paramLayoutState.mAvailable;
      updateAllRemainingSpans(paramLayoutState.mLayoutDirection, j);
      if (!this.mShouldReverseLayout) {
        break label158;
      }
      k = this.mPrimaryOrientation.getEndAfterPadding();
      label58:
      i = 0;
      if ((paramLayoutState.hasMore(paramState)) && (!this.mRemainingSpans.isEmpty())) {
        break label170;
      }
      if (i == 0) {
        recycle(paramRecycler, this.mLayoutState);
      }
      if (this.mLayoutState.mLayoutDirection != -1) {
        break label802;
      }
      i = getMinStart(this.mPrimaryOrientation.getStartAfterPadding());
    }
    label158:
    label170:
    label214:
    label235:
    label246:
    label267:
    label299:
    label406:
    label432:
    label468:
    label510:
    label520:
    label532:
    label542:
    label554:
    label666:
    label742:
    label744:
    label767:
    label784:
    label802:
    for (int i = this.mPrimaryOrientation.getStartAfterPadding() - i;; i = getMaxEnd(this.mPrimaryOrientation.getEndAfterPadding()) - this.mPrimaryOrientation.getEndAfterPadding())
    {
      if (i <= 0) {
        break label826;
      }
      return Math.min(paramLayoutState.mAvailable, i);
      j = paramLayoutState.mStartLine - paramLayoutState.mAvailable;
      break;
      k = this.mPrimaryOrientation.getStartAfterPadding();
      break label58;
      View localView = paramLayoutState.next(paramRecycler);
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      int i3 = localLayoutParams.getViewLayoutPosition();
      i = this.mLazySpanLookup.getSpan(i3);
      int i1;
      Span localSpan;
      int i2;
      int m;
      int n;
      StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem localFullSpanItem;
      if (i == -1)
      {
        i1 = 1;
        if (i1 == 0) {
          break label520;
        }
        if (!localLayoutParams.mFullSpan) {
          break label510;
        }
        localSpan = this.mSpans[0];
        this.mLazySpanLookup.setSpan(i3, localSpan);
        localLayoutParams.mSpan = localSpan;
        if (paramLayoutState.mLayoutDirection != 1) {
          break label532;
        }
        addView(localView);
        measureChildWithDecorationsAndMargin(localView, localLayoutParams);
        if (paramLayoutState.mLayoutDirection != 1) {
          break label554;
        }
        if (!localLayoutParams.mFullSpan) {
          break label542;
        }
        i = getMaxEnd(k);
        i2 = i + this.mPrimaryOrientation.getDecoratedMeasurement(localView);
        m = i;
        n = i2;
        if (i1 != 0)
        {
          m = i;
          n = i2;
          if (localLayoutParams.mFullSpan)
          {
            localFullSpanItem = createFullSpanItemFromEnd(i);
            localFullSpanItem.mGapDir = -1;
            localFullSpanItem.mPosition = i3;
            this.mLazySpanLookup.addFullSpanItem(localFullSpanItem);
            n = i2;
            m = i;
          }
        }
        if ((localLayoutParams.mFullSpan) && (paramLayoutState.mItemDirection == -1))
        {
          if (i1 == 0) {
            break label666;
          }
          this.mLaidOutInvalidFullSpan = true;
        }
        attachViewToSpans(localView, localLayoutParams, paramLayoutState);
        if (!localLayoutParams.mFullSpan) {
          break label744;
        }
        i = this.mSecondaryOrientation.getStartAfterPadding();
        i1 = i + this.mSecondaryOrientation.getDecoratedMeasurement(localView);
        if (this.mOrientation != 1) {
          break label767;
        }
        layoutDecoratedWithMargins(localView, i, m, i1, n);
        if (!localLayoutParams.mFullSpan) {
          break label784;
        }
        updateAllRemainingSpans(this.mLayoutState.mLayoutDirection, j);
      }
      for (;;)
      {
        recycle(paramRecycler, this.mLayoutState);
        i = 1;
        break;
        i1 = 0;
        break label214;
        localSpan = getNextSpan(paramLayoutState);
        break label235;
        localSpan = this.mSpans[i];
        break label246;
        addView(localView, 0);
        break label267;
        i = localSpan.getEndLine(k);
        break label299;
        if (localLayoutParams.mFullSpan) {}
        for (i = getMinStart(k);; i = localSpan.getStartLine(k))
        {
          i2 = i - this.mPrimaryOrientation.getDecoratedMeasurement(localView);
          m = i2;
          n = i;
          if (i1 == 0) {
            break;
          }
          m = i2;
          n = i;
          if (!localLayoutParams.mFullSpan) {
            break;
          }
          localFullSpanItem = createFullSpanItemFromStart(i);
          localFullSpanItem.mGapDir = 1;
          localFullSpanItem.mPosition = i3;
          this.mLazySpanLookup.addFullSpanItem(localFullSpanItem);
          m = i2;
          n = i;
          break;
        }
        if (paramLayoutState.mLayoutDirection == 1) {
          if (areAllEndsEqual()) {
            i = 0;
          }
        }
        for (;;)
        {
          if (i == 0) {
            break label742;
          }
          localFullSpanItem = this.mLazySpanLookup.getFullSpanItem(i3);
          if (localFullSpanItem != null) {
            localFullSpanItem.mHasUnwantedGapAfter = true;
          }
          this.mLaidOutInvalidFullSpan = true;
          break;
          i = 1;
          continue;
          if (areAllStartsEqual()) {
            i = 0;
          } else {
            i = 1;
          }
        }
        break label406;
        i = localSpan.mIndex * this.mSizePerSpan + this.mSecondaryOrientation.getStartAfterPadding();
        break label432;
        layoutDecoratedWithMargins(localView, m, i, n, i1);
        break label468;
        updateRemainingSpans(localSpan, this.mLayoutState.mLayoutDirection, j);
      }
    }
    label826:
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
    int i = getMaxEnd(this.mPrimaryOrientation.getEndAfterPadding());
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
    int i = getMinStart(this.mPrimaryOrientation.getStartAfterPadding()) - this.mPrimaryOrientation.getStartAfterPadding();
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
  
  private int getSpecForDimension(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0) {
      return paramInt2;
    }
    return View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824);
  }
  
  private void handleUpdate(int paramInt1, int paramInt2, int paramInt3)
  {
    int k;
    int j;
    int i;
    if (this.mShouldReverseLayout)
    {
      k = getLastChildPosition();
      if (paramInt3 != 3) {
        break label100;
      }
      if (paramInt1 >= paramInt2) {
        break label89;
      }
      j = paramInt2 + 1;
      i = paramInt1;
      label31:
      this.mLazySpanLookup.invalidateAfter(i);
      switch (paramInt3)
      {
      }
    }
    for (;;)
    {
      if (j > k) {
        break label156;
      }
      return;
      k = getFirstChildPosition();
      break;
      label89:
      j = paramInt1 + 1;
      i = paramInt2;
      break label31;
      label100:
      i = paramInt1;
      j = paramInt1 + paramInt2;
      break label31;
      this.mLazySpanLookup.offsetForAddition(paramInt1, paramInt2);
      continue;
      this.mLazySpanLookup.offsetForRemoval(paramInt1, paramInt2);
      continue;
      this.mLazySpanLookup.offsetForRemoval(paramInt1, 1);
      this.mLazySpanLookup.offsetForAddition(paramInt2, 1);
    }
    label156:
    if (this.mShouldReverseLayout) {}
    for (paramInt1 = getFirstChildPosition();; paramInt1 = getLastChildPosition())
    {
      if (i <= paramInt1) {
        requestLayout();
      }
      return;
    }
  }
  
  private void layoutDecoratedWithMargins(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    layoutDecorated(paramView, paramInt1 + localLayoutParams.leftMargin, paramInt2 + localLayoutParams.topMargin, paramInt3 - localLayoutParams.rightMargin, paramInt4 - localLayoutParams.bottomMargin);
  }
  
  private void measureChildWithDecorationsAndMargin(View paramView, int paramInt1, int paramInt2)
  {
    calculateItemDecorationsForChild(paramView, this.mTmpRect);
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    paramView.measure(updateSpecWithExtra(paramInt1, localLayoutParams.leftMargin + this.mTmpRect.left, localLayoutParams.rightMargin + this.mTmpRect.right), updateSpecWithExtra(paramInt2, localLayoutParams.topMargin + this.mTmpRect.top, localLayoutParams.bottomMargin + this.mTmpRect.bottom));
  }
  
  private void measureChildWithDecorationsAndMargin(View paramView, LayoutParams paramLayoutParams)
  {
    if (paramLayoutParams.mFullSpan)
    {
      if (this.mOrientation == 1)
      {
        measureChildWithDecorationsAndMargin(paramView, this.mFullSizeSpec, getSpecForDimension(paramLayoutParams.height, this.mHeightSpec));
        return;
      }
      measureChildWithDecorationsAndMargin(paramView, getSpecForDimension(paramLayoutParams.width, this.mWidthSpec), this.mFullSizeSpec);
      return;
    }
    if (this.mOrientation == 1)
    {
      measureChildWithDecorationsAndMargin(paramView, this.mWidthSpec, getSpecForDimension(paramLayoutParams.height, this.mHeightSpec));
      return;
    }
    measureChildWithDecorationsAndMargin(paramView, getSpecForDimension(paramLayoutParams.width, this.mWidthSpec), this.mHeightSpec);
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
      if (this.mPrimaryOrientation.getDecoratedStart(localView) >= paramInt)
      {
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if (localLayoutParams.mFullSpan)
        {
          int j = 0;
          while (j < this.mSpanCount)
          {
            if (Span.-get0(this.mSpans[j]).size() == 1) {
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
        if (Span.-get0(localLayoutParams.mSpan).size() == 1) {
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
      if (this.mPrimaryOrientation.getDecoratedEnd(localView) <= paramInt)
      {
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if (localLayoutParams.mFullSpan)
        {
          int i = 0;
          while (i < this.mSpanCount)
          {
            if (Span.-get0(this.mSpans[i]).size() == 1) {
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
        if (Span.-get0(localLayoutParams.mSpan).size() == 1) {
          return;
        }
        localLayoutParams.mSpan.popStart();
        removeAndRecycleView(localView, paramRecycler);
      }
      else {}
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
      if (Span.-get0(this.mSpans[i]).isEmpty()) {}
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
    boolean bool1 = false;
    this.mLayoutState.mAvailable = 0;
    this.mLayoutState.mCurrentPosition = paramInt;
    int k = 0;
    int m = 0;
    int i = m;
    int j = k;
    if (isSmoothScrolling())
    {
      int n = paramState.getTargetScrollPosition();
      i = m;
      j = k;
      if (n != -1)
      {
        boolean bool2 = this.mShouldReverseLayout;
        if (n < paramInt) {
          bool1 = true;
        }
        if (bool2 != bool1) {
          break label133;
        }
        i = this.mPrimaryOrientation.getTotalSpace();
        j = k;
      }
    }
    while (getClipToPadding())
    {
      this.mLayoutState.mStartLine = (this.mPrimaryOrientation.getStartAfterPadding() - j);
      this.mLayoutState.mEndLine = (this.mPrimaryOrientation.getEndAfterPadding() + i);
      return;
      label133:
      j = this.mPrimaryOrientation.getTotalSpace();
      i = m;
    }
    this.mLayoutState.mEndLine = (this.mPrimaryOrientation.getEnd() + i);
    this.mLayoutState.mStartLine = (-j);
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
      return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1) - paramInt2 - paramInt3, i);
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
  
  View findFirstVisibleItemClosestToEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    ensureOrientationHelper();
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
          break label98;
        }
        localObject2 = localObject1;
      }
      for (;;)
      {
        i -= 1;
        localObject1 = localObject2;
        break;
        label98:
        if ((n <= k) || (!paramBoolean1)) {
          break label133;
        }
        localObject2 = localObject1;
        if (paramBoolean2)
        {
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = localView;
          }
        }
      }
      label133:
      return localView;
    }
    return (View)localObject1;
  }
  
  View findFirstVisibleItemClosestToStart(boolean paramBoolean1, boolean paramBoolean2)
  {
    ensureOrientationHelper();
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
          break label97;
        }
        localObject2 = localObject1;
      }
      for (;;)
      {
        i += 1;
        localObject1 = localObject2;
        break;
        label97:
        if ((n >= j) || (!paramBoolean1)) {
          break label132;
        }
        localObject2 = localObject1;
        if (paramBoolean2)
        {
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = localView;
          }
        }
      }
      label132:
      return localView;
    }
    return (View)localObject1;
  }
  
  int findFirstVisibleItemPositionInt()
  {
    if (this.mShouldReverseLayout) {}
    for (View localView = findFirstVisibleItemClosestToEnd(true, true); localView == null; localView = findFirstVisibleItemClosestToStart(true, true)) {
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
  
  public RecyclerView.LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-2, -2);
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
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    int i;
    int j;
    if (getChildCount() > 0)
    {
      paramAccessibilityEvent = AccessibilityEvent.obtain(paramAccessibilityEvent);
      View localView1 = findFirstVisibleItemClosestToStart(false, true);
      View localView2 = findFirstVisibleItemClosestToEnd(false, true);
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
  
  public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, View paramView, AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    paramRecycler = paramView.getLayoutParams();
    if (!(paramRecycler instanceof LayoutParams))
    {
      super.onInitializeAccessibilityNodeInfoForItem(paramView, paramAccessibilityNodeInfo);
      return;
    }
    paramRecycler = (LayoutParams)paramRecycler;
    if (this.mOrientation == 0)
    {
      j = paramRecycler.getSpanIndex();
      if (paramRecycler.mFullSpan) {}
      for (i = this.mSpanCount;; i = 1)
      {
        paramAccessibilityNodeInfo.setCollectionItemInfo(AccessibilityNodeInfo.CollectionItemInfo.obtain(j, i, -1, -1, paramRecycler.mFullSpan, false));
        return;
      }
    }
    int j = paramRecycler.getSpanIndex();
    if (paramRecycler.mFullSpan) {}
    for (int i = this.mSpanCount;; i = 1)
    {
      paramAccessibilityNodeInfo.setCollectionItemInfo(AccessibilityNodeInfo.CollectionItemInfo.obtain(-1, -1, j, i, paramRecycler.mFullSpan, false));
      return;
    }
  }
  
  public void onItemsAdded(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    handleUpdate(paramInt1, paramInt2, 0);
  }
  
  public void onItemsChanged(RecyclerView paramRecyclerView)
  {
    this.mLazySpanLookup.clear();
    requestLayout();
  }
  
  public void onItemsMoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, int paramInt3)
  {
    handleUpdate(paramInt1, paramInt2, 3);
  }
  
  public void onItemsRemoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    handleUpdate(paramInt1, paramInt2, 1);
  }
  
  public void onItemsUpdated(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, Object paramObject)
  {
    handleUpdate(paramInt1, paramInt2, 2);
  }
  
  public void onLayoutChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    int j = 1;
    ensureOrientationHelper();
    AnchorInfo localAnchorInfo = this.mAnchorInfo;
    localAnchorInfo.reset();
    if (((this.mPendingSavedState != null) || (this.mPendingScrollPosition != -1)) && (paramState.getItemCount() == 0))
    {
      removeAndRecycleAllViews(paramRecycler);
      return;
    }
    if (this.mPendingSavedState != null) {
      applyPendingSavedState(localAnchorInfo);
    }
    for (;;)
    {
      updateAnchorInfoForLayout(paramState, localAnchorInfo);
      if ((this.mPendingSavedState == null) && ((localAnchorInfo.mLayoutFromEnd != this.mLastLayoutFromEnd) || (isLayoutRTL() != this.mLastLayoutRTL)))
      {
        this.mLazySpanLookup.clear();
        localAnchorInfo.mInvalidateOffsets = true;
      }
      if ((getChildCount() <= 0) || ((this.mPendingSavedState != null) && (this.mPendingSavedState.mSpanOffsetsSize >= 1))) {
        break label243;
      }
      if (!localAnchorInfo.mInvalidateOffsets) {
        break;
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
      resolveShouldLayoutReverse();
      localAnchorInfo.mLayoutFromEnd = this.mShouldReverseLayout;
    }
    int i = 0;
    while (i < this.mSpanCount)
    {
      this.mSpans[i].cacheReferenceLineAndClear(this.mShouldReverseLayout, localAnchorInfo.mOffset);
      i += 1;
    }
    label243:
    detachAndScrapAttachedViews(paramRecycler);
    this.mLaidOutInvalidFullSpan = false;
    updateMeasureSpecs();
    updateLayoutState(localAnchorInfo.mPosition, paramState);
    if (localAnchorInfo.mLayoutFromEnd)
    {
      setLayoutStateDirection(-1);
      fill(paramRecycler, this.mLayoutState, paramState);
      setLayoutStateDirection(1);
      this.mLayoutState.mCurrentPosition = (localAnchorInfo.mPosition + this.mLayoutState.mItemDirection);
      fill(paramRecycler, this.mLayoutState, paramState);
      if (getChildCount() > 0)
      {
        if (!this.mShouldReverseLayout) {
          break label506;
        }
        fixEndGap(paramRecycler, paramState, true);
        fixStartGap(paramRecycler, paramState, false);
      }
      label355:
      if (!paramState.isPreLayout())
      {
        if ((this.mGapStrategy == 0) || (getChildCount() <= 0)) {
          break label528;
        }
        i = j;
        if (!this.mLaidOutInvalidFullSpan)
        {
          if (hasGapsToFix() == null) {
            break label523;
          }
          i = j;
        }
      }
    }
    for (;;)
    {
      if (i != 0)
      {
        removeCallbacks(this.mCheckForGapsRunnable);
        postOnAnimation(this.mCheckForGapsRunnable);
      }
      this.mPendingScrollPosition = -1;
      this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
      this.mLastLayoutFromEnd = localAnchorInfo.mLayoutFromEnd;
      this.mLastLayoutRTL = isLayoutRTL();
      this.mPendingSavedState = null;
      return;
      setLayoutStateDirection(1);
      fill(paramRecycler, this.mLayoutState, paramState);
      setLayoutStateDirection(-1);
      this.mLayoutState.mCurrentPosition = (localAnchorInfo.mPosition + this.mLayoutState.mItemDirection);
      fill(paramRecycler, this.mLayoutState, paramState);
      break;
      label506:
      fixStartGap(paramRecycler, paramState, true);
      fixEndGap(paramRecycler, paramState, false);
      break label355;
      label523:
      i = 0;
      continue;
      label528:
      i = 0;
    }
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
    label130:
    int j;
    label167:
    int k;
    if ((this.mLazySpanLookup != null) && (this.mLazySpanLookup.mData != null))
    {
      localSavedState.mSpanLookup = this.mLazySpanLookup.mData;
      localSavedState.mSpanLookupSize = localSavedState.mSpanLookup.length;
      localSavedState.mFullSpanItems = this.mLazySpanLookup.mFullSpanItems;
      if (getChildCount() <= 0) {
        break label277;
      }
      ensureOrientationHelper();
      if (!this.mLastLayoutFromEnd) {
        break label236;
      }
      i = getLastChildPosition();
      localSavedState.mAnchorPosition = i;
      localSavedState.mVisibleAnchorPosition = findFirstVisibleItemPositionInt();
      localSavedState.mSpanOffsetsSize = this.mSpanCount;
      localSavedState.mSpanOffsets = new int[this.mSpanCount];
      j = 0;
      if (j >= this.mSpanCount) {
        break label295;
      }
      if (!this.mLastLayoutFromEnd) {
        break label244;
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
      break label167;
      localSavedState.mSpanLookupSize = 0;
      break;
      label236:
      i = getFirstChildPosition();
      break label130;
      label244:
      k = this.mSpans[j].getStartLine(Integer.MIN_VALUE);
      i = k;
      if (k != Integer.MIN_VALUE) {
        i = k - this.mPrimaryOrientation.getStartAfterPadding();
      }
    }
    label277:
    localSavedState.mAnchorPosition = -1;
    localSavedState.mVisibleAnchorPosition = -1;
    localSavedState.mSpanOffsetsSize = 0;
    label295:
    return localSavedState;
  }
  
  public void onScrollStateChanged(int paramInt)
  {
    if (paramInt == 0) {
      checkForGaps();
    }
  }
  
  int scrollBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    ensureOrientationHelper();
    int i;
    int j;
    if (paramInt > 0)
    {
      i = 1;
      j = getLastChildPosition();
      updateLayoutState(j, paramState);
      setLayoutStateDirection(i);
      this.mLayoutState.mCurrentPosition = (this.mLayoutState.mItemDirection + j);
      j = Math.abs(paramInt);
      this.mLayoutState.mAvailable = j;
      i = fill(paramRecycler, this.mLayoutState, paramState);
      if (j >= i) {
        break label112;
      }
    }
    for (;;)
    {
      this.mPrimaryOrientation.offsetChildren(-paramInt);
      this.mLastLayoutFromEnd = this.mShouldReverseLayout;
      return paramInt;
      i = -1;
      j = getFirstChildPosition();
      break;
      label112:
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
    assertNotInLayoutOrScroll(null);
    if (paramInt == this.mGapStrategy) {
      return;
    }
    if ((paramInt != 0) && (paramInt != 2)) {
      throw new IllegalArgumentException("invalid gap strategy. Must be GAP_HANDLING_NONE or GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS");
    }
    this.mGapStrategy = paramInt;
    requestLayout();
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
    if ((this.mPrimaryOrientation != null) && (this.mSecondaryOrientation != null))
    {
      OrientationHelper localOrientationHelper = this.mPrimaryOrientation;
      this.mPrimaryOrientation = this.mSecondaryOrientation;
      this.mSecondaryOrientation = localOrientationHelper;
    }
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
        this.mSpans[paramInt] = new Span(paramInt, null);
        paramInt += 1;
      }
      requestLayout();
    }
  }
  
  public void smoothScrollToPosition(RecyclerView paramRecyclerView, RecyclerView.State paramState, int paramInt)
  {
    paramRecyclerView = new LinearSmoothScroller(paramRecyclerView.getContext())
    {
      public PointF computeScrollVectorForPosition(int paramAnonymousInt)
      {
        paramAnonymousInt = StaggeredGridLayoutManager.-wrap1(StaggeredGridLayoutManager.this, paramAnonymousInt);
        if (paramAnonymousInt == 0) {
          return null;
        }
        if (StaggeredGridLayoutManager.-get0(StaggeredGridLayoutManager.this) == 0) {
          return new PointF(paramAnonymousInt, 0.0F);
        }
        return new PointF(0.0F, paramAnonymousInt);
      }
    };
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
  
  void updateMeasureSpecs()
  {
    this.mSizePerSpan = (this.mSecondaryOrientation.getTotalSpace() / this.mSpanCount);
    this.mFullSizeSpec = View.MeasureSpec.makeMeasureSpec(this.mSecondaryOrientation.getTotalSpace(), 1073741824);
    if (this.mOrientation == 1)
    {
      this.mWidthSpec = View.MeasureSpec.makeMeasureSpec(this.mSizePerSpan, 1073741824);
      this.mHeightSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
      return;
    }
    this.mHeightSpec = View.MeasureSpec.makeMeasureSpec(this.mSizePerSpan, 1073741824);
    this.mWidthSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
  }
  
  private class AnchorInfo
  {
    boolean mInvalidateOffsets;
    boolean mLayoutFromEnd;
    int mOffset;
    int mPosition;
    
    private AnchorInfo() {}
    
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
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      super();
    }
    
    public LayoutParams(RecyclerView.LayoutParams paramLayoutParams)
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
      
      public void invalidateSpanGaps()
      {
        this.mGapPerSpan = null;
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
  
  static class SavedState
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
    private ArrayList<View> mViews = new ArrayList();
    
    private Span(int paramInt)
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
      if (StaggeredGridLayoutManager.-get1(StaggeredGridLayoutManager.this)) {
        return findOneVisibleChild(this.mViews.size() - 1, -1, true);
      }
      return findOneVisibleChild(0, this.mViews.size(), true);
    }
    
    public int findFirstVisibleItemPosition()
    {
      if (StaggeredGridLayoutManager.-get1(StaggeredGridLayoutManager.this)) {
        return findOneVisibleChild(this.mViews.size() - 1, -1, false);
      }
      return findOneVisibleChild(0, this.mViews.size(), false);
    }
    
    public int findLastCompletelyVisibleItemPosition()
    {
      if (StaggeredGridLayoutManager.-get1(StaggeredGridLayoutManager.this)) {
        return findOneVisibleChild(0, this.mViews.size(), true);
      }
      return findOneVisibleChild(this.mViews.size() - 1, -1, true);
    }
    
    public int findLastVisibleItemPosition()
    {
      if (StaggeredGridLayoutManager.-get1(StaggeredGridLayoutManager.this)) {
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
    
    StaggeredGridLayoutManager.LayoutParams getLayoutParams(View paramView)
    {
      return (StaggeredGridLayoutManager.LayoutParams)paramView.getLayoutParams();
    }
    
    int getNormalizedOffset(int paramInt1, int paramInt2, int paramInt3)
    {
      if (this.mViews.size() == 0) {
        return 0;
      }
      if (paramInt1 < 0)
      {
        paramInt3 = getEndLine() - paramInt3;
        if (paramInt3 <= 0) {
          return 0;
        }
        paramInt2 = paramInt1;
        if (-paramInt1 > paramInt3) {
          paramInt2 = -paramInt3;
        }
        return paramInt2;
      }
      paramInt2 -= getStartLine();
      if (paramInt2 <= 0) {
        return 0;
      }
      if (paramInt2 < paramInt1) {
        return paramInt2;
      }
      return paramInt1;
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
    
    boolean isEmpty(int paramInt1, int paramInt2)
    {
      int j = this.mViews.size();
      int i = 0;
      while (i < j)
      {
        View localView = (View)this.mViews.get(i);
        if ((StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedStart(localView) < paramInt2) && (StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedEnd(localView) > paramInt1)) {
          return false;
        }
        i += 1;
      }
      return true;
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


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\recyclerview\StaggeredGridLayoutManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */