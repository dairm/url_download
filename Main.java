package com.ncedu.mustakhimov;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

	public static void main(String[] args) {
		Download down = new Download();
		
        Options options = new Options();
        String urlOp = null;
        String pathOp = null;
        String openOp = null;
        
        //Аргументы командной строки -u -p -o
        //Пример -u nytimes.com -p c://test -o true/false
        Option url = new Option("u", "url", true, "url");
        url.setRequired(true);
        options.addOption(url);
        Option path = new Option("p", "path", true, "path to file");
        path.setOptionalArg(true);
        options.addOption(path); 
        Option open = new Option("o", "open", true, "open file");
        open.setOptionalArg(true);
        options.addOption(open); 
        
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
            return;
        }
        urlOp = cmd.getOptionValue("url");
        pathOp = cmd.getOptionValue("path");
        openOp = cmd.getOptionValue("open");
        System.out.println(urlOp);
        System.out.println(pathOp);
        System.out.println(openOp);
        if(pathOp == null){
        	pathOp = System.getProperty("user.dir");
        }
           
		try {
			down.downloadWithURL(urlOp, pathOp, openOp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
