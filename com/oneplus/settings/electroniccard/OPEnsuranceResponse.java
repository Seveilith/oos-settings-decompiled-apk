package com.oneplus.settings.electroniccard;

import android.text.TextUtils;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class OPEnsuranceResponse
{
  private static final String TAG = "EnsuranceResponse";
  String errMsg;
  DataBean imeiData;
  int ret;
  
  public static OPEnsuranceResponse parse(String paramString1, String paramString2)
  {
    if (TextUtils.isEmpty(paramString1)) {
      return null;
    }
    OPEnsuranceResponse localOPEnsuranceResponse = new OPEnsuranceResponse();
    for (;;)
    {
      try
      {
        localObject = new JSONObject(paramString1);
        int i = ((JSONObject)localObject).getInt("ret");
        paramString1 = ((JSONObject)localObject).getString("errMsg");
        localOPEnsuranceResponse.ret = i;
        localOPEnsuranceResponse.errMsg = paramString1;
        paramString1 = new DataBean();
        localJSONObject = ((JSONObject)localObject).getJSONObject("data").getJSONObject(paramString2);
        paramString2 = localJSONObject.getString("imei1");
        localObject = localJSONObject.getString("imei2");
        str1 = localJSONObject.getString("orderNo");
        str2 = localJSONObject.getString("countryCode");
        l1 = 0L;
      }
      catch (JSONException paramString1)
      {
        Object localObject;
        JSONObject localJSONObject;
        String str1;
        String str2;
        long l1;
        long l2;
        paramString1.printStackTrace();
        continue;
      }
      try
      {
        l2 = localJSONObject.getLong("warrantyStartTime");
        l1 = l2;
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
    }
    paramString1.imei1 = paramString2;
    paramString1.imei2 = ((String)localObject);
    paramString1.orderNo = str1;
    paramString1.countryCode = str2;
    paramString1.warrantyStartTime = l1;
    localOPEnsuranceResponse.imeiData = paramString1;
    Log.i("EnsuranceResponse", "parse result : " + localOPEnsuranceResponse.toString());
    return localOPEnsuranceResponse;
  }
  
  public long getWarrantyStart()
  {
    if (this.imeiData == null) {
      return 0L;
    }
    return this.imeiData.warrantyStartTime;
  }
  
  public String toString()
  {
    return "EnsuranceResponse{ret=" + this.ret + ", errMsg='" + this.errMsg + '\'' + ", imeiData=" + this.imeiData + '}';
  }
  
  static class DataBean
  {
    String countryCode;
    String imei1;
    String imei2;
    String orderNo;
    public long warrantyStartTime;
    
    public String toString()
    {
      return "DataBean{imei1='" + this.imei1 + '\'' + ", imei2='" + this.imei2 + '\'' + ", orderNo='" + this.orderNo + '\'' + ", countryCode='" + this.countryCode + '\'' + ", warrantyStartTime=" + this.warrantyStartTime + '}';
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\electroniccard\OPEnsuranceResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */