package com.oneplus.settings.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.provider.Settings.System;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class OPListDialog
{
  private static final int TYPE_NEGATIVE = 2;
  private static final int TYPE_POSITIVE = 1;
  private Context mContext;
  private int mCurrentIndex = 0;
  private AlertDialog mDialog = null;
  private DialogListAdapter mDialogListAdapter;
  private String[] mListEntries;
  private String[] mListEntriesValue;
  private ListView mListView;
  private int mNegativeCount = 0;
  private OnDialogListItemClickListener mOnDialogListItemClickListener;
  private int mPositiveCount = 0;
  private RadioGroup mRootContainer;
  private String mVibrateKey;
  private Button n = null;
  private Button p = null;
  private boolean status = true;
  
  public OPListDialog(Context paramContext)
  {
    this.mContext = paramContext;
    this.mRootContainer = ((RadioGroup)LayoutInflater.from(paramContext).inflate(2130968822, null));
    this.mDialog = new AlertDialog.Builder(this.mContext).setView(this.mRootContainer).create();
    this.mDialog.setCanceledOnTouchOutside(true);
  }
  
  public OPListDialog(Context paramContext, CharSequence paramCharSequence, String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    this.mContext = paramContext;
    this.mListEntriesValue = paramArrayOfString1;
    this.mListEntries = paramArrayOfString2;
    paramContext = LayoutInflater.from(paramContext).inflate(2130968822, null);
    this.mRootContainer = ((RadioGroup)paramContext.findViewById(2131362353));
    this.mDialog = new AlertDialog.Builder(this.mContext).setView(this.mRootContainer).setTitle(paramCharSequence).setView(paramContext).setPositiveButton(2131690994, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        if (OPListDialog.-get3(OPListDialog.this) != null) {
          OPListDialog.-get3(OPListDialog.this).OnDialogListConfirmClick(OPListDialog.-get0(OPListDialog.this));
        }
        paramAnonymousDialogInterface.dismiss();
      }
    }).setNegativeButton(2131690993, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        if (OPListDialog.-get3(OPListDialog.this) != null) {
          OPListDialog.-get3(OPListDialog.this).OnDialogListCancelClick();
        }
        paramAnonymousDialogInterface.dismiss();
      }
    }).create();
    this.mDialog.setCanceledOnTouchOutside(true);
  }
  
  public void dismiss()
  {
    setStatus(false);
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
        paramOnClickListener.onClick(OPListDialog.-get1(OPListDialog.this), 2);
      }
    });
  }
  
  public void setOnDialogListItemClickListener(OnDialogListItemClickListener paramOnDialogListItemClickListener)
  {
    this.mOnDialogListItemClickListener = paramOnDialogListItemClickListener;
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
  
  public void setVibrateKey(String paramString)
  {
    this.mCurrentIndex = Settings.System.getInt(this.mContext.getContentResolver(), paramString, 0);
    int j = this.mListEntriesValue.length;
    int i = 0;
    if (i < j)
    {
      paramString = (RadioButton)this.mRootContainer.getChildAt(i);
      paramString.setVisibility(0);
      paramString.setText(this.mListEntries[i]);
      if (this.mCurrentIndex == i) {
        paramString.setChecked(true);
      }
      for (;;)
      {
        paramString.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            int i = 0;
            int j = paramAnonymousView.getId();
            if (2131362354 == j) {
              i = 0;
            }
            for (;;)
            {
              OPListDialog.-set0(OPListDialog.this, i);
              if (OPListDialog.-get3(OPListDialog.this) != null) {
                OPListDialog.-get3(OPListDialog.this).OnDialogListItemClick(i);
              }
              return;
              if (2131362355 == j) {
                i = 1;
              } else if (2131362356 == j) {
                i = 2;
              } else if (2131362357 == j) {
                i = 3;
              } else if (2131362358 == j) {
                i = 4;
              }
            }
          }
        });
        i += 1;
        break;
        paramString.setChecked(false);
      }
    }
  }
  
  public void show()
  {
    this.mDialog.show();
  }
  
  class DialogListAdapter
    extends BaseAdapter
  {
    DialogListAdapter() {}
    
    public int getCount()
    {
      return OPListDialog.-get2(OPListDialog.this).length;
    }
    
    public Object getItem(int paramInt)
    {
      return null;
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      return paramView;
    }
    
    class ViewHolder
    {
      ViewHolder() {}
    }
  }
  
  public static abstract interface OnDialogListItemClickListener
  {
    public abstract void OnDialogListCancelClick();
    
    public abstract void OnDialogListConfirmClick(int paramInt);
    
    public abstract void OnDialogListItemClick(int paramInt);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ui\OPListDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */