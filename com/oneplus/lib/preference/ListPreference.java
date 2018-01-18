package com.oneplus.lib.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.oneplus.commonctrl.R.attr;
import com.oneplus.commonctrl.R.styleable;
import com.oneplus.lib.app.OPAlertDialog.Builder;

public class ListPreference
  extends DialogPreference
{
  private int mClickedDialogEntryIndex;
  private CharSequence[] mEntries;
  private CharSequence[] mEntryValues;
  private String mSummary;
  private String mValue;
  private boolean mValueSet;
  
  public ListPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ListPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.op_dialogPreferenceStyle);
  }
  
  public ListPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public ListPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ListPreference, paramInt1, paramInt2);
    this.mEntries = localTypedArray.getTextArray(R.styleable.ListPreference_android_entries);
    this.mEntryValues = localTypedArray.getTextArray(R.styleable.ListPreference_android_entryValues);
    localTypedArray.recycle();
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.Preference, paramInt1, paramInt2);
    this.mSummary = paramContext.getString(R.styleable.Preference_android_summary);
    paramContext.recycle();
  }
  
  private int getValueIndex()
  {
    return findIndexOfValue(this.mValue);
  }
  
  public int findIndexOfValue(String paramString)
  {
    if ((paramString != null) && (this.mEntryValues != null))
    {
      int i = this.mEntryValues.length - 1;
      while (i >= 0)
      {
        if (this.mEntryValues[i].equals(paramString)) {
          return i;
        }
        i -= 1;
      }
    }
    return -1;
  }
  
  public CharSequence[] getEntries()
  {
    return this.mEntries;
  }
  
  public CharSequence getEntry()
  {
    Object localObject2 = null;
    int i = getValueIndex();
    Object localObject1 = localObject2;
    if (i >= 0)
    {
      localObject1 = localObject2;
      if (this.mEntries != null) {
        localObject1 = this.mEntries[i];
      }
    }
    return (CharSequence)localObject1;
  }
  
  public CharSequence[] getEntryValues()
  {
    return this.mEntryValues;
  }
  
  public CharSequence getSummary()
  {
    CharSequence localCharSequence = getEntry();
    if (this.mSummary == null) {
      return super.getSummary();
    }
    String str = this.mSummary;
    Object localObject = localCharSequence;
    if (localCharSequence == null) {
      localObject = "";
    }
    return String.format(str, new Object[] { localObject });
  }
  
  public String getValue()
  {
    return this.mValue;
  }
  
  protected void onDialogClosed(boolean paramBoolean)
  {
    super.onDialogClosed(paramBoolean);
    if ((paramBoolean) && (this.mClickedDialogEntryIndex >= 0) && (this.mEntryValues != null))
    {
      String str = this.mEntryValues[this.mClickedDialogEntryIndex].toString();
      if (callChangeListener(str)) {
        setValue(str);
      }
    }
  }
  
  protected Object onGetDefaultValue(TypedArray paramTypedArray, int paramInt)
  {
    return paramTypedArray.getString(paramInt);
  }
  
  protected void onPrepareDialogBuilder(OPAlertDialog.Builder paramBuilder)
  {
    super.onPrepareDialogBuilder(paramBuilder);
    if ((this.mEntries == null) || (this.mEntryValues == null)) {
      throw new IllegalStateException("ListPreference requires an entries array and an entryValues array.");
    }
    this.mClickedDialogEntryIndex = getValueIndex();
    paramBuilder.setSingleChoiceItems(this.mEntries, this.mClickedDialogEntryIndex, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        ListPreference.-set0(ListPreference.this, paramAnonymousInt);
        ListPreference.this.onClick(paramAnonymousDialogInterface, -1);
        paramAnonymousDialogInterface.dismiss();
      }
    });
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
    for (paramObject = getPersistedString(this.mValue);; paramObject = (String)paramObject)
    {
      setValue((String)paramObject);
      return;
    }
  }
  
  public void setEntries(int paramInt)
  {
    setEntries(getContext().getResources().getTextArray(paramInt));
  }
  
  public void setEntries(CharSequence[] paramArrayOfCharSequence)
  {
    this.mEntries = paramArrayOfCharSequence;
  }
  
  public void setEntryValues(int paramInt)
  {
    setEntryValues(getContext().getResources().getTextArray(paramInt));
  }
  
  public void setEntryValues(CharSequence[] paramArrayOfCharSequence)
  {
    this.mEntryValues = paramArrayOfCharSequence;
  }
  
  public void setSummary(CharSequence paramCharSequence)
  {
    super.setSummary(paramCharSequence);
    if ((paramCharSequence == null) && (this.mSummary != null)) {
      this.mSummary = null;
    }
    while ((paramCharSequence == null) || (paramCharSequence.equals(this.mSummary))) {
      return;
    }
    this.mSummary = paramCharSequence.toString();
  }
  
  public void setValue(String paramString)
  {
    int i;
    if (TextUtils.equals(this.mValue, paramString))
    {
      i = 0;
      if ((i != 0) || (!this.mValueSet)) {
        break label30;
      }
    }
    label30:
    do
    {
      return;
      i = 1;
      break;
      this.mValue = paramString;
      this.mValueSet = true;
      persistString(paramString);
    } while (i == 0);
    notifyChanged();
  }
  
  public void setValueIndex(int paramInt)
  {
    if (this.mEntryValues != null) {
      setValue(this.mEntryValues[paramInt].toString());
    }
  }
  
  private static class SavedState
    extends Preference.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public ListPreference.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ListPreference.SavedState(paramAnonymousParcel);
      }
      
      public ListPreference.SavedState[] newArray(int paramAnonymousInt)
      {
        return new ListPreference.SavedState[paramAnonymousInt];
      }
    };
    String value;
    
    public SavedState(Parcel paramParcel)
    {
      super();
      this.value = paramParcel.readString();
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeString(this.value);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\preference\ListPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */