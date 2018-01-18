package com.android.settingslib.drawable;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import com.android.settingslib.R.dimen;

public class CircleFramedDrawable
  extends Drawable
{
  private final Bitmap mBitmap;
  private RectF mDstRect;
  private final Paint mPaint;
  private float mScale;
  private final int mSize;
  private Rect mSrcRect;
  
  public CircleFramedDrawable(Bitmap paramBitmap, int paramInt)
  {
    this.mSize = paramInt;
    this.mBitmap = Bitmap.createBitmap(this.mSize, this.mSize, Bitmap.Config.ARGB_8888);
    Canvas localCanvas = new Canvas(this.mBitmap);
    paramInt = paramBitmap.getWidth();
    int i = paramBitmap.getHeight();
    int j = Math.min(paramInt, i);
    Rect localRect = new Rect((paramInt - j) / 2, (i - j) / 2, j, j);
    RectF localRectF = new RectF(0.0F, 0.0F, this.mSize, this.mSize);
    Path localPath = new Path();
    localPath.addArc(localRectF, 0.0F, 360.0F);
    localCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
    this.mPaint = new Paint();
    this.mPaint.setAntiAlias(true);
    this.mPaint.setColor(-16777216);
    this.mPaint.setStyle(Paint.Style.FILL);
    this.mPaint.setFlags(1);
    localCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, 3));
    localCanvas.drawPath(localPath, this.mPaint);
    this.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    localCanvas.drawBitmap(paramBitmap, localRect, localRectF, this.mPaint);
    this.mPaint.setXfermode(null);
    this.mScale = 1.0F;
    this.mSrcRect = new Rect(0, 0, this.mSize, this.mSize);
    this.mDstRect = new RectF(0.0F, 0.0F, this.mSize, this.mSize);
  }
  
  public static CircleFramedDrawable getInstance(Context paramContext, Bitmap paramBitmap)
  {
    return new CircleFramedDrawable(paramBitmap, (int)paramContext.getResources().getDimension(R.dimen.circle_avatar_size));
  }
  
  public void draw(Canvas paramCanvas)
  {
    float f1 = this.mScale;
    float f2 = this.mSize;
    f1 = (this.mSize - f1 * f2) / 2.0F;
    this.mDstRect.set(f1, f1, this.mSize - f1, this.mSize - f1);
    paramCanvas.drawBitmap(this.mBitmap, this.mSrcRect, this.mDstRect, null);
  }
  
  public int getIntrinsicHeight()
  {
    return this.mSize;
  }
  
  public int getIntrinsicWidth()
  {
    return this.mSize;
  }
  
  public int getOpacity()
  {
    return -3;
  }
  
  public float getScale()
  {
    return this.mScale;
  }
  
  public void setAlpha(int paramInt) {}
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
  
  public void setScale(float paramFloat)
  {
    this.mScale = paramFloat;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\drawable\CircleFramedDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */