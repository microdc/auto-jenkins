import hudson.security.SecurityRealm
import hudson.security.GlobalMatrixAuthorizationStrategy
import org.jenkinsci.plugins.GithubSecurityRealm
import jenkins.model.*

String githubWebUri = System.getenv("GITHUB_OAUTH_WEB_URI") ?: GithubSecurityRealm.DEFAULT_WEB_URI
String githubApiUri = System.getenv("GITHUB_OAUTH_API_URI") ?: GithubSecurityRealm.DEFAULT_API_URI
String oauthScopes = System.getenv("GITHUB_OAUTH_SCOPES")   ?: GithubSecurityRealm.DEFAULT_OAUTH_SCOPES

clientID = System.getenv("GITHUB_OAUTH_CLIENT_ID")
clientSecret = System.getenv("GITHUB_OAUTH_CLIENT_SECRET")
adminUserOrGroup = System.getenv("GITHUB_OAUTH_ADMIN") ?: "admin"

if(!Jenkins.instance.isQuietingDown()) {
    if(clientID && clientSecret) {
        SecurityRealm github_realm = new GithubSecurityRealm(githubWebUri, githubApiUri, clientID, clientSecret, oauthScopes)

        //check for equality, no need to modify the runtime if no settings changed
        if(!github_realm.equals(Jenkins.instance.getSecurityRealm())) {
            Jenkins.instance.setSecurityRealm(github_realm)
            Jenkins.instance.save()
            println 'Security realm configuration has changed.  Configured GitHub security realm.'
        } else {
            println 'Nothing changed.  GitHub security realm already configured.'
        }

        /*
        For valid permissions,
        http://javadoc.jenkins-ci.org/hudson/security/class-use/Permission.html#hudson.slaves
        */
        valid_perms_map = [
            global_admin:                 Jenkins.ADMINISTER,
            global_read:                  Jenkins.READ,
            global_run_scripts:           Jenkins.RUN_SCRIPTS,
            configure_updatecenter:       hudson.PluginManager.CONFIGURE_UPDATECENTER,
            upload_plugins:               hudson.PluginManager.UPLOAD_PLUGINS,

            credentials_create:           com.cloudbees.plugins.credentials.CredentialsProvider.CREATE,
            credentials_delete:           com.cloudbees.plugins.credentials.CredentialsProvider.DELETE,
            credentials_update:           com.cloudbees.plugins.credentials.CredentialsProvider.UPDATE,
            credentials_view:             com.cloudbees.plugins.credentials.CredentialsProvider.VIEW,
            credentials_manage_domains:   com.cloudbees.plugins.credentials.CredentialsProvider.MANAGE_DOMAINS,

            slave_build:                  hudson.model.Computer.BUILD,
            slave_connect:                hudson.model.Computer.CONNECT,
            slave_configure:              hudson.model.Computer.CONFIGURE,
            slave_create:                 hudson.model.Computer.CREATE,
            slave_delete:                 hudson.model.Computer.DELETE,
            slave_disconnect:             hudson.model.Computer.DISCONNECT,

            job_build:                    hudson.model.Item.BUILD,
            job_cancel:                   hudson.model.Item.CANCEL,
            job_configure:                hudson.model.Item.CONFIGURE,
            job_create:                   hudson.model.Item.CREATE,
            job_delete:                   hudson.model.Item.DELETE,
            job_discover:                 hudson.model.Item.DISCOVER,
            job_read:                     hudson.model.Item.READ,
            job_workspace:                hudson.model.Item.WORKSPACE,

            run_delete:                   hudson.model.Run.DELETE,
            run_update:                   hudson.model.Run.UPDATE,

            view_read:                    hudson.model.View.READ,
            view_configure:               hudson.model.View.CONFIGURE,
            view_create:                  hudson.model.View.CREATE,
            view_delete:                  hudson.model.View.DELETE,
        ]
        def gmas = Jenkins.instance.getAuthorizationStrategy()
        if( !(gmas instanceof GlobalMatrixAuthorizationStrategy) ) {
            gmas = new hudson.security.GlobalMatrixAuthorizationStrategy()
        }
        gmas.add(valid_perms_map["global_admin"], "admin")
        gmas.add(valid_perms_map["global_admin"], "registrator") // to register slaves
        gmas.add(valid_perms_map["global_admin"], adminUserOrGroup)

        gmas.add(valid_perms_map["global_read"], "EqualExperts")
        gmas.add(valid_perms_map["job_read"], "EqualExperts")
        gmas.add(valid_perms_map["job_build"], "EqualExperts")
        gmas.add(valid_perms_map["job_cancel"], "EqualExperts")
        gmas.add(valid_perms_map["job_configure"], "EqualExperts")
        gmas.add(valid_perms_map["job_discover"], "EqualExperts")
        gmas.add(valid_perms_map["job_workspace"], "EqualExperts")
        gmas.add(valid_perms_map["view_read"], "EqualExperts")
        gmas.add(valid_perms_map["view_configure"], "EqualExperts")
        gmas.add(valid_perms_map["view_delete"], "EqualExperts")
        gmas.add(valid_perms_map["slave_delete"], "EqualExperts")

        Jenkins.instance.setAuthorizationStrategy(gmas)
        Jenkins.instance.save()
    }

} else {
    println 'Shutdown mode enabled.  Configure GitHub security realm SKIPPED.'
}
