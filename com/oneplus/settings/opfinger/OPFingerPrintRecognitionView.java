package com.oneplus.settings.opfinger;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import com.oneplus.settings.utils.OPUtils;

public class OPFingerPrintRecognitionView
  extends FrameLayout
{
  private Context mContext;
  private FrameLayout mFingerPrintView;
  private LayoutInflater mLayoutInflater;
  private SvgView mSvgView_01;
  private SvgView mSvgView_02;
  private SvgView mSvgView_03;
  private SvgView mSvgView_04;
  private SvgView mSvgView_05;
  private SvgView mSvgView_06;
  private SvgView mSvgView_07;
  private SvgView mSvgView_08;
  private SvgView mSvgView_09;
  private SvgView mSvgView_10;
  
  public OPFingerPrintRecognitionView(Context paramContext)
  {
    super(paramContext);
    initView(paramContext);
  }
  
  public OPFingerPrintRecognitionView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initView(paramContext);
  }
  
  public OPFingerPrintRecognitionView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
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
    if ((paramInt >= 16) && (paramInt < 17)) {
      this.mSvgView_01.reveal(paramBoolean);
    }
    do
    {
      return;
      if ((paramInt >= 17) && (paramInt < 21))
      {
        this.mSvgView_02.reveal(paramBoolean);
        return;
      }
      if ((paramInt >= 21) && (paramInt < 28))
      {
        this.mSvgView_03.reveal(paramBoolean);
        return;
      }
      if ((paramInt >= 29) && (paramInt < 37))
      {
        this.mSvgView_04.reveal(paramBoolean);
        return;
      }
      if ((paramInt >= 37) && (paramInt < 40))
      {
        this.mSvgView_05.reveal(paramBoolean);
        return;
      }
      if ((paramInt >= 40) && (paramInt < 46))
      {
        this.mSvgView_06.reveal(paramBoolean);
        return;
      }
      if ((paramInt >= 46) && (paramInt < 50))
      {
        this.mSvgView_07.reveal(paramBoolean);
        return;
      }
      if ((paramInt >= 50) && (paramInt < 55))
      {
        this.mSvgView_08.reveal(paramBoolean);
        return;
      }
      if ((paramInt >= 55) && (paramInt < 60))
      {
        this.mSvgView_09.reveal(paramBoolean);
        return;
      }
    } while ((paramInt < 60) || (paramInt >= 65));
    this.mSvgView_10.reveal(paramBoolean);
  }
  
  public void doRecognitionByCount(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    switch (paramInt1)
    {
    }
    do
    {
      do
      {
        return;
        this.mSvgView_01.reveal(paramBoolean);
        return;
        this.mSvgView_02.reveal(paramBoolean);
        return;
        this.mSvgView_03.reveal(paramBoolean);
        return;
        this.mSvgView_04.reveal(paramBoolean);
        return;
        this.mSvgView_05.reveal(paramBoolean);
        return;
        this.mSvgView_06.reveal(paramBoolean);
        return;
        this.mSvgView_07.reveal(paramBoolean);
        return;
        this.mSvgView_08.reveal(paramBoolean);
        return;
      } while (this.mSvgView_09 == null);
      this.mSvgView_09.reveal(paramBoolean);
      return;
    } while (this.mSvgView_09 == null);
    this.mSvgView_10.reveal(paramBoolean);
  }
  
  public void initSvgView(Context paramContext, FrameLayout paramFrameLayout)
  {
    if (OPUtils.isSurportBackFingerprint(paramContext))
    {
      this.mSvgView_01 = getSvgView(2131296281, paramFrameLayout);
      this.mSvgView_02 = getSvgView(2131296282, paramFrameLayout);
      this.mSvgView_03 = getSvgView(2131296283, paramFrameLayout);
      this.mSvgView_04 = getSvgView(2131296284, paramFrameLayout);
      this.mSvgView_05 = getSvgView(2131296285, paramFrameLayout);
      this.mSvgView_06 = getSvgView(2131296286, paramFrameLayout);
      this.mSvgView_07 = getSvgView(2131296287, paramFrameLayout);
      this.mSvgView_08 = getSvgView(2131296288, paramFrameLayout);
    }
    for (;;)
    {
      addView(this.mSvgView_01);
      addView(this.mSvgView_02);
      addView(this.mSvgView_03);
      addView(this.mSvgView_04);
      addView(this.mSvgView_05);
      addView(this.mSvgView_06);
      addView(this.mSvgView_07);
      addView(this.mSvgView_08);
      if (!OPUtils.isSurportBackFingerprint(paramContext))
      {
        addView(this.mSvgView_09);
        addView(this.mSvgView_10);
      }
      resetWithoutAnimation();
      return;
      this.mSvgView_01 = getSvgView(2131296264, paramFrameLayout);
      this.mSvgView_02 = getSvgView(2131296265, paramFrameLayout);
      this.mSvgView_03 = getSvgView(2131296266, paramFrameLayout);
      this.mSvgView_04 = getSvgView(2131296267, paramFrameLayout);
      this.mSvgView_05 = getSvgView(2131296268, paramFrameLayout);
      this.mSvgView_06 = getSvgView(2131296269, paramFrameLayout);
      this.mSvgView_07 = getSvgView(2131296270, paramFrameLayout);
      this.mSvgView_08 = getSvgView(2131296271, paramFrameLayout);
      this.mSvgView_09 = getSvgView(2131296272, paramFrameLayout);
      this.mSvgView_10 = getSvgView(2131296273, paramFrameLayout);
    }
  }
  
  public void initView(Context paramContext)
  {
    this.mContext = paramContext;
    this.mLayoutInflater = LayoutInflater.from(paramContext);
    this.mFingerPrintView = ((FrameLayout)this.mLayoutInflater.inflate(2130968803, this));
    this.mFingerPrintView.setBackgroundResource(2130838307);
    initSvgView(paramContext, this.mFingerPrintView);
  }
  
  public void resetWithAnimation()
  {
    this.mSvgView_01.resetWithAnimation();
    this.mSvgView_02.resetWithAnimation();
    this.mSvgView_03.resetWithAnimation();
    this.mSvgView_04.resetWithAnimation();
    this.mSvgView_05.resetWithAnimation();
    this.mSvgView_06.resetWithAnimation();
    this.mSvgView_07.resetWithAnimation();
    this.mSvgView_08.resetWithAnimation();
    if (!OPUtils.isSurportBackFingerprint(this.mContext))
    {
      this.mSvgView_09.resetWithAnimation();
      this.mSvgView_10.resetWithAnimation();
    }
  }
  
  public void resetWithoutAnimation()
  {
    this.mSvgView_01.resetWithoutAnimation();
    this.mSvgView_02.resetWithoutAnimation();
    this.mSvgView_03.resetWithoutAnimation();
    this.mSvgView_04.resetWithoutAnimation();
    this.mSvgView_05.resetWithoutAnimation();
    this.mSvgView_06.resetWithoutAnimation();
    this.mSvgView_07.resetWithoutAnimation();
    this.mSvgView_08.resetWithoutAnimation();
    if (!OPUtils.isSurportBackFingerprint(this.mContext))
    {
      this.mSvgView_09.resetWithoutAnimation();
      this.mSvgView_10.resetWithoutAnimation();
    }
  }
  
  public void revealWithoutAnimation()
  {
    this.mSvgView_01.revealWithoutAnimation();
    this.mSvgView_02.revealWithoutAnimation();
    this.mSvgView_03.revealWithoutAnimation();
    this.mSvgView_04.revealWithoutAnimation();
    this.mSvgView_05.revealWithoutAnimation();
    this.mSvgView_06.revealWithoutAnimation();
    this.mSvgView_07.revealWithoutAnimation();
    this.mSvgView_08.revealWithoutAnimation();
    if (!OPUtils.isSurportBackFingerprint(this.mContext))
    {
      this.mSvgView_09.revealWithoutAnimation();
      this.mSvgView_10.revealWithoutAnimation();
    }
  }
  
  public void setBackGround(int paramInt)
  {
    if (this.mFingerPrintView != null) {
      this.mFingerPrintView.setBackgroundResource(paramInt);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\opfinger\OPFingerPrintRecognitionView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */