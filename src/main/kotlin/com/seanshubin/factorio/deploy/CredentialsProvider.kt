package com.seanshubin.factorio.deploy

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider

class CredentialsProvider(private val awsAccessKeyId:String, private val awsSecretKey:String): AWSCredentialsProvider{
    override fun getCredentials(): AWSCredentials = Credentials(awsAccessKeyId, awsSecretKey)

    override fun refresh() {
        // intentionally doing nothing
    }
}