package com.oneplus.settings.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settingslib.RestrictedPreference;
import com.oneplus.settings.utils.OPUtils;

public class OPButtonPreference
  extends RestrictedPreference
{
  private boolean mButtonEnable;
  private String mButtonString;
  private boolean mButtonVisible;
  private Context mContext;
  private Drawable mIcon;
  private ImageView mLeftIcon;
  private View.OnClickListener mOnClickListener;
  private TextView mRightButton;
  private ColorStateList mTextButtonColor;
  private TextView mTextSummary;
  private String mTextSummaryString;
  private boolean mTextSummaryVisible;
  private TextView mTextTitle;
  private String mTextTitleString;
  private int resid = 2130968792;
  
  public OPButtonPreference(Context paramContext)
  {
    super(paramContext);
    initViews(paramContext);
  }
  
  public OPButtonPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initViews(paramContext);
  }
  
  public OPButtonPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initViews(paramContext);
  }
  
  private void initViews(Context paramContext)
  {
    this.mContext = paramContext;
    setLayoutResource(this.resid);
    this.mTextTitleString = "";
    this.mTextSummaryString = "";
    this.mButtonString = "";
    this.mIcon = null;
    this.mButtonEnable = false;
    this.mButtonVisible = true;
    this.mTextButtonColor = OPUtils.creatOneplusPrimaryColorStateList(this.mContext);
  }
  
  public String getButtonString()
  {
    return this.mButtonString;
  }
  
  public Drawable getIcon()
  {
    return this.mIcon;
  }
  
  public TextView getLeftButton()
  {
    return this.mRightButton;
  }
  
  public String getLeftTextSummary()
  {
    return this.mTextSummaryString;
  }
  
  public String getLeftTextTitle()
  {
    return this.mTextTitleString;
  }
  
  public CharSequence getSummary()
  {
    return this.mTextSummaryString;
  }
  
  public CharSequence getTitle()
  {
    return this.mTextTitleString;
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    int j = 0;
    super.onBindViewHolder(paramPreferenceViewHolder);
    this.mLeftIcon = ((ImageView)paramPreferenceViewHolder.findViewById(2131362277));
    this.mRightButton = ((TextView)paramPreferenceViewHolder.findViewById(2131362282));
    TextView localTextView = this.mRightButton;
    if (this.mButtonVisible)
    {
      i = 0;
      localTextView.setVisibility(i);
      this.mRightButton.setTextColor(this.mTextButtonColor);
      this.mRightButton.setOnClickListener(this.mOnClickListener);
      this.mRightButton.setEnabled(this.mButtonEnable);
      this.mRightButton.setText(this.mButtonString);
      this.mTextTitle = ((TextView)paramPreferenceViewHolder.findViewById(2131362279));
      this.mTextTitle.setText(this.mTextTitleString);
      this.mTextSummary = ((TextView)paramPreferenceViewHolder.findViewById(2131362280));
      paramPreferenceViewHolder = this.mTextSummary;
      if (!this.mTextSummaryVisible) {
        break label190;
      }
    }
    label190:
    for (int i = j;; i = 8)
    {
      paramPreferenceViewHolder.setVisibility(i);
      this.mTextSummary.setText(this.mTextSummaryString);
      if (this.mIcon != null) {
        this.mLeftIcon.setImageDrawable(this.mIcon);
      }
      return;
      i = 8;
      break;
    }
  }
  
  public void setButtonEnable(boolean paramBoolean)
  {
    this.mButtonEnable = paramBoolean;
    notifyChanged();
  }
  
  public void setButtonString(String paramString)
  {
    this.mButtonString = paramString;
    notifyChanged();
  }
  
  public void setButtonVisible(boolean paramBoolean)
  {
    this.mButtonVisible = paramBoolean;
    notifyChanged();
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    this.mIcon = paramDrawable;
    notifyChanged();
  }
  
  public void setLeftTextSummary(String paramString)
  {
    this.mTextSummaryString = paramString;
    if (!TextUtils.isEmpty(this.mTextSummaryString)) {}
    for (this.mTextSummaryVisible = true;; this.mTextSummaryVisible = false)
    {
      notifyChanged();
      return;
    }
  }
  
  public void setLeftTextTitle(String paramString)
  {
    this.mTextTitleString = paramString;
    notifyChanged();
  }
  
  public void setOnButtonClickListener(View.OnClickListener paramOnClickListener)
  {
    this.mOnClickListener = paramOnClickListener;
  }
  
  public void setSummary(CharSequence paramCharSequence)
  {
    setLeftTextSummary(paramCharSequence.toString());
  }
  
  public void setSummaryVisibility(boolean paramBoolean)
  {
    this.mTextSummaryVisible = paramBoolean;
    notifyChanged();
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    setLeftTextTitle(paramCharSequence.toString());
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ui\OPButtonPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */