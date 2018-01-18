package com.android.settingslib.wifi;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.wifi.WifiConfiguration;
import android.os.Looper;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;
import com.android.settingslib.R.attr;
import com.android.settingslib.R.dimen;
import com.android.settingslib.R.string;

public class AccessPointPreference
  extends Preference
{
  private static final int[] STATE_NONE;
  private static final int[] STATE_SECURED = { R.attr.state_encrypted };
  static final int[] WIFI_CONNECTION_STRENGTH = { R.string.accessibility_wifi_one_bar, R.string.accessibility_wifi_two_bars, R.string.accessibility_wifi_three_bars, R.string.accessibility_wifi_signal_full };
  private static int[] wifi_signal_attributes;
  private AccessPoint mAccessPoint;
  private Drawable mBadge;
  private final UserBadgeCache mBadgeCache;
  private final int mBadgePadding;
  private CharSequence mContentDescription;
  private int mDefaultIconResId;
  private boolean mForSavedNetworks = false;
  private int mLevel;
  private final Runnable mNotifyChanged = new Runnable()
  {
    public void run()
    {
      AccessPointPreference.this.notifyChanged();
    }
  };
  private TextView mTitleView;
  private final StateListDrawable mWifiSld;
  
  static
  {
    STATE_NONE = new int[0];
    wifi_signal_attributes = new int[] { R.attr.wifi_signal };
  }
  
  public AccessPointPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mWifiSld = null;
    this.mBadgePadding = 0;
    this.mBadgeCache = null;
  }
  
  public AccessPointPreference(AccessPoint paramAccessPoint, Context paramContext, UserBadgeCache paramUserBadgeCache, int paramInt, boolean paramBoolean)
  {
    super(paramContext);
    this.mBadgeCache = paramUserBadgeCache;
    this.mAccessPoint = paramAccessPoint;
    this.mForSavedNetworks = paramBoolean;
    this.mAccessPoint.setTag(this);
    this.mLevel = -1;
    this.mDefaultIconResId = paramInt;
    this.mWifiSld = ((StateListDrawable)paramContext.getTheme().obtainStyledAttributes(wifi_signal_attributes).getDrawable(0));
    this.mBadgePadding = paramContext.getResources().getDimensionPixelSize(R.dimen.wifi_preference_badge_padding);
  }
  
  public AccessPointPreference(AccessPoint paramAccessPoint, Context paramContext, UserBadgeCache paramUserBadgeCache, boolean paramBoolean)
  {
    super(paramContext);
    this.mBadgeCache = paramUserBadgeCache;
    this.mAccessPoint = paramAccessPoint;
    this.mForSavedNetworks = paramBoolean;
    this.mAccessPoint.setTag(this);
    this.mLevel = -1;
    this.mWifiSld = ((StateListDrawable)paramContext.getTheme().obtainStyledAttributes(wifi_signal_attributes).getDrawable(0));
    this.mBadgePadding = paramContext.getResources().getDimensionPixelSize(R.dimen.wifi_preference_badge_padding);
    refresh();
  }
  
  private void postNotifyChanged()
  {
    try
    {
      if (this.mTitleView != null) {
        this.mTitleView.post(this.mNotifyChanged);
      }
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  private void safeSetDefaultIcon()
  {
    if (this.mDefaultIconResId != 0)
    {
      setIcon(this.mDefaultIconResId);
      return;
    }
    setIcon(null);
  }
  
  public AccessPoint getAccessPoint()
  {
    return this.mAccessPoint;
  }
  
  protected void notifyChanged()
  {
    if (Looper.getMainLooper() != Looper.myLooper())
    {
      postNotifyChanged();
      return;
    }
    super.notifyChanged();
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    if (this.mAccessPoint == null) {
      return;
    }
    Drawable localDrawable = getIcon();
    if (localDrawable != null) {
      localDrawable.setLevel(this.mLevel);
    }
    this.mTitleView = ((TextView)paramPreferenceViewHolder.findViewById(16908310));
    if (this.mTitleView != null)
    {
      this.mTitleView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, this.mBadge, null);
      this.mTitleView.setCompoundDrawablePadding(this.mBadgePadding);
    }
    paramPreferenceViewHolder.itemView.setContentDescription(this.mContentDescription);
  }
  
  public void onLevelChanged()
  {
    postNotifyChanged();
  }
  
  public void refresh()
  {
    if (this.mAccessPoint == null) {
      return;
    }
    int i;
    if (this.mForSavedNetworks)
    {
      setTitle(this.mAccessPoint.getConfigName());
      localObject = getContext();
      i = this.mAccessPoint.getLevel() + 1;
      if (i != this.mLevel)
      {
        this.mLevel = i;
        updateIcon(this.mLevel, (Context)localObject);
        notifyChanged();
      }
      updateBadge((Context)localObject);
      if (!this.mForSavedNetworks) {
        break label204;
      }
    }
    label204:
    for (Object localObject = this.mAccessPoint.getSavedNetworkSummary();; localObject = this.mAccessPoint.getSettingsSummary())
    {
      setSummary((CharSequence)localObject);
      this.mContentDescription = getTitle();
      if (getSummary() != null) {
        this.mContentDescription = TextUtils.concat(new CharSequence[] { this.mContentDescription, ",", getSummary() });
      }
      if ((i >= 0) && (i < WIFI_CONNECTION_STRENGTH.length)) {
        this.mContentDescription = TextUtils.concat(new CharSequence[] { this.mContentDescription, ",", getContext().getString(WIFI_CONNECTION_STRENGTH[i]) });
      }
      return;
      setTitle(this.mAccessPoint.getSsid());
      break;
    }
  }
  
  protected void updateBadge(Context paramContext)
  {
    paramContext = this.mAccessPoint.getConfig();
    if (paramContext != null) {
      this.mBadge = UserBadgeCache.-wrap0(this.mBadgeCache, paramContext.creatorUid);
    }
  }
  
  protected void updateIcon(int paramInt, Context paramContext)
  {
    if (paramInt == -1) {
      safeSetDefaultIcon();
    }
    label9:
    do
    {
      do
      {
        break label9;
        do
        {
          return;
        } while (getIcon() != null);
        if (this.mWifiSld != null)
        {
          if (this.mAccessPoint != null) {}
          for (;;)
          {
            try
            {
              StateListDrawable localStateListDrawable = this.mWifiSld;
              if (this.mAccessPoint.getSecurity() == 0) {
                continue;
              }
              paramContext = STATE_SECURED;
              localStateListDrawable.setState(paramContext);
            }
            catch (Exception paramContext)
            {
              paramContext.printStackTrace();
              continue;
            }
            paramContext = this.mWifiSld.getCurrent();
            if ((this.mForSavedNetworks) || (paramContext == null)) {
              break;
            }
            setIcon(paramContext);
            return;
            paramContext = STATE_NONE;
          }
        }
        paramContext = (StateListDrawable)paramContext.getTheme().obtainStyledAttributes(wifi_signal_attributes).getDrawable(0);
      } while (paramContext == null);
      paramContext = paramContext.getCurrent();
    } while ((this.mForSavedNetworks) || (paramContext == null));
    setIcon(paramContext);
  }
  
  public static class UserBadgeCache
  {
    private final SparseArray<Drawable> mBadges = new SparseArray();
    private final PackageManager mPm;
    
    public UserBadgeCache(PackageManager paramPackageManager)
    {
      this.mPm = paramPackageManager;
    }
    
    private Drawable getUserBadge(int paramInt)
    {
      int i = this.mBadges.indexOfKey(paramInt);
      if (i < 0)
      {
        Drawable localDrawable = this.mPm.getUserBadgeForDensity(new UserHandle(paramInt), 0);
        this.mBadges.put(paramInt, localDrawable);
        return localDrawable;
      }
      return (Drawable)this.mBadges.valueAt(i);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\wifi\AccessPointPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */