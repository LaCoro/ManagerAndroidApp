package co.llanox.alacartaexpress.admin.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import co.llanox.alacartaexpress.admin.R
import co.llanox.alacartaexpress.admin.presentation.listorders.OrderListActivity
import co.llanox.alacartaexpress.admin.validations.CroutonValidationFailedRenderer
import co.llanox.alacartaexpress.mobile.data.BackendException
import co.llanox.alacartaexpress.mobile.data.NonAuthorizedUserException
import co.llanox.alacartaexpress.mobile.data.RetrievedDataListener
import co.llanox.alacartaexpress.mobile.data.UserAuthentication
import co.llanox.alacartaexpress.mobile.data.UserAuthenticationImpl
import co.llanox.alacartaexpress.mobile.model.User
import ua.org.zasadnyy.zvalidations.Field
import ua.org.zasadnyy.zvalidations.Form
import ua.org.zasadnyy.zvalidations.validations.NotEmpty

class LoginActivity : AppCompatActivity() {
    private var form: Form? = null
    private var username: EditText? = null
    private var password: EditText? = null
    private val userAuthentication: UserAuthentication = UserAuthenticationImpl()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if (userAuthentication.isAuthenticated) {
            goToOrderListActivity()
            return
        }
        form = Form(this)
        form?.validationFailedRenderer = CroutonValidationFailedRenderer(this)
        username = findViewById(R.id.et_email_login)
        password = findViewById(R.id.et_password_login)
        findViewById<View>(R.id.btn_enter).setOnClickListener { view -> signIn(view) }
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (toolbar != null) {
            toolbar.title = this.resources.getString(R.string.app_name)
        }
    }

    fun signIn(view: View?) {
        form?.addField(Field.using(username).validate(NotEmpty.build(this)))
        form?.addField(Field.using(password).validate(NotEmpty.build(this)))
        if (form?.isValid == false) return

        userAuthentication.logIn(username?.text.toString(), password?.text.toString(),
            object : RetrievedDataListener<User> {
                override fun onRetrievedData(data: List<User>) {
                    goToOrderListActivity()
                }

                override fun onError(error: Throwable) {
                    if (error is NonAuthorizedUserException) {
                        Toast.makeText(this@LoginActivity, R.string.err_non_authorized_user, Toast.LENGTH_LONG).show()
                        return
                    }
                    if (error is BackendException) {
                        Toast.makeText(this@LoginActivity, getString(R.string.err_general_error, error.errorCode), Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    private fun goToOrderListActivity() {
        Intent(this@LoginActivity, OrderListActivity::class.java).apply {
            startActivity(this)
            finish()
        }

    }
}