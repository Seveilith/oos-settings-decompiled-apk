package com.oneplus.lib.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.oneplus.commonctrl.R.dimen;
import com.oneplus.commonctrl.R.styleable;
import com.oneplus.lib.util.DrawableUtils;

public abstract class OPAbsSeekBar
  extends OPProgressBar
{
  private static final int NO_ALPHA = 255;
  private float mDisabledAlpha;
  private boolean mHasThumbTint = false;
  private boolean mHasThumbTintMode = false;
  private boolean mIsDragging;
  boolean mIsUserSeekable = true;
  private int mKeyProgressIncrement = 1;
  private int mScaledTouchSlop;
  private boolean mSplitTrack;
  private final Rect mTempRect = new Rect();
  private Drawable mThumb;
  private int mThumbOffset;
  private ColorStateList mThumbTintList = null;
  private PorterDuff.Mode mThumbTintMode = null;
  private float mTouchDownX;
  float mTouchProgressOffset;
  
  public OPAbsSeekBar(Context paramContext)
  {
    super(paramContext);
  }
  
  public OPAbsSeekBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public OPAbsSeekBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public OPAbsSeekBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.OPSeekBar, paramInt1, paramInt2);
    setThumb(paramAttributeSet.getDrawable(R.styleable.OPSeekBar_android_thumb));
    if (paramAttributeSet.hasValue(R.styleable.OPSeekBar_android_thumbTintMode))
    {
      this.mThumbTintMode = DrawableUtils.parseTintMode(paramAttributeSet.getInt(R.styleable.OPSeekBar_android_thumbTintMode, -1), this.mThumbTintMode);
      this.mHasThumbTintMode = true;
    }
    if (paramAttributeSet.hasValue(R.styleable.OPSeekBar_android_thumbTint))
    {
      this.mThumbTintList = paramAttributeSet.getColorStateList(R.styleable.OPSeekBar_android_thumbTint);
      this.mHasThumbTint = true;
    }
    this.mSplitTrack = paramAttributeSet.getBoolean(R.styleable.OPSeekBar_android_splitTrack, false);
    setThumbOffset(paramAttributeSet.getDimensionPixelOffset(R.styleable.OPSeekBar_android_thumbOffset, getThumbOffset()));
    paramAttributeSet.getBoolean(R.styleable.OPSeekBar_useDisabledAlpha, true);
    this.mDisabledAlpha = 1.0F;
    paramAttributeSet.recycle();
    applyThumbTint();
    this.mScaledTouchSlop = ViewConfiguration.get(paramContext).getScaledTouchSlop();
  }
  
  private void applyThumbTint()
  {
    if ((this.mThumb != null) && ((this.mHasThumbTint) || (this.mHasThumbTintMode)))
    {
      this.mThumb = this.mThumb.mutate();
      if (this.mHasThumbTint) {
        this.mThumb.setTintList(this.mThumbTintList);
      }
      if (this.mHasThumbTintMode) {
        this.mThumb.setTintMode(this.mThumbTintMode);
      }
      if (this.mThumb.isStateful()) {
        this.mThumb.setState(getDrawableState());
      }
    }
  }
  
  private void attemptClaimDrag()
  {
    if (getParent() != null) {
      getParent().requestDisallowInterceptTouchEvent(true);
    }
  }
  
  private float getScale()
  {
    int i = getMax();
    if (i > 0) {
      return getProgress() / i;
    }
    return 0.0F;
  }
  
  private void setHotspot(float paramFloat1, float paramFloat2)
  {
    Drawable localDrawable = getBackground();
    if (localDrawable != null) {
      localDrawable.setHotspot(paramFloat1, paramFloat2);
    }
  }
  
  private void setThumbPos(int paramInt1, Drawable paramDrawable, float paramFloat, int paramInt2)
  {
    int i = this.mPaddingLeft;
    int j = this.mPaddingRight;
    int k = paramDrawable.getIntrinsicWidth();
    int n = paramDrawable.getIntrinsicHeight();
    int m = paramInt1 - i - j - k + this.mThumbOffset * 2;
    j = (int)(m * paramFloat + 0.5F);
    Object localObject;
    if (paramInt2 == Integer.MIN_VALUE)
    {
      localObject = paramDrawable.getBounds();
      paramInt2 = ((Rect)localObject).top;
      paramInt1 = ((Rect)localObject).bottom;
      if ((!isLayoutRtl()) || (!this.mMirrorForRtl)) {
        break label189;
      }
    }
    label189:
    for (i = m - j;; i = j)
    {
      j = i + k;
      localObject = getBackground();
      if (localObject != null)
      {
        k = this.mPaddingLeft - this.mThumbOffset;
        m = this.mPaddingTop;
        ((Drawable)localObject).setHotspotBounds(i + k, paramInt2 + m, j + k, paramInt1 + m);
      }
      paramDrawable.setBounds(i, paramInt2, j, paramInt1);
      return;
      i = paramInt2;
      paramInt1 = paramInt2 + n;
      paramInt2 = i;
      break;
    }
  }
  
  private void trackTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = getWidth();
    int j = i - this.mPaddingLeft - this.mPaddingRight;
    int k = (int)paramMotionEvent.getX();
    float f2 = 0.0F;
    float f1;
    if ((isLayoutRtl()) && (this.mMirrorForRtl)) {
      if (k > i - this.mPaddingRight) {
        f1 = 0.0F;
      }
    }
    for (;;)
    {
      float f3 = getMax();
      setHotspot(k, (int)paramMotionEvent.getY());
      setProgress((int)(f2 + f3 * f1), true);
      return;
      if (k < this.mPaddingLeft)
      {
        f1 = 1.0F;
      }
      else
      {
        f1 = (j - k + this.mPaddingLeft) / j;
        f2 = this.mTouchProgressOffset;
        continue;
        if (k < this.mPaddingLeft)
        {
          f1 = 0.0F;
        }
        else if (k > i - this.mPaddingRight)
        {
          f1 = 1.0F;
        }
        else
        {
          f1 = (k - this.mPaddingLeft) / j;
          f2 = this.mTouchProgressOffset;
        }
      }
    }
  }
  
  private void updateThumbAndTrackPos(int paramInt1, int paramInt2)
  {
    int i = paramInt2 - this.mPaddingTop - this.mPaddingBottom;
    Drawable localDrawable1 = getCurrentDrawable();
    Drawable localDrawable2 = this.mThumb;
    int j = Math.min(this.mMaxHeight, i);
    int k;
    if (localDrawable2 == null)
    {
      paramInt2 = 0;
      if (paramInt2 <= j) {
        break label123;
      }
      k = (i - paramInt2) / 2;
      i = k + (paramInt2 - j) / 2;
    }
    for (paramInt2 = k + 0;; paramInt2 = k + (j - paramInt2) / 2)
    {
      if (localDrawable1 != null) {
        localDrawable1.setBounds(0, i, paramInt1 - this.mPaddingRight - this.mPaddingLeft, i + j);
      }
      if (localDrawable2 != null) {
        setThumbPos(paramInt1, localDrawable2, getScale(), paramInt2);
      }
      return;
      paramInt2 = localDrawable2.getIntrinsicHeight();
      break;
      label123:
      k = (i - j) / 2;
      i = k + 0;
    }
  }
  
  void drawThumb(Canvas paramCanvas)
  {
    if (this.mThumb != null)
    {
      paramCanvas.save();
      paramCanvas.translate(this.mPaddingLeft - this.mThumbOffset, this.mPaddingTop);
      this.mThumb.draw(paramCanvas);
      paramCanvas.restore();
    }
  }
  
  void drawTrack(Canvas paramCanvas)
  {
    Drawable localDrawable = this.mThumb;
    if ((localDrawable != null) && (this.mSplitTrack))
    {
      Rect localRect = this.mTempRect;
      int i = (int)getResources().getDimension(R.dimen.seekbar_thumb_optical_inset);
      int j = (int)getResources().getDimension(R.dimen.seekbar_thumb_optical_inset_disabled);
      localDrawable.copyBounds(localRect);
      localRect.offset(this.mPaddingLeft - this.mThumbOffset, this.mPaddingTop);
      int m = localRect.left;
      int k;
      if (isEnabled())
      {
        k = i;
        localRect.left = (k + m);
        k = localRect.right;
        if (!isEnabled()) {
          break label155;
        }
      }
      for (;;)
      {
        localRect.right = (k - i);
        i = paramCanvas.save();
        paramCanvas.clipRect(localRect, Region.Op.DIFFERENCE);
        super.drawTrack(paramCanvas);
        paramCanvas.restoreToCount(i);
        return;
        k = j;
        break;
        label155:
        i = j;
      }
    }
    super.drawTrack(paramCanvas);
  }
  
  public void drawableHotspotChanged(float paramFloat1, float paramFloat2)
  {
    super.drawableHotspotChanged(paramFloat1, paramFloat2);
    if (this.mThumb != null) {
      this.mThumb.setHotspot(paramFloat1, paramFloat2);
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    Drawable localDrawable = getProgressDrawable();
    if ((localDrawable != null) && (this.mDisabledAlpha < 1.0F)) {
      if (!isEnabled()) {
        break label64;
      }
    }
    label64:
    for (int i = 255;; i = (int)(this.mDisabledAlpha * 255.0F))
    {
      localDrawable.setAlpha(i);
      localDrawable = this.mThumb;
      if ((localDrawable != null) && (localDrawable.isStateful())) {
        localDrawable.setState(getDrawableState());
      }
      return;
    }
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return OPAbsSeekBar.class.getName();
  }
  
  public int getKeyProgressIncrement()
  {
    return this.mKeyProgressIncrement;
  }
  
  public boolean getSplitTrack()
  {
    return this.mSplitTrack;
  }
  
  public Drawable getThumb()
  {
    return this.mThumb;
  }
  
  public int getThumbOffset()
  {
    return this.mThumbOffset;
  }
  
  public ColorStateList getThumbTintList()
  {
    return this.mThumbTintList;
  }
  
  public PorterDuff.Mode getThumbTintMode()
  {
    return this.mThumbTintMode;
  }
  
  public boolean isInScrollingContainer()
  {
    for (ViewParent localViewParent = getParent(); (localViewParent != null) && ((localViewParent instanceof ViewGroup)); localViewParent = localViewParent.getParent()) {
      if (((ViewGroup)localViewParent).shouldDelayChildPressedState()) {
        return true;
      }
    }
    return false;
  }
  
  public void jumpDrawablesToCurrentState()
  {
    super.jumpDrawablesToCurrentState();
    if (this.mThumb != null) {
      this.mThumb.jumpToCurrentState();
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    try
    {
      super.onDraw(paramCanvas);
      drawThumb(paramCanvas);
      return;
    }
    finally
    {
      paramCanvas = finally;
      throw paramCanvas;
    }
  }
  
  void onKeyChange() {}
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    int j;
    int i;
    if (isEnabled())
    {
      j = this.mKeyProgressIncrement;
      i = j;
      switch (paramInt)
      {
      }
    }
    do
    {
      return super.onKeyDown(paramInt, paramKeyEvent);
      i = -j;
      j = i;
      if (isLayoutRtl()) {
        j = -i;
      }
    } while (!setProgress(getProgress() + j, true));
    onKeyChange();
    return true;
  }
  
  /* Error */
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokevirtual 279	com/oneplus/lib/widget/OPAbsSeekBar:getCurrentDrawable	()Landroid/graphics/drawable/Drawable;
    //   6: astore 9
    //   8: aload_0
    //   9: getfield 156	com/oneplus/lib/widget/OPAbsSeekBar:mThumb	Landroid/graphics/drawable/Drawable;
    //   12: ifnonnull +118 -> 130
    //   15: iconst_0
    //   16: istore 5
    //   18: iconst_0
    //   19: istore 4
    //   21: iconst_0
    //   22: istore_3
    //   23: aload 9
    //   25: ifnull +49 -> 74
    //   28: aload_0
    //   29: getfield 414	com/oneplus/lib/widget/OPAbsSeekBar:mMinWidth	I
    //   32: aload_0
    //   33: getfield 417	com/oneplus/lib/widget/OPAbsSeekBar:mMaxWidth	I
    //   36: aload 9
    //   38: invokevirtual 219	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   41: invokestatic 287	java/lang/Math:min	(II)I
    //   44: invokestatic 420	java/lang/Math:max	(II)I
    //   47: istore 4
    //   49: iload 5
    //   51: aload_0
    //   52: getfield 423	com/oneplus/lib/widget/OPAbsSeekBar:mMinHeight	I
    //   55: aload_0
    //   56: getfield 282	com/oneplus/lib/widget/OPAbsSeekBar:mMaxHeight	I
    //   59: aload 9
    //   61: invokevirtual 222	android/graphics/drawable/Drawable:getIntrinsicHeight	()I
    //   64: invokestatic 287	java/lang/Math:min	(II)I
    //   67: invokestatic 420	java/lang/Math:max	(II)I
    //   70: invokestatic 420	java/lang/Math:max	(II)I
    //   73: istore_3
    //   74: aload_0
    //   75: getfield 213	com/oneplus/lib/widget/OPAbsSeekBar:mPaddingLeft	I
    //   78: istore 5
    //   80: aload_0
    //   81: getfield 216	com/oneplus/lib/widget/OPAbsSeekBar:mPaddingRight	I
    //   84: istore 6
    //   86: aload_0
    //   87: getfield 245	com/oneplus/lib/widget/OPAbsSeekBar:mPaddingTop	I
    //   90: istore 7
    //   92: aload_0
    //   93: getfield 276	com/oneplus/lib/widget/OPAbsSeekBar:mPaddingBottom	I
    //   96: istore 8
    //   98: aload_0
    //   99: iload 4
    //   101: iload 5
    //   103: iload 6
    //   105: iadd
    //   106: iadd
    //   107: iload_1
    //   108: iconst_0
    //   109: invokestatic 427	com/oneplus/lib/widget/OPAbsSeekBar:resolveSizeAndState	(III)I
    //   112: iload_3
    //   113: iload 7
    //   115: iload 8
    //   117: iadd
    //   118: iadd
    //   119: iload_2
    //   120: iconst_0
    //   121: invokestatic 427	com/oneplus/lib/widget/OPAbsSeekBar:resolveSizeAndState	(III)I
    //   124: invokevirtual 430	com/oneplus/lib/widget/OPAbsSeekBar:setMeasuredDimension	(II)V
    //   127: aload_0
    //   128: monitorexit
    //   129: return
    //   130: aload_0
    //   131: getfield 156	com/oneplus/lib/widget/OPAbsSeekBar:mThumb	Landroid/graphics/drawable/Drawable;
    //   134: invokevirtual 222	android/graphics/drawable/Drawable:getIntrinsicHeight	()I
    //   137: istore 5
    //   139: goto -121 -> 18
    //   142: astore 9
    //   144: aload_0
    //   145: monitorexit
    //   146: aload 9
    //   148: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	149	0	this	OPAbsSeekBar
    //   0	149	1	paramInt1	int
    //   0	149	2	paramInt2	int
    //   22	97	3	i	int
    //   19	88	4	j	int
    //   16	122	5	k	int
    //   84	22	6	m	int
    //   90	28	7	n	int
    //   96	22	8	i1	int
    //   6	54	9	localDrawable	Drawable
    //   142	5	9	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	15	142	finally
    //   28	74	142	finally
    //   74	127	142	finally
    //   130	139	142	finally
  }
  
  void onProgressRefresh(float paramFloat, boolean paramBoolean, int paramInt)
  {
    super.onProgressRefresh(paramFloat, paramBoolean, paramInt);
    Drawable localDrawable = this.mThumb;
    if (localDrawable != null)
    {
      setThumbPos(getWidth(), localDrawable, paramFloat, Integer.MIN_VALUE);
      invalidate();
    }
  }
  
  public void onResolveDrawables(int paramInt)
  {
    super.onResolveDrawables(paramInt);
    if (this.mThumb != null) {
      this.mThumb.setLayoutDirection(paramInt);
    }
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    super.onRtlPropertiesChanged(paramInt);
    Drawable localDrawable = this.mThumb;
    if (localDrawable != null)
    {
      setThumbPos(getWidth(), localDrawable, getScale(), Integer.MIN_VALUE);
      invalidate();
    }
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    updateThumbAndTrackPos(paramInt1, paramInt2);
  }
  
  void onStartTrackingTouch()
  {
    this.mIsDragging = true;
  }
  
  void onStopTrackingTouch()
  {
    this.mIsDragging = false;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((this.mIsUserSeekable) && (isEnabled())) {}
    switch (paramMotionEvent.getAction())
    {
    default: 
    case 0: 
    case 2: 
      do
      {
        return true;
        return false;
        if (isInScrollingContainer())
        {
          this.mTouchDownX = paramMotionEvent.getX();
          return true;
        }
        setPressed(true);
        if (this.mThumb != null) {
          invalidate(this.mThumb.getBounds());
        }
        onStartTrackingTouch();
        trackTouchEvent(paramMotionEvent);
        attemptClaimDrag();
        return true;
        if (this.mIsDragging)
        {
          trackTouchEvent(paramMotionEvent);
          return true;
        }
      } while (Math.abs(paramMotionEvent.getX() - this.mTouchDownX) <= this.mScaledTouchSlop);
      setPressed(true);
      if (this.mThumb != null) {
        invalidate(this.mThumb.getBounds());
      }
      onStartTrackingTouch();
      trackTouchEvent(paramMotionEvent);
      attemptClaimDrag();
      return true;
    case 1: 
      if (this.mIsDragging)
      {
        trackTouchEvent(paramMotionEvent);
        onStopTrackingTouch();
        setPressed(false);
      }
      for (;;)
      {
        invalidate();
        return true;
        onStartTrackingTouch();
        trackTouchEvent(paramMotionEvent);
        onStopTrackingTouch();
      }
    }
    if (this.mIsDragging)
    {
      onStopTrackingTouch();
      setPressed(false);
    }
    invalidate();
    return true;
  }
  
  public void setKeyProgressIncrement(int paramInt)
  {
    int i = paramInt;
    if (paramInt < 0) {
      i = -paramInt;
    }
    this.mKeyProgressIncrement = i;
  }
  
  public void setMax(int paramInt)
  {
    try
    {
      super.setMax(paramInt);
      if ((this.mKeyProgressIncrement == 0) || (getMax() / this.mKeyProgressIncrement > 20)) {
        setKeyProgressIncrement(Math.max(1, Math.round(getMax() / 20.0F)));
      }
      return;
    }
    finally {}
  }
  
  public void setSplitTrack(boolean paramBoolean)
  {
    this.mSplitTrack = paramBoolean;
    invalidate();
  }
  
  public void setThumb(Drawable paramDrawable)
  {
    if ((this.mThumb != null) && (paramDrawable != this.mThumb)) {
      this.mThumb.setCallback(null);
    }
    for (int i = 1;; i = 0)
    {
      if (paramDrawable != null)
      {
        paramDrawable.setCallback(this);
        if (canResolveLayoutDirection()) {
          paramDrawable.setLayoutDirection(getLayoutDirection());
        }
        this.mThumbOffset = (paramDrawable.getIntrinsicWidth() / 2);
        if ((i != 0) && ((paramDrawable.getIntrinsicWidth() != this.mThumb.getIntrinsicWidth()) || (paramDrawable.getIntrinsicHeight() != this.mThumb.getIntrinsicHeight()))) {
          requestLayout();
        }
      }
      this.mThumb = paramDrawable;
      applyThumbTint();
      invalidate();
      if (i != 0)
      {
        updateThumbAndTrackPos(getWidth(), getHeight());
        if ((paramDrawable != null) && (paramDrawable.isStateful())) {
          paramDrawable.setState(getDrawableState());
        }
      }
      return;
    }
  }
  
  public void setThumbOffset(int paramInt)
  {
    this.mThumbOffset = paramInt;
    invalidate();
  }
  
  public void setThumbTintList(ColorStateList paramColorStateList)
  {
    this.mThumbTintList = paramColorStateList;
    this.mHasThumbTint = true;
    applyThumbTint();
  }
  
  public void setThumbTintMode(PorterDuff.Mode paramMode)
  {
    this.mThumbTintMode = paramMode;
    this.mHasThumbTintMode = true;
    applyThumbTint();
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    if (paramDrawable != this.mThumb) {
      return super.verifyDrawable(paramDrawable);
    }
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\OPAbsSeekBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */