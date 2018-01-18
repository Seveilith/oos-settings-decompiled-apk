package com.android.settings.inputmethod;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.preference.Preference;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;
import com.android.internal.inputmethod.InputMethodUtils;
import java.text.Collator;
import java.util.Locale;

class InputMethodSubtypePreference
  extends SwitchWithNoTextPreference
{
  private final boolean mIsSystemLanguage;
  private final boolean mIsSystemLocale;
  
  InputMethodSubtypePreference(Context paramContext, InputMethodSubtype paramInputMethodSubtype, InputMethodInfo paramInputMethodInfo)
  {
    super(paramContext);
    setPersistent(false);
    setKey(paramInputMethodInfo.getId() + paramInputMethodSubtype.hashCode());
    setTitle(InputMethodAndSubtypeUtil.getSubtypeLocaleNameAsSentence(paramInputMethodSubtype, paramContext, paramInputMethodInfo));
    paramInputMethodSubtype = paramInputMethodSubtype.getLocale();
    if (TextUtils.isEmpty(paramInputMethodSubtype))
    {
      this.mIsSystemLocale = false;
      this.mIsSystemLanguage = false;
      return;
    }
    paramContext = paramContext.getResources().getConfiguration().locale;
    this.mIsSystemLocale = paramInputMethodSubtype.equals(paramContext.toString());
    if (!this.mIsSystemLocale) {}
    for (boolean bool = InputMethodUtils.getLanguageFromLocaleString(paramInputMethodSubtype).equals(paramContext.getLanguage());; bool = true)
    {
      this.mIsSystemLanguage = bool;
      return;
    }
  }
  
  int compareTo(Preference paramPreference, Collator paramCollator)
  {
    if (this == paramPreference) {
      return 0;
    }
    if ((paramPreference instanceof InputMethodSubtypePreference))
    {
      InputMethodSubtypePreference localInputMethodSubtypePreference = (InputMethodSubtypePreference)paramPreference;
      CharSequence localCharSequence = getTitle();
      paramPreference = paramPreference.getTitle();
      if (TextUtils.equals(localCharSequence, paramPreference)) {
        return 0;
      }
      if (this.mIsSystemLocale) {
        return -1;
      }
      if (localInputMethodSubtypePreference.mIsSystemLocale) {
        return 1;
      }
      if (this.mIsSystemLanguage) {
        return -1;
      }
      if (localInputMethodSubtypePreference.mIsSystemLanguage) {
        return 1;
      }
      if (TextUtils.isEmpty(localCharSequence)) {
        return 1;
      }
      if (TextUtils.isEmpty(paramPreference)) {
        return -1;
      }
      return paramCollator.compare(localCharSequence.toString(), paramPreference.toString());
    }
    return super.compareTo(paramPreference);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\inputmethod\InputMethodSubtypePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */