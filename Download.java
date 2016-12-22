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
Используется для загрузки сайтов по url в файл,<br/>  находящийся в домашней директории
или в указаной директории 
@author Мустахимов Даир
@version 1.0
*/

public class Download {
	
	/**
	Загрузка данных сайт по URL 
	@param adress - url (без http://)
	@param filePathString - путь к родителю создаваемого файла
	@param openOption - настройка открытия файла после создания
	*/
	public void downloadWithURL(String adress, String filePathString, String openOption) throws IOException{
		URL url = new URL("http://" + adress);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		    //загрузка данных с сайта
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
	Преобразование потока данных в строку, создание файла<br/> и загрузка в него данных 
	@param in - поток данных загруженных с сайта
	@param adress - url
	@param filePathString - путь к родителю создаваемого файла
	@param openOption - настройка открытия файла после создания
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
		  // файл существует и это каталог
			pathOfFile = filePathString +"\\"+ nameOfFile(adress);
		}
		if (Files.exists(path) && Files.isRegularFile(path)) {
	      // файл существует и это обычный файл
			byte bKbd[] = new byte[256];
			int iCnt = 0;
			    System.out.println("1 - Заменить файл");
			    System.out.println("2 - Изменить имя");
			    System.out.print("Ввод: ");
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
		  // файл не существует, сохраняем в домашней  директории
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
        //загрузка изображений и замена ссылок в html документе
		strFileContents = downloadAndChangeImage(strFileContents, filePathString);
		switch(option){
	        case "2":
	        	//переименнование существущего файла
	        	System.out.print(pathOfFile+"=========1");
	        	File file = new File(filePathString);
	        	file.renameTo(new File(pathOfFile));
	        	if(openOption.trim().equals("true")){
			    	openFile(pathOfFile);
			    }
	        break;
	        default:
	        	//загрузка в файл
	        	System.out.println(pathOfFile+"========2");
	    	BufferedOutputStream bufferedOutput = new BufferedOutputStream(new FileOutputStream(
		    		 pathOfFile));
		    bufferedOutput.write(strFileContents.getBytes());
		    bufferedOutput.close();
		    //открываем файл если openOption == true
		    if(openOption.trim().equals("true")){
		    	openFile(pathOfFile);
		    }
	    }
	     
	}
	/**
	Парсинг имени файла по url
	@param adress - url (без http://)
	@return возвращает имя файла 
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
	Парсинг имени изображения из url
	@param adress - url
	@return возвращает имя изображения 
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
	Функцмя открытия файла 
	@param pathFile - путь к файлу 
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
	Парсинг и загрузка изображений из html документа
	@param strFileContents - данные с сайта в виде сроки
	@param filePathString - путь к родилю файла 
	*/
	private String downloadAndChangeImage(String strFileContents, String filePathString){
		Pattern pi = Pattern.compile("<img[^>]+>");  
		Matcher mi = pi.matcher(strFileContents);
		String result = strFileContents;
		String newImageTeg = null;
		String pathFiles = filePathString + "\\files";
		File Catalog = new File(pathFiles);//указываем путь для папки с изображениями
		Catalog.mkdirs();                  //создаем папку для изображений
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
		    //замена ссылок на изоражения локальными ссылками
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
