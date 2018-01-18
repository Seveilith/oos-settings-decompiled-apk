package com.oneplus.lib.widget.recyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

public class OPRecyclerView
  extends RecyclerView
{
  private final Rect mContentPadding = new Rect();
  
  public OPRecyclerView(Context paramContext)
  {
    super(paramContext);
    initialize(paramContext, null, 0);
  }
  
  public OPRecyclerView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initialize(paramContext, paramAttributeSet, 0);
  }
  
  public OPRecyclerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initialize(paramContext, paramAttributeSet, paramInt);
  }
  
  private void initialize(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    setClipToPadding(false);
  }
  
  public void addOPItemDecoration(OPItemDecoration paramOPItemDecoration)
  {
    addItemDecoration(paramOPItemDecoration);
    setPadding(0, paramOPItemDecoration.getSpace(), paramOPItemDecoration.getSpace(), 0);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\recyclerview\OPRecyclerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */