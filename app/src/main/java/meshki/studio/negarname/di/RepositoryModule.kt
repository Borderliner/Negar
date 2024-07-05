package meshki.studio.negarname.di

import meshki.studio.negarname.data.repository.DatabaseRepository
import meshki.studio.negarname.data.repository.NotesRepository
import meshki.studio.negarname.data.repository.TodosRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<DatabaseRepository> { DatabaseRepository(get(), get()) }
    single<NotesRepository> { NotesRepository(get(), get()) }
    single<TodosRepository> { TodosRepository(get()) }
}
