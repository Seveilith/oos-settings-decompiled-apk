package com.android.settings;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v14.preference.ListPreferenceDialogFragment;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.android.settingslib.RestrictedPreferenceHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RestrictedListPreference
  extends CustomListPreference
{
  private final RestrictedPreferenceHelper mHelper;
  private final List<RestrictedItem> mRestrictedItems = new ArrayList();
  
  public RestrictedListPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mHelper = new RestrictedPreferenceHelper(paramContext, this, paramAttributeSet);
  }
  
  public RestrictedListPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    this.mHelper = new RestrictedPreferenceHelper(paramContext, this, paramAttributeSet);
  }
  
  private RestrictedItem getRestrictedItemForEntryValue(CharSequence paramCharSequence)
  {
    if (paramCharSequence == null) {
      return null;
    }
    Iterator localIterator = this.mRestrictedItems.iterator();
    while (localIterator.hasNext())
    {
      RestrictedItem localRestrictedItem = (RestrictedItem)localIterator.next();
      if (paramCharSequence.equals(localRestrictedItem.entryValue)) {
        return localRestrictedItem;
      }
    }
    return null;
  }
  
  public void addRestrictedItem(RestrictedItem paramRestrictedItem)
  {
    this.mRestrictedItems.add(paramRestrictedItem);
  }
  
  public void clearRestrictedItems()
  {
    this.mRestrictedItems.clear();
  }
  
  protected ListAdapter createListAdapter()
  {
    return new RestrictedArrayAdapter(getContext(), getEntries(), getSelectedValuePos());
  }
  
  public int getSelectedValuePos()
  {
    String str = getValue();
    if (str == null) {
      return -1;
    }
    return findIndexOfValue(str);
  }
  
  public boolean isDisabledByAdmin()
  {
    return this.mHelper.isDisabledByAdmin();
  }
  
  public boolean isRestrictedForEntry(CharSequence paramCharSequence)
  {
    if (paramCharSequence == null) {
      return false;
    }
    Iterator localIterator = this.mRestrictedItems.iterator();
    while (localIterator.hasNext()) {
      if (paramCharSequence.equals(((RestrictedItem)localIterator.next()).entry)) {
        return true;
      }
    }
    return false;
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    this.mHelper.onBindViewHolder(paramPreferenceViewHolder);
  }
  
  protected void onPrepareDialogBuilder(AlertDialog.Builder paramBuilder, DialogInterface.OnClickListener paramOnClickListener)
  {
    paramBuilder.setAdapter(createListAdapter(), paramOnClickListener);
  }
  
  public void performClick()
  {
    if (!this.mHelper.performClick()) {
      super.performClick();
    }
  }
  
  public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin paramEnforcedAdmin)
  {
    if (this.mHelper.setDisabledByAdmin(paramEnforcedAdmin)) {
      notifyChanged();
    }
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    if ((paramBoolean) && (isDisabledByAdmin()))
    {
      this.mHelper.setDisabledByAdmin(null);
      return;
    }
    super.setEnabled(paramBoolean);
  }
  
  public class RestrictedArrayAdapter
    extends ArrayAdapter<CharSequence>
  {
    private final int mSelectedIndex;
    
    public RestrictedArrayAdapter(Context paramContext, CharSequence[] paramArrayOfCharSequence, int paramInt)
    {
      super(2130968951, 2131362507, paramArrayOfCharSequence);
      this.mSelectedIndex = paramInt;
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      boolean bool = false;
      paramView = super.getView(paramInt, paramView, paramViewGroup);
      paramViewGroup = (CharSequence)getItem(paramInt);
      CheckedTextView localCheckedTextView = (CheckedTextView)paramView.findViewById(2131362507);
      ImageView localImageView = (ImageView)paramView.findViewById(2131362508);
      if (RestrictedListPreference.this.isRestrictedForEntry(paramViewGroup))
      {
        localCheckedTextView.setEnabled(false);
        localCheckedTextView.setChecked(false);
        localImageView.setVisibility(0);
        return paramView;
      }
      if (this.mSelectedIndex != -1)
      {
        if (paramInt == this.mSelectedIndex) {
          bool = true;
        }
        localCheckedTextView.setChecked(bool);
      }
      if (!localCheckedTextView.isEnabled()) {
        localCheckedTextView.setEnabled(true);
      }
      localImageView.setVisibility(8);
      return paramView;
    }
    
    public boolean hasStableIds()
    {
      return true;
    }
  }
  
  public static class RestrictedItem
  {
    public final RestrictedLockUtils.EnforcedAdmin enforcedAdmin;
    public final CharSequence entry;
    public final CharSequence entryValue;
    
    public RestrictedItem(CharSequence paramCharSequence1, CharSequence paramCharSequence2, RestrictedLockUtils.EnforcedAdmin paramEnforcedAdmin)
    {
      this.entry = paramCharSequence1;
      this.entryValue = paramCharSequence2;
      this.enforcedAdmin = paramEnforcedAdmin;
    }
  }
  
  public static class RestrictedListPreferenceDialogFragment
    extends CustomListPreference.CustomListPreferenceDialogFragment
  {
    private int mLastCheckedPosition = -1;
    
    private RestrictedListPreference getCustomizablePreference()
    {
      return (RestrictedListPreference)getPreference();
    }
    
    private int getLastCheckedPosition()
    {
      if (this.mLastCheckedPosition == -1) {
        this.mLastCheckedPosition = getCustomizablePreference().getSelectedValuePos();
      }
      return this.mLastCheckedPosition;
    }
    
    public static ListPreferenceDialogFragment newInstance(String paramString)
    {
      RestrictedListPreferenceDialogFragment localRestrictedListPreferenceDialogFragment = new RestrictedListPreferenceDialogFragment();
      Bundle localBundle = new Bundle(1);
      localBundle.putString("key", paramString);
      localRestrictedListPreferenceDialogFragment.setArguments(localBundle);
      return localRestrictedListPreferenceDialogFragment;
    }
    
    private void setCheckedPosition(int paramInt)
    {
      this.mLastCheckedPosition = paramInt;
    }
    
    protected DialogInterface.OnClickListener getOnItemClickListener()
    {
      new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          Object localObject = RestrictedListPreference.this;
          if ((paramAnonymousInt < 0) || (paramAnonymousInt >= ((RestrictedListPreference)localObject).getEntryValues().length)) {
            return;
          }
          localObject = RestrictedListPreference.-wrap0((RestrictedListPreference)localObject, localObject.getEntryValues()[paramAnonymousInt].toString());
          if (localObject != null)
          {
            ((AlertDialog)paramAnonymousDialogInterface).getListView().setItemChecked(RestrictedListPreference.RestrictedListPreferenceDialogFragment.-wrap1(RestrictedListPreference.RestrictedListPreferenceDialogFragment.this), true);
            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(RestrictedListPreference.RestrictedListPreferenceDialogFragment.this.getContext(), ((RestrictedListPreference.RestrictedItem)localObject).enforcedAdmin);
          }
          for (;;)
          {
            if (RestrictedListPreference.this.isAutoClosePreference())
            {
              RestrictedListPreference.RestrictedListPreferenceDialogFragment.this.onClick(paramAnonymousDialogInterface, -1);
              paramAnonymousDialogInterface.dismiss();
            }
            return;
            RestrictedListPreference.RestrictedListPreferenceDialogFragment.this.setClickedDialogEntryIndex(paramAnonymousInt);
          }
        }
      };
    }
    
    protected void setClickedDialogEntryIndex(int paramInt)
    {
      super.setClickedDialogEntryIndex(paramInt);
      this.mLastCheckedPosition = paramInt;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\RestrictedListPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */