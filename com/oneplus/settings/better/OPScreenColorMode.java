package com.oneplus.settings.better;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchIndexableResource;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceScreen;
import android.widget.SeekBar;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settings.ui.RadioButtonPreference;
import com.android.settings.ui.RadioButtonPreference.OnClickListener;
import com.oneplus.settings.OneplusColorManager;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.ui.OPScreenColorModeSummary;
import com.oneplus.settings.ui.OPSeekBarPreference;
import com.oneplus.settings.ui.OPSeekBarPreference.OPColorModeSeekBarChangeListener;
import java.util.ArrayList;
import java.util.List;

public class OPScreenColorMode
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceClickListener, RadioButtonPreference.OnClickListener, OPSeekBarPreference.OPColorModeSeekBarChangeListener, Indexable
{
  private static final String KEY_SCREEN_COLOR_MODE_ADAPTIVE_MODEL_SETTINGS = "screen_color_mode_adaptive_model_settings";
  private static final String KEY_SCREEN_COLOR_MODE_BASIC_SETTINGS = "screen_color_mode_basic_settings";
  private static final String KEY_SCREEN_COLOR_MODE_DCI_P3_SETTINGS = "screen_color_mode_dci_p3_settings";
  private static final String KEY_SCREEN_COLOR_MODE_DEFAULT_SETTINGS = "screen_color_mode_default_settings";
  private static final String KEY_SCREEN_COLOR_MODE_DEFINED_SETTINGS = "screen_color_mode_defined_settings";
  private static final String KEY_SCREEN_COLOR_MODE_SEEKBAR = "screen_color_mode_seekbar";
  private static final String KEY_SCREEN_COLOR_MODE_SOFT_SETTINGS = "screen_color_mode_soft_settings";
  private static final String KEY_SCREEN_COLOR_MODE_TITLE_SUMMARY = "oneplus_screen_color_mode_title_summary";
  public static final String NIGHT_MODE_ENABLED = "night_mode_enabled";
  private static final int SCREEN_COLOR_MODE_ADAPTIVE_MODEL_SETTINGS_VALUE = 5;
  private static final int SCREEN_COLOR_MODE_BASIC_SETTINGS_VALUE = 2;
  private static final int SCREEN_COLOR_MODE_DCI_P3_SETTINGS_VALUE = 4;
  private static final int SCREEN_COLOR_MODE_DEFAULT_SETTINGS_VALUE = 1;
  private static final int SCREEN_COLOR_MODE_DEFINED_SETTINGS_VALUE = 3;
  private static final int SCREEN_COLOR_MODE_SOFT_SETTINGS_VALUE = 6;
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<String> getNonIndexableKeys(Context paramAnonymousContext)
    {
      paramAnonymousContext = new ArrayList();
      if (!OPScreenColorMode.-get1()) {
        paramAnonymousContext.add("screen_color_mode_dci_p3_settings");
      }
      if (!OPScreenColorMode.-get0()) {
        paramAnonymousContext.add("screen_color_mode_adaptive_model_settings");
      }
      if (!OPScreenColorMode.-get2()) {
        paramAnonymousContext.add("screen_color_mode_soft_settings");
      }
      return paramAnonymousContext;
    }
    
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      paramAnonymousContext = new SearchIndexableResource(paramAnonymousContext);
      paramAnonymousContext.xmlResId = 2131230813;
      localArrayList.add(paramAnonymousContext);
      return localArrayList;
    }
  };
  private static boolean isSupportAdaptive = false;
  private static boolean isSupportDcip3 = false;
  private static boolean isSupportSoft = false;
  private static final String sDCI_P3Path = "/sys/devices/virtual/graphics/fb0/DCI_P3";
  private static final String sRGBPath = "/sys/devices/virtual/graphics/fb0/SRGB";
  private static final String s_OPEN_VALUE = "mode = 1";
  private boolean isSupportReadingMode;
  private OneplusColorManager mCM;
  private Context mContext;
  private OPScreenColorModeSummary mOPScreenColorModeSummary;
  private RadioButtonPreference mScreenColorModeAdaptiveModelSettings;
  private RadioButtonPreference mScreenColorModeBasicSettings;
  private ContentObserver mScreenColorModeContentObserver = new ContentObserver(new Handler())
  {
    public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
    {
      int i;
      if (Settings.Secure.getInt(OPScreenColorMode.-wrap0(OPScreenColorMode.this), "night_display_activated", 0) != 1)
      {
        i = 1;
        if (Settings.System.getInt(OPScreenColorMode.-wrap0(OPScreenColorMode.this), "reading_mode_status_manual", 0) == 1) {
          break label212;
        }
        paramAnonymousBoolean = true;
        label38:
        if (i == 0) {
          break label217;
        }
      }
      label212:
      label217:
      for (boolean bool = paramAnonymousBoolean;; bool = false)
      {
        OPScreenColorMode.-get7(OPScreenColorMode.this).setEnabled(bool);
        OPScreenColorMode.-get5(OPScreenColorMode.this).setEnabled(bool);
        OPScreenColorMode.-get8(OPScreenColorMode.this).setEnabled(bool);
        OPScreenColorMode.-get6(OPScreenColorMode.this).setEnabled(bool);
        OPScreenColorMode.-get4(OPScreenColorMode.this).setEnabled(bool);
        OPScreenColorMode.-get9(OPScreenColorMode.this).setEnabled(bool);
        OPScreenColorMode.-get10(OPScreenColorMode.this).setEnabled(bool);
        if (OPScreenColorMode.-get3(OPScreenColorMode.this) != null)
        {
          if (i == 0) {
            OPScreenColorMode.-get3(OPScreenColorMode.this).setSummary(SettingsBaseApplication.mApplication.getText(2131690095));
          }
          if (!paramAnonymousBoolean) {
            OPScreenColorMode.-get3(OPScreenColorMode.this).setSummary(SettingsBaseApplication.mApplication.getText(2131690403));
          }
          if (bool) {
            break label223;
          }
          OPScreenColorMode.this.getPreferenceScreen().addPreference(OPScreenColorMode.-get3(OPScreenColorMode.this));
        }
        return;
        i = 0;
        break;
        paramAnonymousBoolean = false;
        break label38;
      }
      label223:
      OPScreenColorMode.this.getPreferenceScreen().removePreference(OPScreenColorMode.-get3(OPScreenColorMode.this));
    }
  };
  private RadioButtonPreference mScreenColorModeDciP3Settings;
  private RadioButtonPreference mScreenColorModeDefaultSettings;
  private RadioButtonPreference mScreenColorModeDefinedSettings;
  private RadioButtonPreference mScreenColorModeSoftSettings;
  private int mScreenColorModeValue;
  private SeekBar mSeekBar;
  private OPSeekBarPreference mSeekBarpreference;
  
  private void resetDefinedScreenColorModeValue()
  {
    int i = Settings.System.getInt(this.mContext.getContentResolver(), "oem_screen_better_value", 43);
    if (this.mCM != null)
    {
      if (!this.isSupportReadingMode) {
        break label56;
      }
      this.mCM.setActiveMode(0);
      this.mCM.setColorBalance(100 - i);
    }
    for (;;)
    {
      this.mCM.saveScreenBetter();
      return;
      label56:
      this.mCM.setColorBalance(100 - i + 512);
    }
  }
  
  private void updateRadioButtons(int paramInt)
  {
    if (1 == paramInt)
    {
      this.mScreenColorModeDefaultSettings.setChecked(true);
      this.mScreenColorModeBasicSettings.setChecked(false);
      this.mScreenColorModeDefinedSettings.setChecked(false);
      this.mScreenColorModeDciP3Settings.setChecked(false);
      this.mScreenColorModeAdaptiveModelSettings.setChecked(false);
      this.mScreenColorModeSoftSettings.setChecked(false);
      removePreference("screen_color_mode_seekbar");
    }
    do
    {
      return;
      if (2 == paramInt)
      {
        this.mScreenColorModeDefaultSettings.setChecked(false);
        this.mScreenColorModeBasicSettings.setChecked(true);
        this.mScreenColorModeDefinedSettings.setChecked(false);
        this.mScreenColorModeDciP3Settings.setChecked(false);
        this.mScreenColorModeAdaptiveModelSettings.setChecked(false);
        this.mScreenColorModeSoftSettings.setChecked(false);
        removePreference("screen_color_mode_seekbar");
        return;
      }
      if (3 == paramInt)
      {
        this.mScreenColorModeDefaultSettings.setChecked(false);
        this.mScreenColorModeBasicSettings.setChecked(false);
        this.mScreenColorModeDefinedSettings.setChecked(true);
        this.mScreenColorModeDciP3Settings.setChecked(false);
        this.mScreenColorModeAdaptiveModelSettings.setChecked(false);
        getPreferenceScreen().addPreference(this.mSeekBarpreference);
        return;
      }
      if (4 == paramInt)
      {
        this.mScreenColorModeDefaultSettings.setChecked(false);
        this.mScreenColorModeBasicSettings.setChecked(false);
        this.mScreenColorModeDefinedSettings.setChecked(false);
        this.mScreenColorModeDciP3Settings.setChecked(true);
        this.mScreenColorModeAdaptiveModelSettings.setChecked(false);
        this.mScreenColorModeSoftSettings.setChecked(false);
        removePreference("screen_color_mode_seekbar");
        return;
      }
      if (5 == paramInt)
      {
        this.mScreenColorModeDefaultSettings.setChecked(false);
        this.mScreenColorModeBasicSettings.setChecked(false);
        this.mScreenColorModeDefinedSettings.setChecked(false);
        this.mScreenColorModeDciP3Settings.setChecked(false);
        this.mScreenColorModeAdaptiveModelSettings.setChecked(true);
        this.mScreenColorModeSoftSettings.setChecked(false);
        removePreference("screen_color_mode_seekbar");
        return;
      }
    } while (6 != paramInt);
    this.mScreenColorModeDefaultSettings.setChecked(false);
    this.mScreenColorModeBasicSettings.setChecked(false);
    this.mScreenColorModeDefinedSettings.setChecked(false);
    this.mScreenColorModeDciP3Settings.setChecked(false);
    this.mScreenColorModeAdaptiveModelSettings.setChecked(false);
    this.mScreenColorModeSoftSettings.setChecked(true);
    removePreference("screen_color_mode_seekbar");
  }
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public int getScreenColorModeSettingsValue()
  {
    return Settings.System.getInt(this.mContext.getContentResolver(), "screen_color_mode_settings_value", 1);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230813);
    this.mContext = SettingsBaseApplication.mApplication;
    this.isSupportReadingMode = this.mContext.getPackageManager().hasSystemFeature("oem.read_mode.support");
    this.mScreenColorModeDefaultSettings = ((RadioButtonPreference)findPreference("screen_color_mode_default_settings"));
    this.mScreenColorModeBasicSettings = ((RadioButtonPreference)findPreference("screen_color_mode_basic_settings"));
    this.mScreenColorModeDefinedSettings = ((RadioButtonPreference)findPreference("screen_color_mode_defined_settings"));
    this.mScreenColorModeDciP3Settings = ((RadioButtonPreference)findPreference("screen_color_mode_dci_p3_settings"));
    this.mScreenColorModeAdaptiveModelSettings = ((RadioButtonPreference)findPreference("screen_color_mode_adaptive_model_settings"));
    this.mScreenColorModeSoftSettings = ((RadioButtonPreference)findPreference("screen_color_mode_soft_settings"));
    this.mOPScreenColorModeSummary = ((OPScreenColorModeSummary)findPreference("oneplus_screen_color_mode_title_summary"));
    this.mSeekBarpreference = ((OPSeekBarPreference)findPreference("screen_color_mode_seekbar"));
    this.mSeekBarpreference.setOPColorModeSeekBarChangeListener(this);
    this.mScreenColorModeDefaultSettings.setOnClickListener(this);
    this.mScreenColorModeBasicSettings.setOnClickListener(this);
    this.mScreenColorModeDefinedSettings.setOnClickListener(this);
    this.mScreenColorModeDciP3Settings.setOnClickListener(this);
    this.mScreenColorModeAdaptiveModelSettings.setOnClickListener(this);
    this.mScreenColorModeSoftSettings.setOnClickListener(this);
    getPreferenceScreen().removePreference(this.mOPScreenColorModeSummary);
    this.mCM = new OneplusColorManager(this.mContext);
    isSupportDcip3 = this.mContext.getPackageManager().hasSystemFeature("oem.dcip3.support");
    if (!isSupportDcip3) {
      removePreference("screen_color_mode_dci_p3_settings");
    }
    isSupportAdaptive = this.mContext.getPackageManager().hasSystemFeature("oem.display.adaptive.mode.support");
    if (!isSupportAdaptive) {
      removePreference("screen_color_mode_adaptive_model_settings");
    }
    isSupportSoft = this.mContext.getPackageManager().hasSystemFeature("oem.display.soft.support");
    if (!isSupportSoft) {
      removePreference("screen_color_mode_soft_settings");
    }
  }
  
  public void onPause()
  {
    super.onPause();
    getContentResolver().unregisterContentObserver(this.mScreenColorModeContentObserver);
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    return true;
  }
  
  public void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean)
  {
    this.mScreenColorModeValue = paramInt;
    if (this.mCM != null)
    {
      if (this.isSupportReadingMode) {
        this.mCM.setColorBalance(100 - this.mScreenColorModeValue);
      }
    }
    else {
      return;
    }
    this.mCM.setColorBalance(100 - this.mScreenColorModeValue + 512);
  }
  
  public void onRadioButtonClicked(RadioButtonPreference paramRadioButtonPreference)
  {
    if (paramRadioButtonPreference == null)
    {
      this.mScreenColorModeDefaultSettings.setChecked(false);
      this.mScreenColorModeBasicSettings.setChecked(false);
      this.mScreenColorModeDefinedSettings.setChecked(false);
      this.mScreenColorModeDciP3Settings.setChecked(false);
      this.mScreenColorModeAdaptiveModelSettings.setChecked(false);
      this.mScreenColorModeSoftSettings.setChecked(false);
    }
    do
    {
      return;
      if (paramRadioButtonPreference == this.mScreenColorModeDefaultSettings)
      {
        this.mScreenColorModeDefaultSettings.setChecked(true);
        this.mScreenColorModeBasicSettings.setChecked(false);
        this.mScreenColorModeDefinedSettings.setChecked(false);
        this.mScreenColorModeDciP3Settings.setChecked(false);
        this.mScreenColorModeAdaptiveModelSettings.setChecked(false);
        this.mScreenColorModeSoftSettings.setChecked(false);
        if (getScreenColorModeSettingsValue() != 1) {
          onSaveScreenColorModeSettingsValue(1);
        }
        removePreference("screen_color_mode_seekbar");
        return;
      }
      if (paramRadioButtonPreference == this.mScreenColorModeBasicSettings)
      {
        this.mScreenColorModeDefaultSettings.setChecked(false);
        this.mScreenColorModeBasicSettings.setChecked(true);
        this.mScreenColorModeDefinedSettings.setChecked(false);
        this.mScreenColorModeDciP3Settings.setChecked(false);
        this.mScreenColorModeAdaptiveModelSettings.setChecked(false);
        this.mScreenColorModeSoftSettings.setChecked(false);
        if (getScreenColorModeSettingsValue() != 2) {
          onSaveScreenColorModeSettingsValue(2);
        }
        removePreference("screen_color_mode_seekbar");
        return;
      }
      if (paramRadioButtonPreference == this.mScreenColorModeDefinedSettings)
      {
        this.mScreenColorModeDefaultSettings.setChecked(false);
        this.mScreenColorModeBasicSettings.setChecked(false);
        this.mScreenColorModeDefinedSettings.setChecked(true);
        this.mScreenColorModeDciP3Settings.setChecked(false);
        this.mScreenColorModeAdaptiveModelSettings.setChecked(false);
        this.mScreenColorModeSoftSettings.setChecked(false);
        if (getScreenColorModeSettingsValue() != 3) {
          onSaveScreenColorModeSettingsValue(3);
        }
        getPreferenceScreen().addPreference(this.mSeekBarpreference);
        return;
      }
      if (paramRadioButtonPreference == this.mScreenColorModeDciP3Settings)
      {
        this.mScreenColorModeDefaultSettings.setChecked(false);
        this.mScreenColorModeBasicSettings.setChecked(false);
        this.mScreenColorModeDefinedSettings.setChecked(false);
        this.mScreenColorModeDciP3Settings.setChecked(true);
        this.mScreenColorModeAdaptiveModelSettings.setChecked(false);
        this.mScreenColorModeSoftSettings.setChecked(false);
        if (getScreenColorModeSettingsValue() != 4) {
          onSaveScreenColorModeSettingsValue(4);
        }
        removePreference("screen_color_mode_seekbar");
        return;
      }
      if (paramRadioButtonPreference == this.mScreenColorModeAdaptiveModelSettings)
      {
        this.mScreenColorModeDefaultSettings.setChecked(false);
        this.mScreenColorModeBasicSettings.setChecked(false);
        this.mScreenColorModeDefinedSettings.setChecked(false);
        this.mScreenColorModeDciP3Settings.setChecked(false);
        this.mScreenColorModeAdaptiveModelSettings.setChecked(true);
        this.mScreenColorModeSoftSettings.setChecked(false);
        if (getScreenColorModeSettingsValue() != 5) {
          onSaveScreenColorModeSettingsValue(5);
        }
        removePreference("screen_color_mode_seekbar");
        return;
      }
    } while (paramRadioButtonPreference != this.mScreenColorModeSoftSettings);
    this.mScreenColorModeDefaultSettings.setChecked(false);
    this.mScreenColorModeBasicSettings.setChecked(false);
    this.mScreenColorModeDefinedSettings.setChecked(false);
    this.mScreenColorModeDciP3Settings.setChecked(false);
    this.mScreenColorModeAdaptiveModelSettings.setChecked(false);
    this.mScreenColorModeSoftSettings.setChecked(true);
    if (getScreenColorModeSettingsValue() != 6) {
      onSaveScreenColorModeSettingsValue(6);
    }
    removePreference("screen_color_mode_seekbar");
  }
  
  public void onResume()
  {
    super.onResume();
    updateRadioButtons(getScreenColorModeSettingsValue());
    getContentResolver().registerContentObserver(Settings.Secure.getUriFor("night_display_activated"), true, this.mScreenColorModeContentObserver, -1);
    getContentResolver().registerContentObserver(Settings.System.getUriFor("reading_mode_status_manual"), true, this.mScreenColorModeContentObserver, -1);
  }
  
  public void onSaveScreenColorModeSettingsValue(int paramInt)
  {
    Settings.System.putInt(getContentResolver(), "screen_color_mode_settings_value", paramInt);
  }
  
  public void onSaveScreenColorModeValue(int paramInt)
  {
    Settings.System.putInt(getContentResolver(), "oem_screen_better_value", paramInt);
  }
  
  public void onStartTrackingTouch(SeekBar paramSeekBar) {}
  
  public void onStopTrackingTouch(SeekBar paramSeekBar)
  {
    onSaveScreenColorModeValue(this.mScreenColorModeValue);
    this.mCM.saveScreenBetter();
  }
  
  /* Error */
  public String readFile(String paramString)
  {
    // Byte code:
    //   0: ldc_w 320
    //   3: astore 4
    //   5: aconst_null
    //   6: astore_2
    //   7: aconst_null
    //   8: astore_3
    //   9: new 322	java/io/BufferedReader
    //   12: dup
    //   13: new 324	java/io/FileReader
    //   16: dup
    //   17: aload_1
    //   18: invokespecial 326	java/io/FileReader:<init>	(Ljava/lang/String;)V
    //   21: invokespecial 329	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   24: astore_1
    //   25: aload_1
    //   26: invokevirtual 333	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   29: astore_2
    //   30: aload_1
    //   31: ifnull +7 -> 38
    //   34: aload_1
    //   35: invokevirtual 336	java/io/BufferedReader:close	()V
    //   38: aload_2
    //   39: areturn
    //   40: astore_1
    //   41: aload_1
    //   42: invokevirtual 339	java/io/IOException:printStackTrace	()V
    //   45: goto -7 -> 38
    //   48: astore_2
    //   49: aload_3
    //   50: astore_1
    //   51: aload_2
    //   52: astore_3
    //   53: aload_1
    //   54: astore_2
    //   55: aload_3
    //   56: invokevirtual 339	java/io/IOException:printStackTrace	()V
    //   59: aload 4
    //   61: astore_2
    //   62: aload_1
    //   63: ifnull -25 -> 38
    //   66: aload_1
    //   67: invokevirtual 336	java/io/BufferedReader:close	()V
    //   70: ldc_w 320
    //   73: areturn
    //   74: astore_1
    //   75: aload_1
    //   76: invokevirtual 339	java/io/IOException:printStackTrace	()V
    //   79: ldc_w 320
    //   82: areturn
    //   83: astore_1
    //   84: aload_2
    //   85: ifnull +7 -> 92
    //   88: aload_2
    //   89: invokevirtual 336	java/io/BufferedReader:close	()V
    //   92: aload_1
    //   93: athrow
    //   94: astore_2
    //   95: aload_2
    //   96: invokevirtual 339	java/io/IOException:printStackTrace	()V
    //   99: goto -7 -> 92
    //   102: astore_3
    //   103: aload_1
    //   104: astore_2
    //   105: aload_3
    //   106: astore_1
    //   107: goto -23 -> 84
    //   110: astore_3
    //   111: goto -58 -> 53
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	114	0	this	OPScreenColorMode
    //   0	114	1	paramString	String
    //   6	33	2	str1	String
    //   48	4	2	localIOException1	java.io.IOException
    //   54	35	2	str2	String
    //   94	2	2	localIOException2	java.io.IOException
    //   104	1	2	str3	String
    //   8	48	3	localIOException3	java.io.IOException
    //   102	4	3	localObject	Object
    //   110	1	3	localIOException4	java.io.IOException
    //   3	57	4	str4	String
    // Exception table:
    //   from	to	target	type
    //   34	38	40	java/io/IOException
    //   9	25	48	java/io/IOException
    //   66	70	74	java/io/IOException
    //   9	25	83	finally
    //   55	59	83	finally
    //   88	92	94	java/io/IOException
    //   25	30	102	finally
    //   25	30	110	java/io/IOException
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\better\OPScreenColorMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */