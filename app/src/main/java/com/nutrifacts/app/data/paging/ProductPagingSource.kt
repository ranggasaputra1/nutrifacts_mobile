package com.nutrifacts.app.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nutrifacts.app.data.model.ProductModel // Import ProductModel yang sudah diperbarui
import com.nutrifacts.app.data.response.GetAllProductResponse
import com.nutrifacts.app.data.retrofit.APIService

class ProductPagingSource(private val apiService: APIService) : PagingSource<Int, ProductModel>() { // ✅ Perbarui tipe data di sini

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ProductModel>): Int? { // ✅ Perbarui tipe data di sini
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductModel> { // ✅ Perbarui tipe data di sini
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getAllProducts()

            LoadResult.Page(
                data = responseData.products,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.products.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}