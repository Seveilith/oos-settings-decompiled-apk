package android.support.v7.widget;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public class TintContextWrapper
  extends ContextWrapper
{
  private static final Object CACHE_LOCK = new Object();
  private static ArrayList<WeakReference<TintContextWrapper>> sCache;
  private final Resources mResources;
  private final Resources.Theme mTheme;
  
  private TintContextWrapper(@NonNull Context paramContext)
  {
    super(paramContext);
    if (VectorEnabledTintResources.shouldBeUsed())
    {
      this.mResources = new VectorEnabledTintResources(this, paramContext.getResources());
      this.mTheme = this.mResources.newTheme();
      this.mTheme.setTo(paramContext.getTheme());
      return;
    }
    this.mResources = new TintResources(this, paramContext.getResources());
    this.mTheme = null;
  }
  
  private static boolean shouldWrap(@NonNull Context paramContext)
  {
    if (((paramContext instanceof TintContextWrapper)) || ((paramContext.getResources() instanceof TintResources)) || ((paramContext.getResources() instanceof VectorEnabledTintResources))) {
      return false;
    }
    if (Build.VERSION.SDK_INT >= 21) {
      return VectorEnabledTintResources.shouldBeUsed();
    }
    return true;
  }
  
  public static Context wrap(@NonNull Context paramContext)
  {
    if (shouldWrap(paramContext)) {}
    for (;;)
    {
      int i;
      synchronized (CACHE_LOCK)
      {
        if (sCache == null)
        {
          sCache = new ArrayList();
          paramContext = new TintContextWrapper(paramContext);
          sCache.add(new WeakReference(paramContext));
          return paramContext;
        }
        i = sCache.size() - 1;
        if (i >= 0)
        {
          localObject1 = (WeakReference)sCache.get(i);
          if ((localObject1 != null) && (((WeakReference)localObject1).get() != null)) {
            break label178;
          }
          sCache.remove(i);
          break label178;
        }
        i = sCache.size() - 1;
        if (i < 0) {
          continue;
        }
        Object localObject1 = (WeakReference)sCache.get(i);
        if (localObject1 != null)
        {
          localObject1 = (TintContextWrapper)((WeakReference)localObject1).get();
          if (localObject1 != null)
          {
            Context localContext = ((TintContextWrapper)localObject1).getBaseContext();
            if (localContext == paramContext) {
              return (Context)localObject1;
            }
          }
        }
        else
        {
          localObject1 = null;
          continue;
        }
        i -= 1;
      }
      return paramContext;
      label178:
      i -= 1;
    }
  }
  
  public Resources getResources()
  {
    return this.mResources;
  }
  
  public Resources.Theme getTheme()
  {
    if (this.mTheme == null) {
      return super.getTheme();
    }
    return this.mTheme;
  }
  
  public void setTheme(int paramInt)
  {
    if (this.mTheme == null)
    {
      super.setTheme(paramInt);
      return;
    }
    this.mTheme.applyStyle(paramInt, true);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\android\support\v7\widget\TintContextWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */