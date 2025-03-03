package com.example.myapplication


import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var searchWordEditText: EditText
    private lateinit var translationEditText: EditText
    private lateinit var clearButton: Button
    private val translationMap = HashMap<String, String>()
    private var toast: Toast? = null // Store the Toast object

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchWordEditText = findViewById(R.id.searchWord)
        translationEditText = findViewById(R.id.translation)
        clearButton = findViewById(R.id.clearContent)

        loadTranslationsFromRaw()

        searchWordEditText.setOnKeyListener { _, _, _ ->
            autoFillTranslation()
            false
        }

        translationEditText.setOnKeyListener { _, _, _ ->
            autoFillSearchWord()
            false
        }

        clearButton.setOnClickListener {
            searchWordEditText.text.clear()
            translationEditText.text.clear()
            toast?.cancel() // Cancel any existing Toast
        }
    }

    private fun loadTranslationsFromRaw() {
        try {
            val inputStream = resources.openRawResource(R.raw.en_mk_recnik)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String? = reader.readLine()

            while (line != null) {
                val parts = line.split(",")
                if (parts.size == 2) {
                    val key = parts[0].trim()
                    val value = parts[1].trim()
                    translationMap[key] = value
                    translationMap[value] = key
                }
                line = reader.readLine()
            }
            reader.close()
            inputStream.close()
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading translations: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun autoFillTranslation() {
        val searchText = searchWordEditText.text.toString().trim()
        val translation = translationMap[searchText]

        if (translation != null) {
            translationEditText.setText(translation)
            toast?.cancel() // Cancel the toast if a translation is found
        } else if (searchText.isNotEmpty() && translationEditText.text.isEmpty()) {
            showToast("Translation not found")
        }
    }

    private fun autoFillSearchWord() {
        val translationText = translationEditText.text.toString().trim()
        val searchWord = translationMap[translationText]

        if (searchWord != null) {
            searchWordEditText.setText(searchWord)
            toast?.cancel()
        } else if (translationText.isNotEmpty() && searchWordEditText.text.isEmpty()) {
            showToast("Word not found")
        }
    }

    private fun showToast(message: String) {
        toast?.cancel() // Cancel any previous toast
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast?.show()
    }
}