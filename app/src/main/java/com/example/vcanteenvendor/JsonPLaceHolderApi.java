package com.example.vcanteenvendor;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface JsonPlaceHolderApi {

    @GET("v1/settings/{vendorId}/info")
    Call<VendorInfoArray> getVendorInfo(@Path("vendorId") int vendorId);

    @GET("v1/vendor-main/{vendorId}/orders")
    Call<OrderList> getOrder(@Path("vendorId") int vendorId);

    @GET("v1/menu-management/{vendorId}/menu")
    Call<CombinationAlacarteList> getAllMenu(@Path("vendorId") int vendorId);





    @POST("v1/user-authentication/vendor/check/token")
    Call<LoginResponse> sendLogin(@Body LoginVendor loginVendor);

    @PUT("v1/user-authentication/vendor/password/recover")
    Call<Void> recoverPass(@Body RecoverPass recoverPass);

    @FormUrlEncoded
    @PUT("v1/menu-management/vendorId/menu/foodId")
    Call<Void> editMenu(@Field("vendorId") int vendorId, @Field("foodId") int foodId,
                        @Field("foodName") String foodName,
                        @Field("price") int foodPrice,
                        @Field("foodStatus") String foodStatus,
                        @Field("foodType")String foodType,
                        @Field("foodImage") String foodImage);

    @FormUrlEncoded
    @POST("v1/menu-management/vendorId/menu")
    Call<Integer> addMenu(@Field("vendorId") int vendorId,
                        @Field("foodName") String foodName,
                        @Field("price") int foodPrice,
                        @Field("foodStatus") String foodStatus,
                        @Field("foodType")String foodType,
                        @Field("foodImage") String foodImage);

    @DELETE("v1/menu-management/{vendorId}/menu/{foodId}")
    Call<Void> deleteMenu(@Path("vendorId") int vendorId,
                          @Path("foodId") int foodId);

    @FormUrlEncoded
    @PUT("v1/settings/vendorId/status")
    Call<Void> editVendorStatus(@Field("vendorId") int vendorId,
                                @Field("vendorStatus") String vendorStatus);

    @FormUrlEncoded
    @PUT("v1/vendor-main/order/status")
    Call<Void> editOrderStatus(@Field("orderId") int orderId,
                                @Field("orderStatus") String orderStatus);

    @PUT("v1/user-authentication/vendor/password/change")
    Call<Void> resetPass(@Body ChangePass changePass);

    @POST("v1/user-authentication/vendor/verify/token")
    Call<TokenVerification> verifyToken(@Body Token token);

    @FormUrlEncoded
    @PUT("v1/settings/vendor/orders/cancel-all")
    Call<Void> cancelAllOrder(@Field("vendorId") int vendorId);
}
