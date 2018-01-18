package com.caverock.androidsvg;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.util.Log;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SimpleAssetResolver
  extends SVGExternalFileResolver
{
  private static final String TAG = SimpleAssetResolver.class.getSimpleName();
  private static final Set<String> supportedFormats = new HashSet(8);
  private AssetManager assetManager;
  
  public SimpleAssetResolver(AssetManager paramAssetManager)
  {
    supportedFormats.add("image/svg+xml");
    supportedFormats.add("image/jpeg");
    supportedFormats.add("image/png");
    supportedFormats.add("image/pjpeg");
    supportedFormats.add("image/gif");
    supportedFormats.add("image/bmp");
    supportedFormats.add("image/x-windows-bmp");
    if (Build.VERSION.SDK_INT < 14) {}
    for (;;)
    {
      this.assetManager = paramAssetManager;
      return;
      supportedFormats.add("image/webp");
    }
  }
  
  public boolean isFormatSupported(String paramString)
  {
    return supportedFormats.contains(paramString);
  }
  
  public Typeface resolveFont(String paramString1, int paramInt, String paramString2)
  {
    Log.i(TAG, "resolveFont(" + paramString1 + "," + paramInt + "," + paramString2 + ")");
    try
    {
      paramString2 = Typeface.createFromAsset(this.assetManager, paramString1 + ".ttf");
      return paramString2;
    }
    catch (Exception paramString2)
    {
      try
      {
        paramString1 = Typeface.createFromAsset(this.assetManager, paramString1 + ".otf");
        return paramString1;
      }
      catch (Exception paramString1) {}
    }
    return null;
  }
  
  public Bitmap resolveImage(String paramString)
  {
    Log.i(TAG, "resolveImage(" + paramString + ")");
    try
    {
      paramString = BitmapFactory.decodeStream(this.assetManager.open(paramString));
      return paramString;
    }
    catch (IOException paramString) {}
    return null;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\caverock\androidsvg\SimpleAssetResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */