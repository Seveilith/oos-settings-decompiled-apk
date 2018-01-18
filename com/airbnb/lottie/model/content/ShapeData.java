package com.airbnb.lottie.model.content;

import android.graphics.PointF;
import android.support.annotation.FloatRange;
import com.airbnb.lottie.model.CubicCurveData;
import com.airbnb.lottie.model.animatable.AnimatableValue.Factory;
import com.airbnb.lottie.utils.MiscUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class ShapeData
{
  private boolean closed;
  private final List<CubicCurveData> curves = new ArrayList();
  private PointF initialPoint;
  
  public ShapeData() {}
  
  private ShapeData(PointF paramPointF, boolean paramBoolean, List<CubicCurveData> paramList)
  {
    this.initialPoint = paramPointF;
    this.closed = paramBoolean;
    this.curves.addAll(paramList);
  }
  
  private void setInitialPoint(float paramFloat1, float paramFloat2)
  {
    if (this.initialPoint != null) {}
    for (;;)
    {
      this.initialPoint.set(paramFloat1, paramFloat2);
      return;
      this.initialPoint = new PointF();
    }
  }
  
  public List<CubicCurveData> getCurves()
  {
    return this.curves;
  }
  
  public PointF getInitialPoint()
  {
    return this.initialPoint;
  }
  
  public void interpolateBetween(ShapeData paramShapeData1, ShapeData paramShapeData2, @FloatRange(from=0.0D, to=1.0D) float paramFloat)
  {
    boolean bool = false;
    label17:
    label20:
    label38:
    PointF localPointF1;
    PointF localPointF2;
    int i;
    if (this.initialPoint != null)
    {
      if (!paramShapeData1.isClosed()) {
        break label127;
      }
      bool = true;
      this.closed = bool;
      if (!this.curves.isEmpty()) {
        break label137;
      }
      if (this.curves.isEmpty()) {
        break label248;
      }
      localPointF1 = paramShapeData1.getInitialPoint();
      localPointF2 = paramShapeData2.getInitialPoint();
      setInitialPoint(MiscUtils.lerp(localPointF1.x, localPointF2.x, paramFloat), MiscUtils.lerp(localPointF1.y, localPointF2.y, paramFloat));
      i = this.curves.size() - 1;
    }
    for (;;)
    {
      if (i < 0)
      {
        return;
        this.initialPoint = new PointF();
        break;
        label127:
        if (paramShapeData2.isClosed()) {
          break label17;
        }
        break label20;
        label137:
        if ((this.curves.size() == paramShapeData1.getCurves().size()) || (this.curves.size() == paramShapeData2.getCurves().size())) {
          break label38;
        }
        throw new IllegalStateException("Curves must have the same number of control points. This: " + getCurves().size() + "\tShape 1: " + paramShapeData1.getCurves().size() + "\tShape 2: " + paramShapeData2.getCurves().size());
        label248:
        i = paramShapeData1.getCurves().size();
        for (;;)
        {
          i -= 1;
          if (i < 0) {
            break;
          }
          this.curves.add(new CubicCurveData());
        }
      }
      Object localObject2 = (CubicCurveData)paramShapeData1.getCurves().get(i);
      Object localObject1 = (CubicCurveData)paramShapeData2.getCurves().get(i);
      localPointF1 = ((CubicCurveData)localObject2).getControlPoint1();
      localPointF2 = ((CubicCurveData)localObject2).getControlPoint2();
      localObject2 = ((CubicCurveData)localObject2).getVertex();
      PointF localPointF3 = ((CubicCurveData)localObject1).getControlPoint1();
      PointF localPointF4 = ((CubicCurveData)localObject1).getControlPoint2();
      localObject1 = ((CubicCurveData)localObject1).getVertex();
      ((CubicCurveData)this.curves.get(i)).setControlPoint1(MiscUtils.lerp(localPointF1.x, localPointF3.x, paramFloat), MiscUtils.lerp(localPointF1.y, localPointF3.y, paramFloat));
      ((CubicCurveData)this.curves.get(i)).setControlPoint2(MiscUtils.lerp(localPointF2.x, localPointF4.x, paramFloat), MiscUtils.lerp(localPointF2.y, localPointF4.y, paramFloat));
      ((CubicCurveData)this.curves.get(i)).setVertex(MiscUtils.lerp(((PointF)localObject2).x, ((PointF)localObject1).x, paramFloat), MiscUtils.lerp(((PointF)localObject2).y, ((PointF)localObject1).y, paramFloat));
      i -= 1;
    }
  }
  
  public boolean isClosed()
  {
    return this.closed;
  }
  
  public String toString()
  {
    return "ShapeData{numCurves=" + this.curves.size() + "closed=" + this.closed + '}';
  }
  
  public static class Factory
    implements AnimatableValue.Factory<ShapeData>
  {
    public static final Factory INSTANCE = new Factory();
    
    private static PointF vertexAtIndex(int paramInt, JSONArray paramJSONArray)
    {
      Object localObject;
      float f1;
      if (paramInt < paramJSONArray.length())
      {
        localObject = paramJSONArray.optJSONArray(paramInt);
        paramJSONArray = ((JSONArray)localObject).opt(0);
        localObject = ((JSONArray)localObject).opt(1);
        if ((paramJSONArray instanceof Double)) {
          break label118;
        }
        f1 = ((Integer)paramJSONArray).intValue();
        if ((localObject instanceof Double)) {
          break label129;
        }
      }
      label118:
      label129:
      for (float f2 = ((Integer)localObject).intValue();; f2 = ((Double)localObject).floatValue())
      {
        return new PointF(f1, f2);
        throw new IllegalArgumentException("Invalid index " + paramInt + ". There are only " + paramJSONArray.length() + " points.");
        f1 = ((Double)paramJSONArray).floatValue();
        break;
      }
    }
    
    public ShapeData valueFromObject(Object paramObject, float paramFloat)
    {
      Object localObject1 = null;
      Object localObject4;
      Object localObject2;
      Object localObject3;
      boolean bool;
      if (!(paramObject instanceof JSONArray))
      {
        if ((paramObject instanceof JSONObject)) {
          break label136;
        }
        if (localObject1 == null) {
          break label157;
        }
        localObject4 = ((JSONObject)localObject1).optJSONArray("v");
        localObject2 = ((JSONObject)localObject1).optJSONArray("i");
        localObject3 = ((JSONObject)localObject1).optJSONArray("o");
        bool = ((JSONObject)localObject1).optBoolean("c", false);
        if (localObject4 != null) {
          break label159;
        }
      }
      label108:
      label136:
      label157:
      label159:
      while ((localObject2 == null) || (localObject3 == null) || (((JSONArray)localObject4).length() != ((JSONArray)localObject2).length()) || (((JSONArray)localObject4).length() != ((JSONArray)localObject3).length()))
      {
        throw new IllegalStateException("Unable to process points array or tangents. " + localObject1);
        paramObject = ((JSONArray)paramObject).opt(0);
        if (!(paramObject instanceof JSONObject)) {}
        for (paramObject = null;; paramObject = (JSONObject)paramObject)
        {
          localObject1 = paramObject;
          break;
          if (!((JSONObject)paramObject).has("v")) {
            break label108;
          }
        }
        if (!((JSONObject)paramObject).has("v")) {
          break;
        }
        localObject1 = (JSONObject)paramObject;
        break;
        return null;
      }
      int j;
      int i;
      if (((JSONArray)localObject4).length() != 0)
      {
        j = ((JSONArray)localObject4).length();
        paramObject = vertexAtIndex(0, (JSONArray)localObject4);
        ((PointF)paramObject).x *= paramFloat;
        ((PointF)paramObject).y *= paramFloat;
        localObject1 = new ArrayList(j);
        i = 1;
        if (i < j) {
          break label295;
        }
        if (bool) {
          break label449;
        }
      }
      for (;;)
      {
        return new ShapeData((PointF)paramObject, bool, (List)localObject1, null);
        return new ShapeData(new PointF(), false, Collections.emptyList(), null);
        label295:
        PointF localPointF1 = vertexAtIndex(i, (JSONArray)localObject4);
        PointF localPointF3 = vertexAtIndex(i - 1, (JSONArray)localObject4);
        PointF localPointF4 = vertexAtIndex(i - 1, (JSONArray)localObject3);
        PointF localPointF2 = vertexAtIndex(i, (JSONArray)localObject2);
        localPointF3 = MiscUtils.addPoints(localPointF3, localPointF4);
        localPointF2 = MiscUtils.addPoints(localPointF1, localPointF2);
        localPointF3.x *= paramFloat;
        localPointF3.y *= paramFloat;
        localPointF2.x *= paramFloat;
        localPointF2.y *= paramFloat;
        localPointF1.x *= paramFloat;
        localPointF1.y *= paramFloat;
        ((List)localObject1).add(new CubicCurveData(localPointF3, localPointF2, localPointF1));
        i += 1;
        break;
        label449:
        localPointF1 = vertexAtIndex(0, (JSONArray)localObject4);
        localObject4 = vertexAtIndex(j - 1, (JSONArray)localObject4);
        localObject3 = vertexAtIndex(j - 1, (JSONArray)localObject3);
        localObject2 = vertexAtIndex(0, (JSONArray)localObject2);
        localObject3 = MiscUtils.addPoints((PointF)localObject4, (PointF)localObject3);
        localObject2 = MiscUtils.addPoints(localPointF1, (PointF)localObject2);
        if (paramFloat != 1.0F)
        {
          ((PointF)localObject3).x *= paramFloat;
          ((PointF)localObject3).y *= paramFloat;
          ((PointF)localObject2).x *= paramFloat;
          ((PointF)localObject2).y *= paramFloat;
          localPointF1.x *= paramFloat;
          localPointF1.y *= paramFloat;
        }
        ((List)localObject1).add(new CubicCurveData((PointF)localObject3, (PointF)localObject2, localPointF1));
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\content\ShapeData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */