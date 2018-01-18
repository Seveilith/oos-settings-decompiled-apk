package com.android.settings.widget;

import android.content.Context;
import android.content.res.Resources;
import android.net.NetworkPolicy;
import android.net.NetworkStatsHistory;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.format.Formatter.BytesResult;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.util.MathUtils;
import android.view.MotionEvent;
import com.android.settings.datausage.OPDataUsageUtils;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ChartDataUsageView
  extends ChartView
{
  private static final long DELAY_MILLIS = 250L;
  private static final int MSG_UPDATE_AXIS = 100;
  private static final String TAG = "DataUsage";
  private ChartNetworkSeriesView mDetailSeries;
  private ChartGridView mGrid;
  private Handler mHandler;
  private NetworkStatsHistory mHistory;
  private ChartSweepView.OnSweepListener mHorizListener = new ChartSweepView.OnSweepListener()
  {
    public void onSweep(ChartSweepView paramAnonymousChartSweepView, boolean paramAnonymousBoolean)
    {
      ChartDataUsageView.-wrap5(ChartDataUsageView.this);
      if ((paramAnonymousBoolean) && (ChartDataUsageView.-get0(ChartDataUsageView.this) != null)) {
        ChartDataUsageView.-get0(ChartDataUsageView.this).onInspectRangeChanged();
      }
    }
    
    public void requestEdit(ChartSweepView paramAnonymousChartSweepView) {}
  };
  private long mInspectEnd;
  private long mInspectStart;
  private DataUsageChartListener mListener;
  private ChartNetworkSeriesView mSeries;
  private boolean mShowWifi = true;
  private int mSubId = 0;
  private ChartSweepView mSweepLeft;
  private ChartSweepView mSweepLimit;
  private ChartSweepView mSweepRight;
  private ChartSweepView mSweepWarning;
  private ChartSweepView.OnSweepListener mVertListener = new ChartSweepView.OnSweepListener()
  {
    public void onSweep(ChartSweepView paramAnonymousChartSweepView, boolean paramAnonymousBoolean)
    {
      if (paramAnonymousBoolean)
      {
        ChartDataUsageView.-wrap1(ChartDataUsageView.this, paramAnonymousChartSweepView);
        ChartDataUsageView.-wrap4(ChartDataUsageView.this);
        if ((paramAnonymousChartSweepView == ChartDataUsageView.-get2(ChartDataUsageView.this)) && (ChartDataUsageView.-get0(ChartDataUsageView.this) != null)) {
          ChartDataUsageView.-get0(ChartDataUsageView.this).onWarningChanged();
        }
        while ((paramAnonymousChartSweepView != ChartDataUsageView.-get1(ChartDataUsageView.this)) || (ChartDataUsageView.-get0(ChartDataUsageView.this) == null)) {
          return;
        }
        ChartDataUsageView.-get0(ChartDataUsageView.this).onLimitChanged();
        return;
      }
      ChartDataUsageView.-wrap2(ChartDataUsageView.this, paramAnonymousChartSweepView, false);
    }
    
    public void requestEdit(ChartSweepView paramAnonymousChartSweepView)
    {
      if ((paramAnonymousChartSweepView == ChartDataUsageView.-get2(ChartDataUsageView.this)) && (ChartDataUsageView.-get0(ChartDataUsageView.this) != null)) {
        ChartDataUsageView.-get0(ChartDataUsageView.this).requestWarningEdit();
      }
      while ((paramAnonymousChartSweepView != ChartDataUsageView.-get1(ChartDataUsageView.this)) || (ChartDataUsageView.-get0(ChartDataUsageView.this) == null)) {
        return;
      }
      ChartDataUsageView.-get0(ChartDataUsageView.this).requestLimitEdit();
    }
  };
  private long mVertMax;
  private long mWarnBytes = 0L;
  private int mWarnState = 0;
  
  public ChartDataUsageView(Context paramContext)
  {
    this(paramContext, null, 0);
  }
  
  public ChartDataUsageView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ChartDataUsageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(new TimeAxis(), new InvertedChartAxis(new DataAxis()));
    this.mHandler = new Handler()
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        paramAnonymousMessage = (ChartSweepView)paramAnonymousMessage.obj;
        ChartDataUsageView.-wrap6(ChartDataUsageView.this, paramAnonymousMessage);
        ChartDataUsageView.-wrap4(ChartDataUsageView.this);
        ChartDataUsageView.-wrap2(ChartDataUsageView.this, paramAnonymousMessage, true);
      }
    };
  }
  
  private void clearUpdateAxisDelayed(ChartSweepView paramChartSweepView)
  {
    this.mHandler.removeMessages(100, paramChartSweepView);
  }
  
  private long getHistoryEnd()
  {
    if (this.mHistory != null) {
      return this.mHistory.getEnd();
    }
    return Long.MIN_VALUE;
  }
  
  private long getHistoryStart()
  {
    if (this.mHistory != null) {
      return this.mHistory.getStart();
    }
    return Long.MAX_VALUE;
  }
  
  private static long roundUpToPowerOfTwo(long paramLong)
  {
    paramLong -= 1L;
    paramLong |= paramLong >>> 1;
    paramLong |= paramLong >>> 2;
    paramLong |= paramLong >>> 4;
    paramLong |= paramLong >>> 8;
    paramLong |= paramLong >>> 16;
    paramLong = (paramLong | paramLong >>> 32) + 1L;
    if (paramLong > 0L) {
      return paramLong;
    }
    return Long.MAX_VALUE;
  }
  
  private void sendUpdateAxisDelayed(ChartSweepView paramChartSweepView, boolean paramBoolean)
  {
    if ((!paramBoolean) && (this.mHandler.hasMessages(100, paramChartSweepView))) {
      return;
    }
    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(100, paramChartSweepView), 250L);
  }
  
  private static void setText(SpannableStringBuilder paramSpannableStringBuilder, Object paramObject, CharSequence paramCharSequence, String paramString)
  {
    int k = paramSpannableStringBuilder.getSpanStart(paramObject);
    int j = paramSpannableStringBuilder.getSpanEnd(paramObject);
    int i = k;
    if (k == -1)
    {
      i = TextUtils.indexOf(paramSpannableStringBuilder, paramString);
      j = i + paramString.length();
      paramSpannableStringBuilder.setSpan(paramObject, i, j, 18);
    }
    paramSpannableStringBuilder.replace(i, j, paramCharSequence);
  }
  
  private void updateEstimateVisible()
  {
    long l3 = this.mSeries.getMaxEstimate();
    long l1 = Long.MAX_VALUE;
    if (this.mSweepWarning.isEnabled())
    {
      l1 = this.mSweepWarning.getValue();
      long l2 = l1;
      if (l1 < 0L) {
        l2 = Long.MAX_VALUE;
      }
      if (l3 < 7L * l2 / 10L) {
        break label92;
      }
    }
    label92:
    for (boolean bool = true;; bool = false)
    {
      this.mSeries.setEstimateVisible(bool);
      return;
      if (!this.mSweepLimit.isEnabled()) {
        break;
      }
      l1 = this.mSweepLimit.getValue();
      break;
    }
  }
  
  private void updatePrimaryRange()
  {
    long l1 = this.mSweepLeft.getValue();
    long l2 = this.mSweepRight.getValue();
    if (this.mDetailSeries.getVisibility() == 0)
    {
      this.mDetailSeries.setPrimaryRange(l1, l2);
      this.mSeries.setPrimaryRange(0L, 0L);
      this.mSeries.setSecondary(true);
      return;
    }
    this.mSeries.setPrimaryRange(l1, l2);
    this.mSeries.setSecondary(false);
  }
  
  private void updateVertAxisBounds(ChartSweepView paramChartSweepView)
  {
    long l2 = this.mVertMax;
    long l1 = 0L;
    int i;
    if (paramChartSweepView != null)
    {
      i = paramChartSweepView.shouldAdjustAxis();
      if (i <= 0) {
        break label204;
      }
      l1 = 11L * l2 / 10L;
    }
    for (;;)
    {
      l2 = Math.max(this.mSweepWarning.getValue(), this.mSweepLimit.getValue());
      l1 = Math.max(Math.max(Math.max(Math.max(this.mSeries.getMaxVisible(), this.mDetailSeries.getMaxVisible()), l2) * 12L / 10L, 52428800L), l1);
      if (l1 != this.mVertMax)
      {
        this.mVertMax = l1;
        boolean bool = this.mVert.setBounds(0L, l1);
        this.mSweepWarning.setValidRange(0L, l1);
        this.mSweepLimit.setValidRange(0L, l1);
        if (bool)
        {
          this.mSeries.invalidatePath();
          this.mDetailSeries.invalidatePath();
        }
        this.mGrid.invalidate();
        if (paramChartSweepView != null) {
          paramChartSweepView.updateValueFromPosition();
        }
        if (this.mSweepLimit != paramChartSweepView) {
          layoutSweep(this.mSweepLimit);
        }
        if (this.mSweepWarning != paramChartSweepView) {
          layoutSweep(this.mSweepWarning);
        }
      }
      return;
      label204:
      if (i < 0) {
        l1 = 9L * l2 / 10L;
      } else {
        l1 = l2;
      }
    }
  }
  
  public void bindDetailNetworkStats(NetworkStatsHistory paramNetworkStatsHistory)
  {
    this.mDetailSeries.bindNetworkStats(paramNetworkStatsHistory);
    ChartNetworkSeriesView localChartNetworkSeriesView = this.mDetailSeries;
    if (paramNetworkStatsHistory != null) {}
    for (int i = 0;; i = 8)
    {
      localChartNetworkSeriesView.setVisibility(i);
      if (this.mHistory != null) {
        this.mDetailSeries.setEndTime(this.mHistory.getEnd());
      }
      updateVertAxisBounds(null);
      updateEstimateVisible();
      updatePrimaryRange();
      requestLayout();
      return;
    }
  }
  
  public void bindNetworkPolicy(NetworkPolicy paramNetworkPolicy)
  {
    if (paramNetworkPolicy == null)
    {
      this.mSweepLimit.setVisibility(4);
      this.mSweepLimit.setValue(-1L);
      this.mSweepWarning.setVisibility(4);
      this.mSweepWarning.setValue(-1L);
      return;
    }
    this.mWarnState = OPDataUsageUtils.getDataWarnState(this.mContext, this.mSubId);
    this.mWarnBytes = OPDataUsageUtils.getDataWarnBytes(this.mContext, this.mSubId);
    if (paramNetworkPolicy.limitBytes != -1L)
    {
      this.mSweepLimit.setVisibility(0);
      this.mSweepLimit.setEnabled(true);
      this.mSweepLimit.setValue(paramNetworkPolicy.limitBytes);
      if ((this.mWarnState == 1) && (this.mWarnBytes != -1L) && (!this.mShowWifi)) {
        break label196;
      }
      this.mSweepWarning.setVisibility(4);
      this.mSweepWarning.setValue(-1L);
    }
    for (;;)
    {
      updateVertAxisBounds(null);
      requestLayout();
      invalidate();
      return;
      this.mSweepLimit.setVisibility(4);
      this.mSweepLimit.setEnabled(false);
      this.mSweepLimit.setValue(-1L);
      break;
      label196:
      this.mSweepWarning.setVisibility(0);
      this.mSweepWarning.setValue(this.mWarnBytes);
    }
  }
  
  public void bindNetworkStats(NetworkStatsHistory paramNetworkStatsHistory)
  {
    this.mSeries.bindNetworkStats(paramNetworkStatsHistory);
    this.mHistory = paramNetworkStatsHistory;
    updateVertAxisBounds(null);
    updateEstimateVisible();
    updatePrimaryRange();
    requestLayout();
  }
  
  public long getInspectEnd()
  {
    return this.mInspectEnd;
  }
  
  public long getInspectLeft()
  {
    return this.mSweepLeft.getValue();
  }
  
  public long getInspectRight()
  {
    return this.mSweepRight.getValue();
  }
  
  public long getInspectStart()
  {
    return this.mInspectStart;
  }
  
  public long getLimitBytes()
  {
    return this.mSweepLimit.getLabelValue();
  }
  
  public long getWarningBytes()
  {
    return this.mWarnBytes;
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mGrid = ((ChartGridView)findViewById(2131362075));
    this.mSeries = ((ChartNetworkSeriesView)findViewById(2131362076));
    this.mDetailSeries = ((ChartNetworkSeriesView)findViewById(2131362077));
    this.mDetailSeries.setVisibility(8);
    this.mSweepLeft = ((ChartSweepView)findViewById(2131362078));
    this.mSweepLeft.setVisibility(8);
    this.mSweepRight = ((ChartSweepView)findViewById(2131362079));
    this.mSweepRight.setVisibility(8);
    this.mSweepLimit = ((ChartSweepView)findViewById(2131362081));
    this.mSweepWarning = ((ChartSweepView)findViewById(2131362080));
    this.mSweepLeft.setValidRangeDynamic(null, this.mSweepRight);
    this.mSweepRight.setValidRangeDynamic(this.mSweepLeft, null);
    this.mSweepWarning.setValidRangeDynamic(null, this.mSweepLimit);
    this.mSweepLimit.setValidRangeDynamic(this.mSweepWarning, null);
    this.mSweepLeft.setNeighbors(new ChartSweepView[] { this.mSweepRight });
    this.mSweepRight.setNeighbors(new ChartSweepView[] { this.mSweepLeft });
    this.mSweepLimit.setNeighbors(new ChartSweepView[] { this.mSweepWarning, this.mSweepLeft, this.mSweepRight });
    this.mSweepWarning.setNeighbors(new ChartSweepView[] { this.mSweepLimit, this.mSweepLeft, this.mSweepRight });
    this.mSweepLeft.addOnSweepListener(this.mHorizListener);
    this.mSweepRight.addOnSweepListener(this.mHorizListener);
    this.mSweepWarning.addOnSweepListener(this.mVertListener);
    this.mSweepLimit.addOnSweepListener(this.mVertListener);
    this.mSweepLeft.setClickable(false);
    this.mSweepLeft.setFocusable(false);
    this.mSweepRight.setClickable(false);
    this.mSweepRight.setFocusable(false);
    this.mSweepWarning.setDragInterval(5242880L);
    this.mSweepLimit.setDragInterval(5242880L);
    this.mGrid.init(this.mHoriz, this.mVert);
    this.mSeries.init(this.mHoriz, this.mVert);
    this.mDetailSeries.init(this.mHoriz, this.mVert);
    this.mSweepLeft.init(this.mHoriz);
    this.mSweepRight.init(this.mHoriz);
    this.mSweepWarning.init(this.mVert);
    this.mSweepLimit.init(this.mVert);
    setActivated(false);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (isActivated()) {
      return false;
    }
    switch (paramMotionEvent.getAction())
    {
    default: 
      return false;
    case 0: 
      return true;
    }
    setActivated(true);
    return true;
  }
  
  public void setDateSelectionSweepVisible(int paramInt)
  {
    this.mSweepLeft.setVisibility(paramInt);
    this.mSweepRight.setVisibility(paramInt);
  }
  
  public void setListener(DataUsageChartListener paramDataUsageChartListener)
  {
    this.mListener = paramDataUsageChartListener;
  }
  
  public void setShowWifi(boolean paramBoolean)
  {
    this.mShowWifi = paramBoolean;
  }
  
  public void setSubId(int paramInt)
  {
    this.mSubId = paramInt;
  }
  
  public void setVisibleRange(long paramLong1, long paramLong2, long paramLong3, long paramLong4)
  {
    boolean bool = this.mHoriz.setBounds(paramLong1, paramLong2);
    this.mGrid.setBounds(paramLong1, paramLong2);
    this.mSeries.setBounds(paramLong1, paramLong2);
    this.mDetailSeries.setBounds(paramLong1, paramLong2);
    this.mInspectStart = paramLong1;
    this.mInspectEnd = paramLong2;
    long l1 = getHistoryStart();
    long l2 = getHistoryEnd();
    label83:
    ChartSweepView localChartSweepView;
    if (l1 == Long.MAX_VALUE)
    {
      if (l2 != Long.MIN_VALUE) {
        break label281;
      }
      l1 = paramLong2;
      this.mSweepLeft.setValidRange(paramLong1, paramLong2);
      this.mSweepRight.setValidRange(paramLong1, paramLong2);
      l2 = (paramLong2 + paramLong1) / 2L;
      l2 = Math.max(paramLong1, l1 - 604800000L);
      localChartSweepView = this.mSweepLeft;
      if ((paramLong3 < paramLong1) || (paramLong3 > paramLong2)) {
        break label292;
      }
      label142:
      localChartSweepView.setValue(paramLong3);
      localChartSweepView = this.mSweepRight;
      if ((paramLong4 < paramLong1) || (paramLong4 > paramLong2)) {
        break label299;
      }
    }
    for (;;)
    {
      localChartSweepView.setValue(paramLong4);
      Log.d("DataUsage", "sweepMax" + new Date(l1).toString() + "sweepMin" + new Date(l2).toString());
      requestLayout();
      if (bool)
      {
        this.mSeries.invalidatePath();
        this.mDetailSeries.invalidatePath();
      }
      updateVertAxisBounds(null);
      updateEstimateVisible();
      updatePrimaryRange();
      return;
      Math.max(paramLong1, l1);
      break;
      label281:
      l1 = Math.min(paramLong2, l2);
      break label83;
      label292:
      paramLong3 = l2;
      break label142;
      label299:
      paramLong4 = l1;
    }
  }
  
  public static class DataAxis
    implements ChartAxis
  {
    private static final boolean LOG_SCALE = false;
    private static final Object sSpanSize = new Object();
    private static final Object sSpanUnit = new Object();
    private long mMax;
    private long mMin;
    private float mSize;
    
    public long buildLabel(Resources paramResources, SpannableStringBuilder paramSpannableStringBuilder, long paramLong)
    {
      paramResources = Formatter.formatBytes(paramResources, MathUtils.constrain(paramLong, 0L, 1099511627776L), 3);
      ChartDataUsageView.-wrap3(paramSpannableStringBuilder, sSpanSize, paramResources.value, "^1");
      ChartDataUsageView.-wrap3(paramSpannableStringBuilder, sSpanUnit, paramResources.units, "^2");
      return paramResources.roundedBytes;
    }
    
    public float convertToPoint(long paramLong)
    {
      return this.mSize * (float)(paramLong - this.mMin) / (float)(this.mMax - this.mMin);
    }
    
    public long convertToValue(float paramFloat)
    {
      return ((float)this.mMin + (float)(this.mMax - this.mMin) * paramFloat / this.mSize);
    }
    
    public float[] getTickPoints()
    {
      long l1 = this.mMax - this.mMin;
      long l2 = ChartDataUsageView.-wrap0(l1 / 16L);
      float[] arrayOfFloat = new float[(int)(l1 / l2)];
      l1 = this.mMin;
      int i = 0;
      while (i < arrayOfFloat.length)
      {
        arrayOfFloat[i] = convertToPoint(l1);
        l1 += l2;
        i += 1;
      }
      return arrayOfFloat;
    }
    
    public int hashCode()
    {
      return Objects.hash(new Object[] { Long.valueOf(this.mMin), Long.valueOf(this.mMax), Float.valueOf(this.mSize) });
    }
    
    public boolean setBounds(long paramLong1, long paramLong2)
    {
      if ((this.mMin != paramLong1) || (this.mMax != paramLong2))
      {
        this.mMin = paramLong1;
        this.mMax = paramLong2;
        return true;
      }
      return false;
    }
    
    public boolean setSize(float paramFloat)
    {
      if (this.mSize != paramFloat)
      {
        this.mSize = paramFloat;
        return true;
      }
      return false;
    }
    
    public int shouldAdjustAxis(long paramLong)
    {
      float f = convertToPoint(paramLong);
      if (f < this.mSize * 0.1D) {
        return -1;
      }
      if (f > this.mSize * 0.85D) {
        return 1;
      }
      return 0;
    }
  }
  
  public static abstract interface DataUsageChartListener
  {
    public abstract void onInspectRangeChanged();
    
    public abstract void onLimitChanged();
    
    public abstract void onWarningChanged();
    
    public abstract void requestLimitEdit();
    
    public abstract void requestWarningEdit();
  }
  
  public static class TimeAxis
    implements ChartAxis
  {
    private static final int FIRST_DAY_OF_WEEK = Calendar.getInstance().getFirstDayOfWeek() - 1;
    private long mMax;
    private long mMin;
    private float mSize;
    
    public TimeAxis()
    {
      long l = System.currentTimeMillis();
      setBounds(l - 2592000000L, l);
    }
    
    public long buildLabel(Resources paramResources, SpannableStringBuilder paramSpannableStringBuilder, long paramLong)
    {
      paramSpannableStringBuilder.replace(0, paramSpannableStringBuilder.length(), Long.toString(paramLong));
      return paramLong;
    }
    
    public float convertToPoint(long paramLong)
    {
      return this.mSize * (float)(paramLong - this.mMin) / (float)(this.mMax - this.mMin);
    }
    
    public long convertToValue(float paramFloat)
    {
      return ((float)this.mMin + (float)(this.mMax - this.mMin) * paramFloat / this.mSize);
    }
    
    public float[] getTickPoints()
    {
      float[] arrayOfFloat = new float[32];
      int i = 0;
      Time localTime = new Time();
      localTime.set(this.mMax);
      localTime.monthDay -= localTime.weekDay - FIRST_DAY_OF_WEEK;
      localTime.second = 0;
      localTime.minute = 0;
      localTime.hour = 0;
      localTime.normalize(true);
      long l = localTime.toMillis(true);
      while (l > this.mMin)
      {
        int j = i;
        if (l <= this.mMax)
        {
          arrayOfFloat[i] = convertToPoint(l);
          j = i + 1;
        }
        localTime.monthDay -= 7;
        localTime.normalize(true);
        l = localTime.toMillis(true);
        i = j;
      }
      return Arrays.copyOf(arrayOfFloat, i);
    }
    
    public int hashCode()
    {
      return Objects.hash(new Object[] { Long.valueOf(this.mMin), Long.valueOf(this.mMax), Float.valueOf(this.mSize) });
    }
    
    public boolean setBounds(long paramLong1, long paramLong2)
    {
      if ((this.mMin != paramLong1) || (this.mMax != paramLong2))
      {
        this.mMin = paramLong1;
        this.mMax = paramLong2;
        return true;
      }
      return false;
    }
    
    public boolean setSize(float paramFloat)
    {
      if (this.mSize != paramFloat)
      {
        this.mSize = paramFloat;
        return true;
      }
      return false;
    }
    
    public int shouldAdjustAxis(long paramLong)
    {
      return 0;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\widget\ChartDataUsageView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */