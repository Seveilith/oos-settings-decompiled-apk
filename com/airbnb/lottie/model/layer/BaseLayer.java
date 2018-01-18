package com.airbnb.lottie.model.layer;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.CallSuper;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.util.Log;
import com.airbnb.lottie.L;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.PerformanceTracker;
import com.airbnb.lottie.animation.content.Content;
import com.airbnb.lottie.animation.content.DrawingContent;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation.AnimationListener;
import com.airbnb.lottie.animation.keyframe.FloatKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.MaskKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.StaticKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.TransformKeyframeAnimation;
import com.airbnb.lottie.model.animatable.AnimatableTransform;
import com.airbnb.lottie.model.content.Mask;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class BaseLayer
  implements DrawingContent, BaseKeyframeAnimation.AnimationListener
{
  private static final int SAVE_FLAGS = 19;
  private final List<BaseKeyframeAnimation<?, ?>> animations = new ArrayList();
  final Matrix boundsMatrix = new Matrix();
  private final Paint clearPaint = new Paint();
  private final Paint contentPaint = new Paint(1);
  private final String drawTraceName;
  final Layer layerModel;
  final LottieDrawable lottieDrawable;
  @Nullable
  private MaskKeyframeAnimation mask;
  private final RectF maskBoundsRect = new RectF();
  private final Paint maskPaint = new Paint(1);
  private final Matrix matrix = new Matrix();
  private final RectF matteBoundsRect = new RectF();
  @Nullable
  private BaseLayer matteLayer;
  private final Paint mattePaint = new Paint(1);
  @Nullable
  private BaseLayer parentLayer;
  private List<BaseLayer> parentLayers;
  private final Path path = new Path();
  private final RectF rect = new RectF();
  private final RectF tempMaskBoundsRect = new RectF();
  final TransformKeyframeAnimation transform;
  private boolean visible = true;
  
  BaseLayer(LottieDrawable paramLottieDrawable, Layer paramLayer)
  {
    this.lottieDrawable = paramLottieDrawable;
    this.layerModel = paramLayer;
    this.drawTraceName = (paramLayer.getName() + "#draw");
    this.clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    this.maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    if (paramLayer.getMatteType() != Layer.MatteType.Invert)
    {
      this.mattePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
      this.transform = paramLayer.getTransform().createAnimation();
      this.transform.addListener(this);
      this.transform.addAnimationsToLayer(this);
      if (paramLayer.getMasks() != null) {
        break label304;
      }
    }
    label304:
    while (paramLayer.getMasks().isEmpty())
    {
      setupInOutAnimations();
      return;
      this.mattePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
      break;
    }
    this.mask = new MaskKeyframeAnimation(paramLayer.getMasks());
    paramLottieDrawable = this.mask.getMaskAnimations().iterator();
    for (;;)
    {
      if (!paramLottieDrawable.hasNext())
      {
        paramLottieDrawable = this.mask.getOpacityAnimations().iterator();
        while (paramLottieDrawable.hasNext())
        {
          paramLayer = (BaseKeyframeAnimation)paramLottieDrawable.next();
          addAnimation(paramLayer);
          paramLayer.addUpdateListener(this);
        }
        break;
      }
      paramLayer = (BaseKeyframeAnimation)paramLottieDrawable.next();
      addAnimation(paramLayer);
      paramLayer.addUpdateListener(this);
    }
  }
  
  @SuppressLint({"WrongConstant"})
  private void applyMasks(Canvas paramCanvas, Matrix paramMatrix)
  {
    L.beginSection("Layer#drawMask");
    L.beginSection("Layer#saveLayer");
    paramCanvas.saveLayer(this.rect, this.maskPaint, 19);
    L.endSection("Layer#saveLayer");
    clearCanvas(paramCanvas);
    int j = this.mask.getMasks().size();
    int i = 0;
    if (i >= j)
    {
      L.beginSection("Layer#restoreLayer");
      paramCanvas.restore();
      L.endSection("Layer#restoreLayer");
      L.endSection("Layer#drawMask");
      return;
    }
    Object localObject = (Mask)this.mask.getMasks().get(i);
    Path localPath = (Path)((BaseKeyframeAnimation)this.mask.getMaskAnimations().get(i)).getValue();
    this.path.set(localPath);
    this.path.transform(paramMatrix);
    switch (localObject.getMaskMode())
    {
    case ???: 
    case ???: 
    case ???: 
    default: 
      this.path.setFillType(Path.FillType.WINDING);
    }
    for (;;)
    {
      localObject = (BaseKeyframeAnimation)this.mask.getOpacityAnimations().get(i);
      int k = this.contentPaint.getAlpha();
      this.contentPaint.setAlpha((int)(((Integer)((BaseKeyframeAnimation)localObject).getValue()).intValue() * 2.55F));
      paramCanvas.drawPath(this.path, this.contentPaint);
      this.contentPaint.setAlpha(k);
      i += 1;
      break;
      this.path.setFillType(Path.FillType.INVERSE_WINDING);
    }
  }
  
  private void buildParentLayerListIfNeeded()
  {
    if (this.parentLayers == null)
    {
      if (this.parentLayer == null) {
        break label36;
      }
      this.parentLayers = new ArrayList();
    }
    for (BaseLayer localBaseLayer = this.parentLayer;; localBaseLayer = localBaseLayer.parentLayer)
    {
      if (localBaseLayer == null)
      {
        return;
        return;
        label36:
        this.parentLayers = Collections.emptyList();
        return;
      }
      this.parentLayers.add(localBaseLayer);
    }
  }
  
  private void clearCanvas(Canvas paramCanvas)
  {
    L.beginSection("Layer#clearLayer");
    paramCanvas.drawRect(this.rect.left - 1.0F, this.rect.top - 1.0F, this.rect.right + 1.0F, 1.0F + this.rect.bottom, this.clearPaint);
    L.endSection("Layer#clearLayer");
  }
  
  @Nullable
  static BaseLayer forModel(Layer paramLayer, LottieDrawable paramLottieDrawable, LottieComposition paramLottieComposition)
  {
    switch (2.$SwitchMap$com$airbnb$lottie$model$layer$Layer$LayerType[paramLayer.getLayerType().ordinal()])
    {
    case 7: 
    default: 
      Log.w("LOTTIE", "Unknown layer type " + paramLayer.getLayerType());
      return null;
    case 1: 
      return new ShapeLayer(paramLottieDrawable, paramLayer);
    case 2: 
      return new CompositionLayer(paramLottieDrawable, paramLayer, paramLottieComposition.getPrecomps(paramLayer.getRefId()), paramLottieComposition);
    case 3: 
      return new SolidLayer(paramLottieDrawable, paramLayer);
    case 4: 
      return new ImageLayer(paramLottieDrawable, paramLayer, paramLottieComposition.getDpScale());
    case 5: 
      return new NullLayer(paramLottieDrawable, paramLayer);
    }
    return new TextLayer(paramLottieDrawable, paramLayer);
  }
  
  private void intersectBoundsWithMask(RectF paramRectF, Matrix paramMatrix)
  {
    this.maskBoundsRect.set(0.0F, 0.0F, 0.0F, 0.0F);
    int i;
    if (hasMasksOnThisLayer())
    {
      int j = this.mask.getMasks().size();
      i = 0;
      if (i >= j) {
        paramRectF.set(Math.max(paramRectF.left, this.maskBoundsRect.left), Math.max(paramRectF.top, this.maskBoundsRect.top), Math.min(paramRectF.right, this.maskBoundsRect.right), Math.min(paramRectF.bottom, this.maskBoundsRect.bottom));
      }
    }
    else
    {
      return;
    }
    Mask localMask = (Mask)this.mask.getMasks().get(i);
    Path localPath = (Path)((BaseKeyframeAnimation)this.mask.getMaskAnimations().get(i)).getValue();
    this.path.set(localPath);
    this.path.transform(paramMatrix);
    switch (localMask.getMaskMode())
    {
    case ???: 
    default: 
      this.path.computeBounds(this.tempMaskBoundsRect, false);
      if (i != 0) {
        this.maskBoundsRect.set(Math.min(this.maskBoundsRect.left, this.tempMaskBoundsRect.left), Math.min(this.maskBoundsRect.top, this.tempMaskBoundsRect.top), Math.max(this.maskBoundsRect.right, this.tempMaskBoundsRect.right), Math.max(this.maskBoundsRect.bottom, this.tempMaskBoundsRect.bottom));
      }
      break;
    }
    for (;;)
    {
      i += 1;
      break;
      return;
      return;
      return;
      this.maskBoundsRect.set(this.tempMaskBoundsRect);
    }
  }
  
  private void intersectBoundsWithMatte(RectF paramRectF, Matrix paramMatrix)
  {
    if (hasMatteOnThisLayer())
    {
      if (this.layerModel.getMatteType() != Layer.MatteType.Invert)
      {
        this.matteLayer.getBounds(this.matteBoundsRect, paramMatrix);
        paramRectF.set(Math.max(paramRectF.left, this.matteBoundsRect.left), Math.max(paramRectF.top, this.matteBoundsRect.top), Math.min(paramRectF.right, this.matteBoundsRect.right), Math.min(paramRectF.bottom, this.matteBoundsRect.bottom));
      }
    }
    else {}
  }
  
  private void invalidateSelf()
  {
    this.lottieDrawable.invalidateSelf();
  }
  
  private void recordRenderTime(float paramFloat)
  {
    this.lottieDrawable.getComposition().getPerformanceTracker().recordRenderTime(this.layerModel.getName(), paramFloat);
  }
  
  private void setVisible(boolean paramBoolean)
  {
    if (paramBoolean == this.visible) {
      return;
    }
    this.visible = paramBoolean;
    invalidateSelf();
  }
  
  private void setupInOutAnimations()
  {
    if (this.layerModel.getInOutKeyframes().isEmpty())
    {
      setVisible(true);
      return;
    }
    final FloatKeyframeAnimation localFloatKeyframeAnimation = new FloatKeyframeAnimation(this.layerModel.getInOutKeyframes());
    localFloatKeyframeAnimation.setIsDiscrete();
    localFloatKeyframeAnimation.addUpdateListener(new BaseKeyframeAnimation.AnimationListener()
    {
      public void onValueChanged()
      {
        BaseLayer localBaseLayer = BaseLayer.this;
        if (((Float)localFloatKeyframeAnimation.getValue()).floatValue() == 1.0F) {}
        for (boolean bool = true;; bool = false)
        {
          localBaseLayer.setVisible(bool);
          return;
        }
      }
    });
    if (((Float)localFloatKeyframeAnimation.getValue()).floatValue() == 1.0F) {}
    for (boolean bool = true;; bool = false)
    {
      setVisible(bool);
      addAnimation(localFloatKeyframeAnimation);
      return;
    }
  }
  
  public void addAnimation(BaseKeyframeAnimation<?, ?> paramBaseKeyframeAnimation)
  {
    if ((paramBaseKeyframeAnimation instanceof StaticKeyframeAnimation)) {
      return;
    }
    this.animations.add(paramBaseKeyframeAnimation);
  }
  
  public void addColorFilter(@Nullable String paramString1, @Nullable String paramString2, @Nullable ColorFilter paramColorFilter) {}
  
  @SuppressLint({"WrongConstant"})
  public void draw(Canvas paramCanvas, Matrix paramMatrix, int paramInt)
  {
    L.beginSection(this.drawTraceName);
    int i;
    if (this.visible)
    {
      buildParentLayerListIfNeeded();
      L.beginSection("Layer#parentMatrix");
      this.matrix.reset();
      this.matrix.set(paramMatrix);
      i = this.parentLayers.size() - 1;
      if (i >= 0) {
        break label309;
      }
      L.endSection("Layer#parentMatrix");
      float f = paramInt / 255.0F;
      paramInt = (int)(((Integer)this.transform.getOpacity().getValue()).intValue() * f / 100.0F * 255.0F);
      if (!hasMatteOnThisLayer()) {
        break label346;
      }
      label109:
      L.beginSection("Layer#computeBounds");
      this.rect.set(0.0F, 0.0F, 0.0F, 0.0F);
      getBounds(this.rect, this.matrix);
      intersectBoundsWithMatte(this.rect, this.matrix);
      this.matrix.preConcat(this.transform.getMatrix());
      intersectBoundsWithMask(this.rect, this.matrix);
      this.rect.set(0.0F, 0.0F, paramCanvas.getWidth(), paramCanvas.getHeight());
      L.endSection("Layer#computeBounds");
      L.beginSection("Layer#saveLayer");
      paramCanvas.saveLayer(this.rect, this.contentPaint, 31);
      L.endSection("Layer#saveLayer");
      clearCanvas(paramCanvas);
      L.beginSection("Layer#drawLayer");
      drawLayer(paramCanvas, this.matrix, paramInt);
      L.endSection("Layer#drawLayer");
      if (hasMasksOnThisLayer()) {
        break label403;
      }
      label264:
      if (hasMatteOnThisLayer()) {
        break label415;
      }
    }
    for (;;)
    {
      L.beginSection("Layer#restoreLayer");
      paramCanvas.restore();
      L.endSection("Layer#restoreLayer");
      recordRenderTime(L.endSection(this.drawTraceName));
      return;
      L.endSection(this.drawTraceName);
      return;
      label309:
      this.matrix.preConcat(((BaseLayer)this.parentLayers.get(i)).transform.getMatrix());
      i -= 1;
      break;
      label346:
      if (hasMasksOnThisLayer()) {
        break label109;
      }
      this.matrix.preConcat(this.transform.getMatrix());
      L.beginSection("Layer#drawLayer");
      drawLayer(paramCanvas, this.matrix, paramInt);
      L.endSection("Layer#drawLayer");
      recordRenderTime(L.endSection(this.drawTraceName));
      return;
      label403:
      applyMasks(paramCanvas, this.matrix);
      break label264;
      label415:
      L.beginSection("Layer#drawMatte");
      L.beginSection("Layer#saveLayer");
      paramCanvas.saveLayer(this.rect, this.mattePaint, 19);
      L.endSection("Layer#saveLayer");
      clearCanvas(paramCanvas);
      this.matteLayer.draw(paramCanvas, paramMatrix, paramInt);
      L.beginSection("Layer#restoreLayer");
      paramCanvas.restore();
      L.endSection("Layer#restoreLayer");
      L.endSection("Layer#drawMatte");
    }
  }
  
  abstract void drawLayer(Canvas paramCanvas, Matrix paramMatrix, int paramInt);
  
  @CallSuper
  public void getBounds(RectF paramRectF, Matrix paramMatrix)
  {
    this.boundsMatrix.set(paramMatrix);
    this.boundsMatrix.preConcat(this.transform.getMatrix());
  }
  
  Layer getLayerModel()
  {
    return this.layerModel;
  }
  
  public String getName()
  {
    return this.layerModel.getName();
  }
  
  boolean hasMasksOnThisLayer()
  {
    if (this.mask == null) {}
    while (this.mask.getMaskAnimations().isEmpty()) {
      return false;
    }
    return true;
  }
  
  boolean hasMatteOnThisLayer()
  {
    return this.matteLayer != null;
  }
  
  public void onValueChanged()
  {
    invalidateSelf();
  }
  
  public void setContents(List<Content> paramList1, List<Content> paramList2) {}
  
  void setMatteLayer(@Nullable BaseLayer paramBaseLayer)
  {
    this.matteLayer = paramBaseLayer;
  }
  
  void setParentLayer(@Nullable BaseLayer paramBaseLayer)
  {
    this.parentLayer = paramBaseLayer;
  }
  
  void setProgress(@FloatRange(from=0.0D, to=1.0D) float paramFloat)
  {
    float f = paramFloat;
    if (this.layerModel.getTimeStretch() != 0.0F) {
      f = paramFloat / this.layerModel.getTimeStretch();
    }
    int i;
    if (this.matteLayer == null) {
      i = 0;
    }
    for (;;)
    {
      if (i >= this.animations.size())
      {
        return;
        this.matteLayer.setProgress(f);
        break;
      }
      ((BaseKeyframeAnimation)this.animations.get(i)).setProgress(f);
      i += 1;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\layer\BaseLayer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */