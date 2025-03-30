package com.densitect.rssreader.app

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface State
interface Action
interface Effect

interface Store<S : State, A : Action, E : Effect> {
    fun observerState(): MutableStateFlow<S>
    fun observerSideEffect(): Flow<E>
    fun dispatch(action: A)
}