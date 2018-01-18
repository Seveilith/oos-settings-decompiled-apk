package com.google.tagmanager;

import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.containertag.common.Key;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class DataLayerWriteTag
  extends TrackingTag
{
  private static final String CLEAR_PERSISTENT_DATA_LAYER_PREFIX = Key.CLEAR_PERSISTENT_DATA_LAYER_PREFIX.toString();
  private static final String ID = FunctionType.DATA_LAYER_WRITE.toString();
  private static final String VALUE = Key.VALUE.toString();
  private final DataLayer mDataLayer;
  
  public DataLayerWriteTag(DataLayer paramDataLayer)
  {
    super(ID, new String[] { VALUE });
    this.mDataLayer = paramDataLayer;
  }
  
  private void clearPersistent(TypeSystem.Value paramValue)
  {
    if (paramValue == null) {}
    while (paramValue == Types.getDefaultObject()) {
      return;
    }
    paramValue = Types.valueToString(paramValue);
    if (paramValue != Types.getDefaultString())
    {
      this.mDataLayer.clearPersistentKeysWithPrefix(paramValue);
      return;
    }
  }
  
  public static String getFunctionId()
  {
    return ID;
  }
  
  private void pushToDataLayer(TypeSystem.Value paramValue)
  {
    if (paramValue == null) {}
    while (paramValue == Types.getDefaultObject()) {
      return;
    }
    paramValue = Types.valueToObject(paramValue);
    if ((paramValue instanceof List)) {
      paramValue = ((List)paramValue).iterator();
    }
    for (;;)
    {
      if (!paramValue.hasNext())
      {
        return;
        return;
      }
      Object localObject = paramValue.next();
      if ((localObject instanceof Map))
      {
        localObject = (Map)localObject;
        this.mDataLayer.push((Map)localObject);
      }
    }
  }
  
  public void evaluateTrackingTag(Map<String, TypeSystem.Value> paramMap)
  {
    pushToDataLayer((TypeSystem.Value)paramMap.get(VALUE));
    clearPersistent((TypeSystem.Value)paramMap.get(CLEAR_PERSISTENT_DATA_LAYER_PREFIX));
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\DataLayerWriteTag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */