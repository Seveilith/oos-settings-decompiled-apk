package com.oneplus.lib.design.widget;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toolbar;
import java.lang.reflect.Field;

public class Utils
{
  private static final Matrix IDENTITY = new Matrix();
  private static final ThreadLocal<Matrix> sMatrix = new ThreadLocal();
  private static final ThreadLocal<RectF> sRectF = new ThreadLocal();
  
  static float constrain(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (paramFloat1 < paramFloat2) {
      return paramFloat2;
    }
    if (paramFloat1 > paramFloat3) {
      return paramFloat3;
    }
    return paramFloat1;
  }
  
  static int constrain(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 < paramInt2) {
      return paramInt2;
    }
    if (paramInt1 > paramInt3) {
      return paramInt3;
    }
    return paramInt1;
  }
  
  public static void getDescendantRect(ViewGroup paramViewGroup, View paramView, Rect paramRect)
  {
    paramRect.set(0, 0, paramView.getWidth(), paramView.getHeight());
    offsetDescendantRect(paramViewGroup, paramView, paramRect);
  }
  
  public static int getTitleMarginBottom(Toolbar paramToolbar)
  {
    Object localObject = null;
    if (Build.VERSION.SDK_INT >= 24) {
      return paramToolbar.getTitleMarginBottom();
    }
    try
    {
      Field localField = Toolbar.class.getDeclaredField("mTitleMarginBottom");
      localObject = localField;
      localField.setAccessible(true);
      localObject = localField;
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      for (;;) {}
    }
    if (localObject != null) {
      try
      {
        int i = ((Integer)((Field)localObject).get(paramToolbar)).intValue();
        return i;
      }
      catch (Exception paramToolbar) {}
    }
    return 0;
  }
  
  public static int getTitleMarginEnd(Toolbar paramToolbar)
  {
    Object localObject = null;
    if (Build.VERSION.SDK_INT >= 24) {
      return paramToolbar.getTitleMarginEnd();
    }
    try
    {
      Field localField = Toolbar.class.getDeclaredField("mTitleMarginEnd");
      localObject = localField;
      localField.setAccessible(true);
      localObject = localField;
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      for (;;) {}
    }
    if (localObject != null) {
      try
      {
        int i = ((Integer)((Field)localObject).get(paramToolbar)).intValue();
        return i;
      }
      catch (Exception paramToolbar) {}
    }
    return 0;
  }
  
  public static int getTitleMarginStart(Toolbar paramToolbar)
  {
    Object localObject = null;
    if (Build.VERSION.SDK_INT >= 24) {
      return paramToolbar.getTitleMarginStart();
    }
    try
    {
      Field localField = Toolbar.class.getDeclaredField("mTitleMarginStart");
      localObject = localField;
      localField.setAccessible(true);
      localObject = localField;
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      for (;;) {}
    }
    if (localObject != null) {
      try
      {
        int i = ((Integer)((Field)localObject).get(paramToolbar)).intValue();
        return i;
      }
      catch (Exception paramToolbar) {}
    }
    return 0;
  }
  
  public static int getTitleMarginTop(Toolbar paramToolbar)
  {
    Object localObject = null;
    if (Build.VERSION.SDK_INT >= 24) {
      return paramToolbar.getTitleMarginTop();
    }
    try
    {
      Field localField = Toolbar.class.getDeclaredField("mTitleMarginTop");
      localObject = localField;
      localField.setAccessible(true);
      localObject = localField;
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      for (;;) {}
    }
    if (localObject != null) {
      try
      {
        int i = ((Integer)((Field)localObject).get(paramToolbar)).intValue();
        return i;
      }
      catch (Exception paramToolbar) {}
    }
    return 0;
  }
  
  static float lerp(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return (paramFloat2 - paramFloat1) * paramFloat3 + paramFloat1;
  }
  
  static int lerp(int paramInt1, int paramInt2, float paramFloat)
  {
    return Math.round((paramInt2 - paramInt1) * paramFloat) + paramInt1;
  }
  
  static boolean objectEquals(Object paramObject1, Object paramObject2)
  {
    if (paramObject1 != paramObject2)
    {
      if (paramObject1 != null) {
        return paramObject1.equals(paramObject2);
      }
    }
    else {
      return true;
    }
    return false;
  }
  
  static void offsetDescendantMatrix(ViewParent paramViewParent, View paramView, Matrix paramMatrix)
  {
    Object localObject = paramView.getParent();
    if (((localObject instanceof View)) && (localObject != paramViewParent))
    {
      localObject = (View)localObject;
      offsetDescendantMatrix(paramViewParent, (View)localObject, paramMatrix);
      paramMatrix.preTranslate(-((View)localObject).getScrollX(), -((View)localObject).getScrollY());
    }
    paramMatrix.preTranslate(paramView.getLeft(), paramView.getTop());
    if (!paramView.getMatrix().isIdentity()) {
      paramMatrix.preConcat(paramView.getMatrix());
    }
  }
  
  static void offsetDescendantRect(ViewGroup paramViewGroup, View paramView, Rect paramRect)
  {
    Matrix localMatrix = (Matrix)sMatrix.get();
    if (localMatrix == null)
    {
      localMatrix = new Matrix();
      sMatrix.set(localMatrix);
    }
    for (;;)
    {
      offsetDescendantMatrix(paramViewGroup, paramView, localMatrix);
      paramView = (RectF)sRectF.get();
      paramViewGroup = paramView;
      if (paramView == null) {
        paramViewGroup = new RectF();
      }
      paramViewGroup.set(paramRect);
      localMatrix.mapRect(paramViewGroup);
      paramRect.set((int)(paramViewGroup.left + 0.5F), (int)(paramViewGroup.top + 0.5F), (int)(paramViewGroup.right + 0.5F), (int)(paramViewGroup.bottom + 0.5F));
      return;
      localMatrix.set(IDENTITY);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\design\widget\Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */