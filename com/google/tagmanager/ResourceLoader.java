package com.google.tagmanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.google.analytics.containertag.proto.Serving.SupplementedResource;
import com.google.android.gms.common.util.VisibleForTesting;

class ResourceLoader
  implements Runnable
{
  private static final String CTFE_URL_PREFIX = "/r?id=";
  private static final String CTFE_URL_SUFFIX = "&v=a62676326";
  private static final String PREVIOUS_CONTAINER_VERSION_QUERY_NAME = "pv";
  @VisibleForTesting
  static final String SDK_VERSION = "a62676326";
  private LoadCallback<Serving.SupplementedResource> mCallback;
  private final NetworkClientFactory mClientFactory;
  private final String mContainerId;
  private final Context mContext;
  private volatile CtfeHost mCtfeHost;
  private volatile String mCtfeUrlPathAndQuery;
  private final String mDefaultCtfeUrlPathAndQuery;
  private volatile String mPreviousVersion;
  
  public ResourceLoader(Context paramContext, String paramString, CtfeHost paramCtfeHost)
  {
    this(paramContext, paramString, new NetworkClientFactory(), paramCtfeHost);
  }
  
  @VisibleForTesting
  ResourceLoader(Context paramContext, String paramString, NetworkClientFactory paramNetworkClientFactory, CtfeHost paramCtfeHost)
  {
    this.mContext = paramContext;
    this.mClientFactory = paramNetworkClientFactory;
    this.mContainerId = paramString;
    this.mCtfeHost = paramCtfeHost;
    this.mDefaultCtfeUrlPathAndQuery = ("/r?id=" + paramString);
    this.mCtfeUrlPathAndQuery = this.mDefaultCtfeUrlPathAndQuery;
    this.mPreviousVersion = null;
  }
  
  /* Error */
  private void loadResource()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 80	com/google/tagmanager/ResourceLoader:okToLoad	()Z
    //   4: ifeq +104 -> 108
    //   7: ldc 82
    //   9: invokestatic 88	com/google/tagmanager/Log:v	(Ljava/lang/String;)V
    //   12: aload_0
    //   13: invokevirtual 91	com/google/tagmanager/ResourceLoader:getCtfeUrl	()Ljava/lang/String;
    //   16: astore_2
    //   17: aload_0
    //   18: getfield 49	com/google/tagmanager/ResourceLoader:mClientFactory	Lcom/google/tagmanager/NetworkClientFactory;
    //   21: invokevirtual 95	com/google/tagmanager/NetworkClientFactory:createNetworkClient	()Lcom/google/tagmanager/NetworkClient;
    //   24: astore_1
    //   25: aload_1
    //   26: aload_2
    //   27: invokeinterface 101 2 0
    //   32: astore_3
    //   33: new 103	java/io/ByteArrayOutputStream
    //   36: dup
    //   37: invokespecial 104	java/io/ByteArrayOutputStream:<init>	()V
    //   40: astore 4
    //   42: aload_3
    //   43: aload 4
    //   45: invokestatic 110	com/google/tagmanager/ResourceUtil:copyStream	(Ljava/io/InputStream;Ljava/io/OutputStream;)V
    //   48: aload 4
    //   50: invokevirtual 114	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   53: invokestatic 120	com/google/analytics/containertag/proto/Serving$SupplementedResource:parseFrom	([B)Lcom/google/analytics/containertag/proto/Serving$SupplementedResource;
    //   56: astore_3
    //   57: new 55	java/lang/StringBuilder
    //   60: dup
    //   61: invokespecial 56	java/lang/StringBuilder:<init>	()V
    //   64: ldc 122
    //   66: invokevirtual 60	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   69: aload_3
    //   70: invokevirtual 125	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   73: invokevirtual 64	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   76: invokestatic 88	com/google/tagmanager/Log:v	(Ljava/lang/String;)V
    //   79: aload_3
    //   80: getfield 129	com/google/analytics/containertag/proto/Serving$SupplementedResource:resource	Lcom/google/analytics/containertag/proto/Serving$Resource;
    //   83: ifnull +152 -> 235
    //   86: aload_0
    //   87: getfield 131	com/google/tagmanager/ResourceLoader:mCallback	Lcom/google/tagmanager/LoadCallback;
    //   90: aload_3
    //   91: invokeinterface 137 2 0
    //   96: aload_1
    //   97: invokeinterface 140 1 0
    //   102: ldc -114
    //   104: invokestatic 88	com/google/tagmanager/Log:v	(Ljava/lang/String;)V
    //   107: return
    //   108: aload_0
    //   109: getfield 131	com/google/tagmanager/ResourceLoader:mCallback	Lcom/google/tagmanager/LoadCallback;
    //   112: getstatic 148	com/google/tagmanager/LoadCallback$Failure:NOT_AVAILABLE	Lcom/google/tagmanager/LoadCallback$Failure;
    //   115: invokeinterface 152 2 0
    //   120: return
    //   121: astore_3
    //   122: new 55	java/lang/StringBuilder
    //   125: dup
    //   126: invokespecial 56	java/lang/StringBuilder:<init>	()V
    //   129: ldc -102
    //   131: invokevirtual 60	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   134: aload_2
    //   135: invokevirtual 60	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   138: ldc -100
    //   140: invokevirtual 60	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   143: aload_0
    //   144: getfield 51	com/google/tagmanager/ResourceLoader:mContainerId	Ljava/lang/String;
    //   147: invokevirtual 60	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   150: ldc -98
    //   152: invokevirtual 60	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   155: invokevirtual 64	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   158: invokestatic 161	com/google/tagmanager/Log:w	(Ljava/lang/String;)V
    //   161: aload_0
    //   162: getfield 131	com/google/tagmanager/ResourceLoader:mCallback	Lcom/google/tagmanager/LoadCallback;
    //   165: getstatic 164	com/google/tagmanager/LoadCallback$Failure:SERVER_ERROR	Lcom/google/tagmanager/LoadCallback$Failure;
    //   168: invokeinterface 152 2 0
    //   173: aload_1
    //   174: invokeinterface 140 1 0
    //   179: return
    //   180: astore_3
    //   181: new 55	java/lang/StringBuilder
    //   184: dup
    //   185: invokespecial 56	java/lang/StringBuilder:<init>	()V
    //   188: ldc -90
    //   190: invokevirtual 60	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   193: aload_2
    //   194: invokevirtual 60	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   197: ldc -88
    //   199: invokevirtual 60	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   202: aload_3
    //   203: invokevirtual 171	java/io/IOException:getMessage	()Ljava/lang/String;
    //   206: invokevirtual 60	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   209: invokevirtual 64	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   212: aload_3
    //   213: invokestatic 174	com/google/tagmanager/Log:w	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   216: aload_0
    //   217: getfield 131	com/google/tagmanager/ResourceLoader:mCallback	Lcom/google/tagmanager/LoadCallback;
    //   220: getstatic 177	com/google/tagmanager/LoadCallback$Failure:IO_ERROR	Lcom/google/tagmanager/LoadCallback$Failure;
    //   223: invokeinterface 152 2 0
    //   228: aload_1
    //   229: invokeinterface 140 1 0
    //   234: return
    //   235: new 55	java/lang/StringBuilder
    //   238: dup
    //   239: invokespecial 56	java/lang/StringBuilder:<init>	()V
    //   242: ldc -77
    //   244: invokevirtual 60	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   247: aload_0
    //   248: getfield 51	com/google/tagmanager/ResourceLoader:mContainerId	Ljava/lang/String;
    //   251: invokevirtual 60	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   254: invokevirtual 64	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   257: invokestatic 88	com/google/tagmanager/Log:v	(Ljava/lang/String;)V
    //   260: goto -174 -> 86
    //   263: astore_3
    //   264: new 55	java/lang/StringBuilder
    //   267: dup
    //   268: invokespecial 56	java/lang/StringBuilder:<init>	()V
    //   271: ldc -75
    //   273: invokevirtual 60	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   276: aload_2
    //   277: invokevirtual 60	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   280: ldc -88
    //   282: invokevirtual 60	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   285: aload_3
    //   286: invokevirtual 171	java/io/IOException:getMessage	()Ljava/lang/String;
    //   289: invokevirtual 60	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   292: invokevirtual 64	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   295: aload_3
    //   296: invokestatic 174	com/google/tagmanager/Log:w	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   299: aload_0
    //   300: getfield 131	com/google/tagmanager/ResourceLoader:mCallback	Lcom/google/tagmanager/LoadCallback;
    //   303: getstatic 164	com/google/tagmanager/LoadCallback$Failure:SERVER_ERROR	Lcom/google/tagmanager/LoadCallback$Failure;
    //   306: invokeinterface 152 2 0
    //   311: aload_1
    //   312: invokeinterface 140 1 0
    //   317: return
    //   318: astore_2
    //   319: aload_1
    //   320: invokeinterface 140 1 0
    //   325: aload_2
    //   326: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	327	0	this	ResourceLoader
    //   24	296	1	localNetworkClient	NetworkClient
    //   16	261	2	str	String
    //   318	8	2	localObject1	Object
    //   32	59	3	localObject2	Object
    //   121	1	3	localFileNotFoundException	java.io.FileNotFoundException
    //   180	33	3	localIOException1	java.io.IOException
    //   263	33	3	localIOException2	java.io.IOException
    //   40	9	4	localByteArrayOutputStream	java.io.ByteArrayOutputStream
    // Exception table:
    //   from	to	target	type
    //   25	33	121	java/io/FileNotFoundException
    //   25	33	180	java/io/IOException
    //   33	86	263	java/io/IOException
    //   86	96	263	java/io/IOException
    //   235	260	263	java/io/IOException
    //   25	33	318	finally
    //   33	86	318	finally
    //   86	96	318	finally
    //   122	173	318	finally
    //   181	228	318	finally
    //   235	260	318	finally
    //   264	311	318	finally
  }
  
  private boolean okToLoad()
  {
    NetworkInfo localNetworkInfo = ((ConnectivityManager)this.mContext.getSystemService("connectivity")).getActiveNetworkInfo();
    if (localNetworkInfo == null) {}
    while (!localNetworkInfo.isConnected())
    {
      Log.v("...no network connectivity");
      return false;
    }
    return true;
  }
  
  @VisibleForTesting
  String getCtfeUrl()
  {
    String str2 = this.mCtfeHost.getCtfeServerAddress() + this.mCtfeUrlPathAndQuery + "&v=a62676326";
    String str1;
    if (this.mPreviousVersion == null) {
      str1 = str2;
    }
    while (!PreviewManager.getInstance().getPreviewMode().equals(PreviewManager.PreviewMode.CONTAINER_DEBUG))
    {
      return str1;
      str1 = str2;
      if (!this.mPreviousVersion.trim().equals("")) {
        str1 = str2 + "&pv=" + this.mPreviousVersion;
      }
    }
    return str1 + "&gtm_debug=x";
  }
  
  public void run()
  {
    if (this.mCallback != null)
    {
      this.mCallback.startLoad();
      loadResource();
      return;
    }
    throw new IllegalStateException("callback must be set before execute");
  }
  
  @VisibleForTesting
  void setCtfeURLPathAndQuery(String paramString)
  {
    if (paramString != null)
    {
      Log.d("Setting CTFE URL path: " + paramString);
      this.mCtfeUrlPathAndQuery = paramString;
      return;
    }
    this.mCtfeUrlPathAndQuery = this.mDefaultCtfeUrlPathAndQuery;
  }
  
  void setLoadCallback(LoadCallback<Serving.SupplementedResource> paramLoadCallback)
  {
    this.mCallback = paramLoadCallback;
  }
  
  @VisibleForTesting
  void setPreviousVersion(String paramString)
  {
    Log.d("Setting previous container version: " + paramString);
    this.mPreviousVersion = paramString;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\ResourceLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */