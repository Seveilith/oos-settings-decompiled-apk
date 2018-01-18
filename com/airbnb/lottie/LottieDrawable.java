package com.airbnb.lottie;

import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.os.Build.VERSION;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import com.airbnb.lottie.manager.FontAssetManager;
import com.airbnb.lottie.manager.ImageAssetManager;
import com.airbnb.lottie.model.layer.CompositionLayer;
import com.airbnb.lottie.model.layer.Layer.Factory;
import com.airbnb.lottie.utils.LottieValueAnimator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LottieDrawable
  extends Drawable
  implements Drawable.Callback
{
  private static final String TAG = LottieDrawable.class.getSimpleName();
  private int alpha = 255;
  private final LottieValueAnimator animator = new LottieValueAnimator();
  private final Set<ColorFilterData> colorFilterData = new HashSet();
  private LottieComposition composition;
  @Nullable
  private CompositionLayer compositionLayer;
  private boolean enableMergePaths;
  @Nullable
  FontAssetDelegate fontAssetDelegate;
  @Nullable
  private FontAssetManager fontAssetManager;
  @Nullable
  private ImageAssetDelegate imageAssetDelegate;
  @Nullable
  private ImageAssetManager imageAssetManager;
  @Nullable
  private String imageAssetsFolder;
  private final ArrayList<LazyCompositionTask> lazyCompositionTasks = new ArrayList();
  private final Matrix matrix = new Matrix();
  private boolean performanceTrackingEnabled;
  private float scale = 1.0F;
  private float speed = 1.0F;
  private boolean systemAnimationsAreDisabled;
  @Nullable
  TextDelegate textDelegate;
  
  public LottieDrawable()
  {
    this.animator.setRepeatCount(0);
    this.animator.setInterpolator(new LinearInterpolator());
    this.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        if (LottieDrawable.this.compositionLayer == null) {
          return;
        }
        LottieDrawable.this.compositionLayer.setProgress(LottieDrawable.this.animator.getProgress());
      }
    });
  }
  
  private void addColorFilterInternal(@Nullable String paramString1, @Nullable String paramString2, @Nullable ColorFilter paramColorFilter)
  {
    ColorFilterData localColorFilterData = new ColorFilterData(paramString1, paramString2, paramColorFilter);
    if (paramColorFilter != null) {
      this.colorFilterData.add(new ColorFilterData(paramString1, paramString2, paramColorFilter));
    }
    for (;;)
    {
      if (this.compositionLayer == null) {
        return;
      }
      this.compositionLayer.addColorFilter(paramString1, paramString2, paramColorFilter);
      return;
      if (!this.colorFilterData.contains(localColorFilterData)) {
        break;
      }
      this.colorFilterData.remove(localColorFilterData);
    }
  }
  
  private void applyColorFilters()
  {
    Iterator localIterator;
    if (this.compositionLayer != null) {
      localIterator = this.colorFilterData.iterator();
    }
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        return;
        return;
      }
      ColorFilterData localColorFilterData = (ColorFilterData)localIterator.next();
      this.compositionLayer.addColorFilter(localColorFilterData.layerName, localColorFilterData.contentName, localColorFilterData.colorFilter);
    }
  }
  
  private void buildCompositionLayer()
  {
    this.compositionLayer = new CompositionLayer(this, Layer.Factory.newInstance(this.composition), this.composition.getLayers(), this.composition);
  }
  
  private void clearComposition()
  {
    recycleBitmaps();
    this.compositionLayer = null;
    this.imageAssetManager = null;
    invalidateSelf();
  }
  
  @Nullable
  private Context getContext()
  {
    Drawable.Callback localCallback = getCallback();
    if (localCallback != null)
    {
      if (!(localCallback instanceof View)) {
        return null;
      }
    }
    else {
      return null;
    }
    return ((View)localCallback).getContext();
  }
  
  private FontAssetManager getFontAssetManager()
  {
    if (getCallback() != null) {
      if (this.fontAssetManager == null) {
        break label21;
      }
    }
    for (;;)
    {
      return this.fontAssetManager;
      return null;
      label21:
      this.fontAssetManager = new FontAssetManager(getCallback(), this.fontAssetDelegate);
    }
  }
  
  private ImageAssetManager getImageAssetManager()
  {
    if (getCallback() != null)
    {
      if (this.imageAssetManager != null) {
        break label28;
      }
      if (this.imageAssetManager == null) {
        break label57;
      }
    }
    for (;;)
    {
      return this.imageAssetManager;
      return null;
      label28:
      if (this.imageAssetManager.hasSameContext(getContext())) {
        break;
      }
      this.imageAssetManager.recycleBitmaps();
      this.imageAssetManager = null;
      break;
      label57:
      this.imageAssetManager = new ImageAssetManager(getCallback(), this.imageAssetsFolder, this.imageAssetDelegate, this.composition.getImages());
    }
  }
  
  private float getMaxScale(@NonNull Canvas paramCanvas)
  {
    return Math.min(paramCanvas.getWidth() / this.composition.getBounds().width(), paramCanvas.getHeight() / this.composition.getBounds().height());
  }
  
  private void playAnimation(final boolean paramBoolean)
  {
    if (this.compositionLayer != null)
    {
      if (!paramBoolean) {
        this.animator.resumeAnimation();
      }
    }
    else
    {
      this.lazyCompositionTasks.add(new LazyCompositionTask()
      {
        public void run(LottieComposition paramAnonymousLottieComposition)
        {
          LottieDrawable.this.playAnimation(paramBoolean);
        }
      });
      return;
    }
    this.animator.start();
  }
  
  private void reverseAnimation(final boolean paramBoolean)
  {
    float f;
    if (this.compositionLayer != null)
    {
      f = this.animator.getProgress();
      this.animator.reverse();
      if (!paramBoolean) {
        break label59;
      }
    }
    label59:
    while (getProgress() == 1.0F)
    {
      this.animator.setProgress(this.animator.getMinProgress());
      return;
      this.lazyCompositionTasks.add(new LazyCompositionTask()
      {
        public void run(LottieComposition paramAnonymousLottieComposition)
        {
          LottieDrawable.this.reverseAnimation(paramBoolean);
        }
      });
      return;
    }
    this.animator.setProgress(f);
  }
  
  private void updateBounds()
  {
    if (this.composition != null)
    {
      float f = getScale();
      setBounds(0, 0, (int)(this.composition.getBounds().width() * f), (int)(f * this.composition.getBounds().height()));
      return;
    }
  }
  
  public void addAnimatorListener(Animator.AnimatorListener paramAnimatorListener)
  {
    this.animator.addListener(paramAnimatorListener);
  }
  
  public void addAnimatorUpdateListener(ValueAnimator.AnimatorUpdateListener paramAnimatorUpdateListener)
  {
    this.animator.addUpdateListener(paramAnimatorUpdateListener);
  }
  
  public void addColorFilter(ColorFilter paramColorFilter)
  {
    addColorFilterInternal(null, null, paramColorFilter);
  }
  
  public void addColorFilterToContent(String paramString1, String paramString2, @Nullable ColorFilter paramColorFilter)
  {
    addColorFilterInternal(paramString1, paramString2, paramColorFilter);
  }
  
  public void addColorFilterToLayer(String paramString, @Nullable ColorFilter paramColorFilter)
  {
    addColorFilterInternal(paramString, null, paramColorFilter);
  }
  
  public void cancelAnimation()
  {
    this.lazyCompositionTasks.clear();
    this.animator.cancel();
  }
  
  public void clearColorFilters()
  {
    this.colorFilterData.clear();
    addColorFilterInternal(null, null, null);
  }
  
  public void draw(@NonNull Canvas paramCanvas)
  {
    L.beginSection("Drawable#draw");
    float f2;
    if (this.compositionLayer != null)
    {
      f1 = this.scale;
      f2 = getMaxScale(paramCanvas);
      if (f1 <= f2) {
        break label176;
      }
    }
    for (float f1 = this.scale / f2;; f1 = 1.0F)
    {
      if (f1 > 1.0F)
      {
        paramCanvas.save();
        float f3 = this.composition.getBounds().width() / 2.0F;
        float f4 = this.composition.getBounds().height() / 2.0F;
        float f5 = f3 * f2;
        float f6 = f4 * f2;
        paramCanvas.translate(f3 * getScale() - f5, f4 * getScale() - f6);
        paramCanvas.scale(f1, f1, f5, f6);
      }
      this.matrix.reset();
      this.matrix.preScale(f2, f2);
      this.compositionLayer.draw(paramCanvas, this.matrix, this.alpha);
      L.endSection("Drawable#draw");
      if (f1 > 1.0F) {
        paramCanvas.restore();
      }
      return;
      return;
      label176:
      f2 = f1;
    }
  }
  
  public void enableMergePathsForKitKatAndAbove(boolean paramBoolean)
  {
    if (Build.VERSION.SDK_INT >= 19)
    {
      this.enableMergePaths = paramBoolean;
      if (this.composition != null) {}
    }
    else
    {
      Log.w(TAG, "Merge paths are not supported pre-Kit Kat.");
      return;
    }
    buildCompositionLayer();
  }
  
  public boolean enableMergePathsForKitKatAndAbove()
  {
    return this.enableMergePaths;
  }
  
  public int getAlpha()
  {
    return this.alpha;
  }
  
  public LottieComposition getComposition()
  {
    return this.composition;
  }
  
  @Nullable
  public Bitmap getImageAsset(String paramString)
  {
    ImageAssetManager localImageAssetManager = getImageAssetManager();
    if (localImageAssetManager == null) {
      return null;
    }
    return localImageAssetManager.bitmapForId(paramString);
  }
  
  @Nullable
  public String getImageAssetsFolder()
  {
    return this.imageAssetsFolder;
  }
  
  public int getIntrinsicHeight()
  {
    if (this.composition != null) {
      return (int)(this.composition.getBounds().height() * getScale());
    }
    return -1;
  }
  
  public int getIntrinsicWidth()
  {
    if (this.composition != null) {
      return (int)(this.composition.getBounds().width() * getScale());
    }
    return -1;
  }
  
  public int getOpacity()
  {
    return -3;
  }
  
  @Nullable
  public PerformanceTracker getPerformanceTracker()
  {
    if (this.composition == null) {
      return null;
    }
    return this.composition.getPerformanceTracker();
  }
  
  public float getProgress()
  {
    return this.animator.getProgress();
  }
  
  public float getScale()
  {
    return this.scale;
  }
  
  @Nullable
  public TextDelegate getTextDelegate()
  {
    return this.textDelegate;
  }
  
  @Nullable
  public Typeface getTypeface(String paramString1, String paramString2)
  {
    FontAssetManager localFontAssetManager = getFontAssetManager();
    if (localFontAssetManager == null) {
      return null;
    }
    return localFontAssetManager.getTypeface(paramString1, paramString2);
  }
  
  public boolean hasMasks()
  {
    if (this.compositionLayer == null) {}
    while (!this.compositionLayer.hasMasks()) {
      return false;
    }
    return true;
  }
  
  public boolean hasMatte()
  {
    if (this.compositionLayer == null) {}
    while (!this.compositionLayer.hasMatte()) {
      return false;
    }
    return true;
  }
  
  public void invalidateDrawable(@NonNull Drawable paramDrawable)
  {
    paramDrawable = getCallback();
    if (paramDrawable != null)
    {
      paramDrawable.invalidateDrawable(this);
      return;
    }
  }
  
  public void invalidateSelf()
  {
    Drawable.Callback localCallback = getCallback();
    if (localCallback == null) {
      return;
    }
    localCallback.invalidateDrawable(this);
  }
  
  public boolean isAnimating()
  {
    return this.animator.isRunning();
  }
  
  public boolean isLooping()
  {
    return this.animator.getRepeatCount() == -1;
  }
  
  public void loop(boolean paramBoolean)
  {
    int i = 0;
    LottieValueAnimator localLottieValueAnimator = this.animator;
    if (!paramBoolean) {}
    for (;;)
    {
      localLottieValueAnimator.setRepeatCount(i);
      return;
      i = -1;
    }
  }
  
  public void playAnimation()
  {
    playAnimation(true);
  }
  
  public void playAnimation(@FloatRange(from=0.0D, to=1.0D) float paramFloat1, @FloatRange(from=0.0D, to=1.0D) float paramFloat2)
  {
    this.animator.updateValues(paramFloat1, paramFloat2);
    this.animator.setCurrentPlayTime(0L);
    setProgress(paramFloat1);
    playAnimation(false);
  }
  
  public void playAnimation(final int paramInt1, final int paramInt2)
  {
    if (this.composition != null)
    {
      playAnimation(paramInt1 / this.composition.getDurationFrames(), paramInt2 / this.composition.getDurationFrames());
      return;
    }
    this.lazyCompositionTasks.add(new LazyCompositionTask()
    {
      public void run(LottieComposition paramAnonymousLottieComposition)
      {
        LottieDrawable.this.playAnimation(paramInt1, paramInt2);
      }
    });
  }
  
  public void recycleBitmaps()
  {
    if (this.imageAssetManager == null) {
      return;
    }
    this.imageAssetManager.recycleBitmaps();
  }
  
  public void removeAnimatorListener(Animator.AnimatorListener paramAnimatorListener)
  {
    this.animator.removeListener(paramAnimatorListener);
  }
  
  public void removeAnimatorUpdateListener(ValueAnimator.AnimatorUpdateListener paramAnimatorUpdateListener)
  {
    this.animator.removeUpdateListener(paramAnimatorUpdateListener);
  }
  
  public void resumeAnimation()
  {
    boolean bool = false;
    if ((this.animator.getAnimatedFraction() != this.animator.getMaxProgress()) && (!this.systemAnimationsAreDisabled)) {}
    for (;;)
    {
      playAnimation(bool);
      return;
      bool = true;
    }
  }
  
  public void resumeReverseAnimation()
  {
    reverseAnimation(false);
  }
  
  public void reverseAnimation()
  {
    getProgress();
    reverseAnimation(true);
  }
  
  public void scheduleDrawable(@NonNull Drawable paramDrawable, @NonNull Runnable paramRunnable, long paramLong)
  {
    paramDrawable = getCallback();
    if (paramDrawable != null)
    {
      paramDrawable.scheduleDrawable(this, paramRunnable, paramLong);
      return;
    }
  }
  
  public void setAlpha(@IntRange(from=0L, to=255L) int paramInt)
  {
    this.alpha = paramInt;
  }
  
  public void setColorFilter(@Nullable ColorFilter paramColorFilter)
  {
    throw new UnsupportedOperationException("Use addColorFilter instead.");
  }
  
  public boolean setComposition(LottieComposition paramLottieComposition)
  {
    Iterator localIterator;
    if (this.composition != paramLottieComposition)
    {
      clearComposition();
      this.composition = paramLottieComposition;
      setSpeed(this.speed);
      setScale(this.scale);
      updateBounds();
      buildCompositionLayer();
      applyColorFilters();
      localIterator = new ArrayList(this.lazyCompositionTasks).iterator();
    }
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        this.lazyCompositionTasks.clear();
        paramLottieComposition.setPerformanceTrackingEnabled(this.performanceTrackingEnabled);
        this.animator.forceUpdate();
        return true;
        return false;
      }
      ((LazyCompositionTask)localIterator.next()).run(paramLottieComposition);
      localIterator.remove();
    }
  }
  
  public void setFontAssetDelegate(FontAssetDelegate paramFontAssetDelegate)
  {
    this.fontAssetDelegate = paramFontAssetDelegate;
    if (this.fontAssetManager == null) {
      return;
    }
    this.fontAssetManager.setDelegate(paramFontAssetDelegate);
  }
  
  public void setImageAssetDelegate(ImageAssetDelegate paramImageAssetDelegate)
  {
    this.imageAssetDelegate = paramImageAssetDelegate;
    if (this.imageAssetManager == null) {
      return;
    }
    this.imageAssetManager.setDelegate(paramImageAssetDelegate);
  }
  
  public void setImagesAssetsFolder(@Nullable String paramString)
  {
    this.imageAssetsFolder = paramString;
  }
  
  public void setMaxFrame(final int paramInt)
  {
    if (this.composition != null)
    {
      setMaxProgress(paramInt / this.composition.getDurationFrames());
      return;
    }
    this.lazyCompositionTasks.add(new LazyCompositionTask()
    {
      public void run(LottieComposition paramAnonymousLottieComposition)
      {
        LottieDrawable.this.setMaxFrame(paramInt);
      }
    });
  }
  
  public void setMaxProgress(float paramFloat)
  {
    this.animator.setMaxProgress(paramFloat);
  }
  
  public void setMinAndMaxFrame(int paramInt1, int paramInt2)
  {
    setMinFrame(paramInt1);
    setMaxFrame(paramInt2);
  }
  
  public void setMinAndMaxProgress(float paramFloat1, float paramFloat2)
  {
    setMinProgress(paramFloat1);
    setMaxProgress(paramFloat2);
  }
  
  public void setMinFrame(final int paramInt)
  {
    if (this.composition != null)
    {
      setMinProgress(paramInt / this.composition.getDurationFrames());
      return;
    }
    this.lazyCompositionTasks.add(new LazyCompositionTask()
    {
      public void run(LottieComposition paramAnonymousLottieComposition)
      {
        LottieDrawable.this.setMinFrame(paramInt);
      }
    });
  }
  
  public void setMinProgress(float paramFloat)
  {
    this.animator.setMinProgress(paramFloat);
  }
  
  public void setPerformanceTrackingEnabled(boolean paramBoolean)
  {
    this.performanceTrackingEnabled = paramBoolean;
    if (this.composition == null) {
      return;
    }
    this.composition.setPerformanceTrackingEnabled(paramBoolean);
  }
  
  public void setProgress(@FloatRange(from=0.0D, to=1.0D) float paramFloat)
  {
    this.animator.setProgress(paramFloat);
    if (this.compositionLayer == null) {
      return;
    }
    this.compositionLayer.setProgress(paramFloat);
  }
  
  public void setScale(float paramFloat)
  {
    this.scale = paramFloat;
    updateBounds();
  }
  
  public void setSpeed(float paramFloat)
  {
    this.speed = paramFloat;
    LottieValueAnimator localLottieValueAnimator = this.animator;
    if (paramFloat < 0.0F) {}
    for (boolean bool = true;; bool = false)
    {
      localLottieValueAnimator.setIsReversed(bool);
      if (this.composition != null) {
        break;
      }
      return;
    }
    this.animator.setDuration(((float)this.composition.getDuration() / Math.abs(paramFloat)));
  }
  
  public void setTextDelegate(TextDelegate paramTextDelegate)
  {
    this.textDelegate = paramTextDelegate;
  }
  
  void systemAnimationsAreDisabled()
  {
    this.systemAnimationsAreDisabled = true;
    this.animator.systemAnimationsAreDisabled();
  }
  
  public void unscheduleDrawable(@NonNull Drawable paramDrawable, @NonNull Runnable paramRunnable)
  {
    paramDrawable = getCallback();
    if (paramDrawable != null)
    {
      paramDrawable.unscheduleDrawable(this, paramRunnable);
      return;
    }
  }
  
  @Nullable
  public Bitmap updateBitmap(String paramString, @Nullable Bitmap paramBitmap)
  {
    ImageAssetManager localImageAssetManager = getImageAssetManager();
    if (localImageAssetManager != null)
    {
      paramString = localImageAssetManager.updateBitmap(paramString, paramBitmap);
      invalidateSelf();
      return paramString;
    }
    Log.w("LOTTIE", "Cannot update bitmap. Most likely the drawable is not added to a View which prevents Lottie from getting a Context.");
    return null;
  }
  
  public boolean useTextGlyphs()
  {
    if (this.textDelegate != null) {}
    while (this.composition.getCharacters().size() <= 0) {
      return false;
    }
    return true;
  }
  
  private static class ColorFilterData
  {
    @Nullable
    final ColorFilter colorFilter;
    @Nullable
    final String contentName;
    final String layerName;
    
    ColorFilterData(@Nullable String paramString1, @Nullable String paramString2, @Nullable ColorFilter paramColorFilter)
    {
      this.layerName = paramString1;
      this.contentName = paramString2;
      this.colorFilter = paramColorFilter;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (this != paramObject)
      {
        if (!(paramObject instanceof ColorFilterData)) {
          break label36;
        }
        paramObject = (ColorFilterData)paramObject;
        if (hashCode() == ((ColorFilterData)paramObject).hashCode()) {
          break label38;
        }
      }
      for (;;)
      {
        bool = false;
        label36:
        label38:
        do
        {
          return bool;
          return true;
          return false;
        } while (this.colorFilter == ((ColorFilterData)paramObject).colorFilter);
      }
    }
    
    public int hashCode()
    {
      int i = 17;
      if (this.layerName == null) {}
      while (this.contentName == null)
      {
        return i;
        i = this.layerName.hashCode() * 527;
      }
      return i * 31 * this.contentName.hashCode();
    }
  }
  
  private static abstract interface LazyCompositionTask
  {
    public abstract void run(LottieComposition paramLottieComposition);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\LottieDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */