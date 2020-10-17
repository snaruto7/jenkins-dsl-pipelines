def mapApps = [
    1: [teamId: "ShivamLabs", appId: "Slave_Agents", scriptName: "jenkins_slave_agents_build", github: "https://github.com/snaruto7/jenkins-slave-agents.git", primaryBranch: "master", primaryFolder: "", primaryFolderExec: ""],
]

mapApps.collect { map ->

    pipelineJob("${map.value.teamId}_Maintenance_${map.value.appId}_Build") {
        logRotator {
            numToKeep(15)
        }
        properties {
            githubProjectUrl("${map.value.github}")
        }
        environmentVariables {
            env('registry', "shivamlabs.jfrog.io")
        }
        parameters {
            choiceParam( 'image_name',  ['jenkins-slave'],  'Enter the image to be created')
            stringParam( 'tag',  '',  'Enter version of the image')
        }
        definition {
            cps {
                script(readFileFromWorkspace("./jenkins-maintenance/builds/scripts/${map.value.scriptName.toLowerCase()}.groovy"))
                sandbox()
            }
        }
    }

}