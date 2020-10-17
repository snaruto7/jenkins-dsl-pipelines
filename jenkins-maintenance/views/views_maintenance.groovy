nestedView('Jenkins Maintenance'){
    views {
        listView('Jenkins Maintenance Overview') {
            jobs {
                regex(/ShivamLabs_Maintenance_*.*/)
            }
            columns {
                status()
                weather()
                name()
                lastSuccess()
                lastFailure()
            }
        }// end list view
        listView('Jenkins Maintenance Builds') {
            jobs {
                regex(/ShivamLabs_Maintenance_*.*_Build/)
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
        view / defaultView('Jenkins Maintenance Builds')
    }
}