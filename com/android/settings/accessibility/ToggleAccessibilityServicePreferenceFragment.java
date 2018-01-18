package com.android.settings.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.AccessibilityServiceInfo.CapabilityInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.ConfirmDeviceCredentialActivity;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.ToggleSwitch;
import com.android.settings.widget.ToggleSwitch.OnBeforeCheckedChangeListener;
import com.android.settingslib.accessibility.AccessibilityUtils;
import java.util.List;

public class ToggleAccessibilityServicePreferenceFragment
  extends ToggleFeaturePreferenceFragment
  implements DialogInterface.OnClickListener
{
  public static final int ACTIVITY_REQUEST_CONFIRM_CREDENTIAL_FOR_WEAKER_ENCRYPTION = 1;
  private static final int DIALOG_ID_DISABLE_WARNING = 2;
  private static final int DIALOG_ID_ENABLE_WARNING = 1;
  private ComponentName mComponentName;
  private LockPatternUtils mLockPatternUtils;
  private final SettingsContentObserver mSettingsContentObserver = new SettingsContentObserver(new Handler())
  {
    public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
    {
      ToggleAccessibilityServicePreferenceFragment.-wrap1(ToggleAccessibilityServicePreferenceFragment.this);
    }
  };
  private int mShownDialogId;
  
  private String createConfirmCredentialReasonMessage()
  {
    int i = 2131692393;
    switch (this.mLockPatternUtils.getKeyguardStoredPasswordQuality(UserHandle.myUserId()))
    {
    }
    while (getAccessibilityServiceInfo() != null)
    {
      return getString(i, new Object[] { getAccessibilityServiceInfo().getResolveInfo().loadLabel(getPackageManager()) });
      i = 2131692391;
      continue;
      i = 2131692392;
    }
    return "";
  }
  
  private View createEnableDialogContentView(AccessibilityServiceInfo paramAccessibilityServiceInfo)
  {
    LayoutInflater localLayoutInflater = (LayoutInflater)getSystemService("layout_inflater");
    View localView1 = localLayoutInflater.inflate(2130968697, null);
    Object localObject1 = (TextView)localView1.findViewById(2131362135);
    if (isFullDiskEncrypted())
    {
      ((TextView)localObject1).setText(getString(2131692389, new Object[] { paramAccessibilityServiceInfo.getResolveInfo().loadLabel(getPackageManager()) }));
      ((TextView)localObject1).setVisibility(0);
    }
    for (;;)
    {
      ((TextView)localView1.findViewById(2131362136)).setText(getString(2131692387, new Object[] { paramAccessibilityServiceInfo.getResolveInfo().loadLabel(getPackageManager()) }));
      localObject1 = (LinearLayout)localView1.findViewById(2131362137);
      Object localObject2 = localLayoutInflater.inflate(17367096, null);
      ((ImageView)((View)localObject2).findViewById(16909120)).setImageDrawable(getActivity().getDrawable(17302607));
      ((TextView)((View)localObject2).findViewById(16909124)).setText(getString(2131692394));
      ((TextView)((View)localObject2).findViewById(16909125)).setText(getString(2131692395));
      paramAccessibilityServiceInfo = paramAccessibilityServiceInfo.getCapabilityInfos();
      ((LinearLayout)localObject1).addView((View)localObject2);
      int j = paramAccessibilityServiceInfo.size();
      int i = 0;
      while (i < j)
      {
        localObject2 = (AccessibilityServiceInfo.CapabilityInfo)paramAccessibilityServiceInfo.get(i);
        View localView2 = localLayoutInflater.inflate(17367096, null);
        ((ImageView)localView2.findViewById(16909120)).setImageDrawable(getActivity().getDrawable(17302607));
        ((TextView)localView2.findViewById(16909124)).setText(getString(((AccessibilityServiceInfo.CapabilityInfo)localObject2).titleResId));
        ((TextView)localView2.findViewById(16909125)).setText(getString(((AccessibilityServiceInfo.CapabilityInfo)localObject2).descResId));
        ((LinearLayout)localObject1).addView(localView2);
        i += 1;
      }
      ((TextView)localObject1).setVisibility(8);
    }
    return localView1;
  }
  
  private AccessibilityServiceInfo getAccessibilityServiceInfo()
  {
    List localList = AccessibilityManager.getInstance(getActivity()).getInstalledAccessibilityServiceList();
    int j = localList.size();
    int i = 0;
    while (i < j)
    {
      AccessibilityServiceInfo localAccessibilityServiceInfo = (AccessibilityServiceInfo)localList.get(i);
      ResolveInfo localResolveInfo = localAccessibilityServiceInfo.getResolveInfo();
      if ((this.mComponentName.getPackageName().equals(localResolveInfo.serviceInfo.packageName)) && (this.mComponentName.getClassName().equals(localResolveInfo.serviceInfo.name))) {
        return localAccessibilityServiceInfo;
      }
      i += 1;
    }
    return null;
  }
  
  private void handleConfirmServiceEnabled(boolean paramBoolean)
  {
    this.mSwitchBar.setCheckedInternal(paramBoolean);
    getArguments().putBoolean("checked", paramBoolean);
    onPreferenceToggled(this.mPreferenceKey, paramBoolean);
  }
  
  private boolean isFullDiskEncrypted()
  {
    return StorageManager.isNonDefaultBlockEncrypted();
  }
  
  private void updateSwitchBarToggleSwitch()
  {
    String str = Settings.Secure.getString(getContentResolver(), "enabled_accessibility_services");
    if (str != null) {}
    for (boolean bool = str.contains(this.mComponentName.flattenToString());; bool = false)
    {
      this.mSwitchBar.setCheckedInternal(bool);
      return;
    }
  }
  
  protected int getMetricsCategory()
  {
    return 4;
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt1 == 1)
    {
      if (paramInt2 != -1) {
        break label42;
      }
      handleConfirmServiceEnabled(true);
      if (isFullDiskEncrypted())
      {
        this.mLockPatternUtils.clearEncryptionPassword();
        Settings.Global.putInt(getContentResolver(), "require_password_to_decrypt", 0);
      }
    }
    return;
    label42:
    handleConfirmServiceEnabled(false);
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    boolean bool = true;
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException();
    case -1: 
      if (this.mShownDialogId == 1)
      {
        if (isFullDiskEncrypted())
        {
          startActivityForResult(ConfirmDeviceCredentialActivity.createIntent(createConfirmCredentialReasonMessage(), null), 1);
          return;
        }
        handleConfirmServiceEnabled(true);
        return;
      }
      handleConfirmServiceEnabled(false);
      return;
    }
    if (this.mShownDialogId == 2) {}
    for (;;)
    {
      handleConfirmServiceEnabled(bool);
      return;
      bool = false;
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mLockPatternUtils = new LockPatternUtils(getActivity());
  }
  
  public Dialog onCreateDialog(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException();
    case 1: 
      this.mShownDialogId = 1;
      localObject = getAccessibilityServiceInfo();
      if (localObject == null) {
        return null;
      }
      localObject = new AlertDialog.Builder(getActivity()).setTitle(getString(2131692386, new Object[] { ((AccessibilityServiceInfo)localObject).getResolveInfo().loadLabel(getPackageManager()) })).setView(createEnableDialogContentView((AccessibilityServiceInfo)localObject)).setCancelable(true).setPositiveButton(17039370, this).setNegativeButton(17039360, this).create();
      View.OnTouchListener local2 = new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          if ((paramAnonymousMotionEvent.getFlags() & 0x1) != 0)
          {
            if (paramAnonymousMotionEvent.getAction() == 1) {
              Toast.makeText(paramAnonymousView.getContext(), 2131692388, 0).show();
            }
            return true;
          }
          return false;
        }
      };
      ((AlertDialog)localObject).create();
      ((AlertDialog)localObject).getButton(-1).setOnTouchListener(local2);
      return (Dialog)localObject;
    }
    this.mShownDialogId = 2;
    Object localObject = getAccessibilityServiceInfo();
    if (localObject == null) {
      return null;
    }
    return new AlertDialog.Builder(getActivity()).setTitle(getString(2131692396, new Object[] { ((AccessibilityServiceInfo)localObject).getResolveInfo().loadLabel(getPackageManager()) })).setMessage(getString(2131692397, new Object[] { ((AccessibilityServiceInfo)localObject).getResolveInfo().loadLabel(getPackageManager()) })).setCancelable(true).setPositiveButton(17039370, this).setNegativeButton(17039360, this).create();
  }
  
  protected void onInstallSwitchBarToggleSwitch()
  {
    super.onInstallSwitchBarToggleSwitch();
    this.mToggleSwitch.setOnBeforeCheckedChangeListener(new ToggleSwitch.OnBeforeCheckedChangeListener()
    {
      public boolean onBeforeCheckedChanged(ToggleSwitch paramAnonymousToggleSwitch, boolean paramAnonymousBoolean)
      {
        if (paramAnonymousBoolean)
        {
          ToggleAccessibilityServicePreferenceFragment.this.mSwitchBar.setCheckedInternal(false);
          ToggleAccessibilityServicePreferenceFragment.this.getArguments().putBoolean("checked", false);
          ToggleAccessibilityServicePreferenceFragment.-wrap0(ToggleAccessibilityServicePreferenceFragment.this, 1);
          return true;
        }
        ToggleAccessibilityServicePreferenceFragment.this.mSwitchBar.setCheckedInternal(true);
        ToggleAccessibilityServicePreferenceFragment.this.getArguments().putBoolean("checked", true);
        ToggleAccessibilityServicePreferenceFragment.-wrap0(ToggleAccessibilityServicePreferenceFragment.this, 2);
        return true;
      }
    });
  }
  
  public void onPause()
  {
    this.mSettingsContentObserver.unregister(getContentResolver());
    super.onPause();
  }
  
  public void onPreferenceToggled(String paramString, boolean paramBoolean)
  {
    paramString = ComponentName.unflattenFromString(paramString);
    AccessibilityUtils.setAccessibilityServiceState(getActivity(), paramString, paramBoolean);
  }
  
  protected void onProcessArguments(Bundle paramBundle)
  {
    super.onProcessArguments(paramBundle);
    String str = paramBundle.getString("settings_title");
    Object localObject = paramBundle.getString("settings_component_name");
    if ((TextUtils.isEmpty(str)) || (TextUtils.isEmpty((CharSequence)localObject))) {}
    for (;;)
    {
      this.mComponentName = ((ComponentName)paramBundle.getParcelable("component_name"));
      return;
      localObject = new Intent("android.intent.action.MAIN").setComponent(ComponentName.unflattenFromString(((String)localObject).toString()));
      if (!getPackageManager().queryIntentActivities((Intent)localObject, 0).isEmpty())
      {
        this.mSettingsTitle = str;
        this.mSettingsIntent = ((Intent)localObject);
        setHasOptionsMenu(true);
      }
    }
  }
  
  public void onResume()
  {
    this.mSettingsContentObserver.register(getContentResolver());
    updateSwitchBarToggleSwitch();
    super.onResume();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accessibility\ToggleAccessibilityServicePreferenceFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */