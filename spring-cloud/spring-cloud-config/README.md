配置中心拉取git配置文件:
```$xslt
[on(6)-127.0.0.1] o.s.c.c.s.e.NativeEnvironmentRepository  : Adding property source: file:/D:/git-config/spring-cloud/spring-cloud-test-config/application.yml
[nio-8010-exec-2] pClientConfigurableHttpConnectionFactoryx : No custom http config found for URL: https://github.com/heibaiying/spring-samples-for-all/info/refs?service=git-upload-pack
[nio-8010-exec-2] o.s.cloud.commons.util.InetUtils         : Cannot determine local hostname
[nio-8010-exec-2] o.s.c.c.s.e.NativeEnvironmentRepository  : Adding property source: file:/D:/git-config/spring-cloud/spring-cloud-test-config/application-dev.yml
[nio-8010-exec-2] o.s.c.c.s.e.NativeEnvironmentRepository  : Adding property source: file:/D:/git-config/spring-cloud/spring-cloud-test-config/application.yml
```

每次启动时候都会去注册中心拉取最新的代码，但是已经启动的项目是不会热更新的
```$xslt
2019-01-17 13:35:14.986  INFO 4448 --- [           main] c.c.c.ConfigServicePropertySourceLocator : Fetching config from server at : http://localhost:8020/
2019-01-17 13:35:20.910  INFO 4448 --- [           main] c.c.c.ConfigServicePropertySourceLocator : Located environment: name=config-client, profiles=[dev], label=master, version=50dcfb85cd751e4f28761cd6bad84c1f73034002, state=null
```

消息总线
```$xslt
2019-01-17 13:37:50.998  INFO 4448 --- [e0bL-TWAMhWg-19] o.s.a.r.c.CachingConnectionFactory       : Created new connection: rabbitConnectionFactory#496c6d94:22/SimpleConnection@185d85d2 [delegate=amqp://guest@127.0.0.1:5672/, localPort= 63713]
2019-01-17 13:37:51.015  INFO 4448 --- [e0bL-TWAMhWg-19] o.s.amqp.rabbit.core.RabbitAdmin         : Auto-declaring a non-durable, auto-delete, or exclusive Queue (springCloudBus.anonymous.iY4TIIi9TSe0bL-TWAMhWg) durable:false, auto-delete:true, exclusive:true. It will be redeclared if the broker stops and is restarted while the connection factory is alive, but all messages will be lost.
```

刷新应用上下文，热更新

```$xslt
Attempting to connect to: [127.0.0.1:5672]
Created new connection: rabbitConnectionFactory.publisher#b00f2d6:0/SimpleConnection@403c0406 [delegate=amqp://guest@127.0.0.1:5672/, localPort= 62748]
Fetching config from server at : http://DESKTOP-8JGSFLJ:8020/
Refreshing org.springframework.context.annotation.AnnotationConfigApplicationContext@62e12f66
```


http://localhost:8030/actuator/bus-refresh
