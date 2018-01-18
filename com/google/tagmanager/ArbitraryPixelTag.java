package com.google.tagmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.Uri.Builder;
import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.containertag.common.Key;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import com.google.android.gms.common.util.VisibleForTesting;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class ArbitraryPixelTag
  extends TrackingTag
{
  private static final String ADDITIONAL_PARAMS;
  static final String ARBITRARY_PIXEL_UNREPEATABLE = "gtm_" + ID + "_unrepeatable";
  private static final String ID = FunctionType.ARBITRARY_PIXEL.toString();
  private static final String UNREPEATABLE;
  private static final String URL = Key.URL.toString();
  private static final Set<String> unrepeatableIds = new HashSet();
  private final Context mContext;
  private final HitSenderProvider mHitSenderProvider;
  
  static
  {
    ADDITIONAL_PARAMS = Key.ADDITIONAL_PARAMS.toString();
    UNREPEATABLE = Key.UNREPEATABLE.toString();
  }
  
  public ArbitraryPixelTag(Context paramContext)
  {
    this(paramContext, new HitSenderProvider()
    {
      public HitSender get()
      {
        return DelayedHitSender.getInstance(ArbitraryPixelTag.this);
      }
    });
  }
  
  @VisibleForTesting
  ArbitraryPixelTag(Context paramContext, HitSenderProvider paramHitSenderProvider)
  {
    super(ID, new String[] { URL });
    this.mHitSenderProvider = paramHitSenderProvider;
    this.mContext = paramContext;
  }
  
  public static String getFunctionId()
  {
    return ID;
  }
  
  private boolean idProcessed(String paramString)
  {
    try
    {
      if (!idInCache(paramString))
      {
        boolean bool = idInSharedPreferences(paramString);
        if (!bool) {
          return false;
        }
      }
      else
      {
        return true;
      }
      unrepeatableIds.add(paramString);
      return true;
    }
    finally {}
  }
  
  @VisibleForTesting
  void clearCache()
  {
    unrepeatableIds.clear();
  }
  
  public void evaluateTrackingTag(Map<String, TypeSystem.Value> paramMap)
  {
    String str;
    if (paramMap.get(UNREPEATABLE) == null)
    {
      str = null;
      if (str != null) {
        break label127;
      }
    }
    Uri.Builder localBuilder;
    label127:
    while (!idProcessed(str))
    {
      localBuilder = Uri.parse(Types.valueToString((TypeSystem.Value)paramMap.get(URL))).buildUpon();
      paramMap = (TypeSystem.Value)paramMap.get(ADDITIONAL_PARAMS);
      if (paramMap != null) {
        break label136;
      }
      paramMap = localBuilder.build().toString();
      this.mHitSenderProvider.get().sendHit(paramMap);
      Log.v("ArbitraryPixel: url = " + paramMap);
      if (str != null) {
        break label308;
      }
      return;
      str = Types.valueToString((TypeSystem.Value)paramMap.get(UNREPEATABLE));
      break;
    }
    return;
    label136:
    paramMap = Types.valueToObject(paramMap);
    if ((paramMap instanceof List))
    {
      paramMap = ((List)paramMap).iterator();
      while (paramMap.hasNext())
      {
        Object localObject = paramMap.next();
        if (!(localObject instanceof Map)) {
          break label279;
        }
        localObject = ((Map)localObject).entrySet().iterator();
        while (((Iterator)localObject).hasNext())
        {
          Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
          localBuilder.appendQueryParameter(localEntry.getKey().toString(), localEntry.getValue().toString());
        }
      }
    }
    Log.e("ArbitraryPixel: additional params not a list: not sending partial hit: " + localBuilder.build().toString());
    return;
    label279:
    Log.e("ArbitraryPixel: additional params contains non-map: not sending partial hit: " + localBuilder.build().toString());
    return;
    try
    {
      label308:
      unrepeatableIds.add(str);
      SharedPreferencesUtil.saveAsync(this.mContext, ARBITRARY_PIXEL_UNREPEATABLE, str, "true");
      return;
    }
    finally {}
  }
  
  @VisibleForTesting
  boolean idInCache(String paramString)
  {
    return unrepeatableIds.contains(paramString);
  }
  
  @VisibleForTesting
  boolean idInSharedPreferences(String paramString)
  {
    return this.mContext.getSharedPreferences(ARBITRARY_PIXEL_UNREPEATABLE, 0).contains(paramString);
  }
  
  public static abstract interface HitSenderProvider
  {
    public abstract HitSender get();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\ArbitraryPixelTag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */