package com.google.tagmanager;

import android.content.Context;
import com.google.analytics.containertag.common.Key;
import com.google.analytics.containertag.proto.Serving.Supplemental;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import com.google.android.gms.common.util.VisibleForTesting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class Runtime
{
  static final String DEFAULT_RULE_NAME = "Unknown";
  private static final ObjectAndStatic<TypeSystem.Value> DEFAULT_VALUE_AND_STATIC = new ObjectAndStatic(Types.getDefaultValue(), true);
  static final String EXPERIMENT_SUPPLEMENTAL_NAME_PREFIX = "gaExperiment:";
  private static final int MAX_CACHE_SIZE = 1048576;
  private final EventInfoDistributor eventInfoDistributor;
  private volatile String mCurrentEventName;
  private final DataLayer mDataLayer;
  private final Cache<ResourceUtil.ExpandedFunctionCall, ObjectAndStatic<TypeSystem.Value>> mFunctionCallCache;
  private final Cache<String, CachedMacro> mMacroEvaluationCache;
  private final Map<String, MacroInfo> mMacroLookup;
  private final Map<String, FunctionCallImplementation> mMacroMap;
  private final Map<String, FunctionCallImplementation> mPredicateMap;
  private final ResourceUtil.ExpandedResource mResource;
  private final Set<ResourceUtil.ExpandedRule> mRules;
  private final Map<String, FunctionCallImplementation> mTrackingTagMap;
  
  public Runtime(Context paramContext, ResourceUtil.ExpandedResource paramExpandedResource, DataLayer paramDataLayer, CustomFunctionCall.CustomEvaluator paramCustomEvaluator1, CustomFunctionCall.CustomEvaluator paramCustomEvaluator2)
  {
    this(paramContext, paramExpandedResource, paramDataLayer, paramCustomEvaluator1, paramCustomEvaluator2, new NoopEventInfoDistributor());
  }
  
  public Runtime(Context paramContext, ResourceUtil.ExpandedResource paramExpandedResource, DataLayer paramDataLayer, CustomFunctionCall.CustomEvaluator paramCustomEvaluator1, CustomFunctionCall.CustomEvaluator paramCustomEvaluator2, EventInfoDistributor paramEventInfoDistributor)
  {
    if (paramExpandedResource != null)
    {
      this.mResource = paramExpandedResource;
      this.mRules = new HashSet(paramExpandedResource.getRules());
      this.mDataLayer = paramDataLayer;
      this.eventInfoDistributor = paramEventInfoDistributor;
      paramExpandedResource = new CacheFactory.CacheSizeManager()
      {
        public int sizeOf(ResourceUtil.ExpandedFunctionCall paramAnonymousExpandedFunctionCall, ObjectAndStatic<TypeSystem.Value> paramAnonymousObjectAndStatic)
        {
          return ((TypeSystem.Value)paramAnonymousObjectAndStatic.getObject()).getCachedSize();
        }
      };
      this.mFunctionCallCache = new CacheFactory().createCache(1048576, paramExpandedResource);
      paramExpandedResource = new CacheFactory.CacheSizeManager()
      {
        public int sizeOf(String paramAnonymousString, Runtime.CachedMacro paramAnonymousCachedMacro)
        {
          return paramAnonymousString.length() + paramAnonymousCachedMacro.getSize();
        }
      };
      this.mMacroEvaluationCache = new CacheFactory().createCache(1048576, paramExpandedResource);
      this.mTrackingTagMap = new HashMap();
      addTrackingTag(new ArbitraryPixelTag(paramContext));
      addTrackingTag(new CustomFunctionCall(paramCustomEvaluator2));
      addTrackingTag(new DataLayerWriteTag(paramDataLayer));
      addTrackingTag(new UniversalAnalyticsTag(paramContext, paramDataLayer));
      this.mPredicateMap = new HashMap();
      addPredicate(new ContainsPredicate());
      addPredicate(new EndsWithPredicate());
      addPredicate(new EqualsPredicate());
      addPredicate(new GreaterEqualsPredicate());
      addPredicate(new GreaterThanPredicate());
      addPredicate(new LessEqualsPredicate());
      addPredicate(new LessThanPredicate());
      addPredicate(new RegexPredicate());
      addPredicate(new StartsWithPredicate());
      this.mMacroMap = new HashMap();
      addMacro(new AdvertiserIdMacro(paramContext));
      addMacro(new AdvertisingTrackingEnabledMacro());
      addMacro(new AdwordsClickReferrerMacro(paramContext));
      addMacro(new AppIdMacro(paramContext));
      addMacro(new AppNameMacro(paramContext));
      addMacro(new AppVersionMacro(paramContext));
      addMacro(new ConstantMacro());
      addMacro(new ContainerVersionMacro(this));
      addMacro(new CustomFunctionCall(paramCustomEvaluator1));
      addMacro(new DataLayerMacro(paramDataLayer));
      addMacro(new DeviceIdMacro(paramContext));
      addMacro(new DeviceNameMacro());
      addMacro(new EncodeMacro());
      addMacro(new EventMacro(this));
      addMacro(new GtmVersionMacro());
      addMacro(new HashMacro());
      addMacro(new InstallReferrerMacro(paramContext));
      addMacro(new JoinerMacro());
      addMacro(new LanguageMacro());
      addMacro(new MobileAdwordsUniqueIdMacro(paramContext));
      addMacro(new OsVersionMacro());
      addMacro(new PlatformMacro());
      addMacro(new RandomMacro());
      addMacro(new RegexGroupMacro());
      addMacro(new ResolutionMacro(paramContext));
      addMacro(new RuntimeVersionMacro());
      addMacro(new SdkVersionMacro());
      addMacro(new TimeMacro());
      this.mMacroLookup = new HashMap();
      paramExpandedResource = this.mRules.iterator();
      if (paramExpandedResource.hasNext()) {
        break label664;
      }
      paramContext = this.mResource.getAllMacros().entrySet().iterator();
    }
    for (;;)
    {
      if (!paramContext.hasNext())
      {
        return;
        throw new NullPointerException("resource cannot be null");
        label664:
        paramDataLayer = (ResourceUtil.ExpandedRule)paramExpandedResource.next();
        label684:
        int i;
        if (!paramEventInfoDistributor.debugMode())
        {
          i = 0;
          if (i < paramDataLayer.getAddMacros().size()) {
            break label850;
          }
          i = 0;
          label704:
          if (i < paramDataLayer.getRemoveMacros().size())
          {
            paramCustomEvaluator1 = (ResourceUtil.ExpandedFunctionCall)paramDataLayer.getRemoveMacros().get(i);
            paramContext = "Unknown";
            if (paramEventInfoDistributor.debugMode()) {
              break label955;
            }
          }
        }
        for (;;)
        {
          paramCustomEvaluator2 = getOrAddMacroInfo(this.mMacroLookup, getFunctionName(paramCustomEvaluator1));
          paramCustomEvaluator2.addRule(paramDataLayer);
          paramCustomEvaluator2.addRemoveMacroForRule(paramDataLayer, paramCustomEvaluator1);
          paramCustomEvaluator2.addRemoveMacroRuleNameForRule(paramDataLayer, paramContext);
          i += 1;
          break label704;
          break;
          verifyFunctionAndNameListSizes(paramDataLayer.getAddMacros(), paramDataLayer.getAddMacroRuleNames(), "add macro");
          verifyFunctionAndNameListSizes(paramDataLayer.getRemoveMacros(), paramDataLayer.getRemoveMacroRuleNames(), "remove macro");
          verifyFunctionAndNameListSizes(paramDataLayer.getAddTags(), paramDataLayer.getAddTagRuleNames(), "add tag");
          verifyFunctionAndNameListSizes(paramDataLayer.getRemoveTags(), paramDataLayer.getRemoveTagRuleNames(), "remove tag");
          break label684;
          label850:
          paramCustomEvaluator1 = (ResourceUtil.ExpandedFunctionCall)paramDataLayer.getAddMacros().get(i);
          paramContext = "Unknown";
          if (!paramEventInfoDistributor.debugMode()) {}
          for (;;)
          {
            paramCustomEvaluator2 = getOrAddMacroInfo(this.mMacroLookup, getFunctionName(paramCustomEvaluator1));
            paramCustomEvaluator2.addRule(paramDataLayer);
            paramCustomEvaluator2.addAddMacroForRule(paramDataLayer, paramCustomEvaluator1);
            paramCustomEvaluator2.addAddMacroRuleNameForRule(paramDataLayer, paramContext);
            i += 1;
            break;
            if (i < paramDataLayer.getAddMacroRuleNames().size()) {
              paramContext = (String)paramDataLayer.getAddMacroRuleNames().get(i);
            }
          }
          label955:
          if (i < paramDataLayer.getRemoveMacroRuleNames().size()) {
            paramContext = (String)paramDataLayer.getRemoveMacroRuleNames().get(i);
          }
        }
      }
      paramExpandedResource = (Map.Entry)paramContext.next();
      paramDataLayer = ((List)paramExpandedResource.getValue()).iterator();
      while (paramDataLayer.hasNext())
      {
        paramCustomEvaluator1 = (ResourceUtil.ExpandedFunctionCall)paramDataLayer.next();
        if (!Types.valueToBoolean((TypeSystem.Value)paramCustomEvaluator1.getProperties().get(Key.NOT_DEFAULT_MACRO.toString())).booleanValue()) {
          getOrAddMacroInfo(this.mMacroLookup, (String)paramExpandedResource.getKey()).setDefault(paramCustomEvaluator1);
        }
      }
    }
  }
  
  private static void addFunctionImplToMap(Map<String, FunctionCallImplementation> paramMap, FunctionCallImplementation paramFunctionCallImplementation)
  {
    if (!paramMap.containsKey(paramFunctionCallImplementation.getInstanceFunctionId()))
    {
      paramMap.put(paramFunctionCallImplementation.getInstanceFunctionId(), paramFunctionCallImplementation);
      return;
    }
    throw new IllegalArgumentException("Duplicate function type name: " + paramFunctionCallImplementation.getInstanceFunctionId());
  }
  
  private ObjectAndStatic<Set<ResourceUtil.ExpandedFunctionCall>> calculateGenericToRun(Set<ResourceUtil.ExpandedRule> paramSet, Set<String> paramSet1, AddRemoveSetPopulator paramAddRemoveSetPopulator, RuleEvaluationStepInfoBuilder paramRuleEvaluationStepInfoBuilder)
  {
    HashSet localHashSet1 = new HashSet();
    HashSet localHashSet2 = new HashSet();
    paramSet = paramSet.iterator();
    boolean bool = true;
    if (!paramSet.hasNext())
    {
      localHashSet1.removeAll(localHashSet2);
      paramRuleEvaluationStepInfoBuilder.setEnabledFunctions(localHashSet1);
      return new ObjectAndStatic(localHashSet1, bool);
    }
    ResourceUtil.ExpandedRule localExpandedRule = (ResourceUtil.ExpandedRule)paramSet.next();
    ResolvedRuleBuilder localResolvedRuleBuilder = paramRuleEvaluationStepInfoBuilder.createResolvedRuleBuilder();
    ObjectAndStatic localObjectAndStatic = evaluatePredicatesInRule(localExpandedRule, paramSet1, localResolvedRuleBuilder);
    if (!((Boolean)localObjectAndStatic.getObject()).booleanValue()) {
      label113:
      if (bool) {
        break label141;
      }
    }
    label118:
    for (bool = false;; bool = true)
    {
      break;
      paramAddRemoveSetPopulator.rulePassed(localExpandedRule, localHashSet1, localHashSet2, localResolvedRuleBuilder);
      break label113;
      label141:
      if (!localObjectAndStatic.isStatic()) {
        break label118;
      }
    }
  }
  
  private ObjectAndStatic<TypeSystem.Value> evaluateMacroReferenceCycleDetection(String paramString, Set<String> paramSet, MacroEvaluationInfoBuilder paramMacroEvaluationInfoBuilder)
  {
    Object localObject = (CachedMacro)this.mMacroEvaluationCache.get(paramString);
    label112:
    label135:
    label166:
    boolean bool;
    if (localObject == null)
    {
      localObject = (MacroInfo)this.mMacroLookup.get(paramString);
      if (localObject == null) {
        break label241;
      }
      ObjectAndStatic localObjectAndStatic = calculateMacrosToRun(paramString, ((MacroInfo)localObject).getRules(), ((MacroInfo)localObject).getAddMacros(), ((MacroInfo)localObject).getAddMacroRuleNames(), ((MacroInfo)localObject).getRemoveMacros(), ((MacroInfo)localObject).getRemoveMacroRuleNames(), paramSet, paramMacroEvaluationInfoBuilder.createRulesEvaluation());
      if (((Set)localObjectAndStatic.getObject()).isEmpty()) {
        break label268;
      }
      if (((Set)localObjectAndStatic.getObject()).size() > 1) {
        break label278;
      }
      localObject = (ResourceUtil.ExpandedFunctionCall)((Set)localObjectAndStatic.getObject()).iterator().next();
      if (localObject == null) {
        break label304;
      }
      paramMacroEvaluationInfoBuilder = executeFunction(this.mMacroMap, (ResourceUtil.ExpandedFunctionCall)localObject, paramSet, paramMacroEvaluationInfoBuilder.createResult());
      if (localObjectAndStatic.isStatic()) {
        break label308;
      }
      bool = false;
      label169:
      if (paramMacroEvaluationInfoBuilder == DEFAULT_VALUE_AND_STATIC) {
        break label321;
      }
      paramMacroEvaluationInfoBuilder = new ObjectAndStatic(paramMacroEvaluationInfoBuilder.getObject(), bool);
      label190:
      localObject = ((ResourceUtil.ExpandedFunctionCall)localObject).getPushAfterEvaluate();
      if (paramMacroEvaluationInfoBuilder.isStatic()) {
        break label328;
      }
    }
    for (;;)
    {
      pushUnevaluatedValueToDataLayer((TypeSystem.Value)localObject, paramSet);
      return paramMacroEvaluationInfoBuilder;
      if (this.eventInfoDistributor.debugMode()) {
        break;
      }
      pushUnevaluatedValueToDataLayer(((CachedMacro)localObject).getPushAfterEvaluate(), paramSet);
      return ((CachedMacro)localObject).getObjectAndStatic();
      label241:
      Log.e("Invalid macro: " + paramString);
      return DEFAULT_VALUE_AND_STATIC;
      label268:
      localObject = ((MacroInfo)localObject).getDefault();
      break label135;
      label278:
      Log.w("Multiple macros active for macroName " + paramString);
      break label112;
      label304:
      return DEFAULT_VALUE_AND_STATIC;
      label308:
      if (!paramMacroEvaluationInfoBuilder.isStatic()) {
        break label166;
      }
      bool = true;
      break label169;
      label321:
      paramMacroEvaluationInfoBuilder = DEFAULT_VALUE_AND_STATIC;
      break label190;
      label328:
      this.mMacroEvaluationCache.put(paramString, new CachedMacro(paramMacroEvaluationInfoBuilder, (TypeSystem.Value)localObject));
    }
  }
  
  private ObjectAndStatic<TypeSystem.Value> executeFunction(Map<String, FunctionCallImplementation> paramMap, ResourceUtil.ExpandedFunctionCall paramExpandedFunctionCall, Set<String> paramSet, ResolvedFunctionCallBuilder paramResolvedFunctionCallBuilder)
  {
    boolean bool = true;
    Object localObject1 = (TypeSystem.Value)paramExpandedFunctionCall.getProperties().get(Key.FUNCTION.toString());
    Object localObject2;
    Iterator localIterator;
    int i;
    if (localObject1 != null)
    {
      localObject1 = ((TypeSystem.Value)localObject1).functionId;
      paramMap = (FunctionCallImplementation)paramMap.get(localObject1);
      if (paramMap == null) {
        break label178;
      }
      localObject2 = (ObjectAndStatic)this.mFunctionCallCache.get(paramExpandedFunctionCall);
      if (localObject2 != null) {
        break label206;
      }
      localObject2 = new HashMap();
      localIterator = paramExpandedFunctionCall.getProperties().entrySet().iterator();
      i = 1;
      if (localIterator.hasNext()) {
        break label221;
      }
      if (!paramMap.hasRequiredKeys(((Map)localObject2).keySet())) {
        break label357;
      }
      if (i != 0) {
        break label414;
      }
      label128:
      bool = false;
      label131:
      paramMap = new ObjectAndStatic(paramMap.evaluate((Map)localObject2), bool);
      if (bool) {
        break label424;
      }
    }
    for (;;)
    {
      paramResolvedFunctionCallBuilder.setFunctionResult((TypeSystem.Value)paramMap.getObject());
      return paramMap;
      Log.e("No function id in properties");
      return DEFAULT_VALUE_AND_STATIC;
      label178:
      Log.e((String)localObject1 + " has no backing implementation.");
      return DEFAULT_VALUE_AND_STATIC;
      label206:
      if (this.eventInfoDistributor.debugMode()) {
        break;
      }
      return (ObjectAndStatic<TypeSystem.Value>)localObject2;
      label221:
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Object localObject3 = paramResolvedFunctionCallBuilder.createResolvedPropertyBuilder((String)localEntry.getKey());
      localObject3 = macroExpandValue((TypeSystem.Value)localEntry.getValue(), paramSet, ((ResolvedPropertyBuilder)localObject3).createPropertyValueBuilder((TypeSystem.Value)localEntry.getValue()));
      if (localObject3 != DEFAULT_VALUE_AND_STATIC)
      {
        if (((ObjectAndStatic)localObject3).isStatic()) {
          break label332;
        }
        i = 0;
      }
      for (;;)
      {
        ((Map)localObject2).put(localEntry.getKey(), ((ObjectAndStatic)localObject3).getObject());
        break;
        return DEFAULT_VALUE_AND_STATIC;
        label332:
        paramExpandedFunctionCall.updateCacheableProperty((String)localEntry.getKey(), (TypeSystem.Value)((ObjectAndStatic)localObject3).getObject());
      }
      label357:
      Log.e("Incorrect keys for function " + (String)localObject1 + " required " + paramMap.getRequiredKeys() + " had " + ((Map)localObject2).keySet());
      return DEFAULT_VALUE_AND_STATIC;
      label414:
      if (paramMap.isCacheable()) {
        break label131;
      }
      break label128;
      label424:
      this.mFunctionCallCache.put(paramExpandedFunctionCall, paramMap);
    }
  }
  
  private static String getFunctionName(ResourceUtil.ExpandedFunctionCall paramExpandedFunctionCall)
  {
    return Types.valueToString((TypeSystem.Value)paramExpandedFunctionCall.getProperties().get(Key.INSTANCE_NAME.toString()));
  }
  
  private static MacroInfo getOrAddMacroInfo(Map<String, MacroInfo> paramMap, String paramString)
  {
    MacroInfo localMacroInfo = (MacroInfo)paramMap.get(paramString);
    if (localMacroInfo != null) {
      return localMacroInfo;
    }
    localMacroInfo = new MacroInfo();
    paramMap.put(paramString, localMacroInfo);
    return localMacroInfo;
  }
  
  private ObjectAndStatic<TypeSystem.Value> macroExpandValue(TypeSystem.Value paramValue, Set<String> paramSet, ValueBuilder paramValueBuilder)
  {
    if (paramValue.containsReferences) {}
    ObjectAndStatic localObjectAndStatic1;
    switch (paramValue.type)
    {
    case 5: 
    case 6: 
    default: 
      Log.e("Unknown type: " + paramValue.type);
      return DEFAULT_VALUE_AND_STATIC;
      return new ObjectAndStatic(paramValue, true);
    case 2: 
      localValue = ResourceUtil.newValueBasedOnValue(paramValue);
      localValue.listItem = new TypeSystem.Value[paramValue.listItem.length];
      i = 0;
      for (;;)
      {
        if (i >= paramValue.listItem.length) {
          return new ObjectAndStatic(localValue, false);
        }
        localObjectAndStatic1 = macroExpandValue(paramValue.listItem[i], paramSet, paramValueBuilder.getListItem(i));
        if (localObjectAndStatic1 == DEFAULT_VALUE_AND_STATIC) {
          break;
        }
        localValue.listItem[i] = ((TypeSystem.Value)localObjectAndStatic1.getObject());
        i += 1;
      }
      return DEFAULT_VALUE_AND_STATIC;
    case 3: 
      localValue = ResourceUtil.newValueBasedOnValue(paramValue);
      if (paramValue.mapKey.length == paramValue.mapValue.length)
      {
        localValue.mapKey = new TypeSystem.Value[paramValue.mapKey.length];
        localValue.mapValue = new TypeSystem.Value[paramValue.mapKey.length];
        i = 0;
      }
      for (;;)
      {
        if (i >= paramValue.mapKey.length)
        {
          return new ObjectAndStatic(localValue, false);
          Log.e("Invalid serving value: " + paramValue.toString());
          return DEFAULT_VALUE_AND_STATIC;
        }
        localObjectAndStatic1 = macroExpandValue(paramValue.mapKey[i], paramSet, paramValueBuilder.getMapKey(i));
        ObjectAndStatic localObjectAndStatic2 = macroExpandValue(paramValue.mapValue[i], paramSet, paramValueBuilder.getMapValue(i));
        if (localObjectAndStatic1 == DEFAULT_VALUE_AND_STATIC) {}
        while (localObjectAndStatic2 == DEFAULT_VALUE_AND_STATIC) {
          return DEFAULT_VALUE_AND_STATIC;
        }
        localValue.mapKey[i] = ((TypeSystem.Value)localObjectAndStatic1.getObject());
        localValue.mapValue[i] = ((TypeSystem.Value)localObjectAndStatic2.getObject());
        i += 1;
      }
    case 4: 
      if (!paramSet.contains(paramValue.macroReference))
      {
        paramSet.add(paramValue.macroReference);
        paramValueBuilder = ValueEscapeUtil.applyEscapings(evaluateMacroReferenceCycleDetection(paramValue.macroReference, paramSet, paramValueBuilder.createValueMacroEvaluationInfoExtension()), paramValue.escaping);
        paramSet.remove(paramValue.macroReference);
        return paramValueBuilder;
      }
      Log.e("Macro cycle detected.  Current macro reference: " + paramValue.macroReference + "." + "  Previous macro references: " + paramSet.toString() + ".");
      return DEFAULT_VALUE_AND_STATIC;
    }
    TypeSystem.Value localValue = ResourceUtil.newValueBasedOnValue(paramValue);
    localValue.templateToken = new TypeSystem.Value[paramValue.templateToken.length];
    int i = 0;
    for (;;)
    {
      if (i >= paramValue.templateToken.length) {
        return new ObjectAndStatic(localValue, false);
      }
      localObjectAndStatic1 = macroExpandValue(paramValue.templateToken[i], paramSet, paramValueBuilder.getTemplateToken(i));
      if (localObjectAndStatic1 == DEFAULT_VALUE_AND_STATIC) {
        break;
      }
      localValue.templateToken[i] = ((TypeSystem.Value)localObjectAndStatic1.getObject());
      i += 1;
    }
    return DEFAULT_VALUE_AND_STATIC;
  }
  
  private void pushUnevaluatedValueToDataLayer(TypeSystem.Value paramValue, Set<String> paramSet)
  {
    if (paramValue != null)
    {
      paramValue = macroExpandValue(paramValue, paramSet, new NoopValueBuilder());
      if (paramValue != DEFAULT_VALUE_AND_STATIC) {
        break label27;
      }
    }
    for (;;)
    {
      return;
      return;
      label27:
      paramValue = Types.valueToObject((TypeSystem.Value)paramValue.getObject());
      if (!(paramValue instanceof Map))
      {
        if (!(paramValue instanceof List)) {
          Log.w("pushAfterEvaluate: value not a Map or List");
        }
      }
      else
      {
        paramValue = (Map)paramValue;
        this.mDataLayer.push(paramValue);
        return;
      }
      paramValue = ((List)paramValue).iterator();
      while (paramValue.hasNext())
      {
        paramSet = paramValue.next();
        if (!(paramSet instanceof Map))
        {
          Log.w("pushAfterEvaluate: value not a Map");
        }
        else
        {
          paramSet = (Map)paramSet;
          this.mDataLayer.push(paramSet);
        }
      }
    }
  }
  
  private static void verifyFunctionAndNameListSizes(List<ResourceUtil.ExpandedFunctionCall> paramList, List<String> paramList1, String paramString)
  {
    if (paramList.size() == paramList1.size()) {
      return;
    }
    Log.i("Invalid resource: imbalance of rule names of functions for " + paramString + " operation. Using default rule name instead");
  }
  
  @VisibleForTesting
  void addMacro(FunctionCallImplementation paramFunctionCallImplementation)
  {
    addFunctionImplToMap(this.mMacroMap, paramFunctionCallImplementation);
  }
  
  @VisibleForTesting
  void addPredicate(FunctionCallImplementation paramFunctionCallImplementation)
  {
    addFunctionImplToMap(this.mPredicateMap, paramFunctionCallImplementation);
  }
  
  @VisibleForTesting
  void addTrackingTag(FunctionCallImplementation paramFunctionCallImplementation)
  {
    addFunctionImplToMap(this.mTrackingTagMap, paramFunctionCallImplementation);
  }
  
  @VisibleForTesting
  ObjectAndStatic<Set<ResourceUtil.ExpandedFunctionCall>> calculateMacrosToRun(String paramString, Set<ResourceUtil.ExpandedRule> paramSet, final Map<ResourceUtil.ExpandedRule, List<ResourceUtil.ExpandedFunctionCall>> paramMap1, final Map<ResourceUtil.ExpandedRule, List<String>> paramMap2, final Map<ResourceUtil.ExpandedRule, List<ResourceUtil.ExpandedFunctionCall>> paramMap3, final Map<ResourceUtil.ExpandedRule, List<String>> paramMap4, Set<String> paramSet1, RuleEvaluationStepInfoBuilder paramRuleEvaluationStepInfoBuilder)
  {
    calculateGenericToRun(paramSet, paramSet1, new AddRemoveSetPopulator()
    {
      public void rulePassed(ResourceUtil.ExpandedRule paramAnonymousExpandedRule, Set<ResourceUtil.ExpandedFunctionCall> paramAnonymousSet1, Set<ResourceUtil.ExpandedFunctionCall> paramAnonymousSet2, ResolvedRuleBuilder paramAnonymousResolvedRuleBuilder)
      {
        List localList1 = (List)paramMap1.get(paramAnonymousExpandedRule);
        List localList2 = (List)paramMap2.get(paramAnonymousExpandedRule);
        if (localList1 == null) {}
        for (;;)
        {
          paramAnonymousSet1 = (List)paramMap3.get(paramAnonymousExpandedRule);
          paramAnonymousExpandedRule = (List)paramMap4.get(paramAnonymousExpandedRule);
          if (paramAnonymousSet1 != null) {
            break;
          }
          return;
          paramAnonymousSet1.addAll(localList1);
          paramAnonymousResolvedRuleBuilder.getAddedMacroFunctions().translateAndAddAll(localList1, localList2);
        }
        paramAnonymousSet2.addAll(paramAnonymousSet1);
        paramAnonymousResolvedRuleBuilder.getRemovedMacroFunctions().translateAndAddAll(paramAnonymousSet1, paramAnonymousExpandedRule);
      }
    }, paramRuleEvaluationStepInfoBuilder);
  }
  
  @VisibleForTesting
  ObjectAndStatic<Set<ResourceUtil.ExpandedFunctionCall>> calculateTagsToRun(Set<ResourceUtil.ExpandedRule> paramSet, RuleEvaluationStepInfoBuilder paramRuleEvaluationStepInfoBuilder)
  {
    calculateGenericToRun(paramSet, new HashSet(), new AddRemoveSetPopulator()
    {
      public void rulePassed(ResourceUtil.ExpandedRule paramAnonymousExpandedRule, Set<ResourceUtil.ExpandedFunctionCall> paramAnonymousSet1, Set<ResourceUtil.ExpandedFunctionCall> paramAnonymousSet2, ResolvedRuleBuilder paramAnonymousResolvedRuleBuilder)
      {
        paramAnonymousSet1.addAll(paramAnonymousExpandedRule.getAddTags());
        paramAnonymousSet2.addAll(paramAnonymousExpandedRule.getRemoveTags());
        paramAnonymousResolvedRuleBuilder.getAddedTagFunctions().translateAndAddAll(paramAnonymousExpandedRule.getAddTags(), paramAnonymousExpandedRule.getAddTagRuleNames());
        paramAnonymousResolvedRuleBuilder.getRemovedTagFunctions().translateAndAddAll(paramAnonymousExpandedRule.getRemoveTags(), paramAnonymousExpandedRule.getRemoveTagRuleNames());
      }
    }, paramRuleEvaluationStepInfoBuilder);
  }
  
  public ObjectAndStatic<TypeSystem.Value> evaluateMacroReference(String paramString)
  {
    EventInfoBuilder localEventInfoBuilder = this.eventInfoDistributor.createMacroEvalutionEventInfo(paramString);
    paramString = evaluateMacroReferenceCycleDetection(paramString, new HashSet(), localEventInfoBuilder.createMacroEvaluationInfoBuilder());
    localEventInfoBuilder.processEventInfo();
    return paramString;
  }
  
  @VisibleForTesting
  ObjectAndStatic<Boolean> evaluatePredicate(ResourceUtil.ExpandedFunctionCall paramExpandedFunctionCall, Set<String> paramSet, ResolvedFunctionCallBuilder paramResolvedFunctionCallBuilder)
  {
    paramExpandedFunctionCall = executeFunction(this.mPredicateMap, paramExpandedFunctionCall, paramSet, paramResolvedFunctionCallBuilder);
    paramSet = Types.valueToBoolean((TypeSystem.Value)paramExpandedFunctionCall.getObject());
    paramResolvedFunctionCallBuilder.setFunctionResult(Types.objectToValue(paramSet));
    return new ObjectAndStatic(paramSet, paramExpandedFunctionCall.isStatic());
  }
  
  @VisibleForTesting
  ObjectAndStatic<Boolean> evaluatePredicatesInRule(ResourceUtil.ExpandedRule paramExpandedRule, Set<String> paramSet, ResolvedRuleBuilder paramResolvedRuleBuilder)
  {
    Object localObject = paramExpandedRule.getNegativePredicates().iterator();
    boolean bool = true;
    if (!((Iterator)localObject).hasNext()) {
      paramExpandedRule = paramExpandedRule.getPositivePredicates().iterator();
    }
    for (;;)
    {
      if (!paramExpandedRule.hasNext())
      {
        paramResolvedRuleBuilder.setValue(Types.objectToValue(Boolean.valueOf(true)));
        return new ObjectAndStatic(Boolean.valueOf(true), bool);
        ObjectAndStatic localObjectAndStatic = evaluatePredicate((ResourceUtil.ExpandedFunctionCall)((Iterator)localObject).next(), paramSet, paramResolvedRuleBuilder.createNegativePredicate());
        if (!((Boolean)localObjectAndStatic.getObject()).booleanValue()) {
          if (bool) {
            break label148;
          }
        }
        label112:
        for (bool = false;; bool = true)
        {
          break;
          paramResolvedRuleBuilder.setValue(Types.objectToValue(Boolean.valueOf(false)));
          return new ObjectAndStatic(Boolean.valueOf(false), localObjectAndStatic.isStatic());
          label148:
          if (!localObjectAndStatic.isStatic()) {
            break label112;
          }
        }
      }
      localObject = evaluatePredicate((ResourceUtil.ExpandedFunctionCall)paramExpandedRule.next(), paramSet, paramResolvedRuleBuilder.createPositivePredicate());
      if (((Boolean)((ObjectAndStatic)localObject).getObject()).booleanValue()) {
        if (bool) {
          break label239;
        }
      }
      label239:
      while (!((ObjectAndStatic)localObject).isStatic())
      {
        bool = false;
        break;
        paramResolvedRuleBuilder.setValue(Types.objectToValue(Boolean.valueOf(false)));
        return new ObjectAndStatic(Boolean.valueOf(false), ((ObjectAndStatic)localObject).isStatic());
      }
      bool = true;
    }
  }
  
  /* Error */
  public void evaluateTags(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_1
    //   4: invokevirtual 800	com/google/tagmanager/Runtime:setCurrentEventName	(Ljava/lang/String;)V
    //   7: aload_0
    //   8: getfield 101	com/google/tagmanager/Runtime:eventInfoDistributor	Lcom/google/tagmanager/EventInfoDistributor;
    //   11: aload_1
    //   12: invokeinterface 803 2 0
    //   17: astore_1
    //   18: aload_1
    //   19: invokeinterface 807 1 0
    //   24: astore_2
    //   25: aload_0
    //   26: aload_0
    //   27: getfield 97	com/google/tagmanager/Runtime:mRules	Ljava/util/Set;
    //   30: aload_2
    //   31: invokeinterface 810 1 0
    //   36: invokevirtual 812	com/google/tagmanager/Runtime:calculateTagsToRun	(Ljava/util/Set;Lcom/google/tagmanager/RuleEvaluationStepInfoBuilder;)Lcom/google/tagmanager/ObjectAndStatic;
    //   39: invokevirtual 475	com/google/tagmanager/ObjectAndStatic:getObject	()Ljava/lang/Object;
    //   42: checkcast 267	java/util/Set
    //   45: invokeinterface 271 1 0
    //   50: astore_3
    //   51: aload_3
    //   52: invokeinterface 277 1 0
    //   57: ifne +17 -> 74
    //   60: aload_1
    //   61: invokeinterface 764 1 0
    //   66: aload_0
    //   67: aconst_null
    //   68: invokevirtual 800	com/google/tagmanager/Runtime:setCurrentEventName	(Ljava/lang/String;)V
    //   71: aload_0
    //   72: monitorexit
    //   73: return
    //   74: aload_3
    //   75: invokeinterface 298 1 0
    //   80: checkcast 323	com/google/tagmanager/ResourceUtil$ExpandedFunctionCall
    //   83: astore 4
    //   85: aload_0
    //   86: aload_0
    //   87: getfield 121	com/google/tagmanager/Runtime:mTrackingTagMap	Ljava/util/Map;
    //   90: aload 4
    //   92: new 86	java/util/HashSet
    //   95: dup
    //   96: invokespecial 454	java/util/HashSet:<init>	()V
    //   99: aload_2
    //   100: invokeinterface 815 1 0
    //   105: invokespecial 520	com/google/tagmanager/Runtime:executeFunction	(Ljava/util/Map;Lcom/google/tagmanager/ResourceUtil$ExpandedFunctionCall;Ljava/util/Set;Lcom/google/tagmanager/ResolvedFunctionCallBuilder;)Lcom/google/tagmanager/ObjectAndStatic;
    //   108: pop
    //   109: goto -58 -> 51
    //   112: astore_1
    //   113: aload_0
    //   114: monitorexit
    //   115: aload_1
    //   116: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	117	0	this	Runtime
    //   0	117	1	paramString	String
    //   24	76	2	localDataLayerEventEvaluationInfoBuilder	DataLayerEventEvaluationInfoBuilder
    //   50	25	3	localIterator	Iterator
    //   83	8	4	localExpandedFunctionCall	ResourceUtil.ExpandedFunctionCall
    // Exception table:
    //   from	to	target	type
    //   2	51	112	finally
    //   51	71	112	finally
    //   74	109	112	finally
  }
  
  String getCurrentEventName()
  {
    try
    {
      String str = this.mCurrentEventName;
      return str;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public ResourceUtil.ExpandedResource getResource()
  {
    return this.mResource;
  }
  
  @VisibleForTesting
  void setCurrentEventName(String paramString)
  {
    try
    {
      this.mCurrentEventName = paramString;
      return;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  public void setSupplementals(List<Serving.Supplemental> paramList)
  {
    for (;;)
    {
      Serving.Supplemental localSupplemental;
      try
      {
        paramList = paramList.iterator();
        boolean bool = paramList.hasNext();
        if (!bool) {
          return;
        }
        localSupplemental = (Serving.Supplemental)paramList.next();
        if (localSupplemental.name == null)
        {
          Log.v("Ignored supplemental: " + localSupplemental);
          continue;
        }
        if (!localSupplemental.name.startsWith("gaExperiment:")) {
          continue;
        }
      }
      finally {}
      ExperimentMacroHelper.handleExperimentSupplemental(this.mDataLayer, localSupplemental);
    }
  }
  
  static abstract interface AddRemoveSetPopulator
  {
    public abstract void rulePassed(ResourceUtil.ExpandedRule paramExpandedRule, Set<ResourceUtil.ExpandedFunctionCall> paramSet1, Set<ResourceUtil.ExpandedFunctionCall> paramSet2, ResolvedRuleBuilder paramResolvedRuleBuilder);
  }
  
  private static class CachedMacro
  {
    private ObjectAndStatic<TypeSystem.Value> mObjectAndStatic;
    private TypeSystem.Value mPushAfterEvaluate;
    
    public CachedMacro(ObjectAndStatic<TypeSystem.Value> paramObjectAndStatic)
    {
      this(paramObjectAndStatic, null);
    }
    
    public CachedMacro(ObjectAndStatic<TypeSystem.Value> paramObjectAndStatic, TypeSystem.Value paramValue)
    {
      this.mObjectAndStatic = paramObjectAndStatic;
      this.mPushAfterEvaluate = paramValue;
    }
    
    public ObjectAndStatic<TypeSystem.Value> getObjectAndStatic()
    {
      return this.mObjectAndStatic;
    }
    
    public TypeSystem.Value getPushAfterEvaluate()
    {
      return this.mPushAfterEvaluate;
    }
    
    public int getSize()
    {
      int j = ((TypeSystem.Value)this.mObjectAndStatic.getObject()).getCachedSize();
      if (this.mPushAfterEvaluate != null) {}
      for (int i = this.mPushAfterEvaluate.getCachedSize();; i = 0) {
        return i + j;
      }
    }
  }
  
  private static class MacroInfo
  {
    private final Map<ResourceUtil.ExpandedRule, List<String>> mAddMacroNames = new HashMap();
    private final Map<ResourceUtil.ExpandedRule, List<ResourceUtil.ExpandedFunctionCall>> mAddMacros = new HashMap();
    private ResourceUtil.ExpandedFunctionCall mDefault;
    private final Map<ResourceUtil.ExpandedRule, List<String>> mRemoveMacroNames = new HashMap();
    private final Map<ResourceUtil.ExpandedRule, List<ResourceUtil.ExpandedFunctionCall>> mRemoveMacros = new HashMap();
    private final Set<ResourceUtil.ExpandedRule> mRules = new HashSet();
    
    public void addAddMacroForRule(ResourceUtil.ExpandedRule paramExpandedRule, ResourceUtil.ExpandedFunctionCall paramExpandedFunctionCall)
    {
      Object localObject = (List)this.mAddMacros.get(paramExpandedRule);
      if (localObject != null) {}
      for (paramExpandedRule = (ResourceUtil.ExpandedRule)localObject;; paramExpandedRule = (ResourceUtil.ExpandedRule)localObject)
      {
        paramExpandedRule.add(paramExpandedFunctionCall);
        return;
        localObject = new ArrayList();
        this.mAddMacros.put(paramExpandedRule, localObject);
      }
    }
    
    public void addAddMacroRuleNameForRule(ResourceUtil.ExpandedRule paramExpandedRule, String paramString)
    {
      Object localObject = (List)this.mAddMacroNames.get(paramExpandedRule);
      if (localObject != null) {}
      for (paramExpandedRule = (ResourceUtil.ExpandedRule)localObject;; paramExpandedRule = (ResourceUtil.ExpandedRule)localObject)
      {
        paramExpandedRule.add(paramString);
        return;
        localObject = new ArrayList();
        this.mAddMacroNames.put(paramExpandedRule, localObject);
      }
    }
    
    public void addRemoveMacroForRule(ResourceUtil.ExpandedRule paramExpandedRule, ResourceUtil.ExpandedFunctionCall paramExpandedFunctionCall)
    {
      Object localObject = (List)this.mRemoveMacros.get(paramExpandedRule);
      if (localObject != null) {}
      for (paramExpandedRule = (ResourceUtil.ExpandedRule)localObject;; paramExpandedRule = (ResourceUtil.ExpandedRule)localObject)
      {
        paramExpandedRule.add(paramExpandedFunctionCall);
        return;
        localObject = new ArrayList();
        this.mRemoveMacros.put(paramExpandedRule, localObject);
      }
    }
    
    public void addRemoveMacroRuleNameForRule(ResourceUtil.ExpandedRule paramExpandedRule, String paramString)
    {
      Object localObject = (List)this.mRemoveMacroNames.get(paramExpandedRule);
      if (localObject != null) {}
      for (paramExpandedRule = (ResourceUtil.ExpandedRule)localObject;; paramExpandedRule = (ResourceUtil.ExpandedRule)localObject)
      {
        paramExpandedRule.add(paramString);
        return;
        localObject = new ArrayList();
        this.mRemoveMacroNames.put(paramExpandedRule, localObject);
      }
    }
    
    public void addRule(ResourceUtil.ExpandedRule paramExpandedRule)
    {
      this.mRules.add(paramExpandedRule);
    }
    
    public Map<ResourceUtil.ExpandedRule, List<String>> getAddMacroRuleNames()
    {
      return this.mAddMacroNames;
    }
    
    public Map<ResourceUtil.ExpandedRule, List<ResourceUtil.ExpandedFunctionCall>> getAddMacros()
    {
      return this.mAddMacros;
    }
    
    public ResourceUtil.ExpandedFunctionCall getDefault()
    {
      return this.mDefault;
    }
    
    public Map<ResourceUtil.ExpandedRule, List<String>> getRemoveMacroRuleNames()
    {
      return this.mRemoveMacroNames;
    }
    
    public Map<ResourceUtil.ExpandedRule, List<ResourceUtil.ExpandedFunctionCall>> getRemoveMacros()
    {
      return this.mRemoveMacros;
    }
    
    public Set<ResourceUtil.ExpandedRule> getRules()
    {
      return this.mRules;
    }
    
    public void setDefault(ResourceUtil.ExpandedFunctionCall paramExpandedFunctionCall)
    {
      this.mDefault = paramExpandedFunctionCall;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\Runtime.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */