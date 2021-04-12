package it.unipd.dei.yourwaytoitaly.rest;


import it.unipd.dei.yourwaytoitaly.database.*;
import it.unipd.dei.yourwaytoitaly.resource.*;
import it.unipd.dei.yourwaytoitaly.servlet.SessionCheckServlet;
import it.unipd.dei.yourwaytoitaly.utils.ErrorCode;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the REST API for the {@link Advertisement} resource.
 *
 * @author Nicola Ferro (ferro@dei.unipd.it)
 * @version 1.00
 * @since 1.00
 */
public class AdvertisementRestResource extends RestResource {

    /**
     * Creates a new REST resource for managing {@code Employee} resources.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public AdvertisementRestResource(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
        super(req, res, con);
    }

    /**
     * Creates an Advertisement
     *
     * @throws IOException
     *             if any error occurs
     * @throws ServletException
     *             if any error occurs
     */
    public void insertAdvertisement() throws ServletException, IOException {

        try{
            // check if a session is valid
            User u = new SessionCheckServlet(req, res).getUser();
            if (u == null) {
                ErrorCode ec = ErrorCode.USER_NOT_FOUND;
                Message m = new Message("User not found.",
                        ec.getErrorCode(),"User not found.");
                res.setStatus(ec.getHTTPCode());
                req.setAttribute("message", m);
                req.getRequestDispatcher("/jsp/login.jsp").forward(req, res);
            }

            int idAdvertisement = 0;
            String title = "";
            String description = "";
            int price=0;
            int numTotItem=0;
            Date dateStart=null, dateEnd=null;
            Time timeStart=null, timeEnd=null;
            int score;
            String type;
            int idType;

            // check if the servlet needs to receive images or if a json object (advertisement)
            if (!ServletFileUpload.isMultipartContent(req)) {

                // receive JSON object
                Advertisement advertisement = Advertisement.fromJSON(req.getInputStream());

                // control the parameters
                title = advertisement.getTitle();
                if(title==null || title.length()<5 || title.length()>100){
                    ErrorCode ec = ErrorCode.WRONG_FORMAT;
                    Message m = new Message("Input value not valid.",
                            ec.getErrorCode(),"Title of the advertisement not valid.");
                    res.setStatus(ec.getHTTPCode());
                    req.setAttribute("message", m);
                    res.sendRedirect(req.getContextPath() + "/advertisement-do-create/");
                }
                description = advertisement.getDescription();
                if(description==null || description.length()<5 || description.length()>10000){
                    ErrorCode ec = ErrorCode.WRONG_FORMAT;
                    Message m = new Message("Input value not valid.",
                            ec.getErrorCode(),"description of the advertisement not valid.");
                    res.setStatus(ec.getHTTPCode());
                    req.setAttribute("message", m);
                    res.sendRedirect(req.getContextPath() + "/advertisement-do-create/");
                }
                price = advertisement.getPrice();
                if(price<0){
                    ErrorCode ec = ErrorCode.WRONG_FORMAT;
                    Message m = new Message("Input value not valid.",
                            ec.getErrorCode(),"Price not valid");
                    res.setStatus(ec.getHTTPCode());
                    req.setAttribute("message", m);
                    res.sendRedirect(req.getContextPath() + "/advertisement-do-create/");
                }

                numTotItem = advertisement.getNumTotItem();
                if(numTotItem<=0){
                    ErrorCode ec = ErrorCode.WRONG_FORMAT;
                    Message m = new Message("Input value not valid.",
                            ec.getErrorCode(),"Total number of item not valid");
                    res.setStatus(ec.getHTTPCode());
                    req.setAttribute("message", m);
                    res.sendRedirect(req.getContextPath() + "/advertisement-do-create/");
                }

                dateStart = advertisement.getDateStart();
                dateEnd = advertisement.getDateEnd();
                timeStart = advertisement.getTimeStart();
                timeEnd = advertisement.getTimeEnd();
                if(dateEnd.compareTo(dateStart)<0 || timeEnd.compareTo(timeStart)<0){
                    ErrorCode ec = ErrorCode.WRONG_FORMAT;
                    Message m = new Message("Input value not valid.",
                            ec.getErrorCode(),"Dates entered are not valid.");
                    res.setStatus(ec.getHTTPCode());
                    req.setAttribute("message", m);
                    res.sendRedirect(req.getContextPath() + "/advertisement-do-create/");
                }

                score = (int) (price/3.14);

                // insert JSON inside DB
                advertisement = AdvertisementDAO.createAdvertisement(advertisement);

                if(advertisement==null){
                    ErrorCode ec = ErrorCode.INTERNAL_ERROR;
                    Message m = new Message("Generic error",
                            ec.getErrorCode(),"Cannot create the advertisement.");
                    res.setStatus(ec.getHTTPCode());
                    req.setAttribute("message", m);
                    res.sendRedirect(req.getContextPath() + "/advertisement-do-create/");
                }

                // set attribute
                req.setAttribute("idAdvertisement",idAdvertisement);

                // forward to create-advertisement.jsp
                req.getRequestDispatcher("/jsp/create-advertisement.jsp").forward(req, res);

            } else {
                // receive Images (if idAdvertisement!=0)
                idAdvertisement = (int) req.getAttribute("idAdvertisement");
                if (idAdvertisement!=0) {
                    final String UPLOAD_DIRECTORY = "/" + String.valueOf(idAdvertisement);
                    List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(req);
                    int count = 1;
                    for (FileItem item : multiparts) {
                        if (!item.isFormField()) {
                            //String name = new File(item.getName()).getName();
                            String pathName = UPLOAD_DIRECTORY + File.separator + String.valueOf(count++) + ".jpg";
                            item.write(new File(pathName));
                            Image img = new Image
                                    (
                                            0,
                                            pathName,
                                            title,
                                            idAdvertisement
                                    );
                            ImageDAO.createImage(img);
                        }
                    }
                    // forward the user to its user profile page
                    req.getRequestDispatcher("/user/profile").forward(req, res);
                }
            }

        } catch (Exception ex) {
            ErrorCode ec = ErrorCode.INTERNAL_ERROR;
            Message m = new Message("Cannot create the advertisement. ",
                    ec.getErrorCode(), ex.getMessage());
            res.setStatus(ec.getHTTPCode());
            req.setAttribute("message", m);
            req.setAttribute("idAdvertisement", 0);
            req.getRequestDispatcher("/advertisement-do-create").forward(req, res);
        }
    }

    /**
     * Reads an Advertisement from the database.
     *
     * @throws IOException
     *             if any error occurs
     * @throws ServletException
     *             if any error occurs
     */
    public void showAdvertisement() throws IOException, ServletException {

        try {

            String idAdvertisement = req.getRequestURI();
            idAdvertisement = idAdvertisement.substring(idAdvertisement.lastIndexOf("advertisement") + 14);
            Advertisement advertisement = AdvertisementDAO.searchAdvertisement(Integer.parseInt(idAdvertisement));

            advertisement.toJSON(res.getOutputStream());
            //req.setAttribute("advertisement", advertisement);

            List<Image> imageList = ImageDAO.searchImageByIdAdvertisement(Integer.parseInt(idAdvertisement));

            if (imageList!=null) {
                List<String> filepathList = new ArrayList<String>();
                for (Image image : imageList) {
                    String filepath = image.getPath();
                    filepathList.add(filepath);
                }
                req.setAttribute("filepath-list", filepathList);
            } else {
                req.setAttribute("filepath-list", null);
            }

            // The owner can see the booking list relative to this advertisement: check if a session is valid
            User u = new SessionCheckServlet(req, res).getUser();
            if (u != null) {
                // check if the user (company) is the owner of the advertisement
                String emailSession = u.getEmail();
                if (emailSession.equals(advertisement.getEmailCompany())) {
                    List<Booking> listBookings = BookingDAO.searchBookingByAdvertisement(Integer.parseInt(idAdvertisement));
                    req.setAttribute("booking-list", listBookings);
                } else {
                    req.setAttribute("booking-list", null);
                }

            }
            List<Feedback> feedbackList = FeedbackDAO.searchFeedbackByAdvertisement(Integer.parseInt(idAdvertisement));
            double rate = 0;
            if (feedbackList!=null) {
                for (Feedback f: feedbackList) {
                    rate += f.getRate();
                }
                rate/=feedbackList.size();
                req.setAttribute("feedback-list", feedbackList);
                req.setAttribute("rate", (int) rate);
            } else {
                req.setAttribute("feedback-list", null);
                req.setAttribute("rate", 0);
            }

            req.getRequestDispatcher("/jsp/show-advertisement.jsp").forward(req, res);

        } catch (Exception ex) {
            ErrorCode ec = ErrorCode.INTERNAL_ERROR;
            Message m = new Message("Cannot show the advertisement. ",
                    ec.getErrorCode(), ex.getMessage());
            res.setStatus(ec.getHTTPCode());
            req.setAttribute("message", m);
            res.sendRedirect(req.getContextPath() + "/index/");
        }

    }

    /**
     * Updates an employee in the database.
     *
     * @throws IOException
     *             if any error occurs
     * @throws ServletException
     *             if any error occurs
     */
    public void editAdvertisement() throws IOException, ServletException {

        int score;
        int price;
        int numTotItem = 0;
        int idAdvertisement = 0;
        String emailCompany = null;
        Advertisement advertisement;
        String op = req.getRequestURI();
        op = op.substring(op.lastIndexOf("advertisement") + 14);

        try{
            // check if a session is valid
            User u = new SessionCheckServlet(req, res).getUser();
            if (u == null) {
                ErrorCode ec = ErrorCode.USER_NOT_FOUND;
                Message m = new Message("User not found.",
                        ec.getErrorCode(),"User not found.");
                res.setStatus(ec.getHTTPCode());
                req.setAttribute("message", m);
                req.getRequestDispatcher("/user/do-login/").forward(req, res);
            }

            // check if the email of the session is equal to emailCompany
            String emailSession = u.getEmail();
            emailCompany = AdvertisementDAO.searchAdvertisement(idAdvertisement).getEmailCompany();
            if (!emailSession.equals(emailCompany)) {
                ErrorCode ec = ErrorCode.WRONG_CREDENTIALS;
                Message m = new Message("User is not authorized.",
                        ec.getErrorCode(),"User is not authorized to edit this advertisement");
                res.setStatus(ec.getHTTPCode());
                req.setAttribute("message", m);
                req.getRequestDispatcher("/user/do-login/").forward(req, res);
            }

            // receive idAdvertisement from the hidden form
            idAdvertisement = Integer.parseInt(op);

            advertisement = Advertisement.fromJSON(req.getInputStream());

            price = advertisement.getPrice();
            numTotItem = advertisement.getNumTotItem();

            //price = Integer.parseInt(req.getParameter("price"));
            //numTotItem = Integer.parseInt(req.getParameter("numTotItem"));

            if(price<0 || price>50000){
                ErrorCode ec = ErrorCode.WRONG_FORMAT;
                Message m = new Message("Price not valid.",
                        ec.getErrorCode(),"Price not valid");
                res.setStatus(ec.getHTTPCode());
                req.setAttribute("message", m);
                req.getRequestDispatcher("/jsp/edit-advertisement.jsp").forward(req, res);
            }

            if(numTotItem<0 || numTotItem>1000){
                ErrorCode ec = ErrorCode.WRONG_FORMAT;
                Message m = new Message("Number of items not valid.",
                        ec.getErrorCode(),"Number of items not valid");
                res.setStatus(ec.getHTTPCode());
                req.setAttribute("message", m);
                req.getRequestDispatcher("/jsp/edit-advertisement.jsp").forward(req, res);
            }

            score = (int) (price/3.14);

            advertisement = new Advertisement(
                    idAdvertisement,
                    null,
                    null,
                    score,
                    price,
                    numTotItem,
                    null,
                    null,
                    null,
                    null,
                    null,
                    0
            );

            AdvertisementDAO.editAdvertisement(advertisement);

            req.getRequestDispatcher("/user/profile").forward(req, res);

        } catch (Exception ex) {
            ErrorCode ec = ErrorCode.INTERNAL_ERROR;
            Message m = new Message("Cannot edit the advertisement. ",
                    ec.getErrorCode(), ex.getMessage());
            res.setStatus(ec.getHTTPCode());
            req.setAttribute("message", m);
            req.getRequestDispatcher("/jsp/edit-advertisement.jsp").forward(req, res);
        }
    }

    /**
     * Shows:
     * - the list of Advertisements to a tourist (based on search criteria)
     * - the list of Advertisements to a company (based on emailCompany)
     *
     * @throws IOException
     *             if any error occurs in the client/server communication.
     */
    public void listAdvertisement() throws IOException, ServletException, SQLException, NamingException {
        //per la company: list/advertisement
        //per il tourist: homepage
        String op = req.getRequestURI();
        op = op.substring(op.lastIndexOf("list") + 5);
        List<Advertisement> listAdvertisement;

        try {

            switch (op) {
                case "advertisement":
                    // list all the advertisements requested by the user

                    int idCity;
                    Date date = null;
                    int idType;

                    date = Date.valueOf(req.getParameter("date").toString());
                    City city = CityDAO.searchCity(req.getParameter("city"));
                    TypeAdvertisement typeAdvertisement =
                            TypeAdvertisementDAO.searchTypeAdvertisement(req.getParameter("typeAdvertisement"));

                    if (date == null || city == null || typeAdvertisement == null) {
                        ErrorCode ec = ErrorCode.WRONG_FORMAT;
                        Message m = new Message("Input value not valid.",
                                ec.getErrorCode(), "Input value not valid.");
                        res.setStatus(ec.getHTTPCode());
                        req.setAttribute("message", m);
                        req.getRequestDispatcher("/jsp/index.jsp").forward(req, res);
                    }

                    idCity = city.getIdCity();
                    idType = typeAdvertisement.getIdType();

                    listAdvertisement = AdvertisementDAO.searchAdvertisement(idCity, idType, date);
                    new ResourceList(listAdvertisement).toJSON(res.getOutputStream());

                    //req.setAttribute("advertisement-list", listAdvertisement);
                    req.getRequestDispatcher("/jsp/show-advertisement-list.jsp").forward(req, res);
                    break;
                default:
                    ErrorCode ec = ErrorCode.METHOD_NOT_ALLOWED;
                    Message m = new Message("Cannot show the advertisement. ",
                            ec.getErrorCode(), "Method not allowed");
                    res.setStatus(ec.getHTTPCode());
                    req.setAttribute("message", m);
                    req.getRequestDispatcher("/jsp/index.jsp").forward(req, res);
                    break;
            }
        } catch (Exception ex) {
            ErrorCode ec = ErrorCode.INTERNAL_ERROR;
            Message m = new Message("Cannot show the advertisement. ",
                    ec.getErrorCode(), ex.getMessage());
            res.setStatus(ec.getHTTPCode());
            req.setAttribute("message", m);
            req.getRequestDispatcher("/jsp/index.jsp").forward(req, res);
        }


    }


}
