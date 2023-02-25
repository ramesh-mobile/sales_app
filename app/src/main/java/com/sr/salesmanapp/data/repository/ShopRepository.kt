package com.sr.salesmanapp.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.sr.salesmanapp.data.model.pojo.ShopModel
import com.sr.salesmanapp.data.model.pojo.ShopModelResponse
import com.sr.salesmanapp.data.network.ResultStatus
import com.sr.salesmanapp.utils.ViewUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

interface ShopRepository {
    suspend fun getShopList(): Flow<ResultStatus<List<ShopModelResponse>>>
    fun putShopDetails(shopModel: ShopModel): Flow<ResultStatus<String>>
    fun updateShopDetails(shopModel: ShopModelResponse): Flow<ResultStatus<String>>
    fun deleteShopDetails(key: String): Flow<ResultStatus<String>>
}


@Singleton
class ShopRepositoryImpl @Inject constructor(
    private val dbReference: DatabaseReference
) : ShopRepository {

    override suspend fun getShopList() = callbackFlow{

            trySend(ResultStatus.Loading)

            val valueEvent = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = snapshot.children.map {
                        ShopModelResponse(it.key, it.getValue(ShopModel::class.java))
                    }
                    trySend(ResultStatus.Success(items))
                }

                override fun onCancelled(error: DatabaseError) {
                        trySend(ResultStatus.Failure(error.toException()))
                }
            }

            dbReference.addValueEventListener(valueEvent)

            awaitClose {
                dbReference.removeEventListener(valueEvent)
                close()
            }
    }

    override fun putShopDetails(shopModel: ShopModel): Flow<ResultStatus<String>> {
        return callbackFlow {
            trySend(ResultStatus.Loading)

            dbReference.push().setValue(shopModel)
                .addOnCompleteListener {
                    if (it.isSuccessful)
                        trySend(ResultStatus.Success("Data inserted successfully"))
                }
                .addOnFailureListener {
                    trySend(ResultStatus.Failure(it))
                }
            awaitClose {
                close()
            }
        }
    }

    override fun updateShopDetails(shopModelResponse: ShopModelResponse): Flow<ResultStatus<String>> {
        return callbackFlow {

            trySend(ResultStatus.Loading)
            val shopModel: ShopModel = shopModelResponse.shopModel!!
            var valueMap = HashMap<String, Any?>().apply {
                put("shopId", shopModel.shopId!!)
                put("shopName", shopModel.shopName!!)
                put("ownerName", shopModel.ownerName!!)
                put("contact_one", shopModel.contact_one!!)
                put("contact_two", shopModel.contact_two!!)
                put("address", shopModel.address!!)
                put("lat", shopModel.lat)
                put("lng", shopModel.lng)
                put("email", shopModel.email!!)
            }

            dbReference.child(shopModelResponse.key!!)
                .updateChildren(valueMap)
                .addOnCompleteListener {
                    if (it.isSuccessful)
                        trySend(ResultStatus.Success("Data updated successfully"))
                }
                .addOnFailureListener {
                    trySend(ResultStatus.Failure(it))
                }

            awaitClose {
                close()
            }
        }
    }

    override fun deleteShopDetails(key: String): Flow<ResultStatus<String>> {
        return callbackFlow {
            trySend(ResultStatus.Loading)
            dbReference.child(key).removeValue()
                .addOnCompleteListener {
                    if (it.isSuccessful)
                        trySend(ResultStatus.Success("Data deleted successfully"))
                }
                .addOnFailureListener {
                    trySend(ResultStatus.Failure(it))
                }

            awaitClose {
                close()
            }
        }
    }
}