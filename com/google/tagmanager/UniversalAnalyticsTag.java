package com.google.tagmanager;

import android.content.Context;
import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.containertag.common.Key;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import com.google.analytics.tracking.android.Tracker;
import com.google.android.gms.common.util.VisibleForTesting;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class UniversalAnalyticsTag
  extends TrackingTag
{
  private static final String ACCOUNT;
  private static final String ANALYTICS_FIELDS = Key.ANALYTICS_FIELDS.toString();
  private static final String ANALYTICS_PASS_THROUGH;
  private static final String DEFAULT_TRACKING_ID = "_GTM_DEFAULT_TRACKER_";
  private static final String ID = FunctionType.UNIVERSAL_ANALYTICS.toString();
  private static final String TRACK_TRANSACTION = Key.TRACK_TRANSACTION.toString();
  private static final String TRANSACTION_DATALAYER_MAP = Key.TRANSACTION_DATALAYER_MAP.toString();
  private static final String TRANSACTION_ITEM_DATALAYER_MAP = Key.TRANSACTION_ITEM_DATALAYER_MAP.toString();
  private static Map<String, String> defaultItemMap;
  private static Map<String, String> defaultTransactionMap;
  private final DataLayer mDataLayer;
  private final TrackerProvider mTrackerProvider;
  private final Set<String> mTurnOffAnonymizeIpValues;
  
  static
  {
    ACCOUNT = Key.ACCOUNT.toString();
    ANALYTICS_PASS_THROUGH = Key.ANALYTICS_PASS_THROUGH.toString();
  }
  
  public UniversalAnalyticsTag(Context paramContext, DataLayer paramDataLayer)
  {
    this(paramContext, paramDataLayer, new TrackerProvider(paramContext));
  }
  
  @VisibleForTesting
  UniversalAnalyticsTag(Context paramContext, DataLayer paramDataLayer, TrackerProvider paramTrackerProvider)
  {
    super(ID, new String[0]);
    this.mDataLayer = paramDataLayer;
    this.mTrackerProvider = paramTrackerProvider;
    this.mTurnOffAnonymizeIpValues = new HashSet();
    this.mTurnOffAnonymizeIpValues.add("");
    this.mTurnOffAnonymizeIpValues.add("0");
    this.mTurnOffAnonymizeIpValues.add("false");
  }
  
  private void addParam(Map<String, String> paramMap, String paramString1, String paramString2)
  {
    if (paramString2 == null) {
      return;
    }
    paramMap.put(paramString1, paramString2);
  }
  
  private boolean checkBooleanProperty(Map<String, TypeSystem.Value> paramMap, String paramString)
  {
    paramMap = (TypeSystem.Value)paramMap.get(paramString);
    if (paramMap != null) {
      return Types.valueToBoolean(paramMap).booleanValue();
    }
    return false;
  }
  
  private Map<String, String> convertToGaFields(TypeSystem.Value paramValue)
  {
    String str;
    if (paramValue != null)
    {
      paramValue = valueToMap(paramValue);
      if (paramValue == null) {
        break label40;
      }
      str = (String)paramValue.get("&aip");
      if (str != null) {
        break label48;
      }
    }
    label40:
    label48:
    while (!this.mTurnOffAnonymizeIpValues.contains(str.toLowerCase()))
    {
      return paramValue;
      return new HashMap();
      return new HashMap();
    }
    paramValue.remove("&aip");
    return paramValue;
  }
  
  private String getDataLayerString(String paramString)
  {
    Object localObject1 = null;
    Object localObject2 = this.mDataLayer.get(paramString);
    paramString = (String)localObject1;
    if (localObject2 != null) {
      paramString = localObject2.toString();
    }
    return paramString;
  }
  
  public static String getFunctionId()
  {
    return ID;
  }
  
  private Map<String, String> getTransactionFields(Map<String, TypeSystem.Value> paramMap)
  {
    paramMap = (TypeSystem.Value)paramMap.get(TRANSACTION_DATALAYER_MAP);
    if (paramMap == null) {
      if (defaultTransactionMap == null) {
        break label33;
      }
    }
    for (;;)
    {
      return defaultTransactionMap;
      return valueToMap(paramMap);
      label33:
      paramMap = new HashMap();
      paramMap.put("transactionId", "&ti");
      paramMap.put("transactionAffiliation", "&ta");
      paramMap.put("transactionTax", "&tt");
      paramMap.put("transactionShipping", "&ts");
      paramMap.put("transactionTotal", "&tr");
      paramMap.put("transactionCurrency", "&cu");
      defaultTransactionMap = paramMap;
    }
  }
  
  private Map<String, String> getTransactionItemFields(Map<String, TypeSystem.Value> paramMap)
  {
    paramMap = (TypeSystem.Value)paramMap.get(TRANSACTION_ITEM_DATALAYER_MAP);
    if (paramMap == null) {
      if (defaultItemMap == null) {
        break label33;
      }
    }
    for (;;)
    {
      return defaultItemMap;
      return valueToMap(paramMap);
      label33:
      paramMap = new HashMap();
      paramMap.put("name", "&in");
      paramMap.put("sku", "&ic");
      paramMap.put("category", "&iv");
      paramMap.put("price", "&ip");
      paramMap.put("quantity", "&iq");
      paramMap.put("currency", "&cu");
      defaultItemMap = paramMap;
    }
  }
  
  private List<Map<String, String>> getTransactionItems()
  {
    Object localObject = this.mDataLayer.get("transactionProducts");
    Iterator localIterator;
    if (localObject != null)
    {
      if (!(localObject instanceof List)) {
        break label47;
      }
      localIterator = ((List)localObject).iterator();
    }
    label47:
    do
    {
      if (!localIterator.hasNext())
      {
        return (List)localObject;
        return null;
        throw new IllegalArgumentException("transactionProducts should be of type List.");
      }
    } while ((localIterator.next() instanceof Map));
    throw new IllegalArgumentException("Each element of transactionProducts should be of type Map.");
  }
  
  private void sendTransaction(Tracker paramTracker, Map<String, TypeSystem.Value> paramMap)
  {
    String str = getDataLayerString("transactionId");
    LinkedList localLinkedList;
    if (str != null) {
      localLinkedList = new LinkedList();
    }
    for (;;)
    {
      Object localObject1;
      Object localObject2;
      Object localObject3;
      try
      {
        localObject1 = convertToGaFields((TypeSystem.Value)paramMap.get(ANALYTICS_FIELDS));
        ((Map)localObject1).put("&t", "transaction");
        localObject2 = getTransactionFields(paramMap).entrySet().iterator();
        if (!((Iterator)localObject2).hasNext())
        {
          localLinkedList.add(localObject1);
          localObject1 = getTransactionItems();
          if (localObject1 == null)
          {
            paramMap = localLinkedList.iterator();
            boolean bool = paramMap.hasNext();
            if (bool) {
              break label365;
            }
            return;
            Log.e("Cannot find transactionId in data layer.");
          }
        }
        else
        {
          localObject3 = (Map.Entry)((Iterator)localObject2).next();
          addParam((Map)localObject1, (String)((Map.Entry)localObject3).getValue(), getDataLayerString((String)((Map.Entry)localObject3).getKey()));
          continue;
        }
        localObject1 = ((List)localObject1).iterator();
      }
      catch (IllegalArgumentException paramTracker)
      {
        Log.e("Unable to send transaction", paramTracker);
        return;
      }
      label192:
      Iterator localIterator;
      if (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Map)((Iterator)localObject1).next();
        if (((Map)localObject2).get("name") == null) {
          break label310;
        }
        localObject3 = convertToGaFields((TypeSystem.Value)paramMap.get(ANALYTICS_FIELDS));
        ((Map)localObject3).put("&t", "item");
        ((Map)localObject3).put("&ti", str);
        localIterator = getTransactionItemFields(paramMap).entrySet().iterator();
      }
      for (;;)
      {
        if (!localIterator.hasNext())
        {
          localLinkedList.add(localObject3);
          break label192;
          break;
          label310:
          Log.e("Unable to send transaction item hit due to missing 'name' field.");
          return;
        }
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        addParam((Map)localObject3, (String)localEntry.getValue(), (String)((Map)localObject2).get(localEntry.getKey()));
      }
      label365:
      paramTracker.send((Map)paramMap.next());
    }
  }
  
  private Map<String, String> valueToMap(TypeSystem.Value paramValue)
  {
    paramValue = Types.valueToObject(paramValue);
    Object localObject;
    if ((paramValue instanceof Map))
    {
      localObject = (Map)paramValue;
      paramValue = new LinkedHashMap();
      localObject = ((Map)localObject).entrySet().iterator();
    }
    for (;;)
    {
      if (!((Iterator)localObject).hasNext())
      {
        return paramValue;
        return null;
      }
      Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
      paramValue.put(localEntry.getKey().toString(), localEntry.getValue().toString());
    }
  }
  
  public void evaluateTrackingTag(Map<String, TypeSystem.Value> paramMap)
  {
    Tracker localTracker = this.mTrackerProvider.getTracker("_GTM_DEFAULT_TRACKER_");
    if (!checkBooleanProperty(paramMap, ANALYTICS_PASS_THROUGH))
    {
      if (checkBooleanProperty(paramMap, TRACK_TRANSACTION)) {
        break label70;
      }
      Log.w("Ignoring unknown tag.");
    }
    for (;;)
    {
      this.mTrackerProvider.close(localTracker);
      return;
      localTracker.send(convertToGaFields((TypeSystem.Value)paramMap.get(ANALYTICS_FIELDS)));
      continue;
      label70:
      sendTransaction(localTracker, paramMap);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\UniversalAnalyticsTag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */