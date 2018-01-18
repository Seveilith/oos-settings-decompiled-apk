package com.google.tagmanager.protobuf.nano;

import java.util.ArrayList;
import java.util.List;

public abstract class ExtendableMessageNano
  extends MessageNano
{
  protected List<UnknownFieldData> unknownFieldData;
  
  public <T> T getExtension(Extension<T> paramExtension)
  {
    return (T)WireFormatNano.getExtension(paramExtension, this.unknownFieldData);
  }
  
  public int getSerializedSize()
  {
    int i = WireFormatNano.computeWireSize(this.unknownFieldData);
    this.cachedSize = i;
    return i;
  }
  
  public <T> void setExtension(Extension<T> paramExtension, T paramT)
  {
    if (this.unknownFieldData != null) {}
    for (;;)
    {
      WireFormatNano.setExtension(paramExtension, paramT, this.unknownFieldData);
      return;
      this.unknownFieldData = new ArrayList();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\protobuf\nano\ExtendableMessageNano.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */