package com.seanshubin.factorio.deploy.app

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder
import com.seanshubin.factorio.deploy.*
import com.seanshubin.factorio.deploy.contract.FilesContract
import com.seanshubin.factorio.deploy.contract.FilesDelegate
import com.seanshubin.factorio.deploy.ec2.Ec2Api
import com.seanshubin.factorio.deploy.ec2.Ec2ApiImpl
import com.seanshubin.factorio.deploy.http.Http
import com.seanshubin.factorio.deploy.http.HttpExec
import com.seanshubin.factorio.deploy.logger.LineEmittingLogger
import com.seanshubin.factorio.deploy.logger.Logger
import com.seanshubin.factorio.deploy.ssh.SshFactory
import com.seanshubin.factorio.deploy.ssh.SshFactoryImpl
import com.seanshubin.factorio.deploy.table.RowStyleTableFormatter
import com.seanshubin.factorio.deploy.table.TableFormatter
import com.seanshubin.factorio.deploy.timer.Timer
import java.net.http.HttpClient
import java.nio.file.Paths
import java.time.Clock

class Dependencies(args:Array<String>) {
    private val secrets = SecretsImpl(
        awsAccessKeyId = args[0],
        awsSecretKey = args[1],
        keyPath = args[2]
    )
    private val credentialsProvider =
        CredentialsProvider(secrets.awsAccessKeyId, secrets.awsSecretKey)
    private val client: AmazonEC2 = AmazonEC2ClientBuilder.standard().
        withRegion(GlobalConstants.region).
        withCredentials(credentialsProvider).build()
    private val emit:(String)->Unit=::println
    private val ec2Api: Ec2Api =
        Ec2ApiImpl(client, emit)
    private val userName:String = "ec2-user"
    private val keyPath = Paths.get(secrets.keyPath)
    private val files:FilesContract = FilesDelegate
    private val sshFactory: SshFactory =
        SshFactoryImpl(userName, keyPath, emit, files)
    private val httpClient = HttpClient.newHttpClient()
    private val clock = Clock.systemUTC()
    private val timer: Timer  = Timer(clock)
    private val tableFormatter: TableFormatter = RowStyleTableFormatter.boxDrawing
    private val logger: Logger = LineEmittingLogger(emit, tableFormatter)
    private val http: Http = HttpExec(httpClient, timer, logger::requestEvent, logger::responseEvent)
    private val factorioApi: FactorioApi = FactorioApiImpl(http, GlobalConstants.headlessFactorioUrl)
    private val seed = 12345L
    val deployer: Deployer = DeployerImpl(
        ec2Api,
        factorioApi,
        sshFactory,
        seed,
        keyPath,
        emit,
        GlobalConstants.headlessFactorioUrl,
        GlobalConstants.instanceName)
    val updater: Updater = UpdaterImpl(
        ec2Api,
        factorioApi,
        sshFactory,
        keyPath,
        emit,
        GlobalConstants.headlessFactorioUrl,
        GlobalConstants.instanceName)
}
