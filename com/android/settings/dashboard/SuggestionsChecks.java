package com.android.settings.dashboard;

import android.app.AutomaticZenRule;
import android.app.IWallpaperManager;
import android.app.IWallpaperManager.Stub;
import android.app.IWallpaperManagerCallback;
import android.app.IWallpaperManagerCallback.Stub;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import com.android.ims.ImsManager;
import com.android.settings.Settings.FingerprintEnrollSuggestionActivity;
import com.android.settings.Settings.FingerprintSuggestionActivity;
import com.android.settings.Settings.ScreenLockSuggestionActivity;
import com.android.settings.Settings.WallpaperSuggestionActivity;
import com.android.settings.Settings.WifiCallingSuggestionActivity;
import com.android.settings.Settings.ZenModeAutomationSuggestionActivity;
import com.android.settingslib.drawer.Tile;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SuggestionsChecks
{
  private final IWallpaperManagerCallback mCallback = new IWallpaperManagerCallback.Stub()
  {
    public void onWallpaperChanged()
      throws RemoteException
    {}
  };
  private final Context mContext;
  
  public SuggestionsChecks(Context paramContext)
  {
    this.mContext = paramContext.getApplicationContext();
  }
  
  private boolean hasEnabledZenAutoRules()
  {
    Iterator localIterator = NotificationManager.from(this.mContext).getAutomaticZenRules().values().iterator();
    while (localIterator.hasNext()) {
      if (((AutomaticZenRule)localIterator.next()).isEnabled()) {
        return true;
      }
    }
    return false;
  }
  
  private boolean hasWallpaperSet()
  {
    Object localObject = IWallpaperManager.Stub.asInterface(ServiceManager.getService("wallpaper"));
    try
    {
      localObject = ((IWallpaperManager)localObject).getWallpaper(this.mCallback, 1, new Bundle(), this.mContext.getUserId());
      return localObject != null;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  private boolean isDeviceSecured()
  {
    return ((KeyguardManager)this.mContext.getSystemService(KeyguardManager.class)).isKeyguardSecure();
  }
  
  private boolean isNotSingleFingerprintEnrolled()
  {
    FingerprintManager localFingerprintManager = (FingerprintManager)this.mContext.getSystemService(FingerprintManager.class);
    return (localFingerprintManager == null) || (localFingerprintManager.getEnrolledFingerprints().size() != 1);
  }
  
  public boolean isSuggestionComplete(Tile paramTile)
  {
    paramTile = paramTile.intent.getComponent().getClassName();
    if (paramTile.equals(Settings.ZenModeAutomationSuggestionActivity.class.getName())) {
      return hasEnabledZenAutoRules();
    }
    if (paramTile.equals(Settings.WallpaperSuggestionActivity.class.getName())) {
      return hasWallpaperSet();
    }
    if (paramTile.equals(Settings.WifiCallingSuggestionActivity.class.getName())) {
      return isWifiCallingUnavailableOrEnabled();
    }
    if (paramTile.equals(Settings.FingerprintSuggestionActivity.class.getName())) {
      return isNotSingleFingerprintEnrolled();
    }
    if ((paramTile.equals(Settings.ScreenLockSuggestionActivity.class.getName())) || (paramTile.equals(Settings.FingerprintEnrollSuggestionActivity.class.getName()))) {
      return isDeviceSecured();
    }
    return false;
  }
  
  public boolean isWifiCallingUnavailableOrEnabled()
  {
    if (!ImsManager.isWfcEnabledByPlatform(this.mContext)) {
      return true;
    }
    if (ImsManager.isWfcEnabledByUser(this.mContext)) {
      return ImsManager.isNonTtyOrTtyOnVolteEnabled(this.mContext);
    }
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\dashboard\SuggestionsChecks.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */