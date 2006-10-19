/*
 * Copyright 2000-2005 The Apache Software Foundation.
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
package org.apache.portals.graffito.jcr.persistence.collectionconverter.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;

import org.apache.portals.graffito.jcr.exception.PersistenceException;
import org.apache.portals.graffito.jcr.mapper.Mapper;
import org.apache.portals.graffito.jcr.mapper.model.CollectionDescriptor;
import org.apache.portals.graffito.jcr.persistence.collectionconverter.ManageableCollection;
import org.apache.portals.graffito.jcr.persistence.collectionconverter.ManageableCollectionUtil;
import org.apache.portals.graffito.jcr.persistence.collectionconverter.impl.AbstractCollectionConverterImpl;
import org.apache.portals.graffito.jcr.persistence.objectconverter.ObjectConverter;

/**
 * The <code>ResidualNodesCollectionConverterImpl</code> is a collection
 * converter for multiple child nodes accessed through Node.getNodes(String
 * pattern).
 * 
 * @author <a href="mailto:fmeschbe[at]apache[dot]com">Felix Meschberger</a>
 */
public class ResidualNodesCollectionConverterImpl extends
        AbstractCollectionConverterImpl {

    
    /**
     * Constructor
     *
     * @param atomicTypeConverters
     * @param objectConverter
     * @param mapper
     */
    public ResidualNodesCollectionConverterImpl(Map atomicTypeConverters,
        ObjectConverter objectConverter, Mapper mapper) {
        super(atomicTypeConverters, objectConverter, mapper);
    }

    /**
     *
     * @see AbstractCollectionConverterImpl#doInsertCollection(Session, Node, CollectionDescriptor, ManageableCollection)
     */
    protected void doInsertCollection(Session session, Node parentNode,
        CollectionDescriptor collectionDescriptor,
        ManageableCollection collection) /* throws PersistenceException */ {

        if (!(collection instanceof Map)) { 
            return;
        }
        
        Map map = (Map) collection;
        for (Iterator ei=map.entrySet().iterator(); ei.hasNext(); ) {
            Map.Entry entry = (Map.Entry) ei.next();
            String name = String.valueOf(entry.getKey());
            objectConverter.insert(session, parentNode, name, entry.getValue());
        }
    }

    /**
     *
     * @see AbstractCollectionConverterImpl#doUpdateCollection(Session, Node, CollectionDescriptor, ManageableCollection)
     */
    protected void doUpdateCollection(Session session, Node parentNode,
            CollectionDescriptor collectionDescriptor,
            ManageableCollection collection) throws RepositoryException {

        String jcrName = getCollectionJcrName(collectionDescriptor);
        if (!(collection instanceof Map)) {
            for (NodeIterator ni=parentNode.getNodes(jcrName); ni.hasNext(); ) {
                ni.nextNode().remove();
            }
            return;
        }

        Map map = (Map) collection;
        Set updatedItems = new HashSet();
        for (Iterator ei=map.entrySet().iterator(); ei.hasNext(); ) {
            Map.Entry entry = (Map.Entry) ei.next();
            String elementJcrName = String.valueOf(entry.getKey());
            Object item = entry.getValue();

            // Update existing JCR Nodes
            if (parentNode.hasNode(elementJcrName)) {
                objectConverter.update(session, parentNode, elementJcrName, item);
            }
            else {
                // Add new collection elements
                objectConverter.insert(session, parentNode, elementJcrName, item);
            }

            updatedItems.add(elementJcrName);
        }

        // Delete JCR nodes that are not present in the collection
        NodeIterator nodeIterator = parentNode.getNodes(jcrName);
        List removeNodes = new ArrayList();
        while (nodeIterator.hasNext()) {
            Node child = nodeIterator.nextNode();
            if (!updatedItems.contains(child.getName())) {
                removeNodes.add(child);
            }
        }
        for(int i = 0; i < removeNodes.size(); i++) {
            ((Node) removeNodes.get(i)).remove();
        }
    }

    /**
     * @see AbstractCollectionConverterImpl#doGetCollection(Session, Node, CollectionDescriptor, Class)
     */
    protected ManageableCollection doGetCollection(Session session,
        Node parentNode, CollectionDescriptor collectionDescriptor,
        Class collectionFieldClass) throws RepositoryException {
        
        try {
            String jcrName = getCollectionJcrName(collectionDescriptor);
            NodeIterator ni = parentNode.getNodes(jcrName);
            if (!ni.hasNext()) {
                return null;
            }

            ManageableCollection collection = ManageableCollectionUtil.getManageableCollection(collectionFieldClass);
            while (ni.hasNext()) {
                Node node = ni.nextNode();

                // ignore protected nodes here
                if (node.getDefinition().isProtected()) {
                    continue;
                }

                Object item = objectConverter.getObject(session, node.getPath());
                if (collection instanceof Map) {
                    String name = node.getName();
                    ((Map) collection).put(name, item);
                } else {
                    collection.addObject(item);
                }
            }

            return collection;
        } catch (ValueFormatException vfe) {
            throw new PersistenceException("Cannot get the collection field : "
                + collectionDescriptor.getFieldName() + "for class "
                + collectionDescriptor.getClassDescriptor().getClassName(), vfe);
        }
    }

    /**
     * @see AbstractCollectionConverterImpl#doIsNull(Session, Node, CollectionDescriptor, Class)
     */
    protected boolean doIsNull(Session session, Node parentNode,
            CollectionDescriptor collectionDescriptor, Class collectionFieldClass)
            throws RepositoryException {
        String jcrName = getCollectionJcrName(collectionDescriptor);
        return (parentNode == null || !parentNode.getNodes(jcrName).hasNext());
    }
}