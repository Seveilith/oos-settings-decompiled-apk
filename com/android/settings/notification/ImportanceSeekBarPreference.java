package com.android.settings.notification;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.android.internal.R.styleable;
import com.android.settings.SeekBarPreference;

public class ImportanceSeekBarPreference
  extends SeekBarPreference
  implements SeekBar.OnSeekBarChangeListener
{
  private static final String TAG = "ImportanceSeekBarPref";
  private float mActiveSliderAlpha = 1.0F;
  private ColorStateList mActiveSliderTint;
  private boolean mAutoOn;
  private Callback mCallback;
  private Handler mHandler;
  private float mInactiveSliderAlpha;
  private ColorStateList mInactiveSliderTint;
  private int mMinProgress;
  private final Runnable mNotifyChanged = new Runnable()
  {
    public void run()
    {
      ImportanceSeekBarPreference.-wrap1(ImportanceSeekBarPreference.this);
    }
  };
  private SeekBar mSeekBar;
  private String mSummary;
  private TextView mSummaryTextView;
  
  public ImportanceSeekBarPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ImportanceSeekBarPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ImportanceSeekBarPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public ImportanceSeekBarPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setLayoutResource(2130968910);
    this.mActiveSliderTint = ColorStateList.valueOf(paramContext.getColor(2131493715));
    this.mInactiveSliderTint = ColorStateList.valueOf(paramContext.getColor(2131493716));
    this.mHandler = new Handler();
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.Theme, 0, 0);
    this.mInactiveSliderAlpha = paramContext.getFloat(3, 0.5F);
    paramContext.recycle();
  }
  
  private void applyAuto(ImageView paramImageView)
  {
    boolean bool;
    if (this.mAutoOn)
    {
      bool = false;
      this.mAutoOn = bool;
      if (this.mAutoOn) {
        break label48;
      }
      setProgress(3);
      this.mCallback.onImportanceChanged(3, true);
    }
    for (;;)
    {
      applyAutoUi(paramImageView);
      return;
      bool = true;
      break;
      label48:
      this.mCallback.onImportanceChanged(64536, true);
    }
  }
  
  private void applyAutoUi(ImageView paramImageView)
  {
    Object localObject = this.mSeekBar;
    boolean bool;
    float f;
    if (this.mAutoOn)
    {
      bool = false;
      ((SeekBar)localObject).setEnabled(bool);
      if (!this.mAutoOn) {
        break label116;
      }
      f = this.mInactiveSliderAlpha;
      label33:
      if (!this.mAutoOn) {
        break label124;
      }
    }
    label116:
    label124:
    for (localObject = this.mActiveSliderTint;; localObject = this.mInactiveSliderTint)
    {
      Drawable localDrawable = paramImageView.getDrawable().mutate();
      localDrawable.setTintList((ColorStateList)localObject);
      paramImageView.setImageDrawable(localDrawable);
      this.mSeekBar.setAlpha(f);
      if (this.mAutoOn)
      {
        setProgress(3);
        this.mSummary = getProgressSummary(64536);
      }
      this.mSummaryTextView.setText(this.mSummary);
      return;
      bool = true;
      break;
      f = this.mActiveSliderAlpha;
      break label33;
    }
  }
  
  private String getProgressSummary(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return getContext().getString(2131693231);
    case 0: 
      return getContext().getString(2131693225);
    case 1: 
      return getContext().getString(2131693226);
    case 2: 
      return getContext().getString(2131693227);
    case 3: 
      return getContext().getString(2131693228);
    case 4: 
      return getContext().getString(2131693229);
    }
    return getContext().getString(2131693230);
  }
  
  private void postNotifyChanged()
  {
    super.notifyChanged();
  }
  
  public CharSequence getSummary()
  {
    return this.mSummary;
  }
  
  protected void notifyChanged()
  {
    this.mHandler.post(this.mNotifyChanged);
  }
  
  public void onBindViewHolder(final PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    this.mSummaryTextView = ((TextView)paramPreferenceViewHolder.findViewById(16908304));
    this.mSeekBar = ((SeekBar)paramPreferenceViewHolder.findViewById(16909275));
    paramPreferenceViewHolder = (ImageView)paramPreferenceViewHolder.findViewById(2131362438);
    applyAutoUi(paramPreferenceViewHolder);
    paramPreferenceViewHolder.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ImportanceSeekBarPreference.-wrap0(ImportanceSeekBarPreference.this, paramPreferenceViewHolder);
      }
    });
  }
  
  public void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean)
  {
    super.onProgressChanged(paramSeekBar, paramInt, paramBoolean);
    int i = paramInt;
    if (paramInt < this.mMinProgress)
    {
      paramSeekBar.setProgress(this.mMinProgress);
      i = this.mMinProgress;
    }
    if (this.mSummaryTextView != null)
    {
      this.mSummary = getProgressSummary(i);
      this.mSummaryTextView.setText(this.mSummary);
    }
    this.mCallback.onImportanceChanged(i, paramBoolean);
  }
  
  public void setAutoOn(boolean paramBoolean)
  {
    this.mAutoOn = paramBoolean;
    notifyChanged();
  }
  
  public void setCallback(Callback paramCallback)
  {
    this.mCallback = paramCallback;
  }
  
  public void setMinimumProgress(int paramInt)
  {
    this.mMinProgress = paramInt;
    notifyChanged();
  }
  
  public void setProgress(int paramInt)
  {
    this.mSummary = getProgressSummary(paramInt);
    super.setProgress(paramInt);
  }
  
  public static abstract interface Callback
  {
    public abstract void onImportanceChanged(int paramInt, boolean paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\ImportanceSeekBarPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */