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

package com.kevalpatel2106.ci.greenbuild.buildList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kevalpatel2106.ci.greenbuild.R
import com.kevalpatel2106.ci.greenbuild.base.ciInterface.entities.Build
import com.kevalpatel2106.ci.greenbuild.base.ciInterface.entities.getBuildStateColor
import com.kevalpatel2106.ci.greenbuild.base.ciInterface.entities.getTriggerTypeText
import com.kevalpatel2106.ci.greenbuild.base.utils.ConversationUtils
import com.kevalpatel2106.ci.greenbuild.base.utils.isEmpty
import com.kevalpatel2106.ci.greenbuild.base.view.PageRecyclerViewAdapter
import kotlinx.android.synthetic.main.row_builds_list.view.*

/**
 * Created by Keval on 18/04/18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class BuildViewHolder private constructor(itemView: View)
    : PageRecyclerViewAdapter.PageViewHolder(itemView) {

    companion object {

        fun create(parent: ViewGroup): BuildViewHolder {
            return BuildViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_builds_list, parent, false))
        }
    }

    fun bind(build: Build) {
        itemView.build_status_view.setBackgroundColor(build.state.getBuildStateColor(itemView.context))

        itemView.built_number_tv.text = "#${build.number}"
        itemView.branch_name_tv.text = build.branch.name

        itemView.commit_message_tv.text = build.commit.message
        itemView.commit_hash_tv.text = build.commit.sha.drop(build.commit.sha.length - 8 /* Show last 8 letters */)
        itemView.build_trigger_type_iv.chipText = build.triggerType.getTriggerTypeText(itemView.context)

        itemView.commit_author_name_tv.text = build.author.username
        itemView.commit_author_avatar_iv.setImageResource(R.drawable.ic_user)

        if (build.duration.isEmpty()) {
            itemView.time_taken_tv.text = "-"
        } else {
            itemView.time_taken_tv.text = itemView.context.getString(
                    R.string.build_row_ran_for,
                    ConversationUtils.convertToHumanReadableDuration(build.duration * 1000L)
            )
        }

        if (build.startedAt == 0L) {
            itemView.build_start_time_tv.text = "-"
        } else {
            itemView.build_start_time_tv.text = ConversationUtils.getDate(build.startedAt)
        }
    }
}
