package com.oneplus.settings.electroniccard;

import android.util.Log;

public class OPScopeInfoGen
{
  static boolean isDevEnv = false;
  
  public static String[] getScopeInfo(int paramInt)
  {
    Log.i("OPScopeInfoGen", "gen scopeInfo type = " + paramInt);
    String[] arrayOfString = new String[4];
    arrayOfString[0] = "sales_client";
    arrayOfString[1] = "client_credentials";
    arrayOfString[2] = "write";
    arrayOfString[3] = "30e1872d0c9f5aac488151a4585e564b";
    switch (paramInt)
    {
    }
    for (;;)
    {
      Log.i("OPScopeInfoGen", "gen scopeInfo : client_id = " + arrayOfString[0] + ", grant_type = " + arrayOfString[1] + ", scope = " + arrayOfString[2] + ", client_secret = " + arrayOfString[3]);
      return arrayOfString;
      arrayOfString[0] = "sales_client";
      arrayOfString[1] = "client_credentials";
      arrayOfString[2] = "write";
      arrayOfString[3] = "30e1872d0c9f5aac488151a4585e564b";
      continue;
      arrayOfString[0] = "trade_client";
      arrayOfString[1] = "client_credentials";
      arrayOfString[2] = "read";
      arrayOfString[3] = "fb312879464fd1c306085f18fe4f8d06";
      continue;
      arrayOfString[0] = "sales_client";
      arrayOfString[1] = "client_credentials";
      arrayOfString[2] = "read";
      arrayOfString[3] = "30e1872d0c9f5aac488151a4585e564b";
      continue;
      if (isDevEnv)
      {
        str = "cstest";
        label252:
        arrayOfString[0] = str;
        if (!isDevEnv) {
          break label302;
        }
        label262:
        arrayOfString[1] = "client_credentials";
        if (!isDevEnv) {
          break label305;
        }
        str = "read";
        label276:
        arrayOfString[2] = str;
        if (!isDevEnv) {
          break label311;
        }
      }
      label302:
      label305:
      label311:
      for (String str = "2d65b204c84348549f228b7cc61352bb";; str = "3a985db6112d366392fcbc5bd90c0901")
      {
        arrayOfString[3] = str;
        break;
        str = "CS_gsp";
        break label252;
        break label262;
        str = "gspread";
        break label276;
      }
      arrayOfString[0] = "goodscenter_client";
      arrayOfString[1] = "client_credentials";
      arrayOfString[2] = "read";
      arrayOfString[3] = "5b660d3f269f549423186a7840c3b1a5";
      continue;
      arrayOfString[0] = "wms_rom";
      arrayOfString[1] = "client_credentials";
      arrayOfString[2] = "read";
      arrayOfString[3] = "d605e0937c794339a9e964d50e486238";
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\electroniccard\OPScopeInfoGen.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */