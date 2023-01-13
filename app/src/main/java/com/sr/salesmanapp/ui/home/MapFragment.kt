package com.sr.salesmanapp.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.sr.salesmanapp.R
import com.sr.salesmanapp.SalesManApplication
import com.sr.salesmanapp.databinding.FragmentMapBinding
import com.sr.salesmanapp.ui.base.BaseFragment
import com.sr.salesmanapp.utils.Constants
import com.sr.salesmanapp.utils.GpsUtils
import com.sr.salesmanapp.utils.LocationUtils
import com.sr.salesmanapp.utils.ViewUtils.error
import com.sr.salesmanapp.utils.ViewUtils.gone
import com.sr.salesmanapp.utils.ViewUtils.print
import com.sr.salesmanapp.utils.ViewUtils.visible

class MapFragment : BaseFragment<FragmentMapBinding>(), OnMapReadyCallback,
    OnMapClickListener, OnMarkerClickListener{

    private val TAG = "MapFragment"

    /*geofence variables start*/
    var googleMap: GoogleMap? = null
    var locationMarker: Marker? = null
    var shopLocationMarker: Marker? = null
    var latLng: LatLng? = null
    var lastLocation: Location? = null

    /**Provides access to the Fused Location Provider API. */
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    /**Contains parameters used by [com.google.android.gms.location.FusedLocationProviderApi].*/
    private var mLocationRequest: LocationRequest? = null

    /**Callback for changes in location.*/
    private var mLocationCallback: LocationCallback? = null

    private var UPDATE_INTERVAL_IN_MILLISECONDS: Long = Constants.miliSecondsForLocationUpdate
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = UPDATE_INTERVAL_IN_MILLISECONDS / 2

    /*geofence variables end*/

    private fun initializeGoogleMapFragment() {
        print(TAG, "initializeGoogleMapFragment: ")
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        setListener()
        checkPermission()
    }

    private fun checkPermission() {
        if (checkLocationPermissionGranted()) {
            createLocationRequest()
            requestLocationUpdates()
            checkLocationAccuracy()
        } else {
            locationRequest.send()
            locationRequest.listeners {
                onAccepted {
                    createLocationRequest()
                    requestLocationUpdates()
                    checkLocationAccuracy()
                }
                onDenied {
                    activity?.finish()
                }
                onPermanentlyDenied {
                }

            }
        }
    }

    private fun checkLocationAccuracy() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            setHighAccuracyMode(LocationUtils.getLocationMode(requireActivity()))
        }
    }

    private fun setHighAccuracyMode(mode: Int) {
        if (Settings.Secure.LOCATION_MODE_HIGH_ACCURACY != mode) {

            startActivityForResult(
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                Constants.LOCATION_ACCURACY_CODE
            )


        }
    }

    protected val locationRequest by lazy {
        permissionsBuilder(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
            .build()
    }

    fun checkLocationPermissionGranted(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= 23) {
            val permissionFineLocation = ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            val permissionCoarseLocation = ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            (permissionFineLocation == PackageManager.PERMISSION_GRANTED && permissionCoarseLocation == PackageManager.PERMISSION_GRANTED)
        } else {
            true
        }
    }

    private fun setListener() {
        fusedLocationUpdate()
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest?.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest?.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }


    private fun fusedLocationUpdate(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        try {
            binding.btnConfirm.setOnClickListener {
                shopLocationMarker?.let {
                    SalesManApplication.lastAddressLatLog = it.position
                    Constants.IS_LOCATION_UPDATE = true
                }?:kotlin.run {
                    locationMarker?.let {
                        SalesManApplication.lastAddressLatLog = it.position
                        Constants.IS_LOCATION_UPDATE = true
                    }
                }?:kotlin.run {

                }
                findNavController().popBackStack()
            }

            binding.fabCancel.setOnClickListener {
                shopLocationMarker?.remove()
                shopLocationMarker = null
                binding.fabCancel.gone()
            }

            mLocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    if (locationResult.lastLocation.isFromMockProvider) {
                        print(TAG, "onLocationResult: location is mocked")
                    }
                    lastLocation = locationResult.lastLocation
                    lastLocation?.latitude?.let {
                        latLng = LatLng(lastLocation?.latitude!!,lastLocation?.longitude!!)
                        setLocationOnMap(latLng)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun requestLocationUpdates() {
        try {
            GpsUtils.setRequestingLocationUpdates(requireActivity(), true)
            try {
                mFusedLocationClient?.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper())
            } catch (unlikely: SecurityException) {
                GpsUtils.setRequestingLocationUpdates(requireActivity(), false)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun removeLocationUpdates() {
        print(TAG,"Removing location updates")
        try {
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
            GpsUtils.setRequestingLocationUpdates(requireActivity(), false)
        } catch (unlikely: SecurityException) {
            GpsUtils.setRequestingLocationUpdates(requireActivity(), true)
            error(TAG,"Lost location permission. Could not remove updates. $unlikely")
        }
    }

    private fun setLocationOnMap(lastLocation: LatLng?) {
        if (googleMap != null) {
            zoomLevel = googleMap?.cameraPosition?.zoom!!
            (requireActivity() as HomeActivity).zoomLevel = (zoomLevel)
            latLng = LatLng(lastLocation?.latitude!!,lastLocation?.longitude)
            latLng?.let { markerLocation(it) }
        }
    }

    var zoomLevel = 22f

    private fun markerLocation(latLng: LatLng) {
        val title = "My Location"
        val markerOptions = MarkerOptions().position(latLng).title(title)
        if (googleMap != null) {
            locationMarker?.remove()
            locationMarker = googleMap?.addMarker(markerOptions)
            if(shopLocationMarker==null) {
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel)
                googleMap?.animateCamera(cameraUpdate)
            }
        }
    }

    private fun shopMarkerLocation(latLng: LatLng) {
        val title = "Shop Location"
        shopLocationMarker?.remove()

        shopLocationMarker = googleMap?.addMarker(MarkerOptions().
        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).
        position(latLng).title(title).draggable(true))

        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initialize google map fragment
        initializeGoogleMapFragment()
    }

    override fun onMapReady(gMap: GoogleMap?) {
        print(TAG, "onMapReady: ")
        this.googleMap = gMap
        googleMap?.setOnMapClickListener(this)
        googleMap?.setOnMarkerClickListener(this)
    }

    override fun onMapClick(p0: LatLng?) {
        p0?.let {
            binding.fabCancel.visible()
            shopMarkerLocation(it)
        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        return false
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMapBinding
        get() = FragmentMapBinding::inflate

    override fun initView() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeLocationUpdates()
    }

}