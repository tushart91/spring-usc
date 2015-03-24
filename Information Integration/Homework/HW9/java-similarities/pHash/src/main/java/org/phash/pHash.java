package org.phash;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class pHash
 {

  private native static VideoHash videoHash(String file);
  private native static AudioHash audioHash(String file);
  private native static DCTImageHash dctImageHash(String file);
  private native static MHImageHash mhImageHash(String file);
  private native static RadialImageHash radialImageHash(String file);
  private native static TextHash textHash(String file);
  public native static double imageDistance(ImageHash hash1, ImageHash hash2);
  public native static double audioDistance(AudioHash hash1, AudioHash hash2);
  public native static double videoDistance(VideoHash hash1, VideoHash hash2, int threshold);
  private native static int textDistance(TextHash txtHash1, TextHash txtHash2);
  private native static void pHashInit();

  private native static void cleanup();

  private static void checkFileExistence(File file) throws FileNotFoundException
   {
    if (!file.isFile())
     throw new FileNotFoundException("\"" + file.getAbsolutePath() + "\" is not a file.");
    if (!file.exists())
     throw new FileNotFoundException("File \"" + file.getAbsolutePath() + "\" doesn't exist.");
   }

  public static VideoHash videoHash(File file) throws FileNotFoundException
   {
    checkFileExistence(file);
    return videoHash(file.getAbsolutePath());
   }

  public static AudioHash audioHash(File file) throws FileNotFoundException
   {
    checkFileExistence(file);
    return audioHash(file.getAbsolutePath());
   }

  public static DCTImageHash dctImageHash(File file) throws FileNotFoundException
   {
    checkFileExistence(file);
    return dctImageHash(file.getAbsolutePath());
   }

  public static MHImageHash mhImageHash(File file) throws FileNotFoundException
   {
    checkFileExistence(file);
    return mhImageHash(file.getAbsolutePath());
   }

  public static RadialImageHash radialImageHash(File file) throws FileNotFoundException
   {
    checkFileExistence(file);
    return radialImageHash(file.getAbsolutePath());
   }

  static
   {
    String osName= System.getProperty("os.name");

    String libName = null;
    if(osName.contains("Windows"))
     libName = "pHash-jni.dll";
    else
     libName = "libpHash-jni.so";

    File libFile = new File("./" + libName);
    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(libName);
    if(is == null)
     throw new IllegalStateException("Current Operating System is not supported");

    OutputStream os = null;
    try
     {
      if(!libFile.exists())
       libFile.createNewFile();
      libFile.deleteOnExit();
      os = new FileOutputStream(libFile);

      byte buffer[] = new byte[10240];
      int bytesRead = 0;
      while((bytesRead = is.read(buffer)) > 0)
       os.write(buffer, 0, bytesRead);
      os.flush();
     }
    catch(Exception e) {}
    finally
    {
     try
      {
       is.close();
      }
     catch(Exception e){}
     try
      {
       os.close();
      }
     catch(Exception e){}
    }


    System.load(libFile.getAbsolutePath());
    pHashInit();

    Runtime.getRuntime().addShutdownHook(new Thread()
     {
      @Override
      public void run()
       {
        try
         {
          cleanup();
         }
        catch (Throwable e) {}
       }
     });
   }

 }
