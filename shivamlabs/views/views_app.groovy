nestedView('Applications'){
    views {
        listView('Applications Overview') {
            jobs {
                regex(/ShivamLabs_App_*.*/)
            }
            columns {
                status()
                weather()
                name()
                lastSuccess()
                lastFailure()
            }
        }// end list view
        listView('Applications Builds') {
            jobs {
                regex(/ShivamLabs_App_*.*_Build/)
            }
            columns {
                status()
                weather()
                name()
                lastSuccess()
                lastFailure()
            }
        }//end list view
        listView('Applications Deploys') {
            jobs {
                regex(/ShivamLabs_App_*.*_Deploy/)
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
        view / defaultView('Applications Builds')
    }
}