package com.android.settings.fuelgauge;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.BatteryStats;
import android.os.BatteryStats.HistoryItem;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import com.android.settings.Utils;
import com.android.settingslib.BatteryInfo;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import libcore.icu.LocaleData;

public class BatteryHistoryChart
  extends View
{
  static final int CHART_DATA_BIN_MASK = -65536;
  static final int CHART_DATA_BIN_SHIFT = 16;
  static final int CHART_DATA_X_MASK = 65535;
  static final boolean DEBUG = false;
  static final int MONOSPACE = 3;
  static final int NUM_PHONE_SIGNALS = 7;
  static final int SANS = 1;
  static final int SERIF = 2;
  static final String TAG = "BatteryHistoryChart";
  final Path mBatCriticalPath = new Path();
  final Path mBatGoodPath = new Path();
  int mBatHigh;
  final Path mBatLevelPath = new Path();
  int mBatLow;
  final Path mBatWarnPath = new Path();
  final Paint mBatteryBackgroundPaint = new Paint(1);
  Intent mBatteryBroadcast;
  int mBatteryCriticalLevel = this.mContext.getResources().getInteger(17694806);
  final Paint mBatteryCriticalPaint = new Paint(1);
  final Paint mBatteryGoodPaint = new Paint(1);
  int mBatteryWarnLevel = this.mContext.getResources().getInteger(17694808);
  final Paint mBatteryWarnPaint = new Paint(1);
  Bitmap mBitmap;
  String mCameraOnLabel;
  int mCameraOnOffset;
  final Paint mCameraOnPaint = new Paint();
  final Path mCameraOnPath = new Path();
  Canvas mCanvas;
  String mChargeDurationString;
  int mChargeDurationStringWidth;
  int mChargeLabelStringWidth;
  String mChargingLabel;
  int mChargingOffset;
  final Paint mChargingPaint = new Paint();
  final Path mChargingPath = new Path();
  int mChartMinHeight;
  String mCpuRunningLabel;
  int mCpuRunningOffset;
  final Paint mCpuRunningPaint = new Paint();
  final Path mCpuRunningPath = new Path();
  final ArrayList<DateLabel> mDateLabels = new ArrayList();
  final Paint mDateLinePaint = new Paint();
  final Path mDateLinePath = new Path();
  final Paint mDebugRectPaint = new Paint();
  String mDrainString;
  int mDrainStringWidth;
  String mDurationString;
  int mDurationStringWidth;
  long mEndDataWallTime;
  long mEndWallTime;
  String mFlashlightOnLabel;
  int mFlashlightOnOffset;
  final Paint mFlashlightOnPaint = new Paint();
  final Path mFlashlightOnPath = new Path();
  String mGpsOnLabel;
  int mGpsOnOffset;
  final Paint mGpsOnPaint = new Paint();
  final Path mGpsOnPath = new Path();
  boolean mHaveCamera;
  boolean mHaveFlashlight;
  boolean mHaveGps;
  boolean mHavePhoneSignal;
  boolean mHaveWifi;
  int mHeaderHeight;
  int mHeaderTextAscent;
  int mHeaderTextDescent;
  final TextPaint mHeaderTextPaint = new TextPaint(1);
  long mHistDataEnd;
  long mHistEnd;
  long mHistStart;
  BatteryInfo mInfo;
  boolean mLargeMode;
  int mLastHeight = -1;
  int mLastWidth = -1;
  int mLevelBottom;
  int mLevelLeft;
  int mLevelOffset;
  int mLevelRight;
  int mLevelTop;
  int mLineWidth;
  String mMaxPercentLabelString;
  int mMaxPercentLabelStringWidth;
  String mMinPercentLabelString;
  int mMinPercentLabelStringWidth;
  int mNumHist;
  final ChartData mPhoneSignalChart = new ChartData();
  String mPhoneSignalLabel;
  int mPhoneSignalOffset;
  String mScreenOnLabel;
  int mScreenOnOffset;
  final Paint mScreenOnPaint = new Paint();
  final Path mScreenOnPath = new Path();
  long mStartWallTime;
  BatteryStats mStats;
  long mStatsPeriod;
  int mTextAscent;
  int mTextDescent;
  final TextPaint mTextPaint = new TextPaint(1);
  int mThinLineWidth = (int)TypedValue.applyDimension(1, 2.0F, getResources().getDisplayMetrics());
  final ArrayList<TimeLabel> mTimeLabels = new ArrayList();
  final Paint mTimeRemainPaint = new Paint(1);
  final Path mTimeRemainPath = new Path();
  String mWifiRunningLabel;
  int mWifiRunningOffset;
  final Paint mWifiRunningPaint = new Paint();
  final Path mWifiRunningPath = new Path();
  
  public BatteryHistoryChart(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mBatteryBackgroundPaint.setColor(-16738680);
    this.mBatteryBackgroundPaint.setStyle(Paint.Style.FILL);
    this.mBatteryGoodPaint.setARGB(128, 0, 128, 0);
    this.mBatteryGoodPaint.setStyle(Paint.Style.STROKE);
    this.mBatteryWarnPaint.setARGB(128, 128, 128, 0);
    this.mBatteryWarnPaint.setStyle(Paint.Style.STROKE);
    this.mBatteryCriticalPaint.setARGB(192, 128, 0, 0);
    this.mBatteryCriticalPaint.setStyle(Paint.Style.STROKE);
    this.mTimeRemainPaint.setColor(-3221573);
    this.mTimeRemainPaint.setStyle(Paint.Style.FILL);
    this.mChargingPaint.setStyle(Paint.Style.STROKE);
    this.mScreenOnPaint.setStyle(Paint.Style.STROKE);
    this.mGpsOnPaint.setStyle(Paint.Style.STROKE);
    this.mCameraOnPaint.setStyle(Paint.Style.STROKE);
    this.mFlashlightOnPaint.setStyle(Paint.Style.STROKE);
    this.mWifiRunningPaint.setStyle(Paint.Style.STROKE);
    this.mCpuRunningPaint.setStyle(Paint.Style.STROKE);
    this.mPhoneSignalChart.setColors(Utils.BADNESS_COLORS);
    this.mDebugRectPaint.setARGB(255, 255, 0, 0);
    this.mDebugRectPaint.setStyle(Paint.Style.STROKE);
    this.mScreenOnPaint.setColor(-16738680);
    this.mGpsOnPaint.setColor(-16738680);
    this.mCameraOnPaint.setColor(-16738680);
    this.mFlashlightOnPaint.setColor(-16738680);
    this.mWifiRunningPaint.setColor(-16738680);
    this.mCpuRunningPaint.setColor(-16738680);
    this.mChargingPaint.setColor(-16738680);
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, com.android.settings.R.styleable.BatteryHistoryChart, 0, 0);
    TextAttrs localTextAttrs1 = new TextAttrs();
    TextAttrs localTextAttrs2 = new TextAttrs();
    localTextAttrs1.retrieve(paramContext, paramAttributeSet, 0);
    localTextAttrs2.retrieve(paramContext, paramAttributeSet, 9);
    int i = 0;
    float f3 = 0.0F;
    float f2 = 0.0F;
    float f1 = 0.0F;
    int m = paramAttributeSet.getIndexCount();
    int j = 0;
    if (j < m)
    {
      int n = paramAttributeSet.getIndex(j);
      float f4 = f3;
      float f5 = f2;
      float f6 = f1;
      k = i;
      switch (n)
      {
      default: 
        k = i;
        f6 = f1;
        f5 = f2;
        f4 = f3;
      }
      for (;;)
      {
        j += 1;
        f3 = f4;
        f2 = f5;
        f1 = f6;
        i = k;
        break;
        k = paramAttributeSet.getInt(n, 0);
        f4 = f3;
        f5 = f2;
        f6 = f1;
        continue;
        f4 = paramAttributeSet.getFloat(n, 0.0F);
        f5 = f2;
        f6 = f1;
        k = i;
        continue;
        f5 = paramAttributeSet.getFloat(n, 0.0F);
        f4 = f3;
        f6 = f1;
        k = i;
        continue;
        f6 = paramAttributeSet.getFloat(n, 0.0F);
        f4 = f3;
        f5 = f2;
        k = i;
        continue;
        localTextAttrs1.textColor = paramAttributeSet.getColorStateList(n);
        localTextAttrs2.textColor = paramAttributeSet.getColorStateList(n);
        f4 = f3;
        f5 = f2;
        f6 = f1;
        k = i;
        continue;
        localTextAttrs1.textSize = paramAttributeSet.getDimensionPixelSize(n, localTextAttrs1.textSize);
        localTextAttrs2.textSize = paramAttributeSet.getDimensionPixelSize(n, localTextAttrs2.textSize);
        f4 = f3;
        f5 = f2;
        f6 = f1;
        k = i;
        continue;
        localTextAttrs1.typefaceIndex = paramAttributeSet.getInt(n, localTextAttrs1.typefaceIndex);
        localTextAttrs2.typefaceIndex = paramAttributeSet.getInt(n, localTextAttrs2.typefaceIndex);
        f4 = f3;
        f5 = f2;
        f6 = f1;
        k = i;
        continue;
        localTextAttrs1.styleIndex = paramAttributeSet.getInt(n, localTextAttrs1.styleIndex);
        localTextAttrs2.styleIndex = paramAttributeSet.getInt(n, localTextAttrs2.styleIndex);
        f4 = f3;
        f5 = f2;
        f6 = f1;
        k = i;
        continue;
        this.mBatteryBackgroundPaint.setColor(paramAttributeSet.getInt(n, 0));
        this.mScreenOnPaint.setColor(paramAttributeSet.getInt(n, 0));
        this.mGpsOnPaint.setColor(paramAttributeSet.getInt(n, 0));
        this.mCameraOnPaint.setColor(paramAttributeSet.getInt(n, 0));
        this.mFlashlightOnPaint.setColor(paramAttributeSet.getInt(n, 0));
        this.mWifiRunningPaint.setColor(paramAttributeSet.getInt(n, 0));
        this.mCpuRunningPaint.setColor(paramAttributeSet.getInt(n, 0));
        this.mChargingPaint.setColor(paramAttributeSet.getInt(n, 0));
        f4 = f3;
        f5 = f2;
        f6 = f1;
        k = i;
        continue;
        this.mTimeRemainPaint.setColor(paramAttributeSet.getInt(n, 0));
        f4 = f3;
        f5 = f2;
        f6 = f1;
        k = i;
        continue;
        this.mChartMinHeight = paramAttributeSet.getDimensionPixelSize(n, 0);
        f4 = f3;
        f5 = f2;
        f6 = f1;
        k = i;
      }
    }
    paramAttributeSet.recycle();
    localTextAttrs1.apply(paramContext, this.mTextPaint);
    localTextAttrs2.apply(paramContext, this.mHeaderTextPaint);
    this.mDateLinePaint.set(this.mTextPaint);
    this.mDateLinePaint.setStyle(Paint.Style.STROKE);
    int k = this.mThinLineWidth / 2;
    j = k;
    if (k < 1) {
      j = 1;
    }
    this.mDateLinePaint.setStrokeWidth(j);
    this.mDateLinePaint.setPathEffect(new DashPathEffect(new float[] { this.mThinLineWidth * 2, this.mThinLineWidth * 2 }, 0.0F));
    if (i != 0)
    {
      this.mTextPaint.setShadowLayer(f1, f3, f2, i);
      this.mHeaderTextPaint.setShadowLayer(f1, f3, f2, i);
    }
  }
  
  private boolean is24Hour()
  {
    return DateFormat.is24HourFormat(getContext());
  }
  
  private boolean isDayFirst()
  {
    String str = LocaleData.get(getResources().getConfiguration().locale).getDateFormat(3);
    return str.indexOf('M') > str.indexOf('d');
  }
  
  void addDateLabel(Calendar paramCalendar, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    long l1 = this.mStartWallTime;
    long l2 = this.mEndWallTime;
    this.mDateLabels.add(new DateLabel(this.mTextPaint, (int)((paramCalendar.getTimeInMillis() - l1) * (paramInt2 - paramInt1) / (l2 - l1)) + paramInt1, paramCalendar, paramBoolean));
  }
  
  void addTimeLabel(Calendar paramCalendar, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    long l1 = this.mStartWallTime;
    long l2 = this.mEndWallTime;
    this.mTimeLabels.add(new TimeLabel(this.mTextPaint, (int)((paramCalendar.getTimeInMillis() - l1) * (paramInt2 - paramInt1) / (l2 - l1)) + paramInt1, paramCalendar, paramBoolean));
  }
  
  void buildBitmap(int paramInt1, int paramInt2)
  {
    if ((this.mBitmap != null) && (paramInt1 == this.mBitmap.getWidth()) && (paramInt2 == this.mBitmap.getHeight())) {
      return;
    }
    this.mBitmap = Bitmap.createBitmap(getResources().getDisplayMetrics(), paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
    this.mCanvas = new Canvas(this.mBitmap);
    drawChart(this.mCanvas, paramInt1, paramInt2);
  }
  
  void drawChart(Canvas paramCanvas, int paramInt1, int paramInt2)
  {
    boolean bool = isLayoutRtl();
    int j;
    int k;
    label22:
    Object localObject;
    label32:
    Paint.Align localAlign;
    label42:
    int i2;
    int i3;
    label139:
    TimeLabel localTimeLabel;
    if (bool)
    {
      j = paramInt1;
      if (!bool) {
        break label276;
      }
      k = 0;
      if (!bool) {
        break label282;
      }
      localObject = Paint.Align.RIGHT;
      if (!bool) {
        break label290;
      }
      localAlign = Paint.Align.LEFT;
      paramCanvas.drawPath(this.mBatLevelPath, this.mBatteryBackgroundPaint);
      if (!this.mTimeRemainPath.isEmpty()) {
        paramCanvas.drawPath(this.mTimeRemainPath, this.mTimeRemainPaint);
      }
      if (this.mTimeLabels.size() <= 1) {
        break label538;
      }
      i2 = this.mLevelBottom - this.mTextAscent + this.mThinLineWidth * 4;
      i3 = this.mLevelBottom + this.mThinLineWidth + this.mThinLineWidth / 2;
      this.mTextPaint.setTextAlign(Paint.Align.LEFT);
      n = 0;
      m = 0;
      if (m >= this.mTimeLabels.size()) {
        break label620;
      }
      localTimeLabel = (TimeLabel)this.mTimeLabels.get(m);
      if (m != 0) {
        break label298;
      }
      n = localTimeLabel.x - localTimeLabel.width / 2;
      i = n;
      if (n < 0) {
        i = 0;
      }
      paramCanvas.drawText(localTimeLabel.label, i, i2, this.mTextPaint);
      paramCanvas.drawLine(localTimeLabel.x, i3, localTimeLabel.x, this.mThinLineWidth + i3, this.mTextPaint);
      i += localTimeLabel.width;
    }
    for (;;)
    {
      m += 1;
      n = i;
      break label139;
      j = 0;
      break;
      label276:
      k = paramInt1;
      break label22;
      label282:
      localObject = Paint.Align.LEFT;
      break label32;
      label290:
      localAlign = Paint.Align.RIGHT;
      break label42;
      label298:
      if (m < this.mTimeLabels.size() - 1)
      {
        i1 = localTimeLabel.x - localTimeLabel.width / 2;
        i = n;
        if (i1 >= this.mTextAscent + n)
        {
          i = n;
          if (i1 <= paramInt1 - ((TimeLabel)this.mTimeLabels.get(m + 1)).width - this.mTextAscent)
          {
            paramCanvas.drawText(localTimeLabel.label, i1, i2, this.mTextPaint);
            paramCanvas.drawLine(localTimeLabel.x, i3, localTimeLabel.x, this.mThinLineWidth + i3, this.mTextPaint);
            i = i1 + localTimeLabel.width;
          }
        }
      }
      else
      {
        i1 = localTimeLabel.x - localTimeLabel.width / 2;
        i = i1;
        if (localTimeLabel.width + i1 >= paramInt1) {
          i = paramInt1 - 1 - localTimeLabel.width;
        }
        paramCanvas.drawText(localTimeLabel.label, i, i2, this.mTextPaint);
        paramCanvas.drawLine(localTimeLabel.x, i3, localTimeLabel.x, this.mThinLineWidth + i3, this.mTextPaint);
        i = n;
      }
    }
    label538:
    if (this.mDurationString != null)
    {
      i = this.mLevelBottom;
      m = this.mTextAscent;
      n = this.mThinLineWidth;
      this.mTextPaint.setTextAlign(Paint.Align.LEFT);
      paramCanvas.drawText(this.mDurationString, this.mLevelLeft + (this.mLevelRight - this.mLevelLeft) / 2 - this.mDurationStringWidth / 2, i - m + n * 4, this.mTextPaint);
    }
    label620:
    int n = -this.mHeaderTextAscent + (this.mHeaderTextDescent - this.mHeaderTextAscent) / 3;
    this.mHeaderTextPaint.setTextAlign((Paint.Align)localObject);
    paramCanvas.drawText(this.mInfo.mChargeLabelString, j, n, this.mHeaderTextPaint);
    int m = this.mChargeDurationStringWidth / 2;
    int i = m;
    if (bool) {
      i = -m;
    }
    int i1 = (paramInt1 - this.mChargeDurationStringWidth - this.mDrainStringWidth) / 2;
    if (bool) {}
    for (m = this.mDrainStringWidth;; m = this.mChargeLabelStringWidth)
    {
      paramCanvas.drawText(this.mChargeDurationString, i1 + m - i, n, this.mHeaderTextPaint);
      this.mHeaderTextPaint.setTextAlign(localAlign);
      paramCanvas.drawText(this.mDrainString, k, n, this.mHeaderTextPaint);
      if (!this.mBatGoodPath.isEmpty()) {
        paramCanvas.drawPath(this.mBatGoodPath, this.mBatteryGoodPaint);
      }
      if (!this.mBatWarnPath.isEmpty()) {
        paramCanvas.drawPath(this.mBatWarnPath, this.mBatteryWarnPaint);
      }
      if (!this.mBatCriticalPath.isEmpty()) {
        paramCanvas.drawPath(this.mBatCriticalPath, this.mBatteryCriticalPaint);
      }
      if (this.mHavePhoneSignal)
      {
        i = this.mPhoneSignalOffset;
        k = this.mLineWidth / 2;
        this.mPhoneSignalChart.draw(paramCanvas, paramInt2 - i - k, this.mLineWidth);
      }
      if (!this.mScreenOnPath.isEmpty()) {
        paramCanvas.drawPath(this.mScreenOnPath, this.mScreenOnPaint);
      }
      if (!this.mChargingPath.isEmpty()) {
        paramCanvas.drawPath(this.mChargingPath, this.mChargingPaint);
      }
      if ((this.mHaveGps) && (!this.mGpsOnPath.isEmpty())) {
        paramCanvas.drawPath(this.mGpsOnPath, this.mGpsOnPaint);
      }
      if ((this.mHaveFlashlight) && (!this.mFlashlightOnPath.isEmpty())) {
        paramCanvas.drawPath(this.mFlashlightOnPath, this.mFlashlightOnPaint);
      }
      if ((this.mHaveCamera) && (!this.mCameraOnPath.isEmpty())) {
        paramCanvas.drawPath(this.mCameraOnPath, this.mCameraOnPaint);
      }
      if ((this.mHaveWifi) && (!this.mWifiRunningPath.isEmpty())) {
        paramCanvas.drawPath(this.mWifiRunningPath, this.mWifiRunningPaint);
      }
      if (!this.mCpuRunningPath.isEmpty()) {
        paramCanvas.drawPath(this.mCpuRunningPath, this.mCpuRunningPaint);
      }
      if (this.mLargeMode)
      {
        localAlign = this.mTextPaint.getTextAlign();
        this.mTextPaint.setTextAlign((Paint.Align)localObject);
        if (this.mHavePhoneSignal) {
          paramCanvas.drawText(this.mPhoneSignalLabel, j, paramInt2 - this.mPhoneSignalOffset - this.mTextDescent, this.mTextPaint);
        }
        if (this.mHaveGps) {
          paramCanvas.drawText(this.mGpsOnLabel, j, paramInt2 - this.mGpsOnOffset - this.mTextDescent, this.mTextPaint);
        }
        if (this.mHaveFlashlight) {
          paramCanvas.drawText(this.mFlashlightOnLabel, j, paramInt2 - this.mFlashlightOnOffset - this.mTextDescent, this.mTextPaint);
        }
        if (this.mHaveCamera) {
          paramCanvas.drawText(this.mCameraOnLabel, j, paramInt2 - this.mCameraOnOffset - this.mTextDescent, this.mTextPaint);
        }
        if (this.mHaveWifi) {
          paramCanvas.drawText(this.mWifiRunningLabel, j, paramInt2 - this.mWifiRunningOffset - this.mTextDescent, this.mTextPaint);
        }
        paramCanvas.drawText(this.mCpuRunningLabel, j, paramInt2 - this.mCpuRunningOffset - this.mTextDescent, this.mTextPaint);
        paramCanvas.drawText(this.mChargingLabel, j, paramInt2 - this.mChargingOffset - this.mTextDescent, this.mTextPaint);
        paramCanvas.drawText(this.mScreenOnLabel, j, paramInt2 - this.mScreenOnOffset - this.mTextDescent, this.mTextPaint);
        this.mTextPaint.setTextAlign(localAlign);
      }
      paramCanvas.drawLine(this.mLevelLeft - this.mThinLineWidth, this.mLevelTop, this.mLevelLeft - this.mThinLineWidth, this.mLevelBottom + this.mThinLineWidth / 2, this.mTextPaint);
      if (!this.mLargeMode) {
        break;
      }
      paramInt2 = 0;
      while (paramInt2 < 10)
      {
        i = this.mLevelTop + this.mThinLineWidth / 2 + (this.mLevelBottom - this.mLevelTop) * paramInt2 / 10;
        paramCanvas.drawLine(this.mLevelLeft - this.mThinLineWidth * 2 - this.mThinLineWidth / 2, i, this.mLevelLeft - this.mThinLineWidth - this.mThinLineWidth / 2, i, this.mTextPaint);
        paramInt2 += 1;
      }
    }
    paramCanvas.drawText(this.mMaxPercentLabelString, 0.0F, this.mLevelTop, this.mTextPaint);
    paramCanvas.drawText(this.mMinPercentLabelString, this.mMaxPercentLabelStringWidth - this.mMinPercentLabelStringWidth, this.mLevelBottom - this.mThinLineWidth, this.mTextPaint);
    paramCanvas.drawLine(this.mLevelLeft / 2, this.mLevelBottom + this.mThinLineWidth, paramInt1, this.mLevelBottom + this.mThinLineWidth, this.mTextPaint);
    if (this.mDateLabels.size() > 0)
    {
      k = this.mLevelTop + this.mTextAscent;
      m = this.mLevelBottom;
      n = this.mLevelRight;
      this.mTextPaint.setTextAlign(Paint.Align.LEFT);
      paramInt1 = this.mDateLabels.size() - 1;
      if (paramInt1 >= 0)
      {
        localObject = (DateLabel)this.mDateLabels.get(paramInt1);
        paramInt2 = ((DateLabel)localObject).x - this.mThinLineWidth;
        j = ((DateLabel)localObject).x + this.mThinLineWidth * 2;
        i = j;
        if (((DateLabel)localObject).width + j >= n)
        {
          i = ((DateLabel)localObject).x - this.mThinLineWidth * 2 - ((DateLabel)localObject).width;
          j = i - this.mThinLineWidth;
          paramInt2 = j;
          if (j < n) {}
        }
        for (;;)
        {
          paramInt1 -= 1;
          break;
          if (paramInt2 >= this.mLevelLeft)
          {
            this.mDateLinePath.reset();
            this.mDateLinePath.moveTo(((DateLabel)localObject).x, k);
            this.mDateLinePath.lineTo(((DateLabel)localObject).x, m);
            paramCanvas.drawPath(this.mDateLinePath, this.mDateLinePaint);
            paramCanvas.drawText(((DateLabel)localObject).label, i, k - this.mTextAscent, this.mTextPaint);
          }
        }
      }
    }
  }
  
  void finishPaths(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Path paramPath1, int paramInt6, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6, boolean paramBoolean7, Path paramPath2)
  {
    if (paramPath1 != null)
    {
      if ((paramInt6 >= 0) && (paramInt6 < paramInt1))
      {
        if (paramPath2 != null) {
          paramPath2.lineTo(paramInt1, paramInt5);
        }
        paramPath1.lineTo(paramInt1, paramInt5);
      }
      paramPath1.lineTo(paramInt1, this.mLevelTop + paramInt3);
      paramPath1.lineTo(paramInt4, this.mLevelTop + paramInt3);
      paramPath1.close();
    }
    if (paramBoolean1) {
      this.mChargingPath.lineTo(paramInt1, paramInt2 - this.mChargingOffset);
    }
    if (paramBoolean2) {
      this.mScreenOnPath.lineTo(paramInt1, paramInt2 - this.mScreenOnOffset);
    }
    if (paramBoolean3) {
      this.mGpsOnPath.lineTo(paramInt1, paramInt2 - this.mGpsOnOffset);
    }
    if (paramBoolean4) {
      this.mFlashlightOnPath.lineTo(paramInt1, paramInt2 - this.mFlashlightOnOffset);
    }
    if (paramBoolean5) {
      this.mCameraOnPath.lineTo(paramInt1, paramInt2 - this.mCameraOnOffset);
    }
    if (paramBoolean6) {
      this.mWifiRunningPath.lineTo(paramInt1, paramInt2 - this.mWifiRunningOffset);
    }
    if (paramBoolean7) {
      this.mCpuRunningPath.lineTo(paramInt1, paramInt2 - this.mCpuRunningOffset);
    }
    if (this.mHavePhoneSignal) {
      this.mPhoneSignalChart.finish(paramInt1);
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    drawChart(paramCanvas, getWidth(), getHeight());
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    this.mMaxPercentLabelStringWidth = ((int)this.mTextPaint.measureText(this.mMaxPercentLabelString));
    this.mMinPercentLabelStringWidth = ((int)this.mTextPaint.measureText(this.mMinPercentLabelString));
    this.mDrainStringWidth = ((int)this.mHeaderTextPaint.measureText(this.mDrainString));
    this.mChargeLabelStringWidth = ((int)this.mHeaderTextPaint.measureText(this.mInfo.mChargeLabelString));
    this.mChargeDurationStringWidth = ((int)this.mHeaderTextPaint.measureText(this.mChargeDurationString));
    this.mTextAscent = ((int)this.mTextPaint.ascent());
    this.mTextDescent = ((int)this.mTextPaint.descent());
    this.mHeaderTextAscent = ((int)this.mHeaderTextPaint.ascent());
    this.mHeaderTextDescent = ((int)this.mHeaderTextPaint.descent());
    this.mHeaderHeight = ((this.mHeaderTextDescent - this.mHeaderTextAscent) * 2 - this.mTextAscent);
    setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), paramInt1), getDefaultSize(this.mChartMinHeight + this.mHeaderHeight, paramInt2));
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if ((this.mLastWidth == paramInt1) && (this.mLastHeight == paramInt2)) {
      return;
    }
    if ((this.mLastWidth == 0) || (this.mLastHeight == 0)) {
      return;
    }
    this.mLastWidth = paramInt1;
    this.mLastHeight = paramInt2;
    this.mBitmap = null;
    this.mCanvas = null;
    paramInt3 = this.mTextDescent - this.mTextAscent;
    int i5;
    int i;
    label363:
    label388:
    label413:
    label438:
    label460:
    label497:
    long l6;
    long l4;
    label620:
    long l1;
    long l2;
    int i7;
    int i8;
    int i6;
    int n;
    int i3;
    BatteryStats.HistoryItem localHistoryItem;
    Object localObject3;
    Object localObject4;
    boolean bool16;
    boolean bool17;
    boolean bool10;
    boolean bool11;
    boolean bool12;
    boolean bool13;
    boolean bool14;
    boolean bool3;
    boolean bool15;
    int m;
    int j;
    Object localObject1;
    int k;
    boolean bool8;
    boolean bool7;
    boolean bool6;
    boolean bool5;
    boolean bool4;
    boolean bool1;
    boolean bool2;
    Object localObject2;
    label931:
    long l3;
    label1109:
    label1150:
    label1174:
    int i4;
    if (paramInt2 > paramInt3 * 10 + this.mChartMinHeight)
    {
      this.mLargeMode = true;
      if (paramInt2 > paramInt3 * 15)
      {
        this.mLineWidth = (paramInt3 / 2);
        if (this.mLineWidth <= 0) {
          this.mLineWidth = 1;
        }
        this.mLevelTop = this.mHeaderHeight;
        this.mLevelLeft = (this.mMaxPercentLabelStringWidth + this.mThinLineWidth * 3);
        this.mLevelRight = paramInt1;
        i5 = this.mLevelRight - this.mLevelLeft;
        this.mTextPaint.setStrokeWidth(this.mThinLineWidth);
        this.mBatteryGoodPaint.setStrokeWidth(this.mThinLineWidth);
        this.mBatteryWarnPaint.setStrokeWidth(this.mThinLineWidth);
        this.mBatteryCriticalPaint.setStrokeWidth(this.mThinLineWidth);
        this.mChargingPaint.setStrokeWidth(this.mLineWidth);
        this.mScreenOnPaint.setStrokeWidth(this.mLineWidth);
        this.mGpsOnPaint.setStrokeWidth(this.mLineWidth);
        this.mCameraOnPaint.setStrokeWidth(this.mLineWidth);
        this.mFlashlightOnPaint.setStrokeWidth(this.mLineWidth);
        this.mWifiRunningPaint.setStrokeWidth(this.mLineWidth);
        this.mCpuRunningPaint.setStrokeWidth(this.mLineWidth);
        this.mDebugRectPaint.setStrokeWidth(1.0F);
        paramInt3 += this.mLineWidth;
        if (!this.mLargeMode) {
          break label2071;
        }
        this.mChargingOffset = this.mLineWidth;
        this.mScreenOnOffset = (this.mChargingOffset + paramInt3);
        this.mCpuRunningOffset = (this.mScreenOnOffset + paramInt3);
        this.mWifiRunningOffset = (this.mCpuRunningOffset + paramInt3);
        i = this.mWifiRunningOffset;
        if (!this.mHaveWifi) {
          break label2042;
        }
        paramInt4 = paramInt3;
        this.mGpsOnOffset = (paramInt4 + i);
        i = this.mGpsOnOffset;
        if (!this.mHaveGps) {
          break label2048;
        }
        paramInt4 = paramInt3;
        this.mFlashlightOnOffset = (paramInt4 + i);
        i = this.mFlashlightOnOffset;
        if (!this.mHaveFlashlight) {
          break label2054;
        }
        paramInt4 = paramInt3;
        this.mCameraOnOffset = (paramInt4 + i);
        i = this.mCameraOnOffset;
        if (!this.mHaveCamera) {
          break label2060;
        }
        paramInt4 = paramInt3;
        this.mPhoneSignalOffset = (paramInt4 + i);
        paramInt4 = this.mPhoneSignalOffset;
        if (!this.mHavePhoneSignal) {
          break label2066;
        }
        this.mLevelOffset = (paramInt4 + paramInt3 + this.mLineWidth * 2 + this.mLineWidth / 2);
        if (this.mHavePhoneSignal) {
          this.mPhoneSignalChart.init(paramInt1);
        }
        this.mBatLevelPath.reset();
        this.mBatGoodPath.reset();
        this.mBatWarnPath.reset();
        this.mTimeRemainPath.reset();
        this.mBatCriticalPath.reset();
        this.mScreenOnPath.reset();
        this.mGpsOnPath.reset();
        this.mFlashlightOnPath.reset();
        this.mCameraOnPath.reset();
        this.mWifiRunningPath.reset();
        this.mCpuRunningPath.reset();
        this.mChargingPath.reset();
        this.mTimeLabels.clear();
        this.mDateLabels.clear();
        l6 = this.mStartWallTime;
        if (this.mEndWallTime <= l6) {
          break label2141;
        }
        l4 = this.mEndWallTime - l6;
        l1 = this.mStartWallTime;
        l2 = 0L;
        i7 = this.mBatLow;
        i8 = this.mBatHigh - this.mBatLow;
        i6 = paramInt2 - this.mLevelOffset - this.mLevelTop;
        this.mLevelBottom = (this.mLevelTop + i6);
        paramInt3 = this.mLevelLeft;
        i = this.mLevelLeft;
        int i1 = -1;
        n = -1;
        i3 = 0;
        localHistoryItem = null;
        localObject3 = null;
        Object localObject5 = null;
        localObject4 = null;
        bool16 = false;
        boolean bool9 = false;
        bool17 = false;
        bool10 = false;
        boolean bool18 = false;
        bool11 = false;
        boolean bool19 = false;
        bool12 = false;
        boolean bool20 = false;
        bool13 = false;
        boolean bool21 = false;
        bool14 = false;
        bool3 = false;
        boolean bool22 = false;
        bool15 = false;
        m = 0;
        int i9 = this.mNumHist;
        paramInt4 = i;
        j = n;
        localObject1 = localHistoryItem;
        k = i1;
        bool8 = bool16;
        bool7 = bool17;
        bool6 = bool18;
        bool5 = bool19;
        bool4 = bool20;
        bool1 = bool21;
        bool2 = bool22;
        localObject2 = localObject5;
        if (this.mEndDataWallTime <= this.mStartWallTime) {
          break label2678;
        }
        paramInt4 = i;
        j = n;
        localObject1 = localHistoryItem;
        k = i1;
        bool8 = bool16;
        bool7 = bool17;
        bool6 = bool18;
        bool5 = bool19;
        bool4 = bool20;
        bool1 = bool21;
        bool2 = bool22;
        localObject2 = localObject5;
        if (!this.mStats.startIteratingHistoryLocked()) {
          break label2678;
        }
        localHistoryItem = new BatteryStats.HistoryItem();
        localObject2 = localObject4;
        bool2 = bool15;
        bool1 = bool14;
        bool4 = bool13;
        bool5 = bool12;
        bool6 = bool11;
        bool7 = bool10;
        bool8 = bool9;
        k = i1;
        localObject1 = localObject3;
        j = n;
        paramInt4 = i;
        if ((!this.mStats.getNextHistoryLocked(localHistoryItem)) || (i3 >= i9)) {
          break label2671;
        }
        if (!localHistoryItem.isDeltaData()) {
          break label2430;
        }
        l3 = l1 + (localHistoryItem.time - l2);
        long l5 = localHistoryItem.time;
        i = this.mLevelLeft + (int)((l3 - l6) * i5 / l4);
        paramInt3 = i;
        if (i < 0) {
          paramInt3 = 0;
        }
        i = this.mLevelTop + i6 - (localHistoryItem.batteryLevel - i7) * (i6 - 1) / i8;
        int i2 = paramInt4;
        i1 = j;
        localObject4 = localObject1;
        n = k;
        localObject3 = localObject2;
        if (k != paramInt3)
        {
          i2 = paramInt4;
          i1 = j;
          localObject4 = localObject1;
          n = k;
          localObject3 = localObject2;
          if (j != i)
          {
            j = localHistoryItem.batteryLevel;
            if (j > this.mBatteryCriticalLevel) {
              break label2147;
            }
            localObject3 = this.mBatCriticalPath;
            if (localObject3 == localObject2) {
              break label2171;
            }
            if (localObject2 != null) {
              ((Path)localObject2).lineTo(paramInt3, i);
            }
            if (localObject3 != null) {
              ((Path)localObject3).moveTo(paramInt3, i);
            }
            localObject4 = localObject3;
            if (localObject1 != null) {
              break label2197;
            }
            localObject1 = this.mBatLevelPath;
            ((Path)localObject1).moveTo(paramInt3, i);
            paramInt4 = paramInt3;
            n = paramInt3;
            i1 = i;
            localObject3 = localObject4;
            localObject4 = localObject1;
            i2 = paramInt4;
          }
        }
        i = paramInt3;
        paramInt4 = i2;
        j = i1;
        localObject1 = localObject4;
        k = n;
        bool10 = bool8;
        bool11 = bool7;
        bool12 = bool6;
        bool13 = bool5;
        bool14 = bool4;
        bool16 = bool1;
        bool15 = bool2;
        localObject2 = localObject3;
        l1 = l3;
        l2 = l5;
        bool17 = bool3;
        i4 = m;
        if (this.mLargeMode)
        {
          if ((localHistoryItem.states & 0x80000) == 0) {
            break label2210;
          }
          bool10 = true;
          label1282:
          bool9 = bool8;
          if (bool10 != bool8)
          {
            if (!bool10) {
              break label2216;
            }
            this.mChargingPath.moveTo(paramInt3, paramInt2 - this.mChargingOffset);
            label1314:
            bool9 = bool10;
          }
          if ((localHistoryItem.states & 0x100000) == 0) {
            break label2235;
          }
          bool10 = true;
          label1333:
          bool8 = bool7;
          if (bool10 != bool7)
          {
            if (!bool10) {
              break label2241;
            }
            this.mScreenOnPath.moveTo(paramInt3, paramInt2 - this.mScreenOnOffset);
            label1365:
            bool8 = bool10;
          }
          if ((localHistoryItem.states & 0x20000000) == 0) {
            break label2260;
          }
          bool10 = true;
          label1384:
          bool7 = bool6;
          if (bool10 != bool6)
          {
            if (!bool10) {
              break label2266;
            }
            this.mGpsOnPath.moveTo(paramInt3, paramInt2 - this.mGpsOnOffset);
            label1416:
            bool7 = bool10;
          }
          if ((localHistoryItem.states2 & 0x8000000) == 0) {
            break label2285;
          }
          bool10 = true;
          label1435:
          bool6 = bool5;
          if (bool10 != bool5)
          {
            if (!bool10) {
              break label2291;
            }
            this.mFlashlightOnPath.moveTo(paramInt3, paramInt2 - this.mFlashlightOnOffset);
            label1467:
            bool6 = bool10;
          }
          if ((localHistoryItem.states2 & 0x200000) == 0) {
            break label2310;
          }
          bool10 = true;
          label1486:
          bool5 = bool4;
          if (bool10 != bool4)
          {
            if (!bool10) {
              break label2316;
            }
            this.mCameraOnPath.moveTo(paramInt3, paramInt2 - this.mCameraOnOffset);
            label1518:
            bool5 = bool10;
          }
          paramInt4 = (localHistoryItem.states2 & 0xF) >> 0;
          if (m == paramInt4) {
            break label2344;
          }
          m = paramInt4;
          switch (paramInt4)
          {
          case 4: 
          case 5: 
          case 6: 
          case 7: 
          case 8: 
          case 9: 
          case 10: 
          default: 
            bool3 = true;
            bool4 = true;
            label1618:
            bool10 = bool4;
            if ((localHistoryItem.states & 0x18010000) != 0) {
              bool10 = true;
            }
            bool4 = bool1;
            if (bool10 != bool1)
            {
              if (bool10)
              {
                this.mWifiRunningPath.moveTo(paramInt3, paramInt2 - this.mWifiRunningOffset);
                label1669:
                bool4 = bool10;
              }
            }
            else
            {
              if ((localHistoryItem.states & 0x80000000) == 0) {
                break label2370;
              }
              bool10 = true;
              label1688:
              bool1 = bool2;
              if (bool10 != bool2)
              {
                if (!bool10) {
                  break label2376;
                }
                this.mCpuRunningPath.moveTo(paramInt3, paramInt2 - this.mCpuRunningOffset);
                label1720:
                bool1 = bool10;
              }
              i = paramInt3;
              paramInt4 = i2;
              j = i1;
              localObject1 = localObject4;
              k = n;
              bool10 = bool9;
              bool11 = bool8;
              bool12 = bool7;
              bool13 = bool6;
              bool14 = bool5;
              bool16 = bool4;
              bool15 = bool1;
              localObject2 = localObject3;
              l1 = l3;
              l2 = l5;
              bool17 = bool3;
              i4 = m;
              if (this.mLargeMode)
              {
                i = paramInt3;
                paramInt4 = i2;
                j = i1;
                localObject1 = localObject4;
                k = n;
                bool10 = bool9;
                bool11 = bool8;
                bool12 = bool7;
                bool13 = bool6;
                bool14 = bool5;
                bool16 = bool4;
                bool15 = bool1;
                localObject2 = localObject3;
                l1 = l3;
                l2 = l5;
                bool17 = bool3;
                i4 = m;
                if (this.mHavePhoneSignal)
                {
                  if ((localHistoryItem.states & 0x1C0) >> 6 != 3) {
                    break label2395;
                  }
                  paramInt4 = 0;
                  label1891:
                  this.mPhoneSignalChart.addTick(paramInt3, paramInt4);
                  i4 = m;
                  bool17 = bool3;
                  l2 = l5;
                  l1 = l3;
                  localObject2 = localObject3;
                  bool15 = bool1;
                  bool16 = bool4;
                  bool14 = bool5;
                  bool13 = bool6;
                  bool12 = bool7;
                  bool11 = bool8;
                  bool10 = bool9;
                  k = n;
                  localObject1 = localObject4;
                  j = i1;
                  paramInt4 = i2;
                  i = paramInt3;
                }
              }
            }
            break;
          }
        }
      }
    }
    for (;;)
    {
      i3 += 1;
      paramInt3 = i;
      bool8 = bool10;
      bool7 = bool11;
      bool6 = bool12;
      bool5 = bool13;
      bool4 = bool14;
      bool1 = bool16;
      bool2 = bool15;
      bool3 = bool17;
      m = i4;
      break label931;
      this.mLineWidth = (paramInt3 / 3);
      break;
      this.mLargeMode = false;
      this.mLineWidth = this.mThinLineWidth;
      break;
      label2042:
      paramInt4 = 0;
      break label363;
      label2048:
      paramInt4 = 0;
      break label388;
      label2054:
      paramInt4 = 0;
      break label413;
      label2060:
      paramInt4 = 0;
      break label438;
      label2066:
      paramInt3 = 0;
      break label460;
      label2071:
      this.mPhoneSignalOffset = 0;
      this.mChargingOffset = 0;
      this.mCpuRunningOffset = 0;
      this.mWifiRunningOffset = 0;
      this.mFlashlightOnOffset = 0;
      this.mCameraOnOffset = 0;
      this.mGpsOnOffset = 0;
      this.mScreenOnOffset = 0;
      this.mLevelOffset = (this.mThinLineWidth * 4 + paramInt3);
      if (!this.mHavePhoneSignal) {
        break label497;
      }
      this.mPhoneSignalChart.init(0);
      break label497;
      label2141:
      l4 = 1L;
      break label620;
      label2147:
      if (j <= this.mBatteryWarnLevel)
      {
        localObject3 = this.mBatWarnPath;
        break label1109;
      }
      localObject3 = null;
      break label1109;
      label2171:
      localObject4 = localObject2;
      if (localObject3 == null) {
        break label1150;
      }
      ((Path)localObject3).lineTo(paramInt3, i);
      localObject4 = localObject2;
      break label1150;
      label2197:
      ((Path)localObject1).lineTo(paramInt3, i);
      break label1174;
      label2210:
      bool10 = false;
      break label1282;
      label2216:
      this.mChargingPath.lineTo(paramInt3, paramInt2 - this.mChargingOffset);
      break label1314;
      label2235:
      bool10 = false;
      break label1333;
      label2241:
      this.mScreenOnPath.lineTo(paramInt3, paramInt2 - this.mScreenOnOffset);
      break label1365;
      label2260:
      bool10 = false;
      break label1384;
      label2266:
      this.mGpsOnPath.lineTo(paramInt3, paramInt2 - this.mGpsOnOffset);
      break label1416;
      label2285:
      bool10 = false;
      break label1435;
      label2291:
      this.mFlashlightOnPath.lineTo(paramInt3, paramInt2 - this.mFlashlightOnOffset);
      break label1467;
      label2310:
      bool10 = false;
      break label1486;
      label2316:
      this.mCameraOnPath.lineTo(paramInt3, paramInt2 - this.mCameraOnOffset);
      break label1518;
      bool3 = false;
      bool4 = false;
      break label1618;
      label2344:
      bool4 = bool3;
      break label1618;
      this.mWifiRunningPath.lineTo(paramInt3, paramInt2 - this.mWifiRunningOffset);
      break label1669;
      label2370:
      bool10 = false;
      break label1688;
      label2376:
      this.mCpuRunningPath.lineTo(paramInt3, paramInt2 - this.mCpuRunningOffset);
      break label1720;
      label2395:
      if ((localHistoryItem.states & 0x200000) != 0)
      {
        paramInt4 = 1;
        break label1891;
      }
      paramInt4 = ((localHistoryItem.states & 0x38) >> 3) + 2;
      break label1891;
      label2430:
      if (localHistoryItem.cmd != 5)
      {
        l3 = l1;
        if (localHistoryItem.cmd != 7) {}
      }
      else
      {
        if (localHistoryItem.currentTime < this.mStartWallTime) {
          break label2605;
        }
      }
      label2605:
      for (l3 = localHistoryItem.currentTime;; l3 = this.mStartWallTime + (localHistoryItem.time - this.mHistStart))
      {
        l2 = localHistoryItem.time;
        if ((localHistoryItem.cmd == 6) || ((localHistoryItem.cmd == 5) && (Math.abs(l1 - l3) <= 3600000L))) {
          break label2625;
        }
        if (localObject1 == null) {
          break label3621;
        }
        finishPaths(paramInt3 + 1, paramInt2, i6, paramInt4, j, (Path)localObject1, k, bool8, bool7, bool6, bool5, bool4, bool1, bool2, (Path)localObject2);
        j = -1;
        k = -1;
        localObject1 = null;
        localObject2 = null;
        bool15 = false;
        bool14 = false;
        bool13 = false;
        bool12 = false;
        bool11 = false;
        bool10 = false;
        i = paramInt3;
        bool16 = bool1;
        l1 = l3;
        bool17 = bool3;
        i4 = m;
        break;
      }
      label2625:
      i = paramInt3;
      bool10 = bool8;
      bool11 = bool7;
      bool12 = bool6;
      bool13 = bool5;
      bool14 = bool4;
      bool16 = bool1;
      bool15 = bool2;
      l1 = l3;
      bool17 = bool3;
      i4 = m;
      continue;
      label2671:
      this.mStats.finishIteratingHistoryLocked();
      label2678:
      if ((j < 0) || (k < 0))
      {
        i = this.mLevelLeft;
        m = this.mLevelTop + i6 - (this.mInfo.mBatteryLevel - i7) * (i6 - 1) / i8;
        paramInt3 = (byte)this.mInfo.mBatteryLevel;
        if (paramInt3 <= this.mBatteryCriticalLevel)
        {
          localObject1 = this.mBatCriticalPath;
          if (localObject1 != null)
          {
            ((Path)localObject1).moveTo(i, m);
            localObject2 = localObject1;
          }
          this.mBatLevelPath.moveTo(i, m);
          localObject4 = this.mBatLevelPath;
          paramInt3 = paramInt1;
          localObject3 = localObject2;
          label2790:
          finishPaths(paramInt3, paramInt2, i6, paramInt4, m, (Path)localObject4, i, bool8, bool7, bool6, bool5, bool4, bool1, bool2, (Path)localObject3);
          if (paramInt3 < paramInt1)
          {
            this.mTimeRemainPath.moveTo(paramInt3, m);
            paramInt1 = this.mLevelTop;
            paramInt2 = (100 - i7) * (i6 - 1) / i8;
            paramInt4 = this.mLevelTop + i6 - (0 - i7) * (i6 - 1) / i8;
            if (!this.mInfo.mDischarging) {
              break label3573;
            }
            this.mTimeRemainPath.lineTo(this.mLevelRight, paramInt4);
          }
        }
      }
      for (;;)
      {
        this.mTimeRemainPath.lineTo(paramInt3, paramInt4);
        this.mTimeRemainPath.close();
        if ((this.mStartWallTime > 0L) && (this.mEndWallTime > this.mStartWallTime))
        {
          bool1 = is24Hour();
          localObject2 = Calendar.getInstance();
          ((Calendar)localObject2).setTimeInMillis(this.mStartWallTime);
          ((Calendar)localObject2).set(14, 0);
          ((Calendar)localObject2).set(13, 0);
          ((Calendar)localObject2).set(12, 0);
          l2 = ((Calendar)localObject2).getTimeInMillis();
          l1 = l2;
          if (l2 < this.mStartWallTime)
          {
            ((Calendar)localObject2).set(11, ((Calendar)localObject2).get(11) + 1);
            l1 = ((Calendar)localObject2).getTimeInMillis();
          }
          localObject1 = Calendar.getInstance();
          ((Calendar)localObject1).setTimeInMillis(this.mEndWallTime);
          ((Calendar)localObject1).set(14, 0);
          ((Calendar)localObject1).set(13, 0);
          ((Calendar)localObject1).set(12, 0);
          l2 = ((Calendar)localObject1).getTimeInMillis();
          if (l1 < l2)
          {
            addTimeLabel((Calendar)localObject2, this.mLevelLeft, this.mLevelRight, bool1);
            localObject3 = Calendar.getInstance();
            ((Calendar)localObject3).setTimeInMillis(this.mStartWallTime + (this.mEndWallTime - this.mStartWallTime) / 2L);
            ((Calendar)localObject3).set(14, 0);
            ((Calendar)localObject3).set(13, 0);
            ((Calendar)localObject3).set(12, 0);
            l3 = ((Calendar)localObject3).getTimeInMillis();
            if ((l3 > l1) && (l3 < l2)) {
              addTimeLabel((Calendar)localObject3, this.mLevelLeft, this.mLevelRight, bool1);
            }
            addTimeLabel((Calendar)localObject1, this.mLevelLeft, this.mLevelRight, bool1);
          }
          if ((((Calendar)localObject2).get(6) != ((Calendar)localObject1).get(6)) || (((Calendar)localObject2).get(1) != ((Calendar)localObject1).get(1)))
          {
            bool1 = isDayFirst();
            ((Calendar)localObject2).set(11, 0);
            l2 = ((Calendar)localObject2).getTimeInMillis();
            l1 = l2;
            if (l2 < this.mStartWallTime)
            {
              ((Calendar)localObject2).set(6, ((Calendar)localObject2).get(6) + 1);
              l1 = ((Calendar)localObject2).getTimeInMillis();
            }
            ((Calendar)localObject1).set(11, 0);
            l2 = ((Calendar)localObject1).getTimeInMillis();
            if (l1 < l2)
            {
              addDateLabel((Calendar)localObject2, this.mLevelLeft, this.mLevelRight, bool1);
              localObject2 = Calendar.getInstance();
              ((Calendar)localObject2).setTimeInMillis((l2 - l1) / 2L + l1 + 7200000L);
              ((Calendar)localObject2).set(11, 0);
              ((Calendar)localObject2).set(12, 0);
              l3 = ((Calendar)localObject2).getTimeInMillis();
              if ((l3 > l1) && (l3 < l2)) {
                addDateLabel((Calendar)localObject2, this.mLevelLeft, this.mLevelRight, bool1);
              }
            }
            addDateLabel((Calendar)localObject1, this.mLevelLeft, this.mLevelRight, bool1);
          }
        }
        if (this.mTimeLabels.size() >= 2) {
          break label3610;
        }
        this.mDurationString = Formatter.formatShortElapsedTime(getContext(), this.mEndWallTime - this.mStartWallTime);
        this.mDurationStringWidth = ((int)this.mTextPaint.measureText(this.mDurationString));
        return;
        if (paramInt3 <= this.mBatteryWarnLevel)
        {
          localObject1 = this.mBatWarnPath;
          break;
        }
        localObject1 = null;
        break;
        n = this.mLevelLeft + (int)((this.mEndDataWallTime - l6) * i5 / l4);
        paramInt3 = n;
        m = j;
        localObject4 = localObject1;
        i = k;
        localObject3 = localObject2;
        if (n >= 0) {
          break label2790;
        }
        paramInt3 = 0;
        m = j;
        localObject4 = localObject1;
        i = k;
        localObject3 = localObject2;
        break label2790;
        label3573:
        this.mTimeRemainPath.lineTo(this.mLevelRight, paramInt1 + i6 - paramInt2);
        this.mTimeRemainPath.lineTo(this.mLevelRight, paramInt4);
      }
      label3610:
      this.mDurationString = null;
      this.mDurationStringWidth = 0;
      return;
      label3621:
      i = paramInt3;
      bool10 = bool8;
      bool11 = bool7;
      bool12 = bool6;
      bool13 = bool5;
      bool14 = bool4;
      bool16 = bool1;
      bool15 = bool2;
      l1 = l3;
      bool17 = bool3;
      i4 = m;
    }
  }
  
  void setStats(BatteryStats paramBatteryStats, Intent paramIntent)
  {
    this.mStats = paramBatteryStats;
    this.mBatteryBroadcast = paramIntent;
    long l1 = SystemClock.elapsedRealtime() * 1000L;
    this.mStatsPeriod = this.mStats.computeBatteryRealtime(l1, 0);
    this.mChargingLabel = getContext().getString(2131692437);
    this.mScreenOnLabel = getContext().getString(2131692438);
    this.mGpsOnLabel = getContext().getString(2131692439);
    this.mCameraOnLabel = getContext().getString(2131692440);
    this.mFlashlightOnLabel = getContext().getString(2131692441);
    this.mWifiRunningLabel = getContext().getString(2131692442);
    this.mCpuRunningLabel = getContext().getString(2131692443);
    this.mPhoneSignalLabel = getContext().getString(2131692444);
    this.mMaxPercentLabelString = Utils.formatPercentage(100);
    this.mMinPercentLabelString = Utils.formatPercentage(0);
    this.mInfo = BatteryInfo.getBatteryInfo(getContext(), this.mBatteryBroadcast, this.mStats, l1);
    this.mDrainString = "";
    this.mChargeDurationString = "";
    setContentDescription(this.mInfo.mChargeLabelString);
    int i1 = 0;
    int i4 = 0;
    int k = 0;
    int n = -1;
    this.mBatLow = 0;
    this.mBatHigh = 100;
    this.mStartWallTime = 0L;
    this.mEndDataWallTime = 0L;
    this.mEndWallTime = 0L;
    this.mHistStart = 0L;
    this.mHistEnd = 0L;
    l1 = 0L;
    long l2 = 0L;
    int m = 0;
    int j = 0;
    int i2 = 0;
    int i = 0;
    int i3 = 1;
    long l3 = l2;
    long l4 = l1;
    if (paramBatteryStats.startIteratingHistoryLocked())
    {
      paramIntent = new BatteryStats.HistoryItem();
      for (;;)
      {
        m = j;
        i2 = i;
        i4 = k;
        l3 = l2;
        l4 = l1;
        if (!paramBatteryStats.getNextHistoryLocked(paramIntent)) {
          break;
        }
        m = i1 + 1;
        i2 = i3;
        if (i3 != 0)
        {
          i2 = 0;
          this.mHistStart = paramIntent.time;
        }
        if (paramIntent.cmd != 5)
        {
          l3 = l2;
          l4 = l1;
          if (paramIntent.cmd != 7) {}
        }
        else
        {
          if ((paramIntent.currentTime > 15552000000L + l1) || (paramIntent.time < this.mHistStart + 300000L)) {
            this.mStartWallTime = 0L;
          }
          l1 = paramIntent.currentTime;
          l2 = paramIntent.time;
          l3 = l2;
          l4 = l1;
          if (this.mStartWallTime == 0L)
          {
            this.mStartWallTime = (l1 - (l2 - this.mHistStart));
            l4 = l1;
            l3 = l2;
          }
        }
        i3 = i2;
        i1 = m;
        l2 = l3;
        l1 = l4;
        if (paramIntent.isDeltaData())
        {
          if ((paramIntent.batteryLevel != n) || (m == 1)) {
            n = paramIntent.batteryLevel;
          }
          k = m;
          this.mHistDataEnd = paramIntent.time;
          j |= paramIntent.states;
          i |= paramIntent.states2;
          i3 = i2;
          i1 = m;
          l2 = l3;
          l1 = l4;
        }
      }
    }
    this.mHistEnd = (this.mHistDataEnd + this.mInfo.remainingTimeUs / 1000L);
    this.mEndDataWallTime = (this.mHistDataEnd + l4 - l3);
    this.mEndWallTime = (this.mEndDataWallTime + this.mInfo.remainingTimeUs / 1000L);
    this.mNumHist = i4;
    boolean bool;
    if ((0x20000000 & m) != 0)
    {
      bool = true;
      this.mHaveGps = bool;
      if ((0x8000000 & i2) == 0) {
        break label739;
      }
      bool = true;
      label644:
      this.mHaveFlashlight = bool;
      if ((0x200000 & i2) == 0) {
        break label745;
      }
      bool = true;
      label662:
      this.mHaveCamera = bool;
      if ((0x20000000 & i2) != 0) {
        break label751;
      }
      if ((0x18010000 & m) == 0) {
        break label757;
      }
      bool = true;
    }
    for (;;)
    {
      this.mHaveWifi = bool;
      if (!Utils.isWifiOnly(getContext())) {
        this.mHavePhoneSignal = true;
      }
      if (this.mHistEnd <= this.mHistStart) {
        this.mHistEnd = (this.mHistStart + 1L);
      }
      return;
      bool = false;
      break;
      label739:
      bool = false;
      break label644;
      label745:
      bool = false;
      break label662;
      label751:
      bool = true;
      continue;
      label757:
      bool = false;
    }
  }
  
  static class ChartData
  {
    int[] mColors;
    int mLastBin;
    int mNumTicks;
    Paint[] mPaints;
    int[] mTicks;
    
    void addTick(int paramInt1, int paramInt2)
    {
      if ((paramInt2 != this.mLastBin) && (this.mNumTicks < this.mTicks.length))
      {
        this.mTicks[this.mNumTicks] = (0xFFFF & paramInt1 | paramInt2 << 16);
        this.mNumTicks += 1;
        this.mLastBin = paramInt2;
      }
    }
    
    void draw(Canvas paramCanvas, int paramInt1, int paramInt2)
    {
      int j = 0;
      int k = 0;
      int i = 0;
      while (i < this.mNumTicks)
      {
        int n = this.mTicks[i];
        int m = n & 0xFFFF;
        if (j != 0) {
          paramCanvas.drawRect(k, paramInt1, m, paramInt1 + paramInt2, this.mPaints[j]);
        }
        j = (0xFFFF0000 & n) >> 16;
        k = m;
        i += 1;
      }
    }
    
    void finish(int paramInt)
    {
      if (this.mLastBin != 0) {
        addTick(paramInt, 0);
      }
    }
    
    void init(int paramInt)
    {
      if (paramInt > 0) {}
      for (this.mTicks = new int[paramInt * 2];; this.mTicks = null)
      {
        this.mNumTicks = 0;
        this.mLastBin = 0;
        return;
      }
    }
    
    void setColors(int[] paramArrayOfInt)
    {
      this.mColors = paramArrayOfInt;
      this.mPaints = new Paint[paramArrayOfInt.length];
      int i = 0;
      while (i < paramArrayOfInt.length)
      {
        this.mPaints[i] = new Paint();
        this.mPaints[i].setColor(paramArrayOfInt[i]);
        this.mPaints[i].setStyle(Paint.Style.FILL);
        i += 1;
      }
    }
  }
  
  static class DateLabel
  {
    final String label;
    final int width;
    final int x;
    
    DateLabel(TextPaint paramTextPaint, int paramInt, Calendar paramCalendar, boolean paramBoolean)
    {
      this.x = paramInt;
      Locale localLocale = Locale.getDefault();
      if (paramBoolean) {}
      for (String str = "dM";; str = "Md")
      {
        this.label = DateFormat.format(DateFormat.getBestDateTimePattern(localLocale, str), paramCalendar).toString();
        this.width = ((int)paramTextPaint.measureText(this.label));
        return;
      }
    }
  }
  
  static class TextAttrs
  {
    int styleIndex = -1;
    ColorStateList textColor = null;
    int textSize = 15;
    int typefaceIndex = -1;
    
    void apply(Context paramContext, TextPaint paramTextPaint)
    {
      paramTextPaint.density = paramContext.getResources().getDisplayMetrics().density;
      paramTextPaint.setCompatibilityScaling(paramContext.getResources().getCompatibilityInfo().applicationScale);
      paramTextPaint.setColor(this.textColor.getDefaultColor());
      paramTextPaint.setTextSize(this.textSize);
      switch (this.typefaceIndex)
      {
      default: 
        return;
      case 1: 
        paramContext = Typeface.SANS_SERIF;
        return;
      case 2: 
        paramContext = Typeface.SERIF;
        return;
      }
      paramContext = Typeface.MONOSPACE;
    }
    
    void retrieve(Context paramContext, TypedArray paramTypedArray, int paramInt)
    {
      Object localObject = null;
      paramInt = paramTypedArray.getResourceId(paramInt, -1);
      paramTypedArray = (TypedArray)localObject;
      if (paramInt != -1) {
        paramTypedArray = paramContext.obtainStyledAttributes(paramInt, com.android.internal.R.styleable.TextAppearance);
      }
      if (paramTypedArray != null)
      {
        int i = paramTypedArray.getIndexCount();
        paramInt = 0;
        if (paramInt < i)
        {
          int j = paramTypedArray.getIndex(paramInt);
          switch (j)
          {
          }
          for (;;)
          {
            paramInt += 1;
            break;
            this.textColor = paramTypedArray.getColorStateList(j);
            continue;
            this.textSize = paramTypedArray.getDimensionPixelSize(j, this.textSize);
            continue;
            this.typefaceIndex = paramTypedArray.getInt(j, -1);
            continue;
            this.styleIndex = paramTypedArray.getInt(j, -1);
          }
        }
        paramTypedArray.recycle();
      }
    }
    
    public void setTypeface(TextPaint paramTextPaint, Typeface paramTypeface, int paramInt)
    {
      boolean bool = false;
      if (paramInt > 0)
      {
        int i;
        if (paramTypeface == null)
        {
          paramTypeface = Typeface.defaultFromStyle(paramInt);
          if (paramTypeface == null) {
            break label72;
          }
          i = paramTypeface.getStyle();
          label26:
          paramInt &= i;
          if ((paramInt & 0x1) != 0) {
            bool = true;
          }
          paramTextPaint.setFakeBoldText(bool);
          if ((paramInt & 0x2) == 0) {
            break label78;
          }
        }
        label72:
        label78:
        for (float f = -0.25F;; f = 0.0F)
        {
          paramTextPaint.setTextSkewX(f);
          return;
          paramTypeface = Typeface.create(paramTypeface, paramInt);
          break;
          i = 0;
          break label26;
        }
      }
      paramTextPaint.setFakeBoldText(false);
      paramTextPaint.setTextSkewX(0.0F);
    }
  }
  
  static class TimeLabel
  {
    final String label;
    final int width;
    final int x;
    
    TimeLabel(TextPaint paramTextPaint, int paramInt, Calendar paramCalendar, boolean paramBoolean)
    {
      this.x = paramInt;
      Locale localLocale = Locale.getDefault();
      if (paramBoolean) {}
      for (String str = "km";; str = "ha")
      {
        this.label = DateFormat.format(DateFormat.getBestDateTimePattern(localLocale, str), paramCalendar).toString();
        this.width = ((int)paramTextPaint.measureText(this.label));
        return;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fuelgauge\BatteryHistoryChart.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */