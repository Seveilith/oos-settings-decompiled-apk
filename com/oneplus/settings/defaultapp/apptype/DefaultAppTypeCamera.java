package com.oneplus.settings.defaultapp.apptype;

import android.content.Intent;
import android.content.IntentFilter;
import java.util.ArrayList;
import java.util.List;

public class DefaultAppTypeCamera
  extends DefaultAppTypeInfo
{
  public List<IntentFilter> getAppFilter()
  {
    ArrayList localArrayList = new ArrayList();
    IntentFilter localIntentFilter = new IntentFilter("android.media.action.IMAGE_CAPTURE");
    localIntentFilter.addCategory("android.intent.category.DEFAULT");
    localArrayList.add(localIntentFilter);
    localIntentFilter = new IntentFilter("android.media.action.VIDEO_CAPTURE");
    localIntentFilter.addCategory("android.intent.category.DEFAULT");
    localArrayList.add(localIntentFilter);
    localIntentFilter = new IntentFilter("android.media.action.VIDEO_CAMERA");
    localIntentFilter.addCategory("android.intent.category.DEFAULT");
    localArrayList.add(localIntentFilter);
    localIntentFilter = new IntentFilter("android.media.action.STILL_IMAGE_CAMERA");
    localIntentFilter.addCategory("android.intent.category.DEFAULT");
    localArrayList.add(localIntentFilter);
    localIntentFilter.addAction("com.oppo.action.CAMERA");
    localIntentFilter.addCategory("android.intent.category.DEFAULT");
    localArrayList.add(localIntentFilter);
    return localArrayList;
  }
  
  public List<Intent> getAppIntent()
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(new Intent("android.media.action.IMAGE_CAPTURE"));
    localArrayList.add(new Intent("android.media.action.VIDEO_CAPTURE"));
    localArrayList.add(new Intent("android.media.action.VIDEO_CAMERA"));
    localArrayList.add(new Intent("android.media.action.STILL_IMAGE_CAMERA"));
    localArrayList.add(new Intent("com.oppo.action.CAMERA"));
    return localArrayList;
  }
  
  public List<Integer> getAppMatchParam()
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(Integer.valueOf(1048576));
    localArrayList.add(Integer.valueOf(1048576));
    localArrayList.add(Integer.valueOf(1048576));
    localArrayList.add(Integer.valueOf(1048576));
    localArrayList.add(Integer.valueOf(1048576));
    return localArrayList;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\defaultapp\apptype\DefaultAppTypeCamera.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */