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

package com.kevalpatel2106.ci.greenbuild.cacheList

import android.content.Context
import android.view.ViewGroup
import androidx.core.widget.toast
import com.kevalpatel2106.ci.greenbuild.R
import com.kevalpatel2106.ci.greenbuild.base.ciInterface.CompatibilityCheck
import com.kevalpatel2106.ci.greenbuild.base.ciInterface.ServerInterface
import com.kevalpatel2106.ci.greenbuild.base.ciInterface.cache.Cache
import com.kevalpatel2106.ci.greenbuild.base.utils.alert
import com.kevalpatel2106.ci.greenbuild.base.view.PageRecyclerViewAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Keval on 18/04/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
internal class CacheListAdapter(
        context: Context,
        list: ArrayList<Cache>,
        private val serverInterface: ServerInterface,
        private val compatibilityCheck: CompatibilityCheck,
        listener: PageRecyclerViewAdapter.RecyclerViewListener<Cache>)
    : PageRecyclerViewAdapter<CacheListViewHolder, Cache>(context, list, listener) {

    override fun bindView(holder: CacheListViewHolder, item: Cache) {
        holder.bind(item, { deleteCache(item) })
    }

    override fun prepareViewHolder(parent: ViewGroup, viewType: Int): CacheListViewHolder {
        return CacheListViewHolder.create(
                parent = parent,
                isDeleteSupported = compatibilityCheck.isCacheDeleteSupported()
        )
    }

    override fun prepareViewType(position: Int): Int {
        return 1
    }

    override fun getPageSize(): Int {
        return ServerInterface.PAGE_SIZE
    }

    /**
     * Delete [cache] from the server. This method will display the confirmation dialog and if user
     * confirms delete operation it will delete [cache] by calling [ServerInterface.deleteCache].
     *
     * @see ServerInterface.deleteCache
     */
    private fun deleteCache(cache: Cache) {
        context.alert(
                title = null,
                message = context.getString(R.string.delete_cache_title_confirmation_title),
                func = {

                    positiveButton(R.string.btn_title_delete, {
                        serverInterface.deleteCache(cache.repositoryId, cache.branchName)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .doOnSubscribe {
                                    cache.isDeleting = true
                                    notifyDataSetChanged()
                                }
                                .doOnTerminate {
                                    cache.isDeleting = false
                                    notifyDataSetChanged()
                                }
                                .subscribe({
                                    val posToRemove = collection.lastIndexOf(cache)
                                    collection.remove(cache)
                                    notifyItemRemoved(posToRemove)
                                }, {
                                    context.toast(it.message!!)
                                })
                    })

                    negativeButton(android.R.string.cancel)

                    cancelable = false
                }
        )
    }
}