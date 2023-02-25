package com.sr.salesmanapp.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ae.blazeapp.network.response.locationSearch.GeoCoderResponse
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.mhv.lymouser.api.response.PlacesResponse
import com.mhv.lymouser.api.response.Prediction
import com.sr.salesmanapp.R
import com.sr.salesmanapp.SalesManApplication
import com.sr.salesmanapp.data.location.LocationUpdateManager
import com.sr.salesmanapp.data.model.response.location_search.LocationModel
import com.sr.salesmanapp.data.network.api.RestApi
import com.sr.salesmanapp.databinding.FragmentDeliveryDetailsMapBinding
import com.sr.salesmanapp.ui.base.BaseFragment
import com.sr.salesmanapp.ui.home.adapter.PlaceSearchAdapter
import com.sr.salesmanapp.utils.Constants
import com.sr.salesmanapp.utils.LocationUtils
import com.sr.salesmanapp.utils.ViewUtils.gone
import com.sr.salesmanapp.utils.ViewUtils.visible
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class ShopLocationMapFragment : BaseFragment<FragmentDeliveryDetailsMapBinding>(), GoogleApiClient.OnConnectionFailedListener,
    GoogleMap.OnMarkerClickListener  {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDeliveryDetailsMapBinding
        get() = FragmentDeliveryDetailsMapBinding::inflate

    @Inject
    lateinit var myApi : RestApi

    private var markerArray: HashMap<String?, Marker?> = HashMap()
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mCurrentLocationMarker: Marker? = null
    private var mCurrentLocationMarkerOption: MarkerOptions? = null
    private var mMap: GoogleMap? = null
    private var currentLocation: Location? = null
    private var isEditMode = false
    private var currentLatLong: LatLng? = null
    private var searchAdapter: PlaceSearchAdapter? = null
    private var addressToEdit: LocationModel? = null
    private var selectedLocation: String? = null
    private var lastSelectedLocation: LatLng? = null

    override fun initView() {


        init(savedInstanceState)
        setListener()

        searchAdapter = PlaceSearchAdapter()
        var layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.searchList.layoutManager = layoutManager

        searchAdapter?.onItemClickListener = object : PlaceSearchAdapter.OnItemClickListener {
            override fun onItemClick(
                prediction: Prediction
            ) {
                selectedLocation = prediction.description
                binding.placeSearch.setText(prediction.description)
                if (isEditMode) {
                    addressToEdit?.address = prediction.description
                }
                getLocationFromAddress(null, prediction.description!!)
            }

        }
        binding.searchList.adapter = searchAdapter
        addressToEdit = LocationModel()
        arguments?.getSerializable(Constants.DATA)?.let {
            addressToEdit = it as LocationModel
            var latLong = LatLng(addressToEdit?.latitude!!, addressToEdit?.longitude!!)
            isEditMode = true
            currentLatLong = latLong
            binding.placeSearch.setText(addressToEdit?.address)
        }
    }

    override fun observeData() {
    }

    private fun setListener() {
        setTextChangeListener()
        binding.confirmLocation.setOnClickListener {
            addAddress()
        }
        binding.clearSearch.setOnClickListener {
            binding.placeSearch?.setText("")
            binding.clearSearch?.gone()
        }
    }

    fun getLocationFromAddress(name: String?, address: String?) {
        showProgress()
        myApi.getGeoCodeFromAddress(address,true, Constants.PLACE_API_KEY)
            .enqueue(object : Callback<GeoCoderResponse> {
            override fun onResponse(
                call: Call<GeoCoderResponse>,
                response: Response<GeoCoderResponse>
            ) {
                hideProgress()

                val geoCoderResponse = response.body()
                geoCoderResponse?.let {
                    it.results?.let {
                        if (it.isNotEmpty()) {
                            val result = it[0]
                            val selectedLocation = LocationUtils.getLocationFromGeoCoder(result)
                            zoomToCurrentLocation(
                                LatLng(
                                    selectedLocation.latitude!!,
                                    selectedLocation.longitude!!
                                ), true
                            )
                            name?.let {
                                selectedLocation.name = name
                            }
                            binding.searchList.gone()

                        }
                    }
                }
            }

            override fun onFailure(call: Call<GeoCoderResponse>, t: Throwable) {
                hideProgress()
            }
        })
    }

    fun addAddress() {
        addressToEdit?.latitude = lastSelectedLocation?.latitude
        addressToEdit?.longitude = lastSelectedLocation?.longitude
        SalesManApplication.lastSelectedLocation = addressToEdit
        Constants.IS_LOCATION_UPDATE = true
        findNavController().popBackStack()
    }

    private fun setTextChangeListener() {
        binding.placeSearch?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

                if (s.toString().isNotEmpty() && binding.placeSearch.hasFocus()) {
                    binding.searchList.visibility = View.VISIBLE
                    binding.clearSearch?.visible()
                    getPlaces(s.toString())
                }
                else {
                    binding.searchList.visibility = View.GONE

                }
                if (s.toString().isNotEmpty()){		binding.clearSearch?.visible()}
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

            }
        })
    }


    private fun getPlaces(input: String) {
        var location = ""
        currentLocation?.let {
            location = "" + it.latitude + "," + it.longitude
        }
        var placeSearchApi =
            myApi.getPlacesSortByLocation(input, location,Constants.PLACE_RADIUS, /*getString(R.string.google_api_key)*/Constants.PLACE_API_KEY, "en")
        placeSearchApi?.enqueue(object : Callback<PlacesResponse> {
            override fun onResponse(call: Call<PlacesResponse>,response: Response<PlacesResponse>) {
                val placeResponse = response.body()
                placeResponse?.let {
                    if (it.status.equals("OK", ignoreCase = true)) {
                        val predictions = it.predictions
                        predictions?.let {
                            searchAdapter!!.setArrData(it)
                        }

                    }
                } ?: run {

                }
            }

            override fun onFailure(call: Call<PlacesResponse>, t: Throwable) {

            }
        })
    }

    private fun init(savedInstanceState: Bundle?) {
        binding.mapView?.onCreate(savedInstanceState)
        binding.mapView?.onResume()
        try {
            MapsInitializer.initialize(requireContext())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        initLocation()
        mGoogleApiClient = GoogleApiClient.Builder(requireContext())
            .enableAutoManage(requireActivity(), 0, this /* OnConnectionFailedListener */)
            .addApi(Places.GEO_DATA_API)
            .addApi(Places.PLACE_DETECTION_API)
            .build()

    }

    override fun onResume() {
        super.onResume()
        mGoogleApiClient?.stopAutoManage(requireActivity())
    }

    override fun onDestroy() {
        super.onDestroy()
        mGoogleApiClient?.stopAutoManage(requireActivity())
        mGoogleApiClient?.disconnect();

    }

    override fun onPause() {
        super.onPause()
        mGoogleApiClient?.stopAutoManage(requireActivity())
        mGoogleApiClient?.disconnect();

    }

    private fun initLocation() {
        if (checkLocationPermissionGranted()) {
            setUpMap()
            checkLocationAccuracy()
        } else {
            locationRequest.send()
            locationRequest.listeners {
                onAccepted {
                    setUpMap()
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
            setHighAccuracyMode(LocationUtils.getLocationMode(requireContext()))
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

    private fun setUpMap() {
        binding.mapView?.getMapAsync { googleMap ->
            mMap = googleMap
            mMap?.setOnCameraIdleListener(GoogleMap.OnCameraIdleListener { //get latlng at the center by calling
                val midLatLng: LatLng = mMap?.getCameraPosition()?.target!!
                lastSelectedLocation = midLatLng
                if (selectedLocation.isNullOrEmpty()) {
                    binding.placeSearch.setText(LocationUtils.getAddressFromLatLong(midLatLng.latitude, midLatLng.longitude, requireContext()))
                    addressToEdit?.address = LocationUtils.getAddressFromLatLong(midLatLng.latitude, midLatLng.longitude, requireContext())
                }

            })
            setLocationListener()
            mMap?.setOnMarkerClickListener(this)
            if (isEditMode)
                zoomToCurrentLocation(currentLatLong, true)
        }
    }

    private fun setLocationListener() {

        LocationUpdateManager.getLastLocation(
            WeakReference(requireContext()).get() as Context,
            object : LocationUpdateManager.LocationUpdateListener {
                @SuppressLint("MissingPermission")
                override fun locationUpdate(location: Location) {
                    this@ShopLocationMapFragment.currentLocation = location
                    if (!isEditMode)
                        zoomToCurrentLocation(location, true)
                }
            })


        LocationUpdateManager.getLocation(
            WeakReference(requireContext()).get() as Context,
            object : LocationUpdateManager.LocationUpdateListener {
                @SuppressLint("MissingPermission")
                override fun locationUpdate(location: Location) {
                    this@ShopLocationMapFragment.currentLocation = location
                    //zoomToCurrentLocation(location, false)
                    mMap?.isMyLocationEnabled = true
                    mMap?.uiSettings?.isMyLocationButtonEnabled = false
                    mMap?.uiSettings?.isMapToolbarEnabled = false
                }
            })
    }

    private fun zoomToCurrentLocation(location: Location?, needToUpdateCamera: Boolean = false) {
        if (null == location)
            return
        val latLng = LatLng(location.latitude, location.longitude)
        //val latLng = LatLng(24.453962, 54.393519)
        mCurrentLocationMarker?.let {
            it.position = latLng
            if (needToUpdateCamera) {
                updateCamera(latLng, true)
            }
        } ?: kotlin.run {
            mCurrentLocationMarkerOption = MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_location_on_24))
//			mCurrentLocationMarker =
//				mMap?.addMarker(mCurrentLocationMarkerOption)
            markerArray["0"] = mCurrentLocationMarker
            mCurrentLocationMarker?.isVisible = true
            updateCamera(latLng, false)

        }

    }

    private fun zoomToCurrentLocation(latLng: LatLng?, needToUpdateCamera: Boolean = false) {
        if (null == latLng)
            return

        //val latLng = LatLng(24.453962, 54.393519)
        lastSelectedLocation = latLng
        mCurrentLocationMarker?.let {
            it.position = latLng
            if (needToUpdateCamera) {
                updateCamera(latLng, true)
            }
        } ?: kotlin.run {
            mCurrentLocationMarkerOption = MarkerOptions()
                .position(latLng)
                .icon(null)
//			mCurrentLocationMarker =
//				mMap?.addMarker(mCurrentLocationMarkerOption)
            markerArray["0"] = mCurrentLocationMarker
            mCurrentLocationMarker?.isVisible = true
            updateCamera(latLng, false)

        }

    }

    private fun updateCamera(latLng: LatLng, needAnimation: Boolean = false) {

        if (needAnimation) {
            mMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    latLng,
                    13f
                )
            )
        } else {
            mMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    latLng,
                    13f
                )
            )
        }
        Handler().postDelayed(Runnable {

        }, 1000)
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
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            val permissionCoarseLocation = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            (permissionFineLocation == PackageManager.PERMISSION_GRANTED && permissionCoarseLocation == PackageManager.PERMISSION_GRANTED)
        } else {
            true
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        return false
    }

    fun showProgress(){
        (requireActivity() as HomeActivity).showProgress()
    }

    fun hideProgress(){
        (requireActivity() as HomeActivity).hideProgress()
    }

}