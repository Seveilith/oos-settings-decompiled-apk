package com.airbnb.lottie.model.layer;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.TextDelegate;
import com.airbnb.lottie.animation.content.ContentGroup;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.TextKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.TransformKeyframeAnimation;
import com.airbnb.lottie.model.DocumentData;
import com.airbnb.lottie.model.Font;
import com.airbnb.lottie.model.FontCharacter;
import com.airbnb.lottie.model.animatable.AnimatableColorValue;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import com.airbnb.lottie.model.animatable.AnimatableTextFrame;
import com.airbnb.lottie.model.animatable.AnimatableTextProperties;
import com.airbnb.lottie.model.content.ShapeGroup;
import com.airbnb.lottie.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextLayer
  extends BaseLayer
{
  @Nullable
  private BaseKeyframeAnimation<Integer, Integer> colorAnimation;
  private final LottieComposition composition;
  private final Map<FontCharacter, List<ContentGroup>> contentsForCharacter = new HashMap();
  private final Paint fillPaint = new Paint(1) {};
  private final LottieDrawable lottieDrawable;
  private final Matrix matrix = new Matrix();
  private final RectF rectF = new RectF();
  @Nullable
  private BaseKeyframeAnimation<Integer, Integer> strokeAnimation;
  private final Paint strokePaint = new Paint(1) {};
  @Nullable
  private BaseKeyframeAnimation<Float, Float> strokeWidthAnimation;
  private final char[] tempCharArray = new char[1];
  private final TextKeyframeAnimation textAnimation;
  @Nullable
  private BaseKeyframeAnimation<Float, Float> trackingAnimation;
  
  TextLayer(LottieDrawable paramLottieDrawable, Layer paramLayer)
  {
    super(paramLottieDrawable, paramLayer);
    this.lottieDrawable = paramLottieDrawable;
    this.composition = paramLayer.getComposition();
    this.textAnimation = paramLayer.getText().createAnimation();
    this.textAnimation.addUpdateListener(this);
    addAnimation(this.textAnimation);
    paramLottieDrawable = paramLayer.getTextProperties();
    if (paramLottieDrawable == null) {}
    label125:
    label133:
    label208:
    label245:
    do
    {
      break label133;
      if (paramLottieDrawable == null) {
        if (paramLottieDrawable != null) {
          break label208;
        }
      }
      for (;;)
      {
        if (paramLottieDrawable != null) {
          break label245;
        }
        return;
        if (paramLottieDrawable.color == null) {
          break;
        }
        this.colorAnimation = paramLottieDrawable.color.createAnimation();
        this.colorAnimation.addUpdateListener(this);
        addAnimation(this.colorAnimation);
        break;
        if (paramLottieDrawable.stroke == null) {
          break label125;
        }
        this.strokeAnimation = paramLottieDrawable.stroke.createAnimation();
        this.strokeAnimation.addUpdateListener(this);
        addAnimation(this.strokeAnimation);
        break label125;
        if (paramLottieDrawable.strokeWidth != null)
        {
          this.strokeWidthAnimation = paramLottieDrawable.strokeWidth.createAnimation();
          this.strokeWidthAnimation.addUpdateListener(this);
          addAnimation(this.strokeWidthAnimation);
        }
      }
    } while (paramLottieDrawable.tracking == null);
    this.trackingAnimation = paramLottieDrawable.tracking.createAnimation();
    this.trackingAnimation.addUpdateListener(this);
    addAnimation(this.trackingAnimation);
  }
  
  private void drawCharacter(char[] paramArrayOfChar, Paint paramPaint, Canvas paramCanvas)
  {
    if (paramPaint.getColor() != 0) {
      if (paramPaint.getStyle() == Paint.Style.STROKE) {
        break label29;
      }
    }
    label29:
    while (paramPaint.getStrokeWidth() != 0.0F)
    {
      paramCanvas.drawText(paramArrayOfChar, 0, 1, 0.0F, 0.0F, paramPaint);
      return;
      return;
    }
  }
  
  private void drawCharacterAsGlyph(FontCharacter paramFontCharacter, Matrix paramMatrix, float paramFloat, DocumentData paramDocumentData, Canvas paramCanvas)
  {
    paramFontCharacter = getContentsForCharacter(paramFontCharacter);
    int i = 0;
    if (i >= paramFontCharacter.size()) {
      return;
    }
    Path localPath = ((ContentGroup)paramFontCharacter.get(i)).getPath();
    localPath.computeBounds(this.rectF, false);
    this.matrix.set(paramMatrix);
    this.matrix.preScale(paramFloat, paramFloat);
    localPath.transform(this.matrix);
    if (!paramDocumentData.strokeOverFill)
    {
      drawGlyph(localPath, this.strokePaint, paramCanvas);
      drawGlyph(localPath, this.fillPaint, paramCanvas);
    }
    for (;;)
    {
      i += 1;
      break;
      drawGlyph(localPath, this.fillPaint, paramCanvas);
      drawGlyph(localPath, this.strokePaint, paramCanvas);
    }
  }
  
  private void drawCharacterFromFont(char paramChar, DocumentData paramDocumentData, Canvas paramCanvas)
  {
    this.tempCharArray[0] = ((char)paramChar);
    if (!paramDocumentData.strokeOverFill)
    {
      drawCharacter(this.tempCharArray, this.strokePaint, paramCanvas);
      drawCharacter(this.tempCharArray, this.fillPaint, paramCanvas);
      return;
    }
    drawCharacter(this.tempCharArray, this.fillPaint, paramCanvas);
    drawCharacter(this.tempCharArray, this.strokePaint, paramCanvas);
  }
  
  private void drawGlyph(Path paramPath, Paint paramPaint, Canvas paramCanvas)
  {
    if (paramPaint.getColor() != 0) {
      if (paramPaint.getStyle() == Paint.Style.STROKE) {
        break label25;
      }
    }
    label25:
    while (paramPaint.getStrokeWidth() != 0.0F)
    {
      paramCanvas.drawPath(paramPath, paramPaint);
      return;
      return;
    }
  }
  
  private void drawTextGlyphs(DocumentData paramDocumentData, Matrix paramMatrix, Font paramFont, Canvas paramCanvas)
  {
    float f2 = paramDocumentData.size / 100.0F;
    float f3 = Utils.getScale(paramMatrix);
    String str = paramDocumentData.text;
    int i = 0;
    if (i >= str.length()) {
      return;
    }
    int j = FontCharacter.hashFor(str.charAt(i), paramFont.getFamily(), paramFont.getStyle());
    FontCharacter localFontCharacter = (FontCharacter)this.composition.getCharacters().get(j);
    float f4;
    float f5;
    float f1;
    if (localFontCharacter != null)
    {
      drawCharacterAsGlyph(localFontCharacter, paramMatrix, f2, paramDocumentData, paramCanvas);
      f4 = (float)localFontCharacter.getWidth();
      f5 = this.composition.getDpScale();
      f1 = paramDocumentData.tracking / 10.0F;
      if (this.trackingAnimation != null) {
        break label157;
      }
    }
    for (;;)
    {
      paramCanvas.translate(f1 * f3 + f4 * f2 * f5 * f3, 0.0F);
      i += 1;
      break;
      label157:
      f1 = ((Float)this.trackingAnimation.getValue()).floatValue() + f1;
    }
  }
  
  private void drawTextWithFont(DocumentData paramDocumentData, Font paramFont, Matrix paramMatrix, Canvas paramCanvas)
  {
    float f2 = Utils.getScale(paramMatrix);
    paramMatrix = this.lottieDrawable.getTypeface(paramFont.getFamily(), paramFont.getStyle());
    TextDelegate localTextDelegate;
    if (paramMatrix != null)
    {
      paramFont = paramDocumentData.text;
      localTextDelegate = this.lottieDrawable.getTextDelegate();
      if (localTextDelegate != null) {
        break label117;
      }
    }
    int i;
    for (;;)
    {
      this.fillPaint.setTypeface(paramMatrix);
      this.fillPaint.setTextSize(paramDocumentData.size * this.composition.getDpScale());
      this.strokePaint.setTypeface(this.fillPaint.getTypeface());
      this.strokePaint.setTextSize(this.fillPaint.getTextSize());
      i = 0;
      if (i < paramFont.length()) {
        break;
      }
      return;
      return;
      label117:
      paramFont = localTextDelegate.getTextInternal(paramFont);
    }
    char c = paramFont.charAt(i);
    drawCharacterFromFont(c, paramDocumentData, paramCanvas);
    this.tempCharArray[0] = c;
    float f3 = this.fillPaint.measureText(this.tempCharArray, 0, 1);
    float f1 = paramDocumentData.tracking / 10.0F;
    if (this.trackingAnimation == null) {}
    for (;;)
    {
      paramCanvas.translate(f1 * f2 + f3, 0.0F);
      i += 1;
      break;
      f1 = ((Float)this.trackingAnimation.getValue()).floatValue() + f1;
    }
  }
  
  private List<ContentGroup> getContentsForCharacter(FontCharacter paramFontCharacter)
  {
    List localList;
    int j;
    ArrayList localArrayList;
    int i;
    if (!this.contentsForCharacter.containsKey(paramFontCharacter))
    {
      localList = paramFontCharacter.getShapes();
      j = localList.size();
      localArrayList = new ArrayList(j);
      i = 0;
    }
    for (;;)
    {
      if (i >= j)
      {
        this.contentsForCharacter.put(paramFontCharacter, localArrayList);
        return localArrayList;
        return (List)this.contentsForCharacter.get(paramFontCharacter);
      }
      ShapeGroup localShapeGroup = (ShapeGroup)localList.get(i);
      localArrayList.add(new ContentGroup(this.lottieDrawable, this, localShapeGroup));
      i += 1;
    }
  }
  
  void drawLayer(Canvas paramCanvas, Matrix paramMatrix, int paramInt)
  {
    paramCanvas.save();
    DocumentData localDocumentData;
    Font localFont;
    if (this.lottieDrawable.useTextGlyphs())
    {
      localDocumentData = (DocumentData)this.textAnimation.getValue();
      localFont = (Font)this.composition.getFonts().get(localDocumentData.fontName);
      if (localFont == null) {
        break label202;
      }
      if (this.colorAnimation != null) {
        break label207;
      }
      this.fillPaint.setColor(localDocumentData.color);
      label73:
      if (this.strokeAnimation != null) {
        break label230;
      }
      this.strokePaint.setColor(localDocumentData.strokeColor);
      label92:
      paramInt = ((Integer)this.transform.getOpacity().getValue()).intValue() * 255 / 100;
      this.fillPaint.setAlpha(paramInt);
      this.strokePaint.setAlpha(paramInt);
      if (this.strokeWidthAnimation != null) {
        break label253;
      }
      float f = Utils.getScale(paramMatrix);
      this.strokePaint.setStrokeWidth(f * (localDocumentData.strokeWidth * this.composition.getDpScale()));
      label169:
      if (this.lottieDrawable.useTextGlyphs()) {
        break label276;
      }
      drawTextWithFont(localDocumentData, localFont, paramMatrix, paramCanvas);
    }
    for (;;)
    {
      paramCanvas.restore();
      return;
      paramCanvas.setMatrix(paramMatrix);
      break;
      label202:
      paramCanvas.restore();
      return;
      label207:
      this.fillPaint.setColor(((Integer)this.colorAnimation.getValue()).intValue());
      break label73;
      label230:
      this.strokePaint.setColor(((Integer)this.strokeAnimation.getValue()).intValue());
      break label92;
      label253:
      this.strokePaint.setStrokeWidth(((Float)this.strokeWidthAnimation.getValue()).floatValue());
      break label169;
      label276:
      drawTextGlyphs(localDocumentData, paramMatrix, localFont, paramCanvas);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\layer\TextLayer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */