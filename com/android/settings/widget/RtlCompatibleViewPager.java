package com.android.settings.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import java.util.Locale;

public final class RtlCompatibleViewPager
  extends ViewPager
{
  public RtlCompatibleViewPager(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public RtlCompatibleViewPager(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public int getCurrentItem()
  {
    return getRtlAwareIndex(super.getCurrentItem());
  }
  
  public int getRtlAwareIndex(int paramInt)
  {
    if (TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1) {
      return getAdapter().getCount() - paramInt - 1;
    }
    return paramInt;
  }
  
  public void setCurrentItem(int paramInt)
  {
    super.setCurrentItem(getRtlAwareIndex(paramInt));
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\widget\RtlCompatibleViewPager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */