package entity

data class Usuario(

    var id: String = "",
    var nombre: String = "",
    var correo: String = "",
    val contra: String = "",
    var rol: String = "mesero"

)