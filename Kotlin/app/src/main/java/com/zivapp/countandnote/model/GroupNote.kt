package com.zivapp.countandnote.model

open class GroupNote() : Note() {
    open var member: String = ""
    open var date: String = ""
    open var group_id: String = ""
}