package com.google.tagmanager.proto;

import com.google.analytics.containertag.proto.Serving.Resource;
import com.google.analytics.containertag.proto.Serving.SupplementedResource;
import com.google.tagmanager.protobuf.nano.CodedInputByteBufferNano;
import com.google.tagmanager.protobuf.nano.CodedOutputByteBufferNano;
import com.google.tagmanager.protobuf.nano.ExtendableMessageNano;
import com.google.tagmanager.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.tagmanager.protobuf.nano.MessageNano;
import com.google.tagmanager.protobuf.nano.WireFormatNano;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract interface Resource
{
  public static final class ResourceWithMetadata
    extends ExtendableMessageNano
  {
    public static final ResourceWithMetadata[] EMPTY_ARRAY = new ResourceWithMetadata[0];
    public Serving.Resource resource = null;
    public Serving.SupplementedResource supplementedResource = null;
    public long timeStamp = 0L;
    
    public static ResourceWithMetadata parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new ResourceWithMetadata().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static ResourceWithMetadata parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (ResourceWithMetadata)MessageNano.mergeFrom(new ResourceWithMetadata(), paramArrayOfByte);
    }
    
    public final ResourceWithMetadata clear()
    {
      this.timeStamp = 0L;
      this.resource = null;
      this.supplementedResource = null;
      this.unknownFieldData = null;
      this.cachedSize = -1;
      return this;
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (paramObject != this)
      {
        if (!(paramObject instanceof ResourceWithMetadata)) {
          break label58;
        }
        paramObject = (ResourceWithMetadata)paramObject;
        if (this.timeStamp == ((ResourceWithMetadata)paramObject).timeStamp)
        {
          if (this.resource == null) {
            break label60;
          }
          if (this.resource.equals(((ResourceWithMetadata)paramObject).resource)) {
            break label67;
          }
        }
      }
      for (;;)
      {
        bool = false;
        label54:
        label58:
        label60:
        label67:
        do
        {
          return bool;
          return true;
          return false;
          if (((ResourceWithMetadata)paramObject).resource != null) {
            break;
          }
          if (this.supplementedResource != null) {
            if (!this.supplementedResource.equals(((ResourceWithMetadata)paramObject).supplementedResource)) {
              break;
            }
          }
          for (;;)
          {
            if (this.unknownFieldData != null)
            {
              if (this.unknownFieldData.equals(((ResourceWithMetadata)paramObject).unknownFieldData)) {
                break label54;
              }
              break;
              if (((ResourceWithMetadata)paramObject).supplementedResource != null) {
                break;
              }
            }
          }
        } while (((ResourceWithMetadata)paramObject).unknownFieldData == null);
      }
    }
    
    public int getSerializedSize()
    {
      int i = CodedOutputByteBufferNano.computeInt64Size(1, this.timeStamp) + 0;
      if (this.resource == null) {
        if (this.supplementedResource != null) {
          break label56;
        }
      }
      for (;;)
      {
        i += WireFormatNano.computeWireSize(this.unknownFieldData);
        this.cachedSize = i;
        return i;
        i += CodedOutputByteBufferNano.computeMessageSize(2, this.resource);
        break;
        label56:
        i += CodedOutputByteBufferNano.computeMessageSize(3, this.supplementedResource);
      }
    }
    
    public int hashCode()
    {
      int k = 0;
      int m = (int)(this.timeStamp ^ this.timeStamp >>> 32);
      int i;
      if (this.resource != null)
      {
        i = this.resource.hashCode();
        if (this.supplementedResource == null) {
          break label91;
        }
      }
      label91:
      for (int j = this.supplementedResource.hashCode();; j = 0)
      {
        if (this.unknownFieldData != null) {
          k = this.unknownFieldData.hashCode();
        }
        return (j + (i + (m + 527) * 31) * 31) * 31 + k;
        i = 0;
        break;
      }
    }
    
    public ResourceWithMetadata mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      for (;;)
      {
        int i = paramCodedInputByteBufferNano.readTag();
        switch (i)
        {
        default: 
          if (this.unknownFieldData == null) {}
        case 0: 
          while (!WireFormatNano.storeUnknownField(this.unknownFieldData, paramCodedInputByteBufferNano, i))
          {
            return this;
            return this;
            this.unknownFieldData = new ArrayList();
          }
        case 8: 
          this.timeStamp = paramCodedInputByteBufferNano.readInt64();
          break;
        case 18: 
          this.resource = new Serving.Resource();
          paramCodedInputByteBufferNano.readMessage(this.resource);
          break;
        case 26: 
          this.supplementedResource = new Serving.SupplementedResource();
          paramCodedInputByteBufferNano.readMessage(this.supplementedResource);
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      paramCodedOutputByteBufferNano.writeInt64(1, this.timeStamp);
      if (this.resource == null) {
        if (this.supplementedResource != null) {
          break label44;
        }
      }
      for (;;)
      {
        WireFormatNano.writeUnknownFields(this.unknownFieldData, paramCodedOutputByteBufferNano);
        return;
        paramCodedOutputByteBufferNano.writeMessage(2, this.resource);
        break;
        label44:
        paramCodedOutputByteBufferNano.writeMessage(3, this.supplementedResource);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\proto\Resource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */