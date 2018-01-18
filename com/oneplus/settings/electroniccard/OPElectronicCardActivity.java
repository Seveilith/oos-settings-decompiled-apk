package com.oneplus.settings.electroniccard;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import com.oneplus.settings.BaseActivity;
import com.oneplus.settings.utils.OPPrefUtil;
import com.oneplus.settings.utils.OPTimeUtil;
import com.oneplus.settings.utils.OPUtils;

public class OPElectronicCardActivity
  extends BaseActivity
{
  OPElectronicCardView card;
  ElecEnsurenceTask task;
  
  private void findViews()
  {
    this.card = ((OPElectronicCardView)findViewById(2131362303));
  }
  
  public static void launch(Context paramContext)
  {
    Intent localIntent = new Intent();
    localIntent.setClass(paramContext, OPElectronicCardActivity.class);
    paramContext.startActivity(localIntent);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968799);
    findViews();
    this.card.getDeviceModelTv().setText(OPUtils.getDeviceModel());
    this.card.getDeviceImeiTv().setText(OPUtils.getImei(this));
    long l = OPPrefUtil.getLong("key_warranty_time", -1L);
    paramBundle = OPUtils.getImei(this);
    Object localObject = OPPrefUtil.getString("key_imei", "");
    if ((l != -1L) && (paramBundle.equals(localObject)))
    {
      localObject = this.card.getWarrantyExpriedDateTv();
      if (l == 0L) {
        break label144;
      }
    }
    label144:
    for (paramBundle = OPTimeUtil.UnixTimeRead(l);; paramBundle = getString(2131689591))
    {
      ((TextView)localObject).setText(paramBundle);
      return;
      OPPrefUtil.putString("key_imei", OPUtils.getImei(this));
      this.task = new ElecEnsurenceTask(paramBundle);
      this.task.execute(new String[0]);
      return;
    }
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    if (this.task != null) {
      this.task.cancel(true);
    }
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (paramMenuItem.getItemId() == 16908332)
    {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(paramMenuItem);
  }
  
  class ElecEnsurenceTask
    extends AsyncTask<String, Void, OPEnsuranceResponse>
  {
    String imei;
    
    public ElecEnsurenceTask(String paramString)
    {
      this.imei = paramString;
    }
    
    protected OPEnsuranceResponse doInBackground(String... paramVarArgs)
    {
      return OPEnsuranceResponse.parse(OneplusServiceTools.elecEnsurance(this.imei), this.imei);
    }
    
    protected void onPostExecute(OPEnsuranceResponse paramOPEnsuranceResponse)
    {
      super.onPostExecute(paramOPEnsuranceResponse);
      if (paramOPEnsuranceResponse != null)
      {
        long l = paramOPEnsuranceResponse.getWarrantyStart();
        TextView localTextView = OPElectronicCardActivity.this.card.getWarrantyExpriedDateTv();
        if (l != 0L) {}
        for (paramOPEnsuranceResponse = OPTimeUtil.UnixTimeRead(l);; paramOPEnsuranceResponse = OPElectronicCardActivity.this.getString(2131689591))
        {
          localTextView.setText(paramOPEnsuranceResponse);
          OPPrefUtil.putLong("key_warranty_time", l);
          return;
        }
      }
      OPElectronicCardActivity.this.card.getWarrantyExpriedDateTv().setText(OPElectronicCardActivity.this.getString(2131690468));
    }
    
    protected void onPreExecute()
    {
      super.onPreExecute();
      OPElectronicCardActivity.this.card.getWarrantyExpriedDateTv().setText(OPElectronicCardActivity.this.getString(2131690467));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\electroniccard\OPElectronicCardActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */