job('dsl_seed_app_builds') {
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
        upstream('dsl_seed_apps', 'UNSTABLE')
    }
    wrappers {
        preBuildCleanup()
    }
    steps {
        dsl {
            external('./shivamlabs/builds/builds_*.groovy')
            removeAction('DELETE')
            removeViewAction('DELETE')
            additionalClasspath('**/tools/*.groovy')
        }
    }
}

job('dsl_seed_app_deploys') {
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
        upstream('dsl_seed_apps', 'UNSTABLE')
    }
    wrappers {
        preBuildCleanup()
    }
    steps {
        dsl {
            external('./shivamlabs/deploys/deploys_*.groovy')
            removeAction('DELETE')
            removeViewAction('DELETE')
        }
    }
}

job('dsl_seed_app_views') {
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
        upstream('dsl_seed_apps', 'UNSTABLE')
    }
    wrappers {
        preBuildCleanup()
    }
    steps {
        dsl {
            external('./shivamlabs/views/views_*.groovy')
            removeAction('DELETE')
            removeViewAction('DELETE')
        }
    }
}
