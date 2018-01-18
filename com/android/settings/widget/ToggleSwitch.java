package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.widget.Switch;
import com.oneplus.settings.utils.OPUtils;

public class ToggleSwitch
  extends Switch
{
  private OnBeforeCheckedChangeListener mOnBeforeListener;
  
  public ToggleSwitch(Context paramContext)
  {
    super(wrapperContext(paramContext));
  }
  
  public ToggleSwitch(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(wrapperContext(paramContext), paramAttributeSet);
  }
  
  public ToggleSwitch(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(wrapperContext(paramContext), paramAttributeSet, paramInt);
  }
  
  public ToggleSwitch(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(wrapperContext(paramContext), paramAttributeSet, paramInt1, paramInt2);
  }
  
  private static Context wrapperContext(Context paramContext)
  {
    return new ContextThemeWrapper(paramContext, OPUtils.getRightTheme(paramContext, 16974410, 16974408));
  }
  
  public void setChecked(boolean paramBoolean)
  {
    if ((this.mOnBeforeListener != null) && (this.mOnBeforeListener.onBeforeCheckedChanged(this, paramBoolean))) {
      return;
    }
    super.setChecked(paramBoolean);
  }
  
  public void setCheckedInternal(boolean paramBoolean)
  {
    super.setChecked(paramBoolean);
  }
  
  public void setOnBeforeCheckedChangeListener(OnBeforeCheckedChangeListener paramOnBeforeCheckedChangeListener)
  {
    this.mOnBeforeListener = paramOnBeforeCheckedChangeListener;
  }
  
  public static abstract interface OnBeforeCheckedChangeListener
  {
    public abstract boolean onBeforeCheckedChanged(ToggleSwitch paramToggleSwitch, boolean paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\widget\ToggleSwitch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */