package com.example.xusoku.bledemo.api;

import com.example.xusoku.bledemo.Grils;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by xusoku on 2016/4/5.
 */
public interface ApiService {
        //http://www.tngou.net/tnfs/api/list?page=1&rows=10
        @GET("tnfs/api/list")
        Call<Grils> listGrils(@Query("id") int id,@Query("page") int page,@Query("rows") int rows);
}

