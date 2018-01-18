package android.support.v4.util;

import java.util.Map;

public class SimpleArrayMap<K, V>
{
  private static final int BASE_SIZE = 4;
  private static final int CACHE_SIZE = 10;
  private static final boolean DEBUG = false;
  private static final String TAG = "ArrayMap";
  static Object[] mBaseCache;
  static int mBaseCacheSize;
  static Object[] mTwiceBaseCache;
  static int mTwiceBaseCacheSize;
  Object[] mArray;
  int[] mHashes;
  int mSize;
  
  public SimpleArrayMap()
  {
    this.mHashes = ContainerHelpers.EMPTY_INTS;
    this.mArray = ContainerHelpers.EMPTY_OBJECTS;
    this.mSize = 0;
  }
  
  public SimpleArrayMap(int paramInt)
  {
    if (paramInt == 0)
    {
      this.mHashes = ContainerHelpers.EMPTY_INTS;
      this.mArray = ContainerHelpers.EMPTY_OBJECTS;
    }
    for (;;)
    {
      this.mSize = 0;
      return;
      allocArrays(paramInt);
    }
  }
  
  public SimpleArrayMap(SimpleArrayMap paramSimpleArrayMap)
  {
    this();
    if (paramSimpleArrayMap != null) {
      putAll(paramSimpleArrayMap);
    }
  }
  
  private void allocArrays(int paramInt)
  {
    Object localObject1;
    if (paramInt == 8) {
      localObject1 = ArrayMap.class;
    }
    for (;;)
    {
      try
      {
        if (mTwiceBaseCache != null)
        {
          localObject1 = mTwiceBaseCache;
          this.mArray = ((Object[])localObject1);
          mTwiceBaseCache = (Object[])localObject1[0];
          this.mHashes = ((int[])localObject1[1]);
          localObject1[1] = null;
          localObject1[0] = null;
          mTwiceBaseCacheSize -= 1;
          return;
        }
        this.mHashes = new int[paramInt];
        this.mArray = new Object[paramInt << 1];
        return;
      }
      finally {}
      if (paramInt != 4) {
        continue;
      }
      Object localObject3 = ArrayMap.class;
      try
      {
        if (mBaseCache == null) {
          continue;
        }
        localObject3 = mBaseCache;
        this.mArray = ((Object[])localObject3);
        mBaseCache = (Object[])localObject3[0];
        this.mHashes = ((int[])localObject3[1]);
        localObject3[1] = null;
        localObject3[0] = null;
        mBaseCacheSize -= 1;
        return;
      }
      finally {}
    }
  }
  
  private static void freeArrays(int[] paramArrayOfInt, Object[] paramArrayOfObject, int paramInt)
  {
    if (paramArrayOfInt.length == 8)
    {
      localClass2 = ArrayMap.class;
      localClass1 = localClass2;
    }
    label59:
    while (paramArrayOfInt.length != 4) {
      try
      {
        if (mTwiceBaseCacheSize < 10)
        {
          paramArrayOfObject[0] = mTwiceBaseCache;
          paramArrayOfObject[1] = paramArrayOfInt;
          paramInt = (paramInt << 1) - 1;
          break;
          mTwiceBaseCache = paramArrayOfObject;
          mTwiceBaseCacheSize += 1;
          localClass1 = localClass2;
        }
        return;
      }
      finally {}
    }
    Class localClass2 = ArrayMap.class;
    Class localClass1 = localClass2;
    for (;;)
    {
      try
      {
        if (mBaseCacheSize >= 10) {
          break label59;
        }
        paramArrayOfObject[0] = mBaseCache;
        paramArrayOfObject[1] = paramArrayOfInt;
        paramInt = (paramInt << 1) - 1;
      }
      finally {}
      mBaseCache = paramArrayOfObject;
      mBaseCacheSize += 1;
      localClass1 = localClass2;
      break label59;
      while (paramInt >= 2)
      {
        paramArrayOfObject[paramInt] = null;
        paramInt -= 1;
      }
      break;
      while (paramInt >= 2)
      {
        paramArrayOfObject[paramInt] = null;
        paramInt -= 1;
      }
    }
  }
  
