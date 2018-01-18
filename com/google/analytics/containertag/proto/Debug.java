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

public abstract interface Debug
{
  public static final class DataLayerEventEvaluationInfo
    extends ExtendableMessageNano
  {
    public static final DataLayerEventEvaluationInfo[] EMPTY_ARRAY = new DataLayerEventEvaluationInfo[0];
    public Debug.ResolvedFunctionCall[] results = Debug.ResolvedFunctionCall.EMPTY_ARRAY;
    public Debug.RuleEvaluationStepInfo rulesEvaluation = null;
    
    public static DataLayerEventEvaluationInfo parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new DataLayerEventEvaluationInfo().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static DataLayerEventEvaluationInfo parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (DataLayerEventEvaluationInfo)MessageNano.mergeFrom(new DataLayerEventEvaluationInfo(), paramArrayOfByte);
    }
    
    public final DataLayerEventEvaluationInfo clear()
    {
      this.rulesEvaluation = null;
      this.results = Debug.ResolvedFunctionCall.EMPTY_ARRAY;
      this.unknownFieldData = null;
      this.cachedSize = -1;
      return this;
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (paramObject != this)
      {
        if (!(paramObject instanceof DataLayerEventEvaluationInfo)) {
          break label46;
        }
        paramObject = (DataLayerEventEvaluationInfo)paramObject;
        if (this.rulesEvaluation == null) {
          break label48;
        }
        if (this.rulesEvaluation.equals(((DataLayerEventEvaluationInfo)paramObject).rulesEvaluation)) {
          break label55;
        }
      }
      for (;;)
      {
        bool = false;
        label46:
        label48:
        label55:
        label95:
        do
        {
          do
          {
            return bool;
            return true;
            return false;
            if (((DataLayerEventEvaluationInfo)paramObject).rulesEvaluation != null) {
              break;
            }
            if (!Arrays.equals(this.results, ((DataLayerEventEvaluationInfo)paramObject).results)) {
              break;
            }
            if (this.unknownFieldData == null) {
              break label95;
            }
          } while (this.unknownFieldData.equals(((DataLayerEventEvaluationInfo)paramObject).unknownFieldData));
          break;
        } while (((DataLayerEventEvaluationInfo)paramObject).unknownFieldData == null);
      }
    }
    
    public int getSerializedSize()
    {
      int j = 0;
      if (this.rulesEvaluation == null) {}
      int k;
      for (int i = 0; this.results == null; i = CodedOutputByteBufferNano.computeMessageSize(1, this.rulesEvaluation) + 0)
      {
        k = i;
        i = k + WireFormatNano.computeWireSize(this.unknownFieldData);
        this.cachedSize = i;
        return i;
      }
      Debug.ResolvedFunctionCall[] arrayOfResolvedFunctionCall = this.results;
      int m = arrayOfResolvedFunctionCall.length;
      for (;;)
      {
        k = i;
        if (j >= m) {
          break;
        }
        i += CodedOutputByteBufferNano.computeMessageSize(2, arrayOfResolvedFunctionCall[j]);
        j += 1;
      }
    }
    
    public int hashCode()
    {
      int m = 0;
      int i;
      int j;
      if (this.rulesEvaluation != null)
      {
        i = this.rulesEvaluation.hashCode();
        i += 527;
        if (this.results == null) {
          break label74;
        }
        j = 0;
        if (j < this.results.length) {
          break label82;
        }
      }
      for (;;)
      {
        j = m;
        if (this.unknownFieldData != null) {
          j = this.unknownFieldData.hashCode();
        }
        return i * 31 + j;
        i = 0;
        break;
        label74:
        i *= 31;
      }
      label82:
      if (this.results[j] != null) {}
      for (int k = this.results[j].hashCode();; k = 0)
      {
        i = k + i * 31;
        j += 1;
        break;
      }
    }
    
