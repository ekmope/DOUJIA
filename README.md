# 豆荚

`豆荚` 是一个基于 LSPosed 的模块，仅供学习和研究

## 项目定位

- 首页入口精简
- 首页控件透明度调整
- 视频流内容过滤

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

构建需要自行提供签名配置。


## 说明

- 当前仓库是固定配置版本，不依赖前端设置页。
  - 仅供学习和研究
  - 请自行承担使用风险
  - 不提供目标应用本体
