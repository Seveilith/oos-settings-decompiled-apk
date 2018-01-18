package android.support.v4.graphics.drawable;

import android.graphics.drawable.Drawable;
import android.support.annotation.RestrictTo;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public abstract interface DrawableWrapper
{
  public abstract Drawable getWrappedDrawable();
  
  public abstract void setWrappedDrawable(Drawable paramDrawable);
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\graphics\drawable\DrawableWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */