package co.llanox.alacartaexpress.admin.mvp

import co.llanox.alacartaexpress.mobile.data.BackendException

interface BaseView {
    fun showBackendError(error: BackendException?)
}