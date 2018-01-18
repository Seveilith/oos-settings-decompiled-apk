package com.oneplus.lib.widget.recyclerview;

import android.view.MotionEvent;

public class OPItemAnimator
  extends DefaultItemAnimator
{
  private static final int ONEPLUS_DURATION_MOVE = 200;
  private static final int ONEPLUS_DURATION_REMOVE = 200;
  private RecyclerView mRecyclerView;
  private RecyclerView.OnItemTouchListener recyclerViewDisabler;
  
  public OPItemAnimator(RecyclerView paramRecyclerView)
  {
    this.mRecyclerView = paramRecyclerView;
    this.recyclerViewDisabler = new RecyclerViewDisabler(null);
    setRemoveDuration(200L);
    setMoveDuration(200L);
  }
  
  public void onMoveFinished(RecyclerView.ViewHolder paramViewHolder)
  {
    this.mRecyclerView.removeOnItemTouchListener(this.recyclerViewDisabler);
  }
  
  public void onRemoveStarting(RecyclerView.ViewHolder paramViewHolder)
  {
    this.mRecyclerView.addOnItemTouchListener(this.recyclerViewDisabler);
  }
  
  private class RecyclerViewDisabler
    implements RecyclerView.OnItemTouchListener
  {
    private RecyclerViewDisabler() {}
    
    public boolean onInterceptTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent)
    {
      return true;
    }
    
    public void onRequestDisallowInterceptTouchEvent(boolean paramBoolean) {}
    
    public void onTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent) {}
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\recyclerview\OPItemAnimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */