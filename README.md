![screenshot](https://i.imgur.com/LiY9Iuj.png)

HuntMaster is a powerful and dynamic name that captures the essence of setting bounties, managing targets, and tracking players with expert-level features. Perfect for a plugin that turns bounty hunting into a thrilling and strategic experience on your server!

---

## 🚀 For Developers

Although the plugin is not officially listed as a library, you can still integrate it into your project by following these steps:

### 1️⃣ Create a `libs/` folder in your project directory.
### 2️⃣ Add the latest JAR file to the folder.
### 3️⃣ Import the plugin like this:

#### 📦 **Groovy Gradle**:
```groovy
dependencies {
    implementation files('libs/plugin.jar')
}
```

#### 📦 **Kotlin Gradle**:
```kotlin
implementation(files("libs/plugin.jar"))
```

#### 📦 **Maven**:
```xml
<dependencies>
    <dependency>
        <groupId>net.solostudio</groupId>
        <artifactId>huntmaster</artifactId>
        <version>{VERSION}</version>
        <scope>system</scope>
        <systemPath>${project.basedir}/libs/plugin.jar</systemPath>
    </dependency>
</dependencies>
```

---

## 📜 Commands

- <> - Required | () - Optional
###
- `/huntmaster reload` - Reloads the plugin.
- `/huntmaster streaktop (value)` - Retrieves the streak top list.
- `/huntmaster menu` - Opens the huntmaster menu.
- `/huntmaster set <target> <reward type> <reward>` - Puts a bounty on the target.
- `/huntmaster remove <target>` - Removes the bounty from the target.
- `/huntmaster raise <target> <reward to increase by>` - Increases the bounty.
- `/huntmaster takeoff <target>` - Forcibly removes the bounty from the given player.
- `/huntmaster bountyfinder (target)` - Gives the player a bountyfinder.

---

## 🔑 Permissions

- `huntmaster.reload`
- `huntmaster.streaktop`
- `huntmaster.menu`
- `huntmaster.set`
- `huntmaster.remove`
- `huntmaster.raise`
- `huntmaster.takeoff`
- `huntmaster.bountyfinder`

---

## ❓ What is bountyfinder?

- The bountyfinder is an item that, when held by the player, displays the nearest bounty.

---

## 🤝 Contributing

Contributions are always welcome! If you have ideas for improvements, feel free to join the Discord server and open a ticket. Let's build something awesome together!

---

## 📜 License

This plugin is licensed under the [Apache-2.0 License](https://www.apache.org/licenses/LICENSE-2.0).