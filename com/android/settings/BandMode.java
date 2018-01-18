package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;

public class BandMode
  extends Activity
{
  private static final String[] BAND_NAMES = { "Automatic", "Europe", "United States", "Japan", "Australia", "Australia 2", "Cellular 800", "PCS", "Class 3 (JTACS)", "Class 4 (Korea-PCS)", "Class 5", "Class 6 (IMT2000)", "Class 7 (700Mhz-Upper)", "Class 8 (1800Mhz-Upper)", "Class 9 (900Mhz)", "Class 10 (800Mhz-Secondary)", "Class 11 (Europe PAMR 400Mhz)", "Class 15 (US-AWS)", "Class 16 (US-2500Mhz)" };
  private static final boolean DBG = false;
  private static final int EVENT_BAND_SCAN_COMPLETED = 100;
  private static final int EVENT_BAND_SELECTION_DONE = 200;
  private static final String LOG_TAG = "phone";
  private ListView mBandList;
  private ArrayAdapter mBandListAdapter;
  private AdapterView.OnItemClickListener mBandSelectionHandler = new AdapterView.OnItemClickListener()
  {
    public void onItemClick(AdapterView paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      BandMode.this.getWindow().setFeatureInt(5, -1);
      BandMode.-set0(BandMode.this, (BandMode.BandListItem)paramAnonymousAdapterView.getAdapter().getItem(paramAnonymousInt));
      paramAnonymousAdapterView = BandMode.-get1(BandMode.this).obtainMessage(200);
      BandMode.-get2(BandMode.this).setBandMode(BandMode.-get3(BandMode.this).getBand(), paramAnonymousAdapterView);
    }
  };
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      }
      do
      {
        return;
        paramAnonymousMessage = (AsyncResult)paramAnonymousMessage.obj;
        BandMode.-wrap0(BandMode.this, paramAnonymousMessage);
        return;
        paramAnonymousMessage = (AsyncResult)paramAnonymousMessage.obj;
        BandMode.this.getWindow().setFeatureInt(5, -2);
      } while (BandMode.this.isFinishing());
      BandMode.-wrap1(BandMode.this, paramAnonymousMessage.exception);
    }
  };
  private Phone mPhone = null;
  private DialogInterface mProgressPanel;
  private BandListItem mTargetBand = null;
  
  private void bandListLoaded(AsyncResult paramAsyncResult)
  {
    if (this.mProgressPanel != null) {
      this.mProgressPanel.dismiss();
    }
    clearList();
    int j = 0;
    int i = j;
    if (paramAsyncResult.result != null)
    {
      paramAsyncResult = (int[])paramAsyncResult.result;
      if (paramAsyncResult.length == 0)
      {
        Log.wtf("phone", "No Supported Band Modes");
        return;
      }
      int k = paramAsyncResult[0];
      i = j;
      if (k > 0)
      {
        i = 1;
        while (i <= k)
        {
          BandListItem localBandListItem = new BandListItem(paramAsyncResult[i]);
          this.mBandListAdapter.add(localBandListItem);
          i += 1;
        }
        i = 1;
      }
    }
    if (i == 0)
    {
      i = 0;
      while (i < 19)
      {
        paramAsyncResult = new BandListItem(i);
        this.mBandListAdapter.add(paramAsyncResult);
        i += 1;
      }
    }
    this.mBandList.requestFocus();
  }
  
  private void clearList()
  {
    while (this.mBandListAdapter.getCount() > 0) {
      this.mBandListAdapter.remove(this.mBandListAdapter.getItem(0));
    }
  }
  
  private void displayBandSelectionResult(Throwable paramThrowable)
  {
    String str = getString(2131690957) + " [" + this.mTargetBand.toString() + "] ";
    if (paramThrowable != null) {}
    for (paramThrowable = str + getString(2131690958);; paramThrowable = str + getString(2131690959))
    {
      this.mProgressPanel = new AlertDialog.Builder(this).setMessage(paramThrowable).setPositiveButton(17039370, null).show();
      return;
    }
  }
  
  private void loadBandList()
  {
    Object localObject = getString(2131690956);
    this.mProgressPanel = new AlertDialog.Builder(this).setMessage((CharSequence)localObject).show();
    localObject = this.mHandler.obtainMessage(100);
    this.mPhone.queryAvailableBandMode((Message)localObject);
  }
  
  private void log(String paramString)
  {
    Log.d("phone", "[BandsList] " + paramString);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    requestWindowFeature(5);
    setContentView(2130968623);
    setTitle(getString(2131690955));
    getWindow().setLayout(-1, -2);
    this.mPhone = PhoneFactory.getDefaultPhone();
    this.mBandList = ((ListView)findViewById(2131361973));
    this.mBandListAdapter = new ArrayAdapter(this, 17367043);
    this.mBandList.setAdapter(this.mBandListAdapter);
    this.mBandList.setOnItemClickListener(this.mBandSelectionHandler);
    loadBandList();
  }
  
  private static class BandListItem
  {
    private int mBandMode = 0;
    
    public BandListItem(int paramInt)
    {
      this.mBandMode = paramInt;
    }
    
    public int getBand()
    {
      return this.mBandMode;
    }
    
    public String toString()
    {
      if (this.mBandMode >= BandMode.-get0().length) {
        return "Band mode " + this.mBandMode;
      }
      return BandMode.-get0()[this.mBandMode];
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\BandMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */