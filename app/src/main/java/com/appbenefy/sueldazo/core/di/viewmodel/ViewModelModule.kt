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
package com.appbenefy.sueldazo.core.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.merckers.core.di.viewmodel.ViewModelFactory
import com.merckers.core.di.viewmodel.ViewModelKey
import com.appbenefy.sueldazo.accounts.viewModel.AccountsViewModel
import com.appbenefy.sueldazo.ui.agency.viewModel.AgencyViewModel
import com.appbenefy.sueldazo.ui.commerce.viewModel.CommerceViewModel
import com.appbenefy.sueldazo.ui.coupon.viewModel.CouponViewModel
import com.appbenefy.sueldazo.ui.home.viewModel.HomeViewModel
import com.appbenefy.sueldazo.ui.notifications.viewModel.NotificationViewModel
import com.appbenefy.sueldazo.ui.profile.viewModel.ProfileViewModel
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
    @ViewModelKey(ProfileViewModel::class)
    abstract fun bindsProfileViewModel(profileViewModel: ProfileViewModel): ViewModel

}