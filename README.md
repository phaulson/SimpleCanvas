# SimpleCanvas
a highly customizable snapchat like canvas

## Quick Start
<a href="https://jitpack.io/#phaulson/SimpleCanvas">![Release](https://jitpack.io/v/phaulson/SimpleCanvas.svg)</a>
<a href="https://android-arsenal.com/api?level=24">![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)</a>
### Gradle
Step 1. Add the JitPack repository to your build file:
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
````
Step 2. Add the dependency:
```gradle
dependencies {
    implementation 'com.github.phaulson:SimpleCanvas:v1.0'
}
```
### Maven
Step 1. Add the JitPack repository to your build file:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
````
Step 2. Add the dependency:
```xml
<dependency>
    <groupId>com.github.phaulson</groupId>
    <artifactId>SimpleCanvas</artifactId>
    <version>v1.0</version>
</dependency>
```

##  Usage

XML
```xml
<com.phaulson.simplecanvaslib.customView.SimpleCanvas
        android:id="@+id/simpleCanvas"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```

## Attributes


