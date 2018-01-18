package com.oneplus.settings.ui;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import java.util.ArrayList;

public class OPScreenBetterPreference
  extends Preference
{
  private static final int layoutResId = 2130968852;
  private int currIndex = 0;
  private Context mContext;
  private ImageView mPage0;
  private ImageView mPage1;
  private ImageView mPage2;
  private ViewPager mViewPager;
  
  public OPScreenBetterPreference(Context paramContext)
  {
    super(paramContext);
    initView(paramContext);
  }
  
  public OPScreenBetterPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initView(paramContext);
  }
  
  public OPScreenBetterPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initView(paramContext);
  }
  
  private void initView(Context paramContext)
  {
    setLayoutResource(2130968852);
    this.mContext = paramContext;
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    this.mViewPager = ((ViewPager)paramPreferenceViewHolder.findViewById(2131362380));
    this.mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
    this.mPage0 = ((ImageView)paramPreferenceViewHolder.findViewById(2131362381));
    this.mPage1 = ((ImageView)paramPreferenceViewHolder.findViewById(2131362382));
    this.mPage2 = ((ImageView)paramPreferenceViewHolder.findViewById(2131362383));
    Object localObject = LayoutInflater.from(this.mContext);
    paramPreferenceViewHolder = ((LayoutInflater)localObject).inflate(2130968855, null);
    View localView = ((LayoutInflater)localObject).inflate(2130968857, null);
    localObject = ((LayoutInflater)localObject).inflate(2130968856, null);
    final ArrayList localArrayList = new ArrayList();
    localArrayList.add(paramPreferenceViewHolder);
    localArrayList.add(localView);
    localArrayList.add(localObject);
    paramPreferenceViewHolder = new PagerAdapter()
    {
      public void destroyItem(View paramAnonymousView, int paramAnonymousInt, Object paramAnonymousObject)
      {
        ((ViewPager)paramAnonymousView).removeView((View)localArrayList.get(paramAnonymousInt));
      }
      
      public int getCount()
      {
        return localArrayList.size();
      }
      
      public Object instantiateItem(View paramAnonymousView, int paramAnonymousInt)
      {
        ((ViewPager)paramAnonymousView).addView((View)localArrayList.get(paramAnonymousInt));
        return localArrayList.get(paramAnonymousInt);
      }
      
      public boolean isViewFromObject(View paramAnonymousView, Object paramAnonymousObject)
      {
        return paramAnonymousView == paramAnonymousObject;
      }
    };
    this.mViewPager.setAdapter(paramPreferenceViewHolder);
    this.mViewPager.setCurrentItem(this.currIndex);
  }
  
  public class MyOnPageChangeListener
    implements ViewPager.OnPageChangeListener
  {
    public MyOnPageChangeListener() {}
    
    public void onPageScrollStateChanged(int paramInt) {}
    
    public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2) {}
    
    public void onPageSelected(int paramInt)
    {
      switch (paramInt)
      {
      }
      for (;;)
      {
        OPScreenBetterPreference.-set0(OPScreenBetterPreference.this, paramInt);
        return;
        OPScreenBetterPreference.-get1(OPScreenBetterPreference.this).setImageDrawable(OPScreenBetterPreference.-get0(OPScreenBetterPreference.this).getResources().getDrawable(2130838242));
        OPScreenBetterPreference.-get2(OPScreenBetterPreference.this).setImageDrawable(OPScreenBetterPreference.-get0(OPScreenBetterPreference.this).getResources().getDrawable(2130838241));
        continue;
        OPScreenBetterPreference.-get2(OPScreenBetterPreference.this).setImageDrawable(OPScreenBetterPreference.-get0(OPScreenBetterPreference.this).getResources().getDrawable(2130838242));
        OPScreenBetterPreference.-get1(OPScreenBetterPreference.this).setImageDrawable(OPScreenBetterPreference.-get0(OPScreenBetterPreference.this).getResources().getDrawable(2130838241));
        OPScreenBetterPreference.-get3(OPScreenBetterPreference.this).setImageDrawable(OPScreenBetterPreference.-get0(OPScreenBetterPreference.this).getResources().getDrawable(2130838241));
        continue;
        OPScreenBetterPreference.-get3(OPScreenBetterPreference.this).setImageDrawable(OPScreenBetterPreference.-get0(OPScreenBetterPreference.this).getResources().getDrawable(2130838242));
        OPScreenBetterPreference.-get2(OPScreenBetterPreference.this).setImageDrawable(OPScreenBetterPreference.-get0(OPScreenBetterPreference.this).getResources().getDrawable(2130838241));
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ui\OPScreenBetterPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */