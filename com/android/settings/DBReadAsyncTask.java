package com.android.settings;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

public class DBReadAsyncTask
  extends AsyncTask<Void, Void, Boolean>
{
  public static final String AUTHORITY = "com.qti.smq.qualcommFeedback.provider";
  public static final String KEY_VALUE = "app_status";
  public static final String SMQ_KEY_VALUE = "app_status";
  final Uri CONTENT_URI = Uri.parse("content://com.qti.smq.qualcommFeedback.provider");
  final Uri SNAP_CONTENT_URI = Uri.withAppendedPath(this.CONTENT_URI, "smq_settings");
  Context mContext;
  
  public DBReadAsyncTask(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  protected Boolean doInBackground(Void... paramVarArgs)
  {
    paramVarArgs = this.mContext.getContentResolver().query(this.SNAP_CONTENT_URI, null, "key=?", new String[] { "app_status" }, null);
    Object localObject = this.mContext.getSharedPreferences("smqpreferences", 0);
    int i;
    if ((paramVarArgs != null) && (paramVarArgs.getCount() > 0))
    {
      paramVarArgs.moveToFirst();
      i = paramVarArgs.getInt(1);
      if (((SharedPreferences)localObject).getInt("app_status", 0) != i) {}
    }
    for (;;)
    {
      if (paramVarArgs != null) {
        paramVarArgs.close();
      }
      return Boolean.valueOf(true);
      localObject = ((SharedPreferences)localObject).edit();
      ((SharedPreferences.Editor)localObject).putInt("app_status", i);
      ((SharedPreferences.Editor)localObject).commit();
      continue;
      localObject = ((SharedPreferences)localObject).edit();
      ((SharedPreferences.Editor)localObject).putInt("app_status", 0);
      ((SharedPreferences.Editor)localObject).commit();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\DBReadAsyncTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */