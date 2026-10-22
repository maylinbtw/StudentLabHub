package com.slh.studentlabhub.ui.auth

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.slh.studentlabhub.data.remote.ClassroomApiException
import com.slh.studentlabhub.data.remote.UnauthorizedException
import java.io.IOException

fun Throwable.toUserMessage(): String = when (this) {
    is ApiException -> when (statusCode) {
        CommonStatusCodes.DEVELOPER_ERROR ->
            "Ошибка настройки Google (код 10): проверь имя пакета и SHA-1 в Google Cloud"
        CommonStatusCodes.CANCELED -> "Вход отменён"
        CommonStatusCodes.NETWORK_ERROR -> "Нет подключения к интернету"
        else -> "Ошибка Google (код $statusCode)"
    }
    is UnauthorizedException -> "Сессия Google истекла. Нажми ещё раз"
    is ClassroomApiException -> when (code) {
        403 -> "Нет доступа к Classroom. Проверь, что API включён и ты в списке тестовых пользователей"
        else -> "Ошибка Classroom (код $code)"
    }
    is IOException -> "Нет подключения к интернету"
    else -> message ?: "Неизвестная ошибка"
}