package com.zivapp.countandnote.model

data class MainMenuNote(
    var date: String = "",
    var id: String = "",
    var title: String = "",
    var total_sum: Int = 0,
    var group: Boolean = false,
    var message: String = ""
)