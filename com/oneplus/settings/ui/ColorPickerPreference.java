package com.oneplus.settings.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v7.preference.Preference.BaseSavedState;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.R.styleable;
import com.oneplus.settings.utils.OPUtils;
import java.util.Arrays;
import java.util.List;

public class ColorPickerPreference
  extends CustomDialogPreference
{
  private String mColor;
  private Context mContext;
  private String mDefaultColor = "";
  private String[] mDefaultColors;
  private int mDisabledCellColor;
  ImageView mImageView;
  private TextView mMessage;
  private CharSequence mMessageText;
  private String[] mPalette;
  private int[] mPaletteNamesResIds;
  private int mRippleEffectColor;
  private String mTmpColor;
  private boolean mUseColorLabelAsSummary;
  private View[] mViews;
  private boolean mVisibility = false;
  
  public ColorPickerPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ColorPickerPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ColorPickerPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public ColorPickerPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setLayoutResource(2130968794);
    this.mContext = paramContext;
    this.mDefaultColors = new String[] { this.mContext.getResources().getString(2131493723), this.mContext.getResources().getString(2131493731) };
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ColorPickerPreference, 0, 0);
    this.mRippleEffectColor = paramAttributeSet.getColor(0, paramContext.getResources().getColor(2131493555));
    this.mDisabledCellColor = paramAttributeSet.getColor(1, paramContext.getResources().getColor(2131493556));
    paramAttributeSet.recycle();
    setNeutralButtonText(2131690262);
    setNegativeButtonText(17039360);
    setPositiveButtonText(17039370);
    setDialogLayoutResource(2130968898);
    if (getSummary() == null)
    {
      this.mUseColorLabelAsSummary = true;
      return;
    }
    this.mUseColorLabelAsSummary = false;
  }
  
  private boolean isDefaultColor()
  {
    int i = 0;
    while (i < this.mDefaultColors.length)
    {
      if (this.mDefaultColors[i].equals(this.mColor)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private void setTmpColor(String paramString)
  {
    this.mTmpColor = paramString;
    setSelection(this.mTmpColor, 0);
  }
  
  private void updateSummary()
  {
    int i;
    if ((this.mUseColorLabelAsSummary) || ((this.mPaletteNamesResIds != null) && (this.mPaletteNamesResIds.length >= 0)))
    {
      if (this.mPalette != null) {
        break label69;
      }
      i = -1;
      if ((this.mPaletteNamesResIds == null) || (i < 0) || (i >= this.mPaletteNamesResIds.length)) {
        break label89;
      }
      setSummary(getContext().getString(this.mPaletteNamesResIds[i]));
    }
    label69:
    label89:
    label170:
    do
    {
      do
      {
        return;
        i = Arrays.asList(this.mPalette).indexOf(this.mColor);
        break;
        if ((this.mColor != null) && (!this.mColor.equals(this.mDefaultColor)) && (!TextUtils.isEmpty(this.mColor)) && (!isDefaultColor())) {
          break label227;
        }
        if (!OPUtils.isAndroidModeOn(this.mContext.getContentResolver())) {
          break label170;
        }
        setSummary(getContext().getString(2131690339));
      } while (this.mImageView == null);
      this.mImageView.setVisibility(8);
      return;
      if (!OPUtils.isStarWarModeOn(this.mContext.getContentResolver())) {
        break label213;
      }
      setSummary(getContext().getString(2131690549));
    } while (this.mImageView == null);
    this.mImageView.setVisibility(8);
    return;
    label213:
    setSummary(getContext().getString(2131690556));
    return;
    label227:
    setSummary(getContext().getString(2131690264));
  }
  
  public RippleDrawable createRippleDrawable(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {}
    for (int i = this.mDisabledCellColor;; i = Color.parseColor(paramString))
    {
      paramString = new ColorDrawable(i);
      int[] arrayOfInt = new int[0];
      i = this.mRippleEffectColor;
      return new RippleDrawable(new ColorStateList(new int[][] { arrayOfInt }, new int[] { i }), paramString, null);
    }
  }
  
  public String getColor()
  {
    if (this.mColor != null) {
      return this.mColor;
    }
    return getPersistedString(getDefaultColor());
  }
  
  public String getDefaultColor()
  {
    return this.mDefaultColor;
  }
  
  public void init()
  {
    this.mColor = getColor();
    this.mTmpColor = this.mColor;
    setSelection(this.mTmpColor, 0);
  }
  
  protected void onBindDialogView(View paramView)
  {
    super.onBindDialogView(paramView);
    onSetColorPalette(this.mPalette);
    updateSummary();
    int j = this.mPalette.length;
    if ((j != 0) && (j <= 4)) {
      paramView.findViewById(2131362424).setVisibility(8);
    }
    this.mViews = new View[this.mPalette.length];
    int i = 0;
    if (i < this.mPalette.length)
    {
      this.mViews[i] = paramView.findViewById(new int[] { 2131362416, 2131362418, 2131362420, 2131362422, 2131362425, 2131362427, 2131362429, 2131362431 }[i]);
      this.mViews[i].setBackground(createRippleDrawable(this.mPalette[i]));
      this.mViews[i].setTag(Integer.valueOf(i));
      View localView = this.mViews[i];
      if (j > 0) {}
      for (View.OnClickListener local1 = new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              int i = ((Integer)paramAnonymousView.getTag()).intValue();
              if (!ColorPickerPreference.-get0(ColorPickerPreference.this)[i].equals(ColorPickerPreference.-get1(ColorPickerPreference.this)))
              {
                ColorPickerPreference.this.setSelection(ColorPickerPreference.-get1(ColorPickerPreference.this), 8);
                ColorPickerPreference.-wrap0(ColorPickerPreference.this, ColorPickerPreference.-get0(ColorPickerPreference.this)[i]);
              }
            }
          };; local1 = null)
      {
        localView.setOnClickListener(local1);
        i += 1;
        break;
      }
    }
    this.mMessage = ((TextView)paramView.findViewById(2131361987));
    if (this.mMessageText != null) {
      setMessage(this.mMessageText);
    }
    for (;;)
    {
      init();
      return;
      setMessage(2131690263);
    }
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    this.mImageView = ((ImageView)paramPreferenceViewHolder.findViewById(2131362299));
    if (this.mImageView != null)
    {
      this.mImageView.getDrawable();
      if ((this.mColor != null) && (!TextUtils.isEmpty(this.mColor))) {
        break label86;
      }
    }
    label86:
    for (paramPreferenceViewHolder = this.mDefaultColor;; paramPreferenceViewHolder = this.mColor)
    {
      this.mImageView.setImageTintList(ColorStateList.valueOf(Color.parseColor(paramPreferenceViewHolder)));
      if (this.mVisibility) {
        this.mImageView.setVisibility(0);
      }
      return;
    }
  }
  
  protected void onDialogClosed(int paramInt)
  {
    boolean bool2 = true;
    boolean bool1 = true;
    super.onDialogClosed(paramInt);
    int i;
    if (paramInt == -1)
    {
      i = 1;
      if (paramInt != -3) {
        break label61;
      }
      paramInt = 1;
      label25:
      if (i == 0) {
        break label93;
      }
      if ((this.mColor != null) || (this.mTmpColor != null)) {
        break label66;
      }
      label43:
      if (!bool1) {
        setColor(this.mTmpColor);
      }
    }
    for (;;)
    {
      return;
      i = 0;
      break;
      label61:
      paramInt = 0;
      break label25;
      label66:
      if (this.mColor != null)
      {
        bool1 = this.mColor.equals(this.mTmpColor);
        break label43;
      }
      bool1 = false;
      break label43;
      label93:
      if (paramInt != 0)
      {
        if ((this.mColor == null) && (this.mDefaultColor == null)) {
          bool1 = bool2;
        }
        while (!bool1)
        {
          setColor(this.mDefaultColor);
          return;
          if (this.mColor != null) {
            bool1 = this.mColor.equals(this.mDefaultColor);
          } else {
            bool1 = false;
          }
        }
      }
    }
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if ((paramParcelable != null) && (paramParcelable.getClass().equals(SavedState.class)))
    {
      paramParcelable = (SavedState)paramParcelable;
      super.onRestoreInstanceState(paramParcelable.getSuperState());
      if ((!paramParcelable.tmpColor.equals(this.mTmpColor)) && (this.mViews != null))
      {
        setSelection(this.mTmpColor, 8);
        setTmpColor(paramParcelable.tmpColor);
      }
      return;
    }
    super.onRestoreInstanceState(paramParcelable);
  }
  
  protected Parcelable onSaveInstanceState()
  {
    Object localObject = super.onSaveInstanceState();
    if (this.mTmpColor == null) {
      return (Parcelable)localObject;
    }
    localObject = new SavedState((Parcelable)localObject);
    ((SavedState)localObject).tmpColor = this.mTmpColor;
    return (Parcelable)localObject;
  }
  
  protected void onSetColor(String paramString) {}
  
  protected void onSetColorPalette(String[] paramArrayOfString) {}
  
  protected void onSetInitialValue(boolean paramBoolean, Object paramObject)
  {
    if (paramBoolean) {}
    for (paramObject = getPersistedString(this.mDefaultColor);; paramObject = (String)paramObject)
    {
      setColor((String)paramObject);
      return;
    }
  }
  
  public void setColor(String paramString)
  {
    this.mColor = paramString;
    updateSummary();
    if (callChangeListener(this.mColor)) {
      onSetColor(this.mColor);
    }
    if (this.mImageView != null)
    {
      this.mImageView.getDrawable();
      if ((this.mColor != null) && (!TextUtils.isEmpty(this.mColor))) {
        break label86;
      }
    }
    label86:
    for (String str = this.mDefaultColor;; str = paramString)
    {
      this.mImageView.setImageTintList(ColorStateList.valueOf(Color.parseColor(str)));
      persistString(paramString);
      return;
    }
  }
  
  public void setColorPalette(String[] paramArrayOfString)
  {
    this.mPalette = paramArrayOfString;
  }
  
  public void setColorPalette(String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    this.mPalette = paramArrayOfString;
    this.mPaletteNamesResIds = paramArrayOfInt;
  }
  
  public void setDefaultColor(String paramString)
  {
    this.mDefaultColor = paramString;
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    super.setEnabled(paramBoolean);
  }
  
  public void setImageViewVisibility()
  {
    this.mVisibility = true;
  }
  
  public void setMessage(int paramInt)
  {
    this.mMessage.setText(getContext().getString(paramInt));
  }
  
  public void setMessage(CharSequence paramCharSequence)
  {
    this.mMessage.setText(paramCharSequence);
  }
  
  public void setMessageText(int paramInt)
  {
    this.mMessageText = getContext().getString(paramInt);
  }
  
  public void setMessageText(CharSequence paramCharSequence)
  {
    this.mMessageText = paramCharSequence;
  }
  
  public void setSelection(String paramString, int paramInt)
  {
    if (paramString == null) {
      return;
    }
    if (this.mPalette == null) {}
    for (int i = -1;; i = Arrays.asList(this.mPalette).indexOf(paramString))
    {
      if (i >= 0) {
        this.mViews[i].findViewById(new int[] { 2131362417, 2131362419, 2131362421, 2131362423, 2131362426, 2131362428, 2131362430, 2131362432 }[i]).setVisibility(paramInt);
      }
      return;
    }
  }
  
  private static class SavedState
    extends Preference.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public ColorPickerPreference.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ColorPickerPreference.SavedState(paramAnonymousParcel);
      }
      
      public ColorPickerPreference.SavedState[] newArray(int paramAnonymousInt)
      {
        return new ColorPickerPreference.SavedState[paramAnonymousInt];
      }
    };
    String tmpColor;
    
    public SavedState(Parcel paramParcel)
    {
      super();
      try
      {
        this.tmpColor = paramParcel.readString();
        return;
      }
      catch (Exception paramParcel)
      {
        paramParcel.printStackTrace();
      }
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeString(this.tmpColor);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ui\ColorPickerPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */