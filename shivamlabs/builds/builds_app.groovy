def mapApps = [
    1: [teamId: "ShivamLabs", appId: "tic_tac_toe", scriptName: "app_tic_tac_toe_build", github: "https://github.com/snaruto7/tic-tac-toe.git", primaryBranch: "master", primaryFolder: "", primaryFolderExec: ""],
    2: [teamId: "ShivamLabs", appId: "minesweeper", scriptName: "app_minesweeper_build", github: "https://github.com/snaruto7/minesweeper.git", primaryBranch: "master", primaryFolder: "", primaryFolderExec: ""],
    3: [teamId: "ShivamLabs", appId: "jumping_monster", scriptName: "app_jumping_monster_build", github: "https://github.com/snaruto7/jumping-monster.git", primaryBranch: "master", primaryFolder: "", primaryFolderExec: ""],
]

mapApps.collect { map -> 

    pipelineJob("${map.value.teamId}_App_${map.value.appId}_Build") {
        logRotator {
            numToKeep(15)
        }
        properties {
            githubProjectUrl("${map.value.github}")
        }
        definition {
            cps {
                script(readFileFromWorkspace("./${map.value.teamId.toLowerCase()}/builds/scripts/${map.value.scriptName.toLowerCase()}.groovy"))
                sandbox()
            }
        }
    }

    job("${map.value.teamId}_App_${map.value.appId}_Build_Webhook") {
        logRotator {
            numToKeep(15)
        }
        properties {
            githubProjectUrl("${map.value.github}")
        }
        scm {
            git {
                remote {
                    credentials('github-creds')
                    url("${map.value.github}")
                }
                branches("${map.value.primaryBranch}")
                extensions {
                    pathRestriction {
                        excludedRegions("${map.value.primaryFolderExec}")
                        includedRegions("${map.value.primaryFolder}")
                    }
                }
            }
        }
        triggers {
            githubPush()
        }
        wrappers {
            label('master')
            preBuildCleanup()
        }
        publishers {
            downstream("${map.value.teamId}_App_${map.value.appId}_Build", 'UNSTABLE')
        }
    } // end webhook job
}