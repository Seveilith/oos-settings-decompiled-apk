package com.google.tagmanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Build.VERSION;
import com.google.android.gms.common.util.VisibleForTesting;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;

class SimpleNetworkDispatcher
  implements Dispatcher
{
  private static final String USER_AGENT_TEMPLATE = "%s/%s (Linux; U; Android %s; %s; %s Build/%s)";
  private final Context ctx;
  private DispatchListener dispatchListener;
  private final HttpClient httpClient;
  private final String userAgent;
  
  @VisibleForTesting
  SimpleNetworkDispatcher(HttpClient paramHttpClient, Context paramContext, DispatchListener paramDispatchListener)
  {
    this.ctx = paramContext.getApplicationContext();
    this.userAgent = createUserAgentString("GoogleTagManager", "3.02", Build.VERSION.RELEASE, getUserAgentLanguage(Locale.getDefault()), Build.MODEL, Build.ID);
    this.httpClient = paramHttpClient;
    this.dispatchListener = paramDispatchListener;
  }
  
  private HttpEntityEnclosingRequest constructGtmRequest(URL paramURL)
  {
    try
    {
      localBasicHttpEntityEnclosingRequest = new BasicHttpEntityEnclosingRequest("GET", paramURL.toURI().toString());
      Log.w("Exception sending hit: " + paramURL.getClass().getSimpleName());
    }
    catch (URISyntaxException paramURL)
    {
      try
      {
        localBasicHttpEntityEnclosingRequest.addHeader("User-Agent", this.userAgent);
        return localBasicHttpEntityEnclosingRequest;
      }
      catch (URISyntaxException paramURL)
      {
        BasicHttpEntityEnclosingRequest localBasicHttpEntityEnclosingRequest;
        for (;;) {}
      }
      paramURL = paramURL;
      localBasicHttpEntityEnclosingRequest = null;
    }
    Log.w(paramURL.getMessage());
    return localBasicHttpEntityEnclosingRequest;
  }
  
  static String getUserAgentLanguage(Locale paramLocale)
  {
    if (paramLocale != null) {
      if (paramLocale.getLanguage() != null) {
        break label15;
      }
    }
    label15:
    while (paramLocale.getLanguage().length() == 0)
    {
      return null;
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramLocale.getLanguage().toLowerCase());
    if (paramLocale.getCountry() == null) {}
    for (;;)
    {
      return localStringBuilder.toString();
      if (paramLocale.getCountry().length() != 0) {
        localStringBuilder.append("-").append(paramLocale.getCountry().toLowerCase());
      }
    }
  }
  
  private void logDebugInformation(HttpEntityEnclosingRequest paramHttpEntityEnclosingRequest)
  {
    int i = 0;
    StringBuffer localStringBuffer = new StringBuffer();
    Object localObject = paramHttpEntityEnclosingRequest.getAllHeaders();
    int j = localObject.length;
    if (i >= j)
    {
      localStringBuffer.append(paramHttpEntityEnclosingRequest.getRequestLine().toString()).append("\n");
      if (paramHttpEntityEnclosingRequest.getEntity() != null) {
        break label91;
      }
    }
    for (;;)
    {
      Log.v(localStringBuffer.toString());
      return;
      localStringBuffer.append(localObject[i].toString()).append("\n");
      i += 1;
      break;
      try
      {
        label91:
        paramHttpEntityEnclosingRequest = paramHttpEntityEnclosingRequest.getEntity().getContent();
        if (paramHttpEntityEnclosingRequest != null)
        {
          i = paramHttpEntityEnclosingRequest.available();
          if (i > 0)
          {
            localObject = new byte[i];
            paramHttpEntityEnclosingRequest.read((byte[])localObject);
            localStringBuffer.append("POST:\n");
            localStringBuffer.append(new String((byte[])localObject)).append("\n");
          }
        }
      }
      catch (IOException paramHttpEntityEnclosingRequest)
      {
        Log.v("Error Writing hit to log...");
      }
    }
  }
  
  public void close()
  {
    this.httpClient.getConnectionManager().shutdown();
  }
  
  String createUserAgentString(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6)
  {
    return String.format("%s/%s (Linux; U; Android %s; %s; %s Build/%s)", new Object[] { paramString1, paramString2, paramString3, paramString4, paramString5, paramString6 });
  }
  
  public void dispatchHits(List<Hit> paramList)
  {
    int n = Math.min(paramList.size(), 40);
    i = 1;
    int m = 0;
    if (m >= n) {
      return;
    }
    localHit = (Hit)paramList.get(m);
    Object localObject2 = getUrl(localHit);
    Object localObject1;
    if (localObject2 != null)
    {
      localObject1 = constructGtmRequest((URL)localObject2);
      if (localObject1 == null) {
        break label225;
      }
      localObject2 = new HttpHost(((URL)localObject2).getHost(), ((URL)localObject2).getPort(), ((URL)localObject2).getProtocol());
      ((HttpEntityEnclosingRequest)localObject1).addHeader("Host", ((HttpHost)localObject2).toHostString());
      logDebugInformation((HttpEntityEnclosingRequest)localObject1);
      if (i != 0) {
        break label239;
      }
      label114:
      j = i;
      k = i;
    }
    for (;;)
    {
      try
      {
        localObject1 = this.httpClient.execute((HttpHost)localObject2, (HttpRequest)localObject1);
        j = i;
        k = i;
        int i1 = ((HttpResponse)localObject1).getStatusLine().getStatusCode();
        j = i;
        k = i;
        localObject2 = ((HttpResponse)localObject1).getEntity();
        if (localObject2 != null) {
          continue;
        }
        if (i1 != 200) {
          continue;
        }
        j = i;
        k = i;
        this.dispatchListener.onHitDispatched(localHit);
      }
      catch (ClientProtocolException localClientProtocolException)
      {
        Log.w("ClientProtocolException sending hit; discarding hit...");
        this.dispatchListener.onHitPermanentDispatchFailure(localHit);
        i = j;
        continue;
        j = i;
        k = i;
        Log.w("Bad response: " + localClientProtocolException.getStatusLine().getStatusCode());
        j = i;
        k = i;
        this.dispatchListener.onHitTransientDispatchFailure(localHit);
        continue;
      }
      catch (IOException localIOException)
      {
        label225:
        label239:
        Log.w("Exception sending hit: " + localIOException.getClass().getSimpleName());
        Log.w(localIOException.getMessage());
        this.dispatchListener.onHitTransientDispatchFailure(localHit);
        i = k;
        continue;
      }
      m += 1;
      break;
      Log.w("No destination: discarding hit.");
      this.dispatchListener.onHitPermanentDispatchFailure(localHit);
      continue;
      this.dispatchListener.onHitPermanentDispatchFailure(localHit);
      continue;
      j = i;
      k = i;
      NetworkReceiver.sendRadioPoweredBroadcast(this.ctx);
      i = 0;
      break label114;
      j = i;
      k = i;
      ((HttpEntity)localObject2).consumeContent();
    }
  }
  
  @VisibleForTesting
  URL getUrl(Hit paramHit)
  {
    paramHit = paramHit.getHitUrl();
    try
    {
      paramHit = new URL(paramHit);
      return paramHit;
    }
    catch (MalformedURLException paramHit)
    {
      Log.e("Error trying to parse the GTM url.");
    }
    return null;
  }
  
  public boolean okToDispatch()
  {
    NetworkInfo localNetworkInfo = ((ConnectivityManager)this.ctx.getSystemService("connectivity")).getActiveNetworkInfo();
    if (localNetworkInfo == null) {}
    while (!localNetworkInfo.isConnected())
    {
      Log.v("...no network connectivity");
      return false;
    }
    return true;
  }
  
  public static abstract interface DispatchListener
  {
    public abstract void onHitDispatched(Hit paramHit);
    
    public abstract void onHitPermanentDispatchFailure(Hit paramHit);
    
    public abstract void onHitTransientDispatchFailure(Hit paramHit);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\SimpleNetworkDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */