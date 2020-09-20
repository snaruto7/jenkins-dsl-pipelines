def mapJenkinsJobs = [
    1: [teamID: "ShivamLabs", buildName: "aks", artifactFolder: "shivamlabs-terraform", scriptName: "terraform_aks_deploy", artifactName: "aks" ],
]

mapJenkinsJobs.collect { map ->

    pipelineJob("${map.value.teamID}_Terraform_${map.value.buildName}_Deploy") {
        logRotator {
            numToKeep(15)
        }

        configure {
            project->
                project / 'properties' << 'hudson.model.ParametersDefinitionProperty' {
                    parameterDefinitions {
                        'com.cwctravel.hudson.plugins.extended__choice__parameter.ExtendedChoiceParameterDefinition' {
                            name 'version'
                            quoteValue 'false'
                            saveJSONParameterToFile 'false'
                            visibleItemCount '15'
                            type 'PT_SINGLE_SELECT'
                            groovyScript(readFileFromWorkspace('./scripts/artifactory_pull_generic.groovy'))
                            bindings("artifactName=${map.value.artifactName}\nrepository=${map.value.artifactFolder}")
                            multiSelectDelimiter ','
                            projectName "${jobName}"
                        }
                    }
                }

        }
        environmentVariables {
            env('VAULT_ROLE_ID', "VAULT_ROLE_ID")
            env('VAULT_SEC_ID', "VAULT_SEC_ID")
            env('registry', "shivamlabs.jfrog.io")
            env('imageFolder', "${map.value.artifactFolder}")
        }
        definition {
            cps {
                script(readFileFromWorkspace("./terraform/deploys/scripts/${map.value.scriptName}.groovy"))
                sandbox()
            }
        }
    }
}