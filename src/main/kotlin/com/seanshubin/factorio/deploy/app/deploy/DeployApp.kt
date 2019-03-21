package com.seanshubin.factorio.deploy.app.deploy

import com.seanshubin.factorio.deploy.app.Dependencies

fun main(args:Array<String>) {
    Dependencies(args).deployer.deploy()
}
