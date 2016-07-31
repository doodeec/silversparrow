package test;

import android.app.Activity;
import android.content.res.Configuration;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.FailureHandler;
import android.support.test.espresso.base.DefaultFailureHandler;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.squareup.spoon.Spoon;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.Is.is;

/**
 * Base Runner for UI tests
 * <p>
 * Annotate test class with "@RunWith(AndroidJUnit4.class)"
 * and extend this Runner, so you can take advantage of prepared matchers and setup method
 * </p>
 *
 * @author Dusan Bartos
 */
public abstract class BaseTestRunner<T extends Activity> {

    @Rule public ActivityTestRule<T> activityRule;
    protected T activity;

    public BaseTestRunner(Class<T> type) {
        this.activityRule = new ActivityTestRule<>(type);
    }

    /**
     * Try to disable animations (Espresso doesn't like them, tests are crashing if layout animations are used)
     */
    @Before public void disableAnimations() {
        new SystemAnimations(InstrumentationRegistry.getInstrumentation().getTargetContext()).disableAll();
    }

    @After public void clearReferences() {
        activity = null;
    }

    @Before public void setup() {
        activity = activityRule.getActivity();
    }

    /**
     * Prepares test method and decorates original failure handler with Spoon
     * Spoon provides a screenshot of every failed test (all files are located at
     * {sdcard}/app_spoon-screenshots/{testclass}/{testmethod} on a test device)
     *
     * @param activity context
     */
    protected void setupFailureHandler(Activity activity) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        //setup fail handler
        Espresso.setFailureHandler(new CustomFailureHandler(activity, trace[3].getClassName(), trace[3].getMethodName()));
    }

    protected boolean isPortrait(Activity activity) {
        return activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    protected void clickView(int id) {
        onView(withId(id)).perform(click());
    }

    protected void isViewDisplayed(int id) {
        isViewDisplayed(withId(id));
    }

    protected <C> void isViewDisplayed(Class<C> clazz) {
        isViewDisplayed(withClassName(is(clazz.getName())));
    }

    protected void isViewDisplayed(Matcher<View> matcher) {
        onView(matcher).check(matches(isDisplayed()));
    }

    private class CustomFailureHandler implements FailureHandler {
        DefaultFailureHandler defHandler;
        Activity activity;
        String className;
        String methodName;

        CustomFailureHandler(Activity activity, String className, String methodName) {
            this.defHandler = new DefaultFailureHandler(activity);
            this.activity = activity;
            this.className = className;
            this.methodName = methodName;
        }

        @Override public void handle(Throwable error, Matcher<View> viewMatcher) {
            try {
                System.out.println("trying to take a screenshot of failed test: " + className + ":" + methodName);
                Spoon.screenshot(activity, "espresso_assertion_failed", className, methodName);
                System.out.println("screenshot created");
            } catch (Exception e) {
                System.out.println("cannot create screenshot");
            }

            try {
                defHandler.handle(error, viewMatcher);
            } catch (Exception e) {
                System.out.println("error handle failed with exception");
                throw e;
            }
        }
    }
}
