package com.oneplus.settings.ringtone;

import android.app.ActionBar;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.Ringtone;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import com.oneplus.settings.utils.OPUtils;

public abstract class OPRingtoneBaseActivity
  extends PreferenceActivity
  implements Runnable
{
  public static final int DELAY_MS_SELECTION_PLAYED = 300;
  public static final String KEY_SELECTED_ITEM_URI = "key_selected_item_uri";
  private static final String TAG = "RingtoneBaseActivity";
  private boolean isFirst = true;
  private boolean isPlaying = false;
  protected boolean isSelectedNone = false;
  private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener()
  {
    public void onAudioFocusChange(int paramAnonymousInt)
    {
      if ((paramAnonymousInt == -2) && (OPRingtoneBaseActivity.-get0(OPRingtoneBaseActivity.this))) {
        OPRingtoneBaseActivity.this.stopAnyPlayingRingtone();
      }
    }
  };
  private AudioManager mAudioManager;
  public boolean mContactsRingtone = false;
  private Ringtone mDefaultRingtone;
  public Uri mDefualtUri;
  public Handler mHandler;
  public boolean mHasDefaultItem;
  private final Runnable mLookupRingtoneNames = new Runnable()
  {
    public void run()
    {
      if (OPRingtoneBaseActivity.-get1(OPRingtoneBaseActivity.this) == 1) {
        OPRingtoneBaseActivity.this.mUriForDefaultItem = OPRingtoneManager.getActualRingtoneUriBySubId(OPRingtoneBaseActivity.this.getApplicationContext(), 0);
      }
      for (;;)
      {
        OPRingtoneBaseActivity.this.mHandler.post(new Runnable()
        {
          public void run()
          {
            OPRingtoneBaseActivity.this.updateSelected();
          }
        });
        return;
        if (OPRingtoneBaseActivity.-get1(OPRingtoneBaseActivity.this) == 2) {
          OPRingtoneBaseActivity.this.mUriForDefaultItem = OPRingtoneManager.getActualRingtoneUriBySubId(OPRingtoneBaseActivity.this.getApplicationContext(), 1);
        } else {
          OPRingtoneBaseActivity.this.mUriForDefaultItem = OPRingtoneManager.getActualDefaultRingtoneUri(OPRingtoneBaseActivity.this.getApplicationContext(), OPRingtoneBaseActivity.this.mType);
        }
      }
    }
  };
  private PhoneCallListener mPhoneCallListener;
  public OPRingtoneManager mRingtoneManager;
  private int mSimid = 0;
  private TelephonyManager mTelephonyManager;
  public int mType = 1;
  public Uri mUriForDefaultItem;
  
  private void lookupRingtoneNames()
  {
    if ((isThreePart()) || (this.mContactsRingtone)) {
      return;
    }
    AsyncTask.execute(this.mLookupRingtoneNames);
  }
  
  private void stopAnyPlayingRingtone2()
  {
    if (this.mDefaultRingtone != null)
    {
      this.mDefaultRingtone.stop();
      this.mDefaultRingtone = null;
    }
  }
  
  public int getCountOfSim()
  {
    int i = 0;
    if (!isMultiSimEnabled()) {
      return 1;
    }
    if (this.mTelephonyManager.hasIccCard(0)) {
      i = 1;
    }
    int j = i;
    if (this.mTelephonyManager.hasIccCard(1)) {
      j = i + 1;
    }
    OPMyLog.d("RingtoneBaseActivity", "getCountOfSim:" + j);
    return j;
  }
  
  public boolean getSim1Enable()
  {
    return this.mTelephonyManager.hasIccCard(0);
  }
  
  public boolean getSim2Enable()
  {
    return this.mTelephonyManager.hasIccCard(1);
  }
  
  public int getSimId()
  {
    return this.mSimid;
  }
  
  public boolean isMultiSimEnabled()
  {
    return this.mTelephonyManager.isMultiSimEnabled();
  }
  
  public boolean isThreePart()
  {
    OPMyLog.d("", "mHasDefaultItem:" + this.mHasDefaultItem + " mType:" + this.mType);
    if (this.mType != 4) {
      return this.mHasDefaultItem;
    }
    return true;
  }
  
  public void onBackPressed()
  {
    Intent localIntent = new Intent();
    localIntent.putExtra("android.intent.extra.ringtone.PICKED_URI", this.mUriForDefaultItem);
    setResult(-1, localIntent);
    super.onBackPressed();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (OPUtils.isWhiteModeOn(getContentResolver())) {
      getWindow().getDecorView().setSystemUiVisibility(8192);
    }
    this.mHandler = new Handler();
    Intent localIntent = getIntent();
    this.mType = localIntent.getIntExtra("android.intent.extra.ringtone.TYPE", 1);
    this.mHasDefaultItem = localIntent.getBooleanExtra("android.intent.extra.ringtone.SHOW_DEFAULT", false);
    this.mContactsRingtone = localIntent.getBooleanExtra("ringtone_for_contacts", false);
    this.mDefualtUri = ((Uri)localIntent.getParcelableExtra("android.intent.extra.ringtone.DEFAULT_URI"));
    Object localObject;
    if (this.mUriForDefaultItem == null)
    {
      if (paramBundle != null)
      {
        paramBundle = paramBundle.getString("key_selected_item_uri");
        if (paramBundle != null) {
          this.mUriForDefaultItem = Uri.parse(paramBundle);
        }
      }
      if ((this.mUriForDefaultItem == null) && (!this.isSelectedNone)) {}
    }
    else
    {
      Log.d("RingtoneBaseActivity", "mDefualtUri:" + this.mDefualtUri);
      Log.d("RingtoneBaseActivity", "mUriForDefaultItem:" + this.mUriForDefaultItem);
      Log.d("RingtoneBaseActivity", "mHasDefaultItem:" + this.mHasDefaultItem);
      localObject = localIntent.getCharSequenceExtra("android.intent.extra.ringtone.TITLE");
      this.mSimid = localIntent.getIntExtra("oneplus.intent.extra.ringtone.simid", 0);
      paramBundle = (Bundle)localObject;
      if (localObject == null)
      {
        if (this.mSimid != 1) {
          break label366;
        }
        paramBundle = getString(2131690156);
      }
    }
    for (;;)
    {
      this.mRingtoneManager = new OPRingtoneManager(this);
      this.mRingtoneManager.setType(this.mType);
      setVolumeControlStream(this.mRingtoneManager.inferStreamType());
      localObject = getActionBar();
      ((ActionBar)localObject).setTitle(paramBundle);
      ((ActionBar)localObject).setDisplayHomeAsUpEnabled(true);
      this.mTelephonyManager = ((TelephonyManager)getSystemService("phone"));
      this.mAudioManager = ((AudioManager)getSystemService("audio"));
      this.mPhoneCallListener = new PhoneCallListener();
      return;
      this.mUriForDefaultItem = ((Uri)localIntent.getParcelableExtra("android.intent.extra.ringtone.EXISTING_URI"));
      break;
      label366:
      if (this.mSimid == 2) {
        paramBundle = getString(2131690157);
      } else {
        paramBundle = getString(17040361);
      }
    }
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return super.onOptionsItemSelected(paramMenuItem);
    }
    onBackPressed();
    return true;
  }
  
  protected void onPause()
  {
    super.onPause();
    this.mHandler.removeCallbacks(this);
    stopAnyPlayingRingtone();
    this.mTelephonyManager.listen(this.mPhoneCallListener, 0);
    this.mAudioManager.abandonAudioFocus(this.mAudioFocusChangeListener);
  }
  
  protected void onRestoreInstanceState(Bundle paramBundle)
  {
    String str = paramBundle.getString("key_selected_item_uri");
    if (str != null) {
      this.mUriForDefaultItem = Uri.parse(str);
    }
    super.onRestoreInstanceState(paramBundle);
  }
  
  protected void onResume()
  {
    super.onResume();
    if (!this.isFirst) {
      lookupRingtoneNames();
    }
    this.isFirst = false;
    this.mTelephonyManager.listen(this.mPhoneCallListener, 32);
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    if (this.mUriForDefaultItem != null) {
      paramBundle.putString("key_selected_item_uri", this.mUriForDefaultItem.toString());
    }
    super.onSaveInstanceState(paramBundle);
  }
  
  protected void playRingtone(int paramInt, Uri paramUri)
  {
    this.mHandler.removeCallbacks(this);
    this.mUriForDefaultItem = paramUri;
    this.mHandler.postDelayed(this, paramInt);
  }
  
  public void run()
  {
    stopAnyPlayingRingtone2();
    OPMyLog.d("RingtoneBaseActivity", "mUriForDefaultItem:" + this.mUriForDefaultItem);
    if (this.mUriForDefaultItem == null) {
      return;
    }
    try
    {
      this.mDefaultRingtone = OPRingtoneManager.getRingtone(this, this.mUriForDefaultItem);
      this.mDefaultRingtone.setStreamType(this.mRingtoneManager.inferStreamType());
      if (this.mDefaultRingtone != null)
      {
        if (!this.isPlaying)
        {
          this.isPlaying = true;
          this.mAudioManager.requestAudioFocus(this.mAudioFocusChangeListener, this.mRingtoneManager.inferStreamType(), 2);
        }
        this.mDefaultRingtone.play();
      }
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        localException.printStackTrace();
      }
    }
  }
  
  protected void stopAnyPlayingRingtone()
  {
    stopAnyPlayingRingtone2();
    this.isPlaying = false;
    this.mAudioManager.abandonAudioFocus(null);
  }
  
  protected abstract void updateSelected();
  
  class PhoneCallListener
    extends PhoneStateListener
  {
    PhoneCallListener() {}
    
    public void onCallStateChanged(int paramInt, String paramString)
    {
      super.onCallStateChanged(paramInt, paramString);
      switch (paramInt)
      {
      case 2: 
      default: 
        return;
      }
      Log.d("RingtoneBaseActivity", "PhoneCallListener-CALL_STATE_RINGING--stopAnyPlayingRingtone");
      OPRingtoneBaseActivity.this.stopAnyPlayingRingtone();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ringtone\OPRingtoneBaseActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */