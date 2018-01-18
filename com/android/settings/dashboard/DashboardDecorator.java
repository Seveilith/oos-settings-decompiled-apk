package com.android.settings.dashboard;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.TypedValue;
import android.view.View;

public class DashboardDecorator
  extends RecyclerView.ItemDecoration
{
  private final Context mContext;
  private final Drawable mDivider;
  
  public DashboardDecorator(Context paramContext)
  {
    this.mContext = paramContext;
    paramContext = new TypedValue();
    this.mContext.getTheme().resolveAttribute(16843284, paramContext, true);
    this.mDivider = this.mContext.getDrawable(paramContext.resourceId);
  }
  
  private int getChildTop(View paramView)
  {
    RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
    return paramView.getTop() + localLayoutParams.topMargin + Math.round(ViewCompat.getTranslationY(paramView));
  }
  
  public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    int j = paramRecyclerView.getChildCount();
    int i = 1;
    if (i < j)
    {
      paramState = paramRecyclerView.getChildAt(i);
      RecyclerView.ViewHolder localViewHolder = paramRecyclerView.getChildViewHolder(paramState);
      if (localViewHolder.getItemViewType() == 2130968667) {
        if ((paramRecyclerView.getChildViewHolder(paramRecyclerView.getChildAt(i - 1)).getItemViewType() == 2130968669) && (paramRecyclerView.getChildViewHolder(paramRecyclerView.getChildAt(i - 1)).getItemViewType() == 2130968670)) {
          break label99;
        }
      }
      for (;;)
      {
        i += 1;
        break;
        if (localViewHolder.getItemViewType() == 2130968642)
        {
          label99:
          int k = getChildTop(paramState);
          this.mDivider.setBounds(paramState.getLeft(), k, paramState.getRight(), this.mDivider.getIntrinsicHeight() + k);
          this.mDivider.draw(paramCanvas);
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\dashboard\DashboardDecorator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */