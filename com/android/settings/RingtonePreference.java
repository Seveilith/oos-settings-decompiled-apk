package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.android.internal.R.styleable;

public class RingtonePreference
  extends Preference
{
  private static final String TAG = "RingtonePreference";
  private int mRequestCode;
  private int mRingtoneType;
  private boolean mShowDefault;
  private boolean mShowSilent;
  
  public RingtonePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.RingtonePreference, 0, 0);
    this.mRingtoneType = paramContext.getInt(0, 1);
    this.mShowDefault = paramContext.getBoolean(1, true);
    this.mShowSilent = paramContext.getBoolean(2, true);
    paramContext.recycle();
  }
  
  public int getRequestCode()
  {
    return this.mRequestCode;
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
  
  public boolean onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    Uri localUri;
    if (paramIntent != null)
    {
      localUri = (Uri)paramIntent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
      if (localUri == null) {
        break label42;
      }
    }
    label42:
    for (paramIntent = localUri.toString();; paramIntent = "")
    {
      if (callChangeListener(paramIntent)) {
        onSaveRingtone(localUri);
      }
      return true;
    }
  }
  
  protected void onAttachedToHierarchy(PreferenceManager paramPreferenceManager)
  {
    super.onAttachedToHierarchy(paramPreferenceManager);
  }
  
  protected Object onGetDefaultValue(TypedArray paramTypedArray, int paramInt)
  {
    return paramTypedArray.getString(paramInt);
  }
  
  public void onPrepareRingtonePickerIntent(Intent paramIntent)
  {
    if (paramIntent != null)
    {
      paramIntent.putExtra("android.intent.extra.ringtone.EXISTING_URI", onRestoreRingtone());
      paramIntent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", this.mShowDefault);
      if (this.mShowDefault) {
        paramIntent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(getRingtoneType()));
      }
      paramIntent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", this.mShowSilent);
      paramIntent.putExtra("android.intent.extra.ringtone.TYPE", this.mRingtoneType);
      paramIntent.putExtra("android.intent.extra.ringtone.TITLE", getTitle());
      paramIntent.putExtra("android.intent.extra.ringtone.AUDIO_ATTRIBUTES_FLAGS", 64);
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
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\RingtonePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */