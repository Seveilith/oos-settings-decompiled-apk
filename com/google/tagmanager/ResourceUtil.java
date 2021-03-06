package com.google.tagmanager;

import com.google.analytics.containertag.common.Key;
import com.google.analytics.containertag.proto.Serving.FunctionCall;
import com.google.analytics.containertag.proto.Serving.Property;
import com.google.analytics.containertag.proto.Serving.Resource;
import com.google.analytics.containertag.proto.Serving.Rule;
import com.google.analytics.containertag.proto.Serving.ServingValue;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class ResourceUtil
{
  private static final int BUFFER_SIZE = 1024;
  
  public static void copyStream(InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte['Ѐ'];
    for (;;)
    {
      int i = paramInputStream.read(arrayOfByte);
      if (i == -1) {
        break;
      }
      paramOutputStream.write(arrayOfByte, 0, i);
    }
  }
  
  private static ExpandedFunctionCall expandFunctionCall(Serving.FunctionCall paramFunctionCall, Serving.Resource paramResource, TypeSystem.Value[] paramArrayOfValue, int paramInt)
    throws ResourceUtil.InvalidResourceException
  {
    ExpandedFunctionCallBuilder localExpandedFunctionCallBuilder = ExpandedFunctionCall.newBuilder();
    paramFunctionCall = paramFunctionCall.property;
    int i = paramFunctionCall.length;
    paramInt = 0;
    if (paramInt >= i) {
      return localExpandedFunctionCallBuilder.build();
    }
    int j = paramFunctionCall[paramInt];
    Object localObject = (Serving.Property)getWithBoundsCheck(paramResource.property, Integer.valueOf(j).intValue(), "properties");
    String str = (String)getWithBoundsCheck(paramResource.key, ((Serving.Property)localObject).key, "keys");
    localObject = (TypeSystem.Value)getWithBoundsCheck(paramArrayOfValue, ((Serving.Property)localObject).value, "values");
    if (!Key.PUSH_AFTER_EVALUATE.toString().equals(str)) {
      localExpandedFunctionCallBuilder.addProperty(str, (TypeSystem.Value)localObject);
    }
    for (;;)
    {
      paramInt += 1;
      break;
      localExpandedFunctionCallBuilder.setPushAfterEvaluate((TypeSystem.Value)localObject);
    }
  }
  
  private static ExpandedRule expandRule(Serving.Rule paramRule, List<ExpandedFunctionCall> paramList1, List<ExpandedFunctionCall> paramList2, List<ExpandedFunctionCall> paramList3, Serving.Resource paramResource)
  {
    ExpandedRuleBuilder localExpandedRuleBuilder = ExpandedRule.newBuilder();
    int[] arrayOfInt = paramRule.positivePredicate;
    int j = arrayOfInt.length;
    int i = 0;
    if (i >= j)
    {
      arrayOfInt = paramRule.negativePredicate;
      j = arrayOfInt.length;
      i = 0;
      label40:
      if (i < j) {
        break label240;
      }
      paramList3 = paramRule.addTag;
      j = paramList3.length;
      i = 0;
      label59:
      if (i < j) {
        break label275;
      }
      paramList3 = paramRule.addTagRuleName;
      j = paramList3.length;
      i = 0;
      label78:
      if (i < j) {
        break label309;
      }
      paramList3 = paramRule.removeTag;
      j = paramList3.length;
      i = 0;
      label97:
      if (i < j) {
        break label347;
      }
      paramList1 = paramRule.removeTagRuleName;
      j = paramList1.length;
      i = 0;
      label116:
      if (i < j) {
        break label381;
      }
      paramList1 = paramRule.addMacro;
      j = paramList1.length;
      i = 0;
      label135:
      if (i < j) {
        break label419;
      }
      paramList1 = paramRule.addMacroRuleName;
      j = paramList1.length;
      i = 0;
      label154:
      if (i < j) {
        break label453;
      }
      paramList1 = paramRule.removeMacro;
      j = paramList1.length;
      i = 0;
      label173:
      if (i < j) {
        break label491;
      }
      paramRule = paramRule.removeMacroRuleName;
      j = paramRule.length;
      i = 0;
    }
    for (;;)
    {
      if (i >= j)
      {
        return localExpandedRuleBuilder.build();
        localExpandedRuleBuilder.addPositivePredicate((ExpandedFunctionCall)paramList3.get(Integer.valueOf(arrayOfInt[i]).intValue()));
        i += 1;
        break;
        label240:
        localExpandedRuleBuilder.addNegativePredicate((ExpandedFunctionCall)paramList3.get(Integer.valueOf(arrayOfInt[i]).intValue()));
        i += 1;
        break label40;
        label275:
        localExpandedRuleBuilder.addAddTag((ExpandedFunctionCall)paramList1.get(Integer.valueOf(paramList3[i]).intValue()));
        i += 1;
        break label59;
        label309:
        k = paramList3[i];
        localExpandedRuleBuilder.addAddTagRuleName(paramResource.value[Integer.valueOf(k).intValue()].string);
        i += 1;
        break label78;
        label347:
        localExpandedRuleBuilder.addRemoveTag((ExpandedFunctionCall)paramList1.get(Integer.valueOf(paramList3[i]).intValue()));
        i += 1;
        break label97;
        label381:
        k = paramList1[i];
        localExpandedRuleBuilder.addRemoveTagRuleName(paramResource.value[Integer.valueOf(k).intValue()].string);
        i += 1;
        break label116;
        label419:
        localExpandedRuleBuilder.addAddMacro((ExpandedFunctionCall)paramList2.get(Integer.valueOf(paramList1[i]).intValue()));
        i += 1;
        break label135;
        label453:
        k = paramList1[i];
        localExpandedRuleBuilder.addAddMacroRuleName(paramResource.value[Integer.valueOf(k).intValue()].string);
        i += 1;
        break label154;
        label491:
        localExpandedRuleBuilder.addRemoveMacro((ExpandedFunctionCall)paramList2.get(Integer.valueOf(paramList1[i]).intValue()));
        i += 1;
        break label173;
      }
      int k = paramRule[i];
      localExpandedRuleBuilder.addRemoveMacroRuleName(paramResource.value[Integer.valueOf(k).intValue()].string);
      i += 1;
    }
  }
  
  private static TypeSystem.Value expandValue(int paramInt, Serving.Resource paramResource, TypeSystem.Value[] paramArrayOfValue, Set<Integer> paramSet)
    throws ResourceUtil.InvalidResourceException
  {
    Object localObject = null;
    int k = 0;
    int m = 0;
    int j = 0;
    TypeSystem.Value localValue1;
    if (!paramSet.contains(Integer.valueOf(paramInt)))
    {
      localValue1 = (TypeSystem.Value)getWithBoundsCheck(paramResource.value, paramInt, "values");
      if (paramArrayOfValue[paramInt] != null) {
        break label176;
      }
      paramSet.add(Integer.valueOf(paramInt));
      switch (localValue1.type)
      {
      default: 
        label108:
        if (localObject == null) {
          break;
        }
      }
    }
    for (;;)
    {
      paramArrayOfValue[paramInt] = localObject;
      paramSet.remove(Integer.valueOf(paramInt));
      return (TypeSystem.Value)localObject;
      logAndThrow("Value cycle detected.  Current value reference: " + paramInt + "." + "  Previous value references: " + paramSet + ".");
      break;
      label176:
      return paramArrayOfValue[paramInt];
      localObject = getServingValue(localValue1);
      TypeSystem.Value localValue2 = newValueBasedOnValue(localValue1);
      localValue2.listItem = new TypeSystem.Value[((Serving.ServingValue)localObject).listItem.length];
      int[] arrayOfInt = ((Serving.ServingValue)localObject).listItem;
      k = arrayOfInt.length;
      int i = 0;
      for (;;)
      {
        localObject = localValue2;
        if (j >= k) {
          break;
        }
        m = arrayOfInt[j];
        localValue2.listItem[i] = expandValue(m, paramResource, paramArrayOfValue, paramSet);
        j += 1;
        i += 1;
      }
      localValue2 = newValueBasedOnValue(localValue1);
      localObject = getServingValue(localValue1);
      if (((Serving.ServingValue)localObject).mapKey.length == ((Serving.ServingValue)localObject).mapValue.length)
      {
        label301:
        localValue2.mapKey = new TypeSystem.Value[((Serving.ServingValue)localObject).mapKey.length];
        localValue2.mapValue = new TypeSystem.Value[((Serving.ServingValue)localObject).mapKey.length];
        arrayOfInt = ((Serving.ServingValue)localObject).mapKey;
        m = arrayOfInt.length;
        j = 0;
        i = 0;
      }
      for (;;)
      {
        if (j >= m)
        {
          arrayOfInt = ((Serving.ServingValue)localObject).mapValue;
          m = arrayOfInt.length;
          i = 0;
          j = k;
          for (;;)
          {
            localObject = localValue2;
            if (j >= m) {
              break;
            }
            k = arrayOfInt[j];
            localValue2.mapValue[i] = expandValue(k, paramResource, paramArrayOfValue, paramSet);
            j += 1;
            i += 1;
          }
          logAndThrow("Uneven map keys (" + ((Serving.ServingValue)localObject).mapKey.length + ") and map values (" + ((Serving.ServingValue)localObject).mapValue.length + ")");
          break label301;
        }
        int n = arrayOfInt[j];
        localValue2.mapKey[i] = expandValue(n, paramResource, paramArrayOfValue, paramSet);
        j += 1;
        i += 1;
      }
      localObject = newValueBasedOnValue(localValue1);
      ((TypeSystem.Value)localObject).macroReference = Types.valueToString(expandValue(getServingValue(localValue1).macroNameReference, paramResource, paramArrayOfValue, paramSet));
      break label108;
      localValue2 = newValueBasedOnValue(localValue1);
      localObject = getServingValue(localValue1);
      localValue2.templateToken = new TypeSystem.Value[((Serving.ServingValue)localObject).templateToken.length];
      arrayOfInt = ((Serving.ServingValue)localObject).templateToken;
      k = arrayOfInt.length;
      i = 0;
      j = m;
      for (;;)
      {
        localObject = localValue2;
        if (j >= k) {
          break;
        }
        m = arrayOfInt[j];
        localValue2.templateToken[i] = expandValue(m, paramResource, paramArrayOfValue, paramSet);
        j += 1;
        i += 1;
      }
      localObject = localValue1;
      break label108;
      logAndThrow("Invalid value: " + localValue1);
    }
  }
  
  public static ExpandedResource getExpandedResource(Serving.Resource paramResource)
    throws ResourceUtil.InvalidResourceException
  {
    int j = 0;
    Object localObject = new TypeSystem.Value[paramResource.value.length];
    int i = 0;
    ExpandedResourceBuilder localExpandedResourceBuilder;
    ArrayList localArrayList1;
    label39:
    ArrayList localArrayList2;
    label59:
    ArrayList localArrayList3;
    label79:
    int k;
    if (i >= paramResource.value.length)
    {
      localExpandedResourceBuilder = ExpandedResource.newBuilder();
      localArrayList1 = new ArrayList();
      i = 0;
      if (i < paramResource.tag.length) {
        break label154;
      }
      localArrayList2 = new ArrayList();
      i = 0;
      if (i < paramResource.predicate.length) {
        break label182;
      }
      localArrayList3 = new ArrayList();
      i = 0;
      if (i < paramResource.macro.length) {
        break label210;
      }
      localObject = paramResource.rule;
      k = localObject.length;
      i = j;
    }
    for (;;)
    {
      if (i >= k)
      {
        localExpandedResourceBuilder.setVersion(paramResource.version);
        localExpandedResourceBuilder.setResourceFormatVersion(paramResource.resourceFormatVersion);
        return localExpandedResourceBuilder.build();
        expandValue(i, paramResource, (TypeSystem.Value[])localObject, new HashSet(0));
        i += 1;
        break;
        label154:
        localArrayList1.add(expandFunctionCall(paramResource.tag[i], paramResource, (TypeSystem.Value[])localObject, i));
        i += 1;
        break label39;
        label182:
        localArrayList2.add(expandFunctionCall(paramResource.predicate[i], paramResource, (TypeSystem.Value[])localObject, i));
        i += 1;
        break label59;
        label210:
        ExpandedFunctionCall localExpandedFunctionCall = expandFunctionCall(paramResource.macro[i], paramResource, (TypeSystem.Value[])localObject, i);
        localExpandedResourceBuilder.addMacro(localExpandedFunctionCall);
        localArrayList3.add(localExpandedFunctionCall);
        i += 1;
        break label79;
      }
      localExpandedResourceBuilder.addRule(expandRule(localObject[i], localArrayList1, localArrayList3, localArrayList2, paramResource));
      i += 1;
    }
  }
  
  private static Serving.ServingValue getServingValue(TypeSystem.Value paramValue)
    throws ResourceUtil.InvalidResourceException
  {
    if ((Serving.ServingValue)paramValue.getExtension(Serving.ServingValue.ext) != null) {}
    for (;;)
    {
      return (Serving.ServingValue)paramValue.getExtension(Serving.ServingValue.ext);
      logAndThrow("Expected a ServingValue and didn't get one. Value is: " + paramValue);
    }
  }
  
  private static <T> T getWithBoundsCheck(List<T> paramList, int paramInt, String paramString)
    throws ResourceUtil.InvalidResourceException
  {
    if (paramInt < 0) {
      logAndThrow("Index out of bounds detected: " + paramInt + " in " + paramString);
    }
    for (;;)
    {
      return (T)paramList.get(paramInt);
      if (paramInt >= paramList.size()) {
        break;
      }
    }
  }
  
  private static <T> T getWithBoundsCheck(T[] paramArrayOfT, int paramInt, String paramString)
    throws ResourceUtil.InvalidResourceException
  {
    if (paramInt < 0) {
      logAndThrow("Index out of bounds detected: " + paramInt + " in " + paramString);
    }
    for (;;)
    {
      return paramArrayOfT[paramInt];
      if (paramInt >= paramArrayOfT.length) {
        break;
      }
    }
  }
  
  private static void logAndThrow(String paramString)
    throws ResourceUtil.InvalidResourceException
  {
    Log.e(paramString);
    throw new InvalidResourceException(paramString);
  }
  
  public static TypeSystem.Value newValueBasedOnValue(TypeSystem.Value paramValue)
  {
    TypeSystem.Value localValue = new TypeSystem.Value();
    localValue.type = paramValue.type;
    localValue.escaping = ((int[])paramValue.escaping.clone());
    if (!paramValue.containsReferences) {
      return localValue;
    }
    localValue.containsReferences = paramValue.containsReferences;
    return localValue;
  }
  
  public static class ExpandedFunctionCall
  {
    private final Map<String, TypeSystem.Value> mPropertiesMap;
    private final TypeSystem.Value mPushAfterEvaluate;
    
    private ExpandedFunctionCall(Map<String, TypeSystem.Value> paramMap, TypeSystem.Value paramValue)
    {
      this.mPropertiesMap = paramMap;
      this.mPushAfterEvaluate = paramValue;
    }
    
    public static ResourceUtil.ExpandedFunctionCallBuilder newBuilder()
    {
      return new ResourceUtil.ExpandedFunctionCallBuilder(null);
    }
    
    public Map<String, TypeSystem.Value> getProperties()
    {
      return Collections.unmodifiableMap(this.mPropertiesMap);
    }
    
    public TypeSystem.Value getPushAfterEvaluate()
    {
      return this.mPushAfterEvaluate;
    }
    
    public String toString()
    {
      return "Properties: " + getProperties() + " pushAfterEvaluate: " + this.mPushAfterEvaluate;
    }
    
    public void updateCacheableProperty(String paramString, TypeSystem.Value paramValue)
    {
      this.mPropertiesMap.put(paramString, paramValue);
    }
  }
  
  public static class ExpandedFunctionCallBuilder
  {
    private final Map<String, TypeSystem.Value> mPropertiesMap = new HashMap();
    private TypeSystem.Value mPushAfterEvaluate;
    
    public ExpandedFunctionCallBuilder addProperty(String paramString, TypeSystem.Value paramValue)
    {
      this.mPropertiesMap.put(paramString, paramValue);
      return this;
    }
    
    public ResourceUtil.ExpandedFunctionCall build()
    {
      return new ResourceUtil.ExpandedFunctionCall(this.mPropertiesMap, this.mPushAfterEvaluate, null);
    }
    
    public ExpandedFunctionCallBuilder setPushAfterEvaluate(TypeSystem.Value paramValue)
    {
      this.mPushAfterEvaluate = paramValue;
      return this;
    }
  }
  
  public static class ExpandedResource
  {
    private final Map<String, List<ResourceUtil.ExpandedFunctionCall>> mMacros;
    private final int mResourceFormatVersion;
    private final List<ResourceUtil.ExpandedRule> mRules;
    private final String mVersion;
    
    private ExpandedResource(List<ResourceUtil.ExpandedRule> paramList, Map<String, List<ResourceUtil.ExpandedFunctionCall>> paramMap, String paramString, int paramInt)
    {
      this.mRules = Collections.unmodifiableList(paramList);
      this.mMacros = Collections.unmodifiableMap(paramMap);
      this.mVersion = paramString;
      this.mResourceFormatVersion = paramInt;
    }
    
    public static ResourceUtil.ExpandedResourceBuilder newBuilder()
    {
      return new ResourceUtil.ExpandedResourceBuilder(null);
    }
    
    public Map<String, List<ResourceUtil.ExpandedFunctionCall>> getAllMacros()
    {
      return this.mMacros;
    }
    
    public List<ResourceUtil.ExpandedFunctionCall> getMacros(String paramString)
    {
      return (List)this.mMacros.get(paramString);
    }
    
    public int getResourceFormatVersion()
    {
      return this.mResourceFormatVersion;
    }
    
    public List<ResourceUtil.ExpandedRule> getRules()
    {
      return this.mRules;
    }
    
    public String getVersion()
    {
      return this.mVersion;
    }
    
    public String toString()
    {
      return "Rules: " + getRules() + "  Macros: " + this.mMacros;
    }
  }
  
  public static class ExpandedResourceBuilder
  {
    private final Map<String, List<ResourceUtil.ExpandedFunctionCall>> mMacros = new HashMap();
    private int mResourceFormatVersion = 0;
    private final List<ResourceUtil.ExpandedRule> mRules = new ArrayList();
    private String mVersion = "";
    
    public ExpandedResourceBuilder addMacro(ResourceUtil.ExpandedFunctionCall paramExpandedFunctionCall)
    {
      String str = Types.valueToString((TypeSystem.Value)paramExpandedFunctionCall.getProperties().get(Key.INSTANCE_NAME.toString()));
      Object localObject = (List)this.mMacros.get(str);
      if (localObject != null) {}
      for (;;)
      {
        ((List)localObject).add(paramExpandedFunctionCall);
        return this;
        localObject = new ArrayList();
        this.mMacros.put(str, localObject);
      }
    }
    
    public ExpandedResourceBuilder addRule(ResourceUtil.ExpandedRule paramExpandedRule)
    {
      this.mRules.add(paramExpandedRule);
      return this;
    }
    
    public ResourceUtil.ExpandedResource build()
    {
      return new ResourceUtil.ExpandedResource(this.mRules, this.mMacros, this.mVersion, this.mResourceFormatVersion, null);
    }
    
    public ExpandedResourceBuilder setResourceFormatVersion(int paramInt)
    {
      this.mResourceFormatVersion = paramInt;
      return this;
    }
    
    public ExpandedResourceBuilder setVersion(String paramString)
    {
      this.mVersion = paramString;
      return this;
    }
  }
  
  public static class ExpandedRule
  {
    private final List<String> mAddMacroRuleNames;
    private final List<ResourceUtil.ExpandedFunctionCall> mAddMacros;
    private final List<String> mAddTagRuleNames;
    private final List<ResourceUtil.ExpandedFunctionCall> mAddTags;
    private final List<ResourceUtil.ExpandedFunctionCall> mNegativePredicates;
    private final List<ResourceUtil.ExpandedFunctionCall> mPositivePredicates;
    private final List<String> mRemoveMacroRuleNames;
    private final List<ResourceUtil.ExpandedFunctionCall> mRemoveMacros;
    private final List<String> mRemoveTagRuleNames;
    private final List<ResourceUtil.ExpandedFunctionCall> mRemoveTags;
    
    private ExpandedRule(List<ResourceUtil.ExpandedFunctionCall> paramList1, List<ResourceUtil.ExpandedFunctionCall> paramList2, List<ResourceUtil.ExpandedFunctionCall> paramList3, List<ResourceUtil.ExpandedFunctionCall> paramList4, List<ResourceUtil.ExpandedFunctionCall> paramList5, List<ResourceUtil.ExpandedFunctionCall> paramList6, List<String> paramList7, List<String> paramList8, List<String> paramList9, List<String> paramList10)
    {
      this.mPositivePredicates = Collections.unmodifiableList(paramList1);
      this.mNegativePredicates = Collections.unmodifiableList(paramList2);
      this.mAddTags = Collections.unmodifiableList(paramList3);
      this.mRemoveTags = Collections.unmodifiableList(paramList4);
      this.mAddMacros = Collections.unmodifiableList(paramList5);
      this.mRemoveMacros = Collections.unmodifiableList(paramList6);
      this.mAddMacroRuleNames = Collections.unmodifiableList(paramList7);
      this.mRemoveMacroRuleNames = Collections.unmodifiableList(paramList8);
      this.mAddTagRuleNames = Collections.unmodifiableList(paramList9);
      this.mRemoveTagRuleNames = Collections.unmodifiableList(paramList10);
    }
    
    public static ResourceUtil.ExpandedRuleBuilder newBuilder()
    {
      return new ResourceUtil.ExpandedRuleBuilder(null);
    }
    
    public List<String> getAddMacroRuleNames()
    {
      return this.mAddMacroRuleNames;
    }
    
    public List<ResourceUtil.ExpandedFunctionCall> getAddMacros()
    {
      return this.mAddMacros;
    }
    
    public List<String> getAddTagRuleNames()
    {
      return this.mAddTagRuleNames;
    }
    
    public List<ResourceUtil.ExpandedFunctionCall> getAddTags()
    {
      return this.mAddTags;
    }
    
    public List<ResourceUtil.ExpandedFunctionCall> getNegativePredicates()
    {
      return this.mNegativePredicates;
    }
    
    public List<ResourceUtil.ExpandedFunctionCall> getPositivePredicates()
    {
      return this.mPositivePredicates;
    }
    
    public List<String> getRemoveMacroRuleNames()
    {
      return this.mRemoveMacroRuleNames;
    }
    
    public List<ResourceUtil.ExpandedFunctionCall> getRemoveMacros()
    {
      return this.mRemoveMacros;
    }
    
    public List<String> getRemoveTagRuleNames()
    {
      return this.mRemoveTagRuleNames;
    }
    
    public List<ResourceUtil.ExpandedFunctionCall> getRemoveTags()
    {
      return this.mRemoveTags;
    }
    
    public String toString()
    {
      return "Positive predicates: " + getPositivePredicates() + "  Negative predicates: " + getNegativePredicates() + "  Add tags: " + getAddTags() + "  Remove tags: " + getRemoveTags() + "  Add macros: " + getAddMacros() + "  Remove macros: " + getRemoveMacros();
    }
  }
  
  public static class ExpandedRuleBuilder
  {
    private final List<String> mAddMacroRuleNames = new ArrayList();
    private final List<ResourceUtil.ExpandedFunctionCall> mAddMacros = new ArrayList();
    private final List<String> mAddTagRuleNames = new ArrayList();
    private final List<ResourceUtil.ExpandedFunctionCall> mAddTags = new ArrayList();
    private final List<ResourceUtil.ExpandedFunctionCall> mNegativePredicates = new ArrayList();
    private final List<ResourceUtil.ExpandedFunctionCall> mPositivePredicates = new ArrayList();
    private final List<String> mRemoveMacroRuleNames = new ArrayList();
    private final List<ResourceUtil.ExpandedFunctionCall> mRemoveMacros = new ArrayList();
    private final List<String> mRemoveTagRuleNames = new ArrayList();
    private final List<ResourceUtil.ExpandedFunctionCall> mRemoveTags = new ArrayList();
    
    public ExpandedRuleBuilder addAddMacro(ResourceUtil.ExpandedFunctionCall paramExpandedFunctionCall)
    {
      this.mAddMacros.add(paramExpandedFunctionCall);
      return this;
    }
    
    public ExpandedRuleBuilder addAddMacroRuleName(String paramString)
    {
      this.mAddMacroRuleNames.add(paramString);
      return this;
    }
    
    public ExpandedRuleBuilder addAddTag(ResourceUtil.ExpandedFunctionCall paramExpandedFunctionCall)
    {
      this.mAddTags.add(paramExpandedFunctionCall);
      return this;
    }
    
    public ExpandedRuleBuilder addAddTagRuleName(String paramString)
    {
      this.mAddTagRuleNames.add(paramString);
      return this;
    }
    
    public ExpandedRuleBuilder addNegativePredicate(ResourceUtil.ExpandedFunctionCall paramExpandedFunctionCall)
    {
      this.mNegativePredicates.add(paramExpandedFunctionCall);
      return this;
    }
    
    public ExpandedRuleBuilder addPositivePredicate(ResourceUtil.ExpandedFunctionCall paramExpandedFunctionCall)
    {
      this.mPositivePredicates.add(paramExpandedFunctionCall);
      return this;
    }
    
    public ExpandedRuleBuilder addRemoveMacro(ResourceUtil.ExpandedFunctionCall paramExpandedFunctionCall)
    {
      this.mRemoveMacros.add(paramExpandedFunctionCall);
      return this;
    }
    
    public ExpandedRuleBuilder addRemoveMacroRuleName(String paramString)
    {
      this.mRemoveMacroRuleNames.add(paramString);
      return this;
    }
    
    public ExpandedRuleBuilder addRemoveTag(ResourceUtil.ExpandedFunctionCall paramExpandedFunctionCall)
    {
      this.mRemoveTags.add(paramExpandedFunctionCall);
      return this;
    }
    
    public ExpandedRuleBuilder addRemoveTagRuleName(String paramString)
    {
      this.mRemoveTagRuleNames.add(paramString);
      return this;
    }
    
    public ResourceUtil.ExpandedRule build()
    {
      return new ResourceUtil.ExpandedRule(this.mPositivePredicates, this.mNegativePredicates, this.mAddTags, this.mRemoveTags, this.mAddMacros, this.mRemoveMacros, this.mAddMacroRuleNames, this.mRemoveMacroRuleNames, this.mAddTagRuleNames, this.mRemoveTagRuleNames, null);
    }
  }
  
  public static class InvalidResourceException
    extends Exception
  {
    public InvalidResourceException(String paramString)
    {
      super();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\ResourceUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */