package meshki.studio.negarname.di

import kotlinx.coroutines.Dispatchers
import meshki.studio.negarname.data.repository.AppRepository
import meshki.studio.negarname.data.repository.NotesRepository
import meshki.studio.negarname.data.repository.TodosRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AppRepository> { AppRepository(get(), get(), Dispatchers.IO) }
    single<NotesRepository> { NotesRepository(get(), get(), Dispatchers.IO) }
    single<TodosRepository> { TodosRepository(get(), Dispatchers.IO) }
}
