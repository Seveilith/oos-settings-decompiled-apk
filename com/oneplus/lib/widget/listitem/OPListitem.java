package com.oneplus.lib.widget.listitem;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class OPListitem
  extends ViewGroup
{
  public OPListitem(Context paramContext)
  {
    super(paramContext);
  }
  
  public abstract ImageView getActionButton();
  
  public abstract ImageView getIcon();
  
  public abstract TextView getPrimaryText();
  
  public abstract TextView getSecondaryText();
  
  public abstract TextView getStamp();
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\listitem\OPListitem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */