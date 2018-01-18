package com.airbnb.lottie.manager;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable.Callback;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieImageAsset;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ImageAssetManager
{
  private final Map<String, Bitmap> bitmaps = new HashMap();
  private final Context context;
  @Nullable
  private ImageAssetDelegate delegate;
  private final Map<String, LottieImageAsset> imageAssets;
  private String imagesFolder;
  
  public ImageAssetManager(Drawable.Callback paramCallback, String paramString, ImageAssetDelegate paramImageAssetDelegate, Map<String, LottieImageAsset> paramMap)
  {
    this.imagesFolder = paramString;
    if (TextUtils.isEmpty(paramString)) {}
    while ((paramCallback instanceof View))
    {
      this.context = ((View)paramCallback).getContext();
      this.imageAssets = paramMap;
      setDelegate(paramImageAssetDelegate);
      return;
      if (this.imagesFolder.charAt(this.imagesFolder.length() - 1) != '/') {
        this.imagesFolder += '/';
      }
    }
    Log.w("LOTTIE", "LottieDrawable must be inside of a view for images to work.");
    this.imageAssets = new HashMap();
    this.context = null;
  }
  
  @Nullable
  public Bitmap bitmapForId(String paramString)
  {
    Object localObject = (Bitmap)this.bitmaps.get(paramString);
    if (localObject != null) {
      return (Bitmap)localObject;
    }
    localObject = (LottieImageAsset)this.imageAssets.get(paramString);
    if ((localObject == null) || (this.delegate == null)) {}
    try
    {
      if (!TextUtils.isEmpty(this.imagesFolder))
      {
        localObject = this.context.getAssets().open(this.imagesFolder + ((LottieImageAsset)localObject).getFileName());
        BitmapFactory.Options localOptions = new BitmapFactory.Options();
        localOptions.inScaled = true;
        localOptions.inDensity = 160;
        localObject = BitmapFactory.decodeStream((InputStream)localObject, null, localOptions);
        this.bitmaps.put(paramString, localObject);
        return (Bitmap)localObject;
        return null;
        localObject = this.delegate.fetchBitmap((LottieImageAsset)localObject);
        if (localObject == null) {
          return (Bitmap)localObject;
        }
        this.bitmaps.put(paramString, localObject);
        return (Bitmap)localObject;
      }
      throw new IllegalStateException("You must set an images folder before loading an image. Set it with LottieComposition#setImagesFolder or LottieDrawable#setImagesFolder");
    }
    catch (IOException paramString)
    {
      Log.w("LOTTIE", "Unable to open asset.", paramString);
    }
    return null;
  }
  
  public boolean hasSameContext(Context paramContext)
  {
    if (paramContext != null) {}
    while (this.context != null)
    {
      if (paramContext != null) {
        break;
      }
      return false;
    }
    while (this.context.equals(paramContext)) {
      return true;
    }
    return false;
  }
  
  public void recycleBitmaps()
  {
    Iterator localIterator = this.bitmaps.entrySet().iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      ((Bitmap)((Map.Entry)localIterator.next()).getValue()).recycle();
      localIterator.remove();
    }
  }
  
  public void setDelegate(@Nullable ImageAssetDelegate paramImageAssetDelegate)
  {
    this.delegate = paramImageAssetDelegate;
  }
  
  @Nullable
  public Bitmap updateBitmap(String paramString, @Nullable Bitmap paramBitmap)
  {
    return (Bitmap)this.bitmaps.put(paramString, paramBitmap);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\manager\ImageAssetManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */