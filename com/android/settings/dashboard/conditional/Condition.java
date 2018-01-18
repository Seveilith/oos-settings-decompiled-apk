package com.android.settings.dashboard.conditional;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.os.PersistableBundle;
import com.android.internal.logging.MetricsLogger;

public abstract class Condition
{
  private static final String KEY_ACTIVE = "active";
  private static final String KEY_LAST_STATE = "last_state";
  private static final String KEY_SILENCE = "silence";
  private boolean mIsActive;
  private boolean mIsSilenced;
  private long mLastStateChange;
  protected final ConditionManager mManager;
  
  Condition(ConditionManager paramConditionManager)
  {
    this.mManager = paramConditionManager;
  }
  
  private void onSilenceChanged(boolean paramBoolean)
  {
    Object localObject = getReceiverClass();
    if (localObject == null) {
      return;
    }
    PackageManager localPackageManager = this.mManager.getContext().getPackageManager();
    localObject = new ComponentName(this.mManager.getContext(), (Class)localObject);
    if (paramBoolean) {}
    for (int i = 1;; i = 2)
    {
      localPackageManager.setComponentEnabledSetting((ComponentName)localObject, i, 1);
      return;
    }
  }
  
  public abstract CharSequence[] getActions();
  
  public abstract Icon getIcon();
  
  long getLastChange()
  {
    return this.mLastStateChange;
  }
  
  public abstract int getMetricsConstant();
  
  protected Class<?> getReceiverClass()
  {
    return null;
  }
  
  public abstract CharSequence getSummary();
  
  public abstract CharSequence getTitle();
  
  public boolean isActive()
  {
    return this.mIsActive;
  }
  
  public boolean isSilenced()
  {
    return this.mIsSilenced;
  }
  
  protected void notifyChanged()
  {
    this.mManager.notifyChanged(this);
  }
  
  public abstract void onActionClick(int paramInt);
  
  public abstract void onPrimaryClick();
  
  public abstract void refreshState();
  
  void restoreState(PersistableBundle paramPersistableBundle)
  {
    this.mIsSilenced = paramPersistableBundle.getBoolean("silence");
    this.mIsActive = paramPersistableBundle.getBoolean("active");
    this.mLastStateChange = paramPersistableBundle.getLong("last_state");
  }
  
  boolean saveState(PersistableBundle paramPersistableBundle)
  {
    if (this.mIsSilenced) {
      paramPersistableBundle.putBoolean("silence", this.mIsSilenced);
    }
    if (this.mIsActive)
    {
      paramPersistableBundle.putBoolean("active", this.mIsActive);
      paramPersistableBundle.putLong("last_state", this.mLastStateChange);
    }
    if (!this.mIsSilenced) {
      return this.mIsActive;
    }
    return true;
  }
  
  protected void setActive(boolean paramBoolean)
  {
    if (this.mIsActive == paramBoolean) {
      return;
    }
    this.mIsActive = paramBoolean;
    this.mLastStateChange = System.currentTimeMillis();
    if ((!this.mIsSilenced) || (paramBoolean)) {}
    for (;;)
    {
      notifyChanged();
      return;
      this.mIsSilenced = false;
      onSilenceChanged(this.mIsSilenced);
    }
  }
  
  public void setSilenced()
  {
    this.mIsSilenced = false;
  }
  
  public boolean shouldShow()
  {
    return (isActive()) && (!isSilenced());
  }
  
  public void silence()
  {
    if (!this.mIsSilenced)
    {
      this.mIsSilenced = true;
      MetricsLogger.action(this.mManager.getContext(), 372, getMetricsConstant());
      onSilenceChanged(this.mIsSilenced);
      notifyChanged();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\dashboard\conditional\Condition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */