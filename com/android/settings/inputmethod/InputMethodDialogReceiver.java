package com.android.settings.inputmethod;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.InputMethodManager;

public class InputMethodDialogReceiver
  extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if ("android.settings.SHOW_INPUT_METHOD_PICKER".equals(paramIntent.getAction())) {
      ((InputMethodManager)paramContext.getSystemService("input_method")).showInputMethodPicker(true);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\inputmethod\InputMethodDialogReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */