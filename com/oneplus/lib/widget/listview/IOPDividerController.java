package com.oneplus.lib.widget.listview;

public abstract interface IOPDividerController
{
  public static final int DIVIDER_TYPE_NONE = 0;
  public static final int DIVIDER_TYPE_NORAML = 1;
  public static final int DIVIDER_TYPE_PADDING = 2;
  
  public abstract int getDividerType(int paramInt);
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\widget\listview\IOPDividerController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */