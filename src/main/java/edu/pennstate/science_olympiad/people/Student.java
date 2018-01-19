package edu.pennstate.science_olympiad.people;

import edu.pennstate.science_olympiad.School;
import edu.pennstate.science_olympiad.Team;
import edu.pennstate.science_olympiad.TeamRoleEnum;

public class Student extends AUser {
    private School school;
    private Team team;
    private TeamRoleEnum teamRoleEnum;

    public Student() {
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public TeamRoleEnum getTeamRoleEnum() {
        return teamRoleEnum;
    }

    public void setTeamRoleEnum(TeamRoleEnum teamRoleEnum) {
        this.teamRoleEnum = teamRoleEnum;
    }
}
