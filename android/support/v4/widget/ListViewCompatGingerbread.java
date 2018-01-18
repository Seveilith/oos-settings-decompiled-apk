package android.support.v4.widget;

import android.view.View;
import android.widget.ListView;

class ListViewCompatGingerbread
{
  static void scrollListBy(ListView paramListView, int paramInt)
  {
    int i = paramListView.getFirstVisiblePosition();
    if (i == -1) {
      return;
    }
    View localView = paramListView.getChildAt(0);
    if (localView == null) {
      return;
    }
    paramListView.setSelectionFromTop(i, localView.getTop() - paramInt);
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\android\support\v4\widget\ListViewCompatGingerbread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */