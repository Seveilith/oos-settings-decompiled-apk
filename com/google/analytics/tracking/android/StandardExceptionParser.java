package com.google.analytics.tracking.android;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class StandardExceptionParser
  implements ExceptionParser
{
  private final TreeSet<String> includedPackages = new TreeSet();
  
  public StandardExceptionParser(Context paramContext, Collection<String> paramCollection)
  {
    setIncludedPackages(paramContext, paramCollection);
  }
  
  protected StackTraceElement getBestStackTraceElement(Throwable paramThrowable)
  {
    paramThrowable = paramThrowable.getStackTrace();
    if (paramThrowable == null) {}
    while (paramThrowable.length == 0) {
      return null;
    }
    int j = paramThrowable.length;
    int i = 0;
    if (i >= j) {
      return paramThrowable[0];
    }
    StackTraceElement localStackTraceElement = paramThrowable[i];
    String str = localStackTraceElement.getClassName();
    Iterator localIterator = this.includedPackages.iterator();
    do
    {
      if (!localIterator.hasNext())
      {
        i += 1;
        break;
      }
    } while (!str.startsWith((String)localIterator.next()));
    return localStackTraceElement;
  }
  
  protected Throwable getCause(Throwable paramThrowable)
  {
    for (;;)
    {
      if (paramThrowable.getCause() == null) {
        return paramThrowable;
      }
      paramThrowable = paramThrowable.getCause();
    }
  }
  
  public String getDescription(String paramString, Throwable paramThrowable)
  {
    return getDescription(getCause(paramThrowable), getBestStackTraceElement(getCause(paramThrowable)), paramString);
  }
  
  protected String getDescription(Throwable paramThrowable, StackTraceElement paramStackTraceElement, String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramThrowable.getClass().getSimpleName());
    if (paramStackTraceElement == null) {
      if (paramString != null) {
        break label112;
      }
    }
    for (;;)
    {
      return localStringBuilder.toString();
      String[] arrayOfString = paramStackTraceElement.getClassName().split("\\.");
      paramThrowable = "unknown";
      if (arrayOfString == null) {}
      for (;;)
      {
        localStringBuilder.append(String.format(" (@%s:%s:%s)", new Object[] { paramThrowable, paramStackTraceElement.getMethodName(), Integer.valueOf(paramStackTraceElement.getLineNumber()) }));
        break;
        if (arrayOfString.length > 0) {
          paramThrowable = arrayOfString[(arrayOfString.length - 1)];
        }
      }
      label112:
      localStringBuilder.append(String.format(" {%s}", new Object[] { paramString }));
    }
  }
  
  public void setIncludedPackages(Context paramContext, Collection<String> paramCollection)
  {
    this.includedPackages.clear();
    Object localObject = new HashSet();
    if (paramCollection == null)
    {
      if (paramContext != null) {
        break label54;
      }
      label24:
      paramContext = ((Set)localObject).iterator();
    }
    label54:
    label155:
    label227:
    for (;;)
    {
      if (!paramContext.hasNext())
      {
        return;
        ((Set)localObject).addAll(paramCollection);
        break;
        try
        {
          paramCollection = paramContext.getApplicationContext().getPackageName();
          this.includedPackages.add(paramCollection);
          paramContext = paramContext.getApplicationContext().getPackageManager().getPackageInfo(paramCollection, 15).activities;
          if (paramContext == null) {
            break label24;
          }
          int j = paramContext.length;
          i = 0;
          while (i < j)
          {
            ((Set)localObject).add(paramContext[i].packageName);
            i += 1;
          }
        }
        catch (PackageManager.NameNotFoundException paramContext)
        {
          Log.i("No package found");
        }
      }
      paramCollection = (String)paramContext.next();
      localObject = this.includedPackages.iterator();
      int i = 1;
      if (!((Iterator)localObject).hasNext()) {}
      for (;;)
      {
        if (i == 0) {
          break label227;
        }
        this.includedPackages.add(paramCollection);
        break;
        String str = (String)((Iterator)localObject).next();
        if (paramCollection.startsWith(str))
        {
          i = 0;
          break label155;
        }
        if (str.startsWith(paramCollection)) {
          this.includedPackages.remove(str);
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\analytics\tracking\android\StandardExceptionParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */