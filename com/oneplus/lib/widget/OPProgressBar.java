package com.oneplus.lib.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.ViewDebug.ExportedProperty;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.RemoteViews.RemoteView;
import com.oneplus.commonctrl.R.styleable;
import com.oneplus.lib.util.DrawableUtils;
import com.oneplus.lib.util.MathUtils;
import com.oneplus.lib.util.Pools.SynchronizedPool;
import java.util.ArrayList;

@RemoteViews.RemoteView
public class OPProgressBar
  extends View
{
  private static final int MAX_LEVEL = 10000;
  private static final int TIMEOUT_SEND_ACCESSIBILITY_EVENT = 200;
  private AccessibilityEventSender mAccessibilityEventSender;
  private AlphaAnimation mAnimation;
  private boolean mAttached;
  private int mBehavior;
  private Drawable mCurrentDrawable;
  private int mDuration;
  private boolean mHasAnimation;
  private boolean mInDrawing;
  private boolean mIndeterminate;
  private Drawable mIndeterminateDrawable;
  private Interpolator mInterpolator;
  private int mMax;
  int mMaxHeight;
  int mMaxWidth;
  int mMinHeight;
  int mMinWidth;
  boolean mMirrorForRtl = false;
  private boolean mNoInvalidate;
  private boolean mOnlyIndeterminate;
  protected int mPaddingBottom = getPaddingBottom();
  protected int mPaddingLeft = getPaddingLeft();
  protected int mPaddingRight = getPaddingRight();
  protected int mPaddingTop = getPaddingTop();
  private int mProgress;
  private Drawable mProgressDrawable;
  private ProgressTintInfo mProgressTintInfo;
  private final ArrayList<RefreshData> mRefreshData = new ArrayList();
  private boolean mRefreshIsPosted;
  private RefreshProgressRunnable mRefreshProgressRunnable;
  Bitmap mSampleTile;
  private int mSecondaryProgress;
  private boolean mShouldStartAnimationDrawable;
  private Transformation mTransformation;
  private long mUiThreadId = Thread.currentThread().getId();
  
  public OPProgressBar(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public OPProgressBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842871);
  }
  
  public OPProgressBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public OPProgressBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    initProgressBar();
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.OPProgressBar, paramInt1, paramInt2);
    this.mNoInvalidate = true;
    Drawable localDrawable = paramAttributeSet.getDrawable(R.styleable.OPProgressBar_android_progressDrawable);
    if (localDrawable != null)
    {
      if (needsTileify(localDrawable)) {
        setProgressDrawableTiled(localDrawable);
      }
    }
    else
    {
      this.mDuration = paramAttributeSet.getInt(R.styleable.OPProgressBar_android_indeterminateDuration, this.mDuration);
      this.mMinWidth = paramAttributeSet.getDimensionPixelSize(R.styleable.OPProgressBar_android_minWidth, this.mMinWidth);
      this.mMaxWidth = paramAttributeSet.getDimensionPixelSize(R.styleable.OPProgressBar_android_maxWidth, this.mMaxWidth);
      this.mMinHeight = paramAttributeSet.getDimensionPixelSize(R.styleable.OPProgressBar_android_minHeight, this.mMinHeight);
      this.mMaxHeight = paramAttributeSet.getDimensionPixelSize(R.styleable.OPProgressBar_android_maxHeight, this.mMaxHeight);
      this.mBehavior = paramAttributeSet.getInt(R.styleable.OPProgressBar_android_indeterminateBehavior, this.mBehavior);
      paramInt1 = paramAttributeSet.getResourceId(R.styleable.OPProgressBar_android_interpolator, 17432587);
      if (paramInt1 > 0) {
        setInterpolator(paramContext, paramInt1);
      }
      setMax(paramAttributeSet.getInt(R.styleable.OPProgressBar_android_max, this.mMax));
      setProgress(paramAttributeSet.getInt(R.styleable.OPProgressBar_android_progress, this.mProgress));
      setSecondaryProgress(paramAttributeSet.getInt(R.styleable.OPProgressBar_android_secondaryProgress, this.mSecondaryProgress));
      paramContext = paramAttributeSet.getDrawable(R.styleable.OPProgressBar_android_indeterminateDrawable);
      if (paramContext != null)
      {
        if (!needsTileify(paramContext)) {
          break label818;
        }
        setIndeterminateDrawableTiled(paramContext);
      }
      label295:
      this.mOnlyIndeterminate = paramAttributeSet.getBoolean(R.styleable.OPProgressBar_android_indeterminateOnly, this.mOnlyIndeterminate);
      this.mNoInvalidate = false;
      if (this.mOnlyIndeterminate) {
        break label826;
      }
    }
    label818:
    label826:
    for (boolean bool = paramAttributeSet.getBoolean(R.styleable.OPProgressBar_android_indeterminate, this.mIndeterminate);; bool = true)
    {
      setIndeterminate(bool);
      this.mMirrorForRtl = paramAttributeSet.getBoolean(R.styleable.OPProgressBar_android_mirrorForRtl, this.mMirrorForRtl);
      if (paramAttributeSet.hasValue(R.styleable.OPProgressBar_android_progressTintMode))
      {
        if (this.mProgressTintInfo == null) {
          this.mProgressTintInfo = new ProgressTintInfo(null);
        }
        this.mProgressTintInfo.mProgressTintMode = DrawableUtils.parseTintMode(paramAttributeSet.getInt(R.styleable.OPProgressBar_android_progressTintMode, -1), null);
        this.mProgressTintInfo.mHasProgressTintMode = true;
      }
      if (paramAttributeSet.hasValue(R.styleable.OPProgressBar_android_progressTint))
      {
        if (this.mProgressTintInfo == null) {
          this.mProgressTintInfo = new ProgressTintInfo(null);
        }
        this.mProgressTintInfo.mProgressTintList = paramAttributeSet.getColorStateList(R.styleable.OPProgressBar_android_progressTint);
        this.mProgressTintInfo.mHasProgressTint = true;
      }
      if (paramAttributeSet.hasValue(R.styleable.OPProgressBar_android_progressBackgroundTintMode))
      {
        if (this.mProgressTintInfo == null) {
          this.mProgressTintInfo = new ProgressTintInfo(null);
        }
        this.mProgressTintInfo.mProgressBackgroundTintMode = DrawableUtils.parseTintMode(paramAttributeSet.getInt(R.styleable.OPProgressBar_android_progressBackgroundTintMode, -1), null);
        this.mProgressTintInfo.mHasProgressBackgroundTintMode = true;
      }
      if (paramAttributeSet.hasValue(R.styleable.OPProgressBar_android_progressBackgroundTint))
      {
        if (this.mProgressTintInfo == null) {
          this.mProgressTintInfo = new ProgressTintInfo(null);
        }
        this.mProgressTintInfo.mProgressBackgroundTintList = paramAttributeSet.getColorStateList(R.styleable.OPProgressBar_android_progressBackgroundTint);
        this.mProgressTintInfo.mHasProgressBackgroundTint = true;
      }
      if (paramAttributeSet.hasValue(R.styleable.OPProgressBar_android_secondaryProgressTintMode))
      {
        if (this.mProgressTintInfo == null) {
          this.mProgressTintInfo = new ProgressTintInfo(null);
        }
        this.mProgressTintInfo.mSecondaryProgressTintMode = DrawableUtils.parseTintMode(paramAttributeSet.getInt(R.styleable.OPProgressBar_android_secondaryProgressTintMode, -1), null);
        this.mProgressTintInfo.mHasSecondaryProgressTintMode = true;
      }
      if (paramAttributeSet.hasValue(R.styleable.OPProgressBar_android_secondaryProgressTint))
      {
        if (this.mProgressTintInfo == null) {
          this.mProgressTintInfo = new ProgressTintInfo(null);
        }
        this.mProgressTintInfo.mSecondaryProgressTintList = paramAttributeSet.getColorStateList(R.styleable.OPProgressBar_android_secondaryProgressTint);
        this.mProgressTintInfo.mHasSecondaryProgressTint = true;
      }
      if (paramAttributeSet.hasValue(R.styleable.OPProgressBar_android_indeterminateTintMode))
      {
        if (this.mProgressTintInfo == null) {
          this.mProgressTintInfo = new ProgressTintInfo(null);
        }
        this.mProgressTintInfo.mIndeterminateTintMode = DrawableUtils.parseTintMode(paramAttributeSet.getInt(R.styleable.OPProgressBar_android_indeterminateTintMode, -1), null);
        this.mProgressTintInfo.mHasIndeterminateTintMode = true;
      }
      if (paramAttributeSet.hasValue(R.styleable.OPProgressBar_android_indeterminateTint))
      {
        if (this.mProgressTintInfo == null) {
          this.mProgressTintInfo = new ProgressTintInfo(null);
        }
        this.mProgressTintInfo.mIndeterminateTintList = paramAttributeSet.getColorStateList(R.styleable.OPProgressBar_android_indeterminateTint);
        this.mProgressTintInfo.mHasIndeterminateTint = true;
      }
      paramAttributeSet.recycle();
      applyProgressTints();
      applyIndeterminateTint();
      if (getImportantForAccessibility() == 0) {
        setImportantForAccessibility(1);
      }
      return;
      setProgressDrawable(localDrawable);
      break;
      setIndeterminateDrawable(paramContext);
      break label295;
    }
  }
  
  private void applyIndeterminateTint()
  {
    if ((this.mIndeterminateDrawable != null) && (this.mProgressTintInfo != null))
    {
      ProgressTintInfo localProgressTintInfo = this.mProgressTintInfo;
      if ((localProgressTintInfo.mHasIndeterminateTint) || (localProgressTintInfo.mHasIndeterminateTintMode))
      {
        this.mIndeterminateDrawable = this.mIndeterminateDrawable.mutate();
        if (localProgressTintInfo.mHasIndeterminateTint) {
          this.mIndeterminateDrawable.setTintList(localProgressTintInfo.mIndeterminateTintList);
        }
        if (localProgressTintInfo.mHasIndeterminateTintMode) {
          this.mIndeterminateDrawable.setTintMode(localProgressTintInfo.mIndeterminateTintMode);
        }
        if (this.mIndeterminateDrawable.isStateful()) {
          this.mIndeterminateDrawable.setState(getDrawableState());
        }
      }
    }
  }
  
  private void applyPrimaryProgressTint()
  {
    if ((this.mProgressTintInfo.mHasProgressTint) || (this.mProgressTintInfo.mHasProgressTintMode))
    {
      Drawable localDrawable = getTintTarget(16908301, true);
      if (localDrawable != null)
      {
        if (this.mProgressTintInfo.mHasProgressTint) {
          localDrawable.setTintList(this.mProgressTintInfo.mProgressTintList);
        }
        if (this.mProgressTintInfo.mHasProgressTintMode) {
          localDrawable.setTintMode(this.mProgressTintInfo.mProgressTintMode);
        }
        if (localDrawable.isStateful()) {
          localDrawable.setState(getDrawableState());
        }
      }
    }
  }
  
  private void applyProgressBackgroundTint()
  {
    if ((this.mProgressTintInfo.mHasProgressBackgroundTint) || (this.mProgressTintInfo.mHasProgressBackgroundTintMode))
    {
      Drawable localDrawable = getTintTarget(16908288, false);
      if (localDrawable != null)
      {
        if (this.mProgressTintInfo.mHasProgressBackgroundTint) {
          localDrawable.setTintList(this.mProgressTintInfo.mProgressBackgroundTintList);
        }
        if (this.mProgressTintInfo.mHasProgressBackgroundTintMode) {
          localDrawable.setTintMode(this.mProgressTintInfo.mProgressBackgroundTintMode);
        }
        if (localDrawable.isStateful()) {
          localDrawable.setState(getDrawableState());
        }
      }
    }
  }
  
  private void applyProgressTints()
  {
    if ((this.mProgressDrawable != null) && (this.mProgressTintInfo != null))
    {
      applyPrimaryProgressTint();
      applyProgressBackgroundTint();
      applySecondaryProgressTint();
    }
  }
  
  private void applySecondaryProgressTint()
  {
    if ((this.mProgressTintInfo.mHasSecondaryProgressTint) || (this.mProgressTintInfo.mHasSecondaryProgressTintMode))
    {
      Drawable localDrawable = getTintTarget(16908303, false);
      if (localDrawable != null)
      {
        if (this.mProgressTintInfo.mHasSecondaryProgressTint) {
          localDrawable.setTintList(this.mProgressTintInfo.mSecondaryProgressTintList);
        }
        if (this.mProgressTintInfo.mHasSecondaryProgressTintMode) {
          localDrawable.setTintMode(this.mProgressTintInfo.mSecondaryProgressTintMode);
        }
        if (localDrawable.isStateful()) {
          localDrawable.setState(getDrawableState());
        }
      }
    }
  }
  
  private void doRefreshProgress(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    for (;;)
    {
      try
      {
        float f;
        Drawable localDrawable1;
        Object localObject1;
        if (this.mMax > 0)
        {
          f = paramInt2 / this.mMax;
          localDrawable1 = this.mCurrentDrawable;
          if (localDrawable1 != null)
          {
            localObject1 = null;
            if (!(localDrawable1 instanceof LayerDrawable)) {
              break label147;
            }
            Drawable localDrawable2 = ((LayerDrawable)localDrawable1).findDrawableByLayerId(paramInt1);
            localObject1 = localDrawable2;
            if (localDrawable2 == null) {
              break label147;
            }
            localObject1 = localDrawable2;
            if (!canResolveLayoutDirection()) {
              break label147;
            }
            localDrawable2.setLayoutDirection(getLayoutDirection());
            localObject1 = localDrawable2;
            break label147;
            ((Drawable)localObject1).setLevel(i);
            if ((paramBoolean2) && (paramInt1 == 16908301)) {
              onProgressRefresh(f, paramBoolean1, paramInt2);
            }
          }
        }
        else
        {
          f = 0.0F;
          continue;
          localObject1 = localDrawable1;
          continue;
        }
        invalidate();
        continue;
        int i = (int)(10000.0F * f);
      }
      finally {}
      label147:
      if (localObject2 == null) {}
    }
  }
  
  private Drawable getTintTarget(int paramInt, boolean paramBoolean)
  {
    Object localObject = null;
    Drawable localDrawable1 = null;
    Drawable localDrawable2 = this.mProgressDrawable;
    if (localDrawable2 != null)
    {
      this.mProgressDrawable = localDrawable2.mutate();
      if ((localDrawable2 instanceof LayerDrawable)) {
        localDrawable1 = ((LayerDrawable)localDrawable2).findDrawableByLayerId(paramInt);
      }
      localObject = localDrawable1;
      if (paramBoolean)
      {
        localObject = localDrawable1;
        if (localDrawable1 == null) {
          localObject = localDrawable2;
        }
      }
    }
    return (Drawable)localObject;
  }
  
  private void initProgressBar()
  {
    this.mMax = 100;
    this.mProgress = 0;
    this.mSecondaryProgress = 0;
    this.mIndeterminate = false;
    this.mOnlyIndeterminate = false;
    this.mDuration = 4000;
    this.mBehavior = 1;
    this.mMinWidth = 24;
    this.mMaxWidth = 48;
    this.mMinHeight = 24;
    this.mMaxHeight = 48;
  }
  
  private static boolean needsTileify(Drawable paramDrawable)
  {
    if ((paramDrawable instanceof LayerDrawable))
    {
      paramDrawable = (LayerDrawable)paramDrawable;
      int j = paramDrawable.getNumberOfLayers();
      int i = 0;
      while (i < j)
      {
        if (needsTileify(paramDrawable.getDrawable(i))) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    return (paramDrawable instanceof BitmapDrawable);
  }
  
  /* Error */
  private void refreshProgress(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 141	com/oneplus/lib/widget/OPProgressBar:mUiThreadId	J
    //   6: invokestatic 135	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   9: invokevirtual 139	java/lang/Thread:getId	()J
    //   12: lcmp
    //   13: ifne +14 -> 27
    //   16: aload_0
    //   17: iload_1
    //   18: iload_2
    //   19: iload_3
    //   20: iconst_1
    //   21: invokespecial 88	com/oneplus/lib/widget/OPProgressBar:doRefreshProgress	(IIZZ)V
    //   24: aload_0
    //   25: monitorexit
    //   26: return
    //   27: aload_0
    //   28: getfield 472	com/oneplus/lib/widget/OPProgressBar:mRefreshProgressRunnable	Lcom/oneplus/lib/widget/OPProgressBar$RefreshProgressRunnable;
    //   31: ifnonnull +16 -> 47
    //   34: aload_0
    //   35: new 15	com/oneplus/lib/widget/OPProgressBar$RefreshProgressRunnable
    //   38: dup
    //   39: aload_0
    //   40: aconst_null
    //   41: invokespecial 475	com/oneplus/lib/widget/OPProgressBar$RefreshProgressRunnable:<init>	(Lcom/oneplus/lib/widget/OPProgressBar;Lcom/oneplus/lib/widget/OPProgressBar$RefreshProgressRunnable;)V
    //   44: putfield 472	com/oneplus/lib/widget/OPProgressBar:mRefreshProgressRunnable	Lcom/oneplus/lib/widget/OPProgressBar$RefreshProgressRunnable;
    //   47: iload_1
    //   48: iload_2
    //   49: iload_3
    //   50: invokestatic 479	com/oneplus/lib/widget/OPProgressBar$RefreshData:obtain	(IIZ)Lcom/oneplus/lib/widget/OPProgressBar$RefreshData;
    //   53: astore 4
    //   55: aload_0
    //   56: getfield 77	com/oneplus/lib/widget/OPProgressBar:mRefreshData	Ljava/util/ArrayList;
    //   59: aload 4
    //   61: invokevirtual 483	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   64: pop
    //   65: aload_0
    //   66: getfield 485	com/oneplus/lib/widget/OPProgressBar:mAttached	Z
    //   69: ifeq -45 -> 24
    //   72: aload_0
    //   73: getfield 82	com/oneplus/lib/widget/OPProgressBar:mRefreshIsPosted	Z
    //   76: ifne -52 -> 24
    //   79: aload_0
    //   80: aload_0
    //   81: getfield 472	com/oneplus/lib/widget/OPProgressBar:mRefreshProgressRunnable	Lcom/oneplus/lib/widget/OPProgressBar$RefreshProgressRunnable;
    //   84: invokevirtual 489	com/oneplus/lib/widget/OPProgressBar:post	(Ljava/lang/Runnable;)Z
    //   87: pop
    //   88: aload_0
    //   89: iconst_1
    //   90: putfield 82	com/oneplus/lib/widget/OPProgressBar:mRefreshIsPosted	Z
    //   93: goto -69 -> 24
    //   96: astore 4
    //   98: aload_0
    //   99: monitorexit
    //   100: aload 4
    //   102: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	103	0	this	OPProgressBar
    //   0	103	1	paramInt1	int
    //   0	103	2	paramInt2	int
    //   0	103	3	paramBoolean	boolean
    //   53	7	4	localRefreshData	RefreshData
    //   96	5	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	24	96	finally
    //   27	47	96	finally
    //   47	93	96	finally
  }
  
  private void scheduleAccessibilityEventSender()
  {
    if (this.mAccessibilityEventSender == null) {
      this.mAccessibilityEventSender = new AccessibilityEventSender(null);
    }
    for (;;)
    {
      postDelayed(this.mAccessibilityEventSender, 200L);
      return;
      removeCallbacks(this.mAccessibilityEventSender);
    }
  }
  
  private Drawable tileify(Drawable paramDrawable, boolean paramBoolean)
  {
    Object localObject;
    if ((paramDrawable instanceof LayerDrawable))
    {
      paramDrawable = (LayerDrawable)paramDrawable;
      int j = paramDrawable.getNumberOfLayers();
      localObject = new Drawable[j];
      int i = 0;
      if (i < j)
      {
        int k = paramDrawable.getId(i);
        Drawable localDrawable = paramDrawable.getDrawable(i);
        if ((k == 16908301) || (k == 16908303)) {}
        for (paramBoolean = true;; paramBoolean = false)
        {
          localObject[i] = tileify(localDrawable, paramBoolean);
          i += 1;
          break;
        }
      }
      localObject = new LayerDrawable((Drawable[])localObject);
      i = 0;
      while (i < j)
      {
        ((LayerDrawable)localObject).setId(i, paramDrawable.getId(i));
        ((LayerDrawable)localObject).setLayerGravity(i, paramDrawable.getLayerGravity(i));
        ((LayerDrawable)localObject).setLayerWidth(i, paramDrawable.getLayerWidth(i));
        ((LayerDrawable)localObject).setLayerHeight(i, paramDrawable.getLayerHeight(i));
        ((LayerDrawable)localObject).setLayerInsetLeft(i, paramDrawable.getLayerInsetLeft(i));
        ((LayerDrawable)localObject).setLayerInsetRight(i, paramDrawable.getLayerInsetRight(i));
        ((LayerDrawable)localObject).setLayerInsetTop(i, paramDrawable.getLayerInsetTop(i));
        ((LayerDrawable)localObject).setLayerInsetBottom(i, paramDrawable.getLayerInsetBottom(i));
        ((LayerDrawable)localObject).setLayerInsetStart(i, paramDrawable.getLayerInsetStart(i));
        ((LayerDrawable)localObject).setLayerInsetEnd(i, paramDrawable.getLayerInsetEnd(i));
        i += 1;
      }
      return (Drawable)localObject;
    }
    if ((paramDrawable instanceof BitmapDrawable))
    {
      paramDrawable = (BitmapDrawable)paramDrawable;
      localObject = paramDrawable.getBitmap();
      if (this.mSampleTile == null) {
        this.mSampleTile = ((Bitmap)localObject);
      }
      paramDrawable = (BitmapDrawable)paramDrawable.getConstantState().newDrawable();
      paramDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
      if (paramBoolean) {
        return new ClipDrawable(paramDrawable, 3, 1);
      }
      return paramDrawable;
    }
    return paramDrawable;
  }
  
  private Drawable tileifyIndeterminate(Drawable paramDrawable)
  {
    Object localObject = paramDrawable;
    if ((paramDrawable instanceof AnimationDrawable))
    {
      paramDrawable = (AnimationDrawable)paramDrawable;
      int j = paramDrawable.getNumberOfFrames();
      localObject = new AnimationDrawable();
      ((AnimationDrawable)localObject).setOneShot(paramDrawable.isOneShot());
      int i = 0;
      while (i < j)
      {
        Drawable localDrawable = tileify(paramDrawable.getFrame(i), true);
        localDrawable.setLevel(10000);
        ((AnimationDrawable)localObject).addFrame(localDrawable, paramDrawable.getDuration(i));
        i += 1;
      }
      ((AnimationDrawable)localObject).setLevel(10000);
    }
    return (Drawable)localObject;
  }
  
  private void updateDrawableBounds(int paramInt1, int paramInt2)
  {
    int n = paramInt1 - (this.mPaddingRight + this.mPaddingLeft);
    int i3 = paramInt2 - (this.mPaddingTop + this.mPaddingBottom);
    paramInt2 = n;
    int i = i3;
    int i1 = 0;
    int i2 = 0;
    paramInt1 = i;
    int j = paramInt2;
    int k;
    int m;
    if (this.mIndeterminateDrawable != null)
    {
      paramInt1 = i;
      j = i2;
      k = paramInt2;
      m = i1;
      if (this.mOnlyIndeterminate)
      {
        if (!(this.mIndeterminateDrawable instanceof AnimationDrawable)) {
          break label174;
        }
        m = i1;
        k = paramInt2;
        j = i2;
        paramInt1 = i;
      }
    }
    for (;;)
    {
      i = j;
      paramInt2 = k;
      if (isLayoutRtl())
      {
        i = j;
        paramInt2 = k;
        if (this.mMirrorForRtl)
        {
          i = n - k;
          paramInt2 = n - j;
        }
      }
      this.mIndeterminateDrawable.setBounds(i, m, paramInt2, paramInt1);
      j = paramInt2;
      if (this.mProgressDrawable != null) {
        this.mProgressDrawable.setBounds(0, 0, j, paramInt1);
      }
      return;
      label174:
      paramInt1 = this.mIndeterminateDrawable.getIntrinsicWidth();
      j = this.mIndeterminateDrawable.getIntrinsicHeight();
      float f1 = paramInt1 / j;
      float f2 = n / i3;
      paramInt1 = i;
      j = i2;
      k = paramInt2;
      m = i1;
      if (f1 != f2) {
        if (f2 > f1)
        {
          paramInt1 = (int)(i3 * f1);
          j = (n - paramInt1) / 2;
          k = j + paramInt1;
          paramInt1 = i;
          m = i1;
        }
        else
        {
          paramInt1 = (int)(n * (1.0F / f1));
          m = (i3 - paramInt1) / 2;
          paramInt1 = m + paramInt1;
          j = i2;
          k = paramInt2;
        }
      }
    }
  }
  
  private void updateDrawableState()
  {
    int[] arrayOfInt = getDrawableState();
    if ((this.mProgressDrawable != null) && (this.mProgressDrawable.isStateful())) {
      this.mProgressDrawable.setState(arrayOfInt);
    }
    if ((this.mIndeterminateDrawable != null) && (this.mIndeterminateDrawable.isStateful())) {
      this.mIndeterminateDrawable.setState(arrayOfInt);
    }
  }
  
  void drawTrack(Canvas paramCanvas)
  {
    Drawable localDrawable = this.mCurrentDrawable;
    int i;
    if (localDrawable != null)
    {
      i = paramCanvas.save();
      if ((!isLayoutRtl()) || (!this.mMirrorForRtl)) {
        break label160;
      }
      paramCanvas.translate(getWidth() - this.mPaddingRight, this.mPaddingTop);
      paramCanvas.scale(-1.0F, 1.0F);
    }
    for (;;)
    {
      long l = getDrawingTime();
      float f;
      if (this.mHasAnimation)
      {
        this.mAnimation.getTransformation(l, this.mTransformation);
        f = this.mTransformation.getAlpha();
      }
      try
      {
        this.mInDrawing = true;
        localDrawable.setLevel((int)(10000.0F * f));
        this.mInDrawing = false;
        postInvalidateOnAnimation();
        localDrawable.draw(paramCanvas);
        paramCanvas.restoreToCount(i);
        if ((this.mShouldStartAnimationDrawable) && ((localDrawable instanceof Animatable))) {
          ((Animatable)localDrawable).start();
        }
        return;
      }
      finally
      {
        label160:
        this.mInDrawing = false;
      }
      paramCanvas.translate(this.mPaddingLeft, this.mPaddingTop);
    }
  }
  
  public void drawableHotspotChanged(float paramFloat1, float paramFloat2)
  {
    super.drawableHotspotChanged(paramFloat1, paramFloat2);
    if (this.mProgressDrawable != null) {
      this.mProgressDrawable.setHotspot(paramFloat1, paramFloat2);
    }
    if (this.mIndeterminateDrawable != null) {
      this.mIndeterminateDrawable.setHotspot(paramFloat1, paramFloat2);
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    updateDrawableState();
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return OPProgressBar.class.getName();
  }
  
  Drawable getCurrentDrawable()
  {
    return this.mCurrentDrawable;
  }
  
  Shape getDrawableShape()
  {
    return new RoundRectShape(new float[] { 5.0F, 5.0F, 5.0F, 5.0F, 5.0F, 5.0F, 5.0F, 5.0F }, null, null);
  }
  
  public Drawable getIndeterminateDrawable()
  {
    return this.mIndeterminateDrawable;
  }
  
  public ColorStateList getIndeterminateTintList()
  {
    ColorStateList localColorStateList = null;
    if (this.mProgressTintInfo != null) {
      localColorStateList = this.mProgressTintInfo.mIndeterminateTintList;
    }
    return localColorStateList;
  }
  
  public PorterDuff.Mode getIndeterminateTintMode()
  {
    PorterDuff.Mode localMode = null;
    if (this.mProgressTintInfo != null) {
      localMode = this.mProgressTintInfo.mIndeterminateTintMode;
    }
    return localMode;
  }
  
  public Interpolator getInterpolator()
  {
    return this.mInterpolator;
  }
  
  @ViewDebug.ExportedProperty(category="progress")
  public int getMax()
  {
    try
    {
      int i = this.mMax;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  @ViewDebug.ExportedProperty(category="progress")
  public int getProgress()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 268	com/oneplus/lib/widget/OPProgressBar:mIndeterminate	Z
    //   6: istore_2
    //   7: iload_2
    //   8: ifeq +9 -> 17
    //   11: iconst_0
    //   12: istore_1
    //   13: aload_0
    //   14: monitorexit
    //   15: iload_1
    //   16: ireturn
    //   17: aload_0
    //   18: getfield 237	com/oneplus/lib/widget/OPProgressBar:mProgress	I
    //   21: istore_1
    //   22: goto -9 -> 13
    //   25: astore_3
    //   26: aload_0
    //   27: monitorexit
    //   28: aload_3
    //   29: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	30	0	this	OPProgressBar
    //   12	10	1	i	int
    //   6	2	2	bool	boolean
    //   25	4	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	7	25	finally
    //   17	22	25	finally
  }
  
  public ColorStateList getProgressBackgroundTintList()
  {
    ColorStateList localColorStateList = null;
    if (this.mProgressTintInfo != null) {
      localColorStateList = this.mProgressTintInfo.mProgressBackgroundTintList;
    }
    return localColorStateList;
  }
  
  public PorterDuff.Mode getProgressBackgroundTintMode()
  {
    PorterDuff.Mode localMode = null;
    if (this.mProgressTintInfo != null) {
      localMode = this.mProgressTintInfo.mProgressBackgroundTintMode;
    }
    return localMode;
  }
  
  public Drawable getProgressDrawable()
  {
    return this.mProgressDrawable;
  }
  
  public ColorStateList getProgressTintList()
  {
    ColorStateList localColorStateList = null;
    if (this.mProgressTintInfo != null) {
      localColorStateList = this.mProgressTintInfo.mProgressTintList;
    }
    return localColorStateList;
  }
  
  public PorterDuff.Mode getProgressTintMode()
  {
    PorterDuff.Mode localMode = null;
    if (this.mProgressTintInfo != null) {
      localMode = this.mProgressTintInfo.mProgressTintMode;
    }
    return localMode;
  }
  
  /* Error */
  @ViewDebug.ExportedProperty(category="progress")
  public int getSecondaryProgress()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 268	com/oneplus/lib/widget/OPProgressBar:mIndeterminate	Z
    //   6: istore_2
    //   7: iload_2
    //   8: ifeq +9 -> 17
    //   11: iconst_0
    //   12: istore_1
    //   13: aload_0
    //   14: monitorexit
    //   15: iload_1
    //   16: ireturn
    //   17: aload_0
    //   18: getfield 245	com/oneplus/lib/widget/OPProgressBar:mSecondaryProgress	I
    //   21: istore_1
    //   22: goto -9 -> 13
    //   25: astore_3
    //   26: aload_0
    //   27: monitorexit
    //   28: aload_3
    //   29: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	30	0	this	OPProgressBar
    //   12	10	1	i	int
    //   6	2	2	bool	boolean
    //   25	4	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	7	25	finally
    //   17	22	25	finally
  }
  
  public ColorStateList getSecondaryProgressTintList()
  {
    ColorStateList localColorStateList = null;
    if (this.mProgressTintInfo != null) {
      localColorStateList = this.mProgressTintInfo.mSecondaryProgressTintList;
    }
    return localColorStateList;
  }
  
  public PorterDuff.Mode getSecondaryProgressTintMode()
  {
    PorterDuff.Mode localMode = null;
    if (this.mProgressTintInfo != null) {
      localMode = this.mProgressTintInfo.mSecondaryProgressTintMode;
    }
    return localMode;
  }
  
  public final void incrementProgressBy(int paramInt)
  {
    try
    {
      setProgress(this.mProgress + paramInt);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public final void incrementSecondaryProgressBy(int paramInt)
  {
    try
    {
      setSecondaryProgress(this.mSecondaryProgress + paramInt);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void invalidateDrawable(Drawable paramDrawable)
  {
    if (!this.mInDrawing)
    {
      if (verifyDrawable(paramDrawable))
      {
        paramDrawable = paramDrawable.getBounds();
        int i = getScrollX() + this.mPaddingLeft;
        int j = getScrollY() + this.mPaddingTop;
        invalidate(paramDrawable.left + i, paramDrawable.top + j, paramDrawable.right + i, paramDrawable.bottom + j);
      }
    }
    else {
      return;
    }
    super.invalidateDrawable(paramDrawable);
  }
  
  @ViewDebug.ExportedProperty(category="progress")
  public boolean isIndeterminate()
  {
    try
    {
      boolean bool = this.mIndeterminate;
      return bool;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public boolean isLayoutRtl()
  {
    return getLayoutDirection() == 1;
  }
  
  public void jumpDrawablesToCurrentState()
  {
    super.jumpDrawablesToCurrentState();
    if (this.mProgressDrawable != null) {
      this.mProgressDrawable.jumpToCurrentState();
    }
    if (this.mIndeterminateDrawable != null) {
      this.mIndeterminateDrawable.jumpToCurrentState();
    }
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (this.mIndeterminate) {
      startAnimation();
    }
    if (this.mRefreshData != null) {}
    try
    {
      int j = this.mRefreshData.size();
      int i = 0;
      while (i < j)
      {
        RefreshData localRefreshData = (RefreshData)this.mRefreshData.get(i);
        doRefreshProgress(localRefreshData.id, localRefreshData.progress, localRefreshData.fromUser, true);
        localRefreshData.recycle();
        i += 1;
      }
      this.mRefreshData.clear();
      this.mAttached = true;
      return;
    }
    finally {}
  }
  
  protected void onDetachedFromWindow()
  {
    if (this.mIndeterminate) {
      stopAnimation();
    }
    if (this.mRefreshProgressRunnable != null)
    {
      removeCallbacks(this.mRefreshProgressRunnable);
      this.mRefreshIsPosted = false;
    }
    if (this.mAccessibilityEventSender != null) {
      removeCallbacks(this.mAccessibilityEventSender);
    }
    super.onDetachedFromWindow();
    this.mAttached = false;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    try
    {
      super.onDraw(paramCanvas);
      drawTrack(paramCanvas);
      return;
    }
    finally
    {
      paramCanvas = finally;
      throw paramCanvas;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int j = 0;
    int i = 0;
    try
    {
      Drawable localDrawable = this.mCurrentDrawable;
      if (localDrawable != null)
      {
        j = Math.max(this.mMinWidth, Math.min(this.mMaxWidth, localDrawable.getIntrinsicWidth()));
        i = Math.max(this.mMinHeight, Math.min(this.mMaxHeight, localDrawable.getIntrinsicHeight()));
      }
      updateDrawableState();
      int k = this.mPaddingLeft;
      int m = this.mPaddingRight;
      int n = this.mPaddingTop;
      int i1 = this.mPaddingBottom;
      setMeasuredDimension(resolveSizeAndState(j + (k + m), paramInt1, 0), resolveSizeAndState(i + (n + i1), paramInt2, 0));
      return;
    }
    finally {}
  }
  
  void onProgressRefresh(float paramFloat, boolean paramBoolean, int paramInt) {}
  
  public void onResolveDrawables(int paramInt)
  {
    Drawable localDrawable = this.mCurrentDrawable;
    if (localDrawable != null) {
      localDrawable.setLayoutDirection(paramInt);
    }
    if (this.mIndeterminateDrawable != null) {
      this.mIndeterminateDrawable.setLayoutDirection(paramInt);
    }
    if (this.mProgressDrawable != null) {
      this.mProgressDrawable.setLayoutDirection(paramInt);
    }
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    setProgress(paramParcelable.progress);
    setSecondaryProgress(paramParcelable.secondaryProgress);
  }
  
  public Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    localSavedState.progress = this.mProgress;
    localSavedState.secondaryProgress = this.mSecondaryProgress;
    return localSavedState;
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    updateDrawableBounds(paramInt1, paramInt2);
  }
  
  protected void onVisibilityChanged(View paramView, int paramInt)
  {
    super.onVisibilityChanged(paramView, paramInt);
    if (this.mIndeterminate)
    {
      if ((paramInt == 8) || (paramInt == 4)) {
        stopAnimation();
      }
    }
    else {
      return;
    }
    startAnimation();
  }
  
  public void postInvalidate()
  {
    if (!this.mNoInvalidate) {
      super.postInvalidate();
    }
  }
  
  public void setIndeterminate(boolean paramBoolean)
  {
    for (;;)
    {
      try
      {
        if (this.mOnlyIndeterminate)
        {
          boolean bool = this.mIndeterminate;
          if (bool) {
            return;
          }
        }
        if (paramBoolean == this.mIndeterminate) {
          continue;
        }
        this.mIndeterminate = paramBoolean;
        if (paramBoolean)
        {
          this.mCurrentDrawable = this.mIndeterminateDrawable;
          startAnimation();
          continue;
        }
        this.mCurrentDrawable = this.mProgressDrawable;
      }
      finally {}
      stopAnimation();
    }
  }
  
  public void setIndeterminateDrawable(Drawable paramDrawable)
  {
    if (this.mIndeterminateDrawable != paramDrawable)
    {
      if (this.mIndeterminateDrawable != null)
      {
        this.mIndeterminateDrawable.setCallback(null);
        unscheduleDrawable(this.mIndeterminateDrawable);
      }
      this.mIndeterminateDrawable = paramDrawable;
      if (paramDrawable != null)
      {
        paramDrawable.setCallback(this);
        paramDrawable.setLayoutDirection(getLayoutDirection());
        if (paramDrawable.isStateful()) {
          paramDrawable.setState(getDrawableState());
        }
        applyIndeterminateTint();
      }
      if (this.mIndeterminate)
      {
        this.mCurrentDrawable = paramDrawable;
        postInvalidate();
      }
    }
  }
  
  public void setIndeterminateDrawableTiled(Drawable paramDrawable)
  {
    Drawable localDrawable = paramDrawable;
    if (paramDrawable != null) {
      localDrawable = tileifyIndeterminate(paramDrawable);
    }
    setIndeterminateDrawable(localDrawable);
  }
  
  public void setIndeterminateTintList(ColorStateList paramColorStateList)
  {
    if (this.mProgressTintInfo == null) {
      this.mProgressTintInfo = new ProgressTintInfo(null);
    }
    this.mProgressTintInfo.mIndeterminateTintList = paramColorStateList;
    this.mProgressTintInfo.mHasIndeterminateTint = true;
    applyIndeterminateTint();
  }
  
  public void setIndeterminateTintMode(PorterDuff.Mode paramMode)
  {
    if (this.mProgressTintInfo == null) {
      this.mProgressTintInfo = new ProgressTintInfo(null);
    }
    this.mProgressTintInfo.mIndeterminateTintMode = paramMode;
    this.mProgressTintInfo.mHasIndeterminateTintMode = true;
    applyIndeterminateTint();
  }
  
  public void setInterpolator(Context paramContext, int paramInt)
  {
    setInterpolator(AnimationUtils.loadInterpolator(paramContext, paramInt));
  }
  
  public void setInterpolator(Interpolator paramInterpolator)
  {
    this.mInterpolator = paramInterpolator;
  }
  
  public void setMax(int paramInt)
  {
    int i = paramInt;
    if (paramInt < 0) {
      i = 0;
    }
    try
    {
      if (i != this.mMax)
      {
        this.mMax = i;
        postInvalidate();
        if (this.mProgress > i) {
          this.mProgress = i;
        }
        refreshProgress(16908301, this.mProgress, false);
      }
      return;
    }
    finally {}
  }
  
  public void setProgress(int paramInt)
  {
    try
    {
      setProgress(paramInt, false);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  boolean setProgress(int paramInt, boolean paramBoolean)
  {
    try
    {
      boolean bool = this.mIndeterminate;
      if (bool) {
        return false;
      }
      paramInt = MathUtils.constrain(paramInt, 0, this.mMax);
      int i = this.mProgress;
      if (paramInt == i) {
        return false;
      }
      this.mProgress = paramInt;
      refreshProgress(16908301, this.mProgress, paramBoolean);
      return true;
    }
    finally {}
  }
  
  public void setProgressBackgroundTintList(ColorStateList paramColorStateList)
  {
    if (this.mProgressTintInfo == null) {
      this.mProgressTintInfo = new ProgressTintInfo(null);
    }
    this.mProgressTintInfo.mProgressBackgroundTintList = paramColorStateList;
    this.mProgressTintInfo.mHasProgressBackgroundTint = true;
    if (this.mProgressDrawable != null) {
      applyProgressBackgroundTint();
    }
  }
  
  public void setProgressBackgroundTintMode(PorterDuff.Mode paramMode)
  {
    if (this.mProgressTintInfo == null) {
      this.mProgressTintInfo = new ProgressTintInfo(null);
    }
    this.mProgressTintInfo.mProgressBackgroundTintMode = paramMode;
    this.mProgressTintInfo.mHasProgressBackgroundTintMode = true;
    if (this.mProgressDrawable != null) {
      applyProgressBackgroundTint();
    }
  }
  
  public void setProgressDrawable(Drawable paramDrawable)
  {
    if (this.mProgressDrawable != paramDrawable)
    {
      if (this.mProgressDrawable != null)
      {
        this.mProgressDrawable.setCallback(null);
        unscheduleDrawable(this.mProgressDrawable);
      }
      this.mProgressDrawable = paramDrawable;
      if (paramDrawable != null)
      {
        paramDrawable.setCallback(this);
        paramDrawable.setLayoutDirection(getLayoutDirection());
        if (paramDrawable.isStateful()) {
          paramDrawable.setState(getDrawableState());
        }
        int i = paramDrawable.getMinimumHeight();
        if (this.mMaxHeight < i)
        {
          this.mMaxHeight = i;
          requestLayout();
        }
        applyProgressTints();
      }
      if (!this.mIndeterminate)
      {
        this.mCurrentDrawable = paramDrawable;
        postInvalidate();
      }
      updateDrawableBounds(getWidth(), getHeight());
      updateDrawableState();
      doRefreshProgress(16908301, this.mProgress, false, false);
      doRefreshProgress(16908303, this.mSecondaryProgress, false, false);
    }
  }
  
  public void setProgressDrawableTiled(Drawable paramDrawable)
  {
    Drawable localDrawable = paramDrawable;
    if (paramDrawable != null) {
      localDrawable = tileify(paramDrawable, false);
    }
    setProgressDrawable(localDrawable);
  }
  
  public void setProgressTintList(ColorStateList paramColorStateList)
  {
    if (this.mProgressTintInfo == null) {
      this.mProgressTintInfo = new ProgressTintInfo(null);
    }
    this.mProgressTintInfo.mProgressTintList = paramColorStateList;
    this.mProgressTintInfo.mHasProgressTint = true;
    if (this.mProgressDrawable != null) {
      applyPrimaryProgressTint();
    }
  }
  
  public void setProgressTintMode(PorterDuff.Mode paramMode)
  {
    if (this.mProgressTintInfo == null) {
      this.mProgressTintInfo = new ProgressTintInfo(null);
    }
    this.mProgressTintInfo.mProgressTintMode = paramMode;
    this.mProgressTintInfo.mHasProgressTintMode = true;
    if (this.mProgressDrawable != null) {
      applyPrimaryProgressTint();
    }
  }
  
  public void setSecondaryProgress(int paramInt)
  {
    try
    {
      boolean bool = this.mIndeterminate;
      if (bool) {
        return;
      }
      int i = paramInt;
      if (paramInt < 0) {
        i = 0;
      }
      paramInt = i;
      if (i > this.mMax) {
        paramInt = this.mMax;
      }
      if (paramInt != this.mSecondaryProgress)
      {
        this.mSecondaryProgress = paramInt;
        refreshProgress(16908303, this.mSecondaryProgress, false);
      }
      return;
    }
    finally {}
  }
  
  public void setSecondaryProgressTintList(ColorStateList paramColorStateList)
  {
    if (this.mProgressTintInfo == null) {
      this.mProgressTintInfo = new ProgressTintInfo(null);
    }
    this.mProgressTintInfo.mSecondaryProgressTintList = paramColorStateList;
    this.mProgressTintInfo.mHasSecondaryProgressTint = true;
    if (this.mProgressDrawable != null) {
      applySecondaryProgressTint();
    }
  }
  
  public void setSecondaryProgressTintMode(PorterDuff.Mode paramMode)
  {
    if (this.mProgressTintInfo == null) {
      this.mProgressTintInfo = new ProgressTintInfo(null);
    }
    this.mProgressTintInfo.mSecondaryProgressTintMode = paramMode;
    this.mProgressTintInfo.mHasSecondaryProgressTintMode = true;
    if (this.mProgressDrawable != null) {
      applySecondaryProgressTint();
    }
  }
  
  public void setVisibility(int paramInt)
  {
    if (getVisibility() != paramInt)
    {
      super.setVisibility(paramInt);
      if (this.mIndeterminate)
      {
        if ((paramInt != 8) && (paramInt != 4)) {
          break label36;
        }
        stopAnimation();
      }
    }
    return;
    label36:
    startAnimation();
  }
  
  void startAnimation()
  {
    if (getVisibility() != 0) {
      return;
    }
    if ((this.mIndeterminateDrawable instanceof Animatable))
    {
      this.mShouldStartAnimationDrawable = true;
      this.mHasAnimation = false;
      postInvalidate();
      return;
    }
    this.mHasAnimation = true;
    if (this.mInterpolator == null) {
      this.mInterpolator = new LinearInterpolator();
    }
    if (this.mTransformation == null)
    {
      this.mTransformation = new Transformation();
      label74:
      if (this.mAnimation != null) {
        break label159;
      }
      this.mAnimation = new AlphaAnimation(0.0F, 1.0F);
    }
    for (;;)
    {
      this.mAnimation.setRepeatMode(this.mBehavior);
      this.mAnimation.setRepeatCount(-1);
      this.mAnimation.setDuration(this.mDuration);
      this.mAnimation.setInterpolator(this.mInterpolator);
      this.mAnimation.setStartTime(-1L);
      break;
      this.mTransformation.clear();
      break label74;
      label159:
      this.mAnimation.reset();
    }
  }
  
  void stopAnimation()
  {
    this.mHasAnimation = false;
    if ((this.mIndeterminateDrawable instanceof Animatable))
    {
      ((Animatable)this.mIndeterminateDrawable).stop();
      this.mShouldStartAnimationDrawable = false;
    }
    postInvalidate();
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    if ((paramDrawable == this.mProgressDrawable) || (paramDrawable == this.mIndeterminateDrawable)) {
      return true;
    }
    return super.verifyDrawable(paramDrawable);
  }
  
  private class AccessibilityEventSender
    implements Runnable
  {
    private AccessibilityEventSender() {}
    
    public void run()
    {
      OPProgressBar.this.sendAccessibilityEvent(4);
    }
  }
  
  private static class ProgressTintInfo
  {
    boolean mHasIndeterminateTint;
    boolean mHasIndeterminateTintMode;
    boolean mHasProgressBackgroundTint;
    boolean mHasProgressBackgroundTintMode;
    boolean mHasProgressTint;
    boolean mHasProgressTintMode;
    boolean mHasSecondaryProgressTint;
    boolean mHasSecondaryProgressTintMode;
    ColorStateList mIndeterminateTintList;
    PorterDuff.Mode mIndeterminateTintMode;
    ColorStateList mProgressBackgroundTintList;
    PorterDuff.Mode mProgressBackgroundTintMode;
    ColorStateList mProgressTintList;
    PorterDuff.Mode mProgressTintMode;
    ColorStateList mSecondaryProgressTintList;
    PorterDuff.Mode mSecondaryProgressTintMode;
  }
  
  private static class RefreshData
  {
    private static final int POOL_MAX = 24;
    private static final Pools.SynchronizedPool<RefreshData> sPool = new Pools.SynchronizedPool(24);
    public boolean fromUser;
    public int id;
    public int progress;
    
    public static RefreshData obtain(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      RefreshData localRefreshData2 = (RefreshData)sPool.acquire();
      RefreshData localRefreshData1 = localRefreshData2;
      if (localRefreshData2 == null) {
        localRefreshData1 = new RefreshData();
      }
      localRefreshData1.id = paramInt1;
      localRefreshData1.progress = paramInt2;
      localRefreshData1.fromUser = paramBoolean;
      return localRefreshData1;
    }
    
    public void recycle()
    {
      sPool.release(this);
    }
  }
  
  private class RefreshProgressRunnable
    implements Runnable
  {
    private RefreshProgressRunnable() {}
    
    public void run()
    {
      synchronized (OPProgressBar.this)
      {
        int j = OPProgressBar.-get0(OPProgressBar.this).size();
        int i = 0;
        while (i < j)
        {
          OPProgressBar.RefreshData localRefreshData = (OPProgressBar.RefreshData)OPProgressBar.-get0(OPProgressBar.this).get(i);
          OPProgressBar.-wrap0(OPProgressBar.this, localRefreshData.id, localRefreshData.progress, localRefreshData.fromUser, true);
          localRefreshData.recycle();
          i += 1;
        }
        OPProgressBar.-get0(OPProgressBar.this).clear();
        OPProgressBar.-set0(OPProgressBar.this, false);
        return;
      }
    }
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public OPProgressBar.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new OPProgressBar.SavedState(paramAnonymousParcel, null);
      }
      
      public OPProgressBar.SavedState[] newArray(int paramAnonymousInt)
      {
        return new OPProgressBar.SavedState[paramAnonymousInt];
      }
    };
    int progress;
    int secondaryProgress;
    
    private SavedState(Parcel paramParcel)
    {
      super();
      this.progress = paramParcel.readInt();
      this.secondaryProgress = paramParcel.readInt();
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.progress);
      paramParcel.writeInt(this.secondaryProgress);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\OPProgressBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */