package me.federicopeyrani.duetto.data

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.Instant
import java.util.Date

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var db: AppDatabase

    private lateinit var trackDao: TrackDao

    private lateinit var playHistoryDao: PlayHistoryDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        trackDao = db.trackDao()
        playHistoryDao = db.playHistoryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun shouldJoinTracksAndPlayHistory() = runBlocking {
        db.clearAllTables()

        val instant = Instant.now()
        val track = Track("12ef", "I Disagree", "Poppy", arrayOf())
        val playHistoryEntity = PlayHistoryEntity(Date.from(instant), "12ef")

        trackDao.insertAll(listOf(track))
        playHistoryDao.insertAll(listOf(playHistoryEntity))

        val playHistory = playHistoryDao.getPlayHistories()[0]
        playHistory.run {
            track.title shouldBe "I Disagree"
            playedAt shouldBe Date.from(instant)
        }
    }

    @Test
    @Throws(Exception::class)
    fun shouldReturnPagingSource() = runBlocking<Unit> {
        db.clearAllTables()

        val instant = Instant.now()
        val playHistories = (0..100).map {
            val newInstant = instant.plusSeconds(it.toLong())
            PlayHistoryEntity(Date.from(newInstant), "12ef")
        }
        playHistoryDao.insertAll(playHistories)

        val loadParams = PagingSource.LoadParams.Append(0, 10, false)
        val data = playHistoryDao.getPlayHistoryPagingSource().load(loadParams)
        data as PagingSource.LoadResult.Page<Int, PlayHistoryEntity>

        data.data shouldHaveSize 10
    }
}