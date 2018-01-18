package com.oneplus.settings.opfinger;

import android.content.Context;
import android.graphics.Color;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.TextView;

public class OPFingerPrintItemPreference
  extends Preference
{
  private static String BACKGROUND_COLOR = "#239ff1";
  private LayoutInflater inflater;
  private AlphaAnimation mAlphaAnimation;
  private View mBackGroundView;
  private Context mContext;
  private boolean mHighlightBackgroundColor = false;
  private ImageView mIconView;
  private int mLayoutResId = 2130968809;
  private String mOPFingerPrintSummary;
  private String mOPFingerPrintTitle;
  private TextView mSummaryView;
  private TextView mTitleView;
  
  public OPFingerPrintItemPreference(Context paramContext)
  {
    super(paramContext);
    initViews(paramContext);
  }
  
  public OPFingerPrintItemPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initViews(paramContext);
  }
  
  public OPFingerPrintItemPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initViews(paramContext);
  }
  
  private void initViews(Context paramContext)
  {
    this.mContext = paramContext;
    setLayoutResource(this.mLayoutResId);
  }
  
  protected void onBindView(View paramView)
  {
    super.onBindView(paramView);
    this.mBackGroundView = paramView.findViewById(2131362321);
    this.mTitleView = ((TextView)paramView.findViewById(2131362323));
    this.mSummaryView = ((TextView)paramView.findViewById(2131362324));
    this.mTitleView.setText(this.mOPFingerPrintTitle);
    this.mSummaryView.setText(this.mOPFingerPrintSummary);
    this.mSummaryView.setVisibility(8);
    this.mAlphaAnimation = new AlphaAnimation(0.0F, 0.4F);
    if (this.mHighlightBackgroundColor)
    {
      this.mBackGroundView.setBackgroundColor(Color.parseColor(BACKGROUND_COLOR));
      this.mAlphaAnimation.setDuration(500L);
      this.mAlphaAnimation.setRepeatCount(1);
      this.mAlphaAnimation.setRepeatMode(2);
      this.mAlphaAnimation.setFillAfter(true);
      this.mAlphaAnimation.setAnimationListener(new Animation.AnimationListener()
      {
        public void onAnimationEnd(Animation paramAnonymousAnimation)
        {
          OPFingerPrintItemPreference.-set0(OPFingerPrintItemPreference.this, false);
        }
        
        public void onAnimationRepeat(Animation paramAnonymousAnimation) {}
        
        public void onAnimationStart(Animation paramAnonymousAnimation) {}
      });
      this.mBackGroundView.setAnimation(this.mAlphaAnimation);
      this.mAlphaAnimation.start();
      return;
    }
    this.mBackGroundView.setBackgroundColor(0);
  }
  
  protected View onCreateView(ViewGroup paramViewGroup)
  {
    return super.onCreateView(paramViewGroup);
  }
  
  public void setOPFingerSummary(String paramString)
  {
    this.mOPFingerPrintSummary = paramString;
    notifyChanged();
  }
  
  public void setOPFingerTitle(String paramString)
  {
    this.mOPFingerPrintTitle = paramString;
    notifyChanged();
  }
  
  public void updateBackgroundColor(boolean paramBoolean)
  {
    this.mHighlightBackgroundColor = paramBoolean;
    notifyChanged();
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\opfinger\OPFingerPrintItemPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */