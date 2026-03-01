package entity

data class Pedido(

    val id: Int = 0,
    val idMesa: Int = 0,
    val idUsuario: Int = 0,
    val fecha: String = "",
    val metodoPago: String = "",
    val total: Double = 0.0,
    val estado: String = "activo"

)