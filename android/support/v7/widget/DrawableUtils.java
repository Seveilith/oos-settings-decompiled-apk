package android.support.v7.widget;

import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.DrawableContainer.DrawableContainerState;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public class DrawableUtils
{
  public static final Rect INSETS_NONE = new Rect();
  private static final String TAG = "DrawableUtils";
  private static final String VECTOR_DRAWABLE_CLAZZ_NAME = "android.graphics.drawable.VectorDrawable";
  private static Class<?> sInsetsClazz;
  
  static
  {
    if (Build.VERSION.SDK_INT >= 18) {}
    try
    {
      sInsetsClazz = Class.forName("android.graphics.Insets");
      return;
    }
    catch (ClassNotFoundException localClassNotFoundException) {}
  }
  
  public static boolean canSafelyMutateDrawable(@NonNull Drawable paramDrawable)
  {
    if ((Build.VERSION.SDK_INT < 15) && ((paramDrawable instanceof InsetDrawable))) {
      return false;
    }
    if ((Build.VERSION.SDK_INT < 15) && ((paramDrawable instanceof GradientDrawable))) {
      return false;
    }
    if ((Build.VERSION.SDK_INT < 17) && ((paramDrawable instanceof LayerDrawable))) {
      return false;
    }
    if ((paramDrawable instanceof DrawableContainer))
    {
      paramDrawable = paramDrawable.getConstantState();
      if ((paramDrawable instanceof DrawableContainer.DrawableContainerState))
      {
        paramDrawable = ((DrawableContainer.DrawableContainerState)paramDrawable).getChildren();
        int j = paramDrawable.length;
        int i = 0;
        while (i < j)
        {
          if (!canSafelyMutateDrawable(paramDrawable[i])) {
            return false;
          }
          i += 1;
        }
      }
    }
    else
    {
      if ((paramDrawable instanceof android.support.v4.graphics.drawable.DrawableWrapper)) {
        return canSafelyMutateDrawable(((android.support.v4.graphics.drawable.DrawableWrapper)paramDrawable).getWrappedDrawable());
      }
      if ((paramDrawable instanceof android.support.v7.graphics.drawable.DrawableWrapper)) {
        return canSafelyMutateDrawable(((android.support.v7.graphics.drawable.DrawableWrapper)paramDrawable).getWrappedDrawable());
      }
      if ((paramDrawable instanceof ScaleDrawable)) {
        return canSafelyMutateDrawable(((ScaleDrawable)paramDrawable).getDrawable());
      }
    }
    return true;
  }
  
  static void fixDrawable(@NonNull Drawable paramDrawable)
  {
    if ((Build.VERSION.SDK_INT == 21) && ("android.graphics.drawable.VectorDrawable".equals(paramDrawable.getClass().getName()))) {
      fixVectorDrawableTinting(paramDrawable);
    }
  }
  
  private static void fixVectorDrawableTinting(Drawable paramDrawable)
  {
    int[] arrayOfInt = paramDrawable.getState();
    if ((arrayOfInt == null) || (arrayOfInt.length == 0)) {
      paramDrawable.setState(ThemeUtils.CHECKED_STATE_SET);
    }
    for (;;)
    {
      paramDrawable.setState(arrayOfInt);
      return;
      paramDrawable.setState(ThemeUtils.EMPTY_STATE_SET);
    }
  }
  
  public static Rect getOpticalBounds(Drawable paramDrawable)
  {
    int i = 0;
    if (sInsetsClazz != null) {}
    for (;;)
    {
      Rect localRect;
      Field localField;
      String str;
      try
      {
        paramDrawable = DrawableCompat.unwrap(paramDrawable);
        paramDrawable = paramDrawable.getClass().getMethod("getOpticalInsets", new Class[0]).invoke(paramDrawable, new Object[0]);
        if (paramDrawable != null)
        {
          localRect = new Rect();
          Field[] arrayOfField = sInsetsClazz.getFields();
          int j = arrayOfField.length;
          if (i >= j) {
            break label182;
          }
          localField = arrayOfField[i];
          str = localField.getName();
          if (str.equals("left")) {
            localRect.left = localField.getInt(paramDrawable);
          } else if (str.equals("top")) {
            localRect.top = localField.getInt(paramDrawable);
          }
        }
      }
      catch (Exception paramDrawable)
      {
        Log.e("DrawableUtils", "Couldn't obtain the optical insets. Ignoring.");
      }
      return INSETS_NONE;
      if (str.equals("right"))
      {
        localRect.right = localField.getInt(paramDrawable);
      }
      else if (str.equals("bottom"))
      {
        localRect.bottom = localField.getInt(paramDrawable);
        break label184;
        label182:
        return localRect;
      }
      label184:
      i += 1;
    }
  }
  
  static PorterDuff.Mode parseTintMode(int paramInt, PorterDuff.Mode paramMode)
  {
    switch (paramInt)
    {
    case 4: 
    case 6: 
    case 7: 
    case 8: 
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    default: 
      return paramMode;
    case 3: 
      return PorterDuff.Mode.SRC_OVER;
    case 5: 
      return PorterDuff.Mode.SRC_IN;
    case 9: 
      return PorterDuff.Mode.SRC_ATOP;
    case 14: 
      return PorterDuff.Mode.MULTIPLY;
    case 15: 
      return PorterDuff.Mode.SCREEN;
    }
    if (Build.VERSION.SDK_INT >= 11) {
      paramMode = PorterDuff.Mode.valueOf("ADD");
    }
    return paramMode;
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\widget\DrawableUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */