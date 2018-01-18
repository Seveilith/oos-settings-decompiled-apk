package com.oneplus.settings.ringtone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import com.oneplus.settings.utils.OPNotificationUtils;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OPRingtonePickerActivity
  extends OPRingtoneBaseActivity
  implements Preference.OnPreferenceClickListener
{
  private static final String DEFUALT_SELECT_KEY = "defualt_select";
  private static final String LOCAL_SELECT_KEY = "local_select";
  private static final String MAIN_KEY = "setting_title";
  private static final String NO_SELECT_KEY = "no_select";
  private static final String SIM1_SELECT_KEY = "sim1_select";
  private static final String SIM2_SELECT_KEY = "sim2_select";
  private static final String SWITCH_KEY = "setting_key";
  private Cursor mCursor;
  private OPRadioButtonPreference mDefualtPreference;
  private OPRadioButtonPreference mLocalPreference;
  private PreferenceCategory mMainRoot;
  private OPRadioButtonPreference mNOPreference;
  private int mRequestCode = 100;
  private Preference mSim1Layout;
  private Uri mSim1Uri;
  private Preference mSim2Layout;
  private Uri mSim2Uri;
  private final BroadcastReceiver mSimStateReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (("android.intent.action.SIM_STATE_CHANGED".equals(paramAnonymousIntent.getAction())) && (OPRingtonePickerActivity.this.mType == 1) && (OPRingtonePickerActivity.this.isMultiSimEnabled()))
      {
        if (OPRingtonePickerActivity.-get0(OPRingtonePickerActivity.this) != null) {
          OPRingtonePickerActivity.-get0(OPRingtonePickerActivity.this).setEnabled(OPRingtonePickerActivity.this.getSim1Enable());
        }
        if (OPRingtonePickerActivity.-get1(OPRingtonePickerActivity.this) != null) {
          OPRingtonePickerActivity.-get1(OPRingtonePickerActivity.this).setEnabled(OPRingtonePickerActivity.this.getSim2Enable());
        }
      }
    }
  };
  private SwitchPreference mSwitch;
  private List<OPRadioButtonPreference> mSystemRings = null;
  private Uri mUriForLocalItem;
  
  private void initPreference(boolean paramBoolean)
  {
    OPRadioButtonPreference localOPRadioButtonPreference;
    if (this.mSystemRings == null)
    {
      this.mSystemRings = new ArrayList();
      if ((this.mCursor != null) && (this.mCursor.moveToFirst()))
      {
        localOPRadioButtonPreference = new OPRadioButtonPreference(this);
        Object localObject = OPRingtoneManager.getSettingForType(this.mType);
        localOPRadioButtonPreference.setTitle(OPNotificationUtils.replaceWith(this, this.mCursor.getString(1), (String)localObject));
        localObject = OPRingtoneManager.getUriFromCursor(this.mCursor);
        localOPRadioButtonPreference.setKey(((Uri)localObject).toString());
        localOPRadioButtonPreference.setOnPreferenceClickListener(this);
        this.mSystemRings.add(localOPRadioButtonPreference);
        getPreferenceScreen().addPreference(localOPRadioButtonPreference);
        if ((this.mUriForDefaultItem == null) || (!this.mUriForDefaultItem.equals(localObject))) {
          break label155;
        }
        localOPRadioButtonPreference.setChecked(true);
        label142:
        if (this.mCursor.moveToNext()) {
          break label161;
        }
      }
    }
    label155:
    label161:
    do
    {
      return;
      localOPRadioButtonPreference.setChecked(false);
      break label142;
      break;
      int i = 0;
      if (i < this.mSystemRings.size())
      {
        if (paramBoolean) {
          getPreferenceScreen().addPreference((Preference)this.mSystemRings.get(i));
        }
        for (;;)
        {
          i += 1;
          break;
          getPreferenceScreen().removePreference((Preference)this.mSystemRings.get(i));
        }
      }
    } while (!paramBoolean);
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        OPRingtonePickerActivity.this.updateSelected();
      }
    });
  }
  
  private void switchSimRingtone(boolean paramBoolean)
  {
    if ((!this.mSwitch.isChecked()) || (this.mContactsRingtone))
    {
      this.mMainRoot.removePreference(this.mSim1Layout);
      this.mMainRoot.removePreference(this.mSim2Layout);
      initPreference(true);
      return;
    }
    this.mMainRoot.removePreference(this.mLocalPreference);
    this.mMainRoot.removePreference(this.mNOPreference);
  }
  
  private void updateChecks(String paramString)
  {
    if (this.mSystemRings == null) {
      return;
    }
    Iterator localIterator = this.mSystemRings.iterator();
    if (localIterator.hasNext())
    {
      OPRadioButtonPreference localOPRadioButtonPreference = (OPRadioButtonPreference)localIterator.next();
      if (localOPRadioButtonPreference.getKey().equals(paramString)) {}
      for (boolean bool = true;; bool = false)
      {
        localOPRadioButtonPreference.setChecked(bool);
        break;
      }
    }
  }
  
  private void updatePreference()
  {
    this.mCursor = this.mRingtoneManager.getCursor();
    if ((this.mType == 1) && (isMultiSimEnabled()))
    {
      addPreferencesFromResource(2131230811);
      this.mMainRoot = ((PreferenceCategory)findPreference("setting_title"));
      this.mSwitch = ((SwitchPreference)findPreference("setting_key"));
      this.mSim1Layout = findPreference("sim1_select");
      this.mSim2Layout = findPreference("sim2_select");
      this.mLocalPreference = ((OPRadioButtonPreference)findPreference("local_select"));
      this.mNOPreference = ((OPRadioButtonPreference)findPreference("no_select"));
      this.mSwitch.setOnPreferenceClickListener(this);
      this.mSim1Layout.setOnPreferenceClickListener(this);
      this.mSim2Layout.setOnPreferenceClickListener(this);
      this.mSwitch.setChecked(OPRingtoneManager.isRingSimSwitchOn(getApplicationContext()));
      if (this.mContactsRingtone) {
        getPreferenceScreen().removePreference(this.mSwitch);
      }
      switchSimRingtone(false);
    }
    for (;;)
    {
      this.mLocalPreference.setOnPreferenceClickListener(this);
      this.mNOPreference.setOnPreferenceClickListener(this);
      updateSelected();
      return;
      addPreferencesFromResource(2131230812);
      this.mLocalPreference = ((OPRadioButtonPreference)findPreference("local_select"));
      this.mNOPreference = ((OPRadioButtonPreference)findPreference("no_select"));
      this.mDefualtPreference = ((OPRadioButtonPreference)findPreference("defualt_select"));
      this.mDefualtPreference.setOnPreferenceClickListener(this);
      if (!this.mHasDefaultItem) {
        getPreferenceScreen().removePreference(this.mDefualtPreference);
      }
      initPreference(true);
    }
  }
  
  private void updateSimSwitch()
  {
    if (!isMultiSimEnabled()) {
      return;
    }
    if (this.mSim1Layout != null) {
      this.mSim1Layout.setEnabled(getSim1Enable());
    }
    if (this.mSim2Layout != null) {
      this.mSim2Layout.setEnabled(getSim2Enable());
    }
    this.mSim1Uri = OPRingtoneManager.getActualRingtoneUriBySubId(getApplicationContext(), 0);
    this.mSim2Uri = OPRingtoneManager.getActualRingtoneUriBySubId(getApplicationContext(), 1);
    Object localObject = OPRingtoneManager.getLocatRingtoneTitle(getApplicationContext(), this.mSim1Uri, this.mType, 1);
    OPRingtoneManager.ResultRing localResultRing = OPRingtoneManager.getLocatRingtoneTitle(getApplicationContext(), this.mSim2Uri, this.mType, 2);
    this.mSim1Uri = ((OPRingtoneManager.ResultRing)localObject).ringUri;
    this.mSim2Uri = localResultRing.ringUri;
    String str = OPRingtoneManager.getSettingForType(this.mType);
    Preference localPreference = this.mSim1Layout;
    if (((OPRingtoneManager.ResultRing)localObject).title != null)
    {
      localObject = OPNotificationUtils.replaceWith(this, OPUtils.getFileNameNoEx(((OPRingtoneManager.ResultRing)localObject).title), str);
      localPreference.setSummary((CharSequence)localObject);
      localPreference = this.mSim2Layout;
      if (localResultRing.title == null) {
        break label202;
      }
    }
    label202:
    for (localObject = OPNotificationUtils.replaceWith(this, OPUtils.getFileNameNoEx(localResultRing.title), str);; localObject = getString(2131690680))
    {
      localPreference.setSummary((CharSequence)localObject);
      return;
      localObject = getString(2131690680);
      break;
    }
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if ((paramInt1 == this.mRequestCode) && (paramIntent != null))
    {
      paramIntent = (Uri)paramIntent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
      if ((paramIntent != null) && (!paramIntent.equals(this.mUriForDefaultItem))) {}
    }
    else
    {
      return;
    }
    this.mUriForDefaultItem = paramIntent;
    updateSelected();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    updatePreference();
  }
  
  protected void onDestroy()
  {
    if (this.mCursor != null)
    {
      this.mCursor.close();
      this.mCursor = null;
    }
    super.onDestroy();
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    this.isSelectedNone = false;
    paramPreference = paramPreference.getKey();
    if (paramPreference.equals("setting_key"))
    {
      stopAnyPlayingRingtone();
      paramPreference = getApplicationContext();
      if (this.mSwitch.isChecked()) {}
      for (int i = 1;; i = 0)
      {
        OPRingtoneManager.setRingSimSwitch(paramPreference, i);
        if (!this.mSwitch.isChecked()) {
          break;
        }
        OPRingtoneManager.updateActualRingtone2(getApplicationContext());
        this.mMainRoot.removePreference(this.mLocalPreference);
        this.mMainRoot.removePreference(this.mNOPreference);
        this.mMainRoot.addPreference(this.mSim1Layout);
        this.mMainRoot.addPreference(this.mSim2Layout);
        initPreference(false);
        return true;
      }
      OPRingtoneManager.updateActualRingtone(getApplicationContext());
      this.mMainRoot.addPreference(this.mLocalPreference);
      this.mMainRoot.addPreference(this.mNOPreference);
      this.mMainRoot.removePreference(this.mSim1Layout);
      this.mMainRoot.removePreference(this.mSim2Layout);
      initPreference(true);
      return true;
    }
    if (paramPreference.equals("local_select"))
    {
      stopAnyPlayingRingtone();
      paramPreference = new Intent(this, OPLocalRingtonePickerActivity.class);
      paramPreference.putExtra("android.intent.extra.ringtone.TYPE", this.mType);
      paramPreference.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", false);
      paramPreference.putExtra("android.intent.extra.ringtone.EXISTING_URI", this.mUriForLocalItem);
      paramPreference.putExtra("ringtone_for_contacts", this.mContactsRingtone);
      startActivityForResult(paramPreference, this.mRequestCode);
      return true;
    }
    if (paramPreference.equals("no_select"))
    {
      stopAnyPlayingRingtone();
      updateChecks("-1");
      if (!isThreePart()) {
        OPRingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), this.mType, null);
      }
      if ((this.mHasDefaultItem) && (this.mDefualtPreference != null)) {
        this.mDefualtPreference.setChecked(false);
      }
      this.mLocalPreference.setChecked(false);
      this.mNOPreference.setChecked(true);
      this.isSelectedNone = true;
      this.mLocalPreference.setSummary(2131690159);
      this.mUriForLocalItem = null;
      this.mUriForDefaultItem = null;
      return true;
    }
    if (paramPreference.equals("sim1_select"))
    {
      paramPreference = new Intent(this, OPSystemRingtonePicker.class);
      paramPreference.putExtra("oneplus.intent.extra.ringtone.simid", 1);
      paramPreference.putExtra("android.intent.extra.ringtone.EXISTING_URI", this.mSim1Uri);
      paramPreference.putExtra("android.intent.extra.ringtone.TYPE", this.mType);
      startActivity(paramPreference);
      return true;
    }
    if (paramPreference.equals("sim2_select"))
    {
      paramPreference = new Intent(this, OPSystemRingtonePicker.class);
      paramPreference.putExtra("oneplus.intent.extra.ringtone.simid", 2);
      paramPreference.putExtra("android.intent.extra.ringtone.EXISTING_URI", this.mSim2Uri);
      paramPreference.putExtra("android.intent.extra.ringtone.TYPE", this.mType);
      startActivity(paramPreference);
      return true;
    }
    if (paramPreference.equals("defualt_select"))
    {
      this.mUriForDefaultItem = this.mDefualtUri;
      playRingtone(300, this.mUriForDefaultItem);
      updateChecks("-1");
      OPMyLog.d("", "mUriForDefaultItem:" + this.mUriForDefaultItem + " key:" + paramPreference);
      this.mLocalPreference.setChecked(false);
      this.mNOPreference.setChecked(false);
      if (this.mDefualtPreference != null) {
        this.mDefualtPreference.setChecked(true);
      }
      this.mLocalPreference.setSummary(2131690159);
      this.mUriForLocalItem = null;
      return true;
    }
    this.mUriForDefaultItem = Uri.parse(paramPreference);
    playRingtone(300, this.mUriForDefaultItem);
    if (!isThreePart()) {
      OPRingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), this.mType, this.mUriForDefaultItem);
    }
    updateChecks(paramPreference);
    if ((this.mHasDefaultItem) && (this.mDefualtPreference != null)) {
      this.mDefualtPreference.setChecked(false);
    }
    this.mLocalPreference.setChecked(false);
    this.mNOPreference.setChecked(false);
    this.mLocalPreference.setSummary(2131690159);
    this.mUriForLocalItem = null;
    return true;
  }
  
  protected void onStart()
  {
    super.onStart();
    if ((this.mType == 1) && (isMultiSimEnabled()))
    {
      IntentFilter localIntentFilter = new IntentFilter("android.intent.action.SIM_STATE_CHANGED");
      registerReceiver(this.mSimStateReceiver, localIntentFilter);
    }
  }
  
  protected void onStop()
  {
    if ((this.mType == 1) && (isMultiSimEnabled())) {
      unregisterReceiver(this.mSimStateReceiver);
    }
    super.onStop();
  }
  
  protected void updateSelected()
  {
    if (this.mType == 1) {
      updateSimSwitch();
    }
    if (this.mUriForDefaultItem == null)
    {
      this.mLocalPreference.setChecked(false);
      this.mNOPreference.setChecked(true);
      updateChecks("-1");
      this.mLocalPreference.setSummary(2131690159);
      this.mUriForLocalItem = null;
      if ((this.mHasDefaultItem) && (this.mDefualtPreference != null)) {
        this.mDefualtPreference.setChecked(false);
      }
    }
    do
    {
      return;
      this.mNOPreference.setChecked(false);
      if ((OPRingtoneManager.isDefault(this.mUriForDefaultItem)) && (this.mHasDefaultItem))
      {
        if (this.mDefualtPreference != null) {
          this.mDefualtPreference.setChecked(true);
        }
        this.mLocalPreference.setChecked(false);
        this.mLocalPreference.setSummary(2131690159);
        updateChecks("-1");
        this.mUriForLocalItem = null;
        return;
      }
      if (OPRingtoneManager.isDefault(this.mUriForDefaultItem)) {
        this.mUriForDefaultItem = OPRingtoneManager.ringtoneRestoreFromDefault(getApplicationContext(), this.mType, this.mUriForDefaultItem);
      }
      if (!OPRingtoneManager.isSystemRingtone(getApplicationContext(), this.mUriForDefaultItem, this.mType)) {
        break;
      }
      this.mLocalPreference.setChecked(false);
      updateChecks(this.mUriForDefaultItem.toString());
      this.mLocalPreference.setSummary(2131690159);
      this.mUriForLocalItem = null;
    } while ((!this.mHasDefaultItem) || (this.mDefualtPreference == null));
    this.mDefualtPreference.setChecked(false);
    return;
    this.mLocalPreference.setChecked(true);
    if ((this.mHasDefaultItem) && (this.mDefualtPreference != null)) {
      this.mDefualtPreference.setChecked(false);
    }
    OPRingtoneManager.ResultRing localResultRing = OPRingtoneManager.getLocatRingtoneTitle(getApplicationContext(), this.mUriForDefaultItem, this.mType, 0);
    OPRadioButtonPreference localOPRadioButtonPreference = this.mLocalPreference;
    if (localResultRing.title != null) {}
    for (Object localObject = localResultRing.title;; localObject = getString(2131690159))
    {
      localOPRadioButtonPreference.setSummary((CharSequence)localObject);
      localObject = localResultRing.ringUri;
      this.mUriForDefaultItem = ((Uri)localObject);
      this.mUriForLocalItem = ((Uri)localObject);
      updateChecks("-1");
      if (this.mType != 2) {
        break;
      }
      getPreferenceScreen().addPreference(this.mLocalPreference);
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ringtone\OPRingtonePickerActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */