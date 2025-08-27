// integration/HostLocator.kt (core)
package org.coreengine.integration

object HostLocator {
    @Volatile var host: HostBridge? = null
}
