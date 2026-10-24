package dataaccess;

import model.AuthData;

import java.util.ArrayList;


public class MemoryAuthDAO implements AuthDAO {
    private ArrayList<AuthData> authDataList;

    public  MemoryAuthDAO() {
        this.authDataList = new ArrayList<>();
    }

    @Override
    public void clear() throws DataAccessException {
        authDataList.clear();
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData auth : authDataList) {
            if (authToken.equals(auth.authToken())) {
                return auth;
            }
        }
        return null;
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        authDataList.add(auth);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        AuthData auth = getAuth(authToken);

        if (auth != null) {
            authDataList.remove(auth);
        }
    }

}