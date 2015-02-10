package uk.co.revsys.requestmirror;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
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
        String ipAddress = req.getRemoteAddr();
        if(req.getParameter("RevSysOverrideIPAddress")!=null){
            ipAddress = req.getParameter("RevSysOverrideIPAddress");
        }
        HashMap data = new HashMap();
        data.put("ipAddress", ipAddress);
        data.put("timestamp", new Date().getTime());
        Enumeration<String> headerNames = req.getHeaderNames();
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = req.getHeader(headerName);
            headers.put(headerName, headerValue);
        }
        
        data.put("headers", headers);
        if ("true".equals(req.getParameter("includeNetwork"))) {
            RipeSearchResult searchResult = ripeClient.search(ipAddress);
            HashMap network = new HashMap();
            network.put("name", searchResult.getNetworkName());
            network.put("description", searchResult.getNetworkDescription());
            data.put("network", network);
        }
        resp.setContentType("application/json");
        resp.getOutputStream().print(JSONObject.toJSONString(data));
        resp.getOutputStream().flush();
    }

}
