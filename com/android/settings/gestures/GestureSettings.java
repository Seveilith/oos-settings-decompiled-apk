package com.android.settings.gestures;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings.Secure;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.hardware.AmbientDisplayConfiguration;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GestureSettings
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener, Indexable
{
  private static final String DEBUG_DOZE_COMPONENT = "debug.doze.component";
  private static final String PREF_KEY_DOUBLE_TAP_POWER = "gesture_double_tap_power";
  private static final String PREF_KEY_DOUBLE_TAP_SCREEN = "gesture_double_tap_screen";
  private static final String PREF_KEY_DOUBLE_TWIST = "gesture_double_twist";
  private static final String PREF_KEY_PICK_UP = "gesture_pick_up";
  private static final String PREF_KEY_SWIPE_DOWN_FINGERPRINT = "gesture_swipe_down_fingerprint";
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<String> getNonIndexableKeys(Context paramAnonymousContext)
    {
      ArrayList localArrayList = new ArrayList();
      AmbientDisplayConfiguration localAmbientDisplayConfiguration = new AmbientDisplayConfiguration(paramAnonymousContext);
      if (!GestureSettings.-wrap0(paramAnonymousContext.getResources())) {
        localArrayList.add("gesture_double_tap_power");
      }
      if (!localAmbientDisplayConfiguration.pulseOnPickupAvailable()) {
        localArrayList.add("gesture_pick_up");
      }
      if (!localAmbientDisplayConfiguration.pulseOnDoubleTapAvailable()) {
        localArrayList.add("gesture_double_tap_screen");
      }
      if (!GestureSettings.-wrap2(paramAnonymousContext)) {
        localArrayList.add("gesture_swipe_down_fingerprint");
      }
      if (!GestureSettings.-wrap1(paramAnonymousContext)) {
        localArrayList.add("gesture_double_twist");
      }
      return localArrayList;
    }
    
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      paramAnonymousContext = new SearchIndexableResource(paramAnonymousContext);
      paramAnonymousContext.xmlResId = 2131230767;
      localArrayList.add(paramAnonymousContext);
      return localArrayList;
    }
  };
  private static final String TAG = "GestureSettings";
  private AmbientDisplayConfiguration mAmbientConfig;
  private List<GesturePreference> mPreferences;
  
  private void addPreference(String paramString, boolean paramBoolean)
  {
    paramString = (GesturePreference)findPreference(paramString);
    paramString.setChecked(paramBoolean);
    paramString.setOnPreferenceChangeListener(this);
    this.mPreferences.add(paramString);
  }
  
  private static boolean hasSensor(Context paramContext, int paramInt1, int paramInt2)
  {
    Object localObject = paramContext.getResources();
    String str = ((Resources)localObject).getString(paramInt1);
    localObject = ((Resources)localObject).getString(paramInt2);
    if ((TextUtils.isEmpty(str)) || (TextUtils.isEmpty((CharSequence)localObject))) {}
    Sensor localSensor;
    do
    {
      while (!paramContext.hasNext())
      {
        return false;
        paramContext = ((SensorManager)paramContext.getSystemService("sensor")).getSensorList(-1).iterator();
      }
      localSensor = (Sensor)paramContext.next();
    } while ((!str.equals(localSensor.getName())) || (!((String)localObject).equals(localSensor.getVendor())));
    return true;
  }
  
  private static boolean isCameraDoubleTapPowerGestureAvailable(Resources paramResources)
  {
    return paramResources.getBoolean(17957038);
  }
  
  private static boolean isDoubleTwistAvailable(Context paramContext)
  {
    return hasSensor(paramContext, 2131689896, 2131689897);
  }
  
  private static boolean isSystemUINavigationAvailable(Context paramContext)
  {
    return paramContext.getResources().getBoolean(17957078);
  }
  
  private static boolean isSystemUINavigationEnabled(Context paramContext)
  {
    return Settings.Secure.getInt(paramContext.getContentResolver(), "system_navigation_keys_enabled", 0) == 1;
  }
  
  protected int getHelpResource()
  {
    return 2131693031;
  }
  
  protected int getMetricsCategory()
  {
    return 459;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    boolean bool2 = true;
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230767);
    paramBundle = getActivity();
    this.mPreferences = new ArrayList();
    if (isCameraDoubleTapPowerGestureAvailable(getResources())) {
      if (Settings.Secure.getInt(getContentResolver(), "camera_double_tap_power_gesture_disabled", 0) == 0)
      {
        bool1 = true;
        addPreference("gesture_double_tap_power", bool1);
        label61:
        this.mAmbientConfig = new AmbientDisplayConfiguration(paramBundle);
        if (!this.mAmbientConfig.pulseOnPickupAvailable()) {
          break label186;
        }
        addPreference("gesture_pick_up", this.mAmbientConfig.pulseOnPickupEnabled(UserHandle.myUserId()));
        label99:
        if (!this.mAmbientConfig.pulseOnDoubleTapAvailable()) {
          break label195;
        }
        addPreference("gesture_double_tap_screen", this.mAmbientConfig.pulseOnDoubleTapEnabled(UserHandle.myUserId()));
        label125:
        if (!isSystemUINavigationAvailable(paramBundle)) {
          break label204;
        }
        addPreference("gesture_swipe_down_fingerprint", isSystemUINavigationEnabled(paramBundle));
        label142:
        if (!isDoubleTwistAvailable(paramBundle)) {
          break label218;
        }
        if (Settings.Secure.getInt(getContentResolver(), "camera_double_twist_to_flip_enabled", 1) == 0) {
          break label213;
        }
      }
    }
    label186:
    label195:
    label204:
    label213:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      addPreference("gesture_double_twist", bool1);
      return;
      bool1 = false;
      break;
      removePreference("gesture_double_tap_power");
      break label61;
      removePreference("gesture_pick_up");
      break label99;
      removePreference("gesture_double_tap_screen");
      break label125;
      removePreference("gesture_swipe_down_fingerprint");
      break label142;
    }
    label218:
    removePreference("gesture_double_twist");
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
    getListView().addOnScrollListener(new RecyclerView.OnScrollListener()
    {
      public void onScrollStateChanged(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt)
      {
        if (paramAnonymousInt == 1)
        {
          paramAnonymousRecyclerView = GestureSettings.-get0(GestureSettings.this).iterator();
          while (paramAnonymousRecyclerView.hasNext()) {
            ((GesturePreference)paramAnonymousRecyclerView.next()).setScrolling(true);
          }
        }
        if (paramAnonymousInt == 0)
        {
          paramAnonymousRecyclerView = GestureSettings.-get0(GestureSettings.this).iterator();
          while (paramAnonymousRecyclerView.hasNext()) {
            ((GesturePreference)paramAnonymousRecyclerView.next()).setScrolling(false);
          }
        }
      }
      
      public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2) {}
    });
    return paramLayoutInflater;
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    int i = 0;
    boolean bool = ((Boolean)paramObject).booleanValue();
    paramPreference = paramPreference.getKey();
    if ("gesture_double_tap_power".equals(paramPreference))
    {
      paramPreference = getContentResolver();
      if (bool) {
        Settings.Secure.putInt(paramPreference, "camera_double_tap_power_gesture_disabled", i);
      }
    }
    do
    {
      return true;
      i = 1;
      break;
      if ("gesture_pick_up".equals(paramPreference))
      {
        paramPreference = getContentResolver();
        i = j;
        if (bool) {
          i = 1;
        }
        Settings.Secure.putInt(paramPreference, "doze_pulse_on_pick_up", i);
        return true;
      }
      if ("gesture_double_tap_screen".equals(paramPreference))
      {
        paramPreference = getContentResolver();
        i = k;
        if (bool) {
          i = 1;
        }
        Settings.Secure.putInt(paramPreference, "doze_pulse_on_double_tap", i);
        return true;
      }
      if ("gesture_swipe_down_fingerprint".equals(paramPreference))
      {
        paramPreference = getContentResolver();
        i = m;
        if (bool) {
          i = 1;
        }
        Settings.Secure.putInt(paramPreference, "system_navigation_keys_enabled", i);
        return true;
      }
    } while (!"gesture_double_twist".equals(paramPreference));
    paramPreference = getContentResolver();
    i = n;
    if (bool) {
      i = 1;
    }
    Settings.Secure.putInt(paramPreference, "camera_double_twist_to_flip_enabled", i);
    return true;
  }
  
  public void onStart()
  {
    super.onStart();
    Iterator localIterator = this.mPreferences.iterator();
    while (localIterator.hasNext()) {
      ((GesturePreference)localIterator.next()).onViewVisible();
    }
  }
  
  public void onStop()
  {
    super.onStop();
    Iterator localIterator = this.mPreferences.iterator();
    while (localIterator.hasNext()) {
      ((GesturePreference)localIterator.next()).onViewInvisible();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\gestures\GestureSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */