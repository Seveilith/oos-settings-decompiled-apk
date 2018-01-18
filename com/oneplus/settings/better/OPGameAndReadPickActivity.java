package com.oneplus.settings.better;

import android.app.ActionBar;
import android.app.AppOpsManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.oneplus.settings.BaseActivity;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.apploader.OPApplicationLoader;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPGameAndReadPickActivity
  extends BaseActivity
  implements AdapterView.OnItemClickListener
{
  private List<OPAppModel> mAppList = new ArrayList();
  private ListView mAppListView;
  private AppOpsManager mAppOpsManager;
  private int mAppType;
  private Handler mHandler = new Handler(Looper.getMainLooper())
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      super.handleMessage(paramAnonymousMessage);
      if ((OPGameAndReadPickActivity.-get3(OPGameAndReadPickActivity.this) != null) && (OPGameAndReadPickActivity.-get2(OPGameAndReadPickActivity.this) != null))
      {
        OPGameAndReadPickActivity.-get0(OPGameAndReadPickActivity.this).clear();
        OPGameAndReadPickActivity.-get0(OPGameAndReadPickActivity.this).addAll(OPGameAndReadPickActivity.-get2(OPGameAndReadPickActivity.this).getAppListByType(paramAnonymousMessage.what));
        OPGameAndReadPickActivity.-get3(OPGameAndReadPickActivity.this).setData(OPGameAndReadPickActivity.-get0(OPGameAndReadPickActivity.this));
        paramAnonymousMessage = OPGameAndReadPickActivity.this.findViewById(2131362268);
        if (OPGameAndReadPickActivity.-get0(OPGameAndReadPickActivity.this).isEmpty())
        {
          paramAnonymousMessage.setVisibility(0);
          OPGameAndReadPickActivity.-get1(OPGameAndReadPickActivity.this).setEmptyView(paramAnonymousMessage);
        }
      }
    }
  };
  private View mLoadingContainer;
  private OPApplicationLoader mOPApplicationLoader;
  private OPGameAndReadPickAdapter mOPGameAndReadPickAdapter;
  private PackageManager mPackageManager;
  
  private void initView()
  {
    this.mAppListView = ((ListView)findViewById(2131362267));
    OPUtils.setListDivider(SettingsBaseApplication.mApplication, this.mAppListView, 2130838238, 2130838236, 2131755334);
    this.mOPGameAndReadPickAdapter = new OPGameAndReadPickAdapter(this, this.mAppList);
    this.mAppListView.setAdapter(this.mOPGameAndReadPickAdapter);
    this.mAppListView.setOnItemClickListener(this);
    this.mLoadingContainer = findViewById(2131362189);
    this.mOPApplicationLoader.setmLoadingContainer(this.mLoadingContainer);
    this.mOPApplicationLoader.loadSelectedGameOrReadAppMap(this.mAppType);
    this.mOPApplicationLoader.initData(2, this.mHandler);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968813);
    this.mAppType = getIntent().getIntExtra("op_load_app_tyep", 0);
    paramBundle = getActionBar();
    paramBundle.setDisplayHomeAsUpEnabled(true);
    paramBundle.setHomeButtonEnabled(true);
    if (this.mAppType == 68) {
      paramBundle.setTitle(getString(2131690399));
    }
    for (;;)
    {
      this.mAppOpsManager = ((AppOpsManager)getSystemService("appops"));
      this.mPackageManager = getPackageManager();
      this.mOPApplicationLoader = new OPApplicationLoader(this, this.mAppOpsManager, this.mPackageManager);
      this.mOPApplicationLoader.setAppType(this.mAppType);
      initView();
      return;
      if (this.mAppType == 67) {
        paramBundle.setTitle(getString(2131690401));
      } else if (this.mAppType == 63) {
        paramBundle.setTitle(getString(2131690502));
      }
    }
  }
  
  public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    paramView = (OPAppModel)this.mAppListView.getItemAtPosition(paramInt);
    boolean bool;
    int i;
    int j;
    if (this.mOPGameAndReadPickAdapter.getSelected(paramInt))
    {
      bool = false;
      this.mOPGameAndReadPickAdapter.setSelected(paramInt, bool);
      paramAdapterView = this.mAppOpsManager;
      i = this.mAppType;
      j = paramView.getUid();
      paramView = paramView.getPkgName();
      if (!bool) {
        break label82;
      }
    }
    label82:
    for (paramInt = 0;; paramInt = 1)
    {
      paramAdapterView.setMode(i, j, paramView, paramInt);
      return;
      bool = true;
      break;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\better\OPGameAndReadPickActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */