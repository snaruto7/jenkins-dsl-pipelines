job('dsl_seed_apps') {
    scm {
        git {
            remote {
                url('https://github.com/snaruto7/jenkins-dsl-pipelines.git')
                credentials('github-creds')
            }
            branches('*/master')
        }
    }
    triggers {
        upstream('dsl_meta_seed', 'UNSTABLE')
    }
    wrappers {
        preBuildCleanup()
    }
    steps {
        dsl {
            external('./applications/dsl_seeds_app.groovy')
            removeAction('DELETE')
            removeViewAction('DELETE')
            additionalClasspath('**/tools/*.groovy')
        }
    }
}