package uk.org.ukfederation.mda;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public abstract class BaseTest {

    /** Class being tested. */
    protected final Class<?> testingClass;
    
    /**
     * Base path for class-relative test resource references.
     * 
     * Will <em>not</em> end in a '/'.
     */
    private final String baseClassPath;
    
    /** Package for the class being tested. */
    private final Package testingPackage;
    
    /**
     * Base path for package-relative test resource references.
     * 
     * Will always end in a '/'.
     */
    private final String basePackagePath;
    
    /**
     * Constructor.
     * 
     * @param clazz class under test
     */
    protected BaseTest(final Class<?> clazz) {
        testingClass = clazz;
        baseClassPath = nameToPath(testingClass.getName());
        testingPackage = testingClass.getPackage();
        basePackagePath = nameToPath(testingPackage.getName()) + "/";
    }

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
    
    protected String simpleClassRelativeName(final String which) {
        return testingClass.getSimpleName() + "-" + which;
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
     * Helper method to acquire a ClasspathResource based on the given resource path.
     * 
     * Uses class-relative resource names if there is a known class under test.
     * 
     * @param resourcePath classpath path to the resource
     * @return the data file as a resource
     */
    public Resource getClasspathResource(final String resourcePath) {
        return new ClassPathResource(classRelativeResource(resourcePath).substring(1));
    }

}
