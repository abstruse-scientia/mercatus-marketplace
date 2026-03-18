package com.scientia.mercatus.factory;


import com.scientia.mercatus.entity.AddressSnapshot;
import com.scientia.mercatus.entity.UserAddress;

public class AddressFactory {

    public static UserAddress withSnapshot  (Long userId){
        UserAddress address = new UserAddress();
        address.setUserId(userId);
        address.setAddressSnapshot(createSnapshot());
        address.setActive(true);
        return address;
    }
    public static AddressSnapshot createSnapshot() {
        AddressSnapshot snapshot = new AddressSnapshot();
        snapshot.setFullName("John Doe");
        snapshot.setMobileNumber("881924800");
        snapshot.setFlatHouse("434 California");
        snapshot.setArea("Downtown");
        snapshot.setLandmark("Big Mac");
        snapshot.setPincode("823176");
        snapshot.setCity("New York");
        snapshot.setState("NY");
        snapshot.setCountry("Bosnia");
        return snapshot;
    }

    public static UserAddress createDefault (Long userId){
        UserAddress address = new UserAddress();
        address.setUserId(userId);
        address.setAddressSnapshot(createSnapshot());
        address.setActive(true);
        address.setDefault(true);
        return address;
    }
}
