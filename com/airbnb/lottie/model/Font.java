package com.airbnb.lottie.model;

import org.json.JSONObject;

public class Font
{
  private final float ascent;
  private final String family;
  private final String name;
  private final String style;
  
  Font(String paramString1, String paramString2, String paramString3, float paramFloat)
  {
    this.family = paramString1;
    this.name = paramString2;
    this.style = paramString3;
    this.ascent = paramFloat;
  }
  
  float getAscent()
  {
    return this.ascent;
  }
  
  public String getFamily()
  {
    return this.family;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String getStyle()
  {
    return this.style;
  }
  
  public static class Factory
  {
    public static Font newInstance(JSONObject paramJSONObject)
    {
      return new Font(paramJSONObject.optString("fFamily"), paramJSONObject.optString("fName"), paramJSONObject.optString("fStyle"), (float)paramJSONObject.optDouble("ascent"));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\Font.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */