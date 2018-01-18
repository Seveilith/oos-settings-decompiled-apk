package com.airbnb.lottie.utils;

import android.graphics.PointF;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonUtils
{
  public static PointF pointFromJsonArray(JSONArray paramJSONArray, float paramFloat)
  {
    if (paramJSONArray.length() >= 2) {
      return new PointF((float)paramJSONArray.optDouble(0, 1.0D) * paramFloat, (float)paramJSONArray.optDouble(1, 1.0D) * paramFloat);
    }
    throw new IllegalArgumentException("Unable to parse point for " + paramJSONArray);
  }
  
  public static PointF pointFromJsonObject(JSONObject paramJSONObject, float paramFloat)
  {
    return new PointF(valueFromObject(paramJSONObject.opt("x")) * paramFloat, valueFromObject(paramJSONObject.opt("y")) * paramFloat);
  }
  
  public static float valueFromObject(Object paramObject)
  {
    if (!(paramObject instanceof Float))
    {
      if (!(paramObject instanceof Integer))
      {
        if ((paramObject instanceof Double)) {
          break label47;
        }
        if ((paramObject instanceof JSONArray)) {
          break label56;
        }
        return 0.0F;
      }
    }
    else {
      return ((Float)paramObject).floatValue();
    }
    return ((Integer)paramObject).intValue();
    label47:
    return (float)((Double)paramObject).doubleValue();
    label56:
    return (float)((JSONArray)paramObject).optDouble(0);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\utils\JsonUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */