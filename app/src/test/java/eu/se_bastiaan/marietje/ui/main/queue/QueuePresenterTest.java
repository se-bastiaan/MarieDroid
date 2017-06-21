package eu.se_bastiaan.marietje.ui.main.queue;

import android.content.Context;
import android.test.mock.MockContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import eu.se_bastiaan.marietje.data.ControlDataManager;
import eu.se_bastiaan.marietje.data.DataManager;
import eu.se_bastiaan.marietje.data.model.Empty;
import eu.se_bastiaan.marietje.data.model.Queue;
import eu.se_bastiaan.marietje.test.common.TestDataFactory;
import eu.se_bastiaan.marietje.util.EventBus;
import eu.se_bastiaan.marietje.util.RxSchedulersOverrideRule;
import rx.Observable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QueuePresenterTest {

    Context context = new MockContext();
    @Mock
    EventBus eventBus;
    @Mock
    QueueView mockQueueView;
    @Mock
    DataManager mockDataManager;
    private QueuePresenter queuePresenter;

    @Rule
    public final RxSchedulersOverrideRule overrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() {
        when(mockDataManager.controlDataManager()).thenReturn(mock(ControlDataManager.class));

        queuePresenter = new QueuePresenter(mockDataManager, context, eventBus);
        queuePresenter.attachView(mockQueueView);

    }

    @After
    public void tearDown() {
        queuePresenter.detachView(mockQueueView);
    }

    @Test
    public void loadDataReturnsQueue() {
        Queue queue = TestDataFactory.makeQueueResponse();
        when(mockDataManager.controlDataManager().queue())
                .thenReturn(Observable.just(queue));

        queuePresenter.loadData();
        verify(mockQueueView).showLoading();
        verify(mockQueueView).showQueue(queue);
        verify(mockQueueView, never()).showLoadingError();
    }

    @Test
    public void loadDataFails() {
        when(mockDataManager.controlDataManager().queue())
                .thenReturn(Observable.error(new RuntimeException()));

        queuePresenter.loadData();
        verify(mockQueueView).showLoading();
        verify(mockQueueView).showLoadingError();
        verify(mockQueueView, never()).showQueue(any(Queue.class));
    }

    @Test
    public void removeSongFromQueueShowsSuccess() {
        Empty response = new Empty();
        when(mockDataManager.controlDataManager().cancel(0))
                .thenReturn(Observable.just(response));

        queuePresenter.removeSongFromQueue(0);
        verify(mockQueueView).showRemoveSuccess();
        verify(mockQueueView, never()).showRemoveError();
    }

    @Test
    public void removeSongFromQueueShowsError() {
        when(mockDataManager.controlDataManager().cancel(0))
                .thenReturn(Observable.error(new RuntimeException()));

        queuePresenter.removeSongFromQueue(0);
        verify(mockQueueView).showRemoveError();
        verify(mockQueueView, never()).showRemoveSuccess();
    }

    @Test
    public void moveSongDownInQueueShowsSuccess() {
        Empty response = new Empty();
        when(mockDataManager.controlDataManager().moveDown(0))
                .thenReturn(Observable.just(response));

        queuePresenter.moveSongDownInQueue(0);
        verify(mockQueueView).showMoveDownSuccess();
        verify(mockQueueView, never()).showMoveDownError();
    }

    @Test
    public void moveSongDownInQueueShowsError() {
        when(mockDataManager.controlDataManager().moveDown(0))
                .thenReturn(Observable.error(new RuntimeException()));

        queuePresenter.moveSongDownInQueue(0);
        verify(mockQueueView).showMoveDownError();
        verify(mockQueueView, never()).showMoveDownSuccess();
    }

    @Test
    public void moveSongUpInQueueShowsSuccess() {
        Empty response = new Empty();
        when(mockDataManager.controlDataManager().moveUp(0))
                .thenReturn(Observable.just(response));

        queuePresenter.moveSongUpInQueue(0);
        verify(mockQueueView).showMoveUpSuccess();
        verify(mockQueueView, never()).showMoveUpError();
    }

    @Test
    public void moveSongUpInQueueShowsError() {
        when(mockDataManager.controlDataManager().moveUp(0))
                .thenReturn(Observable.error(new RuntimeException()));

        queuePresenter.moveSongUpInQueue(0);
        verify(mockQueueView).showMoveUpError();
        verify(mockQueueView, never()).showMoveUpSuccess();
    }

    @Test
    public void skipCurrentSongShowsSuccess() {
        Empty response = new Empty();
        when(mockDataManager.controlDataManager().skip())
                .thenReturn(Observable.just(response));

        queuePresenter.skipCurrentSong();
        verify(mockQueueView).showSkipSuccess();
        verify(mockQueueView, never()).showSkipError();
    }

    @Test
    public void skipCurrentSongShowsError() {
        when(mockDataManager.controlDataManager().skip())
                .thenReturn(Observable.error(new RuntimeException()));

        queuePresenter.skipCurrentSong();
        verify(mockQueueView).showSkipError();
        verify(mockQueueView, never()).showSkipSuccess();
    }

}
