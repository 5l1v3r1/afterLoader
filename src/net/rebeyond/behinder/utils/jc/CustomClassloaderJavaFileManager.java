 package net.rebeyond.behinder.utils.jc;
 
 import java.io.IOException;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 import javax.tools.FileObject;
 import javax.tools.JavaFileManager.Location;
 import javax.tools.JavaFileObject;
 import javax.tools.JavaFileObject.Kind;
 import javax.tools.StandardJavaFileManager;
 import javax.tools.StandardLocation;
 
 public class CustomClassloaderJavaFileManager implements javax.tools.JavaFileManager
 {
   private final ClassLoader classLoader;
   private final StandardJavaFileManager standardFileManager;
   private final PackageInternalsFinder finder;
   public static Map<String, JavaFileObject> fileObjects = new ConcurrentHashMap();
   
   public CustomClassloaderJavaFileManager(ClassLoader classLoader, StandardJavaFileManager standardFileManager) {
     this.classLoader = classLoader;
     this.standardFileManager = standardFileManager;
     this.finder = new PackageInternalsFinder(classLoader);
   }
   
 
   public ClassLoader getClassLoader(JavaFileManager.Location location)
   {
     return this.standardFileManager.getClassLoader(location);
   }
   
 
   public String inferBinaryName(JavaFileManager.Location location, JavaFileObject file)
   {
     if ((file instanceof CustomJavaFileObject)) {
       return ((CustomJavaFileObject)file).binaryName();
     }
     return this.standardFileManager.inferBinaryName(location, file);
   }
   
 
   public boolean isSameFile(FileObject a, FileObject b)
   {
     return this.standardFileManager.isSameFile(a, b);
   }
   
   public boolean handleOption(String current, Iterator<String> remaining)
   {
     return this.standardFileManager.handleOption(current, remaining);
   }
   
   public boolean hasLocation(JavaFileManager.Location location)
   {
     return (location == StandardLocation.CLASS_PATH) || (location == StandardLocation.PLATFORM_CLASS_PATH);
   }
   
   public JavaFileObject getJavaFileForInput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind) throws IOException
   {
     JavaFileObject javaFileObject = (JavaFileObject)fileObjects.get(className);
     if (javaFileObject == null) {
       this.standardFileManager.getJavaFileForInput(location, className, kind);
     }
     return javaFileObject;
   }
   
   public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException
   {
     JavaFileObject javaFileObject = new MyJavaFileObject(className, kind);
     fileObjects.put(className, javaFileObject);
     return javaFileObject;
   }
   
   public FileObject getFileForInput(JavaFileManager.Location location, String packageName, String relativeName) throws IOException
   {
     return this.standardFileManager.getFileForInput(location, packageName, relativeName);
   }
   
   public FileObject getFileForOutput(JavaFileManager.Location location, String packageName, String relativeName, FileObject sibling) throws IOException
   {
     return this.standardFileManager.getFileForOutput(location, packageName, relativeName, sibling);
   }
   
   public void flush()
     throws IOException
   {
     this.standardFileManager.flush();
   }
   
   public void close()
     throws IOException
   {
     this.standardFileManager.close();
   }
   
   public Iterable<JavaFileObject> list(JavaFileManager.Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse)
     throws IOException
   {
     if (location == StandardLocation.PLATFORM_CLASS_PATH)
     {
       return this.standardFileManager.list(location, packageName, kinds, recurse); }
     if ((location == StandardLocation.CLASS_PATH) && (kinds.contains(JavaFileObject.Kind.CLASS))) {
       if (packageName.startsWith("java."))
       {
         return this.standardFileManager.list(location, packageName, kinds, recurse);
       }
       
       return this.finder.find(packageName);
     }
     
     return java.util.Collections.emptyList();
   }
   
 
   public int isSupportedOption(String option)
   {
     return -1;
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/net/rebeyond/behinder/utils/jc/CustomClassloaderJavaFileManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */