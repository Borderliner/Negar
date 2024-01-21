package meshki.studio.negarname.di

import kotlinx.coroutines.Dispatchers
import meshki.studio.negarname.data.repository.NotesRepoImpl
import meshki.studio.negarname.data.repository.NotesRepository
import meshki.studio.negarname.data.repository.TodosRepoImpl
import meshki.studio.negarname.data.repository.TodosRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<NotesRepository> { NotesRepoImpl(get(), get(), Dispatchers.IO) }
    single<TodosRepository> { TodosRepoImpl(get(), get()) }
}
