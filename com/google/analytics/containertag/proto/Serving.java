package com.google.analytics.containertag.proto;

import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import com.google.tagmanager.protobuf.nano.CodedInputByteBufferNano;
import com.google.tagmanager.protobuf.nano.CodedOutputByteBufferNano;
import com.google.tagmanager.protobuf.nano.ExtendableMessageNano;
import com.google.tagmanager.protobuf.nano.Extension;
import com.google.tagmanager.protobuf.nano.Extension.TypeLiteral;
import com.google.tagmanager.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.tagmanager.protobuf.nano.MessageNano;
import com.google.tagmanager.protobuf.nano.WireFormatNano;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract interface Serving
{
  public static final class CacheOption
    extends ExtendableMessageNano
  {
    public static final CacheOption[] EMPTY_ARRAY = new CacheOption[0];
    public int expirationSeconds = 0;
    public int gcacheExpirationSeconds = 0;
    public int level = 1;
    
    public static CacheOption parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new CacheOption().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static CacheOption parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (CacheOption)MessageNano.mergeFrom(new CacheOption(), paramArrayOfByte);
    }
    
    public final CacheOption clear()
    {
      this.level = 1;
      this.expirationSeconds = 0;
      this.gcacheExpirationSeconds = 0;
      this.unknownFieldData = null;
      this.cachedSize = -1;
      return this;
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (paramObject != this)
      {
        if (!(paramObject instanceof CacheOption)) {
          break label36;
        }
        paramObject = (CacheOption)paramObject;
        if (this.level == ((CacheOption)paramObject).level) {
          break label38;
        }
      }
      for (;;)
      {
        bool = false;
        label36:
        label38:
        label86:
        do
        {
          do
          {
            return bool;
            return true;
            return false;
            if ((this.expirationSeconds != ((CacheOption)paramObject).expirationSeconds) || (this.gcacheExpirationSeconds != ((CacheOption)paramObject).gcacheExpirationSeconds)) {
              break;
            }
            if (this.unknownFieldData == null) {
              break label86;
            }
          } while (this.unknownFieldData.equals(((CacheOption)paramObject).unknownFieldData));
          break;
        } while (((CacheOption)paramObject).unknownFieldData == null);
      }
    }
    
    public int getSerializedSize()
    {
      int i = 0;
      if (this.level == 1)
      {
        if (this.expirationSeconds != 0) {
          break label55;
        }
        label17:
        if (this.gcacheExpirationSeconds != 0) {
          break label69;
        }
      }
      for (;;)
      {
        i += WireFormatNano.computeWireSize(this.unknownFieldData);
        this.cachedSize = i;
        return i;
        i = CodedOutputByteBufferNano.computeInt32Size(1, this.level) + 0;
        break;
        label55:
        i += CodedOutputByteBufferNano.computeInt32Size(2, this.expirationSeconds);
        break label17;
        label69:
        i += CodedOutputByteBufferNano.computeInt32Size(3, this.gcacheExpirationSeconds);
      }
    }
    
    public int hashCode()
    {
      int j = this.level;
      int k = this.expirationSeconds;
      int m = this.gcacheExpirationSeconds;
      if (this.unknownFieldData != null) {}
      for (int i = this.unknownFieldData.hashCode();; i = 0) {
        return i + (((j + 527) * 31 + k) * 31 + m) * 31;
      }
    }
    
    public CacheOption mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
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
          i = paramCodedInputByteBufferNano.readInt32();
          if (i == 1) {}
          while ((i == 2) || (i == 3))
          {
            this.level = i;
            break;
          }
          this.level = 1;
          break;
        case 16: 
          this.expirationSeconds = paramCodedInputByteBufferNano.readInt32();
          break;
        case 24: 
          this.gcacheExpirationSeconds = paramCodedInputByteBufferNano.readInt32();
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if (this.level == 1)
      {
        if (this.expirationSeconds != 0) {
          break label43;
        }
        label15:
        if (this.gcacheExpirationSeconds != 0) {
          break label55;
        }
      }
      for (;;)
      {
        WireFormatNano.writeUnknownFields(this.unknownFieldData, paramCodedOutputByteBufferNano);
        return;
        paramCodedOutputByteBufferNano.writeInt32(1, this.level);
        break;
        label43:
        paramCodedOutputByteBufferNano.writeInt32(2, this.expirationSeconds);
        break label15;
        label55:
        paramCodedOutputByteBufferNano.writeInt32(3, this.gcacheExpirationSeconds);
      }
    }
    
    public static abstract interface CacheLevel
    {
      public static final int NO_CACHE = 1;
      public static final int PRIVATE = 2;
      public static final int PUBLIC = 3;
    }
  }
  
  public static final class Container
    extends ExtendableMessageNano
  {
    public static final Container[] EMPTY_ARRAY = new Container[0];
    public String containerId = "";
    public Serving.Resource jsResource = null;
    public int state = 1;
    public String version = "";
    
    public static Container parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new Container().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static Container parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (Container)MessageNano.mergeFrom(new Container(), paramArrayOfByte);
    }
    
    public final Container clear()
    {
      this.jsResource = null;
      this.containerId = "";
      this.state = 1;
      this.version = "";
      this.unknownFieldData = null;
      this.cachedSize = -1;
      return this;
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (paramObject != this)
      {
        if (!(paramObject instanceof Container)) {
          break label46;
        }
        paramObject = (Container)paramObject;
        if (this.jsResource == null) {
          break label48;
        }
        if (this.jsResource.equals(((Container)paramObject).jsResource)) {
          break label55;
        }
        break label76;
      }
      for (;;)
      {
        label40:
        bool = false;
        label42:
        label46:
        label48:
        label55:
        label76:
        label144:
        do
        {
          return bool;
          return true;
          return false;
          if (((Container)paramObject).jsResource != null) {
            break label40;
          }
          if (this.containerId != null)
          {
            if (!this.containerId.equals(((Container)paramObject).containerId)) {
              break label40;
            }
            if (this.state != ((Container)paramObject).state) {
              break label40;
            }
            if (this.version == null) {
              break label144;
            }
            if (!this.version.equals(((Container)paramObject).version)) {
              break label40;
            }
          }
          for (;;)
          {
            if (this.unknownFieldData != null)
            {
              if (this.unknownFieldData.equals(((Container)paramObject).unknownFieldData)) {
                break label42;
              }
              break label40;
              if (((Container)paramObject).containerId == null) {
                break;
              }
              break label40;
              if (((Container)paramObject).version != null) {
                break label40;
              }
            }
          }
        } while (((Container)paramObject).unknownFieldData == null);
      }
    }
    
    public int getSerializedSize()
    {
      int i = 0;
      if (this.jsResource == null)
      {
        i = i + CodedOutputByteBufferNano.computeStringSize(3, this.containerId) + CodedOutputByteBufferNano.computeInt32Size(4, this.state);
        if (!this.version.equals("")) {
          break label72;
        }
      }
      for (;;)
      {
        i += WireFormatNano.computeWireSize(this.unknownFieldData);
        this.cachedSize = i;
        return i;
        i = CodedOutputByteBufferNano.computeMessageSize(1, this.jsResource) + 0;
        break;
        label72:
        i += CodedOutputByteBufferNano.computeStringSize(5, this.version);
      }
    }
    
    public int hashCode()
    {
      int m = 0;
      int i;
      int j;
      label33:
      int n;
      if (this.jsResource != null)
      {
        i = this.jsResource.hashCode();
        if (this.containerId == null) {
          break label105;
        }
        j = this.containerId.hashCode();
        n = this.state;
        if (this.version == null) {
          break label110;
        }
      }
      label105:
      label110:
      for (int k = this.version.hashCode();; k = 0)
      {
        if (this.unknownFieldData != null) {
          m = this.unknownFieldData.hashCode();
        }
        return (k + ((j + (i + 527) * 31) * 31 + n) * 31) * 31 + m;
        i = 0;
        break;
        j = 0;
        break label33;
      }
    }
    
    public Container mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
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
        case 10: 
          this.jsResource = new Serving.Resource();
          paramCodedInputByteBufferNano.readMessage(this.jsResource);
          break;
        case 26: 
          this.containerId = paramCodedInputByteBufferNano.readString();
          break;
        case 32: 
          i = paramCodedInputByteBufferNano.readInt32();
          if (i == 1) {}
          while (i == 2)
          {
            this.state = i;
            break;
          }
          this.state = 1;
          break;
        case 42: 
          this.version = paramCodedInputByteBufferNano.readString();
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if (this.jsResource == null)
      {
        paramCodedOutputByteBufferNano.writeString(3, this.containerId);
        paramCodedOutputByteBufferNano.writeInt32(4, this.state);
        if (!this.version.equals("")) {
          break label58;
        }
      }
      for (;;)
      {
        WireFormatNano.writeUnknownFields(this.unknownFieldData, paramCodedOutputByteBufferNano);
        return;
        paramCodedOutputByteBufferNano.writeMessage(1, this.jsResource);
        break;
        label58:
        paramCodedOutputByteBufferNano.writeString(5, this.version);
      }
    }
  }
  
  public static final class FunctionCall
    extends ExtendableMessageNano
  {
    public static final FunctionCall[] EMPTY_ARRAY = new FunctionCall[0];
    public int function = 0;
    public boolean liveOnly = false;
    public int name = 0;
    public int[] property = WireFormatNano.EMPTY_INT_ARRAY;
    public boolean serverSide = false;
    
    public static FunctionCall parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new FunctionCall().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static FunctionCall parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (FunctionCall)MessageNano.mergeFrom(new FunctionCall(), paramArrayOfByte);
    }
    
    public final FunctionCall clear()
    {
      this.property = WireFormatNano.EMPTY_INT_ARRAY;
      this.function = 0;
      this.name = 0;
      this.liveOnly = false;
      this.serverSide = false;
      this.unknownFieldData = null;
      this.cachedSize = -1;
      return this;
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (paramObject != this)
      {
        if (!(paramObject instanceof FunctionCall)) {
          break label39;
        }
        paramObject = (FunctionCall)paramObject;
        if (Arrays.equals(this.property, ((FunctionCall)paramObject).property)) {
          break label41;
        }
      }
      for (;;)
      {
        bool = false;
        label39:
        label41:
        label111:
        do
        {
          do
          {
            return bool;
            return true;
            return false;
            if ((this.function != ((FunctionCall)paramObject).function) || (this.name != ((FunctionCall)paramObject).name) || (this.liveOnly != ((FunctionCall)paramObject).liveOnly) || (this.serverSide != ((FunctionCall)paramObject).serverSide)) {
              break;
            }
            if (this.unknownFieldData == null) {
              break label111;
            }
          } while (this.unknownFieldData.equals(((FunctionCall)paramObject).unknownFieldData));
          break;
        } while (((FunctionCall)paramObject).unknownFieldData == null);
      }
    }
    
    public int getSerializedSize()
    {
      int k = 0;
      int i;
      int m;
      if (!this.serverSide)
      {
        i = 0;
        m = i + CodedOutputByteBufferNano.computeInt32Size(2, this.function);
        if (this.property != null) {
          break label78;
        }
        i = m;
        label33:
        if (this.name != 0) {
          break label143;
        }
        label40:
        if (this.liveOnly) {
          break label157;
        }
      }
      for (;;)
      {
        i += WireFormatNano.computeWireSize(this.unknownFieldData);
        this.cachedSize = i;
        return i;
        i = CodedOutputByteBufferNano.computeBoolSize(1, this.serverSide) + 0;
        break;
        label78:
        i = m;
        if (this.property.length <= 0) {
          break label33;
        }
        int[] arrayOfInt = this.property;
        int n = arrayOfInt.length;
        int j = 0;
        i = k;
        for (;;)
        {
          if (i >= n)
          {
            i = m + j + this.property.length * 1;
            break;
          }
          j += CodedOutputByteBufferNano.computeInt32SizeNoTag(arrayOfInt[i]);
          i += 1;
        }
        label143:
        i += CodedOutputByteBufferNano.computeInt32Size(4, this.name);
        break label40;
        label157:
        i += CodedOutputByteBufferNano.computeBoolSize(6, this.liveOnly);
      }
    }
    
    public int hashCode()
    {
      int k = 2;
      int m = 0;
      int i;
      int j;
      label26:
      int n;
      int i1;
      if (this.property != null)
      {
        i = 17;
        j = 0;
        if (j < this.property.length) {
          break label109;
        }
        n = this.function;
        i1 = this.name;
        if (this.liveOnly) {
          break label128;
        }
        j = 2;
        label47:
        if (this.serverSide) {
          break label133;
        }
      }
      for (;;)
      {
        if (this.unknownFieldData != null) {
          m = this.unknownFieldData.hashCode();
        }
        return ((j + ((i * 31 + n) * 31 + i1) * 31) * 31 + k) * 31 + m;
        i = 527;
        break label26;
        label109:
        i = i * 31 + this.property[j];
        j += 1;
        break;
        label128:
        j = 1;
        break label47;
        label133:
        k = 1;
      }
    }
    
    public FunctionCall mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
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
          this.serverSide = paramCodedInputByteBufferNano.readBool();
          break;
        case 16: 
          this.function = paramCodedInputByteBufferNano.readInt32();
          break;
        case 24: 
          int j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 24);
          i = this.property.length;
          int[] arrayOfInt = new int[j + i];
          System.arraycopy(this.property, 0, arrayOfInt, 0, i);
          this.property = arrayOfInt;
          for (;;)
          {
            if (i >= this.property.length - 1)
            {
              this.property[i] = paramCodedInputByteBufferNano.readInt32();
              break;
            }
            this.property[i] = paramCodedInputByteBufferNano.readInt32();
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 32: 
          this.name = paramCodedInputByteBufferNano.readInt32();
          break;
        case 48: 
          this.liveOnly = paramCodedInputByteBufferNano.readBool();
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      int i = 0;
      if (!this.serverSide)
      {
        paramCodedOutputByteBufferNano.writeInt32(2, this.function);
        if (this.property != null) {
          break label60;
        }
        label25:
        if (this.name != 0) {
          break label91;
        }
        label32:
        if (this.liveOnly) {
          break label103;
        }
      }
      for (;;)
      {
        WireFormatNano.writeUnknownFields(this.unknownFieldData, paramCodedOutputByteBufferNano);
        return;
        paramCodedOutputByteBufferNano.writeBool(1, this.serverSide);
        break;
        label60:
        int[] arrayOfInt = this.property;
        int j = arrayOfInt.length;
        while (i < j)
        {
          paramCodedOutputByteBufferNano.writeInt32(3, arrayOfInt[i]);
          i += 1;
        }
        break label25;
        label91:
        paramCodedOutputByteBufferNano.writeInt32(4, this.name);
        break label32;
        label103:
        paramCodedOutputByteBufferNano.writeBool(6, this.liveOnly);
      }
    }
  }
  
  public static final class GaExperimentRandom
    extends ExtendableMessageNano
  {
    public static final GaExperimentRandom[] EMPTY_ARRAY = new GaExperimentRandom[0];
    public String key = "";
    public long lifetimeInMilliseconds = 0L;
    public long maxRandom = 2147483647L;
    public long minRandom = 0L;
    public boolean retainOriginalValue = false;
    
    public static GaExperimentRandom parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new GaExperimentRandom().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static GaExperimentRandom parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (GaExperimentRandom)MessageNano.mergeFrom(new GaExperimentRandom(), paramArrayOfByte);
    }
    
    public final GaExperimentRandom clear()
    {
      this.key = "";
      this.minRandom = 0L;
      this.maxRandom = 2147483647L;
      this.retainOriginalValue = false;
      this.lifetimeInMilliseconds = 0L;
      this.unknownFieldData = null;
      this.cachedSize = -1;
      return this;
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (paramObject != this)
      {
        if (!(paramObject instanceof GaExperimentRandom)) {
          break label46;
        }
        paramObject = (GaExperimentRandom)paramObject;
        if (this.key == null) {
          break label48;
        }
        if (this.key.equals(((GaExperimentRandom)paramObject).key)) {
          break label55;
        }
      }
      for (;;)
      {
        bool = false;
        label46:
        label48:
        label55:
        label128:
        do
        {
          do
          {
            return bool;
            return true;
            return false;
            if (((GaExperimentRandom)paramObject).key != null) {
              break;
            }
            if ((this.minRandom != ((GaExperimentRandom)paramObject).minRandom) || (this.maxRandom != ((GaExperimentRandom)paramObject).maxRandom) || (this.retainOriginalValue != ((GaExperimentRandom)paramObject).retainOriginalValue) || (this.lifetimeInMilliseconds != ((GaExperimentRandom)paramObject).lifetimeInMilliseconds)) {
              break;
            }
            if (this.unknownFieldData == null) {
              break label128;
            }
          } while (this.unknownFieldData.equals(((GaExperimentRandom)paramObject).unknownFieldData));
          break;
        } while (((GaExperimentRandom)paramObject).unknownFieldData == null);
      }
    }
    
    public int getSerializedSize()
    {
      int j = 0;
      int i;
      if (this.key.equals(""))
      {
        i = j;
        if (this.minRandom != 0L) {
          i = j + CodedOutputByteBufferNano.computeInt64Size(2, this.minRandom);
        }
        j = i;
        if (this.maxRandom != 2147483647L) {
          j = i + CodedOutputByteBufferNano.computeInt64Size(3, this.maxRandom);
        }
        if (this.retainOriginalValue) {
          break label120;
        }
      }
      for (;;)
      {
        i = j;
        if (this.lifetimeInMilliseconds != 0L) {
          i = j + CodedOutputByteBufferNano.computeInt64Size(5, this.lifetimeInMilliseconds);
        }
        i += WireFormatNano.computeWireSize(this.unknownFieldData);
        this.cachedSize = i;
        return i;
        j = CodedOutputByteBufferNano.computeStringSize(1, this.key) + 0;
        break;
        label120:
        j += CodedOutputByteBufferNano.computeBoolSize(4, this.retainOriginalValue);
      }
    }
    
    public int hashCode()
    {
      int k = 0;
      int i;
      int m;
      int n;
      if (this.key != null)
      {
        i = this.key.hashCode();
        m = (int)(this.minRandom ^ this.minRandom >>> 32);
        n = (int)(this.maxRandom ^ this.maxRandom >>> 32);
        if (this.retainOriginalValue) {
          break label127;
        }
      }
      label127:
      for (int j = 2;; j = 1)
      {
        int i1 = (int)(this.lifetimeInMilliseconds ^ this.lifetimeInMilliseconds >>> 32);
        if (this.unknownFieldData != null) {
          k = this.unknownFieldData.hashCode();
        }
        return ((j + (((i + 527) * 31 + m) * 31 + n) * 31) * 31 + i1) * 31 + k;
        i = 0;
        break;
      }
    }
    
    public GaExperimentRandom mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
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
        case 10: 
          this.key = paramCodedInputByteBufferNano.readString();
          break;
        case 16: 
          this.minRandom = paramCodedInputByteBufferNano.readInt64();
          break;
        case 24: 
          this.maxRandom = paramCodedInputByteBufferNano.readInt64();
          break;
        case 32: 
          this.retainOriginalValue = paramCodedInputByteBufferNano.readBool();
          break;
        case 40: 
          this.lifetimeInMilliseconds = paramCodedInputByteBufferNano.readInt64();
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if (this.key.equals(""))
      {
        if (this.minRandom != 0L) {
          paramCodedOutputByteBufferNano.writeInt64(2, this.minRandom);
        }
        if (this.maxRandom != 2147483647L) {
          paramCodedOutputByteBufferNano.writeInt64(3, this.maxRandom);
        }
        if (this.retainOriginalValue) {
          break label96;
        }
      }
      for (;;)
      {
        if (this.lifetimeInMilliseconds != 0L) {
          paramCodedOutputByteBufferNano.writeInt64(5, this.lifetimeInMilliseconds);
        }
        WireFormatNano.writeUnknownFields(this.unknownFieldData, paramCodedOutputByteBufferNano);
        return;
        paramCodedOutputByteBufferNano.writeString(1, this.key);
        break;
        label96:
        paramCodedOutputByteBufferNano.writeBool(4, this.retainOriginalValue);
      }
    }
  }
  
  public static final class GaExperimentSupplemental
    extends ExtendableMessageNano
  {
    public static final GaExperimentSupplemental[] EMPTY_ARRAY = new GaExperimentSupplemental[0];
    public Serving.GaExperimentRandom[] experimentRandom = Serving.GaExperimentRandom.EMPTY_ARRAY;
    public TypeSystem.Value[] valueToClear = TypeSystem.Value.EMPTY_ARRAY;
    public TypeSystem.Value[] valueToPush = TypeSystem.Value.EMPTY_ARRAY;
    
    public static GaExperimentSupplemental parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new GaExperimentSupplemental().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static GaExperimentSupplemental parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (GaExperimentSupplemental)MessageNano.mergeFrom(new GaExperimentSupplemental(), paramArrayOfByte);
    }
    
    public final GaExperimentSupplemental clear()
    {
      this.valueToPush = TypeSystem.Value.EMPTY_ARRAY;
      this.valueToClear = TypeSystem.Value.EMPTY_ARRAY;
      this.experimentRandom = Serving.GaExperimentRandom.EMPTY_ARRAY;
      this.unknownFieldData = null;
      this.cachedSize = -1;
      return this;
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (paramObject != this)
      {
        if (!(paramObject instanceof GaExperimentSupplemental)) {
          break label39;
        }
        paramObject = (GaExperimentSupplemental)paramObject;
        if (Arrays.equals(this.valueToPush, ((GaExperimentSupplemental)paramObject).valueToPush)) {
          break label41;
        }
      }
      for (;;)
      {
        bool = false;
        label39:
        label41:
        label95:
        do
        {
          do
          {
            return bool;
            return true;
            return false;
            if ((!Arrays.equals(this.valueToClear, ((GaExperimentSupplemental)paramObject).valueToClear)) || (!Arrays.equals(this.experimentRandom, ((GaExperimentSupplemental)paramObject).experimentRandom))) {
              break;
            }
            if (this.unknownFieldData == null) {
              break label95;
            }
          } while (this.unknownFieldData.equals(((GaExperimentSupplemental)paramObject).unknownFieldData));
          break;
        } while (((GaExperimentSupplemental)paramObject).unknownFieldData == null);
      }
    }
    
    public int getSerializedSize()
    {
      int m = 0;
      int i;
      if (this.valueToPush == null)
      {
        i = 0;
        if (this.valueToClear == null)
        {
          if (this.experimentRandom != null) {
            break label131;
          }
          k = i;
          i = k + WireFormatNano.computeWireSize(this.unknownFieldData);
          this.cachedSize = i;
          return i;
        }
      }
      else
      {
        localObject = this.valueToPush;
        n = localObject.length;
        k = 0;
        for (j = 0;; j = i + j)
        {
          i = j;
          if (k >= n) {
            break;
          }
          i = CodedOutputByteBufferNano.computeMessageSize(1, localObject[k]);
          k += 1;
        }
      }
      Object localObject = this.valueToClear;
      int n = localObject.length;
      int k = 0;
      for (int j = i;; j = i + j)
      {
        i = j;
        if (k >= n) {
          break;
        }
        i = CodedOutputByteBufferNano.computeMessageSize(2, localObject[k]);
        k += 1;
      }
      label131:
      localObject = this.experimentRandom;
      n = localObject.length;
      j = m;
      for (;;)
      {
        k = i;
        if (j >= n) {
          break;
        }
        i += CodedOutputByteBufferNano.computeMessageSize(3, localObject[j]);
        j += 1;
      }
    }
    
    public int hashCode()
    {
      int m = 0;
      int i;
      int j;
      if (this.valueToPush != null)
      {
        i = 17;
        j = 0;
        if (j < this.valueToPush.length) {
          break label94;
        }
        label24:
        if (this.valueToClear == null) {
          break label132;
        }
        j = 0;
        if (j < this.valueToClear.length) {
          break label140;
        }
        label42:
        if (this.experimentRandom == null) {
          break label178;
        }
        j = 0;
        if (j < this.experimentRandom.length) {
          break label186;
        }
      }
      for (;;)
      {
        j = m;
        if (this.unknownFieldData != null) {
          j = this.unknownFieldData.hashCode();
        }
        return i * 31 + j;
        i = 527;
        break label24;
        label94:
        if (this.valueToPush[j] != null) {}
        for (k = this.valueToPush[j].hashCode();; k = 0)
        {
          i = k + i * 31;
          j += 1;
          break;
        }
        label132:
        i *= 31;
        break label42;
        label140:
        if (this.valueToClear[j] != null) {}
        for (k = this.valueToClear[j].hashCode();; k = 0)
        {
          i = k + i * 31;
          j += 1;
          break;
        }
        label178:
        i *= 31;
      }
      label186:
      if (this.experimentRandom[j] != null) {}
      for (int k = this.experimentRandom[j].hashCode();; k = 0)
      {
        i = k + i * 31;
        j += 1;
        break;
      }
    }
    
    public GaExperimentSupplemental mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      int i = paramCodedInputByteBufferNano.readTag();
      label105:
      Object localObject;
      switch (i)
      {
      default: 
        if (this.unknownFieldData == null) {
          break;
        }
      case 0: 
        while (!WireFormatNano.storeUnknownField(this.unknownFieldData, paramCodedInputByteBufferNano, i))
        {
          return this;
          return this;
          this.unknownFieldData = new ArrayList();
        }
      case 10: 
        j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 10);
        if (this.valueToPush != null)
        {
          i = this.valueToPush.length;
          localObject = new TypeSystem.Value[j + i];
          if (this.valueToPush != null) {
            break label168;
          }
          this.valueToPush = ((TypeSystem.Value[])localObject);
        }
        for (;;)
        {
          if (i >= this.valueToPush.length - 1)
          {
            this.valueToPush[i] = new TypeSystem.Value();
            paramCodedInputByteBufferNano.readMessage(this.valueToPush[i]);
            break;
            i = 0;
            break label105;
            System.arraycopy(this.valueToPush, 0, localObject, 0, i);
            break label120;
          }
          this.valueToPush[i] = new TypeSystem.Value();
          paramCodedInputByteBufferNano.readMessage(this.valueToPush[i]);
          paramCodedInputByteBufferNano.readTag();
          i += 1;
        }
      case 18: 
        label120:
        label168:
        j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 18);
        if (this.valueToClear != null)
        {
          i = this.valueToClear.length;
          label238:
          localObject = new TypeSystem.Value[j + i];
          if (this.valueToClear != null) {
            break label301;
          }
          label253:
          this.valueToClear = ((TypeSystem.Value[])localObject);
        }
        for (;;)
        {
          if (i >= this.valueToClear.length - 1)
          {
            this.valueToClear[i] = new TypeSystem.Value();
            paramCodedInputByteBufferNano.readMessage(this.valueToClear[i]);
            break;
            i = 0;
            break label238;
            label301:
            System.arraycopy(this.valueToClear, 0, localObject, 0, i);
            break label253;
          }
          this.valueToClear[i] = new TypeSystem.Value();
          paramCodedInputByteBufferNano.readMessage(this.valueToClear[i]);
          paramCodedInputByteBufferNano.readTag();
          i += 1;
        }
      }
      int j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 26);
      if (this.experimentRandom != null)
      {
        i = this.experimentRandom.length;
        label371:
        localObject = new Serving.GaExperimentRandom[j + i];
        if (this.experimentRandom != null) {
          break label434;
        }
        label386:
        this.experimentRandom = ((Serving.GaExperimentRandom[])localObject);
      }
      for (;;)
      {
        if (i >= this.experimentRandom.length - 1)
        {
          this.experimentRandom[i] = new Serving.GaExperimentRandom();
          paramCodedInputByteBufferNano.readMessage(this.experimentRandom[i]);
          break;
          i = 0;
          break label371;
          label434:
          System.arraycopy(this.experimentRandom, 0, localObject, 0, i);
          break label386;
        }
        this.experimentRandom[i] = new Serving.GaExperimentRandom();
        paramCodedInputByteBufferNano.readMessage(this.experimentRandom[i]);
        paramCodedInputByteBufferNano.readTag();
        i += 1;
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      int j = 0;
      if (this.valueToPush == null)
      {
        if (this.valueToClear != null) {
          break label67;
        }
        label16:
        if (this.experimentRandom != null) {
          break label102;
        }
      }
      for (;;)
      {
        WireFormatNano.writeUnknownFields(this.unknownFieldData, paramCodedOutputByteBufferNano);
        return;
        Object localObject = this.valueToPush;
        int k = localObject.length;
        int i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeMessage(1, localObject[i]);
          i += 1;
        }
        break;
        label67:
        localObject = this.valueToClear;
        k = localObject.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeMessage(2, localObject[i]);
          i += 1;
        }
        break label16;
        label102:
        localObject = this.experimentRandom;
        k = localObject.length;
        i = j;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeMessage(3, localObject[i]);
          i += 1;
        }
      }
    }
  }
  
  public static final class Property
    extends ExtendableMessageNano
  {
    public static final Property[] EMPTY_ARRAY = new Property[0];
    public int key = 0;
    public int value = 0;
    
    public static Property parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new Property().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static Property parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (Property)MessageNano.mergeFrom(new Property(), paramArrayOfByte);
    }
    
    public final Property clear()
    {
      this.key = 0;
      this.value = 0;
      this.unknownFieldData = null;
      this.cachedSize = -1;
      return this;
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (paramObject != this)
      {
        if (!(paramObject instanceof Property)) {
          break label36;
        }
        paramObject = (Property)paramObject;
        if (this.key == ((Property)paramObject).key) {
          break label38;
        }
      }
      for (;;)
      {
        bool = false;
        label36:
        label38:
        label75:
        do
        {
          do
          {
            return bool;
            return true;
            return false;
            if (this.value != ((Property)paramObject).value) {
              break;
            }
            if (this.unknownFieldData == null) {
              break label75;
            }
          } while (this.unknownFieldData.equals(((Property)paramObject).unknownFieldData));
          break;
        } while (((Property)paramObject).unknownFieldData == null);
      }
    }
    
    public int getSerializedSize()
    {
      int i = CodedOutputByteBufferNano.computeInt32Size(1, this.key) + 0 + CodedOutputByteBufferNano.computeInt32Size(2, this.value) + WireFormatNano.computeWireSize(this.unknownFieldData);
      this.cachedSize = i;
      return i;
    }
    
    public int hashCode()
    {
      int j = this.key;
      int k = this.value;
      if (this.unknownFieldData != null) {}
      for (int i = this.unknownFieldData.hashCode();; i = 0) {
        return i + ((j + 527) * 31 + k) * 31;
      }
    }
    
    public Property mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
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
          this.key = paramCodedInputByteBufferNano.readInt32();
          break;
        case 16: 
          this.value = paramCodedInputByteBufferNano.readInt32();
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      paramCodedOutputByteBufferNano.writeInt32(1, this.key);
      paramCodedOutputByteBufferNano.writeInt32(2, this.value);
      WireFormatNano.writeUnknownFields(this.unknownFieldData, paramCodedOutputByteBufferNano);
    }
  }
  
  public static final class Resource
    extends ExtendableMessageNano
  {
    public static final Resource[] EMPTY_ARRAY = new Resource[0];
    private static final String TEMPLATE_VERSION_SET_DEFAULT = "0";
    public String[] key = WireFormatNano.EMPTY_STRING_ARRAY;
    public Serving.CacheOption liveJsCacheOption = null;
    public Serving.FunctionCall[] macro = Serving.FunctionCall.EMPTY_ARRAY;
    public String malwareScanAuthCode = "";
    public boolean oBSOLETEEnableAutoEventTracking = false;
    public Serving.FunctionCall[] predicate = Serving.FunctionCall.EMPTY_ARRAY;
    public String previewAuthCode = "";
    public Serving.Property[] property = Serving.Property.EMPTY_ARRAY;
    public float reportingSampleRate = 0.0F;
    public int resourceFormatVersion = 0;
    public Serving.Rule[] rule = Serving.Rule.EMPTY_ARRAY;
    public String[] supplemental = WireFormatNano.EMPTY_STRING_ARRAY;
    public Serving.FunctionCall[] tag = Serving.FunctionCall.EMPTY_ARRAY;
    public String templateVersionSet = "0";
    public String[] usageContext = WireFormatNano.EMPTY_STRING_ARRAY;
    public TypeSystem.Value[] value = TypeSystem.Value.EMPTY_ARRAY;
    public String version = "";
    
    public static Resource parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new Resource().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static Resource parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (Resource)MessageNano.mergeFrom(new Resource(), paramArrayOfByte);
    }
    
    public final Resource clear()
    {
      this.supplemental = WireFormatNano.EMPTY_STRING_ARRAY;
      this.key = WireFormatNano.EMPTY_STRING_ARRAY;
      this.value = TypeSystem.Value.EMPTY_ARRAY;
      this.property = Serving.Property.EMPTY_ARRAY;
      this.macro = Serving.FunctionCall.EMPTY_ARRAY;
      this.tag = Serving.FunctionCall.EMPTY_ARRAY;
      this.predicate = Serving.FunctionCall.EMPTY_ARRAY;
      this.rule = Serving.Rule.EMPTY_ARRAY;
      this.previewAuthCode = "";
      this.malwareScanAuthCode = "";
      this.templateVersionSet = "0";
      this.version = "";
      this.liveJsCacheOption = null;
      this.reportingSampleRate = 0.0F;
      this.oBSOLETEEnableAutoEventTracking = false;
      this.usageContext = WireFormatNano.EMPTY_STRING_ARRAY;
      this.resourceFormatVersion = 0;
      this.unknownFieldData = null;
      this.cachedSize = -1;
      return this;
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (paramObject != this)
      {
        if (!(paramObject instanceof Resource)) {
          break label39;
        }
        paramObject = (Resource)paramObject;
        if (Arrays.equals(this.supplemental, ((Resource)paramObject).supplemental)) {
          break label41;
        }
        break label244;
      }
      for (;;)
      {
        label33:
        bool = false;
        label35:
        label39:
        label41:
        label160:
        label181:
        label202:
        label244:
        label328:
        label338:
        label348:
        label368:
        do
        {
          return bool;
          return true;
          return false;
          if ((!Arrays.equals(this.key, ((Resource)paramObject).key)) || (!Arrays.equals(this.value, ((Resource)paramObject).value)) || (!Arrays.equals(this.property, ((Resource)paramObject).property)) || (!Arrays.equals(this.macro, ((Resource)paramObject).macro)) || (!Arrays.equals(this.tag, ((Resource)paramObject).tag)) || (!Arrays.equals(this.predicate, ((Resource)paramObject).predicate)) || (!Arrays.equals(this.rule, ((Resource)paramObject).rule))) {
            break label33;
          }
          if (this.previewAuthCode != null)
          {
            if (!this.previewAuthCode.equals(((Resource)paramObject).previewAuthCode)) {
              break label33;
            }
            if (this.malwareScanAuthCode == null) {
              break label328;
            }
            if (!this.malwareScanAuthCode.equals(((Resource)paramObject).malwareScanAuthCode)) {
              break label33;
            }
            if (this.templateVersionSet == null) {
              break label338;
            }
            if (!this.templateVersionSet.equals(((Resource)paramObject).templateVersionSet)) {
              break label33;
            }
            if (this.version == null) {
              break label348;
            }
            if (!this.version.equals(((Resource)paramObject).version)) {
              break label33;
            }
          }
          for (;;)
          {
            if (this.liveJsCacheOption != null)
            {
              if (!this.liveJsCacheOption.equals(((Resource)paramObject).liveJsCacheOption)) {
                break;
              }
              if ((this.reportingSampleRate != ((Resource)paramObject).reportingSampleRate) || (this.oBSOLETEEnableAutoEventTracking != ((Resource)paramObject).oBSOLETEEnableAutoEventTracking) || (!Arrays.equals(this.usageContext, ((Resource)paramObject).usageContext)) || (this.resourceFormatVersion != ((Resource)paramObject).resourceFormatVersion)) {
                break;
              }
              if (this.unknownFieldData == null) {
                break label368;
              }
              if (this.unknownFieldData.equals(((Resource)paramObject).unknownFieldData)) {
                break label35;
              }
              break;
              if (((Resource)paramObject).previewAuthCode == null) {
                break label160;
              }
              break;
              if (((Resource)paramObject).malwareScanAuthCode == null) {
                break label181;
              }
              break;
              if (((Resource)paramObject).templateVersionSet == null) {
                break label202;
              }
              break;
              if (((Resource)paramObject).version != null) {
                break;
              }
            }
          }
          if (((Resource)paramObject).liveJsCacheOption == null) {
            break;
          }
          break label33;
        } while (((Resource)paramObject).unknownFieldData == null);
      }
    }
    
    public int getSerializedSize()
    {
      int m = 0;
      int i;
      if (this.key == null)
      {
        i = 0;
        if (this.value != null) {
          break label244;
        }
        if (this.property != null) {
          break label287;
        }
        if (this.macro != null) {
          break label330;
        }
        if (this.tag != null) {
          break label373;
        }
        if (this.predicate != null) {
          break label416;
        }
        if (this.rule != null) {
          break label460;
        }
        j = i;
        if (!this.previewAuthCode.equals("")) {
          break label502;
        }
        label68:
        if (!this.malwareScanAuthCode.equals("")) {
          break label517;
        }
        label80:
        if (!this.templateVersionSet.equals("0")) {
          break label532;
        }
        label92:
        if (!this.version.equals("")) {
          break label547;
        }
        label104:
        if (this.liveJsCacheOption != null) {
          break label562;
        }
        label111:
        i = j;
        if (this.reportingSampleRate != 0.0F) {
          i = j + CodedOutputByteBufferNano.computeFloatSize(15, this.reportingSampleRate);
        }
        if (this.usageContext != null) {
          break label577;
        }
        j = i;
        label143:
        if (this.resourceFormatVersion != 0) {
          break label640;
        }
        i = j;
        label152:
        if (this.oBSOLETEEnableAutoEventTracking) {
          break label655;
        }
        label159:
        if (this.supplemental != null) {
          break label670;
        }
        j = i;
      }
      label244:
      label287:
      label330:
      label373:
      label416:
      label460:
      label502:
      label517:
      label532:
      label547:
      label562:
      label577:
      label640:
      label655:
      label670:
      do
      {
        i = j + WireFormatNano.computeWireSize(this.unknownFieldData);
        this.cachedSize = i;
        return i;
        if (this.key.length <= 0) {
          break;
        }
        localObject = this.key;
        k = localObject.length;
        i = 0;
        j = 0;
        for (;;)
        {
          if (i >= k)
          {
            i = j + 0 + this.key.length * 1;
            break;
          }
          j += CodedOutputByteBufferNano.computeStringSizeNoTag(localObject[i]);
          i += 1;
        }
        localObject = this.value;
        n = localObject.length;
        k = 0;
        for (j = i;; j = i + j)
        {
          i = j;
          if (k >= n) {
            break;
          }
          i = CodedOutputByteBufferNano.computeMessageSize(2, localObject[k]);
          k += 1;
        }
        localObject = this.property;
        n = localObject.length;
        k = 0;
        for (j = i;; j = i + j)
        {
          i = j;
          if (k >= n) {
            break;
          }
          i = CodedOutputByteBufferNano.computeMessageSize(3, localObject[k]);
          k += 1;
        }
        localObject = this.macro;
        n = localObject.length;
        k = 0;
        for (j = i;; j = i + j)
        {
          i = j;
          if (k >= n) {
            break;
          }
          i = CodedOutputByteBufferNano.computeMessageSize(4, localObject[k]);
          k += 1;
        }
        localObject = this.tag;
        n = localObject.length;
        k = 0;
        for (j = i;; j = i + j)
        {
          i = j;
          if (k >= n) {
            break;
          }
          i = CodedOutputByteBufferNano.computeMessageSize(5, localObject[k]);
          k += 1;
        }
        localObject = this.predicate;
        n = localObject.length;
        k = 0;
        for (j = i;; j = i + j)
        {
          i = j;
          if (k >= n) {
            break;
          }
          i = CodedOutputByteBufferNano.computeMessageSize(6, localObject[k]);
          k += 1;
        }
        localObject = this.rule;
        n = localObject.length;
        k = 0;
        for (;;)
        {
          j = i;
          if (k >= n) {
            break;
          }
          j = CodedOutputByteBufferNano.computeMessageSize(7, localObject[k]);
          k += 1;
          i = j + i;
        }
        j += CodedOutputByteBufferNano.computeStringSize(9, this.previewAuthCode);
        break label68;
        j += CodedOutputByteBufferNano.computeStringSize(10, this.malwareScanAuthCode);
        break label80;
        j += CodedOutputByteBufferNano.computeStringSize(12, this.templateVersionSet);
        break label92;
        j += CodedOutputByteBufferNano.computeStringSize(13, this.version);
        break label104;
        j += CodedOutputByteBufferNano.computeMessageSize(14, this.liveJsCacheOption);
        break label111;
        j = i;
        if (this.usageContext.length <= 0) {
          break label143;
        }
        localObject = this.usageContext;
        n = localObject.length;
        j = 0;
        k = 0;
        for (;;)
        {
          if (j >= n)
          {
            j = i + k + this.usageContext.length * 2;
            break;
          }
          k += CodedOutputByteBufferNano.computeStringSizeNoTag(localObject[j]);
          j += 1;
        }
        i = j + CodedOutputByteBufferNano.computeInt32Size(17, this.resourceFormatVersion);
        break label152;
        i += CodedOutputByteBufferNano.computeBoolSize(18, this.oBSOLETEEnableAutoEventTracking);
        break label159;
        j = i;
      } while (this.supplemental.length <= 0);
      Object localObject = this.supplemental;
      int n = localObject.length;
      int k = 0;
      int j = m;
      for (;;)
      {
        if (j >= n)
        {
          j = i + k + this.supplemental.length * 2;
          break;
        }
        k += CodedOutputByteBufferNano.computeStringSizeNoTag(localObject[j]);
        j += 1;
      }
    }
    
    public int hashCode()
    {
      int i3 = 0;
      int i;
      int j;
      label24:
      label42:
      label60:
      label78:
      label96:
      label114:
      label132:
      label150:
      label165:
      label180:
      int m;
      label196:
      int n;
      label212:
      int i1;
      label228:
      int i2;
      if (this.supplemental != null)
      {
        i = 17;
        j = 0;
        if (j < this.supplemental.length) {
          break label351;
        }
        if (this.key == null) {
          break label389;
        }
        j = 0;
        if (j < this.key.length) {
          break label397;
        }
        if (this.value == null) {
          break label435;
        }
        j = 0;
        if (j < this.value.length) {
          break label443;
        }
        if (this.property == null) {
          break label481;
        }
        j = 0;
        if (j < this.property.length) {
          break label489;
        }
        if (this.macro == null) {
          break label527;
        }
        j = 0;
        if (j < this.macro.length) {
          break label535;
        }
        if (this.tag == null) {
          break label573;
        }
        j = 0;
        if (j < this.tag.length) {
          break label581;
        }
        if (this.predicate == null) {
          break label619;
        }
        j = 0;
        if (j < this.predicate.length) {
          break label627;
        }
        if (this.rule == null) {
          break label665;
        }
        j = 0;
        if (j < this.rule.length) {
          break label673;
        }
        if (this.previewAuthCode == null) {
          break label711;
        }
        j = this.previewAuthCode.hashCode();
        if (this.malwareScanAuthCode == null) {
          break label716;
        }
        k = this.malwareScanAuthCode.hashCode();
        if (this.templateVersionSet == null) {
          break label721;
        }
        m = this.templateVersionSet.hashCode();
        if (this.version == null) {
          break label727;
        }
        n = this.version.hashCode();
        if (this.liveJsCacheOption == null) {
          break label733;
        }
        i1 = this.liveJsCacheOption.hashCode();
        int i4 = Float.floatToIntBits(this.reportingSampleRate);
        if (this.oBSOLETEEnableAutoEventTracking) {
          break label739;
        }
        i2 = 2;
        label247:
        i = i2 + ((i1 + (n + (m + (k + (j + i * 31) * 31) * 31) * 31) * 31) * 31 + i4) * 31;
        if (this.usageContext == null) {
          break label745;
        }
        j = 0;
        if (j < this.usageContext.length) {
          break label753;
        }
      }
      for (;;)
      {
        k = this.resourceFormatVersion;
        j = i3;
        if (this.unknownFieldData != null) {
          j = this.unknownFieldData.hashCode();
        }
        return (i * 31 + k) * 31 + j;
        i = 527;
        break label24;
        label351:
        if (this.supplemental[j] != null) {}
        for (k = this.supplemental[j].hashCode();; k = 0)
        {
          i = k + i * 31;
          j += 1;
          break;
        }
        label389:
        i *= 31;
        break label42;
        label397:
        if (this.key[j] != null) {}
        for (k = this.key[j].hashCode();; k = 0)
        {
          i = k + i * 31;
          j += 1;
          break;
        }
        label435:
        i *= 31;
        break label60;
        label443:
        if (this.value[j] != null) {}
        for (k = this.value[j].hashCode();; k = 0)
        {
          i = k + i * 31;
          j += 1;
          break;
        }
        label481:
        i *= 31;
        break label78;
        label489:
        if (this.property[j] != null) {}
        for (k = this.property[j].hashCode();; k = 0)
        {
          i = k + i * 31;
          j += 1;
          break;
        }
        label527:
        i *= 31;
        break label96;
        label535:
        if (this.macro[j] != null) {}
        for (k = this.macro[j].hashCode();; k = 0)
        {
          i = k + i * 31;
          j += 1;
          break;
        }
        label573:
        i *= 31;
        break label114;
        label581:
        if (this.tag[j] != null) {}
        for (k = this.tag[j].hashCode();; k = 0)
        {
          i = k + i * 31;
          j += 1;
          break;
        }
        label619:
        i *= 31;
        break label132;
        label627:
        if (this.predicate[j] != null) {}
        for (k = this.predicate[j].hashCode();; k = 0)
        {
          i = k + i * 31;
          j += 1;
          break;
        }
        label665:
        i *= 31;
        break label150;
        label673:
        if (this.rule[j] != null) {}
        for (k = this.rule[j].hashCode();; k = 0)
        {
          i = k + i * 31;
          j += 1;
          break;
        }
        label711:
        j = 0;
        break label165;
        label716:
        k = 0;
        break label180;
        label721:
        m = 0;
        break label196;
        label727:
        n = 0;
        break label212;
        label733:
        i1 = 0;
        break label228;
        label739:
        i2 = 1;
        break label247;
        label745:
        i *= 31;
      }
      label753:
      if (this.usageContext[j] != null) {}
      for (int k = this.usageContext[j].hashCode();; k = 0)
      {
        i = k + i * 31;
        j += 1;
        break;
      }
    }
    
    public Resource mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      for (;;)
      {
        i = paramCodedInputByteBufferNano.readTag();
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
        case 10: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 10);
          i = this.key.length;
          localObject = new String[j + i];
          System.arraycopy(this.key, 0, localObject, 0, i);
          this.key = ((String[])localObject);
          for (;;)
          {
            if (i >= this.key.length - 1)
            {
              this.key[i] = paramCodedInputByteBufferNano.readString();
              break;
            }
            this.key[i] = paramCodedInputByteBufferNano.readString();
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 18: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 18);
          if (this.value != null)
          {
            i = this.value.length;
            localObject = new TypeSystem.Value[j + i];
            if (this.value != null) {
              break label365;
            }
            this.value = ((TypeSystem.Value[])localObject);
          }
          for (;;)
          {
            if (i >= this.value.length - 1)
            {
              this.value[i] = new TypeSystem.Value();
              paramCodedInputByteBufferNano.readMessage(this.value[i]);
              break;
              i = 0;
              break label302;
              System.arraycopy(this.value, 0, localObject, 0, i);
              break label317;
            }
            this.value[i] = new TypeSystem.Value();
            paramCodedInputByteBufferNano.readMessage(this.value[i]);
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 26: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 26);
          if (this.property != null)
          {
            i = this.property.length;
            localObject = new Serving.Property[j + i];
            if (this.property != null) {
              break label498;
            }
            this.property = ((Serving.Property[])localObject);
          }
          for (;;)
          {
            if (i >= this.property.length - 1)
            {
              this.property[i] = new Serving.Property();
              paramCodedInputByteBufferNano.readMessage(this.property[i]);
              break;
              i = 0;
              break label435;
              System.arraycopy(this.property, 0, localObject, 0, i);
              break label450;
            }
            this.property[i] = new Serving.Property();
            paramCodedInputByteBufferNano.readMessage(this.property[i]);
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 34: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 34);
          if (this.macro != null)
          {
            i = this.macro.length;
            localObject = new Serving.FunctionCall[j + i];
            if (this.macro != null) {
              break label631;
            }
            this.macro = ((Serving.FunctionCall[])localObject);
          }
          for (;;)
          {
            if (i >= this.macro.length - 1)
            {
              this.macro[i] = new Serving.FunctionCall();
              paramCodedInputByteBufferNano.readMessage(this.macro[i]);
              break;
              i = 0;
              break label568;
              System.arraycopy(this.macro, 0, localObject, 0, i);
              break label583;
            }
            this.macro[i] = new Serving.FunctionCall();
            paramCodedInputByteBufferNano.readMessage(this.macro[i]);
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 42: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 42);
          if (this.tag != null)
          {
            i = this.tag.length;
            localObject = new Serving.FunctionCall[j + i];
            if (this.tag != null) {
              break label764;
            }
            this.tag = ((Serving.FunctionCall[])localObject);
          }
          for (;;)
          {
            if (i >= this.tag.length - 1)
            {
              this.tag[i] = new Serving.FunctionCall();
              paramCodedInputByteBufferNano.readMessage(this.tag[i]);
              break;
              i = 0;
              break label701;
              System.arraycopy(this.tag, 0, localObject, 0, i);
              break label716;
            }
            this.tag[i] = new Serving.FunctionCall();
            paramCodedInputByteBufferNano.readMessage(this.tag[i]);
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 50: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 50);
          if (this.predicate != null)
          {
            i = this.predicate.length;
            localObject = new Serving.FunctionCall[j + i];
            if (this.predicate != null) {
              break label897;
            }
            this.predicate = ((Serving.FunctionCall[])localObject);
          }
          for (;;)
          {
            if (i >= this.predicate.length - 1)
            {
              this.predicate[i] = new Serving.FunctionCall();
              paramCodedInputByteBufferNano.readMessage(this.predicate[i]);
              break;
              i = 0;
              break label834;
              System.arraycopy(this.predicate, 0, localObject, 0, i);
              break label849;
            }
            this.predicate[i] = new Serving.FunctionCall();
            paramCodedInputByteBufferNano.readMessage(this.predicate[i]);
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 58: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 58);
          if (this.rule != null)
          {
            i = this.rule.length;
            localObject = new Serving.Rule[j + i];
            if (this.rule != null) {
              break label1030;
            }
            this.rule = ((Serving.Rule[])localObject);
          }
          for (;;)
          {
            if (i >= this.rule.length - 1)
            {
              this.rule[i] = new Serving.Rule();
              paramCodedInputByteBufferNano.readMessage(this.rule[i]);
              break;
              i = 0;
              break label967;
              System.arraycopy(this.rule, 0, localObject, 0, i);
              break label982;
            }
            this.rule[i] = new Serving.Rule();
            paramCodedInputByteBufferNano.readMessage(this.rule[i]);
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 74: 
          this.previewAuthCode = paramCodedInputByteBufferNano.readString();
          break;
        case 82: 
          this.malwareScanAuthCode = paramCodedInputByteBufferNano.readString();
          break;
        case 98: 
          this.templateVersionSet = paramCodedInputByteBufferNano.readString();
          break;
        case 106: 
          this.version = paramCodedInputByteBufferNano.readString();
          break;
        case 114: 
          this.liveJsCacheOption = new Serving.CacheOption();
          paramCodedInputByteBufferNano.readMessage(this.liveJsCacheOption);
          break;
        case 125: 
          this.reportingSampleRate = paramCodedInputByteBufferNano.readFloat();
          break;
        case 130: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 130);
          i = this.usageContext.length;
          localObject = new String[j + i];
          System.arraycopy(this.usageContext, 0, localObject, 0, i);
          this.usageContext = ((String[])localObject);
          for (;;)
          {
            if (i >= this.usageContext.length - 1)
            {
              this.usageContext[i] = paramCodedInputByteBufferNano.readString();
              break;
            }
            this.usageContext[i] = paramCodedInputByteBufferNano.readString();
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 136: 
          this.resourceFormatVersion = paramCodedInputByteBufferNano.readInt32();
          break;
        case 144: 
          label302:
          label317:
          label365:
          label435:
          label450:
          label498:
          label568:
          label583:
          label631:
          label701:
          label716:
          label764:
          label834:
          label849:
          label897:
          label967:
          label982:
          label1030:
          this.oBSOLETEEnableAutoEventTracking = paramCodedInputByteBufferNano.readBool();
        }
      }
      int j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 154);
      int i = this.supplemental.length;
      Object localObject = new String[j + i];
      System.arraycopy(this.supplemental, 0, localObject, 0, i);
      this.supplemental = ((String[])localObject);
      for (;;)
      {
        if (i >= this.supplemental.length - 1)
        {
          this.supplemental[i] = paramCodedInputByteBufferNano.readString();
          break;
        }
        this.supplemental[i] = paramCodedInputByteBufferNano.readString();
        paramCodedInputByteBufferNano.readTag();
        i += 1;
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      int j = 0;
      if (this.key == null)
      {
        if (this.value != null) {
          break label197;
        }
        label16:
        if (this.property != null) {
          break label232;
        }
        label23:
        if (this.macro != null) {
          break label267;
        }
        label30:
        if (this.tag != null) {
          break label302;
        }
        label37:
        if (this.predicate != null) {
          break label337;
        }
        label44:
        if (this.rule != null) {
          break label373;
        }
        label51:
        if (!this.previewAuthCode.equals("")) {
          break label409;
        }
        label63:
        if (!this.malwareScanAuthCode.equals("")) {
          break label422;
        }
        label75:
        if (!this.templateVersionSet.equals("0")) {
          break label435;
        }
        label87:
        if (!this.version.equals("")) {
          break label448;
        }
        label99:
        if (this.liveJsCacheOption != null) {
          break label461;
        }
        label106:
        if (this.reportingSampleRate != 0.0F) {
          paramCodedOutputByteBufferNano.writeFloat(15, this.reportingSampleRate);
        }
        if (this.usageContext != null) {
          break label474;
        }
        label132:
        if (this.resourceFormatVersion != 0) {
          break label510;
        }
        label139:
        if (this.oBSOLETEEnableAutoEventTracking) {
          break label523;
        }
        label146:
        if (this.supplemental != null) {
          break label536;
        }
      }
      for (;;)
      {
        WireFormatNano.writeUnknownFields(this.unknownFieldData, paramCodedOutputByteBufferNano);
        return;
        Object localObject = this.key;
        int k = localObject.length;
        int i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeString(1, localObject[i]);
          i += 1;
        }
        break;
        label197:
        localObject = this.value;
        k = localObject.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeMessage(2, localObject[i]);
          i += 1;
        }
        break label16;
        label232:
        localObject = this.property;
        k = localObject.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeMessage(3, localObject[i]);
          i += 1;
        }
        break label23;
        label267:
        localObject = this.macro;
        k = localObject.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeMessage(4, localObject[i]);
          i += 1;
        }
        break label30;
        label302:
        localObject = this.tag;
        k = localObject.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeMessage(5, localObject[i]);
          i += 1;
        }
        break label37;
        label337:
        localObject = this.predicate;
        k = localObject.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeMessage(6, localObject[i]);
          i += 1;
        }
        break label44;
        label373:
        localObject = this.rule;
        k = localObject.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeMessage(7, localObject[i]);
          i += 1;
        }
        break label51;
        label409:
        paramCodedOutputByteBufferNano.writeString(9, this.previewAuthCode);
        break label63;
        label422:
        paramCodedOutputByteBufferNano.writeString(10, this.malwareScanAuthCode);
        break label75;
        label435:
        paramCodedOutputByteBufferNano.writeString(12, this.templateVersionSet);
        break label87;
        label448:
        paramCodedOutputByteBufferNano.writeString(13, this.version);
        break label99;
        label461:
        paramCodedOutputByteBufferNano.writeMessage(14, this.liveJsCacheOption);
        break label106;
        label474:
        localObject = this.usageContext;
        k = localObject.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeString(16, localObject[i]);
          i += 1;
        }
        break label132;
        label510:
        paramCodedOutputByteBufferNano.writeInt32(17, this.resourceFormatVersion);
        break label139;
        label523:
        paramCodedOutputByteBufferNano.writeBool(18, this.oBSOLETEEnableAutoEventTracking);
        break label146;
        label536:
        localObject = this.supplemental;
        k = localObject.length;
        i = j;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeString(19, localObject[i]);
          i += 1;
        }
      }
    }
  }
  
  public static abstract interface ResourceState
  {
    public static final int LIVE = 2;
    public static final int PREVIEW = 1;
  }
  
  public static abstract interface ResourceType
  {
    public static final int CLEAR_CACHE = 6;
    public static final int GET_COOKIE = 5;
    public static final int JS_RESOURCE = 1;
    public static final int NS_RESOURCE = 2;
    public static final int PIXEL_COLLECTION = 3;
    public static final int RAW_PROTO = 7;
    public static final int SET_COOKIE = 4;
  }
  
  public static final class Rule
    extends ExtendableMessageNano
  {
    public static final Rule[] EMPTY_ARRAY = new Rule[0];
    public int[] addMacro = WireFormatNano.EMPTY_INT_ARRAY;
    public int[] addMacroRuleName = WireFormatNano.EMPTY_INT_ARRAY;
    public int[] addTag = WireFormatNano.EMPTY_INT_ARRAY;
    public int[] addTagRuleName = WireFormatNano.EMPTY_INT_ARRAY;
    public int[] negativePredicate = WireFormatNano.EMPTY_INT_ARRAY;
    public int[] positivePredicate = WireFormatNano.EMPTY_INT_ARRAY;
    public int[] removeMacro = WireFormatNano.EMPTY_INT_ARRAY;
    public int[] removeMacroRuleName = WireFormatNano.EMPTY_INT_ARRAY;
    public int[] removeTag = WireFormatNano.EMPTY_INT_ARRAY;
    public int[] removeTagRuleName = WireFormatNano.EMPTY_INT_ARRAY;
    
    public static Rule parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new Rule().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static Rule parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (Rule)MessageNano.mergeFrom(new Rule(), paramArrayOfByte);
    }
    
    public final Rule clear()
    {
      this.positivePredicate = WireFormatNano.EMPTY_INT_ARRAY;
      this.negativePredicate = WireFormatNano.EMPTY_INT_ARRAY;
      this.addTag = WireFormatNano.EMPTY_INT_ARRAY;
      this.removeTag = WireFormatNano.EMPTY_INT_ARRAY;
      this.addTagRuleName = WireFormatNano.EMPTY_INT_ARRAY;
      this.removeTagRuleName = WireFormatNano.EMPTY_INT_ARRAY;
      this.addMacro = WireFormatNano.EMPTY_INT_ARRAY;
      this.removeMacro = WireFormatNano.EMPTY_INT_ARRAY;
      this.addMacroRuleName = WireFormatNano.EMPTY_INT_ARRAY;
      this.removeMacroRuleName = WireFormatNano.EMPTY_INT_ARRAY;
      this.unknownFieldData = null;
      this.cachedSize = -1;
      return this;
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (paramObject != this)
      {
        if (!(paramObject instanceof Rule)) {
          break label39;
        }
        paramObject = (Rule)paramObject;
        if (Arrays.equals(this.positivePredicate, ((Rule)paramObject).positivePredicate)) {
          break label41;
        }
      }
      for (;;)
      {
        bool = false;
        label39:
        label41:
        label193:
        do
        {
          do
          {
            return bool;
            return true;
            return false;
            if ((!Arrays.equals(this.negativePredicate, ((Rule)paramObject).negativePredicate)) || (!Arrays.equals(this.addTag, ((Rule)paramObject).addTag)) || (!Arrays.equals(this.removeTag, ((Rule)paramObject).removeTag)) || (!Arrays.equals(this.addTagRuleName, ((Rule)paramObject).addTagRuleName)) || (!Arrays.equals(this.removeTagRuleName, ((Rule)paramObject).removeTagRuleName)) || (!Arrays.equals(this.addMacro, ((Rule)paramObject).addMacro)) || (!Arrays.equals(this.removeMacro, ((Rule)paramObject).removeMacro)) || (!Arrays.equals(this.addMacroRuleName, ((Rule)paramObject).addMacroRuleName)) || (!Arrays.equals(this.removeMacroRuleName, ((Rule)paramObject).removeMacroRuleName))) {
              break;
            }
            if (this.unknownFieldData == null) {
              break label193;
            }
          } while (this.unknownFieldData.equals(((Rule)paramObject).unknownFieldData));
          break;
        } while (((Rule)paramObject).unknownFieldData == null);
      }
    }
    
    public int getSerializedSize()
    {
      int m = 0;
      int j;
      if (this.positivePredicate == null)
      {
        j = 0;
        if (this.negativePredicate != null) {
          break label169;
        }
        i = j;
        label21:
        if (this.addTag != null) {
          break label232;
        }
        j = i;
        label30:
        if (this.removeTag != null) {
          break label295;
        }
        i = j;
        label39:
        if (this.addTagRuleName != null) {
          break label358;
        }
        j = i;
        label48:
        if (this.removeTagRuleName != null) {
          break label421;
        }
        i = j;
        label57:
        if (this.addMacro != null) {
          break label484;
        }
        j = i;
        label66:
        if (this.removeMacro != null) {
          break label547;
        }
        i = j;
        label75:
        if (this.addMacroRuleName != null) {
          break label610;
        }
        j = i;
        label84:
        if (this.removeMacroRuleName != null) {
          break label673;
        }
        i = j;
      }
      label169:
      label232:
      label295:
      label358:
      label421:
      label484:
      label547:
      label610:
      label673:
      do
      {
        i += WireFormatNano.computeWireSize(this.unknownFieldData);
        this.cachedSize = i;
        return i;
        if (this.positivePredicate.length <= 0) {
          break;
        }
        arrayOfInt = this.positivePredicate;
        k = arrayOfInt.length;
        i = 0;
        j = 0;
        for (;;)
        {
          if (i >= k)
          {
            j = j + 0 + this.positivePredicate.length * 1;
            break;
          }
          j += CodedOutputByteBufferNano.computeInt32SizeNoTag(arrayOfInt[i]);
          i += 1;
        }
        i = j;
        if (this.negativePredicate.length <= 0) {
          break label21;
        }
        arrayOfInt = this.negativePredicate;
        n = arrayOfInt.length;
        i = 0;
        k = 0;
        for (;;)
        {
          if (i >= n)
          {
            i = j + k + this.negativePredicate.length * 1;
            break;
          }
          k += CodedOutputByteBufferNano.computeInt32SizeNoTag(arrayOfInt[i]);
          i += 1;
        }
        j = i;
        if (this.addTag.length <= 0) {
          break label30;
        }
        arrayOfInt = this.addTag;
        n = arrayOfInt.length;
        j = 0;
        k = 0;
        for (;;)
        {
          if (j >= n)
          {
            j = i + k + this.addTag.length * 1;
            break;
          }
          k += CodedOutputByteBufferNano.computeInt32SizeNoTag(arrayOfInt[j]);
          j += 1;
        }
        i = j;
        if (this.removeTag.length <= 0) {
          break label39;
        }
        arrayOfInt = this.removeTag;
        n = arrayOfInt.length;
        i = 0;
        k = 0;
        for (;;)
        {
          if (i >= n)
          {
            i = j + k + this.removeTag.length * 1;
            break;
          }
          k += CodedOutputByteBufferNano.computeInt32SizeNoTag(arrayOfInt[i]);
          i += 1;
        }
        j = i;
        if (this.addTagRuleName.length <= 0) {
          break label48;
        }
        arrayOfInt = this.addTagRuleName;
        n = arrayOfInt.length;
        j = 0;
        k = 0;
        for (;;)
        {
          if (j >= n)
          {
            j = i + k + this.addTagRuleName.length * 1;
            break;
          }
          k += CodedOutputByteBufferNano.computeInt32SizeNoTag(arrayOfInt[j]);
          j += 1;
        }
        i = j;
        if (this.removeTagRuleName.length <= 0) {
          break label57;
        }
        arrayOfInt = this.removeTagRuleName;
        n = arrayOfInt.length;
        i = 0;
        k = 0;
        for (;;)
        {
          if (i >= n)
          {
            i = j + k + this.removeTagRuleName.length * 1;
            break;
          }
          k += CodedOutputByteBufferNano.computeInt32SizeNoTag(arrayOfInt[i]);
          i += 1;
        }
        j = i;
        if (this.addMacro.length <= 0) {
          break label66;
        }
        arrayOfInt = this.addMacro;
        n = arrayOfInt.length;
        j = 0;
        k = 0;
        for (;;)
        {
          if (j >= n)
          {
            j = i + k + this.addMacro.length * 1;
            break;
          }
          k += CodedOutputByteBufferNano.computeInt32SizeNoTag(arrayOfInt[j]);
          j += 1;
        }
        i = j;
        if (this.removeMacro.length <= 0) {
          break label75;
        }
        arrayOfInt = this.removeMacro;
        n = arrayOfInt.length;
        i = 0;
        k = 0;
        for (;;)
        {
          if (i >= n)
          {
            i = j + k + this.removeMacro.length * 1;
            break;
          }
          k += CodedOutputByteBufferNano.computeInt32SizeNoTag(arrayOfInt[i]);
          i += 1;
        }
        j = i;
        if (this.addMacroRuleName.length <= 0) {
          break label84;
        }
        arrayOfInt = this.addMacroRuleName;
        n = arrayOfInt.length;
        j = 0;
        k = 0;
        for (;;)
        {
          if (j >= n)
          {
            j = i + k + this.addMacroRuleName.length * 1;
            break;
          }
          k += CodedOutputByteBufferNano.computeInt32SizeNoTag(arrayOfInt[j]);
          j += 1;
        }
        i = j;
      } while (this.removeMacroRuleName.length <= 0);
      int[] arrayOfInt = this.removeMacroRuleName;
      int n = arrayOfInt.length;
      int k = 0;
      int i = m;
      for (;;)
      {
        if (i >= n)
        {
          i = j + k + this.removeMacroRuleName.length * 1;
          break;
        }
        k += CodedOutputByteBufferNano.computeInt32SizeNoTag(arrayOfInt[i]);
        i += 1;
      }
    }
    
    public int hashCode()
    {
      int k = 0;
      int i;
      int j;
      if (this.positivePredicate != null)
      {
        i = 17;
        j = 0;
        if (j < this.positivePredicate.length) {
          break label218;
        }
        label23:
        if (this.negativePredicate == null) {
          break label237;
        }
        j = 0;
        label32:
        if (j < this.negativePredicate.length) {
          break label245;
        }
        label41:
        if (this.addTag == null) {
          break label264;
        }
        j = 0;
        label50:
        if (j < this.addTag.length) {
          break label272;
        }
        label59:
        if (this.removeTag == null) {
          break label291;
        }
        j = 0;
        label68:
        if (j < this.removeTag.length) {
          break label299;
        }
        label77:
        if (this.addTagRuleName == null) {
          break label318;
        }
        j = 0;
        label86:
        if (j < this.addTagRuleName.length) {
          break label326;
        }
        label95:
        if (this.removeTagRuleName == null) {
          break label345;
        }
        j = 0;
        label104:
        if (j < this.removeTagRuleName.length) {
          break label353;
        }
        label113:
        if (this.addMacro == null) {
          break label372;
        }
        j = 0;
        label122:
        if (j < this.addMacro.length) {
          break label380;
        }
        label131:
        if (this.removeMacro == null) {
          break label399;
        }
        j = 0;
        label140:
        if (j < this.removeMacro.length) {
          break label407;
        }
        label149:
        if (this.addMacroRuleName == null) {
          break label426;
        }
        j = 0;
        label158:
        if (j < this.addMacroRuleName.length) {
          break label434;
        }
        label167:
        if (this.removeMacroRuleName == null) {
          break label453;
        }
        j = 0;
      }
      for (;;)
      {
        if (j >= this.removeMacroRuleName.length) {
          for (;;)
          {
            j = k;
            if (this.unknownFieldData != null) {
              j = this.unknownFieldData.hashCode();
            }
            return i * 31 + j;
            i = 527;
            break label23;
            label218:
            i = i * 31 + this.positivePredicate[j];
            j += 1;
            break;
            label237:
            i *= 31;
            break label41;
            label245:
            i = i * 31 + this.negativePredicate[j];
            j += 1;
            break label32;
            label264:
            i *= 31;
            break label59;
            label272:
            i = i * 31 + this.addTag[j];
            j += 1;
            break label50;
            label291:
            i *= 31;
            break label77;
            label299:
            i = i * 31 + this.removeTag[j];
            j += 1;
            break label68;
            label318:
            i *= 31;
            break label95;
            label326:
            i = i * 31 + this.addTagRuleName[j];
            j += 1;
            break label86;
            label345:
            i *= 31;
            break label113;
            label353:
            i = i * 31 + this.removeTagRuleName[j];
            j += 1;
            break label104;
            label372:
            i *= 31;
            break label131;
            label380:
            i = i * 31 + this.addMacro[j];
            j += 1;
            break label122;
            label399:
            i *= 31;
            break label149;
            label407:
            i = i * 31 + this.removeMacro[j];
            j += 1;
            break label140;
            label426:
            i *= 31;
            break label167;
            label434:
            i = i * 31 + this.addMacroRuleName[j];
            j += 1;
            break label158;
            label453:
            i *= 31;
          }
        }
        i = i * 31 + this.removeMacroRuleName[j];
        j += 1;
      }
    }
    
    public Rule mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      int i = paramCodedInputByteBufferNano.readTag();
      switch (i)
      {
      default: 
        if (this.unknownFieldData == null) {
          break;
        }
      case 0: 
        while (!WireFormatNano.storeUnknownField(this.unknownFieldData, paramCodedInputByteBufferNano, i))
        {
          return this;
          return this;
          this.unknownFieldData = new ArrayList();
        }
      case 8: 
        j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 8);
        i = this.positivePredicate.length;
        arrayOfInt = new int[j + i];
        System.arraycopy(this.positivePredicate, 0, arrayOfInt, 0, i);
        this.positivePredicate = arrayOfInt;
        for (;;)
        {
          if (i >= this.positivePredicate.length - 1)
          {
            this.positivePredicate[i] = paramCodedInputByteBufferNano.readInt32();
            break;
          }
          this.positivePredicate[i] = paramCodedInputByteBufferNano.readInt32();
          paramCodedInputByteBufferNano.readTag();
          i += 1;
        }
      case 16: 
        j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 16);
        i = this.negativePredicate.length;
        arrayOfInt = new int[j + i];
        System.arraycopy(this.negativePredicate, 0, arrayOfInt, 0, i);
        this.negativePredicate = arrayOfInt;
        for (;;)
        {
          if (i >= this.negativePredicate.length - 1)
          {
            this.negativePredicate[i] = paramCodedInputByteBufferNano.readInt32();
            break;
          }
          this.negativePredicate[i] = paramCodedInputByteBufferNano.readInt32();
          paramCodedInputByteBufferNano.readTag();
          i += 1;
        }
      case 24: 
        j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 24);
        i = this.addTag.length;
        arrayOfInt = new int[j + i];
        System.arraycopy(this.addTag, 0, arrayOfInt, 0, i);
        this.addTag = arrayOfInt;
        for (;;)
        {
          if (i >= this.addTag.length - 1)
          {
            this.addTag[i] = paramCodedInputByteBufferNano.readInt32();
            break;
          }
          this.addTag[i] = paramCodedInputByteBufferNano.readInt32();
          paramCodedInputByteBufferNano.readTag();
          i += 1;
        }
      case 32: 
        j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 32);
        i = this.removeTag.length;
        arrayOfInt = new int[j + i];
        System.arraycopy(this.removeTag, 0, arrayOfInt, 0, i);
        this.removeTag = arrayOfInt;
        for (;;)
        {
          if (i >= this.removeTag.length - 1)
          {
            this.removeTag[i] = paramCodedInputByteBufferNano.readInt32();
            break;
          }
          this.removeTag[i] = paramCodedInputByteBufferNano.readInt32();
          paramCodedInputByteBufferNano.readTag();
          i += 1;
        }
      case 40: 
        j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 40);
        i = this.addTagRuleName.length;
        arrayOfInt = new int[j + i];
        System.arraycopy(this.addTagRuleName, 0, arrayOfInt, 0, i);
        this.addTagRuleName = arrayOfInt;
        for (;;)
        {
          if (i >= this.addTagRuleName.length - 1)
          {
            this.addTagRuleName[i] = paramCodedInputByteBufferNano.readInt32();
            break;
          }
          this.addTagRuleName[i] = paramCodedInputByteBufferNano.readInt32();
          paramCodedInputByteBufferNano.readTag();
          i += 1;
        }
      case 48: 
        j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 48);
        i = this.removeTagRuleName.length;
        arrayOfInt = new int[j + i];
        System.arraycopy(this.removeTagRuleName, 0, arrayOfInt, 0, i);
        this.removeTagRuleName = arrayOfInt;
        for (;;)
        {
          if (i >= this.removeTagRuleName.length - 1)
          {
            this.removeTagRuleName[i] = paramCodedInputByteBufferNano.readInt32();
            break;
          }
          this.removeTagRuleName[i] = paramCodedInputByteBufferNano.readInt32();
          paramCodedInputByteBufferNano.readTag();
          i += 1;
        }
      case 56: 
        j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 56);
        i = this.addMacro.length;
        arrayOfInt = new int[j + i];
        System.arraycopy(this.addMacro, 0, arrayOfInt, 0, i);
        this.addMacro = arrayOfInt;
        for (;;)
        {
          if (i >= this.addMacro.length - 1)
          {
            this.addMacro[i] = paramCodedInputByteBufferNano.readInt32();
            break;
          }
          this.addMacro[i] = paramCodedInputByteBufferNano.readInt32();
          paramCodedInputByteBufferNano.readTag();
          i += 1;
        }
      case 64: 
        j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 64);
        i = this.removeMacro.length;
        arrayOfInt = new int[j + i];
        System.arraycopy(this.removeMacro, 0, arrayOfInt, 0, i);
        this.removeMacro = arrayOfInt;
        for (;;)
        {
          if (i >= this.removeMacro.length - 1)
          {
            this.removeMacro[i] = paramCodedInputByteBufferNano.readInt32();
            break;
          }
          this.removeMacro[i] = paramCodedInputByteBufferNano.readInt32();
          paramCodedInputByteBufferNano.readTag();
          i += 1;
        }
      case 72: 
        j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 72);
        i = this.addMacroRuleName.length;
        arrayOfInt = new int[j + i];
        System.arraycopy(this.addMacroRuleName, 0, arrayOfInt, 0, i);
        this.addMacroRuleName = arrayOfInt;
        for (;;)
        {
          if (i >= this.addMacroRuleName.length - 1)
          {
            this.addMacroRuleName[i] = paramCodedInputByteBufferNano.readInt32();
            break;
          }
          this.addMacroRuleName[i] = paramCodedInputByteBufferNano.readInt32();
          paramCodedInputByteBufferNano.readTag();
          i += 1;
        }
      }
      int j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 80);
      i = this.removeMacroRuleName.length;
      int[] arrayOfInt = new int[j + i];
      System.arraycopy(this.removeMacroRuleName, 0, arrayOfInt, 0, i);
      this.removeMacroRuleName = arrayOfInt;
      for (;;)
      {
        if (i >= this.removeMacroRuleName.length - 1)
        {
          this.removeMacroRuleName[i] = paramCodedInputByteBufferNano.readInt32();
          break;
        }
        this.removeMacroRuleName[i] = paramCodedInputByteBufferNano.readInt32();
        paramCodedInputByteBufferNano.readTag();
        i += 1;
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      int j = 0;
      if (this.positivePredicate == null)
      {
        if (this.negativePredicate != null) {
          break label116;
        }
        label16:
        if (this.addTag != null) {
          break label151;
        }
        label23:
        if (this.removeTag != null) {
          break label186;
        }
        label30:
        if (this.addTagRuleName != null) {
          break label221;
        }
        label37:
        if (this.removeTagRuleName != null) {
          break label256;
        }
        label44:
        if (this.addMacro != null) {
          break label292;
        }
        label51:
        if (this.removeMacro != null) {
          break label328;
        }
        label58:
        if (this.addMacroRuleName != null) {
          break label364;
        }
        label65:
        if (this.removeMacroRuleName != null) {
          break label400;
        }
      }
      for (;;)
      {
        WireFormatNano.writeUnknownFields(this.unknownFieldData, paramCodedOutputByteBufferNano);
        return;
        int[] arrayOfInt = this.positivePredicate;
        int k = arrayOfInt.length;
        int i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeInt32(1, arrayOfInt[i]);
          i += 1;
        }
        break;
        label116:
        arrayOfInt = this.negativePredicate;
        k = arrayOfInt.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeInt32(2, arrayOfInt[i]);
          i += 1;
        }
        break label16;
        label151:
        arrayOfInt = this.addTag;
        k = arrayOfInt.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeInt32(3, arrayOfInt[i]);
          i += 1;
        }
        break label23;
        label186:
        arrayOfInt = this.removeTag;
        k = arrayOfInt.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeInt32(4, arrayOfInt[i]);
          i += 1;
        }
        break label30;
        label221:
        arrayOfInt = this.addTagRuleName;
        k = arrayOfInt.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeInt32(5, arrayOfInt[i]);
          i += 1;
        }
        break label37;
        label256:
        arrayOfInt = this.removeTagRuleName;
        k = arrayOfInt.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeInt32(6, arrayOfInt[i]);
          i += 1;
        }
        break label44;
        label292:
        arrayOfInt = this.addMacro;
        k = arrayOfInt.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeInt32(7, arrayOfInt[i]);
          i += 1;
        }
        break label51;
        label328:
        arrayOfInt = this.removeMacro;
        k = arrayOfInt.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeInt32(8, arrayOfInt[i]);
          i += 1;
        }
        break label58;
        label364:
        arrayOfInt = this.addMacroRuleName;
        k = arrayOfInt.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeInt32(9, arrayOfInt[i]);
          i += 1;
        }
        break label65;
        label400:
        arrayOfInt = this.removeMacroRuleName;
        k = arrayOfInt.length;
        i = j;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeInt32(10, arrayOfInt[i]);
          i += 1;
        }
      }
    }
  }
  
  public static final class ServingValue
    extends ExtendableMessageNano
  {
    public static final ServingValue[] EMPTY_ARRAY = new ServingValue[0];
    public static final Extension<ServingValue> ext = Extension.create(101, new Extension.TypeLiteral() {});
    public int[] listItem = WireFormatNano.EMPTY_INT_ARRAY;
    public int macroNameReference = 0;
    public int macroReference = 0;
    public int[] mapKey = WireFormatNano.EMPTY_INT_ARRAY;
    public int[] mapValue = WireFormatNano.EMPTY_INT_ARRAY;
    public int tagReference = 0;
    public int[] templateToken = WireFormatNano.EMPTY_INT_ARRAY;
    
    public static ServingValue parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new ServingValue().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static ServingValue parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (ServingValue)MessageNano.mergeFrom(new ServingValue(), paramArrayOfByte);
    }
    
    public final ServingValue clear()
    {
      this.listItem = WireFormatNano.EMPTY_INT_ARRAY;
      this.mapKey = WireFormatNano.EMPTY_INT_ARRAY;
      this.mapValue = WireFormatNano.EMPTY_INT_ARRAY;
      this.macroReference = 0;
      this.templateToken = WireFormatNano.EMPTY_INT_ARRAY;
      this.macroNameReference = 0;
      this.tagReference = 0;
      this.unknownFieldData = null;
      this.cachedSize = -1;
      return this;
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (paramObject != this)
      {
        if (!(paramObject instanceof ServingValue)) {
          break label39;
        }
        paramObject = (ServingValue)paramObject;
        if (Arrays.equals(this.listItem, ((ServingValue)paramObject).listItem)) {
          break label41;
        }
      }
      for (;;)
      {
        bool = false;
        label39:
        label41:
        label142:
        do
        {
          do
          {
            return bool;
            return true;
            return false;
            if ((!Arrays.equals(this.mapKey, ((ServingValue)paramObject).mapKey)) || (!Arrays.equals(this.mapValue, ((ServingValue)paramObject).mapValue)) || (this.macroReference != ((ServingValue)paramObject).macroReference) || (!Arrays.equals(this.templateToken, ((ServingValue)paramObject).templateToken)) || (this.macroNameReference != ((ServingValue)paramObject).macroNameReference) || (this.tagReference != ((ServingValue)paramObject).tagReference)) {
              break;
            }
            if (this.unknownFieldData == null) {
              break label142;
            }
          } while (this.unknownFieldData.equals(((ServingValue)paramObject).unknownFieldData));
          break;
        } while (((ServingValue)paramObject).unknownFieldData == null);
      }
    }
    
    public int getSerializedSize()
    {
      int m = 0;
      int j;
      int i;
      if (this.listItem == null)
      {
        j = 0;
        if (this.mapKey != null) {
          break label138;
        }
        i = j;
        label21:
        if (this.mapValue != null) {
          break label201;
        }
        j = i;
        label30:
        if (this.macroReference != 0) {
          break label264;
        }
        i = j;
        label39:
        if (this.templateToken != null) {
          break label278;
        }
        j = i;
        label48:
        if (this.macroNameReference != 0) {
          break label342;
        }
        label55:
        if (this.tagReference != 0) {
          break label357;
        }
      }
      for (;;)
      {
        i = j + WireFormatNano.computeWireSize(this.unknownFieldData);
        this.cachedSize = i;
        return i;
        if (this.listItem.length <= 0) {
          break;
        }
        int[] arrayOfInt = this.listItem;
        int k = arrayOfInt.length;
        i = 0;
        j = 0;
        for (;;)
        {
          if (i >= k)
          {
            j = j + 0 + this.listItem.length * 1;
            break;
          }
          j += CodedOutputByteBufferNano.computeInt32SizeNoTag(arrayOfInt[i]);
          i += 1;
        }
        label138:
        i = j;
        if (this.mapKey.length <= 0) {
          break label21;
        }
        arrayOfInt = this.mapKey;
        int n = arrayOfInt.length;
        i = 0;
        k = 0;
        for (;;)
        {
          if (i >= n)
          {
            i = j + k + this.mapKey.length * 1;
            break;
          }
          k += CodedOutputByteBufferNano.computeInt32SizeNoTag(arrayOfInt[i]);
          i += 1;
        }
        label201:
        j = i;
        if (this.mapValue.length <= 0) {
          break label30;
        }
        arrayOfInt = this.mapValue;
        n = arrayOfInt.length;
        j = 0;
        k = 0;
        for (;;)
        {
          if (j >= n)
          {
            j = i + k + this.mapValue.length * 1;
            break;
          }
          k += CodedOutputByteBufferNano.computeInt32SizeNoTag(arrayOfInt[j]);
          j += 1;
        }
        label264:
        i = j + CodedOutputByteBufferNano.computeInt32Size(4, this.macroReference);
        break label39;
        label278:
        j = i;
        if (this.templateToken.length <= 0) {
          break label48;
        }
        arrayOfInt = this.templateToken;
        n = arrayOfInt.length;
        k = 0;
        j = m;
        for (;;)
        {
          if (j >= n)
          {
            j = i + k + this.templateToken.length * 1;
            break;
          }
          k += CodedOutputByteBufferNano.computeInt32SizeNoTag(arrayOfInt[j]);
          j += 1;
        }
        label342:
        j += CodedOutputByteBufferNano.computeInt32Size(6, this.macroNameReference);
        break label55;
        label357:
        j += CodedOutputByteBufferNano.computeInt32Size(7, this.tagReference);
      }
    }
    
    public int hashCode()
    {
      int k = 0;
      int i;
      int j;
      if (this.listItem != null)
      {
        i = 17;
        j = 0;
        if (j < this.listItem.length) {
          break label144;
        }
        label23:
        if (this.mapKey == null) {
          break label163;
        }
        j = 0;
        label32:
        if (j < this.mapKey.length) {
          break label171;
        }
        label41:
        if (this.mapValue == null) {
          break label190;
        }
        j = 0;
        label50:
        if (j < this.mapValue.length) {
          break label198;
        }
        label59:
        i = i * 31 + this.macroReference;
        if (this.templateToken == null) {
          break label217;
        }
        j = 0;
      }
      for (;;)
      {
        if (j >= this.templateToken.length) {
          for (;;)
          {
            int m = this.macroNameReference;
            int n = this.tagReference;
            j = k;
            if (this.unknownFieldData != null) {
              j = this.unknownFieldData.hashCode();
            }
            return ((i * 31 + m) * 31 + n) * 31 + j;
            i = 527;
            break label23;
            label144:
            i = i * 31 + this.listItem[j];
            j += 1;
            break;
            label163:
            i *= 31;
            break label41;
            label171:
            i = i * 31 + this.mapKey[j];
            j += 1;
            break label32;
            label190:
            i *= 31;
            break label59;
            label198:
            i = i * 31 + this.mapValue[j];
            j += 1;
            break label50;
            label217:
            i *= 31;
          }
        }
        i = i * 31 + this.templateToken[j];
        j += 1;
      }
    }
    
    public ServingValue mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      for (;;)
      {
        int i = paramCodedInputByteBufferNano.readTag();
        int j;
        int[] arrayOfInt;
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
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 8);
          i = this.listItem.length;
          arrayOfInt = new int[j + i];
          System.arraycopy(this.listItem, 0, arrayOfInt, 0, i);
          this.listItem = arrayOfInt;
          for (;;)
          {
            if (i >= this.listItem.length - 1)
            {
              this.listItem[i] = paramCodedInputByteBufferNano.readInt32();
              break;
            }
            this.listItem[i] = paramCodedInputByteBufferNano.readInt32();
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 16: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 16);
          i = this.mapKey.length;
          arrayOfInt = new int[j + i];
          System.arraycopy(this.mapKey, 0, arrayOfInt, 0, i);
          this.mapKey = arrayOfInt;
          for (;;)
          {
            if (i >= this.mapKey.length - 1)
            {
              this.mapKey[i] = paramCodedInputByteBufferNano.readInt32();
              break;
            }
            this.mapKey[i] = paramCodedInputByteBufferNano.readInt32();
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 24: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 24);
          i = this.mapValue.length;
          arrayOfInt = new int[j + i];
          System.arraycopy(this.mapValue, 0, arrayOfInt, 0, i);
          this.mapValue = arrayOfInt;
          for (;;)
          {
            if (i >= this.mapValue.length - 1)
            {
              this.mapValue[i] = paramCodedInputByteBufferNano.readInt32();
              break;
            }
            this.mapValue[i] = paramCodedInputByteBufferNano.readInt32();
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 32: 
          this.macroReference = paramCodedInputByteBufferNano.readInt32();
          break;
        case 40: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 40);
          i = this.templateToken.length;
          arrayOfInt = new int[j + i];
          System.arraycopy(this.templateToken, 0, arrayOfInt, 0, i);
          this.templateToken = arrayOfInt;
          for (;;)
          {
            if (i >= this.templateToken.length - 1)
            {
              this.templateToken[i] = paramCodedInputByteBufferNano.readInt32();
              break;
            }
            this.templateToken[i] = paramCodedInputByteBufferNano.readInt32();
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 48: 
          this.macroNameReference = paramCodedInputByteBufferNano.readInt32();
          break;
        case 56: 
          this.tagReference = paramCodedInputByteBufferNano.readInt32();
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      int j = 0;
      if (this.listItem == null)
      {
        if (this.mapKey != null) {
          break label95;
        }
        label16:
        if (this.mapValue != null) {
          break label130;
        }
        label23:
        if (this.macroReference != 0) {
          break label165;
        }
        label30:
        if (this.templateToken != null) {
          break label177;
        }
        label37:
        if (this.macroNameReference != 0) {
          break label212;
        }
        label44:
        if (this.tagReference != 0) {
          break label225;
        }
      }
      for (;;)
      {
        WireFormatNano.writeUnknownFields(this.unknownFieldData, paramCodedOutputByteBufferNano);
        return;
        int[] arrayOfInt = this.listItem;
        int k = arrayOfInt.length;
        int i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeInt32(1, arrayOfInt[i]);
          i += 1;
        }
        break;
        label95:
        arrayOfInt = this.mapKey;
        k = arrayOfInt.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeInt32(2, arrayOfInt[i]);
          i += 1;
        }
        break label16;
        label130:
        arrayOfInt = this.mapValue;
        k = arrayOfInt.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeInt32(3, arrayOfInt[i]);
          i += 1;
        }
        break label23;
        label165:
        paramCodedOutputByteBufferNano.writeInt32(4, this.macroReference);
        break label30;
        label177:
        arrayOfInt = this.templateToken;
        k = arrayOfInt.length;
        i = j;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeInt32(5, arrayOfInt[i]);
          i += 1;
        }
        break label37;
        label212:
        paramCodedOutputByteBufferNano.writeInt32(6, this.macroNameReference);
        break label44;
        label225:
        paramCodedOutputByteBufferNano.writeInt32(7, this.tagReference);
      }
    }
  }
  
  public static final class Supplemental
    extends ExtendableMessageNano
  {
    public static final Supplemental[] EMPTY_ARRAY = new Supplemental[0];
    public Serving.GaExperimentSupplemental experimentSupplemental = null;
    public String name = "";
    public TypeSystem.Value value = null;
    
    public static Supplemental parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new Supplemental().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static Supplemental parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (Supplemental)MessageNano.mergeFrom(new Supplemental(), paramArrayOfByte);
    }
    
    public final Supplemental clear()
    {
      this.name = "";
      this.value = null;
      this.experimentSupplemental = null;
      this.unknownFieldData = null;
      this.cachedSize = -1;
      return this;
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (paramObject != this)
      {
        if (!(paramObject instanceof Supplemental)) {
          break label46;
        }
        paramObject = (Supplemental)paramObject;
        if (this.name == null) {
          break label48;
        }
        if (this.name.equals(((Supplemental)paramObject).name)) {
          break label55;
        }
      }
      for (;;)
      {
        bool = false;
        label42:
        label46:
        label48:
        label55:
        label76:
        label133:
        do
        {
          return bool;
          return true;
          return false;
          if (((Supplemental)paramObject).name != null) {
            break;
          }
          if (this.value != null)
          {
            if (!this.value.equals(((Supplemental)paramObject).value)) {
              break;
            }
            if (this.experimentSupplemental == null) {
              break label133;
            }
            if (!this.experimentSupplemental.equals(((Supplemental)paramObject).experimentSupplemental)) {
              break;
            }
          }
          for (;;)
          {
            if (this.unknownFieldData != null)
            {
              if (this.unknownFieldData.equals(((Supplemental)paramObject).unknownFieldData)) {
                break label42;
              }
              break;
              if (((Supplemental)paramObject).value == null) {
                break label76;
              }
              break;
              if (((Supplemental)paramObject).experimentSupplemental != null) {
                break;
              }
            }
          }
        } while (((Supplemental)paramObject).unknownFieldData == null);
      }
    }
    
    public int getSerializedSize()
    {
      int i = 0;
      if (this.name.equals(""))
      {
        if (this.value != null) {
          break label59;
        }
        label21:
        if (this.experimentSupplemental != null) {
          break label73;
        }
      }
      for (;;)
      {
        i += WireFormatNano.computeWireSize(this.unknownFieldData);
        this.cachedSize = i;
        return i;
        i = CodedOutputByteBufferNano.computeStringSize(1, this.name) + 0;
        break;
        label59:
        i += CodedOutputByteBufferNano.computeMessageSize(2, this.value);
        break label21;
        label73:
        i += CodedOutputByteBufferNano.computeMessageSize(3, this.experimentSupplemental);
      }
    }
    
    public int hashCode()
    {
      int m = 0;
      int i;
      int j;
      if (this.name != null)
      {
        i = this.name.hashCode();
        if (this.value == null) {
          break label93;
        }
        j = this.value.hashCode();
        label33:
        if (this.experimentSupplemental == null) {
          break label98;
        }
      }
      label93:
      label98:
      for (int k = this.experimentSupplemental.hashCode();; k = 0)
      {
        if (this.unknownFieldData != null) {
          m = this.unknownFieldData.hashCode();
        }
        return (k + (j + (i + 527) * 31) * 31) * 31 + m;
        i = 0;
        break;
        j = 0;
        break label33;
      }
    }
    
    public Supplemental mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
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
        case 10: 
          this.name = paramCodedInputByteBufferNano.readString();
          break;
        case 18: 
          this.value = new TypeSystem.Value();
          paramCodedInputByteBufferNano.readMessage(this.value);
          break;
        case 26: 
          this.experimentSupplemental = new Serving.GaExperimentSupplemental();
          paramCodedInputByteBufferNano.readMessage(this.experimentSupplemental);
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if (this.name.equals(""))
      {
        if (this.value != null) {
          break label47;
        }
        label19:
        if (this.experimentSupplemental != null) {
          break label59;
        }
      }
      for (;;)
      {
        WireFormatNano.writeUnknownFields(this.unknownFieldData, paramCodedOutputByteBufferNano);
        return;
        paramCodedOutputByteBufferNano.writeString(1, this.name);
        break;
        label47:
        paramCodedOutputByteBufferNano.writeMessage(2, this.value);
        break label19;
        label59:
        paramCodedOutputByteBufferNano.writeMessage(3, this.experimentSupplemental);
      }
    }
  }
  
  public static final class SupplementedResource
    extends ExtendableMessageNano
  {
    public static final SupplementedResource[] EMPTY_ARRAY = new SupplementedResource[0];
    public String fingerprint = "";
    public Serving.Resource resource = null;
    public Serving.Supplemental[] supplemental = Serving.Supplemental.EMPTY_ARRAY;
    
    public static SupplementedResource parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new SupplementedResource().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static SupplementedResource parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (SupplementedResource)MessageNano.mergeFrom(new SupplementedResource(), paramArrayOfByte);
    }
    
    public final SupplementedResource clear()
    {
      this.supplemental = Serving.Supplemental.EMPTY_ARRAY;
      this.resource = null;
      this.fingerprint = "";
      this.unknownFieldData = null;
      this.cachedSize = -1;
      return this;
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (paramObject != this)
      {
        if (!(paramObject instanceof SupplementedResource)) {
          break label39;
        }
        paramObject = (SupplementedResource)paramObject;
        if (Arrays.equals(this.supplemental, ((SupplementedResource)paramObject).supplemental)) {
          break label41;
        }
      }
      for (;;)
      {
        bool = false;
        label35:
        label39:
        label41:
        label62:
        label119:
        do
        {
          return bool;
          return true;
          return false;
          if (this.resource != null)
          {
            if (!this.resource.equals(((SupplementedResource)paramObject).resource)) {
              break;
            }
            if (this.fingerprint == null) {
              break label119;
            }
            if (!this.fingerprint.equals(((SupplementedResource)paramObject).fingerprint)) {
              break;
            }
          }
          for (;;)
          {
            if (this.unknownFieldData != null)
            {
              if (this.unknownFieldData.equals(((SupplementedResource)paramObject).unknownFieldData)) {
                break label35;
              }
              break;
              if (((SupplementedResource)paramObject).resource == null) {
                break label62;
              }
              break;
              if (((SupplementedResource)paramObject).fingerprint != null) {
                break;
              }
            }
          }
        } while (((SupplementedResource)paramObject).unknownFieldData == null);
      }
    }
    
    public int getSerializedSize()
    {
      int i = 0;
      int j = 0;
      if (this.supplemental == null)
      {
        if (this.resource != null) {
          break label88;
        }
        label18:
        if (!this.fingerprint.equals("")) {
          break label102;
        }
      }
      for (;;)
      {
        i = j + WireFormatNano.computeWireSize(this.unknownFieldData);
        this.cachedSize = i;
        return i;
        Serving.Supplemental[] arrayOfSupplemental = this.supplemental;
        int m = arrayOfSupplemental.length;
        int k = 0;
        for (;;)
        {
          j = i;
          if (k >= m) {
            break;
          }
          j = CodedOutputByteBufferNano.computeMessageSize(1, arrayOfSupplemental[k]);
          k += 1;
          i = j + i;
        }
        label88:
        j += CodedOutputByteBufferNano.computeMessageSize(2, this.resource);
        break label18;
        label102:
        j += CodedOutputByteBufferNano.computeStringSize(3, this.fingerprint);
      }
    }
    
    public int hashCode()
    {
      int m = 0;
      int i;
      int j;
      if (this.supplemental != null)
      {
        i = 17;
        j = 0;
        if (j < this.supplemental.length) {
          break label97;
        }
        label24:
        if (this.resource == null) {
          break label135;
        }
        j = this.resource.hashCode();
        label39:
        if (this.fingerprint == null) {
          break label140;
        }
      }
      label97:
      label135:
      label140:
      for (int k = this.fingerprint.hashCode();; k = 0)
      {
        if (this.unknownFieldData != null) {
          m = this.unknownFieldData.hashCode();
        }
        return (k + (j + i * 31) * 31) * 31 + m;
        i = 527;
        break label24;
        if (this.supplemental[j] != null) {}
        for (k = this.supplemental[j].hashCode();; k = 0)
        {
          i = k + i * 31;
          j += 1;
          break;
        }
        j = 0;
        break label39;
      }
    }
    
    public SupplementedResource mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
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
        case 10: 
          int j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 10);
          Serving.Supplemental[] arrayOfSupplemental;
          if (this.supplemental != null)
          {
            i = this.supplemental.length;
            arrayOfSupplemental = new Serving.Supplemental[j + i];
            if (this.supplemental != null) {
              break label168;
            }
            this.supplemental = arrayOfSupplemental;
          }
          for (;;)
          {
            if (i >= this.supplemental.length - 1)
            {
              this.supplemental[i] = new Serving.Supplemental();
              paramCodedInputByteBufferNano.readMessage(this.supplemental[i]);
              break;
              i = 0;
              break label105;
              System.arraycopy(this.supplemental, 0, arrayOfSupplemental, 0, i);
              break label120;
            }
            this.supplemental[i] = new Serving.Supplemental();
            paramCodedInputByteBufferNano.readMessage(this.supplemental[i]);
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 18: 
          this.resource = new Serving.Resource();
          paramCodedInputByteBufferNano.readMessage(this.resource);
          break;
        case 26: 
          label105:
          label120:
          label168:
          this.fingerprint = paramCodedInputByteBufferNano.readString();
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      int i = 0;
      if (this.supplemental == null)
      {
        if (this.resource != null) {
          break label68;
        }
        label16:
        if (!this.fingerprint.equals("")) {
          break label80;
        }
      }
      for (;;)
      {
        WireFormatNano.writeUnknownFields(this.unknownFieldData, paramCodedOutputByteBufferNano);
        return;
        Serving.Supplemental[] arrayOfSupplemental = this.supplemental;
        int j = arrayOfSupplemental.length;
        while (i < j)
        {
          paramCodedOutputByteBufferNano.writeMessage(1, arrayOfSupplemental[i]);
          i += 1;
        }
        break;
        label68:
        paramCodedOutputByteBufferNano.writeMessage(2, this.resource);
        break label16;
        label80:
        paramCodedOutputByteBufferNano.writeString(3, this.fingerprint);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\analytics\containertag\proto\Serving.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */