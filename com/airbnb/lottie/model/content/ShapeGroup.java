package com.airbnb.lottie.model.content;

import android.support.annotation.Nullable;
import android.util.Log;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.animation.content.Content;
import com.airbnb.lottie.animation.content.ContentGroup;
import com.airbnb.lottie.model.animatable.AnimatableTransform.Factory;
import com.airbnb.lottie.model.layer.BaseLayer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class ShapeGroup
  implements ContentModel
{
  private final List<ContentModel> items;
  private final String name;
  
  public ShapeGroup(String paramString, List<ContentModel> paramList)
  {
    this.name = paramString;
    this.items = paramList;
  }
  
  @Nullable
  public static ContentModel shapeItemWithJson(JSONObject paramJSONObject, LottieComposition paramLottieComposition)
  {
    String str = paramJSONObject.optString("ty");
    int i = -1;
    switch (str.hashCode())
    {
    }
    for (;;)
    {
      switch (i)
      {
      default: 
        Log.w("LOTTIE", "Unknown shape type " + str);
        return null;
        if (str.equals("gr"))
        {
          i = 0;
          continue;
          if (str.equals("st"))
          {
            i = 1;
            continue;
            if (str.equals("gs"))
            {
              i = 2;
              continue;
              if (str.equals("fl"))
              {
                i = 3;
                continue;
                if (str.equals("gf"))
                {
                  i = 4;
                  continue;
                  if (str.equals("tr"))
                  {
                    i = 5;
                    continue;
                    if (str.equals("sh"))
                    {
                      i = 6;
                      continue;
                      if (str.equals("el"))
                      {
                        i = 7;
                        continue;
                        if (str.equals("rc"))
                        {
                          i = 8;
                          continue;
                          if (str.equals("tm"))
                          {
                            i = 9;
                            continue;
                            if (str.equals("sr"))
                            {
                              i = 10;
                              continue;
                              if (str.equals("mm"))
                              {
                                i = 11;
                                continue;
                                if (str.equals("rp")) {
                                  i = 12;
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
        break;
      }
    }
    return Factory.newInstance(paramJSONObject, paramLottieComposition);
    return ShapeStroke.Factory.newInstance(paramJSONObject, paramLottieComposition);
    return GradientStroke.Factory.newInstance(paramJSONObject, paramLottieComposition);
    return ShapeFill.Factory.newInstance(paramJSONObject, paramLottieComposition);
    return GradientFill.Factory.newInstance(paramJSONObject, paramLottieComposition);
    return AnimatableTransform.Factory.newInstance(paramJSONObject, paramLottieComposition);
    return ShapePath.Factory.newInstance(paramJSONObject, paramLottieComposition);
    return CircleShape.Factory.newInstance(paramJSONObject, paramLottieComposition);
    return RectangleShape.Factory.newInstance(paramJSONObject, paramLottieComposition);
    return ShapeTrimPath.Factory.newInstance(paramJSONObject, paramLottieComposition);
    return PolystarShape.Factory.newInstance(paramJSONObject, paramLottieComposition);
    return MergePaths.Factory.newInstance(paramJSONObject);
    return Repeater.Factory.newInstance(paramJSONObject, paramLottieComposition);
  }
  
  public List<ContentModel> getItems()
  {
    return this.items;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public Content toContent(LottieDrawable paramLottieDrawable, BaseLayer paramBaseLayer)
  {
    return new ContentGroup(paramLottieDrawable, paramBaseLayer, this);
  }
  
  public String toString()
  {
    return "ShapeGroup{name='" + this.name + "' Shapes: " + Arrays.toString(this.items.toArray()) + '}';
  }
  
  static class Factory
  {
    private static ShapeGroup newInstance(JSONObject paramJSONObject, LottieComposition paramLottieComposition)
    {
      JSONArray localJSONArray = paramJSONObject.optJSONArray("it");
      paramJSONObject = paramJSONObject.optString("nm");
      ArrayList localArrayList = new ArrayList();
      int i = 0;
      if (i >= localJSONArray.length()) {
        return new ShapeGroup(paramJSONObject, localArrayList);
      }
      ContentModel localContentModel = ShapeGroup.shapeItemWithJson(localJSONArray.optJSONObject(i), paramLottieComposition);
      if (localContentModel == null) {}
      for (;;)
      {
        i += 1;
        break;
        localArrayList.add(localContentModel);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\content\ShapeGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */