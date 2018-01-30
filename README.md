# tidyspider
tidyspider是一个轻量级的爬虫服务

## 配置
 + 服务配置
 
 | 字段         | 解释 |
 |-------------|------|
 | name        | 标识应用名字 |
 | concurrency | 并发数 |
 | jar_path    | 动态加载的包目录 |
 | duplicate_task | 是否执行重复的任务 |

## 打包
```
./setup.sh
```

## 启动
```
cd ./target
./tidyspider start
```
