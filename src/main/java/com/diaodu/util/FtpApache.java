//package com.diaodu.util;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//
//import org.apache.commons.net.ftp.FTP;
//import org.apache.commons.net.ftp.FTPClient;
//import org.apache.commons.net.ftp.FTPFile;
//import org.apache.commons.net.ftp.FTPReply;
//
//import junit.extensions.TestSetup;
//
//public class FtpApache {
//    private static FTPClient ftpClient = new FTPClient();
//    private static String encoding = System.getProperty("file.encoding");
//    /**
//     * Description: 向FTP服务器上传文件
//     * 
//     * @Version1.0
//     * @param url
//     *            FTP服务器hostname
//     * @param port
//     *            FTP服务器端口
//     * @param username
//     *            FTP登录账号
//     * @param password
//     *            FTP登录密码
//     * @param path
//     *            FTP服务器保存目录,如果是根目录则为“/”
//     * @param filename
//     *            上传到FTP服务器上的文件名
//     * @param input
//     *            本地文件输入流
//     * @return 成功返回true，否则返回false
//     */
//    public static boolean uploadFile(String url, int port, String username,
//            String password, String path, String filename, InputStream input) {
//        boolean result = false;
//
//        try {
//            int reply;
//            // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
//            ftpClient.connect(url);
//            // ftp.connect(url, port);// 连接FTP服务器
//            // 登录
//            ftpClient.login(username, password);
//            ftpClient.setControlEncoding(encoding);
//            // 检验是否连接成功
//            reply = ftpClient.getReplyCode();
//            if (!FTPReply.isPositiveCompletion(reply)) {
//                System.out.println("连接失败");
//                ftpClient.disconnect();
//                return result;
//            }
//
//            // 转移工作目录至指定目录下
//            boolean change = ftpClient.changeWorkingDirectory(path);
//            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
//            if (change) {
//                result = ftpClient.storeFile(new String(filename.getBytes(encoding),"iso-8859-1"), input);
//                if (result) {
//                    System.out.println("上传成功!");
//                }
//            }
//            input.close();
//            ftpClient.logout();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (ftpClient.isConnected()) {
//                try {
//                    ftpClient.disconnect();
//                } catch (IOException ioe) {
//                }
//            }
//        }
//        return result;
//    }
//
//    /**
//     * 将本地文件上传到FTP服务器上
//     * 
//     */
//    public static void testUpLoadFromDisk() {
//        try {
//            FileInputStream in = new FileInputStream(new File("/tmp/upload/mm.txt"));
//            boolean flag = uploadFile("10.201.48.26", 22, "bl","bl", "/tmp", "abc.txt", in);
//            System.out.println(flag);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    /**
//     * Description: 从FTP服务器下载文件
//     * 
//     * @Version1.0
//     * @param url
//     *            FTP服务器hostname
//     * @param port
//     *            FTP服务器端口
//     * @param username
//     *            FTP登录账号
//     * @param password
//     *            FTP登录密码
//     * @param remotePath
//     *            FTP服务器上的相对路径
//     * @param fileName
//     *            要下载的文件名
//     * @param localPath
//     *            下载后保存到本地的路径
//     * @return
//     */
//    public static boolean downFile(String url, int port, String username,
//            String password, String remotePath, String fileName,
//            String localPath) {
//        boolean result = false;
//        try {
//            int reply;
//            ftpClient.setControlEncoding(encoding);
//            
//            /*
//             *  为了上传和下载中文文件，有些地方建议使用以下两句代替
//             *  new String(remotePath.getBytes(encoding),"iso-8859-1")转码。
//             *  经过测试，通不过。
//             */
////            FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
////            conf.setServerLanguageCode("zh");
//
//            ftpClient.connect(url, port);
//            // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
//            ftpClient.login(username, password);// 登录
//            // 设置文件传输类型为二进制
//            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
//            // 获取ftp登录应答代码
//            reply = ftpClient.getReplyCode();
//            // 验证是否登陆成功
//            if (!FTPReply.isPositiveCompletion(reply)) {
//                ftpClient.disconnect();
//                System.err.println("FTP server refused connection.");
//                return result;
//            }
//            // 转移到FTP服务器目录至指定的目录下
//            ftpClient.changeWorkingDirectory(new String(remotePath.getBytes(encoding),"iso-8859-1"));
//            // 获取文件列表
//            FTPFile[] fs = ftpClient.listFiles();
//            for (FTPFile ff : fs) {
//                if (ff.getName().equals(fileName)) {
//                    File localFile = new File(localPath + "/" + ff.getName());
//                    OutputStream is = new FileOutputStream(localFile);
//                    ftpClient.retrieveFile(ff.getName(), is);
//                    is.close();
//                }
//            }
//
//            ftpClient.logout();
//            result = true;
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (ftpClient.isConnected()) {
//                try {
//                    ftpClient.disconnect();
//                } catch (IOException ioe) {
//                }
//            }
//        }
//        return result;
//    }
//
//    /**
//     * 将FTP服务器上文件下载到本地
//     * 
//     */
//    public void testDownFile() {
//        try {
//            boolean flag = downFile("127.0.0.1", 21, "zlb",
//                    "123", "/", "哈哈.txt", "D:/");
//            System.out.println(flag);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    
//    public static void main(String[] args) {
//    	testUpLoadFromDisk();
//    }
//}