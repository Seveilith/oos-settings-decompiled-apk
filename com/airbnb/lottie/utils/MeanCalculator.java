package com.airbnb.lottie.utils;

public class MeanCalculator
{
  private int n;
  private float sum;
  
  public void add(float paramFloat)
  {
    this.sum += paramFloat;
    this.n += 1;
    if (this.n != Integer.MAX_VALUE) {
      return;
    }
    this.sum /= 2.0F;
    this.n /= 2;
  }
  
  public float getMean()
  {
    if (this.n != 0) {
      return this.sum / this.n;
    }
    return 0.0F;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\utils\MeanCalculator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */