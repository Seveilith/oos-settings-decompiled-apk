package com.android.setupwizardlib.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.widget.Button;

public class NavigationBarButton
  extends Button
{
  public NavigationBarButton(Context paramContext)
  {
    super(paramContext);
    init();
  }
  
  public NavigationBarButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init();
  }
  
  private Drawable[] getAllCompoundDrawables()
  {
    Drawable[] arrayOfDrawable1 = new Drawable[6];
    Drawable[] arrayOfDrawable2 = getCompoundDrawables();
    arrayOfDrawable1[0] = arrayOfDrawable2[0];
    arrayOfDrawable1[1] = arrayOfDrawable2[1];
    arrayOfDrawable1[2] = arrayOfDrawable2[2];
    arrayOfDrawable1[3] = arrayOfDrawable2[3];
    if (Build.VERSION.SDK_INT >= 17)
    {
      arrayOfDrawable2 = getCompoundDrawablesRelative();
      arrayOfDrawable1[4] = arrayOfDrawable2[0];
      arrayOfDrawable1[5] = arrayOfDrawable2[2];
    }
    return arrayOfDrawable1;
  }
  
  private void init()
  {
    if (Build.VERSION.SDK_INT >= 17)
    {
      Drawable[] arrayOfDrawable = getCompoundDrawablesRelative();
      int i = 0;
      while (i < arrayOfDrawable.length)
      {
        if (arrayOfDrawable[i] != null) {
          arrayOfDrawable[i] = TintedDrawable.wrap(arrayOfDrawable[i]);
        }
        i += 1;
      }
      setCompoundDrawablesRelativeWithIntrinsicBounds(arrayOfDrawable[0], arrayOfDrawable[1], arrayOfDrawable[2], arrayOfDrawable[3]);
    }
  }
  
  private void tintDrawables()
  {
    ColorStateList localColorStateList = getTextColors();
    if (localColorStateList != null)
    {
      Drawable[] arrayOfDrawable = getAllCompoundDrawables();
      int i = 0;
      int j = arrayOfDrawable.length;
      while (i < j)
      {
        Drawable localDrawable = arrayOfDrawable[i];
        if ((localDrawable instanceof TintedDrawable)) {
          ((TintedDrawable)localDrawable).setTintListCompat(localColorStateList);
        }
        i += 1;
      }
      invalidate();
    }
  }
  
  public void setCompoundDrawables(Drawable paramDrawable1, Drawable paramDrawable2, Drawable paramDrawable3, Drawable paramDrawable4)
  {
    Object localObject = paramDrawable1;
    if (paramDrawable1 != null) {
      localObject = TintedDrawable.wrap(paramDrawable1);
    }
    paramDrawable1 = paramDrawable2;
    if (paramDrawable2 != null) {
      paramDrawable1 = TintedDrawable.wrap(paramDrawable2);
    }
    paramDrawable2 = paramDrawable3;
    if (paramDrawable3 != null) {
      paramDrawable2 = TintedDrawable.wrap(paramDrawable3);
    }
    paramDrawable3 = paramDrawable4;
    if (paramDrawable4 != null) {
      paramDrawable3 = TintedDrawable.wrap(paramDrawable4);
    }
    super.setCompoundDrawables((Drawable)localObject, paramDrawable1, paramDrawable2, paramDrawable3);
    tintDrawables();
  }
  
  public void setCompoundDrawablesRelative(Drawable paramDrawable1, Drawable paramDrawable2, Drawable paramDrawable3, Drawable paramDrawable4)
  {
    Object localObject = paramDrawable1;
    if (paramDrawable1 != null) {
      localObject = TintedDrawable.wrap(paramDrawable1);
    }
    paramDrawable1 = paramDrawable2;
    if (paramDrawable2 != null) {
      paramDrawable1 = TintedDrawable.wrap(paramDrawable2);
    }
    paramDrawable2 = paramDrawable3;
    if (paramDrawable3 != null) {
      paramDrawable2 = TintedDrawable.wrap(paramDrawable3);
    }
    paramDrawable3 = paramDrawable4;
    if (paramDrawable4 != null) {
      paramDrawable3 = TintedDrawable.wrap(paramDrawable4);
    }
    super.setCompoundDrawablesRelative((Drawable)localObject, paramDrawable1, paramDrawable2, paramDrawable3);
    tintDrawables();
  }
  
  public void setTextColor(ColorStateList paramColorStateList)
  {
    super.setTextColor(paramColorStateList);
    tintDrawables();
  }
  
  private static class TintedDrawable
    extends LayerDrawable
  {
    private ColorStateList mTintList = null;
    
    public TintedDrawable(Drawable paramDrawable)
    {
      super();
    }
    
    private boolean updateState()
    {
      if (this.mTintList != null)
      {
        setColorFilter(this.mTintList.getColorForState(getState(), 0), PorterDuff.Mode.SRC_IN);
        return true;
      }
      return false;
    }
    
    public static TintedDrawable wrap(Drawable paramDrawable)
    {
      if ((paramDrawable instanceof TintedDrawable)) {
        return (TintedDrawable)paramDrawable;
      }
      return new TintedDrawable(paramDrawable.mutate());
    }
    
    public boolean isStateful()
    {
      return true;
    }
    
    public boolean setState(int[] paramArrayOfInt)
    {
      boolean bool1 = super.setState(paramArrayOfInt);
      boolean bool2 = updateState();
      if (!bool1) {
        return bool2;
      }
      return true;
    }
    
    public void setTintListCompat(ColorStateList paramColorStateList)
    {
      this.mTintList = paramColorStateList;
      if (updateState()) {
        invalidateSelf();
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\setupwizardlib\view\NavigationBarButton.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */