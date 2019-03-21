package com.seanshubin.factorio.deploy.ssh

interface SshCommand {
    fun exec(connection: SshConnection)
    data class Exec(val command:String): SshCommand {
        override fun exec(connection: SshConnection) {
            connection.exec(command)
        }
    }
    data class CreateFile(val filePath:String, val fileContents:String): SshCommand {
        override fun exec(connection: SshConnection) {
            connection.createFile(filePath, fileContents)
        }
    }
}
