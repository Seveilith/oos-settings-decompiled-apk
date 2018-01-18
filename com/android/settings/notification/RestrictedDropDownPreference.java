package com.android.settings.notification;

import android.content.Context;
import android.support.v7.preference.DropDownPreference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.android.settingslib.RestrictedPreferenceHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RestrictedDropDownPreference
  extends DropDownPreference
{
  private final RestrictedPreferenceHelper mHelper;
  private final AdapterView.OnItemSelectedListener mItemSelectedListener = new AdapterView.OnItemSelectedListener()
  {
    public void onItemSelected(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      if (RestrictedDropDownPreference.-get1(RestrictedDropDownPreference.this))
      {
        RestrictedDropDownPreference.-set0(RestrictedDropDownPreference.this, false);
        if ((paramAnonymousInt >= 0) && (paramAnonymousInt < RestrictedDropDownPreference.this.getEntryValues().length))
        {
          paramAnonymousAdapterView = RestrictedDropDownPreference.this.getEntryValues()[paramAnonymousInt].toString();
          paramAnonymousView = RestrictedDropDownPreference.-wrap2(RestrictedDropDownPreference.this, paramAnonymousAdapterView);
          if (paramAnonymousView == null) {
            break label103;
          }
          RestrictedLockUtils.sendShowAdminSupportDetailsIntent(RestrictedDropDownPreference.this.getContext(), paramAnonymousView.enforcedAdmin);
          RestrictedDropDownPreference.-get0(RestrictedDropDownPreference.this).setSelection(RestrictedDropDownPreference.this.findIndexOfValue(RestrictedDropDownPreference.this.getValue()));
        }
      }
      label103:
      while ((paramAnonymousAdapterView.equals(RestrictedDropDownPreference.this.getValue())) || (!RestrictedDropDownPreference.this.callChangeListener(paramAnonymousAdapterView)))
      {
        return;
        return;
      }
      RestrictedDropDownPreference.this.setValue(paramAnonymousAdapterView);
    }
    
    public void onNothingSelected(AdapterView<?> paramAnonymousAdapterView) {}
  };
  private Preference.OnPreferenceClickListener mPreClickListener;
  private List<RestrictedItem> mRestrictedItems = new ArrayList();
  private ReselectionSpinner mSpinner;
  private boolean mUserClicked = false;
  
  public RestrictedDropDownPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setLayoutResource(2130968954);
    setWidgetLayoutResource(2130968952);
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
  
  private RestrictedItem getRestrictedItemForPosition(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= getEntryValues().length)) {
      return null;
    }
    return getRestrictedItemForEntryValue(getEntryValues()[paramInt]);
  }
  
  private boolean isRestrictedForEntry(CharSequence paramCharSequence)
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
  
  private boolean isUserClicked()
  {
    return this.mUserClicked;
  }
  
  private void setUserClicked(boolean paramBoolean)
  {
    this.mUserClicked = paramBoolean;
  }
  
  public void addRestrictedItem(RestrictedItem paramRestrictedItem)
  {
    this.mRestrictedItems.add(paramRestrictedItem);
  }
  
  public void clearRestrictedItems()
  {
    this.mRestrictedItems.clear();
  }
  
  protected ArrayAdapter createAdapter()
  {
    return new RestrictedArrayItemAdapter(getContext());
  }
  
  public boolean isDisabledByAdmin()
  {
    return this.mHelper.isDisabledByAdmin();
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    this.mSpinner = ((ReselectionSpinner)paramPreferenceViewHolder.itemView.findViewById(2131362214));
    this.mSpinner.setPreference(this);
    super.onBindViewHolder(paramPreferenceViewHolder);
    this.mHelper.onBindViewHolder(paramPreferenceViewHolder);
    this.mSpinner.setOnItemSelectedListener(this.mItemSelectedListener);
    paramPreferenceViewHolder = paramPreferenceViewHolder.findViewById(2131362509);
    if (paramPreferenceViewHolder != null) {
      if (!isDisabledByAdmin()) {
        break label74;
      }
    }
    label74:
    for (int i = 0;; i = 8)
    {
      paramPreferenceViewHolder.setVisibility(i);
      return;
    }
  }
  
  public void performClick()
  {
    if ((this.mPreClickListener != null) && (this.mPreClickListener.onPreferenceClick(this))) {
      return;
    }
    if (!this.mHelper.performClick())
    {
      this.mUserClicked = true;
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
  
  public void setOnPreClickListener(Preference.OnPreferenceClickListener paramOnPreferenceClickListener)
  {
    this.mPreClickListener = paramOnPreferenceClickListener;
  }
  
  public void setValue(String paramString)
  {
    if (getRestrictedItemForEntryValue(paramString) != null) {
      return;
    }
    super.setValue(paramString);
  }
  
  public static class ReselectionSpinner
    extends Spinner
  {
    private RestrictedDropDownPreference pref;
    
    public ReselectionSpinner(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public void setPreference(RestrictedDropDownPreference paramRestrictedDropDownPreference)
    {
      this.pref = paramRestrictedDropDownPreference;
    }
    
    public void setSelection(int paramInt)
    {
      int i = getSelectedItemPosition();
      super.setSelection(paramInt);
      if ((paramInt == i) && (RestrictedDropDownPreference.-wrap1(this.pref)))
      {
        RestrictedDropDownPreference.-wrap4(this.pref, false);
        RestrictedDropDownPreference.RestrictedItem localRestrictedItem = RestrictedDropDownPreference.-wrap3(this.pref, paramInt);
        if (localRestrictedItem != null) {
          RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getContext(), localRestrictedItem.enforcedAdmin);
        }
      }
    }
  }
  
  private class RestrictedArrayItemAdapter
    extends ArrayAdapter<String>
  {
    private static final int TEXT_RES_ID = 16908308;
    
    public RestrictedArrayItemAdapter(Context paramContext)
    {
      super(2130969007, 16908308);
    }
    
    public View getDropDownView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      int i = 0;
      paramView = super.getView(paramInt, paramView, paramViewGroup);
      paramViewGroup = (CharSequence)getItem(paramInt);
      boolean bool2 = RestrictedDropDownPreference.-wrap0(RestrictedDropDownPreference.this, paramViewGroup);
      paramViewGroup = (TextView)paramView.findViewById(16908308);
      boolean bool1;
      if (paramViewGroup != null)
      {
        if (bool2)
        {
          bool1 = false;
          paramViewGroup.setEnabled(bool1);
        }
      }
      else
      {
        paramViewGroup = paramView.findViewById(2131362509);
        if (paramViewGroup != null) {
          if (!bool2) {
            break label90;
          }
        }
      }
      label90:
      for (paramInt = i;; paramInt = 8)
      {
        paramViewGroup.setVisibility(paramInt);
        return paramView;
        bool1 = true;
        break;
      }
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
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\RestrictedDropDownPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */