package com.doodeec.silversparrow.data.provider;

import com.doodeec.silversparrow.data.model.Contact;
import com.doodeec.silversparrow.data.model.Order;
import com.doodeec.silversparrow.data.model.ResponseError;
import com.doodeec.silversparrow.network.RestService;
import com.doodeec.silversparrow.network.response.ContactsResponse;
import com.doodeec.silversparrow.network.response.CreateContactResponse;
import com.doodeec.silversparrow.network.response.OrdersResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import test.RxTest;
import test.UnitTestRunner;

/**
 * @author Dusan Bartos
 */
@RunWith(UnitTestRunner.class)
public class ServiceProviderTest {

    ServiceProvider serviceProvider;
    RestService restService;

    @Before public void setup() throws Exception {
        restService = Mockito.mock(RestService.class);

        this.serviceProvider = new ServiceProvider(restService);

        UnitTestRunner.computationScheduler.advanceTimeTo(0, TimeUnit.SECONDS);
    }

    @Test public void serviceProvider_createContact_success() {
        Mockito.when(restService.createContact(Mockito.any())).thenReturn(Observable.just(new CreateContactResponse()));

        List<Boolean> results = Collections.singletonList(true);
        RxTest.checkObservableResult(serviceProvider.createContact("janko", "01010"), results);
    }

    @Test public void serviceProvider_createContact_error() {
        Exception ex = new Exception("Error");
        Mockito.when(restService.createContact(Mockito.any())).thenReturn(Observable.error(ex));

        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        serviceProvider.createContact("janko", "01010")
                .observeOn(Schedulers.immediate())
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.requestMore(Long.MAX_VALUE);
        testSubscriber.assertError(ex);
    }

    @Test public void serviceProvider_getContacts_success() {
        List<Contact> contacts = Arrays.asList(Mockito.mock(Contact.class), Mockito.mock(Contact.class));
        ContactsResponse contactsResponse = Mockito.mock(ContactsResponse.class);
        Mockito.when(contactsResponse.getContacts()).thenReturn(contacts);
        Mockito.when(restService.getContacts()).thenReturn(Observable.just(contactsResponse));

        List<List<Contact>> results = Collections.singletonList(contacts);
        RxTest.checkObservableResult(serviceProvider.getContacts(), results);
    }

    @Test public void serviceProvider_getContacts_error() {
        Exception ex = new Exception("Error");
        Mockito.when(restService.getContacts()).thenReturn(Observable.error(ex));

        TestSubscriber<List<Contact>> testSubscriber = new TestSubscriber<>();
        serviceProvider.getContacts()
                .observeOn(Schedulers.immediate())
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.requestMore(Long.MAX_VALUE);
        testSubscriber.assertError(ex);
    }

    @Test public void serviceProvider_getOrders_success() {
        List<Order> orders = Arrays.asList(Mockito.mock(Order.class), Mockito.mock(Order.class));
        OrdersResponse ordersResponse = Mockito.mock(OrdersResponse.class);
        Mockito.when(ordersResponse.getOrders()).thenReturn(orders);
        Mockito.when(restService.getOrders(Mockito.anyString())).thenReturn(Observable.just(ordersResponse));

        List<List<Order>> results = Collections.singletonList(orders);
        RxTest.checkObservableResult(serviceProvider.getOrders("1234"), results);
    }

    @Test public void serviceProvider_getOrders_error() {
        Exception ex = new Exception("Error");
        Mockito.when(restService.getOrders(Mockito.anyString())).thenReturn(Observable.error(ex));

        TestSubscriber<List<Order>> testSubscriber = new TestSubscriber<>();
        serviceProvider.getOrders("1234")
                .observeOn(Schedulers.immediate())
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.requestMore(Long.MAX_VALUE);
        testSubscriber.assertError(ex);
    }

    @Test public void serviceProvider_baseRequest_error() {
        ResponseError error = Mockito.mock(ResponseError.class);
        Mockito.when(error.getMessage()).thenReturn("Response Error");
        ContactsResponse contactsResponse = Mockito.mock(ContactsResponse.class);
        Mockito.when(contactsResponse.hasError()).thenReturn(true);
        Mockito.when(contactsResponse.getError()).thenReturn(error);
        Mockito.when(restService.getContacts()).thenReturn(Observable.just(contactsResponse));

        TestSubscriber<List<Contact>> testSubscriber = new TestSubscriber<>();
        serviceProvider.getContacts()
                .observeOn(Schedulers.immediate())
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.requestMore(Long.MAX_VALUE);
        testSubscriber.assertError(Throwable.class);
    }
}
