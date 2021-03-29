package me.federicopeyrani.duetto.di

import androidx.databinding.DataBindingComponent
import dagger.hilt.DefineComponent
import dagger.hilt.components.SingletonComponent
import me.federicopeyrani.duetto.di.scopes.BindingScope

@BindingScope
@DefineComponent(parent = SingletonComponent::class)
interface BindingComponent : DataBindingComponent