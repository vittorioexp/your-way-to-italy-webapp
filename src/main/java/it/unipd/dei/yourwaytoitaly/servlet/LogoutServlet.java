package it.unipd.dei.yourwaytoitaly.servlet;

import it.unipd.dei.yourwaytoitaly.resource.Message;
import it.unipd.dei.yourwaytoitaly.utils.ErrorCode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


/**
 * Servlet closing an existing session
 * @author Francesco Giurisato
 * @author Marco Basso
 * @author Matteo Piva
 * @version 1.0
 * @since 1.0
 */

public class LogoutServlet extends AbstractDatabaseServlet {

    private static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";

    /**
     * Manages HTTP GET requests for logout
     *
     * @param req
     *            the request from the client.
     * @param res
     *            the response from the server.
     *
     * @throws Exception
     *             if any problem occurs while executing the servlet.
     */


    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        handleRequest(req, res);
    }

    public void handleRequest(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

        res.setContentType(JSON_UTF_8_MEDIA_TYPE);

        String op = req.getRequestURI();
        op = op.substring(op.lastIndexOf("user") + 5);

        if (op.equals ("do-logout")) {
            // the requested operation is register
            logout(req, res);
        }
        else{
            // the requested operation is unknown
            ErrorCode ec = ErrorCode.OPERATION_UNKNOWN;
            Message m = new Message("Not Valid Request.", ec.getErrorCode()
                    ,"You have requested a non existing resource .");
            res.setStatus(ec.getHTTPCode());
            m.toJSON(res.getOutputStream());
            return;
        }
    }


    public void logout (HttpServletRequest req, HttpServletResponse res)  throws IOException ,ServletException {

        try {
            HttpSession session = req.getSession(false);
            session.invalidate();

            Message success = new Message("Successful logout!");
            req.setAttribute("message", success);
            res.setStatus(HttpServletResponse.SC_OK);
            res.sendRedirect(req.getContextPath() + "/index");

        }catch (Exception ex){
            ErrorCode ec = ErrorCode.INTERNAL_ERROR;
            Message m = new Message("Failed to logout.",
                    ec.getErrorCode(), ex.toString());
            res.setStatus(ec.getHTTPCode());
            m.toJSON(res.getOutputStream());
            return;
        }
    }
}



