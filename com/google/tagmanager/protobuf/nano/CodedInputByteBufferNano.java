package com.google.tagmanager.protobuf.nano;

import java.io.IOException;

public final class CodedInputByteBufferNano
{
  private static final int DEFAULT_RECURSION_LIMIT = 64;
  private static final int DEFAULT_SIZE_LIMIT = 67108864;
  private final byte[] buffer;
  private int bufferPos;
  private int bufferSize;
  private int bufferSizeAfterLimit;
  private int bufferStart;
  private int currentLimit = Integer.MAX_VALUE;
  private int lastTag;
  private int recursionDepth;
  private int recursionLimit = 64;
  private int sizeLimit = 67108864;
  
  private CodedInputByteBufferNano(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.buffer = paramArrayOfByte;
    this.bufferStart = paramInt1;
    this.bufferSize = (paramInt1 + paramInt2);
    this.bufferPos = paramInt1;
  }
  
  public static int decodeZigZag32(int paramInt)
  {
    return paramInt >>> 1 ^ -(paramInt & 0x1);
  }
  
  public static long decodeZigZag64(long paramLong)
  {
    return paramLong >>> 1 ^ -(1L & paramLong);
  }
  
  public static CodedInputByteBufferNano newInstance(byte[] paramArrayOfByte)
  {
    return newInstance(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static CodedInputByteBufferNano newInstance(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new CodedInputByteBufferNano(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  private void recomputeBufferSizeAfterLimit()
  {
    this.bufferSize += this.bufferSizeAfterLimit;
    int i = this.bufferSize;
    if (i <= this.currentLimit)
    {
      this.bufferSizeAfterLimit = 0;
      return;
    }
    this.bufferSizeAfterLimit = (i - this.currentLimit);
    this.bufferSize -= this.bufferSizeAfterLimit;
  }
  
  public void checkLastTagWas(int paramInt)
    throws InvalidProtocolBufferNanoException
  {
    if (this.lastTag == paramInt) {
      return;
    }
    throw InvalidProtocolBufferNanoException.invalidEndTag();
  }
  
  public int getBytesUntilLimit()
  {
    if (this.currentLimit != Integer.MAX_VALUE)
    {
      int i = this.bufferPos;
      return this.currentLimit - i;
    }
    return -1;
  }
  
  public byte[] getData(int paramInt1, int paramInt2)
  {
    if (paramInt2 != 0)
    {
      byte[] arrayOfByte = new byte[paramInt2];
      int i = this.bufferStart;
      System.arraycopy(this.buffer, i + paramInt1, arrayOfByte, 0, paramInt2);
      return arrayOfByte;
    }
    return WireFormatNano.EMPTY_BYTES;
  }
  
  public int getPosition()
  {
    return this.bufferPos - this.bufferStart;
  }
  
  public boolean isAtEnd()
  {
    return this.bufferPos == this.bufferSize;
  }
  
  public void popLimit(int paramInt)
  {
    this.currentLimit = paramInt;
    recomputeBufferSizeAfterLimit();
  }
  
  public int pushLimit(int paramInt)
    throws InvalidProtocolBufferNanoException
  {
    if (paramInt >= 0)
    {
      paramInt = this.bufferPos + paramInt;
      int i = this.currentLimit;
      if (paramInt <= i)
      {
        this.currentLimit = paramInt;
        recomputeBufferSizeAfterLimit();
        return i;
      }
    }
    else
    {
      throw InvalidProtocolBufferNanoException.negativeSize();
    }
    throw InvalidProtocolBufferNanoException.truncatedMessage();
  }
  
  public boolean readBool()
    throws IOException
  {
    return readRawVarint32() != 0;
  }
  
  public byte[] readBytes()
    throws IOException
  {
    int i = readRawVarint32();
    if (i > this.bufferSize - this.bufferPos) {}
    while (i <= 0) {
      return readRawBytes(i);
    }
    byte[] arrayOfByte = new byte[i];
    System.arraycopy(this.buffer, this.bufferPos, arrayOfByte, 0, i);
    this.bufferPos = (i + this.bufferPos);
    return arrayOfByte;
  }
  
  public double readDouble()
    throws IOException
  {
    return Double.longBitsToDouble(readRawLittleEndian64());
  }
  
  public int readEnum()
    throws IOException
  {
    return readRawVarint32();
  }
  
  public int readFixed32()
    throws IOException
  {
    return readRawLittleEndian32();
  }
  
  public long readFixed64()
    throws IOException
  {
    return readRawLittleEndian64();
  }
  
  public float readFloat()
    throws IOException
  {
    return Float.intBitsToFloat(readRawLittleEndian32());
  }
  
  public void readGroup(MessageNano paramMessageNano, int paramInt)
    throws IOException
  {
    if (this.recursionDepth < this.recursionLimit)
    {
      this.recursionDepth += 1;
      paramMessageNano.mergeFrom(this);
      checkLastTagWas(WireFormatNano.makeTag(paramInt, 4));
      this.recursionDepth -= 1;
      return;
    }
    throw InvalidProtocolBufferNanoException.recursionLimitExceeded();
  }
  
  public int readInt32()
    throws IOException
  {
    return readRawVarint32();
  }
  
  public long readInt64()
    throws IOException
  {
    return readRawVarint64();
  }
  
  public void readMessage(MessageNano paramMessageNano)
    throws IOException
  {
    int i = readRawVarint32();
    if (this.recursionDepth < this.recursionLimit)
    {
      i = pushLimit(i);
      this.recursionDepth += 1;
      paramMessageNano.mergeFrom(this);
      checkLastTagWas(0);
      this.recursionDepth -= 1;
      popLimit(i);
      return;
    }
    throw InvalidProtocolBufferNanoException.recursionLimitExceeded();
  }
  
  public byte readRawByte()
    throws IOException
  {
    if (this.bufferPos != this.bufferSize)
    {
      byte[] arrayOfByte = this.buffer;
      int i = this.bufferPos;
      this.bufferPos = (i + 1);
      return arrayOfByte[i];
    }
    throw InvalidProtocolBufferNanoException.truncatedMessage();
  }
  
  public byte[] readRawBytes(int paramInt)
    throws IOException
  {
    if (paramInt >= 0)
    {
      if (this.bufferPos + paramInt <= this.currentLimit)
      {
        if (paramInt <= this.bufferSize - this.bufferPos) {
          break label55;
        }
        throw InvalidProtocolBufferNanoException.truncatedMessage();
      }
    }
    else {
      throw InvalidProtocolBufferNanoException.negativeSize();
    }
    skipRawBytes(this.currentLimit - this.bufferPos);
    throw InvalidProtocolBufferNanoException.truncatedMessage();
    label55:
    byte[] arrayOfByte = new byte[paramInt];
    System.arraycopy(this.buffer, this.bufferPos, arrayOfByte, 0, paramInt);
    this.bufferPos += paramInt;
    return arrayOfByte;
  }
  
  public int readRawLittleEndian32()
    throws IOException
  {
    return readRawByte() & 0xFF | (readRawByte() & 0xFF) << 8 | (readRawByte() & 0xFF) << 16 | (readRawByte() & 0xFF) << 24;
  }
  
  public long readRawLittleEndian64()
    throws IOException
  {
    int i = readRawByte();
    int j = readRawByte();
    int k = readRawByte();
    int m = readRawByte();
    int n = readRawByte();
    int i1 = readRawByte();
    int i2 = readRawByte();
    int i3 = readRawByte();
    long l = i;
    return (j & 0xFF) << 8 | l & 0xFF | (k & 0xFF) << 16 | (m & 0xFF) << 24 | (n & 0xFF) << 32 | (i1 & 0xFF) << 40 | (i2 & 0xFF) << 48 | (i3 & 0xFF) << 56;
  }
  
  public int readRawVarint32()
    throws IOException
  {
    int i = 0;
    int j = readRawByte();
    int k;
    int m;
    if (j < 0)
    {
      j &= 0x7F;
      k = readRawByte();
      if (k < 0)
      {
        j |= (k & 0x7F) << 7;
        k = readRawByte();
        if (k >= 0) {
          break label101;
        }
        k = j | (k & 0x7F) << 14;
        m = readRawByte();
        if (m >= 0) {
          break label108;
        }
        j = readRawByte();
        k = k | (m & 0x7F) << 21 | j << 28;
        if (j < 0) {
          break label127;
        }
        return k;
      }
    }
    else
    {
      return j;
    }
    return j | k << 7;
    label101:
    return j | k << 14;
    label108:
    return k | m << 21;
    while (readRawByte() < 0)
    {
      i += 1;
      label127:
      if (i >= 5) {
        throw InvalidProtocolBufferNanoException.malformedVarint();
      }
    }
    return k;
  }
  
  public long readRawVarint64()
    throws IOException
  {
    int i = 0;
    long l = 0L;
    for (;;)
    {
      if (i >= 64) {
        throw InvalidProtocolBufferNanoException.malformedVarint();
      }
      int j = readRawByte();
      l |= (j & 0x7F) << i;
      if ((j & 0x80) == 0) {
        break;
      }
      i += 7;
    }
    return l;
  }
  
  public int readSFixed32()
    throws IOException
  {
    return readRawLittleEndian32();
  }
  
  public long readSFixed64()
    throws IOException
  {
    return readRawLittleEndian64();
  }
  
  public int readSInt32()
    throws IOException
  {
    return decodeZigZag32(readRawVarint32());
  }
  
  public long readSInt64()
    throws IOException
  {
    return decodeZigZag64(readRawVarint64());
  }
  
  public String readString()
    throws IOException
  {
    int i = readRawVarint32();
    if (i > this.bufferSize - this.bufferPos) {}
    while (i <= 0) {
      return new String(readRawBytes(i), "UTF-8");
    }
    String str = new String(this.buffer, this.bufferPos, i, "UTF-8");
    this.bufferPos = (i + this.bufferPos);
    return str;
  }
  
  public int readTag()
    throws IOException
  {
    if (!isAtEnd())
    {
      this.lastTag = readRawVarint32();
      if (this.lastTag != 0) {
        return this.lastTag;
      }
    }
    else
    {
      this.lastTag = 0;
      return 0;
    }
    throw InvalidProtocolBufferNanoException.invalidTag();
  }
  
  public int readUInt32()
    throws IOException
  {
    return readRawVarint32();
  }
  
  public long readUInt64()
    throws IOException
  {
    return readRawVarint64();
  }
  
  public void resetSizeCounter() {}
  
  public void rewindToPosition(int paramInt)
  {
    if (paramInt <= this.bufferPos - this.bufferStart)
    {
      if (paramInt >= 0) {
        this.bufferPos = (this.bufferStart + paramInt);
      }
    }
    else {
      throw new IllegalArgumentException("Position " + paramInt + " is beyond current " + (this.bufferPos - this.bufferStart));
    }
    throw new IllegalArgumentException("Bad position " + paramInt);
  }
  
  public int setRecursionLimit(int paramInt)
  {
    if (paramInt >= 0)
    {
      int i = this.recursionLimit;
      this.recursionLimit = paramInt;
      return i;
    }
    throw new IllegalArgumentException("Recursion limit cannot be negative: " + paramInt);
  }
  
  public int setSizeLimit(int paramInt)
  {
    if (paramInt >= 0)
    {
      int i = this.sizeLimit;
      this.sizeLimit = paramInt;
      return i;
    }
    throw new IllegalArgumentException("Size limit cannot be negative: " + paramInt);
  }
  
  public boolean skipField(int paramInt)
    throws IOException
  {
    switch (WireFormatNano.getTagWireType(paramInt))
    {
    default: 
      throw InvalidProtocolBufferNanoException.invalidWireType();
    case 0: 
      readInt32();
      return true;
    case 1: 
      readRawLittleEndian64();
      return true;
    case 2: 
      skipRawBytes(readRawVarint32());
      return true;
    case 3: 
      skipMessage();
      checkLastTagWas(WireFormatNano.makeTag(WireFormatNano.getTagFieldNumber(paramInt), 4));
      return true;
    case 4: 
      return false;
    }
    readRawLittleEndian32();
    return true;
  }
  
  public void skipMessage()
    throws IOException
  {
    for (;;)
    {
      int i = readTag();
      if (i == 0) {}
      while (!skipField(i)) {
        return;
      }
    }
  }
  
  public void skipRawBytes(int paramInt)
    throws IOException
  {
    if (paramInt >= 0)
    {
      if (this.bufferPos + paramInt <= this.currentLimit)
      {
        if (paramInt <= this.bufferSize - this.bufferPos) {
          break label55;
        }
        throw InvalidProtocolBufferNanoException.truncatedMessage();
      }
    }
    else {
      throw InvalidProtocolBufferNanoException.negativeSize();
    }
    skipRawBytes(this.currentLimit - this.bufferPos);
    throw InvalidProtocolBufferNanoException.truncatedMessage();
    label55:
    this.bufferPos += paramInt;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\protobuf\nano\CodedInputByteBufferNano.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */