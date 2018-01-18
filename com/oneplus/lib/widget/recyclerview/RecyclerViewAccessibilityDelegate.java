package com.oneplus.lib.widget.recyclerview;

import android.os.Bundle;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class RecyclerViewAccessibilityDelegate
  extends View.AccessibilityDelegate
{
  final View.AccessibilityDelegate mItemDelegate = new View.AccessibilityDelegate()
  {
    public void onInitializeAccessibilityNodeInfo(View paramAnonymousView, AccessibilityNodeInfo paramAnonymousAccessibilityNodeInfo)
    {
      super.onInitializeAccessibilityNodeInfo(paramAnonymousView, paramAnonymousAccessibilityNodeInfo);
      if ((!RecyclerViewAccessibilityDelegate.-wrap0(RecyclerViewAccessibilityDelegate.this)) && (RecyclerViewAccessibilityDelegate.this.mRecyclerView.getLayoutManager() != null)) {
        RecyclerViewAccessibilityDelegate.this.mRecyclerView.getLayoutManager().onInitializeAccessibilityNodeInfoForItem(paramAnonymousView, paramAnonymousAccessibilityNodeInfo);
      }
    }
    
    public boolean performAccessibilityAction(View paramAnonymousView, int paramAnonymousInt, Bundle paramAnonymousBundle)
    {
      if (super.performAccessibilityAction(paramAnonymousView, paramAnonymousInt, paramAnonymousBundle)) {
        return true;
      }
      if ((!RecyclerViewAccessibilityDelegate.-wrap0(RecyclerViewAccessibilityDelegate.this)) && (RecyclerViewAccessibilityDelegate.this.mRecyclerView.getLayoutManager() != null)) {
        return RecyclerViewAccessibilityDelegate.this.mRecyclerView.getLayoutManager().performAccessibilityActionForItem(paramAnonymousView, paramAnonymousInt, paramAnonymousBundle);
      }
      return false;
    }
  };
  final RecyclerView mRecyclerView;
  
  public RecyclerViewAccessibilityDelegate(RecyclerView paramRecyclerView)
  {
    this.mRecyclerView = paramRecyclerView;
  }
  
  private boolean shouldIgnore()
  {
    return this.mRecyclerView.hasPendingAdapterUpdates();
  }
  
  View.AccessibilityDelegate getItemDelegate()
  {
    return this.mItemDelegate;
  }
  
  public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
    paramAccessibilityEvent.setClassName(RecyclerView.class.getName());
    if ((!(paramView instanceof RecyclerView)) || (shouldIgnore())) {}
    do
    {
      return;
      paramView = (RecyclerView)paramView;
    } while (paramView.getLayoutManager() == null);
    paramView.getLayoutManager().onInitializeAccessibilityEvent(paramAccessibilityEvent);
  }
  
  public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfo);
    paramAccessibilityNodeInfo.setClassName(RecyclerView.class.getName());
    if ((!shouldIgnore()) && (this.mRecyclerView.getLayoutManager() != null)) {
      this.mRecyclerView.getLayoutManager().onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
    }
  }
  
  public boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
  {
    if (super.performAccessibilityAction(paramView, paramInt, paramBundle)) {
      return true;
    }
    if ((!shouldIgnore()) && (this.mRecyclerView.getLayoutManager() != null)) {
      return this.mRecyclerView.getLayoutManager().performAccessibilityAction(paramInt, paramBundle);
    }
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\recyclerview\RecyclerViewAccessibilityDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */