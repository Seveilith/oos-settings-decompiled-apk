package com.android.settings.dashboard.conditional;

import android.content.Context;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.graphics.drawable.Icon;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.settings.Settings.AccountSettingsActivity;
import java.util.List;

public class WorkModeCondition
  extends Condition
{
  private UserManager mUm = (UserManager)this.mManager.getContext().getSystemService("user");
  private UserHandle mUserHandle;
  
  public WorkModeCondition(ConditionManager paramConditionManager)
  {
    super(paramConditionManager);
  }
  
  private void updateUserHandle()
  {
    List localList = this.mUm.getProfiles(UserHandle.myUserId());
    int j = localList.size();
    this.mUserHandle = null;
    int i = 0;
    for (;;)
    {
      if (i < j)
      {
        UserInfo localUserInfo = (UserInfo)localList.get(i);
        if (localUserInfo.isManagedProfile()) {
          this.mUserHandle = localUserInfo.getUserHandle();
        }
      }
      else
      {
        return;
      }
      i += 1;
    }
  }
  
  public CharSequence[] getActions()
  {
    return new CharSequence[] { this.mManager.getContext().getString(2131693602) };
  }
  
  public Icon getIcon()
  {
    return Icon.createWithResource(this.mManager.getContext(), 2130838079);
  }
  
  public int getMetricsConstant()
  {
    return 383;
  }
  
  public CharSequence getSummary()
  {
    return this.mManager.getContext().getString(2131693615);
  }
  
  public CharSequence getTitle()
  {
    return this.mManager.getContext().getString(2131693614);
  }
  
  public void onActionClick(int paramInt)
  {
    if (paramInt == 0)
    {
      if (this.mUserHandle != null) {
        this.mUm.trySetQuietModeDisabled(this.mUserHandle.getIdentifier(), null);
      }
      setActive(false);
      return;
    }
    throw new IllegalArgumentException("Unexpected index " + paramInt);
  }
  
  public void onPrimaryClick()
  {
    this.mManager.getContext().startActivity(new Intent(this.mManager.getContext(), Settings.AccountSettingsActivity.class));
  }
  
  public void refreshState()
  {
    updateUserHandle();
    if (this.mUserHandle != null) {}
    for (boolean bool = this.mUm.isQuietModeEnabled(this.mUserHandle);; bool = false)
    {
      setActive(bool);
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\dashboard\conditional\WorkModeCondition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */