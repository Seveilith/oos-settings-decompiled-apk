package com.android.settings.bluetooth;

import android.content.Context;
import android.util.AttributeSet;
import com.android.settings.ProgressCategory;

public class BluetoothProgressCategory
  extends ProgressCategory
{
  public BluetoothProgressCategory(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public BluetoothProgressCategory(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public BluetoothProgressCategory(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public BluetoothProgressCategory(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setEmptyTextRes(2131690869);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\bluetooth\BluetoothProgressCategory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */