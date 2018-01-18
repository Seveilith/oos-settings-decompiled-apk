package com.oneplus.settings.defaultapp.apptype;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;

public class DefaultAppTypeEmail
  extends DefaultAppTypeInfo
{
  public List<IntentFilter> getAppFilter()
  {
    ArrayList localArrayList = new ArrayList();
    IntentFilter localIntentFilter = new IntentFilter("android.intent.action.SEND");
    localIntentFilter.addCategory("android.intent.category.DEFAULT");
    localIntentFilter.addDataScheme("mailto");
    localArrayList.add(localIntentFilter);
    localIntentFilter.addAction("android.intent.action.SENDTO");
    localIntentFilter.addCategory("android.intent.category.DEFAULT");
    localIntentFilter.addDataScheme("mailto");
    localArrayList.add(localIntentFilter);
    localIntentFilter.addAction("android.intent.action.VIEW");
    localIntentFilter.addCategory("android.intent.category.DEFAULT");
    localIntentFilter.addCategory("android.intent.category.BROWSABLE");
    localIntentFilter.addDataScheme("mailto");
    localArrayList.add(localIntentFilter);
    return localArrayList;
  }
  
  public List<Intent> getAppIntent()
  {
    ArrayList localArrayList = new ArrayList();
    Intent localIntent = new Intent("android.intent.action.SEND");
    localIntent.addCategory("android.intent.category.DEFAULT");
    localIntent.setDataAndType(Uri.parse("mailto://"), null);
    localArrayList.add(localIntent);
    localIntent = new Intent("android.intent.action.SENDTO");
    localIntent.addCategory("android.intent.category.DEFAULT");
    localIntent.setDataAndType(Uri.parse("mailto://"), null);
    localArrayList.add(localIntent);
    localIntent = new Intent("android.intent.action.VIEW");
    localIntent.addCategory("android.intent.category.DEFAULT");
    localIntent.addCategory("android.intent.category.BROWSABLE");
    localIntent.setDataAndType(Uri.parse("mailto://"), null);
    localArrayList.add(localIntent);
    return localArrayList;
  }
  
  public List<Integer> getAppMatchParam()
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(Integer.valueOf(6291456));
    localArrayList.add(Integer.valueOf(2097152));
    localArrayList.add(Integer.valueOf(2097152));
    return localArrayList;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\defaultapp\apptype\DefaultAppTypeEmail.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */