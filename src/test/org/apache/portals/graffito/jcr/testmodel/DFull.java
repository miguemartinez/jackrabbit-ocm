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


/**
 * @author <a href='mailto:the_mindstorm[at]evolva[dot]ro'>Alexandru Popescu</a>
 */
public class DFull {
    private String path;
    private String d1;
    private String b1;
    private String b2;

    /**
     * @return Returns the b1.
     */
    public String getB1() {
        return b1;
    }

    /**
     * @param b1 The b1 to set.
     */
    public void setB1(String b1) {
        this.b1 = b1;
    }

    /**
     * @return Returns the b2.
     */
    public String getB2() {
        return b2;
    }

    /**
     * @param b2 The b2 to set.
     */
    public void setB2(String b2) {
        this.b2 = b2;
    }

    /**
     * @return Returns the dString.
     */
    public String getD1() {
        return this.d1;
    }

    /**
     * @param string The dString to set.
     */
    public void setD1(String string) {
        this.d1 = string;
    }

    /**
     * @return Returns the path.
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path The path to set.
     */
    public void setPath(String path) {
        this.path= path;
    }
}