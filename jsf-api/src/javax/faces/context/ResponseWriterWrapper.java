/*
 * $Id: ResponseWriterWrapper.java,v 1.6 2007/04/27 22:00:06 ofung Exp $
 */

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

package javax.faces.context;

import javax.faces.component.UIComponent;

import java.io.IOException;
import java.io.Writer;
import javax.faces.FacesWrapper;

/**
 * <p>Provides a simple implementation of {@link ResponseWriter} that
 * can be subclassed by developers wishing to provide specialized
 * behavior to an existing {@link ResponseWriter} instance.  The default
 * implementation of all methods is to call through to the wrapped
 * {@link ResponseWriter}.</p>
 *
 * <p>Usage: extend this class and override {@link #getWrapped} to
 * return the instance we are wrapping.</p>
 *
 * @since 1.2
 */
public abstract class ResponseWriterWrapper extends ResponseWriter implements FacesWrapper<ResponseWriter> {


    /**
     * @return the instance that we are wrapping.
     */ 

    public abstract ResponseWriter getWrapped();

    // -------------------------- Methods from javax.faces.contxt.ResponseWriter


    /**
     * <p>The default behavior of this method is to
     * call {@link ResponseWriter#getContentType()}
     * on the wrapped {@link ResponseWriter} object.</p>
     *
     * @see ResponseWriter#getContentType()
     * @since 1.2
     */
    public String getContentType() {

        return getWrapped().getContentType();

    }


    /**
     * <p>The default behavior of this method is to
     * call {@link ResponseWriter#getCharacterEncoding()}
     * on the wrapped {@link ResponseWriter} object.</p>
     *
     * @see ResponseWriter#getCharacterEncoding()
     * @since 1.2
     */
    public String getCharacterEncoding() {

        return getWrapped().getCharacterEncoding();

    }


    /**
     * <p>The default behavior of this method is to
     * call {@link ResponseWriter#flush()}
     * on the wrapped {@link ResponseWriter} object.</p>
     *
     * @see ResponseWriter#flush()
     * @since 1.2
     */
    public void flush() throws IOException {

        getWrapped().flush();

    }


    /**
     * <p>The default behavior of this method is to
     * call {@link ResponseWriter#startDocument()}
     * on the wrapped {@link ResponseWriter} object.</p>
     *
     * @see ResponseWriter#startDocument()
     * @since 1.2
     */
    public void startDocument() throws IOException {

        getWrapped().startDocument();

    }


    /**
     * <p>The default behavior of this method is to
     * call {@link ResponseWriter#endDocument()}
     * on the wrapped {@link ResponseWriter} object.</p>
     *
     * @see ResponseWriter#endDocument()
     * @since 1.2
     */
    public void endDocument() throws IOException {

        getWrapped().endDocument();

    }


    /**
     * <p>The default behavior of this method is to
     * call {@link ResponseWriter#startElement(String, javax.faces.component.UIComponent)}
     * on the wrapped {@link ResponseWriter} object.</p>
     *
     * @see ResponseWriter#startElement(String, javax.faces.component.UIComponent)
     * @since 1.2
     */
    public void startElement(String name, UIComponent component)
    throws IOException {

        getWrapped().startElement(name, component);

    }


    /**
     * <p>The default behavior of this method is to
     * call {@link ResponseWriter#endElement(String)}
     * on the wrapped {@link ResponseWriter} object.</p>
     *
     * @see ResponseWriter#endElement(String)
     * @since 1.2
     */
    public void endElement(String name) throws IOException {

        getWrapped().endElement(name);

    }


    /**
     * <p>The default behavior of this method is to
     * call {@link ResponseWriter#writeAttribute(String, Object, String)}
     * on the wrapped {@link ResponseWriter} object.</p>
     *
     * @see ResponseWriter#writeAttribute(String, Object, String)
     * @since 1.2
     */
    public void writeAttribute(String name, Object value, String property)
        throws IOException {

        getWrapped().writeAttribute(name, value, property);

    }


    /**
     * <p>The default behavior of this method is to
     * call {@link ResponseWriter#writeURIAttribute(String, Object, String)}
     * on the wrapped {@link ResponseWriter} object.</p>
     *
     * @see ResponseWriter#writeURIAttribute(String, Object, String)
     * @since 1.2
     */
    public void writeURIAttribute(String name, Object value, String property)
        throws IOException {

        getWrapped().writeURIAttribute(name, value, property);

    }


    /**
     * <p>The default behavior of this method is to
     * call {@link ResponseWriter#writeComment(Object)}
     * on the wrapped {@link ResponseWriter} object.</p>
     *
     * @see ResponseWriter#writeComment(Object)
     * @since 1.2
     */
    public void writeComment(Object comment) throws IOException {

        getWrapped().writeComment(comment);

    }


    /**
     * <p>The default behavior of this method is to
     * call {@link ResponseWriter#writeText(Object, String)}
     * on the wrapped {@link ResponseWriter} object.</p>
     *
     * @see ResponseWriter#writeText(Object, String)
     * @since 1.2
     */
    public void writeText(Object text, String property) throws IOException {

        getWrapped().writeText(text, property);

    }

    /**
     * <p>The default behavior of this method is to
     * call {@link ResponseWriter#writeText(Object, UIComponent, String)}
     * on the wrapped {@link ResponseWriter} object.</p>
     *
     * @see ResponseWriter#writeText(Object, String)
     * @since 1.2
     */

    public void writeText(Object text, UIComponent component, String property)
        throws IOException {
	getWrapped().writeText(text, component, property);
    }



    /**
     * <p>The default behavior of this method is to
     * call {@link ResponseWriter#writeText(char[], int, int)}
     * on the wrapped {@link ResponseWriter} object.</p>
     *
     * @see ResponseWriter#writeText(char[], int, int)
     * @since 1.2
     */
    public void writeText(char[] text, int off, int len) throws IOException {

        getWrapped().writeText(text, off, len);

    }


    /**
     * <p>The default behavior of this method is to
     * call {@link ResponseWriter#cloneWithWriter(java.io.Writer)}
     * on the wrapped {@link ResponseWriter} object.</p>
     *
     * @see ResponseWriter#cloneWithWriter(java.io.Writer)
     * @since 1.2
     */
    public ResponseWriter cloneWithWriter(Writer writer) {

        return getWrapped().cloneWithWriter(writer);

    }


    // --------------------------------------------- Methods from java.io.Writer


    /**
     * <p>The default behavior of this method is to
     * call {@link ResponseWriter#close()}
     * on the wrapped {@link ResponseWriter} object.</p>
     *
     * @see ResponseWriter#close()
     * @since 1.2
     */
    public void close() throws IOException {

        getWrapped().close();

    }


    /**
     * <p>The default behavior of this method is to
     * call {@link ResponseWriter#write(char[], int, int)}
     * on the wrapped {@link ResponseWriter} object.</p>
     *
     * @see ResponseWriter#write(char[], int, int)
     * @since 1.2
     */
    public void write(char[] cbuf, int off, int len) throws IOException {

        getWrapped().write(cbuf, off, len);

    }

}
