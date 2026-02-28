package entity

data class Pedido(

    var id: String = "",
    var numeroMesa: Int = 0,
    var items: List<ItemPedido> = listOf(),
    var total: Double = 0.0,
    var metodoPago: String = "",
    var fecha: String = "",
    var estado: String = "Pagado"

)