package kr.co.bdgen.indywrapper.data.argument

import org.json.JSONString

data class CredentialInfo(
    val credDefJson: String,
    val credReqMetadataJson: String,
    val credReqJson: String
)