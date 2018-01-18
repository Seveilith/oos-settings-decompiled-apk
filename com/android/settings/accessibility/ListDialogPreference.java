package com.android.settings.accessibility;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v7.preference.Preference.BaseSavedState;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import com.android.settings.CustomDialogPreference;

public abstract class ListDialogPreference
  extends CustomDialogPreference
{
  private CharSequence[] mEntryTitles;
  private int[] mEntryValues;
  private int mListItemLayout;
  private OnValueChangedListener mOnValueChangedListener;
  private int mValue;
  private int mValueIndex;
  private boolean mValueSet;
  
  public ListDialogPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  protected int getIndexForValue(int paramInt)
  {
    int[] arrayOfInt = this.mEntryValues;
    if (arrayOfInt != null)
    {
      int j = arrayOfInt.length;
      int i = 0;
      while (i < j)
      {
        if (arrayOfInt[i] == paramInt) {
          return i;
        }
        i += 1;
      }
    }
    return -1;
  }
  
  public CharSequence getSummary()
  {
    if (this.mValueIndex >= 0) {
      return getTitleAt(this.mValueIndex);
    }
    return null;
  }
  
  protected CharSequence getTitleAt(int paramInt)
  {
    if ((this.mEntryTitles == null) || (this.mEntryTitles.length <= paramInt)) {
      return null;
    }
    return this.mEntryTitles[paramInt];
  }
  
  public int getValue()
  {
    return this.mValue;
  }
  
  protected int getValueAt(int paramInt)
  {
    return this.mEntryValues[paramInt];
  }
  
  protected abstract void onBindListItem(View paramView, int paramInt);
  
  protected Object onGetDefaultValue(TypedArray paramTypedArray, int paramInt)
  {
    return Integer.valueOf(paramTypedArray.getInt(paramInt, 0));
  }
  
  protected void onPrepareDialogBuilder(AlertDialog.Builder paramBuilder, DialogInterface.OnClickListener paramOnClickListener)
  {
    super.onPrepareDialogBuilder(paramBuilder, paramOnClickListener);
    paramOnClickListener = getContext();
    int i = getDialogLayoutResource();
    paramOnClickListener = LayoutInflater.from(paramOnClickListener).inflate(i, null);
    ListPreferenceAdapter localListPreferenceAdapter = new ListPreferenceAdapter(null);
    AbsListView localAbsListView = (AbsListView)paramOnClickListener.findViewById(16908298);
    localAbsListView.setAdapter(localListPreferenceAdapter);
    localAbsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        if (ListDialogPreference.this.callChangeListener(Integer.valueOf((int)paramAnonymousLong))) {
          ListDialogPreference.this.setValue((int)paramAnonymousLong);
        }
        paramAnonymousAdapterView = ListDialogPreference.this.getDialog();
        if (paramAnonymousAdapterView != null) {
          paramAnonymousAdapterView.dismiss();
        }
      }
    });
    i = getIndexForValue(this.mValue);
    if (i != -1) {
      localAbsListView.setSelection(i);
    }
    paramBuilder.setView(paramOnClickListener);
    paramBuilder.setPositiveButton(null, null);
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if ((paramParcelable != null) && (paramParcelable.getClass().equals(SavedState.class)))
    {
      paramParcelable = (SavedState)paramParcelable;
      super.onRestoreInstanceState(paramParcelable.getSuperState());
      setValue(paramParcelable.value);
      return;
    }
    super.onRestoreInstanceState(paramParcelable);
  }
  
  protected Parcelable onSaveInstanceState()
  {
    Object localObject = super.onSaveInstanceState();
    if (isPersistent()) {
      return (Parcelable)localObject;
    }
    localObject = new SavedState((Parcelable)localObject);
    ((SavedState)localObject).value = getValue();
    return (Parcelable)localObject;
  }
  
  protected void onSetInitialValue(boolean paramBoolean, Object paramObject)
  {
    if (paramBoolean) {}
    for (int i = getPersistedInt(this.mValue);; i = ((Integer)paramObject).intValue())
    {
      setValue(i);
      return;
    }
  }
  
  public void setListItemLayoutResource(int paramInt)
  {
    this.mListItemLayout = paramInt;
  }
  
  public void setOnValueChangedListener(OnValueChangedListener paramOnValueChangedListener)
  {
    this.mOnValueChangedListener = paramOnValueChangedListener;
  }
  
  public void setTitles(CharSequence[] paramArrayOfCharSequence)
  {
    this.mEntryTitles = paramArrayOfCharSequence;
  }
  
  public void setValue(int paramInt)
  {
    int i;
    if (this.mValue != paramInt)
    {
      i = 1;
      if ((i != 0) || (!this.mValueSet)) {
        break label27;
      }
    }
    label27:
    do
    {
      return;
      i = 0;
      break;
      this.mValue = paramInt;
      this.mValueIndex = getIndexForValue(paramInt);
      this.mValueSet = true;
      persistInt(paramInt);
      if (i != 0)
      {
        notifyDependencyChange(shouldDisableDependents());
        notifyChanged();
      }
    } while (this.mOnValueChangedListener == null);
    this.mOnValueChangedListener.onValueChanged(this, paramInt);
  }
  
  public void setValues(int[] paramArrayOfInt)
  {
    this.mEntryValues = paramArrayOfInt;
    if ((this.mValueSet) && (this.mValueIndex == -1)) {
      this.mValueIndex = getIndexForValue(this.mValue);
    }
  }
  
  private class ListPreferenceAdapter
    extends BaseAdapter
  {
    private LayoutInflater mInflater;
    
    private ListPreferenceAdapter() {}
    
    public int getCount()
    {
      return ListDialogPreference.-get0(ListDialogPreference.this).length;
    }
    
    public Integer getItem(int paramInt)
    {
      return Integer.valueOf(ListDialogPreference.-get0(ListDialogPreference.this)[paramInt]);
    }
    
    public long getItemId(int paramInt)
    {
      return ListDialogPreference.-get0(ListDialogPreference.this)[paramInt];
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      View localView = paramView;
      if (paramView == null)
      {
        if (this.mInflater == null) {
          this.mInflater = LayoutInflater.from(paramViewGroup.getContext());
        }
        localView = this.mInflater.inflate(ListDialogPreference.-get1(ListDialogPreference.this), paramViewGroup, false);
      }
      ListDialogPreference.this.onBindListItem(localView, paramInt);
      return localView;
    }
    
    public boolean hasStableIds()
    {
      return true;
    }
  }
  
  public static abstract interface OnValueChangedListener
  {
    public abstract void onValueChanged(ListDialogPreference paramListDialogPreference, int paramInt);
  }
  
  private static class SavedState
    extends Preference.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public ListDialogPreference.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ListDialogPreference.SavedState(paramAnonymousParcel);
      }
      
      public ListDialogPreference.SavedState[] newArray(int paramAnonymousInt)
      {
        return new ListDialogPreference.SavedState[paramAnonymousInt];
      }
    };
    public int value;
    
    public SavedState(Parcel paramParcel)
    {
      super();
      this.value = paramParcel.readInt();
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.value);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accessibility\ListDialogPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */