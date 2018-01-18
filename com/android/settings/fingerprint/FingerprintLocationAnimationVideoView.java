package com.android.settings.fingerprint;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View.MeasureSpec;

public class FingerprintLocationAnimationVideoView
  extends TextureView
  implements FingerprintFindSensorAnimation
{
  protected float mAspect = 1.0F;
  protected MediaPlayer mMediaPlayer;
  
  public FingerprintLocationAnimationVideoView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  protected static Uri resourceEntryToUri(Context paramContext, int paramInt)
  {
    paramContext = paramContext.getResources();
    return Uri.parse("android.resource://" + paramContext.getResourcePackageName(paramInt) + '/' + paramContext.getResourceTypeName(paramInt) + '/' + paramContext.getResourceEntryName(paramInt));
  }
  
  protected Uri getFingerprintLocationAnimation()
  {
    return resourceEntryToUri(getContext(), 2131296257);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    setSurfaceTextureListener(new TextureView.SurfaceTextureListener()
    {
      private SurfaceTexture mTextureToDestroy = null;
      
      public void onSurfaceTextureAvailable(SurfaceTexture paramAnonymousSurfaceTexture, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        FingerprintLocationAnimationVideoView.this.setVisibility(4);
        Uri localUri = FingerprintLocationAnimationVideoView.this.getFingerprintLocationAnimation();
        if (FingerprintLocationAnimationVideoView.this.mMediaPlayer != null) {
          FingerprintLocationAnimationVideoView.this.mMediaPlayer.release();
        }
        if (this.mTextureToDestroy != null)
        {
          this.mTextureToDestroy.release();
          this.mTextureToDestroy = null;
        }
        FingerprintLocationAnimationVideoView.this.mMediaPlayer = MediaPlayer.create(FingerprintLocationAnimationVideoView.-get0(FingerprintLocationAnimationVideoView.this), localUri);
        FingerprintLocationAnimationVideoView.this.mMediaPlayer.setSurface(new Surface(paramAnonymousSurfaceTexture));
        FingerprintLocationAnimationVideoView.this.mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
          public void onPrepared(MediaPlayer paramAnonymous2MediaPlayer)
          {
            paramAnonymous2MediaPlayer.setLooping(true);
          }
        });
        FingerprintLocationAnimationVideoView.this.mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener()
        {
          public boolean onInfo(MediaPlayer paramAnonymous2MediaPlayer, int paramAnonymous2Int1, int paramAnonymous2Int2)
          {
            if (paramAnonymous2Int1 == 3) {
              FingerprintLocationAnimationVideoView.this.setVisibility(0);
            }
            return false;
          }
        });
        FingerprintLocationAnimationVideoView.this.mAspect = (FingerprintLocationAnimationVideoView.this.mMediaPlayer.getVideoHeight() / FingerprintLocationAnimationVideoView.this.mMediaPlayer.getVideoWidth());
        FingerprintLocationAnimationVideoView.this.requestLayout();
        FingerprintLocationAnimationVideoView.this.startAnimation();
      }
      
      public boolean onSurfaceTextureDestroyed(SurfaceTexture paramAnonymousSurfaceTexture)
      {
        this.mTextureToDestroy = paramAnonymousSurfaceTexture;
        return false;
      }
      
      public void onSurfaceTextureSizeChanged(SurfaceTexture paramAnonymousSurfaceTexture, int paramAnonymousInt1, int paramAnonymousInt2) {}
      
      public void onSurfaceTextureUpdated(SurfaceTexture paramAnonymousSurfaceTexture) {}
    });
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = View.MeasureSpec.getSize(paramInt1);
    super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(Math.round(this.mAspect * paramInt2), 1073741824));
  }
  
  public void pauseAnimation()
  {
    if ((this.mMediaPlayer != null) && (this.mMediaPlayer.isPlaying())) {
      this.mMediaPlayer.pause();
    }
  }
  
  public void startAnimation()
  {
    if ((this.mMediaPlayer == null) || (this.mMediaPlayer.isPlaying())) {
      return;
    }
    this.mMediaPlayer.start();
  }
  
  public void stopAnimation()
  {
    if (this.mMediaPlayer != null)
    {
      this.mMediaPlayer.stop();
      this.mMediaPlayer.release();
      this.mMediaPlayer = null;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fingerprint\FingerprintLocationAnimationVideoView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */