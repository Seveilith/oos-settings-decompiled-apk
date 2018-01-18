package com.android.settings.drawable;

import android.graphics.drawable.Drawable;

public class InsetBoundsDrawable
  extends DrawableWrapper
{
  private final int mInsetBoundsSides;
  
  public InsetBoundsDrawable(Drawable paramDrawable, int paramInt)
  {
    super(paramDrawable);
    this.mInsetBoundsSides = paramInt;
  }
  
  public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.setBounds(this.mInsetBoundsSides + paramInt1, paramInt2, paramInt3 - this.mInsetBoundsSides, paramInt4);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\drawable\InsetBoundsDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */