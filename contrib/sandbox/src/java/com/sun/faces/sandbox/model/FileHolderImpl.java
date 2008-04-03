/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

/**
 * 
 */
package com.sun.faces.sandbox.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jason Lee
 *
 */
public class FileHolderImpl implements FileHolder {
    Map<String, InputStream> files = new HashMap<String, InputStream>();

    public void addFile(String fileName, InputStream is) {
        files.put(fileName, is);
    }

    public void clearFiles() {
        for (InputStream is : files.values()) {
            closeInputStream(is);
        }
        files.clear();
    }

    public InputStream getFile(String fileName) {
        return files.get(fileName);
    }
    
    public List<String> getFileNames() {
        List<String> fileNames = new ArrayList<String>();
        fileNames.addAll(files.keySet());
        return fileNames;
    }
    
    public void removeFile(String fileName) {
        closeInputStream(files.get(fileName));
        
        files.remove(fileName);
    }
    
    protected void closeInputStream (InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException ioe) {
                //  Just let it go...
            }
        }
    }
}