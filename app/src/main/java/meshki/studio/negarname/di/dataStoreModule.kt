package meshki.studio.negarname.di

import com.google.gson.Gson
import meshki.studio.negarname.data.storage.PersistentStorage
import meshki.studio.negarname.data.storage.StorageApi
import org.koin.dsl.module

val dataStoreModule = module {

    single<StorageApi> {
        PersistentStorage(context = get())
    }

    single { Gson() }
}