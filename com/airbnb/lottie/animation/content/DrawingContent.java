package com.airbnb.lottie.animation.content;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.support.annotation.Nullable;

public abstract interface DrawingContent
  extends Content
{
  public abstract void addColorFilter(@Nullable String paramString1, @Nullable String paramString2, @Nullable ColorFilter paramColorFilter);
  
  public abstract void draw(Canvas paramCanvas, Matrix paramMatrix, int paramInt);
  
  public abstract void getBounds(RectF paramRectF, Matrix paramMatrix);
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\content\DrawingContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */