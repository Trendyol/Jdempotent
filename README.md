# Jdempotent

<p align="center">
  <img src="examples/logo.jpg">
</p>

# Goal of this Jdempotent-spring-boot-starter

Make your listener or etc idempotent easily

# Usage

```xml
    <dependency>
        <groupId>com.trendyol</groupId>
        <artifactId>Jdempotent-spring-boot-redis-starter</artifactId>
        <version>1.0.0</version>
    </dependency>
```

You almost don't need anything, just add dependency and datasource configuration later have fun.
But if you want custom error case, you should implement `ErrorConditionalCallback` like a following example

```java
@Component
public class AspectConditionalCallback implements ErrorConditionalCallback {

    @Override
    public boolean onErrorCondition(Object response) {
        return response == IdempotentStateEnum.ERROR;
    }
    
    public RuntimeException onErrorCustomException() {
        return new RuntimeException("Status cannot be error");
    }

}
```

### Configuration

```yaml
jdempotent:
  enable: true
  cryptography:
    algorithm: MD5
  cache:
    redis:
      database: 1
      password: "password"
      sentinelHostList: 192.168.0.1,192.168.0.2,192.168.0.3
      sentinelPort: "26379"
      sentinelMasterName: "admin"
      expirationTimeHour: 2
      dialTimeoutSecond: 3
      readTimeoutSecond: 3
      writeTimeoutSecond: 3
      maxRetryCount: 3
      expireTimeoutHour: 3
```

### TODOS
- [ ] Write UT,IT
- [ ] Disable request&response config
- [ ] Write examples under the examples folders
- [ ] Support multiple request paylaod as a paramater
- [ ] Ignore a throwing custom exception like ErrorConditionalCallback
- [ ] Support multiple datasources
