package com.airbnb.lottie.animation.content;

import android.annotation.TargetApi;
import android.graphics.Path;
import android.graphics.Path.Op;
import android.os.Build.VERSION;
import com.airbnb.lottie.model.content.MergePaths;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@TargetApi(19)
public class MergePathsContent
  implements PathContent, GreedyContent
{
  private final Path firstPath = new Path();
  private final MergePaths mergePaths;
  private final String name;
  private final Path path = new Path();
  private final List<PathContent> pathContents = new ArrayList();
  private final Path remainderPath = new Path();
  
  public MergePathsContent(MergePaths paramMergePaths)
  {
    if (Build.VERSION.SDK_INT >= 19)
    {
      this.name = paramMergePaths.getName();
      this.mergePaths = paramMergePaths;
      return;
    }
    throw new IllegalStateException("Merge paths are not supported pre-KitKat.");
  }
  
  private void addPaths()
  {
    int i = 0;
    for (;;)
    {
      if (i >= this.pathContents.size()) {
        return;
      }
      this.path.addPath(((PathContent)this.pathContents.get(i)).getPath());
      i += 1;
    }
  }
  
  @TargetApi(19)
  private void opFirstPathWithRest(Path.Op paramOp)
  {
    int k = 0;
    this.remainderPath.reset();
    this.firstPath.reset();
    int i = this.pathContents.size() - 1;
    PathContent localPathContent;
    if (i < 1)
    {
      localPathContent = (PathContent)this.pathContents.get(0);
      if ((localPathContent instanceof ContentGroup)) {
        break label204;
      }
      this.firstPath.set(localPathContent.getPath());
    }
    for (;;)
    {
      this.path.op(this.firstPath, this.remainderPath, paramOp);
      return;
      localPathContent = (PathContent)this.pathContents.get(i);
      if (!(localPathContent instanceof ContentGroup)) {
        this.remainderPath.addPath(localPathContent.getPath());
      }
      Path localPath;
      for (;;)
      {
        i -= 1;
        break;
        localList = ((ContentGroup)localPathContent).getPathList();
        int j = localList.size() - 1;
        while (j >= 0)
        {
          localPath = ((PathContent)localList.get(j)).getPath();
          localPath.transform(((ContentGroup)localPathContent).getTransformationMatrix());
          this.remainderPath.addPath(localPath);
          j -= 1;
        }
      }
      label204:
      List localList = ((ContentGroup)localPathContent).getPathList();
      i = k;
      while (i < localList.size())
      {
        localPath = ((PathContent)localList.get(i)).getPath();
        localPath.transform(((ContentGroup)localPathContent).getTransformationMatrix());
        this.firstPath.addPath(localPath);
        i += 1;
      }
    }
  }
  
  public void absorbContent(ListIterator<Content> paramListIterator)
  {
    if (!paramListIterator.hasPrevious()) {}
    for (;;)
    {
      if (!paramListIterator.hasPrevious())
      {
        return;
        if (paramListIterator.previous() != this) {
          break;
        }
        continue;
      }
      Content localContent = (Content)paramListIterator.previous();
      if ((localContent instanceof PathContent))
      {
        this.pathContents.add((PathContent)localContent);
        paramListIterator.remove();
      }
    }
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public Path getPath()
  {
    this.path.reset();
    switch (this.mergePaths.getMode())
    {
    }
    for (;;)
    {
      return this.path;
      addPaths();
      continue;
      opFirstPathWithRest(Path.Op.UNION);
      continue;
      opFirstPathWithRest(Path.Op.REVERSE_DIFFERENCE);
      continue;
      opFirstPathWithRest(Path.Op.INTERSECT);
      continue;
      opFirstPathWithRest(Path.Op.XOR);
    }
  }
  
  public void setContents(List<Content> paramList1, List<Content> paramList2)
  {
    int i = 0;
    for (;;)
    {
      if (i >= this.pathContents.size()) {
        return;
      }
      ((PathContent)this.pathContents.get(i)).setContents(paramList1, paramList2);
      i += 1;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\airbnb\lottie\animation\content\MergePathsContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */