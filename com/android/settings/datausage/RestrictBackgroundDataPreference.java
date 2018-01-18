package com.android.settings.datausage;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.NetworkPolicyManager;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import com.android.settings.CustomDialogPreference;
import com.android.settings.Utils;
import com.android.settings.dashboard.conditional.BackgroundDataCondition;
import com.android.settings.dashboard.conditional.ConditionManager;

public class RestrictBackgroundDataPreference
  extends CustomDialogPreference
{
  private boolean mChecked;
  private NetworkPolicyManager mPolicyManager;
  
  public RestrictBackgroundDataPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet, 16843629);
  }
  
  private void setChecked(boolean paramBoolean)
  {
    if (this.mChecked == paramBoolean) {
      return;
    }
    this.mChecked = paramBoolean;
    notifyChanged();
  }
  
  public void onAttached()
  {
    super.onAttached();
    this.mPolicyManager = NetworkPolicyManager.from(getContext());
    setChecked(this.mPolicyManager.getRestrictBackground());
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    paramPreferenceViewHolder = paramPreferenceViewHolder.findViewById(16908352);
    paramPreferenceViewHolder.setClickable(false);
    ((Checkable)paramPreferenceViewHolder).setChecked(this.mChecked);
  }
  
  protected void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if (paramInt != -1) {
      return;
    }
    setRestrictBackground(true);
  }
  
  protected void onPrepareDialogBuilder(AlertDialog.Builder paramBuilder, DialogInterface.OnClickListener paramOnClickListener)
  {
    super.onPrepareDialogBuilder(paramBuilder, paramOnClickListener);
    paramBuilder.setTitle(2131692818);
    if (Utils.hasMultipleUsers(getContext())) {
      paramBuilder.setMessage(2131692820);
    }
    for (;;)
    {
      paramBuilder.setPositiveButton(17039370, paramOnClickListener);
      paramBuilder.setNegativeButton(17039360, null);
      return;
      paramBuilder.setMessage(2131692819);
    }
  }
  
  protected void performClick(View paramView)
  {
    if (this.mChecked)
    {
      setRestrictBackground(false);
      return;
    }
    super.performClick(paramView);
  }
  
  public void setRestrictBackground(boolean paramBoolean)
  {
    this.mPolicyManager.setRestrictBackground(paramBoolean);
    setChecked(paramBoolean);
    ((BackgroundDataCondition)ConditionManager.get(getContext()).getCondition(BackgroundDataCondition.class)).refreshState();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\RestrictBackgroundDataPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */