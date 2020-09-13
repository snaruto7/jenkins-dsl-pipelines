def mapJenkinsJobs = [
    1: [teamID: "ShivamLabs", buildName: "tic_tac_toe", scriptName: "app_tic_tac_toe_deploy", artifactName: "tic-tac-toe" ],
    2: [teamID: "ShivamLabs", buildName: "minesweeper", scriptName: "app_minesweeper_deploy", artifactName: "minesweeper" ],
    3: [teamID: "ShivamLabs", buildName: "jumping_monster", scriptName: "app_jumping_monster_deploy", artifactName: "jumping-monster" ],
]

mapJenkinsJobs.collect { map ->

    pipelineJob("${map.value.teamID}_App_${map.value.buildName}_Deploy") {
        logRotator {
            numToKeep(15)
        }
        configure {
            project->
                project / 'properties' << 'hudson.model.ParametersDefintionProperty' {
                    parameterDefinitions {
                        'com.cwctravel.hudson.plugins.extended__choice__parameter.ExtendedChoiceParameterDefintion' {
                            name 'version'
                            qouteValue 'false'
                            saveJSONParameterToFile 'false'
                            visibleItemCount '15'
                            type 'PI_SINGLE_SELECT'
                            bindings("docker_image=${map.value.artifactName}")
                            multiSelectDelimeter ','
                            projectName "${$jobName}"
                        }
                    }
                }
        }
        environmentVariables {
            env('VAULT_ROLE_ID', "VAULT_ROLE_ID")
            env('VAULT_SEC_ID', "VAULT_SEC_ID")
            env('registry', 'kubepractice.azurecr.io')
        }
        definition {
            cps {
                script(readFileFromWorkspace("./shivamlabs/deploys/scripts/${map.value.scriptName}.groovy"))
                sandbox()
            }
        }
    }
}