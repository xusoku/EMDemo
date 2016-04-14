package cn.kejin.gitbook.controllers

import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import com.example.xusoku.bledemo.R
import com.example.xusoku.bledemo.views.recyclerview.ExRecyclerView

/**
 * Author: Kejin ( Liang Ke Jin )
 * Date: 2016/3/24
 */

/**
 * Control 'SwipeRefreshLayout' and 'ExRecyclerView' driver page
 */
class PageDriver(val refreshLayout: SwipeRefreshLayout?,
                 val exListView: ExRecyclerView,
                 val callback: ICallback) : PageController() {
    init {
        refreshLayout?.setOnRefreshListener { refresh() }

        exListView.setOnLoadMoreListener {
            loadMore()
        };
    }

    override fun onLoading(page: Int) {
        callback.onLoading(page)
    }

    override fun onRefresh() {
        if (refreshLayout!= null && !refreshLayout.isRefreshing) {
            refreshLayout.post({ refreshLayout.isRefreshing = true })
        }
    }

    override fun onRefreshFinish(result: Result, lastResult: Result) {
        when (result) {
            Result.SUCCESS -> {
                if (!exListView.hasFooter(footer))
                    exListView.addFooter(footer)
                showLoading()
            }

            Result.NO_MORE -> {
                if (!exListView.hasFooter(footer))
                    exListView.addFooter(footer)
                showNoMore()
            }

            Result.FAILED -> {
                callback.onRefreshFailed()
            }
        }
    }

    override fun onLoadMore(page: Int) {
        showLoading()
    }

    override fun onLoadMoreFinish(result: Result, lastResult: Result, loadingPage: Int) {
        when (result) {
            PageController.Result.SUCCESS -> {
                showLoading()
                exListView.endLoadMore()
            }
            PageController.Result.NO_MORE -> showNoMore()
            PageController.Result.FAILED -> showReload()
        }
    }

    override fun onFinish(result: Result, lastResult: Result, loadedPage: Int, loadingPage: Int) {
        refreshLayout?.post({ refreshLayout.isRefreshing = false })
    }

    /**
     * Footer control
     */
    private val footer = View.inflate(exListView.context, R.layout.layout_list_footer, null)

    private val loading by lazy { footer.findViewById(R.id.loading) }
    private val reload by lazy { footer.findViewById(R.id.reload) }
    private val noMore by lazy { footer.findViewById(R.id.noMore) }

    private fun showLoading() {
        loading.visibility = View.VISIBLE
        reload.visibility = View.GONE
        noMore.visibility = View.GONE
    }

    private fun showReload() {
        loading.visibility = View.GONE
        reload.visibility = View.VISIBLE
        noMore.visibility = View.GONE

        reload.findViewById(R.id.btn).setOnClickListener({ loadMore() })
    }

    private fun showNoMore() {
        loading.visibility = View.GONE
        reload.visibility = View.GONE
        noMore.visibility = View.VISIBLE
    }

    interface ICallback {
        fun onLoading(page: Int)
        fun onRefreshFailed()
    }
}