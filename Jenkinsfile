//=====Jenkins需要添加的几个参数========
// branch          配置部署的分支
// project_name    配置部署的多个项目
// publish_server  配置多个部署的服务器地址


//=====需要修改的地方========
//gitlab的凭证
def git_auth = "99266a00-e11b-41d0-ab42-f3d16177200e"
//Harbor私服地址
def harbor_url = "192.168.249.129:85"
//Harbor的项目名称
def harbor_project_name = "mall"
//Harbor的凭证
def harbor_auth = "39da1c19-57a8-4872-ba18-799edcb2b421"
//基础的公共包
def base_common="mall-common"
//构建版本的名称
def tag = "latest"

//harbor账户密码
def harbor_username="admin"
def harbor_password="Harbor12345"


//激活生产环境
def activeProfile="prod"


node {
        //把选择的项目信息转为数组 mall-eureka@6001,mall-user@7002,mall-auth@7001,mall-stock@7003
        def selectedProjectNames = "${project_name}".split(',')
        //获取当前选择的服务器名称  master_server,slave_server1,slave_server2
        def selectedServers = "${publish_server}".split(",")

        stage("拉取代码") {
           checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'dc6800ac-bdca-4422-8849-6c503de23d95', url: 'git@gitee.com:xiejs/mall.git']]])
        }


        stage("编译----->构建镜像----->部署服务") {

             echo '编译并安装公共工程'
             sh "mvn -f ${base_common} clean install -Dmaven.test.skip=true"

             for(int i=0;i<selectedProjectNames.length;i++){
                 //mall-eureka@6001
                 def projectInfo = selectedProjectNames[i];
                 //取出每个项目的名称和端口
                 def currentProjectName = "${projectInfo}".split("@")[0]
                 def currentProjectPort = "${projectInfo}".split("@")[1]


                echo '编译，构建本地镜像'
                sh "mvn -f ${currentProjectName} clean package dockerfile:build"

                //定义镜像名称 mall-eureka:latest
                echo '定义镜像名称'
                def imageName = "${currentProjectName}:${tag}"


                echo '对镜像打上标签'
                sh "docker tag ${imageName} ${harbor_url}/${harbor_project_name}/${imageName}"

                echo '登录Harbor后并上传镜像'
                withCredentials([usernamePassword(credentialsId: "${harbor_auth}",
                passwordVariable: 'password', usernameVariable: 'username')]) {
                    //登录
                    sh "docker login -u ${username} -p ${password} ${harbor_url}"
                    //上传镜像
                    sh "docker push ${harbor_url}/${harbor_project_name}/${imageName}"
                    sh "echo 镜像上传成功"
                }

                 echo '删除本地镜像'
                 sh "docker rmi -f ${imageName}"
                 sh "docker rmi -f ${harbor_url}/${harbor_project_name}/${imageName}"

                echo '遍历所有服务器，分别部署'
                for(int j=0;j<selectedServers.length;j++){
                    //获取当前遍历的服务器名称 master_server
                    def currentServerName = selectedServers[j]

                    echo "远程调用进行项目${currentProjectName}部署路径${currentServerName}"

                    sshPublisher(publishers: [sshPublisherDesc(configName: "${currentServerName}",
                    transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand:
                    "/opt/jenkins_shell/deployCluster.sh $harbor_url $harbor_project_name $currentProjectName $tag $currentProjectPort $harbor_username $harbor_password", execTimeout: 120000, flatten: false, makeEmptyDirs: false,
                    noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '',
                    remoteDirectorySDF: false, removePrefix: '', sourceFiles: '')],
                    usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
                }

                echo "${currentProjectName}完成编译，构建镜像"
             }
        }
}
