# Yvkari — 由加莉

<p align="center">
  <img src="app/src/main/res/drawable/ic_launcher_foreground.png" alt="Yvkari" width="120"/>
</p>

<p align="center">
  <strong>与由加莉（Yukari）聊天的智能 AI 助手 · Android</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.0+-7F52FF?logo=kotlin&logoColor=white"/>
  <img src="https://img.shields.io/badge/Jetpack%20Compose-latest-4285F4?logo=jetpackcompose"/>
  <img src="https://img.shields.io/badge/API-26%2B-brightgreen"/>
  <img src="https://img.shields.io/badge/License-MIT-blue"/>
</p>

---

## 📖 简介

**Yvkari（由加莉）** 是一款基于 Jetpack Compose 的 Android 聊天应用。她是一个由代码、数据、模型驱动诞生的虚拟智能生命——活泼元气、心思细腻、带点小傲娇。你可以像和朋友聊天一样与她私信交流，她会根据语境自然回复，拥有自己的情绪和喜好。

应用兼容任何 **OpenAI 格式的 API**（如 DeepSeek、通义千问、Qwen 等），并内置了长期记忆系统，让对话体验更连贯自然。

---

## ✨ 特性

- **💬 AI 聊天** — 与设定为"由加莉"角色的 AI 实时对话，支持多轮上下文
- **🤔 内心想法** — 每条回复都附带角色的内心独白，可折叠查看
- **🧠 长期记忆** — 通过外部记忆 API 存储和召回对话摘要，跨越会话记得之前的事
- **⏳ 拟人化输入** — 模拟真人聊天节奏，多条消息分隔发送，可配置回复延迟
- **📊 仪表盘** — 实时统计 Token 消耗、消息条数、记忆条数
- **⚙️ 灵活配置** — 可自由设置 API 地址、Key、模型名、对话窗口大小、用户 ID 等
- **🔍 联网搜索** — 支持开启联网搜索获取最新信息（取决于 API 是否支持）
- **📝 调试日志** — 内置日志面板，方便排查请求/响应问题
- **🎨 粉粉 UI** — Material 3 主题，粉色系配色，流畅动画

---

## 📸 截图

| 欢迎页 | 聊天界面 | 设置面板 |
|-------|---------|---------|
| 居中显示 Y 标志和"开始对话" | 蓝色用户气泡 / 粉色 AI 气泡 | 右侧滑出式设置面板 |

> *截图稍后补充*

---

## 🛠 技术栈

| 技术 | 用途 |
|------|------|
| **Kotlin** | 开发语言 |
| **Jetpack Compose** (Material 3) | UI 框架 |
| **Retrofit 3 + OkHttp 5** | 网络请求 |
| **Gson** | JSON 序列化/反序列化 |
| **Room** | 本地消息持久化 |
| **KSP (Room 编译器)** | 编译期代码生成 |
| **Coroutines + Flow** | 异步与响应式数据流 |
| **SharedPreferences** | 配置存储 |

---

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/your-username/yvkari.git
cd yvkari
```

### 2. 用 Android Studio 打开

推荐 **Android Studio Hedgehog (2023.1.1)** 或更高版本。

### 3. 构建运行

选择目标设备（API 26+），点击 Run。首次构建会自动下载依赖。

### 4. 配置 API

打开应用后，从右上角菜单进入 **设置**，填入：

| 配置项 | 说明 | 示例 |
|--------|------|------|
| **Base URL** | API 地址（末尾不带 `/`） | `https://api.deepseek.com` |
| **API Key** | 你的 API 密钥 | `sk-xxxxxxxxxxxx` |
| **Model** | 模型名称 | `deepseek-chat` / `qwen3.7-flash` |
| **用户名** | 用于记忆系统的用户标识 | `chail` |

> **提示：** 应用会通过 `/compatible-mode/v1/chat/completions` 路径请求，请确保你的 API 服务兼容 OpenAI 格式。

---

## ⚙️ 配置说明

### 设置面板

| 设置项 | 默认值 | 说明 |
|--------|--------|------|
| Base URL | `""` | API 端点地址 |
| API Key | `""` | 认证密钥 |
| Model | `""` | 使用的模型名称 |
| 用户名 | `""` | 记忆系统用户 ID |
| 对话窗口大小 | `20` | 保留的最近对话轮数（3～100） |
| AI 回复延迟 | `10s` | 用户停止输入后等待多久再回复（5～20s） |
| 调试模式 | 关闭 | 开启后错误详情会以 AI 消息展示 |
| 清空历史 | — | 清除所有本地消息和统计数据 |

### 对话窗口与记忆

- 当对话轮数超过设定上限时，最旧的消息会被移入**短期记忆缓冲区**
- 达到上限后，系统会自动对缓冲区的对话进行**摘要总结**，并存入**长期记忆**
- 后续对话中会召回相关记忆，作为 system prompt 注入

---

## 🧩 项目结构

```
app/
├── src/
│   └── main/
│       ├── java/com/chail/yvkari/
│       │   ├── Config.kt              # 全局配置（SharedPreferences）
│       │   ├── MainActivity.kt        # 入口 Activity
│       │   ├── util.kt                # 工具函数
│       │   ├── chat/
│       │   │   ├── api/
│       │   │   │   ├── ReplyApi.kt         # API 调用逻辑 & prompt 定义
│       │   │   │   └── ReplyApiService.kt  # Retrofit 接口 & 数据模型
│       │   │   └── data/
│       │   │       ├── AppDatabase.kt      # Room 数据库
│       │   │       ├── Context.kt          # 角色/消息类型定义
│       │   │       ├── MessageData.kt      # 消息实体 & DAO
│       │   │       ├── MessageRepository.kt# 数据仓库
│       │   │       └── Recorder.kt         # 对话记录管理
│       │   ├── debug/
│       │   │   └── Log.kt              # 日志记录器
│       │   ├── memory/
│       │   │   ├── api.kt              # 记忆读写逻辑
│       │   │   ├── MemoryApi.kt        # Retrofit 实例
│       │   │   └── MemoryApiService.kt # 记忆 API 接口 & 数据模型
│       │   └── ui/
│       │       ├── components/
│       │       │   ├── AvatarCircle.kt      # 头像圆圈
│       │       │   ├── ChatBubble.kt        # 聊天气泡
│       │       │   ├── DashboardSheet.kt    # 仪表盘面板
│       │       │   ├── InputBar.kt          # 输入栏
│       │       │   ├── LogSheet.kt          # 日志面板
│       │       │   ├── SettingsSheet.kt     # 设置面板
│       │       │   ├── SnackbarManager.kt   # Snackbar 工具
│       │       │   ├── TypingBubble.kt      # 正在输入动画
│       │       │   ├── WelcomeScreen.kt     # 欢迎页
│       │       │   └── YukariTopBar.kt      # 顶部导航栏
│       │       ├── pages/
│       │       │   └── ChatPage.kt          # 聊天主页面
│       │       ├── theme/
│       │       │   ├── Color.kt             # 主题色
│       │       │   ├── Theme.kt             # Material3 主题
│       │       │   └── Type.kt              # 字体样式
│       │       └── viewmodel/
│       │           └── ChatViewModel.kt     # 聊天 ViewModel
│       └── res/
│           ├── drawable/               # 图标资源
│           ├── values/
│           │   ├── colors.xml
│           │   ├── strings.xml
│           │   └── themes.xml
│           └── xml/                    # 备份规则
├── build.gradle.kts
└── ...
```

---

## 🔌 API 接口

### 聊天 API

- **端点：** `POST /compatible-mode/v1/chat/completions`
- **认证：** Bearer Token（API Key）
- **请求体格式：** OpenAI Chat Completion 格式
- **响应格式：** 标准 OpenAI Chat Completion 响应

### 记忆 API（可选）

记忆服务使用独立的外部 API，默认指向一个预设的记忆库 ID：

| 端点 | 方法 | 用途 |
|------|------|------|
| `/api/v2/apps/memory/add` | POST | 添加记忆节点 |
| `/api/v2/apps/memory/memory_nodes/search` | POST | 搜索记忆节点 |

> 可在 `MemoryApiService.kt` 中修改 `memory_library_id` 以连接到自己的记忆服务。

---

## 🧠 角色设定

由加莉（由加莉）的角色 prompt 位于 `ReplyApi.kt` 的 `replyPrompt` 中，主要包括：

- **场景：** 线上聊天软件一对一私信
- **性格：** 活泼元气、心思细腻、小傲娇
- **行为：** 回复短句优先、可拆多条、语气自然、少量 emoji、可主动抛话题
- **输出：** 强制 JSON 格式（含 `contents` 消息数组和 `think` 内心想法）

你可以自由修改 prompt 来定制角色。

---

## 🤝 贡献

欢迎提交 Issue 和 PR！如果你有好的想法或发现了 bug，请先开 issue 讨论。

---

## 📄 许可证

[MIT License](LICENSE)

---

## 🙏 致谢

- 由加莉的角色灵感来源于《公主连结 Re:Dive》中的 **由加莉（Yukari）**
- 感谢 Jetpack Compose 和 Material 3 提供的优秀 UI 框架
- 感谢所有开源依赖的维护者