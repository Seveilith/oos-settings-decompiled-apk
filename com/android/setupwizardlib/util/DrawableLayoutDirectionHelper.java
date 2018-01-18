package com.android.setupwizardlib.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build.VERSION;
import android.view.View;

public class DrawableLayoutDirectionHelper
{
  public static InsetDrawable createRelativeInsetDrawable(Drawable paramDrawable, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    boolean bool = true;
    if (paramInt5 == 1) {}
    for (;;)
    {
      return createRelativeInsetDrawable(paramDrawable, paramInt1, paramInt2, paramInt3, paramInt4, bool);
      bool = false;
    }
  }
  
  public static InsetDrawable createRelativeInsetDrawable(Drawable paramDrawable, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Context paramContext)
  {
    boolean bool = false;
    if (Build.VERSION.SDK_INT >= 17) {
      if (paramContext.getResources().getConfiguration().getLayoutDirection() != 1) {
        break label41;
      }
    }
    label41:
    for (bool = true;; bool = false) {
      return createRelativeInsetDrawable(paramDrawable, paramInt1, paramInt2, paramInt3, paramInt4, bool);
    }
  }
  
  public static InsetDrawable createRelativeInsetDrawable(Drawable paramDrawable, int paramInt1, int paramInt2, int paramInt3, int paramInt4, View paramView)
  {
    boolean bool = true;
    if (Build.VERSION.SDK_INT >= 17) {
      if (paramView.getLayoutDirection() != 1) {}
    }
    for (;;)
    {
      return createRelativeInsetDrawable(paramDrawable, paramInt1, paramInt2, paramInt3, paramInt4, bool);
      bool = false;
      continue;
      bool = false;
    }
  }
  
  private static InsetDrawable createRelativeInsetDrawable(Drawable paramDrawable, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    if (paramBoolean) {
      return new InsetDrawable(paramDrawable, paramInt3, paramInt2, paramInt1, paramInt4);
    }
    return new InsetDrawable(paramDrawable, paramInt1, paramInt2, paramInt3, paramInt4);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\util\DrawableLayoutDirectionHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */