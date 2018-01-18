package com.airbnb.lottie.model.layer;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.animation.content.ContentGroup;
import com.airbnb.lottie.model.content.ShapeGroup;
import java.util.Collections;

public class ShapeLayer
  extends BaseLayer
{
  private final ContentGroup contentGroup;
  
  ShapeLayer(LottieDrawable paramLottieDrawable, Layer paramLayer)
  {
    super(paramLottieDrawable, paramLayer);
    this.contentGroup = new ContentGroup(paramLottieDrawable, this, new ShapeGroup(paramLayer.getName(), paramLayer.getShapes()));
    this.contentGroup.setContents(Collections.emptyList(), Collections.emptyList());
  }
  
  public void addColorFilter(@Nullable String paramString1, @Nullable String paramString2, @Nullable ColorFilter paramColorFilter)
  {
    this.contentGroup.addColorFilter(paramString1, paramString2, paramColorFilter);
  }
  
  void drawLayer(@NonNull Canvas paramCanvas, Matrix paramMatrix, int paramInt)
  {
    this.contentGroup.draw(paramCanvas, paramMatrix, paramInt);
  }
  
  public void getBounds(RectF paramRectF, Matrix paramMatrix)
  {
    super.getBounds(paramRectF, paramMatrix);
    this.contentGroup.getBounds(paramRectF, this.boundsMatrix);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\layer\ShapeLayer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */