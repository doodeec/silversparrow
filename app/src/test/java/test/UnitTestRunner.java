package test;

import org.mockito.runners.MockitoJUnitRunner;

import rx.Scheduler;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;
import rx.plugins.RxJavaTestPlugins;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import timber.log.Timber;

/**
 * Custom test runner, which is necessary for using mock Rx Schedulers
 *
 * @author Dusan Bartos
 */
public final class UnitTestRunner extends MockitoJUnitRunner {
    public static TestScheduler computationScheduler = new TestScheduler();
    public static TestScheduler newScheduler = new TestScheduler();

    public UnitTestRunner(Class<?> cls) throws Exception {
        super(cls);

        RxJavaTestPlugins.resetPlugins();
        RxJavaPlugins.getInstance().registerSchedulersHook(new RxJavaSchedulersHook() {
            @Override
            public Scheduler getIOScheduler() {
                return Schedulers.immediate();
            }

            @Override public Scheduler getComputationScheduler() {
                return computationScheduler;
            }

            @Override public Scheduler getNewThreadScheduler() {
                return newScheduler;
            }
        });

        Timber.plant(new TestDebugTree());
    }
}
