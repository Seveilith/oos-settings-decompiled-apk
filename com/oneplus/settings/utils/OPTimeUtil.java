package com.oneplus.settings.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OPTimeUtil
{
  public static String UnixTimeRead(long paramLong)
  {
    return new SimpleDateFormat("yyyy/MM/dd HH:mm").format(new Date(1000L * paramLong));
  }
  
  public static String millsTimeRead(long paramLong)
  {
    return new SimpleDateFormat("yyyy/MM/dd HH:mm").format(new Date(paramLong));
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\utils\OPTimeUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */