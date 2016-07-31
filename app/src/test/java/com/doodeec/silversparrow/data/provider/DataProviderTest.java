package com.doodeec.silversparrow.data.provider;

import com.doodeec.silversparrow.data.model.Contact;
import com.doodeec.silversparrow.data.model.Order;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import test.RxTest;
import test.UnitTestRunner;

/**
 * @author Dusan Bartos
 */
@RunWith(UnitTestRunner.class)
public class DataProviderTest {

    DataProvider dataProvider;
    StorageManager storageManager;
    ServiceProvider serviceProvider;

    BehaviorSubject<List<Contact>> mockContacts = BehaviorSubject.create(Collections.emptyList());
    BehaviorSubject<List<Order>> mockOrders = BehaviorSubject.create(Collections.emptyList());

    @Before public void setup() throws Exception {
        serviceProvider = Mockito.mock(ServiceProvider.class);
        Mockito.when(serviceProvider.getContacts()).thenReturn(mockContacts.asObservable());
        Mockito.when(serviceProvider.getOrders(Mockito.anyString())).thenReturn(mockOrders.asObservable());
        Mockito.when(serviceProvider.createContact(Mockito.any(), Mockito.any())).thenReturn(Observable.just(true));

        storageManager = Mockito.mock(StorageManager.class);
        Mockito.when(storageManager.observe(Contact.class)).thenReturn(mockContacts.asObservable());
        Mockito.when(storageManager.observe(Order.class)).thenReturn(mockOrders.asObservable());
        Mockito.when(storageManager.addAll(Mockito.any())).thenReturn(true);
        Mockito.when(storageManager.delete(Mockito.any())).thenReturn(true);

        this.dataProvider = new DataProvider(storageManager, serviceProvider);

        UnitTestRunner.computationScheduler.advanceTimeTo(0, TimeUnit.SECONDS);
    }

    @Test public void dataProvider_observeContacts() {
        Contact c1 = Mockito.mock(Contact.class);
        Contact c2 = Mockito.mock(Contact.class);
        Contact c3 = Mockito.mock(Contact.class);
        Contact c4 = Mockito.mock(Contact.class);
        Contact c5 = Mockito.mock(Contact.class);

        List<Contact> list1 = Arrays.asList(c1, c3);
        List<Contact> list2 = Arrays.asList(c1, c2, c3);
        List<Contact> list3 = Arrays.asList(c2, c4, c5);

        List<List<Contact>> results = Arrays.asList(
                Collections.emptyList(), list1, Collections.emptyList(), list2, list3);

        RxTest.checkObservableResult(dataProvider.observeContacts(), results, () -> {
            mockContacts.onNext(list1);
            UnitTestRunner.computationScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
            mockContacts.onNext(Collections.emptyList());
            UnitTestRunner.computationScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
            mockContacts.onNext(list2);
            UnitTestRunner.computationScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
            mockContacts.onNext(list3);
            UnitTestRunner.computationScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        });
    }

    @Test public void dataProvider_observeOrders() {
        Order o1 = Mockito.mock(Order.class);
        Mockito.when(o1.getContactId()).thenReturn("1234");
        Order o2 = Mockito.mock(Order.class);
        Mockito.when(o2.getContactId()).thenReturn("0123");
        Order o3 = Mockito.mock(Order.class);
        Mockito.when(o3.getContactId()).thenReturn("1234");
        Order o4 = Mockito.mock(Order.class);
        Mockito.when(o4.getContactId()).thenReturn("5555");
        Order o5 = Mockito.mock(Order.class);
        Mockito.when(o5.getContactId()).thenReturn("1234");

        List<Order> list1 = Arrays.asList(o1, o3);
        List<Order> list2 = Arrays.asList(o1, o2, o3);
        List<Order> list3 = Arrays.asList(o2, o4, o5);

        List<List<Order>> results = Arrays.asList(
                Collections.emptyList(),
                Arrays.asList(o1, o3),
                Collections.emptyList(),
                Arrays.asList(o1, o3),
                Arrays.asList(o5));

        RxTest.checkObservableResult(dataProvider.observeOrders("1234"), results, () -> {
            mockOrders.onNext(list1);
            UnitTestRunner.computationScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
            mockOrders.onNext(Collections.emptyList());
            UnitTestRunner.computationScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
            mockOrders.onNext(list2);
            UnitTestRunner.computationScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
            mockOrders.onNext(list3);
            UnitTestRunner.computationScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        });
    }

    @Test public void dataProvider_observeSelectedContact() {
        Contact c1 = Mockito.mock(Contact.class);
        Mockito.when(c1.getId()).thenReturn("1234");
        Contact c2 = Mockito.mock(Contact.class);
        Mockito.when(c2.getId()).thenReturn("3456");

        List<Contact> contacts = Arrays.asList(c1, c2);
        List<Contact> results = Arrays.asList(null, c1, null, c2);

        mockContacts.onNext(contacts);
        dataProvider.selectedContactId().onNext(null);
        UnitTestRunner.computationScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);

        RxTest.checkObservableResult(dataProvider.observeSelectedContact(), results, () -> {
            dataProvider.selectedContactId().onNext("1234");
            UnitTestRunner.computationScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
            dataProvider.selectedContactId().onNext("2222");
            UnitTestRunner.computationScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
            dataProvider.selectedContactId().onNext("3456");
            UnitTestRunner.computationScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        });
    }

    @Test public void dataProvider_observeErrors() {
        Throwable thr = new Throwable("mockError");
        List<Throwable> results = Arrays.asList(thr);

        RxTest.checkObservableResult(dataProvider.observeErrors(), results, () -> {
            mockContacts.onError(thr);
            UnitTestRunner.computationScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        });
    }

    @Test public void dataProvider_refreshContacts() {
        Contact c1 = Mockito.mock(Contact.class);
        Mockito.when(c1.getId()).thenReturn("1234");
        Mockito.when(c1.getImage()).thenReturn(null);
        Contact c2 = Mockito.mock(Contact.class);
        Mockito.when(c2.getId()).thenReturn("3456");
        Mockito.when(c2.getImage()).thenReturn("image.jpg");

        List<Contact> contacts = Arrays.asList(c1, c2);

        mockContacts.onNext(contacts);

        dataProvider.refreshContacts();
        UnitTestRunner.computationScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
    }

    @Test public void dataProvider_refreshContacts_error() {
        Exception ex = new Exception("Contacts error");
        Mockito.when(serviceProvider.getContacts()).thenReturn(Observable.error(ex));

        List<Throwable> results = Collections.singletonList(ex);

        RxTest.checkObservableResult(dataProvider.observeErrors(), results, () -> {
            dataProvider.refreshContacts();
            UnitTestRunner.computationScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        });
    }

    @Test public void dataProvider_refreshOrders() {
        Order o1 = Mockito.mock(Order.class);
        Order o2 = Mockito.mock(Order.class);
        Order o3 = Mockito.mock(Order.class);

        List<Order> orders = Arrays.asList(o1, o2, o3);

        mockOrders.onNext(orders);

        dataProvider.refreshOrders("1234");
        UnitTestRunner.computationScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
    }

    @Test public void dataProvider_refreshOrders_error() {
        Exception ex = new Exception("Contacts error");
        Mockito.when(serviceProvider.getOrders(Mockito.anyString())).thenReturn(Observable.error(ex));

        List<Throwable> results = Collections.singletonList(ex);

        RxTest.checkObservableResult(dataProvider.observeErrors(), results, () -> {
            dataProvider.refreshOrders("1234");
            UnitTestRunner.computationScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        });
    }

    @Test public void dataProvider_createContact() {
        List<Boolean> results = Collections.singletonList(true);

        RxTest.checkObservableResult(dataProvider.createContact("Meno", "Fon"), results);
    }

    @Test public void dataProvider_addData_success() {
        Contact c1 = Mockito.mock(Contact.class);
        Contact c2 = Mockito.mock(Contact.class);
        List<Contact> contacts = Arrays.asList(c1, c2);
        dataProvider.addData(contacts);

        List<Throwable> results = Collections.emptyList();

        RxTest.checkObservableResult(dataProvider.observeErrors(), results);
    }

    @Test public void dataProvider_addData_error() throws Exception {
        Exception ex = new Exception("Save error");

        Contact c1 = Mockito.mock(Contact.class);
        Contact c2 = Mockito.mock(Contact.class);
        List<Contact> contacts = Arrays.asList(c1, c2);

        Mockito.when(storageManager.addAll(contacts)).thenThrow(ex);

        List<Throwable> results = Collections.singletonList(ex);

        RxTest.checkObservableResult(dataProvider.observeErrors(), results, () -> {
            dataProvider.addData(contacts);
        });
    }

    @Test public void dataProvider_deleteData_success() {
        Contact c1 = Mockito.mock(Contact.class);
        Contact c2 = Mockito.mock(Contact.class);
        List<Contact> contacts = Arrays.asList(c1, c2);
        dataProvider.deleteData(contacts);

        List<Throwable> results = Collections.emptyList();

        RxTest.checkObservableResult(dataProvider.observeErrors(), results);
    }

    @Test public void dataProvider_deleteData_error() throws Exception {
        Exception ex = new Exception("Save error");

        Contact c1 = Mockito.mock(Contact.class);
        Contact c2 = Mockito.mock(Contact.class);
        List<Contact> contacts = Arrays.asList(c1, c2);

        Mockito.when(storageManager.delete(contacts)).thenThrow(ex);

        List<Throwable> results = Collections.singletonList(ex);

        RxTest.checkObservableResult(dataProvider.observeErrors(), results, () -> {
            dataProvider.deleteData(contacts);
        });
    }
}
