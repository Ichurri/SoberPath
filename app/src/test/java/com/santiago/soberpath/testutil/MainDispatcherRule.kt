package com.santiago.soberpath.testutil

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Sustituye [Dispatchers.Main] por un [TestDispatcher] durante cada test, de modo que el
 * `viewModelScope` se ejecute de forma controlada en el JVM. Por defecto usa un
 * [UnconfinedTestDispatcher] para que las corrutinas se lancen de forma "eager" y el estado
 * quede disponible inmediatamente tras cada intent.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
