package com.seanshubin.factorio.deploy.ssh

interface SshFactory{
    fun <T> withSshConnection(host:String,f:(SshConnection)->T):T
}
