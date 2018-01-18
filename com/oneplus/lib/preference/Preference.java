package com.oneplus.lib.preference;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.AbsSavedState;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.oneplus.commonctrl.R.attr;
import com.oneplus.commonctrl.R.id;
import com.oneplus.commonctrl.R.layout;
import com.oneplus.commonctrl.R.styleable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Preference
  implements Comparable<Preference>
{
  public static final int DEFAULT_ORDER = Integer.MAX_VALUE;
  private boolean mBaseMethodCalled;
  private boolean mCanRecycleLayout = true;
  private Context mContext;
  private Object mDefaultValue;
  private String mDependencyKey;
  private boolean mDependencyMet = true;
  private List<Preference> mDependents;
  private boolean mEnabled = true;
  private Bundle mExtras;
  private String mFragment;
  private Drawable mIcon;
  private int mIconResId;
  private long mId;
  private Intent mIntent;
  private String mKey;
  private int mLayoutResId = R.layout.preference;
  private OnPreferenceChangeInternalListener mListener;
  private OnPreferenceChangeListener mOnChangeListener;
  private OnPreferenceClickListener mOnClickListener;
  private int mOrder = Integer.MAX_VALUE;
  private boolean mParentDependencyMet = true;
  private boolean mPersistent = true;
  private PreferenceManager mPreferenceManager;
  private boolean mRequiresKey;
  private Drawable mSecondaryIcon;
  private int mSecondaryIconResId;
  private boolean mSelectable = true;
  private boolean mShouldDisableView = true;
  private CharSequence mSummary;
  private CharSequence mTitle;
  private int mTitleRes;
  private int mWidgetLayoutResId;
  
  public Preference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public Preference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.op_preferenceStyle);
  }
  
  public Preference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public Preference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    this.mContext = paramContext;
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.Preference, paramInt1, paramInt2);
    this.mIconResId = paramContext.getResourceId(R.styleable.Preference_android_icon, 0);
    this.mKey = paramContext.getString(R.styleable.Preference_android_key);
    this.mTitleRes = paramContext.getResourceId(R.styleable.Preference_android_title, 0);
    this.mTitle = paramContext.getString(R.styleable.Preference_android_title);
    this.mSummary = paramContext.getString(R.styleable.Preference_android_summary);
    this.mOrder = paramContext.getInt(R.styleable.Preference_android_order, this.mOrder);
    this.mFragment = paramContext.getString(R.styleable.Preference_android_fragment);
    this.mLayoutResId = paramContext.getResourceId(R.styleable.Preference_android_layout, this.mLayoutResId);
    this.mWidgetLayoutResId = paramContext.getResourceId(R.styleable.Preference_android_widgetLayout, this.mWidgetLayoutResId);
    this.mEnabled = paramContext.getBoolean(R.styleable.Preference_android_enabled, true);
    this.mSelectable = paramContext.getBoolean(R.styleable.Preference_android_selectable, true);
    this.mPersistent = paramContext.getBoolean(R.styleable.Preference_android_persistent, this.mPersistent);
    this.mDependencyKey = paramContext.getString(R.styleable.Preference_android_dependency);
    if (paramContext.hasValue(R.styleable.Preference_android_defaultValue)) {
      this.mDefaultValue = onGetDefaultValue(paramContext, R.styleable.Preference_android_defaultValue);
    }
    this.mShouldDisableView = paramContext.getBoolean(R.styleable.Preference_android_shouldDisableView, this.mShouldDisableView);
    paramContext.recycle();
  }
  
  private void dispatchSetInitialValue()
  {
    if ((shouldPersist()) && (getSharedPreferences().contains(this.mKey))) {
      onSetInitialValue(true, null);
    }
    while (this.mDefaultValue == null) {
      return;
    }
    onSetInitialValue(false, this.mDefaultValue);
  }
  
  private void registerDependency()
  {
    if (TextUtils.isEmpty(this.mDependencyKey)) {
      return;
    }
    Preference localPreference = findPreferenceInHierarchy(this.mDependencyKey);
    if (localPreference != null)
    {
      localPreference.registerDependent(this);
      return;
    }
    throw new IllegalStateException("Dependency \"" + this.mDependencyKey + "\" not found for preference \"" + this.mKey + "\" (title: \"" + this.mTitle + "\"");
  }
  
  private void registerDependent(Preference paramPreference)
  {
    if (this.mDependents == null) {
      this.mDependents = new ArrayList();
    }
    this.mDependents.add(paramPreference);
    paramPreference.onDependencyChanged(this, shouldDisableDependents());
  }
  
  private void setEnabledStateOnViews(View paramView, boolean paramBoolean)
  {
    paramView.setEnabled(paramBoolean);
    if ((paramView instanceof ViewGroup))
    {
      paramView = (ViewGroup)paramView;
      int i = paramView.getChildCount() - 1;
      while (i >= 0)
      {
        setEnabledStateOnViews(paramView.getChildAt(i), paramBoolean);
        i -= 1;
      }
    }
  }
  
  private void tryCommit(SharedPreferences.Editor paramEditor)
  {
    if (this.mPreferenceManager.shouldCommit()) {}
    try
    {
      paramEditor.apply();
      return;
    }
    catch (AbstractMethodError localAbstractMethodError)
    {
      paramEditor.commit();
    }
  }
  
  private void unregisterDependency()
  {
    if (this.mDependencyKey != null)
    {
      Preference localPreference = findPreferenceInHierarchy(this.mDependencyKey);
      if (localPreference != null) {
        localPreference.unregisterDependent(this);
      }
    }
  }
  
  private void unregisterDependent(Preference paramPreference)
  {
    if (this.mDependents != null) {
      this.mDependents.remove(paramPreference);
    }
  }
  
  protected boolean callChangeListener(Object paramObject)
  {
    if (this.mOnChangeListener == null) {
      return true;
    }
    return this.mOnChangeListener.onPreferenceChange(this, paramObject);
  }
  
  boolean canRecycleLayout()
  {
    return this.mCanRecycleLayout;
  }
  
  public int compareTo(Preference paramPreference)
  {
    if (this.mOrder != paramPreference.mOrder) {
      return this.mOrder - paramPreference.mOrder;
    }
    if (this.mTitle == paramPreference.mTitle) {
      return 0;
    }
    if (this.mTitle == null) {
      return 1;
    }
    if (paramPreference.mTitle == null) {
      return -1;
    }
    return CharSequences.compareToIgnoreCase(this.mTitle, paramPreference.mTitle);
  }
  
  void dispatchRestoreInstanceState(Bundle paramBundle)
  {
    if (hasKey())
    {
      paramBundle = paramBundle.getParcelable(this.mKey);
      if (paramBundle != null)
      {
        this.mBaseMethodCalled = false;
        onRestoreInstanceState(paramBundle);
        if (!this.mBaseMethodCalled) {
          throw new IllegalStateException("Derived class did not call super.onRestoreInstanceState()");
        }
      }
    }
  }
  
  void dispatchSaveInstanceState(Bundle paramBundle)
  {
    if (hasKey())
    {
      this.mBaseMethodCalled = false;
      Parcelable localParcelable = onSaveInstanceState();
      if (!this.mBaseMethodCalled) {
        throw new IllegalStateException("Derived class did not call super.onSaveInstanceState()");
      }
      if (localParcelable != null) {
        paramBundle.putParcelable(this.mKey, localParcelable);
      }
    }
  }
  
  protected Preference findPreferenceInHierarchy(String paramString)
  {
    if ((TextUtils.isEmpty(paramString)) || (this.mPreferenceManager == null)) {
      return null;
    }
    return this.mPreferenceManager.findPreference(paramString);
  }
  
  public Context getContext()
  {
    return this.mContext;
  }
  
  public String getDependency()
  {
    return this.mDependencyKey;
  }
  
  public SharedPreferences.Editor getEditor()
  {
    if (this.mPreferenceManager == null) {
      return null;
    }
    return this.mPreferenceManager.getEditor();
  }
  
  public Bundle getExtras()
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    return this.mExtras;
  }
  
  StringBuilder getFilterableStringBuilder()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    CharSequence localCharSequence = getTitle();
    if (!TextUtils.isEmpty(localCharSequence)) {
      localStringBuilder.append(localCharSequence).append(' ');
    }
    localCharSequence = getSummary();
    if (!TextUtils.isEmpty(localCharSequence)) {
      localStringBuilder.append(localCharSequence).append(' ');
    }
    if (localStringBuilder.length() > 0) {
      localStringBuilder.setLength(localStringBuilder.length() - 1);
    }
    return localStringBuilder;
  }
  
  public String getFragment()
  {
    return this.mFragment;
  }
  
  public Drawable getIcon()
  {
    return this.mIcon;
  }
  
  long getId()
  {
    return this.mId;
  }
  
  public Intent getIntent()
  {
    return this.mIntent;
  }
  
  public String getKey()
  {
    return this.mKey;
  }
  
  public int getLayoutResource()
  {
    return this.mLayoutResId;
  }
  
  public OnPreferenceChangeListener getOnPreferenceChangeListener()
  {
    return this.mOnChangeListener;
  }
  
  public OnPreferenceClickListener getOnPreferenceClickListener()
  {
    return this.mOnClickListener;
  }
  
  public int getOrder()
  {
    return this.mOrder;
  }
  
  protected boolean getPersistedBoolean(boolean paramBoolean)
  {
    if (!shouldPersist()) {
      return paramBoolean;
    }
    return this.mPreferenceManager.getSharedPreferences().getBoolean(this.mKey, paramBoolean);
  }
  
  protected float getPersistedFloat(float paramFloat)
  {
    if (!shouldPersist()) {
      return paramFloat;
    }
    return this.mPreferenceManager.getSharedPreferences().getFloat(this.mKey, paramFloat);
  }
  
  protected int getPersistedInt(int paramInt)
  {
    if (!shouldPersist()) {
      return paramInt;
    }
    return this.mPreferenceManager.getSharedPreferences().getInt(this.mKey, paramInt);
  }
  
  protected long getPersistedLong(long paramLong)
  {
    if (!shouldPersist()) {
      return paramLong;
    }
    return this.mPreferenceManager.getSharedPreferences().getLong(this.mKey, paramLong);
  }
  
  protected String getPersistedString(String paramString)
  {
    if (!shouldPersist()) {
      return paramString;
    }
    return this.mPreferenceManager.getSharedPreferences().getString(this.mKey, paramString);
  }
  
  protected Set<String> getPersistedStringSet(Set<String> paramSet)
  {
    if (!shouldPersist()) {
      return paramSet;
    }
    return this.mPreferenceManager.getSharedPreferences().getStringSet(this.mKey, paramSet);
  }
  
  public PreferenceManager getPreferenceManager()
  {
    return this.mPreferenceManager;
  }
  
  public Drawable getSecondaryIcon()
  {
    return this.mSecondaryIcon;
  }
  
  public SharedPreferences getSharedPreferences()
  {
    if (this.mPreferenceManager == null) {
      return null;
    }
    return this.mPreferenceManager.getSharedPreferences();
  }
  
  public boolean getShouldDisableView()
  {
    return this.mShouldDisableView;
  }
  
  public CharSequence getSummary()
  {
    return this.mSummary;
  }
  
  public CharSequence getTitle()
  {
    return this.mTitle;
  }
  
  public int getTitleRes()
  {
    return this.mTitleRes;
  }
  
  public View getView(View paramView, ViewGroup paramViewGroup)
  {
    View localView = paramView;
    if (paramView == null) {
      localView = onCreateView(paramViewGroup);
    }
    onBindView(localView);
    return localView;
  }
  
  public int getWidgetLayoutResource()
  {
    return this.mWidgetLayoutResId;
  }
  
  public boolean hasKey()
  {
    return !TextUtils.isEmpty(this.mKey);
  }
  
  public boolean isEnabled()
  {
    if ((this.mEnabled) && (this.mDependencyMet)) {
      return this.mParentDependencyMet;
    }
    return false;
  }
  
  public boolean isPersistent()
  {
    return this.mPersistent;
  }
  
  public boolean isSelectable()
  {
    return this.mSelectable;
  }
  
  protected void notifyChanged()
  {
    if (this.mListener != null) {
      this.mListener.onPreferenceChange(this);
    }
  }
  
  public void notifyDependencyChange(boolean paramBoolean)
  {
    List localList = this.mDependents;
    if (localList == null) {
      return;
    }
    int j = localList.size();
    int i = 0;
    while (i < j)
    {
      ((Preference)localList.get(i)).onDependencyChanged(this, paramBoolean);
      i += 1;
    }
  }
  
  protected void notifyHierarchyChanged()
  {
    if (this.mListener != null) {
      this.mListener.onPreferenceHierarchyChange(this);
    }
  }
  
  protected void onAttachedToActivity()
  {
    registerDependency();
  }
  
  protected void onAttachedToHierarchy(PreferenceManager paramPreferenceManager)
  {
    this.mPreferenceManager = paramPreferenceManager;
    this.mId = paramPreferenceManager.getNextId();
    dispatchSetInitialValue();
  }
  
  protected void onBindView(View paramView)
  {
    int j = 0;
    Object localObject = (TextView)paramView.findViewById(16908310);
    CharSequence localCharSequence;
    if (localObject != null)
    {
      localCharSequence = getTitle();
      if (!TextUtils.isEmpty(localCharSequence))
      {
        ((TextView)localObject).setText(localCharSequence);
        ((TextView)localObject).setVisibility(0);
      }
    }
    else
    {
      localObject = (TextView)paramView.findViewById(16908304);
      if (localObject != null)
      {
        localCharSequence = getSummary();
        if (TextUtils.isEmpty(localCharSequence)) {
          break label314;
        }
        ((TextView)localObject).setText(localCharSequence);
        ((TextView)localObject).setVisibility(0);
      }
      label90:
      localObject = (ImageView)paramView.findViewById(16908294);
      if (localObject != null)
      {
        if ((this.mIconResId != 0) || (this.mIcon != null))
        {
          if (this.mIcon == null) {
            this.mIcon = getContext().getDrawable(this.mIconResId);
          }
          if (this.mIcon != null) {
            ((ImageView)localObject).setImageDrawable(this.mIcon);
          }
        }
        if (this.mIcon == null) {
          break label324;
        }
        i = 0;
        label168:
        ((ImageView)localObject).setVisibility(i);
      }
      localObject = paramView.findViewById(R.id.icon_frame);
      if (localObject != null)
      {
        if (this.mIcon == null) {
          break label330;
        }
        i = 0;
        label197:
        ((View)localObject).setVisibility(i);
      }
      localObject = (ImageView)paramView.findViewById(R.id.secondary_icon);
      if (localObject != null)
      {
        if ((this.mSecondaryIconResId != 0) || (this.mSecondaryIcon != null))
        {
          if (this.mSecondaryIcon == null) {
            this.mSecondaryIcon = getContext().getDrawable(this.mSecondaryIconResId);
          }
          if (this.mSecondaryIcon != null) {
            ((ImageView)localObject).setImageDrawable(this.mSecondaryIcon);
          }
        }
        if (this.mSecondaryIcon == null) {
          break label336;
        }
      }
    }
    label314:
    label324:
    label330:
    label336:
    for (int i = j;; i = 8)
    {
      ((ImageView)localObject).setVisibility(i);
      if (this.mShouldDisableView) {
        setEnabledStateOnViews(paramView, isEnabled());
      }
      return;
      ((TextView)localObject).setVisibility(8);
      break;
      ((TextView)localObject).setVisibility(8);
      break label90;
      i = 8;
      break label168;
      i = 8;
      break label197;
    }
  }
  
  protected void onClick() {}
  
  protected View onCreateView(ViewGroup paramViewGroup)
  {
    LayoutInflater localLayoutInflater = (LayoutInflater)this.mContext.getSystemService("layout_inflater");
    paramViewGroup = localLayoutInflater.inflate(this.mLayoutResId, paramViewGroup, false);
    ViewGroup localViewGroup = (ViewGroup)paramViewGroup.findViewById(16908312);
    if (localViewGroup != null)
    {
      if (this.mWidgetLayoutResId != 0) {
        localLayoutInflater.inflate(this.mWidgetLayoutResId, localViewGroup);
      }
    }
    else {
      return paramViewGroup;
    }
    localViewGroup.setVisibility(8);
    return paramViewGroup;
  }
  
  public void onDependencyChanged(Preference paramPreference, boolean paramBoolean)
  {
    if (this.mDependencyMet == paramBoolean) {
      if (!paramBoolean) {
        break label32;
      }
    }
    label32:
    for (paramBoolean = false;; paramBoolean = true)
    {
      this.mDependencyMet = paramBoolean;
      notifyDependencyChange(shouldDisableDependents());
      notifyChanged();
      return;
    }
  }
  
  protected Object onGetDefaultValue(TypedArray paramTypedArray, int paramInt)
  {
    return null;
  }
  
  public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
  {
    return false;
  }
  
  public void onParentChanged(Preference paramPreference, boolean paramBoolean)
  {
    if (this.mParentDependencyMet == paramBoolean) {
      if (!paramBoolean) {
        break label32;
      }
    }
    label32:
    for (paramBoolean = false;; paramBoolean = true)
    {
      this.mParentDependencyMet = paramBoolean;
      notifyDependencyChange(shouldDisableDependents());
      notifyChanged();
      return;
    }
  }
  
  protected void onPrepareForRemoval()
  {
    unregisterDependency();
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    this.mBaseMethodCalled = true;
    if ((paramParcelable != BaseSavedState.EMPTY_STATE) && (paramParcelable != null)) {
      throw new IllegalArgumentException("Wrong state class -- expecting Preference State");
    }
  }
  
  protected Parcelable onSaveInstanceState()
  {
    this.mBaseMethodCalled = true;
    return BaseSavedState.EMPTY_STATE;
  }
  
  protected void onSetInitialValue(boolean paramBoolean, Object paramObject) {}
  
  public Bundle peekExtras()
  {
    return this.mExtras;
  }
  
  public void performClick(PreferenceScreen paramPreferenceScreen)
  {
    if (!isEnabled()) {
      return;
    }
    onClick();
    if ((this.mOnClickListener != null) && (this.mOnClickListener.onPreferenceClick(this))) {
      return;
    }
    Object localObject = getPreferenceManager();
    if (localObject != null)
    {
      localObject = ((PreferenceManager)localObject).getOnPreferenceTreeClickListener();
      if ((paramPreferenceScreen != null) && (localObject != null) && (((PreferenceManager.OnPreferenceTreeClickListener)localObject).onPreferenceTreeClick(paramPreferenceScreen, this))) {
        return;
      }
    }
    if (this.mIntent != null) {
      getContext().startActivity(this.mIntent);
    }
  }
  
  protected boolean persistBoolean(boolean paramBoolean)
  {
    boolean bool = false;
    if (shouldPersist())
    {
      if (paramBoolean) {}
      while (paramBoolean == getPersistedBoolean(bool))
      {
        return true;
        bool = true;
      }
      SharedPreferences.Editor localEditor = this.mPreferenceManager.getEditor();
      localEditor.putBoolean(this.mKey, paramBoolean);
      tryCommit(localEditor);
      return true;
    }
    return false;
  }
  
  protected boolean persistFloat(float paramFloat)
  {
    if (shouldPersist())
    {
      if (paramFloat == getPersistedFloat(NaN.0F)) {
        return true;
      }
      SharedPreferences.Editor localEditor = this.mPreferenceManager.getEditor();
      localEditor.putFloat(this.mKey, paramFloat);
      tryCommit(localEditor);
      return true;
    }
    return false;
  }
  
  protected boolean persistInt(int paramInt)
  {
    if (shouldPersist())
    {
      if (paramInt == getPersistedInt(paramInt)) {
        return true;
      }
      SharedPreferences.Editor localEditor = this.mPreferenceManager.getEditor();
      localEditor.putInt(this.mKey, paramInt);
      tryCommit(localEditor);
      return true;
    }
    return false;
  }
  
  protected boolean persistLong(long paramLong)
  {
    if (shouldPersist())
    {
      if (paramLong == getPersistedLong(paramLong)) {
        return true;
      }
      SharedPreferences.Editor localEditor = this.mPreferenceManager.getEditor();
      localEditor.putLong(this.mKey, paramLong);
      tryCommit(localEditor);
      return true;
    }
    return false;
  }
  
  protected boolean persistString(String paramString)
  {
    if (shouldPersist())
    {
      if (TextUtils.equals(paramString, getPersistedString(null))) {
        return true;
      }
      SharedPreferences.Editor localEditor = this.mPreferenceManager.getEditor();
      localEditor.putString(this.mKey, paramString);
      tryCommit(localEditor);
      return true;
    }
    return false;
  }
  
  protected boolean persistStringSet(Set<String> paramSet)
  {
    if (shouldPersist())
    {
      if (paramSet.equals(getPersistedStringSet(null))) {
        return true;
      }
      SharedPreferences.Editor localEditor = this.mPreferenceManager.getEditor();
      localEditor.putStringSet(this.mKey, paramSet);
      tryCommit(localEditor);
      return true;
    }
    return false;
  }
  
  void requireKey()
  {
    if (this.mKey == null) {
      throw new IllegalStateException("Preference does not have a key assigned.");
    }
    this.mRequiresKey = true;
  }
  
  public void restoreHierarchyState(Bundle paramBundle)
  {
    dispatchRestoreInstanceState(paramBundle);
  }
  
  public void saveHierarchyState(Bundle paramBundle)
  {
    dispatchSaveInstanceState(paramBundle);
  }
  
  public void setDefaultValue(Object paramObject)
  {
    this.mDefaultValue = paramObject;
  }
  
  public void setDependency(String paramString)
  {
    unregisterDependency();
    this.mDependencyKey = paramString;
    registerDependency();
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    if (this.mEnabled != paramBoolean)
    {
      this.mEnabled = paramBoolean;
      notifyDependencyChange(shouldDisableDependents());
      notifyChanged();
    }
  }
  
  public void setFragment(String paramString)
  {
    this.mFragment = paramString;
  }
  
  public void setIcon(int paramInt)
  {
    if (this.mIconResId != paramInt)
    {
      this.mIconResId = paramInt;
      setIcon(this.mContext.getDrawable(paramInt));
    }
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    if ((paramDrawable == null) && (this.mIcon != null)) {}
    for (;;)
    {
      this.mIcon = paramDrawable;
      notifyChanged();
      do
      {
        return;
      } while ((paramDrawable == null) || (this.mIcon == paramDrawable));
    }
  }
  
  public void setIntent(Intent paramIntent)
  {
    this.mIntent = paramIntent;
  }
  
  public void setKey(String paramString)
  {
    this.mKey = paramString;
    if ((!this.mRequiresKey) || (hasKey())) {
      return;
    }
    requireKey();
  }
  
  public void setLayoutResource(int paramInt)
  {
    if (paramInt != this.mLayoutResId) {
      this.mCanRecycleLayout = false;
    }
    this.mLayoutResId = paramInt;
  }
  
  final void setOnPreferenceChangeInternalListener(OnPreferenceChangeInternalListener paramOnPreferenceChangeInternalListener)
  {
    this.mListener = paramOnPreferenceChangeInternalListener;
  }
  
  public void setOnPreferenceChangeListener(OnPreferenceChangeListener paramOnPreferenceChangeListener)
  {
    this.mOnChangeListener = paramOnPreferenceChangeListener;
  }
  
  public void setOnPreferenceClickListener(OnPreferenceClickListener paramOnPreferenceClickListener)
  {
    this.mOnClickListener = paramOnPreferenceClickListener;
  }
  
  public void setOrder(int paramInt)
  {
    if (paramInt != this.mOrder)
    {
      this.mOrder = paramInt;
      notifyHierarchyChanged();
    }
  }
  
  public void setPersistent(boolean paramBoolean)
  {
    this.mPersistent = paramBoolean;
  }
  
  public void setSecondaryIcon(int paramInt)
  {
    this.mSecondaryIconResId = paramInt;
    setSecondaryIcon(this.mContext.getDrawable(paramInt));
  }
  
  public void setSecondaryIcon(Drawable paramDrawable)
  {
    if ((paramDrawable == null) && (this.mSecondaryIcon != null)) {}
    for (;;)
    {
      this.mSecondaryIcon = paramDrawable;
      notifyChanged();
      do
      {
        return;
      } while ((paramDrawable == null) || (this.mSecondaryIcon == paramDrawable));
    }
  }
  
  public void setSelectable(boolean paramBoolean)
  {
    if (this.mSelectable != paramBoolean)
    {
      this.mSelectable = paramBoolean;
      notifyChanged();
    }
  }
  
  public void setShouldDisableView(boolean paramBoolean)
  {
    this.mShouldDisableView = paramBoolean;
    notifyChanged();
  }
  
  public void setSummary(int paramInt)
  {
    setSummary(this.mContext.getString(paramInt));
  }
  
  public void setSummary(CharSequence paramCharSequence)
  {
    if ((paramCharSequence == null) && (this.mSummary != null)) {}
    do
    {
      this.mSummary = paramCharSequence;
      notifyChanged();
      do
      {
        return;
      } while (paramCharSequence == null);
    } while (!paramCharSequence.equals(this.mSummary));
  }
  
  public void setTitle(int paramInt)
  {
    setTitle(this.mContext.getString(paramInt));
    this.mTitleRes = paramInt;
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    if ((paramCharSequence == null) && (this.mTitle != null)) {}
    do
    {
      this.mTitleRes = 0;
      this.mTitle = paramCharSequence;
      notifyChanged();
      do
      {
        return;
      } while (paramCharSequence == null);
    } while (!paramCharSequence.equals(this.mTitle));
  }
  
  public void setWidgetLayoutResource(int paramInt)
  {
    if (paramInt != this.mWidgetLayoutResId) {
      this.mCanRecycleLayout = false;
    }
    this.mWidgetLayoutResId = paramInt;
  }
  
  public boolean shouldCommit()
  {
    if (this.mPreferenceManager == null) {
      return false;
    }
    return this.mPreferenceManager.shouldCommit();
  }
  
  public boolean shouldDisableDependents()
  {
    return !isEnabled();
  }
  
  protected boolean shouldPersist()
  {
    if ((this.mPreferenceManager != null) && (isPersistent())) {
      return hasKey();
    }
    return false;
  }
  
  public String toString()
  {
    return getFilterableStringBuilder().toString();
  }
  
  public static class BaseSavedState
    extends AbsSavedState
  {
    public static final Parcelable.Creator<BaseSavedState> CREATOR = new Parcelable.Creator()
    {
      public Preference.BaseSavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new Preference.BaseSavedState(paramAnonymousParcel);
      }
      
      public Preference.BaseSavedState[] newArray(int paramAnonymousInt)
      {
        return new Preference.BaseSavedState[paramAnonymousInt];
      }
    };
    
    public BaseSavedState(Parcel paramParcel)
    {
      super();
    }
    
    public BaseSavedState(Parcelable paramParcelable)
    {
      super();
    }
  }
  
  static abstract interface OnPreferenceChangeInternalListener
  {
    public abstract void onPreferenceChange(Preference paramPreference);
    
    public abstract void onPreferenceHierarchyChange(Preference paramPreference);
  }
  
  public static abstract interface OnPreferenceChangeListener
  {
    public abstract boolean onPreferenceChange(Preference paramPreference, Object paramObject);
  }
  
  public static abstract interface OnPreferenceClickListener
  {
    public abstract boolean onPreferenceClick(Preference paramPreference);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\preference\Preference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */