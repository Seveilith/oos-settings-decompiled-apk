package com.airbnb.lottie.model;

import android.graphics.PointF;

public class CubicCurveData
{
  private final PointF controlPoint1;
  private final PointF controlPoint2;
  private final PointF vertex;
  
  public CubicCurveData()
  {
    this.controlPoint1 = new PointF();
    this.controlPoint2 = new PointF();
    this.vertex = new PointF();
  }
  
  public CubicCurveData(PointF paramPointF1, PointF paramPointF2, PointF paramPointF3)
  {
    this.controlPoint1 = paramPointF1;
    this.controlPoint2 = paramPointF2;
    this.vertex = paramPointF3;
  }
  
  public PointF getControlPoint1()
  {
    return this.controlPoint1;
  }
  
  public PointF getControlPoint2()
  {
    return this.controlPoint2;
  }
  
  public PointF getVertex()
  {
    return this.vertex;
  }
  
  public void setControlPoint1(float paramFloat1, float paramFloat2)
  {
    this.controlPoint1.set(paramFloat1, paramFloat2);
  }
  
  public void setControlPoint2(float paramFloat1, float paramFloat2)
  {
    this.controlPoint2.set(paramFloat1, paramFloat2);
  }
  
  public void setVertex(float paramFloat1, float paramFloat2)
  {
    this.vertex.set(paramFloat1, paramFloat2);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\CubicCurveData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */