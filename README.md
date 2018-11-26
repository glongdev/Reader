# Reader
> A reader that is easy to integrate and extend.


![](images/微信图片_20181126135424.jpg)

同步[我的博客地址](https://www.jianshu.com/p/15aad84c11c7)，并且
 **本篇文章已授权微信公众号 guolin_blog （郭霖）独家发布**


先看拥有部分功能的效果图

![](images/20181123_204344.gif)
#### 一款小说类APP阅读器的业务逻辑

首先我们需要知道一款小说类app加载阅读器的业务流程。大致的流程是：

1.先获取章节列表(目录)，数据大约长这个样子

```json
[
    {
        "chapterId":"00001",
        "chapterName":"第一章 郭芙蓉"
    },
    {
        "chapterId":"00002",
        "chapterName":"第二章 吕秀才"
    },
    {
        "chapterId":"00003",
        "chapterName":"第三章 白大侠"
    },
    ...等等
]
```

2.然后通过章节列表中的一个Item获取章节内容，数据大约长这个样子

```json
{
    "chapterId":"00001",
    "cahpterName":"郭芙蓉",
    "content":"年纪轻轻为追求自由寻求真正的江湖道义就敢于离开父母的庇护背井离乡一个人跑出来闯荡，按照及性格和父亲身份分析，郭芙蓉是千金小姐，不管发生什么都能一脸不在乎相信一切都会变好，因为父亲是大侠中的大侠，八个师兄又从小和父亲习武成为六扇门神捕，她也很向往这种生活，也想成为父亲一样大侠中的大侠，闯荡江湖来到同福客栈找到了所向往的生活后安定下来，和秀才是一对欢喜冤家，几十年后有两个女儿，一个是小女儿龙门镖局的镖师吕青橙...."
}
```

3.最后阅读器将章节内容分成N页，并分页显示。

Reader就是基于这种业务逻辑提供了如下功能：

- 分页、翻页功能
- 翻页动效（目前有仿真(单项)、仿真(双向)、覆盖、滑动(左右)、无效果）
- 缓存系列功能（增、删、改、查）
- 跳转指定章节、指定文字位置
- 自定义分段符
- 背景、文字颜色、大小、行间距等设置
- 正文位置设置

总而言之，只需要指定业务逻辑中，章节列表Item和章节内容的数据格式，便可以工作了。集成和用法都比较简单。

#### Reader的简单使用

- 首先，在XML中配置View，跟所有View一样，无需多说

  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
      ...省略部分无关代码>
  
      <com.glong.reader.widget.ReaderView
          android:id="@+id/simple_reader_view"
          android:layout_width="match_parent"
          android:layout_height="match_parent"/>
  
  </android.support.constraint.ConstraintLayout>
  ```

  在Activity的`onCreate()`方法中通过`findViewById`或注解方式获取ReaderView实例

- 给ReaderView设置ReaderManager

  ```java
      private void initReader() {
          mReaderView = findViewById(R.id.simple_reader_view);
  
          mReaderManager = new ReaderView.ReaderManager();
          mReaderView.setReaderManager(mReaderManager);
      }
  ```

- 设置`Adapter<K,T>`（敲黑板，划重点，前年20分大题，去年没考，今年必考...）

  其中泛型K代表章节列表Item的数据类型，T代表章节内容的数据类型。假如我们的业务逻辑章节列表Item数据类型是`ChapterItemBean`，章节内容数据类型是`BookContentBean`，可按如下方式设置Adapter

  ```java
  mReaderView.setAdapter(new ReaderView.Adapter<ChapterItemBean, ChapterContentBean>() {
  
          @Override
          public String obtainCacheKey(ChapterItemBean chapterItemBean) {
              return chapterItemBean.getChapterId() + userId;
          }
  
          @Override
          public String obtainChapterName(ChapterItemBean chapterItemBean) {
              return chapterItemBean.getChapterName();
          }
  
          @Override
          public String obtainChapterContent(ChapterContentBean contentBean) {
              return contentBean.getChapterContent();
          }
  
          /**
           * 这个方法运行在子线程中，同步返回章节内容
           */
          @Override
          public ChapterContentBean downLoad(ChapterItemBean chapterItemBean) {
              return LocalServer.syncDownloadContent(chapterItemBean);
          }
      });
  ```

  为什么要有Adapter？



  因为我们的Reader是跟业务逻辑解耦的，同时又是跟业务逻辑直接对接的。Reader不知道业务逻辑要用什么key缓存章节内容；虽然拿到了ChapterItemBean但Reader不知道章节标题是哪个字段；虽然拿到了BookContentBean但Reader不知道章节内容是哪个字段。



  这就好比生活中的插座和电子设备的关系，插座可以给任何电子设备供电，但是插座又不知道你到底插的是电脑还是手机还是剃须刀，不知道你要多大的电压，所以就有了变压器。往往电子设备提供变压器。（Adapter适配器）



  同样的道理，Adapter将数据类型JavaBean转换成阅读器真正需要的字符。

  download又是什么鬼？

  前面我们讲阅读器业务逻辑的时候，说到，小说类app是先拿到章节列表，然后通过章节列表里的一个Item获取章节内容，这个`download(ChapterItemBean chapterItemBean)`就是通过章节列表的一个Item获取章节内容的。

  或者可以不用在`download(ChapterItemBean chapterItemBean)`实现同步下载了，直接让他返回null就行了，但需要重写Adapter的`requestParams(ChapterItemBean chapterItemBean)`方法

  ```java
  	@Override
      public Request requestParams(ChapterItemBean chapterItemBean) {
          return new Request.Builder()
                  .addHeader("token", "userToken")
                  .addUrlParams("bookId", "123")
                  .addUrlParams("bookName", "123")
                  .addBody("chapterId", chapterItemBean.getChapterId())
                  .post()
                  .build();
      }   
  ```

- 最后获取章节列表，调用`notifyDataSetChanged()`即可

  ```java
  	/*
       * 获取章节列表
       */
       LocalServer.getChapterList("123", new LocalServer.OnResponseCallback() {
          @Override
          public void onSuccess(List<ChapterItemBean> chapters) {
              mAdapter.setChapterList(chapters);
              
              mAdapter.notifyDataSetChanged();
          }
  
          @Override
          public void onError(Exception e) {
  
          }
      });
  ```

  运行一下我们的项目，我们已经迫不及待的想看一下效果了

  ![](/images/video2gif_20181123_183236.gif)

大家常用RecyclerView，可以把ReaderView当作RecyclerView，把ReaderManager当作LayoutManager，把ReaderView#Adapter当作RecyclerView#Adapter，是不是简单了很多。事实上，他们的用法以及功能划分也及其相似。

#### 基本设置

 1.`ReaderView#setAdapter(Adapter adapter)`

设置适配器，前面已经用过了，跟RecyclerView的setAdapter()类似；



2.`ReaderView#setReaderManager(ReaderManager readerManager)`

跟RecyclerView的setLayoutManager类似，关于ReaderManager以及它的API后面详细说；



3.`ReaderView#setTextSize(int textSize)`

设置阅读器文字大小



4.`View#setBackground(xxxxxx)`、`View#setBackgroundColor(int color)`

这是View设置背景的方法，通过原生的设置View背景的方法设置Reader的背景，可以是color、Bitmap、Drawable；



5.`ReaderView#setLineSpace(int lineSpace)`

设置阅读器文字间距；



6.`ReaderView#setBodyTextPadding(int[] padding)`

设置阅读器正文距离View上下左右边界的位置的位置，padding是长度为4的int数组，元素0~3分别代表left、top、right、bottom四个值；



7.`ReaderView#setBatteryWidthAndHeight(int[] widthAndHeight)`

设置电池的长宽，参数widthAndHeight是长度为2的int数组，元素0~1分别代表电池的宽度和高度

能否自定义电池？可以的，后面将扩展的时候详细说；



8.`ReaderView#setColorsConfig(ColorsConfig colorsConfig)`

设置界面颜色相关的对象，ColorsConfig是所有颜色的封装，比如电池颜色、文字颜色...

为什么要把颜色封装，不能像`setTextSize()`一样直接放在ReaderView里面？

因为一般情况下背景和其他元素的颜色都是对应的，比如当设置了黑色的背景时，这个时候文字颜色应该设置为白色，电池颜色也应该设置为白色；



9.`ReaderView#setEffect(@NonNull Effect effect)`

设置阅读器的翻页动效，目前已有动效如下表格

| 动效                | 描述             | 父类   |
| ------------------- | ---------------- | ------ |
| EffectOfRealOneWay  | 仿真（单向）     | Effect |
| EffectOfRealBothWay | 仿真（双向）     | Effect |
| EffectOfCover       | 覆盖             | Effect |
| EffectOfSlide       | 滑动（左右滑动） | Effect |
| EffectOfNon         | 无效果（瞬变）   | Effect |

“可不可以自定义翻页动效？”

![](images/QQ%E6%88%AA%E5%9B%BE20181123224610.png)

of course！只需要继承自Effect即可；



10.`ReaderView#setPageChangedCallback(@NonNull PageChangedCallback pageChangedCallback)`

阅读器翻页的回调；



11.`ReaderView#setPageDrawingCallback(@NonNull PageDrawingCallback pageDrawingCallback)`

阅读器需要刷新页面时的回调（一般用不到）



12.`ReaderView#invalidateCurrPage()`

刷新当前页；

**不推荐使用**



13.`ReaderView#invalidateNextPage()`

刷新下一页；

 **不推荐使用**



14.`ReaderView#invalidateBothPage()`

刷新当前页和下一页；

 **墙裂建议使用该方法**

当需要主动刷新当前阅读器UI时，推荐使用`invalidateBothPage()`方法。

“*我只需要刷新当前页，不需要刷新下一页（下一页用户也看不到啊），为什么还要两页同时刷新？*”

の，这个还真不太好解释，因为这里的上一页/下一页和我们理解的上一页/下一页并不太一样，就好比我们看到的3D效果其实也是2D实现的，都是骗眼睛的。当翻向下一页的时候我们看到的是下一页，当翻向上一页的时候我们看到的其实还是下一页。有点绕，这其实跟软件的设计有关，翻页设计时只有当前页和下一页，没有上一页（节省内存），那翻向上一页怎么办？这里的上一页也就是下一页。

![](/images/QQ%E6%88%AA%E5%9B%BE20181123225319.png)

总之，只有理解原理后才建议调用`invalidateCurrPage()`和`invalidateNextPage()`。否则就调用`invalidateBothPage()`确保显示没有问题。

婆婆妈妈说了一大堆，总结就俩字：**当需要刷新界面时使用`invalidateBothPage()`**。



15.`ReaderView#addParagraph(String paragraph)`

增加分段符；

ReaderView默认的分段符有`<br><br>`、`<br>`、`</p>`，比如你的业务逻辑中是使用`sb`分段的，那么只需要调用`ReaderView#addParagraph("sb")`，比如你的业务逻辑使用`</sb>`分段的那就传入`</sb>`即可。



16.`ReaderView#getBodyTextPaint()`

获取正文画笔；

有了画笔，可以设置字体、文字的各种样式等等。

比如要设置字体：

```java
Paint paint = mReaderView.getBodyTextPaint();
Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
paint.setTypeface( font );

// 刷新页面
mReaderView.invalidateBothPage();
```



17.其他`ReaderView#getxxx()`方法

省略...



 **OK**，ReaderView的API基本介绍完了，根据这些API我们稍微修改上面已经写好的阅读器。是其支持基本设置。

按照惯例线上效果图：

![](images/20181123_204344.gif)



省略xml布局文件中添加Button；

省略`findViewById(id)`、`setOnClickListener(View.OnClickListener listener)`操作；

收到点击事件后设置相关的背景和翻页动效；

```java
@Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reader_bg_0:
                mReaderView.setBackgroundColor(getResources().getColor(R.color.reader_bg_0));
                break;
            case R.id.reader_bg_1:
                mReaderView.setBackgroundColor(getResources().getColor(R.color.reader_bg_1));
                break;
            case R.id.reader_bg_2:
                mReaderView.setBackgroundColor(getResources().getColor(R.color.reader_bg_2));
                break;
            case R.id.reader_bg_3:
                mReaderView.setBackgroundColor(getResources().getColor(R.color.reader_bg_3));
                break;
            case R.id.effect_real_one_way:
            case R.id.effect_default:
                mReaderView.setEffect(new EffectOfRealOneWay(this));
                break;
            case R.id.effect_real_both_way:
                mReaderView.setEffect(new EffectOfRealBothWay(this));
                break;
            case R.id.effect_cover:
                mReaderView.setEffect(new EffectOfCover(this));
                break;
            case R.id.effect_slide:
                mReaderView.setEffect(new EffectOfSlide(this));
                break;
            case R.id.effect_non:
                mReaderView.setEffect(new EffectOfNon(this));
                break;
        }
    }
```

动态设置文字大小textSize和行间距lineSpace

```java
SeekBar textSizeSeek = findViewById(R.id.text_size_seek_bar);
        textSizeSeek.setMax(100);
        textSizeSeek.setProgress(mReaderView.getTextSize());
        textSizeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mReaderView.setTextSize(progress);
            }
			// 省略无关代码
        });

        SeekBar lineSpaceSeek = findViewById(R.id.line_space_seek_bar);
        lineSpaceSeek.setMax(100);
        lineSpaceSeek.setProgress(mReaderView.getLineSpace());
        lineSpaceSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mReaderView.setLineSpace(seekBar.getProgress());
            }
			// 省略无关代码
        });
```



这里没有颜色相关设置，其用法也比较简单，就是简单的调用方法，这里不再赘述。



自定义PageChangedCallback回调。比如，在翻页时，当没有上一页/下一页时 弹出Toast提示

```java
mReaderView.setPageChangedCallback(new PageChangedCallback() {
            @Override
            public TurnStatus toPrevPage() {
                TurnStatus turnStatus = readerManager.toPrevPage();
                if (turnStatus == TurnStatus.NO_PREV_CHAPTER) {
                    Toast.makeText(NormalReaderActivity.this, "没有上一页啦", Toast.LENGTH_SHORT).show();
                }
                return turnStatus;
            }

            @Override
            public TurnStatus toNextPage() {
                TurnStatus turnStatus = readerManager.toNextPage();
                if (turnStatus == TurnStatus.NO_NEXT_CHAPTER) {
                    Toast.makeText(NormalReaderActivity.this, "没有下一页啦", Toast.LENGTH_SHORT).show();
                }
                return turnStatus;
            }
        });
```

通过`setPageChangedCallback()`设置PageChangedCallback，可以监听当滑向下一页/上一页时的状态。枚举TurnStatus共有五种状态；

| 枚举值          | 描述                                                         |
| --------------- | ------------------------------------------------------------ |
| IDLE            | 空闲状态，未翻页                                             |
| LOAD_SUCCESS    | 加载上一页/下一页/指定章节 成功                              |
| LOAD_FAILURE    | 加载失败（如下载失败）                                       |
| DOWNLOADING     | 正在下载                                                     |
| NO_NEXT_CHAPTER | 没有上一章了（在第一章第一页时，再往前翻就会返回这个状态）   |
| NO_PREV_CHAPTER | 没有下一章了（再最后一章最后一页时，再往后翻就会返回这个状态） |

当然，通过这个回调也可以实现，当翻向下一页时直接跳到下一章。当翻向上一页的时候直接跳到上一章。等等。需要结合ReaderView#ReaderManager API实现。



#### ReaderView#ReaderManager 的API

1.`TurnStatus toPrevPage()` & `TurnStatus toNextPage()`

这两个方法前面已经用过了。意思是将数据置为上一页/下一页(如果当前章节已经在第一页/最后一页时，自动跳到下一章/上一章)。

比如通过音量键翻页，重写Activity的onKeyDown和onKeyUp方法

```java
@Override
public boolean onKeyUp(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
        mReaderManager.toNextPage();
        mReaderView.invalidateBothPage();
        return true;// 返回ture防止翻页有声音
    } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
        mReaderManager.toPrevPage();
        mReaderView.invalidateBothPage();
        return true;
    }
    return super.onKeyUp(keyCode, event);
}

@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch (keyCode) {
        case KeyEvent.KEYCODE_VOLUME_DOWN:
            return true;//返回turn 不弹出音量控件
        case KeyEvent.KEYCODE_VOLUME_UP:
            return true;
    }
    return super.onKeyDown(keyCode, event);
}
```

因为`toPrevPage()` /`toNextPage()`仅仅是将数据置为上一页/下一页。所以，还需要调用`ReaderView#invalidateBothPage()`刷新页面。

“*前面设置PageChangedCallback时为什么不调用`ReaderView#invalidateBothPage()`呢？*”

因为PageChangedCallback是ReaderView的回调，其内部已经实现了刷新逻辑。**务必不要调用刷新页面的任何方法**;



2.`TurnStatus toPrevChapter()` & `TurnStatus toNextChapter()`

跳转到上一章最后字符（即最后一页） & 跳转到下一章第一个字符（即第一页）。如果是手动跳转需要刷新页面；



3.`TurnStatus toPrevChapter(int charIndex)` & `TurnStatus toNextChapter(int charIndex)`

跳转到上一章指定字符 & 跳转到下一章指定字符  ， -1代表最后一个字符。如果是手动跳转需要刷新页面；



4.`TurnStatus toSpecifiedChapter(final int chapterIndex, final int charIndex)`

跳转到指定章节的指定字符位置，比如要跳转到第5章的第一页`toSpecifiedChapter(4,0)`，要跳转到第5章的最后一页`toSpecifiedChapter(4,-1)`。如果是手动跳转需要刷新页面；



5.`setCache(Cache cache)`

设置缓存。如果没有设置，ReaderManager会设置一个默认的缓存；



6.`setCustomReaderResolve(ReaderResolve readerResolve)`

设置自定义的ReaderResolve。ReaderResolve处理所有页面上计算、具体画文字、图标等等。



7.`setOnReaderWatcherListener(OnReaderWatcherListener onReaderWatcherListener)`

关于OnReaderWatcherListener，看OnReaderWathcListener的定义，一目了然

```java
	/**
     * 页码发生了变化
     *
     * @param pageIndex 第pageIndex页（从第0页开始）
     */
    void onPageChanged(int pageIndex);

    /**
     * 章节发生了变化
     *
     * @param chapterIndex 跳转到了第chapterIndex章
     * @param pageIndex    跳转到了这章的第pageIndex页（从第0页开始）
     */
    void onChapterChanged(int chapterIndex, int pageIndex);

    /**
     * 开始下载当前所需章节时调用（方便弹出提示等等）
     * 当下载缓存时不会回调
     *
     * @param chapterIndex 章节索引
     */
    void onChapterDownloadStart(int chapterIndex);

    /**
     * 当前所需章节下载成功后回调
     * 仅下载缓存时不会回调
     *
     * @param chapterIndex 章节索引
     */
    void onChapterDownloadSuccess(int chapterIndex);

    /**
     * 当前所需章节下载成功后回调
     * 仅下载缓存时不会回调
     *
     * @param chapterIndex 章节索引
     */
    void onChapterDownloadError(int chapterIndex);
```



8.`void onAdapterChanged(Adapter oldAdapter, Adapter adapter)`

当Adapter发生变化时回调；



9.`startFromCache(String key, int chapterIndex, int charIndex, @NonNull String chapterName)`

最开始我们说了，小说类app的业务逻辑先获取章节列表，然后通过章节列表的某一项获取章节内容，最后交给阅读器显示。那么问题来了，①当用户之前已经看过了，表示已经有缓存了，按照正常的业务逻辑要显示出来内容，必须得在章节列表下载完成后，这显然需要一定得等待（网络请求），用户体验肯定不好。②用户第二次打开应该显示上次观看得位置，如果按照正常逻辑显示的是第一章第一页，这显然也是不对的。

所以当明确了有阅读历史（比如用户就是从阅读历史启动的）时，调用这个方法。



10.`startFromCache(File cacheDir, String key, int chapterIndex, int charIndex, @NonNull String chapterName)`

跟9类似，这里多了一个参数File cacheDir，如果当缓存的时候制定了自定义的路径，这里就需要传入自定义的路径。如果没有设置，默认路径是context.getCacheDir();



到这里，ReaderView#ReaderManager的API也介绍完了。

核心类其实也就3个，ReaderView 、ReaderView#Adapter、ReaderView#ReaderManager,通过这三个类可以完成阅读器的基本功能了。后续再讲解扩展，以及原理。
