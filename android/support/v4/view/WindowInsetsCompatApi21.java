package android.support.v4.view;

import android.graphics.Rect;
import android.view.WindowInsets;

class WindowInsetsCompatApi21
{
  public static Object consumeStableInsets(Object paramObject)
  {
    return ((WindowInsets)paramObject).consumeStableInsets();
  }
  
  public static int getStableInsetBottom(Object paramObject)
  {
    return ((WindowInsets)paramObject).getStableInsetBottom();
  }
  
  public static int getStableInsetLeft(Object paramObject)
  {
    return ((WindowInsets)paramObject).getStableInsetLeft();
  }
  
  public static int getStableInsetRight(Object paramObject)
  {
    return ((WindowInsets)paramObject).getStableInsetRight();
  }
  
  public static int getStableInsetTop(Object paramObject)
  {
    return ((WindowInsets)paramObject).getStableInsetTop();
  }
  
  public static boolean hasStableInsets(Object paramObject)
  {
    return ((WindowInsets)paramObject).hasStableInsets();
  }
  
  public static boolean isConsumed(Object paramObject)
  {
    return ((WindowInsets)paramObject).isConsumed();
  }
  
  public static Object replaceSystemWindowInsets(Object paramObject, Rect paramRect)
  {
    return ((WindowInsets)paramObject).replaceSystemWindowInsets(paramRect);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\view\WindowInsetsCompatApi21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */