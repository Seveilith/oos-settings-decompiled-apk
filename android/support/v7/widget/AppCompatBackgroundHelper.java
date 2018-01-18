package android.support.v7.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R.styleable;
import android.util.AttributeSet;
import android.view.View;

class AppCompatBackgroundHelper
{
  private int mBackgroundResId = -1;
  private BackgroundTintInfo mBackgroundTint;
  private final AppCompatDrawableManager mDrawableManager;
  private BackgroundTintInfo mInternalBackgroundTint;
  private BackgroundTintInfo mTmpInfo;
  private final View mView;
  
  AppCompatBackgroundHelper(View paramView)
  {
    this.mView = paramView;
    this.mDrawableManager = AppCompatDrawableManager.get();
  }
  
  private boolean applyFrameworkTintUsingColorFilter(@NonNull Drawable paramDrawable)
  {
    if (this.mTmpInfo == null) {
      this.mTmpInfo = new BackgroundTintInfo();
    }
    BackgroundTintInfo localBackgroundTintInfo = this.mTmpInfo;
    localBackgroundTintInfo.clear();
    Object localObject = ViewCompat.getBackgroundTintList(this.mView);
    if (localObject != null)
    {
      localBackgroundTintInfo.mHasTintList = true;
      localBackgroundTintInfo.mTintList = ((ColorStateList)localObject);
    }
    localObject = ViewCompat.getBackgroundTintMode(this.mView);
    if (localObject != null)
    {
      localBackgroundTintInfo.mHasTintMode = true;
      localBackgroundTintInfo.mTintMode = ((PorterDuff.Mode)localObject);
    }
    if ((localBackgroundTintInfo.mHasTintList) || (localBackgroundTintInfo.mHasTintMode))
    {
      AppCompatDrawableManager.tintDrawable(paramDrawable, localBackgroundTintInfo, this.mView.getDrawableState());
      return true;
    }
    return false;
  }
  
  private boolean updateBackgroundTint()
  {
    if ((this.mBackgroundTint != null) && (this.mBackgroundTint.mHasTintList))
    {
      if (this.mBackgroundResId >= 0)
      {
        ColorStateList localColorStateList = this.mDrawableManager.getTintList(this.mView.getContext(), this.mBackgroundResId, this.mBackgroundTint.mOriginalTintList);
        if (localColorStateList != null)
        {
          this.mBackgroundTint.mTintList = localColorStateList;
          return true;
        }
      }
      if (this.mBackgroundTint.mTintList != this.mBackgroundTint.mOriginalTintList)
      {
        this.mBackgroundTint.mTintList = this.mBackgroundTint.mOriginalTintList;
        return true;
      }
    }
    return false;
  }
  
  void applySupportBackgroundTint()
  {
    Drawable localDrawable = this.mView.getBackground();
    if (localDrawable != null)
    {
      if ((Build.VERSION.SDK_INT == 21) && (applyFrameworkTintUsingColorFilter(localDrawable))) {
        return;
      }
      if (this.mBackgroundTint == null) {
        break label52;
      }
      AppCompatDrawableManager.tintDrawable(localDrawable, this.mBackgroundTint, this.mView.getDrawableState());
    }
    label52:
    while (this.mInternalBackgroundTint == null) {
      return;
    }
    AppCompatDrawableManager.tintDrawable(localDrawable, this.mInternalBackgroundTint, this.mView.getDrawableState());
  }
  
  ColorStateList getSupportBackgroundTintList()
  {
    ColorStateList localColorStateList = null;
    if (this.mBackgroundTint != null) {
      localColorStateList = this.mBackgroundTint.mTintList;
    }
    return localColorStateList;
  }
  
  PorterDuff.Mode getSupportBackgroundTintMode()
  {
    PorterDuff.Mode localMode = null;
    if (this.mBackgroundTint != null) {
      localMode = this.mBackgroundTint.mTintMode;
    }
    return localMode;
  }
  
  void loadFromAttributes(AttributeSet paramAttributeSet, int paramInt)
  {
    paramAttributeSet = TintTypedArray.obtainStyledAttributes(this.mView.getContext(), paramAttributeSet, R.styleable.ViewBackgroundHelper, paramInt, 0);
    try
    {
      if (paramAttributeSet.hasValue(R.styleable.ViewBackgroundHelper_android_background))
      {
        this.mBackgroundResId = paramAttributeSet.getResourceId(R.styleable.ViewBackgroundHelper_android_background, -1);
        ColorStateList localColorStateList = this.mDrawableManager.getTintList(this.mView.getContext(), this.mBackgroundResId);
        if (localColorStateList != null) {
          setInternalBackgroundTint(localColorStateList);
        }
      }
      if (paramAttributeSet.hasValue(R.styleable.ViewBackgroundHelper_backgroundTint)) {
        ViewCompat.setBackgroundTintList(this.mView, paramAttributeSet.getColorStateList(R.styleable.ViewBackgroundHelper_backgroundTint));
      }
      if (paramAttributeSet.hasValue(R.styleable.ViewBackgroundHelper_backgroundTintMode)) {
        ViewCompat.setBackgroundTintMode(this.mView, DrawableUtils.parseTintMode(paramAttributeSet.getInt(R.styleable.ViewBackgroundHelper_backgroundTintMode, -1), null));
      }
      return;
    }
    finally
    {
      paramAttributeSet.recycle();
    }
  }
  
  void onSetBackgroundDrawable(Drawable paramDrawable)
  {
    this.mBackgroundResId = -1;
    setInternalBackgroundTint(null);
    if (updateBackgroundTint()) {
      applySupportBackgroundTint();
    }
  }
  
  void onSetBackgroundResource(int paramInt)
  {
    ColorStateList localColorStateList = null;
    this.mBackgroundResId = paramInt;
    if (this.mDrawableManager != null) {
      localColorStateList = this.mDrawableManager.getTintList(this.mView.getContext(), paramInt);
    }
    setInternalBackgroundTint(localColorStateList);
    if (updateBackgroundTint()) {
      applySupportBackgroundTint();
    }
  }
  
  void setInternalBackgroundTint(ColorStateList paramColorStateList)
  {
    if (paramColorStateList != null)
    {
      if (this.mInternalBackgroundTint == null) {
        this.mInternalBackgroundTint = new BackgroundTintInfo();
      }
      this.mInternalBackgroundTint.mTintList = paramColorStateList;
      this.mInternalBackgroundTint.mHasTintList = true;
    }
    for (;;)
    {
      applySupportBackgroundTint();
      return;
      this.mInternalBackgroundTint = null;
    }
  }
  
  void setSupportBackgroundTintList(ColorStateList paramColorStateList)
  {
    if (this.mBackgroundTint == null) {
      this.mBackgroundTint = new BackgroundTintInfo();
    }
    this.mBackgroundTint.mOriginalTintList = paramColorStateList;
    this.mBackgroundTint.mTintList = null;
    this.mBackgroundTint.mHasTintList = true;
    if (updateBackgroundTint()) {
      applySupportBackgroundTint();
    }
  }
  
  void setSupportBackgroundTintMode(PorterDuff.Mode paramMode)
  {
    if (this.mBackgroundTint == null) {
      this.mBackgroundTint = new BackgroundTintInfo();
    }
    this.mBackgroundTint.mTintMode = paramMode;
    this.mBackgroundTint.mHasTintMode = true;
    applySupportBackgroundTint();
  }
  
  private static class BackgroundTintInfo
    extends TintInfo
  {
    public ColorStateList mOriginalTintList;
    
    void clear()
    {
      super.clear();
      this.mOriginalTintList = null;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\widget\AppCompatBackgroundHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */