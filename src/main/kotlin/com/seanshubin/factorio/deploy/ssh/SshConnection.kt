package com.seanshubin.factorio.deploy.ssh

interface SshConnection {
    fun exec(command:String):Int
    fun createFile(path:String, content:String):Int
}
