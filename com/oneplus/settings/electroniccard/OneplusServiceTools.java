package com.oneplus.settings.electroniccard;

import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class OneplusServiceTools
{
  public static final String ElectronicCardUrl = "https://api.oneplus.cn/service/router?method=%s&format=%s&access_token=%s&imei=%s&currentLanguage=%s";
  public static final String OpenBaseUri = "https://api.oneplus.cn/";
  public static final String ServiceUrl = "https://api.oneplus.cn/service/router?method=%s&format=%s&access_token=%s";
  private static final String TAG = "OneplusServiceTools";
  public static final String TokenUrl = "https://api.oneplus.cn/oauth/token?client_id=%s&grant_type=%s&scope=%s&client_secret=%s";
  
  public static String elecEnsurance(String paramString)
  {
    String str = getAccessToken(6);
    if (str == null) {
      return null;
    }
    paramString = getHttpPostResp(String.format("https://api.oneplus.cn/service/router?method=%s&format=%s&access_token=%s&imei=%s&currentLanguage=%s", new Object[] { "open.CS.km.getWarrantyInfo", "json", str, paramString, "en_US" }), OPHutil.$m(new Object[] { "imei", paramString, "currentLanguage", "en_US" }));
    if (paramString == null) {
      return null;
    }
    Log.i("OneplusServiceTools", "elecEnsurance--resp:" + paramString);
    return paramString;
  }
  
  public static String getAccessToken(int paramInt)
  {
    Object localObject = OPScopeInfoGen.getScopeInfo(paramInt);
    localObject = String.format("https://api.oneplus.cn/oauth/token?client_id=%s&grant_type=%s&scope=%s&client_secret=%s", new Object[] { localObject[0], localObject[1], localObject[2], localObject[3] });
    Log.d("OneplusServiceTools", "getAccessToken type = " + paramInt + ", url = " + (String)localObject);
    localObject = getHttpPostResp((String)localObject, null);
    if (localObject == null)
    {
      Log.d("OneplusServiceTools", "getAccessToken resp is null, return null");
      return null;
    }
    localObject = OPTokenInfo.parse((String)localObject);
    if (localObject == null)
    {
      Log.i("OneplusServiceTools", "getAccessToken tokenInfo is null, return null");
      return null;
    }
    Log.i("OneplusServiceTools", "getAccessToken type result = " + ((OPTokenInfo)localObject).getAccessToken());
    return ((OPTokenInfo)localObject).getAccessToken();
  }
  
  public static String getHttpPostResp(String paramString, Map<String, Object> paramMap)
  {
    String str = "none";
    paramMap = str;
    try
    {
      HttpURLConnection localHttpURLConnection = (HttpURLConnection)new URL(paramString).openConnection();
      paramMap = str;
      localHttpURLConnection.setConnectTimeout(10000);
      paramMap = str;
      localHttpURLConnection.setReadTimeout(10000);
      paramMap = str;
      localHttpURLConnection.setDoOutput(true);
      paramMap = str;
      localHttpURLConnection.setDoInput(true);
      paramMap = str;
      localHttpURLConnection.setUseCaches(false);
      paramMap = str;
      localHttpURLConnection.setRequestMethod("POST");
      paramMap = str;
      localHttpURLConnection.addRequestProperty("Accept", "*/*");
      paramMap = str;
      localHttpURLConnection.addRequestProperty("Accept-Encoding", "gzip, deflate");
      paramMap = str;
      localHttpURLConnection.addRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
      paramMap = str;
      localHttpURLConnection.addRequestProperty("Content-Type", "application/json;charset=UTF-8");
      paramMap = str;
      localHttpURLConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0");
      paramMap = str;
      localHttpURLConnection.connect();
      paramMap = str;
      if (localHttpURLConnection.getResponseCode() == 200)
      {
        paramMap = str;
        paramString = streamToString(localHttpURLConnection.getInputStream());
        paramMap = paramString;
        Log.e("OneplusServiceTools", "getHttpPostResp success，result--->" + paramString);
      }
      for (;;)
      {
        paramMap = paramString;
        localHttpURLConnection.disconnect();
        return paramString;
        paramMap = str;
        Log.e("OneplusServiceTools", "getHttpPostResp faild");
        paramString = str;
      }
      return paramMap;
    }
    catch (Exception paramString)
    {
      Log.e("OneplusServiceTools", paramString.toString());
    }
  }
  
  public static String streamToString(InputStream paramInputStream)
  {
    ByteArrayOutputStream localByteArrayOutputStream;
    try
    {
      localByteArrayOutputStream = new ByteArrayOutputStream();
      byte[] arrayOfByte = new byte['Ѐ'];
      for (;;)
      {
        int i = paramInputStream.read(arrayOfByte);
        if (i == -1) {
          break;
        }
        localByteArrayOutputStream.write(arrayOfByte, 0, i);
      }
      localByteArrayOutputStream.close();
    }
    catch (Exception paramInputStream)
    {
      Log.e("OneplusServiceTools", paramInputStream.toString());
      return null;
    }
    paramInputStream.close();
    paramInputStream = new String(localByteArrayOutputStream.toByteArray());
    return paramInputStream;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\electroniccard\OneplusServiceTools.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */