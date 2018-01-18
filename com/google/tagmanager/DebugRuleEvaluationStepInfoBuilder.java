package com.google.tagmanager;

import com.google.analytics.containertag.proto.Debug.ResolvedRule;
import com.google.analytics.containertag.proto.Debug.RuleEvaluationStepInfo;
import java.util.Iterator;
import java.util.Set;

class DebugRuleEvaluationStepInfoBuilder
  implements RuleEvaluationStepInfoBuilder
{
  private Debug.RuleEvaluationStepInfo ruleEvaluationStepInfo;
  
  public DebugRuleEvaluationStepInfoBuilder(Debug.RuleEvaluationStepInfo paramRuleEvaluationStepInfo)
  {
    this.ruleEvaluationStepInfo = paramRuleEvaluationStepInfo;
  }
  
  public ResolvedRuleBuilder createResolvedRuleBuilder()
  {
    Debug.ResolvedRule localResolvedRule = new Debug.ResolvedRule();
    this.ruleEvaluationStepInfo.rules = ArrayUtils.appendToArray(this.ruleEvaluationStepInfo.rules, localResolvedRule);
    return new DebugResolvedRuleBuilder(localResolvedRule);
  }
  
  public void setEnabledFunctions(Set<ResourceUtil.ExpandedFunctionCall> paramSet)
  {
    paramSet = paramSet.iterator();
    for (;;)
    {
      if (!paramSet.hasNext()) {
        return;
      }
      ResourceUtil.ExpandedFunctionCall localExpandedFunctionCall = (ResourceUtil.ExpandedFunctionCall)paramSet.next();
      this.ruleEvaluationStepInfo.enabledFunctions = ArrayUtils.appendToArray(this.ruleEvaluationStepInfo.enabledFunctions, DebugResolvedRuleBuilder.translateExpandedFunctionCall(localExpandedFunctionCall));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\DebugRuleEvaluationStepInfoBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */