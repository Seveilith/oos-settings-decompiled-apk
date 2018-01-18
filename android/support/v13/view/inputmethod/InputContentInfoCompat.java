package android.support.v13.view.inputmethod;

import android.content.ClipDescription;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.os.BuildCompat;

public final class InputContentInfoCompat
{
  private final InputContentInfoCompatImpl mImpl;
  
  public InputContentInfoCompat(@NonNull Uri paramUri1, @NonNull ClipDescription paramClipDescription, @Nullable Uri paramUri2)
  {
    if (BuildCompat.isAtLeastNMR1())
    {
      this.mImpl = new Api25InputContentInfoCompatImpl(paramUri1, paramClipDescription, paramUri2);
      return;
    }
    this.mImpl = new BaseInputContentInfoCompatImpl(paramUri1, paramClipDescription, paramUri2);
  }
  
  private InputContentInfoCompat(@NonNull InputContentInfoCompatImpl paramInputContentInfoCompatImpl)
  {
    this.mImpl = paramInputContentInfoCompatImpl;
  }
  
  @Nullable
  public static InputContentInfoCompat wrap(@Nullable Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if (!BuildCompat.isAtLeastNMR1()) {
      return null;
    }
    return new InputContentInfoCompat(new Api25InputContentInfoCompatImpl(paramObject));
  }
  
  @NonNull
  public Uri getContentUri()
  {
    return this.mImpl.getContentUri();
  }
  
  @NonNull
  public ClipDescription getDescription()
  {
    return this.mImpl.getDescription();
  }
  
  @Nullable
  public Uri getLinkUri()
  {
    return this.mImpl.getLinkUri();
  }
  
  public void releasePermission()
  {
    this.mImpl.releasePermission();
  }
  
  public void requestPermission()
  {
    this.mImpl.requestPermission();
  }
  
  @Nullable
  public Object unwrap()
  {
    return this.mImpl.getInputContentInfo();
  }
  
  private static final class Api25InputContentInfoCompatImpl
    implements InputContentInfoCompat.InputContentInfoCompatImpl
  {
    @NonNull
    final Object mObject;
    
    public Api25InputContentInfoCompatImpl(@NonNull Uri paramUri1, @NonNull ClipDescription paramClipDescription, @Nullable Uri paramUri2)
    {
      this.mObject = InputContentInfoCompatApi25.create(paramUri1, paramClipDescription, paramUri2);
    }
    
    public Api25InputContentInfoCompatImpl(@NonNull Object paramObject)
    {
      this.mObject = paramObject;
    }
    
    @NonNull
    public Uri getContentUri()
    {
      return InputContentInfoCompatApi25.getContentUri(this.mObject);
    }
    
    @NonNull
    public ClipDescription getDescription()
    {
      return InputContentInfoCompatApi25.getDescription(this.mObject);
    }
    
    @Nullable
    public Object getInputContentInfo()
    {
      return this.mObject;
    }
    
    @Nullable
    public Uri getLinkUri()
    {
      return InputContentInfoCompatApi25.getLinkUri(this.mObject);
    }
    
    public void releasePermission()
    {
      InputContentInfoCompatApi25.releasePermission(this.mObject);
    }
    
    public void requestPermission()
    {
      InputContentInfoCompatApi25.requestPermission(this.mObject);
    }
  }
  
  private static final class BaseInputContentInfoCompatImpl
    implements InputContentInfoCompat.InputContentInfoCompatImpl
  {
    @NonNull
    private final Uri mContentUri;
    @NonNull
    private final ClipDescription mDescription;
    @Nullable
    private final Uri mLinkUri;
    
    public BaseInputContentInfoCompatImpl(@NonNull Uri paramUri1, @NonNull ClipDescription paramClipDescription, @Nullable Uri paramUri2)
    {
      this.mContentUri = paramUri1;
      this.mDescription = paramClipDescription;
      this.mLinkUri = paramUri2;
    }
    
    @NonNull
    public Uri getContentUri()
    {
      return this.mContentUri;
    }
    
    @NonNull
    public ClipDescription getDescription()
    {
      return this.mDescription;
    }
    
    @Nullable
    public Object getInputContentInfo()
    {
      return null;
    }
    
    @Nullable
    public Uri getLinkUri()
    {
      return this.mLinkUri;
    }
    
    public void releasePermission() {}
    
    public void requestPermission() {}
  }
  
  private static abstract interface InputContentInfoCompatImpl
  {
    @NonNull
    public abstract Uri getContentUri();
    
    @NonNull
    public abstract ClipDescription getDescription();
    
    @Nullable
    public abstract Object getInputContentInfo();
    
    @Nullable
    public abstract Uri getLinkUri();
    
    public abstract void releasePermission();
    
    public abstract void requestPermission();
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v13\view\inputmethod\InputContentInfoCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */