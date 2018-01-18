package com.android.settings;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v14.preference.PreferenceDialogFragment;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

public class CustomDialogPreference
  extends DialogPreference
{
  private CustomPreferenceDialogFragment mFragment;
  
  public CustomDialogPreference(Context paramContext)
  {
    super(paramContext);
  }
  
  public CustomDialogPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public CustomDialogPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public CustomDialogPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  private void setFragment(CustomPreferenceDialogFragment paramCustomPreferenceDialogFragment)
  {
    this.mFragment = paramCustomPreferenceDialogFragment;
  }
  
  public Dialog getDialog()
  {
    Dialog localDialog = null;
    if (this.mFragment != null) {
      localDialog = this.mFragment.getDialog();
    }
    return localDialog;
  }
  
  public boolean isDialogOpen()
  {
    if (getDialog() != null) {
      return getDialog().isShowing();
    }
    return false;
  }
  
  protected void onBindDialogView(View paramView) {}
  
  protected void onClick(DialogInterface paramDialogInterface, int paramInt) {}
  
  protected void onDialogClosed(boolean paramBoolean) {}
  
  protected void onPrepareDialogBuilder(AlertDialog.Builder paramBuilder, DialogInterface.OnClickListener paramOnClickListener) {}
  
  public static class CustomPreferenceDialogFragment
    extends PreferenceDialogFragment
  {
    private CustomDialogPreference getCustomizablePreference()
    {
      return (CustomDialogPreference)getPreference();
    }
    
    public static CustomPreferenceDialogFragment newInstance(String paramString)
    {
      CustomPreferenceDialogFragment localCustomPreferenceDialogFragment = new CustomPreferenceDialogFragment();
      Bundle localBundle = new Bundle(1);
      localBundle.putString("key", paramString);
      localCustomPreferenceDialogFragment.setArguments(localBundle);
      return localCustomPreferenceDialogFragment;
    }
    
    protected void onBindDialogView(View paramView)
    {
      super.onBindDialogView(paramView);
      getCustomizablePreference().onBindDialogView(paramView);
    }
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      super.onClick(paramDialogInterface, paramInt);
      getCustomizablePreference().onClick(paramDialogInterface, paramInt);
    }
    
    public void onDialogClosed(boolean paramBoolean)
    {
      getCustomizablePreference().onDialogClosed(paramBoolean);
    }
    
    protected void onPrepareDialogBuilder(AlertDialog.Builder paramBuilder)
    {
      super.onPrepareDialogBuilder(paramBuilder);
      CustomDialogPreference.-wrap0(getCustomizablePreference(), this);
      getCustomizablePreference().onPrepareDialogBuilder(paramBuilder, this);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\CustomDialogPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */