/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.portals.graffito.jcr.persistence.beanconverter;



import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.portals.graffito.jcr.RepositoryLifecycleTestSetup;
import org.apache.portals.graffito.jcr.TestBase;
import org.apache.portals.graffito.jcr.mapper.model.BeanDescriptor;
import org.apache.portals.graffito.jcr.persistence.objectconverter.ObjectConverter;
import org.apache.portals.graffito.jcr.persistence.objectconverter.impl.ObjectConverterImpl;
import org.apache.portals.graffito.jcr.testmodel.B;
import org.apache.portals.graffito.jcr.testmodel.D;
import org.apache.portals.graffito.jcr.testmodel.DFull;
import org.apache.portals.graffito.jcr.testmodel.E;

/**
 * ObjectConverter test for bean-descriptor with inner bean inlined and inner bean with
 * custom converter.
 * 
 * @author <a href='mailto:the_mindstorm[at]evolva[dot]ro'>Alexandru Popescu</a>
 */
public class BeanDescriptorTest extends TestBase {
    private ObjectConverter objectConverter;
    
    public BeanDescriptorTest(String testname) {
        super(testname);
    }

    public static Test suite() {

        // All methods starting with "test" will be executed in the test suite.
        return new RepositoryLifecycleTestSetup(new TestSuite(BeanDescriptorTest.class));
    }
    
    
    /**
     * @see org.apache.portals.graffito.jcr.TestBase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        this.objectConverter = new ObjectConverterImpl(this.mapper, this.converterProvider);

        clean();
    }

    
    /**
     * @see org.apache.portals.graffito.jcr.TestBase#tearDown()
     */
    public void tearDown() throws Exception {
        clean();
        super.tearDown();
    }

    private void clean() throws Exception {
        if(getSession().itemExists("/someD")) {
            getSession().getItem("/someD").remove();
            getSession().save();
        }
    }
    
    public void testInlined() throws Exception {
        System.out.println("inlined");
        
        B expB = new B();
        expB.setB1("b1value");
        expB.setB2("b2value");
        D expD = new D();
        expD.setPath("/someD");
        expD.setD1("d1value");
        expD.setB1(expB);
        
       persistenceManager.insert( expD);
       persistenceManager.save();
        
        D actD = (D) persistenceManager.getObject( "/someD");
        
        assertEquals(expD.getD1(), actD.getD1());
        assertEquals(expB.getB1(), actD.getB1().getB1());
        assertEquals(expB.getB2(), actD.getB1().getB2());
        
        DFull actDFull = (DFull) persistenceManager.getObject( DFull.class,  "/someD");
        
        assertEquals(expD.getD1(), actDFull.getD1());
        assertEquals(expB.getB1(), actDFull.getB1());
        assertEquals(expB.getB2(), actDFull.getB2());
        
        expB.setB1("updatedvalue1");
        
        persistenceManager.update( expD);
        getSession().save();
        
        actD = (D) persistenceManager.getObject( "/someD");
        
        assertEquals(expD.getD1(), actD.getD1());
        assertEquals(expB.getB1(), actD.getB1().getB1());
        assertEquals(expB.getB2(), actD.getB1().getB2());
        
        actDFull = (DFull) persistenceManager.getObject( DFull.class,  "/someD");
        
        assertEquals(expD.getD1(), actDFull.getD1());
        assertEquals(expB.getB1(), actDFull.getB1());
        assertEquals(expB.getB2(), actDFull.getB2());
        
            
        expD.setB1(null);
        persistenceManager.update( expD);
        getSession().save();
        
        actD = (D) persistenceManager.getObject(  "/someD");
        
        assertEquals(expD.getD1(), actD.getD1());
        assertNull("b1 was not  removed", actD.getB1());
        
        actDFull = (DFull) persistenceManager.getObject( DFull.class,  "/someD");
        
        assertEquals(expD.getD1(), actDFull.getD1());
        assertNull("b1 was not  removed", actDFull.getB1());
        assertNull("b2 wan not remove", actDFull.getB2());

    }
    
    
    public void testBeanDescriptorConverter() throws Exception 
    {
        
        B expB = new B();
        expB.setB1("b1value");
        expB.setB2("b2value");
        E expE = new E();
        expE.setPath("/someD");
        expE.setD1("d1value");
        expE.setB1(expB);
        
        
        persistenceManager.insert( expE);
        persistenceManager.save();
       
        E actE = (E) persistenceManager.getObject( "/someD");
       
        assertEquals(expE.getD1(), actE.getD1());
        
        expE.setD1("updatedvalueD1");
        expB.setB1("updatedvalue1");
        
        persistenceManager.update( expE);
        persistenceManager.save();
               
        actE = (E) persistenceManager.getObject(  "/someD");
        
        assertEquals(expE.getD1(), actE.getD1());
                        
        expE.setB1(null);
        persistenceManager.update( expE);
        persistenceManager.save();
        
        actE = (E) persistenceManager.getObject(  "/someD");
        
        assertEquals(expE.getD1(), actE.getD1());        
        
   
        List messages = FakeBeanConverter.getLog();
        assertEquals(6, messages.size());
        assertEquals("insert at path /someD", messages.get(0));
        assertEquals("get from path /someD", messages.get(1));
        assertEquals("update at path /someD", messages.get(2));
        assertEquals("get from path /someD", messages.get(3));
        assertEquals("remove from path /someD", messages.get(4));
        assertEquals("get from path /someD", messages.get(5));

    }
    
}
