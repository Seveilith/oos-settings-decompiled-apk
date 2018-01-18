package com.android.settings.notification;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import com.android.settings.utils.ManagedServiceSettings;
import com.android.settings.utils.ManagedServiceSettings.Config;
import com.android.settings.utils.ServiceListing;

public class NotificationAccessSettings
  extends ManagedServiceSettings
{
  private static final ManagedServiceSettings.Config CONFIG = getNotificationListenerConfig();
  private static final String TAG = NotificationAccessSettings.class.getSimpleName();
  private NotificationManager mNm;
  
  private static void deleteRules(Context paramContext, final String paramString)
  {
    AsyncTask.execute(new Runnable()
    {
      public void run()
      {
        ((NotificationManager)this.val$context.getSystemService(NotificationManager.class)).removeAutomaticZenRules(paramString);
      }
    });
  }
  
  public static int getEnabledListenersCount(Context paramContext)
  {
    return ServiceListing.getEnabledServicesCount(CONFIG, paramContext);
  }
  
  public static int getListenersCount(PackageManager paramPackageManager)
  {
    return ServiceListing.getServicesCount(CONFIG, paramPackageManager);
  }
  
  private static ManagedServiceSettings.Config getNotificationListenerConfig()
  {
    ManagedServiceSettings.Config localConfig = new ManagedServiceSettings.Config();
    localConfig.tag = TAG;
    localConfig.setting = "enabled_notification_listeners";
    localConfig.intentAction = "android.service.notification.NotificationListenerService";
    localConfig.permission = "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE";
    localConfig.noun = "notification listener";
    localConfig.warningDialogTitle = 2131693238;
    localConfig.warningDialogSummary = 2131693239;
    localConfig.emptyText = 2131693237;
    return localConfig;
  }
  
  protected ManagedServiceSettings.Config getConfig()
  {
    return CONFIG;
  }
  
  protected int getMetricsCategory()
  {
    return 179;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mNm = ((NotificationManager)getSystemService("notification"));
  }
  
  protected boolean setEnabled(ComponentName paramComponentName, String paramString, boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      if (!mServiceListing.isEnabled(paramComponentName)) {
        return true;
      }
      new FriendlyWarningDialogFragment().setServiceInfo(paramComponentName, paramString).show(getFragmentManager(), "friendlydialog");
      return false;
    }
    return super.setEnabled(paramComponentName, paramString, paramBoolean);
  }
  
  public class FriendlyWarningDialogFragment
    extends DialogFragment
  {
    static final String KEY_COMPONENT = "c";
    static final String KEY_LABEL = "l";
    
    public FriendlyWarningDialogFragment() {}
    
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      super.onCreate(paramBundle);
      final Object localObject = getArguments();
      paramBundle = ((Bundle)localObject).getString("l");
      localObject = ComponentName.unflattenFromString(((Bundle)localObject).getString("c"));
      paramBundle = getResources().getString(2131693240, new Object[] { paramBundle });
      new AlertDialog.Builder(NotificationAccessSettings.-get0()).setMessage(paramBundle).setCancelable(true).setPositiveButton(2131693241, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          NotificationAccessSettings.-get2().setEnabled(localObject, false);
          if (!NotificationAccessSettings.-get1(NotificationAccessSettings.this).isNotificationPolicyAccessGrantedForPackage(localObject.getPackageName())) {
            NotificationAccessSettings.-wrap0(NotificationAccessSettings.-get0(), localObject.getPackageName());
          }
        }
      }).setNegativeButton(2131693242, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {}
      }).create();
    }
    
    public FriendlyWarningDialogFragment setServiceInfo(ComponentName paramComponentName, String paramString)
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("c", paramComponentName.flattenToString());
      localBundle.putString("l", paramString);
      setArguments(localBundle);
      return this;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\NotificationAccessSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */