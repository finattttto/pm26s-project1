package com.example.sistemacontrolefinancas.entity

data class Localizacao (
    var _id: Int,
    var longitude : Double,
    var latitude : Double,
    var nome : String ,
    var descricao : String ,
    var dataAdd : String ,
    var imageUri : String
)