import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.identity.Identity;
import com.oracle.bmc.identity.IdentityClient;
import com.oracle.bmc.identity.model.User;
import com.oracle.bmc.identity.requests.ListUsersRequest;
import com.oracle.bmc.identity.responses.ListUsersResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class hubUserChecks {

    //Simple declare for later use.
    hubUserChecks() {

    }

     // Set the logger to the class.
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
    private static List<User> tenancyUsers = new ArrayList<User>();

    public static void main(String[] args) throws Exception {

        hubUserChecks a = new hubUserChecks();
        logger.info("Starting Program");
       // TODO: Fill in this value
        String configurationFilePath = ".oci/config";
        String profile = "DEFAULT";
        ConfigFileReader.ConfigFile config = ConfigFileReader.parse(configurationFilePath, profile);
        logger.info("OCI Compartment ID set to:"+config.get("compartment-id"));

        AuthenticationDetailsProvider provider =  new ConfigFileAuthenticationDetailsProvider(configurationFilePath, profile);

        final String tenantId = provider.getTenantId();
        final Identity identityClient = new IdentityClient(provider);
        identityClient.setRegion(config.get("region"));

        logger.info("Running program to check users in tenancy.");
        hubUserChecks hubTenancy =new hubUserChecks();
        //Get Federated Identity users
        hubTenancy.setTenancyUsers(identityClient, config.get("compartment-id"),config.get("identityProvider"));
        //hubTenancy.getHubTenancyUsersSize();
       // hubTenancy.printTenancyUsers();

          hubTenancy.getUsersNoLongerAtOracle();

        //System.out.println("User in tenancy = "+hubTenancy.userInTenancyLDAP("chip.baber@oracle.com"));

       // ArrayList<Attributes> allHubUsers = new ArrayList<Attributes>();
       // allHubUsers.addAll(hubTenancy.getOrgFromLDAP("doug.basset@oracle.com"));
       // allHubUsers.addAll(hubTenancy.getOrgFromLDAP("frank.baber@oracle.com"));
       // allHubUsers.addAll(hubTenancy.getOrgFromLDAP("santosh.kunchala@oracle.com"));
       // allHubUsers.addAll(hubTenancy.getOrgFromLDAP("uche.ibekwe@oracle.com "));
       // allHubUsers.addAll(hubTenancy.getOrgFromLDAP("syed.i.imam@oracle.com"));
       // allHubUsers.addAll(hubTenancy.getOrgFromLDAP("maharshi.desai@oracle.com"));
       // System.out.println("Size of all allHubUsers is: "+ allHubUsers.size());

       // System.out.println("Get all hub users not in Tenancy."+"\n");
       // ArrayList<Attributes> toBeAddedHubsters = hubTenancy.orgUsersNotInTenancy(allHubUsers);
       // System.out.println("Number of Hubsters to be added: "+ toBeAddedHubsters.size());
       // hubTenancy.writeHubstersToAdd(toBeAddedHubsters);
        /**/
   }

   /*
   Method to retrieve tenancy for all users.
    */
   public void setTenancyUsers(Identity identityClient, String compartment) {
       String nextPageToken = null;
       do {
           ListUsersResponse hubTenancyUsers =  identityClient.listUsers(
                   ListUsersRequest.builder()
                           .compartmentId(compartment)
                           .page(nextPageToken)
                           .build());
           for (User user : hubTenancyUsers.getItems()) {
               // System.out.println(user.getName());
               tenancyUsers.add(user);
           }
           nextPageToken = hubTenancyUsers.getOpcNextPage();
       } while (nextPageToken != null);
   }
    /*
    Method to retrieve tenancy for specific identity provider
     */
    public void setTenancyUsers(Identity identityClient, String compartment, String identityProviderId) {
        String nextPageToken = null;
        do {
            ListUsersResponse hubTenancyUsers =  identityClient.listUsers(
                               ListUsersRequest.builder().identityProviderId(identityProviderId)
                                    .compartmentId(compartment)
                                    .page(nextPageToken)
                                    .build());
            for (User user : hubTenancyUsers.getItems()) {
                // System.out.println(user.getName());
                 tenancyUsers.add(user);
            }
            nextPageToken = hubTenancyUsers.getOpcNextPage();
        } while (nextPageToken != null);
    }

    /*Return the Tenancy Users*/
    private List<User> getTenancyUsers() {
        return tenancyUsers;
    }

    /*Print tenancy users to console*/
    private void printTenancyUsers() {
        for (User hubUsers : tenancyUsers) {
            System.out.println(hubUsers.getName());

        }
    }

    /*List Users in Tennancy but not in LDAP*/
    private void getUsersNoLongerAtOracle() {
        //Loop through tennancy users
        for (User hubUsers : tenancyUsers) {
            //bypass if local user.
            if (hubUsers.getName().indexOf('/') > 1) {
                //if user does not exist in LDAP output the users name.
                if (!userExistsInLDAP(hubUsers.getName().substring(hubUsers.getName().indexOf('/') + 1))) {
                    System.out.println(hubUsers.getName().substring(hubUsers.getName().indexOf('/') + 1));
                }
            }
        }
        logger.info("Checking for user in tenancy but not LDAP complete.");
    }

    /*
    Method to see if a user is in the tenancy
     */
    private boolean userInTenancy(String email) {
        return tenancyUsers.stream().anyMatch(t -> t.getName().substring(t.getName().indexOf('/')+1).equals(email));
    }

    /*
 Method to see if a user is in the tenancy with the input of just a email address.
  */
    private boolean userInTenancyLDAP(String email) {
        String t_name = "";
        for (User hubster : tenancyUsers) {
            t_name = hubster.getName().substring(hubster.getName().indexOf('/')+1).toLowerCase().trim();
           // System.out.println("tenancy name = " + t_name);
            //System.out.println("input name = " + email);
            if (t_name.equals(email)) {
                return true;
            }
        }
        return false;
    }

    /*
     Method userExists checks the LDAP server for a email address to see if user in corp LDAP
     */
    private boolean userExistsInLDAP(String email) {
        return new ldap().userExists(email);
    }


    /*
     Method to identify users in an org that are not in the tenancy as Federated users.
     */
    private ArrayList<Attributes> orgUsersNotInTenancy(ArrayList<Attributes> emails){
        System.out.println("Checking for users not in tenancy but in org.");
        ArrayList<Attributes> notInTenancy = new ArrayList<Attributes>();
        String holder = "";
        try {
            //loop through each email in org.
            for (Attributes a : emails) {
                    holder = a.get("mail").get().toString();
                    holder = holder.toLowerCase();
                    holder = holder.trim();
                    //logger.info("Looking for user: "+holder + " in tenancy.");
                //if user not in org
                if (userInTenancyLDAP(holder)) {
                   //do nothing
                    //System.out.println(a.get("mail").get().toString());
                }
                else{
                   // System.out.println("User not in tenancy");
                    notInTenancy.add(a);
                }
            }
            return notInTenancy;
        }
        catch (NamingException e) {
            logger.warn("Error inside orgUserTenancyCheck method.");
            logger.warn(e.toString());
        }
        return notInTenancy;
    }

    /*Return the size of the hub user count*/
    private void getHubTenancyUsersSize() {
        System.out.println("Size of Hub Tenancy Users is: "+ tenancyUsers.size());
    }

    /*
     Method gets all employees and a users organization
     */
    private ArrayList<Attributes> getOrgFromLDAP (String email) {
        System.out.println("Gathering organization users from LDAP for " + email);
        ldap Informe = new ldap();
        Informe.clearPeople();
        Informe.loadDirects(email);
        Informe.processTotalReports();
       // Informe.printresults(3);
        System.out.println(Informe.howManyPeople()+" people found in org.");
        return Informe.getPeople();
    }

    private void writeHubstersToAdd(ArrayList<Attributes> peeps) {
        try (

                PrintWriter writer = new PrintWriter(new File("hubstersToAdd.csv"))) {
//User Name	Work Email	Primary Email Type	First Name	Last Name
            StringBuilder sb = new StringBuilder();
            sb.append("User Name");
            sb.append(',');
            sb.append("Work Email");
            sb.append(',');
            sb.append("Primary Email Type");
            sb.append(',');
            sb.append("First Name");
            sb.append(',');
            sb.append("Last Name");
            sb.append('\n');



            for (Attributes a : peeps) {
                try {
                    sb.append(a.get("mail").get().toString());
                    sb.append(',');
                    sb.append(a.get("mail").get().toString());
                    sb.append(',');
                    sb.append("Work");
                    sb.append(',');
                    sb.append(a.get("givenname").get().toString());
                    sb.append(',');
                    sb.append(a.get("sn").get().toString());
                    sb.append('\n');
                }
                catch (NamingException e) {
                    logger.warn("Error inside orgUserTenancyCheck method.");
                    logger.warn(e.toString());
                }
            }

            writer.write(sb.toString());
            writer.close();
            System.out.println("done!");

        } catch ( FileNotFoundException e) {
            System.out.println(e.getMessage());
        }



    }



}
