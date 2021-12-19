package turnertech.qftest;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;


// UI Docs here! https://docs.huihoo.com/webwork/2.2.6/Form%20Tags.html


public class RunQFTest implements TaskType
{
    @Override
    public TaskResult execute(final TaskContext taskContext) throws TaskException
    {
        final BuildLogger buildLogger = taskContext.getBuildLogger();
		final TaskResultBuilder builder = TaskResultBuilder.create(taskContext).failed(); //Initially set to Failed.

        buildLogger.addBuildLogEntry("Hello, World!");

		if (true) {
			builder.success();
		}

		final TaskResult result = builder.build();
        return result;
    }
}