package com.example.recycleadapterholder

class Saint(var name: String, var dob: String, var dod: String, rating: Float) :
    Comparable<Saint?> {
    // Для того, чтобы экземпляры класса можно было отсортировать
    // класс должен имплементить Comparable
    override operator fun compareTo(o: Saint?): Int {
        return name.compareTo(o!!.name)
    }

    var rating = 0f

    init {
        this.rating = rating
    }
}
