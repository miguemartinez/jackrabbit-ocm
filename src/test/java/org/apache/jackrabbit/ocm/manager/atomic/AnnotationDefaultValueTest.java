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
package org.apache.jackrabbit.ocm.manager.atomic;

import javax.jcr.Node;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jackrabbit.ocm.AnnotationRepositoryTestBase;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.testmodel.Default;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test Default value assignement
 */
public class AnnotationDefaultValueTest extends AnnotationRepositoryTestBase
{
    private final static Logger log = LoggerFactory.getLogger(AnnotationDefaultValueTest.class);


    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(AnnotationDefaultValueTest.class);
    }

	public void testDefaultValues()
	{
	    try
	    {
	        ObjectContentManager ocm = getObjectContentManager();
	
	        // --------------------------------------------------------------------------------
	        // Create and store an object graph in the repository
	        // --------------------------------------------------------------------------------
	        Default a = new Default();
	        a.setPath("/testDefault");
	        a.setP1("p1Value");
	        // do not set p2, p3, p4, p5
	
	        ocm.insert(a);
	        ocm.save();
	
	
	        // --------------------------------------------------------------------------------
	        // Get the object
	        // --------------------------------------------------------------------------------
	        a = null;
	        a = (Default) ocm.getObject( "/testDefault" );
	        assertNotNull("a is null", a);
	
	        assertEquals("p1Value", a.getP1());
	        assertNull(a.getP2());
	        assertEquals("p3DescriptorDefaultValue", a.getP3());
	        assertEquals("p4DefaultValue", a.getP4());
	        assertEquals("p5DefaultValue", a.getP5());
	
	    }
	    catch (Exception e)
	    {
	        log.error("testDefaultValues failed", e);
	        fail("Exception occurs during the unit test : " + e);
	    }
	
	}
	
	
    public void testDefaultValuesRead()
    {
        try
        {
        	ObjectContentManager ocm = getObjectContentManager();

        	// --------------------------------------------------------------------------------
            // Manually create a node
        	// we need this test as SimpleFieldsHelper.storeSimpleField sets the
        	// property value if the field is not set but a jcrDefaultValue
        	// is set. But we want to test, that SimpleFieldsHelper.retrieveSimpleField
        	// sets the default value from the jcrDefaultValue
            // --------------------------------------------------------------------------------
        	Node nodeA = ocm.getSession().getRootNode().addNode("testDefault", "ocm:DefTestPrimary");
        	nodeA.setProperty("ocm:p1", "p1Value");
        	ocm.getSession().save();
        	

            // --------------------------------------------------------------------------------
            // Get the object
            // --------------------------------------------------------------------------------
            Default a = (Default) ocm.getObject( "/testDefault" );
            assertNotNull("a is null", a);

            assertEquals("p1Value", a.getP1());
            assertNull(a.getP2());
            assertEquals("p3DescriptorDefaultValue", a.getP3());
            assertEquals("p4DefaultValue", a.getP4());
            assertEquals("p5DefaultValue", a.getP5());

        }
        catch (Exception e)
        {
            log.error("testDefaultValuesRead failed", e);
            fail("Exception occurs during the unit test : " + e);
        }

    }

}