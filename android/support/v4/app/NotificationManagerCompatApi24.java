package android.support.v4.app;

import android.app.NotificationManager;

class NotificationManagerCompatApi24
{
  public static boolean areNotificationsEnabled(NotificationManager paramNotificationManager)
  {
    return paramNotificationManager.areNotificationsEnabled();
  }
  
  public static int getImportance(NotificationManager paramNotificationManager)
  {
    return paramNotificationManager.getImportance();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\app\NotificationManagerCompatApi24.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */