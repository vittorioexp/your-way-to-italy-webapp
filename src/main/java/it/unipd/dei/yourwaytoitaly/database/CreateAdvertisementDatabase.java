package it.unipd.dei.yourwaytoitaly.database;

import it.unipd.dei.yourwaytoitaly.resource.Advertisement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Class for inserting an Advertisement inside the database
 *
 * @author Vittorio Esposito
 * @author Marco Basso
 * @author Matteo Piva
 * @version 1.0
 * @since 1.0
 */


public final class CreateAdvertisementDatabase {

    /**
     * The SQL statements to be executed
     */
    private static final String STATEMENT =
            "INSERT INTO YWTI.Advertisement (TITLE, DESCRIPTION, SCORE, PRICE, NUM_TOT_ITEM, " +
                    "DATE_START, DATE_END, TIME_START, TIME_END, email_c, ID_TYPE) " +
                    "SELECT ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ID_TYPE " +
                    "FROM Type_advertisement WHERE Type_advertisement.type = ? RETURNING *;";

    private static final String STATEMENT_EDIT =
            "UPDATE TWTI.ADVERTISEMENT SET price = ? , score = ? WHERE ID_advertisement = ?;";

    /**
     * The connection to the database
     */
    private final Connection con;

    /**
     * The advertisement to create
     */
    private final Advertisement advertisement;

    /**
     * Creates a new object for creating advertisements.
     *
     * @param con
     *            the connection to the database.
     * @param advertisement
     *            the advertisement to create.
     */
    public CreateAdvertisementDatabase(final Connection con, final Advertisement advertisement) {
        this.con = con;
        this.advertisement = advertisement;
    }

    /**
     * Creates a new advertisement.
     *
     * @return the just created advertisement
     *
     * @throws SQLException
     *             if any error occurs while creating advertisements.
     */
    public Advertisement createAdvertisement() throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // the results of the creation
        Advertisement a = null;
        try {
            pstmt = con.prepareStatement(STATEMENT);
            pstmt.setString(1, advertisement.getTitle());
            pstmt.setString(2, advertisement.getDescription());
            pstmt.setInt(3, advertisement.getScore());
            pstmt.setInt(4, advertisement.getPrice());
            pstmt.setInt(5, advertisement.getNumTotItem());
            pstmt.setDate(6, advertisement.getDateStart());
            pstmt.setDate(7, advertisement.getDateEnd());
            pstmt.setTime(8, advertisement.getTimeStart());
            pstmt.setTime(9, advertisement.getTimeEnd());
            pstmt.setString(10, advertisement.getEmailCompany());
            pstmt.setInt(11, advertisement.getIdType());

            rs = pstmt.executeQuery();

            while (rs.next()) {
                a = new Advertisement(
                        rs.getInt("ID_advertisement"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("score"),
                        rs.getInt("price"),
                        rs.getInt("num_tot_item"),
                        rs.getDate("date_start"),
                        rs.getDate("date_end"),
                        rs.getTime("time_start"),
                        rs.getTime("time_end"),
                        rs.getString("email_c"),
                        rs.getInt("ID_type"));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            con.close();
        }

        return a;
    }

    public void editAdvertisement() throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareStatement(STATEMENT_EDIT);
            pstmt.setInt(1, advertisement.getPrice());
            pstmt.setInt(2, advertisement.getScore());
            pstmt.setInt(3, advertisement.getIdAdvertisement());

            rs = pstmt.executeQuery();

        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            con.close();
        }

        return;
    }
}