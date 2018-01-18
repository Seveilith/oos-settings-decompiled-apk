package com.airbnb.lottie.model;

import android.graphics.Color;
import com.airbnb.lottie.model.animatable.AnimatableValue.Factory;
import org.json.JSONArray;

public class ColorFactory
  implements AnimatableValue.Factory<Integer>
{
  public static final ColorFactory INSTANCE = new ColorFactory();
  
  public Integer valueFromObject(Object paramObject, float paramFloat)
  {
    paramObject = (JSONArray)paramObject;
    if (((JSONArray)paramObject).length() != 4) {
      return Integer.valueOf(-16777216);
    }
    int i = 0;
    int j = 1;
    if (i >= ((JSONArray)paramObject).length()) {
      if (j != 0) {
        break label102;
      }
    }
    label102:
    for (paramFloat = 1.0F;; paramFloat = 255.0F)
    {
      return Integer.valueOf(Color.argb((int)(((JSONArray)paramObject).optDouble(3) * paramFloat), (int)(((JSONArray)paramObject).optDouble(0) * paramFloat), (int)(((JSONArray)paramObject).optDouble(1) * paramFloat), (int)(((JSONArray)paramObject).optDouble(2) * paramFloat)));
      if (((JSONArray)paramObject).optDouble(i) > 1.0D) {
        j = 0;
      }
      i += 1;
      break;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\ColorFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */