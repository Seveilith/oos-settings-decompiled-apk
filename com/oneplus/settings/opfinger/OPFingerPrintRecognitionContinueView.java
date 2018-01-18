package com.oneplus.settings.opfinger;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import com.oneplus.settings.utils.OPUtils;

public class OPFingerPrintRecognitionContinueView
  extends FrameLayout
{
  private Context mContext;
  private FrameLayout mFingerPrintView;
  private LayoutInflater mLayoutInflater;
  private SvgView mSvgView_11;
  private SvgView mSvgView_12;
  private SvgView mSvgView_13;
  private SvgView mSvgView_14;
  private SvgView mSvgView_15;
  private SvgView mSvgView_16;
  private SvgView mSvgView_17;
  private SvgView mSvgView_18;
  private SvgView mSvgView_19;
  private SvgView mSvgView_20;
  
  public OPFingerPrintRecognitionContinueView(Context paramContext)
  {
    super(paramContext);
    initView(paramContext);
  }
  
  public OPFingerPrintRecognitionContinueView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initView(paramContext);
  }
  
  public OPFingerPrintRecognitionContinueView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initView(paramContext);
  }
  
  private SvgView getSvgView(int paramInt, FrameLayout paramFrameLayout)
  {
    paramFrameLayout = (SvgView)this.mLayoutInflater.inflate(2130968804, paramFrameLayout, false);
    paramFrameLayout.setSvgResource(paramInt);
    return paramFrameLayout;
  }
  
  public void doRecognition(int paramInt, boolean paramBoolean)
  {
    if ((paramInt >= 65) && (paramInt < 70)) {
      this.mSvgView_11.reveal(paramBoolean);
    }
    do
    {
      return;
      if ((paramInt >= 70) && (paramInt < 75))
      {
        this.mSvgView_12.reveal(paramBoolean);
        return;
      }
      if ((paramInt >= 75) && (paramInt < 80))
      {
        this.mSvgView_13.reveal(paramBoolean);
        return;
      }
      if ((paramInt >= 80) && (paramInt < 85))
      {
        this.mSvgView_14.reveal(paramBoolean);
        return;
      }
      if ((paramInt >= 85) && (paramInt < 90))
      {
        this.mSvgView_15.reveal(paramBoolean);
        return;
      }
      if ((paramInt >= 90) && (paramInt < 95))
      {
        this.mSvgView_16.reveal(paramBoolean);
        return;
      }
      if ((paramInt >= 95) && (paramInt < 100))
      {
        this.mSvgView_17.reveal(paramBoolean);
        return;
      }
    } while (paramInt != 100);
    this.mSvgView_18.reveal(paramBoolean);
  }
  
  public void doRecognitionByCount(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = paramInt1;
    if (OPUtils.getFingerprintScaleAnimStep(this.mContext) == 8) {
      i = paramInt1 + 2;
    }
    switch (i)
    {
    default: 
    case 11: 
    case 12: 
    case 13: 
    case 14: 
    case 15: 
    case 16: 
    case 17: 
      do
      {
        do
        {
          do
          {
            return;
            this.mSvgView_11.reveal(paramBoolean);
            return;
            this.mSvgView_12.reveal(paramBoolean);
            return;
            this.mSvgView_13.reveal(paramBoolean);
            return;
            this.mSvgView_14.reveal(paramBoolean);
            return;
          } while (this.mSvgView_15 == null);
          this.mSvgView_15.reveal(paramBoolean);
          return;
        } while (this.mSvgView_16 == null);
        this.mSvgView_16.reveal(paramBoolean);
        return;
      } while (this.mSvgView_17 == null);
      this.mSvgView_17.reveal(paramBoolean);
      return;
    case 18: 
      if (paramInt2 >= 100)
      {
        this.mSvgView_18.reveal(paramBoolean);
        this.mSvgView_19.reveal(paramBoolean);
        this.mSvgView_20.reveal(paramBoolean);
        return;
      }
      this.mSvgView_18.reveal(paramBoolean);
      return;
    case 19: 
      if (paramInt2 >= 100)
      {
        this.mSvgView_19.reveal(paramBoolean);
        this.mSvgView_20.reveal(paramBoolean);
        return;
      }
      this.mSvgView_19.reveal(paramBoolean);
      return;
    }
    this.mSvgView_20.reveal(paramBoolean);
  }
  
  public void initSvgView(Context paramContext, FrameLayout paramFrameLayout)
  {
    if (OPUtils.isSurportBackFingerprint(paramContext))
    {
      this.mSvgView_11 = getSvgView(2131296289, paramFrameLayout);
      this.mSvgView_12 = getSvgView(2131296290, paramFrameLayout);
      this.mSvgView_13 = getSvgView(2131296291, paramFrameLayout);
      this.mSvgView_14 = getSvgView(2131296292, paramFrameLayout);
    }
    for (;;)
    {
      addView(this.mSvgView_11);
      addView(this.mSvgView_12);
      addView(this.mSvgView_13);
      addView(this.mSvgView_14);
      if (!OPUtils.isSurportBackFingerprint(paramContext))
      {
        addView(this.mSvgView_15);
        addView(this.mSvgView_16);
        addView(this.mSvgView_17);
        addView(this.mSvgView_18);
        addView(this.mSvgView_19);
        addView(this.mSvgView_20);
      }
      return;
      this.mSvgView_11 = getSvgView(2131296274, paramFrameLayout);
      this.mSvgView_12 = getSvgView(2131296275, paramFrameLayout);
      this.mSvgView_13 = getSvgView(2131296276, paramFrameLayout);
      this.mSvgView_14 = getSvgView(2131296277, paramFrameLayout);
      this.mSvgView_15 = getSvgView(2131296278, paramFrameLayout);
      this.mSvgView_16 = getSvgView(2131296279, paramFrameLayout);
      this.mSvgView_17 = getSvgView(2131296280, paramFrameLayout);
      this.mSvgView_18 = getSvgView(2131296293, paramFrameLayout);
      this.mSvgView_19 = getSvgView(2131296294, paramFrameLayout);
      this.mSvgView_20 = getSvgView(2131296295, paramFrameLayout);
    }
  }
  
  public void initView(Context paramContext)
  {
    this.mContext = paramContext;
    this.mLayoutInflater = LayoutInflater.from(paramContext);
    this.mFingerPrintView = ((FrameLayout)this.mLayoutInflater.inflate(2130968803, this));
    this.mFingerPrintView.setBackgroundResource(2130838308);
    initSvgView(paramContext, this.mFingerPrintView);
  }
  
  public void resetWithAnimation()
  {
    this.mSvgView_11.resetWithAnimation();
    this.mSvgView_12.resetWithAnimation();
    this.mSvgView_13.resetWithAnimation();
    this.mSvgView_14.resetWithAnimation();
    if (!OPUtils.isSurportBackFingerprint(this.mContext))
    {
      this.mSvgView_15.resetWithAnimation();
      this.mSvgView_16.resetWithAnimation();
      this.mSvgView_17.resetWithAnimation();
      this.mSvgView_18.resetWithAnimation();
      this.mSvgView_19.resetWithAnimation();
      this.mSvgView_20.resetWithAnimation();
    }
  }
  
  public void resetWithoutAnimation()
  {
    this.mSvgView_11.resetWithoutAnimation();
    this.mSvgView_12.resetWithoutAnimation();
    this.mSvgView_13.resetWithoutAnimation();
    this.mSvgView_14.resetWithoutAnimation();
    if (!OPUtils.isSurportBackFingerprint(this.mContext))
    {
      this.mSvgView_15.resetWithoutAnimation();
      this.mSvgView_16.resetWithoutAnimation();
      this.mSvgView_17.resetWithoutAnimation();
      this.mSvgView_18.resetWithoutAnimation();
      this.mSvgView_19.resetWithoutAnimation();
      this.mSvgView_20.resetWithoutAnimation();
    }
  }
  
  public void revealWithoutAnimation()
  {
    this.mSvgView_11.revealWithoutAnimation();
    this.mSvgView_12.revealWithoutAnimation();
    this.mSvgView_13.revealWithoutAnimation();
    this.mSvgView_14.revealWithoutAnimation();
    if (!OPUtils.isSurportBackFingerprint(this.mContext))
    {
      this.mSvgView_15.revealWithoutAnimation();
      this.mSvgView_16.revealWithoutAnimation();
      this.mSvgView_17.revealWithoutAnimation();
      this.mSvgView_18.revealWithoutAnimation();
      this.mSvgView_19.revealWithoutAnimation();
      this.mSvgView_20.revealWithoutAnimation();
    }
  }
  
  public void setBackGround(int paramInt)
  {
    if (this.mFingerPrintView != null) {
      this.mFingerPrintView.setBackgroundResource(paramInt);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\opfinger\OPFingerPrintRecognitionContinueView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */