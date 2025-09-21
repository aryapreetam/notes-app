package org.notesapp.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.notesapp.data.db.createDatabase
import org.notesapp.data.db.createDatabaseDriver
import org.notesapp.data.repository.NotesRepository
import org.notesapp.data.repository.NotesRepositoryImpl
import org.notesapp.domain.usecase.CreateNoteUseCase
import org.notesapp.domain.usecase.DeleteNoteUseCase
import org.notesapp.domain.usecase.GetNotesUseCase
import org.notesapp.presentation.notes.create.CreateNoteViewModel
import org.notesapp.presentation.notes.list.NotesListViewModel

fun initKoin() {
  startKoin {
    printLogger()
    modules(appModules)
  }
}

// DatabaseModule
val databaseModule = module {
  // Platform module must provide SqlDriver; we wrap it for uniform API and provide NotesDatabase
  singleOf(::createDatabaseDriver)
  // Using explicit lambda to avoid unresolved reference issues during codegen phase
  singleOf(::createDatabase)
}

val repositoryModule = module {
  single { NotesRepositoryImpl(get()) }
  single<NotesRepository> { get<NotesRepositoryImpl>() }
}

val useCaseModule = module {
  factory { CreateNoteUseCase(get()) }
  factory { DeleteNoteUseCase(get()) }
  factory { GetNotesUseCase(get()) }
}

val viewModelModule = module {
  factory { NotesListViewModel(get(), get()) }
  factory { CreateNoteViewModel(get()) }
}

expect val platformModule: Module

val appModules = listOf(
  // Platform first to ensure SqlDriver is available when constructing DB
  databaseModule,
  repositoryModule,
  useCaseModule,
  viewModelModule,
  platformModule
)
