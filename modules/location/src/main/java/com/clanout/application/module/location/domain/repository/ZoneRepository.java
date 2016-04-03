package com.clanout.application.module.location.domain.repository;

import com.clanout.application.module.location.domain.model.LocationZone;

import java.util.List;

public interface ZoneRepository
{
    List<LocationZone> fetchAllZones();
}
