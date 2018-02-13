package edu.pennstate.science_olympiad.people;

public class UserFactory {

    private static final UserFactory INSTANCE = new UserFactory();

    private UserFactory() {
    }

    public static UserFactory getInstance() {
        return INSTANCE;
    }

    public AUser createUser(String userType) {
        AUser user = null;
        if (userType.equalsIgnoreCase(IUserTypes.ADMIN))
            user = new Admin();
        else if (userType.equalsIgnoreCase(IUserTypes.COACH))
            user = new Coach();
        else if (userType.equalsIgnoreCase(IUserTypes.JUDGE))
            user = new Judge();
        else if (userType.equalsIgnoreCase(IUserTypes.STUDENT))
            user = new Student();

        return user;
    }

    public String getUserType(AUser user) {
        System.out.println(user);

        if(user instanceof Admin) {
            return IUserTypes.ADMIN;
        } else if (user instanceof Coach) {
            return IUserTypes.COACH;
        } else if (user instanceof Judge) {
            return IUserTypes.JUDGE;
        } else if (user instanceof Student) {
            return IUserTypes.STUDENT;
        } else {
            return null;
        }
    }
}
