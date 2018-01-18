package com.android.settings.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Path.Op;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnAttachStateChangeListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import com.android.settings.R.styleable;
import java.util.Arrays;

public class DotsPageIndicator
  extends View
  implements ViewPager.OnPageChangeListener
{
  private static final int DEFAULT_ANIM_DURATION = 400;
  private static final int DEFAULT_DOT_SIZE = 8;
  private static final int DEFAULT_GAP = 12;
  private static final int DEFAULT_SELECTED_COLOUR = -1;
  private static final int DEFAULT_UNSELECTED_COLOUR = -2130706433;
  private static final float INVALID_FRACTION = -1.0F;
  private static final float MINIMAL_REVEAL = 1.0E-5F;
  public static final String TAG = DotsPageIndicator.class.getSimpleName();
  private long animDuration;
  private long animHalfDuration;
  private boolean attachedState;
  private final Path combinedUnselectedPath;
  float controlX1;
  float controlX2;
  float controlY1;
  float controlY2;
  private int currentPage;
  private float dotBottomY;
  private float[] dotCenterX;
  private float dotCenterY;
  private int dotDiameter;
  private float dotRadius;
  private float[] dotRevealFractions;
  private float dotTopY;
  float endX1;
  float endX2;
  float endY1;
  float endY2;
  private int gap;
  private float halfDotRadius;
  private final Interpolator interpolator;
  private AnimatorSet joiningAnimationSet;
  private ValueAnimator[] joiningAnimations;
  private float[] joiningFractions;
  private ValueAnimator moveAnimation;
  private ViewPager.OnPageChangeListener pageChangeListener;
  private int pageCount;
  private final RectF rectF;
  private PendingRetreatAnimator retreatAnimation;
  private float retreatingJoinX1;
  private float retreatingJoinX2;
  private PendingRevealAnimator[] revealAnimations;
  private int selectedColour;
  private boolean selectedDotInPosition;
  private float selectedDotX;
  private final Paint selectedPaint;
  private int unselectedColour;
  private final Path unselectedDotLeftPath;
  private final Path unselectedDotPath;
  private final Path unselectedDotRightPath;
  private final Paint unselectedPaint;
  private ViewPager viewPager;
  
  public DotsPageIndicator(Context paramContext)
  {
    this(paramContext, null, 0);
  }
  
  public DotsPageIndicator(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public DotsPageIndicator(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    int i = (int)paramContext.getResources().getDisplayMetrics().scaledDensity;
    paramAttributeSet = getContext().obtainStyledAttributes(paramAttributeSet, R.styleable.DotsPageIndicator, paramInt, 0);
    this.dotDiameter = paramAttributeSet.getDimensionPixelSize(0, i * 8);
    this.dotRadius = (this.dotDiameter / 2);
    this.halfDotRadius = (this.dotRadius / 2.0F);
    this.gap = paramAttributeSet.getDimensionPixelSize(1, i * 12);
    this.animDuration = paramAttributeSet.getInteger(2, 400);
    this.animHalfDuration = (this.animDuration / 2L);
    this.unselectedColour = paramAttributeSet.getColor(3, -2130706433);
    this.selectedColour = paramAttributeSet.getColor(4, -1);
    paramAttributeSet.recycle();
    this.unselectedPaint = new Paint(1);
    this.unselectedPaint.setColor(this.unselectedColour);
    this.selectedPaint = new Paint(1);
    this.selectedPaint.setColor(this.selectedColour);
    if (Build.VERSION.SDK_INT >= 21) {}
    for (this.interpolator = AnimationUtils.loadInterpolator(paramContext, 17563661);; this.interpolator = AnimationUtils.loadInterpolator(paramContext, 17432580))
    {
      this.combinedUnselectedPath = new Path();
      this.unselectedDotPath = new Path();
      this.unselectedDotLeftPath = new Path();
      this.unselectedDotRightPath = new Path();
      this.rectF = new RectF();
      addOnAttachStateChangeListener(new View.OnAttachStateChangeListener()
      {
        public void onViewAttachedToWindow(View paramAnonymousView)
        {
          DotsPageIndicator.-set0(DotsPageIndicator.this, true);
        }
        
        public void onViewDetachedFromWindow(View paramAnonymousView)
        {
          DotsPageIndicator.-set0(DotsPageIndicator.this, false);
        }
      });
      return;
    }
  }
  
  private void calculateDotPositions()
  {
    int i = getPaddingLeft();
    int j = getPaddingTop();
    float f1 = (getWidth() - getPaddingRight() - i - getRequiredWidth()) / 2 + i;
    float f2 = this.dotRadius;
    this.dotCenterX = new float[this.pageCount];
    i = 0;
    while (i < this.pageCount)
    {
      this.dotCenterX[i] = ((this.dotDiameter + this.gap) * i + (f1 + f2));
      i += 1;
    }
    this.dotTopY = j;
    this.dotCenterY = (j + this.dotRadius);
    this.dotBottomY = (this.dotDiameter + j);
    setCurrentPageImmediate();
  }
  
  private void cancelJoiningAnimations()
  {
    if ((this.joiningAnimationSet != null) && (this.joiningAnimationSet.isRunning())) {
      this.joiningAnimationSet.cancel();
    }
  }
  
  private void cancelMoveAnimation()
  {
    if ((this.moveAnimation != null) && (this.moveAnimation.isRunning())) {
      this.moveAnimation.cancel();
    }
  }
  
  private void cancelRetreatAnimation()
  {
    if ((this.retreatAnimation != null) && (this.retreatAnimation.isRunning())) {
      this.retreatAnimation.cancel();
    }
  }
  
  private void cancelRevealAnimations()
  {
    if (this.revealAnimations != null)
    {
      PendingRevealAnimator[] arrayOfPendingRevealAnimator = this.revealAnimations;
      int i = 0;
      int j = arrayOfPendingRevealAnimator.length;
      while (i < j)
      {
        arrayOfPendingRevealAnimator[i].cancel();
        i += 1;
      }
    }
  }
  
  private void cancelRunningAnimations()
  {
    cancelMoveAnimation();
    cancelJoiningAnimations();
    cancelRetreatAnimation();
    cancelRevealAnimations();
    resetState();
  }
  
  private void clearJoiningFractions()
  {
    Arrays.fill(this.joiningFractions, 0.0F);
    postInvalidateOnAnimation();
  }
  
  private ValueAnimator createJoiningAnimator(final int paramInt, long paramLong)
  {
    ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
    localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        DotsPageIndicator.-wrap3(DotsPageIndicator.this, paramInt, paramAnonymousValueAnimator.getAnimatedFraction());
      }
    });
    localValueAnimator.setDuration(this.animHalfDuration);
    localValueAnimator.setStartDelay(paramLong);
    localValueAnimator.setInterpolator(this.interpolator);
    return localValueAnimator;
  }
  
  private ValueAnimator createMoveSelectedAnimator(float paramFloat, int paramInt1, int paramInt2, int paramInt3)
  {
    ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { this.selectedDotX, paramFloat });
    Object localObject;
    if (paramInt2 > paramInt1)
    {
      localObject = new RightwardStartPredicate(paramFloat - (paramFloat - this.selectedDotX) * 0.25F);
      this.retreatAnimation = new PendingRetreatAnimator(paramInt1, paramInt2, paramInt3, (StartPredicate)localObject);
      localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          DotsPageIndicator.-set5(DotsPageIndicator.this, ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
          DotsPageIndicator.-get4(DotsPageIndicator.this).startIfNecessary(DotsPageIndicator.-get8(DotsPageIndicator.this));
          DotsPageIndicator.this.postInvalidateOnAnimation();
        }
      });
      localValueAnimator.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          DotsPageIndicator.-set4(DotsPageIndicator.this, true);
        }
        
        public void onAnimationStart(Animator paramAnonymousAnimator)
        {
          DotsPageIndicator.-set4(DotsPageIndicator.this, false);
        }
      });
      if (!this.selectedDotInPosition) {
        break label169;
      }
    }
    label169:
    for (long l = this.animDuration / 4L;; l = 0L)
    {
      localValueAnimator.setStartDelay(l);
      localValueAnimator.setDuration(this.animDuration * 3L / 4L);
      localValueAnimator.setInterpolator(this.interpolator);
      return localValueAnimator;
      localObject = new LeftwardStartPredicate((this.selectedDotX - paramFloat) * 0.25F + paramFloat);
      break;
    }
  }
  
  private void drawSelected(Canvas paramCanvas)
  {
    paramCanvas.drawCircle(this.selectedDotX, this.dotCenterY, this.dotRadius, this.selectedPaint);
  }
  
  private void drawUnselected(Canvas paramCanvas)
  {
    this.combinedUnselectedPath.rewind();
    int i = 0;
    if (i < this.pageCount)
    {
      int j;
      label34:
      float f1;
      if (i == this.pageCount - 1)
      {
        j = i;
        if (Build.VERSION.SDK_INT < 21) {
          break label134;
        }
        float f2 = this.dotCenterX[i];
        float f3 = this.dotCenterX[j];
        if (i != this.pageCount - 1) {
          break label123;
        }
        f1 = -1.0F;
        label73:
        Path localPath = getUnselectedPath(i, f2, f3, f1, this.dotRevealFractions[i]);
        this.combinedUnselectedPath.op(localPath, Path.Op.UNION);
      }
      for (;;)
      {
        i += 1;
        break;
        j = i + 1;
        break label34;
        label123:
        f1 = this.joiningFractions[i];
        break label73;
        label134:
        paramCanvas.drawCircle(this.dotCenterX[i], this.dotCenterY, this.dotRadius, this.unselectedPaint);
      }
    }
    if ((this.retreatingJoinX1 != -1.0F) && (Build.VERSION.SDK_INT >= 21)) {
      this.combinedUnselectedPath.op(getRetreatingJoinPath(), Path.Op.UNION);
    }
    paramCanvas.drawPath(this.combinedUnselectedPath, this.unselectedPaint);
  }
  
  private int getDesiredHeight()
  {
    return getPaddingTop() + this.dotDiameter + getPaddingBottom();
  }
  
  private int getDesiredWidth()
  {
    return getPaddingLeft() + getRequiredWidth() + getPaddingRight();
  }
  
  private int getRequiredWidth()
  {
    return this.pageCount * this.dotDiameter + (this.pageCount - 1) * this.gap;
  }
  
  private Path getRetreatingJoinPath()
  {
    this.unselectedDotPath.rewind();
    this.rectF.set(this.retreatingJoinX1, this.dotTopY, this.retreatingJoinX2, this.dotBottomY);
    this.unselectedDotPath.addRoundRect(this.rectF, this.dotRadius, this.dotRadius, Path.Direction.CW);
    return this.unselectedDotPath;
  }
  
  private Path getUnselectedPath(int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.unselectedDotPath.rewind();
    if (((paramFloat3 == 0.0F) || (paramFloat3 == -1.0F)) && (paramFloat4 == 0.0F) && ((paramInt != this.currentPage) || (!this.selectedDotInPosition))) {
      this.unselectedDotPath.addCircle(this.dotCenterX[paramInt], this.dotCenterY, this.dotRadius, Path.Direction.CW);
    }
    if ((paramFloat3 > 0.0F) && (paramFloat3 < 0.5F) && (this.retreatingJoinX1 == -1.0F))
    {
      this.unselectedDotLeftPath.rewind();
      this.unselectedDotLeftPath.moveTo(paramFloat1, this.dotBottomY);
      this.rectF.set(paramFloat1 - this.dotRadius, this.dotTopY, this.dotRadius + paramFloat1, this.dotBottomY);
      this.unselectedDotLeftPath.arcTo(this.rectF, 90.0F, 180.0F, true);
      this.endX1 = (this.dotRadius + paramFloat1 + this.gap * paramFloat3);
      this.endY1 = this.dotCenterY;
      this.controlX1 = (this.halfDotRadius + paramFloat1);
      this.controlY1 = this.dotTopY;
      this.controlX2 = this.endX1;
      this.controlY2 = (this.endY1 - this.halfDotRadius);
      this.unselectedDotLeftPath.cubicTo(this.controlX1, this.controlY1, this.controlX2, this.controlY2, this.endX1, this.endY1);
      this.endX2 = paramFloat1;
      this.endY2 = this.dotBottomY;
      this.controlX1 = this.endX1;
      this.controlY1 = (this.endY1 + this.halfDotRadius);
      this.controlX2 = (this.halfDotRadius + paramFloat1);
      this.controlY2 = this.dotBottomY;
      this.unselectedDotLeftPath.cubicTo(this.controlX1, this.controlY1, this.controlX2, this.controlY2, this.endX2, this.endY2);
      if (Build.VERSION.SDK_INT >= 21) {
        this.unselectedDotPath.op(this.unselectedDotLeftPath, Path.Op.UNION);
      }
      this.unselectedDotRightPath.rewind();
      this.unselectedDotRightPath.moveTo(paramFloat2, this.dotBottomY);
      this.rectF.set(paramFloat2 - this.dotRadius, this.dotTopY, this.dotRadius + paramFloat2, this.dotBottomY);
      this.unselectedDotRightPath.arcTo(this.rectF, 90.0F, -180.0F, true);
      this.endX1 = (paramFloat2 - this.dotRadius - this.gap * paramFloat3);
      this.endY1 = this.dotCenterY;
      this.controlX1 = (paramFloat2 - this.halfDotRadius);
      this.controlY1 = this.dotTopY;
      this.controlX2 = this.endX1;
      this.controlY2 = (this.endY1 - this.halfDotRadius);
      this.unselectedDotRightPath.cubicTo(this.controlX1, this.controlY1, this.controlX2, this.controlY2, this.endX1, this.endY1);
      this.endX2 = paramFloat2;
      this.endY2 = this.dotBottomY;
      this.controlX1 = this.endX1;
      this.controlY1 = (this.endY1 + this.halfDotRadius);
      this.controlX2 = (this.endX2 - this.halfDotRadius);
      this.controlY2 = this.dotBottomY;
      this.unselectedDotRightPath.cubicTo(this.controlX1, this.controlY1, this.controlX2, this.controlY2, this.endX2, this.endY2);
      if (Build.VERSION.SDK_INT >= 21) {
        this.unselectedDotPath.op(this.unselectedDotRightPath, Path.Op.UNION);
      }
    }
    if ((paramFloat3 > 0.5F) && (paramFloat3 < 1.0F) && (this.retreatingJoinX1 == -1.0F))
    {
      this.unselectedDotPath.moveTo(paramFloat1, this.dotBottomY);
      this.rectF.set(paramFloat1 - this.dotRadius, this.dotTopY, this.dotRadius + paramFloat1, this.dotBottomY);
      this.unselectedDotPath.arcTo(this.rectF, 90.0F, 180.0F, true);
      this.endX1 = (this.dotRadius + paramFloat1 + this.gap / 2);
      this.endY1 = (this.dotCenterY - this.dotRadius * paramFloat3);
      this.controlX1 = (this.endX1 - this.dotRadius * paramFloat3);
      this.controlY1 = this.dotTopY;
      this.controlX2 = (this.endX1 - (1.0F - paramFloat3) * this.dotRadius);
      this.controlY2 = this.endY1;
      this.unselectedDotPath.cubicTo(this.controlX1, this.controlY1, this.controlX2, this.controlY2, this.endX1, this.endY1);
      this.endX2 = paramFloat2;
      this.endY2 = this.dotTopY;
      this.controlX1 = (this.endX1 + (1.0F - paramFloat3) * this.dotRadius);
      this.controlY1 = this.endY1;
      this.controlX2 = (this.endX1 + this.dotRadius * paramFloat3);
      this.controlY2 = this.dotTopY;
      this.unselectedDotPath.cubicTo(this.controlX1, this.controlY1, this.controlX2, this.controlY2, this.endX2, this.endY2);
      this.rectF.set(paramFloat2 - this.dotRadius, this.dotTopY, this.dotRadius + paramFloat2, this.dotBottomY);
      this.unselectedDotPath.arcTo(this.rectF, 270.0F, 180.0F, true);
      this.endY1 = (this.dotCenterY + this.dotRadius * paramFloat3);
      this.controlX1 = (this.endX1 + this.dotRadius * paramFloat3);
      this.controlY1 = this.dotBottomY;
      this.controlX2 = (this.endX1 + (1.0F - paramFloat3) * this.dotRadius);
      this.controlY2 = this.endY1;
      this.unselectedDotPath.cubicTo(this.controlX1, this.controlY1, this.controlX2, this.controlY2, this.endX1, this.endY1);
      this.endX2 = paramFloat1;
      this.endY2 = this.dotBottomY;
      this.controlX1 = (this.endX1 - (1.0F - paramFloat3) * this.dotRadius);
      this.controlY1 = this.endY1;
      this.controlX2 = (this.endX1 - this.dotRadius * paramFloat3);
      this.controlY2 = this.endY2;
      this.unselectedDotPath.cubicTo(this.controlX1, this.controlY1, this.controlX2, this.controlY2, this.endX2, this.endY2);
    }
    if ((paramFloat3 == 1.0F) && (this.retreatingJoinX1 == -1.0F))
    {
      this.rectF.set(paramFloat1 - this.dotRadius, this.dotTopY, this.dotRadius + paramFloat2, this.dotBottomY);
      this.unselectedDotPath.addRoundRect(this.rectF, this.dotRadius, this.dotRadius, Path.Direction.CW);
    }
    if (paramFloat4 > 1.0E-5F) {
      this.unselectedDotPath.addCircle(paramFloat1, this.dotCenterY, this.dotRadius * paramFloat4, Path.Direction.CW);
    }
    return this.unselectedDotPath;
  }
  
  private void resetState()
  {
    if (this.pageCount > 0)
    {
      this.joiningFractions = new float[this.pageCount - 1];
      Arrays.fill(this.joiningFractions, 0.0F);
      this.dotRevealFractions = new float[this.pageCount];
      Arrays.fill(this.dotRevealFractions, 0.0F);
      this.retreatingJoinX1 = -1.0F;
      this.retreatingJoinX2 = -1.0F;
      this.selectedDotInPosition = true;
    }
  }
  
  private void setCurrentPageImmediate()
  {
    if (this.viewPager != null) {}
    for (this.currentPage = this.viewPager.getCurrentItem();; this.currentPage = 0)
    {
      if (this.pageCount > 0) {
        this.selectedDotX = this.dotCenterX[this.currentPage];
      }
      return;
    }
  }
  
  private void setDotRevealFraction(int paramInt, float paramFloat)
  {
    this.dotRevealFractions[paramInt] = paramFloat;
    postInvalidateOnAnimation();
  }
  
  private void setJoiningFraction(int paramInt, float paramFloat)
  {
    this.joiningFractions[paramInt] = paramFloat;
    postInvalidateOnAnimation();
  }
  
  private void setPageCount(int paramInt)
  {
    this.pageCount = paramInt;
    calculateDotPositions();
    resetState();
  }
  
  private void setSelectedPage(int paramInt)
  {
    if ((paramInt == this.currentPage) || (this.pageCount == 0)) {
      return;
    }
    int k = this.currentPage;
    this.currentPage = paramInt;
    if (Build.VERSION.SDK_INT >= 16)
    {
      cancelRunningAnimations();
      int m = Math.abs(paramInt - k);
      this.moveAnimation = createMoveSelectedAnimator(this.dotCenterX[paramInt], k, paramInt, m);
      this.joiningAnimations = new ValueAnimator[m];
      int i = 0;
      if (i < m)
      {
        ValueAnimator[] arrayOfValueAnimator = this.joiningAnimations;
        if (paramInt > k) {}
        for (int j = k + i;; j = k - 1 - i)
        {
          arrayOfValueAnimator[i] = createJoiningAnimator(j, i * (this.animDuration / 8L));
          i += 1;
          break;
        }
      }
      this.moveAnimation.start();
      startJoiningAnimations();
      return;
    }
    setCurrentPageImmediate();
    invalidate();
  }
  
  private void startJoiningAnimations()
  {
    this.joiningAnimationSet = new AnimatorSet();
    this.joiningAnimationSet.playTogether(this.joiningAnimations);
    this.joiningAnimationSet.start();
  }
  
  public void clearAnimation()
  {
    super.clearAnimation();
    if (Build.VERSION.SDK_INT >= 16) {
      cancelRunningAnimations();
    }
  }
  
  int getCurrentPage()
  {
    return this.currentPage;
  }
  
  float getDotCenterX(int paramInt)
  {
    return this.dotCenterX[paramInt];
  }
  
  float getDotCenterY()
  {
    return this.dotCenterY;
  }
  
  int getSelectedColour()
  {
    return this.selectedColour;
  }
  
  float getSelectedDotX()
  {
    return this.selectedDotX;
  }
  
  int getUnselectedColour()
  {
    return this.unselectedColour;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if ((this.viewPager == null) || (this.pageCount == 0)) {
      return;
    }
    drawUnselected(paramCanvas);
    drawSelected(paramCanvas);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = getDesiredHeight();
    switch (View.MeasureSpec.getMode(paramInt2))
    {
    default: 
      paramInt2 = i;
      i = getDesiredWidth();
      switch (View.MeasureSpec.getMode(paramInt1))
      {
      default: 
        paramInt1 = i;
      }
      break;
    }
    for (;;)
    {
      setMeasuredDimension(paramInt1, paramInt2);
      calculateDotPositions();
      return;
      paramInt2 = View.MeasureSpec.getSize(paramInt2);
      break;
      paramInt2 = Math.min(i, View.MeasureSpec.getSize(paramInt2));
      break;
      paramInt1 = View.MeasureSpec.getSize(paramInt1);
      continue;
      paramInt1 = Math.min(i, View.MeasureSpec.getSize(paramInt1));
    }
  }
  
  public void onPageScrollStateChanged(int paramInt)
  {
    if (this.pageChangeListener != null) {
      this.pageChangeListener.onPageScrollStateChanged(paramInt);
    }
  }
  
  public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2)
  {
    if (this.pageChangeListener != null) {
      this.pageChangeListener.onPageScrolled(paramInt1, paramFloat, paramInt2);
    }
  }
  
  public void onPageSelected(int paramInt)
  {
    if (this.attachedState) {
      setSelectedPage(paramInt);
    }
    for (;;)
    {
      if (this.pageChangeListener != null) {
        this.pageChangeListener.onPageSelected(paramInt);
      }
      return;
      setCurrentPageImmediate();
    }
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    setMeasuredDimension(paramInt1, paramInt2);
    calculateDotPositions();
  }
  
  public void setOnPageChangeListener(ViewPager.OnPageChangeListener paramOnPageChangeListener)
  {
    this.pageChangeListener = paramOnPageChangeListener;
  }
  
  public void setViewPager(ViewPager paramViewPager)
  {
    this.viewPager = paramViewPager;
    paramViewPager.setOnPageChangeListener(this);
    setPageCount(paramViewPager.getAdapter().getCount());
    paramViewPager.getAdapter().registerDataSetObserver(new DataSetObserver()
    {
      public void onChanged()
      {
        DotsPageIndicator.-wrap4(DotsPageIndicator.this, DotsPageIndicator.-get9(DotsPageIndicator.this).getAdapter().getCount());
      }
    });
    setCurrentPageImmediate();
  }
  
  public class LeftwardStartPredicate
    extends DotsPageIndicator.StartPredicate
  {
    public LeftwardStartPredicate(float paramFloat)
    {
      super(paramFloat);
    }
    
    boolean shouldStart(float paramFloat)
    {
      return paramFloat < this.thresholdValue;
    }
  }
  
  public class PendingRetreatAnimator
    extends DotsPageIndicator.PendingStartAnimator
  {
    public PendingRetreatAnimator(int paramInt1, int paramInt2, int paramInt3, final DotsPageIndicator.StartPredicate paramStartPredicate)
    {
      super(paramStartPredicate);
      setDuration(DotsPageIndicator.-get0(DotsPageIndicator.this));
      setInterpolator(DotsPageIndicator.-get3(DotsPageIndicator.this));
      final float f1;
      float f2;
      label72:
      final float f3;
      if (paramInt2 > paramInt1)
      {
        f1 = Math.min(DotsPageIndicator.-get1(DotsPageIndicator.this)[paramInt1], DotsPageIndicator.-get8(DotsPageIndicator.this)) - DotsPageIndicator.-get2(DotsPageIndicator.this);
        if (paramInt2 <= paramInt1) {
          break label220;
        }
        f2 = DotsPageIndicator.-get1(DotsPageIndicator.this)[paramInt2] - DotsPageIndicator.-get2(DotsPageIndicator.this);
        if (paramInt2 <= paramInt1) {
          break label236;
        }
        f3 = DotsPageIndicator.-get1(DotsPageIndicator.this)[paramInt2] + DotsPageIndicator.-get2(DotsPageIndicator.this);
        label90:
        if (paramInt2 <= paramInt1) {
          break label259;
        }
      }
      label220:
      label236:
      label259:
      for (float f4 = DotsPageIndicator.-get1(DotsPageIndicator.this)[paramInt2] + DotsPageIndicator.-get2(DotsPageIndicator.this);; f4 = DotsPageIndicator.-get1(DotsPageIndicator.this)[paramInt2] + DotsPageIndicator.-get2(DotsPageIndicator.this))
      {
        DotsPageIndicator.-set3(DotsPageIndicator.this, new DotsPageIndicator.PendingRevealAnimator[paramInt3]);
        paramStartPredicate = new int[paramInt3];
        if (f1 == f2) {
          break label306;
        }
        setFloatValues(new float[] { f1, f2 });
        paramInt2 = 0;
        while (paramInt2 < paramInt3)
        {
          DotsPageIndicator.-get7(DotsPageIndicator.this)[paramInt2] = new DotsPageIndicator.PendingRevealAnimator(DotsPageIndicator.this, paramInt1 + paramInt2, new DotsPageIndicator.RightwardStartPredicate(DotsPageIndicator.this, DotsPageIndicator.-get1(DotsPageIndicator.this)[(paramInt1 + paramInt2)]));
          paramStartPredicate[paramInt2] = (paramInt1 + paramInt2);
          paramInt2 += 1;
        }
        f1 = DotsPageIndicator.-get1(DotsPageIndicator.this)[paramInt2] - DotsPageIndicator.-get2(DotsPageIndicator.this);
        break;
        f2 = DotsPageIndicator.-get1(DotsPageIndicator.this)[paramInt2] - DotsPageIndicator.-get2(DotsPageIndicator.this);
        break label72;
        f3 = Math.max(DotsPageIndicator.-get1(DotsPageIndicator.this)[paramInt1], DotsPageIndicator.-get8(DotsPageIndicator.this)) + DotsPageIndicator.-get2(DotsPageIndicator.this);
        break label90;
      }
      addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          DotsPageIndicator.-set1(DotsPageIndicator.this, ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
          DotsPageIndicator.this.postInvalidateOnAnimation();
          paramAnonymousValueAnimator = DotsPageIndicator.-get7(DotsPageIndicator.this);
          int i = 0;
          int j = paramAnonymousValueAnimator.length;
          while (i < j)
          {
            paramAnonymousValueAnimator[i].startIfNecessary(DotsPageIndicator.-get5(DotsPageIndicator.this));
            i += 1;
          }
        }
      });
      for (;;)
      {
        addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            DotsPageIndicator.-set1(DotsPageIndicator.this, -1.0F);
            DotsPageIndicator.-set2(DotsPageIndicator.this, -1.0F);
            DotsPageIndicator.this.postInvalidateOnAnimation();
          }
          
          public void onAnimationStart(Animator paramAnonymousAnimator)
          {
            DotsPageIndicator.-wrap0(DotsPageIndicator.this);
            DotsPageIndicator.-wrap1(DotsPageIndicator.this);
            paramAnonymousAnimator = paramStartPredicate;
            int i = 0;
            int j = paramAnonymousAnimator.length;
            while (i < j)
            {
              int k = paramAnonymousAnimator[i];
              DotsPageIndicator.-wrap2(DotsPageIndicator.this, k, 1.0E-5F);
              i += 1;
            }
            DotsPageIndicator.-set1(DotsPageIndicator.this, f1);
            DotsPageIndicator.-set2(DotsPageIndicator.this, f3);
            DotsPageIndicator.this.postInvalidateOnAnimation();
          }
        });
        return;
        label306:
        setFloatValues(new float[] { f3, f4 });
        paramInt2 = 0;
        while (paramInt2 < paramInt3)
        {
          DotsPageIndicator.-get7(DotsPageIndicator.this)[paramInt2] = new DotsPageIndicator.PendingRevealAnimator(DotsPageIndicator.this, paramInt1 - paramInt2, new DotsPageIndicator.LeftwardStartPredicate(DotsPageIndicator.this, DotsPageIndicator.-get1(DotsPageIndicator.this)[(paramInt1 - paramInt2)]));
          paramStartPredicate[paramInt2] = (paramInt1 - paramInt2);
          paramInt2 += 1;
        }
        addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
          public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
          {
            DotsPageIndicator.-set2(DotsPageIndicator.this, ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
            DotsPageIndicator.this.postInvalidateOnAnimation();
            paramAnonymousValueAnimator = DotsPageIndicator.-get7(DotsPageIndicator.this);
            int i = 0;
            int j = paramAnonymousValueAnimator.length;
            while (i < j)
            {
              paramAnonymousValueAnimator[i].startIfNecessary(DotsPageIndicator.-get6(DotsPageIndicator.this));
              i += 1;
            }
          }
        });
      }
    }
  }
  
  public class PendingRevealAnimator
    extends DotsPageIndicator.PendingStartAnimator
  {
    private final int dot;
    
    public PendingRevealAnimator(int paramInt, DotsPageIndicator.StartPredicate paramStartPredicate)
    {
      super(paramStartPredicate);
      this.dot = paramInt;
      setFloatValues(new float[] { 1.0E-5F, 1.0F });
      setDuration(DotsPageIndicator.-get0(DotsPageIndicator.this));
      setInterpolator(DotsPageIndicator.-get3(DotsPageIndicator.this));
      addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          DotsPageIndicator.-wrap2(DotsPageIndicator.this, DotsPageIndicator.PendingRevealAnimator.-get0(DotsPageIndicator.PendingRevealAnimator.this), ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
        }
      });
      addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          DotsPageIndicator.-wrap2(DotsPageIndicator.this, DotsPageIndicator.PendingRevealAnimator.-get0(DotsPageIndicator.PendingRevealAnimator.this), 0.0F);
          DotsPageIndicator.this.postInvalidateOnAnimation();
        }
      });
    }
  }
  
  public abstract class PendingStartAnimator
    extends ValueAnimator
  {
    protected boolean hasStarted;
    protected DotsPageIndicator.StartPredicate predicate;
    
    public PendingStartAnimator(DotsPageIndicator.StartPredicate paramStartPredicate)
    {
      this.predicate = paramStartPredicate;
      this.hasStarted = false;
    }
    
    public void startIfNecessary(float paramFloat)
    {
      if ((!this.hasStarted) && (this.predicate.shouldStart(paramFloat)))
      {
        start();
        this.hasStarted = true;
      }
    }
  }
  
  public class RightwardStartPredicate
    extends DotsPageIndicator.StartPredicate
  {
    public RightwardStartPredicate(float paramFloat)
    {
      super(paramFloat);
    }
    
    boolean shouldStart(float paramFloat)
    {
      return paramFloat > this.thresholdValue;
    }
  }
  
  public abstract class StartPredicate
  {
    protected float thresholdValue;
    
    public StartPredicate(float paramFloat)
    {
      this.thresholdValue = paramFloat;
    }
    
    abstract boolean shouldStart(float paramFloat);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\widget\DotsPageIndicator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */