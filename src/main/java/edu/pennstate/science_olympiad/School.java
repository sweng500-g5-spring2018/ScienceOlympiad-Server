package edu.pennstate.science_olympiad;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This is just the contact information for the school that the  {@link edu.pennstate.science_olympiad.people.Student}s
 * and  {@link edu.pennstate.science_olympiad.people.Coach}s are from. This is to keep track for our records for later.
 */
@Document
public class School {

    @Id
    public String id;
    private String schoolName;
    private String schoolContactName;
    private String schoolContactPhone;

    public School(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getId() { return this.id; }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolContactName() {
        return schoolContactName;
    }

    public void setSchoolContactName(String schoolContactName) {
        this.schoolContactName = schoolContactName;
    }

    public String getSchoolContactPhone() {
        return schoolContactPhone;
    }

    public void setSchoolContactPhone(String schoolContactPhone) {
        this.schoolContactPhone = schoolContactPhone;
    }

    public void copyInfo(School school) {
        this.schoolName = school.getSchoolName();
        this.schoolContactName = school.getSchoolContactName();
        this.schoolContactPhone = school.getSchoolContactPhone();
    }

}
