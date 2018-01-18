package com.android.settings.display;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;

public class MessageBubbleBackground
  extends LinearLayout
{
  private final int mSnapWidthPixels;
  
  public MessageBubbleBackground(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mSnapWidthPixels = paramContext.getResources().getDimensionPixelSize(2131755610);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    int i = getPaddingLeft() + getPaddingRight();
    int j = getMeasuredWidth();
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(paramInt1) - i, (int)(Math.ceil((j - i) / this.mSnapWidthPixels) * this.mSnapWidthPixels)) + i, 1073741824), paramInt2);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\display\MessageBubbleBackground.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */