package android.support.v13.view;

import android.content.ClipData;
import android.support.v4.os.BuildCompat;
import android.view.View;
import android.view.View.DragShadowBuilder;

public class ViewCompat
  extends android.support.v4.view.ViewCompat
{
  static ViewCompatImpl IMPL = new BaseViewCompatImpl();
  
  static
  {
    if (BuildCompat.isAtLeastN())
    {
      IMPL = new Api24ViewCompatImpl();
      return;
    }
  }
  
  public static void cancelDragAndDrop(View paramView)
  {
    IMPL.cancelDragAndDrop(paramView);
  }
  
  public static boolean startDragAndDrop(View paramView, ClipData paramClipData, View.DragShadowBuilder paramDragShadowBuilder, Object paramObject, int paramInt)
  {
    return IMPL.startDragAndDrop(paramView, paramClipData, paramDragShadowBuilder, paramObject, paramInt);
  }
  
  public static void updateDragShadow(View paramView, View.DragShadowBuilder paramDragShadowBuilder)
  {
    IMPL.updateDragShadow(paramView, paramDragShadowBuilder);
  }
  
  private static class Api24ViewCompatImpl
    implements ViewCompat.ViewCompatImpl
  {
    public void cancelDragAndDrop(View paramView)
    {
      ViewCompatApi24.cancelDragAndDrop(paramView);
    }
    
    public boolean startDragAndDrop(View paramView, ClipData paramClipData, View.DragShadowBuilder paramDragShadowBuilder, Object paramObject, int paramInt)
    {
      return ViewCompatApi24.startDragAndDrop(paramView, paramClipData, paramDragShadowBuilder, paramObject, paramInt);
    }
    
    public void updateDragShadow(View paramView, View.DragShadowBuilder paramDragShadowBuilder)
    {
      ViewCompatApi24.updateDragShadow(paramView, paramDragShadowBuilder);
    }
  }
  
  private static class BaseViewCompatImpl
    implements ViewCompat.ViewCompatImpl
  {
    public void cancelDragAndDrop(View paramView) {}
    
    public boolean startDragAndDrop(View paramView, ClipData paramClipData, View.DragShadowBuilder paramDragShadowBuilder, Object paramObject, int paramInt)
    {
      return paramView.startDrag(paramClipData, paramDragShadowBuilder, paramObject, paramInt);
    }
    
    public void updateDragShadow(View paramView, View.DragShadowBuilder paramDragShadowBuilder) {}
  }
  
  static abstract interface ViewCompatImpl
  {
    public abstract void cancelDragAndDrop(View paramView);
    
    public abstract boolean startDragAndDrop(View paramView, ClipData paramClipData, View.DragShadowBuilder paramDragShadowBuilder, Object paramObject, int paramInt);
    
    public abstract void updateDragShadow(View paramView, View.DragShadowBuilder paramDragShadowBuilder);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v13\view\ViewCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */