package com.android.settings;

import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.IActivityManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.security.KeyChain;
import android.security.KeyStore;
import android.security.KeyStore.State;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.internal.widget.LockPatternUtils;
import com.android.org.bouncycastle.asn1.ASN1InputStream;
import com.android.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import com.android.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;

public final class CredentialStorage
  extends Activity
{
  public static final String ACTION_INSTALL = "com.android.credentials.INSTALL";
  public static final String ACTION_RESET = "com.android.credentials.RESET";
  public static final String ACTION_UNLOCK = "com.android.credentials.UNLOCK";
  private static final int CONFIRM_CLEAR_SYSTEM_CREDENTIAL_REQUEST = 2;
  private static final int CONFIRM_KEY_GUARD_REQUEST = 1;
  static final int MIN_PASSWORD_QUALITY = 65536;
  private static final String TAG = "CredentialStorage";
  private Bundle mInstallBundle;
  private boolean mIsConfigureKeyGuardDialogShowing;
  private final KeyStore mKeyStore = KeyStore.getInstance();
  private int mRetriesRemaining = -1;
  
  private boolean checkCallerIsCertInstallerOrSelfInProfile()
  {
    if (TextUtils.equals("com.android.certinstaller", getCallingPackage())) {
      return getPackageManager().checkSignatures(getCallingPackage(), getPackageName()) == 0;
    }
    try
    {
      int i = ActivityManagerNative.getDefault().getLaunchedFromUid(getActivityToken());
      if (i == -1)
      {
        Log.e("CredentialStorage", "com.android.credentials.INSTALL must be started with startActivityForResult");
        return false;
      }
      if (!UserHandle.isSameApp(i, Process.myUid())) {
        return false;
      }
      i = UserHandle.getUserId(i);
      UserInfo localUserInfo = ((UserManager)getSystemService("user")).getProfileParent(i);
      if ((localUserInfo == null) || (localUserInfo.id != UserHandle.myUserId())) {
        return false;
      }
    }
    catch (RemoteException localRemoteException)
    {
      return false;
    }
    return true;
  }
  
  private boolean checkKeyGuardQuality()
  {
    int i = UserManager.get(this).getCredentialOwnerProfile(UserHandle.myUserId());
    return new LockPatternUtils(this).getActivePasswordQuality(i) >= 65536;
  }
  
  private boolean confirmKeyGuard(int paramInt)
  {
    Resources localResources = getResources();
    return new ChooseLockSettingsHelper(this).launchConfirmationActivity(paramInt, localResources.getText(2131692593), true);
  }
  
  private void ensureKeyGuard()
  {
    if (!checkKeyGuardQuality())
    {
      if (!this.mIsConfigureKeyGuardDialogShowing) {
        new ConfigureKeyGuardDialog(null);
      }
      return;
    }
    if (confirmKeyGuard(1)) {
      return;
    }
    finish();
  }
  
  private void handleUnlockOrInstall()
  {
    if (isFinishing()) {
      return;
    }
    switch (-getandroid-security-KeyStore$StateSwitchesValues()[this.mKeyStore.state().ordinal()])
    {
    default: 
      return;
    case 2: 
      ensureKeyGuard();
      return;
    case 1: 
      new UnlockDialog(null);
      return;
    }
    if (!checkKeyGuardQuality())
    {
      if (!this.mIsConfigureKeyGuardDialogShowing) {
        new ConfigureKeyGuardDialog(null);
      }
      return;
    }
    installIfAvailable();
    finish();
  }
  
  private void installIfAvailable()
  {
    if ((this.mInstallBundle == null) || (this.mInstallBundle.isEmpty())) {
      return;
    }
    Object localObject = this.mInstallBundle;
    this.mInstallBundle = null;
    int k = ((Bundle)localObject).getInt("install_as_uid", -1);
    String str;
    byte[] arrayOfByte;
    if ((k == -1) || (UserHandle.isSameUser(k, Process.myUid())))
    {
      if (((Bundle)localObject).containsKey("user_private_key_name"))
      {
        str = ((Bundle)localObject).getString("user_private_key_name");
        arrayOfByte = ((Bundle)localObject).getByteArray("user_private_key_data");
        int j = 1;
        i = j;
        if (k == 1010)
        {
          i = j;
          if (isHardwareBackedKey(arrayOfByte))
          {
            Log.d("CredentialStorage", "Saving private key with FLAG_NONE for WIFI_UID");
            i = 0;
          }
        }
        if (!this.mKeyStore.importKey(str, arrayOfByte, k, i)) {
          Log.e("CredentialStorage", "Failed to install " + str + " as uid " + k);
        }
      }
    }
    else
    {
      i = UserHandle.getUserId(k);
      UserHandle.myUserId();
      if (k != 1010)
      {
        Log.e("CredentialStorage", "Failed to install credentials as uid " + k + ": cross-user installs" + " may only target wifi uids");
        return;
      }
      startActivityAsUser(new Intent("com.android.credentials.INSTALL").setFlags(33554432).putExtras((Bundle)localObject), new UserHandle(i));
      return;
    }
    if (k == 1010) {}
    for (int i = 0; ((Bundle)localObject).containsKey("user_certificate_name"); i = 1)
    {
      str = ((Bundle)localObject).getString("user_certificate_name");
      arrayOfByte = ((Bundle)localObject).getByteArray("user_certificate_data");
      if (this.mKeyStore.put(str, arrayOfByte, k, i)) {
        break;
      }
      Log.e("CredentialStorage", "Failed to install " + str + " as uid " + k);
      return;
    }
    if (((Bundle)localObject).containsKey("ca_certificates_name"))
    {
      str = ((Bundle)localObject).getString("ca_certificates_name");
      localObject = ((Bundle)localObject).getByteArray("ca_certificates_data");
      if (!this.mKeyStore.put(str, (byte[])localObject, k, i))
      {
        Log.e("CredentialStorage", "Failed to install " + str + " as uid " + k);
        return;
      }
    }
    setResult(-1);
  }
  
  private boolean isHardwareBackedKey(byte[] paramArrayOfByte)
  {
    try
    {
      boolean bool = KeyChain.isBoundKeyAlgorithm(new AlgorithmId(new ObjectIdentifier(PrivateKeyInfo.getInstance(new ASN1InputStream(new ByteArrayInputStream(paramArrayOfByte)).readObject()).getAlgorithmId().getAlgorithm().getId())).getName());
      return bool;
    }
    catch (IOException paramArrayOfByte)
    {
      Log.e("CredentialStorage", "Failed to parse key data");
    }
    return false;
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (paramInt1 == 1)
    {
      if (paramInt2 == -1)
      {
        paramIntent = paramIntent.getStringExtra("password");
        if (!TextUtils.isEmpty(paramIntent))
        {
          this.mKeyStore.unlock(paramIntent);
          return;
        }
      }
      finish();
    }
    while (paramInt1 != 2) {
      return;
    }
    if (paramInt2 == -1) {
      new ResetKeyStoreAndKeyChain(null).execute(new Void[0]);
    }
    finish();
  }
  
  protected void onResume()
  {
    super.onResume();
    Intent localIntent = getIntent();
    String str = localIntent.getAction();
    if (!((UserManager)getSystemService("user")).hasUserRestriction("no_config_credentials"))
    {
      if ("com.android.credentials.RESET".equals(str))
      {
        new ResetDialog(null);
        return;
      }
      if (("com.android.credentials.INSTALL".equals(str)) && (checkCallerIsCertInstallerOrSelfInProfile())) {
        this.mInstallBundle = localIntent.getExtras();
      }
      handleUnlockOrInstall();
      return;
    }
    if (("com.android.credentials.UNLOCK".equals(str)) && (this.mKeyStore.state() == KeyStore.State.UNINITIALIZED))
    {
      ensureKeyGuard();
      return;
    }
    finish();
  }
  
  private class ConfigureKeyGuardDialog
    implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener
  {
    private boolean mConfigureConfirmed;
    
    private ConfigureKeyGuardDialog()
    {
      AlertDialog localAlertDialog = new AlertDialog.Builder(CredentialStorage.this).setTitle(17039380).setMessage(2131692618).setPositiveButton(17039370, this).setNegativeButton(17039360, this).create();
      localAlertDialog.setOnDismissListener(this);
      localAlertDialog.show();
      CredentialStorage.-set0(CredentialStorage.this, true);
    }
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      if (paramInt == -1) {}
      for (boolean bool = true;; bool = false)
      {
        this.mConfigureConfirmed = bool;
        return;
      }
    }
    
    public void onDismiss(DialogInterface paramDialogInterface)
    {
      CredentialStorage.-set0(CredentialStorage.this, false);
      if (this.mConfigureConfirmed)
      {
        this.mConfigureConfirmed = false;
        paramDialogInterface = new Intent("android.app.action.SET_NEW_PASSWORD");
        paramDialogInterface.putExtra("minimum_quality", 65536);
        CredentialStorage.this.startActivity(paramDialogInterface);
        return;
      }
      CredentialStorage.this.finish();
    }
  }
  
  private class ResetDialog
    implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener
  {
    private boolean mResetConfirmed;
    
    private ResetDialog()
    {
      this$1 = new AlertDialog.Builder(CredentialStorage.this).setTitle(17039380).setMessage(2131692610).setPositiveButton(17039370, this).setNegativeButton(17039360, this).create();
      CredentialStorage.this.setOnDismissListener(this);
      CredentialStorage.this.show();
    }
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      if (paramInt == -1) {}
      for (boolean bool = true;; bool = false)
      {
        this.mResetConfirmed = bool;
        return;
      }
    }
    
    public void onDismiss(DialogInterface paramDialogInterface)
    {
      if (this.mResetConfirmed)
      {
        this.mResetConfirmed = false;
        if (CredentialStorage.-wrap0(CredentialStorage.this, 2)) {
          return;
        }
      }
      CredentialStorage.this.finish();
    }
  }
  
  private class ResetKeyStoreAndKeyChain
    extends AsyncTask<Void, Void, Boolean>
  {
    private ResetKeyStoreAndKeyChain() {}
    
    /* Error */
    protected Boolean doInBackground(Void... paramVarArgs)
    {
      // Byte code:
      //   0: new 31	com/android/internal/widget/LockPatternUtils
      //   3: dup
      //   4: aload_0
      //   5: getfield 14	com/android/settings/CredentialStorage$ResetKeyStoreAndKeyChain:this$0	Lcom/android/settings/CredentialStorage;
      //   8: invokespecial 34	com/android/internal/widget/LockPatternUtils:<init>	(Landroid/content/Context;)V
      //   11: invokestatic 40	android/os/UserHandle:myUserId	()I
      //   14: invokevirtual 44	com/android/internal/widget/LockPatternUtils:resetKeyStore	(I)V
      //   17: aload_0
      //   18: getfield 14	com/android/settings/CredentialStorage$ResetKeyStoreAndKeyChain:this$0	Lcom/android/settings/CredentialStorage;
      //   21: invokestatic 50	android/security/KeyChain:bind	(Landroid/content/Context;)Landroid/security/KeyChain$KeyChainConnection;
      //   24: astore_1
      //   25: aload_1
      //   26: invokevirtual 56	android/security/KeyChain$KeyChainConnection:getService	()Landroid/security/IKeyChainService;
      //   29: invokeinterface 62 1 0
      //   34: invokestatic 68	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
      //   37: astore_2
      //   38: aload_1
      //   39: invokevirtual 71	android/security/KeyChain$KeyChainConnection:close	()V
      //   42: aload_2
      //   43: areturn
      //   44: astore_1
      //   45: aload_1
      //   46: invokevirtual 74	java/lang/IllegalArgumentException:printStackTrace	()V
      //   49: aload_2
      //   50: areturn
      //   51: astore_1
      //   52: invokestatic 80	java/lang/Thread:currentThread	()Ljava/lang/Thread;
      //   55: invokevirtual 83	java/lang/Thread:interrupt	()V
      //   58: iconst_0
      //   59: invokestatic 68	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
      //   62: areturn
      //   63: astore_2
      //   64: iconst_0
      //   65: invokestatic 68	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
      //   68: astore_2
      //   69: aload_1
      //   70: invokevirtual 71	android/security/KeyChain$KeyChainConnection:close	()V
      //   73: aload_2
      //   74: areturn
      //   75: astore_1
      //   76: aload_1
      //   77: invokevirtual 74	java/lang/IllegalArgumentException:printStackTrace	()V
      //   80: aload_2
      //   81: areturn
      //   82: astore_2
      //   83: aload_1
      //   84: invokevirtual 71	android/security/KeyChain$KeyChainConnection:close	()V
      //   87: aload_2
      //   88: athrow
      //   89: astore_1
      //   90: aload_1
      //   91: invokevirtual 74	java/lang/IllegalArgumentException:printStackTrace	()V
      //   94: goto -7 -> 87
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	97	0	this	ResetKeyStoreAndKeyChain
      //   0	97	1	paramVarArgs	Void[]
      //   37	13	2	localBoolean1	Boolean
      //   63	1	2	localRemoteException	RemoteException
      //   68	13	2	localBoolean2	Boolean
      //   82	6	2	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   38	42	44	java/lang/IllegalArgumentException
      //   17	25	51	java/lang/InterruptedException
      //   38	42	51	java/lang/InterruptedException
      //   45	49	51	java/lang/InterruptedException
      //   69	73	51	java/lang/InterruptedException
      //   76	80	51	java/lang/InterruptedException
      //   83	87	51	java/lang/InterruptedException
      //   87	89	51	java/lang/InterruptedException
      //   90	94	51	java/lang/InterruptedException
      //   25	38	63	android/os/RemoteException
      //   69	73	75	java/lang/IllegalArgumentException
      //   25	38	82	finally
      //   64	69	82	finally
      //   83	87	89	java/lang/IllegalArgumentException
    }
    
    protected void onPostExecute(Boolean paramBoolean)
    {
      if (paramBoolean.booleanValue()) {
        Toast.makeText(CredentialStorage.this, 2131692615, 0).show();
      }
      for (;;)
      {
        CredentialStorage.this.finish();
        return;
        Toast.makeText(CredentialStorage.this, 2131692616, 0).show();
      }
    }
  }
  
  private class UnlockDialog
    implements TextWatcher, DialogInterface.OnClickListener, DialogInterface.OnDismissListener
  {
    private final Button mButton;
    private final TextView mError;
    private final TextView mOldPassword;
    private boolean mUnlockConfirmed;
    
    private UnlockDialog()
    {
      View localView = View.inflate(CredentialStorage.this, 2130968652, null);
      Object localObject;
      if (CredentialStorage.-get1(CredentialStorage.this) == -1) {
        localObject = CredentialStorage.this.getResources().getText(2131692608);
      }
      for (;;)
      {
        ((TextView)localView.findViewById(2131362048)).setText((CharSequence)localObject);
        this.mOldPassword = ((TextView)localView.findViewById(2131362051));
        this.mOldPassword.setVisibility(0);
        this.mOldPassword.addTextChangedListener(this);
        this.mError = ((TextView)localView.findViewById(2131362049));
        this$1 = new AlertDialog.Builder(CredentialStorage.this).setView(localView).setTitle(2131692607).setPositiveButton(17039370, this).setNegativeButton(17039360, this).create();
        CredentialStorage.this.setOnDismissListener(this);
        CredentialStorage.this.show();
        this.mButton = CredentialStorage.this.getButton(-1);
        this.mButton.setEnabled(false);
        return;
        if (CredentialStorage.-get1(CredentialStorage.this) > 3) {
          localObject = CredentialStorage.this.getResources().getText(2131692612);
        } else if (CredentialStorage.-get1(CredentialStorage.this) == 1) {
          localObject = CredentialStorage.this.getResources().getText(2131692613);
        } else {
          localObject = CredentialStorage.this.getString(2131692614, new Object[] { Integer.valueOf(CredentialStorage.-get1(CredentialStorage.this)) });
        }
      }
    }
    
    public void afterTextChanged(Editable paramEditable)
    {
      boolean bool2 = true;
      paramEditable = this.mButton;
      boolean bool1 = bool2;
      if (this.mOldPassword != null) {
        if (this.mOldPassword.getText().length() <= 0) {
          break label39;
        }
      }
      label39:
      for (bool1 = bool2;; bool1 = false)
      {
        paramEditable.setEnabled(bool1);
        return;
      }
    }
    
    public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      if (paramInt == -1) {}
      for (boolean bool = true;; bool = false)
      {
        this.mUnlockConfirmed = bool;
        return;
      }
    }
    
    public void onDismiss(DialogInterface paramDialogInterface)
    {
      if (this.mUnlockConfirmed)
      {
        this.mUnlockConfirmed = false;
        this.mError.setVisibility(0);
        CredentialStorage.-get0(CredentialStorage.this).unlock(this.mOldPassword.getText().toString());
        int i = CredentialStorage.-get0(CredentialStorage.this).getLastError();
        if (i == 1)
        {
          CredentialStorage.-set1(CredentialStorage.this, -1);
          Toast.makeText(CredentialStorage.this, 2131692617, 0).show();
          CredentialStorage.-wrap1(CredentialStorage.this);
        }
        do
        {
          return;
          if (i == 3)
          {
            CredentialStorage.-set1(CredentialStorage.this, -1);
            Toast.makeText(CredentialStorage.this, 2131692615, 0).show();
            CredentialStorage.-wrap2(CredentialStorage.this);
            return;
          }
        } while (i < 10);
        CredentialStorage.-set1(CredentialStorage.this, i - 10 + 1);
        CredentialStorage.-wrap2(CredentialStorage.this);
        return;
      }
      CredentialStorage.this.finish();
    }
    
    public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\CredentialStorage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */