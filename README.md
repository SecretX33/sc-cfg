# sc-cfg
 
SC-CFG is a simple, yet powerful library that automatically generate configuration files based on your classes. Compatible with Java 8+ and Kotlin, anyone can use it and start enjoying the commodities right now.

You won't have to worry about reloading your configuration anymore, SC-CFG handles everything for you, automatically.

## Add the library as dependency

### Modules

First, pick what modules you're going to use, currently there are:

- `bukkit` for Spigot 1.8+ and Java 8+
- `bukkit-kotlin` for Kotlin extensions on Bukkit

[comment]: <> (- `standalone` TBD)

Then add them to your favorite build tool.
#### Gradle
```gradle
repositories {
    maven("https://jitpack.io")
}

dependencies {
    // replace 'bukkit' with artifact name
    implementation("com.github.secretx33.sc-cfg:bukkit:master-SNAPSHOT")
}
```

#### Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.secretx33.sc-cfg</groupId>
        <!-- Replace 'bukkit' with the artifact name -->
        <artifactId>bukkit</artifactId>
        <version>master-SNAPSHOT</version>
    </dependency>
</dependencies>
```

## Usage

### Annotate your config class

Using SC-CFG is as simple as annotating your config class with `@Configuration`. Both `public` and `private` fields are supported, `final` or not.

```java
import com.github.secretx33.sccfg.api.annotation.Configuration;

// you just have to annotate the class with it
@Configuration
public class MyConfig {
    
    public int someValue = 0;
    private final String someString = "rock";
}
```

Which is serialized to `MyConfig.yml`, automatically.

```yaml
someValue: 0
someString: rock
```

### Get the instance of the config class

Getting the singleton instance of the class through the `Config` class.

```java
public class MyPlugin {
    
    @Override
    public void onEnable() {
        // get the instance of your config
        MyConfig config = Config.getConfig(MyConfig.class);
    }
}
```

### Register an instance of the config class

If your config class cannot have a no args constructor for some reason, like when it needs some dependency injected on it, you can handle the instantiation of the config, registering it later, when is convenient for you.

```java
public class MyPlugin {
    
    @Override
    public void onEnable() {
        // config that require some dependency injected
        MyConfig config = new MyConfig(this);
                
        // you can register any config whenever you want
        Config.registerConfig(config);

        // and even multiple configs at once
        OtherConfigTwo config2 = new OtherConfigTwo(this);
        OtherConfigThree config3 = new OtherConfigThree(this);
  
        Config.registerConfigs(config2, config3);
    }
}
```

# Features

### Automatic reload

Configs are already reload automatically, whenever there's some modification on the file, you don't have to do anything extra.

### Save
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

### Multiple file types
The default file type for configurations is `YAML`, but we do support multiple file type like `HOCON` and `JSON`, to switch between then is as easy as changing one option on your `@Configuration` annotation.

```java
import com.github.secretx33.sccfg.api.*;

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

### Change file name and path

You can modify the file name and path through your `@Configuration` annotation.

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

### Run methods on reload

Run methods before or after the config is reloaded, all you have to do it annotate the method with the respective annotation. Both public and private **no args** methods are supported. 

By default, these methods run on the main thread, but you can specify `async` on the annotation parameter to run the method on another thread (ForkJoinPool).

```java
import com.github.secretx33.sccfg.api.annotation.*;

@Configuration
public class MyConfig {
    
    @BeforeReload
    public void doBefore() {
        // doing something before config is reloaded
    }

    @BeforeReload(async = true)
    private void doBeforeAsync() {
        // doing something before config is reloaded, but async
    }
    
    @AfterReload
    private void doAfter() {
        // doing something after config is reloaded
    }

    @AfterReload(async = true)
    public void doAfterAsync() {
        // doing another thing after config is reloaded, but async
    }
}
```

### Skip fields

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

### Choose the field name scheme

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

## Type Adapters

If you got a `ConfigSerializationException` explaining that SC-CFG could not deserialize your field, this is probably what you are looking for. By creating a type adapter, you tell SC-CFG how to serialize and deserialize a certain type.

By default, SC-CFG come with some preconfigured type adapters, but you can always provide custom adapters for any type, and if it happens that one of your type adapter clash with the default ones, your type adapter will override the default one, this is already handled, you don't have to do anything.

### Automatically register type adapters
Register type adapters for your custom types simply by annotating them with `@RegisterTypeAdapter`. 

```java
import com.google.gson.JsonSerializer;

// this adapter is automatically registered
@RegisterTypeAdapter(YourCustomClass.class)
public class ClassAdapter implements JsonSerializer<YourCustomClass>, JsonDeserializer<YourCustomClass> {
    // ...
}
```

### Manually register type adapters

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

## Bugs or suggestions

Please report all bugs or post suggestions on the [Issue](https://github.com/SecretX33/sc-cfg/issues) section.

## Support

TODO
