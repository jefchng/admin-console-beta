/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 **/
package org.codice.ddf.admin.beta;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.vmOption;
import static org.ops4j.pax.exam.CoreOptions.when;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.debugConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.replaceConfigurationFile;
import static org.osgi.framework.FrameworkUtil.getBundle;

import static junit.framework.TestCase.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import javax.inject.Inject;


import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.security.policy.context.ContextPolicyManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class AppTest {

    @Configuration
    public Option[] config() {
        return combineOptions(
                distributionSettings(),
                featuresToAddToFeatureRepo(),
                systemBundles(),
                bootFeatures(),
                configurableSettings()
        );
    }
    public Option[] distributionSettings() {
        return new Option[] {
                // KarafDistributionOption.debugConfiguration("5005", true),
//                replaceConfigurationFile("etc/custom.properties",  new File("target/test-classes/custom.properties")),
                debugConfiguration("5005", Boolean.getBoolean("isDebugEnabled")),
                karafDistributionConfiguration()
                        .frameworkUrl(maven()
                                .groupId("org.apache.karaf")
                                .artifactId("apache-karaf")
                                .version("4.0.7")
                                .type("tar.gz"))
                        .unpackDirectory(new File("target/exam"))
                        .useDeployFolder(false)
        };
    }

    public Option[] featuresToAddToFeatureRepo() {
        return new Option[] {
//                features(maven().groupId("org.codice.ddf")
//                        .artifactId("kernel")
//                        .type("xml")
//                        .classifier("features")
//                        .version("2.10.2")),
//
//                features(maven().groupId("ddf.platform")
//                        .artifactId("platform-app")
//                        .type("xml")
//                        .classifier("features")
//                        .version("2.10.2"))
        };
    }

    public Option[] systemBundles() {
        return new Option[] {
//                bootClasspathLibrary(
//                        mavenBundle().groupId("org.bouncycastle")
//                        .artifactId("bcprov-jdk15on")
//                        .version("1.54")).beforeFramework(),
//                bootClasspathLibrary(
//                        mavenBundle().groupId("org.bouncycastle")
//                                .artifactId("bcmail-jdk15on")
//                                .version("1.54")).beforeFramework(),
//                bootClasspathLibrary(
//                        mavenBundle().groupId("org.bouncycastle")
//                                .artifactId("bcpkix-jdk15on")
//                                .version("1.54")).beforeFramework()
        };
    }

    public Option[] bootFeatures() {
        return new Option[] {
                features(maven().groupId("org.codice.ddf.admin.beta")
                        .artifactId("admin-itest-dependencies")
                        .type("xml")
                        .classifier("features")
                        .version("0.1.3-SNAPSHOT"), "admin-app")
//                ,
//                 features(
//                        maven().groupId("org.codice.ddf.admin.beta")
//                                .artifactId("admin-query-app")
//                                .type("xml")
//                                .classifier("features")
//                                .version("0.1.3-SNAPSHOT"), "graphql-dependencies", "admin-beta-temp-configurator")
        };
    }

    public Option[] configurableSettings() {
        return new Option[] {
                //http://stackoverflow.com/questions/35316570/pax-exam-copy-a-jar-in-karaf-lib-ext-folder/35332081
                keepRuntimeFolder(),
                logLevel(LogLevelOption.LogLevel.INFO),
                editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiRegistryPort", "20001"),
                editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiServerPort", "20002")
        };
    }

    public Option[] combineOptions(Option[]... options) {
        return Arrays.stream(options).filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .toArray(Option[]::new);
    }

    @Inject
    private ConfigurationAdmin configAdmin;

    @Test
    public void getHelloService() throws IOException {
        ContextPolicyManager contextPolicyManager = getService(ContextPolicyManager.class);
        org.osgi.service.cm.Configuration policyManagerConfig = getServiceConfig("org.codice.ddf.security.policy.context.impl.PolicyManager");

        if(contextPolicyManager.getAllContextPolicies().isEmpty() || policyManagerConfig == null) {
            fail("Failed to retrieve context policy manager reference or service properties.");
        }
        System.out.println("Hello world!");
    }

    public org.osgi.service.cm.Configuration getServiceConfig(String pid) {
        try {
            return configAdmin.getConfiguration(pid);
        } catch (Exception e) {
            fail("Unable to retrieve service configuration for: " + pid);
        }
        return null;
    }

    public<T> T getService(Class<T> clazz)  {
        return getBundleContext().getService(getBundleContext().getServiceReference(clazz));
    }

    public BundleContext getBundleContext() {
        return FrameworkUtil.getBundle(this.getClass()).getBundleContext();
    }
}
