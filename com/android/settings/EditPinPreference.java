package com.android.settings;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

class EditPinPreference
  extends CustomEditTextPreference
{
  private OnPinEnteredListener mPinListener;
  
  public EditPinPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setDialogLayoutResource(2130968737);
  }
  
  public EditPinPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setDialogLayoutResource(2130968737);
  }
  
  public boolean isDialogOpen()
  {
    Dialog localDialog = getDialog();
    if (localDialog != null) {
      return localDialog.isShowing();
    }
    return false;
  }
  
  protected void onBindDialogView(View paramView)
  {
    super.onBindDialogView(paramView);
    paramView = (EditText)paramView.findViewById(16908291);
    if (paramView != null) {
      paramView.setInputType(18);
    }
  }
  
  protected void onDialogClosed(boolean paramBoolean)
  {
    super.onDialogClosed(paramBoolean);
    if (this.mPinListener != null) {
      this.mPinListener.onPinEntered(this, paramBoolean);
    }
  }
  
  public void setOnPinEnteredListener(OnPinEnteredListener paramOnPinEnteredListener)
  {
    this.mPinListener = paramOnPinEnteredListener;
  }
  
  public void showPinDialog()
  {
    Dialog localDialog = getDialog();
    if ((localDialog != null) && (localDialog.isShowing())) {
      return;
    }
    onClick();
  }
  
  static abstract interface OnPinEnteredListener
  {
    public abstract void onPinEntered(EditPinPreference paramEditPinPreference, boolean paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\EditPinPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */