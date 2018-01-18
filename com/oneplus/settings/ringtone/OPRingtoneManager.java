package com.oneplus.settings.ringtone;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.IContentProvider;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.MediaStore.Audio.Media;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.util.Log;
import com.oneplus.settings.utils.OPUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import libcore.io.IoUtils;

public class OPRingtoneManager
{
  private static final String DEFAULT_RINGTONE_PROPERTY_PREFIX_RO = "ro.config.";
  public static final String EXTRA_RINGTONE_DEFAULT_URI = "android.intent.extra.ringtone.DEFAULT_URI";
  public static final String EXTRA_RINGTONE_EXISTING_URI = "android.intent.extra.ringtone.EXISTING_URI";
  public static final String EXTRA_RINGTONE_FOR_CONTACTS = "ringtone_for_contacts";
  public static final String EXTRA_RINGTONE_PICKED_URI = "android.intent.extra.ringtone.PICKED_URI";
  public static final String EXTRA_RINGTONE_SHOW_DEFAULT = "android.intent.extra.ringtone.SHOW_DEFAULT";
  public static final String EXTRA_RINGTONE_SIMID = "oneplus.intent.extra.ringtone.simid";
  public static final String EXTRA_RINGTONE_TITLE = "android.intent.extra.ringtone.TITLE";
  public static final String EXTRA_RINGTONE_TYPE = "android.intent.extra.ringtone.TYPE";
  public static final int ID_COLUMN_INDEX = 0;
  private static final String[] INTERNAL_COLUMNS = { "_id", "title", "\"" + MediaStore.Audio.Media.INTERNAL_CONTENT_URI + "\"", "title_key", "_data" };
  private static final String OP_RINGTONE1_DEFUALT = "op_ringtone1_df";
  private static final String OP_RINGTONE2_DEFUALT = "op_ringtone2_df";
  private static final String OP_RINGTONE_DEFUALT = "op_ringtone_df";
  private static final String OP_SIM_SWITCH = "op_sim_sw";
  private static final String TAG = "RingtoneManager";
  public static final int TITLE_COLUMN_INDEX = 1;
  public static final int TYPE_ALARM = 4;
  public static final int TYPE_ALL = 103;
  public static final int TYPE_NOTIFICATION = 2;
  public static final int TYPE_RINGTONE = 1;
  public static final int TYPE_SMS = 100;
  public static final int URI_COLUMN_INDEX = 2;
  private static Uri mDefaultUri = null;
  private Activity mActivity;
  private Context mContext;
  private Cursor mCursor;
  private final List<String> mFilterColumns = new ArrayList();
  private int mType = 1;
  
  public OPRingtoneManager(Activity paramActivity)
  {
    this.mActivity = paramActivity;
    this.mContext = paramActivity;
    setType(this.mType);
  }
  
  public OPRingtoneManager(Context paramContext)
  {
    this.mContext = paramContext;
    setType(this.mType);
  }
  
  private static String constructBooleanTrueWhereClause(List<String> paramList)
  {
    if (paramList == null) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("(");
    int i = paramList.size() - 1;
    while (i >= 0)
    {
      localStringBuilder.append((String)paramList.get(i)).append("=1 or ");
      i -= 1;
    }
    if (paramList.size() > 0) {
      localStringBuilder.setLength(localStringBuilder.length() - 4);
    }
    localStringBuilder.append(") ");
    return localStringBuilder.toString();
  }
  
  private static String[] constructWhereClause(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    switch (paramInt)
    {
    default: 
      localStringBuilder.append("/system/media/audio/ringtones/%");
    }
    for (;;)
    {
      return new String[] { localStringBuilder.toString() };
      localStringBuilder.append("/system/media/audio/alarms/%");
      continue;
      localStringBuilder.append("/system/media/audio/notifications/%");
    }
  }
  
