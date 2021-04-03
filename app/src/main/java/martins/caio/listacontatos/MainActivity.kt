package martins.caio.listacontatos

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock.sleep
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_contato.*
import martins.caio.listacontatos.adapter.ContatoAdapter
import martins.caio.listacontatos.application.ContatoApplication
import martins.caio.listacontatos.bases.BaseActivity
import martins.caio.listacontatos.contato.ContatoActivity
import martins.caio.listacontatos.model.ContatosVO
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_contato.*
import java.lang.Exception


class MainActivity : BaseActivity() {

    private var adapter: ContatoAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolBar(toolBarMain, "Contatos",false)
        setupListView()
        setupOnClicks()
    }

    private fun setupOnClicks(){
        fabAdd.setOnClickListener { onClickAdd() }
        etBuscar.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {onClickBuscar() }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
        })
    }

    private fun setupListView(){
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        onClickBuscar()
    }

    override fun onPause() {
        super.onPause()
        Log.i("onPause: ", "Pause")
    }

    private fun onClickAdd(){
        val intent = Intent(this, ContatoActivity::class.java)
        startActivity(intent)
    }

    private fun onClickItemRecyclerView(index: Int){
        val intent = Intent(this, ContatoActivity::class.java)
        intent.putExtra("index", index)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    private fun onClickBuscar(){
        val busca = etBuscar.text.toString()

        recyclerView.visibility = View.GONE

        shimmerView.visibility = View.VISIBLE
        shimmerView.startShimmer()
        shimmerView.showShimmer(true)


        Thread(Runnable {
            Thread.sleep(2000)

            var listaFiltrada: List<ContatosVO> = mutableListOf()
            try {
                listaFiltrada = ContatoApplication.instance.helperDB?.buscarContatos(busca) ?: mutableListOf()
            }catch (ex: Exception){
                ex.printStackTrace()
            }

            runOnUiThread {

                adapter = ContatoAdapter(this,listaFiltrada.sortedBy { it.nome.toLowerCase()}) {onClickItemRecyclerView(it)}
                recyclerView.adapter = adapter

                recyclerView.visibility = View.VISIBLE
                shimmerView.visibility = View.GONE
            }
        }).start()

    }
}
