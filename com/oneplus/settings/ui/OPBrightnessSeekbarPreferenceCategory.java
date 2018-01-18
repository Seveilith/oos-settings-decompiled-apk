package com.oneplus.settings.ui;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.provider.Settings.System;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import java.io.PrintStream;

public class OPBrightnessSeekbarPreferenceCategory
  extends OPSeekbarPreferenceCategory
  implements SeekBar.OnSeekBarChangeListener
{
  private static final float BRIGHTNESS_ADJ_RESOLUTION = 100.0F;
  private static final int BRIGHTNESS_DEFALUT_VALUE = 102;
  private static final int OEM_AMOLED_BRIGHTNESS_DEFALUT_VALUE = 46;
  private boolean isManuallyTouchingSeekbar;
  private boolean mAutomatic;
  private String mBrightness;
  private OPCallbackBrightness mCallback;
  private Context mContext;
  private int mMaximumBacklight;
  private int mMinimumBacklight;
  private SeekBar mSeekBar;
  private int mSeekBarMax;
  
  public OPBrightnessSeekbarPreferenceCategory(Context paramContext)
  {
    super(paramContext);
    initView(paramContext);
  }
  
  public OPBrightnessSeekbarPreferenceCategory(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initView(paramContext);
  }
  
  public OPBrightnessSeekbarPreferenceCategory(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initView(paramContext);
  }
  
  private void initView(Context paramContext)
  {
    this.mContext = paramContext;
    paramContext = (PowerManager)this.mContext.getSystemService("power");
    this.mMinimumBacklight = paramContext.getMinimumScreenBrightnessSetting();
    this.mMaximumBacklight = paramContext.getMaximumScreenBrightnessSetting();
    this.mBrightness = Settings.System.getString(this.mContext.getContentResolver(), "screen_brightness");
    System.out.println("seekbar initView : " + this.mBrightness);
  }
  
  private void updateMode()
  {
    boolean bool = false;
    if (Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_brightness_mode", 0, -2) != 0) {
      bool = true;
    }
    this.mAutomatic = bool;
    if (this.mAutomatic)
    {
      this.mBrightness = String.valueOf((int)((1.0F + Settings.System.getFloatForUser(this.mContext.getContentResolver(), "screen_auto_brightness_adj", 0.0F, -2)) * 100.0F / 2.0F));
      return;
    }
    this.mBrightness = String.valueOf(Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_brightness", this.mMaximumBacklight, -2) - this.mMinimumBacklight);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    this.mSeekBar = ((SeekBar)paramPreferenceViewHolder.findViewById(2131362370));
    this.mSeekBarMax = this.mMaximumBacklight;
    this.mSeekBar.setMax(this.mSeekBarMax);
    if (this.mBrightness != null) {
      this.mSeekBar.setProgress(Integer.parseInt(this.mBrightness));
    }
    for (;;)
    {
      this.mSeekBar.setOnSeekBarChangeListener(this);
      paramPreferenceViewHolder.setDividerAllowedAbove(false);
      return;
      if (this.mContext.getPackageManager().hasSystemFeature("oem.amoled.support")) {
        this.mSeekBar.setProgress(46);
      } else {
        this.mSeekBar.setProgress(102);
      }
    }
  }
  
  public void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean)
  {
    Log.d("display", "seekbar progress arg1 : " + paramInt + " mSeekBar.getProgress : " + this.mSeekBar.getProgress());
    if (this.isManuallyTouchingSeekbar)
    {
      this.mCallback.onOPBrightValueChanged(0, this.mSeekBar.getProgress());
      this.mBrightness = String.valueOf(this.mSeekBar.getProgress());
    }
  }
  
  public void onStartTrackingTouch(SeekBar paramSeekBar)
  {
    Log.d("display", "start tracking seekbar");
    this.isManuallyTouchingSeekbar = true;
  }
  
  public void onStopTrackingTouch(SeekBar paramSeekBar)
  {
    Log.d("display", "stop tracking seekbar " + this.mSeekBar.getProgress());
    this.isManuallyTouchingSeekbar = false;
    if ((this.mCallback != null) && (this.mSeekBar != null)) {
      this.mCallback.saveBrightnessDataBase(this.mSeekBar.getProgress());
    }
  }
  
  public void setBrightness(String paramString)
  {
    String str = paramString;
    if (Integer.parseInt(paramString) < 0) {
      str = "0";
    }
    Log.d("display", "seekbar brightness from caller : " + str);
    this.mBrightness = str;
    Log.d("display", "seekbar brightness after set : " + this.mBrightness);
    notifyChanged();
  }
  
  public void setCallback(OPCallbackBrightness paramOPCallbackBrightness)
  {
    this.mCallback = paramOPCallbackBrightness;
  }
  
  public void setMax(int paramInt)
  {
    this.mSeekBarMax = paramInt;
    if (this.mSeekBar != null)
    {
      Log.d("display", "mseekbar is OK ! max : " + paramInt);
      this.mSeekBar.setMax(paramInt);
    }
  }
  
  public static abstract interface OPCallbackBrightness
  {
    public abstract void onOPBrightValueChanged(int paramInt1, int paramInt2);
    
    public abstract void saveBrightnessDataBase(int paramInt);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ui\OPBrightnessSeekbarPreferenceCategory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */