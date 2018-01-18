package com.android.settings.notification;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.service.notification.ZenModeConfig;
import android.service.notification.ZenModeConfig.EventInfo;
import android.service.notification.ZenModeConfig.ScheduleInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.utils.ZenServiceListing;
import com.android.settings.utils.ZenServiceListing.Callback;
import java.lang.ref.WeakReference;
import java.text.Collator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public abstract class ZenRuleSelectionDialog
{
  private static final boolean DEBUG = ZenModeSettings.DEBUG;
  private static final Comparator<ZenRuleInfo> RULE_TYPE_COMPARATOR = new Comparator()
  {
    private final Collator mCollator = Collator.getInstance();
    
    public int compare(ZenRuleInfo paramAnonymousZenRuleInfo1, ZenRuleInfo paramAnonymousZenRuleInfo2)
    {
      int i = this.mCollator.compare(paramAnonymousZenRuleInfo1.packageLabel, paramAnonymousZenRuleInfo2.packageLabel);
      if (i != 0) {
        return i;
      }
      return this.mCollator.compare(paramAnonymousZenRuleInfo1.title, paramAnonymousZenRuleInfo2.title);
    }
  };
  private static final String TAG = "ZenRuleSelectionDialog";
  private final Context mContext;
  private final AlertDialog mDialog;
  private NotificationManager mNm;
  private final PackageManager mPm;
  private final LinearLayout mRuleContainer;
  private final ZenServiceListing mServiceListing;
  private final ZenServiceListing.Callback mServiceListingCallback = new ZenServiceListing.Callback()
  {
    public void onServicesReloaded(Set<ServiceInfo> paramAnonymousSet)
    {
      if (ZenRuleSelectionDialog.-get0()) {
        Log.d("ZenRuleSelectionDialog", "Services reloaded: count=" + paramAnonymousSet.size());
      }
      TreeSet localTreeSet = new TreeSet(ZenRuleSelectionDialog.-get1());
      paramAnonymousSet = paramAnonymousSet.iterator();
      while (paramAnonymousSet.hasNext())
      {
        ServiceInfo localServiceInfo = (ServiceInfo)paramAnonymousSet.next();
        ZenRuleInfo localZenRuleInfo = ZenModeAutomationSettings.getRuleInfo(ZenRuleSelectionDialog.-get4(ZenRuleSelectionDialog.this), localServiceInfo);
        if ((localZenRuleInfo != null) && (localZenRuleInfo.configurationActivity != null) && (ZenRuleSelectionDialog.-get3(ZenRuleSelectionDialog.this).isNotificationPolicyAccessGrantedForPackage(localZenRuleInfo.packageName)) && ((localZenRuleInfo.ruleInstanceLimit <= 0) || (localZenRuleInfo.ruleInstanceLimit >= ZenRuleSelectionDialog.-get3(ZenRuleSelectionDialog.this).getRuleInstanceCount(localServiceInfo.getComponentName()) + 1))) {
          localTreeSet.add(localZenRuleInfo);
        }
      }
      ZenRuleSelectionDialog.-wrap0(ZenRuleSelectionDialog.this, localTreeSet);
    }
  };
  
  public ZenRuleSelectionDialog(Context paramContext, ZenServiceListing paramZenServiceListing)
  {
    this.mContext = paramContext;
    this.mPm = paramContext.getPackageManager();
    this.mNm = ((NotificationManager)paramContext.getSystemService("notification"));
    this.mServiceListing = paramZenServiceListing;
    paramZenServiceListing = LayoutInflater.from(paramContext).inflate(2130969130, null, false);
    this.mRuleContainer = ((LinearLayout)paramZenServiceListing.findViewById(2131362827));
    if (this.mServiceListing != null)
    {
      bindType(defaultNewEvent());
      bindType(defaultNewSchedule());
      this.mServiceListing.addZenCallback(this.mServiceListingCallback);
      this.mServiceListing.reloadApprovedServices();
    }
    this.mDialog = new AlertDialog.Builder(paramContext).setTitle(2131693268).setView(paramZenServiceListing).setOnDismissListener(new DialogInterface.OnDismissListener()
    {
      public void onDismiss(DialogInterface paramAnonymousDialogInterface)
      {
        if (ZenRuleSelectionDialog.-get5(ZenRuleSelectionDialog.this) != null) {
          ZenRuleSelectionDialog.-get5(ZenRuleSelectionDialog.this).removeZenCallback(ZenRuleSelectionDialog.-get6(ZenRuleSelectionDialog.this));
        }
      }
    }).setNegativeButton(2131690993, null).create();
  }
  
  private void bindExternalRules(Set<ZenRuleInfo> paramSet)
  {
    paramSet = paramSet.iterator();
    while (paramSet.hasNext()) {
      bindType((ZenRuleInfo)paramSet.next());
    }
  }
  
  private void bindType(final ZenRuleInfo paramZenRuleInfo)
  {
    try
    {
      ApplicationInfo localApplicationInfo = this.mPm.getApplicationInfo(paramZenRuleInfo.packageName, 0);
      LinearLayout localLinearLayout = (LinearLayout)LayoutInflater.from(this.mContext).inflate(2130969129, null, false);
      new LoadIconTask((ImageView)localLinearLayout.findViewById(2131361793)).execute(new ApplicationInfo[] { localApplicationInfo });
      ((TextView)localLinearLayout.findViewById(2131361894)).setText(paramZenRuleInfo.title);
      if (!paramZenRuleInfo.isSystem)
      {
        TextView localTextView = (TextView)localLinearLayout.findViewById(2131362171);
        localTextView.setText(localApplicationInfo.loadLabel(this.mPm));
        localTextView.setVisibility(0);
      }
      localLinearLayout.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          ZenRuleSelectionDialog.-get2(ZenRuleSelectionDialog.this).dismiss();
          if (paramZenRuleInfo.isSystem)
          {
            ZenRuleSelectionDialog.this.onSystemRuleSelected(paramZenRuleInfo);
            return;
          }
          ZenRuleSelectionDialog.this.onExternalRuleSelected(paramZenRuleInfo);
        }
      });
      this.mRuleContainer.addView(localLinearLayout);
      return;
    }
    catch (PackageManager.NameNotFoundException paramZenRuleInfo) {}
  }
  
  private ZenRuleInfo defaultNewEvent()
  {
    ZenModeConfig.EventInfo localEventInfo = new ZenModeConfig.EventInfo();
    localEventInfo.calendar = null;
    localEventInfo.reply = 0;
    ZenRuleInfo localZenRuleInfo = new ZenRuleInfo();
    localZenRuleInfo.settingsAction = "android.settings.ZEN_MODE_EVENT_RULE_SETTINGS";
    localZenRuleInfo.title = this.mContext.getString(2131693276);
    localZenRuleInfo.packageName = ZenModeConfig.getScheduleConditionProvider().getPackageName();
    localZenRuleInfo.defaultConditionId = ZenModeConfig.toEventConditionId(localEventInfo);
    localZenRuleInfo.serviceComponent = ZenModeConfig.getEventConditionProvider();
    localZenRuleInfo.isSystem = true;
    return localZenRuleInfo;
  }
  
  private ZenRuleInfo defaultNewSchedule()
  {
    ZenModeConfig.ScheduleInfo localScheduleInfo = new ZenModeConfig.ScheduleInfo();
    localScheduleInfo.days = ZenModeConfig.ALL_DAYS;
    localScheduleInfo.startHour = 22;
    localScheduleInfo.endHour = 7;
    ZenRuleInfo localZenRuleInfo = new ZenRuleInfo();
    localZenRuleInfo.settingsAction = "android.settings.ZEN_MODE_SCHEDULE_RULE_SETTINGS";
    localZenRuleInfo.title = this.mContext.getString(2131693274);
    localZenRuleInfo.packageName = ZenModeConfig.getEventConditionProvider().getPackageName();
    localZenRuleInfo.defaultConditionId = ZenModeConfig.toScheduleConditionId(localScheduleInfo);
    localZenRuleInfo.serviceComponent = ZenModeConfig.getScheduleConditionProvider();
    localZenRuleInfo.isSystem = true;
    return localZenRuleInfo;
  }
  
  public abstract void onExternalRuleSelected(ZenRuleInfo paramZenRuleInfo);
  
  public abstract void onSystemRuleSelected(ZenRuleInfo paramZenRuleInfo);
  
  public void show()
  {
    this.mDialog.show();
  }
  
  private class LoadIconTask
    extends AsyncTask<ApplicationInfo, Void, Drawable>
  {
    private final WeakReference<ImageView> viewReference;
    
    public LoadIconTask(ImageView paramImageView)
    {
      this.viewReference = new WeakReference(paramImageView);
    }
    
    protected Drawable doInBackground(ApplicationInfo... paramVarArgs)
    {
      return paramVarArgs[0].loadIcon(ZenRuleSelectionDialog.-get4(ZenRuleSelectionDialog.this));
    }
    
    protected void onPostExecute(Drawable paramDrawable)
    {
      if (paramDrawable != null)
      {
        ImageView localImageView = (ImageView)this.viewReference.get();
        if (localImageView != null) {
          localImageView.setImageDrawable(paramDrawable);
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\ZenRuleSelectionDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */