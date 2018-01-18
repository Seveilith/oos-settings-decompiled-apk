package com.android.settings.inputmethod;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import com.android.settings.SettingsPreferenceFragment;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public final class AvailableVirtualKeyboardFragment
  extends SettingsPreferenceFragment
  implements InputMethodPreference.OnSavePreferenceListener
{
  private DevicePolicyManager mDpm;
  private InputMethodManager mImm;
  private final ArrayList<InputMethodPreference> mInputMethodPreferenceList = new ArrayList();
  private InputMethodSettingValuesWrapper mInputMethodSettingValues;
  
  private static Drawable getInputMethodIcon(PackageManager paramPackageManager, InputMethodInfo paramInputMethodInfo)
  {
    Object localObject = paramInputMethodInfo.getServiceInfo();
    ApplicationInfo localApplicationInfo = ((ServiceInfo)localObject).applicationInfo;
    paramInputMethodInfo = paramInputMethodInfo.getPackageName();
    if ((localObject == null) || (localApplicationInfo == null)) {}
    while (paramInputMethodInfo == null) {
      return new ColorDrawable(0);
    }
    Drawable localDrawable = loadDrawable(paramPackageManager, paramInputMethodInfo, ((ServiceInfo)localObject).logo, localApplicationInfo);
    if (localDrawable != null) {
      return localDrawable;
    }
    localObject = loadDrawable(paramPackageManager, paramInputMethodInfo, ((ServiceInfo)localObject).icon, localApplicationInfo);
    if (localObject != null) {
      return (Drawable)localObject;
    }
    localObject = loadDrawable(paramPackageManager, paramInputMethodInfo, localApplicationInfo.logo, localApplicationInfo);
    if (localObject != null) {
      return (Drawable)localObject;
    }
    paramPackageManager = loadDrawable(paramPackageManager, paramInputMethodInfo, localApplicationInfo.icon, localApplicationInfo);
    if (paramPackageManager != null) {
      return paramPackageManager;
    }
    return new ColorDrawable(0);
  }
  
  private static Drawable loadDrawable(PackageManager paramPackageManager, String paramString, int paramInt, ApplicationInfo paramApplicationInfo)
  {
    if (paramInt == 0) {
      return null;
    }
    try
    {
      paramPackageManager = paramPackageManager.getDrawable(paramString, paramInt, paramApplicationInfo);
      return paramPackageManager;
    }
    catch (Exception paramPackageManager) {}
    return null;
  }
  
  private void updateInputMethodPreferenceViews()
  {
    this.mInputMethodSettingValues.refreshAllInputMethodAndSubtypes();
    this.mInputMethodPreferenceList.clear();
    final Object localObject = this.mDpm.getPermittedInputMethodsForCurrentUser();
    Context localContext = getPrefContext();
    PackageManager localPackageManager = getActivity().getPackageManager();
    List localList = this.mInputMethodSettingValues.getInputMethodList();
    int i;
    label56:
    InputMethodInfo localInputMethodInfo;
    if (localList == null)
    {
      i = 0;
      j = 0;
      if (j >= i) {
        break label153;
      }
      localInputMethodInfo = (InputMethodInfo)localList.get(j);
      if (localObject == null) {
        break label148;
      }
    }
    label148:
    for (boolean bool = ((List)localObject).contains(localInputMethodInfo.getPackageName());; bool = true)
    {
      InputMethodPreference localInputMethodPreference = new InputMethodPreference(localContext, localInputMethodInfo, true, bool, this);
      localInputMethodPreference.setIcon(getInputMethodIcon(localPackageManager, localInputMethodInfo));
      this.mInputMethodPreferenceList.add(localInputMethodPreference);
      j += 1;
      break label56;
      i = localList.size();
      break;
    }
    label153:
    localObject = Collator.getInstance();
    Collections.sort(this.mInputMethodPreferenceList, new Comparator()
    {
      public int compare(InputMethodPreference paramAnonymousInputMethodPreference1, InputMethodPreference paramAnonymousInputMethodPreference2)
      {
        return paramAnonymousInputMethodPreference1.compareTo(paramAnonymousInputMethodPreference2, localObject);
      }
    });
    getPreferenceScreen().removeAll();
    int j = 0;
    while (j < i)
    {
      localObject = (InputMethodPreference)this.mInputMethodPreferenceList.get(j);
      ((InputMethodPreference)localObject).setOrder(j);
      getPreferenceScreen().addPreference((Preference)localObject);
      InputMethodAndSubtypeUtil.removeUnnecessaryNonPersistentPreference((Preference)localObject);
      ((InputMethodPreference)localObject).updatePreferenceViews();
      j += 1;
    }
  }
  
  protected int getMetricsCategory()
  {
    return 347;
  }
  
  public void onCreatePreferences(Bundle paramBundle, String paramString)
  {
    paramBundle = getActivity();
    paramString = getPreferenceManager().createPreferenceScreen(paramBundle);
    paramString.setTitle(paramBundle.getString(2131692243));
    setPreferenceScreen(paramString);
    this.mInputMethodSettingValues = InputMethodSettingValuesWrapper.getInstance(paramBundle);
    this.mImm = ((InputMethodManager)paramBundle.getSystemService(InputMethodManager.class));
    this.mDpm = ((DevicePolicyManager)paramBundle.getSystemService(DevicePolicyManager.class));
  }
  
  public void onResume()
  {
    super.onResume();
    this.mInputMethodSettingValues.refreshAllInputMethodAndSubtypes();
    updateInputMethodPreferenceViews();
  }
  
  public void onSaveInputMethodPreference(InputMethodPreference paramInputMethodPreference)
  {
    if (getResources().getConfiguration().keyboard == 2) {}
    for (boolean bool = true;; bool = false)
    {
      InputMethodAndSubtypeUtil.saveInputMethodSubtypeList(this, getContentResolver(), this.mImm.getInputMethodList(), bool);
      this.mInputMethodSettingValues.refreshAllInputMethodAndSubtypes();
      paramInputMethodPreference = this.mInputMethodPreferenceList.iterator();
      while (paramInputMethodPreference.hasNext()) {
        ((InputMethodPreference)paramInputMethodPreference.next()).updatePreferenceViews();
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\inputmethod\AvailableVirtualKeyboardFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */