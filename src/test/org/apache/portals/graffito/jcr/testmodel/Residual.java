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
package org.apache.portals.graffito.jcr.testmodel;

import org.apache.portals.graffito.jcr.persistence.collectionconverter.impl.ManagedHashMap;

/**
 *
 * @author <a href="mailto:fmeschbe[at]apache[dot]com">Felix Meschberger</a>
 * 
 */
public class Residual
{
	private String path;
    private ManagedHashMap elements;
    
    public static class ResidualProperties extends Residual {}
    public static class ResidualNodes extends Residual {}
    
    protected Residual() {}
    
    
    public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	/**
     * @return Returns the elements.
     */
    public ManagedHashMap getElements()
    {
        return elements;
    }
    /**
     * @param elements The elements to set.
     */
    public void setElements(ManagedHashMap elements)
    {
        this.elements = elements;
    }
}