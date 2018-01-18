package com.android.settings.utils;

import android.content.AsyncTaskLoader;
import android.content.Context;

public abstract class AsyncLoader<T>
  extends AsyncTaskLoader<T>
{
  private T mResult;
  
  public AsyncLoader(Context paramContext)
  {
    super(paramContext);
  }
  
  public void deliverResult(T paramT)
  {
    if (isReset())
    {
      if (paramT != null) {
        onDiscardResult(paramT);
      }
      return;
    }
    Object localObject = this.mResult;
    this.mResult = paramT;
    if (isStarted()) {
      super.deliverResult(paramT);
    }
    if ((localObject != null) && (localObject != this.mResult)) {
      onDiscardResult(localObject);
    }
  }
  
  public void onCanceled(T paramT)
  {
    super.onCanceled(paramT);
    if (paramT != null) {
      onDiscardResult(paramT);
    }
  }
  
  protected abstract void onDiscardResult(T paramT);
  
  protected void onReset()
  {
    super.onReset();
    onStopLoading();
    if (this.mResult != null) {
      onDiscardResult(this.mResult);
    }
    this.mResult = null;
  }
  
  protected void onStartLoading()
  {
    if (this.mResult != null) {
      deliverResult(this.mResult);
    }
    if ((takeContentChanged()) || (this.mResult == null)) {
      forceLoad();
    }
  }
  
  protected void onStopLoading()
  {
    cancelLoad();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\utils\AsyncLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */