package com.oneplus.lib.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class OPCheckBoxNoAnim
  extends ImageView
{
  private boolean mChecked = false;
  private int mCheckedResId = 0;
  private int mIntrinsicWidth = 0;
  private int mUnCheckedResId = 0;
  
  public OPCheckBoxNoAnim(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public OPCheckBoxNoAnim(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public OPCheckBoxNoAnim(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init();
  }
  
  private void init() {}
  
  public void setChecked(boolean paramBoolean)
  {
    if (paramBoolean) {
      super.setImageResource(this.mCheckedResId);
    }
    for (;;)
    {
      this.mChecked = paramBoolean;
      return;
      super.setImageResource(this.mUnCheckedResId);
    }
  }
  
  public void setCheckedImageResource(int paramInt)
  {
    this.mCheckedResId = paramInt;
    if (this.mIntrinsicWidth == 0) {
      this.mIntrinsicWidth = getContext().getResources().getDrawable(this.mCheckedResId, null).getIntrinsicWidth();
    }
  }
  
  public void setImageResource(int paramInt) {}
  
  public void setUnCheckedImageResource(int paramInt)
  {
    this.mUnCheckedResId = paramInt;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\OPCheckBoxNoAnim.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */