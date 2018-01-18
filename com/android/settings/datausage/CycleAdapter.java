package com.android.settings.datausage;

import android.content.Context;
import android.net.NetworkPolicy;
import android.net.NetworkPolicyManager;
import android.net.NetworkStatsHistory;
import android.net.NetworkStatsHistory.Entry;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import com.android.settings.Utils;
import com.android.settingslib.net.ChartData;
import libcore.util.Objects;

public class CycleAdapter
  extends ArrayAdapter<CycleItem>
{
  private final AdapterView.OnItemSelectedListener mListener;
  private final SpinnerInterface mSpinner;
  
  public CycleAdapter(Context paramContext, SpinnerInterface paramSpinnerInterface, AdapterView.OnItemSelectedListener paramOnItemSelectedListener, boolean paramBoolean) {}
  
  public int findNearestPosition(CycleItem paramCycleItem)
  {
    if (paramCycleItem != null)
    {
      int i = getCount() - 1;
      while (i >= 0)
      {
        if (((CycleItem)getItem(i)).compareTo(paramCycleItem) >= 0) {
          return i;
        }
        i -= 1;
      }
    }
    return 0;
  }
  
  public boolean updateCycleList(NetworkPolicy paramNetworkPolicy, ChartData paramChartData)
  {
    CycleItem localCycleItem = (CycleItem)this.mSpinner.getSelectedItem();
    clear();
    Context localContext = getContext();
    Object localObject = null;
    NetworkStatsHistory.Entry localEntry1 = null;
    long l1 = Long.MAX_VALUE;
    long l2 = Long.MIN_VALUE;
    if (paramChartData != null)
    {
      l1 = paramChartData.network.getStart();
      l2 = paramChartData.network.getEnd();
    }
    long l4 = System.currentTimeMillis();
    long l3 = l1;
    if (l1 == Long.MAX_VALUE) {
      l3 = l4;
    }
    l1 = l2;
    if (l2 == Long.MIN_VALUE) {
      l1 = l4 + 1L;
    }
    int j = 0;
    int i = 0;
    if (paramNetworkPolicy != null) {
      for (l4 = NetworkPolicyManager.computeNextCycleBoundary(l1, paramNetworkPolicy);; l4 = l2)
      {
        localObject = localEntry1;
        j = i;
        if (l4 <= l3) {
          break label241;
        }
        l2 = 0L;
        try
        {
          long l5 = NetworkPolicyManager.computeLastCycleBoundary(l4, paramNetworkPolicy);
          l2 = l5;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            localException.printStackTrace();
            continue;
            j = 0;
            continue;
            j = 1;
          }
        }
        if (paramChartData == null) {
          break label235;
        }
        localEntry1 = paramChartData.network.getValues(l2, l4, localEntry1);
        if (localEntry1.rxBytes + localEntry1.txBytes <= 0L) {
          break;
        }
        j = 1;
        if (j != 0)
        {
          add(new CycleItem(localContext, l2, l4));
          i = 1;
        }
      }
    }
    label235:
    label241:
    if (j == 0) {
      if (l1 > l3)
      {
        l2 = l1 - 2419200000L;
        if (paramChartData != null)
        {
          NetworkStatsHistory.Entry localEntry2 = paramChartData.network.getValues(l2, l1, localException);
          if (localEntry2.rxBytes + localEntry2.txBytes > 0L) {
            i = 1;
          }
        }
        for (;;)
        {
          if (i != 0) {
            add(new CycleItem(localContext, l2, l1));
          }
          l1 = l2;
          break;
          i = 0;
          continue;
          i = 1;
        }
      }
    }
    if (getCount() > 0)
    {
      i = findNearestPosition(localCycleItem);
      this.mSpinner.setSelection(i);
      if (!Objects.equal((CycleItem)getItem(i), localCycleItem))
      {
        this.mListener.onItemSelected(null, null, i, 0L);
        return false;
      }
    }
    return true;
  }
  
  public static class CycleItem
    implements Comparable<CycleItem>
  {
    public long end;
    public CharSequence label;
    public long start;
    
    public CycleItem(Context paramContext, long paramLong1, long paramLong2)
    {
      this.label = Utils.formatDateRange(paramContext, paramLong1, paramLong2);
      this.start = paramLong1;
      this.end = paramLong2;
    }
    
    public CycleItem(CharSequence paramCharSequence)
    {
      this.label = paramCharSequence;
    }
    
    public int compareTo(CycleItem paramCycleItem)
    {
      return Long.compare(this.start, paramCycleItem.start);
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      if ((paramObject instanceof CycleItem))
      {
        paramObject = (CycleItem)paramObject;
        boolean bool1 = bool2;
        if (this.start == ((CycleItem)paramObject).start)
        {
          bool1 = bool2;
          if (this.end == ((CycleItem)paramObject).end) {
            bool1 = true;
          }
        }
        return bool1;
      }
      return false;
    }
    
    public String toString()
    {
      return this.label.toString();
    }
  }
  
  public static abstract interface SpinnerInterface
  {
    public abstract Object getSelectedItem();
    
    public abstract void setAdapter(CycleAdapter paramCycleAdapter);
    
    public abstract void setOnItemSelectedListener(AdapterView.OnItemSelectedListener paramOnItemSelectedListener);
    
    public abstract void setSelection(int paramInt);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\CycleAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */