package com.seanshubin.factorio.deploy.ec2

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model.*
import com.seanshubin.factorio.deploy.GlobalConstants
import com.seanshubin.factorio.deploy.RetryUtil
import com.seanshubin.factorio.deploy.collection.CollectionUtil.exactlyOne
import java.time.Duration

class Ec2ApiImpl(private val amazonEc2: AmazonEC2,
                 private val emit: (String) -> Unit) :
    Ec2Api {
    override fun provisionEc2Instance(name:String): Ec2Instance {
        val request = RunInstancesRequest()
        request.withImageId(GlobalConstants.amazonMachineImage)
        request.withMinCount(1)
        request.withMaxCount(1)
        request.withKeyName(GlobalConstants.keyName)
        request.withInstanceType("t2.micro")
        val nameTag = Tag("Name", name)
        val tagSpecification = TagSpecification().withTags(nameTag).withResourceType("instance")
        request.withTagSpecifications(tagSpecification)
        request.withSecurityGroups(
            GlobalConstants.factorioSecurityGroup,
            GlobalConstants.sshSecurityGroup
        )
        val result: RunInstancesResult = amazonEc2.runInstances(request)
        val instanceId = result.reservation.instances.exactlyOne().instanceId
        RetryUtil.wait(
            attemptLimit = 20,
            timeBetweenAttempts = Duration.ofSeconds(5),
            totalTimeLimit = Duration.ofMinutes(2)
        ) {
            val state = getState(instanceId)
            emit("state = $state")
            state == "running"
        }
        waitUntilReady(instanceId)
        return createEc2Instance(instanceId)
    }

    override fun findEc2Instance(name: String): Ec2Instance {
        val result = amazonEc2.describeInstances()
        val list = mutableListOf<String>()
        for (reservation in result.reservations) {
            for (instance in reservation.instances) {
                if (instance.state.name == "running" && getTag(instance, "Name") == name) {
                    val id = instance.instanceId
                    list.add(id)
                }
            }
        }
        val instanceId = list.exactlyOne()
        return createEc2Instance(instanceId)
    }

    private fun getTag(instance:Instance, key:String) =
        instance.tags.filter{it.key == key}.exactlyOne().value

    private fun createEc2Instance(instanceId: String): Ec2Instance {
        val host = getHost(instanceId)
        emit("host = $host")
        return Ec2Instance(instanceId, host)
    }

    private fun getHost(instanceId: String): String {
        val request = DescribeInstancesRequest()
        request.withInstanceIds(instanceId)
        val result = amazonEc2.describeInstances(request)
        val host = result.reservations.exactlyOne().instances.exactlyOne().publicDnsName
        return host
    }

    private fun getState(instanceId: String): String {
        val request = DescribeInstancesRequest()
        request.withInstanceIds(instanceId)
        val result = amazonEc2.describeInstances(request)
        val state = result.reservations.exactlyOne().instances.exactlyOne().state.name
        return state
    }

    private tailrec fun waitUntilReady(instanceId: String) {
        val state = getState(instanceId)
        emit("state = $state")
        if (state != "running") {
            Thread.sleep(5000)
            waitUntilReady(instanceId)
        }
    }
}
