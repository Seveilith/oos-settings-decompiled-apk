package com.airbnb.lottie;

import org.json.JSONObject;

public class LottieImageAsset
{
  private final String fileName;
  private final int height;
  private final String id;
  private final int width;
  
  private LottieImageAsset(int paramInt1, int paramInt2, String paramString1, String paramString2)
  {
    this.width = paramInt1;
    this.height = paramInt2;
    this.id = paramString1;
    this.fileName = paramString2;
  }
  
  public String getFileName()
  {
    return this.fileName;
  }
  
  public int getHeight()
  {
    return this.height;
  }
  
  public String getId()
  {
    return this.id;
  }
  
  public int getWidth()
  {
    return this.width;
  }
  
  static class Factory
  {
    static LottieImageAsset newInstance(JSONObject paramJSONObject)
    {
      return new LottieImageAsset(paramJSONObject.optInt("w"), paramJSONObject.optInt("h"), paramJSONObject.optString("id"), paramJSONObject.optString("p"), null);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\LottieImageAsset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */