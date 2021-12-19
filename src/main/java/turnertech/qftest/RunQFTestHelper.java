package turnertech.qftest;

/**
 * @author Luke Ian Turner
 *
 * Most constant strings here match to a "name" key in the UI ".ftl" files. They are stored here for ease of use and to prevent bugs.
 *
 */
public class RunQFTestHelper {

	private RunQFTestHelper() {
		// This is a static helper class. Do not instantiate it.
	}

	/**
     * The user selected QF-Test Capability to be used for the task
     */
    public static final String QF_EXECUTABLE = "qfTestExecutable";
      
    /**
     * The list of available QF-Test capabilities
     */
    public static final String QF_EXECUTABLES = "qfTestExecutables";
    
    /**
     * The prefix for the capability. This is a static value we need for Bamboo
     */
    public static final String QF_CAPABILITY_PREFIX = "system.builder.qftest";
        
    /**
     * The folder specified as the location to write test logs to. This is a mandatory field in the configurator
     */
    public static final String LOG_OUTPUT_FOLDER = "logOutputFolder";	
    
    /**
     * The output file name to use for the generated logs
     */
    public static final String LOG_OUTPUT_NAME = "logNameFormat";	
        
    /**
     * The file which QF-Test must execute
     */
    public static final String QF_FILE = "qfTestFile";
    
    /**
     * QF-Test variables to be passed into QF-Test. These are important to good CI with QF!
     */
    public static final String QF_VARIABLES = "qfVariables";
    
    /**
     * Additional Environment variables to be passed to the Java VM
     */
    public static final String ENV_VARS = "environmentVariables";  
    
}
