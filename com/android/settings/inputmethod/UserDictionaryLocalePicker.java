package com.android.settings.inputmethod;

import com.android.internal.app.LocalePicker;

public class UserDictionaryLocalePicker
  extends LocalePicker
{
  public UserDictionaryLocalePicker(UserDictionaryAddWordFragment paramUserDictionaryAddWordFragment)
  {
    setLocaleSelectionListener(paramUserDictionaryAddWordFragment);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\inputmethod\UserDictionaryLocalePicker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */