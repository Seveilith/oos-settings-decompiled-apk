package com.oneplus.lib.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.oneplus.commonctrl.R.attr;
import com.oneplus.commonctrl.R.style;
import com.oneplus.lib.widget.util.utils;

public class OPSeekBar
  extends OPAbsSeekBar
{
  private OnSeekBarChangeListener mOnSeekBarChangeListener;
  
  public OPSeekBar(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public OPSeekBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.OPSeekBarStyle);
  }
  
  public OPSeekBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, R.style.Oneplus_DeviceDefault_Widget_Material_SeekBar);
  }
  
  public OPSeekBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, utils.resolveDefStyleAttr(paramContext, paramInt1), paramInt2);
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return OPSeekBar.class.getName();
  }
  
  void onProgressRefresh(float paramFloat, boolean paramBoolean, int paramInt)
  {
    super.onProgressRefresh(paramFloat, paramBoolean, paramInt);
    if (this.mOnSeekBarChangeListener != null) {
      this.mOnSeekBarChangeListener.onProgressChanged(this, paramInt, paramBoolean);
    }
  }
  
  void onStartTrackingTouch()
  {
    super.onStartTrackingTouch();
    if (this.mOnSeekBarChangeListener != null) {
      this.mOnSeekBarChangeListener.onStartTrackingTouch(this);
    }
  }
  
  void onStopTrackingTouch()
  {
    super.onStopTrackingTouch();
    if (this.mOnSeekBarChangeListener != null) {
      this.mOnSeekBarChangeListener.onStopTrackingTouch(this);
    }
  }
  
  public void setOnSeekBarChangeListener(OnSeekBarChangeListener paramOnSeekBarChangeListener)
  {
    this.mOnSeekBarChangeListener = paramOnSeekBarChangeListener;
  }
  
  public static abstract interface OnSeekBarChangeListener
  {
    public abstract void onProgressChanged(OPSeekBar paramOPSeekBar, int paramInt, boolean paramBoolean);
    
    public abstract void onStartTrackingTouch(OPSeekBar paramOPSeekBar);
    
    public abstract void onStopTrackingTouch(OPSeekBar paramOPSeekBar);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\OPSeekBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */