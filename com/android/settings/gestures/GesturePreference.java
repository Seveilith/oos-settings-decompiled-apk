package com.android.settings.gestures;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.net.Uri.Builder;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.android.settings.R.styleable;

public final class GesturePreference
  extends SwitchPreference
{
  private static final String TAG = "GesturePreference";
  private boolean mAnimationAvailable;
  private final Context mContext;
  private MediaPlayer mMediaPlayer;
  private int mPreviewResource;
  private boolean mScrolling;
  private Uri mVideoPath;
  private boolean mVideoReady;
  
  public GesturePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContext = paramContext;
    paramAttributeSet = paramContext.getTheme().obtainStyledAttributes(paramAttributeSet, R.styleable.GesturePreference, 0, 0);
    try
    {
      int i = paramAttributeSet.getResourceId(0, 0);
      this.mVideoPath = new Uri.Builder().scheme("android.resource").authority(paramContext.getPackageName()).appendPath(String.valueOf(i)).build();
      this.mMediaPlayer = MediaPlayer.create(this.mContext, this.mVideoPath);
      if ((this.mMediaPlayer != null) && (this.mMediaPlayer.getDuration() > 0))
      {
        setLayoutResource(2130968714);
        this.mPreviewResource = paramAttributeSet.getResourceId(1, 0);
        this.mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener()
        {
          public void onSeekComplete(MediaPlayer paramAnonymousMediaPlayer)
          {
            GesturePreference.-set1(GesturePreference.this, true);
          }
        });
        this.mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
          public void onPrepared(MediaPlayer paramAnonymousMediaPlayer)
          {
            paramAnonymousMediaPlayer.setLooping(true);
          }
        });
        this.mAnimationAvailable = true;
      }
      return;
    }
    catch (Exception paramContext)
    {
      Log.w("GesturePreference", "Animation resource not found. Will not show animation.");
      return;
    }
    finally
    {
      paramAttributeSet.recycle();
    }
  }
  
  public void onBindViewHolder(final PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    if (!this.mAnimationAvailable) {
      return;
    }
    TextureView localTextureView = (TextureView)paramPreferenceViewHolder.findViewById(2131362168);
    final ImageView localImageView = (ImageView)paramPreferenceViewHolder.findViewById(2131362169);
    localImageView.setImageResource(this.mPreviewResource);
    paramPreferenceViewHolder = (ImageView)paramPreferenceViewHolder.findViewById(2131362170);
    localTextureView.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (GesturePreference.-get0(GesturePreference.this) != null)
        {
          if (GesturePreference.-get0(GesturePreference.this).isPlaying())
          {
            GesturePreference.-get0(GesturePreference.this).pause();
            paramPreferenceViewHolder.setVisibility(0);
          }
        }
        else {
          return;
        }
        GesturePreference.-get0(GesturePreference.this).start();
        paramPreferenceViewHolder.setVisibility(8);
      }
    });
    localTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener()
    {
      public void onSurfaceTextureAvailable(SurfaceTexture paramAnonymousSurfaceTexture, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        if (GesturePreference.-get0(GesturePreference.this) != null)
        {
          GesturePreference.-get0(GesturePreference.this).setSurface(new Surface(paramAnonymousSurfaceTexture));
          GesturePreference.-set1(GesturePreference.this, false);
          GesturePreference.-get0(GesturePreference.this).seekTo(0);
        }
      }
      
      public boolean onSurfaceTextureDestroyed(SurfaceTexture paramAnonymousSurfaceTexture)
      {
        localImageView.setVisibility(0);
        return false;
      }
      
      public void onSurfaceTextureSizeChanged(SurfaceTexture paramAnonymousSurfaceTexture, int paramAnonymousInt1, int paramAnonymousInt2) {}
      
      public void onSurfaceTextureUpdated(SurfaceTexture paramAnonymousSurfaceTexture)
      {
        if ((GesturePreference.-get2(GesturePreference.this)) && (localImageView.getVisibility() == 0)) {
          localImageView.setVisibility(8);
        }
        do
        {
          while ((GesturePreference.-get0(GesturePreference.this) == null) || (GesturePreference.-get0(GesturePreference.this).isPlaying()))
          {
            return;
            if (GesturePreference.-get1(GesturePreference.this))
            {
              GesturePreference.-set0(GesturePreference.this, false);
              if ((GesturePreference.-get0(GesturePreference.this) != null) && (GesturePreference.-get0(GesturePreference.this).isPlaying()))
              {
                GesturePreference.-get0(GesturePreference.this).pause();
                paramPreferenceViewHolder.setVisibility(0);
              }
            }
          }
        } while (paramPreferenceViewHolder.getVisibility() == 0);
        paramPreferenceViewHolder.setVisibility(0);
      }
    });
  }
  
  public void onDetached()
  {
    if (this.mMediaPlayer != null)
    {
      this.mMediaPlayer.stop();
      this.mMediaPlayer.reset();
      this.mMediaPlayer.release();
    }
    super.onDetached();
  }
  
  void onViewInvisible()
  {
    if ((this.mMediaPlayer != null) && (this.mMediaPlayer.isPlaying())) {
      this.mMediaPlayer.pause();
    }
  }
  
  void onViewVisible()
  {
    if ((!this.mVideoReady) || (this.mMediaPlayer == null) || (this.mMediaPlayer.isPlaying())) {
      return;
    }
    this.mMediaPlayer.seekTo(0);
  }
  
  void setScrolling(boolean paramBoolean)
  {
    this.mScrolling = paramBoolean;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\gestures\GesturePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */