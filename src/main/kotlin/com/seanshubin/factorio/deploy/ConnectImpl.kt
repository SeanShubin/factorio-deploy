package com.seanshubin.factorio.deploy

import com.seanshubin.factorio.deploy.ec2.Ec2Api
import com.seanshubin.factorio.deploy.ssh.SshFactory
import java.nio.file.Path
import java.time.Duration

class ConnectImpl(
    private val ec2Api: Ec2Api,
    private val factorioApi: FactorioApi,
    private val sshFactory: SshFactory,
    private val keyPath: Path,
    private val emit: (String) -> Unit,
    private val factorioHeadlessUrl: String,
    private val factorioInstanceName: String
) : Connect {
    override fun connect() {
        val ec2Instance = ec2Api.findEc2Instance(factorioInstanceName)
        val factorio = factorioApi.loadFactorio()
        sshFactory.withSshConnection(ec2Instance.host) { sshConnection ->
            RetryUtil.wait(
                attemptLimit = 20,
                timeBetweenAttempts = Duration.ofSeconds(5),
                totalTimeLimit = Duration.ofMinutes(2)
            ) {
                sshConnection.exec("ls -1") == 0
            }
        }
        val host = ec2Instance.host
        emit("""ssh -i "$keyPath" ec2-user@$host""")
    }
}
