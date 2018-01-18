package com.oneplus.settings.product;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class ProductInfoActivity
  extends Activity
{
  private static int count;
  private TextView mCountTextView;
  private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener()
  {
    public void onPageScrollStateChanged(int paramAnonymousInt) {}
    
    public void onPageScrolled(int paramAnonymousInt1, float paramAnonymousFloat, int paramAnonymousInt2) {}
    
    public void onPageSelected(int paramAnonymousInt)
    {
      ProductInfoActivity.-wrap0(ProductInfoActivity.this, paramAnonymousInt);
    }
  };
  private NovicePagerAdapter mPagerAdapter = null;
  private ViewPager mViewPager = null;
  private List<View> mViews = null;
  
  public static String autoGenericCode(int paramInt1, int paramInt2)
  {
    return String.format("%0" + paramInt2 + "d", new Object[] { Integer.valueOf(paramInt1) });
  }
  
  private void initViews()
  {
    setContentView(2130968846);
    this.mViews = new ArrayList();
    LayoutInflater localLayoutInflater = LayoutInflater.from(this);
    boolean bool = OPUtils.isSurportProductInfo16859(getApplicationContext());
    int i = 0;
    if (i < 18)
    {
      View localView = localLayoutInflater.inflate(2130968845, null);
      ImageView localImageView = (ImageView)localView.findViewById(2131361891);
      if (bool) {}
      for (int j = getResources().getIdentifier("product_17801_" + autoGenericCode(i + 1, 2), "drawable", getPackageName());; j = getResources().getIdentifier("product_16859_" + autoGenericCode(i + 1, 2), "drawable", getPackageName()))
      {
        localImageView.setImageResource(j);
        this.mViews.add(localView);
        i += 1;
        break;
      }
    }
    this.mViewPager = ((ViewPager)findViewById(2131362371));
    this.mCountTextView = ((TextView)findViewById(2131362373));
    this.mCountTextView.setVisibility(4);
    this.mPagerAdapter = new NovicePagerAdapter(this.mViews);
    this.mViewPager.setAdapter(this.mPagerAdapter);
    this.mViewPager.setCurrentItem(0);
    this.mViewPager.setOnPageChangeListener(this.mPageChangeListener);
    count = this.mPagerAdapter.getCount();
    this.mCountTextView.setText("1/" + count);
  }
  
  private void updatePagerViews(int paramInt)
  {
    this.mCountTextView.setText(paramInt + 1 + "/" + count);
  }
  
  public void onAttachedToWindow()
  {
    super.onAttachedToWindow();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    getWindow().setFlags(1024, 1024);
    try
    {
      initViews();
      return;
    }
    catch (Exception paramBundle)
    {
      paramBundle.printStackTrace();
    }
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
  }
  
  protected void onPause()
  {
    super.onPause();
  }
  
  protected void onResume()
  {
    super.onResume();
  }
  
  protected void onStart()
  {
    super.onStart();
  }
  
  protected void onStop()
  {
    super.onStop();
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\product\ProductInfoActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */