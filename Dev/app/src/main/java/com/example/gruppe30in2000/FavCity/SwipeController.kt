package com.example.gruppe30in2000.FavCity

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper.Callback

import android.support.v7.widget.helper.ItemTouchHelper.*
import android.view.MotionEvent
import android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.example.gruppe30in2000.R


open class SwipeController : Callback() {
    private var swipeBack = false
    private val buttonWidth = 300f

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(0, LEFT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        Log.e("Swipeconntroller", "Moving")
        moveItem(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = false
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {

        if (actionState == ACTION_STATE_SWIPE) {
            setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                swipeBack = event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
                if (swipeBack) {
                    // Dersom brukeren swiper til venste, viser vi dialogen slik at brukeren kan slette stasjonen.
                    if (dX <= buttonWidth) {
                        if (isCurrentlyActive) {
                            val dialogBuilder = AlertDialog.Builder(recyclerView.context) // make a dialog builder
                            val dialogView = LayoutInflater.from(recyclerView.context).inflate(R.layout.delete_alert, null) // get the dialog xml view
                            dialogBuilder.setView(dialogView) // set the view into the builder
                            val alertDialog = dialogBuilder.create()
                            alertDialog.show()

                            val okButton = dialogView.findViewById<Button>(R.id.ok_button)
                            val cancelButton = dialogView.findViewById<Button>(R.id.cancel_button)

                            okButton.setOnClickListener {
                                deleteItem(viewHolder.adapterPosition)
                                alertDialog.hide()
                            }

                            cancelButton.setOnClickListener {
                                alertDialog.hide()
                            }
                        }
                    }
                }
                return false
            }
        })
    }
    open fun deleteItem(pos: Int) {
        return
    }
    open fun moveItem(oldPos: Int, newPos: Int) {
        return
    }
}


