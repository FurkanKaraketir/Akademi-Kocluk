package com.karaketir.akademi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karaketir.akademi.adapter.PaywallAdapter
import com.karaketir.akademi.adapter.PaywallItem
import com.karaketir.akademi.services.buildError
import com.revenuecat.purchases.*
import com.revenuecat.purchases.models.StoreProduct
import com.revenuecat.purchases.models.SubscriptionOption

class PaywallFragment : Fragment() {
    private lateinit var root: View
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PaywallAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_paywall, container, false)

        recyclerView = root.findViewById(R.id.paywall_list)

        recyclerView.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearLayoutManager



        adapter = PaywallAdapter(null, didChoosePaywallItem = { item: PaywallItem ->
            when (item) {
                is PaywallItem.Product -> {
                    purchaseProduct(item.storeProduct)
                }

                is PaywallItem.Option -> {
                    purchaseOption(item.subscriptionOption)
                }

                is PaywallItem.Title -> {
                    // Do nothing
                }
            }
        })

        recyclerView.adapter = adapter

        /*
        Load offerings when the paywall is displayed
         */
        fetchOfferings()

        return root
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun fetchOfferings() {
        Purchases.sharedInstance.getOfferingsWith { offerings: Offerings ->
            adapter.offering = offerings.current
            adapter.notifyDataSetChanged()
        }
    }

    private fun purchaseProduct(item: StoreProduct) {
        Purchases.sharedInstance.purchaseWith(PurchaseParams.Builder(requireActivity(), item)
            .build(),
            onError = { error, userCancelled ->
                if (!userCancelled) {
                    buildError(context, error.message)
                }
            },
            onSuccess = { _, _ ->

                val newIntent = Intent(context, MainActivity::class.java)
                activity?.startActivity(newIntent)

                activity?.finish()
            })
    }

    private fun purchaseOption(item: SubscriptionOption) {
        Purchases.sharedInstance.purchaseWith(PurchaseParams.Builder(requireActivity(), item)
            .build(),
            onError = { error, userCancelled ->
                if (!userCancelled) {
                    buildError(context, error.message)
                }
            },
            onSuccess = { _, _ ->

                val newIntent = Intent(context, MainActivity::class.java)
                activity?.startActivity(newIntent)

                activity?.finish()
            })
    }
}