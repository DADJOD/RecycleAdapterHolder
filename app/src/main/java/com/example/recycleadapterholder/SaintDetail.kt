package com.example.recycleadapterholder

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity


class SaintDetail : AppCompatActivity() {
    private var mSaintWebView: WebView? = null
    private var mSaintRating: RatingBar? = null
    private var mSaintId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saint_detail)

        // Находим элементы управления
        mSaintWebView = findViewById<View>(R.id.saint_detail) as WebView
        mSaintWebView!!.webViewClient = WebViewClient()
        mSaintRating = findViewById<View>(R.id.rating) as RatingBar

        // Получаем Intent, с которым была запущена Activity
        val intent = intent
        if (intent != null) {
            // Получаем из Intent переданные параметры
            var saint = intent.getStringExtra(MainActivity.SAINT_NAME)
            if (saint != null) {
                // Формируем URL для википедии
                saint = saint.replace(" ", "_")
                val url = "https://en.m.wikipedia.org/wiki/$saint"
                mSaintWebView!!.loadUrl(url)
            }

            // Вначале проверяем, есть ли такое значение
            if (intent.hasExtra(MainActivity.SAINT_RATING)) {
                val rating = intent.getFloatExtra(MainActivity.SAINT_RATING, -1f)
                mSaintRating!!.rating = rating
            }
            if (intent.hasExtra(MainActivity.SAINT_ID)) {
                mSaintId = intent.getIntExtra(MainActivity.SAINT_ID, -1)
            }
        }
    }

    // По нажатию на кнопку "Back"
    override fun onBackPressed() {
        // Формируем Intent, который будет возвращен в вызвавшую нас Activity
        val intent = Intent()
        // Добавляем в Intent нужные параметры
        intent.putExtra(MainActivity.SAINT_RATING, mSaintRating!!.rating)
        intent.putExtra(MainActivity.SAINT_ID, mSaintId)

        // Устанавливаем результат
        setResult(RESULT_OK, intent)

        // Вызываем onBackPressed суперкласса, закрывая Activity
        super.onBackPressed()
    }
}
