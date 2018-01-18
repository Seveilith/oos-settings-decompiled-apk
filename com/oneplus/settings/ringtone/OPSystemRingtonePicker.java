package com.oneplus.settings.ringtone;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;
import com.oneplus.settings.utils.OPNotificationUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OPSystemRingtonePicker
  extends OPRingtoneBaseActivity
  implements Preference.OnPreferenceClickListener
{
  private static final String DEFUALT_SELECT_KEY = "defualt_select";
  private static final String LOCAL_SELECT_KEY = "local_select";
  private static final String NO_SELECT_KEY = "no_select";
  private Cursor mCursor;
  private OPRadioButtonPreference mDefualtPreference;
  private OPRadioButtonPreference mLocalPreference;
  private OPRadioButtonPreference mNOPreference;
  private List<OPRadioButtonPreference> mSystemRings = null;
  private Uri mUriForLocalItem;
  
  private void initPreference()
  {
    if (this.mSystemRings == null)
    {
      this.mSystemRings = new ArrayList();
      if ((this.mCursor != null) && (this.mCursor.moveToFirst())) {
        do
        {
          OPRadioButtonPreference localOPRadioButtonPreference = new OPRadioButtonPreference(this);
          String str = OPRingtoneManager.getSettingForType(this.mType);
          localOPRadioButtonPreference.setTitle(OPNotificationUtils.replaceWith(this, this.mCursor.getString(1), str));
          localOPRadioButtonPreference.setKey(OPRingtoneManager.getUriFromCursor(this.mCursor).toString());
          localOPRadioButtonPreference.setOnPreferenceClickListener(this);
          this.mSystemRings.add(localOPRadioButtonPreference);
          getPreferenceScreen().addPreference(localOPRadioButtonPreference);
          localOPRadioButtonPreference.setChecked(false);
        } while (this.mCursor.moveToNext());
      }
    }
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
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230812);
    this.mCursor = this.mRingtoneManager.getCursor();
    this.mLocalPreference = ((OPRadioButtonPreference)findPreference("local_select"));
    this.mNOPreference = ((OPRadioButtonPreference)findPreference("no_select"));
    this.mDefualtPreference = ((OPRadioButtonPreference)findPreference("defualt_select"));
    getPreferenceScreen().removePreference(this.mDefualtPreference);
    this.mLocalPreference.setOnPreferenceClickListener(this);
    this.mNOPreference.setOnPreferenceClickListener(this);
    initPreference();
    updateSelected();
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
    paramPreference = paramPreference.getKey();
    if (paramPreference.equals("local_select"))
    {
      stopAnyPlayingRingtone();
      paramPreference = new Intent(this, OPLocalRingtonePickerActivity.class);
      paramPreference.putExtra("android.intent.extra.ringtone.TYPE", this.mType);
      paramPreference.putExtra("oneplus.intent.extra.ringtone.simid", getSimId());
      paramPreference.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
      paramPreference.putExtra("android.intent.extra.ringtone.EXISTING_URI", this.mUriForLocalItem);
      startActivity(paramPreference);
      return true;
    }
    if (paramPreference.equals("no_select"))
    {
      stopAnyPlayingRingtone();
      updateChecks("-1");
      if (getSimId() == 2) {
        OPRingtoneManager.setActualRingtoneUriBySubId(getApplicationContext(), 1, null);
      }
      for (;;)
      {
        this.mLocalPreference.setChecked(false);
        this.mNOPreference.setChecked(true);
        this.mLocalPreference.setSummary(2131690159);
        this.mUriForLocalItem = null;
        this.mUriForDefaultItem = null;
        return true;
        OPRingtoneManager.setActualRingtoneUriBySubId(getApplicationContext(), 0, null);
      }
    }
    this.mUriForDefaultItem = Uri.parse(paramPreference);
    playRingtone(300, this.mUriForDefaultItem);
    if (getSimId() == 2) {
      OPRingtoneManager.setActualRingtoneUriBySubId(getApplicationContext(), 1, this.mUriForDefaultItem);
    }
    for (;;)
    {
      updateChecks(paramPreference);
      this.mLocalPreference.setChecked(false);
      this.mNOPreference.setChecked(false);
      this.mLocalPreference.setSummary(2131690159);
      this.mUriForLocalItem = null;
      return true;
      OPRingtoneManager.setActualRingtoneUriBySubId(getApplicationContext(), 0, this.mUriForDefaultItem);
    }
  }
  
  protected void updateSelected()
  {
    if (this.mUriForDefaultItem == null)
    {
      this.mLocalPreference.setChecked(false);
      this.mNOPreference.setChecked(true);
      updateChecks("-1");
      this.mLocalPreference.setSummary(2131690159);
      this.mUriForLocalItem = null;
      return;
    }
    this.mNOPreference.setChecked(false);
    if (OPRingtoneManager.isSystemRingtone(getApplicationContext(), this.mUriForDefaultItem, this.mType))
    {
      this.mLocalPreference.setChecked(false);
      updateChecks(this.mUriForDefaultItem.toString());
      this.mLocalPreference.setSummary(2131690159);
      this.mUriForLocalItem = null;
      return;
    }
    this.mLocalPreference.setChecked(true);
    OPRingtoneManager.ResultRing localResultRing = OPRingtoneManager.getLocatRingtoneTitle(getApplicationContext(), this.mUriForDefaultItem, this.mType, getSimId());
    OPRadioButtonPreference localOPRadioButtonPreference = this.mLocalPreference;
    if (localResultRing.title != null) {}
    for (Object localObject = localResultRing.title;; localObject = getString(2131690159))
    {
      localOPRadioButtonPreference.setSummary((CharSequence)localObject);
      localObject = localResultRing.ringUri;
      this.mUriForDefaultItem = ((Uri)localObject);
      this.mUriForLocalItem = ((Uri)localObject);
      updateChecks("-1");
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ringtone\OPSystemRingtonePicker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */