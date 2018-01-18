package com.android.settings;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserManager;
import com.android.internal.widget.LockPatternUtils;

abstract class SaveChosenLockWorkerBase
  extends Fragment
{
  private boolean mBlocking;
  protected long mChallenge;
  private boolean mFinished;
  protected boolean mHasChallenge;
  private Listener mListener;
  private Intent mResultData;
  protected int mUserId;
  protected LockPatternUtils mUtils;
  protected boolean mWasSecureBefore;
  
  protected void finish(Intent paramIntent)
  {
    this.mFinished = true;
    this.mResultData = paramIntent;
    if (this.mListener != null) {
      this.mListener.onChosenLockSaveFinished(this.mWasSecureBefore, this.mResultData);
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setRetainInstance(true);
  }
  
  protected void prepare(LockPatternUtils paramLockPatternUtils, boolean paramBoolean1, boolean paramBoolean2, long paramLong, int paramInt)
  {
    this.mUtils = paramLockPatternUtils;
    this.mUserId = paramInt;
    this.mHasChallenge = paramBoolean2;
    this.mChallenge = paramLong;
    this.mWasSecureBefore = this.mUtils.isSecure(this.mUserId);
    paramLockPatternUtils = getContext();
    if ((paramLockPatternUtils == null) || (UserManager.get(paramLockPatternUtils).getUserInfo(this.mUserId).isPrimary())) {
      this.mUtils.setCredentialRequiredToDecrypt(paramBoolean1);
    }
    this.mFinished = false;
    this.mResultData = null;
  }
  
  protected abstract Intent saveAndVerifyInBackground();
  
  public void setBlocking(boolean paramBoolean)
  {
    this.mBlocking = paramBoolean;
  }
  
  public void setListener(Listener paramListener)
  {
    if (this.mListener == paramListener) {
      return;
    }
    this.mListener = paramListener;
    if ((this.mFinished) && (this.mListener != null)) {
      this.mListener.onChosenLockSaveFinished(this.mWasSecureBefore, this.mResultData);
    }
  }
  
  protected void start()
  {
    if (this.mBlocking)
    {
      finish(saveAndVerifyInBackground());
      return;
    }
    new Task(null).execute(new Void[0]);
  }
  
  static abstract interface Listener
  {
    public abstract void onChosenLockSaveFinished(boolean paramBoolean, Intent paramIntent);
  }
  
  private class Task
    extends AsyncTask<Void, Void, Intent>
  {
    private Task() {}
    
    protected Intent doInBackground(Void... paramVarArgs)
    {
      return SaveChosenLockWorkerBase.this.saveAndVerifyInBackground();
    }
    
    protected void onPostExecute(Intent paramIntent)
    {
      SaveChosenLockWorkerBase.this.finish(paramIntent);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\SaveChosenLockWorkerBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */