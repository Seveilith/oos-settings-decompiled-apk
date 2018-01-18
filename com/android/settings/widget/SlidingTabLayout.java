package com.android.settings.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class SlidingTabLayout
  extends FrameLayout
  implements View.OnClickListener
{
  private final View mIndicatorView;
  private final LayoutInflater mLayoutInflater;
  private int mSelectedPosition;
  private float mSelectionOffset;
  private final LinearLayout mTitleView;
  private RtlCompatibleViewPager mViewPager;
  
  public SlidingTabLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mLayoutInflater = LayoutInflater.from(paramContext);
    this.mTitleView = new LinearLayout(paramContext);
    this.mTitleView.setGravity(1);
    this.mIndicatorView = this.mLayoutInflater.inflate(2130969004, this, false);
    addView(this.mTitleView, -1, -2);
    addView(this.mIndicatorView, this.mIndicatorView.getLayoutParams());
  }
  
  private int getIndicatorLeft()
  {
    int j = this.mTitleView.getChildAt(this.mSelectedPosition).getLeft();
    int i = j;
    if (this.mSelectionOffset > 0.0F)
    {
      i = j;
      if (this.mSelectedPosition < getChildCount() - 1)
      {
        View localView = this.mTitleView.getChildAt(this.mSelectedPosition + 1);
        i = (int)(this.mSelectionOffset * localView.getLeft() + (1.0F - this.mSelectionOffset) * j);
      }
    }
    return i;
  }
  
  private boolean isRtlMode()
  {
    return getLayoutDirection() == 1;
  }
  
  private void onViewPagerPageChanged(int paramInt, float paramFloat)
  {
    this.mSelectedPosition = paramInt;
    this.mSelectionOffset = paramFloat;
    if (isRtlMode()) {}
    for (paramInt = -getIndicatorLeft();; paramInt = getIndicatorLeft())
    {
      this.mIndicatorView.setTranslationX(paramInt);
      return;
    }
  }
  
  private void populateTabStrip()
  {
    PagerAdapter localPagerAdapter = this.mViewPager.getAdapter();
    int i = 0;
    if (i < localPagerAdapter.getCount())
    {
      TextView localTextView = (TextView)this.mLayoutInflater.inflate(2130969005, this.mTitleView, false);
      localTextView.setText(localPagerAdapter.getPageTitle(i));
      localTextView.setOnClickListener(this);
      this.mTitleView.addView(localTextView);
      if (i == this.mViewPager.getCurrentItem()) {}
      for (boolean bool = true;; bool = false)
      {
        localTextView.setSelected(bool);
        i += 1;
        break;
      }
    }
  }
  
  public void onClick(View paramView)
  {
    int j = this.mTitleView.getChildCount();
    int i = 0;
    while (i < j)
    {
      if (paramView == this.mTitleView.getChildAt(i))
      {
        this.mViewPager.setCurrentItem(i);
        return;
      }
      i += 1;
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.mTitleView.getChildCount() > 0)
    {
      paramInt1 = getMeasuredHeight();
      paramInt2 = this.mIndicatorView.getMeasuredHeight();
      paramInt3 = this.mIndicatorView.getMeasuredWidth();
      paramInt4 = getMeasuredWidth();
      int i = getPaddingLeft();
      int j = getPaddingRight();
      this.mTitleView.layout(i, 0, this.mTitleView.getMeasuredWidth() + j, this.mTitleView.getMeasuredHeight());
      if (isRtlMode()) {
        this.mIndicatorView.layout(paramInt4 - paramInt3, paramInt1 - paramInt2, paramInt4, paramInt1);
      }
    }
    else
    {
      return;
    }
    this.mIndicatorView.layout(0, paramInt1 - paramInt2, paramInt3, paramInt1);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    paramInt1 = this.mTitleView.getChildCount();
    if (paramInt1 > 0)
    {
      paramInt1 = View.MeasureSpec.makeMeasureSpec(this.mTitleView.getMeasuredWidth() / paramInt1, 1073741824);
      paramInt2 = View.MeasureSpec.makeMeasureSpec(this.mIndicatorView.getMeasuredHeight(), 1073741824);
      this.mIndicatorView.measure(paramInt1, paramInt2);
    }
  }
  
  public void setViewPager(RtlCompatibleViewPager paramRtlCompatibleViewPager)
  {
    this.mTitleView.removeAllViews();
    this.mViewPager = paramRtlCompatibleViewPager;
    if (paramRtlCompatibleViewPager != null)
    {
      paramRtlCompatibleViewPager.addOnPageChangeListener(new InternalViewPagerListener(null));
      populateTabStrip();
    }
  }
  
  private final class InternalViewPagerListener
    implements ViewPager.OnPageChangeListener
  {
    private int mScrollState;
    
    private InternalViewPagerListener() {}
    
    public void onPageScrollStateChanged(int paramInt)
    {
      this.mScrollState = paramInt;
    }
    
    public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2)
    {
      paramInt2 = SlidingTabLayout.-get0(SlidingTabLayout.this).getChildCount();
      if ((paramInt2 == 0) || (paramInt1 < 0)) {}
      while (paramInt1 >= paramInt2) {
        return;
      }
      SlidingTabLayout.-wrap0(SlidingTabLayout.this, paramInt1, paramFloat);
    }
    
    public void onPageSelected(int paramInt)
    {
      int i = SlidingTabLayout.-get1(SlidingTabLayout.this).getRtlAwareIndex(paramInt);
      if (this.mScrollState == 0) {
        SlidingTabLayout.-wrap0(SlidingTabLayout.this, i, 0.0F);
      }
      int j = SlidingTabLayout.-get0(SlidingTabLayout.this).getChildCount();
      paramInt = 0;
      if (paramInt < j)
      {
        View localView = SlidingTabLayout.-get0(SlidingTabLayout.this).getChildAt(paramInt);
        if (i == paramInt) {}
        for (boolean bool = true;; bool = false)
        {
          localView.setSelected(bool);
          paramInt += 1;
          break;
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\widget\SlidingTabLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */