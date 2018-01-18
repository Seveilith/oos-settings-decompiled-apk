package com.android.settings.support;

import android.content.Intent;
import android.net.Uri.Builder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.text.ParseException;

public final class SupportPhone
  implements Parcelable
{
  public static final Parcelable.Creator<SupportPhone> CREATOR = new Parcelable.Creator()
  {
    public SupportPhone createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SupportPhone(paramAnonymousParcel);
    }
    
    public SupportPhone[] newArray(int paramAnonymousInt)
    {
      return new SupportPhone[paramAnonymousInt];
    }
  };
  public final boolean isTollFree;
  public final String language;
  public final String number;
  
  protected SupportPhone(Parcel paramParcel)
  {
    this.language = paramParcel.readString();
    this.number = paramParcel.readString();
    if (paramParcel.readInt() != 0) {
      bool = true;
    }
    this.isTollFree = bool;
  }
  
  public SupportPhone(String paramString)
    throws ParseException
  {
    String[] arrayOfString = paramString.split(":");
    if (arrayOfString.length != 3) {
      throw new ParseException("Phone config is invalid " + paramString, 0);
    }
    this.language = arrayOfString[0];
    this.isTollFree = TextUtils.equals(arrayOfString[1], "tollfree");
    this.number = arrayOfString[2];
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public Intent getDialIntent()
  {
    return new Intent("android.intent.action.DIAL").setData(new Uri.Builder().scheme("tel").appendPath(this.number).build());
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.language);
    paramParcel.writeString(this.number);
    if (this.isTollFree) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\support\SupportPhone.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */