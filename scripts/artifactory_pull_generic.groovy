import groovy.json.JsonSlurper
import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*

try {

    List<String> artifacts = new ArrayList<String>()

    def jenkinsCredentials = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
        com.cloudbees.plugins.credentials.Credentials.class,
        Jenkins.instance,
        null,
        null
    );

    def authTOKEN = ""

    for (creds in jenkinsCredentials) {
        if (creds.id == "artifactory-jfrog"){
            println("Credentials present!")
            authTOKEN = creds.password
        }
    }

    def connection = new URL("https://shivamlabs.jfrog.io/artifactory/api/search/artifact?name=${artifactName}&repos=${repository}").openConnection() as HttpURLConnection
    connection.setRequestMethod("GET")

    connection.setRequestProperty( 'X-JFrog-Art-Api', ('' + authTOKEN.toString() + ''))
    connection.setRequestProperty( 'Accept', 'application/json' )

    if ( connection.responseCode == 200 ) {
        def json = connection.inputStream.withCloseable { inStream ->
            new JsonSlurper().parse( inStream as InputStream )
        }

        def item = json
        item.results.each { f ->

            def myURI = f.toString().replaceAll('}', '').replaceAll('.zip', '')

            def artver = myURI.split('/')
            artver.each { r ->
                if (r.contains("project_artifacts")) {
                    myVer = r.toString().replaceAll('project_artifacts.', '')
                    println myVer
                    artifacts.add(myVer)
                }
            }
        }

        return artifacts.sort().reverse()
    }
} catch (Exception e) {
    print "There was a problem fetching the artifacts"
}