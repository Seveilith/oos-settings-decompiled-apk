package com.android.settings.inputmethod;

import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.textservice.SpellCheckerInfo;
import com.android.settings.CustomListPreference;

class SpellCheckerPreference
  extends CustomListPreference
{
  private Intent mIntent;
  private final SpellCheckerInfo[] mScis;
  
  public SpellCheckerPreference(Context paramContext, SpellCheckerInfo[] paramArrayOfSpellCheckerInfo)
  {
    super(paramContext, null);
    this.mScis = paramArrayOfSpellCheckerInfo;
    setWidgetLayoutResource(2130968931);
    CharSequence[] arrayOfCharSequence1 = new CharSequence[paramArrayOfSpellCheckerInfo.length];
    CharSequence[] arrayOfCharSequence2 = new CharSequence[paramArrayOfSpellCheckerInfo.length];
    int i = 0;
    while (i < paramArrayOfSpellCheckerInfo.length)
    {
      arrayOfCharSequence1[i] = paramArrayOfSpellCheckerInfo[i].loadLabel(paramContext.getPackageManager());
      arrayOfCharSequence2[i] = String.valueOf(i);
      i += 1;
    }
    setEntries(arrayOfCharSequence1);
    setEntryValues(arrayOfCharSequence2);
  }
  
  private void onSettingsButtonClicked()
  {
    Context localContext = getContext();
    try
    {
      Intent localIntent = this.mIntent;
      if (localIntent != null) {
        localContext.startActivity(localIntent);
      }
      return;
    }
    catch (ActivityNotFoundException localActivityNotFoundException) {}
  }
  
  public boolean callChangeListener(Object paramObject)
  {
    if (paramObject != null) {}
    for (paramObject = this.mScis[Integer.parseInt((String)paramObject)];; paramObject = null) {
      return super.callChangeListener(paramObject);
    }
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    paramPreferenceViewHolder = paramPreferenceViewHolder.findViewById(2131362454);
    if (this.mIntent != null) {}
    for (int i = 0;; i = 4)
    {
      paramPreferenceViewHolder.setVisibility(i);
      paramPreferenceViewHolder.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          SpellCheckerPreference.-wrap0(SpellCheckerPreference.this);
        }
      });
      return;
    }
  }
  
  protected void onPrepareDialogBuilder(AlertDialog.Builder paramBuilder, DialogInterface.OnClickListener paramOnClickListener)
  {
    paramBuilder.setTitle(2131693671);
    paramBuilder.setSingleChoiceItems(getEntries(), findIndexOfValue(getValue()), paramOnClickListener);
  }
  
  public void setSelected(SpellCheckerInfo paramSpellCheckerInfo)
  {
    if (paramSpellCheckerInfo == null)
    {
      setValue(null);
      return;
    }
    int i = 0;
    while (i < this.mScis.length)
    {
      if (this.mScis[i].getId().equals(paramSpellCheckerInfo.getId()))
      {
        setValueIndex(i);
        return;
      }
      i += 1;
    }
  }
  
  public void setValue(String paramString)
  {
    super.setValue(paramString);
    if (paramString != null) {}
    for (int i = Integer.parseInt(paramString); i == -1; i = -1)
    {
      this.mIntent = null;
      return;
    }
    paramString = this.mScis[i];
    String str = paramString.getSettingsActivity();
    if (TextUtils.isEmpty(str))
    {
      this.mIntent = null;
      return;
    }
    this.mIntent = new Intent("android.intent.action.MAIN");
    this.mIntent.setClassName(paramString.getPackageName(), str);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\inputmethod\SpellCheckerPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */