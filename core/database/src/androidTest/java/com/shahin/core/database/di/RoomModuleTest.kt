package com.shahin.core.database.di

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.shahin.core.database.AppDatabase
import com.shahin.core.database.BuildConfig
import com.shahin.core.database.common.Constants.DB_PASSPHRASE_KEY
import com.shahin.core.database.common.Constants.DB_PREFS
import com.shahin.core.database.encryption.SupportFactory
import com.shahin.core.database.extensions.hash
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidTest
class RoomModuleTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    @Named(DB_PASSPHRASE_KEY)
    lateinit var currentPassphrase: String

    @Inject
    lateinit var supportFactory: SupportFactory

    private lateinit var context: Context

    @Before
    fun setup() {
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testProvidingSupportFactory_firstTimeAppLaunch() {
        val sharedPrefs = context.getSharedPreferences(DB_PREFS, Context.MODE_PRIVATE)
        sharedPrefs.edit().clear().apply()

        // since first time there is no shared key stored yet, no password is considered as changed
        assertEquals(false, supportFactory.passphraseChanged)

        // check if the current password [currentPassphrase] is equal to what that's been passed to room - should pass
        assertEquals(
            currentPassphrase,
            supportFactory.currentPassphrase
        )
    }

    @Test
    fun testProvidingSupportFactory_passwordUnchanged() {
        val sharedPrefs = context.getSharedPreferences(DB_PREFS, Context.MODE_PRIVATE)
        sharedPrefs.edit().putString(DB_PASSPHRASE_KEY, currentPassphrase).apply()

        // check implementation of checking phrases
        val passwordChanged = sharedPrefs.getString(DB_PASSPHRASE_KEY, null)
            ?.equals(currentPassphrase, ignoreCase = false)?.not() ?: false

        assertEquals(false, passwordChanged)

        // check if the current password is equal to what that's been passed to room - should pass
        assertEquals(
            currentPassphrase,
            supportFactory.currentPassphrase
        )

        // check if the latest password is stored in shared preferences
        assertEquals(
            sharedPrefs.getString(DB_PASSPHRASE_KEY, null),
            currentPassphrase
        )
    }

    /**
     * Simulating the scenario where the passphrase for the db is changed
     */
    @Test
    fun testProvidingSupportFactory_passwordChanged() {
        val sharedPrefs = context.getSharedPreferences(DB_PREFS, Context.MODE_PRIVATE)

        // first we clear whatever shared prefs that are stored, we assign a new value later anyways
        sharedPrefs.edit().clear().apply()

        // we assume our old password is the following
        val oldPassphrase = "my_old_passphrase".hash()


        // first check if shared preference is empty, cause this is a test and we want to mock the mentioned scenario
        assertNotEquals(sharedPrefs.getString(DB_PASSPHRASE_KEY, null), oldPassphrase)
        assertNull(sharedPrefs.getString(DB_PASSPHRASE_KEY, null))

        // so we forcefully put [oldPassphrase] as our old passphrase into our shared preferences
        sharedPrefs.edit().putString(DB_PASSPHRASE_KEY, oldPassphrase).apply()

        // check again if the shared preference is really set
        assertNotNull(sharedPrefs.getString(DB_PASSPHRASE_KEY, null))
        assertEquals(sharedPrefs.getString(DB_PASSPHRASE_KEY, null), oldPassphrase)

        // check implementation of checking phrases
        val passwordChanged = sharedPrefs.getString(DB_PASSPHRASE_KEY, null)
            ?.equals(currentPassphrase, ignoreCase = false)?.not() ?: false

        assertEquals(true, passwordChanged)

        // and we should check if the old passphrase isn't the one we're sending to our Room via [provideSupportFactory]
        assertNotEquals(
            oldPassphrase,
            supportFactory.currentPassphrase
        )

        // and finally we should check if our [currentPassphrase] is being sent to our Room instead
        assertEquals(
            currentPassphrase,
            supportFactory.currentPassphrase
        )
    }

    @Test
    fun testProvidingAppDatabaseWithSupportFactory() {
        val db = Room.databaseBuilder(context, AppDatabase::class.java, BuildConfig.DATABASE_NAME)
            .openHelperFactory(supportFactory.supportFactory)
            .build()

        assertNotNull(supportFactory.passphraseChanged)
        assertNotNull(supportFactory.currentPassphrase)
        assertNotNull(db)
    }
}