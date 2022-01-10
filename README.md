# sc-cfg
 
SC-CFG is a simple, yet powerful library that automatically generate configuration files based on your classes. Compatible with Java 8+ and Kotlin, anyone can use it and start enjoying the commodities right now.

You won't have to worry about reloading your configuration anymore, SC-CFG handles everything for you, automatically.

## Add the library as dependency

### Modules

First, pick what platform modules you're going to use, currently there are:

- `bukkit` for Spigot 1.8+ and Java 8+
- `bungee` for BungeeCord
- `kotlin` for Kotlin extensions on any platform

Second, pick the serializers you're going to use according to the file type you want your configs have.

- `hocon` (.conf)
- `json` (.json)
- `yaml` (.yml)

Then add them to your favorite build tool.

#### Gradle
```gradle
repositories {
    maven { url = 'https://jitpack.io' }
}

dependencies {
    // replace 'bukkit' with artifact name
    implementation("com.github.secretx33.sc-cfg:bukkit:main-SNAPSHOT")
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
        <version>main-SNAPSHOT</version>
    </dependency>
</dependencies>
```

## Usage

### Annotate your config class

Using SC-CFG is as simple as annotating your config class with `@Configuration`. Both `public` and `private` fields are supported, `final` or not, in both the own class and parents.

```java
import com.github.secretx33.sccfg.api.annotation.Configuration;

// you just have to annotate the class
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

To get the singleton instance of the class through the `Config` class, do as follows.

```java
public class MyPlugin extends JavaPlugin {
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
import com.github.secretx33.sccfg.Config;

public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        MyConfigOne config = new MyConfig(this); // config that require some dependency injected
        Config.registerConfig(config); // you can register any config whenever is convenient
        // Config.registerConfigs(...); // and even multiple configs at once
    }
}
```

# Features

### Automatic reload

Configs are already reload automatically whenever there's some modification on the file, you don't have to do anything extra.

### Save config

You can save config current values to the disk with a single method call.

```java
public class MyPlugin extends JavaPlugin {

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

The default file type for configurations is `YAML`, but we do support multiple file type (check all supported types [here](https://secretx.gitbook.io/sc-cfg/setup#choose-the-modules-you-need)).

To switch between then is as easy as changing one option on your `@Configuration` annotation (and add the respective serializer for that type).

```java
import com.github.secretx33.sccfg.api.*;

// switch your config to hocon
@Configuration(type = FileType.HOCON)
public class MyConfig {
    // ...
}
```

### Header and comments

It's now possible to add a header to the file by setting `header` property of your `@Configuration` annotation, and also comments directly to properties by annotating them with `@Comment` (or `@NamedPath`).

OBS.: Json does not support comments by nature, so any comments on this `FileType` will be ignored.

```java
import com.github.secretx33.sccfg.api.*;

@Configuration(header = {
    "I am the first line of the header",
    "and I am the second one",
})
public class MyConfig {
    
    @Comment("this value is awesome")
    public int someValue = 0;
    
    @Comment({"but this one...", "this one ROCKS!"})
    private final String someString = "rock";
}
```

And our `MyConfig.yml` will looks like this.

```yaml
# I am the first line of the header
# and I am the second one

# this value is awesome
someValue: 0
# but this one...
# this one ROCKS!
someString: rock
```

#### And much, much more... Please check [our wiki](https://secretx.gitbook.io/sc-cfg/) for a complete list of sc-cfg features with usage examples.

## Kotlin

Please check [our wiki](https://secretx.gitbook.io/sc-cfg/) for detailed usages of Kotlin extension functions, but there's a brief list of them.

```kotlin
import com.github.secretx33.sccfg.*

// get the singleton instance of MyConfig
val config = getConfig<MyConfig>()
val lazyConfig = lazyConfig<MyConfig>()

// register a config instance manually initiated
val anotherConfig = AnotherConfig(someDependency)
registerConfig(anotherConfig)

// persist the config to the disk
saveConfig(config)
// or
saveConfig<MyConfig>()

// save default values to the disk, optionally overriding the file if exists
saveDefaults(config, reloadAfterwards = true, overrideIfExists = false)
```

## Bugs or suggestions

Please report all bugs or post suggestions on the [Issue](https://github.com/SecretX33/sc-cfg/issues) section.

## License

Read file [LICENSE](LICENSE).
