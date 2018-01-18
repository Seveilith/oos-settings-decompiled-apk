package com.android.settings.dashboard.conditional;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class FocusRecyclerView
  extends RecyclerView
{
  private FocusListener mListener;
  
  public FocusRecyclerView(Context paramContext, @Nullable AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public void onWindowFocusChanged(boolean paramBoolean)
  {
    super.onWindowFocusChanged(paramBoolean);
    if (this.mListener != null) {
      this.mListener.onWindowFocusChanged(paramBoolean);
    }
  }
  
  public void setListener(FocusListener paramFocusListener)
  {
    this.mListener = paramFocusListener;
  }
  
  public static abstract interface FocusListener
  {
    public abstract void onWindowFocusChanged(boolean paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\dashboard\conditional\FocusRecyclerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */