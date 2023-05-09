package com.example.recycleadapterholder

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.util.Collections
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory


class MainActivity : AppCompatActivity(), RecyclerSaintAdapter.OnItemClickListener,
    RecyclerSaintAdapter.OnItemLongClickListener {
    private val data: MutableList<Saint> = ArrayList<Saint>()
    private var recycler: RecyclerView? = null

    // private List<String> saints = new ArrayList<>();
    // private ArrayAdapter<String> adapter;
    // private SaintAdapter adapter;
    private var adapter: RecyclerSaintAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recycler = findViewById<View>(R.id.list) as RecyclerView

        // Источник данных для парсера XML из ресурсов
        val mySaints = InputSource(resources.openRawResource(R.raw.saints))
        // Новый XPath запрос
        val xPath = XPathFactory.newInstance().newXPath()

        // Подробно об XPath
        // http://www.w3schools.com/xsl/xpath_syntax.asp

        // Собственно запрос
        val expression = "/saints/saint"
        val nodes: NodeList
        try {
            // Результат XPath запроса - набор узлов
            nodes = xPath.evaluate(expression, mySaints, XPathConstants.NODESET) as NodeList
            if (nodes != null) {
                val numSaints = nodes.length
                // Для каждого из узлов
                for (i in 0 until numSaints) {
                    // Узел
                    val saint = nodes.item(i)

                    // Дочерние элементы
                    val name = saint.firstChild.textContent
                    val dob = saint.childNodes.item(1).textContent
                    val dod = saint.childNodes.item(2).textContent
                    Log.d("happy", name)
                    val s = Saint(name, dob, dod, 0f)

                    // saints.add(name);
                    data.add(s)
                }
            }
        } catch (e: Exception) {
        }

        // Стандартный ArrayAdapter - подходит для 1 или 2 текстовых полей
        /*
        adapter = new ArrayAdapter<String>(
                this,
                R.layout.saint,
                R.id.text,
                saints
        );
        */

        // Нестадартный адаптер - 3 текстовых поля, рейтинг и картинка с листенером
        // adapter = new SaintAdapter(this, R.layout.listviewitem, data);
        adapter = RecyclerSaintAdapter(data)
        recycler!!.adapter = adapter
        recycler!!.layoutManager = LinearLayoutManager(this)
        recycler!!.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        adapter!!.setOnItemClickListener(this)
        adapter!!.setOnItemLongClickListener(this)
        val helper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    adapter!!.remove(position)
                }
            }
        })
        helper.attachToRecyclerView(recycler)

        // recycler.setOnItemClickListener(this);

        // Чтобы подписать ListView на стандартное контекстное меню по "длинному" щелчку
        // на элемент
        // registerForContextMenu(list);

        // Чтобы запустить мульти селект, нужно один раз
        // "долго" нажать на элемент listview, поэтому
        // регистрируем listener.
        // При этом прекращает работать
        // контекстное меню для элемента listview.
        // list.setOnItemLongClickListener(this);
    }

    /*

// Вызывается при создании контекстного меню
@Override
public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    getMenuInflater().inflate(R.menu.context, menu);
    super.onCreateContextMenu(menu, v, menuInfo);
}

// Вызывается при выборе элемента контекстного меню
@Override
public boolean onContextItemSelected(MenuItem item) {
    AdapterView.AdapterContextMenuInfo info =
            (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    int position = info.position;

//        String s = saints.get(position);
//        //Snackbar.make(list, "Deleted: "+ s, Snackbar.LENGTH_SHORT).show();
//        Toast.makeText(this, "Deleted: "+ s, Toast.LENGTH_SHORT).show();

    Saint s = data.get(position);
    String name = s.getName();
    Toast.makeText(this, "Deleted: "+ name, Toast.LENGTH_SHORT).show();

    //saints.remove(position);
//        data.remove(position);
//        adapter.notifyDataSetChanged();

    Saint saint = adapter.getItem(position);
    adapter.remove(saint);

    return super.onContextItemSelected(item);
}

*/
    // Вызывается при создании меню
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Меню рисуется по-разному если есть выделенные элементы
        // и если выделенных элементов нет.
        if (adapter!!.hasSelected()) {
            menuInflater.inflate(R.menu.delete, menu)
        } else {
            menuInflater.inflate(R.menu.main, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    // Вызывается при выборе элемента меню
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_up -> {
                //Collections.sort(saints);
                // Чтобы отсортировать контейнер кастомного класса таким образом
                // класс должен имплементить интерфейс Comparable
                Collections.sort(data)
                adapter!!.notifyDataSetChanged()
                return true
            }

            R.id.menu_down -> {
                //Collections.sort(saints, Collections.<String>reverseOrder());
                Collections.sort(data, Collections.reverseOrder<Saint>())
                adapter!!.notifyDataSetChanged()
                return true
            }

            R.id.menu_add -> {
                showAddDialog()
                return true
            }

            R.id.main_delete -> {
                adapter!!.deleteSelected()
                // После удалении выделенных элементов
                // нужно перерисовать меню.
                invalidateOptionsMenu()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Вызывается по нажатию на "+" меню
    @SuppressLint("MissingInflatedId")
    private fun showAddDialog() {
        // Для создания кастомного диалога нужно создать Layout XML файл
        // "Надуваем" view диалога используя xml файл
        val dialog: View = layoutInflater.inflate(R.layout.dialog_add, null)

        // Находим EditText используя только что "надутый" view
        val text = dialog.findViewById(R.id.dialog_add) as EditText

        // Создаем диалог используя Builder шаблон
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setView(dialog)
            .setTitle("Add a Saint!") // Заголовок диалога
            // "Отрицательная" кнопка и ее Listener
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel() // Просто закрываем диалог
            }) // "Положительная" кнопка - нужно добавить Святого
            .setPositiveButton(
                "Create",
                DialogInterface.OnClickListener { dialog, which -> // Получаем имя
                    val saint = text.text.toString()
                    // Создаем святого и добавляем его в контейнер
                    // Закрываем диалог
                    createSaint(Saint(saint, "", "", 0f))
                    dialog.dismiss()
                })
            .create() // Создаем диалог из builder-а
            .show() // Показываем его
    }

    private fun createSaint(saint: Saint) {
        // Добавляем Святого в контейнер
        data.add(saint)
        // Просим адаптер обновиться
        adapter!!.notifyDataSetChanged()
    }

    // Детальная Activity закрылась, получим ее результат
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var id = -1
        var rating = -1f
        // Проверяем Intent
        if (data != null) {
            // Проверяем, есть ли нужные данные
            if (data.hasExtra(SAINT_ID)) {
                // Если есть, получаем их
                id = data.getIntExtra(SAINT_ID, -1)
            }
            // Проверяем, есть ли нужные данные
            if (data.hasExtra(SAINT_RATING)) {
                // Если есть, получаем их
                rating = data.getFloatExtra(SAINT_RATING, -1f)
            }
            // Если данные валидны, обновляем запись в контейнере
            if (id >= 0 && rating >= 0f) {
                this.data[id].rating = rating
                // Уведомляем адаптер о том, что данные в контейнере
                // могли измениться
                adapter!!.notifyDataSetChanged()
            }
        }
    }

    override fun onItemClick(itemView: View?, position: Int) {
        // Работает по-разному в зависимости от того, выделены ли
        // какие-нибудь элементы listview.
        if (!adapter!!.hasSelected()) {
            // Если выделенных элементов нет,
            // покажем активность с информацией.
            val s: Saint = data[position]
            val saint: String = s.name

            // Создаем Intent на запуск детальной Activity
            val intent = Intent(this, SaintDetail::class.java)

            // Добавляем туда нужные данные - имя, номер в контейнере и рейтинг
            intent.putExtra(SAINT_NAME, saint)
            intent.putExtra(SAINT_ID, position)
            intent.putExtra(SAINT_RATING, s.rating)

            // Запускаем Activity и подписываемся на получение результата
            startActivityForResult(intent, RATING_REQUEST)
        } else {
            // Если есть выделенные элементы, будем
            // изментять статус того, по которому пользователь
            // "щелкнул".
            toggleSelection(position)
        }
    }

    // Если изменяется статус выбранности элемента.
    private fun toggleSelection(pos: Int) {
        // Уведомляем адаптер о том, что статус выделенности элемента изменился.
        adapter!!.toggleSelection(pos)
        // При этом нужно вызывать пересоздание меню, так как пользователь
        // мог убрать выделение со всех элементов.
        invalidateOptionsMenu()
    }

    override fun onItemLongClick(itemView: View?, position: Int) {
        toggleSelection(position)
    } /*


    // Обработка "длинного" клика
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        toggleSelection(position);
        return true;
    }
    */

    companion object {
        // Константы для передачи данных через Intent
        // Доджны быть доступны "детальной" Activity
        const val SAINT_NAME = "SAINT_NAME"
        const val SAINT_ID = "SAINT_ID"
        const val SAINT_RATING = "SAINT_RATING"
        const val RATING_REQUEST = 777
    }
}