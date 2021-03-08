package martins.caio.listacontatos.singleton

import martins.caio.listacontatos.model.ContatosVO

object ContatoSingleton {
    var lista: MutableList<ContatosVO> = mutableListOf()
}