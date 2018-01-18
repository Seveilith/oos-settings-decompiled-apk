package com.google.analytics.midtier.proto.containertag;

import com.google.tagmanager.protobuf.nano.CodedInputByteBufferNano;
import com.google.tagmanager.protobuf.nano.CodedOutputByteBufferNano;
import com.google.tagmanager.protobuf.nano.ExtendableMessageNano;
import com.google.tagmanager.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.tagmanager.protobuf.nano.MessageNano;
import com.google.tagmanager.protobuf.nano.WireFormatNano;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract interface TypeSystem
{
  public static final class Value
    extends ExtendableMessageNano
  {
    public static final Value[] EMPTY_ARRAY = new Value[0];
    public boolean boolean_ = false;
    public boolean containsReferences = false;
    public int[] escaping = WireFormatNano.EMPTY_INT_ARRAY;
    public String functionId = "";
    public long integer = 0L;
    public Value[] listItem = EMPTY_ARRAY;
    public String macroReference = "";
    public Value[] mapKey = EMPTY_ARRAY;
    public Value[] mapValue = EMPTY_ARRAY;
    public String string = "";
    public String tagReference = "";
    public Value[] templateToken = EMPTY_ARRAY;
    public int type = 1;
    
    public static Value parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new Value().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static Value parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (Value)MessageNano.mergeFrom(new Value(), paramArrayOfByte);
    }
    
    public final Value clear()
    {
      this.type = 1;
      this.string = "";
      this.listItem = EMPTY_ARRAY;
      this.mapKey = EMPTY_ARRAY;
      this.mapValue = EMPTY_ARRAY;
      this.macroReference = "";
      this.functionId = "";
      this.integer = 0L;
      this.boolean_ = false;
      this.templateToken = EMPTY_ARRAY;
      this.tagReference = "";
      this.escaping = WireFormatNano.EMPTY_INT_ARRAY;
      this.containsReferences = false;
      this.unknownFieldData = null;
      this.cachedSize = -1;
      return this;
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (paramObject != this)
      {
        if (!(paramObject instanceof Value)) {
          break label36;
        }
        paramObject = (Value)paramObject;
        if (this.type == ((Value)paramObject).type) {
          break label38;
        }
        break label201;
        break label143;
        break label59;
      }
      for (;;)
      {
        label30:
        bool = false;
        label32:
        label36:
        label38:
        label59:
        label143:
        label201:
        label262:
        label282:
        label292:
        do
        {
          return bool;
          return true;
          return false;
          if (this.string != null)
          {
            if (!this.string.equals(((Value)paramObject).string)) {
              break label30;
            }
            if ((!Arrays.equals(this.listItem, ((Value)paramObject).listItem)) || (!Arrays.equals(this.mapKey, ((Value)paramObject).mapKey)) || (!Arrays.equals(this.mapValue, ((Value)paramObject).mapValue))) {
              break label30;
            }
            if (this.macroReference == null) {
              break label262;
            }
            if (!this.macroReference.equals(((Value)paramObject).macroReference)) {
              break label30;
            }
          }
          for (;;)
          {
            if (this.functionId != null)
            {
              if (!this.functionId.equals(((Value)paramObject).functionId)) {
                break label30;
              }
              if ((this.integer != ((Value)paramObject).integer) || (this.boolean_ != ((Value)paramObject).boolean_) || (!Arrays.equals(this.templateToken, ((Value)paramObject).templateToken))) {
                break label30;
              }
              if (this.tagReference == null) {
                break label282;
              }
              if (!this.tagReference.equals(((Value)paramObject).tagReference)) {
                break label30;
              }
              if ((!Arrays.equals(this.escaping, ((Value)paramObject).escaping)) || (this.containsReferences != ((Value)paramObject).containsReferences)) {
                break label30;
              }
              if (this.unknownFieldData == null) {
                break label292;
              }
              if (this.unknownFieldData.equals(((Value)paramObject).unknownFieldData)) {
                break label32;
              }
              break label30;
              if (((Value)paramObject).string == null) {
                break;
              }
              break label30;
              if (((Value)paramObject).macroReference != null) {
                break label30;
              }
            }
          }
          if (((Value)paramObject).functionId == null) {
            break;
          }
          break label30;
          if (((Value)paramObject).tagReference == null) {
            break;
          }
          break label30;
        } while (((Value)paramObject).unknownFieldData == null);
      }
    }
    
    public int getSerializedSize()
    {
      int m = 0;
      int i = CodedOutputByteBufferNano.computeInt32Size(1, this.type) + 0;
      int j;
      if (this.string.equals(""))
      {
        if (this.listItem != null) {
          break label173;
        }
        if (this.mapKey != null) {
          break label216;
        }
        if (this.mapValue != null) {
          break label259;
        }
        j = i;
        if (!this.macroReference.equals("")) {
          break label300;
        }
        i = j;
        label63:
        if (!this.functionId.equals("")) {
          break label315;
        }
        label75:
        j = i;
        if (this.integer != 0L) {
          j = i + CodedOutputByteBufferNano.computeInt64Size(8, this.integer);
        }
        if (this.containsReferences) {
          break label330;
        }
        label105:
        if (this.escaping != null) {
          break label345;
        }
        i = j;
        label114:
        if (this.templateToken != null) {
          break label408;
        }
        j = i;
        if (this.boolean_) {
          break label451;
        }
        label130:
        if (!this.tagReference.equals("")) {
          break label466;
        }
      }
      for (;;)
      {
        i = j + WireFormatNano.computeWireSize(this.unknownFieldData);
        this.cachedSize = i;
        return i;
        i += CodedOutputByteBufferNano.computeStringSize(2, this.string);
        break;
        label173:
        Object localObject = this.listItem;
        int n = localObject.length;
        int k = 0;
        for (j = i;; j = i + j)
        {
          i = j;
          if (k >= n) {
            break;
          }
          i = CodedOutputByteBufferNano.computeMessageSize(3, localObject[k]);
          k += 1;
        }
        label216:
        localObject = this.mapKey;
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
        label259:
        localObject = this.mapValue;
        n = localObject.length;
        k = 0;
        for (;;)
        {
          j = i;
          if (k >= n) {
            break;
          }
          j = CodedOutputByteBufferNano.computeMessageSize(5, localObject[k]);
          k += 1;
          i = j + i;
        }
        label300:
        i = j + CodedOutputByteBufferNano.computeStringSize(6, this.macroReference);
        break label63;
        label315:
        i += CodedOutputByteBufferNano.computeStringSize(7, this.functionId);
        break label75;
        label330:
        j += CodedOutputByteBufferNano.computeBoolSize(9, this.containsReferences);
        break label105;
        label345:
        i = j;
        if (this.escaping.length <= 0) {
          break label114;
        }
        localObject = this.escaping;
        n = localObject.length;
        i = 0;
        k = 0;
        for (;;)
        {
          if (i >= n)
          {
            i = j + k + this.escaping.length * 1;
            break;
          }
          k += CodedOutputByteBufferNano.computeInt32SizeNoTag(localObject[i]);
          i += 1;
        }
        label408:
        localObject = this.templateToken;
        n = localObject.length;
        k = m;
        for (;;)
        {
          j = i;
          if (k >= n) {
            break;
          }
          j = CodedOutputByteBufferNano.computeMessageSize(11, localObject[k]);
          k += 1;
          i = j + i;
        }
        label451:
        j += CodedOutputByteBufferNano.computeBoolSize(12, this.boolean_);
        break label130;
        label466:
        j += CodedOutputByteBufferNano.computeStringSize(13, this.tagReference);
      }
    }
    
    public int hashCode()
    {
      int i1 = 2;
      int n = 0;
      int j = this.type;
      int i;
      label55:
      label73:
      label91:
      label106:
      int k;
      label121:
      int m;
      if (this.string != null)
      {
        i = this.string.hashCode();
        i += (j + 527) * 31;
        if (this.listItem == null) {
          break label275;
        }
        j = 0;
        if (j < this.listItem.length) {
          break label283;
        }
        if (this.mapKey == null) {
          break label321;
        }
        j = 0;
        if (j < this.mapKey.length) {
          break label329;
        }
        if (this.mapValue == null) {
          break label367;
        }
        j = 0;
        if (j < this.mapValue.length) {
          break label375;
        }
        if (this.macroReference == null) {
          break label413;
        }
        j = this.macroReference.hashCode();
        if (this.functionId == null) {
          break label418;
        }
        k = this.functionId.hashCode();
        int i2 = (int)(this.integer ^ this.integer >>> 32);
        if (this.boolean_) {
          break label423;
        }
        m = 2;
        label146:
        i = m + ((k + (j + i * 31) * 31) * 31 + i2) * 31;
        if (this.templateToken == null) {
          break label429;
        }
        j = 0;
        if (j < this.templateToken.length) {
          break label437;
        }
        label188:
        if (this.tagReference == null) {
          break label475;
        }
        j = this.tagReference.hashCode();
        label203:
        i = j + i * 31;
        if (this.escaping == null) {
          break label480;
        }
        j = 0;
        label219:
        if (j < this.escaping.length) {
          break label488;
        }
        label228:
        if (this.containsReferences) {
          break label507;
        }
      }
      label275:
      label283:
      label321:
      label329:
      label367:
      label375:
      label413:
      label418:
      label423:
      label429:
      label437:
      label475:
      label480:
      label488:
      label507:
      for (j = i1;; j = 1)
      {
        k = n;
        if (this.unknownFieldData != null) {
          k = this.unknownFieldData.hashCode();
        }
        return (i * 31 + j) * 31 + k;
        i = 0;
        break;
        i *= 31;
        break label55;
        if (this.listItem[j] != null) {}
        for (k = this.listItem[j].hashCode();; k = 0)
        {
          i = k + i * 31;
          j += 1;
          break;
        }
        i *= 31;
        break label73;
        if (this.mapKey[j] != null) {}
        for (k = this.mapKey[j].hashCode();; k = 0)
        {
          i = k + i * 31;
          j += 1;
          break;
        }
        i *= 31;
        break label91;
        if (this.mapValue[j] != null) {}
        for (k = this.mapValue[j].hashCode();; k = 0)
        {
          i = k + i * 31;
          j += 1;
          break;
        }
        j = 0;
        break label106;
        k = 0;
        break label121;
        m = 1;
        break label146;
        i *= 31;
        break label188;
        if (this.templateToken[j] != null) {}
        for (k = this.templateToken[j].hashCode();; k = 0)
        {
          i = k + i * 31;
          j += 1;
          break;
        }
        j = 0;
        break label203;
        i *= 31;
        break label228;
        i = i * 31 + this.escaping[j];
        j += 1;
        break label219;
      }
    }
    
    public Value mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      for (;;)
      {
        int i = paramCodedInputByteBufferNano.readTag();
        int j;
        label266:
        Object localObject;
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
          while ((i == 2) || (i == 3) || (i == 4) || (i == 5) || (i == 6) || (i == 7) || (i == 8) || (i == 9))
          {
            this.type = i;
            break;
          }
          this.type = 1;
          break;
        case 18: 
          this.string = paramCodedInputByteBufferNano.readString();
          break;
        case 26: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 26);
          if (this.listItem != null)
          {
            i = this.listItem.length;
            localObject = new Value[j + i];
            if (this.listItem != null) {
              break label329;
            }
            this.listItem = ((Value[])localObject);
          }
          for (;;)
          {
            if (i >= this.listItem.length - 1)
            {
              this.listItem[i] = new Value();
              paramCodedInputByteBufferNano.readMessage(this.listItem[i]);
              break;
              i = 0;
              break label266;
              System.arraycopy(this.listItem, 0, localObject, 0, i);
              break label281;
            }
            this.listItem[i] = new Value();
            paramCodedInputByteBufferNano.readMessage(this.listItem[i]);
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 34: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 34);
          if (this.mapKey != null)
          {
            i = this.mapKey.length;
            localObject = new Value[j + i];
            if (this.mapKey != null) {
              break label462;
            }
            this.mapKey = ((Value[])localObject);
          }
          for (;;)
          {
            if (i >= this.mapKey.length - 1)
            {
              this.mapKey[i] = new Value();
              paramCodedInputByteBufferNano.readMessage(this.mapKey[i]);
              break;
              i = 0;
              break label399;
              System.arraycopy(this.mapKey, 0, localObject, 0, i);
              break label414;
            }
            this.mapKey[i] = new Value();
            paramCodedInputByteBufferNano.readMessage(this.mapKey[i]);
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 42: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 42);
          if (this.mapValue != null)
          {
            i = this.mapValue.length;
            localObject = new Value[j + i];
            if (this.mapValue != null) {
              break label595;
            }
            this.mapValue = ((Value[])localObject);
          }
          for (;;)
          {
            if (i >= this.mapValue.length - 1)
            {
              this.mapValue[i] = new Value();
              paramCodedInputByteBufferNano.readMessage(this.mapValue[i]);
              break;
              i = 0;
              break label532;
              System.arraycopy(this.mapValue, 0, localObject, 0, i);
              break label547;
            }
            this.mapValue[i] = new Value();
            paramCodedInputByteBufferNano.readMessage(this.mapValue[i]);
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 50: 
          this.macroReference = paramCodedInputByteBufferNano.readString();
          break;
        case 58: 
          this.functionId = paramCodedInputByteBufferNano.readString();
          break;
        case 64: 
          this.integer = paramCodedInputByteBufferNano.readInt64();
          break;
        case 72: 
          this.containsReferences = paramCodedInputByteBufferNano.readBool();
          break;
        case 80: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 80);
          i = this.escaping.length;
          localObject = new int[j + i];
          System.arraycopy(this.escaping, 0, localObject, 0, i);
          this.escaping = ((int[])localObject);
          for (;;)
          {
            if (i >= this.escaping.length - 1)
            {
              this.escaping[i] = paramCodedInputByteBufferNano.readInt32();
              break;
            }
            this.escaping[i] = paramCodedInputByteBufferNano.readInt32();
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 90: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 90);
          if (this.templateToken != null)
          {
            i = this.templateToken.length;
            localObject = new Value[j + i];
            if (this.templateToken != null) {
              break label856;
            }
            this.templateToken = ((Value[])localObject);
          }
          for (;;)
          {
            if (i >= this.templateToken.length - 1)
            {
              this.templateToken[i] = new Value();
              paramCodedInputByteBufferNano.readMessage(this.templateToken[i]);
              break;
              i = 0;
              break label793;
              System.arraycopy(this.templateToken, 0, localObject, 0, i);
              break label808;
            }
            this.templateToken[i] = new Value();
            paramCodedInputByteBufferNano.readMessage(this.templateToken[i]);
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 96: 
          this.boolean_ = paramCodedInputByteBufferNano.readBool();
          break;
        case 106: 
          label281:
          label329:
          label399:
          label414:
          label462:
          label532:
          label547:
          label595:
          label793:
          label808:
          label856:
          this.tagReference = paramCodedInputByteBufferNano.readString();
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      int j = 0;
      paramCodedOutputByteBufferNano.writeInt32(1, this.type);
      if (this.string.equals(""))
      {
        if (this.listItem != null) {
          break label148;
        }
        label30:
        if (this.mapKey != null) {
          break label183;
        }
        label37:
        if (this.mapValue != null) {
          break label218;
        }
        label44:
        if (!this.macroReference.equals("")) {
          break label253;
        }
        label56:
        if (!this.functionId.equals("")) {
          break label266;
        }
        label68:
        if (this.integer != 0L) {
          paramCodedOutputByteBufferNano.writeInt64(8, this.integer);
        }
        if (this.containsReferences) {
          break label279;
        }
        label94:
        if (this.escaping != null) {
          break label292;
        }
        label101:
        if (this.templateToken != null) {
          break label336;
        }
        label108:
        if (this.boolean_) {
          break label372;
        }
        label115:
        if (!this.tagReference.equals("")) {
          break label385;
        }
      }
      for (;;)
      {
        WireFormatNano.writeUnknownFields(this.unknownFieldData, paramCodedOutputByteBufferNano);
        return;
        paramCodedOutputByteBufferNano.writeString(2, this.string);
        break;
        label148:
        Object localObject = this.listItem;
        int k = localObject.length;
        int i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeMessage(3, localObject[i]);
          i += 1;
        }
        break label30;
        label183:
        localObject = this.mapKey;
        k = localObject.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeMessage(4, localObject[i]);
          i += 1;
        }
        break label37;
        label218:
        localObject = this.mapValue;
        k = localObject.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeMessage(5, localObject[i]);
          i += 1;
        }
        break label44;
        label253:
        paramCodedOutputByteBufferNano.writeString(6, this.macroReference);
        break label56;
        label266:
        paramCodedOutputByteBufferNano.writeString(7, this.functionId);
        break label68;
        label279:
        paramCodedOutputByteBufferNano.writeBool(9, this.containsReferences);
        break label94;
        label292:
        if (this.escaping.length <= 0) {
          break label101;
        }
        localObject = this.escaping;
        k = localObject.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeInt32(10, localObject[i]);
          i += 1;
        }
        break label101;
        label336:
        localObject = this.templateToken;
        k = localObject.length;
        i = j;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeMessage(11, localObject[i]);
          i += 1;
        }
        break label108;
        label372:
        paramCodedOutputByteBufferNano.writeBool(12, this.boolean_);
        break label115;
        label385:
        paramCodedOutputByteBufferNano.writeString(13, this.tagReference);
      }
    }
    
    public static abstract interface Escaping
    {
      public static final int CONVERT_JS_VALUE_TO_EXPRESSION = 16;
      public static final int ESCAPE_CSS_STRING = 10;
      public static final int ESCAPE_HTML = 1;
      public static final int ESCAPE_HTML_ATTRIBUTE = 3;
      public static final int ESCAPE_HTML_ATTRIBUTE_NOSPACE = 4;
      public static final int ESCAPE_HTML_RCDATA = 2;
      public static final int ESCAPE_JS_REGEX = 9;
      public static final int ESCAPE_JS_STRING = 7;
      public static final int ESCAPE_JS_VALUE = 8;
      public static final int ESCAPE_URI = 12;
      public static final int FILTER_CSS_VALUE = 11;
      public static final int FILTER_HTML_ATTRIBUTES = 6;
      public static final int FILTER_HTML_ELEMENT_NAME = 5;
      public static final int FILTER_NORMALIZE_URI = 14;
      public static final int NORMALIZE_URI = 13;
      public static final int NO_AUTOESCAPE = 15;
      public static final int TEXT = 17;
    }
    
    public static abstract interface Type
    {
      public static final int BOOLEAN = 8;
      public static final int FUNCTION_ID = 5;
      public static final int INTEGER = 6;
      public static final int LIST = 2;
      public static final int MACRO_REFERENCE = 4;
      public static final int MAP = 3;
      public static final int STRING = 1;
      public static final int TAG_REFERENCE = 9;
      public static final int TEMPLATE = 7;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\analytics\midtier\proto\containertag\TypeSystem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */