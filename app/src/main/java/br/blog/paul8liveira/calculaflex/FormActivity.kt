package br.blog.paul8liveira.calculaflex

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import br.blog.paul8liveira.calculaflex.model.CarData
import br.blog.paul8liveira.calculaflex.utils.CalculaFlexTracker
import br.blog.paul8liveira.calculaflex.utils.DatabaseUtils
import br.blog.paul8liveira.calculaflex.utils.RemoteConfig
import br.blog.paul8liveira.calculaflex.watchers.DecimalTextWatcher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_form.*

class FormActivity : BaseActivity() {
    private lateinit var userId: String
    private lateinit var mAuth: FirebaseAuth
    private val firebaseReferenceNode = "CarData"
    private val defaultClearValueText = "0.0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        mAuth = FirebaseAuth.getInstance()

        userId = FirebaseAuth.getInstance().currentUser!!.uid

        listenerFirebaseRealtime()
        loadBanner()

        etGasPrice.addTextChangedListener(DecimalTextWatcher(etGasPrice))
        etEthanolPrice.addTextChangedListener(DecimalTextWatcher(etEthanolPrice))
        etGasAverage.addTextChangedListener(DecimalTextWatcher(etGasAverage, 1))
        etEthanolAverage.addTextChangedListener(DecimalTextWatcher(etEthanolAverage, 1))

        btCalculate.setOnClickListener {
            saveCarData()
            val proximaTela = Intent(this@FormActivity, ResultActivity::class.java)
            proximaTela.putExtra("GAS_PRICE", etGasPrice.text.toString().toDouble())
            proximaTela.putExtra("ETHANOL_PRICE", etEthanolPrice.text.toString().toDouble())
            proximaTela.putExtra("GAS_AVERAGE", etGasAverage.text.toString().toDouble())
            proximaTela.putExtra("ETHANOL_AVERAGE", etEthanolAverage.text.toString().toDouble())

            sendDataToAnalytics()
            startActivity(proximaTela)
        }

    }

    private fun sendDataToAnalytics() {
        val bundle = Bundle()
        bundle.putString("EVENT_NAME", "CALCULATION")
        bundle.putDouble("GAS_PRICE", etGasPrice.text.toString().toDouble())
        bundle.putDouble("ETHANOL_PRICE", etEthanolPrice.text.toString().toDouble())
        bundle.putDouble("GAS_AVERAGE", etGasAverage.text.toString().toDouble())
        bundle.putDouble("ETHANOL_AVERAGE", etEthanolAverage.text.toString().toDouble())

        CalculaFlexTracker.trackEvent(this, bundle)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.form_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.action_clear -> {
                clearData()
                return true
            }
            R.id.action_logout -> {
                DatabaseUtils.saveToken("")
                logout()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        mAuth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun clearData() {
        etGasPrice.setText(defaultClearValueText)
        etEthanolPrice.setText(defaultClearValueText)
        etGasAverage.setText(defaultClearValueText)
        etEthanolAverage.setText(defaultClearValueText)
    }

    private fun saveCarData() {
        val carData = CarData(
            etGasPrice.text.toString().toDouble(),
            etEthanolPrice.text.toString().toDouble(),
            etGasAverage.text.toString().toDouble(),
            etEthanolAverage.text.toString().toDouble()
        )

        FirebaseDatabase.getInstance().getReference(firebaseReferenceNode)
            .child(userId)
            .setValue(carData)
        //.addOnCompleteListener {}
    }

    private fun listenerFirebaseRealtime() {
        DatabaseUtils.getDatabase().getReference(firebaseReferenceNode)
            .child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val carData = dataSnapshot.getValue(CarData::class.java)
                    etGasPrice.setText(carData?.gasPrice.toString())
                    etEthanolPrice.setText(carData?.ethanolPrice.toString())
                    etGasAverage.setText(carData?.gasAverage.toString())
                    etEthanolAverage.setText(carData?.ethanolAverage.toString())
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun loadBanner() {
        val loginBanner = RemoteConfig.getFirebaseRemoteConfig()
            .getString("banner_image")

        Picasso.get().load(loginBanner).into(ivBanner)
    }

}
