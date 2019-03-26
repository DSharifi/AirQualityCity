package com.example.gruppe30in2000.FavCity

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.*
import com.example.gruppe30in2000.R
import kotlinx.android.synthetic.main.alert_dialog.view.*

class CityListAdapter (private val dataSet: ArrayList<CityElement>, context: Context) :
    RecyclerView.Adapter<CityListAdapter.ViewHolder>() {
    val context = context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val textView = LayoutInflater.from(parent.context).inflate(R.layout.city_element, parent, false) as View
        return ViewHolder(textView)

    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        //Log.d("VIEWHOLDER: ", "onBindViewHolder: called")

        // set the element (cardview) text and description text base on the current position of the dataSet list.
        holder.title.text = dataSet[pos].title.toString()
        holder.description.text = dataSet[pos].description.toString()

        // VALIDATE the risk type of newly added city
        validateRiskType(holder.description.text.toString(), holder)

//        holder.deleteButton.setOnClickListener {
//
//            val itemDetail = dataSet.get(pos).title
//            dataSet.removeAt(pos)
//            notifyItemRemoved(pos)
//            notifyItemRangeChanged(pos,dataSet.size)
//            Toast.makeText(holder.deleteButton.context,"Removed $itemDetail",Toast.LENGTH_SHORT).show()
//
//        }


        // TODO Find a way to collapse to one function for cleaner code?
        // Edit elementContent - Similar code to when adding a new element in FavoriteCity.
        holder.linearView.setOnClickListener {

            // get the current content
            val tempTitle = dataSet[pos].title
            val tempDescription= dataSet[pos].description

            // Build new dialog for content change
            val dialogBuilder = AlertDialog.Builder(context) // make a dialog builder
            val dialogView = LayoutInflater.from(context).inflate(R.layout.alert_dialog, null) // get the dialog xml view
            dialogBuilder.setView(dialogView) // set the view into the builder
            dialogView.edit_title.text = tempTitle
            dialogView.edit_description.text = tempDescription

            val alertDialog = dialogBuilder.create()

            alertDialog.show()


            // Change Element content
            val addButton = dialogView.findViewById<Button>(R.id.add_button)
            addButton.text = "Save"
            val edit_title = dialogView.findViewById<EditText>(R.id.edit_title)
            val edit_description = dialogView.findViewById<EditText>(R.id.edit_description)


            // make a common textWatcher to use for several editText listener
            val textWatcher = object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val titleInput = edit_title.text
                    val descriptionInput = edit_description.text

                    addButton.isEnabled = (!titleInput.isEmpty() && !descriptionInput.isEmpty())
                }
            }

            edit_title.addTextChangedListener(textWatcher)
            edit_description.addTextChangedListener(textWatcher)


            addButton.setOnClickListener {
                val titleLength = dataSet[pos].title.length
                val descriptLength = dataSet[pos].description.length


                // Edit the data list element content

                // TODO remove? no longer need to implement edit this way.

//                dataSet[pos].title.replace(0, titleLength, edit_title.text)
//                dataSet[pos].description.replace(0, descriptLength, edit_description.text)

                // Edit the gui element content
                holder.title.text = edit_title.text
                holder.description.text = edit_description.text

                validateRiskType(holder.description.text.toString(), holder)

                alertDialog.hide()

                Toast.makeText(addButton.context, "Edit saved", Toast.LENGTH_SHORT).show()
            }

        }


//        val onSwipeTouchListener = OnSwipeTouchListener(context)
//
//        holder.linearView.setOnTouchListener(onSwipeTouchListener)

    }
    override fun getItemCount(): Int {
        return dataSet.size
    }

    // Method to validate and change the riskdisplay image by description text.
    private fun validateRiskType(text : String, holder : ViewHolder) {
        // Change risk displayimage color.
        when {
            text.contains("hoy", ignoreCase = true) ->
                holder.riskDisplay.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.ic_lens_red_35dp
                ))

            text.contains("moderat", ignoreCase = true) ->
                holder.riskDisplay.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.ic_lens_yellow_35dp
                ))

            text.contains("lav", ignoreCase = true) ->
                holder.riskDisplay.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.ic_lens_green_35dp
                ))

            else -> Log.e("log: ", "FEIL RISK INPUT!!")
        }
    }

    class ViewHolder(textView: View) : RecyclerView.ViewHolder(textView) {
        val title = textView.findViewById<TextView>(R.id.title_text)
        val description = textView.findViewById<TextView>(R.id.description_text)
//        val deleteButton = textView.findViewById<ImageButton>(R.id.delete_button)
//        val editButton = textView.findViewById<ImageButton>(R.id.edit_button)
        val riskDisplay = textView.findViewById<ImageView>(R.id.risk_display)
        val swipeDelete = textView.findViewById<LinearLayout>(R.id.swipe_delete)
        val linearView = textView.findViewById<LinearLayout>(R.id.linear_view)

    }



}