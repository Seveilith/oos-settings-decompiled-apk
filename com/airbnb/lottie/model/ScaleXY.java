package com.airbnb.lottie.model;

import com.airbnb.lottie.model.animatable.AnimatableValue.Factory;
import org.json.JSONArray;

public class ScaleXY
{
  private final float scaleX;
  private final float scaleY;
  
  public ScaleXY()
  {
    this(1.0F, 1.0F);
  }
  
  public ScaleXY(float paramFloat1, float paramFloat2)
  {
    this.scaleX = paramFloat1;
    this.scaleY = paramFloat2;
  }
  
  public float getScaleX()
  {
    return this.scaleX;
  }
  
  public float getScaleY()
  {
    return this.scaleY;
  }
  
  public String toString()
  {
    return getScaleX() + "x" + getScaleY();
  }
  
  public static class Factory
    implements AnimatableValue.Factory<ScaleXY>
  {
    public static final Factory INSTANCE = new Factory();
    
    public ScaleXY valueFromObject(Object paramObject, float paramFloat)
    {
      paramObject = (JSONArray)paramObject;
      return new ScaleXY((float)((JSONArray)paramObject).optDouble(0, 1.0D) / 100.0F * paramFloat, (float)((JSONArray)paramObject).optDouble(1, 1.0D) / 100.0F * paramFloat);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\ScaleXY.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */