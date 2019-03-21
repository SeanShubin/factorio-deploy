package com.seanshubin.factorio.deploy

import com.amazonaws.regions.Regions
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

object GlobalConstants{
    val charset: Charset = StandardCharsets.UTF_8
    val region = Regions.US_WEST_1
    val amazonMachineImage = "ami-0019ef04ac50be30f"
    val headlessFactorioUrl:String = "https://factorio.com/get-download/latest/headless/linux64"
    val keyName:String = "factorio"
    val instanceName:String = "factorio-automated-deploy"
    val factorioSecurityGroup = "factorio"
    val sshSecurityGroup = "ssh"

    fun factorioSupervisorConfig(seed:Long) = """
        [program:factorio]
        directory=/home/ec2-user/factorio/
        command=/home/ec2-user/factorio/bin/x64/factorio --use-server-whitelist --server-whitelist users.json --server-adminlist users.json --start-server seed-$seed
        stdout_logfile=/home/ec2-user/factorio/supervisor-log/out.log
        stderr_logfile=/home/ec2-user/factorio/supervisor-log/err.log
        autostart=true
    """.trimIndent()

    val supervisorConfig = """
        [unix_http_server]
        file=/tmp/supervisor.sock   ; the path to the socket file

        [supervisord]
        logfile=/tmp/supervisord.log ; main log file; default ${'$'}CWD/supervisord.log
        logfile_maxbytes=50MB        ; max main logfile bytes b4 rotation; default 50MB
        logfile_backups=10           ; # of main logfile backups; 0 means none, default 10
        loglevel=info                ; log level; default info; others: debug,warn,trace
        pidfile=/tmp/supervisord.pid ; supervisord pidfile; default supervisord.pid
        nodaemon=false               ; start in foreground if true; default false
        minfds=1024                  ; min. avail startup file descriptors; default 1024
        minprocs=200                 ; min. avail process descriptors;default 200

        [rpcinterface:supervisor]
        supervisor.rpcinterface_factory = supervisor.rpcinterface:make_main_rpcinterface

        [supervisorctl]
        serverurl=unix:///tmp/supervisor.sock ; use a unix:// URL  for a unix socket
    """.trimIndent()

    val usersJson = """
        [
          "evilmushroom",
          "isamu78",
          "subwlf"
        ]
    """.trimIndent()
}
