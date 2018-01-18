package com.airbnb.lottie.model.layer;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import com.airbnb.lottie.LottieDrawable;

public class NullLayer
  extends BaseLayer
{
  NullLayer(LottieDrawable paramLottieDrawable, Layer paramLayer)
  {
    super(paramLottieDrawable, paramLayer);
  }
  
  void drawLayer(Canvas paramCanvas, Matrix paramMatrix, int paramInt) {}
  
  public void getBounds(RectF paramRectF, Matrix paramMatrix)
  {
    super.getBounds(paramRectF, paramMatrix);
    paramRectF.set(0.0F, 0.0F, 0.0F, 0.0F);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\layer\NullLayer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */