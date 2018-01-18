package com.oneplus.lib.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import com.oneplus.commonctrl.R.attr;
import com.oneplus.commonctrl.R.styleable;

public class CheckBoxPreference
  extends TwoStatePreference
{
  public CheckBoxPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public CheckBoxPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.op_checkBoxPreferenceStyle);
  }
  
  public CheckBoxPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public CheckBoxPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.CheckBoxPreference, paramInt1, paramInt2);
    setSummaryOn(paramContext.getString(R.styleable.CheckBoxPreference_android_summaryOn));
    setSummaryOff(paramContext.getString(R.styleable.CheckBoxPreference_android_summaryOff));
    setDisableDependentsState(paramContext.getBoolean(R.styleable.CheckBoxPreference_android_disableDependentsState, false));
    paramContext.recycle();
  }
  
  protected void onBindView(View paramView)
  {
    super.onBindView(paramView);
    View localView = paramView.findViewById(16908289);
    if ((localView != null) && ((localView instanceof Checkable))) {
      ((Checkable)localView).setChecked(this.mChecked);
    }
    syncSummaryView(paramView);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\preference\CheckBoxPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */