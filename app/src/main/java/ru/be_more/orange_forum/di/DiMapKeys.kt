package ru.be_more.orange_forum.di

import androidx.lifecycle.ViewModel
import dagger.MapKey
import ru.be_more.model.model.ImageboardType
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Target(AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ApiKey(val value: ImageboardType)