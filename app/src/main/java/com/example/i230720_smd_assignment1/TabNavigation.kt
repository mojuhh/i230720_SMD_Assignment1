package com.example.i230720_smd_assignment1

import android.app.Activity
import android.content.Intent
import android.view.View

object TabNavigation {
    fun setupTabs(activity: Activity) {
        activity.findViewById<View>(R.id.tabHome)?.setOnClickListener {
            if (activity !is FeedActivity) {
                activity.startActivity(Intent(activity, FeedActivity::class.java))
            }
        }
        activity.findViewById<View>(R.id.tabFriends)?.setOnClickListener {
            if (activity !is FriendsActivity) {
                activity.startActivity(Intent(activity, FriendsActivity::class.java))
            }
        }
        activity.findViewById<View>(R.id.tabMarketplace)?.setOnClickListener {
            if (activity !is MarketplaceActivity) {
                activity.startActivity(Intent(activity, MarketplaceActivity::class.java))
            }
        }
        activity.findViewById<View>(R.id.tabNotifications)?.setOnClickListener {
            if (activity !is NotificationsActivity) {
                activity.startActivity(Intent(activity, NotificationsActivity::class.java))
            }
        }
        activity.findViewById<View>(R.id.tabMenu)?.setOnClickListener {
            if (activity !is MenuActivity) {
                activity.startActivity(Intent(activity, MenuActivity::class.java))
            }
        }
    }
}
