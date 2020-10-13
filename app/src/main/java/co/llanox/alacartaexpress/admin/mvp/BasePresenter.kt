package co.llanox.alacartaexpress.admin.mvp

abstract class BasePresenter<V : BaseView?> {
    protected var view: V? = null

    fun attachView(view: V) {
        this.view = view
    }

    fun detachView() {
        view = null
    }


    protected abstract fun init()
}