package com.android.settings.sim;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.SystemProperties;
import android.support.v4.app.NotificationCompat.Builder;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.settings.Settings.SimSettingsActivity;
import com.android.settings.Utils;
import java.util.List;

public class SimSelectNotification
  extends BroadcastReceiver
{
  private static final int NOTIFICATION_ID = 1;
  private static final String TAG = "SimSelectNotification";
  
  public static void cancelNotification(Context paramContext)
  {
    ((NotificationManager)paramContext.getSystemService("notification")).cancel(1);
  }
  
  private void createNotification(Context paramContext)
  {
    Object localObject = paramContext.getResources();
    localObject = new NotificationCompat.Builder(paramContext).setSmallIcon(2130838080).setColor(paramContext.getColor(2131493683)).setContentTitle(((Resources)localObject).getString(2131693110)).setContentText(((Resources)localObject).getString(2131693111));
    Intent localIntent = new Intent(paramContext, Settings.SimSettingsActivity.class);
    localIntent.addFlags(268435456);
    ((NotificationCompat.Builder)localObject).setContentIntent(PendingIntent.getActivity(paramContext, 0, localIntent, 268435456));
    ((NotificationManager)paramContext.getSystemService("notification")).notify(1, ((NotificationCompat.Builder)localObject).build());
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    TelephonyManager localTelephonyManager = (TelephonyManager)paramContext.getSystemService("phone");
    Object localObject = SubscriptionManager.from(paramContext);
    int j = localTelephonyManager.getSimCount();
    if ((j >= 2) && (Utils.isDeviceProvisioned(paramContext)) && (SystemProperties.getBoolean("persist.radio.aosp_usr_pref_sel", false)))
    {
      cancelNotification(paramContext);
      paramIntent = paramIntent.getStringExtra("ss");
      if ("ABSENT".equals(paramIntent)) {
        break label127;
      }
    }
    label127:
    for (boolean bool1 = "LOADED".equals(paramIntent); !bool1; bool1 = true)
    {
      Log.d("SimSelectNotification", "sim state is not Absent or Loaded");
      return;
      Log.d("SimSelectNotification", " no of slots " + j + " provision = " + Utils.isDeviceProvisioned(paramContext));
      return;
    }
    Log.d("SimSelectNotification", "simstatus = " + paramIntent);
    int i = 0;
    while (i < j)
    {
      int k = localTelephonyManager.getSimState(i);
      if ((k != 1) && (k != 5) && (k != 0))
      {
        Log.d("SimSelectNotification", "All sims not in valid state yet");
        return;
      }
      i += 1;
    }
    paramIntent = ((SubscriptionManager)localObject).getActiveSubscriptionInfoList();
    if ((paramIntent == null) || (paramIntent.size() < 1))
    {
      Log.d("SimSelectNotification", "Subscription list is empty");
      return;
    }
    ((SubscriptionManager)localObject).clearDefaultsForInactiveSubIds();
    bool1 = SubscriptionManager.isUsableSubIdValue(SubscriptionManager.getDefaultDataSubscriptionId());
    boolean bool2 = SubscriptionManager.isUsableSubIdValue(SubscriptionManager.getDefaultSmsSubscriptionId());
    if ((bool1) && (bool2))
    {
      Log.d("SimSelectNotification", "Data & SMS default sims are selected. No notification");
      return;
    }
    createNotification(paramContext);
    if (paramIntent.size() == 1)
    {
      localObject = new Intent(paramContext, SimDialogActivity.class);
      ((Intent)localObject).addFlags(268435456);
      ((Intent)localObject).putExtra(SimDialogActivity.DIALOG_TYPE_KEY, 3);
      ((Intent)localObject).putExtra(SimDialogActivity.PREFERRED_SIM, ((SubscriptionInfo)paramIntent.get(0)).getSimSlotIndex());
      paramContext.startActivity((Intent)localObject);
    }
    while (bool1) {
      return;
    }
    paramIntent = new Intent(paramContext, SimDialogActivity.class);
    paramIntent.addFlags(268435456);
    paramIntent.putExtra(SimDialogActivity.DIALOG_TYPE_KEY, 0);
    paramContext.startActivity(paramIntent);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\sim\SimSelectNotification.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */