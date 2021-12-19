package turnertech.qftest;

import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.task.TaskRequirementSupport;
import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.specs.builders.task.NUnitRunnerTask;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.v2.build.agent.capability.Requirement;
import com.atlassian.bamboo.v2.build.agent.capability.RequirementImpl;
import com.atlassian.bamboo.ww2.actions.build.admin.create.UIConfigSupport;
import com.atlassian.plugin.spring.scanner.annotation.imports.BambooImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

public class RunQFTestConfigurator extends AbstractTaskConfigurator implements TaskRequirementSupport {
	
    @ComponentImport
    @Inject
    protected I18nResolver i18nResolver;
    
    @BambooImport("uiConfigBean")
    @Inject
    protected UIConfigSupport uiConfigSupport;
	
    /**
     * The user selected QF-Test Capability to be used for the task
     */
    private static final String QF_EXECUTABLE = "qfTestExecutable";
        
    /**
     * The list of available QF-Test capabilities
     */
    private static final String QF_EXECUTABLES = "qfTestExecutables";
    
    /**
     * The prefix for the capability. This is a static value we need for Bamboo
     */
    private static final String QF_CAPABILITY_PREFIX = "system.builder.qftest";
    
    /**
     * This setter must be here to support injection!
     * 
     * @param i18nResolver
     */
    public void setI18nResolver(I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    /**
     * This setter must be here to support injection!
     * 
     * @param uiConfigSupport
     */
    public void setUiConfigSupport(UIConfigSupport uiConfigSupport) {
        this.uiConfigSupport = uiConfigSupport;
    }
    
    /**
     * Called before saving the configuration with {@link #generateTaskConfigMap}
     */
    @Override
    public void validate(@NotNull final ActionParametersMap params, @NotNull final ErrorCollection errorCollection)
    {
        super.validate(params, errorCollection);

        final String qfTestExecutable = params.getString(QF_EXECUTABLE);
        if (StringUtils.isEmpty(qfTestExecutable) || qfTestExecutable.equals(i18nResolver.getText("turnertech.qftest.configurator.error.noExecutableField")))
        {
            errorCollection.addError(QF_EXECUTABLE, i18nResolver.getText("turnertech.qftest.configurator.error.noExecutable"));
        }
    }

    /**
     * Essentially the "save" function called by Bamboo
     */
    @Override
    public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params, @Nullable final TaskDefinition previousTaskDefinition)
    {
        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);

        config.put(QF_EXECUTABLE, params.getString(QF_EXECUTABLE));

        return config;
    }

    /**
     * Called by Bamboo to generate the UI and default values for new tasks
     */
    @Override
    public void populateContextForCreate(@NotNull final Map<String, Object> context)
    {
        super.populateContextForCreate(context);
        populateContextForCreateAndEdit(context);
        

    }

    /**
     * Called by Bamboo to generate the UI and retrieve existing values existing new tasks which we are editing
     */
    @Override
    public void populateContextForEdit(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForEdit(context, taskDefinition);
        populateContextForCreateAndEdit(context);
        
        // Populate saved values
        context.put(QF_EXECUTABLE, taskDefinition.getConfiguration().get(QF_EXECUTABLE));
    }

    /**
     * A function added by us to handle the common context elements like list values for combo boxes. This saves typing the logic twice in {@link #populateContextForCreate} and {@link #populateContextForEdit}
     * 
     * @param context
     */
    public void populateContextForCreateAndEdit(@NotNull final Map<String, Object> context)
    {
    	final List<String> qfTestExecutables = uiConfigSupport.getExecutableLabels("qftest");
        if(qfTestExecutables.isEmpty()) {
        	qfTestExecutables.add(i18nResolver.getText("turnertech.qftest.configurator.error.noExecutableField"));
        }
        context.put(QF_EXECUTABLES, qfTestExecutables);
    }
    
	/**
	 * Used by Bamboo to ask us which Builder capabilities we will require for our task. It powers the text top right in the Configurator UI which says "'X' Agents can build this task"
	 * 
	 * Note, I believe it is only called after {@link #validate} returns with no errors.
	 */
	@Override
	public Set<Requirement> calculateRequirements(TaskDefinition taskDefinition) {
		final String qfTestRuntime = taskDefinition.getConfiguration().get(QF_EXECUTABLE);
	     //Preconditions.checkState(StringUtils.isNotBlank(qfTestRuntime), i18nResolver.getText("turnertech.qftest.configurator.error.noExecutable"));
	    return Collections.singleton(new RequirementImpl(QF_CAPABILITY_PREFIX + "." + qfTestRuntime, true, ".*"));
	}

}
