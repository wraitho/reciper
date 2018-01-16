package com.peeeq.reciper.widgets

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ViewGroup
import com.peeeq.reciper.commons.helper.hide
import com.peeeq.reciper.commons.helper.show
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * This class and the whole pagination solution was over brought from former projects, so it's kinda old.
 * Probably should be replaced by Paging library from Google
 *
 * Extends the basic [android.support.v7.widget.RecyclerView.Adapter] to enable Header view and Footer view
 *
 * @param <NORMAL_VH> ViewHolder class extends [android.support.v7.widget.RecyclerView.ViewHolder] for "normal" items.
 * @param <FOOTER_VH> ViewHolder class extends [android.support.v7.widget.RecyclerView.ViewHolder] for the footer item.
</FOOTER_VH></NORMAL_VH> */
abstract class PaginatingRecyclerViewAdapter<NORMAL_VH : RecyclerView.ViewHolder, FOOTER_VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected abstract val realItemCount: Int

    private val keepOnAppending: AtomicBoolean = AtomicBoolean()
    private val dataPending: AtomicBoolean = AtomicBoolean()
    private val requestedPage: AtomicInteger = AtomicInteger(0)
    val loadMoreListener: PublishSubject<Int> = PublishSubject.create()

    private enum class ViewType(val code: Int) {
        NORMAL(0),
        FOOTER(1)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) ViewType.FOOTER.code else ViewType.NORMAL.code
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ViewType.FOOTER.code) onCreateFooterViewHolder(parent)
            else onCreateNormalViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == ViewType.FOOTER.code) {

            if (!dataPending.get() && dataPending.compareAndSet(false, true)) {
                loadMoreListener.onNext(requestedPage.incrementAndGet())
                Log.d("Adapter", "Requesting ${requestedPage.get()}. page")
            }

            if (position == 0) {
                holder.itemView.hide()
            } else {
                holder.itemView.show()
            }

            onBindFooterView(holder as FOOTER_VH)
        } else {
            onBindNormalView(holder as NORMAL_VH, position)
        }
    }

    override fun getItemCount(): Int {
        return realItemCount + 1
    }

    /**
     * Let the adapter know that data is load and ready to view.
     *
     * @param keepOnAppending whether the adapter should request to load more when scrolling to the bottom.
     */
    fun onDataReady(keepOnAppending: Boolean) {
        dataPending.set(false)
        this.keepOnAppending.set(keepOnAppending)
        notifyDataSetChanged()
    }

    protected abstract fun onCreateFooterViewHolder(parent: ViewGroup): FOOTER_VH

    protected abstract fun onCreateNormalViewHolder(parent: ViewGroup): NORMAL_VH

    protected abstract fun onBindFooterView(holder: FOOTER_VH)

    protected abstract fun onBindNormalView(holder: NORMAL_VH, position: Int)
}
