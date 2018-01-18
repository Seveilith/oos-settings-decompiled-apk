package com.android.settings.sim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.SearchIndexableResource;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.SubscriptionManager.OnSubscriptionsChangedListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.RestrictedSettingsFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import java.util.ArrayList;
import java.util.List;

public class SimSettings
  extends RestrictedSettingsFragment
  implements Indexable
{
  private static final boolean DBG = false;
  private static final String DISALLOW_CONFIG_SIM = "no_config_sim";
  public static final String EXTRA_SLOT_ID = "slot_id";
  private static final String KEY_CALLS = "sim_calls";
  private static final String KEY_CELLULAR_DATA = "sim_cellular_data";
  private static final String KEY_SMS = "sim_sms";
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      return new ArrayList();
    }
  };
  private static final String SIM_CARD_CATEGORY = "sim_cards";
  private static final String TAG = "SimSettings";
  private List<SubscriptionInfo> mAvailableSubInfos = null;
  private int[] mCallState = new int[this.mPhoneCount];
  private Context mContext;
  private int mNumSlots;
  private final SubscriptionManager.OnSubscriptionsChangedListener mOnSubscriptionsChangeListener = new SubscriptionManager.OnSubscriptionsChangedListener()
  {
    public void onSubscriptionsChanged()
    {
      SimSettings.-wrap2(SimSettings.this);
    }
  };
  private int mPhoneCount = TelephonyManager.getDefault().getPhoneCount();
  private PhoneStateListener[] mPhoneStateListener = new PhoneStateListener[this.mPhoneCount];
  private List<SubscriptionInfo> mSelectableSubInfos = null;
  private PreferenceScreen mSimCards = null;
  private List<SubscriptionInfo> mSubInfoList = null;
  private SubscriptionManager mSubscriptionManager;
  
  public SimSettings()
  {
    super("no_config_sim");
  }
  
  private String getPhoneNumber(SubscriptionInfo paramSubscriptionInfo)
  {
    return ((TelephonyManager)this.mContext.getSystemService("phone")).getLine1Number(paramSubscriptionInfo.getSubscriptionId());
  }
  
  private PhoneStateListener getPhoneStateListener(final int paramInt1, int paramInt2)
  {
    this.mPhoneStateListener[paramInt1 = new PhoneStateListener(paramInt2)
    {
      public void onCallStateChanged(int paramAnonymousInt, String paramAnonymousString)
      {
        SimSettings.-get0(SimSettings.this)[paramInt1] = paramAnonymousInt;
        SimSettings.-wrap1(SimSettings.this);
      }
    };
    return this.mPhoneStateListener[paramInt1];
  }
  
  private boolean isCallStateIdle()
  {
    boolean bool = true;
    int i = 0;
    while (i < this.mCallState.length)
    {
      if (this.mCallState[i] != 0) {
        bool = false;
      }
      i += 1;
    }
    Log.d("SimSettings", "isCallStateIdle " + bool);
    return bool;
  }
  
  private void log(String paramString)
  {
    Log.d("SimSettings", paramString);
  }
  
  private void updateActivitesCategory()
  {
    updateCellularDataValues();
    updateCallValues();
    updateSmsValues();
  }
  
  private void updateAllOptions()
  {
    updateSimSlotValues();
    updateActivitesCategory();
  }
  
  private void updateCallValues()
  {
    Preference localPreference = findPreference("sim_calls");
    Object localObject = TelecomManager.from(this.mContext);
    PhoneAccountHandle localPhoneAccountHandle = ((TelecomManager)localObject).getUserSelectedOutgoingPhoneAccount();
    List localList = ((TelecomManager)localObject).getCallCapablePhoneAccounts();
    localPreference.setTitle(2131691007);
    if (localPhoneAccountHandle == null)
    {
      localObject = this.mContext.getResources().getString(2131693113);
      localPreference.setSummary((CharSequence)localObject);
      if (localList.size() <= 1) {
        break label91;
      }
    }
    label91:
    for (boolean bool = true;; bool = false)
    {
      localPreference.setEnabled(bool);
      return;
      localObject = (String)((TelecomManager)localObject).getPhoneAccount(localPhoneAccountHandle).getLabel();
      break;
    }
  }
  
  private void updateCellularDataValues()
  {
    boolean bool3 = false;
    boolean bool2 = false;
    Preference localPreference = findPreference("sim_cellular_data");
    SubscriptionInfo localSubscriptionInfo = this.mSubscriptionManager.getDefaultDataSubscriptionInfo();
    localPreference.setTitle(2131691006);
    boolean bool4 = isCallStateIdle();
    boolean bool5 = SystemProperties.getBoolean("ril.cdma.inecmmode", false);
    if (localSubscriptionInfo != null)
    {
      localPreference.setSummary(localSubscriptionInfo.getDisplayName());
      bool1 = bool2;
      if (this.mSelectableSubInfos.size() > 1)
      {
        bool1 = bool2;
        if (bool4)
        {
          if (!bool5) {
            break label93;
          }
          bool1 = bool2;
        }
      }
      localPreference.setEnabled(bool1);
    }
    label93:
    while (localSubscriptionInfo != null) {
      for (;;)
      {
        return;
        bool1 = true;
      }
    }
    localPreference.setSummary(2131693114);
    boolean bool1 = bool3;
    if (this.mSelectableSubInfos.size() >= 1)
    {
      bool1 = bool3;
      if (bool4) {
        if (!bool5) {
          break label147;
        }
      }
    }
    label147:
    for (bool1 = bool3;; bool1 = true)
    {
      localPreference.setEnabled(bool1);
      return;
    }
  }
  
  private void updateSimSlotValues()
  {
    int j = this.mSimCards.getPreferenceCount();
    int i = 0;
    while (i < j)
    {
      Preference localPreference = this.mSimCards.getPreference(i);
      if ((localPreference instanceof SimPreference)) {
        ((SimPreference)localPreference).update();
      }
      i += 1;
    }
  }
  
  private void updateSmsValues()
  {
    boolean bool2 = true;
    boolean bool1 = true;
    Preference localPreference = findPreference("sim_sms");
    SubscriptionInfo localSubscriptionInfo = this.mSubscriptionManager.getDefaultSmsSubscriptionInfo();
    localPreference.setTitle(2131691008);
    if (localSubscriptionInfo != null)
    {
      localPreference.setSummary(localSubscriptionInfo.getDisplayName());
      if (this.mSelectableSubInfos.size() > 1) {
        localPreference.setEnabled(bool1);
      }
    }
    while (localSubscriptionInfo != null) {
      for (;;)
      {
        return;
        bool1 = false;
      }
    }
    localPreference.setSummary(2131693114);
    if (this.mSelectableSubInfos.size() >= 1) {}
    for (bool1 = bool2;; bool1 = false)
    {
      localPreference.setEnabled(bool1);
      return;
    }
  }
  
  private void updateSubscriptions()
  {
    this.mSubInfoList = this.mSubscriptionManager.getActiveSubscriptionInfoList();
    int i = 0;
    Object localObject;
    while (i < this.mNumSlots)
    {
      localObject = this.mSimCards.findPreference("sim" + i);
      if ((localObject instanceof SimPreference)) {
        this.mSimCards.removePreference((Preference)localObject);
      }
      i += 1;
    }
    this.mAvailableSubInfos.clear();
    this.mSelectableSubInfos.clear();
    i = 0;
    while (i < this.mNumSlots)
    {
      localObject = this.mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(i);
      SimPreference localSimPreference = new SimPreference(getPrefContext(), (SubscriptionInfo)localObject, i);
      localSimPreference.setOrder(i - this.mNumSlots);
      this.mSimCards.addPreference(localSimPreference);
      this.mAvailableSubInfos.add(localObject);
      if (localObject != null) {
        this.mSelectableSubInfos.add(localObject);
      }
      i += 1;
    }
    updateAllOptions();
  }
  
  protected int getMetricsCategory()
  {
    return 88;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mContext = getActivity();
    this.mSubscriptionManager = SubscriptionManager.from(getActivity());
    paramBundle = (TelephonyManager)getActivity().getSystemService("phone");
    addPreferencesFromResource(2131230860);
    this.mNumSlots = paramBundle.getSimCount();
    this.mSimCards = ((PreferenceScreen)findPreference("sim_cards"));
    this.mAvailableSubInfos = new ArrayList(this.mNumSlots);
    this.mSelectableSubInfos = new ArrayList();
    SimSelectNotification.cancelNotification(getActivity());
  }
  
  public void onPause()
  {
    super.onPause();
    this.mSubscriptionManager.removeOnSubscriptionsChangedListener(this.mOnSubscriptionsChangeListener);
    TelephonyManager localTelephonyManager = (TelephonyManager)getSystemService("phone");
    int i = 0;
    while (i < this.mPhoneCount)
    {
      if (this.mPhoneStateListener[i] != null)
      {
        localTelephonyManager.listen(this.mPhoneStateListener[i], 0);
        this.mPhoneStateListener[i] = null;
      }
      i += 1;
    }
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    Object localObject = this.mContext;
    Intent localIntent = new Intent((Context)localObject, SimDialogActivity.class);
    localIntent.addFlags(268435456);
    if ((paramPreference instanceof SimPreference))
    {
      localObject = new Intent((Context)localObject, SimPreferenceDialog.class);
      ((Intent)localObject).putExtra("slot_id", SimPreference.-wrap0((SimPreference)paramPreference));
      startActivity((Intent)localObject);
    }
    do
    {
      return true;
      if (findPreference("sim_cellular_data") == paramPreference)
      {
        localIntent.putExtra(SimDialogActivity.DIALOG_TYPE_KEY, 0);
        ((Context)localObject).startActivity(localIntent);
        return true;
      }
      if (findPreference("sim_calls") == paramPreference)
      {
        localIntent.putExtra(SimDialogActivity.DIALOG_TYPE_KEY, 1);
        ((Context)localObject).startActivity(localIntent);
        return true;
      }
    } while (findPreference("sim_sms") != paramPreference);
    localIntent.putExtra(SimDialogActivity.DIALOG_TYPE_KEY, 2);
    ((Context)localObject).startActivity(localIntent);
    return true;
  }
  
  public void onResume()
  {
    super.onResume();
    this.mSubscriptionManager.addOnSubscriptionsChangedListener(this.mOnSubscriptionsChangeListener);
    updateSubscriptions();
    TelephonyManager localTelephonyManager = (TelephonyManager)getActivity().getSystemService("phone");
    if (this.mSelectableSubInfos.size() > 1)
    {
      Log.d("SimSettings", "Register for call state change");
      int i = 0;
      while (i < this.mPhoneCount)
      {
        localTelephonyManager.listen(getPhoneStateListener(i, ((SubscriptionInfo)this.mSelectableSubInfos.get(i)).getSubscriptionId()), 32);
        i += 1;
      }
    }
  }
  
  private class SimPreference
    extends Preference
  {
    Context mContext;
    private int mSlotId;
    private SubscriptionInfo mSubInfoRecord;
    
    public SimPreference(Context paramContext, SubscriptionInfo paramSubscriptionInfo, int paramInt)
    {
      super();
      this.mContext = paramContext;
      this.mSubInfoRecord = paramSubscriptionInfo;
      this.mSlotId = paramInt;
      setKey("sim" + this.mSlotId);
      update();
    }
    
    private int getSlotId()
    {
      return this.mSlotId;
    }
    
    public void update()
    {
      Resources localResources = this.mContext.getResources();
      setTitle(String.format(this.mContext.getResources().getString(2131693093), new Object[] { Integer.valueOf(this.mSlotId + 1) }));
      if (this.mSubInfoRecord != null)
      {
        if (TextUtils.isEmpty(SimSettings.-wrap0(SimSettings.this, this.mSubInfoRecord))) {
          setSummary(this.mSubInfoRecord.getDisplayName());
        }
        for (;;)
        {
          setIcon(new BitmapDrawable(localResources, this.mSubInfoRecord.createIconBitmap(this.mContext)));
          return;
          setSummary(this.mSubInfoRecord.getDisplayName() + " - " + PhoneNumberUtils.createTtsSpannable(SimSettings.-wrap0(SimSettings.this, this.mSubInfoRecord)));
          setEnabled(true);
        }
      }
      setSummary(2131693090);
      setFragment(null);
      setEnabled(false);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\sim\SimSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */