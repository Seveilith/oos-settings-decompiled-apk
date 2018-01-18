package com.oneplus.lib.widget.recyclerview;

import android.view.View;

class LayoutState
{
  static final int INVALID_LAYOUT = Integer.MIN_VALUE;
  static final int ITEM_DIRECTION_HEAD = -1;
  static final int ITEM_DIRECTION_TAIL = 1;
  static final int LAYOUT_END = 1;
  static final int LAYOUT_START = -1;
  static final int SCOLLING_OFFSET_NaN = Integer.MIN_VALUE;
  static final String TAG = "LayoutState";
  int mAvailable;
  int mCurrentPosition;
  int mEndLine = 0;
  int mItemDirection;
  int mLayoutDirection;
  int mStartLine = 0;
  
  boolean hasMore(RecyclerView.State paramState)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mCurrentPosition >= 0)
    {
      bool1 = bool2;
      if (this.mCurrentPosition < paramState.getItemCount()) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  View next(RecyclerView.Recycler paramRecycler)
  {
    paramRecycler = paramRecycler.getViewForPosition(this.mCurrentPosition);
    this.mCurrentPosition += this.mItemDirection;
    return paramRecycler;
  }
  
  public String toString()
  {
    return "LayoutState{mAvailable=" + this.mAvailable + ", mCurrentPosition=" + this.mCurrentPosition + ", mItemDirection=" + this.mItemDirection + ", mLayoutDirection=" + this.mLayoutDirection + ", mStartLine=" + this.mStartLine + ", mEndLine=" + this.mEndLine + '}';
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\recyclerview\LayoutState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */