package ui;

import client.ServerException;
import client.ServerFacade;
import model.GameData;
import service.AuthResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class Repl {
    private enum State{
        PRELOGIN,
        POSTLOGIN
    }

    private final ServerFacade server;
    private final Scanner scanner;

    private State state = State.PRELOGIN;
    private boolean running;

    private String authToken;
    private String username;
    private List<GameData> gameList = new ArrayList<>();

    public Repl () {
        this.server = new ServerFacade(8080);
        this.scanner = new Scanner(System.in);
        this.running = true;
    }

    public void run() {
        while (running) {
            System.out.print("\n>>> ");

            String command = scanner.nextLine().trim().toLowerCase();

            if (command.isEmpty()) {
                continue;
            }

            try {
                if (state == State.PRELOGIN) {
                    switch (command) {
                        case "help" -> preloginHelp();
                        case "login" -> login();
                        case "register" -> register();
                        case "quit" -> running = false;
                        default -> System.out.println("Invalid. Type \"help\" for available commands!");
                    }
                } else if (state == State.POSTLOGIN) {
                    switch (command) {
                        case "help" -> postloginHelp();
                        case "logout" -> logout();
                        case "create" -> create();
                        case "list" -> list();
                        case "play" -> play();
                        case "observe" -> observe();
                        case "quit" -> running = false;
                        default -> System.out.println("Invalid. Type \"help\" for available commands!");
                    }
                }
            } catch (Exception e) {
                System.out.println("Something went wrong :(");
            }
        }
        System.out.println("Goodbye!");
    }

    private void preloginHelp() {
        System.out.println("""
                help        - Show available commands
                login       - Log in to existing account
                register    - Create new account
                quit        - Exit
                """);
    }

    private void login() {
        try {
            System.out.println("Username: ");
            String user = scanner.nextLine();

            System.out.println("Password: ");
            String password = scanner.nextLine();

            AuthResult auth = server.login(user, password);
            authToken = auth.authToken();
            username = auth.username();

            state = State.POSTLOGIN;
            System.out.println("Logged in as " + username + "!");
        } catch (Exception e) {
            System.out.println("Invalid username and password");
        }
    }

    private void register() {
        try {
            System.out.println("Username: ");
            String user = scanner.nextLine();

            System.out.println("Password: ");
            String password = scanner.nextLine();

            System.out.println("Email: ");
            String email = scanner.nextLine();

            AuthResult auth = server.register(user, password, email);
            authToken = auth.authToken();
            username = auth.username();

            state = State.POSTLOGIN;
            System.out.println("Registered as " + username + "!");
        } catch (ServerException e) {
            if (e.getMessage().contains("Username taken")) {
                System.out.println("Username already exists");
            } else {
                System.out.println("Registration failed");
            }
        }
    }

    private void postloginHelp() {
        System.out.println("""
                help        - Show available commands
                logout      - Log out
                create      - Create new game
                list        - List existing games
                play        - Join a game
                observe     - Observe a game
                quit        - Exit
                """);
    }

    private void logout() {
        try {
            server.logout(authToken);

            authToken = null;
            username = null;
            gameList.clear();

            state = State.PRELOGIN;
            System.out.println("Logged out");
        } catch (Exception e) {
            System.out.println("Logout failed");
        }
    }

    private void create() {
        System.out.println("Game Name: ");
        String gameName = scanner.nextLine().trim();

        if (gameName .isEmpty()) {
            System.out.println("Game name cannot be blank");
            return;
        }

        try {
            server.createGame(authToken, gameName);
            System.out.println("Game created");

        } catch (Exception e) {
            System.out.println("Unable to create game");
        }
    }

    private void list() {
        gameList.clear();

        try {
            Collection<GameData> games = server.listGames(authToken);
            gameList = new ArrayList<>(games);

            if (gameList.isEmpty()) {
                System.out.println("No games currently");
                return;
            }

            StringBuilder output = new StringBuilder("Games: \n");

            for (int i = 0; i < gameList.size(); i++) {
                GameData game = gameList.get(i);

                String white = game.whiteUsername() == null ? "None" : game.whiteUsername();
                String black = game.blackUsername() == null ? "None" : game.blackUsername();

                output.append(String.format(
                        "%d. %s | White: %s | Black: %s%n", i + 1, game.gameName(), white, black ));
            }
            System.out.println(output.toString().stripTrailing());

        } catch (Exception e) {
            System.out.println("Unable to retrieve games");
        }
    }

    private void play() {
        list();
        if (gameList.isEmpty()) {
            return;
        }

        try {
            System.out.println("Game Number: ");
            int number = Integer.parseInt(scanner.nextLine());

            if (number < 1 || number > gameList.size()) {
                System.out.println("Invalid game number");
                return;
            }

            System.out.println("WHITE or BLACK?: ");
            String color = scanner.nextLine().trim().toUpperCase();

            while (!color.equals("WHITE") && !color.equals("BLACK")) {
                System.out.println("Invalid color. WHITE or BLACK?: ");
                color = scanner.nextLine().trim().toUpperCase();
            }

            GameData game = gameList.get(number -1 );

            if (color.equals("WHITE") && game.whiteUsername() != null) {
                System.out.println("White already taken");
                return;
            } else if (color.equals("BLACK") && game.blackUsername() != null) {
                System.out.println("Black already taken");
                return;
            }

            server.joinGame(authToken, color, game.gameID());
            System.out.println("Game joined");

        } catch (NumberFormatException e) {
            System.out.println("Invalid game number");
        } catch (Exception e) {
            System.out.println("Unable to join game");
        }
    }

    private void observe() {
        list();
        if (gameList.isEmpty()) {
            return;
        }

        try {
            System.out.println("Game Number: ");
            int number = Integer.parseInt(scanner.nextLine());

            if (number < 1 || number > gameList.size()) {
                System.out.println("Invalid game number");
                return;
            }

            GameData game = gameList.get(number -1 );

            server.observeGame(authToken, game.gameID());
            System.out.println("Game joined as an observer");

        } catch (NumberFormatException e) {
            System.out.println("Invalid game number");
        } catch (Exception e) {
            System.out.println("Unable to observe game");
        }
    }
}
