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
package com.syntepro.appbeneficiosbolivia.core.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.merckers.core.di.viewmodel.ViewModelFactory
import com.merckers.core.di.viewmodel.ViewModelKey
import com.syntepro.appbeneficiosbolivia.accounts.viewModel.AccountsViewModel
import com.syntepro.appbeneficiosbolivia.ui.agency.viewModel.AgencyViewModel
import com.syntepro.appbeneficiosbolivia.ui.commerce.viewModel.CommerceViewModel
import com.syntepro.appbeneficiosbolivia.ui.coupon.viewModel.CouponViewModel
import com.syntepro.appbeneficiosbolivia.ui.home.viewModel.HomeViewModel
import com.syntepro.appbeneficiosbolivia.ui.notifications.viewModel.NotificationViewModel
import com.syntepro.appbeneficiosbolivia.ui.profile.viewModel.ProfileViewModel
import com.syntepro.appbeneficiosbolivia.ui.shop.viewModel.ShopViewModel
import com.syntepro.appbeneficiosbolivia.ui.sudamericana.viewModel.SurveyViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(AccountsViewModel::class)
    abstract fun bindsMovieDetailsViewModel(movieDetailsViewModel: AccountsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindsHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CouponViewModel::class)
    abstract fun bindsCouponViewModel(couponViewModel: CouponViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CommerceViewModel::class)
    abstract fun bindsCommerceViewModel(commerceViewModel: CommerceViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AgencyViewModel::class)
    abstract fun bindsAgencyViewModel(agencyViewModel: AgencyViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NotificationViewModel::class)
    abstract fun bindsNotificationViewModel(notificationViewModel: NotificationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SurveyViewModel::class)
    abstract fun bindsSurveyViewModel(surveyViewModel: SurveyViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun bindsProfileViewModel(profileViewModel: ProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ShopViewModel::class)
    abstract fun bindsShopViewModel(shopViewModel: ShopViewModel): ViewModel

}