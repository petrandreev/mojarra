/*
 * $Id: FacesConfigRuleSet.java,v 1.5 2004/04/30 14:32:04 eburns Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.faces.config.rules;


import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <p>Digester <code>RuleSet</code> for parsing a JavaServer Faces
 * configuration file.  Varying levels of detail may be requested by
 * inclusion of the boolean flags passed to our constructor.  In all
 * cases, the basic structural rules will be included.</p>
 */

public class FacesConfigRuleSet extends RuleSetBase {


    // ------------------------------------------------------------- Constructor


    /**
     * <p>Construct a new instance that will incorporate rules for the profiles
     * identified by our parameters.</p>
     *
     * @param design Include rules suitable for design time use in a tool
     * @param generate Include rules suitable for generating component,
     *  renderer, and tag classes
     * @param runtime Include rules suitable for runtime execution of an
     *  application
     */
    public FacesConfigRuleSet(boolean design, boolean generate,
                              boolean runtime) {

        this.design = design;
        this.generate = generate;
        this.runtime = runtime;

    }


    // ------------------------------------------------------ Instance Variables


    private static final Log log = LogFactory.getLog(FacesConfigRuleSet.class);

    private boolean design = false;
    private boolean generate = false;
    private boolean runtime = false;


    // ---------------------------------------------------------- Public Methods


    /**
     * <p>Add the set of Rule instances defined in this RuleSet to the
     * specified <code>Digester</code> instance.</p>
     *
     * @param digester Digester instance for adding Rules
     */
    public void addRuleInstances(Digester digester) {

        // faces-config
        digester.addRule
            ("faces-config", new FacesConfigRule());

        // faces-config/application
        if (runtime) {
            digester.addRule
                ("faces-config/application", new ApplicationRule());
            digester.addCallMethod
                ("faces-config/application/action-listener",
                 "addActionListener", 0);
            digester.addRule
                ("faces-config/application/locale-config",
                 new LocaleConfigRule());
            digester.addCallMethod
                ("faces-config/application/locale-config/default-locale",
                 "setDefaultLocale", 0);
            digester.addCallMethod
                ("faces-config/application/locale-config/supported-locale",
                 "addSupportedLocale", 0);
            digester.addCallMethod
                ("faces-config/application/message-bundle",
                 "setMessageBundle", 0);
            digester.addCallMethod
                ("faces-config/application/navigation-handler",
                 "addNavigationHandler", 0);
            digester.addCallMethod
                ("faces-config/application/property-resolver",
                 "addPropertyResolver", 0);
            digester.addCallMethod
                ("faces-config/application/state-manager",
                 "addStateManager", 0);
            digester.addCallMethod
                ("faces-config/application/variable-resolver",
                 "addVariableResolver", 0);
            digester.addCallMethod
                ("faces-config/application/view-handler",
                 "addViewHandler", 0);
            digester.addCallMethod
                ("faces-config/application/default-render-kit-id",
                 "setDefaultRenderKitId", 0);
        }

        // faces-config/component
        digester.addRule
            ("faces-config/component", new ComponentRule());
        digester.addCallMethod
            ("faces-config/component/component-class",
             "setComponentClass", 0);
        digester.addCallMethod
            ("faces-config/component/component-type",
             "setComponentType", 0);
        if (design || generate) {
            addAttributeRules("faces-config/component", digester);
            addFeatureRules("faces-config/component", digester);
            addPropertyRules("faces-config/component", digester);
            digester.addCallMethod
                ("faces-config/component/component-extension/base-component-type",
                 "setBaseComponentType", 0);
            digester.addCallMethod
                ("faces-config/component/component-extension/renderer-type",
                 "setRendererType", 0);
            digester.addCallMethod
                ("faces-config/component/component-extension/component-family",
                 "setComponentFamily", 0);
        }

        // faces-config/converter
        if (design || runtime) {
            digester.addRule
                ("faces-config/converter", new ConverterRule());
            digester.addCallMethod
                ("faces-config/converter/converter-class",
                 "setConverterClass", 0);
            digester.addCallMethod
                ("faces-config/converter/converter-for-class",
                 "setConverterForClass", 0);
            digester.addCallMethod
                ("faces-config/converter/converter-id",
                 "setConverterId", 0);
            if (design) {
                addAttributeRules("faces-config/converter", digester);
                addFeatureRules("faces-config/converter", digester);
                addPropertyRules("faces-config/converter", digester);
            }
        }

        // faces-config/factory
        if (runtime) {
            digester.addRule
                ("faces-config/factory", new FactoryRule());
            digester.addCallMethod
                ("faces-config/factory/application-factory",
                 "addApplicationFactory", 0);
            digester.addCallMethod
                ("faces-config/factory/faces-context-factory",
                 "addFacesContextFactory", 0);
            digester.addCallMethod
                ("faces-config/factory/lifecycle-factory",
                 "addLifecycleFactory", 0);
            digester.addCallMethod
                ("faces-config/factory/render-kit-factory",
                 "addRenderKitFactory", 0);
        }

        // faces-config/lifecycle
        if (runtime) {
            digester.addRule
                ("faces-config/lifecycle", new LifecycleRule());
            digester.addCallMethod
                ("faces-config/lifecycle/phase-listener",
                 "addPhaseListener", 0);
        }

        // faces-config/managed-bean
        if (design || runtime) {
            digester.addRule
                ("faces-config/managed-bean", new ManagedBeanRule());
            digester.addCallMethod
                ("faces-config/managed-bean/managed-bean-class",
                 "setManagedBeanClass", 0);
            digester.addCallMethod
                ("faces-config/managed-bean/managed-bean-name",
                 "setManagedBeanName", 0);
            digester.addCallMethod
                ("faces-config/managed-bean/managed-bean-scope",
                 "setManagedBeanScope", 0);
            addFeatureRules("faces-config/managed-bean", digester);
            addListEntriesRules("faces-config/managed-bean", digester);
            addMapEntriesRules("faces-config/managed-bean", digester);
        }

        // faces-config/managed-bean/managed-property
        if (design || runtime) {
            digester.addRule
                ("faces-config/managed-bean/managed-property",
                 new ManagedPropertyRule());
            digester.addCallMethod
                ("faces-config/managed-bean/managed-property/property-class",
                 "setPropertyClass", 0);
            digester.addCallMethod
                ("faces-config/managed-bean/managed-property/property-name",
                 "setPropertyName", 0);
            digester.addRule
                ("faces-config/managed-bean/managed-property/null-value",
                 new NullValueRule());
            digester.addCallMethod
                ("faces-config/managed-bean/managed-property/value",
                 "setValue", 0);
            addFeatureRules("faces-config/managed-bean/managed-property",
                            digester);
            addListEntriesRules("faces-config/managed-bean/managed-property",
                                digester);
            addMapEntriesRules("faces-config/managed-bean/managed-property",
                               digester);
        }

        // faces-config/navigation-rule
        if (design || runtime) {
            digester.addRule
                ("faces-config/navigation-rule", new NavigationRuleRule());
            digester.addCallMethod
                ("faces-config/navigation-rule/from-view-id",
                 "setFromViewId", 0);
            addFeatureRules("faces-config/navigation-rule", digester);
        }

        // faces-config/navigation-rule/navigation-case
        if (design || runtime) {
            digester.addRule
                ("faces-config/navigation-rule/navigation-case",
                 new NavigationCaseRule());
            digester.addCallMethod
                ("faces-config/navigation-rule/navigation-case/from-action",
                 "setFromAction", 0);
            digester.addCallMethod
                ("faces-config/navigation-rule/navigation-case/from-outcome",
                 "setFromOutcome", 0);
            digester.addCallMethod
                ("faces-config/navigation-rule/navigation-case/redirect",
                 "setRedirectTrue", 0);
            digester.addCallMethod
                ("faces-config/navigation-rule/navigation-case/to-view-id",
                 "setToViewId", 0);
            addFeatureRules("faces-config/navigation-rule/navigation-case",
                            digester);
        }

        // faces-config/referenced-bean
        if (design) {
            digester.addRule
                ("faces-config/referenced-bean", new ReferencedBeanRule());
            digester.addCallMethod
                ("faces-config/referenced-bean/referenced-bean-class",
                 "setReferencedBeanClass", 0);
            digester.addCallMethod
                ("faces-config/referenced-bean/referenced-bean-name",
                 "setReferencedBeanName", 0);
            addFeatureRules("faces-config/referenced-bean", digester);
        }

        // faces-config/render-kit
        digester.addRule
            ("faces-config/render-kit", new RenderKitRule());
        digester.addCallMethod
            ("faces-config/render-kit/render-kit-class",
             "setRenderKitClass", 0);
        digester.addCallMethod
            ("faces-config/render-kit/render-kit-id",
             "setRenderKitId", 0);
        if (design || generate) {
            addFeatureRules("faces-config/render-kit", digester);
        }

        // faces-config/render-kit/renderer
        digester.addRule
            ("faces-config/render-kit/renderer", new RendererRule());
        digester.addCallMethod
            ("faces-config/render-kit/renderer/component-family",
             "setComponentFamily", 0);
        digester.addCallMethod
            ("faces-config/render-kit/renderer/renderer-class",
             "setRendererClass", 0);
        digester.addCallMethod
            ("faces-config/render-kit/renderer/renderer-type",
             "setRendererType", 0);
        if (design || generate) {
            addAttributeRules("faces-config/render-kit/renderer", digester);
            addFeatureRules("faces-config/render-kit/renderer", digester);
            digester.addCallMethod
                ("faces-config/render-kit/renderer/renderer-extension/renders-children",
                 "setRendersChildren", 0, new String[] { "java.lang.Boolean" });
            digester.addCallMethod
                ("faces-config/render-kit/renderer/renderer-extension/exclude-attributes",
                 "setExcludeAttributes", 0);

	    digester.addCallMethod
		("faces-config/render-kit/renderer/renderer-extension/tag-name",
		 "setTagName", 0);
        }

        // faces-config/validator
        if (design || runtime) {
            digester.addRule
                ("faces-config/validator", new ValidatorRule());
            digester.addCallMethod
                ("faces-config/validator/validator-class",
                 "setValidatorClass", 0);
            digester.addCallMethod
                ("faces-config/validator/validator-id",
                 "setValidatorId", 0);
            if (design) {
                addAttributeRules("faces-config/validator", digester);
                addFeatureRules("faces-config/validator", digester);
                addPropertyRules("faces-config/validator", digester);
            }
        }

    }


    // --------------------------------------------------------- Private Methods


    private void addAttributeRules(String prefix, Digester digester) {

        digester.addRule
            (prefix + "/attribute",
             new AttributeRule());
        addFeatureRules(prefix + "/attribute", digester);
        digester.addCallMethod
            (prefix + "/attribute/attribute-name",
             "setAttributeName", 0);
        digester.addCallMethod
            (prefix + "/attribute/attribute-class",
             "setAttributeClass", 0);
        digester.addCallMethod
            (prefix + "/attribute/suggested-value",
             "setSuggestedValue", 0);
        digester.addCallMethod
            (prefix + "/attribute/attribute-extension/default-value",
             "setDefaultValue", 0);
        digester.addCallMethod
            (prefix + "/attribute/attribute-extension/pass-through",
             "setPassThrough", 0, new String[] { "java.lang.Boolean" });
        digester.addCallMethod
            (prefix + "/attribute/attribute-extension/required",
             "setRequired", 0, new String[] { "java.lang.Boolean" });
        digester.addCallMethod
            (prefix + "/attribute/attribute-extension/read-only",
             "setReadOnly", 0, new String[] { "java.lang.Boolean" });
        digester.addCallMethod
            (prefix + "/attribute/attribute-extension/tag-attribute",
             "setTagAttribute", 0, new String[] { "java.lang.Boolean" });
    }


    private void addFeatureRules(String prefix, Digester digester) {

        digester.addRule
            (prefix + "/description",
             new DescriptionRule());
        try {
            digester.addRule
                (prefix + "/description",
                 new DescriptionTextRule());
        } catch (Exception e) {
            log.error("Cannot configure DescriptionTextRule for pattern '" +
                      prefix + "/description" + "'", e);
            throw new IllegalStateException
                ("Cannot configure DescriptionTextRule for pattern '" +
                 prefix + "/description" + "'");
        }
        digester.addRule
            (prefix + "/display-name",
             new DisplayNameRule());
        digester.addRule
            (prefix + "/icon",
             new IconRule());
        digester.addCallMethod
            (prefix + "/icon/large-icon",
             "setLargeIcon", 0);
        digester.addCallMethod
            (prefix + "/icon/small-icon",
             "setSmallIcon", 0);

    }


    private void addListEntriesRules(String prefix, Digester digester) {

        digester.addRule
            (prefix + "/list-entries",
             new ListEntriesRule());
        digester.addCallMethod
            (prefix + "/list-entries/null-value",
             "addNullValue"); // No arguments
        digester.addCallMethod
            (prefix + "/list-entries/value",
             "addValue", 0);
        digester.addCallMethod
            (prefix + "/list-entries/value-class",
             "setValueClass", 0);

    }


    private void addMapEntriesRules(String prefix, Digester digester) {

        digester.addRule
            (prefix + "/map-entries",
             new MapEntriesRule());
        digester.addCallMethod
            (prefix + "/map-entries/key-class",
             "setKeyClass", 0);
        digester.addCallMethod
            (prefix + "/map-entries/value-class",
             "setValueClass", 0);

        digester.addRule
            (prefix + "/map-entries/map-entry",
             new MapEntryRule());
        digester.addCallMethod
            (prefix + "/map-entries/map-entry/key",
             "setKey", 0);
        digester.addRule
            (prefix + "/map-entries/map-entry/null-value",
             new NullValueRule()); // No arguments
        digester.addCallMethod
            (prefix + "/map-entries/map-entry/value",
             "setValue", 0);

    }


    private void addPropertyRules(String prefix, Digester digester) {

        digester.addRule
            (prefix + "/property",
             new PropertyRule());
        addFeatureRules(prefix + "/property", digester);
        digester.addCallMethod
            (prefix + "/property/property-name",
             "setPropertyName", 0);
        digester.addCallMethod
            (prefix + "/property/property-class",
             "setPropertyClass", 0);
        digester.addCallMethod
            (prefix + "/property/suggested-value",
             "setSuggestedValue", 0);
        digester.addCallMethod
            (prefix + "/property/property-extension/default-value",
             "setDefaultValue", 0);
        digester.addCallMethod
            (prefix + "/property/property-extension/pass-through",
             "setPassThrough", 0, new String[] { "java.lang.Boolean" });
        digester.addCallMethod
            (prefix + "/property/property-extension/read-only",
             "setReadOnly", 0, new String[] { "java.lang.Boolean" });
        digester.addCallMethod
            (prefix + "/property/property-extension/required",
             "setRequired", 0, new String[] { "java.lang.Boolean" });
        digester.addCallMethod
            (prefix + "/property/property-extension/tag-attribute",
             "setTagAttribute", 0, new String[] { "java.lang.Boolean" });

    }


}
