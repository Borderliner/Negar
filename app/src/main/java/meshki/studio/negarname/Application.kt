package meshki.studio.negarname

import android.app.Application
import io.embrace.android.embracesdk.Embrace
import meshki.studio.negarname.di.appModule
import meshki.studio.negarname.di.storageModule
import meshki.studio.negarname.di.databaseModule
import meshki.studio.negarname.di.repositoryModule
import meshki.studio.negarname.di.serviceModule
import meshki.studio.negarname.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import timber.log.Timber

class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }

        Embrace.getInstance().start(this)

        startKoin {
            androidLogger()
            androidContext(this@Application)
            modules(
                listOf(
                    appModule,
                    serviceModule,
                    storageModule,
                    databaseModule,
                    repositoryModule,
                    viewModelModule,
                )
            )
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        stopKoin()
    }
}
