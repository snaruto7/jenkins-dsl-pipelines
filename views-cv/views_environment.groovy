categorizedJobsView('Environment Overview') {
    jobs {
        regex(/.*/)
    }
    categorizationCriteria {
        regexGroupRule(/dsl_*.*/, "Jenkins Job DSL")
        regexGroupRule(/ShivamLabs_App_*.*/, "Application Jobs")
        regexGroupRule(/ShivamLabs_Terraform_*.*/, "Terraform Jobs")
    }
    columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
    }
}