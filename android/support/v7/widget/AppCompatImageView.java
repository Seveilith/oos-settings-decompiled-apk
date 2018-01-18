package android.support.v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.view.TintableBackgroundView;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AppCompatImageView
  extends ImageView
  implements TintableBackgroundView
{
  private AppCompatBackgroundHelper mBackgroundTintHelper = new AppCompatBackgroundHelper(this);
  private AppCompatImageHelper mImageHelper;
  
  public AppCompatImageView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public AppCompatImageView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public AppCompatImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(TintContextWrapper.wrap(paramContext), paramAttributeSet, paramInt);
    this.mBackgroundTintHelper.loadFromAttributes(paramAttributeSet, paramInt);
    this.mImageHelper = new AppCompatImageHelper(this);
    this.mImageHelper.loadFromAttributes(paramAttributeSet, paramInt);
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    if (this.mBackgroundTintHelper != null) {
      this.mBackgroundTintHelper.applySupportBackgroundTint();
    }
  }
  
  @Nullable
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public ColorStateList getSupportBackgroundTintList()
  {
    ColorStateList localColorStateList = null;
    if (this.mBackgroundTintHelper != null) {
      localColorStateList = this.mBackgroundTintHelper.getSupportBackgroundTintList();
    }
    return localColorStateList;
  }
  
  @Nullable
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public PorterDuff.Mode getSupportBackgroundTintMode()
  {
    PorterDuff.Mode localMode = null;
    if (this.mBackgroundTintHelper != null) {
      localMode = this.mBackgroundTintHelper.getSupportBackgroundTintMode();
    }
    return localMode;
  }
  
  public boolean hasOverlappingRendering()
  {
    if (this.mImageHelper.hasOverlappingRendering()) {
      return super.hasOverlappingRendering();
    }
    return false;
  }
  
  public void setBackgroundDrawable(Drawable paramDrawable)
  {
    super.setBackgroundDrawable(paramDrawable);
    if (this.mBackgroundTintHelper != null) {
      this.mBackgroundTintHelper.onSetBackgroundDrawable(paramDrawable);
    }
  }
  
  public void setBackgroundResource(@DrawableRes int paramInt)
  {
    super.setBackgroundResource(paramInt);
    if (this.mBackgroundTintHelper != null) {
      this.mBackgroundTintHelper.onSetBackgroundResource(paramInt);
    }
  }
  
  public void setImageResource(@DrawableRes int paramInt)
  {
    this.mImageHelper.setImageResource(paramInt);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public void setSupportBackgroundTintList(@Nullable ColorStateList paramColorStateList)
  {
    if (this.mBackgroundTintHelper != null) {
      this.mBackgroundTintHelper.setSupportBackgroundTintList(paramColorStateList);
    }
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public void setSupportBackgroundTintMode(@Nullable PorterDuff.Mode paramMode)
  {
    if (this.mBackgroundTintHelper != null) {
      this.mBackgroundTintHelper.setSupportBackgroundTintMode(paramMode);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\widget\AppCompatImageView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */