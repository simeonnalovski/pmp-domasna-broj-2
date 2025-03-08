package com.example.myapplication


import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader


class MainActivity : AppCompatActivity() {

    private lateinit var searchWordEditText: EditText
    private lateinit var translationEditText: EditText
    private lateinit var clearButton: Button
    private lateinit var saveButton:Button
    private val translationMap = HashMap<String, String>()
    private var toast: Toast? = null
    private val dictionaryFile: File by lazy {
        File(filesDir, "en_mk_recnik.txt")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchWordEditText = findViewById(R.id.searchWord)
        translationEditText = findViewById(R.id.translation)
        clearButton = findViewById(R.id.clearContent)
        saveButton = findViewById(R.id.saveContent)
        loadTranslations()

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
            toast?.cancel()
        }
        saveButton.setOnClickListener{
            saveNewWord()
        }
    }


    private fun loadTranslations() {
        val file= dictionaryFile

        try {
            val inputStream = FileInputStream(file)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String? = reader.readLine()
            while (line != null){
                val parts = line.split(",")
                if (parts.size == 2) {
                    val key = parts[0].trim().lowercase()
                    val value = parts[1].trim().lowercase()
                    translationMap[key] = value
                    translationMap[value] = key
                }

                line = reader.readLine()
            }
            reader.close()
            inputStream.close()
        } catch (e: Exception) {
            Log.e("FileCheck", "Error reading file: ${e.message}")
        }
    }


    private fun isEnglish(text: String): Boolean {
        return text.matches(Regex("^[a-zA-Z]+$"))
    }

    private fun autoFillTranslation() {
        val searchText = searchWordEditText.text.toString().trim().lowercase()
        val translation = translationMap[searchText]

        if (translation != null) {
            translationEditText.setText(translation)
            toast?.cancel() //
        } else if (searchText.isNotEmpty() && translationEditText.text.isEmpty()) {
            showToast("Translation not found")
        }
    }

    private fun autoFillSearchWord() {
        val translationText = translationEditText.text.toString().trim().lowercase()
        val searchWord = translationMap[translationText]

        if (searchWord != null) {
            searchWordEditText.setText(searchWord)
            toast?.cancel()
        } else if (translationText.isNotEmpty() && searchWordEditText.text.isEmpty()) {
            showToast("Word not found")
        }
    }

    private fun showToast(message: String) {
        toast?.cancel()
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast?.show()
    }

    private fun saveNewWord() {
        var word = searchWordEditText.text.toString().trim().lowercase()
        var translation = translationEditText.text.toString().trim().lowercase()

        if (word.isEmpty() || translation.isEmpty()) {
            Toast.makeText(this, "Both fields must be filled!", Toast.LENGTH_SHORT).show()
            return
        }

        if (translationMap.containsKey(word)) {
            Toast.makeText(this, "Word already exists!", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isEnglish(word)) {
            val temp = word
            word = translation
            translation = temp
        }

        translationMap[word] = translation
        translationMap[translation] = word

        try {
            val outputStream = FileOutputStream(dictionaryFile, true)
            outputStream.write("$word,$translation\n".toByteArray())
            outputStream.flush()
            outputStream.close()
            Toast.makeText(this, "Word saved!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error saving word: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

}