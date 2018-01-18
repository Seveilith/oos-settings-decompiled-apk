package com.airbnb.lottie.animation;

import android.graphics.PointF;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.model.animatable.AnimatableValue.Factory;
import com.airbnb.lottie.utils.JsonUtils;
import com.airbnb.lottie.utils.MiscUtils;
import com.airbnb.lottie.utils.Utils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class Keyframe<T>
{
  private static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
  private static final float MAX_CP_VALUE = 100.0F;
  private final LottieComposition composition;
  @Nullable
  public Float endFrame;
  private float endProgress = Float.MIN_VALUE;
  @Nullable
  public final T endValue;
  @Nullable
  public final Interpolator interpolator;
  public final float startFrame;
  private float startProgress = Float.MIN_VALUE;
  @Nullable
  public final T startValue;
  
  public Keyframe(LottieComposition paramLottieComposition, @Nullable T paramT1, @Nullable T paramT2, @Nullable Interpolator paramInterpolator, float paramFloat, @Nullable Float paramFloat1)
  {
    this.composition = paramLottieComposition;
    this.startValue = paramT1;
    this.endValue = paramT2;
    this.interpolator = paramInterpolator;
    this.startFrame = paramFloat;
    this.endFrame = paramFloat1;
  }
  
  public static void setEndFrames(List<? extends Keyframe<?>> paramList)
  {
    int j = paramList.size();
    int i = 0;
    Keyframe localKeyframe;
    for (;;)
    {
      if (i >= j - 1)
      {
        localKeyframe = (Keyframe)paramList.get(j - 1);
        if (localKeyframe.startValue == null) {
          break;
        }
        return;
      }
      ((Keyframe)paramList.get(i)).endFrame = Float.valueOf(((Keyframe)paramList.get(i + 1)).startFrame);
      i += 1;
    }
    paramList.remove(localKeyframe);
  }
  
  public boolean containsProgress(@FloatRange(from=0.0D, to=1.0D) float paramFloat)
  {
    return (paramFloat >= getStartProgress()) && (paramFloat <= getEndProgress());
  }
  
  public float getEndProgress()
  {
    if (this.endProgress == Float.MIN_VALUE) {
      if (this.endFrame == null) {
        break label51;
      }
    }
    label51:
    for (this.endProgress = (getStartProgress() + (this.endFrame.floatValue() - this.startFrame) / this.composition.getDurationFrames());; this.endProgress = 1.0F) {
      return this.endProgress;
    }
  }
  
  public float getStartProgress()
  {
    if (this.startProgress == Float.MIN_VALUE) {
      this.startProgress = ((this.startFrame - (float)this.composition.getStartFrame()) / this.composition.getDurationFrames());
    }
    return this.startProgress;
  }
  
  public boolean isStatic()
  {
    return this.interpolator == null;
  }
  
  public String toString()
  {
    return "Keyframe{startValue=" + this.startValue + ", endValue=" + this.endValue + ", startFrame=" + this.startFrame + ", endFrame=" + this.endFrame + ", interpolator=" + this.interpolator + '}';
  }
  
  public static class Factory
  {
    private static SparseArrayCompat<WeakReference<Interpolator>> pathInterpolatorCache;
    
    @Nullable
    private static WeakReference<Interpolator> getInterpolator(int paramInt)
    {
      try
      {
        WeakReference localWeakReference = (WeakReference)pathInterpolatorCache().get(paramInt);
        return localWeakReference;
      }
      finally {}
    }
    
    public static <T> Keyframe<T> newInstance(JSONObject paramJSONObject, LottieComposition paramLottieComposition, float paramFloat, AnimatableValue.Factory<T> paramFactory)
    {
      Object localObject2;
      if (!paramJSONObject.has("t"))
      {
        localObject1 = paramFactory.valueFromObject(paramJSONObject, paramFloat);
        localObject2 = null;
        paramJSONObject = (JSONObject)localObject1;
        paramFloat = 0.0F;
        paramFactory = paramJSONObject;
        return new Keyframe(paramLottieComposition, paramFactory, localObject1, (Interpolator)localObject2, paramFloat, null);
      }
      float f = (float)paramJSONObject.optDouble("t", 0.0D);
      Object localObject1 = paramJSONObject.opt("s");
      label71:
      label86:
      Object localObject3;
      label107:
      label113:
      int i;
      if (localObject1 == null)
      {
        localObject1 = null;
        localObject2 = paramJSONObject.opt("e");
        if (localObject2 != null) {
          break label174;
        }
        paramFactory = null;
        localObject3 = paramJSONObject.optJSONObject("o");
        localObject2 = paramJSONObject.optJSONObject("i");
        if (localObject3 != null) {
          break label187;
        }
        localObject2 = null;
        localObject3 = null;
        if (paramJSONObject.optInt("h", 0) == 1) {
          break label211;
        }
        i = 0;
        label127:
        if (i != 0) {
          break label217;
        }
        if (localObject3 != null) {
          break label227;
        }
        paramJSONObject = Keyframe.LINEAR_INTERPOLATOR;
      }
      for (;;)
      {
        localObject2 = paramFactory;
        paramFloat = f;
        paramFactory = (AnimatableValue.Factory<T>)localObject1;
        localObject1 = localObject2;
        localObject2 = paramJSONObject;
        break;
        localObject1 = paramFactory.valueFromObject(localObject1, paramFloat);
        break label71;
        label174:
        paramFactory = paramFactory.valueFromObject(localObject2, paramFloat);
        break label86;
        label187:
        if (localObject2 == null) {
          break label107;
        }
        localObject3 = JsonUtils.pointFromJsonObject((JSONObject)localObject3, paramFloat);
        localObject2 = JsonUtils.pointFromJsonObject((JSONObject)localObject2, paramFloat);
        break label113;
        label211:
        i = 1;
        break label127;
        label217:
        paramJSONObject = Keyframe.LINEAR_INTERPOLATOR;
        paramFactory = (AnimatableValue.Factory<T>)localObject1;
        continue;
        label227:
        ((PointF)localObject3).x = MiscUtils.clamp(((PointF)localObject3).x, -paramFloat, paramFloat);
        ((PointF)localObject3).y = MiscUtils.clamp(((PointF)localObject3).y, -100.0F, 100.0F);
        ((PointF)localObject2).x = MiscUtils.clamp(((PointF)localObject2).x, -paramFloat, paramFloat);
        ((PointF)localObject2).y = MiscUtils.clamp(((PointF)localObject2).y, -100.0F, 100.0F);
        i = Utils.hashFor(((PointF)localObject3).x, ((PointF)localObject3).y, ((PointF)localObject2).x, ((PointF)localObject2).y);
        WeakReference localWeakReference = getInterpolator(i);
        if (localWeakReference == null)
        {
          paramJSONObject = null;
          label332:
          if (localWeakReference != null) {
            break label402;
          }
        }
        label402:
        while (paramJSONObject == null)
        {
          paramJSONObject = PathInterpolatorCompat.create(((PointF)localObject3).x / paramFloat, ((PointF)localObject3).y / paramFloat, ((PointF)localObject2).x / paramFloat, ((PointF)localObject2).y / paramFloat);
          try
          {
            putInterpolator(i, new WeakReference(paramJSONObject));
          }
          catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
          break;
          paramJSONObject = (Interpolator)localWeakReference.get();
          break label332;
        }
      }
    }
    
    public static <T> List<Keyframe<T>> parseKeyframes(JSONArray paramJSONArray, LottieComposition paramLottieComposition, float paramFloat, AnimatableValue.Factory<T> paramFactory)
    {
      int i = 0;
      int j = paramJSONArray.length();
      ArrayList localArrayList;
      if (j != 0) {
        localArrayList = new ArrayList();
      }
      for (;;)
      {
        if (i >= j)
        {
          Keyframe.setEndFrames(localArrayList);
          return localArrayList;
          return Collections.emptyList();
        }
        localArrayList.add(newInstance(paramJSONArray.optJSONObject(i), paramLottieComposition, paramFloat, paramFactory));
        i += 1;
      }
    }
    
    private static SparseArrayCompat<WeakReference<Interpolator>> pathInterpolatorCache()
    {
      if (pathInterpolatorCache != null) {}
      for (;;)
      {
        return pathInterpolatorCache;
        pathInterpolatorCache = new SparseArrayCompat();
      }
    }
    
    private static void putInterpolator(int paramInt, WeakReference<Interpolator> paramWeakReference)
    {
      try
      {
        pathInterpolatorCache.put(paramInt, paramWeakReference);
        return;
      }
      finally {}
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\Keyframe.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */