package com.oneplus.settings.product;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class NovicePagerAdapter
  extends PagerAdapter
{
  public List<View> mListViews = null;
  
  public NovicePagerAdapter(List<View> paramList)
  {
    this.mListViews = paramList;
  }
  
  public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
  {
    try
    {
      ((ViewPager)paramViewGroup).removeView((View)this.mListViews.get(paramInt));
      return;
    }
    catch (Exception paramViewGroup) {}
  }
  
  public int getCount()
  {
    if (this.mListViews != null) {
      return this.mListViews.size();
    }
    return 0;
  }
  
  public Object instantiateItem(ViewGroup paramViewGroup, int paramInt)
  {
    ((ViewPager)paramViewGroup).addView((View)this.mListViews.get(paramInt), 0);
    return this.mListViews.get(paramInt);
  }
  
  public boolean isViewFromObject(View paramView, Object paramObject)
  {
    return paramView == paramObject;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\product\NovicePagerAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */