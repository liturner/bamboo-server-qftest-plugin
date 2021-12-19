[@ww.select labelKey='turnertech.qftest.configurator.qfTestVersion' 
			name='qfTestExecutable'
            list='qfTestExecutables' 
            required='false' /]
[@ww.textfield 	labelKey='turnertech.qftest.configurator.qfTestFile' 
				name="environment" 
				required='false'/]
[@ww.textfield 	label="Log name format"
				name="deploymentProject" 
				required='false'/]
[@ww.label label="Publishing the test results utilises Bamboos inbuilt 'Test' section" name="userName" /]
[@ww.checkbox label="Publish test results?" name="cbxPublishTestResults"/]