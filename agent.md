#每次对话时记住之前的对话内容，直到用户说“结束对话”。 
1: 单个文件不要太过冗余，对于复杂的ui和功能可以拆分成多个文件，保持每个文件的职责单一。
2: 代码注释要清晰，尤其是函数和类的注释，
3: 代码风格要统一，遵循团队的编码规范。
4: 不要使用固定硬编码，文本应该放在资源文件中，方便后续的维护和国际化。
5：对于ui需要加上preview，方便kotin调试ui
6：间距最好使用百分比，固定距离会导致多设备不兼容
7: 对于ui，有preference_debug_outline 需要添加边界

## 首页 UI 设计规范

> 2026-06-12 通过 `adb devices` 检查时未发现连接设备或模拟器，因此本规范先基于首页 Compose 源码整理。后续有设备时，需要用 `adb exec-out screencap -p` 复核真实截图与规范是否一致。

### 设计语言

1: 整体风格是轻量、柔和、卡片驱动的日记/生活记录工具，不做重色块后台管理风格，也不做营销页风格。
2: 页面以大留白、浅色雾面渐变背景、圆形/胶囊控件、强阴影主卡片为核心。
3: 首页首屏的视觉重心是堆叠推荐卡片，其他信息只做辅助，不要抢卡片层级。
4: UI 需要保持安静、温和、可触摸，避免尖锐边角、密集文字、复杂边框和过多说明文本。

### 背景与主题

1: 页面背景必须优先使用 `LocalAppAppearance.current.backgroundBrush`，不要在页面里随意硬编码背景色。
2: 默认 `mist` 主题使用浅雾渐变：`#F8F9FB -> #EFF2F6 -> #F8F9FB`。
3: 已有主题包括 `mist`、`sunrise`、`forest`、`night`，后续页面需要通过 `AppAppearance` 读取全局主题颜色。
4: 深色模式下如果背景为 `mist`，当前逻辑会切到 `night`，后续页面要遵循这个行为。

### 布局与留白

1: 首页根布局使用全屏 `Box`，背景铺满，内容区底部预留底部导航高度。
2: 顶部栏横向内边距约 `24.dp`，顶部加 `statusBarsPadding()`，头像与右侧圆形按钮分布在两端。
3: 主内容垂直位置使用屏幕高度比例控制，首页卡片区顶部间距约为 `maxHeight * 0.1f`，后续页面不要全部写死垂直距离。
4: 底部导航使用 `navigationBarsPadding()`，底部保留安全区和 12dp 左右的视觉呼吸。

### 圆角与阴影

1: 主推荐卡片圆角约 `28-30.dp`，小推荐卡可以使用 `22-24.dp`，不要小于 16dp。
2: 圆形按钮必须使用 `CircleShape`，顶部按钮直径约 `46.dp`，头像约 `42.dp`，主加号按钮约 `56.dp`。
3: 底部导航 dock 使用全胶囊 `RoundedCornerShape(999.dp)`，高度约 `64.dp`。
4: 主卡阴影较重，首页推荐卡 elevation 约 `36.dp`；顶部圆形按钮约 `8-12.dp`；底部 dock 约 `15.dp`；普通信息卡约 `8.dp`。
5: 阴影颜色需要低透明度，优先使用带 alpha 的暖灰或卡片 palette shadow，不要使用纯黑高透明阴影。

### 推荐卡片

1: 推荐卡片颜色必须通过 `recommendationPalette(imageAsset)` 取得，禁止在业务页面重新定义相同类型颜色。
2: 卡片视觉结构固定为：渐变底色 + 顶部 radial glow + motion 图片 + 底部深色 scrim + 分类 pill + 标题。
3: 首页主卡尺寸由 `HomeRecommendationStage` 控制，宽度约 `(maxWidth - 128.dp).coerceIn(214.dp, 246.dp)`，高度约 `302.dp`。
4: 卡片堆叠层级为 3 层：前景卡正常显示，后两张缩小、上移、左右错位并轻微旋转。
5: 卡片标题放在底部左侧，颜色 `#FFFCF3`，字号约 `23.sp`，最多 2 行。
6: 分类 pill 使用卡片 palette 的 `pillBackground`、`pillStroke`、`pillForeground`，圆角为全胶囊，内边距约水平 `11.dp`、垂直 `7.dp`。
7: motion 图片统一使用 `categoryMotionImageRes(imageAsset)`，不得在页面中另起一套 tag 图案或颜色映射。

### 动效与交互

1: 卡片切换动效优先使用 transform 属性：`translationX`、`translationY`、`scaleX`、`scaleY`、`rotationZ`、`alpha`。
2: 不要通过频繁改变 width、height、padding 等布局属性实现核心动画，避免卡顿和布局跳动。
3: 首页卡片切换时长约 `360ms`，使用 `FastOutSlowInEasing`。
4: 首页卡片静置时需要非常轻微的漂浮和旋转，前景卡浮动约 `5px`、旋转约 `0.8°`，后景卡更弱。
5: 横向滑动阈值约 `42f`，拖动距离限制约 `-140f..140f`。
6: 卡片点击进入编辑页时，应使用与卡片相关的过渡动画；加号进入编辑页应单独设计另一套动画，不要混用。

### 顶部栏

1: 顶部头像和操作按钮保持圆形白底、轻阴影、单图标，不加文字说明。
2: 顶部右侧按钮间距约 `14.dp`，图标尺寸约 `23-24.dp`。
3: 顶部按钮 icon 颜色使用接近黑色的 `#151515`，不要使用主题主色抢视觉。

### 底部导航

1: 底部导航由左侧胶囊 dock 和右侧独立圆形加号组成。
2: 左侧 dock 宽约 `190.dp`、高约 `64.dp`，内部每个 nav item 可独立点击，触控区域约 `52.dp`。
3: 选中态使用浅色圆角矩形底，不选中态透明；图标颜色使用 `bottomDockContentColor` / `bottomDockMutedColor`。
4: 加号按钮是黑/白反差圆形按钮，浅色主题黑底白 icon，深色主题白底黑 icon。
5: dock 和加号都要加 `debugOutline()`，确保开启 `preference_debug_outline` 时可检查边界。

### 字体与文字

1: 首页推荐说明使用居中、低对比灰色，约 `15.sp / 21.sp`，最多 3 行。
2: 主操作按钮使用胶囊按钮，主色 `#2F6CE5`，文字白色，图标与文字间距约 `9.dp`。
3: 首页不要堆叠解释性文字；能用图标和卡片表达的，不额外加说明。
4: 所有正式文案必须放到 strings 资源文件中，Preview 或临时 mock 数据除外。

### 文件分级与复用

1: 首页结构按职责拆分：`HomeScreen.kt` 管页面编排，`HomeHeader.kt` 管顶部栏，`HomeBottomBar.kt` 管底部导航，`HomeContent.kt` 管卡片和内容。
2: 底部 nav 切换首页内部区域时，必须把目标内容独立成单独 content 文件，例如记录区域使用 `RecordContent.kt`；`HomeScreen.kt` 只负责状态切换和替换 content，不要把新区域的完整 UI 直接写进 `HomeContent.kt`。
3: 全局外观放在 `ui/screens/app/AppAppearance.kt`，推荐卡片颜色放在 `RecommendationVisuals.kt`。
4: 后续页面如果需要推荐卡片、palette、motion 图片，必须复用 `recommendationPalette()` 和 `categoryMotionImageRes()`。
5: 有 UI 的 composable 必须补 `@Preview`，并注入 `LocalAppAppearance` / `LocalDebugUiOutline` 的合理预览值。
6: 新 UI 都要支持 `debugOutline()`，尤其是卡片、导航、按钮、输入面板等可视区域。
