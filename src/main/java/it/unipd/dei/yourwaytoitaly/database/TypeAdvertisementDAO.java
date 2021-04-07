package it.unipd.dei.yourwaytoitaly.database;

import it.unipd.dei.yourwaytoitaly.resource.TypeAdvertisement;
import it.unipd.dei.yourwaytoitaly.utils.DataSourceProvider;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class for:
 * - searching and returning a TypeAdvertisement by ID_TYPE
 * - searching and returning a TypeAdvertisement by TYPE
 * inside the database
 *
 * @author Vittorio Esposito
 * @author Marco Basso
 * @author Matteo Piva
 * @version 1.0
 * @since 1.0
 */
public class TypeAdvertisementDAO extends AbstractDAO{

    /**
     * searches and returns a TypeAdvertisement by ID_TYPE
     *
     * @return a TypeAdvertisement objects matching
     *
     * @throws SQLException
     *             if any error occurs.
     * @throws NamingException
     *             if any error occurs.
     */
    public static TypeAdvertisement searchTypeAdvertisement(int idType) throws SQLException, NamingException {
        final String STATEMENT_ID = "SELECT ID_type, type " +
                "FROM YWTI.TYPE_ADVERTISEMENT " +
                "WHERE ID_type = ?;";
        Connection con = DataSourceProvider.getDataSource().getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        TypeAdvertisement typeAdvertisement = null;

        try {
            pstmt = con.prepareStatement(STATEMENT_ID);
            pstmt.setInt(1, idType);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                typeAdvertisement = new TypeAdvertisement(
                        rs.getInt("ID_type"),
                        rs.getString("type"));
            }

        } finally {
            //close all the possible resources
            cleaningOperations(pstmt, rs, con);
        }

        return typeAdvertisement;
    }

    /**
     * searches and returns a TypeAdvertisement by NAME (TYPE)
     *
     * @return a TypeAdvertisement objects matching
     *
     * @throws SQLException
     *             if any error occurs.
     * @throws NamingException
     *             if any error occurs.
     */
    public static TypeAdvertisement searchTypeAdvertisement(String type) throws SQLException, NamingException {
        final String STATEMENT_NAME = "SELECT ID_type, type " +
                "FROM YWTI.TYPE_ADVERTISEMENT " +
                "WHERE type = ?;";
        Connection con = DataSourceProvider.getDataSource().getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        TypeAdvertisement typeAdvertisement = null;

        try {

            pstmt = con.prepareStatement(STATEMENT_NAME);
            pstmt.setString(1, type);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                typeAdvertisement = new TypeAdvertisement(
                        rs.getInt("ID_city"),
                        rs.getString("name"));

            }
        } finally {
            //close all the possible resources
            cleaningOperations(pstmt, rs, con);
        }

        return typeAdvertisement;
    }
}
