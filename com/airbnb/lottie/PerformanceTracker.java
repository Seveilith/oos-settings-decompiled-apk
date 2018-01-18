package com.airbnb.lottie;

import android.os.Build.VERSION;
import android.support.v4.util.Pair;
import android.util.ArraySet;
import android.util.Log;
import com.airbnb.lottie.utils.MeanCalculator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class PerformanceTracker
{
  private boolean enabled = false;
  private final Comparator<Pair<String, Float>> floatComparator = new Comparator()
  {
    public int compare(Pair<String, Float> paramAnonymousPair1, Pair<String, Float> paramAnonymousPair2)
    {
      float f1 = ((Float)paramAnonymousPair1.second).floatValue();
      float f2 = ((Float)paramAnonymousPair2.second).floatValue();
      if (f2 > f1) {
        return 1;
      }
      if (f1 > f2) {
        return -1;
      }
      return 0;
    }
  };
  private final Set<FrameListener> frameListeners = initSet();
  private Map<String, MeanCalculator> layerRenderTimes = new HashMap();
  
  private Set<FrameListener> initSet()
  {
    if (Build.VERSION.SDK_INT < 23) {
      return new HashSet();
    }
    return new ArraySet();
  }
  
  public void addFrameListener(FrameListener paramFrameListener)
  {
    this.frameListeners.add(paramFrameListener);
  }
  
  public void clearRenderTimes()
  {
    this.layerRenderTimes.clear();
  }
  
  public List<Pair<String, Float>> getSortedRenderTimes()
  {
    ArrayList localArrayList;
    Iterator localIterator;
    if (this.enabled)
    {
      localArrayList = new ArrayList(this.layerRenderTimes.size());
      localIterator = this.layerRenderTimes.entrySet().iterator();
    }
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        Collections.sort(localArrayList, this.floatComparator);
        return localArrayList;
        return Collections.emptyList();
      }
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      localArrayList.add(new Pair(localEntry.getKey(), Float.valueOf(((MeanCalculator)localEntry.getValue()).getMean())));
    }
  }
  
  public void logRenderTimes()
  {
    List localList;
    int i;
    if (this.enabled)
    {
      localList = getSortedRenderTimes();
      Log.d("LOTTIE", "Render times:");
      i = 0;
    }
    for (;;)
    {
      if (i >= localList.size())
      {
        return;
        return;
      }
      Pair localPair = (Pair)localList.get(i);
      Log.d("LOTTIE", String.format("\t\t%30s:%.2f", new Object[] { localPair.first, localPair.second }));
      i += 1;
    }
  }
  
  public void recordRenderTime(String paramString, float paramFloat)
  {
    MeanCalculator localMeanCalculator;
    if (this.enabled)
    {
      localMeanCalculator = (MeanCalculator)this.layerRenderTimes.get(paramString);
      if (localMeanCalculator == null) {
        break label41;
      }
      localMeanCalculator.add(paramFloat);
      if (paramString.equals("root")) {
        break label64;
      }
    }
    for (;;)
    {
      return;
      return;
      label41:
      localMeanCalculator = new MeanCalculator();
      this.layerRenderTimes.put(paramString, localMeanCalculator);
      break;
      label64:
      paramString = this.frameListeners.iterator();
      while (paramString.hasNext()) {
        ((FrameListener)paramString.next()).onFrameRendered(paramFloat);
      }
    }
  }
  
  public void removeFrameListener(FrameListener paramFrameListener)
  {
    this.frameListeners.add(paramFrameListener);
  }
  
  void setEnabled(boolean paramBoolean)
  {
    this.enabled = paramBoolean;
  }
  
  public static abstract interface FrameListener
  {
    public abstract void onFrameRendered(float paramFloat);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\PerformanceTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */