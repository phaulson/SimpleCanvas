# SimpleCanvas
a highly customizable snapchat like canvas

### Gradle:
<a href="https://jitpack.io/#phaulson/SimpleCanvas">![Release](https://jitpack.io/v/phaulson/SimpleCanvas.svg)</a>
<a href="https://android-arsenal.com/api?level=24">![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)</a>

Step 1. Add the JitPack repository in your root build.gradles:
```
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```
Step 2. Add the dependency:
```
implementation 'com.github.phaulson:SimpleCanvas:v1.0'
```

##  Usage

XML
```xml
<com.phaulson.simplecanvaslib.customView.SimpleCanvas
        android:id="@+id/simpleCanvas"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```
