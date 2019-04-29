package com.seanshubin.factorio.deploy.ssh

data class ExecResult(val exitCode: Int, val outputLines: List<String>, val errorLines: List<String>)