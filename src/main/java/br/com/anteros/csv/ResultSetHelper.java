package br.com.anteros.csv;
/*
 Copyright 2005 Bytecode Pty Ltd.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Interface for the ResultSetHelperService.
 * Allows the user to define their own ResultSetHelper for use in the CSVWriter.
 */
public interface ResultSetHelper {
    /**
     * Returns the column names from the ResultSet.
     *
     * @param rs ResultSet
     * @return String array containing the column names.
     * @throws SQLException Thrown by the ResultSet.
     */
    String[] getColumnNames(ResultSet rs) throws SQLException;

    /**
     * Returns the column values from the result set.
     * @param rs The ResultSet containing the values.
     * @return String array containing the values.
     * @throws SQLException Thrown by the ResultSet.
     * @throws IOException Thrown by the ResultSet.
     */
    String[] getColumnValues(ResultSet rs) throws SQLException, IOException;

    /**
     * Returns the column values from the result set with the values trimmed if desired.
     * @param rs The ResultSet containing the values.
     * @param trim Values should have white spaces trimmed.
     * @return String array containing the values.
     * @throws SQLException Thrown by the ResultSet.
     * @throws IOException Thrown by the ResultSet.
     */
    String[] getColumnValues(ResultSet rs, boolean trim) throws SQLException, IOException;

    /**
     * Returns the column values from the result set with the values trimmed if desired.
     * Also format the date and time columns based on the format strings passed in.
     * @param rs The ResultSet containing the values.
     * @param trim Values should have white spaces trimmed.
     * @param dateFormatString Format string for dates.
     * @param timeFormatString Format string for timestamps.
     * @return String array containing the values.
     * @throws SQLException Thrown by the ResultSet.
     * @throws IOException Thrown by the ResultSet.
     */
    String[] getColumnValues(ResultSet rs, boolean trim, String dateFormatString, String timeFormatString)
         throws SQLException, IOException;
}
