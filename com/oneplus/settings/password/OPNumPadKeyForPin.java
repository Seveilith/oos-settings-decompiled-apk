package com.oneplus.settings.password;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.R.styleable;

public class OPNumPadKeyForPin
  extends ViewGroup
{
  static String[] sKlondike;
  private RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
  private int mDigit = -1;
  private TextView mDigitText;
  private boolean mEnableHaptics;
  private View.OnClickListener mListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if ((OPNumPadKeyForPin.-get1(OPNumPadKeyForPin.this) == null) && (OPNumPadKeyForPin.-get2(OPNumPadKeyForPin.this) > 0))
      {
        paramAnonymousView = OPNumPadKeyForPin.this.getRootView().findViewById(OPNumPadKeyForPin.-get2(OPNumPadKeyForPin.this));
        if ((paramAnonymousView != null) && ((paramAnonymousView instanceof OPPasswordTextViewForPin))) {
          OPNumPadKeyForPin.-set0(OPNumPadKeyForPin.this, (OPPasswordTextViewForPin)paramAnonymousView);
        }
      }
      if ((OPNumPadKeyForPin.-get1(OPNumPadKeyForPin.this) != null) && (OPNumPadKeyForPin.-get1(OPNumPadKeyForPin.this).isEnabled())) {
        OPNumPadKeyForPin.-get1(OPNumPadKeyForPin.this).append(Character.forDigit(OPNumPadKeyForPin.-get0(OPNumPadKeyForPin.this), 10));
      }
      OPNumPadKeyForPin.this.userActivity();
      OPNumPadKeyForPin.this.doHapticKeyClick();
    }
  };
  private PowerManager mPM;
  private OPPasswordTextViewForPin mTextView;
  private int mTextViewResId;
  
  public OPNumPadKeyForPin(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public OPNumPadKeyForPin(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public OPNumPadKeyForPin(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setFocusable(true);
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.NumPadKey);
    for (;;)
    {
      try
      {
        this.mDigit = paramAttributeSet.getInt(0, this.mDigit);
        this.mTextViewResId = paramAttributeSet.getResourceId(1, 0);
        paramAttributeSet.recycle();
        setOnClickListener(this.mListener);
        this.mEnableHaptics = new LockPatternUtils(paramContext).isTactileFeedbackEnabled();
        this.mPM = ((PowerManager)this.mContext.getSystemService("power"));
        ((LayoutInflater)getContext().getSystemService("layout_inflater")).inflate(2130968736, this, true);
        this.mDigitText = ((TextView)findViewById(2131362188));
        if ((getTag() != null) && (getTag().equals("applocker")))
        {
          this.mDigitText.setTextColor(paramContext.getResources().getColor(17170443));
          this.mDigitText.setText(Integer.toString(this.mDigit));
          setBackground(this.mContext.getDrawable(2130838507));
          return;
        }
      }
      finally
      {
        paramAttributeSet.recycle();
      }
      this.mDigitText.setTextSize(2, 29.0F);
      this.layoutParams.addRule(13);
      this.mDigitText.setLayoutParams(this.layoutParams);
    }
  }
  
  public void doHapticKeyClick()
  {
    if (this.mEnableHaptics) {
      performHapticFeedback(1, 3);
    }
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt1 = this.mDigitText.getMeasuredHeight();
    paramInt2 = getHeight() / 2 - paramInt1 / 2;
    paramInt3 = getWidth() / 2 - this.mDigitText.getMeasuredWidth() / 2;
    this.mDigitText.layout(paramInt3, paramInt2, this.mDigitText.getMeasuredWidth() + paramInt3, paramInt2 + paramInt1);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    measureChildren(paramInt1, paramInt2);
  }
  
  public void userActivity()
  {
    this.mPM.userActivity(SystemClock.uptimeMillis(), false);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\password\OPNumPadKeyForPin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */