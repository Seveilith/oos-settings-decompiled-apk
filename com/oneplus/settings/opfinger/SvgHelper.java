package com.oneplus.settings.opfinger;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.Log;
import com.caverock.androidsvg.PreserveAspectRatio;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import java.util.ArrayList;
import java.util.List;

public class SvgHelper
{
  private static final String LOG_TAG = "SVG";
  private final List<SvgPath> mPaths = new ArrayList();
  private final Paint mSourcePaint;
  private SVG mSvg;
  
  public SvgHelper(Paint paramPaint)
  {
    this.mSourcePaint = paramPaint;
  }
  
  public List<SvgPath> getPathsForViewport(final int paramInt1, final int paramInt2)
  {
    this.mPaths.clear();
    Canvas local1 = new Canvas()
    {
      private final Matrix mMatrix = new Matrix();
      
      public void drawPath(Path paramAnonymousPath, Paint paramAnonymousPaint)
      {
        paramAnonymousPaint = new Path();
        getMatrix(this.mMatrix);
        paramAnonymousPath.transform(this.mMatrix, paramAnonymousPaint);
        SvgHelper.-get0(SvgHelper.this).add(new SvgHelper.SvgPath(paramAnonymousPaint, new Paint(SvgHelper.-get1(SvgHelper.this))));
      }
      
      public int getHeight()
      {
        return paramInt2;
      }
      
      public int getWidth()
      {
        return paramInt1;
      }
    };
    RectF localRectF = this.mSvg.getDocumentViewBox();
    float f = Math.min(paramInt1 / localRectF.width(), paramInt2 / localRectF.height());
    local1.translate((paramInt1 - localRectF.width() * f) / 2.0F, (paramInt2 - localRectF.height() * f) / 2.0F);
    local1.scale(f, f);
    this.mSvg.renderToCanvas(local1);
    return this.mPaths;
  }
  
  public void load(Context paramContext, int paramInt)
  {
    if (this.mSvg != null) {
      return;
    }
    try
    {
      this.mSvg = SVG.getFromResource(paramContext, paramInt);
      this.mSvg.setDocumentPreserveAspectRatio(PreserveAspectRatio.UNSCALED);
      return;
    }
    catch (SVGParseException paramContext)
    {
      Log.e("SVG", "Could not load specified SVG resource", paramContext);
    }
  }
  
  public static class SvgPath
  {
    private static final Region sMaxClip = new Region(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    private static final Region sRegion = new Region();
    final Rect bounds;
    final float length;
    final PathMeasure measure;
    final Paint paint;
    final Path path;
    final Path renderPath = new Path();
    
    SvgPath(Path paramPath, Paint paramPaint)
    {
      this.path = paramPath;
      this.paint = paramPaint;
      this.measure = new PathMeasure(paramPath, false);
      this.length = this.measure.getLength();
      sRegion.setPath(paramPath, sMaxClip);
      this.bounds = sRegion.getBounds();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\opfinger\SvgHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */