package com.shahin.bookmine.presentation

import com.shahin.bookmine.BuildConfig
import com.shahin.core.network.books.model.BookItem
import com.shahin.core.network.model.ErrorReason
import com.shahin.core.network.model.NetworkResponse
import com.shahin.feature.books.domain.GetBooksUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

/**
 * Important to note:
 * Our view model triggers [syncBooks] upon initialization
 * This mean we can't really test the scenario of before and after [syncBooks] is triggered
 * we have to assume all tests begin with a network [syncBooks] call and then work our way
 * through retries and different types of responses
 */
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)


    @Mock
    lateinit var getBooksUseCase: GetBooksUseCase

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel(getBooksUseCase)
    }


    @Test
    fun testSyncingBooksForSuccessResultOnInit() = runTest(testDispatcher) {
        val successResponse = NetworkResponse.Success<List<BookItem?>>(emptyList())
        `when`(getBooksUseCase.syncBooks(BuildConfig.MOCKY_IDENTIFIER)).thenReturn(successResponse)


        // we expect `ongoingSyncInProgress` to be True
        assertTrue(viewModel.ongoingSyncInProgress.value)
        // we expect `retryCount` to be 0
        assertEquals(0, viewModel.retryCount.value)
        // we expect `retryCount` to be null
        assertNull(viewModel.errorResponse.value)

    }

}