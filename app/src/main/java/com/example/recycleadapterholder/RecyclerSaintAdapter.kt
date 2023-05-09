package com.example.recycleadapterholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections


class RecyclerSaintAdapter(saints: List<Saint>) :
    RecyclerView.Adapter<SaintHolder>() {
    fun ratingChanged(position: Int, newRating: Float) {
        saints[position].rating = newRating
        // notifyItemChanged(position);
    }

    interface OnItemClickListener {
        fun onItemClick(itemView: View?, position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(itemView: View?, position: Int)
    }

    private var clickListener: OnItemClickListener? = null
    private var longClickListener: OnItemLongClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        clickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener?) {
        longClickListener = listener
    }

    private val selection = HashSet<Int>()
    private val saints: MutableList<Saint>

    init {
        this.saints = saints as MutableList<Saint>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaintHolder {
        val inflater = LayoutInflater.from(parent.context)
        var view: View? = null
        when (viewType) {
            0 -> view = inflater.inflate(R.layout.listviewitem, parent, false)
            1 -> view = inflater.inflate(R.layout.listviewitemselected, parent, false)
        }
        return SaintHolder(view!!, this, clickListener, longClickListener!!)
    }

    fun remove(pos: Int) {
        selection.remove(pos)
        saints.removeAt(pos)
        notifyItemRemoved(pos)
    }

    override fun onBindViewHolder(holder: SaintHolder, position: Int) {
        val s = saints[position]

        // Актуализируем данные View через ссылки ViewHolder
        holder.name.text = s.name
        holder.dob.text = s.dob
        holder.dod.text = s.dod
        holder.bar.rating = s.rating
    }

    override fun getItemCount(): Int {
        return saints.size
    }

    // Хелпер чтобы определить, выделены ли
    // какие-нибудь элементы listview.
    fun hasSelected(): Boolean {
        return selection.isNotEmpty()
    }

    // Хелпер - выделен ли конкретный элемент.
    fun isSelected(position: Int): Boolean {
        return selection.contains(position)
    }

    // Выделение - если элемент выделен, сделать не выделенным;
    // если не выделен - выделить.
    // В любом случае при изменении статуса уведомить об этом
    // адаптер.
    fun toggleSelection(position: Int) {
        if (isSelected(position)) {
            selection.remove(position)
        } else {
            selection.add(position)
        }
        notifyItemChanged(position)
    }

    // Удалить выделенные элементы
    fun deleteSelected() {
        val items: MutableList<Int> = ArrayList()

        // Вначале формируем List из сета.
        items.addAll(selection)

        // Обратно сортируем этот лист, чтобы
        // вначале удалять элемент с самым большим
        // номером.
        Collections.sort(items)
        Collections.reverse(items)

        // Удаляем как сам элемент так и
        // его признак выделенности.
        for (i in items) {
            selection.remove(i)
            saints.removeAt(i)
        }

        // Уведомляем об этом адаптер.
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (isSelected(position)) 1 else 0
    }
}
