package com.oneplus.settings.ui;

import android.content.Context;
import android.provider.Settings.System;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class OPNightModeLevelPreference
  extends Preference
{
  private static final int SEEKBAR_MAX = 600;
  private Context mContext;
  OPNightModeLevelPreferenceChangeListener mOPNightModeLevelPreferenceChangeListener;
  private SeekBar mSeekBar;
  
  public OPNightModeLevelPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public OPNightModeLevelPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public OPNightModeLevelPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public OPNightModeLevelPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    this.mContext = paramContext;
    setLayoutResource(2130968828);
  }
  
  private void initSeekBar(PreferenceViewHolder paramPreferenceViewHolder)
  {
    this.mSeekBar = ((SeekBar)paramPreferenceViewHolder.findViewById(2131362364));
    this.mSeekBar.setMax(600);
    int i = Settings.System.getInt(this.mContext.getContentResolver(), "oem_nightmode_progress_status", 400);
    this.mSeekBar.setProgress(i);
    this.mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
    {
      public void onProgressChanged(SeekBar paramAnonymousSeekBar, int paramAnonymousInt, boolean paramAnonymousBoolean)
      {
        if (OPNightModeLevelPreference.this.mOPNightModeLevelPreferenceChangeListener != null) {
          OPNightModeLevelPreference.this.mOPNightModeLevelPreferenceChangeListener.onProgressChanged(paramAnonymousSeekBar, paramAnonymousInt, paramAnonymousBoolean);
        }
      }
      
      public void onStartTrackingTouch(SeekBar paramAnonymousSeekBar)
      {
        if (OPNightModeLevelPreference.this.mOPNightModeLevelPreferenceChangeListener != null) {
          OPNightModeLevelPreference.this.mOPNightModeLevelPreferenceChangeListener.onStartTrackingTouch(paramAnonymousSeekBar);
        }
      }
      
      public void onStopTrackingTouch(SeekBar paramAnonymousSeekBar)
      {
        if (OPNightModeLevelPreference.this.mOPNightModeLevelPreferenceChangeListener != null) {
          OPNightModeLevelPreference.this.mOPNightModeLevelPreferenceChangeListener.onStopTrackingTouch(paramAnonymousSeekBar);
        }
      }
    });
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    initSeekBar(paramPreferenceViewHolder);
  }
  
  public void setOPColorModeSeekBarChangeListener(OPNightModeLevelPreferenceChangeListener paramOPNightModeLevelPreferenceChangeListener)
  {
    this.mOPNightModeLevelPreferenceChangeListener = paramOPNightModeLevelPreferenceChangeListener;
  }
  
  public static abstract interface OPNightModeLevelPreferenceChangeListener
  {
    public abstract void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean);
    
    public abstract void onStartTrackingTouch(SeekBar paramSeekBar);
    
    public abstract void onStopTrackingTouch(SeekBar paramSeekBar);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ui\OPNightModeLevelPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */