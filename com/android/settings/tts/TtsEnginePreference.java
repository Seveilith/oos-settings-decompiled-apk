package com.android.settings.tts;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech.EngineInfo;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import com.android.settings.SettingsActivity;

public class TtsEnginePreference
  extends Preference
{
  static final String FRAGMENT_ARGS_LABEL = "label";
  static final String FRAGMENT_ARGS_NAME = "name";
  static final String FRAGMENT_ARGS_VOICES = "voices";
  private static final String TAG = "TtsEnginePreference";
  private final TextToSpeech.EngineInfo mEngineInfo;
  private volatile boolean mPreventRadioButtonCallbacks;
  private RadioButton mRadioButton;
  private final CompoundButton.OnCheckedChangeListener mRadioChangeListener = new CompoundButton.OnCheckedChangeListener()
  {
    public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
    {
      TtsEnginePreference.-wrap1(TtsEnginePreference.this, paramAnonymousCompoundButton, paramAnonymousBoolean);
    }
  };
  private final SettingsActivity mSettingsActivity;
  private View mSettingsIcon;
  private final RadioButtonGroupState mSharedState;
  private Intent mVoiceCheckData;
  
  public TtsEnginePreference(Context paramContext, TextToSpeech.EngineInfo paramEngineInfo, RadioButtonGroupState paramRadioButtonGroupState, SettingsActivity paramSettingsActivity)
  {
    super(paramContext);
    setLayoutResource(2130968924);
    this.mSharedState = paramRadioButtonGroupState;
    this.mSettingsActivity = paramSettingsActivity;
    this.mEngineInfo = paramEngineInfo;
    this.mPreventRadioButtonCallbacks = false;
    setKey(this.mEngineInfo.name);
    setTitle(this.mEngineInfo.label);
  }
  
  private void displayDataAlert(DialogInterface.OnClickListener paramOnClickListener1, DialogInterface.OnClickListener paramOnClickListener2)
  {
    Log.i("TtsEnginePreference", "Displaying data alert for :" + this.mEngineInfo.name);
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getContext());
    localBuilder.setTitle(17039380).setMessage(getContext().getString(2131689609, new Object[] { this.mEngineInfo.label })).setCancelable(true).setPositiveButton(17039370, paramOnClickListener1).setNegativeButton(17039360, paramOnClickListener2);
    localBuilder.create().show();
  }
  
  private void makeCurrentEngine(Checkable paramCheckable)
  {
    if (this.mSharedState.getCurrentChecked() != null) {
      this.mSharedState.getCurrentChecked().setChecked(false);
    }
    this.mSharedState.setCurrentChecked(paramCheckable);
    this.mSharedState.setCurrentKey(getKey());
    callChangeListener(this.mSharedState.getCurrentKey());
    this.mSettingsIcon.setEnabled(true);
  }
  
  private void onRadioButtonClicked(final CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    if ((this.mPreventRadioButtonCallbacks) || (this.mSharedState.getCurrentChecked() == paramCompoundButton)) {
      return;
    }
    if (paramBoolean)
    {
      if (shouldDisplayDataAlert())
      {
        displayDataAlert(new DialogInterface.OnClickListener()new DialogInterface.OnClickListener
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            TtsEnginePreference.-wrap0(TtsEnginePreference.this, paramCompoundButton);
          }
        }, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            paramCompoundButton.setChecked(false);
          }
        });
        return;
      }
      makeCurrentEngine(paramCompoundButton);
      return;
    }
    this.mSettingsIcon.setEnabled(false);
  }
  
  private boolean shouldDisplayDataAlert()
  {
    return !this.mEngineInfo.system;
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    boolean bool1 = true;
    super.onBindViewHolder(paramPreferenceViewHolder);
    if (this.mSharedState == null) {
      throw new IllegalStateException("Call to getView() before a call tosetSharedState()");
    }
    RadioButton localRadioButton = (RadioButton)paramPreferenceViewHolder.findViewById(2131362451);
    localRadioButton.setOnCheckedChangeListener(this.mRadioChangeListener);
    localRadioButton.setText(this.mEngineInfo.label);
    boolean bool2 = getKey().equals(this.mSharedState.getCurrentKey());
    if (bool2) {
      this.mSharedState.setCurrentChecked(localRadioButton);
    }
    this.mPreventRadioButtonCallbacks = true;
    localRadioButton.setChecked(bool2);
    this.mPreventRadioButtonCallbacks = false;
    this.mRadioButton = localRadioButton;
    this.mSettingsIcon = paramPreferenceViewHolder.findViewById(2131362452);
    paramPreferenceViewHolder = this.mSettingsIcon;
    if ((bool2) && (this.mVoiceCheckData != null)) {}
    for (;;)
    {
      paramPreferenceViewHolder.setEnabled(bool1);
      if (!bool2) {
        this.mSettingsIcon.setAlpha(0.4F);
      }
      this.mSettingsIcon.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = new Bundle();
          paramAnonymousView.putString("name", TtsEnginePreference.-get0(TtsEnginePreference.this).name);
          paramAnonymousView.putString("label", TtsEnginePreference.-get0(TtsEnginePreference.this).label);
          if (TtsEnginePreference.-get2(TtsEnginePreference.this) != null) {
            paramAnonymousView.putParcelable("voices", TtsEnginePreference.-get2(TtsEnginePreference.this));
          }
          TtsEnginePreference.-get1(TtsEnginePreference.this).startPreferencePanel(TtsEngineSettingsFragment.class.getName(), paramAnonymousView, 0, TtsEnginePreference.-get0(TtsEnginePreference.this).label, null, 0);
        }
      });
      if (this.mVoiceCheckData != null) {
        this.mSettingsIcon.setEnabled(this.mRadioButton.isChecked());
      }
      return;
      bool1 = false;
    }
  }
  
  public void setVoiceDataDetails(Intent paramIntent)
  {
    this.mVoiceCheckData = paramIntent;
    if ((this.mSettingsIcon != null) && (this.mRadioButton != null))
    {
      if (this.mRadioButton.isChecked()) {
        this.mSettingsIcon.setEnabled(true);
      }
    }
    else {
      return;
    }
    this.mSettingsIcon.setEnabled(false);
    this.mSettingsIcon.setAlpha(0.4F);
  }
  
  public static abstract interface RadioButtonGroupState
  {
    public abstract Checkable getCurrentChecked();
    
    public abstract String getCurrentKey();
    
    public abstract void setCurrentChecked(Checkable paramCheckable);
    
    public abstract void setCurrentKey(String paramString);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\tts\TtsEnginePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */