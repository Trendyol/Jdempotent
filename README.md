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
