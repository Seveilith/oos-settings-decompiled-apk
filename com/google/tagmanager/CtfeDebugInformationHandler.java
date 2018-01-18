package com.google.tagmanager;

import com.google.analytics.containertag.proto.Debug.DebugEvents;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.tagmanager.protobuf.nano.MessageNano;
import java.io.IOException;

class CtfeDebugInformationHandler
  implements DebugInformationHandler
{
  @VisibleForTesting
  static final String CTFE_URL_PATH_PREFIX = "/d?";
  @VisibleForTesting
  static final int NUM_EVENTS_PER_SEND = 1;
  private int currentDebugEventNumber;
  private NetworkClient mClient;
  private CtfeHost mCtfeHost;
  private Debug.DebugEvents mDebugEvents;
  
  public CtfeDebugInformationHandler(CtfeHost paramCtfeHost)
  {
    this(new NetworkClientFactory().createNetworkClient(), paramCtfeHost);
  }
  
  @VisibleForTesting
  CtfeDebugInformationHandler(NetworkClient paramNetworkClient, CtfeHost paramCtfeHost)
  {
    this.mCtfeHost = paramCtfeHost;
    this.mClient = paramNetworkClient;
    this.mDebugEvents = new Debug.DebugEvents();
  }
  
  private byte[] getDebugEventsAsBytes()
    throws IOException
  {
    return MessageNano.toByteArray(this.mDebugEvents);
  }
  
  private boolean sendDebugInformationtoCtfe()
  {
    try
    {
      NetworkClient localNetworkClient = this.mClient;
      CtfeHost localCtfeHost = this.mCtfeHost;
      int i = this.currentDebugEventNumber;
      this.currentDebugEventNumber = (i + 1);
      localNetworkClient.sendPostRequest(localCtfeHost.constructCtfeDebugUrl(i), getDebugEventsAsBytes());
      return true;
    }
    catch (IOException localIOException)
    {
      Log.e("CtfeDebugInformationHandler: Error sending information to server that handles debug information: " + localIOException.getMessage());
    }
    return false;
  }
  
  /* Error */
  public void receiveEventInfo(com.google.analytics.containertag.proto.Debug.EventInfo paramEventInfo)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 46	com/google/tagmanager/CtfeDebugInformationHandler:mDebugEvents	Lcom/google/analytics/containertag/proto/Debug$DebugEvents;
    //   6: aload_0
    //   7: getfield 46	com/google/tagmanager/CtfeDebugInformationHandler:mDebugEvents	Lcom/google/analytics/containertag/proto/Debug$DebugEvents;
    //   10: getfield 104	com/google/analytics/containertag/proto/Debug$DebugEvents:event	[Lcom/google/analytics/containertag/proto/Debug$EventInfo;
    //   13: aload_1
    //   14: invokestatic 110	com/google/tagmanager/ArrayUtils:appendToArray	([Lcom/google/analytics/containertag/proto/Debug$EventInfo;Lcom/google/analytics/containertag/proto/Debug$EventInfo;)[Lcom/google/analytics/containertag/proto/Debug$EventInfo;
    //   17: putfield 104	com/google/analytics/containertag/proto/Debug$DebugEvents:event	[Lcom/google/analytics/containertag/proto/Debug$EventInfo;
    //   20: aload_0
    //   21: getfield 46	com/google/tagmanager/CtfeDebugInformationHandler:mDebugEvents	Lcom/google/analytics/containertag/proto/Debug$DebugEvents;
    //   24: getfield 104	com/google/analytics/containertag/proto/Debug$DebugEvents:event	[Lcom/google/analytics/containertag/proto/Debug$EventInfo;
    //   27: arraylength
    //   28: istore_2
    //   29: iload_2
    //   30: iconst_1
    //   31: if_icmpge +6 -> 37
    //   34: aload_0
    //   35: monitorexit
    //   36: return
    //   37: aload_0
    //   38: invokespecial 112	com/google/tagmanager/CtfeDebugInformationHandler:sendDebugInformationtoCtfe	()Z
    //   41: ifeq -7 -> 34
    //   44: aload_0
    //   45: getfield 46	com/google/tagmanager/CtfeDebugInformationHandler:mDebugEvents	Lcom/google/analytics/containertag/proto/Debug$DebugEvents;
    //   48: invokevirtual 116	com/google/analytics/containertag/proto/Debug$DebugEvents:clear	()Lcom/google/analytics/containertag/proto/Debug$DebugEvents;
    //   51: pop
    //   52: goto -18 -> 34
    //   55: astore_1
    //   56: aload_0
    //   57: monitorexit
    //   58: aload_1
    //   59: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	60	0	this	CtfeDebugInformationHandler
    //   0	60	1	paramEventInfo	com.google.analytics.containertag.proto.Debug.EventInfo
    //   28	4	2	i	int
    // Exception table:
    //   from	to	target	type
    //   2	29	55	finally
    //   37	52	55	finally
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\CtfeDebugInformationHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */