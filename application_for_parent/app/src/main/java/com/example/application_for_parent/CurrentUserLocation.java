package com.example.application_for_parent;

import androidx.annotation.NonNull;

import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.user_location.UserLocationView;

interface CurrentUserLocation {
    void onObjectAdded(@NonNull UserLocationView var1);

    void onObjectRemoved(@NonNull UserLocationView var1);

    void onObjectUpdated(@NonNull UserLocationView var1, @NonNull ObjectEvent var2);
}
