package com.google.analytics.tracking.android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import com.google.android.gms.common.util.VisibleForTesting;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
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
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;

class SimpleNetworkDispatcher
  implements Dispatcher
{
  private static final String USER_AGENT_TEMPLATE = "%s/%s (Linux; U; Android %s; %s; %s Build/%s)";
  private final Context ctx;
  private GoogleAnalytics gaInstance;
  private final HttpClient httpClient;
  private URL mOverrideHostUrl;
  private final String userAgent;
  
  SimpleNetworkDispatcher(HttpClient paramHttpClient, Context paramContext)
  {
    this(paramHttpClient, GoogleAnalytics.getInstance(paramContext), paramContext);
  }
  
  @VisibleForTesting
  SimpleNetworkDispatcher(HttpClient paramHttpClient, GoogleAnalytics paramGoogleAnalytics, Context paramContext)
  {
    this.ctx = paramContext.getApplicationContext();
    this.userAgent = createUserAgentString("GoogleAnalytics", "3.0", Build.VERSION.RELEASE, Utils.getLanguage(Locale.getDefault()), Build.MODEL, Build.ID);
    this.httpClient = paramHttpClient;
    this.gaInstance = paramGoogleAnalytics;
  }
  
  private HttpEntityEnclosingRequest buildRequest(String paramString1, String paramString2)
  {
    String str;
    if (!TextUtils.isEmpty(paramString1))
    {
      str = paramString2 + "?" + paramString1;
      if (str.length() < 2036) {
        break label89;
      }
      paramString2 = new BasicHttpEntityEnclosingRequest("POST", paramString2);
    }
    for (;;)
    {
      try
      {
        paramString2.setEntity(new StringEntity(paramString1));
        paramString1 = paramString2;
        paramString1.addHeader("User-Agent", this.userAgent);
        return paramString1;
      }
      catch (UnsupportedEncodingException paramString1)
      {
        label89:
        Log.w("Encoding error, discarding hit");
      }
      Log.w("Empty hit, discarding.");
      return null;
      paramString1 = new BasicHttpEntityEnclosingRequest("GET", str);
    }
    return null;
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
  
  public int dispatchHits(List<Hit> paramList)
  {
    int m = Math.min(paramList.size(), 40);
    int i = 1;
    int k = 0;
    j = 0;
    if (k >= m) {
      return j;
    }
    Object localObject1 = (Hit)paramList.get(k);
    Object localObject3 = getUrl((Hit)localObject1);
    Object localObject2;
    if (localObject3 != null)
    {
      localObject2 = new HttpHost(((URL)localObject3).getHost(), ((URL)localObject3).getPort(), ((URL)localObject3).getProtocol());
      localObject3 = ((URL)localObject3).getPath();
      if (TextUtils.isEmpty(((Hit)localObject1).getHitParams())) {
        break label281;
      }
      localObject1 = HitBuilder.postProcessHit((Hit)localObject1, System.currentTimeMillis());
      label107:
      localObject3 = buildRequest((String)localObject1, (String)localObject3);
      if (localObject3 == null) {
        break label289;
      }
      ((HttpEntityEnclosingRequest)localObject3).addHeader("Host", ((HttpHost)localObject2).toHostString());
      if (Log.isVerbose()) {
        break label296;
      }
      label143:
      if ((((String)localObject1).length() <= 8192) && ((!this.gaInstance.isDryRunEnabled()) && (i != 0))) {
        break label323;
      }
    }
    for (;;)
    {
      try
      {
        localObject1 = this.httpClient.execute((HttpHost)localObject2, (HttpRequest)localObject3);
        int n = ((HttpResponse)localObject1).getStatusLine().getStatusCode();
        localObject2 = ((HttpResponse)localObject1).getEntity();
        if (localObject2 != null) {
          continue;
        }
        if (n != 200) {
          continue;
        }
      }
      catch (ClientProtocolException localClientProtocolException1)
      {
        Log.w("ClientProtocolException sending hit; discarding hit...");
        continue;
        Log.w("Bad response: " + localClientProtocolException1.getStatusLine().getStatusCode());
        continue;
      }
      catch (IOException paramList)
      {
        label281:
        label289:
        label296:
        label323:
        Log.w("Exception sending hit: " + paramList.getClass().getSimpleName());
        Log.w(paramList.getMessage());
        return j;
      }
      j += 1;
      k += 1;
      break;
      if (!Log.isVerbose())
      {
        Log.w("No destination: discarding hit.");
        j += 1;
      }
      else
      {
        Log.w("No destination: discarding hit: " + ((Hit)localObject1).getHitParams());
        continue;
        localObject1 = "";
        break label107;
        j += 1;
        continue;
        logDebugInformation((HttpEntityEnclosingRequest)localObject3);
        break label143;
        Log.w("Hit too long (> 8192 bytes)--not sent");
        continue;
        Log.i("Dry run enabled. Hit not actually sent.");
      }
      try
      {
        GANetworkReceiver.sendRadioPoweredBroadcast(this.ctx);
        i = 0;
      }
      catch (ClientProtocolException localClientProtocolException2)
      {
        continue;
      }
      ((HttpEntity)localObject2).consumeContent();
    }
  }
  
  @VisibleForTesting
  URL getUrl(Hit paramHit)
  {
    if (this.mOverrideHostUrl == null) {
      paramHit = paramHit.getHitUrlScheme();
    }
    try
    {
      if (!"http:".equals(paramHit)) {}
      for (paramHit = "https://ssl.google-analytics.com/collect";; paramHit = "http://www.google-analytics.com/collect")
      {
        paramHit = new URL(paramHit);
        return paramHit;
        return this.mOverrideHostUrl;
      }
      return null;
    }
    catch (MalformedURLException paramHit)
    {
      Log.e("Error trying to parse the hardcoded host url. This really shouldn't happen.");
    }
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
  
  @VisibleForTesting
  public void overrideHostUrl(String paramString)
  {
    try
    {
      this.mOverrideHostUrl = new URL(paramString);
      return;
    }
    catch (MalformedURLException paramString)
    {
      this.mOverrideHostUrl = null;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\analytics\tracking\android\SimpleNetworkDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */