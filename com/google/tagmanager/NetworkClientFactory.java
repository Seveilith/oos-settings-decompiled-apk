package com.google.tagmanager;

import android.os.Build.VERSION;
import com.google.android.gms.common.util.VisibleForTesting;

class NetworkClientFactory
{
  public NetworkClient createNetworkClient()
  {
    if (getSdkVersion() >= 8) {
      return new HttpUrlConnectionNetworkClient();
    }
    return new HttpNetworkClient();
  }
  
  @VisibleForTesting
  int getSdkVersion()
  {
    return Build.VERSION.SDK_INT;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\NetworkClientFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */