package com.android.settings.notification;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.SearchIndexableResource;
import android.telephony.TelephonyManager;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OtherSoundSettings
  extends SettingsPreferenceFragment
  implements Indexable
{
  private static final int DEFAULT_DOCK_AUDIO_MEDIA = 0;
  private static final int DEFAULT_EMERGENCY_TONE = 0;
  private static final int DEFAULT_ON = 1;
  private static final int DOCK_AUDIO_MEDIA_DISABLED = 0;
  private static final int DOCK_AUDIO_MEDIA_ENABLED = 1;
  private static final int EMERGENCY_TONE_ALERT = 1;
  private static final int EMERGENCY_TONE_SILENT = 0;
  private static final int EMERGENCY_TONE_VIBRATE = 2;
  private static final String KEY_CHARGING_SOUNDS = "charging_sounds";
  private static final String KEY_DIAL_PAD_TONES = "dial_pad_tones";
  private static final String KEY_DOCKING_SOUNDS = "docking_sounds";
  private static final String KEY_DOCK_AUDIO_MEDIA = "dock_audio_media";
  private static final String KEY_EMERGENCY_TONE = "emergency_tone";
  private static final String KEY_SCREEN_LOCKING_SOUNDS = "screen_locking_sounds";
  private static final String KEY_TOUCH_SOUNDS = "touch_sounds";
  private static final String KEY_VIBRATE_ON_TOUCH = "vibrate_on_touch";
  private static final SettingPref[] PREFS = { PREF_DIAL_PAD_TONES, PREF_SCREEN_LOCKING_SOUNDS, PREF_CHARGING_SOUNDS, PREF_DOCKING_SOUNDS, PREF_TOUCH_SOUNDS, PREF_VIBRATE_ON_TOUCH, PREF_DOCK_AUDIO_MEDIA, PREF_EMERGENCY_TONE };
  private static final SettingPref PREF_CHARGING_SOUNDS;
  private static final SettingPref PREF_DIAL_PAD_TONES = new SettingPref(2, "dial_pad_tones", "dtmf_tone", 1, new int[0])
  {
    public boolean isApplicable(Context paramAnonymousContext)
    {
      return Utils.isVoiceCapable(paramAnonymousContext);
    }
  };
  private static final SettingPref PREF_DOCKING_SOUNDS;
  private static final SettingPref PREF_DOCK_AUDIO_MEDIA;
  private static final SettingPref PREF_EMERGENCY_TONE;
  private static final SettingPref PREF_SCREEN_LOCKING_SOUNDS = new SettingPref(2, "screen_locking_sounds", "lockscreen_sounds_enabled", 1, new int[0]);
  private static final SettingPref PREF_TOUCH_SOUNDS;
  private static final SettingPref PREF_VIBRATE_ON_TOUCH;
  public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<String> getNonIndexableKeys(Context paramAnonymousContext)
    {
      ArrayList localArrayList = new ArrayList();
      SettingPref[] arrayOfSettingPref = OtherSoundSettings.-get0();
      int i = 0;
      int j = arrayOfSettingPref.length;
      while (i < j)
      {
        SettingPref localSettingPref = arrayOfSettingPref[i];
        if (!localSettingPref.isApplicable(paramAnonymousContext)) {
          localArrayList.add(localSettingPref.getKey());
        }
        i += 1;
      }
      return localArrayList;
    }
    
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      paramAnonymousContext = new SearchIndexableResource(paramAnonymousContext);
      paramAnonymousContext.xmlResId = 2131230822;
      return Arrays.asList(new SearchIndexableResource[] { paramAnonymousContext });
    }
  };
  private static final String TAG = "OtherSoundSettings";
  private Context mContext;
  private final SettingsObserver mSettingsObserver = new SettingsObserver();
  
  static
  {
    PREF_CHARGING_SOUNDS = new SettingPref(1, "charging_sounds", "charging_sounds_enabled", 1, new int[0]);
    PREF_DOCKING_SOUNDS = new SettingPref(1, "docking_sounds", "dock_sounds_enabled", 1, new int[0])
    {
      public boolean isApplicable(Context paramAnonymousContext)
      {
        return OtherSoundSettings.-wrap1(paramAnonymousContext);
      }
    };
    PREF_TOUCH_SOUNDS = new SettingPref(2, "touch_sounds", "sound_effects_enabled", 1, new int[0])
    {
      protected boolean setSetting(final Context paramAnonymousContext, final int paramAnonymousInt)
      {
        AsyncTask.execute(new Runnable()
        {
          public void run()
          {
            AudioManager localAudioManager = (AudioManager)paramAnonymousContext.getSystemService("audio");
            if (paramAnonymousInt != 0)
            {
              localAudioManager.loadSoundEffects();
              return;
            }
            localAudioManager.unloadSoundEffects();
          }
        });
        return super.setSetting(paramAnonymousContext, paramAnonymousInt);
      }
    };
    PREF_VIBRATE_ON_TOUCH = new SettingPref(2, "vibrate_on_touch", "haptic_feedback_enabled", 1, new int[0])
    {
      public boolean isApplicable(Context paramAnonymousContext)
      {
        return OtherSoundSettings.-wrap2(paramAnonymousContext);
      }
    };
    PREF_DOCK_AUDIO_MEDIA = new SettingPref(1, "dock_audio_media", "dock_audio_media_enabled", 0, new int[] { 0, 1 })
    {
      protected String getCaption(Resources paramAnonymousResources, int paramAnonymousInt)
      {
        switch (paramAnonymousInt)
        {
        default: 
          throw new IllegalArgumentException();
        case 0: 
          return paramAnonymousResources.getString(2131693191);
        }
        return paramAnonymousResources.getString(2131693192);
      }
      
      public boolean isApplicable(Context paramAnonymousContext)
      {
        return OtherSoundSettings.-wrap1(paramAnonymousContext);
      }
    };
    PREF_EMERGENCY_TONE = new SettingPref(1, "emergency_tone", "emergency_tone", 0, new int[] { 1, 2, 0 })
    {
      protected String getCaption(Resources paramAnonymousResources, int paramAnonymousInt)
      {
        switch (paramAnonymousInt)
        {
        default: 
          throw new IllegalArgumentException();
        case 0: 
          return paramAnonymousResources.getString(2131693193);
        case 1: 
          return paramAnonymousResources.getString(2131693194);
        }
        return paramAnonymousResources.getString(2131693195);
      }
      
      public boolean isApplicable(Context paramAnonymousContext)
      {
        return TelephonyManager.getDefault().getCurrentPhoneType() == 2;
      }
    };
  }
  
  private static boolean hasDockSettings(Context paramContext)
  {
    return paramContext.getResources().getBoolean(2131558411);
  }
  
  private static boolean hasHaptic(Context paramContext)
  {
    paramContext = (Vibrator)paramContext.getSystemService("vibrator");
    if (paramContext != null) {
      return paramContext.hasVibrator();
    }
    return false;
  }
  
  protected int getHelpResource()
  {
    return 2131693004;
  }
  
  protected int getMetricsCategory()
  {
    return 73;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230822);
    this.mContext = getActivity();
    paramBundle = PREFS;
    int i = 0;
    int j = paramBundle.length;
    while (i < j)
    {
      paramBundle[i].init(this);
      i += 1;
    }
  }
  
  public void onPause()
  {
    super.onPause();
    this.mSettingsObserver.register(false);
  }
  
  public void onResume()
  {
    super.onResume();
    this.mSettingsObserver.register(true);
  }
  
  private final class SettingsObserver
    extends ContentObserver
  {
    public SettingsObserver()
    {
      super();
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      super.onChange(paramBoolean, paramUri);
      SettingPref[] arrayOfSettingPref = OtherSoundSettings.-get0();
      int i = 0;
      int j = arrayOfSettingPref.length;
      while (i < j)
      {
        SettingPref localSettingPref = arrayOfSettingPref[i];
        if (localSettingPref.getUri().equals(paramUri))
        {
          localSettingPref.update(OtherSoundSettings.-get1(OtherSoundSettings.this));
          return;
        }
        i += 1;
      }
    }
    
    public void register(boolean paramBoolean)
    {
      ContentResolver localContentResolver = OtherSoundSettings.-wrap0(OtherSoundSettings.this);
      if (paramBoolean)
      {
        SettingPref[] arrayOfSettingPref = OtherSoundSettings.-get0();
        int j = arrayOfSettingPref.length;
        int i = 0;
        while (i < j)
        {
          localContentResolver.registerContentObserver(arrayOfSettingPref[i].getUri(), false, this);
          i += 1;
        }
      }
      localContentResolver.unregisterContentObserver(this);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\OtherSoundSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */