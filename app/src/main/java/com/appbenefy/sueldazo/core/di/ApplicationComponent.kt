/**
 * Copyright (C) 2018 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appbenefy.sueldazo.core.di

import com.appbenefy.sueldazo.ui.MainActivity
import com.appbenefy.sueldazo.core.AndroidApplication
import com.appbenefy.sueldazo.core.di.viewmodel.ViewModelModule
import com.appbenefy.sueldazo.ui.category.CategoryActivity
import com.appbenefy.sueldazo.ui.commerce.ui.activities.BranchFilterActivity
import com.appbenefy.sueldazo.ui.commerce.ui.activities.Commerce2Activity
import com.appbenefy.sueldazo.ui.commerce.ui.activities.CommerceDetail2Activity
import com.appbenefy.sueldazo.ui.commerce.ui.activities.CommerceList2Activity
import com.appbenefy.sueldazo.ui.coupon.AgencyActivity
import com.appbenefy.sueldazo.ui.coupon.AgencyDetailActivity
import com.appbenefy.sueldazo.ui.coupon.ui.activities.BestDiscountListActivity
import com.appbenefy.sueldazo.ui.coupon.ui.activities.CouponDetail2Activity
import com.appbenefy.sueldazo.ui.coupon.ui.activities.CouponListActivity
import com.appbenefy.sueldazo.ui.coupon.ui.activities.RatingActivity
import com.appbenefy.sueldazo.ui.home.ui.fragments.BenefyFragment
import com.appbenefy.sueldazo.ui.home.ui.fragments.FavoriteFragment
import com.appbenefy.sueldazo.ui.home.HomeActivity
import com.appbenefy.sueldazo.ui.home.ui.fragments.HomeFragment
import com.appbenefy.sueldazo.ui.notifications.ui.activities.NotificationsActivity
import com.appbenefy.sueldazo.ui.profile.ui.activities.StatisticsActivity
import com.appbenefy.sueldazo.ui.profile.ui.activities.TransactionsActivity
import com.appbenefy.sueldazo.ui.profile.ui.activities.TransactionsFilterDialog
import com.appbenefy.sueldazo.ui.profile.ui.activities.TransactionsInfoDialog
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, ViewModelModule::class])
interface ApplicationComponent {
    fun inject(application: AndroidApplication)
    fun inject(mainActivity: MainActivity)
    fun inject(homeActivity: HomeActivity)

    // Rodrigo Osegueda
    fun inject(commerce2Activity: Commerce2Activity)
    fun inject(branchFilterActivity: BranchFilterActivity)
    fun inject(commerceDetail2Activity: CommerceDetail2Activity)
    fun inject(agencyActivity: AgencyActivity)
    fun inject(couponDetail2Activity: CouponDetail2Activity)
    fun inject(categoryActivity: CategoryActivity)
    fun inject(notificationsActivity: NotificationsActivity)
    fun inject(commerceList2Activity: CommerceList2Activity)
    fun inject(ratingActivity: RatingActivity)
    fun inject(statisticsActivity: StatisticsActivity)
    fun inject(agencyDetailActivity: AgencyDetailActivity)
    fun inject(couponListActivity: CouponListActivity)
    fun inject(favoriteFragment: FavoriteFragment)
    fun inject(homeFragment: HomeFragment)
    fun inject(bestDiscountListActivity: BestDiscountListActivity)
    fun inject(benefyFragment: BenefyFragment)
    fun inject(transactionsActivity: TransactionsActivity)
    fun inject(transactionsInfoDialog: TransactionsInfoDialog)
    fun inject(transactionsFilterDialog: TransactionsFilterDialog)
}
