package android.support.v13.app;

import android.app.Activity;
import android.support.v13.view.DragAndDropPermissionsCompat;
import android.view.DragEvent;

public class ActivityCompat
  extends android.support.v4.app.ActivityCompat
{
  public static DragAndDropPermissionsCompat requestDragAndDropPermissions(Activity paramActivity, DragEvent paramDragEvent)
  {
    return DragAndDropPermissionsCompat.request(paramActivity, paramDragEvent);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v13\app\ActivityCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */