package com.android.settings.inputmethod;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.icu.text.ListFormatter;
import android.os.LocaleList;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.TwoStatePreference;
import android.text.TextUtils;
import android.text.TextUtils.SimpleStringSplitter;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;
import com.android.internal.app.LocaleHelper;
import com.android.internal.inputmethod.InputMethodUtils;
import com.android.settings.SettingsPreferenceFragment;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

class InputMethodAndSubtypeUtil
{
  private static final boolean DEBUG = false;
  private static final char INPUT_METHOD_SEPARATER = ':';
  private static final char INPUT_METHOD_SUBTYPE_SEPARATER = ';';
  private static final int NOT_A_SUBTYPE_ID = -1;
  static final String TAG = "InputMethdAndSubtypeUtil";
  private static final TextUtils.SimpleStringSplitter sStringInputMethodSplitter = new TextUtils.SimpleStringSplitter(':');
  private static final TextUtils.SimpleStringSplitter sStringInputMethodSubtypeSplitter = new TextUtils.SimpleStringSplitter(';');
  
  static String buildInputMethodsAndSubtypesString(HashMap<String, HashSet<String>> paramHashMap)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = paramHashMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject1 = (String)localIterator.next();
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(':');
      }
      Object localObject2 = (HashSet)paramHashMap.get(localObject1);
      localStringBuilder.append((String)localObject1);
      localObject1 = ((Iterable)localObject2).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (String)((Iterator)localObject1).next();
        localStringBuilder.append(';').append((String)localObject2);
      }
    }
    return localStringBuilder.toString();
  }
  
  private static String buildInputMethodsString(HashSet<String> paramHashSet)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    paramHashSet = paramHashSet.iterator();
    while (paramHashSet.hasNext())
    {
      String str = (String)paramHashSet.next();
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(':');
      }
      localStringBuilder.append(str);
    }
    return localStringBuilder.toString();
  }
  
  static void enableInputMethodSubtypesOf(ContentResolver paramContentResolver, String paramString, HashSet<String> paramHashSet)
  {
    HashMap localHashMap = getEnabledInputMethodsAndSubtypeList(paramContentResolver);
    localHashMap.put(paramString, paramHashSet);
    Settings.Secure.putString(paramContentResolver, "enabled_input_methods", buildInputMethodsAndSubtypesString(localHashMap));
  }
  
  private static HashSet<String> getDisabledSystemIMEs(ContentResolver paramContentResolver)
  {
    HashSet localHashSet = new HashSet();
    paramContentResolver = Settings.Secure.getString(paramContentResolver, "disabled_system_input_methods");
    if (TextUtils.isEmpty(paramContentResolver)) {
      return localHashSet;
    }
    sStringInputMethodSplitter.setString(paramContentResolver);
    while (sStringInputMethodSplitter.hasNext()) {
      localHashSet.add(sStringInputMethodSplitter.next());
    }
    return localHashSet;
  }
  
  private static Locale getDisplayLocale(Context paramContext)
  {
    if (paramContext == null) {
      return Locale.getDefault();
    }
    if (paramContext.getResources() == null) {
      return Locale.getDefault();
    }
    paramContext = paramContext.getResources().getConfiguration();
    if (paramContext == null) {
      return Locale.getDefault();
    }
    paramContext = paramContext.getLocales().get(0);
    if (paramContext == null) {
      return Locale.getDefault();
    }
    return paramContext;
  }
  
  private static HashMap<String, HashSet<String>> getEnabledInputMethodsAndSubtypeList(ContentResolver paramContentResolver)
  {
    return parseInputMethodsAndSubtypesString(Settings.Secure.getString(paramContentResolver, "enabled_input_methods"));
  }
  
  private static int getInputMethodSubtypeSelected(ContentResolver paramContentResolver)
  {
    try
    {
      int i = Settings.Secure.getInt(paramContentResolver, "selected_input_method_subtype");
      return i;
    }
    catch (Settings.SettingNotFoundException paramContentResolver) {}
    return -1;
  }
  
  static String getSubtypeLocaleNameAsSentence(InputMethodSubtype paramInputMethodSubtype, Context paramContext, InputMethodInfo paramInputMethodInfo)
  {
    if (paramInputMethodSubtype == null) {
      return "";
    }
    Locale localLocale = getDisplayLocale(paramContext);
    return LocaleHelper.toSentenceCase(paramInputMethodSubtype.getDisplayName(paramContext, paramInputMethodInfo.getPackageName(), paramInputMethodInfo.getServiceInfo().applicationInfo).toString(), localLocale);
  }
  
  static String getSubtypeLocaleNameListAsSentence(List<InputMethodSubtype> paramList, Context paramContext, InputMethodInfo paramInputMethodInfo)
  {
    if (paramList.isEmpty()) {
      return "";
    }
    Locale localLocale = getDisplayLocale(paramContext);
    int j = paramList.size();
    CharSequence[] arrayOfCharSequence = new CharSequence[j];
    int i = 0;
    while (i < j)
    {
      arrayOfCharSequence[i] = ((InputMethodSubtype)paramList.get(i)).getDisplayName(paramContext, paramInputMethodInfo.getPackageName(), paramInputMethodInfo.getServiceInfo().applicationInfo);
      i += 1;
    }
    return LocaleHelper.toSentenceCase(ListFormatter.getInstance(localLocale).format(arrayOfCharSequence), localLocale);
  }
  
  private static boolean isInputMethodSubtypeSelected(ContentResolver paramContentResolver)
  {
    return getInputMethodSubtypeSelected(paramContentResolver) != -1;
  }
  
  static void loadInputMethodSubtypeList(SettingsPreferenceFragment paramSettingsPreferenceFragment, ContentResolver paramContentResolver, List<InputMethodInfo> paramList, Map<String, List<Preference>> paramMap)
  {
    paramContentResolver = getEnabledInputMethodsAndSubtypeList(paramContentResolver);
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      String str = ((InputMethodInfo)localIterator.next()).getId();
      Object localObject = paramSettingsPreferenceFragment.findPreference(str);
      if ((localObject instanceof TwoStatePreference))
      {
        localObject = (TwoStatePreference)localObject;
        boolean bool = paramContentResolver.containsKey(str);
        ((TwoStatePreference)localObject).setChecked(bool);
        if (paramMap != null)
        {
          localObject = ((List)paramMap.get(str)).iterator();
          while (((Iterator)localObject).hasNext()) {
            ((Preference)((Iterator)localObject).next()).setEnabled(bool);
          }
        }
        setSubtypesPreferenceEnabled(paramSettingsPreferenceFragment, paramList, str, bool);
      }
    }
    updateSubtypesPreferenceChecked(paramSettingsPreferenceFragment, paramList, paramContentResolver);
  }
  
  static HashMap<String, HashSet<String>> parseInputMethodsAndSubtypesString(String paramString)
  {
    HashMap localHashMap = new HashMap();
    if (TextUtils.isEmpty(paramString)) {
      return localHashMap;
    }
    sStringInputMethodSplitter.setString(paramString);
    while (sStringInputMethodSplitter.hasNext())
    {
      paramString = sStringInputMethodSplitter.next();
      sStringInputMethodSubtypeSplitter.setString(paramString);
      if (sStringInputMethodSubtypeSplitter.hasNext())
      {
        paramString = new HashSet();
        String str = sStringInputMethodSubtypeSplitter.next();
        while (sStringInputMethodSubtypeSplitter.hasNext()) {
          paramString.add(sStringInputMethodSubtypeSplitter.next());
        }
        localHashMap.put(str, paramString);
      }
    }
    return localHashMap;
  }
  
  private static void putSelectedInputMethodSubtype(ContentResolver paramContentResolver, int paramInt)
  {
    Settings.Secure.putInt(paramContentResolver, "selected_input_method_subtype", paramInt);
  }
  
  static void removeUnnecessaryNonPersistentPreference(Preference paramPreference)
  {
    String str = paramPreference.getKey();
    if ((paramPreference.isPersistent()) || (str == null)) {
      return;
    }
    paramPreference = paramPreference.getSharedPreferences();
    if ((paramPreference != null) && (paramPreference.contains(str))) {
      paramPreference.edit().remove(str).apply();
    }
  }
  
  static void saveInputMethodSubtypeList(SettingsPreferenceFragment paramSettingsPreferenceFragment, ContentResolver paramContentResolver, List<InputMethodInfo> paramList, boolean paramBoolean)
  {
    Object localObject = Settings.Secure.getString(paramContentResolver, "default_input_method");
    int i1 = getInputMethodSubtypeSelected(paramContentResolver);
    HashMap localHashMap = getEnabledInputMethodsAndSubtypeList(paramContentResolver);
    HashSet localHashSet1 = getDisabledSystemIMEs(paramContentResolver);
    int i = 0;
    Iterator localIterator = paramList.iterator();
    paramList = (List<InputMethodInfo>)localObject;
    while (localIterator.hasNext())
    {
      InputMethodInfo localInputMethodInfo = (InputMethodInfo)localIterator.next();
      String str1 = localInputMethodInfo.getId();
      localObject = paramSettingsPreferenceFragment.findPreference(str1);
      if (localObject != null)
      {
        boolean bool1;
        boolean bool2;
        boolean bool3;
        HashSet localHashSet2;
        int j;
        int m;
        label194:
        String str2;
        TwoStatePreference localTwoStatePreference;
        int n;
        if ((localObject instanceof TwoStatePreference))
        {
          bool1 = ((TwoStatePreference)localObject).isChecked();
          bool2 = str1.equals(paramList);
          bool3 = InputMethodUtils.isSystemIme(localInputMethodInfo);
          if (((paramBoolean) || (!InputMethodSettingValuesWrapper.getInstance(paramSettingsPreferenceFragment.getActivity()).isAlwaysCheckedIme(localInputMethodInfo, paramSettingsPreferenceFragment.getActivity()))) && (!bool1)) {
            break label406;
          }
          if (!localHashMap.containsKey(str1)) {
            localHashMap.put(str1, new HashSet());
          }
          localHashSet2 = (HashSet)localHashMap.get(str1);
          j = 0;
          int i2 = localInputMethodInfo.getSubtypeCount();
          m = 0;
          localObject = paramList;
          k = i;
          if (m >= i2) {
            break label433;
          }
          localObject = localInputMethodInfo.getSubtypeAt(m);
          str2 = String.valueOf(((InputMethodSubtype)localObject).hashCode());
          localTwoStatePreference = (TwoStatePreference)paramSettingsPreferenceFragment.findPreference(str1 + str2);
          if (localTwoStatePreference != null) {
            break label298;
          }
          n = j;
          j = i;
        }
        for (;;)
        {
          m += 1;
          i = j;
          j = n;
          break label194;
          bool1 = localHashMap.containsKey(str1);
          break;
          label298:
          k = i;
          i = j;
          if (j == 0)
          {
            localHashSet2.clear();
            k = 1;
            i = 1;
          }
          if ((localTwoStatePreference.isEnabled()) && (localTwoStatePreference.isChecked()))
          {
            localHashSet2.add(str2);
            j = k;
            n = i;
            if (bool2)
            {
              j = k;
              n = i;
              if (i1 == ((InputMethodSubtype)localObject).hashCode())
              {
                j = 0;
                n = i;
              }
            }
          }
          else
          {
            localHashSet2.remove(str2);
            j = k;
            n = i;
          }
        }
        label406:
        localHashMap.remove(str1);
        localObject = paramList;
        int k = i;
        if (bool2)
        {
          localObject = null;
          k = i;
        }
        label433:
        paramList = (List<InputMethodInfo>)localObject;
        i = k;
        if (bool3)
        {
          paramList = (List<InputMethodInfo>)localObject;
          i = k;
          if (paramBoolean) {
            if (localHashSet1.contains(str1))
            {
              paramList = (List<InputMethodInfo>)localObject;
              i = k;
              if (bool1)
              {
                localHashSet1.remove(str1);
                paramList = (List<InputMethodInfo>)localObject;
                i = k;
              }
            }
            else
            {
              paramList = (List<InputMethodInfo>)localObject;
              i = k;
              if (!bool1)
              {
                localHashSet1.add(str1);
                paramList = (List<InputMethodInfo>)localObject;
                i = k;
              }
            }
          }
        }
      }
    }
    paramSettingsPreferenceFragment = buildInputMethodsAndSubtypesString(localHashMap);
    localObject = buildInputMethodsString(localHashSet1);
    if ((i == 0) && (isInputMethodSubtypeSelected(paramContentResolver)))
    {
      Settings.Secure.putString(paramContentResolver, "enabled_input_methods", paramSettingsPreferenceFragment);
      if (((String)localObject).length() > 0) {
        Settings.Secure.putString(paramContentResolver, "disabled_system_input_methods", (String)localObject);
      }
      if (paramList == null) {
        break label598;
      }
    }
    for (;;)
    {
      Settings.Secure.putString(paramContentResolver, "default_input_method", paramList);
      return;
      putSelectedInputMethodSubtype(paramContentResolver, -1);
      break;
      label598:
      paramList = "";
    }
  }
  
  static void setSubtypesPreferenceEnabled(SettingsPreferenceFragment paramSettingsPreferenceFragment, List<InputMethodInfo> paramList, String paramString, boolean paramBoolean)
  {
    paramSettingsPreferenceFragment = paramSettingsPreferenceFragment.getPreferenceScreen();
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      InputMethodInfo localInputMethodInfo = (InputMethodInfo)paramList.next();
      if (paramString.equals(localInputMethodInfo.getId()))
      {
        int j = localInputMethodInfo.getSubtypeCount();
        int i = 0;
        while (i < j)
        {
          Object localObject = localInputMethodInfo.getSubtypeAt(i);
          localObject = (TwoStatePreference)paramSettingsPreferenceFragment.findPreference(paramString + ((InputMethodSubtype)localObject).hashCode());
          if (localObject != null) {
            ((TwoStatePreference)localObject).setEnabled(paramBoolean);
          }
          i += 1;
        }
      }
    }
  }
  
  private static void updateSubtypesPreferenceChecked(SettingsPreferenceFragment paramSettingsPreferenceFragment, List<InputMethodInfo> paramList, HashMap<String, HashSet<String>> paramHashMap)
  {
    paramSettingsPreferenceFragment = paramSettingsPreferenceFragment.getPreferenceScreen();
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      InputMethodInfo localInputMethodInfo = (InputMethodInfo)paramList.next();
      String str1 = localInputMethodInfo.getId();
      if (paramHashMap.containsKey(str1))
      {
        HashSet localHashSet = (HashSet)paramHashMap.get(str1);
        int j = localInputMethodInfo.getSubtypeCount();
        int i = 0;
        while (i < j)
        {
          String str2 = String.valueOf(localInputMethodInfo.getSubtypeAt(i).hashCode());
          TwoStatePreference localTwoStatePreference = (TwoStatePreference)paramSettingsPreferenceFragment.findPreference(str1 + str2);
          if (localTwoStatePreference != null) {
            localTwoStatePreference.setChecked(localHashSet.contains(str2));
          }
          i += 1;
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\inputmethod\InputMethodAndSubtypeUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */