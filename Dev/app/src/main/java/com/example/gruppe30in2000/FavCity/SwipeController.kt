package com.example.gruppe30in2000.FavCity

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.Callback

import android.support.v7.widget.helper.ItemTouchHelper.*
import android.view.MotionEvent
import android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.example.gruppe30in2000.R


enum class ButtonsState {
    GONE,
    LEFT_VISIBLE,
    RIGHT_VISIBLE
}

open class SwipeController : Callback() {
    private var swipeBack = false
    private val buttonShowedState = ButtonsState.GONE
    private val buttonWidth = 300f

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return ItemTouchHelper.Callback.makeMovementFlags(0, LEFT or RIGHT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
//                moveItem(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                Log.e("onSwiped:", "Swiping")
//                val dialogBuilder = AlertDialog.Builder(recyclerView.context) // make a dialog builder
//                val dialogView = LayoutInflater.from(recyclerView.context).inflate(R.layout.delete_alert, null) // get the dialog xml view
//                dialogBuilder.setView(dialogView) // set the view into the builder
//                val alertDialog = dialogBuilder.create()
//                alertDialog.show()
//
//                val okButton = dialogView.findViewById<Button>(R.id.ok_button)
//                val cancelButton = dialogView.findViewById<Button>(R.id.cancel_button)
//
//                okButton.setOnClickListener {
//                    deleteItem(viewHolder.adapterPosition)
//                    alertDialog.hide()
//                }
//
//                cancelButton.setOnClickListener {
//                    alertDialog.hide()
//                }
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
                    if (dX <= buttonWidth) {
                        Log.e("onSwiped:", "Swiping")
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
                return false
            }
        })
    }
    open fun deleteItem(pos: Int) {
        return
    }
}


