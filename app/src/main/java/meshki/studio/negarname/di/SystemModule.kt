package meshki.studio.negarname.di

import android.app.AlarmManager
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val systemModule = module {
    single<AlarmManager> { androidContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager }
}
