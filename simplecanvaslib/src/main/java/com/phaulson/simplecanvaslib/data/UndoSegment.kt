package com.phaulson.simplecanvaslib.data

import com.phaulson.simplecanvaslib.enums.PathState

internal class UndoSegment(var pathState: PathState) {
    val items = mutableListOf<DrawItem>()

    constructor(pathState: PathState, item: DrawItem): this(pathState) {
        items.add(item)
    }

    constructor(pathState: PathState, items: MutableList<DrawItem>): this(pathState) {
        this.items.addAll(items)
    }
}