  public static Uri getActualDefaultRingtoneUri(Context paramContext, int paramInt)
  {
    localObject2 = null;
    localObject1 = getSettingForType(paramInt);
    if (localObject1 == null) {
      return null;
    }
    Object localObject4;
    if ((paramInt & 0x1) != 0)
    {
      localObject3 = "op_ringtone_df";
      localObject1 = localObject3;
      if (!isRingSimSwitchOn(paramContext))
      {
        localObject4 = Settings.System.getString(paramContext.getContentResolver(), "ringtone");
        localObject5 = paramContext.getContentResolver();
        localObject1 = localObject4;
        if (localObject4 == null) {
          localObject1 = "none";
        }
        Settings.System.putString((ContentResolver)localObject5, "op_ringtone_df", (String)localObject1);
        localObject1 = localObject3;
      }
    }
    Object localObject5 = Settings.System.getString(paramContext.getContentResolver(), (String)localObject1);
    if ((localObject5 != null) && (((String)localObject5).equals("none"))) {
      return null;
    }
    if ((localObject5 == null) || ((paramInt & 0x1) == 0))
    {
      paramContext = (Context)localObject2;
      if (localObject5 != null) {
        paramContext = Uri.parse((String)localObject5);
      }
      return paramContext;
    }
    localObject3 = getStaticDefaultRingtoneUri(paramContext);
    localObject2 = null;
    localObject1 = null;
    try
    {
      localObject4 = paramContext.getContentResolver().query(Uri.parse((String)localObject5), null, null, null, null);
      paramContext = (Context)localObject3;
      if (localObject4 != null)
      {
        paramContext = (Context)localObject3;
        localObject1 = localObject4;
        localObject2 = localObject4;
        if (((Cursor)localObject4).getCount() > 0)
        {
          localObject1 = localObject4;
          localObject2 = localObject4;
          paramContext = Uri.parse((String)localObject5);
        }
      }
      localObject2 = paramContext;
      if (localObject4 != null)
      {
        ((Cursor)localObject4).close();
        localObject2 = paramContext;
      }
    }
    catch (SQLiteException paramContext)
    {
      localObject2 = localObject1;
      Log.e("RingtoneManager", "ex " + paramContext);
      localObject2 = localObject3;
      return (Uri)localObject3;
    }
    finally
    {
      if (localObject2 == null) {
        break label266;
      }
      ((Cursor)localObject2).close();
    }
    return (Uri)localObject2;
  }
  
  public static Uri getActualRingtoneUriBySubId(Context paramContext, int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= 2)) {
      return null;
    }
    if (paramInt == 0) {
      localObject2 = "ringtone";
    }
    Object localObject4;
    String str;
    for (localObject1 = "op_ringtone1_df";; localObject1 = "op_ringtone2_df")
    {
      if (isRingSimSwitchOn(paramContext))
      {
        localObject3 = Settings.System.getString(paramContext.getContentResolver(), (String)localObject2);
        localObject4 = paramContext.getContentResolver();
        localObject2 = localObject3;
        if (localObject3 == null) {
          localObject2 = "none";
        }
        Settings.System.putString((ContentResolver)localObject4, (String)localObject1, (String)localObject2);
      }
      str = Settings.System.getString(paramContext.getContentResolver(), (String)localObject1);
      localObject3 = getStaticDefaultRingtoneUri(paramContext);
      if (str != null) {
        break;
      }
      return (Uri)localObject3;
      localObject2 = "ringtone_" + (paramInt + 1);
    }
    if (str.equals("none")) {
      return null;
    }
    localObject2 = null;
    localObject1 = null;
    try
    {
      localObject4 = paramContext.getContentResolver().query(Uri.parse(str), null, null, null, null);
      paramContext = (Context)localObject3;
      if (localObject4 != null)
      {
        paramContext = (Context)localObject3;
        localObject1 = localObject4;
        localObject2 = localObject4;
        if (((Cursor)localObject4).getCount() > 0)
        {
          localObject1 = localObject4;
          localObject2 = localObject4;
          paramContext = Uri.parse(str);
        }
      }
      localObject2 = paramContext;
      if (localObject4 != null)
      {
        ((Cursor)localObject4).close();
        localObject2 = paramContext;
      }
    }
    catch (SQLiteException paramContext)
    {
      localObject2 = localObject1;
      Log.e("RingtoneManager", "ex " + paramContext);
      localObject2 = localObject3;
      return (Uri)localObject3;
    }
    finally
    {
      if (localObject2 == null) {
        break label261;
      }
      ((Cursor)localObject2).close();
    }
    return (Uri)localObject2;
  }
  
  private static String getDefaultRingtoneFileName(Context paramContext, String paramString)
  {
    return SystemProperties.get("ro.config." + paramString);
  }
  
  private Cursor getInternalRingtones()
  {
    return query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, INTERNAL_COLUMNS, constructBooleanTrueWhereClause(this.mFilterColumns) + " and " + "_data" + " like ?", constructWhereClause(this.mType), "_display_name");
  }
  
  public static ResultRing getLocatRingtoneTitle(Context paramContext, Uri paramUri, int paramInt1, int paramInt2)
  {
    localResultRing = new ResultRing(null, paramUri);
    if ((paramUri == null) || (RingtoneManager.isDefault(paramUri))) {
      return localResultRing;
    }
    OPMyLog.d("RingtoneManager", "ringtoneUri:" + paramUri);
    Object localObject2 = null;
    Cursor localCursor1 = null;
    localCursor2 = localCursor1;
    localObject1 = localObject2;
    try
    {
      if (paramUri.getPath().contains("internal"))
      {
        localCursor2 = localCursor1;
        localObject1 = localObject2;
      }
      for (localCursor1 = paramContext.getContentResolver().query(paramUri, new String[] { "title", "_data", "mime_type" }, null, null, null); localCursor1 != null; localCursor1 = paramContext.getContentResolver().query(paramUri, new String[] { "_display_name", "_data", "mime_type", "title" }, null, null, null))
      {
        localCursor2 = localCursor1;
        localObject1 = localCursor1;
        if (!localCursor1.moveToFirst()) {
          break;
        }
        localCursor2 = localCursor1;
        localObject1 = localCursor1;
        paramContext = updateRingtoneForInternal(paramContext, paramUri, localCursor1, paramInt1, paramInt2);
        localCursor2 = localCursor1;
        localObject1 = localCursor1;
        localResultRing.title = OPUtils.getFileNameNoEx(localCursor1.getString(0));
        localCursor2 = localCursor1;
        localObject1 = localCursor1;
        if (TextUtils.isEmpty(localResultRing.title))
        {
          localCursor2 = localCursor1;
          localObject1 = localCursor1;
          localResultRing.title = localCursor1.getString(localCursor1.getColumnIndex("title"));
        }
        localCursor2 = localCursor1;
        localObject1 = localCursor1;
        localResultRing.ringUri = paramContext;
        if (localCursor1 != null) {
          localCursor1.close();
        }
        return localResultRing;
        localCursor2 = localCursor1;
        localObject1 = localObject2;
      }
      if (localCursor1 != null) {
        localCursor1.close();
      }
    }
    catch (SQLiteException paramContext)
    {
      do
      {
        localObject1 = localCursor2;
        Log.e("RingtoneManager", "ex " + paramContext);
      } while (localCursor2 == null);
      localCursor2.close();
      return localResultRing;
    }
    finally
    {
      if (localObject1 == null) {
        break label383;
      }
      ((Cursor)localObject1).close();
    }
    return localResultRing;
  }
  
  public static Ringtone getRingtone(Context paramContext, Uri paramUri)
  {
    return RingtoneManager.getRingtone(paramContext, paramUri);
  }
  
  public static String getSettingForType(int paramInt)
  {
    if (paramInt == 1) {
      return "ringtone";
    }
    if (paramInt == 2) {
      return "notification_sound";
    }
    if (paramInt == 4) {
      return "alarm_alert";
    }
    if (paramInt == 100) {
      return "mms_notification";
    }
    return null;
  }
  
  public static Uri getStaticDefaultRingtoneUri(Context paramContext)
  {
    Object localObject4 = null;
    Object localObject3 = null;
    if (mDefaultUri != null) {
      return mDefaultUri;
    }
    localObject2 = localObject3;
    localObject1 = localObject4;
    try
    {
      IContentProvider localIContentProvider = paramContext.getContentResolver().acquireProvider("media");
      localObject2 = localObject3;
      localObject1 = localObject4;
      Uri localUri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
      localObject2 = localObject3;
      localObject1 = localObject4;
      paramContext = getDefaultRingtoneFileName(paramContext, "ringtone");
      localObject2 = localObject3;
      localObject1 = localObject4;
      paramContext = localIContentProvider.query(null, localUri, new String[] { "_id" }, "_display_name=?", new String[] { paramContext }, null, null);
      if (paramContext != null)
      {
        localObject2 = paramContext;
        localObject1 = paramContext;
        if (paramContext.getCount() > 0)
        {
          localObject2 = paramContext;
          localObject1 = paramContext;
          if (paramContext.moveToFirst())
          {
            localObject2 = paramContext;
            localObject1 = paramContext;
            long l = paramContext.getLong(0);
            localObject2 = paramContext;
            localObject1 = paramContext;
            mDefaultUri = ContentUris.withAppendedId(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, l);
          }
        }
      }
      if (paramContext != null) {
        paramContext.close();
      }
    }
    catch (RemoteException paramContext)
    {
      for (;;)
      {
        localObject1 = localObject2;
        OPMyLog.e("RingtoneManager", "RemoteException: ", paramContext);
        if (localObject2 != null) {
          ((Cursor)localObject2).close();
        }
      }
    }
    finally
    {
      if (localObject1 == null) {
        break label214;
      }
      ((Cursor)localObject1).close();
    }
    return mDefaultUri;
  }
  
  public static Uri getUriFromCursor(Cursor paramCursor)
  {
    return ContentUris.withAppendedId(Uri.parse(paramCursor.getString(2)), paramCursor.getLong(0));
  }
  
  public static boolean isDefault(Uri paramUri)
  {
    return RingtoneManager.isDefault(paramUri);
  }
  
  public static boolean isRingSimSwitchOn(Context paramContext)
  {
    return Settings.System.getInt(paramContext.getContentResolver(), "op_sim_sw", 0) == 1;
  }
  
  public static boolean isSystemRingtone(Context paramContext, Uri paramUri, int paramInt)
  {
    if (paramUri == null) {
      return false;
    }
    Object localObject4 = null;
    Object localObject3 = null;
    localObject2 = localObject3;
    localObject1 = localObject4;
    try
    {
      if (!"media".equals(paramUri.getAuthority())) {
        return false;
      }
      localObject2 = localObject3;
      localObject1 = localObject4;
      paramContext = paramContext.getContentResolver().query(paramUri, null, "_data like ?", constructWhereClause(paramInt), null);
      if (paramContext != null)
      {
        localObject2 = paramContext;
        localObject1 = paramContext;
        paramInt = paramContext.getCount();
        if (paramInt > 0)
        {
          if (paramContext != null) {
            paramContext.close();
          }
          return true;
        }
      }
      if (paramContext != null) {
        paramContext.close();
      }
    }
    catch (SQLiteException paramContext)
    {
      localObject1 = localObject2;
      Log.e("RingtoneManager", "ex " + paramContext);
      return false;
    }
    finally
    {
      if (localObject1 == null) {
        break label157;
      }
      ((Cursor)localObject1).close();
    }
    return false;
  }
  
  private Cursor query(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    if (this.mActivity != null) {
      return this.mActivity.managedQuery(paramUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2);
    }
    return this.mContext.getContentResolver().query(paramUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2);
  }
  
  public static Uri ringtoneRestoreFromDefault(Context paramContext, int paramInt, Uri paramUri)
  {
    Object localObject2 = getSettingForType(paramInt);
    Object localObject1 = null;
    if ((paramInt & 0x1) != 0) {
      localObject1 = "is_ringtone";
    }
    if ((paramInt & 0x2) != 0) {
      localObject1 = "is_notification";
    }
    Object localObject4 = localObject1;
    if ((paramInt & 0x4) != 0) {
      localObject4 = "is_alarm";
    }
    localObject1 = paramUri;
    Object localObject3;
    String str;
    Object localObject7;
    Object localObject6;
    Object localObject5;
    if (localObject4 != null)
    {
      localObject3 = localObject2;
      if (((String)localObject2).startsWith("ringtone")) {
        localObject3 = "ringtone";
      }
      localObject1 = SystemProperties.get("ro.config." + "ringtone");
      localObject1 = SystemProperties.get("ro.config." + (String)localObject3, (String)localObject1);
      str = ((String)localObject1).substring(0, ((String)localObject1).lastIndexOf("."));
      OPMyLog.d("RingtoneManager", "ringtoneRestoreFromDefault: title = " + str);
      localObject7 = null;
      localObject6 = null;
      localObject2 = localObject6;
      localObject5 = paramUri;
      localObject1 = localObject7;
    }
    try
    {
      paramContext = paramContext.getContentResolver().acquireProvider("media");
      localObject2 = localObject6;
      localObject5 = paramUri;
      localObject1 = localObject7;
      Uri localUri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
      localObject2 = localObject6;
      localObject5 = paramUri;
      localObject1 = localObject7;
      localObject4 = (String)localObject4 + "=1 and " + "title" + "=?";
      localObject2 = localObject6;
      localObject5 = paramUri;
      localObject1 = localObject7;
      localObject4 = paramContext.query(null, localUri, new String[] { "_id" }, (String)localObject4, new String[] { str }, null, null);
      paramContext = paramUri;
      if (localObject4 != null)
      {
        paramContext = paramUri;
        localObject2 = localObject4;
        localObject5 = paramUri;
        localObject1 = localObject4;
        if (((Cursor)localObject4).getCount() > 0)
        {
          paramContext = paramUri;
          localObject2 = localObject4;
          localObject5 = paramUri;
          localObject1 = localObject4;
          if (((Cursor)localObject4).moveToFirst())
          {
            localObject2 = localObject4;
            localObject5 = paramUri;
            localObject1 = localObject4;
            long l = ((Cursor)localObject4).getLong(0);
            localObject2 = localObject4;
            localObject5 = paramUri;
            localObject1 = localObject4;
            paramContext = ContentUris.withAppendedId(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, l);
            localObject2 = localObject4;
            localObject5 = paramContext;
            localObject1 = localObject4;
            Log.d("RingtoneManager", "ringtoneRestoreFromDefault: [" + (String)localObject3 + "] = " + paramContext.toString());
          }
        }
      }
      IoUtils.closeQuietly((AutoCloseable)localObject4);
      localObject1 = paramContext;
      return (Uri)localObject1;
    }
    catch (RemoteException paramContext)
    {
      localObject1 = localObject2;
      Log.w("RingtoneManager", "RemoteException: ", paramContext);
      return (Uri)localObject5;
    }
    finally
    {
      IoUtils.closeQuietly((AutoCloseable)localObject1);
    }
  }
  
  public static void setActualDefaultRingtoneUri(Context paramContext, int paramInt, Uri paramUri)
  {
    Object localObject2 = null;
    Object localObject3 = getSettingForType(paramInt);
    if (localObject3 == null) {
      return;
    }
    ContentResolver localContentResolver = paramContext.getContentResolver();
    if (paramUri != null)
    {
      localObject1 = paramUri.toString();
      Settings.System.putString(localContentResolver, (String)localObject3, (String)localObject1);
      if (paramInt == 1)
      {
        localObject3 = paramContext.getContentResolver();
        if (paramUri == null) {
          break label100;
        }
      }
    }
    label100:
    for (Object localObject1 = paramUri.toString();; localObject1 = "none")
    {
      Settings.System.putString((ContentResolver)localObject3, "op_ringtone_df", (String)localObject1);
      localObject1 = paramContext.getContentResolver();
      paramContext = (Context)localObject2;
      if (paramUri != null) {
        paramContext = paramUri.toString();
      }
      Settings.System.putString((ContentResolver)localObject1, "ringtone_2", paramContext);
      return;
      localObject1 = null;
      break;
    }
  }
  
  public static void setActualRingtoneUriBySubId(Context paramContext, int paramInt, Uri paramUri)
  {
    if ((paramInt < 0) || (paramInt >= 2)) {
      return;
    }
    Object localObject2;
    ContentResolver localContentResolver;
    Object localObject1;
    if (paramInt == 0)
    {
      localObject2 = "ringtone";
      localContentResolver = paramContext.getContentResolver();
      if (paramUri != null)
      {
        localObject1 = paramUri.toString();
        Settings.System.putString(localContentResolver, "op_ringtone1_df", (String)localObject1);
        localObject1 = localObject2;
        localObject2 = paramContext.getContentResolver();
        if (paramUri == null) {
          break label134;
        }
      }
    }
    label134:
    for (paramContext = paramUri.toString();; paramContext = null)
    {
      Settings.System.putString((ContentResolver)localObject2, (String)localObject1, paramContext);
      return;
      localObject1 = "none";
      break;
      localObject2 = "ringtone_" + (paramInt + 1);
      localContentResolver = paramContext.getContentResolver();
      if (paramUri != null) {}
      for (localObject1 = paramUri.toString();; localObject1 = "none")
      {
        Settings.System.putString(localContentResolver, "op_ringtone2_df", (String)localObject1);
        localObject1 = localObject2;
        break;
      }
    }
  }
  
  private void setFilterColumnsList(int paramInt)
  {
    List localList = this.mFilterColumns;
    localList.clear();
    if ((paramInt & 0x1) != 0) {
      localList.add("is_ringtone");
    }
    if ((paramInt & 0x2) != 0) {
      localList.add("is_notification");
    }
    if ((paramInt & 0x64) != 0) {
      localList.add("is_notification");
    }
    if ((paramInt & 0x4) != 0) {
      localList.add("is_alarm");
    }
  }
  
  public static void setRingSimSwitch(Context paramContext, int paramInt)
  {
    Settings.System.putInt(paramContext.getContentResolver(), "op_sim_sw", paramInt);
  }
  
  public static long transToId(String paramString)
  {
    try
    {
      long l = Long.valueOf(paramString).longValue();
      return l;
    }
    catch (Exception paramString)
    {
      paramString.printStackTrace();
    }
    return -1L;
  }
  
  public static void updateActualRingtone(Context paramContext)
  {
    Object localObject2 = null;
    String str = Settings.System.getString(paramContext.getContentResolver(), "op_ringtone_df");
    Uri localUri = getStaticDefaultRingtoneUri(paramContext);
    ContentResolver localContentResolver = paramContext.getContentResolver();
    Object localObject1;
    if (str != null) {
      if (str.equals("none"))
      {
        localObject1 = null;
        Settings.System.putString(localContentResolver, "ringtone", (String)localObject1);
        localObject1 = paramContext.getContentResolver();
        if (str == null) {
          break label117;
        }
        if (!str.equals("none")) {
          break label108;
        }
        paramContext = (Context)localObject2;
      }
    }
    for (;;)
    {
      Settings.System.putString((ContentResolver)localObject1, "ringtone_2", paramContext);
      return;
      localObject1 = str.toString();
      break;
      if (localUri != null)
      {
        localObject1 = localUri.toString();
        break;
      }
      localObject1 = null;
      break;
      label108:
      paramContext = str.toString();
      continue;
      label117:
      paramContext = (Context)localObject2;
      if (localUri != null) {
        paramContext = localUri.toString();
      }
    }
  }
  
  public static void updateActualRingtone2(Context paramContext)
  {
    Object localObject2 = null;
    Object localObject1 = Settings.System.getString(paramContext.getContentResolver(), "op_ringtone1_df");
    String str = Settings.System.getString(paramContext.getContentResolver(), "op_ringtone2_df");
    Uri localUri = getStaticDefaultRingtoneUri(paramContext);
    ContentResolver localContentResolver = paramContext.getContentResolver();
    if (localObject1 != null) {
      if (((String)localObject1).equals("none"))
      {
        localObject1 = null;
        Settings.System.putString(localContentResolver, "ringtone", (String)localObject1);
        localObject1 = paramContext.getContentResolver();
        if (str == null) {
          break label124;
        }
        if (!str.equals("none")) {
          break label115;
        }
        paramContext = (Context)localObject2;
      }
    }
    for (;;)
    {
      Settings.System.putString((ContentResolver)localObject1, "ringtone_2", paramContext);
      return;
      localObject1 = ((String)localObject1).toString();
      break;
      if (localUri != null)
      {
        localObject1 = localUri.toString();
        break;
      }
      localObject1 = null;
      break;
      label115:
      paramContext = str.toString();
      continue;
      label124:
      paramContext = (Context)localObject2;
      if (localUri != null) {
        paramContext = localUri.toString();
      }
    }
  }
  
  public static void updateDb(Context paramContext, Uri paramUri, int paramInt)
  {
    if (paramUri == null) {
      return;
    }
    ContentValues localContentValues = new ContentValues();
    switch (paramInt)
    {
    default: 
      localContentValues.put("is_ringtone", Boolean.valueOf(true));
    }
    for (;;)
    {
      paramContext.getContentResolver().update(paramUri, localContentValues, null, null);
      return;
      localContentValues.put("is_alarm", Boolean.valueOf(true));
      continue;
      localContentValues.put("is_notification", Boolean.valueOf(true));
    }
  }
  
  public static void updateDefaultRingtone(Context paramContext)
  {
    Object localObject = Settings.System.getString(paramContext.getContentResolver(), "ringtone");
    String str = Settings.System.getString(paramContext.getContentResolver(), "ringtone_2");
    ContentResolver localContentResolver = paramContext.getContentResolver();
    if (localObject != null)
    {
      localObject = ((String)localObject).toString();
      Settings.System.putString(localContentResolver, "op_ringtone1_df", (String)localObject);
      localObject = paramContext.getContentResolver();
      if (str == null) {
        break label72;
      }
    }
    label72:
    for (paramContext = str.toString();; paramContext = "none")
    {
      Settings.System.putString((ContentResolver)localObject, "op_ringtone2_df", paramContext);
      return;
      localObject = "none";
      break;
    }
  }
  
  public static Uri updateRingtoneForInternal(Context paramContext, Uri paramUri, Cursor paramCursor, int paramInt1, int paramInt2)
  {
    if (!paramUri.toString().contains(MediaStore.Audio.Media.INTERNAL_CONTENT_URI.toString())) {
      return paramUri;
    }
    Object localObject = paramCursor.getString(1);
    if ((localObject == null) || ((((String)localObject).startsWith("/system/media/audio/ringtones/")) && (paramInt1 == 1))) {
      return paramUri;
    }
    paramUri = ((String)localObject).replace("/storage/emulated/legacy", Environment.getExternalStorageDirectory().getAbsolutePath());
    localObject = paramCursor.getString(0);
    String str = paramCursor.getString(2);
    Uri localUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    paramCursor = paramContext.getContentResolver().query(localUri, new String[] { "_id" }, "_data=?", new String[] { paramUri }, null);
    if ((paramCursor != null) && (paramCursor.moveToFirst()))
    {
      paramUri = ContentUris.withAppendedId(localUri, paramCursor.getLong(0));
      localObject = new ContentValues();
      if (paramInt1 == 1) {
        ((ContentValues)localObject).put("is_ringtone", Boolean.valueOf(true));
      }
      for (;;)
      {
        paramContext.getContentResolver().update(paramUri, (ContentValues)localObject, null, null);
        if (paramCursor != null) {
          paramCursor.close();
        }
        if (paramInt2 <= 0) {
          break;
        }
        setActualRingtoneUriBySubId(paramContext, paramInt2 - 1, paramUri);
        return paramUri;
        if ((paramInt1 == 2) || (paramInt1 == 100)) {
          ((ContentValues)localObject).put("is_notification", Boolean.valueOf(true));
        } else {
          ((ContentValues)localObject).put("is_alarm", Boolean.valueOf(true));
        }
      }
    }
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("_data", paramUri);
    localContentValues.put("title", (String)localObject);
    localContentValues.put("mime_type", str);
    if (paramInt1 == 1) {
      localContentValues.put("is_ringtone", Boolean.valueOf(true));
    }
    for (;;)
    {
      paramContext.getContentResolver().delete(localUri, "_data=\"" + paramUri + "\"", null);
      paramUri = paramContext.getContentResolver().insert(localUri, localContentValues);
      break;
      if ((paramInt1 == 2) || (paramInt1 == 100)) {
        localContentValues.put("is_notification", Boolean.valueOf(true));
      } else {
        localContentValues.put("is_alarm", Boolean.valueOf(true));
      }
    }
    setActualDefaultRingtoneUri(paramContext, paramInt1, paramUri);
    return paramUri;
  }
  
  public static Uri updateSigleRingtone(Context paramContext)
  {
    String str = Settings.System.getString(paramContext.getContentResolver(), "ringtone");
    Uri localUri = getStaticDefaultRingtoneUri(paramContext);
    Object localObject2 = null;
    Object localObject3 = null;
    Object localObject4 = null;
    Object localObject1;
    if (str == null)
    {
      localObject1 = null;
      localObject3 = localObject1;
      if (localObject4 != null)
      {
        ((Cursor)localObject4).close();
        localObject3 = localObject1;
      }
      label47:
      localObject1 = paramContext.getContentResolver();
      if (localObject3 == null) {
        break label207;
      }
    }
    label207:
    for (paramContext = ((Uri)localObject3).toString();; paramContext = "none")
    {
      Settings.System.putString((ContentResolver)localObject1, "op_ringtone_df", paramContext);
      if (localObject3 == null) {
        break label213;
      }
      return (Uri)localObject3;
      try
      {
        Cursor localCursor = paramContext.getContentResolver().query(Uri.parse(str), null, null, null, null);
        localObject4 = localCursor;
        localObject1 = localUri;
        if (localCursor == null) {
          break;
        }
        localObject4 = localCursor;
        localObject1 = localUri;
        localObject2 = localCursor;
        localObject3 = localCursor;
        if (localCursor.getCount() <= 0) {
          break;
        }
        localObject2 = localCursor;
        localObject3 = localCursor;
        localObject1 = Uri.parse(str);
        localObject4 = localCursor;
        break;
      }
      catch (SQLiteException localSQLiteException)
      {
        localObject3 = localObject2;
        Log.e("RingtoneManager", "ex " + localSQLiteException);
        localObject3 = localUri;
        if (localObject2 == null) {
          break label47;
        }
        ((Cursor)localObject2).close();
        localObject3 = localUri;
        break label47;
      }
      finally
      {
        if (localObject3 != null) {
          ((Cursor)localObject3).close();
        }
      }
    }
    label213:
    return null;
  }
  
  public Cursor getCursor()
  {
    if ((this.mCursor != null) && (this.mCursor.requery())) {
      return this.mCursor;
    }
    Cursor localCursor = getInternalRingtones();
    this.mCursor = localCursor;
    return localCursor;
  }
  
  public int inferStreamType()
  {
    switch (this.mType)
    {
    default: 
      return 2;
    case 4: 
      return 4;
    }
    return 5;
  }
  
  public void setType(int paramInt)
  {
    if (this.mCursor != null) {
      throw new IllegalStateException("Setting filter columns should be done before querying for ringtones.");
    }
    this.mType = paramInt;
    setFilterColumnsList(paramInt);
  }
  
  public static class ResultRing
  {
    Uri ringUri;
    String title;
    
    public ResultRing(String paramString, Uri paramUri)
    {
      this.title = paramString;
      this.ringUri = paramUri;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ringtone\OPRingtoneManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */