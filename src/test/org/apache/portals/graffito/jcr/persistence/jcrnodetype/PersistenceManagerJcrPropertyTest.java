/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.portals.graffito.jcr.persistence.jcrnodetype;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.graffito.jcr.RepositoryLifecycleTestSetup;
import org.apache.portals.graffito.jcr.TestBase;
import org.apache.portals.graffito.jcr.persistence.PersistenceManager;
import org.apache.portals.graffito.jcr.testmodel.PropertyTest;

/**
 * Test inheritance with node type per concrete class (without  discreminator field)
 *
 * @author <a href="mailto:christophe.lombart@gmail.com">Christophe Lombart</a>
 */
public class PersistenceManagerJcrPropertyTest extends TestBase {
	private final static Log log = LogFactory.getLog(PersistenceManagerJcrPropertyTest.class);

	/**
	 * <p>Defines the test case name for junit.</p>
	 * @param testName The test case name.
	 */
	public PersistenceManagerJcrPropertyTest(String testName) throws Exception {
		super(testName);

	}

	public static Test suite() {
		// All methods starting with "test" will be executed in the test suite.
		return new RepositoryLifecycleTestSetup(new TestSuite(PersistenceManagerJcrPropertyTest.class));
	}

	public void tearDown() throws Exception {

		cleanUpRepisotory();
		super.tearDown();
		
	}


	public void testRequiredProperty() 
	{

		try 
		{
			PersistenceManager persistenceManager = this.getPersistenceManager();
			//---------------------------------------------------------------------------------------------------------
			// Insert without the mandatory field
			//---------------------------------------------------------------------------------------------------------			
			
            PropertyTest propertyTest = new PropertyTest();
            propertyTest.setPath("/test");
            propertyTest.setRequiredProp("requiredPropValue");
            propertyTest.setRequiredWithConstraintsProp("abc");
            
            try 
            {
                 persistenceManager.insert(propertyTest);
                 fail("Incorrect insert operation - the mandatory fields have no value");
            }
            catch(Exception e)
            {
               // Normal behaviour 	
            	persistenceManager.refresh(false);
            }
            
			//---------------------------------------------------------------------------------------------------------
			// Insert with the mandatory fields
			//---------------------------------------------------------------------------------------------------------			
            propertyTest.setMandatoryProp("mandatoryValue");
            propertyTest.setMandatoryWithConstaintsProp("xx");
            persistenceManager.insert(propertyTest);
            persistenceManager.save();
            
			//---------------------------------------------------------------------------------------------------------
			// Retrieve
			//---------------------------------------------------------------------------------------------------------			
            propertyTest = (PropertyTest) persistenceManager.getObject("/test");
            assertTrue("Invalid required property", propertyTest.getRequiredProp().equals("requiredPropValue"));
            assertTrue("Invalid required property with constraints", propertyTest.getRequiredWithConstraintsProp().equals("abc"));            
            assertTrue("Invalid autocreated property", propertyTest.getAutoCreatedProp().equals("aaa")); 
            assertTrue("Invalid autocreated property", propertyTest.getAutoCreatedWithConstraintsProp().equals("ccc"));
            assertTrue("Invalid protected property", propertyTest.getProtectedWithDefaultValueProp().equals("protectedValue"));
            
            //---------------------------------------------------------------------------------------------------------
			// update the property requiredWithConstraintsProp with bad value
			//---------------------------------------------------------------------------------------------------------			
            propertyTest = (PropertyTest) persistenceManager.getObject("/test");
            propertyTest.setRequiredWithConstraintsProp("invalid value");
            try 
            {
            	persistenceManager.update(propertyTest);
            	persistenceManager.save();
            	fail("Invalid value was accepted for requiredWithConstraintsProp");
            }
            catch(Exception e)
            {                	
               // Do nothing - normal behaviour, the value               	
            }
            
            //---------------------------------------------------------------------------------------------------------
			// update the property AutoCreatedWithConstraintsProp with bad value
			//---------------------------------------------------------------------------------------------------------			
            propertyTest = (PropertyTest) persistenceManager.getObject("/test");
            propertyTest.setAutoCreatedWithConstraintsProp("invalid value");
            try 
            {
            	persistenceManager.update(propertyTest);
            	persistenceManager.save();
            	fail("Invalid value was accepted for autoCreatedWithConstraintsProp ");
            }
            catch(Exception e)
            {             	
               // Do nothing - normal behaviour, the value is not valid
               	
            }
            
            //---------------------------------------------------------------------------------------------------------
			// update the property mandatoryWithConstaintsProp with bad value
			//---------------------------------------------------------------------------------------------------------			
            propertyTest = (PropertyTest) persistenceManager.getObject("/test");
            propertyTest.setMandatoryWithConstaintsProp("yy");
            try 
            {
            	persistenceManager.update(propertyTest);
            	persistenceManager.save();
            	fail("Invalid value was accepted for mandatoryWithConstaintsProp");
            }
            catch(Exception e)
            {                	
                e.printStackTrace();
            }            
			
			
		}
		catch (Exception e)
		{			
			e.printStackTrace();
			fail();
		}
			
	}
	
	
	
}