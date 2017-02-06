package eu.se_bastiaan.marietje.ui.main.queue;

import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import eu.se_bastiaan.marietje.R;
import eu.se_bastiaan.marietje.data.model.PlaylistSong;
import eu.se_bastiaan.marietje.data.model.Queue;
import eu.se_bastiaan.marietje.test.common.TestComponentRule;
import eu.se_bastiaan.marietje.test.common.TestDataFactory;
import eu.se_bastiaan.marietje.ui.main.MainActivity;
import eu.se_bastiaan.marietje.util.IdlingSchedulersOverrideRule;
import rx.Observable;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class QueueFragmentTest {

    public final TestComponentRule component =
            new TestComponentRule(getTargetContext());
    public final ActivityTestRule<MainActivity> main =
            new ActivityTestRule<MainActivity>(MainActivity.class, false, false) {
                @Override
                protected Intent getActivityIntent() {
                    // Override the default intent so we pass a false flag for syncing so it doesn't
                    // start a sync service in the background that would affect  the behaviour of
                    // this test.
                    return MainActivity.getStartIntent(getTargetContext(), false);
                }
            };

    @Before
    public void setUp() {
        when(component.getMockDataManager().preferencesHelper().getSessionId())
                .thenReturn("sessionId");
    }

    // TestComponentRule needs to go first to make sure the Dagger ApplicationTestComponent is set
    // in the Application before any Activity is launched.
    @Rule
    public final TestRule chain = RuleChain.outerRule(component).around(main);
    @Rule
    public final IdlingSchedulersOverrideRule overrideSchedulersRule = new IdlingSchedulersOverrideRule();

    @Test
    public void playlistShows() {
        Queue response = TestDataFactory.makeQueueResponse();
        when(component.getMockDataManager().controlDataManager().queue())
                .thenReturn(Observable.just(response));

        main.launchActivity(null);

        List<PlaylistSong> playlistSongs = new ArrayList<>(response.queuedSongs());
        playlistSongs.add(0, response.currentSong());

        int position = 0;
        long startsAt = response.startedAt();
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        for (PlaylistSong playlistSong : playlistSongs) {
            Date date = new Date(startsAt * 1000);

            onView(withId(R.id.recycler_view))
                    .perform(RecyclerViewActions.scrollToPosition(position));
            onView(withText(playlistSong.song().title()))
                    .check(matches(isDisplayed()));
            onView(withText(playlistSong.song().artist()))
                    .check(matches(isDisplayed()));
            if (position != 0) {
                onView(withText(formatter.format(date)))
                        .check(matches(isDisplayed()));
            }

            startsAt += playlistSong.song().duration();

            position++;
        }
    }

}