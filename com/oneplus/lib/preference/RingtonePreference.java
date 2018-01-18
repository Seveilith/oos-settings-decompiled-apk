package com.oneplus.lib.preference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.oneplus.commonctrl.R.attr;
import com.oneplus.commonctrl.R.styleable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RingtonePreference
  extends Preference
  implements PreferenceManager.OnActivityResultListener
{
  private static final String EXTRA_RINGTONE_AUDIO_ATTRIBUTES_FLAGS = "android.intent.extra.ringtone.AUDIO_ATTRIBUTES_FLAGS";
  private static final int FLAG_BYPASS_INTERRUPTION_POLICY = 64;
  private static final String TAG = "RingtonePreference";
  private static Method getDefaultRingtoneUriBySubId = null;
  private int mRequestCode;
  private int mRingtoneType;
  private boolean mShowDefault;
  private boolean mShowSilent;
  private int mSubscriptionID = 0;
  
  public RingtonePreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public RingtonePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.op_ringtonePreferenceStyle);
  }
  
  public RingtonePreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public RingtonePreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.RingtonePreference, paramInt1, paramInt2);
    this.mRingtoneType = paramContext.getInt(R.styleable.RingtonePreference_android_ringtoneType, 1);
    this.mShowDefault = paramContext.getBoolean(R.styleable.RingtonePreference_android_showDefault, true);
    this.mShowSilent = paramContext.getBoolean(R.styleable.RingtonePreference_android_showSilent, true);
    paramContext.recycle();
  }
  
  public int getRingtoneType()
  {
    return this.mRingtoneType;
  }
  
  public boolean getShowDefault()
  {
    return this.mShowDefault;
  }
  
  public boolean getShowSilent()
  {
    return this.mShowSilent;
  }
  
  public int getSubId()
  {
    return this.mSubscriptionID;
  }
  
  public boolean onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt1 == this.mRequestCode)
    {
      Uri localUri;
      if (paramIntent != null)
      {
        localUri = (Uri)paramIntent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
        if (localUri == null) {
          break label50;
        }
      }
      label50:
      for (paramIntent = localUri.toString();; paramIntent = "")
      {
        if (callChangeListener(paramIntent)) {
          onSaveRingtone(localUri);
        }
        return true;
      }
    }
    return false;
  }
  
  protected void onAttachedToHierarchy(PreferenceManager paramPreferenceManager)
  {
    super.onAttachedToHierarchy(paramPreferenceManager);
    paramPreferenceManager.registerOnActivityResultListener(this);
    this.mRequestCode = paramPreferenceManager.getNextRequestCode();
  }
  
  protected void onClick()
  {
    if (Build.VERSION.SDK_INT >= 26) {}
    for (Intent localIntent = new Intent("oneplus.intent.action.RINGTONE_PICKER");; localIntent = new Intent("android.intent.action.oneplus.RINGTONE_PICKER"))
    {
      onPrepareRingtonePickerIntent(localIntent);
      PreferenceFragment localPreferenceFragment = getPreferenceManager().getFragment();
      if (localPreferenceFragment == null) {
        break;
      }
      localPreferenceFragment.startActivityForResult(localIntent, this.mRequestCode);
      return;
    }
    getPreferenceManager().getActivity().startActivityForResult(localIntent, this.mRequestCode);
  }
  
  protected Object onGetDefaultValue(TypedArray paramTypedArray, int paramInt)
  {
    return paramTypedArray.getString(paramInt);
  }
  
  protected void onPrepareRingtonePickerIntent(Intent paramIntent)
  {
    paramIntent.putExtra("android.intent.extra.ringtone.EXISTING_URI", onRestoreRingtone());
    paramIntent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", this.mShowDefault);
    if ((!this.mShowDefault) || (getRingtoneType() == 1)) {}
    for (;;)
    {
      try
      {
        if (getDefaultRingtoneUriBySubId == null) {
          getDefaultRingtoneUriBySubId = RingtoneManager.class.getDeclaredMethod("getDefaultRingtoneUriBySubId", new Class[] { Integer.TYPE });
        }
        if (getDefaultRingtoneUriBySubId != null) {
          paramIntent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", (Uri)getDefaultRingtoneUriBySubId.invoke(null, new Object[] { Integer.valueOf(getSubId()) }));
        }
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        localNoSuchMethodException.printStackTrace();
        continue;
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        localInvocationTargetException.printStackTrace();
        continue;
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        localIllegalArgumentException.printStackTrace();
        continue;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        localIllegalAccessException.printStackTrace();
        continue;
      }
      paramIntent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", this.mShowSilent);
      paramIntent.putExtra("android.intent.extra.ringtone.TYPE", this.mRingtoneType);
      paramIntent.putExtra("android.intent.extra.ringtone.TITLE", getTitle());
      paramIntent.putExtra("android.intent.extra.ringtone.AUDIO_ATTRIBUTES_FLAGS", 64);
      return;
      paramIntent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(getRingtoneType()));
    }
  }
  
  protected Uri onRestoreRingtone()
  {
    Uri localUri = null;
    String str = getPersistedString(null);
    if (!TextUtils.isEmpty(str)) {
      localUri = Uri.parse(str);
    }
    return localUri;
  }
  
  protected void onSaveRingtone(Uri paramUri)
  {
    if (paramUri != null) {}
    for (paramUri = paramUri.toString();; paramUri = "")
    {
      persistString(paramUri);
      return;
    }
  }
  
  protected void onSetInitialValue(boolean paramBoolean, Object paramObject)
  {
    paramObject = (String)paramObject;
    if (paramBoolean) {
      return;
    }
    if (!TextUtils.isEmpty((CharSequence)paramObject)) {
      onSaveRingtone(Uri.parse((String)paramObject));
    }
  }
  
  public void setRingtoneType(int paramInt)
  {
    this.mRingtoneType = paramInt;
  }
  
  public void setShowDefault(boolean paramBoolean)
  {
    this.mShowDefault = paramBoolean;
  }
  
  public void setShowSilent(boolean paramBoolean)
  {
    this.mShowSilent = paramBoolean;
  }
  
  public void setSubId(int paramInt)
  {
    this.mSubscriptionID = paramInt;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\preference\RingtonePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */