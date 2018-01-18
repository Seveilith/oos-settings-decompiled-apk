package com.oneplus.lib.widget.button;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.view.ViewPropertyAnimator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import com.oneplus.commonctrl.R.color;

class OPFloatingActionButtonImpl
{
  static final int[] EMPTY_STATE_SET = new int[0];
  static final int[] FOCUSED_ENABLED_STATE_SET;
  static final int[] PRESSED_ENABLED_STATE_SET = { 16842919, 16842910 };
  static final int SHOW_HIDE_ANIM_DURATION = 200;
  private Drawable mBorderDrawable;
  private Interpolator mInterpolator;
  private boolean mIsHiding;
  private RippleDrawable mRippleDrawable;
  final OPShadowViewDelegate mShadowViewDelegate;
  private Drawable mShapeDrawable;
  final OPFloatingActionButton mView;
  
  static
  {
    FOCUSED_ENABLED_STATE_SET = new int[] { 16842908, 16842910 };
  }
  
  OPFloatingActionButtonImpl(OPFloatingActionButton paramOPFloatingActionButton, OPShadowViewDelegate paramOPShadowViewDelegate)
  {
    this.mView = paramOPFloatingActionButton;
    this.mShadowViewDelegate = paramOPShadowViewDelegate;
    if (!paramOPFloatingActionButton.isInEditMode()) {
      this.mInterpolator = AnimationUtils.loadInterpolator(this.mView.getContext(), 17563661);
    }
  }
  
  private Animator setupAnimator(Animator paramAnimator)
  {
    paramAnimator.setInterpolator(this.mInterpolator);
    return paramAnimator;
  }
  
  Drawable createBorderDrawable(int paramInt, ColorStateList paramColorStateList)
  {
    Resources localResources = this.mView.getResources();
    OPCircularBorderDrawable localOPCircularBorderDrawable = new OPCircularBorderDrawable();
    localOPCircularBorderDrawable.setGradientColors(localResources.getColor(R.color.design_fab_stroke_top_outer_color), localResources.getColor(R.color.design_fab_stroke_top_inner_color), localResources.getColor(R.color.design_fab_stroke_end_inner_color), localResources.getColor(R.color.design_fab_stroke_end_outer_color));
    localOPCircularBorderDrawable.setBorderWidth(paramInt);
    localOPCircularBorderDrawable.setTintColor(paramColorStateList.getDefaultColor());
    return localOPCircularBorderDrawable;
  }
  
  void hide(final boolean paramBoolean)
  {
    if ((this.mIsHiding) || (this.mView.getVisibility() != 0)) {
      return;
    }
    if ((!this.mView.isLaidOut()) || (this.mView.isInEditMode()))
    {
      this.mView.internalSetVisibility(8, paramBoolean);
      return;
    }
    this.mView.animate().scaleX(0.0F).scaleY(0.0F).alpha(0.0F).setDuration(200L).setInterpolator(new FastOutSlowInInterpolator()).setListener(new Animator.AnimatorListener()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        OPFloatingActionButtonImpl.-set0(OPFloatingActionButtonImpl.this, false);
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        OPFloatingActionButtonImpl.-set0(OPFloatingActionButtonImpl.this, false);
        OPFloatingActionButtonImpl.this.mView.internalSetVisibility(8, paramBoolean);
      }
      
      public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        OPFloatingActionButtonImpl.-set0(OPFloatingActionButtonImpl.this, true);
        OPFloatingActionButtonImpl.this.mView.internalSetVisibility(0, paramBoolean);
      }
    });
  }
  
  void jumpDrawableToCurrentState() {}
  
  void onDrawableStateChanged(int[] paramArrayOfInt) {}
  
  void setBackground(Drawable paramDrawable, ColorStateList paramColorStateList, PorterDuff.Mode paramMode, int paramInt1, int paramInt2)
  {
    this.mShapeDrawable = paramDrawable.mutate();
    this.mShapeDrawable.setTintList(paramColorStateList);
    if (paramMode != null) {
      this.mShapeDrawable.setTintMode(paramMode);
    }
    if (paramInt2 > 0) {
      this.mBorderDrawable = createBorderDrawable(paramInt2, paramColorStateList);
    }
    for (paramDrawable = new LayerDrawable(new Drawable[] { this.mBorderDrawable, this.mShapeDrawable });; paramDrawable = this.mShapeDrawable)
    {
      this.mRippleDrawable = new RippleDrawable(ColorStateList.valueOf(paramInt1), paramDrawable, null);
      this.mShadowViewDelegate.setBackground(this.mRippleDrawable);
      this.mShadowViewDelegate.setShadowPadding(0, 0, 0, 0);
      return;
      this.mBorderDrawable = null;
    }
  }
  
  void setBackgroundTintList(ColorStateList paramColorStateList)
  {
    this.mShapeDrawable.setTintList(paramColorStateList);
    if (this.mBorderDrawable != null) {
      this.mBorderDrawable.setTintList(paramColorStateList);
    }
  }
  
  void setBackgroundTintMode(PorterDuff.Mode paramMode)
  {
    this.mShapeDrawable.setTintMode(paramMode);
  }
  
  public void setElevation(float paramFloat)
  {
    this.mView.setElevation(paramFloat);
  }
  
  void setPressedTranslationZ(float paramFloat)
  {
    StateListAnimator localStateListAnimator = new StateListAnimator();
    localStateListAnimator.addState(PRESSED_ENABLED_STATE_SET, setupAnimator(ObjectAnimator.ofFloat(this.mView, "translationZ", new float[] { paramFloat })));
    localStateListAnimator.addState(FOCUSED_ENABLED_STATE_SET, setupAnimator(ObjectAnimator.ofFloat(this.mView, "translationZ", new float[] { paramFloat })));
    localStateListAnimator.addState(EMPTY_STATE_SET, setupAnimator(ObjectAnimator.ofFloat(this.mView, "translationZ", new float[] { 0.0F })));
    this.mView.setStateListAnimator(localStateListAnimator);
  }
  
  void setRippleColor(int paramInt)
  {
    this.mRippleDrawable.setColor(ColorStateList.valueOf(paramInt));
  }
  
  void show(final boolean paramBoolean)
  {
    if (this.mView.getVisibility() != 0)
    {
      if ((!this.mView.isLaidOut()) || (this.mView.isInEditMode()))
      {
        this.mView.internalSetVisibility(0, paramBoolean);
        this.mView.setAlpha(1.0F);
        this.mView.setScaleY(1.0F);
        this.mView.setScaleX(1.0F);
      }
    }
    else {
      return;
    }
    this.mView.setAlpha(0.0F);
    this.mView.setScaleY(0.0F);
    this.mView.setScaleX(0.0F);
    this.mView.animate().scaleX(1.0F).scaleY(1.0F).alpha(1.0F).setDuration(200L).setInterpolator(new FastOutSlowInInterpolator()).setListener(new Animator.AnimatorListener()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator) {}
      
      public void onAnimationEnd(Animator paramAnonymousAnimator) {}
      
      public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        OPFloatingActionButtonImpl.this.mView.internalSetVisibility(0, paramBoolean);
      }
    });
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\button\OPFloatingActionButtonImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */