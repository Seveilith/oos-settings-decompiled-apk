package com.google.tagmanager;

import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;

class NoopResolvedPropertyBuilder
  implements ResolvedPropertyBuilder
{
  public ValueBuilder createPropertyValueBuilder(TypeSystem.Value paramValue)
  {
    return new NoopValueBuilder();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\NoopResolvedPropertyBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */