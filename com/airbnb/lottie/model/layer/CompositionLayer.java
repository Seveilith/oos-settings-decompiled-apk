package com.airbnb.lottie.model.layer;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;
import com.airbnb.lottie.L;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import java.util.ArrayList;
import java.util.List;

public class CompositionLayer
  extends BaseLayer
{
  @Nullable
  private Boolean hasMasks;
  @Nullable
  private Boolean hasMatte;
  private final List<BaseLayer> layers = new ArrayList();
  private final RectF newClipRect = new RectF();
  private final RectF rect = new RectF();
  @Nullable
  private final BaseKeyframeAnimation<Float, Float> timeRemapping;
  
  public CompositionLayer(LottieDrawable paramLottieDrawable, Layer paramLayer, List<Layer> paramList, LottieComposition paramLottieComposition)
  {
    super(paramLottieDrawable, paramLayer);
    paramLayer = paramLayer.getTimeRemapping();
    if (paramLayer == null) {
      this.timeRemapping = null;
    }
    LongSparseArray localLongSparseArray;
    int i;
    for (;;)
    {
      localLongSparseArray = new LongSparseArray(paramLottieComposition.getLayers().size());
      i = paramList.size() - 1;
      paramLayer = null;
      if (i >= 0) {
        break;
      }
      i = 0;
      if (i < localLongSparseArray.size()) {
        break label253;
      }
      return;
      this.timeRemapping = paramLayer.createAnimation();
      addAnimation(this.timeRemapping);
      this.timeRemapping.addUpdateListener(this);
    }
    Layer localLayer = (Layer)paramList.get(i);
    BaseLayer localBaseLayer = BaseLayer.forModel(localLayer, paramLottieDrawable, paramLottieComposition);
    if (localBaseLayer != null)
    {
      localLongSparseArray.put(localBaseLayer.getLayerModel().getId(), localBaseLayer);
      if (paramLayer != null) {
        break label236;
      }
      this.layers.add(0, localBaseLayer);
      switch (localLayer.getMatteType())
      {
      }
    }
    for (;;)
    {
      i -= 1;
      break;
      continue;
      label236:
      paramLayer.setMatteLayer(localBaseLayer);
      paramLayer = null;
      continue;
      paramLayer = localBaseLayer;
    }
    label253:
    paramLottieDrawable = (BaseLayer)localLongSparseArray.get(localLongSparseArray.keyAt(i));
    paramLayer = (BaseLayer)localLongSparseArray.get(paramLottieDrawable.getLayerModel().getParentId());
    if (paramLayer == null) {}
    for (;;)
    {
      i += 1;
      break;
      paramLottieDrawable.setParentLayer(paramLayer);
    }
  }
  
  public void addColorFilter(@Nullable String paramString1, @Nullable String paramString2, @Nullable ColorFilter paramColorFilter)
  {
    int i = 0;
    if (i >= this.layers.size()) {
      return;
    }
    BaseLayer localBaseLayer = (BaseLayer)this.layers.get(i);
    String str = localBaseLayer.getLayerModel().getName();
    if (paramString1 != null) {
      if (str.equals(paramString1)) {
        break label77;
      }
    }
    for (;;)
    {
      i += 1;
      break;
      localBaseLayer.addColorFilter(null, null, paramColorFilter);
      continue;
      label77:
      localBaseLayer.addColorFilter(paramString1, paramString2, paramColorFilter);
    }
  }
  
  void drawLayer(Canvas paramCanvas, Matrix paramMatrix, int paramInt)
  {
    L.beginSection("CompositionLayer#draw");
    paramCanvas.save();
    this.newClipRect.set(0.0F, 0.0F, this.layerModel.getPreCompWidth(), this.layerModel.getPreCompHeight());
    paramMatrix.mapRect(this.newClipRect);
    int i = this.layers.size() - 1;
    if (i < 0)
    {
      paramCanvas.restore();
      L.endSection("CompositionLayer#draw");
      return;
    }
    boolean bool = true;
    if (this.newClipRect.isEmpty()) {
      label86:
      if (bool) {
        break label113;
      }
    }
    for (;;)
    {
      i -= 1;
      break;
      bool = paramCanvas.clipRect(this.newClipRect);
      break label86;
      label113:
      ((BaseLayer)this.layers.get(i)).draw(paramCanvas, paramMatrix, paramInt);
    }
  }
  
  public void getBounds(RectF paramRectF, Matrix paramMatrix)
  {
    super.getBounds(paramRectF, paramMatrix);
    this.rect.set(0.0F, 0.0F, 0.0F, 0.0F);
    int i = this.layers.size() - 1;
    if (i < 0) {
      return;
    }
    ((BaseLayer)this.layers.get(i)).getBounds(this.rect, this.boundsMatrix);
    if (!paramRectF.isEmpty()) {
      paramRectF.set(Math.min(paramRectF.left, this.rect.left), Math.min(paramRectF.top, this.rect.top), Math.max(paramRectF.right, this.rect.right), Math.max(paramRectF.bottom, this.rect.bottom));
    }
    for (;;)
    {
      i -= 1;
      break;
      paramRectF.set(this.rect);
    }
  }
  
  public boolean hasMasks()
  {
    if (this.hasMasks != null) {}
    int i;
    for (;;)
    {
      return this.hasMasks.booleanValue();
      i = this.layers.size() - 1;
      if (i >= 0) {
        break;
      }
      this.hasMasks = Boolean.valueOf(false);
    }
    BaseLayer localBaseLayer = (BaseLayer)this.layers.get(i);
    if (!(localBaseLayer instanceof ShapeLayer)) {
      if ((localBaseLayer instanceof CompositionLayer)) {
        break label94;
      }
    }
    label94:
    while (!((CompositionLayer)localBaseLayer).hasMasks())
    {
      do
      {
        i -= 1;
        break;
      } while (!localBaseLayer.hasMasksOnThisLayer());
      this.hasMasks = Boolean.valueOf(true);
      return true;
    }
    this.hasMasks = Boolean.valueOf(true);
    return true;
  }
  
  public boolean hasMatte()
  {
    if (this.hasMatte != null) {
      return this.hasMatte.booleanValue();
    }
    int i;
    if (!hasMatteOnThisLayer()) {
      i = this.layers.size() - 1;
    }
    for (;;)
    {
      if (i < 0)
      {
        this.hasMatte = Boolean.valueOf(false);
        break;
        this.hasMatte = Boolean.valueOf(true);
        return true;
      }
      if (((BaseLayer)this.layers.get(i)).hasMatteOnThisLayer()) {
        break label85;
      }
      i -= 1;
    }
    label85:
    this.hasMatte = Boolean.valueOf(true);
    return true;
  }
  
  public void setProgress(@FloatRange(from=0.0D, to=1.0D) float paramFloat)
  {
    super.setProgress(paramFloat);
    float f;
    int i;
    if (this.timeRemapping == null)
    {
      f = paramFloat;
      if (this.layerModel.getTimeStretch() != 0.0F) {
        f = paramFloat / this.layerModel.getTimeStretch();
      }
      paramFloat = this.layerModel.getStartProgress();
      i = this.layers.size() - 1;
    }
    for (;;)
    {
      if (i < 0)
      {
        return;
        long l = this.lottieDrawable.getComposition().getDuration();
        paramFloat = (float)(((Float)this.timeRemapping.getValue()).floatValue() * 1000.0F) / (float)l;
        break;
      }
      ((BaseLayer)this.layers.get(i)).setProgress(f - paramFloat);
      i -= 1;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\layer\CompositionLayer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */