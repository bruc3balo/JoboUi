package com.example.joboui.clientUi.request;

import static com.example.joboui.SplashScreen.LOCATION_PERMISSION_CODE;
import static com.example.joboui.clientUi.ServiceRequestActivity.jobRequestForm;
import static com.example.joboui.globals.GlobalVariables.ASAP;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.services.NotificationService.myLocation;
import static com.example.joboui.utils.DataOps.TIMESTAMP_PATTERN;
import static com.example.joboui.utils.DataOps.getNairobiBounds;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.joboui.R;
import com.example.joboui.databinding.FragmentLocationRequestBinding;
import com.example.joboui.db.LocationsViewModel;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.example.joboui.utils.ConvertDate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;


public class LocationRequest extends Fragment implements OnMapReadyCallback {

    private final ArrayList<Address> placesList = new ArrayList<>();
    private LocationsViewModel locationsViewModel;
    private MapView mapView;
    private GoogleMap mMap;
    private FragmentLocationRequestBinding binding;
    private Calendar scheduledDate = Calendar.getInstance();


    public LocationRequest() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for requireActivity() fragment

        binding = FragmentLocationRequestBinding.inflate(inflater);

        SearchView locationSearch = binding.locationSearch;
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);

        locationSearch.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));

        locationSearch.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    System.out.println("FOCUS");
                } else {
                    System.out.println("NO FOCUS");
                }
            }
        });


        //set up search query listener on submit only
        locationSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new Handler().post(() -> {
                    System.out.println("CHANGE {" + query + "}");
                    if (!query.isEmpty()) {
                        getSuggestions(query, locationSearch);
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                return false;
            }
        });

        locationSearch.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("OPEN");
            }
        });

        locationSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                System.out.println("CLOSE");
                return false;
            }
        });

        locationsViewModel = new ViewModelProvider(requireActivity()).get(LocationsViewModel.class);
        mapView = binding.serviceLocationMap;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        jobRequestForm.setScheduled_at(ASAP);

        EditText descriptionField = binding.descriptionField;
        descriptionField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    jobRequestForm.setJob_description(s.toString());
                } else {
                    jobRequestForm.setJob_description(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //set up price estimate
        EditText estimate = binding.estimate;
        estimate.addTextChangedListener(priceRangeWatcher());


        //set up time estimate
        RadioGroup timeGroup = binding.timeGroup;
        timeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == binding.asap.getId()) {
                binding.scheduledTimeTv.setVisibility(View.GONE);
                jobRequestForm.setScheduled_at(ASAP);
            } else if (checkedId == binding.schedules.getId()) {
                binding.scheduledTimeTv.setVisibility(View.VISIBLE);
                final Calendar cldr = Calendar.getInstance();
                final Calendar chosenCalendar = Calendar.getInstance();




                TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
                    chosenCalendar.set(chosenCalendar.get(Calendar.YEAR), chosenCalendar.get(Calendar.MONTH), chosenCalendar.get(Calendar.DAY_OF_MONTH), chosenCalendar.get(Calendar.HOUR_OF_DAY), chosenCalendar.get(Calendar.MINUTE));

                    String h = String.valueOf(hourOfDay).length() == 1 ? "0".concat(String.valueOf(hourOfDay)) : String.valueOf(hourOfDay);
                    String m = String.valueOf(minute).length() == 1 ? "0".concat(String.valueOf(minute)) : String.valueOf(minute);


                    LocalTime picked = LocalTime.parse(h.concat(":").concat(m));

                    final int tense = chosenCalendar.getTime().compareTo(cldr.getTime());

                    if (tense < 0) {
                        binding.asap.setChecked(true);
                        Toast.makeText(requireContext(), "Time cannot be in the past", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (tense == 0) {
                        LocalTime localTime = LocalTime.now();
                        localTime = LocalTime.parse(localTime.getHour() + ":"+localTime.getMinute());

                        System.out.println("Time is picked" + picked);
                        System.out.println("Time is local" + localTime);


                        if (picked.isBefore(localTime)) {
                            binding.asap.setChecked(true);
                            Toast.makeText(requireContext(), "Time cannot be in the past", Toast.LENGTH_SHORT).show();
                        } else if (picked.equals(localTime)) {
                            binding.asap.setChecked(true);
                            Toast.makeText(requireContext(), "Changed to asap", Toast.LENGTH_SHORT).show();
                        } else {
                            scheduledDate = chosenCalendar;
                            String date = ConvertDate.formatDateReadable(scheduledDate.getTime());
                            binding.scheduledTimeTv.setText(date);
                            jobRequestForm.setScheduled_at(date);
                        }

                    } else {
                        scheduledDate = chosenCalendar;
                        String date = ConvertDate.formatDateReadable(scheduledDate.getTime());
                        binding.scheduledTimeTv.setText(date);
                        jobRequestForm.setScheduled_at(date);
                    }

                }, cldr.get(Calendar.HOUR_OF_DAY), cldr.get(Calendar.MINUTE), true);

                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                    chosenCalendar.set(year, month, dayOfMonth);
                    timePickerDialog.show();
                }, cldr.get(Calendar.YEAR), cldr.get(Calendar.MONTH), cldr.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.setOnShowListener(dialog -> datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis()));

                datePickerDialog.show();
                datePickerDialog.setOnCancelListener(dialog -> {
                    binding.asap.setChecked(true);
                    Toast.makeText(requireContext(), "Date Cancelled", Toast.LENGTH_SHORT).show();
                });
                timePickerDialog.setOnCancelListener(dialog -> {
                    binding.asap.setChecked(true);
                    Toast.makeText(requireContext(), "Time Cancelled", Toast.LENGTH_SHORT).show();
                });
            }
        });


        //default
        binding.asap.setChecked(true);

        return binding.getRoot();
    }

    private TextWatcher priceRangeWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (binding.estimate.getText().toString().isEmpty()) {
                    return;
                }

                try {
                    Double.parseDouble(binding.estimate.getText().toString());
                    jobRequestForm.setJob_price_range(binding.estimate.getText().toString());
                } catch (Exception e) {
                    if (e instanceof NumberFormatException) {
                        binding.estimate.setError("Value to be a number");
                        binding.estimate.requestFocus();
                        return;
                    }
                    e.printStackTrace();
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    //show location pop up when found
    private void showSuggestions(View anchor) {
        PopupMenu menu = new PopupMenu(requireActivity(), anchor);
        menu.getMenu().add("CANCEL").setTitle("CANCEL").setOnMenuItemClickListener(menuItem -> {
            menu.dismiss();
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        placesList.forEach(p -> menu.getMenu().add(p.getAddressLine(0)).setTitle(p.getAddressLine(0)).setIcon(R.drawable.right).setOnMenuItemClickListener(menuItem -> {
            String item = menuItem.getTitle().toString();
            Toast.makeText(requireActivity(), item, Toast.LENGTH_SHORT).show();
            binding.locationPickedTv.setText(item);
            Snackbar.make(binding.getRoot(), item, Snackbar.LENGTH_LONG).show();

            Optional<Address> address = placesList.stream().filter(p1 -> p1.getAddressLine(0).equals(item)).findFirst();
            if (address.isPresent()) {
                LatLng lat = new LatLng(address.get().getLatitude(), address.get().getLongitude());

                if (!getNairobiBounds().contains(lat)) {
                    Toast.makeText(requireActivity(), "Location is not in nairobi", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lat, 15));
                    addMarkerToMap(mMap, lat);
                    jobRequestForm.setJob_location(new Gson().toJson(lat));
                }


            }

            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS));
        menu.show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;


        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireActivity(), "Location Not granted", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);

            return;
        }

        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(() -> false);
        googleMap.setOnMyLocationClickListener(location -> Toast.makeText(requireActivity(), "I am here !!! ", Toast.LENGTH_SHORT).show());
        googleMap.setOnMapClickListener(latLng -> addMarkerToMap(googleMap, latLng));
        googleMap.setOnMapLongClickListener(latLng -> addMarkerToMap(googleMap, latLng));
        googleMap.setOnMarkerClickListener(marker -> false);
        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(@NotNull Marker marker) {

            }

            @Override
            public void onMarkerDrag(@NotNull Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(@NotNull Marker marker) {
                getFromMarker(marker);
            }
        });
        googleMap.setOnInfoWindowClickListener(this::getFromMarker);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.setLatLngBoundsForCameraTarget(getNairobiBounds());

        int padding = (int) (getResources().getDisplayMetrics().widthPixels * 0.2);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getNairobiBounds(), padding));


        //set up marker to my location
        addMarkerToMap(googleMap, myLocation);
    }

    private void addMarkerToMap(GoogleMap googleMap, LatLng latLng) {

        if (!getNairobiBounds().contains(latLng)) {
            Toast.makeText(requireActivity(), "Location is not in nairobi", Toast.LENGTH_SHORT).show();
            return;
        }

        @SuppressLint("UseCompatLoadingForDrawables") MarkerOptions markerOptions = new MarkerOptions().position(latLng).draggable(true).title(getFromLocation(latLng)).snippet("Click to confirm location").icon(BitmapDescriptorFactory.fromBitmap(drawableToBitmap(requireActivity().getDrawable(R.drawable.ic_service))));
        googleMap.clear();
        googleMap.addMarker(markerOptions);
        System.out.println("lat " + latLng.latitude + " long " + latLng.longitude);
    }


    //get address
    private String getFromMarker(Marker marker) {
        Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
            Address address = addresses.get(0);
            Snackbar.make(binding.getRoot(), address.getAddressLine(0), Snackbar.LENGTH_LONG).show();
            binding.locationPickedTv.setText(address.getAddressLine(0));
            try {
                jobRequestForm.setJob_location(getObjectMapper().writeValueAsString(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude)));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return address.getAddressLine(0);
        } catch (IOException | IllegalArgumentException e) {
            Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }


    //get address
    private String getFromLocation(LatLng latLng) {
        Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            Address address = addresses.get(0);
            Snackbar.make(binding.getRoot(), address.getAddressLine(0), Snackbar.LENGTH_LONG).show();
            binding.locationPickedTv.setText(address.getAddressLine(0));
            try {
                jobRequestForm.setJob_location(getObjectMapper().writeValueAsString(latLng));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            return address.getAddressLine(0);
        } catch (IOException | IllegalArgumentException e) {
            Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public static Address getAddressFromLocation(Context context, LatLng latLng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (!addresses.isEmpty()) {
                return addresses.get(0);
            } else {
                return null;
            }
        } catch (IOException | IllegalArgumentException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    //convert drawable to bitmap for map
    static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireActivity(), "Storage Permission Granted. You May proceed Now", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireActivity(), "Storage Permission Denied. If you denied requireActivity() you need to allow from settings", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }


    //get location suggestions
    private void getSuggestions(String query, View anchor) {
        if (query == null || query.isEmpty()) {
            placesList.clear();
            return;
        }


        //placesList.addAll(geocoder.getFromLocationName(query, 10));
        locationsViewModel.getSuggestedAddresses(query).observe(requireActivity(), addresses -> {
            if (addresses.isEmpty()) {
                Toast.makeText(requireActivity(), "Location not found", Toast.LENGTH_SHORT).show();
                return;
            }

            placesList.clear();
            placesList.addAll(addresses);
            System.out.println(Arrays.toString(addresses.stream().map(p -> p.getAddressLine(0)).toArray()));
            showSuggestions(anchor);
        });


    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}