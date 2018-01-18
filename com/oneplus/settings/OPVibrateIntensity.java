package com.oneplus.settings;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings.System;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import com.android.settings.SettingsPreferenceFragment;
import com.oneplus.settings.ui.OPListDialog;
import com.oneplus.settings.ui.OPListDialog.OnDialogListItemClickListener;

public class OPVibrateIntensity
  extends SettingsPreferenceFragment
  implements OPListDialog.OnDialogListItemClickListener, Preference.OnPreferenceChangeListener
{
  private static final String KEY_INCOMING_CALL_VIBRATE_INTENSITY = "incoming_call_vibrate_intensity";
  private static final String KEY_NOTICE_VIBRATE_INTENSITY = "notice_vibrate_intensity";
  private static final String KEY_VIBRATE_ON_TOUCH_INTENSITY = "vibrate_on_touch_intensity";
  private static final String TAG = "OPVibrateIntensity";
  private Context mContext;
  private String mCunrrentType = "incoming_call_vibrate_intensity";
  private Preference mIncomingCallVibrateIntensityPreference;
  private Preference mNoticeVibrateIntensityPreference;
  private OPListDialog mOPListDialog;
  private Preference mVibrateOnTouchIntensityPreference;
  private Vibrator mVibrator;
  private long[][] sNoticeVibrateIntensity;
  private long[][] sRepeatVibrateIntensity;
  private long[][] sTouchVibrateIntensity;
  private long[][] sVibrateIntensity;
  private long[][] sVibratePatternrhythm;
  
  public OPVibrateIntensity()
  {
    long[] arrayOfLong1 = { -2L, 0L, 1000L, 1000L, 1000L };
    long[] arrayOfLong2 = { -2L, 0L, 500L, 250L, 10L, 1000L, 500L, 250L, 10L };
    long[] arrayOfLong3 = { -2L, 0L, 30L, 80L, 30L, 80L, 50L, 180L, 600L, 1000L, 30L, 80L, 30L, 80L, 50L, 180L, 600L };
    this.sVibratePatternrhythm = new long[][] { arrayOfLong1, arrayOfLong2, { -2L, 0L, 300L, 400L, 300L, 400L, 300L, 1000L, 300L, 400L, 300L, 400L, 300L }, arrayOfLong3, { -2L, 0L, 80L, 200L, 600L, 150L, 10L, 1000L, 80L, 200L, 600L, 150L, 10L } };
    arrayOfLong1 = new long[] { -2L, 500L, 1000L, 1500L, 1000L };
    arrayOfLong2 = new long[] { -3L, 500L, 1000L, 1500L, 1000L };
    this.sRepeatVibrateIntensity = new long[][] { { -1L, 500L, 1000L, 1500L, 1000L }, arrayOfLong1, arrayOfLong2 };
    arrayOfLong1 = new long[] { -2L, 500L, 1000L, 500L };
    arrayOfLong2 = new long[] { -3L, 500L, 1000L, 500L };
    this.sVibrateIntensity = new long[][] { { -1L, 500L, 1000L, 500L }, arrayOfLong1, arrayOfLong2 };
    arrayOfLong1 = new long[] { -3L, 0L, 100L, 150L, 100L, 1000L, 100L, 150L, 100L };
    this.sNoticeVibrateIntensity = new long[][] { { -1L, 0L, 100L, 150L, 100L, 1000L, 100L, 150L, 100L }, { -2L, 0L, 100L, 150L, 100L, 1000L, 100L, 150L, 100L }, arrayOfLong1 };
    this.sTouchVibrateIntensity = new long[][] { { -1L, 0L, 10L, 1000L, 10L }, { -2L, 0L, 10L, 1000L, 10L }, { -3L, 0L, 10L, 1000L, 10L } };
  }
  
  private void updateVibratePreferenceDescription(String paramString, int paramInt)
  {
    paramString = findPreference(paramString);
    if (paramString != null) {
      paramString.setSummary(this.mContext.getResources().getStringArray(2131427483)[paramInt]);
    }
  }
  
  public void OnDialogListCancelClick()
  {
    if (this.mVibrator != null) {
      this.mVibrator.cancel();
    }
  }
  
  public void OnDialogListConfirmClick(int paramInt)
  {
    if ("incoming_call_vibrate_intensity".equals(this.mCunrrentType))
    {
      Settings.System.putInt(getActivity().getContentResolver(), "incoming_call_vibrate_intensity", paramInt);
      updateVibratePreferenceDescription("incoming_call_vibrate_intensity", paramInt);
    }
    for (;;)
    {
      if (this.mVibrator != null) {
        this.mVibrator.cancel();
      }
      return;
      if (("notice_vibrate_intensity".equals(this.mCunrrentType)) && (this.mVibrator != null))
      {
        Settings.System.putInt(getActivity().getContentResolver(), "notice_vibrate_intensity", paramInt);
        updateVibratePreferenceDescription("notice_vibrate_intensity", paramInt);
      }
      else if (("vibrate_on_touch_intensity".equals(this.mCunrrentType)) && (this.mVibrator != null))
      {
        Settings.System.putInt(getActivity().getContentResolver(), "vibrate_on_touch_intensity", paramInt);
        updateVibratePreferenceDescription("vibrate_on_touch_intensity", paramInt);
      }
    }
  }
  
  public void OnDialogListItemClick(int paramInt)
  {
    Log.d("OPVibrateIntensity", "OnDialogListItemClick--index:" + paramInt);
    if (("incoming_call_vibrate_intensity".equals(this.mCunrrentType)) && (this.mVibrator != null))
    {
      int i = Settings.System.getInt(getActivity().getContentResolver(), "incoming_call_vibrate_mode", paramInt);
      this.mVibrator.cancel();
      if (paramInt == 0) {
        this.sVibratePatternrhythm[i][0] = -1L;
      }
      for (;;)
      {
        paramInt = 0;
        while (paramInt < this.sVibratePatternrhythm[i].length)
        {
          Log.d("OPVibrateIntensity", "sVibratePatternrhythm [" + i + "][" + paramInt + "] = " + this.sVibratePatternrhythm[i][paramInt]);
          paramInt += 1;
        }
        if (paramInt == 1) {
          this.sVibratePatternrhythm[i][0] = -2L;
        } else if (paramInt == 2) {
          this.sVibratePatternrhythm[i][0] = -3L;
        }
      }
      this.mVibrator.vibrate(this.sVibratePatternrhythm[i], -1);
    }
    do
    {
      return;
      if (("notice_vibrate_intensity".equals(this.mCunrrentType)) && (this.mVibrator != null))
      {
        this.mVibrator.cancel();
        this.mVibrator.vibrate(this.sNoticeVibrateIntensity[paramInt], -1);
        return;
      }
    } while ((!"vibrate_on_touch_intensity".equals(this.mCunrrentType)) || (this.mVibrator == null));
    this.mVibrator.cancel();
    this.mVibrator.vibrate(this.sTouchVibrateIntensity[paramInt], -1);
  }
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230820);
    this.mContext = getActivity();
    this.mVibrator = ((Vibrator)getActivity().getSystemService("vibrator"));
    if ((this.mVibrator == null) || (this.mVibrator.hasVibrator())) {}
    for (;;)
    {
      this.mIncomingCallVibrateIntensityPreference = findPreference("incoming_call_vibrate_intensity");
      this.mNoticeVibrateIntensityPreference = findPreference("notice_vibrate_intensity");
      this.mVibrateOnTouchIntensityPreference = findPreference("vibrate_on_touch_intensity");
      updateVibratePreferenceDescription("incoming_call_vibrate_intensity", Settings.System.getInt(getActivity().getContentResolver(), "incoming_call_vibrate_intensity", 0));
      updateVibratePreferenceDescription("notice_vibrate_intensity", Settings.System.getInt(getActivity().getContentResolver(), "notice_vibrate_intensity", 0));
      updateVibratePreferenceDescription("vibrate_on_touch_intensity", Settings.System.getInt(getActivity().getContentResolver(), "vibrate_on_touch_intensity", 0));
      return;
      this.mVibrator = null;
    }
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    return false;
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    String str = paramPreference.getKey();
    String[] arrayOfString1 = this.mContext.getResources().getStringArray(2131427484);
    String[] arrayOfString2 = this.mContext.getResources().getStringArray(2131427483);
    this.mOPListDialog = new OPListDialog(this.mContext, paramPreference.getTitle(), arrayOfString1, arrayOfString2);
    this.mOPListDialog.setOnDialogListItemClickListener(this);
    if ("incoming_call_vibrate_intensity".equals(str))
    {
      this.mOPListDialog.setVibrateKey("incoming_call_vibrate_intensity");
      this.mCunrrentType = "incoming_call_vibrate_intensity";
    }
    for (;;)
    {
      this.mOPListDialog.show();
      return true;
      if ("notice_vibrate_intensity".equals(str))
      {
        this.mOPListDialog.setVibrateKey("notice_vibrate_intensity");
        this.mCunrrentType = "notice_vibrate_intensity";
      }
      else if ("vibrate_on_touch_intensity".equals(str))
      {
        this.mOPListDialog.setVibrateKey("vibrate_on_touch_intensity");
        this.mCunrrentType = "vibrate_on_touch_intensity";
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\OPVibrateIntensity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */