package com.android.settings;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

public class CredentialCheckResultTracker
  extends Fragment
{
  private boolean mHasResult = false;
  private Listener mListener;
  private Intent mResultData;
  private int mResultEffectiveUserId;
  private boolean mResultMatched;
  private int mResultTimeoutMs;
  
  public void clearResult()
  {
    this.mHasResult = false;
    this.mResultMatched = false;
    this.mResultData = null;
    this.mResultTimeoutMs = 0;
    this.mResultEffectiveUserId = 0;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setRetainInstance(true);
  }
  
  public void setListener(Listener paramListener)
  {
    if (this.mListener == paramListener) {
      return;
    }
    this.mListener = paramListener;
    if ((this.mListener != null) && (this.mHasResult)) {
      this.mListener.onCredentialChecked(this.mResultMatched, this.mResultData, this.mResultTimeoutMs, this.mResultEffectiveUserId, false);
    }
  }
  
  public void setResult(boolean paramBoolean, Intent paramIntent, int paramInt1, int paramInt2)
  {
    this.mResultMatched = paramBoolean;
    this.mResultData = paramIntent;
    this.mResultTimeoutMs = paramInt1;
    this.mResultEffectiveUserId = paramInt2;
    this.mHasResult = true;
    if (this.mListener != null)
    {
      this.mListener.onCredentialChecked(this.mResultMatched, this.mResultData, this.mResultTimeoutMs, this.mResultEffectiveUserId, true);
      this.mHasResult = false;
    }
  }
  
  static abstract interface Listener
  {
    public abstract void onCredentialChecked(boolean paramBoolean1, Intent paramIntent, int paramInt1, int paramInt2, boolean paramBoolean2);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\CredentialCheckResultTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */