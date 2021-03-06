package com.google.tagmanager;

import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;

abstract interface ResolvedRuleBuilder
{
  public abstract ResolvedFunctionCallBuilder createNegativePredicate();
  
  public abstract ResolvedFunctionCallBuilder createPositivePredicate();
  
  public abstract ResolvedFunctionCallTranslatorList getAddedMacroFunctions();
  
  public abstract ResolvedFunctionCallTranslatorList getAddedTagFunctions();
  
  public abstract ResolvedFunctionCallTranslatorList getRemovedMacroFunctions();
  
  public abstract ResolvedFunctionCallTranslatorList getRemovedTagFunctions();
  
  public abstract void setValue(TypeSystem.Value paramValue);
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\ResolvedRuleBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */