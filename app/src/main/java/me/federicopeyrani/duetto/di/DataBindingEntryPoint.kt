package me.federicopeyrani.duetto.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import me.federicopeyrani.duetto.adapters.BindingAdapter
import me.federicopeyrani.duetto.di.scopes.BindingScope

@EntryPoint
@BindingScope
@InstallIn(BindingComponent::class)
interface DataBindingEntryPoint {

    @BindingScope
    fun getBindingAdapter(): BindingAdapter
}