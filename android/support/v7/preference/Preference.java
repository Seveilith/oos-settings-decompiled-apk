package android.support.v7.preference;

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
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.SharedPreferencesCompat.EditorCompat;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.AbsSavedState;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class Preference
  implements Comparable<Preference>
{
  public static final int DEFAULT_ORDER = Integer.MAX_VALUE;
  private boolean mBaseMethodCalled;
  private final View.OnClickListener mClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      Preference.this.performClick(paramAnonymousView);
    }
  };
  private Context mContext;
  private Object mDefaultValue;
  private String mDependencyKey;
  private boolean mDependencyMet = true;
  private List<Preference> mDependents;
  private boolean mEnabled = true;
  private Bundle mExtras;
  private String mFragment;
  private boolean mHasId;
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
  private boolean mSelectable = true;
  private boolean mShouldDisableView = true;
  private CharSequence mSummary;
  private CharSequence mTitle;
  private int mViewId = 0;
  private boolean mVisible = true;
  private int mWidgetLayoutResId;
  
  public Preference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public Preference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, TypedArrayUtils.getAttr(paramContext, R.attr.preferenceStyle, 16842894));
  }
  
  public Preference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public Preference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    this.mContext = paramContext;
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.Preference, paramInt1, paramInt2);
    this.mIconResId = TypedArrayUtils.getResourceId(paramContext, R.styleable.Preference_icon, R.styleable.Preference_android_icon, 0);
    this.mKey = TypedArrayUtils.getString(paramContext, R.styleable.Preference_key, R.styleable.Preference_android_key);
    this.mTitle = TypedArrayUtils.getString(paramContext, R.styleable.Preference_title, R.styleable.Preference_android_title);
    this.mSummary = TypedArrayUtils.getString(paramContext, R.styleable.Preference_summary, R.styleable.Preference_android_summary);
    this.mOrder = TypedArrayUtils.getInt(paramContext, R.styleable.Preference_order, R.styleable.Preference_android_order, Integer.MAX_VALUE);
    this.mFragment = TypedArrayUtils.getString(paramContext, R.styleable.Preference_fragment, R.styleable.Preference_android_fragment);
    this.mLayoutResId = TypedArrayUtils.getResourceId(paramContext, R.styleable.Preference_layout, R.styleable.Preference_android_layout, R.layout.preference);
    this.mWidgetLayoutResId = TypedArrayUtils.getResourceId(paramContext, R.styleable.Preference_widgetLayout, R.styleable.Preference_android_widgetLayout, 0);
    this.mEnabled = TypedArrayUtils.getBoolean(paramContext, R.styleable.Preference_enabled, R.styleable.Preference_android_enabled, true);
    this.mSelectable = TypedArrayUtils.getBoolean(paramContext, R.styleable.Preference_selectable, R.styleable.Preference_android_selectable, true);
    this.mPersistent = TypedArrayUtils.getBoolean(paramContext, R.styleable.Preference_persistent, R.styleable.Preference_android_persistent, true);
    this.mDependencyKey = TypedArrayUtils.getString(paramContext, R.styleable.Preference_dependency, R.styleable.Preference_android_dependency);
    if (paramContext.hasValue(R.styleable.Preference_defaultValue)) {
      this.mDefaultValue = onGetDefaultValue(paramContext, R.styleable.Preference_defaultValue);
    }
    for (;;)
    {
      this.mShouldDisableView = TypedArrayUtils.getBoolean(paramContext, R.styleable.Preference_shouldDisableView, R.styleable.Preference_android_shouldDisableView, true);
      paramContext.recycle();
      return;
      if (paramContext.hasValue(R.styleable.Preference_android_defaultValue)) {
        this.mDefaultValue = onGetDefaultValue(paramContext, R.styleable.Preference_android_defaultValue);
      }
    }
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
  
  private void tryCommit(@NonNull SharedPreferences.Editor paramEditor)
  {
    if (this.mPreferenceManager.shouldCommit()) {
      SharedPreferencesCompat.EditorCompat.getInstance().apply(paramEditor);
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
  
  public boolean callChangeListener(Object paramObject)
  {
    if (this.mOnChangeListener != null) {
      return this.mOnChangeListener.onPreferenceChange(this, paramObject);
    }
    return true;
  }
  
  public int compareTo(@NonNull Preference paramPreference)
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
    return this.mTitle.toString().compareToIgnoreCase(paramPreference.mTitle.toString());
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
  
  public final int getLayoutResource()
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
  
  public PreferenceManager getPreferenceManager()
  {
    return this.mPreferenceManager;
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
  
  public final int getWidgetLayoutResource()
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
  
  public final boolean isVisible()
  {
    return this.mVisible;
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
  
  public void onAttached()
  {
    registerDependency();
  }
  
  protected void onAttachedToHierarchy(PreferenceManager paramPreferenceManager)
  {
    this.mPreferenceManager = paramPreferenceManager;
    if (!this.mHasId) {
      this.mId = paramPreferenceManager.getNextId();
    }
    dispatchSetInitialValue();
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  protected void onAttachedToHierarchy(PreferenceManager paramPreferenceManager, long paramLong)
  {
    this.mId = paramLong;
    this.mHasId = true;
    try
    {
      onAttachedToHierarchy(paramPreferenceManager);
      return;
    }
    finally
    {
      this.mHasId = false;
    }
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    int j = 0;
    paramPreferenceViewHolder.itemView.setOnClickListener(this.mClickListener);
    paramPreferenceViewHolder.itemView.setId(this.mViewId);
    Object localObject1 = (TextView)paramPreferenceViewHolder.findViewById(16908310);
    Object localObject2;
    label112:
    int i;
    if (localObject1 != null)
    {
      localObject2 = getTitle();
      if (!TextUtils.isEmpty((CharSequence)localObject2))
      {
        ((TextView)localObject1).setText((CharSequence)localObject2);
        ((TextView)localObject1).setVisibility(0);
      }
    }
    else
    {
      localObject1 = (TextView)paramPreferenceViewHolder.findViewById(16908304);
      if (localObject1 != null)
      {
        localObject2 = getSummary();
        if (TextUtils.isEmpty((CharSequence)localObject2)) {
          break label309;
        }
        ((TextView)localObject1).setText((CharSequence)localObject2);
        ((TextView)localObject1).setVisibility(0);
      }
      localObject1 = (ImageView)paramPreferenceViewHolder.findViewById(16908294);
      if (localObject1 != null)
      {
        if ((this.mIconResId != 0) || (this.mIcon != null))
        {
          if (this.mIcon == null) {
            this.mIcon = ContextCompat.getDrawable(getContext(), this.mIconResId);
          }
          if (this.mIcon != null) {
            ((ImageView)localObject1).setImageDrawable(this.mIcon);
          }
        }
        if (this.mIcon == null) {
          break label319;
        }
        i = 0;
        label190:
        ((ImageView)localObject1).setVisibility(i);
      }
      localObject2 = paramPreferenceViewHolder.findViewById(R.id.icon_frame);
      localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = paramPreferenceViewHolder.findViewById(16908350);
      }
      if (localObject1 != null)
      {
        if (this.mIcon == null) {
          break label325;
        }
        i = j;
        label237:
        ((View)localObject1).setVisibility(i);
      }
      if (!this.mShouldDisableView) {
        break label331;
      }
      setEnabledStateOnViews(paramPreferenceViewHolder.itemView, isEnabled());
    }
    for (;;)
    {
      boolean bool = isSelectable();
      paramPreferenceViewHolder.itemView.setFocusable(bool);
      paramPreferenceViewHolder.itemView.setClickable(bool);
      paramPreferenceViewHolder.setDividerAllowedAbove(bool);
      paramPreferenceViewHolder.setDividerAllowedBelow(bool);
      return;
      ((TextView)localObject1).setVisibility(8);
      break;
      label309:
      ((TextView)localObject1).setVisibility(8);
      break label112;
      label319:
      i = 8;
      break label190;
      label325:
      i = 8;
      break label237;
      label331:
      setEnabledStateOnViews(paramPreferenceViewHolder.itemView, true);
    }
  }
  
  protected void onClick() {}
  
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
  
  public void onDetached()
  {
    unregisterDependency();
  }
  
  protected Object onGetDefaultValue(TypedArray paramTypedArray, int paramInt)
  {
    return null;
  }
  
  @CallSuper
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat) {}
  
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
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  public void performClick()
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
      if ((localObject != null) && (((PreferenceManager.OnPreferenceTreeClickListener)localObject).onPreferenceTreeClick(this))) {
        return;
      }
    }
    if (this.mIntent != null) {
      getContext().startActivity(this.mIntent);
    }
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
  protected void performClick(View paramView)
  {
    performClick();
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
      if (paramString == getPersistedString(null)) {
        return true;
      }
      SharedPreferences.Editor localEditor = this.mPreferenceManager.getEditor();
      localEditor.putString(this.mKey, paramString);
      tryCommit(localEditor);
      return true;
    }
    return false;
  }
  
  void requireKey()
  {
    if (TextUtils.isEmpty(this.mKey)) {
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
    setIcon(ContextCompat.getDrawable(this.mContext, paramInt));
    this.mIconResId = paramInt;
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    if ((paramDrawable == null) && (this.mIcon != null)) {}
    for (;;)
    {
      this.mIcon = paramDrawable;
      this.mIconResId = 0;
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
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    if ((paramCharSequence == null) && (this.mTitle != null)) {}
    do
    {
      this.mTitle = paramCharSequence;
      notifyChanged();
      do
      {
        return;
      } while (paramCharSequence == null);
    } while (!paramCharSequence.equals(this.mTitle));
  }
  
  public void setViewId(int paramInt)
  {
    this.mViewId = paramInt;
  }
  
  public final void setVisible(boolean paramBoolean)
  {
    if (this.mVisible != paramBoolean)
    {
      this.mVisible = paramBoolean;
      if (this.mListener != null) {
        this.mListener.onPreferenceVisibilityChange(this);
      }
    }
  }
  
  public void setWidgetLayoutResource(int paramInt)
  {
    this.mWidgetLayoutResId = paramInt;
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
    
    public abstract void onPreferenceVisibilityChange(Preference paramPreference);
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


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\preference\Preference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */