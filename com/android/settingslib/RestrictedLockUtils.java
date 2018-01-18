package com.android.settingslib;

import android.app.AppGlobals;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.TextView;
import com.android.internal.widget.LockPatternUtils;
import java.util.Iterator;
import java.util.List;

public class RestrictedLockUtils
{
  public static EnforcedAdmin checkIfAccessibilityServiceDisallowed(Context paramContext, String paramString, int paramInt)
  {
    DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)paramContext.getSystemService("device_policy");
    if (localDevicePolicyManager == null) {
      return null;
    }
    EnforcedAdmin localEnforcedAdmin = getProfileOrDeviceOwner(paramContext, paramInt);
    boolean bool1 = true;
    if (localEnforcedAdmin != null) {
      bool1 = localDevicePolicyManager.isAccessibilityServicePermittedByAdmin(localEnforcedAdmin.component, paramString, paramInt);
    }
    paramInt = getManagedProfileId(paramContext, paramInt);
    paramContext = getProfileOrDeviceOwner(paramContext, paramInt);
    boolean bool2 = true;
    if (paramContext != null) {
      bool2 = localDevicePolicyManager.isAccessibilityServicePermittedByAdmin(paramContext.component, paramString, paramInt);
    }
    if ((bool1) || (bool2))
    {
      if (!bool1) {
        return localEnforcedAdmin;
      }
    }
    else {
      return EnforcedAdmin.MULTIPLE_ENFORCED_ADMIN;
    }
    if (!bool2) {
      return paramContext;
    }
    return null;
  }
  
  public static EnforcedAdmin checkIfAccountManagementDisabled(Context paramContext, String paramString, int paramInt)
  {
    if (paramString == null) {
      return null;
    }
    Object localObject = (DevicePolicyManager)paramContext.getSystemService("device_policy");
    if (localObject == null) {
      return null;
    }
    int k = 0;
    localObject = ((DevicePolicyManager)localObject).getAccountTypesWithManagementDisabledAsUser(paramInt);
    int i = 0;
    int m = localObject.length;
    for (;;)
    {
      int j = k;
      if (i < m)
      {
        if (paramString.equals(localObject[i])) {
          j = 1;
        }
      }
      else
      {
        if (j != 0) {
          break;
        }
        return null;
      }
      i += 1;
    }
    return getProfileOrDeviceOwner(paramContext, paramInt);
  }
  
  public static EnforcedAdmin checkIfApplicationIsSuspended(Context paramContext, String paramString, int paramInt)
  {
    IPackageManager localIPackageManager = AppGlobals.getPackageManager();
    try
    {
      if (localIPackageManager.isPackageSuspendedForUser(paramString, paramInt))
      {
        paramContext = getProfileOrDeviceOwner(paramContext, paramInt);
        return paramContext;
      }
    }
    catch (RemoteException|IllegalArgumentException paramContext) {}
    return null;
  }
  
  public static EnforcedAdmin checkIfAutoTimeRequired(Context paramContext)
  {
    paramContext = (DevicePolicyManager)paramContext.getSystemService("device_policy");
    if ((paramContext != null) && (paramContext.getAutoTimeRequired())) {
      return new EnforcedAdmin(paramContext.getDeviceOwnerComponentOnCallingUser(), UserHandle.myUserId());
    }
    return null;
  }
  
  public static EnforcedAdmin checkIfInputMethodDisallowed(Context paramContext, String paramString, int paramInt)
  {
    DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)paramContext.getSystemService("device_policy");
    if (localDevicePolicyManager == null) {
      return null;
    }
    EnforcedAdmin localEnforcedAdmin = getProfileOrDeviceOwner(paramContext, paramInt);
    boolean bool1 = true;
    if (localEnforcedAdmin != null) {
      bool1 = localDevicePolicyManager.isInputMethodPermittedByAdmin(localEnforcedAdmin.component, paramString, paramInt);
    }
    paramInt = getManagedProfileId(paramContext, paramInt);
    paramContext = getProfileOrDeviceOwner(paramContext, paramInt);
    boolean bool2 = true;
    if (paramContext != null) {
      bool2 = localDevicePolicyManager.isInputMethodPermittedByAdmin(paramContext.component, paramString, paramInt);
    }
    if ((bool1) || (bool2))
    {
      if (!bool1) {
        return localEnforcedAdmin;
      }
    }
    else {
      return EnforcedAdmin.MULTIPLE_ENFORCED_ADMIN;
    }
    if (!bool2) {
      return paramContext;
    }
    return null;
  }
  
  public static EnforcedAdmin checkIfKeyguardFeaturesDisabled(Context paramContext, int paramInt1, int paramInt2)
  {
    DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)paramContext.getSystemService("device_policy");
    if (localDevicePolicyManager == null) {
      return null;
    }
    Object localObject3 = (UserManager)paramContext.getSystemService("user");
    Object localObject2 = new LockPatternUtils(paramContext);
    Object localObject1 = null;
    paramContext = null;
    if (((UserManager)localObject3).getUserInfo(paramInt2).isManagedProfile())
    {
      localObject1 = localDevicePolicyManager.getActiveAdminsAsUser(paramInt2);
      if (localObject1 == null) {
        return null;
      }
      localObject2 = ((Iterable)localObject1).iterator();
      for (;;)
      {
        localObject1 = paramContext;
        if (!((Iterator)localObject2).hasNext()) {
          break label348;
        }
        localObject1 = (ComponentName)((Iterator)localObject2).next();
        if ((localDevicePolicyManager.getKeyguardDisabledFeatures((ComponentName)localObject1, paramInt2) & paramInt1) != 0)
        {
          if (paramContext != null) {
            break;
          }
          paramContext = new EnforcedAdmin((ComponentName)localObject1, paramInt2);
        }
      }
      return EnforcedAdmin.MULTIPLE_ENFORCED_ADMIN;
    }
    localObject3 = ((UserManager)localObject3).getProfiles(paramInt2).iterator();
    paramContext = (Context)localObject1;
    UserInfo localUserInfo;
    do
    {
      localObject1 = paramContext;
      if (!((Iterator)localObject3).hasNext()) {
        break;
      }
      localUserInfo = (UserInfo)((Iterator)localObject3).next();
      localObject1 = localDevicePolicyManager.getActiveAdminsAsUser(localUserInfo.id);
    } while (localObject1 == null);
    boolean bool = ((LockPatternUtils)localObject2).isSeparateProfileChallengeEnabled(localUserInfo.id);
    Iterator localIterator = ((Iterable)localObject1).iterator();
    localObject1 = paramContext;
    for (;;)
    {
      paramContext = (Context)localObject1;
      if (!localIterator.hasNext()) {
        break;
      }
      paramContext = (ComponentName)localIterator.next();
      if ((!bool) && ((localDevicePolicyManager.getKeyguardDisabledFeatures(paramContext, localUserInfo.id) & paramInt1) != 0))
      {
        if (localObject1 == null) {
          localObject1 = new EnforcedAdmin(paramContext, localUserInfo.id);
        } else {
          return EnforcedAdmin.MULTIPLE_ENFORCED_ADMIN;
        }
      }
      else if ((localUserInfo.isManagedProfile()) && ((localDevicePolicyManager.getParentProfileInstance(localUserInfo).getKeyguardDisabledFeatures(paramContext, localUserInfo.id) & paramInt1) != 0))
      {
        if (localObject1 != null) {
          break label344;
        }
        localObject1 = new EnforcedAdmin(paramContext, localUserInfo.id);
      }
    }
    label344:
    return EnforcedAdmin.MULTIPLE_ENFORCED_ADMIN;
    label348:
    return (EnforcedAdmin)localObject1;
  }
  
  public static EnforcedAdmin checkIfMaximumTimeToLockIsSet(Context paramContext)
  {
    DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)paramContext.getSystemService("device_policy");
    if (localDevicePolicyManager == null) {
      return null;
    }
    new LockPatternUtils(paramContext);
    Object localObject = null;
    int i = UserHandle.myUserId();
    List localList = UserManager.get(paramContext).getProfiles(i);
    int j = localList.size();
    i = 0;
    paramContext = (Context)localObject;
    while (i < j)
    {
      UserInfo localUserInfo = (UserInfo)localList.get(i);
      localObject = localDevicePolicyManager.getActiveAdminsAsUser(localUserInfo.id);
      if (localObject == null)
      {
        localObject = paramContext;
        i += 1;
        paramContext = (Context)localObject;
      }
      else
      {
        Iterator localIterator = ((Iterable)localObject).iterator();
        for (;;)
        {
          localObject = paramContext;
          if (!localIterator.hasNext()) {
            break;
          }
          localObject = (ComponentName)localIterator.next();
          if (localDevicePolicyManager.getMaximumTimeToLock((ComponentName)localObject, localUserInfo.id) > 0L)
          {
            if (paramContext == null) {
              paramContext = new EnforcedAdmin((ComponentName)localObject, localUserInfo.id);
            } else {
              return EnforcedAdmin.MULTIPLE_ENFORCED_ADMIN;
            }
          }
          else if ((localUserInfo.isManagedProfile()) && (localDevicePolicyManager.getParentProfileInstance(localUserInfo).getMaximumTimeToLock((ComponentName)localObject, localUserInfo.id) > 0L))
          {
            if (paramContext != null) {
              break label221;
            }
            paramContext = new EnforcedAdmin((ComponentName)localObject, localUserInfo.id);
          }
        }
        label221:
        return EnforcedAdmin.MULTIPLE_ENFORCED_ADMIN;
      }
    }
    return paramContext;
  }
  
  public static EnforcedAdmin checkIfPasswordQualityIsSet(Context paramContext, int paramInt)
  {
    DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)paramContext.getSystemService("device_policy");
    if (localDevicePolicyManager == null) {
      return null;
    }
    LockPatternUtils localLockPatternUtils = new LockPatternUtils(paramContext);
    Object localObject2 = null;
    Object localObject1 = null;
    if (localLockPatternUtils.isSeparateProfileChallengeEnabled(paramInt))
    {
      paramContext = localDevicePolicyManager.getActiveAdminsAsUser(paramInt);
      if (paramContext == null) {
        return null;
      }
      localObject2 = paramContext.iterator();
      paramContext = (Context)localObject1;
      for (;;)
      {
        localObject1 = paramContext;
        if (!((Iterator)localObject2).hasNext()) {
          return localObject1;
        }
        localObject1 = (ComponentName)((Iterator)localObject2).next();
        if (localDevicePolicyManager.getPasswordQuality((ComponentName)localObject1, paramInt) > 0)
        {
          if (paramContext != null) {
            break;
          }
          paramContext = new EnforcedAdmin((ComponentName)localObject1, paramInt);
        }
      }
      return EnforcedAdmin.MULTIPLE_ENFORCED_ADMIN;
    }
    Iterator localIterator1 = ((UserManager)paramContext.getSystemService("user")).getProfiles(paramInt).iterator();
    paramContext = (Context)localObject2;
    do
    {
      localObject1 = paramContext;
      if (!localIterator1.hasNext()) {
        break;
      }
      localObject2 = (UserInfo)localIterator1.next();
      localObject1 = localDevicePolicyManager.getActiveAdminsAsUser(((UserInfo)localObject2).id);
    } while (localObject1 == null);
    boolean bool = localLockPatternUtils.isSeparateProfileChallengeEnabled(((UserInfo)localObject2).id);
    Iterator localIterator2 = ((Iterable)localObject1).iterator();
    localObject1 = paramContext;
    for (;;)
    {
      paramContext = (Context)localObject1;
      if (!localIterator2.hasNext()) {
        break;
      }
      paramContext = (ComponentName)localIterator2.next();
      if ((!bool) && (localDevicePolicyManager.getPasswordQuality(paramContext, ((UserInfo)localObject2).id) > 0))
      {
        if (localObject1 == null) {
          localObject1 = new EnforcedAdmin(paramContext, ((UserInfo)localObject2).id);
        } else {
          return EnforcedAdmin.MULTIPLE_ENFORCED_ADMIN;
        }
      }
      else if ((((UserInfo)localObject2).isManagedProfile()) && (localDevicePolicyManager.getParentProfileInstance((UserInfo)localObject2).getPasswordQuality(paramContext, ((UserInfo)localObject2).id) > 0))
      {
        if (localObject1 != null) {
          break label316;
        }
        localObject1 = new EnforcedAdmin(paramContext, ((UserInfo)localObject2).id);
      }
    }
    label316:
    return EnforcedAdmin.MULTIPLE_ENFORCED_ADMIN;
    return (EnforcedAdmin)localObject1;
  }
  
  public static EnforcedAdmin checkIfRemoteContactSearchDisallowed(Context paramContext, int paramInt)
  {
    DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)paramContext.getSystemService("device_policy");
    if (localDevicePolicyManager == null) {
      return null;
    }
    paramContext = getProfileOwner(paramContext, paramInt);
    if (paramContext == null) {
      return null;
    }
    UserHandle localUserHandle = UserHandle.of(paramInt);
    if ((localDevicePolicyManager.getCrossProfileContactsSearchDisabled(localUserHandle)) && (localDevicePolicyManager.getCrossProfileCallerIdDisabled(localUserHandle))) {
      return paramContext;
    }
    return null;
  }
  
  public static EnforcedAdmin checkIfRestrictionEnforced(Context paramContext, String paramString, int paramInt)
  {
    if ((DevicePolicyManager)paramContext.getSystemService("device_policy") == null) {
      return null;
    }
    int j = UserManager.get(paramContext).getUserRestrictionSource(paramString, UserHandle.of(paramInt));
    if ((j == 0) || (j == 1)) {
      return null;
    }
    int i;
    if ((j & 0x4) != 0)
    {
      i = 1;
      if ((j & 0x2) == 0) {
        break label75;
      }
    }
    label75:
    for (j = 1;; j = 0)
    {
      if (i == 0) {
        break label81;
      }
      return getProfileOwner(paramContext, paramInt);
      i = 0;
      break;
    }
    label81:
    if (j != 0)
    {
      paramContext = getDeviceOwner(paramContext);
      if (paramContext.userId == paramInt) {
        return paramContext;
      }
      return EnforcedAdmin.MULTIPLE_ENFORCED_ADMIN;
    }
    return null;
  }
  
  public static EnforcedAdmin checkIfUninstallBlocked(Context paramContext, String paramString, int paramInt)
  {
    Object localObject = checkIfRestrictionEnforced(paramContext, "no_control_apps", paramInt);
    if (localObject != null) {
      return (EnforcedAdmin)localObject;
    }
    localObject = checkIfRestrictionEnforced(paramContext, "no_uninstall_apps", paramInt);
    if (localObject != null) {
      return (EnforcedAdmin)localObject;
    }
    localObject = AppGlobals.getPackageManager();
    try
    {
      if (((IPackageManager)localObject).getBlockUninstallForUser(paramString, paramInt))
      {
        paramContext = getProfileOrDeviceOwner(paramContext, paramInt);
        return paramContext;
      }
    }
    catch (RemoteException paramContext) {}
    return null;
  }
  
  public static EnforcedAdmin getDeviceOwner(Context paramContext)
  {
    paramContext = (DevicePolicyManager)paramContext.getSystemService("device_policy");
    if (paramContext == null) {
      return null;
    }
    ComponentName localComponentName = paramContext.getDeviceOwnerComponentOnAnyUser();
    if (localComponentName != null) {
      return new EnforcedAdmin(localComponentName, paramContext.getDeviceOwnerUserId());
    }
    return null;
  }
  
  private static int getManagedProfileId(Context paramContext, int paramInt)
  {
    paramContext = ((UserManager)paramContext.getSystemService("user")).getProfiles(paramInt).iterator();
    while (paramContext.hasNext())
    {
      UserInfo localUserInfo = (UserInfo)paramContext.next();
      if ((localUserInfo.id != paramInt) && (localUserInfo.isManagedProfile())) {
        return localUserInfo.id;
      }
    }
    return 55536;
  }
  
  public static EnforcedAdmin getProfileOrDeviceOwner(Context paramContext, int paramInt)
  {
    if (paramInt == 55536) {
      return null;
    }
    paramContext = (DevicePolicyManager)paramContext.getSystemService("device_policy");
    if (paramContext == null) {
      return null;
    }
    ComponentName localComponentName = paramContext.getProfileOwnerAsUser(paramInt);
    if (localComponentName != null) {
      return new EnforcedAdmin(localComponentName, paramInt);
    }
    if (paramContext.getDeviceOwnerUserId() == paramInt)
    {
      paramContext = paramContext.getDeviceOwnerComponentOnAnyUser();
      if (paramContext != null) {
        return new EnforcedAdmin(paramContext, paramInt);
      }
    }
    return null;
  }
  
  private static EnforcedAdmin getProfileOwner(Context paramContext, int paramInt)
  {
    if (paramInt == 55536) {
      return null;
    }
    paramContext = (DevicePolicyManager)paramContext.getSystemService("device_policy");
    if (paramContext == null) {
      return null;
    }
    paramContext = paramContext.getProfileOwnerAsUser(paramInt);
    if (paramContext != null) {
      return new EnforcedAdmin(paramContext, paramInt);
    }
    return null;
  }
  
  public static Drawable getRestrictedPadlock(Context paramContext)
  {
    Drawable localDrawable = paramContext.getDrawable(R.drawable.ic_info);
    int i = paramContext.getResources().getDimensionPixelSize(R.dimen.restricted_icon_size);
    localDrawable.setBounds(0, 0, i, i);
    return localDrawable;
  }
  
  public static Intent getShowAdminSupportDetailsIntent(Context paramContext, EnforcedAdmin paramEnforcedAdmin)
  {
    paramContext = new Intent("android.settings.SHOW_ADMIN_SUPPORT_DETAILS");
    if (paramEnforcedAdmin != null)
    {
      if (paramEnforcedAdmin.component != null) {
        paramContext.putExtra("android.app.extra.DEVICE_ADMIN", paramEnforcedAdmin.component);
      }
      int i = UserHandle.myUserId();
      if (paramEnforcedAdmin.userId != 55536) {
        i = paramEnforcedAdmin.userId;
      }
      paramContext.putExtra("android.intent.extra.USER_ID", i);
    }
    return paramContext;
  }
  
  public static boolean hasBaseUserRestriction(Context paramContext, String paramString, int paramInt)
  {
    return ((UserManager)paramContext.getSystemService("user")).hasBaseUserRestriction(paramString, UserHandle.of(paramInt));
  }
  
  public static boolean isAdminInCurrentUserOrProfile(Context paramContext, ComponentName paramComponentName)
  {
    DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)paramContext.getSystemService("device_policy");
    paramContext = UserManager.get(paramContext).getProfiles(UserHandle.myUserId()).iterator();
    while (paramContext.hasNext()) {
      if (localDevicePolicyManager.isAdminActiveAsUser(paramComponentName, ((UserInfo)paramContext.next()).id)) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean isCurrentUserOrProfile(Context paramContext, int paramInt)
  {
    paramContext = UserManager.get(paramContext).getProfiles(UserHandle.myUserId()).iterator();
    while (paramContext.hasNext()) {
      if (((UserInfo)paramContext.next()).id == paramInt) {
        return true;
      }
    }
    return false;
  }
  
  private static void removeExistingRestrictedSpans(SpannableStringBuilder paramSpannableStringBuilder)
  {
    int j = 0;
    int k = paramSpannableStringBuilder.length();
    Object localObject1 = (RestrictedLockImageSpan[])paramSpannableStringBuilder.getSpans(k - 1, k, RestrictedLockImageSpan.class);
    int m = localObject1.length;
    int i = 0;
    while (i < m)
    {
      Object localObject2 = localObject1[i];
      int n = paramSpannableStringBuilder.getSpanStart(localObject2);
      int i1 = paramSpannableStringBuilder.getSpanEnd(localObject2);
      paramSpannableStringBuilder.removeSpan(localObject2);
      paramSpannableStringBuilder.delete(n, i1);
      i += 1;
    }
    localObject1 = (ForegroundColorSpan[])paramSpannableStringBuilder.getSpans(0, k, ForegroundColorSpan.class);
    k = localObject1.length;
    i = j;
    while (i < k)
    {
      paramSpannableStringBuilder.removeSpan(localObject1[i]);
      i += 1;
    }
  }
  
  public static void sendShowAdminSupportDetailsIntent(Context paramContext, EnforcedAdmin paramEnforcedAdmin)
  {
    Intent localIntent = getShowAdminSupportDetailsIntent(paramContext, paramEnforcedAdmin);
    int j = UserHandle.myUserId();
    int i = j;
    if (paramEnforcedAdmin != null)
    {
      i = j;
      if (paramEnforcedAdmin.userId != 55536)
      {
        i = j;
        if (isCurrentUserOrProfile(paramContext, paramEnforcedAdmin.userId)) {
          i = paramEnforcedAdmin.userId;
        }
      }
    }
    paramContext.startActivityAsUser(localIntent, new UserHandle(i));
  }
  
  public static void setMenuItemAsDisabledByAdmin(Context paramContext, MenuItem paramMenuItem, final EnforcedAdmin paramEnforcedAdmin)
  {
    SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(paramMenuItem.getTitle());
    removeExistingRestrictedSpans(localSpannableStringBuilder);
    if (paramEnforcedAdmin != null)
    {
      localSpannableStringBuilder.setSpan(new ForegroundColorSpan(paramContext.getColor(R.color.disabled_text_color)), 0, localSpannableStringBuilder.length(), 33);
      localSpannableStringBuilder.append(" ", new RestrictedLockImageSpan(paramContext), 33);
      paramMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
      {
        public boolean onMenuItemClick(MenuItem paramAnonymousMenuItem)
        {
          RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this.val$context, paramEnforcedAdmin);
          return true;
        }
      });
    }
    for (;;)
    {
      paramMenuItem.setTitle(localSpannableStringBuilder);
      return;
      paramMenuItem.setOnMenuItemClickListener(null);
    }
  }
  
  public static void setTextViewAsDisabledByAdmin(Context paramContext, TextView paramTextView, boolean paramBoolean)
  {
    SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(paramTextView.getText());
    removeExistingRestrictedSpans(localSpannableStringBuilder);
    if (paramBoolean)
    {
      localSpannableStringBuilder.setSpan(new ForegroundColorSpan(paramContext.getColor(R.color.disabled_text_color)), 0, localSpannableStringBuilder.length(), 33);
      paramTextView.setCompoundDrawables(null, null, getRestrictedPadlock(paramContext), null);
      paramTextView.setCompoundDrawablePadding(paramContext.getResources().getDimensionPixelSize(R.dimen.restricted_icon_padding));
    }
    for (;;)
    {
      paramTextView.setText(localSpannableStringBuilder);
      return;
      paramTextView.setCompoundDrawables(null, null, null, null);
    }
  }
  
  public static void setTextViewPadlock(Context paramContext, TextView paramTextView, boolean paramBoolean)
  {
    SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(paramTextView.getText());
    removeExistingRestrictedSpans(localSpannableStringBuilder);
    if (paramBoolean) {
      localSpannableStringBuilder.append(" ", new RestrictedLockImageSpan(paramContext), 33);
    }
    paramTextView.setText(localSpannableStringBuilder);
  }
  
  public static class EnforcedAdmin
  {
    public static final EnforcedAdmin MULTIPLE_ENFORCED_ADMIN = new EnforcedAdmin();
    public ComponentName component = null;
    public int userId = 55536;
    
    public EnforcedAdmin() {}
    
    public EnforcedAdmin(ComponentName paramComponentName, int paramInt)
    {
      this.component = paramComponentName;
      this.userId = paramInt;
    }
    
    public EnforcedAdmin(EnforcedAdmin paramEnforcedAdmin)
    {
      if (paramEnforcedAdmin == null) {
        throw new IllegalArgumentException();
      }
      this.component = paramEnforcedAdmin.component;
      this.userId = paramEnforcedAdmin.userId;
    }
    
    public void copyTo(EnforcedAdmin paramEnforcedAdmin)
    {
      if (paramEnforcedAdmin == null) {
        throw new IllegalArgumentException();
      }
      paramEnforcedAdmin.component = this.component;
      paramEnforcedAdmin.userId = this.userId;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if (!(paramObject instanceof EnforcedAdmin)) {
        return false;
      }
      paramObject = (EnforcedAdmin)paramObject;
      if (this.userId != ((EnforcedAdmin)paramObject).userId) {
        return false;
      }
      if ((this.component == null) && (((EnforcedAdmin)paramObject).component == null)) {}
      while ((this.component != null) && (this.component.equals(((EnforcedAdmin)paramObject).component))) {
        return true;
      }
      return false;
    }
    
    public String toString()
    {
      return "EnforcedAdmin{component=" + this.component + ",userId=" + this.userId + "}";
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\RestrictedLockUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */