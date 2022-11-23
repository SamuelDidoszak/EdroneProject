package stringgenerator.Database;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.stereotype.Component;

import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseHandler {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * Adds a file to the database
     * @param params filename - unique identifier of a file
     * @param file file to save
     * @throws IOException
     */
    public void addFile(String params, File file) throws IOException {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("params", params, Types.VARCHAR);
        source.addValue("file", new SqlLobValue(new FileInputStream(file), (int) file.length(), new DefaultLobHandler()), Types.BLOB);
        jdbcTemplate.update("INSERT INTO strings(params, file) VALUES (:params, :file) ON DUPLICATE KEY UPDATE params=:params", source);
    }

    /**
     * Finds a file with param or returns null
     * @param params custom strings parameters
     * @return File or null if not in the database
     */
    public File getFileByParams(String params) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("params", params);
        List<UniqueStrings> dbStrings = jdbcTemplate.query(
                "select file, params from strings where params = :params",
                source, (rs, rowNum) -> {
                    UniqueStrings uniqueStrings = new UniqueStrings();
                    uniqueStrings.setParams(rs.getString("params"));
                    uniqueStrings.setFile(rs.getBlob("file"));
                    return uniqueStrings;
                }
        );

        if (dbStrings.isEmpty())
            return null;

        return blobToFileConversion(dbStrings.get(0).getParams(), dbStrings.get(0).getFile());
    }

    /**
     * Finds multiple files with param or returns null
     * @param params custom strings parameters
     * @return File or null if not in the database
     */
    public ArrayList<File> getFilesByParams(List<String> params) {
        ArrayList<File> uniqueStrings = new ArrayList<>();
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("params", params);
        List<UniqueStrings> dbStrings = jdbcTemplate.query(
            "select file, params from strings where params in (:params)",
                source, (rs, rowNum) -> {
                    UniqueStrings uniqueStrings1 = new UniqueStrings();
                    uniqueStrings1.setParams(rs.getString("params"));
                    uniqueStrings1.setFile(rs.getBlob("file"));
                    return uniqueStrings1;
                }
        );

        if (dbStrings.isEmpty())
            return null;

        for (UniqueStrings strings : dbStrings) {
            uniqueStrings.add(blobToFileConversion(strings.getParams(), strings.getFile()));
        }
        return uniqueStrings;
    }

    private File blobToFileConversion(String params, Blob blob) {
        File newFile = new File(params);
        try(OutputStream outputStream = new FileOutputStream(newFile)) {
            IOUtils.copy(blob.getBinaryStream(), outputStream);
            return newFile;
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        return newFile;
    }
}
