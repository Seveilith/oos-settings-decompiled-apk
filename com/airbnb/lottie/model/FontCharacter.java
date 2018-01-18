package com.airbnb.lottie.model;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.model.content.ShapeGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class FontCharacter
{
  private final char character;
  private final String fontFamily;
  private final List<ShapeGroup> shapes;
  private final int size;
  private final String style;
  private final double width;
  
  FontCharacter(List<ShapeGroup> paramList, char paramChar, int paramInt, double paramDouble, String paramString1, String paramString2)
  {
    this.shapes = paramList;
    this.character = ((char)paramChar);
    this.size = paramInt;
    this.width = paramDouble;
    this.style = paramString1;
    this.fontFamily = paramString2;
  }
  
  public static int hashFor(char paramChar, String paramString1, String paramString2)
  {
    return ((paramChar + '\000') * 31 + paramString1.hashCode()) * 31 + paramString2.hashCode();
  }
  
  public List<ShapeGroup> getShapes()
  {
    return this.shapes;
  }
  
  int getSize()
  {
    return this.size;
  }
  
  String getStyle()
  {
    return this.style;
  }
  
  public double getWidth()
  {
    return this.width;
  }
  
  public int hashCode()
  {
    return hashFor(this.character, this.fontFamily, this.style);
  }
  
  public static class Factory
  {
    public static FontCharacter newInstance(JSONObject paramJSONObject, LottieComposition paramLottieComposition)
    {
      char c = paramJSONObject.optString("ch").charAt(0);
      int j = paramJSONObject.optInt("size");
      double d = paramJSONObject.optDouble("w");
      String str1 = paramJSONObject.optString("style");
      String str2 = paramJSONObject.optString("fFamily");
      Object localObject = paramJSONObject.optJSONObject("data");
      paramJSONObject = Collections.emptyList();
      if (localObject == null) {}
      for (;;)
      {
        return new FontCharacter(paramJSONObject, c, j, d, str1, str2);
        localObject = ((JSONObject)localObject).optJSONArray("shapes");
        if (localObject != null)
        {
          paramJSONObject = new ArrayList(((JSONArray)localObject).length());
          int i = 0;
          while (i < ((JSONArray)localObject).length())
          {
            paramJSONObject.add((ShapeGroup)ShapeGroup.shapeItemWithJson(((JSONArray)localObject).optJSONObject(i), paramLottieComposition));
            i += 1;
          }
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\FontCharacter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */