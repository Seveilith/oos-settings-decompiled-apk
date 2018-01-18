package com.oneplus.settings.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.util.AttributeSet;
import com.oneplus.settings.gestures.OPGestureUtils;

public class OPGesturePreference
  extends Preference
  implements Preference.OnPreferenceClickListener
{
  private Context mContext;
  
  public OPGesturePreference(Context paramContext)
  {
    super(paramContext);
    init(paramContext);
  }
  
  public OPGesturePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramContext);
  }
  
  public OPGesturePreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramContext);
  }
  
  private void init(Context paramContext)
  {
    this.mContext = paramContext;
    setOnPreferenceClickListener(this);
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    Intent localIntent = new Intent("android.intent.action.ONEPLUS_GESTURE_APP_LIST_ACTION");
    localIntent.putExtra("op_gesture_key", OPGestureUtils.getGestureTypebyGestureKey(paramPreference.getKey()));
    localIntent.putExtra("op_gesture_action", paramPreference.getTitle());
    this.mContext.startActivity(localIntent);
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ui\OPGesturePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */