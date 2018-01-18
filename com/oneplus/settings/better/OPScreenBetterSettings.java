package com.oneplus.settings.better;

import android.content.Context;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.net.LocalSocketAddress.Namespace;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.System;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.ui.RadioButtonPreference;
import com.android.settings.ui.RadioButtonPreference.OnClickListener;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class OPScreenBetterSettings
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceClickListener, RadioButtonPreference.OnClickListener
{
  private static final String KEY_COOL_SETTINGS = "cool_settings";
  private static final String KEY_NORMAL_SETTINGS = "nomal_settings";
  private static final String KEY_WARM_SETTINGS = "warm_settings";
  private static final String TAG = "OPScreenBetterSettings";
  private static final String TYPE_ONE = "oem:qdcm:mode_1";
  private static final String TYPE_SERVER = "pps";
  private static final String TYPE_THREE = "oem:qdcm:mode_3";
  private static final String TYPE_TWO = "oem:qdcm:mode_2";
  private String M_TYPE_STRING = "oem:qdcm:mode_1";
  private int TYPE_SETTINGS_ID = 1;
  private LocalSocket localSocket;
  private int mBetterStatus = 1;
  private Context mContext;
  private RadioButtonPreference mCoolSettings;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      super.handleMessage(paramAnonymousMessage);
      switch (paramAnonymousMessage.what)
      {
      default: 
        OPScreenBetterSettings.-set0(OPScreenBetterSettings.this, "oem:qdcm:mode_1");
      }
      for (;;)
      {
        OPScreenBetterSettings.-set2(OPScreenBetterSettings.this, paramAnonymousMessage.what);
        try
        {
          int i;
          if (Settings.System.getInt(OPScreenBetterSettings.-get3(OPScreenBetterSettings.this).getContentResolver(), "oem_eyecare_enable", 0) == 0)
          {
            OPScreenBetterSettings.-set1(OPScreenBetterSettings.this, new LocalSocket());
            OPScreenBetterSettings.-get1(OPScreenBetterSettings.this).connect(new LocalSocketAddress("pps", LocalSocketAddress.Namespace.RESERVED));
            paramAnonymousMessage = OPScreenBetterSettings.-get1(OPScreenBetterSettings.this).getOutputStream();
            i = 0;
          }
          for (;;)
          {
            Object localObject;
            if (i < 3)
            {
              paramAnonymousMessage.write(OPScreenBetterSettings.-get0(OPScreenBetterSettings.this).getBytes());
              OPScreenBetterSettings.-get1(OPScreenBetterSettings.this).setReceiveBufferSize(1024);
              localObject = OPScreenBetterSettings.-get1(OPScreenBetterSettings.this).getInputStream();
              byte[] arrayOfByte = new byte['Ѐ'];
              ((InputStream)localObject).read(arrayOfByte);
              localObject = new String(arrayOfByte);
              if (((String)localObject).contains("Success")) {
                Log.i("OPScreenBetterSettings", "succ buffer : " + ((String)localObject).substring(0, 8));
              }
            }
            else
            {
              paramAnonymousMessage.close();
              Settings.System.putInt(OPScreenBetterSettings.-get3(OPScreenBetterSettings.this).getContentResolver(), "oem_better_status", OPScreenBetterSettings.-get2(OPScreenBetterSettings.this));
              Log.i("OPScreenBetterSettings", "M_TYPE_STRING : " + OPScreenBetterSettings.-get0(OPScreenBetterSettings.this));
              return;
              OPScreenBetterSettings.-set0(OPScreenBetterSettings.this, "oem:qdcm:mode_1");
              break;
              OPScreenBetterSettings.-set0(OPScreenBetterSettings.this, "oem:qdcm:mode_2");
              break;
              OPScreenBetterSettings.-set0(OPScreenBetterSettings.this, "oem:qdcm:mode_3");
              break;
            }
            Log.i("OPScreenBetterSettings", "fail buffer : " + ((String)localObject).substring(0, 8) + " i = " + i);
            paramAnonymousMessage.flush();
            i += 1;
          }
          return;
        }
        catch (Exception paramAnonymousMessage)
        {
          paramAnonymousMessage.printStackTrace();
          Log.i("OPScreenBetterSettings", "socket exception !");
        }
      }
    }
  };
  private RadioButtonPreference mNormalSettings;
  private RadioButtonPreference mWarmSettings;
  
  private void updateRadioButtons(int paramInt)
  {
    if (paramInt == 1)
    {
      this.mNormalSettings.setChecked(true);
      this.mWarmSettings.setChecked(false);
      this.mCoolSettings.setChecked(false);
    }
    do
    {
      return;
      if (paramInt == 2)
      {
        this.mNormalSettings.setChecked(false);
        this.mWarmSettings.setChecked(true);
        this.mCoolSettings.setChecked(false);
        return;
      }
    } while (paramInt != 3);
    this.mNormalSettings.setChecked(false);
    this.mWarmSettings.setChecked(false);
    this.mCoolSettings.setChecked(true);
  }
  
  private void updateRadioButtons(RadioButtonPreference paramRadioButtonPreference)
  {
    System.out.println("url : updateLocation mode : 2-");
    if (paramRadioButtonPreference == null)
    {
      this.mNormalSettings.setChecked(false);
      this.mWarmSettings.setChecked(false);
      this.mCoolSettings.setChecked(false);
    }
    do
    {
      return;
      if (paramRadioButtonPreference == this.mNormalSettings)
      {
        this.mNormalSettings.setChecked(true);
        this.mWarmSettings.setChecked(false);
        this.mCoolSettings.setChecked(false);
        return;
      }
      if (paramRadioButtonPreference == this.mWarmSettings)
      {
        this.mNormalSettings.setChecked(false);
        this.mWarmSettings.setChecked(true);
        this.mCoolSettings.setChecked(false);
        return;
      }
    } while (paramRadioButtonPreference != this.mCoolSettings);
    this.mNormalSettings.setChecked(false);
    this.mWarmSettings.setChecked(false);
    this.mCoolSettings.setChecked(true);
  }
  
  protected int getMetricsCategory()
  {
    return 0;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230814);
    this.mContext = getActivity();
    this.mNormalSettings = ((RadioButtonPreference)findPreference("nomal_settings"));
    this.mWarmSettings = ((RadioButtonPreference)findPreference("warm_settings"));
    this.mCoolSettings = ((RadioButtonPreference)findPreference("cool_settings"));
    this.mNormalSettings.setOnClickListener(this);
    this.mWarmSettings.setOnClickListener(this);
    this.mCoolSettings.setOnClickListener(this);
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    return true;
  }
  
  public void onRadioButtonClicked(RadioButtonPreference paramRadioButtonPreference)
  {
    if (paramRadioButtonPreference == this.mNormalSettings) {
      this.TYPE_SETTINGS_ID = 1;
    }
    for (;;)
    {
      Log.i("OPScreenBetterSettings", "TYPE_SETTINGS_ID : " + this.TYPE_SETTINGS_ID);
      this.mHandler.sendEmptyMessage(this.TYPE_SETTINGS_ID);
      updateRadioButtons(paramRadioButtonPreference);
      return;
      if (paramRadioButtonPreference == this.mWarmSettings) {
        this.TYPE_SETTINGS_ID = 2;
      } else if (paramRadioButtonPreference == this.mCoolSettings) {
        this.TYPE_SETTINGS_ID = 3;
      }
    }
  }
  
  public void onResume()
  {
    super.onResume();
    this.mBetterStatus = Settings.System.getInt(this.mContext.getContentResolver(), "oem_better_status", 1);
    updateRadioButtons(this.mBetterStatus);
    this.mHandler.sendEmptyMessage(this.mBetterStatus);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\better\OPScreenBetterSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */