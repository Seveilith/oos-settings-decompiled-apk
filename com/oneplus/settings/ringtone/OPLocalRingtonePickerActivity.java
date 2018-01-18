package com.oneplus.settings.ringtone;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore.Audio.Media;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.oneplus.settings.utils.OPUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OPLocalRingtonePickerActivity
  extends OPRingtoneBaseActivity
{
  private static final String ALARMS_PATH = SDCARD_PATH + "/Alarms/";
  private static final String AUDIO_FILE_SELECTION_ALL;
  private static final String AUDIO_FILE_SELECTION_PART;
  public static String AUDIO_SECTION;
  private static final long MINTIME = 60000L;
  private static final String NOTIFICATIONS_PATH;
  private static final String[] PROJECTION;
  private static final String RECORD_PATH = SDCARD_PATH + "/Record/";
  private static final Uri RECORD_URI;
  private static final String RINGTONE_PATH;
  private static final String SDCARD_PATH;
  public static String SECTION_AFTER = " and _data NOT LIKE '%/.%' and _data NOT LIKE '%cache%' and _data NOT LIKE '%/res/%' and _data NOT LIKE '%/plugins/%' and _data NOT LIKE '%/temp/%' and _data NOT LIKE '%/tencent/MobileQQ/qbiz/%' and _data NOT LIKE '%/tencent/MobileQQ/PhotoPlus/%' and _data NOT LIKE '%/thumb/%' and _data NOT LIKE '%/oem_log/%'";
  private static final String[] SELECTION_ARGS_ALL = { RECORD_PATH + "%" };
  private static final String[] SELECTION_ARGS_PART = { RECORD_PATH + "%" };
  private boolean isFirst = true;
  private ListView mListView;
  private TextView mNofileView;
  private OPLocalRingtoneAdapter mOPLocalRingtoneAdapter;
  private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener()
  {
    public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      paramAnonymousAdapterView = (OPLocalRingtoneAdapter.RingtoneData)paramAnonymousAdapterView.getItemAtPosition(paramAnonymousInt);
      OPLocalRingtonePickerActivity.this.mUriForDefaultItem = paramAnonymousAdapterView.mUri;
      OPLocalRingtonePickerActivity.-wrap3(OPLocalRingtonePickerActivity.this, OPLocalRingtonePickerActivity.this.mUriForDefaultItem);
      if (OPLocalRingtonePickerActivity.-get10(OPLocalRingtonePickerActivity.this) != null)
      {
        OPLocalRingtonePickerActivity.-get10(OPLocalRingtonePickerActivity.this).stopThread();
        OPLocalRingtonePickerActivity.-set2(OPLocalRingtonePickerActivity.this, null);
      }
      OPLocalRingtonePickerActivity.-set2(OPLocalRingtonePickerActivity.this, new OPLocalRingtonePickerActivity.SetExternalThread(OPLocalRingtonePickerActivity.this, paramAnonymousAdapterView));
      OPLocalRingtonePickerActivity.-get10(OPLocalRingtonePickerActivity.this).start();
    }
  };
  private AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener()
  {
    public void onItemSelected(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      if (OPLocalRingtonePickerActivity.-get6(OPLocalRingtonePickerActivity.this))
      {
        OPLocalRingtonePickerActivity.-set0(OPLocalRingtonePickerActivity.this, false);
        return;
      }
      OPLocalRingtonePickerActivity.this.stopAnyPlayingRingtone();
      switch (paramAnonymousInt)
      {
      default: 
        return;
      case 0: 
        OPLocalRingtonePickerActivity.-wrap2(OPLocalRingtonePickerActivity.this, 0);
        return;
      }
      OPLocalRingtonePickerActivity.-wrap2(OPLocalRingtonePickerActivity.this, 1);
    }
    
    public void onNothingSelected(AdapterView<?> paramAnonymousAdapterView) {}
  };
  private ProgressBar mProgressBar;
  private SetExternalThread mSetExternalThread;
  private List mSystemRings = null;
  private WorkAsyncTask mWorkAsyncTask;
  
  static
  {
    AUDIO_SECTION = "(_data LIKE '%.wma' or _data LIKE '%.mp3' or _data LIKE '%.aac' or _data LIKE '%.mid' or _data LIKE '%.ogg' or _data LIKE '%.flac' or _data LIKE '%.ape' or _data LIKE '%.ra' or _data LIKE '%.mod' or _data LIKE '%.m4a' or _data LIKE '%.amr' )" + SECTION_AFTER;
    RECORD_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    PROJECTION = new String[] { "_id", "_display_name", "\"" + MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "\"", "_data", "mime_type", "title" };
    AUDIO_FILE_SELECTION_ALL = "_data not like ? and" + AUDIO_SECTION;
    AUDIO_FILE_SELECTION_PART = "_data not like ? and duration < 60000 and " + AUDIO_SECTION;
    SDCARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    RINGTONE_PATH = SDCARD_PATH + "/Ringtones/";
    NOTIFICATIONS_PATH = SDCARD_PATH + "/Notifications/";
  }
  
  private String checkDir()
  {
    String str = RINGTONE_PATH;
    if ((this.mType == 2) || (this.mType == 100)) {
      str = NOTIFICATIONS_PATH;
    }
    for (;;)
    {
      File localFile = new File(str);
      if (!localFile.exists()) {
        localFile.mkdirs();
      }
      return str;
      if (this.mType == 4) {
        str = ALARMS_PATH;
      }
    }
  }
  
  private void initActionbar()
  {
    ActionBar localActionBar = getActionBar();
    localActionBar.setDisplayOptions(16);
    localActionBar.setDisplayHomeAsUpEnabled(true);
    localActionBar.setDisplayShowTitleEnabled(false);
    View localView = View.inflate(this, 2130968869, null);
    Spinner localSpinner = (Spinner)localView.findViewById(2131362389);
    ArrayAdapter localArrayAdapter = new ArrayAdapter(this, 2130968868, 16908308, getResources().getStringArray(2131427476));
    localArrayAdapter.setDropDownViewResource(17367049);
    localSpinner.setAdapter(localArrayAdapter);
    localSpinner.setOnItemSelectedListener(this.mOnItemSelectedListener);
    localActionBar.setCustomView(localView);
  }
  
  /* Error */
  private boolean isApeFile(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_1
    //   1: astore 5
    //   3: ldc_w 306
    //   6: aload_1
    //   7: invokevirtual 310	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   10: ifeq +90 -> 100
    //   13: aconst_null
    //   14: astore_1
    //   15: aconst_null
    //   16: astore 8
    //   18: aconst_null
    //   19: astore 7
    //   21: aconst_null
    //   22: astore 5
    //   24: new 312	android/media/MediaExtractor
    //   27: dup
    //   28: invokespecial 313	android/media/MediaExtractor:<init>	()V
    //   31: astore 6
    //   33: aload 6
    //   35: aload_2
    //   36: invokevirtual 316	android/media/MediaExtractor:setDataSource	(Ljava/lang/String;)V
    //   39: iconst_0
    //   40: istore_3
    //   41: aload 5
    //   43: astore_1
    //   44: iload_3
    //   45: aload 6
    //   47: invokevirtual 320	android/media/MediaExtractor:getTrackCount	()I
    //   50: if_icmpge +34 -> 84
    //   53: aload 6
    //   55: iload_3
    //   56: invokevirtual 324	android/media/MediaExtractor:getTrackFormat	(I)Landroid/media/MediaFormat;
    //   59: ldc_w 326
    //   62: invokevirtual 332	android/media/MediaFormat:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   65: astore_1
    //   66: aload_1
    //   67: ifnull +50 -> 117
    //   70: aload_1
    //   71: ldc_w 334
    //   74: invokevirtual 338	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   77: istore 4
    //   79: iload 4
    //   81: ifeq +36 -> 117
    //   84: aload_1
    //   85: astore 5
    //   87: aload 6
    //   89: ifnull +11 -> 100
    //   92: aload 6
    //   94: invokevirtual 341	android/media/MediaExtractor:release	()V
    //   97: aload_1
    //   98: astore 5
    //   100: aload 5
    //   102: ifnull +13 -> 115
    //   105: aload 5
    //   107: invokestatic 347	android/media/MediaFile:getFileTypeForMimeType	(Ljava/lang/String;)I
    //   110: bipush 10
    //   112: if_icmple +76 -> 188
    //   115: iconst_1
    //   116: ireturn
    //   117: iload_3
    //   118: iconst_1
    //   119: iadd
    //   120: istore_3
    //   121: goto -80 -> 41
    //   124: astore 5
    //   126: aload 8
    //   128: astore_2
    //   129: aload_2
    //   130: astore_1
    //   131: ldc_w 349
    //   134: new 142	java/lang/StringBuilder
    //   137: dup
    //   138: invokespecial 145	java/lang/StringBuilder:<init>	()V
    //   141: ldc_w 351
    //   144: invokevirtual 151	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   147: aload 5
    //   149: invokevirtual 172	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   152: invokevirtual 154	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   155: invokestatic 357	com/oneplus/settings/ringtone/OPMyLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   158: aload 7
    //   160: astore 5
    //   162: aload_2
    //   163: ifnull -63 -> 100
    //   166: aload_2
    //   167: invokevirtual 341	android/media/MediaExtractor:release	()V
    //   170: aload 7
    //   172: astore 5
    //   174: goto -74 -> 100
    //   177: astore_2
    //   178: aload_1
    //   179: ifnull +7 -> 186
    //   182: aload_1
    //   183: invokevirtual 341	android/media/MediaExtractor:release	()V
    //   186: aload_2
    //   187: athrow
    //   188: iconst_0
    //   189: ireturn
    //   190: astore_2
    //   191: aload 6
    //   193: astore_1
    //   194: goto -16 -> 178
    //   197: astore 5
    //   199: aload 6
    //   201: astore_2
    //   202: goto -73 -> 129
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	205	0	this	OPLocalRingtonePickerActivity
    //   0	205	1	paramString1	String
    //   0	205	2	paramString2	String
    //   40	81	3	i	int
    //   77	3	4	bool	boolean
    //   1	105	5	str	String
    //   124	24	5	localIOException1	java.io.IOException
    //   160	13	5	localObject1	Object
    //   197	1	5	localIOException2	java.io.IOException
    //   31	169	6	localMediaExtractor	android.media.MediaExtractor
    //   19	152	7	localObject2	Object
    //   16	111	8	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   24	33	124	java/io/IOException
    //   24	33	177	finally
    //   131	158	177	finally
    //   33	39	190	finally
    //   44	66	190	finally
    //   70	79	190	finally
    //   33	39	197	java/io/IOException
    //   44	66	197	java/io/IOException
    //   70	79	197	java/io/IOException
  }
  
  private void startTask(int paramInt)
  {
    if (this.mWorkAsyncTask != null)
    {
      this.mWorkAsyncTask.setClose();
      this.mWorkAsyncTask = null;
    }
    this.mProgressBar.setVisibility(0);
    this.mNofileView.setVisibility(8);
    this.mListView.setVisibility(8);
    this.mWorkAsyncTask = new WorkAsyncTask(getContentResolver());
    this.mWorkAsyncTask.execute(new Integer[] { Integer.valueOf(paramInt) });
  }
  
  private void updateChecks(Uri paramUri)
  {
    if ((this.mSystemRings == null) || (this.mOPLocalRingtoneAdapter == null)) {
      return;
    }
    Iterator localIterator = this.mSystemRings.iterator();
    while (localIterator.hasNext())
    {
      OPLocalRingtoneAdapter.RingtoneData localRingtoneData = (OPLocalRingtoneAdapter.RingtoneData)localIterator.next();
      localRingtoneData.isCheck = localRingtoneData.mUri.equals(paramUri);
    }
    this.mOPLocalRingtoneAdapter.notifyDataSetChanged();
  }
  
  private Uri updateDb(OPLocalRingtoneAdapter.RingtoneData paramRingtoneData, String paramString)
  {
    Uri localUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    Cursor localCursor = getContentResolver().query(localUri, new String[] { "_id" }, "_data=?", new String[] { paramString }, null);
    if ((localCursor != null) && (localCursor.moveToFirst()))
    {
      paramRingtoneData = ContentUris.withAppendedId(localUri, localCursor.getLong(0));
      if (localCursor != null) {
        localCursor.close();
      }
      OPMyLog.d("chenhl", "defaultitem:" + paramRingtoneData + " path:" + paramString);
      return paramRingtoneData;
    }
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("_data", paramString);
    localContentValues.put("title", paramRingtoneData.title);
    localContentValues.put("mime_type", paramRingtoneData.mimetype);
    if (this.mType == 1) {
      localContentValues.put("is_ringtone", Boolean.valueOf(true));
    }
    for (;;)
    {
      getContentResolver().delete(localUri, "_data=\"" + paramString + "\"", null);
      paramRingtoneData = getContentResolver().insert(localUri, localContentValues);
      break;
      if ((this.mType == 2) || (this.mType == 100)) {
        localContentValues.put("is_notification", Boolean.valueOf(true));
      } else {
        localContentValues.put("is_alarm", Boolean.valueOf(true));
      }
    }
  }
  
  private Uri updateExternalFile(OPLocalRingtoneAdapter.RingtoneData paramRingtoneData)
  {
    OPMyLog.d("chenhl", "getKey:" + paramRingtoneData.mUri);
    Object localObject = paramRingtoneData.filepath;
    OPMyLog.d("chenhl", "path:" + (String)localObject);
    File localFile = new File((String)localObject);
    if (!localFile.exists())
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          Toast.makeText(OPLocalRingtonePickerActivity.this, OPLocalRingtonePickerActivity.this.getString(2131690221), 0).show();
        }
      });
      return null;
    }
    playRingtone(300, this.mUriForDefaultItem);
    if ((localObject == null) || (((String)localObject).startsWith("/storage/emulated/legacy")) || (((String)localObject).startsWith(SDCARD_PATH))) {
      return this.mUriForDefaultItem;
    }
    localObject = new File(checkDir() + localFile.getName());
    if (!((File)localObject).exists()) {
      copyFile(localFile, (File)localObject);
    }
    return updateDb(paramRingtoneData, ((File)localObject).getAbsolutePath());
  }
  
  public void copyFile(File paramFile1, File paramFile2)
  {
    int i = 0;
    try
    {
      if (paramFile1.exists())
      {
        paramFile1 = new FileInputStream(paramFile1);
        paramFile2 = new FileOutputStream(paramFile2);
        byte[] arrayOfByte = new byte['֤'];
        for (;;)
        {
          int j = paramFile1.read(arrayOfByte);
          if (j == -1) {
            break;
          }
          i += j;
          paramFile2.write(arrayOfByte, 0, j);
        }
      }
      return;
    }
    catch (Exception paramFile1)
    {
      paramFile1.printStackTrace();
    }
    paramFile1.close();
    paramFile2.close();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    if (paramBundle != null)
    {
      String str = paramBundle.getString("key_selected_item_uri");
      if (str != null) {
        this.mUriForDefaultItem = Uri.parse(str);
      }
    }
    super.onCreate(paramBundle);
    setContentView(2130968836);
    initActionbar();
    this.mListView = getListView();
    this.mNofileView = ((TextView)findViewById(2131362368));
    this.mProgressBar = ((ProgressBar)findViewById(2131362369));
    this.mListView.setEmptyView(this.mNofileView);
    this.mListView.setOnItemClickListener(this.mOnItemClickListener);
    startTask(0);
  }
  
  protected void onDestroy()
  {
    if (this.mWorkAsyncTask != null)
    {
      this.mWorkAsyncTask.setClose();
      this.mWorkAsyncTask = null;
    }
    super.onDestroy();
  }
  
  protected void updateSelected()
  {
    if (this.mSystemRings == null) {}
  }
  
  class SetExternalThread
    extends Thread
  {
    private boolean isClose;
    private OPLocalRingtoneAdapter.RingtoneData mPreference;
    
    public SetExternalThread(OPLocalRingtoneAdapter.RingtoneData paramRingtoneData)
    {
      this.mPreference = paramRingtoneData;
      this.isClose = false;
    }
    
    public void run()
    {
      Uri localUri = OPLocalRingtonePickerActivity.-wrap0(OPLocalRingtonePickerActivity.this, this.mPreference);
      if ((this.isClose) || (localUri == null)) {}
      while (OPLocalRingtonePickerActivity.this.mContactsRingtone) {
        return;
      }
      if (OPLocalRingtonePickerActivity.this.getSimId() == 2) {
        OPRingtoneManager.setActualRingtoneUriBySubId(OPLocalRingtonePickerActivity.this.getApplicationContext(), 1, localUri);
      }
      for (;;)
      {
        if (OPLocalRingtonePickerActivity.this.mUriForDefaultItem.equals(localUri)) {
          OPRingtoneManager.updateDb(OPLocalRingtonePickerActivity.this.getApplicationContext(), localUri, OPLocalRingtonePickerActivity.this.mType);
        }
        OPMyLog.d("chenhl", "set ringtone ok!");
        return;
        if (OPLocalRingtonePickerActivity.this.getSimId() == 1) {
          OPRingtoneManager.setActualRingtoneUriBySubId(OPLocalRingtonePickerActivity.this.getApplicationContext(), 0, localUri);
        } else if (!OPLocalRingtonePickerActivity.this.isThreePart()) {
          OPRingtoneManager.setActualDefaultRingtoneUri(OPLocalRingtonePickerActivity.this.getApplicationContext(), OPLocalRingtonePickerActivity.this.mType, localUri);
        }
      }
    }
    
    public void stopThread()
    {
      this.isClose = true;
    }
  }
  
  private class WorkAsyncTask
    extends AsyncTask<Integer, Void, Void>
  {
    private boolean isclose = false;
    private ContentResolver resolver;
    
    public WorkAsyncTask(ContentResolver paramContentResolver)
    {
      this.resolver = paramContentResolver;
    }
    
    protected Void doInBackground(Integer... paramVarArgs)
    {
      if (paramVarArgs[0].intValue() == 0) {}
      for (paramVarArgs = this.resolver.query(OPLocalRingtonePickerActivity.-get3(), OPLocalRingtonePickerActivity.-get2(), OPLocalRingtonePickerActivity.-get0(), OPLocalRingtonePickerActivity.-get4(), "date_modified DESC,title DESC"); paramVarArgs == null; paramVarArgs = this.resolver.query(OPLocalRingtonePickerActivity.-get3(), OPLocalRingtonePickerActivity.-get2(), OPLocalRingtonePickerActivity.-get1(), OPLocalRingtonePickerActivity.-get5(), "date_modified DESC,title DESC")) {
        return null;
      }
      if (OPLocalRingtonePickerActivity.-get11(OPLocalRingtonePickerActivity.this) == null) {
        OPLocalRingtonePickerActivity.-set3(OPLocalRingtonePickerActivity.this, new ArrayList());
      }
      OPLocalRingtonePickerActivity.-get11(OPLocalRingtonePickerActivity.this).clear();
      if ((paramVarArgs != null) && (paramVarArgs.moveToFirst()))
      {
        OPMyLog.d("111", "isclose:" + this.isclose);
        if (!this.isclose) {}
      }
      else
      {
        if (paramVarArgs != null) {
          paramVarArgs.close();
        }
        return null;
      }
      Object localObject = paramVarArgs.getString(3);
      if (localObject == null) {}
      label170:
      Uri localUri;
      for (;;)
      {
        if (paramVarArgs.moveToNext())
        {
          break;
          if ((new File((String)localObject).exists()) && (!OPLocalRingtonePickerActivity.-wrap1(OPLocalRingtonePickerActivity.this, paramVarArgs.getString(4), (String)localObject)))
          {
            localUri = OPRingtoneManager.getUriFromCursor(paramVarArgs);
            if (!TextUtils.isEmpty(OPUtils.getFileNameNoEx(paramVarArgs.getString(1)))) {
              break label313;
            }
          }
        }
      }
      label313:
      for (localObject = paramVarArgs.getString(paramVarArgs.getColumnIndex("title")).toString();; localObject = paramVarArgs.getString(paramVarArgs.getColumnIndex("_display_name")).toString())
      {
        localObject = new OPLocalRingtoneAdapter.RingtoneData(localUri, (String)localObject, localUri.equals(OPLocalRingtonePickerActivity.this.mUriForDefaultItem));
        ((OPLocalRingtoneAdapter.RingtoneData)localObject).filepath = paramVarArgs.getString(3);
        ((OPLocalRingtoneAdapter.RingtoneData)localObject).mimetype = paramVarArgs.getString(4);
        OPLocalRingtonePickerActivity.-get11(OPLocalRingtonePickerActivity.this).add(localObject);
        break label170;
        break;
      }
    }
    
    protected void onPostExecute(Void paramVoid)
    {
      if (this.isclose) {
        return;
      }
      OPLocalRingtonePickerActivity.-get9(OPLocalRingtonePickerActivity.this).setVisibility(8);
      OPLocalRingtonePickerActivity.-get7(OPLocalRingtonePickerActivity.this).setVisibility(0);
      if (OPLocalRingtonePickerActivity.-get8(OPLocalRingtonePickerActivity.this) == null)
      {
        OPLocalRingtonePickerActivity.-set1(OPLocalRingtonePickerActivity.this, new OPLocalRingtoneAdapter(OPLocalRingtonePickerActivity.this, OPLocalRingtonePickerActivity.-get11(OPLocalRingtonePickerActivity.this)));
        if (OPLocalRingtonePickerActivity.-get7(OPLocalRingtonePickerActivity.this) != null) {
          OPLocalRingtonePickerActivity.-get7(OPLocalRingtonePickerActivity.this).setAdapter(OPLocalRingtonePickerActivity.-get8(OPLocalRingtonePickerActivity.this));
        }
        return;
      }
      OPLocalRingtonePickerActivity.-get8(OPLocalRingtonePickerActivity.this).notifyDataSetChanged();
    }
    
    public void setClose()
    {
      this.isclose = true;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ringtone\OPLocalRingtonePickerActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */