package com.seanshubin.factorio.deploy.ssh

import com.jcabi.ssh.Ssh
import com.seanshubin.factorio.deploy.GlobalConstants
import com.seanshubin.factorio.deploy.contract.FilesContract
import java.nio.file.Path

class SshFactoryImpl(
    private val userName: String,
    private val privateKeyPath: Path,
    private val lineEvent: (String) -> Unit,
    private val files: FilesContract
) : SshFactory {
    private val port = 22
    override fun <T> withSshConnection(host: String, f: (SshConnection) -> T): T {
        val privateKey = files.readString(privateKeyPath, GlobalConstants.charset)
        val jcabiSsh = Ssh(host, port, userName, privateKey)
        val sshConnection = SshConnectionImpl(jcabiSsh, lineEvent)
        val result = f(sshConnection)
        return result
    }
}
