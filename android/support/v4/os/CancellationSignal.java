package android.support.v4.os;

import android.os.Build.VERSION;

public final class CancellationSignal
{
  private boolean mCancelInProgress;
  private Object mCancellationSignalObj;
  private boolean mIsCanceled;
  private OnCancelListener mOnCancelListener;
  
  private void waitForCancelFinishedLocked()
  {
    while (this.mCancelInProgress) {
      try
      {
        wait();
      }
      catch (InterruptedException localInterruptedException) {}
    }
  }
  
  /* Error */
  public void cancel()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 30	android/support/v4/os/CancellationSignal:mIsCanceled	Z
    //   6: istore_1
    //   7: iload_1
    //   8: ifeq +6 -> 14
    //   11: aload_0
    //   12: monitorexit
    //   13: return
    //   14: aload_0
    //   15: iconst_1
    //   16: putfield 30	android/support/v4/os/CancellationSignal:mIsCanceled	Z
    //   19: aload_0
    //   20: iconst_1
    //   21: putfield 24	android/support/v4/os/CancellationSignal:mCancelInProgress	Z
    //   24: aload_0
    //   25: getfield 32	android/support/v4/os/CancellationSignal:mOnCancelListener	Landroid/support/v4/os/CancellationSignal$OnCancelListener;
    //   28: astore_2
    //   29: aload_0
    //   30: getfield 34	android/support/v4/os/CancellationSignal:mCancellationSignalObj	Ljava/lang/Object;
    //   33: astore_3
    //   34: aload_0
    //   35: monitorexit
    //   36: aload_2
    //   37: ifnull +9 -> 46
    //   40: aload_2
    //   41: invokeinterface 37 1 0
    //   46: aload_3
    //   47: ifnull +7 -> 54
    //   50: aload_3
    //   51: invokestatic 42	android/support/v4/os/CancellationSignalCompatJellybean:cancel	(Ljava/lang/Object;)V
    //   54: aload_0
    //   55: monitorenter
    //   56: aload_0
    //   57: iconst_0
    //   58: putfield 24	android/support/v4/os/CancellationSignal:mCancelInProgress	Z
    //   61: aload_0
    //   62: invokevirtual 45	android/support/v4/os/CancellationSignal:notifyAll	()V
    //   65: aload_0
    //   66: monitorexit
    //   67: return
    //   68: astore_2
    //   69: aload_0
    //   70: monitorexit
    //   71: aload_2
    //   72: athrow
    //   73: astore_2
    //   74: aload_0
    //   75: monitorexit
    //   76: aload_2
    //   77: athrow
    //   78: astore_2
    //   79: aload_0
    //   80: monitorenter
    //   81: aload_0
    //   82: iconst_0
    //   83: putfield 24	android/support/v4/os/CancellationSignal:mCancelInProgress	Z
    //   86: aload_0
    //   87: invokevirtual 45	android/support/v4/os/CancellationSignal:notifyAll	()V
    //   90: aload_0
    //   91: monitorexit
    //   92: aload_2
    //   93: athrow
    //   94: astore_2
    //   95: aload_0
    //   96: monitorexit
    //   97: aload_2
    //   98: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	99	0	this	CancellationSignal
    //   6	2	1	bool	boolean
    //   28	13	2	localOnCancelListener	OnCancelListener
    //   68	4	2	localObject1	Object
    //   73	4	2	localObject2	Object
    //   78	15	2	localObject3	Object
    //   94	4	2	localObject4	Object
    //   33	18	3	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   2	7	68	finally
    //   14	34	68	finally
    //   56	65	73	finally
    //   40	46	78	finally
    //   50	54	78	finally
    //   81	90	94	finally
  }
  
  public Object getCancellationSignalObject()
  {
    if (Build.VERSION.SDK_INT < 16) {
      return null;
    }
    try
    {
      if (this.mCancellationSignalObj == null)
      {
        this.mCancellationSignalObj = CancellationSignalCompatJellybean.create();
        if (this.mIsCanceled) {
          CancellationSignalCompatJellybean.cancel(this.mCancellationSignalObj);
        }
      }
      Object localObject1 = this.mCancellationSignalObj;
      return localObject1;
    }
    finally {}
  }
  
  public boolean isCanceled()
  {
    try
    {
      boolean bool = this.mIsCanceled;
      return bool;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void setOnCancelListener(OnCancelListener paramOnCancelListener)
  {
    try
    {
      waitForCancelFinishedLocked();
      OnCancelListener localOnCancelListener = this.mOnCancelListener;
      if (localOnCancelListener == paramOnCancelListener) {
        return;
      }
      this.mOnCancelListener = paramOnCancelListener;
      boolean bool = this.mIsCanceled;
      if ((!bool) || (paramOnCancelListener == null)) {
        return;
      }
      paramOnCancelListener.onCancel();
      return;
    }
    finally {}
  }
  
  public void throwIfCanceled()
  {
    if (isCanceled()) {
      throw new OperationCanceledException();
    }
  }
  
  public static abstract interface OnCancelListener
  {
    public abstract void onCancel();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\os\CancellationSignal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */