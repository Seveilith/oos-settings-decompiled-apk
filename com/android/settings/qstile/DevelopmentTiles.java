package com.android.settings.qstile;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.SystemProperties;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import com.android.settings.DevelopmentSettings.SystemPropPoker;

public class DevelopmentTiles
{
  static final Class[] TILE_CLASSES = { ShowLayout.class, GPUProfiling.class };
  
  public static void setTilesEnabled(Context paramContext, boolean paramBoolean)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    Class[] arrayOfClass = TILE_CLASSES;
    int k = arrayOfClass.length;
    int i = 0;
    if (i < k)
    {
      ComponentName localComponentName = new ComponentName(paramContext, arrayOfClass[i]);
      if (paramBoolean) {}
      for (int j = 1;; j = 0)
      {
        localPackageManager.setComponentEnabledSetting(localComponentName, j, 1);
        i += 1;
        break;
      }
    }
  }
  
  public static class GPUProfiling
    extends TileService
  {
    public IBinder onBind(Intent paramIntent)
    {
      try
      {
        paramIntent = super.onBind(paramIntent);
        return paramIntent;
      }
      catch (RuntimeException paramIntent)
      {
        Log.d("DevelopmentTiles", "bind GPUProfiling service failed");
      }
      return null;
    }
    
    public void onClick()
    {
      if (getQsTile().getState() == 1) {}
      for (String str = "visual_bars";; str = "")
      {
        SystemProperties.set("debug.hwui.profile", str);
        new DevelopmentSettings.SystemPropPoker().execute(new Void[0]);
        refresh();
        return;
      }
    }
    
    public void onStartListening()
    {
      super.onStartListening();
      refresh();
    }
    
    public void refresh()
    {
      String str = SystemProperties.get("debug.hwui.profile");
      Tile localTile = getQsTile();
      if (str.equals("visual_bars")) {}
      for (int i = 2;; i = 1)
      {
        localTile.setState(i);
        getQsTile().updateTile();
        return;
      }
    }
  }
  
  public static class ShowLayout
    extends TileService
  {
    public IBinder onBind(Intent paramIntent)
    {
      try
      {
        paramIntent = super.onBind(paramIntent);
        return paramIntent;
      }
      catch (RuntimeException paramIntent)
      {
        Log.d("DevelopmentTiles", "bind GPUProfiling service failed");
      }
      return null;
    }
    
    public void onClick()
    {
      if (getQsTile().getState() == 1) {}
      for (String str = "true";; str = "false")
      {
        SystemProperties.set("debug.layout", str);
        new DevelopmentSettings.SystemPropPoker().execute(new Void[0]);
        refresh();
        return;
      }
    }
    
    public void onStartListening()
    {
      super.onStartListening();
      refresh();
    }
    
    public void refresh()
    {
      boolean bool = SystemProperties.getBoolean("debug.layout", false);
      Tile localTile = getQsTile();
      if (bool) {}
      for (int i = 2;; i = 1)
      {
        localTile.setState(i);
        getQsTile().updateTile();
        return;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\qstile\DevelopmentTiles.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */