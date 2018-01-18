package com.android.settings.voice;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.provider.Settings.Secure;
import android.service.voice.VoiceInteractionServiceInfo;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import com.android.settings.AppListPreferenceWithSettings;
import java.util.ArrayList;
import java.util.List;

public class VoiceInputListPreference
  extends AppListPreferenceWithSettings
{
  private ComponentName mAssistRestrict;
  private final List<Integer> mAvailableIndexes = new ArrayList();
  private VoiceInputHelper mHelper;
  
  public VoiceInputListPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setDialogTitle(2131693436);
  }
  
  protected ListAdapter createListAdapter()
  {
    return new CustomAdapter(getContext(), getEntries());
  }
  
  public ComponentName getCurrentService()
  {
    if (this.mHelper.mCurrentVoiceInteraction != null) {
      return this.mHelper.mCurrentVoiceInteraction;
    }
    if (this.mHelper.mCurrentRecognizer != null) {
      return this.mHelper.mCurrentRecognizer;
    }
    return null;
  }
  
  protected boolean persistString(String paramString)
  {
    int i = 0;
    Object localObject;
    while (i < this.mHelper.mAvailableInteractionInfos.size())
    {
      localObject = (VoiceInputHelper.InteractionInfo)this.mHelper.mAvailableInteractionInfos.get(i);
      if (((VoiceInputHelper.InteractionInfo)localObject).key.equals(paramString))
      {
        Settings.Secure.putString(getContext().getContentResolver(), "voice_interaction_service", paramString);
        Settings.Secure.putString(getContext().getContentResolver(), "voice_recognition_service", new ComponentName(((VoiceInputHelper.InteractionInfo)localObject).service.packageName, ((VoiceInputHelper.InteractionInfo)localObject).serviceInfo.getRecognitionService()).flattenToShortString());
        setSummary(getEntry());
        setSettingsComponent(((VoiceInputHelper.InteractionInfo)localObject).settings);
        return true;
      }
      i += 1;
    }
    i = 0;
    while (i < this.mHelper.mAvailableRecognizerInfos.size())
    {
      localObject = (VoiceInputHelper.RecognizerInfo)this.mHelper.mAvailableRecognizerInfos.get(i);
      if (((VoiceInputHelper.RecognizerInfo)localObject).key.equals(paramString))
      {
        Settings.Secure.putString(getContext().getContentResolver(), "voice_interaction_service", "");
        Settings.Secure.putString(getContext().getContentResolver(), "voice_recognition_service", paramString);
        setSummary(getEntry());
        setSettingsComponent(((VoiceInputHelper.RecognizerInfo)localObject).settings);
        return true;
      }
      i += 1;
    }
    setSettingsComponent(null);
    return true;
  }
  
  public void refreshVoiceInputs()
  {
    this.mHelper = new VoiceInputHelper(getContext());
    this.mHelper.buildUi();
    if (this.mAssistRestrict == null) {}
    ArrayList localArrayList1;
    ArrayList localArrayList2;
    for (Object localObject = "";; localObject = this.mAssistRestrict.flattenToShortString())
    {
      this.mAvailableIndexes.clear();
      localArrayList1 = new ArrayList();
      localArrayList2 = new ArrayList();
      i = 0;
      while (i < this.mHelper.mAvailableInteractionInfos.size())
      {
        VoiceInputHelper.InteractionInfo localInteractionInfo = (VoiceInputHelper.InteractionInfo)this.mHelper.mAvailableInteractionInfos.get(i);
        localArrayList1.add(localInteractionInfo.appLabel);
        localArrayList2.add(localInteractionInfo.key);
        if (localInteractionInfo.key.contentEquals((CharSequence)localObject)) {
          this.mAvailableIndexes.add(Integer.valueOf(i));
        }
        i += 1;
      }
    }
    if (this.mAvailableIndexes.isEmpty()) {}
    for (int i = 0;; i = 1)
    {
      int k = localArrayList1.size();
      int j = 0;
      while (j < this.mHelper.mAvailableRecognizerInfos.size())
      {
        localObject = (VoiceInputHelper.RecognizerInfo)this.mHelper.mAvailableRecognizerInfos.get(j);
        localArrayList1.add(((VoiceInputHelper.RecognizerInfo)localObject).label);
        localArrayList2.add(((VoiceInputHelper.RecognizerInfo)localObject).key);
        if (i == 0) {
          this.mAvailableIndexes.add(Integer.valueOf(k + j));
        }
        j += 1;
      }
    }
    setEntries((CharSequence[])localArrayList1.toArray(new CharSequence[localArrayList1.size()]));
    setEntryValues((CharSequence[])localArrayList2.toArray(new CharSequence[localArrayList2.size()]));
    if (this.mHelper.mCurrentVoiceInteraction != null)
    {
      setValue(this.mHelper.mCurrentVoiceInteraction.flattenToShortString());
      return;
    }
    if (this.mHelper.mCurrentRecognizer != null)
    {
      setValue(this.mHelper.mCurrentRecognizer.flattenToShortString());
      return;
    }
    setValue(null);
  }
  
  public void setAssistRestrict(ComponentName paramComponentName)
  {
    this.mAssistRestrict = paramComponentName;
  }
  
  public void setPackageNames(CharSequence[] paramArrayOfCharSequence, CharSequence paramCharSequence) {}
  
  private class CustomAdapter
    extends ArrayAdapter<CharSequence>
  {
    public CustomAdapter(Context paramContext, CharSequence[] paramArrayOfCharSequence)
    {
      super(17367268, 16908308, paramArrayOfCharSequence);
    }
    
    public boolean areAllItemsEnabled()
    {
      return false;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      paramView = super.getView(paramInt, paramView, paramViewGroup);
      paramView.setEnabled(isEnabled(paramInt));
      return paramView;
    }
    
    public boolean isEnabled(int paramInt)
    {
      return VoiceInputListPreference.-get0(VoiceInputListPreference.this).contains(Integer.valueOf(paramInt));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\voice\VoiceInputListPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */