package com.oneplus.lib.util.loading;

import android.view.View;

public abstract class ViewLoadingAsyncTask<Param, Progress, Result>
  extends LoadingAsyncTask<Param, Progress, Result>
{
  private View mProgressView;
  
  public ViewLoadingAsyncTask(View paramView)
  {
    this.mProgressView = paramView;
  }
  
  protected void hideProgree(Object paramObject)
  {
    if (this.mProgressView != null) {
      this.mProgressView.setVisibility(8);
    }
  }
  
  protected Object showProgree()
  {
    if (this.mProgressView != null) {
      this.mProgressView.setVisibility(0);
    }
    return this.mProgressView;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\util\loading\ViewLoadingAsyncTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */