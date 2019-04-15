package com.example.vcanteenvendor;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SalesRecordApi {


    @GET("v1/sales-record/vendor/{vendorId}/sales")
    Call<SalesRecord> getSalesRecord(@Path("vendorId") int vendorId);
}
