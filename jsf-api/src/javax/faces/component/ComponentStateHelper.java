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


package javax.faces.component;

import javax.faces.context.FacesContext;
import static javax.faces.component.UIComponentBase.saveAttachedState;
import static javax.faces.component.UIComponentBase.restoreAttachedState;
import javax.el.ValueExpression;
import java.util.*;
import java.io.Serializable;

/**A base implementation for
 * maps which implement the PartialStateHolder interface.
 *
 * This can be used as a base-class for all
 * state-holder implementations in components,
 * converters and validators and other implementations
 * of the StateHolder interface.
 */
@SuppressWarnings({"unchecked"})
class ComponentStateHelper implements StateHelper {

    private UIComponent component;
    private boolean isTransient;
    private Map<Serializable, Object> deltaMap;
    private Map<Serializable, Object> defaultMap;

    // ------------------------------------------------------------ Constructors


    public ComponentStateHelper(UIComponent component) {

        this.component = component;
        this.deltaMap = new HashMap<Serializable,Object>();
        this.defaultMap = new HashMap<Serializable,Object>();
        
    }


    // ----------------------------------------- Methods from PartialStateHolder


    /**Put the object in the main-map
     * and/or the delta-map, if necessary.
     *
     * @param key
     * @param value
     * @return the original value in the delta-map, if not present, the old value in the main map
     */
    public Object put(Serializable key, Object value) {

        if(component.initialStateMarked() || value instanceof PartialStateHolder) {
            Object retVal = deltaMap.put(key, value);

            if(retVal==null) {
                return defaultMap.put(key,value);
            }
            else {
                defaultMap.put(key,value);
                return retVal;
            }
        }
        else {
            return defaultMap.put(key,value);
        }
    }


    /**We need to remove from both
     * maps, if we do remove an existing key.
     *
     * @param key
     * @return the removed object in the delta-map. if not present, the removed object from the main map
     */
    public Object remove(Serializable key) {
        if(component.initialStateMarked()) {
            Object retVal = deltaMap.remove(key);

            if(retVal==null) {
                return defaultMap.remove(key);
            }
            else {
                defaultMap.remove(key);
                return retVal;
            }
        }
        else {
            return defaultMap.remove(key);
        }
    }


    public Object put(Serializable key, String mapKey, Object value) {

        Object ret = null;
        if (component.initialStateMarked() || value instanceof PartialStateHolder) {
            Map<String,Object> dMap = (Map<String,Object>) deltaMap.get(key);
            if (dMap == null) {
                dMap = new HashMap<String,Object>(5);
                deltaMap.put(key, dMap);
            }
            ret = dMap.put(mapKey, value);

        }
        Map<String,Object> map = (Map<String,Object>) get(key);
        if (map == null) {
            map = new HashMap<String,Object>(8);
            defaultMap.put(key, map);
        }
        if (ret == null) {
            return map.put(mapKey, value);
        } else {
            map.put(mapKey, value);
            return ret;
        }

    }

    /**Get the object from the main-map.
     * As everything is written through
     * from the delta-map to the main-map, this
     * should be enough.
     *
     * @param key
     * @return
     */
    public Object get(Serializable key) {
        return defaultMap.get(key);
    }

    public Object eval(Serializable key) {
        return eval(key, null);
    }

    /**
     * Docs
     * @param key
     * @param defaultValue value to return if key is not present in main or delta-map
     * @return value - if null, defaultValue
     */
    public Object eval(Serializable key, Object defaultValue) {
        Object retVal = get(key);
        if (retVal == null) {
            ValueExpression ve = component.getValueExpression(key.toString());
            if (ve != null) {
                retVal = ve.getValue(component.getFacesContext().getELContext());
            }

        }

        return ((retVal != null) ? retVal : defaultValue);
    }

    public void add(Serializable key, Object value) {

        if (component.initialStateMarked() || value instanceof PartialStateHolder) {
            List<Object> deltaList = (List<Object>) deltaMap.get(key);
            if (deltaList == null) {
                deltaList = new ArrayList<Object>(4);
                deltaMap.put(key, deltaList);
            }
            deltaList.add(value);
        }
        List<Object> items = (List<Object>) get(key);
        if (items == null) {
            items = new ArrayList<Object>(4);
            defaultMap.put(key, items);
        }
        items.add(value);

    }


    public Object remove(Serializable key, Object valueOrKey) {
        Object source = get(key);
        if (source instanceof Collection) {
            return removeFromList(key, valueOrKey);
        } else if (source instanceof Map) {
            return removeFromMap(key, valueOrKey.toString());
        }
        return null;
    }

    private Object removeFromList(Serializable key, Object value) {
        Object ret = null;
        if (component.initialStateMarked() || value instanceof PartialStateHolder) {
            Collection<Object> deltaList = (Collection<Object>) deltaMap.get(key);
            if (deltaList != null) {
                ret = deltaList.remove(value);
                if (deltaList.isEmpty()) {
                    deltaMap.remove(key);
                }
            }
        }
        Collection<Object> list = (Collection<Object>) get(key);
        if (list != null) {
            if (ret == null) {
                ret = list.remove(value);
            } else {
                list.remove(value);
            }
            if (list.isEmpty()) {
                defaultMap.remove(key);
            }
        }
        return ret;
    }

    private Object removeFromMap(Serializable key, String mapKey) {
        Object ret = null;
        if (component.initialStateMarked()) {
            Map<String,Object> dMap = (Map<String,Object>) deltaMap.get(key);
            if (dMap != null) {
                ret = dMap.remove(mapKey);
                if (dMap.isEmpty()) {
                    deltaMap.remove(key);
                }
            }
        }
        Map<String,Object> map = (Map<String,Object>) get(key);
        if (map != null) {
            if (ret == null) {
                ret = map.remove(mapKey);
            } else {
                map.remove(mapKey);

            }
            if (map.isEmpty()) {
                defaultMap.remove(key);
            }
        }
        if (ret != null && !component.initialStateMarked()) {
            deltaMap.remove(key);
        }
        return ret;
    }


    /**One and only implementation of
     * save-state - makes all other implementations
     * unnecessary.
     *
     * @param context
     * @return the saved state
     */
    public Object saveState(FacesContext context) {
        if(component.initialStateMarked()) {
            return saveMap(context, deltaMap);
        }
        else {
            return saveMap(context, defaultMap);
        }
    }

    private Object saveMap(FacesContext context, Map<Serializable, Object> map) {

        if (map.isEmpty()) {
            if (!component.initialStateMarked()) {
                // only need to propagate the component's delta status when
                // delta tracking has been disabled.  We're assuming that
                // the PDL will reset the status when the view is reconstructed,
                // so no need to save the state if the saved state is the default.
                return new Object[] { component.initialStateMarked() };
            }
            return null;
        }

        Object[] savedState = new Object[map.size() * 2 + 1];

        int i=0;

        for(Map.Entry<Serializable, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value == null) {
                value = Void.TYPE;
            }
            savedState[i * 2] = entry.getKey();
            if (value instanceof Collection) {
                value = saveAttachedState(context,value);
            }
            savedState[i * 2 + 1] = value instanceof Serializable?value:saveAttachedState(context, value);
            i++;
        }
        if (!component.initialStateMarked()) {
            savedState[savedState.length - 1] = component.initialStateMarked();
        }
        return savedState;

    }

    /**One and only implementation of
     * restore state. Makes all other implementations
     * unnecessary.
     *
     * @param context FacesContext
     * @param state the state to be restored.
     */
    public void restoreState(FacesContext context, Object state) {

        if (state == null) {
            return;
        }
        Object[] savedState = (Object[]) state;
        if (savedState[savedState.length - 1] != null) {
            component.initialState = (Boolean) savedState[savedState.length - 1];
        }
        int length = (savedState.length-1)/2;
        for (int i = 0; i < length; i++) {
           Object value = savedState[i * 2 + 1];
           if (Void.TYPE.equals(value)) {
               value = null;
           }
            Serializable serializable = (Serializable) savedState[i * 2];
            if (value != null) {
                if (value instanceof Collection) {
                    value = restoreAttachedState(context, value);
                } else if (value instanceof StateHolderSaver) {
                    value = ((StateHolderSaver) value).restore(context);
                } else {
                    value = (value instanceof Serializable
                             ? value
                             : restoreAttachedState(context, value));
                }
            }
            if (value instanceof Map) {
                for (Map.Entry<String, Object> entry : ((Map<String, Object>) value)
                      .entrySet()) {
                    this.put(serializable, entry.getKey(), entry.getValue());
                }
            } else if (value instanceof List) {
                for (Object o : ((List<Object>) value)) {
                    this.add(serializable, o);
                }
            } else {
                put(serializable, value);
            }
        }
    }

    /**TODO decide if this is can just return false
     *
     * @return
     */
    public boolean isTransient() {
        return isTransient;
    }

    /**TODO decide if this should be a NO-OP
     *
     * @param newTransientValue
     */
    public void setTransient(boolean newTransientValue) {
        isTransient = newTransientValue;
    }
}
