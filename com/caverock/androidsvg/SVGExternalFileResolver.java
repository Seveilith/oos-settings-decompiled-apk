package com.caverock.androidsvg;

import android.graphics.Bitmap;
import android.graphics.Typeface;

public abstract class SVGExternalFileResolver
{
  public boolean isFormatSupported(String paramString)
  {
    return false;
  }
  
  public Typeface resolveFont(String paramString1, int paramInt, String paramString2)
  {
    return null;
  }
  
  public Bitmap resolveImage(String paramString)
  {
    return null;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\caverock\androidsvg\SVGExternalFileResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */