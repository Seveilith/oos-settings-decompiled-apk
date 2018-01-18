package com.caverock.androidsvg;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

public class SVGImageView
  extends ImageView
{
  private static Method setLayerTypeMethod = null;
  
  public SVGImageView(Context paramContext)
  {
    super(paramContext);
    try
    {
      setLayerTypeMethod = View.class.getMethod("setLayerType", new Class[] { Integer.TYPE, Paint.class });
      return;
    }
    catch (NoSuchMethodException paramContext) {}
  }
  
  public SVGImageView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet, 0);
    try
    {
      setLayerTypeMethod = View.class.getMethod("setLayerType", new Class[] { Integer.TYPE, Paint.class });
      init(paramAttributeSet, 0);
      return;
    }
    catch (NoSuchMethodException paramContext)
    {
      for (;;) {}
    }
  }
  
  public SVGImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    try
    {
      setLayerTypeMethod = View.class.getMethod("setLayerType", new Class[] { Integer.TYPE, Paint.class });
      init(paramAttributeSet, paramInt);
      return;
    }
    catch (NoSuchMethodException paramContext)
    {
      for (;;) {}
    }
  }
  
  private void init(AttributeSet paramAttributeSet, int paramInt)
  {
    paramAttributeSet = getContext().getTheme().obtainStyledAttributes(paramAttributeSet, R.styleable.SVGImageView, paramInt, 0);
    try
    {
      paramInt = paramAttributeSet.getResourceId(0, -1);
      if (paramInt == -1)
      {
        String str = paramAttributeSet.getString(0);
        if (!internalSetImageURI(Uri.parse(str))) {
          setImageAsset(str);
        }
      }
      else
      {
        setImageResource(paramInt);
        return;
      }
      return;
    }
    finally
    {
      paramAttributeSet.recycle();
    }
  }
  
  private boolean internalSetImageURI(Uri paramUri)
  {
    Object localObject2 = null;
    Object localObject1 = null;
    try
    {
      InputStream localInputStream = getContext().getContentResolver().openInputStream(paramUri);
      localObject1 = localInputStream;
      localObject2 = localInputStream;
      SVG localSVG = SVG.getFromInputStream(localInputStream);
      localObject1 = localInputStream;
      localObject2 = localInputStream;
      setSoftwareLayerType();
      localObject1 = localInputStream;
      localObject2 = localInputStream;
      setImageDrawable(new PictureDrawable(localSVG.renderToPicture()));
      for (;;)
      {
        return true;
        try
        {
          localInputStream.close();
        }
        catch (IOException paramUri) {}
      }
      throw paramUri;
    }
    catch (Exception localException)
    {
      localObject2 = localObject1;
      Log.w("ImageView", "Unable to open content: " + paramUri, localException);
      for (;;)
      {
        return false;
        try
        {
          ((InputStream)localObject1).close();
        }
        catch (IOException paramUri) {}
      }
    }
    finally
    {
      if (localObject2 != null) {}
    }
    for (;;)
    {
      try
      {
        ((InputStream)localObject2).close();
      }
      catch (IOException localIOException) {}
    }
  }
  
  private void setSoftwareLayerType()
  {
    if (setLayerTypeMethod != null) {}
    try
    {
      setLayerTypeMethod.invoke(this, new Object[] { Integer.valueOf(1), null });
      return;
    }
    catch (Exception localException)
    {
      Log.w("SVGImageView", "Unexpected failure calling setLayerType", localException);
    }
    return;
  }
  
  public void setImageAsset(String paramString)
  {
    try
    {
      SVG localSVG = SVG.getFromAsset(getContext().getAssets(), paramString);
      setSoftwareLayerType();
      setImageDrawable(new PictureDrawable(localSVG.renderToPicture()));
      return;
    }
    catch (Exception localException)
    {
      Log.w("SVGImageView", "Unable to find asset file: " + paramString, localException);
    }
  }
  
  public void setImageResource(int paramInt)
  {
    try
    {
      SVG localSVG = SVG.getFromResource(getContext(), paramInt);
      setSoftwareLayerType();
      setImageDrawable(new PictureDrawable(localSVG.renderToPicture()));
      return;
    }
    catch (SVGParseException localSVGParseException)
    {
      Log.w("SVGImageView", "Unable to find resource: " + paramInt, localSVGParseException);
    }
  }
  
  public void setImageURI(Uri paramUri)
  {
    internalSetImageURI(paramUri);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\caverock\androidsvg\SVGImageView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */