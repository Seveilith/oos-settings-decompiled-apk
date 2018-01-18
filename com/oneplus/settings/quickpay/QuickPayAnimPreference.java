package com.oneplus.settings.quickpay;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.oneplus.settings.utils.OPUtils;

public class QuickPayAnimPreference
  extends Preference
  implements View.OnClickListener
{
  private static final int BEHINDQUICKPAYDRAWABLENUMBER = 211;
  private static final int DURATION = 16;
  private static final int MSG_PLAY = 0;
  private static final int QUICKPAYDRAWABLENUMBER = 153;
  private static int[] pFrameRess = null;
  private ImageView img_quickpay_instructions;
  private ImageView img_quickpay_phone_ui;
  private ImageView img_quickpay_play;
  private Context mContext;
  Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      }
      QuickPayAnimPreference.-wrap0(QuickPayAnimPreference.this);
    }
  };
  private OnPreferenceViewClickListener mListener;
  private int resid = 2130968849;
  private SceneAnimation sceneAnimation;
  
  public QuickPayAnimPreference(Context paramContext)
  {
    super(paramContext);
    initViews(paramContext);
  }
  
  public QuickPayAnimPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initViews(paramContext);
  }
  
  public QuickPayAnimPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initViews(paramContext);
  }
  
  public static String autoGenericCode(int paramInt1, int paramInt2)
  {
    return String.format("%0" + paramInt2 + "d", new Object[] { Integer.valueOf(paramInt1) });
  }
  
  private void initViews(Context paramContext)
  {
    this.mContext = paramContext;
    setLayoutResource(this.resid);
  }
  
  private void setResourceforAnimation(String paramString, int paramInt)
  {
    pFrameRess = new int[paramInt];
    int i = 0;
    for (;;)
    {
      if (i < paramInt) {
        try
        {
          int j = this.mContext.getResources().getIdentifier(paramString + autoGenericCode(i, 5), "drawable", this.mContext.getPackageName());
          pFrameRess[i] = j;
          i += 1;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            localException.printStackTrace();
          }
        }
      }
    }
  }
  
  private void startAnim()
  {
    this.img_quickpay_play.setVisibility(8);
    if (this.sceneAnimation == null) {
      this.sceneAnimation = new SceneAnimation(this.img_quickpay_instructions, pFrameRess, 16);
    }
    this.sceneAnimation.play();
  }
  
  private void startOrStopAnim()
  {
    if ((this.sceneAnimation != null) && (this.sceneAnimation.isStarting()))
    {
      stopAnim();
      return;
    }
    startAnim();
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    this.img_quickpay_play = ((ImageView)paramPreferenceViewHolder.findViewById(2131362377));
    this.img_quickpay_instructions = ((ImageView)paramPreferenceViewHolder.findViewById(2131362376));
    this.img_quickpay_phone_ui = ((ImageView)paramPreferenceViewHolder.findViewById(2131362375));
    if (OPUtils.isSurportBackFingerprint(this.mContext))
    {
      this.img_quickpay_play.setImageResource(2130837613);
      this.img_quickpay_instructions.setImageResource(2130837614);
      this.img_quickpay_phone_ui.setImageResource(2130837614);
    }
    this.img_quickpay_play.setOnClickListener(this);
    this.img_quickpay_instructions.setOnClickListener(this);
    this.img_quickpay_phone_ui.setOnClickListener(this);
    if (pFrameRess == null)
    {
      if (OPUtils.isSurportBackFingerprint(this.mContext)) {
        break label158;
      }
      setResourceforAnimation("quick_pay_", 153);
    }
    for (;;)
    {
      if (this.sceneAnimation == null) {
        this.sceneAnimation = new SceneAnimation(this.img_quickpay_instructions, pFrameRess, 16);
      }
      return;
      label158:
      setResourceforAnimation("behind_quick_pay__", 211);
    }
  }
  
  public void onClick(View paramView)
  {
    if (this.mListener != null) {
      this.mListener.onPreferenceViewClick(paramView);
    }
  }
  
  public void playOrStopAnim()
  {
    this.mHandler.removeMessages(0);
    this.mHandler.sendEmptyMessage(0);
  }
  
  public void setViewOnClick(OnPreferenceViewClickListener paramOnPreferenceViewClickListener)
  {
    this.mListener = paramOnPreferenceViewClickListener;
  }
  
  public void stopAnim()
  {
    if (this.sceneAnimation != null) {
      this.sceneAnimation.stop();
    }
    if (this.img_quickpay_play != null) {
      this.img_quickpay_play.setVisibility(0);
    }
  }
  
  public static abstract interface OnPreferenceViewClickListener
  {
    public abstract void onPreferenceViewClick(View paramView);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\quickpay\QuickPayAnimPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */