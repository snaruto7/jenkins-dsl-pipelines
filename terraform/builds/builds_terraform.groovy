def mapApps = [
    1: [teamId: "ShivamLabs", appId: "aks", scriptName: "terraform_aks_build", github: "https://github.com/snaruto7/terraform-aks-setup.git", primaryBranch: "master", primaryFolder: "", primaryFolderExec: ""],
]

mapApps.collect { map ->

    pipelineJob("${map.value.teamId}_Terraform_${map.value.appId}_Build") {
        logRotator {
            numToKeep(15)
        }
        properties {
            githubProjectUrl("${map.value.github}")
        }
        parameters {
            choiceParam( 'subscription_id', ['shivam-subs'],  'Select the subscription')
            stringParam( 'rg_name',  '',  'Enter resource group name')
            stringParam( 'rg_location',  '',  'Enter location')
            stringParam( 'cluster_name',  '',  'Enter unique name for cluster')
            stringParam( 'dns_prefix',  '',  'Enter unique name for dns prefix')
            stringParam( 'node_count',  '3',  'Enter node count for AKS cluster')
            choiceParam( 'vm_size', [ 'Standard_DS1_v2','Standard_D2_v2', 'Standard_D2S_v2', 'Standard_B2s', 'Standard_B2ms'],  'Enter size for AKS nodes')
            stringParam( 'environment',  '',  'Add the type of environment')
            choiceParam( 'Create', ['None', 'True', 'False'],  'Tf infa to be created or destroyed or none')
        }
        definition {
            cps {
                script(readFileFromWorkspace("./terraform/builds/scripts/${map.value.scriptName.toLowerCase()}.groovy"))
                sandbox()
            }
        }
    }

}