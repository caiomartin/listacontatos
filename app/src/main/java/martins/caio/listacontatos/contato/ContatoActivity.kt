package  martins.caio.listacontatos.contato

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import martins.caio.listacontatos.R
import martins.caio.listacontatos.application.ContatoApplication
import martins.caio.listacontatos.bases.BaseActivity
import martins.caio.listacontatos.model.ContatosVO
import kotlinx.android.synthetic.main.activity_contato.*
import kotlinx.android.synthetic.main.activity_main.*


class ContatoActivity : BaseActivity() {

    private var idContato: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contato)
        setupToolBar(toolBar,"Contato", navgationBack = true)
        setupContato()
        setupOn()

    }

    private fun setupOn(){
        btnSalvarConato.setOnClickListener { onClickSalvarContato() }
        btnQrCodeContato.setOnClickListener { gerarQrCode() }

        ccp.registerCarrierNumberEditText(etTelefone)

    }

    private fun setupContato(){
        idContato = intent.getIntExtra("index",-1)
        if (idContato == -1){
            btnExcluirContato.visibility = View.GONE
            btnQrCodeContato.visibility = View.GONE
            btnSalvarConato.text = "Salvar"
            return
        }
        loadContato()

    }

    private fun loadContato() {
        progress.visibility = View.VISIBLE
        ccp.setCountryForPhoneCode(0)
        Thread(Runnable {
            Thread.sleep(1500)
            var lista = ContatoApplication.instance.helperDB?.buscarContatos("$idContato",true) ?: return@Runnable
            var contato = lista.getOrNull(0) ?: return@Runnable
            runOnUiThread {
                etNome.setText(contato.nome)
                ccp.setCountryForPhoneCode(contato.code.toInt())
                etTelefone.setText(contato.telefone)
                progress.visibility = View.GONE
            }
        }).start()
    }

    private fun onClickSalvarContato(){
        closeTeclado()
        val nome = etNome.text.toString().trim()
        val code = ccp.selectedCountryCode
        val telefone = etTelefone.text.toString().trim()

        if (nome.isEmpty() || telefone.isEmpty()){
            val snackBar = Snackbar.make(contatoConstraintlayout,"Preencher Nome e Telefone!",Snackbar.LENGTH_LONG)
            snackBar.view.setBackgroundColor(Color.parseColor("#F44336"))
            snackBar.setTextColor(Color.parseColor("#ffe2ff"))
            snackBar.show()

        }else {
            val contato = ContatosVO(
                idContato,
                code,
                nome,
                telefone
            )
            Thread(Runnable {
                Thread.sleep(1500)

                if (idContato == -1) {
                    ContatoApplication.instance.helperDB?.salvarContato(contato)
                } else {
                    ContatoApplication.instance.helperDB?.updateContato(contato)
                }
                runOnUiThread {
                    if (idContato == -1) {
                        progress.visibility = View.GONE
                        finish()
                    }else {
                        progress.visibility = View.GONE
                        gerarQrCode()
                    }

                }
            }).start()

            val snackBar = Snackbar.make(contatoConstraintlayout,"Contato salvo com sucesso!",Snackbar.LENGTH_LONG)
            snackBar.view.setBackgroundColor(Color.parseColor("#4CAF50"))
            snackBar.setTextColor(Color.parseColor("#ffe2ff"))
            snackBar.show()
        }
    }

    private fun closeTeclado(){
        val view: View? = currentFocus
        view?.let {
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken,0)
        }
    }

    fun onClickExcluirContato(view: View) {
        closeTeclado()
        val snackBar = Snackbar.make(contatoConstraintlayout,"Contato excluído com sucesso!!",Snackbar.LENGTH_LONG)
        snackBar.view.setBackgroundColor(Color.parseColor("#4CAF50"))
        snackBar.setTextColor(Color.parseColor("#ffe2ff"))
        snackBar.show()
        if(idContato > -1){
            Thread(Runnable {
               Thread.sleep(1500)
                ContatoApplication.instance.helperDB?.deletarCoontato(idContato)
                runOnUiThread {
                    progress.visibility = View.GONE
                    finish()
                }
            }).start()
        }
    }

    private fun gerarQrCode() {
        val multiFormatWriter = MultiFormatWriter()

        try {
            val replace = "(?i)[^0-9a-záéíóúàèìòùâêîôûãõç\\\\s]".toRegex()
            val code = ccp.getSelectedCountryCode().toString().trim()
            val telefoneReplace = etTelefone.text.trim().replace(replace, "")

            val bitMatrix: BitMatrix = multiFormatWriter.encode("https://api.whatsapp.com/send?phone=+$code$telefoneReplace&text=", BarcodeFormat.QR_CODE, 300, 300)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap : Bitmap? = barcodeEncoder.createBitmap(bitMatrix)
            ivQrCode.setImageBitmap(bitmap)

        }catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    override fun onRestart() {
        super.onRestart()
        Log.i("State", "onRestart: "+idContato.toString())
        loadContato()
    }
}