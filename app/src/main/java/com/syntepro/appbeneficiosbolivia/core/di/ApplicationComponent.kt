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
package com.syntepro.appbeneficiosbolivia.core.di

import com.syntepro.appbeneficiosbolivia.ui.MainActivity
import com.syntepro.appbeneficiosbolivia.core.AndroidApplication
import com.syntepro.appbeneficiosbolivia.core.di.viewmodel.ViewModelModule
import com.syntepro.appbeneficiosbolivia.ui.category.CategoryActivity
import com.syntepro.appbeneficiosbolivia.ui.commerce.ui.activities.BranchFilterActivity
import com.syntepro.appbeneficiosbolivia.ui.commerce.ui.activities.Commerce2Activity
import com.syntepro.appbeneficiosbolivia.ui.commerce.ui.activities.CommerceDetail2Activity
import com.syntepro.appbeneficiosbolivia.ui.commerce.ui.activities.CommerceList2Activity
import com.syntepro.appbeneficiosbolivia.ui.coupon.AgencyActivity
import com.syntepro.appbeneficiosbolivia.ui.coupon.AgencyDetailActivity
import com.syntepro.appbeneficiosbolivia.ui.coupon.ui.activities.BestDiscountListActivity
import com.syntepro.appbeneficiosbolivia.ui.coupon.ui.activities.CouponDetail2Activity
import com.syntepro.appbeneficiosbolivia.ui.coupon.ui.activities.CouponListActivity
import com.syntepro.appbeneficiosbolivia.ui.coupon.ui.activities.RatingActivity
import com.syntepro.appbeneficiosbolivia.ui.home.ui.fragments.BenefyFragment
import com.syntepro.appbeneficiosbolivia.ui.home.ui.fragments.FavoriteFragment
import com.syntepro.appbeneficiosbolivia.ui.home.HomeActivity
import com.syntepro.appbeneficiosbolivia.ui.home.ui.fragments.HomeFragment
import com.syntepro.appbeneficiosbolivia.ui.notifications.ui.activities.NotificationsActivity
import com.syntepro.appbeneficiosbolivia.ui.profile.ui.activities.StatisticsActivity
import com.syntepro.appbeneficiosbolivia.ui.profile.ui.activities.TransactionsActivity
import com.syntepro.appbeneficiosbolivia.ui.profile.ui.activities.TransactionsInfoDialog
import com.syntepro.appbeneficiosbolivia.ui.shop.ui.activities.GiftDetailActivity
import com.syntepro.appbeneficiosbolivia.ui.shop.ui.activities.ShopActivity
import com.syntepro.appbeneficiosbolivia.ui.sudamericana.ui.activities.SurveyActivity
import com.syntepro.appbeneficiosbolivia.ui.wallet.MilesGoalsInfoDialog
import com.syntepro.appbeneficiosbolivia.ui.wallet.StampCardInfoDialog
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, ViewModelModule::class])
interface ApplicationComponent {
    fun inject(application: AndroidApplication)
    fun inject(mainActivity: MainActivity)
    fun inject(homeActivity: HomeActivity)

    // Rodrigo Osegueda
    fun inject(stampCardInfoDialog: StampCardInfoDialog)
    fun inject(milesGoalsInfoDialog: MilesGoalsInfoDialog)
    fun inject(commerce2Activity: Commerce2Activity)
    fun inject(branchFilterActivity: BranchFilterActivity)
    fun inject(commerceDetail2Activity: CommerceDetail2Activity)
    fun inject(agencyActivity: AgencyActivity)
    fun inject(couponDetail2Activity: CouponDetail2Activity)
    fun inject(categoryActivity: CategoryActivity)
    fun inject(notificationsActivity: NotificationsActivity)
    fun inject(commerceList2Activity: CommerceList2Activity)
    fun inject(surveyActivity: SurveyActivity)
    fun inject(ratingActivity: RatingActivity)
    fun inject(statisticsActivity: StatisticsActivity)
    fun inject(agencyDetailActivity: AgencyDetailActivity)
    fun inject(couponListActivity: CouponListActivity)
    fun inject(favoriteFragment: FavoriteFragment)
    fun inject(homeFragment: HomeFragment)
    fun inject(bestDiscountListActivity: BestDiscountListActivity)
    fun inject(benefyFragment: BenefyFragment)
    fun inject(giftDetailActivity: GiftDetailActivity)
    fun inject(transactionsActivity: TransactionsActivity)
    fun inject(transactionsInfoDialog: TransactionsInfoDialog)
    fun inject(shopActivity: ShopActivity)
}
