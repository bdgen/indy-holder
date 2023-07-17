package kr.co.bdgen.indywrapper.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CredentialFilter(
    val schema_id: String,
    val schema_name: String? = null,
    val schema_version: String? = null,
    val cred_def_id: String? = null
) : Parcelable
