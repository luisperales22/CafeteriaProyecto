package entity

data class DetallePedido(

    var productoNombre: String = "",
    var cantidad: Int = 0,
    var precioUnitario: Double = 0.0,
    var subtotal: Double = 0.0

)