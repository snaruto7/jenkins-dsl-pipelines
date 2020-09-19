import groovy.json.JsonSlurper
import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*

try {

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

    List<String> artifacts = new ArrayList<String>()
    def connection = new URL("https://shivamlabs.jfrog.io/artifactory/api/docker/${docker_folder}/v2/${docker_image}/tags/list").openConnection() as HttpURLConnection
    connection.setRequestMethod("GET")

    connection.setRequestProperty( 'X-JFrog-Art-Api', ('' + authTOKEN.toString() + ''))
    connection.setRequestProperty( 'Accept', 'application/json' )

    if ( connection.responseCode == 200 ) {
        def json = connection.inputStream.withCloseable { inStream ->
            new JsonSlurper().parse( inStream as InputStream )
        }

        def item = json
        items.tags.each { f -> 
            println "${f}"
            artifacts.add(f)
        }

        return artifacts.sort { a,b -> 
            def av = a.split(/\./)
            def bv = b.split(/\./)

            for (i=0; i < av.length || i < bv.length; ++i) {
                def c
                if (av[i].isInteger() && bv[i].isInteger())
                    c = av[i] as Integer <=> bv[i] as Integer
                else
                    c = av[i] <=> bv[i]
                
                if(c != 0 )
                    return c
            }
            return 0
        }.reverse()
    }
} catch (Exception e) {
    print "There was a problem fetching the artifacts"
}