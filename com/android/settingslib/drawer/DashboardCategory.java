package com.android.settingslib.drawer;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

public class DashboardCategory
  implements Parcelable
{
  public static final Parcelable.Creator<DashboardCategory> CREATOR = new Parcelable.Creator()
  {
    public DashboardCategory createFromParcel(Parcel paramAnonymousParcel)
    {
      return new DashboardCategory(paramAnonymousParcel);
    }
    
    public DashboardCategory[] newArray(int paramAnonymousInt)
    {
      return new DashboardCategory[paramAnonymousInt];
    }
  };
  public String key;
  public int priority;
  public List<Tile> tiles = new ArrayList();
  public CharSequence title;
  
  public DashboardCategory() {}
  
  DashboardCategory(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
  }
  
  public void addTile(int paramInt, Tile paramTile)
  {
    this.tiles.add(paramInt, paramTile);
  }
  
  public void addTile(Tile paramTile)
  {
    this.tiles.add(paramTile);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public Tile getTile(int paramInt)
  {
    return (Tile)this.tiles.get(paramInt);
  }
  
  public int getTilesCount()
  {
    return this.tiles.size();
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    this.title = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    this.key = paramParcel.readString();
    this.priority = paramParcel.readInt();
    int j = paramParcel.readInt();
    int i = 0;
    while (i < j)
    {
      Tile localTile = (Tile)Tile.CREATOR.createFromParcel(paramParcel);
      this.tiles.add(localTile);
      i += 1;
    }
  }
  
  public void removeTile(int paramInt)
  {
    this.tiles.remove(paramInt);
  }
  
  public void removeTile(Tile paramTile)
  {
    this.tiles.remove(paramTile);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    TextUtils.writeToParcel(this.title, paramParcel, paramInt);
    paramParcel.writeString(this.key);
    paramParcel.writeInt(this.priority);
    int j = this.tiles.size();
    paramParcel.writeInt(j);
    int i = 0;
    while (i < j)
    {
      ((Tile)this.tiles.get(i)).writeToParcel(paramParcel, paramInt);
      i += 1;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\drawer\DashboardCategory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */