package com.oneplus.lib.app;

import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.oneplus.commonctrl.R.id;
import com.oneplus.commonctrl.R.layout;
import com.oneplus.lib.widget.OPProgressBar;
import java.text.NumberFormat;

public class OPProgressDialog
  extends OPAlertDialog
{
  public static final int STYLE_HORIZONTAL = 1;
  public static final int STYLE_SPINNER = 0;
  private boolean mHasStarted;
  private int mIncrementBy;
  private int mIncrementSecondaryBy;
  private boolean mIndeterminate;
  private Drawable mIndeterminateDrawable;
  private int mMax;
  private CharSequence mMessage;
  private TextView mMessageView;
  private OPProgressBar mProgress;
  private Drawable mProgressDrawable;
  private TextView mProgressNumber;
  private String mProgressNumberFormat;
  private TextView mProgressPercent;
  private NumberFormat mProgressPercentFormat;
  private int mProgressStyle = 0;
  private int mProgressVal;
  private int mSecondaryProgressVal;
  private Handler mViewUpdateHandler;
  
  public OPProgressDialog(Context paramContext)
  {
    super(paramContext);
    initFormats();
  }
  
  public OPProgressDialog(Context paramContext, int paramInt)
  {
    super(paramContext, paramInt);
    initFormats();
  }
  
  private void initFormats()
  {
    this.mProgressNumberFormat = "%1d/%2d";
    this.mProgressPercentFormat = NumberFormat.getPercentInstance();
    this.mProgressPercentFormat.setMaximumFractionDigits(0);
  }
  
  private void onProgressChanged()
  {
    if ((this.mProgressStyle != 1) || (this.mViewUpdateHandler == null) || (this.mViewUpdateHandler.hasMessages(0))) {
      return;
    }
    this.mViewUpdateHandler.sendEmptyMessage(0);
  }
  
  public static OPProgressDialog show(Context paramContext, CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    return show(paramContext, paramCharSequence1, paramCharSequence2, false);
  }
  
  public static OPProgressDialog show(Context paramContext, CharSequence paramCharSequence1, CharSequence paramCharSequence2, boolean paramBoolean)
  {
    return show(paramContext, paramCharSequence1, paramCharSequence2, paramBoolean, false, null);
  }
  
  public static OPProgressDialog show(Context paramContext, CharSequence paramCharSequence1, CharSequence paramCharSequence2, boolean paramBoolean1, boolean paramBoolean2)
  {
    return show(paramContext, paramCharSequence1, paramCharSequence2, paramBoolean1, paramBoolean2, null);
  }
  
  public static OPProgressDialog show(Context paramContext, CharSequence paramCharSequence1, CharSequence paramCharSequence2, boolean paramBoolean1, boolean paramBoolean2, DialogInterface.OnCancelListener paramOnCancelListener)
  {
    paramContext = new OPProgressDialog(paramContext);
    paramContext.setTitle(paramCharSequence1);
    paramContext.setMessage(paramCharSequence2);
    paramContext.setIndeterminate(paramBoolean1);
    paramContext.setCancelable(paramBoolean2);
    paramContext.setOnCancelListener(paramOnCancelListener);
    paramContext.show();
    return paramContext;
  }
  
  public int getMax()
  {
    if (this.mProgress != null) {
      return this.mProgress.getMax();
    }
    return this.mMax;
  }
  
  public int getProgress()
  {
    if (this.mProgress != null) {
      return this.mProgress.getProgress();
    }
    return this.mProgressVal;
  }
  
  public int getSecondaryProgress()
  {
    if (this.mProgress != null) {
      return this.mProgress.getSecondaryProgress();
    }
    return this.mSecondaryProgressVal;
  }
  
  public void incrementProgressBy(int paramInt)
  {
    if (this.mProgress != null)
    {
      this.mProgress.incrementProgressBy(paramInt);
      onProgressChanged();
      return;
    }
    this.mIncrementBy += paramInt;
  }
  
  public void incrementSecondaryProgressBy(int paramInt)
  {
    if (this.mProgress != null)
    {
      this.mProgress.incrementSecondaryProgressBy(paramInt);
      onProgressChanged();
      return;
    }
    this.mIncrementSecondaryBy += paramInt;
  }
  
  public boolean isIndeterminate()
  {
    if (this.mProgress != null) {
      return this.mProgress.isIndeterminate();
    }
    return this.mIndeterminate;
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    Object localObject = LayoutInflater.from(this.mContext);
    if (this.mProgressStyle == 1)
    {
      this.mViewUpdateHandler = new Handler()
      {
        public void handleMessage(Message paramAnonymousMessage)
        {
          super.handleMessage(paramAnonymousMessage);
          int i = OPProgressDialog.-get0(OPProgressDialog.this).getProgress();
          int j = OPProgressDialog.-get0(OPProgressDialog.this).getMax();
          if (OPProgressDialog.-get2(OPProgressDialog.this) != null)
          {
            paramAnonymousMessage = OPProgressDialog.-get2(OPProgressDialog.this);
            OPProgressDialog.-get1(OPProgressDialog.this).setText(String.format(paramAnonymousMessage, new Object[] { Integer.valueOf(i), Integer.valueOf(j) }));
          }
          while (OPProgressDialog.-get4(OPProgressDialog.this) != null)
          {
            double d = i / j;
            paramAnonymousMessage = new SpannableString(OPProgressDialog.-get4(OPProgressDialog.this).format(d));
            paramAnonymousMessage.setSpan(new StyleSpan(1), 0, paramAnonymousMessage.length(), 33);
            OPProgressDialog.-get3(OPProgressDialog.this).setText(paramAnonymousMessage);
            return;
            OPProgressDialog.-get1(OPProgressDialog.this).setText("");
          }
          OPProgressDialog.-get3(OPProgressDialog.this).setText("");
        }
      };
      localObject = ((LayoutInflater)localObject).inflate(R.layout.op_alert_dialog_progress_material, null);
      this.mProgress = ((OPProgressBar)((View)localObject).findViewById(16908301));
      this.mProgressNumber = ((TextView)((View)localObject).findViewById(R.id.progress_number));
      this.mProgressPercent = ((TextView)((View)localObject).findViewById(R.id.progress_percent));
      setView((View)localObject);
    }
    for (;;)
    {
      if (this.mMax > 0) {
        setMax(this.mMax);
      }
      if (this.mProgressVal > 0) {
        setProgress(this.mProgressVal);
      }
      if (this.mSecondaryProgressVal > 0) {
        setSecondaryProgress(this.mSecondaryProgressVal);
      }
      if (this.mIncrementBy > 0) {
        incrementProgressBy(this.mIncrementBy);
      }
      if (this.mIncrementSecondaryBy > 0) {
        incrementSecondaryProgressBy(this.mIncrementSecondaryBy);
      }
      if (this.mProgressDrawable != null) {
        setProgressDrawable(this.mProgressDrawable);
      }
      if (this.mIndeterminateDrawable != null) {
        setIndeterminateDrawable(this.mIndeterminateDrawable);
      }
      if (this.mMessage != null) {
        setMessage(this.mMessage);
      }
      setIndeterminate(this.mIndeterminate);
      onProgressChanged();
      super.onCreate(paramBundle);
      return;
      localObject = ((LayoutInflater)localObject).inflate(R.layout.op_progress_dialog_material, null);
      this.mProgress = ((OPProgressBar)((View)localObject).findViewById(16908301));
      this.mMessageView = ((TextView)((View)localObject).findViewById(16908299));
      setView((View)localObject);
    }
  }
  
  public void onStart()
  {
    super.onStart();
    this.mHasStarted = true;
  }
  
  protected void onStop()
  {
    super.onStop();
    this.mHasStarted = false;
  }
  
  public void setIndeterminate(boolean paramBoolean)
  {
    if (this.mProgress != null)
    {
      this.mProgress.setIndeterminate(paramBoolean);
      return;
    }
    this.mIndeterminate = paramBoolean;
  }
  
  public void setIndeterminateDrawable(Drawable paramDrawable)
  {
    if (this.mProgress != null)
    {
      this.mProgress.setIndeterminateDrawable(paramDrawable);
      return;
    }
    this.mIndeterminateDrawable = paramDrawable;
  }
  
  public void setMax(int paramInt)
  {
    if (this.mProgress != null)
    {
      this.mProgress.setMax(paramInt);
      onProgressChanged();
      return;
    }
    this.mMax = paramInt;
  }
  
  public void setMessage(CharSequence paramCharSequence)
  {
    if (this.mProgress != null)
    {
      if (this.mProgressStyle == 1)
      {
        super.setMessage(paramCharSequence);
        return;
      }
      this.mMessageView.setText(paramCharSequence);
      return;
    }
    this.mMessage = paramCharSequence;
  }
  
  public void setProgress(int paramInt)
  {
    if (this.mHasStarted)
    {
      this.mProgress.setProgress(paramInt);
      onProgressChanged();
      return;
    }
    this.mProgressVal = paramInt;
  }
  
  public void setProgressDrawable(Drawable paramDrawable)
  {
    if (this.mProgress != null)
    {
      this.mProgress.setProgressDrawable(paramDrawable);
      return;
    }
    this.mProgressDrawable = paramDrawable;
  }
  
  public void setProgressNumberFormat(String paramString)
  {
    this.mProgressNumberFormat = paramString;
    onProgressChanged();
  }
  
  public void setProgressPercentFormat(NumberFormat paramNumberFormat)
  {
    this.mProgressPercentFormat = paramNumberFormat;
    onProgressChanged();
  }
  
  public void setProgressStyle(int paramInt)
  {
    this.mProgressStyle = paramInt;
  }
  
  public void setSecondaryProgress(int paramInt)
  {
    if (this.mProgress != null)
    {
      this.mProgress.setSecondaryProgress(paramInt);
      onProgressChanged();
      return;
    }
    this.mSecondaryProgressVal = paramInt;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\app\OPProgressDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */