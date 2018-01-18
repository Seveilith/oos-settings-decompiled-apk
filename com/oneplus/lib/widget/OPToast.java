package com.oneplus.lib.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.oneplus.commonctrl.R.attr;
import com.oneplus.commonctrl.R.layout;
import com.oneplus.commonctrl.R.style;
import com.oneplus.commonctrl.R.styleable;
import java.lang.reflect.Field;

public class OPToast
  extends Toast
{
  public OPToast(Context paramContext)
  {
    super(paramContext);
  }
  
  public static OPToast makeText(Context paramContext, int paramInt1, int paramInt2)
    throws Resources.NotFoundException
  {
    return makeText(paramContext, paramContext.getResources().getText(paramInt1), paramInt2);
  }
  
  public static OPToast makeText(Context paramContext, CharSequence paramCharSequence, int paramInt)
  {
    OPToast localOPToast = new OPToast(paramContext);
    LayoutInflater localLayoutInflater = (LayoutInflater)paramContext.getSystemService("layout_inflater");
    paramContext = paramContext.obtainStyledAttributes(null, R.styleable.OPToast, R.attr.OPToastStyle, R.style.Oneplus_DeviceDefault_OPToast);
    int i = paramContext.getResourceId(R.styleable.OPToast_android_layout, R.layout.op_transient_notification_light);
    paramContext.recycle();
    paramContext = localLayoutInflater.inflate(i, null);
    ((TextView)paramContext.findViewById(16908299)).setText(paramCharSequence);
    setViewAndDuation(localOPToast, paramContext, paramInt);
    return localOPToast;
  }
  
  private static void setViewAndDuation(OPToast paramOPToast, View paramView, int paramInt)
  {
    try
    {
      Object localObject = OPToast.class.getSuperclass();
      Field localField = ((Class)localObject).getDeclaredField("mNextView");
      localObject = ((Class)localObject).getDeclaredField("mDuration");
      localField.setAccessible(true);
      localField.set(paramOPToast, paramView);
      ((Field)localObject).setAccessible(true);
      ((Field)localObject).set(paramOPToast, Integer.valueOf(paramInt));
      return;
    }
    catch (NoSuchFieldException paramOPToast)
    {
      paramOPToast.printStackTrace();
      return;
    }
    catch (IllegalAccessException paramOPToast)
    {
      paramOPToast.printStackTrace();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\OPToast.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */