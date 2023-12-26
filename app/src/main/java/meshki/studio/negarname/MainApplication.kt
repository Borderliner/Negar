package meshki.studio.negarname

import android.app.Application
import meshki.studio.negarname.di.appModule
import meshki.studio.negarname.di.dbModule
import meshki.studio.negarname.di.repositoryModule
import meshki.studio.negarname.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(
                listOf(
                    dbModule,
                    viewModelModule,
                    repositoryModule,
                    appModule,
                )
            )
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        stopKoin()
    }
}
