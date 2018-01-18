package com.android.settingslib.drawable;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.util.DisplayMetrics;
import com.android.settingslib.R.dimen;

public class UserIconDrawable
  extends Drawable
  implements Drawable.Callback
{
  private Drawable mBadge;
  private float mBadgeMargin;
  private float mBadgeRadius;
  private Bitmap mBitmap;
  private Paint mClearPaint;
  private float mDisplayRadius;
  private ColorStateList mFrameColor = null;
  private float mFramePadding;
  private Paint mFramePaint;
  private float mFrameWidth;
  private final Matrix mIconMatrix = new Matrix();
  private final Paint mIconPaint = new Paint();
  private float mIntrinsicRadius;
  private boolean mInvalidated = true;
  private float mPadding = 0.0F;
  private final Paint mPaint = new Paint();
  private int mSize = 0;
  private ColorStateList mTintColor = null;
  private PorterDuff.Mode mTintMode = PorterDuff.Mode.SRC_ATOP;
  private Drawable mUserDrawable;
  private Bitmap mUserIcon;
  
  public UserIconDrawable()
  {
    this(0);
  }
  
  public UserIconDrawable(int paramInt)
  {
    this.mIconPaint.setAntiAlias(true);
    this.mIconPaint.setFilterBitmap(true);
    this.mPaint.setFilterBitmap(true);
    this.mPaint.setAntiAlias(true);
    if (paramInt > 0)
    {
      setBounds(0, 0, paramInt, paramInt);
      setIntrinsicSize(paramInt);
    }
    setIcon(null);
  }
  
  public static Drawable getManagedUserBadgeDrawable(Context paramContext)
  {
    int i = paramContext.getResources().getDisplayMetrics().densityDpi;
    return paramContext.getResources().getDrawableForDensity(17302317, i, paramContext.getTheme());
  }
  
  public static int getSizeForList(Context paramContext)
  {
    return (int)paramContext.getResources().getDimension(R.dimen.circle_avatar_size);
  }
  
  private void initFramePaint()
  {
    if (this.mFramePaint == null)
    {
      this.mFramePaint = new Paint();
      this.mFramePaint.setStyle(Paint.Style.STROKE);
      this.mFramePaint.setAntiAlias(true);
    }
  }
  
  private void rebake()
  {
    this.mInvalidated = false;
    if ((this.mBitmap == null) || ((this.mUserDrawable == null) && (this.mUserIcon == null))) {
      return;
    }
    Canvas localCanvas = new Canvas(this.mBitmap);
    localCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
    if (this.mUserDrawable != null) {
      this.mUserDrawable.draw(localCanvas);
    }
    for (;;)
    {
      if (this.mFrameColor != null) {
        this.mFramePaint.setColor(this.mFrameColor.getColorForState(getState(), 0));
      }
      float f1;
      float f2;
      float f3;
      if (this.mFrameWidth + this.mFramePadding > 0.001F)
      {
        f1 = this.mDisplayRadius;
        f2 = this.mPadding;
        f3 = this.mFrameWidth;
        localCanvas.drawCircle(getBounds().exactCenterX(), getBounds().exactCenterY(), f1 - f2 - f3 * 0.5F, this.mFramePaint);
      }
      if ((this.mBadge != null) && (this.mBadgeRadius > 0.001F))
      {
        f3 = this.mBadgeRadius * 2.0F;
        f1 = this.mBitmap.getHeight() - f3;
        f2 = this.mBitmap.getWidth() - f3;
        this.mBadge.setBounds((int)f2, (int)f1, (int)(f2 + f3), (int)(f1 + f3));
        f3 = this.mBadge.getBounds().width();
        float f4 = this.mBadgeMargin;
        localCanvas.drawCircle(this.mBadgeRadius + f2, this.mBadgeRadius + f1, f3 * 0.5F + f4, this.mClearPaint);
        this.mBadge.draw(localCanvas);
      }
      return;
      if (this.mUserIcon != null)
      {
        int i = localCanvas.save();
        localCanvas.concat(this.mIconMatrix);
        localCanvas.drawCircle(this.mUserIcon.getWidth() * 0.5F, this.mUserIcon.getHeight() * 0.5F, this.mIntrinsicRadius, this.mIconPaint);
        localCanvas.restoreToCount(i);
      }
    }
  }
  
  public UserIconDrawable bake()
  {
    if (this.mSize <= 0) {
      throw new IllegalStateException("Baking requires an explicit intrinsic size");
    }
    onBoundsChange(new Rect(0, 0, this.mSize, this.mSize));
    rebake();
    this.mFrameColor = null;
    this.mFramePaint = null;
    this.mClearPaint = null;
    if (this.mUserDrawable != null)
    {
      this.mUserDrawable.setCallback(null);
      this.mUserDrawable = null;
    }
    while (this.mUserIcon == null) {
      return this;
    }
    this.mUserIcon.recycle();
    this.mUserIcon = null;
    return this;
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (this.mInvalidated) {
      rebake();
    }
    if (this.mBitmap != null)
    {
      if (this.mTintColor != null) {
        break label49;
      }
      this.mPaint.setColorFilter(null);
    }
    for (;;)
    {
      paramCanvas.drawBitmap(this.mBitmap, 0.0F, 0.0F, this.mPaint);
      return;
      label49:
      int i = this.mTintColor.getColorForState(getState(), this.mTintColor.getDefaultColor());
      if (this.mPaint.getColorFilter() == null)
      {
        this.mPaint.setColorFilter(new PorterDuffColorFilter(i, this.mTintMode));
      }
      else
      {
        ((PorterDuffColorFilter)this.mPaint.getColorFilter()).setMode(this.mTintMode);
        ((PorterDuffColorFilter)this.mPaint.getColorFilter()).setColor(i);
      }
    }
  }
  
  public int getIntrinsicHeight()
  {
    return getIntrinsicWidth();
  }
  
  public int getIntrinsicWidth()
  {
    if (this.mSize <= 0) {
      return (int)this.mIntrinsicRadius * 2;
    }
    return this.mSize;
  }
  
  public int getOpacity()
  {
    return -3;
  }
  
  public void invalidateDrawable(Drawable paramDrawable)
  {
    invalidateSelf();
  }
  
  public void invalidateSelf()
  {
    super.invalidateSelf();
    this.mInvalidated = true;
  }
  
  public boolean isStateful()
  {
    if (this.mFrameColor != null) {
      return this.mFrameColor.isStateful();
    }
    return false;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    if ((paramRect.isEmpty()) || ((this.mUserIcon == null) && (this.mUserDrawable == null))) {
      return;
    }
    float f1 = Math.min(paramRect.width(), paramRect.height()) * 0.5F;
    int i = (int)(f1 * 2.0F);
    if ((this.mBitmap == null) || (i != (int)(this.mDisplayRadius * 2.0F)))
    {
      this.mDisplayRadius = f1;
      if (this.mBitmap != null) {
        this.mBitmap.recycle();
      }
      this.mBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
    }
    this.mDisplayRadius = (Math.min(paramRect.width(), paramRect.height()) * 0.5F);
    f1 = this.mDisplayRadius - this.mFrameWidth - this.mFramePadding - this.mPadding;
    paramRect = new RectF(paramRect.exactCenterX() - f1, paramRect.exactCenterY() - f1, paramRect.exactCenterX() + f1, paramRect.exactCenterY() + f1);
    Object localObject;
    if (this.mUserDrawable != null)
    {
      localObject = new Rect();
      paramRect.round((Rect)localObject);
      this.mIntrinsicRadius = (Math.min(this.mUserDrawable.getIntrinsicWidth(), this.mUserDrawable.getIntrinsicHeight()) * 0.5F);
      this.mUserDrawable.setBounds((Rect)localObject);
    }
    for (;;)
    {
      invalidateSelf();
      return;
      if (this.mUserIcon != null)
      {
        f1 = this.mUserIcon.getWidth() * 0.5F;
        float f2 = this.mUserIcon.getHeight() * 0.5F;
        this.mIntrinsicRadius = Math.min(f1, f2);
        localObject = new RectF(f1 - this.mIntrinsicRadius, f2 - this.mIntrinsicRadius, this.mIntrinsicRadius + f1, this.mIntrinsicRadius + f2);
        this.mIconMatrix.setRectToRect((RectF)localObject, paramRect, Matrix.ScaleToFit.FILL);
      }
    }
  }
  
  public void scheduleDrawable(Drawable paramDrawable, Runnable paramRunnable, long paramLong)
  {
    scheduleSelf(paramRunnable, paramLong);
  }
  
  public void setAlpha(int paramInt)
  {
    this.mPaint.setAlpha(paramInt);
    super.invalidateSelf();
  }
  
  public UserIconDrawable setBadge(Drawable paramDrawable)
  {
    this.mBadge = paramDrawable;
    if (this.mBadge != null)
    {
      if (this.mClearPaint == null)
      {
        this.mClearPaint = new Paint();
        this.mClearPaint.setAntiAlias(true);
        this.mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        this.mClearPaint.setStyle(Paint.Style.FILL);
      }
      onBoundsChange(getBounds());
      return this;
    }
    invalidateSelf();
    return this;
  }
  
  public UserIconDrawable setBadgeIfManagedUser(Context paramContext, int paramInt)
  {
    Drawable localDrawable = null;
    if (((DevicePolicyManager)paramContext.getSystemService(DevicePolicyManager.class)).getProfileOwnerAsUser(paramInt) != null) {}
    for (paramInt = 1;; paramInt = 0)
    {
      if (paramInt != 0) {
        localDrawable = getManagedUserBadgeDrawable(paramContext);
      }
      return setBadge(localDrawable);
    }
  }
  
  public void setBadgeMargin(float paramFloat)
  {
    this.mBadgeMargin = paramFloat;
    onBoundsChange(getBounds());
  }
  
  public void setBadgeRadius(float paramFloat)
  {
    this.mBadgeRadius = paramFloat;
    onBoundsChange(getBounds());
  }
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
  
  public void setFrameColor(int paramInt)
  {
    initFramePaint();
    this.mFramePaint.setColor(paramInt);
    invalidateSelf();
  }
  
  public void setFrameColor(ColorStateList paramColorStateList)
  {
    initFramePaint();
    this.mFrameColor = paramColorStateList;
    invalidateSelf();
  }
  
  public void setFramePadding(float paramFloat)
  {
    initFramePaint();
    this.mFramePadding = paramFloat;
    onBoundsChange(getBounds());
  }
  
  public void setFrameWidth(float paramFloat)
  {
    initFramePaint();
    this.mFrameWidth = paramFloat;
    this.mFramePaint.setStrokeWidth(paramFloat);
    onBoundsChange(getBounds());
  }
  
  public UserIconDrawable setIcon(Bitmap paramBitmap)
  {
    if (this.mUserDrawable != null)
    {
      this.mUserDrawable.setCallback(null);
      this.mUserDrawable = null;
    }
    this.mUserIcon = paramBitmap;
    if (this.mUserIcon == null)
    {
      this.mIconPaint.setShader(null);
      this.mBitmap = null;
    }
    for (;;)
    {
      onBoundsChange(getBounds());
      return this;
      this.mIconPaint.setShader(new BitmapShader(paramBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
    }
  }
  
  public UserIconDrawable setIconDrawable(Drawable paramDrawable)
  {
    if (this.mUserDrawable != null) {
      this.mUserDrawable.setCallback(null);
    }
    this.mUserIcon = null;
    this.mUserDrawable = paramDrawable;
    if (this.mUserDrawable == null) {
      this.mBitmap = null;
    }
    for (;;)
    {
      onBoundsChange(getBounds());
      return this;
      this.mUserDrawable.setCallback(this);
    }
  }
  
  public void setIntrinsicSize(int paramInt)
  {
    this.mSize = paramInt;
  }
  
  public void setPadding(float paramFloat)
  {
    this.mPadding = paramFloat;
    onBoundsChange(getBounds());
  }
  
  public void setTintList(ColorStateList paramColorStateList)
  {
    this.mTintColor = paramColorStateList;
    super.invalidateSelf();
  }
  
  public void setTintMode(PorterDuff.Mode paramMode)
  {
    this.mTintMode = paramMode;
    super.invalidateSelf();
  }
  
  public void unscheduleDrawable(Drawable paramDrawable, Runnable paramRunnable)
  {
    unscheduleSelf(paramRunnable);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\drawable\UserIconDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */