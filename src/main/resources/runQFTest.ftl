[@ww.select labelKey='tt.qftest.executable' name='qfTestExecutable' list='qfTestExecutables' required='true' /]
            
[@ww.textfield 	labelKey='tt.qftest.file' name="qfTestFile"	required='true'/]
				
[@ui.bambooSection title='Logging' collapsible=true isCollapsed=true]
	[@ww.textfield 	labelKey='tt.qftest.logs' name="logOutputFolder" cssClass="long-field" required='true'/]			
	[@ww.textfield 	labelKey="tt.qftest.logName" name="logNameFormat" cssClass="long-field" required='false'/]
[/@ui.bambooSection]

[@ui.bambooSection title='Variables' collapsible=true isCollapsed=!(qfVariables?has_content)]
	[@ww.textarea 	labelKey='tt.qftest.variables' name="qfVariables" cssClass="textarea" required='false'/]			
[/@ui.bambooSection]

[@ui.bambooSection titleKey='repository.advanced.option' collapsible=true isCollapsed=!(environmentVariables?has_content || workingSubDirectory?has_content)]
    [@s.textfield labelKey='builder.common.env' name='environmentVariables' cssClass="long-field" /]
    [@s.textfield labelKey='builder.common.sub' name='workingSubDirectory' cssClass="long-field" /]
[/@ui.bambooSection]