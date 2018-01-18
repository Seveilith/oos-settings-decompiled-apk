package com.oneplus.settings.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;

public class OPTimerDialog
{
  private static final int TYPE_NEGATIVE = 2;
  private static final int TYPE_POSITIVE = 1;
  private Context mContext;
  private AlertDialog mDialog = null;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      }
      do
      {
        do
        {
          do
          {
            return;
            if (OPTimerDialog.-get2(OPTimerDialog.this) > 0)
            {
              paramAnonymousMessage = OPTimerDialog.this;
              OPTimerDialog.-set0(paramAnonymousMessage, OPTimerDialog.-get2(paramAnonymousMessage) - 1);
              if (OPTimerDialog.-get4(OPTimerDialog.this) != null)
              {
                paramAnonymousMessage = (String)OPTimerDialog.-get4(OPTimerDialog.this).getText();
                OPTimerDialog.-get4(OPTimerDialog.this).setText(OPTimerDialog.this.getTimeText(paramAnonymousMessage, OPTimerDialog.-get2(OPTimerDialog.this)));
              }
              OPTimerDialog.-get1(OPTimerDialog.this).sendEmptyMessageDelayed(2, 1000L);
              return;
            }
          } while (OPTimerDialog.-get4(OPTimerDialog.this) == null);
          if (OPTimerDialog.-get4(OPTimerDialog.this).isEnabled())
          {
            OPTimerDialog.-get4(OPTimerDialog.this).performClick();
            return;
          }
          OPTimerDialog.-get4(OPTimerDialog.this).setEnabled(true);
          return;
          if (OPTimerDialog.-get3(OPTimerDialog.this) <= 0) {
            break;
          }
          paramAnonymousMessage = OPTimerDialog.this;
          OPTimerDialog.-set1(paramAnonymousMessage, OPTimerDialog.-get3(paramAnonymousMessage) - 1);
          if (OPTimerDialog.-get5(OPTimerDialog.this) != null)
          {
            paramAnonymousMessage = (String)OPTimerDialog.-get5(OPTimerDialog.this).getText();
            OPTimerDialog.this.setMessage("已经达到定时关机时间," + String.valueOf(OPTimerDialog.-get3(OPTimerDialog.this)) + "s后确认关机?");
          }
        } while (OPTimerDialog.-get1(OPTimerDialog.this) == null);
        OPTimerDialog.-get1(OPTimerDialog.this).sendEmptyMessageDelayed(1, 1000L);
        return;
      } while ((OPTimerDialog.-get5(OPTimerDialog.this) == null) || (!OPTimerDialog.-get6(OPTimerDialog.this)));
      if (OPTimerDialog.-get5(OPTimerDialog.this).isEnabled())
      {
        OPTimerDialog.-get5(OPTimerDialog.this).performClick();
        return;
      }
      OPTimerDialog.-get5(OPTimerDialog.this).setEnabled(true);
    }
  };
  private int mNegativeCount = 0;
  private int mPositiveCount = 0;
  private Button n = null;
  private Button p = null;
  private boolean status = true;
  
  public OPTimerDialog(Context paramContext)
  {
    this.mContext = paramContext;
    this.mDialog = new AlertDialog.Builder(this.mContext).create();
    this.mDialog.setCanceledOnTouchOutside(false);
  }
  
  public void dismiss()
  {
    setStatus(false);
    if (this.mHandler != null) {
      this.mHandler = null;
    }
    if (this.mDialog != null) {}
    try
    {
      this.mDialog.dismiss();
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public Button getNButton()
  {
    if (this.n != null) {
      return this.n;
    }
    return null;
  }
  
  public Button getPButton()
  {
    if (this.p != null) {
      return this.p;
    }
    return null;
  }
  
  public String getTimeText(String paramString, int paramInt)
  {
    if ((paramString != null) && (paramString.length() > 0) && (paramInt > 0))
    {
      int i = paramString.indexOf("(");
      if (i > 0)
      {
        paramString = paramString.substring(0, i);
        return paramString + "(" + paramInt + "s)";
      }
      return paramString + "(" + paramInt + "s)";
    }
    return paramString;
  }
  
  public boolean isShowing()
  {
    if (this.mDialog != null) {
      return this.mDialog.isShowing();
    }
    return false;
  }
  
  public void setButtonType(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (paramInt2 <= 0) {
      return;
    }
    if (paramInt1 == -1)
    {
      this.p = this.mDialog.getButton(-1);
      this.p.setEnabled(paramBoolean);
      this.mPositiveCount = paramInt2;
    }
    while (paramInt1 != -2) {
      return;
    }
    this.n = this.mDialog.getButton(-2);
    this.n.setEnabled(paramBoolean);
    this.mNegativeCount = paramInt2;
  }
  
  public void setMessage(String paramString)
  {
    this.mDialog.setMessage(paramString);
  }
  
  public void setNegativeButton(String paramString, final DialogInterface.OnClickListener paramOnClickListener, int paramInt)
  {
    this.mDialog.setButton(-2, paramString, paramOnClickListener);
    this.mDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
    {
      public void onCancel(DialogInterface paramAnonymousDialogInterface)
      {
        paramOnClickListener.onClick(OPTimerDialog.-get0(OPTimerDialog.this), 2);
      }
    });
  }
  
  public void setPositiveButton(String paramString, DialogInterface.OnClickListener paramOnClickListener, int paramInt)
  {
    this.mDialog.setButton(-1, paramString, paramOnClickListener);
  }
  
  public void setStatus(boolean paramBoolean)
  {
    this.status = paramBoolean;
  }
  
  public void setTitle(int paramInt)
  {
    this.mDialog.setTitle(paramInt);
  }
  
  public void setTitle(String paramString)
  {
    this.mDialog.setTitle(paramString);
  }
  
  public void show()
  {
    this.mDialog.show();
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ui\OPTimerDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */