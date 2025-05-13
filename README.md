# 面试刷题平台

## 项目介绍

面试刷题平台是一个帮助用户准备技术面试的在线系统，提供海量面试题目的浏览、练习和管理功能。系统分为用户端和管理端两个部分，采用现代化技术栈构建，实现高可用、高性能、高并发的系统架构。

### 项目背景

为帮助求职者更好地准备技术面试，本平台整合了各类技术领域的面试题目，用户可以通过分类浏览、按需练习，管理员可以进行内容维护和系统管理。

## 技术架构

### 后端技术栈

- **核心框架**：SpringBoot 2.7.2
- **ORM框架**：MyBatis-Plus 3.5.2
- **缓存技术**：Redis + Redisson
- **搜索引擎**：ElasticSearch
- **热点数据**：HotKey
- **限流熔断**：Sentinel
- **配置中心**：Nacos
- **文档工具**：Knife4j (基于Swagger)
- **其他工具**：Hutool、easyexcel等

### 系统架构图

```
用户请求 --> 负载均衡 --> 应用服务器集群 --> 数据服务层
                                 ↑
                                 ↓
                       缓存/搜索/配置中心
```

## 功能模块

### 用户端功能

1. **用户模块**
   - 用户注册/登录（账号密码、微信登录）
   - 用户信息管理
   - 用户权限控制

2. **题目浏览**
   - 查看题目列表
   - 题目详情浏览
   - 题目分类筛选
   - 题目搜索

3. **题库浏览**
   - 题库列表查看
   - 题库内题目浏览
   - 热门题库推荐

4. **签到系统**
   - 每日签到
   - 签到记录查看
   - 签到统计

### 管理端功能

1. **题目管理**
   - 添加/编辑/删除题目
   - 题目标签管理
   - 题目批量导入导出

2. **题库管理**
   - 创建/编辑/删除题库
   - 管理题目与题库关系
   - 题库热点缓存管理

3. **系统管理**
   - 用户黑名单管理
   - 系统限流和熔断配置
   - 热点数据监控

## 系统特点

1. **高性能**：
   - 使用Redis进行数据缓存
   - HotKey热点数据实时感知
   - ElasticSearch提供高效搜索

2. **高可用**：
   - Sentinel实现限流和熔断
   - Nacos提供动态配置管理
   - 分布式会话管理

3. **安全可靠**：
   - 用户黑名单动态更新
   - 接口访问权限控制
   - 敏感操作日志记录

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Redis 6.0+
- ElasticSearch 7.x
- Nacos 2.x
- Etcd (用于HotKey服务)

### 安装部署

1. **克隆项目**

```bash
git clone https://github.com/yourusername/interview.git
cd interview
```

2. **配置数据库**

导入SQL脚本并修改`application.yml`中的数据库配置：

```bash
mysql -u username -p your_database < sql/create_table.sql
```

3. **修改配置文件**

根据实际环境修改`application.yml`中相关配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database
    username: your_username
    password: your_password
  redis:
    host: your_redis_host
    port: 6379
    password: your_password
  elasticsearch:
    uris: http://your_es_host:9200
    
hotkey:
  etcd-server: http://your_etcd_server:2379
  
nacos:
  config:
    server-addr: your_nacos_server:8848
```

4. **编译打包**

```bash
mvn clean package -DskipTests
```

5. **启动应用**

```bash
java -jar target/interview-0.0.1-SNAPSHOT.jar
```

访问：`http://localhost:8101/api/doc.html` 查看API文档

## 开发指南

### 项目结构

```
src/main/java/com/zxc/interview/
├── annotation        // 自定义注解
├── aop               // AOP切面
├── common            // 通用类
├── config            // 配置类
├── constant          // 常量类
├── controller        // 控制器
├── esdao             // ES操作类
├── exception         // 异常处理
├── job               // 定时任务
├── manager           // 业务管理
├── mapper            // MyBatis映射
├── model             // 数据模型
│   ├── dto           // 数据传输对象
│   ├── entity        // 实体类
│   ├── enums         // 枚举类
│   └── vo            // 视图对象
├── nacos             // Nacos相关
├── sentinel          // Sentinel相关
├── service           // 服务接口
│   └── impl          // 服务实现
├── utils             // 工具类
└── MainApplication   // 应用入口
```

### API文档

项目集成了Knife4j，启动后访问：`http://localhost:8101/api/doc.html` 查看API文档




