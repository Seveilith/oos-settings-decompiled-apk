package com.google.tagmanager;

import android.content.Context;
import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.containertag.common.Key;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.Map;

class AdwordsClickReferrerMacro
  extends FunctionCallImplementation
{
  private static final String COMPONENT = Key.COMPONENT.toString();
  private static final String CONVERSION_ID = Key.CONVERSION_ID.toString();
  private static final String ID = FunctionType.ADWORDS_CLICK_REFERRER.toString();
  private final Context context;
  
  public AdwordsClickReferrerMacro(Context paramContext)
  {
    super(ID, new String[] { CONVERSION_ID });
    this.context = paramContext;
  }
  
  public static String getFunctionId()
  {
    return ID;
  }
  
  public TypeSystem.Value evaluate(Map<String, TypeSystem.Value> paramMap)
  {
    Object localObject = (TypeSystem.Value)paramMap.get(CONVERSION_ID);
    if (localObject != null)
    {
      localObject = Types.valueToString((TypeSystem.Value)localObject);
      paramMap = (TypeSystem.Value)paramMap.get(COMPONENT);
      if (paramMap != null) {
        break label63;
      }
    }
    label63:
    for (paramMap = null;; paramMap = Types.valueToString(paramMap))
    {
      paramMap = InstallReferrerUtil.getClickReferrer(this.context, (String)localObject, paramMap);
      if (paramMap != null) {
        break;
      }
      return Types.getDefaultValue();
      return Types.getDefaultValue();
    }
    return Types.objectToValue(paramMap);
  }
  
  public boolean isCacheable()
  {
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\AdwordsClickReferrerMacro.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */