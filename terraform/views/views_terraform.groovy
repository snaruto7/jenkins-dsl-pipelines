nestedView('Terraform'){
    views {
        listView('Terraform Overview') {
            jobs {
                regex(/ShivamLabs_Terraform_*.*/)
            }
            columns {
                status()
                weather()
                name()
                lastSuccess()
                lastFailure()
            }
        }// end list view
        listView('Terraform Builds') {
            jobs {
                regex(/ShivamLabs_Terraform_*.*_Build/)
            }
            columns {
                status()
                weather()
                name()
                lastSuccess()
                lastFailure()
            }
        }//end list view
        listView('Terraform Deploys') {
            jobs {
                regex(/ShivamLabs_Terraform_*.*_Deploy/)
            }
            columns {
                status()
                weather()
                name()
                lastSuccess()
                lastFailure()
            }
        }//end list view
    }
    configure { view ->
        view / defaultView('Terraform Builds')
    }
}