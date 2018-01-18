package com.oneplus.settings.electroniccard;

import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class OPTokenInfo
{
  private String accessToken;
  private Long expiresIn;
  private String scope;
  private String tokenType;
  
  public static OPTokenInfo parse(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return null;
    }
    OPTokenInfo localOPTokenInfo = new OPTokenInfo();
    try
    {
      Object localObject = new JSONObject(paramString);
      paramString = ((JSONObject)localObject).getString("access_token");
      String str = ((JSONObject)localObject).getString("token_type");
      long l = ((JSONObject)localObject).getLong("expires_in");
      localObject = ((JSONObject)localObject).getString("scope");
      localOPTokenInfo.setAccessToken(paramString);
      localOPTokenInfo.setTokenType(str);
      localOPTokenInfo.setExpiresIn(Long.valueOf(l));
      localOPTokenInfo.setScope((String)localObject);
      return localOPTokenInfo;
    }
    catch (JSONException paramString)
    {
      paramString.printStackTrace();
    }
    return localOPTokenInfo;
  }
  
  public String getAccessToken()
  {
    return this.accessToken;
  }
  
  public Long getExpiresIn()
  {
    return this.expiresIn;
  }
  
  public String getScope()
  {
    return this.scope;
  }
  
  public String getTokenType()
  {
    return this.tokenType;
  }
  
  public void setAccessToken(String paramString)
  {
    this.accessToken = paramString;
  }
  
  public void setExpiresIn(Long paramLong)
  {
    this.expiresIn = paramLong;
  }
  
  public void setScope(String paramString)
  {
    this.scope = paramString;
  }
  
  public void setTokenType(String paramString)
  {
    this.tokenType = paramString;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\electroniccard\OPTokenInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */