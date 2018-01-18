package com.android.settings.inputmethod;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.TwoStatePreference;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import com.android.settings.SettingsPreferenceFragment;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class InputMethodAndSubtypeEnabler
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener
{
  private final HashMap<String, TwoStatePreference> mAutoSelectionPrefsMap = new HashMap();
  private Collator mCollator;
  private boolean mHaveHardKeyboard;
  private InputMethodManager mImm;
  private final HashMap<String, List<Preference>> mInputMethodAndSubtypePrefsMap = new HashMap();
  private List<InputMethodInfo> mInputMethodInfoList;
  
  private void addInputMethodSubtypePreferences(InputMethodInfo paramInputMethodInfo, PreferenceScreen paramPreferenceScreen)
  {
    Context localContext = getPrefContext();
    int j = paramInputMethodInfo.getSubtypeCount();
    if (j <= 1) {
      return;
    }
    String str = paramInputMethodInfo.getId();
    Object localObject = new PreferenceCategory(getPrefContext());
    paramPreferenceScreen.addPreference((Preference)localObject);
    ((PreferenceCategory)localObject).setTitle(paramInputMethodInfo.loadLabel(getPackageManager()));
    ((PreferenceCategory)localObject).setKey(str);
    SwitchWithNoTextPreference localSwitchWithNoTextPreference = new SwitchWithNoTextPreference(getPrefContext());
    this.mAutoSelectionPrefsMap.put(str, localSwitchWithNoTextPreference);
    ((PreferenceCategory)localObject).addPreference(localSwitchWithNoTextPreference);
    localSwitchWithNoTextPreference.setOnPreferenceChangeListener(this);
    PreferenceCategory localPreferenceCategory = new PreferenceCategory(getPrefContext());
    localPreferenceCategory.setTitle(2131692295);
    paramPreferenceScreen.addPreference(localPreferenceCategory);
    paramPreferenceScreen = null;
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    if (i < j)
    {
      InputMethodSubtype localInputMethodSubtype = paramInputMethodInfo.getSubtypeAt(i);
      if (localInputMethodSubtype.overridesImplicitlyEnabledSubtype())
      {
        localObject = paramPreferenceScreen;
        if (paramPreferenceScreen != null) {}
      }
      for (localObject = InputMethodAndSubtypeUtil.getSubtypeLocaleNameAsSentence(localInputMethodSubtype, localContext, paramInputMethodInfo);; localObject = paramPreferenceScreen)
      {
        i += 1;
        paramPreferenceScreen = (PreferenceScreen)localObject;
        break;
        localArrayList.add(new InputMethodSubtypePreference(localContext, localInputMethodSubtype, paramInputMethodInfo));
      }
    }
    Collections.sort(localArrayList, new Comparator()
    {
      public int compare(Preference paramAnonymousPreference1, Preference paramAnonymousPreference2)
      {
        if ((paramAnonymousPreference1 instanceof InputMethodSubtypePreference)) {
          return ((InputMethodSubtypePreference)paramAnonymousPreference1).compareTo(paramAnonymousPreference2, InputMethodAndSubtypeEnabler.-get0(InputMethodAndSubtypeEnabler.this));
        }
        return paramAnonymousPreference1.compareTo(paramAnonymousPreference2);
      }
    });
    j = localArrayList.size();
    i = 0;
    while (i < j)
    {
      paramInputMethodInfo = (Preference)localArrayList.get(i);
      localPreferenceCategory.addPreference(paramInputMethodInfo);
      paramInputMethodInfo.setOnPreferenceChangeListener(this);
      InputMethodAndSubtypeUtil.removeUnnecessaryNonPersistentPreference(paramInputMethodInfo);
      i += 1;
    }
    this.mInputMethodAndSubtypePrefsMap.put(str, localArrayList);
    if (TextUtils.isEmpty(paramPreferenceScreen))
    {
      localSwitchWithNoTextPreference.setTitle(2131692296);
      return;
    }
    localSwitchWithNoTextPreference.setTitle(paramPreferenceScreen);
  }
  
  private String getStringExtraFromIntentOrArguments(String paramString)
  {
    Object localObject = getActivity().getIntent().getStringExtra(paramString);
    if (localObject != null) {
      return (String)localObject;
    }
    localObject = getArguments();
    if (localObject == null) {
      return null;
    }
    return ((Bundle)localObject).getString(paramString);
  }
  
  private boolean isNoSubtypesExplicitlySelected(String paramString)
  {
    paramString = ((List)this.mInputMethodAndSubtypePrefsMap.get(paramString)).iterator();
    while (paramString.hasNext())
    {
      Preference localPreference = (Preference)paramString.next();
      if (((localPreference instanceof TwoStatePreference)) && (((TwoStatePreference)localPreference).isChecked())) {
        return false;
      }
    }
    return true;
  }
  
  private void setAutoSelectionSubtypesEnabled(String paramString, boolean paramBoolean)
  {
    Object localObject = (TwoStatePreference)this.mAutoSelectionPrefsMap.get(paramString);
    if (localObject == null) {
      return;
    }
    ((TwoStatePreference)localObject).setChecked(paramBoolean);
    localObject = ((List)this.mInputMethodAndSubtypePrefsMap.get(paramString)).iterator();
    while (((Iterator)localObject).hasNext())
    {
      Preference localPreference = (Preference)((Iterator)localObject).next();
      if ((localPreference instanceof TwoStatePreference))
      {
        if (paramBoolean) {}
        for (boolean bool = false;; bool = true)
        {
          localPreference.setEnabled(bool);
          if (!paramBoolean) {
            break;
          }
          ((TwoStatePreference)localPreference).setChecked(false);
          break;
        }
      }
    }
    if (paramBoolean)
    {
      InputMethodAndSubtypeUtil.saveInputMethodSubtypeList(this, getContentResolver(), this.mInputMethodInfoList, this.mHaveHardKeyboard);
      updateImplicitlyEnabledSubtypes(paramString, true);
    }
  }
  
  private void updateAutoSelectionPreferences()
  {
    Iterator localIterator = this.mInputMethodAndSubtypePrefsMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      setAutoSelectionSubtypesEnabled(str, isNoSubtypesExplicitlySelected(str));
    }
    updateImplicitlyEnabledSubtypes(null, true);
  }
  
  private void updateImplicitlyEnabledSubtypes(String paramString, boolean paramBoolean)
  {
    Iterator localIterator = this.mInputMethodInfoList.iterator();
    while (localIterator.hasNext())
    {
      InputMethodInfo localInputMethodInfo = (InputMethodInfo)localIterator.next();
      String str = localInputMethodInfo.getId();
      TwoStatePreference localTwoStatePreference = (TwoStatePreference)this.mAutoSelectionPrefsMap.get(str);
      if ((localTwoStatePreference != null) && (localTwoStatePreference.isChecked()) && ((str.equals(paramString)) || (paramString == null))) {
        updateImplicitlyEnabledSubtypesOf(localInputMethodInfo, paramBoolean);
      }
    }
  }
  
  private void updateImplicitlyEnabledSubtypesOf(InputMethodInfo paramInputMethodInfo, boolean paramBoolean)
  {
    String str = paramInputMethodInfo.getId();
    Object localObject1 = (List)this.mInputMethodAndSubtypePrefsMap.get(str);
    paramInputMethodInfo = this.mImm.getEnabledInputMethodSubtypeList(paramInputMethodInfo, true);
    if ((localObject1 == null) || (paramInputMethodInfo == null)) {
      return;
    }
    localObject1 = ((Iterable)localObject1).iterator();
    for (;;)
    {
      if (!((Iterator)localObject1).hasNext()) {
        return;
      }
      Object localObject2 = (Preference)((Iterator)localObject1).next();
      if ((localObject2 instanceof TwoStatePreference))
      {
        localObject2 = (TwoStatePreference)localObject2;
        ((TwoStatePreference)localObject2).setChecked(false);
        if (paramBoolean)
        {
          Iterator localIterator = paramInputMethodInfo.iterator();
          if (localIterator.hasNext())
          {
            Object localObject3 = (InputMethodSubtype)localIterator.next();
            localObject3 = str + ((InputMethodSubtype)localObject3).hashCode();
            if (!((TwoStatePreference)localObject2).getKey().equals(localObject3)) {
              break;
            }
            ((TwoStatePreference)localObject2).setChecked(true);
          }
        }
      }
    }
  }
  
  protected int getMetricsCategory()
  {
    return 60;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    paramBundle = getStringExtraFromIntentOrArguments("android.intent.extra.TITLE");
    if (!TextUtils.isEmpty(paramBundle)) {
      getActivity().setTitle(paramBundle);
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mImm = ((InputMethodManager)getSystemService("input_method"));
    if (getResources().getConfiguration().keyboard == 2) {}
    PreferenceScreen localPreferenceScreen;
    for (boolean bool = true;; bool = false)
    {
      this.mHaveHardKeyboard = bool;
      paramBundle = getStringExtraFromIntentOrArguments("input_method_id");
      this.mInputMethodInfoList = this.mImm.getInputMethodList();
      this.mCollator = Collator.getInstance();
      localPreferenceScreen = getPreferenceManager().createPreferenceScreen(getActivity());
      int j = this.mInputMethodInfoList.size();
      int i = 0;
      while (i < j)
      {
        InputMethodInfo localInputMethodInfo = (InputMethodInfo)this.mInputMethodInfoList.get(i);
        if ((localInputMethodInfo.getId().equals(paramBundle)) || (TextUtils.isEmpty(paramBundle))) {
          addInputMethodSubtypePreferences(localInputMethodInfo, localPreferenceScreen);
        }
        i += 1;
      }
    }
    setPreferenceScreen(localPreferenceScreen);
  }
  
  public void onPause()
  {
    super.onPause();
    InputMethodAndSubtypeUtil.saveInputMethodSubtypeList(this, getContentResolver(), this.mInputMethodInfoList, this.mHaveHardKeyboard);
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if (!(paramObject instanceof Boolean)) {
      return true;
    }
    boolean bool = ((Boolean)paramObject).booleanValue();
    Iterator localIterator = this.mAutoSelectionPrefsMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      paramObject = (String)localIterator.next();
      if (this.mAutoSelectionPrefsMap.get(paramObject) == paramPreference)
      {
        paramPreference = (TwoStatePreference)paramPreference;
        paramPreference.setChecked(bool);
        setAutoSelectionSubtypesEnabled((String)paramObject, paramPreference.isChecked());
        return false;
      }
    }
    if ((paramPreference instanceof InputMethodSubtypePreference))
    {
      paramPreference = (InputMethodSubtypePreference)paramPreference;
      paramPreference.setChecked(bool);
      if (!paramPreference.isChecked()) {
        updateAutoSelectionPreferences();
      }
      return false;
    }
    return true;
  }
  
  public void onResume()
  {
    super.onResume();
    InputMethodSettingValuesWrapper.getInstance(getActivity()).refreshAllInputMethodAndSubtypes();
    InputMethodAndSubtypeUtil.loadInputMethodSubtypeList(this, getContentResolver(), this.mInputMethodInfoList, this.mInputMethodAndSubtypePrefsMap);
    updateAutoSelectionPreferences();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\inputmethod\InputMethodAndSubtypeEnabler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */