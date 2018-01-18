package com.oneplus.settings.utils;

public class OPFirewallRule
{
  private static final String TAG = "FirewallRule";
  private Integer id;
  private Integer mobile;
  private String pkg;
  private Integer wlan;
  
  public OPFirewallRule() {}
  
  public OPFirewallRule(Integer paramInteger1, String paramString, Integer paramInteger2, Integer paramInteger3)
  {
    this.id = paramInteger1;
    this.pkg = paramString;
    this.wlan = paramInteger2;
    this.mobile = paramInteger3;
  }
  
  public OPFirewallRule(String paramString, Integer paramInteger1, Integer paramInteger2)
  {
    this.pkg = paramString;
    this.wlan = paramInteger1;
    this.mobile = paramInteger2;
  }
  
  public Integer getId()
  {
    return this.id;
  }
  
  public Integer getMobile()
  {
    return this.mobile;
  }
  
  public String getPkg()
  {
    return this.pkg;
  }
  
  public Integer getWlan()
  {
    return this.wlan;
  }
  
  public void setId(Integer paramInteger)
  {
    this.id = paramInteger;
  }
  
  public void setMobile(Integer paramInteger)
  {
    this.mobile = paramInteger;
  }
  
  public void setPkg(String paramString)
  {
    this.pkg = paramString;
  }
  
  public void setWlan(Integer paramInteger)
  {
    this.wlan = paramInteger;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\utils\OPFirewallRule.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */