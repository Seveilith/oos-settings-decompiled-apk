package com.android.settings;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewStub;
import android.view.ViewStub.OnInflateListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import java.lang.reflect.Array;

public class PreviewPagerAdapter
  extends PagerAdapter
{
  private static final long CROSS_FADE_DURATION_MS = 400L;
  private static final Interpolator FADE_IN_INTERPOLATOR = new DecelerateInterpolator();
  private static final Interpolator FADE_OUT_INTERPOLATOR = new AccelerateInterpolator();
  private int mAnimationCounter;
  private Runnable mAnimationEndAction;
  private boolean mIsLayoutRtl;
  private FrameLayout[] mPreviewFrames;
  private boolean[][] mViewStubInflated;
  
  public PreviewPagerAdapter(Context paramContext, boolean paramBoolean, int[] paramArrayOfInt, Configuration[] paramArrayOfConfiguration)
  {
    this.mIsLayoutRtl = paramBoolean;
    this.mPreviewFrames = new FrameLayout[paramArrayOfInt.length];
    this.mViewStubInflated = ((boolean[][])Array.newInstance(Boolean.TYPE, new int[] { paramArrayOfInt.length, paramArrayOfConfiguration.length }));
    final int i = 0;
    while (i < paramArrayOfInt.length)
    {
      if (this.mIsLayoutRtl) {}
      for (int j = paramArrayOfInt.length - 1 - i;; j = i)
      {
        this.mPreviewFrames[j] = new FrameLayout(paramContext);
        this.mPreviewFrames[j].setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        final int k = 0;
        while (k < paramArrayOfConfiguration.length)
        {
          Object localObject = paramContext.createConfigurationContext(paramArrayOfConfiguration[k]);
          ((Context)localObject).setTheme(paramContext.getThemeResId());
          LayoutInflater.from((Context)localObject);
          localObject = new ViewStub((Context)localObject);
          ((ViewStub)localObject).setLayoutResource(paramArrayOfInt[i]);
          ((ViewStub)localObject).setOnInflateListener(new ViewStub.OnInflateListener()
          {
            public void onInflate(ViewStub paramAnonymousViewStub, View paramAnonymousView)
            {
              paramAnonymousView.setVisibility(paramAnonymousViewStub.getVisibility());
              PreviewPagerAdapter.-get1(PreviewPagerAdapter.this)[i][k] = 1;
            }
          });
          this.mPreviewFrames[j].addView((View)localObject);
          k += 1;
        }
      }
      i += 1;
    }
  }
  
  private void runAnimationEndAction()
  {
    if ((this.mAnimationEndAction == null) || (isAnimating())) {
      return;
    }
    this.mAnimationEndAction.run();
    this.mAnimationEndAction = null;
  }
  
  private void setVisibility(final View paramView, final int paramInt, boolean paramBoolean)
  {
    if (paramInt == 0) {}
    for (float f = 1.0F; !paramBoolean; f = 0.0F)
    {
      paramView.setAlpha(f);
      paramView.setVisibility(paramInt);
      return;
    }
    if (paramInt == 0) {}
    for (Interpolator localInterpolator = FADE_IN_INTERPOLATOR; paramInt == 0; localInterpolator = FADE_OUT_INTERPOLATOR)
    {
      paramView.animate().alpha(f).setInterpolator(FADE_IN_INTERPOLATOR).setDuration(400L).setListener(new PreviewFrameAnimatorListener(null)).withStartAction(new Runnable()
      {
        public void run()
        {
          paramView.setVisibility(paramInt);
        }
      });
      return;
    }
    paramView.animate().alpha(f).setInterpolator(FADE_OUT_INTERPOLATOR).setDuration(400L).setListener(new PreviewFrameAnimatorListener(null)).withEndAction(new Runnable()
    {
      public void run()
      {
        paramView.setVisibility(paramInt);
      }
    });
  }
  
  public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
  {
    paramViewGroup.removeView((View)paramObject);
  }
  
  public int getCount()
  {
    return this.mPreviewFrames.length;
  }
  
  public Object instantiateItem(ViewGroup paramViewGroup, int paramInt)
  {
    paramViewGroup.addView(this.mPreviewFrames[paramInt]);
    return this.mPreviewFrames[paramInt];
  }
  
  boolean isAnimating()
  {
    boolean bool = false;
    if (this.mAnimationCounter > 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isViewFromObject(View paramView, Object paramObject)
  {
    return paramView == paramObject;
  }
  
  void setAnimationEndAction(Runnable paramRunnable)
  {
    this.mAnimationEndAction = paramRunnable;
  }
  
  void setPreviewLayer(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    FrameLayout[] arrayOfFrameLayout = this.mPreviewFrames;
    int j = arrayOfFrameLayout.length;
    int i = 0;
    if (i < j)
    {
      Object localObject = arrayOfFrameLayout[i];
      if (paramInt2 >= 0)
      {
        localView = ((FrameLayout)localObject).getChildAt(paramInt2);
        if (this.mViewStubInflated[paramInt3][paramInt2] != 0)
        {
          if (localObject != this.mPreviewFrames[paramInt3]) {
            break label139;
          }
          setVisibility(localView, 4, paramBoolean);
        }
      }
      label71:
      View localView = ((FrameLayout)localObject).getChildAt(paramInt1);
      if (localObject == this.mPreviewFrames[paramInt3])
      {
        localObject = localView;
        if (this.mViewStubInflated[paramInt3][paramInt1] == 0)
        {
          localObject = ((ViewStub)localView).inflate();
          ((View)localObject).setAlpha(0.0F);
        }
        setVisibility((View)localObject, 0, paramBoolean);
      }
      for (;;)
      {
        i += 1;
        break;
        label139:
        setVisibility(localView, 4, false);
        break label71;
        setVisibility(localView, 0, false);
      }
    }
  }
  
  private class PreviewFrameAnimatorListener
    implements Animator.AnimatorListener
  {
    private PreviewFrameAnimatorListener() {}
    
    public void onAnimationCancel(Animator paramAnimator) {}
    
    public void onAnimationEnd(Animator paramAnimator)
    {
      paramAnimator = PreviewPagerAdapter.this;
      PreviewPagerAdapter.-set0(paramAnimator, PreviewPagerAdapter.-get0(paramAnimator) - 1);
      PreviewPagerAdapter.-wrap0(PreviewPagerAdapter.this);
    }
    
    public void onAnimationRepeat(Animator paramAnimator) {}
    
    public void onAnimationStart(Animator paramAnimator)
    {
      paramAnimator = PreviewPagerAdapter.this;
      PreviewPagerAdapter.-set0(paramAnimator, PreviewPagerAdapter.-get0(paramAnimator) + 1);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\PreviewPagerAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */