package android.support.v13.view.inputmethod;

import android.os.Bundle;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.view.inputmethod.InputContentInfo;

final class InputConnectionCompatApi25
{
  public static boolean commitContent(InputConnection paramInputConnection, Object paramObject, int paramInt, Bundle paramBundle)
  {
    return paramInputConnection.commitContent((InputContentInfo)paramObject, paramInt, paramBundle);
  }
  
  public static InputConnection createWrapper(InputConnection paramInputConnection, final OnCommitContentListener paramOnCommitContentListener)
  {
    new InputConnectionWrapper(paramInputConnection, false)
    {
      public boolean commitContent(InputContentInfo paramAnonymousInputContentInfo, int paramAnonymousInt, Bundle paramAnonymousBundle)
      {
        if (paramOnCommitContentListener.onCommitContent(paramAnonymousInputContentInfo, paramAnonymousInt, paramAnonymousBundle)) {
          return true;
        }
        return super.commitContent(paramAnonymousInputContentInfo, paramAnonymousInt, paramAnonymousBundle);
      }
    };
  }
  
  public static abstract interface OnCommitContentListener
  {
    public abstract boolean onCommitContent(Object paramObject, int paramInt, Bundle paramBundle);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v13\view\inputmethod\InputConnectionCompatApi25.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */