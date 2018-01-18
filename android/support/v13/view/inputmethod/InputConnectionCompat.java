package android.support.v13.view.inputmethod;

import android.content.ClipDescription;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.os.BuildCompat;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

public final class InputConnectionCompat
{
  private static final InputConnectionCompatImpl IMPL;
  public static int INPUT_CONTENT_GRANT_READ_URI_PERMISSION;
  
  static
  {
    if (BuildCompat.isAtLeastNMR1()) {}
    for (IMPL = new Api25InputContentInfoCompatImpl(null);; IMPL = new BaseInputContentInfoCompatImpl())
    {
      INPUT_CONTENT_GRANT_READ_URI_PERMISSION = 1;
      return;
    }
  }
  
  public static boolean commitContent(@NonNull InputConnection paramInputConnection, @NonNull EditorInfo paramEditorInfo, @NonNull InputContentInfoCompat paramInputContentInfoCompat, int paramInt, @Nullable Bundle paramBundle)
  {
    ClipDescription localClipDescription = paramInputContentInfoCompat.getDescription();
    int k = 0;
    paramEditorInfo = EditorInfoCompat.getContentMimeTypes(paramEditorInfo);
    int m = paramEditorInfo.length;
    int i = 0;
    for (;;)
    {
      int j = k;
      if (i < m)
      {
        if (localClipDescription.hasMimeType(paramEditorInfo[i])) {
          j = 1;
        }
      }
      else
      {
        if (j != 0) {
          break;
        }
        return false;
      }
      i += 1;
    }
    return IMPL.commitContent(paramInputConnection, paramInputContentInfoCompat, paramInt, paramBundle);
  }
  
  @NonNull
  public static InputConnection createWrapper(@NonNull InputConnection paramInputConnection, @NonNull EditorInfo paramEditorInfo, @NonNull OnCommitContentListener paramOnCommitContentListener)
  {
    if (paramInputConnection == null) {
      throw new IllegalArgumentException("inputConnection must be non-null");
    }
    if (paramEditorInfo == null) {
      throw new IllegalArgumentException("editorInfo must be non-null");
    }
    if (paramOnCommitContentListener == null) {
      throw new IllegalArgumentException("onCommitContentListener must be non-null");
    }
    return IMPL.createWrapper(paramInputConnection, paramEditorInfo, paramOnCommitContentListener);
  }
  
  private static final class Api25InputContentInfoCompatImpl
    implements InputConnectionCompat.InputConnectionCompatImpl
  {
    public boolean commitContent(@NonNull InputConnection paramInputConnection, @NonNull InputContentInfoCompat paramInputContentInfoCompat, int paramInt, @Nullable Bundle paramBundle)
    {
      return InputConnectionCompatApi25.commitContent(paramInputConnection, paramInputContentInfoCompat.unwrap(), paramInt, paramBundle);
    }
    
    @Nullable
    public InputConnection createWrapper(@Nullable InputConnection paramInputConnection, @NonNull EditorInfo paramEditorInfo, @Nullable final InputConnectionCompat.OnCommitContentListener paramOnCommitContentListener)
    {
      InputConnectionCompatApi25.createWrapper(paramInputConnection, new InputConnectionCompatApi25.OnCommitContentListener()
      {
        public boolean onCommitContent(Object paramAnonymousObject, int paramAnonymousInt, Bundle paramAnonymousBundle)
        {
          paramAnonymousObject = InputContentInfoCompat.wrap(paramAnonymousObject);
          return paramOnCommitContentListener.onCommitContent((InputContentInfoCompat)paramAnonymousObject, paramAnonymousInt, paramAnonymousBundle);
        }
      });
    }
  }
  
  static final class BaseInputContentInfoCompatImpl
    implements InputConnectionCompat.InputConnectionCompatImpl
  {
    private static String COMMIT_CONTENT_ACTION = "android.support.v13.view.inputmethod.InputConnectionCompat.COMMIT_CONTENT";
    private static String COMMIT_CONTENT_CONTENT_URI_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_URI";
    private static String COMMIT_CONTENT_DESCRIPTION_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_DESCRIPTION";
    private static String COMMIT_CONTENT_FLAGS_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_FLAGS";
    private static String COMMIT_CONTENT_LINK_URI_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_LINK_URI";
    private static String COMMIT_CONTENT_OPTS_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_OPTS";
    private static String COMMIT_CONTENT_RESULT_RECEIVER = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_RESULT_RECEIVER";
    
    static boolean handlePerformPrivateCommand(@Nullable String paramString, @NonNull Bundle paramBundle, @NonNull InputConnectionCompat.OnCommitContentListener paramOnCommitContentListener)
    {
      int i = 1;
      if (!TextUtils.equals(COMMIT_CONTENT_ACTION, paramString)) {
        return false;
      }
      if (paramBundle == null) {
        return false;
      }
      paramString = null;
      try
      {
        ResultReceiver localResultReceiver = (ResultReceiver)paramBundle.getParcelable(COMMIT_CONTENT_RESULT_RECEIVER);
        paramString = localResultReceiver;
        Uri localUri1 = (Uri)paramBundle.getParcelable(COMMIT_CONTENT_CONTENT_URI_KEY);
        paramString = localResultReceiver;
        ClipDescription localClipDescription = (ClipDescription)paramBundle.getParcelable(COMMIT_CONTENT_DESCRIPTION_KEY);
        paramString = localResultReceiver;
        Uri localUri2 = (Uri)paramBundle.getParcelable(COMMIT_CONTENT_LINK_URI_KEY);
        paramString = localResultReceiver;
        int j = paramBundle.getInt(COMMIT_CONTENT_FLAGS_KEY);
        paramString = localResultReceiver;
        paramBundle = (Bundle)paramBundle.getParcelable(COMMIT_CONTENT_OPTS_KEY);
        paramString = localResultReceiver;
        boolean bool = paramOnCommitContentListener.onCommitContent(new InputContentInfoCompat(localUri1, localClipDescription, localUri2), j, paramBundle);
        if (localResultReceiver != null) {
          if (!bool) {
            break label154;
          }
        }
        label154:
        for (i = 1;; i = 0)
        {
          localResultReceiver.send(i, null);
          return bool;
        }
        if (0 == 0) {}
      }
      finally
      {
        if (paramString == null) {}
      }
      for (;;)
      {
        paramString.send(i, null);
        throw paramBundle;
        i = 0;
      }
    }
    
    public boolean commitContent(@NonNull InputConnection paramInputConnection, @NonNull InputContentInfoCompat paramInputContentInfoCompat, int paramInt, @Nullable Bundle paramBundle)
    {
      Bundle localBundle = new Bundle();
      localBundle.putParcelable(COMMIT_CONTENT_CONTENT_URI_KEY, paramInputContentInfoCompat.getContentUri());
      localBundle.putParcelable(COMMIT_CONTENT_DESCRIPTION_KEY, paramInputContentInfoCompat.getDescription());
      localBundle.putParcelable(COMMIT_CONTENT_LINK_URI_KEY, paramInputContentInfoCompat.getLinkUri());
      localBundle.putInt(COMMIT_CONTENT_FLAGS_KEY, paramInt);
      localBundle.putParcelable(COMMIT_CONTENT_OPTS_KEY, paramBundle);
      return paramInputConnection.performPrivateCommand(COMMIT_CONTENT_ACTION, localBundle);
    }
    
    @NonNull
    public InputConnection createWrapper(@NonNull InputConnection paramInputConnection, @NonNull EditorInfo paramEditorInfo, @NonNull final InputConnectionCompat.OnCommitContentListener paramOnCommitContentListener)
    {
      if (EditorInfoCompat.getContentMimeTypes(paramEditorInfo).length == 0) {
        return paramInputConnection;
      }
      new InputConnectionWrapper(paramInputConnection, false)
      {
        public boolean performPrivateCommand(String paramAnonymousString, Bundle paramAnonymousBundle)
        {
          if (InputConnectionCompat.BaseInputContentInfoCompatImpl.handlePerformPrivateCommand(paramAnonymousString, paramAnonymousBundle, paramOnCommitContentListener)) {
            return true;
          }
          return super.performPrivateCommand(paramAnonymousString, paramAnonymousBundle);
        }
      };
    }
  }
  
  private static abstract interface InputConnectionCompatImpl
  {
    public abstract boolean commitContent(@NonNull InputConnection paramInputConnection, @NonNull InputContentInfoCompat paramInputContentInfoCompat, int paramInt, @Nullable Bundle paramBundle);
    
    @NonNull
    public abstract InputConnection createWrapper(@NonNull InputConnection paramInputConnection, @NonNull EditorInfo paramEditorInfo, @NonNull InputConnectionCompat.OnCommitContentListener paramOnCommitContentListener);
  }
  
  public static abstract interface OnCommitContentListener
  {
    public abstract boolean onCommitContent(InputContentInfoCompat paramInputContentInfoCompat, int paramInt, Bundle paramBundle);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v13\view\inputmethod\InputConnectionCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */