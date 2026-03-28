# 豆荚

`豆荚` 是一个基于 `libxposed/api 101` 的 LSPosed 模块，目标应用为 `com.ss.android.ugc.aweme`。

建议仓库名：
- `doujia-lsposed`

对外文案统一建议：
- 仓库名：`doujia-lsposed`
- README 标题：`豆荚`
- 模块显示名：`豆荚`
- 应用包名：`com.android.ads`

## 项目定位

这个模块聚焦三类能力：

- 首页入口精简
- 首页控件透明度调整
- 视频流内容过滤

## 当前功能

### 顶部精简
- 隐藏顶部频道项：
  - 同城
  - 商城
  - 团购
  - 热点
  - 经验
  - 精选
- 隐藏“返回推荐页”按钮

### 底部精简
- 隐藏“朋友”
- 隐藏“发布/拍摄”

### 首页透明度
- 首页右侧操作区透明度
- 首页左下文案透明度
- 首页右下音乐区透明度
- 状态栏透明
- 底部栏透明

### 视频过滤
- 关键词过滤：`广告`、`推广`
- 低赞过滤：点赞数低于 `100`
- 直播流过滤

## 作用域

静态作用域：

```text
com.ss.android.ugc.aweme
```

对应文件：
- `app/src/main/resources/META-INF/xposed/scope.list`

## 应用信息

- 模块名称：`豆荚`
- 应用包名：`com.android.ads`
- 目标应用：`com.ss.android.ugc.aweme`
- Xposed 入口：`com.ss.android.ugc.awemes.ModuleMain`

## 构建环境

- Gradle: `9.4.0`
- Compile SDK: `36`
- Min SDK: `28`
- Java: `17`

## 构建

### Debug

```powershell
.\gradlew.bat assembleDebug
```

输出：

```text
app/build/outputs/apk/debug/app-debug.apk
```

### Release

```powershell
.\gradlew.bat assembleRelease
```

输出：

```text
app/build/outputs/apk/release/
```

说明：
- 仓库未内置签名文件。
- `release` 构建需要你自行提供签名配置。

## 项目结构

```text
GitHub/
├─ app/
│  ├─ src/main/java/com/android/admin/module/
│  │  ├─ DouyinMigratedHooks.java
│  │  ├─ ModulePrefs.java
│  │  ├─ UiProfile.java
│  │  ├─ ViewInstaller.java
│  │  ├─ KeepFireStore.java
│  │  └─ WardStore.java
│  ├─ src/main/java/com/ss/android/ugc/awemes/
│  │  └─ ModuleMain.java
│  └─ src/main/resources/META-INF/xposed/
│     ├─ java_init.list
│     ├─ module.prop
│     └─ scope.list
├─ gradle/wrapper/
├─ gradlew
├─ gradlew.bat
├─ LICENSE
└─ README.md
```

## 说明

- 当前仓库是固定配置版本，不依赖前端设置页。
- 如果要公开发布，建议在 release 文案里明确说明：
  - 仅供学习和研究
  - 请自行承担使用风险
  - 不提供目标应用本体
