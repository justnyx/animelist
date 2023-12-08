package com.animelist.models

import kotlinx.serialization.Serializable

@Serializable
data class WatchStatusRequest(
    val watchStatus: WatchStatus
) {
    //converts the WatchStatus to the corresponding character in the database
    override fun toString(): String {
        val watchStatusMap = mapOf(
            WatchStatus.COMPLETED to "c",
            WatchStatus.WATCHING to "w",
            WatchStatus.PLAN_TO_WATCH to "p")
        return watchStatusMap[watchStatus] ?: ""
    }
}
