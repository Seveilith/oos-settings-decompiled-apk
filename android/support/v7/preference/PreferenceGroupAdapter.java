package android.support.v7.preference;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.RestrictTo;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestrictTo({android.support.annotation.RestrictTo.Scope.GROUP_ID})
public class PreferenceGroupAdapter
  extends RecyclerView.Adapter<PreferenceViewHolder>
  implements Preference.OnPreferenceChangeInternalListener, PreferenceGroup.PreferencePositionCallback
{
  private static final String TAG = "PreferenceGroupAdapter";
  private Handler mHandler = new Handler();
  private PreferenceGroup mPreferenceGroup;
  private List<PreferenceLayout> mPreferenceLayouts;
  private List<Preference> mPreferenceList;
  private List<Preference> mPreferenceListInternal;
  private Runnable mSyncRunnable = new Runnable()
  {
    public void run()
    {
      PreferenceGroupAdapter.-wrap0(PreferenceGroupAdapter.this);
    }
  };
  private PreferenceLayout mTempPreferenceLayout = new PreferenceLayout();
  
  public PreferenceGroupAdapter(PreferenceGroup paramPreferenceGroup)
  {
    this.mPreferenceGroup = paramPreferenceGroup;
    this.mPreferenceGroup.setOnPreferenceChangeInternalListener(this);
    this.mPreferenceList = new ArrayList();
    this.mPreferenceListInternal = new ArrayList();
    this.mPreferenceLayouts = new ArrayList();
    if ((this.mPreferenceGroup instanceof PreferenceScreen)) {
      setHasStableIds(((PreferenceScreen)this.mPreferenceGroup).shouldUseGeneratedIds());
    }
    for (;;)
    {
      syncMyPreferences();
      return;
      setHasStableIds(true);
    }
  }
  
  private void addPreferenceClassName(Preference paramPreference)
  {
    paramPreference = createPreferenceLayout(paramPreference, null);
    if (!this.mPreferenceLayouts.contains(paramPreference)) {
      this.mPreferenceLayouts.add(paramPreference);
    }
  }
  
  private PreferenceLayout createPreferenceLayout(Preference paramPreference, PreferenceLayout paramPreferenceLayout)
  {
    if (paramPreferenceLayout != null) {}
    for (;;)
    {
      PreferenceLayout.-set0(paramPreferenceLayout, paramPreference.getClass().getName());
      PreferenceLayout.-set1(paramPreferenceLayout, paramPreference.getLayoutResource());
      PreferenceLayout.-set2(paramPreferenceLayout, paramPreference.getWidgetLayoutResource());
      return paramPreferenceLayout;
      paramPreferenceLayout = new PreferenceLayout();
    }
  }
  
  private void flattenPreferenceGroup(List<Preference> paramList, PreferenceGroup paramPreferenceGroup)
  {
    paramPreferenceGroup.sortPreferences();
    int j = paramPreferenceGroup.getPreferenceCount();
    int i = 0;
    while (i < j)
    {
      Preference localPreference = paramPreferenceGroup.getPreference(i);
      paramList.add(localPreference);
      addPreferenceClassName(localPreference);
      if ((localPreference instanceof PreferenceGroup))
      {
        PreferenceGroup localPreferenceGroup = (PreferenceGroup)localPreference;
        if (localPreferenceGroup.isOnSameScreenAsChildren()) {
          flattenPreferenceGroup(paramList, localPreferenceGroup);
        }
      }
      localPreference.setOnPreferenceChangeInternalListener(this);
      i += 1;
    }
  }
  
  private void syncMyPreferences()
  {
    Object localObject = this.mPreferenceListInternal.iterator();
    while (((Iterator)localObject).hasNext()) {
      ((Preference)((Iterator)localObject).next()).setOnPreferenceChangeInternalListener(null);
    }
    localObject = new ArrayList(this.mPreferenceListInternal.size());
    flattenPreferenceGroup((List)localObject, this.mPreferenceGroup);
    ArrayList localArrayList = new ArrayList(((List)localObject).size());
    Iterator localIterator = ((Iterable)localObject).iterator();
    while (localIterator.hasNext())
    {
      Preference localPreference = (Preference)localIterator.next();
      if (localPreference.isVisible()) {
        localArrayList.add(localPreference);
      }
    }
    this.mPreferenceList = localArrayList;
    this.mPreferenceListInternal = ((List)localObject);
    notifyDataSetChanged();
  }
  
  public Preference getItem(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= getItemCount())) {
      return null;
    }
    return (Preference)this.mPreferenceList.get(paramInt);
  }
  
  public int getItemCount()
  {
    return this.mPreferenceList.size();
  }
  
  public long getItemId(int paramInt)
  {
    if (!hasStableIds()) {
      return -1L;
    }
    return getItem(paramInt).getId();
  }
  
  public int getItemViewType(int paramInt)
  {
    this.mTempPreferenceLayout = createPreferenceLayout(getItem(paramInt), this.mTempPreferenceLayout);
    paramInt = this.mPreferenceLayouts.indexOf(this.mTempPreferenceLayout);
    if (paramInt != -1) {
      return paramInt;
    }
    paramInt = this.mPreferenceLayouts.size();
    this.mPreferenceLayouts.add(new PreferenceLayout(this.mTempPreferenceLayout));
    return paramInt;
  }
  
  public int getPreferenceAdapterPosition(Preference paramPreference)
  {
    int j = this.mPreferenceList.size();
    int i = 0;
    while (i < j)
    {
      Preference localPreference = (Preference)this.mPreferenceList.get(i);
      if ((localPreference != null) && (localPreference.equals(paramPreference))) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  public int getPreferenceAdapterPosition(String paramString)
  {
    int j = this.mPreferenceList.size();
    int i = 0;
    while (i < j)
    {
      if (TextUtils.equals(paramString, ((Preference)this.mPreferenceList.get(i)).getKey())) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder, int paramInt)
  {
    getItem(paramInt).onBindViewHolder(paramPreferenceViewHolder);
  }
  
  public PreferenceViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    PreferenceLayout localPreferenceLayout = (PreferenceLayout)this.mPreferenceLayouts.get(paramInt);
    LayoutInflater localLayoutInflater = LayoutInflater.from(paramViewGroup.getContext());
    TypedArray localTypedArray = paramViewGroup.getContext().obtainStyledAttributes(null, R.styleable.BackgroundStyle);
    Drawable localDrawable = localTypedArray.getDrawable(R.styleable.BackgroundStyle_android_selectableItemBackground);
    Object localObject = localDrawable;
    if (localDrawable == null) {
      localObject = paramViewGroup.getContext().getResources().getDrawable(17301602);
    }
    localTypedArray.recycle();
    paramViewGroup = localLayoutInflater.inflate(PreferenceLayout.-get0(localPreferenceLayout), paramViewGroup, false);
    if (paramViewGroup.getBackground() == null) {
      ViewCompat.setBackground(paramViewGroup, (Drawable)localObject);
    }
    localObject = (ViewGroup)paramViewGroup.findViewById(16908312);
    if (localObject != null)
    {
      if (PreferenceLayout.-get1(localPreferenceLayout) == 0) {
        break label143;
      }
      localLayoutInflater.inflate(PreferenceLayout.-get1(localPreferenceLayout), (ViewGroup)localObject);
    }
    for (;;)
    {
      return new PreferenceViewHolder(paramViewGroup);
      label143:
      ((ViewGroup)localObject).setVisibility(8);
    }
  }
  
  public void onPreferenceChange(Preference paramPreference)
  {
    int i = this.mPreferenceList.indexOf(paramPreference);
    if (i != -1) {
      notifyItemChanged(i, paramPreference);
    }
  }
  
  public void onPreferenceHierarchyChange(Preference paramPreference)
  {
    this.mHandler.removeCallbacks(this.mSyncRunnable);
    this.mHandler.post(this.mSyncRunnable);
  }
  
  public void onPreferenceVisibilityChange(Preference paramPreference)
  {
    if (!this.mPreferenceListInternal.contains(paramPreference)) {
      return;
    }
    if (paramPreference.isVisible())
    {
      i = -1;
      Iterator localIterator = this.mPreferenceListInternal.iterator();
      for (;;)
      {
        Preference localPreference;
        if (localIterator.hasNext())
        {
          localPreference = (Preference)localIterator.next();
          if (!paramPreference.equals(localPreference)) {}
        }
        else
        {
          this.mPreferenceList.add(i + 1, paramPreference);
          notifyItemInserted(i + 1);
          return;
        }
        if (localPreference.isVisible()) {
          i += 1;
        }
      }
    }
    int j = this.mPreferenceList.size();
    int i = 0;
    for (;;)
    {
      if ((i >= j) || (paramPreference.equals(this.mPreferenceList.get(i))))
      {
        this.mPreferenceList.remove(i);
        notifyItemRemoved(i);
        return;
      }
      i += 1;
    }
  }
  
  private static class PreferenceLayout
  {
    private String name;
    private int resId;
    private int widgetResId;
    
    public PreferenceLayout() {}
    
    public PreferenceLayout(PreferenceLayout paramPreferenceLayout)
    {
      this.resId = paramPreferenceLayout.resId;
      this.widgetResId = paramPreferenceLayout.widgetResId;
      this.name = paramPreferenceLayout.name;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      if (!(paramObject instanceof PreferenceLayout)) {
        return false;
      }
      paramObject = (PreferenceLayout)paramObject;
      boolean bool1 = bool2;
      if (this.resId == ((PreferenceLayout)paramObject).resId)
      {
        bool1 = bool2;
        if (this.widgetResId == ((PreferenceLayout)paramObject).widgetResId) {
          bool1 = TextUtils.equals(this.name, ((PreferenceLayout)paramObject).name);
        }
      }
      return bool1;
    }
    
    public int hashCode()
    {
      return ((this.resId + 527) * 31 + this.widgetResId) * 31 + this.name.hashCode();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v7\preference\PreferenceGroupAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */