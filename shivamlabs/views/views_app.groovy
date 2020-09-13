nestedView('Applications'){
    views {
        listView('Applications Overview') {
            jobs {
                regex(/ShivamLabs_*.*/)
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
                regex(/ShivamLabs_*.*_Build/)
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
                regex(/ShivamLabs_*.*_Deploy/)
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