    public DataLayerEventEvaluationInfo mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      int i;
      for (;;)
      {
        i = paramCodedInputByteBufferNano.readTag();
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
          this.rulesEvaluation = new Debug.RuleEvaluationStepInfo();
          paramCodedInputByteBufferNano.readMessage(this.rulesEvaluation);
        }
      }
      int j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 18);
      label119:
      Debug.ResolvedFunctionCall[] arrayOfResolvedFunctionCall;
      if (this.results != null)
      {
        i = this.results.length;
        arrayOfResolvedFunctionCall = new Debug.ResolvedFunctionCall[j + i];
        if (this.results != null) {
          break label182;
        }
        label134:
        this.results = arrayOfResolvedFunctionCall;
      }
      for (;;)
      {
        if (i >= this.results.length - 1)
        {
          this.results[i] = new Debug.ResolvedFunctionCall();
          paramCodedInputByteBufferNano.readMessage(this.results[i]);
          break;
          i = 0;
          break label119;
          label182:
          System.arraycopy(this.results, 0, arrayOfResolvedFunctionCall, 0, i);
          break label134;
        }
        this.results[i] = new Debug.ResolvedFunctionCall();
        paramCodedInputByteBufferNano.readMessage(this.results[i]);
        paramCodedInputByteBufferNano.readTag();
        i += 1;
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if (this.rulesEvaluation == null) {
        if (this.results != null) {
          break label35;
        }
      }
      for (;;)
      {
        WireFormatNano.writeUnknownFields(this.unknownFieldData, paramCodedOutputByteBufferNano);
        return;
        paramCodedOutputByteBufferNano.writeMessage(1, this.rulesEvaluation);
        break;
        label35:
        Debug.ResolvedFunctionCall[] arrayOfResolvedFunctionCall = this.results;
        int j = arrayOfResolvedFunctionCall.length;
        int i = 0;
        while (i < j)
        {
          paramCodedOutputByteBufferNano.writeMessage(2, arrayOfResolvedFunctionCall[i]);
          i += 1;
        }
      }
    }
  }
  
  public static final class DebugEvents
    extends ExtendableMessageNano
  {
    public static final DebugEvents[] EMPTY_ARRAY = new DebugEvents[0];
    public Debug.EventInfo[] event = Debug.EventInfo.EMPTY_ARRAY;
    
    public static DebugEvents parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new DebugEvents().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static DebugEvents parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (DebugEvents)MessageNano.mergeFrom(new DebugEvents(), paramArrayOfByte);
    }
    
    public final DebugEvents clear()
    {
      this.event = Debug.EventInfo.EMPTY_ARRAY;
      this.unknownFieldData = null;
      this.cachedSize = -1;
      return this;
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (paramObject != this)
      {
        if (!(paramObject instanceof DebugEvents)) {
          break label39;
        }
        paramObject = (DebugEvents)paramObject;
        if (Arrays.equals(this.event, ((DebugEvents)paramObject).event)) {
          break label41;
        }
      }
      for (;;)
      {
        bool = false;
        label39:
        label41:
        do
        {
          do
          {
            return bool;
            return true;
            return false;
            if (this.unknownFieldData == null) {
              break;
            }
          } while (this.unknownFieldData.equals(((DebugEvents)paramObject).unknownFieldData));
          break;
        } while (((DebugEvents)paramObject).unknownFieldData == null);
      }
    }
    
    public int getSerializedSize()
    {
      int i = 0;
      int k = 0;
      if (this.event == null)
      {
        i = k + WireFormatNano.computeWireSize(this.unknownFieldData);
        this.cachedSize = i;
        return i;
      }
      Debug.EventInfo[] arrayOfEventInfo = this.event;
      int m = arrayOfEventInfo.length;
      int j = 0;
      for (;;)
      {
        k = i;
        if (j >= m) {
          break;
        }
        k = CodedOutputByteBufferNano.computeMessageSize(1, arrayOfEventInfo[j]);
        j += 1;
        i = k + i;
      }
    }
    
    public int hashCode()
    {
      int m = 0;
      int i;
      int j;
      if (this.event != null)
      {
        i = 17;
        j = 0;
        if (j < this.event.length) {}
      }
      else
      {
        for (;;)
        {
          j = m;
          if (this.unknownFieldData != null) {
            j = this.unknownFieldData.hashCode();
          }
          return i * 31 + j;
          i = 527;
        }
      }
      if (this.event[j] != null) {}
      for (int k = this.event[j].hashCode();; k = 0)
      {
        i = k + i * 31;
        j += 1;
        break;
      }
    }
    
    public DebugEvents mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
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
      }
      int j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 10);
      label89:
      Debug.EventInfo[] arrayOfEventInfo;
      if (this.event != null)
      {
        i = this.event.length;
        arrayOfEventInfo = new Debug.EventInfo[j + i];
        if (this.event != null) {
          break label152;
        }
        label104:
        this.event = arrayOfEventInfo;
      }
      for (;;)
      {
        if (i >= this.event.length - 1)
        {
          this.event[i] = new Debug.EventInfo();
          paramCodedInputByteBufferNano.readMessage(this.event[i]);
          break;
          i = 0;
          break label89;
          label152:
          System.arraycopy(this.event, 0, arrayOfEventInfo, 0, i);
          break label104;
        }
        this.event[i] = new Debug.EventInfo();
        paramCodedInputByteBufferNano.readMessage(this.event[i]);
        paramCodedInputByteBufferNano.readTag();
        i += 1;
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if (this.event == null) {}
      for (;;)
      {
        WireFormatNano.writeUnknownFields(this.unknownFieldData, paramCodedOutputByteBufferNano);
        return;
        Debug.EventInfo[] arrayOfEventInfo = this.event;
        int j = arrayOfEventInfo.length;
        int i = 0;
        while (i < j)
        {
          paramCodedOutputByteBufferNano.writeMessage(1, arrayOfEventInfo[i]);
          i += 1;
        }
      }
    }
  }
  
  public static final class EventInfo
    extends ExtendableMessageNano
  {
    public static final EventInfo[] EMPTY_ARRAY = new EventInfo[0];
    public String containerId = "";
    public String containerVersion = "";
    public Debug.DataLayerEventEvaluationInfo dataLayerEventResult = null;
    public int eventType = 1;
    public String key = "";
    public Debug.MacroEvaluationInfo macroResult = null;
    
    public static EventInfo parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new EventInfo().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static EventInfo parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (EventInfo)MessageNano.mergeFrom(new EventInfo(), paramArrayOfByte);
    }
    
    public final EventInfo clear()
    {
      this.eventType = 1;
      this.containerVersion = "";
      this.containerId = "";
      this.key = "";
      this.macroResult = null;
      this.dataLayerEventResult = null;
      this.unknownFieldData = null;
      this.cachedSize = -1;
      return this;
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (paramObject != this)
      {
        if (!(paramObject instanceof EventInfo)) {
          break label36;
        }
        paramObject = (EventInfo)paramObject;
        if (this.eventType == ((EventInfo)paramObject).eventType) {
          break label38;
        }
      }
      for (;;)
      {
        bool = false;
        label32:
        label36:
        label38:
        label59:
        label80:
        label101:
        label122:
        label179:
        label189:
        label199:
        label209:
        do
        {
          return bool;
          return true;
          return false;
          if (this.containerVersion != null)
          {
            if (!this.containerVersion.equals(((EventInfo)paramObject).containerVersion)) {
              break;
            }
            if (this.containerId == null) {
              break label179;
            }
            if (!this.containerId.equals(((EventInfo)paramObject).containerId)) {
              break;
            }
            if (this.key == null) {
              break label189;
            }
            if (!this.key.equals(((EventInfo)paramObject).key)) {
              break;
            }
            if (this.macroResult == null) {
              break label199;
            }
            if (!this.macroResult.equals(((EventInfo)paramObject).macroResult)) {
              break;
            }
            if (this.dataLayerEventResult == null) {
              break label209;
            }
            if (!this.dataLayerEventResult.equals(((EventInfo)paramObject).dataLayerEventResult)) {
              break;
            }
          }
          for (;;)
          {
            if (this.unknownFieldData != null)
            {
              if (this.unknownFieldData.equals(((EventInfo)paramObject).unknownFieldData)) {
                break label32;
              }
              break;
              if (((EventInfo)paramObject).containerVersion == null) {
                break label59;
              }
              break;
              if (((EventInfo)paramObject).containerId == null) {
                break label80;
              }
              break;
              if (((EventInfo)paramObject).key == null) {
                break label101;
              }
              break;
              if (((EventInfo)paramObject).macroResult == null) {
                break label122;
              }
              break;
              if (((EventInfo)paramObject).dataLayerEventResult != null) {
                break;
              }
            }
          }
        } while (((EventInfo)paramObject).unknownFieldData == null);
      }
    }
    
    public int getSerializedSize()
    {
      int i = 0;
      if (this.eventType == 1)
      {
        if (!this.containerVersion.equals("")) {
          break label91;
        }
        label22:
        if (!this.containerId.equals("")) {
          break label105;
        }
        label34:
        if (!this.key.equals("")) {
          break label119;
        }
        label46:
        if (this.macroResult != null) {
          break label133;
        }
        label53:
        if (this.dataLayerEventResult != null) {
          break label148;
        }
      }
      for (;;)
      {
        i += WireFormatNano.computeWireSize(this.unknownFieldData);
        this.cachedSize = i;
        return i;
        i = CodedOutputByteBufferNano.computeInt32Size(1, this.eventType) + 0;
        break;
        label91:
        i += CodedOutputByteBufferNano.computeStringSize(2, this.containerVersion);
        break label22;
        label105:
        i += CodedOutputByteBufferNano.computeStringSize(3, this.containerId);
        break label34;
        label119:
        i += CodedOutputByteBufferNano.computeStringSize(4, this.key);
        break label46;
        label133:
        i += CodedOutputByteBufferNano.computeMessageSize(6, this.macroResult);
        break label53;
        label148:
        i += CodedOutputByteBufferNano.computeMessageSize(7, this.dataLayerEventResult);
      }
    }
    
    public int hashCode()
    {
      int i1 = 0;
      int i2 = this.eventType;
      int i;
      int j;
      label39:
      int k;
      label54:
      int m;
      if (this.containerVersion != null)
      {
        i = this.containerVersion.hashCode();
        if (this.containerId == null) {
          break label149;
        }
        j = this.containerId.hashCode();
        if (this.key == null) {
          break label154;
        }
        k = this.key.hashCode();
        if (this.macroResult == null) {
          break label159;
        }
        m = this.macroResult.hashCode();
        label70:
        if (this.dataLayerEventResult == null) {
          break label165;
        }
      }
      label149:
      label154:
      label159:
      label165:
      for (int n = this.dataLayerEventResult.hashCode();; n = 0)
      {
        if (this.unknownFieldData != null) {
          i1 = this.unknownFieldData.hashCode();
        }
        return (n + (m + (k + (j + (i + (i2 + 527) * 31) * 31) * 31) * 31) * 31) * 31 + i1;
        i = 0;
        break;
        j = 0;
        break label39;
        k = 0;
        break label54;
        m = 0;
        break label70;
      }
    }
    
    public EventInfo mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
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
          while (i == 2)
          {
            this.eventType = i;
            break;
          }
          this.eventType = 1;
          break;
        case 18: 
          this.containerVersion = paramCodedInputByteBufferNano.readString();
          break;
        case 26: 
          this.containerId = paramCodedInputByteBufferNano.readString();
          break;
        case 34: 
          this.key = paramCodedInputByteBufferNano.readString();
          break;
        case 50: 
          this.macroResult = new Debug.MacroEvaluationInfo();
          paramCodedInputByteBufferNano.readMessage(this.macroResult);
          break;
        case 58: 
          this.dataLayerEventResult = new Debug.DataLayerEventEvaluationInfo();
          paramCodedInputByteBufferNano.readMessage(this.dataLayerEventResult);
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if (this.eventType == 1)
      {
        if (!this.containerVersion.equals("")) {
          break label79;
        }
        label20:
        if (!this.containerId.equals("")) {
          break label91;
        }
        label32:
        if (!this.key.equals("")) {
          break label103;
        }
        label44:
        if (this.macroResult != null) {
          break label115;
        }
        label51:
        if (this.dataLayerEventResult != null) {
          break label128;
        }
      }
      for (;;)
      {
        WireFormatNano.writeUnknownFields(this.unknownFieldData, paramCodedOutputByteBufferNano);
        return;
        paramCodedOutputByteBufferNano.writeInt32(1, this.eventType);
        break;
        label79:
        paramCodedOutputByteBufferNano.writeString(2, this.containerVersion);
        break label20;
        label91:
        paramCodedOutputByteBufferNano.writeString(3, this.containerId);
        break label32;
        label103:
        paramCodedOutputByteBufferNano.writeString(4, this.key);
        break label44;
        label115:
        paramCodedOutputByteBufferNano.writeMessage(6, this.macroResult);
        break label51;
        label128:
        paramCodedOutputByteBufferNano.writeMessage(7, this.dataLayerEventResult);
      }
    }
    
    public static abstract interface EventType
    {
      public static final int DATA_LAYER_EVENT = 1;
      public static final int MACRO_REFERENCE = 2;
    }
  }
  
  public static final class MacroEvaluationInfo
    extends ExtendableMessageNano
  {
    public static final MacroEvaluationInfo[] EMPTY_ARRAY = new MacroEvaluationInfo[0];
    public static final Extension<MacroEvaluationInfo> macro = Extension.create(47497405, new Extension.TypeLiteral() {});
    public Debug.ResolvedFunctionCall result = null;
    public Debug.RuleEvaluationStepInfo rulesEvaluation = null;
    
    public static MacroEvaluationInfo parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new MacroEvaluationInfo().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static MacroEvaluationInfo parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (MacroEvaluationInfo)MessageNano.mergeFrom(new MacroEvaluationInfo(), paramArrayOfByte);
    }
    
    public final MacroEvaluationInfo clear()
    {
      this.rulesEvaluation = null;
      this.result = null;
      this.unknownFieldData = null;
      this.cachedSize = -1;
      return this;
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (paramObject != this)
      {
        if (!(paramObject instanceof MacroEvaluationInfo)) {
          break label46;
        }
        paramObject = (MacroEvaluationInfo)paramObject;
        if (this.rulesEvaluation == null) {
          break label48;
        }
        if (this.rulesEvaluation.equals(((MacroEvaluationInfo)paramObject).rulesEvaluation)) {
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
        do
        {
          return bool;
          return true;
          return false;
          if (((MacroEvaluationInfo)paramObject).rulesEvaluation != null) {
            break;
          }
          if (this.result != null) {
            if (!this.result.equals(((MacroEvaluationInfo)paramObject).result)) {
              break;
            }
          }
          for (;;)
          {
            if (this.unknownFieldData != null)
            {
              if (this.unknownFieldData.equals(((MacroEvaluationInfo)paramObject).unknownFieldData)) {
                break label42;
              }
              break;
              if (((MacroEvaluationInfo)paramObject).result != null) {
                break;
              }
            }
          }
        } while (((MacroEvaluationInfo)paramObject).unknownFieldData == null);
      }
    }
    
    public int getSerializedSize()
    {
      int i = 0;
      if (this.rulesEvaluation == null) {
        if (this.result != null) {
          break label47;
        }
      }
      for (;;)
      {
        i += WireFormatNano.computeWireSize(this.unknownFieldData);
        this.cachedSize = i;
        return i;
        i = CodedOutputByteBufferNano.computeMessageSize(1, this.rulesEvaluation) + 0;
        break;
        label47:
        i += CodedOutputByteBufferNano.computeMessageSize(3, this.result);
      }
    }
    
    public int hashCode()
    {
      int k = 0;
      int i;
      if (this.rulesEvaluation != null)
      {
        i = this.rulesEvaluation.hashCode();
        if (this.result == null) {
          break label70;
        }
      }
      label70:
      for (int j = this.result.hashCode();; j = 0)
      {
        if (this.unknownFieldData != null) {
          k = this.unknownFieldData.hashCode();
        }
        return (j + (i + 527) * 31) * 31 + k;
        i = 0;
        break;
      }
    }
    
    public MacroEvaluationInfo mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
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
          this.rulesEvaluation = new Debug.RuleEvaluationStepInfo();
          paramCodedInputByteBufferNano.readMessage(this.rulesEvaluation);
          break;
        case 26: 
          this.result = new Debug.ResolvedFunctionCall();
          paramCodedInputByteBufferNano.readMessage(this.result);
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if (this.rulesEvaluation == null) {
        if (this.result != null) {
          break label35;
        }
      }
      for (;;)
      {
        WireFormatNano.writeUnknownFields(this.unknownFieldData, paramCodedOutputByteBufferNano);
        return;
        paramCodedOutputByteBufferNano.writeMessage(1, this.rulesEvaluation);
        break;
        label35:
        paramCodedOutputByteBufferNano.writeMessage(3, this.result);
      }
    }
  }
  
  public static final class ResolvedFunctionCall
    extends ExtendableMessageNano
  {
    public static final ResolvedFunctionCall[] EMPTY_ARRAY = new ResolvedFunctionCall[0];
    public String associatedRuleName = "";
    public Debug.ResolvedProperty[] properties = Debug.ResolvedProperty.EMPTY_ARRAY;
    public TypeSystem.Value result = null;
    
    public static ResolvedFunctionCall parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new ResolvedFunctionCall().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static ResolvedFunctionCall parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (ResolvedFunctionCall)MessageNano.mergeFrom(new ResolvedFunctionCall(), paramArrayOfByte);
    }
    
    public final ResolvedFunctionCall clear()
    {
      this.properties = Debug.ResolvedProperty.EMPTY_ARRAY;
      this.result = null;
      this.associatedRuleName = "";
      this.unknownFieldData = null;
      this.cachedSize = -1;
      return this;
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (paramObject != this)
      {
        if (!(paramObject instanceof ResolvedFunctionCall)) {
          break label39;
        }
        paramObject = (ResolvedFunctionCall)paramObject;
        if (Arrays.equals(this.properties, ((ResolvedFunctionCall)paramObject).properties)) {
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
          if (this.result != null)
          {
            if (!this.result.equals(((ResolvedFunctionCall)paramObject).result)) {
              break;
            }
            if (this.associatedRuleName == null) {
              break label119;
            }
            if (!this.associatedRuleName.equals(((ResolvedFunctionCall)paramObject).associatedRuleName)) {
              break;
            }
          }
          for (;;)
          {
            if (this.unknownFieldData != null)
            {
              if (this.unknownFieldData.equals(((ResolvedFunctionCall)paramObject).unknownFieldData)) {
                break label35;
              }
              break;
              if (((ResolvedFunctionCall)paramObject).result == null) {
                break label62;
              }
              break;
              if (((ResolvedFunctionCall)paramObject).associatedRuleName != null) {
                break;
              }
            }
          }
        } while (((ResolvedFunctionCall)paramObject).unknownFieldData == null);
      }
    }
    
    public int getSerializedSize()
    {
      int i = 0;
      int j = 0;
      if (this.properties == null)
      {
        if (this.result != null) {
          break label88;
        }
        label18:
        if (!this.associatedRuleName.equals("")) {
          break label102;
        }
      }
      for (;;)
      {
        i = j + WireFormatNano.computeWireSize(this.unknownFieldData);
        this.cachedSize = i;
        return i;
        Debug.ResolvedProperty[] arrayOfResolvedProperty = this.properties;
        int m = arrayOfResolvedProperty.length;
        int k = 0;
        for (;;)
        {
          j = i;
          if (k >= m) {
            break;
          }
          j = CodedOutputByteBufferNano.computeMessageSize(1, arrayOfResolvedProperty[k]);
          k += 1;
          i = j + i;
        }
        label88:
        j += CodedOutputByteBufferNano.computeMessageSize(2, this.result);
        break label18;
        label102:
        j += CodedOutputByteBufferNano.computeStringSize(3, this.associatedRuleName);
      }
    }
    
    public int hashCode()
    {
      int m = 0;
      int i;
      int j;
      if (this.properties != null)
      {
        i = 17;
        j = 0;
        if (j < this.properties.length) {
          break label97;
        }
        label24:
        if (this.result == null) {
          break label135;
        }
        j = this.result.hashCode();
        label39:
        if (this.associatedRuleName == null) {
          break label140;
        }
      }
      label97:
      label135:
      label140:
      for (int k = this.associatedRuleName.hashCode();; k = 0)
      {
        if (this.unknownFieldData != null) {
          m = this.unknownFieldData.hashCode();
        }
        return (k + (j + i * 31) * 31) * 31 + m;
        i = 527;
        break label24;
        if (this.properties[j] != null) {}
        for (k = this.properties[j].hashCode();; k = 0)
        {
          i = k + i * 31;
          j += 1;
          break;
        }
        j = 0;
        break label39;
      }
    }
    
    public ResolvedFunctionCall mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
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
          Debug.ResolvedProperty[] arrayOfResolvedProperty;
          if (this.properties != null)
          {
            i = this.properties.length;
            arrayOfResolvedProperty = new Debug.ResolvedProperty[j + i];
            if (this.properties != null) {
              break label168;
            }
            this.properties = arrayOfResolvedProperty;
          }
          for (;;)
          {
            if (i >= this.properties.length - 1)
            {
              this.properties[i] = new Debug.ResolvedProperty();
              paramCodedInputByteBufferNano.readMessage(this.properties[i]);
              break;
              i = 0;
              break label105;
              System.arraycopy(this.properties, 0, arrayOfResolvedProperty, 0, i);
              break label120;
            }
            this.properties[i] = new Debug.ResolvedProperty();
            paramCodedInputByteBufferNano.readMessage(this.properties[i]);
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 18: 
          this.result = new TypeSystem.Value();
          paramCodedInputByteBufferNano.readMessage(this.result);
          break;
        case 26: 
          label105:
          label120:
          label168:
          this.associatedRuleName = paramCodedInputByteBufferNano.readString();
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      int i = 0;
      if (this.properties == null)
      {
        if (this.result != null) {
          break label68;
        }
        label16:
        if (!this.associatedRuleName.equals("")) {
          break label80;
        }
      }
      for (;;)
      {
        WireFormatNano.writeUnknownFields(this.unknownFieldData, paramCodedOutputByteBufferNano);
        return;
        Debug.ResolvedProperty[] arrayOfResolvedProperty = this.properties;
        int j = arrayOfResolvedProperty.length;
        while (i < j)
        {
          paramCodedOutputByteBufferNano.writeMessage(1, arrayOfResolvedProperty[i]);
          i += 1;
        }
        break;
        label68:
        paramCodedOutputByteBufferNano.writeMessage(2, this.result);
        break label16;
        label80:
        paramCodedOutputByteBufferNano.writeString(3, this.associatedRuleName);
      }
    }
  }
  
  public static final class ResolvedProperty
    extends ExtendableMessageNano
  {
    public static final ResolvedProperty[] EMPTY_ARRAY = new ResolvedProperty[0];
    public String key = "";
    public TypeSystem.Value value = null;
    
    public static ResolvedProperty parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new ResolvedProperty().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static ResolvedProperty parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (ResolvedProperty)MessageNano.mergeFrom(new ResolvedProperty(), paramArrayOfByte);
    }
    
    public final ResolvedProperty clear()
    {
      this.key = "";
      this.value = null;
      this.unknownFieldData = null;
      this.cachedSize = -1;
      return this;
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (paramObject != this)
      {
        if (!(paramObject instanceof ResolvedProperty)) {
          break label46;
        }
        paramObject = (ResolvedProperty)paramObject;
        if (this.key == null) {
          break label48;
        }
        if (this.key.equals(((ResolvedProperty)paramObject).key)) {
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
        do
        {
          return bool;
          return true;
          return false;
          if (((ResolvedProperty)paramObject).key != null) {
            break;
          }
          if (this.value != null) {
            if (!this.value.equals(((ResolvedProperty)paramObject).value)) {
              break;
            }
          }
          for (;;)
          {
            if (this.unknownFieldData != null)
            {
              if (this.unknownFieldData.equals(((ResolvedProperty)paramObject).unknownFieldData)) {
                break label42;
              }
              break;
              if (((ResolvedProperty)paramObject).value != null) {
                break;
              }
            }
          }
        } while (((ResolvedProperty)paramObject).unknownFieldData == null);
      }
    }
    
    public int getSerializedSize()
    {
      int i = 0;
      if (this.key.equals("")) {
        if (this.value != null) {
          break label52;
        }
      }
      for (;;)
      {
        i += WireFormatNano.computeWireSize(this.unknownFieldData);
        this.cachedSize = i;
        return i;
        i = CodedOutputByteBufferNano.computeStringSize(1, this.key) + 0;
        break;
        label52:
        i += CodedOutputByteBufferNano.computeMessageSize(2, this.value);
      }
    }
    
    public int hashCode()
    {
      int k = 0;
      int i;
      if (this.key != null)
      {
        i = this.key.hashCode();
        if (this.value == null) {
          break label70;
        }
      }
      label70:
      for (int j = this.value.hashCode();; j = 0)
      {
        if (this.unknownFieldData != null) {
          k = this.unknownFieldData.hashCode();
        }
        return (j + (i + 527) * 31) * 31 + k;
        i = 0;
        break;
      }
    }
    
    public ResolvedProperty mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
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
        case 18: 
          this.value = new TypeSystem.Value();
          paramCodedInputByteBufferNano.readMessage(this.value);
        }
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      if (this.key.equals("")) {
        if (this.value != null) {
          break label40;
        }
      }
      for (;;)
      {
        WireFormatNano.writeUnknownFields(this.unknownFieldData, paramCodedOutputByteBufferNano);
        return;
        paramCodedOutputByteBufferNano.writeString(1, this.key);
        break;
        label40:
        paramCodedOutputByteBufferNano.writeMessage(2, this.value);
      }
    }
  }
  
  public static final class ResolvedRule
    extends ExtendableMessageNano
  {
    public static final ResolvedRule[] EMPTY_ARRAY = new ResolvedRule[0];
    public Debug.ResolvedFunctionCall[] addMacros = Debug.ResolvedFunctionCall.EMPTY_ARRAY;
    public Debug.ResolvedFunctionCall[] addTags = Debug.ResolvedFunctionCall.EMPTY_ARRAY;
    public Debug.ResolvedFunctionCall[] negativePredicates = Debug.ResolvedFunctionCall.EMPTY_ARRAY;
    public Debug.ResolvedFunctionCall[] positivePredicates = Debug.ResolvedFunctionCall.EMPTY_ARRAY;
    public Debug.ResolvedFunctionCall[] removeMacros = Debug.ResolvedFunctionCall.EMPTY_ARRAY;
    public Debug.ResolvedFunctionCall[] removeTags = Debug.ResolvedFunctionCall.EMPTY_ARRAY;
    public TypeSystem.Value result = null;
    
    public static ResolvedRule parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new ResolvedRule().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static ResolvedRule parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (ResolvedRule)MessageNano.mergeFrom(new ResolvedRule(), paramArrayOfByte);
    }
    
    public final ResolvedRule clear()
    {
      this.positivePredicates = Debug.ResolvedFunctionCall.EMPTY_ARRAY;
      this.negativePredicates = Debug.ResolvedFunctionCall.EMPTY_ARRAY;
      this.addTags = Debug.ResolvedFunctionCall.EMPTY_ARRAY;
      this.removeTags = Debug.ResolvedFunctionCall.EMPTY_ARRAY;
      this.addMacros = Debug.ResolvedFunctionCall.EMPTY_ARRAY;
      this.removeMacros = Debug.ResolvedFunctionCall.EMPTY_ARRAY;
      this.result = null;
      this.unknownFieldData = null;
      this.cachedSize = -1;
      return this;
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (paramObject != this)
      {
        if (!(paramObject instanceof ResolvedRule)) {
          break label39;
        }
        paramObject = (ResolvedRule)paramObject;
        if (Arrays.equals(this.positivePredicates, ((ResolvedRule)paramObject).positivePredicates)) {
          break label41;
        }
      }
      for (;;)
      {
        bool = false;
        label35:
        label39:
        label41:
        do
        {
          return bool;
          return true;
          return false;
          if ((!Arrays.equals(this.negativePredicates, ((ResolvedRule)paramObject).negativePredicates)) || (!Arrays.equals(this.addTags, ((ResolvedRule)paramObject).addTags)) || (!Arrays.equals(this.removeTags, ((ResolvedRule)paramObject).removeTags)) || (!Arrays.equals(this.addMacros, ((ResolvedRule)paramObject).addMacros)) || (!Arrays.equals(this.removeMacros, ((ResolvedRule)paramObject).removeMacros))) {
            break;
          }
          if (this.result != null) {
            if (!this.result.equals(((ResolvedRule)paramObject).result)) {
              break;
            }
          }
          for (;;)
          {
            if (this.unknownFieldData != null)
            {
              if (this.unknownFieldData.equals(((ResolvedRule)paramObject).unknownFieldData)) {
                break label35;
              }
              break;
              if (((ResolvedRule)paramObject).result != null) {
                break;
              }
            }
          }
        } while (((ResolvedRule)paramObject).unknownFieldData == null);
      }
    }
    
    public int getSerializedSize()
    {
      int m = 0;
      int i;
      int j;
      if (this.positivePredicates == null)
      {
        i = 0;
        if (this.negativePredicates != null) {
          break label116;
        }
        if (this.addTags != null) {
          break label159;
        }
        if (this.removeTags != null) {
          break label202;
        }
        if (this.addMacros != null) {
          break label245;
        }
        if (this.removeMacros != null) {
          break label288;
        }
        j = i;
        if (this.result != null) {
          break label329;
        }
      }
      for (;;)
      {
        i = j + WireFormatNano.computeWireSize(this.unknownFieldData);
        this.cachedSize = i;
        return i;
        Debug.ResolvedFunctionCall[] arrayOfResolvedFunctionCall = this.positivePredicates;
        int n = arrayOfResolvedFunctionCall.length;
        int k = 0;
        for (j = 0;; j = i + j)
        {
          i = j;
          if (k >= n) {
            break;
          }
          i = CodedOutputByteBufferNano.computeMessageSize(1, arrayOfResolvedFunctionCall[k]);
          k += 1;
        }
        label116:
        arrayOfResolvedFunctionCall = this.negativePredicates;
        n = arrayOfResolvedFunctionCall.length;
        k = 0;
        for (j = i;; j = i + j)
        {
          i = j;
          if (k >= n) {
            break;
          }
          i = CodedOutputByteBufferNano.computeMessageSize(2, arrayOfResolvedFunctionCall[k]);
          k += 1;
        }
        label159:
        arrayOfResolvedFunctionCall = this.addTags;
        n = arrayOfResolvedFunctionCall.length;
        k = 0;
        for (j = i;; j = i + j)
        {
          i = j;
          if (k >= n) {
            break;
          }
          i = CodedOutputByteBufferNano.computeMessageSize(3, arrayOfResolvedFunctionCall[k]);
          k += 1;
        }
        label202:
        arrayOfResolvedFunctionCall = this.removeTags;
        n = arrayOfResolvedFunctionCall.length;
        k = 0;
        for (j = i;; j = i + j)
        {
          i = j;
          if (k >= n) {
            break;
          }
          i = CodedOutputByteBufferNano.computeMessageSize(4, arrayOfResolvedFunctionCall[k]);
          k += 1;
        }
        label245:
        arrayOfResolvedFunctionCall = this.addMacros;
        n = arrayOfResolvedFunctionCall.length;
        k = 0;
        for (j = i;; j = i + j)
        {
          i = j;
          if (k >= n) {
            break;
          }
          i = CodedOutputByteBufferNano.computeMessageSize(5, arrayOfResolvedFunctionCall[k]);
          k += 1;
        }
        label288:
        arrayOfResolvedFunctionCall = this.removeMacros;
        n = arrayOfResolvedFunctionCall.length;
        k = m;
        for (;;)
        {
          j = i;
          if (k >= n) {
            break;
          }
          i += CodedOutputByteBufferNano.computeMessageSize(6, arrayOfResolvedFunctionCall[k]);
          k += 1;
        }
        label329:
        j += CodedOutputByteBufferNano.computeMessageSize(7, this.result);
      }
    }
    
    public int hashCode()
    {
      int m = 0;
      int i;
      if (this.positivePredicates != null)
      {
        i = 17;
        j = 0;
        if (j < this.positivePredicates.length) {
          break label168;
        }
        label24:
        if (this.negativePredicates == null) {
          break label206;
        }
        j = 0;
        if (j < this.negativePredicates.length) {
          break label214;
        }
        label42:
        if (this.addTags == null) {
          break label252;
        }
        j = 0;
        if (j < this.addTags.length) {
          break label260;
        }
        label60:
        if (this.removeTags == null) {
          break label298;
        }
        j = 0;
        if (j < this.removeTags.length) {
          break label306;
        }
        label78:
        if (this.addMacros == null) {
          break label344;
        }
        j = 0;
        if (j < this.addMacros.length) {
          break label352;
        }
        label96:
        if (this.removeMacros == null) {
          break label390;
        }
        j = 0;
        if (j < this.removeMacros.length) {
          break label398;
        }
        label114:
        if (this.result == null) {
          break label436;
        }
      }
      label168:
      label206:
      label214:
      label252:
      label260:
      label298:
      label306:
      label344:
      label352:
      label390:
      label398:
      label436:
      for (int j = this.result.hashCode();; j = 0)
      {
        int k = m;
        if (this.unknownFieldData != null) {
          k = this.unknownFieldData.hashCode();
        }
        return (j + i * 31) * 31 + k;
        i = 527;
        break label24;
        if (this.positivePredicates[j] != null) {}
        for (k = this.positivePredicates[j].hashCode();; k = 0)
        {
          i = k + i * 31;
          j += 1;
          break;
        }
        i *= 31;
        break label42;
        if (this.negativePredicates[j] != null) {}
        for (k = this.negativePredicates[j].hashCode();; k = 0)
        {
          i = k + i * 31;
          j += 1;
          break;
        }
        i *= 31;
        break label60;
        if (this.addTags[j] != null) {}
        for (k = this.addTags[j].hashCode();; k = 0)
        {
          i = k + i * 31;
          j += 1;
          break;
        }
        i *= 31;
        break label78;
        if (this.removeTags[j] != null) {}
        for (k = this.removeTags[j].hashCode();; k = 0)
        {
          i = k + i * 31;
          j += 1;
          break;
        }
        i *= 31;
        break label96;
        if (this.addMacros[j] != null) {}
        for (k = this.addMacros[j].hashCode();; k = 0)
        {
          i = k + i * 31;
          j += 1;
          break;
        }
        i *= 31;
        break label114;
        if (this.removeMacros[j] != null) {}
        for (k = this.removeMacros[j].hashCode();; k = 0)
        {
          i = k + i * 31;
          j += 1;
          break;
        }
      }
    }
    
    public ResolvedRule mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      for (;;)
      {
        int i = paramCodedInputByteBufferNano.readTag();
        int j;
        label137:
        Debug.ResolvedFunctionCall[] arrayOfResolvedFunctionCall;
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
          if (this.positivePredicates != null)
          {
            i = this.positivePredicates.length;
            arrayOfResolvedFunctionCall = new Debug.ResolvedFunctionCall[j + i];
            if (this.positivePredicates != null) {
              break label200;
            }
            this.positivePredicates = arrayOfResolvedFunctionCall;
          }
          for (;;)
          {
            if (i >= this.positivePredicates.length - 1)
            {
              this.positivePredicates[i] = new Debug.ResolvedFunctionCall();
              paramCodedInputByteBufferNano.readMessage(this.positivePredicates[i]);
              break;
              i = 0;
              break label137;
              System.arraycopy(this.positivePredicates, 0, arrayOfResolvedFunctionCall, 0, i);
              break label152;
            }
            this.positivePredicates[i] = new Debug.ResolvedFunctionCall();
            paramCodedInputByteBufferNano.readMessage(this.positivePredicates[i]);
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 18: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 18);
          if (this.negativePredicates != null)
          {
            i = this.negativePredicates.length;
            arrayOfResolvedFunctionCall = new Debug.ResolvedFunctionCall[j + i];
            if (this.negativePredicates != null) {
              break label333;
            }
            this.negativePredicates = arrayOfResolvedFunctionCall;
          }
          for (;;)
          {
            if (i >= this.negativePredicates.length - 1)
            {
              this.negativePredicates[i] = new Debug.ResolvedFunctionCall();
              paramCodedInputByteBufferNano.readMessage(this.negativePredicates[i]);
              break;
              i = 0;
              break label270;
              System.arraycopy(this.negativePredicates, 0, arrayOfResolvedFunctionCall, 0, i);
              break label285;
            }
            this.negativePredicates[i] = new Debug.ResolvedFunctionCall();
            paramCodedInputByteBufferNano.readMessage(this.negativePredicates[i]);
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 26: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 26);
          if (this.addTags != null)
          {
            i = this.addTags.length;
            arrayOfResolvedFunctionCall = new Debug.ResolvedFunctionCall[j + i];
            if (this.addTags != null) {
              break label466;
            }
            this.addTags = arrayOfResolvedFunctionCall;
          }
          for (;;)
          {
            if (i >= this.addTags.length - 1)
            {
              this.addTags[i] = new Debug.ResolvedFunctionCall();
              paramCodedInputByteBufferNano.readMessage(this.addTags[i]);
              break;
              i = 0;
              break label403;
              System.arraycopy(this.addTags, 0, arrayOfResolvedFunctionCall, 0, i);
              break label418;
            }
            this.addTags[i] = new Debug.ResolvedFunctionCall();
            paramCodedInputByteBufferNano.readMessage(this.addTags[i]);
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 34: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 34);
          if (this.removeTags != null)
          {
            i = this.removeTags.length;
            arrayOfResolvedFunctionCall = new Debug.ResolvedFunctionCall[j + i];
            if (this.removeTags != null) {
              break label599;
            }
            this.removeTags = arrayOfResolvedFunctionCall;
          }
          for (;;)
          {
            if (i >= this.removeTags.length - 1)
            {
              this.removeTags[i] = new Debug.ResolvedFunctionCall();
              paramCodedInputByteBufferNano.readMessage(this.removeTags[i]);
              break;
              i = 0;
              break label536;
              System.arraycopy(this.removeTags, 0, arrayOfResolvedFunctionCall, 0, i);
              break label551;
            }
            this.removeTags[i] = new Debug.ResolvedFunctionCall();
            paramCodedInputByteBufferNano.readMessage(this.removeTags[i]);
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 42: 
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 42);
          if (this.addMacros != null)
          {
            i = this.addMacros.length;
            arrayOfResolvedFunctionCall = new Debug.ResolvedFunctionCall[j + i];
            if (this.addMacros != null) {
              break label732;
            }
            this.addMacros = arrayOfResolvedFunctionCall;
          }
          for (;;)
          {
            if (i >= this.addMacros.length - 1)
            {
              this.addMacros[i] = new Debug.ResolvedFunctionCall();
              paramCodedInputByteBufferNano.readMessage(this.addMacros[i]);
              break;
              i = 0;
              break label669;
              System.arraycopy(this.addMacros, 0, arrayOfResolvedFunctionCall, 0, i);
              break label684;
            }
            this.addMacros[i] = new Debug.ResolvedFunctionCall();
            paramCodedInputByteBufferNano.readMessage(this.addMacros[i]);
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        case 50: 
          label152:
          label200:
          label270:
          label285:
          label333:
          label403:
          label418:
          label466:
          label536:
          label551:
          label599:
          label669:
          label684:
          label732:
          j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 50);
          if (this.removeMacros != null)
          {
            i = this.removeMacros.length;
            label802:
            arrayOfResolvedFunctionCall = new Debug.ResolvedFunctionCall[j + i];
            if (this.removeMacros != null) {
              break label865;
            }
            label817:
            this.removeMacros = arrayOfResolvedFunctionCall;
          }
          for (;;)
          {
            if (i >= this.removeMacros.length - 1)
            {
              this.removeMacros[i] = new Debug.ResolvedFunctionCall();
              paramCodedInputByteBufferNano.readMessage(this.removeMacros[i]);
              break;
              i = 0;
              break label802;
              label865:
              System.arraycopy(this.removeMacros, 0, arrayOfResolvedFunctionCall, 0, i);
              break label817;
            }
            this.removeMacros[i] = new Debug.ResolvedFunctionCall();
            paramCodedInputByteBufferNano.readMessage(this.removeMacros[i]);
            paramCodedInputByteBufferNano.readTag();
            i += 1;
          }
        }
        this.result = new TypeSystem.Value();
        paramCodedInputByteBufferNano.readMessage(this.result);
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      int j = 0;
      if (this.positivePredicates == null)
      {
        if (this.negativePredicates != null) {
          break label95;
        }
        label16:
        if (this.addTags != null) {
          break label130;
        }
        label23:
        if (this.removeTags != null) {
          break label165;
        }
        label30:
        if (this.addMacros != null) {
          break label200;
        }
        label37:
        if (this.removeMacros != null) {
          break label235;
        }
        label44:
        if (this.result != null) {
          break label271;
        }
      }
      for (;;)
      {
        WireFormatNano.writeUnknownFields(this.unknownFieldData, paramCodedOutputByteBufferNano);
        return;
        Debug.ResolvedFunctionCall[] arrayOfResolvedFunctionCall = this.positivePredicates;
        int k = arrayOfResolvedFunctionCall.length;
        int i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeMessage(1, arrayOfResolvedFunctionCall[i]);
          i += 1;
        }
        break;
        label95:
        arrayOfResolvedFunctionCall = this.negativePredicates;
        k = arrayOfResolvedFunctionCall.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeMessage(2, arrayOfResolvedFunctionCall[i]);
          i += 1;
        }
        break label16;
        label130:
        arrayOfResolvedFunctionCall = this.addTags;
        k = arrayOfResolvedFunctionCall.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeMessage(3, arrayOfResolvedFunctionCall[i]);
          i += 1;
        }
        break label23;
        label165:
        arrayOfResolvedFunctionCall = this.removeTags;
        k = arrayOfResolvedFunctionCall.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeMessage(4, arrayOfResolvedFunctionCall[i]);
          i += 1;
        }
        break label30;
        label200:
        arrayOfResolvedFunctionCall = this.addMacros;
        k = arrayOfResolvedFunctionCall.length;
        i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeMessage(5, arrayOfResolvedFunctionCall[i]);
          i += 1;
        }
        break label37;
        label235:
        arrayOfResolvedFunctionCall = this.removeMacros;
        k = arrayOfResolvedFunctionCall.length;
        i = j;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeMessage(6, arrayOfResolvedFunctionCall[i]);
          i += 1;
        }
        break label44;
        label271:
        paramCodedOutputByteBufferNano.writeMessage(7, this.result);
      }
    }
  }
  
  public static final class RuleEvaluationStepInfo
    extends ExtendableMessageNano
  {
    public static final RuleEvaluationStepInfo[] EMPTY_ARRAY = new RuleEvaluationStepInfo[0];
    public Debug.ResolvedFunctionCall[] enabledFunctions = Debug.ResolvedFunctionCall.EMPTY_ARRAY;
    public Debug.ResolvedRule[] rules = Debug.ResolvedRule.EMPTY_ARRAY;
    
    public static RuleEvaluationStepInfo parseFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      return new RuleEvaluationStepInfo().mergeFrom(paramCodedInputByteBufferNano);
    }
    
    public static RuleEvaluationStepInfo parseFrom(byte[] paramArrayOfByte)
      throws InvalidProtocolBufferNanoException
    {
      return (RuleEvaluationStepInfo)MessageNano.mergeFrom(new RuleEvaluationStepInfo(), paramArrayOfByte);
    }
    
    public final RuleEvaluationStepInfo clear()
    {
      this.rules = Debug.ResolvedRule.EMPTY_ARRAY;
      this.enabledFunctions = Debug.ResolvedFunctionCall.EMPTY_ARRAY;
      this.unknownFieldData = null;
      this.cachedSize = -1;
      return this;
    }
    
    public final boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (paramObject != this)
      {
        if (!(paramObject instanceof RuleEvaluationStepInfo)) {
          break label39;
        }
        paramObject = (RuleEvaluationStepInfo)paramObject;
        if (Arrays.equals(this.rules, ((RuleEvaluationStepInfo)paramObject).rules)) {
          break label41;
        }
      }
      for (;;)
      {
        bool = false;
        label39:
        label41:
        label81:
        do
        {
          do
          {
            return bool;
            return true;
            return false;
            if (!Arrays.equals(this.enabledFunctions, ((RuleEvaluationStepInfo)paramObject).enabledFunctions)) {
              break;
            }
            if (this.unknownFieldData == null) {
              break label81;
            }
          } while (this.unknownFieldData.equals(((RuleEvaluationStepInfo)paramObject).unknownFieldData));
          break;
        } while (((RuleEvaluationStepInfo)paramObject).unknownFieldData == null);
      }
    }
    
    public int getSerializedSize()
    {
      int m = 0;
      int i;
      int k;
      if (this.rules == null)
      {
        i = 0;
        if (this.enabledFunctions == null)
        {
          k = i;
          i = k + WireFormatNano.computeWireSize(this.unknownFieldData);
          this.cachedSize = i;
          return i;
        }
      }
      else
      {
        localObject = this.rules;
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
      Object localObject = this.enabledFunctions;
      int n = localObject.length;
      int j = m;
      for (;;)
      {
        k = i;
        if (j >= n) {
          break;
        }
        i += CodedOutputByteBufferNano.computeMessageSize(2, localObject[j]);
        j += 1;
      }
    }
    
    public int hashCode()
    {
      int m = 0;
      int i;
      int j;
      if (this.rules != null)
      {
        i = 17;
        j = 0;
        if (j < this.rules.length) {
          break label76;
        }
        label24:
        if (this.enabledFunctions == null) {
          break label114;
        }
        j = 0;
        if (j < this.enabledFunctions.length) {
          break label122;
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
        label76:
        if (this.rules[j] != null) {}
        for (k = this.rules[j].hashCode();; k = 0)
        {
          i = k + i * 31;
          j += 1;
          break;
        }
        label114:
        i *= 31;
      }
      label122:
      if (this.enabledFunctions[j] != null) {}
      for (int k = this.enabledFunctions[j].hashCode();; k = 0)
      {
        i = k + i * 31;
        j += 1;
        break;
      }
    }
    
    public RuleEvaluationStepInfo mergeFrom(CodedInputByteBufferNano paramCodedInputByteBufferNano)
      throws IOException
    {
      int i = paramCodedInputByteBufferNano.readTag();
      label97:
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
        if (this.rules != null)
        {
          i = this.rules.length;
          localObject = new Debug.ResolvedRule[j + i];
          if (this.rules != null) {
            break label160;
          }
          label112:
          this.rules = ((Debug.ResolvedRule[])localObject);
        }
        for (;;)
        {
          if (i >= this.rules.length - 1)
          {
            this.rules[i] = new Debug.ResolvedRule();
            paramCodedInputByteBufferNano.readMessage(this.rules[i]);
            break;
            i = 0;
            break label97;
            label160:
            System.arraycopy(this.rules, 0, localObject, 0, i);
            break label112;
          }
          this.rules[i] = new Debug.ResolvedRule();
          paramCodedInputByteBufferNano.readMessage(this.rules[i]);
          paramCodedInputByteBufferNano.readTag();
          i += 1;
        }
      }
      int j = WireFormatNano.getRepeatedFieldArrayLength(paramCodedInputByteBufferNano, 18);
      if (this.enabledFunctions != null)
      {
        i = this.enabledFunctions.length;
        label230:
        localObject = new Debug.ResolvedFunctionCall[j + i];
        if (this.enabledFunctions != null) {
          break label293;
        }
        label245:
        this.enabledFunctions = ((Debug.ResolvedFunctionCall[])localObject);
      }
      for (;;)
      {
        if (i >= this.enabledFunctions.length - 1)
        {
          this.enabledFunctions[i] = new Debug.ResolvedFunctionCall();
          paramCodedInputByteBufferNano.readMessage(this.enabledFunctions[i]);
          break;
          i = 0;
          break label230;
          label293:
          System.arraycopy(this.enabledFunctions, 0, localObject, 0, i);
          break label245;
        }
        this.enabledFunctions[i] = new Debug.ResolvedFunctionCall();
        paramCodedInputByteBufferNano.readMessage(this.enabledFunctions[i]);
        paramCodedInputByteBufferNano.readTag();
        i += 1;
      }
    }
    
    public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
      throws IOException
    {
      int j = 0;
      if (this.rules == null) {
        if (this.enabledFunctions != null) {
          break label60;
        }
      }
      for (;;)
      {
        WireFormatNano.writeUnknownFields(this.unknownFieldData, paramCodedOutputByteBufferNano);
        return;
        Object localObject = this.rules;
        int k = localObject.length;
        int i = 0;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeMessage(1, localObject[i]);
          i += 1;
        }
        break;
        label60:
        localObject = this.enabledFunctions;
        k = localObject.length;
        i = j;
        while (i < k)
        {
          paramCodedOutputByteBufferNano.writeMessage(2, localObject[i]);
          i += 1;
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\analytics\containertag\proto\Debug.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */