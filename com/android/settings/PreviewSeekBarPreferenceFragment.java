package com.android.settings;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.android.settings.widget.DotsPageIndicator;
import com.android.settings.widget.LabeledSeekBar;

public abstract class PreviewSeekBarPreferenceFragment
  extends SettingsPreferenceFragment
{
  protected int mActivityLayoutResId;
  protected int mCurrentIndex;
  protected String[] mEntries;
  protected int mInitialIndex;
  private TextView mLabel;
  private View mLarger;
  private DotsPageIndicator mPageIndicator;
  private ViewPager.OnPageChangeListener mPageIndicatorPageChangeListener = new ViewPager.OnPageChangeListener()
  {
    public void onPageScrollStateChanged(int paramAnonymousInt) {}
    
    public void onPageScrolled(int paramAnonymousInt1, float paramAnonymousFloat, int paramAnonymousInt2) {}
    
    public void onPageSelected(int paramAnonymousInt)
    {
      PreviewSeekBarPreferenceFragment.-wrap0(PreviewSeekBarPreferenceFragment.this, paramAnonymousInt);
    }
  };
  private ViewPager.OnPageChangeListener mPreviewPageChangeListener = new ViewPager.OnPageChangeListener()
  {
    public void onPageScrollStateChanged(int paramAnonymousInt) {}
    
    public void onPageScrolled(int paramAnonymousInt1, float paramAnonymousFloat, int paramAnonymousInt2) {}
    
    public void onPageSelected(int paramAnonymousInt)
    {
      PreviewSeekBarPreferenceFragment.-get0(PreviewSeekBarPreferenceFragment.this).sendAccessibilityEvent(16384);
    }
  };
  private ViewPager mPreviewPager;
  private PreviewPagerAdapter mPreviewPagerAdapter;
  protected int[] mPreviewSampleResIds;
  private View mSmaller;
  
  private void setPagerIndicatorContentDescription(int paramInt)
  {
    this.mPageIndicator.setContentDescription(getPrefContext().getString(2131690828, new Object[] { Integer.valueOf(paramInt + 1), Integer.valueOf(this.mPreviewSampleResIds.length) }));
  }
  
  private void setPreviewLayer(int paramInt, boolean paramBoolean)
  {
    boolean bool2 = true;
    this.mLabel.setText(this.mEntries[paramInt]);
    View localView = this.mSmaller;
    if (paramInt > 0)
    {
      bool1 = true;
      localView.setEnabled(bool1);
      localView = this.mLarger;
      if (paramInt >= this.mEntries.length - 1) {
        break label102;
      }
    }
    label102:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      localView.setEnabled(bool1);
      setPagerIndicatorContentDescription(this.mPreviewPager.getCurrentItem());
      this.mPreviewPagerAdapter.setPreviewLayer(paramInt, this.mCurrentIndex, this.mPreviewPager.getCurrentItem(), paramBoolean);
      this.mCurrentIndex = paramInt;
      return;
      bool1 = false;
      break;
    }
  }
  
  protected abstract void commit();
  
  protected abstract Configuration createConfig(Configuration paramConfiguration, int paramInt);
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, final Bundle paramBundle)
  {
    paramViewGroup = super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
    paramBundle = (ViewGroup)paramViewGroup.findViewById(16908351);
    paramBundle.removeAllViews();
    paramLayoutInflater = paramLayoutInflater.inflate(this.mActivityLayoutResId, paramBundle, false);
    paramBundle.addView(paramLayoutInflater);
    this.mLabel = ((TextView)paramLayoutInflater.findViewById(2131362162));
    int i = Math.max(1, this.mEntries.length - 1);
    paramBundle = (LabeledSeekBar)paramLayoutInflater.findViewById(2131362164);
    paramBundle.setLabels(this.mEntries);
    paramBundle.setMax(i);
    paramBundle.setProgress(this.mInitialIndex);
    paramBundle.setOnSeekBarChangeListener(new onPreviewSeekBarChangeListener(null));
    this.mSmaller = paramLayoutInflater.findViewById(2131362163);
    this.mSmaller.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        int i = paramBundle.getProgress();
        if (i > 0) {
          paramBundle.setProgress(i - 1, true);
        }
      }
    });
    this.mLarger = paramLayoutInflater.findViewById(2131362165);
    this.mLarger.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        int i = paramBundle.getProgress();
        if (i < paramBundle.getMax()) {
          paramBundle.setProgress(i + 1, true);
        }
      }
    });
    if (this.mEntries.length == 1) {
      paramBundle.setEnabled(false);
    }
    paramBundle = getPrefContext();
    Configuration localConfiguration = paramBundle.getResources().getConfiguration();
    if (localConfiguration.getLayoutDirection() == 1) {}
    Configuration[] arrayOfConfiguration;
    for (boolean bool = true;; bool = false)
    {
      arrayOfConfiguration = new Configuration[this.mEntries.length];
      i = 0;
      while (i < this.mEntries.length)
      {
        arrayOfConfiguration[i] = createConfig(localConfiguration, i);
        i += 1;
      }
    }
    this.mPreviewPager = ((ViewPager)paramLayoutInflater.findViewById(2131362459));
    this.mPreviewPagerAdapter = new PreviewPagerAdapter(paramBundle, bool, this.mPreviewSampleResIds, arrayOfConfiguration);
    this.mPreviewPager.setAdapter(this.mPreviewPagerAdapter);
    paramBundle = this.mPreviewPager;
    if (bool)
    {
      i = this.mPreviewSampleResIds.length - 1;
      paramBundle.setCurrentItem(i);
      this.mPreviewPager.addOnPageChangeListener(this.mPreviewPageChangeListener);
      this.mPageIndicator = ((DotsPageIndicator)paramLayoutInflater.findViewById(2131362161));
      if (this.mPreviewSampleResIds.length <= 1) {
        break label401;
      }
      this.mPageIndicator.setViewPager(this.mPreviewPager);
      this.mPageIndicator.setVisibility(0);
      this.mPageIndicator.setOnPageChangeListener(this.mPageIndicatorPageChangeListener);
    }
    for (;;)
    {
      setPreviewLayer(this.mInitialIndex, false);
      return paramViewGroup;
      i = 0;
      break;
      label401:
      this.mPageIndicator.setVisibility(8);
    }
  }
  
  private class onPreviewSeekBarChangeListener
    implements SeekBar.OnSeekBarChangeListener
  {
    private boolean mSeekByTouch;
    
    private onPreviewSeekBarChangeListener() {}
    
    public void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean)
    {
      PreviewSeekBarPreferenceFragment.-wrap1(PreviewSeekBarPreferenceFragment.this, paramInt, false);
      if (!this.mSeekByTouch) {
        PreviewSeekBarPreferenceFragment.this.commit();
      }
    }
    
    public void onStartTrackingTouch(SeekBar paramSeekBar)
    {
      this.mSeekByTouch = true;
    }
    
    public void onStopTrackingTouch(SeekBar paramSeekBar)
    {
      if (PreviewSeekBarPreferenceFragment.-get1(PreviewSeekBarPreferenceFragment.this).isAnimating()) {
        PreviewSeekBarPreferenceFragment.-get1(PreviewSeekBarPreferenceFragment.this).setAnimationEndAction(new Runnable()
        {
          public void run()
          {
            PreviewSeekBarPreferenceFragment.this.commit();
          }
        });
      }
      for (;;)
      {
        this.mSeekByTouch = false;
        return;
        PreviewSeekBarPreferenceFragment.this.commit();
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\PreviewSeekBarPreferenceFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */