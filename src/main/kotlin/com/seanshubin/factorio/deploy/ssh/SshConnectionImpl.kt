package com.seanshubin.factorio.deploy.ssh

import com.jcabi.ssh.Ssh
import com.seanshubin.factorio.deploy.GlobalConstants
import com.seanshubin.factorio.deploy.capture.NoInput
import com.seanshubin.factorio.deploy.capture.OutputStreamAsLineEvents
import com.seanshubin.factorio.deploy.capture.OutputStreamStoredAsLines
import java.io.InputStream
import java.io.OutputStream

class SshConnectionImpl(private val ssh: Ssh, private val lineEvent: (String) -> Unit) : SshConnection {
    override fun exec(command: String) {
        execResult(command)
    }

    override fun execExitCode(command: String): Int = execResult(command).exitCode
    override fun execResult(command: String): ExecResult {
        val standardOutput = OutputStreamStoredAsLines(lineEvent)
        val standardError = OutputStreamStoredAsLines(lineEvent)
        val exitCode = exec(command, NoInput, standardOutput, standardError)
        standardOutput.flush()
        standardError.flush()
        val outputLines = standardOutput.lines
        val errorLines = standardError.lines
        return ExecResult(exitCode, outputLines, errorLines)
    }

    override fun createFile(path: String, content: String):Int =
        exec(
            "cat > $path",
            content.byteInputStream(GlobalConstants.charset),
            OutputStreamAsLineEvents(lineEvent),
            OutputStreamAsLineEvents(lineEvent)
        )

    private fun exec(command:String, stdin: InputStream, stdout: OutputStream, stderr:OutputStream ):Int{
        lineEvent(command)
        val exitCode = ssh.exec(command, stdin, stdout, stderr)
        if(exitCode != 0) {
            throw RuntimeException("Failed with exit code $exitCode: $command")
        }
        return exitCode
    }
}
