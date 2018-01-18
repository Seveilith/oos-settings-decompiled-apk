package com.oneplus.lib.widget.listitem;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import com.oneplus.commonctrl.R.dimen;
import com.oneplus.commonctrl.R.style;

public final class OPListitemBuilder
{
  private static final boolean DEBUG = false;
  private static final String TAG = "OPListitem";
  private boolean mActionButtonEnabled = false;
  private Context mContext = null;
  private boolean mIconEnabled = false;
  private boolean mPrimaryTextEnabled = false;
  private boolean mSecondaryTextEnabled = false;
  private boolean mStampEnabled = false;
  
  public OPListitemBuilder(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  public OPListitem build()
  {
    OPListitemImpl localOPListitemImpl = new OPListitemImpl(this.mContext);
    localOPListitemImpl.setLayoutParams(new AbsListView.LayoutParams(-1, 216));
    return localOPListitemImpl;
  }
  
  public OPListitemBuilder reset()
  {
    this.mIconEnabled = false;
    this.mPrimaryTextEnabled = false;
    this.mSecondaryTextEnabled = false;
    this.mStampEnabled = false;
    this.mActionButtonEnabled = false;
    return this;
  }
  
  public OPListitemBuilder setActionButtonEnabled(boolean paramBoolean)
  {
    this.mActionButtonEnabled = paramBoolean;
    if (paramBoolean) {}
    for (paramBoolean = false;; paramBoolean = true)
    {
      this.mStampEnabled = paramBoolean;
      return this;
    }
  }
  
  public OPListitemBuilder setIconEnabled(boolean paramBoolean)
  {
    this.mIconEnabled = paramBoolean;
    return this;
  }
  
  public OPListitemBuilder setPrimaryTextEnabled(boolean paramBoolean)
  {
    this.mPrimaryTextEnabled = paramBoolean;
    return this;
  }
  
  public OPListitemBuilder setSecondaryTextEnabled(boolean paramBoolean)
  {
    this.mSecondaryTextEnabled = paramBoolean;
    return this;
  }
  
  public OPListitemBuilder setStampEnabled(boolean paramBoolean)
  {
    this.mStampEnabled = paramBoolean;
    if (paramBoolean) {}
    for (paramBoolean = false;; paramBoolean = true)
    {
      this.mActionButtonEnabled = paramBoolean;
      return this;
    }
  }
  
  private class OPListitemImpl
    extends OPListitem
  {
    private int mActionBtnSize = -1;
    private ImageView mActionButton = null;
    private Context mContext = null;
    private ImageView mIcon = null;
    private int mIconSize = -1;
    private int mMarginM1 = 0;
    private TextView mPrimaryText = null;
    private int mRemainHeight = 0;
    private Resources mResources = null;
    private TextView mSecondaryText = null;
    private TextView mStamp = null;
    
    public OPListitemImpl(Context paramContext)
    {
      super();
      this.mContext = paramContext;
      init();
    }
    
    private void init()
    {
      if (this.mContext == null) {
        return;
      }
      this.mResources = this.mContext.getResources();
      this.mMarginM1 = this.mResources.getDimensionPixelOffset(R.dimen.margin_m1);
      if (OPListitemBuilder.-get1(OPListitemBuilder.this))
      {
        this.mIcon = new ImageView(this.mContext);
        this.mIconSize = this.mResources.getDimensionPixelOffset(R.dimen.listitem_icon_size);
        this.mIcon.setLayoutParams(new ViewGroup.LayoutParams(this.mIconSize, this.mIconSize));
        addView(this.mIcon);
      }
      if (OPListitemBuilder.-get2(OPListitemBuilder.this))
      {
        this.mPrimaryText = new TextView(this.mContext, null, 0, R.style.listitem_primary_text_font);
        this.mPrimaryText.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        addView(this.mPrimaryText);
      }
      if (OPListitemBuilder.-get3(OPListitemBuilder.this))
      {
        this.mSecondaryText = new TextView(this.mContext, null, 0, R.style.listitem_secondary_text_font);
        this.mSecondaryText.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        addView(this.mSecondaryText);
      }
      if (OPListitemBuilder.-get4(OPListitemBuilder.this))
      {
        this.mStamp = new TextView(this.mContext, null, 0, R.style.listitem_stamp_font);
        this.mStamp.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        addView(this.mStamp);
      }
      if (OPListitemBuilder.-get0(OPListitemBuilder.this))
      {
        this.mActionButton = new ImageView(this.mContext);
        this.mActionBtnSize = this.mResources.getDimensionPixelOffset(R.dimen.listitem_actionbutton_size);
        this.mActionButton.setLayoutParams(new ViewGroup.LayoutParams(this.mActionBtnSize, this.mActionBtnSize));
        addView(this.mActionButton);
      }
    }
    
    private void layoutLTR(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramInt4 -= paramInt2;
      paramInt2 = paramInt1;
      int i;
      int j;
      if (this.mIcon != null)
      {
        paramInt1 += this.mMarginM1;
        i = (paramInt4 - this.mIconSize) / 2;
        paramInt2 = paramInt1 + this.mIconSize;
        j = this.mIconSize;
        this.mIcon.layout(paramInt1, i, paramInt2, i + j);
      }
      int k;
      if (this.mActionButton != null)
      {
        paramInt1 = paramInt3 - this.mMarginM1;
        i = this.mActionBtnSize;
        j = (paramInt4 - this.mActionBtnSize) / 2;
        k = this.mActionBtnSize;
        this.mActionButton.layout(paramInt1 - i, j, paramInt1, j + k);
      }
      if (this.mSecondaryText != null)
      {
        paramInt1 = paramInt4 - this.mRemainHeight / 2;
        i = this.mSecondaryText.getMeasuredHeight();
        j = paramInt2 + this.mMarginM1;
        k = this.mSecondaryText.getMeasuredWidth();
        this.mSecondaryText.layout(j, paramInt1 - i, j + k, paramInt1);
      }
      if (this.mPrimaryText != null)
      {
        paramInt1 = paramInt2 + this.mMarginM1;
        paramInt2 = this.mPrimaryText.getMeasuredWidth();
        i = this.mRemainHeight / 2;
        j = this.mPrimaryText.getMeasuredHeight();
        this.mPrimaryText.layout(paramInt1, i, paramInt1 + paramInt2, i + j);
      }
      if (this.mStamp != null)
      {
        paramInt2 = paramInt3 - this.mMarginM1;
        paramInt3 = this.mStamp.getMeasuredWidth();
        if (this.mSecondaryText == null) {
          break label317;
        }
      }
      label317:
      for (paramInt1 = this.mRemainHeight / 2 + (this.mPrimaryText.getMeasuredHeight() - this.mStamp.getMeasuredHeight()) / 2;; paramInt1 = (paramInt4 - this.mStamp.getMeasuredHeight()) / 2)
      {
        paramInt4 = this.mStamp.getMeasuredHeight();
        this.mStamp.layout(paramInt2 - paramInt3, paramInt1, paramInt2, paramInt1 + paramInt4);
        return;
      }
    }
    
    protected void dispatchDraw(Canvas paramCanvas)
    {
      super.dispatchDraw(paramCanvas);
    }
    
    public ImageView getActionButton()
    {
      return this.mActionButton;
    }
    
    public ImageView getIcon()
    {
      return this.mIcon;
    }
    
    public TextView getPrimaryText()
    {
      return this.mPrimaryText;
    }
    
    public TextView getSecondaryText()
    {
      return this.mSecondaryText;
    }
    
    public TextView getStamp()
    {
      return this.mStamp;
    }
    
    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      layoutLTR(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      setMeasuredDimension(paramInt1, paramInt2);
      int k = View.MeasureSpec.getSize(paramInt1);
      int j = View.MeasureSpec.getSize(paramInt2);
      paramInt1 = k;
      int i = j;
      if (this.mIcon != null)
      {
        this.mIcon.measure(View.MeasureSpec.makeMeasureSpec(this.mIconSize, 1073741824), View.MeasureSpec.makeMeasureSpec(this.mIconSize, 1073741824));
        paramInt1 = k - this.mIconSize - this.mMarginM1;
      }
      paramInt2 = paramInt1;
      if (this.mActionButton != null)
      {
        this.mActionButton.measure(View.MeasureSpec.makeMeasureSpec(this.mActionBtnSize, 1073741824), View.MeasureSpec.makeMeasureSpec(this.mActionBtnSize, 1073741824));
        paramInt2 = paramInt1 - this.mActionBtnSize - this.mMarginM1;
      }
      paramInt1 = paramInt2;
      if (this.mStamp != null)
      {
        this.mStamp.measure(View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(j, Integer.MIN_VALUE));
        paramInt1 = paramInt2 - this.mStamp.getMeasuredWidth() - this.mMarginM1;
      }
      paramInt2 = i;
      if (this.mPrimaryText != null)
      {
        this.mPrimaryText.measure(View.MeasureSpec.makeMeasureSpec(paramInt1 - this.mMarginM1 * 2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(j, Integer.MIN_VALUE));
        paramInt2 = j - this.mPrimaryText.getMeasuredHeight();
      }
      i = paramInt2;
      if (this.mSecondaryText != null)
      {
        i = paramInt1;
        if (this.mStamp != null) {
          i = paramInt1 + this.mStamp.getMeasuredWidth();
        }
        this.mSecondaryText.measure(View.MeasureSpec.makeMeasureSpec(i - this.mMarginM1 * 2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE));
        i = paramInt2 - this.mSecondaryText.getMeasuredHeight();
      }
      this.mRemainHeight = i;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\listitem\OPListitemBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */