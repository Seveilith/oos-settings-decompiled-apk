package com.oneplus.lib.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import com.oneplus.commonctrl.R.attr;
import com.oneplus.commonctrl.R.drawable;
import com.oneplus.commonctrl.R.style;
import com.oneplus.commonctrl.R.styleable;
import com.oneplus.lib.widget.util.utils;

public class OPEditText
  extends EditText
{
  static final String TAG = "OPListView";
  private Drawable mBackground = null;
  private Context mContext = null;
  private Drawable mErrorBackground = null;
  
  public OPEditText(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public OPEditText(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.OPEditTextStyle);
  }
  
  public OPEditText(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, R.style.Oneplus_DeviceDefault_Widget_Material_EditText);
  }
  
  public OPEditText(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, utils.resolveDefStyleAttr(paramContext, paramInt1), paramInt2);
    init(paramContext, paramAttributeSet);
  }
  
  private Drawable getETBackground()
  {
    return getResources().getDrawable(R.drawable.op_edit_text_material_light, this.mContext.getTheme());
  }
  
  private Drawable getETErrBackground()
  {
    return getResources().getDrawable(R.drawable.op_edit_text_error_material_light, this.mContext.getTheme());
  }
  
  private void init(Context paramContext, AttributeSet paramAttributeSet)
  {
    Log.i("OPListView", "OPEditText init");
    this.mContext = paramContext;
    paramContext = this.mContext.obtainStyledAttributes(paramAttributeSet, R.styleable.OPEditText, R.attr.OPEditTextStyle, R.style.Oneplus_DeviceDefault_Widget_Material_EditText);
    this.mBackground = paramContext.getDrawable(R.styleable.OPEditText_android_background);
    this.mErrorBackground = paramContext.getDrawable(R.styleable.OPEditText_colorError);
    paramContext.recycle();
    if (this.mBackground == null)
    {
      this.mBackground = getETBackground();
      this.mErrorBackground = getETErrBackground();
    }
  }
  
  public void setError(CharSequence paramCharSequence)
  {
    super.setError(paramCharSequence);
    Log.i("OPListView", "OPEditText setError");
    if (paramCharSequence != null)
    {
      setBackground(this.mErrorBackground);
      return;
    }
    setBackground(this.mBackground);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\OPEditText.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */