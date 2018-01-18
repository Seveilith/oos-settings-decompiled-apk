package com.android.settings.localepicker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

class LocaleRecyclerView
  extends RecyclerView
{
  public LocaleRecyclerView(Context paramContext)
  {
    super(paramContext);
  }
  
  public LocaleRecyclerView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public LocaleRecyclerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getAction() == 1)
    {
      LocaleDragAndDropAdapter localLocaleDragAndDropAdapter = (LocaleDragAndDropAdapter)getAdapter();
      if (localLocaleDragAndDropAdapter != null) {
        localLocaleDragAndDropAdapter.doTheUpdate();
      }
    }
    return super.onTouchEvent(paramMotionEvent);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\localepicker\LocaleRecyclerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */