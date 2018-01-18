package com.android.settings.vpn2;

import android.content.Context;
import android.content.res.Resources;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.android.settings.GearPreference;

public abstract class ManageablePreference
  extends GearPreference
{
  public static int STATE_NONE = -1;
  boolean mIsAlwaysOn = false;
  int mState = STATE_NONE;
  int mUserId;
  
  public ManageablePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setPersistent(false);
    setOrder(0);
    setUserId(UserHandle.myUserId());
  }
  
  public int getState()
  {
    return this.mState;
  }
  
  public int getUserId()
  {
    return this.mUserId;
  }
  
  public boolean isAlwaysOn()
  {
    return this.mIsAlwaysOn;
  }
  
  public void setAlwaysOn(boolean paramBoolean)
  {
    if (this.mIsAlwaysOn != paramBoolean)
    {
      this.mIsAlwaysOn = paramBoolean;
      updateSummary();
    }
  }
  
  public void setState(int paramInt)
  {
    if (this.mState != paramInt)
    {
      this.mState = paramInt;
      updateSummary();
      notifyHierarchyChanged();
    }
  }
  
  public void setUserId(int paramInt)
  {
    this.mUserId = paramInt;
    checkRestrictionAndSetDisabled("no_config_vpn", paramInt);
  }
  
  protected void updateSummary()
  {
    Resources localResources = getContext().getResources();
    Object localObject1 = localResources.getStringArray(2131427434);
    Object localObject2;
    if (this.mState == STATE_NONE)
    {
      localObject1 = "";
      localObject2 = localObject1;
      if (this.mIsAlwaysOn)
      {
        localObject2 = localResources.getString(2131692876);
        if (!TextUtils.isEmpty((CharSequence)localObject1)) {
          break label67;
        }
      }
    }
    for (;;)
    {
      setSummary((CharSequence)localObject2);
      return;
      localObject1 = localObject1[this.mState];
      break;
      label67:
      localObject2 = localResources.getString(2131692143, new Object[] { localObject1, localObject2 });
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\vpn2\ManageablePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */