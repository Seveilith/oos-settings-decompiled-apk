package android.support.v4.util;

public class Pair<F, S>
{
  public final F first;
  public final S second;
  
  public Pair(F paramF, S paramS)
  {
    this.first = paramF;
    this.second = paramS;
  }
  
  public static <A, B> Pair<A, B> create(A paramA, B paramB)
  {
    return new Pair(paramA, paramB);
  }
  
  private static boolean objectsEqual(Object paramObject1, Object paramObject2)
  {
    if (paramObject1 != paramObject2)
    {
      if (paramObject1 != null) {
        return paramObject1.equals(paramObject2);
      }
    }
    else {
      return true;
    }
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if (!(paramObject instanceof Pair)) {
      return false;
    }
    paramObject = (Pair)paramObject;
    if (objectsEqual(((Pair)paramObject).first, this.first)) {
      bool = objectsEqual(((Pair)paramObject).second, this.second);
    }
    return bool;
  }
  
  public int hashCode()
  {
    int j = 0;
    int i;
    if (this.first == null)
    {
      i = 0;
      if (this.second != null) {
        break label33;
      }
    }
    for (;;)
    {
      return i ^ j;
      i = this.first.hashCode();
      break;
      label33:
      j = this.second.hashCode();
    }
  }
  
  public String toString()
  {
    return "Pair{" + String.valueOf(this.first) + " " + String.valueOf(this.second) + "}";
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\util\Pair.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */