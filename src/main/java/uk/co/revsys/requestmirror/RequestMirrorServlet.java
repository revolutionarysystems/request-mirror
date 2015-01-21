package uk.co.revsys.requestmirror;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import uk.co.revsys.ripe.client.RipeClient;
import uk.co.revsys.ripe.client.RipeSearchResult;

public class RequestMirrorServlet extends HttpServlet {

    private RipeClient ripeClient;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.ripeClient = new RipeClient();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject json = new JSONObject();
        String ipAddress = req.getRemoteAddr();
        json.put("ipAddress", ipAddress);
        json.put("timestamp", new Date().getTime());
        JSONObject headersJson = new JSONObject();
        Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = req.getHeader(headerName);
            headersJson.put(headerName, headerValue);
        }
        json.put("headers", headersJson);
        if ("true".equals(req.getParameter("includeNetwork"))) {
            RipeSearchResult searchResult = ripeClient.search(ipAddress);
            JSONObject networkJSON = new JSONObject();
            networkJSON.put("name", searchResult.getNetworkName());
            networkJSON.put("description", searchResult.getNetworkDescription());
            json.put("network", networkJSON);
        }
        resp.setContentType("application/json");
        resp.getOutputStream().print(json.toString());
        resp.getOutputStream().flush();
    }

}
