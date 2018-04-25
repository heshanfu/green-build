/*
 * Copyright 2018 Keval Patel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kevalpatel2106.greenbuild.travisInterface.entities

import com.google.gson.annotations.SerializedName
import com.kevalpatel2106.ci.greenbuild.base.ciInterface.entities.Branch
import com.kevalpatel2106.ci.greenbuild.base.ciInterface.entities.Build
import com.kevalpatel2106.ci.greenbuild.base.ciInterface.entities.BuildState
import com.kevalpatel2106.ci.greenbuild.base.ciInterface.entities.TriggerType
import com.kevalpatel2106.ci.greenbuild.base.utils.ConversationUtils
import com.kevalpatel2106.greenbuild.travisInterface.Constants


internal data class TravisBuild(

        @field:SerializedName("id")
        val id: Int,

        @field:SerializedName("number")
        val number: String,

        @field:SerializedName("state")
        val state: String,

        @field:SerializedName("duration")
        val duration: Int,

        @field:SerializedName("previous_state")
        val previousState: String? = null,

        @field:SerializedName("finished_at")
        val finishedAt: String? = null,

        @field:SerializedName("jobs")
        val jobs: List<TravisJob>? = null,

        @field:SerializedName("commit")
        val commit: TravisCommit,

        @field:SerializedName("repository")
        val repository: TravisRepo,

        @field:SerializedName("branch")
        val branch: TravisBranch?,

        @field:SerializedName("created_by")
        val createdBy: TravisAuthor,

        @field:SerializedName("event_type")
        val eventType: String,

        @field:SerializedName("pull_request_title")
        val pullRequestTitle: String? = null,

        @field:SerializedName("updated_at")
        val updatedAt: String? = null,

        @field:SerializedName("@representation")
        val representation: String? = null,

        @field:SerializedName("pull_request_number")
        val pullRequestNumber: String? = null,

        @field:SerializedName("@envVarsPermissions")
        val permissions: BuildPermissions,

        @field:SerializedName("tag")
        val tag: TravisTag? = null,

        @field:SerializedName("started_at")
        val startedAt: String? = null
) {

    fun toBuild(): Build {
        return Build(
                id = id.toLong(),
                state = getBuildState(state),
                duration = duration.toLong(),
                triggerType = getEventType(eventType),
                number = number,
                finishedAt = if (finishedAt == null) 0 else ConversationUtils.rfc3339ToMills(finishedAt),
                startedAt = if (startedAt == null) 0 else ConversationUtils.rfc3339ToMills(startedAt),
                previousState = if (previousState != null) getBuildState(previousState) else null,
                author = Build.Author(
                        id = createdBy.id.toString(),
                        username = createdBy.login
                ),
                branch = Branch(
                        name = branch?.name ?: "default"
                ),
                commit = Build.Commit(
                        committedAt = commit.committedAt,
                        message = commit.message,
                        sha = commit.sha,
                        tagName = tag?.tagName
                )
        )
    }

    private fun getEventType(eventType: String): TriggerType {
        return when (eventType) {
            Constants.PUSH_EVENT -> TriggerType.PUSH
            Constants.PULL_REQUEST_EVENT -> TriggerType.PULL_REQUEST
            Constants.CRON_EVENT -> TriggerType.CRON
            else -> throw IllegalArgumentException("Invalid trigger event type: $eventType")
        }
    }

    private fun getBuildState(buildState: String): BuildState {
        return when (buildState.toLowerCase().trim()) {
            Constants.FAILED_BUILD -> BuildState.FAILED
            Constants.PASSED_BUILD -> BuildState.PASSED
            Constants.ERRORED_BUILD -> BuildState.ERRORED
            Constants.CANCEL_BUILD -> BuildState.CANCELED
            Constants.RUNNING_BUILD -> BuildState.RUNNING
            Constants.BOOTING_BUILD -> BuildState.BOOTING
            else -> throw IllegalArgumentException("Invalid build state: $buildState")
        }
    }

    data class BuildPermissions(

            @field:SerializedName("cancel")
            val cancel: Boolean,

            @field:SerializedName("canRead")
            val read: Boolean,

            @field:SerializedName("restart")
            val restart: Boolean
    )
}
