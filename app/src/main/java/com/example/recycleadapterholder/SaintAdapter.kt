package com.example.recycleadapterholder

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.RatingBar
import android.widget.TextView
import java.util.Collections


class SaintAdapter(
    private val context: Context,
    private val resource: Int,
    private val data: MutableList<Saint>
) :
    ArrayAdapter<Saint?>(context, resource) {
    private val inflater: LayoutInflater

    // Сет для хранения номеров выбранных элементов
    private val selection: HashSet<Int>? = null

    // Так как класс расширяет ArrayAdapter
    // он должен иметь перегруженный конструктор в котором вызывать
    // один из конструкторов суперкласса
    init {
        inflater = LayoutInflater.from(context)
    }

    // Говорит listview, сколько будет типов элементов
    override fun getViewTypeCount(): Int {
        return 2
    }

    // Хелпер чтобы определить, выделены ли
    // какие-нибудь элементы listview.
    fun hasSelected(): Boolean {
        return !selection!!.isEmpty()
    }

    // Хелпер - выделен ли конкретный элемент.
    fun isSelected(position: Int): Boolean {
        return selection!!.contains(position)
    }

    // Выделение - если элемент выделен, сделать не выделенным;
    // если не выделен - выделить.
    // В любом случае при изменении статуса уведомить об этом
    // адаптер.
    fun toggleSelection(position: Int) {
        if (isSelected(position)) {
            selection!!.remove(position)
        } else {
            selection!!.add(position)
        }
        notifyDataSetChanged()
    }

    // Количество элементов в контейнере
    override fun getCount(): Int {
        return data.size
    }

    // Удалить выделенные элементы
    fun deleteSelected() {
        val items: MutableList<Int> = ArrayList()

        // Вначале формируем List из сета.
        items.addAll(selection!!)

        // Обратно сортируем этот лист, чтобы
        // вначале удалять элемент с самым большим
        // номером.
        Collections.sort(items)
        Collections.reverse(items)

        // Удаляем как сам элемент так и
        // его признак выделенности.
        for (i in items) {
            selection.remove(i)
            data.removeAt(i)
        }

        // Уведомляем об этом адаптер.
        notifyDataSetChanged()
    }

    // Шаблон ViewHolder
    // Подробнее https://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
    internal class Holder {
        var name: TextView? = null
        var dob: TextView? = null
        var dod: TextView? = null
        var bar: RatingBar? = null
        var button: ImageView? = null
    }

    // Возвращает тип элемента
    override fun getItemViewType(position: Int): Int {
        return if (isSelected(position)) 1 else 0
    }

    // Возвращение нового или переопределенного View для ListView
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // View rowView = inflater.inflate(R.layout.listviewitem, parent, false);
        var rowView = convertView
        val holder: Holder

        // Если переданный нам View нулевой, нужно:
        // 1. Создать его
        // 2. Найти ссылки на его поля
        // 3. Создать ViewHolder
        // 4. Присвоить ссылкам ViewHolder ссылки на поля View
        // 5. Созранить созданный ViewHolder в Tag созданного View
        if (rowView == null) {
            // Создаем элемент в зависимости от его типа.
            // ListView сам следить за тем, чтобы
            // передать элемент правильного типа в convertView.
            if (isSelected(position)) rowView =
                inflater.inflate(R.layout.listviewitemselected, parent, false) else rowView =
                inflater.inflate(R.layout.listviewitem, parent, false)
            val name = rowView!!.findViewById<View>(R.id.text) as TextView
            val dob = rowView.findViewById<View>(R.id.dob) as TextView
            val dod = rowView.findViewById<View>(R.id.dod) as TextView
            val bar = rowView.findViewById<View>(R.id.rating) as RatingBar
            val button = rowView.findViewById<View>(R.id.threedots) as ImageView
            holder = Holder()
            holder.name = name
            holder.dob = dob
            holder.dod = dod
            holder.bar = bar
            holder.button = button
            rowView.setTag(holder)
        } else {
            holder = rowView.tag as Holder
        }

        // View любо пустой, либо содержит уже не актуальные данные
        // Загрузим Святого
        val s = data[position]

        // Актуализируем данные View через ссылки ViewHolder
        holder.name!!.text = s.name
        holder.dob!!.text = s.dob
        holder.dod!!.text = s.dod
        holder.bar!!.rating = s.rating
        if (hasSelected()) holder.button!!.visibility =
            View.INVISIBLE else holder.button!!.visibility =
            View.VISIBLE

        // Реакция на click на картинку
        holder.button!!.setOnClickListener { v -> // Отобразим popup меню.
            showPopupMenu(context, v, position)
        }
        return rowView
    }

    // Хелпер для запуска popup меню.
    // Оно используется так как
    // если зарегистрировать для listview "длинный" клик,
    // контекстное меню больше не показывается.
    private fun showPopupMenu(con: Context, v: View, pos: Int) {
        // Отображаем меню только если никакой элемент не выбран.
        if (!hasSelected()) {
            val popupMenu = PopupMenu(con, v)
            popupMenu.inflate(R.menu.context)
            popupMenu
                .setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem): Boolean {
                        when (item.itemId) {
                            R.id.context_delete -> {
                                selection!!.remove(pos)
                                data.removeAt(pos)
                                notifyDataSetChanged()
                                return true
                            }
                        }
                        return false
                    }
                })
            popupMenu.show()
        }
    }
}
