package com.messenger.toaster.exception

class ExceptionMessage(private val message: String?) {
    fun getMessage():String{
        return message ?: "Неизвестная ошибка"
    }
}