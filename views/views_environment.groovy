categorizedJobsView('Environment Overview') {
    jobs {
        regex(/.*/)
    }
    categorizationCriteria {
        regexGroupingRule(/dsl_*.*/, "Jenkins Job DSL")
        regexGroupingRule(/ShivamLabs_App_*.*/, "Application Jobs")
        regexGroupingRule(/ShivamLabs_Terraform_*.*/, "Terraform Jobs")
        regexGroupingRule(/ShivamLabs_Maintenance_*.*/, "Jenkins Maintenance Jobs")
    }
    columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
    }
}