package com.zivapp.countandnote.model

data class User(
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var id: String = "",
    var selected: Boolean = false
)