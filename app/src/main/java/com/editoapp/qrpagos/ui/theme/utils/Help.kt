package com.editoapp.qrpagos.ui.theme.utils

class Help {


    companion object {
        fun FormatAmount(amount: Double):String{
            return "%,.2f".format(amount)
        }
    }
}