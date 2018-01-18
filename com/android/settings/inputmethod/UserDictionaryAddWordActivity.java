package com.android.settings.inputmethod;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.view.Window;

public class UserDictionaryAddWordActivity
  extends Activity
{
  static final int CODE_ALREADY_PRESENT = 2;
  static final int CODE_CANCEL = 1;
  static final int CODE_WORD_ADDED = 0;
  public static final String MODE_EDIT_ACTION = "com.android.settings.USER_DICTIONARY_EDIT";
  public static final String MODE_INSERT_ACTION = "com.android.settings.USER_DICTIONARY_INSERT";
  private UserDictionaryAddWordContents mContents;
  
  private void reportBackToCaller(int paramInt, Bundle paramBundle)
  {
    Object localObject = getIntent();
    if (((Intent)localObject).getExtras() == null) {
      return;
    }
    localObject = ((Intent)localObject).getExtras().get("listener");
    if (!(localObject instanceof Messenger)) {
      return;
    }
    localObject = (Messenger)localObject;
    Message localMessage = Message.obtain();
    localMessage.obj = paramBundle;
    localMessage.what = paramInt;
    try
    {
      ((Messenger)localObject).send(localMessage);
      return;
    }
    catch (RemoteException paramBundle) {}
  }
  
  public void onClickCancel(View paramView)
  {
    reportBackToCaller(1, null);
    finish();
  }
  
  public void onClickConfirm(View paramView)
  {
    paramView = new Bundle();
    reportBackToCaller(this.mContents.apply(this, paramView), paramView);
    finish();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130969097);
    Object localObject1 = getIntent();
    Object localObject2 = ((Intent)localObject1).getAction();
    if ("com.android.settings.USER_DICTIONARY_EDIT".equals(localObject2)) {}
    for (int i = 0;; i = 1)
    {
      localObject2 = ((Intent)localObject1).getExtras();
      localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = new Bundle();
      }
      ((Bundle)localObject1).putInt("mode", i);
      if (paramBundle != null) {
        ((Bundle)localObject1).putAll(paramBundle);
      }
      this.mContents = new UserDictionaryAddWordContents(getWindow().getDecorView(), (Bundle)localObject1);
      return;
      if (!"com.android.settings.USER_DICTIONARY_INSERT".equals(localObject2)) {
        break;
      }
    }
    throw new RuntimeException("Unsupported action: " + (String)localObject2);
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    this.mContents.saveStateIntoBundle(paramBundle);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\inputmethod\UserDictionaryAddWordActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */