package me.federicopeyrani.duetto.di

import dagger.hilt.DefineComponent

@DefineComponent.Builder
interface BindingComponentBuilder {

    fun build(): BindingComponent
}