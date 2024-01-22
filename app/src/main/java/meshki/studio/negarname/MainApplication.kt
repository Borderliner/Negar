package meshki.studio.negarname

import android.app.Application
import android.content.Context
import meshki.studio.negarname.di.appModule
import meshki.studio.negarname.di.dbModule
import meshki.studio.negarname.di.repositoryModule
import meshki.studio.negarname.di.systemModule
import meshki.studio.negarname.di.viewModelModule
import org.acra.config.dialog
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import timber.log.Timber

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(
                listOf(
                    dbModule,
                    viewModelModule,
                    repositoryModule,
                    systemModule,
                    appModule,
                )
            )
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        stopKoin()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

        if (!BuildConfig.DEBUG) {
            initAcra {
                //core configuration:
                buildConfigClass = BuildConfig::class.java
                reportFormat = StringFormat.JSON
                //each plugin you chose above can be configured in a block like this:
                dialog {
                    //required
                    text = getString(R.string.crash_report_text)
                    //optional, enables the dialog title
                    title = getString(R.string.crash_report_title)
                    //defaults to android.R.string.ok
                    positiveButtonText = getString(R.string.ok)
                    //defaults to android.R.string.cancel
                    negativeButtonText = getString(R.string.cancel)
                    //optional, enables the comment input
                    commentPrompt = getString(R.string.crash_report_comment)
                    //optional, enables the email input
                    emailPrompt = getString(R.string.crash_report_email)
                    //defaults to android.R.drawable.ic_dialog_alert
                    resIcon = R.drawable.pulse_alert
                    //optional, defaults to @android:style/Theme.Dialog
                    resTheme = R.style.Theme_Negarname
                    //allows other customization
                    // reportDialogClass = MyCustomDialog::class.java
                }
            }
        }
    }
}
