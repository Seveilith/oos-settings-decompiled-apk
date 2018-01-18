package com.android.settings.datausage;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.ProgressBar;
import com.android.internal.util.Preconditions;
import com.android.settingslib.AppItem;
import com.android.settingslib.net.UidDetail;
import com.android.settingslib.net.UidDetailProvider;
import java.util.concurrent.ExecutorService;

public class AppDataUsagePreference
  extends Preference
{
  private final AppItem mItem;
  private final int mPercent;
  
  public AppDataUsagePreference(Context paramContext, AppItem paramAppItem, int paramInt, UidDetailProvider paramUidDetailProvider, ExecutorService paramExecutorService)
  {
    super(paramContext);
    this.mItem = paramAppItem;
    this.mPercent = paramInt;
    setLayoutResource(2130968678);
    setWidgetLayoutResource(2130969108);
    if ((paramAppItem.restricted) && (paramAppItem.total <= 0L)) {
      setSummary(2131692782);
    }
    for (;;)
    {
      UidDetailTask.bindView(paramUidDetailProvider, paramAppItem, this, paramExecutorService);
      return;
      setSummary(Formatter.formatFileSize(paramContext, paramAppItem.total));
    }
  }
  
  public AppItem getItem()
  {
    return this.mItem;
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    paramPreferenceViewHolder = (ProgressBar)paramPreferenceViewHolder.findViewById(16908301);
    if ((this.mItem.restricted) && (this.mItem.total <= 0L)) {
      paramPreferenceViewHolder.setVisibility(8);
    }
    for (;;)
    {
      paramPreferenceViewHolder.setProgress(this.mPercent);
      return;
      paramPreferenceViewHolder.setVisibility(0);
    }
  }
  
  private static class UidDetailTask
    extends AsyncTask<Void, Void, UidDetail>
  {
    private final AppItem mItem;
    private final UidDetailProvider mProvider;
    private final AppDataUsagePreference mTarget;
    
    private UidDetailTask(UidDetailProvider paramUidDetailProvider, AppItem paramAppItem, AppDataUsagePreference paramAppDataUsagePreference)
    {
      this.mProvider = ((UidDetailProvider)Preconditions.checkNotNull(paramUidDetailProvider));
      this.mItem = ((AppItem)Preconditions.checkNotNull(paramAppItem));
      this.mTarget = ((AppDataUsagePreference)Preconditions.checkNotNull(paramAppDataUsagePreference));
    }
    
    private static void bindView(UidDetail paramUidDetail, Preference paramPreference)
    {
      if (paramUidDetail != null)
      {
        paramPreference.setIcon(paramUidDetail.icon);
        paramPreference.setTitle(paramUidDetail.label);
        return;
      }
      paramPreference.setIcon(null);
      paramPreference.setTitle(null);
    }
    
    public static void bindView(UidDetailProvider paramUidDetailProvider, AppItem paramAppItem, AppDataUsagePreference paramAppDataUsagePreference, ExecutorService paramExecutorService)
    {
      UidDetail localUidDetail = paramUidDetailProvider.getUidDetail(paramAppItem.key, false);
      if (localUidDetail != null) {
        bindView(localUidDetail, paramAppDataUsagePreference);
      }
      for (;;)
      {
        return;
        try
        {
          if (!paramExecutorService.isShutdown())
          {
            new UidDetailTask(paramUidDetailProvider, paramAppItem, paramAppDataUsagePreference).executeOnExecutor(paramExecutorService, new Void[0]);
            return;
          }
        }
        catch (Exception paramUidDetailProvider)
        {
          Log.e("UidDetailTask", "execute UidDetailTask error.");
          paramUidDetailProvider.printStackTrace();
        }
      }
    }
    
    protected UidDetail doInBackground(Void... paramVarArgs)
    {
      return this.mProvider.getUidDetail(this.mItem.key, true);
    }
    
    protected void onPostExecute(UidDetail paramUidDetail)
    {
      bindView(paramUidDetail, this.mTarget);
    }
    
    protected void onPreExecute()
    {
      bindView(null, this.mTarget);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\AppDataUsagePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */