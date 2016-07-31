package com.doodeec.silversparrow.network;

import com.doodeec.silversparrow.network.request.CreateContactRequest;
import com.doodeec.silversparrow.network.response.ContactsResponse;
import com.doodeec.silversparrow.network.response.CreateContactResponse;
import com.doodeec.silversparrow.network.response.OrdersResponse;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Retrofit interface which defines REST API
 * @author Dusan Bartos
 */
public interface RestService {
    @GET("contactendpoint/v1/contact")
    Observable<ContactsResponse> getContacts();

    @GET("orderendpoint/v1/order/{contact_id}")
    Observable<OrdersResponse> getOrders(@Path("contact_id") String contactId);

    @POST("contactendpoint/v1/contact")
    Observable<CreateContactResponse> createContact(@Body CreateContactRequest createContactRequest);
}
