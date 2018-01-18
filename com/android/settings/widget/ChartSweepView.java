package com.android.settings.widget;

import android.content.Context;
import android.content.res.CompatibilityInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.MathUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewParent;
import com.android.internal.util.Preconditions;
import com.android.settings.R.styleable;

public class ChartSweepView
  extends View
{
  private static final boolean DRAW_OUTLINE = false;
  public static final int HORIZONTAL = 0;
  private static final int LARGE_WIDTH = 1024;
  private static final int MODE_DRAG = 1;
  private static final int MODE_LABEL = 2;
  private static final int MODE_NONE = 0;
  public static final int VERTICAL = 1;
  private ChartAxis mAxis;
  private View.OnClickListener mClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      ChartSweepView.-wrap0(ChartSweepView.this);
    }
  };
  private Rect mContentOffset = new Rect();
  private long mDragInterval = 1L;
  private int mFollowAxis;
  private int mLabelColor;
  private DynamicLayout mLabelLayout;
  private int mLabelMinSize;
  private float mLabelOffset;
  private float mLabelSize;
  private SpannableStringBuilder mLabelTemplate;
  private int mLabelTemplateRes;
  private long mLabelValue;
  private OnSweepListener mListener;
  private Rect mMargins = new Rect();
  private float mNeighborMargin;
  private ChartSweepView[] mNeighbors = new ChartSweepView[0];
  private Paint mOutlinePaint = new Paint();
  private int mSafeRegion;
  private Drawable mSweep;
  private Point mSweepOffset = new Point();
  private Rect mSweepPadding = new Rect();
  private int mTouchMode = 0;
  private MotionEvent mTracking;
  private float mTrackingStart;
  private long mValidAfter;
  private ChartSweepView mValidAfterDynamic;
  private long mValidBefore;
  private ChartSweepView mValidBeforeDynamic;
  private long mValue;
  
  public ChartSweepView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ChartSweepView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ChartSweepView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ChartSweepView, paramInt, 0);
    paramInt = paramContext.getColor(5, -16776961);
    setSweepDrawable(paramContext.getDrawable(0), paramInt);
    setFollowAxis(paramContext.getInt(1, -1));
    setNeighborMargin(paramContext.getDimensionPixelSize(2, 0));
    setSafeRegion(paramContext.getDimensionPixelSize(6, 0));
    setLabelMinSize(paramContext.getDimensionPixelSize(3, 0));
    setLabelTemplate(paramContext.getResourceId(4, 0));
    setLabelColor(paramInt);
    setBackgroundResource(2130837903);
    this.mOutlinePaint.setColor(-65536);
    this.mOutlinePaint.setStrokeWidth(1.0F);
    this.mOutlinePaint.setStyle(Paint.Style.STROKE);
    paramContext.recycle();
    setClickable(true);
    setOnClickListener(this.mClickListener);
    setWillNotDraw(false);
  }
  
  private Rect buildClampRect(Rect paramRect, long paramLong1, long paramLong2, float paramFloat)
  {
    long l2 = paramLong1;
    long l1 = paramLong2;
    if ((this.mAxis instanceof InvertedChartAxis))
    {
      l1 = paramLong1;
      l2 = paramLong2;
    }
    int i;
    int j;
    label66:
    float f;
    if ((l2 != Long.MIN_VALUE) && (l2 != Long.MAX_VALUE))
    {
      i = 1;
      if ((l1 == Long.MIN_VALUE) || (l1 == Long.MAX_VALUE)) {
        break label158;
      }
      j = 1;
      f = this.mAxis.convertToPoint(l2) + paramFloat;
      paramFloat = this.mAxis.convertToPoint(l1) - paramFloat;
      paramRect = new Rect(paramRect);
      if (this.mFollowAxis != 1) {
        break label164;
      }
      if (j != 0) {
        paramRect.bottom = (paramRect.top + (int)paramFloat);
      }
      if (i != 0) {
        paramRect.top = ((int)(paramRect.top + f));
      }
    }
    label158:
    label164:
    do
    {
      return paramRect;
      i = 0;
      break;
      j = 0;
      break label66;
      if (j != 0) {
        paramRect.right = (paramRect.left + (int)paramFloat);
      }
    } while (i == 0);
    paramRect.left = ((int)(paramRect.left + f));
    return paramRect;
  }
  
  private Rect computeClampRect(Rect paramRect)
  {
    Rect localRect = buildClampRect(paramRect, this.mValidAfter, this.mValidBefore, 0.0F);
    if (!localRect.intersect(buildClampRect(paramRect, getValidAfterDynamic(), getValidBeforeDynamic(), this.mNeighborMargin))) {
      localRect.setEmpty();
    }
    return localRect;
  }
  
  private void dispatchOnSweep(boolean paramBoolean)
  {
    if (this.mListener != null) {
      this.mListener.onSweep(this, paramBoolean);
    }
  }
  
  private void dispatchRequestEdit()
  {
    if (this.mListener != null) {
      this.mListener.requestEdit(this);
    }
  }
  
  public static float getLabelBottom(ChartSweepView paramChartSweepView)
  {
    return getLabelTop(paramChartSweepView) + paramChartSweepView.mLabelLayout.getHeight();
  }
  
  public static float getLabelTop(ChartSweepView paramChartSweepView)
  {
    return paramChartSweepView.getY() + paramChartSweepView.mContentOffset.top;
  }
  
  public static float getLabelWidth(ChartSweepView paramChartSweepView)
  {
    return Layout.getDesiredWidth(paramChartSweepView.mLabelLayout.getText(), paramChartSweepView.mLabelLayout.getPaint());
  }
  
  private Rect getParentContentRect()
  {
    View localView = (View)getParent();
    return new Rect(localView.getPaddingLeft(), localView.getPaddingTop(), localView.getWidth() - localView.getPaddingRight(), localView.getHeight() - localView.getPaddingBottom());
  }
  
  private float getTargetInset()
  {
    if (this.mFollowAxis == 1)
    {
      f = this.mSweep.getIntrinsicHeight() - this.mSweepPadding.top - this.mSweepPadding.bottom;
      return this.mSweepPadding.top + f / 2.0F + this.mSweepOffset.y;
    }
    float f = this.mSweep.getIntrinsicWidth() - this.mSweepPadding.left - this.mSweepPadding.right;
    return this.mSweepPadding.left + f / 2.0F + this.mSweepOffset.x;
  }
  
  private float getTouchDistanceFromTarget(MotionEvent paramMotionEvent)
  {
    if (this.mFollowAxis == 0) {
      return Math.abs(paramMotionEvent.getX() - (getX() + getTargetInset()));
    }
    return Math.abs(paramMotionEvent.getY() - (getY() + getTargetInset()));
  }
  
  private long getValidAfterDynamic()
  {
    ChartSweepView localChartSweepView = this.mValidAfterDynamic;
    if ((localChartSweepView != null) && (localChartSweepView.isEnabled())) {
      return localChartSweepView.getValue();
    }
    return Long.MIN_VALUE;
  }
  
  private long getValidBeforeDynamic()
  {
    ChartSweepView localChartSweepView = this.mValidBeforeDynamic;
    if ((localChartSweepView != null) && (localChartSweepView.isEnabled())) {
      return localChartSweepView.getValue();
    }
    return Long.MAX_VALUE;
  }
  
  private void invalidateLabel()
  {
    if ((this.mLabelTemplate != null) && (this.mAxis != null))
    {
      this.mLabelValue = this.mAxis.buildLabel(getResources(), this.mLabelTemplate, this.mValue);
      setContentDescription(this.mLabelTemplate);
      invalidateLabelOffset();
      invalidate();
      return;
    }
    this.mLabelValue = this.mValue;
  }
  
  private void invalidateLabelTemplate()
  {
    if (this.mLabelTemplateRes != 0)
    {
      CharSequence localCharSequence = getResources().getText(this.mLabelTemplateRes);
      TextPaint localTextPaint = new TextPaint(1);
      localTextPaint.density = getResources().getDisplayMetrics().density;
      localTextPaint.setCompatibilityScaling(getResources().getCompatibilityInfo().applicationScale);
      localTextPaint.setColor(this.mLabelColor);
      this.mLabelTemplate = new SpannableStringBuilder(localCharSequence);
      this.mLabelLayout = new DynamicLayout(this.mLabelTemplate, localTextPaint, 1024, Layout.Alignment.ALIGN_RIGHT, 1.0F, 0.0F, false);
      invalidateLabel();
    }
    for (;;)
    {
      invalidate();
      requestLayout();
      return;
      this.mLabelTemplate = null;
      this.mLabelLayout = null;
    }
  }
  
  public void addOnLayoutChangeListener(View.OnLayoutChangeListener paramOnLayoutChangeListener) {}
  
  public void addOnSweepListener(OnSweepListener paramOnSweepListener)
  {
    this.mListener = paramOnSweepListener;
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    if (this.mSweep.isStateful()) {
      this.mSweep.setState(getDrawableState());
    }
  }
  
  public ChartAxis getAxis()
  {
    return this.mAxis;
  }
  
  public int getFollowAxis()
  {
    return this.mFollowAxis;
  }
  
  public long getLabelValue()
  {
    return this.mLabelValue;
  }
  
  public Rect getMargins()
  {
    return this.mMargins;
  }
  
  public float getPoint()
  {
    if (isEnabled()) {
      return this.mAxis.convertToPoint(this.mValue);
    }
    return 0.0F;
  }
  
  public long getValue()
  {
    return this.mValue;
  }
  
  void init(ChartAxis paramChartAxis)
  {
    this.mAxis = ((ChartAxis)Preconditions.checkNotNull(paramChartAxis, "missing axis"));
  }
  
  public void invalidateLabelOffset()
  {
    float f2 = 0.0F;
    float f1 = f2;
    float f3;
    if (this.mFollowAxis == 1)
    {
      if (this.mValidAfterDynamic == null) {
        break label125;
      }
      this.mLabelSize = Math.max(getLabelWidth(this), getLabelWidth(this.mValidAfterDynamic));
      f3 = getLabelTop(this.mValidAfterDynamic) - getLabelBottom(this);
      f1 = f2;
      if (f3 < 0.0F) {
        f1 = f3 / 2.0F;
      }
    }
    for (;;)
    {
      this.mLabelSize = Math.max(this.mLabelSize, this.mLabelMinSize);
      if (f1 != this.mLabelOffset)
      {
        this.mLabelOffset = f1;
        invalidate();
        if (this.mValidAfterDynamic != null) {
          this.mValidAfterDynamic.invalidateLabelOffset();
        }
        if (this.mValidBeforeDynamic != null) {
          this.mValidBeforeDynamic.invalidateLabelOffset();
        }
      }
      return;
      label125:
      if (this.mValidBeforeDynamic != null)
      {
        this.mLabelSize = Math.max(getLabelWidth(this), getLabelWidth(this.mValidBeforeDynamic));
        f3 = getLabelTop(this) - getLabelBottom(this.mValidBeforeDynamic);
        f1 = f2;
        if (f3 < 0.0F) {
          f1 = -f3 / 2.0F;
        }
      }
      else
      {
        this.mLabelSize = getLabelWidth(this);
        f1 = f2;
      }
    }
  }
  
  public boolean isTouchCloserTo(MotionEvent paramMotionEvent, ChartSweepView paramChartSweepView)
  {
    float f = getTouchDistanceFromTarget(paramMotionEvent);
    return paramChartSweepView.getTouchDistanceFromTarget(paramMotionEvent) < f;
  }
  
  public void jumpDrawablesToCurrentState()
  {
    super.jumpDrawablesToCurrentState();
    if (this.mSweep != null) {
      this.mSweep.jumpToCurrentState();
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    int j = getWidth();
    int k = getHeight();
    int i;
    if ((isEnabled()) && (this.mLabelLayout != null))
    {
      i = paramCanvas.save();
      float f = this.mLabelSize;
      paramCanvas.translate(this.mContentOffset.left + (f - 1024.0F), this.mContentOffset.top + this.mLabelOffset);
      this.mLabelLayout.draw(paramCanvas);
      paramCanvas.restoreToCount(i);
      i = (int)this.mLabelSize + this.mSafeRegion;
      if (this.mFollowAxis != 1) {
        break label158;
      }
      this.mSweep.setBounds(i, this.mSweepOffset.y, this.mContentOffset.right + j, this.mSweepOffset.y + this.mSweep.getIntrinsicHeight());
    }
    for (;;)
    {
      this.mSweep.draw(paramCanvas);
      return;
      i = 0;
      break;
      label158:
      this.mSweep.setBounds(this.mSweepOffset.x, i, this.mSweepOffset.x + this.mSweep.getIntrinsicWidth(), this.mContentOffset.bottom + k);
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    invalidateLabelOffset();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i;
    label171:
    Rect localRect;
    if ((isEnabled()) && (this.mLabelLayout != null))
    {
      paramInt1 = this.mSweep.getIntrinsicHeight();
      paramInt2 = this.mLabelLayout.getHeight();
      this.mSweepOffset.x = 0;
      this.mSweepOffset.y = 0;
      this.mSweepOffset.y = ((int)(paramInt2 / 2 - getTargetInset()));
      setMeasuredDimension(this.mSweep.getIntrinsicWidth(), Math.max(paramInt1, paramInt2));
      if (this.mFollowAxis != 1) {
        break label349;
      }
      paramInt1 = this.mSweep.getIntrinsicHeight();
      paramInt2 = this.mSweepPadding.top;
      i = this.mSweepPadding.bottom;
      this.mMargins.top = (-(this.mSweepPadding.top + (paramInt1 - paramInt2 - i) / 2));
      this.mMargins.bottom = 0;
      this.mMargins.left = (-this.mSweepPadding.left);
      this.mMargins.right = this.mSweepPadding.right;
      this.mContentOffset.set(0, 0, 0, 0);
      paramInt1 = getMeasuredWidth();
      paramInt2 = getMeasuredHeight();
      if (this.mFollowAxis != 0) {
        break label436;
      }
      i = paramInt1 * 3;
      setMeasuredDimension(i, paramInt2);
      this.mContentOffset.left = ((i - paramInt1) / 2);
      paramInt1 = this.mSweepPadding.bottom * 2;
      localRect = this.mContentOffset;
      localRect.bottom -= paramInt1;
      localRect = this.mMargins;
      localRect.bottom += paramInt1;
    }
    for (;;)
    {
      this.mSweepOffset.offset(this.mContentOffset.left, this.mContentOffset.top);
      this.mMargins.offset(-this.mSweepOffset.x, -this.mSweepOffset.y);
      return;
      this.mSweepOffset.x = 0;
      this.mSweepOffset.y = 0;
      setMeasuredDimension(this.mSweep.getIntrinsicWidth(), this.mSweep.getIntrinsicHeight());
      break;
      label349:
      paramInt1 = this.mSweep.getIntrinsicWidth();
      paramInt2 = this.mSweepPadding.left;
      i = this.mSweepPadding.right;
      this.mMargins.left = (-(this.mSweepPadding.left + (paramInt1 - paramInt2 - i) / 2));
      this.mMargins.right = 0;
      this.mMargins.top = (-this.mSweepPadding.top);
      this.mMargins.bottom = this.mSweepPadding.bottom;
      break label171;
      label436:
      i = paramInt2 * 2;
      setMeasuredDimension(paramInt1, i);
      this.mContentOffset.offset(0, (i - paramInt2) / 2);
      paramInt1 = this.mSweepPadding.right * 2;
      localRect = this.mContentOffset;
      localRect.right -= paramInt1;
      localRect = this.mMargins;
      localRect.right += paramInt1;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!isEnabled()) {
      return false;
    }
    Object localObject1 = (View)getParent();
    label111:
    Object localObject2;
    switch (paramMotionEvent.getAction())
    {
    default: 
      return false;
    case 0: 
      int j;
      int i;
      ChartSweepView[] arrayOfChartSweepView;
      int k;
      int m;
      if (this.mFollowAxis == 1) {
        if (paramMotionEvent.getX() > getWidth() - this.mSweepPadding.right * 8)
        {
          j = 1;
          if (this.mLabelLayout == null) {
            break label181;
          }
          if (paramMotionEvent.getX() >= this.mLabelLayout.getWidth()) {
            break label175;
          }
          i = 1;
          localObject2 = paramMotionEvent.copy();
          ((MotionEvent)localObject2).offsetLocation(getLeft(), getTop());
          arrayOfChartSweepView = this.mNeighbors;
          k = 0;
          m = arrayOfChartSweepView.length;
        }
      }
      for (;;)
      {
        if (k >= m) {
          break label294;
        }
        if (isTouchCloserTo((MotionEvent)localObject2, arrayOfChartSweepView[k]))
        {
          return false;
          j = 0;
          break;
          i = 0;
          break label111;
          i = 0;
          break label111;
          if (paramMotionEvent.getY() > getHeight() - this.mSweepPadding.bottom * 8) {
            i = 1;
          }
          for (;;)
          {
            if (this.mLabelLayout != null)
            {
              if (paramMotionEvent.getY() < this.mLabelLayout.getHeight())
              {
                k = 1;
                j = i;
                i = k;
                break;
                i = 0;
                continue;
              }
              k = 0;
              j = i;
              i = k;
              break;
            }
          }
          k = 0;
          j = i;
          i = k;
          break label111;
        }
        k += 1;
      }
      if (j != 0)
      {
        if (this.mFollowAxis == 1) {}
        for (this.mTrackingStart = (getTop() - this.mMargins.top);; this.mTrackingStart = (getLeft() - this.mMargins.left))
        {
          this.mTracking = paramMotionEvent.copy();
          this.mTouchMode = 1;
          if (!((View)localObject1).isActivated()) {
            ((View)localObject1).setActivated(true);
          }
          return true;
        }
      }
      if (i != 0)
      {
        this.mTouchMode = 2;
        return true;
      }
      this.mTouchMode = 0;
      return false;
    case 2: 
      label175:
      label181:
      label294:
      if (this.mTouchMode == 2) {
        return true;
      }
      getParent().requestDisallowInterceptTouchEvent(true);
      localObject1 = getParentContentRect();
      localObject2 = computeClampRect((Rect)localObject1);
      if (((Rect)localObject2).isEmpty()) {
        return true;
      }
      float f1;
      float f2;
      if (this.mFollowAxis == 1)
      {
        f1 = getTop() - this.mMargins.top;
        f2 = MathUtils.constrain(this.mTrackingStart + (paramMotionEvent.getRawY() - this.mTracking.getRawY()), ((Rect)localObject2).top, ((Rect)localObject2).bottom);
        setTranslationY(f2 - f1);
      }
      for (long l = this.mAxis.convertToValue(f2 - ((Rect)localObject1).top);; l = this.mAxis.convertToValue(f2 - ((Rect)localObject1).left))
      {
        setValue(l - l % this.mDragInterval);
        dispatchOnSweep(false);
        return true;
        f1 = getLeft() - this.mMargins.left;
        f2 = MathUtils.constrain(this.mTrackingStart + (paramMotionEvent.getRawX() - this.mTracking.getRawX()), ((Rect)localObject2).left, ((Rect)localObject2).right);
        setTranslationX(f2 - f1);
      }
    }
    if (this.mTouchMode == 2) {
      performClick();
    }
    for (;;)
    {
      this.mTouchMode = 0;
      return true;
      if (this.mTouchMode == 1)
      {
        this.mTrackingStart = 0.0F;
        this.mTracking = null;
        this.mValue = this.mLabelValue;
        dispatchOnSweep(true);
        setTranslationX(0.0F);
        setTranslationY(0.0F);
        requestLayout();
      }
    }
  }
  
  public void removeOnLayoutChangeListener(View.OnLayoutChangeListener paramOnLayoutChangeListener) {}
  
  public void setDragInterval(long paramLong)
  {
    this.mDragInterval = paramLong;
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    super.setEnabled(paramBoolean);
    setFocusable(paramBoolean);
    requestLayout();
  }
  
  public void setFollowAxis(int paramInt)
  {
    this.mFollowAxis = paramInt;
  }
  
  public void setLabelColor(int paramInt)
  {
    this.mLabelColor = paramInt;
    invalidateLabelTemplate();
  }
  
  public void setLabelMinSize(int paramInt)
  {
    this.mLabelMinSize = paramInt;
    invalidateLabelTemplate();
  }
  
  public void setLabelTemplate(int paramInt)
  {
    this.mLabelTemplateRes = paramInt;
    invalidateLabelTemplate();
  }
  
  public void setNeighborMargin(float paramFloat)
  {
    this.mNeighborMargin = paramFloat;
  }
  
  public void setNeighbors(ChartSweepView... paramVarArgs)
  {
    this.mNeighbors = paramVarArgs;
  }
  
  public void setSafeRegion(int paramInt)
  {
    this.mSafeRegion = paramInt;
  }
  
  public void setSweepDrawable(Drawable paramDrawable, int paramInt)
  {
    if (this.mSweep != null)
    {
      this.mSweep.setCallback(null);
      unscheduleDrawable(this.mSweep);
    }
    boolean bool;
    if (paramDrawable != null)
    {
      paramDrawable.setCallback(this);
      if (paramDrawable.isStateful()) {
        paramDrawable.setState(getDrawableState());
      }
      if (getVisibility() == 0)
      {
        bool = true;
        paramDrawable.setVisible(bool, false);
        this.mSweep = paramDrawable;
        this.mSweep.setTint(paramInt);
        paramDrawable.getPadding(this.mSweepPadding);
      }
    }
    for (;;)
    {
      invalidate();
      return;
      bool = false;
      break;
      this.mSweep = null;
    }
  }
  
  public void setValidRange(long paramLong1, long paramLong2)
  {
    this.mValidAfter = paramLong1;
    this.mValidBefore = paramLong2;
  }
  
  public void setValidRangeDynamic(ChartSweepView paramChartSweepView1, ChartSweepView paramChartSweepView2)
  {
    this.mValidAfterDynamic = paramChartSweepView1;
    this.mValidBeforeDynamic = paramChartSweepView2;
  }
  
  public void setValue(long paramLong)
  {
    this.mValue = paramLong;
    invalidateLabel();
  }
  
  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    Drawable localDrawable;
    if (this.mSweep != null)
    {
      localDrawable = this.mSweep;
      if (paramInt != 0) {
        break label31;
      }
    }
    label31:
    for (boolean bool = true;; bool = false)
    {
      localDrawable.setVisible(bool, false);
      return;
    }
  }
  
  public int shouldAdjustAxis()
  {
    return this.mAxis.shouldAdjustAxis(getValue());
  }
  
  public void updateValueFromPosition()
  {
    Rect localRect = getParentContentRect();
    if (this.mFollowAxis == 1)
    {
      f1 = getY();
      f2 = this.mMargins.top;
      f3 = localRect.top;
      setValue(this.mAxis.convertToValue(f1 - f2 - f3));
      return;
    }
    float f1 = getX();
    float f2 = this.mMargins.left;
    float f3 = localRect.left;
    setValue(this.mAxis.convertToValue(f1 - f2 - f3));
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    if (paramDrawable != this.mSweep) {
      return super.verifyDrawable(paramDrawable);
    }
    return true;
  }
  
  public static abstract interface OnSweepListener
  {
    public abstract void onSweep(ChartSweepView paramChartSweepView, boolean paramBoolean);
    
    public abstract void requestEdit(ChartSweepView paramChartSweepView);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\widget\ChartSweepView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */