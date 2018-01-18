package com.android.settings.inputmethod;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Context;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.RemoteException;
import android.util.Log;
import android.util.Slog;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import com.android.internal.inputmethod.InputMethodUtils;
import com.android.internal.inputmethod.InputMethodUtils.InputMethodSettings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

class InputMethodSettingValuesWrapper
{
  private static final String TAG = InputMethodSettingValuesWrapper.class.getSimpleName();
  private static volatile InputMethodSettingValuesWrapper sInstance;
  private final HashSet<InputMethodInfo> mAsciiCapableEnabledImis = new HashSet();
  private final InputMethodManager mImm;
  private final ArrayList<InputMethodInfo> mMethodList = new ArrayList();
  private final HashMap<String, InputMethodInfo> mMethodMap = new HashMap();
  private final InputMethodUtils.InputMethodSettings mSettings = new InputMethodUtils.InputMethodSettings(paramContext.getResources(), paramContext.getContentResolver(), this.mMethodMap, this.mMethodList, getDefaultCurrentUserId(), false);
  
  private InputMethodSettingValuesWrapper(Context paramContext)
  {
    this.mImm = ((InputMethodManager)paramContext.getSystemService("input_method"));
    refreshAllInputMethodAndSubtypes();
  }
  
  private static int getDefaultCurrentUserId()
  {
    try
    {
      int i = ActivityManagerNative.getDefault().getCurrentUser().id;
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w(TAG, "Couldn't get current user ID; guessing it's 0", localRemoteException);
    }
    return 0;
  }
  
  private int getEnabledValidSystemNonAuxAsciiCapableImeCount(Context paramContext)
  {
    int i = 0;
    synchronized (this.mMethodMap)
    {
      ArrayList localArrayList = this.mSettings.getEnabledInputMethodListLocked();
      ??? = localArrayList.iterator();
      while (((Iterator)???).hasNext()) {
        if (isValidSystemNonAuxAsciiCapableIme((InputMethodInfo)((Iterator)???).next(), paramContext)) {
          i += 1;
        }
      }
    }
    Log.w(TAG, "No \"enabledValidSystemNonAuxAsciiCapableIme\"s found.");
    return i;
  }
  
  static InputMethodSettingValuesWrapper getInstance(Context paramContext)
  {
    if (sInstance == null) {}
    synchronized (TAG)
    {
      if (sInstance == null) {
        sInstance = new InputMethodSettingValuesWrapper(paramContext);
      }
      return sInstance;
    }
  }
  
  private void updateAsciiCapableEnabledImis()
  {
    for (;;)
    {
      int i;
      synchronized (this.mMethodMap)
      {
        this.mAsciiCapableEnabledImis.clear();
        Iterator localIterator = this.mSettings.getEnabledInputMethodListLocked().iterator();
        if (!localIterator.hasNext()) {
          break;
        }
        InputMethodInfo localInputMethodInfo = (InputMethodInfo)localIterator.next();
        int j = localInputMethodInfo.getSubtypeCount();
        i = 0;
        if (i >= j) {
          continue;
        }
        InputMethodSubtype localInputMethodSubtype = localInputMethodInfo.getSubtypeAt(i);
        if (("keyboard".equalsIgnoreCase(localInputMethodSubtype.getMode())) && (localInputMethodSubtype.isAsciiCapable())) {
          this.mAsciiCapableEnabledImis.add(localInputMethodInfo);
        }
      }
      i += 1;
    }
  }
  
  CharSequence getCurrentInputMethodName(Context paramContext)
  {
    synchronized (this.mMethodMap)
    {
      InputMethodInfo localInputMethodInfo = (InputMethodInfo)this.mMethodMap.get(this.mSettings.getSelectedInputMethod());
      if (localInputMethodInfo == null)
      {
        Log.w(TAG, "Invalid selected imi: " + this.mSettings.getSelectedInputMethod());
        return "";
      }
      paramContext = InputMethodUtils.getImeAndSubtypeDisplayName(paramContext, localInputMethodInfo, this.mImm.getCurrentInputMethodSubtype());
      return paramContext;
    }
  }
  
  List<InputMethodInfo> getInputMethodList()
  {
    synchronized (this.mMethodMap)
    {
      ArrayList localArrayList = this.mMethodList;
      return localArrayList;
    }
  }
  
  boolean isAlwaysCheckedIme(InputMethodInfo paramInputMethodInfo, Context paramContext)
  {
    boolean bool = isEnabledImi(paramInputMethodInfo);
    int i;
    synchronized (this.mMethodMap)
    {
      i = this.mSettings.getEnabledInputMethodListLocked().size();
      if ((i <= 1) && (bool)) {
        return true;
      }
      i = getEnabledValidSystemNonAuxAsciiCapableImeCount(paramContext);
      if (i > 1) {
        return false;
      }
    }
    if ((i != 1) || (bool))
    {
      if (!InputMethodUtils.isSystemIme(paramInputMethodInfo)) {
        return false;
      }
    }
    else {
      return false;
    }
    return isValidSystemNonAuxAsciiCapableIme(paramInputMethodInfo, paramContext);
  }
  
  boolean isEnabledImi(InputMethodInfo paramInputMethodInfo)
  {
    synchronized (this.mMethodMap)
    {
      ArrayList localArrayList = this.mSettings.getEnabledInputMethodListLocked();
      ??? = localArrayList.iterator();
      while (((Iterator)???).hasNext()) {
        if (((InputMethodInfo)((Iterator)???).next()).getId().equals(paramInputMethodInfo.getId())) {
          return true;
        }
      }
    }
    return false;
  }
  
  boolean isValidSystemNonAuxAsciiCapableIme(InputMethodInfo paramInputMethodInfo, Context paramContext)
  {
    if (paramInputMethodInfo.isAuxiliaryIme()) {
      return false;
    }
    if (InputMethodUtils.isSystemImeThatHasSubtypeOf(paramInputMethodInfo, paramContext, true, paramContext.getResources().getConfiguration().locale, false, InputMethodUtils.SUBTYPE_MODE_ANY)) {
      return true;
    }
    if (this.mAsciiCapableEnabledImis.isEmpty())
    {
      Log.w(TAG, "ascii capable subtype enabled imi not found. Fall back to English Keyboard subtype.");
      return InputMethodUtils.containsSubtypeOf(paramInputMethodInfo, Locale.ENGLISH, false, "keyboard");
    }
    return this.mAsciiCapableEnabledImis.contains(paramInputMethodInfo);
  }
  
  void refreshAllInputMethodAndSubtypes()
  {
    synchronized (this.mMethodMap)
    {
      this.mMethodList.clear();
      this.mMethodMap.clear();
      Object localObject1 = this.mImm.getInputMethodList();
      this.mMethodList.addAll((Collection)localObject1);
      localObject1 = ((Iterable)localObject1).iterator();
      if (((Iterator)localObject1).hasNext())
      {
        InputMethodInfo localInputMethodInfo = (InputMethodInfo)((Iterator)localObject1).next();
        this.mMethodMap.put(localInputMethodInfo.getId(), localInputMethodInfo);
      }
    }
    updateAsciiCapableEnabledImis();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\inputmethod\InputMethodSettingValuesWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */