package com.android.settings.accessibility;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.preference.ListPreference;
import android.util.AttributeSet;
import com.android.internal.app.LocalePicker;
import com.android.internal.app.LocalePicker.LocaleInfo;
import java.util.List;
import java.util.Locale;

public class LocalePreference
  extends ListPreference
{
  public LocalePreference(Context paramContext)
  {
    super(paramContext);
    init(paramContext);
  }
  
  public LocalePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramContext);
  }
  
  public void init(Context paramContext)
  {
    List localList = LocalePicker.getAllAssetLocales(paramContext, false);
    int j = localList.size();
    CharSequence[] arrayOfCharSequence1 = new CharSequence[j + 1];
    CharSequence[] arrayOfCharSequence2 = new CharSequence[j + 1];
    arrayOfCharSequence1[0] = paramContext.getResources().getString(2131692372);
    arrayOfCharSequence2[0] = "";
    int i = 0;
    while (i < j)
    {
      paramContext = (LocalePicker.LocaleInfo)localList.get(i);
      arrayOfCharSequence1[(i + 1)] = paramContext.toString();
      arrayOfCharSequence2[(i + 1)] = paramContext.getLocale().toString();
      i += 1;
    }
    setEntries(arrayOfCharSequence1);
    setEntryValues(arrayOfCharSequence2);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accessibility\LocalePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */