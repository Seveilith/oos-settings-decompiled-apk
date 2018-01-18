package com.oneplus.lib.widget.button;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RemoteViews.RemoteView;
import android.widget.TextView;
import com.oneplus.commonctrl.R.style;
import com.oneplus.lib.widget.util.utils;

@RemoteViews.RemoteView
public class OPButton
  extends TextView
{
  public OPButton(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public OPButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842824);
  }
  
  public OPButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, R.style.OnePlus_DeviceDefault_Widget_Material_Button);
  }
  
  public OPButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, utils.resolveDefStyleAttr(paramContext, paramInt1), paramInt2);
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return OPButton.class.getName();
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\button\OPButton.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */