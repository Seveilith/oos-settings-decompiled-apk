package com.airbnb.lottie.model;

import android.graphics.PointF;
import com.airbnb.lottie.model.animatable.AnimatableValue.Factory;
import com.airbnb.lottie.utils.JsonUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class PointFFactory
  implements AnimatableValue.Factory<PointF>
{
  public static final PointFFactory INSTANCE = new PointFFactory();
  
  public PointF valueFromObject(Object paramObject, float paramFloat)
  {
    if (!(paramObject instanceof JSONArray))
    {
      if (!(paramObject instanceof JSONObject)) {
        throw new IllegalArgumentException("Unable to parse point from " + paramObject);
      }
    }
    else {
      return JsonUtils.pointFromJsonArray((JSONArray)paramObject, paramFloat);
    }
    return JsonUtils.pointFromJsonObject((JSONObject)paramObject, paramFloat);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\PointFFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */