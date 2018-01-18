package android.support.v7.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.CollectionItemInfoCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import java.util.Arrays;

public class GridLayoutManager
  extends LinearLayoutManager
{
  private static final boolean DEBUG = false;
  public static final int DEFAULT_SPAN_COUNT = -1;
  private static final String TAG = "GridLayoutManager";
  int[] mCachedBorders;
  final Rect mDecorInsets = new Rect();
  boolean mPendingSpanCountChange = false;
  final SparseIntArray mPreLayoutSpanIndexCache = new SparseIntArray();
  final SparseIntArray mPreLayoutSpanSizeCache = new SparseIntArray();
  View[] mSet;
  int mSpanCount = -1;
  SpanSizeLookup mSpanSizeLookup = new DefaultSpanSizeLookup();
  
  public GridLayoutManager(Context paramContext, int paramInt)
  {
    super(paramContext);
    setSpanCount(paramInt);
  }
  
  public GridLayoutManager(Context paramContext, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    super(paramContext, paramInt2, paramBoolean);
    setSpanCount(paramInt1);
  }
  
  public GridLayoutManager(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setSpanCount(getProperties(paramContext, paramAttributeSet, paramInt1, paramInt2).spanCount);
  }
  
  private void assignSpans(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int j;
    int i;
    if (paramBoolean)
    {
      j = 0;
      i = paramInt1;
      paramInt2 = 1;
      paramInt1 = j;
    }
    for (;;)
    {
      j = 0;
      while (paramInt1 != i)
      {
        View localView = this.mSet[paramInt1];
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        localLayoutParams.mSpanSize = getSpanSize(paramRecycler, paramState, getPosition(localView));
        localLayoutParams.mSpanIndex = j;
        j += localLayoutParams.mSpanSize;
        paramInt1 += paramInt2;
      }
      paramInt1 -= 1;
      i = -1;
      paramInt2 = -1;
    }
  }
  
  private void cachePreLayoutSpanMapping()
  {
    int j = getChildCount();
    int i = 0;
    while (i < j)
    {
      LayoutParams localLayoutParams = (LayoutParams)getChildAt(i).getLayoutParams();
      int k = localLayoutParams.getViewLayoutPosition();
      this.mPreLayoutSpanSizeCache.put(k, localLayoutParams.getSpanSize());
      this.mPreLayoutSpanIndexCache.put(k, localLayoutParams.getSpanIndex());
      i += 1;
    }
  }
  
  private void calculateItemBorders(int paramInt)
  {
    this.mCachedBorders = calculateItemBorders(this.mCachedBorders, this.mSpanCount, paramInt);
  }
  
  static int[] calculateItemBorders(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    if ((paramArrayOfInt == null) || (paramArrayOfInt.length != paramInt1 + 1)) {}
    int[] arrayOfInt;
    for (;;)
    {
      arrayOfInt = new int[paramInt1 + 1];
      do
      {
        arrayOfInt[0] = 0;
        int n = paramInt2 / paramInt1;
        int i2 = paramInt2 % paramInt1;
        int j = 0;
        paramInt2 = 0;
        int i = 1;
        while (i <= paramInt1)
        {
          int k = n;
          int i1 = paramInt2 + i2;
          paramInt2 = i1;
          int m = k;
          if (i1 > 0)
          {
            paramInt2 = i1;
            m = k;
            if (paramInt1 - i1 < i2)
            {
              m = n + 1;
              paramInt2 = i1 - paramInt1;
            }
          }
          j += m;
          arrayOfInt[i] = j;
          i += 1;
        }
        arrayOfInt = paramArrayOfInt;
      } while (paramArrayOfInt[(paramArrayOfInt.length - 1)] == paramInt2);
    }
    return arrayOfInt;
  }
  
  private void clearPreLayoutSpanMappingCache()
  {
    this.mPreLayoutSpanSizeCache.clear();
    this.mPreLayoutSpanIndexCache.clear();
  }
  
  private void ensureAnchorIsInCorrectSpan(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, LinearLayoutManager.AnchorInfo paramAnchorInfo, int paramInt)
  {
    if (paramInt == 1) {}
    for (int i = 1;; i = 0)
    {
      paramInt = getSpanIndex(paramRecycler, paramState, paramAnchorInfo.mPosition);
      if (i == 0) {
        break;
      }
      while ((paramInt > 0) && (paramAnchorInfo.mPosition > 0))
      {
        paramAnchorInfo.mPosition -= 1;
        paramInt = getSpanIndex(paramRecycler, paramState, paramAnchorInfo.mPosition);
      }
    }
    int k = paramState.getItemCount();
    i = paramAnchorInfo.mPosition;
    while (i < k - 1)
    {
      int j = getSpanIndex(paramRecycler, paramState, i + 1);
      if (j <= paramInt) {
        break;
      }
      i += 1;
      paramInt = j;
    }
    paramAnchorInfo.mPosition = i;
  }
  
  private void ensureViewSet()
  {
    if ((this.mSet == null) || (this.mSet.length != this.mSpanCount)) {
      this.mSet = new View[this.mSpanCount];
    }
  }
  
  private int getSpanGroupIndex(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt)
  {
    if (!paramState.isPreLayout()) {
      return this.mSpanSizeLookup.getSpanGroupIndex(paramInt, this.mSpanCount);
    }
    int i = paramRecycler.convertPreLayoutPositionToPostLayout(paramInt);
    if (i == -1)
    {
      Log.w("GridLayoutManager", "Cannot find span size for pre layout position. " + paramInt);
      return 0;
    }
    return this.mSpanSizeLookup.getSpanGroupIndex(i, this.mSpanCount);
  }
  
  private int getSpanIndex(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt)
  {
    if (!paramState.isPreLayout()) {
      return this.mSpanSizeLookup.getCachedSpanIndex(paramInt, this.mSpanCount);
    }
    int i = this.mPreLayoutSpanIndexCache.get(paramInt, -1);
    if (i != -1) {
      return i;
    }
    i = paramRecycler.convertPreLayoutPositionToPostLayout(paramInt);
    if (i == -1)
    {
      Log.w("GridLayoutManager", "Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:" + paramInt);
      return 0;
    }
    return this.mSpanSizeLookup.getCachedSpanIndex(i, this.mSpanCount);
  }
  
  private int getSpanSize(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt)
  {
    if (!paramState.isPreLayout()) {
      return this.mSpanSizeLookup.getSpanSize(paramInt);
    }
    int i = this.mPreLayoutSpanSizeCache.get(paramInt, -1);
    if (i != -1) {
      return i;
    }
    i = paramRecycler.convertPreLayoutPositionToPostLayout(paramInt);
    if (i == -1)
    {
      Log.w("GridLayoutManager", "Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:" + paramInt);
      return 1;
    }
    return this.mSpanSizeLookup.getSpanSize(i);
  }
  
  private void guessMeasurement(float paramFloat, int paramInt)
  {
    calculateItemBorders(Math.max(Math.round(this.mSpanCount * paramFloat), paramInt));
  }
  
  private void measureChild(View paramView, int paramInt, boolean paramBoolean)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    Rect localRect = localLayoutParams.mDecorInsets;
    int j = localRect.top + localRect.bottom + localLayoutParams.topMargin + localLayoutParams.bottomMargin;
    int i = localRect.left + localRect.right + localLayoutParams.leftMargin + localLayoutParams.rightMargin;
    int k = getSpaceForSpanRange(localLayoutParams.mSpanIndex, localLayoutParams.mSpanSize);
    if (this.mOrientation == 1)
    {
      i = getChildMeasureSpec(k, paramInt, i, localLayoutParams.width, false);
      paramInt = getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), getHeightMode(), j, localLayoutParams.height, true);
    }
    for (;;)
    {
      measureChildWithDecorationsAndMargin(paramView, i, paramInt, paramBoolean);
      return;
      paramInt = getChildMeasureSpec(k, paramInt, j, localLayoutParams.height, false);
      i = getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), getWidthMode(), i, localLayoutParams.width, true);
    }
  }
  
  private void measureChildWithDecorationsAndMargin(View paramView, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
    if (paramBoolean) {}
    for (paramBoolean = shouldReMeasureChild(paramView, paramInt1, paramInt2, localLayoutParams);; paramBoolean = shouldMeasureChild(paramView, paramInt1, paramInt2, localLayoutParams))
    {
      if (paramBoolean) {
        paramView.measure(paramInt1, paramInt2);
      }
      return;
    }
  }
  
  private void updateMeasurements()
  {
    if (getOrientation() == 1) {}
    for (int i = getWidth() - getPaddingRight() - getPaddingLeft();; i = getHeight() - getPaddingBottom() - getPaddingTop())
    {
      calculateItemBorders(i);
      return;
    }
  }
  
  public boolean checkLayoutParams(RecyclerView.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  View findReferenceChild(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt1, int paramInt2, int paramInt3)
  {
    ensureLayoutState();
    Object localObject2 = null;
    Object localObject1 = null;
    int j = this.mOrientationHelper.getStartAfterPadding();
    int k = this.mOrientationHelper.getEndAfterPadding();
    int i;
    View localView;
    Object localObject3;
    Object localObject4;
    if (paramInt2 > paramInt1)
    {
      i = 1;
      if (paramInt1 == paramInt2) {
        break label220;
      }
      localView = getChildAt(paramInt1);
      int m = getPosition(localView);
      localObject3 = localObject2;
      localObject4 = localObject1;
      if (m >= 0)
      {
        localObject3 = localObject2;
        localObject4 = localObject1;
        if (m < paramInt3)
        {
          if (getSpanIndex(paramRecycler, paramState, m) == 0) {
            break label127;
          }
          localObject4 = localObject1;
          localObject3 = localObject2;
        }
      }
    }
    for (;;)
    {
      paramInt1 += i;
      localObject2 = localObject3;
      localObject1 = localObject4;
      break;
      i = -1;
      break;
      label127:
      if (((RecyclerView.LayoutParams)localView.getLayoutParams()).isItemRemoved())
      {
        localObject3 = localObject2;
        localObject4 = localObject1;
        if (localObject2 == null)
        {
          localObject3 = localView;
          localObject4 = localObject1;
        }
      }
      else
      {
        if ((this.mOrientationHelper.getDecoratedStart(localView) < k) && (this.mOrientationHelper.getDecoratedEnd(localView) >= j)) {
          break label217;
        }
        localObject3 = localObject2;
        localObject4 = localObject1;
        if (localObject1 == null)
        {
          localObject3 = localObject2;
          localObject4 = localView;
        }
      }
    }
    label217:
    return localView;
    label220:
    if (localObject1 != null) {
      return (View)localObject1;
    }
    return (View)localObject2;
  }
  
  int gatherPrefetchIndicesForLayoutState(RecyclerView.State paramState, LinearLayoutManager.LayoutState paramLayoutState, int[] paramArrayOfInt)
  {
    int j = this.mSpanCount;
    int i = 0;
    while ((i < this.mSpanCount) && (paramLayoutState.hasMore(paramState)) && (j > 0))
    {
      int k = paramLayoutState.mCurrentPosition;
      paramArrayOfInt[i] = k;
      j -= this.mSpanSizeLookup.getSpanSize(k);
      paramLayoutState.mCurrentPosition += paramLayoutState.mItemDirection;
      i += 1;
    }
    return i;
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
    if (paramState.getItemCount() < 1) {
      return 0;
    }
    return getSpanGroupIndex(paramRecycler, paramState, paramState.getItemCount() - 1) + 1;
  }
  
  int getItemPrefetchCount()
  {
    return this.mSpanCount;
  }
  
  public int getRowCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (this.mOrientation == 0) {
      return this.mSpanCount;
    }
    if (paramState.getItemCount() < 1) {
      return 0;
    }
    return getSpanGroupIndex(paramRecycler, paramState, paramState.getItemCount() - 1) + 1;
  }
  
  int getSpaceForSpanRange(int paramInt1, int paramInt2)
  {
    if ((this.mOrientation == 1) && (isLayoutRTL())) {
      return this.mCachedBorders[(this.mSpanCount - paramInt1)] - this.mCachedBorders[(this.mSpanCount - paramInt1 - paramInt2)];
    }
    return this.mCachedBorders[(paramInt1 + paramInt2)] - this.mCachedBorders[paramInt1];
  }
  
  public int getSpanCount()
  {
    return this.mSpanCount;
  }
  
  public SpanSizeLookup getSpanSizeLookup()
  {
    return this.mSpanSizeLookup;
  }
  
  void layoutChunk(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, LinearLayoutManager.LayoutState paramLayoutState, LinearLayoutManager.LayoutChunkResult paramLayoutChunkResult)
  {
    int i3 = this.mOrientationHelper.getModeInOther();
    label38:
    boolean bool;
    label58:
    int i2;
    int n;
    if (i3 != 1073741824)
    {
      j = 1;
      if (getChildCount() <= 0) {
        break label226;
      }
      m = this.mCachedBorders[this.mSpanCount];
      if (j != 0) {
        updateMeasurements();
      }
      if (paramLayoutState.mItemDirection != 1) {
        break label232;
      }
      bool = true;
      i1 = 0;
      i2 = 0;
      i = this.mSpanCount;
      n = i1;
      k = i2;
      if (!bool)
      {
        i = getSpanIndex(paramRecycler, paramState, paramLayoutState.mCurrentPosition) + getSpanSize(paramRecycler, paramState, paramLayoutState.mCurrentPosition);
        k = i2;
        n = i1;
      }
    }
    label226:
    label232:
    label262:
    Object localObject;
    for (;;)
    {
      if ((n < this.mSpanCount) && (paramLayoutState.hasMore(paramState)) && (i > 0))
      {
        i2 = paramLayoutState.mCurrentPosition;
        i1 = getSpanSize(paramRecycler, paramState, i2);
        if (i1 > this.mSpanCount)
        {
          throw new IllegalArgumentException("Item at position " + i2 + " requires " + i1 + " spans but GridLayoutManager has only " + this.mSpanCount + " spans.");
          j = 0;
          break;
          m = 0;
          break label38;
          bool = false;
          break label58;
        }
        i -= i1;
        if (i >= 0) {
          break label262;
        }
      }
      do
      {
        if (n != 0) {
          break;
        }
        paramLayoutChunkResult.mFinished = true;
        return;
        localObject = paramLayoutState.next(paramRecycler);
      } while (localObject == null);
      k += i1;
      this.mSet[n] = localObject;
      n += 1;
    }
    int i = 0;
    float f1 = 0.0F;
    assignSpans(paramRecycler, paramState, n, k, bool);
    int k = 0;
    if (k < n)
    {
      paramRecycler = this.mSet[k];
      if (paramLayoutState.mScrapList == null) {
        if (bool) {
          addView(paramRecycler);
        }
      }
      for (;;)
      {
        calculateItemDecorationsForChild(paramRecycler, this.mDecorInsets);
        measureChild(paramRecycler, i3, false);
        i2 = this.mOrientationHelper.getDecoratedMeasurement(paramRecycler);
        i1 = i;
        if (i2 > i) {
          i1 = i2;
        }
        paramState = (LayoutParams)paramRecycler.getLayoutParams();
        float f3 = this.mOrientationHelper.getDecoratedMeasurementInOther(paramRecycler) * 1.0F / paramState.mSpanSize;
        float f2 = f1;
        if (f3 > f1) {
          f2 = f3;
        }
        k += 1;
        i = i1;
        f1 = f2;
        break;
        addView(paramRecycler, 0);
        continue;
        if (bool) {
          addDisappearingView(paramRecycler);
        } else {
          addDisappearingView(paramRecycler, 0);
        }
      }
    }
    k = i;
    if (j != 0)
    {
      guessMeasurement(f1, m);
      i = 0;
      j = 0;
      for (;;)
      {
        k = i;
        if (j >= n) {
          break;
        }
        paramRecycler = this.mSet[j];
        measureChild(paramRecycler, 1073741824, true);
        m = this.mOrientationHelper.getDecoratedMeasurement(paramRecycler);
        k = i;
        if (m > i) {
          k = m;
        }
        j += 1;
        i = k;
      }
    }
    i = 0;
    if (i < n)
    {
      paramRecycler = this.mSet[i];
      if (this.mOrientationHelper.getDecoratedMeasurement(paramRecycler) != k)
      {
        paramState = (LayoutParams)paramRecycler.getLayoutParams();
        localObject = paramState.mDecorInsets;
        j = ((Rect)localObject).top + ((Rect)localObject).bottom + paramState.topMargin + paramState.bottomMargin;
        m = ((Rect)localObject).left + ((Rect)localObject).right + paramState.leftMargin + paramState.rightMargin;
        i1 = getSpaceForSpanRange(paramState.mSpanIndex, paramState.mSpanSize);
        if (this.mOrientation != 1) {
          break label736;
        }
        m = getChildMeasureSpec(i1, 1073741824, m, paramState.width, false);
      }
      for (j = View.MeasureSpec.makeMeasureSpec(k - j, 1073741824);; j = getChildMeasureSpec(i1, 1073741824, j, paramState.height, false))
      {
        measureChildWithDecorationsAndMargin(paramRecycler, m, j, true);
        i += 1;
        break;
        label736:
        m = View.MeasureSpec.makeMeasureSpec(k - m, 1073741824);
      }
    }
    paramLayoutChunkResult.mConsumed = k;
    i = 0;
    int i1 = 0;
    int j = 0;
    int m = 0;
    if (this.mOrientation == 1) {
      if (paramLayoutState.mLayoutDirection == -1)
      {
        m = paramLayoutState.mOffset;
        j = m - k;
        k = i1;
        i1 = 0;
        i2 = k;
        k = j;
        label832:
        if (i1 >= n) {
          break label1097;
        }
        paramRecycler = this.mSet[i1];
        paramState = (LayoutParams)paramRecycler.getLayoutParams();
        if (this.mOrientation != 1) {
          break label1061;
        }
        if (!isLayoutRTL()) {
          break label1029;
        }
        j = getPaddingLeft() + this.mCachedBorders[(this.mSpanCount - paramState.mSpanIndex)];
        i = j - this.mOrientationHelper.getDecoratedMeasurementInOther(paramRecycler);
      }
    }
    for (;;)
    {
      layoutDecoratedWithMargins(paramRecycler, i, k, j, m);
      if ((paramState.isItemRemoved()) || (paramState.isItemChanged())) {
        paramLayoutChunkResult.mIgnoreConsumed = true;
      }
      paramLayoutChunkResult.mFocusable |= paramRecycler.isFocusable();
      i1 += 1;
      i2 = j;
      break label832;
      j = paramLayoutState.mOffset;
      m = j + k;
      k = i1;
      break;
      if (paramLayoutState.mLayoutDirection == -1)
      {
        i1 = paramLayoutState.mOffset;
        i = i1 - k;
        k = i1;
        break;
      }
      i = paramLayoutState.mOffset;
      k = i + k;
      break;
      label1029:
      i = getPaddingLeft() + this.mCachedBorders[paramState.mSpanIndex];
      j = i + this.mOrientationHelper.getDecoratedMeasurementInOther(paramRecycler);
      continue;
      label1061:
      k = getPaddingTop() + this.mCachedBorders[paramState.mSpanIndex];
      m = k + this.mOrientationHelper.getDecoratedMeasurementInOther(paramRecycler);
      j = i2;
    }
    label1097:
    Arrays.fill(this.mSet, null);
  }
  
  void onAnchorReady(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, LinearLayoutManager.AnchorInfo paramAnchorInfo, int paramInt)
  {
    super.onAnchorReady(paramRecycler, paramState, paramAnchorInfo, paramInt);
    updateMeasurements();
    if ((paramState.getItemCount() <= 0) || (paramState.isPreLayout())) {}
    for (;;)
    {
      ensureViewSet();
      return;
      ensureAnchorIsInCorrectSpan(paramRecycler, paramState, paramAnchorInfo, paramInt);
    }
  }
  
  public View onFocusSearchFailed(View paramView, int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    View localView = findContainingItemView(paramView);
    if (localView == null) {
      return null;
    }
    LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
    int i2 = localLayoutParams.mSpanIndex;
    int i3 = localLayoutParams.mSpanIndex + localLayoutParams.mSpanSize;
    if (super.onFocusSearchFailed(paramView, paramInt, paramRecycler, paramState) == null) {
      return null;
    }
    boolean bool1;
    label81:
    int i;
    int j;
    label98:
    label112:
    int m;
    int n;
    int k;
    if (convertFocusDirectionToLayoutDirection(paramInt) == 1)
    {
      bool1 = true;
      if (bool1 == this.mShouldReverseLayout) {
        break label151;
      }
      paramInt = 1;
      if (paramInt == 0) {
        break label156;
      }
      paramInt = getChildCount() - 1;
      i = -1;
      j = -1;
      if (this.mOrientation != 1) {
        break label170;
      }
      bool1 = isLayoutRTL();
      paramView = null;
      m = -1;
      n = 0;
      k = paramInt;
    }
    for (;;)
    {
      if (k != j)
      {
        paramRecycler = getChildAt(k);
        if (paramRecycler != localView) {}
      }
      else
      {
        return paramView;
        bool1 = false;
        break;
        label151:
        paramInt = 0;
        break label81;
        label156:
        paramInt = 0;
        i = 1;
        j = getChildCount();
        break label98;
        label170:
        bool1 = false;
        break label112;
      }
      if (paramRecycler.isFocusable()) {
        break label193;
      }
      k += i;
    }
    label193:
    paramState = (LayoutParams)paramRecycler.getLayoutParams();
    int i4 = paramState.mSpanIndex;
    int i5 = paramState.mSpanIndex + paramState.mSpanSize;
    if ((i4 == i2) && (i5 == i3)) {
      return paramRecycler;
    }
    int i1 = 0;
    if (paramView == null) {
      paramInt = 1;
    }
    for (;;)
    {
      label247:
      if (paramInt != 0)
      {
        paramView = paramRecycler;
        m = paramState.mSpanIndex;
        n = Math.min(i5, i3) - Math.max(i4, i2);
        break;
        paramInt = Math.max(i4, i2);
        int i6 = Math.min(i5, i3) - paramInt;
        if (i6 > n)
        {
          paramInt = 1;
        }
        else
        {
          paramInt = i1;
          if (i6 == n) {
            if (i4 <= m) {
              break label346;
            }
          }
        }
      }
    }
    label346:
    for (boolean bool2 = true;; bool2 = false)
    {
      paramInt = i1;
      if (bool1 != bool2) {
        break label247;
      }
      paramInt = 1;
      break label247;
      break;
    }
  }
  
  public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
  {
    ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
    if (!(localLayoutParams instanceof LayoutParams))
    {
      super.onInitializeAccessibilityNodeInfoForItem(paramView, paramAccessibilityNodeInfoCompat);
      return;
    }
    paramView = (LayoutParams)localLayoutParams;
    int i = getSpanGroupIndex(paramRecycler, paramState, paramView.getViewLayoutPosition());
    if (this.mOrientation == 0)
    {
      j = paramView.getSpanIndex();
      k = paramView.getSpanSize();
      if ((this.mSpanCount > 1) && (paramView.getSpanSize() == this.mSpanCount)) {}
      for (bool = true;; bool = false)
      {
        paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(j, k, i, 1, bool, false));
        return;
      }
    }
    int j = paramView.getSpanIndex();
    int k = paramView.getSpanSize();
    if ((this.mSpanCount > 1) && (paramView.getSpanSize() == this.mSpanCount)) {}
    for (boolean bool = true;; bool = false)
    {
      paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(i, 1, j, k, bool, false));
      return;
    }
  }
  
  public void onItemsAdded(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    this.mSpanSizeLookup.invalidateSpanIndexCache();
  }
  
  public void onItemsChanged(RecyclerView paramRecyclerView)
  {
    this.mSpanSizeLookup.invalidateSpanIndexCache();
  }
  
  public void onItemsMoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, int paramInt3)
  {
    this.mSpanSizeLookup.invalidateSpanIndexCache();
  }
  
  public void onItemsRemoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    this.mSpanSizeLookup.invalidateSpanIndexCache();
  }
  
  public void onItemsUpdated(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, Object paramObject)
  {
    this.mSpanSizeLookup.invalidateSpanIndexCache();
  }
  
  public void onLayoutChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (paramState.isPreLayout()) {
      cachePreLayoutSpanMapping();
    }
    super.onLayoutChildren(paramRecycler, paramState);
    clearPreLayoutSpanMappingCache();
  }
  
  public void onLayoutCompleted(RecyclerView.State paramState)
  {
    super.onLayoutCompleted(paramState);
    this.mPendingSpanCountChange = false;
  }
  
  public int scrollHorizontallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    updateMeasurements();
    ensureViewSet();
    return super.scrollHorizontallyBy(paramInt, paramRecycler, paramState);
  }
  
  public int scrollVerticallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    updateMeasurements();
    ensureViewSet();
    return super.scrollVerticallyBy(paramInt, paramRecycler, paramState);
  }
  
  public void setMeasuredDimension(Rect paramRect, int paramInt1, int paramInt2)
  {
    if (this.mCachedBorders == null) {
      super.setMeasuredDimension(paramRect, paramInt1, paramInt2);
    }
    int j = getPaddingLeft() + getPaddingRight();
    int k = getPaddingTop() + getPaddingBottom();
    int i;
    if (this.mOrientation == 1)
    {
      i = chooseSize(paramInt2, paramRect.height() + k, getMinimumHeight());
      paramInt2 = chooseSize(paramInt1, this.mCachedBorders[(this.mCachedBorders.length - 1)] + j, getMinimumWidth());
      paramInt1 = i;
    }
    for (;;)
    {
      setMeasuredDimension(paramInt2, paramInt1);
      return;
      i = chooseSize(paramInt1, paramRect.width() + j, getMinimumWidth());
      paramInt1 = chooseSize(paramInt2, this.mCachedBorders[(this.mCachedBorders.length - 1)] + k, getMinimumHeight());
      paramInt2 = i;
    }
  }
  
  public void setSpanCount(int paramInt)
  {
    if (paramInt == this.mSpanCount) {
      return;
    }
    this.mPendingSpanCountChange = true;
    if (paramInt < 1) {
      throw new IllegalArgumentException("Span count should be at least 1. Provided " + paramInt);
    }
    this.mSpanCount = paramInt;
    this.mSpanSizeLookup.invalidateSpanIndexCache();
    requestLayout();
  }
  
  public void setSpanSizeLookup(SpanSizeLookup paramSpanSizeLookup)
  {
    this.mSpanSizeLookup = paramSpanSizeLookup;
  }
  
  public void setStackFromEnd(boolean paramBoolean)
  {
    if (paramBoolean) {
      throw new UnsupportedOperationException("GridLayoutManager does not support stack from end. Consider using reverse layout");
    }
    super.setStackFromEnd(false);
  }
  
  public boolean supportsPredictiveItemAnimations()
  {
    return (this.mPendingSavedState == null) && (!this.mPendingSpanCountChange);
  }
  
  public static final class DefaultSpanSizeLookup
    extends GridLayoutManager.SpanSizeLookup
  {
    public int getSpanIndex(int paramInt1, int paramInt2)
    {
      return paramInt1 % paramInt2;
    }
    
    public int getSpanSize(int paramInt)
    {
      return 1;
    }
  }
  
  public static class LayoutParams
    extends RecyclerView.LayoutParams
  {
    public static final int INVALID_SPAN_ID = -1;
    int mSpanIndex = -1;
    int mSpanSize = 0;
    
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
    
    public int getSpanIndex()
    {
      return this.mSpanIndex;
    }
    
    public int getSpanSize()
    {
      return this.mSpanSize;
    }
  }
  
  public static abstract class SpanSizeLookup
  {
    private boolean mCacheSpanIndices = false;
    final SparseIntArray mSpanIndexCache = new SparseIntArray();
    
    int findReferenceIndexFromCache(int paramInt)
    {
      int i = 0;
      int j = this.mSpanIndexCache.size() - 1;
      while (i <= j)
      {
        int k = i + j >>> 1;
        if (this.mSpanIndexCache.keyAt(k) < paramInt) {
          i = k + 1;
        } else {
          j = k - 1;
        }
      }
      paramInt = i - 1;
      if ((paramInt >= 0) && (paramInt < this.mSpanIndexCache.size())) {
        return this.mSpanIndexCache.keyAt(paramInt);
      }
      return -1;
    }
    
    int getCachedSpanIndex(int paramInt1, int paramInt2)
    {
      if (!this.mCacheSpanIndices) {
        return getSpanIndex(paramInt1, paramInt2);
      }
      int i = this.mSpanIndexCache.get(paramInt1, -1);
      if (i != -1) {
        return i;
      }
      paramInt2 = getSpanIndex(paramInt1, paramInt2);
      this.mSpanIndexCache.put(paramInt1, paramInt2);
      return paramInt2;
    }
    
    public int getSpanGroupIndex(int paramInt1, int paramInt2)
    {
      int i = 0;
      int j = 0;
      int i2 = getSpanSize(paramInt1);
      int m = 0;
      if (m < paramInt1)
      {
        int n = getSpanSize(m);
        int i1 = i + n;
        int k;
        if (i1 == paramInt2)
        {
          i = 0;
          k = j + 1;
        }
        for (;;)
        {
          m += 1;
          j = k;
          break;
          k = j;
          i = i1;
          if (i1 > paramInt2)
          {
            i = n;
            k = j + 1;
          }
        }
      }
      paramInt1 = j;
      if (i + i2 > paramInt2) {
        paramInt1 = j + 1;
      }
      return paramInt1;
    }
    
    public int getSpanIndex(int paramInt1, int paramInt2)
    {
      int n = getSpanSize(paramInt1);
      if (n == paramInt2) {
        return 0;
      }
      int k = 0;
      int m = 0;
      int i = k;
      int j = m;
      if (this.mCacheSpanIndices)
      {
        i = k;
        j = m;
        if (this.mSpanIndexCache.size() > 0)
        {
          int i1 = findReferenceIndexFromCache(paramInt1);
          i = k;
          j = m;
          if (i1 >= 0)
          {
            i = this.mSpanIndexCache.get(i1) + getSpanSize(i1);
            j = i1 + 1;
          }
        }
      }
      if (j < paramInt1)
      {
        k = getSpanSize(j);
        m = i + k;
        if (m == paramInt2) {
          i = 0;
        }
        for (;;)
        {
          j += 1;
          break;
          i = m;
          if (m > paramInt2) {
            i = k;
          }
        }
      }
      if (i + n <= paramInt2) {
        return i;
      }
      return 0;
    }
    
    public abstract int getSpanSize(int paramInt);
    
    public void invalidateSpanIndexCache()
    {
      this.mSpanIndexCache.clear();
    }
    
    public boolean isSpanIndexCacheEnabled()
    {
      return this.mCacheSpanIndices;
    }
    
    public void setSpanIndexCacheEnabled(boolean paramBoolean)
    {
      this.mCacheSpanIndices = paramBoolean;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\widget\GridLayoutManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */