package com.android.settings.accounts;

import android.accounts.Account;
import android.app.ActivityManager;
import android.content.Context;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.android.settingslib.widget.AnimatedImageView;

public class SyncStateSwitchPreference
  extends SwitchPreference
{
  private Account mAccount;
  private String mAuthority;
  private boolean mFailed = false;
  private boolean mIsActive = false;
  private boolean mIsPending = false;
  private boolean mOneTimeSyncMode = false;
  
  public SyncStateSwitchPreference(Context paramContext, Account paramAccount, String paramString)
  {
    super(paramContext, null, 0, 2131821496);
    setup(paramAccount, paramString);
  }
  
  public SyncStateSwitchPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet, 0, 2131821496);
    this.mAccount = null;
    this.mAuthority = null;
  }
  
  public Account getAccount()
  {
    return this.mAccount;
  }
  
  public String getAuthority()
  {
    return this.mAuthority;
  }
  
  public boolean isOneTimeSyncMode()
  {
    return this.mOneTimeSyncMode;
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    Object localObject = (AnimatedImageView)paramPreferenceViewHolder.findViewById(2131362457);
    View localView = paramPreferenceViewHolder.findViewById(2131362456);
    boolean bool;
    if (!this.mIsActive)
    {
      bool = this.mIsPending;
      if (!bool) {
        break label142;
      }
      i = 0;
      label42:
      ((AnimatedImageView)localObject).setVisibility(i);
      ((AnimatedImageView)localObject).setAnimating(this.mIsActive);
      if ((this.mFailed) && (!bool)) {
        break label148;
      }
      i = 0;
      label70:
      if (i == 0) {
        break label153;
      }
    }
    label142:
    label148:
    label153:
    for (int i = 0;; i = 8)
    {
      localView.setVisibility(i);
      localObject = paramPreferenceViewHolder.findViewById(16908352);
      if (!this.mOneTimeSyncMode) {
        break label159;
      }
      ((View)localObject).setVisibility(8);
      ((TextView)paramPreferenceViewHolder.findViewById(16908304)).setText(getContext().getString(2131692718, new Object[] { getSummary() }));
      return;
      bool = true;
      break;
      i = 8;
      break label42;
      i = 1;
      break label70;
    }
    label159:
    ((View)localObject).setVisibility(0);
  }
  
  protected void onClick()
  {
    if (!this.mOneTimeSyncMode)
    {
      if (ActivityManager.isUserAMonkey()) {
        Log.d("SyncState", "ignoring monkey's attempt to flip sync state");
      }
    }
    else {
      return;
    }
    super.onClick();
  }
  
  public void setActive(boolean paramBoolean)
  {
    this.mIsActive = paramBoolean;
    notifyChanged();
  }
  
  public void setFailed(boolean paramBoolean)
  {
    this.mFailed = paramBoolean;
    notifyChanged();
  }
  
  public void setOneTimeSyncMode(boolean paramBoolean)
  {
    this.mOneTimeSyncMode = paramBoolean;
    notifyChanged();
  }
  
  public void setPending(boolean paramBoolean)
  {
    this.mIsPending = paramBoolean;
    notifyChanged();
  }
  
  public void setup(Account paramAccount, String paramString)
  {
    this.mAccount = paramAccount;
    this.mAuthority = paramString;
    notifyChanged();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accounts\SyncStateSwitchPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */