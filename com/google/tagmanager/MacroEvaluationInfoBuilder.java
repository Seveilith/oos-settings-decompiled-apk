package com.google.tagmanager;

abstract interface MacroEvaluationInfoBuilder
{
  public abstract ResolvedFunctionCallBuilder createResult();
  
  public abstract RuleEvaluationStepInfoBuilder createRulesEvaluation();
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\MacroEvaluationInfoBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */