package com.seanshubin.factorio.deploy

interface Secrets{
    val awsAccessKeyId:String
    val awsSecretKey:String
    val keyPath:String
}
