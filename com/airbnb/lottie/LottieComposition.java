package com.airbnb.lottie;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.util.LongSparseArray;
import android.support.v4.util.SparseArrayCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import com.airbnb.lottie.model.FileCompositionLoader;
import com.airbnb.lottie.model.Font;
import com.airbnb.lottie.model.Font.Factory;
import com.airbnb.lottie.model.FontCharacter;
import com.airbnb.lottie.model.FontCharacter.Factory;
import com.airbnb.lottie.model.JsonCompositionLoader;
import com.airbnb.lottie.model.layer.Layer;
import com.airbnb.lottie.model.layer.Layer.Factory;
import com.airbnb.lottie.model.layer.Layer.LayerType;
import com.airbnb.lottie.utils.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LottieComposition
{
  private final Rect bounds;
  private final SparseArrayCompat<FontCharacter> characters = new SparseArrayCompat();
  private final float dpScale;
  private final long endFrame;
  private final Map<String, Font> fonts = new HashMap();
  private final float frameRate;
  private final Map<String, LottieImageAsset> images = new HashMap();
  private final LongSparseArray<Layer> layerMap = new LongSparseArray();
  private final List<Layer> layers = new ArrayList();
  private final int majorVersion;
  private final int minorVersion;
  private final int patchVersion;
  private final PerformanceTracker performanceTracker = new PerformanceTracker();
  private final Map<String, List<Layer>> precomps = new HashMap();
  private final long startFrame;
  private final HashSet<String> warnings = new HashSet();
  
  private LottieComposition(Rect paramRect, long paramLong1, long paramLong2, float paramFloat1, float paramFloat2, int paramInt1, int paramInt2, int paramInt3)
  {
    this.bounds = paramRect;
    this.startFrame = paramLong1;
    this.endFrame = paramLong2;
    this.frameRate = paramFloat1;
    this.dpScale = paramFloat2;
    this.majorVersion = paramInt1;
    this.minorVersion = paramInt2;
    this.patchVersion = paramInt3;
    if (Utils.isAtLeastVersion(this, 4, 5, 0)) {
      return;
    }
    addWarning("Lottie only supports bodymovin >= 4.5.0");
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY})
  public void addWarning(String paramString)
  {
    Log.w("LOTTIE", paramString);
    this.warnings.add(paramString);
  }
  
  public Rect getBounds()
  {
    return this.bounds;
  }
  
  public SparseArrayCompat<FontCharacter> getCharacters()
  {
    return this.characters;
  }
  
  public float getDpScale()
  {
    return this.dpScale;
  }
  
  public long getDuration()
  {
    return ((float)(this.endFrame - this.startFrame) / this.frameRate * 1000.0F);
  }
  
  public float getDurationFrames()
  {
    return (float)getDuration() * this.frameRate / 1000.0F;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY})
  public long getEndFrame()
  {
    return this.endFrame;
  }
  
  public Map<String, Font> getFonts()
  {
    return this.fonts;
  }
  
  Map<String, LottieImageAsset> getImages()
  {
    return this.images;
  }
  
  public List<Layer> getLayers()
  {
    return this.layers;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY})
  public int getMajorVersion()
  {
    return this.majorVersion;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY})
  public int getMinorVersion()
  {
    return this.minorVersion;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY})
  public int getPatchVersion()
  {
    return this.patchVersion;
  }
  
  public PerformanceTracker getPerformanceTracker()
  {
    return this.performanceTracker;
  }
  
  @Nullable
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY})
  public List<Layer> getPrecomps(String paramString)
  {
    return (List)this.precomps.get(paramString);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY})
  public long getStartFrame()
  {
    return this.startFrame;
  }
  
  public ArrayList<String> getWarnings()
  {
    return new ArrayList(Arrays.asList(this.warnings.toArray(new String[this.warnings.size()])));
  }
  
  public boolean hasImages()
  {
    return !this.images.isEmpty();
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY})
  public Layer layerModelForId(long paramLong)
  {
    return (Layer)this.layerMap.get(paramLong);
  }
  
  public void setPerformanceTrackingEnabled(boolean paramBoolean)
  {
    this.performanceTracker.setEnabled(paramBoolean);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("LottieComposition:\n");
    Iterator localIterator = this.layers.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return localStringBuilder.toString();
      }
      localStringBuilder.append(((Layer)localIterator.next()).toString("\t"));
    }
  }
  
  public static class Factory
  {
    private static void addLayer(List<Layer> paramList, LongSparseArray<Layer> paramLongSparseArray, Layer paramLayer)
    {
      paramList.add(paramLayer);
      paramLongSparseArray.put(paramLayer.getId(), paramLayer);
    }
    
    public static Cancellable fromAssetFileName(Context paramContext, String paramString, OnCompositionLoadedListener paramOnCompositionLoadedListener)
    {
      try
      {
        InputStream localInputStream = paramContext.getAssets().open(paramString);
        return fromInputStream(paramContext, localInputStream, paramOnCompositionLoadedListener);
      }
      catch (IOException paramContext)
      {
        throw new IllegalStateException("Unable to find file " + paramString, paramContext);
      }
    }
    
    public static LottieComposition fromFileSync(Context paramContext, String paramString)
    {
      try
      {
        InputStream localInputStream = paramContext.getAssets().open(paramString);
        return fromInputStream(paramContext.getResources(), localInputStream);
      }
      catch (IOException paramContext)
      {
        throw new IllegalStateException("Unable to find file " + paramString, paramContext);
      }
    }
    
    public static Cancellable fromInputStream(Context paramContext, InputStream paramInputStream, OnCompositionLoadedListener paramOnCompositionLoadedListener)
    {
      paramContext = new FileCompositionLoader(paramContext.getResources(), paramOnCompositionLoadedListener);
      paramContext.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new InputStream[] { paramInputStream });
      return paramContext;
    }
    
    @Nullable
    public static LottieComposition fromInputStream(Resources paramResources, InputStream paramInputStream)
    {
      try
      {
        byte[] arrayOfByte = new byte[paramInputStream.available()];
        paramInputStream.read(arrayOfByte);
        paramResources = fromJsonSync(paramResources, new JSONObject(new String(arrayOfByte, "UTF-8")));
        return paramResources;
      }
      catch (IOException paramResources)
      {
        Log.e("LOTTIE", "Failed to load composition.", new IllegalStateException("Unable to find file.", paramResources));
        return null;
      }
      catch (JSONException paramResources)
      {
        for (;;)
        {
          Log.e("LOTTIE", "Failed to load composition.", new IllegalStateException("Unable to load JSON.", paramResources));
          Utils.closeQuietly(paramInputStream);
        }
      }
      finally
      {
        Utils.closeQuietly(paramInputStream);
      }
    }
    
    public static Cancellable fromJson(Resources paramResources, JSONObject paramJSONObject, OnCompositionLoadedListener paramOnCompositionLoadedListener)
    {
      paramResources = new JsonCompositionLoader(paramResources, paramOnCompositionLoadedListener);
      paramResources.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new JSONObject[] { paramJSONObject });
      return paramResources;
    }
    
    public static LottieComposition fromJsonSync(Resources paramResources, JSONObject paramJSONObject)
    {
      float f1 = paramResources.getDisplayMetrics().density;
      int i = paramJSONObject.optInt("w", -1);
      int j = paramJSONObject.optInt("h", -1);
      if (i == -1) {}
      for (paramResources = null;; paramResources = new Rect(0, 0, (int)(i * f1), (int)(j * f1)))
      {
        long l1 = paramJSONObject.optLong("ip", 0L);
        long l2 = paramJSONObject.optLong("op", 0L);
        float f2 = (float)paramJSONObject.optDouble("fr", 0.0D);
        Object localObject = paramJSONObject.optString("v").split("[.]");
        paramResources = new LottieComposition(paramResources, l1, l2, f2, f1, Integer.parseInt(localObject[0]), Integer.parseInt(localObject[1]), Integer.parseInt(localObject[2]), null);
        localObject = paramJSONObject.optJSONArray("assets");
        parseImages((JSONArray)localObject, paramResources);
        parsePrecomps((JSONArray)localObject, paramResources);
        parseFonts(paramJSONObject.optJSONObject("fonts"), paramResources);
        parseChars(paramJSONObject.optJSONArray("chars"), paramResources);
        parseLayers(paramJSONObject, paramResources);
        return paramResources;
        if (j == -1) {
          break;
        }
      }
    }
    
    private static void parseChars(@Nullable JSONArray paramJSONArray, LottieComposition paramLottieComposition)
    {
      int j;
      int i;
      if (paramJSONArray != null)
      {
        j = paramJSONArray.length();
        i = 0;
      }
      for (;;)
      {
        if (i >= j)
        {
          return;
          return;
        }
        FontCharacter localFontCharacter = FontCharacter.Factory.newInstance(paramJSONArray.optJSONObject(i), paramLottieComposition);
        paramLottieComposition.characters.put(localFontCharacter.hashCode(), localFontCharacter);
        i += 1;
      }
    }
    
    private static void parseFonts(@Nullable JSONObject paramJSONObject, LottieComposition paramLottieComposition)
    {
      int j;
      int i;
      if (paramJSONObject != null)
      {
        paramJSONObject = paramJSONObject.optJSONArray("list");
        if (paramJSONObject == null) {
          break label30;
        }
        j = paramJSONObject.length();
        i = 0;
      }
      for (;;)
      {
        if (i >= j)
        {
          return;
          return;
          label30:
          return;
        }
        Font localFont = Font.Factory.newInstance(paramJSONObject.optJSONObject(i));
        paramLottieComposition.fonts.put(localFont.getName(), localFont);
        i += 1;
      }
    }
    
    private static void parseImages(@Nullable JSONArray paramJSONArray, LottieComposition paramLottieComposition)
    {
      int i = 0;
      int j;
      if (paramJSONArray != null) {
        j = paramJSONArray.length();
      }
      for (;;)
      {
        if (i >= j)
        {
          return;
          return;
        }
        Object localObject = paramJSONArray.optJSONObject(i);
        if (((JSONObject)localObject).has("p"))
        {
          localObject = LottieImageAsset.Factory.newInstance((JSONObject)localObject);
          paramLottieComposition.images.put(((LottieImageAsset)localObject).getId(), localObject);
        }
        i += 1;
      }
    }
    
    private static void parseLayers(JSONObject paramJSONObject, LottieComposition paramLottieComposition)
    {
      int j = 0;
      paramJSONObject = paramJSONObject.optJSONArray("layers");
      int i;
      if (paramJSONObject != null)
      {
        int k = paramJSONObject.length();
        i = 0;
        if (i >= k) {
          if (j > 4) {
            break label84;
          }
        }
      }
      else
      {
        return;
      }
      Layer localLayer = Layer.Factory.newInstance(paramJSONObject.optJSONObject(i), paramLottieComposition);
      if (localLayer.getLayerType() != Layer.LayerType.Image) {}
      for (;;)
      {
        addLayer(paramLottieComposition.layers, paramLottieComposition.layerMap, localLayer);
        i += 1;
        break;
        j += 1;
      }
      label84:
      paramLottieComposition.addWarning("You have " + j + " images. Lottie should primarily be used with shapes. If you are using Adobe Illustrator, convert the Illustrator layers to shape layers.");
    }
    
    private static void parsePrecomps(@Nullable JSONArray paramJSONArray, LottieComposition paramLottieComposition)
    {
      int i;
      if (paramJSONArray != null)
      {
        int k = paramJSONArray.length();
        i = 0;
        if (i < k) {}
      }
      else
      {
        return;
      }
      Object localObject = paramJSONArray.optJSONObject(i);
      JSONArray localJSONArray = ((JSONObject)localObject).optJSONArray("layers");
      ArrayList localArrayList;
      LongSparseArray localLongSparseArray;
      int j;
      if (localJSONArray != null)
      {
        localArrayList = new ArrayList(localJSONArray.length());
        localLongSparseArray = new LongSparseArray();
        j = 0;
      }
      for (;;)
      {
        if (j >= localJSONArray.length())
        {
          localObject = ((JSONObject)localObject).optString("id");
          paramLottieComposition.precomps.put(localObject, localArrayList);
          i += 1;
          break;
        }
        Layer localLayer = Layer.Factory.newInstance(localJSONArray.optJSONObject(j), paramLottieComposition);
        localLongSparseArray.put(localLayer.getId(), localLayer);
        localArrayList.add(localLayer);
        j += 1;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\LottieComposition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */