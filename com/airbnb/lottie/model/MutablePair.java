package com.airbnb.lottie.model;

import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

public class MutablePair<T>
{
  @Nullable
  T first;
  @Nullable
  T second;
  
  private static boolean objectsEqual(Object paramObject1, Object paramObject2)
  {
    boolean bool = false;
    if (paramObject1 == paramObject2) {}
    do
    {
      bool = true;
      do
      {
        return bool;
      } while (paramObject1 == null);
    } while (paramObject1.equals(paramObject2));
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Pair))
    {
      paramObject = (Pair)paramObject;
      if (objectsEqual(((Pair)paramObject).first, this.first)) {
        break label30;
      }
    }
    label30:
    while (!objectsEqual(((Pair)paramObject).second, this.second))
    {
      return false;
      return false;
    }
    return true;
  }
  
  public int hashCode()
  {
    int j = 0;
    if (this.first != null) {}
    for (int i = this.first.hashCode();; i = 0)
    {
      if (this.second != null) {
        j = this.second.hashCode();
      }
      return i ^ j;
    }
  }
  
  public void set(T paramT1, T paramT2)
  {
    this.first = paramT1;
    this.second = paramT2;
  }
  
  public String toString()
  {
    return "Pair{" + String.valueOf(this.first) + " " + String.valueOf(this.second) + "}";
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\MutablePair.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */