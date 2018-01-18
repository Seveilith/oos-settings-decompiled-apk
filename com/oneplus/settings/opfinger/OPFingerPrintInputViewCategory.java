package com.oneplus.settings.opfinger;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class OPFingerPrintInputViewCategory
  extends PreferenceCategory
{
  private LayoutInflater inflater;
  private Context mContext;
  private Handler mHandler = new Handler();
  private ImageView mImage;
  private int mLayoutResId = 2130968808;
  private Button mOPFingerInputCompletedComfirmBtn;
  private TextView mOPFingerInputTipsSubTitle;
  private TextView mOPFingerInputTipsTitle;
  private OPFingerPrintRecognitionContinueView mOPFingerPrintRecognitionContinueView;
  private OPFingerPrintRecognitionView mOPFingerPrintRecognitionView;
  public OnOPFingerComfirmListener mOnOPFingerComfirmListener;
  private int mPercent = 0;
  
  public OPFingerPrintInputViewCategory(Context paramContext)
  {
    super(paramContext);
    initViews(paramContext);
  }
  
  public OPFingerPrintInputViewCategory(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initViews(paramContext);
  }
  
  public OPFingerPrintInputViewCategory(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initViews(paramContext);
  }
  
  private void initViews(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  public void doRecognition(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (paramInt1 <= 13) {
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
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    this.mOPFingerPrintRecognitionView = ((OPFingerPrintRecognitionView)paramPreferenceViewHolder.findViewById(2131362318));
    this.mOPFingerPrintRecognitionContinueView = ((OPFingerPrintRecognitionContinueView)paramPreferenceViewHolder.findViewById(2131362317));
    this.mOPFingerInputTipsTitle = ((TextView)paramPreferenceViewHolder.findViewById(2131362314));
    this.mOPFingerInputTipsSubTitle = ((TextView)paramPreferenceViewHolder.findViewById(2131362316));
    this.mOPFingerInputCompletedComfirmBtn = ((Button)paramPreferenceViewHolder.findViewById(2131362320));
    this.mOPFingerInputCompletedComfirmBtn.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (OPFingerPrintInputViewCategory.this.mOnOPFingerComfirmListener != null) {
          OPFingerPrintInputViewCategory.this.mOnOPFingerComfirmListener.onOPFingerComfirmClick();
        }
      }
    });
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    return ((LayoutInflater)this.mContext.getSystemService("layout_inflater")).inflate(this.mLayoutResId, paramViewGroup, false);
  }
  
  public void resetTextAndBtn()
  {
    this.mOPFingerInputTipsTitle.setText(2131689958);
    this.mOPFingerInputTipsSubTitle.setText(2131689959);
    this.mOPFingerInputCompletedComfirmBtn.setVisibility(8);
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
  
  public void setTipsCompletedContent()
  {
    this.mOPFingerInputTipsTitle.setText(2131689962);
    this.mOPFingerInputTipsSubTitle.setText(2131689963);
    this.mOPFingerInputCompletedComfirmBtn.setVisibility(0);
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
  
  public static abstract interface OnOPFingerComfirmListener
  {
    public abstract void onOPFingerComfirmClick();
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\opfinger\OPFingerPrintInputViewCategory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */