package com.android.settings;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v14.preference.ListPreferenceDialogFragment;
import android.support.v7.preference.ListPreference;
import android.util.AttributeSet;

public class CustomListPreference
  extends ListPreference
{
  public CustomListPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public CustomListPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  protected boolean isAutoClosePreference()
  {
    return true;
  }
  
  protected void onDialogClosed(boolean paramBoolean) {}
  
  protected void onDialogCreated(Dialog paramDialog) {}
  
  protected void onDialogStateRestored(Dialog paramDialog, Bundle paramBundle) {}
  
  protected void onPrepareDialogBuilder(AlertDialog.Builder paramBuilder, DialogInterface.OnClickListener paramOnClickListener) {}
  
  public static class CustomListPreferenceDialogFragment
    extends ListPreferenceDialogFragment
  {
    private static final String KEY_CLICKED_ENTRY_INDEX = "settings.CustomListPrefDialog.KEY_CLICKED_ENTRY_INDEX";
    private int mClickedDialogEntryIndex;
    
    private CustomListPreference getCustomizablePreference()
    {
      return (CustomListPreference)getPreference();
    }
    
    public static ListPreferenceDialogFragment newInstance(String paramString)
    {
      CustomListPreferenceDialogFragment localCustomListPreferenceDialogFragment = new CustomListPreferenceDialogFragment();
      Bundle localBundle = new Bundle(1);
      localBundle.putString("key", paramString);
      localCustomListPreferenceDialogFragment.setArguments(localBundle);
      return localCustomListPreferenceDialogFragment;
    }
    
    protected DialogInterface.OnClickListener getOnItemClickListener()
    {
      new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          CustomListPreference.CustomListPreferenceDialogFragment.this.setClickedDialogEntryIndex(paramAnonymousInt);
          if (CustomListPreference.this.isAutoClosePreference())
          {
            CustomListPreference.CustomListPreferenceDialogFragment.this.onClick(paramAnonymousDialogInterface, -1);
            paramAnonymousDialogInterface.dismiss();
          }
        }
      };
    }
    
    public void onActivityCreated(Bundle paramBundle)
    {
      super.onActivityCreated(paramBundle);
      getCustomizablePreference().onDialogStateRestored(getDialog(), paramBundle);
    }
    
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      Dialog localDialog = super.onCreateDialog(paramBundle);
      if (paramBundle != null) {
        this.mClickedDialogEntryIndex = paramBundle.getInt("settings.CustomListPrefDialog.KEY_CLICKED_ENTRY_INDEX", this.mClickedDialogEntryIndex);
      }
      getCustomizablePreference().onDialogCreated(localDialog);
      return localDialog;
    }
    
    public void onDialogClosed(boolean paramBoolean)
    {
      getCustomizablePreference().onDialogClosed(paramBoolean);
      CustomListPreference localCustomListPreference = getCustomizablePreference();
      if ((paramBoolean) && (this.mClickedDialogEntryIndex >= 0) && (localCustomListPreference.getEntryValues() != null))
      {
        String str = localCustomListPreference.getEntryValues()[this.mClickedDialogEntryIndex].toString();
        if (localCustomListPreference.callChangeListener(str)) {
          localCustomListPreference.setValue(str);
        }
      }
    }
    
    protected void onPrepareDialogBuilder(AlertDialog.Builder paramBuilder)
    {
      super.onPrepareDialogBuilder(paramBuilder);
      this.mClickedDialogEntryIndex = getCustomizablePreference().findIndexOfValue(getCustomizablePreference().getValue());
      getCustomizablePreference().onPrepareDialogBuilder(paramBuilder, getOnItemClickListener());
      if (!getCustomizablePreference().isAutoClosePreference()) {
        paramBuilder.setPositiveButton(2131690994, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            CustomListPreference.CustomListPreferenceDialogFragment.this.onClick(paramAnonymousDialogInterface, -1);
            paramAnonymousDialogInterface.dismiss();
          }
        });
      }
    }
    
    public void onSaveInstanceState(Bundle paramBundle)
    {
      super.onSaveInstanceState(paramBundle);
      paramBundle.putInt("settings.CustomListPrefDialog.KEY_CLICKED_ENTRY_INDEX", this.mClickedDialogEntryIndex);
    }
    
    protected void setClickedDialogEntryIndex(int paramInt)
    {
      this.mClickedDialogEntryIndex = paramInt;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\CustomListPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */