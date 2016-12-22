package com.ncedu.mustakhimov;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

import java.nio.file.*;

/**
������������ ��� �������� ������ �� url � ����,<br/>  ����������� � �������� ����������
��� � �������� ���������� 
@author ���������� ����
@version 1.0
*/

public class Download {
	
	/**
	�������� ������ ���� �� URL 
	@param adress - url (��� http://)
	@param filePathString - ���� � �������� ������������ �����
	@param openOption - ��������� �������� ����� ����� ��������
	*/
	public void downloadWithURL(String adress, String filePathString, String openOption) throws IOException{
		URL url = new URL("http://" + adress);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		    //�������� ������ � �����
			InputStream in;
			try {
				in = new BufferedInputStream(urlConnection.getInputStream());
				readAndWriteToFileStream(in,adress,filePathString, openOption);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				urlConnection.disconnect();
			}
		
	}
	
	/**
	�������������� ������ ������ � ������, �������� �����<br/> � �������� � ���� ������ 
	@param in - ����� ������ ����������� � �����
	@param adress - url
	@param filePathString - ���� � �������� ������������ �����
	@param openOption - ��������� �������� ����� ����� ��������
	*/
	private void readAndWriteToFileStream(InputStream in, String adress, String filePathString, String openOption) throws IOException {
		// TODO Auto-generated method stub
		byte[] contents = new byte[1024];
		int bytesRead = 0;
		String strFileContents = "";
		String pathOfFile = "";
		String pathHome = System.getProperty("user.dir");
		String option = "";
		Path path = Paths.get(filePathString);
		if (Files.exists(path) && Files.isDirectory(path)) {
		  // ���� ���������� � ��� �������
			pathOfFile = filePathString +"\\"+ nameOfFile(adress);
		}
		if (Files.exists(path) && Files.isRegularFile(path)) {
	      // ���� ���������� � ��� ������� ����
			byte bKbd[] = new byte[256];
			int iCnt = 0;
			    System.out.println("1 - �������� ����");
			    System.out.println("2 - �������� ���");
			    System.out.print("����: ");
			    try {
					iCnt = System.in.read(bKbd);
				} catch (IOException e) {
					e.printStackTrace();
				}
			    option = new String(bKbd, 0, iCnt);
	            option = (String)option.trim();
	            System.out.println(option);
	            Pattern p3 = Pattern.compile("(\\\\[a-z.]+)$");  
	   		    Matcher m3 = p3.matcher(filePathString); 		    
	            pathOfFile = m3.replaceAll("\\\\" + nameOfFile(adress));
	    }
		if (Files.notExists(path)) {
		  // ���� �� ����������, ��������� � ��������  ����������
			Pattern p1 = Pattern.compile("\\\\");  
			Matcher m1 = p1.matcher(filePathString);
			Pattern p2 = Pattern.compile(":");  
			Matcher m2 = p2.matcher(m1.replaceAll("_")); 
			pathOfFile = pathHome + "\\" + m2.replaceAll("") + ".html";
			filePathString = System.getProperty("user.dir");
		}
		
		while((bytesRead = in.read(contents)) != -1) { 
		    strFileContents += new String(contents, 0, bytesRead);
		}
        //�������� ����������� � ������ ������ � html ���������
		strFileContents = downloadAndChangeImage(strFileContents, filePathString);
		switch(option){
	        case "2":
	        	//��������������� ������������ �����
	        	System.out.print(pathOfFile+"=========1");
	        	File file = new File(filePathString);
	        	file.renameTo(new File(pathOfFile));
	        	if(openOption.trim().equals("true")){
			    	openFile(pathOfFile);
			    }
	        break;
	        default:
	        	//�������� � ����
	        	System.out.println(pathOfFile+"========2");
	    	BufferedOutputStream bufferedOutput = new BufferedOutputStream(new FileOutputStream(
		    		 pathOfFile));
		    bufferedOutput.write(strFileContents.getBytes());
		    bufferedOutput.close();
		    //��������� ���� ���� openOption == true
		    if(openOption.trim().equals("true")){
		    	openFile(pathOfFile);
		    }
	    }
	     
	}
	/**
	������� ����� ����� �� url
	@param adress - url (��� http://)
	@return ���������� ��� ����� 
	*/
	private String nameOfFile(String adress){
		Pattern p = Pattern.compile("(/[a-z]+)$");  
		Matcher m = p.matcher(adress);
		if(m.find()) {
			return adress.substring(m.start()+1, m.end()) + ".html";
		}
		 p = Pattern.compile("/[a-z]+/?");  
		 m = p.matcher(adress);
		if(m.find()) {
			return adress.substring(m.start()+1, m.end()-1) + ".html";
	    }
		 p = Pattern.compile("(/[a-z]+/)$");  
         m = p.matcher(adress);
		if(m.find()){
		    return adress.substring(m.start()+1, m.end()-1) + ".html";
		}
		
		return "index.html";
	}
	/**
	������� ����� ����������� �� url
	@param adress - url
	@return ���������� ��� ����������� 
	*/
	private String nameOfImage(String adress){
		Pattern p = Pattern.compile("/[^/]+.png");  
		Matcher m = p.matcher(adress);
		if(m.find()) {
			return adress.substring(m.start()+1, m.end());
		}
		p = Pattern.compile("/[^/]+.jpg");  
		m = p.matcher(adress);
		if(m.find()) {
			return adress.substring(m.start()+1, m.end());
	    }
		 p = Pattern.compile("/[^/]+.gif");  
         m = p.matcher(adress);
		if(m.find()){
			return adress.substring(m.start()+1, m.end());
		}
		
		return "image.jpg";
	}
	/**
	������� �������� ����� 
	@param pathFile - ���� � ����� 
	*/
	private void openFile(String pathFile){
		try {
		      Desktop desktop = null;
		      if (Desktop.isDesktopSupported()) {
		        desktop = Desktop.getDesktop();
		      }
		       desktop.open(new File(pathFile));
		    } catch (IOException ioe) {
		      ioe.printStackTrace();
		    }
	}
	/**
	������� � �������� ����������� �� html ���������
	@param strFileContents - ������ � ����� � ���� �����
	@param filePathString - ���� � ������ ����� 
	*/
	private String downloadAndChangeImage(String strFileContents, String filePathString){
		Pattern pi = Pattern.compile("<img[^>]+>");  
		Matcher mi = pi.matcher(strFileContents);
		String result = strFileContents;
		String newImageTeg = null;
		String pathFiles = filePathString + "\\files";
		File Catalog = new File(pathFiles);//��������� ���� ��� ����� � �������������
		Catalog.mkdirs();                  //������� ����� ��� �����������
		String nameIm = null;
		BufferedImage image = null;
		while(mi.find()) {
			String urlImage = "";
			Pattern ps = Pattern.compile("\"[^\"]+\"");  
			Matcher ms = ps.matcher(strFileContents.substring(mi.start(),mi.end()));

			if(ms.find()){
				urlImage = strFileContents.substring(mi.start(),mi.end()).substring(ms.start()+1, ms.end()-1);
				nameIm = nameOfImage(urlImage);
				try {
					URL url = new URL(urlImage);
					image = ImageIO.read(url);
					ImageIO.write(image, "jpg",new File(pathFiles + "\\"+ nameIm));

				} 
				catch (MalformedURLException e ) {
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				newImageTeg = ms.replaceAll("\""+ pathFiles.replace("\\", "\\\\") + "\\\\"+ nameIm + "\"");
				
			}
		    //������ ������ �� ���������� ���������� ��������
			result = result.replaceFirst("<img[^<]+" + nameIm + "[^>]+>",newImageTeg.toString().replace("\\", "\\\\"));
		}
		return result;
	}
//	Pattern pl = Pattern.compile("<link [\\w|:|//|.|?|=| |\"|-]+>");  
//	Matcher ml = pl.matcher(strFileContents);
//	while(ml.find()) {
//		Pattern ph = Pattern.compile("href=\"[\\w|:|//|.|?|=|_|-]+\"");  
//		Matcher mh = ph.matcher(strFileContents.substring(ml.start(),ml.end()));
//		System.out.println(strFileContents.substring(ml.start(),ml.end()));
//		if(mh.find())
//		System.out.println(strFileContents.substring(ml.start(),ml.end()).substring(mh.start(), mh.end()));
//	}
	
}
