package com.android.settings.wifi;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class WapiCertMgmtDialog
  extends AlertDialog
  implements DialogInterface.OnClickListener, AdapterView.OnItemSelectedListener, View.OnClickListener
{
  private static final int CANCEL_BUTTON = -2;
  private static final String DEFAULT_CERTIFICATE_PATH = "/data/misc/wapi_certificate";
  private static final int INSTALL_BUTTON = -1;
  private static final String InstallTitle = "Install";
  public static final int MODE_INSTALL = 0;
  public static final int MODE_UNINSTALL = 1;
  private static final String TAG = "WapiCertMgmtDialog";
  private static final int UNINSTALL_BUTTON = -3;
  private static final String UninstallTitle = "Uninstall";
  private static String certificate_installation_path;
  private static String certificate_path;
  private static String default_sdcard_path;
  private static String external_sdcard_path;
  private static String wifi_sdcard_path;
  private EditText mASCertEdit;
  private TextView mASCertText;
  private int mCancelButtonPos = Integer.MAX_VALUE;
  private EditText mCreateSubdirEdit;
  private TextView mCreateSubdirText;
  private CharSequence mCustomTitle;
  private Spinner mDeletDirSpinner;
  private TextView mDeletDirText;
  private int mInstallButtonPos = Integer.MAX_VALUE;
  private int mMode = 0;
  private RefreshAdvance mRefreshAdvance;
  private StorageManager mStorageManager;
  private int mUninstallButtonPos = Integer.MAX_VALUE;
  private String mUninstallCerts;
  private EditText mUserCertEdit;
  private TextView mUserCertText;
  private View mView;
  
  public WapiCertMgmtDialog(Context paramContext)
  {
    super(paramContext);
  }
  
  /* Error */
  private boolean copyFile(File paramFile1, File paramFile2)
  {
    // Byte code:
    //   0: sipush 1024
    //   3: newarray <illegal type>
    //   5: astore 4
    //   7: ldc 32
    //   9: ldc 85
    //   11: invokestatic 91	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   14: pop
    //   15: new 93	java/io/FileInputStream
    //   18: dup
    //   19: aload_2
    //   20: invokespecial 96	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   23: astore_2
    //   24: new 98	java/io/FileOutputStream
    //   27: dup
    //   28: aload_1
    //   29: invokespecial 99	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   32: astore_1
    //   33: aload_2
    //   34: aload 4
    //   36: invokevirtual 103	java/io/FileInputStream:read	([B)I
    //   39: istore_3
    //   40: iload_3
    //   41: iconst_m1
    //   42: if_icmpne +46 -> 88
    //   45: aload_2
    //   46: invokevirtual 107	java/io/FileInputStream:close	()V
    //   49: aload_1
    //   50: invokevirtual 108	java/io/FileOutputStream:close	()V
    //   53: iconst_1
    //   54: ireturn
    //   55: astore_1
    //   56: aload_0
    //   57: aload_1
    //   58: invokevirtual 112	java/lang/Exception:toString	()Ljava/lang/String;
    //   61: invokevirtual 116	com/android/settings/wifi/WapiCertMgmtDialog:setMessage	(Ljava/lang/CharSequence;)V
    //   64: iconst_0
    //   65: ireturn
    //   66: astore_1
    //   67: aload_0
    //   68: aload_1
    //   69: invokevirtual 112	java/lang/Exception:toString	()Ljava/lang/String;
    //   72: invokevirtual 116	com/android/settings/wifi/WapiCertMgmtDialog:setMessage	(Ljava/lang/CharSequence;)V
    //   75: iconst_0
    //   76: ireturn
    //   77: astore_1
    //   78: aload_0
    //   79: aload_1
    //   80: invokevirtual 112	java/lang/Exception:toString	()Ljava/lang/String;
    //   83: invokevirtual 116	com/android/settings/wifi/WapiCertMgmtDialog:setMessage	(Ljava/lang/CharSequence;)V
    //   86: iconst_0
    //   87: ireturn
    //   88: aload_1
    //   89: aload 4
    //   91: iconst_0
    //   92: iload_3
    //   93: invokevirtual 120	java/io/FileOutputStream:write	([BII)V
    //   96: goto -63 -> 33
    //   99: astore_1
    //   100: aload_0
    //   101: aload_1
    //   102: invokevirtual 112	java/lang/Exception:toString	()Ljava/lang/String;
    //   105: invokevirtual 116	com/android/settings/wifi/WapiCertMgmtDialog:setMessage	(Ljava/lang/CharSequence;)V
    //   108: iconst_0
    //   109: ireturn
    //   110: astore_1
    //   111: aload_0
    //   112: aload_1
    //   113: invokevirtual 112	java/lang/Exception:toString	()Ljava/lang/String;
    //   116: invokevirtual 116	com/android/settings/wifi/WapiCertMgmtDialog:setMessage	(Ljava/lang/CharSequence;)V
    //   119: iconst_0
    //   120: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	121	0	this	WapiCertMgmtDialog
    //   0	121	1	paramFile1	File
    //   0	121	2	paramFile2	File
    //   39	54	3	i	int
    //   5	85	4	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   15	24	55	java/lang/Exception
    //   24	33	66	java/lang/Exception
    //   33	40	77	java/lang/Exception
    //   88	96	99	java/lang/Exception
    //   45	53	110	java/lang/Exception
  }
  
  private void deleteAll(String paramString)
  {
    File localFile = new File(paramString);
    Log.v("WapiCertMgmtDialog", "deleteAll filepath " + paramString);
    if ((localFile.exists()) && (localFile.isDirectory()))
    {
      paramString = localFile.listFiles();
      j = paramString.length;
      if (j == 0)
      {
        localFile.delete();
        localFile.delete();
      }
    }
    while (!localFile.exists()) {
      for (;;)
      {
        int j;
        return;
        int i = 0;
        while (i < j)
        {
          deleteAll(paramString[i].getAbsolutePath());
          i += 1;
        }
      }
    }
    localFile.delete();
  }
  
  private int getDeletDirFromSpinner()
  {
    return this.mDeletDirSpinner.getSelectedItemPosition();
  }
  
  private String getInput(EditText paramEditText)
  {
    String str = null;
    if (paramEditText != null) {
      str = paramEditText.getText().toString();
    }
    return str;
  }
  
  private void handleCancle() {}
  
  private void handleDeletDirChange(int paramInt)
  {
    File localFile = new File("/data/misc/wapi_certificate");
    try
    {
      if (!localFile.isDirectory()) {
        return;
      }
      this.mUninstallCerts = localFile.listFiles()[paramInt].getAbsolutePath();
      return;
    }
    catch (Exception localException)
    {
      setMessage(localException.toString());
    }
  }
  
  private void handleInstall()
  {
    Log.v("WapiCertMgmtDialog", "handleInstall");
    Object localObject2 = new File("/data/misc/wapi_certificate");
    this.mStorageManager = StorageManager.from(getContext());
    Object localObject1 = this.mStorageManager.getVolumeList();
    if (!((File)localObject2).exists())
    {
      ((File)localObject2).mkdir();
      if (!((File)localObject2).exists())
      {
        new AlertDialog.Builder(getContext()).setTitle(2131691876).setIcon(17301543).setMessage("Cert. base dir create failed").setPositiveButton(17039370, null).show();
        return;
      }
      FileUtils.setPermissions("/data/misc/wapi_certificate", 511, -1, -1);
    }
    localObject2 = getInput(this.mCreateSubdirEdit);
    if ((localObject2 == null) || (TextUtils.isEmpty((CharSequence)localObject2)))
    {
      new AlertDialog.Builder(getContext()).setTitle(2131691876).setIcon(17301543).setMessage(2131690115).setPositiveButton(17039370, null).show();
      return;
    }
    String str1 = "/data/misc/wapi_certificate/" + (String)localObject2;
    localObject2 = new File(str1);
    if (((File)localObject2).exists())
    {
      new AlertDialog.Builder(getContext()).setTitle(2131691876).setIcon(17301543).setMessage(2131690116).setPositiveButton(17039370, null).show();
      return;
    }
    try
    {
      ((File)localObject2).mkdir();
      if (!((File)localObject2).exists())
      {
        new AlertDialog.Builder(getContext()).setTitle(2131691876).setIcon(17301543).setMessage(2131690117).setPositiveButton(17039370, null).show();
        return;
      }
    }
    catch (Exception localException1)
    {
      for (;;)
      {
        setMessage(localException1.toString());
      }
      String str2 = getInput(this.mASCertEdit);
      if ((str2 == null) || (TextUtils.isEmpty(str2)))
      {
        new AlertDialog.Builder(getContext()).setTitle(2131691876).setIcon(17301543).setMessage(2131690118).setPositiveButton(17039370, null).show();
        deleteAll(str1);
        return;
      }
      wifi_sdcard_path = "/system/wifi/sdcard";
      int i = 0;
      while (i < localObject1.length)
      {
        localObject2 = new File(localObject1[i].getPath());
        Log.e("adarsh", "Trying to create file at - " + localObject2 + ":: isRemovable=" + localObject1[i].isRemovable() + ", getDescription=" + localObject1[i].getDescription(getContext()) + ", isEmulated=" + localObject1[i].isEmulated() + ", isPrimary=" + localObject1[i].isPrimary());
        if ((localObject1[i].isPrimary()) && (localObject1[i].isEmulated())) {
          default_sdcard_path = ((File)localObject2).toString();
        }
        if (localObject1[i].isRemovable()) {
          external_sdcard_path = ((File)localObject2).toString();
        }
        i += 1;
      }
      certificate_installation_path = default_sdcard_path;
      Log.d("WapiCertMgmtDialog", "default_sdcard_path: " + default_sdcard_path);
      Log.d("WapiCertMgmtDialog", "asCert file:" + str2);
      certificate_path = default_sdcard_path + "/" + str2;
      Log.d("WapiCertMgmtDialog", "certificate_path: " + certificate_path);
      localObject2 = new File(certificate_path);
      Log.d("WapiCertMgmtDialog", "fileASCert.exists(): " + ((File)localObject2).exists());
      localObject1 = localObject2;
      if (!((File)localObject2).exists())
      {
        Log.d("WapiCertMgmtDialog", "Certificate path: " + certificate_path + " does not exist");
        Log.d("WapiCertMgmtDialog", "Hence trying with " + external_sdcard_path);
        certificate_installation_path = external_sdcard_path;
        certificate_path = external_sdcard_path + "/" + str2;
        localObject2 = new File(certificate_path);
        Log.d("WapiCertMgmtDialog", "fileASCert.exists(): " + ((File)localObject2).exists());
        localObject1 = localObject2;
        if (!((File)localObject2).exists())
        {
          Log.d("WapiCertMgmtDialog", "Secondary certificate path: " + certificate_path + " does not exist.");
          Log.d("WapiCertMgmtDialog", "Hence trying with " + wifi_sdcard_path);
          certificate_installation_path = wifi_sdcard_path;
          certificate_path = wifi_sdcard_path + "/" + str2;
          localObject2 = new File(certificate_path);
          Log.d("WapiCertMgmtDialog", "fileASCert.exists(): " + ((File)localObject2).exists());
          localObject1 = localObject2;
          if (!((File)localObject2).exists())
          {
            Log.d("WapiCertMgmtDialog", "wifi certificate path: " + certificate_path + " does not exist.");
            Log.d("WapiCertMgmtDialog", "Hence ABORTING!!!!!");
            new AlertDialog.Builder(getContext()).setTitle(2131691876).setIcon(17301543).setMessage(2131690119).setPositiveButton(17039370, null).show();
            deleteAll(str1);
            return;
          }
        }
      }
      Log.e("WapiCertMgmtDialog", "certificate is installing from " + certificate_installation_path);
      if (!isAsCertificate(certificate_installation_path + "/" + str2))
      {
        new AlertDialog.Builder(getContext()).setTitle(2131691876).setIcon(17301543).setMessage(2131690120).setPositiveButton(17039370, null).show();
        deleteAll(str1);
        return;
      }
      Log.e("WapiCertMgmtDialog", "handleInstall Create AS Cert: = " + str2);
      localObject2 = new File(str1 + "/" + "as.cer");
      try
      {
        ((File)localObject2).createNewFile();
        if (((File)localObject2).exists())
        {
          if (copyFile((File)localObject2, (File)localObject1)) {
            break label1298;
          }
          deleteAll(str1);
        }
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          setMessage(localException2.toString());
        }
        deleteAll(str1);
        return;
      }
      label1298:
      localObject2 = getInput(this.mUserCertEdit);
      if ((localObject2 == null) || (TextUtils.isEmpty((CharSequence)localObject2)))
      {
        new AlertDialog.Builder(getContext()).setTitle(2131691876).setIcon(17301543).setMessage(2131690122).setPositiveButton(17039370, null).show();
        deleteAll(str1);
        return;
      }
      localObject1 = new File(certificate_installation_path + "/" + (String)localObject2);
      if (!((File)localObject1).exists())
      {
        new AlertDialog.Builder(getContext()).setTitle(2131691876).setIcon(17301543).setMessage(2131690123).setPositiveButton(17039370, null).show();
        deleteAll(str1);
        return;
      }
      if (!isUserCertificate(certificate_installation_path + "/" + (String)localObject2))
      {
        new AlertDialog.Builder(getContext()).setTitle(2131691876).setIcon(17301543).setMessage(2131690121).setPositiveButton(17039370, null).show();
        deleteAll(str1);
        return;
      }
      localObject2 = new File(str1 + "/" + "user.cer");
      try
      {
        ((File)localObject2).createNewFile();
        if (((File)localObject2).exists())
        {
          if (copyFile((File)localObject2, (File)localObject1)) {
            break label1608;
          }
          deleteAll(str1);
        }
      }
      catch (Exception localException3)
      {
        for (;;)
        {
          setMessage(localException3.toString());
        }
        deleteAll(str1);
        return;
      }
      label1608:
      FileUtils.setPermissions(str1, 455, -1, -1);
      FileUtils.setPermissions(str1 + "/" + "user.cer", 292, -1, -1);
      FileUtils.setPermissions(str1 + "/" + "as.cer", 292, -1, -1);
      this.mRefreshAdvance.refreashAdvance();
    }
  }
  
  private void handleUninstall()
  {
    Log.v("WapiCertMgmtDialog", "handleUninstall");
    if (this.mUninstallCerts != null) {
      deleteAll(this.mUninstallCerts);
    }
    if (this.mRefreshAdvance != null) {
      this.mRefreshAdvance.refreashAdvance();
    }
  }
  
  private boolean isAsCertificate(String paramString)
  {
    paramString = new File(paramString);
    int i;
    try
    {
      i = searchString("BEGIN CERTIFICATE", paramString);
      if (1 != i) {
        return false;
      }
    }
    catch (Exception paramString)
    {
      setMessage(paramString.toString());
      return false;
    }
    try
    {
      i = searchString("END CERTIFICATE", paramString);
      if (1 != i) {
        return false;
      }
    }
    catch (Exception paramString)
    {
      setMessage(paramString.toString());
      return false;
    }
    try
    {
      i = searchString("BEGIN EC PRIVATE KEY", paramString);
      if (i != 0) {
        return false;
      }
    }
    catch (Exception paramString)
    {
      setMessage(paramString.toString());
      return false;
    }
    try
    {
      i = searchString("END EC PRIVATE KEY", paramString);
      if (i != 0) {
        return false;
      }
    }
    catch (Exception paramString)
    {
      setMessage(paramString.toString());
      return false;
    }
    return true;
  }
  
  private boolean isUserCertificate(String paramString)
  {
    paramString = new File(paramString);
    int i;
    try
    {
      i = searchString("BEGIN CERTIFICATE", paramString);
      if (1 != i) {
        return false;
      }
    }
    catch (Exception paramString)
    {
      setMessage(paramString.toString());
      return false;
    }
    try
    {
      i = searchString("END CERTIFICATE", paramString);
      if (1 != i) {
        return false;
      }
    }
    catch (Exception paramString)
    {
      setMessage(paramString.toString());
      return false;
    }
    try
    {
      i = searchString("BEGIN EC PRIVATE KEY", paramString);
      if (1 != i) {
        return false;
      }
    }
    catch (Exception paramString)
    {
      setMessage(paramString.toString());
      return false;
    }
    try
    {
      i = searchString("END EC PRIVATE KEY", paramString);
      if (1 != i) {
        return false;
      }
    }
    catch (Exception paramString)
    {
      setMessage(paramString.toString());
      return false;
    }
    return true;
  }
  
  private void onLayout()
  {
    int k = 0;
    int j = 0;
    setInverseBackgroundForced(true);
    int i;
    if (this.mMode == 0)
    {
      setLayout(2130969123);
      i = 2131690107;
      this.mInstallButtonPos = -1;
    }
    for (;;)
    {
      this.mCancelButtonPos = -2;
      setButtons(i, 2131690109, j);
      return;
      i = k;
      if (this.mMode == 1)
      {
        setLayout(2130969124);
        j = 2131690108;
        this.mUninstallButtonPos = -3;
        i = k;
      }
    }
  }
  
  private void onReferenceViews(View paramView)
  {
    if (this.mMode == 0)
    {
      this.mCreateSubdirText = ((TextView)paramView.findViewById(2131362812));
      this.mCreateSubdirEdit = ((EditText)paramView.findViewById(2131362813));
      this.mASCertText = ((TextView)paramView.findViewById(2131362814));
      this.mASCertEdit = ((EditText)paramView.findViewById(2131362815));
      this.mUserCertText = ((TextView)paramView.findViewById(2131362816));
      this.mUserCertEdit = ((EditText)paramView.findViewById(2131362817));
    }
    while (this.mMode != 1) {
      return;
    }
    this.mDeletDirText = ((TextView)paramView.findViewById(2131362818));
    this.mDeletDirSpinner = ((Spinner)paramView.findViewById(2131362819));
    this.mDeletDirSpinner.setOnItemSelectedListener(this);
    setDeletDirSpinnerAdapter();
  }
  
  private void setButtons(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 > 0) {
      setButton(getContext().getString(paramInt1), this);
    }
    if (paramInt3 > 0) {
      setButton3(getContext().getString(paramInt3), this);
    }
    if (paramInt2 > 0) {
      setButton2(getContext().getString(paramInt2), this);
    }
  }
  
  private void setDeletDirSpinnerAdapter()
  {
    Object localObject1 = getContext();
    ArrayList localArrayList = new ArrayList();
    Object localObject2 = new File("/data/misc/wapi_certificate");
    try
    {
      if (!((File)localObject2).isDirectory()) {
        return;
      }
      localObject2 = ((File)localObject2).listFiles();
      int i = 0;
      while (i < localObject2.length)
      {
        localArrayList.add(localObject2[i].getName());
        i += 1;
      }
      localObject1 = new ArrayAdapter((Context)localObject1, 17367048, (String[])localArrayList.toArray(new String[0]));
      ((ArrayAdapter)localObject1).setDropDownViewResource(17367049);
      this.mDeletDirSpinner.setAdapter((SpinnerAdapter)localObject1);
      return;
    }
    catch (Exception localException)
    {
      setMessage(localException.toString());
    }
  }
  
  private void setLayout(int paramInt)
  {
    View localView = getLayoutInflater().inflate(paramInt, null);
    this.mView = localView;
    setView(localView);
    onReferenceViews(this.mView);
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    Log.v("WapiCertMgmtDialog", "onClick which " + paramInt);
    if (paramInt == this.mInstallButtonPos) {
      handleInstall();
    }
    do
    {
      return;
      if (paramInt == this.mUninstallButtonPos)
      {
        handleUninstall();
        return;
      }
    } while (paramInt != this.mCancelButtonPos);
    handleCancle();
  }
  
  public void onClick(View paramView)
  {
    Log.v("WapiCertMgmtDialog", "onClick View ");
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    onLayout();
    super.onCreate(paramBundle);
  }
  
  public void onItemSelected(AdapterView paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    Log.v("WapiCertMgmtDialog", "onItemSelected ");
    if (paramAdapterView == this.mDeletDirSpinner) {
      handleDeletDirChange(getDeletDirFromSpinner());
    }
  }
  
  public void onNothingSelected(AdapterView paramAdapterView)
  {
    Log.v("WapiCertMgmtDialog", "onNothingSelected ");
  }
  
  public int searchString(String paramString, File paramFile)
    throws Exception
  {
    BufferedReader localBufferedReader = new BufferedReader(new FileReader(paramFile), 2048);
    paramFile = "";
    Object localObject = new String("");
    String str1;
    String str2;
    do
    {
      str1 = (String)localObject + paramFile;
      str2 = localBufferedReader.readLine();
      localObject = str1;
      paramFile = str2;
    } while (str2 != null);
    return str1.split(paramString).length - 1;
  }
  
  public void setMode(int paramInt)
  {
    this.mMode = paramInt;
  }
  
  public void setRefreshAdvance(RefreshAdvance paramRefreshAdvance)
  {
    this.mRefreshAdvance = paramRefreshAdvance;
  }
  
  public void setTitle(int paramInt)
  {
    setTitle(getContext().getString(paramInt));
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    super.setTitle(paramCharSequence);
    this.mCustomTitle = paramCharSequence;
  }
  
  public static abstract interface RefreshAdvance
  {
    public abstract void refreashAdvance();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\WapiCertMgmtDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */