import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.identity.Identity;
import com.oracle.bmc.identity.IdentityClient;
import com.oracle.bmc.identity.model.Compartment;
import com.oracle.bmc.identity.requests.ListCompartmentsRequest;
import com.oracle.bmc.identity.responses.ListCompartmentsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class ListCompartments {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

    /* This method identifies all compartments out of violation for the account mapping model.
    *
    * */
    private static void getViolationCompartments(Identity identityClient, String compartmentId){
        String nextPageToken = null;
        System.out.println("ListCompartments");
        //Build appoved compartment list -- will pull from csv in future.
        List violationCompartments = new ArrayList();
        List approvedCompartments = new ArrayList();
        approvedCompartments.add("Canada");
        approvedCompartments.add("DevCS");
        approvedCompartments.add("FSI");
        approvedCompartments.add("FSI_Communications");
        approvedCompartments.add("Healthcare_Lifescience");
        approvedCompartments.add("ManagedCompartmentForPaaS");
        approvedCompartments.add("Mfg_Auto_Travel_Defense");
        approvedCompartments.add("Retail");
        approvedCompartments.add("Energy_Utiliies_Process");
        approvedCompartments.add("Media_High-tech");

        do {
            ListCompartmentsResponse response =
                    identityClient.listCompartments(
                            ListCompartmentsRequest.builder()
                                    .limit(30)
                                    .compartmentId(compartmentId)
                                    .page(nextPageToken)
                                    .build());

            for (Compartment compartment : response.getItems()) {
                if (approvedCompartments.contains(compartment.getName())) {
                    //do nothing compartment is approved.
                } else {
                    violationCompartments.add(compartment);
                    System.out.println("Violation Compartment Name is:   "+compartment.getName());
                }

            }
            nextPageToken = response.getOpcNextPage();
        } while (nextPageToken != null);
    }



    public static void main(String[] args) throws Exception {

        // TODO: Fill in this value
        String configurationFilePath = ".oci/config";
        String profile = "DEFAULT";

        AuthenticationDetailsProvider provider =   new ConfigFileAuthenticationDetailsProvider(configurationFilePath, profile);
        ConfigFileReader.ConfigFile config = ConfigFileReader.parse(configurationFilePath, profile);
        logger.info("OCI Compartment ID set to:"+config.get("compartment-id"));

        String compartmentId = config.get("compartment-id");
        final String tenantId = provider.getTenantId();
        Identity identityClient = new IdentityClient(provider);
        identityClient.setRegion(config.get("region"));
        logger.info("********************Identifing Violation Compartments************");
        getViolationCompartments(identityClient, config.get("compartment-id"));

        // List all compartments within tenancy with Accessible compartment filter
        String nextPageToken = null;
 /*       System.out.println( "ListCompartments: with compartmentIdInSubtree == true and AccessLevel==Accessible");
        do {
            ListCompartmentsResponse response =
                    identityClient.listCompartments(
                            ListCompartmentsRequest.builder()
                                    .limit(20)
                                    .compartmentId(compartmentId)
                                    .accessLevel(ListCompartmentsRequest.AccessLevel.Accessible)
                                    .compartmentIdInSubtree(Boolean.TRUE)
                                    .page(nextPageToken)
                                    .build());

            for (Compartment compartment : response.getItems()) {
                System.out.println("Compartment Name: "+compartment.getName() );
            }
            nextPageToken = response.getOpcNextPage();
        } while (nextPageToken != null);

        // List all compartments within tenancy without Accessible compartment filter
        System.out.println("ListCompartments: with compartmentIdInSubtree == true");
        nextPageToken = null;
        do {
            ListCompartmentsResponse response =
                    identityClient.listCompartments(
                            ListCompartmentsRequest.builder()
                                    .limit(40)
                                    .compartmentId(compartmentId)
                                    .compartmentIdInSubtree(Boolean.TRUE)
                                    .page(nextPageToken)
                                    .build());

            for (Compartment compartment : response.getItems()) {
                System.out.println("Compartment Name: "+compartment.getName());
            }
            nextPageToken = response.getOpcNextPage();
        } while (nextPageToken != null);

        // List single level compartments within tenancy with Accessible compartment filter
        System.out.println("ListCompartments: with AccessLevel == Accessible");
        do {
            ListCompartmentsResponse response =
                    identityClient.listCompartments(
                            ListCompartmentsRequest.builder()
                                    .limit(3)
                                    .compartmentId(compartmentId)
                                    .accessLevel(ListCompartmentsRequest.AccessLevel.Accessible)
                                    .page(nextPageToken)
                                    .build());

            for (Compartment compartment : response.getItems()) {
                System.out.println("Compartment Name: "+compartment.getName());
            }
            nextPageToken = response.getOpcNextPage();
        } while (nextPageToken != null);

        // List single level compartments within tenancy without Accessible compartment filter
        System.out.println("ListCompartments");
        do {
            ListCompartmentsResponse response =
                    identityClient.listCompartments(
                            ListCompartmentsRequest.builder()
                                    .limit(10)
                                    .compartmentId(compartmentId)
                                    .page(nextPageToken)
                                    .build());

            for (Compartment compartment : response.getItems()) {
                System.out.println("Compartment Name: "+compartment.getName());
            }
            nextPageToken = response.getOpcNextPage();
        } while (nextPageToken != null);
        */

    }



}
