package com.android.settings.applications;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog.Builder;
import android.app.ApplicationErrorReport;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.android.settings.InstrumentedFragment;
import com.android.settings.Utils;
import java.util.ArrayList;
import java.util.Collections;

public class RunningServiceDetails
  extends InstrumentedFragment
  implements RunningState.OnRefreshUiListener
{
  static final int DIALOG_CONFIRM_STOP = 1;
  static final String KEY_BACKGROUND = "background";
  static final String KEY_PROCESS = "process";
  static final String KEY_UID = "uid";
  static final String KEY_USER_ID = "user_id";
  static final String TAG = "RunningServicesDetails";
  final ArrayList<ActiveDetail> mActiveDetails = new ArrayList();
  ViewGroup mAllDetails;
  ActivityManager mAm;
  StringBuilder mBuilder = new StringBuilder(128);
  boolean mHaveData;
  LayoutInflater mInflater;
  RunningState.MergedItem mMergedItem;
  int mNumProcesses;
  int mNumServices;
  String mProcessName;
  TextView mProcessesHeader;
  View mRootView;
  TextView mServicesHeader;
  boolean mShowBackground;
  ViewGroup mSnippet;
  RunningProcessesView.ActiveItem mSnippetActiveItem;
  RunningProcessesView.ViewHolder mSnippetViewHolder;
  RunningState mState;
  int mUid;
  int mUserId;
  
  private void finish()
  {
    new Handler().post(new Runnable()
    {
      public void run()
      {
        Activity localActivity = RunningServiceDetails.this.getActivity();
        if (localActivity != null) {
          localActivity.onBackPressed();
        }
      }
    });
  }
  
  private void showConfirmStopDialog(ComponentName paramComponentName)
  {
    paramComponentName = MyAlertDialogFragment.newConfirmStop(1, paramComponentName);
    paramComponentName.setTargetFragment(this, 0);
    paramComponentName.show(getFragmentManager(), "confirmstop");
  }
  
  ActiveDetail activeDetailForService(ComponentName paramComponentName)
  {
    int i = 0;
    while (i < this.mActiveDetails.size())
    {
      ActiveDetail localActiveDetail = (ActiveDetail)this.mActiveDetails.get(i);
      if ((localActiveDetail.mServiceItem != null) && (localActiveDetail.mServiceItem.mRunningService != null) && (paramComponentName.equals(localActiveDetail.mServiceItem.mRunningService.service))) {
        return localActiveDetail;
      }
      i += 1;
    }
    return null;
  }
  
  void addDetailViews()
  {
    int i = this.mActiveDetails.size() - 1;
    while (i >= 0)
    {
      this.mAllDetails.removeView(((ActiveDetail)this.mActiveDetails.get(i)).mRootView);
      i -= 1;
    }
    this.mActiveDetails.clear();
    if (this.mServicesHeader != null)
    {
      this.mAllDetails.removeView(this.mServicesHeader);
      this.mServicesHeader = null;
    }
    if (this.mProcessesHeader != null)
    {
      this.mAllDetails.removeView(this.mProcessesHeader);
      this.mProcessesHeader = null;
    }
    this.mNumProcesses = 0;
    this.mNumServices = 0;
    if (this.mMergedItem != null)
    {
      if (this.mMergedItem.mUser != null)
      {
        ArrayList localArrayList;
        if (this.mShowBackground)
        {
          localArrayList = new ArrayList(this.mMergedItem.mChildren);
          Collections.sort(localArrayList, this.mState.mBackgroundComparator);
        }
        for (;;)
        {
          i = 0;
          while (i < localArrayList.size())
          {
            addDetailsViews((RunningState.MergedItem)localArrayList.get(i), true, false);
            i += 1;
          }
          localArrayList = this.mMergedItem.mChildren;
        }
        i = 0;
        while (i < localArrayList.size())
        {
          addDetailsViews((RunningState.MergedItem)localArrayList.get(i), false, true);
          i += 1;
        }
      }
      addDetailsViews(this.mMergedItem, true, true);
    }
  }
  
  void addDetailsViews(RunningState.MergedItem paramMergedItem, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramMergedItem != null)
    {
      if (paramBoolean1)
      {
        i = 0;
        while (i < paramMergedItem.mServices.size())
        {
          addServiceDetailsView((RunningState.ServiceItem)paramMergedItem.mServices.get(i), paramMergedItem, true, true);
          i += 1;
        }
      }
      if (paramBoolean2)
      {
        if (paramMergedItem.mServices.size() > 0) {
          break label91;
        }
        if (paramMergedItem.mUserId == UserHandle.myUserId()) {
          break label86;
        }
      }
    }
    label86:
    for (paramBoolean1 = true;; paramBoolean1 = false)
    {
      addServiceDetailsView(null, paramMergedItem, false, paramBoolean1);
      return;
    }
    label91:
    int i = -1;
    label94:
    if (i < paramMergedItem.mOtherProcesses.size()) {
      if (i >= 0) {
        break label139;
      }
    }
    label139:
    for (RunningState.ProcessItem localProcessItem = paramMergedItem.mProcess;; localProcessItem = (RunningState.ProcessItem)paramMergedItem.mOtherProcesses.get(i))
    {
      if ((localProcessItem == null) || (localProcessItem.mPid > 0)) {
        break label156;
      }
      i += 1;
      break label94;
      break;
    }
    label156:
    if (i < 0) {}
    for (paramBoolean1 = true;; paramBoolean1 = false)
    {
      addProcessDetailsView(localProcessItem, paramBoolean1);
      break;
    }
  }
  
  void addProcessDetailsView(RunningState.ProcessItem paramProcessItem, boolean paramBoolean)
  {
    addProcessesHeader();
    ActiveDetail localActiveDetail = new ActiveDetail();
    View localView = this.mInflater.inflate(2130968963, this.mAllDetails, false);
    this.mAllDetails.addView(localView);
    localActiveDetail.mRootView = localView;
    localActiveDetail.mViewHolder = new RunningProcessesView.ViewHolder(localView);
    localActiveDetail.mActiveItem = localActiveDetail.mViewHolder.bind(this.mState, paramProcessItem, this.mBuilder);
    TextView localTextView = (TextView)localView.findViewById(2131362528);
    if (paramProcessItem.mUserId != UserHandle.myUserId()) {
      localTextView.setVisibility(8);
    }
    for (;;)
    {
      this.mActiveDetails.add(localActiveDetail);
      return;
      if (!paramBoolean) {
        break;
      }
      localTextView.setText(2131692218);
    }
    int i = 0;
    localView = null;
    ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo = paramProcessItem.mRunningProcessInfo;
    paramProcessItem = localRunningAppProcessInfo.importanceReasonComponent;
    switch (localRunningAppProcessInfo.importanceReasonCode)
    {
    default: 
      paramProcessItem = localView;
    }
    int j;
    while ((i != 0) && (paramProcessItem != null))
    {
      localTextView.setText(getActivity().getString(i, new Object[] { paramProcessItem }));
      break;
      j = 2131692220;
      paramProcessItem = localView;
      i = j;
      if (localRunningAppProcessInfo.importanceReasonComponent != null)
      {
        try
        {
          paramProcessItem = getActivity().getPackageManager().getProviderInfo(localRunningAppProcessInfo.importanceReasonComponent, 0);
          paramProcessItem = RunningState.makeLabel(getActivity().getPackageManager(), paramProcessItem.name, paramProcessItem);
          i = j;
        }
        catch (PackageManager.NameNotFoundException paramProcessItem)
        {
          paramProcessItem = localView;
          i = j;
        }
        j = 2131692219;
        paramProcessItem = localView;
        i = j;
        if (localRunningAppProcessInfo.importanceReasonComponent != null) {
          try
          {
            paramProcessItem = getActivity().getPackageManager().getServiceInfo(localRunningAppProcessInfo.importanceReasonComponent, 0);
            paramProcessItem = RunningState.makeLabel(getActivity().getPackageManager(), paramProcessItem.name, paramProcessItem);
            i = j;
          }
          catch (PackageManager.NameNotFoundException paramProcessItem)
          {
            paramProcessItem = localView;
            i = j;
          }
        }
      }
    }
  }
  
  void addProcessesHeader()
  {
    if (this.mNumProcesses == 0)
    {
      this.mProcessesHeader = ((TextView)this.mInflater.inflate(2130968981, this.mAllDetails, false));
      this.mProcessesHeader.setText(2131692211);
      this.mAllDetails.addView(this.mProcessesHeader);
    }
    this.mNumProcesses += 1;
  }
  
  void addServiceDetailsView(RunningState.ServiceItem paramServiceItem, RunningState.MergedItem paramMergedItem, boolean paramBoolean1, boolean paramBoolean2)
  {
    Object localObject;
    label15:
    ActiveDetail localActiveDetail;
    if (paramBoolean1)
    {
      addServicesHeader();
      if (paramServiceItem == null) {
        break label259;
      }
      localObject = paramServiceItem;
      localActiveDetail = new ActiveDetail();
      View localView = this.mInflater.inflate(2130968964, this.mAllDetails, false);
      this.mAllDetails.addView(localView);
      localActiveDetail.mRootView = localView;
      localActiveDetail.mServiceItem = paramServiceItem;
      localActiveDetail.mViewHolder = new RunningProcessesView.ViewHolder(localView);
      localActiveDetail.mActiveItem = localActiveDetail.mViewHolder.bind(this.mState, (RunningState.BaseItem)localObject, this.mBuilder);
      if (!paramBoolean2) {
        localView.findViewById(2131362527).setVisibility(8);
      }
      if ((paramServiceItem != null) && (paramServiceItem.mRunningService.clientLabel != 0)) {
        localActiveDetail.mManageIntent = this.mAm.getRunningServiceControlPanel(paramServiceItem.mRunningService.service);
      }
      localObject = (TextView)localView.findViewById(2131362528);
      localActiveDetail.mStopButton = ((Button)localView.findViewById(2131362644));
      localActiveDetail.mReportButton = ((Button)localView.findViewById(2131362282));
      if ((!paramBoolean1) || (paramMergedItem.mUserId == UserHandle.myUserId())) {
        break label265;
      }
      ((TextView)localObject).setVisibility(8);
      localView.findViewById(2131362178).setVisibility(8);
    }
    for (;;)
    {
      this.mActiveDetails.add(localActiveDetail);
      return;
      if (paramMergedItem.mUserId == UserHandle.myUserId()) {
        break;
      }
      addProcessesHeader();
      break;
      label259:
      localObject = paramMergedItem;
      break label15;
      label265:
      label315:
      int i;
      if ((paramServiceItem != null) && (paramServiceItem.mServiceInfo.descriptionRes != 0))
      {
        ((TextView)localObject).setText(getActivity().getPackageManager().getText(paramServiceItem.mServiceInfo.packageName, paramServiceItem.mServiceInfo.descriptionRes, paramServiceItem.mServiceInfo.applicationInfo));
        localActiveDetail.mStopButton.setOnClickListener(localActiveDetail);
        paramMergedItem = localActiveDetail.mStopButton;
        localObject = getActivity();
        if (localActiveDetail.mManageIntent == null) {
          break label576;
        }
        i = 2131692213;
        label350:
        paramMergedItem.setText(((Activity)localObject).getText(i));
        localActiveDetail.mReportButton.setOnClickListener(localActiveDetail);
        localActiveDetail.mReportButton.setText(17040308);
        if ((Settings.Global.getInt(getActivity().getContentResolver(), "send_action_app_error", 0) == 0) || (paramServiceItem == null)) {
          break label589;
        }
        localActiveDetail.mInstaller = ApplicationErrorReport.getErrorReportReceiver(getActivity(), paramServiceItem.mServiceInfo.packageName, paramServiceItem.mServiceInfo.applicationInfo.flags);
        paramServiceItem = localActiveDetail.mReportButton;
        if (localActiveDetail.mInstaller == null) {
          break label584;
        }
      }
      label576:
      label584:
      for (paramBoolean1 = true;; paramBoolean1 = false)
      {
        paramServiceItem.setEnabled(paramBoolean1);
        break;
        if (paramMergedItem.mBackground)
        {
          ((TextView)localObject).setText(2131692216);
          break label315;
        }
        if (localActiveDetail.mManageIntent != null)
        {
          try
          {
            paramMergedItem = getActivity().getPackageManager().getResourcesForApplication(paramServiceItem.mRunningService.clientPackage).getString(paramServiceItem.mRunningService.clientLabel);
            ((TextView)localObject).setText(getActivity().getString(2131692217, new Object[] { paramMergedItem }));
          }
          catch (PackageManager.NameNotFoundException paramMergedItem) {}
          break label315;
        }
        paramMergedItem = getActivity();
        if (paramServiceItem != null) {}
        for (i = 2131692214;; i = 2131692215)
        {
          ((TextView)localObject).setText(paramMergedItem.getText(i));
          break;
        }
        i = 2131692212;
        break label350;
      }
      label589:
      localActiveDetail.mReportButton.setEnabled(false);
    }
  }
  
  void addServicesHeader()
  {
    if (this.mNumServices == 0)
    {
      this.mServicesHeader = ((TextView)this.mInflater.inflate(2130968981, this.mAllDetails, false));
      this.mServicesHeader.setText(2131692210);
      this.mAllDetails.addView(this.mServicesHeader);
    }
    this.mNumServices += 1;
  }
  
  void ensureData()
  {
    if (!this.mHaveData)
    {
      this.mHaveData = true;
      this.mState.resume(this);
      this.mState.waitForData();
      refreshUi(true);
    }
  }
  
  boolean findMergedItem()
  {
    Object localObject2 = null;
    ArrayList localArrayList;
    Object localObject1;
    int i;
    if (this.mShowBackground)
    {
      localArrayList = this.mState.getCurrentBackgroundItems();
      localObject1 = localObject2;
      if (localArrayList == null) {
        break label135;
      }
      i = 0;
      label27:
      localObject1 = localObject2;
      if (i >= localArrayList.size()) {
        break label135;
      }
      localObject1 = (RunningState.MergedItem)localArrayList.get(i);
      if (((RunningState.MergedItem)localObject1).mUserId == this.mUserId) {
        break label76;
      }
    }
    label76:
    while (((this.mUid >= 0) && (((RunningState.MergedItem)localObject1).mProcess != null) && (((RunningState.MergedItem)localObject1).mProcess.mUid != this.mUid)) || ((this.mProcessName != null) && ((((RunningState.MergedItem)localObject1).mProcess == null) || (!this.mProcessName.equals(((RunningState.MergedItem)localObject1).mProcess.mProcessName)))))
    {
      i += 1;
      break label27;
      localArrayList = this.mState.getCurrentMergedItems();
      break;
    }
    label135:
    if (this.mMergedItem != localObject1)
    {
      this.mMergedItem = ((RunningState.MergedItem)localObject1);
      return true;
    }
    return false;
  }
  
  protected int getMetricsCategory()
  {
    return 85;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mUid = getArguments().getInt("uid", -1);
    this.mUserId = getArguments().getInt("user_id", 0);
    this.mProcessName = getArguments().getString("process", null);
    this.mShowBackground = getArguments().getBoolean("background", false);
    this.mAm = ((ActivityManager)getActivity().getSystemService("activity"));
    this.mInflater = ((LayoutInflater)getActivity().getSystemService("layout_inflater"));
    this.mState = RunningState.getInstance(getActivity());
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = paramLayoutInflater.inflate(2130968962, paramViewGroup, false);
    Utils.prepareCustomPreferencesList(paramViewGroup, paramLayoutInflater, paramLayoutInflater, false);
    this.mRootView = paramLayoutInflater;
    this.mAllDetails = ((ViewGroup)paramLayoutInflater.findViewById(2131361954));
    this.mSnippet = ((ViewGroup)paramLayoutInflater.findViewById(2131362526));
    this.mSnippetViewHolder = new RunningProcessesView.ViewHolder(this.mSnippet);
    ensureData();
    return paramLayoutInflater;
  }
  
  public void onPause()
  {
    super.onPause();
    this.mHaveData = false;
    this.mState.pause();
  }
  
  public void onRefreshUi(int paramInt)
  {
    if (getActivity() == null) {
      return;
    }
    switch (paramInt)
    {
    default: 
      return;
    case 0: 
      updateTimes();
      return;
    case 1: 
      refreshUi(false);
      updateTimes();
      return;
    }
    refreshUi(true);
    updateTimes();
  }
  
  public void onResume()
  {
    super.onResume();
    ensureData();
  }
  
  void refreshUi(boolean paramBoolean)
  {
    if (findMergedItem()) {
      paramBoolean = true;
    }
    if (paramBoolean)
    {
      if (this.mMergedItem == null) {
        break label48;
      }
      this.mSnippetActiveItem = this.mSnippetViewHolder.bind(this.mState, this.mMergedItem, this.mBuilder);
    }
    for (;;)
    {
      addDetailViews();
      return;
      label48:
      if (this.mSnippetActiveItem == null) {
        break;
      }
      this.mSnippetActiveItem.mHolder.size.setText("");
      this.mSnippetActiveItem.mHolder.uptime.setText("");
      this.mSnippetActiveItem.mHolder.description.setText(2131692209);
    }
    finish();
  }
  
  void updateTimes()
  {
    if (this.mSnippetActiveItem != null) {
      this.mSnippetActiveItem.updateTime(getActivity(), this.mBuilder);
    }
    int i = 0;
    while (i < this.mActiveDetails.size())
    {
      ((ActiveDetail)this.mActiveDetails.get(i)).mActiveItem.updateTime(getActivity(), this.mBuilder);
      i += 1;
    }
  }
  
  class ActiveDetail
    implements View.OnClickListener
  {
    RunningProcessesView.ActiveItem mActiveItem;
    ComponentName mInstaller;
    PendingIntent mManageIntent;
    Button mReportButton;
    View mRootView;
    RunningState.ServiceItem mServiceItem;
    Button mStopButton;
    RunningProcessesView.ViewHolder mViewHolder;
    
    ActiveDetail() {}
    
    /* Error */
    public void onClick(View paramView)
    {
      // Byte code:
      //   0: aload_1
      //   1: aload_0
      //   2: getfield 46	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mReportButton	Landroid/widget/Button;
      //   5: if_acmpne +538 -> 543
      //   8: new 48	android/app/ApplicationErrorReport
      //   11: dup
      //   12: invokespecial 49	android/app/ApplicationErrorReport:<init>	()V
      //   15: astore 5
      //   17: aload 5
      //   19: iconst_5
      //   20: putfield 53	android/app/ApplicationErrorReport:type	I
      //   23: aload 5
      //   25: aload_0
      //   26: getfield 55	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mServiceItem	Lcom/android/settings/applications/RunningState$ServiceItem;
      //   29: getfield 61	com/android/settings/applications/RunningState$ServiceItem:mServiceInfo	Landroid/content/pm/ServiceInfo;
      //   32: getfield 67	android/content/pm/ServiceInfo:packageName	Ljava/lang/String;
      //   35: putfield 68	android/app/ApplicationErrorReport:packageName	Ljava/lang/String;
      //   38: aload 5
      //   40: aload_0
      //   41: getfield 70	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mInstaller	Landroid/content/ComponentName;
      //   44: invokevirtual 76	android/content/ComponentName:getPackageName	()Ljava/lang/String;
      //   47: putfield 79	android/app/ApplicationErrorReport:installerPackageName	Ljava/lang/String;
      //   50: aload 5
      //   52: aload_0
      //   53: getfield 55	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mServiceItem	Lcom/android/settings/applications/RunningState$ServiceItem;
      //   56: getfield 83	com/android/settings/applications/RunningState$ServiceItem:mRunningService	Landroid/app/ActivityManager$RunningServiceInfo;
      //   59: getfield 88	android/app/ActivityManager$RunningServiceInfo:process	Ljava/lang/String;
      //   62: putfield 91	android/app/ApplicationErrorReport:processName	Ljava/lang/String;
      //   65: aload 5
      //   67: invokestatic 97	java/lang/System:currentTimeMillis	()J
      //   70: putfield 101	android/app/ApplicationErrorReport:time	J
      //   73: aload_0
      //   74: getfield 55	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mServiceItem	Lcom/android/settings/applications/RunningState$ServiceItem;
      //   77: getfield 61	com/android/settings/applications/RunningState$ServiceItem:mServiceInfo	Landroid/content/pm/ServiceInfo;
      //   80: getfield 105	android/content/pm/ServiceInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
      //   83: getfield 110	android/content/pm/ApplicationInfo:flags	I
      //   86: iconst_1
      //   87: iand
      //   88: ifeq +283 -> 371
      //   91: iconst_1
      //   92: istore_2
      //   93: aload 5
      //   95: iload_2
      //   96: putfield 114	android/app/ApplicationErrorReport:systemApp	Z
      //   99: new 116	android/app/ApplicationErrorReport$RunningServiceInfo
      //   102: dup
      //   103: invokespecial 117	android/app/ApplicationErrorReport$RunningServiceInfo:<init>	()V
      //   106: astore 6
      //   108: aload_0
      //   109: getfield 119	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mActiveItem	Lcom/android/settings/applications/RunningProcessesView$ActiveItem;
      //   112: getfield 124	com/android/settings/applications/RunningProcessesView$ActiveItem:mFirstRunTime	J
      //   115: lconst_0
      //   116: lcmp
      //   117: iflt +259 -> 376
      //   120: aload 6
      //   122: invokestatic 129	android/os/SystemClock:elapsedRealtime	()J
      //   125: aload_0
      //   126: getfield 119	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mActiveItem	Lcom/android/settings/applications/RunningProcessesView$ActiveItem;
      //   129: getfield 124	com/android/settings/applications/RunningProcessesView$ActiveItem:mFirstRunTime	J
      //   132: lsub
      //   133: putfield 132	android/app/ApplicationErrorReport$RunningServiceInfo:durationMillis	J
      //   136: new 72	android/content/ComponentName
      //   139: dup
      //   140: aload_0
      //   141: getfield 55	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mServiceItem	Lcom/android/settings/applications/RunningState$ServiceItem;
      //   144: getfield 61	com/android/settings/applications/RunningState$ServiceItem:mServiceInfo	Landroid/content/pm/ServiceInfo;
      //   147: getfield 67	android/content/pm/ServiceInfo:packageName	Ljava/lang/String;
      //   150: aload_0
      //   151: getfield 55	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mServiceItem	Lcom/android/settings/applications/RunningState$ServiceItem;
      //   154: getfield 61	com/android/settings/applications/RunningState$ServiceItem:mServiceInfo	Landroid/content/pm/ServiceInfo;
      //   157: getfield 135	android/content/pm/ServiceInfo:name	Ljava/lang/String;
      //   160: invokespecial 138	android/content/ComponentName:<init>	(Ljava/lang/String;Ljava/lang/String;)V
      //   163: astore 8
      //   165: aload_0
      //   166: getfield 30	com/android/settings/applications/RunningServiceDetails$ActiveDetail:this$0	Lcom/android/settings/applications/RunningServiceDetails;
      //   169: invokevirtual 142	com/android/settings/applications/RunningServiceDetails:getActivity	()Landroid/app/Activity;
      //   172: ldc -112
      //   174: invokevirtual 150	android/app/Activity:getFileStreamPath	(Ljava/lang/String;)Ljava/io/File;
      //   177: astore 7
      //   179: aconst_null
      //   180: astore_1
      //   181: aconst_null
      //   182: astore 4
      //   184: new 152	java/io/FileOutputStream
      //   187: dup
      //   188: aload 7
      //   190: invokespecial 155	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
      //   193: astore_3
      //   194: ldc -99
      //   196: aload_3
      //   197: invokevirtual 161	java/io/FileOutputStream:getFD	()Ljava/io/FileDescriptor;
      //   200: iconst_3
      //   201: anewarray 163	java/lang/String
      //   204: dup
      //   205: iconst_0
      //   206: ldc -91
      //   208: aastore
      //   209: dup
      //   210: iconst_1
      //   211: ldc -89
      //   213: aastore
      //   214: dup
      //   215: iconst_2
      //   216: aload 8
      //   218: invokevirtual 170	android/content/ComponentName:flattenToString	()Ljava/lang/String;
      //   221: aastore
      //   222: invokestatic 176	android/os/Debug:dumpService	(Ljava/lang/String;Ljava/io/FileDescriptor;[Ljava/lang/String;)Z
      //   225: pop
      //   226: aload_3
      //   227: ifnull +7 -> 234
      //   230: aload_3
      //   231: invokevirtual 179	java/io/FileOutputStream:close	()V
      //   234: aconst_null
      //   235: astore_1
      //   236: aconst_null
      //   237: astore 4
      //   239: new 181	java/io/FileInputStream
      //   242: dup
      //   243: aload 7
      //   245: invokespecial 182	java/io/FileInputStream:<init>	(Ljava/io/File;)V
      //   248: astore_3
      //   249: aload 7
      //   251: invokevirtual 187	java/io/File:length	()J
      //   254: l2i
      //   255: newarray <illegal type>
      //   257: astore_1
      //   258: aload_3
      //   259: aload_1
      //   260: invokevirtual 191	java/io/FileInputStream:read	([B)I
      //   263: pop
      //   264: aload 6
      //   266: new 163	java/lang/String
      //   269: dup
      //   270: aload_1
      //   271: invokespecial 194	java/lang/String:<init>	([B)V
      //   274: putfield 197	android/app/ApplicationErrorReport$RunningServiceInfo:serviceDetails	Ljava/lang/String;
      //   277: aload_3
      //   278: ifnull +7 -> 285
      //   281: aload_3
      //   282: invokevirtual 198	java/io/FileInputStream:close	()V
      //   285: aload 7
      //   287: invokevirtual 202	java/io/File:delete	()Z
      //   290: pop
      //   291: ldc -52
      //   293: new 206	java/lang/StringBuilder
      //   296: dup
      //   297: invokespecial 207	java/lang/StringBuilder:<init>	()V
      //   300: ldc -47
      //   302: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   305: aload 6
      //   307: getfield 197	android/app/ApplicationErrorReport$RunningServiceInfo:serviceDetails	Ljava/lang/String;
      //   310: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   313: invokevirtual 216	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   316: invokestatic 222	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
      //   319: pop
      //   320: aload 5
      //   322: aload 6
      //   324: putfield 226	android/app/ApplicationErrorReport:runningServiceInfo	Landroid/app/ApplicationErrorReport$RunningServiceInfo;
      //   327: new 228	android/content/Intent
      //   330: dup
      //   331: ldc -26
      //   333: invokespecial 233	android/content/Intent:<init>	(Ljava/lang/String;)V
      //   336: astore_1
      //   337: aload_1
      //   338: aload_0
      //   339: getfield 70	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mInstaller	Landroid/content/ComponentName;
      //   342: invokevirtual 237	android/content/Intent:setComponent	(Landroid/content/ComponentName;)Landroid/content/Intent;
      //   345: pop
      //   346: aload_1
      //   347: ldc -17
      //   349: aload 5
      //   351: invokevirtual 243	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
      //   354: pop
      //   355: aload_1
      //   356: ldc -12
      //   358: invokevirtual 248	android/content/Intent:addFlags	(I)Landroid/content/Intent;
      //   361: pop
      //   362: aload_0
      //   363: getfield 30	com/android/settings/applications/RunningServiceDetails$ActiveDetail:this$0	Lcom/android/settings/applications/RunningServiceDetails;
      //   366: aload_1
      //   367: invokevirtual 252	com/android/settings/applications/RunningServiceDetails:startActivity	(Landroid/content/Intent;)V
      //   370: return
      //   371: iconst_0
      //   372: istore_2
      //   373: goto -280 -> 93
      //   376: aload 6
      //   378: ldc2_w 253
      //   381: putfield 132	android/app/ApplicationErrorReport$RunningServiceInfo:durationMillis	J
      //   384: goto -248 -> 136
      //   387: astore_1
      //   388: goto -154 -> 234
      //   391: astore_1
      //   392: aload 4
      //   394: astore_3
      //   395: aload_1
      //   396: astore 4
      //   398: aload_3
      //   399: astore_1
      //   400: ldc -52
      //   402: new 206	java/lang/StringBuilder
      //   405: dup
      //   406: invokespecial 207	java/lang/StringBuilder:<init>	()V
      //   409: ldc_w 256
      //   412: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   415: aload 8
      //   417: invokevirtual 259	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   420: invokevirtual 216	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   423: aload 4
      //   425: invokestatic 263	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   428: pop
      //   429: aload_3
      //   430: ifnull -196 -> 234
      //   433: aload_3
      //   434: invokevirtual 179	java/io/FileOutputStream:close	()V
      //   437: goto -203 -> 234
      //   440: astore_1
      //   441: goto -207 -> 234
      //   444: astore 4
      //   446: aload_1
      //   447: astore_3
      //   448: aload 4
      //   450: astore_1
      //   451: aload_3
      //   452: ifnull +7 -> 459
      //   455: aload_3
      //   456: invokevirtual 179	java/io/FileOutputStream:close	()V
      //   459: aload_1
      //   460: athrow
      //   461: astore_3
      //   462: goto -3 -> 459
      //   465: astore_1
      //   466: goto -181 -> 285
      //   469: astore_1
      //   470: aload 4
      //   472: astore_3
      //   473: aload_1
      //   474: astore 4
      //   476: aload_3
      //   477: astore_1
      //   478: ldc -52
      //   480: new 206	java/lang/StringBuilder
      //   483: dup
      //   484: invokespecial 207	java/lang/StringBuilder:<init>	()V
      //   487: ldc_w 265
      //   490: invokevirtual 213	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   493: aload 8
      //   495: invokevirtual 259	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   498: invokevirtual 216	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   501: aload 4
      //   503: invokestatic 263	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   506: pop
      //   507: aload_3
      //   508: ifnull -223 -> 285
      //   511: aload_3
      //   512: invokevirtual 198	java/io/FileInputStream:close	()V
      //   515: goto -230 -> 285
      //   518: astore_1
      //   519: goto -234 -> 285
      //   522: astore 4
      //   524: aload_1
      //   525: astore_3
      //   526: aload 4
      //   528: astore_1
      //   529: aload_3
      //   530: ifnull +7 -> 537
      //   533: aload_3
      //   534: invokevirtual 198	java/io/FileInputStream:close	()V
      //   537: aload_1
      //   538: athrow
      //   539: astore_3
      //   540: goto -3 -> 537
      //   543: aload_0
      //   544: getfield 267	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mManageIntent	Landroid/app/PendingIntent;
      //   547: ifnull +56 -> 603
      //   550: aload_0
      //   551: getfield 30	com/android/settings/applications/RunningServiceDetails$ActiveDetail:this$0	Lcom/android/settings/applications/RunningServiceDetails;
      //   554: invokevirtual 142	com/android/settings/applications/RunningServiceDetails:getActivity	()Landroid/app/Activity;
      //   557: aload_0
      //   558: getfield 267	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mManageIntent	Landroid/app/PendingIntent;
      //   561: invokevirtual 273	android/app/PendingIntent:getIntentSender	()Landroid/content/IntentSender;
      //   564: aconst_null
      //   565: ldc_w 274
      //   568: ldc_w 275
      //   571: iconst_0
      //   572: invokevirtual 279	android/app/Activity:startIntentSender	(Landroid/content/IntentSender;Landroid/content/Intent;III)V
      //   575: return
      //   576: astore_1
      //   577: ldc -52
      //   579: aload_1
      //   580: invokestatic 282	android/util/Log:w	(Ljava/lang/String;Ljava/lang/Throwable;)I
      //   583: pop
      //   584: return
      //   585: astore_1
      //   586: ldc -52
      //   588: aload_1
      //   589: invokestatic 282	android/util/Log:w	(Ljava/lang/String;Ljava/lang/Throwable;)I
      //   592: pop
      //   593: return
      //   594: astore_1
      //   595: ldc -52
      //   597: aload_1
      //   598: invokestatic 282	android/util/Log:w	(Ljava/lang/String;Ljava/lang/Throwable;)I
      //   601: pop
      //   602: return
      //   603: aload_0
      //   604: getfield 55	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mServiceItem	Lcom/android/settings/applications/RunningState$ServiceItem;
      //   607: ifnull +9 -> 616
      //   610: aload_0
      //   611: iconst_0
      //   612: invokevirtual 286	com/android/settings/applications/RunningServiceDetails$ActiveDetail:stopActiveService	(Z)V
      //   615: return
      //   616: aload_0
      //   617: getfield 119	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mActiveItem	Lcom/android/settings/applications/RunningProcessesView$ActiveItem;
      //   620: getfield 290	com/android/settings/applications/RunningProcessesView$ActiveItem:mItem	Lcom/android/settings/applications/RunningState$BaseItem;
      //   623: getfield 295	com/android/settings/applications/RunningState$BaseItem:mBackground	Z
      //   626: ifeq +34 -> 660
      //   629: aload_0
      //   630: getfield 30	com/android/settings/applications/RunningServiceDetails$ActiveDetail:this$0	Lcom/android/settings/applications/RunningServiceDetails;
      //   633: getfield 299	com/android/settings/applications/RunningServiceDetails:mAm	Landroid/app/ActivityManager;
      //   636: aload_0
      //   637: getfield 119	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mActiveItem	Lcom/android/settings/applications/RunningProcessesView$ActiveItem;
      //   640: getfield 290	com/android/settings/applications/RunningProcessesView$ActiveItem:mItem	Lcom/android/settings/applications/RunningState$BaseItem;
      //   643: getfield 303	com/android/settings/applications/RunningState$BaseItem:mPackageInfo	Landroid/content/pm/PackageItemInfo;
      //   646: getfield 306	android/content/pm/PackageItemInfo:packageName	Ljava/lang/String;
      //   649: invokevirtual 311	android/app/ActivityManager:killBackgroundProcesses	(Ljava/lang/String;)V
      //   652: aload_0
      //   653: getfield 30	com/android/settings/applications/RunningServiceDetails$ActiveDetail:this$0	Lcom/android/settings/applications/RunningServiceDetails;
      //   656: invokestatic 314	com/android/settings/applications/RunningServiceDetails:-wrap0	(Lcom/android/settings/applications/RunningServiceDetails;)V
      //   659: return
      //   660: aload_0
      //   661: getfield 30	com/android/settings/applications/RunningServiceDetails$ActiveDetail:this$0	Lcom/android/settings/applications/RunningServiceDetails;
      //   664: getfield 299	com/android/settings/applications/RunningServiceDetails:mAm	Landroid/app/ActivityManager;
      //   667: aload_0
      //   668: getfield 119	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mActiveItem	Lcom/android/settings/applications/RunningProcessesView$ActiveItem;
      //   671: getfield 290	com/android/settings/applications/RunningProcessesView$ActiveItem:mItem	Lcom/android/settings/applications/RunningState$BaseItem;
      //   674: getfield 303	com/android/settings/applications/RunningState$BaseItem:mPackageInfo	Landroid/content/pm/PackageItemInfo;
      //   677: getfield 306	android/content/pm/PackageItemInfo:packageName	Ljava/lang/String;
      //   680: invokevirtual 317	android/app/ActivityManager:forceStopPackage	(Ljava/lang/String;)V
      //   683: aload_0
      //   684: getfield 30	com/android/settings/applications/RunningServiceDetails$ActiveDetail:this$0	Lcom/android/settings/applications/RunningServiceDetails;
      //   687: invokestatic 314	com/android/settings/applications/RunningServiceDetails:-wrap0	(Lcom/android/settings/applications/RunningServiceDetails;)V
      //   690: return
      //   691: astore_1
      //   692: goto -163 -> 529
      //   695: astore 4
      //   697: goto -221 -> 476
      //   700: astore_1
      //   701: goto -250 -> 451
      //   704: astore 4
      //   706: goto -308 -> 398
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	709	0	this	ActiveDetail
      //   0	709	1	paramView	View
      //   92	281	2	bool	boolean
      //   193	263	3	localObject1	Object
      //   461	1	3	localIOException1	java.io.IOException
      //   472	62	3	localObject2	Object
      //   539	1	3	localIOException2	java.io.IOException
      //   182	242	4	localView1	View
      //   444	27	4	localObject3	Object
      //   474	28	4	localView2	View
      //   522	5	4	localObject4	Object
      //   695	1	4	localIOException3	java.io.IOException
      //   704	1	4	localIOException4	java.io.IOException
      //   15	335	5	localApplicationErrorReport	ApplicationErrorReport
      //   106	271	6	localRunningServiceInfo	android.app.ApplicationErrorReport.RunningServiceInfo
      //   177	109	7	localFile	java.io.File
      //   163	331	8	localComponentName	ComponentName
      // Exception table:
      //   from	to	target	type
      //   230	234	387	java/io/IOException
      //   184	194	391	java/io/IOException
      //   433	437	440	java/io/IOException
      //   184	194	444	finally
      //   400	429	444	finally
      //   455	459	461	java/io/IOException
      //   281	285	465	java/io/IOException
      //   239	249	469	java/io/IOException
      //   511	515	518	java/io/IOException
      //   239	249	522	finally
      //   478	507	522	finally
      //   533	537	539	java/io/IOException
      //   550	575	576	android/content/ActivityNotFoundException
      //   550	575	585	java/lang/IllegalArgumentException
      //   550	575	594	android/content/IntentSender$SendIntentException
      //   249	277	691	finally
      //   249	277	695	java/io/IOException
      //   194	226	700	finally
      //   194	226	704	java/io/IOException
    }
    
    void stopActiveService(boolean paramBoolean)
    {
      RunningState.ServiceItem localServiceItem = this.mServiceItem;
      if ((!paramBoolean) && ((localServiceItem.mServiceInfo.applicationInfo.flags & 0x1) != 0))
      {
        RunningServiceDetails.-wrap1(RunningServiceDetails.this, localServiceItem.mRunningService.service);
        return;
      }
      RunningServiceDetails.this.getActivity().stopService(new Intent().setComponent(localServiceItem.mRunningService.service));
      if (RunningServiceDetails.this.mMergedItem == null)
      {
        RunningServiceDetails.this.mState.updateNow();
        RunningServiceDetails.-wrap0(RunningServiceDetails.this);
        return;
      }
      if ((!RunningServiceDetails.this.mShowBackground) && (RunningServiceDetails.this.mMergedItem.mServices.size() <= 1))
      {
        RunningServiceDetails.this.mState.updateNow();
        RunningServiceDetails.-wrap0(RunningServiceDetails.this);
        return;
      }
      RunningServiceDetails.this.mState.updateNow();
    }
  }
  
  public static class MyAlertDialogFragment
    extends DialogFragment
  {
    public static MyAlertDialogFragment newConfirmStop(int paramInt, ComponentName paramComponentName)
    {
      MyAlertDialogFragment localMyAlertDialogFragment = new MyAlertDialogFragment();
      Bundle localBundle = new Bundle();
      localBundle.putInt("id", paramInt);
      localBundle.putParcelable("comp", paramComponentName);
      localMyAlertDialogFragment.setArguments(localBundle);
      return localMyAlertDialogFragment;
    }
    
    RunningServiceDetails getOwner()
    {
      return (RunningServiceDetails)getTargetFragment();
    }
    
    public Dialog onCreateDialog(final Bundle paramBundle)
    {
      int i = getArguments().getInt("id");
      switch (i)
      {
      default: 
        throw new IllegalArgumentException("unknown id " + i);
      }
      paramBundle = (ComponentName)getArguments().getParcelable("comp");
      if (getOwner().activeDetailForService(paramBundle) == null) {
        return null;
      }
      new AlertDialog.Builder(getActivity()).setTitle(getActivity().getString(2131692221)).setMessage(getActivity().getString(2131692222)).setPositiveButton(2131692133, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          paramAnonymousDialogInterface = RunningServiceDetails.MyAlertDialogFragment.this.getOwner().activeDetailForService(paramBundle);
          if (paramAnonymousDialogInterface != null) {
            paramAnonymousDialogInterface.stopActiveService(true);
          }
        }
      }).setNegativeButton(2131692134, null).create();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\RunningServiceDetails.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */