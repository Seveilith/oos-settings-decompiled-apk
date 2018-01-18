package com.android.setupwizardlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class DividerItemDecoration
  extends RecyclerView.ItemDecoration
{
  public static final int DIVIDER_CONDITION_BOTH = 1;
  public static final int DIVIDER_CONDITION_EITHER = 0;
  private Drawable mDivider;
  private int mDividerCondition;
  private int mDividerHeight;
  private int mDividerIntrinsicHeight;
  
  public DividerItemDecoration() {}
  
  public DividerItemDecoration(Context paramContext)
  {
    paramContext = paramContext.obtainStyledAttributes(R.styleable.SuwDividerItemDecoration);
    Drawable localDrawable = paramContext.getDrawable(R.styleable.SuwDividerItemDecoration_android_listDivider);
    int i = paramContext.getDimensionPixelSize(R.styleable.SuwDividerItemDecoration_android_dividerHeight, 0);
    int j = paramContext.getInt(R.styleable.SuwDividerItemDecoration_suwDividerCondition, 0);
    paramContext.recycle();
    setDivider(localDrawable);
    setDividerHeight(i);
    setDividerCondition(j);
  }
  
  @Deprecated
  public static DividerItemDecoration getDefault(Context paramContext)
  {
    return new DividerItemDecoration(paramContext);
  }
  
  private boolean shouldDrawDividerBelow(View paramView, RecyclerView paramRecyclerView)
  {
    paramView = paramRecyclerView.getChildViewHolder(paramView);
    int i = paramView.getLayoutPosition();
    int j = paramRecyclerView.getAdapter().getItemCount() - 1;
    if (isDividerAllowedBelow(paramView))
    {
      if (this.mDividerCondition == 0) {
        return true;
      }
    }
    else if ((this.mDividerCondition == 1) || (i == j)) {
      return false;
    }
    return (i >= j) || (isDividerAllowedAbove(paramRecyclerView.findViewHolderForLayoutPosition(i + 1)));
  }
  
  public Drawable getDivider()
  {
    return this.mDivider;
  }
  
  public int getDividerCondition()
  {
    return this.mDividerCondition;
  }
  
  public int getDividerHeight()
  {
    return this.mDividerHeight;
  }
  
  public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    if (shouldDrawDividerBelow(paramView, paramRecyclerView)) {
      if (this.mDividerHeight == 0) {
        break label29;
      }
    }
    label29:
    for (int i = this.mDividerHeight;; i = this.mDividerIntrinsicHeight)
    {
      paramRect.bottom = i;
      return;
    }
  }
  
  protected boolean isDividerAllowedAbove(RecyclerView.ViewHolder paramViewHolder)
  {
    if ((paramViewHolder instanceof DividedViewHolder)) {
      return ((DividedViewHolder)paramViewHolder).isDividerAllowedAbove();
    }
    return true;
  }
  
  protected boolean isDividerAllowedBelow(RecyclerView.ViewHolder paramViewHolder)
  {
    if ((paramViewHolder instanceof DividedViewHolder)) {
      return ((DividedViewHolder)paramViewHolder).isDividerAllowedBelow();
    }
    return true;
  }
  
  public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    if (this.mDivider == null) {
      return;
    }
    int k = paramRecyclerView.getChildCount();
    int m = paramRecyclerView.getWidth();
    if (this.mDividerHeight != 0) {}
    for (int i = this.mDividerHeight;; i = this.mDividerIntrinsicHeight)
    {
      int j = 0;
      while (j < k)
      {
        paramState = paramRecyclerView.getChildAt(j);
        if (shouldDrawDividerBelow(paramState, paramRecyclerView))
        {
          int n = (int)ViewCompat.getY(paramState) + paramState.getHeight();
          this.mDivider.setBounds(0, n, m, n + i);
          this.mDivider.draw(paramCanvas);
        }
        j += 1;
      }
    }
  }
  
  public void setDivider(Drawable paramDrawable)
  {
    if (paramDrawable != null) {}
    for (this.mDividerIntrinsicHeight = paramDrawable.getIntrinsicHeight();; this.mDividerIntrinsicHeight = 0)
    {
      this.mDivider = paramDrawable;
      return;
    }
  }
  
  public void setDividerCondition(int paramInt)
  {
    this.mDividerCondition = paramInt;
  }
  
  public void setDividerHeight(int paramInt)
  {
    this.mDividerHeight = paramInt;
  }
  
  public static abstract interface DividedViewHolder
  {
    public abstract boolean isDividerAllowedAbove();
    
    public abstract boolean isDividerAllowedBelow();
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface DividerCondition {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\DividerItemDecoration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */