package com.airbnb.lottie.model.layer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.airbnb.lottie.LottieDrawable;

public class ImageLayer
  extends BaseLayer
{
  private final float density;
  private final Rect dst = new Rect();
  private final Paint paint = new Paint(3);
  private final Rect src = new Rect();
  
  ImageLayer(LottieDrawable paramLottieDrawable, Layer paramLayer, float paramFloat)
  {
    super(paramLottieDrawable, paramLayer);
    this.density = paramFloat;
  }
  
  @Nullable
  private Bitmap getBitmap()
  {
    String str = this.layerModel.getRefId();
    return this.lottieDrawable.getImageAsset(str);
  }
  
  public void addColorFilter(@Nullable String paramString1, @Nullable String paramString2, @Nullable ColorFilter paramColorFilter)
  {
    this.paint.setColorFilter(paramColorFilter);
  }
  
  public void drawLayer(@NonNull Canvas paramCanvas, Matrix paramMatrix, int paramInt)
  {
    Bitmap localBitmap = getBitmap();
    if (localBitmap != null)
    {
      this.paint.setAlpha(paramInt);
      paramCanvas.save();
      paramCanvas.concat(paramMatrix);
      this.src.set(0, 0, localBitmap.getWidth(), localBitmap.getHeight());
      this.dst.set(0, 0, (int)(localBitmap.getWidth() * this.density), (int)(localBitmap.getHeight() * this.density));
      paramCanvas.drawBitmap(localBitmap, this.src, this.dst, this.paint);
      paramCanvas.restore();
      return;
    }
  }
  
  public void getBounds(RectF paramRectF, Matrix paramMatrix)
  {
    super.getBounds(paramRectF, paramMatrix);
    paramMatrix = getBitmap();
    if (paramMatrix == null) {
      return;
    }
    paramRectF.set(paramRectF.left, paramRectF.top, Math.min(paramRectF.right, paramMatrix.getWidth()), Math.min(paramRectF.bottom, paramMatrix.getHeight()));
    this.boundsMatrix.mapRect(paramRectF);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\layer\ImageLayer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */