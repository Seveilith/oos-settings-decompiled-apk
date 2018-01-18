package com.oneplus.settings.ui;

import android.content.Context;
import android.provider.Settings.System;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class OPSeekBarPreference
  extends Preference
{
  private Context mContext;
  OPColorModeSeekBarChangeListener mOPColorModeSeekBarChangeListener;
  private SeekBar mSeekBar;
  
  public OPSeekBarPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public OPSeekBarPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public OPSeekBarPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public OPSeekBarPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    this.mContext = paramContext;
    setLayoutResource(2130968859);
  }
  
  private void initSeekBar(PreferenceViewHolder paramPreferenceViewHolder)
  {
    this.mSeekBar = ((SeekBar)paramPreferenceViewHolder.findViewById(2131362388));
    this.mSeekBar.setMax(100);
    int i = Settings.System.getInt(this.mContext.getContentResolver(), "oem_screen_better_value", 43);
    this.mSeekBar.setProgress(i);
    this.mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
    {
      public void onProgressChanged(SeekBar paramAnonymousSeekBar, int paramAnonymousInt, boolean paramAnonymousBoolean)
      {
        if (OPSeekBarPreference.this.mOPColorModeSeekBarChangeListener != null) {
          OPSeekBarPreference.this.mOPColorModeSeekBarChangeListener.onProgressChanged(paramAnonymousSeekBar, paramAnonymousInt, paramAnonymousBoolean);
        }
      }
      
      public void onStartTrackingTouch(SeekBar paramAnonymousSeekBar)
      {
        if (OPSeekBarPreference.this.mOPColorModeSeekBarChangeListener != null) {
          OPSeekBarPreference.this.mOPColorModeSeekBarChangeListener.onStartTrackingTouch(paramAnonymousSeekBar);
        }
      }
      
      public void onStopTrackingTouch(SeekBar paramAnonymousSeekBar)
      {
        if (OPSeekBarPreference.this.mOPColorModeSeekBarChangeListener != null) {
          OPSeekBarPreference.this.mOPColorModeSeekBarChangeListener.onStopTrackingTouch(paramAnonymousSeekBar);
        }
      }
    });
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    initSeekBar(paramPreferenceViewHolder);
  }
  
  public void setOPColorModeSeekBarChangeListener(OPColorModeSeekBarChangeListener paramOPColorModeSeekBarChangeListener)
  {
    this.mOPColorModeSeekBarChangeListener = paramOPColorModeSeekBarChangeListener;
  }
  
  public static abstract interface OPColorModeSeekBarChangeListener
  {
    public abstract void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean);
    
    public abstract void onStartTrackingTouch(SeekBar paramSeekBar);
    
    public abstract void onStopTrackingTouch(SeekBar paramSeekBar);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ui\OPSeekBarPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */