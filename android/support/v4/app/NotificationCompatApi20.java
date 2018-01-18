package android.support.v4.app;

import android.app.Notification;
import android.app.Notification.Action;
import android.app.Notification.Action.Builder;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.RemoteViews;
import java.util.ArrayList;

class NotificationCompatApi20
{
  public static void addAction(Notification.Builder paramBuilder, NotificationCompatBase.Action paramAction)
  {
    Notification.Action.Builder localBuilder = new Notification.Action.Builder(paramAction.getIcon(), paramAction.getTitle(), paramAction.getActionIntent());
    if (paramAction.getRemoteInputs() != null)
    {
      localObject = RemoteInputCompatApi20.fromCompat(paramAction.getRemoteInputs());
      int i = 0;
      int j = localObject.length;
      while (i < j)
      {
        localBuilder.addRemoteInput(localObject[i]);
        i += 1;
      }
    }
    if (paramAction.getExtras() != null) {}
    for (Object localObject = new Bundle(paramAction.getExtras());; localObject = new Bundle())
    {
      ((Bundle)localObject).putBoolean("android.support.allowGeneratedReplies", paramAction.getAllowGeneratedReplies());
      localBuilder.addExtras((Bundle)localObject);
      paramBuilder.addAction(localBuilder.build());
      return;
    }
  }
  
  public static NotificationCompatBase.Action getAction(Notification paramNotification, int paramInt, NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    return getActionCompatFromAction(paramNotification.actions[paramInt], paramFactory, paramFactory1);
  }
  
  private static NotificationCompatBase.Action getActionCompatFromAction(Notification.Action paramAction, NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    paramFactory1 = RemoteInputCompatApi20.toCompat(paramAction.getRemoteInputs(), paramFactory1);
    boolean bool = paramAction.getExtras().getBoolean("android.support.allowGeneratedReplies");
    return paramFactory.build(paramAction.icon, paramAction.title, paramAction.actionIntent, paramAction.getExtras(), paramFactory1, bool);
  }
  
  private static Notification.Action getActionFromActionCompat(NotificationCompatBase.Action paramAction)
  {
    Notification.Action.Builder localBuilder = new Notification.Action.Builder(paramAction.getIcon(), paramAction.getTitle(), paramAction.getActionIntent()).addExtras(paramAction.getExtras());
    paramAction = paramAction.getRemoteInputs();
    if (paramAction != null)
    {
      paramAction = RemoteInputCompatApi20.fromCompat(paramAction);
      int i = 0;
      int j = paramAction.length;
      while (i < j)
      {
        localBuilder.addRemoteInput(paramAction[i]);
        i += 1;
      }
    }
    return localBuilder.build();
  }
  
  public static NotificationCompatBase.Action[] getActionsFromParcelableArrayList(ArrayList<Parcelable> paramArrayList, NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    if (paramArrayList == null) {
      return null;
    }
    NotificationCompatBase.Action[] arrayOfAction = paramFactory.newArray(paramArrayList.size());
    int i = 0;
    while (i < arrayOfAction.length)
    {
      arrayOfAction[i] = getActionCompatFromAction((Notification.Action)paramArrayList.get(i), paramFactory, paramFactory1);
      i += 1;
    }
    return arrayOfAction;
  }
  
  public static String getGroup(Notification paramNotification)
  {
    return paramNotification.getGroup();
  }
  
