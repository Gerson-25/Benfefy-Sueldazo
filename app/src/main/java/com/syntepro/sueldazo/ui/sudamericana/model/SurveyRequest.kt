package com.syntepro.sueldazo.ui.sudamericana.model

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class SurveyRequest(
        val insuredFirstName: String,
        val insuredMiddleName: String,
        val insuredLastName: String,
        val insuredOtherLastName: String,
        val insuredIdentityDocument: String,
        val documentExtension: String,
        val insuredBirthDate: String,
        val subsidiaryCode: Int,
        val insuredExpeditionDateIdentityDocument: String,
        val phoneNumber: String,
        val beneficiaryFullName: String,
        val beneficiaryDocumentId: String,
        val beneficiaryRelationship: String,
        val beneficiaryPercentage: Int,
        val question1: Boolean,
        val descriptionQuestion1: String?,
        val question2: Boolean?,
        val descriptionQuestion2: String?,
        val question3: Boolean?,
        val descriptionQuestion3: String?,
        val weight: Int?,
        val height: Int?,
        val question5: Boolean?,
        val descriptionQuestion5: String?,
        val question6: Boolean?,
        val doctorName: String?,
        val doctorAddress: String?,
        val petType: String?,
        val petBreed: String?,
        val petYears: Int?,
        val petMonths: Int?,
        val petName: String?,
        val userCountry: String?,
        val userCountryResidence: String?,
        val homeAddress: String?
): Serializable