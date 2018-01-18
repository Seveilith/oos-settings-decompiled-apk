package android.support.v4.widget;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class FocusStrategy
{
  private static boolean beamBeats(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2, @NonNull Rect paramRect3)
  {
    boolean bool = beamsOverlap(paramInt, paramRect1, paramRect2);
    if ((!beamsOverlap(paramInt, paramRect1, paramRect3)) && (bool))
    {
      if (!isToDirectionOf(paramInt, paramRect1, paramRect3)) {
        return true;
      }
    }
    else {
      return false;
    }
    if ((paramInt == 17) || (paramInt == 66)) {
      return true;
    }
    return majorAxisDistance(paramInt, paramRect1, paramRect2) < majorAxisDistanceToFarEdge(paramInt, paramRect1, paramRect3);
  }
  
  private static boolean beamsOverlap(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2)
  {
    boolean bool2 = false;
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
    case 17: 
    case 66: 
      return (paramRect2.bottom >= paramRect1.top) && (paramRect2.top <= paramRect1.bottom);
    }
    boolean bool1 = bool2;
    if (paramRect2.right >= paramRect1.left)
    {
      bool1 = bool2;
      if (paramRect2.left <= paramRect1.right) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static <L, T> T findNextFocusInAbsoluteDirection(@NonNull L paramL, @NonNull CollectionAdapter<L, T> paramCollectionAdapter, @NonNull BoundsAdapter<T> paramBoundsAdapter, @Nullable T paramT, @NonNull Rect paramRect, int paramInt)
  {
    Rect localRect1 = new Rect(paramRect);
    Object localObject1;
    Rect localRect2;
    int i;
    label103:
    Object localObject2;
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
    case 17: 
      localRect1.offset(paramRect.width() + 1, 0);
      localObject1 = null;
      int j = paramCollectionAdapter.size(paramL);
      localRect2 = new Rect();
      i = 0;
      if (i >= j) {
        break label224;
      }
      localObject2 = paramCollectionAdapter.get(paramL, i);
      if (localObject2 != paramT) {
        break;
      }
    }
    for (;;)
    {
      i += 1;
      break label103;
      localRect1.offset(-(paramRect.width() + 1), 0);
      break;
      localRect1.offset(0, paramRect.height() + 1);
      break;
      localRect1.offset(0, -(paramRect.height() + 1));
      break;
      paramBoundsAdapter.obtainBounds(localObject2, localRect2);
      if (isBetterCandidate(paramInt, paramRect, localRect2, localRect1))
      {
        localRect1.set(localRect2);
        localObject1 = localObject2;
      }
    }
    label224:
    return (T)localObject1;
  }
  
  public static <L, T> T findNextFocusInRelativeDirection(@NonNull L paramL, @NonNull CollectionAdapter<L, T> paramCollectionAdapter, @NonNull BoundsAdapter<T> paramBoundsAdapter, @Nullable T paramT, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    int j = paramCollectionAdapter.size(paramL);
    ArrayList localArrayList = new ArrayList(j);
    int i = 0;
    while (i < j)
    {
      localArrayList.add(paramCollectionAdapter.get(paramL, i));
      i += 1;
    }
    Collections.sort(localArrayList, new SequentialComparator(paramBoolean1, paramBoundsAdapter));
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("direction must be one of {FOCUS_FORWARD, FOCUS_BACKWARD}.");
    case 2: 
      return (T)getNextFocusable(paramT, localArrayList, paramBoolean2);
    }
    return (T)getPreviousFocusable(paramT, localArrayList, paramBoolean2);
  }
  
  private static <T> T getNextFocusable(T paramT, ArrayList<T> paramArrayList, boolean paramBoolean)
  {
    int j = paramArrayList.size();
    if (paramT == null) {}
    for (int i = -1;; i = paramArrayList.lastIndexOf(paramT))
    {
      i += 1;
      if (i >= j) {
        break;
      }
      return (T)paramArrayList.get(i);
    }
    if ((paramBoolean) && (j > 0)) {
      return (T)paramArrayList.get(0);
    }
    return null;
  }
  
  private static <T> T getPreviousFocusable(T paramT, ArrayList<T> paramArrayList, boolean paramBoolean)
  {
    int j = paramArrayList.size();
    if (paramT == null) {}
    for (int i = j;; i = paramArrayList.indexOf(paramT))
    {
      i -= 1;
      if (i < 0) {
        break;
      }
      return (T)paramArrayList.get(i);
    }
    if ((paramBoolean) && (j > 0)) {
      return (T)paramArrayList.get(j - 1);
    }
    return null;
  }
  
  private static int getWeightedDistanceFor(int paramInt1, int paramInt2)
  {
    return paramInt1 * 13 * paramInt1 + paramInt2 * paramInt2;
  }
  
  private static boolean isBetterCandidate(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2, @NonNull Rect paramRect3)
  {
    if (!isCandidate(paramRect1, paramRect2, paramInt)) {
      return false;
    }
    if (!isCandidate(paramRect1, paramRect3, paramInt)) {
      return true;
    }
    if (beamBeats(paramInt, paramRect1, paramRect2, paramRect3)) {
      return true;
    }
    if (beamBeats(paramInt, paramRect1, paramRect3, paramRect2)) {
      return false;
    }
    return getWeightedDistanceFor(majorAxisDistance(paramInt, paramRect1, paramRect2), minorAxisDistance(paramInt, paramRect1, paramRect2)) < getWeightedDistanceFor(majorAxisDistance(paramInt, paramRect1, paramRect3), minorAxisDistance(paramInt, paramRect1, paramRect3));
  }
  
  private static boolean isCandidate(@NonNull Rect paramRect1, @NonNull Rect paramRect2, int paramInt)
  {
    boolean bool3 = true;
    boolean bool4 = true;
    boolean bool5 = true;
    boolean bool2 = true;
    boolean bool6 = false;
    boolean bool7 = false;
    boolean bool8 = false;
    boolean bool1 = false;
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
    case 17: 
      if ((paramRect1.right > paramRect2.right) || (paramRect1.left >= paramRect2.right)) {
        if (paramRect1.left <= paramRect2.left) {
          break label116;
        }
      }
      for (bool1 = bool2;; bool1 = false) {
        return bool1;
      }
    case 66: 
      if (paramRect1.left >= paramRect2.left)
      {
        bool1 = bool6;
        if (paramRect1.right > paramRect2.left) {}
      }
      else
      {
        if (paramRect1.right >= paramRect2.right) {
          break label162;
        }
      }
      for (bool1 = bool3;; bool1 = false) {
        return bool1;
      }
    case 33: 
      label116:
      label162:
      if (paramRect1.bottom <= paramRect2.bottom)
      {
        bool1 = bool7;
        if (paramRect1.top < paramRect2.bottom) {}
      }
      else
      {
        if (paramRect1.top <= paramRect2.top) {
          break label208;
        }
      }
      label208:
      for (bool1 = bool4;; bool1 = false) {
        return bool1;
      }
    }
    if (paramRect1.top >= paramRect2.top)
    {
      bool1 = bool8;
      if (paramRect1.bottom > paramRect2.top) {}
    }
    else
    {
      if (paramRect1.bottom >= paramRect2.bottom) {
        break label254;
      }
    }
    label254:
    for (bool1 = bool5;; bool1 = false) {
      return bool1;
    }
  }
  
  private static boolean isToDirectionOf(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
    case 17: 
      return paramRect1.left >= paramRect2.right;
    case 66: 
      return paramRect1.right <= paramRect2.left;
    case 33: 
      return paramRect1.top >= paramRect2.bottom;
    }
    return paramRect1.bottom <= paramRect2.top;
  }
  
  private static int majorAxisDistance(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2)
  {
    return Math.max(0, majorAxisDistanceRaw(paramInt, paramRect1, paramRect2));
  }
  
  private static int majorAxisDistanceRaw(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
    case 17: 
      return paramRect1.left - paramRect2.right;
    case 66: 
      return paramRect2.left - paramRect1.right;
    case 33: 
      return paramRect1.top - paramRect2.bottom;
    }
    return paramRect2.top - paramRect1.bottom;
  }
  
  private static int majorAxisDistanceToFarEdge(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2)
  {
    return Math.max(1, majorAxisDistanceToFarEdgeRaw(paramInt, paramRect1, paramRect2));
  }
  
  private static int majorAxisDistanceToFarEdgeRaw(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
    case 17: 
      return paramRect1.left - paramRect2.left;
    case 66: 
      return paramRect2.right - paramRect1.right;
    case 33: 
      return paramRect1.top - paramRect2.top;
    }
    return paramRect2.bottom - paramRect1.bottom;
  }
  
  private static int minorAxisDistance(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
    case 17: 
    case 66: 
      return Math.abs(paramRect1.top + paramRect1.height() / 2 - (paramRect2.top + paramRect2.height() / 2));
    }
    return Math.abs(paramRect1.left + paramRect1.width() / 2 - (paramRect2.left + paramRect2.width() / 2));
  }
  
  public static abstract interface BoundsAdapter<T>
  {
    public abstract void obtainBounds(T paramT, Rect paramRect);
  }
  
  public static abstract interface CollectionAdapter<T, V>
  {
    public abstract V get(T paramT, int paramInt);
    
    public abstract int size(T paramT);
  }
  
  private static class SequentialComparator<T>
    implements Comparator<T>
  {
    private final FocusStrategy.BoundsAdapter<T> mAdapter;
    private final boolean mIsLayoutRtl;
    private final Rect mTemp1 = new Rect();
    private final Rect mTemp2 = new Rect();
    
    SequentialComparator(boolean paramBoolean, FocusStrategy.BoundsAdapter<T> paramBoundsAdapter)
    {
      this.mIsLayoutRtl = paramBoolean;
      this.mAdapter = paramBoundsAdapter;
    }
    
    public int compare(T paramT1, T paramT2)
    {
      Rect localRect1 = this.mTemp1;
      Rect localRect2 = this.mTemp2;
      this.mAdapter.obtainBounds(paramT1, localRect1);
      this.mAdapter.obtainBounds(paramT2, localRect2);
      if (localRect1.top < localRect2.top) {
        return -1;
      }
      if (localRect1.top > localRect2.top) {
        return 1;
      }
      if (localRect1.left < localRect2.left)
      {
        if (this.mIsLayoutRtl) {
          return 1;
        }
        return -1;
      }
      if (localRect1.left > localRect2.left)
      {
        if (this.mIsLayoutRtl) {
          return -1;
        }
        return 1;
      }
      if (localRect1.bottom < localRect2.bottom) {
        return -1;
      }
      if (localRect1.bottom > localRect2.bottom) {
        return 1;
      }
      if (localRect1.right < localRect2.right)
      {
        if (this.mIsLayoutRtl) {
          return 1;
        }
        return -1;
      }
      if (localRect1.right > localRect2.right)
      {
        if (this.mIsLayoutRtl) {
          return -1;
        }
        return 1;
      }
      return 0;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\widget\FocusStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */