package com.oneplus.lib.widget.cardview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import com.oneplus.commonctrl.R.style;
import com.oneplus.commonctrl.R.styleable;

public class OPCardView
  extends CardView
{
  private int mBackgroundColor;
  private int mBackgroundColorMask;
  Paint mCardBackgroundMaskPaint;
  private boolean mIsCardSelected;
  
  public OPCardView(Context paramContext)
  {
    super(paramContext);
    initialize(paramContext, null, 0);
  }
  
  public OPCardView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initialize(paramContext, paramAttributeSet, 0);
  }
  
  public OPCardView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initialize(paramContext, paramAttributeSet, paramInt);
  }
  
  private void initialize(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.CardView, paramInt, R.style.Oneplus_CardView_Light);
    this.mBackgroundColor = paramContext.getColor(R.styleable.CardView_cardBackgroundColor, 0);
    this.mBackgroundColorMask = paramContext.getColor(R.styleable.CardView_cardBackgroundColorMask, 0);
    paramContext.recycle();
    setCardBackgroundColor(this.mBackgroundColor);
    this.mCardBackgroundMaskPaint = new Paint();
    this.mCardBackgroundMaskPaint.setColor(this.mBackgroundColorMask);
  }
  
  public void dispatchDraw(Canvas paramCanvas)
  {
    super.dispatchDraw(paramCanvas);
    if ((this.mCardBackgroundMaskPaint != null) && (this.mIsCardSelected)) {
      paramCanvas.drawRect(0.0F, 0.0F, getWidth(), getHeight(), this.mCardBackgroundMaskPaint);
    }
  }
  
  public void setCardSelected(boolean paramBoolean)
  {
    this.mIsCardSelected = paramBoolean;
    invalidate();
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\cardview\OPCardView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */