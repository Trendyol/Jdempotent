# jidempotent-spring-boot-starter

````
   ___     _                            _             _   
  |_  |   | |                          | |           | |  
    | | __| | ___ _ __ ___  _ __   ___ | |_ ___ _ __ | |_ 
    | |/ _` |/ _ \ '_ ` _ \| '_ \ / _ \| __/ _ \ '_ \| __|
/\__/ / (_| |  __/ | | | | | |_) | (_) | ||  __/ | | | |_ 
\____/ \__,_|\___|_| |_| |_| .__/ \___/ \__\___|_| |_|\__|
                           | |                            
                           |_|                            
````

# Goal of this jidempotent-spring-boot-starter

Make your listener or etc idempotent easily

# Usage
new dependecy

```xml
    <dependency>
        <groupId>com.trendyol</groupId>
        <artifactId>Jdempotent-spring-boot-redis-starter</artifactId>
        <version>0.8.3</version>
    </dependency>
```

old dependecy:
```xml
    <dependency>
       <groupId>com.trendyol.jidempotent</groupId>
       <artifactId>jidempotent-spring-boot-starter</artifactId>
       <version>0.0.17-SNAPSHOT</version>
    </dependency>
```

You dont need anything, just add dependecy and have fun.
But if you want custom error case you should implement `ErrorConditionalCallback` 


```java
@Component
public class AspectConditionalCallback implements ErrorConditionalCallback {

    @Override
    public boolean onErrorCondition(Object response) {
        return response == IdempotentStateEnum.ERROR;
    }
    
    public RuntimeException onErrorCustomException() {
        return new RuntimeException("SOME MESSAGES....");
    }

}
```
config example
```
jdempotent:
  enable: true
  cryptography:
    algorithm: MD5
  cache:
    redis:
      database: 1
      password: "nEx-ya5-sso-ecomm"
      sentinelHostList: 10.250.217.172,10.250.217.173,10.250.217.174
      sentinelPort: "26379"
      sentinelMasterName: "ecomcore"
      expirationTimeHour: 2
      dialTimeoutSecond: 3
      readTimeoutSecond: 3
      writeTimeoutSecond: 3
      maxRetryCount: 3
      expireTimeoutHour: 3
```

#shortcut implementation

you can replace 
`@KafkaListener` to `@IdempotentKafkaListener`
`@RabbitListener` to `@IdempotentRabbitListener`

thats all. you dont need any changes.

```java
@IdempotentResource
@KafkaListener
public @interface IdempotentKafkaListener {
    @AliasFor(annotation = KafkaListener.class, attribute = "id")
    String id() default "";

    @AliasFor(annotation = KafkaListener.class, attribute = "containerFactory")
    String containerFactory() default "";

    @AliasFor(annotation = KafkaListener.class, attribute = "topics")
    String[] topics() default {};

    @AliasFor(annotation = KafkaListener.class, attribute = "groupId")
    String groupId() default "";

}
```

```java
@IdempotentResource
@RabbitListener
public @interface IdempotentRabbitListener {
    @AliasFor(annotation = RabbitListener.class, attribute = "queues")
    String[] queues() default {};
}
```
TODOS
<ol>
<li>ci pipeline</li>
<li>Consumer projelerinde exceptional caselerin çıkarılması jira : https://jtracker.trendyol.com/browse/CUS-1445</li>
<li>UT,IT testlerinin yazılması</li>
<li>disable request&response config</li>
<li>algoritma tipinin configden okunması</li>
<li>java doc update edilmesi</li>
<li>readme update edilmesi</li>
<li>examples altına örnek proje yapılması</li>
<li>Jdempotent-spring-boot-redis-starter yazılması</li>
<li>yük testi</li>
</ol>