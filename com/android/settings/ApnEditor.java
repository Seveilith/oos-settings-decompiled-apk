package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony.Carriers;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.util.ArrayUtils;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ApnEditor
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener, View.OnKeyListener
{
  private static final int APN_INDEX = 2;
  private static final int AUTH_TYPE_INDEX = 14;
  private static final int BEARER_BITMASK_INDEX = 19;
  private static final int BEARER_INDEX = 18;
  private static final int CARRIER_ENABLED_INDEX = 17;
  private static final int DEFAULT_IPV4V6_INDEX = 2;
  private static final int EDITED_INDEX = 23;
  private static final int ID_INDEX = 0;
  private static final String KEY_AUTH_TYPE = "auth_type";
  private static final String KEY_BEARER_MULTI = "bearer_multi";
  private static final String KEY_CARRIER_ENABLED = "carrier_enabled";
  private static final String KEY_MVNO_TYPE = "mvno_type";
  private static final String KEY_PROTOCOL = "apn_protocol";
  private static final String KEY_ROAMING_PROTOCOL = "apn_roaming_protocol";
  private static final int MCC_INDEX = 9;
  private static final int MENU_CANCEL = 3;
  private static final int MENU_DELETE = 1;
  private static final int MENU_SAVE = 2;
  private static final int MMSC_INDEX = 8;
  private static final int MMSPORT_INDEX = 13;
  private static final int MMSPROXY_INDEX = 12;
  private static final int MNC_INDEX = 10;
  private static final int MVNO_MATCH_DATA_INDEX = 22;
  private static final int MVNO_TYPE_INDEX = 21;
  private static final int NAME_INDEX = 1;
  private static final int PASSWORD_INDEX = 7;
  private static final int PORT_INDEX = 4;
  private static final int PROTOCOL_INDEX = 16;
  private static final int PROXY_INDEX = 3;
  private static final int ROAMING_PROTOCOL_INDEX = 20;
  private static final String SAVED_POS = "pos";
  private static final int SERVER_INDEX = 6;
  private static final String TAG = ApnEditor.class.getSimpleName();
  private static final int TYPE_INDEX = 15;
  private static final int USER_INDEX = 5;
  private static String sNotSet;
  private static final String[] sProjection = { "_id", "name", "apn", "proxy", "port", "user", "server", "password", "mmsc", "mcc", "mnc", "numeric", "mmsproxy", "mmsport", "authtype", "type", "protocol", "carrier_enabled", "bearer", "bearer_bitmask", "roaming_protocol", "mvno_type", "mvno_match_data", "edited" };
  private EditTextPreference mApn;
  private boolean mApnDisable = false;
  private EditTextPreference mApnType;
  private ListPreference mAuthType;
  private int mBearerInitialVal = 0;
  private MultiSelectListPreference mBearerMulti;
  private SwitchPreference mCarrierEnabled;
  private String mCurMcc;
  private String mCurMnc;
  private Cursor mCursor;
  private boolean mDisableEditor = false;
  private boolean mFirstTime;
  private EditTextPreference mMcc;
  private EditTextPreference mMmsPort;
  private EditTextPreference mMmsProxy;
  private EditTextPreference mMmsc;
  private EditTextPreference mMnc;
  private EditTextPreference mMvnoMatchData;
  private String mMvnoMatchDataStr;
  private ListPreference mMvnoType;
  private String mMvnoTypeStr;
  private EditTextPreference mName;
  private boolean mNewApn;
  private EditTextPreference mPassword;
  private EditTextPreference mPort;
  private ListPreference mProtocol;
  private EditTextPreference mProxy;
  private boolean mReadOnlyApn;
  private String[] mReadOnlyApnFields;
  private String[] mReadOnlyApnTypes;
  private Resources mRes;
  private ListPreference mRoamingProtocol;
  private EditTextPreference mServer;
  private int mSubId;
  private TelephonyManager mTelephonyManager;
  private Uri mUri;
  private EditTextPreference mUser;
  
  private boolean apnTypesMatch(String[] paramArrayOfString, String paramString)
  {
    if (ArrayUtils.isEmpty(paramArrayOfString)) {
      return false;
    }
    if ((hasAllApns(paramArrayOfString)) || (TextUtils.isEmpty(paramString))) {
      return true;
    }
    paramArrayOfString = Arrays.asList(paramArrayOfString);
    paramString = paramString.split(",");
    int j = paramString.length;
    int i = 0;
    while (i < j)
    {
      Object localObject = paramString[i];
      if (paramArrayOfString.contains(((String)localObject).trim()))
      {
        Log.d(TAG, "apnTypesMatch: true because match found for " + ((String)localObject).trim());
        return true;
      }
      i += 1;
    }
    Log.d(TAG, "apnTypesMatch: false");
    return false;
  }
  
  private String bearerDescription(String paramString)
  {
    int i = this.mBearerMulti.findIndexOfValue(paramString);
    if (i == -1) {
      return null;
    }
    return this.mRes.getStringArray(2131427409)[i];
  }
  
  private String bearerMultiDescription(Set<String> paramSet)
  {
    String[] arrayOfString = this.mRes.getStringArray(2131427409);
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 1;
    paramSet = paramSet.iterator();
    while (paramSet.hasNext())
    {
      String str = (String)paramSet.next();
      int j = this.mBearerMulti.findIndexOfValue(str);
      if (i != 0) {}
      try
      {
        localStringBuilder.append(arrayOfString[j]);
        i = 0;
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
      localStringBuilder.append(", ").append(arrayOfString[j]);
    }
    paramSet = localStringBuilder.toString();
    if (!TextUtils.isEmpty(paramSet)) {
      return paramSet;
    }
    return null;
  }
  
  private String checkApnType(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return "default";
    }
    return paramString;
  }
  
  private String checkNotSet(String paramString)
  {
    if ((paramString == null) || (paramString.equals(sNotSet))) {
      return "";
    }
    return paramString;
  }
  
  private String checkNull(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return sNotSet;
    }
    return paramString;
  }
  
  private void deleteApn()
  {
    getContentResolver().delete(this.mUri, null, null);
    finish();
  }
  
  private void disableAllFields()
  {
    this.mName.setEnabled(false);
    this.mApn.setEnabled(false);
    this.mProxy.setEnabled(false);
    this.mPort.setEnabled(false);
    this.mUser.setEnabled(false);
    this.mServer.setEnabled(false);
    this.mPassword.setEnabled(false);
    this.mMmsProxy.setEnabled(false);
    this.mMmsPort.setEnabled(false);
    this.mMmsc.setEnabled(false);
    this.mMcc.setEnabled(false);
    this.mMnc.setEnabled(false);
    this.mApnType.setEnabled(false);
    this.mAuthType.setEnabled(false);
    this.mProtocol.setEnabled(false);
    this.mRoamingProtocol.setEnabled(false);
    this.mCarrierEnabled.setEnabled(false);
    this.mBearerMulti.setEnabled(false);
    this.mMvnoType.setEnabled(false);
    this.mMvnoMatchData.setEnabled(false);
  }
  
  private void disableFields(String[] paramArrayOfString)
  {
    int j = paramArrayOfString.length;
    int i = 0;
    while (i < j)
    {
      Preference localPreference = getPreferenceFromFieldName(paramArrayOfString[i]);
      if (localPreference != null) {
        localPreference.setEnabled(false);
      }
      i += 1;
    }
  }
  
  private void fillUi()
  {
    Object localObject2;
    int i;
    label393:
    boolean bool;
    label416:
    int j;
    if (this.mFirstTime)
    {
      this.mFirstTime = false;
      this.mName.setText(this.mCursor.getString(1));
      this.mApn.setText(this.mCursor.getString(2));
      this.mProxy.setText(this.mCursor.getString(3));
      this.mPort.setText(this.mCursor.getString(4));
      this.mUser.setText(this.mCursor.getString(5));
      this.mServer.setText(this.mCursor.getString(6));
      this.mPassword.setText(this.mCursor.getString(7));
      this.mMmsProxy.setText(this.mCursor.getString(12));
      this.mMmsPort.setText(this.mCursor.getString(13));
      this.mMmsc.setText(this.mCursor.getString(8));
      this.mMcc.setText(this.mCursor.getString(9));
      this.mMnc.setText(this.mCursor.getString(10));
      this.mApnType.setText(this.mCursor.getString(15));
      if (this.mNewApn)
      {
        localObject2 = this.mTelephonyManager.getSimOperator(this.mSubId);
        if ((localObject2 != null) && (((String)localObject2).length() > 4))
        {
          localObject1 = ((String)localObject2).substring(0, 3);
          localObject2 = ((String)localObject2).substring(3);
          this.mMcc.setText((String)localObject1);
          this.mMnc.setText((String)localObject2);
          this.mCurMnc = ((String)localObject2);
          this.mCurMcc = ((String)localObject1);
        }
        this.mApnType.setText("default");
      }
      i = this.mCursor.getInt(14);
      if (i == -1) {
        break label1130;
      }
      this.mAuthType.setValueIndex(i);
      if ((!this.mNewApn) || (!getResources().getBoolean(2131558444))) {
        break label1141;
      }
      this.mProtocol.setValueIndex(2);
      this.mRoamingProtocol.setValueIndex(2);
      localObject1 = this.mCarrierEnabled;
      if (this.mCursor.getInt(17) != 1) {
        break label1180;
      }
      bool = true;
      ((SwitchPreference)localObject1).setChecked(bool);
      this.mBearerInitialVal = this.mCursor.getInt(18);
      localObject1 = new HashSet();
      j = this.mCursor.getInt(19);
      if (j != 0) {
        break label1185;
      }
      if (this.mBearerInitialVal == 0) {
        ((HashSet)localObject1).add("0");
      }
      label478:
      if ((this.mBearerInitialVal != 0) && (!((HashSet)localObject1).contains("" + this.mBearerInitialVal))) {
        ((HashSet)localObject1).add("" + this.mBearerInitialVal);
      }
      this.mBearerMulti.setValues((Set)localObject1);
      this.mMvnoType.setValue(this.mCursor.getString(21));
      this.mMvnoMatchData.setEnabled(false);
      this.mMvnoMatchData.setText(this.mCursor.getString(22));
      localObject1 = ApnSettings.getLocalizedName(getActivity(), this.mCursor, 1);
      if (!TextUtils.isEmpty((CharSequence)localObject1)) {
        this.mName.setText((String)localObject1);
      }
    }
    this.mName.setSummary(checkNull(this.mName.getText()));
    this.mApn.setSummary(checkNull(this.mApn.getText()));
    this.mProxy.setSummary(checkNull(this.mProxy.getText()));
    this.mPort.setSummary(checkNull(this.mPort.getText()));
    this.mUser.setSummary(checkNull(this.mUser.getText()));
    this.mServer.setSummary(checkNull(this.mServer.getText()));
    this.mPassword.setSummary(starify(this.mPassword.getText()));
    this.mMmsProxy.setSummary(checkNull(this.mMmsProxy.getText()));
    this.mMmsPort.setSummary(checkNull(this.mMmsPort.getText()));
    this.mMmsc.setSummary(checkNull(this.mMmsc.getText()));
    this.mMcc.setSummary(checkNull(this.mMcc.getText()));
    this.mMnc.setSummary(checkNull(this.mMnc.getText()));
    this.mApnType.setSummary(checkApnType(this.mApnType.getText()));
    Object localObject1 = this.mAuthType.getValue();
    if (localObject1 != null)
    {
      i = Integer.parseInt((String)localObject1);
      this.mAuthType.setValueIndex(i);
      localObject1 = this.mRes.getStringArray(2131427405);
      this.mAuthType.setSummary(localObject1[i]);
      label914:
      this.mProtocol.setSummary(checkNull(protocolDescription(this.mProtocol.getValue(), this.mProtocol)));
      this.mRoamingProtocol.setSummary(checkNull(protocolDescription(this.mRoamingProtocol.getValue(), this.mRoamingProtocol)));
      this.mBearerMulti.setSummary(checkNull(bearerMultiDescription(this.mBearerMulti.getValues())));
      this.mMvnoType.setSummary(checkNull(mvnoDescription(this.mMvnoType.getValue())));
      this.mMvnoMatchData.setSummary(checkNull(this.mMvnoMatchData.getText()));
      if (!getResources().getBoolean(2131558450)) {
        break label1248;
      }
      this.mCarrierEnabled.setEnabled(true);
    }
    for (;;)
    {
      localObject1 = this.mMcc.getText() + this.mMnc.getText();
      localObject2 = getResources().getStringArray(2131427452);
      i = 0;
      j = localObject2.length;
      for (;;)
      {
        if (i >= j) {
          break label1274;
        }
        if ((localObject2[i].equals(localObject1)) && (!this.mNewApn)) {
          break;
        }
        i += 1;
      }
      label1130:
      this.mAuthType.setValue(null);
      break;
      label1141:
      this.mProtocol.setValue(this.mCursor.getString(16));
      this.mRoamingProtocol.setValue(this.mCursor.getString(20));
      break label393;
      label1180:
      bool = false;
      break label416;
      label1185:
      i = 1;
      while (j != 0)
      {
        if ((j & 0x1) == 1) {
          ((HashSet)localObject1).add("" + i);
        }
        j >>= 1;
        i += 1;
      }
      break label478;
      this.mAuthType.setSummary(sNotSet);
      break label914;
      label1248:
      this.mCarrierEnabled.setEnabled(false);
    }
    this.mApnDisable = true;
    Log.d(TAG, "APN is China Telecom's.");
    label1274:
    if (this.mDisableEditor)
    {
      if (this.mApnDisable)
      {
        this.mApn.setEnabled(false);
        Log.d(TAG, "Apn Name can't be edited.");
      }
    }
    else {
      return;
    }
    getPreferenceScreen().setEnabled(false);
    Log.d(TAG, "ApnEditor form is disabled.");
  }
  
  private String getErrorMsg()
  {
    String str1 = null;
    String str3 = checkNotSet(this.mName.getText());
    String str4 = checkNotSet(this.mApn.getText());
    String str5 = checkNotSet(this.mMcc.getText());
    String str2 = checkNotSet(this.mMnc.getText());
    if (str3.length() < 1) {
      str1 = this.mRes.getString(2131691877);
    }
    do
    {
      return str1;
      if (str4.length() < 1) {
        return this.mRes.getString(2131691878);
      }
      if (str5.length() != 3) {
        return this.mRes.getString(2131691879);
      }
    } while ((str2.length() & 0xFFFE) == 2);
    return this.mRes.getString(2131691880);
  }
  
  private Preference getPreferenceFromFieldName(String paramString)
  {
    if (paramString.equals("name")) {
      return this.mName;
    }
    if (paramString.equals("apn")) {
      return this.mApn;
    }
    if (paramString.equals("proxy")) {
      return this.mProxy;
    }
    if (paramString.equals("port")) {
      return this.mPort;
    }
    if (paramString.equals("user")) {
      return this.mUser;
    }
    if (paramString.equals("server")) {
      return this.mServer;
    }
    if (paramString.equals("password")) {
      return this.mPassword;
    }
    if (paramString.equals("mmsproxy")) {
      return this.mMmsProxy;
    }
    if (paramString.equals("mmsport")) {
      return this.mMmsPort;
    }
    if (paramString.equals("mmsc")) {
      return this.mMmsc;
    }
    if (paramString.equals("mcc")) {
      return this.mMcc;
    }
    if (paramString.equals("mnc")) {
      return this.mMnc;
    }
    if (paramString.equals("type")) {
      return this.mApnType;
    }
    if (paramString.equals("authtype")) {
      return this.mAuthType;
    }
    if (paramString.equals("protocol")) {
      return this.mProtocol;
    }
    if (paramString.equals("roaming_protocol")) {
      return this.mRoamingProtocol;
    }
    if (paramString.equals("carrier_enabled")) {
      return this.mCarrierEnabled;
    }
    if (paramString.equals("bearer")) {}
    while (paramString.equals("bearer_bitmask")) {
      return this.mBearerMulti;
    }
    if (paramString.equals("mvno_type")) {
      return this.mMvnoType;
    }
    if (paramString.equals("mvno_match_data")) {
      return this.mMvnoMatchData;
    }
    return null;
  }
  
  private boolean hasAllApns(String[] paramArrayOfString)
  {
    if (ArrayUtils.isEmpty(paramArrayOfString)) {
      return false;
    }
    paramArrayOfString = Arrays.asList(paramArrayOfString);
    if (paramArrayOfString.contains("*"))
    {
      Log.d(TAG, "hasAllApns: true because apnList.contains(PhoneConstants.APN_TYPE_ALL)");
      return true;
    }
    String[] arrayOfString = PhoneConstants.APN_TYPES;
    int j = arrayOfString.length;
    int i = 0;
    while (i < j)
    {
      if (!paramArrayOfString.contains(arrayOfString[i])) {
        return false;
      }
      i += 1;
    }
    Log.d(TAG, "hasAllApns: true");
    return true;
  }
  
  private String mvnoDescription(String paramString)
  {
    boolean bool = false;
    int i = this.mMvnoType.findIndexOfValue(paramString);
    String str = this.mMvnoType.getValue();
    if (i == -1) {
      return null;
    }
    String[] arrayOfString = this.mRes.getStringArray(2131427411);
    EditTextPreference localEditTextPreference = this.mMvnoMatchData;
    if (i != 0) {
      bool = true;
    }
    localEditTextPreference.setEnabled(bool);
    if ((paramString != null) && (!paramString.equals(str)))
    {
      if (!arrayOfString[i].equals("SPN")) {
        break label102;
      }
      this.mMvnoMatchData.setText(this.mTelephonyManager.getSimOperatorName());
    }
    for (;;)
    {
      return arrayOfString[i];
      label102:
      if (arrayOfString[i].equals("IMSI"))
      {
        paramString = this.mTelephonyManager.getSimOperator(this.mSubId);
        this.mMvnoMatchData.setText(paramString + "x");
      }
      else if (arrayOfString[i].equals("GID"))
      {
        this.mMvnoMatchData.setText(this.mTelephonyManager.getGroupIdLevel1());
      }
      else if ((arrayOfString[i].equals("ICCID")) && (this.mMvnoMatchDataStr != null))
      {
        Log.d(TAG, "mMvnoMatchDataStr: " + this.mMvnoMatchDataStr);
        this.mMvnoMatchData.setText(this.mMvnoMatchDataStr);
      }
    }
  }
  
  private String protocolDescription(String paramString, ListPreference paramListPreference)
  {
    int i = paramListPreference.findIndexOfValue(paramString);
    if (i == -1) {
      return null;
    }
    return this.mRes.getStringArray(2131427407)[i];
  }
  
  private String starify(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return sNotSet;
    }
    paramString = new char[paramString.length()];
    int i = 0;
    while (i < paramString.length)
    {
      paramString[i] = 42;
      i += 1;
    }
    return new String(paramString);
  }
  
  private boolean validateAndSave(boolean paramBoolean)
  {
    String str1;
    String str4;
    String str2;
    String str3;
    if ((!this.mDisableEditor) || (this.mApnDisable))
    {
      str1 = checkNotSet(this.mName.getText());
      str4 = checkNotSet(this.mApn.getText());
      str2 = checkNotSet(this.mMcc.getText());
      str3 = checkNotSet(this.mMnc.getText());
      if ((getErrorMsg() == null) || (paramBoolean))
      {
        if (this.mCursor.moveToFirst()) {
          break label119;
        }
        Log.w(TAG, "Could not go to the first row in the Cursor when saving data.");
        return false;
      }
    }
    else
    {
      Log.d(TAG, "Form is disabled. Nothing to save.");
      return true;
    }
    ErrorDialog.showError(this);
    return false;
    label119:
    if ((paramBoolean) && (this.mNewApn) && (str1.length() < 1) && (str4.length() < 1))
    {
      getContentResolver().delete(this.mUri, null, null);
      return false;
    }
    ContentValues localContentValues = new ContentValues();
    Object localObject = str1;
    if (str1.length() < 1) {
      localObject = getResources().getString(2131692657);
    }
    localContentValues.put("name", (String)localObject);
    localContentValues.put("apn", str4);
    localContentValues.put("proxy", checkNotSet(this.mProxy.getText()));
    localContentValues.put("port", checkNotSet(this.mPort.getText()));
    localContentValues.put("mmsproxy", checkNotSet(this.mMmsProxy.getText()));
    localContentValues.put("mmsport", checkNotSet(this.mMmsPort.getText()));
    localContentValues.put("user", checkNotSet(this.mUser.getText()));
    localContentValues.put("server", checkNotSet(this.mServer.getText()));
    localContentValues.put("password", checkNotSet(this.mPassword.getText()));
    localContentValues.put("mmsc", checkNotSet(this.mMmsc.getText()));
    localObject = this.mAuthType.getValue();
    if (localObject != null) {
      localContentValues.put("authtype", Integer.valueOf(Integer.parseInt((String)localObject)));
    }
    localContentValues.put("protocol", checkNotSet(this.mProtocol.getValue()));
    localContentValues.put("roaming_protocol", checkNotSet(this.mRoamingProtocol.getValue()));
    localContentValues.put("type", checkApnType(this.mApnType.getText()));
    localContentValues.put("mcc", str2);
    localContentValues.put("mnc", str3);
    localContentValues.put("numeric", str2 + str3);
    if ((this.mCurMnc != null) && (this.mCurMcc != null) && (this.mCurMnc.equals(str3)) && (this.mCurMcc.equals(str2))) {
      localContentValues.put("current", Integer.valueOf(1));
    }
    localObject = this.mBearerMulti.getValues();
    int i = 0;
    localObject = ((Iterable)localObject).iterator();
    int j = i;
    if (((Iterator)localObject).hasNext())
    {
      str1 = (String)((Iterator)localObject).next();
      if (Integer.parseInt(str1) == 0) {
        j = 0;
      }
    }
    else
    {
      localContentValues.put("bearer_bitmask", Integer.valueOf(j));
      if ((j != 0) && (this.mBearerInitialVal != 0)) {
        break label718;
      }
      i = 0;
      label616:
      localContentValues.put("bearer", Integer.valueOf(i));
      localContentValues.put("mvno_type", checkNotSet(this.mMvnoType.getValue()));
      localContentValues.put("mvno_match_data", checkNotSet(this.mMvnoMatchData.getText()));
      if (!this.mCarrierEnabled.isChecked()) {
        break label742;
      }
    }
    label718:
    label742:
    for (i = 1;; i = 0)
    {
      localContentValues.put("carrier_enabled", Integer.valueOf(i));
      getContentResolver().update(this.mUri, localContentValues, null, null);
      return true;
      i |= ServiceState.getBitmaskForTech(Integer.parseInt(str1));
      break;
      if (ServiceState.bitmaskHasTech(j, this.mBearerInitialVal))
      {
        i = this.mBearerInitialVal;
        break label616;
      }
      i = 0;
      break label616;
    }
  }
  
  protected int getMetricsCategory()
  {
    return 13;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230729);
    if (getActivity() != null) {
      getActivity().setTheme(2131821583);
    }
    sNotSet = getResources().getString(2131691834);
    this.mName = ((EditTextPreference)findPreference("apn_name"));
    this.mApn = ((EditTextPreference)findPreference("apn_apn"));
    this.mProxy = ((EditTextPreference)findPreference("apn_http_proxy"));
    this.mPort = ((EditTextPreference)findPreference("apn_http_port"));
    this.mUser = ((EditTextPreference)findPreference("apn_user"));
    this.mServer = ((EditTextPreference)findPreference("apn_server"));
    this.mPassword = ((EditTextPreference)findPreference("apn_password"));
    this.mMmsProxy = ((EditTextPreference)findPreference("apn_mms_proxy"));
    this.mMmsPort = ((EditTextPreference)findPreference("apn_mms_port"));
    this.mMmsc = ((EditTextPreference)findPreference("apn_mmsc"));
    this.mMcc = ((EditTextPreference)findPreference("apn_mcc"));
    this.mMnc = ((EditTextPreference)findPreference("apn_mnc"));
    this.mApnType = ((EditTextPreference)findPreference("apn_type"));
    this.mAuthType = ((ListPreference)findPreference("auth_type"));
    this.mAuthType.setOnPreferenceChangeListener(this);
    this.mProtocol = ((ListPreference)findPreference("apn_protocol"));
    this.mProtocol.setOnPreferenceChangeListener(this);
    this.mRoamingProtocol = ((ListPreference)findPreference("apn_roaming_protocol"));
    this.mRoamingProtocol.setOnPreferenceChangeListener(this);
    this.mCarrierEnabled = ((SwitchPreference)findPreference("carrier_enabled"));
    this.mBearerMulti = ((MultiSelectListPreference)findPreference("bearer_multi"));
    this.mBearerMulti.setOnPreferenceChangeListener(this);
    this.mBearerMulti.setPositiveButtonText(2131692133);
    this.mBearerMulti.setNegativeButtonText(2131692134);
    this.mMvnoType = ((ListPreference)findPreference("mvno_type"));
    this.mMvnoType.setOnPreferenceChangeListener(this);
    this.mMvnoMatchData = ((EditTextPreference)findPreference("mvno_match_data"));
    this.mRes = getResources();
    Intent localIntent = getIntent();
    String str = localIntent.getAction();
    this.mSubId = localIntent.getIntExtra("sub_id", -1);
    this.mDisableEditor = localIntent.getBooleanExtra("DISABLE_EDITOR", false);
    if (paramBundle == null) {}
    for (boolean bool = true;; bool = false)
    {
      this.mFirstTime = bool;
      this.mReadOnlyApn = false;
      this.mReadOnlyApnTypes = null;
      this.mReadOnlyApnFields = null;
      if (!str.equals("android.intent.action.EDIT")) {
        break label686;
      }
      paramBundle = localIntent.getData();
      if (paramBundle.isPathPrefixMatch(Telephony.Carriers.CONTENT_URI)) {
        break;
      }
      Log.e(TAG, "Edit request not for carrier table. Uri: " + paramBundle);
      finish();
      return;
    }
    this.mUri = paramBundle;
    this.mCursor = getActivity().managedQuery(this.mUri, sProjection, null, null);
    this.mCursor.moveToFirst();
    this.mTelephonyManager = ((TelephonyManager)getSystemService("phone"));
    Log.d(TAG, "onCreate: EDITED " + this.mCursor.getInt(23));
    if ((this.mCursor.getInt(23) != 1) && (apnTypesMatch(this.mReadOnlyApnTypes, this.mCursor.getString(15))))
    {
      Log.d(TAG, "onCreate: apnTypesMatch; read-only APN");
      this.mReadOnlyApn = true;
      disableAllFields();
    }
    for (;;)
    {
      int i = 0;
      while (i < getPreferenceScreen().getPreferenceCount())
      {
        getPreferenceScreen().getPreference(i).setOnPreferenceChangeListener(this);
        i += 1;
      }
      label686:
      if (str.equals("android.intent.action.INSERT"))
      {
        if ((this.mFirstTime) || (paramBundle.getInt("pos") == 0))
        {
          paramBundle = localIntent.getData();
          if (!paramBundle.isPathPrefixMatch(Telephony.Carriers.CONTENT_URI))
          {
            Log.e(TAG, "Insert request not for carrier table. Uri: " + paramBundle);
            finish();
            return;
          }
        }
        for (this.mUri = getContentResolver().insert(paramBundle, new ContentValues());; this.mUri = ContentUris.withAppendedId(Telephony.Carriers.CONTENT_URI, paramBundle.getInt("pos")))
        {
          this.mNewApn = true;
          this.mMvnoTypeStr = localIntent.getStringExtra("mvno_type");
          this.mMvnoMatchDataStr = localIntent.getStringExtra("mvno_match_data");
          if (this.mUri != null) {
            break;
          }
          Log.w(TAG, "Failed to insert new telephony provider into " + getIntent().getData());
          finish();
          return;
        }
        setResult(-1, new Intent().setAction(this.mUri.toString()));
        break;
      }
      finish();
      return;
      if (!ArrayUtils.isEmpty(this.mReadOnlyApnFields)) {
        disableFields(this.mReadOnlyApnFields);
      }
    }
    fillUi();
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
    if (this.mDisableEditor)
    {
      Log.d(TAG, "Form is disabled. Do not create the options menu.");
      return;
    }
    if (!this.mNewApn) {
      paramMenu.add(0, 1, 0, 2131691872).setIcon(2130838003);
    }
    paramMenu.add(0, 2, 0, 2131691874).setIcon(17301582);
    paramMenu.add(0, 3, 0, 2131691875).setIcon(17301560);
  }
  
  public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent.getAction() != 0) {
      return false;
    }
    switch (paramInt)
    {
    default: 
      return false;
    }
    if (validateAndSave(false)) {
      finish();
    }
    return true;
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return super.onOptionsItemSelected(paramMenuItem);
    case 1: 
      deleteApn();
      return true;
    case 2: 
      if (validateAndSave(false)) {
        finish();
      }
      return true;
    }
    if (this.mNewApn) {
      getContentResolver().delete(this.mUri, null, null);
    }
    finish();
    return true;
  }
  
  public void onPause()
  {
    super.onPause();
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    String str1 = null;
    String str2 = paramPreference.getKey();
    if ("auth_type".equals(str2)) {}
    for (;;)
    {
      try
      {
        int i = Integer.parseInt((String)paramObject);
        this.mAuthType.setValueIndex(i);
        paramPreference = this.mRes.getStringArray(2131427405);
        this.mAuthType.setSummary(paramPreference[i]);
        return true;
      }
      catch (NumberFormatException paramPreference)
      {
        return false;
      }
      if ("apn_protocol".equals(str2))
      {
        paramPreference = protocolDescription((String)paramObject, this.mProtocol);
        if (paramPreference == null) {
          return false;
        }
        this.mProtocol.setSummary(paramPreference);
        this.mProtocol.setValue((String)paramObject);
      }
      else if ("apn_roaming_protocol".equals(str2))
      {
        paramPreference = protocolDescription((String)paramObject, this.mRoamingProtocol);
        if (paramPreference == null) {
          return false;
        }
        this.mRoamingProtocol.setSummary(paramPreference);
        this.mRoamingProtocol.setValue((String)paramObject);
      }
      else if ("bearer_multi".equals(str2))
      {
        paramPreference = bearerMultiDescription((Set)paramObject);
        if (paramPreference == null) {
          return false;
        }
        this.mBearerMulti.setValues((Set)paramObject);
        this.mBearerMulti.setSummary(paramPreference);
      }
      else if ("mvno_type".equals(str2))
      {
        paramPreference = mvnoDescription((String)paramObject);
        if (paramPreference == null) {
          return false;
        }
        this.mMvnoType.setValue((String)paramObject);
        this.mMvnoType.setSummary(paramPreference);
      }
      else
      {
        if (paramPreference.equals(this.mPassword))
        {
          if (paramObject != null) {}
          for (paramObject = String.valueOf(paramObject);; paramObject = "")
          {
            paramPreference.setSummary(starify((String)paramObject));
            break;
          }
        }
        if (!paramPreference.equals(this.mCarrierEnabled))
        {
          if (paramObject != null) {
            str1 = String.valueOf(paramObject);
          }
          paramPreference.setSummary(checkNull(str1));
        }
      }
    }
  }
  
  public void onResume()
  {
    super.onResume();
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if ((validateAndSave(true)) && (this.mCursor != null) && (this.mCursor.moveToFirst())) {
      paramBundle.putInt("pos", this.mCursor.getInt(0));
    }
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    paramView.setOnKeyListener(this);
  }
  
  public static class ErrorDialog
    extends DialogFragment
  {
    public static void showError(ApnEditor paramApnEditor)
    {
      ErrorDialog localErrorDialog = new ErrorDialog();
      localErrorDialog.setTargetFragment(paramApnEditor, 0);
      localErrorDialog.show(paramApnEditor.getFragmentManager(), "error");
    }
    
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      paramBundle = ApnEditor.-wrap0((ApnEditor)getTargetFragment());
      return new AlertDialog.Builder(getContext()).setTitle(2131691876).setPositiveButton(17039370, null).setMessage(paramBundle).create();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ApnEditor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */