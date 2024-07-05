package meshki.studio.negarname.di

import com.google.gson.Gson
import meshki.studio.negarname.data.storage.PersistentStorage
import meshki.studio.negarname.data.storage.Storage
import org.koin.dsl.module

val storageModule = module {
    single<Storage> { PersistentStorage(get()) }
    single { Gson() }
}
