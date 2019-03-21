package com.seanshubin.factorio.deploy

import com.seanshubin.factorio.deploy.ec2.Ec2Api
import com.seanshubin.factorio.deploy.ssh.SshCommand
import com.seanshubin.factorio.deploy.ssh.SshFactory
import java.nio.file.Path
import java.time.Duration

class DeployerImpl(
    private val ec2Api: Ec2Api,
    private val factorioApi: FactorioApi,
    private val sshFactory: SshFactory,
    private val seed:Long,
    private val keyPath: Path,
    private val emit:(String)->Unit,
    private val factorioHeadlessUrl:String,
    private val instanceName:String
) : Deployer {
    override fun deploy() {
        val ec2Instance = ec2Api.provisionEc2Instance(instanceName)
        val factorio = factorioApi.loadFactorio()
        sshFactory.withSshConnection(ec2Instance.host) { sshConnection ->
            RetryUtil.wait(
                attemptLimit = 20,
                timeBetweenAttempts = Duration.ofSeconds(5),
                totalTimeLimit = Duration.ofMinutes(2)
            ) {
                sshConnection.exec("ls -1") == 0
            }
            val sshCommands = buildSshCommands(factorio)
            sshCommands.forEach { it.exec(sshConnection) }
        }
        val host = ec2Instance.host
        emit("""ssh -i "$keyPath" ec2-user@$host""")
    }

    private fun buildSshCommands(factorio: FactorioInfo): List<SshCommand> {
        val fileName = factorio.fileName
        val supervisorConfContent =
            GlobalConstants.supervisorConfig + "\n\n" + GlobalConstants.factorioSupervisorConfig(seed)
        return listOf(
            SshCommand.Exec("wget -O $fileName $factorioHeadlessUrl"),
            SshCommand.Exec("tar xf $fileName"),
            SshCommand.Exec("factorio/bin/x64/factorio --map-gen-seed $seed --create factorio/seed-$seed"),
            SshCommand.Exec("mkdir factorio/supervisor-log"),
            SshCommand.CreateFile("factorio/users.json", GlobalConstants.usersJson),
            SshCommand.Exec("sudo easy_install supervisor"),
            SshCommand.CreateFile("supervisord.conf", supervisorConfContent),
            SshCommand.Exec("sudo mv supervisord.conf /etc/supervisord.conf"),
            SshCommand.Exec("supervisord"),
            SshCommand.Exec("supervisorctl status")
        )
    }
}
