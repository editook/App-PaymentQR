package com.editoapp.qrpago.data

object HelpText {

    object Actions {
        const val NUEVO_PAY = "nuevo_pay"
        const val SINCRONIZAR = "sincronizar_reuniones"
    }

    object DataKeys {
        const val TITLE = "title"
        const val VALUE = "value"
    }

    object Channels {
        const val PAGOS = "pagos_channel"
        const val DEPOSITOS = "depositos_channel"
    }

    object Notifications {
        const val TITLE_PAGO = "Nuevo pago recibido"
        const val TITLE_DEPOSITO = "Nuevo depósito detectado"
    }

    object WorkNames {
        const val MONITOR_DISPLAY = "monitor_display"
        const val MONITOR_DEPOSITOS = "monitor_depositos"
    }
}