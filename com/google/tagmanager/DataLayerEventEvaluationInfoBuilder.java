package com.google.tagmanager;

abstract interface DataLayerEventEvaluationInfoBuilder
{
  public abstract ResolvedFunctionCallBuilder createAndAddResult();
  
  public abstract RuleEvaluationStepInfoBuilder createRulesEvaluation();
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\DataLayerEventEvaluationInfoBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */