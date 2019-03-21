package com.seanshubin.factorio.deploy

import com.amazonaws.auth.AWSCredentials

class Credentials(private val awsAccessKeyId:String, private val awsSecretKey:String):AWSCredentials{
    override fun getAWSAccessKeyId(): String = awsAccessKeyId
    override fun getAWSSecretKey(): String = awsSecretKey
}
