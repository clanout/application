package com.clanout.application.library.postgres;


import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PostgresQuery
{
    public static String load(String filename, Class<?> clazz)
    {
        try
        {
            StringBuilder query = new StringBuilder();

            BufferedReader br = new BufferedReader(new InputStreamReader(clazz.getResourceAsStream("/postgres/" + filename)));
            String line;
            while ((line = br.readLine()) != null)
            {
                query.append(line);
                query.append("\n");
            }

            br.close();

            return query.toString();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to read query : " + filename);
        }
    }
}
