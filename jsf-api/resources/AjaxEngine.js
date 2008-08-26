/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * AjaxEngine contains the JavaScript for performing Ajax functions. 
 */
javax.faces.Ajax.AjaxEngine = function() {

    var req = new Object();        // Request Object
    req.url = null;                // Request URL
    req.xmlReq = null;             // XMLHttpRequest Object
    req.async = true;              // Default - Asynchronous
    req.parameters = new Object(); // Parameters For GET or POST
    req.queryString = null;        // Encoded Data For GET or POST
    req.method = null;             // GET or POST
    req.onComplete = null;         // Request/Response Complete Callback Handle
    req.onSuccess = null;          // Request/Response Success Callback Handle
    req.responseTxt = null;        // Response Content (Text)
    req.responseXML = null;        // Response Content (XML) 
    req.status = null;             // Response Status Code From Server
    req.fromQueue = false;
    req.que = new javax.faces.Ajax.AjaxEngine.Queue();

    // Get an XMLHttpRequest Handle
    req.xmlReq = javax.faces.Ajax.AjaxEngine.getTransport();
    if (req.xmlReq == null) { return null; }

    // Set up request/response state callbacks
    req.xmlReq.onreadystatechange = function() {
        if (req==null || req.xmlReq==null) { return; }
        if (req.xmlReq.readyState==1) { req.onOpenCB(req); }      // open has been called
        if (req.xmlReq.readyState==2) { req.onSendCB(req); }      // send has been called
        if (req.xmlReq.readyState==3) { req.onReceivingCB(req); } // data in process of being received from the server
        if (req.xmlReq.readyState==4) { req.onCompleteCB(req); }  // response from server has arrived
    };

    // State Callback Functions

    req.onOpenCB = function() {
    };

    req.onSendCB = function() {
    };

    req.onReceivingCB = function() {
    };

    /**
     * This function is called when the request/response interaction
     * is complete.  'onComplete', 'onSuccess' or 'onError' callbacks 
     * will be called if they have been registered,
     */
    req.onCompleteCB = function() {
        if (typeof(req.onComplete)=="function") {
            req.onComplete(req);
        }
        if ((req.xmlReq.status == null || typeof req.xmlReq.status == 'undefined') || req.xmlReq == 0 ||
            (req.xmlReq.status >= 200 && req.xmlReq.status < 300)) { 
            javax.faces.Ajax.ajaxResponse(req.xmlReq);

            var nextReq = req.que.getOldestElement();
            if (nextReq == null || typeof nextReq == 'undefined') { 
                return;
            }
            while ((typeof nextReq.xmlReq != 'undefined' && nextReq.xmlReq != null) && 
                nextReq.xmlReq.readyState == 4) {
                req.que.dequeue();
                nextReq = req.que.getOldestElement();
                if (nextReq == null || typeof nextReq == 'undefined') {
                    break;
                }
            }
            if (nextReq == null || typeof nextReq == 'undefined') { 
                return;
            }
            if ((typeof nextReq.xmlReq != 'undefined' && nextReq.xmlReq != null) && 
                nextReq.xmlReq.readyState == 0) {
                nextReq.fromQueue = true;
                nextReq.sendRequest();
            }
        } else if (typeof(req.onError)=="function") {
            req.onError(req);
        }


//        delete req.xmlReq['onreadystatechange'];
//        req.xmlReq = null;
    };

    /**
     * Utility method that accepts additional arguments for the AjaxEngine.
     * If an argument is passed in that matches an AjaxEngine property, the 
     * argument value becomes the value of the AjaxEngine property.  
     * Arguments that don't match AjaxEngine properties are added as 
     * request parameters.
     */
    req.setupArguments = function(args) {
        for (var i in args) {
            if (typeof(req[i]) == 'undefined') {
                req.parameters[i] = args[i];
            } else {
                req[i] = args[i];
            }
        }
    };

    /**
     * This function does final encoding of parameters, determines the request method
     * (GET or POST) and sends the request using the specified url.
     */ 
    req.sendRequest = function() {
        if (req.xmlReq != null) {
            // if there is already a request on the queue waiting to be processed..
            // just queue this request
            if (!req.que.isEmpty()) {
                if (!req.fromQueue) {
                    req.que.enqueue(req);
                    return;
                }
            }
            // If the queue is empty, queue up this request and send
            if (!req.fromQueue) {
                req.que.enqueue(req);
            } 
            // Some logic to get the real request URL
            if (req.generateUniqueUrl && req.method=="GET") {
                req.parameters["AjaxRequestUniqueId"] = new Date().getTime() + "" + req.requestIndex;
            }
            var content = null; // For POST requests, to hold query string
            for (var i in req.parameters) {
                if (req.queryString.length>0) { req.queryString += "&"; }
                    req.queryString += encodeURIComponent(i) + "=" + encodeURIComponent(req.parameters[i]);
            }
            if (req.method=="GET") {
                if (req.queryString.length>0) {
                    req.url += ((req.url.indexOf("?")>-1)?"&":"?") + req.queryString;
                }
            }
            req.xmlReq.open(req.method,req.url,req.async);
            if (req.method=="POST") {
                if (typeof(req.xmlReq.setRequestHeader) != 'undefined') {
                    req.xmlReq.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
                }
                content = req.queryString;
            }
            req.xmlReq.send(content);
        }
    };

    return req;
};

/**
 * Utility function to serialize form elements.
 */
javax.faces.Ajax.AjaxEngine.serializeForm = function(theform) {
    var els = theform.elements;
    var len = els.length;
    var qString = "";
    this.addField = function(name,value) {
        if (qString.length>0) {
            qString += "&";
        }
        qString += encodeURIComponent(name) + "=" + encodeURIComponent(value);
    };
    for (var i=0; i<len; i++) {
        var el = els[i];
        if (!el.disabled) {
            switch(el.type) {
                case 'text': case 'password': case 'hidden': case 'textarea':
                    this.addField(el.name,el.value);
                    break;
                case 'select-one':
                    if (el.selectedIndex>=0) {
                        this.addField(el.name,el.options[el.selectedIndex].value);
                    }
                    break;
                case 'select-multiple':
                    for (var j=0; j<el.options.length; j++) {
                        if (el.options[j].selected) {
                            this.addField(el.name,el.options[j].value);
                        }
                    }
                    break;
                case 'checkbox': case 'radio':
                    if (el.checked) {
                        this.addField(el.name,el.value);
                    }
                    break;
            }
        }
    }
    return qString;
};

/**
 * Utility function to get an XMLHttpRequest handle.
 */
javax.faces.Ajax.AjaxEngine.getTransport = function() {
    var methods = [
      function() { return new XMLHttpRequest(); },
      function() { return new ActiveXObject('Msxml2.XMLHTTP'); },
      function() { return new ActiveXObject('Microsoft.XMLHTTP'); }
    ];

    var returnVal;
    for(var i = 0, len = methods.length; i < len; i++) {
      try {
        returnVal = methods[i]();
      } catch(e) {
        continue;
      }
      return returnVal;
    }
    throw new Error('Could not create an XHR object.');
}

javax.faces.Ajax.AjaxEngine.queue = [];
var queue = javax.faces.Ajax.AjaxEngine.queue;

javax.faces.Ajax.AjaxEngine.Queue = function() {

  // the amount of space at the front of the queue, initialised to zero
  var queueSpace = 0;

  /* Returns the size of this Queue. The size of a Queue is equal to the number
   * of elements that have been enqueued minus the number of elements that have
   * been dequeued.
   */
  this.getSize = function(){

    // return the number of elements in the queue
    return queue.length - queueSpace;

  }

  /* Returns true if this Queue is empty, and false otherwise. A Queue is empty
   * if the number of elements that have been enqueued equals the number of
   * elements that have been dequeued.
   */
  this.isEmpty = function(){

    // return true if the queue is empty, and false otherwise
    return (queue.length == 0);

  }

  /* Enqueues the specified element in this Queue. The parameter is:
   *
   * element - the element to enqueue
   */
  this.enqueue = function(element){
    queue.push(element);
    var args = new Object();
    args["enqueue"] = element;
    observer.fire(args);
  }

  /* Dequeues an element from this Queue. The oldest element in this Queue is
   * removed and returned. If this Queue is empty then undefined is returned.
   */
  this.dequeue = function(){

    // initialise the element to return to be undefined
    var element = undefined;

    // check whether the queue is empty
    if (queue.length){

      // fetch the oldest element in the queue
      element = queue[queueSpace];

      // update the amount of space and check whether a shift should occur
      if (++queueSpace * 2 >= queue.length){

        // set the queue equal to the non-empty portion of the queue
        queue = queue.slice(queueSpace);

        // reset the amount of space at the front of the queue
        queueSpace=0;

      }
    }
    if (element != "undefined") {
        var args = new Object();
        args["dequeue"] = element;
        observer.fire(args);
    }

    // return the removed element
    return element;
  }

  /* Returns the oldest element in this Queue. If this Queue is empty then
   * undefined is returned. This function returns the same value as the dequeue
   * function, but does not remove the returned element from this Queue.
   */
  this.getOldestElement = function(){

    // initialise the element to return to be undefined
    var element = undefined;

    // if the queue is not element then fetch the oldest element in the queue
    if (queue.length) element = queue[queueSpace];

    // return the oldest element
    return element;

  }
}

javax.faces.Ajax.AjaxEngine.Observer = function() {
    this.fns = [];
    this.subscribe = function(fn) {
        this.fns.push(fn);
    },
    this.unsubscribe = function(fn) {
        this.fns = this.fns.filter(
            function(el) {
                if ( el !== fn ) {
                    return el;
                }
            }
        );
    },
    this.fire = function(o, thisObj) {
        var scope = thisObj || window;
        this.fns.forEach(
            function(el) {
                 el.call(scope, o);
            }
        );
    }
}

Array.prototype.forEach = function(fn, thisObj) {
    var scope = thisObj || window;
    for ( var i=0, j=this.length; i < j; ++i ) {
        fn.call(scope, this[i], i, this);
    }
}

Array.prototype.filter = function(fn, thisObj) {
    var scope = thisObj || window;
    var a = [];
    for ( var i=0, j=this.length; i < j; ++i ) {
        if ( !fn.call(scope, this[i], i, this) ) {
            continue;
        }
        a.push(this[i]);
    }
    return a;
}

var observer = new javax.faces.Ajax.AjaxEngine.Observer();


