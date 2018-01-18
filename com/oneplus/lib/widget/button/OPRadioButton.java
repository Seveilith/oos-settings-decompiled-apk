package com.oneplus.lib.widget.button;

import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.oneplus.commonctrl.R.style;
import com.oneplus.lib.widget.util.utils;

public class OPRadioButton
  extends OPCompoundButton
{
  public OPRadioButton(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public OPRadioButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842878);
  }
  
  public OPRadioButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, R.style.Oneplus_DeviceDefault_Widget_Material_CompoundButton_RadioButton);
  }
  
  public OPRadioButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, utils.resolveDefStyleAttr(paramContext, paramInt1), paramInt2);
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    paramAccessibilityEvent.setClassName(OPRadioButton.class.getName());
  }
  
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
    paramAccessibilityNodeInfo.setClassName(OPRadioButton.class.getName());
  }
  
  public void toggle()
  {
    if (!isChecked()) {
      super.toggle();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\button\OPRadioButton.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */