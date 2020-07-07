# Spring boot custom redis
Spring boot redis custom cacheable 

```java
public @interface Cacheable {

    String cacheName() default "";

    String identifier() default "";

    String condition() default "";

    boolean eternal() default false;

    TimeUnit timeUnit() default TimeUnit.MINUTES;

    long ttl() default 1;

    Type type() default Type.STRING;
}
```
- Here by default type is ```Type.STRING```
- You can manually set to ```Type.HASHMAP``` if necessary
- default ```ttl``` is 1 with default with ```timeUnit``` ```TimeUnit.MINUTES```
- Your can set the desired ```TimeUnit``` manually to  ```TimeUnit.SECONDS```, ```TimeUnit.MILLISECONDS```
- ```eternal``` is ```false```, which means cache is not stored permanently
- ```condition```,```identifier```,```cacheName``` is "" (empty)

#### Syntax
```java
@Cacheable(identifier = <Hashname>, cacheName = <cacheName>, ttl = <Ttl time>, timeUnit = <timeUnit for ttl time>, type = <caching type>
```
#### Annotation for Storing as Group inside HASH (```Type.HASHMAP```)
```java
@Cacheable(identifier = KeyConstant.COMMENTS, cacheName = KeyConstant.POST + ".concat(#postId)", ttl = 5, type = Type.HASHMAP)
```
- identifier is necessary to include to determine the hashname over here
- set the type to ```java Type.HashMap ```
- Here the ttl is for the entire hash
- The ttl is updated every time anytime something is inserted in hash
#### Annotation for Storing as Individual Key Pair as Cache (```Type.STRING```)
```java
@Cacheable(cacheName = KeyConstant.COMMENTS_ALL, ttl = 5)
```

#### cacheName
- cacheName support expressions and normal string which is need to be surrounded with single quotes

```java
    @Cacheable(identifier = KeyConstant.COMMENTS, cacheName = KeyConstant.POST + ".concat(#postId)", ttl = 5, type = Type.HASHMAP, timeUnit = TimeUnit.SECONDS)
    List<Comment> findByPostId(Long postId);
```

```java
    @Cacheable(identifier = KeyConstant.COMMENTS, cacheName = KeyConstant.POST + ".concat(#root.methodName)", ttl = 5, type = Type.HASHMAP, timeUnit = TimeUnit.SECONDS)
    List<Comment> findByPostId(Long postId);
```

```java
    cacheName = "'POST'" // plain string only
    cacheName = KeyConstant.POST // stored in string
    cacheName = "#root.args[0]"
    cacheName = "#root.targetClass"
    cacheName = "#root.target"
    cacheName = "#root.method.getName()" // Use method object
    cacheName = "#root.methodName"
```
#### Serializer
```java
    
    public RedisSerializer JdkSerializationRedisSerializer() {
        JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
        return jdkSerializationRedisSerializer;
    }

    @Bean
    public RedisSerializer fastJsonRedisSerializer() {
        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer(Object.class);
        return fastJsonRedisSerializer;
    }

```

- There are Two serializer use the fastJson to store as JSON and use jdk to store as bytes
- set One as ```@Bean``` to use the serializer


