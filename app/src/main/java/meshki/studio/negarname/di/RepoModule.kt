package meshki.studio.negarname.di

import meshki.studio.negarname.data.repository.NotesRepoImpl
import meshki.studio.negarname.data.repository.NotesRepository
import meshki.studio.negarname.data.repository.TodosRepoImpl
import meshki.studio.negarname.data.repository.TodosRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<NotesRepository> { NotesRepoImpl(get(), get()) }
    single<TodosRepository> { TodosRepoImpl(get(), get()) }
}
