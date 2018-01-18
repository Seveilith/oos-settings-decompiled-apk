package com.oneplus.settings;

import android.content.Context;
import android.os.ConditionVariable;
import android.text.TextUtils;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class OPRebootWipeUserdata
{
  private static File COMMAND_FILE = new File(RECOVERY_DIR, "command");
  private static File LOG_FILE = new File(RECOVERY_DIR, "log");
  private static File RECOVERY_DIR = new File("/cache/recovery");
  private static final String TAG = "OPRebootWipeUserdata";
  
  /* Error */
  private static void bootCommand(Context paramContext, String... paramVarArgs)
    throws IOException
  {
    // Byte code:
    //   0: ldc 12
    //   2: ldc 46
    //   4: invokestatic 52	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   7: pop
    //   8: getstatic 24	com/oneplus/settings/OPRebootWipeUserdata:RECOVERY_DIR	Ljava/io/File;
    //   11: invokevirtual 56	java/io/File:mkdirs	()Z
    //   14: pop
    //   15: getstatic 31	com/oneplus/settings/OPRebootWipeUserdata:COMMAND_FILE	Ljava/io/File;
    //   18: invokevirtual 59	java/io/File:delete	()Z
    //   21: pop
    //   22: getstatic 35	com/oneplus/settings/OPRebootWipeUserdata:LOG_FILE	Ljava/io/File;
    //   25: invokevirtual 59	java/io/File:delete	()Z
    //   28: pop
    //   29: new 61	java/io/FileWriter
    //   32: dup
    //   33: getstatic 31	com/oneplus/settings/OPRebootWipeUserdata:COMMAND_FILE	Ljava/io/File;
    //   36: invokespecial 64	java/io/FileWriter:<init>	(Ljava/io/File;)V
    //   39: astore 4
    //   41: iconst_0
    //   42: istore_2
    //   43: aload_1
    //   44: arraylength
    //   45: istore_3
    //   46: iload_2
    //   47: iload_3
    //   48: if_icmpge +37 -> 85
    //   51: aload_1
    //   52: iload_2
    //   53: aaload
    //   54: astore 5
    //   56: aload 5
    //   58: invokestatic 70	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   61: ifne +17 -> 78
    //   64: aload 4
    //   66: aload 5
    //   68: invokevirtual 73	java/io/FileWriter:write	(Ljava/lang/String;)V
    //   71: aload 4
    //   73: ldc 75
    //   75: invokevirtual 73	java/io/FileWriter:write	(Ljava/lang/String;)V
    //   78: iload_2
    //   79: iconst_1
    //   80: iadd
    //   81: istore_2
    //   82: goto -36 -> 46
    //   85: aload 4
    //   87: invokevirtual 78	java/io/FileWriter:close	()V
    //   90: ldc 80
    //   92: invokestatic 86	android/os/ServiceManager:getService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   95: astore_1
    //   96: ldc 12
    //   98: ldc 88
    //   100: invokestatic 52	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   103: pop
    //   104: aload_1
    //   105: invokestatic 94	android/os/storage/IMountService$Stub:asInterface	(Landroid/os/IBinder;)Landroid/os/storage/IMountService;
    //   108: astore_1
    //   109: aload_1
    //   110: ldc 96
    //   112: ldc 98
    //   114: invokeinterface 104 3 0
    //   119: ldc 12
    //   121: ldc 106
    //   123: invokestatic 52	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   126: pop
    //   127: aload_1
    //   128: ldc 108
    //   130: ldc 98
    //   132: invokeinterface 104 3 0
    //   137: ldc 12
    //   139: ldc 110
    //   141: invokestatic 52	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   144: pop
    //   145: aload_1
    //   146: ldc 112
    //   148: ldc 98
    //   150: invokeinterface 104 3 0
    //   155: ldc 12
    //   157: ldc 114
    //   159: invokestatic 52	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   162: pop
    //   163: aload_0
    //   164: ldc 116
    //   166: invokevirtual 122	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   169: checkcast 124	android/os/PowerManager
    //   172: ldc 126
    //   174: invokevirtual 129	android/os/PowerManager:reboot	(Ljava/lang/String;)V
    //   177: new 42	java/io/IOException
    //   180: dup
    //   181: ldc -125
    //   183: invokespecial 132	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   186: athrow
    //   187: astore_0
    //   188: aload 4
    //   190: invokevirtual 78	java/io/FileWriter:close	()V
    //   193: aload_0
    //   194: athrow
    //   195: astore_1
    //   196: aload_1
    //   197: invokevirtual 135	android/os/RemoteException:printStackTrace	()V
    //   200: goto -37 -> 163
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	203	0	paramContext	Context
    //   0	203	1	paramVarArgs	String[]
    //   42	40	2	i	int
    //   45	4	3	j	int
    //   39	150	4	localFileWriter	java.io.FileWriter
    //   54	13	5	str	String
    // Exception table:
    //   from	to	target	type
    //   43	46	187	finally
    //   56	78	187	finally
    //   90	163	195	android/os/RemoteException
  }
  
  public static void rebootWipeUserData(Context paramContext, boolean paramBoolean, String paramString1, String paramString2, String paramString3)
    throws IOException
  {
    new ConditionVariable();
    String str1 = null;
    if (paramBoolean) {
      str1 = "--shutdown_after";
    }
    String str2 = null;
    if (!TextUtils.isEmpty(paramString1)) {
      str2 = "--reason=" + sanitizeArg(paramString1);
    }
    bootCommand(paramContext, new String[] { str1, paramString2, str2, "--locale=" + Locale.getDefault().toString(), "--password=" + paramString3 });
  }
  
  private static String sanitizeArg(String paramString)
  {
    return paramString.replace('\000', '?').replace('\n', '?');
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\OPRebootWipeUserdata.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */