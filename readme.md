# SpringCloud项目整合Seata实现分布式事务

### 环境说明：

- jdk1.8
- h2
- springboot 2.5.6.RELEASE
- spring-cloud-alibaba-dependencies 2.2.0.RELEASE
- seata-server 1.5.0-SNAPSHOT
- mybatisplus
- spring-cloud-feign



### Reference Documentation

#### 步骤一：搭建多模块微服务工程



- 项目模块定义

```text
- 1. spring-cloud-seata：根/父工程
    - 1.1 account-parent：			    账户微服务
    	- 1.1.1 account-api				账户feign api服务
      - 1.1.2 account-service		账户核心service服务
    - 1.2 order-parent：					  订单微服务
      - 1.2.1 order-api					订单feign api微服务
      - 1.2.2 order-service			订单核心service服务
    - 1.3 storage-parent：				  库存微服务
      - 1.3.1 storage-api				库存feign api微服务
      - 1.3.2 storage-service		库存核心service服务
    - 1.4 customer-service：				消费者微服务  

```



- 构建父工程,步骤同新建maven工程相同

并定义父工程pom文件中SpringBoot、SpringCloud、OpenFeign等版本

```java
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <packaging>pom</packaging>
    <modules>
        <module>order-parent</module>
        <module>account-parent</module>
        <module>storage-parent</module>
    </modules>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.3.10.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.hongyan.study</groupId>
    <artifactId>spring-cloud-seata</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>spring-cloud-seata</name>
    <description>Demo project for Spring Cloud Feign Seata</description>
    <properties>
        <java.version>1.8</java.version>
        <springboot.version>2.3.10.RELEASE</springboot.version>
        <springcloud.version>Hoxton.SR9</springcloud.version>
        <spring-cloud-alibaba.version>2.2.3.RELEASE</spring-cloud-alibaba.version>
        <spring-cloud-feign.version>2.2.5.RELEASE</spring-cloud-feign.version>
        <lombok.version>1.18.16</lombok.version>
        <feign-okhttp.version>11.0</feign-okhttp.version>
    </properties>


    <!--通用依赖-->
    <dependencies>
        <!--配置文件处理器-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
        <!--监控-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <!--测试依赖-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!--Lombok-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <!--spring boot-->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${springboot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <!--spring cloud-->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${springcloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <!--spring cloud alibaba-->
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                <version>${spring-cloud-alibaba.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <!--spring cloud feign-->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-openfeign</artifactId>
                <version>${spring-cloud-feign.version}</version>
            </dependency>
            <dependency>
                <groupId>io.github.openfeign</groupId>
                <artifactId>feign-okhttp</artifactId>
                <version>${feign-okhttp.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```



- 构建各自微服务模块

搭建订单微服务过程（账户微服务、库存微服务步骤相同）

1、新增module

![image-20211101141058315](/Users/dearzhang/Library/Application Support/typora-user-images/image-20211101141058315.png)

2、创建maven项目工程

![image-20211101135604289](/Users/dearzhang/Library/Application Support/typora-user-images/image-20211101135604289.png)

3、定义服务名称

![image-20211101135622032](/Users/dearzhang/Library/Application Support/typora-user-images/image-20211101135622032.png)



![image-20211101135636181](/Users/dearzhang/Library/Application Support/typora-user-images/image-20211101135636181.png)

4、剔除无用pom依赖

![image-20211101135839712](/Users/dearzhang/Library/Application Support/typora-user-images/image-20211101135839712.png)

5、引入feign依赖，并剔除多余test模块,以及实现feign接口功能

![image-20211101142800299](/Users/dearzhang/Library/Application Support/typora-user-images/image-20211101142800299.png)

6、新增model对象

```java
@Data
public class OrderDto implements Serializable {

    /**
     * 用户编码
     */
    private String userId;

    /**
     * 商品编码
     */
    private String productCode;

    /**
     * 商品数量
     */
    private Integer count;

}
```

7、新增feign接口

```java
@FeignClient(value = "order-service", fallbackFactory = RestOrderApiFallbackFactory.class)
public interface RestOrderApi {

    /**
     * 创建订单
     * @param userId 用户编码
     * @param productCode 商品编码
     * @param count 商品数量
     * @return
     */
    @GetMapping("/order/create")
    OrderDto create(@RequestParam("userId") String userId, @RequestParam("productCode") String productCode, @RequestParam("count") Integer count);
}
```

8、增加feign工厂

```java
public class RestOrderApiFallbackFactory implements FallbackFactory<RestOrderApi> {


    @Override
    public RestOrderApi create(Throwable e) {
        RestOrderApiFallback restServiceFallback = new RestOrderApiFallback();
        restServiceFallback.setCause(e);
        return restServiceFallback;
    }
}
```

9、增加快速失败调用

```java
public class RestOrderApiFallback implements RestOrderApi {
    private static Logger logger = LoggerFactory.getLogger(RestOrderApiFallback.class);
    private Throwable cause;

    public Throwable getCause() {
        return cause;
    }
    public void setCause(Throwable cause) {
        this.cause = cause;
    }
    public RestOrderApiFallback() {
    }
    @Override
    public OrderDto create(String userId, String productCode, Integer count) {
        logger.error("userId :{}, productCode:{}, count:{} create error",userId,productCode,count);
        return null;
    }
}
```



10、创建xx-service核心微服务，采用csutom:https://start.springboot.io 方式 （官方方式老是超时，实在受不了了）

![image-20211101142939578](/Users/dearzhang/Library/Application Support/typora-user-images/image-20211101142939578.png)



![image-20211101143146289](/Users/dearzhang/Library/Application Support/typora-user-images/image-20211101143146289.png)

此处需要手动加上对应服务路径（因为这个服务当时还不存在嘛，系统吗，默认只能找到对应的上级目录）

![image-20211101143232439](/Users/dearzhang/Library/Application Support/typora-user-images/image-20211101143232439.png)

咱们在父目录中，手动添加子模块<module>xx-service<module>

```java
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>spring-cloud-seata</artifactId>
        <groupId>com.hongyan.study</groupId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>order-parent</artifactId>
    <packaging>pom</packaging>

    <name>order-parent</name>
    <!-- FIXME change it to the project's website -->
    <url>http://www.example.com</url>
    <modules>
        <module>order-api</module>
        <module>order-service</module>
    </modules>

    <description>订单微服务模块</description>
</project>

```



![image-20211101143459856](/Users/dearzhang/Library/Application Support/typora-user-images/image-20211101143459856.png)



11、调整核心服务xx-service的pom文件依赖关系，引入springcloud、openfeign、test等模块，并集成对应xx-api模块

```java
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<artifactId>order-parent</artifactId>
		<groupId>com.hongyan.study</groupId>
		<version>0.0.1-SNAPSHOT</version>
	</parent>
	<groupId>com.hongyan.study</groupId>
	<artifactId>order-service</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>order-service</name>
	<description>订单核心service微服务</description>
	<properties>
		<java.version>1.8</java.version>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-autoconfigure</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-aop</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-context-support</artifactId>
		</dependency>

		<!--引入order-api模块-->
		<dependency>
			<groupId>com.hongyan.study</groupId>
			<artifactId>order-api</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
		<!--引入lombok插件-->
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<!--引入spring-test模块-->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>

		<!--注册中心客户端-->
		<dependency>
			<groupId>com.alibaba.cloud</groupId>
			<artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
		</dependency>
		<!--配置中心客户端-->
		<dependency>
			<groupId>com.alibaba.cloud</groupId>
			<artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
		</dependency>
		<!-- Spring Cloud Open Feign -->
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-openfeign</artifactId>
		</dependency>
		<dependency>
			<groupId>io.github.openfeign</groupId>
			<artifactId>feign-okhttp</artifactId>
		</dependency>

</dependencies>

</project>

```



12、集成各自xx-api模块后，在service服务中实现api对应接口功能

```java
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderServiceController implements RestOrderApi {

    /*
     * 创建订单
     * @author zy
     * @date 2021-11-01 14:50
     * @description 创建订单
     * @param [userId, productCode, count]
     * @return com.hongyan.study.order.api.model.OrderDto
     */
    @GetMapping("/create")
    @Override
    public OrderDto create(String userId, String productCode, Integer count) {
        OrderDto orderDto = new OrderDto();
        orderDto.setUserId(userId);
        orderDto.setProductCode(productCode);
        orderDto.setCount(count);
        return orderDto;
    }
}
```



13、新增nacos配置bootstrap.yaml

```yaml
server:
  port: 7889

spring:
  application:
    name: order-service
  main:
    allow-bean-definition-overriding: true
  profiles:
    active: dev
  cloud:
    nacos:
      discovery:
        enabled: true
        register-enabled: true
      config:
        file-extension: yaml
---
spring:
  profiles: dev    #配置dev环境nacos的地址、账号、namespace，暂时使用ali-test-nacos
  cloud:
    nacos:
      discovery:
        username: nacos
        password: nacos
        namespace: 54ad6dd2-ed0f-42fa-b3bf-adeb0f951bee
        server-addr: ali-test-nacos.lp.com:80
      config:
        username: nacos
        password: nacos
        namespace: 54ad6dd2-ed0f-42fa-b3bf-adeb0f951bee
        server-addr: ali-test-nacos.lp.com:80
```



验证功能是否正常





#### 步骤二：搭建分布式事务Seata服务端

##### 1、利用申请到的资源机器先搭建docker（centos主机）

```shell
curl -fsSL https://get.docker.com | bash -s docker --mirror Aliyun
or
curl -sSL https://get.daocloud.io/docker | sh
```





##### 2、在docker中搭建seata服务端

- 获取镜像

```shell
docker pull seataio/seata-server:1.4.0
```

- 运行容器

```shell
docker run -it  -p 8091:8091 --name seata-server -v /root/docker/seata-server:/seata-server --net=host -d seataio/seata-server:1.4.0
```



- 调整seata服务端registry.conf、file.conf

registry.conf文件内容中设置注册中心、配置中心

```shell
registry {
  # file 、nacos 、eureka、redis、zk、consul、etcd3、sofa
  #todo 注册中心
  type = "nacos"
  loadBalance = "RandomLoadBalance"
  loadBalanceVirtualNodes = 10

  nacos {
    application = "seata-server"
    serverAddr = "ali-test-nacos.lp.com:80"
    group = "SEATA_GROUP"
    namespace = "54ad6dd2-ed0f-42fa-b3bf-adeb0f951bee"
    cluster = "default"
    username = "nacos"
    password = "nacos"
  }
  eureka {
    serviceUrl = "http://localhost:8761/eureka"
    application = "default"
    weight = "1"
  }
  redis {
    serverAddr = "localhost:6379"
    db = 0
    password = ""
    cluster = "default"
    timeout = 0
  }
  zk {
    cluster = "default"
    serverAddr = "127.0.0.1:2181"
    sessionTimeout = 6000
    connectTimeout = 2000
    username = ""
    password = ""
  }
  consul {
    cluster = "default"
    serverAddr = "127.0.0.1:8500"
  }
  etcd3 {
    cluster = "default"
    serverAddr = "http://localhost:2379"
  }
  sofa {
    serverAddr = "127.0.0.1:9603"
    application = "default"
    region = "DEFAULT_ZONE"
    datacenter = "DefaultDataCenter"
    cluster = "default"
    group = "SEATA_GROUP"
    addressWaitTime = "3000"
  }
  file {
    name = "file.conf"
  }
}

config {
  # file、nacos 、apollo、zk、consul、etcd3
  #todo 配置中心
  type = "nacos"

  nacos {
    serverAddr = "ali-test-nacos.lp.com:80"
    namespace = "54ad6dd2-ed0f-42fa-b3bf-adeb0f951bee"
    group = "SEATA_GROUP"
    username = "nacos"
    password = "nacos"
  }
  consul {
    serverAddr = "127.0.0.1:8500"
  }
  apollo {
    appId = "seata-server"
    apolloMeta = "http://192.168.1.204:8801"
    namespace = "application"
    apolloAccesskeySecret = ""
  }
  zk {
    serverAddr = "127.0.0.1:2181"
    sessionTimeout = 6000
    connectTimeout = 2000
    username = ""
    password = ""
  }
  etcd3 {
    serverAddr = "http://localhost:2379"
  }
  file {
    name = "file.conf"
  }
}
```



file.conf文件内容中设置存储模式

```shell
## transaction log store, only used in seata-server
store {
  ## store mode: file、db、redis
  #todo 存储模式，file为单机模式，集群模式建议改用db、redis
  mode = "file"

  ## file store property
  file {
    ## store location dir
    dir = "sessionStore"
    # branch session size , if exceeded first try compress lockkey, still exceeded throws exceptions
    maxBranchSessionSize = 16384
    # globe session size , if exceeded throws exceptions
    maxGlobalSessionSize = 512
    # file buffer size , if exceeded allocate new buffer
    fileWriteBufferCacheSize = 16384
    # when recover batch read size
    sessionReloadReadSize = 100
    # async, sync
    flushDiskMode = async
  }

  ## database store property
  db {
    ## the implement of javax.sql.DataSource, such as DruidDataSource(druid)/BasicDataSource(dbcp)/HikariDataSource(hikari) etc.
    datasource = "druid"
    ## mysql/oracle/postgresql/h2/oceanbase etc.
    dbType = "mysql"
    driverClassName = "com.mysql.jdbc.Driver"
    url = "jdbc:mysql://127.0.0.1:3306/seata"
    user = "mysql"
    password = "mysql"
    minConn = 5
    maxConn = 100
    globalTable = "global_table"
    branchTable = "branch_table"
    lockTable = "lock_table"
    queryLimit = 100
    maxWait = 5000
  }

  ## redis store property
  redis {
    host = "127.0.0.1"
    port = "6379"
    password = ""
    database = "0"
    minConn = 1
    maxConn = 10
    maxTotal = 100
    queryLimit = 100
  }

}
```





#### 步骤三：集成Seata客户端

1、引入seata核心包

```java
    <properties>
        <seata.version>1.5.0-SNAPSHOT</seata.version>
    </properties>

<dependency>
  <groupId>io.seata</groupId>
  <artifactId>seata-spring-boot-starter</artifactId>
  <version>${seata.version}</version>
</dependency>
```



2、配置seata配置文件

```java
seata:
  registry:
    type: nacos
    nacos:
      application: seata-server
      server-addr: ali-test-nacos.lp.com:80
      group : "SEATA_GROUP"
      namespace: 54ad6dd2-ed0f-42fa-b3bf-adeb0f951bee
      username: nacos
      password: nacos
  config:
    type: nacos
    nacos:
      application: seata-server
      server-addr: ali-test-nacos.lp.com:80
      group : "SEATA_GROUP"
      namespace: 54ad6dd2-ed0f-42fa-b3bf-adeb0f951bee
      username: nacos
      password: nacos
  service:
    vgroup-mapping:
      spring-cloud-seata-service-group: default
  tx-service-group: spring-cloud-seata-service-group
```



3、核心代码处增加全局事务自定义注解@GlobalTransactional

```java
@Slf4j
@RestController
@RequestMapping("/customer/order")
public class OrderController {

    @Autowired
    private RestAccountApi restAccountApi;

    @Autowired
    private RestOrderApi restOrderApi;

    @Autowired
    private RestStorageApi restStorageApi;

    @GlobalTransactional(name = "spring-cloud-seata-tx")
    @GetMapping("/createOrder")
    public String createOrder(String userId, String productCode, Integer count, BigDecimal money,Boolean exception){
        log.info("<createOrder-req> userId:{}, productCode:{},count:{},money:{},exception:{}",userId,productCode,count,money,exception);
        log.info("business Service Begin ... xid: {}",RootContext.getXID());
        OrderDto orderDto = restOrderApi.create(userId, productCode, count);
        Boolean flag = restAccountApi.debit(userId,money);
        //模拟异常情况
        if(exception){
            int i = 1/0;
        }

        restStorageApi.deduct(productCode, count);

        log.info("<createOrder-resp> orderDto:{},flag:{}", JSON.toJSONString(orderDto),flag);

        return "success";
    }
}
```



4、增加全局事务XID的跨服务传递规则

```java
/**
 * @author zy
 * <p>在调用feign接口之前,将当前线程的reqId放到请求头里面,串联下游应用<p>
 * @date 2020-06-18
 **/
@Component
@Slf4j
public class FeignAddHeaderConfiguration {
    @Bean
    public FeignHeaderRequestInterceptor basicAuthRequestInterceptor() {
        return new FeignHeaderRequestInterceptor();
    }

    public static class FeignHeaderRequestInterceptor implements RequestInterceptor {
        @Override
        public void apply(RequestTemplate template) {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes == null) {
                return;
            }
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            //处理header信息
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    Enumeration<String> values = request.getHeaders(name);
                    while (values.hasMoreElements()) {
                        String value = values.nextElement();
                        if(RootContext.KEY_XID.toLowerCase().equals(name)){
                            log.info("global xid add next feign api  name:{},value:{}",name,value);
                            template.header(name, value);
                        }
                    }
                }
            }
        }
    }
}
```



5、新增seata事务记录表

```sql
DROP TABLE IF EXISTS undo_log;

CREATE TABLE `undo_log` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`branch_id` bigint(20) NOT NULL,
	`xid` varchar(100) NOT NULL,
	`context` varchar(128) NOT NULL,
	`rollback_info` longblob NOT NULL,
	`log_status` int(11) NOT NULL,
	`log_created` datetime NOT NULL,
	`log_modified` datetime NOT NULL,
	`ext` varchar(100) DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ;
```



6、功能走起来，查看调用日志

测试正常提交：

```shell
127.0.0.1:7891/customer/order/createOrder?userId=Jone&money=5.34&productCode=11111111&count=5&exception=false
```



```shell
[10:05:48:074][ INFO][io.seata.tm.TransactionManagerHolder:40] [reqId:] TransactionManager Singleton io.seata.tm.DefaultTransactionManager@1b2cf32d
[10:05:48:109][ INFO][io.seata.tm.api.DefaultGlobalTransaction:109] [reqId:] Begin new global transaction [172.31.7.186:8091:204539211225473024]
[10:05:48:113][ INFO][com.hongyan.study.customerservice.controller.OrderController:40] [reqId:] <createOrder-req> userId:Jone, productCode:11111111,count:5,money:5.34,exception:false
[10:05:48:114][ INFO][com.hongyan.study.customerservice.controller.OrderController:41] [reqId:] business Service Begin ... xid: 172.31.7.186:8091:204539211225473024
[10:05:48:332][ INFO][com.netflix.config.ChainedDynamicProperty:115] [reqId:] Flipping property: order-service.ribbon.ActiveConnectionsLimit to use NEXT property: niws.loadbalancer.availabilityFilteringRule.activeConnectionsLimit = 2147483647
[10:05:48:346][ INFO][com.netflix.loadbalancer.BaseLoadBalancer:197] [reqId:] Client: order-service instantiated a LoadBalancer: DynamicServerListLoadBalancer:{NFLoadBalancer:name=order-service,current list of Servers=[],Load balancer stats=Zone stats: {},Server stats: []}ServerList:null
[10:05:48:352][ INFO][com.netflix.loadbalancer.DynamicServerListLoadBalancer:222] [reqId:] Using serverListUpdater PollingServerListUpdater
[10:05:48:388][ INFO][com.alibaba.nacos.client.naming:191] [reqId:] new ips(1) service: DEFAULT_GROUP@@order-service -> [{"instanceId":"10.28.180.125#7889#DEFAULT#DEFAULT_GROUP@@order-service","ip":"10.28.180.125","port":7889,"weight":1.0,"healthy":true,"enabled":true,"ephemeral":true,"clusterName":"DEFAULT","serviceName":"DEFAULT_GROUP@@order-service","metadata":{"preserved.register.source":"SPRING_CLOUD"},"ipDeleteTimeout":30000,"instanceHeartBeatInterval":5000,"instanceHeartBeatTimeOut":15000}]
[10:05:48:390][ INFO][com.alibaba.nacos.client.naming:228] [reqId:] current ips:(1) service: DEFAULT_GROUP@@order-service -> [{"instanceId":"10.28.180.125#7889#DEFAULT#DEFAULT_GROUP@@order-service","ip":"10.28.180.125","port":7889,"weight":1.0,"healthy":true,"enabled":true,"ephemeral":true,"clusterName":"DEFAULT","serviceName":"DEFAULT_GROUP@@order-service","metadata":{"preserved.register.source":"SPRING_CLOUD"},"ipDeleteTimeout":30000,"instanceHeartBeatInterval":5000,"instanceHeartBeatTimeOut":15000}]
[10:05:48:402][ INFO][com.netflix.config.ChainedDynamicProperty:115] [reqId:] Flipping property: order-service.ribbon.ActiveConnectionsLimit to use NEXT property: niws.loadbalancer.availabilityFilteringRule.activeConnectionsLimit = 2147483647
[10:05:48:404][ INFO][com.netflix.loadbalancer.DynamicServerListLoadBalancer:150] [reqId:] DynamicServerListLoadBalancer for client order-service initialized: DynamicServerListLoadBalancer:{NFLoadBalancer:name=order-service,current list of Servers=[10.28.180.125:7889],Load balancer stats=Zone stats: {unknown=[Zone:unknown;	Instance count:1;	Active connections count: 0;	Circuit breaker tripped count: 0;	Active connections per server: 0.0;]
},Server stats: [[Server:10.28.180.125:7889;	Zone:UNKNOWN;	Total Requests:0;	Successive connection failure:0;	Total blackout seconds:0;	Last connection made:Thu Jan 01 08:00:00 CST 1970;	First connection made: Thu Jan 01 08:00:00 CST 1970;	Active Connections:0;	total failure count in last (1000) msecs:0;	average resp time:0.0;	90 percentile resp time:0.0;	95 percentile resp time:0.0;	min resp time:0.0;	max resp time:0.0;	stddev resp time:0.0]
]}ServerList:com.alibaba.cloud.nacos.ribbon.NacosServerList@f720519
[10:05:49:356][ INFO][com.netflix.config.ChainedDynamicProperty:115] [reqId:] Flipping property: order-service.ribbon.ActiveConnectionsLimit to use NEXT property: niws.loadbalancer.availabilityFilteringRule.activeConnectionsLimit = 2147483647
[10:05:49:595][ INFO][com.netflix.config.ChainedDynamicProperty:115] [reqId:] Flipping property: account-service.ribbon.ActiveConnectionsLimit to use NEXT property: niws.loadbalancer.availabilityFilteringRule.activeConnectionsLimit = 2147483647
[10:05:49:599][ INFO][com.netflix.loadbalancer.BaseLoadBalancer:197] [reqId:] Client: account-service instantiated a LoadBalancer: DynamicServerListLoadBalancer:{NFLoadBalancer:name=account-service,current list of Servers=[],Load balancer stats=Zone stats: {},Server stats: []}ServerList:null
[10:05:49:602][ INFO][com.netflix.loadbalancer.DynamicServerListLoadBalancer:222] [reqId:] Using serverListUpdater PollingServerListUpdater
[10:05:49:631][ INFO][com.alibaba.nacos.client.naming:191] [reqId:] new ips(1) service: DEFAULT_GROUP@@account-service -> [{"instanceId":"10.28.180.125#7888#DEFAULT#DEFAULT_GROUP@@account-service","ip":"10.28.180.125","port":7888,"weight":1.0,"healthy":true,"enabled":true,"ephemeral":true,"clusterName":"DEFAULT","serviceName":"DEFAULT_GROUP@@account-service","metadata":{"preserved.register.source":"SPRING_CLOUD"},"ipDeleteTimeout":30000,"instanceHeartBeatInterval":5000,"instanceHeartBeatTimeOut":15000}]
[10:05:49:632][ INFO][com.alibaba.nacos.client.naming:228] [reqId:] current ips:(1) service: DEFAULT_GROUP@@account-service -> [{"instanceId":"10.28.180.125#7888#DEFAULT#DEFAULT_GROUP@@account-service","ip":"10.28.180.125","port":7888,"weight":1.0,"healthy":true,"enabled":true,"ephemeral":true,"clusterName":"DEFAULT","serviceName":"DEFAULT_GROUP@@account-service","metadata":{"preserved.register.source":"SPRING_CLOUD"},"ipDeleteTimeout":30000,"instanceHeartBeatInterval":5000,"instanceHeartBeatTimeOut":15000}]
[10:05:49:633][ INFO][com.netflix.config.ChainedDynamicProperty:115] [reqId:] Flipping property: account-service.ribbon.ActiveConnectionsLimit to use NEXT property: niws.loadbalancer.availabilityFilteringRule.activeConnectionsLimit = 2147483647
[10:05:49:635][ INFO][com.netflix.loadbalancer.DynamicServerListLoadBalancer:150] [reqId:] DynamicServerListLoadBalancer for client account-service initialized: DynamicServerListLoadBalancer:{NFLoadBalancer:name=account-service,current list of Servers=[10.28.180.125:7888],Load balancer stats=Zone stats: {unknown=[Zone:unknown;	Instance count:1;	Active connections count: 0;	Circuit breaker tripped count: 0;	Active connections per server: 0.0;]
},Server stats: [[Server:10.28.180.125:7888;	Zone:UNKNOWN;	Total Requests:0;	Successive connection failure:0;	Total blackout seconds:0;	Last connection made:Thu Jan 01 08:00:00 CST 1970;	First connection made: Thu Jan 01 08:00:00 CST 1970;	Active Connections:0;	total failure count in last (1000) msecs:0;	average resp time:0.0;	90 percentile resp time:0.0;	95 percentile resp time:0.0;	min resp time:0.0;	max resp time:0.0;	stddev resp time:0.0]
]}ServerList:com.alibaba.cloud.nacos.ribbon.NacosServerList@5c410738
[10:05:50:606][ INFO][com.netflix.config.ChainedDynamicProperty:115] [reqId:] Flipping property: account-service.ribbon.ActiveConnectionsLimit to use NEXT property: niws.loadbalancer.availabilityFilteringRule.activeConnectionsLimit = 2147483647
[10:05:54:285][ INFO][com.netflix.config.ChainedDynamicProperty:115] [reqId:] Flipping property: storage-service.ribbon.ActiveConnectionsLimit to use NEXT property: niws.loadbalancer.availabilityFilteringRule.activeConnectionsLimit = 2147483647
[10:05:54:287][ INFO][com.netflix.loadbalancer.BaseLoadBalancer:197] [reqId:] Client: storage-service instantiated a LoadBalancer: DynamicServerListLoadBalancer:{NFLoadBalancer:name=storage-service,current list of Servers=[],Load balancer stats=Zone stats: {},Server stats: []}ServerList:null
[10:05:54:289][ INFO][com.netflix.loadbalancer.DynamicServerListLoadBalancer:222] [reqId:] Using serverListUpdater PollingServerListUpdater
[10:05:54:316][ INFO][com.alibaba.nacos.client.naming:191] [reqId:] new ips(1) service: DEFAULT_GROUP@@storage-service -> [{"instanceId":"10.28.180.125#7890#DEFAULT#DEFAULT_GROUP@@storage-service","ip":"10.28.180.125","port":7890,"weight":1.0,"healthy":true,"enabled":true,"ephemeral":true,"clusterName":"DEFAULT","serviceName":"DEFAULT_GROUP@@storage-service","metadata":{"preserved.register.source":"SPRING_CLOUD"},"ipDeleteTimeout":30000,"instanceHeartBeatInterval":5000,"instanceHeartBeatTimeOut":15000}]
[10:05:54:317][ INFO][com.alibaba.nacos.client.naming:228] [reqId:] current ips:(1) service: DEFAULT_GROUP@@storage-service -> [{"instanceId":"10.28.180.125#7890#DEFAULT#DEFAULT_GROUP@@storage-service","ip":"10.28.180.125","port":7890,"weight":1.0,"healthy":true,"enabled":true,"ephemeral":true,"clusterName":"DEFAULT","serviceName":"DEFAULT_GROUP@@storage-service","metadata":{"preserved.register.source":"SPRING_CLOUD"},"ipDeleteTimeout":30000,"instanceHeartBeatInterval":5000,"instanceHeartBeatTimeOut":15000}]
[10:05:54:318][ INFO][com.netflix.config.ChainedDynamicProperty:115] [reqId:] Flipping property: storage-service.ribbon.ActiveConnectionsLimit to use NEXT property: niws.loadbalancer.availabilityFilteringRule.activeConnectionsLimit = 2147483647
[10:05:54:320][ INFO][com.netflix.loadbalancer.DynamicServerListLoadBalancer:150] [reqId:] DynamicServerListLoadBalancer for client storage-service initialized: DynamicServerListLoadBalancer:{NFLoadBalancer:name=storage-service,current list of Servers=[10.28.180.125:7890],Load balancer stats=Zone stats: {unknown=[Zone:unknown;	Instance count:1;	Active connections count: 0;	Circuit breaker tripped count: 0;	Active connections per server: 0.0;]
},Server stats: [[Server:10.28.180.125:7890;	Zone:UNKNOWN;	Total Requests:0;	Successive connection failure:0;	Total blackout seconds:0;	Last connection made:Thu Jan 01 08:00:00 CST 1970;	First connection made: Thu Jan 01 08:00:00 CST 1970;	Active Connections:0;	total failure count in last (1000) msecs:0;	average resp time:0.0;	90 percentile resp time:0.0;	95 percentile resp time:0.0;	min resp time:0.0;	max resp time:0.0;	stddev resp time:0.0]
]}ServerList:com.alibaba.cloud.nacos.ribbon.NacosServerList@3ac63028
[10:05:55:294][ INFO][com.netflix.config.ChainedDynamicProperty:115] [reqId:] Flipping property: storage-service.ribbon.ActiveConnectionsLimit to use NEXT property: niws.loadbalancer.availabilityFilteringRule.activeConnectionsLimit = 2147483647
[10:05:55:445][ INFO][com.hongyan.study.customerservice.controller.OrderController:51] [reqId:] <createOrder-resp> orderDto:{"count":5,"productCode":"11111111","userId":"Jone"},flag:true
[10:05:55:473][ INFO][io.seata.tm.api.DefaultGlobalTransaction:190] [reqId:] Suspending current transaction, xid = 172.31.7.186:8091:204539211225473024
[10:05:55:474][ INFO][io.seata.tm.api.DefaultGlobalTransaction:144] [reqId:] [172.31.7.186:8091:204539211225473024] commit status: Committed
```



测试异常提交：

```shell
127.0.0.1:7891/customer/order/createOrder?userId=Jone&money=5.34&productCode=11111111&count=5&exception=true
```



```shell
[10:07:50:430][ INFO][io.seata.tm.api.DefaultGlobalTransaction:109] [reqId:] Begin new global transaction [172.31.7.186:8091:204539724306292736]
[10:07:50:431][ INFO][com.hongyan.study.customerservice.controller.OrderController:40] [reqId:] <createOrder-req> userId:Jone, productCode:11111111,count:5,money:5.34,exception:true
[10:07:50:431][ INFO][com.hongyan.study.customerservice.controller.OrderController:41] [reqId:] business Service Begin ... xid: 172.31.7.186:8091:204539724306292736
[10:07:50:756][ INFO][io.seata.tm.api.DefaultGlobalTransaction:190] [reqId:] Suspending current transaction, xid = 172.31.7.186:8091:204539724306292736
[10:07:50:757][ INFO][io.seata.tm.api.DefaultGlobalTransaction:180] [reqId:] [172.31.7.186:8091:204539724306292736] rollback status: Rollbacked
[10:07:50:774][ERROR][org.apache.catalina.core.ContainerBase.[Tomcat].[localhost].[/].[dispatcherServlet]:175] [reqId:] Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is java.lang.ArithmeticException: / by zero] with root cause
java.lang.ArithmeticException: / by zero

```







### 借鉴于：

- [分布式事务AT、TCC、Saga、XA 模式分析对比](https://www.cnblogs.com/JaminXie/p/13885671.html)
- [分布式事务的4种模式](https://zhuanlan.zhihu.com/p/78599954)
- [Spring Boot 集成 Seata 解决分布式事务问题](https://www.cnblogs.com/huanchupkblog/p/12185851.html)
- [Java--IDEA创建多服务模块的SpringCloud微服务项目](https://blog.csdn.net/MinggeQingchun/article/details/111594699)
- [Seata官方文档](http://seata.io/zh-cn/docs/overview/what-is-seata.html)
- [seata异常：can not get cluster name in registry config](https://www.91mszl.com/zhangwuji/article/details/1342)
- [SpringBoot 整合 Seata](https://blog.csdn.net/qq_32691791/article/details/112727500)
- [nacos、feign、hystrix、seata整合](https://juejin.cn/post/6997787728897294350)



