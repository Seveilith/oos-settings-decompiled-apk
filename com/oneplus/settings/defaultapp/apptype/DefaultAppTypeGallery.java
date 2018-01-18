package com.oneplus.settings.defaultapp.apptype;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;

public class DefaultAppTypeGallery
  extends DefaultAppTypeInfo
{
  public List<IntentFilter> getAppFilter()
  {
    ArrayList localArrayList = new ArrayList();
    IntentFilter localIntentFilter = new IntentFilter("android.intent.action.VIEW");
    localIntentFilter.addCategory("android.intent.category.DEFAULT");
    localIntentFilter.addDataScheme("content");
    try
    {
      localIntentFilter.addDataType("image/*");
      localArrayList.add(localIntentFilter);
      localIntentFilter = new IntentFilter("android.intent.action.VIEW");
      localIntentFilter.addCategory("android.intent.category.DEFAULT");
      localIntentFilter.addDataScheme("file");
    }
    catch (Exception localException1)
    {
      try
      {
        localIntentFilter.addDataType("image/*");
        localArrayList.add(localIntentFilter);
        return localArrayList;
        localException1 = localException1;
        localException1.printStackTrace();
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          localException2.printStackTrace();
        }
      }
    }
  }
  
  public List<Intent> getAppIntent()
  {
    ArrayList localArrayList = new ArrayList();
    Intent localIntent = new Intent("android.intent.action.VIEW");
    localIntent.addCategory("android.intent.category.DEFAULT");
    localIntent.setDataAndType(Uri.parse("content://"), "image/*");
    localArrayList.add(localIntent);
    localIntent = new Intent("android.intent.action.VIEW");
    localIntent.addCategory("android.intent.category.DEFAULT");
    localIntent.setDataAndType(Uri.parse("file://"), "image/*");
    localArrayList.add(localIntent);
    return localArrayList;
  }
  
  public List<Integer> getAppMatchParam()
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(Integer.valueOf(6291456));
    localArrayList.add(Integer.valueOf(6291456));
    return localArrayList;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\defaultapp\apptype\DefaultAppTypeGallery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */