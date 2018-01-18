package com.google.tagmanager;

import com.google.analytics.containertag.common.Key;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import com.google.android.gms.common.util.VisibleForTesting;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class JsonUtils
{
  public static ResourceUtil.ExpandedResource expandedResourceFromJsonString(String paramString)
    throws JSONException
  {
    paramString = jsonObjectToValue(new JSONObject(paramString));
    ResourceUtil.ExpandedResourceBuilder localExpandedResourceBuilder = ResourceUtil.ExpandedResource.newBuilder();
    int i = 0;
    for (;;)
    {
      if (i >= paramString.mapKey.length) {
        return localExpandedResourceBuilder.build();
      }
      localExpandedResourceBuilder.addMacro(ResourceUtil.ExpandedFunctionCall.newBuilder().addProperty(Key.INSTANCE_NAME.toString(), paramString.mapKey[i]).addProperty(Key.FUNCTION.toString(), Types.functionIdToValue(ConstantMacro.getFunctionId())).addProperty(ConstantMacro.getValueKey(), paramString.mapValue[i]).build());
      i += 1;
    }
  }
  
  @VisibleForTesting
  static Object jsonObjectToObject(Object paramObject)
    throws JSONException
  {
    if (!(paramObject instanceof JSONArray))
    {
      if (!JSONObject.NULL.equals(paramObject))
      {
        if ((paramObject instanceof JSONObject)) {
          break label46;
        }
        return paramObject;
      }
    }
    else {
      throw new RuntimeException("JSONArrays are not supported");
    }
    throw new RuntimeException("JSON nulls are not supported");
    label46:
    paramObject = (JSONObject)paramObject;
    HashMap localHashMap = new HashMap();
    Iterator localIterator = ((JSONObject)paramObject).keys();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return localHashMap;
      }
      String str = (String)localIterator.next();
      localHashMap.put(str, jsonObjectToObject(((JSONObject)paramObject).get(str)));
    }
  }
  
  private static TypeSystem.Value jsonObjectToValue(Object paramObject)
    throws JSONException
  {
    return Types.objectToValue(jsonObjectToObject(paramObject));
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\JsonUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */