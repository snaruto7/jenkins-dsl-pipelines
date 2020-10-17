job('dsl_seed_maintenance_builds') {
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
            external('./jenkins-maintenance/builds/builds_*.groovy')
            removeAction('DELETE')
            removeViewAction('DELETE')
        }
    }
}

// job('dsl_seed_maintenance_deploys') {
//     scm {
//         git {
//             remote {
//                 url('https://github.com/snaruto7/jenkins-dsl-pipelines.git')
//                 credentials('github-creds')
//             }
//             branches('*/master')
//         }
//     }
//     triggers {
//         upstream('dsl_seed_tf', 'UNSTABLE')
//     }
//     wrappers {
//         preBuildCleanup()
//     }
//     steps {
//         dsl {
//             external('./jenkins-maintenance/deploys/deploys_*.groovy')
//             removeAction('DELETE')
//             removeViewAction('DELETE')
//         }
//     }
// }

job('dsl_seed_maintenance_views') {
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
            external('./jenkins-maintenance/views/views_*.groovy')
            removeAction('DELETE')
            removeViewAction('DELETE')
        }
    }
}