  public static boolean getLocalOnly(Notification paramNotification)
  {
    boolean bool = false;
    if ((paramNotification.flags & 0x100) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public static ArrayList<Parcelable> getParcelableArrayListForActions(NotificationCompatBase.Action[] paramArrayOfAction)
  {
    if (paramArrayOfAction == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList(paramArrayOfAction.length);
    int i = 0;
    int j = paramArrayOfAction.length;
    while (i < j)
    {
      localArrayList.add(getActionFromActionCompat(paramArrayOfAction[i]));
      i += 1;
    }
    return localArrayList;
  }
  
  public static String getSortKey(Notification paramNotification)
  {
    return paramNotification.getSortKey();
  }
  
  public static boolean isGroupSummary(Notification paramNotification)
  {
    boolean bool = false;
    if ((paramNotification.flags & 0x200) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public static class Builder
    implements NotificationBuilderWithBuilderAccessor, NotificationBuilderWithActions
  {
    private Notification.Builder b;
    private RemoteViews mBigContentView;
    private RemoteViews mContentView;
    private Bundle mExtras;
    
    public Builder(Context paramContext, Notification paramNotification, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, RemoteViews paramRemoteViews1, int paramInt1, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2, Bitmap paramBitmap, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt4, CharSequence paramCharSequence4, boolean paramBoolean4, ArrayList<String> paramArrayList, Bundle paramBundle, String paramString1, boolean paramBoolean5, String paramString2, RemoteViews paramRemoteViews2, RemoteViews paramRemoteViews3)
    {
      paramContext = new Notification.Builder(paramContext).setWhen(paramNotification.when).setShowWhen(paramBoolean2).setSmallIcon(paramNotification.icon, paramNotification.iconLevel).setContent(paramNotification.contentView).setTicker(paramNotification.tickerText, paramRemoteViews1).setSound(paramNotification.sound, paramNotification.audioStreamType).setVibrate(paramNotification.vibrate).setLights(paramNotification.ledARGB, paramNotification.ledOnMS, paramNotification.ledOffMS);
      if ((paramNotification.flags & 0x2) != 0)
      {
        paramBoolean2 = true;
        paramContext = paramContext.setOngoing(paramBoolean2);
        if ((paramNotification.flags & 0x8) == 0) {
          break label314;
        }
        paramBoolean2 = true;
        label117:
        paramContext = paramContext.setOnlyAlertOnce(paramBoolean2);
        if ((paramNotification.flags & 0x10) == 0) {
          break label320;
        }
        paramBoolean2 = true;
        label137:
        paramContext = paramContext.setAutoCancel(paramBoolean2).setDefaults(paramNotification.defaults).setContentTitle(paramCharSequence1).setContentText(paramCharSequence2).setSubText(paramCharSequence4).setContentInfo(paramCharSequence3).setContentIntent(paramPendingIntent1).setDeleteIntent(paramNotification.deleteIntent);
        if ((paramNotification.flags & 0x80) == 0) {
          break label326;
        }
        paramBoolean2 = true;
        label196:
        this.b = paramContext.setFullScreenIntent(paramPendingIntent2, paramBoolean2).setLargeIcon(paramBitmap).setNumber(paramInt1).setUsesChronometer(paramBoolean3).setPriority(paramInt4).setProgress(paramInt2, paramInt3, paramBoolean1).setLocalOnly(paramBoolean4).setGroup(paramString1).setGroupSummary(paramBoolean5).setSortKey(paramString2);
        this.mExtras = new Bundle();
        if (paramBundle != null) {
          this.mExtras.putAll(paramBundle);
        }
        if ((paramArrayList != null) && (!paramArrayList.isEmpty())) {
          break label332;
        }
      }
      for (;;)
      {
        this.mContentView = paramRemoteViews2;
        this.mBigContentView = paramRemoteViews3;
        return;
        paramBoolean2 = false;
        break;
        label314:
        paramBoolean2 = false;
        break label117;
        label320:
        paramBoolean2 = false;
        break label137;
        label326:
        paramBoolean2 = false;
        break label196;
        label332:
        this.mExtras.putStringArray("android.people", (String[])paramArrayList.toArray(new String[paramArrayList.size()]));
      }
    }
    
    public void addAction(NotificationCompatBase.Action paramAction)
    {
      NotificationCompatApi20.addAction(this.b, paramAction);
    }
    
    public Notification build()
    {
      this.b.setExtras(this.mExtras);
      Notification localNotification = this.b.build();
      if (this.mContentView != null) {
        localNotification.contentView = this.mContentView;
      }
      if (this.mBigContentView != null) {
        localNotification.bigContentView = this.mBigContentView;
      }
      return localNotification;
    }
    
    public Notification.Builder getBuilder()
    {
      return this.b;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\app\NotificationCompatApi20.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */