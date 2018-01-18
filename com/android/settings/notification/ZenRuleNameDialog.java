package com.android.settings.notification;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public abstract class ZenRuleNameDialog
{
  private static final boolean DEBUG = ZenModeSettings.DEBUG;
  private static final String TAG = "ZenRuleNameDialog";
  private final AlertDialog mDialog;
  private final EditText mEditText;
  private final boolean mIsNew;
  private final CharSequence mOriginalRuleName;
  
  public ZenRuleNameDialog(Context paramContext, CharSequence paramCharSequence)
  {
    boolean bool;
    View localView;
    if (paramCharSequence == null)
    {
      bool = true;
      this.mIsNew = bool;
      this.mOriginalRuleName = paramCharSequence;
      localView = LayoutInflater.from(paramContext).inflate(2130969128, null, false);
      this.mEditText = ((EditText)localView.findViewById(2131362825));
      if (!this.mIsNew) {
        this.mEditText.setText(paramCharSequence);
      }
      this.mEditText.setSelectAllOnFocus(true);
      paramContext = new AlertDialog.Builder(paramContext);
      if (!this.mIsNew) {
        break label134;
      }
    }
    label134:
    for (int i = 2131693266;; i = 2131693263)
    {
      this.mDialog = paramContext.setTitle(i).setView(localView).setPositiveButton(2131690994, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          paramAnonymousDialogInterface = ZenRuleNameDialog.-wrap0(ZenRuleNameDialog.this);
          if (TextUtils.isEmpty(paramAnonymousDialogInterface)) {
            return;
          }
          if ((!ZenRuleNameDialog.-get0(ZenRuleNameDialog.this)) && (ZenRuleNameDialog.-get1(ZenRuleNameDialog.this) != null) && (ZenRuleNameDialog.-get1(ZenRuleNameDialog.this).equals(paramAnonymousDialogInterface))) {
            return;
          }
          ZenRuleNameDialog.this.onOk(paramAnonymousDialogInterface);
        }
      }).setNegativeButton(2131690993, null).create();
      return;
      bool = false;
      break;
    }
  }
  
  private String trimmedText()
  {
    if (this.mEditText.getText() == null) {
      return null;
    }
    return this.mEditText.getText().toString().trim();
  }
  
  public abstract void onOk(String paramString);
  
  public void show()
  {
    this.mDialog.show();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\ZenRuleNameDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */