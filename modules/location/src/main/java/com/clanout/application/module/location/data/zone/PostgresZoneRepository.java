package com.clanout.application.module.location.data.zone;

import com.clanout.application.library.postgres.PostgresDataSource;
import com.clanout.application.library.postgres.PostgresQuery;
import com.clanout.application.module.location.domain.model.LocationZone;
import com.clanout.application.module.location.domain.repository.ZoneRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostgresZoneRepository implements ZoneRepository
{
    private static Logger LOG = LogManager.getRootLogger();

    private static final String SQL_READ_ZONES = PostgresQuery.load("read_zones.sql", PostgresZoneRepository.class);

    @Override
    public List<LocationZone> fetchAllZones()
    {
        List<LocationZone> locationZones = new ArrayList<>();

        try (Connection connection = PostgresDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_READ_ZONES))
        {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
                String zoneCode = resultSet.getString("zone_code");
                String name = resultSet.getString("name");
                double latitude = resultSet.getDouble("centroid_latitude");
                double longitude = resultSet.getDouble("centroid_longitude");
                double range = resultSet.getDouble("range");

                locationZones.add(new LocationZone(zoneCode, name, latitude, longitude, range));
            }

            resultSet.close();

            return locationZones;
        }
        catch (SQLException e)
        {
            LOG.error("Zones Read Error [" + e.getSQLState() + " : " + e.getMessage() + "]");
            throw new RuntimeException();
        }
    }
}
