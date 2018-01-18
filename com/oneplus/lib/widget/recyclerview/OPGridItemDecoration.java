package com.oneplus.lib.widget.recyclerview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import com.oneplus.commonctrl.R.dimen;

public class OPGridItemDecoration
  extends OPItemDecoration
{
  Context mContext;
  
  public OPGridItemDecoration(Context paramContext)
  {
    super(0);
    setSpace(paramContext.getResources().getDimensionPixelSize(R.dimen.opgridview_margin));
  }
  
  public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    paramRect.set(this.mSpace, 0, 0, this.mSpace);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\recyclerview\OPGridItemDecoration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */