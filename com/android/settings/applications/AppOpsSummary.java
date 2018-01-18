package com.android.settings.applications;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceFrameLayout;
import android.preference.PreferenceFrameLayout.LayoutParams;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.settings.InstrumentedFragment;

public class AppOpsSummary
  extends InstrumentedFragment
{
  static AppOpsState.OpsTemplate[] sPageTemplates = { AppOpsState.LOCATION_TEMPLATE, AppOpsState.PERSONAL_TEMPLATE, AppOpsState.MESSAGING_TEMPLATE, AppOpsState.MEDIA_TEMPLATE, AppOpsState.DEVICE_TEMPLATE };
  private ViewGroup mContentContainer;
  int mCurPos;
  private LayoutInflater mInflater;
  CharSequence[] mPageNames;
  private View mRootView;
  private ViewPager mViewPager;
  
  protected int getMetricsCategory()
  {
    return 15;
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mInflater = paramLayoutInflater;
    paramLayoutInflater = this.mInflater.inflate(2130968618, paramViewGroup, false);
    this.mContentContainer = paramViewGroup;
    this.mRootView = paramLayoutInflater;
    this.mPageNames = getResources().getTextArray(2131427415);
    this.mViewPager = ((ViewPager)paramLayoutInflater.findViewById(2131361962));
    paramBundle = new MyPagerAdapter(getChildFragmentManager());
    this.mViewPager.setAdapter(paramBundle);
    this.mViewPager.setOnPageChangeListener(paramBundle);
    paramBundle = (PagerTabStrip)paramLayoutInflater.findViewById(2131361963);
    TypedArray localTypedArray = paramBundle.getContext().obtainStyledAttributes(new int[] { 16843829 });
    int i = localTypedArray.getColor(0, 0);
    localTypedArray.recycle();
    paramBundle.setTabIndicatorColorResource(i);
    if ((paramViewGroup instanceof PreferenceFrameLayout)) {
      ((PreferenceFrameLayout.LayoutParams)paramLayoutInflater.getLayoutParams()).removeBorders = true;
    }
    return paramLayoutInflater;
  }
  
  class MyPagerAdapter
    extends FragmentPagerAdapter
    implements ViewPager.OnPageChangeListener
  {
    public MyPagerAdapter(FragmentManager paramFragmentManager)
    {
      super();
    }
    
    public int getCount()
    {
      return AppOpsSummary.sPageTemplates.length;
    }
    
    public Fragment getItem(int paramInt)
    {
      return new AppOpsCategory(AppOpsSummary.sPageTemplates[paramInt]);
    }
    
    public CharSequence getPageTitle(int paramInt)
    {
      return AppOpsSummary.this.mPageNames[paramInt];
    }
    
    public void onPageScrollStateChanged(int paramInt)
    {
      if (paramInt == 0) {}
    }
    
    public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2) {}
    
    public void onPageSelected(int paramInt)
    {
      AppOpsSummary.this.mCurPos = paramInt;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\AppOpsSummary.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */