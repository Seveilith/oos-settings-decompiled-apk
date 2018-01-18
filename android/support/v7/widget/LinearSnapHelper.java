package android.support.v7.widget;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

public class LinearSnapHelper
  extends SnapHelper
{
  private static final float INVALID_DISTANCE = 1.0F;
  @Nullable
  private OrientationHelper mHorizontalHelper;
  @Nullable
  private OrientationHelper mVerticalHelper;
  
  private float computeDistancePerChild(RecyclerView.LayoutManager paramLayoutManager, OrientationHelper paramOrientationHelper)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    int i = Integer.MAX_VALUE;
    int k = Integer.MIN_VALUE;
    int i2 = paramLayoutManager.getChildCount();
    if (i2 == 0) {
      return 1.0F;
    }
    int m = 0;
    if (m < i2)
    {
      View localView = paramLayoutManager.getChildAt(m);
      int n = paramLayoutManager.getPosition(localView);
      Object localObject3;
      int i1;
      if (n == -1)
      {
        localObject3 = localObject1;
        i1 = i;
        i = k;
      }
      for (;;)
      {
        m += 1;
        k = i;
        i = i1;
        localObject1 = localObject3;
        break;
        j = i;
        if (n < i)
        {
          j = n;
          localObject1 = localView;
        }
        i = k;
        i1 = j;
        localObject3 = localObject1;
        if (n > k)
        {
          i = n;
          localObject2 = localView;
          i1 = j;
          localObject3 = localObject1;
        }
      }
    }
    if ((localObject1 == null) || (localObject2 == null)) {
      return 1.0F;
    }
    int j = Math.min(paramOrientationHelper.getDecoratedStart((View)localObject1), paramOrientationHelper.getDecoratedStart((View)localObject2));
    j = Math.max(paramOrientationHelper.getDecoratedEnd((View)localObject1), paramOrientationHelper.getDecoratedEnd((View)localObject2)) - j;
    if (j == 0) {
      return 1.0F;
    }
    return j * 1.0F / (k - i + 1);
  }
  
  private int distanceToCenter(@NonNull RecyclerView.LayoutManager paramLayoutManager, @NonNull View paramView, OrientationHelper paramOrientationHelper)
  {
    int j = paramOrientationHelper.getDecoratedStart(paramView);
    int k = paramOrientationHelper.getDecoratedMeasurement(paramView) / 2;
    if (paramLayoutManager.getClipToPadding()) {}
    for (int i = paramOrientationHelper.getStartAfterPadding() + paramOrientationHelper.getTotalSpace() / 2;; i = paramOrientationHelper.getEnd() / 2) {
      return j + k - i;
    }
  }
  
  private int estimateNextPositionDiffForFling(RecyclerView.LayoutManager paramLayoutManager, OrientationHelper paramOrientationHelper, int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = calculateScrollDistance(paramInt1, paramInt2);
    float f = computeDistancePerChild(paramLayoutManager, paramOrientationHelper);
    if (f <= 0.0F) {
      return 0;
    }
    if (Math.abs(arrayOfInt[0]) > Math.abs(arrayOfInt[1])) {}
    for (paramInt1 = arrayOfInt[0]; paramInt1 > 0; paramInt1 = arrayOfInt[1]) {
      return (int)Math.floor(paramInt1 / f);
    }
    return (int)Math.ceil(paramInt1 / f);
  }
  
  @Nullable
  private View findCenterView(RecyclerView.LayoutManager paramLayoutManager, OrientationHelper paramOrientationHelper)
  {
    int i1 = paramLayoutManager.getChildCount();
    if (i1 == 0) {
      return null;
    }
    Object localObject = null;
    if (paramLayoutManager.getClipToPadding()) {}
    for (int i = paramOrientationHelper.getStartAfterPadding() + paramOrientationHelper.getTotalSpace() / 2;; i = paramOrientationHelper.getEnd() / 2)
    {
      int k = Integer.MAX_VALUE;
      int j = 0;
      while (j < i1)
      {
        View localView = paramLayoutManager.getChildAt(j);
        int n = Math.abs(paramOrientationHelper.getDecoratedStart(localView) + paramOrientationHelper.getDecoratedMeasurement(localView) / 2 - i);
        int m = k;
        if (n < k)
        {
          m = n;
          localObject = localView;
        }
        j += 1;
        k = m;
      }
    }
    return (View)localObject;
  }
  
  @NonNull
  private OrientationHelper getHorizontalHelper(@NonNull RecyclerView.LayoutManager paramLayoutManager)
  {
    if ((this.mHorizontalHelper == null) || (this.mHorizontalHelper.mLayoutManager != paramLayoutManager)) {
      this.mHorizontalHelper = OrientationHelper.createHorizontalHelper(paramLayoutManager);
    }
    return this.mHorizontalHelper;
  }
  
  @NonNull
  private OrientationHelper getVerticalHelper(@NonNull RecyclerView.LayoutManager paramLayoutManager)
  {
    if ((this.mVerticalHelper == null) || (this.mVerticalHelper.mLayoutManager != paramLayoutManager)) {
      this.mVerticalHelper = OrientationHelper.createVerticalHelper(paramLayoutManager);
    }
    return this.mVerticalHelper;
  }
  
  public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager paramLayoutManager, @NonNull View paramView)
  {
    int[] arrayOfInt = new int[2];
    if (paramLayoutManager.canScrollHorizontally()) {
      arrayOfInt[0] = distanceToCenter(paramLayoutManager, paramView, getHorizontalHelper(paramLayoutManager));
    }
    while (paramLayoutManager.canScrollVertically())
    {
      arrayOfInt[1] = distanceToCenter(paramLayoutManager, paramView, getVerticalHelper(paramLayoutManager));
      return arrayOfInt;
      arrayOfInt[0] = 0;
    }
    arrayOfInt[1] = 0;
    return arrayOfInt;
  }
  
  public View findSnapView(RecyclerView.LayoutManager paramLayoutManager)
  {
    if (paramLayoutManager.canScrollVertically()) {
      return findCenterView(paramLayoutManager, getVerticalHelper(paramLayoutManager));
    }
    if (paramLayoutManager.canScrollHorizontally()) {
      return findCenterView(paramLayoutManager, getHorizontalHelper(paramLayoutManager));
    }
    return null;
  }
  
  public int findTargetSnapPosition(RecyclerView.LayoutManager paramLayoutManager, int paramInt1, int paramInt2)
  {
    if (!(paramLayoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
      return -1;
    }
    int j = paramLayoutManager.getItemCount();
    if (j == 0) {
      return -1;
    }
    Object localObject = findSnapView(paramLayoutManager);
    if (localObject == null) {
      return -1;
    }
    int k = paramLayoutManager.getPosition((View)localObject);
    if (k == -1) {
      return -1;
    }
    localObject = ((RecyclerView.SmoothScroller.ScrollVectorProvider)paramLayoutManager).computeScrollVectorForPosition(j - 1);
    if (localObject == null) {
      return -1;
    }
    if (paramLayoutManager.canScrollHorizontally())
    {
      int i = estimateNextPositionDiffForFling(paramLayoutManager, getHorizontalHelper(paramLayoutManager), paramInt1, 0);
      paramInt1 = i;
      if (((PointF)localObject).x < 0.0F) {
        paramInt1 = -i;
      }
      if (!paramLayoutManager.canScrollVertically()) {
        break label168;
      }
      i = estimateNextPositionDiffForFling(paramLayoutManager, getVerticalHelper(paramLayoutManager), 0, paramInt2);
      paramInt2 = i;
      if (((PointF)localObject).y < 0.0F) {
        paramInt2 = -i;
      }
      label150:
      if (!paramLayoutManager.canScrollVertically()) {
        break label173;
      }
    }
    for (;;)
    {
      if (paramInt2 != 0) {
        break label178;
      }
      return -1;
      paramInt1 = 0;
      break;
      label168:
      paramInt2 = 0;
      break label150;
      label173:
      paramInt2 = paramInt1;
    }
    label178:
    paramInt2 = k + paramInt2;
    paramInt1 = paramInt2;
    if (paramInt2 < 0) {
      paramInt1 = 0;
    }
    paramInt2 = paramInt1;
    if (paramInt1 >= j) {
      paramInt2 = j - 1;
    }
    return paramInt2;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\widget\LinearSnapHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */