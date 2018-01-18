package com.oneplus.lib.widget.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Switch;
import com.oneplus.commonctrl.R.style;
import com.oneplus.commonctrl.R.styleable;
import com.oneplus.lib.widget.util.utils;

public class OPSwitch
  extends Switch
{
  public static String TAG = OPSwitch.class.getSimpleName();
  
  public OPSwitch(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public OPSwitch(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16843839);
  }
  
  public OPSwitch(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, R.style.Oneplus_DeviceDefault_Widget_Material_CompoundButton_Switch);
  }
  
  public OPSwitch(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, utils.resolveDefStyleAttr(paramContext, paramInt1), paramInt2);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.OPSwitch, paramInt1, paramInt2);
    setRadius(paramContext.getDimensionPixelSize(R.styleable.OPSwitch_android_radius, -1));
    paramContext.recycle();
  }
  
  private void setRadius(int paramInt)
  {
    if (paramInt == -1) {
      return;
    }
    Drawable localDrawable = getBackground();
    if ((localDrawable != null) && ((localDrawable instanceof RippleDrawable)))
    {
      localDrawable.mutate();
      ((RippleDrawable)localDrawable).setRadius(paramInt);
      return;
    }
    Log.i(TAG, "setRaidus fail , background not a rippleDrawable");
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\button\OPSwitch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */