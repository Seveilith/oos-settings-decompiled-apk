package com.oneplus.lib.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;
import com.oneplus.commonctrl.R.id;
import com.oneplus.commonctrl.R.layout;

public class OPNumberPickerDialog
  extends OPAlertDialog
  implements DialogInterface.OnClickListener
{
  private final int OP_NUMBER_PICKER_DEFAULT_MAX_VALUE = 60;
  private final int OP_NUMBER_PICKER_DEFAULT_MIN_VALUE = 1;
  private int mMaxValue = 60;
  private TextView mMin;
  private int mMinValue = 1;
  private NumberPicker mNumberPicker;
  private OnNumberSetListener mNumberSetListener;
  private int mPlurals;
  private int mValue = 1;
  
  public OPNumberPickerDialog(Context paramContext)
  {
    this(paramContext, 0);
  }
  
  public OPNumberPickerDialog(Context paramContext, int paramInt)
  {
    super(paramContext, paramInt);
  }
  
  public OPNumberPickerDialog(Context paramContext, int paramInt, OnNumberSetListener paramOnNumberSetListener)
  {
    super(paramContext, paramInt);
    this.mNumberSetListener = paramOnNumberSetListener;
  }
  
  public OPNumberPickerDialog(Context paramContext, OnNumberSetListener paramOnNumberSetListener)
  {
    this(paramContext, 0, paramOnNumberSetListener);
  }
  
  private void updateMinutes()
  {
    if (this.mPlurals != 0) {
      this.mMin.setText(String.format(this.mContext.getResources().getQuantityText(this.mPlurals, this.mNumberPicker.getValue()).toString(), new Object[0]));
    }
  }
  
  public int getMaxValue()
  {
    return this.mMinValue;
  }
  
  public int getMinValue()
  {
    return this.mMinValue;
  }
  
  public int getValue()
  {
    return this.mValue;
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    switch (paramInt)
    {
    default: 
    case -1: 
      do
      {
        return;
      } while (this.mNumberSetListener == null);
      this.mNumberSetListener.onNumberSet(this.mNumberPicker, this.mNumberPicker.getValue());
      return;
    }
    cancel();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    View localView = LayoutInflater.from(this.mContext).inflate(R.layout.op_number_picker_dialog, null);
    this.mNumberPicker = ((NumberPicker)localView.findViewById(R.id.numberPicker));
    this.mNumberPicker.setMinValue(1);
    this.mNumberPicker.setMaxValue(30);
    this.mNumberPicker.setValue(this.mValue);
    this.mMin = ((TextView)localView.findViewById(R.id.min));
    updateMinutes();
    this.mNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
    {
      public void onValueChange(NumberPicker paramAnonymousNumberPicker, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        OPNumberPickerDialog.-wrap0(OPNumberPickerDialog.this);
      }
    });
    setButton(-1, this.mContext.getString(17039370), this);
    setButton(-2, this.mContext.getString(17039360), this);
    setView(localView);
    super.onCreate(paramBundle);
  }
  
  public void setMaxValue(int paramInt)
  {
    this.mMinValue = paramInt;
  }
  
  public void setMinValue(int paramInt)
  {
    this.mMinValue = paramInt;
  }
  
  public void setPlurals(int paramInt)
  {
    this.mPlurals = paramInt;
  }
  
  public void setValue(int paramInt)
  {
    this.mValue = paramInt;
  }
  
  public void updateNumber(int paramInt)
  {
    this.mNumberPicker.setValue(paramInt);
  }
  
  public static abstract interface OnNumberSetListener
  {
    public abstract void onNumberSet(NumberPicker paramNumberPicker, int paramInt);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\app\OPNumberPickerDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */