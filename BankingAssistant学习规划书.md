# Banking Assistant 多Agent项目 个性化学习规划书

> **学习定位**：NanoCode之后的进阶项目 · 简历亮点项目  
> **目标岗位**：Java后端实习（Agent能力为竞争力加分项）  
> **学员画像**：Java后端熟手（微服务/并发/JVM） · Agent入门级（已完成NanoCode） · LLM零基础  
> **学习周期**：3天集中（每天4-6小时，按NanoCode完成后再启动）

---

## 目录

1. [项目概览与技术栈](#1-项目概览与技术栈)
2. [与NanoCode的对比定位](#2-与nanocode的对比定位)
3. [学习路线总览](#3-学习路线总览)
4. [阶段一：项目启动 + 架构理解（Day1 上午，~3h）](#4-阶段一项目启动--架构理解)
5. [阶段二：Agent体系深度研读（Day1 下午，~4h）](#5-阶段二agent体系深度研读)
6. [阶段三：LangGraph4j 工作流引擎（Day2 上午，~3h）](#6-阶段三langgraph4j-工作流引擎)
7. [阶段四：MCP协议与工具集成（Day2 下午，~4h）](#7-阶段四mcp协议与工具集成)
8. [阶段五：总结与面试准备（Day3 全天，~6h）](#8-阶段五总结与面试准备)
9. [附录：技术术语速查表](#附录技术术语速查表)

---

## 1. 项目概览与技术栈

### 这是什么？

**微软Azure官方开源示例** — 一个银行个人助理Copilot，用户用自然语言查询账户、交易记录、支付账单（支持上传发票图片自动识别）。

```
你："What's my account balance?"
  → Supervisor判断意图 → 路由到AccountAgent → 查账户API → 回答

你："Pay this invoice"  [上传发票照片]
  → Supervisor → PaymentAgent → OCR扫描发票 → 确认收款方 → 提交支付
```

### 核心架构一句话

```
React前端 → Spring Boot Copilot → [LangGraph4j工作流引擎]
                                      ├── SupervisorAgent（路由调度）
                                      ├── AccountAgent（账户查询）
                                      ├── TransactionHistoryAgent（交易记录）
                                      └── PaymentAgent（发票支付+OCR）

每个Agent通过 MCP协议 连接后端微服务：
  Account Service / Payment Service / Transaction Service
```

### 技术栈一览

| 层次      | 技术                                    | 你熟悉度                  |
| ------- | ------------------------------------- | --------------------- |
| 语言      | **Java 17** (非25)                     | ★★★★★                 |
| 构建      | **Maven 多模块**                         | ★★★★★                 |
| 框架      | **Spring Boot**                       | ★★★★★                 |
| Agent框架 | **LangChain4j** + **LangGraph4j**     | ★★☆☆☆ (刚学完NanoCode)   |
| LLM     | **Azure OpenAI (gpt-4o-mini)**        | ★☆☆☆☆ (DeepSeek经验可迁移) |
| 协议      | **MCP (Model Context Protocol)**      | ☆☆☆☆☆ (全新)            |
| 前端      | **React + Fluent UI**                 | 无需关注                  |
| 云       | **Azure Container Apps** (可跳过)        | 无需关注                  |
| 文档智能    | **Azure Document Intelligence** (OCR) | 无需深入                  |

### 项目文件结构（核心模块）

```
agent-openai-java-banking-assistant-langgraph4j/
├── app/
│   ├── copilot/
│   │   ├── copilot-backend/          ← Spring Boot主应用 (Controller层)
│   │   ├── copilot-common/           ← 公共工具 (OCR, Blob)
│   │   ├── langchain4j-agents/       ← Agent定义 + LangChain4j集成 ★★★
│   │   ├── langchain4j-openapi/      ← OpenAPI→Tool 自动转换工具 ★★
│   │   └── langgraph4j-agents/       ← LangGraph4j 工作流定义 ★★★
│   └── business-api/                 ← 3个业务微服务 (MCP暴露)
│       ├── account/                  ← 账户查询服务
│       ├── payment/                  ← 支付处理服务
│       └── transactions-history/     ← 交易记录服务
└── docs/
    └── multi-agents/introduction.md  ← Agent架构概念文档
```

---

## 2. 与NanoCode的对比定位

> **NanoCode 教你"什么是Agent"，Banking Assistant 教你"Agent怎么工程化落地"**

| 维度          | NanoCode                              | Banking Assistant                |
| ----------- | ------------------------------------- | -------------------------------- |
| **定位**      | 练手入门                                  | 生产级参考实现                          |
| **Agent数量** | 1个 (basic) / 3个 (agentic)             | 4个 (1 Supervisor + 3 业务)         |
| **架构模式**    | Supervisor Pattern                    | Supervisor Pattern + **工作流引擎**   |
| **Agent编排** | `AgenticServices.supervisorBuilder()` | **LangGraph4j StateGraph** (有向图) |
| **运行时**     | JBang单文件脚本                            | **Maven多模块 + Spring Boot**       |
| **工具来源**    | 手写 @Tool Java方法                       | **MCP协议**自动发现远端工具                |
| **模型**      | 单一 (DeepSeek/Gemini)                  | Azure OpenAI                     |
| **记忆管理**    | MessageWindowChatMemory               | 同 + LangGraph Checkpoint         |
| **前端**      | 终端CLI                                 | React Web UI                     |
| **部署**      | 本地JBang运行                             | Docker Compose / Azure           |

### 你应该学什么、跳过什么

| 学                                        | 跳                             |
| ---------------------------------------- | ----------------------------- |
| ✅ LangGraph4j StateGraph 工作流             | ❌ Azure部署 (azd/ACA)           |
| ✅ MCP协议 - Agent如何消费远端API                 | ❌ React前端                     |
| ✅ 多Agent架构的设计思想                          | ❌ Azure认证配置                   |
| ✅ LangChain4j + Spring Boot 整合           | ❌ Document Intelligence OCR细节 |
| ✅ Supervisor路由策略 (Prompt-driven routing) | ❌ GitHub Actions CI/CD        |

---

## 3. 学习路线总览

```
Day1 上午 (3h)   项目启动 + 架构全景理解
Day1 下午 (4h)   Agent体系深度研读 (Agent接口→MCP→Supervisor)
Day2 上午 (3h)   LangGraph4j 工作流引擎
Day2 下午 (4h)   MCP协议 + 工具集成 + 业务API
Day3 全天 (6h)   总结 + 面试准备 + 架构图绘制
```

---

## 4. 阶段一：项目启动 + 架构理解

> **目标**：理解项目整体架构，能在本地用Docker Compose跑起来

### 4.1 任务清单

| #   | 任务               | 时长    | 具体操作                                          |
| --- | ---------------- | ----- | --------------------------------------------- |
| 1.1 | 阅读README + 架构文档  | 30min | README.md + docs/multi-agents/introduction.md |
| 1.2 | 理解项目模块划分         | 30min | 打开pom.xml，理解Maven多模块结构                        |
| 1.3 | 理解HLA架构图         | 20min | docs/assets/HLA-MCP.png — Agent ↔ MCP ↔ 微服务   |
| 1.4 | Docker Compose启动 | 60min | 阅读 app/start-compose.ps1，理解各容器角色              |
| 1.5 | 浏览器体验完整流程        | 40min | 问余额、查交易、上传发票支付                                |

### 4.2 关键架构概念（先建立认知，不急着看代码）

#### 4.2.1 这个项目的Agent不是"一个类"，而是一个体系

NanoCode里 Agent = 一个接口 + @Tool方法。这个项目里 Agent = **三层抽象**：

```
com.microsoft.langchain4j.agent.Agent (接口)
    ├── getName()          — "AccountAgent"
    ├── getMetadata()      — "可以查余额、支付方式、收款人"
    └── invoke(chatHistory) → List<ChatMessage>
         ↑
    ┌────┴──────────────────────┐
    │                            │
MCPToolAgent              OpenAPIToolAgent
(通过MCP协议调远端工具)    (通过OpenAPI Schema调REST API)
    │
    ├── AccountMCPAgent
    ├── PaymentMCPAgent
    └── TransactionHistoryMCPAgent
```

> **类比**：Agent接口 = Spring的`Controller`接口。MCPToolAgent = 一个`@RestController`基类，自动发现远端工具。

#### 4.2.2 Supervisor不做任何业务，只做"路由"

看 `SupervisorRoutingAgent.java` 第31-37行的 System Prompt：

```java
"""
You are a banking customer support agent triaging conversation.
Use the below list of agents metadata to select the best one:
{{agentsMetadata}}
Answer only with the agent name.
if you are not able to select an agent answer with none.
"""
```

输出只有三个可能：`"AccountAgent"` / `"TransactionHistoryAgent"` / `"PaymentAgent"` / `"none"`。

> **对比NanoCode**：NanoCode的Supervisor也做路由，但是通过 `@Agent(description=...)` 注解隐式匹配。这里的做法更直接——**Prompt里明文列出所有Agent的metadata，让模型直接输出名字**。

#### 4.2.3 两个版本的Supervisor共存

项目里有两个Supervisor实现：

| 文件                                                   | 框架           | 路由方式                |
| ---------------------------------------------------- | ------------ | ------------------- |
| `langchain4j-agents/.../SupervisorRoutingAgent.java` | 纯LangChain4j | 自己invoke子Agent      |
| `langgraph4j-agents/.../SupervisorAgent.java`        | LangGraph4j  | 输出Agent名，由Graph引擎路由 |

> 后者是生产版本。前者可看作"简化版"参考实现。

### 4.3 检验题

1. 这个项目有几个Agent？各自负责什么业务领域？
2. Supervisor的System Prompt只输出什么？为什么这样设计？
3. Agent是如何获取业务数据的？（提示：不是@Tool方法）
4. 画出HLA架构图的关键组件连线。

---

## 5. 阶段二：Agent体系深度研读

> **目标**：理解Agent接口设计、MCPAgent如何自动发现工具、Supervisor路由逻辑

### 5.1 任务清单

| #   | 任务                             | 时长    | 核心文件                                          |
| --- | ------------------------------ | ----- | --------------------------------------------- |
| 2.1 | Agent接口 + AgentMetadata        | 20min | Agent.java, AgentMetadata.java                |
| 2.2 | AbstractReActAgent             | 30min | AbstractReActAgent.java — ReAct循环实现           |
| 2.3 | MCPToolAgent 深读                | 40min | MCPToolAgent.java — 如何通过MCP自动发现工具             |
| 2.4 | OpenAPIToolAgent 深读            | 30min | OpenAPIToolAgent.java — 如何从OpenAPI Schema生成工具 |
| 2.5 | AccountMCPAgent 实例             | 30min | AccountMCPAgent.java — 一个具体Agent的完整实现         |
| 2.6 | SupervisorRoutingAgent 深读      | 30min | SupervisorRoutingAgent.java — 纯LC4j版路由        |
| 2.7 | Langchain4JAgentsConfiguration | 20min | Spring配置如何组装所有Agent                           |

### 5.2 核心研读点

#### 5.2.1 Agent接口的设计哲学

```java
public interface Agent {
    String getName();           // 唯一标识，如 "AccountAgent"
    AgentMetadata getMetadata(); // 能力描述，给Supervisor看
    List<ChatMessage> invoke(List<ChatMessage> chatHistory); // 执行
}
```

> **关键设计**：`invoke()`的入参和出参都是 `List<ChatMessage>`。这意味着Agent是**无状态函数**——输入历史消息，输出新消息。状态由外部（LangGraph StateGraph）管理。

#### 5.2.2 MCPToolAgent — 工具自动发现机制

这是本项目最核心的创新点。对比NanoCode：

|      | NanoCode    | Banking Assistant |
| ---- | ----------- | ----------------- |
| 工具定义 | 手写`@Tool`方法 | **MCP协议自动发现**     |
| 工具位置 | 本地Java类     | **远端微服务**         |
| 修改工具 | 改代码重新编译     | 改远端服务即可           |

MCPToolAgent 的启动流程：

```
1. Agent启动 → 连接 MCP Server URL (如 http://localhost:8070/sse)
2. 调用 tools/list → 服务端返回可用工具列表 (JSON Schema)
3. 每个工具自动注册为 LangChain4j Tool
4. Agent调用工具时 → MCP协议转发到远端服务 → 返回结果
```

> **类比**：MCP之于Agent工具发现 = Eureka/Nacos之于微服务发现。Agent不需要硬编码知道有什么工具，启动时自动发现。

#### 5.2.3 三个业务Agent的职责

| Agent                      | 连哪个MCP Server                                          | 能做什么                 |
| -------------------------- | ------------------------------------------------------ | -------------------- |
| AccountMCPAgent            | account:8070                                           | 查余额、支付方式、收款人列表       |
| TransactionHistoryMCPAgent | transaction:8090 + account:8070                        | 查交易记录（需要先查accountId） |
| PaymentMCPAgent            | payment:8060 + account:8070 + transaction:8090 + OCR工具 | 扫描发票→确认→提交支付→查历史     |

> **注意**：PaymentAgent最复杂，它同时连3个MCP Server + 1个本地OCR工具(InvoiceScanTool)。

### 5.3 检验题

1. Agent接口为什么把invoke设计成 `List<ChatMessage> → List<ChatMessage>` 的纯函数？
2. MCP协议的 `tools/list` 调用是做什么的？它替代了NanoCode中的什么？
3. 如果要新增一个"贷款查询Agent"，需要改哪些代码？

---

## 6. 阶段三：LangGraph4j 工作流引擎

> **目标**：理解LangGraph4j如何编排多Agent协作，对比NanoCode的Agentic模块

### 6.1 任务清单

| #   | 任务                           | 时长    | 核心文件                                             |
| --- | ---------------------------- | ----- | ------------------------------------------------ |
| 3.1 | LangGraph4j概念建立              | 30min | 阅读LangGraph4j README + 官方文档概述                    |
| 3.2 | SupervisorAgent (LangGraph版) | 30min | langgraph4j-agents/.../SupervisorAgent.java      |
| 3.3 | SupervisorAgentNode          | 20min | langgraph4j-agents/.../SupervisorAgentNode.java  |
| 3.4 | AgentNode                    | 20min | langgraph4j-agents/.../AgentNode.java            |
| 3.5 | AgentWorkflowState           | 15min | langgraph4j-agents/.../AgentWorkflowState.java   |
| 3.6 | AgentWorkflowBuilder ★核心     | 45min | langgraph4j-agents/.../AgentWorkflowBuilder.java |
| 3.7 | AgentWorkflowITest           | 20min | 测试用例即文档，理解workflow如何被调用                          |

### 6.2 关键概念详解

#### 6.2.1 LangGraph4j vs NanoCode Agentic — 核心区别

|      | NanoCode Agentic                    | Banking Assistant (LangGraph4j)      |
| ---- | ----------------------------------- | ------------------------------------ |
| 编排方式 | Builder模式 `.subAgents(...).build()` | **StateGraph** (有向图)                 |
| 节点   | 隐式 (Supervisor + 子Agent)            | **显式** (addNode注册每个节点)               |
| 边    | 隐式 (Supervisor自动路由)                 | **显式** (addEdge/addConditionalEdges) |
| 状态   | AgenticScope (黑盒)                   | **AgentWorkflowState** (透明可序列化)      |
| 可观测性 | 有限                                  | **Checkpoint + 图可视化**                |

#### 6.2.2 StateGraph — Agent协作的"流程图"

`AgentWorkflowBuilder.java` 第80-95行定义了这个图：

```java
new StateGraph<>(...)
    // 注册4个节点
    .addNode("Supervisor", ...)
    .addNode("AccountAgent", ...)
    .addNode("TransactionHistoryAgent", ...)
    .addNode("PaymentAgent", ...)

    // 定义流转
    .addEdge(START, "Supervisor")           // 入口→Supervisor
    .addConditionalEdges("Supervisor", ...)  // Supervisor→根据输出路由到对应Agent
    .addEdge("AccountAgent", END)           // 任一Agent完成后→结束
    .addEdge("TransactionHistoryAgent", END)
    .addEdge("PaymentAgent", END)
```

对应的流程图：

```
  START
    │
    ▼
Supervisor ──(条件边)──→ AccountAgent ──→ END
                │
                ├────→ TransactionHistoryAgent ──→ END
                │
                └────→ PaymentAgent ──→ END
                │
                └────→ END (none/无法识别)
```

> **对比NanoCode**：NanoCode的Agentic模块内部也是类似的图结构，但黑盒封装了。LangGraph4j让你**显式定义每个节点和每条边**，图就是代码，代码就是图。

#### 6.2.3 AgentWorkflowState — 图的状态

```java
public class AgentWorkflowState extends MessagesState<ChatMessage> {
    public Optional<String> nextAgent() { return value("nextAgent"); }
}
```

- `messages`（继承自MessagesState）：累积所有对话消息
- `nextAgent`：Supervisor设置的"下一个该调哪个Agent"

流转机制：

```
1. START → Supervisor节点
2. Supervisor执行 → 设置 nextAgent = "PaymentAgent"
3. 条件边读取 nextAgent → 路由到 PaymentAgent节点
4. PaymentAgent执行 → 追加消息到 messages
5. 边指向 END → 工作流结束
```

#### 6.2.4 Checkpoint — 工作流的"断点续传"

```java
var checkPointSaver = new MemorySaver();
var config = CompileConfig.builder()
    .checkpointSaver(checkPointSaver)
    .build();
```

> 类比：Checkpoint = 数据库事务日志。每步执行后自动保存状态，失败可以重放。这也是**Human-in-the-loop**（人机协同）的基础——Agent可以在关键步骤暂停，等待人类确认后再继续。

### 6.3 检验题

1. StateGraph的 `addNode` / `addEdge` / `addConditionalEdges` 分别是什么含义？
2. Supervisor如何把"下一个Agent"信息传递给Graph引擎？
3. 如果我想在PaymentAgent执行前加一个人工确认步骤，应该在图中怎么改？
4. LangGraph4j相比NanoCode Agentic的优劣势各是什么？

---

## 7. 阶段四：MCP协议与工具集成

> **目标**：理解MCP协议，理解Agent如何动态发现和调用远端工具

### 7.1 任务清单

| #   | 任务                                        | 时长    | 核心文件                                              |
| --- | ----------------------------------------- | ----- | ------------------------------------------------- |
| 4.1 | MCP协议概念建立                                 | 20min | modelcontextprotocol.io 官网速览                      |
| 4.2 | MCPToolAgent 工具发现流程                       | 40min | MCPToolAgent.java                                 |
| 4.3 | AccountMCPService 实现                      | 30min | account/.../AccountMCPService.java                |
| 4.4 | PaymentMCPService + TransactionMCPService | 30min | 两个MCPService对比                                    |
| 4.5 | Spring AI MCP配置                           | 30min | 各MCPServerConfiguration.java                      |
| 4.6 | OpenAPIToolsImporter                      | 40min | langchain4j-openapi/.../OpenAPIToolsImporter.java |
| 4.7 | 工具发现流程全链路追踪                               | 30min | MCP Server → MCP Client → Tool → Agent            |

### 7.2 关键知识点

#### 7.2.1 MCP协议是什么？

> **一句话**：MCP = Agent工具调用的"USB-C接口标准"。就像USB-C统一了充电/数据传输，MCP统一了Agent调用外部工具的方式。

```
传统方式 (NanoCode):
  Agent → @Tool Java方法 → 本地执行

MCP方式 (Banking Assistant):
  Agent → MCP Client → (HTTP/SSE) → MCP Server → 远端微服务
```

MCP有两个核心操作：

- `tools/list`：Agent问Server"你有什么能力？"，Server返回工具列表JSON
- `tools/call`：Agent说"调用这个工具"，Server执行并返回结果

#### 7.2.2 一个MCP Service长什么样？

`AccountMCPService.java` 中：

```java
@Tool(description = "获取用户账户信息")
public Account getAccountByUsername(String username) { ... }

@Tool(description = "获取用户的支付方式列表")
public List<PaymentMethod> getPaymentMethods(String accountId) { ... }

@Tool(description = "获取用户的收款人列表")
public List<Beneficiary> getBeneficiaries(String accountId) { ... }
```

这些 `@Tool` 注解会被 **Spring AI MCP** 自动暴露为MCP工具，然后Agent端通过 `tools/list` 自动发现。

> **注意到**：Agent端的MCPToolAgent.java里**没有任何这3个方法**——它们是通过MCP协议在运行时动态发现的！

#### 7.2.3 Agent工具来源全景

```
PaymentAgent需要的工具：
├── 来自 Account MCP Server (远端)
│   ├── getAccountByUsername()
│   ├── getPaymentMethods()
│   └── getBeneficiaries()
├── 来自 Payment MCP Server (远端)
│   └── submitPayment()
├── 来自 Transaction MCP Server (远端)
│   └── searchTransactions()
└── 来自本地 (InvoiceScanTool.java)
    └── scanInvoice(图片URL) → 发票结构化数据
```

> **设计价值**：新增一个微服务，Agent自动获得其工具，无需改Agent代码。

### 7.3 检验题

1. MCP的 `tools/list` 和 `tools/call` 分别做什么？
2. Agent端怎么知道远端有哪些工具可用？（提示：不是在代码里写死的）
3. 如果给你一个新微服务"贷款服务"，需要几步把它集成到Agent体系中？

---

## 8. 阶段五：总结与面试准备

> **目标**：能用自己的话讲清楚整个项目架构、技术选型、你的学习收获

### 8.1 任务清单

| #   | 任务                                  | 时长    |
| --- | ----------------------------------- | ----- |
| 5.1 | 绘制项目架构图                             | 60min |
| 5.2 | 绘制Agent请求流转图                        | 45min |
| 5.3 | 整理面试问答                              | 90min |
| 5.4 | 总结核心技术收获                            | 45min |
| 5.5 | 对比NanoCode → Banking Assistant的成长路径 | 30min |

### 8.2 架构图绘制（必做）

用你熟悉的工具（Draw.io / Excalidraw / 手绘拍照）画出两张图：

**图1：系统架构图**

```
[React前端] → [Spring Boot Copilot] → [LangGraph4j StateGraph]
                                           ├── Supervisor
                                           ├── AccountAgent ──MCP── [Account Service]
                                           ├── TransactionAgent ──MCP── [Transaction Service]
                                           └── PaymentAgent ──MCP── [Payment Service]
                                                                   └── [Document Intelligence OCR]
```

**图2：一次请求的完整流转**

```
用户: "Pay this electricity bill" + [上传发票]
  → ChatController
  → LangGraph4j StateGraph
  → Supervisor: "需要支付 → PaymentAgent"
  → PaymentAgent:
      1. MCP→Account Service: 获取账户信息
      2. OCR scanInvoice: 扫描发票提取金额/收款方
      3. MCP→Transaction Service: 检查是否已支付
      4. 与用户确认: "Pay $85.30 to Electric Co?"
      5. 用户确认 → MCP→Payment Service: 提交支付
  → 返回: "Payment of $85.30 to Electric Co submitted!"
```

### 8.3 面试问答（重点准备）

#### Q1: 介绍一下你做的这个银行助理Agent项目

> **答题要点**：
> 
> - 这是微软Azure官方的多Agent示例项目，我拿来深入学习并做了适配
> - 核心架构：LangGraph4j StateGraph编排4个Agent（1 Supervisor + 3 业务）
> - 创新点：Agent通过MCP协议动态发现远端微服务的工具能力，而非硬编码
> - 技术栈：Spring Boot + LangChain4j + LangGraph4j + MCP + Azure OpenAI
> - 我做了：DeepSeek适配、架构研究、面试总结

#### Q2: 为什么用LangGraph4j而不是直接写if-else路由？

> **答题要点**：
> 
> - if-else只能处理静态规则，Supervisor用LLM做意图理解更灵活
> - StateGraph提供：状态管理、Checkpoint（断点续传）、图可视化
> - 支持Human-in-the-loop：支付等敏感操作可在图中插入人工确认节点
> - 可扩展：新增Agent只需addNode，不改路由逻辑

#### Q3: MCP协议解决了什么问题？

> **答题要点**：
> 
> - 传统方式：Agent代码里硬编码工具方法 → 改工具要改Agent代码
> - MCP方式：Agent通过标准协议发现远端工具 → 新增微服务自动可见
> - 类比：就像USB-C统一了设备连接标准，MCP统一了Agent工具调用标准
> - 实际价值：业务团队新增API，Agent团队无需改动

#### Q4: Agent是怎么知道该调用哪个工具、以及工具的参数是什么的？

> **答题要点**：
> 
> - MCP的 `tools/list` 返回每个工具的 JSON Schema（名称、描述、参数类型）
> - LangChain4j把这些Schema转成Function Calling格式
> - 每次请求时，LLM收到"可用工具列表" + "用户问题" → 自主判断调用哪个
> - @Tool注解中的描述文字至关重要——它是LLM选择工具的唯一依据

#### Q5: 从NanoCode到这个项目，你对Agent的理解发生了什么变化？

> **答题要点**：
> 
> - NanoCode：Agent = 1个接口 + 几个@Tool方法，本质是"带工具的聊天机器人"
> - Banking Assistant：Agent = 工程体系。涉及工作流编排、工具自动发现、状态管理、人机协同
> - 从"怎么写一个Agent" → "怎么设计一个Agent系统"
> - 技术视野：LangChain4j AiServices → LangGraph4j StateGraph → MCP协议

### 8.4 学习收获总结模板

```
【Agent技术栈收获清单】

1. LangChain4j:
   - AiServices、@Tool、@SystemMessage、ChatMemory 的实际应用
   - Agent接口设计模式：getName/getMetadata/invoke
   - 与Spring Boot的整合方案

2. LangGraph4j:
   - StateGraph: 显式定义多Agent协作流程
   - Checkpoint: 工作流状态持久化与断点续传
   - ConditionalEdges: 基于LLM输出的动态路由

3. MCP协议:
   - tools/list + tools/call 标准交互
   - Agent与微服务的解耦方案
   - Spring AI MCP的服务端/客户端实现

4. 架构设计:
   - Supervisor Pattern 的工程化落地
   - Agent工具来源的多样性 (MCP远端 + OpenAPI Schema + 本地Tool)
   - 多Agent系统的可观测性设计
```

---

## 附录：技术术语速查表

| 术语                   | 一句话解释                     | 在这个项目哪里                                                |
| -------------------- | ------------------------- | ------------------------------------------------------ |
| **Agent**            | LLM + 工具 + System Prompt  | `com.microsoft.langchain4j.agent.Agent` 接口             |
| **Supervisor**       | 不干活只路由的Agent              | `SupervisorAgent.java` / `SupervisorRoutingAgent.java` |
| **StateGraph**       | 有向图，节点=Agent，边=流转规则       | `AgentWorkflowBuilder.graph()`                         |
| **MCP**              | Agent工具调用的标准协议            | 所有 `MCPToolAgent` / `MCPService`                       |
| **ReAct**            | Think→Act→Observe循环       | `AbstractReActAgent.java`                              |
| **Checkpoint**       | 工作流状态快照，支持断点续传            | `MemorySaver` in `AgentWorkflowBuilder`                |
| **ConditionalEdge**  | 根据运行时状态决定下一节点             | `supervisorRoute` in `AgentWorkflowBuilder`            |
| **OpenAPI Importer** | 从OpenAPI Schema自动生成工具     | `OpenAPIToolsImporter.java`                            |
| **LangGraph4j**      | Java版的LangGraph（状态图编排引擎）  | `langgraph4j-agents` 模块                                |
| **LangChain4j**      | Java版LangChain（Agent开发框架） | `langchain4j-agents` 模块                                |

---

## 学习建议

1. **不要试图看懂每一个文件**。这个项目是微软团队写的生产级代码，有大量Azure云服务、认证、监控的胶水代码，跳过它们。
2. **聚焦核心链路**：Agent接口 → MCPToolAgent → Supervisor → StateGraph → ConditionEdge。这条链路吃透。
3. **多画图**。这个项目的本质是"多Agent协作的图结构"。代码是图的实现，图是代码的灵魂。画清楚图比读懂每一行代码更重要。
4. **对比NanoCode**。每个新概念出现时，回想NanoCode里对应的是什么。对比是最好的学习方式。
5. **面向面试学习**。阶段五的面试问答是你最终要能说出来的东西。其他代码细节是为这些答案服务的。

---

> 📅 **创建日期**：2026-06-06  
> 📌 **前置条件**：已完成 NanoCode 项目学习  
> ⏭️ **后续**：完成后可开始自定义多Agent集群项目（简历亮点项目）

## 结构图

copilot-backend/
├── CopilotApplication.java              ← Spring Boot 启动类
│
├── controller/                          ← 前端入口（HTTP API）
│   ├── ChatController.java       ✅     ← 纯 LangChain4j 版
│   ├── ChatLanggraph4JController.java ✅ ← LangGraph4j 版（生产用）
│   ├── ChatAppRequest.java      ❌     ← DTO，不用管
│   ├── ChatResponse.java        ❌     ← DTO
│   └── content/ContentController.java ❌ ← 文件上传
│
├── config/                              ← Spring @Configuration 装配
│   ├── Langchain4JAgentsConfiguration.java ✅ ← 组装纯 LC4j 版 Agent
│   ├── Langgraph4JAgentsConfiguration.java ✅ ← 组装 LangGraph4j 版（生产用）
│   ├── Langchain4JConfiguration.java   ✅     ← 创建 ChatLanguageModel Bean
│   ├── AzureOpenAIConfiguration.java   ❌     ← Azure 大模型连接
│   ├── AzureAuthenticationConfiguration.java ❌ ← Azure AD 认证
│   ├── BlobStorageProxyConfiguration.java ❌  ← Azure 存储
│   └── DocumentIntelligence*.java      ❌     ← Azure OCR
│
├── security/                            ← 用户认证
│   ├── LoggedUser.java          ❌     ← Azure AD 绑定
│   └── LoggedUserService.java   ❌     ← Azure AD 绑定
│
└── common/
    └── ChatGPTMessage.java       ❌     ← 消息格式转换

langchain4j-agents/
│
├── com/microsoft/langchain4j/agent/        ← 框架层（通用，跟银行无关）
│   ├── Agent.java                    ✅    接口：getName/getMetadata/invoke
│   ├── AgentMetadata.java            ✅    record：description + intents
│   ├── AgentExecutionException.java  ❌    异常类
│   ├── AbstractReActAgent.java       ✅    ReAct while循环骨架
│   ├── mcp/
│   │   ├── MCPToolAgent.java         ✅    工具从MCP自动发现，重写执行逻辑
│   │   ├── MCPServerMetadata.java    ❌    record：serverName+url+协议类型
│   │   └── MCPProtocolType.java      ❌    枚举：SSE 还是 STDIO
│   └── openapi/
│       ├── OpenAPIToolAgent.java     ✅    工具从yaml自动生成
│       └── OpenAPIImporterMetadata.java ❌ record：工具名+yaml路径+URL
│
├── com/microsoft/openai/samples/assistant/langchain4j/agent/
│   │                                      ← 业务层（银行专用）
│   ├── SupervisorRoutingAgent.java  ✅    Supervisor（纯LC4j版）
│   ├── mcp/
│   │   ├── AccountMCPAgent.java     ✅    填getName/getMetadata/getSystemMessage
│   │   ├── PaymentMCPAgent.java     ✅    最复杂，连3个MCP+1个本地工具
│   │   └── TransactionHistoryMCPAgent.java ✅ 连2个MCP Server
│   ├── openapi/
│   │   ├── AccountAgent.java        ❌    OpenAPI版，当前不用
│   │   ├── PaymentAgent.java        ❌    OpenAPI版，当前不用
│   │   └── TransactionHistoryAgent.java ❌  OpenAPI版，当前不用
│   └── tools/
│       └── InvoiceScanTool.java     ✅    本地@Tool，OCR发票扫描

```查账户余额
┌─────────────────────────────────────────────────────────────────────────────┐
│  第 0 步：系统启动                                                           │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Langgraph4JAgentsConfiguration.langgraph4jWorkflow()                        │
│    │                                                                        │
│    ├── new AccountMCPAgent(model, "bob.user@contoso.com",                    │
│    │       "http://localhost:8070/sse")                                     │
│    │       │                                                                │
│    │       └── 父类 MCPToolAgent 构造时：                                     │
│    │             mcpClient.listTools()  ←── HTTP ──→  Account 微服务 :8070   │
│    │                                                   返回 3 个工具：        │
│    │                                                   getAccountDetails     │
│    │                                                   getPaymentMethodDetails│
│    │                                                   getRegisteredBeneficiary│
│    │                                                                        │
│    ├── new TransactionHistoryMCPAgent(...)  ← 同样连 :8090 拉工具             │
│    ├── new PaymentMCPAgent(...)              ← 同样连 :8060/:8090/:8070      │
│    │                                                                        │
│    ├── new SupervisorAgent(model, [account, transaction, payment])           │
│    │       内部拼好 Prompt：                                                  │
│    │       "You are a banking customer support agent...                      │
│    │        AccountAgent: Personal financial advisor for retrieving...       │
│    │        TransactionHistoryAgent: Personal financial advisor for...       │
│    │        PaymentAgent: Personal financial advisor for submitting..."       │
│    │                                                                        │
│    └── new StateGraph<>(...)                                                │
│          .addNode("Supervisor", ...)                                        │
│          .addNode("AccountAgent", ...)                                      │
│          .addNode("TransactionHistoryAgent", ...)                            │
│          .addNode("PaymentAgent", ...)                                      │
│          .addEdge(START, "Supervisor")                                      │
│          .addConditionalEdges("Supervisor", route, ...)                     │
│          .addEdge("AccountAgent", END) ...                                  │
│          .compile(config)  ← 产出 CompiledGraph Bean                        │
│                                                                             │
│  启动完成，Graph 等待请求。                                                    │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│  第 1 步：用户输入                                                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  浏览器 React 前端                                                           │
│    用户输入："What's my account balance?"                                     │
│    点击发送                                                                  │
│       │                                                                     │
│       ▼                                                                     │
│  HTTP POST /api/chat                                                        │
│  Body: { "messages": [                                                      │
│    { "role": "user", "content": "What's my account balance?" }              │
│  ] }                                                                        │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│  第 2 步：ChatLanggraph4JController.openAIAsk()                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  // 校验 messages 非空                                                        │
│  // 生成 threadId = UUID.randomUUID()                                        │
│                                                                             │
│  RunnableConfig config = RunnableConfig.builder()                            │
│      .threadId(threadId)                                                    │
│      .build();                                                              │
│                                                                             │
│  // ★ 核心调用                                                                │
│  var state = compiledGraph.invoke(                                          │
│      Map.of("messages", [UserMessage("What's my account balance?")]),        │
│      config                                                                 │
│  );                                                                         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│  第 3 步：StateGraph 执行 START                                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  State: {                                                                    │
│    messages: [UserMessage("What's my account balance?")],                    │
│    nextAgent: null                                                          │
│  }                                                                          │
│       │                                                                     │
│       │ .addEdge(START, "Supervisor")                                       │
│       ▼                                                                     │
│  Supervisor 节点                                                             │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│  第 4 步：SupervisorAgentNode.apply(state)                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  SupervisorAgentNode.apply(state):                                          │
│       │                                                                     │
│       └── supervisorAgent.invoke(state.messages)                             │
│                │                                                            │
│                ▼                                                            │
│  拼装请求发给 DeepSeek：                                                      │
│  ┌─────────────────────────────────────────┐                                │
│  │ System: You are a banking customer      │                                │
│  │   support agent triaging conversation.  │                                │
│  │   Use the below agents metadata:         │                                │
│  │   AccountAgent: Personal financial      │                                │
│  │     advisor for retrieving bank         │                                │
│  │     account information.                │                                │
│  │   TransactionHistoryAgent: Personal...  │                                │
│  │   PaymentAgent: Personal financial...    │                                │
│  │   Answer only with the agent name.      │                                │
│  │                                         │                                │
│  │ User: What's my account balance?        │                                │
│  └─────────────────────────────────────────┘                                │
│                │                                                            │
│                ▼                                                            │
│  DeepSeek 返回: "AccountAgent"                                               │
│                │                                                            │
│                ▼                                                            │
│  SupervisorAgent 返回: [AiMessage("AccountAgent")]                           │
│                │                                                            │
│                ▼                                                            │
│  SupervisorAgentNode 写入 State:                                             │
│      return Map.of("nextAgent", "AccountAgent");                             │
│                                                                             │
│  State 变为:                                                                 │
│  { messages: [UserMessage("...")],                                          │
│    nextAgent: "AccountAgent" }                                              │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│  第 5 步：条件边路由                                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  supervisorRoute(state):                                                    │
│      nextAgent = state.nextAgent()  // "AccountAgent"                       │
│      Intent.names()                 // ["TransactionHistoryAgent",           │
│                                     //   "AccountAgent",                    │
│                                     //   "PaymentAgent"]                     │
│      匹配 → "AccountAgent"                                                   │
│      return "AccountAgent"                                                  │
│                                                                             │
│  Graph 路由到 AccountAgent 节点                                               │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│  第 6 步：AgentNode.apply(state)                                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  AgentNode.apply(state):                                                    │
│       │                                                                     │
│       └── accountMCPAgent.invoke(state.messages)                             │
│                │                                                            │
│                │  进入 AbstractReActAgent.invoke()                            │
│                │                                                            │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│  第 7 步：AbstractReActAgent.invoke() —— 第一次发 LLM                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  buildInternalChat(history):                                                │
│    拼装消息：                                                                 │
│  ┌─────────────────────────────────────────┐                                │
│  │ System: you are a personal financial    │  ← AccountMCPAgent 的 Prompt   │
│  │   advisor who help the user to retrieve │                                │
│  │   information about their bank accounts.│                                │
│  │   Always use: 'bob.user@contoso.com'    │                                │
│  │                                         │                                │
│  │ User: What's my account balance?        │  ← 用户的原始消息                 │
│  └─────────────────────────────────────────┘                                │
│                                                                             │
│  getToolSpecifications():                                                   │
│    返回 3 个工具（启动时从 :8070 MCP Server 拉取的）：                         │
│    ┌──────────────────────────────────────┐                                 │
│    │ 1. getAccountDetails                 │                                 │
│    │    参数: accountId (string)           │                                 │
│    │    描述: Get account details          │                                 │
│    │                                      │                                 │
│    │ 2. getPaymentMethodDetails           │                                 │
│    │    参数: paymentMethodId (string)     │                                 │
│    │                                      │                                 │
│    │ 3. getRegisteredBeneficiary          │                                 │
│    │    参数: accountId (string)           │                                 │
│    └──────────────────────────────────────┘                                 │
│                                                                             │
│  一起发给 DeepSeek ─────────────────────────→                                │
│                                                                             │
│  DeepSeek 返回:                                                              │
│  "I need to call getAccountDetails. But I need the accountId..."             │
│  hasToolExecutionRequests() = true                                          │
│  要调工具: getAccountDetails(accountId = ???)                                 │
│                                                                             │
│  进入 while 循环！                                                            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│  第 8 步：while 循环 —— 调用 AccountMCPService 查用户                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  DeepSeek 发现用户没给 accountId，但是它知道用户名是 bob.user@contoso.com        │
│  于是先调 Account 微服务暴露的另一个工具（也可能是多个工具连续调）：              │
│                                                                             │
│  DeepSeek 输出: getAccountsByUserName("bob.user@contoso.com")                │
│                                                                             │
│  executeToolRequests():                                                     │
│      │                                                                      │
│      │  ① extendedExecutorMap 里没有 → 跳过                                   │
│      │  ② tool2ClientMap.get("getAccountsByUserName")                        │
│      │     → 找到对应的 mcpClient                                            │
│      │                                                                      │
│      └── mcpClient.executeTool("getAccountsByUserName",                      │
│              "bob.user@contoso.com")                                        │
│                    │                                                        │
│                    │  MCP tools/call  ──HTTP──→  Account 微服务 :8070/sse     │
│                    │                           ↓                             │
│                    │              UserMCPService.getAccountsByUserName()      │
│                    │              返回: [{accountId:"1010", name:"Bob",...}]  │
│                    │                                                        │
│                    │  ←──────── HTTP ────────                                │
│                    │                                                        │
│  结果: [ToolExecutionResultMessage("accountId=1010,...")]                    │
│                                                                             │
│  追加到对话历史，再次发给 DeepSeek                                             │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│  第 9 步：while 循环 —— 第二轮，查余额                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  现在 LLM 知道 accountId=1010，再次决定调工具：                                │
│                                                                             │
│  DeepSeek 输出: getAccountDetails("1010")                                    │
│                                                                             │
│  executeToolRequests():                                                     │
│      │                                                                      │
│      └── mcpClient.executeTool("getAccountDetails", "1010")                  │
│                    │                                                        │
│                    │  MCP tools/call  ──HTTP──→  Account 微服务 :8070/sse     │
│                    │                           ↓                             │
│                    │            AccountMCPService.getAccountDetails("1010")   │
│                    │            → AccountService.get("1010")                 │
│                    │            返回: {                                       │
│                    │              id:"1010",                                 │
│                    │              userName:"bob.user@contoso.com",            │
│                    │              balance:"10000",                           │
│                    │              currency:"EUR",                            │
│                    │              paymentMethods:[...]                       │
│                    │            }                                            │
│                    │                                                        │
│                    │  ←──────── HTTP ────────                                │
│                    │                                                        │
│  结果: [ToolExecutionResultMessage("{id:1010, balance:10000,...}")]           │
│                                                                             │
│  追加到对话历史，再次发给 DeepSeek                                             │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│  第 10 步：跳出循环，返回最终回答                                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  DeepSeek 收到工具结果后，不需要再调工具了：                                    │
│                                                                             │
│  DeepSeek 返回:                                                              │
│  "Your account balance is €10,000. You have 2 payment methods:              │
│   BankTransfer (€10,000 available) and Visa (€350 available)."              │
│                                                                             │
│  hasToolExecutionRequests() = false → 跳出 while 循环                         │
│                                                                             │
│  buildResponse() → 过滤掉 SystemMessage，返回对话历史                           │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│  第 11 步：响应原路返回                                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  AgentNode: return Map.of("messages", [..., AiMessage("Your balance...")])   │
│       │                                                                     │
│       │ .addEdge("AccountAgent", END)                                       │
│       ▼                                                                     │
│  END                                                                         │
│       │                                                                     │
│  compiledGraph.invoke() 返回 State                                           │
│       │                                                                     │
│  ChatLanggraph4JController:                                                  │
│      AiMessage msg = state.lastMessage()                                     │
│      return ChatResponse(msg, threadId)  ← HTTP 200 JSON                     │
│       │                                                                     │
│       ▼                                                                     │
│  React 前端渲染: "Your account balance is €10,000..."                        │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```
