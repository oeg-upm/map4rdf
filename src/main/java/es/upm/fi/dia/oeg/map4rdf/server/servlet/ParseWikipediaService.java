package es.upm.fi.dia.oeg.map4rdf.server.servlet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Singleton;

@Singleton
public class ParseWikipediaService extends HttpServlet{
	
	private static final long serialVersionUID = -8524195705285261839L;
	private static final String WIKIPEDIA_PARAM="URL";
	
	@SuppressWarnings("static-access")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setContentType("text/html; charset=UTF-8");
			String URL = getWikipediaURL(req);
			String result="";
			if(URL.isEmpty() || !URL.contains("wikipedia")){
				result="You need to specified URL parameter with wikipedia URL.";
				printString(result, resp);
				resp.getOutputStream().close();
				return;
			}
			if(!URL.contains("http://")){
				URL="https://"+URL;
			}
			if(URL.contains("http://")){
				URL=URL.replace("http://", "https://");
			}
			try {
	            final URL wikipediaURL = new URL(URL);
	            final String host=wikipediaURL.getHost();
	            final HttpURLConnection wikipediaCon = (HttpURLConnection)wikipediaURL.openConnection();
	            wikipediaCon.setFollowRedirects(true);
	            wikipediaCon.addRequestProperty("Content-Type", "text/plain; charset=utf-8");
	            wikipediaCon.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
	            wikipediaCon.setRequestProperty("content-type", "text/plain; charset=utf-8");
	            wikipediaCon.connect();
	            BufferedReader buffReader = new BufferedReader(
	                    new InputStreamReader(wikipediaCon.getInputStream(),"UTF-8"));
	            String toReturn=htmlParseWikipediaInfobox(buffReader,host);
	            if(toReturn==null){
	            	final URLConnection wikipediaConDescription = wikipediaURL.openConnection();
	            	final BufferedReader buffReaderDescription = new BufferedReader(
		                    new InputStreamReader(wikipediaConDescription.getInputStream(),"UTF-8"));
	            	toReturn=htmlParseWikipediaFirtsDescription(buffReaderDescription,host);
	            }
	            printString(toReturn, resp);
	            resp.getOutputStream().close();
	            return;
	           
	        } catch (final MalformedURLException e) {
	            e.printStackTrace();
	            throw new ServletException(e);
	        } catch (final IOException e) {
	            e.printStackTrace();
	            throw new ServletException(e);
	        }
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
	
	private String getWikipediaURL(HttpServletRequest req){
		String wikipediaURL=req.getParameterValues(WIKIPEDIA_PARAM)[0];
		return wikipediaURL;
	}
	private void printString(String toPrint,HttpServletResponse resp) throws IOException{
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(resp.getOutputStream(),"UTF-8"));
		writer.append(toPrint);
		writer.flush();
	}
	private String htmlParseWikipediaFirtsDescription(
			BufferedReader buffReader, String host) {
		String result=""; 
		boolean finalHead=false;
		boolean foundFirtsDescription=false;
		boolean foundFirtsP=false;
		boolean finish=false;
		try {
             String inputLine = buffReader.readLine();
             while (inputLine != null && !finish) {
             	//System.out.println(inputLine);
             	if(inputLine.contains("</head")){
                	finalHead = true;
                	result+=inputLine;
                	result+="<body>";
                }
             	if(finalHead && inputLine.contains("mw-content-text")){
             		foundFirtsDescription=true;
             	}
             	if(foundFirtsDescription && inputLine.contains("<p")){
             		foundFirtsP=true;
             	}
             	if(!finalHead || foundFirtsP){
             		if(inputLine.contains("href=/")){
             			inputLine=inputLine.replace("href=/", "target=\"_blank\" href=\"http://"+host+"/");
             		}
             		if(inputLine.contains("href=\"/")){
             			inputLine=inputLine.replace("href=\"/", "target=\"_blank\" href=\"http://"+host+"/");
             		}
             		result+=inputLine;
             	}
             	if(inputLine.contains("id=\"toc\"")){
             			finish=true;
             	}
                inputLine = buffReader.readLine();
             }
             if(!foundFirtsP){
            	 return "Not found infobox or description";
             }
             result+="</body></html>";
        } catch (IOException e) {
			e.printStackTrace();
		} finally {
             try {
				buffReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		return result;
	}
	private String htmlParseWikipediaInfobox(BufferedReader buffReader, String host){
		String result=""; 
		boolean finalHead=false;
		boolean foundInfobox=false;
		boolean finish=false;
		int countTables=-1;
		try {
			boolean firtsExecution = true;
             String inputLine = buffReader.readLine();
             if(firtsExecution){
            	 while(!buffReader.ready()){}
            	 inputLine = buffReader.readLine();
            	 firtsExecution = false;
             }
             String inputLineContains = "";
             while (inputLine != null && !finish) {
            	inputLineContains = "";
                if(inputLine !=null ){
                	inputLineContains = inputLine.toLowerCase();
                }
             	if(inputLineContains.contains("</head")){
                	finalHead = true;
                	result+=inputLine;
                	result+="<body>";
                }
             	if(finalHead && (inputLineContains.contains("infobox_v2") || inputLineContains.contains("infobox"))){
             		foundInfobox=true;
             	}
             	if(!finalHead || foundInfobox){
             		if((inputLineContains.contains("infobox_v2") || inputLineContains.contains("infobox"))){
             			result+="<table style=\"width:15px; text-align:left;\">";
             		}else{
             			if(finalHead){
             				if(inputLine.contains("href=/")){
             					inputLine=inputLine.replace("href=/", "target=\"_blank\" href=\"http://"+host+"/");
             				}
             				if(inputLine.contains("href=\"/")){
             					inputLine=inputLine.replace("href=\"/", "target=\"_blank\" href=\"http://"+host+"/");
             				}
             			}
             			result+=inputLine;
             		}
             		if(foundInfobox && inputLine.contains("<table")){
             			countTables++;
             		}
             		if(inputLineContains.contains("</table")){
             			if(countTables==0){
             				finish=true;
             			}else{
             				countTables--;
             			}
             		}
             	}
                inputLine = buffReader.readLine();
             }
             if(!foundInfobox){
            	 return null;
             }
             result+="</body></html>";
        } catch (IOException e) {
			e.printStackTrace();
		} finally {
             try {
				buffReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		return result;
	}
}
