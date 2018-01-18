package com.android.settings.users;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedPreference;
import java.util.Comparator;

public class UserPreference
  extends RestrictedPreference
{
  private static final int ALPHA_DISABLED = 102;
  private static final int ALPHA_ENABLED = 255;
  static final int DELETE_ID = 2131362514;
  public static final Comparator<UserPreference> SERIAL_NUMBER_COMPARATOR = new Comparator()
  {
    public int compare(UserPreference paramAnonymousUserPreference1, UserPreference paramAnonymousUserPreference2)
    {
      int i = UserPreference.-wrap0(paramAnonymousUserPreference1);
      int j = UserPreference.-wrap0(paramAnonymousUserPreference2);
      if (i < j) {
        return -1;
      }
      if (i > j) {
        return 1;
      }
      return 0;
    }
  };
  static final int SETTINGS_ID = 2131362512;
  public static final int USERID_GUEST_DEFAULTS = -11;
  public static final int USERID_UNKNOWN = -10;
  private View.OnClickListener mDeleteClickListener;
  private int mSerialNumber = -1;
  private View.OnClickListener mSettingsClickListener;
  private int mUserId = -10;
  
  public UserPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, -10, null, null);
  }
  
  UserPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt, View.OnClickListener paramOnClickListener1, View.OnClickListener paramOnClickListener2)
  {
    super(paramContext, paramAttributeSet);
    if ((paramOnClickListener2 != null) || (paramOnClickListener1 != null)) {
      setWidgetLayoutResource(2130968955);
    }
    this.mDeleteClickListener = paramOnClickListener2;
    this.mSettingsClickListener = paramOnClickListener1;
    this.mUserId = paramInt;
    useAdminDisabledSummary(true);
  }
  
  private void dimIcon(boolean paramBoolean)
  {
    Drawable localDrawable1 = getIcon();
    Drawable localDrawable2;
    if (localDrawable1 != null)
    {
      localDrawable2 = localDrawable1.mutate();
      if (!paramBoolean) {
        break label34;
      }
    }
    label34:
    for (int i = 102;; i = 255)
    {
      localDrawable2.setAlpha(i);
      setIcon(localDrawable1);
      return;
    }
  }
  
  private int getSerialNumber()
  {
    if (this.mUserId == UserHandle.myUserId()) {
      return Integer.MIN_VALUE;
    }
    if (this.mSerialNumber < 0)
    {
      if (this.mUserId == -10) {
        return Integer.MAX_VALUE;
      }
      if (this.mUserId == -11) {
        return 2147483646;
      }
      this.mSerialNumber = ((UserManager)getContext().getSystemService("user")).getUserSerialNumber(this.mUserId);
      if (this.mSerialNumber < 0) {
        return this.mUserId;
      }
    }
    return this.mSerialNumber;
  }
  
  public int getUserId()
  {
    return this.mUserId;
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    int j = 0;
    super.onBindViewHolder(paramPreferenceViewHolder);
    paramPreferenceViewHolder.setDividerAllowedAbove(true);
    paramPreferenceViewHolder.setDividerAllowedBelow(true);
    boolean bool = isDisabledByAdmin();
    dimIcon(bool);
    Object localObject = paramPreferenceViewHolder.findViewById(2131362510);
    View localView1;
    View localView2;
    if (localObject != null)
    {
      if (bool)
      {
        i = 8;
        ((View)localObject).setVisibility(i);
      }
    }
    else if (!bool)
    {
      localObject = (UserManager)getContext().getSystemService("user");
      localView1 = paramPreferenceViewHolder.findViewById(2131362513);
      localObject = paramPreferenceViewHolder.findViewById(2131362511);
      localView2 = paramPreferenceViewHolder.findViewById(2131362514);
      if (localView2 != null)
      {
        if ((this.mDeleteClickListener != null) && (!RestrictedLockUtils.hasBaseUserRestriction(getContext(), "no_remove_user", UserHandle.myUserId()))) {
          break label200;
        }
        localView2.setVisibility(8);
        localView1.setVisibility(8);
      }
      label140:
      paramPreferenceViewHolder = (ImageView)paramPreferenceViewHolder.findViewById(2131362512);
      if (paramPreferenceViewHolder != null)
      {
        if (this.mSettingsClickListener == null) {
          break label236;
        }
        paramPreferenceViewHolder.setVisibility(0);
        if (this.mDeleteClickListener != null) {
          break label230;
        }
      }
    }
    label200:
    label230:
    for (int i = j;; i = 8)
    {
      ((View)localObject).setVisibility(i);
      paramPreferenceViewHolder.setOnClickListener(this.mSettingsClickListener);
      paramPreferenceViewHolder.setTag(this);
      return;
      i = 0;
      break;
      localView2.setVisibility(0);
      localView1.setVisibility(0);
      localView2.setOnClickListener(this.mDeleteClickListener);
      localView2.setTag(this);
      break label140;
    }
    label236:
    paramPreferenceViewHolder.setVisibility(8);
    ((View)localObject).setVisibility(8);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\users\UserPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */