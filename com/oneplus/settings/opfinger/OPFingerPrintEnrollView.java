package com.oneplus.settings.opfinger;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.oneplus.lib.widget.button.OPButton;
import com.oneplus.settings.utils.OPUtils;
import java.io.PrintStream;

public class OPFingerPrintEnrollView
  extends RelativeLayout
{
  private LayoutInflater inflater;
  private Context mContext;
  private Handler mHandler = new Handler();
  private ImageView mImage;
  private int mLayoutResId = 2130968808;
  private OPButton mOPFingerInputCompletedComfirmBtn;
  private TextView mOPFingerInputTipsSubTitle;
  private TextView mOPFingerInputTipsTitle;
  private TextView mOPFingerInputTipsWarning;
  private OPFingerPrintRecognitionContinueView mOPFingerPrintRecognitionContinueView;
  private OPFingerPrintRecognitionView mOPFingerPrintRecognitionView;
  public OnOPFingerComfirmListener mOnOPFingerComfirmListener;
  private int mPercent = 0;
  private View mView;
  
  public OPFingerPrintEnrollView(Context paramContext)
  {
    super(paramContext);
    initViews(paramContext);
  }
  
  public OPFingerPrintEnrollView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initViews(paramContext);
  }
  
  public OPFingerPrintEnrollView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initViews(paramContext);
  }
  
  private void initViews(Context paramContext)
  {
    this.mContext = paramContext;
    this.mView = LayoutInflater.from(paramContext).inflate(2130968808, this);
    this.mOPFingerPrintRecognitionView = ((OPFingerPrintRecognitionView)this.mView.findViewById(2131362318));
    this.mOPFingerPrintRecognitionContinueView = ((OPFingerPrintRecognitionContinueView)this.mView.findViewById(2131362317));
    this.mOPFingerInputTipsTitle = ((TextView)this.mView.findViewById(2131362314));
    this.mOPFingerInputTipsSubTitle = ((TextView)this.mView.findViewById(2131362316));
    this.mOPFingerInputTipsWarning = ((TextView)this.mView.findViewById(2131362319));
    this.mOPFingerInputCompletedComfirmBtn = ((OPButton)this.mView.findViewById(2131362320));
    this.mOPFingerInputCompletedComfirmBtn.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (OPFingerPrintEnrollView.this.mOnOPFingerComfirmListener != null) {
          OPFingerPrintEnrollView.this.mOnOPFingerComfirmListener.onOPFingerComfirmClick();
        }
      }
    });
  }
  
  public void doEnroll(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    System.out.println("doEnroll--1:" + this.mOPFingerPrintRecognitionView);
    System.out.println("doEnroll--2:" + this.mOPFingerPrintRecognitionContinueView);
    if (paramInt2 >= 7) {
      if (this.mOPFingerPrintRecognitionView != null) {
        this.mOPFingerPrintRecognitionView.doRecognitionByCount(paramInt2, 0, paramBoolean);
      }
    }
    while (this.mOPFingerPrintRecognitionContinueView == null) {
      return;
    }
    this.mOPFingerPrintRecognitionContinueView.doRecognitionByCount(paramInt2, 0, paramBoolean);
  }
  
  public void doRecognition(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (paramInt1 <= OPUtils.getFingerprintScaleAnimStep(this.mContext)) {
      if (this.mOPFingerPrintRecognitionView != null) {
        this.mOPFingerPrintRecognitionView.doRecognitionByCount(paramInt1, paramInt2, paramBoolean);
      }
    }
    while (this.mOPFingerPrintRecognitionContinueView == null) {
      return;
    }
    this.mOPFingerPrintRecognitionContinueView.doRecognitionByCount(paramInt1, paramInt2, paramBoolean);
  }
  
  public void enrollFailed()
  {
    this.mOPFingerInputTipsTitle.setText(2131690202);
    this.mOPFingerInputTipsSubTitle.setText(2131690203);
    this.mOPFingerInputCompletedComfirmBtn.setVisibility(8);
    if (this.mOPFingerPrintRecognitionView != null) {
      this.mOPFingerPrintRecognitionView.resetWithoutAnimation();
    }
    if (this.mOPFingerPrintRecognitionContinueView != null)
    {
      this.mOPFingerPrintRecognitionContinueView.resetWithoutAnimation();
      this.mOPFingerPrintRecognitionContinueView.setVisibility(8);
    }
  }
  
  public TextView getWarningTipsView()
  {
    return this.mOPFingerInputTipsWarning;
  }
  
  public void hideHeaderView()
  {
    ImageView localImageView1 = (ImageView)findViewById(2131362313);
    ImageView localImageView2 = (ImageView)findViewById(2131362315);
    TextView localTextView1 = (TextView)this.mView.findViewById(2131362314);
    TextView localTextView2 = (TextView)this.mView.findViewById(2131362316);
    localImageView1.setVisibility(8);
    localImageView2.setVisibility(8);
    localTextView1.setVisibility(8);
    localTextView2.setVisibility(8);
  }
  
  public void hideWarningTips()
  {
    if (this.mOPFingerInputTipsWarning != null)
    {
      this.mOPFingerInputTipsWarning.setText("");
      this.mOPFingerInputTipsWarning.setVisibility(4);
    }
  }
  
  public void resetTextAndBtn()
  {
    if (this.mOPFingerInputTipsTitle != null) {
      this.mOPFingerInputTipsTitle.setText(2131689958);
    }
    if (this.mOPFingerInputTipsSubTitle != null) {
      if (!OPUtils.isSurportBackFingerprint(this.mContext)) {
        break label61;
      }
    }
    label61:
    for (int i = 2131690505;; i = 2131689959)
    {
      this.mOPFingerInputTipsSubTitle.setText(i);
      if (this.mOPFingerInputCompletedComfirmBtn != null) {
        this.mOPFingerInputCompletedComfirmBtn.setVisibility(8);
      }
      return;
    }
  }
  
  public void resetWithAnimation()
  {
    resetTextAndBtn();
    if (this.mOPFingerPrintRecognitionView != null) {
      this.mOPFingerPrintRecognitionView.resetWithAnimation();
    }
    if (this.mOPFingerPrintRecognitionContinueView != null)
    {
      this.mOPFingerPrintRecognitionContinueView.resetWithoutAnimation();
      this.mOPFingerPrintRecognitionContinueView.setVisibility(8);
    }
  }
  
  public void resetWithoutAnimation()
  {
    resetTextAndBtn();
    if (this.mOPFingerPrintRecognitionView != null) {
      this.mOPFingerPrintRecognitionView.resetWithoutAnimation();
    }
    if (this.mOPFingerPrintRecognitionContinueView != null)
    {
      this.mOPFingerPrintRecognitionContinueView.resetWithoutAnimation();
      this.mOPFingerPrintRecognitionContinueView.setVisibility(8);
    }
  }
  
  public void revealWithoutAnimation()
  {
    if (this.mOPFingerPrintRecognitionView != null) {
      this.mOPFingerPrintRecognitionView.revealWithoutAnimation();
    }
    if (this.mOPFingerPrintRecognitionContinueView != null) {
      this.mOPFingerPrintRecognitionContinueView.revealWithoutAnimation();
    }
  }
  
  public void setOnOPFingerComfirmListener(OnOPFingerComfirmListener paramOnOPFingerComfirmListener)
  {
    this.mOnOPFingerComfirmListener = paramOnOPFingerComfirmListener;
  }
  
  public void setSubTitleView(TextView paramTextView)
  {
    this.mOPFingerInputTipsSubTitle = paramTextView;
  }
  
  public void setTipsCompletedContent()
  {
    this.mOPFingerInputTipsTitle.setText(2131689962);
    this.mOPFingerInputTipsSubTitle.setText(2131689963);
    this.mOPFingerInputCompletedComfirmBtn.setVisibility(8);
  }
  
  public void setTipsContinueContent()
  {
    this.mOPFingerInputTipsTitle.setText(2131690198);
    this.mOPFingerInputTipsSubTitle.setText(2131690199);
    this.mOPFingerInputCompletedComfirmBtn.setVisibility(8);
  }
  
  public void setTipsProgressContent(int paramInt1, int paramInt2)
  {
    if (paramInt1 == 13)
    {
      this.mOPFingerInputTipsTitle.setText(2131690198);
      this.mOPFingerInputTipsSubTitle.setText(2131690199);
      this.mOPFingerInputCompletedComfirmBtn.setVisibility(8);
      return;
    }
    if (paramInt2 >= 100)
    {
      this.mOPFingerInputTipsTitle.setText(2131690200);
      this.mOPFingerInputTipsSubTitle.setText(2131690201);
      this.mOPFingerInputCompletedComfirmBtn.setVisibility(8);
      return;
    }
    this.mOPFingerInputTipsTitle.setText(2131689960);
    this.mOPFingerInputTipsSubTitle.setText(2131689961);
    this.mOPFingerInputCompletedComfirmBtn.setVisibility(8);
  }
  
  public void setTipsStatusContent(int paramInt)
  {
    switch (paramInt)
    {
    case 2: 
    default: 
      return;
    case 1: 
      this.mOPFingerInputTipsTitle.setText(2131689958);
      this.mOPFingerInputTipsSubTitle.setText(2131689959);
      return;
    }
    this.mOPFingerInputTipsTitle.setText(2131690196);
    this.mOPFingerInputTipsSubTitle.setText(2131690197);
  }
  
  public void setTitleView(TextView paramTextView)
  {
    this.mOPFingerInputTipsTitle = paramTextView;
  }
  
  public void showContinueView()
  {
    this.mOPFingerPrintRecognitionContinueView.setVisibility(0);
    AnimationSet localAnimationSet = new AnimationSet(true);
    Object localObject = new AlphaAnimation(0.0F, 1.0F);
    ((AlphaAnimation)localObject).setDuration(500L);
    localAnimationSet.addAnimation((Animation)localObject);
    localObject = new ScaleAnimation(0.8F, 1.0F, 0.8F, 1.0F, 1, 0.5F, 1, 0.5F);
    ((ScaleAnimation)localObject).setDuration(500L);
    localAnimationSet.addAnimation((Animation)localObject);
    this.mOPFingerPrintRecognitionContinueView.setAnimation(localAnimationSet);
    localAnimationSet.start();
  }
  
  public void showWarningTips(CharSequence paramCharSequence)
  {
    if (this.mOPFingerInputTipsWarning != null)
    {
      this.mOPFingerInputTipsWarning.setText(paramCharSequence);
      this.mOPFingerInputTipsWarning.setAlpha(1.0F);
      this.mOPFingerInputTipsWarning.setVisibility(0);
    }
  }
  
  public static abstract interface OnOPFingerComfirmListener
  {
    public abstract void onOPFingerComfirmClick();
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\opfinger\OPFingerPrintEnrollView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */