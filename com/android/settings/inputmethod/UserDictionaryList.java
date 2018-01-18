package com.android.settings.inputmethod;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.UserDictionary.Words;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.UserDictionarySettings;
import com.android.settings.Utils;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;

public class UserDictionaryList
  extends SettingsPreferenceFragment
{
  public static final String USER_DICTIONARY_SETTINGS_INTENT_ACTION = "android.settings.USER_DICTIONARY_SETTINGS";
  private String mLocale;
  
  public static TreeSet<String> getUserDictionaryLocalesSet(Context paramContext)
  {
    Object localObject2 = paramContext.getContentResolver().query(UserDictionary.Words.CONTENT_URI, new String[] { "locale" }, null, null, null);
    TreeSet localTreeSet = new TreeSet();
    if (localObject2 == null) {
      return null;
    }
    try
    {
      Object localObject1;
      if (((Cursor)localObject2).moveToFirst())
      {
        int i = ((Cursor)localObject2).getColumnIndex("locale");
        localObject1 = ((Cursor)localObject2).getString(i);
        if (localObject1 == null) {
          break label193;
        }
      }
      for (;;)
      {
        localTreeSet.add(localObject1);
        boolean bool = ((Cursor)localObject2).moveToNext();
        if (bool) {
          break;
        }
        ((Cursor)localObject2).close();
        paramContext = (InputMethodManager)paramContext.getSystemService("input_method");
        localObject1 = paramContext.getEnabledInputMethodList().iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = paramContext.getEnabledInputMethodSubtypeList((InputMethodInfo)((Iterator)localObject1).next(), true).iterator();
          while (((Iterator)localObject2).hasNext())
          {
            String str = ((InputMethodSubtype)((Iterator)localObject2).next()).getLocale();
            if (!TextUtils.isEmpty(str)) {
              localTreeSet.add(str);
            }
          }
        }
        label193:
        localObject1 = "";
      }
      if (localTreeSet.contains(Locale.getDefault().getLanguage().toString())) {
        break label238;
      }
    }
    finally
    {
      ((Cursor)localObject2).close();
    }
    localTreeSet.add(Locale.getDefault().toString());
    label238:
    return localTreeSet;
  }
  
  protected void createUserDictSettings(PreferenceGroup paramPreferenceGroup)
  {
    Activity localActivity = getActivity();
    paramPreferenceGroup.removeAll();
    Object localObject = getUserDictionaryLocalesSet(localActivity);
    if (this.mLocale != null) {
      ((TreeSet)localObject).add(this.mLocale);
    }
    if (((TreeSet)localObject).size() > 1) {
      ((TreeSet)localObject).add("");
    }
    if (((TreeSet)localObject).isEmpty()) {
      paramPreferenceGroup.addPreference(createUserDictionaryPreference(null, localActivity));
    }
    for (;;)
    {
      return;
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        paramPreferenceGroup.addPreference(createUserDictionaryPreference((String)((Iterator)localObject).next(), localActivity));
      }
    }
  }
  
  protected Preference createUserDictionaryPreference(String paramString, Activity paramActivity)
  {
    paramActivity = new Preference(getPrefContext());
    Intent localIntent = new Intent("android.settings.USER_DICTIONARY_SETTINGS");
    if (paramString == null)
    {
      paramActivity.setTitle(Locale.getDefault().getDisplayName());
      paramActivity.setIntent(localIntent);
      paramActivity.setFragment(UserDictionarySettings.class.getName());
      return paramActivity;
    }
    if ("".equals(paramString)) {
      paramActivity.setTitle(getString(2131692280));
    }
    for (;;)
    {
      localIntent.putExtra("locale", paramString);
      paramActivity.getExtras().putString("locale", paramString);
      break;
      paramActivity.setTitle(Utils.createLocaleFromString(paramString).getDisplayName());
    }
  }
  
  protected int getMetricsCategory()
  {
    return 61;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    String str = null;
    super.onActivityCreated(paramBundle);
    getActivity().getActionBar().setTitle(2131692263);
    paramBundle = getActivity().getIntent();
    Bundle localBundle;
    if (paramBundle == null)
    {
      paramBundle = null;
      localBundle = getArguments();
      if (localBundle != null) {
        break label64;
      }
      label42:
      if (str == null) {
        break label74;
      }
      paramBundle = str;
    }
    for (;;)
    {
      this.mLocale = paramBundle;
      return;
      paramBundle = paramBundle.getStringExtra("locale");
      break;
      label64:
      str = localBundle.getString("locale");
      break label42;
      label74:
      if (paramBundle == null) {
        paramBundle = null;
      }
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getActivity()));
  }
  
  public void onResume()
  {
    super.onResume();
    createUserDictSettings(getPreferenceScreen());
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\inputmethod\UserDictionaryList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */