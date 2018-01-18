package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.UserHandle;
import android.os.UserManager;
import android.security.KeyStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

public class UserCredentialsSettings
  extends OptionsMenuFragment
  implements AdapterView.OnItemClickListener
{
  private static final String TAG = "UserCredentialsSettings";
  private ListView mListView;
  private View mRootView;
  
  protected int getMetricsCategory()
  {
    return 285;
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mRootView = paramLayoutInflater.inflate(2130969096, paramViewGroup, false);
    this.mListView = ((ListView)this.mRootView.findViewById(2131362669));
    this.mListView.setOnItemClickListener(this);
    return this.mRootView;
  }
  
  public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    CredentialDialogFragment.show(this, (Credential)paramAdapterView.getItemAtPosition(paramInt));
  }
  
  public void onResume()
  {
    super.onResume();
    refreshItems();
  }
  
  protected void refreshItems()
  {
    if (isAdded()) {
      new AliasLoader(null).execute(new Void[0]);
    }
  }
  
  private class AliasLoader
    extends AsyncTask<Void, Void, SortedMap<String, UserCredentialsSettings.Credential>>
  {
    private AliasLoader() {}
    
    protected SortedMap<String, UserCredentialsSettings.Credential> doInBackground(Void... paramVarArgs)
    {
      TreeMap localTreeMap = new TreeMap();
      KeyStore localKeyStore = KeyStore.getInstance();
      UserCredentialsSettings.Credential.Type[] arrayOfType = UserCredentialsSettings.Credential.Type.values();
      int k = arrayOfType.length;
      int i = 0;
      while (i < k)
      {
        UserCredentialsSettings.Credential.Type localType = arrayOfType[i];
        String[] arrayOfString = localKeyStore.list(localType.prefix);
        int m = arrayOfString.length;
        int j = 0;
        if (j < m)
        {
          String str = arrayOfString[j];
          if ((str.startsWith("profile_key_name_encrypt_")) || (str.startsWith("profile_key_name_decrypt_"))) {}
          for (;;)
          {
            j += 1;
            break;
            UserCredentialsSettings.Credential localCredential = (UserCredentialsSettings.Credential)localTreeMap.get(str);
            paramVarArgs = localCredential;
            if (localCredential == null)
            {
              paramVarArgs = new UserCredentialsSettings.Credential(str);
              localTreeMap.put(str, paramVarArgs);
            }
            paramVarArgs.storedTypes.add(localType);
          }
        }
        i += 1;
      }
      return localTreeMap;
    }
    
    protected void onPostExecute(SortedMap<String, UserCredentialsSettings.Credential> paramSortedMap)
    {
      UserCredentialsSettings.-get0(UserCredentialsSettings.this).setAdapter(new UserCredentialsSettings.CredentialAdapter(UserCredentialsSettings.this.getContext(), 2130969094, (UserCredentialsSettings.Credential[])paramSortedMap.values().toArray(new UserCredentialsSettings.Credential[0])));
    }
  }
  
  static class Credential
    implements Parcelable
  {
    public static final Parcelable.Creator<Credential> CREATOR = new Parcelable.Creator()
    {
      public UserCredentialsSettings.Credential createFromParcel(Parcel paramAnonymousParcel)
      {
        return new UserCredentialsSettings.Credential(paramAnonymousParcel);
      }
      
      public UserCredentialsSettings.Credential[] newArray(int paramAnonymousInt)
      {
        return new UserCredentialsSettings.Credential[paramAnonymousInt];
      }
    };
    final String alias;
    final EnumSet<Type> storedTypes = EnumSet.noneOf(Type.class);
    
    Credential(Parcel paramParcel)
    {
      this(paramParcel.readString());
      long l = paramParcel.readLong();
      paramParcel = Type.values();
      int i = 0;
      int j = paramParcel.length;
      while (i < j)
      {
        Object localObject = paramParcel[i];
        if ((1L << ((Type)localObject).ordinal() & l) != 0L) {
          this.storedTypes.add(localObject);
        }
        i += 1;
      }
    }
    
    Credential(String paramString)
    {
      this.alias = paramString;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeString(this.alias);
      long l = 0L;
      Iterator localIterator = this.storedTypes.iterator();
      while (localIterator.hasNext()) {
        l |= 1L << ((Type)localIterator.next()).ordinal();
      }
      paramParcel.writeLong(l);
    }
    
    static enum Type
    {
      CA_CERTIFICATE("CACERT_"),  USER_CERTIFICATE("USRCERT_"),  USER_PRIVATE_KEY("USRPKEY_"),  USER_SECRET_KEY("USRSKEY_");
      
      final String prefix;
      
      private Type(String paramString1)
      {
        this.prefix = paramString1;
      }
    }
  }
  
  private static class CredentialAdapter
    extends ArrayAdapter<UserCredentialsSettings.Credential>
  {
    public CredentialAdapter(Context paramContext, int paramInt, UserCredentialsSettings.Credential[] paramArrayOfCredential)
    {
      super(paramInt, paramArrayOfCredential);
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      int i = 0;
      View localView = paramView;
      if (paramView == null) {
        localView = LayoutInflater.from(getContext()).inflate(2130969094, paramViewGroup, false);
      }
      paramView = (UserCredentialsSettings.Credential)getItem(paramInt);
      ((TextView)localView.findViewById(2131362664)).setText(paramView.alias);
      paramViewGroup = localView.findViewById(2131362665);
      if (paramView.storedTypes.contains(UserCredentialsSettings.Credential.Type.USER_PRIVATE_KEY))
      {
        paramInt = 0;
        paramViewGroup.setVisibility(paramInt);
        paramViewGroup = localView.findViewById(2131362666);
        if (!paramView.storedTypes.contains(UserCredentialsSettings.Credential.Type.USER_CERTIFICATE)) {
          break label146;
        }
        paramInt = 0;
        label103:
        paramViewGroup.setVisibility(paramInt);
        paramViewGroup = localView.findViewById(2131362667);
        if (!paramView.storedTypes.contains(UserCredentialsSettings.Credential.Type.CA_CERTIFICATE)) {
          break label152;
        }
      }
      label146:
      label152:
      for (paramInt = i;; paramInt = 8)
      {
        paramViewGroup.setVisibility(paramInt);
        return localView;
        paramInt = 8;
        break;
        paramInt = 8;
        break label103;
      }
    }
  }
  
  public static class CredentialDialogFragment
    extends DialogFragment
  {
    private static final String ARG_CREDENTIAL = "credential";
    private static final String TAG = "CredentialDialogFragment";
    
    public static void show(Fragment paramFragment, UserCredentialsSettings.Credential paramCredential)
    {
      Bundle localBundle = new Bundle();
      localBundle.putParcelable("credential", paramCredential);
      if (paramFragment.getFragmentManager().findFragmentByTag("CredentialDialogFragment") == null)
      {
        paramCredential = new CredentialDialogFragment();
        paramCredential.setTargetFragment(paramFragment, -1);
        paramCredential.setArguments(localBundle);
        paramCredential.show(paramFragment.getFragmentManager(), "CredentialDialogFragment");
      }
    }
    
    public Dialog onCreateDialog(final Bundle paramBundle)
    {
      paramBundle = (UserCredentialsSettings.Credential)getArguments().getParcelable("credential");
      Object localObject = getActivity().getLayoutInflater().inflate(2130969095, null);
      ((ViewGroup)((View)localObject).findViewById(2131362668)).addView(new UserCredentialsSettings.CredentialAdapter(getActivity(), 2130969094, new UserCredentialsSettings.Credential[] { paramBundle }).getView(0, null, null));
      UserManager localUserManager = (UserManager)getContext().getSystemService("user");
      localObject = new AlertDialog.Builder(getActivity()).setView((View)localObject).setTitle(2131692896).setPositiveButton(2131690998, null);
      final int i = UserHandle.myUserId();
      if (!RestrictedLockUtils.hasBaseUserRestriction(getContext(), "no_config_credentials", i)) {
        ((AlertDialog.Builder)localObject).setNegativeButton(2131692887, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            RestrictedLockUtils.EnforcedAdmin localEnforcedAdmin = RestrictedLockUtils.checkIfRestrictionEnforced(UserCredentialsSettings.CredentialDialogFragment.this.getContext(), "no_config_credentials", i);
            if (localEnforcedAdmin != null) {
              RestrictedLockUtils.sendShowAdminSupportDetailsIntent(UserCredentialsSettings.CredentialDialogFragment.this.getContext(), localEnforcedAdmin);
            }
            for (;;)
            {
              paramAnonymousDialogInterface.dismiss();
              return;
              new UserCredentialsSettings.CredentialDialogFragment.RemoveCredentialsTask(UserCredentialsSettings.CredentialDialogFragment.this, UserCredentialsSettings.CredentialDialogFragment.this.getContext(), UserCredentialsSettings.CredentialDialogFragment.this.getTargetFragment()).execute(new String[] { paramBundle.alias });
            }
          }
        });
      }
      return ((AlertDialog.Builder)localObject).create();
    }
    
    private class RemoveCredentialsTask
      extends AsyncTask<String, Void, Void>
    {
      private Context context;
      private Fragment targetFragment;
      
      public RemoveCredentialsTask(Context paramContext, Fragment paramFragment)
      {
        this.context = paramContext;
        this.targetFragment = paramFragment;
      }
      
      /* Error */
      protected Void doInBackground(String... paramVarArgs)
      {
        // Byte code:
        //   0: aload_0
        //   1: getfield 21	com/android/settings/UserCredentialsSettings$CredentialDialogFragment$RemoveCredentialsTask:this$1	Lcom/android/settings/UserCredentialsSettings$CredentialDialogFragment;
        //   4: invokevirtual 44	com/android/settings/UserCredentialsSettings$CredentialDialogFragment:getContext	()Landroid/content/Context;
        //   7: invokestatic 50	android/security/KeyChain:bind	(Landroid/content/Context;)Landroid/security/KeyChain$KeyChainConnection;
        //   10: astore 4
        //   12: aload 4
        //   14: invokevirtual 56	android/security/KeyChain$KeyChainConnection:getService	()Landroid/security/IKeyChainService;
        //   17: astore 5
        //   19: iconst_0
        //   20: istore_2
        //   21: aload_1
        //   22: arraylength
        //   23: istore_3
        //   24: iload_2
        //   25: iload_3
        //   26: if_icmpge +21 -> 47
        //   29: aload 5
        //   31: aload_1
        //   32: iload_2
        //   33: aaload
        //   34: invokeinterface 62 2 0
        //   39: pop
        //   40: iload_2
        //   41: iconst_1
        //   42: iadd
        //   43: istore_2
        //   44: goto -20 -> 24
        //   47: aload 4
        //   49: invokevirtual 65	android/security/KeyChain$KeyChainConnection:close	()V
        //   52: aconst_null
        //   53: areturn
        //   54: astore_1
        //   55: ldc 66
        //   57: ldc 68
        //   59: aload_1
        //   60: invokestatic 74	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //   63: pop
        //   64: aload 4
        //   66: invokevirtual 65	android/security/KeyChain$KeyChainConnection:close	()V
        //   69: goto -17 -> 52
        //   72: astore_1
        //   73: ldc 66
        //   75: ldc 76
        //   77: aload_1
        //   78: invokestatic 74	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //   81: pop
        //   82: goto -30 -> 52
        //   85: astore_1
        //   86: aload 4
        //   88: invokevirtual 65	android/security/KeyChain$KeyChainConnection:close	()V
        //   91: aload_1
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	RemoveCredentialsTask
        //   0	93	1	paramVarArgs	String[]
        //   20	24	2	i	int
        //   23	4	3	j	int
        //   10	77	4	localKeyChainConnection	android.security.KeyChain.KeyChainConnection
        //   17	13	5	localIKeyChainService	android.security.IKeyChainService
        // Exception table:
        //   from	to	target	type
        //   12	19	54	android/os/RemoteException
        //   21	24	54	android/os/RemoteException
        //   29	40	54	android/os/RemoteException
        //   0	12	72	java/lang/InterruptedException
        //   47	52	72	java/lang/InterruptedException
        //   64	69	72	java/lang/InterruptedException
        //   86	93	72	java/lang/InterruptedException
        //   12	19	85	finally
        //   21	24	85	finally
        //   29	40	85	finally
        //   55	64	85	finally
      }
      
      protected void onPostExecute(Void paramVoid)
      {
        if ((this.targetFragment instanceof UserCredentialsSettings)) {
          ((UserCredentialsSettings)this.targetFragment).refreshItems();
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\UserCredentialsSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */