package com.oneplus.lib.util.loading;

import android.os.AsyncTask;

public abstract class LoadingAsyncTask<Param, Progress, Result>
  extends AsyncTask<Param, Progress, Result>
{
  private LoadingHelper mProgressHelper = new LoadingHelper()
  {
    protected void hideProgree(Object paramAnonymousObject)
    {
      LoadingAsyncTask.this.hideProgree(paramAnonymousObject);
    }
    
    protected Object showProgree()
    {
      return LoadingAsyncTask.this.showProgree();
    }
  };
  
  private void onFinish(final Result paramResult)
  {
    this.mProgressHelper.finishShowProgress(new LoadingHelper.FinishShowCallback()
    {
      public void finish(boolean paramAnonymousBoolean)
      {
        if (!LoadingAsyncTask.this.isCancelled()) {
          LoadingAsyncTask.this.onPostExecuteExtend(paramResult);
        }
      }
    });
  }
  
  protected abstract void hideProgree(Object paramObject);
  
  protected final void onCancelled()
  {
    super.onCancelled();
  }
  
  protected final void onCancelled(Result paramResult)
  {
    onFinish(paramResult);
    onCancelledExtend(paramResult);
  }
  
  protected void onCancelledExtend(Result paramResult) {}
  
  protected final void onPostExecute(Result paramResult)
  {
    onFinish(paramResult);
  }
  
  protected void onPostExecuteExtend(Result paramResult) {}
  
  protected final void onPreExecute()
  {
    this.mProgressHelper.beginShowProgress();
    onPreExecuteExtend();
  }
  
  protected void onPreExecuteExtend() {}
  
  public LoadingAsyncTask<Param, Progress, Result> setProgreeMinShowTime(long paramLong)
  {
    this.mProgressHelper.setProgreeMinShowTime(paramLong);
    return this;
  }
  
  public LoadingAsyncTask<Param, Progress, Result> setWillShowProgreeTime(long paramLong)
  {
    this.mProgressHelper.setWillShowProgreeTime(paramLong);
    return this;
  }
  
  protected abstract Object showProgree();
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\util\loading\LoadingAsyncTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */