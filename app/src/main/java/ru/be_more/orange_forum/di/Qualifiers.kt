package ru.be_more.orange_forum.di

import javax.inject.Qualifier

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class Dvach(
    val value: String = "Dvach"
)

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class Fourchan(
    val value: String = "Fourchan"
)