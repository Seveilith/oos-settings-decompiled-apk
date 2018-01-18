package android.support.v4.media.session;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.annotation.RestrictTo;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.MediaMetadataCompat.Builder;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.VolumeProviderCompat;
import android.support.v4.media.VolumeProviderCompat.Callback;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MediaSessionCompat
{
  static final String ACTION_ARGUMENT_EXTRAS = "android.support.v4.media.session.action.ARGUMENT_EXTRAS";
  static final String ACTION_ARGUMENT_MEDIA_ID = "android.support.v4.media.session.action.ARGUMENT_MEDIA_ID";
  static final String ACTION_ARGUMENT_QUERY = "android.support.v4.media.session.action.ARGUMENT_QUERY";
  static final String ACTION_ARGUMENT_URI = "android.support.v4.media.session.action.ARGUMENT_URI";
  static final String ACTION_PLAY_FROM_URI = "android.support.v4.media.session.action.PLAY_FROM_URI";
  static final String ACTION_PREPARE = "android.support.v4.media.session.action.PREPARE";
  static final String ACTION_PREPARE_FROM_MEDIA_ID = "android.support.v4.media.session.action.PREPARE_FROM_MEDIA_ID";
  static final String ACTION_PREPARE_FROM_SEARCH = "android.support.v4.media.session.action.PREPARE_FROM_SEARCH";
  static final String ACTION_PREPARE_FROM_URI = "android.support.v4.media.session.action.PREPARE_FROM_URI";
  public static final int FLAG_HANDLES_MEDIA_BUTTONS = 1;
  public static final int FLAG_HANDLES_TRANSPORT_CONTROLS = 2;
  private static final int MAX_BITMAP_SIZE_IN_DP = 320;
  static final String TAG = "MediaSessionCompat";
  static int sMaxBitmapSize;
  private final ArrayList<OnActiveChangeListener> mActiveListeners = new ArrayList();
  private final MediaControllerCompat mController;
  private final MediaSessionImpl mImpl;
  
  private MediaSessionCompat(Context paramContext, MediaSessionImpl paramMediaSessionImpl)
  {
    this.mImpl = paramMediaSessionImpl;
    this.mController = new MediaControllerCompat(paramContext, this);
  }
  
  public MediaSessionCompat(Context paramContext, String paramString)
  {
    this(paramContext, paramString, null, null);
  }
  
  public MediaSessionCompat(Context paramContext, String paramString, ComponentName paramComponentName, PendingIntent paramPendingIntent)
  {
    if (paramContext == null) {
      throw new IllegalArgumentException("context must not be null");
    }
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("tag must not be null or empty");
    }
    if (Build.VERSION.SDK_INT >= 21) {}
    for (this.mImpl = new MediaSessionImplApi21(paramContext, paramString);; this.mImpl = new MediaSessionImplBase(paramContext, paramString, paramComponentName, paramPendingIntent))
    {
      this.mController = new MediaControllerCompat(paramContext, this);
      if (sMaxBitmapSize == 0) {
        sMaxBitmapSize = (int)TypedValue.applyDimension(1, 320.0F, paramContext.getResources().getDisplayMetrics());
      }
      return;
    }
  }
  
  public static MediaSessionCompat fromMediaSession(Context paramContext, Object paramObject)
  {
    if ((paramContext == null) || (paramObject == null)) {}
    while (Build.VERSION.SDK_INT < 21) {
      return null;
    }
    return new MediaSessionCompat(paramContext, new MediaSessionImplApi21(paramObject));
  }
  
  @Deprecated
  public static MediaSessionCompat obtain(Context paramContext, Object paramObject)
  {
    return fromMediaSession(paramContext, paramObject);
  }
  
  public void addOnActiveChangeListener(OnActiveChangeListener paramOnActiveChangeListener)
  {
    if (paramOnActiveChangeListener == null) {
      throw new IllegalArgumentException("Listener may not be null");
    }
    this.mActiveListeners.add(paramOnActiveChangeListener);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public String getCallingPackage()
  {
    return this.mImpl.getCallingPackage();
  }
  
  public MediaControllerCompat getController()
  {
    return this.mController;
  }
  
  public Object getMediaSession()
  {
    return this.mImpl.getMediaSession();
  }
  
  public Object getRemoteControlClient()
  {
    return this.mImpl.getRemoteControlClient();
  }
  
  public Token getSessionToken()
  {
    return this.mImpl.getSessionToken();
  }
  
  public boolean isActive()
  {
    return this.mImpl.isActive();
  }
  
  public void release()
  {
    this.mImpl.release();
  }
  
  public void removeOnActiveChangeListener(OnActiveChangeListener paramOnActiveChangeListener)
  {
    if (paramOnActiveChangeListener == null) {
      throw new IllegalArgumentException("Listener may not be null");
    }
    this.mActiveListeners.remove(paramOnActiveChangeListener);
  }
  
  public void sendSessionEvent(String paramString, Bundle paramBundle)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("event cannot be null or empty");
    }
    this.mImpl.sendSessionEvent(paramString, paramBundle);
  }
  
  public void setActive(boolean paramBoolean)
  {
    this.mImpl.setActive(paramBoolean);
    Iterator localIterator = this.mActiveListeners.iterator();
    while (localIterator.hasNext()) {
      ((OnActiveChangeListener)localIterator.next()).onActiveChanged();
    }
  }
  
  public void setCallback(Callback paramCallback)
  {
    setCallback(paramCallback, null);
  }
  
  public void setCallback(Callback paramCallback, Handler paramHandler)
  {
    MediaSessionImpl localMediaSessionImpl = this.mImpl;
    if (paramHandler != null) {}
    for (;;)
    {
      localMediaSessionImpl.setCallback(paramCallback, paramHandler);
      return;
      paramHandler = new Handler();
    }
  }
  
  public void setExtras(Bundle paramBundle)
  {
    this.mImpl.setExtras(paramBundle);
  }
  
  public void setFlags(int paramInt)
  {
    this.mImpl.setFlags(paramInt);
  }
  
  public void setMediaButtonReceiver(PendingIntent paramPendingIntent)
  {
    this.mImpl.setMediaButtonReceiver(paramPendingIntent);
  }
  
  public void setMetadata(MediaMetadataCompat paramMediaMetadataCompat)
  {
    this.mImpl.setMetadata(paramMediaMetadataCompat);
  }
  
  public void setPlaybackState(PlaybackStateCompat paramPlaybackStateCompat)
  {
    this.mImpl.setPlaybackState(paramPlaybackStateCompat);
  }
  
  public void setPlaybackToLocal(int paramInt)
  {
    this.mImpl.setPlaybackToLocal(paramInt);
  }
  
  public void setPlaybackToRemote(VolumeProviderCompat paramVolumeProviderCompat)
  {
    if (paramVolumeProviderCompat == null) {
      throw new IllegalArgumentException("volumeProvider may not be null!");
    }
    this.mImpl.setPlaybackToRemote(paramVolumeProviderCompat);
  }
  
  public void setQueue(List<QueueItem> paramList)
  {
    this.mImpl.setQueue(paramList);
  }
  
  public void setQueueTitle(CharSequence paramCharSequence)
  {
    this.mImpl.setQueueTitle(paramCharSequence);
  }
  
  public void setRatingType(int paramInt)
  {
    this.mImpl.setRatingType(paramInt);
  }
  
  public void setSessionActivity(PendingIntent paramPendingIntent)
  {
    this.mImpl.setSessionActivity(paramPendingIntent);
  }
  
  public static abstract class Callback
  {
    final Object mCallbackObj;
    
    public Callback()
    {
      if (Build.VERSION.SDK_INT >= 24)
      {
        this.mCallbackObj = MediaSessionCompatApi24.createCallback(new StubApi24());
        return;
      }
      if (Build.VERSION.SDK_INT >= 23)
      {
        this.mCallbackObj = MediaSessionCompatApi23.createCallback(new StubApi23());
        return;
      }
      if (Build.VERSION.SDK_INT >= 21)
      {
        this.mCallbackObj = MediaSessionCompatApi21.createCallback(new StubApi21());
        return;
      }
      this.mCallbackObj = null;
    }
    
    public void onCommand(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver) {}
    
    public void onCustomAction(String paramString, Bundle paramBundle) {}
    
    public void onFastForward() {}
    
    public boolean onMediaButtonEvent(Intent paramIntent)
    {
      return false;
    }
    
    public void onPause() {}
    
    public void onPlay() {}
    
    public void onPlayFromMediaId(String paramString, Bundle paramBundle) {}
    
    public void onPlayFromSearch(String paramString, Bundle paramBundle) {}
    
    public void onPlayFromUri(Uri paramUri, Bundle paramBundle) {}
    
    public void onPrepare() {}
    
    public void onPrepareFromMediaId(String paramString, Bundle paramBundle) {}
    
    public void onPrepareFromSearch(String paramString, Bundle paramBundle) {}
    
    public void onPrepareFromUri(Uri paramUri, Bundle paramBundle) {}
    
    public void onRewind() {}
    
    public void onSeekTo(long paramLong) {}
    
    public void onSetRating(RatingCompat paramRatingCompat) {}
    
    public void onSkipToNext() {}
    
    public void onSkipToPrevious() {}
    
    public void onSkipToQueueItem(long paramLong) {}
    
    public void onStop() {}
    
    private class StubApi21
      implements MediaSessionCompatApi21.Callback
    {
      StubApi21() {}
      
      public void onCommand(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver)
      {
        MediaSessionCompat.Callback.this.onCommand(paramString, paramBundle, paramResultReceiver);
      }
      
      public void onCustomAction(String paramString, Bundle paramBundle)
      {
        if (paramString.equals("android.support.v4.media.session.action.PLAY_FROM_URI"))
        {
          paramString = (Uri)paramBundle.getParcelable("android.support.v4.media.session.action.ARGUMENT_URI");
          paramBundle = (Bundle)paramBundle.getParcelable("android.support.v4.media.session.action.ARGUMENT_EXTRAS");
          MediaSessionCompat.Callback.this.onPlayFromUri(paramString, paramBundle);
          return;
        }
        if (paramString.equals("android.support.v4.media.session.action.PREPARE"))
        {
          MediaSessionCompat.Callback.this.onPrepare();
          return;
        }
        if (paramString.equals("android.support.v4.media.session.action.PREPARE_FROM_MEDIA_ID"))
        {
          paramString = paramBundle.getString("android.support.v4.media.session.action.ARGUMENT_MEDIA_ID");
          paramBundle = paramBundle.getBundle("android.support.v4.media.session.action.ARGUMENT_EXTRAS");
          MediaSessionCompat.Callback.this.onPrepareFromMediaId(paramString, paramBundle);
          return;
        }
        if (paramString.equals("android.support.v4.media.session.action.PREPARE_FROM_SEARCH"))
        {
          paramString = paramBundle.getString("android.support.v4.media.session.action.ARGUMENT_QUERY");
          paramBundle = paramBundle.getBundle("android.support.v4.media.session.action.ARGUMENT_EXTRAS");
          MediaSessionCompat.Callback.this.onPrepareFromSearch(paramString, paramBundle);
          return;
        }
        if (paramString.equals("android.support.v4.media.session.action.PREPARE_FROM_URI"))
        {
          paramString = (Uri)paramBundle.getParcelable("android.support.v4.media.session.action.ARGUMENT_URI");
          paramBundle = paramBundle.getBundle("android.support.v4.media.session.action.ARGUMENT_EXTRAS");
          MediaSessionCompat.Callback.this.onPrepareFromUri(paramString, paramBundle);
          return;
        }
        MediaSessionCompat.Callback.this.onCustomAction(paramString, paramBundle);
      }
      
      public void onFastForward()
      {
        MediaSessionCompat.Callback.this.onFastForward();
      }
      
      public boolean onMediaButtonEvent(Intent paramIntent)
      {
        return MediaSessionCompat.Callback.this.onMediaButtonEvent(paramIntent);
      }
      
      public void onPause()
      {
        MediaSessionCompat.Callback.this.onPause();
      }
      
      public void onPlay()
      {
        MediaSessionCompat.Callback.this.onPlay();
      }
      
      public void onPlayFromMediaId(String paramString, Bundle paramBundle)
      {
        MediaSessionCompat.Callback.this.onPlayFromMediaId(paramString, paramBundle);
      }
      
      public void onPlayFromSearch(String paramString, Bundle paramBundle)
      {
        MediaSessionCompat.Callback.this.onPlayFromSearch(paramString, paramBundle);
      }
      
      public void onRewind()
      {
        MediaSessionCompat.Callback.this.onRewind();
      }
      
      public void onSeekTo(long paramLong)
      {
        MediaSessionCompat.Callback.this.onSeekTo(paramLong);
      }
      
      public void onSetRating(Object paramObject)
      {
        MediaSessionCompat.Callback.this.onSetRating(RatingCompat.fromRating(paramObject));
      }
      
      public void onSkipToNext()
      {
        MediaSessionCompat.Callback.this.onSkipToNext();
      }
      
      public void onSkipToPrevious()
      {
        MediaSessionCompat.Callback.this.onSkipToPrevious();
      }
      
      public void onSkipToQueueItem(long paramLong)
      {
        MediaSessionCompat.Callback.this.onSkipToQueueItem(paramLong);
      }
      
      public void onStop()
      {
        MediaSessionCompat.Callback.this.onStop();
      }
    }
    
    private class StubApi23
      extends MediaSessionCompat.Callback.StubApi21
      implements MediaSessionCompatApi23.Callback
    {
      StubApi23()
      {
        super();
      }
      
      public void onPlayFromUri(Uri paramUri, Bundle paramBundle)
      {
        MediaSessionCompat.Callback.this.onPlayFromUri(paramUri, paramBundle);
      }
    }
    
    private class StubApi24
      extends MediaSessionCompat.Callback.StubApi23
      implements MediaSessionCompatApi24.Callback
    {
      StubApi24()
      {
        super();
      }
      
      public void onPrepare()
      {
        MediaSessionCompat.Callback.this.onPrepare();
      }
      
      public void onPrepareFromMediaId(String paramString, Bundle paramBundle)
      {
        MediaSessionCompat.Callback.this.onPrepareFromMediaId(paramString, paramBundle);
      }
      
      public void onPrepareFromSearch(String paramString, Bundle paramBundle)
      {
        MediaSessionCompat.Callback.this.onPrepareFromSearch(paramString, paramBundle);
      }
      
      public void onPrepareFromUri(Uri paramUri, Bundle paramBundle)
      {
        MediaSessionCompat.Callback.this.onPrepareFromUri(paramUri, paramBundle);
      }
    }
  }
  
  static abstract interface MediaSessionImpl
  {
    public abstract String getCallingPackage();
    
    public abstract Object getMediaSession();
    
    public abstract Object getRemoteControlClient();
    
    public abstract MediaSessionCompat.Token getSessionToken();
    
    public abstract boolean isActive();
    
    public abstract void release();
    
    public abstract void sendSessionEvent(String paramString, Bundle paramBundle);
    
    public abstract void setActive(boolean paramBoolean);
    
    public abstract void setCallback(MediaSessionCompat.Callback paramCallback, Handler paramHandler);
    
    public abstract void setExtras(Bundle paramBundle);
    
    public abstract void setFlags(int paramInt);
    
    public abstract void setMediaButtonReceiver(PendingIntent paramPendingIntent);
    
    public abstract void setMetadata(MediaMetadataCompat paramMediaMetadataCompat);
    
    public abstract void setPlaybackState(PlaybackStateCompat paramPlaybackStateCompat);
    
    public abstract void setPlaybackToLocal(int paramInt);
    
    public abstract void setPlaybackToRemote(VolumeProviderCompat paramVolumeProviderCompat);
    
    public abstract void setQueue(List<MediaSessionCompat.QueueItem> paramList);
    
    public abstract void setQueueTitle(CharSequence paramCharSequence);
    
    public abstract void setRatingType(int paramInt);
    
    public abstract void setSessionActivity(PendingIntent paramPendingIntent);
  }
  
  static class MediaSessionImplApi21
    implements MediaSessionCompat.MediaSessionImpl
  {
    private PendingIntent mMediaButtonIntent;
    private final Object mSessionObj;
    private final MediaSessionCompat.Token mToken;
    
    public MediaSessionImplApi21(Context paramContext, String paramString)
    {
      this.mSessionObj = MediaSessionCompatApi21.createSession(paramContext, paramString);
      this.mToken = new MediaSessionCompat.Token(MediaSessionCompatApi21.getSessionToken(this.mSessionObj));
    }
    
    public MediaSessionImplApi21(Object paramObject)
    {
      this.mSessionObj = MediaSessionCompatApi21.verifySession(paramObject);
      this.mToken = new MediaSessionCompat.Token(MediaSessionCompatApi21.getSessionToken(this.mSessionObj));
    }
    
    public String getCallingPackage()
    {
      if (Build.VERSION.SDK_INT < 24) {
        return null;
      }
      return MediaSessionCompatApi24.getCallingPackage(this.mSessionObj);
    }
    
    public Object getMediaSession()
    {
      return this.mSessionObj;
    }
    
    public Object getRemoteControlClient()
    {
      return null;
    }
    
    public MediaSessionCompat.Token getSessionToken()
    {
      return this.mToken;
    }
    
    public boolean isActive()
    {
      return MediaSessionCompatApi21.isActive(this.mSessionObj);
    }
    
    public void release()
    {
      MediaSessionCompatApi21.release(this.mSessionObj);
    }
    
    public void sendSessionEvent(String paramString, Bundle paramBundle)
    {
      MediaSessionCompatApi21.sendSessionEvent(this.mSessionObj, paramString, paramBundle);
    }
    
    public void setActive(boolean paramBoolean)
    {
      MediaSessionCompatApi21.setActive(this.mSessionObj, paramBoolean);
    }
    
    public void setCallback(MediaSessionCompat.Callback paramCallback, Handler paramHandler)
    {
      Object localObject1 = null;
      Object localObject2 = this.mSessionObj;
      if (paramCallback == null) {}
      for (paramCallback = (MediaSessionCompat.Callback)localObject1;; paramCallback = paramCallback.mCallbackObj)
      {
        MediaSessionCompatApi21.setCallback(localObject2, paramCallback, paramHandler);
        return;
      }
    }
    
    public void setExtras(Bundle paramBundle)
    {
      MediaSessionCompatApi21.setExtras(this.mSessionObj, paramBundle);
    }
    
    public void setFlags(int paramInt)
    {
      MediaSessionCompatApi21.setFlags(this.mSessionObj, paramInt);
    }
    
    public void setMediaButtonReceiver(PendingIntent paramPendingIntent)
    {
      this.mMediaButtonIntent = paramPendingIntent;
      MediaSessionCompatApi21.setMediaButtonReceiver(this.mSessionObj, paramPendingIntent);
    }
    
    public void setMetadata(MediaMetadataCompat paramMediaMetadataCompat)
    {
      Object localObject1 = null;
      Object localObject2 = this.mSessionObj;
      if (paramMediaMetadataCompat == null) {}
      for (paramMediaMetadataCompat = (MediaMetadataCompat)localObject1;; paramMediaMetadataCompat = paramMediaMetadataCompat.getMediaMetadata())
      {
        MediaSessionCompatApi21.setMetadata(localObject2, paramMediaMetadataCompat);
        return;
      }
    }
    
    public void setPlaybackState(PlaybackStateCompat paramPlaybackStateCompat)
    {
      Object localObject1 = null;
      Object localObject2 = this.mSessionObj;
      if (paramPlaybackStateCompat == null) {}
      for (paramPlaybackStateCompat = (PlaybackStateCompat)localObject1;; paramPlaybackStateCompat = paramPlaybackStateCompat.getPlaybackState())
      {
        MediaSessionCompatApi21.setPlaybackState(localObject2, paramPlaybackStateCompat);
        return;
      }
    }
    
    public void setPlaybackToLocal(int paramInt)
    {
      MediaSessionCompatApi21.setPlaybackToLocal(this.mSessionObj, paramInt);
    }
    
    public void setPlaybackToRemote(VolumeProviderCompat paramVolumeProviderCompat)
    {
      MediaSessionCompatApi21.setPlaybackToRemote(this.mSessionObj, paramVolumeProviderCompat.getVolumeProvider());
    }
    
    public void setQueue(List<MediaSessionCompat.QueueItem> paramList)
    {
      Object localObject = null;
      if (paramList != null)
      {
        ArrayList localArrayList = new ArrayList();
        paramList = paramList.iterator();
        for (;;)
        {
          localObject = localArrayList;
          if (!paramList.hasNext()) {
            break;
          }
          localArrayList.add(((MediaSessionCompat.QueueItem)paramList.next()).getQueueItem());
        }
      }
      MediaSessionCompatApi21.setQueue(this.mSessionObj, (List)localObject);
    }
    
    public void setQueueTitle(CharSequence paramCharSequence)
    {
      MediaSessionCompatApi21.setQueueTitle(this.mSessionObj, paramCharSequence);
    }
    
    public void setRatingType(int paramInt)
    {
      if (Build.VERSION.SDK_INT < 22) {
        return;
      }
      MediaSessionCompatApi22.setRatingType(this.mSessionObj, paramInt);
    }
    
    public void setSessionActivity(PendingIntent paramPendingIntent)
    {
      MediaSessionCompatApi21.setSessionActivity(this.mSessionObj, paramPendingIntent);
    }
  }
  
  static class MediaSessionImplBase
    implements MediaSessionCompat.MediaSessionImpl
  {
    final AudioManager mAudioManager;
    volatile MediaSessionCompat.Callback mCallback;
    private final Context mContext;
    final RemoteCallbackList<IMediaControllerCallback> mControllerCallbacks = new RemoteCallbackList();
    boolean mDestroyed = false;
    Bundle mExtras;
    int mFlags;
    private MessageHandler mHandler;
    private boolean mIsActive = false;
    private boolean mIsMbrRegistered = false;
    private boolean mIsRccRegistered = false;
    int mLocalStream;
    final Object mLock = new Object();
    private final ComponentName mMediaButtonReceiverComponentName;
    private final PendingIntent mMediaButtonReceiverIntent;
    MediaMetadataCompat mMetadata;
    final String mPackageName;
    List<MediaSessionCompat.QueueItem> mQueue;
    CharSequence mQueueTitle;
    int mRatingType;
    private final Object mRccObj;
    PendingIntent mSessionActivity;
    PlaybackStateCompat mState;
    private final MediaSessionStub mStub;
    final String mTag;
    private final MediaSessionCompat.Token mToken;
    private VolumeProviderCompat.Callback mVolumeCallback = new VolumeProviderCompat.Callback()
    {
      public void onVolumeChanged(VolumeProviderCompat paramAnonymousVolumeProviderCompat)
      {
        if (MediaSessionCompat.MediaSessionImplBase.this.mVolumeProvider != paramAnonymousVolumeProviderCompat) {
          return;
        }
        paramAnonymousVolumeProviderCompat = new ParcelableVolumeInfo(MediaSessionCompat.MediaSessionImplBase.this.mVolumeType, MediaSessionCompat.MediaSessionImplBase.this.mLocalStream, paramAnonymousVolumeProviderCompat.getVolumeControl(), paramAnonymousVolumeProviderCompat.getMaxVolume(), paramAnonymousVolumeProviderCompat.getCurrentVolume());
        MediaSessionCompat.MediaSessionImplBase.this.sendVolumeInfoChanged(paramAnonymousVolumeProviderCompat);
      }
    };
    VolumeProviderCompat mVolumeProvider;
    int mVolumeType;
    
    public MediaSessionImplBase(Context paramContext, String paramString, ComponentName paramComponentName, PendingIntent paramPendingIntent)
    {
      ComponentName localComponentName = paramComponentName;
      if (paramComponentName == null)
      {
        paramComponentName = MediaButtonReceiver.getMediaButtonReceiverComponent(paramContext);
        localComponentName = paramComponentName;
        if (paramComponentName == null)
        {
          Log.w("MediaSessionCompat", "Couldn't find a unique registered media button receiver in the given context.");
          localComponentName = paramComponentName;
        }
      }
      paramComponentName = paramPendingIntent;
      if (localComponentName != null)
      {
        paramComponentName = paramPendingIntent;
        if (paramPendingIntent == null)
        {
          paramComponentName = new Intent("android.intent.action.MEDIA_BUTTON");
          paramComponentName.setComponent(localComponentName);
          paramComponentName = PendingIntent.getBroadcast(paramContext, 0, paramComponentName, 0);
        }
      }
      if (localComponentName == null) {
        throw new IllegalArgumentException("MediaButtonReceiver component may not be null.");
      }
      this.mContext = paramContext;
      this.mPackageName = paramContext.getPackageName();
      this.mAudioManager = ((AudioManager)paramContext.getSystemService("audio"));
      this.mTag = paramString;
      this.mMediaButtonReceiverComponentName = localComponentName;
      this.mMediaButtonReceiverIntent = paramComponentName;
      this.mStub = new MediaSessionStub();
      this.mToken = new MediaSessionCompat.Token(this.mStub);
      this.mRatingType = 0;
      this.mVolumeType = 1;
      this.mLocalStream = 3;
      if (Build.VERSION.SDK_INT >= 14)
      {
        this.mRccObj = MediaSessionCompatApi14.createRemoteControlClient(paramComponentName);
        return;
      }
      this.mRccObj = null;
    }
    
    private void sendEvent(String paramString, Bundle paramBundle)
    {
      int i = this.mControllerCallbacks.beginBroadcast() - 1;
      for (;;)
      {
        if (i >= 0)
        {
          IMediaControllerCallback localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
          try
          {
            localIMediaControllerCallback.onEvent(paramString, paramBundle);
            i -= 1;
          }
          catch (RemoteException localRemoteException)
          {
            for (;;) {}
          }
        }
      }
      this.mControllerCallbacks.finishBroadcast();
    }
    
    private void sendExtras(Bundle paramBundle)
    {
      int i = this.mControllerCallbacks.beginBroadcast() - 1;
      for (;;)
      {
        if (i >= 0)
        {
          IMediaControllerCallback localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
          try
          {
            localIMediaControllerCallback.onExtrasChanged(paramBundle);
            i -= 1;
          }
          catch (RemoteException localRemoteException)
          {
            for (;;) {}
          }
        }
      }
      this.mControllerCallbacks.finishBroadcast();
    }
    
    private void sendMetadata(MediaMetadataCompat paramMediaMetadataCompat)
    {
      int i = this.mControllerCallbacks.beginBroadcast() - 1;
      for (;;)
      {
        if (i >= 0)
        {
          IMediaControllerCallback localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
          try
          {
            localIMediaControllerCallback.onMetadataChanged(paramMediaMetadataCompat);
            i -= 1;
          }
          catch (RemoteException localRemoteException)
          {
            for (;;) {}
          }
        }
      }
      this.mControllerCallbacks.finishBroadcast();
    }
    
    private void sendQueue(List<MediaSessionCompat.QueueItem> paramList)
    {
      int i = this.mControllerCallbacks.beginBroadcast() - 1;
      for (;;)
      {
        if (i >= 0)
        {
          IMediaControllerCallback localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
          try
          {
            localIMediaControllerCallback.onQueueChanged(paramList);
            i -= 1;
          }
          catch (RemoteException localRemoteException)
          {
            for (;;) {}
          }
        }
      }
      this.mControllerCallbacks.finishBroadcast();
    }
    
    private void sendQueueTitle(CharSequence paramCharSequence)
    {
      int i = this.mControllerCallbacks.beginBroadcast() - 1;
      for (;;)
      {
        if (i >= 0)
        {
          IMediaControllerCallback localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
          try
          {
            localIMediaControllerCallback.onQueueTitleChanged(paramCharSequence);
            i -= 1;
          }
          catch (RemoteException localRemoteException)
          {
            for (;;) {}
          }
        }
      }
      this.mControllerCallbacks.finishBroadcast();
    }
    
    private void sendSessionDestroyed()
    {
      int i = this.mControllerCallbacks.beginBroadcast() - 1;
      for (;;)
      {
        if (i >= 0)
        {
          IMediaControllerCallback localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
          try
          {
            localIMediaControllerCallback.onSessionDestroyed();
            i -= 1;
          }
          catch (RemoteException localRemoteException)
          {
            for (;;) {}
          }
        }
      }
      this.mControllerCallbacks.finishBroadcast();
      this.mControllerCallbacks.kill();
    }
    
    private void sendState(PlaybackStateCompat paramPlaybackStateCompat)
    {
      int i = this.mControllerCallbacks.beginBroadcast() - 1;
      for (;;)
      {
        if (i >= 0)
        {
          IMediaControllerCallback localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
          try
          {
            localIMediaControllerCallback.onPlaybackStateChanged(paramPlaybackStateCompat);
            i -= 1;
          }
          catch (RemoteException localRemoteException)
          {
            for (;;) {}
          }
        }
      }
      this.mControllerCallbacks.finishBroadcast();
    }
    
    private boolean update()
    {
      boolean bool2 = false;
      label53:
      boolean bool1;
      if (this.mIsActive)
      {
        if ((!this.mIsMbrRegistered) && ((this.mFlags & 0x1) != 0)) {
          if (Build.VERSION.SDK_INT >= 18)
          {
            MediaSessionCompatApi18.registerMediaButtonEventReceiver(this.mContext, this.mMediaButtonReceiverIntent, this.mMediaButtonReceiverComponentName);
            this.mIsMbrRegistered = true;
            bool1 = bool2;
            if (Build.VERSION.SDK_INT >= 14)
            {
              if ((this.mIsRccRegistered) || ((this.mFlags & 0x2) == 0)) {
                break label190;
              }
              MediaSessionCompatApi14.registerRemoteControlClient(this.mContext, this.mRccObj);
              this.mIsRccRegistered = true;
              bool1 = true;
            }
          }
        }
        label190:
        do
        {
          do
          {
            return bool1;
            ((AudioManager)this.mContext.getSystemService("audio")).registerMediaButtonEventReceiver(this.mMediaButtonReceiverComponentName);
            break;
            if ((!this.mIsMbrRegistered) || ((this.mFlags & 0x1) != 0)) {
              break label53;
            }
            if (Build.VERSION.SDK_INT >= 18) {
              MediaSessionCompatApi18.unregisterMediaButtonEventReceiver(this.mContext, this.mMediaButtonReceiverIntent, this.mMediaButtonReceiverComponentName);
            }
            for (;;)
            {
              this.mIsMbrRegistered = false;
              break;
              ((AudioManager)this.mContext.getSystemService("audio")).unregisterMediaButtonEventReceiver(this.mMediaButtonReceiverComponentName);
            }
            bool1 = bool2;
          } while (!this.mIsRccRegistered);
          bool1 = bool2;
        } while ((this.mFlags & 0x2) != 0);
        MediaSessionCompatApi14.setState(this.mRccObj, 0);
        MediaSessionCompatApi14.unregisterRemoteControlClient(this.mContext, this.mRccObj);
        this.mIsRccRegistered = false;
        return false;
      }
      if (this.mIsMbrRegistered)
      {
        if (Build.VERSION.SDK_INT < 18) {
          break label306;
        }
        MediaSessionCompatApi18.unregisterMediaButtonEventReceiver(this.mContext, this.mMediaButtonReceiverIntent, this.mMediaButtonReceiverComponentName);
      }
      for (;;)
      {
        this.mIsMbrRegistered = false;
        bool1 = bool2;
        if (!this.mIsRccRegistered) {
          break;
        }
        MediaSessionCompatApi14.setState(this.mRccObj, 0);
        MediaSessionCompatApi14.unregisterRemoteControlClient(this.mContext, this.mRccObj);
        this.mIsRccRegistered = false;
        return false;
        label306:
        ((AudioManager)this.mContext.getSystemService("audio")).unregisterMediaButtonEventReceiver(this.mMediaButtonReceiverComponentName);
      }
    }
    
    void adjustVolume(int paramInt1, int paramInt2)
    {
      if (this.mVolumeType == 2)
      {
        if (this.mVolumeProvider != null) {
          this.mVolumeProvider.onAdjustVolume(paramInt1);
        }
        return;
      }
      this.mAudioManager.adjustStreamVolume(this.mLocalStream, paramInt1, paramInt2);
    }
    
    public String getCallingPackage()
    {
      return null;
    }
    
    public Object getMediaSession()
    {
      return null;
    }
    
    public Object getRemoteControlClient()
    {
      return this.mRccObj;
    }
    
    public MediaSessionCompat.Token getSessionToken()
    {
      return this.mToken;
    }
    
    PlaybackStateCompat getStateWithUpdatedPosition()
    {
      long l2 = -1L;
      for (;;)
      {
        PlaybackStateCompat localPlaybackStateCompat;
        long l1;
        synchronized (this.mLock)
        {
          localPlaybackStateCompat = this.mState;
          l1 = l2;
          if (this.mMetadata != null)
          {
            l1 = l2;
            if (this.mMetadata.containsKey("android.media.metadata.DURATION")) {
              l1 = this.mMetadata.getLong("android.media.metadata.DURATION");
            }
          }
          Object localObject2 = null;
          ??? = localObject2;
          if (localPlaybackStateCompat != null)
          {
            if ((localPlaybackStateCompat.getState() != 3) && (localPlaybackStateCompat.getState() != 4)) {
              break label192;
            }
            l2 = localPlaybackStateCompat.getLastPositionUpdateTime();
            long l3 = SystemClock.elapsedRealtime();
            ??? = localObject2;
            if (l2 > 0L)
            {
              l2 = (localPlaybackStateCompat.getPlaybackSpeed() * (float)(l3 - l2)) + localPlaybackStateCompat.getPosition();
              if ((l1 < 0L) || (l2 <= l1)) {
                break label208;
              }
              ??? = new PlaybackStateCompat.Builder(localPlaybackStateCompat);
              ((PlaybackStateCompat.Builder)???).setState(localPlaybackStateCompat.getState(), l1, localPlaybackStateCompat.getPlaybackSpeed(), l3);
              ??? = ((PlaybackStateCompat.Builder)???).build();
            }
          }
          if (??? != null) {
            break;
          }
          return localPlaybackStateCompat;
        }
        label192:
        ??? = localObject3;
        if (localPlaybackStateCompat.getState() == 5)
        {
          continue;
          label208:
          l1 = l2;
          if (l2 < 0L) {
            l1 = 0L;
          }
        }
      }
      return (PlaybackStateCompat)???;
    }
    
    public boolean isActive()
    {
      return this.mIsActive;
    }
    
    void postToHandler(int paramInt)
    {
      postToHandler(paramInt, null);
    }
    
    void postToHandler(int paramInt, Object paramObject)
    {
      postToHandler(paramInt, paramObject, null);
    }
    
    void postToHandler(int paramInt, Object paramObject, Bundle paramBundle)
    {
      synchronized (this.mLock)
      {
        if (this.mHandler != null) {
          this.mHandler.post(paramInt, paramObject, paramBundle);
        }
        return;
      }
    }
    
    public void release()
    {
      this.mIsActive = false;
      this.mDestroyed = true;
      update();
      sendSessionDestroyed();
    }
    
    public void sendSessionEvent(String paramString, Bundle paramBundle)
    {
      sendEvent(paramString, paramBundle);
    }
    
    void sendVolumeInfoChanged(ParcelableVolumeInfo paramParcelableVolumeInfo)
    {
      int i = this.mControllerCallbacks.beginBroadcast() - 1;
      for (;;)
      {
        if (i >= 0)
        {
          IMediaControllerCallback localIMediaControllerCallback = (IMediaControllerCallback)this.mControllerCallbacks.getBroadcastItem(i);
          try
          {
            localIMediaControllerCallback.onVolumeInfoChanged(paramParcelableVolumeInfo);
            i -= 1;
          }
          catch (RemoteException localRemoteException)
          {
            for (;;) {}
          }
        }
      }
      this.mControllerCallbacks.finishBroadcast();
    }
    
    public void setActive(boolean paramBoolean)
    {
      if (paramBoolean == this.mIsActive) {
        return;
      }
      this.mIsActive = paramBoolean;
      if (update())
      {
        setMetadata(this.mMetadata);
        setPlaybackState(this.mState);
      }
    }
    
    public void setCallback(MediaSessionCompat.Callback paramCallback, Handler arg2)
    {
      this.mCallback = paramCallback;
      if (paramCallback == null)
      {
        if (Build.VERSION.SDK_INT >= 18) {
          MediaSessionCompatApi18.setOnPlaybackPositionUpdateListener(this.mRccObj, null);
        }
        if (Build.VERSION.SDK_INT >= 19) {
          MediaSessionCompatApi19.setOnMetadataUpdateListener(this.mRccObj, null);
        }
      }
      for (;;)
      {
        return;
        paramCallback = ???;
        if (??? == null) {
          paramCallback = new Handler();
        }
        synchronized (this.mLock)
        {
          this.mHandler = new MessageHandler(paramCallback.getLooper());
          paramCallback = new MediaSessionCompatApi19.Callback()
          {
            public void onSeekTo(long paramAnonymousLong)
            {
              MediaSessionCompat.MediaSessionImplBase.this.postToHandler(18, Long.valueOf(paramAnonymousLong));
            }
            
            public void onSetRating(Object paramAnonymousObject)
            {
              MediaSessionCompat.MediaSessionImplBase.this.postToHandler(19, RatingCompat.fromRating(paramAnonymousObject));
            }
          };
          if (Build.VERSION.SDK_INT >= 18)
          {
            ??? = MediaSessionCompatApi18.createPlaybackPositionUpdateListener(paramCallback);
            MediaSessionCompatApi18.setOnPlaybackPositionUpdateListener(this.mRccObj, ???);
          }
          if (Build.VERSION.SDK_INT < 19) {
            continue;
          }
          paramCallback = MediaSessionCompatApi19.createMetadataUpdateListener(paramCallback);
          MediaSessionCompatApi19.setOnMetadataUpdateListener(this.mRccObj, paramCallback);
          return;
        }
      }
    }
    
    public void setExtras(Bundle paramBundle)
    {
      this.mExtras = paramBundle;
      sendExtras(paramBundle);
    }
    
    public void setFlags(int paramInt)
    {
      synchronized (this.mLock)
      {
        this.mFlags = paramInt;
        update();
        return;
      }
    }
    
    public void setMediaButtonReceiver(PendingIntent paramPendingIntent) {}
    
    public void setMetadata(MediaMetadataCompat arg1)
    {
      Object localObject3 = null;
      Object localObject2 = null;
      MediaMetadataCompat localMediaMetadataCompat = ???;
      if (??? != null) {
        localMediaMetadataCompat = new MediaMetadataCompat.Builder(???, MediaSessionCompat.sMaxBitmapSize).build();
      }
      synchronized (this.mLock)
      {
        this.mMetadata = localMediaMetadataCompat;
        sendMetadata(localMediaMetadataCompat);
        if (!this.mIsActive) {
          return;
        }
      }
      if (Build.VERSION.SDK_INT >= 19)
      {
        localObject3 = this.mRccObj;
        if (localObject1 == null)
        {
          ??? = (MediaMetadataCompat)localObject2;
          if (this.mState != null) {
            break label113;
          }
          l = 0L;
          MediaSessionCompatApi19.setMetadata(localObject3, ???, l);
        }
      }
      label113:
      while (Build.VERSION.SDK_INT < 14) {
        for (;;)
        {
          return;
          ??? = ((MediaMetadataCompat)localObject1).getBundle();
          continue;
          long l = this.mState.getActions();
        }
      }
      localObject2 = this.mRccObj;
      if (localObject1 == null) {}
      for (??? = (MediaMetadataCompat)localObject3;; ??? = ((MediaMetadataCompat)localObject1).getBundle())
      {
        MediaSessionCompatApi14.setMetadata(localObject2, ???);
        return;
      }
    }
    
    public void setPlaybackState(PlaybackStateCompat paramPlaybackStateCompat)
    {
      synchronized (this.mLock)
      {
        this.mState = paramPlaybackStateCompat;
        sendState(paramPlaybackStateCompat);
        if (!this.mIsActive) {
          return;
        }
      }
      if (paramPlaybackStateCompat == null) {
        if (Build.VERSION.SDK_INT >= 14)
        {
          MediaSessionCompatApi14.setState(this.mRccObj, 0);
          MediaSessionCompatApi14.setTransportControlFlags(this.mRccObj, 0L);
        }
      }
      do
      {
        return;
        if (Build.VERSION.SDK_INT >= 18) {
          MediaSessionCompatApi18.setState(this.mRccObj, paramPlaybackStateCompat.getState(), paramPlaybackStateCompat.getPosition(), paramPlaybackStateCompat.getPlaybackSpeed(), paramPlaybackStateCompat.getLastPositionUpdateTime());
        }
        while (Build.VERSION.SDK_INT >= 19)
        {
          MediaSessionCompatApi19.setTransportControlFlags(this.mRccObj, paramPlaybackStateCompat.getActions());
          return;
          if (Build.VERSION.SDK_INT >= 14) {
            MediaSessionCompatApi14.setState(this.mRccObj, paramPlaybackStateCompat.getState());
          }
        }
        if (Build.VERSION.SDK_INT >= 18)
        {
          MediaSessionCompatApi18.setTransportControlFlags(this.mRccObj, paramPlaybackStateCompat.getActions());
          return;
        }
      } while (Build.VERSION.SDK_INT < 14);
      MediaSessionCompatApi14.setTransportControlFlags(this.mRccObj, paramPlaybackStateCompat.getActions());
    }
    
    public void setPlaybackToLocal(int paramInt)
    {
      if (this.mVolumeProvider != null) {
        this.mVolumeProvider.setCallback(null);
      }
      this.mVolumeType = 1;
      sendVolumeInfoChanged(new ParcelableVolumeInfo(this.mVolumeType, this.mLocalStream, 2, this.mAudioManager.getStreamMaxVolume(this.mLocalStream), this.mAudioManager.getStreamVolume(this.mLocalStream)));
    }
    
    public void setPlaybackToRemote(VolumeProviderCompat paramVolumeProviderCompat)
    {
      if (paramVolumeProviderCompat == null) {
        throw new IllegalArgumentException("volumeProvider may not be null");
      }
      if (this.mVolumeProvider != null) {
        this.mVolumeProvider.setCallback(null);
      }
      this.mVolumeType = 2;
      this.mVolumeProvider = paramVolumeProviderCompat;
      sendVolumeInfoChanged(new ParcelableVolumeInfo(this.mVolumeType, this.mLocalStream, this.mVolumeProvider.getVolumeControl(), this.mVolumeProvider.getMaxVolume(), this.mVolumeProvider.getCurrentVolume()));
      paramVolumeProviderCompat.setCallback(this.mVolumeCallback);
    }
    
    public void setQueue(List<MediaSessionCompat.QueueItem> paramList)
    {
      this.mQueue = paramList;
      sendQueue(paramList);
    }
    
    public void setQueueTitle(CharSequence paramCharSequence)
    {
      this.mQueueTitle = paramCharSequence;
      sendQueueTitle(paramCharSequence);
    }
    
    public void setRatingType(int paramInt)
    {
      this.mRatingType = paramInt;
    }
    
    public void setSessionActivity(PendingIntent paramPendingIntent)
    {
      synchronized (this.mLock)
      {
        this.mSessionActivity = paramPendingIntent;
        return;
      }
    }
    
    void setVolumeTo(int paramInt1, int paramInt2)
    {
      if (this.mVolumeType == 2)
      {
        if (this.mVolumeProvider != null) {
          this.mVolumeProvider.onSetVolumeTo(paramInt1);
        }
        return;
      }
      this.mAudioManager.setStreamVolume(this.mLocalStream, paramInt1, paramInt2);
    }
    
    private static final class Command
    {
      public final String command;
      public final Bundle extras;
      public final ResultReceiver stub;
      
      public Command(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver)
      {
        this.command = paramString;
        this.extras = paramBundle;
        this.stub = paramResultReceiver;
      }
    }
    
    class MediaSessionStub
      extends IMediaSession.Stub
    {
      MediaSessionStub() {}
      
      public void adjustVolume(int paramInt1, int paramInt2, String paramString)
      {
        MediaSessionCompat.MediaSessionImplBase.this.adjustVolume(paramInt1, paramInt2);
      }
      
      public void fastForward()
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(16);
      }
      
      public Bundle getExtras()
      {
        synchronized (MediaSessionCompat.MediaSessionImplBase.this.mLock)
        {
          Bundle localBundle = MediaSessionCompat.MediaSessionImplBase.this.mExtras;
          return localBundle;
        }
      }
      
      public long getFlags()
      {
        synchronized (MediaSessionCompat.MediaSessionImplBase.this.mLock)
        {
          int i = MediaSessionCompat.MediaSessionImplBase.this.mFlags;
          long l = i;
          return l;
        }
      }
      
      public PendingIntent getLaunchPendingIntent()
      {
        synchronized (MediaSessionCompat.MediaSessionImplBase.this.mLock)
        {
          PendingIntent localPendingIntent = MediaSessionCompat.MediaSessionImplBase.this.mSessionActivity;
          return localPendingIntent;
        }
      }
      
      public MediaMetadataCompat getMetadata()
      {
        return MediaSessionCompat.MediaSessionImplBase.this.mMetadata;
      }
      
      public String getPackageName()
      {
        return MediaSessionCompat.MediaSessionImplBase.this.mPackageName;
      }
      
      public PlaybackStateCompat getPlaybackState()
      {
        return MediaSessionCompat.MediaSessionImplBase.this.getStateWithUpdatedPosition();
      }
      
      public List<MediaSessionCompat.QueueItem> getQueue()
      {
        synchronized (MediaSessionCompat.MediaSessionImplBase.this.mLock)
        {
          List localList = MediaSessionCompat.MediaSessionImplBase.this.mQueue;
          return localList;
        }
      }
      
      public CharSequence getQueueTitle()
      {
        return MediaSessionCompat.MediaSessionImplBase.this.mQueueTitle;
      }
      
      public int getRatingType()
      {
        return MediaSessionCompat.MediaSessionImplBase.this.mRatingType;
      }
      
      public String getTag()
      {
        return MediaSessionCompat.MediaSessionImplBase.this.mTag;
      }
      
      public ParcelableVolumeInfo getVolumeAttributes()
      {
        synchronized (MediaSessionCompat.MediaSessionImplBase.this.mLock)
        {
          int m = MediaSessionCompat.MediaSessionImplBase.this.mVolumeType;
          int n = MediaSessionCompat.MediaSessionImplBase.this.mLocalStream;
          VolumeProviderCompat localVolumeProviderCompat = MediaSessionCompat.MediaSessionImplBase.this.mVolumeProvider;
          if (m == 2)
          {
            i = localVolumeProviderCompat.getVolumeControl();
            j = localVolumeProviderCompat.getMaxVolume();
            k = localVolumeProviderCompat.getCurrentVolume();
            return new ParcelableVolumeInfo(m, n, i, j, k);
          }
          int i = 2;
          int j = MediaSessionCompat.MediaSessionImplBase.this.mAudioManager.getStreamMaxVolume(n);
          int k = MediaSessionCompat.MediaSessionImplBase.this.mAudioManager.getStreamVolume(n);
        }
      }
      
      public boolean isTransportControlEnabled()
      {
        boolean bool = false;
        if ((MediaSessionCompat.MediaSessionImplBase.this.mFlags & 0x2) != 0) {
          bool = true;
        }
        return bool;
      }
      
      public void next()
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(14);
      }
      
      public void pause()
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(12);
      }
      
      public void play()
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(7);
      }
      
      public void playFromMediaId(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(8, paramString, paramBundle);
      }
      
      public void playFromSearch(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(9, paramString, paramBundle);
      }
      
      public void playFromUri(Uri paramUri, Bundle paramBundle)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(10, paramUri, paramBundle);
      }
      
      public void prepare()
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(3);
      }
      
      public void prepareFromMediaId(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(4, paramString, paramBundle);
      }
      
      public void prepareFromSearch(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(5, paramString, paramBundle);
      }
      
      public void prepareFromUri(Uri paramUri, Bundle paramBundle)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(6, paramUri, paramBundle);
      }
      
      public void previous()
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(15);
      }
      
      public void rate(RatingCompat paramRatingCompat)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(19, paramRatingCompat);
      }
      
      public void registerCallbackListener(IMediaControllerCallback paramIMediaControllerCallback)
      {
        if (MediaSessionCompat.MediaSessionImplBase.this.mDestroyed) {
          try
          {
            paramIMediaControllerCallback.onSessionDestroyed();
            return;
          }
          catch (Exception paramIMediaControllerCallback)
          {
            return;
          }
        }
        MediaSessionCompat.MediaSessionImplBase.this.mControllerCallbacks.register(paramIMediaControllerCallback);
      }
      
      public void rewind()
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(17);
      }
      
      public void seekTo(long paramLong)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(18, Long.valueOf(paramLong));
      }
      
      public void sendCommand(String paramString, Bundle paramBundle, MediaSessionCompat.ResultReceiverWrapper paramResultReceiverWrapper)
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(1, new MediaSessionCompat.MediaSessionImplBase.Command(paramString, paramBundle, MediaSessionCompat.ResultReceiverWrapper.-get0(paramResultReceiverWrapper)));
      }
      
      public void sendCustomAction(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(20, paramString, paramBundle);
      }
      
      public boolean sendMediaButton(KeyEvent paramKeyEvent)
      {
        boolean bool = false;
        if ((MediaSessionCompat.MediaSessionImplBase.this.mFlags & 0x1) != 0) {
          bool = true;
        }
        if (bool) {
          MediaSessionCompat.MediaSessionImplBase.this.postToHandler(21, paramKeyEvent);
        }
        return bool;
      }
      
      public void setVolumeTo(int paramInt1, int paramInt2, String paramString)
      {
        MediaSessionCompat.MediaSessionImplBase.this.setVolumeTo(paramInt1, paramInt2);
      }
      
      public void skipToQueueItem(long paramLong)
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(11, Long.valueOf(paramLong));
      }
      
      public void stop()
        throws RemoteException
      {
        MediaSessionCompat.MediaSessionImplBase.this.postToHandler(13);
      }
      
      public void unregisterCallbackListener(IMediaControllerCallback paramIMediaControllerCallback)
      {
        MediaSessionCompat.MediaSessionImplBase.this.mControllerCallbacks.unregister(paramIMediaControllerCallback);
      }
    }
    
    private class MessageHandler
      extends Handler
    {
      private static final int KEYCODE_MEDIA_PAUSE = 127;
      private static final int KEYCODE_MEDIA_PLAY = 126;
      private static final int MSG_ADJUST_VOLUME = 2;
      private static final int MSG_COMMAND = 1;
      private static final int MSG_CUSTOM_ACTION = 20;
      private static final int MSG_FAST_FORWARD = 16;
      private static final int MSG_MEDIA_BUTTON = 21;
      private static final int MSG_NEXT = 14;
      private static final int MSG_PAUSE = 12;
      private static final int MSG_PLAY = 7;
      private static final int MSG_PLAY_MEDIA_ID = 8;
      private static final int MSG_PLAY_SEARCH = 9;
      private static final int MSG_PLAY_URI = 10;
      private static final int MSG_PREPARE = 3;
      private static final int MSG_PREPARE_MEDIA_ID = 4;
      private static final int MSG_PREPARE_SEARCH = 5;
      private static final int MSG_PREPARE_URI = 6;
      private static final int MSG_PREVIOUS = 15;
      private static final int MSG_RATE = 19;
      private static final int MSG_REWIND = 17;
      private static final int MSG_SEEK_TO = 18;
      private static final int MSG_SET_VOLUME = 22;
      private static final int MSG_SKIP_TO_ITEM = 11;
      private static final int MSG_STOP = 13;
      
      public MessageHandler(Looper paramLooper)
      {
        super();
      }
      
      private void onMediaButtonEvent(KeyEvent paramKeyEvent, MediaSessionCompat.Callback paramCallback)
      {
        if ((paramKeyEvent == null) || (paramKeyEvent.getAction() != 0)) {
          return;
        }
        long l;
        if (MediaSessionCompat.MediaSessionImplBase.this.mState == null)
        {
          l = 0L;
          switch (paramKeyEvent.getKeyCode())
          {
          }
        }
        int i;
        int j;
        label278:
        label316:
        label322:
        label328:
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      do
                      {
                        return;
                        l = MediaSessionCompat.MediaSessionImplBase.this.mState.getActions();
                        break;
                      } while ((0x4 & l) == 0L);
                      paramCallback.onPlay();
                      return;
                    } while ((0x2 & l) == 0L);
                    paramCallback.onPause();
                    return;
                  } while ((0x20 & l) == 0L);
                  paramCallback.onSkipToNext();
                  return;
                } while ((0x10 & l) == 0L);
                paramCallback.onSkipToPrevious();
                return;
              } while ((1L & l) == 0L);
              paramCallback.onStop();
              return;
            } while ((0x40 & l) == 0L);
            paramCallback.onFastForward();
            return;
          } while ((0x8 & l) == 0L);
          paramCallback.onRewind();
          return;
          if (MediaSessionCompat.MediaSessionImplBase.this.mState != null) {
            if (MediaSessionCompat.MediaSessionImplBase.this.mState.getState() == 3)
            {
              i = 1;
              if ((0x204 & l) == 0L) {
                break label316;
              }
              j = 1;
              if ((0x202 & l) == 0L) {
                break label322;
              }
            }
          }
          for (int k = 1;; k = 0)
          {
            if ((i == 0) || (k == 0)) {
              break label328;
            }
            paramCallback.onPause();
            return;
            i = 0;
            break;
            i = 0;
            break;
            j = 0;
            break label278;
          }
        } while ((i != 0) || (j == 0));
        paramCallback.onPlay();
      }
      
      public void handleMessage(Message paramMessage)
      {
        MediaSessionCompat.Callback localCallback = MediaSessionCompat.MediaSessionImplBase.this.mCallback;
        if (localCallback == null) {
          return;
        }
        switch (paramMessage.what)
        {
        default: 
        case 1: 
        case 21: 
          Intent localIntent;
          do
          {
            return;
            paramMessage = (MediaSessionCompat.MediaSessionImplBase.Command)paramMessage.obj;
            localCallback.onCommand(paramMessage.command, paramMessage.extras, paramMessage.stub);
            return;
            paramMessage = (KeyEvent)paramMessage.obj;
            localIntent = new Intent("android.intent.action.MEDIA_BUTTON");
            localIntent.putExtra("android.intent.extra.KEY_EVENT", paramMessage);
          } while (localCallback.onMediaButtonEvent(localIntent));
          onMediaButtonEvent(paramMessage, localCallback);
          return;
        case 3: 
          localCallback.onPrepare();
          return;
        case 4: 
          localCallback.onPrepareFromMediaId((String)paramMessage.obj, paramMessage.getData());
          return;
        case 5: 
          localCallback.onPrepareFromSearch((String)paramMessage.obj, paramMessage.getData());
          return;
        case 6: 
          localCallback.onPrepareFromUri((Uri)paramMessage.obj, paramMessage.getData());
          return;
        case 7: 
          localCallback.onPlay();
          return;
        case 8: 
          localCallback.onPlayFromMediaId((String)paramMessage.obj, paramMessage.getData());
          return;
        case 9: 
          localCallback.onPlayFromSearch((String)paramMessage.obj, paramMessage.getData());
          return;
        case 10: 
          localCallback.onPlayFromUri((Uri)paramMessage.obj, paramMessage.getData());
          return;
        case 11: 
          localCallback.onSkipToQueueItem(((Long)paramMessage.obj).longValue());
          return;
        case 12: 
          localCallback.onPause();
          return;
        case 13: 
          localCallback.onStop();
          return;
        case 14: 
          localCallback.onSkipToNext();
          return;
        case 15: 
          localCallback.onSkipToPrevious();
          return;
        case 16: 
          localCallback.onFastForward();
          return;
        case 17: 
          localCallback.onRewind();
          return;
        case 18: 
          localCallback.onSeekTo(((Long)paramMessage.obj).longValue());
          return;
        case 19: 
          localCallback.onSetRating((RatingCompat)paramMessage.obj);
          return;
        case 20: 
          localCallback.onCustomAction((String)paramMessage.obj, paramMessage.getData());
          return;
        case 2: 
          MediaSessionCompat.MediaSessionImplBase.this.adjustVolume(((Integer)paramMessage.obj).intValue(), 0);
          return;
        }
        MediaSessionCompat.MediaSessionImplBase.this.setVolumeTo(((Integer)paramMessage.obj).intValue(), 0);
      }
      
      public void post(int paramInt)
      {
        post(paramInt, null);
      }
      
      public void post(int paramInt, Object paramObject)
      {
        obtainMessage(paramInt, paramObject).sendToTarget();
      }
      
      public void post(int paramInt1, Object paramObject, int paramInt2)
      {
        obtainMessage(paramInt1, paramInt2, 0, paramObject).sendToTarget();
      }
      
      public void post(int paramInt, Object paramObject, Bundle paramBundle)
      {
        paramObject = obtainMessage(paramInt, paramObject);
        ((Message)paramObject).setData(paramBundle);
        ((Message)paramObject).sendToTarget();
      }
    }
  }
  
  public static abstract interface OnActiveChangeListener
  {
    public abstract void onActiveChanged();
  }
  
  public static final class QueueItem
    implements Parcelable
  {
    public static final Parcelable.Creator<QueueItem> CREATOR = new Parcelable.Creator()
    {
      public MediaSessionCompat.QueueItem createFromParcel(Parcel paramAnonymousParcel)
      {
        return new MediaSessionCompat.QueueItem(paramAnonymousParcel);
      }
      
      public MediaSessionCompat.QueueItem[] newArray(int paramAnonymousInt)
      {
        return new MediaSessionCompat.QueueItem[paramAnonymousInt];
      }
    };
    public static final int UNKNOWN_ID = -1;
    private final MediaDescriptionCompat mDescription;
    private final long mId;
    private Object mItem;
    
    QueueItem(Parcel paramParcel)
    {
      this.mDescription = ((MediaDescriptionCompat)MediaDescriptionCompat.CREATOR.createFromParcel(paramParcel));
      this.mId = paramParcel.readLong();
    }
    
    public QueueItem(MediaDescriptionCompat paramMediaDescriptionCompat, long paramLong)
    {
      this(null, paramMediaDescriptionCompat, paramLong);
    }
    
    private QueueItem(Object paramObject, MediaDescriptionCompat paramMediaDescriptionCompat, long paramLong)
    {
      if (paramMediaDescriptionCompat == null) {
        throw new IllegalArgumentException("Description cannot be null.");
      }
      if (paramLong == -1L) {
        throw new IllegalArgumentException("Id cannot be QueueItem.UNKNOWN_ID");
      }
      this.mDescription = paramMediaDescriptionCompat;
      this.mId = paramLong;
      this.mItem = paramObject;
    }
    
    public static QueueItem fromQueueItem(Object paramObject)
    {
      if ((paramObject == null) || (Build.VERSION.SDK_INT < 21)) {
        return null;
      }
      return new QueueItem(paramObject, MediaDescriptionCompat.fromMediaDescription(MediaSessionCompatApi21.QueueItem.getDescription(paramObject)), MediaSessionCompatApi21.QueueItem.getQueueId(paramObject));
    }
    
    public static List<QueueItem> fromQueueItemList(List<?> paramList)
    {
      if ((paramList == null) || (Build.VERSION.SDK_INT < 21)) {
        return null;
      }
      ArrayList localArrayList = new ArrayList();
      paramList = paramList.iterator();
      while (paramList.hasNext()) {
        localArrayList.add(fromQueueItem(paramList.next()));
      }
      return localArrayList;
    }
    
    @Deprecated
    public static QueueItem obtain(Object paramObject)
    {
      return fromQueueItem(paramObject);
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public MediaDescriptionCompat getDescription()
    {
      return this.mDescription;
    }
    
    public long getQueueId()
    {
      return this.mId;
    }
    
    public Object getQueueItem()
    {
      if ((this.mItem != null) || (Build.VERSION.SDK_INT < 21)) {
        return this.mItem;
      }
      this.mItem = MediaSessionCompatApi21.QueueItem.createItem(this.mDescription.getMediaDescription(), this.mId);
      return this.mItem;
    }
    
    public String toString()
    {
      return "MediaSession.QueueItem {Description=" + this.mDescription + ", Id=" + this.mId + " }";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      this.mDescription.writeToParcel(paramParcel, paramInt);
      paramParcel.writeLong(this.mId);
    }
  }
  
  static final class ResultReceiverWrapper
    implements Parcelable
  {
    public static final Parcelable.Creator<ResultReceiverWrapper> CREATOR = new Parcelable.Creator()
    {
      public MediaSessionCompat.ResultReceiverWrapper createFromParcel(Parcel paramAnonymousParcel)
      {
        return new MediaSessionCompat.ResultReceiverWrapper(paramAnonymousParcel);
      }
      
      public MediaSessionCompat.ResultReceiverWrapper[] newArray(int paramAnonymousInt)
      {
        return new MediaSessionCompat.ResultReceiverWrapper[paramAnonymousInt];
      }
    };
    private ResultReceiver mResultReceiver;
    
    ResultReceiverWrapper(Parcel paramParcel)
    {
      this.mResultReceiver = ((ResultReceiver)ResultReceiver.CREATOR.createFromParcel(paramParcel));
    }
    
    public ResultReceiverWrapper(ResultReceiver paramResultReceiver)
    {
      this.mResultReceiver = paramResultReceiver;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      this.mResultReceiver.writeToParcel(paramParcel, paramInt);
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public static @interface SessionFlags {}
  
  public static final class Token
    implements Parcelable
  {
    public static final Parcelable.Creator<Token> CREATOR = new Parcelable.Creator()
    {
      public MediaSessionCompat.Token createFromParcel(Parcel paramAnonymousParcel)
      {
        if (Build.VERSION.SDK_INT >= 21) {}
        for (paramAnonymousParcel = paramAnonymousParcel.readParcelable(null);; paramAnonymousParcel = paramAnonymousParcel.readStrongBinder()) {
          return new MediaSessionCompat.Token(paramAnonymousParcel);
        }
      }
      
      public MediaSessionCompat.Token[] newArray(int paramAnonymousInt)
      {
        return new MediaSessionCompat.Token[paramAnonymousInt];
      }
    };
    private final Object mInner;
    
    Token(Object paramObject)
    {
      this.mInner = paramObject;
    }
    
    public static Token fromToken(Object paramObject)
    {
      if ((paramObject == null) || (Build.VERSION.SDK_INT < 21)) {
        return null;
      }
      return new Token(MediaSessionCompatApi21.verifyToken(paramObject));
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof Token)) {
        return false;
      }
      paramObject = (Token)paramObject;
      if (this.mInner == null) {
        return ((Token)paramObject).mInner == null;
      }
      if (((Token)paramObject).mInner == null) {
        return false;
      }
      return this.mInner.equals(((Token)paramObject).mInner);
    }
    
    public Object getToken()
    {
      return this.mInner;
    }
    
    public int hashCode()
    {
      if (this.mInner == null) {
        return 0;
      }
      return this.mInner.hashCode();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      if (Build.VERSION.SDK_INT >= 21)
      {
        paramParcel.writeParcelable((Parcelable)this.mInner, paramInt);
        return;
      }
      paramParcel.writeStrongBinder((IBinder)this.mInner);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\media\session\MediaSessionCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */