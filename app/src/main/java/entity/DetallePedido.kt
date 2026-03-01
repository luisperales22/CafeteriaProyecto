package entity

data class DetallePedido(

    val id: Int = 0,
    val idPedido: Int = 0,
    val idProducto: Int = 0,
    val productoNombre: String = "",
    val cantidad: Int = 0,
    val precioUnitario: Double = 0.0,
    val subtotal: Double = 0.0

)