package com.android.settingslib;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

public class RestrictedLockImageSpan
  extends ImageSpan
{
  private Context mContext;
  private final float mExtraPadding;
  private final Drawable mRestrictedPadlock;
  
  public RestrictedLockImageSpan(Context paramContext)
  {
    super((Drawable)null);
    this.mContext = paramContext;
    this.mExtraPadding = this.mContext.getResources().getDimensionPixelSize(R.dimen.restricted_icon_padding);
    this.mRestrictedPadlock = RestrictedLockUtils.getRestrictedPadlock(this.mContext);
  }
  
  public void draw(Canvas paramCanvas, CharSequence paramCharSequence, int paramInt1, int paramInt2, float paramFloat, int paramInt3, int paramInt4, int paramInt5, Paint paramPaint)
  {
    paramCharSequence = getDrawable();
    paramCanvas.save();
    paramCanvas.translate(paramFloat + this.mExtraPadding, (paramInt5 - paramCharSequence.getBounds().bottom) / 2.0F);
    paramCharSequence.draw(paramCanvas);
    paramCanvas.restore();
  }
  
  public Drawable getDrawable()
  {
    return this.mRestrictedPadlock;
  }
  
  public int getSize(Paint paramPaint, CharSequence paramCharSequence, int paramInt1, int paramInt2, Paint.FontMetricsInt paramFontMetricsInt)
  {
    return (int)(super.getSize(paramPaint, paramCharSequence, paramInt1, paramInt2, paramFontMetricsInt) + this.mExtraPadding * 2.0F);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\RestrictedLockImageSpan.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */