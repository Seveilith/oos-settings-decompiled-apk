package com.airbnb.lottie.model.content;

import android.support.annotation.Nullable;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.animation.content.Content;
import com.airbnb.lottie.model.layer.BaseLayer;

public abstract interface ContentModel
{
  @Nullable
  public abstract Content toContent(LottieDrawable paramLottieDrawable, BaseLayer paramBaseLayer);
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\content\ContentModel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */