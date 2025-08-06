package com.github.mullerma.nfcreader

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withChild
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.github.muellerma.nfcreader.MainActivity
import com.github.muellerma.nfcreader.R
import kotlinx.coroutines.delay
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImplementationTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Before
    fun setup() {
        //val idlingResource = MyIdlingResource()
    }

    @Test
    fun `mainPage to helpPage`(){
        var helpButton = 0
        var backButton = 0
        var howTo = 0
        var tg = 0
        activityRule.scenario.onActivity { activity ->
            helpButton = activity.helpButton.id
            howTo = activity.howTo.id
            backButton = activity.returnButton.id
            tg = activity.tagList!!.id
        }

        onView(withId(helpButton))
           .perform(click())

        Espresso.onIdle()

        //onView(withId(tg))
          //  .check(matches(withChild(withId(howTo))))

        onView(isRoot()).perform(DelayAction(1000))

       onView(withId(backButton))
           .check(matches(isDisplayed()))
           //.perform(click())

    }

    /*
    @Test
    fun `mainPage to helpPage`(){}
    */

}

class DelayAction(private val delayMillis: Long) : ViewAction {
    override fun getConstraints(): Matcher<View> = isAssignableFrom(View::class.java)

    override fun getDescription(): String = "Delay for $delayMillis ms"

    override fun perform(uiController: UiController?, view: View?) {
        uiController?.loopMainThreadForAtLeast(delayMillis)
    }
}
