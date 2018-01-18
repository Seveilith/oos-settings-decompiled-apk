package com.android.settings;

import android.app.Activity;
import android.app.AppGlobals;
import android.app.ListFragment;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public class DeviceAdminSettings
  extends ListFragment
{
  static final String TAG = "DeviceAdminSettings";
  private final ArrayList<DeviceAdminListItem> mAdmins = new ArrayList();
  private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED".equals(paramAnonymousIntent.getAction())) {
        DeviceAdminSettings.this.updateList();
      }
    }
  };
  private DevicePolicyManager mDPM;
  private String mDeviceOwnerPkg;
  private SparseArray<ComponentName> mProfileOwnerComponents = new SparseArray();
  private UserManager mUm;
  
  private void addActiveAdminsForProfile(List<ComponentName> paramList, int paramInt)
  {
    PackageManager localPackageManager;
    Object localObject;
    if (paramList != null)
    {
      localPackageManager = getActivity().getPackageManager();
      IPackageManager localIPackageManager = AppGlobals.getPackageManager();
      int j = paramList.size();
      int i = 0;
      for (;;)
      {
        if (i >= j) {
          return;
        }
        localObject = (ComponentName)paramList.get(i);
        try
        {
          ActivityInfo localActivityInfo = localIPackageManager.getReceiverInfo((ComponentName)localObject, 819328, paramInt);
          localObject = createDeviceAdminInfo(localActivityInfo);
          if (localObject != null) {
            break;
          }
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Log.w("DeviceAdminSettings", "Unable to load component: " + localObject);
            continue;
            DeviceAdminListItem localDeviceAdminListItem = new DeviceAdminListItem(null);
            localDeviceAdminListItem.info = ((DeviceAdminInfo)localObject);
            localDeviceAdminListItem.name = ((DeviceAdminInfo)localObject).loadLabel(localPackageManager).toString();
            localDeviceAdminListItem.active = true;
            this.mAdmins.add(localDeviceAdminListItem);
          }
        }
        i += 1;
      }
    }
  }
  
  private void addDeviceAdminBroadcastReceiversForProfile(Collection<ComponentName> paramCollection, int paramInt)
  {
    PackageManager localPackageManager = getActivity().getPackageManager();
    Object localObject2 = localPackageManager.queryBroadcastReceiversAsUser(new Intent("android.app.action.DEVICE_ADMIN_ENABLED"), 32896, paramInt);
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = Collections.emptyList();
    }
    int i = ((List)localObject1).size();
    paramInt = 0;
    if (paramInt < i)
    {
      localObject2 = (ResolveInfo)((List)localObject1).get(paramInt);
      Object localObject3 = new ComponentName(((ResolveInfo)localObject2).activityInfo.packageName, ((ResolveInfo)localObject2).activityInfo.name);
      if ((paramCollection != null) && (paramCollection.contains(localObject3))) {}
      for (;;)
      {
        paramInt += 1;
        break;
        localObject2 = createDeviceAdminInfo(((ResolveInfo)localObject2).activityInfo);
        if ((localObject2 != null) && (((DeviceAdminInfo)localObject2).isVisible()) && (((DeviceAdminInfo)localObject2).getActivityInfo().applicationInfo.isInternal()))
        {
          localObject3 = new DeviceAdminListItem(null);
          ((DeviceAdminListItem)localObject3).info = ((DeviceAdminInfo)localObject2);
          ((DeviceAdminListItem)localObject3).name = ((DeviceAdminInfo)localObject2).loadLabel(localPackageManager).toString();
          ((DeviceAdminListItem)localObject3).active = false;
          this.mAdmins.add(localObject3);
        }
      }
    }
  }
  
  private DeviceAdminInfo createDeviceAdminInfo(ActivityInfo paramActivityInfo)
  {
    try
    {
      DeviceAdminInfo localDeviceAdminInfo = new DeviceAdminInfo(getActivity(), paramActivityInfo);
      return localDeviceAdminInfo;
    }
    catch (XmlPullParserException|IOException localXmlPullParserException)
    {
      Log.w("DeviceAdminSettings", "Skipping " + paramActivityInfo, localXmlPullParserException);
    }
    return null;
  }
  
  private int getUserId(DeviceAdminInfo paramDeviceAdminInfo)
  {
    return UserHandle.getUserId(paramDeviceAdminInfo.getActivityInfo().applicationInfo.uid);
  }
  
  private boolean isActiveAdmin(DeviceAdminInfo paramDeviceAdminInfo)
  {
    return this.mDPM.isAdminActiveAsUser(paramDeviceAdminInfo.getComponent(), getUserId(paramDeviceAdminInfo));
  }
  
  private boolean isDeviceOwner(DeviceAdminInfo paramDeviceAdminInfo)
  {
    if (getUserId(paramDeviceAdminInfo) == UserHandle.myUserId()) {
      return paramDeviceAdminInfo.getPackageName().equals(this.mDeviceOwnerPkg);
    }
    return false;
  }
  
  private boolean isProfileOwner(DeviceAdminInfo paramDeviceAdminInfo)
  {
    ComponentName localComponentName = (ComponentName)this.mProfileOwnerComponents.get(getUserId(paramDeviceAdminInfo));
    return paramDeviceAdminInfo.getComponent().equals(localComponentName);
  }
  
  private boolean isRemovingAdmin(DeviceAdminInfo paramDeviceAdminInfo)
  {
    return this.mDPM.isRemovingAdmin(paramDeviceAdminInfo.getComponent(), getUserId(paramDeviceAdminInfo));
  }
  
  private void updateAvailableAdminsForProfile(int paramInt)
  {
    List localList = this.mDPM.getActiveAdminsAsUser(paramInt);
    addActiveAdminsForProfile(localList, paramInt);
    addDeviceAdminBroadcastReceiversForProfile(localList, paramInt);
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    setHasOptionsMenu(true);
    Utils.forceCustomPadding(getListView(), true);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mDPM = ((DevicePolicyManager)getActivity().getSystemService("device_policy"));
    this.mUm = ((UserManager)getActivity().getSystemService("user"));
    return paramLayoutInflater.inflate(2130968685, paramViewGroup, false);
  }
  
  public void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
  {
    paramListView = (DeviceAdminInfo)paramListView.getAdapter().getItem(paramInt);
    paramView = new UserHandle(getUserId(paramListView));
    Activity localActivity = getActivity();
    Intent localIntent = new Intent(localActivity, DeviceAdminAdd.class);
    localIntent.putExtra("android.app.extra.DEVICE_ADMIN", paramListView.getComponent());
    localActivity.startActivityAsUser(localIntent, paramView);
  }
  
  public void onPause()
  {
    getActivity().unregisterReceiver(this.mBroadcastReceiver);
    super.onPause();
  }
  
  public void onResume()
  {
    Object localObject1 = null;
    super.onResume();
    Object localObject2 = new IntentFilter();
    ((IntentFilter)localObject2).addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
    getActivity().registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, (IntentFilter)localObject2, null, null);
    localObject2 = this.mDPM.getDeviceOwnerComponentOnAnyUser();
    if (localObject2 != null) {
      localObject1 = ((ComponentName)localObject2).getPackageName();
    }
    this.mDeviceOwnerPkg = ((String)localObject1);
    this.mProfileOwnerComponents.clear();
    localObject1 = this.mUm.getUserProfiles();
    int j = ((List)localObject1).size();
    int i = 0;
    while (i < j)
    {
      int k = ((UserHandle)((List)localObject1).get(i)).getIdentifier();
      this.mProfileOwnerComponents.put(k, this.mDPM.getProfileOwnerAsUser(k));
      i += 1;
    }
    updateList();
  }
  
  void updateList()
  {
    this.mAdmins.clear();
    List localList = this.mUm.getUserProfiles();
    int j = localList.size();
    int i = 0;
    while (i < j)
    {
      updateAvailableAdminsForProfile(((UserHandle)localList.get(i)).getIdentifier());
      i += 1;
    }
    Collections.sort(this.mAdmins);
    getListView().setAdapter(new PolicyListAdapter());
  }
  
  private static class DeviceAdminListItem
    implements Comparable<DeviceAdminListItem>
  {
    public boolean active;
    public DeviceAdminInfo info;
    public String name;
    
    public int compareTo(DeviceAdminListItem paramDeviceAdminListItem)
    {
      if (this.active != paramDeviceAdminListItem.active)
      {
        if (this.active) {
          return -1;
        }
        return 1;
      }
      return this.name.compareTo(paramDeviceAdminListItem.name);
    }
  }
  
  class PolicyListAdapter
    extends BaseAdapter
  {
    final LayoutInflater mInflater = (LayoutInflater)DeviceAdminSettings.this.getActivity().getSystemService("layout_inflater");
    
    PolicyListAdapter() {}
    
    private void bindView(View paramView, DeviceAdminInfo paramDeviceAdminInfo)
    {
      Activity localActivity = DeviceAdminSettings.this.getActivity();
      paramView = (DeviceAdminSettings.ViewHolder)paramView.getTag();
      Drawable localDrawable = paramDeviceAdminInfo.loadIcon(localActivity.getPackageManager());
      localDrawable = localActivity.getPackageManager().getUserBadgedIcon(localDrawable, new UserHandle(DeviceAdminSettings.-wrap2(DeviceAdminSettings.this, paramDeviceAdminInfo)));
      paramView.icon.setImageDrawable(localDrawable);
      paramView.name.setText(paramDeviceAdminInfo.loadLabel(localActivity.getPackageManager()));
      paramView.checkbox.setChecked(DeviceAdminSettings.-wrap0(DeviceAdminSettings.this, paramDeviceAdminInfo));
      boolean bool = isEnabled(paramDeviceAdminInfo);
      try
      {
        paramView.description.setText(paramDeviceAdminInfo.loadDescription(localActivity.getPackageManager()));
        paramView.checkbox.setEnabled(bool);
        paramView.name.setEnabled(bool);
        paramView.description.setEnabled(bool);
        paramView.icon.setEnabled(bool);
        return;
      }
      catch (Resources.NotFoundException paramDeviceAdminInfo)
      {
        for (;;) {}
      }
    }
    
    private boolean isEnabled(Object paramObject)
    {
      paramObject = (DeviceAdminInfo)paramObject;
      return !DeviceAdminSettings.-wrap1(DeviceAdminSettings.this, (DeviceAdminInfo)paramObject);
    }
    
    private View newDeviceAdminView(ViewGroup paramViewGroup)
    {
      paramViewGroup = this.mInflater.inflate(2130968684, paramViewGroup, false);
      DeviceAdminSettings.ViewHolder localViewHolder = new DeviceAdminSettings.ViewHolder();
      localViewHolder.icon = ((ImageView)paramViewGroup.findViewById(2131361793));
      localViewHolder.name = ((TextView)paramViewGroup.findViewById(2131362120));
      localViewHolder.checkbox = ((CheckBox)paramViewGroup.findViewById(2131361909));
      localViewHolder.description = ((TextView)paramViewGroup.findViewById(2131362121));
      paramViewGroup.setTag(localViewHolder);
      return paramViewGroup;
    }
    
    public boolean areAllItemsEnabled()
    {
      return false;
    }
    
    public int getCount()
    {
      return DeviceAdminSettings.-get0(DeviceAdminSettings.this).size();
    }
    
    public Object getItem(int paramInt)
    {
      return ((DeviceAdminSettings.DeviceAdminListItem)DeviceAdminSettings.-get0(DeviceAdminSettings.this).get(paramInt)).info;
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public int getItemViewType(int paramInt)
    {
      return 0;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      Object localObject = getItem(paramInt);
      View localView = paramView;
      if (paramView == null) {
        localView = newDeviceAdminView(paramViewGroup);
      }
      bindView(localView, (DeviceAdminInfo)localObject);
      return localView;
    }
    
    public int getViewTypeCount()
    {
      return 1;
    }
    
    public boolean hasStableIds()
    {
      return false;
    }
    
    public boolean isEnabled(int paramInt)
    {
      return isEnabled(getItem(paramInt));
    }
  }
  
  static class ViewHolder
  {
    CheckBox checkbox;
    TextView description;
    ImageView icon;
    TextView name;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\DeviceAdminSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */