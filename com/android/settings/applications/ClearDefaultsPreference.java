package com.android.settings.applications;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.IUsbManager.Stub;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.ApplicationsState.AppEntry;

public class ClearDefaultsPreference
  extends Preference
{
  protected static final String TAG = ClearDefaultsPreference.class.getSimpleName();
  private Button mActivitiesButton;
  protected ApplicationsState.AppEntry mAppEntry;
  private AppWidgetManager mAppWidgetManager;
  private String mPackageName;
  private PackageManager mPm;
  private IUsbManager mUsbManager;
  
  public ClearDefaultsPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ClearDefaultsPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ClearDefaultsPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public ClearDefaultsPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setLayoutResource(2130968620);
    this.mAppWidgetManager = AppWidgetManager.getInstance(paramContext);
    this.mPm = paramContext.getPackageManager();
    this.mUsbManager = IUsbManager.Stub.asInterface(ServiceManager.getService("usb"));
  }
  
  private boolean isDefaultBrowser(String paramString)
  {
    return paramString.equals(this.mPm.getDefaultBrowserPackageNameAsUser(UserHandle.myUserId()));
  }
  
  private void resetLaunchDefaultsUi(TextView paramTextView)
  {
    paramTextView.setText(2131692102);
    this.mActivitiesButton.setEnabled(false);
  }
  
  public void onBindViewHolder(final PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    this.mActivitiesButton = ((Button)paramPreferenceViewHolder.findViewById(2131361968));
    this.mActivitiesButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        int i;
        if (ClearDefaultsPreference.-get3(ClearDefaultsPreference.this) != null)
        {
          i = UserHandle.myUserId();
          ClearDefaultsPreference.-get2(ClearDefaultsPreference.this).clearPackagePreferredActivities(ClearDefaultsPreference.-get1(ClearDefaultsPreference.this));
          if (ClearDefaultsPreference.-wrap0(ClearDefaultsPreference.this, ClearDefaultsPreference.-get1(ClearDefaultsPreference.this))) {
            ClearDefaultsPreference.-get2(ClearDefaultsPreference.this).setDefaultBrowserPackageNameAsUser(null, i);
          }
        }
        try
        {
          ClearDefaultsPreference.-get3(ClearDefaultsPreference.this).clearDefaults(ClearDefaultsPreference.-get1(ClearDefaultsPreference.this), i);
          ClearDefaultsPreference.-get0(ClearDefaultsPreference.this).setBindAppWidgetPermission(ClearDefaultsPreference.-get1(ClearDefaultsPreference.this), false);
          paramAnonymousView = (TextView)paramPreferenceViewHolder.findViewById(2131361967);
          ClearDefaultsPreference.-wrap1(ClearDefaultsPreference.this, paramAnonymousView);
          return;
        }
        catch (RemoteException paramAnonymousView)
        {
          for (;;)
          {
            Log.e(ClearDefaultsPreference.TAG, "mUsbManager.clearDefaults", paramAnonymousView);
          }
        }
      }
    });
    updateUI(paramPreferenceViewHolder);
  }
  
  public void setAppEntry(ApplicationsState.AppEntry paramAppEntry)
  {
    this.mAppEntry = paramAppEntry;
  }
  
  public void setPackageName(String paramString)
  {
    this.mPackageName = paramString;
  }
  
  public boolean updateUI(PreferenceViewHolder paramPreferenceViewHolder)
  {
    boolean bool3 = this.mAppWidgetManager.hasBindAppWidgetPermission(this.mAppEntry.info.packageName);
    TextView localTextView = (TextView)paramPreferenceViewHolder.findViewById(2131361967);
    boolean bool1;
    boolean bool2;
    label84:
    label96:
    Object localObject2;
    if ((!AppUtils.hasPreferredActivities(this.mPm, this.mPackageName)) && (!isDefaultBrowser(this.mPackageName)))
    {
      bool1 = AppUtils.hasUsbDefaults(this.mUsbManager, this.mPackageName);
      if ((!bool1) && (!bool3)) {
        break label281;
      }
      if (!bool3) {
        break label290;
      }
      bool2 = bool1;
      if (!bool3) {
        break label296;
      }
      localTextView.setText(2131692079);
      localObject2 = getContext();
      paramPreferenceViewHolder = null;
      int i = ((Context)localObject2).getResources().getDimensionPixelSize(2131755471);
      if (bool1)
      {
        paramPreferenceViewHolder = ((Context)localObject2).getText(2131692100);
        localObject1 = new SpannableString(paramPreferenceViewHolder);
        if (bool2) {
          ((SpannableString)localObject1).setSpan(new BulletSpan(i), 0, paramPreferenceViewHolder.length(), 0);
        }
        paramPreferenceViewHolder = TextUtils.concat(new CharSequence[] { localObject1, "\n" });
      }
      localObject1 = paramPreferenceViewHolder;
      if (bool3)
      {
        localObject1 = ((Context)localObject2).getText(2131692101);
        localObject2 = new SpannableString((CharSequence)localObject1);
        if (bool2) {
          ((SpannableString)localObject2).setSpan(new BulletSpan(i), 0, ((CharSequence)localObject1).length(), 0);
        }
        if (paramPreferenceViewHolder != null) {
          break label306;
        }
      }
    }
    label281:
    label290:
    label296:
    label306:
    for (Object localObject1 = TextUtils.concat(new CharSequence[] { localObject2, "\n" });; localObject1 = TextUtils.concat(new CharSequence[] { paramPreferenceViewHolder, "\n", localObject2, "\n" }))
    {
      localTextView.setText((CharSequence)localObject1);
      this.mActivitiesButton.setEnabled(true);
      for (;;)
      {
        return true;
        bool1 = true;
        break;
        resetLaunchDefaultsUi(localTextView);
      }
      bool2 = false;
      break label84;
      localTextView.setText(2131692078);
      break label96;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\ClearDefaultsPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */