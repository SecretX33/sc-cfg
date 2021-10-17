# sc-cfg
 
SC-CFG is a simple, yet powerful library that automatically generate configuration files based on your classes. Compatible with Java 8+ and Kotlin, anyone can use it and start enjoying the commodities right now.

You won't have to worry about reloading your configuration anymore, SC-CFG handles everything for you, automatically.

## Usage

Using SC-CFG is as simple as annotating your config class with `@Configuration`. Both public and private fields are supported.

```java
import com.github.secretx33.sccfg.api.annotation.Configuration;

// you just have to annotate the class with it
@Configuration
public class MyConfig {
    public int someValue = 0;
    private String someString = "rock";
}
```

Then getting the singleton instance of the class through the `Config` class.

```java
public class MyPlugin {
    
    @Override
    public void onEnable() {
        // get the instance of your config
        MyConfig config = Config.getConfig(MyConfig.class);
    }
}
```

Which is serialized to `MyConfig.yml`, automatically.

```yaml
someValue: 0
someString: rock
```

## Features

Save you config with a single method call.

```java
public class MyPlugin {

    @Override
    public void onEnable() {
        MyConfig config = Config.getConfig(MyConfig.class);
        
        // save your config by using
        Config.saveConfig(config);
        // or
        Config.saveConfig(MyConfig.class);
    }
}
```

The default file type for configurations is `YAML`, but we do support multiple file type like `HOCON` and `JSON`, to switch between then is as easy as changing one option on your `@Configuration` annotation.

```java
import com.github.secretx33.sccfg.api.annotation.Configuration;

// switch your config to hocon
@Configuration(type = FileType.HOCON)
public class MyConfig {
    // ...
}
```

Obs.: For `HOCON` you'll also have to include `configurate-hocon` dependency on your `pom.xml`/`build.gradle` file.

```gradle
implementation("org.spongepowered:configurate-hocon:4.1.2")
```

Change the file name and path through your `@Configuration` annotation.

```java
import com.github.secretx33.sccfg.api.annotation.Configuration;

// all of those annotations are valid options

// @Configuration("myconfig")
// @Configuration("myconfig.yml")
// @Configuration("folder/anotherfolder/myconfig.yml")
public class MyConfig {
    // ...
}
```

Run methods before or after the config is reloaded, all you have to do it annotate the method with the respective annotation. Both public and private **no args** methods are supported. 

By default, these methods run on the main thread, but you can specify `async` on the annotation parameter to run the method on another thread (ForkJoinPool).

```java
import com.github.secretx33.sccfg.api.annotation.*;

@Configuration
public class MyConfig {
    
    @BeforeReload
    private void doBefore() {
        // doing something before config is reloaded
    }

    @BeforeReload(async = true)
    private void doBeforeAsync() {
        // doing something before config is reloaded, but async
    }
    
    @AfterReload
    public void doAfter() {
        // doing something after config is reloaded
    }

    @AfterReload(async = true)
    private void doAfterAsync() {
        // doing another thing after config is reloaded, but async
    }
}
```

Skip field serialization by using Java `transient`
keyword, or by using our very own annotation `@IgnoreField`.

```java
import com.github.secretx33.sccfg.api.annotation.IgnoreField;

public class MyConfig {
    // skip transient fields
    public transient int someIgnoredInt = 0;
    
    // skip fields annotated with @IgnoreField too
    @IgnoreField
    private String someIgnoredString = "rock";
}
```

Choose a different name scheme for your configuration fields by modifying your `@Configuration` annotation, currently we do have five options:

1. `NONE` (default)
2. `LOWERCASE_HYPHENATED`
3. `UPPERCASE_HYPHENATED`
4. `LOWERCASE_UNDERLINED`
5. `UPPERCASE_UNDERLINED`

```java
import com.github.secretx33.sccfg.api.annotation.Configuration;
import com.github.secretx33.sccfg.api.NameStrategy;

@Configuration(nameStrategy = NameStrategy.LOWERCASE_HYPHENATED)
public class MyConfig {
    public int someInt = 0;
    public final String SOME_SCREAMING_VARIABLE = "screaming";
}
```

Will become:

```yml
some-int: 0
some-screaming-variable: screaming
```

Register type adapters for your custom types by simply annotating them with `@RegisterTypeAdapter(YourCustomClass.class)` (you only have to provide type adapters if you get a `ConfigSerializationException` explaining that SC-CFG could not deserialize your field). 

```java
@RegisterTypeAdapter(YourCustomClass.class)
public class ClassAdapter implements JsonSerializer<YourCustomClass>, JsonDeserializer<YourCustomClass> {
    // ...
}
```

Sometimes, we have a very specific type which uses [parameterized types](http://www.angelikalanger.com/GenericsFAQ/FAQSections/ParameterizedTypes.html#FAQ001), which our `RegisterTypeAdapter` annotation does not support. If that happens to you, worry not! You can still register your type adapters by using:

```java
import com.google.gson.reflect.TypeToken;

public class MyPlugin {
    
    @Override
    public void onEnable() {
        final Type type = new TypeAdapter<YourCustomType<List<String>>>() {}.getType();
        
        // register one type adapter
        Config.registerTypeAdapter(type, new YourCustomTypeAdapter());

        // register multiple type adapters at once
        final Map<? extends Type, Object> typeAdapters = new HashMap<>();
        typeAdapters.put(SomeClass.class, AdapterForSomeClass());
        typeAdapters.put(type, YourCustomTypeAdapter());
        Config.registerTypeAdapters(typeAdapters);
    }
}
```


## Kotlin

TODO
