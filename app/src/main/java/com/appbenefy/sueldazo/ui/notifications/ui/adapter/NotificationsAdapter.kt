package com.appbenefy.sueldazo.ui.notifications.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.ui.notifications.model.NotificationResponse
import com.appbenefy.sueldazo.ui.notifications.ui.NotificationDiff
import com.appbenefy.sueldazo.ui.notifications.ui.activities.NotificationsActivity
import com.appbenefy.sueldazo.utils.Helpers
import kotlinx.android.synthetic.main.rv_notificaciones_items.view.*
import java.text.DateFormat
import javax.inject.Inject
import kotlin.properties.Delegates

class NotificationsAdapter @Inject constructor() :
        PagedListAdapter<NotificationResponse, NotificationsAdapter.ViewHolder>(
                NotificationDiff()
        ) {

    private var activity: NotificationsActivity? = null

    fun setActivity(activity: NotificationsActivity) { this.activity = activity }

    internal var collection: List<NotificationResponse> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount() = collection.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_notificaciones_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        collection[position].let { holder.bind(it) }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(model: NotificationResponse) {
//            if (!model.read)
//                view.card.setBackgroundColor(view.context.resources.getColor(R.color.secondary_card_gray))
//            else
//                view.card.setBackgroundColor(view.context.resources.getColor(R.color.transparent))

            view.title.text = model.title
            view.description.text = model.subtitle

            view.seeCoupon.visibility = if (model.idNotificationType == 3) View.VISIBLE else View.GONE
            view.seeCoupon.setOnClickListener {
                activity?.readNotification(model.idNotificationPush)
                activity?.openNotification(model.payload)
            }

            view.setOnClickListener {
                if (!model.read) {
                    activity?.readNotification(model.idNotificationPush)
                    activity?.openNotification(model.payload)
                }
            }
        }
    }

}