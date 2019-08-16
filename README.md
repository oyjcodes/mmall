# 网上商城

## 软件架构
<table>
<thead>
<tr>
<th>项目版本号</th>
<th>技术架构</th>
</tr>
</thead>
<tbody>

<tr>
<td>一期项目</td>
<td>Spring + SpringMVC + Mybatis + Guava Cache</td>
</tr>
<tr>
<td>二期项目</td>
<td>Spring + SpringMVC + Mybatis + Nginx + vsftp + Redis + Jedis + Lombok + Jackson + Spring Schedule</td>
</tr>

</tbody>
</table>



**系统描述：** 本项目是基于 SSM 框架开发的前后端分离电商网站，数据库采用的是 MySQL。包含用户管理，订单，品类，产品，购物车，地址，在线支付七个模块。项目还融合了 Tomcat 集群，Nginx 负载均衡，Redis 缓存分布式，Redis 分布式锁，单点登录等技术。

### 技术点
1.  前后端分离
2.  搭建Tomcat+Nginx集群环境
3.  搭建Redis分布式环境
4.  Redis+Cookie+Jackson+Filter原生的方式解决集群session共享的问题
5.  项目集成Redis客户端jedis
6.  jackson封装JsonUtil
7.  Cookie封装
8.  SessionExpireFilter重置session有效期
9.  封装Shard Redis API
10. Spring Session框架集成零侵入实现单点登录
11. SpringMVC 全局异常处理
12. SpringMVC 拦截器实现权限统一校验
13. 使用部分RESTful接口
14. 利用蚂蚁金服开放平台沙箱集成支付宝完成真实支付
15. Spring Schedule实现定时关单

## 项目架构图

<img src="./img/jiagou.png">



## 项目功能接口图

<img src="./img/接口清单.png">

## 登录购物演示

![](./img/buy.gif)

## 支付演示

![](./img/pay.gif)


本项目对接了支付宝的'当面付'，即'扫码支付'，通过订单号生成付款二维码，上传FTP服务器，展示给客户扫码成功付款后（通过支付宝沙箱环境模拟支付），收到支付宝回调，商家验证签名正确性，然后进行后续操作

[支付宝官方文档](https://doc.open.alipay.com/docs/doc.htm?spm=a219a.7629140.0.0.Q4tRmQ&treeId=193&articleId=105072&docType=1)<br>
[支付宝沙箱](https://openhome.alipay.com/platform/appDaily.htm?tab=info)<br>

## 业务流程

![](./img/payprocess.png)

## 调用流程

![流程图](./img/payprocess2.jpg)

1. 商户系统调用支付宝预下单接口`alipay.trade.precreate`，获得该订单二维码图片地址。

2. 发起轮询获得支付结果：等待`5`秒后调用交易查询接口`alipay.trade.query`通过支付时传入的商户订单号(`out_trade_no`)查询支付结果（返回参数`TRADE_STATUS`），如果仍然返回等待用户付款（`WAIT_BUYER_PAY`），则再次等待5秒后继续查询，直到返回确切的支付结果（成功`TRADE_SUCCESS` 或 已撤销关闭`TRADE_CLOSED`），或是超出轮询时间。在最后一次查询仍然返回等待用户付款的情况下，必须立即调用交易撤销接口`alipay.trade.cancel`将这笔交易撤销，避免用户继续支付。

3. 除了主动轮询，也可以通过接受异步通知获得支付结果，详见扫码异步通知，注意一定要对异步通知做验签，确保通知是支付宝发出的。

## Nginx 配置
- 在nginx配置的 `conf` 文件夹下创建 `vhost` 文件 并在 `conf` 下的 `nginx.conf` 文件中加入 `include vhost/*.conf;`

- nginx.conf文件加载vhost文件夹下面的不同配置文件，这样便于分类配置管理，无需全部写在nginx.conf文件中，导致配置的混乱，不便于维护

### Nginx实现静态资源映射

- vhost下添加image.jiekk.com.conf配置文件

- 验证 `nginx` 的配置文件是否生效 `nginx.exe -t `

- 启动 Nginx `nginx.exe`

- ```
  server {
      listen 80; //监听 80 端口
      autoindex on;
      server_name image.jiekk.com; //当访问这个域名时
      access_log d:/access.log combined;
      index index.html index.htm index.jsp index.php;
      #error_page 404 /404. html;
      if ($query_string ~* ".*[\;'\<\>].*") {
          return 404;
      }
      location ~ /(mmall_fe|mmall_admin_fe)/dist/view/* {
                  deny all;
          }
          location / {
                  root D:\coder\ftpfile\img;
                  add_header Access-Control-Allow-Origin *;
          }
  }
  ```

- 访问 `image.jiekk.com` 时转发到路径 `root D:\coder\ftpfile\img;` 从而实现路径转发

- 修改配置文件后要重启 `nginx` : `nginx.exe -s reload`

- 修改 Window 域名(本地开发，避免走外网的域名解析)，`C:\Windows\System32\drivers\etc\hosts`



### Nginx 负载均衡 http 转发

```
upstream www.happymmall.com{
	 server www.jiekk.com:8080 weight=1;
	 server www.jiekk.com:9080 weight=1;
	 #server 127.0.0.1:8080;
	 #server 127.0.0.1:9080;
}
server {
    listen 80;
    autoindex on;
    server_name www.jiekk.com jiekk.com	;
    access_log d:/access.log combined;
    index index.html index.htm index.jsp index.php;
    #error_page 404 /404. html;
    if ($query_string ~* ".*[\;'\<\>].*") {
        return 404;
    }
    
    location / {
	proxy_pass http://www.jiekk.com;
	add_header Access-Control-Allow-Origin *;
    }
}
```

讲解：当访问 ` www.jiekk.com 或 jiekk.com` 时，会转到`http://www.jiekk.com;` ，然后根据负载均衡到 `upstream www.jiekk.com`(这里我配置了本机的 hosts 文件 当访问 www.jiekk.com 会转发到 127.0.0.1)

### 错误总结

- 路径名要使用英文，否则会报错 500

## 缓存比较

### Guava Cache
- Guava Cache是一个本地缓存，它是线程安全的缓存，与ConcurrentMap相似，但前者增加了更多的元素失效策略，通常都设定为自动回收元素,后者只能显示的移除元素

- GuavaCache提供了三种基本的缓存回收方式：基于容量回收、定时回收和基于引用回收。定时回收有两种：按照写入时间，最早写入的最先回收；按照访问时间，最早访问的最早回收

- 它可以监控加载/命中情况

- Guava Cache是单个应用运行时的本地缓存。它不把数据存放到文件或外部服务器。

注：如果你不需要Cache中的特性，使用ConcurrentHashMap有更好的内存效率——但Cache的大多数特性都很难基于旧有的ConcurrentMap复制，甚至根本不可能做到。

### Guava Cache缓存回收 
- 基于容量的回收（size-based eviction）

 如果要规定缓存项的数目不超过固定值，只需使用CacheBuilder.maximumSize(long)。缓存将尝试回收最近没有使用或总体上很少使用的缓存项。——警告：在缓存项的数目达到限定值之前，缓存就可能进行回收操作——通常来说，这种情况发生在缓存项的数目逼近限定值时。

另外，不同的缓存项有不同的“权重”（weights）——例如，如果你的缓存值，占据完全不同的内存空间，你可以使用CacheBuilder.weigher(Weigher)指定一个权重函数，并且用CacheBuilder.maximumWeight(long)指定最大总重。在权重限定场景中，除了要注意回收也是在重量逼近限定值时就进行了，还要知道重量是在缓存创建时计算的，因此要考虑重量计算的复杂度。

- 定时回收（Timed Eviction）

CacheBuilder提供两种定时回收的方法：

expireAfterAccess(long, TimeUnit)：缓存项在给定时间内没有被读/写访问，则回收。请注意这种缓存的回收顺序和基于大小回收一样。
expireAfterWrite(long, TimeUnit)：缓存项在给定时间内没有被写访问（创建或覆盖），则回收。如果认为缓存数据总是在固定时候后变得陈旧不可用，这种回收方式是可取的。

- 基于引用的回收（Reference-based Eviction）

通过使用弱引用的键、或弱引用的值、或软引用的值，Guava Cache可以把缓存设置为允许垃圾回收：

CacheBuilder.weakKeys()：使用弱引用存储键。当键没有其它（强或软）引用时，缓存项可以被垃圾回收。因为垃圾回收仅依赖恒等式（==），使用弱引用键的缓存用==而不是equals比较键。
CacheBuilder.weakValues()：使用弱引用存储值。当值没有其它（强或软）引用时，缓存项可以被垃圾回收。因为垃圾回收仅依赖恒等式（==），使用弱引用值的缓存用==而不是equals比较值。
CacheBuilder.softValues()：使用软引用存储值。软引用只有在响应内存需要时，才按照全局最近最少使用的顺序回收。考虑到使用软引用的性能影响，我们通常建议使用更有性能预测性的缓存大小限定（见上文，基于容量回收）。使用软引用值的缓存同样用==而不是equals比较值。


### Redis
redis是一个key-value存储系统。和Memcached类似，它支持存储的value类型相对更多，包括string(字符串)、list(链表)、set(集合)、zset(sorted set --有序集合)和hash（哈希类型）；

Redis与MemmCached的区别：redis会周期性的把更新的数据写入磁盘或者把修改操作写入追加的记录文件，并且在此基础上实现了master-slave(主从)同步

Redis是一个分布式缓存系统，具有内存缓存和持久化双重功能，有利于缓存集群的扩展

### Redis持久化策略

RDB（数据快照模式）
定期存储，保存的是数据本身，存储文件是紧凑的，RDB定时备份内存中的数据集。服务器启动的时候，可以从 RDB 文件中恢复数据集。

- 优点

1. 存储的文件是紧凑的
2. 适合用于备份，方便恢复不同版本的数据
3. 适合于容灾恢复，备份文件可以在其他服务器恢复
4. 最大化了Redis的性能，备份的时候启动的是子线程，父进程不需要执行IO操作
5. 数据保存比AOF要快

- 缺点

1. 如果Redis因为没有正确关闭而停止工作是，到上个保存点之间的数据将会丢失
2. 由于需要经常fork子线程来进行备份操作，如果数据量很大的话，fork比较耗时，如果cpu性能不够，服务器可能是卡顿。属于数据量大的时候，一个服务器不要部署多个Redis服务。

AOF（追加模式）
每次修改数据时，同步到硬盘(写操作日志)，保存的是数据的变更记录

- 优点 
1. 使用AOF模式更加的灵活，因为可以有不同的fsync策略 
2. AOF是一个日志追加文件，所有不需要定位，就算断电也没有损坏问题，哪怕文件末尾是一个写到一半的命令，redus-check-aof工具也可以很轻易的修复 
3. 当AOF文件很大的，Redis会自动在后台进行重写。重写是决对安全的，因为Redis是继续往旧的文件里面追加，使用创建当前数据集所需的最小操作集合来创建一个全新的文件，一旦创建完成，Redis就会切换到新文件，开始往新文件进行追加操作 
4. AOF包含一个又一个的操作命令，易于理解和解析

- 缺点

1. 对于同样的数据集，AOF文件通常要大于RDB文件
2. AOF可能比RDB要慢，这取决于fsync策略。通常fsync设置为每秒一次的话性能仍然很高，如果关闭sfync，即使在很高的负载下也和RDB一样快。不过，即使在很大的写负载情况下，RDB还是能提供很好的最大延迟保证
3. AOF通过递增的方式更新数据，而RDB快照是从头开始创建，RDB会更健壮和稳定（所以适用于备份）



注意：如果只希望数据保存在内存中的话，俩种策略都可以关闭，也可以同时开启俩种策略，当Redis重启时，AOF文件会用于重建原始数据




## 一期项目使用 Guava Cache 实现重置密码接口

Guava Cache的实现
```java
public class TokenCache {

    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    public static final String TOKEN_PREFIX = "token_";

    //LRU算法
    private static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                //默认的数据加载实现,当调用get取值的时候,如果key没有对应的值,就调用这个方法进行加载.
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });

    public static void setKey(String key,String value){
        localCache.put(key,value);
    }

    public static String getKey(String key){
        String value = null;
        try {
            value = localCache.get(key);
            if("null".equals(value)){
                return null;
            }
            return value;
        }catch (Exception e){
            logger.error("localCache get error",e);
        }
        return null;
    }
}
```
回答问题，客户端获取token

```java
 public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount>0){
            //说明问题及问题答案是这个用户的,并且是正确的，cache中存储该用户对应的forgetToken，并将这个forgetToken返回给客户端
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }

```
客户端携带token去后台修改密码

```java
 public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误,token需要传递");
        }
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        //从cache中获取该用户的token
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);

        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }
        //用户携带的token值与服务器中cache缓存中存储的值相等的话，则修改密码
        if(StringUtils.equals(forgetToken,token)){
            String md5Password  = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);

            if(rowCount > 0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else{
            return ServerResponse.createByErrorMessage("token错误,请重新获取重置密码的token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }
```

## 二期项目Tomcat集群下的架构演进

Tomcat集群能带来什么？

提高服务的性能，例如计算处理能力、并发能力等，以及实现服务的高可用性
提供项目架构的横向扩展能力，增加集群中的机器就能提高集群的性能

Tomcat集群最佳方式？

Tomcat集群的实现方式有多种，最简单的就是通过Nginx负载进行请求转发来实现

”想当然“ 的觉得Tomcat集群架构是这样子的(error)

![流程图](./img/error1.jpg)

这种 ”想当然“ 的Tomcat集群会带来什么问题？？？

1. Session登录信息存储及读取的问题
2. 服务器定时任务并发的问题
3. ......

case1:试想一下问题1这个场景，A用户通过tomcat1服务器进行登陆操作，然后进行下单，下单的业务逻辑是，必须要求登陆的用户才有权限进行下单的操作，这个时候如果下单的请求刚好呗tomcat2服务器所接受，但是tomcat2服务器上的session会话没有A用户的登陆信息，此时界面会跳转到登陆页面，强制要求A用户进行登陆。可以看到这对用户是多么差的购物体验，对于A用户来说明明刚刚登陆了，我现在要下单，你却说我没有登陆，又让我登陆，对于网站而言，本想通过部署多台服务器集群来提高网站的并发能力和吞吐量，但对用户造成了极差的购物体验，用户压根不会关心网站的架构是如何实现的。

case2:试想一下问题2这个场景，当用户进行下单操作，但在规定的付款时间内没有完成订单的支付操作，则这样的订单就是无效的订单，系统就需要将这些订单包含的各个商品的数量更新回商品的库存，管理员进行逐个的商品库存修改是不可能完成的，于是就需要为购物系统配置一个定时关单的任务，如果在集群环境下，每个服务器上的定时关单任务都运行，在某些复杂的情况下，会造成关单失败，并且不好发现问题。


所以架构的演进并不是 ”想当然“ 的那么简单，当我们的架构随着业务的需求进行演进时，就可能会发生代码上的改动，以及其他各方面配置及机器的改动，并不是单纯的增加Tomcat机器就行了。因为架构的演进都不是一蹴而就的，编程是一个遇见问题解决问题的过程，所以我们不可能一下子就设计出一个完美的架构，而且也不存在完美的架构，只有合适的架构。

### 常见的Tomcat集群解决方案

1. 采用 nginx 中的 ip hash 解决后端session不共享问题，来保持某个ip始终连接在某一个机器上（hash(ip) % 集群数量）,这样同一个客户端只要在网络不变的情况下，总是会访问同一台服务器

- 优点 
  可以不改变现有的技术架构，直接实现横向扩展（省事）。但是缺陷也很明显，在实际的生产环境中，极少使用这种方式
- 缺点
  服务器请求（负载）不均衡，这是完全依赖 ip hash 的结果，如果多个用户ip hash后都请求同一台服务器，则其它的服务器就因为闲置，而造成负载不均衡，造成资源的浪费

  客户机ip动态变化频繁的情况下，无法进行服务，因为可能每次的ip hash都不一样，就无法始终保持只连接在同一台机器上。

2. 采用redis或memcache等nosql数据库，实现一个缓存session的服务器，当请求过来的时候，所有的Tomcat Server都统一往这个服务器里读取session信息。这是企业中比较常用的一种解决方案



### Redis分布式


## 分布式算法

### 传统分布式算法：效率低下

原始的做法是对缓存项的键进行哈希，将hash后的结果对缓存服务器的数量进行取模操作，通过取模后的结果，决定缓存项将会缓存在哪一台服务器上。例如一个图片选择存哪台服务器的一个过程，redis集群节点有0，1，2；hash(test.jpg) % 3 == 0/1/2
则该图片会根据取模后的值存在的redis集群的0，1，2中的某一个节点上。

<img src="./img/传统分布式算法.jpg">

这样Hash取模的方式是有弊端的，就是在我们业务扩展的时候，新增服务器节点，会导致一部分数据不能准确的在缓存服务器中找到。换句话说，当服务器数量发生变化的时候，所有缓存在一点时间内是失效的，当应用无法从缓存中获取数据时，则会向后端服务器请求数据，造成了缓存的雪崩，整个系统很有可能被压垮，所以，我们应该想办法不让这种糟糕的情况出现，但是由于Hash算法本身的缘故，使用取模法进行缓存时，这种情况是无法避免的，为了解决这些问题而出现一致性哈希算法诞生。

假设现在的redis分布式有4个节点，20个数据 hash 取模后值为0-20，在redis节点中的存储分布如下
<img src="./img/redis4.jpg">

### Redis 分布式一致性哈希算法：32位 圆环，从 0 开始

<img src="./img/Redis分布式算法1.png">

<img src="./img/Redis分布式算法2.png">

<img src="./img/Redis分布式算法3.png">



将 key 存到顺时针方向最近的 Cache 上，当 Cache 移除或者增加，只会影响到 Cache 到上一个 Cache 的方位的 key，并不会向传统的 hash，牵一发而动全身（导致大量缓存不命中造成缓存穿透从而给数据库增大压力）。



#### 一致性 hash 存在的问题：Hash 倾斜性

<img src="./img/Redis分布式算法4.png">

<img src="./img/Redis分布式算法5.png">



### 封装分布式Shard(分片) Redis API
```java
public class RedisShardedPool {
    //sharded jedis连接池
    private static ShardedJedisPool pool;
     //最大连接数
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));
    //在jedispool中最大的idle状态(空闲的)的jedis实例的个数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","20"));
    //在jedispool中最小的idle状态(空闲的)的jedis实例的个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","20"));
    //在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值true。则得到的jedis实例肯定是可以用的。
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true"));
    //在return一个jedis实例的时候，是否要进行验证操作，如果赋值true。则放回jedispool的jedis实例肯定是可以用的
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","true"));

    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));
    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));




    private static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        config.setBlockWhenExhausted(true);//连接耗尽的时候，是否阻塞，false会抛出异常，true阻塞直到超时。默认为true。

        JedisShardInfo info1 = new JedisShardInfo(redis1Ip,redis1Port,1000*2);

        JedisShardInfo info2 = new JedisShardInfo(redis2Ip,redis2Port,1000*2);

        List<JedisShardInfo> jedisShardInfoList = new ArrayList<JedisShardInfo>(2);

        jedisShardInfoList.add(info1);
        jedisShardInfoList.add(info2);

        pool = new ShardedJedisPool(config,jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    static{
        initPool();
    }

    public static ShardedJedis getJedis(){
        return pool.getResource();
    }


    public static void returnBrokenResource(ShardedJedis jedis){
        pool.returnBrokenResource(jedis);
    }



    public static void returnResource(ShardedJedis jedis){
        pool.returnResource(jedis);
    }
```


### 单点登陆SSO（Single Sign On）的实现


- 采用Redis+Cookie+Jackson+Filter原生实现单点登陆，前提是实现session共享

实现session共享前，访问tomcat1和tomcat2上的服务，浏览器客户端对应的【Cookie: JSESSIONID=*****】值是不一样的，而且随着服务器的重启或者刷新请求，JSESSIONID的值是会更新的。
我们需要在服务端创建cookie，保存这个JSESSIONID的值，并将这个cookie返回给客户端。后续访问网站就是使用这个cookie中的value值，去redis中去查询匹配是否存在这样的值，如果存在这个用户是否已经登陆。

![流程图](./img/单点登录1.png)

单点登陆接口

```java
 
public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse) {
    ServerResponse<User> response = iUserService.login(username, password);
    if (response.isSuccess()) {
        //登陆成功，将sessionId存储在Cookie中，写回客户端
        CookieUtil.writeLoginToken(httpServletResponse,session.getId());
        //sessionId:  8FF6EDA78AB9858725B1983298610F8E
        //将sessionId作为key ，user的序列化查询结果为 value 存储在 Redis 中，{key:sessionId value:user} 过期时间REDIS_SESSION_EXTIME：30分钟
        RedisShardedPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
    }
    return response;
}
```


```Java
    //新建一个 Cookie ，new Cookie(COOKIE_NAME, token) ，token为sessionid
    public static void writeLoginToken(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(COOKIE_NAME, token); // COOKIE_NAME = mmall_login_token
        cookie.setDomain(COOKIE_DOMAIN); // www.happymmall.com
        cookie.setPath("/"); //代表设置在根目录，即 www.happymmall.com/
        //这样前端无法操作cookie，就无法获取cookie进行恶意的攻击
        cookie.setHttpOnly(true);
        //单位是秒。
        //如果这个maxage不设置的话，cookie就不会写入硬盘，而是写在内存。只在当前页面有效。
        cookie.setMaxAge(60 * 60 * 24 * 365); //-1代表永久
        log.info("write cookieName:{}, coo  kieValue:{}", cookie.getName(), cookie.getValue());
        response.addCookie(cookie);
    }
```



### 解决 SessionId 在多个 Tomcat 不一致问题：使用 Cookie 保存 SessionId

`private final static String COOKIE_DOMAIN = "www.happymmall.com";` 

将 Cookie 写在二级域名 happymmall.com 下，即 三级域名 xxx.happymmall.com 都能访问到这二级域名 Cookie。(以后做微服务可以把用户模块单独设置一个域，user.happymmall.com)

然后通过 www.happymmall.com 来访问


![流程图](./img/单点登录2.png)


## 解决用户 session 过期问题（SessionExpirefilter）


**问题描述：** redis中的session是设置有过期时间的（比如30分钟），所以在后续访问系统页面时，要将redis中的session过期时间重新设置为30分钟，否者，只能玩30分钟，后续访问系统页面时，会由于redis中session过期，而不能访问。如果用户30分钟一直没有访问页面，session失效时合乎情理的，但是如果用户两次操作间隔小于30分钟，即使用比较频繁的情况下，30分钟后就要从新登陆，这样用户体验就不好了。

**解决方案：** 使用SessionExpireFilter过滤器重置session有效期。过滤所有*.do结尾的访问请求，重新设置session缓存时间，这样每次访问新的页面时，session的有效期将会从新更新为30分钟，保持用户会话的次序有效性。


```java
public class SessionExpireFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;

        String loginToken = CookieUtil.readLoginToken(httpServletRequest);

        if(StringUtils.isNotEmpty(loginToken)){
            //判断logintoken是否为空或者""；
            //如果不为空的话，符合条件，继续拿user信息
            
            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
            User user = JsonUtil.string2Obj(userJsonStr,User.class);
            if(user != null){
                //如果user不为空，则重置session的时间，即调用expire命令
                RedisShardedPoolUtil.expire(loginToken, Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}

```

web.xml

```
<!-- 二期新增重置session时间的filter-->
    <filter>
        <filter-name>sessionExpireFilter</filter-name>
        <filter-class>com.mmall.controller.common.SessionExpireFilter</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>sessionExpireFilter</filter-name>
        <url-pattern>*.do</url-pattern>
    </filter-mapping>

```

### 测试
先访问 login.do ，然后 ttl 查看剩余时间，然后随便访问一个 .do 请求（因为拦截的是所有 .do 请求，然后重置时间），再 ttl 查看，发现重置时间了，成功~！

<img src="./img/session过期.png">


## 集群环境下Guava Cache 迁移到 redis实现重置密码接口

### 场景
为了在用户未登录的情况下可以通过回答问题修改密码，在回答问题正确之后，后台生成Token保存到服务器，并且返回到前端，用户携带Token作为用户的标志去请求后台修改密码。

### 问题
一期项目使用的是Guava cache保存Token，如果第一次回答问题的请求在A服务器上，客户端拿到Token，此时客户端携带token去请求后台服务器修改密码，但此时连接到B服务器上，而B服务器上的Guava Cache上没有这个Token,所以密码回修改失败，这是由tomcat集群演进所产生的问题。

### 解决方法
回答问题答案正确时将token保存到redis中,修改密码的时候从redis中读取，和前端传来的token进行匹配，如果匹配成功则修改密码，否者不修改。






