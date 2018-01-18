package com.google.tagmanager;

import com.google.analytics.containertag.proto.Serving.GaExperimentRandom;
import com.google.analytics.containertag.proto.Serving.GaExperimentSupplemental;
import com.google.analytics.containertag.proto.Serving.Supplemental;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.Map;

public class ExperimentMacroHelper
{
  private static void clearKeys(DataLayer paramDataLayer, Serving.GaExperimentSupplemental paramGaExperimentSupplemental)
  {
    paramGaExperimentSupplemental = paramGaExperimentSupplemental.valueToClear;
    int j = paramGaExperimentSupplemental.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return;
      }
      paramDataLayer.clearPersistentKeysWithPrefix(Types.valueToString(paramGaExperimentSupplemental[i]));
      i += 1;
    }
  }
  
  public static void handleExperimentSupplemental(DataLayer paramDataLayer, Serving.Supplemental paramSupplemental)
  {
    if (paramSupplemental.experimentSupplemental != null)
    {
      clearKeys(paramDataLayer, paramSupplemental.experimentSupplemental);
      pushValues(paramDataLayer, paramSupplemental.experimentSupplemental);
      setRandomValues(paramDataLayer, paramSupplemental.experimentSupplemental);
      return;
    }
    Log.w("supplemental missing experimentSupplemental");
  }
  
  private static void pushValues(DataLayer paramDataLayer, Serving.GaExperimentSupplemental paramGaExperimentSupplemental)
  {
    paramGaExperimentSupplemental = paramGaExperimentSupplemental.valueToPush;
    int j = paramGaExperimentSupplemental.length;
    int i = 0;
    if (i >= j) {
      return;
    }
    Map localMap = valueToMap(paramGaExperimentSupplemental[i]);
    if (localMap == null) {}
    for (;;)
    {
      i += 1;
      break;
      paramDataLayer.push(localMap);
    }
  }
  
  private static void setRandomValues(DataLayer paramDataLayer, Serving.GaExperimentSupplemental paramGaExperimentSupplemental)
  {
    Serving.GaExperimentRandom[] arrayOfGaExperimentRandom = paramGaExperimentSupplemental.experimentRandom;
    int k = arrayOfGaExperimentRandom.length;
    int i = 0;
    if (i >= k) {
      return;
    }
    Serving.GaExperimentRandom localGaExperimentRandom = arrayOfGaExperimentRandom[i];
    Object localObject;
    label65:
    long l1;
    long l2;
    label87:
    int j;
    if (localGaExperimentRandom.key != null)
    {
      localObject = paramDataLayer.get(localGaExperimentRandom.key);
      if (!(localObject instanceof Number)) {
        break label212;
      }
      paramGaExperimentSupplemental = Long.valueOf(((Number)localObject).longValue());
      l1 = localGaExperimentRandom.minRandom;
      l2 = localGaExperimentRandom.maxRandom;
      if (localGaExperimentRandom.retainOriginalValue) {
        break label217;
      }
      if (l1 <= l2) {
        break label269;
      }
      j = 1;
      label97:
      if (j != 0) {
        break label274;
      }
      paramGaExperimentSupplemental = Long.valueOf(Math.round(Math.random() * (l2 - l1) + l1));
      label122:
      paramDataLayer.clearPersistentKeysWithPrefix(localGaExperimentRandom.key);
      paramGaExperimentSupplemental = paramDataLayer.expandKeyValue(localGaExperimentRandom.key, paramGaExperimentSupplemental);
      if (localGaExperimentRandom.lifetimeInMilliseconds > 0L) {
        break label282;
      }
      j = 1;
      label154:
      if (j == 0)
      {
        if (!paramGaExperimentSupplemental.containsKey("gtm")) {
          break label287;
        }
        localObject = paramGaExperimentSupplemental.get("gtm");
        if ((localObject instanceof Map)) {
          break label322;
        }
        Log.w("GaExperimentRandom: gtm not a map");
      }
    }
    for (;;)
    {
      paramDataLayer.push(paramGaExperimentSupplemental);
      for (;;)
      {
        i += 1;
        break;
        Log.w("GaExperimentRandom: No key");
        continue;
        label212:
        paramGaExperimentSupplemental = null;
        break label65;
        label217:
        if (paramGaExperimentSupplemental == null) {
          break label87;
        }
        if (paramGaExperimentSupplemental.longValue() < l1)
        {
          j = 1;
          label233:
          if (j != 0) {
            break label262;
          }
          if (paramGaExperimentSupplemental.longValue() > l2) {
            break label264;
          }
        }
        label262:
        label264:
        for (j = 1;; j = 0)
        {
          paramGaExperimentSupplemental = (Serving.GaExperimentSupplemental)localObject;
          if (j != 0) {
            break label122;
          }
          break;
          j = 0;
          break label233;
          break;
        }
        label269:
        j = 0;
        break label97;
        label274:
        Log.w("GaExperimentRandom: random range invalid");
      }
      label282:
      j = 0;
      break label154;
      label287:
      paramGaExperimentSupplemental.put("gtm", DataLayer.mapOf(new Object[] { "lifetime", Long.valueOf(localGaExperimentRandom.lifetimeInMilliseconds) }));
      continue;
      label322:
      ((Map)localObject).put("lifetime", Long.valueOf(localGaExperimentRandom.lifetimeInMilliseconds));
    }
  }
  
  private static Map<Object, Object> valueToMap(TypeSystem.Value paramValue)
  {
    paramValue = Types.valueToObject(paramValue);
    if ((paramValue instanceof Map)) {
      return (Map)paramValue;
    }
    Log.w("value: " + paramValue + " is not a map value, ignored.");
    return null;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\ExperimentMacroHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */