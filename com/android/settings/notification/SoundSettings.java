package com.android.settings.notification;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.Vibrator;
import android.provider.SearchIndexableResource;
import android.provider.Settings.System;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.TwoStatePreference;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.RingtonePreference;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.dashboard.SummaryLoader.SummaryProvider;
import com.android.settings.dashboard.SummaryLoader.SummaryProviderFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.android.settingslib.RestrictedPreference;
import com.oem.os.ThreeKeyManager;
import com.oneplus.settings.notification.OPSeekBarVolumizer;
import com.oneplus.settings.ringtone.OPRingtoneManager;
import com.oneplus.settings.ui.OPListDialog;
import com.oneplus.settings.ui.OPListDialog.OnDialogListItemClickListener;
import com.oneplus.settings.ui.OPPreferenceDivider;
import com.oneplus.settings.utils.OPUtils;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class SoundSettings
  extends SettingsPreferenceFragment
  implements OPListDialog.OnDialogListItemClickListener, Preference.OnPreferenceChangeListener, Indexable
{
  private static final int DEFAULT_ON = 0;
  private static final String KEY_ALARM_RINGTONE = "alarm_ringtone";
  private static final String KEY_ALARM_VOLUME = "alarm_volume";
  private static final String KEY_CELL_BROADCAST_SETTINGS = "cell_broadcast_settings";
  private static final String KEY_CHARGING_SOUNDS = "charging_sounds";
  private static final String KEY_DIAL_PAD_TONES = "dial_pad_tones";
  private static final String KEY_INCOMING_CALL_VIBRATE = "incoming_call_vibrate_mode";
  private static final String KEY_MEDIA_VOLUME = "media_volume";
  private static final String KEY_MMS_RINGTONE = "message_ringtone";
  private static final String KEY_NOTIFICATION_RINGTONE = "notification_ringtone";
  private static final String KEY_NOTIFICATION_VOLUME = "notification_volume";
  private static final String KEY_PHONE_RINGTONE = "ringtone";
  private static final String KEY_RING_VOLUME = "ring_volume";
  private static final String KEY_SCREENSHOT_SOUNDS = "screenshot_sounds";
  private static final String KEY_SCREEN_LOCKING_SOUNDS = "screen_locking_sounds";
  private static final String KEY_SOUND = "sound";
  private static final String KEY_SOUND_DIRECT = "sound_direct";
  private static final String KEY_SYSTEM = "system";
  private static final String KEY_TOUCH_SOUNDS = "touch_sounds";
  private static final String KEY_VIBRATE = "vibrate";
  private static final String KEY_VIBRATE_INTENSITY = "vibrate_intensity";
  private static final String KEY_VIBRATE_ON_TOUCH = "vibrate_on_touch";
  private static final String KEY_VIBRATE_ON_TOUCH_FOR_VIBRATE = "vibrate_on_touch_for_vibrate";
  private static final String KEY_VIBRATE_WHEN_RINGING = "vibrate_when_ringing";
  private static final String KEY_VIBRATE_WHEN_RINGING_FOR_VIBRATE = "vibrate_when_ringing_for_vibrate";
  private static final String KEY_WIFI_DISPLAY = "wifi_display";
  private static final String KEY_ZEN_MODE = "zen_mode";
  private static final SettingPref[] PREFS;
  private static final SettingPref PREF_CHARGING_SOUNDS;
  private static final SettingPref PREF_DIAL_PAD_TONES;
  private static final SettingPref PREF_SCREENSHOT_SOUNDS;
  private static final SettingPref PREF_SCREEN_LOCKING_SOUNDS;
  private static final SettingPref PREF_TOUCH_SOUNDS;
  private static final SettingPref PREF_VIBRATE_ON_TOUCH;
  private static final SettingPref PREF_VIBRATE_ON_TOUCH_FOR_VIBRATE;
  private static final int REQUEST_CODE = 200;
  private static final String[] RESTRICTED_KEYS = { "media_volume", "alarm_volume", "ring_volume", "notification_volume", "zen_mode" };
  private static final int SAMPLE_CUTOFF = 2000;
  public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<String> getNonIndexableKeys(Context paramAnonymousContext)
    {
      ArrayList localArrayList = new ArrayList();
      if (Utils.isVoiceCapable(paramAnonymousContext)) {
        localArrayList.add("notification_volume");
      }
      boolean bool1;
      for (;;)
      {
        PackageManager localPackageManager = paramAnonymousContext.getPackageManager();
        UserManager localUserManager = (UserManager)paramAnonymousContext.getSystemService("user");
        boolean bool2 = paramAnonymousContext.getResources().getBoolean(17956983);
        bool1 = bool2;
        if (bool2) {}
        try
        {
          int i = localPackageManager.getApplicationEnabledSetting("com.android.cellbroadcastreceiver");
          bool1 = bool2;
          if (i == 2) {
            bool1 = false;
          }
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          for (;;)
          {
            bool1 = false;
            continue;
            localArrayList.add("cell_broadcast_settings");
          }
          localArrayList.add("vibrate");
          localArrayList.add("vibrate_when_ringing_for_vibrate");
          localArrayList.add("incoming_call_vibrate_mode");
          localArrayList.add("vibrate_on_touch_for_vibrate");
          localArrayList.add("vibrate_intensity");
        }
        if ((!localUserManager.isAdminUser()) || (!bool1)) {
          break;
        }
        if (!OPUtils.isAppPakExist(paramAnonymousContext, "com.oneplus.dirac.simplemanager")) {
          localArrayList.add("sound_direct");
        }
        if ((OPUtils.isGuestMode()) || (!SoundSettings.-get0())) {
          break label175;
        }
        return localArrayList;
        localArrayList.add("ring_volume");
        localArrayList.add("ringtone");
        localArrayList.add("wifi_display");
        localArrayList.add("vibrate_when_ringing");
      }
      label175:
      return localArrayList;
    }
    
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      paramAnonymousContext = new SearchIndexableResource(paramAnonymousContext);
      paramAnonymousContext.xmlResId = 2131230861;
      return Arrays.asList(new SearchIndexableResource[] { paramAnonymousContext });
    }
  };
  private static final String SELECTED_PREFERENCE_KEY = "selected_preference";
  public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY;
  private static final String TAG = "SoundSettings";
  private static final int THREE_KEY_SILENT_VALUE = 1;
  private static boolean isSupportLinearMotor;
  private Preference mAlarmRingtonePreference;
  private AudioManager mAudioManager;
  private Context mContext;
  private final H mHandler = new H(null);
  private Preference mIncomingCallVibrateModePreference;
  private final Runnable mLookupRingtoneNames = new Runnable()
  {
    public void run()
    {
      CharSequence localCharSequence;
      if (SoundSettings.-get5(SoundSettings.this) != null)
      {
        localCharSequence = SoundSettings.-wrap2(SoundSettings.this, SoundSettings.-get2(SoundSettings.this), 1);
        if (localCharSequence != null) {
          SoundSettings.-get3(SoundSettings.this).obtainMessage(1, localCharSequence).sendToTarget();
        }
      }
      if (SoundSettings.-get4(SoundSettings.this) != null)
      {
        localCharSequence = SoundSettings.-wrap2(SoundSettings.this, SoundSettings.-get2(SoundSettings.this), 2);
        if (localCharSequence != null) {
          SoundSettings.-get3(SoundSettings.this).obtainMessage(2, localCharSequence).sendToTarget();
        }
      }
      if (SoundSettings.-get1(SoundSettings.this) != null)
      {
        localCharSequence = SoundSettings.-wrap2(SoundSettings.this, SoundSettings.-get2(SoundSettings.this), 4);
        if (localCharSequence != null) {
          SoundSettings.-get3(SoundSettings.this).obtainMessage(6, localCharSequence).sendToTarget();
        }
      }
      if (SoundSettings.-get6(SoundSettings.this) != null)
      {
        localCharSequence = SoundSettings.-wrap2(SoundSettings.this, SoundSettings.-get2(SoundSettings.this), 8);
        if (localCharSequence != null) {
          SoundSettings.-get3(SoundSettings.this).obtainMessage(7, localCharSequence).sendToTarget();
        }
      }
    }
  };
  private VolumeSeekBarPreference mMediaVolumePreference;
  private Preference mNotificationRingtonePreference;
  private OPListDialog mOPListDialog;
  private Preference mPhoneRingtonePreference;
  private PackageManager mPm;
  private final Receiver mReceiver = new Receiver(null);
  private RingtonePreference mRequestPreference;
  private VolumeSeekBarPreference mRingOrNotificationPreference;
  private int mRingerMode = -1;
  private final SettingsObserver mSettingsObserver = new SettingsObserver();
  private final BroadcastReceiver mSimStateReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.intent.action.SIM_STATE_CHANGED".equals(paramAnonymousIntent.getAction())) {
        SoundSettings.-wrap3(SoundSettings.this);
      }
    }
  };
  private Preference mSmsRingtonePreference;
  private Preference mSoundDirectPreference;
  private ComponentName mSuppressor;
  private TelephonyManager mTelephonyManager;
  private UserManager mUserManager;
  private TwoStatePreference mVibrateWhenRinging;
  private TwoStatePreference mVibrateWhenRingingForVibrate;
  private Preference mVibrateWhenRingingPreference;
  private Vibrator mVibrator;
  private boolean mVoiceCapable;
  private final VolumePreferenceCallback mVolumeCallback = new VolumePreferenceCallback(null);
  private final ArrayList<VolumeSeekBarPreference> mVolumePrefs = new ArrayList();
  private long[][] sVibratePatternrhythm = { { -2L, 0L, 1000L, 1000L, 1000L }, { -2L, 0L, 500L, 250L, 10L, 1000L, 500L, 250L, 10L }, { -2L, 0L, 300L, 400L, 300L, 400L, 300L, 1000L, 300L, 400L, 300L, 400L, 300L }, { -2L, 0L, 30L, 80L, 30L, 80L, 50L, 180L, 600L, 1000L, 30L, 80L, 30L, 80L, 50L, 180L, 600L }, { -2L, 0L, 80L, 200L, 600L, 150L, 10L, 1000L, 80L, 200L, 600L, 150L, 10L } };
  
  static
  {
    PREF_CHARGING_SOUNDS = new SettingPref(1, "charging_sounds", "charging_sounds_enabled", 0, new int[0]);
    PREF_TOUCH_SOUNDS = new SettingPref(2, "touch_sounds", "sound_effects_enabled", 0, new int[0])
    {
      protected boolean setSetting(Context paramAnonymousContext, int paramAnonymousInt)
      {
        return super.setSetting(paramAnonymousContext, paramAnonymousInt);
      }
    };
    PREF_DIAL_PAD_TONES = new SettingPref(2, "dial_pad_tones", "dtmf_tone", 0, new int[0])
    {
      public boolean isApplicable(Context paramAnonymousContext)
      {
        return Utils.isVoiceCapable(paramAnonymousContext);
      }
    };
    PREF_SCREEN_LOCKING_SOUNDS = new SettingPref(2, "screen_locking_sounds", "lockscreen_sounds_enabled", 0, new int[0]);
    PREF_SCREENSHOT_SOUNDS = new SettingPref(2, "screenshot_sounds", "oem_screenshot_sound_enable", 0, new int[0]);
    PREF_VIBRATE_ON_TOUCH = new SettingPref(2, "vibrate_on_touch", "haptic_feedback_enabled", 0, new int[0])
    {
      public boolean isApplicable(Context paramAnonymousContext)
      {
        return SoundSettings.-wrap1(paramAnonymousContext);
      }
    };
    PREF_VIBRATE_ON_TOUCH_FOR_VIBRATE = new SettingPref(2, "vibrate_on_touch_for_vibrate", "haptic_feedback_enabled", 0, new int[0])
    {
      public boolean isApplicable(Context paramAnonymousContext)
      {
        return SoundSettings.-wrap1(paramAnonymousContext);
      }
    };
    PREFS = new SettingPref[] { PREF_DIAL_PAD_TONES, PREF_SCREEN_LOCKING_SOUNDS, PREF_SCREENSHOT_SOUNDS, PREF_CHARGING_SOUNDS, PREF_TOUCH_SOUNDS, PREF_VIBRATE_ON_TOUCH, PREF_VIBRATE_ON_TOUCH_FOR_VIBRATE };
    SUMMARY_PROVIDER_FACTORY = new SummaryLoader.SummaryProviderFactory()
    {
      public SummaryLoader.SummaryProvider createSummaryProvider(Activity paramAnonymousActivity, SummaryLoader paramAnonymousSummaryLoader)
      {
        return new SoundSettings.SummaryProvider(paramAnonymousActivity, paramAnonymousSummaryLoader);
      }
    };
  }
  
  private String getSuppressorCaption(ComponentName paramComponentName)
  {
    Object localObject = this.mContext.getPackageManager();
    try
    {
      ServiceInfo localServiceInfo = ((PackageManager)localObject).getServiceInfo(paramComponentName, 0);
      if (localServiceInfo != null)
      {
        localObject = localServiceInfo.loadLabel((PackageManager)localObject);
        if (localObject != null)
        {
          localObject = ((CharSequence)localObject).toString().trim();
          int i = ((String)localObject).length();
          if (i > 0) {
            return (String)localObject;
          }
        }
      }
    }
    catch (Throwable localThrowable)
    {
      Log.w("SoundSettings", "Error loading suppressor caption", localThrowable);
    }
    return paramComponentName.getPackageName();
  }
  
  private static boolean hasHaptic(Context paramContext)
  {
    paramContext = (Vibrator)paramContext.getSystemService("vibrator");
    if (paramContext != null) {
      return paramContext.hasVibrator();
    }
    return false;
  }
  
  private void initRingtones()
  {
    this.mPhoneRingtonePreference = getPreferenceScreen().findPreference("ringtone");
    if ((this.mPhoneRingtonePreference == null) || (this.mVoiceCapable)) {}
    for (;;)
    {
      this.mNotificationRingtonePreference = getPreferenceScreen().findPreference("notification_ringtone");
      this.mAlarmRingtonePreference = getPreferenceScreen().findPreference("alarm_ringtone");
      this.mSmsRingtonePreference = getPreferenceScreen().findPreference("message_ringtone");
      return;
      getPreferenceScreen().removePreference(this.mPhoneRingtonePreference);
      this.mPhoneRingtonePreference = null;
    }
  }
  
  private void initVibrateWhenRinging()
  {
    this.mVibrateWhenRinging = ((TwoStatePreference)getPreferenceScreen().findPreference("vibrate_when_ringing"));
    if (this.mVibrateWhenRinging == null)
    {
      Log.i("SoundSettings", "Preference not found: vibrate_when_ringing");
      return;
    }
    if (!this.mVoiceCapable)
    {
      getPreferenceScreen().removePreference(this.mVibrateWhenRinging);
      this.mVibrateWhenRinging = null;
      return;
    }
    this.mVibrateWhenRinging.setPersistent(false);
    updateVibrateWhenRinging();
    this.mVibrateWhenRinging.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
    {
      public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
      {
        boolean bool = ((Boolean)paramAnonymousObject).booleanValue();
        paramAnonymousPreference = SoundSettings.-wrap0(SoundSettings.this);
        if (bool) {}
        for (int i = 1;; i = 0) {
          return Settings.System.putInt(paramAnonymousPreference, "vibrate_when_ringing", i);
        }
      }
    });
  }
  
  private void initVibrateWhenRingingForVibrate()
  {
    this.mVibrateWhenRingingForVibrate = ((TwoStatePreference)getPreferenceScreen().findPreference("vibrate_when_ringing_for_vibrate"));
    if (this.mVibrateWhenRingingForVibrate == null) {
      return;
    }
    if (!this.mVoiceCapable)
    {
      getPreferenceScreen().removePreference(this.mVibrateWhenRingingForVibrate);
      this.mVibrateWhenRingingForVibrate = null;
      return;
    }
    this.mVibrateWhenRingingForVibrate.setPersistent(false);
    updateVibrateWhenRingingForVibrate();
    this.mVibrateWhenRingingForVibrate.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
    {
      public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
      {
        boolean bool = ((Boolean)paramAnonymousObject).booleanValue();
        paramAnonymousPreference = SoundSettings.-wrap0(SoundSettings.this);
        if (bool) {}
        for (int i = 1;; i = 0) {
          return Settings.System.putInt(paramAnonymousPreference, "vibrate_when_ringing", i);
        }
      }
    });
  }
  
  private VolumeSeekBarPreference initVolumePreference(String paramString, int paramInt1, int paramInt2)
  {
    paramString = (VolumeSeekBarPreference)findPreference(paramString);
    paramString.setCallback(this.mVolumeCallback);
    paramString.setStream(paramInt1);
    this.mVolumePrefs.add(paramString);
    paramString.setMuteIcon(paramInt2);
    return paramString;
  }
  
  private void lookupRingtoneNames()
  {
    AsyncTask.execute(this.mLookupRingtoneNames);
  }
  
  private void updateEffectsSuppressor()
  {
    Object localObject = NotificationManager.from(this.mContext).getEffectsSuppressor();
    if (Objects.equals(localObject, this.mSuppressor)) {
      return;
    }
    this.mSuppressor = ((ComponentName)localObject);
    if (this.mRingOrNotificationPreference != null) {
      if (localObject == null) {
        break label75;
      }
    }
    label75:
    for (localObject = this.mContext.getString(17040860, new Object[] { getSuppressorCaption((ComponentName)localObject) });; localObject = null)
    {
      this.mRingOrNotificationPreference.setSuppressionText((String)localObject);
      updateRingOrNotificationPreference();
      return;
    }
  }
  
  private void updateRingOrNotificationPreference()
  {
    int i = Settings.System.getInt(this.mContext.getContentResolver(), "oem_vibrate_under_silent", 0);
    VolumeSeekBarPreference localVolumeSeekBarPreference = this.mRingOrNotificationPreference;
    if (isZenMuted()) {
      if (i != 0) {
        i = 17302261;
      }
    }
    for (;;)
    {
      localVolumeSeekBarPreference.showIcon(i);
      return;
      i = 17302260;
      continue;
      i = 17302259;
    }
  }
  
  private void updateRingerMode()
  {
    int i = this.mAudioManager.getRingerModeInternal();
    if (this.mRingerMode == i) {
      return;
    }
    this.mRingerMode = i;
    updateRingOrNotificationPreference();
  }
  
  private CharSequence updateRingtoneName(Context paramContext, int paramInt)
  {
    if (paramContext == null)
    {
      Log.e("SoundSettings", "Unable to update ringtone name, no context provided");
      return null;
    }
    Uri localUri;
    Object localObject1;
    if (1 == paramInt)
    {
      localUri = getDefaultPhoneRingUri(paramContext);
      localObject1 = paramContext.getString(17040362);
      if (localUri != null) {
        break label63;
      }
      localObject1 = paramContext.getString(17040360);
    }
    for (;;)
    {
      return (CharSequence)localObject1;
      localUri = RingtoneManager.getActualDefaultRingtoneUri(paramContext, paramInt);
      break;
      label63:
      Object localObject7 = null;
      Object localObject8 = null;
      Object localObject9 = null;
      Cursor localCursor = null;
      Object localObject4 = localObject7;
      Object localObject2 = localObject1;
      Object localObject5 = localObject8;
      Object localObject3 = localObject1;
      Object localObject6 = localObject9;
      try
      {
        if ("media".equals(localUri.getAuthority()))
        {
          localObject4 = localObject7;
          localObject2 = localObject1;
          localObject5 = localObject8;
          localObject3 = localObject1;
          localObject6 = localObject9;
          if (localUri.getPath().contains("internal"))
          {
            localObject4 = localObject7;
            localObject2 = localObject1;
            localObject5 = localObject8;
            localObject3 = localObject1;
            localObject6 = localObject9;
            localCursor = paramContext.getContentResolver().query(localUri, new String[] { "title" }, null, null, null);
          }
        }
        for (;;)
        {
          paramContext = (Context)localObject1;
          if (localCursor != null)
          {
            paramContext = (Context)localObject1;
            localObject4 = localCursor;
            localObject2 = localObject1;
            localObject5 = localCursor;
            localObject3 = localObject1;
            localObject6 = localCursor;
            if (localCursor.moveToFirst())
            {
              localObject4 = localCursor;
              localObject2 = localObject1;
              localObject5 = localCursor;
              localObject3 = localObject1;
              localObject6 = localCursor;
              localObject1 = OPUtils.getFileNameNoEx(localCursor.getString(0).toString());
              paramContext = (Context)localObject1;
              localObject4 = localCursor;
              localObject2 = localObject1;
              localObject5 = localCursor;
              localObject3 = localObject1;
              localObject6 = localCursor;
              if (TextUtils.isEmpty((CharSequence)localObject1))
              {
                localObject4 = localCursor;
                localObject2 = localObject1;
                localObject5 = localCursor;
                localObject3 = localObject1;
                localObject6 = localCursor;
                paramContext = localCursor.getString(localCursor.getColumnIndex("title")).toString();
              }
            }
          }
          localObject1 = paramContext;
          if (localCursor == null) {
            break;
          }
          localCursor.close();
          return paramContext;
          localObject4 = localObject7;
          localObject2 = localObject1;
          localObject5 = localObject8;
          localObject3 = localObject1;
          localObject6 = localObject9;
          localCursor = paramContext.getContentResolver().query(localUri, new String[] { "_display_name", "title" }, null, null, null);
          continue;
          localObject4 = localObject7;
          localObject2 = localObject1;
          localObject5 = localObject8;
          localObject3 = localObject1;
          localObject6 = localObject9;
          if ("content".equals(localUri.getScheme()))
          {
            localObject4 = localObject7;
            localObject2 = localObject1;
            localObject5 = localObject8;
            localObject3 = localObject1;
            localObject6 = localObject9;
            localCursor = paramContext.getContentResolver().query(localUri, new String[] { "_display_name", "title" }, null, null, null);
          }
        }
      }
      catch (IllegalArgumentException paramContext)
      {
        localObject1 = localObject2;
        return (CharSequence)localObject2;
      }
      catch (SQLiteException paramContext)
      {
        localObject1 = localObject3;
        return (CharSequence)localObject3;
      }
      finally
      {
        if (localObject6 != null) {
          ((Cursor)localObject6).close();
        }
      }
    }
  }
  
  private void updateVibratePreferenceDescription(String paramString, int paramInt)
  {
    paramString = findPreference(paramString);
    if (paramString != null) {
      paramString.setSummary(this.mContext.getResources().getStringArray(2131427481)[paramInt]);
    }
  }
  
  private void updateVibrateWhenRinging()
  {
    boolean bool = false;
    if (this.mVibrateWhenRinging == null) {
      return;
    }
    TwoStatePreference localTwoStatePreference = this.mVibrateWhenRinging;
    if (Settings.System.getInt(getContentResolver(), "vibrate_when_ringing", 0) != 0) {
      bool = true;
    }
    localTwoStatePreference.setChecked(bool);
  }
  
  private void updateVibrateWhenRingingForVibrate()
  {
    boolean bool = false;
    if (this.mVibrateWhenRingingForVibrate == null) {
      return;
    }
    TwoStatePreference localTwoStatePreference = this.mVibrateWhenRingingForVibrate;
    if (Settings.System.getInt(getContentResolver(), "vibrate_when_ringing", 0) != 0) {
      bool = true;
    }
    localTwoStatePreference.setChecked(bool);
  }
  
  private boolean wasRingerModeVibrate()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mVibrator != null)
    {
      bool1 = bool2;
      if (this.mRingerMode == 0)
      {
        bool1 = bool2;
        if (this.mAudioManager.getLastAudibleStreamVolume(2) == 0) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  public void OnDialogListCancelClick()
  {
    if (this.mVibrator != null) {
      this.mVibrator.cancel();
    }
  }
  
  public void OnDialogListConfirmClick(int paramInt)
  {
    Settings.System.putInt(getActivity().getContentResolver(), "incoming_call_vibrate_mode", paramInt);
    updateVibratePreferenceDescription("incoming_call_vibrate_mode", paramInt);
    if (this.mVibrator != null) {
      this.mVibrator.cancel();
    }
  }
  
  public void OnDialogListItemClick(int paramInt)
  {
    int i;
    if (this.mVibrator != null)
    {
      i = Settings.System.getInt(getContentResolver(), "incoming_call_vibrate_intensity", -1);
      this.mVibrator.cancel();
      if (i != 0) {
        break label56;
      }
      this.sVibratePatternrhythm[paramInt][0] = -1L;
    }
    for (;;)
    {
      this.mVibrator.vibrate(this.sVibratePatternrhythm[paramInt], -1);
      return;
      label56:
      if (i == 1) {
        this.sVibratePatternrhythm[paramInt][0] = -2L;
      } else if (i == 2) {
        this.sVibratePatternrhythm[paramInt][0] = -3L;
      }
    }
  }
  
  public Uri getDefaultPhoneRingUri(Context paramContext)
  {
    if ((OPRingtoneManager.isRingSimSwitchOn(paramContext)) && (!getSim1Enable()) && (getSim2Enable())) {
      return OPRingtoneManager.getActualRingtoneUriBySubId(paramContext, 1);
    }
    return RingtoneManager.getActualDefaultRingtoneUri(paramContext, 1);
  }
  
  protected int getMetricsCategory()
  {
    return 336;
  }
  
  public boolean getSim1Enable()
  {
    return this.mTelephonyManager.hasIccCard(0);
  }
  
  public boolean getSim2Enable()
  {
    return this.mTelephonyManager.hasIccCard(1);
  }
  
  public int getThreeKeyStatus(Context paramContext)
  {
    int i = 0;
    if (paramContext == null)
    {
      Log.e("SoundSettings", "getThreeKeyStatus error, context is null");
      return 0;
    }
    paramContext = (ThreeKeyManager)paramContext.getSystemService("threekey");
    if (paramContext != null) {}
    try
    {
      i = paramContext.getThreeKeyStatus();
      return i;
    }
    catch (Exception paramContext)
    {
      Log.e("SoundSettings", "Exception occurs, Three Key Service may not ready", paramContext);
    }
    return 0;
  }
  
  public boolean isMultiSimEnabled()
  {
    return this.mTelephonyManager.isMultiSimEnabled();
  }
  
  public boolean isZenMuted()
  {
    return getThreeKeyStatus(this.mContext) == 1;
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (this.mRequestPreference != null)
    {
      this.mRequestPreference.onActivityResult(paramInt1, paramInt2, paramIntent);
      this.mRequestPreference = null;
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mContext = getActivity();
    this.mPm = getPackageManager();
    this.mUserManager = UserManager.get(getContext());
    this.mVoiceCapable = Utils.isVoiceCapable(this.mContext);
    isSupportLinearMotor = this.mContext.getPackageManager().hasSystemFeature("oem.linear.motor.support");
    this.mTelephonyManager = ((TelephonyManager)getSystemService("phone"));
    this.mAudioManager = ((AudioManager)this.mContext.getSystemService("audio"));
    this.mVibrator = ((Vibrator)getActivity().getSystemService("vibrator"));
    PreferenceCategory localPreferenceCategory;
    if ((this.mVibrator == null) || (this.mVibrator.hasVibrator()))
    {
      addPreferencesFromResource(2131230861);
      this.mMediaVolumePreference = initVolumePreference("media_volume", 3, 17302254);
      initVolumePreference("alarm_volume", 4, 17302252);
      localPreferenceCategory = (PreferenceCategory)findPreference("sound");
      if (!this.mVoiceCapable) {
        break label423;
      }
      this.mRingOrNotificationPreference = initVolumePreference("ring_volume", 2, 17302260);
      localPreferenceCategory.removePreference(findPreference("notification_volume"));
    }
    boolean bool1;
    Object localObject;
    for (;;)
    {
      boolean bool2 = getResources().getBoolean(17956983);
      bool1 = bool2;
      if (bool2) {}
      try
      {
        i = this.mPm.getApplicationEnabledSetting("com.android.cellbroadcastreceiver");
        bool1 = bool2;
        if (i == 2) {
          bool1 = false;
        }
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        for (;;)
        {
          int i;
          int j;
          bool1 = false;
          continue;
          if (this.mSoundDirectPreference != null) {
            localIllegalArgumentException.removePreference(this.mSoundDirectPreference);
          }
        }
        this.mVibrateWhenRingingPreference = findPreference("vibrate_when_ringing");
        if (!OPUtils.isGuestMode()) {
          break label525;
        }
        if (this.mVibrateWhenRingingPreference == null) {
          break label513;
        }
        localIllegalArgumentException.removePreference(this.mVibrateWhenRingingPreference);
        localIllegalArgumentException.removePreference(findPreference("screen_locking_sounds"));
        if (paramBundle == null) {
          break label556;
        }
        paramBundle = paramBundle.getString("selected_preference", null);
        if (TextUtils.isEmpty(paramBundle)) {
          break label556;
        }
        this.mRequestPreference = ((RingtonePreference)findPreference(paramBundle));
        this.mIncomingCallVibrateModePreference = findPreference("incoming_call_vibrate_mode");
        paramBundle = (OPPreferenceDivider)findPreference("preference_divider_line_vibrate");
        updateVibratePreferenceDescription("incoming_call_vibrate_mode", Settings.System.getInt(getActivity().getContentResolver(), "incoming_call_vibrate_mode", 0));
        localObject = (PreferenceCategory)findPreference("vibrate");
        if (!isSupportLinearMotor) {
          break label685;
        }
      }
      if ((!this.mUserManager.isAdminUser()) || (!bool1) || (RestrictedLockUtils.hasBaseUserRestriction(this.mContext, "no_config_cell_broadcasts", UserHandle.myUserId()))) {
        removePreference("cell_broadcast_settings");
      }
      initRingtones();
      initVibrateWhenRinging();
      initVibrateWhenRingingForVibrate();
      updateRingerMode();
      updateEffectsSuppressor();
      localPreferenceCategory = (PreferenceCategory)findPreference("system");
      this.mSoundDirectPreference = findPreference("sound_direct");
      this.mSoundDirectPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
      {
        public boolean onPreferenceClick(Preference paramAnonymousPreference)
        {
          paramAnonymousPreference = new Intent();
          paramAnonymousPreference.setComponent(new ComponentName("com.oneplus.dirac.simplemanager", "com.oneplus.dirac.simplemanager.SimpleManager"));
          SoundSettings.this.getActivity().startActivity(paramAnonymousPreference);
          return true;
        }
      });
      if (!OPUtils.isAppPakExist(this.mContext, "com.oneplus.dirac.simplemanager")) {
        break label460;
      }
      if ((!getActivity().getPackageManager().hasSystemFeature("oem.direct.support")) && (this.mSoundDirectPreference != null)) {
        localPreferenceCategory.removePreference(this.mSoundDirectPreference);
      }
      localObject = PREFS;
      i = 0;
      j = localObject.length;
      while (i < j)
      {
        localObject[i].init(this);
        i += 1;
      }
      this.mVibrator = null;
      break;
      label423:
      this.mRingOrNotificationPreference = initVolumePreference("notification_volume", 5, 17302260);
      localPreferenceCategory.removePreference(findPreference("ring_volume"));
    }
    label460:
    label513:
    label525:
    label556:
    if (this.mVibrateWhenRingingPreference != null) {
      localIllegalArgumentException.removePreference(this.mVibrateWhenRingingPreference);
    }
    localIllegalArgumentException.removePreference(findPreference("vibrate_on_touch"));
    for (;;)
    {
      if (OPUtils.isGuestMode())
      {
        if (localObject != null) {
          getPreferenceScreen().removePreference((Preference)localObject);
        }
        if (paramBundle != null) {
          getPreferenceScreen().removePreference(paramBundle);
        }
      }
      this.mSettingsObserver.register(true);
      return;
      label685:
      if (paramBundle != null) {
        getPreferenceScreen().removePreference(paramBundle);
      }
      getPreferenceScreen().removePreference((Preference)localObject);
    }
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    this.mSettingsObserver.register(false);
  }
  
  public void onPause()
  {
    super.onPause();
    Iterator localIterator = this.mVolumePrefs.iterator();
    while (localIterator.hasNext()) {
      ((VolumeSeekBarPreference)localIterator.next()).onActivityPause();
    }
    this.mVolumeCallback.stopSample();
    this.mReceiver.register(false);
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    paramPreference = paramPreference.getKey();
    int i = Integer.parseInt((String)paramObject);
    int j;
    if (("incoming_call_vibrate_mode".equals(paramPreference)) && (this.mVibrator != null))
    {
      Settings.System.putInt(getActivity().getContentResolver(), "incoming_call_vibrate_mode", i);
      updateVibratePreferenceDescription("incoming_call_vibrate_mode", i);
      j = Settings.System.getInt(getContentResolver(), "incoming_call_vibrate_intensity", -1);
      this.mVibrator.cancel();
      if (j != 0) {
        break label102;
      }
      this.sVibratePatternrhythm[i][0] = -1L;
    }
    for (;;)
    {
      this.mVibrator.vibrate(this.sVibratePatternrhythm[i], -1);
      return true;
      label102:
      if (j == 1) {
        this.sVibratePatternrhythm[i][0] = -2L;
      } else if (j == 2) {
        this.sVibratePatternrhythm[i][0] = -3L;
      }
    }
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    if ("incoming_call_vibrate_mode".equals(paramPreference.getKey()))
    {
      String[] arrayOfString1 = this.mContext.getResources().getStringArray(2131427482);
      String[] arrayOfString2 = this.mContext.getResources().getStringArray(2131427481);
      this.mOPListDialog = new OPListDialog(this.mContext, paramPreference.getTitle(), arrayOfString1, arrayOfString2);
      this.mOPListDialog.setOnDialogListItemClickListener(this);
      this.mOPListDialog.setVibrateKey("incoming_call_vibrate_mode");
      this.mOPListDialog.show();
      return true;
    }
    if ((paramPreference instanceof RingtonePreference))
    {
      this.mRequestPreference = ((RingtonePreference)paramPreference);
      this.mRequestPreference.onPrepareRingtonePickerIntent(this.mRequestPreference.getIntent());
      if (paramPreference.getIntent() != null) {
        startActivityForResult(paramPreference.getIntent(), 200);
      }
      return true;
    }
    return super.onPreferenceTreeClick(paramPreference);
  }
  
  public void onResume()
  {
    int j = 0;
    super.onResume();
    lookupRingtoneNames();
    this.mReceiver.register(true);
    updateRingOrNotificationPreference();
    updateEffectsSuppressor();
    Object localObject = this.mVolumePrefs.iterator();
    while (((Iterator)localObject).hasNext()) {
      ((VolumeSeekBarPreference)((Iterator)localObject).next()).onActivityResume();
    }
    localObject = RestrictedLockUtils.checkIfRestrictionEnforced(this.mContext, "no_adjust_volume", UserHandle.myUserId());
    boolean bool2 = RestrictedLockUtils.hasBaseUserRestriction(this.mContext, "no_adjust_volume", UserHandle.myUserId());
    String[] arrayOfString = RESTRICTED_KEYS;
    int k = arrayOfString.length;
    int i = 0;
    if (i < k)
    {
      Preference localPreference = findPreference(arrayOfString[i]);
      boolean bool1;
      if (localPreference != null)
      {
        if (bool2)
        {
          bool1 = false;
          label132:
          localPreference.setEnabled(bool1);
        }
      }
      else {
        if (((localPreference instanceof RestrictedPreference)) && (!bool2)) {
          break label165;
        }
      }
      for (;;)
      {
        i += 1;
        break;
        bool1 = true;
        break label132;
        label165:
        ((RestrictedPreference)localPreference).setDisabledByAdmin((RestrictedLockUtils.EnforcedAdmin)localObject);
      }
    }
    localObject = (RestrictedPreference)findPreference("cell_broadcast_settings");
    if (localObject != null) {
      ((RestrictedPreference)localObject).checkRestrictionAndSetDisabled("no_config_cell_broadcasts");
    }
    updateVibrateWhenRinging();
    localObject = PREFS;
    k = localObject.length;
    i = j;
    while (i < k)
    {
      arrayOfString = localObject[i];
      if (arrayOfString != null) {
        arrayOfString.init(this);
      }
      i += 1;
    }
    i = this.mAudioManager.getStreamVolume(3);
    this.mMediaVolumePreference.setSeekbar(i);
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if (this.mRequestPreference != null) {
      paramBundle.putString("selected_preference", this.mRequestPreference.getKey());
    }
  }
  
  public void onStart()
  {
    super.onStart();
    if (isMultiSimEnabled())
    {
      IntentFilter localIntentFilter = new IntentFilter("android.intent.action.SIM_STATE_CHANGED");
      getContext().registerReceiver(this.mSimStateReceiver, localIntentFilter);
    }
  }
  
  public void onStop()
  {
    if (isMultiSimEnabled()) {
      getContext().unregisterReceiver(this.mSimStateReceiver);
    }
    super.onStop();
  }
  
  private final class H
    extends Handler
  {
    private static final int STOP_SAMPLE = 3;
    private static final int UPDATE_ALARM_RINGTONE = 6;
    private static final int UPDATE_EFFECTS_SUPPRESSOR = 4;
    private static final int UPDATE_NOTIFICATION_RINGTONE = 2;
    private static final int UPDATE_PHONE_RINGTONE = 1;
    private static final int UPDATE_RINGER_MODE = 5;
    private static final int UPDATE_SMS_RINGTONE = 7;
    
    private H()
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        SoundSettings.-get5(SoundSettings.this).setSummary((CharSequence)paramMessage.obj);
        return;
      case 2: 
        SoundSettings.-get4(SoundSettings.this).setSummary((CharSequence)paramMessage.obj);
        return;
      case 7: 
        SoundSettings.-get6(SoundSettings.this).setSummary((CharSequence)paramMessage.obj);
        return;
      case 3: 
        SoundSettings.-get7(SoundSettings.this).stopSample();
        return;
      case 4: 
        SoundSettings.-wrap4(SoundSettings.this);
        return;
      case 5: 
        SoundSettings.-wrap5(SoundSettings.this);
        return;
      }
      SoundSettings.-get1(SoundSettings.this).setSummary((CharSequence)paramMessage.obj);
    }
  }
  
  private class Receiver
    extends BroadcastReceiver
  {
    private boolean mRegistered;
    
    private Receiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      paramContext = paramIntent.getAction();
      if ("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED".equals(paramContext)) {
        SoundSettings.-get3(SoundSettings.this).sendEmptyMessage(4);
      }
      while (!"android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION".equals(paramContext)) {
        return;
      }
      SoundSettings.-get3(SoundSettings.this).sendEmptyMessage(5);
    }
    
    public void register(boolean paramBoolean)
    {
      if (this.mRegistered == paramBoolean) {
        return;
      }
      if (paramBoolean)
      {
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED");
        localIntentFilter.addAction("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION");
        SoundSettings.-get2(SoundSettings.this).registerReceiver(this, localIntentFilter);
      }
      for (;;)
      {
        this.mRegistered = paramBoolean;
        return;
        SoundSettings.-get2(SoundSettings.this).unregisterReceiver(this);
      }
    }
  }
  
  private final class SettingsObserver
    extends ContentObserver
  {
    private final Uri VIBRATE_WHEN_RINGING_URI = Settings.System.getUriFor("vibrate_when_ringing");
    
    public SettingsObserver()
    {
      super();
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      super.onChange(paramBoolean, paramUri);
      if (this.VIBRATE_WHEN_RINGING_URI.equals(paramUri))
      {
        SoundSettings.-wrap7(SoundSettings.this);
        SoundSettings.-wrap6(SoundSettings.this);
      }
    }
    
    public void register(boolean paramBoolean)
    {
      ContentResolver localContentResolver = SoundSettings.-wrap0(SoundSettings.this);
      if (paramBoolean)
      {
        localContentResolver.registerContentObserver(this.VIBRATE_WHEN_RINGING_URI, false, this);
        return;
      }
      localContentResolver.unregisterContentObserver(this);
    }
  }
  
  private static class SummaryProvider
    extends BroadcastReceiver
    implements SummaryLoader.SummaryProvider
  {
    private final AudioManager mAudioManager;
    private final Context mContext;
    private final SummaryLoader mSummaryLoader;
    private final int maxVolume;
    
    public SummaryProvider(Context paramContext, SummaryLoader paramSummaryLoader)
    {
      this.mContext = paramContext;
      this.mSummaryLoader = paramSummaryLoader;
      this.mAudioManager = ((AudioManager)this.mContext.getSystemService("audio"));
      this.maxVolume = this.mAudioManager.getStreamMaxVolume(2);
    }
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      paramContext = NumberFormat.getPercentInstance().format(this.mAudioManager.getStreamVolume(2) / this.maxVolume);
      this.mSummaryLoader.setSummary(this, this.mContext.getString(2131693171, new Object[] { paramContext }));
    }
    
    public void setListening(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
        localIntentFilter.addAction("android.media.STREAM_DEVICES_CHANGED_ACTION");
        localIntentFilter.addAction("android.media.RINGER_MODE_CHANGED");
        localIntentFilter.addAction("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION");
        localIntentFilter.addAction("android.media.STREAM_MUTE_CHANGED_ACTION");
        localIntentFilter.addAction("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED");
        this.mContext.registerReceiver(this, localIntentFilter);
        return;
      }
      this.mContext.unregisterReceiver(this);
    }
  }
  
  private final class VolumePreferenceCallback
    implements VolumeSeekBarPreference.Callback
  {
    private OPSeekBarVolumizer mCurrent;
    
    private VolumePreferenceCallback() {}
    
    public void onSampleStarting(OPSeekBarVolumizer paramOPSeekBarVolumizer)
    {
      if ((this.mCurrent != null) && (this.mCurrent != paramOPSeekBarVolumizer)) {
        this.mCurrent.stopSample();
      }
      this.mCurrent = paramOPSeekBarVolumizer;
      if (this.mCurrent != null)
      {
        SoundSettings.-get3(SoundSettings.this).removeMessages(3);
        SoundSettings.-get3(SoundSettings.this).sendEmptyMessageDelayed(3, 2000L);
      }
    }
    
    public void onStreamValueChanged(int paramInt1, int paramInt2) {}
    
    public void stopSample()
    {
      if (this.mCurrent != null) {
        this.mCurrent.stopSample();
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\SoundSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */