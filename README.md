[TOC]

# 一、hello-openfeign 介绍

基于 OpenFeign 进行模块间通信的示例代码。

## 1.1 模块简单介绍

### croducer 模块

producer 模块作为服务被调用方，即服务生产者，类似于消息队列中的消息生产者。

### consumer 模块

consumer 模块作为服务调用方，即服务消费者，类似于消息队列中的消费者。

项目集成 OpenFeign 实现 consumer 模块对 producer 模块的调用。Demo 略过 Eureka 服务注册中心，直接通过 IP & Port 发现服务提供方。

### common 模块

存放一些全局类和方法，包括 Feign 异常拦截器（错误解码处理 ErrorDecoder ）、自定义异常类、Feign 请求拦截器。

## 1.2 测试流程

> 1、启动 producer 模块主类 ProducerApplication 和 consumer 模块主类 ConsumerApplication

> 2、根据 application.yml 配置文件中的配置，在浏览器 Or Postman 中输入 ``localhost:8080/file`` ，返回 ``FileName is returned，Success! `` 则表示从 consumer 模块调用 producer 模块成功。

> 3、测试服务端抛出异常，客户端获取异常状态码和异常信息。
>
> 具体做法是：实现 ErrorDecoder 接口，当服务端返回异常时，FeignErrorDecoder 会捕获异常，通过 HTTP 返回 response 。
>
> 重写 decode 方法，结合自定义的各种 Exception 类实现自定义的错误信息输出。
>
> 测试流程：在浏览器 Or Postman 中输入 ``localhost:8080/123`` 123 表示 {id} ，``TODO``：应该返回异常状态码和对应异常信息。

# 二、OpenFeign 学习文档

## 2.1、SpringCloud 介绍

### 2.1.1 微服务架构

> 微服务架构的提出者：马丁·福乐 Martin Fowler
>
> https://martinfowler.com/articles/microservices.html

> In short, the microservice architectural style is an approach to developing a single application as a suite of small services, each running in its own process and communicating with lightweight mechanisms, often an HTTP resource API. These services are built around business capabilities and independently deployable by fully automated deployment machinery. There is a bare minimum of centralized management of these services, which may be written in different programming languages and use different data storage technologies.
>
> 简而言之，微服务架构**样式**是一种将单个应用程序开发为一组**小服务**的方法，每个小服务都在自己的进程中运行并与轻量级机制（通常是HTTP资源API）进行通信。这些服务围绕业务功能构建，并且可以由全自动部署机制独立部署。这些服务的集中管理几乎没有，可以用不同的编程语言编写并使用不同的数据存储技术。
>
> 1、微服务架构知识一种样式风格
>
> 2、将一个完整的项目，拆分成多个模块去单独开发
>
> 3、每一个模块都是单独的运行在自己的容器中
>
> 4、每一个模块都需要相互通信，HTTP，RPC，MQ
>
> 5、每一个模块之间没有依赖关系，可以单独部署
>
> 6、可以使用多种语言开发不同模块
>
> 7、使用数据库、Redis、ES存储数据，也可以使用多个MySQL
>
> 总结：将复杂的单体应用进行细粒度的划分，每个拆分出来的服务各自打包部署。SpringCloud 是基于微服务思想的落地技术。

### 2.1.2 SpringCloud 介绍

> SpringCloud 是基于微服务架构落地的**一套技术栈**。
>
> SpringCloud 中的大多数技术都是基于 Netflix 公司的技术进行二次开发的。
>
> [SpringCloud 中文社区](http://springcloud.cn/)
>
> [SpringCloud 中文网](http://springcloud.cc/)
>
> 八个技术点
>
> 1. **Eureka** - 服务的注册与发现
> 2. **Ribbon** - 服务之间的负载均衡
> 3. **Feign** - 服务之间的通讯
> 4. **Hystrix** - 服务的线程隔离和断路器
> 5. **Zuul** - 服务网关
> 6. **Stream** - 实现MQ的使用
> 7. **Config** - 动态配置
> 8. **Sleuth** - 服务追踪

## 2.2、Eureka - 服务的注册与发现

### 2.2.1 为什么需要 Eureka

1、如果被调用方 IP 或者 port 发生改变，需要维护全部的调用方，成本高

![image-20200901005907202](https://static01.imgkr.com/temp/f2b0cd3fd49540398548f4f9960b2829.png)

2、搜索压力大，被调用方搭建集群，仍然需要维护全部调用方

![image-20200901010341363](https://static01.imgkr.com/temp/f7aa114358da4567aa8620c5c059238e.png)

Ereka 的作用：服务注册和发现。

**维护所有服务的信息，以便服务之间的相互调用。**

![image-20200901010852133](https://static01.imgkr.com/temp/d103071a230e460983ae0a2d81f99d1a.png)

Eureka 服务注册与发现步骤：

![image-20200902153916339](https://static01.imgkr.com/temp/c67cbb55cb7d4f479f032e004df5ccac.png)

### 2.2.2 Eureka 快速入门

#### 2.2.2.1 创建 Eureka Server

注： Springboot 和 SpringCloud 需要对应版本：

| Release Train | Boot Version                     |
| :------------ | :------------------------------- |
| Hoxton        | 2.2.x, 2.3.x (Starting with SR5) |
| Greenwich     | 2.1.x                            |
| Finchley      | 2.0.x                            |
| Edgware       | 1.5.x                            |
| Dalston       | 1.5.x                            |

父工程中指定版本号：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>Finchley.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    ...
</dependencies>
```

> 1、创建一个父工程，在父工程中指定SpringCloud的版本，将packaging修改为 pom
>
> **注意，一定要对应版本号！**

```xml
<packaging>pom</packaging>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>Finchley.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```



> 2、创建Eureka的server，创建Springboot的Maven工程，导入依赖，在启动类中添加注解，编写yml文件。
>
> 根据官网文档，添加依赖 
>
> https://docs.spring.io/spring-cloud-netflix/docs/2.2.5.RELEASE/reference/html/#spring-cloud-eureka-server

```
2.1 导入依赖：
 <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
```

```java
2.2 启动类中添加注解:
@SpringBootApplication
@EnableEurekaServer
public class EurekaApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication.class,args);
    }
}
```

```yml
2.3 编写yml配置文件：
server:
  port: 8761  # 端口号

eureka:
  instance:
    hostname: localhost
  client:
    #当前的Eureka服务是单机版的
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```

#### 2.2.2.2 创建Eureka Client

> 1、创建Maven工程，修改为Springboot

> 2、导入client依赖

```xml
 <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
```

> 3、在启动类上添加注解

```java
@SpringBootApplication
@EnableEurekaClient
public class CustomerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }
}

```



> 4、修改配置文件

```yml
# 指定 Eureka 服务地址
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka

# 指定服务的名称
spring:
  application:
    name: CUSTOMER

```

#### 2.2.2.3 测试Eureka

> 1、创建一个Search搜索模块，注册到Eureka

> 2、使用EurekaClient 对象去获取服务信息

```java
@Autowired
private EurekaClient eurekaClient;
```

> 3、正常的RestTemplate 调用即可

    @GetMapping("/customer")
    public String customer() {
        // 1 通过 eurekaClient 获取 SEARCH 服务的信息
        InstanceInfo info = eurekaClient.getNextServerFromEureka("SEARCH", false);
        // 2 获取访问地址
        String homePageUrl = info.getHomePageUrl();
        System.out.println(homePageUrl);
        // 3 通过 restTemplate 访问
        String result = restTemplate.getForObject(homePageUrl + "/search", String.class);
        // 4 返回结果
        return result;
    }

达成目标：

Customer 模块不需要知道 Search 模块的IP地址，而是直接向Eureka要服务的名称，就可以知道Search模块的IP和地址。

CustomerController完整代码

```java
package com.zittur.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CustomerController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EurekaClient eurekaClient;

    @GetMapping("/customer")
    public String customer() {
        // 1 通过 eurekaClient 获取 SEARCH 服务的信息
        InstanceInfo info = eurekaClient.getNextServerFromEureka("SEARCH", false);
        // 2 获取访问地址
        String homePageUrl = info.getHomePageUrl();
        System.out.println(homePageUrl);
        // 3 通过 restTemplate 访问
        String result = restTemplate.getForObject(homePageUrl + "/search", String.class);
        // 4 返回结果
        return result;
    }
}
```

SearchController

```java
package com.zittur.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {
    @GetMapping("/search")
    public String search(){
        return "search";
    }
}

```

### 2.2.3 Eureka 的安全性

以上方式中访问 Eureka Server 只需要知道IP 和端口号即可，不安全，希望增加安全校验模块。

**实现 Eureka 认证**

> 1、导入依赖

```java
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
```

> 2、编写配置类
>
> 注意一定不要少了注解@EnableWebSecurity

```java
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //忽略/eureka/**路径
        http.csrf().ignoringAntMatchers("/eureka/**");
        super.configure(http);
    }
}
```

> 3、编写配置文件

```yml
spring:
  security:
    user:
      name: root
      password: root
```

> 4、其他服务注册到Eureka需要添加用户名密码

```
# 指定 Eureka 服务地址
eureka:
  client:
    service-url:
      defaultZone: http://用户名:密码@localhost:8761/eureka
```

### 2.2.4 Eureka 的高可用

问题：如果程序正在运行，而Eureka突然宕机了，怎么办？

两种情况：

> 1、如果调用方已经访问过一次被调用方，那么Eureka 的宕机不会影响到功能的正常运行。
>
> 2、如果调用方没有访问过被调用方，那么Eureka的宕机会导致当前的功能不可用。

高可用的Eureka方案：准备多台Eureka搭建一个Eureka集群。

![image-20200901205438978](https://static01.imgkr.com/temp/37a5a2e054d1463ea1a9d9d54e62136c.png)

注意：两台Eureka应该要能够相互通信，以免造成数据不一致的问题

----

> 搭建Eureka高可用集群

> 1、准备多台Eureka

```
采用复制的方式，删除非必须文件，iml和target文件，修改pom.xml中的项目名称，在父工程中添加module
```

> 2、服务注册到多台Eureka上

修改服务的配置文件，让服务注册到多台Eureka上。

```yml
# 指定 Eureka 服务地址
eureka:
  client:
    service-url:
      defaultZone: http://root:root@localhost:8761/eureka,http://root:root@localhost:8762/eureka
```

注意，服务会选择一台Eureka进行注册，不会同时在多台Eureka上注册

> 3、让多台Eureka之间相互通信

```yml
eureka:
  instance:
    hostname: localhost
  client:
    #当前的Eureka服务是单机版的
    # registerWithEureka: false
    # fetchRegistry: false
    registerWithEureka: true  # 注册到Eureka上
    fetchRegistry: true       # 从Eureka上拉取信息，保证多台Eureka上的信息是同步的
    serviceUrl:
      # defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
      # 8761连接的是8762
      defaultZone: http://root:root@localhost:8762/eureka/
```

### 2.2.5 Eureka的细节

> 1、EurekaClient 启动时，会将自己的信息注册到 EurekaServer 上，EurekaServer 会存储 EurekaClient 的注册信息。

> 2、当 EurekaClient 调用服务时，如果本地没有注册信息的缓存时，回去 EurekaServer 中获取。

> 3、EurekaClient 会通过心跳机制和 EurekaServer 进行连接，从而使得当 EurekaClient 宕机之后，EurekaServer 能够发现，并将其在 EurekaServer 的注册信息中删除。（默认 30s 发送一次心跳请求到 EurekaServer，如果超过 90s 还没有发送心跳请求的话， EurekaServer 就认为这个 EurekaClient 宕机了，将其从注册表中移除。）

配置文件中的设置：

```yml
eureka:
  instance:
    lease-renewal-interval-in-seconds: 30
    lease-expiration-duration-in-seconds: 90
```

> 4、EurekaClient 每隔 30s 回到 EurekaServer 中更新本地的注册表信息

配置文件中的修改：更新注册表缓存时间

```yml
eureka:
  client:
    registry-fetch-interval-seconds: 30
```

> 5、Eureka 的自我保护机制：
>
> 统计 15min 之内，如果一个服务的心跳发送比例低于 85%，就会触发 EurekaServer 开启自我保护机制。
>
> 1. 不会到 EurekaServer 中移除长时间没有收到心跳请求的 EurekaClient。
> 2. EurekaServer 可以正常提供服务
> 3. 网络比较稳定时，EurekaServer 才会把自己的信息同步到其他节点上

开启 自我保护机制的配置：

```
eureka:
  server:
    enable-self-preservation: true
```

> 6、CAP 定理
>
> C - 一致性
>
> A - 可用性
>
> P - 分区容错性
>
> 在分布式环境下，只能满足三个中的两个，而且 P 是必须要满足的，只能在 C 和 A 之间只能满足其中一个。
>
> 1. 如果选择 CP，保证一致性，可能造成系统在一定的时间内是不可用的，如果同步数据的时间比较长，造成的损失会很大。
> 2. Eureka是满足AP模型的，即是一个高可用的集群，Eureka是无中心的，宕机不会影响集群的使用，不需要推举 master，但是会导致一定时间内数据时不一致的。 

## 2.3、Ribbon - 服务间的负载均衡

### 2.3.1 为什么需要 Ribbon

1、客户端请求服务器的负载均衡 - Nginx

2、**实现服务与服务之间的负载均衡 - Ribbon** 

两种负载均衡模式：

> 客户端负载均衡：Customer 客户模块将 3 个 Search 模块信息，全部拉到本地的缓存中，然后自己做负载均衡策略，选择其中的某一个服务。

![image-20200901231043192](https://static01.imgkr.com/temp/721e39219e344742be4f841676499713.png)

> 服务端负载均衡：在注册中心（比如 Eureka 中）根据指定的负载均衡策略，选择一个服务模块信息，并返回。

![image-20200901222232146](https://static01.imgkr.com/temp/669860153f744b45bdd16d32b230918d.png)

Ribbon 采用的是 **客户端负载均衡**：实现的是服务和服务之间的负载均衡

Dubbo 采用的是 服务端负载均衡

### 2.3.2 Ribbon 快速入门

> 1、启动两个 Search 模块

复制 Search 模块的 IDEA 配置，将端口改为 8082：

-Dserver.port=8082

> 2、在 Customer 模块导入 Ribbon 依赖

```xml
<!-- ribbon -->
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
</dependency>
```

> 3、配置整合 RestTemplate 和 Ribbon，通过添加注解@LoadBalanced的方式实现整合

```java
@Bean
@LoadBalanced
public RestTemplate restTemplate() {
	return new RestTemplate();
}
```

> 4、在 Customer 模块中直接访问 Search

```java
@GetMapping("/customer")
public String customer() {
	String result = restTemplate.getForObject("http://SEARCH/search", String.class);
	return result;
}
```

### 2.3.3 Ribbon 配置负载均衡策略

> 1、常用的负载均衡的策略
>
> 1. **RandomRule** 随机策略
> 2. **RoundRobinRule** 轮询策略
> 3. **WeightedResponseTimeRule** 默认会采用轮询策略，但是后续会根据服务的相应时间来自动分配权重
> 4. **BestAvailableRule** 根据被调用方并发数最小的原则分配

> 2、采用注解的形式

```java
@Bean
public IRule ribbonRule() {
	return new RandomRule();
}
```

> 3、根据配置文件去指定负载均衡策略（实现单个服务的负载均衡）

```
# Ribbon 负载均衡配置 指定特定服务的负载均衡策略
SEARCH:   # 编写服务名称
  ribbon:
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.WeightedResponseTimeRule  #指定负载均衡策略
```

默认是**轮询**的负载均衡策略 ``RoundRobinRule``

## 2.4、Feign - 服务之间的调用

### 2.4.1 为什么需要 Feign

Feign 实现**面向接口编程**，调用方 Autowired 之后可以直接调用被调用方的服务，简化开发。

### 2.4.2 Feign 快速入门

> 1、为调用方导入依赖，比如上面的 Customer 模块

```xml
<!-- Feign -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

> 2、添加注解

```
在Customer的启动类上加上@EnableFeignClients注解

@EnableFeignClients
```

> 3、在调用方创建接口，并且和 Search 服务模块做映射

```java
@FeignClient("SEARCH")  //指定服务名称
public interface SearchClient {
    // value -> 映射目标服务的请求路径 method -> 映射请求方式
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    String search();
}
```

> 4、在调用方测试使用

```java
@Autowired
private SearchClient searchClient;
@GetMapping("/customer")
public String customer() {
    String result = searchClient.search();
    return result;
}
```

### 2.4.3 Feign 参数传递

> 1、注意事项
>
> 1. 如果传递的参加比较复杂时，默认会采用POST请求方式
> 2. 传递单个参数时，推荐使用@PathVariable。FeignClient 传递多个参数时，可以采用@RequestParam ，注意不要省略value属性。
> 3. 传递对象信息时，统一采用json方式，添加@RequestBody
> 4. Client接口必须采用RequestMapping



> 1、在Search模块下新增三个接口（被调用方具体逻辑实现）

```java
@GetMapping("/search/{id}")
public Customer findById(@PathVariable Integer id) {
    return new Customer(1,"zhangsan",23);
}

@GetMapping("/getCustomer")
public Customer getCustomer(@RequestParam Integer id,
                            @RequestParam String name) {
    return new Customer(id,name,23);
}

@PostMapping("/save")
public Customer save(@RequestBody  Customer customer) {
    return customer;
}
```

> 2、封装Customer模块下的Controller（面向接口）

```java
@GetMapping("/search/{id}")
public Customer findById(@PathVariable Integer id) {
    return searchClient.findById(id);
}

@GetMapping("/getCustomer")
public Customer getCustomer(@RequestParam Integer id,
                            @RequestParam String name) {
    return searchClient.getCustomer(id,name);
}

@GetMapping("/save")
public Customer save(Customer customer) {
    return searchClient.save(customer);
}
```

> 3、封装Client接口（接口interface）

```java
@FeignClient("SEARCH") 
public interface SearchClient {

    @RequestMapping(value = "/search/{id}", method = RequestMethod.GET)
    Customer findById(@PathVariable Integer id);

    @RequestMapping(value = "/getCustomer", method = RequestMethod.GET)
    Customer getCustomer(@RequestParam(value = "id") Integer id,
                                @RequestParam(value = "name") String name);

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    Customer save(@RequestBody Customer customer);
}
```

> 4、测试

### 2.4.4 Feign 的 Fallback 服务降级

为什么要有 Fallback 服务降级机制？

当使用 Feign 去请求另外一个服务时，如果服务出现问题，挂掉了，那么应该服务降级机制可以暂时返回一个错误的数据，以免因为一个服务出现问题，导致全部失效。

具体实现步骤：

> 1、创建POJO类，实现Client接口

```java
@Component
public class SearchClientFallback implements SearchClient {
    @Override
    public String search() {
        return "出现问题啦！！！！！！";
    }

    @Override
    public Customer findById(Integer id) {
        return null;
    }

    @Override
    public Customer getCustomer(Integer id, String name) {
        return null;
    }

    @Override
    public Customer save(Customer customer) {
        return null;
    }
}
```

> 2、修改Client接口中的注解，添加一个fallback属性。

```java
@FeignClient(value = "SEARCH",fallback = SearchClientFallback.class)
```

> 3、添加配置文件

```yml
# feign 和 Hystrix 组件的整合
feign:
  hystrix:
    enabled: true
```

问题：调用方无法知道被调用方的具体错误信息是什么，需要通过 ``FallbackFactory`` 的方式实现。

> 1、基于 fallback 创建  FallbackFactory

> 2、创建一个POJO类，实现FallbackFactory<Client> 

```java
@Component
public class SearchClientFallbackFactory implements FallbackFactory<SearchClient> {
    @Autowired
    private SearchClientFallback searchClientFallback;

    @Override
    public SearchClient create(Throwable throwable) {
        throwable.printStackTrace();
        return searchClientFallback;
    }
}
```

> 3、修改Client中的属性 

```java
@FeignClient(value = "SEARCH",
        fallbackFactory = SearchClientFallbackFactory.class)
```



# 三、参考资料

## 3.1 文档材料

[Spring Cloud OpenFeign 官方文档](https://spring.io/projects/spring-cloud-openfeign)

[Spring Cloud Feign 设计原理](https://www.jianshu.com/p/8c7b92b4396c)

[feign服务端出异常客户端处理的方法](https://www.cnblogs.com/lori/p/11157394.html)

## 3.2 视频材料

[尚硅谷SpringCloud 2020新版视频 SpringCloud Hoxton+SpringCloud alibaba](https://www.bilibili.com/video/BV1rE411x7Hz?p=43)