  public void clear()
  {
    if (this.mSize != 0)
    {
      freeArrays(this.mHashes, this.mArray, this.mSize);
      this.mHashes = ContainerHelpers.EMPTY_INTS;
      this.mArray = ContainerHelpers.EMPTY_OBJECTS;
      this.mSize = 0;
    }
  }
  
  public boolean containsKey(Object paramObject)
  {
    boolean bool = false;
    if (indexOfKey(paramObject) >= 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean containsValue(Object paramObject)
  {
    boolean bool = false;
    if (indexOfValue(paramObject) >= 0) {
      bool = true;
    }
    return bool;
  }
  
  public void ensureCapacity(int paramInt)
  {
    if (this.mHashes.length < paramInt)
    {
      int[] arrayOfInt = this.mHashes;
      Object[] arrayOfObject = this.mArray;
      allocArrays(paramInt);
      if (this.mSize > 0)
      {
        System.arraycopy(arrayOfInt, 0, this.mHashes, 0, this.mSize);
        System.arraycopy(arrayOfObject, 0, this.mArray, 0, this.mSize << 1);
      }
      freeArrays(arrayOfInt, arrayOfObject, this.mSize);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    int i;
    Object localObject1;
    Object localObject2;
    Object localObject3;
    boolean bool;
    if ((paramObject instanceof SimpleArrayMap))
    {
      paramObject = (SimpleArrayMap)paramObject;
      if (size() != ((SimpleArrayMap)paramObject).size()) {
        return false;
      }
      i = 0;
      try
      {
        if (i < this.mSize)
        {
          localObject1 = keyAt(i);
          localObject2 = valueAt(i);
          localObject3 = ((SimpleArrayMap)paramObject).get(localObject1);
          if (localObject2 == null)
          {
            if ((localObject3 != null) || (!((SimpleArrayMap)paramObject).containsKey(localObject1))) {
              break label224;
            }
            break label217;
          }
          bool = localObject2.equals(localObject3);
          if (bool) {
            break label217;
          }
          return false;
        }
      }
      catch (ClassCastException paramObject)
      {
        return false;
      }
      catch (NullPointerException paramObject)
      {
        return false;
      }
      return true;
    }
    if ((paramObject instanceof Map))
    {
      paramObject = (Map)paramObject;
      if (size() != ((Map)paramObject).size()) {
        return false;
      }
      i = 0;
    }
    for (;;)
    {
      try
      {
        if (i < this.mSize)
        {
          localObject1 = keyAt(i);
          localObject2 = valueAt(i);
          localObject3 = ((Map)paramObject).get(localObject1);
          if (localObject2 == null)
          {
            if ((localObject3 != null) || (!((Map)paramObject).containsKey(localObject1))) {
              break label233;
            }
            break label226;
          }
          bool = localObject2.equals(localObject3);
          if (bool) {
            break label226;
          }
          return false;
        }
      }
      catch (ClassCastException paramObject)
      {
        return false;
      }
      catch (NullPointerException paramObject)
      {
        return false;
      }
      return true;
      return false;
      label217:
      i += 1;
      break;
      label224:
      return false;
      label226:
      i += 1;
    }
    label233:
    return false;
  }
  
  public V get(Object paramObject)
  {
    int i = indexOfKey(paramObject);
    if (i >= 0) {
      return (V)this.mArray[((i << 1) + 1)];
    }
    return null;
  }
  
  public int hashCode()
  {
    int[] arrayOfInt = this.mHashes;
    Object[] arrayOfObject = this.mArray;
    int k = 0;
    int j = 0;
    int i = 1;
    int n = this.mSize;
    if (j < n)
    {
      Object localObject = arrayOfObject[i];
      int i1 = arrayOfInt[j];
      if (localObject == null) {}
      for (int m = 0;; m = localObject.hashCode())
      {
        k += (m ^ i1);
        j += 1;
        i += 2;
        break;
      }
    }
    return k;
  }
  
  int indexOf(Object paramObject, int paramInt)
  {
    int j = this.mSize;
    if (j == 0) {
      return -1;
    }
    int k = ContainerHelpers.binarySearch(this.mHashes, j, paramInt);
    if (k < 0) {
      return k;
    }
    if (paramObject.equals(this.mArray[(k << 1)])) {
      return k;
    }
    int i = k + 1;
    while ((i < j) && (this.mHashes[i] == paramInt))
    {
      if (paramObject.equals(this.mArray[(i << 1)])) {
        return i;
      }
      i += 1;
    }
    j = k - 1;
    while ((j >= 0) && (this.mHashes[j] == paramInt))
    {
      if (paramObject.equals(this.mArray[(j << 1)])) {
        return j;
      }
      j -= 1;
    }
    return i;
  }
  
  public int indexOfKey(Object paramObject)
  {
    if (paramObject == null) {
      return indexOfNull();
    }
    return indexOf(paramObject, paramObject.hashCode());
  }
  
  int indexOfNull()
  {
    int j = this.mSize;
    if (j == 0) {
      return -1;
    }
    int k = ContainerHelpers.binarySearch(this.mHashes, j, 0);
    if (k < 0) {
      return k;
    }
    if (this.mArray[(k << 1)] == null) {
      return k;
    }
    int i = k + 1;
    while ((i < j) && (this.mHashes[i] == 0))
    {
      if (this.mArray[(i << 1)] == null) {
        return i;
      }
      i += 1;
    }
    j = k - 1;
    while ((j >= 0) && (this.mHashes[j] == 0))
    {
      if (this.mArray[(j << 1)] == null) {
        return j;
      }
      j -= 1;
    }
    return i;
  }
  
  int indexOfValue(Object paramObject)
  {
    int j = this.mSize * 2;
    Object[] arrayOfObject = this.mArray;
    if (paramObject == null)
    {
      i = 1;
      while (i < j)
      {
        if (arrayOfObject[i] == null) {
          return i >> 1;
        }
        i += 2;
      }
    }
    int i = 1;
    while (i < j)
    {
      if (paramObject.equals(arrayOfObject[i])) {
        return i >> 1;
      }
      i += 2;
    }
    return -1;
  }
  
  public boolean isEmpty()
  {
    boolean bool = false;
    if (this.mSize <= 0) {
      bool = true;
    }
    return bool;
  }
  
  public K keyAt(int paramInt)
  {
    return (K)this.mArray[(paramInt << 1)];
  }
  
  public V put(K paramK, V paramV)
  {
    int j;
    if (paramK == null) {
      j = 0;
    }
    for (int i = indexOfNull(); i >= 0; i = indexOf(paramK, j))
    {
      i = (i << 1) + 1;
      paramK = this.mArray[i];
      this.mArray[i] = paramV;
      return paramK;
      j = paramK.hashCode();
    }
    int k = i;
    if (this.mSize >= this.mHashes.length)
    {
      if (this.mSize < 8) {
        break label261;
      }
      i = this.mSize + (this.mSize >> 1);
    }
    for (;;)
    {
      int[] arrayOfInt = this.mHashes;
      Object[] arrayOfObject = this.mArray;
      allocArrays(i);
      if (this.mHashes.length > 0)
      {
        System.arraycopy(arrayOfInt, 0, this.mHashes, 0, arrayOfInt.length);
        System.arraycopy(arrayOfObject, 0, this.mArray, 0, arrayOfObject.length);
      }
      freeArrays(arrayOfInt, arrayOfObject, this.mSize);
      if (k < this.mSize)
      {
        System.arraycopy(this.mHashes, k, this.mHashes, k + 1, this.mSize - k);
        System.arraycopy(this.mArray, k << 1, this.mArray, k + 1 << 1, this.mSize - k << 1);
      }
      this.mHashes[k] = j;
      this.mArray[(k << 1)] = paramK;
      this.mArray[((k << 1) + 1)] = paramV;
      this.mSize += 1;
      return null;
      label261:
      if (this.mSize >= 4) {
        i = 8;
      } else {
        i = 4;
      }
    }
  }
  
  public void putAll(SimpleArrayMap<? extends K, ? extends V> paramSimpleArrayMap)
  {
    int j = paramSimpleArrayMap.mSize;
    ensureCapacity(this.mSize + j);
    if (this.mSize == 0) {
      if (j > 0)
      {
        System.arraycopy(paramSimpleArrayMap.mHashes, 0, this.mHashes, 0, j);
        System.arraycopy(paramSimpleArrayMap.mArray, 0, this.mArray, 0, j << 1);
        this.mSize = j;
      }
    }
    for (;;)
    {
      return;
      int i = 0;
      while (i < j)
      {
        put(paramSimpleArrayMap.keyAt(i), paramSimpleArrayMap.valueAt(i));
        i += 1;
      }
    }
  }
  
  public V remove(Object paramObject)
  {
    int i = indexOfKey(paramObject);
    if (i >= 0) {
      return (V)removeAt(i);
    }
    return null;
  }
  
  public V removeAt(int paramInt)
  {
    Object localObject = this.mArray[((paramInt << 1) + 1)];
    if (this.mSize <= 1)
    {
      freeArrays(this.mHashes, this.mArray, this.mSize);
      this.mHashes = ContainerHelpers.EMPTY_INTS;
      this.mArray = ContainerHelpers.EMPTY_OBJECTS;
      this.mSize = 0;
      return (V)localObject;
    }
    if ((this.mHashes.length > 8) && (this.mSize < this.mHashes.length / 3))
    {
      if (this.mSize > 8) {}
      for (int i = this.mSize + (this.mSize >> 1);; i = 8)
      {
        int[] arrayOfInt = this.mHashes;
        Object[] arrayOfObject = this.mArray;
        allocArrays(i);
        this.mSize -= 1;
        if (paramInt > 0)
        {
          System.arraycopy(arrayOfInt, 0, this.mHashes, 0, paramInt);
          System.arraycopy(arrayOfObject, 0, this.mArray, 0, paramInt << 1);
        }
        if (paramInt >= this.mSize) {
          break;
        }
        System.arraycopy(arrayOfInt, paramInt + 1, this.mHashes, paramInt, this.mSize - paramInt);
        System.arraycopy(arrayOfObject, paramInt + 1 << 1, this.mArray, paramInt << 1, this.mSize - paramInt << 1);
        return (V)localObject;
      }
    }
    this.mSize -= 1;
    if (paramInt < this.mSize)
    {
      System.arraycopy(this.mHashes, paramInt + 1, this.mHashes, paramInt, this.mSize - paramInt);
      System.arraycopy(this.mArray, paramInt + 1 << 1, this.mArray, paramInt << 1, this.mSize - paramInt << 1);
    }
    this.mArray[(this.mSize << 1)] = null;
    this.mArray[((this.mSize << 1) + 1)] = null;
    return (V)localObject;
  }
  
  public V setValueAt(int paramInt, V paramV)
  {
    paramInt = (paramInt << 1) + 1;
    Object localObject = this.mArray[paramInt];
    this.mArray[paramInt] = paramV;
    return (V)localObject;
  }
  
  public int size()
  {
    return this.mSize;
  }
  
  public String toString()
  {
    if (isEmpty()) {
      return "{}";
    }
    StringBuilder localStringBuilder = new StringBuilder(this.mSize * 28);
    localStringBuilder.append('{');
    int i = 0;
    if (i < this.mSize)
    {
      if (i > 0) {
        localStringBuilder.append(", ");
      }
      Object localObject = keyAt(i);
      if (localObject != this)
      {
        localStringBuilder.append(localObject);
        label70:
        localStringBuilder.append('=');
        localObject = valueAt(i);
        if (localObject == this) {
          break label111;
        }
        localStringBuilder.append(localObject);
      }
      for (;;)
      {
        i += 1;
        break;
        localStringBuilder.append("(this Map)");
        break label70;
        label111:
        localStringBuilder.append("(this Map)");
      }
    }
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public V valueAt(int paramInt)
  {
    return (V)this.mArray[((paramInt << 1) + 1)];
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\util\SimpleArrayMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */