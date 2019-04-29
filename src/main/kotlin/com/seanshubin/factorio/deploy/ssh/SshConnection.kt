package com.seanshubin.factorio.deploy.ssh

interface SshConnection {
    fun exec(command: String)
    fun execExitCode(command: String): Int
    fun execResult(command: String): ExecResult
    fun createFile(path:String, content:String):Int
}
