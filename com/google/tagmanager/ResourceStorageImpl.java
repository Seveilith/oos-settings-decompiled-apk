package com.google.tagmanager;

import android.content.Context;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.tagmanager.proto.Resource.ResourceWithMetadata;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ResourceStorageImpl
  implements Container.ResourceStorage
{
  private static final String SAVED_RESOURCE_FILENAME_PREFIX = "resource_";
  private static final String SAVED_RESOURCE_SUB_DIR = "google_tagmanager";
  private LoadCallback<Resource.ResourceWithMetadata> mCallback;
  private final String mContainerId;
  private final Context mContext;
  private final ExecutorService mExecutor;
  
  ResourceStorageImpl(Context paramContext, String paramString)
  {
    this.mContext = paramContext;
    this.mContainerId = paramString;
    this.mExecutor = Executors.newSingleThreadExecutor();
  }
  
  private String stringFromInputStream(InputStream paramInputStream)
    throws IOException
  {
    StringWriter localStringWriter = new StringWriter();
    char[] arrayOfChar = new char['Ѐ'];
    paramInputStream = new BufferedReader(new InputStreamReader(paramInputStream, "UTF-8"));
    for (;;)
    {
      int i = paramInputStream.read(arrayOfChar);
      if (i == -1) {
        return localStringWriter.toString();
      }
      localStringWriter.write(arrayOfChar, 0, i);
    }
  }
  
  public void close()
  {
    try
    {
      this.mExecutor.shutdown();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  @VisibleForTesting
  File getResourceFile()
  {
    String str = "resource_" + this.mContainerId;
    return new File(this.mContext.getDir("google_tagmanager", 0), str);
  }
  
  /* Error */
  public ResourceUtil.ExpandedResource loadExpandedResourceFromJsonAsset(String paramString)
  {
    // Byte code:
    //   0: new 90	java/lang/StringBuilder
    //   3: dup
    //   4: invokespecial 91	java/lang/StringBuilder:<init>	()V
    //   7: ldc 114
    //   9: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   12: aload_1
    //   13: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   16: invokevirtual 96	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   19: invokestatic 120	com/google/tagmanager/Log:v	(Ljava/lang/String;)V
    //   22: aload_0
    //   23: getfield 32	com/google/tagmanager/ResourceStorageImpl:mContext	Landroid/content/Context;
    //   26: invokevirtual 124	android/content/Context:getAssets	()Landroid/content/res/AssetManager;
    //   29: astore_2
    //   30: aload_2
    //   31: ifnull +28 -> 59
    //   34: aload_2
    //   35: aload_1
    //   36: invokevirtual 130	android/content/res/AssetManager:open	(Ljava/lang/String;)Ljava/io/InputStream;
    //   39: astore_2
    //   40: aload_2
    //   41: astore_3
    //   42: aload_0
    //   43: aload_2
    //   44: invokespecial 132	com/google/tagmanager/ResourceStorageImpl:stringFromInputStream	(Ljava/io/InputStream;)Ljava/lang/String;
    //   47: invokestatic 137	com/google/tagmanager/JsonUtils:expandedResourceFromJsonString	(Ljava/lang/String;)Lcom/google/tagmanager/ResourceUtil$ExpandedResource;
    //   50: astore 4
    //   52: aload_2
    //   53: ifnonnull +13 -> 66
    //   56: aload 4
    //   58: areturn
    //   59: ldc -117
    //   61: invokestatic 142	com/google/tagmanager/Log:w	(Ljava/lang/String;)V
    //   64: aconst_null
    //   65: areturn
    //   66: aload_2
    //   67: invokevirtual 146	java/io/InputStream:close	()V
    //   70: aload 4
    //   72: areturn
    //   73: astore_1
    //   74: aload 4
    //   76: areturn
    //   77: astore_2
    //   78: aconst_null
    //   79: astore_2
    //   80: aload_2
    //   81: astore_3
    //   82: new 90	java/lang/StringBuilder
    //   85: dup
    //   86: invokespecial 91	java/lang/StringBuilder:<init>	()V
    //   89: ldc -108
    //   91: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   94: aload_1
    //   95: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   98: ldc -106
    //   100: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   103: invokevirtual 96	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   106: invokestatic 142	com/google/tagmanager/Log:w	(Ljava/lang/String;)V
    //   109: aload_2
    //   110: ifnonnull +5 -> 115
    //   113: aconst_null
    //   114: areturn
    //   115: aload_2
    //   116: invokevirtual 146	java/io/InputStream:close	()V
    //   119: aconst_null
    //   120: areturn
    //   121: astore_1
    //   122: aconst_null
    //   123: areturn
    //   124: astore_3
    //   125: aconst_null
    //   126: astore_2
    //   127: new 90	java/lang/StringBuilder
    //   130: dup
    //   131: invokespecial 91	java/lang/StringBuilder:<init>	()V
    //   134: ldc -104
    //   136: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   139: aload_1
    //   140: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   143: ldc -102
    //   145: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   148: aload_3
    //   149: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   152: invokevirtual 96	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   155: invokestatic 142	com/google/tagmanager/Log:w	(Ljava/lang/String;)V
    //   158: aload_2
    //   159: ifnonnull +5 -> 164
    //   162: aconst_null
    //   163: areturn
    //   164: aload_2
    //   165: invokevirtual 146	java/io/InputStream:close	()V
    //   168: aconst_null
    //   169: areturn
    //   170: astore_1
    //   171: aconst_null
    //   172: areturn
    //   173: astore_1
    //   174: aconst_null
    //   175: astore_2
    //   176: aload_2
    //   177: ifnonnull +5 -> 182
    //   180: aload_1
    //   181: athrow
    //   182: aload_2
    //   183: invokevirtual 146	java/io/InputStream:close	()V
    //   186: goto -6 -> 180
    //   189: astore_2
    //   190: goto -10 -> 180
    //   193: astore_1
    //   194: aload_3
    //   195: astore_2
    //   196: goto -20 -> 176
    //   199: astore_1
    //   200: goto -24 -> 176
    //   203: astore_3
    //   204: goto -77 -> 127
    //   207: astore_3
    //   208: goto -128 -> 80
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	211	0	this	ResourceStorageImpl
    //   0	211	1	paramString	String
    //   29	38	2	localObject1	Object
    //   77	1	2	localIOException1	IOException
    //   79	104	2	localObject2	Object
    //   189	1	2	localIOException2	IOException
    //   195	1	2	localObject3	Object
    //   41	41	3	localObject4	Object
    //   124	71	3	localJSONException1	org.json.JSONException
    //   203	1	3	localJSONException2	org.json.JSONException
    //   207	1	3	localIOException3	IOException
    //   50	25	4	localExpandedResource	ResourceUtil.ExpandedResource
    // Exception table:
    //   from	to	target	type
    //   66	70	73	java/io/IOException
    //   34	40	77	java/io/IOException
    //   115	119	121	java/io/IOException
    //   34	40	124	org/json/JSONException
    //   164	168	170	java/io/IOException
    //   34	40	173	finally
    //   182	186	189	java/io/IOException
    //   42	52	193	finally
    //   82	109	193	finally
    //   127	158	199	finally
    //   42	52	203	org/json/JSONException
    //   42	52	207	java/io/IOException
  }
  
  /* Error */
  public com.google.analytics.containertag.proto.Serving.Resource loadResourceFromContainerAsset(String paramString)
  {
    // Byte code:
    //   0: new 90	java/lang/StringBuilder
    //   3: dup
    //   4: invokespecial 91	java/lang/StringBuilder:<init>	()V
    //   7: ldc -95
    //   9: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   12: aload_1
    //   13: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   16: invokevirtual 96	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   19: invokestatic 120	com/google/tagmanager/Log:v	(Ljava/lang/String;)V
    //   22: aload_0
    //   23: getfield 32	com/google/tagmanager/ResourceStorageImpl:mContext	Landroid/content/Context;
    //   26: invokevirtual 124	android/content/Context:getAssets	()Landroid/content/res/AssetManager;
    //   29: astore_2
    //   30: aload_2
    //   31: ifnull +58 -> 89
    //   34: aload_2
    //   35: aload_1
    //   36: invokevirtual 130	android/content/res/AssetManager:open	(Ljava/lang/String;)Ljava/io/InputStream;
    //   39: astore_2
    //   40: new 163	java/io/ByteArrayOutputStream
    //   43: dup
    //   44: invokespecial 164	java/io/ByteArrayOutputStream:<init>	()V
    //   47: astore_3
    //   48: aload_2
    //   49: aload_3
    //   50: invokestatic 170	com/google/tagmanager/ResourceUtil:copyStream	(Ljava/io/InputStream;Ljava/io/OutputStream;)V
    //   53: aload_3
    //   54: invokevirtual 174	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   57: invokestatic 180	com/google/analytics/containertag/proto/Serving$Resource:parseFrom	([B)Lcom/google/analytics/containertag/proto/Serving$Resource;
    //   60: astore_3
    //   61: new 90	java/lang/StringBuilder
    //   64: dup
    //   65: invokespecial 91	java/lang/StringBuilder:<init>	()V
    //   68: ldc -74
    //   70: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   73: aload_3
    //   74: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   77: invokevirtual 96	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   80: invokestatic 120	com/google/tagmanager/Log:v	(Ljava/lang/String;)V
    //   83: aload_2
    //   84: invokevirtual 146	java/io/InputStream:close	()V
    //   87: aload_3
    //   88: areturn
    //   89: ldc -72
    //   91: invokestatic 187	com/google/tagmanager/Log:e	(Ljava/lang/String;)V
    //   94: aconst_null
    //   95: areturn
    //   96: astore_2
    //   97: new 90	java/lang/StringBuilder
    //   100: dup
    //   101: invokespecial 91	java/lang/StringBuilder:<init>	()V
    //   104: ldc -108
    //   106: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   109: aload_1
    //   110: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   113: ldc -67
    //   115: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   118: invokevirtual 96	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   121: invokestatic 142	com/google/tagmanager/Log:w	(Ljava/lang/String;)V
    //   124: aconst_null
    //   125: areturn
    //   126: astore_1
    //   127: aload_3
    //   128: areturn
    //   129: astore_3
    //   130: new 90	java/lang/StringBuilder
    //   133: dup
    //   134: invokespecial 91	java/lang/StringBuilder:<init>	()V
    //   137: ldc -65
    //   139: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   142: aload_1
    //   143: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   146: invokevirtual 96	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   149: invokestatic 142	com/google/tagmanager/Log:w	(Ljava/lang/String;)V
    //   152: aload_2
    //   153: invokevirtual 146	java/io/InputStream:close	()V
    //   156: aconst_null
    //   157: areturn
    //   158: astore_1
    //   159: aconst_null
    //   160: areturn
    //   161: astore_1
    //   162: aload_2
    //   163: invokevirtual 146	java/io/InputStream:close	()V
    //   166: aload_1
    //   167: athrow
    //   168: astore_2
    //   169: goto -3 -> 166
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	172	0	this	ResourceStorageImpl
    //   0	172	1	paramString	String
    //   29	55	2	localObject1	Object
    //   96	67	2	localIOException1	IOException
    //   168	1	2	localIOException2	IOException
    //   47	81	3	localObject2	Object
    //   129	1	3	localIOException3	IOException
    // Exception table:
    //   from	to	target	type
    //   34	40	96	java/io/IOException
    //   83	87	126	java/io/IOException
    //   40	83	129	java/io/IOException
    //   152	156	158	java/io/IOException
    //   40	83	161	finally
    //   130	152	161	finally
    //   162	166	168	java/io/IOException
  }
  
  /* Error */
  @VisibleForTesting
  void loadResourceFromDisk()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 196	com/google/tagmanager/ResourceStorageImpl:mCallback	Lcom/google/tagmanager/LoadCallback;
    //   4: ifnull +96 -> 100
    //   7: aload_0
    //   8: getfield 196	com/google/tagmanager/ResourceStorageImpl:mCallback	Lcom/google/tagmanager/LoadCallback;
    //   11: invokeinterface 201 1 0
    //   16: ldc -53
    //   18: invokestatic 120	com/google/tagmanager/Log:v	(Ljava/lang/String;)V
    //   21: invokestatic 209	com/google/tagmanager/PreviewManager:getInstance	()Lcom/google/tagmanager/PreviewManager;
    //   24: invokevirtual 213	com/google/tagmanager/PreviewManager:getPreviewMode	()Lcom/google/tagmanager/PreviewManager$PreviewMode;
    //   27: getstatic 219	com/google/tagmanager/PreviewManager$PreviewMode:CONTAINER	Lcom/google/tagmanager/PreviewManager$PreviewMode;
    //   30: if_acmpne +80 -> 110
    //   33: aload_0
    //   34: getfield 34	com/google/tagmanager/ResourceStorageImpl:mContainerId	Ljava/lang/String;
    //   37: invokestatic 209	com/google/tagmanager/PreviewManager:getInstance	()Lcom/google/tagmanager/PreviewManager;
    //   40: invokevirtual 222	com/google/tagmanager/PreviewManager:getContainerId	()Ljava/lang/String;
    //   43: invokevirtual 228	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   46: ifne +79 -> 125
    //   49: new 230	java/io/FileInputStream
    //   52: dup
    //   53: aload_0
    //   54: invokevirtual 232	com/google/tagmanager/ResourceStorageImpl:getResourceFile	()Ljava/io/File;
    //   57: invokespecial 235	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   60: astore_1
    //   61: new 163	java/io/ByteArrayOutputStream
    //   64: dup
    //   65: invokespecial 164	java/io/ByteArrayOutputStream:<init>	()V
    //   68: astore_2
    //   69: aload_1
    //   70: aload_2
    //   71: invokestatic 170	com/google/tagmanager/ResourceUtil:copyStream	(Ljava/io/InputStream;Ljava/io/OutputStream;)V
    //   74: aload_0
    //   75: getfield 196	com/google/tagmanager/ResourceStorageImpl:mCallback	Lcom/google/tagmanager/LoadCallback;
    //   78: aload_2
    //   79: invokevirtual 174	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   82: invokestatic 240	com/google/tagmanager/proto/Resource$ResourceWithMetadata:parseFrom	([B)Lcom/google/tagmanager/proto/Resource$ResourceWithMetadata;
    //   85: invokeinterface 244 2 0
    //   90: aload_1
    //   91: invokevirtual 245	java/io/FileInputStream:close	()V
    //   94: ldc -9
    //   96: invokestatic 120	com/google/tagmanager/Log:v	(Ljava/lang/String;)V
    //   99: return
    //   100: new 249	java/lang/IllegalStateException
    //   103: dup
    //   104: ldc -5
    //   106: invokespecial 253	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   109: athrow
    //   110: invokestatic 209	com/google/tagmanager/PreviewManager:getInstance	()Lcom/google/tagmanager/PreviewManager;
    //   113: invokevirtual 213	com/google/tagmanager/PreviewManager:getPreviewMode	()Lcom/google/tagmanager/PreviewManager$PreviewMode;
    //   116: getstatic 256	com/google/tagmanager/PreviewManager$PreviewMode:CONTAINER_DEBUG	Lcom/google/tagmanager/PreviewManager$PreviewMode;
    //   119: if_acmpeq -86 -> 33
    //   122: goto -73 -> 49
    //   125: aload_0
    //   126: getfield 196	com/google/tagmanager/ResourceStorageImpl:mCallback	Lcom/google/tagmanager/LoadCallback;
    //   129: getstatic 262	com/google/tagmanager/LoadCallback$Failure:NOT_AVAILABLE	Lcom/google/tagmanager/LoadCallback$Failure;
    //   132: invokeinterface 266 2 0
    //   137: return
    //   138: astore_1
    //   139: ldc_w 268
    //   142: invokestatic 271	com/google/tagmanager/Log:d	(Ljava/lang/String;)V
    //   145: aload_0
    //   146: getfield 196	com/google/tagmanager/ResourceStorageImpl:mCallback	Lcom/google/tagmanager/LoadCallback;
    //   149: getstatic 262	com/google/tagmanager/LoadCallback$Failure:NOT_AVAILABLE	Lcom/google/tagmanager/LoadCallback$Failure;
    //   152: invokeinterface 266 2 0
    //   157: return
    //   158: astore_1
    //   159: ldc_w 273
    //   162: invokestatic 142	com/google/tagmanager/Log:w	(Ljava/lang/String;)V
    //   165: goto -71 -> 94
    //   168: astore_2
    //   169: ldc_w 275
    //   172: invokestatic 142	com/google/tagmanager/Log:w	(Ljava/lang/String;)V
    //   175: aload_0
    //   176: getfield 196	com/google/tagmanager/ResourceStorageImpl:mCallback	Lcom/google/tagmanager/LoadCallback;
    //   179: getstatic 278	com/google/tagmanager/LoadCallback$Failure:IO_ERROR	Lcom/google/tagmanager/LoadCallback$Failure;
    //   182: invokeinterface 266 2 0
    //   187: aload_1
    //   188: invokevirtual 245	java/io/FileInputStream:close	()V
    //   191: goto -97 -> 94
    //   194: astore_1
    //   195: ldc_w 273
    //   198: invokestatic 142	com/google/tagmanager/Log:w	(Ljava/lang/String;)V
    //   201: goto -107 -> 94
    //   204: astore_2
    //   205: aload_1
    //   206: invokevirtual 245	java/io/FileInputStream:close	()V
    //   209: aload_2
    //   210: athrow
    //   211: astore_1
    //   212: ldc_w 273
    //   215: invokestatic 142	com/google/tagmanager/Log:w	(Ljava/lang/String;)V
    //   218: goto -9 -> 209
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	221	0	this	ResourceStorageImpl
    //   60	31	1	localFileInputStream	java.io.FileInputStream
    //   138	1	1	localFileNotFoundException	java.io.FileNotFoundException
    //   158	30	1	localIOException1	IOException
    //   194	12	1	localIOException2	IOException
    //   211	1	1	localIOException3	IOException
    //   68	11	2	localByteArrayOutputStream	java.io.ByteArrayOutputStream
    //   168	1	2	localIOException4	IOException
    //   204	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   49	61	138	java/io/FileNotFoundException
    //   90	94	158	java/io/IOException
    //   61	90	168	java/io/IOException
    //   187	191	194	java/io/IOException
    //   61	90	204	finally
    //   169	187	204	finally
    //   205	209	211	java/io/IOException
  }
  
  public void loadResourceFromDiskInBackground()
  {
    this.mExecutor.execute(new Runnable()
    {
      public void run()
      {
        ResourceStorageImpl.this.loadResourceFromDisk();
      }
    });
  }
  
  /* Error */
  @VisibleForTesting
  boolean saveResourceToDisk(Resource.ResourceWithMetadata paramResourceWithMetadata)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 232	com/google/tagmanager/ResourceStorageImpl:getResourceFile	()Ljava/io/File;
    //   4: astore_3
    //   5: new 290	java/io/FileOutputStream
    //   8: dup
    //   9: aload_3
    //   10: invokespecial 291	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   13: astore_2
    //   14: aload_2
    //   15: aload_1
    //   16: invokestatic 296	com/google/tagmanager/protobuf/nano/MessageNano:toByteArray	(Lcom/google/tagmanager/protobuf/nano/MessageNano;)[B
    //   19: invokevirtual 299	java/io/FileOutputStream:write	([B)V
    //   22: aload_2
    //   23: invokevirtual 300	java/io/FileOutputStream:close	()V
    //   26: iconst_1
    //   27: ireturn
    //   28: astore_1
    //   29: ldc_w 302
    //   32: invokestatic 187	com/google/tagmanager/Log:e	(Ljava/lang/String;)V
    //   35: iconst_0
    //   36: ireturn
    //   37: astore_1
    //   38: ldc_w 304
    //   41: invokestatic 142	com/google/tagmanager/Log:w	(Ljava/lang/String;)V
    //   44: goto -18 -> 26
    //   47: astore_1
    //   48: ldc_w 306
    //   51: invokestatic 142	com/google/tagmanager/Log:w	(Ljava/lang/String;)V
    //   54: aload_3
    //   55: invokevirtual 310	java/io/File:delete	()Z
    //   58: pop
    //   59: aload_2
    //   60: invokevirtual 300	java/io/FileOutputStream:close	()V
    //   63: iconst_0
    //   64: ireturn
    //   65: astore_1
    //   66: ldc_w 304
    //   69: invokestatic 142	com/google/tagmanager/Log:w	(Ljava/lang/String;)V
    //   72: iconst_0
    //   73: ireturn
    //   74: astore_1
    //   75: aload_2
    //   76: invokevirtual 300	java/io/FileOutputStream:close	()V
    //   79: aload_1
    //   80: athrow
    //   81: astore_2
    //   82: ldc_w 304
    //   85: invokestatic 142	com/google/tagmanager/Log:w	(Ljava/lang/String;)V
    //   88: goto -9 -> 79
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	91	0	this	ResourceStorageImpl
    //   0	91	1	paramResourceWithMetadata	Resource.ResourceWithMetadata
    //   13	63	2	localFileOutputStream	java.io.FileOutputStream
    //   81	1	2	localIOException	IOException
    //   4	51	3	localFile	File
    // Exception table:
    //   from	to	target	type
    //   5	14	28	java/io/FileNotFoundException
    //   22	26	37	java/io/IOException
    //   14	22	47	java/io/IOException
    //   59	63	65	java/io/IOException
    //   14	22	74	finally
    //   48	59	74	finally
    //   75	79	81	java/io/IOException
  }
  
  public void saveResourceToDiskInBackground(final Resource.ResourceWithMetadata paramResourceWithMetadata)
  {
    this.mExecutor.execute(new Runnable()
    {
      public void run()
      {
        ResourceStorageImpl.this.saveResourceToDisk(paramResourceWithMetadata);
      }
    });
  }
  
  public void setLoadCallback(LoadCallback<Resource.ResourceWithMetadata> paramLoadCallback)
  {
    this.mCallback = paramLoadCallback;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\ResourceStorageImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */