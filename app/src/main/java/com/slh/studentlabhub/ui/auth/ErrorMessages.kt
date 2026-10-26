package com.slh.studentlabhub.ui.auth

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.slh.studentlabhub.data.remote.ClassroomApiException
import com.slh.studentlabhub.data.remote.UnauthorizedException
import java.io.IOException
import com.slh.studentlabhub.R
import com.slh.studentlabhub.i18n.Strings

fun Throwable.toUserMessage(): String = when (this) {
    is ApiException -> when (statusCode) {
        CommonStatusCodes.DEVELOPER_ERROR ->
            Strings.get(R.string.error_google_config)
        CommonStatusCodes.CANCELED -> Strings.get(R.string.sign_in_cancelled)
        CommonStatusCodes.NETWORK_ERROR -> Strings.get(R.string.error_no_internet)
        else -> Strings.get(R.string.error_google_code, statusCode)
    }
    is UnauthorizedException -> Strings.get(R.string.error_session_expired)
    is ClassroomApiException -> when (code) {
        403 -> Strings.get(R.string.error_classroom_403)
        else -> Strings.get(R.string.error_classroom_code, code)
    }
    is IOException -> Strings.get(R.string.error_no_internet)
    else -> message ?: Strings.get(R.string.error_unknown)
}