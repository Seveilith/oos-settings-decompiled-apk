package com.android.settings.users;

import android.app.Fragment;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.UserHandle;
import android.provider.ContactsContract.DisplayPhoto;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.android.settingslib.drawable.CircleFramedDrawable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class EditUserPhotoController
{
  private static final String CROP_PICTURE_FILE_NAME = "CropEditUserPhoto.jpg";
  private static final String NEW_USER_PHOTO_FILE_NAME = "NewUserPhoto.png";
  private static final int REQUEST_CODE_CHOOSE_PHOTO = 1001;
  private static final int REQUEST_CODE_CROP_PHOTO = 1003;
  private static final int REQUEST_CODE_TAKE_PHOTO = 1002;
  private static final String TAG = "EditUserPhotoController";
  private static final String TAKE_PICTURE_FILE_NAME = "TakeEditUserPhoto2.jpg";
  private final Context mContext;
  private final Uri mCropPictureUri;
  private final Fragment mFragment;
  private final ImageView mImageView;
  private Bitmap mNewUserPhotoBitmap;
  private Drawable mNewUserPhotoDrawable;
  private final int mPhotoSize;
  private final Uri mTakePictureUri;
  
  public EditUserPhotoController(Fragment paramFragment, ImageView paramImageView, Bitmap paramBitmap, Drawable paramDrawable, boolean paramBoolean)
  {
    this.mContext = paramImageView.getContext();
    this.mFragment = paramFragment;
    this.mImageView = paramImageView;
    paramFragment = this.mContext;
    boolean bool1;
    if (paramBoolean)
    {
      bool1 = false;
      this.mCropPictureUri = createTempImageUri(paramFragment, "CropEditUserPhoto.jpg", bool1);
      paramFragment = this.mContext;
      if (!paramBoolean) {
        break label122;
      }
    }
    label122:
    for (paramBoolean = bool2;; paramBoolean = true)
    {
      this.mTakePictureUri = createTempImageUri(paramFragment, "TakeEditUserPhoto2.jpg", paramBoolean);
      this.mPhotoSize = getPhotoSize(this.mContext);
      this.mImageView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          EditUserPhotoController.-wrap1(EditUserPhotoController.this);
        }
      });
      this.mNewUserPhotoBitmap = paramBitmap;
      this.mNewUserPhotoDrawable = paramDrawable;
      return;
      bool1 = true;
      break;
    }
  }
  
  private void appendCropExtras(Intent paramIntent)
  {
    paramIntent.putExtra("crop", "true");
    paramIntent.putExtra("scale", true);
    paramIntent.putExtra("scaleUpIfNeeded", true);
    paramIntent.putExtra("aspectX", 1);
    paramIntent.putExtra("aspectY", 1);
    paramIntent.putExtra("outputX", this.mPhotoSize);
    paramIntent.putExtra("outputY", this.mPhotoSize);
  }
  
  private void appendOutputExtra(Intent paramIntent, Uri paramUri)
  {
    paramIntent.putExtra("output", paramUri);
    paramIntent.addFlags(3);
    paramIntent.setClipData(ClipData.newRawUri("output", paramUri));
  }
  
  private boolean canChoosePhoto()
  {
    boolean bool = false;
    Intent localIntent = new Intent("android.intent.action.GET_CONTENT");
    localIntent.setType("image/*");
    if (this.mImageView.getContext().getPackageManager().queryIntentActivities(localIntent, 0).size() > 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean canTakePhoto()
  {
    boolean bool = false;
    if (this.mImageView.getContext().getPackageManager().queryIntentActivities(new Intent("android.media.action.IMAGE_CAPTURE"), 65536).size() > 0) {
      bool = true;
    }
    return bool;
  }
  
  private void choosePhoto()
  {
    Intent localIntent = new Intent("android.intent.action.GET_CONTENT", null);
    localIntent.setType("image/*");
    appendOutputExtra(localIntent, this.mTakePictureUri);
    this.mFragment.startActivityForResult(localIntent, 1001);
  }
  
  private Uri createTempImageUri(Context paramContext, String paramString, boolean paramBoolean)
  {
    File localFile = paramContext.getCacheDir();
    localFile.mkdirs();
    paramString = new File(localFile, paramString);
    if (paramBoolean) {
      paramString.delete();
    }
    return FileProvider.getUriForFile(paramContext, "com.android.settings.files", paramString);
  }
  
  private void cropPhoto(Uri paramUri)
  {
    Intent localIntent = new Intent("com.android.camera.action.CROP");
    localIntent.setDataAndType(paramUri, "image/*");
    appendOutputExtra(localIntent, this.mCropPictureUri);
    appendCropExtras(localIntent);
    if (localIntent.resolveActivity(this.mContext.getPackageManager()) != null) {
      try
      {
        StrictMode.disableDeathOnFileUriExposure();
        this.mFragment.startActivityForResult(localIntent, 1003);
        return;
      }
      finally
      {
        StrictMode.enableDeathOnFileUriExposure();
      }
    }
    onPhotoCropped(paramUri, false);
  }
  
  private static int getPhotoSize(Context paramContext)
  {
    paramContext = paramContext.getContentResolver().query(ContactsContract.DisplayPhoto.CONTENT_MAX_DIMENSIONS_URI, new String[] { "display_max_dim" }, null, null, null);
    try
    {
      paramContext.moveToFirst();
      int i = paramContext.getInt(0);
      return i;
    }
    finally
    {
      paramContext.close();
    }
  }
  
  static Bitmap loadNewUserPhotoBitmap(File paramFile)
  {
    return BitmapFactory.decodeFile(paramFile.getAbsolutePath());
  }
  
  private void onPhotoCropped(final Uri paramUri, final boolean paramBoolean)
  {
    new AsyncTask()
    {
      protected Bitmap doInBackground(Void... paramAnonymousVarArgs)
      {
        Object localObject;
        if (paramBoolean)
        {
          localObject = null;
          paramAnonymousVarArgs = null;
          try
          {
            InputStream localInputStream = EditUserPhotoController.-get0(EditUserPhotoController.this).getContentResolver().openInputStream(paramUri);
            paramAnonymousVarArgs = localInputStream;
            localObject = localInputStream;
            Bitmap localBitmap2 = BitmapFactory.decodeStream(localInputStream);
            if (localInputStream != null) {}
            try
            {
              localInputStream.close();
              return localBitmap2;
            }
            catch (IOException paramAnonymousVarArgs)
            {
              Log.w("EditUserPhotoController", "Cannot close image stream", paramAnonymousVarArgs);
              return localBitmap2;
            }
            try
            {
              ((InputStream)localObject).close();
              throw paramAnonymousVarArgs;
            }
            catch (IOException localIOException)
            {
              for (;;)
              {
                Log.w("EditUserPhotoController", "Cannot close image stream", localIOException);
              }
            }
          }
          catch (FileNotFoundException localFileNotFoundException)
          {
            localObject = paramAnonymousVarArgs;
            Log.w("EditUserPhotoController", "Cannot find image file", localFileNotFoundException);
            if (paramAnonymousVarArgs != null) {}
            try
            {
              paramAnonymousVarArgs.close();
              return null;
            }
            catch (IOException paramAnonymousVarArgs)
            {
              Log.w("EditUserPhotoController", "Cannot close image stream", paramAnonymousVarArgs);
              return null;
            }
          }
          finally
          {
            if (localObject == null) {}
          }
        }
        paramAnonymousVarArgs = Bitmap.createBitmap(EditUserPhotoController.-get4(EditUserPhotoController.this), EditUserPhotoController.-get4(EditUserPhotoController.this), Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(paramAnonymousVarArgs);
        try
        {
          Bitmap localBitmap1 = BitmapFactory.decodeStream(EditUserPhotoController.-get0(EditUserPhotoController.this).getContentResolver().openInputStream(paramUri));
          if (localBitmap1 != null)
          {
            int i = Math.min(localBitmap1.getWidth(), localBitmap1.getHeight());
            int j = (localBitmap1.getWidth() - i) / 2;
            int k = (localBitmap1.getHeight() - i) / 2;
            localCanvas.drawBitmap(localBitmap1, new Rect(j, k, j + i, k + i), new Rect(0, 0, EditUserPhotoController.-get4(EditUserPhotoController.this), EditUserPhotoController.-get4(EditUserPhotoController.this)), new Paint());
            return paramAnonymousVarArgs;
          }
        }
        catch (FileNotFoundException paramAnonymousVarArgs)
        {
          return null;
        }
        return null;
      }
      
      protected void onPostExecute(Bitmap paramAnonymousBitmap)
      {
        if (paramAnonymousBitmap != null)
        {
          EditUserPhotoController.-set0(EditUserPhotoController.this, paramAnonymousBitmap);
          EditUserPhotoController.-set1(EditUserPhotoController.this, CircleFramedDrawable.getInstance(EditUserPhotoController.-get1(EditUserPhotoController.this).getContext(), EditUserPhotoController.-get2(EditUserPhotoController.this)));
          EditUserPhotoController.-get1(EditUserPhotoController.this).setImageDrawable(EditUserPhotoController.-get3(EditUserPhotoController.this));
        }
        new File(EditUserPhotoController.-get0(EditUserPhotoController.this).getCacheDir(), "TakeEditUserPhoto2.jpg").delete();
        new File(EditUserPhotoController.-get0(EditUserPhotoController.this).getCacheDir(), "CropEditUserPhoto.jpg").delete();
      }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
  }
  
  private void showUpdatePhotoPopup()
  {
    boolean bool1 = canTakePhoto();
    boolean bool2 = canChoosePhoto();
    if ((bool1) || (bool2))
    {
      Context localContext = this.mImageView.getContext();
      ArrayList localArrayList = new ArrayList();
      if (bool1) {
        localArrayList.add(new RestrictedMenuItem(localContext, localContext.getString(2131693073), "no_set_user_icon", new Runnable()
        {
          public void run()
          {
            EditUserPhotoController.-wrap2(EditUserPhotoController.this);
          }
        }));
      }
      if (bool2) {
        localArrayList.add(new RestrictedMenuItem(localContext, localContext.getString(2131693074), "no_set_user_icon", new Runnable()
        {
          public void run()
          {
            EditUserPhotoController.-wrap0(EditUserPhotoController.this);
          }
        }));
      }
      final ListPopupWindow localListPopupWindow = new ListPopupWindow(localContext);
      localListPopupWindow.setAnchorView(this.mImageView);
      localListPopupWindow.setModal(true);
      localListPopupWindow.setInputMethodMode(2);
      localListPopupWindow.setAdapter(new RestrictedPopupMenuAdapter(localContext, localArrayList));
      localListPopupWindow.setWidth(Math.max(this.mImageView.getWidth(), localContext.getResources().getDimensionPixelSize(2131755493)));
      localListPopupWindow.setDropDownGravity(8388611);
      localListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          localListPopupWindow.dismiss();
          ((EditUserPhotoController.RestrictedMenuItem)paramAnonymousAdapterView.getAdapter().getItem(paramAnonymousInt)).doAction();
        }
      });
      localListPopupWindow.show();
      return;
    }
  }
  
  private void takePhoto()
  {
    Intent localIntent = new Intent("android.media.action.IMAGE_CAPTURE");
    appendOutputExtra(localIntent, this.mTakePictureUri);
    this.mFragment.startActivityForResult(localIntent, 1002);
  }
  
  public Bitmap getNewUserPhotoBitmap()
  {
    return this.mNewUserPhotoBitmap;
  }
  
  public Drawable getNewUserPhotoDrawable()
  {
    return this.mNewUserPhotoDrawable;
  }
  
  public boolean onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt2 != -1) {
      return false;
    }
    if ((paramIntent != null) && (paramIntent.getData() != null)) {}
    for (paramIntent = paramIntent.getData();; paramIntent = this.mTakePictureUri) {
      switch (paramInt1)
      {
      default: 
        return false;
      }
    }
    onPhotoCropped(paramIntent, true);
    return true;
    cropPhoto(paramIntent);
    return true;
  }
  
  void removeNewUserPhotoBitmapFile()
  {
    new File(this.mContext.getCacheDir(), "NewUserPhoto.png").delete();
  }
  
  File saveNewUserPhotoBitmap()
  {
    if (this.mNewUserPhotoBitmap == null) {
      return null;
    }
    try
    {
      File localFile = new File(this.mContext.getCacheDir(), "NewUserPhoto.png");
      FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
      this.mNewUserPhotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, localFileOutputStream);
      localFileOutputStream.flush();
      localFileOutputStream.close();
      return localFile;
    }
    catch (IOException localIOException)
    {
      Log.e("EditUserPhotoController", "Cannot create temp file", localIOException);
    }
    return null;
  }
  
  private static final class RestrictedMenuItem
  {
    private final Runnable mAction;
    private final RestrictedLockUtils.EnforcedAdmin mAdmin;
    private final Context mContext;
    private final boolean mIsRestrictedByBase;
    private final String mTitle;
    
    public RestrictedMenuItem(Context paramContext, String paramString1, String paramString2, Runnable paramRunnable)
    {
      this.mContext = paramContext;
      this.mTitle = paramString1;
      this.mAction = paramRunnable;
      int i = UserHandle.myUserId();
      this.mAdmin = RestrictedLockUtils.checkIfRestrictionEnforced(paramContext, paramString2, i);
      this.mIsRestrictedByBase = RestrictedLockUtils.hasBaseUserRestriction(this.mContext, paramString2, i);
    }
    
    final void doAction()
    {
      if (isRestrictedByBase()) {
        return;
      }
      if (isRestrictedByAdmin())
      {
        RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this.mContext, this.mAdmin);
        return;
      }
      this.mAction.run();
    }
    
    final boolean isRestrictedByAdmin()
    {
      return this.mAdmin != null;
    }
    
    final boolean isRestrictedByBase()
    {
      return this.mIsRestrictedByBase;
    }
    
    public String toString()
    {
      return this.mTitle;
    }
  }
  
  private static final class RestrictedPopupMenuAdapter
    extends ArrayAdapter<EditUserPhotoController.RestrictedMenuItem>
  {
    public RestrictedPopupMenuAdapter(Context paramContext, List<EditUserPhotoController.RestrictedMenuItem> paramList)
    {
      super(2130968953, 2131361995, paramList);
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      int i = 0;
      paramView = super.getView(paramInt, paramView, paramViewGroup);
      paramViewGroup = (EditUserPhotoController.RestrictedMenuItem)getItem(paramInt);
      TextView localTextView = (TextView)paramView.findViewById(2131361995);
      ImageView localImageView = (ImageView)paramView.findViewById(2131362509);
      if ((paramViewGroup.isRestrictedByAdmin()) || (paramViewGroup.isRestrictedByBase())) {}
      for (boolean bool = false;; bool = true)
      {
        localTextView.setEnabled(bool);
        if (paramViewGroup.isRestrictedByAdmin())
        {
          paramInt = i;
          if (!paramViewGroup.isRestrictedByBase()) {}
        }
        else
        {
          paramInt = 8;
        }
        localImageView.setVisibility(paramInt);
        return paramView;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\users\EditUserPhotoController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */