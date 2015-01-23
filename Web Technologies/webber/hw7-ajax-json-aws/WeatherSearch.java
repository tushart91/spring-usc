import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.*;
import org.json.*;


public class WeatherSearch extends HttpServlet {
    
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String location = request.getParameter("location");
    location = location.replace(' ', '+');
    String type = request.getParameter("type");
    String tempUnit = request.getParameter("tempUnit");
    
    String urlStr = "http://default-environment-xizqxi8m9e.elasticbeanstalk.com/";
    urlStr = urlStr + "?location=" + location + "&type=" + type + "&tempUnit=" + tempUnit; 
     
    response.setContentType("text/JSON"); 
    URL url = new URL(urlStr);
    URLConnection c = url.openConnection();
    BufferedReader in = new BufferedReader(
      new InputStreamReader(c.getInputStream())); //url.openStream()));

    String inputLine, input = "";
    while ((inputLine = in.readLine()) != null){
      input = input + inputLine;
    }
    in.close();
    
    PrintWriter out = response.getWriter();
    try {
      String jsonString = XML.toJSONObject(input).toString(2);
      out.println(jsonString);
    }
    catch(JSONException je) {
      out.println(je.toString());
    }
    
    
    /*URLConnection urlConnection = url.openConnection();
    urlConnection.setAllowUserInteraction(false);
    InputStream urlstream = url.openStream();*/
    

	}    	
/* We are going to perform the same operations for POST requests as for GET methods, so this method just sends the request to the doGet method.*/
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException { 
		doGet(request, response);
	}
}