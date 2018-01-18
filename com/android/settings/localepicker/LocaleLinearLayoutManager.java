package com.android.settings.localepicker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.Recycler;
import android.support.v7.widget.RecyclerView.State;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

public class LocaleLinearLayoutManager
  extends LinearLayoutManager
{
  private final AccessibilityNodeInfoCompat.AccessibilityActionCompat mActionMoveBottom;
  private final AccessibilityNodeInfoCompat.AccessibilityActionCompat mActionMoveDown;
  private final AccessibilityNodeInfoCompat.AccessibilityActionCompat mActionMoveTop;
  private final AccessibilityNodeInfoCompat.AccessibilityActionCompat mActionMoveUp;
  private final AccessibilityNodeInfoCompat.AccessibilityActionCompat mActionRemove;
  private final LocaleDragAndDropAdapter mAdapter;
  private final Context mContext;
  
  public LocaleLinearLayoutManager(Context paramContext, LocaleDragAndDropAdapter paramLocaleDragAndDropAdapter)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mAdapter = paramLocaleDragAndDropAdapter;
    this.mActionMoveUp = new AccessibilityNodeInfoCompat.AccessibilityActionCompat(2131361814, this.mContext.getString(2131690981));
    this.mActionMoveDown = new AccessibilityNodeInfoCompat.AccessibilityActionCompat(2131361815, this.mContext.getString(2131690982));
    this.mActionMoveTop = new AccessibilityNodeInfoCompat.AccessibilityActionCompat(2131361816, this.mContext.getString(2131690983));
    this.mActionMoveBottom = new AccessibilityNodeInfoCompat.AccessibilityActionCompat(2131361817, this.mContext.getString(2131690984));
    this.mActionRemove = new AccessibilityNodeInfoCompat.AccessibilityActionCompat(2131361818, this.mContext.getString(2131690985));
  }
  
  public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
  {
    super.onInitializeAccessibilityNodeInfoForItem(paramRecycler, paramState, paramView, paramAccessibilityNodeInfoCompat);
    int i = getItemCount();
    int j = getPosition(paramView);
    if (!(paramView instanceof LocaleDragCell))
    {
      Log.e("LocaleLinearLayoutManager", "host view is not instanceof LocaleDragCell");
      return;
    }
    paramRecycler = (LocaleDragCell)paramView;
    paramAccessibilityNodeInfoCompat.setContentDescription(j + 1 + ", " + paramRecycler.getCheckbox().getContentDescription());
    if (this.mAdapter.isRemoveMode()) {
      return;
    }
    if (j > 0)
    {
      paramAccessibilityNodeInfoCompat.addAction(this.mActionMoveUp);
      paramAccessibilityNodeInfoCompat.addAction(this.mActionMoveTop);
    }
    if (j + 1 < i)
    {
      paramAccessibilityNodeInfoCompat.addAction(this.mActionMoveDown);
      paramAccessibilityNodeInfoCompat.addAction(this.mActionMoveBottom);
    }
    if (i > 1) {
      paramAccessibilityNodeInfoCompat.addAction(this.mActionRemove);
    }
  }
  
  public boolean performAccessibilityActionForItem(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, View paramView, int paramInt, Bundle paramBundle)
  {
    int i = getItemCount();
    int j = getPosition(paramView);
    boolean bool = false;
    switch (paramInt)
    {
    default: 
      return super.performAccessibilityActionForItem(paramRecycler, paramState, paramView, paramInt, paramBundle);
    case 2131361814: 
      if (j > 0)
      {
        this.mAdapter.onItemMove(j, j - 1);
        bool = true;
      }
      break;
    }
    for (;;)
    {
      if (bool) {
        this.mAdapter.doTheUpdate();
      }
      return bool;
      if (j + 1 < i)
      {
        this.mAdapter.onItemMove(j, j + 1);
        bool = true;
        continue;
        if (j != 0)
        {
          this.mAdapter.onItemMove(j, 0);
          bool = true;
          continue;
          if (j != i - 1)
          {
            this.mAdapter.onItemMove(j, i - 1);
            bool = true;
            continue;
            if (i > 1)
            {
              this.mAdapter.removeItem(j);
              bool = true;
            }
          }
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\localepicker\LocaleLinearLayoutManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */