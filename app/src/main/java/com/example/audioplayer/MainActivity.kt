package com.example.audioplayer

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import java.util.concurrent.atomic.AtomicReference

class MainActivity : AppCompatActivity() {
    private val dialog = AskPermissionsDialog(::requestPerm);

    private val hasPermission

        get() = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    private var actualService: AtomicReference<PlayerService?> =
        AtomicReference(null)

    val playerService by lazy {
        PlayerServiceWrapper(this, AudioFilesViewModel(), actualService)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindService(
            Intent(this, PlayerService::class.java),
            PlayerServiceConnection(),
            Context.BIND_AUTO_CREATE
        )
        setContentView(R.layout.activity_main)
        //goToPermissionSettings()
        if (hasPermission) showAppFragment()
        else {
            requestPerm()
            val transaction = supportFragmentManager.beginTransaction()
            val fragmentPerm = PermissionsFragment()
            transaction.add(R.id.fragment_container, fragmentPerm)
                .addToBackStack(null)
                .commit()
        }
    }
    private fun requestPerm() = ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
        0
    )

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val permIdx = permissions.indexOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val result = grantResults.getOrElse(permIdx) {
            PackageManager.PERMISSION_DENIED
        }
// Si nous avons la permission, nous affichons l'application
        if(result == PackageManager.PERMISSION_GRANTED) {
            showAppFragment()
        }
// Sinon, si le système nous indique que nous
// devons afficher un message alors nous l'affichons
        else {
            val showRequestRationnale = ActivityCompat
                .shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            if(showRequestRationnale) dialog.show(
                supportFragmentManager,
                AskPermissionsDialog::class.simpleName
            )
        }
    }

    override fun onResume() {
        super.onResume()
        val currentFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container)
        if(currentFragment !is AudioFileListFragment && hasPermission)
            showAppFragment()
    }

    fun showAppFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = AudioFileListFragment()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    class AskPermissionsDialog(
        private val requestPerm: () -> Unit
    ): DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                // Use the Builder class for convenient dialog construction
                val builder = AlertDialog.Builder(it)
                builder.setMessage(R.string.condition)
                    .setPositiveButton(R.string.ok,
                        DialogInterface.OnClickListener { dialog, id ->
                            requestPerm()
                        })
                // Create the AlertDialog object and return it
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }
    }
    inner class PlayerServiceConnection : ServiceConnection {
        override fun onServiceConnected(
            name: ComponentName?,
            service: IBinder?
        ) = actualService.set(
            (service as? PlayerService.Binder)?.service
        )
        override fun onServiceDisconnected(name: ComponentName?) =
            actualService.set(null)
    }
}