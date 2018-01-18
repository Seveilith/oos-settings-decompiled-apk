package com.android.settings;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Telephony.Carriers;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.telephony.CarrierConfigManager;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import com.android.internal.telephony.PhoneConstants.DataState;
import com.android.internal.telephony.dataconnection.ApnSetting;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.internal.telephony.uicc.UiccController;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class ApnSettings
  extends RestrictedSettingsFragment
  implements Preference.OnPreferenceChangeListener
{
  public static final String APN_ID = "apn_id";
  private static final int APN_INDEX = 2;
  private static final String APN_NAME_DM = "CMCC DM";
  private static final int BEARER_BITMASK_INDEX = 8;
  private static final int BEARER_INDEX = 7;
  private static final Uri DEFAULTAPN_URI = Uri.parse("content://telephony/carriers/restore");
  private static final int DIALOG_RESTORE_DEFAULTAPN = 1001;
  private static final int EVENT_RESTORE_DEFAULTAPN_COMPLETE = 2;
  private static final int EVENT_RESTORE_DEFAULTAPN_START = 1;
  public static final String EXTRA_POSITION = "position";
  private static final int ID_INDEX = 0;
  private static final int MENU_NEW = 1;
  private static final int MENU_RESTORE = 2;
  public static final String MVNO_MATCH_DATA = "mvno_match_data";
  private static final int MVNO_MATCH_DATA_INDEX = 5;
  public static final String MVNO_TYPE = "mvno_type";
  private static final int MVNO_TYPE_INDEX = 4;
  private static final int NAME_INDEX = 1;
  private static final Uri PREFERAPN_URI = Uri.parse("content://telephony/carriers/preferapn");
  public static final String PREFERRED_APN_URI = "content://telephony/carriers/preferapn";
  public static final String RESTORE_CARRIERS_URI = "content://telephony/carriers/restore";
  private static final int RO_INDEX = 6;
  public static final String SUB_ID = "sub_id";
  static final String TAG = "ApnSettings";
  private static final int TYPES_INDEX = 3;
  private static boolean mRestoreDefaultApnMode;
  private boolean mAllowAddingApns;
  private boolean mApnSettingsHidden;
  private boolean mHideImsApn;
  private HashSet mIccidSet;
  private IntentFilter mMobileStateFilter;
  private final BroadcastReceiver mMobileStateReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (paramAnonymousIntent.getAction().equals("android.intent.action.ANY_DATA_STATE"))
      {
        paramAnonymousContext = ApnSettings.-wrap1(paramAnonymousIntent);
        switch (-getcom-android-internal-telephony-PhoneConstants$DataStateSwitchesValues()[paramAnonymousContext.ordinal()])
        {
        }
      }
      int i;
      do
      {
        do
        {
          do
          {
            return;
          } while (ApnSettings.-get2());
          ApnSettings.-wrap2(ApnSettings.this);
          return;
        } while (!paramAnonymousIntent.getAction().equals("android.intent.action.SIM_STATE_CHANGED"));
        i = paramAnonymousIntent.getIntExtra("phone", -1);
        Log.d("ApnSettings", "slotId: " + i);
        if (i == -1) {
          return;
        }
        paramAnonymousContext = paramAnonymousIntent.getStringExtra("ss");
        Log.d("ApnSettings", "simStatus: " + paramAnonymousContext);
      } while ((!"ABSENT".equals(paramAnonymousContext)) || (ApnSettings.-get3(ApnSettings.this) == null) || (ApnSettings.-get3(ApnSettings.this).getSimSlotIndex() != i));
      ApnSettings.this.finish();
    }
  };
  private String mMvnoMatchData;
  private String mMvnoType;
  private RestoreApnProcessHandler mRestoreApnProcessHandler;
  private RestoreApnUiHandler mRestoreApnUiHandler;
  private HandlerThread mRestoreDefaultApnThread;
  private String mSelectedKey;
  private SubscriptionInfo mSubscriptionInfo;
  private UiccController mUiccController;
  private boolean mUnavailable;
  private UserManager mUserManager;
  
  public ApnSettings()
  {
    super("no_config_mobile_networks");
  }
  
  private void addApnToList(ApnPreference paramApnPreference, ArrayList<ApnPreference> paramArrayList1, ArrayList<ApnPreference> paramArrayList2, IccRecords paramIccRecords, String paramString1, String paramString2)
  {
    if ((paramIccRecords == null) || (TextUtils.isEmpty(paramString1))) {}
    do
    {
      do
      {
        paramArrayList1.add(paramApnPreference);
        return;
      } while (TextUtils.isEmpty(paramString2));
    } while (!ApnSetting.mvnoMatches(paramIccRecords, paramString1, paramString2));
    paramArrayList2.add(paramApnPreference);
    this.mMvnoType = paramString1;
    this.mMvnoMatchData = paramString2;
  }
  
  private void addNewApn()
  {
    Intent localIntent = new Intent("android.intent.action.INSERT", getUri(Telephony.Carriers.CONTENT_URI));
    int i;
    if (this.mSubscriptionInfo != null)
    {
      i = this.mSubscriptionInfo.getSubscriptionId();
      localIntent.putExtra("sub_id", i);
      if ((!TextUtils.isEmpty(this.mMvnoType)) && (!TextUtils.isEmpty(this.mMvnoMatchData))) {
        break label71;
      }
    }
    for (;;)
    {
      startActivity(localIntent);
      return;
      i = -1;
      break;
      label71:
      localIntent.putExtra("mvno_type", this.mMvnoType);
      localIntent.putExtra("mvno_match_data", this.mMvnoMatchData);
    }
  }
  
  private void fillList()
  {
    int k = 0;
    Object localObject1 = (TelephonyManager)getSystemService("phone");
    Object localObject3;
    int i;
    label150:
    int m;
    label211:
    Cursor localCursor;
    PreferenceGroup localPreferenceGroup;
    ArrayList localArrayList3;
    ArrayList localArrayList1;
    ArrayList localArrayList2;
    label611:
    String str1;
    String str2;
    String str3;
    String str4;
    String str5;
    boolean bool;
    if (this.mSubscriptionInfo == null)
    {
      localObject1 = "";
      Log.d("ApnSettings", "mccmnc = " + (String)localObject1);
      localObject2 = new StringBuilder("numeric=\"" + (String)localObject1 + "\" AND NOT (type='ia' AND (apn=\"\" OR apn IS NULL)) AND user_visible!=0");
      if (SystemProperties.getBoolean("persist.sys.hideapn", true))
      {
        Log.d("ApnSettings", "hiden apn feature enable.");
        if (getResources().getBoolean(2131558439)) {
          this.mHideImsApn = true;
        }
        if (getResources().getBoolean(2131558440))
        {
          localObject3 = getResources().getStringArray(2131427449);
          i = 0;
          j = localObject3.length;
          if (i < j)
          {
            if (!localObject3[i].equals(localObject1)) {
              break label829;
            }
            ((StringBuilder)localObject2).append(" and name <>\"CMCC DM\"");
          }
        }
        if (getResources().getBoolean(2131558441))
        {
          m = 0;
          localObject3 = getResources().getStringArray(2131427450);
          i = 0;
          int n = localObject3.length;
          j = m;
          if (i < n)
          {
            if (!localObject3[i].equals(localObject1)) {
              break label836;
            }
            j = 1;
          }
          if (j != 0) {
            ((StringBuilder)localObject2).append(" and type <>\"supl\"");
          }
        }
        if (getResources().getBoolean(2131558442)) {
          ((StringBuilder)localObject2).append(" and type <>\"mms\"");
        }
      }
      if (getResources().getBoolean(2131558449))
      {
        ((StringBuilder)localObject2).append(" AND type <>\"dun\"");
        ((StringBuilder)localObject2).append(" AND type <>\"ims\"");
      }
      if (this.mHideImsApn) {
        ((StringBuilder)localObject2).append(" AND NOT (type='ims')");
      }
      if (isOperatorIccId())
      {
        ((StringBuilder)localObject2).append(" AND type <>\"emergency\"");
        ((StringBuilder)localObject2).append(" AND type <>\"ims\"");
      }
      Log.d("ApnSettings", "where---" + localObject2);
      localObject1 = getContentResolver();
      localObject3 = Telephony.Carriers.CONTENT_URI;
      localObject2 = ((StringBuilder)localObject2).toString();
      localCursor = ((ContentResolver)localObject1).query((Uri)localObject3, new String[] { "_id", "name", "apn", "type", "mvno_type", "mvno_match_data", "read_only", "bearer", "bearer_bitmask" }, (String)localObject2, null, "_id");
      if (localCursor == null) {
        return;
      }
      localObject2 = null;
      localObject1 = localObject2;
      if (this.mUiccController != null)
      {
        localObject1 = localObject2;
        if (this.mSubscriptionInfo != null) {
          localObject1 = this.mUiccController.getIccRecords(SubscriptionManager.getPhoneId(this.mSubscriptionInfo.getSubscriptionId()), 1);
        }
      }
      localPreferenceGroup = (PreferenceGroup)findPreference("apn_list");
      localPreferenceGroup.removeAll();
      localArrayList3 = new ArrayList();
      localArrayList1 = new ArrayList();
      localArrayList2 = new ArrayList();
      localObject3 = new ArrayList();
      this.mSelectedKey = getSelectedApnKey();
      Log.d("ApnSettings", "select key = " + this.mSelectedKey);
      localCursor.moveToFirst();
      i = k;
      if (localCursor.isAfterLast()) {
        break label1096;
      }
      localObject2 = localCursor.getString(1);
      str1 = localCursor.getString(2);
      str2 = localCursor.getString(0);
      str3 = localCursor.getString(3);
      str4 = localCursor.getString(4);
      str5 = localCursor.getString(5);
      if (localCursor.getInt(6) != 1) {
        break label843;
      }
      bool = true;
      label697:
      localObject4 = getLocalizedName(getActivity(), localCursor, 1);
      if (!TextUtils.isEmpty((CharSequence)localObject4)) {
        localObject2 = localObject4;
      }
      k = localCursor.getInt(7);
      m = localCursor.getInt(8);
      if (this.mSubscriptionInfo == null) {
        break label849;
      }
    }
    label829:
    label836:
    label843:
    label849:
    for (int j = this.mSubscriptionInfo.getSubscriptionId();; j = -1)
    {
      j = networkTypeToRilRidioTechnology(TelephonyManager.getDefault().getDataNetworkType(j));
      if ((ServiceState.bitmaskHasTech(k | m, j)) || ((k == 0) && (m == 0)) || ((j == 0) && ((k != 0) || (j != 0)))) {
        break label854;
      }
      localCursor.moveToNext();
      break label611;
      localObject1 = ((TelephonyManager)localObject1).getSimOperator(this.mSubscriptionInfo.getSubscriptionId());
      break;
      i += 1;
      break label150;
      i += 1;
      break label211;
      bool = false;
      break label697;
    }
    label854:
    Object localObject4 = new ApnPreference(getPrefContext());
    ((ApnPreference)localObject4).setApnReadOnly(bool);
    ((ApnPreference)localObject4).setKey(str2);
    ((ApnPreference)localObject4).setTitle((CharSequence)localObject2);
    ((ApnPreference)localObject4).setSummary(str1);
    ((ApnPreference)localObject4).setPersistent(false);
    ((ApnPreference)localObject4).setOnPreferenceChangeListener(this);
    if (str3 != null)
    {
      if ((!str3.equals("mms")) && (!str3.equals("ia"))) {
        break label1054;
      }
      bool = false;
      label937:
      ((ApnPreference)localObject4).setSelectable(bool);
      if (!bool) {
        break label1077;
      }
      j = i;
      if (this.mSelectedKey != null)
      {
        j = i;
        if (this.mSelectedKey.equals(str2))
        {
          ((ApnPreference)localObject4).setChecked();
          j = 1;
          Log.d("ApnSettings", "find select key = " + this.mSelectedKey + " apn: " + str1);
        }
      }
      addApnToList((ApnPreference)localObject4, localArrayList3, localArrayList1, (IccRecords)localObject1, str4, str5);
      i = j;
    }
    for (;;)
    {
      localCursor.moveToNext();
      break;
      bool = true;
      break label937;
      label1054:
      if (str3.equals("ims")) {}
      for (bool = false;; bool = true) {
        break;
      }
      label1077:
      addApnToList((ApnPreference)localObject4, localArrayList2, (ArrayList)localObject3, (IccRecords)localObject1, str4, str5);
    }
    label1096:
    localCursor.close();
    Object localObject2 = localArrayList3;
    localObject1 = localArrayList2;
    if (!localArrayList1.isEmpty())
    {
      localObject2 = localArrayList1;
      localObject1 = localObject3;
    }
    localObject2 = ((Iterable)localObject2).iterator();
    while (((Iterator)localObject2).hasNext()) {
      localPreferenceGroup.addPreference((ApnPreference)((Iterator)localObject2).next());
    }
    if ((i == 0) && (localPreferenceGroup.getPreferenceCount() > 0))
    {
      localObject2 = (ApnPreference)localPreferenceGroup.getPreference(0);
      ((ApnPreference)localObject2).setChecked();
      setSelectedApnKey(((ApnPreference)localObject2).getKey());
      Log.d("ApnSettings", "set key to  " + ((ApnPreference)localObject2).getKey());
    }
    localObject1 = ((Iterable)localObject1).iterator();
    while (((Iterator)localObject1).hasNext()) {
      localPreferenceGroup.addPreference((ApnPreference)((Iterator)localObject1).next());
    }
  }
  
  private void fillOperatorIccidset()
  {
    this.mIccidSet = new HashSet();
    this.mIccidSet.add("8991840");
    this.mIccidSet.add("8991854");
    this.mIccidSet.add("8991855");
    this.mIccidSet.add("8991856");
    this.mIccidSet.add("8991857");
    this.mIccidSet.add("8991858");
    this.mIccidSet.add("8991859");
    this.mIccidSet.add("899186");
    this.mIccidSet.add("8991870");
    this.mIccidSet.add("8991871");
    this.mIccidSet.add("8991872");
    this.mIccidSet.add("8991873");
    this.mIccidSet.add("8991874");
  }
  
  public static String getLocalizedName(Context paramContext, Cursor paramCursor, int paramInt)
  {
    String str = paramCursor.getString(paramInt);
    paramCursor = null;
    if ((str == null) || (str.isEmpty())) {
      return null;
    }
    paramInt = paramContext.getResources().getIdentifier(str, "string", paramContext.getPackageName());
    try
    {
      paramContext = paramContext.getResources().getString(paramInt);
      paramCursor = paramContext;
      Log.d("ApnSettings", "Replaced apn name with localized name");
      return paramContext;
    }
    catch (Resources.NotFoundException paramContext)
    {
      Log.e("ApnSettings", "Got execption while getting the localized apn name.", paramContext);
    }
    return paramCursor;
  }
  
  private static PhoneConstants.DataState getMobileDataState(Intent paramIntent)
  {
    paramIntent = paramIntent.getStringExtra("state");
    if (paramIntent != null) {
      return (PhoneConstants.DataState)Enum.valueOf(PhoneConstants.DataState.class, paramIntent);
    }
    return PhoneConstants.DataState.DISCONNECTED;
  }
  
  private String getSelectedApnKey()
  {
    String str = null;
    Cursor localCursor = getContentResolver().query(getUri(PREFERAPN_URI), new String[] { "_id" }, null, null, "name ASC");
    if (localCursor.getCount() > 0)
    {
      localCursor.moveToFirst();
      str = localCursor.getString(0);
    }
    localCursor.close();
    return str;
  }
  
  private Uri getUri(Uri paramUri)
  {
    int j = SubscriptionManager.getDefaultDataSubscriptionId();
    int i = j;
    if (this.mSubscriptionInfo != null)
    {
      i = j;
      if (SubscriptionManager.isValidSubscriptionId(this.mSubscriptionInfo.getSubscriptionId())) {
        i = this.mSubscriptionInfo.getSubscriptionId();
      }
    }
    return Uri.withAppendedPath(paramUri, "/subId/" + i);
  }
  
  private boolean isOperatorIccId()
  {
    if (this.mSubscriptionInfo == null) {}
    for (String str = "";; str = this.mSubscriptionInfo.getIccId())
    {
      Iterator localIterator = this.mIccidSet.iterator();
      do
      {
        if (!localIterator.hasNext()) {
          break;
        }
      } while (!str.contains((CharSequence)localIterator.next()));
      return true;
    }
    return false;
  }
  
  private int networkTypeToRilRidioTechnology(int paramInt)
  {
    switch (paramInt)
    {
    case 11: 
    default: 
      return 0;
    case 1: 
      return 1;
    case 2: 
      return 2;
    case 3: 
      return 3;
    case 8: 
      return 9;
    case 9: 
      return 10;
    case 10: 
      return 11;
    case 4: 
      return 5;
    case 7: 
      return 6;
    case 5: 
      return 7;
    case 6: 
      return 8;
    case 12: 
      return 12;
    case 14: 
      return 13;
    case 13: 
      return 14;
    case 15: 
      return 15;
    case 16: 
      return 16;
    case 17: 
      return 17;
    case 18: 
      return 18;
    }
    return 19;
  }
  
  private boolean restoreDefaultApn()
  {
    try
    {
      showDialog(1001);
      mRestoreDefaultApnMode = true;
      if (this.mRestoreApnUiHandler == null) {
        this.mRestoreApnUiHandler = new RestoreApnUiHandler(null);
      }
      if ((this.mRestoreApnProcessHandler == null) || (this.mRestoreDefaultApnThread == null))
      {
        this.mRestoreDefaultApnThread = new HandlerThread("Restore default APN Handler: Process Thread");
        this.mRestoreDefaultApnThread.start();
        this.mRestoreApnProcessHandler = new RestoreApnProcessHandler(this.mRestoreDefaultApnThread.getLooper(), this.mRestoreApnUiHandler);
      }
      this.mRestoreApnProcessHandler.sendEmptyMessage(1);
      return true;
    }
    catch (IllegalStateException localIllegalStateException) {}
    return true;
  }
  
  private void setSelectedApnKey(String paramString)
  {
    this.mSelectedKey = paramString;
    paramString = getContentResolver();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("apn_id", this.mSelectedKey);
    paramString.update(getUri(PREFERAPN_URI), localContentValues, null, null);
  }
  
  protected int getMetricsCategory()
  {
    return 12;
  }
  
  public RestrictedLockUtils.EnforcedAdmin getRestrictionEnforcedAdmin()
  {
    UserHandle localUserHandle = UserHandle.of(this.mUserManager.getUserHandle());
    if ((!this.mUserManager.hasUserRestriction("no_config_mobile_networks", localUserHandle)) || (this.mUserManager.hasBaseUserRestriction("no_config_mobile_networks", localUserHandle))) {
      return null;
    }
    return RestrictedLockUtils.EnforcedAdmin.MULTIPLE_ENFORCED_ADMIN;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    getEmptyTextView().setText(2131689633);
    this.mUnavailable = isUiRestricted();
    if (this.mUnavailable) {}
    for (boolean bool = false;; bool = true)
    {
      setHasOptionsMenu(bool);
      if (!this.mUnavailable) {
        break;
      }
      setPreferenceScreen(new PreferenceScreen(getPrefContext(), null));
      getPreferenceScreen().removeAll();
      return;
    }
    addPreferencesFromResource(2131230730);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    getActivity().getWindow().getDecorView().setSystemUiVisibility(0);
    super.onCreate(paramBundle);
    paramBundle = getActivity();
    int i = paramBundle.getIntent().getIntExtra("sub_id", -1);
    fillOperatorIccidset();
    Log.d("ApnSettings", "onCreate: subId = " + i);
    this.mMobileStateFilter = new IntentFilter("android.intent.action.ANY_DATA_STATE");
    this.mMobileStateFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
    setIfOnlyAvailableForAdmins(true);
    this.mSubscriptionInfo = SubscriptionManager.from(paramBundle).getActiveSubscriptionInfo(i);
    this.mUiccController = UiccController.getInstance();
    PersistableBundle localPersistableBundle = ((CarrierConfigManager)getSystemService("carrier_config")).getConfig();
    this.mHideImsApn = localPersistableBundle.getBoolean("hide_ims_apn_bool");
    this.mAllowAddingApns = localPersistableBundle.getBoolean("allow_adding_apns_bool");
    this.mUserManager = UserManager.get(paramBundle);
  }
  
  public Dialog onCreateDialog(int paramInt)
  {
    if (paramInt == 1001)
    {
      ProgressDialog local2 = new ProgressDialog(getActivity())
      {
        public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      };
      local2.setMessage(getResources().getString(2131691881));
      local2.setCancelable(false);
      return local2;
    }
    return null;
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    if (!this.mUnavailable)
    {
      if (this.mAllowAddingApns) {
        paramMenu.add(0, 1, 0, getResources().getString(2131691873)).setIcon(17301555).setShowAsAction(1);
      }
      paramMenu.add(0, 2, 0, getResources().getString(2131691882)).setIcon(17301589);
    }
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    if (this.mRestoreDefaultApnThread != null)
    {
      removeDialog(1001);
      this.mRestoreDefaultApnThread.quit();
      this.mRestoreDefaultApnThread = null;
      this.mRestoreApnProcessHandler = null;
    }
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return super.onOptionsItemSelected(paramMenuItem);
    case 1: 
      addNewApn();
      return true;
    case 2: 
      restoreDefaultApn();
      return true;
    }
    finish();
    return true;
  }
  
  public void onPause()
  {
    super.onPause();
    if (this.mRestoreDefaultApnThread != null)
    {
      removeDialog(1001);
      this.mRestoreDefaultApnThread.quit();
      this.mRestoreDefaultApnThread = null;
      this.mRestoreApnProcessHandler = null;
    }
    if (this.mUnavailable) {
      return;
    }
    getActivity().unregisterReceiver(this.mMobileStateReceiver);
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    Log.d("ApnSettings", "onPreferenceChange(): Preference - " + paramPreference + ", newValue - " + paramObject + ", newValue type - " + paramObject.getClass());
    if ((paramObject instanceof String)) {
      setSelectedApnKey((String)paramObject);
    }
    return true;
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    try
    {
      int i = Integer.parseInt(paramPreference.getKey());
      startActivity(new Intent("android.intent.action.EDIT", ContentUris.withAppendedId(getUri(Telephony.Carriers.CONTENT_URI), i)));
      return true;
    }
    catch (ActivityNotFoundException paramPreference)
    {
      for (;;)
      {
        paramPreference.printStackTrace();
      }
    }
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.mRestoreDefaultApnThread != null)
    {
      removeDialog(1001);
      this.mRestoreDefaultApnThread.quit();
      this.mRestoreDefaultApnThread = null;
      this.mRestoreApnProcessHandler = null;
    }
    if (this.mUnavailable) {
      return;
    }
    getActivity().registerReceiver(this.mMobileStateReceiver, this.mMobileStateFilter);
    if (!mRestoreDefaultApnMode) {
      fillList();
    }
    this.mApnSettingsHidden = false;
  }
  
  public void onStop()
  {
    super.onStop();
    this.mApnSettingsHidden = true;
  }
  
  private class RestoreApnProcessHandler
    extends Handler
  {
    private Handler mRestoreApnUiHandler;
    
    public RestoreApnProcessHandler(Looper paramLooper, Handler paramHandler)
    {
      super();
      this.mRestoreApnUiHandler = paramHandler;
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      }
      paramMessage = (TelephonyManager)ApnSettings.this.getSystemService("phone");
      if (ApnSettings.-get3(ApnSettings.this) == null) {}
      for (paramMessage = "";; paramMessage = paramMessage.getSimOperator(ApnSettings.-get3(ApnSettings.this).getSubscriptionId()))
      {
        Log.d("ApnSettings", "delete apn mccmnc = " + paramMessage);
        paramMessage = new StringBuilder("numeric=\"" + paramMessage + "\"");
        ApnSettings.this.getContentResolver().delete(ApnSettings.-wrap0(ApnSettings.this, ApnSettings.-get0()), paramMessage.toString(), null);
        this.mRestoreApnUiHandler.sendEmptyMessage(2);
        return;
      }
    }
  }
  
  private class RestoreApnUiHandler
    extends Handler
  {
    private RestoreApnUiHandler() {}
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      }
      paramMessage = ApnSettings.this.getActivity();
      if (paramMessage == null)
      {
        ApnSettings.-set0(false);
        return;
      }
      ApnSettings.-wrap2(ApnSettings.this);
      ApnSettings.this.getPreferenceScreen().setEnabled(true);
      ApnSettings.-set0(false);
      ApnSettings.this.removeDialog(1001, ApnSettings.-get1(ApnSettings.this));
      Toast.makeText(paramMessage, ApnSettings.this.getResources().getString(2131691883), 1).show();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ApnSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */