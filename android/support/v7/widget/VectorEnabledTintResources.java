package android.support.v7.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v7.app.AppCompatDelegate;
import java.lang.ref.WeakReference;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public class VectorEnabledTintResources
  extends Resources
{
  public static final int MAX_SDK_WHERE_REQUIRED = 20;
  private final WeakReference<Context> mContextRef;
  
  public VectorEnabledTintResources(@NonNull Context paramContext, @NonNull Resources paramResources)
  {
    super(paramResources.getAssets(), paramResources.getDisplayMetrics(), paramResources.getConfiguration());
    this.mContextRef = new WeakReference(paramContext);
  }
  
  public static boolean shouldBeUsed()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (AppCompatDelegate.isCompatVectorFromResourcesEnabled())
    {
      bool1 = bool2;
      if (Build.VERSION.SDK_INT <= 20) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public Drawable getDrawable(int paramInt)
    throws Resources.NotFoundException
  {
    Context localContext = (Context)this.mContextRef.get();
    if (localContext != null) {
      return AppCompatDrawableManager.get().onDrawableLoadedFromResources(localContext, this, paramInt);
    }
    return super.getDrawable(paramInt);
  }
  
  final Drawable superGetDrawable(int paramInt)
  {
    return super.getDrawable(paramInt);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\android\support\v7\widget\VectorEnabledTintResources.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */