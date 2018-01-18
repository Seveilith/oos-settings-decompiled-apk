package com.airbnb.lottie.model;

import android.content.res.Resources;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieComposition.Factory;
import com.airbnb.lottie.OnCompositionLoadedListener;
import java.io.InputStream;

public final class FileCompositionLoader
  extends CompositionLoader<InputStream>
{
  private final OnCompositionLoadedListener loadedListener;
  private final Resources res;
  
  public FileCompositionLoader(Resources paramResources, OnCompositionLoadedListener paramOnCompositionLoadedListener)
  {
    this.res = paramResources;
    this.loadedListener = paramOnCompositionLoadedListener;
  }
  
  protected LottieComposition doInBackground(InputStream... paramVarArgs)
  {
    return LottieComposition.Factory.fromInputStream(this.res, paramVarArgs[0]);
  }
  
  protected void onPostExecute(LottieComposition paramLottieComposition)
  {
    this.loadedListener.onCompositionLoaded(paramLottieComposition);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\model\FileCompositionLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */