package com.android.settings.notification;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.INotificationManager;
import android.app.INotificationManager.Stub;
import android.app.Notification;
import android.app.Notification.Action;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.NotificationListenerService.Ranking;
import android.service.notification.NotificationListenerService.RankingMap;
import android.service.notification.StatusBarNotification;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DateTimeView;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.android.settings.CopyablePreference;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class NotificationStation
  extends SettingsPreferenceFragment
{
  private static final boolean DEBUG = false;
  private static final boolean DUMP_EXTRAS = true;
  private static final boolean DUMP_PARCEL = true;
  private static final String TAG = NotificationStation.class.getSimpleName();
  private Context mContext;
  private Handler mHandler;
  private final NotificationListenerService mListener = new NotificationListenerService()
  {
    public void onListenerConnected()
    {
      NotificationStation.-set0(NotificationStation.this, getCurrentRanking());
      if (NotificationStation.-get0(NotificationStation.this) == null) {}
      for (int i = 0;; i = NotificationStation.-get0(NotificationStation.this).getOrderedKeys().length)
      {
        NotificationStation.-wrap0("onListenerConnected with update for %d", new Object[] { Integer.valueOf(i) });
        NotificationStation.-wrap2(NotificationStation.this);
        return;
      }
    }
    
    public void onNotificationPosted(StatusBarNotification paramAnonymousStatusBarNotification, NotificationListenerService.RankingMap paramAnonymousRankingMap)
    {
      int i = 0;
      paramAnonymousStatusBarNotification = paramAnonymousStatusBarNotification.getNotification();
      if (paramAnonymousRankingMap == null) {}
      for (;;)
      {
        NotificationStation.-wrap0("onNotificationPosted: %s, with update for %d", new Object[] { paramAnonymousStatusBarNotification, Integer.valueOf(i) });
        NotificationStation.-set0(NotificationStation.this, paramAnonymousRankingMap);
        NotificationStation.-wrap2(NotificationStation.this);
        return;
        i = paramAnonymousRankingMap.getOrderedKeys().length;
      }
    }
    
    public void onNotificationRankingUpdate(NotificationListenerService.RankingMap paramAnonymousRankingMap)
    {
      if (paramAnonymousRankingMap == null) {}
      for (int i = 0;; i = paramAnonymousRankingMap.getOrderedKeys().length)
      {
        NotificationStation.-wrap0("onNotificationRankingUpdate with update for %d", new Object[] { Integer.valueOf(i) });
        NotificationStation.-set0(NotificationStation.this, paramAnonymousRankingMap);
        NotificationStation.-wrap2(NotificationStation.this);
        return;
      }
    }
    
    public void onNotificationRemoved(StatusBarNotification paramAnonymousStatusBarNotification, NotificationListenerService.RankingMap paramAnonymousRankingMap)
    {
      if (paramAnonymousRankingMap == null) {}
      for (int i = 0;; i = paramAnonymousRankingMap.getOrderedKeys().length)
      {
        NotificationStation.-wrap0("onNotificationRankingUpdate with update for %d", new Object[] { Integer.valueOf(i) });
        NotificationStation.-set0(NotificationStation.this, paramAnonymousRankingMap);
        NotificationStation.-wrap2(NotificationStation.this);
        return;
      }
    }
  };
  private INotificationManager mNoMan;
  private final Comparator<HistoricalNotificationInfo> mNotificationSorter = new Comparator()
  {
    public int compare(NotificationStation.HistoricalNotificationInfo paramAnonymousHistoricalNotificationInfo1, NotificationStation.HistoricalNotificationInfo paramAnonymousHistoricalNotificationInfo2)
    {
      return (int)(paramAnonymousHistoricalNotificationInfo2.timestamp - paramAnonymousHistoricalNotificationInfo1.timestamp);
    }
  };
  private PackageManager mPm;
  private NotificationListenerService.RankingMap mRanking;
  private Runnable mRefreshListRunnable = new Runnable()
  {
    public void run()
    {
      NotificationStation.-wrap1(NotificationStation.this);
    }
  };
  
  private static CharSequence bold(CharSequence paramCharSequence)
  {
    if (paramCharSequence.length() == 0) {
      return paramCharSequence;
    }
    SpannableString localSpannableString = new SpannableString(paramCharSequence);
    localSpannableString.setSpan(new StyleSpan(1), 0, paramCharSequence.length(), 0);
    return localSpannableString;
  }
  
  private static String formatPendingIntent(PendingIntent paramPendingIntent)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    paramPendingIntent = paramPendingIntent.getIntentSender();
    localStringBuilder.append("Intent(pkg=").append(paramPendingIntent.getCreatorPackage());
    try
    {
      if (ActivityManagerNative.getDefault().isIntentSenderAnActivity(paramPendingIntent.getTarget())) {
        localStringBuilder.append(" (activity)");
      }
      localStringBuilder.append(")");
      return localStringBuilder.toString();
    }
    catch (RemoteException paramPendingIntent)
    {
      for (;;) {}
    }
  }
  
  private Resources getResourcesForUserPackage(String paramString, int paramInt)
  {
    if (paramString != null)
    {
      int i = paramInt;
      if (paramInt == -1) {
        i = 0;
      }
      try
      {
        Resources localResources = this.mPm.getResourcesForApplicationAsUser(paramString, i);
        return localResources;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        Log.e(TAG, "Icon package not found: " + paramString, localNameNotFoundException);
        return null;
      }
    }
    return this.mContext.getResources();
  }
  
  private static String getTitleString(Notification paramNotification)
  {
    Object localObject1 = null;
    if (paramNotification.extras != null)
    {
      localObject2 = paramNotification.extras.getCharSequence("android.title");
      localObject1 = localObject2;
      if (TextUtils.isEmpty((CharSequence)localObject2)) {
        localObject1 = paramNotification.extras.getCharSequence("android.text");
      }
    }
    Object localObject2 = localObject1;
    if (TextUtils.isEmpty((CharSequence)localObject1)) {
      if (!TextUtils.isEmpty(paramNotification.tickerText)) {
        break label64;
      }
    }
    label64:
    for (localObject2 = localObject1;; localObject2 = paramNotification.tickerText) {
      return String.valueOf(localObject2);
    }
  }
  
  private Drawable loadIconDrawable(String paramString, int paramInt1, int paramInt2)
  {
    Object localObject = getResourcesForUserPackage(paramString, paramInt1);
    if (paramInt2 == 0) {
      return null;
    }
    String str;
    StringBuilder localStringBuilder;
    try
    {
      localObject = ((Resources)localObject).getDrawable(paramInt2, null);
      return (Drawable)localObject;
    }
    catch (RuntimeException localRuntimeException)
    {
      str = TAG;
      localStringBuilder = new StringBuilder().append("Icon not found in ");
      if (paramString == null) {}
    }
    for (paramString = Integer.valueOf(paramInt2);; paramString = "<system>")
    {
      Log.w(str, paramString + ": " + Integer.toHexString(paramInt2), localRuntimeException);
      return null;
    }
  }
  
  private List<HistoricalNotificationInfo> loadNotifications()
  {
    int n = ActivityManager.getCurrentUser();
    ArrayList localArrayList;
    int i;
    int j;
    int k;
    label135:
    int m;
    try
    {
      arrayOfStatusBarNotification1 = this.mNoMan.getActiveNotifications(this.mContext.getPackageName());
      localObject1 = this.mNoMan.getHistoricalNotifications(this.mContext.getPackageName(), 50);
      localArrayList = new ArrayList(arrayOfStatusBarNotification1.length + localObject1.length);
      localRanking = new NotificationListenerService.Ranking();
      StatusBarNotification[][] arrayOfStatusBarNotification = new StatusBarNotification[2][];
      arrayOfStatusBarNotification[0] = arrayOfStatusBarNotification1;
      arrayOfStatusBarNotification[1] = localObject1;
      int i1 = arrayOfStatusBarNotification.length;
      i = 0;
      if (i >= i1) {
        break label1723;
      }
      arrayOfStatusBarNotification2 = arrayOfStatusBarNotification[i];
      int i2 = arrayOfStatusBarNotification2.length;
      j = 0;
      if (j >= i2) {
        break label1716;
      }
      localObject1 = arrayOfStatusBarNotification2[j];
      if (((StatusBarNotification)localObject1).getUserId() == -1) {
        break label1740;
      }
      k = 1;
      if (((StatusBarNotification)localObject1).getUserId() == n) {
        break label1745;
      }
      m = 1;
    }
    catch (RemoteException localRemoteException)
    {
      StatusBarNotification[] arrayOfStatusBarNotification1;
      Object localObject1;
      NotificationListenerService.Ranking localRanking;
      StatusBarNotification[] arrayOfStatusBarNotification2;
      label151:
      Log.e(TAG, "Cannot load Notifications: ", localRemoteException);
      return null;
    }
    Notification localNotification = ((StatusBarNotification)localObject1).getNotification();
    HistoricalNotificationInfo localHistoricalNotificationInfo = new HistoricalNotificationInfo(null);
    localHistoricalNotificationInfo.pkg = ((StatusBarNotification)localObject1).getPackageName();
    localHistoricalNotificationInfo.user = ((StatusBarNotification)localObject1).getUserId();
    localHistoricalNotificationInfo.icon = loadIconDrawable(localHistoricalNotificationInfo.pkg, localHistoricalNotificationInfo.user, localNotification.icon);
    localHistoricalNotificationInfo.pkgicon = loadPackageIconDrawable(localHistoricalNotificationInfo.pkg, localHistoricalNotificationInfo.user);
    localHistoricalNotificationInfo.pkgname = loadPackageName(localHistoricalNotificationInfo.pkg);
    localHistoricalNotificationInfo.title = getTitleString(localNotification);
    if (TextUtils.isEmpty(localHistoricalNotificationInfo.title)) {
      localHistoricalNotificationInfo.title = getString(2131693673);
    }
    localHistoricalNotificationInfo.timestamp = ((StatusBarNotification)localObject1).getPostTime();
    localHistoricalNotificationInfo.priority = localNotification.priority;
    if (arrayOfStatusBarNotification2 == arrayOfStatusBarNotification1) {}
    for (boolean bool = true;; bool = false)
    {
      localHistoricalNotificationInfo.active = bool;
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
      String str2 = getString(2131693674);
      localSpannableStringBuilder.append(bold(getString(2131693675))).append(str2).append(localHistoricalNotificationInfo.pkg).append("\n").append(bold(getString(2131693676))).append(str2).append(((StatusBarNotification)localObject1).getKey());
      localSpannableStringBuilder.append("\n").append(bold(getString(2131693692))).append(str2).append(localNotification.getSmallIcon().toString());
      if (((StatusBarNotification)localObject1).isGroup())
      {
        localSpannableStringBuilder.append("\n").append(bold(getString(2131693677))).append(str2).append(((StatusBarNotification)localObject1).getGroupKey());
        if (localNotification.isGroupSummary()) {
          localSpannableStringBuilder.append(bold(getString(2131693678)));
        }
      }
      localSpannableStringBuilder.append("\n").append(bold(getString(2131693695))).append(str2);
      if ((localNotification.defaults & 0x1) != 0)
      {
        localSpannableStringBuilder.append(getString(2131693697));
        label549:
        localSpannableStringBuilder.append("\n").append(bold(getString(2131693696))).append(str2);
        if ((localNotification.defaults & 0x2) == 0) {
          break label1231;
        }
        localSpannableStringBuilder.append(getString(2131693697));
        label599:
        localSpannableStringBuilder.append("\n").append(bold(getString(2131693679))).append(str2).append(Notification.visibilityToString(localNotification.visibility));
        if (localNotification.publicVersion != null) {
          localSpannableStringBuilder.append("\n").append(bold(getString(2131693680))).append(str2).append(getTitleString(localNotification.publicVersion));
        }
        localSpannableStringBuilder.append("\n").append(bold(getString(2131693681))).append(str2).append(Notification.priorityToString(localNotification.priority));
        if (arrayOfStatusBarNotification2 == arrayOfStatusBarNotification1)
        {
          if ((this.mRanking == null) || (!this.mRanking.getRanking(((StatusBarNotification)localObject1).getKey(), localRanking))) {
            break label1302;
          }
          localSpannableStringBuilder.append("\n").append(bold(getString(2131693682))).append(str2).append(NotificationListenerService.Ranking.importanceToString(localRanking.getImportance()));
          if (localRanking.getImportanceExplanation() != null) {
            localSpannableStringBuilder.append("\n").append(bold(getString(2131693683))).append(str2).append(localRanking.getImportanceExplanation());
          }
        }
        label833:
        if (localNotification.contentIntent != null) {
          localSpannableStringBuilder.append("\n").append(bold(getString(2131693684))).append(str2).append(formatPendingIntent(localNotification.contentIntent));
        }
        if (localNotification.deleteIntent != null) {
          localSpannableStringBuilder.append("\n").append(bold(getString(2131693685))).append(str2).append(formatPendingIntent(localNotification.deleteIntent));
        }
        if (localNotification.fullScreenIntent != null) {
          localSpannableStringBuilder.append("\n").append(bold(getString(2131693686))).append(str2).append(formatPendingIntent(localNotification.fullScreenIntent));
        }
        if ((localNotification.actions == null) || (localNotification.actions.length <= 0)) {
          break label1359;
        }
        localSpannableStringBuilder.append("\n").append(bold(getString(2131693687)));
        k = 0;
      }
      for (;;)
      {
        if (k < localNotification.actions.length)
        {
          localObject1 = localNotification.actions[k];
          localSpannableStringBuilder.append("\n  ").append(String.valueOf(k)).append(' ').append(bold(getString(2131693688))).append(str2).append(((Notification.Action)localObject1).title);
          if (((Notification.Action)localObject1).actionIntent != null) {
            localSpannableStringBuilder.append("\n    ").append(bold(getString(2131693684))).append(str2).append(formatPendingIntent(((Notification.Action)localObject1).actionIntent));
          }
          if (((Notification.Action)localObject1).getRemoteInputs() == null) {
            break label1751;
          }
          localSpannableStringBuilder.append("\n    ").append(bold(getString(2131693689))).append(str2).append(String.valueOf(((Notification.Action)localObject1).getRemoteInputs().length));
          break label1751;
          if (localNotification.sound != null)
          {
            localSpannableStringBuilder.append(localNotification.sound.toString());
            break label549;
          }
          localSpannableStringBuilder.append(getString(2131693698));
          break label549;
          label1231:
          if (localNotification.vibrate != null)
          {
            k = 0;
            while (k < localNotification.vibrate.length)
            {
              if (k > 0) {
                localSpannableStringBuilder.append(',');
              }
              localSpannableStringBuilder.append(String.valueOf(localNotification.vibrate[k]));
              k += 1;
            }
            break label599;
          }
          localSpannableStringBuilder.append(getString(2131693698));
          break label599;
          label1302:
          if (this.mRanking == null)
          {
            localSpannableStringBuilder.append("\n").append(bold(getString(2131693699)));
            break label833;
          }
          localSpannableStringBuilder.append("\n").append(bold(getString(2131693700)));
          break label833;
        }
        label1359:
        if (localNotification.contentView != null) {
          localSpannableStringBuilder.append("\n").append(bold(getString(2131693690))).append(str2).append(localNotification.contentView.toString());
        }
        if ((localNotification.extras != null) && (localNotification.extras.size() > 0))
        {
          localSpannableStringBuilder.append("\n").append(bold(getString(2131693691)));
          Iterator localIterator = localNotification.extras.keySet().iterator();
          while (localIterator.hasNext())
          {
            String str3 = (String)localIterator.next();
            String str1 = String.valueOf(localNotification.extras.get(str3));
            localObject2 = str1;
            if (str1.length() > 100) {
              localObject2 = str1.substring(0, 100) + "...";
            }
            localSpannableStringBuilder.append("\n  ").append(str3).append(str2).append((CharSequence)localObject2);
          }
        }
        Object localObject2 = Parcel.obtain();
        localNotification.writeToParcel((Parcel)localObject2, 0);
        localSpannableStringBuilder.append("\n").append(bold(getString(2131693693))).append(str2).append(String.valueOf(((Parcel)localObject2).dataPosition())).append(' ').append(bold(getString(2131693694))).append(str2).append(String.valueOf(((Parcel)localObject2).getBlobAshmemSize())).append("\n");
        localHistoricalNotificationInfo.extra = localSpannableStringBuilder;
        logd("   [%d] %s: %s", new Object[] { Long.valueOf(localHistoricalNotificationInfo.timestamp), localHistoricalNotificationInfo.pkg, localHistoricalNotificationInfo.title });
        localArrayList.add(localHistoricalNotificationInfo);
        break label1733;
        label1716:
        i += 1;
        break;
        label1723:
        return localArrayList;
        for (;;)
        {
          if ((m & k) == 0) {
            break label1749;
          }
          label1733:
          j += 1;
          break;
          label1740:
          k = 0;
          break label135;
          label1745:
          m = 0;
        }
        label1749:
        break label151;
        label1751:
        k += 1;
      }
    }
  }
  
  private Drawable loadPackageIconDrawable(String paramString, int paramInt)
  {
    try
    {
      paramString = this.mPm.getApplicationIcon(paramString);
      return paramString;
    }
    catch (PackageManager.NameNotFoundException paramString)
    {
      Log.e(TAG, "Cannot get application icon", paramString);
    }
    return null;
  }
  
  private CharSequence loadPackageName(String paramString)
  {
    try
    {
      Object localObject = this.mPm.getApplicationInfo(paramString, 8192);
      if (localObject != null)
      {
        localObject = this.mPm.getApplicationLabel((ApplicationInfo)localObject);
        return (CharSequence)localObject;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.e(TAG, "Cannot load package name", localNameNotFoundException);
    }
    return paramString;
  }
  
  private static void logd(String paramString, Object... paramVarArgs) {}
  
  private void refreshList()
  {
    List localList = loadNotifications();
    if (localList != null)
    {
      int j = localList.size();
      logd("adding %d infos", new Object[] { Integer.valueOf(j) });
      Collections.sort(localList, this.mNotificationSorter);
      if (getPreferenceScreen() == null) {
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getContext()));
      }
      getPreferenceScreen().removeAll();
      int i = 0;
      while (i < j)
      {
        getPreferenceScreen().addPreference(new HistoricalNotificationPreference(getPrefContext(), (HistoricalNotificationInfo)localList.get(i)));
        i += 1;
      }
    }
  }
  
  private void scheduleRefreshList()
  {
    if (this.mHandler != null)
    {
      this.mHandler.removeCallbacks(this.mRefreshListRunnable);
      this.mHandler.postDelayed(this.mRefreshListRunnable, 100L);
    }
  }
  
  protected int getMetricsCategory()
  {
    return 75;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    logd("onActivityCreated(%s)", new Object[] { paramBundle });
    super.onActivityCreated(paramBundle);
    Utils.forceCustomPadding(getListView(), false);
  }
  
  public void onAttach(Activity paramActivity)
  {
    logd("onAttach(%s)", new Object[] { paramActivity.getClass().getSimpleName() });
    super.onAttach(paramActivity);
    this.mHandler = new Handler(paramActivity.getMainLooper());
    this.mContext = paramActivity;
    this.mPm = this.mContext.getPackageManager();
    this.mNoMan = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
  }
  
  public void onDetach()
  {
    logd("onDetach()", new Object[0]);
    this.mHandler.removeCallbacks(this.mRefreshListRunnable);
    this.mHandler = null;
    super.onDetach();
  }
  
  public void onPause()
  {
    try
    {
      this.mListener.unregisterAsSystemService();
      super.onPause();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.e(TAG, "Cannot unregister listener", localRemoteException);
      }
    }
  }
  
  public void onResume()
  {
    logd("onResume()", new Object[0]);
    super.onResume();
    try
    {
      this.mListener.registerAsSystemService(this.mContext, new ComponentName(this.mContext.getPackageName(), getClass().getCanonicalName()), ActivityManager.getCurrentUser());
      refreshList();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.e(TAG, "Cannot register listener", localRemoteException);
      }
    }
  }
  
  private static class HistoricalNotificationInfo
  {
    public boolean active;
    public CharSequence extra;
    public Drawable icon;
    public String pkg;
    public Drawable pkgicon;
    public CharSequence pkgname;
    public int priority;
    public long timestamp;
    public CharSequence title;
    public int user;
  }
  
  private static class HistoricalNotificationPreference
    extends CopyablePreference
  {
    private final NotificationStation.HistoricalNotificationInfo mInfo;
    
    public HistoricalNotificationPreference(Context paramContext, NotificationStation.HistoricalNotificationInfo paramHistoricalNotificationInfo)
    {
      super();
      setLayoutResource(2130968762);
      this.mInfo = paramHistoricalNotificationInfo;
    }
    
    public CharSequence getCopyableText()
    {
      return new SpannableStringBuilder(this.mInfo.title).append(" [").append(new Date(this.mInfo.timestamp).toString()).append("]\n").append(this.mInfo.pkgname).append("\n").append(this.mInfo.extra);
    }
    
    public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
    {
      super.onBindViewHolder(paramPreferenceViewHolder);
      if (this.mInfo.icon != null) {
        ((ImageView)paramPreferenceViewHolder.findViewById(2131361793)).setImageDrawable(this.mInfo.icon);
      }
      if (this.mInfo.pkgicon != null) {
        ((ImageView)paramPreferenceViewHolder.findViewById(2131362234)).setImageDrawable(this.mInfo.pkgicon);
      }
      ((DateTimeView)paramPreferenceViewHolder.findViewById(2131362235)).setTime(this.mInfo.timestamp);
      ((TextView)paramPreferenceViewHolder.findViewById(2131361894)).setText(this.mInfo.title);
      ((TextView)paramPreferenceViewHolder.findViewById(2131362236)).setText(this.mInfo.pkgname);
      final TextView localTextView = (TextView)paramPreferenceViewHolder.findViewById(2131362237);
      localTextView.setText(this.mInfo.extra);
      localTextView.setVisibility(8);
      paramPreferenceViewHolder.itemView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          int i = 0;
          paramAnonymousView = localTextView;
          if (localTextView.getVisibility() == 0) {
            i = 8;
          }
          paramAnonymousView.setVisibility(i);
        }
      });
      paramPreferenceViewHolder = paramPreferenceViewHolder.itemView;
      if (this.mInfo.active) {}
      for (float f = 1.0F;; f = 0.5F)
      {
        paramPreferenceViewHolder.setAlpha(f);
        return;
      }
    }
    
    public void performClick() {}
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\NotificationStation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */