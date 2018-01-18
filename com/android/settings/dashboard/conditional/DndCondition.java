package com.android.settings.dashboard.conditional;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.PersistableBundle;
import android.service.notification.ZenModeConfig;
import android.util.Log;

public class DndCondition
  extends Condition
{
  private static final String KEY_STATE = "state";
  private static final String TAG = "DndCondition";
  private ZenModeConfig mConfig;
  private int mZen;
  
  public DndCondition(ConditionManager paramConditionManager)
  {
    super(paramConditionManager);
  }
  
  private CharSequence getZenState()
  {
    Log.w("DndCondition", "getZenState mZen = " + this.mZen);
    switch (this.mZen)
    {
    default: 
      return null;
    case 3: 
      return null;
    case 1: 
      return this.mManager.getContext().getString(2131690336);
    }
    return this.mManager.getContext().getString(2131693204);
  }
  
  public CharSequence[] getActions()
  {
    return new CharSequence[] { this.mManager.getContext().getString(2131693601) };
  }
  
  public Icon getIcon()
  {
    this.mZen = ((NotificationManager)this.mManager.getContext().getSystemService(NotificationManager.class)).getZenMode();
    Log.w("DndCondition", "getIcon mZen = " + this.mZen);
    switch (this.mZen)
    {
    case 2: 
    default: 
      return Icon.createWithResource(this.mManager.getContext(), 2130838233);
    case 3: 
      return Icon.createWithResource(this.mManager.getContext(), 2130838233);
    }
    return Icon.createWithResource(this.mManager.getContext(), 2130838232);
  }
  
  public int getMetricsConstant()
  {
    return 381;
  }
  
  protected Class<?> getReceiverClass()
  {
    return Receiver.class;
  }
  
  public CharSequence getSummary()
  {
    return null;
  }
  
  public CharSequence getTitle()
  {
    return getZenState();
  }
  
  public void onActionClick(int paramInt)
  {
    if (paramInt == 0)
    {
      ((NotificationManager)this.mManager.getContext().getSystemService(NotificationManager.class)).setZenMode(0, null, "DndCondition");
      setActive(false);
      return;
    }
    throw new IllegalArgumentException("Unexpected index " + paramInt);
  }
  
  public void onPrimaryClick() {}
  
  public void refreshState()
  {
    boolean bool = false;
    NotificationManager localNotificationManager = (NotificationManager)this.mManager.getContext().getSystemService(NotificationManager.class);
    this.mZen = localNotificationManager.getZenMode();
    if (this.mZen != 0) {
      bool = true;
    }
    if (bool) {}
    for (this.mConfig = localNotificationManager.getZenModeConfig();; this.mConfig = null)
    {
      setActive(bool);
      return;
    }
  }
  
  void restoreState(PersistableBundle paramPersistableBundle)
  {
    super.restoreState(paramPersistableBundle);
    this.mZen = paramPersistableBundle.getInt("state", 0);
  }
  
  boolean saveState(PersistableBundle paramPersistableBundle)
  {
    paramPersistableBundle.putInt("state", this.mZen);
    return super.saveState(paramPersistableBundle);
  }
  
  public static class Receiver
    extends BroadcastReceiver
  {
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if ("android.app.action.INTERRUPTION_FILTER_CHANGED_INTERNAL".equals(paramIntent.getAction())) {
        ((DndCondition)ConditionManager.get(paramContext).getCondition(DndCondition.class)).refreshState();
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\dashboard\conditional\DndCondition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */