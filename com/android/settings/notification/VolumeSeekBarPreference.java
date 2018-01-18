package com.android.settings.notification;

import android.content.Context;
import android.net.Uri;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.android.settings.SeekBarPreference;
import com.oneplus.settings.notification.OPSeekBarVolumizer;
import com.oneplus.settings.notification.OPSeekBarVolumizer.Callback;
import java.util.Objects;

public class VolumeSeekBarPreference
  extends SeekBarPreference
{
  private static final String TAG = "VolumeSeekBarPreference";
  private Callback mCallback;
  private int mIconResId;
  private ImageView mIconView;
  private int mMuteIconResId;
  private boolean mMuted;
  private SeekBar mSeekBar;
  private boolean mStopped;
  private int mStream;
  private String mSuppressionText;
  private TextView mSuppressionTextView;
  private OPSeekBarVolumizer mVolumizer;
  private boolean mZenMuted;
  
  public VolumeSeekBarPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public VolumeSeekBarPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public VolumeSeekBarPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public VolumeSeekBarPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setLayoutResource(2130968925);
  }
  
  private Uri getMediaVolumeUri()
  {
    return Uri.parse("android.resource://" + getContext().getPackageName() + "/" + 2131296263);
  }
  
  private void init()
  {
    if (this.mSeekBar == null) {
      return;
    }
    OPSeekBarVolumizer.Callback local1 = new OPSeekBarVolumizer.Callback()
    {
      public void onMuted(boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2)
      {
        VolumeSeekBarPreference.-set0(VolumeSeekBarPreference.this, paramAnonymousBoolean1);
        VolumeSeekBarPreference.-set1(VolumeSeekBarPreference.this, paramAnonymousBoolean2);
        VolumeSeekBarPreference.-wrap0(VolumeSeekBarPreference.this);
      }
      
      public void onProgressChanged(SeekBar paramAnonymousSeekBar, int paramAnonymousInt, boolean paramAnonymousBoolean)
      {
        if (VolumeSeekBarPreference.-get0(VolumeSeekBarPreference.this) != null)
        {
          VolumeSeekBarPreference.-get0(VolumeSeekBarPreference.this).onStreamValueChanged(VolumeSeekBarPreference.-get1(VolumeSeekBarPreference.this), paramAnonymousInt);
          VolumeSeekBarPreference.-wrap0(VolumeSeekBarPreference.this);
        }
      }
      
      public void onSampleStarting(OPSeekBarVolumizer paramAnonymousOPSeekBarVolumizer)
      {
        if (VolumeSeekBarPreference.-get0(VolumeSeekBarPreference.this) != null) {
          VolumeSeekBarPreference.-get0(VolumeSeekBarPreference.this).onSampleStarting(paramAnonymousOPSeekBarVolumizer);
        }
      }
    };
    if (this.mStream == 3) {}
    for (Uri localUri = getMediaVolumeUri();; localUri = null)
    {
      if (this.mVolumizer == null) {
        this.mVolumizer = new OPSeekBarVolumizer(getContext(), this.mStream, localUri, local1);
      }
      this.mVolumizer.start();
      this.mVolumizer.setSeekBar(this.mSeekBar);
      updateIconView();
      this.mCallback.onStreamValueChanged(this.mStream, this.mSeekBar.getProgress());
      updateSuppressionText();
      if (!isEnabled())
      {
        this.mSeekBar.setEnabled(false);
        this.mVolumizer.stop();
      }
      return;
    }
  }
  
  private void updateIconView()
  {
    if (this.mIconView == null) {
      return;
    }
    if (this.mIconResId != 0)
    {
      this.mIconView.setImageResource(this.mIconResId);
      return;
    }
    if (this.mSeekBar.getProgress() == 0)
    {
      this.mIconView.setImageResource(this.mMuteIconResId);
      return;
    }
    this.mIconView.setImageDrawable(getIcon());
  }
  
  private void updateSuppressionText()
  {
    int k = 4;
    Object localObject;
    int j;
    if ((this.mSuppressionTextView != null) && (this.mSeekBar != null))
    {
      this.mSuppressionTextView.setText(this.mSuppressionText);
      if (!TextUtils.isEmpty(this.mSuppressionText)) {
        break label76;
      }
      i = 0;
      localObject = this.mSuppressionTextView;
      if (i == 0) {
        break label81;
      }
      j = 0;
      label51:
      ((TextView)localObject).setVisibility(j);
      localObject = this.mSeekBar;
      if (i == 0) {
        break label86;
      }
    }
    label76:
    label81:
    label86:
    for (int i = k;; i = 0)
    {
      ((SeekBar)localObject).setVisibility(i);
      return;
      i = 1;
      break;
      j = 4;
      break label51;
    }
  }
  
  public void onActivityPause()
  {
    this.mStopped = true;
    if (this.mVolumizer != null) {
      this.mVolumizer.stop();
    }
  }
  
  public void onActivityResume()
  {
    if (this.mStopped) {
      init();
    }
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    if (this.mStream == 0)
    {
      Log.w("VolumeSeekBarPreference", "No stream found, not binding volumizer");
      return;
    }
    paramPreferenceViewHolder.setDividerAllowedAbove(false);
    this.mSeekBar = ((SeekBar)paramPreferenceViewHolder.findViewById(16909275));
    this.mIconView = ((ImageView)paramPreferenceViewHolder.findViewById(16908294));
    this.mSuppressionTextView = ((TextView)paramPreferenceViewHolder.findViewById(2131362453));
    init();
  }
  
  public void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean)
  {
    super.onProgressChanged(paramSeekBar, paramInt, paramBoolean);
    if (this.mCallback != null) {
      this.mCallback.onStreamValueChanged(this.mStream, paramInt);
    }
  }
  
  public void setCallback(Callback paramCallback)
  {
    this.mCallback = paramCallback;
  }
  
  public void setMuteIcon(int paramInt)
  {
    if (this.mMuteIconResId == paramInt) {
      return;
    }
    this.mMuteIconResId = paramInt;
    updateIconView();
  }
  
  public void setSeekbar(int paramInt)
  {
    if (this.mSeekBar != null) {
      this.mSeekBar.setProgress(paramInt);
    }
  }
  
  public void setStream(int paramInt)
  {
    this.mStream = paramInt;
  }
  
  public void setSuppressionText(String paramString)
  {
    if (Objects.equals(paramString, this.mSuppressionText)) {
      return;
    }
    this.mSuppressionText = paramString;
    updateSuppressionText();
  }
  
  public void showIcon(int paramInt)
  {
    if (this.mIconResId == paramInt) {
      return;
    }
    this.mIconResId = paramInt;
    updateIconView();
  }
  
  public static abstract interface Callback
  {
    public abstract void onSampleStarting(OPSeekBarVolumizer paramOPSeekBarVolumizer);
    
    public abstract void onStreamValueChanged(int paramInt1, int paramInt2);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\VolumeSeekBarPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */