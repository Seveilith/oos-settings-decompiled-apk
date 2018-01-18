package com.caverock.androidsvg;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.PathMeasure;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.util.Base64;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;

public class SVGAndroidRenderer
{
  private static final float BEZIER_ARC_FACTOR = 0.5522848F;
  private static final String DEFAULT_FONT_FAMILY = "sans-serif";
  private static final int LUMINANCE_FACTOR_SHIFT = 15;
  private static final int LUMINANCE_TO_ALPHA_BLUE = 2362;
  private static final int LUMINANCE_TO_ALPHA_GREEN = 23442;
  private static final int LUMINANCE_TO_ALPHA_RED = 6963;
  private static final String TAG = "SVGAndroidRenderer";
  private Stack<Bitmap> bitmapStack;
  private Canvas canvas;
  private Stack<Canvas> canvasStack;
  private SVG.Box canvasViewPort;
  private boolean directRenderingMode;
  private SVG document;
  private float dpi;
  private Stack<Matrix> matrixStack;
  private Stack<SVG.SvgContainer> parentStack;
  private RendererState state;
  private Stack<RendererState> stateStack;
  
  protected SVGAndroidRenderer(Canvas paramCanvas, SVG.Box paramBox, float paramFloat)
  {
    this.canvas = paramCanvas;
    this.dpi = paramFloat;
    this.canvasViewPort = paramBox;
  }
  
  private void addObjectToClip(SVG.GraphicsElement paramGraphicsElement, Path paramPath, Matrix paramMatrix)
  {
    updateStyleForElement(this.state, paramGraphicsElement);
    if (display())
    {
      if (!visible()) {
        break label60;
      }
      if (paramGraphicsElement.transform != null) {
        break label61;
      }
    }
    while (!(paramGraphicsElement instanceof SVG.Rect))
    {
      if ((paramGraphicsElement instanceof SVG.Circle)) {
        break label105;
      }
      if ((paramGraphicsElement instanceof SVG.Ellipse)) {
        break label118;
      }
      if ((paramGraphicsElement instanceof SVG.PolyLine)) {
        break label131;
      }
      return;
      return;
      label60:
      return;
      label61:
      paramMatrix.preConcat(paramGraphicsElement.transform);
    }
    Path localPath = makePathAndBoundingBox((SVG.Rect)paramGraphicsElement);
    for (;;)
    {
      checkForClipPath(paramGraphicsElement);
      paramPath.setFillType(localPath.getFillType());
      paramPath.addPath(localPath, paramMatrix);
      return;
      label105:
      localPath = makePathAndBoundingBox((SVG.Circle)paramGraphicsElement);
      continue;
      label118:
      localPath = makePathAndBoundingBox((SVG.Ellipse)paramGraphicsElement);
      continue;
      label131:
      localPath = makePathAndBoundingBox((SVG.PolyLine)paramGraphicsElement);
    }
  }
  
  private void addObjectToClip(SVG.Path paramPath, Path paramPath1, Matrix paramMatrix)
  {
    updateStyleForElement(this.state, paramPath);
    Path localPath;
    if (display())
    {
      if (!visible()) {
        break label76;
      }
      if (paramPath.transform != null) {
        break label77;
      }
      localPath = new PathConverter(paramPath.d).getPath();
      if (paramPath.boundingBox == null) {
        break label89;
      }
    }
    for (;;)
    {
      checkForClipPath(paramPath);
      paramPath1.setFillType(getClipRuleFromState());
      paramPath1.addPath(localPath, paramMatrix);
      return;
      return;
      label76:
      return;
      label77:
      paramMatrix.preConcat(paramPath.transform);
      break;
      label89:
      paramPath.boundingBox = calculatePathBounds(localPath);
    }
  }
  
  private void addObjectToClip(SVG.SvgObject paramSvgObject, boolean paramBoolean, Path paramPath, Matrix paramMatrix)
  {
    if (display())
    {
      clipStatePush();
      if ((paramSvgObject instanceof SVG.Use)) {
        break label65;
      }
      if ((paramSvgObject instanceof SVG.Path)) {
        break label96;
      }
      if ((paramSvgObject instanceof SVG.Text)) {
        break label110;
      }
      if ((paramSvgObject instanceof SVG.GraphicsElement)) {
        break label124;
      }
      error("Invalid %s element found in clipPath definition", new Object[] { paramSvgObject.getClass().getSimpleName() });
    }
    for (;;)
    {
      clipStatePop();
      return;
      return;
      label65:
      if (!paramBoolean)
      {
        error("<use> elements inside a <clipPath> cannot reference another <use>", new Object[0]);
      }
      else
      {
        addObjectToClip((SVG.Use)paramSvgObject, paramPath, paramMatrix);
        continue;
        label96:
        addObjectToClip((SVG.Path)paramSvgObject, paramPath, paramMatrix);
        continue;
        label110:
        addObjectToClip((SVG.Text)paramSvgObject, paramPath, paramMatrix);
        continue;
        label124:
        addObjectToClip((SVG.GraphicsElement)paramSvgObject, paramPath, paramMatrix);
      }
    }
  }
  
  private void addObjectToClip(SVG.Text paramText, Path paramPath, Matrix paramMatrix)
  {
    float f4 = 0.0F;
    updateStyleForElement(this.state, paramText);
    label33:
    float f1;
    label36:
    label43:
    float f2;
    label46:
    label53:
    float f3;
    if (display())
    {
      if (paramText.transform != null) {
        break label142;
      }
      if (paramText.x != null) {
        break label154;
      }
      f1 = 0.0F;
      if (paramText.y != null) {
        break label188;
      }
      f2 = 0.0F;
      if (paramText.dx != null) {
        break label222;
      }
      f3 = 0.0F;
      label56:
      if (paramText.dy != null) {
        break label256;
      }
      label63:
      if (this.state.style.textAnchor != SVG.Style.TextAnchor.Start) {
        break label290;
      }
      label79:
      if (paramText.boundingBox == null) {
        break label335;
      }
    }
    for (;;)
    {
      checkForClipPath(paramText);
      Object localObject = new Path();
      enumerateTextSpans(paramText, new PlainTextToPath(f1 + f3, f4 + f2, (Path)localObject));
      paramPath.setFillType(getClipRuleFromState());
      paramPath.addPath((Path)localObject, paramMatrix);
      return;
      return;
      label142:
      paramMatrix.preConcat(paramText.transform);
      break;
      label154:
      if (paramText.x.size() == 0) {
        break label33;
      }
      f1 = ((SVG.Length)paramText.x.get(0)).floatValueX(this);
      break label36;
      label188:
      if (paramText.y.size() == 0) {
        break label43;
      }
      f2 = ((SVG.Length)paramText.y.get(0)).floatValueY(this);
      break label46;
      label222:
      if (paramText.dx.size() == 0) {
        break label53;
      }
      f3 = ((SVG.Length)paramText.dx.get(0)).floatValueX(this);
      break label56;
      label256:
      if (paramText.dy.size() == 0) {
        break label63;
      }
      f4 = ((SVG.Length)paramText.dy.get(0)).floatValueY(this);
      break label63;
      label290:
      float f5 = calculateTextWidth(paramText);
      if (this.state.style.textAnchor != SVG.Style.TextAnchor.Middle)
      {
        f1 -= f5;
        break label79;
      }
      f1 -= f5 / 2.0F;
      break label79;
      label335:
      localObject = new TextBoundsCalculator(f1, f2);
      enumerateTextSpans(paramText, (TextProcessor)localObject);
      paramText.boundingBox = new SVG.Box(((TextBoundsCalculator)localObject).bbox.left, ((TextBoundsCalculator)localObject).bbox.top, ((TextBoundsCalculator)localObject).bbox.width(), ((TextBoundsCalculator)localObject).bbox.height());
    }
  }
  
  private void addObjectToClip(SVG.Use paramUse, Path paramPath, Matrix paramMatrix)
  {
    updateStyleForElement(this.state, paramUse);
    if (display())
    {
      if (!visible()) {
        break label64;
      }
      if (paramUse.transform != null) {
        break label65;
      }
    }
    for (;;)
    {
      SVG.SvgObject localSvgObject = paramUse.document.resolveIRI(paramUse.href);
      if (localSvgObject == null) {
        break;
      }
      checkForClipPath(paramUse);
      addObjectToClip(localSvgObject, false, paramPath, paramMatrix);
      return;
      return;
      label64:
      return;
      label65:
      paramMatrix.preConcat(paramUse.transform);
    }
    error("Use reference '%s' not found", new Object[] { paramUse.href });
  }
  
  private static void arcTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, boolean paramBoolean1, boolean paramBoolean2, float paramFloat6, float paramFloat7, SVG.PathInterface paramPathInterface)
  {
    if ((paramFloat1 == paramFloat6) && (paramFloat2 == paramFloat7)) {
      return;
    }
    if ((paramFloat3 == 0.0F) || (paramFloat4 == 0.0F))
    {
      paramPathInterface.lineTo(paramFloat6, paramFloat7);
      return;
    }
    float f = Math.abs(paramFloat3);
    paramFloat3 = Math.abs(paramFloat4);
    paramFloat4 = (float)Math.toRadians(paramFloat5 % 360.0D);
    double d3 = Math.cos(paramFloat4);
    double d4 = Math.sin(paramFloat4);
    double d1 = (paramFloat1 - paramFloat6) / 2.0D;
    double d2 = (paramFloat2 - paramFloat7) / 2.0D;
    double d5 = d4 * d2 + d3 * d1;
    double d6 = d1 * -d4 + d2 * d3;
    d2 = f * f;
    d1 = paramFloat3 * paramFloat3;
    double d7 = d5 * d5;
    double d8 = d6 * d6;
    double d9 = d7 / d2 + d8 / d1;
    int i;
    label214:
    double d10;
    double d12;
    if (d9 > 1.0D)
    {
      paramFloat4 = f * (float)Math.sqrt(d9);
      paramFloat3 *= (float)Math.sqrt(d9);
      d2 = paramFloat4 * paramFloat4;
      d1 = paramFloat3 * paramFloat3;
      if (paramBoolean1 == paramBoolean2) {
        break label676;
      }
      i = 1;
      d9 = i;
      d2 = (d2 * d1 - d2 * d8 - d1 * d7) / (d1 * d7 + d2 * d8);
      d1 = d2;
      if (d2 < 0.0D) {
        d1 = 0.0D;
      }
      d1 = Math.sqrt(d1) * d9;
      d7 = paramFloat4 * d6 / paramFloat3 * d1;
      d8 = d1 * -(paramFloat3 * d5 / paramFloat4);
      d9 = (paramFloat1 + paramFloat6) / 2.0D;
      d10 = (paramFloat2 + paramFloat7) / 2.0D;
      d2 = (d5 - d7) / paramFloat4;
      double d11 = (d6 - d8) / paramFloat3;
      d5 = (-d5 - d7) / paramFloat4;
      d6 = (-d6 - d8) / paramFloat3;
      d12 = Math.sqrt(d2 * d2 + d11 * d11);
      if (d11 >= 0.0D) {
        break label682;
      }
      d1 = -1.0D;
      label393:
      d12 = Math.toDegrees(d1 * Math.acos(d2 / d12));
      double d13 = Math.sqrt((d2 * d2 + d11 * d11) * (d5 * d5 + d6 * d6));
      if (d2 * d6 - d5 * d11 >= 0.0D) {
        break label688;
      }
      d1 = -1.0D;
      label458:
      d2 = Math.toDegrees(d1 * Math.acos((d11 * d6 + d2 * d5) / d13));
      if (!paramBoolean2) {
        break label694;
      }
      label488:
      if (paramBoolean2) {
        break label712;
      }
      d1 = d2;
    }
    for (;;)
    {
      float[] arrayOfFloat = arcToBeziers(d12 % 360.0D, d1 % 360.0D);
      Matrix localMatrix = new Matrix();
      localMatrix.postScale(paramFloat4, paramFloat3);
      localMatrix.postRotate(paramFloat5);
      localMatrix.postTranslate((float)(d9 + (d3 * d7 - d4 * d8)), (float)(d3 * d8 + d4 * d7 + d10));
      localMatrix.mapPoints(arrayOfFloat);
      arrayOfFloat[(arrayOfFloat.length - 2)] = paramFloat6;
      arrayOfFloat[(arrayOfFloat.length - 1)] = paramFloat7;
      i = 0;
      while (i < arrayOfFloat.length)
      {
        paramPathInterface.cubicTo(arrayOfFloat[i], arrayOfFloat[(i + 1)], arrayOfFloat[(i + 2)], arrayOfFloat[(i + 3)], arrayOfFloat[(i + 4)], arrayOfFloat[(i + 5)]);
        i += 6;
      }
      paramFloat4 = f;
      break;
      label676:
      i = -1;
      break label214;
      label682:
      d1 = 1.0D;
      break label393;
      label688:
      d1 = 1.0D;
      break label458;
      label694:
      if (d2 <= 0.0D) {
        break label488;
      }
      d1 = d2 - 360.0D;
      continue;
      label712:
      d1 = d2;
      if (d2 < 0.0D) {
        d1 = d2 + 360.0D;
      }
    }
  }
  
  private static float[] arcToBeziers(double paramDouble1, double paramDouble2)
  {
    int k = (int)Math.ceil(Math.abs(paramDouble2) / 90.0D);
    paramDouble1 = Math.toRadians(paramDouble1);
    float f = (float)(Math.toRadians(paramDouble2) / k);
    paramDouble2 = Math.sin(f / 2.0D) * 1.3333333333333333D / (Math.cos(f / 2.0D) + 1.0D);
    float[] arrayOfFloat = new float[k * 6];
    int j = 0;
    int i = 0;
    while (i < k)
    {
      double d1 = i * f + paramDouble1;
      double d2 = Math.cos(d1);
      double d3 = Math.sin(d1);
      int m = j + 1;
      arrayOfFloat[j] = ((float)(d2 - paramDouble2 * d3));
      j = m + 1;
      arrayOfFloat[m] = ((float)(d2 * paramDouble2 + d3));
      d2 = d1 + f;
      d1 = Math.cos(d2);
      d2 = Math.sin(d2);
      m = j + 1;
      arrayOfFloat[j] = ((float)(paramDouble2 * d2 + d1));
      j = m + 1;
      arrayOfFloat[m] = ((float)(d2 - paramDouble2 * d1));
      m = j + 1;
      arrayOfFloat[j] = ((float)d1);
      j = m + 1;
      arrayOfFloat[m] = ((float)d2);
      i += 1;
    }
    return arrayOfFloat;
  }
  
  private List<MarkerVector> calculateMarkerPositions(SVG.Line paramLine)
  {
    float f4 = 0.0F;
    float f1;
    float f2;
    label21:
    float f3;
    if (paramLine.x1 == null)
    {
      f1 = 0.0F;
      if (paramLine.y1 != null) {
        break label113;
      }
      f2 = 0.0F;
      if (paramLine.x2 != null) {
        break label125;
      }
      f3 = 0.0F;
      label31:
      if (paramLine.y2 != null) {
        break label138;
      }
    }
    for (;;)
    {
      paramLine = new ArrayList(2);
      paramLine.add(new MarkerVector(f1, f2, f3 - f1, f4 - f2));
      paramLine.add(new MarkerVector(f3, f4, f3 - f1, f4 - f2));
      return paramLine;
      f1 = paramLine.x1.floatValueX(this);
      break;
      label113:
      f2 = paramLine.y1.floatValueY(this);
      break label21;
      label125:
      f3 = paramLine.x2.floatValueX(this);
      break label31;
      label138:
      f4 = paramLine.y2.floatValueY(this);
    }
  }
  
  private List<MarkerVector> calculateMarkerPositions(SVG.PolyLine paramPolyLine)
  {
    int i = 2;
    float f2 = 0.0F;
    int j = paramPolyLine.points.length;
    ArrayList localArrayList;
    MarkerVector localMarkerVector;
    if (j >= 2)
    {
      localArrayList = new ArrayList();
      localMarkerVector = new MarkerVector(paramPolyLine.points[0], paramPolyLine.points[1], 0.0F, 0.0F);
      f1 = 0.0F;
      while (i < j)
      {
        f1 = paramPolyLine.points[i];
        f2 = paramPolyLine.points[(i + 1)];
        localMarkerVector.add(f1, f2);
        localArrayList.add(localMarkerVector);
        localMarkerVector = new MarkerVector(f1, f2, f1 - localMarkerVector.x, f2 - localMarkerVector.y);
        i += 2;
      }
    }
    return null;
    if (!(paramPolyLine instanceof SVG.Polygon)) {
      localArrayList.add(localMarkerVector);
    }
    while ((f1 == paramPolyLine.points[0]) || (f2 == paramPolyLine.points[1])) {
      return localArrayList;
    }
    float f1 = paramPolyLine.points[0];
    f2 = paramPolyLine.points[1];
    localMarkerVector.add(f1, f2);
    localArrayList.add(localMarkerVector);
    paramPolyLine = new MarkerVector(f1, f2, f1 - localMarkerVector.x, f2 - localMarkerVector.y);
    paramPolyLine.add((MarkerVector)localArrayList.get(0));
    localArrayList.add(paramPolyLine);
    localArrayList.set(0, paramPolyLine);
    return localArrayList;
  }
  
  private SVG.Box calculatePathBounds(Path paramPath)
  {
    RectF localRectF = new RectF();
    paramPath.computeBounds(localRectF, true);
    return new SVG.Box(localRectF.left, localRectF.top, localRectF.width(), localRectF.height());
  }
  
  private float calculateTextWidth(SVG.TextContainer paramTextContainer)
  {
    TextWidthCalculator localTextWidthCalculator = new TextWidthCalculator(null);
    enumerateTextSpans(paramTextContainer, localTextWidthCalculator);
    return localTextWidthCalculator.x;
  }
  
  private Matrix calculateViewBoxTransform(SVG.Box paramBox1, SVG.Box paramBox2, PreserveAspectRatio paramPreserveAspectRatio)
  {
    Matrix localMatrix = new Matrix();
    if (paramPreserveAspectRatio == null) {}
    while (paramPreserveAspectRatio.getAlignment() == null) {
      return localMatrix;
    }
    float f1 = paramBox1.width / paramBox2.width;
    float f3 = paramBox1.height / paramBox2.height;
    float f4 = -paramBox2.minX;
    float f2 = -paramBox2.minY;
    float f6;
    float f5;
    if (!paramPreserveAspectRatio.equals(PreserveAspectRatio.STRETCH))
    {
      if (paramPreserveAspectRatio.getScale() == PreserveAspectRatio.Scale.Slice) {
        break label294;
      }
      f3 = Math.min(f1, f3);
      f6 = paramBox1.width / f3;
      f5 = paramBox1.height / f3;
      f1 = f4;
      switch ($SWITCH_TABLE$com$caverock$androidsvg$PreserveAspectRatio$Alignment()[paramPreserveAspectRatio.getAlignment().ordinal()])
      {
      default: 
        f1 = f4;
      case 5: 
      case 8: 
        label172:
        switch ($SWITCH_TABLE$com$caverock$androidsvg$PreserveAspectRatio$Alignment()[paramPreserveAspectRatio.getAlignment().ordinal()])
        {
        }
        break;
      }
    }
    for (;;)
    {
      localMatrix.preTranslate(paramBox1.minX, paramBox1.minY);
      localMatrix.preScale(f3, f3);
      localMatrix.preTranslate(f1, f2);
      return localMatrix;
      localMatrix.preTranslate(paramBox1.minX, paramBox1.minY);
      localMatrix.preScale(f1, f3);
      localMatrix.preTranslate(f4, f2);
      return localMatrix;
      label294:
      f3 = Math.max(f1, f3);
      break;
      f1 = f4 - (paramBox2.width - f6) / 2.0F;
      break label172;
      f1 = f4 - (paramBox2.width - f6);
      break label172;
      f2 -= (paramBox2.height - f5) / 2.0F;
      continue;
      f2 -= paramBox2.height - f5;
    }
  }
  
  private void checkForClipPath(SVG.SvgElement paramSvgElement)
  {
    checkForClipPath(paramSvgElement, paramSvgElement.boundingBox);
  }
  
  private void checkForClipPath(SVG.SvgElement paramSvgElement, SVG.Box paramBox)
  {
    Object localObject;
    int i;
    if (this.state.style.clipPath != null)
    {
      localObject = paramSvgElement.document.resolveIRI(this.state.style.clipPath);
      if (localObject == null) {
        break label160;
      }
      localObject = (SVG.ClipPath)localObject;
      if (((SVG.ClipPath)localObject).children.isEmpty()) {
        break label184;
      }
      if (((SVG.ClipPath)localObject).clipPathUnitsAreUser != null) {
        break label197;
      }
      i = 1;
      label67:
      if ((paramSvgElement instanceof SVG.Group)) {
        break label213;
      }
      label74:
      clipStatePush();
      if (i == 0) {
        break label238;
      }
      label82:
      if (((SVG.ClipPath)localObject).transform != null) {
        break label283;
      }
    }
    for (;;)
    {
      this.state = findInheritFromAncestorState((SVG.SvgObject)localObject);
      checkForClipPath((SVG.SvgElement)localObject);
      paramSvgElement = new Path();
      paramBox = ((SVG.ClipPath)localObject).children.iterator();
      while (paramBox.hasNext()) {
        addObjectToClip((SVG.SvgObject)paramBox.next(), true, paramSvgElement, new Matrix());
      }
      return;
      label160:
      error("ClipPath reference '%s' not found", new Object[] { this.state.style.clipPath });
      return;
      label184:
      this.canvas.clipRect(0, 0, 0, 0);
      return;
      label197:
      if (((SVG.ClipPath)localObject).clipPathUnitsAreUser.booleanValue()) {
        break;
      }
      i = 0;
      break label67;
      label213:
      if (i != 0) {
        break label74;
      }
      warn("<clipPath clipPathUnits=\"objectBoundingBox\"> is not supported when referenced from container elements (like %s)", new Object[] { paramSvgElement.getClass().getSimpleName() });
      return;
      label238:
      paramSvgElement = new Matrix();
      paramSvgElement.preTranslate(paramBox.minX, paramBox.minY);
      paramSvgElement.preScale(paramBox.width, paramBox.height);
      this.canvas.concat(paramSvgElement);
      break label82;
      label283:
      this.canvas.concat(((SVG.ClipPath)localObject).transform);
    }
    this.canvas.clipPath(paramSvgElement);
    clipStatePop();
  }
  
  private void checkForGradiantsAndPatterns(SVG.SvgElement paramSvgElement)
  {
    if (!(this.state.style.fill instanceof SVG.PaintReference)) {}
    while (!(this.state.style.stroke instanceof SVG.PaintReference))
    {
      return;
      decodePaintReference(true, paramSvgElement.boundingBox, (SVG.PaintReference)this.state.style.fill);
    }
    decodePaintReference(false, paramSvgElement.boundingBox, (SVG.PaintReference)this.state.style.stroke);
  }
  
  private Bitmap checkForImageDataURL(String paramString)
  {
    int i;
    if (paramString.startsWith("data:"))
    {
      if (paramString.length() < 14) {
        break label35;
      }
      i = paramString.indexOf(',');
      if (i != -1) {
        break label37;
      }
    }
    label35:
    label37:
    while (i < 12)
    {
      return null;
      return null;
      return null;
    }
    if (";base64".equals(paramString.substring(i - 7, i)))
    {
      paramString = Base64.decode(paramString.substring(i + 1), 0);
      return BitmapFactory.decodeByteArray(paramString, 0, paramString.length);
    }
    return null;
  }
  
  private Typeface checkGenericFont(String paramString, Integer paramInteger, SVG.Style.FontStyle paramFontStyle)
  {
    int j = 0;
    int i;
    if (paramFontStyle != SVG.Style.FontStyle.Italic)
    {
      i = 0;
      if (paramInteger.intValue() > 500) {
        break label89;
      }
      if (i != 0) {
        break label106;
      }
      i = j;
    }
    for (;;)
    {
      if (paramString.equals("serif")) {
        break label112;
      }
      if (paramString.equals("sans-serif")) {
        break label121;
      }
      if (paramString.equals("monospace")) {
        break label130;
      }
      if (paramString.equals("cursive")) {
        break label139;
      }
      if (paramString.equals("fantasy")) {
        break label148;
      }
      return null;
      i = 1;
      break;
      label89:
      if (i == 0)
      {
        i = 1;
      }
      else
      {
        i = 3;
        continue;
        label106:
        i = 2;
      }
    }
    label112:
    return Typeface.create(Typeface.SERIF, i);
    label121:
    return Typeface.create(Typeface.SANS_SERIF, i);
    label130:
    return Typeface.create(Typeface.MONOSPACE, i);
    label139:
    return Typeface.create(Typeface.SANS_SERIF, i);
    label148:
    return Typeface.create(Typeface.SANS_SERIF, i);
  }
  
  private void checkXMLSpaceAttribute(SVG.SvgObject paramSvgObject)
  {
    if ((paramSvgObject instanceof SVG.SvgElementBase))
    {
      paramSvgObject = (SVG.SvgElementBase)paramSvgObject;
      if (paramSvgObject.spacePreserve != null) {}
    }
    else
    {
      return;
    }
    this.state.spacePreserve = paramSvgObject.spacePreserve.booleanValue();
  }
  
  private int clamp255(float paramFloat)
  {
    int i = (int)(256.0F * paramFloat);
    if (i >= 0)
    {
      if (i <= 255) {
        return i;
      }
    }
    else {
      return 0;
    }
    return 255;
  }
  
  private void clipStatePop()
  {
    this.canvas.restore();
    this.state = ((RendererState)this.stateStack.pop());
  }
  
  private void clipStatePush()
  {
    this.canvas.save(1);
    this.stateStack.push(this.state);
    this.state = ((RendererState)this.state.clone());
  }
  
  private static void debug(String paramString, Object... paramVarArgs) {}
  
  private void decodePaintReference(boolean paramBoolean, SVG.Box paramBox, SVG.PaintReference paramPaintReference)
  {
    SVG.SvgObject localSvgObject = this.document.resolveIRI(paramPaintReference.href);
    if (localSvgObject != null)
    {
      if ((localSvgObject instanceof SVG.SvgLinearGradient)) {
        break label122;
      }
      if ((localSvgObject instanceof SVG.SvgRadialGradient)) {
        break label136;
      }
    }
    for (;;)
    {
      if ((localSvgObject instanceof SVG.SolidColor)) {
        break label150;
      }
      return;
      if (!paramBoolean) {}
      for (paramBox = "Stroke";; paramBox = "Fill")
      {
        error("%s reference '%s' not found", new Object[] { paramBox, paramPaintReference.href });
        if (paramPaintReference.fallback != null) {
          break;
        }
        if (paramBoolean) {
          break label113;
        }
        this.state.hasStroke = false;
        return;
      }
      setPaintColour(this.state, paramBoolean, paramPaintReference.fallback);
      return;
      label113:
      this.state.hasFill = false;
      return;
      label122:
      makeLinearGradiant(paramBoolean, paramBox, (SVG.SvgLinearGradient)localSvgObject);
      break;
      label136:
      makeRadialGradiant(paramBoolean, paramBox, (SVG.SvgRadialGradient)localSvgObject);
    }
    label150:
    setSolidColor(paramBoolean, (SVG.SolidColor)localSvgObject);
  }
  
  private boolean display()
  {
    if (this.state.style.display == null) {
      return true;
    }
    return this.state.style.display.booleanValue();
  }
  
  private void doFilledPath(SVG.SvgElement paramSvgElement, Path paramPath)
  {
    if (!(this.state.style.fill instanceof SVG.PaintReference)) {}
    SVG.SvgObject localSvgObject;
    do
    {
      this.canvas.drawPath(paramPath, this.state.fillPaint);
      return;
      localSvgObject = this.document.resolveIRI(((SVG.PaintReference)this.state.style.fill).href);
    } while (!(localSvgObject instanceof SVG.Pattern));
    fillWithPattern(paramSvgElement, paramPath, (SVG.Pattern)localSvgObject);
  }
  
  private void doStroke(Path paramPath)
  {
    if (this.state.style.vectorEffect != SVG.Style.VectorEffect.NonScalingStroke)
    {
      this.canvas.drawPath(paramPath, this.state.strokePaint);
      return;
    }
    Matrix localMatrix1 = this.canvas.getMatrix();
    Path localPath = new Path();
    paramPath.transform(localMatrix1, localPath);
    this.canvas.setMatrix(new Matrix());
    paramPath = this.state.strokePaint.getShader();
    Matrix localMatrix2 = new Matrix();
    if (paramPath == null) {}
    for (;;)
    {
      this.canvas.drawPath(localPath, this.state.strokePaint);
      this.canvas.setMatrix(localMatrix1);
      if (paramPath == null) {
        break;
      }
      paramPath.setLocalMatrix(localMatrix2);
      return;
      paramPath.getLocalMatrix(localMatrix2);
      Matrix localMatrix3 = new Matrix(localMatrix2);
      localMatrix3.postConcat(localMatrix1);
      paramPath.setLocalMatrix(localMatrix3);
    }
  }
  
  private void duplicateCanvas()
  {
    try
    {
      Object localObject = Bitmap.createBitmap(this.canvas.getWidth(), this.canvas.getHeight(), Bitmap.Config.ARGB_8888);
      this.bitmapStack.push(localObject);
      localObject = new Canvas((Bitmap)localObject);
      ((Canvas)localObject).setMatrix(this.canvas.getMatrix());
      this.canvas = ((Canvas)localObject);
      return;
    }
    catch (OutOfMemoryError localOutOfMemoryError)
    {
      error("Not enough memory to create temporary bitmaps for mask processing", new Object[0]);
      throw localOutOfMemoryError;
    }
  }
  
  private void enumerateTextSpans(SVG.TextContainer paramTextContainer, TextProcessor paramTextProcessor)
  {
    boolean bool1;
    if (display())
    {
      paramTextContainer = paramTextContainer.children.iterator();
      for (bool1 = true;; bool1 = false)
      {
        if (!paramTextContainer.hasNext()) {
          return;
        }
        localObject = (SVG.SvgObject)paramTextContainer.next();
        if ((localObject instanceof SVG.TextSequence)) {
          break;
        }
        processTextChild((SVG.SvgObject)localObject, paramTextProcessor);
      }
    }
    return;
    Object localObject = ((SVG.TextSequence)localObject).text;
    if (!paramTextContainer.hasNext()) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      paramTextProcessor.processText(textXMLSpaceTransform((String)localObject, bool1, bool2));
      break;
    }
  }
  
  private static void error(String paramString, Object... paramVarArgs)
  {
    Log.e("SVGAndroidRenderer", String.format(paramString, paramVarArgs));
  }
  
  private void extractRawText(SVG.TextContainer paramTextContainer, StringBuilder paramStringBuilder)
  {
    paramTextContainer = paramTextContainer.children.iterator();
    boolean bool1 = true;
    if (paramTextContainer.hasNext())
    {
      Object localObject = (SVG.SvgObject)paramTextContainer.next();
      if (!(localObject instanceof SVG.TextContainer))
      {
        if ((localObject instanceof SVG.TextSequence)) {}
      }
      else {
        for (;;)
        {
          bool1 = false;
          break;
          extractRawText((SVG.TextContainer)localObject, paramStringBuilder);
        }
      }
      localObject = ((SVG.TextSequence)localObject).text;
      if (!paramTextContainer.hasNext()) {}
      for (boolean bool2 = true;; bool2 = false)
      {
        paramStringBuilder.append(textXMLSpaceTransform((String)localObject, bool1, bool2));
        break;
      }
    }
  }
  
  private void fillInChainedGradientFields(SVG.GradientElement paramGradientElement, String paramString)
  {
    SVG.SvgObject localSvgObject = paramGradientElement.document.resolveIRI(paramString);
    if (localSvgObject != null)
    {
      if (!(localSvgObject instanceof SVG.GradientElement)) {
        break label105;
      }
      if (localSvgObject == paramGradientElement) {
        break label116;
      }
      paramString = (SVG.GradientElement)localSvgObject;
    }
    for (;;)
    {
      label44:
      label51:
      if ((paramGradientElement.gradientUnitsAreUser == null) || ((paramGradientElement.gradientTransform == null) || ((paramGradientElement.spreadMethod == null) || (!paramGradientElement.children.isEmpty())))) {}
      try
      {
        label63:
        if (!(paramGradientElement instanceof SVG.SvgLinearGradient)) {
          fillInChainedGradientFields((SVG.SvgRadialGradient)paramGradientElement, (SVG.SvgRadialGradient)localSvgObject);
        }
        for (;;)
        {
          if (paramString.href != null) {
            break label194;
          }
          return;
          warn("Gradient reference '%s' not found", new Object[] { paramString });
          return;
          label105:
          error("Gradient href attributes must point to other gradient elements", new Object[0]);
          return;
          label116:
          error("Circular reference in gradient href attribute '%s'", new Object[] { paramString });
          return;
          paramGradientElement.gradientUnitsAreUser = paramString.gradientUnitsAreUser;
          break;
          paramGradientElement.gradientTransform = paramString.gradientTransform;
          break label44;
          paramGradientElement.spreadMethod = paramString.spreadMethod;
          break label51;
          paramGradientElement.children = paramString.children;
          break label63;
          fillInChainedGradientFields((SVG.SvgLinearGradient)paramGradientElement, (SVG.SvgLinearGradient)localSvgObject);
        }
      }
      catch (ClassCastException localClassCastException)
      {
        for (;;) {}
        label194:
        fillInChainedGradientFields(paramGradientElement, paramString.href);
      }
    }
  }
  
  private void fillInChainedGradientFields(SVG.SvgLinearGradient paramSvgLinearGradient1, SVG.SvgLinearGradient paramSvgLinearGradient2)
  {
    if (paramSvgLinearGradient1.x1 != null)
    {
      if (paramSvgLinearGradient1.y1 == null) {
        break label40;
      }
      label14:
      if (paramSvgLinearGradient1.x2 == null) {
        break label51;
      }
    }
    for (;;)
    {
      if (paramSvgLinearGradient1.y2 == null) {
        break label62;
      }
      return;
      paramSvgLinearGradient1.x1 = paramSvgLinearGradient2.x1;
      break;
      label40:
      paramSvgLinearGradient1.y1 = paramSvgLinearGradient2.y1;
      break label14;
      label51:
      paramSvgLinearGradient1.x2 = paramSvgLinearGradient2.x2;
    }
    label62:
    paramSvgLinearGradient1.y2 = paramSvgLinearGradient2.y2;
  }
  
  private void fillInChainedGradientFields(SVG.SvgRadialGradient paramSvgRadialGradient1, SVG.SvgRadialGradient paramSvgRadialGradient2)
  {
    if (paramSvgRadialGradient1.cx != null)
    {
      if (paramSvgRadialGradient1.cy == null) {
        break label47;
      }
      label14:
      if (paramSvgRadialGradient1.r == null) {
        break label58;
      }
      label21:
      if (paramSvgRadialGradient1.fx == null) {
        break label69;
      }
    }
    for (;;)
    {
      if (paramSvgRadialGradient1.fy == null) {
        break label80;
      }
      return;
      paramSvgRadialGradient1.cx = paramSvgRadialGradient2.cx;
      break;
      label47:
      paramSvgRadialGradient1.cy = paramSvgRadialGradient2.cy;
      break label14;
      label58:
      paramSvgRadialGradient1.r = paramSvgRadialGradient2.r;
      break label21;
      label69:
      paramSvgRadialGradient1.fx = paramSvgRadialGradient2.fx;
    }
    label80:
    paramSvgRadialGradient1.fy = paramSvgRadialGradient2.fy;
  }
  
  private void fillInChainedPatternFields(SVG.Pattern paramPattern, String paramString)
  {
    SVG.SvgObject localSvgObject = paramPattern.document.resolveIRI(paramString);
    if (localSvgObject != null)
    {
      if (!(localSvgObject instanceof SVG.Pattern)) {
        break label128;
      }
      if (localSvgObject == paramPattern) {
        break label139;
      }
      paramString = (SVG.Pattern)localSvgObject;
      if (paramPattern.patternUnitsAreUser == null) {
        break label154;
      }
      if (paramPattern.patternContentUnitsAreUser == null) {
        break label165;
      }
      label44:
      if (paramPattern.patternTransform == null) {
        break label176;
      }
      label51:
      if (paramPattern.x == null) {
        break label187;
      }
      label58:
      if (paramPattern.y == null) {
        break label198;
      }
      label65:
      if (paramPattern.width == null) {
        break label209;
      }
      label72:
      if (paramPattern.height == null) {
        break label220;
      }
      label79:
      if (paramPattern.children.isEmpty()) {
        break label231;
      }
      label91:
      if (paramPattern.viewBox == null) {
        break label242;
      }
      label98:
      if (paramPattern.preserveAspectRatio == null) {
        break label253;
      }
    }
    for (;;)
    {
      if (paramString.href != null) {
        break label264;
      }
      return;
      warn("Pattern reference '%s' not found", new Object[] { paramString });
      return;
      label128:
      error("Pattern href attributes must point to other pattern elements", new Object[0]);
      return;
      label139:
      error("Circular reference in pattern href attribute '%s'", new Object[] { paramString });
      return;
      label154:
      paramPattern.patternUnitsAreUser = paramString.patternUnitsAreUser;
      break;
      label165:
      paramPattern.patternContentUnitsAreUser = paramString.patternContentUnitsAreUser;
      break label44;
      label176:
      paramPattern.patternTransform = paramString.patternTransform;
      break label51;
      label187:
      paramPattern.x = paramString.x;
      break label58;
      label198:
      paramPattern.y = paramString.y;
      break label65;
      label209:
      paramPattern.width = paramString.width;
      break label72;
      label220:
      paramPattern.height = paramString.height;
      break label79;
      label231:
      paramPattern.children = paramString.children;
      break label91;
      label242:
      paramPattern.viewBox = paramString.viewBox;
      break label98;
      label253:
      paramPattern.preserveAspectRatio = paramString.preserveAspectRatio;
    }
    label264:
    fillInChainedPatternFields(paramPattern, paramString.href);
  }
  
  private void fillWithPattern(SVG.SvgElement paramSvgElement, Path paramPath, SVG.Pattern paramPattern)
  {
    label10:
    label17:
    float f1;
    label32:
    float f2;
    label42:
    float f3;
    if (paramPattern.patternUnitsAreUser == null)
    {
      i = 0;
      if (paramPattern.href != null) {
        break label181;
      }
      if (i != 0) {
        break label193;
      }
      if (paramPattern.x != null) {
        break label296;
      }
      f1 = 0.0F;
      if (paramPattern.y != null) {
        break label310;
      }
      f2 = 0.0F;
      if (paramPattern.width != null) {
        break label324;
      }
      f3 = 0.0F;
      label52:
      if (paramPattern.height != null) {
        break label338;
      }
    }
    float f6;
    float f7;
    float f5;
    float f8;
    label181:
    label193:
    label203:
    label213:
    label223:
    label257:
    label270:
    label283:
    label296:
    label310:
    label324:
    label338:
    for (float f4 = 0.0F;; f4 = paramPattern.height.floatValue(this, 1.0F))
    {
      f6 = paramSvgElement.boundingBox.minX;
      f7 = paramSvgElement.boundingBox.width;
      f5 = paramSvgElement.boundingBox.minY;
      f8 = paramSvgElement.boundingBox.height;
      float f9 = paramSvgElement.boundingBox.width;
      f4 *= paramSvgElement.boundingBox.height;
      f3 *= f9;
      f5 = f2 * f8 + f5;
      f1 = f1 * f7 + f6;
      f2 = f4;
      if ((f3 != 0.0F) && (f2 != 0.0F)) {
        break label352;
      }
      return;
      if (!paramPattern.patternUnitsAreUser.booleanValue()) {
        break;
      }
      i = 1;
      break label10;
      fillInChainedPatternFields(paramPattern, paramPattern.href);
      break label17;
      if (paramPattern.x == null)
      {
        f1 = 0.0F;
        if (paramPattern.y != null) {
          break label257;
        }
        f2 = 0.0F;
        if (paramPattern.width != null) {
          break label270;
        }
        f3 = 0.0F;
        if (paramPattern.height != null) {
          break label283;
        }
      }
      for (f4 = 0.0F;; f4 = paramPattern.height.floatValueY(this))
      {
        f5 = f2;
        f2 = f4;
        break;
        f1 = paramPattern.x.floatValueX(this);
        break label203;
        f2 = paramPattern.y.floatValueY(this);
        break label213;
        f3 = paramPattern.width.floatValueX(this);
        break label223;
      }
      f1 = paramPattern.x.floatValue(this, 1.0F);
      break label32;
      f2 = paramPattern.y.floatValue(this, 1.0F);
      break label42;
      f3 = paramPattern.width.floatValue(this, 1.0F);
      break label52;
    }
    label352:
    PreserveAspectRatio localPreserveAspectRatio;
    if (paramPattern.preserveAspectRatio == null)
    {
      localPreserveAspectRatio = PreserveAspectRatio.LETTERBOX;
      statePush();
      this.canvas.clipPath(paramPath);
      paramPath = new RendererState();
      updateStyle(paramPath, SVG.Style.getDefaultStyle());
      paramPath.style.overflow = Boolean.valueOf(false);
      this.state = findInheritFromAncestorState(paramPattern, paramPath);
      paramPath = paramSvgElement.boundingBox;
      if (paramPattern.patternTransform != null) {
        break label523;
      }
      label427:
      f6 = (float)Math.floor((paramPath.minX - f1) / f3);
      f4 = f5 + (float)Math.floor((paramPath.minY - f5) / f2) * f2;
      f7 = paramPath.maxX();
      f8 = paramPath.maxY();
      paramPath = new SVG.Box(0.0F, 0.0F, f3, f2);
      if (f4 >= f8) {
        break label1089;
      }
    }
    label523:
    label909:
    label912:
    label995:
    label1016:
    label1032:
    label1089:
    for (int i = 1;; i = 0)
    {
      Object localObject;
      if (i == 0)
      {
        statePop();
        return;
        localPreserveAspectRatio = paramPattern.preserveAspectRatio;
        break;
        this.canvas.concat(paramPattern.patternTransform);
        localObject = new Matrix();
        if (!paramPattern.patternTransform.invert((Matrix)localObject)) {
          break label427;
        }
        paramPath = new float[8];
        paramPath[0] = paramSvgElement.boundingBox.minX;
        paramPath[1] = paramSvgElement.boundingBox.minY;
        paramPath[2] = paramSvgElement.boundingBox.maxX();
        paramPath[3] = paramSvgElement.boundingBox.minY;
        paramPath[4] = paramSvgElement.boundingBox.maxX();
        paramPath[5] = paramSvgElement.boundingBox.maxY();
        paramPath[6] = paramSvgElement.boundingBox.minX;
        paramPath[7] = paramSvgElement.boundingBox.maxY();
        ((Matrix)localObject).mapPoints(paramPath);
        localObject = new RectF(paramPath[0], paramPath[1], paramPath[0], paramPath[1]);
        i = 2;
        while (i <= 6)
        {
          if (paramPath[i] < ((RectF)localObject).left) {
            ((RectF)localObject).left = paramPath[i];
          }
          if (paramPath[i] > ((RectF)localObject).right) {
            ((RectF)localObject).right = paramPath[i];
          }
          if (paramPath[(i + 1)] < ((RectF)localObject).top) {
            ((RectF)localObject).top = paramPath[(i + 1)];
          }
          if (paramPath[(i + 1)] > ((RectF)localObject).bottom) {
            ((RectF)localObject).bottom = paramPath[(i + 1)];
          }
          i += 2;
        }
        paramPath = new SVG.Box(((RectF)localObject).left, ((RectF)localObject).top, ((RectF)localObject).right - ((RectF)localObject).left, ((RectF)localObject).bottom - ((RectF)localObject).top);
        break label427;
      }
      f5 = f6 * f3 + f1;
      if (f5 < f7) {}
      for (i = 1;; i = 0)
      {
        if (i == 0)
        {
          f4 += f2;
          break;
        }
        paramPath.minX = f5;
        paramPath.minY = f4;
        statePush();
        if (this.state.style.overflow.booleanValue())
        {
          if (paramPattern.viewBox != null) {
            break label995;
          }
          if (paramPattern.patternContentUnitsAreUser != null) {
            break label1016;
          }
          i = 1;
          this.canvas.translate(f5, f4);
          if (i == 0) {
            break label1032;
          }
        }
        boolean bool;
        for (;;)
        {
          bool = pushLayer();
          localObject = paramPattern.children.iterator();
          while (((Iterator)localObject).hasNext()) {
            render((SVG.SvgObject)((Iterator)localObject).next());
          }
          setClipRect(paramPath.minX, paramPath.minY, paramPath.width, paramPath.height);
          break;
          this.canvas.concat(calculateViewBoxTransform(paramPath, paramPattern.viewBox, localPreserveAspectRatio));
          continue;
          if (paramPattern.patternContentUnitsAreUser.booleanValue()) {
            break label909;
          }
          i = 0;
          break label912;
          this.canvas.scale(paramSvgElement.boundingBox.width, paramSvgElement.boundingBox.height);
        }
        if (!bool) {}
        for (;;)
        {
          statePop();
          f5 += f3;
          break;
          popLayer(paramPattern);
        }
      }
    }
  }
  
  private RendererState findInheritFromAncestorState(SVG.SvgObject paramSvgObject)
  {
    RendererState localRendererState = new RendererState();
    updateStyle(localRendererState, SVG.Style.getDefaultStyle());
    return findInheritFromAncestorState(paramSvgObject, localRendererState);
  }
  
  private RendererState findInheritFromAncestorState(SVG.SvgObject paramSvgObject, RendererState paramRendererState)
  {
    ArrayList localArrayList = new ArrayList();
    if (!(paramSvgObject instanceof SVG.SvgElementBase)) {}
    for (;;)
    {
      if (paramSvgObject.parent == null) {
        break label47;
      }
      paramSvgObject = (SVG.SvgObject)paramSvgObject.parent;
      break;
      localArrayList.add(0, (SVG.SvgElementBase)paramSvgObject);
    }
    label47:
    paramSvgObject = localArrayList.iterator();
    while (paramSvgObject.hasNext()) {
      updateStyleForElement(paramRendererState, (SVG.SvgElementBase)paramSvgObject.next());
    }
    paramRendererState.viewBox = this.document.getRootElement().viewBox;
    if (paramRendererState.viewBox != null) {}
    for (;;)
    {
      paramRendererState.viewPort = this.canvasViewPort;
      paramRendererState.directRendering = this.state.directRendering;
      return paramRendererState;
      paramRendererState.viewBox = this.canvasViewPort;
    }
  }
  
  private SVG.Style.TextAnchor getAnchorPosition()
  {
    if (this.state.style.direction == SVG.Style.TextDirection.LTR) {}
    while (this.state.style.textAnchor == SVG.Style.TextAnchor.Middle) {
      return this.state.style.textAnchor;
    }
    if (this.state.style.textAnchor != SVG.Style.TextAnchor.Start) {
      return SVG.Style.TextAnchor.Start;
    }
    return SVG.Style.TextAnchor.End;
  }
  
  private Path.FillType getClipRuleFromState()
  {
    if (this.state.style.clipRule != null) {}
    switch ($SWITCH_TABLE$com$caverock$androidsvg$SVG$Style$FillRule()[this.state.style.clipRule.ordinal()])
    {
    case 1: 
    default: 
      return Path.FillType.WINDING;
      return Path.FillType.WINDING;
    }
    return Path.FillType.EVEN_ODD;
  }
  
  private Path.FillType getFillTypeFromState()
  {
    if (this.state.style.fillRule != null) {}
    switch ($SWITCH_TABLE$com$caverock$androidsvg$SVG$Style$FillRule()[this.state.style.fillRule.ordinal()])
    {
    case 1: 
    default: 
      return Path.FillType.WINDING;
      return Path.FillType.WINDING;
    }
    return Path.FillType.EVEN_ODD;
  }
  
  private static void info(String paramString, Object... paramVarArgs)
  {
    Log.i("SVGAndroidRenderer", String.format(paramString, paramVarArgs));
  }
  
  private boolean isSpecified(SVG.Style paramStyle, long paramLong)
  {
    return (paramStyle.specifiedFlags & paramLong) != 0L;
  }
  
  private void makeLinearGradiant(boolean paramBoolean, SVG.Box paramBox, SVG.SvgLinearGradient paramSvgLinearGradient)
  {
    label14:
    int i;
    label17:
    Paint localPaint;
    label30:
    float f2;
    label45:
    float f3;
    label55:
    float f4;
    label65:
    float f1;
    float f6;
    float f5;
    label83:
    Object localObject;
    label110:
    label117:
    int j;
    int[] arrayOfInt;
    float[] arrayOfFloat;
    if (paramSvgLinearGradient.href == null)
    {
      if (paramSvgLinearGradient.gradientUnitsAreUser != null) {
        break label304;
      }
      i = 0;
      if (paramBoolean) {
        break label320;
      }
      localPaint = this.state.strokePaint;
      if (i != 0) {
        break label332;
      }
      if (paramSvgLinearGradient.x1 != null) {
        break label469;
      }
      f2 = 0.0F;
      if (paramSvgLinearGradient.y1 != null) {
        break label483;
      }
      f3 = 0.0F;
      if (paramSvgLinearGradient.x2 != null) {
        break label497;
      }
      f4 = 1.0F;
      if (paramSvgLinearGradient.y2 != null) {
        break label511;
      }
      f1 = 0.0F;
      f6 = f3;
      f5 = f2;
      statePush();
      this.state = findInheritFromAncestorState(paramSvgLinearGradient);
      localObject = new Matrix();
      if (i == 0) {
        break label533;
      }
      if (paramSvgLinearGradient.gradientTransform != null) {
        break label564;
      }
      j = paramSvgLinearGradient.children.size();
      if (j == 0) {
        break label577;
      }
      arrayOfInt = new int[j];
      arrayOfFloat = new float[j];
      f2 = -1.0F;
      Iterator localIterator = paramSvgLinearGradient.children.iterator();
      i = 0;
      label164:
      if (!localIterator.hasNext()) {
        break label633;
      }
      paramBox = (SVG.Stop)localIterator.next();
      if (i != 0) {
        break label603;
      }
      label193:
      arrayOfFloat[i] = paramBox.offset.floatValue();
      f2 = paramBox.offset.floatValue();
      label214:
      statePush();
      updateStyleForElement(this.state, paramBox);
      paramBox = (SVG.Colour)this.state.style.stopColor;
      if (paramBox == null) {
        break label626;
      }
    }
    for (;;)
    {
      int k = clamp255(this.state.style.stopOpacity.floatValue());
      arrayOfInt[i] = (paramBox.colour | k << 24);
      statePop();
      i += 1;
      break label164;
      fillInChainedGradientFields(paramSvgLinearGradient, paramSvgLinearGradient.href);
      break;
      label304:
      if (!paramSvgLinearGradient.gradientUnitsAreUser.booleanValue()) {
        break label14;
      }
      i = 1;
      break label17;
      label320:
      localPaint = this.state.fillPaint;
      break label30;
      label332:
      localObject = getCurrentViewPortInUserUnits();
      if (paramSvgLinearGradient.x1 == null)
      {
        f1 = 0.0F;
        label348:
        if (paramSvgLinearGradient.y1 != null) {
          break label414;
        }
        f2 = 0.0F;
        label358:
        if (paramSvgLinearGradient.x2 != null) {
          break label427;
        }
      }
      label414:
      label427:
      for (f3 = ((SVG.Box)localObject).width;; f3 = paramSvgLinearGradient.x2.floatValueX(this))
      {
        if (paramSvgLinearGradient.y2 != null) {
          break label440;
        }
        f7 = 0.0F;
        f5 = f1;
        f6 = f2;
        f4 = f3;
        f1 = f7;
        break;
        f1 = paramSvgLinearGradient.x1.floatValueX(this);
        break label348;
        f2 = paramSvgLinearGradient.y1.floatValueY(this);
        break label358;
      }
      label440:
      float f7 = paramSvgLinearGradient.y2.floatValueY(this);
      f5 = f1;
      f6 = f2;
      f4 = f3;
      f1 = f7;
      break label83;
      label469:
      f2 = paramSvgLinearGradient.x1.floatValue(this, 1.0F);
      break label45;
      label483:
      f3 = paramSvgLinearGradient.y1.floatValue(this, 1.0F);
      break label55;
      label497:
      f4 = paramSvgLinearGradient.x2.floatValue(this, 1.0F);
      break label65;
      label511:
      f1 = paramSvgLinearGradient.y2.floatValue(this, 1.0F);
      f5 = f2;
      f6 = f3;
      break label83;
      label533:
      ((Matrix)localObject).preTranslate(paramBox.minX, paramBox.minY);
      ((Matrix)localObject).preScale(paramBox.width, paramBox.height);
      break label110;
      label564:
      ((Matrix)localObject).preConcat(paramSvgLinearGradient.gradientTransform);
      break label117;
      label577:
      statePop();
      if (!paramBoolean)
      {
        this.state.hasStroke = false;
        return;
      }
      this.state.hasFill = false;
      return;
      label603:
      if (paramBox.offset.floatValue() >= f2) {
        break label193;
      }
      arrayOfFloat[i] = f2;
      break label214;
      label626:
      paramBox = SVG.Colour.BLACK;
    }
    label633:
    if (((f5 != f4) || (f6 != f1)) && (j != 1))
    {
      paramBox = Shader.TileMode.CLAMP;
      if (paramSvgLinearGradient.spreadMethod != null) {
        break label722;
      }
    }
    for (;;)
    {
      statePop();
      paramBox = new LinearGradient(f5, f6, f4, f1, arrayOfInt, arrayOfFloat, paramBox);
      paramBox.setLocalMatrix((Matrix)localObject);
      localPaint.setShader(paramBox);
      return;
      statePop();
      localPaint.setColor(arrayOfInt[(j - 1)]);
      return;
      label722:
      if (paramSvgLinearGradient.spreadMethod != SVG.GradientSpread.reflect)
      {
        if (paramSvgLinearGradient.spreadMethod == SVG.GradientSpread.repeat) {
          paramBox = Shader.TileMode.REPEAT;
        }
      }
      else {
        paramBox = Shader.TileMode.MIRROR;
      }
    }
  }
  
  private Path makePathAndBoundingBox(SVG.Circle paramCircle)
  {
    float f1;
    float f2;
    label18:
    float f7;
    float f3;
    float f4;
    float f5;
    float f6;
    if (paramCircle.cx == null)
    {
      f1 = 0.0F;
      if (paramCircle.cy != null) {
        break label175;
      }
      f2 = 0.0F;
      f7 = paramCircle.r.floatValue(this);
      f3 = f1 - f7;
      f4 = f2 - f7;
      f5 = f1 + f7;
      f6 = f2 + f7;
      if (paramCircle.boundingBox == null) {
        break label187;
      }
    }
    for (;;)
    {
      f7 *= 0.5522848F;
      paramCircle = new Path();
      paramCircle.moveTo(f1, f4);
      paramCircle.cubicTo(f1 + f7, f4, f5, f2 - f7, f5, f2);
      paramCircle.cubicTo(f5, f2 + f7, f1 + f7, f6, f1, f6);
      paramCircle.cubicTo(f1 - f7, f6, f3, f2 + f7, f3, f2);
      paramCircle.cubicTo(f3, f2 - f7, f1 - f7, f4, f1, f4);
      paramCircle.close();
      return paramCircle;
      f1 = paramCircle.cx.floatValueX(this);
      break;
      label175:
      f2 = paramCircle.cy.floatValueY(this);
      break label18;
      label187:
      paramCircle.boundingBox = new SVG.Box(f3, f4, 2.0F * f7, 2.0F * f7);
    }
  }
  
  private Path makePathAndBoundingBox(SVG.Ellipse paramEllipse)
  {
    float f1;
    float f2;
    label18:
    float f8;
    float f7;
    float f3;
    float f4;
    float f5;
    float f6;
    if (paramEllipse.cx == null)
    {
      f1 = 0.0F;
      if (paramEllipse.cy != null) {
        break label192;
      }
      f2 = 0.0F;
      f8 = paramEllipse.rx.floatValueX(this);
      f7 = paramEllipse.ry.floatValueY(this);
      f3 = f1 - f8;
      f4 = f2 - f7;
      f5 = f1 + f8;
      f6 = f2 + f7;
      if (paramEllipse.boundingBox == null) {
        break label204;
      }
    }
    for (;;)
    {
      f8 *= 0.5522848F;
      f7 *= 0.5522848F;
      paramEllipse = new Path();
      paramEllipse.moveTo(f1, f4);
      paramEllipse.cubicTo(f1 + f8, f4, f5, f2 - f7, f5, f2);
      paramEllipse.cubicTo(f5, f2 + f7, f1 + f8, f6, f1, f6);
      paramEllipse.cubicTo(f1 - f8, f6, f3, f2 + f7, f3, f2);
      paramEllipse.cubicTo(f3, f2 - f7, f1 - f8, f4, f1, f4);
      paramEllipse.close();
      return paramEllipse;
      f1 = paramEllipse.cx.floatValueX(this);
      break;
      label192:
      f2 = paramEllipse.cy.floatValueY(this);
      break label18;
      label204:
      paramEllipse.boundingBox = new SVG.Box(f3, f4, 2.0F * f8, 2.0F * f7);
    }
  }
  
  private Path makePathAndBoundingBox(SVG.Line paramLine)
  {
    float f4 = 0.0F;
    float f1;
    float f2;
    label35:
    float f3;
    if (paramLine.x1 != null)
    {
      f1 = paramLine.x1.floatValue(this);
      if (paramLine.y1 == null) {
        break label105;
      }
      f2 = paramLine.y1.floatValue(this);
      if (paramLine.x2 == null) {
        break label110;
      }
      f3 = paramLine.x2.floatValue(this);
      label52:
      if (paramLine.y2 != null) {
        f4 = paramLine.y2.floatValue(this);
      }
      if (paramLine.boundingBox == null) {
        break label116;
      }
    }
    for (;;)
    {
      paramLine = new Path();
      paramLine.moveTo(f1, f2);
      paramLine.lineTo(f3, f4);
      return paramLine;
      f1 = 0.0F;
      break;
      label105:
      f2 = 0.0F;
      break label35;
      label110:
      f3 = 0.0F;
      break label52;
      label116:
      paramLine.boundingBox = new SVG.Box(Math.min(f1, f2), Math.min(f2, f4), Math.abs(f3 - f1), Math.abs(f4 - f2));
    }
  }
  
  private Path makePathAndBoundingBox(SVG.PolyLine paramPolyLine)
  {
    Path localPath = new Path();
    localPath.moveTo(paramPolyLine.points[0], paramPolyLine.points[1]);
    int i = 2;
    while (i < paramPolyLine.points.length)
    {
      localPath.lineTo(paramPolyLine.points[i], paramPolyLine.points[(i + 1)]);
      i += 2;
    }
    if (!(paramPolyLine instanceof SVG.Polygon)) {
      if (paramPolyLine.boundingBox == null) {
        break label91;
      }
    }
    for (;;)
    {
      localPath.setFillType(getClipRuleFromState());
      return localPath;
      localPath.close();
      break;
      label91:
      paramPolyLine.boundingBox = calculatePathBounds(localPath);
    }
  }
  
  private Path makePathAndBoundingBox(SVG.Rect paramRect)
  {
    float f2;
    float f1;
    label39:
    float f3;
    float f4;
    label80:
    label89:
    float f5;
    float f6;
    if (paramRect.rx != null)
    {
      if (paramRect.rx == null) {
        break label204;
      }
      if (paramRect.ry == null) {
        break label218;
      }
      f2 = paramRect.rx.floatValueX(this);
      f1 = paramRect.ry.floatValueY(this);
      f3 = Math.min(f2, paramRect.width.floatValueX(this) / 2.0F);
      f4 = Math.min(f1, paramRect.height.floatValueY(this) / 2.0F);
      if (paramRect.x != null) {
        break label232;
      }
      f1 = 0.0F;
      if (paramRect.y != null) {
        break label244;
      }
      f2 = 0.0F;
      f5 = paramRect.width.floatValueX(this);
      f6 = paramRect.height.floatValueY(this);
      if (paramRect.boundingBox == null) {
        break label256;
      }
      label116:
      f5 = f1 + f5;
      f6 = f2 + f6;
      paramRect = new Path();
      if ((f3 != 0.0F) && (f4 != 0.0F)) {
        break label276;
      }
      paramRect.moveTo(f1, f2);
      paramRect.lineTo(f5, f2);
      paramRect.lineTo(f5, f6);
      paramRect.lineTo(f1, f6);
      paramRect.lineTo(f1, f2);
    }
    for (;;)
    {
      paramRect.close();
      return paramRect;
      if (paramRect.ry != null) {
        break;
      }
      f2 = 0.0F;
      f1 = 0.0F;
      break label39;
      label204:
      f1 = paramRect.ry.floatValueY(this);
      f2 = f1;
      break label39;
      label218:
      f1 = paramRect.rx.floatValueX(this);
      f2 = f1;
      break label39;
      label232:
      f1 = paramRect.x.floatValueX(this);
      break label80;
      label244:
      f2 = paramRect.y.floatValueY(this);
      break label89;
      label256:
      paramRect.boundingBox = new SVG.Box(f1, f2, f5, f6);
      break label116;
      label276:
      float f7 = f3 * 0.5522848F;
      float f8 = f4 * 0.5522848F;
      paramRect.moveTo(f1, f2 + f4);
      paramRect.cubicTo(f1, f2 + f4 - f8, f1 + f3 - f7, f2, f1 + f3, f2);
      paramRect.lineTo(f5 - f3, f2);
      paramRect.cubicTo(f5 - f3 + f7, f2, f5, f2 + f4 - f8, f5, f2 + f4);
      paramRect.lineTo(f5, f6 - f4);
      paramRect.cubicTo(f5, f6 - f4 + f8, f5 - f3 + f7, f6, f5 - f3, f6);
      paramRect.lineTo(f1 + f3, f6);
      paramRect.cubicTo(f1 + f3 - f7, f6, f1, f6 - f4 + f8, f1, f6 - f4);
      paramRect.lineTo(f1, f2 + f4);
    }
  }
  
  private void makeRadialGradiant(boolean paramBoolean, SVG.Box paramBox, SVG.SvgRadialGradient paramSvgRadialGradient)
  {
    label14:
    int i;
    label17:
    Paint localPaint;
    label30:
    float f2;
    label47:
    float f3;
    label59:
    float f1;
    float f4;
    label75:
    Object localObject;
    label102:
    label109:
    int j;
    int[] arrayOfInt;
    float[] arrayOfFloat;
    if (paramSvgRadialGradient.href == null)
    {
      if (paramSvgRadialGradient.gradientUnitsAreUser != null) {
        break label296;
      }
      i = 0;
      if (paramBoolean) {
        break label312;
      }
      localPaint = this.state.strokePaint;
      if (i != 0) {
        break label324;
      }
      if (paramSvgRadialGradient.cx != null) {
        break label450;
      }
      f2 = 0.5F;
      if (paramSvgRadialGradient.cy != null) {
        break label464;
      }
      f3 = 0.5F;
      if (paramSvgRadialGradient.r != null) {
        break label478;
      }
      f1 = 0.5F;
      f4 = f2;
      statePush();
      this.state = findInheritFromAncestorState(paramSvgRadialGradient);
      localObject = new Matrix();
      if (i == 0) {
        break label496;
      }
      if (paramSvgRadialGradient.gradientTransform != null) {
        break label527;
      }
      j = paramSvgRadialGradient.children.size();
      if (j == 0) {
        break label540;
      }
      arrayOfInt = new int[j];
      arrayOfFloat = new float[j];
      f2 = -1.0F;
      Iterator localIterator = paramSvgRadialGradient.children.iterator();
      i = 0;
      label156:
      if (!localIterator.hasNext()) {
        break label596;
      }
      paramBox = (SVG.Stop)localIterator.next();
      if (i != 0) {
        break label566;
      }
      label185:
      arrayOfFloat[i] = paramBox.offset.floatValue();
      f2 = paramBox.offset.floatValue();
      label206:
      statePush();
      updateStyleForElement(this.state, paramBox);
      paramBox = (SVG.Colour)this.state.style.stopColor;
      if (paramBox == null) {
        break label589;
      }
    }
    for (;;)
    {
      int k = clamp255(this.state.style.stopOpacity.floatValue());
      arrayOfInt[i] = (paramBox.colour | k << 24);
      statePop();
      i += 1;
      break label156;
      fillInChainedGradientFields(paramSvgRadialGradient, paramSvgRadialGradient.href);
      break;
      label296:
      if (!paramSvgRadialGradient.gradientUnitsAreUser.booleanValue()) {
        break label14;
      }
      i = 1;
      break label17;
      label312:
      localPaint = this.state.fillPaint;
      break label30;
      label324:
      localObject = new SVG.Length(50.0F, SVG.Unit.percent);
      if (paramSvgRadialGradient.cx == null)
      {
        f1 = ((SVG.Length)localObject).floatValueX(this);
        label354:
        if (paramSvgRadialGradient.cy != null) {
          break label412;
        }
      }
      label412:
      for (f2 = ((SVG.Length)localObject).floatValueY(this);; f2 = paramSvgRadialGradient.cy.floatValueY(this))
      {
        if (paramSvgRadialGradient.r != null) {
          break label425;
        }
        f5 = ((SVG.Length)localObject).floatValue(this);
        f4 = f1;
        f3 = f2;
        f1 = f5;
        break;
        f1 = paramSvgRadialGradient.cx.floatValueX(this);
        break label354;
      }
      label425:
      float f5 = paramSvgRadialGradient.r.floatValue(this);
      f4 = f1;
      f3 = f2;
      f1 = f5;
      break label75;
      label450:
      f2 = paramSvgRadialGradient.cx.floatValue(this, 1.0F);
      break label47;
      label464:
      f3 = paramSvgRadialGradient.cy.floatValue(this, 1.0F);
      break label59;
      label478:
      f1 = paramSvgRadialGradient.r.floatValue(this, 1.0F);
      f4 = f2;
      break label75;
      label496:
      ((Matrix)localObject).preTranslate(paramBox.minX, paramBox.minY);
      ((Matrix)localObject).preScale(paramBox.width, paramBox.height);
      break label102;
      label527:
      ((Matrix)localObject).preConcat(paramSvgRadialGradient.gradientTransform);
      break label109;
      label540:
      statePop();
      if (!paramBoolean)
      {
        this.state.hasStroke = false;
        return;
      }
      this.state.hasFill = false;
      return;
      label566:
      if (paramBox.offset.floatValue() >= f2) {
        break label185;
      }
      arrayOfFloat[i] = f2;
      break label206;
      label589:
      paramBox = SVG.Colour.BLACK;
    }
    label596:
    if ((f1 != 0.0F) && (j != 1))
    {
      paramBox = Shader.TileMode.CLAMP;
      if (paramSvgRadialGradient.spreadMethod != null) {
        break label674;
      }
    }
    for (;;)
    {
      statePop();
      paramBox = new RadialGradient(f4, f3, f1, arrayOfInt, arrayOfFloat, paramBox);
      paramBox.setLocalMatrix((Matrix)localObject);
      localPaint.setShader(paramBox);
      return;
      statePop();
      localPaint.setColor(arrayOfInt[(j - 1)]);
      return;
      label674:
      if (paramSvgRadialGradient.spreadMethod != SVG.GradientSpread.reflect)
      {
        if (paramSvgRadialGradient.spreadMethod == SVG.GradientSpread.repeat) {
          paramBox = Shader.TileMode.REPEAT;
        }
      }
      else {
        paramBox = Shader.TileMode.MIRROR;
      }
    }
  }
  
  private void parentPop()
  {
    this.parentStack.pop();
    this.matrixStack.pop();
  }
  
  private void parentPush(SVG.SvgContainer paramSvgContainer)
  {
    this.parentStack.push(paramSvgContainer);
    this.matrixStack.push(this.canvas.getMatrix());
  }
  
  private void popLayer(SVG.SvgElement paramSvgElement)
  {
    if (this.state.style.mask == null) {}
    for (;;)
    {
      statePop();
      return;
      if (this.state.directRendering)
      {
        SVG.SvgObject localSvgObject = this.document.resolveIRI(this.state.style.mask);
        duplicateCanvas();
        renderMask((SVG.Mask)localSvgObject, paramSvgElement);
        paramSvgElement = processMaskBitmaps();
        this.canvas = ((Canvas)this.canvasStack.pop());
        this.canvas.save();
        this.canvas.setMatrix(new Matrix());
        this.canvas.drawBitmap(paramSvgElement, 0.0F, 0.0F, this.state.fillPaint);
        paramSvgElement.recycle();
        this.canvas.restore();
      }
    }
  }
  
  private Bitmap processMaskBitmaps()
  {
    Bitmap localBitmap1 = (Bitmap)this.bitmapStack.pop();
    Bitmap localBitmap2 = (Bitmap)this.bitmapStack.pop();
    int k = localBitmap1.getWidth();
    int m = localBitmap1.getHeight();
    int[] arrayOfInt1 = new int[k];
    int[] arrayOfInt2 = new int[k];
    int i = 0;
    while (i < m)
    {
      localBitmap1.getPixels(arrayOfInt1, 0, k, 0, i, k, 1);
      localBitmap2.getPixels(arrayOfInt2, 0, k, 0, i, k, 1);
      int j = 0;
      if (j < k)
      {
        int n = arrayOfInt1[j];
        int i1 = n >> 24 & 0xFF;
        if (i1 != 0)
        {
          n = i1 * ((n & 0xFF) * 2362 + ((n >> 8 & 0xFF) * 23442 + (n >> 16 & 0xFF) * 6963)) / 8355840;
          i1 = arrayOfInt2[j];
          arrayOfInt2[j] = (n * (i1 >> 24 & 0xFF) / 255 << 24 | i1 & 0xFFFFFF);
        }
        for (;;)
        {
          j += 1;
          break;
          arrayOfInt2[j] = 0;
        }
      }
      localBitmap2.setPixels(arrayOfInt2, 0, k, 0, i, k, 1);
      i += 1;
    }
    localBitmap1.recycle();
    return localBitmap2;
  }
  
  private void processTextChild(SVG.SvgObject paramSvgObject, TextProcessor paramTextProcessor)
  {
    float f8 = 0.0F;
    if (paramTextProcessor.doTextContainer((SVG.TextContainer)paramSvgObject))
    {
      if (!(paramSvgObject instanceof SVG.TextPath))
      {
        if ((paramSvgObject instanceof SVG.TSpan)) {
          break label54;
        }
        if ((paramSvgObject instanceof SVG.TRef)) {
          break label408;
        }
      }
    }
    else {
      return;
    }
    statePush();
    renderTextPath((SVG.TextPath)paramSvgObject);
    statePop();
    return;
    label54:
    debug("TSpan render", new Object[0]);
    statePush();
    paramSvgObject = (SVG.TSpan)paramSvgObject;
    updateStyleForElement(this.state, paramSvgObject);
    if (!display())
    {
      statePop();
      return;
    }
    float f4;
    float f6;
    float f7;
    float f5;
    if (!(paramTextProcessor instanceof PlainTextDrawer))
    {
      f4 = 0.0F;
      f6 = 0.0F;
      f7 = 0.0F;
      f5 = f8;
      checkForGradiantsAndPatterns((SVG.SvgElement)paramSvgObject.getTextRoot());
      if ((paramTextProcessor instanceof PlainTextDrawer)) {
        break label381;
      }
    }
    for (;;)
    {
      boolean bool = pushLayer();
      enumerateTextSpans(paramSvgObject, paramTextProcessor);
      if (!bool) {
        break;
      }
      popLayer(paramSvgObject);
      break;
      label164:
      float f1;
      label172:
      label179:
      float f2;
      if (paramSvgObject.x == null)
      {
        f1 = ((PlainTextDrawer)paramTextProcessor).x;
        if (paramSvgObject.y != null) {
          break label313;
        }
        f2 = ((PlainTextDrawer)paramTextProcessor).y;
        label188:
        if (paramSvgObject.dx != null) {
          break label347;
        }
      }
      label195:
      for (float f3 = 0.0F;; f3 = ((SVG.Length)paramSvgObject.dx.get(0)).floatValueX(this))
      {
        f7 = f1;
        f6 = f2;
        f4 = f3;
        f5 = f8;
        if (paramSvgObject.dy == null) {
          break;
        }
        f7 = f1;
        f6 = f2;
        f4 = f3;
        f5 = f8;
        if (paramSvgObject.dy.size() == 0) {
          break;
        }
        f5 = ((SVG.Length)paramSvgObject.dy.get(0)).floatValueY(this);
        f7 = f1;
        f6 = f2;
        f4 = f3;
        break;
        if (paramSvgObject.x.size() == 0) {
          break label164;
        }
        f1 = ((SVG.Length)paramSvgObject.x.get(0)).floatValueX(this);
        break label172;
        label313:
        if (paramSvgObject.y.size() == 0) {
          break label179;
        }
        f2 = ((SVG.Length)paramSvgObject.y.get(0)).floatValueY(this);
        break label188;
        label347:
        if (paramSvgObject.dx.size() == 0) {
          break label195;
        }
      }
      label381:
      ((PlainTextDrawer)paramTextProcessor).x = (f7 + f4);
      ((PlainTextDrawer)paramTextProcessor).y = (f6 + f5);
    }
    label408:
    statePush();
    Object localObject = (SVG.TRef)paramSvgObject;
    updateStyleForElement(this.state, (SVG.SvgElementBase)localObject);
    if (!display()) {}
    for (;;)
    {
      statePop();
      return;
      checkForGradiantsAndPatterns((SVG.SvgElement)((SVG.TRef)localObject).getTextRoot());
      paramSvgObject = paramSvgObject.document.resolveIRI(((SVG.TRef)localObject).href);
      if (paramSvgObject == null) {}
      while (!(paramSvgObject instanceof SVG.TextContainer))
      {
        error("Tref reference '%s' not found", new Object[] { ((SVG.TRef)localObject).href });
        break;
      }
      localObject = new StringBuilder();
      extractRawText((SVG.TextContainer)paramSvgObject, (StringBuilder)localObject);
      if (((StringBuilder)localObject).length() > 0) {
        paramTextProcessor.processText(((StringBuilder)localObject).toString());
      }
    }
  }
  
  private boolean pushLayer()
  {
    if (requiresCompositing())
    {
      this.canvas.saveLayerAlpha(null, clamp255(this.state.style.opacity.floatValue()), 4);
      this.stateStack.push(this.state);
      this.state = ((RendererState)this.state.clone());
      if (this.state.style.mask != null) {
        break label77;
      }
    }
    label77:
    while (!this.state.directRendering)
    {
      return true;
      return false;
    }
    SVG.SvgObject localSvgObject = this.document.resolveIRI(this.state.style.mask);
    if (localSvgObject == null) {}
    while (!(localSvgObject instanceof SVG.Mask))
    {
      error("Mask reference '%s' not found", new Object[] { this.state.style.mask });
      this.state.style.mask = null;
      return true;
    }
    this.canvasStack.push(this.canvas);
    duplicateCanvas();
    return true;
  }
  
  private void render(SVG.Circle paramCircle)
  {
    debug("Circle render", new Object[0]);
    if (paramCircle.r == null) {}
    while (paramCircle.r.isZero()) {
      return;
    }
    updateStyleForElement(this.state, paramCircle);
    Path localPath;
    boolean bool;
    if (display())
    {
      if (!visible()) {
        break label110;
      }
      if (paramCircle.transform != null) {
        break label111;
      }
      localPath = makePathAndBoundingBox(paramCircle);
      updateParentBoundingBox(paramCircle);
      checkForGradiantsAndPatterns(paramCircle);
      checkForClipPath(paramCircle);
      bool = pushLayer();
      if (this.state.hasFill) {
        break label125;
      }
      label94:
      if (this.state.hasStroke) {
        break label134;
      }
    }
    for (;;)
    {
      if (bool) {
        break label142;
      }
      return;
      return;
      label110:
      return;
      label111:
      this.canvas.concat(paramCircle.transform);
      break;
      label125:
      doFilledPath(paramCircle, localPath);
      break label94;
      label134:
      doStroke(localPath);
    }
    label142:
    popLayer(paramCircle);
  }
  
  private void render(SVG.Ellipse paramEllipse)
  {
    debug("Ellipse render", new Object[0]);
    if (paramEllipse.rx == null) {}
    while ((paramEllipse.ry == null) || (paramEllipse.rx.isZero()) || (paramEllipse.ry.isZero())) {
      return;
    }
    updateStyleForElement(this.state, paramEllipse);
    Path localPath;
    boolean bool;
    if (display())
    {
      if (!visible()) {
        break label127;
      }
      if (paramEllipse.transform != null) {
        break label128;
      }
      localPath = makePathAndBoundingBox(paramEllipse);
      updateParentBoundingBox(paramEllipse);
      checkForGradiantsAndPatterns(paramEllipse);
      checkForClipPath(paramEllipse);
      bool = pushLayer();
      if (this.state.hasFill) {
        break label142;
      }
      label111:
      if (this.state.hasStroke) {
        break label151;
      }
    }
    for (;;)
    {
      if (bool) {
        break label159;
      }
      return;
      return;
      label127:
      return;
      label128:
      this.canvas.concat(paramEllipse.transform);
      break;
      label142:
      doFilledPath(paramEllipse, localPath);
      break label111;
      label151:
      doStroke(localPath);
    }
    label159:
    popLayer(paramEllipse);
  }
  
  private void render(SVG.Group paramGroup)
  {
    debug("Group render", new Object[0]);
    updateStyleForElement(this.state, paramGroup);
    if (display())
    {
      if (paramGroup.transform != null) {
        break label60;
      }
      checkForClipPath(paramGroup);
      boolean bool = pushLayer();
      renderChildren(paramGroup, true);
      if (bool) {
        break label74;
      }
    }
    for (;;)
    {
      updateParentBoundingBox(paramGroup);
      return;
      return;
      label60:
      this.canvas.concat(paramGroup.transform);
      break;
      label74:
      popLayer(paramGroup);
    }
  }
  
  private void render(SVG.Image paramImage)
  {
    debug("Image render", new Object[0]);
    if (paramImage.width == null) {}
    while ((paramImage.width.isZero()) || (paramImage.height == null) || (paramImage.height.isZero())) {
      return;
    }
    PreserveAspectRatio localPreserveAspectRatio;
    Object localObject;
    label79:
    label114:
    float f1;
    label123:
    float f2;
    if (paramImage.href != null)
    {
      if (paramImage.preserveAspectRatio != null) {
        break label282;
      }
      localPreserveAspectRatio = PreserveAspectRatio.LETTERBOX;
      localObject = checkForImageDataURL(paramImage.href);
      if (localObject == null) {
        break label291;
      }
      if (localObject == null) {
        break label320;
      }
      updateStyleForElement(this.state, paramImage);
      if (!display()) {
        break label338;
      }
      if (!visible()) {
        break label339;
      }
      if (paramImage.transform != null) {
        break label340;
      }
      if (paramImage.x != null) {
        break label354;
      }
      f1 = 0.0F;
      if (paramImage.y != null) {
        break label366;
      }
      f2 = 0.0F;
      label132:
      float f3 = paramImage.width.floatValueX(this);
      float f4 = paramImage.height.floatValueX(this);
      this.state.viewPort = new SVG.Box(f1, f2, f3, f4);
      if (!this.state.style.overflow.booleanValue()) {
        break label378;
      }
    }
    for (;;)
    {
      paramImage.boundingBox = new SVG.Box(0.0F, 0.0F, ((Bitmap)localObject).getWidth(), ((Bitmap)localObject).getHeight());
      this.canvas.concat(calculateViewBoxTransform(this.state.viewPort, paramImage.boundingBox, localPreserveAspectRatio));
      updateParentBoundingBox(paramImage);
      checkForClipPath(paramImage);
      boolean bool = pushLayer();
      viewportFill();
      this.canvas.drawBitmap((Bitmap)localObject, 0.0F, 0.0F, this.state.fillPaint);
      if (bool) {
        break label425;
      }
      return;
      return;
      label282:
      localPreserveAspectRatio = paramImage.preserveAspectRatio;
      break;
      label291:
      localObject = this.document.getFileResolver();
      if (localObject != null)
      {
        localObject = ((SVGExternalFileResolver)localObject).resolveImage(paramImage.href);
        break label79;
      }
      return;
      label320:
      error("Could not locate image '%s'", new Object[] { paramImage.href });
      return;
      label338:
      return;
      label339:
      return;
      label340:
      this.canvas.concat(paramImage.transform);
      break label114;
      label354:
      f1 = paramImage.x.floatValueX(this);
      break label123;
      label366:
      f2 = paramImage.y.floatValueY(this);
      break label132;
      label378:
      setClipRect(this.state.viewPort.minX, this.state.viewPort.minY, this.state.viewPort.width, this.state.viewPort.height);
    }
    label425:
    popLayer(paramImage);
  }
  
  private void render(SVG.Line paramLine)
  {
    debug("Line render", new Object[0]);
    updateStyleForElement(this.state, paramLine);
    if (display())
    {
      if (!visible()) {
        break label92;
      }
      if (!this.state.hasStroke) {
        break label93;
      }
      if (paramLine.transform != null) {
        break label94;
      }
    }
    for (;;)
    {
      Path localPath = makePathAndBoundingBox(paramLine);
      updateParentBoundingBox(paramLine);
      checkForGradiantsAndPatterns(paramLine);
      checkForClipPath(paramLine);
      boolean bool = pushLayer();
      doStroke(localPath);
      renderMarkers(paramLine);
      if (bool) {
        break;
      }
      return;
      return;
      label92:
      return;
      label93:
      return;
      label94:
      this.canvas.concat(paramLine.transform);
    }
    popLayer(paramLine);
  }
  
  private void render(SVG.Path paramPath)
  {
    debug("Path render", new Object[0]);
    updateStyleForElement(this.state, paramPath);
    label50:
    Path localPath;
    label73:
    boolean bool;
    if (display())
    {
      if (!visible()) {
        break label124;
      }
      if (!this.state.hasStroke) {
        break label125;
      }
      if (paramPath.transform != null) {
        break label136;
      }
      localPath = new PathConverter(paramPath.d).getPath();
      if (paramPath.boundingBox == null) {
        break label150;
      }
      updateParentBoundingBox(paramPath);
      checkForGradiantsAndPatterns(paramPath);
      checkForClipPath(paramPath);
      bool = pushLayer();
      if (this.state.hasFill) {
        break label162;
      }
      label103:
      if (this.state.hasStroke) {
        break label179;
      }
    }
    for (;;)
    {
      renderMarkers(paramPath);
      if (bool) {
        break label187;
      }
      return;
      return;
      label124:
      return;
      label125:
      if (this.state.hasFill) {
        break;
      }
      return;
      label136:
      this.canvas.concat(paramPath.transform);
      break label50;
      label150:
      paramPath.boundingBox = calculatePathBounds(localPath);
      break label73;
      label162:
      localPath.setFillType(getFillTypeFromState());
      doFilledPath(paramPath, localPath);
      break label103;
      label179:
      doStroke(localPath);
    }
    label187:
    popLayer(paramPath);
  }
  
  private void render(SVG.PolyLine paramPolyLine)
  {
    debug("PolyLine render", new Object[0]);
    updateStyleForElement(this.state, paramPolyLine);
    label50:
    Path localPath;
    boolean bool;
    if (display())
    {
      if (!visible()) {
        break label116;
      }
      if (!this.state.hasStroke) {
        break label117;
      }
      if (paramPolyLine.transform != null) {
        break label128;
      }
      if (paramPolyLine.points.length < 2) {
        break label142;
      }
      localPath = makePathAndBoundingBox(paramPolyLine);
      updateParentBoundingBox(paramPolyLine);
      checkForGradiantsAndPatterns(paramPolyLine);
      checkForClipPath(paramPolyLine);
      bool = pushLayer();
      if (this.state.hasFill) {
        break label143;
      }
      label95:
      if (this.state.hasStroke) {
        break label152;
      }
    }
    for (;;)
    {
      renderMarkers(paramPolyLine);
      if (bool) {
        break label160;
      }
      return;
      return;
      label116:
      return;
      label117:
      if (this.state.hasFill) {
        break;
      }
      return;
      label128:
      this.canvas.concat(paramPolyLine.transform);
      break label50;
      label142:
      return;
      label143:
      doFilledPath(paramPolyLine, localPath);
      break label95;
      label152:
      doStroke(localPath);
    }
    label160:
    popLayer(paramPolyLine);
  }
  
  private void render(SVG.Polygon paramPolygon)
  {
    debug("Polygon render", new Object[0]);
    updateStyleForElement(this.state, paramPolygon);
    label50:
    Path localPath;
    boolean bool;
    if (display())
    {
      if (!visible()) {
        break label116;
      }
      if (!this.state.hasStroke) {
        break label117;
      }
      if (paramPolygon.transform != null) {
        break label128;
      }
      if (paramPolygon.points.length < 2) {
        break label142;
      }
      localPath = makePathAndBoundingBox(paramPolygon);
      updateParentBoundingBox(paramPolygon);
      checkForGradiantsAndPatterns(paramPolygon);
      checkForClipPath(paramPolygon);
      bool = pushLayer();
      if (this.state.hasFill) {
        break label143;
      }
      label95:
      if (this.state.hasStroke) {
        break label152;
      }
    }
    for (;;)
    {
      renderMarkers(paramPolygon);
      if (bool) {
        break label160;
      }
      return;
      return;
      label116:
      return;
      label117:
      if (this.state.hasFill) {
        break;
      }
      return;
      label128:
      this.canvas.concat(paramPolygon.transform);
      break label50;
      label142:
      return;
      label143:
      doFilledPath(paramPolygon, localPath);
      break label95;
      label152:
      doStroke(localPath);
    }
    label160:
    popLayer(paramPolygon);
  }
  
  private void render(SVG.Rect paramRect)
  {
    debug("Rect render", new Object[0]);
    if (paramRect.width == null) {}
    while ((paramRect.height == null) || (paramRect.width.isZero()) || (paramRect.height.isZero())) {
      return;
    }
    updateStyleForElement(this.state, paramRect);
    Path localPath;
    boolean bool;
    if (display())
    {
      if (!visible()) {
        break label127;
      }
      if (paramRect.transform != null) {
        break label128;
      }
      localPath = makePathAndBoundingBox(paramRect);
      updateParentBoundingBox(paramRect);
      checkForGradiantsAndPatterns(paramRect);
      checkForClipPath(paramRect);
      bool = pushLayer();
      if (this.state.hasFill) {
        break label142;
      }
      label111:
      if (this.state.hasStroke) {
        break label151;
      }
    }
    for (;;)
    {
      if (bool) {
        break label159;
      }
      return;
      return;
      label127:
      return;
      label128:
      this.canvas.concat(paramRect.transform);
      break;
      label142:
      doFilledPath(paramRect, localPath);
      break label111;
      label151:
      doStroke(localPath);
    }
    label159:
    popLayer(paramRect);
  }
  
  private void render(SVG.Svg paramSvg)
  {
    render(paramSvg, paramSvg.width, paramSvg.height);
  }
  
  private void render(SVG.Svg paramSvg, SVG.Length paramLength1, SVG.Length paramLength2)
  {
    render(paramSvg, paramLength1, paramLength2, paramSvg.viewBox, paramSvg.preserveAspectRatio);
  }
  
  private void render(SVG.Svg paramSvg, SVG.Length paramLength1, SVG.Length paramLength2, SVG.Box paramBox, PreserveAspectRatio paramPreserveAspectRatio)
  {
    float f3 = 0.0F;
    debug("Svg render", new Object[0]);
    label21:
    float f2;
    float f1;
    label69:
    float f4;
    if (paramLength1 == null)
    {
      if (paramLength2 != null) {
        break label170;
      }
      if (paramPreserveAspectRatio == null) {
        break label180;
      }
      updateStyleForElement(this.state, paramSvg);
      if (!display()) {
        break label204;
      }
      if (paramSvg.parent != null) {
        break label205;
      }
      f2 = 0.0F;
      SVG.Box localBox = getCurrentViewPortInUserUnits();
      if (paramLength1 != null) {
        break label256;
      }
      f1 = localBox.width;
      if (paramLength2 != null) {
        break label266;
      }
      f4 = localBox.height;
      label80:
      this.state.viewPort = new SVG.Box(f2, f3, f1, f4);
      if (!this.state.style.overflow.booleanValue()) {
        break label276;
      }
      label118:
      checkForClipPath(paramSvg, this.state.viewPort);
      if (paramBox != null) {
        break label323;
      }
      label135:
      boolean bool = pushLayer();
      viewportFill();
      renderChildren(paramSvg, true);
      if (bool) {
        break label359;
      }
    }
    for (;;)
    {
      updateParentBoundingBox(paramSvg);
      return;
      if (!paramLength1.isZero()) {
        break;
      }
      label170:
      do
      {
        return;
      } while (paramLength2.isZero());
      break label21;
      label180:
      if (paramSvg.preserveAspectRatio == null) {}
      for (paramPreserveAspectRatio = PreserveAspectRatio.LETTERBOX;; paramPreserveAspectRatio = paramSvg.preserveAspectRatio) {
        break;
      }
      label204:
      return;
      label205:
      if (paramSvg.x == null) {}
      for (f1 = 0.0F;; f1 = paramSvg.x.floatValueX(this))
      {
        f2 = f1;
        if (paramSvg.y == null) {
          break;
        }
        f3 = paramSvg.y.floatValueY(this);
        f2 = f1;
        break;
      }
      label256:
      f1 = paramLength1.floatValueX(this);
      break label69;
      label266:
      f4 = paramLength2.floatValueY(this);
      break label80;
      label276:
      setClipRect(this.state.viewPort.minX, this.state.viewPort.minY, this.state.viewPort.width, this.state.viewPort.height);
      break label118;
      label323:
      this.canvas.concat(calculateViewBoxTransform(this.state.viewPort, paramBox, paramPreserveAspectRatio));
      this.state.viewBox = paramSvg.viewBox;
      break label135;
      label359:
      popLayer(paramSvg);
    }
  }
  
  private void render(SVG.SvgObject paramSvgObject)
  {
    if (!(paramSvgObject instanceof SVG.NotDirectlyRendered))
    {
      statePush();
      checkXMLSpaceAttribute(paramSvgObject);
      if ((paramSvgObject instanceof SVG.Svg)) {
        break label113;
      }
      if ((paramSvgObject instanceof SVG.Use)) {
        break label124;
      }
      if ((paramSvgObject instanceof SVG.Switch)) {
        break label135;
      }
      if ((paramSvgObject instanceof SVG.Group)) {
        break label146;
      }
      if ((paramSvgObject instanceof SVG.Image)) {
        break label157;
      }
      if ((paramSvgObject instanceof SVG.Path)) {
        break label168;
      }
      if ((paramSvgObject instanceof SVG.Rect)) {
        break label179;
      }
      if ((paramSvgObject instanceof SVG.Circle)) {
        break label190;
      }
      if ((paramSvgObject instanceof SVG.Ellipse)) {
        break label201;
      }
      if ((paramSvgObject instanceof SVG.Line)) {
        break label212;
      }
      if ((paramSvgObject instanceof SVG.Polygon)) {
        break label223;
      }
      if ((paramSvgObject instanceof SVG.PolyLine)) {
        break label234;
      }
      if ((paramSvgObject instanceof SVG.Text)) {
        break label245;
      }
    }
    for (;;)
    {
      statePop();
      return;
      return;
      label113:
      render((SVG.Svg)paramSvgObject);
      continue;
      label124:
      render((SVG.Use)paramSvgObject);
      continue;
      label135:
      render((SVG.Switch)paramSvgObject);
      continue;
      label146:
      render((SVG.Group)paramSvgObject);
      continue;
      label157:
      render((SVG.Image)paramSvgObject);
      continue;
      label168:
      render((SVG.Path)paramSvgObject);
      continue;
      label179:
      render((SVG.Rect)paramSvgObject);
      continue;
      label190:
      render((SVG.Circle)paramSvgObject);
      continue;
      label201:
      render((SVG.Ellipse)paramSvgObject);
      continue;
      label212:
      render((SVG.Line)paramSvgObject);
      continue;
      label223:
      render((SVG.Polygon)paramSvgObject);
      continue;
      label234:
      render((SVG.PolyLine)paramSvgObject);
      continue;
      label245:
      render((SVG.Text)paramSvgObject);
    }
  }
  
  private void render(SVG.Switch paramSwitch)
  {
    debug("Switch render", new Object[0]);
    updateStyleForElement(this.state, paramSwitch);
    if (display())
    {
      if (paramSwitch.transform != null) {
        break label59;
      }
      checkForClipPath(paramSwitch);
      boolean bool = pushLayer();
      renderSwitchChild(paramSwitch);
      if (bool) {
        break label73;
      }
    }
    for (;;)
    {
      updateParentBoundingBox(paramSwitch);
      return;
      return;
      label59:
      this.canvas.concat(paramSwitch.transform);
      break;
      label73:
      popLayer(paramSwitch);
    }
  }
  
  private void render(SVG.Symbol paramSymbol, SVG.Length paramLength1, SVG.Length paramLength2)
  {
    debug("Symbol render", new Object[0]);
    label18:
    PreserveAspectRatio localPreserveAspectRatio;
    label30:
    float f1;
    label55:
    float f2;
    if (paramLength1 == null)
    {
      if (paramLength2 != null) {
        break label145;
      }
      if (paramSymbol.preserveAspectRatio != null) {
        break label155;
      }
      localPreserveAspectRatio = PreserveAspectRatio.LETTERBOX;
      updateStyleForElement(this.state, paramSymbol);
      if (paramLength1 != null) {
        break label164;
      }
      f1 = this.state.viewPort.width;
      if (paramLength2 != null) {
        break label174;
      }
      f2 = this.state.viewPort.height;
      label71:
      this.state.viewPort = new SVG.Box(0.0F, 0.0F, f1, f2);
      if (!this.state.style.overflow.booleanValue()) {
        break label184;
      }
      label107:
      if (paramSymbol.viewBox != null) {
        break label231;
      }
      label114:
      boolean bool = pushLayer();
      renderChildren(paramSymbol, true);
      if (bool) {
        break label269;
      }
    }
    for (;;)
    {
      updateParentBoundingBox(paramSymbol);
      return;
      if (!paramLength1.isZero()) {
        break;
      }
      label145:
      do
      {
        return;
      } while (paramLength2.isZero());
      break label18;
      label155:
      localPreserveAspectRatio = paramSymbol.preserveAspectRatio;
      break label30;
      label164:
      f1 = paramLength1.floatValueX(this);
      break label55;
      label174:
      f2 = paramLength2.floatValueX(this);
      break label71;
      label184:
      setClipRect(this.state.viewPort.minX, this.state.viewPort.minY, this.state.viewPort.width, this.state.viewPort.height);
      break label107;
      label231:
      this.canvas.concat(calculateViewBoxTransform(this.state.viewPort, paramSymbol.viewBox, localPreserveAspectRatio));
      this.state.viewBox = paramSymbol.viewBox;
      break label114;
      label269:
      popLayer(paramSymbol);
    }
  }
  
  private void render(SVG.Text paramText)
  {
    float f4 = 0.0F;
    debug("Text render", new Object[0]);
    updateStyleForElement(this.state, paramText);
    label43:
    float f1;
    label45:
    label52:
    float f2;
    label54:
    label61:
    float f3;
    label64:
    label71:
    Object localObject;
    if (display())
    {
      if (paramText.transform != null) {
        break label141;
      }
      if (paramText.x != null) {
        break label155;
      }
      f1 = 0.0F;
      if (paramText.y != null) {
        break label188;
      }
      f2 = 0.0F;
      if (paramText.dx != null) {
        break label221;
      }
      f3 = 0.0F;
      if (paramText.dy != null) {
        break label255;
      }
      localObject = getAnchorPosition();
      if (localObject != SVG.Style.TextAnchor.Start) {
        break label289;
      }
      label85:
      if (paramText.boundingBox == null) {
        break label322;
      }
    }
    for (;;)
    {
      updateParentBoundingBox(paramText);
      checkForGradiantsAndPatterns(paramText);
      checkForClipPath(paramText);
      boolean bool = pushLayer();
      enumerateTextSpans(paramText, new PlainTextDrawer(f1 + f3, f4 + f2));
      if (bool) {
        break label387;
      }
      return;
      return;
      label141:
      this.canvas.concat(paramText.transform);
      break;
      label155:
      if (paramText.x.size() == 0) {
        break label43;
      }
      f1 = ((SVG.Length)paramText.x.get(0)).floatValueX(this);
      break label45;
      label188:
      if (paramText.y.size() == 0) {
        break label52;
      }
      f2 = ((SVG.Length)paramText.y.get(0)).floatValueY(this);
      break label54;
      label221:
      if (paramText.dx.size() == 0) {
        break label61;
      }
      f3 = ((SVG.Length)paramText.dx.get(0)).floatValueX(this);
      break label64;
      label255:
      if (paramText.dy.size() == 0) {
        break label71;
      }
      f4 = ((SVG.Length)paramText.dy.get(0)).floatValueY(this);
      break label71;
      label289:
      float f5 = calculateTextWidth(paramText);
      if (localObject != SVG.Style.TextAnchor.Middle)
      {
        f1 -= f5;
        break label85;
      }
      f1 -= f5 / 2.0F;
      break label85;
      label322:
      localObject = new TextBoundsCalculator(f1, f2);
      enumerateTextSpans(paramText, (TextProcessor)localObject);
      paramText.boundingBox = new SVG.Box(((TextBoundsCalculator)localObject).bbox.left, ((TextBoundsCalculator)localObject).bbox.top, ((TextBoundsCalculator)localObject).bbox.width(), ((TextBoundsCalculator)localObject).bbox.height());
    }
    label387:
    popLayer(paramText);
  }
  
  private void render(SVG.Use paramUse)
  {
    float f2 = 0.0F;
    debug("Use render", new Object[0]);
    label26:
    Object localObject2;
    label67:
    Object localObject1;
    float f1;
    if (paramUse.width == null)
    {
      if (paramUse.height != null) {
        break label173;
      }
      updateStyleForElement(this.state, paramUse);
      if (!display()) {
        break label186;
      }
      localObject2 = paramUse.document.resolveIRI(paramUse.href);
      if (localObject2 == null) {
        break label187;
      }
      if (paramUse.transform != null) {
        break label205;
      }
      localObject1 = new Matrix();
      if (paramUse.x != null) {
        break label219;
      }
      f1 = 0.0F;
      label85:
      if (paramUse.y != null) {
        break label231;
      }
      label92:
      ((Matrix)localObject1).preTranslate(f1, f2);
      this.canvas.concat((Matrix)localObject1);
      checkForClipPath(paramUse);
      boolean bool = pushLayer();
      parentPush(paramUse);
      if ((localObject2 instanceof SVG.Svg)) {
        break label243;
      }
      if ((localObject2 instanceof SVG.Symbol)) {
        break label317;
      }
      render((SVG.SvgObject)localObject2);
      parentPop();
      if (bool) {
        break label403;
      }
    }
    for (;;)
    {
      updateParentBoundingBox(paramUse);
      return;
      if (!paramUse.width.isZero()) {
        break;
      }
      label173:
      do
      {
        return;
      } while (paramUse.height.isZero());
      break label26;
      label186:
      return;
      label187:
      error("Use reference '%s' not found", new Object[] { paramUse.href });
      return;
      label205:
      this.canvas.concat(paramUse.transform);
      break label67;
      label219:
      f1 = paramUse.x.floatValueX(this);
      break label85;
      label231:
      f2 = paramUse.y.floatValueY(this);
      break label92;
      label243:
      statePush();
      localObject2 = (SVG.Svg)localObject2;
      if (paramUse.width == null)
      {
        localObject1 = ((SVG.Svg)localObject2).width;
        label268:
        if (paramUse.height != null) {
          break label308;
        }
      }
      label308:
      for (SVG.Length localLength = ((SVG.Svg)localObject2).height;; localLength = paramUse.height)
      {
        render((SVG.Svg)localObject2, (SVG.Length)localObject1, localLength);
        statePop();
        break;
        localObject1 = paramUse.width;
        break label268;
      }
      label317:
      if (paramUse.width == null)
      {
        localObject1 = new SVG.Length(100.0F, SVG.Unit.percent);
        label339:
        if (paramUse.height != null) {
          break label394;
        }
      }
      label394:
      for (localLength = new SVG.Length(100.0F, SVG.Unit.percent);; localLength = paramUse.height)
      {
        statePush();
        render((SVG.Symbol)localObject2, (SVG.Length)localObject1, localLength);
        statePop();
        break;
        localObject1 = paramUse.width;
        break label339;
      }
      label403:
      popLayer(paramUse);
    }
  }
  
  private void renderChildren(SVG.SvgContainer paramSvgContainer, boolean paramBoolean)
  {
    if (!paramBoolean) {}
    for (;;)
    {
      paramSvgContainer = paramSvgContainer.getChildren().iterator();
      while (paramSvgContainer.hasNext()) {
        render((SVG.SvgObject)paramSvgContainer.next());
      }
      parentPush(paramSvgContainer);
    }
    if (!paramBoolean) {
      return;
    }
    parentPop();
  }
  
  private void renderMarker(SVG.Marker paramMarker, MarkerVector paramMarkerVector)
  {
    float f1 = 0.0F;
    statePush();
    float f2;
    label39:
    label98:
    float f7;
    label108:
    float f3;
    label120:
    float f4;
    label132:
    label147:
    float f6;
    float f5;
    PreserveAspectRatio localPreserveAspectRatio;
    if (paramMarker.orient == null)
    {
      if (paramMarker.markerUnitsAreUser) {
        break label340;
      }
      f2 = this.state.style.strokeWidth.floatValue(this.dpi);
      this.state = findInheritFromAncestorState(paramMarker);
      Matrix localMatrix = new Matrix();
      localMatrix.preTranslate(paramMarkerVector.x, paramMarkerVector.y);
      localMatrix.preRotate(f1);
      localMatrix.preScale(f2, f2);
      if (paramMarker.refX != null) {
        break label346;
      }
      f2 = 0.0F;
      if (paramMarker.refY != null) {
        break label359;
      }
      f7 = 0.0F;
      if (paramMarker.markerWidth != null) {
        break label372;
      }
      f3 = 3.0F;
      if (paramMarker.markerHeight != null) {
        break label385;
      }
      f4 = 3.0F;
      if (paramMarker.viewBox != null) {
        break label398;
      }
      paramMarkerVector = this.state.viewPort;
      f6 = f3 / paramMarkerVector.width;
      f5 = f4 / paramMarkerVector.height;
      if (paramMarker.preserveAspectRatio != null) {
        break label406;
      }
      localPreserveAspectRatio = PreserveAspectRatio.LETTERBOX;
      label177:
      if (!localPreserveAspectRatio.equals(PreserveAspectRatio.STRETCH)) {
        break label415;
      }
      localMatrix.preTranslate(-f2 * f6, -f7 * f5);
      this.canvas.concat(localMatrix);
      if (!this.state.style.overflow.booleanValue()) {
        break label454;
      }
      localMatrix.reset();
      localMatrix.preScale(f6, f5);
      this.canvas.concat(localMatrix);
      boolean bool = pushLayer();
      renderChildren(paramMarker, false);
      if (bool) {
        break label656;
      }
    }
    for (;;)
    {
      statePop();
      return;
      if (!Float.isNaN(paramMarker.orient.floatValue()))
      {
        f1 = paramMarker.orient.floatValue();
        break;
      }
      if ((paramMarkerVector.dx == 0.0F) && (paramMarkerVector.dy == 0.0F)) {
        break;
      }
      f1 = (float)Math.toDegrees(Math.atan2(paramMarkerVector.dy, paramMarkerVector.dx));
      break;
      label340:
      f2 = 1.0F;
      break label39;
      label346:
      f2 = paramMarker.refX.floatValueX(this);
      break label98;
      label359:
      f7 = paramMarker.refY.floatValueY(this);
      break label108;
      label372:
      f3 = paramMarker.markerWidth.floatValueX(this);
      break label120;
      label385:
      f4 = paramMarker.markerHeight.floatValueY(this);
      break label132;
      label398:
      paramMarkerVector = paramMarker.viewBox;
      break label147;
      label406:
      localPreserveAspectRatio = paramMarker.preserveAspectRatio;
      break label177;
      label415:
      if (localPreserveAspectRatio.getScale() != PreserveAspectRatio.Scale.Slice) {}
      for (f1 = Math.min(f6, f5);; f1 = Math.max(f6, f5))
      {
        f6 = f1;
        f5 = f1;
        break;
      }
      label454:
      float f9 = paramMarkerVector.width * f6;
      float f8 = paramMarkerVector.height * f5;
      f7 = 0.0F;
      f2 = 0.0F;
      f1 = f7;
      switch ($SWITCH_TABLE$com$caverock$androidsvg$PreserveAspectRatio$Alignment()[localPreserveAspectRatio.getAlignment().ordinal()])
      {
      default: 
        f1 = f7;
      case 5: 
      case 8: 
        label543:
        switch ($SWITCH_TABLE$com$caverock$androidsvg$PreserveAspectRatio$Alignment()[localPreserveAspectRatio.getAlignment().ordinal()])
        {
        }
        break;
      }
      for (;;)
      {
        setClipRect(f1, f2, f3, f4);
        break;
        f1 = 0.0F - (f3 - f9) / 2.0F;
        break label543;
        f1 = 0.0F - (f3 - f9);
        break label543;
        f2 = 0.0F - (f4 - f8) / 2.0F;
        continue;
        f2 = 0.0F - (f4 - f8);
      }
      label656:
      popLayer(paramMarker);
    }
  }
  
  private void renderMarkers(SVG.GraphicsElement paramGraphicsElement)
  {
    Object localObject1;
    label29:
    Object localObject2;
    label45:
    Object localObject3;
    label61:
    label84:
    int j;
    if (this.state.style.markerStart != null)
    {
      if (this.state.style.markerStart != null) {
        break label183;
      }
      localObject1 = null;
      if (this.state.style.markerMid != null) {
        break label246;
      }
      localObject2 = null;
      if (this.state.style.markerEnd != null) {
        break label309;
      }
      localObject3 = null;
      if ((paramGraphicsElement instanceof SVG.Path)) {
        break label372;
      }
      if ((paramGraphicsElement instanceof SVG.Line)) {
        break label394;
      }
      paramGraphicsElement = calculateMarkerPositions((SVG.PolyLine)paramGraphicsElement);
      if (paramGraphicsElement == null) {
        break label406;
      }
      j = paramGraphicsElement.size();
      if (j == 0) {
        break label407;
      }
      SVG.Style localStyle1 = this.state.style;
      SVG.Style localStyle2 = this.state.style;
      this.state.style.markerEnd = null;
      localStyle2.markerMid = null;
      localStyle1.markerStart = null;
      if (localObject1 != null) {
        break label408;
      }
      label145:
      if (localObject2 != null) {
        break label427;
      }
    }
    for (;;)
    {
      if (localObject3 != null) {
        break label459;
      }
      return;
      if ((this.state.style.markerMid != null) || (this.state.style.markerEnd != null)) {
        break;
      }
      return;
      label183:
      localObject1 = paramGraphicsElement.document.resolveIRI(this.state.style.markerStart);
      if (localObject1 == null)
      {
        error("Marker reference '%s' not found", new Object[] { this.state.style.markerStart });
        localObject1 = null;
        break label29;
      }
      localObject1 = (SVG.Marker)localObject1;
      break label29;
      label246:
      localObject2 = paramGraphicsElement.document.resolveIRI(this.state.style.markerMid);
      if (localObject2 == null)
      {
        error("Marker reference '%s' not found", new Object[] { this.state.style.markerMid });
        localObject2 = null;
        break label45;
      }
      localObject2 = (SVG.Marker)localObject2;
      break label45;
      label309:
      localObject3 = paramGraphicsElement.document.resolveIRI(this.state.style.markerEnd);
      if (localObject3 == null)
      {
        error("Marker reference '%s' not found", new Object[] { this.state.style.markerEnd });
        localObject3 = null;
        break label61;
      }
      localObject3 = (SVG.Marker)localObject3;
      break label61;
      label372:
      paramGraphicsElement = new MarkerPositionCalculator(((SVG.Path)paramGraphicsElement).d).getMarkers();
      break label84;
      label394:
      paramGraphicsElement = calculateMarkerPositions((SVG.Line)paramGraphicsElement);
      break label84;
      label406:
      return;
      label407:
      return;
      label408:
      renderMarker((SVG.Marker)localObject1, (MarkerVector)paramGraphicsElement.get(0));
      break label145;
      label427:
      int i = 1;
      while (i < j - 1)
      {
        renderMarker((SVG.Marker)localObject2, (MarkerVector)paramGraphicsElement.get(i));
        i += 1;
      }
    }
    label459:
    renderMarker((SVG.Marker)localObject3, (MarkerVector)paramGraphicsElement.get(j - 1));
  }
  
  private void renderMask(SVG.Mask paramMask, SVG.SvgElement paramSvgElement)
  {
    float f2 = -0.1F;
    debug("Mask render", new Object[0]);
    int i;
    label25:
    float f1;
    if (paramMask.maskUnitsAreUser == null)
    {
      i = 0;
      if (i != 0) {
        break label162;
      }
      if (paramMask.x != null) {
        break label307;
      }
      f1 = -0.1F;
      label41:
      if (paramMask.y != null) {
        break label320;
      }
      f1 = f2;
      label51:
      if (paramMask.width != null) {
        break label333;
      }
      f1 = 1.2F;
      label62:
      if (paramMask.height != null) {
        break label346;
      }
    }
    label162:
    label177:
    label193:
    label270:
    label283:
    label295:
    label307:
    label320:
    label333:
    label346:
    for (f2 = 1.2F;; f2 = paramMask.height.floatValue(this, 1.0F))
    {
      float f3 = paramSvgElement.boundingBox.minX;
      f3 = paramSvgElement.boundingBox.width;
      f3 = paramSvgElement.boundingBox.minY;
      f3 = paramSvgElement.boundingBox.height;
      f1 = paramSvgElement.boundingBox.width * f1;
      f2 = paramSvgElement.boundingBox.height * f2;
      for (;;)
      {
        if ((f1 != 0.0F) && (f2 != 0.0F)) {
          break label360;
        }
        return;
        if (!paramMask.maskUnitsAreUser.booleanValue()) {
          break;
        }
        i = 1;
        break label25;
        if (paramMask.width == null)
        {
          f1 = paramSvgElement.boundingBox.width;
          if (paramMask.height != null) {
            break label270;
          }
          f2 = paramSvgElement.boundingBox.height;
          if (paramMask.x != null) {
            break label283;
          }
          f3 = (float)(paramSvgElement.boundingBox.minX - paramSvgElement.boundingBox.width * 0.1D);
        }
        for (;;)
        {
          if (paramMask.y != null) {
            break label295;
          }
          f3 = (float)(paramSvgElement.boundingBox.minY - paramSvgElement.boundingBox.height * 0.1D);
          break;
          f1 = paramMask.width.floatValueX(this);
          break label177;
          f2 = paramMask.height.floatValueY(this);
          break label193;
          paramMask.x.floatValueX(this);
        }
        paramMask.y.floatValueY(this);
      }
      f1 = paramMask.x.floatValue(this, 1.0F);
      break label41;
      f1 = paramMask.y.floatValue(this, 1.0F);
      break label51;
      f1 = paramMask.width.floatValue(this, 1.0F);
      break label62;
    }
    label360:
    statePush();
    this.state = findInheritFromAncestorState(paramMask);
    this.state.style.opacity = Float.valueOf(1.0F);
    if (paramMask.maskContentUnitsAreUser == null)
    {
      i = 1;
      label397:
      if (i == 0) {
        break label429;
      }
    }
    for (;;)
    {
      renderChildren(paramMask, false);
      statePop();
      return;
      if (paramMask.maskContentUnitsAreUser.booleanValue()) {
        break;
      }
      i = 0;
      break label397;
      label429:
      this.canvas.translate(paramSvgElement.boundingBox.minX, paramSvgElement.boundingBox.minY);
      this.canvas.scale(paramSvgElement.boundingBox.width, paramSvgElement.boundingBox.height);
    }
  }
  
  private void renderSwitchChild(SVG.Switch paramSwitch)
  {
    String str = Locale.getDefault().getLanguage();
    SVGExternalFileResolver localSVGExternalFileResolver = this.document.getFileResolver();
    paramSwitch = paramSwitch.getChildren().iterator();
    SVG.SvgObject localSvgObject;
    Object localObject1;
    Object localObject2;
    while (paramSwitch.hasNext())
    {
      localSvgObject = (SVG.SvgObject)paramSwitch.next();
      if ((localSvgObject instanceof SVG.SvgConditional))
      {
        localObject1 = (SVG.SvgConditional)localSvgObject;
        if (((SVG.SvgConditional)localObject1).getRequiredExtensions() == null)
        {
          localObject2 = ((SVG.SvgConditional)localObject1).getSystemLanguage();
          if (localObject2 != null) {
            break label133;
          }
          label84:
          localObject2 = ((SVG.SvgConditional)localObject1).getRequiredFeatures();
          if (localObject2 != null) {
            break label157;
          }
          label98:
          localObject2 = ((SVG.SvgConditional)localObject1).getRequiredFormats();
          if (localObject2 != null) {
            break label181;
          }
          label112:
          localObject1 = ((SVG.SvgConditional)localObject1).getRequiredFonts();
          if (localObject1 != null) {
            break label234;
          }
        }
      }
    }
    label133:
    label157:
    label181:
    label232:
    label234:
    label311:
    for (;;)
    {
      render(localSvgObject);
      return;
      if ((((Set)localObject2).isEmpty()) || (!((Set)localObject2).contains(str))) {
        break;
      }
      break label84;
      if ((((Set)localObject2).isEmpty()) || (!SVGParser.supportedFeatures.containsAll((Collection)localObject2))) {
        break;
      }
      break label98;
      if ((((Set)localObject2).isEmpty()) || (localSVGExternalFileResolver == null)) {
        break;
      }
      localObject2 = ((Set)localObject2).iterator();
      for (;;)
      {
        if (!((Iterator)localObject2).hasNext()) {
          break label232;
        }
        if (!localSVGExternalFileResolver.isFormatSupported((String)((Iterator)localObject2).next())) {
          break;
        }
      }
      break label112;
      if ((((Set)localObject1).isEmpty()) || (localSVGExternalFileResolver == null)) {
        break;
      }
      localObject1 = ((Set)localObject1).iterator();
      for (;;)
      {
        if (!((Iterator)localObject1).hasNext()) {
          break label311;
        }
        if (localSVGExternalFileResolver.resolveFont((String)((Iterator)localObject1).next(), this.state.style.fontWeight.intValue(), String.valueOf(this.state.style.fontStyle)) == null) {
          break;
        }
      }
    }
  }
  
  private void renderTextPath(SVG.TextPath paramTextPath)
  {
    debug("TextPath render", new Object[0]);
    updateStyleForElement(this.state, paramTextPath);
    Object localObject1;
    Object localObject2;
    float f1;
    if (display())
    {
      if (!visible()) {
        break label160;
      }
      localObject1 = paramTextPath.document.resolveIRI(paramTextPath.href);
      if (localObject1 == null) {
        break label161;
      }
      localObject2 = (SVG.Path)localObject1;
      localObject1 = new PathConverter(((SVG.Path)localObject2).d).getPath();
      if (((SVG.Path)localObject2).transform != null) {
        break label179;
      }
      localObject2 = new PathMeasure((Path)localObject1, false);
      if (paramTextPath.startOffset != null) {
        break label192;
      }
      f1 = 0.0F;
      label105:
      localObject2 = getAnchorPosition();
      if (localObject2 != SVG.Style.TextAnchor.Start) {
        break label209;
      }
    }
    for (;;)
    {
      checkForGradiantsAndPatterns((SVG.SvgElement)paramTextPath.getTextRoot());
      boolean bool = pushLayer();
      enumerateTextSpans(paramTextPath, new PathTextDrawer((Path)localObject1, f1, 0.0F));
      if (bool) {
        break label239;
      }
      return;
      return;
      label160:
      return;
      label161:
      error("TextPath reference '%s' not found", new Object[] { paramTextPath.href });
      return;
      label179:
      ((Path)localObject1).transform(((SVG.Path)localObject2).transform);
      break;
      label192:
      f1 = paramTextPath.startOffset.floatValue(this, ((PathMeasure)localObject2).getLength());
      break label105;
      label209:
      float f2 = calculateTextWidth(paramTextPath);
      if (localObject2 != SVG.Style.TextAnchor.Middle) {
        f1 -= f2;
      } else {
        f1 -= f2 / 2.0F;
      }
    }
    label239:
    popLayer(paramTextPath);
  }
  
  private boolean requiresCompositing()
  {
    int i;
    if (this.state.style.mask == null)
    {
      if (this.state.style.opacity.floatValue() >= 1.0F) {
        break label75;
      }
      i = 1;
      label33:
      if (i != 0) {
        break label90;
      }
      if (this.state.style.mask != null) {
        break label80;
      }
    }
    label75:
    label80:
    while (!this.state.directRendering)
    {
      return false;
      if (this.state.directRendering) {
        break;
      }
      warn("Masks are not supported when using getPicture()", new Object[0]);
      break;
      i = 0;
      break label33;
    }
    label90:
    return true;
  }
  
  private void resetState()
  {
    this.state = new RendererState();
    this.stateStack = new Stack();
    updateStyle(this.state, SVG.Style.getDefaultStyle());
    this.state.viewPort = this.canvasViewPort;
    this.state.spacePreserve = false;
    this.state.directRendering = this.directRenderingMode;
    this.stateStack.push((RendererState)this.state.clone());
    this.canvasStack = new Stack();
    this.bitmapStack = new Stack();
    this.matrixStack = new Stack();
    this.parentStack = new Stack();
  }
  
  private void setClipRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    float f2 = paramFloat1 + paramFloat3;
    float f1 = paramFloat2 + paramFloat4;
    if (this.state.style.clip == null)
    {
      paramFloat4 = paramFloat2;
      paramFloat3 = paramFloat1;
      paramFloat2 = f2;
    }
    for (paramFloat1 = f1;; paramFloat1 = f1 - this.state.style.clip.bottom.floatValueY(this))
    {
      this.canvas.clipRect(paramFloat3, paramFloat4, paramFloat2, paramFloat1);
      return;
      paramFloat3 = paramFloat1 + this.state.style.clip.left.floatValueX(this);
      paramFloat4 = paramFloat2 + this.state.style.clip.top.floatValueY(this);
      paramFloat2 = f2 - this.state.style.clip.right.floatValueX(this);
    }
  }
  
  private void setPaintColour(RendererState paramRendererState, boolean paramBoolean, SVG.SvgPaint paramSvgPaint)
  {
    if (!paramBoolean) {}
    float f;
    for (Float localFloat = paramRendererState.style.strokeOpacity;; localFloat = paramRendererState.style.fillOpacity)
    {
      f = localFloat.floatValue();
      if ((paramSvgPaint instanceof SVG.Colour)) {
        break;
      }
      if ((paramSvgPaint instanceof SVG.CurrentColor)) {
        break label84;
      }
      return;
    }
    label84:
    for (int i = ((SVG.Colour)paramSvgPaint).colour;; i = paramRendererState.style.color.colour)
    {
      i |= clamp255(f) << 24;
      if (paramBoolean) {
        break;
      }
      paramRendererState.strokePaint.setColor(i);
      return;
    }
    paramRendererState.fillPaint.setColor(i);
  }
  
  private void setSolidColor(boolean paramBoolean, SVG.SolidColor paramSolidColor)
  {
    boolean bool2 = false;
    boolean bool1 = false;
    if (!paramBoolean)
    {
      if (isSpecified(paramSolidColor.baseStyle, 2147483648L)) {
        break label181;
      }
      if (isSpecified(paramSolidColor.baseStyle, 4294967296L)) {
        break label231;
      }
    }
    for (;;)
    {
      if (isSpecified(paramSolidColor.baseStyle, 6442450944L)) {
        break label251;
      }
      label161:
      label179:
      for (;;)
      {
        return;
        if (!isSpecified(paramSolidColor.baseStyle, 2147483648L)) {
          if (isSpecified(paramSolidColor.baseStyle, 4294967296L)) {
            break label161;
          }
        }
        for (;;)
        {
          if (!isSpecified(paramSolidColor.baseStyle, 6442450944L)) {
            break label179;
          }
          setPaintColour(this.state, paramBoolean, this.state.style.fill);
          return;
          this.state.style.fill = paramSolidColor.baseStyle.solidColor;
          localRendererState = this.state;
          if (paramSolidColor.baseStyle.solidColor == null) {}
          for (;;)
          {
            localRendererState.hasFill = bool1;
            break;
            bool1 = true;
          }
          this.state.style.fillOpacity = paramSolidColor.baseStyle.solidOpacity;
        }
      }
      label181:
      this.state.style.stroke = paramSolidColor.baseStyle.solidColor;
      RendererState localRendererState = this.state;
      if (paramSolidColor.baseStyle.solidColor == null) {}
      for (bool1 = bool2;; bool1 = true)
      {
        localRendererState.hasStroke = bool1;
        break;
      }
      label231:
      this.state.style.strokeOpacity = paramSolidColor.baseStyle.solidOpacity;
    }
    label251:
    setPaintColour(this.state, paramBoolean, this.state.style.stroke);
  }
  
  private void statePop()
  {
    this.canvas.restore();
    this.state = ((RendererState)this.stateStack.pop());
  }
  
  private void statePush()
  {
    this.canvas.save();
    this.stateStack.push(this.state);
    this.state = ((RendererState)this.state.clone());
  }
  
  private String textXMLSpaceTransform(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (!this.state.spacePreserve)
    {
      paramString = paramString.replaceAll("\\n", "").replaceAll("\\t", " ");
      if (paramBoolean1) {
        break label60;
      }
      if (paramBoolean2) {
        break label74;
      }
    }
    for (;;)
    {
      return paramString.replaceAll("\\s{2,}", " ");
      return paramString.replaceAll("[\\n\\t]", " ");
      label60:
      paramString = paramString.replaceAll("^\\s+", "");
      break;
      label74:
      paramString = paramString.replaceAll("\\s+$", "");
    }
  }
  
  private void updateParentBoundingBox(SVG.SvgElement paramSvgElement)
  {
    Matrix localMatrix;
    if (paramSvgElement.parent != null)
    {
      if (paramSvgElement.boundingBox != null)
      {
        localMatrix = new Matrix();
        if (((Matrix)this.matrixStack.peek()).invert(localMatrix)) {
          break label44;
        }
      }
    }
    else {
      return;
    }
    return;
    label44:
    Object localObject = new float[8];
    localObject[0] = paramSvgElement.boundingBox.minX;
    localObject[1] = paramSvgElement.boundingBox.minY;
    localObject[2] = paramSvgElement.boundingBox.maxX();
    localObject[3] = paramSvgElement.boundingBox.minY;
    localObject[4] = paramSvgElement.boundingBox.maxX();
    localObject[5] = paramSvgElement.boundingBox.maxY();
    localObject[6] = paramSvgElement.boundingBox.minX;
    localObject[7] = paramSvgElement.boundingBox.maxY();
    localMatrix.preConcat(this.canvas.getMatrix());
    localMatrix.mapPoints((float[])localObject);
    paramSvgElement = new RectF(localObject[0], localObject[1], localObject[0], localObject[1]);
    int i = 2;
    while (i <= 6)
    {
      if (localObject[i] < paramSvgElement.left) {
        paramSvgElement.left = localObject[i];
      }
      if (localObject[i] > paramSvgElement.right) {
        paramSvgElement.right = localObject[i];
      }
      if (localObject[(i + 1)] < paramSvgElement.top) {
        paramSvgElement.top = localObject[(i + 1)];
      }
      if (localObject[(i + 1)] > paramSvgElement.bottom) {
        paramSvgElement.bottom = localObject[(i + 1)];
      }
      i += 2;
    }
    localObject = (SVG.SvgElement)this.parentStack.peek();
    if (((SVG.SvgElement)localObject).boundingBox != null)
    {
      ((SVG.SvgElement)localObject).boundingBox.union(SVG.Box.fromLimits(paramSvgElement.left, paramSvgElement.top, paramSvgElement.right, paramSvgElement.bottom));
      return;
    }
    ((SVG.SvgElement)localObject).boundingBox = SVG.Box.fromLimits(paramSvgElement.left, paramSvgElement.top, paramSvgElement.right, paramSvgElement.bottom);
  }
  
  private void updateStyle(RendererState paramRendererState, SVG.Style paramStyle)
  {
    boolean bool2 = false;
    if (!isSpecified(paramStyle, 4096L))
    {
      if (isSpecified(paramStyle, 2048L)) {
        break label445;
      }
      label25:
      if (isSpecified(paramStyle, 1L)) {
        break label459;
      }
      if (isSpecified(paramStyle, 4L)) {
        break label495;
      }
      label45:
      if (isSpecified(paramStyle, 6149L)) {
        break label509;
      }
      label56:
      if (isSpecified(paramStyle, 2L)) {
        break label525;
      }
      label67:
      if (isSpecified(paramStyle, 8L)) {
        break label539;
      }
      if (isSpecified(paramStyle, 16L)) {
        break label575;
      }
      label89:
      if (isSpecified(paramStyle, 6168L)) {
        break label589;
      }
      label100:
      if (isSpecified(paramStyle, 34359738368L)) {
        break label605;
      }
      label111:
      if (isSpecified(paramStyle, 32L)) {
        break label619;
      }
      label122:
      if (isSpecified(paramStyle, 64L)) {
        break label651;
      }
      if (isSpecified(paramStyle, 128L)) {
        break label742;
      }
      if (isSpecified(paramStyle, 256L)) {
        break label834;
      }
      label155:
      if (isSpecified(paramStyle, 512L)) {
        break label862;
      }
      label166:
      if (isSpecified(paramStyle, 1024L)) {
        break label876;
      }
      label177:
      if (isSpecified(paramStyle, 1536L)) {
        break label890;
      }
      label188:
      if (isSpecified(paramStyle, 16384L)) {
        break label1071;
      }
      label199:
      if (isSpecified(paramStyle, 8192L)) {
        break label1122;
      }
      label210:
      if (isSpecified(paramStyle, 32768L)) {
        break label1136;
      }
      label221:
      if (isSpecified(paramStyle, 65536L)) {
        break label1259;
      }
      label232:
      if (isSpecified(paramStyle, 106496L)) {
        break label1273;
      }
      if (isSpecified(paramStyle, 131072L)) {
        break label1474;
      }
      label254:
      if (isSpecified(paramStyle, 68719476736L)) {
        break label1625;
      }
      label265:
      if (isSpecified(paramStyle, 262144L)) {
        break label1639;
      }
      label276:
      if (isSpecified(paramStyle, 524288L)) {
        break label1653;
      }
      label287:
      if (isSpecified(paramStyle, 2097152L)) {
        break label1667;
      }
      label298:
      if (isSpecified(paramStyle, 4194304L)) {
        break label1681;
      }
      label309:
      if (isSpecified(paramStyle, 8388608L)) {
        break label1695;
      }
      label320:
      if (isSpecified(paramStyle, 16777216L)) {
        break label1709;
      }
      label331:
      if (isSpecified(paramStyle, 33554432L)) {
        break label1723;
      }
      label342:
      if (isSpecified(paramStyle, 1048576L)) {
        break label1737;
      }
      label353:
      if (isSpecified(paramStyle, 268435456L)) {
        break label1751;
      }
      label364:
      if (isSpecified(paramStyle, 536870912L)) {
        break label1765;
      }
      label375:
      if (isSpecified(paramStyle, 1073741824L)) {
        break label1779;
      }
      label386:
      if (isSpecified(paramStyle, 67108864L)) {
        break label1793;
      }
      label397:
      if (isSpecified(paramStyle, 134217728L)) {
        break label1807;
      }
      label408:
      if (isSpecified(paramStyle, 8589934592L)) {
        break label1821;
      }
    }
    for (;;)
    {
      if (isSpecified(paramStyle, 17179869184L)) {
        break label1835;
      }
      return;
      paramRendererState.style.color = paramStyle.color;
      break;
      label445:
      paramRendererState.style.opacity = paramStyle.opacity;
      break label25;
      label459:
      paramRendererState.style.fill = paramStyle.fill;
      if (paramStyle.fill == null) {}
      for (boolean bool1 = false;; bool1 = true)
      {
        paramRendererState.hasFill = bool1;
        break;
      }
      label495:
      paramRendererState.style.fillOpacity = paramStyle.fillOpacity;
      break label45;
      label509:
      setPaintColour(paramRendererState, true, paramRendererState.style.fill);
      break label56;
      label525:
      paramRendererState.style.fillRule = paramStyle.fillRule;
      break label67;
      label539:
      paramRendererState.style.stroke = paramStyle.stroke;
      if (paramStyle.stroke == null) {}
      for (bool1 = false;; bool1 = true)
      {
        paramRendererState.hasStroke = bool1;
        break;
      }
      label575:
      paramRendererState.style.strokeOpacity = paramStyle.strokeOpacity;
      break label89;
      label589:
      setPaintColour(paramRendererState, false, paramRendererState.style.stroke);
      break label100;
      label605:
      paramRendererState.style.vectorEffect = paramStyle.vectorEffect;
      break label111;
      label619:
      paramRendererState.style.strokeWidth = paramStyle.strokeWidth;
      paramRendererState.strokePaint.setStrokeWidth(paramRendererState.style.strokeWidth.floatValue(this));
      break label122;
      label651:
      paramRendererState.style.strokeLineCap = paramStyle.strokeLineCap;
      switch ($SWITCH_TABLE$com$caverock$androidsvg$SVG$Style$LineCaps()[paramStyle.strokeLineCap.ordinal()])
      {
      default: 
        break;
      case 1: 
        paramRendererState.strokePaint.setStrokeCap(Paint.Cap.BUTT);
        break;
      case 2: 
        paramRendererState.strokePaint.setStrokeCap(Paint.Cap.ROUND);
        break;
      case 3: 
        paramRendererState.strokePaint.setStrokeCap(Paint.Cap.SQUARE);
        break;
        label742:
        paramRendererState.style.strokeLineJoin = paramStyle.strokeLineJoin;
        switch ($SWITCH_TABLE$com$caverock$androidsvg$SVG$Style$LineJoin()[paramStyle.strokeLineJoin.ordinal()])
        {
        default: 
          break;
        case 1: 
          paramRendererState.strokePaint.setStrokeJoin(Paint.Join.MITER);
          break;
        case 2: 
          paramRendererState.strokePaint.setStrokeJoin(Paint.Join.ROUND);
          break;
        case 3: 
          paramRendererState.strokePaint.setStrokeJoin(Paint.Join.BEVEL);
          break;
          label834:
          paramRendererState.style.strokeMiterLimit = paramStyle.strokeMiterLimit;
          paramRendererState.strokePaint.setStrokeMiter(paramStyle.strokeMiterLimit.floatValue());
          break label155;
          label862:
          paramRendererState.style.strokeDashArray = paramStyle.strokeDashArray;
          break label166;
          label876:
          paramRendererState.style.strokeDashOffset = paramStyle.strokeDashOffset;
          break label177;
          label890:
          int k;
          if (paramRendererState.style.strokeDashArray != null)
          {
            k = paramRendererState.style.strokeDashArray.length;
            if (k % 2 == 0) {
              break label992;
            }
          }
          label992:
          for (int i = k * 2;; i = k)
          {
            localObject = new float[i];
            int j = 0;
            f1 = 0.0F;
            while (j < i)
            {
              localObject[j] = paramRendererState.style.strokeDashArray[(j % k)].floatValue(this);
              f1 += localObject[j];
              j += 1;
            }
            paramRendererState.strokePaint.setPathEffect(null);
            break;
          }
          if (f1 == 0.0F)
          {
            paramRendererState.strokePaint.setPathEffect(null);
            break label188;
          }
          float f3 = paramRendererState.style.strokeDashOffset.floatValue(this);
          float f2 = f3;
          if (f3 < 0.0F) {
            f2 = f3 % f1 + f1;
          }
          paramRendererState.strokePaint.setPathEffect(new DashPathEffect((float[])localObject, f2));
          break label188;
          label1071:
          float f1 = getCurrentFontSize();
          paramRendererState.style.fontSize = paramStyle.fontSize;
          paramRendererState.fillPaint.setTextSize(paramStyle.fontSize.floatValue(this, f1));
          paramRendererState.strokePaint.setTextSize(paramStyle.fontSize.floatValue(this, f1));
          break label199;
          label1122:
          paramRendererState.style.fontFamily = paramStyle.fontFamily;
          break label210;
          label1136:
          if (paramStyle.fontWeight.intValue() != -1) {
            label1147:
            if (paramStyle.fontWeight.intValue() == 1) {
              break label1215;
            }
          }
          label1215:
          while (paramRendererState.style.fontWeight.intValue() >= 900)
          {
            paramRendererState.style.fontWeight = paramStyle.fontWeight;
            break;
            if (paramRendererState.style.fontWeight.intValue() <= 100) {
              break label1147;
            }
            localObject = paramRendererState.style;
            ((SVG.Style)localObject).fontWeight = Integer.valueOf(((SVG.Style)localObject).fontWeight.intValue() - 100);
            break;
          }
          Object localObject = paramRendererState.style;
          ((SVG.Style)localObject).fontWeight = Integer.valueOf(((SVG.Style)localObject).fontWeight.intValue() + 100);
          break label221;
          label1259:
          paramRendererState.style.fontStyle = paramStyle.fontStyle;
          break label232;
          label1273:
          if (paramRendererState.style.fontFamily == null)
          {
            label1283:
            localObject = null;
            if (localObject == null) {
              break label1449;
            }
          }
          for (;;)
          {
            paramRendererState.fillPaint.setTypeface((Typeface)localObject);
            paramRendererState.strokePaint.setTypeface((Typeface)localObject);
            break;
            if (this.document == null) {
              break label1283;
            }
            SVGExternalFileResolver localSVGExternalFileResolver = this.document.getFileResolver();
            Iterator localIterator = paramRendererState.style.fontFamily.iterator();
            localObject = null;
            label1347:
            Typeface localTypeface;
            if (localIterator.hasNext())
            {
              localObject = (String)localIterator.next();
              localTypeface = checkGenericFont((String)localObject, paramRendererState.style.fontWeight, paramRendererState.style.fontStyle);
              if (localTypeface == null) {
                break label1412;
              }
            }
            for (;;)
            {
              localObject = localTypeface;
              if (localTypeface != null) {
                break;
              }
              localObject = localTypeface;
              break label1347;
              break;
              label1412:
              if (localSVGExternalFileResolver != null) {
                localTypeface = localSVGExternalFileResolver.resolveFont((String)localObject, paramRendererState.style.fontWeight.intValue(), String.valueOf(paramRendererState.style.fontStyle));
              }
            }
            label1449:
            localObject = checkGenericFont("sans-serif", paramRendererState.style.fontWeight, paramRendererState.style.fontStyle);
          }
          label1474:
          paramRendererState.style.textDecoration = paramStyle.textDecoration;
          localObject = paramRendererState.fillPaint;
          if (paramStyle.textDecoration != SVG.Style.TextDecoration.LineThrough)
          {
            bool1 = false;
            label1504:
            ((Paint)localObject).setStrikeThruText(bool1);
            localObject = paramRendererState.fillPaint;
            if (paramStyle.textDecoration == SVG.Style.TextDecoration.Underline) {
              break label1607;
            }
            bool1 = false;
            label1530:
            ((Paint)localObject).setUnderlineText(bool1);
            if (Build.VERSION.SDK_INT < 17) {
              break label254;
            }
            localObject = paramRendererState.strokePaint;
            if (paramStyle.textDecoration == SVG.Style.TextDecoration.LineThrough) {
              break label1613;
            }
            bool1 = false;
            label1564:
            ((Paint)localObject).setStrikeThruText(bool1);
            localObject = paramRendererState.strokePaint;
            if (paramStyle.textDecoration == SVG.Style.TextDecoration.Underline) {
              break label1619;
            }
          }
          label1607:
          label1613:
          label1619:
          for (bool1 = bool2;; bool1 = true)
          {
            ((Paint)localObject).setUnderlineText(bool1);
            break;
            bool1 = true;
            break label1504;
            bool1 = true;
            break label1530;
            bool1 = true;
            break label1564;
          }
          label1625:
          paramRendererState.style.direction = paramStyle.direction;
          break label265;
          label1639:
          paramRendererState.style.textAnchor = paramStyle.textAnchor;
          break label276;
          label1653:
          paramRendererState.style.overflow = paramStyle.overflow;
          break label287;
          label1667:
          paramRendererState.style.markerStart = paramStyle.markerStart;
          break label298;
          label1681:
          paramRendererState.style.markerMid = paramStyle.markerMid;
          break label309;
          label1695:
          paramRendererState.style.markerEnd = paramStyle.markerEnd;
          break label320;
          label1709:
          paramRendererState.style.display = paramStyle.display;
          break label331;
          label1723:
          paramRendererState.style.visibility = paramStyle.visibility;
          break label342;
          label1737:
          paramRendererState.style.clip = paramStyle.clip;
          break label353;
          label1751:
          paramRendererState.style.clipPath = paramStyle.clipPath;
          break label364;
          label1765:
          paramRendererState.style.clipRule = paramStyle.clipRule;
          break label375;
          label1779:
          paramRendererState.style.mask = paramStyle.mask;
          break label386;
          label1793:
          paramRendererState.style.stopColor = paramStyle.stopColor;
          break label397;
          label1807:
          paramRendererState.style.stopOpacity = paramStyle.stopOpacity;
          break label408;
          label1821:
          paramRendererState.style.viewportFill = paramStyle.viewportFill;
        }
        break;
      }
    }
    label1835:
    paramRendererState.style.viewportFillOpacity = paramStyle.viewportFillOpacity;
  }
  
  private void updateStyleForElement(RendererState paramRendererState, SVG.SvgElementBase paramSvgElementBase)
  {
    boolean bool = false;
    if (paramSvgElementBase.parent != null)
    {
      paramRendererState.style.resetNonInheritingProperties(bool);
      if (paramSvgElementBase.baseStyle != null) {
        break label47;
      }
      label24:
      if (this.document.hasCSSRules()) {
        break label59;
      }
    }
    for (;;)
    {
      if (paramSvgElementBase.style != null) {
        break label120;
      }
      return;
      bool = true;
      break;
      label47:
      updateStyle(paramRendererState, paramSvgElementBase.baseStyle);
      break label24;
      label59:
      Iterator localIterator = this.document.getCSSRules().iterator();
      while (localIterator.hasNext())
      {
        CSSParser.Rule localRule = (CSSParser.Rule)localIterator.next();
        if (CSSParser.ruleMatch(localRule.selector, paramSvgElementBase)) {
          updateStyle(paramRendererState, localRule.style);
        }
      }
    }
    label120:
    updateStyle(paramRendererState, paramSvgElementBase.style);
  }
  
  private void viewportFill()
  {
    int i;
    if (!(this.state.style.viewportFill instanceof SVG.Colour))
    {
      if ((this.state.style.viewportFill instanceof SVG.CurrentColor)) {}
    }
    else
    {
      i = ((SVG.Colour)this.state.style.viewportFill).colour;
      if (this.state.style.viewportFillOpacity != null) {
        break label89;
      }
    }
    for (;;)
    {
      this.canvas.drawColor(i);
      return;
      i = this.state.style.color.colour;
      break;
      label89:
      i |= clamp255(this.state.style.viewportFillOpacity.floatValue()) << 24;
    }
  }
  
  private boolean visible()
  {
    if (this.state.style.visibility == null) {
      return true;
    }
    return this.state.style.visibility.booleanValue();
  }
  
  private static void warn(String paramString, Object... paramVarArgs)
  {
    Log.w("SVGAndroidRenderer", String.format(paramString, paramVarArgs));
  }
  
  protected float getCurrentFontSize()
  {
    return this.state.fillPaint.getTextSize();
  }
  
  protected float getCurrentFontXHeight()
  {
    return this.state.fillPaint.getTextSize() / 2.0F;
  }
  
  protected SVG.Box getCurrentViewPortInUserUnits()
  {
    if (this.state.viewBox == null) {
      return this.state.viewPort;
    }
    return this.state.viewBox;
  }
  
  protected float getDPI()
  {
    return this.dpi;
  }
  
  protected void renderDocument(SVG paramSVG, SVG.Box paramBox, PreserveAspectRatio paramPreserveAspectRatio, boolean paramBoolean)
  {
    this.document = paramSVG;
    this.directRenderingMode = paramBoolean;
    SVG.Svg localSvg = paramSVG.getRootElement();
    SVG.Length localLength1;
    SVG.Length localLength2;
    if (localSvg != null)
    {
      resetState();
      checkXMLSpaceAttribute(localSvg);
      localLength1 = localSvg.width;
      localLength2 = localSvg.height;
      if (paramBox != null) {
        break label90;
      }
      paramSVG = localSvg.viewBox;
      if (paramPreserveAspectRatio != null) {
        break label95;
      }
    }
    label90:
    label95:
    for (paramBox = localSvg.preserveAspectRatio;; paramBox = paramPreserveAspectRatio)
    {
      render(localSvg, localLength1, localLength2, paramSVG, paramBox);
      return;
      warn("Nothing to render. Document is empty.", new Object[0]);
      return;
      paramSVG = paramBox;
      break;
    }
  }
  
  private class MarkerPositionCalculator
    implements SVG.PathInterface
  {
    private boolean closepathReAdjustPending;
    private SVGAndroidRenderer.MarkerVector lastPos = null;
    private List<SVGAndroidRenderer.MarkerVector> markers = new ArrayList();
    private boolean normalCubic = true;
    private boolean startArc = false;
    private float startX;
    private float startY;
    private int subpathStartIndex = -1;
    
    public MarkerPositionCalculator(SVG.PathDefinition paramPathDefinition)
    {
      paramPathDefinition.enumeratePath(this);
      if (!this.closepathReAdjustPending) {}
      while (this.lastPos == null)
      {
        return;
        this.lastPos.add((SVGAndroidRenderer.MarkerVector)this.markers.get(this.subpathStartIndex));
        this.markers.set(this.subpathStartIndex, this.lastPos);
        this.closepathReAdjustPending = false;
      }
      this.markers.add(this.lastPos);
    }
    
    public void arcTo(float paramFloat1, float paramFloat2, float paramFloat3, boolean paramBoolean1, boolean paramBoolean2, float paramFloat4, float paramFloat5)
    {
      this.startArc = true;
      this.normalCubic = false;
      SVGAndroidRenderer.arcTo(this.lastPos.x, this.lastPos.y, paramFloat1, paramFloat2, paramFloat3, paramBoolean1, paramBoolean2, paramFloat4, paramFloat5, this);
      this.normalCubic = true;
      this.closepathReAdjustPending = false;
    }
    
    public void close()
    {
      this.markers.add(this.lastPos);
      lineTo(this.startX, this.startY);
      this.closepathReAdjustPending = true;
    }
    
    public void cubicTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
    {
      if (this.normalCubic)
      {
        this.lastPos.add(paramFloat1, paramFloat2);
        this.markers.add(this.lastPos);
        this.startArc = false;
      }
      for (;;)
      {
        this.lastPos = new SVGAndroidRenderer.MarkerVector(SVGAndroidRenderer.this, paramFloat5, paramFloat6, paramFloat5 - paramFloat3, paramFloat6 - paramFloat4);
        this.closepathReAdjustPending = false;
        return;
        if (this.startArc) {
          break;
        }
      }
    }
    
    public List<SVGAndroidRenderer.MarkerVector> getMarkers()
    {
      return this.markers;
    }
    
    public void lineTo(float paramFloat1, float paramFloat2)
    {
      this.lastPos.add(paramFloat1, paramFloat2);
      this.markers.add(this.lastPos);
      this.lastPos = new SVGAndroidRenderer.MarkerVector(SVGAndroidRenderer.this, paramFloat1, paramFloat2, paramFloat1 - this.lastPos.x, paramFloat2 - this.lastPos.y);
      this.closepathReAdjustPending = false;
    }
    
    public void moveTo(float paramFloat1, float paramFloat2)
    {
      if (!this.closepathReAdjustPending) {
        if (this.lastPos != null) {
          break label106;
        }
      }
      for (;;)
      {
        this.startX = paramFloat1;
        this.startY = paramFloat2;
        this.lastPos = new SVGAndroidRenderer.MarkerVector(SVGAndroidRenderer.this, paramFloat1, paramFloat2, 0.0F, 0.0F);
        this.subpathStartIndex = this.markers.size();
        return;
        this.lastPos.add((SVGAndroidRenderer.MarkerVector)this.markers.get(this.subpathStartIndex));
        this.markers.set(this.subpathStartIndex, this.lastPos);
        this.closepathReAdjustPending = false;
        break;
        label106:
        this.markers.add(this.lastPos);
      }
    }
    
    public void quadTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    {
      this.lastPos.add(paramFloat1, paramFloat2);
      this.markers.add(this.lastPos);
      this.lastPos = new SVGAndroidRenderer.MarkerVector(SVGAndroidRenderer.this, paramFloat3, paramFloat4, paramFloat3 - paramFloat1, paramFloat4 - paramFloat2);
      this.closepathReAdjustPending = false;
    }
  }
  
  private class MarkerVector
  {
    public float dx = 0.0F;
    public float dy = 0.0F;
    public float x;
    public float y;
    
    public MarkerVector(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    {
      this.x = paramFloat1;
      this.y = paramFloat2;
      double d = Math.sqrt(paramFloat3 * paramFloat3 + paramFloat4 * paramFloat4);
      if (d != 0.0D)
      {
        this.dx = ((float)(paramFloat3 / d));
        this.dy = ((float)(paramFloat4 / d));
      }
    }
    
    public void add(float paramFloat1, float paramFloat2)
    {
      paramFloat1 -= this.x;
      paramFloat2 -= this.y;
      double d = Math.sqrt(paramFloat1 * paramFloat1 + paramFloat2 * paramFloat2);
      if (d != 0.0D)
      {
        float f = this.dx;
        this.dx = ((float)(paramFloat1 / d) + f);
        this.dy += (float)(paramFloat2 / d);
      }
    }
    
    public void add(MarkerVector paramMarkerVector)
    {
      this.dx += paramMarkerVector.dx;
      this.dy += paramMarkerVector.dy;
    }
    
    public String toString()
    {
      return "(" + this.x + "," + this.y + " " + this.dx + "," + this.dy + ")";
    }
  }
  
  private class PathConverter
    implements SVG.PathInterface
  {
    float lastX;
    float lastY;
    Path path = new Path();
    
    public PathConverter(SVG.PathDefinition paramPathDefinition)
    {
      paramPathDefinition.enumeratePath(this);
    }
    
    public void arcTo(float paramFloat1, float paramFloat2, float paramFloat3, boolean paramBoolean1, boolean paramBoolean2, float paramFloat4, float paramFloat5)
    {
      SVGAndroidRenderer.arcTo(this.lastX, this.lastY, paramFloat1, paramFloat2, paramFloat3, paramBoolean1, paramBoolean2, paramFloat4, paramFloat5, this);
      this.lastX = paramFloat4;
      this.lastY = paramFloat5;
    }
    
    public void close()
    {
      this.path.close();
    }
    
    public void cubicTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
    {
      this.path.cubicTo(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);
      this.lastX = paramFloat5;
      this.lastY = paramFloat6;
    }
    
    public Path getPath()
    {
      return this.path;
    }
    
    public void lineTo(float paramFloat1, float paramFloat2)
    {
      this.path.lineTo(paramFloat1, paramFloat2);
      this.lastX = paramFloat1;
      this.lastY = paramFloat2;
    }
    
    public void moveTo(float paramFloat1, float paramFloat2)
    {
      this.path.moveTo(paramFloat1, paramFloat2);
      this.lastX = paramFloat1;
      this.lastY = paramFloat2;
    }
    
    public void quadTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    {
      this.path.quadTo(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
      this.lastX = paramFloat3;
      this.lastY = paramFloat4;
    }
  }
  
  private class PathTextDrawer
    extends SVGAndroidRenderer.PlainTextDrawer
  {
    private Path path;
    
    public PathTextDrawer(Path paramPath, float paramFloat1, float paramFloat2)
    {
      super(paramFloat1, paramFloat2);
      this.path = paramPath;
    }
    
    public void processText(String paramString)
    {
      if (!SVGAndroidRenderer.this.visible()) {}
      label130:
      for (;;)
      {
        this.x += SVGAndroidRenderer.this.state.fillPaint.measureText(paramString);
        return;
        if (!SVGAndroidRenderer.this.state.hasFill) {}
        for (;;)
        {
          if (!SVGAndroidRenderer.this.state.hasStroke) {
            break label130;
          }
          SVGAndroidRenderer.this.canvas.drawTextOnPath(paramString, this.path, this.x, this.y, SVGAndroidRenderer.this.state.strokePaint);
          break;
          SVGAndroidRenderer.this.canvas.drawTextOnPath(paramString, this.path, this.x, this.y, SVGAndroidRenderer.this.state.fillPaint);
        }
      }
    }
  }
  
  private class PlainTextDrawer
    extends SVGAndroidRenderer.TextProcessor
  {
    public float x;
    public float y;
    
    public PlainTextDrawer(float paramFloat1, float paramFloat2)
    {
      super(null);
      this.x = paramFloat1;
      this.y = paramFloat2;
    }
    
    public void processText(String paramString)
    {
      SVGAndroidRenderer.debug("TextSequence render", new Object[0]);
      if (!SVGAndroidRenderer.this.visible()) {}
      label131:
      for (;;)
      {
        this.x += SVGAndroidRenderer.this.state.fillPaint.measureText(paramString);
        return;
        if (!SVGAndroidRenderer.this.state.hasFill) {}
        for (;;)
        {
          if (!SVGAndroidRenderer.this.state.hasStroke) {
            break label131;
          }
          SVGAndroidRenderer.this.canvas.drawText(paramString, this.x, this.y, SVGAndroidRenderer.this.state.strokePaint);
          break;
          SVGAndroidRenderer.this.canvas.drawText(paramString, this.x, this.y, SVGAndroidRenderer.this.state.fillPaint);
        }
      }
    }
  }
  
  private class PlainTextToPath
    extends SVGAndroidRenderer.TextProcessor
  {
    public Path textAsPath;
    public float x;
    public float y;
    
    public PlainTextToPath(float paramFloat1, float paramFloat2, Path paramPath)
    {
      super(null);
      this.x = paramFloat1;
      this.y = paramFloat2;
      this.textAsPath = paramPath;
    }
    
    public boolean doTextContainer(SVG.TextContainer paramTextContainer)
    {
      if (!(paramTextContainer instanceof SVG.TextPath)) {
        return true;
      }
      SVGAndroidRenderer.warn("Using <textPath> elements in a clip path is not supported.", new Object[0]);
      return false;
    }
    
    public void processText(String paramString)
    {
      if (!SVGAndroidRenderer.this.visible()) {}
      for (;;)
      {
        this.x += SVGAndroidRenderer.this.state.fillPaint.measureText(paramString);
        return;
        Path localPath = new Path();
        SVGAndroidRenderer.this.state.fillPaint.getTextPath(paramString, 0, paramString.length(), this.x, this.y, localPath);
        this.textAsPath.addPath(localPath);
      }
    }
  }
  
  private class RendererState
    implements Cloneable
  {
    public boolean directRendering;
    public Paint fillPaint = new Paint();
    public boolean hasFill;
    public boolean hasStroke;
    public boolean spacePreserve;
    public Paint strokePaint;
    public SVG.Style style;
    public SVG.Box viewBox;
    public SVG.Box viewPort;
    
    public RendererState()
    {
      this.fillPaint.setFlags(385);
      this.fillPaint.setStyle(Paint.Style.FILL);
      this.fillPaint.setTypeface(Typeface.DEFAULT);
      this.strokePaint = new Paint();
      this.strokePaint.setFlags(385);
      this.strokePaint.setStyle(Paint.Style.STROKE);
      this.strokePaint.setTypeface(Typeface.DEFAULT);
      this.style = SVG.Style.getDefaultStyle();
    }
    
    protected Object clone()
    {
      try
      {
        RendererState localRendererState = (RendererState)super.clone();
        localRendererState.style = ((SVG.Style)this.style.clone());
        localRendererState.fillPaint = new Paint(this.fillPaint);
        localRendererState.strokePaint = new Paint(this.strokePaint);
        return localRendererState;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new InternalError(localCloneNotSupportedException.toString());
      }
    }
  }
  
  private class TextBoundsCalculator
    extends SVGAndroidRenderer.TextProcessor
  {
    RectF bbox = new RectF();
    float x;
    float y;
    
    public TextBoundsCalculator(float paramFloat1, float paramFloat2)
    {
      super(null);
      this.x = paramFloat1;
      this.y = paramFloat2;
    }
    
    public boolean doTextContainer(SVG.TextContainer paramTextContainer)
    {
      if (!(paramTextContainer instanceof SVG.TextPath)) {
        return true;
      }
      Object localObject = (SVG.TextPath)paramTextContainer;
      paramTextContainer = paramTextContainer.document.resolveIRI(((SVG.TextPath)localObject).href);
      if (paramTextContainer != null)
      {
        localObject = (SVG.Path)paramTextContainer;
        paramTextContainer = new SVGAndroidRenderer.PathConverter(SVGAndroidRenderer.this, ((SVG.Path)localObject).d).getPath();
        if (((SVG.Path)localObject).transform != null) {
          break label103;
        }
      }
      for (;;)
      {
        localObject = new RectF();
        paramTextContainer.computeBounds((RectF)localObject, true);
        this.bbox.union((RectF)localObject);
        return false;
        SVGAndroidRenderer.error("TextPath path reference '%s' not found", new Object[] { ((SVG.TextPath)localObject).href });
        return false;
        label103:
        paramTextContainer.transform(((SVG.Path)localObject).transform);
      }
    }
    
    public void processText(String paramString)
    {
      if (!SVGAndroidRenderer.this.visible()) {}
      for (;;)
      {
        this.x += SVGAndroidRenderer.this.state.fillPaint.measureText(paramString);
        return;
        Object localObject = new Rect();
        SVGAndroidRenderer.this.state.fillPaint.getTextBounds(paramString, 0, paramString.length(), (Rect)localObject);
        localObject = new RectF((Rect)localObject);
        ((RectF)localObject).offset(this.x, this.y);
        this.bbox.union((RectF)localObject);
      }
    }
  }
  
  private abstract class TextProcessor
  {
    private TextProcessor() {}
    
    public boolean doTextContainer(SVG.TextContainer paramTextContainer)
    {
      return true;
    }
    
    public abstract void processText(String paramString);
  }
  
  private class TextWidthCalculator
    extends SVGAndroidRenderer.TextProcessor
  {
    public float x = 0.0F;
    
    private TextWidthCalculator()
    {
      super(null);
    }
    
    public void processText(String paramString)
    {
      this.x += SVGAndroidRenderer.this.state.fillPaint.measureText(paramString);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\caverock\androidsvg\SVGAndroidRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */