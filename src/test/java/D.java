/**
 * Created by gongp on 2017/12/4.
 */



import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by kursulla on 4/25/14.
 * <p/>
 * Class purpose:
 */
public class D {

    public static final int BUFFER_SIZE = 1024;
    private static final String TAG = "ZipUtil";

    public static void main(String[] args) {
//        unZip("C:\\Users\\gongp\\Desktop\\入职相关\\社保入职.zip","C:\\Users\\gongp\\Desktop\\入职相关222");
        long l = System.currentTimeMillis();
        listFile("E:\\zookeeper-3.3.6");
        System.out.println((System.currentTimeMillis()-l));

    }



    //迭代目录
    //深度遍历


    public static void listFile(String path){
        File p= new File(path);
//        System.out.println(p.getAbsolutePath()+"----------");
        if(p.isDirectory()){
            String[] list = p.list();
            for(int i=0;i<list.length;i++){
//                System.out.println(list[i]);
                String subFile=p.getAbsolutePath()+File.separator+list[i];
                if(new File(subFile).isDirectory()){
                    listFile(subFile);
                }else{
                    System.out.println(subFile);
                }
            }
        }
    }









    public static File unZip(String pathToZip, String pathToOutputDir) {
        byte[] buffer = new byte[1024];
        File outputDirectory = null;
        try {

            outputDirectory = new File(pathToOutputDir);
            if (!outputDirectory.exists()) {
                outputDirectory.mkdir();
            }

            ZipInputStream zis = new ZipInputStream(new FileInputStream(pathToZip));
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(pathToOutputDir + File.separator + fileName);

                System.out.println("file unzip : " + newFile.getAbsoluteFile());

                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

            System.out.println("Done");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return outputDirectory;
    }

}
