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

### XML
```xml
<com.phaulson.simplecanvaslib.customView.SimpleCanvas
        android:id="@+id/simpleCanvas"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```

### XML-Attributes
|attr|format|default|description|
|---|---|---|---|
|`app:image_src`|reference|0|resource of background image|
|`app:stroke_color`|color|determined by color seek bar|drawing color|
|`app:stroke_width`|float|50F|width of drawn line|
|`app:bg_color`|color|0|background color|
|`app:color_seek_bar_colors`|reference|material design 500|resource array for color seek bar|
|`app:adjust_stroke_width_on_pinch`|boolean|true|enable change of stroke width on pinch gesture|
|`app:show_color_seek_bar`|boolean|true|show color seek bar|
|`app:show_undo_button`|boolean|true|show undo button|
|`app:show_clear_button`|boolean|true|show clear button|
|`app:show_drawing_erasing_toggle`|boolean|true|show drawing/erasing button|
|`app:show_erasing_mark`|boolean|true|show aim of eraser|
|`app:undo_button_color`|color|holo_blue_light|background color of undo button|
|`app:clear_button_color`|color|holo_blue_light|background color of clear button|
|`app:drawing_erasing_toggle_color`|color|holo_blue_light|background color of drawing/erasing button|
|`app:hide_color_seek_bar_on_touch`|boolean|true|hide color seek bar when drawing|
|`app:hide_button_bar_on_touch`|boolean|true|hide buttons when drawing|
|`app:color_seek_bar_position`|enum|VERTICAL_TOP_RIGHT|position of color seek bar|
|`app:button_bar_position`|enum|HORIZONTAL_TOP_RIGHT|position of buttons|

