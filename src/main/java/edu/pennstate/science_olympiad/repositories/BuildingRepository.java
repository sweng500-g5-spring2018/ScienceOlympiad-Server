package edu.pennstate.science_olympiad.repositories;

import com.google.gson.Gson;
import edu.pennstate.science_olympiad.Building;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("buildingRepository")
public class BuildingRepository {
    Log logger = LogFactory.getLog(getClass());

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * Gets all of the buildings from the database
     * @return all of the buildings
     */
    public List<Building> getAllBuildings() {
        return mongoTemplate.findAll(Building.class);
    }

    /**
     * Retrieves a building from the database
     * @param buildingID The building to retrieve
     * @return the building object if found; or null
     */
    public Building getBuilding(String buildingID) {
        Query singleQuery = new Query();
        singleQuery.addCriteria(Criteria.where("_id").is(buildingID));
        Building dbBuilding = mongoTemplate.findOne(singleQuery, Building.class);

        if (dbBuilding != null) {
            return dbBuilding;
        }
        return null;
    }

    /**
     * Adds a new building to the database
     * @param building The building to add
     * @return whether the building was added or not
     */
    public boolean addNewBuilding(Building building) {
        Query query = new Query();
        query.addCriteria(Criteria.where("building").is(building.getBuilding()));
        boolean exists = mongoTemplate.exists(query, Building.class);

        if (!exists) {
            logger.info("Adding a new building");
            mongoTemplate.insert(building);
            return true;
        }
        logger.info("Building already exists");
        return false;
    }

    /**
     * Removes a building from the database
     * @param buildingId The building to remove
     * @return whether the building was removed or not
     */
    public boolean removeBuilding (String buildingId) {
        Query singleQuery = new Query();
        singleQuery.addCriteria(Criteria.where("_id").is(buildingId));
        Building dbBuilding = mongoTemplate.findOne(singleQuery, Building.class);

        if (dbBuilding != null) {
            mongoTemplate.remove(dbBuilding);
            return true;
        }
        return false;
    }

    /**
     * updates a building in the database
     * @param buildingId The building to update
     * @return whether the building was updated or not
     */
    public boolean updateBuilding (String buildingId, String buildingJson) {
        Query singleQuery = new Query();
        singleQuery.addCriteria(Criteria.where("_id").is(buildingId));
        Building dbBuilding = mongoTemplate.findOne(singleQuery, Building.class);

        Gson gson = new Gson();
        Building tempBuilding = gson.fromJson(buildingJson, Building.class);
        if (dbBuilding != null) {
            dbBuilding.copyInfo(tempBuilding);
            mongoTemplate.save(dbBuilding);
            return true;
        }
        return false;
    }

}
