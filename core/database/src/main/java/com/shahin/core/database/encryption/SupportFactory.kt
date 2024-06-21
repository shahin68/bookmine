package com.shahin.core.database.encryption

import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

data class SupportFactory(
    val supportFactory: SupportOpenHelperFactory? = null,
    val passphraseChanged: Boolean? = null,
    val currentPassphrase: String? = null
)
