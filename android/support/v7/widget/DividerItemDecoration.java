package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.view.View;

public class DividerItemDecoration
  extends RecyclerView.ItemDecoration
{
  private static final int[] ATTRS = { 16843284 };
  public static final int HORIZONTAL = 0;
  public static final int VERTICAL = 1;
  private final Rect mBounds = new Rect();
  private Drawable mDivider;
  private int mOrientation;
  
  public DividerItemDecoration(Context paramContext, int paramInt)
  {
    paramContext = paramContext.obtainStyledAttributes(ATTRS);
    this.mDivider = paramContext.getDrawable(0);
    paramContext.recycle();
    setOrientation(paramInt);
  }
  
  private void drawHorizontal(Canvas paramCanvas, RecyclerView paramRecyclerView)
  {
    paramCanvas.save();
    int j;
    int i;
    if (paramRecyclerView.getClipToPadding())
    {
      j = paramRecyclerView.getPaddingTop();
      i = paramRecyclerView.getHeight() - paramRecyclerView.getPaddingBottom();
      paramCanvas.clipRect(paramRecyclerView.getPaddingLeft(), j, paramRecyclerView.getWidth() - paramRecyclerView.getPaddingRight(), i);
    }
    for (;;)
    {
      int m = paramRecyclerView.getChildCount();
      int k = 0;
      while (k < m)
      {
        View localView = paramRecyclerView.getChildAt(k);
        paramRecyclerView.getLayoutManager().getDecoratedBoundsWithMargins(localView, this.mBounds);
        int n = this.mBounds.right + Math.round(ViewCompat.getTranslationX(localView));
        int i1 = this.mDivider.getIntrinsicWidth();
        this.mDivider.setBounds(n - i1, j, n, i);
        this.mDivider.draw(paramCanvas);
        k += 1;
      }
      j = 0;
      i = paramRecyclerView.getHeight();
    }
    paramCanvas.restore();
  }
  
  private void drawVertical(Canvas paramCanvas, RecyclerView paramRecyclerView)
  {
    paramCanvas.save();
    int i;
    int j;
    if (paramRecyclerView.getClipToPadding())
    {
      i = paramRecyclerView.getPaddingLeft();
      j = paramRecyclerView.getWidth() - paramRecyclerView.getPaddingRight();
      paramCanvas.clipRect(i, paramRecyclerView.getPaddingTop(), j, paramRecyclerView.getHeight() - paramRecyclerView.getPaddingBottom());
    }
    for (;;)
    {
      int m = paramRecyclerView.getChildCount();
      int k = 0;
      while (k < m)
      {
        View localView = paramRecyclerView.getChildAt(k);
        paramRecyclerView.getDecoratedBoundsWithMargins(localView, this.mBounds);
        int n = this.mBounds.bottom + Math.round(ViewCompat.getTranslationY(localView));
        int i1 = this.mDivider.getIntrinsicHeight();
        this.mDivider.setBounds(i, n - i1, j, n);
        this.mDivider.draw(paramCanvas);
        k += 1;
      }
      i = 0;
      j = paramRecyclerView.getWidth();
    }
    paramCanvas.restore();
  }
  
  public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    if (this.mOrientation == 1)
    {
      paramRect.set(0, 0, 0, this.mDivider.getIntrinsicHeight());
      return;
    }
    paramRect.set(0, 0, this.mDivider.getIntrinsicWidth(), 0);
  }
  
  public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    if (paramRecyclerView.getLayoutManager() == null) {
      return;
    }
    if (this.mOrientation == 1)
    {
      drawVertical(paramCanvas, paramRecyclerView);
      return;
    }
    drawHorizontal(paramCanvas, paramRecyclerView);
  }
  
  public void setDrawable(@NonNull Drawable paramDrawable)
  {
    if (paramDrawable == null) {
      throw new IllegalArgumentException("Drawable cannot be null.");
    }
    this.mDivider = paramDrawable;
  }
  
  public void setOrientation(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1)) {
      throw new IllegalArgumentException("Invalid orientation. It should be either HORIZONTAL or VERTICAL");
    }
    this.mOrientation = paramInt;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\widget\DividerItemDecoration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */