package com.android.settings.accessibility;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings.System;
import com.android.settings.PreviewSeekBarPreferenceFragment;

public class ToggleFontSizePreferenceFragment
  extends PreviewSeekBarPreferenceFragment
{
  private float[] mValues;
  
  public static int fontSizeValueToIndex(float paramFloat, String[] paramArrayOfString)
  {
    float f1 = Float.parseFloat(paramArrayOfString[0]);
    int i = 1;
    while (i < paramArrayOfString.length)
    {
      float f2 = Float.parseFloat(paramArrayOfString[i]);
      if (paramFloat < (f2 - f1) * 0.5F + f1) {
        return i - 1;
      }
      f1 = f2;
      i += 1;
    }
    return paramArrayOfString.length - 1;
  }
  
  protected void commit()
  {
    if (getContext() == null) {
      return;
    }
    Settings.System.putFloat(getContext().getContentResolver(), "font_scale", this.mValues[this.mCurrentIndex]);
  }
  
  protected Configuration createConfig(Configuration paramConfiguration, int paramInt)
  {
    paramConfiguration = new Configuration(paramConfiguration);
    paramConfiguration.fontScale = this.mValues[paramInt];
    return paramConfiguration;
  }
  
  protected int getMetricsCategory()
  {
    return 340;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mActivityLayoutResId = 2130968712;
    this.mPreviewSampleResIds = new int[] { 2130968713 };
    Object localObject = getContext().getResources();
    paramBundle = getContext().getContentResolver();
    this.mEntries = ((Resources)localObject).getStringArray(2131427379);
    localObject = ((Resources)localObject).getStringArray(2131427380);
    this.mInitialIndex = fontSizeValueToIndex(Settings.System.getFloat(paramBundle, "font_scale", 1.0F), (String[])localObject);
    this.mValues = new float[localObject.length];
    int i = 0;
    while (i < localObject.length)
    {
      this.mValues[i] = Float.parseFloat(localObject[i]);
      i += 1;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accessibility\ToggleFontSizePreferenceFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */