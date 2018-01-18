package com.android.settings;

import android.app.LauncherActivity;
import android.app.LauncherActivity.ListItem;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import com.oneplus.settings.utils.OPUtils;
import java.util.List;

public class CreateShortcut
  extends LauncherActivity
{
  private Bitmap createIcon(int paramInt)
  {
    View localView = LayoutInflater.from(new ContextThemeWrapper(this, 16974372)).inflate(2130969002, null);
    ((ImageView)localView.findViewById(16908294)).setImageResource(paramInt);
    paramInt = View.MeasureSpec.makeMeasureSpec(0, 0);
    localView.measure(paramInt, paramInt);
    Bitmap localBitmap = Bitmap.createBitmap(localView.getMeasuredWidth(), localView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
    Canvas localCanvas = new Canvas(localBitmap);
    localView.layout(0, 0, localView.getMeasuredWidth(), localView.getMeasuredHeight());
    localView.draw(localCanvas);
    return localBitmap;
  }
  
  protected Intent getTargetIntent()
  {
    Intent localIntent = new Intent("android.intent.action.MAIN", null);
    localIntent.addCategory("com.android.settings.SHORTCUT");
    localIntent.addFlags(268435456);
    return localIntent;
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (OPUtils.isWhiteModeOn(getContentResolver())) {
      getWindow().getDecorView().setSystemUiVisibility(8192);
    }
  }
  
  protected boolean onEvaluateShowIcons()
  {
    return false;
  }
  
  protected void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
  {
    paramView = intentForPosition(paramInt);
    paramView.setFlags(2097152);
    paramListView = new Intent();
    paramListView.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", Intent.ShortcutIconResource.fromContext(this, 2130903040));
    paramListView.putExtra("android.intent.extra.shortcut.INTENT", paramView);
    paramListView.putExtra("android.intent.extra.shortcut.NAME", itemForPosition(paramInt).label);
    paramView = itemForPosition(paramInt).resolveInfo.activityInfo;
    if (paramView.icon != 0) {
      paramListView.putExtra("android.intent.extra.shortcut.ICON", createIcon(paramView.icon));
    }
    setResult(-1, paramListView);
    finish();
  }
  
  protected List<ResolveInfo> onQueryPackageManager(Intent paramIntent)
  {
    paramIntent = getPackageManager().queryIntentActivities(paramIntent, 128);
    ConnectivityManager localConnectivityManager = (ConnectivityManager)getSystemService("connectivity");
    if (paramIntent == null) {
      return null;
    }
    int i = paramIntent.size() - 1;
    while (i >= 0)
    {
      if ((((ResolveInfo)paramIntent.get(i)).activityInfo.name.endsWith(Settings.TetherSettingsActivity.class.getSimpleName())) && (!localConnectivityManager.isTetheringSupported())) {
        paramIntent.remove(i);
      }
      i -= 1;
    }
    return paramIntent;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\CreateShortcut.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */