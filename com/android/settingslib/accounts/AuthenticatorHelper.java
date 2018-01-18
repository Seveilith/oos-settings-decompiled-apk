package com.android.settingslib.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SyncAdapterType;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.UserHandle;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class AuthenticatorHelper
  extends BroadcastReceiver
{
  private static final String ONEPLUS_ACCOUNT_TYPE = "com.oneplus.account";
  private static final String TAG = "AuthenticatorHelper";
  private final Map<String, Drawable> mAccTypeIconCache = new HashMap();
  private final HashMap<String, ArrayList<String>> mAccountTypeToAuthorities = new HashMap();
  private final Context mContext;
  private final ArrayList<String> mEnabledAccountTypes = new ArrayList();
  private final OnAccountsUpdateListener mListener;
  private boolean mListeningToAccountUpdates;
  private final Map<String, AuthenticatorDescription> mTypeToAuthDescription = new HashMap();
  private final UserHandle mUserHandle;
  
  public AuthenticatorHelper(Context paramContext, UserHandle paramUserHandle, OnAccountsUpdateListener paramOnAccountsUpdateListener)
  {
    this.mContext = paramContext;
    this.mUserHandle = paramUserHandle;
    this.mListener = paramOnAccountsUpdateListener;
    onAccountsUpdated(null);
  }
  
  private void buildAccountTypeToAuthoritiesMap()
  {
    this.mAccountTypeToAuthorities.clear();
    SyncAdapterType[] arrayOfSyncAdapterType = ContentResolver.getSyncAdapterTypesAsUser(this.mUserHandle.getIdentifier());
    int i = 0;
    int j = arrayOfSyncAdapterType.length;
    while (i < j)
    {
      SyncAdapterType localSyncAdapterType = arrayOfSyncAdapterType[i];
      ArrayList localArrayList2 = (ArrayList)this.mAccountTypeToAuthorities.get(localSyncAdapterType.accountType);
      ArrayList localArrayList1 = localArrayList2;
      if (localArrayList2 == null)
      {
        localArrayList1 = new ArrayList();
        this.mAccountTypeToAuthorities.put(localSyncAdapterType.accountType, localArrayList1);
      }
      if (Log.isLoggable("AuthenticatorHelper", 2)) {
        Log.v("AuthenticatorHelper", "Added authority " + localSyncAdapterType.authority + " to accountType " + localSyncAdapterType.accountType);
      }
      localArrayList1.add(localSyncAdapterType.authority);
      i += 1;
    }
  }
  
  public boolean containsAccountType(String paramString)
  {
    return this.mTypeToAuthDescription.containsKey(paramString);
  }
  
  public AuthenticatorDescription getAccountTypeDescription(String paramString)
  {
    return (AuthenticatorDescription)this.mTypeToAuthDescription.get(paramString);
  }
  
  public ArrayList<String> getAuthoritiesForAccountType(String paramString)
  {
    return (ArrayList)this.mAccountTypeToAuthorities.get(paramString);
  }
  
  public Drawable getDrawableForType(Context paramContext, String paramString)
  {
    localDrawable2 = null;
    localDrawable1 = null;
    Context localContext;
    synchronized (this.mAccTypeIconCache)
    {
      if (this.mAccTypeIconCache.containsKey(paramString))
      {
        paramContext = (Drawable)this.mAccTypeIconCache.get(paramString);
        return paramContext;
      }
      if (this.mTypeToAuthDescription.containsKey(paramString)) {
        localDrawable1 = localDrawable2;
      }
    }
  }
  
  public String[] getEnabledAccountTypes()
  {
    return (String[])this.mEnabledAccountTypes.toArray(new String[this.mEnabledAccountTypes.size()]);
  }
  
  public CharSequence getLabelForType(Context paramContext, String paramString)
  {
    Object localObject = null;
    if (this.mTypeToAuthDescription.containsKey(paramString)) {}
    try
    {
      localObject = (AuthenticatorDescription)this.mTypeToAuthDescription.get(paramString);
      localObject = paramContext.createPackageContextAsUser(((AuthenticatorDescription)localObject).packageName, 0, this.mUserHandle).getResources().getText(((AuthenticatorDescription)localObject).labelId);
      return (CharSequence)localObject;
    }
    catch (Resources.NotFoundException paramContext)
    {
      Log.w("AuthenticatorHelper", "No label icon for account type " + paramString);
      return null;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      Log.w("AuthenticatorHelper", "No label name for account type " + paramString);
    }
    return null;
  }
  
  public int getLabelIdForType(String paramString)
  {
    if (this.mTypeToAuthDescription.containsKey(paramString)) {
      return ((AuthenticatorDescription)this.mTypeToAuthDescription.get(paramString)).labelId;
    }
    return -1;
  }
  
  public String getPackageForType(String paramString)
  {
    if (this.mTypeToAuthDescription.containsKey(paramString)) {
      return ((AuthenticatorDescription)this.mTypeToAuthDescription.get(paramString)).packageName;
    }
    return null;
  }
  
  public boolean hasAccountPreferences(String paramString)
  {
    if (containsAccountType(paramString))
    {
      paramString = getAccountTypeDescription(paramString);
      if ((paramString != null) && (paramString.accountPreferencesId != 0)) {
        return true;
      }
    }
    return false;
  }
  
  public void listenToAccountUpdates()
  {
    if (!this.mListeningToAccountUpdates)
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.accounts.LOGIN_ACCOUNTS_CHANGED");
      localIntentFilter.addAction("android.intent.action.DEVICE_STORAGE_OK");
      this.mContext.registerReceiverAsUser(this, this.mUserHandle, localIntentFilter, null, null);
      this.mListeningToAccountUpdates = true;
    }
  }
  
  void onAccountsUpdated(Account[] paramArrayOfAccount)
  {
    updateAuthDescriptions(this.mContext);
    Account[] arrayOfAccount = paramArrayOfAccount;
    if (paramArrayOfAccount == null) {
      arrayOfAccount = AccountManager.get(this.mContext).getAccountsAsUser(this.mUserHandle.getIdentifier());
    }
    this.mEnabledAccountTypes.clear();
    this.mAccTypeIconCache.clear();
    int i = 0;
    if (i < arrayOfAccount.length)
    {
      paramArrayOfAccount = arrayOfAccount[i];
      if ("com.oneplus.account".equals(paramArrayOfAccount.type)) {
        Log.v("AuthenticatorHelper", "Ignore OnePlus account entry point");
      }
      for (;;)
      {
        i += 1;
        break;
        if (!this.mEnabledAccountTypes.contains(paramArrayOfAccount.type)) {
          this.mEnabledAccountTypes.add(paramArrayOfAccount.type);
        }
      }
    }
    buildAccountTypeToAuthoritiesMap();
    if (this.mListeningToAccountUpdates) {
      this.mListener.onAccountsUpdate(this.mUserHandle);
    }
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    onAccountsUpdated(AccountManager.get(this.mContext).getAccountsAsUser(this.mUserHandle.getIdentifier()));
  }
  
  public void preloadDrawableForType(final Context paramContext, final String paramString)
  {
    new AsyncTask()
    {
      protected Void doInBackground(Void... paramAnonymousVarArgs)
      {
        AuthenticatorHelper.this.getDrawableForType(paramContext, paramString);
        return null;
      }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
  }
  
  public void stopListeningToAccountUpdates()
  {
    if (this.mListeningToAccountUpdates)
    {
      this.mContext.unregisterReceiver(this);
      this.mListeningToAccountUpdates = false;
    }
  }
  
  public void updateAuthDescriptions(Context paramContext)
  {
    paramContext = AccountManager.get(paramContext).getAuthenticatorTypesAsUser(this.mUserHandle.getIdentifier());
    int i = 0;
    while (i < paramContext.length)
    {
      this.mTypeToAuthDescription.put(paramContext[i].type, paramContext[i]);
      i += 1;
    }
  }
  
  public static abstract interface OnAccountsUpdateListener
  {
    public abstract void onAccountsUpdate(UserHandle paramUserHandle);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\accounts\AuthenticatorHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */