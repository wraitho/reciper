package com.peeeq.reciper.details

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers

/**
 * Overloads RxJava and RxAndroid Schedulers for unit tests
 */
class RxJavaTestRule : TestRule {
    /**
     * {@inheritDoc}
     */
    override fun apply(base: Statement, description: Description): Statement {
        return RxJava2TestStatement(base)
    }

    private inner class RxJava2TestStatement(private val base: Statement) : Statement() {

        /**
         * {@inheritDoc}
         */
        @Throws(Throwable::class)
        override fun evaluate() {
            RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
            RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
            RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
            RxJavaPlugins.setSingleSchedulerHandler { Schedulers.trampoline() }

            RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
            RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.trampoline() }

            base.evaluate()

            RxJavaPlugins.reset()
            RxAndroidPlugins.reset()
        }
    }
}
