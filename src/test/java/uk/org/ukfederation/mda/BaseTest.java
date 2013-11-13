package uk.org.ukfederation.mda;

import java.net.URL;

import net.shibboleth.utilities.java.support.resource.ClasspathResource;
import net.shibboleth.utilities.java.support.resource.Resource;

public abstract class BaseTest {

    /** Class being tested. */
    protected Class<?> testingClass;
    
    /**
     * Base path for class-relative test resource references.
     * 
     * Will <em>not<em> end in a '/'.
     */
    private String baseClassPath;
    
    /** Package for the class being tested. */
    private Package testingPackage;
    
    /**
     * Base path for package-relative test resource references.
     * 
     * Will always end in a '/'.
     */
    private String basePackagePath;
    
    /**
     * Converts the "."-separated name of a class or package into an
     * absolute path.
     * 
     * @param name name to be converted
     * @return path to resources associated with the name
     */
    private String nameToPath(final String name) {
        return "/" + name.replace('.', '/');
    }
    
    /**
     * Sets the class being tested, so that references can be made to testing resources
     * relative to it.
     * 
     * @param clazz class being tested
     */
    protected void setTestingClass(final Class<?> clazz) {
        testingClass = clazz;
        baseClassPath = nameToPath(testingClass.getName());
        testingPackage = testingClass.getPackage();
        basePackagePath = nameToPath(testingPackage.getName()) + "/";
    }
    
    /**
     * Makes a resource reference relative to the class being tested.
     * 
     * The convention adopted is that the class-relative name is something
     * like "foo.pem", and that this is expanded to "/a/b/c/Bar-foo.pem".
     * 
     * @param which class-relative resource name
     * @return absolute resource name
     */
    protected String classRelativeResource(final String which) {
        return baseClassPath + "-" + which;
    }
    
    /**
     * Makes a resource reference relative to the package of the class being tested.
     * 
     * The convention adopted is that the package-relative name is something
     * like "foo.pem", and that this is expanded to "/a/b/c/foo.pem".
     * 
     * @param which package-relative resource name
     * @return absolute resource name
     */
    protected String packageRelativeResource(final String which) {
        return basePackagePath + which;
    }
    
    /**
     * Variant of ClasspathResource that patches round the problem described
     * in JSPT-21.
     */
    private class FixedClasspathResource extends ClasspathResource {
    
        /**
         * Constructor.
         *
         * @param resourcePath classpath path to the resource
         */
        public FixedClasspathResource(final String resourcePath) {
            super(resourcePath);
            // Work around the fact that ClasspathResource doesn't handle location correctly
            final URL resourceURL = this.getClass().getClassLoader().getResource(resourcePath);
            setLocation(resourceURL.toExternalForm());
        }
        
    }
    
    /**
     * Helper method to acquire a ClasspathResource based on the given resource path.
     * 
     * Uses class-relative resource names if there is a known class under test.
     * 
     * @param resourcePath classpath path to the resource
     * @return the data file as a resource
     */
    public Resource getClasspathResource(final String resourcePath) {
        if (testingClass != null) {
            return new FixedClasspathResource(classRelativeResource(resourcePath).substring(1));
        } else {
            return new FixedClasspathResource(resourcePath);
        }
    }

}
