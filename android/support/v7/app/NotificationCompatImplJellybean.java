package android.support.v7.app;

import android.app.Notification.BigTextStyle;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;

class NotificationCompatImplJellybean
{
  public static void addBigTextStyle(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor, CharSequence paramCharSequence)
  {
    new Notification.BigTextStyle(paramNotificationBuilderWithBuilderAccessor.getBuilder()).bigText(paramCharSequence);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\app\NotificationCompatImplJellybean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */