package jsy.sample.testwalkietalkie.messageEnum

import jsy.sample.testwalkietalkie.R
import jsy.sample.testwalkietalkie.application.MyApplication


enum class ToastEnum(var messageResource : Int?) {

    None(null),
    CustomMessage(null),
    PlzPrepareStream(R.string.plz_prepare_stream),
    ConnectionStarted(R.string.connection_started),
    ConnectionSuccess(R.string.connection_success),
    ConnectionFailed(R.string.connection_failed),
    Disconnected(R.string.disconnected),
    AuthSuccess(R.string.auth_success),
    AuthError(R.string.auth_error),
    Retry(R.string.retry),
    ;

    override fun toString(): String {
        return MyApplication.instance.applicationContext.getString(messageResource!!)
    }


}