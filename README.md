# Factorio Server

## Todo List
- add timer
- ssh logging
- remove Thread.sleep
- RetryUtil logging

## Log into AWS Management Console
- set region to
    - US West (N. California)

## Create security groups
- EC2
- Security Groups
    - Create Security Group for ssh access
    - Create security group for factorio server access
        - Custom UDP Rule
        - UDP
        - 34197
        - 0.0.0.0/0

## Set private key permissions 
- On windows
    - right click
    - properties
    - security
    - advanced
    - add
    - select a principal
        - Sean
    - disable inheritance
    - remove all inherited permissions from this object
    - Users
        - remove
        
## To launch an ec2 instance
- EC2
- Instances
- Launch Instance
- Step 1: Choose AMI
    - Amazon Linux 2 AMI (HVM), SSD Volume Type - ami-0019ef04ac50be30f
- Step 2: Choose an Instance Type
    - t2.micro
- Step 3: Configure Instance Details
- Step 4: Add Storage
- Step 5: Add Tags
    - key
        - Name
    - value
        - factorio

## SSH into the instance
```
ssh -i "factorio.pem" ec2-user@ec2-54-193-122-154.us-west-1.compute.amazonaws.com
wget -O factorio_headless_x64.tar.xz https://factorio.com/get-download/latest/headless/linux64
tar xf factorio_headless_x64.tar.xz
cd factorio
bin/x64/factorio --create foo
bin/x64/factorio --start-server foo
```
