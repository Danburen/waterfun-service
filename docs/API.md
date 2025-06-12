# WaterFun Service API Documentation

## 项目概述
WaterFun Service 是一个基于 Spring Boot 的后端服务项目，提供了一系列 RESTful API 接口。

## 基础信息
- 基础URL: `http://localhost:8080`
- 所有请求和响应均使用 JSON 格式
- 认证方式: Bearer Token

## 接口列表

### 1. 认证相关接口

#### 1.1 用户登录
- **接口**: `/api/auth/login`
- **方法**: POST
- **描述**: 用户登录接口
- **请求体**:
  ```json
  {
    "username": "string",
    "password": "string"
  }
  ```
- **响应**:
  ```json
  {
    "token": "string",
    "expiresIn": "number"
  }
  ```

#### 1.2 用户注册
- **接口**: `/api/auth/register`
- **方法**: POST
- **描述**: 新用户注册接口
- **请求体**:
  ```json
  {
    "username": "string",
    "password": "string",
    "email": "string"
  }
  ```
- **响应**:
  ```json
  {
    "message": "string",
    "userId": "string"
  }
  ```

### 2. 测试接口

#### 2.1 健康检查
- **接口**: `/api/test/health`
- **方法**: GET
- **描述**: 服务健康状态检查
- **响应**:
  ```json
  {
    "status": "UP",
    "timestamp": "string"
  }
  ```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 通用响应格式

### 成功响应
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 错误响应
```json
{
  "code": 400,
  "message": "error message",
  "data": null
}
```

## 注意事项
1. 所有需要认证的接口都需要在请求头中携带 `Authorization: Bearer {token}`
2. 请求体中的时间格式统一使用 ISO 8601 标准
3. 分页接口统一使用 `page` 和 `size` 参数
4. 所有接口的响应时间不应超过 3 秒

## 更新日志
- 2024-03-21: 初始版本 