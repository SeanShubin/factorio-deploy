package com.seanshubin.factorio.deploy

import com.seanshubin.factorio.deploy.ec2.Ec2Api
import com.seanshubin.factorio.deploy.ssh.SshFactory
import java.time.Duration

class RestoreImpl(
    private val ec2Api: Ec2Api,
    private val sshFactory: SshFactory,
    private val factorioInstanceName: String
) : Restore {
    override fun restore() {
        val ec2Instance = ec2Api.findEc2Instance(factorioInstanceName)
        sshFactory.withSshConnection(ec2Instance.host) { sshConnection ->
            RetryUtil.wait(
                attemptLimit = 20,
                timeBetweenAttempts = Duration.ofSeconds(5),
                totalTimeLimit = Duration.ofMinutes(2)
            ) {
                sshConnection.execExitCode("ls -1") == 0
            }
            val result = sshConnection.execResult("ls -lt factorio/saves")
            val saveGame = result.outputLines.map(::toSaveGameName).filterNotNull()[0]
            sshConnection.exec("date")
            sshConnection.exec("echo cp factorio/saves/$saveGame factorio/seed-12335.zip")
        }
    }

    private fun toSaveGameName(s: String): String? {
        val matchResult = SaveGameRegex.matchEntire(s)
        return if (matchResult == null) {
            null
        } else {
            matchResult.groupValues[1]
        }
    }

    companion object {
        val SaveGameRegex = Regex("""-rw-r--r-- 1 ec2-user ec2-user \d+ \w+ \d+ \d+:\d+ (\w+\.zip)""")
    }
}
