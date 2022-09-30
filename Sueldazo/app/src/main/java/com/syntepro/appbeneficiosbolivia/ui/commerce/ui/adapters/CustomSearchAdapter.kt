package com.syntepro.appbeneficiosbolivia.ui.commerce.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cursoradapter.widget.CursorAdapter
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.ui.commerce.ui.activities.CommerceDetail2Activity
import com.syntepro.appbeneficiosbolivia.ui.coupon.ui.activities.CouponDetail2Activity
import com.syntepro.appbeneficiosbolivia.ui.coupon.ui.activities.CouponListActivity
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions

class CustomSearchAdapter(val context: Context, cursor: Cursor?) : CursorAdapter(context, cursor, 0) {

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.suggestion_row, parent, false)
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val mImage = view.findViewById<View>(R.id.imageId) as ImageView
        val mName = view.findViewById<View>(R.id.nameId) as TextView
        val mRowId = view.findViewById<View>(R.id.rowId) as LinearLayout
        val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
        val subtitle = cursor.getString(cursor.getColumnIndexOrThrow("subtitle"))
        val image = cursor.getString(cursor.getColumnIndexOrThrow("image"))
        val reference = cursor.getString(cursor.getColumnIndexOrThrow("reference"))
        val type = cursor.getString(cursor.getColumnIndexOrThrow("type"))

        var name = ""
        if (type.toInt() == Constants.SUGGESTION_COMMERCE) {
            name = title
        } else if (type.toInt() == Constants.SUGGESTION_COUPON || type.toInt() == Constants.SUGGESTION_VIP_COUPON || type.toInt() == Constants.SUGGESTION_BRANCH_OFFICE) {
             name = "$title - $subtitle"
        }

        if(type.toInt() == Constants.SUGGESTION_COUPON || type.toInt() == Constants.SUGGESTION_VIP_COUPON || type.toInt() == Constants.SUGGESTION_COMMERCE || type.toInt() == Constants.SUGGESTION_BRANCH_OFFICE) {
            mName.text = name
            Functions.showRoundedImage(image, mImage)
        } else {
            mName.text = Functions.fromHtml(name)
            mImage.setImageResource(image.toInt())
        }

        mRowId.setOnClickListener {
            when (type.toInt()) {
                Constants.SUGGESTION_COUPON -> showCouponDetail(reference)
                Constants.SUGGESTION_COMMERCE -> showCommerceDetail(reference)
                Constants.SUGGESTION_BRANCH_OFFICE -> showBranOfficeCoupons(reference, subtitle)
                Constants.SUGGESTION_TOP_TRENDS -> showAssocList()
                Constants.SUGGESTION_VIP_COUPON -> showVIPCouponDetail(reference)
            }
        }

    }

    @SuppressLint("DefaultLocale")
    private fun showCouponDetail(id: String) {
        val intent = Intent(context, CouponDetail2Activity::class.java)
        intent.putExtra("couponId", id.toUpperCase())
        intent.putExtra("couponType", "0")
        context.startActivity(intent)
    }

    @SuppressLint("DefaultLocale")
    private fun showCommerceDetail(id: String) {
        val intent = Intent(context, CommerceDetail2Activity::class.java)
        intent.putExtra("commerceId", id.toUpperCase())
        context.startActivity(intent)
        /*val intent = Intent(context, AssociationsListActivity::class.java)
        intent.putExtra("paramType", AssociationsListActivity.QUERY_TYPE_TOP)
        intent.putExtra("paramValue", "")
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        (context as BaseActivity).startActivity(intent)*/
    }

    private fun showBranOfficeCoupons(id: String, name: String) {
        val intent = Intent(context, CouponListActivity::class.java)
        intent.putExtra("commerceId", id)
        intent.putExtra("commerceName", name)
        context.startActivity(intent)
    }

    private fun showAssocList() {
        /*val intent = Intent(context, AssociationsListActivity::class.java)
        intent.putExtra("paramType", AssociationsListActivity.QUERY_TYPE_COUNTRY)
        intent.putExtra("paramValue", country)
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        (context as BaseActivity).startActivity(intent)*/
    }

    @SuppressLint("DefaultLocale")
    private fun showVIPCouponDetail(id: String) {
        val intent = Intent(context, CouponDetail2Activity::class.java)
        intent.putExtra("couponId", id.toUpperCase())
        intent.putExtra("couponType", "1")
        context.startActivity(intent)
    }

}