package com.example.vcanteenvendor;

import com.example.vcanteenvendor.POJO.BugReport;

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

    /*@GET("v1/menu-management/{vendorId}/menu")
    Call<CombinationAlacarteList> getAllMenu(@Path("vendorId") int vendorId); //UNUSED V1*/

    @GET("v2/menu-management/{vendorId}/menu")
    Call<CombinationAlacarteList> getAllMenuV2(@Path("vendorId") int vendorId);



    @POST("v1/user-authentication/vendor/check/token")
    Call<LoginResponse> sendLogin(@Body LoginVendor loginVendor); //UNUSED V1

//    @POST("v2/user-authentication/vendor/signin")
//    Call<LoginResponse> sendLogin(@Body LoginVendor loginVendor);

    @FormUrlEncoded
    @POST("v2/user-authentication/vendor/signin")
    Call<LoginResponse> sendLoginV2(@Field("email") String email,
                                    @Field("password") String password,
                                    @Field("firebaseToken") String firebaseToken);

    @FormUrlEncoded
    @PUT("v2/user-authentication/vendor/verify/email")
    Call<LoginResponse> checkIfEmailExist(@Field("email") String email);

    @FormUrlEncoded
    @PUT("v2/user-authentication/vendor/verify/facebook")
    Call<LoginResponse> sendLoginFacebook(@Field("email") String email,
                                          @Field("firebaseToken") String firebaseToken);

    @FormUrlEncoded
    @POST("v2/user-authentication/vendor/new")
    Call<LoginResponse> signUpNewVendor(@Field("email") String email,
                                        @Field("password") String password,
                                        @Field("accountType") String accountType,
                                        @Field("serviceProvider") String serviceProvider,
                                        @Field("accountNumber") String accountNumber,
                                        @Field("firebaseToken") String firebaseToken,
                                        @Field("vendorName") String vendorName,
                                        @Field("phoneNumber") String phoneNumber,
                                        @Field("fourDigitPin") String fourDigitPin);


    @PUT("v1/user-authentication/vendor/password/recover")
    Call<Void> recoverPass(@Body RecoverPass recoverPass);

    @FormUrlEncoded
    @PUT("v1/menu-management/vendorId/menu/foodId")
    Call<Void> editMenu(@Field("vendorId") int vendorId, @Field("foodId") int foodId,
                        @Field("foodName") String foodName,
                        @Field("price") int foodPrice,
                        @Field("foodStatus") String foodStatus,
                        @Field("foodType")String foodType,
                        @Field("foodImage") String foodImage);  //UNUSED V1

    @FormUrlEncoded
    @PUT("v2/menu-management/menu")
    Call<Void> editMenuV2(@Field("vendorId") int vendorId, @Field("foodId") int foodId,
                        @Field("foodName") String foodName,
                        @Field("foodPrice") int foodPrice,
                        @Field("foodStatus") String foodStatus,
                        @Field("foodType")String foodType,
                        @Field("foodImage") String foodImage,
                          @Field("categoryName") String categoryName,
                          @Field("prepareDuration") int prepareDuration);

    @FormUrlEncoded
    @POST("v1/menu-management/vendorId/menu")
    Call<Integer> addMenu(@Field("vendorId") int vendorId,
                        @Field("foodName") String foodName,
                        @Field("foodPrice") int foodPrice,
                        @Field("foodStatus") String foodStatus,
                        @Field("foodType")String foodType,
                        @Field("foodImage") String foodImage); //UNUSED V1

    @FormUrlEncoded
    @POST("v2/menu-management/menu")
    Call<Integer> addMenuV2(@Field("vendorId") int vendorId,
                          @Field("foodName") String foodName,
                          @Field("foodPrice") int foodPrice,
                          @Field("foodStatus") String foodStatus,
                          @Field("foodType")String foodType,
                          @Field("foodImage") String foodImage,
                            @Field("categoryName") String categoryName,
                            @Field("prepareDuration") int prepareDuration);

    @DELETE("v1/menu-management/{vendorId}/menu/{foodId}")
    Call<Void> deleteMenu(@Path("vendorId") int vendorId,
                          @Path("foodId") int foodId);

    @FormUrlEncoded
    @PUT("v1/settings/vendorId/status")
    Call<Void> editVendorStatus(@Field("vendorId") int vendorId,
                                @Field("vendorStatus") String vendorStatus);

    @FormUrlEncoded
    @PUT("v2/vendor-main/order/status")
    Call<Void> editOrderStatus(@Field("orderId") int orderId,
                                @Field("orderStatus") String orderStatus,
                               @Field("cancelReason") String cancelReason);

    @PUT("v1/user-authentication/vendor/password/change")
    Call<Void> resetPass(@Body ChangePass changePass);

    @POST("v1/user-authentication/vendor/verify/token")
    Call<TokenVerification> verifyToken(@Body Token token);

    @FormUrlEncoded
    @PUT("v1/settings/vendor/orders/cancel-all")
    Call<Void> cancelAllOrder(@Field("vendorId") int vendorId,
                              @Field("cancelReason") String cancelReason);

    @GET("v2/settings/{vendorId}/info/reviews")
    Call<ReviewList> getReviews(@Path("vendorId") int vendorId);

    @GET("v2/settings/{vendorId}/info")
    Call<VendorInfoArray> getVendorInfoV2(@Path("vendorId") int vendorId);

    @FormUrlEncoded
    @PUT("/v2/user-authentication/verify/pin")
    Call<Void> checkPin(@Field("vendorId") int vendorId, @Field("fourDigitPin") String fourDigitPin);

    @FormUrlEncoded
    @PUT("/v2/user-authentication/vendor/pin")
    Call<Void> changePin(@Field("vendorId") int vendorId, @Field("fourDigitPin") String fourDigitPin);

    @FormUrlEncoded
    @PUT("/v2/profile-management/vendor/profile")
    Call<Void> changeNameAndEmail(@Field("vendorId") int vendorId, @Field("restaurantName") String restaurantName, @Field("email") String email);

    @POST("/v2/settings/vendor/report")
    Call<Void> postBugReport(@Body BugReport bugReport);

    @FormUrlEncoded
    @PUT("/v2/profile-management/vendor/image")
    Call<Void> updateProfileImage(@Field("vendorId") int vendorId, @Field("vendorImage") String vendorImage);

    @FormUrlEncoded
    @POST("/v2/payments/vendor/link ")
    Call<Void> postLinkPayment(
            @Field("vendorId") int vendorId,
            @Field("serviceProvider") String serviceProvider,
            @Field("accountNumber") String accountNumber
    );
}
