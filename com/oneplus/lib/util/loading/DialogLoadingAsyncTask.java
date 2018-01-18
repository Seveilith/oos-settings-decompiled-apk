package com.oneplus.lib.util.loading;

import android.app.Dialog;

public abstract class DialogLoadingAsyncTask<Param, Progress, Result>
  extends LoadingAsyncTask<Param, Progress, Result>
{
  private Dialog mDialog;
  
  public DialogLoadingAsyncTask(Dialog paramDialog)
  {
    this.mDialog = paramDialog;
  }
  
  protected void hideProgree(Object paramObject)
  {
    if (this.mDialog != null) {}
    try
    {
      this.mDialog.dismiss();
      return;
    }
    catch (Throwable paramObject) {}
  }
  
  protected Object showProgree()
  {
    if (this.mDialog != null) {}
    try
    {
      this.mDialog.show();
      return this.mDialog;
    }
    catch (Throwable localThrowable)
    {
      for (;;) {}
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\util\loading\DialogLoadingAsyncTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */