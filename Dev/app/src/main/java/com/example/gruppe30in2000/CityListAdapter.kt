package com.example.gruppe30in2000

import android.graphics.Color
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.alert_dialog.view.*

class CityListAdapter (private val dataSet: ArrayList<CityElement>) :
    RecyclerView.Adapter<CityListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityListAdapter.ViewHolder{

        val textView = LayoutInflater.from(parent.context).inflate(R.layout.city_element, parent, false) as View
        return ViewHolder(textView)

    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        //Log.d("VIEWHOLDER: ", "onBindViewHolder: called")

        // set the element (cardview) text and description text base on the current position of the dataSet list.
        holder.title.text = dataSet[pos].title
        holder.description.text = dataSet[pos].description


        holder.deleteButton.setOnClickListener {

            val itemDetail = dataSet.get(pos).title
            dataSet.removeAt(pos)
            notifyItemRemoved(pos)
            notifyItemRangeChanged(pos,dataSet.size)
            Toast.makeText(holder.deleteButton.context,"Removed $itemDetail",Toast.LENGTH_SHORT).show()

        }


        // TODO Find a way to collapse to one function for cleaner code?
        // Edit elementContent - Similar code to when adding a new element in FavoriteCity.
        holder.editButton.setOnClickListener {

            // get the current content
            val tempTitle = dataSet[pos].title
            val tempDescription= dataSet[pos].description

            // Build new dialog for content change
            val dialogBuilder = AlertDialog.Builder(holder.deleteButton.context) // make a dialog builder
            val dialogView = LayoutInflater.from(holder.deleteButton.context).inflate(R.layout.alert_dialog, null) // get the dialog xml view
            dialogBuilder.setView(dialogView) // set the view into the builder
            dialogView.edit_title.text = tempTitle
            dialogView.edit_description.text = tempDescription

            val alertDialog = dialogBuilder.create()

            alertDialog.show()


            // Change Element content
            val addButton = dialogView.findViewById<Button>(R.id.add_button)
            val edit_title = dialogView.findViewById<EditText>(R.id.edit_title)
            val edit_description = dialogView.findViewById<EditText>(R.id.edit_description)


            // make a common textWatcher to use for several editText listener
            val textWatcher = object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val titleInput = edit_title.text
                    val descriptionInput = edit_description.text


                    // Change risk displayimage color.
                    if (descriptionInput.contains("hoy", ignoreCase = true)) {
                        Log.e("log: ", "DescriptionInput contains hoy!!!")
                        holder.riskDisplay.setBackgroundColor(Color.BLACK)
                    }
                    else {
                        holder.riskDisplay.setBackgroundColor(Color.GREEN)
                    }

                    addButton.isEnabled = (!titleInput.isEmpty() && !descriptionInput.isEmpty())
                }
            }

            edit_title.addTextChangedListener(textWatcher)
            edit_description.addTextChangedListener(textWatcher)


            addButton.setOnClickListener {
                val titleLength = dataSet[pos].title.length
                val descriptLength = dataSet[pos].description.length


                // Edit the data list element content
                dataSet[pos].title.replace(0, titleLength, edit_title.text)
                dataSet[pos].description.replace(0, descriptLength, edit_description.text)

                // Edit the gui element content
                holder.title.text = edit_title.text
                holder.description.text = edit_description.text

                alertDialog.hide()

                Toast.makeText(addButton.context, "Edit saved", Toast.LENGTH_SHORT).show()
            }

        }

    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(textView: View) : RecyclerView.ViewHolder(textView) {
        val title = textView.findViewById<TextView>(R.id.title_text)
        val description = textView.findViewById<TextView>(R.id.description_text)
        val deleteButton = textView.findViewById<ImageButton>(R.id.delete_button)
        val editButton = textView.findViewById<ImageButton>(R.id.edit_button)
        val riskDisplay = textView.findViewById<ImageView>(R.id.risk_display)


    }



}