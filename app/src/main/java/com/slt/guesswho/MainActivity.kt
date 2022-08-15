package com.slt.guesswho

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    private lateinit var ivImageStar: ImageView

    private lateinit var btnAnswer1: Button
    private lateinit var btnAnswer2: Button
    private lateinit var btnAnswer3: Button
    private lateinit var btnAnswer4: Button

    private val url = "https://www.imdb.com/list/ls052283250/"

    private var urls = arrayListOf<String>()
    private var names = arrayListOf<String>()
    private var buttons = arrayListOf<Button>()

    private var numberOfQuestion = 0
    private var numberOfRightAnswer = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        urls = arrayListOf()
        names = arrayListOf()

        ivImageStar = findViewById(R.id.ivImageStar)

        btnAnswer1 =  findViewById(R.id.btnAnswer1)
        btnAnswer2 =  findViewById(R.id.btnAnswer2)
        btnAnswer3 =  findViewById(R.id.btnAnswer3)
        btnAnswer4 =  findViewById(R.id.btnAnswer4)
        buttons = arrayListOf(btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4)
        getContent()
        playGame()

    }

    private fun getContent() {
        val task = DownloadContentTask()
        try {
            val content = task.execute(url).get()
            val start = "<div class=\"list-description\"><p>All time most popular actors and actresses who are popular all around the world,not just in their own country... this list includes only the celebrities from film industry....</p></div>"
            val finish = "<div id=\"top_rhs_wrapper\" class=\"cornerstone_slot\">"
            val pattern = Pattern.compile("$start(.*?)$finish")
            val matcher = pattern.matcher(content)
            var splitContent = ""
            while (matcher.find()) {
                splitContent = matcher.group(1)
            }
            val patternIng = Pattern.compile("src=\"(.*?)\"width=")
            val patternName = Pattern.compile("alt=\"(.*?)\"height=\"")
            val matcherImg = patternIng.matcher(splitContent)
            val matcherName = patternName.matcher(splitContent)

            while (matcherImg.find()) {
                urls.add(matcherImg.group(1))
            }
            while (matcherName.find()) {
                names.add(matcherName.group(1))
            }
//            for (s in urls) {
//                Log.i("MyResult", s)
//            }
        } catch (e: Exception) {
            e.message
        }
    }

    private fun playGame() {
        generateQuestion()
        val task = DownloadImageTask()
        try {
            val bitmap = task.execute(urls[numberOfQuestion]).get()
            if (bitmap != null) {
                ivImageStar.setImageBitmap(bitmap)
                for (i in 0..buttons.size) {
                    if (i == numberOfRightAnswer) {
                        buttons[i].text = names[numberOfQuestion]
                    } else {
                        val wrongAnswer = generateWrongAnswer()
                        buttons[i].text = names[wrongAnswer]
                    }
                }
            }
        } catch (e: Exception) {
            e.message
        }
    }

    private fun generateQuestion() {
        numberOfQuestion = (Math.random() * names.size).toInt()
        numberOfRightAnswer = (Math.random() * buttons.size).toInt()
    }

    private fun generateWrongAnswer(): Int {
        return (Math.random() * names.size).toInt()
    }

    private class DownloadContentTask: AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String?): String? {
            val result = StringBuilder()
            var url: URL? = null
            var urlConnection: HttpURLConnection? = null
            try {
                url = URL(params[0])
                urlConnection = url.openConnection() as HttpURLConnection
                val streamInp = urlConnection.inputStream
                val reader = InputStreamReader(streamInp)
                val bufferedReader = BufferedReader(reader)
                var line = bufferedReader.readLine()
                while (line != null) {
                    result.append(line)
                    line = bufferedReader.readLine()
                }
                return result.toString()
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }catch (e: Exception) {
                println(e.message)
            } finally {
                urlConnection?.disconnect()
            }
            return null
        }
    }

    private class DownloadImageTask: AsyncTask<String, Void, Bitmap>() {

        override fun doInBackground(vararg params: String?): Bitmap? {
            var url: URL? = null
            var urlConnection: HttpURLConnection? = null
            try {
                url = URL(params[0])
                urlConnection = url.openConnection() as HttpURLConnection
                val streamInp = urlConnection.inputStream
                return BitmapFactory.decodeStream(streamInp)
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                println(e.message)
            } finally {
                urlConnection?.disconnect()
            }
            return null
        }
    }

    fun onClickAnswer(view: View) {
        val button = view as Button
        val tag = button.tag.toString()
        if (tag.toInt()-1 == numberOfRightAnswer) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Incorrect, the correct answer is: ${names[numberOfQuestion]}", Toast.LENGTH_SHORT).show()
        }
        playGame()
    }
}