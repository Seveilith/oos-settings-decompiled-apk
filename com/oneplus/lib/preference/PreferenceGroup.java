package com.oneplus.lib.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.oneplus.commonctrl.R.styleable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PreferenceGroup
  extends Preference
  implements GenericInflater.Parent<Preference>
{
  private boolean mAttachedToActivity = false;
  private int mCurrentPreferenceOrder = 0;
  private boolean mOrderingAsAdded = true;
  private List<Preference> mPreferenceList = new ArrayList();
  
  public PreferenceGroup(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public PreferenceGroup(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public PreferenceGroup(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.PreferenceGroup, paramInt1, paramInt2);
    this.mOrderingAsAdded = paramContext.getBoolean(R.styleable.PreferenceGroup_android_orderingFromXml, this.mOrderingAsAdded);
    paramContext.recycle();
  }
  
  private boolean removePreferenceInt(Preference paramPreference)
  {
    try
    {
      paramPreference.onPrepareForRemoval();
      boolean bool = this.mPreferenceList.remove(paramPreference);
      return bool;
    }
    finally
    {
      paramPreference = finally;
      throw paramPreference;
    }
  }
  
  public void addItemFromInflater(Preference paramPreference)
  {
    addPreference(paramPreference);
  }
  
  public boolean addPreference(Preference paramPreference)
  {
    if (this.mPreferenceList.contains(paramPreference)) {
      return true;
    }
    if (paramPreference.getOrder() == Integer.MAX_VALUE)
    {
      if (this.mOrderingAsAdded)
      {
        i = this.mCurrentPreferenceOrder;
        this.mCurrentPreferenceOrder = (i + 1);
        paramPreference.setOrder(i);
      }
      if ((paramPreference instanceof PreferenceGroup)) {
        ((PreferenceGroup)paramPreference).setOrderingAsAdded(this.mOrderingAsAdded);
      }
    }
    int j = Collections.binarySearch(this.mPreferenceList, paramPreference);
    int i = j;
    if (j < 0) {
      i = j * -1 - 1;
    }
    if (!onPrepareAddPreference(paramPreference)) {
      return false;
    }
    try
    {
      this.mPreferenceList.add(i, paramPreference);
      paramPreference.onAttachedToHierarchy(getPreferenceManager());
      if (this.mAttachedToActivity) {
        paramPreference.onAttachedToActivity();
      }
      notifyHierarchyChanged();
      return true;
    }
    finally {}
  }
  
  protected void dispatchRestoreInstanceState(Bundle paramBundle)
  {
    super.dispatchRestoreInstanceState(paramBundle);
    int j = getPreferenceCount();
    int i = 0;
    while (i < j)
    {
      getPreference(i).dispatchRestoreInstanceState(paramBundle);
      i += 1;
    }
  }
  
  protected void dispatchSaveInstanceState(Bundle paramBundle)
  {
    super.dispatchSaveInstanceState(paramBundle);
    int j = getPreferenceCount();
    int i = 0;
    while (i < j)
    {
      getPreference(i).dispatchSaveInstanceState(paramBundle);
      i += 1;
    }
  }
  
  public Preference findPreference(CharSequence paramCharSequence)
  {
    if (TextUtils.equals(getKey(), paramCharSequence)) {
      return this;
    }
    int j = getPreferenceCount();
    int i = 0;
    while (i < j)
    {
      Preference localPreference = getPreference(i);
      String str = localPreference.getKey();
      if ((str != null) && (str.equals(paramCharSequence))) {
        return localPreference;
      }
      if ((localPreference instanceof PreferenceGroup))
      {
        localPreference = ((PreferenceGroup)localPreference).findPreference(paramCharSequence);
        if (localPreference != null) {
          return localPreference;
        }
      }
      i += 1;
    }
    return null;
  }
  
  public Preference getPreference(int paramInt)
  {
    return (Preference)this.mPreferenceList.get(paramInt);
  }
  
  public int getPreferenceCount()
  {
    return this.mPreferenceList.size();
  }
  
  protected boolean isOnSameScreenAsChildren()
  {
    return true;
  }
  
  public boolean isOrderingAsAdded()
  {
    return this.mOrderingAsAdded;
  }
  
  public void notifyDependencyChange(boolean paramBoolean)
  {
    super.notifyDependencyChange(paramBoolean);
    int j = getPreferenceCount();
    int i = 0;
    while (i < j)
    {
      getPreference(i).onParentChanged(this, paramBoolean);
      i += 1;
    }
  }
  
  protected void onAttachedToActivity()
  {
    super.onAttachedToActivity();
    this.mAttachedToActivity = true;
    int j = getPreferenceCount();
    int i = 0;
    while (i < j)
    {
      getPreference(i).onAttachedToActivity();
      i += 1;
    }
  }
  
  protected boolean onPrepareAddPreference(Preference paramPreference)
  {
    paramPreference.onParentChanged(this, shouldDisableDependents());
    return true;
  }
  
  protected void onPrepareForRemoval()
  {
    super.onPrepareForRemoval();
    this.mAttachedToActivity = false;
  }
  
  public void removeAll()
  {
    try
    {
      List localList = this.mPreferenceList;
      int i = localList.size() - 1;
      while (i >= 0)
      {
        removePreferenceInt((Preference)localList.get(0));
        i -= 1;
      }
      notifyHierarchyChanged();
      return;
    }
    finally {}
  }
  
  public boolean removePreference(Preference paramPreference)
  {
    boolean bool = removePreferenceInt(paramPreference);
    notifyHierarchyChanged();
    return bool;
  }
  
  public void setOrderingAsAdded(boolean paramBoolean)
  {
    this.mOrderingAsAdded = paramBoolean;
  }
  
  void sortPreferences()
  {
    try
    {
      Collections.sort(this.mPreferenceList);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\preference\PreferenceGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */