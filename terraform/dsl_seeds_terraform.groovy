job('dsl_seed_terraform_builds') {
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
        upstream('dsl_seed_tf', 'UNSTABLE')
    }
    wrappers {
        preBuildCleanup()
    }
    steps {
        dsl {
            external('./terraform/builds/builds_*.groovy')
            removeAction('DELETE')
            removeViewAction('DELETE')
        }
    }
}

job('dsl_seed_terraform_deploys') {
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
        upstream('dsl_seed_tf', 'UNSTABLE')
    }
    wrappers {
        preBuildCleanup()
    }
    steps {
        dsl {
            external('./terraform/deploys/deploys_*.groovy')
            removeAction('DELETE')
            removeViewAction('DELETE')
        }
    }
}

job('dsl_seed_terraform_views') {
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
        upstream('dsl_seed_tf', 'UNSTABLE')
    }
    wrappers {
        preBuildCleanup()
    }
    steps {
        dsl {
            external('./terraform/views/views_*.groovy')
            removeAction('DELETE')
            removeViewAction('DELETE')
        }
    }
}