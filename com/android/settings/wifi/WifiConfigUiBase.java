package com.android.settings.wifi;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;

public abstract interface WifiConfigUiBase
{
  public static final int MODE_CONNECT = 1;
  public static final int MODE_MODIFY = 2;
  public static final int MODE_VIEW = 0;
  
  public abstract void dispatchSubmit();
  
  public abstract Button getCancelButton();
  
  public abstract Context getContext();
  
  public abstract WifiConfigController getController();
  
  public abstract Button getForgetButton();
  
  public abstract LayoutInflater getLayoutInflater();
  
  public abstract int getMode();
  
  public abstract Button getSubmitButton();
  
  public abstract void setCancelButton(CharSequence paramCharSequence);
  
  public abstract void setForgetButton(CharSequence paramCharSequence);
  
  public abstract void setSubmitButton(CharSequence paramCharSequence);
  
  public abstract void setTitle(int paramInt);
  
  public abstract void setTitle(CharSequence paramCharSequence);
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\WifiConfigUiBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */