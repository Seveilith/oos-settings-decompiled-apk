package android.support.v13.view.inputmethod;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.os.BuildCompat;
import android.view.inputmethod.EditorInfo;

public final class EditorInfoCompat
{
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  private static final EditorInfoCompatImpl IMPL = new BaseEditorInfoCompatImpl(null);
  
  static
  {
    if (BuildCompat.isAtLeastNMR1())
    {
      IMPL = new Api25EditorInfoCompatImpl(null);
      return;
    }
  }
  
  @NonNull
  public static String[] getContentMimeTypes(EditorInfo paramEditorInfo)
  {
    return IMPL.getContentMimeTypes(paramEditorInfo);
  }
  
  public static void setContentMimeTypes(@NonNull EditorInfo paramEditorInfo, @Nullable String[] paramArrayOfString)
  {
    IMPL.setContentMimeTypes(paramEditorInfo, paramArrayOfString);
  }
  
  private static final class Api25EditorInfoCompatImpl
    implements EditorInfoCompat.EditorInfoCompatImpl
  {
    @NonNull
    public String[] getContentMimeTypes(@NonNull EditorInfo paramEditorInfo)
    {
      paramEditorInfo = EditorInfoCompatApi25.getContentMimeTypes(paramEditorInfo);
      if (paramEditorInfo != null) {
        return paramEditorInfo;
      }
      return EditorInfoCompat.-get0();
    }
    
    public void setContentMimeTypes(@NonNull EditorInfo paramEditorInfo, @Nullable String[] paramArrayOfString)
    {
      EditorInfoCompatApi25.setContentMimeTypes(paramEditorInfo, paramArrayOfString);
    }
  }
  
  private static final class BaseEditorInfoCompatImpl
    implements EditorInfoCompat.EditorInfoCompatImpl
  {
    private static String CONTENT_MIME_TYPES_KEY = "android.support.v13.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES";
    
    @NonNull
    public String[] getContentMimeTypes(@NonNull EditorInfo paramEditorInfo)
    {
      if (paramEditorInfo.extras == null) {
        return EditorInfoCompat.-get0();
      }
      paramEditorInfo = paramEditorInfo.extras.getStringArray(CONTENT_MIME_TYPES_KEY);
      if (paramEditorInfo != null) {
        return paramEditorInfo;
      }
      return EditorInfoCompat.-get0();
    }
    
    public void setContentMimeTypes(@NonNull EditorInfo paramEditorInfo, @Nullable String[] paramArrayOfString)
    {
      if (paramEditorInfo.extras == null) {
        paramEditorInfo.extras = new Bundle();
      }
      paramEditorInfo.extras.putStringArray(CONTENT_MIME_TYPES_KEY, paramArrayOfString);
    }
  }
  
  private static abstract interface EditorInfoCompatImpl
  {
    @NonNull
    public abstract String[] getContentMimeTypes(@NonNull EditorInfo paramEditorInfo);
    
    public abstract void setContentMimeTypes(@NonNull EditorInfo paramEditorInfo, @Nullable String[] paramArrayOfString);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v13\view\inputmethod\EditorInfoCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */