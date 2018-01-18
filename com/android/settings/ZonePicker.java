package com.android.settings;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.android.settingslib.datetime.ZoneGetter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class ZonePicker
  extends ListFragment
{
  private static final int MENU_ALPHABETICAL = 1;
  private static final int MENU_TIMEZONE = 2;
  private static final String TAG = "ZonePicker";
  private SimpleAdapter mAlphabeticalAdapter;
  private ZoneSelectionListener mListener;
  private boolean mSortedByTimezone;
  private SimpleAdapter mTimezoneSortedAdapter;
  
  public static SimpleAdapter constructTimezoneAdapter(Context paramContext, boolean paramBoolean)
  {
    return constructTimezoneAdapter(paramContext, paramBoolean, 2130968681);
  }
  
  public static SimpleAdapter constructTimezoneAdapter(Context paramContext, boolean paramBoolean, int paramInt)
  {
    if (paramBoolean) {}
    for (Object localObject = "name";; localObject = "offset")
    {
      localObject = new MyComparator((String)localObject);
      List localList = ZoneGetter.getZonesList(paramContext);
      Collections.sort(localList, (Comparator)localObject);
      return new SimpleAdapter(paramContext, localList, paramInt, new String[] { "name", "gmt" }, new int[] { 16908308, 16908309 });
    }
  }
  
  public static int getTimeZoneIndex(SimpleAdapter paramSimpleAdapter, TimeZone paramTimeZone)
  {
    paramTimeZone = paramTimeZone.getID();
    int j = paramSimpleAdapter.getCount();
    int i = 0;
    while (i < j)
    {
      if (paramTimeZone.equals((String)((HashMap)paramSimpleAdapter.getItem(i)).get("id"))) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  public static TimeZone obtainTimeZoneFromItem(Object paramObject)
  {
    return TimeZone.getTimeZone((String)((Map)paramObject).get("id"));
  }
  
  private void setSorting(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (SimpleAdapter localSimpleAdapter = this.mTimezoneSortedAdapter;; localSimpleAdapter = this.mAlphabeticalAdapter)
    {
      setListAdapter(localSimpleAdapter);
      this.mSortedByTimezone = paramBoolean;
      int i = getTimeZoneIndex(localSimpleAdapter, TimeZone.getDefault());
      if (i >= 0) {
        setSelection(i);
      }
      return;
    }
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    paramBundle = getActivity();
    this.mTimezoneSortedAdapter = constructTimezoneAdapter(paramBundle, false);
    this.mAlphabeticalAdapter = constructTimezoneAdapter(paramBundle, true);
    setSorting(true);
    setHasOptionsMenu(true);
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    paramMenu.add(0, 1, 0, 2131691038).setIcon(17301660);
    paramMenu.add(0, 2, 0, 2131691039).setIcon(2130837997);
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
    Utils.forcePrepareCustomPreferencesList(paramViewGroup, paramLayoutInflater, (ListView)paramLayoutInflater.findViewById(16908298), false);
    return paramLayoutInflater;
  }
  
  public void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
  {
    if (!isResumed()) {
      return;
    }
    paramListView = (String)((Map)paramListView.getItemAtPosition(paramInt)).get("id");
    ((AlarmManager)getActivity().getSystemService("alarm")).setTimeZone(paramListView);
    paramListView = TimeZone.getTimeZone(paramListView);
    if (this.mListener != null)
    {
      this.mListener.onZoneSelected(paramListView);
      return;
    }
    getActivity().onBackPressed();
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return false;
    case 2: 
      setSorting(true);
      return true;
    }
    setSorting(false);
    return true;
  }
  
  public void onPrepareOptionsMenu(Menu paramMenu)
  {
    if (this.mSortedByTimezone)
    {
      paramMenu.findItem(2).setVisible(false);
      paramMenu.findItem(1).setVisible(true);
      return;
    }
    paramMenu.findItem(2).setVisible(true);
    paramMenu.findItem(1).setVisible(false);
  }
  
  public void setZoneSelectionListener(ZoneSelectionListener paramZoneSelectionListener)
  {
    this.mListener = paramZoneSelectionListener;
  }
  
  private static class MyComparator
    implements Comparator<Map<?, ?>>
  {
    private String mSortingKey;
    
    public MyComparator(String paramString)
    {
      this.mSortingKey = paramString;
    }
    
    private boolean isComparable(Object paramObject)
    {
      if (paramObject != null) {
        return paramObject instanceof Comparable;
      }
      return false;
    }
    
    public int compare(Map<?, ?> paramMap1, Map<?, ?> paramMap2)
    {
      paramMap1 = paramMap1.get(this.mSortingKey);
      paramMap2 = paramMap2.get(this.mSortingKey);
      if (!isComparable(paramMap1))
      {
        if (isComparable(paramMap2)) {
          return 1;
        }
        return 0;
      }
      if (!isComparable(paramMap2)) {
        return -1;
      }
      return ((Comparable)paramMap1).compareTo(paramMap2);
    }
    
    public void setSortingKey(String paramString)
    {
      this.mSortingKey = paramString;
    }
  }
  
  public static abstract interface ZoneSelectionListener
  {
    public abstract void onZoneSelected(TimeZone paramTimeZone);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ZonePicker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */