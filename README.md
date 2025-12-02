[English](README_EN.md) | **简体中文**

# ConnectedGlass

Makes glass panes render with a connected texture in Minecraft 1.9 snapshots.

Requires the Meddle loader, with the DynamicMappings and MeddleAPI mods installed.

---

# ConnectedGlassX

这是一个来自 [FyberOptic/ConnectedGlass](https://github.com/FyberOptic/ConnectedGlass) 的分支。

我最近在玩一个愚人节版本 1.RV-pre1 ，因为我找不到能用的 Forge/Optifine 支持，所以只能使用 Meddle 。在 Meddle 中我找了一个接近于连接纹理的 mod ，也就是上文提供的由 FyberOptic 写的 ConnectedGlass ，原本他在 1.RV-pre1 中仅剩下玻璃板还支持连接纹理，我进行了一些修改，现在支持了更多类型的方块。

---

## 目前支持的方块

- [x] 玻璃 及彩色玻璃

- [x] 玻璃板 及彩色玻璃板

- [x] 石台阶 及双石台阶


## 如何编译?

1. 部署整个代码库，加载 Gradle ，然后在 /src 同级目录创建一个叫做 libs 的文件夹，往里面放入 DynamicMappings-023.jar ; meddle-1.3.jar ; meddleapi-1.0.6.jar .
  
2. 在 libs 目录打开任何 Shell ，下面以 BATCH (cmd) 环境下反混淆 Minecraft 1.RV-pre1 为例.
  
  ```BATCH
  java -cp minecraft-1.RV-pre1-client.jar;dynamicmappings-023.jar;asm-all-5.0.3.jar;log4j-api-2.8.1.jar;log4j-core-2.8.1.jar;meddle-1.3.jar;guava-17.0.jar;gson-2.2.4.jar;netty-all-4.0.23.Final.jar;authlib-1.5.21.jar;lwjgl_util-2.9.4-nightly-20150209.jar;lwjgl-2.9.4-nightly-20150209.jar;soundsystem-20120107.jar net.fybertech.dynamicmappings.DynamicRemap
  ```
  

3. 执行后将会在同级目录生成一个 mcremapped.jar ，此便是所需的反混淆的 Minecraft 库。
4. 执行 Gradle 的 Tasks>build>jar 任务，即可成功编译。
