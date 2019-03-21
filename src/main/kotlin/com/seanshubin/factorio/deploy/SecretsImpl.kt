package com.seanshubin.factorio.deploy

class SecretsImpl(
    override val awsAccessKeyId: String,
    override val awsSecretKey: String,
    override val keyPath: String
) : Secrets
