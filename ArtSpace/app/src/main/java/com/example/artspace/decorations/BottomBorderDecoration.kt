package com.example.artspace.decorations

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class BottomBorderDecoration(private val color: Int, private val thickness: Int) : RecyclerView.ItemDecoration() {

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        // Solo aplicar el borde al primer ítem
        if (parent.childCount > 0) {
            val child = parent.getChildAt(0)  // Obtener el primer ítem
            val paint = Paint().apply {
                this.color = color
                this.strokeWidth = thickness.toFloat()
            }

            // Dibuja la línea en el borde inferior del primer ítem
            val left = parent.paddingLeft.toFloat()
            val right = parent.width - parent.paddingRight.toFloat()
            val bottom = child.bottom.toFloat()

            c.drawLine(left, bottom, right, bottom, paint)
        }
    }
}
