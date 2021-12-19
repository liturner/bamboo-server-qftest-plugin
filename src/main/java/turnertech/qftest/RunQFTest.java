package turnertech.qftest;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.build.test.TestCollationService;
import com.atlassian.bamboo.build.test.junit.JunitTestReportCollector;
import com.atlassian.bamboo.process.CommandlineStringUtils;
import com.atlassian.bamboo.process.ExternalProcessBuilder;
import com.atlassian.bamboo.process.ProcessService;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskState;
import com.atlassian.bamboo.task.TaskType;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityContext;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.utils.process.ExternalProcess;
import com.google.common.collect.ImmutableList;

import org.apache.commons.lang3.StringUtils;

// UI Docs here! https://docs.huihoo.com/webwork/2.2.6/Form%20Tags.html


public class RunQFTest implements TaskType
{
	@ComponentImport
	@Inject
	private TestCollationService testCollationService;
	
	@ComponentImport
	@Inject
	private CapabilityContext capabilityContext;
	
	@ComponentImport
	@Inject
	private I18nResolver i18nResolver;
	
	@ComponentImport
	@Inject
	private ProcessService processService;
	
	public void setTestCollationService(final TestCollationService testCollationService) {
		this.testCollationService = testCollationService;
	}
	
	public void setCapabilityContext(final CapabilityContext capabilityContext) {
		this.capabilityContext = capabilityContext;
	}
	
	public void setI18nResolver(I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }
	
	public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }
		
    @Override
    public TaskResult execute(final TaskContext taskContext) throws TaskException
    {
        final BuildLogger buildLogger = taskContext.getBuildLogger();
		final TaskResultBuilder builder = TaskResultBuilder.newBuilder(taskContext).failed(); //Initially set to Failed.

		final String qfTestRuntime = taskContext.getConfigurationMap().get(RunQFTestHelper.QF_EXECUTABLE);
		final String qfTestRuntimePath = capabilityContext.getCapabilityValue(RunQFTestHelper.QF_CAPABILITY_PREFIX + "." + qfTestRuntime);
		final String qfTestLogFolder = taskContext.getConfigurationMap().get(RunQFTestHelper.LOG_OUTPUT_FOLDER);
		final String jUnitTestFolder = qfTestLogFolder + "\\junit";
		final String qfTestLogName = taskContext.getConfigurationMap().get(RunQFTestHelper.LOG_OUTPUT_NAME);
		final String qfTestFile = taskContext.getConfigurationMap().get(RunQFTestHelper.QF_FILE);
		final String qfTestVariableString = taskContext.getConfigurationMap().get(RunQFTestHelper.QF_VARIABLES);
		final String[] qfTestVariables = StringUtils.isEmpty(qfTestVariableString) ? new String[0] : taskContext.getConfigurationMap().get(RunQFTestHelper.QF_VARIABLES).split("\n");


        buildLogger.addBuildLogEntry("# Constructing QF-Test command line with the following supplied parameters:");
        buildLogger.addBuildLogEntry("#   " + i18nResolver.getText("tt.qftest.executable") + ": " + qfTestRuntime);
        buildLogger.addBuildLogEntry("#     " + qfTestRuntimePath);
        buildLogger.addBuildLogEntry("#   " + i18nResolver.getText("tt.qftest.logs") + ": " + qfTestLogFolder);
        buildLogger.addBuildLogEntry("#   " + i18nResolver.getText("tt.qftest.logName") + ": " + qfTestLogName);
        buildLogger.addBuildLogEntry("#   " + i18nResolver.getText("tt.qftest.file") + ": " + qfTestFile);
        buildLogger.addBuildLogEntry("#   " + i18nResolver.getText("tt.qftest.variables") + ": " + qfTestVariables.length);
        for (String var : qfTestVariables ) {
        	buildLogger.addBuildLogEntry("#     " + var);
        }

        final ArrayList<String> commandList = new ArrayList<String>();
        commandList.add(qfTestRuntimePath);
        commandList.add("-batch");
        commandList.add("-engine awt,swt");
        commandList.add("-runtime");
        commandList.add("-verbose [tests,sequences,errors]");
        commandList.add("-runlog " + qfTestLogFolder + "\\runlog\\" + qfTestLogName);
        commandList.add("-report-junit " + jUnitTestFolder);
        for (String var : qfTestVariables ) {
        	commandList.add("-variable " + var);
        }
        commandList.add(qfTestFile);
        
        buildLogger.addBuildLogEntry("# Constructed Command Line:");
        buildLogger.addBuildLogEntry("#   " + String.join(" ", commandList));
        
        // Check that the executable exists on the agent
        File file = new File(qfTestRuntimePath);
        if (file.isDirectory() || !file.exists()) {
        	buildLogger.addErrorLogEntry("The supplied runtime either does not exist or is not an executable!");
        	return builder.failedWithError().build();
        }
                
        ExternalProcess process = processService.createExternalProcess(taskContext, 
                new ExternalProcessBuilder()
                .command(commandList)
                .workingDirectory(taskContext.getWorkingDirectory()));

        process.execute();
        builder.checkReturnCode(process, 0);
        if(builder.getTaskState() == TaskState.FAILED) {
        	return builder.build();
        }
        
        File jUnitTestFolderFile = new File(jUnitTestFolder);
        if (!jUnitTestFolderFile.isDirectory() || !file.exists()) {
        	buildLogger.addErrorLogEntry("Unable to find the JUnit Test Log directory! This is an internal failure!");
        	return builder.failedWithError().build();
        } else {
        	final String testFilePattern = taskContext.getConfigurationMap().get(jUnitTestFolder);
        	testCollationService.collateTestResults(taskContext, testFilePattern, new JunitTestReportCollector());
        }

        builder.checkTestFailures();
        
        return builder.build();
    }
}