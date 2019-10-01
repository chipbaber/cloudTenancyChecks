import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.PartialResultException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;


/*
 * Class: LDAP
 * Purpose: Provide simple API for accessing employee hierarchy and details from LDAP server.
 * Status: Original code written in 2012, needs syntax updates for 2019 but core changes fixed.
 * LDAP search from OS code.
 * Developer: Chip Baber
 */

public class ldap {

    /*Static variables for access to ldap initially*/
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
    protected static String INITCTX = "com.sun.jndi.ldap.LdapCtxFactory";
    protected static String MY_HOST = "ldaps://ldap.oracle.com:636";
    protected static String MY_SEARCHBASE = "dc=oracle,dc=com";
    protected static String MY_FILTER = "(&(objectclass=person)(mail=frank.baber@oracle.com))";
    Hashtable params = new Hashtable();
    DirContext ctx;
    SearchControls constraints;
    Attribute value;
    SearchResult sr;

    /*Result List Variables*/
    static ArrayList<Attributes> people = new ArrayList();
    static ArrayList<Attributes> people_temp = new ArrayList();

    public ldap() {
        params.put(Context.INITIAL_CONTEXT_FACTORY, INITCTX);
        params.put(Context.PROVIDER_URL, MY_HOST);
    }

    public ldap(String initctx, String host, String searchBase, String filter) {
        INITCTX = initctx;
        MY_HOST = host;
        MY_SEARCHBASE = searchBase;
        MY_FILTER = filter;
        params.put(Context.INITIAL_CONTEXT_FACTORY, INITCTX);
        params.put(Context.PROVIDER_URL, MY_HOST);
    }


    public void setSearchBase(String a) {
        MY_SEARCHBASE = a;
    }

    public void setFilter(String a) {
        MY_FILTER = a;
    }




    public void outputRaw(Attributes attrs) {
        try {
            logger.info("This user has "+ attrs.size()+ "   attributes");

            String holder = "";
            for (NamingEnumeration ae = attrs.getAll(); ae.hasMore();) {
                holder="";
                Attribute attr = (Attribute)ae.next();
                holder=attr.getID()+" --> ";
                /* Print each value */
                for (NamingEnumeration e = attr.getAll(); e.hasMore(); ) {
                    holder = holder + e.next();
                }
                logger.info(holder);
            }

        } catch (Exception e) {
            logger.warn("Error inside outputcore method.");
            logger.warn(e.toString());
        }

    }

    public void outputcore(Attributes attrs) {
        try {
            value = attrs.get("uid");
            System.out.println((String)value.get());
            value = attrs.get("uid");
            System.out.println((String)value.get());
            value = attrs.get("mail");
            System.out.println((String)value.get());
            value = attrs.get("orclcorpcostcenter");
            System.out.println((String)value.get());
            value = attrs.get("telephonenumber");
            System.out.println((String)value.get());
            value = attrs.get("orcltimezone");
            System.out.println((String)value.get());
            value = attrs.get("cn");
            System.out.println((String)value.get());
            value = attrs.get("orclwirelessaccountnumber");
            System.out.println((String)value.get());
            value = attrs.get("givenname");
            System.out.println((String)value.get());
            value = attrs.get("usr_guid");
            System.out.println((String)value.get());
            value = attrs.get("displayname");
            System.out.println((String)value.get());
            value = attrs.get("o");
            System.out.println((String)value.get());
            value = attrs.get("orclscreenname");
            System.out.println((String)value.get());
            value = attrs.get("manager");
            System.out.println(managerFilter((String)value.get()));

        } catch (NamingException e) {
            logger("Error inside outputcore method.");
            logger(e.toString());
        }


    }


    public void output_name(Attributes attrs) {
        try {
            value = attrs.get("uid");
            System.out.println((String)value.get());
        } catch (NamingException e) {
            logger("Error inside output_name method.");
            logger(e.toString());
        }

    }

    public void output_email(Attributes attrs) {
        try {
            value = attrs.get("mail");
            System.out.println((String)value.get());
        } catch (NamingException e) {
            logger("Error inside output_email method.");
            logger(e.toString());
        }

    }

    public void outputFirstName(Attributes attrs) {
        try {
            Attribute  givenname;
            givenname = attrs.get("givenname");
            System.out.println((String)givenname.get());
        } catch (NamingException e) {
            logger("Error inside output_email method.");
            logger(e.toString());
        }
    }

    public void outputLastName(Attributes attrs) {
        try {
            Attribute sn;
            sn = attrs.get("sn");
            System.out.println((String)sn.get());
        } catch (NamingException e) {
            logger("Error inside output_email method.");
            logger(e.toString());
        }
    }

    public void output_manager(Attributes attrs) {
        System.out.println(attrs.get("manager").toString());
    }



    /*Adds element to people object list in memory*/
    public void addPeople(Attributes a) {
        people.add(a);
    }

    public int howManyPeople() {
        return people.size();
    }

    public void printresults(int output_results) {
        Iterator<Attributes> it = people.iterator();
        while (it.hasNext()) {

            switch (output_results) {

                case 1:
                    outputcore(it.next());
                    break;

                case 2:
                    output_name(it.next());
                    break;

                case 3:
                    output_email(it.next());
                    break;

                case 4:
                    outputRaw(it.next());
                    break;

                case 5:
                    this.outputLastName(it.next());
                    break;

                case 6:
                    this.outputFirstName(it.next());
                    break;

                default:
                    outputcore(it.next());
                    break;

            }

        }
    }

    public void clearPeople() {
        people.clear();
    }

    /*Edits the manager element to display just the cn*/
    private String managerFilter(String a){
        return "Manager = "+ a.substring(a.indexOf("=")+1,a.indexOf(","));
    }

    public ArrayList getPeople() {
        return people;
    }


    public void getUser(String a) {
        MY_FILTER = "(&(objectclass=person)(mail=" + a + "))";
        logger("filter is: " + MY_FILTER);

        try {
            ctx = new InitialDirContext(params);
            constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration results = ctx.search(MY_SEARCHBASE, MY_FILTER, constraints);

            if (results != null) {
                while (results.hasMore()) {
                    sr = (SearchResult)results.next();
                    this.addPeople(sr.getAttributes());
                }

            } else {
                System.out.println("Not exist User");
            }
        }
        catch (AuthenticationException e) {
            System.out.println("You aren't authenticated on LDAP");
        }
        catch (PartialResultException e) {
            System.out.println(MY_FILTER + " Not exists in LDAP");
        }
        catch (NamingException e) {
            e.printStackTrace();
        }

    }

    /*Check to see if the user exists in LDAP is so return true, else false*/
    public boolean userExists(String emailAddress) {
        MY_FILTER = "(&(objectclass=person)(mail=" + emailAddress + "))";

        try {
            ctx = new InitialDirContext(params);
            constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration results = ctx.search(MY_SEARCHBASE, MY_FILTER, constraints);
               while (results.hasMore()) {
                    sr = (SearchResult)results.next();
                    this.addPeople(sr.getAttributes());
                }
                if (people.size()<1) {
                    return false;
                }
                else {
                    return true;
                }
        }
        catch (AuthenticationException e) {
            System.out.println("You aren't authenticated on LDAP");
        }
        catch (PartialResultException e) {
            System.out.println(MY_FILTER + " Not exists in LDAP");
            return false;
        }
        catch (NamingException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*Load all direct reports of a manager*/
    public void loadDirects(String email) {

        //Sample Manager Data Element
        //manager=manager: cn=andrea_jacobs,l=amer,dc=oracle,dc=com

        setFilter("(&(objectclass=person)(manager=*"+email.substring(0, email.indexOf("@")-1).replace('.', '_')+"*))");

        try {
            ctx = new InitialDirContext(params);
            constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration results = ctx.search(MY_SEARCHBASE, MY_FILTER, constraints);

            if (results != null ) {

                while( results.hasMore()) {
                    sr = (SearchResult)results.next();
                    this.addPeople(sr.getAttributes());
                }

            } else {
                // System.out.println("Search did not retrieve any results.");
            }


        } catch (AuthenticationException e) {
            System.out.println("You aren't authenticated on LDAP");

        } catch (PartialResultException e) {
            System.out.println(MY_FILTER + " Not exists in LDAP");

        } catch (NamingException e) {
            e.printStackTrace();

        }

    }


    /*See if person is a manager */
    public boolean isManager(String email) {

        //Sample Manager Data Element
        //manager=manager: cn=andrea_jacobs,l=amer,dc=oracle,dc=com


        setFilter("(&(objectclass=person)(manager=*" + email.substring(0, email.indexOf("@") - 1).replace('.', '_') + "*))");

        try {
            ctx = new InitialDirContext(params);
            constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration results = ctx.search(MY_SEARCHBASE, MY_FILTER, constraints);

            if (results != null && results.hasMore()) {
                //logger("Returning Results");
                return true;
            }

            else {
                //Person is not a manager
                // System.out.println("Search did not retrieve any results.");
                return false;
            }


        } catch (AuthenticationException e) {
            System.out.println("You aren't authenticated on LDAP");
            return false;
        } catch (PartialResultException e) {
            System.out.println(MY_FILTER + " Not exists in LDAP");
            return false;
        } catch (NamingException e) {
            e.printStackTrace();
            return false;
        }

    }




    /*Method to recursively search the subtree Ldap to identify all the reports of a manager, not just direct reports.*/
    public void processTotalReports() {
        //Queue to hold managers identified 1 level deep.
        ArrayList<String> managers = new ArrayList();
        Iterator<Attributes> it = people.iterator();
        Attributes holder;
        boolean checker =false;


        while (it.hasNext()) {
            holder = it.next();

            try {
                //if attribute does not exist then it could return null so check and account for instance.
                if (holder.get("processedManagerCheck") != null){
                    value = holder.get("processedManagerCheck");
                    checker = (boolean)value.get();
                }
                else {
                    checker = false;
                }


                //if the person has not been checked as a manager then check and add processing flag.
                if (!checker) {
                    value =holder.get("mail");
                    if (isManager((String)value.get() ) ){
                        managers.add((String)value.get());
                        //  logger("Manager added to queue  : "+(String)value.get());
                    }

                    holder.put("processedManagerCheck", true);
                    people_temp.add(holder);
                }
                //if the person has been checked as a manager then we only need to add the current value to the temp array
                else {
                    people_temp.add(holder);

                }

            } catch (Exception e) {
                logger("Inside catch block of processTotal it.hasnext().");
                logger(e.toString());
                checker = false;
            }

        }

        //replace the people with the updated array list and the processManagerCheckFlag
        people.clear();
        people.addAll(people_temp);
        people_temp.clear();

        //if the managers queue is >0 iterate through the queue for processing.
        if (managers.size()>0) {
            Iterator<String> el_effe = managers.iterator();

            while (el_effe.hasNext()) {
                loadDirects(el_effe.next());
            }
            processTotalReports();

        }


    }




    private static void logger(String strMessage) {
        System.out.println(now() + " " + strMessage);
    }

    private static String now() {
        String DATE_FORMAT_NOW = "MM/dd/yy H:mm:ss:SSS";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }




    public static void main(String[] args) {

        ldap Informe = new ldap();

           //Program 1 is a simple individual information search.
         //   Informe.logger("Program Starting");
/*
           // System.out.println("User exists in LDAP: "+Informe.userExists("nolan.corcoran@oracle.com"));
           Informe.logger(" ");
            Informe.getUser("frank.baber@oracle.com");
            Informe.logger("\n Print out personal information.");

             Informe.printresults(4);


            Informe.logger("\n\n\n\n\n\n ");

*/
        String name = "frank.baber@oracle.com";
        //System.out.println("\nIs "  Informe.outputcore);
        // System.out.println("\nIs " + name + " a manager:  " + Informe.isManager(name));
        Informe.loadDirects(name);
        System.out.println("\n" + name + " has " + Informe.howManyPeople() + "  Direct Reports.");
        //System.out.println("\nThere names are:");
        // Informe.printresults(3);
        //  Informe.logger("\n\n\n\n\n\n ");

        //  Informe.logger("Searching for Total Reports.\n\n ");
        Informe.processTotalReports();
        System.out.println("\n" + name + " has " + Informe.howManyPeople() + " total Reports: ");
        Informe.printresults(3);
       /* System.out.println("\n\n");
        Informe.printresults(5);
        System.out.println("\n\n");
        Informe.printresults(6);
        Informe.logger("Program Finished");
*/
    }


}

