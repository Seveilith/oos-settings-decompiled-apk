package android.support.v7.preference;

import android.os.Bundle;
import android.support.annotation.RestrictTo;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerViewAccessibilityDelegate;
import android.view.View;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public class PreferenceRecyclerViewAccessibilityDelegate
  extends RecyclerViewAccessibilityDelegate
{
  final AccessibilityDelegateCompat mDefaultItemDelegate = super.getItemDelegate();
  final AccessibilityDelegateCompat mItemDelegate = new AccessibilityDelegateCompat()
  {
    public void onInitializeAccessibilityNodeInfo(View paramAnonymousView, AccessibilityNodeInfoCompat paramAnonymousAccessibilityNodeInfoCompat)
    {
      PreferenceRecyclerViewAccessibilityDelegate.this.mDefaultItemDelegate.onInitializeAccessibilityNodeInfo(paramAnonymousView, paramAnonymousAccessibilityNodeInfoCompat);
      int i = PreferenceRecyclerViewAccessibilityDelegate.this.mRecyclerView.getChildAdapterPosition(paramAnonymousView);
      paramAnonymousView = PreferenceRecyclerViewAccessibilityDelegate.this.mRecyclerView.getAdapter();
      if (!(paramAnonymousView instanceof PreferenceGroupAdapter)) {
        return;
      }
      paramAnonymousView = ((PreferenceGroupAdapter)paramAnonymousView).getItem(i);
      if (paramAnonymousView == null) {
        return;
      }
      paramAnonymousView.onInitializeAccessibilityNodeInfo(paramAnonymousAccessibilityNodeInfoCompat);
    }
    
    public boolean performAccessibilityAction(View paramAnonymousView, int paramAnonymousInt, Bundle paramAnonymousBundle)
    {
      return PreferenceRecyclerViewAccessibilityDelegate.this.mDefaultItemDelegate.performAccessibilityAction(paramAnonymousView, paramAnonymousInt, paramAnonymousBundle);
    }
  };
  final RecyclerView mRecyclerView;
  
  public PreferenceRecyclerViewAccessibilityDelegate(RecyclerView paramRecyclerView)
  {
    super(paramRecyclerView);
    this.mRecyclerView = paramRecyclerView;
  }
  
  public AccessibilityDelegateCompat getItemDelegate()
  {
    return this.mItemDelegate;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\preference\PreferenceRecyclerViewAccessibilityDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */