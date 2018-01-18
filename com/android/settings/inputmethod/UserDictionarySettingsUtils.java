package com.android.settings.inputmethod;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import com.android.settings.Utils;
import java.util.Locale;

public class UserDictionarySettingsUtils
{
  public static String getLocaleDisplayName(Context paramContext, String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return paramContext.getResources().getString(2131692280);
    }
    return Utils.createLocaleFromString(paramString).getDisplayName(paramContext.getResources().getConfiguration().locale);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\inputmethod\UserDictionarySettingsUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */