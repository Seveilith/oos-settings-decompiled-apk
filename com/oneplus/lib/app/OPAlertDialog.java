package com.oneplus.lib.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

public class OPAlertDialog
  extends Dialog
  implements DialogInterface
{
  private OPAlertController mAlert;
  protected Context mContext;
  
  protected OPAlertDialog(Context paramContext)
  {
    this(paramContext, resolveDialogTheme(paramContext, 0), true);
  }
  
  protected OPAlertDialog(Context paramContext, int paramInt)
  {
    this(paramContext, paramInt, true);
  }
  
  OPAlertDialog(Context paramContext, int paramInt, boolean paramBoolean)
  {
    super(paramContext, resolveDialogTheme(paramContext, paramInt));
    this.mAlert = new OPAlertController(getContext(), this, getWindow());
    this.mContext = paramContext;
  }
  
  protected OPAlertDialog(Context paramContext, boolean paramBoolean, DialogInterface.OnCancelListener paramOnCancelListener)
  {
    super(paramContext, resolveDialogTheme(paramContext, 0));
    setCancelable(paramBoolean);
    setOnCancelListener(paramOnCancelListener);
    this.mAlert = new OPAlertController(paramContext, this, getWindow());
    this.mContext = paramContext;
  }
  
  static int resolveDialogTheme(Context paramContext, int paramInt)
  {
    if (paramInt >= 16777216) {
      return paramInt;
    }
    TypedValue localTypedValue = new TypedValue();
    paramContext.getTheme().resolveAttribute(16843529, localTypedValue, true);
    return localTypedValue.resourceId;
  }
  
  public Button getButton(int paramInt)
  {
    return this.mAlert.getButton(paramInt);
  }
  
  public ListView getListView()
  {
    return this.mAlert.getListView();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mAlert.installContent();
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (this.mAlert.onKeyDown(paramInt, paramKeyEvent)) {
      return true;
    }
    return super.onKeyDown(paramInt, paramKeyEvent);
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    if (this.mAlert.onKeyUp(paramInt, paramKeyEvent)) {
      return true;
    }
    return super.onKeyUp(paramInt, paramKeyEvent);
  }
  
  public void setButton(int paramInt, CharSequence paramCharSequence, DialogInterface.OnClickListener paramOnClickListener)
  {
    this.mAlert.setButton(paramInt, paramCharSequence, paramOnClickListener, null);
  }
  
  public void setButton(int paramInt, CharSequence paramCharSequence, Message paramMessage)
  {
    this.mAlert.setButton(paramInt, paramCharSequence, null, paramMessage);
  }
  
  @Deprecated
  public void setButton(CharSequence paramCharSequence, DialogInterface.OnClickListener paramOnClickListener)
  {
    setButton(-1, paramCharSequence, paramOnClickListener);
  }
  
  @Deprecated
  public void setButton(CharSequence paramCharSequence, Message paramMessage)
  {
    setButton(-1, paramCharSequence, paramMessage);
  }
  
  @Deprecated
  public void setButton2(CharSequence paramCharSequence, DialogInterface.OnClickListener paramOnClickListener)
  {
    setButton(-2, paramCharSequence, paramOnClickListener);
  }
  
  @Deprecated
  public void setButton2(CharSequence paramCharSequence, Message paramMessage)
  {
    setButton(-2, paramCharSequence, paramMessage);
  }
  
  @Deprecated
  public void setButton3(CharSequence paramCharSequence, DialogInterface.OnClickListener paramOnClickListener)
  {
    setButton(-3, paramCharSequence, paramOnClickListener);
  }
  
  @Deprecated
  public void setButton3(CharSequence paramCharSequence, Message paramMessage)
  {
    setButton(-3, paramCharSequence, paramMessage);
  }
  
  public void setCustomTitle(View paramView)
  {
    this.mAlert.setCustomTitle(paramView);
  }
  
  public void setIcon(int paramInt)
  {
    this.mAlert.setIcon(paramInt);
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    this.mAlert.setIcon(paramDrawable);
  }
  
  public void setIconAttribute(int paramInt)
  {
    TypedValue localTypedValue = new TypedValue();
    this.mContext.getTheme().resolveAttribute(paramInt, localTypedValue, true);
    this.mAlert.setIcon(localTypedValue.resourceId);
  }
  
  public void setInverseBackgroundForced(boolean paramBoolean)
  {
    this.mAlert.setInverseBackgroundForced(paramBoolean);
  }
  
  public void setMessage(CharSequence paramCharSequence)
  {
    this.mAlert.setMessage(paramCharSequence);
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    super.setTitle(paramCharSequence);
    this.mAlert.setTitle(paramCharSequence);
  }
  
  public void setView(View paramView)
  {
    this.mAlert.setView(paramView);
  }
  
  public void setView(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mAlert.setView(paramView, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public static class Builder
  {
    private final OPAlertController.AlertParams P;
    
    public Builder(Context paramContext)
    {
      this.P = new OPAlertController.AlertParams(paramContext);
    }
    
    public Builder(Context paramContext, int paramInt)
    {
      this.P = new OPAlertController.AlertParams(new ContextThemeWrapper(paramContext, OPAlertDialog.resolveDialogTheme(paramContext, paramInt)));
    }
    
    public OPAlertDialog create()
    {
      OPAlertDialog localOPAlertDialog = new OPAlertDialog(this.P.mContext);
      this.P.apply(OPAlertDialog.-get0(localOPAlertDialog));
      localOPAlertDialog.setCancelable(this.P.mCancelable);
      if (this.P.mCancelable) {
        localOPAlertDialog.setCanceledOnTouchOutside(true);
      }
      localOPAlertDialog.setOnCancelListener(this.P.mOnCancelListener);
      localOPAlertDialog.setOnDismissListener(this.P.mOnDismissListener);
      if (this.P.mOnKeyListener != null) {
        localOPAlertDialog.setOnKeyListener(this.P.mOnKeyListener);
      }
      return localOPAlertDialog;
    }
    
    public Context getContext()
    {
      return this.P.mContext;
    }
    
    public Builder setAdapter(ListAdapter paramListAdapter, DialogInterface.OnClickListener paramOnClickListener)
    {
      this.P.mAdapter = paramListAdapter;
      this.P.mOnClickListener = paramOnClickListener;
      return this;
    }
    
    public Builder setCancelable(boolean paramBoolean)
    {
      this.P.mCancelable = paramBoolean;
      return this;
    }
    
    public Builder setCursor(Cursor paramCursor, DialogInterface.OnClickListener paramOnClickListener, String paramString)
    {
      this.P.mCursor = paramCursor;
      this.P.mLabelColumn = paramString;
      this.P.mOnClickListener = paramOnClickListener;
      return this;
    }
    
    public Builder setCustomTitle(View paramView)
    {
      this.P.mCustomTitleView = paramView;
      return this;
    }
    
    public Builder setIcon(int paramInt)
    {
      this.P.mIconId = paramInt;
      return this;
    }
    
    public Builder setIcon(Drawable paramDrawable)
    {
      this.P.mIcon = paramDrawable;
      return this;
    }
    
    public Builder setIconAttribute(int paramInt)
    {
      TypedValue localTypedValue = new TypedValue();
      this.P.mContext.getTheme().resolveAttribute(paramInt, localTypedValue, true);
      this.P.mIconId = localTypedValue.resourceId;
      return this;
    }
    
    @Deprecated
    public Builder setInverseBackgroundForced(boolean paramBoolean)
    {
      this.P.mForceInverseBackground = paramBoolean;
      return this;
    }
    
    public Builder setItems(int paramInt, DialogInterface.OnClickListener paramOnClickListener)
    {
      this.P.mItems = this.P.mContext.getResources().getTextArray(paramInt);
      this.P.mOnClickListener = paramOnClickListener;
      return this;
    }
    
    public Builder setItems(CharSequence[] paramArrayOfCharSequence, DialogInterface.OnClickListener paramOnClickListener)
    {
      this.P.mItems = paramArrayOfCharSequence;
      this.P.mOnClickListener = paramOnClickListener;
      return this;
    }
    
    public Builder setMessage(int paramInt)
    {
      this.P.mMessage = this.P.mContext.getText(paramInt);
      return this;
    }
    
    public Builder setMessage(CharSequence paramCharSequence)
    {
      this.P.mMessage = paramCharSequence;
      return this;
    }
    
    public Builder setMultiChoiceItems(int paramInt, boolean[] paramArrayOfBoolean, DialogInterface.OnMultiChoiceClickListener paramOnMultiChoiceClickListener)
    {
      this.P.mItems = this.P.mContext.getResources().getTextArray(paramInt);
      this.P.mOnCheckboxClickListener = paramOnMultiChoiceClickListener;
      this.P.mCheckedItems = paramArrayOfBoolean;
      this.P.mIsMultiChoice = true;
      return this;
    }
    
    public Builder setMultiChoiceItems(Cursor paramCursor, String paramString1, String paramString2, DialogInterface.OnMultiChoiceClickListener paramOnMultiChoiceClickListener)
    {
      this.P.mCursor = paramCursor;
      this.P.mOnCheckboxClickListener = paramOnMultiChoiceClickListener;
      this.P.mIsCheckedColumn = paramString1;
      this.P.mLabelColumn = paramString2;
      this.P.mIsMultiChoice = true;
      return this;
    }
    
    public Builder setMultiChoiceItems(CharSequence[] paramArrayOfCharSequence, boolean[] paramArrayOfBoolean, DialogInterface.OnMultiChoiceClickListener paramOnMultiChoiceClickListener)
    {
      this.P.mItems = paramArrayOfCharSequence;
      this.P.mOnCheckboxClickListener = paramOnMultiChoiceClickListener;
      this.P.mCheckedItems = paramArrayOfBoolean;
      this.P.mIsMultiChoice = true;
      return this;
    }
    
    public Builder setNegativeButton(int paramInt, DialogInterface.OnClickListener paramOnClickListener)
    {
      this.P.mNegativeButtonText = this.P.mContext.getText(paramInt);
      this.P.mNegativeButtonListener = paramOnClickListener;
      return this;
    }
    
    public Builder setNegativeButton(CharSequence paramCharSequence, DialogInterface.OnClickListener paramOnClickListener)
    {
      this.P.mNegativeButtonText = paramCharSequence;
      this.P.mNegativeButtonListener = paramOnClickListener;
      return this;
    }
    
    public Builder setNeutralButton(int paramInt, DialogInterface.OnClickListener paramOnClickListener)
    {
      this.P.mNeutralButtonText = this.P.mContext.getText(paramInt);
      this.P.mNeutralButtonListener = paramOnClickListener;
      return this;
    }
    
    public Builder setNeutralButton(CharSequence paramCharSequence, DialogInterface.OnClickListener paramOnClickListener)
    {
      this.P.mNeutralButtonText = paramCharSequence;
      this.P.mNeutralButtonListener = paramOnClickListener;
      return this;
    }
    
    public Builder setOnCancelListener(DialogInterface.OnCancelListener paramOnCancelListener)
    {
      this.P.mOnCancelListener = paramOnCancelListener;
      return this;
    }
    
    public Builder setOnDismissListener(DialogInterface.OnDismissListener paramOnDismissListener)
    {
      this.P.mOnDismissListener = paramOnDismissListener;
      return this;
    }
    
    public Builder setOnItemSelectedListener(AdapterView.OnItemSelectedListener paramOnItemSelectedListener)
    {
      this.P.mOnItemSelectedListener = paramOnItemSelectedListener;
      return this;
    }
    
    public Builder setOnKeyListener(DialogInterface.OnKeyListener paramOnKeyListener)
    {
      this.P.mOnKeyListener = paramOnKeyListener;
      return this;
    }
    
    public Builder setPositiveButton(int paramInt, DialogInterface.OnClickListener paramOnClickListener)
    {
      this.P.mPositiveButtonText = this.P.mContext.getText(paramInt);
      this.P.mPositiveButtonListener = paramOnClickListener;
      return this;
    }
    
    public Builder setPositiveButton(CharSequence paramCharSequence, DialogInterface.OnClickListener paramOnClickListener)
    {
      this.P.mPositiveButtonText = paramCharSequence;
      this.P.mPositiveButtonListener = paramOnClickListener;
      return this;
    }
    
    public Builder setRecycleOnMeasureEnabled(boolean paramBoolean)
    {
      this.P.mRecycleOnMeasure = paramBoolean;
      return this;
    }
    
    public Builder setSingleChoiceItems(int paramInt1, int paramInt2, DialogInterface.OnClickListener paramOnClickListener)
    {
      this.P.mItems = this.P.mContext.getResources().getTextArray(paramInt1);
      this.P.mOnClickListener = paramOnClickListener;
      this.P.mCheckedItem = paramInt2;
      this.P.mIsSingleChoice = true;
      return this;
    }
    
    public Builder setSingleChoiceItems(Cursor paramCursor, int paramInt, String paramString, DialogInterface.OnClickListener paramOnClickListener)
    {
      this.P.mCursor = paramCursor;
      this.P.mOnClickListener = paramOnClickListener;
      this.P.mCheckedItem = paramInt;
      this.P.mLabelColumn = paramString;
      this.P.mIsSingleChoice = true;
      return this;
    }
    
    public Builder setSingleChoiceItems(ListAdapter paramListAdapter, int paramInt, DialogInterface.OnClickListener paramOnClickListener)
    {
      this.P.mAdapter = paramListAdapter;
      this.P.mOnClickListener = paramOnClickListener;
      this.P.mCheckedItem = paramInt;
      this.P.mIsSingleChoice = true;
      return this;
    }
    
    public Builder setSingleChoiceItems(CharSequence[] paramArrayOfCharSequence, int paramInt, DialogInterface.OnClickListener paramOnClickListener)
    {
      this.P.mItems = paramArrayOfCharSequence;
      this.P.mOnClickListener = paramOnClickListener;
      this.P.mCheckedItem = paramInt;
      this.P.mIsSingleChoice = true;
      return this;
    }
    
    public Builder setTitle(int paramInt)
    {
      this.P.mTitle = this.P.mContext.getText(paramInt);
      return this;
    }
    
    public Builder setTitle(CharSequence paramCharSequence)
    {
      this.P.mTitle = paramCharSequence;
      return this;
    }
    
    public Builder setView(int paramInt)
    {
      this.P.mView = null;
      this.P.mViewLayoutResId = paramInt;
      this.P.mViewSpacingSpecified = false;
      return this;
    }
    
    public Builder setView(View paramView)
    {
      this.P.mView = paramView;
      this.P.mViewLayoutResId = 0;
      this.P.mViewSpacingSpecified = false;
      return this;
    }
    
    @Deprecated
    public Builder setView(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.P.mView = paramView;
      this.P.mViewLayoutResId = 0;
      this.P.mViewSpacingSpecified = true;
      this.P.mViewSpacingLeft = paramInt1;
      this.P.mViewSpacingTop = paramInt2;
      this.P.mViewSpacingRight = paramInt3;
      this.P.mViewSpacingBottom = paramInt4;
      return this;
    }
    
    public OPAlertDialog show()
    {
      OPAlertDialog localOPAlertDialog = create();
      localOPAlertDialog.show();
      return localOPAlertDialog;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\app\OPAlertDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */