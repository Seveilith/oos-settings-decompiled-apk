package com.oneplus.settings.password;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings.System;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import java.util.ArrayList;
import java.util.Stack;

public class OPPasswordTextViewForPin
  extends View
{
  private static final long APPEAR_DURATION = 160L;
  private static final long DISAPPEAR_DURATION = 160L;
  private static final long DOT_APPEAR_DURATION_OVERSHOOT = 320L;
  private static final long DOT_APPEAR_TEXT_DISAPPEAR_OVERLAP_DURATION = 130L;
  private static final float DOT_OVERSHOOT_FACTOR = 1.5F;
  private static final int MAX_LOCK_PASSWORD_SIZE = 16;
  private static final float OVERSHOOT_TIME_POSITION = 0.5F;
  private static final long RESET_DELAY_PER_ELEMENT = 40L;
  private static final long RESET_MAX_DELAY = 200L;
  private static final long TEXT_REST_DURATION_AFTER_APPEAR = 100L;
  private static final long TEXT_VISIBILITY_DURATION = 1300L;
  private CharState charState = new CharState(null);
  private CharState charState2 = new CharState(null);
  private CharState charState3 = new CharState(null);
  private CharState charState4 = new CharState(null);
  private int inputCount = 0;
  private boolean isAllowDelete = true;
  private boolean isDelete = false;
  private boolean isDrawEmptyCircleAfterDelete = true;
  private AccelerateInterpolator mAccelerateInterpolator;
  private Interpolator mAppearInterpolator;
  private OPPasswordInputCountCallBack mCallBack = null;
  private int mCharPadding;
  private Stack<CharState> mCharPool = new Stack();
  private Interpolator mDisappearInterpolator;
  private int mDotSize;
  private int mDotSizeEmpty;
  private final Paint mDrawAlphaPaint1 = new Paint();
  private final Paint mDrawAlphaPaint2 = new Paint();
  private final Paint mDrawAlphaPaint3 = new Paint();
  private final Paint mDrawAlphaPaint4 = new Paint();
  private final Paint mDrawEmptyCirclePaint = new Paint();
  private final Paint mDrawPaint = new Paint();
  private int mEmptyCircleWidth;
  private Interpolator mFastOutSlowInInterpolator;
  private Handler mHandler = new Handler();
  public OnTextEmptyListerner mOnTextEmptyListerner;
  private PowerManager mPM;
  private boolean mPasswordCheckState = true;
  private float mScreenDensity;
  private boolean mShowPassword;
  private String mText = "";
  private ArrayList<CharState> mTextChars = new ArrayList();
  private final int mTextHeightRaw;
  
  public OPPasswordTextViewForPin(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public OPPasswordTextViewForPin(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public OPPasswordTextViewForPin(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public OPPasswordTextViewForPin(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setFocusableInTouchMode(true);
    setFocusable(true);
    this.mTextHeightRaw = 28;
    this.mEmptyCircleWidth = getContext().getResources().getDimensionPixelSize(2131755688);
    this.mDrawPaint.setFlags(129);
    this.mDrawPaint.setTextAlign(Paint.Align.CENTER);
    boolean bool = false;
    if (getTag() != null) {
      bool = getTag().equals("applock");
    }
    paramAttributeSet = this.mDrawPaint;
    Resources localResources = paramContext.getResources();
    if (bool)
    {
      paramInt1 = 17170443;
      paramAttributeSet.setColor(localResources.getColor(paramInt1));
      this.mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
      this.mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
      this.mDrawPaint.setStrokeWidth(this.mEmptyCircleWidth);
      this.mDrawPaint.setStyle(Paint.Style.FILL);
      this.mDrawPaint.setTextSize(getContext().getResources().getDimensionPixelSize(2131755689));
      this.mDrawPaint.setAntiAlias(true);
      this.mDotSize = getContext().getResources().getDimensionPixelSize(2131755686);
      this.mDotSizeEmpty = getContext().getResources().getDimensionPixelSize(2131755687);
      this.mDrawEmptyCirclePaint.setStrokeWidth(this.mEmptyCircleWidth);
      this.mDrawEmptyCirclePaint.setStyle(Paint.Style.STROKE);
      this.mDrawEmptyCirclePaint.setStrokeJoin(Paint.Join.ROUND);
      this.mDrawEmptyCirclePaint.setStrokeCap(Paint.Cap.ROUND);
      this.mDrawEmptyCirclePaint.setColor(-587202560);
      paramAttributeSet = this.mDrawEmptyCirclePaint;
      localResources = paramContext.getResources();
      if (!bool) {
        break label841;
      }
      paramInt1 = i;
      label479:
      paramAttributeSet.setColor(localResources.getColor(paramInt1));
      this.mDrawEmptyCirclePaint.setAntiAlias(true);
      this.mDrawAlphaPaint1.setStyle(Paint.Style.FILL);
      this.mDrawAlphaPaint1.setStrokeJoin(Paint.Join.ROUND);
      this.mDrawAlphaPaint1.setStrokeCap(Paint.Cap.ROUND);
      this.mDrawAlphaPaint1.setColor(16777215);
      this.mDrawAlphaPaint1.setStrokeWidth(this.mEmptyCircleWidth);
      this.mDrawAlphaPaint2.setStyle(Paint.Style.FILL);
      this.mDrawAlphaPaint2.setStrokeJoin(Paint.Join.ROUND);
      this.mDrawAlphaPaint2.setStrokeCap(Paint.Cap.ROUND);
      this.mDrawAlphaPaint2.setColor(16777215);
      this.mDrawAlphaPaint2.setStrokeWidth(this.mEmptyCircleWidth);
      this.mDrawAlphaPaint3.setStyle(Paint.Style.FILL);
      this.mDrawAlphaPaint3.setStrokeJoin(Paint.Join.ROUND);
      this.mDrawAlphaPaint3.setStrokeCap(Paint.Cap.ROUND);
      this.mDrawAlphaPaint3.setColor(16777215);
      this.mDrawAlphaPaint3.setStrokeWidth(this.mEmptyCircleWidth);
      this.mDrawAlphaPaint4.setStyle(Paint.Style.FILL);
      this.mDrawAlphaPaint4.setStrokeJoin(Paint.Join.ROUND);
      this.mDrawAlphaPaint4.setStrokeCap(Paint.Cap.ROUND);
      this.mDrawAlphaPaint4.setColor(16777215);
      this.mDrawAlphaPaint4.setStrokeWidth(this.mEmptyCircleWidth);
      this.mCharPadding = getContext().getResources().getDimensionPixelSize(2131755690);
      if (Settings.System.getInt(this.mContext.getContentResolver(), "show_password", 1) != 1) {
        break label848;
      }
    }
    label841:
    label848:
    for (bool = true;; bool = false)
    {
      this.mShowPassword = bool;
      this.mAccelerateInterpolator = new AccelerateInterpolator();
      this.mAppearInterpolator = AnimationUtils.loadInterpolator(this.mContext, 17563662);
      this.mDisappearInterpolator = AnimationUtils.loadInterpolator(this.mContext, 17563663);
      this.mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(this.mContext, 17563661);
      this.mPM = ((PowerManager)this.mContext.getSystemService("power"));
      this.mScreenDensity = paramContext.getResources().getDisplayMetrics().density;
      return;
      paramInt1 = 2131493760;
      break;
      paramInt1 = 2131493760;
      break label479;
    }
  }
  
  private Rect getCharBounds()
  {
    Rect localRect = new Rect();
    this.mDrawPaint.getTextBounds("0", 0, 1, localRect);
    return localRect;
  }
  
  private float getDrawingWidth()
  {
    int i = 0;
    int m = Math.min(this.mTextChars.size(), 16);
    Object localObject = getCharBounds();
    int n = ((Rect)localObject).right;
    int i1 = ((Rect)localObject).left;
    int j = 0;
    while (j < m)
    {
      localObject = (CharState)this.mTextChars.get(j);
      int k = i;
      if (j != 0) {
        k = (int)(i + this.mCharPadding * ((CharState)localObject).currentDotSizeFactor);
      }
      i = (int)(k + (n - i1) * ((CharState)localObject).currentDotSizeFactor);
      j += 1;
    }
    return i;
  }
  
  private CharState obtainCharState(char paramChar)
  {
    CharState localCharState = new CharState(null);
    this.mCharPool.push(localCharState);
    localCharState.whichChar = paramChar;
    return localCharState;
  }
  
  private void userActivity()
  {
    this.mPM.userActivity(SystemClock.uptimeMillis(), false);
  }
  
  public void append(char paramChar)
  {
    this.isAllowDelete = true;
    int i = Math.min(this.mTextChars.size(), 16);
    Object localObject = this.mText;
    this.mText += paramChar;
    int j = this.mText.length();
    if (j > i)
    {
      localObject = obtainCharState(paramChar);
      this.mTextChars.add(localObject);
    }
    for (;;)
    {
      ((CharState)localObject).startAppearAnimation();
      if (j > 1)
      {
        localObject = (CharState)this.mTextChars.get(j - 2);
        if (((CharState)localObject).isDotSwapPending) {
          ((CharState)localObject).swapToDotWhenAppearFinished();
        }
      }
      if (this.mOnTextEmptyListerner != null) {
        this.mOnTextEmptyListerner.onTextChanged(this.mText);
      }
      if (j == 16) {
        this.mCallBack.setNumbPadKeyForPinEnable(false);
      }
      userActivity();
      return;
      localObject = (CharState)this.mTextChars.get(j - 1);
      ((CharState)localObject).whichChar = paramChar;
    }
  }
  
  public void deleteLastChar()
  {
    int i = this.mText.length();
    String str = this.mText;
    if (i > 0)
    {
      this.mText = this.mText.substring(0, i - 1);
      ((CharState)this.mTextChars.get(i - 1)).startRemoveAnimation(0L, 0L);
    }
    if (this.mOnTextEmptyListerner != null) {
      this.mOnTextEmptyListerner.onTextChanged(this.mText);
    }
    userActivity();
  }
  
  public int getMaxLockPasswordSize()
  {
    return 16;
  }
  
  public String getText()
  {
    return this.mText;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    float f1 = getDrawingWidth();
    f1 = getWidth() / 2 - f1 / 2.0F;
    int j = Math.min(this.mTextChars.size(), 16);
    Rect localRect = getCharBounds();
    int k = localRect.bottom;
    int m = localRect.top;
    float f2 = getHeight() / 2;
    float f3 = localRect.right - localRect.left;
    this.inputCount = this.mText.length();
    int i = 0;
    while (i < j)
    {
      f1 += ((CharState)this.mTextChars.get(i)).draw(paramCanvas, f1, k - m, f2, f3);
      i += 1;
    }
  }
  
  public void reset(boolean paramBoolean)
  {
    this.mText = "";
    this.inputCount = 0;
    this.mTextChars.clear();
    this.mCharPool.clear();
    this.mPasswordCheckState = true;
    this.mCallBack.setNumbPadKeyForPinEnable(true);
    if (this.mOnTextEmptyListerner != null) {
      this.mOnTextEmptyListerner.onTextChanged(this.mText);
    }
    invalidate();
  }
  
  public void setCallBack(OPPasswordInputCountCallBack paramOPPasswordInputCountCallBack)
  {
    this.mCallBack = paramOPPasswordInputCountCallBack;
  }
  
  public void setTextEmptyListener(OnTextEmptyListerner paramOnTextEmptyListerner)
  {
    this.mOnTextEmptyListerner = paramOnTextEmptyListerner;
  }
  
  private class CharState
  {
    float currentDotSizeFactor;
    float currentDotSizeFactor2;
    float currentDotSizeFactor3;
    float currentDotSizeFactor4;
    float currentEmptyCircleSizeFactor = 1.0F;
    float currentTextSizeFactor;
    float currentTextTranslationY = 1.0F;
    float currentWidthFactor;
    boolean dotAnimationIsGrowing;
    Animator dotAnimator;
    Animator.AnimatorListener dotFinishListener = new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        OPPasswordTextViewForPin.CharState.this.dotAnimator = null;
      }
    };
    private ValueAnimator.AnimatorUpdateListener dotSizeUpdater = new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        OPPasswordTextViewForPin.CharState.this.currentDotSizeFactor = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        OPPasswordTextViewForPin.this.invalidate();
      }
    };
    private ValueAnimator.AnimatorUpdateListener dotSizeUpdater2 = new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        OPPasswordTextViewForPin.CharState.this.currentDotSizeFactor2 = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        OPPasswordTextViewForPin.this.invalidate();
      }
    };
    private ValueAnimator.AnimatorUpdateListener dotSizeUpdater3 = new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        OPPasswordTextViewForPin.CharState.this.currentDotSizeFactor3 = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        OPPasswordTextViewForPin.this.invalidate();
      }
    };
    private ValueAnimator.AnimatorUpdateListener dotSizeUpdater4 = new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        OPPasswordTextViewForPin.CharState.this.currentDotSizeFactor4 = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        OPPasswordTextViewForPin.this.invalidate();
      }
    };
    private Runnable dotSwapperRunnable = new Runnable()
    {
      public void run()
      {
        OPPasswordTextViewForPin.CharState.-wrap1(OPPasswordTextViewForPin.CharState.this);
        OPPasswordTextViewForPin.CharState.this.isDotSwapPending = false;
      }
    };
    boolean emptyCircleAnimationIsGrowing;
    ValueAnimator emptyCircleAnimator;
    Animator.AnimatorListener emptyCircleFinishListener = new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        OPPasswordTextViewForPin.CharState.this.emptyCircleAnimator = null;
      }
    };
    private ValueAnimator.AnimatorUpdateListener emptyCircleSizeUpdater = new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        OPPasswordTextViewForPin.CharState.this.currentEmptyCircleSizeFactor = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        OPPasswordTextViewForPin.this.invalidate();
      }
    };
    boolean isDotSwapPending;
    Animator.AnimatorListener passwordErrorFinishListener = new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        OPPasswordTextViewForPin.this.reset(true);
      }
    };
    Animator.AnimatorListener removeEndListener = new AnimatorListenerAdapter()
    {
      private boolean mCancelled;
      
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        this.mCancelled = true;
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if (!this.mCancelled)
        {
          OPPasswordTextViewForPin.-get14(OPPasswordTextViewForPin.this).remove(OPPasswordTextViewForPin.CharState.this);
          OPPasswordTextViewForPin.-get3(OPPasswordTextViewForPin.this).push(OPPasswordTextViewForPin.CharState.this);
          OPPasswordTextViewForPin.CharState.this.resetState();
          OPPasswordTextViewForPin.CharState.-wrap0(OPPasswordTextViewForPin.CharState.this, OPPasswordTextViewForPin.CharState.this.textTranslateAnimator);
          OPPasswordTextViewForPin.CharState.this.textTranslateAnimator = null;
        }
      }
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        this.mCancelled = false;
      }
    };
    boolean textAnimationIsGrowing;
    ValueAnimator textAnimator;
    Animator.AnimatorListener textFinishListener = new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        OPPasswordTextViewForPin.CharState.this.textAnimator = null;
      }
    };
    private ValueAnimator.AnimatorUpdateListener textSizeUpdater = new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        OPPasswordTextViewForPin.CharState.this.currentTextSizeFactor = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        OPPasswordTextViewForPin.this.invalidate();
      }
    };
    ValueAnimator textTranslateAnimator;
    Animator.AnimatorListener textTranslateFinishListener = new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        OPPasswordTextViewForPin.CharState.this.textTranslateAnimator = null;
      }
    };
    private ValueAnimator.AnimatorUpdateListener textTranslationUpdater = new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        OPPasswordTextViewForPin.CharState.this.currentTextTranslationY = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        OPPasswordTextViewForPin.this.invalidate();
      }
    };
    char whichChar;
    boolean widthAnimationIsGrowing;
    ValueAnimator widthAnimator;
    Animator.AnimatorListener widthFinishListener = new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        OPPasswordTextViewForPin.CharState.this.widthAnimator = null;
      }
    };
    private ValueAnimator.AnimatorUpdateListener widthUpdater = new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        OPPasswordTextViewForPin.CharState.this.currentWidthFactor = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        OPPasswordTextViewForPin.this.invalidate();
      }
    };
    
    private CharState() {}
    
    private void cancelAnimator(Animator paramAnimator)
    {
      if (paramAnimator != null) {
        paramAnimator.cancel();
      }
    }
    
    private void performSwap()
    {
      startTextDisappearAnimation(0L);
      startDotAppearAnimation(30L);
    }
    
    private void postDotSwap(long paramLong)
    {
      removeDotSwapCallbacks();
      OPPasswordTextViewForPin.this.postDelayed(this.dotSwapperRunnable, paramLong);
      this.isDotSwapPending = true;
    }
    
    private void removeDotSwapCallbacks()
    {
      OPPasswordTextViewForPin.this.removeCallbacks(this.dotSwapperRunnable);
      this.isDotSwapPending = false;
    }
    
    private void startDotAppearAnimation(long paramLong)
    {
      cancelAnimator(this.dotAnimator);
      ValueAnimator localValueAnimator1;
      AnimatorSet localAnimatorSet;
      if (!OPPasswordTextViewForPin.-get13(OPPasswordTextViewForPin.this))
      {
        localValueAnimator1 = ValueAnimator.ofFloat(new float[] { this.currentDotSizeFactor, 1.5F });
        localValueAnimator1.addUpdateListener(this.dotSizeUpdater);
        localValueAnimator1.setInterpolator(OPPasswordTextViewForPin.-get1(OPPasswordTextViewForPin.this));
        localValueAnimator1.setDuration(160L);
        ValueAnimator localValueAnimator2 = ValueAnimator.ofFloat(new float[] { 1.5F, 1.0F });
        localValueAnimator2.addUpdateListener(this.dotSizeUpdater);
        localValueAnimator2.setDuration(160L);
        localValueAnimator2.addListener(this.dotFinishListener);
        localAnimatorSet = new AnimatorSet();
        localAnimatorSet.playSequentially(new Animator[] { localValueAnimator1, localValueAnimator2 });
        localAnimatorSet.setStartDelay(paramLong);
        localAnimatorSet.start();
      }
      for (this.dotAnimator = localAnimatorSet;; this.dotAnimator = localValueAnimator1)
      {
        this.dotAnimationIsGrowing = true;
        return;
        localValueAnimator1 = ValueAnimator.ofFloat(new float[] { this.currentDotSizeFactor, 1.0F });
        localValueAnimator1.addUpdateListener(this.dotSizeUpdater);
        localValueAnimator1.setDuration(((1.0F - this.currentDotSizeFactor) * 160.0F));
        localValueAnimator1.addListener(this.dotFinishListener);
        localValueAnimator1.setStartDelay(paramLong);
        localValueAnimator1.start();
      }
    }
    
    private void startDotDisappearAnimation(long paramLong)
    {
      cancelAnimator(this.dotAnimator);
      ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { this.currentDotSizeFactor, 0.0F });
      localValueAnimator.addUpdateListener(this.dotSizeUpdater);
      localValueAnimator.addListener(this.dotFinishListener);
      localValueAnimator.setInterpolator(OPPasswordTextViewForPin.-get4(OPPasswordTextViewForPin.this));
      localValueAnimator.setDuration((Math.min(this.currentDotSizeFactor, 1.0F) * 160.0F));
      localValueAnimator.setStartDelay(paramLong);
      localValueAnimator.start();
      this.dotAnimator = localValueAnimator;
      this.dotAnimationIsGrowing = false;
    }
    
    private void startEmptyCircleAppearAnimation(long paramLong)
    {
      cancelAnimator(this.emptyCircleAnimator);
      ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { 1.1F, 1.0F });
      localValueAnimator.addUpdateListener(this.emptyCircleSizeUpdater);
      localValueAnimator.addListener(this.emptyCircleFinishListener);
      localValueAnimator.setInterpolator(OPPasswordTextViewForPin.-get1(OPPasswordTextViewForPin.this));
      localValueAnimator.setDuration((Math.min(this.currentEmptyCircleSizeFactor, 1.0F) * 250.0F));
      localValueAnimator.setStartDelay(paramLong);
      localValueAnimator.start();
      this.emptyCircleAnimator = localValueAnimator;
    }
    
    private void startPasswordErrorAnimation()
    {
      this.currentDotSizeFactor = 1.0F;
      ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { 1.0F, 0.0F });
      localValueAnimator.addUpdateListener(this.dotSizeUpdater);
      localValueAnimator.setInterpolator(OPPasswordTextViewForPin.-get0(OPPasswordTextViewForPin.this));
      localValueAnimator.setDuration(200L);
      localValueAnimator.start();
    }
    
    private void startPasswordErrorAnimation2()
    {
      this.currentDotSizeFactor2 = 1.0F;
      ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { 1.0F, 0.0F });
      localValueAnimator.addUpdateListener(this.dotSizeUpdater2);
      localValueAnimator.setInterpolator(OPPasswordTextViewForPin.-get0(OPPasswordTextViewForPin.this));
      localValueAnimator.setDuration(200L);
      localValueAnimator.setStartDelay(66L);
      localValueAnimator.start();
    }
    
    private void startPasswordErrorAnimation3()
    {
      this.currentDotSizeFactor3 = 1.0F;
      ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { 1.0F, 0.0F });
      localValueAnimator.addUpdateListener(this.dotSizeUpdater3);
      localValueAnimator.setInterpolator(OPPasswordTextViewForPin.-get0(OPPasswordTextViewForPin.this));
      localValueAnimator.setDuration(200L);
      localValueAnimator.setStartDelay(132L);
      localValueAnimator.start();
    }
    
    private void startPasswordErrorAnimation4()
    {
      this.currentDotSizeFactor4 = 1.0F;
      ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { 1.0F, 0.0F });
      localValueAnimator.addUpdateListener(this.dotSizeUpdater4);
      localValueAnimator.setInterpolator(OPPasswordTextViewForPin.-get0(OPPasswordTextViewForPin.this));
      localValueAnimator.addListener(this.passwordErrorFinishListener);
      localValueAnimator.setDuration(200L);
      localValueAnimator.setStartDelay(198L);
      localValueAnimator.start();
    }
    
    private void startTextAppearAnimation()
    {
      cancelAnimator(this.textAnimator);
      this.textAnimator = ValueAnimator.ofFloat(new float[] { this.currentTextSizeFactor, 1.0F });
      this.textAnimator.addUpdateListener(this.textSizeUpdater);
      this.textAnimator.addListener(this.textFinishListener);
      this.textAnimator.setInterpolator(OPPasswordTextViewForPin.-get1(OPPasswordTextViewForPin.this));
      this.textAnimator.setDuration(((1.0F - this.currentTextSizeFactor) * 160.0F));
      this.textAnimator.start();
      this.textAnimationIsGrowing = true;
      if (this.textTranslateAnimator == null)
      {
        this.textTranslateAnimator = ValueAnimator.ofFloat(new float[] { 0.8F, 0.0F });
        this.textTranslateAnimator.addUpdateListener(this.textTranslationUpdater);
        this.textTranslateAnimator.addListener(this.textTranslateFinishListener);
        this.textTranslateAnimator.setInterpolator(OPPasswordTextViewForPin.-get1(OPPasswordTextViewForPin.this));
        this.textTranslateAnimator.setDuration(160L);
        this.textTranslateAnimator.start();
      }
    }
    
    private void startTextDisappearAnimation(long paramLong)
    {
      cancelAnimator(this.textAnimator);
      this.textAnimator = ValueAnimator.ofFloat(new float[] { this.currentTextSizeFactor, 0.0F });
      this.textAnimator.addUpdateListener(this.textSizeUpdater);
      this.textAnimator.addListener(this.textFinishListener);
      this.textAnimator.setInterpolator(OPPasswordTextViewForPin.-get4(OPPasswordTextViewForPin.this));
      this.textAnimator.setDuration((this.currentTextSizeFactor * 160.0F));
      this.textAnimator.setStartDelay(paramLong);
      this.textAnimator.start();
      this.textAnimationIsGrowing = false;
    }
    
    private void startWidthAppearAnimation()
    {
      cancelAnimator(this.widthAnimator);
      this.widthAnimator = ValueAnimator.ofFloat(new float[] { this.currentWidthFactor, 1.0F });
      this.widthAnimator.addUpdateListener(this.widthUpdater);
      this.widthAnimator.addListener(this.widthFinishListener);
      this.widthAnimator.setDuration(((1.0F - this.currentWidthFactor) * 160.0F));
      this.widthAnimator.start();
      this.widthAnimationIsGrowing = true;
    }
    
    private void startWidthDisappearAnimation(long paramLong)
    {
      cancelAnimator(this.widthAnimator);
      this.widthAnimator = ValueAnimator.ofFloat(new float[] { this.currentWidthFactor, 0.0F });
      this.widthAnimator.addUpdateListener(this.widthUpdater);
      this.widthAnimator.addListener(this.widthFinishListener);
      this.widthAnimator.addListener(this.removeEndListener);
      this.widthAnimator.setDuration((this.currentWidthFactor * 160.0F));
      this.widthAnimator.setStartDelay(paramLong);
      this.widthAnimator.start();
      this.widthAnimationIsGrowing = false;
    }
    
    public float draw(Canvas paramCanvas, float paramFloat1, int paramInt, float paramFloat2, float paramFloat3)
    {
      int i;
      if (this.currentTextSizeFactor > 0.0F)
      {
        i = 1;
        if (this.currentDotSizeFactor <= 0.0F) {
          break label206;
        }
      }
      label206:
      for (int j = 1;; j = 0)
      {
        paramFloat3 *= this.currentWidthFactor;
        if (i != 0)
        {
          float f1 = paramInt / 2.0F;
          float f2 = this.currentTextSizeFactor;
          float f3 = paramInt;
          float f4 = this.currentTextTranslationY;
          paramCanvas.save();
          paramCanvas.translate(paramFloat1 + paramFloat3 / 2.0F, f1 * f2 + paramFloat2 + f3 * f4 * 0.8F);
          paramCanvas.scale(this.currentTextSizeFactor, this.currentTextSizeFactor);
          paramCanvas.drawText(Character.toString(this.whichChar), 0.0F, 0.0F, OPPasswordTextViewForPin.-get12(OPPasswordTextViewForPin.this));
          paramCanvas.restore();
        }
        if (j != 0)
        {
          paramCanvas.save();
          paramCanvas.translate(paramFloat1 + paramFloat3 / 2.0F, paramFloat2);
          paramCanvas.drawCircle(0.0F, 0.0F, OPPasswordTextViewForPin.-get5(OPPasswordTextViewForPin.this) / 2 * this.currentDotSizeFactor, OPPasswordTextViewForPin.-get12(OPPasswordTextViewForPin.this));
          paramCanvas.restore();
        }
        return OPPasswordTextViewForPin.-get2(OPPasswordTextViewForPin.this) * this.currentWidthFactor + paramFloat3;
        i = 0;
        break;
      }
    }
    
    public float drawErrorAnimation(Canvas paramCanvas, float paramFloat1, int paramInt, float paramFloat2, float paramFloat3)
    {
      label20:
      int i;
      label32:
      int j;
      label44:
      int k;
      label56:
      float f;
      if (this.currentEmptyCircleSizeFactor > 1.0F)
      {
        if (this.currentDotSizeFactor <= 0.0F) {
          break label348;
        }
        paramInt = 1;
        if (this.currentDotSizeFactor2 <= 0.0F) {
          break label353;
        }
        i = 1;
        if (this.currentDotSizeFactor3 <= 0.0F) {
          break label359;
        }
        j = 1;
        if (this.currentDotSizeFactor4 <= 0.0F) {
          break label365;
        }
        k = 1;
        f = this.currentWidthFactor;
        if (paramInt == 0) {
          break label371;
        }
        paramCanvas.save();
        paramCanvas.translate(paramFloat1, paramFloat2);
        OPPasswordTextViewForPin.-get7(OPPasswordTextViewForPin.this).setAlpha((int)(this.currentDotSizeFactor * 255.0F));
        paramCanvas.drawCircle(0.0F, 0.0F, OPPasswordTextViewForPin.-get5(OPPasswordTextViewForPin.this) * this.currentDotSizeFactor, OPPasswordTextViewForPin.-get7(OPPasswordTextViewForPin.this));
        paramCanvas.restore();
        label127:
        if (i == 0) {
          break label411;
        }
        paramCanvas.save();
        paramCanvas.translate(paramFloat1, paramFloat2);
        OPPasswordTextViewForPin.-get8(OPPasswordTextViewForPin.this).setAlpha((int)(this.currentDotSizeFactor2 * 255.0F));
        paramCanvas.drawCircle(0.0F, 0.0F, OPPasswordTextViewForPin.-get5(OPPasswordTextViewForPin.this) * this.currentDotSizeFactor2, OPPasswordTextViewForPin.-get8(OPPasswordTextViewForPin.this));
        paramCanvas.restore();
        label193:
        if (j == 0) {
          break label451;
        }
        paramCanvas.save();
        paramCanvas.translate(paramFloat1, paramFloat2);
        OPPasswordTextViewForPin.-get9(OPPasswordTextViewForPin.this).setAlpha((int)(this.currentDotSizeFactor3 * 255.0F));
        paramCanvas.drawCircle(0.0F, 0.0F, OPPasswordTextViewForPin.-get5(OPPasswordTextViewForPin.this) * this.currentDotSizeFactor3, OPPasswordTextViewForPin.-get9(OPPasswordTextViewForPin.this));
        paramCanvas.restore();
        label259:
        if (k == 0) {
          break label491;
        }
        paramCanvas.save();
        paramCanvas.translate(paramFloat1, paramFloat2);
        OPPasswordTextViewForPin.-get10(OPPasswordTextViewForPin.this).setAlpha((int)(this.currentDotSizeFactor4 * 255.0F));
        paramCanvas.drawCircle(0.0F, 0.0F, OPPasswordTextViewForPin.-get5(OPPasswordTextViewForPin.this) * this.currentDotSizeFactor4, OPPasswordTextViewForPin.-get10(OPPasswordTextViewForPin.this));
        paramCanvas.restore();
      }
      for (;;)
      {
        return OPPasswordTextViewForPin.-get2(OPPasswordTextViewForPin.this) * this.currentWidthFactor + paramFloat3 * f;
        break;
        label348:
        paramInt = 0;
        break label20;
        label353:
        i = 0;
        break label32;
        label359:
        j = 0;
        break label44;
        label365:
        k = 0;
        break label56;
        label371:
        paramCanvas.save();
        paramCanvas.translate(paramFloat1, paramFloat2);
        paramCanvas.drawCircle(0.0F, 0.0F, OPPasswordTextViewForPin.-get6(OPPasswordTextViewForPin.this), OPPasswordTextViewForPin.-get11(OPPasswordTextViewForPin.this));
        paramCanvas.restore();
        break label127;
        label411:
        paramCanvas.save();
        paramCanvas.translate(paramFloat1, paramFloat2);
        paramCanvas.drawCircle(0.0F, 0.0F, OPPasswordTextViewForPin.-get6(OPPasswordTextViewForPin.this), OPPasswordTextViewForPin.-get11(OPPasswordTextViewForPin.this));
        paramCanvas.restore();
        break label193;
        label451:
        paramCanvas.save();
        paramCanvas.translate(paramFloat1, paramFloat2);
        paramCanvas.drawCircle(0.0F, 0.0F, OPPasswordTextViewForPin.-get6(OPPasswordTextViewForPin.this), OPPasswordTextViewForPin.-get11(OPPasswordTextViewForPin.this));
        paramCanvas.restore();
        break label259;
        label491:
        paramCanvas.save();
        paramCanvas.translate(paramFloat1, paramFloat2);
        paramCanvas.drawCircle(0.0F, 0.0F, OPPasswordTextViewForPin.-get6(OPPasswordTextViewForPin.this), OPPasswordTextViewForPin.-get11(OPPasswordTextViewForPin.this));
        paramCanvas.restore();
      }
    }
    
    void resetState()
    {
      this.whichChar = '\000';
      this.currentTextSizeFactor = 0.0F;
      this.currentDotSizeFactor = 0.0F;
      this.currentDotSizeFactor2 = 0.0F;
      this.currentDotSizeFactor3 = 0.0F;
      this.currentDotSizeFactor4 = 0.0F;
      this.currentWidthFactor = 0.0F;
      cancelAnimator(this.textAnimator);
      this.textAnimator = null;
      cancelAnimator(this.dotAnimator);
      this.dotAnimator = null;
      cancelAnimator(this.widthAnimator);
      this.widthAnimator = null;
      cancelAnimator(this.emptyCircleAnimator);
      this.emptyCircleAnimator = null;
      this.currentEmptyCircleSizeFactor = 1.0F;
      this.currentTextTranslationY = 1.0F;
      removeDotSwapCallbacks();
    }
    
    void startAppearAnimation()
    {
      cancelAnimator(this.dotAnimator);
      cancelAnimator(this.textAnimator);
      int i;
      int j;
      if (!OPPasswordTextViewForPin.-get13(OPPasswordTextViewForPin.this)) {
        if ((this.dotAnimator != null) && (this.dotAnimationIsGrowing))
        {
          i = 0;
          if (!OPPasswordTextViewForPin.-get13(OPPasswordTextViewForPin.this)) {
            break label142;
          }
          if ((this.textAnimator == null) || (!this.textAnimationIsGrowing)) {
            break label137;
          }
          j = 0;
          label68:
          if ((this.widthAnimator == null) || (!this.widthAnimationIsGrowing)) {
            break label147;
          }
        }
      }
      label137:
      label142:
      label147:
      for (int k = 0;; k = 1)
      {
        if (i != 0) {
          startDotAppearAnimation(0L);
        }
        if (j != 0) {
          startTextAppearAnimation();
        }
        if (k != 0) {
          startWidthAppearAnimation();
        }
        if (OPPasswordTextViewForPin.-get13(OPPasswordTextViewForPin.this)) {
          postDotSwap(250L);
        }
        return;
        i = 1;
        break;
        i = 0;
        break;
        j = 1;
        break label68;
        j = 0;
        break label68;
      }
    }
    
    void startRemoveAnimation(long paramLong1, long paramLong2)
    {
      boolean bool1;
      boolean bool2;
      label38:
      boolean bool3;
      if ((this.currentDotSizeFactor > 0.0F) && (this.dotAnimator == null))
      {
        bool1 = true;
        if ((this.currentTextSizeFactor <= 0.0F) || (this.textAnimator != null)) {
          break label117;
        }
        bool2 = true;
        if ((this.currentWidthFactor <= 0.0F) || (this.widthAnimator != null)) {
          break label139;
        }
        bool3 = true;
      }
      for (;;)
      {
        if (bool1) {
          startDotDisappearAnimation(paramLong1);
        }
        if (bool2) {
          startTextDisappearAnimation(paramLong1);
        }
        startEmptyCircleAppearAnimation(264L);
        if (bool3) {
          startWidthDisappearAnimation(paramLong2);
        }
        return;
        if (this.dotAnimator != null)
        {
          bool1 = this.dotAnimationIsGrowing;
          break;
        }
        bool1 = false;
        break;
        label117:
        if (this.textAnimator != null)
        {
          bool2 = this.textAnimationIsGrowing;
          break label38;
        }
        bool2 = false;
        break label38;
        label139:
        if (this.widthAnimator != null) {
          bool3 = this.widthAnimationIsGrowing;
        } else {
          bool3 = false;
        }
      }
    }
    
    void swapToDotWhenAppearFinished()
    {
      removeDotSwapCallbacks();
      if (this.textAnimator != null)
      {
        postDotSwap(100L + (this.textAnimator.getDuration() - this.textAnimator.getCurrentPlayTime()));
        return;
      }
      performSwap();
    }
  }
  
  public static abstract interface OnTextEmptyListerner
  {
    public abstract void onTextChanged(String paramString);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\password\OPPasswordTextViewForPin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */