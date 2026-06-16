package com.span.ashless.di

import org.koin.core.module.Module
import org.koin.dsl.module

val dataModule = module { }

val domainModule = module { }

val presentationModule = module { }

expect fun platformModule(): Module
