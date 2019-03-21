package com.seanshubin.factorio.deploy.ssh

import com.jcabi.ssh.Ssh
import com.seanshubin.factorio.deploy.GlobalConstants
import com.seanshubin.factorio.deploy.capture.CaptureOutputLines
import com.seanshubin.factorio.deploy.capture.NoInput
import java.io.InputStream
import java.io.OutputStream
import java.lang.RuntimeException

class SshConnectionImpl(private val ssh:Ssh, private val lineEvent:(String)->Unit):
    SshConnection {
    override fun exec(command: String):Int =
        exec(command, NoInput, CaptureOutputLines(lineEvent), CaptureOutputLines(lineEvent))

    override fun createFile(path: String, content: String):Int =
        exec(
            "cat > $path",
            content.byteInputStream(GlobalConstants.charset),
            CaptureOutputLines(lineEvent),
            CaptureOutputLines(lineEvent))

    private fun exec(command:String, stdin: InputStream, stdout: OutputStream, stderr:OutputStream ):Int{
        lineEvent(command)
        val exitCode = ssh.exec(command, stdin, stdout, stderr)
        if(exitCode != 0) {
            throw RuntimeException("Failed with exit code $exitCode: $command")
        }
        return exitCode
    }
}
