package com.android.settings.utils;

import android.app.Activity;
import android.app.VoiceInteractor;
import android.app.VoiceInteractor.AbortVoiceRequest;
import android.app.VoiceInteractor.CompleteVoiceRequest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public abstract class VoiceSettingsActivity
  extends Activity
{
  private static final String TAG = "VoiceSettingsActivity";
  
  protected void notifyFailure(CharSequence paramCharSequence)
  {
    if (getVoiceInteractor() != null) {
      getVoiceInteractor().submitRequest(new VoiceInteractor.AbortVoiceRequest(paramCharSequence, null));
    }
  }
  
  protected void notifySuccess(CharSequence paramCharSequence)
  {
    if (getVoiceInteractor() != null) {
      getVoiceInteractor().submitRequest(new VoiceInteractor.CompleteVoiceRequest(paramCharSequence, null)
      {
        public void onCompleteResult(Bundle paramAnonymousBundle)
        {
          VoiceSettingsActivity.this.finish();
        }
      });
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (isVoiceInteractionRoot())
    {
      if (onVoiceSettingInteraction(getIntent())) {
        finish();
      }
      return;
    }
    Log.v("VoiceSettingsActivity", "Cannot modify settings without voice interaction");
    finish();
  }
  
  protected abstract boolean onVoiceSettingInteraction(Intent paramIntent);
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\utils\VoiceSettingsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */