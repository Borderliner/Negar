package meshki.studio.negarname.di

import meshki.studio.negarname.vm.EditNotesViewModel
import meshki.studio.negarname.vm.MainViewModel
import meshki.studio.negarname.vm.NotesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    single {
        MainViewModel(get(), get())
    }
    viewModel { NotesViewModel(get()) }
    viewModel { EditNotesViewModel(get(), get(), get(), get()) }
}
