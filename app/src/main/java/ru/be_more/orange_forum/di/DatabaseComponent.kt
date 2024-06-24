package ru.be_more.orange_forum.di

import dagger.Component


@Component(modules = [DatabaseModule::class])
interface DatabaseComponent