package com.example.joboui.db;

import android.app.Application;
import android.location.Address;
import android.location.Geocoder;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocationsViewModel extends AndroidViewModel {

    private Geocoder geocoder;

    public LocationsViewModel(@NonNull Application application) {
        super(application);
        geocoder = new Geocoder(application);
    }


    //get location from geocoder by name of location
    private MutableLiveData<List<Address>> getSuggestions(String query) {
        MutableLiveData<List<Address>> suggestions = new MutableLiveData<>();

        try {
            suggestions.setValue(geocoder.getFromLocationName(query, 10));
        } catch (IOException e) {
            e.printStackTrace();
            suggestions.setValue(new ArrayList<>());
        }

        return suggestions;
    }

    public LiveData<List<Address>> getSuggestedAddresses(String query) {
        return getSuggestions(query);
    }
}
