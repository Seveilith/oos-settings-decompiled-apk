package android.support.v13.view;

import android.app.Activity;
import android.support.annotation.RestrictTo;
import android.support.v4.os.BuildCompat;
import android.view.DragEvent;

public final class DragAndDropPermissionsCompat
{
  private static DragAndDropPermissionsCompatImpl IMPL = new BaseDragAndDropPermissionsCompatImpl();
  private Object mDragAndDropPermissions;
  
  static
  {
    if (BuildCompat.isAtLeastN())
    {
      IMPL = new Api24DragAndDropPermissionsCompatImpl();
      return;
    }
  }
  
  private DragAndDropPermissionsCompat(Object paramObject)
  {
    this.mDragAndDropPermissions = paramObject;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public static DragAndDropPermissionsCompat request(Activity paramActivity, DragEvent paramDragEvent)
  {
    paramActivity = IMPL.request(paramActivity, paramDragEvent);
    if (paramActivity != null) {
      return new DragAndDropPermissionsCompat(paramActivity);
    }
    return null;
  }
  
  public void release()
  {
    IMPL.release(this.mDragAndDropPermissions);
  }
  
  static class Api24DragAndDropPermissionsCompatImpl
    extends DragAndDropPermissionsCompat.BaseDragAndDropPermissionsCompatImpl
  {
    public void release(Object paramObject)
    {
      DragAndDropPermissionsCompatApi24.release(paramObject);
    }
    
    public Object request(Activity paramActivity, DragEvent paramDragEvent)
    {
      return DragAndDropPermissionsCompatApi24.request(paramActivity, paramDragEvent);
    }
  }
  
  static class BaseDragAndDropPermissionsCompatImpl
    implements DragAndDropPermissionsCompat.DragAndDropPermissionsCompatImpl
  {
    public void release(Object paramObject) {}
    
    public Object request(Activity paramActivity, DragEvent paramDragEvent)
    {
      return null;
    }
  }
  
  static abstract interface DragAndDropPermissionsCompatImpl
  {
    public abstract void release(Object paramObject);
    
    public abstract Object request(Activity paramActivity, DragEvent paramDragEvent);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v13\view\DragAndDropPermissionsCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */