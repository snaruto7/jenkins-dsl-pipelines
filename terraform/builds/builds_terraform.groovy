def mapApps = [
    1. [teamId: "ShivamLabs", appId: "aks", scriptName: "terraform_aks_build", github: "https://github.com/snaruto7/terraform-aks-setup.git", primaryBranch: "master", primaryFolder: "", primaryFolderExec: ""],
]

mapApps.collect { map ->

    pipelineJob("${map.value.teamId}_Terraform_${map.value.appId}_Build") {
        logRotator {
            numToKeep(15)
        }
        properties {
            githubProjecUrl("${map.value.github}")
        }
        parameters {
            choiceParam( name: 'subscription_id', choices: ['shivam-subs'], description: 'Select the subscription')
            stringParam( name: 'rg_name', defaultValue: '', description: 'Enter resource group name')
            stringParam( name: 'rg_location', defaultValue: '', description: 'Enter location')
            stringParam( name: 'cluster_name', defaultValue: '', description: 'Enter unique name for cluster')
            stringParam( name: 'dns_prefix', defaultValue: '', description: 'Enter unique name for dns prefix')
            stringParam( name: 'node_count', defaultValue: '3', description: 'Enter node count for AKS cluster')
            choiceParam( name: 'vm_size', choices: [ 'Standard_DS1_v2','Standard_D2_v2', 'Standard_D2S_v2', 'Standard_B2s', 'Standard_B2ms'], description: 'Enter size for AKS nodes')
            stringParam( name: 'environment', defaultValue: '', description: 'Add the type of environment')
            choiceParam( name: 'Create', choices: ['None'], description: 'Tf infa to be created or destroyed or none')
        }
        definition {
            cps {
                script(readFileFromWorkspace("./terraform/builds/scripts/${map.value.scriptName.toLowerCase()}.groovy"))
                sandbox()
            }
        }
    }

}