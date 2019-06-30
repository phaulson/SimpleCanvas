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

## Features
* simple drawing and erasing on a blank background or image
* color seek bar to pick a color
* pinch gesture to change stroke width
* buttons to undo, clear and toggle drawing/erasing
* highly customizable
* adding text is **NOT** supported yet

##  Usage

### XML
```xml
<com.phaulson.simplecanvaslib.customView.SimpleCanvas
        android:id="@+id/simpleCanvas"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```

### Kotlin (examples)
For a detailed description see [me](##Kotlin-Properties)
```Kotlin
simpleCanvas.drawState = DrawState.Erasing                      // set drawing mode to erasing
simpleCanvas.drawState = DrawState.Drawing                      // set drawing mode back to drawing
simpleCanvas.strokeColor = Color.Black                          // set stroke color
simpleCanvas.strokeWidth = 100F                                 // set stroke width
simpleCanvas.imageSrc = R.drawable.tree                         // set resource of background image
simpleCanvas.showColorSeekBar = false                           // hide color seek bar
simpleCanvas.buttonBarPosition = BarPosition.VERTICAL_TOP_LEFT  // align buttons to vertical top left
simpleCanvas.undo()                                             // undo last action
simplecanvas.clear()                                            // clear canvas
```

### XML-Attributes
|attribute|format|default|description|
|---|---|---|---|
|`app:image_src`|reference|0|resource of background image|
|`app:stroke_color`|color|-1<br/>determined by color seek bar|drawing color|
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

### Kotlin-Properties
|property|type|access|description|
|---|---|---|---|
|`pathCount`|Int|GET|number of lines on canvas|
|`paths`|Map<Path, Paint>|GET|collection of lines on canvas|
|`imageSrc`|Int|GET/SET|resource of background image|
|`strokeColor`|Int|GET/SET|drawing color|
|`strokeWidth`|Float|GET/SET|width of drawn line|
|`bgColor`|Int|GET/SET|background color|
|`adjustStrokeWidthOnPinch`|Boolean|GET/SET|enable change of stroke width on pinch gesture|
|`showColorSeekBar`|Boolean|GET/SET|show color seek bar|
|`showUndoButton`|Boolean|GET/SET|show undo button|
|`showClearButton`|Boolean|GET/SET|show clear button|
|`showDrawingErasingToggle`|Boolean|GET/SET|show drawing/erasing button|
|`showErasingMark`|Boolean|GET/SET|show aim of eraser|
|`undoButtonColor`|Int|GET/SET|background color of undo button|
|`clearButtonColor`|Int|GET/SET|background color of clear button|
|`drawingErasingToggleColor`|Int|GET/SET|background color of drawing/erasing button|
|`hideColorSeekBarOnTouch`|Boolean|GET/SET|hide color seek bar when drawing|
|`hideButtonBarOnTouch`|Boolean|GET/SET|hide buttons when drawing|
|`colorSeekBarPosition`|BarPosition|GET/SET|position of color seek bar|
|`buttonBarPosition`|BarPosition|GET/SET|position of buttons|
|`drawState`|DrawState|GET/SET|switch between drawing and erasing|

### Public Methods
|method|description|
|---|---|
|`undo()`|undo last action (including clear)|
|`clear()`|clear canvas|
|`drawPath(path: Path, paint: Paint, considerWhenUndo: Boolean`)|draw path to canvas<br/>specify if it is considered when calling undo()|
|`drawCircle(x: Float, y: Float, radius: Float, paint: Paint, considerWhenUndo: Boolean)`|draw circle to canvas|
|`erasePath(index: Int)`|erase path at given index|
|`setColorSeekBarColors(res: Int/IntArray)`|set new resource of colors to color seek bar|
