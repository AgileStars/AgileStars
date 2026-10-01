# AgileStars

Minecraft **1.21.1** Fabric 模组，基于官方模板
[`FabricMC/fabric-example-mod`](https://github.com/FabricMC/fabric-example-mod) 的 `1.21.1` 分支。

## 环境要求

| 组件 | 版本 |
| --- | --- |
| Minecraft | 1.21.1 |
| Fabric Loader | 0.19.5 |
| Fabric API | 0.116.17+1.21.1 |
| Fabric Loom | 1.18-SNAPSHOT（`net.fabricmc.fabric-loom-remap`） |
| Gradle | 9.7.1（wrapper 自带，无需本机安装） |
| JDK（跑 Gradle） | **25** |
| JDK（编译目标） | 21（`build.gradle` 里 `options.release = 21`） |

> ⚠️ Fabric Loom 1.18 要求用 **Java 25** 启动 Gradle，用 Java 21 会在配置阶段直接报
> `requires at least a runtime version 25`。模组本身仍编译为 Java 21（Minecraft 1.21.1 的要求），两者不冲突。

版本号都能在 <https://fabricmc.net/develop/> 查到，改了以后同步更新 `gradle.properties`。

## 构建

```bash
# Linux / macOS / Git Bash
./gradlew build

# Windows PowerShell / cmd
.\gradlew.bat build
```

产物：`build/libs/agilestars-<version>.jar`（**不要**把 `-sources.jar` 丢进 `mods/`）。

## 常用 Gradle 任务

| 任务 | 作用 |
| --- | --- |
| `build` | 编译 + 打包 |
| `runClient` | 启动带模组的客户端 |
| `runServer` | 启动带模组的服务端 |

## 项目结构

```
src/
  main/      # 通用逻辑 + 服务端
    java/com/agilestars/AgileStars.java              # ModInitializer
    java/com/agilestars/mixin/AgileStarsMixin.java   # 通用 mixin
    resources/fabric.mod.json                        # 模组元数据
    resources/agilestars.mixins.json                 # 通用 mixin 配置
  client/    # 仅客户端（Loom splitEnvironmentSourceSets）
    java/com/agilestars/client/AgileStarsClient.java # ClientModInitializer
    java/com/agilestars/client/mixin/AgileStarsClientMixin.java
    resources/agilestars.client.mixins.json
```

- mod id：`agilestars`
- group / 包名：`com.agilestars`
- 主类：`com.agilestars.AgileStars`

## CI / 发布（GitHub Actions）

不需要本地环境，全部在 GitHub 上构建：

- [`.github/workflows/ci.yml`](.github/workflows/ci.yml)：push / PR 跑 `./gradlew build`，jar 上传为 Actions artifact `mod-jar`。
- [`.github/workflows/release.yml`](.github/workflows/release.yml)：推 `v*` 标签时构建并把 jar 附到 GitHub Release。

发布一个版本：

```bash
git tag v1.0.0
git push origin v1.0.0
```

## 开发提示

- 注册内容在 `onInitialize()` 里用 `Registry.register(...)`；`AgileStars.id("path")` 构造 `ResourceLocation`。
- 客户端专属代码放 `src/client`，放 `src/main` 会让专用服务端崩溃。
- 新增 mixin 要同时登记到对应的 `*.mixins.json`。
- `src/main/resources/assets/agilestars/icon.png` 目前是官方示例图标（CC0），建议换成自己的。

## 授权

`gradle.properties` / `fabric.mod.json` 当前为 `All-Rights-Reserved`，发布前按需修改。
