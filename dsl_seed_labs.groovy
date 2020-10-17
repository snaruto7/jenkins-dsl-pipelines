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
            external('./shivamlabs/dsl_seeds_app.groovy')
            removeAction('DELETE')
            removeViewAction('DELETE')
            additionalClasspath('**/tools/*.groovy')
        }
    }
}

job('dsl_seed_tf') {
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
            external('./terraform/dsl_seeds_terraform.groovy')
            removeAction('DELETE')
            removeViewAction('DELETE')
            additionalClasspath('**/tools/*.groovy')
        }
    }
}

job('dsl_seed_views'){
    scm{
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
            external('./views/views_*.groovy')
            removeAction('DELETE')
            removeViewAction('DELETE')
            additionalClasspath('**/tools/*.groovy')
        }
    }
}