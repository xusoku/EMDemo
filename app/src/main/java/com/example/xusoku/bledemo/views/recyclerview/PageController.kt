package cn.kejin.gitbook.controllers

/**
 * Author: Kejin ( Liang Ke Jin )
 * Date: 2016/3/24
 */

/**
 * 1. pageIndex = 0, loading this page, eg Refresh
 * 2. pageIndex += 1, load next page,
 *      if load failed or no more data, pageIndex roll back, and page locked
 *      if load failed, it need call refresh or reload to unlock this page,
 *      if no more data, it need refresh all data to unlock
 */
abstract class PageController
{
    enum class Result {
        SUCCESS,
        FAILED,
        NO_MORE
    }

    /**
     * the result of last request
     */
    var lastResult = Result.SUCCESS
        private set

    /**
     * has loaded page index
     */
    var loadedPageIndex = -1
        private set

    /**
     * current loading page index
     * and it the request guard
     */
    var loadingPageIndex = -1
        private set


    fun isRequesting() = loadingPageIndex != -1

    fun isRefreshing() = loadingPageIndex == 0

    fun isLoadingMore() = loadingPageIndex > 0

    /**
     * Refresh Page
     * @return whether do refresh success
     */
    fun refresh() : Boolean {
        if (!loading(0)) {
            onRefreshBlocked()
            return false;
        }
        return true;
    }

    /**
     * Load One More Page
     * if no more data, can't loading
     */
    fun loadMore() : Boolean {
        val index = loadedPageIndex + 1
        if (lastResult == Result.NO_MORE || !loading(index)) {
            onLoadMoreBlocked(index)
            return false;
        }
        return true
    }

    @Synchronized
    private fun loading(page:Int): Boolean{
        if (isRequesting()) {
            return false;
        }

        loadingPageIndex = page;

        if (isRefreshing()) {
            onRefresh();
        }
        else if (isLoadingMore()) {
            onLoadMore(page);
        }

        onLoading(loadingPageIndex)
        return true
    }

    /**
     * Finish current request
     */
    fun finish(result: Result) {
        when (result) {
            Result.SUCCESS -> {
                loadedPageIndex = loadingPageIndex
            }

            Result.FAILED -> {
            }

            Result.NO_MORE -> {
            }
        }

        if (isRefreshing()) {
            onRefreshFinish(result, lastResult)
        }
        else {
            onLoadMoreFinish(result, lastResult, loadingPageIndex)
        }

        onFinish(result, lastResult, loadedPageIndex, loadingPageIndex)

        lastResult = result
        loadingPageIndex = -1
    }

    /**
     * call it before onLoading while refresh
     */
    abstract fun onRefresh()

    /**
     * call it if there has page is loading, can't do refresh
     */
    open fun onRefreshBlocked() {
        //
    }

    /**
     * call it before onFinish
     */
    abstract fun onRefreshFinish(result: Result, lastResult: Result)


    /**
     * call it before onLoading while load more
     */
    abstract fun onLoadMore(page: Int)

    /**
     * call it if there has page is loading, can't do load more
     */
    open fun onLoadMoreBlocked(page: Int) {
        //
    }

    /**
     * call it before onFinish
     */
    abstract fun onLoadMoreFinish(result: Result, lastResult: Result, loadingPage : Int)

    /**
     * do loading
     */
    abstract fun onLoading(page: Int)


    /**
     * call before loadingPageIndex and lastResult be reset,
     * @param result Current request result
     * @param lastResult last request result
     * @param loadedPage current loaded page index
     * @param loadingPage current loading page index
     */
    open fun onFinish(result: Result, lastResult: Result, loadedPage : Int, loadingPage: Int) {
        //
    }
}