package com.android.settings.dashboard;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.provider.Settings.System;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.ArrayUtils;
import com.android.settings.Lte4GEnabler;
import com.android.settings.SettingsActivity;
import com.android.settings.dashboard.conditional.Condition;
import com.android.settings.dashboard.conditional.ConditionAdapterUtils;
import com.android.settingslib.SuggestionParser;
import com.android.settingslib.drawer.DashboardCategory;
import com.android.settingslib.drawer.Tile;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class DashboardAdapter
  extends RecyclerView.Adapter<DashboardItemHolder>
  implements View.OnClickListener
{
  private static final int DEFAULT_SUGGESTION_COUNT = 2;
  public static final String HAS_NEW_VERSION_TO_UPDATE = "has_new_version_to_update";
  private static final String LTE_4G_ACTIVITY = "Lte4GEnableActivity";
  private static final int NS_CONDITION = 3000;
  private static final int NS_ITEMS = 2000;
  private static final int NS_SPACER = 0;
  private static final int NS_SUGGESTION = 1000;
  private static int SUGGESTION_MODE_COLLAPSED = 1;
  private static int SUGGESTION_MODE_DEFAULT = 0;
  private static int SUGGESTION_MODE_EXPANDED = 2;
  private static final String SYSTEM_UPDATE_INTENT = "android.settings.SYSTEM_UPDATE_SETTINGS";
  public static final String TAG = "DashboardAdapter";
  private final Uri ALL_DOWNLOAD_FILES_URI = Uri.parse("content://com.oneplus.ap.upgrader.provider/all_download_files");
  private final IconCache mCache;
  private List<DashboardCategory> mCategories;
  private List<Condition> mConditions;
  private final Context mContext;
  private Condition mExpandedCondition = null;
  private int mId;
  private final List<Integer> mIds = new ArrayList();
  private boolean mIsShowingAll;
  private final List<Object> mItems = new ArrayList();
  private Lte4GEnabler mLte4GEnabler;
  private DashboardItemHolder mLte4GEnablerHolder;
  private int mSuggestionMode = SUGGESTION_MODE_DEFAULT;
  private SuggestionParser mSuggestionParser;
  private List<Tile> mSuggestions;
  private Switch mSw;
  private final List<Integer> mTypes = new ArrayList();
  
  public DashboardAdapter(Context paramContext, SuggestionParser paramSuggestionParser)
  {
    this.mContext = paramContext;
    this.mCache = new IconCache(paramContext);
    this.mLte4GEnabler = new Lte4GEnabler(this.mContext, new Switch(this.mContext));
    this.mSuggestionParser = paramSuggestionParser;
    setHasStableIds(true);
    setShowingAll(true);
  }
  
  private boolean checkShowUpdateIconNeeded()
  {
    int i;
    if (Settings.System.getInt(this.mContext.getContentResolver(), "has_new_version_to_update", 0) == 1)
    {
      i = 1;
      if (getNeedUpdateAppCount(this.mContext.getContentResolver()) <= 0) {
        break label46;
      }
    }
    label46:
    for (boolean bool = true;; bool = false)
    {
      if (i != 0) {
        break label51;
      }
      return bool;
      i = 0;
      break;
    }
    label51:
    return true;
  }
  
  private void countItem(Object paramObject, int paramInt1, boolean paramBoolean, int paramInt2)
  {
    if (paramBoolean)
    {
      this.mItems.add(paramObject);
      this.mTypes.add(Integer.valueOf(paramInt1));
      this.mIds.add(Integer.valueOf(this.mId + paramInt2));
    }
    this.mId += 1;
  }
  
  public static String getSuggestionIdentifier(Context paramContext, Tile paramTile)
  {
    String str2 = paramTile.intent.getComponent().getPackageName();
    String str1 = str2;
    if (str2.equals(paramContext.getPackageName())) {
      str1 = paramTile.intent.getComponent().getClassName();
    }
    return str1;
  }
  
  private boolean hasMoreSuggestions()
  {
    if (this.mSuggestionMode != SUGGESTION_MODE_COLLAPSED)
    {
      if (this.mSuggestionMode != SUGGESTION_MODE_DEFAULT) {}
    }
    else {
      return this.mSuggestions.size() > 2;
    }
    return false;
  }
  
  private boolean isAPMAndSimStateEnable()
  {
    boolean bool = false;
    if (Settings.System.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 0) {
      bool = this.mLte4GEnabler.isThereSimReady();
    }
    return bool;
  }
  
  private void onBindCategory(DashboardItemHolder paramDashboardItemHolder, DashboardCategory paramDashboardCategory)
  {
    if (paramDashboardCategory.title.equals(this.mContext.getResources().getString(2131690789))) {
      paramDashboardItemHolder.divider.setVisibility(8);
    }
    for (;;)
    {
      paramDashboardItemHolder.title.setText(paramDashboardCategory.title);
      return;
      paramDashboardItemHolder.divider.setVisibility(0);
    }
  }
  
  private void onBindSeeAll(DashboardItemHolder paramDashboardItemHolder)
  {
    TextView localTextView = paramDashboardItemHolder.title;
    if (this.mIsShowingAll) {}
    for (int i = 2131693579;; i = 2131693578)
    {
      localTextView.setText(i);
      paramDashboardItemHolder.itemView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = DashboardAdapter.this;
          if (DashboardAdapter.-get3(DashboardAdapter.this)) {}
          for (boolean bool = false;; bool = true)
          {
            paramAnonymousView.setShowingAll(bool);
            return;
          }
        }
      });
      return;
    }
  }
  
  private void onBindSuggestionHeader(DashboardItemHolder paramDashboardItemHolder)
  {
    ImageView localImageView = paramDashboardItemHolder.icon;
    if (hasMoreSuggestions()) {}
    for (int i = 2130837975;; i = 2130837974)
    {
      localImageView.setImageResource(i);
      paramDashboardItemHolder.title.setText(this.mContext.getString(2131693618, new Object[] { Integer.valueOf(this.mSuggestions.size()) }));
      paramDashboardItemHolder.itemView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (DashboardAdapter.-wrap0(DashboardAdapter.this)) {
            DashboardAdapter.-set0(DashboardAdapter.this, DashboardAdapter.-get1());
          }
          for (;;)
          {
            DashboardAdapter.-wrap1(DashboardAdapter.this);
            return;
            DashboardAdapter.-set0(DashboardAdapter.this, DashboardAdapter.-get0());
          }
        }
      });
      return;
    }
  }
  
  private void onBindTile(DashboardItemHolder paramDashboardItemHolder, Tile paramTile)
  {
    paramDashboardItemHolder.icon.setImageDrawable(this.mCache.getIcon(paramTile.icon));
    paramDashboardItemHolder.title.setText(paramTile.title);
    if (!TextUtils.isEmpty(paramTile.summary))
    {
      paramDashboardItemHolder.summary.setText(paramTile.summary);
      paramDashboardItemHolder.summary.setVisibility(0);
    }
    for (;;)
    {
      if (paramDashboardItemHolder.newverison != null)
      {
        if ((!paramDashboardItemHolder.title.getText().equals(this.mContext.getString(2131690673))) || (!checkShowUpdateIconNeeded())) {
          break;
        }
        paramDashboardItemHolder.newverison.setVisibility(0);
      }
      return;
      if (this.mContext.getString(2131691330).equalsIgnoreCase(paramTile.title.toString()))
      {
        paramDashboardItemHolder.summary.setText(this.mContext.getString(2131689515));
        paramDashboardItemHolder.summary.setVisibility(0);
      }
      else
      {
        paramDashboardItemHolder.summary.setVisibility(8);
      }
    }
    paramDashboardItemHolder.newverison.setVisibility(8);
  }
  
  private void recountItems()
  {
    reset();
    int j = 0;
    int i = 0;
    while ((this.mConditions != null) && (i < this.mConditions.size()))
    {
      int k = ((Condition)this.mConditions.get(i)).shouldShow();
      j |= k;
      countItem(this.mConditions.get(i), 2130968642, k, 3000);
      i += 1;
    }
    resetCount();
    i = 0;
    while ((this.mCategories != null) && (i < this.mCategories.size()))
    {
      DashboardCategory localDashboardCategory = (DashboardCategory)this.mCategories.get(i);
      countItem(localDashboardCategory, 2130968667, this.mIsShowingAll, 2000);
      j = 0;
      if (j < localDashboardCategory.tiles.size())
      {
        Tile localTile = (Tile)localDashboardCategory.tiles.get(j);
        if (localTile.intent.getComponent().getClassName().contains("Lte4GEnableActivity"))
        {
          if (!this.mIsShowingAll) {}
          for (bool = ArrayUtils.contains(DashboardSummary.INITIAL_ITEMS, localTile.intent.getComponent().getClassName());; bool = true)
          {
            countItem(localTile, 2130968670, bool, 2000);
            j += 1;
            break;
          }
        }
        if (!this.mIsShowingAll) {}
        for (boolean bool = ArrayUtils.contains(DashboardSummary.INITIAL_ITEMS, localTile.intent.getComponent().getClassName());; bool = true)
        {
          countItem(localTile, 2130968669, bool, 2000);
          break;
        }
      }
      i += 1;
    }
    notifyDataSetChanged();
  }
  
  private void reset()
  {
    this.mItems.clear();
    this.mTypes.clear();
    this.mIds.clear();
    this.mId = 0;
  }
  
  private void resetCount()
  {
    this.mId = 0;
  }
  
  private void set4GEnableSummary(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mLte4GEnablerHolder.summary.setText(2131693772);
      return;
    }
    this.mLte4GEnablerHolder.summary.setText(2131693773);
  }
  
  private void showRemoveOption(View paramView, final Tile paramTile)
  {
    int i = OPUtils.getRightTheme(this.mContext, 2131821334, 2131821333);
    paramView = new PopupMenu(new ContextThemeWrapper(this.mContext, i), paramView);
    paramView.getMenu().add(2131693620).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
    {
      public boolean onMenuItemClick(MenuItem paramAnonymousMenuItem)
      {
        MetricsLogger.action(DashboardAdapter.-get2(DashboardAdapter.this), 387, DashboardAdapter.getSuggestionIdentifier(DashboardAdapter.-get2(DashboardAdapter.this), paramTile));
        DashboardAdapter.this.disableSuggestion(paramTile);
        DashboardAdapter.-get4(DashboardAdapter.this).remove(paramTile);
        DashboardAdapter.-wrap1(DashboardAdapter.this);
        return true;
      }
    });
    paramView.show();
  }
  
  public void disableSuggestion(Tile paramTile)
  {
    if (this.mSuggestionParser == null) {
      return;
    }
    if (this.mSuggestionParser.dismissSuggestion(paramTile))
    {
      this.mContext.getPackageManager().setComponentEnabledSetting(paramTile.intent.getComponent(), 2, 1);
      this.mSuggestionParser.markCategoryDone(paramTile.category);
    }
  }
  
  public Object getItem(long paramLong)
  {
    int i = 0;
    while (i < this.mIds.size())
    {
      if (((Integer)this.mIds.get(i)).intValue() == paramLong) {
        return this.mItems.get(i);
      }
      i += 1;
    }
    return null;
  }
  
  public int getItemCount()
  {
    return this.mIds.size();
  }
  
  public long getItemId(int paramInt)
  {
    return ((Integer)this.mIds.get(paramInt)).intValue();
  }
  
  public int getItemViewType(int paramInt)
  {
    return ((Integer)this.mTypes.get(paramInt)).intValue();
  }
  
  public Lte4GEnabler getLte4GEnabler()
  {
    return this.mLte4GEnabler;
  }
  
  public int getNeedUpdateAppCount(ContentResolver paramContentResolver)
  {
    int j = 0;
    int i = 0;
    localObject = null;
    localContentResolver = null;
    try
    {
      paramContentResolver = paramContentResolver.query(this.ALL_DOWNLOAD_FILES_URI, null, "canInstall =?", new String[] { "1" }, null);
      if (paramContentResolver != null)
      {
        localContentResolver = paramContentResolver;
        localObject = paramContentResolver;
        i = paramContentResolver.getCount();
      }
      j = i;
      if (paramContentResolver != null)
      {
        paramContentResolver.close();
        j = i;
      }
    }
    catch (Exception paramContentResolver)
    {
      do
      {
        localObject = localContentResolver;
        paramContentResolver.printStackTrace();
      } while (localContentResolver == null);
      localContentResolver.close();
      return 0;
    }
    finally
    {
      if (localObject == null) {
        break label103;
      }
      ((Cursor)localObject).close();
    }
    return j;
  }
  
  public List<Tile> getSuggestions()
  {
    return this.mSuggestions;
  }
  
  public Tile getTile(ComponentName paramComponentName)
  {
    int i = 0;
    while (i < this.mCategories.size())
    {
      int j = 0;
      while (j < ((DashboardCategory)this.mCategories.get(i)).tiles.size())
      {
        Tile localTile = (Tile)((DashboardCategory)this.mCategories.get(i)).tiles.get(j);
        if (paramComponentName.equals(localTile.intent.getComponent())) {
          return localTile;
        }
        j += 1;
      }
      i += 1;
    }
    return null;
  }
  
  public boolean isShowingAll()
  {
    return this.mIsShowingAll;
  }
  
  public void notifyChanged(Tile paramTile)
  {
    notifyDataSetChanged();
  }
  
  public void onBindViewHolder(DashboardItemHolder paramDashboardItemHolder, int paramInt)
  {
    switch (((Integer)this.mTypes.get(paramInt)).intValue())
    {
    default: 
      return;
    case 2130968667: 
      onBindCategory(paramDashboardItemHolder, (DashboardCategory)this.mItems.get(paramInt));
      return;
    case 2130968669: 
      localObject = (Tile)this.mItems.get(paramInt);
      onBindTile(paramDashboardItemHolder, (Tile)localObject);
      paramDashboardItemHolder.itemView.setTag(localObject);
      paramDashboardItemHolder.itemView.setOnClickListener(this);
      return;
    case 2130968670: 
      localObject = (Tile)this.mItems.get(paramInt);
      this.mLte4GEnablerHolder = paramDashboardItemHolder;
      onBindTile(paramDashboardItemHolder, (Tile)localObject);
      paramDashboardItemHolder.itemView.setOnClickListener(this);
      this.mSw = ((Switch)paramDashboardItemHolder.itemView.findViewById(2131361960));
      this.mLte4GEnabler.setSwitch(this.mSw);
      paramDashboardItemHolder.itemView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = DashboardAdapter.-get5(DashboardAdapter.this);
          if (DashboardAdapter.-get5(DashboardAdapter.this).isChecked()) {}
          for (boolean bool = false;; bool = true)
          {
            paramAnonymousView.setChecked(bool);
            DashboardAdapter.-wrap2(DashboardAdapter.this, DashboardAdapter.-get5(DashboardAdapter.this).isChecked());
            return;
          }
        }
      });
      updateLte4GEnabler();
      return;
    case 2130968976: 
      onBindSeeAll(paramDashboardItemHolder);
      return;
    }
    Object localObject = (Condition)this.mItems.get(paramInt);
    if (this.mItems.get(paramInt) == this.mExpandedCondition) {}
    for (boolean bool = true;; bool = false)
    {
      ConditionAdapterUtils.bindViews((Condition)localObject, paramDashboardItemHolder, bool, this, new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          DashboardAdapter.this.onExpandClick(paramAnonymousView);
        }
      });
      return;
    }
  }
  
  public void onClick(View paramView)
  {
    if (paramView.getId() == 2131362071) {
      return;
    }
    if (paramView.getId() == 2131362069)
    {
      if ((this.mContext.getResources().getBoolean(2131558436)) && (((Tile)paramView.getTag()).title.equals(this.mContext.getResources().getString(2131691675))))
      {
        Intent localIntent = new Intent("android.settings.SYSTEM_UPDATE_SETTINGS");
        if (this.mContext.getPackageManager().queryIntentActivities(localIntent, 0).size() < 1) {
          return;
        }
      }
      ((SettingsActivity)this.mContext).openTile((Tile)paramView.getTag());
      return;
    }
    if (paramView.getTag() == this.mExpandedCondition)
    {
      MetricsLogger.action(this.mContext, 375, this.mExpandedCondition.getMetricsConstant());
      this.mExpandedCondition.onPrimaryClick();
      return;
    }
    this.mExpandedCondition = ((Condition)paramView.getTag());
    if (this.mContext.getString(2131690470).equals(this.mExpandedCondition.getTitle())) {
      this.mContext.startActivity(new Intent("android.intent.action.CheckUpdate"));
    }
    for (;;)
    {
      notifyDataSetChanged();
      return;
      MetricsLogger.action(this.mContext, 373, this.mExpandedCondition.getMetricsConstant());
    }
  }
  
  public DashboardItemHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    boolean bool = false;
    paramViewGroup = LayoutInflater.from(paramViewGroup.getContext()).inflate(paramInt, paramViewGroup, false);
    if (paramInt == 2130968670) {
      bool = true;
    }
    return new DashboardItemHolder(paramViewGroup, bool);
  }
  
  public void onExpandClick(View paramView)
  {
    if (paramView.getTag() == this.mExpandedCondition)
    {
      MetricsLogger.action(this.mContext, 374, this.mExpandedCondition.getMetricsConstant());
      this.mExpandedCondition = null;
    }
    for (;;)
    {
      notifyDataSetChanged();
      return;
      this.mExpandedCondition = ((Condition)paramView.getTag());
      MetricsLogger.action(this.mContext, 373, this.mExpandedCondition.getMetricsConstant());
    }
  }
  
  public void setCategories(List<DashboardCategory> paramList)
  {
    this.mCategories = paramList;
    TypedValue localTypedValue = new TypedValue();
    this.mContext.getTheme().resolveAttribute(16843829, localTypedValue, true);
    int i = 0;
    while (i < paramList.size())
    {
      int j = 0;
      while (j < ((DashboardCategory)paramList.get(i)).tiles.size())
      {
        Tile localTile = (Tile)((DashboardCategory)paramList.get(i)).tiles.get(j);
        if (!this.mContext.getPackageName().equals(localTile.intent.getComponent().getPackageName())) {
          localTile.icon.setTint(localTypedValue.data);
        }
        j += 1;
      }
      i += 1;
    }
    recountItems();
  }
  
  public void setConditions(List<Condition> paramList)
  {
    this.mConditions = paramList;
    recountItems();
  }
  
  public void setShowingAll(boolean paramBoolean)
  {
    this.mIsShowingAll = paramBoolean;
    recountItems();
  }
  
  public void setSuggestions(List<Tile> paramList)
  {
    this.mSuggestions = paramList;
    recountItems();
  }
  
  public void updateLte4GEnabler()
  {
    if (this.mLte4GEnablerHolder == null) {
      return;
    }
    boolean bool = isAPMAndSimStateEnable();
    this.mLte4GEnablerHolder.itemView.setEnabled(bool);
    this.mLte4GEnablerHolder.title.setEnabled(bool);
    this.mLte4GEnablerHolder.summary.setEnabled(bool);
    this.mLte4GEnablerHolder.sw.setEnabled(bool);
    this.mLte4GEnablerHolder.summary.setVisibility(0);
    ImageView localImageView = this.mLte4GEnablerHolder.icon;
    if (bool) {}
    for (int i = 2130838028;; i = 2130838029)
    {
      localImageView.setImageResource(i);
      if (bool) {
        break;
      }
      this.mLte4GEnablerHolder.summary.setText(2131693774);
      return;
    }
    this.mSw.setChecked(this.mSw.isChecked());
    set4GEnableSummary(this.mSw.isChecked());
  }
  
  public static class DashboardItemHolder
    extends RecyclerView.ViewHolder
  {
    public final View divider;
    public final ImageView icon;
    public ImageView newverison;
    public final TextView summary;
    public final Switch sw;
    public final TextView title;
    
    public DashboardItemHolder(View paramView, boolean paramBoolean)
    {
      super();
      if (!paramBoolean)
      {
        this.icon = ((ImageView)paramView.findViewById(16908294));
        this.title = ((TextView)paramView.findViewById(16908310));
        this.summary = ((TextView)paramView.findViewById(16908304));
        this.newverison = ((ImageView)paramView.findViewById(2131362070));
        this.divider = paramView.findViewById(2131362029);
        this.sw = null;
        return;
      }
      this.icon = ((ImageView)paramView.findViewById(16908294));
      this.title = ((TextView)paramView.findViewById(16908310));
      this.summary = ((TextView)paramView.findViewById(16908304));
      this.divider = paramView.findViewById(2131362029);
      this.sw = ((Switch)paramView.findViewById(2131361960));
    }
  }
  
  private static class IconCache
  {
    private final Context mContext;
    private final ArrayMap<Icon, Drawable> mMap = new ArrayMap();
    
    public IconCache(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public Drawable getIcon(Icon paramIcon)
    {
      Drawable localDrawable2 = (Drawable)this.mMap.get(paramIcon);
      Drawable localDrawable1 = localDrawable2;
      if (localDrawable2 == null)
      {
        localDrawable1 = paramIcon.loadDrawable(this.mContext);
        this.mMap.put(paramIcon, localDrawable1);
      }
      return localDrawable1;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\dashboard\DashboardAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */