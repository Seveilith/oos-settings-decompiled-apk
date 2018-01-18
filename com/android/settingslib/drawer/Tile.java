package com.android.settingslib.drawer;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.UserHandle;
import android.text.TextUtils;
import java.util.ArrayList;

public class Tile
  implements Parcelable
{
  public static final Parcelable.Creator<Tile> CREATOR = new Parcelable.Creator()
  {
    public Tile createFromParcel(Parcel paramAnonymousParcel)
    {
      return new Tile(paramAnonymousParcel);
    }
    
    public Tile[] newArray(int paramAnonymousInt)
    {
      return new Tile[paramAnonymousInt];
    }
  };
  public String category;
  public Bundle extras;
  public Icon icon;
  public Intent intent;
  public Bundle metaData;
  public int priority;
  public CharSequence summary;
  public CharSequence title;
  public ArrayList<UserHandle> userHandle = new ArrayList();
  
  public Tile() {}
  
  Tile(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    this.title = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    this.summary = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    if (paramParcel.readByte() != 0) {
      this.icon = ((Icon)Icon.CREATOR.createFromParcel(paramParcel));
    }
    if (paramParcel.readByte() != 0) {
      this.intent = ((Intent)Intent.CREATOR.createFromParcel(paramParcel));
    }
    int j = paramParcel.readInt();
    int i = 0;
    while (i < j)
    {
      this.userHandle.add((UserHandle)UserHandle.CREATOR.createFromParcel(paramParcel));
      i += 1;
    }
    this.extras = paramParcel.readBundle();
    this.category = paramParcel.readString();
    this.priority = paramParcel.readInt();
    this.metaData = paramParcel.readBundle();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    TextUtils.writeToParcel(this.title, paramParcel, paramInt);
    TextUtils.writeToParcel(this.summary, paramParcel, paramInt);
    if (this.icon != null)
    {
      paramParcel.writeByte((byte)1);
      this.icon.writeToParcel(paramParcel, paramInt);
      if (this.intent == null) {
        break label114;
      }
      paramParcel.writeByte((byte)1);
      this.intent.writeToParcel(paramParcel, paramInt);
    }
    for (;;)
    {
      int j = this.userHandle.size();
      paramParcel.writeInt(j);
      int i = 0;
      while (i < j)
      {
        ((UserHandle)this.userHandle.get(i)).writeToParcel(paramParcel, paramInt);
        i += 1;
      }
      paramParcel.writeByte((byte)0);
      break;
      label114:
      paramParcel.writeByte((byte)0);
    }
    paramParcel.writeBundle(this.extras);
    paramParcel.writeString(this.category);
    paramParcel.writeInt(this.priority);
    paramParcel.writeBundle(this.metaData);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\drawer\Tile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */