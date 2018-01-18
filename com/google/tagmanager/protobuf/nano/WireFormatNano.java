package com.google.tagmanager.protobuf.nano;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class WireFormatNano
{
  public static final boolean[] EMPTY_BOOLEAN_ARRAY;
  public static final Boolean[] EMPTY_BOOLEAN_REF_ARRAY = new Boolean[0];
  public static final byte[] EMPTY_BYTES;
  public static final byte[][] EMPTY_BYTES_ARRAY;
  public static final double[] EMPTY_DOUBLE_ARRAY;
  public static final Double[] EMPTY_DOUBLE_REF_ARRAY;
  public static final float[] EMPTY_FLOAT_ARRAY;
  public static final Float[] EMPTY_FLOAT_REF_ARRAY;
  public static final int[] EMPTY_INT_ARRAY;
  public static final Integer[] EMPTY_INT_REF_ARRAY;
  public static final long[] EMPTY_LONG_ARRAY;
  public static final Long[] EMPTY_LONG_REF_ARRAY;
  public static final String[] EMPTY_STRING_ARRAY;
  static final int MESSAGE_SET_ITEM = 1;
  static final int MESSAGE_SET_ITEM_END_TAG;
  static final int MESSAGE_SET_ITEM_TAG = makeTag(1, 3);
  static final int MESSAGE_SET_MESSAGE = 3;
  static final int MESSAGE_SET_MESSAGE_TAG;
  static final int MESSAGE_SET_TYPE_ID = 2;
  static final int MESSAGE_SET_TYPE_ID_TAG;
  static final int TAG_TYPE_BITS = 3;
  static final int TAG_TYPE_MASK = 7;
  static final int WIRETYPE_END_GROUP = 4;
  static final int WIRETYPE_FIXED32 = 5;
  static final int WIRETYPE_FIXED64 = 1;
  static final int WIRETYPE_LENGTH_DELIMITED = 2;
  static final int WIRETYPE_START_GROUP = 3;
  static final int WIRETYPE_VARINT = 0;
  
  static
  {
    MESSAGE_SET_ITEM_END_TAG = makeTag(1, 4);
    MESSAGE_SET_TYPE_ID_TAG = makeTag(2, 0);
    MESSAGE_SET_MESSAGE_TAG = makeTag(3, 2);
    EMPTY_INT_ARRAY = new int[0];
    EMPTY_LONG_ARRAY = new long[0];
    EMPTY_FLOAT_ARRAY = new float[0];
    EMPTY_DOUBLE_ARRAY = new double[0];
    EMPTY_BOOLEAN_ARRAY = new boolean[0];
    EMPTY_STRING_ARRAY = new String[0];
    EMPTY_BYTES_ARRAY = new byte[0][];
    EMPTY_BYTES = new byte[0];
    EMPTY_INT_REF_ARRAY = new Integer[0];
    EMPTY_LONG_REF_ARRAY = new Long[0];
    EMPTY_FLOAT_REF_ARRAY = new Float[0];
    EMPTY_DOUBLE_REF_ARRAY = new Double[0];
  }
  
  public static int computeWireSize(List<UnknownFieldData> paramList)
  {
    if (paramList != null) {
      paramList = paramList.iterator();
    }
    UnknownFieldData localUnknownFieldData;
    int j;
    for (int i = 0;; i = localUnknownFieldData.bytes.length + (i + j))
    {
      if (!paramList.hasNext())
      {
        return i;
        return 0;
      }
      localUnknownFieldData = (UnknownFieldData)paramList.next();
      j = CodedOutputByteBufferNano.computeRawVarint32Size(localUnknownFieldData.tag);
    }
  }
  
  public static <T> T getExtension(Extension<T> paramExtension, List<UnknownFieldData> paramList)
  {
    if (paramList != null)
    {
      localObject = new ArrayList();
      paramList = paramList.iterator();
    }
    UnknownFieldData localUnknownFieldData;
    for (;;)
    {
      if (!paramList.hasNext())
      {
        if (((List)localObject).isEmpty()) {
          break;
        }
        if (paramExtension.isRepeatedField) {
          break label113;
        }
        paramList = (UnknownFieldData)((List)localObject).get(((List)localObject).size() - 1);
        return (T)readData(paramExtension.fieldType, paramList.bytes);
        return null;
      }
      localUnknownFieldData = (UnknownFieldData)paramList.next();
      if (getTagFieldNumber(localUnknownFieldData.tag) == paramExtension.fieldNumber) {
        ((List)localObject).add(localUnknownFieldData);
      }
    }
    return null;
    label113:
    paramList = new ArrayList(((List)localObject).size());
    Object localObject = ((List)localObject).iterator();
    for (;;)
    {
      if (!((Iterator)localObject).hasNext()) {
        return (T)paramExtension.listType.cast(paramList);
      }
      localUnknownFieldData = (UnknownFieldData)((Iterator)localObject).next();
      paramList.add(readData(paramExtension.fieldType, localUnknownFieldData.bytes));
    }
  }
  
  public static final int getRepeatedFieldArrayLength(CodedInputByteBufferNano paramCodedInputByteBufferNano, int paramInt)
    throws IOException
  {
    int i = 1;
    int j = paramCodedInputByteBufferNano.getPosition();
    paramCodedInputByteBufferNano.skipField(paramInt);
    for (;;)
    {
      if (paramCodedInputByteBufferNano.getBytesUntilLimit() <= 0) {}
      while (paramCodedInputByteBufferNano.readTag() != paramInt)
      {
        paramCodedInputByteBufferNano.rewindToPosition(j);
        return i;
      }
      paramCodedInputByteBufferNano.skipField(paramInt);
      i += 1;
    }
  }
  
  public static int getTagFieldNumber(int paramInt)
  {
    return paramInt >>> 3;
  }
  
  static int getTagWireType(int paramInt)
  {
    return paramInt & 0x7;
  }
  
  static int makeTag(int paramInt1, int paramInt2)
  {
    return paramInt1 << 3 | paramInt2;
  }
  
  public static boolean parseUnknownField(CodedInputByteBufferNano paramCodedInputByteBufferNano, int paramInt)
    throws IOException
  {
    return paramCodedInputByteBufferNano.skipField(paramInt);
  }
  
  private static <T> T readData(Class<T> paramClass, byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length != 0)
    {
      paramArrayOfByte = CodedInputByteBufferNano.newInstance(paramArrayOfByte);
      if (paramClass != String.class)
      {
        if (paramClass == Integer.class) {
          break label112;
        }
        if (paramClass == Long.class) {
          break label124;
        }
        if (paramClass == Boolean.class) {
          break label136;
        }
        if (paramClass == Float.class) {
          break label148;
        }
        if (paramClass == Double.class) {
          break label160;
        }
        if (paramClass == byte[].class) {
          break label172;
        }
        try
        {
          if (MessageNano.class.isAssignableFrom(paramClass)) {
            break label183;
          }
          throw new IllegalArgumentException("Unhandled extension field type: " + paramClass);
        }
        catch (IOException paramClass)
        {
          throw new IllegalArgumentException("Error reading extension field", paramClass);
        }
      }
    }
    else
    {
      return null;
    }
    return (T)paramClass.cast(paramArrayOfByte.readString());
    label112:
    return (T)paramClass.cast(Integer.valueOf(paramArrayOfByte.readInt32()));
    label124:
    return (T)paramClass.cast(Long.valueOf(paramArrayOfByte.readInt64()));
    label136:
    return (T)paramClass.cast(Boolean.valueOf(paramArrayOfByte.readBool()));
    label148:
    return (T)paramClass.cast(Float.valueOf(paramArrayOfByte.readFloat()));
    label160:
    return (T)paramClass.cast(Double.valueOf(paramArrayOfByte.readDouble()));
    label172:
    paramClass = paramClass.cast(paramArrayOfByte.readBytes());
    return paramClass;
    try
    {
      label183:
      MessageNano localMessageNano = (MessageNano)paramClass.newInstance();
      paramArrayOfByte.readMessage(localMessageNano);
      paramArrayOfByte = paramClass.cast(localMessageNano);
      return paramArrayOfByte;
    }
    catch (IllegalAccessException paramArrayOfByte)
    {
      throw new IllegalArgumentException("Error creating instance of class " + paramClass, paramArrayOfByte);
    }
    catch (InstantiationException paramArrayOfByte)
    {
      throw new IllegalArgumentException("Error creating instance of class " + paramClass, paramArrayOfByte);
    }
  }
  
  public static <T> void setExtension(Extension<T> paramExtension, T paramT, List<UnknownFieldData> paramList)
  {
    Object localObject = paramList.iterator();
    if (!((Iterator)localObject).hasNext())
    {
      if (paramT == null) {
        break label78;
      }
      if ((paramT instanceof List)) {
        break label79;
      }
      paramList.add(write(paramExtension.fieldNumber, paramT));
    }
    for (;;)
    {
      return;
      UnknownFieldData localUnknownFieldData = (UnknownFieldData)((Iterator)localObject).next();
      if (paramExtension.fieldNumber != getTagFieldNumber(localUnknownFieldData.tag)) {
        break;
      }
      ((Iterator)localObject).remove();
      break;
      label78:
      return;
      label79:
      paramT = ((List)paramT).iterator();
      while (paramT.hasNext())
      {
        localObject = paramT.next();
        paramList.add(write(paramExtension.fieldNumber, localObject));
      }
    }
  }
  
  public static boolean storeUnknownField(List<UnknownFieldData> paramList, CodedInputByteBufferNano paramCodedInputByteBufferNano, int paramInt)
    throws IOException
  {
    int i = paramCodedInputByteBufferNano.getPosition();
    boolean bool = paramCodedInputByteBufferNano.skipField(paramInt);
    paramList.add(new UnknownFieldData(paramInt, paramCodedInputByteBufferNano.getData(i, paramCodedInputByteBufferNano.getPosition() - i)));
    return bool;
  }
  
  private static UnknownFieldData write(int paramInt, Object paramObject)
  {
    Object localObject = paramObject.getClass();
    if (localObject != String.class)
    {
      if (localObject != Integer.class)
      {
        if (localObject == Long.class) {
          break label164;
        }
        if (localObject == Boolean.class) {
          break label199;
        }
        if (localObject == Float.class) {
          break label234;
        }
        if (localObject == Double.class) {
          break label269;
        }
        if (localObject == byte[].class) {
          break label304;
        }
        try
        {
          if (MessageNano.class.isAssignableFrom((Class)localObject)) {
            break label336;
          }
          throw new IllegalArgumentException("Unhandled extension field type: " + localObject);
        }
        catch (IOException paramObject)
        {
          throw new IllegalArgumentException((Throwable)paramObject);
        }
      }
    }
    else
    {
      localObject = (String)paramObject;
      paramObject = new byte[CodedOutputByteBufferNano.computeStringSizeNoTag((String)localObject)];
      CodedOutputByteBufferNano.newInstance((byte[])paramObject).writeStringNoTag((String)localObject);
      paramInt = makeTag(paramInt, 2);
    }
    for (;;)
    {
      return new UnknownFieldData(paramInt, (byte[])paramObject);
      localObject = (Integer)paramObject;
      paramObject = new byte[CodedOutputByteBufferNano.computeInt32SizeNoTag(((Integer)localObject).intValue())];
      CodedOutputByteBufferNano.newInstance((byte[])paramObject).writeInt32NoTag(((Integer)localObject).intValue());
      paramInt = makeTag(paramInt, 0);
      continue;
      label164:
      localObject = (Long)paramObject;
      paramObject = new byte[CodedOutputByteBufferNano.computeInt64SizeNoTag(((Long)localObject).longValue())];
      CodedOutputByteBufferNano.newInstance((byte[])paramObject).writeInt64NoTag(((Long)localObject).longValue());
      paramInt = makeTag(paramInt, 0);
      continue;
      label199:
      localObject = (Boolean)paramObject;
      paramObject = new byte[CodedOutputByteBufferNano.computeBoolSizeNoTag(((Boolean)localObject).booleanValue())];
      CodedOutputByteBufferNano.newInstance((byte[])paramObject).writeBoolNoTag(((Boolean)localObject).booleanValue());
      paramInt = makeTag(paramInt, 0);
      continue;
      label234:
      localObject = (Float)paramObject;
      paramObject = new byte[CodedOutputByteBufferNano.computeFloatSizeNoTag(((Float)localObject).floatValue())];
      CodedOutputByteBufferNano.newInstance((byte[])paramObject).writeFloatNoTag(((Float)localObject).floatValue());
      paramInt = makeTag(paramInt, 5);
      continue;
      label269:
      localObject = (Double)paramObject;
      paramObject = new byte[CodedOutputByteBufferNano.computeDoubleSizeNoTag(((Double)localObject).doubleValue())];
      CodedOutputByteBufferNano.newInstance((byte[])paramObject).writeDoubleNoTag(((Double)localObject).doubleValue());
      paramInt = makeTag(paramInt, 1);
      continue;
      label304:
      localObject = (byte[])paramObject;
      paramObject = new byte[CodedOutputByteBufferNano.computeByteArraySizeNoTag((byte[])localObject)];
      CodedOutputByteBufferNano.newInstance((byte[])paramObject).writeByteArrayNoTag((byte[])localObject);
      paramInt = makeTag(paramInt, 2);
      continue;
      label336:
      localObject = (MessageNano)paramObject;
      int i = ((MessageNano)localObject).getSerializedSize();
      paramObject = new byte[CodedOutputByteBufferNano.computeRawVarint32Size(i) + i];
      CodedOutputByteBufferNano localCodedOutputByteBufferNano = CodedOutputByteBufferNano.newInstance((byte[])paramObject);
      localCodedOutputByteBufferNano.writeRawVarint32(i);
      localCodedOutputByteBufferNano.writeRawBytes(MessageNano.toByteArray((MessageNano)localObject));
      paramInt = makeTag(paramInt, 2);
    }
  }
  
  public static void writeUnknownFields(List<UnknownFieldData> paramList, CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
    throws IOException
  {
    if (paramList != null) {
      paramList = paramList.iterator();
    }
    for (;;)
    {
      if (!paramList.hasNext())
      {
        return;
        return;
      }
      UnknownFieldData localUnknownFieldData = (UnknownFieldData)paramList.next();
      paramCodedOutputByteBufferNano.writeTag(getTagFieldNumber(localUnknownFieldData.tag), getTagWireType(localUnknownFieldData.tag));
      paramCodedOutputByteBufferNano.writeRawBytes(localUnknownFieldData.bytes);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\protobuf\nano\WireFormatNano.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */