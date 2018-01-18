package com.android.settings.accessibility;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.VideoView;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.ToggleSwitch;
import com.android.settings.widget.ToggleSwitch.OnBeforeCheckedChangeListener;

public class ToggleScreenMagnificationPreferenceFragment
  extends ToggleFeaturePreferenceFragment
{
  protected VideoPreference mVideoPreference;
  
  private static int getScreenWidth(Context paramContext)
  {
    paramContext = ((WindowManager)paramContext.getSystemService("window")).getDefaultDisplay();
    Point localPoint = new Point();
    paramContext.getSize(localPoint);
    return localPoint.x;
  }
  
  private void setMagnificationEnabled(int paramInt)
  {
    Settings.Secure.putInt(getContentResolver(), "accessibility_display_magnification_enabled", paramInt);
  }
  
  protected int getMetricsCategory()
  {
    return 7;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mVideoPreference = new VideoPreference(getPrefContext());
    this.mVideoPreference.setSelectable(false);
    this.mVideoPreference.setPersistent(false);
    this.mVideoPreference.setLayoutResource(2130969103);
    paramBundle = getPreferenceManager().getPreferenceScreen();
    paramBundle.setOrderingAsAdded(false);
    this.mVideoPreference.setOrder(0);
    this.mSummaryPreference.setOrder(1);
    paramBundle.addPreference(this.mVideoPreference);
  }
  
  protected void onInstallSwitchBarToggleSwitch()
  {
    super.onInstallSwitchBarToggleSwitch();
    this.mToggleSwitch.setOnBeforeCheckedChangeListener(new ToggleSwitch.OnBeforeCheckedChangeListener()
    {
      public boolean onBeforeCheckedChanged(ToggleSwitch paramAnonymousToggleSwitch, boolean paramAnonymousBoolean)
      {
        ToggleScreenMagnificationPreferenceFragment.this.mSwitchBar.setCheckedInternal(paramAnonymousBoolean);
        ToggleScreenMagnificationPreferenceFragment.this.getArguments().putBoolean("checked", paramAnonymousBoolean);
        ToggleScreenMagnificationPreferenceFragment.this.onPreferenceToggled(ToggleScreenMagnificationPreferenceFragment.this.mPreferenceKey, paramAnonymousBoolean);
        return false;
      }
    });
  }
  
  public void onPause()
  {
    super.onPause();
    if (!this.mToggleSwitch.isChecked()) {
      setMagnificationEnabled(0);
    }
  }
  
  protected void onPreferenceToggled(String paramString, boolean paramBoolean) {}
  
  public void onResume()
  {
    super.onResume();
    if (Settings.Secure.getInt(getContentResolver(), "accessibility_display_magnification_enabled", 0) == 0) {
      setMagnificationEnabled(1);
    }
    VideoView localVideoView = (VideoView)getView().findViewById(2131362678);
    if (localVideoView != null) {
      localVideoView.start();
    }
  }
  
  protected class VideoPreference
    extends Preference
  {
    private ViewTreeObserver.OnGlobalLayoutListener mLayoutListener;
    private ImageView mVideoBackgroundView;
    
    public VideoPreference(Context paramContext)
    {
      super();
    }
    
    public void onBindViewHolder(final PreferenceViewHolder paramPreferenceViewHolder)
    {
      super.onBindViewHolder(paramPreferenceViewHolder);
      Resources localResources = ToggleScreenMagnificationPreferenceFragment.-wrap0(ToggleScreenMagnificationPreferenceFragment.this).getResources();
      final int i = localResources.getDimensionPixelSize(2131755619);
      final int j = localResources.getDimensionPixelSize(2131755621);
      final int k = localResources.getDimensionPixelSize(2131755622);
      final int m = localResources.getDimensionPixelSize(2131755623);
      paramPreferenceViewHolder.setDividerAllowedAbove(false);
      paramPreferenceViewHolder.setDividerAllowedBelow(false);
      this.mVideoBackgroundView = ((ImageView)paramPreferenceViewHolder.findViewById(2131362677));
      paramPreferenceViewHolder = (VideoView)paramPreferenceViewHolder.findViewById(2131362678);
      paramPreferenceViewHolder.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
      {
        public void onPrepared(MediaPlayer paramAnonymousMediaPlayer)
        {
          paramAnonymousMediaPlayer.setLooping(true);
        }
      });
      paramPreferenceViewHolder.setVideoURI(Uri.parse(String.format("%s://%s/%s", new Object[] { "android.resource", ToggleScreenMagnificationPreferenceFragment.-wrap0(ToggleScreenMagnificationPreferenceFragment.this).getPackageName(), Integer.valueOf(2131296256) })));
      paramPreferenceViewHolder.setMediaController(null);
      this.mLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener()
      {
        public void onGlobalLayout()
        {
          int i = ToggleScreenMagnificationPreferenceFragment.VideoPreference.-get0(ToggleScreenMagnificationPreferenceFragment.VideoPreference.this).getWidth();
          RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams)paramPreferenceViewHolder.getLayoutParams();
          localLayoutParams.width = (j * i / i);
          localLayoutParams.height = (k * i / i);
          localLayoutParams.setMargins(0, m * i / i, 0, 0);
          paramPreferenceViewHolder.setLayoutParams(localLayoutParams);
          paramPreferenceViewHolder.invalidate();
          paramPreferenceViewHolder.start();
        }
      };
      this.mVideoBackgroundView.getViewTreeObserver().addOnGlobalLayoutListener(this.mLayoutListener);
    }
    
    protected void onPrepareForRemoval()
    {
      this.mVideoBackgroundView.getViewTreeObserver().removeOnGlobalLayoutListener(this.mLayoutListener);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accessibility\ToggleScreenMagnificationPreferenceFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */