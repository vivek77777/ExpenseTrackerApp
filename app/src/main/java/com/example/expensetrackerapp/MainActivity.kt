package com.example.expensetrackerapp
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*
data class Expense(val category: String, val amount: Double, val description: String, val date: String)
class MainActivity : AppCompatActivity() {

    private lateinit var selectedDate: String
    private var totalExpenses = 0.0
    private val expenses = mutableListOf<Expense>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinnerCategory: Spinner = findViewById(R.id.spinnerCategory)
        val editTextAmount: EditText = findViewById(R.id.editTextAmount)
        val editTextDescription: EditText = findViewById(R.id.editTextDescription)
        val buttonSelectDate: Button = findViewById(R.id.buttonSelectDate)
        val buttonAddExpense: Button = findViewById(R.id.buttonAddExpense)
        val buttonClearFilters: Button = findViewById(R.id.buttonClearFilters)
        val textViewSummary: TextView = findViewById(R.id.textViewSummary)
        val textViewDate: TextView = findViewById(R.id.textViewDate)
        val spinnerFilterCategory: Spinner = findViewById(R.id.spinnerFilterCategory)
        val textViewFilteredSummary: TextView = findViewById(R.id.textViewFilteredSummary)
        val linearLayoutHistory: LinearLayout = findViewById(R.id.linearLayoutHistory)
        val categories = arrayOf("Food", "Travel", "Entertainment")
        spinnerCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerFilterCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        var selectedCategory = categories[0]

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCategory = categories[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
        buttonSelectDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
                    selectedDate = dateFormat.format(calendar.time)
                    textViewDate.text = "Selected Date: $selectedDate"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
        buttonAddExpense.setOnClickListener {
            val amountText = editTextAmount.text.toString()
            val descriptionText = editTextDescription.text.toString()

            if (amountText.isNotEmpty() && descriptionText.isNotEmpty() && ::selectedDate.isInitialized) {
                val amount = amountText.toDouble()
                val expense = Expense(selectedCategory, amount, descriptionText, selectedDate)
                expenses.add(expense)
                totalExpenses += amount
                textViewSummary.text = "Monthly Summary: $$totalExpenses"

                Toast.makeText(
                    this,
                    "Expense added: $selectedCategory - $$amount ($descriptionText) on $selectedDate",
                    Toast.LENGTH_SHORT
                ).show()

                // Update transaction history
                updateTransactionHistory()

                // Clear input fields
                editTextAmount.text.clear()
                editTextDescription.text.clear()
                textViewDate.text = "Select Date"
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
        buttonClearFilters.setOnClickListener {
            spinnerFilterCategory.setSelection(0)
            textViewFilteredSummary.text = "Filtered Summary: $0.00"
        }
        spinnerFilterCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val filteredCategory = categories[position]
                val filteredExpenses = expenses.filter { it.category == filteredCategory }
                val filteredTotal = filteredExpenses.sumOf { it.amount }
                textViewFilteredSummary.text = "Filtered Summary: $$filteredTotal"
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }
    private fun updateTransactionHistory() {
        val linearLayoutHistory: LinearLayout = findViewById(R.id.linearLayoutHistory)
        linearLayoutHistory.removeAllViews()
        for (expense in expenses) {
            val expenseView = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.HORIZONTAL
                setPadding(8, 8, 8, 8)
                val textViewCategory = TextView(this@MainActivity).apply {
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    text = expense.category
                    textSize = 14f
                    setTextColor(ContextCompat.getColor(context, android.R.color.black))
                }
                val textViewAmount = TextView(this@MainActivity).apply {
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    text = "$${expense.amount}"
                    textSize = 14f
                    setTextColor(ContextCompat.getColor(context, android.R.color.black))
                }
                val textViewDescription = TextView(this@MainActivity).apply {
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    text = expense.description
                    textSize = 14f
                    setTextColor(ContextCompat.getColor(context, android.R.color.black))
                }
                val textViewDate = TextView(this@MainActivity).apply {
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    text = expense.date
                    textSize = 14f
                    setTextColor(ContextCompat.getColor(context, android.R.color.black))
                }
                addView(textViewCategory)
                addView(textViewAmount)
                addView(textViewDescription)
                addView(textViewDate)
            }
            linearLayoutHistory.addView(expenseView)
        }
    }
}