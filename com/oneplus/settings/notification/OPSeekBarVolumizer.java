package com.oneplus.settings.notification;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.AudioAttributes.Builder;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.VolumePreference.VolumeStore;
import android.provider.Settings.System;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.oem.os.ThreeKeyManager;

public class OPSeekBarVolumizer
  implements SeekBar.OnSeekBarChangeListener, Handler.Callback
{
  private static final int CHECK_RINGTONE_PLAYBACK_DELAY_MS = 1000;
  private static final int MSG_INIT_SAMPLE = 3;
  private static final int MSG_SET_STREAM_VOLUME = 0;
  private static final int MSG_START_SAMPLE = 1;
  private static final int MSG_STOP_SAMPLE = 2;
  private static final String OEM_HEADSET_PLUG_ACTION = "android.intent.action.HEADSET_PLUG";
  private static final String TAG = "OPSeekBarVolumizer";
  private static final int THREE_KEY_SILENT_VALUE = 1;
  private boolean mAffectedByRingerMode;
  private final AudioManager mAudioManager;
  private final Callback mCallback;
  private final Context mContext;
  private final Uri mDefaultUri;
  private Handler mHandler;
  private int mLastAudibleStreamVolume;
  private int mLastProgress = -1;
  private final int mMaxStreamVolume;
  private boolean mMuted;
  private final NotificationManager mNotificationManager;
  private boolean mNotificationOrRing;
  private int mOriginalStreamVolume;
  private final Receiver mReceiver = new Receiver(null);
  private int mRingerMode;
  private Ringtone mRingtone;
  private SeekBar mSeekBar;
  private final int mStreamType;
  private final H mUiHandler = new H(null);
  private int mVolumeBeforeMute = -1;
  private Observer mVolumeObserver;
  private int mZenMode;
  
  public OPSeekBarVolumizer(Context paramContext, int paramInt, Uri paramUri, Callback paramCallback)
  {
    this.mContext = paramContext;
    this.mAudioManager = ((AudioManager)paramContext.getSystemService(AudioManager.class));
    this.mNotificationManager = ((NotificationManager)paramContext.getSystemService(NotificationManager.class));
    this.mStreamType = paramInt;
    this.mAffectedByRingerMode = this.mAudioManager.isStreamAffectedByRingerMode(this.mStreamType);
    this.mNotificationOrRing = isNotificationOrRing(this.mStreamType);
    if (this.mNotificationOrRing) {
      this.mRingerMode = this.mAudioManager.getRingerModeInternal();
    }
    this.mZenMode = this.mNotificationManager.getZenMode();
    this.mMaxStreamVolume = this.mAudioManager.getStreamMaxVolume(this.mStreamType);
    this.mCallback = paramCallback;
    this.mOriginalStreamVolume = this.mAudioManager.getStreamVolume(this.mStreamType);
    this.mLastAudibleStreamVolume = this.mAudioManager.getLastAudibleStreamVolume(this.mStreamType);
    this.mMuted = this.mAudioManager.isStreamMute(this.mStreamType);
    if (this.mCallback != null) {
      this.mCallback.onMuted(this.mMuted, isZenMuted());
    }
    paramContext = paramUri;
    if (paramUri == null)
    {
      if (this.mStreamType != 2) {
        break label245;
      }
      paramContext = Settings.System.DEFAULT_RINGTONE_URI;
    }
    for (;;)
    {
      this.mDefaultUri = paramContext;
      return;
      label245:
      if (this.mStreamType == 5) {
        paramContext = Settings.System.DEFAULT_NOTIFICATION_URI;
      } else {
        paramContext = Settings.System.DEFAULT_ALARM_ALERT_URI;
      }
    }
  }
  
  private static boolean isNotificationOrRing(int paramInt)
  {
    return (paramInt == 2) || (paramInt == 5);
  }
  
  private void onInitSample()
  {
    this.mRingtone = RingtoneManager.getRingtone(this.mContext, this.mDefaultUri);
    if (this.mRingtone != null) {}
    try
    {
      this.mRingtone.setStreamType(this.mStreamType);
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  private void onStartSample()
  {
    if (!isSamplePlaying())
    {
      if (this.mCallback != null) {
        this.mCallback.onSampleStarting(this);
      }
      if (this.mRingtone == null) {}
    }
    try
    {
      this.mRingtone.setAudioAttributes(new AudioAttributes.Builder(this.mRingtone.getAudioAttributes()).setFlags(192).build());
      this.mRingtone.play();
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("OPSeekBarVolumizer", "Error playing ringtone, stream " + this.mStreamType, localThrowable);
    }
  }
  
  private void onStopSample()
  {
    if (this.mRingtone != null) {
      this.mRingtone.stop();
    }
  }
  
  private void postSetVolume(int paramInt)
  {
    if (this.mHandler == null) {
      return;
    }
    this.mLastProgress = paramInt;
    this.mHandler.removeMessages(0);
    this.mHandler.sendMessage(this.mHandler.obtainMessage(0));
  }
  
  private void postStartSample()
  {
    if (this.mHandler == null) {
      return;
    }
    this.mHandler.removeMessages(1);
    Handler localHandler = this.mHandler;
    Message localMessage = this.mHandler.obtainMessage(1);
    if (isSamplePlaying()) {}
    for (int i = 1000;; i = 0)
    {
      localHandler.sendMessageDelayed(localMessage, i);
      return;
    }
  }
  
  private void postStopSample()
  {
    if (this.mHandler == null) {
      return;
    }
    this.mHandler.removeMessages(1);
    this.mHandler.removeMessages(2);
    this.mHandler.sendMessage(this.mHandler.obtainMessage(2));
  }
  
  private void updateSlider()
  {
    if ((this.mSeekBar != null) && (this.mAudioManager != null))
    {
      int i = this.mAudioManager.getStreamVolume(this.mStreamType);
      int j = this.mAudioManager.getLastAudibleStreamVolume(this.mStreamType);
      boolean bool = this.mAudioManager.isStreamMute(this.mStreamType);
      this.mUiHandler.postUpdateSlider(i, j, bool);
    }
  }
  
  public void changeVolumeBy(int paramInt)
  {
    this.mSeekBar.incrementProgressBy(paramInt);
    postSetVolume(this.mSeekBar.getProgress());
    postStartSample();
    this.mVolumeBeforeMute = -1;
  }
  
  public SeekBar getSeekBar()
  {
    return this.mSeekBar;
  }
  
  public int getThreeKeyStatus(Context paramContext)
  {
    int i = 0;
    if (paramContext == null)
    {
      Log.e("OPSeekBarVolumizer", "getThreeKeyStatus error, context is null");
      return 0;
    }
    paramContext = (ThreeKeyManager)paramContext.getSystemService("threekey");
    if (paramContext != null) {}
    try
    {
      i = paramContext.getThreeKeyStatus();
      return i;
    }
    catch (Exception paramContext)
    {
      Log.e("OPSeekBarVolumizer", "Exception occurs, Three Key Service may not ready", paramContext);
    }
    return 0;
  }
  
  public boolean handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      Log.e("OPSeekBarVolumizer", "invalid SeekBarVolumizer message: " + paramMessage.what);
    }
    for (;;)
    {
      return true;
      if ((this.mMuted) && (this.mLastProgress > 0)) {
        this.mAudioManager.adjustStreamVolume(this.mStreamType, 100, 0);
      }
      for (;;)
      {
        this.mAudioManager.setStreamVolume(this.mStreamType, this.mLastProgress, 1024);
        break;
        if ((!this.mMuted) && (this.mLastProgress == 0)) {
          this.mAudioManager.adjustStreamVolume(this.mStreamType, -100, 0);
        }
      }
      onStartSample();
      continue;
      onStopSample();
      continue;
      onInitSample();
    }
  }
  
  public boolean isSamplePlaying()
  {
    try
    {
      if (this.mRingtone != null)
      {
        boolean bool = this.mRingtone.isPlaying();
        return bool;
      }
      return false;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return true;
  }
  
  public boolean isZenMuted()
  {
    return getThreeKeyStatus(this.mContext) == 1;
  }
  
  public void muteVolume()
  {
    if (this.mVolumeBeforeMute != -1)
    {
      this.mSeekBar.setProgress(this.mVolumeBeforeMute);
      postSetVolume(this.mVolumeBeforeMute);
      postStartSample();
      this.mVolumeBeforeMute = -1;
      return;
    }
    this.mVolumeBeforeMute = this.mSeekBar.getProgress();
    this.mSeekBar.setProgress(0);
    postStopSample();
    postSetVolume(0);
  }
  
  public void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean)
  {
    int i = paramInt;
    if (!this.mMuted)
    {
      i = paramInt;
      if (this.mNotificationOrRing)
      {
        i = paramInt;
        if (paramInt < 1)
        {
          i = 1;
          paramSeekBar.setProgress(1);
        }
      }
    }
    if (this.mCallback != null) {
      this.mCallback.onProgressChanged(paramSeekBar, i, paramBoolean);
    }
  }
  
  public void onRestoreInstanceState(VolumePreference.VolumeStore paramVolumeStore)
  {
    if (paramVolumeStore.volume != -1)
    {
      this.mOriginalStreamVolume = paramVolumeStore.originalVolume;
      this.mLastProgress = paramVolumeStore.volume;
      postSetVolume(this.mLastProgress);
    }
  }
  
  public void onSaveInstanceState(VolumePreference.VolumeStore paramVolumeStore)
  {
    if (this.mLastProgress >= 0)
    {
      paramVolumeStore.volume = this.mLastProgress;
      paramVolumeStore.originalVolume = this.mOriginalStreamVolume;
    }
  }
  
  public void onStartTrackingTouch(SeekBar paramSeekBar) {}
  
  public void onStopTrackingTouch(SeekBar paramSeekBar)
  {
    postSetVolume(paramSeekBar.getProgress());
    postStartSample();
  }
  
  public void revertVolume()
  {
    this.mAudioManager.setStreamVolume(this.mStreamType, this.mOriginalStreamVolume, 0);
  }
  
  public void setSeekBar(SeekBar paramSeekBar)
  {
    if (this.mSeekBar != null) {
      this.mSeekBar.setOnSeekBarChangeListener(null);
    }
    this.mSeekBar = paramSeekBar;
    this.mSeekBar.setOnSeekBarChangeListener(null);
    this.mSeekBar.setMax(this.mMaxStreamVolume);
    updateSeekBar();
    this.mSeekBar.setOnSeekBarChangeListener(this);
  }
  
  public void start()
  {
    if (this.mHandler != null) {
      return;
    }
    HandlerThread localHandlerThread = new HandlerThread("OPSeekBarVolumizer.CallbackHandler");
    localHandlerThread.start();
    this.mHandler = new Handler(localHandlerThread.getLooper(), this);
    this.mHandler.sendEmptyMessage(3);
    this.mVolumeObserver = new Observer(this.mHandler);
    this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.VOLUME_SETTINGS[this.mStreamType]), false, this.mVolumeObserver);
    this.mReceiver.setListening(true);
  }
  
  public void startSample()
  {
    postStartSample();
  }
  
  public void stop()
  {
    if (this.mHandler == null) {
      return;
    }
    postStopSample();
    this.mContext.getContentResolver().unregisterContentObserver(this.mVolumeObserver);
    this.mReceiver.setListening(false);
    this.mSeekBar.setOnSeekBarChangeListener(null);
    this.mHandler.getLooper().quitSafely();
    this.mHandler = null;
    this.mVolumeObserver = null;
  }
  
  public void stopSample()
  {
    postStopSample();
  }
  
  protected void updateSeekBar()
  {
    boolean bool = isZenMuted();
    if (this.mNotificationOrRing)
    {
      localSeekBar = this.mSeekBar;
      if (!bool) {
        break label52;
      }
    }
    label52:
    for (bool = false;; bool = true)
    {
      localSeekBar.setEnabled(bool);
      if ((!this.mNotificationOrRing) || (this.mRingerMode != 1)) {
        break;
      }
      this.mSeekBar.setProgress(0);
      return;
    }
    if (this.mMuted)
    {
      this.mSeekBar.setProgress(0);
      return;
    }
    SeekBar localSeekBar = this.mSeekBar;
    if (this.mLastProgress > -1) {}
    for (int i = this.mLastProgress;; i = this.mOriginalStreamVolume)
    {
      localSeekBar.setProgress(i);
      return;
    }
  }
  
  public static abstract interface Callback
  {
    public abstract void onMuted(boolean paramBoolean1, boolean paramBoolean2);
    
    public abstract void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean);
    
    public abstract void onSampleStarting(OPSeekBarVolumizer paramOPSeekBarVolumizer);
  }
  
  private final class H
    extends Handler
  {
    private static final int UPDATE_SLIDER = 1;
    
    private H() {}
    
    public void handleMessage(Message paramMessage)
    {
      if ((paramMessage.what == 1) && (OPSeekBarVolumizer.-get8(OPSeekBarVolumizer.this) != null))
      {
        OPSeekBarVolumizer.-set1(OPSeekBarVolumizer.this, paramMessage.arg1);
        OPSeekBarVolumizer.-set0(OPSeekBarVolumizer.this, Math.abs(paramMessage.arg2));
        if (paramMessage.arg2 >= 0) {
          break label118;
        }
      }
      label118:
      for (boolean bool = true;; bool = false)
      {
        if (bool != OPSeekBarVolumizer.-get5(OPSeekBarVolumizer.this))
        {
          OPSeekBarVolumizer.-set2(OPSeekBarVolumizer.this, bool);
          if (OPSeekBarVolumizer.-get2(OPSeekBarVolumizer.this) != null) {
            OPSeekBarVolumizer.-get2(OPSeekBarVolumizer.this).onMuted(OPSeekBarVolumizer.-get5(OPSeekBarVolumizer.this), OPSeekBarVolumizer.this.isZenMuted());
          }
        }
        OPSeekBarVolumizer.this.updateSeekBar();
        return;
      }
    }
    
    public void postUpdateSlider(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (int i = -1;; i = 1)
      {
        obtainMessage(1, paramInt1, paramInt2 * i).sendToTarget();
        return;
      }
    }
  }
  
  private final class Observer
    extends ContentObserver
  {
    public Observer(Handler paramHandler)
    {
      super();
    }
    
    public void onChange(boolean paramBoolean)
    {
      super.onChange(paramBoolean);
      OPSeekBarVolumizer.-wrap1(OPSeekBarVolumizer.this);
    }
  }
  
  private final class Receiver
    extends BroadcastReceiver
  {
    private boolean mListening;
    
    private Receiver() {}
    
    private void updateVolumeSlider(int paramInt1, int paramInt2)
    {
      boolean bool;
      if (OPSeekBarVolumizer.-get7(OPSeekBarVolumizer.this))
      {
        bool = OPSeekBarVolumizer.-wrap0(paramInt1);
        if ((OPSeekBarVolumizer.-get8(OPSeekBarVolumizer.this) != null) && (bool) && (paramInt2 != -1))
        {
          if (OPSeekBarVolumizer.-get1(OPSeekBarVolumizer.this).isStreamMute(OPSeekBarVolumizer.-get9(OPSeekBarVolumizer.this))) {
            break label101;
          }
          if (paramInt2 != 0) {
            break label106;
          }
          bool = true;
        }
      }
      for (;;)
      {
        OPSeekBarVolumizer.-get10(OPSeekBarVolumizer.this).postUpdateSlider(paramInt2, OPSeekBarVolumizer.-get4(OPSeekBarVolumizer.this), bool);
        return;
        if (paramInt1 == OPSeekBarVolumizer.-get9(OPSeekBarVolumizer.this))
        {
          bool = true;
          break;
        }
        bool = false;
        break;
        label101:
        bool = true;
        continue;
        label106:
        bool = false;
      }
    }
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      paramContext = paramIntent.getAction();
      int j;
      if ("android.media.VOLUME_CHANGED_ACTION".equals(paramContext))
      {
        i = paramIntent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1);
        j = paramIntent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", -1);
        if ((OPSeekBarVolumizer.-get9(OPSeekBarVolumizer.this) != 3) || (!OPSeekBarVolumizer.-get1(OPSeekBarVolumizer.this).isWiredHeadsetOn()) || (OPSeekBarVolumizer.-get1(OPSeekBarVolumizer.this).isMusicActive())) {
          updateVolumeSlider(i, j);
        }
      }
      do
      {
        do
        {
          do
          {
            return;
            return;
            if (!"android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION".equals(paramContext)) {
              break;
            }
            if (OPSeekBarVolumizer.-get7(OPSeekBarVolumizer.this)) {
              OPSeekBarVolumizer.-set3(OPSeekBarVolumizer.this, OPSeekBarVolumizer.-get1(OPSeekBarVolumizer.this).getRingerModeInternal());
            }
          } while ((!OPSeekBarVolumizer.-get0(OPSeekBarVolumizer.this)) || (OPSeekBarVolumizer.-get1(OPSeekBarVolumizer.this).isWiredHeadsetOn()) || (OPSeekBarVolumizer.-get1(OPSeekBarVolumizer.this).isMusicActive()));
          OPSeekBarVolumizer.-wrap1(OPSeekBarVolumizer.this);
          return;
          if ("android.media.STREAM_DEVICES_CHANGED_ACTION".equals(paramContext))
          {
            i = paramIntent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1);
            j = OPSeekBarVolumizer.-get1(OPSeekBarVolumizer.this).getStreamVolume(i);
            if (((OPSeekBarVolumizer.-get9(OPSeekBarVolumizer.this) != 4) && (OPSeekBarVolumizer.-get9(OPSeekBarVolumizer.this) != 3)) || (!OPSeekBarVolumizer.-get1(OPSeekBarVolumizer.this).isWiredHeadsetOn()) || (OPSeekBarVolumizer.-get1(OPSeekBarVolumizer.this).isMusicActive()))
            {
              if (!OPSeekBarVolumizer.-wrap0(i)) {}
            }
            else {
              return;
            }
            updateVolumeSlider(i, j);
            return;
          }
          if ("android.app.action.INTERRUPTION_FILTER_CHANGED".equals(paramContext))
          {
            OPSeekBarVolumizer.-set4(OPSeekBarVolumizer.this, OPSeekBarVolumizer.-get6(OPSeekBarVolumizer.this).getZenMode());
            OPSeekBarVolumizer.-wrap1(OPSeekBarVolumizer.this);
            return;
          }
        } while ((!"android.intent.action.HEADSET_PLUG".equals(paramContext)) || (OPSeekBarVolumizer.-get8(OPSeekBarVolumizer.this) == null) || (OPSeekBarVolumizer.-get1(OPSeekBarVolumizer.this) == null));
        Log.w("OPSeekBarVolumizer", "VOLUME_CHANGED_ACTION mStreamType : " + OPSeekBarVolumizer.-get9(OPSeekBarVolumizer.this));
      } while (OPSeekBarVolumizer.-get9(OPSeekBarVolumizer.this) != 3);
      int i = OPSeekBarVolumizer.-get1(OPSeekBarVolumizer.this).getStreamVolume(OPSeekBarVolumizer.-get9(OPSeekBarVolumizer.this));
      OPSeekBarVolumizer.-get1(OPSeekBarVolumizer.this).isStreamMute(OPSeekBarVolumizer.-get9(OPSeekBarVolumizer.this));
      Log.w("OPSeekBarVolumizer", "volume = " + i);
      OPSeekBarVolumizer.-get10(OPSeekBarVolumizer.this).postUpdateSlider(i, OPSeekBarVolumizer.-get4(OPSeekBarVolumizer.this), false);
    }
    
    public void setListening(boolean paramBoolean)
    {
      if (this.mListening == paramBoolean) {
        return;
      }
      this.mListening = paramBoolean;
      if (paramBoolean)
      {
        IntentFilter localIntentFilter = new IntentFilter("android.media.VOLUME_CHANGED_ACTION");
        localIntentFilter.addAction("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION");
        localIntentFilter.addAction("android.intent.action.HEADSET_PLUG");
        localIntentFilter.addAction("android.app.action.INTERRUPTION_FILTER_CHANGED");
        localIntentFilter.addAction("android.media.STREAM_DEVICES_CHANGED_ACTION");
        OPSeekBarVolumizer.-get3(OPSeekBarVolumizer.this).registerReceiver(this, localIntentFilter);
        return;
      }
      OPSeekBarVolumizer.-get3(OPSeekBarVolumizer.this).unregisterReceiver(this);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\notification\OPSeekBarVolumizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */