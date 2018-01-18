package com.oneplus.settings.laboratory;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class OPRadioButtinGroup
  extends LinearLayout
  implements View.OnClickListener
{
  private Context mContext;
  public OnRadioGroupClickListener mOnRadioGroupClickListener;
  
  public OPRadioButtinGroup(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
  }
  
  public OPRadioButtinGroup(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContext = paramContext;
  }
  
  public OPRadioButtinGroup(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.mContext = paramContext;
  }
  
  public OPRadioButtinGroup(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    this.mContext = paramContext;
  }
  
  public void addChild(int paramInt, String[] paramArrayOfString)
  {
    int i = 0;
    while (i < paramInt)
    {
      View localView = LayoutInflater.from(this.mContext).inflate(2130968850, null);
      ((TextView)localView.findViewById(2131361894)).setText(paramArrayOfString[i]);
      localView.setId(i);
      localView.setOnClickListener(this);
      addView(localView, i);
      i += 1;
    }
  }
  
  public void onClick(View paramView)
  {
    setSelect(paramView);
    if (this.mOnRadioGroupClickListener != null) {
      this.mOnRadioGroupClickListener.OnRadioGroupClick(paramView.getId());
    }
  }
  
  public void setOnRadioGroupClickListener(OnRadioGroupClickListener paramOnRadioGroupClickListener)
  {
    this.mOnRadioGroupClickListener = paramOnRadioGroupClickListener;
  }
  
  public void setSelect(int paramInt)
  {
    int j = getChildCount();
    int i = 0;
    if (i < j)
    {
      RadioButton localRadioButton = (RadioButton)getChildAt(i).findViewById(2131362378);
      if (paramInt == i) {
        localRadioButton.setChecked(true);
      }
      for (;;)
      {
        i += 1;
        break;
        localRadioButton.setChecked(false);
      }
    }
  }
  
  public void setSelect(View paramView)
  {
    int j = getChildCount();
    int i = 0;
    if (i < j)
    {
      RadioButton localRadioButton = (RadioButton)getChildAt(i).findViewById(2131362378);
      if (paramView.getId() == i) {
        localRadioButton.setChecked(true);
      }
      for (;;)
      {
        i += 1;
        break;
        localRadioButton.setChecked(false);
      }
    }
  }
  
  public static abstract interface OnRadioGroupClickListener
  {
    public abstract void OnRadioGroupClick(int paramInt);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\laboratory\OPRadioButtinGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */