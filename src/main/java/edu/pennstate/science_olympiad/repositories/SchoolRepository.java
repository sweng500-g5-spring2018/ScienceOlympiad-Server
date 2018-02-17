package edu.pennstate.science_olympiad.repositories;

import edu.pennstate.science_olympiad.School;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("schoolRepository")
public class SchoolRepository {
    Log logger = LogFactory.getLog(getClass());

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * Gets all of the schools from the database
     * @return all of the schools
     */
    public List<School> getAllSchools() {
        return mongoTemplate.findAll(School.class);
    }

    /**
     * Retrieves a school from the database
     * @param schoolID The school to retrieve
     * @return the school object if found; or null
     */
    public School getSchool(String schoolID) {
        Query singleQuery = new Query();
        singleQuery.addCriteria(Criteria.where("_id").is(schoolID));
        School dbSchool = mongoTemplate.findOne(singleQuery, School.class);

        if (dbSchool != null) {
            return dbSchool;
        }
        return null;
    }

    /**
     * Adds a new school to the database
     * @param school The school to add
     * @return whether the school was added or not
     */
    public boolean addNewSchool(School school) {
        Query query = new Query();
        query.addCriteria(Criteria.where("schoolName").is(school.getSchoolName()));
        boolean exists = mongoTemplate.exists(query, School.class);

        if (!exists) {
            logger.info("Adding a new school");
            mongoTemplate.insert(school);
            return true;
        }
        logger.info("School already exists");
        return false;
    }

    /**
     * Removes a school from the database
     * @param schoolId The school to remove
     * @return whether the school was removed or not
     */
    public boolean removeSchool (String schoolId) {
        Query singleQuery = new Query();
        singleQuery.addCriteria(Criteria.where("_id").is(schoolId));
        School dbSchool = mongoTemplate.findOne(singleQuery, School.class);

        if (dbSchool != null) {
            mongoTemplate.remove(dbSchool);
            return true;
        }
        return false;
    }

}
