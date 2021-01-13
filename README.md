# scheduler
Java 开发的大数据任务调度及监控系统


## 扩展了调度及监控的功能
### 监控功能
- 提供了源表及前台表的依赖查询 ，结合zeppelin 快速图表查询功能
  - 数据的血缘关系（例如，多少个前台报表依赖于某一后台表）
  - 数据的增长情况历史回溯
  - 通过每日任务可以观察前台和后台报表的日增长情况
  - 每日任务top执行时间排名
  - 实时日志显示
### 调度功能
- 提供了任务调度的报警（任务执行失败，任务执行超时）
  - 邮件报警
  - 任务依赖调度
  - 任务周期性调度
  - 任务分布式调度（需要打通ssh ，Spark ,hive 任务)
  - 前台访问连接执行后台代码（日志实时显示）
  - 控制任务的并发度
### 其他功能
- [x] 集群间数据迁移功能
- [x] 临时表上传