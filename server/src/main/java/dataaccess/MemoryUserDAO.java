package dataaccess;

import model.UserData;

import java.util.ArrayList;


public class MemoryUserDAO implements UserDAO {
    private ArrayList<UserData> userDataList;

    public  MemoryUserDAO() {
        this.userDataList = new ArrayList<>();
    }

    @Override
    public void clear() throws DataAccessException {
        userDataList.clear();
    }

    @Override
    public UserData getUser(UserData user) throws DataAccessException {
        for (UserData person : userDataList) {
            if (user.username().equals(person.username())) {
                return person;
            }
        }
        return null;
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (getUser(user)!= null) {
            throw new DataAccessException("Username taken");
        }
        userDataList.add(user);
    }

}