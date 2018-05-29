package Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class AppUtils {
	
	public static   String formatPath(String path){
		  if (path != null && !"".equals(path)) {
				path = path.replace('/', File.separatorChar);
				path = path.replace('\\', File.separatorChar);
				while (path.endsWith(String.valueOf(File.separatorChar))) {
					path = path.substring(0, path.length() - 1);
				}
			} else {
				return "";
			}
			return path;
		}
	
	 public static boolean isNullOrEmpty(String str){
	   	  if( str!=null&&!"".equals(str.trim()))
	   		  return false;
	   	  return true;
   }
	 
	 public static String getCurrentDate(String form){
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat(form);
			String staticsDate= format.format(calendar.getTime());
			return staticsDate;
		} 
	 
	 
	 
	 public  static void getFileList(String path,List<VFile> fileList){
	 		File startFile=new File(path);
			if(startFile.isDirectory()){
				collectRFile(startFile, AppContext.DEFAULT_PATH, fileList);
			}else{
				VFile ufile=new VFile(startFile.getAbsolutePath(), AppContext.DEFAULT_PATH, startFile.length(), startFile.lastModified());
				fileList.add(ufile);
			}
	 	}
	 
	 private static void collectRFile(File startFile,String currentPath,List<VFile> fileList){
			if(startFile.isDirectory()){
				String path=currentPath+File.separatorChar+startFile.getName();
				File[] filelist=startFile.listFiles();
				if(filelist!=null&&filelist.length>0){
					for(File file:filelist)
						collectRFile(file, path, fileList);
				}
			}else{
				VFile ufile=new VFile(startFile.getAbsolutePath(), currentPath, startFile.length(), startFile.lastModified());
				fileList.add(ufile);
			}
	 }
	
	 public static String getMd5ByPath(String filePath){
		  FileInputStream fis = null;
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				File file=new File(filePath);
					if(file!=null&&file.isFile()){
						fis = new FileInputStream(file);
						FileChannel fChannel = fis.getChannel();
						ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 1024);
						int length = -1;
						while ((length = fChannel.read(buffer)) != -1) {
							buffer.flip();
							md.update(buffer);
							buffer.compact();
						}
						byte[] b = md.digest();
						return byteToHexString(b);
				}else{
					return "";
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} finally {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	  }
	 
	  private static char hexdigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8','9', 'a', 'b', 'c', 'd', 'e', 'f' };
	  private static String byteToHexString(byte[] tmp) {
			String s;// ���ֽڱ�ʾ���� 16 ���ֽ�
			char str[] = new char[16 * 2]; // ÿ���ֽ��� 16 ���Ʊ�ʾ�Ļ���ʹ�������ַ���
			// ���Ա�ʾ�� 16 ������Ҫ 32 ���ַ�
			int k = 0; // ��ʾת������ж�Ӧ���ַ�λ��
			for (int i = 0; i < 16; i++) { // �ӵ�һ���ֽڿ�ʼ���� MD5 ��ÿһ���ֽ� ת���� 16 �����ַ���ת��
				byte byte0 = tmp[i]; // ȡ�� i ���ֽ�
				str[k++] = hexdigits[byte0 >>> 4 & 0xf]; // ȡ�ֽ��и� 4 λ������ת��, >>> Ϊ�߼����ƣ�������λһ������
				str[k++] = hexdigits[byte0 & 0xf]; // ȡ�ֽ��е� 4 λ������ת��
			}
			s = new String(str); // ����Ľ��ת��Ϊ�ַ���
			return s;
		}
}
