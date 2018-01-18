package com.oneplus.settings.opfinger;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import com.android.settings.R.styleable;
import java.util.ArrayList;
import java.util.List;

public class SvgView
  extends View
{
  private static final String LOG_TAG = "SvgView";
  private int mDuration;
  private float mFadeFactor;
  private boolean mHaveMoved = false;
  private Thread mLoader;
  private float mOffsetY;
  private final Paint mPaint = new Paint(1);
  private float mParallax = 1.0F;
  private List<SvgHelper.SvgPath> mPaths = new ArrayList(0);
  private float mPhase;
  private final SvgHelper mSvg = new SvgHelper(this.mPaint);
  private ObjectAnimator mSvgAnimator;
  private ObjectAnimator mSvgExceptionAnimator;
  private final Object mSvgLock = new Object();
  private ObjectAnimator mSvgResetAnimator;
  private int mSvgResource;
  
  public SvgView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public SvgView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.mPaint.setStyle(Paint.Style.STROKE);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.SvgView, paramInt, 0);
    if (paramContext != null) {}
    try
    {
      paramAttributeSet = getResources().getDisplayMetrics();
      this.mPaint.setStrokeWidth(paramAttributeSet.densityDpi / 50);
      this.mPaint.setColor(paramContext.getColor(1, -16777216));
      this.mPhase = paramContext.getFloat(2, 1.0F);
      this.mDuration = paramContext.getInt(3, 4000);
      this.mFadeFactor = paramContext.getFloat(4, 10.0F);
      if (paramContext != null) {
        paramContext.recycle();
      }
      this.mPaint.setStrokeCap(Paint.Cap.ROUND);
      paramContext = Shader.TileMode.REPEAT;
      new LinearGradient(0.0F, 0.0F, 100.0F, 100.0F, new int[] { -65536, -16711936, -16776961, 65280 }, null, paramContext);
      return;
    }
    finally
    {
      if (paramContext != null) {
        paramContext.recycle();
      }
    }
  }
  
  private void updatePathsPhaseLocked()
  {
    int j = this.mPaths.size();
    int i = 0;
    while (i < j)
    {
      SvgHelper.SvgPath localSvgPath = (SvgHelper.SvgPath)this.mPaths.get(i);
      localSvgPath.renderPath.reset();
      localSvgPath.measure.getSegment(0.0F, localSvgPath.length * this.mPhase, localSvgPath.renderPath, true);
      localSvgPath.renderPath.rLineTo(0.0F, 0.0F);
      i += 1;
    }
  }
  
  public float getParallax()
  {
    return this.mParallax;
  }
  
  public float getPhase()
  {
    return this.mPhase;
  }
  
  public int getSvgResource()
  {
    return this.mSvgResource;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    synchronized (this.mSvgLock)
    {
      paramCanvas.save();
      paramCanvas.translate(getPaddingLeft(), getPaddingTop() + this.mOffsetY);
      int j = this.mPaths.size();
      int i = 0;
      while (i < j)
      {
        SvgHelper.SvgPath localSvgPath = (SvgHelper.SvgPath)this.mPaths.get(i);
        int k = (int)(Math.min(this.mPhase * this.mFadeFactor, 1.0F) * 255.0F);
        localSvgPath.paint.setAlpha((int)(k * this.mParallax));
        paramCanvas.drawPath(localSvgPath.renderPath, localSvgPath.paint);
        i += 1;
      }
      paramCanvas.restore();
      return;
    }
  }
  
  protected void onSizeChanged(final int paramInt1, final int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (this.mLoader != null) {}
    try
    {
      this.mLoader.join();
      this.mLoader = new Thread(new Runnable()
      {
        public void run()
        {
          SvgView.-get0(SvgView.this).load(SvgView.this.getContext(), SvgView.-get2(SvgView.this));
          synchronized (SvgView.-get1(SvgView.this))
          {
            SvgView.-set0(SvgView.this, SvgView.-get0(SvgView.this).getPathsForViewport(paramInt1 - SvgView.this.getPaddingLeft() - SvgView.this.getPaddingRight(), paramInt2 - SvgView.this.getPaddingTop() - SvgView.this.getPaddingBottom()));
            SvgView.-wrap0(SvgView.this);
            return;
          }
        }
      }, "SVG Loader");
      this.mLoader.start();
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      for (;;)
      {
        Log.e("SvgView", "Unexpected error", localInterruptedException);
      }
    }
  }
  
  public void reset(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mSvgResetAnimator = null;
      this.mSvgResetAnimator = ObjectAnimator.ofFloat(this, "phase", new float[] { 1.0F, 0.0F });
      this.mSvgResetAnimator.setDuration(0L);
      this.mSvgResetAnimator.start();
    }
    for (;;)
    {
      this.mHaveMoved = false;
      invalidate();
      return;
      if (this.mSvgResetAnimator == null)
      {
        this.mSvgResetAnimator = ObjectAnimator.ofFloat(this, "phase", new float[] { 1.0F, 0.0F });
        this.mSvgResetAnimator.setDuration(this.mDuration);
        this.mSvgResetAnimator.start();
      }
    }
  }
  
  public void resetWithAnimation()
  {
    this.mSvgResetAnimator = null;
    this.mSvgResetAnimator = ObjectAnimator.ofFloat(this, "phase", new float[] { 1.0F, 0.0F });
    this.mSvgResetAnimator.setDuration(this.mDuration);
    this.mSvgResetAnimator.start();
    this.mHaveMoved = false;
  }
  
  public void resetWithoutAnimation()
  {
    this.mSvgResetAnimator = null;
    this.mSvgResetAnimator = ObjectAnimator.ofFloat(this, "phase", new float[] { 1.0F, 0.0F });
    this.mSvgResetAnimator.setDuration(0L);
    this.mSvgResetAnimator.start();
    this.mHaveMoved = false;
  }
  
  public void reveal(View paramView, int paramInt)
  {
    if (this.mSvgAnimator == null)
    {
      this.mSvgAnimator = ObjectAnimator.ofFloat(this, "phase", new float[] { 0.0F, 1.0F });
      this.mSvgAnimator.setDuration(this.mDuration);
      this.mSvgAnimator.start();
    }
    float f = this.mOffsetY;
    this.mOffsetY = Math.min(0, paramView.getHeight() - (paramInt - paramView.getScrollY()));
    if (f != this.mOffsetY) {
      invalidate();
    }
  }
  
  public void reveal(boolean paramBoolean)
  {
    ObjectAnimator localObjectAnimator;
    if (!this.mHaveMoved)
    {
      this.mSvgAnimator = null;
      this.mSvgAnimator = ObjectAnimator.ofFloat(this, "phase", new float[] { 0.0F, 1.0F });
      this.mSvgAnimator.setDuration(this.mDuration);
      localObjectAnimator = ObjectAnimator.ofFloat(this, "phase", new float[] { 1.0F, 0.0F });
      localObjectAnimator.setDuration(this.mDuration);
      if (paramBoolean) {
        this.mSvgAnimator.start();
      }
    }
    for (;;)
    {
      this.mHaveMoved = true;
      return;
      AnimatorSet localAnimatorSet = new AnimatorSet();
      localAnimatorSet.playSequentially(new Animator[] { this.mSvgAnimator, localObjectAnimator });
      localAnimatorSet.start();
      continue;
      this.mSvgAnimator = ObjectAnimator.ofFloat(this, "phase", new float[] { 0.0F, 1.0F });
      this.mSvgAnimator.setDuration(this.mDuration);
      localObjectAnimator = ObjectAnimator.ofFloat(this, "phase", new float[] { 1.0F, 0.0F });
      localObjectAnimator.setDuration(this.mDuration);
      if (paramBoolean)
      {
        this.mSvgAnimator.start();
      }
      else
      {
        localAnimatorSet = new AnimatorSet();
        localAnimatorSet.playSequentially(new Animator[] { this.mSvgAnimator, localObjectAnimator });
        localAnimatorSet.start();
      }
    }
  }
  
  public void revealWithoutAnimation()
  {
    this.mSvgResetAnimator = null;
    this.mSvgAnimator = ObjectAnimator.ofFloat(this, "phase", new float[] { 0.0F, 1.0F });
    this.mSvgAnimator.setDuration(0L);
    this.mSvgAnimator.start();
  }
  
  public void setParallax(float paramFloat)
  {
    this.mParallax = paramFloat;
    invalidate();
  }
  
  public void setPhase(float paramFloat)
  {
    this.mPhase = paramFloat;
    synchronized (this.mSvgLock)
    {
      updatePathsPhaseLocked();
      invalidate();
      return;
    }
  }
  
  public void setSvgResource(int paramInt)
  {
    this.mSvgResource = paramInt;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\opfinger\SvgView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */