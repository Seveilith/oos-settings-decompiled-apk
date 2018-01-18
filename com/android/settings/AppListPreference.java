package com.android.settings;

import android.app.AlertDialog.Builder;
import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class AppListPreference
  extends CustomListPreference
{
  public static final String ITEM_NONE_VALUE = "";
  public static final int TYPE_COMPONENT = 1;
  public static final int TYPE_PACKAGE = 0;
  private Drawable[] mEntryDrawables;
  protected final boolean mForWork;
  private boolean mShowItemNone = false;
  private CharSequence[] mSummaries;
  private int mSystemAppIndex = -1;
  private int mType = 0;
  protected final int mUserId;
  
  public AppListPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mForWork = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.WorkPreference, 0, 0).getBoolean(0, false);
    paramContext = Utils.getManagedProfile(UserManager.get(paramContext));
    if ((this.mForWork) && (paramContext != null)) {}
    for (int i = paramContext.getIdentifier();; i = UserHandle.myUserId())
    {
      this.mUserId = i;
      return;
    }
  }
  
  public AppListPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    this.mForWork = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.WorkPreference, 0, 0).getBoolean(0, false);
    paramContext = Utils.getManagedProfile(UserManager.get(paramContext));
    if ((this.mForWork) && (paramContext != null)) {}
    for (paramInt1 = paramContext.getIdentifier();; paramInt1 = UserHandle.myUserId())
    {
      this.mUserId = paramInt1;
      return;
    }
  }
  
  protected ListAdapter createListAdapter()
  {
    String str = getValue();
    boolean bool;
    if (str != null)
    {
      if (!this.mShowItemNone) {
        break label58;
      }
      bool = str.contentEquals("");
      if (!bool) {
        break label63;
      }
    }
    label58:
    label63:
    for (int i = -1;; i = findIndexOfValue(str))
    {
      return new AppArrayAdapter(getContext(), 2130968619, getEntries(), this.mEntryDrawables, i);
      bool = true;
      break;
      bool = false;
      break;
    }
  }
  
  protected CharSequence getSoleAppLabel()
  {
    return null;
  }
  
  protected void onPrepareDialogBuilder(AlertDialog.Builder paramBuilder, DialogInterface.OnClickListener paramOnClickListener)
  {
    paramBuilder.setAdapter(createListAdapter(), paramOnClickListener);
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if ((paramParcelable instanceof SavedState))
    {
      paramParcelable = (SavedState)paramParcelable;
      this.mShowItemNone = paramParcelable.showItemNone;
      if (this.mType == 0) {
        setPackageNames(paramParcelable.entryValues, paramParcelable.value);
      }
      for (;;)
      {
        this.mSummaries = paramParcelable.summaries;
        super.onRestoreInstanceState(paramParcelable.superState);
        return;
        if (this.mType == 1)
        {
          int j = paramParcelable.entryValues.length;
          ComponentName[] arrayOfComponentName = new ComponentName[j];
          int i = 0;
          while (i < j)
          {
            arrayOfComponentName[i] = ComponentName.unflattenFromString(paramParcelable.entryValues[i] + "");
            i += 1;
          }
          setComponentNames(arrayOfComponentName, ComponentName.unflattenFromString(paramParcelable.value + ""));
        }
      }
    }
    super.onRestoreInstanceState(paramParcelable);
  }
  
  protected Parcelable onSaveInstanceState()
  {
    Parcelable localParcelable = super.onSaveInstanceState();
    return new SavedState(getEntryValues(), getValue(), this.mSummaries, this.mShowItemNone, localParcelable);
  }
  
  public void setComponentNames(ComponentName[] paramArrayOfComponentName, ComponentName paramComponentName)
  {
    setComponentNames(paramArrayOfComponentName, paramComponentName, null);
  }
  
  public void setComponentNames(ComponentName[] paramArrayOfComponentName, ComponentName paramComponentName, CharSequence[] paramArrayOfCharSequence)
  {
    this.mSummaries = paramArrayOfCharSequence;
    this.mType = 1;
    paramArrayOfCharSequence = getContext().getPackageManager();
    int j = paramArrayOfComponentName.length;
    if (this.mShowItemNone) {}
    for (int i = 1;; i = 0)
    {
      i = j + i;
      ArrayList localArrayList1 = new ArrayList(i);
      ArrayList localArrayList2 = new ArrayList(i);
      ArrayList localArrayList3 = new ArrayList(i);
      j = -1;
      i = 0;
      for (;;)
      {
        if (i < paramArrayOfComponentName.length) {}
        try
        {
          ActivityInfo localActivityInfo = AppGlobals.getPackageManager().getActivityInfo(paramArrayOfComponentName[i], 0, this.mUserId);
          if (localActivityInfo == null)
          {
            k = j;
          }
          else
          {
            localArrayList1.add(localActivityInfo.loadLabel(paramArrayOfCharSequence));
            localArrayList2.add(paramArrayOfComponentName[i].flattenToString());
            localArrayList3.add(localActivityInfo.loadIcon(paramArrayOfCharSequence));
            k = j;
            if (paramComponentName != null)
            {
              boolean bool = paramArrayOfComponentName[i].equals(paramComponentName);
              k = j;
              if (bool) {
                k = i;
              }
            }
          }
        }
        catch (RemoteException localRemoteException)
        {
          int k = j;
          i += 1;
          j = k;
        }
        if (this.mShowItemNone)
        {
          localArrayList1.add(getContext().getResources().getText(2131693528));
          localArrayList2.add("");
          localArrayList3.add(getContext().getDrawable(2130838024));
        }
        setEntries((CharSequence[])localArrayList1.toArray(new CharSequence[localArrayList1.size()]));
        setEntryValues((CharSequence[])localArrayList2.toArray(new CharSequence[localArrayList2.size()]));
        this.mEntryDrawables = ((Drawable[])localArrayList3.toArray(new Drawable[localArrayList3.size()]));
        if (j != -1)
        {
          setValueIndex(j);
          return;
        }
        setValue(null);
        return;
      }
    }
  }
  
  public void setPackageNames(CharSequence[] paramArrayOfCharSequence, CharSequence paramCharSequence)
  {
    setPackageNames(paramArrayOfCharSequence, paramCharSequence, null);
  }
  
  public void setPackageNames(CharSequence[] paramArrayOfCharSequence, CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    PackageManager localPackageManager = getContext().getPackageManager();
    int j = paramArrayOfCharSequence.length;
    if (this.mShowItemNone) {}
    ArrayList localArrayList1;
    ArrayList localArrayList2;
    ArrayList localArrayList3;
    for (i = 1;; i = 0)
    {
      i = j + i;
      localArrayList1 = new ArrayList(i);
      localArrayList2 = new ArrayList(i);
      localArrayList3 = new ArrayList(i);
      i = -1;
      this.mSystemAppIndex = -1;
      int k = 0;
      while (k < paramArrayOfCharSequence.length)
      {
        m = i;
        try
        {
          ApplicationInfo localApplicationInfo = localPackageManager.getApplicationInfoAsUser(paramArrayOfCharSequence[k].toString(), 0, this.mUserId);
          m = i;
          localArrayList1.add(localApplicationInfo.loadLabel(localPackageManager));
          m = i;
          localArrayList2.add(localApplicationInfo.packageName);
          m = i;
          localArrayList3.add(localApplicationInfo.loadIcon(localPackageManager));
          j = i;
          if (paramCharSequence1 != null)
          {
            j = i;
            m = i;
            if (localApplicationInfo.packageName.contentEquals(paramCharSequence1)) {
              j = k;
            }
          }
          i = j;
          m = j;
          if (localApplicationInfo.packageName != null)
          {
            i = j;
            if (paramCharSequence2 != null)
            {
              i = j;
              m = j;
              if (localApplicationInfo.packageName.contentEquals(paramCharSequence2))
              {
                m = j;
                this.mSystemAppIndex = k;
                i = j;
              }
            }
          }
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          for (;;)
          {
            i = m;
          }
        }
        k += 1;
      }
    }
    if (this.mShowItemNone)
    {
      localArrayList1.add(getContext().getResources().getText(2131693528));
      localArrayList2.add("");
      localArrayList3.add(getContext().getDrawable(2130838024));
    }
    setEntries((CharSequence[])localArrayList1.toArray(new CharSequence[localArrayList1.size()]));
    setEntryValues((CharSequence[])localArrayList2.toArray(new CharSequence[localArrayList2.size()]));
    this.mEntryDrawables = ((Drawable[])localArrayList3.toArray(new Drawable[localArrayList3.size()]));
    if (i != -1)
    {
      setValueIndex(i);
      return;
    }
    setValue(null);
  }
  
  public void setShowItemNone(boolean paramBoolean)
  {
    this.mShowItemNone = paramBoolean;
  }
  
  protected void setSoleAppLabelAsSummary()
  {
    CharSequence localCharSequence = getSoleAppLabel();
    if (!TextUtils.isEmpty(localCharSequence)) {
      setSummary(localCharSequence);
    }
  }
  
  public class AppArrayAdapter
    extends ArrayAdapter<CharSequence>
  {
    private Drawable[] mImageDrawables = null;
    private int mSelectedIndex = 0;
    
    public AppArrayAdapter(Context paramContext, int paramInt1, CharSequence[] paramArrayOfCharSequence, Drawable[] paramArrayOfDrawable, int paramInt2)
    {
      super(paramInt1, paramArrayOfCharSequence);
      this.mSelectedIndex = paramInt2;
      this.mImageDrawables = paramArrayOfDrawable;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      boolean bool2 = true;
      paramView = LayoutInflater.from(getContext()).inflate(2130968619, paramViewGroup, false);
      ((TextView)paramView.findViewById(16908310)).setText((CharSequence)getItem(paramInt));
      if ((paramInt == this.mSelectedIndex) && (paramInt == AppListPreference.-get1(AppListPreference.this)))
      {
        paramView.findViewById(2131361966).setVisibility(0);
        ((ImageView)paramView.findViewById(16908294)).setImageDrawable(this.mImageDrawables[paramInt]);
        bool1 = bool2;
        if (AppListPreference.-get0(AppListPreference.this) != null) {
          if (AppListPreference.-get0(AppListPreference.this)[paramInt] != null) {
            break label201;
          }
        }
      }
      label201:
      for (boolean bool1 = bool2;; bool1 = false)
      {
        paramView.setEnabled(bool1);
        if (!bool1)
        {
          paramViewGroup = (TextView)paramView.findViewById(16908304);
          paramViewGroup.setText(AppListPreference.-get0(AppListPreference.this)[paramInt]);
          paramViewGroup.setVisibility(0);
        }
        return paramView;
        if (paramInt == this.mSelectedIndex)
        {
          paramView.findViewById(2131361964).setVisibility(0);
          break;
        }
        if (paramInt != AppListPreference.-get1(AppListPreference.this)) {
          break;
        }
        paramView.findViewById(2131361965).setVisibility(0);
        break;
      }
    }
    
    public boolean isEnabled(int paramInt)
    {
      return (AppListPreference.-get0(AppListPreference.this) == null) || (AppListPreference.-get0(AppListPreference.this)[paramInt] == null);
    }
  }
  
  private static class SavedState
    implements Parcelable
  {
    public static Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public AppListPreference.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        CharSequence[] arrayOfCharSequence = paramAnonymousParcel.readCharSequenceArray();
        CharSequence localCharSequence = paramAnonymousParcel.readCharSequence();
        if (paramAnonymousParcel.readInt() != 0) {}
        for (boolean bool = true;; bool = false)
        {
          Parcelable localParcelable = paramAnonymousParcel.readParcelable(getClass().getClassLoader());
          return new AppListPreference.SavedState(arrayOfCharSequence, localCharSequence, paramAnonymousParcel.readCharSequenceArray(), bool, localParcelable);
        }
      }
      
      public AppListPreference.SavedState[] newArray(int paramAnonymousInt)
      {
        return new AppListPreference.SavedState[paramAnonymousInt];
      }
    };
    public final CharSequence[] entryValues;
    public final boolean showItemNone;
    public final CharSequence[] summaries;
    public final Parcelable superState;
    public final CharSequence value;
    
    public SavedState(CharSequence[] paramArrayOfCharSequence1, CharSequence paramCharSequence, CharSequence[] paramArrayOfCharSequence2, boolean paramBoolean, Parcelable paramParcelable)
    {
      this.entryValues = paramArrayOfCharSequence1;
      this.value = paramCharSequence;
      this.showItemNone = paramBoolean;
      this.superState = paramParcelable;
      this.summaries = paramArrayOfCharSequence2;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeCharSequenceArray(this.entryValues);
      paramParcel.writeCharSequence(this.value);
      if (this.showItemNone) {}
      for (int i = 1;; i = 0)
      {
        paramParcel.writeInt(i);
        paramParcel.writeParcelable(this.superState, paramInt);
        paramParcel.writeCharSequenceArray(this.summaries);
        return;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\AppListPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */