package com.google.tagmanager;

class NoopDataLayerEventEvaluationInfoBuilder
  implements DataLayerEventEvaluationInfoBuilder
{
  public ResolvedFunctionCallBuilder createAndAddResult()
  {
    return new NoopResolvedFunctionCallBuilder();
  }
  
  public RuleEvaluationStepInfoBuilder createRulesEvaluation()
  {
    return new NoopRuleEvaluationStepInfoBuilder();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\NoopDataLayerEventEvaluationInfoBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */