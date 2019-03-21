package com.seanshubin.factorio.deploy.ec2

interface Ec2Api {
    fun provisionEc2Instance(name:String): Ec2Instance
    fun findEc2Instance(name:String): Ec2Instance
}