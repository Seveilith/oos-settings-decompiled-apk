package com.oneplus.settings.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceGroupAdapter;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import com.oneplus.settings.utils.OPUtils;

public class OPSettingsDividerItemDecoration
  extends RecyclerView.ItemDecoration
{
  private int mCategoryStartIndex = -1;
  private Context mContext;
  private Drawable mDivider;
  private int mDividerHeight;
  private boolean mHasCategory;
  boolean mLTRLayout;
  private int mMarginLeft2;
  private int mMarginLeft4;
  
  public OPSettingsDividerItemDecoration(Context paramContext, int paramInt1, int paramInt2)
  {
    this.mContext = paramContext;
    this.mDivider = ContextCompat.getDrawable(paramContext, paramInt1);
    Resources localResources = paramContext.getResources();
    this.mDividerHeight = localResources.getDimensionPixelSize(paramInt2);
    this.mMarginLeft2 = localResources.getDimensionPixelSize(2131755665);
    this.mMarginLeft4 = localResources.getDimensionPixelSize(2131755667);
    this.mLTRLayout = OPUtils.isLTRLayout(paramContext);
  }
  
  private boolean itemHasIcon(View paramView, RecyclerView paramRecyclerView)
  {
    paramView = paramRecyclerView.getChildViewHolder(paramView);
    if ((paramView instanceof PreferenceViewHolder))
    {
      paramView = ((PreferenceViewHolder)paramView).findViewById(16908294);
      return (paramView != null) && (paramView.getVisibility() != 8) && (paramView.getWidth() != 0);
    }
    return false;
  }
  
  private boolean shouldDrawDividerBelow(View paramView, RecyclerView paramRecyclerView)
  {
    RecyclerView.ViewHolder localViewHolder = paramRecyclerView.getChildViewHolder(paramView);
    if ((localViewHolder instanceof PreferenceViewHolder)) {}
    for (boolean bool = ((PreferenceViewHolder)localViewHolder).isDividerAllowedBelow(); !bool; bool = false) {
      return false;
    }
    bool = true;
    int i = paramRecyclerView.indexOfChild(paramView);
    if (i < paramRecyclerView.getChildCount() - 1)
    {
      paramView = paramRecyclerView.getChildViewHolder(paramRecyclerView.getChildAt(i + 1));
      if ((paramView instanceof PreferenceViewHolder)) {
        bool = ((PreferenceViewHolder)paramView).isDividerAllowedAbove();
      }
    }
    else
    {
      return bool;
    }
    return false;
  }
  
  public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    if (shouldDrawDividerBelow(paramView, paramRecyclerView)) {
      paramRect.bottom = this.mDividerHeight;
    }
  }
  
  public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    if (this.mDivider == null) {
      return;
    }
    int m = paramRecyclerView.getChildCount();
    int n = paramRecyclerView.getWidth();
    if (!(paramRecyclerView.getAdapter() instanceof PreferenceGroupAdapter)) {
      return;
    }
    paramState = (PreferenceGroupAdapter)paramRecyclerView.getAdapter();
    Object localObject = paramRecyclerView.getLayoutManager();
    int i = 0;
    if ((localObject instanceof LinearLayoutManager)) {
      i = ((LinearLayoutManager)localObject).findFirstVisibleItemPosition();
    }
    int j = 0;
    if (j < m)
    {
      localObject = paramRecyclerView.getChildAt(j);
      paramRecyclerView.getChildViewHolder((View)localObject);
      if ((paramState.getItem(j) instanceof PreferenceCategory))
      {
        this.mHasCategory = true;
        if (this.mCategoryStartIndex == -1) {
          this.mCategoryStartIndex = j;
        }
      }
      int k;
      label146:
      int i1;
      if ((this.mHasCategory) && (j >= this.mCategoryStartIndex - 1 - i))
      {
        k = 1;
        if (shouldDrawDividerBelow((View)localObject, paramRecyclerView))
        {
          i1 = (int)ViewCompat.getY((View)localObject) + ((View)localObject).getHeight();
          if (j != m - 1) {
            break label221;
          }
          this.mDivider.setBounds(0, i1, n, this.mDividerHeight + i1);
        }
      }
      for (;;)
      {
        this.mDivider.draw(paramCanvas);
        j += 1;
        break;
        k = 0;
        break label146;
        label221:
        if (itemHasIcon((View)localObject, paramRecyclerView))
        {
          if (k != 0)
          {
            if (this.mLTRLayout) {
              this.mDivider.setBounds(this.mMarginLeft4, i1, n, this.mDividerHeight + i1);
            } else {
              this.mDivider.setBounds(0, i1, n - this.mMarginLeft4, this.mDividerHeight + i1);
            }
          }
          else {
            this.mDivider.setBounds(0, i1, n, this.mDividerHeight + i1);
          }
        }
        else if (k != 0)
        {
          if (this.mLTRLayout) {
            this.mDivider.setBounds(this.mMarginLeft2, i1, n, this.mDividerHeight + i1);
          } else {
            this.mDivider.setBounds(0, i1, n - this.mMarginLeft2, this.mDividerHeight + i1);
          }
        }
        else {
          this.mDivider.setBounds(0, i1, n, this.mDividerHeight + i1);
        }
      }
    }
  }
  
  public void setDivider(Drawable paramDrawable)
  {
    if (paramDrawable != null) {}
    for (this.mDividerHeight = paramDrawable.getIntrinsicHeight();; this.mDividerHeight = 0)
    {
      this.mDivider = paramDrawable;
      return;
    }
  }
  
  public void setDividerHeight(int paramInt)
  {
    this.mDividerHeight = paramInt;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ui\OPSettingsDividerItemDecoration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */