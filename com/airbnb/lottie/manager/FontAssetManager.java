package com.airbnb.lottie.manager;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable.Callback;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import com.airbnb.lottie.FontAssetDelegate;
import com.airbnb.lottie.model.MutablePair;
import java.util.HashMap;
import java.util.Map;

public class FontAssetManager
{
  private final AssetManager assetManager;
  private String defaultFontFileExtension = ".ttf";
  @Nullable
  private FontAssetDelegate delegate;
  private final Map<String, Typeface> fontFamilies = new HashMap();
  private final Map<MutablePair<String>, Typeface> fontMap = new HashMap();
  private final MutablePair<String> tempPair = new MutablePair();
  
  public FontAssetManager(Drawable.Callback paramCallback, @Nullable FontAssetDelegate paramFontAssetDelegate)
  {
    this.delegate = paramFontAssetDelegate;
    if ((paramCallback instanceof View))
    {
      this.assetManager = ((View)paramCallback).getContext().getAssets();
      return;
    }
    Log.w("LOTTIE", "LottieDrawable must be inside of a view for images to work.");
    this.assetManager = null;
  }
  
  private Typeface getFontFamily(String paramString)
  {
    Object localObject1 = (Typeface)this.fontFamilies.get(paramString);
    Object localObject2;
    if (localObject1 == null)
    {
      if (this.delegate != null) {
        break label56;
      }
      localObject1 = null;
      if (this.delegate != null) {
        break label68;
      }
      localObject2 = localObject1;
      label36:
      if (localObject2 == null) {
        break label104;
      }
    }
    for (;;)
    {
      this.fontFamilies.put(paramString, localObject2);
      return (Typeface)localObject2;
      return (Typeface)localObject1;
      label56:
      localObject1 = this.delegate.fetchFont(paramString);
      break;
      label68:
      localObject2 = localObject1;
      if (localObject1 != null) {
        break label36;
      }
      String str = this.delegate.getFontPath(paramString);
      localObject2 = localObject1;
      if (str == null) {
        break label36;
      }
      localObject2 = Typeface.createFromAsset(this.assetManager, str);
      break label36;
      label104:
      localObject1 = "fonts/" + paramString + this.defaultFontFileExtension;
      localObject2 = Typeface.createFromAsset(this.assetManager, (String)localObject1);
    }
  }
  
  private Typeface typefaceForStyle(Typeface paramTypeface, String paramString)
  {
    int i = 0;
    boolean bool1 = paramString.contains("Italic");
    boolean bool2 = paramString.contains("Bold");
    if (!bool1)
    {
      if (bool1) {
        break label57;
      }
      if (bool2) {
        break label62;
      }
    }
    for (;;)
    {
      if (paramTypeface.getStyle() == i) {
        return paramTypeface;
      }
      return Typeface.create(paramTypeface, i);
      if (!bool2) {
        break;
      }
      i = 3;
      continue;
      label57:
      i = 2;
      continue;
      label62:
      i = 1;
    }
    return paramTypeface;
  }
  
  public Typeface getTypeface(String paramString1, String paramString2)
  {
    this.tempPair.set(paramString1, paramString2);
    Typeface localTypeface = (Typeface)this.fontMap.get(this.tempPair);
    if (localTypeface == null)
    {
      paramString1 = typefaceForStyle(getFontFamily(paramString1), paramString2);
      this.fontMap.put(this.tempPair, paramString1);
      return paramString1;
    }
    return localTypeface;
  }
  
  public void setDefaultFontFileExtension(String paramString)
  {
    this.defaultFontFileExtension = paramString;
  }
  
  public void setDelegate(@Nullable FontAssetDelegate paramFontAssetDelegate)
  {
    this.delegate = paramFontAssetDelegate;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\manager\FontAssetManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */