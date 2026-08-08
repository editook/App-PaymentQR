package com.editoapp.qrpago.viewmodel

object AppEvents {

    private val _refreshInfoData = kotlinx.coroutines.flow.MutableSharedFlow<Unit>()
    private val _refreshPayments = kotlinx.coroutines.flow.MutableSharedFlow<Unit>()
    val refreshInfoData = _refreshInfoData
    val refreshPayments = _refreshPayments

    suspend fun triggerRefreshInfoData() {
        _refreshInfoData.emit(Unit)
    }
    suspend fun triggerRefreshPayments() {
        _refreshPayments.emit(Unit)
    }
}