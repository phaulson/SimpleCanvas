package com.phaulson.simplecanvas

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.phaulson.simplecanvaslib.enums.BarPosition
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        R.id.menu_vtr_c -> {
            canvas.colorSeekBarPosition = BarPosition.VERTICAL_TOP_RIGHT
            true
        }
        R.id.menu_vbr_c -> {
            canvas.colorSeekBarPosition = BarPosition.VERTICAL_BOTTOM_RIGHT
            true
        }
        R.id.menu_vtl_c -> {
            canvas.colorSeekBarPosition = BarPosition.VERTICAL_TOP_LEFT
            true
        }
        R.id.menu_vbl_c -> {
            canvas.colorSeekBarPosition = BarPosition.VERTICAL_BOTTOM_LEFT
            true
        }
        R.id.menu_htr_c -> {
            canvas.colorSeekBarPosition = BarPosition.HORIZONTAL_TOP_RIGHT
            true
        }
        R.id.menu_hbr_c -> {
            canvas.colorSeekBarPosition = BarPosition.HORIZONTAL_BOTTOM_RIGHT
            true
        }
        R.id.menu_htl_c -> {
            canvas.colorSeekBarPosition = BarPosition.HORIZONTAL_TOP_LEFT
            true
        }
        R.id.menu_hbl_c -> {
            canvas.colorSeekBarPosition = BarPosition.HORIZONTAL_BOTTOM_LEFT
            true
        }
        R.id.menu_vtr_b -> {
            canvas.buttonBarPosition = BarPosition.VERTICAL_TOP_RIGHT
            true
        }
        R.id.menu_vbr_b -> {
            canvas.buttonBarPosition = BarPosition.VERTICAL_BOTTOM_RIGHT
            true
        }
        R.id.menu_vtl_b -> {
            canvas.buttonBarPosition = BarPosition.VERTICAL_TOP_LEFT
            true
        }
        R.id.menu_vbl_b -> {
            canvas.buttonBarPosition = BarPosition.VERTICAL_BOTTOM_LEFT
            true
        }
        R.id.menu_htr_b -> {
            canvas.buttonBarPosition = BarPosition.HORIZONTAL_TOP_RIGHT
            true
        }
        R.id.menu_hbr_b -> {
            canvas.buttonBarPosition = BarPosition.HORIZONTAL_BOTTOM_RIGHT
            true
        }
        R.id.menu_htl_b -> {
            canvas.buttonBarPosition = BarPosition.HORIZONTAL_TOP_LEFT
            true
        }
        R.id.menu_hbl_b -> {
            canvas.buttonBarPosition = BarPosition.HORIZONTAL_BOTTOM_LEFT
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
