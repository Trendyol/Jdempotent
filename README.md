# [![pipeline status](https://gitlab.trendyol.com/customer-service/cs/libs/jdempotent/badges/master/pipeline.svg)](https://gitlab.trendyol.com/customer-service/cs/libs/jdempotent/-/commits/master)  [![coverage report](https://gitlab.trendyol.com/customer-service/cs/libs/jdempotent/badges/master/coverage.svg)](https://gitlab.trendyol.com/customer-service/cs/libs/jdempotent/-/commits/master) Jdempotent 

<p align="center">
  <img src="examples/logo.jpg">
</p>


# Goal of this jidempotent-spring-boot-starter

Make your listener or etc idempotent easily

# Usage

```xml
    <dependency>
        <groupId>com.trendyol</groupId>
        <artifactId>Jdempotent-spring-boot-redis-starter</artifactId>
        <version>0.8.4</version>
    </dependency>
```

```shell script
  mvn -U clean install -s settings.xml -DskipTests=true 
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

### TODOS
- [ ] Write UT,IT
- [ ] Disable request&response config
- [ ] Update Java docs
- [ ] Update Readme
- [ ] Write examples under the examples folders
- [ ] Support multiple request paylaod as a paramater
- [ ] Ignore a throwing custom exception like ErrorConditionalCallback