package com.android.settings.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;
import com.android.settings.search.Index;
import com.android.settings.search.SearchIndexableRaw;
import com.android.settingslib.bluetooth.BluetoothEventManager;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.bluetooth.LocalBluetoothManager.BluetoothManagerCallback;
import com.android.settingslib.bluetooth.Utils.ErrorListener;

public final class Utils
{
  static final boolean D = true;
  static final boolean V = false;
  private static final Utils.ErrorListener mErrorListener = new Utils.ErrorListener()
  {
    public void onShowError(Context paramAnonymousContext, String paramAnonymousString, int paramAnonymousInt)
    {
      Utils.showError(paramAnonymousContext, paramAnonymousString, paramAnonymousInt);
    }
  };
  private static final LocalBluetoothManager.BluetoothManagerCallback mOnInitCallback = new LocalBluetoothManager.BluetoothManagerCallback()
  {
    public void onBluetoothManagerInitialized(Context paramAnonymousContext, LocalBluetoothManager paramAnonymousLocalBluetoothManager)
    {
      paramAnonymousLocalBluetoothManager.getEventManager().registerCallback(new DockService.DockBluetoothCallback(paramAnonymousContext));
      com.android.settingslib.bluetooth.Utils.setErrorListener(Utils.-get0());
    }
  };
  
  public static int getConnectionStateSummary(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0;
    case 2: 
      return 2131689532;
    case 1: 
      return 2131689531;
    case 0: 
      return 2131689529;
    }
    return 2131689530;
  }
  
  public static LocalBluetoothManager getLocalBtManager(Context paramContext)
  {
    return LocalBluetoothManager.getInstance(paramContext, mOnInitCallback);
  }
  
  static void showConnectingError(Context paramContext, String paramString)
  {
    showError(paramContext, paramString, 2131691251);
  }
  
  static AlertDialog showDisconnectDialog(Context paramContext, AlertDialog paramAlertDialog, DialogInterface.OnClickListener paramOnClickListener, CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    if (paramAlertDialog == null) {
      paramAlertDialog = new AlertDialog.Builder(paramContext).setPositiveButton(17039370, paramOnClickListener).setNegativeButton(17039360, null).create();
    }
    for (;;)
    {
      paramAlertDialog.setTitle(paramCharSequence1);
      paramAlertDialog.setMessage(paramCharSequence2);
      paramAlertDialog.show();
      return paramAlertDialog;
      if (paramAlertDialog.isShowing()) {
        paramAlertDialog.dismiss();
      }
      paramAlertDialog.setButton(-1, paramContext.getText(17039370), paramOnClickListener);
    }
  }
  
  static void showError(Context paramContext, String paramString, int paramInt)
  {
    paramString = paramContext.getString(paramInt, new Object[] { paramString });
    LocalBluetoothManager localLocalBluetoothManager = getLocalBtManager(paramContext);
    Context localContext = localLocalBluetoothManager.getForegroundActivity();
    if (localLocalBluetoothManager.isForegroundActivity())
    {
      if (localContext == null) {
        return;
      }
      paramContext = (Activity)localContext;
      if ((paramContext.isFinishing()) || (paramContext.isDestroyed())) {
        return;
      }
      new AlertDialog.Builder(localContext).setTitle(2131691250).setMessage(paramString).setPositiveButton(17039370, null).show();
      return;
    }
    Toast.makeText(paramContext, paramString, 0).show();
  }
  
  public static void updateSearchIndex(Context paramContext, String paramString1, String paramString2, String paramString3, int paramInt, boolean paramBoolean)
  {
    SearchIndexableRaw localSearchIndexableRaw = new SearchIndexableRaw(paramContext);
    localSearchIndexableRaw.className = paramString1;
    localSearchIndexableRaw.title = paramString2;
    localSearchIndexableRaw.screenTitle = paramString3;
    localSearchIndexableRaw.iconResId = paramInt;
    localSearchIndexableRaw.enabled = paramBoolean;
    Index.getInstance(paramContext).updateFromSearchIndexableData(localSearchIndexableRaw);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\bluetooth\Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */