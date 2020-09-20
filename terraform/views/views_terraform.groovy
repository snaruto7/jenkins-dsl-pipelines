nestedView('Terraform'){
    views {
        listView('Terraform Overview') {
            jobs {
                regex(/Terraform_*.*/)
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
                regex(/Terraform_*.*_Build/)
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
                regex(/Terraform_*.*_Deploy/)
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