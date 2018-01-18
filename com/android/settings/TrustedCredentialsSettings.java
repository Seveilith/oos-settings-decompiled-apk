package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ParceledListSlice;
import android.content.pm.UserInfo;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.net.http.SslCertificate;
import android.net.http.SslCertificate.DName;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.security.IKeyChainService;
import android.security.KeyChain;
import android.security.KeyChain.KeyChainConnection;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import com.android.internal.R.styleable;
import com.android.internal.app.UnlaunchableAppActivity;
import com.android.internal.util.ParcelableString;
import com.android.internal.widget.LockPatternUtils;
import com.oneplus.settings.utils.OPUtils;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.IntConsumer;

public class TrustedCredentialsSettings
  extends OptionsMenuFragment
  implements TrustedCredentialsDialogBuilder.DelegateInterface
{
  public static final String ARG_SHOW_NEW_FOR_USER = "ARG_SHOW_NEW_FOR_USER";
  private static final int REQUEST_CONFIRM_CREDENTIALS = 1;
  private static final String SAVED_CONFIRMED_CREDENTIAL_USERS = "ConfirmedCredentialUsers";
  private static final String SAVED_CONFIRMING_CREDENTIAL_USER = "ConfirmingCredentialUser";
  private static final String TAG = "TrustedCredentialsSettings";
  private static final String USER_ACTION = "com.android.settings.TRUSTED_CREDENTIALS_USER";
  private Set<TrustedCredentialsSettings.AdapterData.AliasLoader> mAliasLoaders = new ArraySet(2);
  private AliasOperation mAliasOperation;
  private ArraySet<Integer> mConfirmedCredentialUsers;
  private IntConsumer mConfirmingCredentialListener;
  private int mConfirmingCredentialUser;
  private ArrayList<GroupAdapter> mGroupAdapters = new ArrayList(2);
  private final SparseArray<KeyChain.KeyChainConnection> mKeyChainConnectionByProfileId = new SparseArray();
  private KeyguardManager mKeyguardManager;
  private TabHost mTabHost;
  private int mTrustAllCaUserId;
  private UserManager mUserManager;
  private BroadcastReceiver mWorkProfileChangedReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if (("android.intent.action.MANAGED_PROFILE_AVAILABLE".equals(paramAnonymousContext)) || ("android.intent.action.MANAGED_PROFILE_UNAVAILABLE".equals(paramAnonymousContext)) || ("android.intent.action.MANAGED_PROFILE_UNLOCKED".equals(paramAnonymousContext)))
      {
        paramAnonymousContext = TrustedCredentialsSettings.-get1(TrustedCredentialsSettings.this).iterator();
        while (paramAnonymousContext.hasNext()) {
          ((TrustedCredentialsSettings.GroupAdapter)paramAnonymousContext.next()).load();
        }
      }
    }
  };
  
  private void addTab(Tab paramTab)
  {
    int j = 0;
    Object localObject = this.mTabHost.newTabSpec(Tab.-get6(paramTab)).setIndicator(getActivity().getString(Tab.-get2(paramTab))).setContent(Tab.-get7(paramTab));
    this.mTabHost.addTab((TabHost.TabSpec)localObject);
    int k = this.mUserManager.getUserProfiles().size();
    localObject = new GroupAdapter(paramTab, null);
    this.mGroupAdapters.add(localObject);
    int i = k;
    if (OPUtils.hasMultiAppProfiles(this.mUserManager)) {
      i = k - 1;
    }
    if (i == 1)
    {
      localObject = ((GroupAdapter)localObject).getChildAdapter(0);
      ((ChildAdapter)localObject).setContainerViewId(Tab.-get3(paramTab));
      ((ChildAdapter)localObject).prepare();
    }
    label146:
    label229:
    do
    {
      return;
      if (i == 2)
      {
        if (((GroupAdapter)localObject).getUserInfoByGroup(1).isManagedProfile())
        {
          i = 1;
          if (i != 1) {
            break label229;
          }
        }
        for (;;)
        {
          ChildAdapter localChildAdapter = ((GroupAdapter)localObject).getChildAdapter(j);
          localChildAdapter.setContainerViewId(Tab.-get3(paramTab));
          localChildAdapter.showHeader(true);
          localChildAdapter.prepare();
          if (OPUtils.hasMultiAppProfiles(this.mUserManager)) {
            break;
          }
          localObject = ((GroupAdapter)localObject).getChildAdapter(i);
          ((ChildAdapter)localObject).setContainerViewId(Tab.-get8(paramTab));
          ((ChildAdapter)localObject).showHeader(true);
          ((ChildAdapter)localObject).showDivider(true);
          ((ChildAdapter)localObject).prepare();
          return;
          i = 0;
          break label146;
          j = 1;
        }
      }
    } while (i < 3);
    ((GroupAdapter)localObject).setExpandableListView((ExpandableListView)this.mTabHost.findViewById(Tab.-get1(paramTab)));
  }
  
  private void closeKeyChainConnections()
  {
    int j = this.mKeyChainConnectionByProfileId.size();
    int i = 0;
    while (i < j)
    {
      ((KeyChain.KeyChainConnection)this.mKeyChainConnectionByProfileId.valueAt(i)).close();
      i += 1;
    }
    this.mKeyChainConnectionByProfileId.clear();
  }
  
  private boolean isTrustAllCaCertModeInProgress()
  {
    return this.mTrustAllCaUserId != 55536;
  }
  
  private void showCertDialog(CertHolder paramCertHolder)
  {
    new TrustedCredentialsDialogBuilder(getActivity(), this).setCertHolder(paramCertHolder).show();
  }
  
  private void showTrustAllCaDialog(List<CertHolder> paramList)
  {
    paramList = (CertHolder[])paramList.toArray(new CertHolder[paramList.size()]);
    new TrustedCredentialsDialogBuilder(getActivity(), this).setCertHolders(paramList).setOnDismissListener(new DialogInterface.OnDismissListener()
    {
      public void onDismiss(DialogInterface paramAnonymousDialogInterface)
      {
        TrustedCredentialsSettings.this.getActivity().getIntent().removeExtra("ARG_SHOW_NEW_FOR_USER");
        TrustedCredentialsSettings.-set1(TrustedCredentialsSettings.this, 55536);
      }
    }).show();
  }
  
  private boolean startConfirmCredential(int paramInt)
  {
    Intent localIntent = this.mKeyguardManager.createConfirmDeviceCredentialIntent(null, null, paramInt);
    if (localIntent == null) {
      return false;
    }
    this.mConfirmingCredentialUser = paramInt;
    startActivityForResult(localIntent, 1);
    return true;
  }
  
  protected int getMetricsCategory()
  {
    return 92;
  }
  
  public List<X509Certificate> getX509CertsFromCertHolder(CertHolder paramCertHolder)
  {
    localObject = null;
    for (;;)
    {
      IKeyChainService localIKeyChainService;
      List localList;
      ArrayList localArrayList;
      int i;
      try
      {
        localIKeyChainService = ((KeyChain.KeyChainConnection)this.mKeyChainConnectionByProfileId.get(paramCertHolder.mProfileId)).getService();
        localList = localIKeyChainService.getCaCertificateChainAliases(CertHolder.-get1(paramCertHolder), true);
        int j = localList.size();
        localArrayList = new ArrayList(j);
        i = 0;
        if (i >= j) {}
      }
      catch (RemoteException localRemoteException1) {}
      try
      {
        localArrayList.add(KeyChain.toCertificate(localIKeyChainService.getEncodedCaCertificate((String)localList.get(i), true)));
        i += 1;
      }
      catch (RemoteException localRemoteException3)
      {
        for (;;)
        {
          localObject = localRemoteException1;
          RemoteException localRemoteException2 = localRemoteException3;
        }
      }
    }
    return localArrayList;
    Log.e("TrustedCredentialsSettings", "RemoteException while retrieving certificate chain for root " + CertHolder.-get1(paramCertHolder), localRemoteException1);
    return (List<X509Certificate>)localObject;
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt1 == 1)
    {
      paramInt1 = this.mConfirmingCredentialUser;
      paramIntent = this.mConfirmingCredentialListener;
      this.mConfirmingCredentialUser = 55536;
      this.mConfirmingCredentialListener = null;
      if (paramInt2 == -1)
      {
        this.mConfirmedCredentialUsers.add(Integer.valueOf(paramInt1));
        if (paramIntent != null) {
          paramIntent.accept(paramInt1);
        }
      }
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mUserManager = ((UserManager)getActivity().getSystemService("user"));
    this.mKeyguardManager = ((KeyguardManager)getActivity().getSystemService("keyguard"));
    this.mTrustAllCaUserId = getActivity().getIntent().getIntExtra("ARG_SHOW_NEW_FOR_USER", 55536);
    this.mConfirmedCredentialUsers = new ArraySet(2);
    this.mConfirmingCredentialUser = 55536;
    if (paramBundle != null)
    {
      this.mConfirmingCredentialUser = paramBundle.getInt("ConfirmingCredentialUser", 55536);
      paramBundle = paramBundle.getIntegerArrayList("ConfirmedCredentialUsers");
      if (paramBundle != null) {
        this.mConfirmedCredentialUsers.addAll(paramBundle);
      }
    }
    this.mConfirmingCredentialListener = null;
    paramBundle = new IntentFilter();
    paramBundle.addAction("android.intent.action.MANAGED_PROFILE_AVAILABLE");
    paramBundle.addAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
    paramBundle.addAction("android.intent.action.MANAGED_PROFILE_UNLOCKED");
    getActivity().registerReceiver(this.mWorkProfileChangedReceiver, paramBundle);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mTabHost = ((TabHost)paramLayoutInflater.inflate(2130969084, paramViewGroup, false));
    this.mTabHost.setup();
    addTab(Tab.SYSTEM);
    addTab(Tab.USER);
    if ((getActivity().getIntent() != null) && ("com.android.settings.TRUSTED_CREDENTIALS_USER".equals(getActivity().getIntent().getAction()))) {
      this.mTabHost.setCurrentTabByTag(Tab.-get6(Tab.USER));
    }
    return this.mTabHost;
  }
  
  public void onDestroy()
  {
    getActivity().unregisterReceiver(this.mWorkProfileChangedReceiver);
    Iterator localIterator = this.mAliasLoaders.iterator();
    while (localIterator.hasNext()) {
      ((TrustedCredentialsSettings.AdapterData.AliasLoader)localIterator.next()).cancel(true);
    }
    this.mAliasLoaders.clear();
    this.mGroupAdapters.clear();
    if (this.mAliasOperation != null)
    {
      this.mAliasOperation.cancel(true);
      this.mAliasOperation = null;
    }
    closeKeyChainConnections();
    super.onDestroy();
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putIntegerArrayList("ConfirmedCredentialUsers", new ArrayList(this.mConfirmedCredentialUsers));
    paramBundle.putInt("ConfirmingCredentialUser", this.mConfirmingCredentialUser);
  }
  
  public void removeOrInstallCert(CertHolder paramCertHolder)
  {
    new AliasOperation(paramCertHolder, null).execute(new Void[0]);
  }
  
  public boolean startConfirmCredentialIfNotConfirmed(int paramInt, IntConsumer paramIntConsumer)
  {
    if (this.mConfirmedCredentialUsers.contains(Integer.valueOf(paramInt))) {
      return false;
    }
    boolean bool = startConfirmCredential(paramInt);
    if (bool) {
      this.mConfirmingCredentialListener = paramIntConsumer;
    }
    return bool;
  }
  
  private class AdapterData
  {
    private final TrustedCredentialsSettings.GroupAdapter mAdapter;
    private final SparseArray<List<TrustedCredentialsSettings.CertHolder>> mCertHoldersByUserId = new SparseArray();
    private final TrustedCredentialsSettings.Tab mTab;
    
    private AdapterData(TrustedCredentialsSettings.Tab paramTab, TrustedCredentialsSettings.GroupAdapter paramGroupAdapter)
    {
      this.mAdapter = paramGroupAdapter;
      this.mTab = paramTab;
    }
    
    public void remove(TrustedCredentialsSettings.CertHolder paramCertHolder)
    {
      if (this.mCertHoldersByUserId != null)
      {
        List localList = (List)this.mCertHoldersByUserId.get(paramCertHolder.mProfileId);
        if (localList != null) {
          localList.remove(paramCertHolder);
        }
      }
    }
    
    private class AliasLoader
      extends AsyncTask<Void, Integer, SparseArray<List<TrustedCredentialsSettings.CertHolder>>>
    {
      private View mContentView;
      private Context mContext = TrustedCredentialsSettings.this.getActivity();
      private ProgressBar mProgressBar;
      
      public AliasLoader()
      {
        TrustedCredentialsSettings.-get0(TrustedCredentialsSettings.this).add(this);
        Iterator localIterator = TrustedCredentialsSettings.-get5(TrustedCredentialsSettings.this).getUserProfiles().iterator();
        while (localIterator.hasNext())
        {
          UserHandle localUserHandle = (UserHandle)localIterator.next();
          TrustedCredentialsSettings.AdapterData.-get1(TrustedCredentialsSettings.AdapterData.this).put(localUserHandle.getIdentifier(), new ArrayList());
        }
      }
      
      private boolean isUserTabAndTrustAllCertMode()
      {
        boolean bool2 = false;
        boolean bool1 = bool2;
        if (TrustedCredentialsSettings.-wrap0(TrustedCredentialsSettings.this))
        {
          bool1 = bool2;
          if (TrustedCredentialsSettings.AdapterData.-get2(TrustedCredentialsSettings.AdapterData.this) == TrustedCredentialsSettings.Tab.USER) {
            bool1 = true;
          }
        }
        return bool1;
      }
      
      private boolean shouldSkipProfile(UserHandle paramUserHandle)
      {
        boolean bool2 = true;
        boolean bool1 = bool2;
        if (!TrustedCredentialsSettings.-get5(TrustedCredentialsSettings.this).isQuietModeEnabled(paramUserHandle))
        {
          bool1 = bool2;
          if (TrustedCredentialsSettings.-get5(TrustedCredentialsSettings.this).isUserUnlocked(paramUserHandle.getIdentifier())) {
            bool1 = false;
          }
        }
        return bool1;
      }
      
      private void showTrustAllCaDialogIfNeeded()
      {
        if (!isUserTabAndTrustAllCertMode()) {
          return;
        }
        Object localObject = (List)TrustedCredentialsSettings.AdapterData.-get1(TrustedCredentialsSettings.AdapterData.this).get(TrustedCredentialsSettings.-get4(TrustedCredentialsSettings.this));
        if (localObject == null) {
          return;
        }
        ArrayList localArrayList = new ArrayList();
        DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)this.mContext.getSystemService(DevicePolicyManager.class);
        localObject = ((Iterable)localObject).iterator();
        while (((Iterator)localObject).hasNext())
        {
          TrustedCredentialsSettings.CertHolder localCertHolder = (TrustedCredentialsSettings.CertHolder)((Iterator)localObject).next();
          if ((localCertHolder != null) && (!localDevicePolicyManager.isCaCertApproved(TrustedCredentialsSettings.CertHolder.-get1(localCertHolder), TrustedCredentialsSettings.-get4(TrustedCredentialsSettings.this)))) {
            localArrayList.add(localCertHolder);
          }
        }
        if (localArrayList.size() == 0)
        {
          Log.w("TrustedCredentialsSettings", "no cert is pending approval for user " + TrustedCredentialsSettings.-get4(TrustedCredentialsSettings.this));
          return;
        }
        TrustedCredentialsSettings.-wrap3(TrustedCredentialsSettings.this, localArrayList);
      }
      
      protected SparseArray<List<TrustedCredentialsSettings.CertHolder>> doInBackground(Void... paramVarArgs)
      {
        paramVarArgs = new SparseArray();
        for (;;)
        {
          int k;
          int i;
          try
          {
            localList = TrustedCredentialsSettings.-get5(TrustedCredentialsSettings.this).getUserProfiles();
            n = localList.size();
            localSparseArray = new SparseArray(n);
            j = 0;
            k = 0;
            i = 0;
            if (i < n)
            {
              localObject1 = (UserHandle)localList.get(i);
              m = ((UserHandle)localObject1).getIdentifier();
              if (shouldSkipProfile((UserHandle)localObject1)) {
                break label510;
              }
              localObject1 = KeyChain.bindAsUser(this.mContext, (UserHandle)localObject1);
              TrustedCredentialsSettings.-get2(TrustedCredentialsSettings.this).put(m, localObject1);
              localObject1 = ((KeyChain.KeyChainConnection)localObject1).getService();
              localObject1 = TrustedCredentialsSettings.Tab.-wrap1(TrustedCredentialsSettings.AdapterData.-get2(TrustedCredentialsSettings.AdapterData.this), (IKeyChainService)localObject1);
              if (isCancelled()) {
                return new SparseArray();
              }
              j += ((List)localObject1).size();
              localSparseArray.put(m, localObject1);
            }
          }
          catch (RemoteException paramVarArgs)
          {
            List localList;
            int n;
            SparseArray localSparseArray;
            int j;
            Object localObject1;
            Log.e("TrustedCredentialsSettings", "Remote exception while loading aliases.", paramVarArgs);
            return new SparseArray();
            int m = 0;
            i = k;
            k = m;
            if (k < n)
            {
              Object localObject2 = (UserHandle)localList.get(k);
              int i1 = ((UserHandle)localObject2).getIdentifier();
              localObject1 = (List)localSparseArray.get(i1);
              if (isCancelled()) {
                return new SparseArray();
              }
              Object localObject3 = (KeyChain.KeyChainConnection)TrustedCredentialsSettings.-get2(TrustedCredentialsSettings.this).get(i1);
              if ((shouldSkipProfile((UserHandle)localObject2)) || (localObject1 == null))
              {
                paramVarArgs.put(i1, new ArrayList(0));
                break label517;
              }
              if (localObject3 == null) {
                continue;
              }
              localObject2 = ((KeyChain.KeyChainConnection)localObject3).getService();
              localObject3 = new ArrayList(j);
              int i2 = ((List)localObject1).size();
              m = 0;
              if (m < i2)
              {
                String str = ((ParcelableString)((List)localObject1).get(m)).string;
                X509Certificate localX509Certificate = KeyChain.toCertificate(((IKeyChainService)localObject2).getEncodedCaCertificate(str, true));
                ((List)localObject3).add(new TrustedCredentialsSettings.CertHolder((IKeyChainService)localObject2, TrustedCredentialsSettings.AdapterData.-get0(TrustedCredentialsSettings.AdapterData.this), TrustedCredentialsSettings.AdapterData.-get2(TrustedCredentialsSettings.AdapterData.this), str, localX509Certificate, i1, null));
                i += 1;
                publishProgress(new Integer[] { Integer.valueOf(i), Integer.valueOf(j) });
                m += 1;
                continue;
              }
              Collections.sort((List)localObject3);
              paramVarArgs.put(i1, localObject3);
              break label517;
            }
          }
          catch (InterruptedException paramVarArgs)
          {
            Log.e("TrustedCredentialsSettings", "InterruptedException while loading aliases.", paramVarArgs);
            return new SparseArray();
            return paramVarArgs;
          }
          catch (AssertionError paramVarArgs)
          {
            Log.e("TrustedCredentialsSettings", "AssertionError while loading aliases.", paramVarArgs);
            return new SparseArray();
          }
          label510:
          i += 1;
          continue;
          label517:
          k += 1;
        }
      }
      
      protected void onPostExecute(SparseArray<List<TrustedCredentialsSettings.CertHolder>> paramSparseArray)
      {
        TrustedCredentialsSettings.AdapterData.-get1(TrustedCredentialsSettings.AdapterData.this).clear();
        int j = paramSparseArray.size();
        int i = 0;
        while (i < j)
        {
          TrustedCredentialsSettings.AdapterData.-get1(TrustedCredentialsSettings.AdapterData.this).put(paramSparseArray.keyAt(i), (List)paramSparseArray.valueAt(i));
          i += 1;
        }
        TrustedCredentialsSettings.AdapterData.-get0(TrustedCredentialsSettings.AdapterData.this).notifyDataSetChanged();
        this.mProgressBar.setVisibility(8);
        this.mContentView.setVisibility(0);
        this.mProgressBar.setProgress(0);
        TrustedCredentialsSettings.-get0(TrustedCredentialsSettings.this).remove(this);
        showTrustAllCaDialogIfNeeded();
      }
      
      protected void onPreExecute()
      {
        FrameLayout localFrameLayout = TrustedCredentialsSettings.-get3(TrustedCredentialsSettings.this).getTabContentView();
        this.mProgressBar = ((ProgressBar)localFrameLayout.findViewById(TrustedCredentialsSettings.Tab.-get4(TrustedCredentialsSettings.AdapterData.-get2(TrustedCredentialsSettings.AdapterData.this))));
        this.mContentView = localFrameLayout.findViewById(TrustedCredentialsSettings.Tab.-get0(TrustedCredentialsSettings.AdapterData.-get2(TrustedCredentialsSettings.AdapterData.this)));
        this.mProgressBar.setVisibility(0);
        this.mContentView.setVisibility(8);
      }
      
      protected void onProgressUpdate(Integer... paramVarArgs)
      {
        int i = paramVarArgs[0].intValue();
        int j = paramVarArgs[1].intValue();
        if (j != this.mProgressBar.getMax()) {
          this.mProgressBar.setMax(j);
        }
        this.mProgressBar.setProgress(i);
      }
    }
  }
  
  private class AliasOperation
    extends AsyncTask<Void, Void, Boolean>
  {
    private final TrustedCredentialsSettings.CertHolder mCertHolder;
    
    private AliasOperation(TrustedCredentialsSettings.CertHolder paramCertHolder)
    {
      this.mCertHolder = paramCertHolder;
      TrustedCredentialsSettings.-set0(TrustedCredentialsSettings.this, this);
    }
    
    protected Boolean doInBackground(Void... paramVarArgs)
    {
      try
      {
        paramVarArgs = ((KeyChain.KeyChainConnection)TrustedCredentialsSettings.-get2(TrustedCredentialsSettings.this).get(this.mCertHolder.mProfileId)).getService();
        if (TrustedCredentialsSettings.CertHolder.-get2(this.mCertHolder))
        {
          paramVarArgs.installCaCertificate(TrustedCredentialsSettings.CertHolder.-get6(this.mCertHolder).getEncoded());
          return Boolean.valueOf(true);
        }
        boolean bool = paramVarArgs.deleteCaCertificate(TrustedCredentialsSettings.CertHolder.-get1(this.mCertHolder));
        return Boolean.valueOf(bool);
      }
      catch (CertificateEncodingException|SecurityException|IllegalStateException|RemoteException paramVarArgs)
      {
        Log.w("TrustedCredentialsSettings", "Error while toggling alias " + TrustedCredentialsSettings.CertHolder.-get1(this.mCertHolder), paramVarArgs);
      }
      return Boolean.valueOf(false);
    }
    
    protected void onPostExecute(Boolean paramBoolean)
    {
      boolean bool;
      if (paramBoolean.booleanValue()) {
        if (TrustedCredentialsSettings.Tab.-get5(TrustedCredentialsSettings.CertHolder.-get5(this.mCertHolder)))
        {
          paramBoolean = this.mCertHolder;
          if (TrustedCredentialsSettings.CertHolder.-get2(this.mCertHolder))
          {
            bool = false;
            TrustedCredentialsSettings.CertHolder.-set0(paramBoolean, bool);
            label43:
            TrustedCredentialsSettings.CertHolder.-get0(this.mCertHolder).notifyDataSetChanged();
          }
        }
      }
      for (;;)
      {
        TrustedCredentialsSettings.-set0(TrustedCredentialsSettings.this, null);
        return;
        bool = true;
        break;
        TrustedCredentialsSettings.CertHolder.-get0(this.mCertHolder).remove(this.mCertHolder);
        break label43;
        TrustedCredentialsSettings.CertHolder.-get0(this.mCertHolder).load();
      }
    }
  }
  
  static class CertHolder
    implements Comparable<CertHolder>
  {
    private final TrustedCredentialsSettings.GroupAdapter mAdapter;
    private final String mAlias;
    private boolean mDeleted;
    public int mProfileId;
    private final IKeyChainService mService;
    private final SslCertificate mSslCert;
    private final String mSubjectPrimary;
    private final String mSubjectSecondary;
    private final TrustedCredentialsSettings.Tab mTab;
    private final X509Certificate mX509Cert;
    
    private CertHolder(IKeyChainService paramIKeyChainService, TrustedCredentialsSettings.GroupAdapter paramGroupAdapter, TrustedCredentialsSettings.Tab paramTab, String paramString, X509Certificate paramX509Certificate, int paramInt)
    {
      this.mProfileId = paramInt;
      this.mService = paramIKeyChainService;
      this.mAdapter = paramGroupAdapter;
      this.mTab = paramTab;
      this.mAlias = paramString;
      this.mX509Cert = paramX509Certificate;
      this.mSslCert = new SslCertificate(paramX509Certificate);
      paramIKeyChainService = this.mSslCert.getIssuedTo().getCName();
      paramGroupAdapter = this.mSslCert.getIssuedTo().getOName();
      paramTab = this.mSslCert.getIssuedTo().getUName();
      if (!paramGroupAdapter.isEmpty()) {
        if (!paramIKeyChainService.isEmpty())
        {
          this.mSubjectPrimary = paramGroupAdapter;
          this.mSubjectSecondary = paramIKeyChainService;
        }
      }
      for (;;)
      {
        try
        {
          this.mDeleted = TrustedCredentialsSettings.Tab.-wrap0(this.mTab, this.mService, this.mAlias);
          return;
        }
        catch (RemoteException paramIKeyChainService)
        {
          Log.e("TrustedCredentialsSettings", "Remote exception while checking if alias " + this.mAlias + " is deleted.", paramIKeyChainService);
          this.mDeleted = false;
        }
        this.mSubjectPrimary = paramGroupAdapter;
        this.mSubjectSecondary = paramTab;
        continue;
        if (!paramIKeyChainService.isEmpty())
        {
          this.mSubjectPrimary = paramIKeyChainService;
          this.mSubjectSecondary = "";
        }
        else
        {
          this.mSubjectPrimary = this.mSslCert.getIssuedTo().getDName();
          this.mSubjectSecondary = "";
        }
      }
    }
    
    public int compareTo(CertHolder paramCertHolder)
    {
      int i = this.mSubjectPrimary.compareToIgnoreCase(paramCertHolder.mSubjectPrimary);
      if (i != 0) {
        return i;
      }
      return this.mSubjectSecondary.compareToIgnoreCase(paramCertHolder.mSubjectSecondary);
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof CertHolder)) {
        return false;
      }
      paramObject = (CertHolder)paramObject;
      return this.mAlias.equals(((CertHolder)paramObject).mAlias);
    }
    
    public String getAlias()
    {
      return this.mAlias;
    }
    
    public int getUserId()
    {
      return this.mProfileId;
    }
    
    public int hashCode()
    {
      return this.mAlias.hashCode();
    }
    
    public boolean isDeleted()
    {
      return this.mDeleted;
    }
    
    public boolean isSystemCert()
    {
      return this.mTab == TrustedCredentialsSettings.Tab.SYSTEM;
    }
  }
  
  private class ChildAdapter
    extends BaseAdapter
    implements View.OnClickListener, AdapterView.OnItemClickListener
  {
    private final int[] EMPTY_STATE_SET = new int[0];
    private final int[] GROUP_EXPANDED_STATE_SET = { 16842920 };
    private final LinearLayout.LayoutParams HIDE_LAYOUT_PARAMS = new LinearLayout.LayoutParams(-1, -2);
    private final LinearLayout.LayoutParams SHOW_LAYOUT_PARAMS = new LinearLayout.LayoutParams(-1, -1, 1.0F);
    private LinearLayout mContainerView;
    private final int mGroupPosition;
    private ViewGroup mHeaderView;
    private ImageView mIndicatorView;
    private boolean mIsListExpanded = true;
    private ListView mListView;
    private final DataSetObserver mObserver = new DataSetObserver()
    {
      public void onChanged()
      {
        super.onChanged();
        TrustedCredentialsSettings.ChildAdapter.-wrap0(TrustedCredentialsSettings.ChildAdapter.this);
      }
      
      public void onInvalidated()
      {
        super.onInvalidated();
        TrustedCredentialsSettings.ChildAdapter.-wrap1(TrustedCredentialsSettings.ChildAdapter.this);
      }
    };
    private final TrustedCredentialsSettings.GroupAdapter mParent;
    
    private ChildAdapter(TrustedCredentialsSettings.GroupAdapter paramGroupAdapter, int paramInt)
    {
      this.mParent = paramGroupAdapter;
      this.mGroupPosition = paramInt;
      this.mParent.registerDataSetObserver(this.mObserver);
    }
    
    private boolean checkGroupExpandableAndStartWarningActivity()
    {
      return this.mParent.checkGroupExpandableAndStartWarningActivity(this.mGroupPosition);
    }
    
    private Drawable getGroupIndicator()
    {
      TypedArray localTypedArray = TrustedCredentialsSettings.this.getActivity().obtainStyledAttributes(null, R.styleable.ExpandableListView, 16842863, 0);
      Drawable localDrawable = localTypedArray.getDrawable(0);
      localTypedArray.recycle();
      return localDrawable;
    }
    
    private void refreshViews()
    {
      Object localObject2 = this.mIndicatorView;
      int i;
      if (this.mIsListExpanded)
      {
        localObject1 = this.GROUP_EXPANDED_STATE_SET;
        ((ImageView)localObject2).setImageState((int[])localObject1, false);
        localObject1 = this.mListView;
        if (!this.mIsListExpanded) {
          break label73;
        }
        i = 0;
        label37:
        ((ListView)localObject1).setVisibility(i);
        localObject2 = this.mContainerView;
        if (!this.mIsListExpanded) {
          break label79;
        }
      }
      label73:
      label79:
      for (Object localObject1 = this.SHOW_LAYOUT_PARAMS;; localObject1 = this.HIDE_LAYOUT_PARAMS)
      {
        ((LinearLayout)localObject2).setLayoutParams((ViewGroup.LayoutParams)localObject1);
        return;
        localObject1 = this.EMPTY_STATE_SET;
        break;
        i = 8;
        break label37;
      }
    }
    
    public int getCount()
    {
      return this.mParent.getChildrenCount(this.mGroupPosition);
    }
    
    public TrustedCredentialsSettings.CertHolder getItem(int paramInt)
    {
      return this.mParent.getChild(this.mGroupPosition, paramInt);
    }
    
    public long getItemId(int paramInt)
    {
      return this.mParent.getChildId(this.mGroupPosition, paramInt);
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      return this.mParent.getChildView(this.mGroupPosition, paramInt, false, paramView, paramViewGroup);
    }
    
    public void notifyDataSetChanged()
    {
      this.mParent.notifyDataSetChanged();
    }
    
    public void notifyDataSetInvalidated()
    {
      this.mParent.notifyDataSetInvalidated();
    }
    
    public void onClick(View paramView)
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (checkGroupExpandableAndStartWarningActivity()) {
        if (!this.mIsListExpanded) {
          break label30;
        }
      }
      label30:
      for (bool1 = bool2;; bool1 = true)
      {
        this.mIsListExpanded = bool1;
        refreshViews();
        return;
      }
    }
    
    public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
    {
      TrustedCredentialsSettings.-wrap2(TrustedCredentialsSettings.this, getItem(paramInt));
    }
    
    public void prepare()
    {
      this.mIsListExpanded = this.mParent.checkGroupExpandableAndStartWarningActivity(this.mGroupPosition, false);
      refreshViews();
    }
    
    public void setContainerViewId(int paramInt)
    {
      this.mContainerView = ((LinearLayout)TrustedCredentialsSettings.-get3(TrustedCredentialsSettings.this).findViewById(paramInt));
      this.mContainerView.setVisibility(0);
      this.mListView = ((ListView)this.mContainerView.findViewById(2131362621));
      this.mListView.setAdapter(this);
      this.mListView.setOnItemClickListener(this);
      this.mHeaderView = ((ViewGroup)this.mContainerView.findViewById(2131362617));
      this.mHeaderView.setOnClickListener(this);
      this.mIndicatorView = ((ImageView)this.mHeaderView.findViewById(2131362619));
      this.mIndicatorView.setImageDrawable(getGroupIndicator());
      FrameLayout localFrameLayout = (FrameLayout)this.mHeaderView.findViewById(2131362620);
      localFrameLayout.addView(this.mParent.getGroupView(this.mGroupPosition, true, null, localFrameLayout));
    }
    
    public void showDivider(boolean paramBoolean)
    {
      View localView = this.mHeaderView.findViewById(2131362618);
      if (paramBoolean) {}
      for (int i = 0;; i = 8)
      {
        localView.setVisibility(i);
        return;
      }
    }
    
    public void showHeader(boolean paramBoolean)
    {
      ViewGroup localViewGroup = this.mHeaderView;
      if (paramBoolean) {}
      for (int i = 0;; i = 8)
      {
        localViewGroup.setVisibility(i);
        return;
      }
    }
  }
  
  private class GroupAdapter
    extends BaseExpandableListAdapter
    implements ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener
  {
    private final TrustedCredentialsSettings.AdapterData mData = new TrustedCredentialsSettings.AdapterData(TrustedCredentialsSettings.this, paramTab, this, null);
    
    private GroupAdapter(TrustedCredentialsSettings.Tab paramTab)
    {
      load();
    }
    
    private int getUserIdByGroup(int paramInt)
    {
      return TrustedCredentialsSettings.AdapterData.-get1(this.mData).keyAt(paramInt);
    }
    
    private View getViewForCertificate(TrustedCredentialsSettings.CertHolder paramCertHolder, TrustedCredentialsSettings.Tab paramTab, View paramView, ViewGroup paramViewGroup)
    {
      boolean bool2 = true;
      if (paramView == null)
      {
        paramView = LayoutInflater.from(TrustedCredentialsSettings.this.getActivity()).inflate(2130969081, paramViewGroup, false);
        paramViewGroup = new ViewHolder(null);
        ViewHolder.-set0(paramViewGroup, (TextView)paramView.findViewById(2131362613));
        ViewHolder.-set1(paramViewGroup, (TextView)paramView.findViewById(2131362614));
        ViewHolder.-set2(paramViewGroup, (Switch)paramView.findViewById(2131362615));
        paramView.setTag(paramViewGroup);
        ViewHolder.-get0(paramViewGroup).setText(TrustedCredentialsSettings.CertHolder.-get3(paramCertHolder));
        ViewHolder.-get1(paramViewGroup).setText(TrustedCredentialsSettings.CertHolder.-get4(paramCertHolder));
        if (TrustedCredentialsSettings.Tab.-get5(paramTab))
        {
          paramTab = ViewHolder.-get2(paramViewGroup);
          if (!TrustedCredentialsSettings.CertHolder.-get2(paramCertHolder)) {
            break label209;
          }
        }
      }
      label209:
      for (boolean bool1 = false;; bool1 = true)
      {
        paramTab.setChecked(bool1);
        paramTab = ViewHolder.-get2(paramViewGroup);
        bool1 = bool2;
        if (TrustedCredentialsSettings.-get5(TrustedCredentialsSettings.this).hasUserRestriction("no_config_credentials", new UserHandle(paramCertHolder.mProfileId))) {
          bool1 = false;
        }
        paramTab.setEnabled(bool1);
        ViewHolder.-get2(paramViewGroup).setVisibility(0);
        return paramView;
        paramViewGroup = (ViewHolder)paramView.getTag();
        break;
      }
    }
    
    public boolean checkGroupExpandableAndStartWarningActivity(int paramInt)
    {
      return checkGroupExpandableAndStartWarningActivity(paramInt, true);
    }
    
    public boolean checkGroupExpandableAndStartWarningActivity(int paramInt, boolean paramBoolean)
    {
      Object localObject = getGroup(paramInt);
      paramInt = ((UserHandle)localObject).getIdentifier();
      if (TrustedCredentialsSettings.-get5(TrustedCredentialsSettings.this).isQuietModeEnabled((UserHandle)localObject))
      {
        localObject = UnlaunchableAppActivity.createInQuietModeDialogIntent(paramInt);
        if (paramBoolean) {
          TrustedCredentialsSettings.this.getActivity().startActivity((Intent)localObject);
        }
        return false;
      }
      if ((!TrustedCredentialsSettings.-get5(TrustedCredentialsSettings.this).isUserUnlocked((UserHandle)localObject)) && (new LockPatternUtils(TrustedCredentialsSettings.this.getActivity()).isSeparateProfileChallengeEnabled(paramInt)))
      {
        if (paramBoolean) {
          TrustedCredentialsSettings.-wrap1(TrustedCredentialsSettings.this, paramInt);
        }
        return false;
      }
      return true;
    }
    
    public TrustedCredentialsSettings.CertHolder getChild(int paramInt1, int paramInt2)
    {
      return (TrustedCredentialsSettings.CertHolder)((List)TrustedCredentialsSettings.AdapterData.-get1(this.mData).get(getUserIdByGroup(paramInt1))).get(paramInt2);
    }
    
    public TrustedCredentialsSettings.ChildAdapter getChildAdapter(int paramInt)
    {
      return new TrustedCredentialsSettings.ChildAdapter(TrustedCredentialsSettings.this, this, paramInt, null);
    }
    
    public long getChildId(int paramInt1, int paramInt2)
    {
      return paramInt2;
    }
    
    public View getChildView(int paramInt1, int paramInt2, boolean paramBoolean, View paramView, ViewGroup paramViewGroup)
    {
      return getViewForCertificate(getChild(paramInt1, paramInt2), TrustedCredentialsSettings.AdapterData.-get2(this.mData), paramView, paramViewGroup);
    }
    
    public int getChildrenCount(int paramInt)
    {
      List localList = (List)TrustedCredentialsSettings.AdapterData.-get1(this.mData).valueAt(paramInt);
      if (localList != null) {
        return localList.size();
      }
      return 0;
    }
    
    public UserHandle getGroup(int paramInt)
    {
      return new UserHandle(TrustedCredentialsSettings.AdapterData.-get1(this.mData).keyAt(paramInt));
    }
    
    public int getGroupCount()
    {
      return TrustedCredentialsSettings.AdapterData.-get1(this.mData).size();
    }
    
    public long getGroupId(int paramInt)
    {
      return getUserIdByGroup(paramInt);
    }
    
    public View getGroupView(int paramInt, boolean paramBoolean, View paramView, ViewGroup paramViewGroup)
    {
      View localView = paramView;
      if (paramView == null) {
        localView = Utils.inflateCategoryHeader((LayoutInflater)TrustedCredentialsSettings.this.getActivity().getSystemService("layout_inflater"), paramViewGroup);
      }
      paramView = (TextView)localView.findViewById(16908310);
      if (getUserInfoByGroup(paramInt).isManagedProfile()) {
        paramView.setText(2131689625);
      }
      for (;;)
      {
        paramView.setTextAlignment(6);
        return localView;
        paramView.setText(2131689624);
      }
    }
    
    public UserInfo getUserInfoByGroup(int paramInt)
    {
      return TrustedCredentialsSettings.-get5(TrustedCredentialsSettings.this).getUserInfo(getUserIdByGroup(paramInt));
    }
    
    public boolean hasStableIds()
    {
      return false;
    }
    
    public boolean isChildSelectable(int paramInt1, int paramInt2)
    {
      return true;
    }
    
    public void load()
    {
      TrustedCredentialsSettings.AdapterData localAdapterData = this.mData;
      localAdapterData.getClass();
      new TrustedCredentialsSettings.AdapterData.AliasLoader(localAdapterData).execute(new Void[0]);
    }
    
    public boolean onChildClick(ExpandableListView paramExpandableListView, View paramView, int paramInt1, int paramInt2, long paramLong)
    {
      TrustedCredentialsSettings.-wrap2(TrustedCredentialsSettings.this, getChild(paramInt1, paramInt2));
      return true;
    }
    
    public boolean onGroupClick(ExpandableListView paramExpandableListView, View paramView, int paramInt, long paramLong)
    {
      return !checkGroupExpandableAndStartWarningActivity(paramInt);
    }
    
    public void remove(TrustedCredentialsSettings.CertHolder paramCertHolder)
    {
      this.mData.remove(paramCertHolder);
    }
    
    public void setExpandableListView(ExpandableListView paramExpandableListView)
    {
      paramExpandableListView.setAdapter(this);
      paramExpandableListView.setOnGroupClickListener(this);
      paramExpandableListView.setOnChildClickListener(this);
      paramExpandableListView.setVisibility(0);
    }
    
    private class ViewHolder
    {
      private TextView mSubjectPrimaryView;
      private TextView mSubjectSecondaryView;
      private Switch mSwitch;
      
      private ViewHolder() {}
    }
  }
  
  private static enum Tab
  {
    SYSTEM("system", 2131692883, 2131362622, 2131362623, 2131362625, 2131362626, 2131362627, 2131362624, true),  USER("user", 2131692884, 2131362628, 2131362629, 2131362631, 2131362632, 2131362633, 2131362630, false);
    
    private final int mContentView;
    private final int mExpandableList;
    private final int mLabel;
    private final int mPersonalList;
    private final int mProgress;
    private final boolean mSwitch;
    private final String mTag;
    private final int mView;
    private final int mWorkList;
    
    private Tab(String paramString1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, boolean paramBoolean)
    {
      this.mTag = paramString1;
      this.mLabel = paramInt2;
      this.mView = paramInt3;
      this.mProgress = paramInt4;
      this.mPersonalList = paramInt5;
      this.mWorkList = paramInt6;
      this.mExpandableList = paramInt7;
      this.mContentView = paramInt8;
      this.mSwitch = paramBoolean;
    }
    
    private boolean deleted(IKeyChainService paramIKeyChainService, String paramString)
      throws RemoteException
    {
      switch (-getcom-android-settings-TrustedCredentialsSettings$TabSwitchesValues()[ordinal()])
      {
      default: 
        throw new AssertionError();
      case 1: 
        return !paramIKeyChainService.containsCaAlias(paramString);
      }
      return false;
    }
    
    private List<ParcelableString> getAliases(IKeyChainService paramIKeyChainService)
      throws RemoteException
    {
      switch (-getcom-android-settings-TrustedCredentialsSettings$TabSwitchesValues()[ordinal()])
      {
      default: 
        throw new AssertionError();
      case 1: 
        return paramIKeyChainService.getSystemCaAliases().getList();
      }
      return paramIKeyChainService.getUserCaAliases().getList();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\TrustedCredentialsSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */