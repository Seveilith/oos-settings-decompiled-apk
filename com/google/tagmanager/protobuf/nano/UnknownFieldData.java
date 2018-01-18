package com.google.tagmanager.protobuf.nano;

import java.util.Arrays;

public final class UnknownFieldData
{
  final byte[] bytes;
  final int tag;
  
  UnknownFieldData(int paramInt, byte[] paramArrayOfByte)
  {
    this.tag = paramInt;
    this.bytes = paramArrayOfByte;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (paramObject != this)
    {
      if (!(paramObject instanceof UnknownFieldData)) {
        break label36;
      }
      paramObject = (UnknownFieldData)paramObject;
      if (this.tag == ((UnknownFieldData)paramObject).tag) {
        break label38;
      }
    }
    for (;;)
    {
      bool = false;
      label36:
      label38:
      do
      {
        return bool;
        return true;
        return false;
      } while (Arrays.equals(this.bytes, ((UnknownFieldData)paramObject).bytes));
    }
  }
  
  public int hashCode()
  {
    int j = this.tag + 527;
    int i = 0;
    for (;;)
    {
      if (i >= this.bytes.length) {
        return j;
      }
      j = j * 31 + this.bytes[i];
      i += 1;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\protobuf\nano\UnknownFieldData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */