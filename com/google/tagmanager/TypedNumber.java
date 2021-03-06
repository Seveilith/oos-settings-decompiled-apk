package com.google.tagmanager;

class TypedNumber
  extends Number
  implements Comparable<TypedNumber>
{
  private double mDouble;
  private long mInt64;
  private boolean mIsInt64;
  
  private TypedNumber(double paramDouble)
  {
    this.mDouble = paramDouble;
    this.mIsInt64 = false;
  }
  
  private TypedNumber(long paramLong)
  {
    this.mInt64 = paramLong;
    this.mIsInt64 = true;
  }
  
  public static TypedNumber numberWithDouble(Double paramDouble)
  {
    return new TypedNumber(paramDouble.doubleValue());
  }
  
  public static TypedNumber numberWithInt64(long paramLong)
  {
    return new TypedNumber(paramLong);
  }
  
  public static TypedNumber numberWithString(String paramString)
    throws NumberFormatException
  {
    try
    {
      TypedNumber localTypedNumber1 = new TypedNumber(Long.parseLong(paramString));
      return localTypedNumber1;
    }
    catch (NumberFormatException localNumberFormatException1)
    {
      try
      {
        TypedNumber localTypedNumber2 = new TypedNumber(Double.parseDouble(paramString));
        return localTypedNumber2;
      }
      catch (NumberFormatException localNumberFormatException2)
      {
        throw new NumberFormatException(paramString + " is not a valid TypedNumber");
      }
    }
  }
  
  public byte byteValue()
  {
    return (byte)(int)longValue();
  }
  
  public int compareTo(TypedNumber paramTypedNumber)
  {
    if (!isInt64()) {}
    while (!paramTypedNumber.isInt64()) {
      return Double.compare(doubleValue(), paramTypedNumber.doubleValue());
    }
    return new Long(this.mInt64).compareTo(Long.valueOf(paramTypedNumber.mInt64));
  }
  
  public double doubleValue()
  {
    if (!isInt64()) {
      return this.mDouble;
    }
    return this.mInt64;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof TypedNumber)) {}
    while (compareTo((TypedNumber)paramObject) != 0) {
      return false;
    }
    return true;
  }
  
  public float floatValue()
  {
    return (float)doubleValue();
  }
  
  public int hashCode()
  {
    return new Long(longValue()).hashCode();
  }
  
  public short int16Value()
  {
    return (short)(int)longValue();
  }
  
  public int int32Value()
  {
    return (int)longValue();
  }
  
  public long int64Value()
  {
    if (!isInt64()) {
      return this.mDouble;
    }
    return this.mInt64;
  }
  
  public int intValue()
  {
    return int32Value();
  }
  
  public boolean isDouble()
  {
    return !isInt64();
  }
  
  public boolean isInt64()
  {
    return this.mIsInt64;
  }
  
  public long longValue()
  {
    return int64Value();
  }
  
  public short shortValue()
  {
    return int16Value();
  }
  
  public String toString()
  {
    if (!isInt64()) {
      return Double.toString(this.mDouble);
    }
    return Long.toString(this.mInt64);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\TypedNumber.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */