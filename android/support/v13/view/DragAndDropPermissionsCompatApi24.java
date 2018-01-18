package android.support.v13.view;

import android.app.Activity;
import android.view.DragAndDropPermissions;
import android.view.DragEvent;

class DragAndDropPermissionsCompatApi24
{
  public static void release(Object paramObject)
  {
    ((DragAndDropPermissions)paramObject).release();
  }
  
  public static Object request(Activity paramActivity, DragEvent paramDragEvent)
  {
    return paramActivity.requestDragAndDropPermissions(paramDragEvent);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v13\view\DragAndDropPermissionsCompatApi24.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */