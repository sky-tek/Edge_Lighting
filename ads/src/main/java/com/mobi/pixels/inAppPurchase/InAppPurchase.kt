package com.mobi.pixels.inAppPurchase

import android.app.Activity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.mobi.pixels.enums.PurchaseType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object InAppPurchase {
    private var billingClient: BillingClient? = null
    private var listener: PurchaseListener? = null
    private fun getOrCreateBillingClient(activity: Activity): BillingClient {
        return billingClient ?: BillingClient.newBuilder(activity)
            .enablePendingPurchases()
            .setListener { billingResult, purchases ->
                // Handle purchase updates globally here if needed
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        verifySubPurchase(purchase)
                    }
                }
            }
            .build()
            .also { billingClient = it }
    }


    fun initialize(activity: Activity, productIds: List<String>, type: PurchaseType, onResult: (List<ProductDetails>) -> Unit) {
        val client = getOrCreateBillingClient(activity)
        establishConnection(client, productIds, type, onResult)
    }

    private fun establishConnection(client: BillingClient, productIds: List<String>, type: PurchaseType, onResult: (List<ProductDetails>) -> Unit) {
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    showProducts(client, productIds, type, onResult)
                }
            }

            override fun onBillingServiceDisconnected() {
                establishConnection(client, productIds, type, onResult)
            }
        })
    }

    private fun showProducts(client: BillingClient, productIds: List<String>, type: PurchaseType, onResult: (List<ProductDetails>) -> Unit) {
        val productList = productIds.map {
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(it)
                .setProductType(if (type == PurchaseType.InAppProduct) BillingClient.ProductType.INAPP else BillingClient.ProductType.SUBS)
                .build()
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        client.queryProductDetailsAsync(params) { billingResult, prodDetailsList ->
            CoroutineScope(Dispatchers.Main).launch {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        // Call the callback with the product details
                        onResult(prodDetailsList)
                } else {
                    // Handle the error case if needed
                    onResult(emptyList()) // Or you can handle the error differently
                }
            }

        }
    }

    private fun verifySubPurchase( purchase: Purchase) {
        val productIds = purchase.products
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                CoroutineScope(Dispatchers.Main).launch {
                    listener?.onPurchaseSuccess(productIds.first())
                }
            }
        }
    }

    fun launchPurchaseFlow(activity: Activity, productDetail: ProductDetails,purchaseListener: PurchaseListener) {
         listener = purchaseListener
        val client = getOrCreateBillingClient(activity)
        val productDetailsParamsList = listOf(
            ProductDetailsParams.newBuilder()
                .setProductDetails(productDetail)
                .setOfferToken(productDetail.subscriptionOfferDetails!![0].offerToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        client.launchBillingFlow(activity, billingFlowParams)
    }

    // Check for pending purchases in onResume or when needed
    fun checkPendingTransactions(activity: Activity, type: PurchaseType,purchaseListener: PurchaseListener) {
        listener = purchaseListener
        val client = getOrCreateBillingClient(activity)
        client.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(if (type == PurchaseType.InAppProduct) BillingClient.ProductType.INAPP else BillingClient.ProductType.SUBS).build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (purchase in purchases) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                        verifySubPurchase(purchase)
                    }
                }
            }
        }
    }

    // Call at app launch to check if the user has any existing subscriptions
    fun checkPurchaseStatus(activity: Activity, type: PurchaseType, onResult: (List<String>) -> Unit) {
        val client = getOrCreateBillingClient(activity)
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    client.queryPurchasesAsync(
                        QueryPurchasesParams.newBuilder()
                            .setProductType(if (type == PurchaseType.InAppProduct) BillingClient.ProductType.INAPP else BillingClient.ProductType.SUBS)
                            .build()
                    ) { billingResult, purchases ->
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            val purchasedProductIds = mutableListOf<String>()
                            if (purchases.isNotEmpty()) {
                                // Activate premium features if needed
                                purchases.forEach { purchase ->
                                    purchasedProductIds.add(purchase.products[0]) // Add the product ID to the list
                                }
                            }
                            CoroutineScope(Dispatchers.Main).launch {
                                // Return the list of purchased product IDs through the callback
                                onResult(purchasedProductIds)
                            }
                        } else {
                            CoroutineScope(Dispatchers.Main).launch {
                                // Handle query failure if needed
                                onResult(emptyList()) // Return an empty list if query failed
                            }
                        }
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        // Handle billing setup failure if needed
                        onResult(emptyList()) // Return an empty list if setup failed
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                // Optionally, handle disconnection
                checkPurchaseStatus(activity, type, onResult)
            }
        })
    }
}
