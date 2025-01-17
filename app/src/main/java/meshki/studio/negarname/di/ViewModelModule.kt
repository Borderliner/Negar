package meshki.studio.negarname.di

import meshki.studio.negarname.ui.calendar.CalendarViewModel
import meshki.studio.negarname.ui.notes.vm.EditNotesViewModel
import meshki.studio.negarname.ui.app.AppViewModel
import meshki.studio.negarname.ui.notes.vm.NotesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    single { AppViewModel(get(), get()) }
    viewModel { NotesViewModel(get()) }
    viewModel { EditNotesViewModel(get(), get(), get(), get()) }
    viewModel { CalendarViewModel() }
}
