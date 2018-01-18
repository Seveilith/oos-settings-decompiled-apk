package com.oneplus.settings.notification;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings.System;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.text.TextUtils;
import com.android.settings.SettingsPreferenceFragment;
import com.oneplus.settings.ui.ColorPickerPreference;

public class OPLEDSettings
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener
{
  public static final String BATTERY_LIGHT_FULL_COLOR = "battery_light_full_color";
  public static final String BATTERY_LIGHT_LOW_COLOR = "battery_light_low_color";
  public static final String BATTERY_LIGHT_MEDIUM_COLOR = "battery_light_medium_color";
  private static final String COLOR_BLUE = "#FF0000FF";
  private static final String COLOR_BLUE_DRIVER = "#FF0000FF";
  private static final String COLOR_CYAN = "#FF40FFFF";
  private static final String COLOR_CYAN_DRIVER = "#FF40FFFF";
  private static final String COLOR_GREEN = "#FF40FF00";
  private static final String COLOR_GREEN_DRIVER = "#FF40FF00";
  private static final String COLOR_ORANGE = "#FFFFAE00";
  private static final String COLOR_ORANGE_DRIVER = "#FFFF4000";
  private static final String COLOR_PINK = "#FFEC407A";
  private static final String COLOR_PINK_DRIVER = "#FFFF0040";
  private static final String COLOR_PURPLE = "#FF9E00F9";
  private static final String COLOR_PURPLE_DRIVER = "#FFFF00FF";
  private static final String COLOR_RED = "#FFFF0000";
  private static final String COLOR_RED_DRIVER = "#FFFF0000";
  private static final String COLOR_YELLOW = "#FFFFFF00";
  private static final String COLOR_YELLOW_DRIVER = "#FFFFFF00";
  private static final String DEFAULT_COLOR_BATTERY_FULL = "#FF00FF00";
  private static final String DEFAULT_COLOR_BATTERY_LOW = "#FEFF0000";
  private static final String DEFAULT_COLOR_BATTERY_MEDIUM = "#FEFF0000";
  private static final String DEFAULT_COLOR_NOTIFICATION = "#FF00FF00";
  private static final String KEY_BATTERY_CHARGING = "led_settings_battery_charging";
  private static final String KEY_BATTERY_FULL = "led_settings_battery_full";
  private static final String KEY_BATTERY_LOW = "led_settings_battery_low";
  private static final String KEY_GLOABL_NOTIFICATION = "led_settings_global_notification";
  public static final String NOTIFICATION_LIGHT_PULSE_COLOR = "notification_light_pulse_color";
  private static final String TAG = "LEDSettings";
  private ColorPickerPreference mBatteryChargingPreference;
  private ColorPickerPreference mBatteryFullPreference;
  private ColorPickerPreference mBatteryLowPreference;
  private String[] mDialogColorPalette = { "#FF0000FF", "#FF40FFFF", "#FFFFAE00", "#FF40FF00", "#FFFF0000", "#FFFFFF00", "#FF9E00F9", "#FFEC407A" };
  private ColorPickerPreference mGlobalNotificationPreference;
  
  private String getDialogCode(String paramString)
  {
    String str = "";
    if (paramString.equals("#FF0000FF")) {
      str = "#FF0000FF";
    }
    do
    {
      return str;
      if (paramString.equals("#FF40FFFF")) {
        return "#FF40FFFF";
      }
      if (paramString.equals("#FFFF4000")) {
        return "#FFFFAE00";
      }
      if (paramString.equals("#FF40FF00")) {
        return "#FF40FF00";
      }
      if (paramString.equals("#FFFF0000")) {
        return "#FFFF0000";
      }
      if (paramString.equals("#FFFFFF00")) {
        return "#FFFFFF00";
      }
      if (paramString.equals("#FFFF00FF")) {
        return "#FF9E00F9";
      }
    } while (!paramString.equals("#FFFF0040"));
    return "#FFEC407A";
  }
  
  private String getDriverCode(String paramString)
  {
    String str = "";
    if (paramString.equals("#FF0000FF")) {
      str = "#FF0000FF";
    }
    do
    {
      return str;
      if (paramString.equals("#FF40FFFF")) {
        return "#FF40FFFF";
      }
      if (paramString.equals("#FFFFAE00")) {
        return "#FFFF4000";
      }
      if (paramString.equals("#FF40FF00")) {
        return "#FF40FF00";
      }
      if (paramString.equals("#FFFF0000")) {
        return "#FFFF0000";
      }
      if (paramString.equals("#FFFFFF00")) {
        return "#FFFFFF00";
      }
      if (paramString.equals("#FF9E00F9")) {
        return "#FFFF00FF";
      }
    } while (!paramString.equals("#FFEC407A"));
    return "#FFFF0040";
  }
  
  public int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230796);
    this.mGlobalNotificationPreference = ((ColorPickerPreference)findPreference("led_settings_global_notification"));
    this.mGlobalNotificationPreference.setColorPalette(this.mDialogColorPalette);
    this.mGlobalNotificationPreference.setDefaultColor("#FF00FF00");
    paramBundle = String.format("#%06X", new Object[] { Integer.valueOf(Settings.System.getInt(getActivity().getContentResolver(), "notification_light_pulse_color", Color.parseColor("#FF00FF00"))) });
    if (!TextUtils.isEmpty(paramBundle)) {
      this.mGlobalNotificationPreference.setColor(getDialogCode(paramBundle));
    }
    this.mGlobalNotificationPreference.setMessageText(2131689942);
    this.mGlobalNotificationPreference.setImageViewVisibility();
    this.mGlobalNotificationPreference.setOnPreferenceChangeListener(this);
    this.mBatteryFullPreference = ((ColorPickerPreference)findPreference("led_settings_battery_full"));
    this.mBatteryFullPreference.setColorPalette(this.mDialogColorPalette);
    this.mBatteryFullPreference.setDefaultColor("#FF00FF00");
    paramBundle = String.format("#%06X", new Object[] { Integer.valueOf(Settings.System.getInt(getActivity().getContentResolver(), "battery_light_full_color", Color.parseColor("#FF00FF00"))) });
    if (!TextUtils.isEmpty(paramBundle)) {
      this.mBatteryFullPreference.setColor(getDialogCode(paramBundle));
    }
    this.mBatteryFullPreference.setMessageText(2131689942);
    this.mBatteryFullPreference.setImageViewVisibility();
    this.mBatteryFullPreference.setOnPreferenceChangeListener(this);
    this.mBatteryChargingPreference = ((ColorPickerPreference)findPreference("led_settings_battery_charging"));
    this.mBatteryChargingPreference.setColorPalette(this.mDialogColorPalette);
    this.mBatteryChargingPreference.setDefaultColor("#FEFF0000");
    paramBundle = String.format("#%06X", new Object[] { Integer.valueOf(Settings.System.getInt(getActivity().getContentResolver(), "battery_light_medium_color", Color.parseColor("#FEFF0000"))) });
    if (!TextUtils.isEmpty(paramBundle)) {
      this.mBatteryChargingPreference.setColor(getDialogCode(paramBundle));
    }
    this.mBatteryChargingPreference.setMessageText(2131689942);
    this.mBatteryChargingPreference.setImageViewVisibility();
    this.mBatteryChargingPreference.setOnPreferenceChangeListener(this);
    this.mBatteryLowPreference = ((ColorPickerPreference)findPreference("led_settings_battery_low"));
    this.mBatteryLowPreference.setColorPalette(this.mDialogColorPalette);
    this.mBatteryLowPreference.setDefaultColor("#FEFF0000");
    paramBundle = String.format("#%06X", new Object[] { Integer.valueOf(Settings.System.getInt(getActivity().getContentResolver(), "battery_light_low_color", Color.parseColor("#FEFF0000"))) });
    if (!TextUtils.isEmpty(paramBundle)) {
      this.mBatteryLowPreference.setColor(getDialogCode(paramBundle));
    }
    this.mBatteryLowPreference.setMessageText(2131689942);
    this.mBatteryLowPreference.setImageViewVisibility();
    this.mBatteryLowPreference.setOnPreferenceChangeListener(this);
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    String str = paramPreference.getKey();
    paramPreference = getDriverCode((String)paramObject);
    int i;
    if ((paramPreference == null) || (TextUtils.isEmpty(paramPreference)))
    {
      i = 0;
      ContentResolver localContentResolver;
      if ("led_settings_global_notification".equals(str))
      {
        localContentResolver = getActivity().getContentResolver();
        if (i == 0) {
          break label179;
        }
        paramObject = paramPreference;
        label53:
        Settings.System.putInt(localContentResolver, "notification_light_pulse_color", Color.parseColor((String)paramObject));
      }
      if ("led_settings_battery_full".equals(str))
      {
        localContentResolver = getActivity().getContentResolver();
        if (i == 0) {
          break label185;
        }
        paramObject = paramPreference;
        label90:
        Settings.System.putInt(localContentResolver, "battery_light_full_color", Color.parseColor((String)paramObject));
      }
      if ("led_settings_battery_charging".equals(str))
      {
        localContentResolver = getActivity().getContentResolver();
        if (i == 0) {
          break label191;
        }
        paramObject = paramPreference;
        label127:
        Settings.System.putInt(localContentResolver, "battery_light_medium_color", Color.parseColor((String)paramObject));
      }
      if ("led_settings_battery_low".equals(str))
      {
        paramObject = getActivity().getContentResolver();
        if (i == 0) {
          break label197;
        }
      }
    }
    for (;;)
    {
      Settings.System.putInt((ContentResolver)paramObject, "battery_light_low_color", Color.parseColor(paramPreference));
      return true;
      i = 1;
      break;
      label179:
      paramObject = "#FF00FF00";
      break label53;
      label185:
      paramObject = "#FF00FF00";
      break label90;
      label191:
      paramObject = "#FEFF0000";
      break label127;
      label197:
      paramPreference = "#FEFF0000";
    }
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.mBatteryLowPreference != null)
    {
      String str = getResources().getString(2131689938).replace(" 5%", " 15%");
      this.mBatteryLowPreference.setSummary(str);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\notification\OPLEDSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */